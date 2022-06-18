@file:JvmName("Lwjgl3Launcher")

package mariorez.starfishcollector.lwjgl3

import GameBoot
import GameBoot.Companion.gameSizes
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(GameBoot(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("Starfish Collector")
        setWindowedMode(gameSizes.windowWidth, gameSizes.windowHeight)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
