import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.loaders.SoundLoader
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import ktx.app.KtxGame
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.app.Platform
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import screen.GameScreen
import screen.MenuScreen

class GameBoot : KtxGame<KtxScreen>() {

    companion object {
        val assets = AssetStorage()
        var gameSizes = GameSizes(
            windowWidth = 960,
            windowHeight = 540
        )
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG

        Gdx.input.inputProcessor = if (Platform.isMobile) InputMultiplexer()
        else InputMultiplexer(object : KtxInputAdapter {
            override fun keyDown(keycode: Int): Boolean {
                (currentScreen as BaseScreen).apply {
                    getActionMap()[keycode]?.let { doAction(Action(it, Action.Type.START)) }
                }
                return super.keyDown(keycode)
            }

            override fun keyUp(keycode: Int): Boolean {
                (currentScreen as BaseScreen).apply {
                    getActionMap()[keycode]?.let { doAction(Action(it, Action.Type.END)) }
                }
                return super.keyUp(keycode)
            }
        })

        KtxAsync.initiate()

        assets.apply {
            setLoader<TiledMap> { TmxMapLoader(fileResolver) }
            setLoader<Sound> { SoundLoader(fileResolver) }
            setLoader<FreeTypeFontGenerator> { FreeTypeFontGeneratorLoader(fileResolver) }
            setLoader<BitmapFont>(".ttf") { FreetypeFontLoader(fileResolver) }

            loadSync<BitmapFont>("open-sans.ttf", FreeTypeFontLoaderParameter().apply {
                fontFileName = "open-sans.ttf"
                fontParameters.apply {
                    size = 32
                    color = Color.WHITE
                    borderColor = Color.BLACK
                    borderWidth = 2f
                    borderStraight = true
                    minFilter = Texture.TextureFilter.Linear
                    magFilter = Texture.TextureFilter.Linear
                }
            })
            loadSync<TextureAtlas>("starfish-collector.atlas").apply {
                textures.forEach { it.setFilter(Linear, Linear) }
            }
            loadSync<TiledMap>("map.tmx")
            loadSync<Sound>("water-drop.ogg")
            loadSync<Texture>("water.jpg").setFilter(Linear, Linear)
            loadSync<Texture>("button.png").setFilter(Linear, Linear)
            loadSync<Texture>("game-title.png").setFilter(Linear, Linear)
            loadSync<Texture>("starfish.png").setFilter(Linear, Linear)
            loadSync<Texture>("rock.png").setFilter(Linear, Linear)
            loadSync<Texture>("sign.png").setFilter(Linear, Linear)
            loadSync<Texture>("undo.png").setFilter(Linear, Linear)

            if (Platform.isMobile) {
                loadSync<Texture>("touchpad-bg.png").setFilter(Linear, Linear)
                loadSync<Texture>("touchpad-knob.png").setFilter(Linear, Linear)
            }
        }

        addScreen(MenuScreen(this))
        addScreen(GameScreen())
        setScreen<MenuScreen>()
    }

    override fun dispose() {
        super.dispose()
        assets.disposeSafely()
    }
}
