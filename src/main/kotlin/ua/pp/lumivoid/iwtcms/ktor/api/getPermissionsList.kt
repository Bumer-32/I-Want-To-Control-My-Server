package ua.pp.lumivoid.iwtcms.ktor.api

import ua.pp.lumivoid.iwtcms.Constants

private object ResourceLoader

private var cache: List<String>? = null

fun getPermissionsList(): List<String> {
    cache?.let { return it }

    return ResourceLoader.javaClass.getResourceAsStream(Constants.PERMISSIONS_FILE)!!.bufferedReader().readLines()
}