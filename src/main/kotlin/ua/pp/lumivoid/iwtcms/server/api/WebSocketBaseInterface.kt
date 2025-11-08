package ua.pp.lumivoid.iwtcms.server.api

interface WebSocketBaseInterface {
    suspend fun sendMessage(message: String) {}

    suspend fun shutdown() {}
}