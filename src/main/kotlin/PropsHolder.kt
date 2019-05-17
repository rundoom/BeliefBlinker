package main.kotlin

import java.io.File
import java.util.*

val props = Properties().apply {
    val confFile = File("config.properties")
    if (confFile.exists()) load(confFile.inputStream())
    else {
        val resourcePath = System::class.java.getResource("/config.properties").path
        load(File(resourcePath).inputStream())
    }
}

val internalPort = props["internal.port"].toString().toInt()
val respondSecret = props["respond.secret"].toString()