export default class Constants {
    // URLS
    static BASE_URL = document.location.origin + "/";

    static PAGE_BAD_CONNECTION_URL = this.BASE_URL + "BadConnection";

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

    // * /ws
    static CONSOLE_URL = this.BASE_URL + "ws/console";
    static STATS_URL = this.BASE_URL + "ws/serverStats";

    // * /api
    static CONFIG_URL = this.BASE_URL + "api/config";
    static IS_DEV_ENABLED_URL = this.BASE_URL + "api/isDevEnabled";
    static LOGS_HISTORY_URL = this.BASE_URL + "api/logsHistory";
    static VERSION_URL = this.BASE_URL + "api/version";
}
