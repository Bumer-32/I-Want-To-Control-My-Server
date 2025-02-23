<script lang="ts">
    import icon from "../assets/icon_clearbg.png";
    import { checkAuth, login } from "../scripts/auth";
    import Constants from "../scripts/constants";
    import { isDev } from "../scripts/devMode";
    import ToastSystem from "../scripts/toastSystem";
    import { onMount } from "svelte";

    let loginForm: HTMLFormElement;
    let loginDiv: HTMLDivElement;

    async function checkIsLoginNeeded(): Promise<boolean> {
        try {
            const response = await fetch(Constants.IS_AUTH_ENABLED_URL);

            if (!response.ok) {
                console.error("Error fetching data:", response.status, response.statusText);
                ToastSystem.addToQueue(`Error fetching data: ${response.status}`, ToastSystem.ToastType.ERROR);
                if (!(await isDev())) {
                    window.location.href = Constants.PAGE_BAD_CONNECTION_URL;
                }
                return false;
            }

            if ((await response.text()) == "false") {
                loginDiv.classList.add("disabled");
            }
        } catch (error) {
            console.error(error);
            ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
            if (!(await isDev())) {
                window.location.assign(Constants.PAGE_BAD_CONNECTION_URL);
            }
        }
        return true;
    }

    onMount(async () => {
        const isLoginNeeded = await checkIsLoginNeeded();
        if (!isLoginNeeded) return;

        const auth = await checkAuth();
        if (auth != null) {
            ToastSystem.addToQueue(`Hello ${auth}`, ToastSystem.ToastType.INFO);
            loginDiv.classList.add("disabled");
        }

        loginForm.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(loginForm);
            const username = formData.get("username") as string;
            const password = formData.get("password") as string;
            await login(username, password);
        });
    });
</script>

<div class="login" bind:this={loginDiv}>
    <div class="background"></div>
    <div class="container">
        <h1>Login</h1>
        <a href="https://modrinth.com/mod/i-want-to-control-my-server"><img src={icon} alt="IWTCMS logo" /></a>
        <form id="loginForm" bind:this={loginForm}>
            <label for="username" id="username">Username:</label>
            <input type="text" name="username" required />
            <br />
            <label for="password" id="password">Password:</label>
            <input type="password" name="password" required />
            <br />
            <button type="submit">Login</button>
        </form>
    </div>
</div>

<style lang="scss">
    .login {
        position: absolute;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100vw;
        height: 100vh;

        z-index: 10;

        .background {
            position: absolute;
            width: 100vw;
            height: 100vh;
            z-index: 99;
            background: var(--login-background-color);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            display: flex;
            align-items: center;
            justify-content: center;
            position: absolute;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
            z-index: 100;
            width: 400px;
            height: 250px;
            background-color: var(--login-panel-background-color);
            border-radius: 10px;
            padding-right: 10px;

            h1 {
                position: absolute;
                top: 0;
                left: 50%;
                transform: translateX(-50%);
            }

            input {
                border: none;
                border-radius: 5px;

                &:focus {
                    outline: none;
                }
            }

            label {
                display: inline-block;
                width: 90px;
            }

            br {
                margin-bottom: 10px;
            }

            button {
                width: 100%;
                height: 20px;
                border: none;
                border-radius: 5px;
            }

            input,
            button,
            img {
                // hover holo effect
                transition:
                    filter 0.3s ease,
                    box-shadow 0.3s ease;
                &:hover {
                    filter: drop-shadow(0 0 10px var(--holo-effect-color));
                }
            }
        }
    }
</style>
