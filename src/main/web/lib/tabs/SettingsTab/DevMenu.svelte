<script lang="ts">
    import { onMount } from "svelte";
    import { isDev } from "../../../scripts/devMode";
    import { switchTab } from "../../../scripts/tabsController";
    import ToastSystem from "../../../scripts/toastSystem";

    let devMenu: HTMLDivElement;
    let defaultTabInput: HTMLInputElement;
    let notificationInfoInput: HTMLInputElement;
    let notificationWarningInput: HTMLInputElement;
    let notificationErrorInput: HTMLInputElement;
    let removeNotificationsCheckbox: HTMLInputElement;

    onMount(() => {
        isDev().then((isDEV) => {
            if (!isDEV) {
                return;
            }

            devMenu.classList.remove("disabled");

            // ? default tab
            const defaultTab = localStorage.getItem("default-tab");
            if (defaultTab != null) {
                switchTab(defaultTab);
            }

            // ? remove notifications
            const isNotificationsRemoved = localStorage.getItem("remove-notifications") == "true";
            ToastSystem.enabled = !isNotificationsRemoved;
            removeNotificationsCheckbox.checked = isNotificationsRemoved;
        });
    });
</script>

<div class="dev-menu disabled" bind:this={devMenu}>
    <input type="checkbox" class="open-dev-menu" />

    <h3>Developer mode settings</h3>
    <hr />
    <span>Default tab</span>
    <input type="text" name="default tab (id)" class="dev-set-default-tab-input" bind:this={defaultTabInput} />
    <button
        class="dev-set-default-tab-button"
        onclick={() => {
            const tabName = defaultTabInput.value;
            if (tabName.length === 0) {
                return;
            }

            localStorage.setItem("default-tab", tabName);
            ToastSystem.addToQueue("Set default tab to " + tabName, ToastSystem.ToastType.INFO);
            console.log("Set default tab to " + tabName);
        }}
        >Set default tab
    </button>

    <hr />

    <span>Notification testing</span>
    <input type="text" name="notification text" value="This is a info notification" bind:this={notificationInfoInput} />
    <button onclick={() => ToastSystem.addToQueue(notificationInfoInput!.value.toString(), ToastSystem.ToastType.INFO)}>Test info notification</button>

    <input type="text" name="notification text" value="This is a warning notification" bind:this={notificationWarningInput} />
    <button onclick={() => ToastSystem.addToQueue(notificationWarningInput!.value.toString(), ToastSystem.ToastType.WARNING)}>Test warning notification</button>

    <input type="text" name="notification text" value="This is a error notification" bind:this={notificationErrorInput} />
    <button onclick={() => ToastSystem.addToQueue(notificationErrorInput!.value.toString(), ToastSystem.ToastType.ERROR)}>Test error notification</button>

    <hr />

    <label>
        <input
            type="checkbox"
            class="dev-remove-notifications"
            bind:this={removeNotificationsCheckbox}
            onchange={(event: Event) => localStorage.setItem("remove-notifications", (event.target as HTMLInputElement).checked.toString())}
        />
        Remove all notifications
    </label>
</div>

<style lang="scss">
    .dev-menu {
        position: absolute;
        bottom: 20px;
        left: -200px;
        background-color: #121717;
        padding: 10px;
        border-radius: 10px;
        width: 220px;
        transition: left 0.3s ease;

        .open-dev-menu {
            position: absolute;
            top: 5px;
            right: 5px;
        }

        input {
            background-color: #4b4b4b;
            border-radius: 5px;
        }

        button {
            background-color: #4b4b4b;
            margin-bottom: 10px;
            margin-top: 10px;
            border-radius: 5px;
        }
    }
    .dev-menu:has(.open-dev-menu[type="checkbox"]:checked) {
        left: 20px;
    }
</style>
