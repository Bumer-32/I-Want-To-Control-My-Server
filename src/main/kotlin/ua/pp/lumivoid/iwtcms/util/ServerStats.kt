package ua.pp.lumivoid.iwtcms.util

import kotlinx.serialization.Serializable
import me.lucko.spark.api.SparkProvider
import me.lucko.spark.api.statistic.StatisticWindow
import net.fabricmc.loader.api.FabricLoader
import ua.pp.lumivoid.iwtcms.Constants
import java.lang.management.ManagementFactory

object ServerStats {
    private fun getMemoryUsage(): Double {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        return usedMemory.toDouble() / totalMemory.toDouble() * 100.0
    }

    fun getServerStats(): ServerStatsData {
        val runtime = Runtime.getRuntime()

        var cpuLoad: Double? = null
        val memoryUsage = getMemoryUsage()
        val freeMemory = runtime.freeMemory()
        val totalMemory = runtime.totalMemory()
        val maxMemory = runtime.maxMemory()
        val uptime = ManagementFactory.getRuntimeMXBean().uptime
        val playerCount = MinecraftServerHandler.server?.playerManager?.currentPlayerCount
        val maxPlayerCount = MinecraftServerHandler.server?.playerManager?.maxPlayerCount
        var tps: Double? = null
        val ip = "${MinecraftServerHandler.server?.serverIp}:${MinecraftServerHandler.server?.serverPort}"

        if (FabricLoader.getInstance().isModLoaded(Constants.SPARK_FABRIC_ID)) {
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
            ip = ip
        )
    }
}

@Serializable
data class ServerStatsData(
    val cpuUsage: Double?,
    val memoryUsage: Double,
    val freeMemory: Long,
    val totalMemory: Long,
    val maxMemory: Long,
    val uptime: Long,
    val playerCount: Int?,
    val maxPlayerCount: Int?,
    val tps: Double?,
    val ip: String?
)