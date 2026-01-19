<script lang="ts">
    import icon from "../assets/icon_clearbg.png";
    import { login } from "./auth";
    import { onMount } from "svelte";

    let loginForm: HTMLFormElement;

    onMount(async () => {
        loginForm.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(loginForm);
            const username = formData.get("username") as string;
            const password = formData.get("password") as string;
            await login(username, password);
        });
    });
</script>

<div class="absolute flex h-screen w-screen items-center justify-center">
    <div class="absolute z-[99] h-screen w-screen backdrop-blur-[20px]"></div>
    <div class="absolute z-[100] rounded-[10px] bg-[var(--login-panel-background-color)]">
        <div class="flex h-[250px] items-center pr-[10px] pl-px">
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
                    type="submit">Login</button
                >
            </form>
        </div>
    </div>
</div>

<style lang="scss">
    @use "../styles/hover-holo-effect";

    :is(form > input, button, img) {
        @include hover-holo-effect.hover-holo-effect;
    }
</style>
