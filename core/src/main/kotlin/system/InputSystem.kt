package system

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import component.AnimationComponent
import component.InputComponent
import component.TransformComponent
import ktx.app.Platform
import kotlin.properties.Delegates

class InputSystem(
    private val input: ComponentMapper<InputComponent>,
    private val transform: ComponentMapper<TransformComponent>,
    private val animation: ComponentMapper<AnimationComponent>
) : IntervalSystem() {

    var player: Entity by Delegates.notNull()
    var touchpad: Touchpad by Delegates.notNull()
    private val speedUp = Vector2()
    private val direction = Vector2()

    override fun onTick() {
        if (Platform.isMobile) {
            direction.set(touchpad.knobPercentX, touchpad.knobPercentY)
            if (direction.len() > 0) {
                transform[player].apply {
                    speedUp.set(acceleration, 0f).also { speed ->
                        accelerator.add(speed.setAngleDeg(direction.angleDeg()))
                    }
                }
                animation[player].playMode = LOOP
            } else {
                animation[player].playMode = NORMAL
            }
        } else {
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
}
