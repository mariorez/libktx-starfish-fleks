package system

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.AnimationComponent
import component.RenderComponent
import ktx.collections.gdxArrayOf

@AllOf([AnimationComponent::class])
class AnimationSystem(
    private val animation: ComponentMapper<AnimationComponent>,
    private val render: ComponentMapper<RenderComponent>
) : IteratingSystem() {

    private val animationCache = mutableMapOf<TextureRegion, Animation<TextureRegion>>()

    override fun onTickEntity(entity: Entity) {
        val animationData = animation[entity].apply {
            stateTime += deltaTime
        }

        val animationFrame = getAnimation(animationData).getKeyFrame(animationData.stateTime)

        render[entity].sprite.apply {
            setRegion(animationFrame)
            setSize(animationFrame.regionWidth.toFloat(), animationFrame.regionHeight.toFloat())
        }
    }

    private fun getAnimation(animationData: AnimationComponent): Animation<TextureRegion> {

        return animationCache.getOrPut(animationData.region) {
            val regions = animationData.region.split(
                (animationData.region.regionWidth / animationData.frames),
                animationData.region.regionHeight
            )

            val textureArray = gdxArrayOf<TextureRegion>().apply {
                (0 until animationData.frames).forEach { col ->
                    add(TextureRegion(regions[0][col]))
                }
            }

            Animation(animationData.frameDuration, textureArray).apply {
                playMode = animationData.playMode
            }
        }.apply {
            playMode = animationData.playMode
        }
    }
}
