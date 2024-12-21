import { getVersion } from "./supply.js";

export class Constants {
    // URLS
    static BASE_URL: string;
    static PAGE_404_URL: string;
    static PAGE_BAD_CONNECTION_URL: string;
    static IS_AUTH_ENABLED_URL: string;
    static IS_DEV_ENABLED_URL: string;
    static LOGIN_URL: string;
    static VERSION_URL: string;
    static CHECK_LOGIN_URL: string;

    // OTHER
    static IWTCMS_VERSION: string;

    static async init() {
        console.log("Constants init");

        this.BASE_URL = document.baseURI;
        this.PAGE_404_URL = this.BASE_URL + "/404.html";
        this.PAGE_BAD_CONNECTION_URL = this.BASE_URL + "/BadConnection.html";
        this.IS_AUTH_ENABLED_URL = this.BASE_URL + "/api/isAuthEnabled";
        this.IS_DEV_ENABLED_URL = this.BASE_URL + "/api/isDevEnabled";
        this.LOGIN_URL = this.BASE_URL + "/api/login";
        this.VERSION_URL = this.BASE_URL + "/api/iwtcmsVersion";
        this.CHECK_LOGIN_URL = this.BASE_URL + "/api/checkLogin";
        this.IWTCMS_VERSION = await getVersion();
    }
}

