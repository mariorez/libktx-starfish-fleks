package mariorez.starfishcollector

import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import mariorez.starfishcollector.screen.GameScreen

class GameBoot : KtxGame<KtxScreen>() {

    companion object {
        const val WINDOW_WIDTH = 800
        const val WINDOW_HEIGHT = 600
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        KtxAsync.initiate()

        val assets = AssetStorage().apply {
            loadSync<Texture>("turtle.png").setFilter(Linear, Linear)
        }

        addScreen(GameScreen(assets))
        setScreen<GameScreen>()
    }
}
