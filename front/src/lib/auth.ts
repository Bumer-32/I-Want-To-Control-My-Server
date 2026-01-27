import Constants from "./constants";
import { isDev } from "./pages/SettingsPage/devMode";
import ToastSystem from "./toastSystem";

export async function checkAuth(): Promise<string | null> {
    try {
        const request = await fetch(Constants.CHECK_LOGIN_URL);
        if (request.status == 200) {
            return await request.text();
        } else if (request.status == 401) {
            return null;
        } else {
            console.error("Error:", request.statusText);
            ToastSystem.addToQueue(`Error: ${request.statusText}`, ToastSystem.ToastType.ERROR);
            if (!(await isDev())) {
                window.location.assign("#/badConnection");
            }
        }
    } catch (error) {
        ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
    }

    return null;
}

export async function login(username: string, password: string) {
    const data = {
        username: username,
        password: password,
    };

    try {
        const response = await fetch(Constants.LOGIN_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        });

        if (response.ok) {
            console.log("Success");
            ToastSystem.addToQueue("Success", ToastSystem.ToastType.INFO);
            window.location.assign("#/");
        } else if (response.status == 401) {
            console.error("Unauthorized");
            ToastSystem.addToQueue("Incorrect username or password", ToastSystem.ToastType.ERROR);
        } else {
            ToastSystem.addToQueue(`Error: ${response.statusText}`, ToastSystem.ToastType.ERROR);
        }
    } catch (error) {
        ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
    }
}

export async function logout() {
    try {
        const response = await fetch(Constants.LOGOUT_URL, {
            method: "POST",
        });

        if (response.ok) {
            console.log("Success");
            ToastSystem.addToQueue("Success", ToastSystem.ToastType.INFO);
            window.location.assign("#/login");
        } else {
            console.error("Error:", response.statusText);
            ToastSystem.addToQueue(`Error: ${response.statusText}`, ToastSystem.ToastType.ERROR);
        }
    } catch (error) {
        ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
    }
}

export async function isAllowed(permission: string): Promise<boolean> {
    try {
        const response = await fetch(Constants.IS_ALLOWED_URL + "/" + permission);

        if (response.ok) {
            return true;
        }
    } catch (error) {
        ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
        return false;
    }
    return false;
}
