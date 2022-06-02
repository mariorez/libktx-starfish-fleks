package component

data class InputComponent(
    var up: Boolean = false,
    var down: Boolean = false,
    var left: Boolean = false,
    var right: Boolean = false
) {
    val isMoving: Boolean get() = up || down || left || right
}