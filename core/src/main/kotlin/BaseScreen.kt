import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.app.Platform
import ktx.assets.disposeSafely

abstract class BaseScreen : KtxScreen {

    private val actionMap = mutableMapOf<Int, Action.Name>()
    protected val uiStage = Stage()

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
    }
}
