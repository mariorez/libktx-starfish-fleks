package system

import GameSizes
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import component.RenderComponent
import component.TransformComponent
import kotlin.properties.Delegates

class MovementSystem(
    private val gameSizes: GameSizes,
    private val transform: ComponentMapper<TransformComponent>,
    private val render: ComponentMapper<RenderComponent>
) : IntervalSystem() {

    var player: Entity by Delegates.notNull()

    override fun onTick() {
        transform[player].apply {
            // apply acceleration
            velocity.add(
                accelerator.x * deltaTime,
                accelerator.y * deltaTime
            )

            var speed = velocity.len()

            // decrease speed (decelerate) when not accelerating
            if (accelerator.len() == 0f) {
                speed -= deceleration * deltaTime
            }

            // keep speed within set bounds
            speed = MathUtils.clamp(speed, 0f, maxSpeed)

            // update velocity
            if (velocity.len() == 0f) {
                velocity.set(speed, 0f)
            } else {
                velocity.setLength(speed)
            }

            // move by
            if (velocity.x != 0f || velocity.y != 0f) {
                position.add(velocity.x * deltaTime, velocity.y * deltaTime)
                boundToWorld(position, render[player].sprite.width, render[player].sprite.height)
            }

            // set rotation when moving
            if (velocity.len() > 0) {
                rotation = velocity.angleDeg()
            }

            // reset acceleration
            accelerator.set(0f, 0f)
        }
    }

    private fun boundToWorld(position: Vector2, entityWidth: Float, entityHeight: Float) {
        if (position.x < 0f) position.x = 0f
        if (position.x + entityWidth > gameSizes.worldWidth) position.x = gameSizes.worldWidth - entityWidth
        if (position.y < 0f) position.y = 0f
        if (position.y + entityHeight > gameSizes.worldHeight) position.y = gameSizes.worldHeight - entityHeight
    }
}
