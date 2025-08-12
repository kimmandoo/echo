package echo.kimmandoo.app.feature.home

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import echo.kimmandoo.app.feature.home.model.MenuItem
import kotlin.math.sqrt

class MenuPhysicsController(
    private val menuItemsState: SnapshotStateList<MenuItem>,
    private val containerSize: IntSize,
    private val itemRadiusPx: Float,
) {
    private val friction = 0.98f
    private val repulsionStrength = 2000f
    private val boundaryDamping = 0.8f

    fun update(
        deltaTime: Float,
        draggedItemId: Int?,
    ) {
        val currentItems = menuItemsState.toList()
        val nextOffsets = Array(currentItems.size) { Offset.Zero }
        val nextVelocities = Array(currentItems.size) { Offset.Zero }

        for (i in currentItems.indices) {
            if (currentItems[i].id == draggedItemId) {
                nextOffsets[i] = currentItems[i].offset
                nextVelocities[i] = Offset.Zero
                continue
            }

            val netForce = calculateNetForce(i, currentItems)
            val (newOffset, newVelocity) = updatePositionAndVelocity(currentItems[i], netForce, deltaTime)

            nextOffsets[i] = newOffset
            nextVelocities[i] = newVelocity
        }

        for (i in menuItemsState.indices) {
            if (menuItemsState[i].id != draggedItemId) {
                menuItemsState[i] =
                    menuItemsState[i].copy(
                        offset = nextOffsets[i],
                        velocity = nextVelocities[i],
                    )
            }
        }
    }

    private fun calculateNetForce(
        index: Int,
        items: List<MenuItem>,
    ): Offset {
        var netForce = Offset.Zero
        val currentItem = items[index]

        for (j in items.indices) {
            if (index == j) continue
            val otherItem = items[j]
            val dx = currentItem.offset.x - otherItem.offset.x
            val dy = currentItem.offset.y - otherItem.offset.y
            val distance = sqrt(dx * dx + dy * dy)
            val minDistance = 2 * itemRadiusPx

            if (distance < minDistance && distance != 0f) {
                val direction = Offset(dx, dy) / distance
                val forceMagnitude = repulsionStrength * (minDistance - distance) / minDistance
                netForce += direction * forceMagnitude
            }
        }
        return netForce
    }

    private fun updatePositionAndVelocity(
        item: MenuItem,
        netForce: Offset,
        deltaTime: Float,
    ): Pair<Offset, Offset> {
        var newVelocity = (item.velocity + netForce * deltaTime) * friction
        val newOffset = item.offset + newVelocity * deltaTime

        val xMax = (containerSize.width / 2) - itemRadiusPx
        val yMax = (containerSize.height / 2) - itemRadiusPx

        val clampedX = newOffset.x.coerceIn(-xMax, xMax)
        val clampedY = newOffset.y.coerceIn(-yMax, yMax)

        if (newOffset.x !in -xMax..xMax) {
            newVelocity = newVelocity.copy(x = -newVelocity.x * boundaryDamping)
        }
        if (newOffset.y !in -yMax..yMax) {
            newVelocity = newVelocity.copy(y = -newVelocity.y * boundaryDamping)
        }

        return Pair(Offset(clampedX, clampedY), newVelocity)
    }
}
