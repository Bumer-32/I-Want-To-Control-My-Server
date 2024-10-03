package ua.pp.lumivoid.iwtcms.server

abstract class Server {
    abstract fun setup()
    abstract fun broadcast(message: String)
    abstract fun shutdown()
    abstract fun removeClient(client: ClientHandler)
}