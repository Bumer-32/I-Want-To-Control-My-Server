package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.server.routing.Routing
import org.slf4j.Logger

abstract class Request {
    protected abstract val logger: Logger
    protected abstract val PATH: String
    abstract val request: Routing.() -> Unit
}