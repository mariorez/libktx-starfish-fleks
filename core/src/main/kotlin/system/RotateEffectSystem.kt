package system

import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.RotateEffectComponent
import component.RotateEffectComponent.Direction.CLOCKWISE
import component.RotateEffectComponent.Direction.COUNTERCLOCKWISE
import component.TransformComponent

@AllOf([RotateEffectComponent::class])
class RotateEffectSystem(
    private val transform: ComponentMapper<TransformComponent>,
    private val rotate: ComponentMapper<RotateEffectComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        transform[entity].apply {
            rotate[entity].also {
                when (it.direction) {
                    CLOCKWISE -> {
                        if (rotation <= 0) rotation = 360f
                        else rotation -= it.speed
                    }
                    COUNTERCLOCKWISE -> {
                        if (rotation > 360) rotation -= 360
                        else rotation += it.speed
                    }
                }
            }
        }
    }
}
