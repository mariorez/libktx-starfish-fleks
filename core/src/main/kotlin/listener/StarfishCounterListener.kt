package listener

import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.FamilyListener
import component.StarfishComponent

@AllOf([StarfishComponent::class])
class StarfishCounterListener : FamilyListener {

    companion object {
        var counter = 0
    }

    override fun onEntityAdded(entity: Entity) {
        counter++
    }

    override fun onEntityRemoved(entity: Entity) {
        counter--
    }
}
