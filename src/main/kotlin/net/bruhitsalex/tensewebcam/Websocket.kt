package net.bruhitsalex.tensewebcam

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Duration

@OptIn(DelicateCoroutinesApi::class)
object Websocket {

    var session = arrayListOf<DefaultWebSocketSession>()

    init {
        GlobalScope.launch {
            embeddedServer(Netty, port = 8020, module = Application::myApplicationModule).start(wait = true)
        }
    }

    fun setTalking(talking: Boolean) {
        GlobalScope.launch {
            try {
                session.forEach { it.send(if (talking) "1" else "0") }
            } catch (e: Exception) {
                println("Socket closed")
            }
        }
    }

}

fun Application.myApplicationModule() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/") {
            println("Socket connected!")
            Websocket.session.add(this)
            try {
                while (true) {
                    incoming.receiveCatching().getOrNull() ?: break // keep connection open until client disconnects
                }
            } finally {
                println("Socket disconnected!")
                Websocket.session.remove(this)
            }
        }
    }
}