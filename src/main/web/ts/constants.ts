import { checkAuth, getPermits, getVersion } from "./supply.js";
export class Constants {
    // URLS
    static BASE_URL = document.baseURI;
    static PAGE_404_URL = this.BASE_URL + "/404.html";
    static PAGE_BAD_CONNECTION_URL = this.BASE_URL + "/BadConnection.html";
    static IS_AUTH_ENABLED_URL = this.BASE_URL + "/api/isAuthEnabled";
    static IS_DEV_ENABLED_URL = this.BASE_URL + "/api/isDevEnabled";
    static LOGIN_URL = this.BASE_URL + "/api/login";
    static VERSION_URL = this.BASE_URL + "/api/iwtcmsVersion";
    static CHECK_LOGIN_URL = this.BASE_URL + "/api/checkLogin";
    static LOGOUT_URL = this.BASE_URL + "/api/logout";
    static LOGS_HISTORY_URL = this.BASE_URL + "/api/logsHistory";
    static CONSOLE_URL = this.BASE_URL + "/api/console";

    static PERMITS_URL: string;

    // OTHER
    static IWTCMS_VERSION: string;
    static PERMITS: any;

    static async init() {
        console.log("Constants init");

        this.PERMITS_URL = this.BASE_URL + "/api/permits/" + await checkAuth();

        this.IWTCMS_VERSION = await getVersion();
        this.PERMITS = await getPermits();
    }
}

