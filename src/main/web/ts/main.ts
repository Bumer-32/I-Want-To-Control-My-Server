import { initTabsController } from "./tabsController.js";
import { initLogin } from "./login.js";
import { Constants } from "./constants.js";

async function loadGithubStars() {
    const request = fetch("https://api.github.com/repos/Bumer-32/I-Want-To-Control-My-Server");
    request.then(response => response.text())
    .then(text => {
        const stars: number = JSON.parse(text).stargazers_count;
        (document.querySelector(".footer > .github > .stars") as HTMLSpanElement).innerHTML = stars.toString();
    });
    
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
        window.getSelection()?.removeAllRanges();
    }

    await Constants.init(); // ! IMPORTANT TO LOAD FIRST

    // ? place iwtcms version at footer
    (document.querySelector(".footer > .iwtcms-label") as HTMLLabelElement).innerHTML = `IWTCMS ${Constants.IWTCMS_VERSION}`;

    loadGithubStars()

    initTabsController();
    initLogin();

    // ? remove loading screen
    (document.querySelector(".loading") as HTMLDivElement).classList.add("disabled");
}

main();