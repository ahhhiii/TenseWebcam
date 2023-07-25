package net.bruhitsalex.tensewebcam

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.util.concurrent.Executors

object Server {

    private var threadPoolExecutor = Executors.newSingleThreadExecutor()

    init {
        println("Starting server")

        val server = HttpServer.create(InetSocketAddress("localhost", 8019), 0)
        server.executor = threadPoolExecutor

        server.createContext("/") {
            println("Request received for main page")
            val text = webpage.readText()
            val response = text.toByteArray()
            it.sendResponseHeaders(200, response.size.toLong())
            it.responseBody.write(response)
            it.responseBody.close()
        }

        server.createContext("/waiting") {
            println("Request received for waiting video")
            val response = waitingMp4Bytes ?: byteArrayOf()
            it.sendResponseHeaders(200, response.size.toLong())
            it.responseBody.write(response)
            it.responseBody.close()
        }

        server.createContext("/talking") {
            println("Request received for talking video")
            val response = talkingMp4Bytes ?: byteArrayOf()
            it.sendResponseHeaders(200, response.size.toLong())
            it.responseBody.write(response)
            it.responseBody.close()
        }

        server.start()
    }

}
