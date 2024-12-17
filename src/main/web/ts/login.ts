import { Constants } from "./constants.js";
import { isDEV } from "./supply.js";
import { ToastSystem } from "./toastSystem.js";

export async function initLogin() {
    try {
        const response = await fetch(Constants.IS_AUTH_ENABLED_URL)

        if (!response.ok) {
            console.error("Error fetching data:", response.status, response.statusText);
            ToastSystem.showError(`Error fetching data: ${response.status}`);
            if (!await isDEV()) {
                window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
            }
            return;
        }

        if (await response.text() == "false") {
            document.querySelector(".login")?.classList.add("disabled");
        }
    } catch (error) {
        console.log(error);
        ToastSystem.showError("Error fetching data");
        if (!await isDEV()) {
            window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
        }
    }
}