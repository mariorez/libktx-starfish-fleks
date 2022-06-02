package mariorez.starfishcollector

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import mariorez.starfishcollector.screen.GameScreen

class GameBoot : KtxGame<KtxScreen>() {

    companion object {
        const val WINDOW_WIDTH = 800
        const val WINDOW_HEIGHT = 600
    }

    override fun create() {
        KtxAsync.initiate()
        addScreen(GameScreen())
        setScreen<GameScreen>()
    }
}
