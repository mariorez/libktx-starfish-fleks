package screen

import Action
import BaseScreen
import GameBoot.Companion.WINDOW_HEIGHT
import GameBoot.Companion.WINDOW_WIDTH
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import component.InputComponent
import component.PlayerComponent
import component.RenderComponent
import component.TransformComponent
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import system.InputSystem
import system.MovementSystem
import system.RenderSystem
import kotlin.properties.Delegates

class GameScreen(
    private val assets: AssetStorage
) : BaseScreen() {
    private val batch = SpriteBatch()
    private val mainCamera = OrthographicCamera(
        WINDOW_WIDTH.toFloat(),
        WINDOW_HEIGHT.toFloat()
    ).apply { setToOrtho(false) }
    private val tiledMap = assets.get<TiledMap>("map.tmx")
    private val mapRenderer = OrthoCachedTiledMapRenderer(tiledMap).apply { setBlending(true) }
    private val world = World {
        inject(batch)
        inject(mainCamera)
        inject(mapRenderer)
        system<InputSystem>()
        system<MovementSystem>()
        system<RenderSystem>()
    }
    private var turtle: Entity by Delegates.notNull()

    init {
        registerAction(Input.Keys.W, Action.Name.UP)
        registerAction(Input.Keys.S, Action.Name.DOWN)
        registerAction(Input.Keys.A, Action.Name.LEFT)
        registerAction(Input.Keys.D, Action.Name.RIGHT)

        spawnPlayer()
    }

    private fun spawnPlayer() {
        turtle = world.entity {
            add<PlayerComponent>()
            add<InputComponent>()
            add<TransformComponent> {
                position.set(100f, 100f)
                acceleration = 400f
                deceleration = 250f
                maxSpeed = 150f
            }
            add<RenderComponent> { sprite = Sprite(assets.get<Texture>("turtle.png")) }
        }
    }

    override fun doAction(action: Action) {
        val input = world.mapper<InputComponent>()[turtle]
        val isStarting = action.type == Action.Type.START
        when (action.name) {
            Action.Name.UP -> input.up = isStarting
            Action.Name.DOWN -> input.down = isStarting
            Action.Name.LEFT -> input.left = isStarting
            Action.Name.RIGHT -> input.right = isStarting
        }
    }

    override fun render(delta: Float) {
        world.update(delta)
    }

    override fun dispose() {
        world.dispose()
        batch.disposeSafely()
    }
}
