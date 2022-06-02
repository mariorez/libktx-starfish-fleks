package mariorez.starfishcollector.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer
import com.github.quillraven.fleks.World
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import mariorez.starfishcollector.GameBoot.Companion.WINDOW_HEIGHT
import mariorez.starfishcollector.GameBoot.Companion.WINDOW_WIDTH
import mariorez.starfishcollector.component.RenderComponent
import mariorez.starfishcollector.component.TransformComponent
import mariorez.starfishcollector.system.RenderSystem

class GameScreen(
    private val assets: AssetStorage
) : KtxScreen {
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
        system<RenderSystem>()
    }

    init {
        spawnPlayer()
    }

    private fun spawnPlayer() {
        world.entity {
            add<TransformComponent> { position.set(100f, 100f) }
            add<RenderComponent> { sprite = Sprite(assets.get<Texture>("turtle.png")) }
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
