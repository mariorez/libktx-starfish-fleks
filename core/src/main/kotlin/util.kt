import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

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

fun generateFont(fontFile: String = "open-sans.ttf"): BitmapFont {
    return FreeTypeFontGenerator(Gdx.files.internal(fontFile))
        .generateFont(
            FreeTypeFontGenerator.FreeTypeFontParameter().apply {
                size = 34
                color = Color.WHITE
                borderColor = Color.BLACK
                borderWidth = 2f
                borderStraight = true
                minFilter = Texture.TextureFilter.Linear
                magFilter = Texture.TextureFilter.Linear
            })
}

fun generateButton(texture: Texture): Button {
    return Button(ButtonStyle().apply {
        up = TextureRegionDrawable(texture)
    })
}

fun generateTextButton(text: String, textColor: Color, button: Texture, padding: Int): TextButton {
    return TextButton(text, TextButtonStyle().apply {
        up = NinePatchDrawable(NinePatch(button, padding, padding, padding, padding))
        font = generateFont()
        fontColor = textColor
    })
}
