package listener

import java.lang.System.currentTimeMillis

data class ScoreManager(
    var total: Int = 0
) {
    private var best: Long = 0L
    private var begin: Long = 0L
    private var end: Long = 0L

    fun reset() {
        total = 0
        begin = 0L
        end = 0L
    }

    fun start() {
        if (begin == 0L) begin = currentTimeMillis()
    }

    fun stop() {
        if (end == 0L) {
            end = currentTimeMillis()
            updateBestTime(end - begin)
        }
    }

    private fun updateBestTime(time: Long) {
        if (best == 0L || best > time) best = time
    }

    private fun elapsedTime(): Long {
        return if (begin > 0L && end == 0L) currentTimeMillis() - begin else end - begin
    }

    fun print(): String {
        return "Starfishes: $total - Time: ${elapsedTime()} ms - Best: $best ms"
    }
}
