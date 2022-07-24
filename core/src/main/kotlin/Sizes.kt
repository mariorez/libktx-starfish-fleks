import com.badlogic.gdx.Gdx
import kotlin.math.max

data class Sizes(
    var windowWidth: Int,
    var windowHeight: Int,
    var worldWidth: Int = windowWidth,
    var worldHeight: Int = windowHeight
) {
    fun windowWidthF(): Float = windowWidth.toFloat()
    fun windowHeightF(): Float = windowHeight.toFloat()
    fun worldWidthF(): Float = worldWidth.toFloat()
    fun worldHeightF(): Float = worldHeight.toFloat()
    fun unitsPerPixel(): Float = max(windowWidthF() / Gdx.graphics.width, windowHeightF() / Gdx.graphics.height)
}
