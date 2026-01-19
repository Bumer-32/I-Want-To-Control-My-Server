<script lang="ts">
    import Header from "./lib/Header.svelte";
    import Footer from "./lib/Footer.svelte";
    import ConsolePage from "./lib/pages/ConsolePage/ConsolePage.svelte";
    import SettingsPage from "./lib/pages/SettingsPage/SettingsPage.svelte";
    import PlayersPage from "./lib/pages/PlayersPage/PlayersPage.svelte";
    import UsersPage from "./lib/pages/UsersPage/UsersPage.svelte";
    import Login from "./lib/Login.svelte";
    import Constants from "./lib/constants";
    import ToastSystem from "./lib/toastSystem";
    import icon from "./assets/icon_clearbg.png";
    import "./styles/tailwind.css";
    import "./styles/style.scss";
    import type { Component } from "svelte";
    import { checkAuth } from "./lib/auth";

    const pages: Record<string, Component | null> = {
        "/": ConsolePage,
        "/console": ConsolePage,
        "/settings": SettingsPage,
        "/players": PlayersPage,
        "/users": UsersPage,
    };

    if (window.location.href !== Constants.PAGE_BAD_CONNECTION_URL) {
        checkAuth().then((a) => {
            if (a === null && window.location.pathname !== "/login") {
                window.location.assign("/login");
            }
        });
    }

    let currentPage: Component | null;

    if (pages[window.location.pathname] != null) {
        currentPage = pages[window.location.pathname];
    } else if (window.location.pathname !== "/login") {
        // window.location.assign("/");
    }

    window.addEventListener("load", async () => {
        // ? remove loading screen
        document.querySelector<HTMLDivElement>(".loading")!.style.display = "none";
        console.log(window.location.pathname);
    });
</script>

<Footer />
<div class="toast-notifications" bind:this={ToastSystem.notification}></div>

{#if window.location.href === Constants.PAGE_BAD_CONNECTION_URL}
    <main class="h-screen content-center">
        <div class="flex justify-center">
            <a href="/"><img src={icon} alt="icon" /></a>
            <div class="ml-[20px] flex flex-col content-center justify-center font-['Nunito'] text-[20px]">
                <h1>Oh no!</h1>
                <h1>It seems like you have a bad connection to the server.</h1>
                <h1>Try to refresh the page or check your internet connection.</h1>
                <h1>Good luck!</h1>
            </div>
        </div>
    </main>
{:else if window.location.pathname === "/login"}
    <Header />
    <Login />

    <main class="flex h-full items-center justify-center">
        <img class="opacity-25" alt="MEOW" src="https://cataas.com/cat" />
    </main>
{:else}
    <Header />

    <main class="align-center absolute flex h-screen w-screen justify-center">
        <div class="h-full w-full">
            <svelte:component this={currentPage} />
        </div>
    </main>

    <style lang="scss">
        @use "styles/variables";
        @use "styles/disabled";

        main {
            height: variables.$container-height;
            top: variables.$header-height;
            .page-container {
                display: flex;
                align-items: center;
                justify-content: center;
                transition: color 0.3s ease;
                top: 0;
                height: variables.$container-height;
            }
        }
    </style>
{/if}
