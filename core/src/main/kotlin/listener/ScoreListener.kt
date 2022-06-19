package listener

import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.FamilyListener
import component.StarfishComponent

@AllOf([StarfishComponent::class])
class ScoreListener : FamilyListener {

    companion object {
        var total = 0
    }

    override fun onEntityAdded(entity: Entity) {
        total++
    }

    override fun onEntityRemoved(entity: Entity) {
        total--
    }
}
