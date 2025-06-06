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

<div class="absolute flex items-center justify-center w-screen h-screen z-10" bind:this={loginDiv}>
    <div class="absolute w-screen h-screen z-[99] bg-[var(--login-background-color)] backdrop-blur-[20px]"></div>
    <div class="flex z-[100] items-center absolute h-[250px] bg-[var(--login-panel-background-color)] rounded-[10px] pr-[10px] pl-px">
        <h1 class="absolute top-0 left-[50%] transform-[translateX(-50%)]">Login</h1>
        <a class="h-[110px] w-[110px] block" href="https://modrinth.com/mod/i-want-to-control-my-server"><img class="block h-[110px] w-[110px] transition-[filer 0.3s ease, box-shadow 0.3s ease]" src={icon} alt="IWTCMS logo" /></a>
        <form class="w-[250px]" bind:this={loginForm}>
            <label for="username">Username:</label>
            <input class="bg-[var(--login-input-background-color)] border-none rounded-[5px] focus:outline-none w-full text-[var(--login-text-color)] px-[5px] transition-[filer 0.3s ease, box-shadow 0.3s ease] mb-[10px]" type="text" id="username" name="username" required />

            <label for="password">Password:</label>
            <input class="bg-[var(--login-input-background-color)] border-none rounded-[5px] focus:outline-none w-full text-[var(--login-text-color)] px-[5px] transition-[filer 0.3s ease, box-shadow 0.3s ease] mb-[10px]" type="password" name="password" required />

            <button class="w-full h-[25px] bg-[var(--login-input-background-color)] border-none rounded-[5px] text-[var(--login-text-color)] transition-[filer 0.3s ease, box-shadow 0.3s ease]" type="submit" id="password">Login</button>
        </form>
    </div>
</div>

<style lang="scss">
    @use "../styles/hover-holo-effect";

    form > input,
    button,
    img {
        transition: filter 0.3s ease, box-shadow 0.3s ease;
        &:hover {
            @include hover-holo-effect.hover-holo-effect;
        }
    }
</style>
