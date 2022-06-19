import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.TextureAtlas
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

class GameBoot : KtxGame<KtxScreen>() {

    companion object {
        var gameSizes = GameSizes(
            windowWidth = 960,
            windowHeight = 540
        )
    }

    private val assets = AssetStorage()

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
            loadSync<TiledMap>("map.tmx")
            loadSync<TextureAtlas>("starfish-collector.atlas").apply {
                textures.forEach { it.setFilter(Linear, Linear) }
            }
            loadSync<Texture>("starfish.png").setFilter(Linear, Linear)
            loadSync<Texture>("rock.png").setFilter(Linear, Linear)
            loadSync<Texture>("sign.png").setFilter(Linear, Linear)
            loadSync<Texture>("undo.png").setFilter(Linear, Linear)
            if (Platform.isMobile) {
                loadSync<Texture>("touchpad-bg.png").setFilter(Linear, Linear)
                loadSync<Texture>("touchpad-knob.png").setFilter(Linear, Linear)
            }
        }

        addScreen(GameScreen(this, assets))
        setScreen<GameScreen>()
    }

    override fun dispose() {
        super.dispose()
        assets.disposeSafely()
    }
}
