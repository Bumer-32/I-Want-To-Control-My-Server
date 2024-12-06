package ua.pp.lumivoid.iwtcms.util

import kotlinx.serialization.Serializable
import java.lang.management.ManagementFactory

object ServerStats {
    private fun getCPUUsage(): Double {
        val osBean = ManagementFactory.getOperatingSystemMXBean()
        val cpuLoad = osBean.systemLoadAverage
        return if (cpuLoad.isNaN()) 0.0 else cpuLoad
    }

    private fun getMemoryUsage(): Double {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        return usedMemory.toDouble() / totalMemory.toDouble() * 100.0
    }

    fun getServerStats(): ServerStatsData {
        val runtime = Runtime.getRuntime()

        val cpuLoad = getCPUUsage()
        val memoryUsage = getMemoryUsage()
        val freeMemory = runtime.freeMemory()
        val totalMemory = runtime.totalMemory()
        val maxMemory = runtime.maxMemory()
        val uptime = ManagementFactory.getRuntimeMXBean().uptime
        val playerCount = MinecraftServerHandler.server?.playerManager?.currentPlayerCount
        val maxPlayerCount = MinecraftServerHandler.server?.playerManager?.maxPlayerCount
        val mspt = MinecraftServerHandler.server?.tickManager?.nanosPerTick?.div(1_000_000.0)
        val tps = if (MinecraftServerHandler.server?.tickManager?.tickRate != null && mspt != null) 1000 / (mspt / MinecraftServerHandler.server?.tickManager?.tickRate!!) else null
        val ip = MinecraftServerHandler.server?.serverIp

        return ServerStatsData(
            cpuUsage = cpuLoad,
            memoryUsage = memoryUsage,
            freeMemory = freeMemory,
            totalMemory = totalMemory,
            maxMemory = maxMemory,
            uptime = uptime,
            playerCount = playerCount,
            maxPlayerCount = maxPlayerCount,
            mspt = mspt,
            tps = tps,
            ip = ip
        )
    }
}

@Serializable
data class ServerStatsData(
    val cpuUsage: Double,
    val memoryUsage: Double,
    val freeMemory: Long,
    val totalMemory: Long,
    val maxMemory: Long,
    val uptime: Long,
    val playerCount: Int?,
    val maxPlayerCount: Int?,
    val mspt: Double?,
    val tps: Double?,
    val ip: String?
)