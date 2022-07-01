import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import kotlin.math.max

data class GameSizes(
    var windowWidth: Int,
    var windowHeight: Int,
    var worldWidth: Int = windowWidth,
    var worldHeight: Int = windowHeight
) {
    fun windowWidthF(): Float = windowWidth.toFloat()
    fun windowHeightF(): Float = windowHeight.toFloat()
    fun unitsPerPixel(): Float = max(windowWidthF() / Gdx.graphics.width, windowHeightF() / Gdx.graphics.height)
}

fun generateButton(texture: Texture): Button {
    return Button(ButtonStyle().apply {
        up = TextureRegionDrawable(texture)
    })
}

fun generateTextButton(text: String, textColor: Color, button: Texture, padding: Int): TextButton {
    return TextButton(text, TextButtonStyle().apply {
        up = NinePatchDrawable(NinePatch(button, padding, padding, padding, padding))
        font = GameBoot.assets.get<BitmapFont>("open-sans.ttf")
        fontColor = textColor
    })
}
