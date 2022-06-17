package system

import com.badlogic.gdx.Gdx
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.FadeEffectComponent
import component.FadeEffectComponent.Mode.IN
import component.FadeEffectComponent.Mode.OUT
import component.RenderComponent

@AllOf([RenderComponent::class, FadeEffectComponent::class])
class FadeEffectSystem(
    private val render: ComponentMapper<RenderComponent>,
    private val fade: ComponentMapper<FadeEffectComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {

        val sprite = render[entity].sprite

        fade[entity].apply {

            val fadeAmount = (1f / Gdx.graphics.framesPerSecond) / duration

            when (mode) {
                IN -> {
                    alpha += fadeAmount
                    if (alpha >= 1f) cleanUp(entity, removeEntityOnEnd)
                }
                OUT -> {
                    alpha -= fadeAmount
                    if (alpha <= 0f) cleanUp(entity, removeEntityOnEnd)
                }
            }

            sprite.setAlpha(alpha)
        }
    }

    private fun cleanUp(entity: Entity, remove: Boolean) {
        configureEntity(entity) {
            fade.remove(entity)
        }
        if (remove) world.remove(entity)
    }
}
