package screen

import Action
import Action.Type.START
import BaseScreen
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
import generatePolygon
import generateRectangle
import ktx.app.Platform
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import ktx.tiled.forEachMapObject
import ktx.tiled.totalHeight
import ktx.tiled.totalWidth
import ktx.tiled.x
import ktx.tiled.y
import listener.StarfishCounterListener
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
    private val assets: AssetStorage,
    labelStyle: Label.LabelStyle
) : BaseScreen() {
    private val tiledMap = assets.get<TiledMap>("map.tmx")
    private val mapRenderer = OrthoCachedTiledMapRenderer(tiledMap).apply { setBlending(true) }
    private var turtle: Entity by Delegates.notNull()
    private var starFishLabel = Label("", labelStyle)
    private lateinit var touchpad: Touchpad
    private lateinit var counter: StarfishCounterListener
    private val world = World {
        inject(batch)
        inject(camera)
        inject(mapRenderer)
        inject(gameSizes)
        inject(assets)
        system<InputSystem>()
        system<MovementSystem>()
        system<CollisionSystem>()
        system<CameraSystem>()
        system<AnimationSystem>()
        system<RotateEffectSystem>()
        system<FadeEffectSystem>()
        system<RenderSystem>()
        familyListener<StarfishCounterListener>()
    }

    init {
        gameSizes.worldWidth = tiledMap.totalWidth()
        gameSizes.worldHeight = tiledMap.totalHeight()

        if (Platform.isMobile) {
            buildTouchpad()
        } else {
            registerAction(Input.Keys.UP, Action.Name.UP)
            registerAction(Input.Keys.DOWN, Action.Name.DOWN)
            registerAction(Input.Keys.LEFT, Action.Name.LEFT)
            registerAction(Input.Keys.RIGHT, Action.Name.RIGHT)
        }

        uiStage.addActor(Table().apply {
            setFillParent(true)
            pad(5f)
            add(starFishLabel).expandX().expandY().left().top()
        })

        spawnObjects()

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
            if (obj.name == "turtle") {
                spawnPlayer(obj.x, obj.y)
            } else {
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
                            add<RotateEffectComponent> { speed = 1f }
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

    private fun buildTouchpad() {
        touchpad = Touchpad(5f, Touchpad.TouchpadStyle().apply {
            background = TextureRegionDrawable(TextureRegion(TextureRegion(assets.get<Texture>("touchpad-bg.png"))))
            knob = TextureRegionDrawable(TextureRegion(assets.get<Texture>("touchpad-knob.png")))
        })

        uiStage.addActor(Table().apply {
            setFillParent(true)
            add(touchpad).expandY().expandX().left().bottom()
        })
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
        starFishLabel.setText("Starfish Left: ${StarfishCounterListener.counter}")
        uiStage.draw()
    }

    override fun dispose() {
        super.dispose()
        world.dispose()
        batch.disposeSafely()
        assets.disposeSafely()
        mapRenderer.disposeSafely()
        tiledMap.disposeSafely()
    }
}
