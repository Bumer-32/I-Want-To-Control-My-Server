import { isDEV } from "../supply.js";
import { switchTab } from "../tabsController.js";
import { ToastSystem } from "../toastSystem.js";

export function initDevFunctions() {
    isDEV().then(isDEV => {
        if (!isDEV) {
            return;
        }
    
        settingsDevMenu();
    
        // ? default tab
        const defaultTab = localStorage.getItem("default-tab");
        if (defaultTab != null) {
            switchTab(defaultTab);
        }
    });
}

function settingsDevMenu() {
    const menu = document.querySelector("#settings-tab > .container > .dev-menu") as HTMLDivElement;

    const defaultTabInput = menu.querySelector("#dev-set-default-tab-input") as HTMLInputElement;
    const defaultTabButton = menu.querySelector("#dev-set-default-tab-button") as HTMLButtonElement;
    
    menu.classList.remove("disabled");

    defaultTabButton.onclick = () => {
        const tabName = defaultTabInput.value;
        if (tabName.length === 0) {
            return;
        }

        localStorage.setItem("default-tab", tabName);
        ToastSystem.showInfo("Set default tab to " + tabName);
    }

    const notificationInfoInput = menu.querySelector("#dev-notification-info-input") as HTMLInputElement;
    const notificationInfoButton = menu.querySelector("#dev-notification-info-button") as HTMLButtonElement;

    const notificationWarningInput = menu.querySelector("#dev-notification-warning-input") as HTMLInputElement;
    const notificationWarningButton = menu.querySelector("#dev-notification-warning-button") as HTMLButtonElement;

    const notificationErrorInput = menu.querySelector("#dev-notification-error-input") as HTMLInputElement
    const notificationErrorButton = menu.querySelector("#dev-notification-error-button") as HTMLButtonElement;

    notificationInfoButton.onclick = () => {
        const message = notificationInfoInput.value;
        ToastSystem.showInfo(message.toString());
    }

    notificationWarningButton.onclick = () => {
        const message = notificationWarningInput.value;
        ToastSystem.showWarning(message.toString());
    }

    notificationErrorButton.onclick = () => {
        const message = notificationErrorInput.value;
        ToastSystem.showError(message.toString());
    }
}