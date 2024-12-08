import { initTabsController, switchTab } from "./tabsController.js";
import { editCookie, getCookies } from "./supply.js";

// remove js message
(document.querySelector(".container > .enable-js-message") as HTMLDivElement).style.display = "none";

// color mode switch
const color_mode_switch = document.querySelector(".header > .supply > .color-mode > .switch input") as HTMLInputElement
color_mode_switch.onchange = () => {
    document.body.classList.toggle("light-mode-impl");
    editCookie("color-mode", color_mode_switch.checked ? "light" : "dark", 31536000); // year
};

if (getCookies().get("color-mode") == "light") {
    color_mode_switch.checked = true;
    document.body.classList.add("light-mode-impl");
}

initTabsController();