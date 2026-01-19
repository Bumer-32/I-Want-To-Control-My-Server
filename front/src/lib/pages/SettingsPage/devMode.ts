import Constants from "../../constants";
import ToastSystem from "../../toastSystem";

let isDEV: boolean; // cache

export async function isDev(): Promise<boolean> {
    if (isDEV != undefined) return isDEV;

    try {
        const request = await fetch(Constants.IS_DEV_ENABLED_URL);

        if (!request.ok) {
            console.error("Error fetching data:", request.status, request.statusText);
            ToastSystem.addToQueue(`Error fetching data: ${request.status}`, ToastSystem.ToastType.ERROR);
            isDEV = false;
            return false;
        }

        if ((await request.text()) == "false") {
            console.log("DEV mode disabled");
            isDEV = false;
            return false;
        } else {
            console.log("DEV mode enabled");
            ToastSystem.addToQueue("DEV mode enabled", ToastSystem.ToastType.INFO);
            isDEV = true;
            return true;
        }
    } catch (error) {
        console.error("Error:", error);
        ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
        isDEV = false;
        return false;
    }
}
