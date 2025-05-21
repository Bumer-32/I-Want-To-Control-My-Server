<script lang="ts">
    import Header from "./lib/Header.svelte";
    import Footer from "./lib/Footer.svelte";
    import ConsoleTab from "./lib/tabs/ConsoleTab/ConsoleTab.svelte";
    import SettingsTab from "./lib/tabs/SettingsTab/SettingsTab.svelte";
    import PlayersTab from "./lib/tabs/PlayersTab/PlayersTab.svelte";
    import Login from "./lib/Login.svelte";
    import ToastSystem from "./scripts/toastSystem";
    import icon from "./assets/icon_clearbg.png";
    import "./styles/tailwind.css";
    import "./styles/style.scss";
    import UsersTab from "./lib/tabs/UsersTab/UsersTab.svelte";

    window.addEventListener("load", async () => {
        // ? remove loading screen
        document.querySelector<HTMLDivElement>(".loading")!.style.display = "none";
    });
</script>

<Footer />

{#if window.location.pathname == "/BadConnection"}
    <main class="h-screen content-center">
        <div class="flex justify-center">
            <a href="/"><img src={icon} alt="icon" /></a>
            <div class="ml-[20px] flex flex-col content-center justify-center font-['Nunito'] text-[20px]">
                <p1>Oh no!</p1>
                <p1>It seems like you have a bad connection to the server.</p1>
                <p1>Try to refresh the page or check your internet connection.</p1>
                <p1>Good luck!</p1>
            </div>
        </div>
    </main>
{:else}
    <Header />

    <main class="align-center absolute flex w-screen justify-center">
        <div class="tabs h-full w-full">
            <ConsoleTab />
            <!-- <SettingsTab /> -->
            <PlayersTab />
            <UsersTab />
        </div>
    </main>

    <Login />

    <div class="toast-notifications" bind:this={ToastSystem.notification}></div>

    <style lang="scss">
        @use "./styles/variables";
        @use "./styles/disabled";

        main {
            height: variables.$container-height;
            top: variables.$header-height;

            .tabs > div {
                width: 100%;
                height: 100%;

                .container {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    color: var(--main-text-color);
                    transition: color 0.3s ease;
                    top: 0;
                    height: variables.$container-height;
                }
            }
        }
    </style>
{/if}

<style lang="scss">
</style>
