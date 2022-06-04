package component

import com.badlogic.gdx.math.Polygon

data class BoundingBoxComponent(
    var polygon: Polygon = Polygon()
)
