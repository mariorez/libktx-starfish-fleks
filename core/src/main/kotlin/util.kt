import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon

data class GameSizes(
    var windowWidth: Int = 0,
    var windowHeight: Int = 0,
    var worldWidth: Int = 0,
    var worldHeight: Int = 0
) {
    fun windowWidthFloat(): Float = windowWidth.toFloat()
    fun windowHeightFloat(): Float = windowHeight.toFloat()
}

fun generatePolygon(sides: Int, width: Int, height: Int): Polygon =
    generatePolygon(sides, width.toFloat(), height.toFloat())

fun generatePolygon(sides: Int, width: Float, height: Float): Polygon {
    val vertices = FloatArray(2 * sides)
    for (i in 0 until sides) {
        val angle: Float = i * 6.28f / sides
        // x-coordinate
        vertices[2 * i] = width / 2 * MathUtils.cos(angle) + width / 2
        // y-coordinate
        vertices[2 * i + 1] = height / 2 * MathUtils.sin(angle) + height / 2
    }
    return Polygon(vertices)
}

fun generateRectangle(width: Int, height: Int): Polygon = generateRectangle(width.toFloat(), height.toFloat())
fun generateRectangle(width: Float, height: Float): Polygon {
    return Polygon(floatArrayOf(0f, 0f, width, 0f, width, height, 0f, height))
}
