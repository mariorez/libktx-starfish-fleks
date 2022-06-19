import GameBoot.Companion.gameSizes
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.app.Platform
import ktx.assets.disposeSafely

abstract class BaseScreen(
    protected val gameBoot: GameBoot
) : KtxScreen {

    private val actionMap = mutableMapOf<Int, Action.Name>()
    protected val batch = SpriteBatch()
    protected val camera = OrthographicCamera().apply {
        setToOrtho(false, gameSizes.windowWidthFloat(), gameSizes.windowHeightFloat())
    }
    protected val uiStage = Stage(FitViewport(gameSizes.windowWidthFloat(), gameSizes.windowHeightFloat()))

    init {
        Gdx.input.inputProcessor = if (Platform.isMobile) InputMultiplexer().apply { addProcessor(uiStage) }
        else InputMultiplexer(object : KtxInputAdapter {
            override fun keyDown(keycode: Int): Boolean {
                this.apply {
                    getActionMap()[keycode]?.let { doAction(Action(it, Action.Type.START)) }
                }
                return super.keyDown(keycode)
            }

            override fun keyUp(keycode: Int): Boolean {
                this.apply {
                    getActionMap()[keycode]?.let { doAction(Action(it, Action.Type.END)) }
                }
                return super.keyUp(keycode)
            }
        })
    }

    fun registerAction(inputKey: Int, actionName: Action.Name) {
        actionMap[inputKey] = actionName
    }

    fun getActionMap(): MutableMap<Int, Action.Name> = actionMap

    abstract fun doAction(action: Action)

    override fun dispose() {
        uiStage.disposeSafely()
        batch.disposeSafely()
    }
}
