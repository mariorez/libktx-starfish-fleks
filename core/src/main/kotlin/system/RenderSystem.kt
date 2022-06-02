package system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.RenderComponent
import component.TransformComponent

@AllOf([TransformComponent::class, RenderComponent::class])
class RenderSystem(
    private val batch: SpriteBatch,
    private val camera: OrthographicCamera,
    private val mapRenderer: OrthoCachedTiledMapRenderer,
    private val transform: ComponentMapper<TransformComponent>,
    private val render: ComponentMapper<RenderComponent>
) : IteratingSystem() {

    override fun onTick() {
        mapRenderer.setView(camera)
        mapRenderer.render()
        batch.projectionMatrix = camera.combined
        batch.begin()
        super.onTick()
        batch.end()
    }

    override fun onTickEntity(entity: Entity) {
        render[entity].apply {
            transform[entity].also { transform ->
                sprite.apply {
                    setOriginCenter()
                    rotation = transform.rotation
                    setBounds(transform.position.x, transform.position.y, width, height)
                    draw(batch)
                }
            }
        }
    }
}