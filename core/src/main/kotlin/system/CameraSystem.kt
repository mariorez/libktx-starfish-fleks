package system

import GameSizes
import com.badlogic.gdx.math.MathUtils.clamp
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import component.TransformComponent
import kotlin.properties.Delegates

class CameraSystem(
    private val viewport: ExtendViewport,
    private val gameSizes: GameSizes,
    private val transform: ComponentMapper<TransformComponent>,
) : IntervalSystem() {

    var player: Entity by Delegates.notNull()
    private val middleWidth = viewport.camera.viewportWidth / 2
    private val middleHeight = viewport.camera.viewportHeight / 2

    override fun onTick() {
        transform[player].position.apply {
            viewport.camera.position.x = clamp(x, middleWidth, gameSizes.worldWidth - middleWidth)
            viewport.camera.position.y = clamp(y, middleHeight, gameSizes.worldHeight - middleHeight)
            viewport.camera.update()
        }
    }
}
