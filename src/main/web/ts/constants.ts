import { isDEV } from "./supply.js";

export class Constants {
    static BASE_URL: string;
    static IS_AUTH_ENABLED_URL: string;

    static async init() {
        console.log("Constants init");
        const enabled = await isDEV();
        this.BASE_URL = enabled ? "https://127.0.0.1:25566" : document.baseURI;
        this.IS_AUTH_ENABLED_URL = this.BASE_URL + "/api/isAuthEnabled";
    }
}

