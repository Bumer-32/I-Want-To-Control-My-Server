import { Constants } from "./constants.js";
import { isDEV } from "./dev-mode/dev-mode.js";
import { ToastSystem } from "./toastSystem.js";

export function getCookies(): Map<string, string> {
    const cookiesList = document.cookie.split(";");
    const cookies = new Map<string, string>();

    cookiesList.forEach((cookie) => {
        const [key, value] = cookie.split("=");
        cookies.set(key, value);
    });

    return cookies;
}

export function editCookie(name: string, value: string, lifetime: number): void {
    const expirationDate = new Date();
    expirationDate.setTime(expirationDate.getTime() + lifetime * 1000);

    const expires = `expires=${expirationDate.toUTCString()}`;
    document.cookie = `${name}=${value}; ${expires}; path=/`;
}

export async function getVersion(): Promise<string> {
    try {
        const request = await fetch(Constants.VERSION_URL);
        const version = await request.text();
        return version;
    } catch (e) {
        return "";
    }
}

export async function checkAuth(): Promise<string | null> {
    try {
        const request = await fetch(Constants.CHECK_LOGIN_URL);
        if (request.status == 200) {
            return await request.text();
        } else if (request.status == 401) {
            return null;
        } else {
            console.error("Error:", request.statusText);
            ToastSystem.showError(`Error: ${request.statusText}`);
            if (!(await isDEV())) {
                window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
            }
        }
    } catch (error) {
        console.error("Error:", error);
        ToastSystem.showError(`Error: ${error}`);
        if (!(await isDEV())) {
            window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
        }
    }

    return null;
}

export async function getPermits() {
    try {
        const request = await fetch(Constants.PERMITS_URL);
        if (request.status == 200) {
            return await request.json();
        } else if (request.status == 403) {
            return null;
        } else {
            console.error("Error:", request.statusText);
            ToastSystem.showError(`Error: ${request.statusText}`);
            if (!(await isDEV())) {
                window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
            }
        }
    } catch (error) {
        console.error("Error:", error);
        ToastSystem.showError(`Error: ${error}`);
        if (!(await isDEV())) {
            window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
        }
    }
}

export function isForbidden(permit: string, element: HTMLElement | null = null): boolean {
    if (Constants.PERMITS == null || Constants.PERMITS[permit] == undefined || Constants.PERMITS[permit] == false) {
        element?.classList.add("forbidden");
        return true;
    }

    return false;
}
