package component

import com.badlogic.gdx.math.Vector2

data class TransformComponent(
    var position: Vector2 = Vector2(),
    var zIndex: Float = 0f,
    var velocity: Vector2 = Vector2(),
    var accelerator: Vector2 = Vector2(),
    var acceleration: Float = 0f,
    var deceleration: Float = 0f,
    var maxSpeed: Float = 0f,
    var rotation: Float = 0f
)