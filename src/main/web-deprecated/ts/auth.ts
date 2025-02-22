import { Constants } from "./constants.js";
import { isDEV } from "./dev-mode/dev-mode.js";
import { checkAuth } from "./supply.js";
import { ToastSystem } from "./toastSystem.js";

export async function initLogin() {
    checkIsLoginNeeded();
    const auth = await checkAuth();
    if (auth != null) {
        ToastSystem.showInfo(`Hello ${auth}`);
        document.querySelector(".login")?.classList.add("disabled");
    }
    handleLoginForm();
    handleLogout();
}

async function checkIsLoginNeeded() {
    try {
        const response = await fetch(Constants.IS_AUTH_ENABLED_URL);

        if (!response.ok) {
            console.error(
                "Error fetching data:",
                response.status,
                response.statusText,
            );
            ToastSystem.showError(`Error fetching data: ${response.status}`);
            if (!(await isDEV())) {
                window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
            }
            return;
        }

        if ((await response.text()) == "false") {
            document.querySelector(".login")?.classList.add("disabled");
        }
    } catch (error) {
        console.error(error);
        ToastSystem.showError(`Error: ${error}`);
        if (!(await isDEV())) {
            window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
        }
    }
}

async function handleLoginForm() {
    (document.getElementById("loginForm") as HTMLFormElement).addEventListener(
        "submit",
        async function (event: Event) {
            event.preventDefault();

            const formData = new FormData(this);

            var object: { [key: string]: any } = {};
            formData.forEach((value, key) => (object[key] = value));
            var json = JSON.stringify(object);

            try {
                const response = await fetch(Constants.LOGIN_URL, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    // credentials: "include",
                    body: json,
                });

                if (response.ok) {
                    console.log("Success");
                    ToastSystem.showInfo("Success");
                    location.reload();
                } else if (response.status == 401) {
                    console.error("Unauthorized");
                    ToastSystem.showError("Incorrect username or password");
                } else {
                    console.error("Error:", response.statusText);
                    ToastSystem.showError(`Error: ${response.statusText}`);
                }
            } catch (error) {
                console.error("Error:", error);
                ToastSystem.showError(`Error: ${error}`);
                if (!(await isDEV())) {
                    window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
                }
            }
        },
    );
}

async function handleLogout() {
    (
        document.getElementById("logout-button") as HTMLLIElement
    ).addEventListener("click", async function () {
        try {
            const response = await fetch(Constants.LOGOUT_URL, {
                method: "POST",
            });

            if (response.ok) {
                console.log("Success");
                ToastSystem.showInfo("Success");
                location.reload();
            } else {
                console.error("Error:", response.statusText);
                ToastSystem.showError(`Error: ${response.statusText}`);
            }
        } catch (error) {
            console.error("Error:", error);
            ToastSystem.showError(`Error: ${error}`);
            if (!(await isDEV())) {
                window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
            }
        }
    });
}
