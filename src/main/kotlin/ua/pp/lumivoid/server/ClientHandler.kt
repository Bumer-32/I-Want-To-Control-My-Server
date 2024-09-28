package ua.pp.lumivoid.server

import net.minecraft.client.MinecraftClient
import net.minecraft.server.MinecraftServer
import net.minecraft.server.dedicated.DedicatedServer
import org.slf4j.LoggerFactory
import ua.pp.lumivoid.util.MinecraftServerStartedTrigger
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.Scanner

class ClientHandler(client: Socket?, private val server: Server) {
    private val logger = LoggerFactory.getLogger("iwtcms internal server")

    private val client: Socket = client!!
    private val reader: Scanner = Scanner(this.client.getInputStream())
    private val writer: OutputStream = this.client.getOutputStream()
    private var running = false

    fun run() {
        running = true

        write("iwtcms_connected")

        while (running) {
            try {
                val data = reader.nextLine()
                when (data) {
                    "iwtcms_shutdown" -> {
                        shutdown()
                        break
                    }
                    "iwtcms_ping" -> {
                        write("iwtcms_pong")
                    }
                    else -> {
                        logger.info("Received data from ${client.inetAddress}: $data")
                        try {
                            if (MinecraftServerStartedTrigger.server != null) {
                                MinecraftServerStartedTrigger.server!!.commandManager.executeWithPrefix(
                                    MinecraftServerStartedTrigger.server!!.commandSource,
                                    data
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: Exception) {
                logger.warn("Connection lost with ${client.inetAddress}")
                shutdown()
                break
            }
        }
    }

    fun write(message: String) {
        try {
            writer.write((message).toByteArray(Charset.defaultCharset()))
        } catch (e: Exception) {
        }
    }

    fun shutdown() {
        write("iwtcms_shutdown")
        logger.info("Shutting down client ${client.inetAddress}")
        running = false
        client.close()
        server.removeClient(this)
    }
}