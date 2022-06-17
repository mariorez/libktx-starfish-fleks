package system

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector
import com.badlogic.gdx.math.Polygon
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.NoneOf
import component.AnimationComponent
import component.BoundingBoxComponent
import component.FadeEffectComponent
import component.PlayerComponent
import component.RenderComponent
import component.RockComponent
import component.SignComponent
import component.StarfishComponent
import component.TransformComponent
import ktx.assets.async.AssetStorage
import kotlin.properties.Delegates

@AllOf([BoundingBoxComponent::class])
@NoneOf([PlayerComponent::class])
class CollisionSystem(
    private val assets: AssetStorage,
    private val transform: ComponentMapper<TransformComponent>,
    private val box: ComponentMapper<BoundingBoxComponent>,
    private val render: ComponentMapper<RenderComponent>,
    private val fade: ComponentMapper<FadeEffectComponent>,
    private val rock: ComponentMapper<RockComponent>,
    private val sign: ComponentMapper<SignComponent>,
    private val starfish: ComponentMapper<StarfishComponent>
) : IteratingSystem() {

    var player: Entity by Delegates.notNull()

    override fun onTickEntity(entity: Entity) {
        val playerSprite = render[player].sprite
        val playerBox = box[player].polygon.apply {
            setPosition(playerSprite.x, playerSprite.y)
            setOrigin(playerSprite.originX, playerSprite.originY)
            rotation = playerSprite.rotation
            setScale(playerSprite.scaleX, playerSprite.scaleY)
        }

        val currentSprite = render[entity].sprite
        val objectBox = box[entity].polygon

        val mtv = MinimumTranslationVector()

        if (!overlaps(playerBox, objectBox, mtv)) return

        if (rock.contains(entity) || sign.contains(entity)) {
            transform[player].apply {
                position.x += mtv.normal.x * mtv.depth
                position.y += mtv.normal.y * mtv.depth
            }
        }

        if (starfish.contains(entity)) {
            configureEntity(entity) {
                box.remove(entity)
                fade.add(entity).apply { removeEntityOnEnd = true }
            }
            world.entity {
                add<RenderComponent>()
                add<FadeEffectComponent> { removeEntityOnEnd = true }
                add<TransformComponent> {
                    position.x = currentSprite.x - 15
                    position.y = currentSprite.y - 7
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