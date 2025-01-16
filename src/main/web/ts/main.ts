import { initTabsController } from "./tabsController.js";
import { initLogin } from "./auth.js";
import { Constants } from "./constants.js";
import { isDEV } from "./supply.js";
import consoleInit from "./tabs/console.js";
import { initDevFunctions } from "./dev-mode/dev-mode.js";
import settingsInit from "./tabs/settings.js";

async function loadGithubStars() {
    try {
        const request = fetch("https://api.github.com/repos/Bumer-32/I-Want-To-Control-My-Server");
        request.then(response => response.text())
        .then(text => {
            const stars: number = JSON.parse(text).stargazers_count;
            if (stars !== undefined) { 
                (document.querySelector(".footer > .github > .stars") as HTMLSpanElement).innerHTML = stars.toString();
            }
        });
    } catch (error) {
        console.error("Failed to load github stars");
        console.error(error);
    }
}

async function main() {
    // ? color mode switch
    const color_mode_switch = document.querySelector(".header > .supply > .color-mode > .switch input") as HTMLInputElement
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

    loadGithubStars()

    initTabsController();
    initLogin();

    isDEV(); // for caching

    consoleInit();
    settingsInit();

    initDevFunctions();

    // ? remove loading screen
    (document.querySelector(".loading") as HTMLDivElement).style.display = "none";
}

main();
