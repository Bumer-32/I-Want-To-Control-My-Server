export default class Constants {
    // URLS
    static BASE_URL = document.location.origin + "/";

    static PAGE_BAD_CONNECTION_URL = this.BASE_URL + "BadConnection";

    // api urls
    static IS_DEV_ENABLED_URL = this.BASE_URL + "api/isDevEnabled";
    static LOGS_HISTORY_URL = this.BASE_URL + "api/logsHistory";
    static IS_ALLOWED_URL = this.BASE_URL + "api/isAllowed";
    static VERSION_URL = this.BASE_URL + "api/version";
    static CHECK_LOGIN_URL = this.BASE_URL + "api/checkLogin";
    static LOGIN_URL = this.BASE_URL + "api/login";
    static LOGOUT_URL = this.BASE_URL + "api/logout";
    static CONFIG_URL = this.BASE_URL + "api/config";
    static CREATE_USER_URL = this.BASE_URL + "api/createUser";
    static EDIT_PERMISSIONS_URL = this.BASE_URL + "api/editPermissions";
    static DELETE_USER_URL = this.BASE_URL + "api/deleteUser";
    static USERS_LIST_URL = this.BASE_URL + "api/usersList";
    static PERMISSIONS_LIST_URL = this.BASE_URL + "api/permissionsList";

    // ws
    static CONSOLE_URL = this.BASE_URL + "ws/console";
    static STATS_URL = this.BASE_URL + "ws/serverStats";

    // OTHER
    static IWTCMS_VERSION: string;
    static PERMITS: any;
}
