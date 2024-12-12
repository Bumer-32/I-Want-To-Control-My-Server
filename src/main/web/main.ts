import { initTabsController, switchTab } from "./ts/tabsController.js";
import { editCookie, getCookies } from "./ts/supply.js";
import { initLogin } from "./ts/login.js";
import { Constants } from "./ts/constants.js";
import { ToastSystem } from "./ts/ToastSystem.js";

// remove js message
(document.querySelector(".enable-js-message") as HTMLDivElement).style.display = "none";

// color mode switch
const color_mode_switch = document.querySelector(".header > .supply > .color-mode > .switch input") as HTMLInputElement
color_mode_switch.onchange = () => {
    document.body.classList.toggle("light-mode-impl");
    editCookie("color-mode", color_mode_switch.checked ? "light" : "dark", 31536000); // year
};

if (getCookies().get("color-mode") == "light") {
    color_mode_switch.checked = true;
    document.body.classList.add("light-mode-impl");
    window.getSelection()?.removeAllRanges();
}

async function main() {
    initTabsController();

    await Constants.init();
    // ! NEEDS CONSTANTS, SO LAUNCH ONLY AFTER CONSTANTS INIT
    initLogin();
}

main();

setTimeout(() => {
    console.log("notification!");
    ToastSystem.addToQueue("Hello world!1", "warning");
    ToastSystem.addToQueue("Hello world!2", "error");
    ToastSystem.addToQueue("Hello world!3", "info");
    ToastSystem.addToQueue("Hello world!4", "warning");
    ToastSystem.addToQueue("Hello world!5", "info");
}, 5000);

