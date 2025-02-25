import Constants from "./constants";
import { isDev } from "./devMode";
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
                window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
            }
        }
    } catch (error) {
        console.error("Error:", error);
        ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
        if (!(await isDev())) {
            window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
        }
    }

    return null;
}

export async function login(username: string, password: string) {
    const data = {
        username: username,
        password: password,
    };

    console.log(data);

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
            location.reload();
        } else if (response.status == 401) {
            console.error("Unauthorized");
            ToastSystem.addToQueue("Incorrect username or password", ToastSystem.ToastType.ERROR);
        } else {
            console.error("Error:", response.statusText);
            ToastSystem.addToQueue(`Error: ${response.statusText}`, ToastSystem.ToastType.ERROR);
        }
    } catch (error) {
        console.error("Error:", error);
        ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
        if (!(await isDev())) {
            window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
        }
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
            location.reload();
        } else {
            console.error("Error:", response.statusText);
            ToastSystem.addToQueue(`Error: ${response.statusText}`, ToastSystem.ToastType.ERROR);
        }
    } catch (error) {
        console.error("Error:", error);
        ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
        if (!(await isDev())) {
            window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
        }
    }
}

export async function getPermits() {
    try {
        const request = await fetch(Constants.PERMITS_BASE_URL + (await checkAuth()));
        if (request.status == 200) {
            return await request.json();
        } else if (request.status == 403) {
            return null;
        } else {
            console.error("Error:", request.statusText);
            ToastSystem.addToQueue(`Error: ${request.statusText}`, ToastSystem.ToastType.ERROR);
            if (!(await isDev())) {
                window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
            }
        }
    } catch (error) {
        console.error("Error:", error);
        ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
        if (!(await isDev())) {
            window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
        }
    }
}

export async function isForbidden(permit: string, element: HTMLElement | null = null): Promise<boolean> {
    const permits = await getPermits();
    console.log(permits);
    if (permits == null || permits[permit] == undefined || permits[permit] == false) {
        element?.classList.add("forbidden");
        console.log(element);
        return true;
    }

    return false;
}
