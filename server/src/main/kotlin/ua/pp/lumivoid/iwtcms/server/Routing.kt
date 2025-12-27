package ua.pp.lumivoid.iwtcms.server

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication.CheckLogin
import ua.pp.lumivoid.iwtcms.server.api.requests.api.Configs
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.CreateUser
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.DeleteUser
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.EditPermissions
import ua.pp.lumivoid.iwtcms.server.api.requests.Files
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.IsAllowed
import ua.pp.lumivoid.iwtcms.server.api.requests.api.IsDevEnabled
import ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication.Login
import ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication.Logout
import ua.pp.lumivoid.iwtcms.server.api.requests.api.LogsHistory
import ua.pp.lumivoid.iwtcms.server.api.requests.Main
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.UsersList
import ua.pp.lumivoid.iwtcms.server.api.requests.api.Version
import ua.pp.lumivoid.iwtcms.server.api.requests.api.player.Ban
import ua.pp.lumivoid.iwtcms.server.api.requests.api.player.BanIp
import ua.pp.lumivoid.iwtcms.server.api.requests.api.player.BanIpList
import ua.pp.lumivoid.iwtcms.server.api.requests.api.player.BanList
import ua.pp.lumivoid.iwtcms.server.api.requests.api.player.DeOp
import ua.pp.lumivoid.iwtcms.server.api.requests.api.player.Kick
import ua.pp.lumivoid.iwtcms.server.api.requests.api.player.Kill
import ua.pp.lumivoid.iwtcms.server.api.requests.api.player.Op
import ua.pp.lumivoid.iwtcms.server.api.requests.api.player.Pardon
import ua.pp.lumivoid.iwtcms.server.api.requests.api.player.PardonIp
import ua.pp.lumivoid.iwtcms.server.api.requests.api.ws.ConsoleWS
import ua.pp.lumivoid.iwtcms.server.api.requests.api.ws.PlayersWS
import ua.pp.lumivoid.iwtcms.server.api.requests.api.ws.ServerStatsWS

private val logger = Constants.LOGGER

internal fun Application.configureRouting() {
    logger.info("-=-=-=-=-=-=-=-=-=- Registering routes -=-=-=-=-=-=-=-=-=-")
    val r = routing {}

    // * /api/authentication
    CheckLogin.register(r)
    Login.register(r)
    Logout.register(r)

    // * /api/player
    Ban.register(r)
    BanIp.register(r)
    BanIpList.register(r)
    BanList.register(r)
    DeOp.register(r)
    Kick.register(r)
    Kill.register(r)
    Op.register(r)
    Pardon.register(r)
    PardonIp.register(r)

    // * /api/user
    CreateUser.register(r)
    DeleteUser.register(r)
    EditPermissions.register(r)
    IsAllowed.register(r)
    PermissionsList.register(r)
    UsersList.register(r)

    // * /api/ws
    ConsoleWS.register(r)
    PlayersWS.register(r)
    ServerStatsWS.register(r)

    // * /api
    Configs.register(r)
    IsDevEnabled.register(r)
    LogsHistory.register(r)
    Version.register(r)

    // * /
    Files.register(r)
    Main.register(r)



    logger.info("-=-=-=-=-=-=-=-=-=- Routes registered -=-=-=-=-=-=-=-=-=-")
}
