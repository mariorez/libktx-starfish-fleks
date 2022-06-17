package component

data class FadeEffectComponent(
    var mode: Mode = Mode.OUT,
    var alpha: Float = 1f,
    var duration: Int = 1,
    var removeEntityOnEnd: Boolean = false
) {
    enum class Mode { IN, OUT }
}
