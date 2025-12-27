import Constants from "./constants";
import ToastSystem from "./toastSystem";
import { isDev } from "./devMode";

export async function getVersion(): Promise<string> {
    try {
        const request = await fetch(Constants.VERSION_URL);
        return request.text();
    } catch (e) {
        return "";
    }
}

export function catchError(error: any) {
    console.error(error);
    ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
    if (!isDev()) {
        window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
    }
}
