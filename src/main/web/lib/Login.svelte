<script lang="ts">
    import icon from "../assets/icon_clearbg.png";
    import { checkAuth, login } from "../scripts/auth";
    import ToastSystem from "../scripts/toastSystem";
    import { onMount } from "svelte";

    let loginForm: HTMLFormElement;
    let loginDiv: HTMLDivElement;

    onMount(async () => {
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

<div class="absolute z-10 flex h-screen w-screen items-center justify-center" bind:this={loginDiv}>
    <div class="absolute z-[99] h-screen w-screen bg-[var(--login-background-color)] backdrop-blur-[20px]"></div>
    <div class="absolute z-[100] flex h-[250px] items-center rounded-[10px] bg-[var(--login-panel-background-color)] pr-[10px] pl-px">
        <h1 class="absolute top-0 left-[50%] transform-[translateX(-50%)]">Login</h1>
        <a class="block h-[110px] w-[110px]" href="https://modrinth.com/mod/i-want-to-control-my-server"
            ><img class="transition-[filer 0.3s ease, box-shadow 0.3s ease] block h-[110px] w-[110px]" src={icon} alt="IWTCMS logo" /></a
        >
        <form class="w-[250px]" bind:this={loginForm}>
            <label for="username">Username:</label>
            <input
                class="transition-[filer 0.3s ease, box-shadow 0.3s ease] mb-[10px] w-full rounded-[5px] border-none bg-[var(--login-input-background-color)] px-[5px] text-[var(--login-text-color)] focus:outline-none"
                type="text"
                id="username"
                name="username"
                required
            />

            <label for="password">Password:</label>
            <input
                class="transition-[filer 0.3s ease, box-shadow 0.3s ease] mb-[10px] w-full rounded-[5px] border-none bg-[var(--login-input-background-color)] px-[5px] text-[var(--login-text-color)] focus:outline-none"
                type="password"
                name="password"
                required
            />

            <button
                class="transition-[filer 0.3s ease, box-shadow 0.3s ease] h-[25px] w-full rounded-[5px] border-none bg-[var(--login-input-background-color)] text-[var(--login-text-color)]"
                type="submit"
                id="password">Login</button
            >
        </form>
    </div>
</div>

<style lang="scss">
    @use "../styles/hover-holo-effect";

    form > input,
    button,
    img {
        transition:
            filter 0.3s ease,
            box-shadow 0.3s ease;
        &:hover {
            @include hover-holo-effect.hover-holo-effect;
        }
    }
</style>
