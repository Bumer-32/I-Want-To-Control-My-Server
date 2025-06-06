export default class Constants {
    // URLS
    static BASE_URL = document.location.origin + "/";
    static PAGE_BAD_CONNECTION_URL = this.BASE_URL + "BadConnection";
    static IS_DEV_ENABLED_URL = this.BASE_URL + "api/isDevEnabled";
    static LOGIN_URL = this.BASE_URL + "api/login";
    static VERSION_URL = this.BASE_URL + "api/version";
    static CHECK_LOGIN_URL = this.BASE_URL + "api/checkLogin";
    static LOGOUT_URL = this.BASE_URL + "api/logout";
    static LOGS_HISTORY_URL = this.BASE_URL + "api/logsHistory";
    static CONSOLE_URL = this.BASE_URL + "ws/console";
    static STATS_URL = this.BASE_URL + "ws/serverStats";
    static CONFIG_URL = this.BASE_URL + "api/config";
    static IS_ALLOWED_URL = this.BASE_URL + "api/isAllowed";

    // OTHER
    static IWTCMS_VERSION: string;
    static PERMITS: any;
}
