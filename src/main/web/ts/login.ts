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




    (document.getElementById("loginForm") as HTMLFormElement).addEventListener("submit", async function(event: Event) {
        event.preventDefault();

        const username = (document.getElementById("username") as HTMLInputElement).value;
        const password = (document.getElementById("password") as HTMLInputElement).value;

        const data = { username, password };

        try {
            console.log(JSON.stringify(data));
            const response = await fetch(Constants.LOGIN_URL, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                const result = await response.json();
                console.log("Success:", result);
            } else {
                console.error("Error:", response.statusText);
            }
        } catch (error) {
            console.error("Error:", error);
        }
    });
}