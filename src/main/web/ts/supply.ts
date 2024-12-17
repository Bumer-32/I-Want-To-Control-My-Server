import { Constants } from "./constants.js";
import { ToastSystem } from "./toastSystem.js";

let isDev: boolean;

export function getCookies(): Map<string, string> {
    const cookiesList = document.cookie.split(';');
    const cookies = new Map<string, string>();
    
    cookiesList.forEach(cookie => {
        const [key, value] = cookie.split('=');
        cookies.set(key, value);
    });

    return cookies;
}

export function editCookie(name: string, value: string, lifetime: number): void {
    const expirationDate = new Date();
    expirationDate.setTime(expirationDate.getTime() + (lifetime * 1000));

    const expires = `expires=${expirationDate.toUTCString()}`;
    document.cookie = `${name}=${value}; ${expires}; path=/`;
}

export async function isDEV(): Promise<boolean> {
    if (isDev != undefined) return isDev;

    console.log("Check DEV");

    try {
        const request = await fetch(`${document.baseURI}dev.txt`);
        if (request.status == 200) {
            console.log("DEV mode disabled");
            isDev = false;
            return false;
        } else {
            console.log("DEV mode enabled");
            ToastSystem.showInfo("DEV mode enabled")
            isDev = true;
            return true;
        }
    } catch (e) {
        console.log("DEV mode enabled");
        ToastSystem.showInfo("DEV mode enabled")
        isDev = true;
        return true;
    }
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