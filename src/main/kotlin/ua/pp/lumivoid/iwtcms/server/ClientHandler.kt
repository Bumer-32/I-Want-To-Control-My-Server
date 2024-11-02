package ua.pp.lumivoid.iwtcms.server

import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.util.Config
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
    private var authorized = false

    fun run() {
        running = true

        write("iwtcms_connected\n")

        while (running) {
            try {
                val data = reader.nextLine()
                command(data)
            } catch (_: Exception) {
                logger.warn("Connection lost with ${client.inetAddress}")
                shutdown()
                break
            }
        }
    }

    fun write(message: String, authNeeds: Boolean = true) {
        thread { // thread for every client, because ALL clients must get a message
            if (authNeeds && !authorized && Config.readConfig().useAuthentication) return@thread // Authentication

            try {
                writer.write((message).toByteArray(Charset.defaultCharset()))
            } catch (_: Exception) {
            }
        }
    }

    fun shutdown() {
        write("iwtcms_shutdown\n", false)
        logger.info("Shutting down client ${client.inetAddress}")
        running = false
        client.close()
        server.removeClient(this)
    }


    private fun command(data: String) {
        when {
            data  == "iwtcms_shutdown" -> {
                shutdown()
            }

            data == "iwtcms_ping" -> {
                write("iwtcms_pong\n")
            }

            data.startsWith("iwtcms_login") -> {
                val loginData = data.split(" ")

                if (loginData[1].replace("\n", "") == Constants.passwordHash) {
                    authorized = true
                    write("iwtcms_login_success\n")
                } else {
                    write("iwtcms_login_failed\n", false)
                }

            }

            else -> {
                if (!authorized && Config.readConfig().useAuthentication) { // Authentication
                    write("iwtcms_not_authorized\n", false)
                    logger.warn("Client ${client.inetAddress} tried to send data without authorization")
                    return
                }

                logger.info("Received data from ${client.inetAddress}: $data")

                try {
                    if (MinecraftServerHandler.server != null) {
                        MinecraftServerHandler.server!!.commandManager.executeWithPrefix(MinecraftServerHandler.server!!.commandSource, data)
                    }
                } catch (e: Exception) {
                    e.stackTrace.forEach { logger.error(it.toString()) }
                }
            }
        }
    }
}
