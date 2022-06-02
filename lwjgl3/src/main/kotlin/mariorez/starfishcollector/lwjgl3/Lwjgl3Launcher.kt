@file:JvmName("Lwjgl3Launcher")

package mariorez.starfishcollector.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import mariorez.starfishcollector.GameBoot
import mariorez.starfishcollector.GameBoot.Companion.WINDOW_HEIGHT
import mariorez.starfishcollector.GameBoot.Companion.WINDOW_WIDTH

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(GameBoot(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("Starfish Collector")
        setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
