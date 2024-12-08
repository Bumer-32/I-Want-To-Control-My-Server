import { initTabsController, switchTab } from "./tabsController.js";

// remove js message
(document.querySelector(".container > .enable-js-message") as HTMLDivElement).style.display = "none";

initTabsController();