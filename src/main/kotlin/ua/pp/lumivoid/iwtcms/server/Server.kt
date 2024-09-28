package ua.pp.lumivoid.iwtcms.server

import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.util.Config
import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketException
import kotlin.concurrent.thread

object Server {
    private val logger = LoggerFactory.getLogger("iwtcms internal server")

    private var serverSocket: ServerSocket? = null
    private val clients = mutableListOf<ClientHandler>()
    private var serverActive = true


    fun setup() {
        logger.info("Starting internal server...")
        var ip = Config.readConfig().ip
        val port = Config.readConfig().port

        serverSocket = ServerSocket(port, 50, InetAddress.getByName(ip))
        logger.info("Server started on $ip:$port")

        thread {
            while (serverActive) {
                try {
                    val client = serverSocket!!.accept()

                    logger.info("New client connected: ${client.inetAddress}")
                    logger.info(clients.toString())

                    val clientHandler = ClientHandler(client, this)
                    clients.add(clientHandler)

                    thread {
                        clientHandler.run()
                    }
                } catch (e: SocketException) {
                    logger.info("Socket closed")
                }
            }
            logger.info("Internal server stopped")
        }
    }

    fun broadcast(message: String) {
        clients.toList().forEach { // copy
            it.write(message)
        }
    }

    fun shutdown() {
        logger.info("Shutting down internal server...")
        clients.toList().forEach {
            it.shutdown()
        }
        serverActive = false
        logger.info("Shutting down socket")
        serverSocket!!.close()
    }

    fun removeClient(client: ClientHandler) {
        clients.remove(client)
    }
}