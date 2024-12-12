import { Constants } from "./constants.js";
import { isDEV } from "./supply.js";

export async function initLogin() {
    try {
        const response = await fetch(Constants.IS_AUTH_ENABLED_URL)

        if (!response.ok) {
            console.error("Error fetching data:", response.status, response.statusText);
            return;
        }
    } catch (error) {
        console.log(error);
    }
}