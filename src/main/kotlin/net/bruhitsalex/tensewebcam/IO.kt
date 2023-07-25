package net.bruhitsalex.tensewebcam

private val classLoader = Thread.currentThread().contextClassLoader
private val waitingResource = classLoader.getResource("waiting.mp4")!!
private val talkingResource = classLoader.getResource("talking.mp4")!!

val webpage = classLoader.getResource("webpage.html")!!
var waitingMp4Bytes: ByteArray? = null
var talkingMp4Bytes: ByteArray? = null

object IO {

    init {
        println("Copying resources...")

        waitingResource.openStream().use { waitingMp4Bytes = it.readBytes() }
        talkingResource.openStream().use { talkingMp4Bytes = it.readBytes() }
    }

}