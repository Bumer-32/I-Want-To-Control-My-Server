package ua.pp.lumivoid.iwtcms.server.util

import kotlinx.serialization.Serializable
import me.lucko.spark.api.SparkProvider
import me.lucko.spark.api.statistic.StatisticWindow
import ua.pp.lumivoid.iwtcms.server.Constants
import ua.pp.lumivoid.iwtcms.server.IWTCMS
import java.lang.management.ManagementFactory
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal object ServerStats {
    private fun getMemoryUsage(): Double {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        return usedMemory.toDouble() / totalMemory.toDouble() * 100.0
    }

    fun getServerStats(): ServerStatsData {
        val runtime = Runtime.getRuntime()
        val server = IWTCMS.instance.getMinecraftServer()

        var cpuLoad: Double? = null
        val memoryUsage = getMemoryUsage()
        val freeMemory = runtime.freeMemory()
        val totalMemory = runtime.totalMemory()
        val maxMemory = runtime.maxMemory()
        val uptime = ManagementFactory.getRuntimeMXBean().uptime
        val playerCount = server.playerList.playerCount
        val maxPlayerCount = server.playerList.maxPlayers
        var tps: Double? = null
        val ip = "${server.localIp}:${server.port}"
        val serverTime = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        val serverVersion = IWTCMS.instance.getMinecraftServer().serverVersion
        val platform = IWTCMS.instance.implName
        val iwtcmsVersion = Constants.MOD_VERSION

        if (IWTCMS.instance.isSparkLoaded()) {
            val spark = SparkProvider.get()
            cpuLoad = spark.cpuSystem().poll(StatisticWindow.CpuUsage.SECONDS_10)
            tps = spark.tps()?.poll(StatisticWindow.TicksPerSecond.SECONDS_5)
        }

        return ServerStatsData(
            cpuUsage = cpuLoad,
            memoryUsage = memoryUsage,
            freeMemory = freeMemory,
            totalMemory = totalMemory,
            maxMemory = maxMemory,
            uptime = uptime,
            playerCount = playerCount,
            maxPlayerCount = maxPlayerCount,
            tps = tps,
            ip = ip,
            serverTime = serverTime,
            serverVersion = serverVersion,
            platform = platform,
            iwtcmsVersion = iwtcmsVersion
        )
    }
}

@Serializable
internal data class ServerStatsData(
    val cpuUsage: Double?,
    val memoryUsage: Double,
    val freeMemory: Long,
    val totalMemory: Long,
    val maxMemory: Long,
    val uptime: Long,
    val playerCount: Int?,
    val maxPlayerCount: Int?,
    val tps: Double?,
    val ip: String?,
    val serverTime: String,
    val serverVersion: String,
    val platform: String,
    val iwtcmsVersion: String,
)
