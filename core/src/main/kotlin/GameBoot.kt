import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.Platform
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import screen.GameScreen

class GameBoot : KtxGame<KtxScreen>() {

    companion object {
        const val WINDOW_WIDTH = 864
        const val WINDOW_HEIGHT = 486
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG

        KtxAsync.initiate()

        val assets = AssetStorage().apply {
            setLoader<TiledMap> { TmxMapLoader(fileResolver) }
            loadSync<TiledMap>("map.tmx")
            loadSync<TextureAtlas>("starfish-collector.atlas").apply {
                textures.forEach { it.setFilter(Linear, Linear) }
            }
            loadSync<Texture>("starfish.png").setFilter(Linear, Linear)
            loadSync<Texture>("rock.png").setFilter(Linear, Linear)
            loadSync<Texture>("sign.png").setFilter(Linear, Linear)
            if (Platform.isMobile) {
                loadSync<Texture>("touchpad-bg.png").setFilter(Linear, Linear)
                loadSync<Texture>("touchpad-knob.png").setFilter(Linear, Linear)
            }
        }

        addScreen(GameScreen(assets))
        setScreen<GameScreen>()
    }
}
