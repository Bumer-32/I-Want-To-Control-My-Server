import { checkAuth, getPermits } from "./auth";
import { getVersion } from "./supply";

export default class Constants {
    // URLS
    static BASE_URL = document.baseURI;
    static PAGE_BAD_CONNECTION_URL = this.BASE_URL + "BadConnection.html";
    static IS_AUTH_ENABLED_URL = this.BASE_URL + "api/isAuthEnabled";
    static IS_DEV_ENABLED_URL = this.BASE_URL + "api/isDevEnabled";
    static LOGIN_URL = this.BASE_URL + "api/login";
    static VERSION_URL = this.BASE_URL + "api/version";
    static CHECK_LOGIN_URL = this.BASE_URL + "api/checkLogin";
    static LOGOUT_URL = this.BASE_URL + "api/logout";
    static LOGS_HISTORY_URL = this.BASE_URL + "api/logsHistory";
    static CONSOLE_URL = this.BASE_URL + "ws/console";
    static STATS_URL = this.BASE_URL + "ws/serverStats";
    static PERMITS_BASE_URL = this.BASE_URL + "api/permits/";

    // OTHER
    static IWTCMS_VERSION: string;
    static PERMITS: any;
}
