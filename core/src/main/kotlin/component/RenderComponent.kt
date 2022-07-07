package component

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon

data class RenderComponent(
    var sprite: Sprite = Sprite()
) {
    private var polygon: Polygon? = null

    fun getPolygon(): Polygon {
        polygon?.let {
            return it.apply {
                setPosition(sprite.x, sprite.y)
                setOrigin(sprite.originX, sprite.originY)
                rotation = sprite.rotation
                setScale(sprite.scaleX, sprite.scaleY)
            }
        }
        polygon = Polygon().apply {
            vertices = floatArrayOf(
                0f, 0f,
                sprite.width, 0f,
                sprite.width, sprite.height,
                0f, sprite.height
            )
        }
        return getPolygon()
    }

    fun getPolygon(sides: Int): Polygon {
        polygon?.let {
            return getPolygon()
        }
        val vertices = FloatArray(2 * sides)
        for (i in 0 until sides) {
            val angle: Float = i * 6.28f / sides
            // x-coordinate
            vertices[2 * i] = sprite.width / 2 * MathUtils.cos(angle) + sprite.width / 2
            // y-coordinate
            vertices[2 * i + 1] = sprite.height / 2 * MathUtils.sin(angle) + sprite.height / 2
        }
        polygon = Polygon(vertices)
        return getPolygon()
    }
}
