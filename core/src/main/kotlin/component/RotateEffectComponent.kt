package component

data class RotateEffectComponent(
    var direction: Direction = Direction.CLOCKWISE,
    var speed: Float = 0f
) {
    enum class Direction { CLOCKWISE, COUNTERCLOCKWISE }
}
