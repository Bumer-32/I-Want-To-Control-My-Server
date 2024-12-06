package ua.pp.lumivoid.iwtcms.ktor.api.websockets

interface WebSocketBaseInterface {
    fun sendMessage(message: String) {}
    fun shutdown() {}
}