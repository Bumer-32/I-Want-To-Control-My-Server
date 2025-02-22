import { switchTab } from "../tabsController.js";
import { ToastSystem } from "../toastSystem.js";
import { Constants } from "../constants.js";

let isDev: boolean;

const menu = document.querySelector(
  "#settings-tab > .container > .dev-menu",
) as HTMLDivElement;

export function initDevFunctions() {
  isDEV().then((isDEV) => {
    if (!isDEV) {
      return;
    }

    settingsDevMenu();

    // ? default tab
    const defaultTab = localStorage.getItem("default-tab");
    if (defaultTab != null) {
      switchTab(defaultTab);
    }

    // ? remove notifications
    const removeNotificationsCheckbox = menu.querySelector(
      ".dev-remove-notifications",
    ) as HTMLInputElement;
    const isNotificationsRemoved =
      localStorage.getItem("remove-notifications") == "true";
    ToastSystem.enabled = !isNotificationsRemoved;
    removeNotificationsCheckbox.checked = isNotificationsRemoved;
  });
}

export async function isDEV(): Promise<boolean> {
  if (isDev != undefined) return isDev;

  console.log("Check DEV");

  try {
    const response = await fetch(Constants.IS_DEV_ENABLED_URL);

    if (!response.ok) {
      console.error(
        "Error fetching data:",
        response.status,
        response.statusText,
      );
      ToastSystem.showError(`Error fetching data: ${response.status}`);
      isDev = true;
      return true;
    }

    if ((await response.text()) == "false") {
      console.log("DEV mode disabled");
      isDev = false;
      return false;
    } else {
      console.log("DEV mode enabled");
      ToastSystem.showInfo("DEV mode enabled");
      isDev = true;
      return true;
    }
  } catch (error) {
    console.error(error);
    ToastSystem.showError(`Error: ${error}`);
    isDev = true;
    return true;
  }
}

function settingsDevMenu() {
  // ? remove disabled class if function called (dev mode enabled)
  menu.classList.remove("disabled");

  // ? default tab

  const defaultTabInput = menu.querySelector(
    ".dev-set-default-tab-input",
  ) as HTMLInputElement;
  const defaultTabButton = menu.querySelector(
    ".dev-set-default-tab-button",
  ) as HTMLButtonElement;

  defaultTabButton.onclick = () => {
    const tabName = defaultTabInput.value;
    if (tabName.length === 0) {
      return;
    }

    localStorage.setItem("default-tab", tabName);
    ToastSystem.showInfo("Set default tab to " + tabName);
  };

  // ? notifications

  const notificationInfoInput = menu.querySelector(
    ".dev-notification-info-input",
  ) as HTMLInputElement;
  const notificationInfoButton = menu.querySelector(
    ".dev-notification-info-button",
  ) as HTMLButtonElement;

  const notificationWarningInput = menu.querySelector(
    ".dev-notification-warning-input",
  ) as HTMLInputElement;
  const notificationWarningButton = menu.querySelector(
    ".dev-notification-warning-button",
  ) as HTMLButtonElement;

  const notificationErrorInput = menu.querySelector(
    ".dev-notification-error-input",
  ) as HTMLInputElement;
  const notificationErrorButton = menu.querySelector(
    ".dev-notification-error-button",
  ) as HTMLButtonElement;

  notificationInfoButton.onclick = () => {
    const message = notificationInfoInput.value;
    ToastSystem.showInfo(message.toString());
  };

  notificationWarningButton.onclick = () => {
    const message = notificationWarningInput.value;
    ToastSystem.showWarning(message.toString());
  };

  notificationErrorButton.onclick = () => {
    const message = notificationErrorInput.value;
    ToastSystem.showError(message.toString());
  };

  // ? remove notifications
  const removeNotificationsCheckbox = menu.querySelector(
    ".dev-remove-notifications",
  ) as HTMLInputElement;
  removeNotificationsCheckbox.onchange = () => {
    localStorage.setItem(
      "remove-notifications",
      removeNotificationsCheckbox.checked.toString(),
    );
  };
}
