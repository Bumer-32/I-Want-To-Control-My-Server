export default class Constants {
    // URLS
    static BASE_URL = document.location.origin + "/";

    // api urls
    // * /api/authentication
    static CHECK_LOGIN_URL = this.BASE_URL + "api/authentication/checkLogin";
    static LOGIN_URL = this.BASE_URL + "api/authentication/login";
    static LOGOUT_URL = this.BASE_URL + "api/authentication/logout";

    // * /api/user
    static CREATE_USER_URL = this.BASE_URL + "api/user/createUser";
    static DELETE_USER_URL = this.BASE_URL + "api/user/deleteUser";
    static EDIT_PERMISSIONS_URL = this.BASE_URL + "api/user/editPermissions";
    static IS_ALLOWED_URL = this.BASE_URL + "api/user/isAllowed";
    static PERMISSIONS_LIST_URL = this.BASE_URL + "api/user/permissionsList";
    static USERS_LIST_URL = this.BASE_URL + "api/user/usersList";

    // * /api/player
    static BAN_PLAYER_URL = this.BASE_URL + "api/player/ban";
    static BAN_IP_PLAYER_URL = this.BASE_URL + "api/player/banIp";
    static BAN_LIST_PLAYER_URL = this.BASE_URL + "api/player/banList";
    static BAN_IP_LIST_PLAYER_URL = this.BASE_URL + "api/player/banIpList";
    static DEOP_PLAYER_URL = this.BASE_URL + "api/player/deOp";
    static KICK_PLAYER_URL = this.BASE_URL + "api/player/kick";
    static KILL_PLAYER_URL = this.BASE_URL + "api/player/kill";
    static OP_PLAYER_URL = this.BASE_URL + "api/player/op";
    static PARDON_PLAYER_URL = this.BASE_URL + "api/player/pardon";
    static PARDON_IP_PLAYER_URL = this.BASE_URL + "api/player/pardonIp";

    // * /ws
    static CONSOLE_URL = this.BASE_URL + "ws/console";
    static STATS_URL = this.BASE_URL + "ws/serverStats";
    static PLAYERS_URL = this.BASE_URL + "ws/players";

    // * /api
    static CONFIG_URL = this.BASE_URL + "api/config";
    static IS_DEV_ENABLED_URL = this.BASE_URL + "api/isDevEnabled";
    static LOGS_HISTORY_URL = this.BASE_URL + "api/logsHistory";
    static VERSION_URL = this.BASE_URL + "api/version";
}
