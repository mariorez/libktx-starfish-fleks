package system

import GameBoot.Companion.sizes
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils.clamp
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import component.TransformComponent
import kotlin.properties.Delegates

class CameraSystem(
    private val camera: OrthographicCamera,
    private val transform: ComponentMapper<TransformComponent>,
) : IntervalSystem() {

    var player: Entity by Delegates.notNull()
    private val middleWidth = camera.viewportWidth / 2
    private val middleHeight = camera.viewportHeight / 2

    override fun onTick() {
        transform[player].position.apply {
            camera.position.x = clamp(x, middleWidth, sizes.worldWidth - middleWidth)
            camera.position.y = clamp(y, middleHeight, sizes.worldHeight - middleHeight)
            camera.update()
        }
    }
}
