package system

import GameBoot.Companion.assets
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector
import com.badlogic.gdx.math.Polygon
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.AnimationComponent
import component.FadeEffectComponent
import component.RenderComponent
import component.RockComponent
import component.SignComponent
import component.SolidComponent
import component.StarfishComponent
import component.TransformComponent
import listener.ScoreManager
import kotlin.properties.Delegates

@AllOf([SolidComponent::class])
class CollisionSystem(
    private val score: ScoreManager,
    private val transform: ComponentMapper<TransformComponent>,
    private val solid: ComponentMapper<SolidComponent>,
    private val render: ComponentMapper<RenderComponent>,
    private val fade: ComponentMapper<FadeEffectComponent>,
    private val rock: ComponentMapper<RockComponent>,
    private val sign: ComponentMapper<SignComponent>,
    private val starfish: ComponentMapper<StarfishComponent>
) : IteratingSystem() {

    var player: Entity by Delegates.notNull()

    override fun onTickEntity(entity: Entity) {
        val playerBox = render[player].getPolygon(8)
        val objectBox = if (rock.contains(entity) || starfish.contains(entity)) render[entity].getPolygon(8)
        else render[entity].getPolygon()

        val mtv = MinimumTranslationVector()
        if (!overlaps(playerBox, objectBox, mtv)) return

        if (rock.contains(entity) || sign.contains(entity)) {
            transform[player].apply {
                position.x += mtv.normal.x * mtv.depth
                position.y += mtv.normal.y * mtv.depth
            }
        }

        if (starfish.contains(entity)) {
            score.total--
            assets.get<Sound>("water-drop.ogg").play()
            configureEntity(entity) {
                solid.remove(entity)
                fade.add(entity).apply { removeEntityOnEnd = true }
            }
            world.entity {
                add<RenderComponent>()
                add<FadeEffectComponent> { removeEntityOnEnd = true }
                add<TransformComponent> {
                    position.x = objectBox.x - 15
                    position.y = objectBox.y - 7
                }
                add<AnimationComponent> {
                    region = assets
                        .get<TextureAtlas>("starfish-collector.atlas")
                        .findRegion("whirlpool")
                    frames = 10
                    frameDuration = 0.1f
                }
            }
        }
    }

    private fun overlaps(playerBox: Polygon, otherBox: Polygon, mtv: MinimumTranslationVector): Boolean {
        // initial test to improve performance
        if (playerBox.boundingRectangle.overlaps(otherBox.boundingRectangle)) {
            return Intersector.overlapConvexPolygons(playerBox, otherBox, mtv)
        }
        return false
    }
}