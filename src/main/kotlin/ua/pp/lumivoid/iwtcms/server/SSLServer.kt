package ua.pp.lumivoid.iwtcms.server

import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.util.Config
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler
import java.io.File
import java.net.InetAddress
import java.net.SocketException
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLServerSocketFactory
import kotlin.concurrent.thread

@Suppress("DuplicatedCode")
object SSLServer: Server() {
    private val logger = LoggerFactory.getLogger("iwtcms internal server")

    private var serverSocket: SSLServerSocket? = null
    private val clients = mutableListOf<ClientHandler>()
    private var serverActive = true


    override fun setup() {
        logger.info("Starting internal server with SSL...")

        logger.info("Check is SSL keys available")
        if (!File(Constants.SSL_SERTIFICATE_FILE).exists()) {
            logger.warn("No SSL file found at ${Constants.SSL_SERTIFICATE_FILE}")
            logger.warn("Please generate SSL keys following guide on this page:")
            logger.warn("https://modrinth.com/mod/i-want-to-control-my-server")
            logger.warn("SSL server didn't be started")
            logger.warn("Stopping server for security reasons")
            logger.warn("Generate SSL keys or disable SSL to launch server")

            StoppedServerTrigger.requestLog {
                logger.warn("No SSL file found at ${Constants.SSL_SERTIFICATE_FILE}")
                logger.warn("Please generate SSL keys following guide on this page:")
                logger.warn("https://modrinth.com/mod/i-want-to-control-my-server")
                logger.warn("SSL server didn't be started")
                logger.warn("Stopping server for security reasons")
                logger.warn("Generate SSL keys or disable SSL to launch server")
            }

            MinecraftServerHandler.stop()

            return
        }

        logger.info("SSL keys found!")

        var ip = Config.readConfig().ip
        val port = Config.readConfig().port

        val sslContext = createSSLContext()
        val sslServerSocketFactory: SSLServerSocketFactory = sslContext.serverSocketFactory

        serverSocket = sslServerSocketFactory.createServerSocket(port, 50, InetAddress.getByName(ip)) as SSLServerSocket
        logger.info("Server started on $ip:$port")

        Constants.SERVER_INSTANCE = this

        thread {
            while (serverActive) {
                try {
                    val client = serverSocket!!.accept()

                    logger.info("New client connected: ${client.inetAddress}")

                    val clientHandler = ClientHandler(client, this)
                    clients.add(clientHandler)

                    thread {
                        clientHandler.run()
                    }
                } catch (_: SocketException) {
                    logger.info("Socket closed")
                }
            }
            logger.info("Internal server stopped")
        }
    }

    override fun broadcast(message: String) {
        clients.toList().forEach { // copy
            it.write(message)
        }
    }

    override fun shutdown() {
        logger.info("Shutting down internal server...")
        clients.toList().forEach {
            it.shutdown()
        }
        serverActive = false
        logger.info("Shutting down socket")
        serverSocket?.close()
    }

    override fun removeClient(client: ClientHandler) {
        clients.remove(client)
    }

    private fun createSSLContext(): SSLContext {
        val keyStore = KeyStore.getInstance("JKS")

        keyStore.load(File(Constants.SSL_SERTIFICATE_FILE).inputStream(), "keystorePassword".toCharArray())

        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, "keystorePassword".toCharArray())

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(keyManagerFactory.keyManagers, null, null)

        return sslContext
    }
}