import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

data class GameSizes(
    var windowWidth: Int,
    var windowHeight: Int,
    var worldWidth: Int = windowWidth,
    var worldHeight: Int = windowHeight
) {
    fun windowWidthF(): Float = windowWidth.toFloat()
    fun windowHeightF(): Float = windowHeight.toFloat()
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
