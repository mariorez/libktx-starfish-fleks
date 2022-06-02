package system

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.AnimationComponent
import component.InputComponent
import component.PlayerComponent
import component.TransformComponent

@AllOf([PlayerComponent::class])
class InputSystem(
    private val input: ComponentMapper<InputComponent>,
    private val transform: ComponentMapper<TransformComponent>,
    private val animation: ComponentMapper<AnimationComponent>
) : IteratingSystem() {

    private val speedUp = Vector2()

    override fun onTickEntity(entity: Entity) {
        input[entity].also { playerInput ->
            if (playerInput.isMoving) {
                transform[entity].apply {
                    speedUp.set(acceleration, 0f).also { speed ->
                        if (playerInput.right) accelerator.add(speed.setAngleDeg(0f))
                        if (playerInput.up) accelerator.add(speed.setAngleDeg(90f))
                        if (playerInput.left) accelerator.add(speed.setAngleDeg(180f))
                        if (playerInput.down) accelerator.add(speed.setAngleDeg(270f))
                    }
                }
                animation[entity].playMode = LOOP
            } else {
                animation[entity].playMode = NORMAL
            }
        }
    }
}
