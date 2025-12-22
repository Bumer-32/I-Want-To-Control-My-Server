package ua.pp.lumivoid.iwtcms.server.tables

import org.jetbrains.exposed.v1.core.Table

object MetaTable: Table("meta") {
    val key = varchar("key", 256)
    val value = varchar("value", 256)

    override val primaryKey = PrimaryKey(key)
}