<script lang="ts">
    import Header from "./lib/Header.svelte";
    import Footer from "./lib/Footer.svelte";
    import ConsoleTab from "./lib/tabs/ConsoleTab/ConsoleTab.svelte";
    import SettingsTab from "./lib/tabs/SettingsTab/SettingsTab.svelte";
    import PlayersTab from "./lib/tabs/PlayersTab/PlayersTab.svelte";
    import Login from "./lib/Login.svelte";
    import ToastSystem from "./scripts/toastSystem";
    import icon from "./assets/icon_clearbg.png";
    import "./styles/tailwind.css"

    window.addEventListener("load", async () => {
        // ? remove loading screen
        document.querySelector<HTMLDivElement>(".loading")!.style.display = "none";
    });
</script>

<Footer />

{#if window.location.pathname == "/BadConnection"}
    <main>
        <a href="/"><img src={icon} alt="icon" /></a>
        <div class="text">
            <p1>Oh no!</p1>
            <p1>It seems like you have a bad connection to the server.</p1>
            <p1>Try to refresh the page or check your internet connection.</p1>
            <p1>Good luck!</p1>
        </div>
    </main>

    <style lang="scss">
        main {
            justify-content: center;
            align-items: center;
            display: flex;
            height: 100vh;

            .text {
                margin-left: 20px;
                font-size: 20px;
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                font-family: "Nunito", sans-serif;
            }
        }
    </style>
{:else}
    <Header />

    <main>
        <div class="tabs">
            <ConsoleTab />
            <SettingsTab />
            <PlayersTab />
        </div>
    </main>

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
{/if}
