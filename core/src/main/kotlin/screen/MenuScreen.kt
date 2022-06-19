package screen

import Action
import BaseScreen
import GameBoot
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color.GRAY
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import generateTextButton
import ktx.actors.onTouchDown
import ktx.assets.async.AssetStorage

class MenuScreen(
    gameBoot: GameBoot,
    private val assets: AssetStorage,
) : BaseScreen(gameBoot) {

    init {
        hudStage.apply {
            addActor(Image(assets.get<Texture>("water.jpg")))
            addActor(Table().apply {
                setFillParent(true)
                add(Image(assets.get<Texture>("game-title.png"))).colspan(2).padBottom(50f)
                row()
                add(generateTextButton("Start", GRAY, assets["button.png"], 24).apply {
                    onTouchDown { gameBoot.setScreen<GameScreen>() }
                })
                add(generateTextButton("Quit", GRAY, assets["button.png"], 24).apply {
                    onTouchDown { Gdx.app.exit() }
                })
            })
        }
    }

    override fun render(delta: Float) {
        hudStage.draw()
    }

    override fun doAction(action: Action) {
        TODO("Not yet implemented")
    }
}
