package system

import WorldSize
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils.clamp
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import component.TransformComponent
import kotlin.properties.Delegates

class CameraSystem(
    private val camera: OrthographicCamera,
    private val worldSize: WorldSize,
    private val transform: ComponentMapper<TransformComponent>,
) : IntervalSystem() {

    var player: Entity by Delegates.notNull()
    private val middleWidth = camera.viewportWidth / 2
    private val middleHeight = camera.viewportHeight / 2

    override fun onTick() {
        transform[player].position.apply {
            camera.position.x = clamp(x, middleWidth, worldSize.width - middleWidth)
            camera.position.y = clamp(y, middleHeight, worldSize.height - middleHeight)
            camera.update()
        }
    }
}
