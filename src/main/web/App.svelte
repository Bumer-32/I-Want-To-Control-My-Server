<script lang="ts">
    import Header from "./lib/Header.svelte";
    import Footer from "./lib/Footer.svelte";
    import ConsoleTab from "./lib/tabs/ConsoleTab.svelte";
    import SettingsTab from "./lib/tabs/SettingsTab.svelte";
    import PlayersTab from "./lib/tabs/PlayersTab.svelte";
    import Login from "./lib/Login.svelte";
    import ToastSystem from "./scripts/toastSystem";

    window.addEventListener("load", async () => {
        // ? remove loading screen
        document.querySelector<HTMLDivElement>(".loading")!.style.display = "none";

        console.log("Loaded");
    });
</script>

<Header />

<main>
    <div class="tabs">
        <ConsoleTab />
        <SettingsTab />
        <PlayersTab />
    </div>
</main>

<Footer />

<Login />

<div class="toast-notifications" bind:this={ToastSystem.notification}></div>

<style lang="scss">
    @use "./styles/variables";

    main {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100vw;
        height: variables.$container-height;
        position: absolute;
        top: variables.$header-height;

        .tabs {
            width: 100%;
            height: 100%;

            .tab {
                width: 100%;
                height: 100%;

                .container {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    color: var(--main-text-color);
                    transition: color 0.3s ease;
                    top: 0;
                }
            }
        }
    }
</style>
