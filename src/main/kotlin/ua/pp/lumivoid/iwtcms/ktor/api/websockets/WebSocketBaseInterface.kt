package ua.pp.lumivoid.iwtcms.ktor.api.websockets

interface WebSocketBaseInterface {
    suspend fun sendMessage(message: String) {}

    suspend fun shutdown() {}
}
