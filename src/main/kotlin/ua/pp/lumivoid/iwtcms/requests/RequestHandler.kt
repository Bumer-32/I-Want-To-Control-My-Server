package ua.pp.lumivoid.iwtcms.requests

import ua.pp.lumivoid.iwtcms.Constants

object RequestHandler {
    private val logger = Constants.EMBEDDED_SERVER_LOGGER

    fun processRequest(data: String) {
        logger.info("Received: $data") // needs changes, it must hide all passwords and other shit
    }
}