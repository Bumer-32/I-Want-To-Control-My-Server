package ua.pp.lumivoid.iwtcms.ktor.api

import ua.pp.lumivoid.iwtcms.Constants

object PermissionsList {
    private var permissions: MutableList<String> = mutableListOf()

    init {
        permissions = this.javaClass.getResourceAsStream(Constants.PERMISSIONS_FILE)!!.bufferedReader().readLines().toMutableList()
    }

    fun getPermissionsList(): List<String> {
        return permissions
    }

    fun createPermission(permission: String) {
        permissions.add(permission)
    }
}