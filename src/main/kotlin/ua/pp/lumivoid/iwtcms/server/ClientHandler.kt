package ua.pp.lumivoid.iwtcms.server

import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.Scanner
import kotlin.concurrent.thread

class ClientHandler(client: Socket?, private val server: Server) {
    private val logger = LoggerFactory.getLogger("iwtcms internal server")

    private val client: Socket = client!!
    private val reader: Scanner = Scanner(this.client.getInputStream())
    private val writer: OutputStream = this.client.getOutputStream()
    private var running = false

    fun run() {
        running = true

        write("iwtcms_connected\n")

        while (running) {
            try {
                val data = reader.nextLine()
                when (data) {
                    "iwtcms_shutdown" -> {
                        shutdown()
                        break
                    }
                    "iwtcms_ping" -> {
                        write("iwtcms_pong\n")
                    }
                    else -> {
                        logger.info("Received data from ${client.inetAddress}: $data")
                        try {
                            if (MinecraftServerHandler.server != null) {
                                MinecraftServerHandler.server!!.commandManager.executeWithPrefix(
                                    MinecraftServerHandler.server!!.commandSource,
                                    data
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (_: Exception) {
                logger.warn("Connection lost with ${client.inetAddress}")
                shutdown()
                break
            }
        }
    }

    fun write(message: String) {
        thread { // thread for every client, because ALL clients must get message
            try {
                writer.write((message).toByteArray(Charset.defaultCharset()))
            } catch (_: Exception) {
            }
        }
    }

    fun shutdown() {
        write("iwtcms_shutdown\n")
        logger.info("Shutting down client ${client.inetAddress}")
        running = false
        client.close()
        server.removeClient(this)
    }
}