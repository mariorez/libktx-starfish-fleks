package screen

import Action
import Action.Type.START
import BaseScreen
import GameBoot.Companion.WINDOW_HEIGHT
import GameBoot.Companion.WINDOW_WIDTH
import WorldSize
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import component.AnimationComponent
import component.BoundingBoxComponent
import component.InputComponent
import component.PlayerComponent
import component.RenderComponent
import component.RockComponent
import component.SignComponent
import component.StarfishComponent
import component.TransformComponent
import generatePolygon
import generateRectangle
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import ktx.tiled.forEachMapObject
import ktx.tiled.totalHeight
import ktx.tiled.totalWidth
import ktx.tiled.x
import ktx.tiled.y
import system.AnimationSystem
import system.CameraSystem
import system.CollisionSystem
import system.InputSystem
import system.MovementSystem
import system.RenderSystem
import kotlin.properties.Delegates

class GameScreen(
    private val assets: AssetStorage
) : BaseScreen() {
    private val batch = SpriteBatch()
    private val camera = OrthographicCamera(
        WINDOW_WIDTH.toFloat(),
        WINDOW_HEIGHT.toFloat()
    ).apply { setToOrtho(false) }
    private val tiledMap = assets.get<TiledMap>("map.tmx")
    private val mapRenderer = OrthoCachedTiledMapRenderer(tiledMap).apply { setBlending(true) }
    private val worldSize = WorldSize(tiledMap.totalWidth(), tiledMap.totalHeight())
    private var turtle: Entity by Delegates.notNull()
    private val world = World {
        inject(batch)
        inject(camera)
        inject(mapRenderer)
        inject(worldSize)
        system<InputSystem>()
        system<MovementSystem>()
        system<CollisionSystem>()
        system<CameraSystem>()
        system<AnimationSystem>()
        system<RenderSystem>()
    }

    init {
        registerAction(Input.Keys.W, Action.Name.UP)
        registerAction(Input.Keys.S, Action.Name.DOWN)
        registerAction(Input.Keys.A, Action.Name.LEFT)
        registerAction(Input.Keys.D, Action.Name.RIGHT)

        spawnPlayer()
        spawnObjects()

        world.systems.forEach {
            when (it::class) {
                InputSystem::class -> (it as InputSystem).player = turtle
                MovementSystem::class -> (it as MovementSystem).player = turtle
                CameraSystem::class -> (it as CameraSystem).player = turtle
                CollisionSystem::class -> (it as CollisionSystem).player = turtle
            }
        }
    }

    private fun spawnPlayer() {
        turtle = world.entity {
            add<PlayerComponent>()
            add<InputComponent>()
            add<RenderComponent>()
            add<TransformComponent> {
                position.set(50f, 50f)
                acceleration = 400f
                deceleration = 250f
                maxSpeed = 150f
            }
            add<AnimationComponent> {
                region = assets
                    .get<TextureAtlas>("starfish-collector.atlas")
                    .findRegion("turtle")
                frames = 6
                frameDuration = 0.1f
            }.apply {
                add<BoundingBoxComponent> {
                    val width = (region.regionWidth / frames)
                    val height = region.regionHeight
                    polygon = generatePolygon(8, width, height)
                }
            }
        }
    }

    private fun spawnObjects() {
        tiledMap.forEachMapObject("collision") { obj ->
            val texture = assets.get<Texture>("${obj.name}.png")
            world.entity {
                add<TransformComponent> { position.set(obj.x, obj.y) }
                add<RenderComponent> { sprite = Sprite(texture) }
                when (obj.name) {
                    "rock" -> {
                        add<RockComponent>()
                        add<BoundingBoxComponent> {
                            polygon = generatePolygon(8, texture.width, texture.height).apply {
                                setPosition(obj.x, obj.y)
                            }
                        }
                    }

                    "starfish" -> {
                        add<StarfishComponent>()
                        add<BoundingBoxComponent> {
                            polygon = generatePolygon(8, texture.width, texture.height).apply {
                                setPosition(obj.x, obj.y)
                            }
                        }
                    }

                    "sign" -> {
                        add<SignComponent>()
                        add<BoundingBoxComponent> {
                            polygon = generateRectangle(texture.width, texture.height).apply {
                                setPosition(obj.x, obj.y)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun doAction(action: Action) {
        val input = world.mapper<InputComponent>()[turtle]
        val isStarting = action.type == START
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
        assets.disposeSafely()
        mapRenderer.disposeSafely()
        tiledMap.disposeSafely()
    }
}
