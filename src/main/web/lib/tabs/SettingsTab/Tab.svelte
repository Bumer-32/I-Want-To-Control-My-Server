<script lang="ts">
    import { onMount } from "svelte";
    import type { AvailableConfigSetting } from "../../../scripts/configsManager";
    import Constants from "../../../scripts/constants";
    import ToastSystem from "../../../scripts/toastSystem";

    export let selfConfigSetting: AvailableConfigSetting;

    let tabContainer: HTMLDivElement;
    let textArea: HTMLTextAreaElement;

    export async function update(force: boolean = false) {
        if (!tabContainer.classList.contains("disabled") || force) {
            let sure = false;
            if (!force) sure = confirm(`Are you sure want to update ${selfConfigSetting.selector_name}?\nThis overwrite any changes you made!`);
            if (sure || force) {
                try {
                    const response = await fetch(`${Constants.CONFIG_URL}/${selfConfigSetting.selector_name}`);

                    if (response.ok) {
                        textArea.value = await response.text();
                    }
                } catch (error) {
                    ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
                }
            }
        }
    }

    export async function save() {
        if (!tabContainer.classList.contains("disabled")) {
            const sure = confirm(
                `Are you sure want to save ${selfConfigSetting.selector_name}?\nThis will overwrite the file on the server!\n\nIf something went wrong you can find a backup file in IWTCMS folder.`,
            );
            if (sure) {
                try {
                    const response = await fetch(`${Constants.CONFIG_URL}/${selfConfigSetting.selector_name}`, { method: "PUT", body: textArea.value });

                    if (response.ok) {
                        ToastSystem.addToQueue("Saved", ToastSystem.ToastType.INFO);
                    }
                } catch (error) {
                    ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
                }
            }
        }
    }

    onMount(() => {
        update(true);
    });
</script>

<div class="tab disabled h-full" id={"settings_file|" + selfConfigSetting.selector_name} bind:this={tabContainer}>
    <div class="easy-view">
        <div class="easy-view-text absolute top-[50%] left-[50%] flex flex-col items-center justify-center gap-[10px] text-center">
            <span>Eazy view enabled, but seems like there's no strategy for this file.</span>
            <span>Please switch to file view.</span>
            <hr />
            <span>If you need more info please check console.</span>
        </div>
    </div>
    <div class="disabled flex h-full items-center justify-center">
        <!--suppress HtmlWrongAttributeValue -->
        <textarea
            class="h-[80%] w-[80%] resize-none rounded-[10px] border-none bg-[var(--file-view-background-color)] p-[15px] text-[15px] text-[var(--file-view-text-color)] focus:outline-none"
            spellcheck="false"
            wrap="off"
            bind:this={textArea}
        ></textarea>
    </div>
</div>

<style lang="scss">
    @use "../../../styles/scrollbar";

    .tab {
        .easy-view {
            display: grid;
            grid-template-columns: repeat(3, 300px);
            gap: 10px;
            grid-auto-rows: minmax(100px, auto);
            justify-content: center;
            align-items: center;

            @media (max-width: 1000px) {
                grid-template-columns: repeat(2, 300px);
            }

            @media (max-width: 700px) {
                grid-template-columns: 300px;
            }

            .easy-view-text {
                transform: translate(-50%, -50%);

                &:not(:only-child) {
                    display: none;
                }
            }

            :global(.setting) {
                display: flex;
                flex-direction: column;
            }
            :global(.setting span) {
                font-size: 18px;
                text-wrap: nowrap;
                overflow: hidden;
            }
        }

        textarea {
            @include scrollbar.scrollbar;
        }
    }
</style>
