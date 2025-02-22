import { initTabsController } from "./tabsController.js";
import { initLogin } from "./auth.js";
import { Constants } from "./constants.js";
import consoleInit from "./tabs/console.js";
import { initDevFunctions } from "./dev-mode/dev-mode.js";
import settingsInit from "./tabs/settings.js";

async function loadGithubStars() {
    try {
        const request = fetch("https://corsproxy.io/?url=https://github.com/Bumer-32/I-Want-To-Control-My-Server");
        const html = (await request).text();

        const parser = new DOMParser();
        const doc = parser.parseFromString(await html, "text/html");

        const starsSpan = doc.querySelector('a[href$="/stargazers"] span') as HTMLSpanElement;

        (document.querySelector(".footer > .github > .stars") as HTMLSpanElement).innerHTML = starsSpan.innerHTML.trim();
    } catch (error) {
        console.error("Failed to load github stars");
        console.error(error);
    }
}

async function main() {
    // ? color mode switch
    const color_mode_switch = document.querySelector(".header > .supply > .color-mode > .switch input") as HTMLInputElement;
    color_mode_switch.onchange = () => {
        document.body.classList.toggle("light-mode-impl");
        localStorage.setItem("color-mode", color_mode_switch.checked ? "light" : "dark");
    };
    if (localStorage.getItem("color-mode") == "light") {
        color_mode_switch.checked = true;
        document.body.classList.add("light-mode-impl");
    }

    await Constants.init(); // ! IMPORTANT TO LOAD FIRST

    // ? place iwtcms version at footer
    (document.querySelector(".footer > .iwtcms-label") as HTMLLabelElement).innerHTML = `IWTCMS ${Constants.IWTCMS_VERSION}`;

    // ? closing menu
    (document.querySelector(".header > .menu > .menu-button > input") as HTMLInputElement).checked = false;

    initDevFunctions(); // earlier then other functions because dev functions can influence to it

    loadGithubStars();

    initTabsController();
    initLogin();

    consoleInit();
    settingsInit();

    // ? remove loading screen
    (document.querySelector(".loading") as HTMLDivElement).style.display = "none";
}

main();
