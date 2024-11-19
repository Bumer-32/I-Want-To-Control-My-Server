package ua.pp.lumivoid.iwtcms.ktor.api

import ua.pp.lumivoid.iwtcms.util.Config

object UserAuthentication {
    fun checkId(id: String): Boolean {
        return Config.readConfig().users.any { it.id == id }
    }
}