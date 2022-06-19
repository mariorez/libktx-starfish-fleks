package screen

import Action
import Action.Type.START
import BaseScreen
import GameBoot
import GameBoot.Companion.gameSizes
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import component.AnimationComponent
import component.BoundingBoxComponent
import component.InputComponent
import component.PlayerComponent
import component.RenderComponent
import component.RockComponent
import component.RotateEffectComponent
import component.SignComponent
import component.StarfishComponent
import component.TransformComponent
import generateButton
import generateFont
import generatePolygon
import generateRectangle
import ktx.actors.onTouchDown
import ktx.app.Platform
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import ktx.tiled.forEachMapObject
import ktx.tiled.totalHeight
import ktx.tiled.totalWidth
import ktx.tiled.type
import ktx.tiled.x
import ktx.tiled.y
import listener.ScoreListener
import system.AnimationSystem
import system.CameraSystem
import system.CollisionSystem
import system.FadeEffectSystem
import system.InputSystem
import system.MovementSystem
import system.RenderSystem
import system.RotateEffectSystem
import kotlin.properties.Delegates

class GameScreen(
    gameBoot: GameBoot,
    private val assets: AssetStorage,
) : BaseScreen(gameBoot) {
    private var world: World
    private var turtle: Entity by Delegates.notNull()
    private val tiledMap = assets.get<TiledMap>("map.tmx")
    private val mapRenderer = OrthoCachedTiledMapRenderer(tiledMap).apply { setBlending(true) }
    private var score = Label("", Label.LabelStyle().apply { font = generateFont() })
    private lateinit var touchpad: Touchpad

    init {
        gameSizes.worldWidth = tiledMap.totalWidth()
        gameSizes.worldHeight = tiledMap.totalHeight()

        world = World {
            inject(batch)
            inject(camera)
            inject(mapRenderer)
            inject(gameSizes)
            inject(assets)
            system<InputSystem>()
            system<MovementSystem>()
            system<CameraSystem>()
            system<AnimationSystem>()
            system<RotateEffectSystem>()
            system<FadeEffectSystem>()
            system<RenderSystem>()
            system<CollisionSystem>()
            familyListener<ScoreListener>()
        }

        buildHud()
        buildControls()
        spawnEntities()

        // late injections
        world.apply {
            system<MovementSystem>().player = turtle
            system<CameraSystem>().player = turtle
            system<CollisionSystem>().player = turtle
            system<InputSystem>().also {
                it.player = turtle
                if (Platform.isMobile) it.touchpad = touchpad
            }
        }
    }

    override fun render(delta: Float) {
        score.setText("Starfish Left: ${ScoreListener.total}")
        world.update(delta)
        hudStage.draw()
    }

    override fun dispose() {
        super.dispose()
        world.dispose()
        mapRenderer.disposeSafely()
        tiledMap.disposeSafely()
    }

    private fun buildHud() {
        val restartButton = generateButton(assets["undo.png"]).apply {
            onTouchDown { restart() }
        }

        hudStage.addActor(Table().apply {
            setFillParent(true)
            pad(5f)
            add(score).expandX().expandY().left().top()
            add(restartButton).top()
        })
    }

    private fun buildControls() {
        if (Platform.isMobile) {
            touchpad = Touchpad(5f, Touchpad.TouchpadStyle().apply {
                background = TextureRegionDrawable(TextureRegion(TextureRegion(assets.get<Texture>("touchpad-bg.png"))))
                knob = TextureRegionDrawable(TextureRegion(assets.get<Texture>("touchpad-knob.png")))
            })

            hudStage.addActor(Table().apply {
                setFillParent(true)
                add(touchpad).expandY().expandX().left().bottom()
            })
        } else {
            registerAction(Input.Keys.UP, Action.Name.UP)
            registerAction(Input.Keys.DOWN, Action.Name.DOWN)
            registerAction(Input.Keys.LEFT, Action.Name.LEFT)
            registerAction(Input.Keys.RIGHT, Action.Name.RIGHT)
        }
    }

    private fun restart() {
        world.family(arrayOf(StarfishComponent::class))
            .forEach { world.remove(it) }

        ScoreListener.total = 0

        tiledMap.forEachMapObject("collision") { obj ->
            when (obj.type) {
                "turtle" -> world.mapper<TransformComponent>()[turtle].apply {
                    position.set(obj.x, obj.y)
                    rotation = 0f
                }
                "starfish" -> spawnStarfish(obj.x, obj.y)
            }
        }
    }

    private fun spawnEntities() {
        tiledMap.forEachMapObject("collision") { obj ->
            when (obj.type) {
                "turtle" -> spawnPlayer(obj.x, obj.y)
                "starfish" -> spawnStarfish(obj.x, obj.y)
                else -> {
                    val texture = assets.get<Texture>("${obj.type}.png")
                    world.entity {
                        add<TransformComponent> { position.set(obj.x, obj.y) }
                        add<RenderComponent> { sprite = Sprite(texture) }
                        when (obj.type) {
                            "rock" -> {
                                add<RockComponent>()
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
        }
    }

    private fun spawnPlayer(x: Float, y: Float) {
        turtle = world.entity {
            add<PlayerComponent>()
            add<InputComponent>()
            add<RenderComponent>()
            add<TransformComponent> {
                position.set(x, y)
                zIndex = 1f
                acceleration = 400f
                deceleration = 250f
                maxSpeed = 150f
            }
            add<AnimationComponent> {
                region = assets.get<TextureAtlas>("starfish-collector.atlas").findRegion("turtle")
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

    private fun spawnStarfish(x: Float, y: Float) {
        val texture = assets.get<Texture>("starfish.png")
        world.entity {
            add<TransformComponent> { position.set(x, y) }
            add<RenderComponent> { sprite = Sprite(texture) }
            add<StarfishComponent>()
            add<RotateEffectComponent> { speed = 1f }
            add<BoundingBoxComponent> {
                polygon = generatePolygon(8, texture.width, texture.height).apply {
                    setPosition(x, y)
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
}
