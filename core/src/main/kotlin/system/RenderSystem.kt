package system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.collection.compareEntity
import component.RenderComponent
import component.TransformComponent
import ktx.graphics.use

@AllOf([TransformComponent::class, RenderComponent::class])
class RenderSystem(
    private val batch: SpriteBatch,
    private val viewport: ExtendViewport,
    private val mapRenderer: OrthoCachedTiledMapRenderer,
    private val transform: ComponentMapper<TransformComponent>,
    private val render: ComponentMapper<RenderComponent>
) : IteratingSystem(
    compareEntity { entA, entB -> transform[entA].zIndex.compareTo(transform[entB].zIndex) }
) {

    override fun onTick() {
        mapRenderer.apply {
            setView(viewport.camera as OrthographicCamera)
            render()
        }
        batch.use(viewport.camera) {
            super.onTick()
        }
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
