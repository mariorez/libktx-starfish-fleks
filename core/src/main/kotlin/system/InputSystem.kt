package system

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import component.AnimationComponent
import component.InputComponent
import component.TransformComponent
import kotlin.properties.Delegates

class InputSystem(
    private val input: ComponentMapper<InputComponent>,
    private val transform: ComponentMapper<TransformComponent>,
    private val animation: ComponentMapper<AnimationComponent>
) : IntervalSystem() {

    var player: Entity by Delegates.notNull()
    private val speedUp = Vector2()

    override fun onTick() {
        input[player].also { playerInput ->
            if (playerInput.isMoving) {
                transform[player].apply {
                    speedUp.set(acceleration, 0f).also { speed ->
                        if (playerInput.right) accelerator.add(speed.setAngleDeg(0f))
                        if (playerInput.up) accelerator.add(speed.setAngleDeg(90f))
                        if (playerInput.left) accelerator.add(speed.setAngleDeg(180f))
                        if (playerInput.down) accelerator.add(speed.setAngleDeg(270f))
                    }
                }
                animation[player].playMode = LOOP
            } else {
                animation[player].playMode = NORMAL
            }
        }
    }
}
