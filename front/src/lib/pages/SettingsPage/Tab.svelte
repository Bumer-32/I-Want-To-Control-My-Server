<script lang="ts">
    import { onMount } from "svelte";
    import readConfig, { type AvailableConfigSetting, ConfigWriters, type Strategy } from "./configsManager";
    import Constants from "../../constants";
    import ToastSystem from "../../toastSystem";
    import slideOnOverflow from "./slideOnOverflow";
    import YAML from "yaml";

    export let selfConfigSetting: AvailableConfigSetting;

    let tabContainer: HTMLDivElement;
    let textArea: HTMLTextAreaElement;
    let easyViewDiv: HTMLDivElement;
    let fileViewDiv: HTMLDivElement;

    let defaultStrategy: Strategy;
    let easyViewPlatesList: Strategy = {};

    let platesList: HTMLDivElement[] = [];

    export async function update(force: boolean = false) {
        if (!tabContainer.classList.contains("disabled") || force) {
            let sure = false;
            if (!force) sure = confirm(`Are you sure want to update ${selfConfigSetting.selector_name}?\nThis overwrite any changes you made!`);
            if (sure || force) {
                try {
                    const response = await fetch(`${Constants.CONFIG_URL}/${selfConfigSetting.selector_name}`);

                    if (response.ok) {
                        textArea.value = await response.text();
                        await easyViewPlates();
                    }
                } catch (error) {
                    ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
                }
            }
        }
    }

    export function fileView() {
        easyViewDiv.classList.toggle("disabled");
        fileViewDiv.classList.toggle("disabled");
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

    async function easyViewPlates() {
        try {
            const currentConfig = textArea.value;
            const strategyText = await (await fetch(`${Constants.CONFIG_URL}/${selfConfigSetting.selector_name}/strategy`)).text();
            const strategy: Strategy = YAML.parse(strategyText);
            defaultStrategy = strategy;

            const config = await readConfig(selfConfigSetting, strategy, currentConfig);
            if (config == null) return;
            easyViewPlatesList = config;
        } catch (error) {
            ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
        }
    }

    function generateEasyConfig() {
        let configData: Record<string, any> = {};

        platesList.forEach((plate) => {
            const name = plate.querySelector<HTMLSpanElement>(".name")!.innerHTML;
            const input = plate.querySelector<HTMLInputElement | HTMLSelectElement>(".input")!;
            const type = defaultStrategy[name].type;

            if (type == "int") {
                configData[name] = +input.value;
            } else if (type == "bool") {
                configData[name] = input.value === "true";
            } else {
                configData[name] = input.value;
            }
        });

        const result = ConfigWriters.writeConfig(selfConfigSetting.config_type, configData);
        if (result == null) {
            ToastSystem.addToQueue("Can't generate config: no such writer/unknown config type", ToastSystem.ToastType.ERROR);
            return;
        }
        textArea.value = result;
    }

    onMount(() => {
        update(true);
    });
</script>

<div class="tab disabled h-full" id={"settings_file|" + selfConfigSetting.selector_name} bind:this={tabContainer}>
    <div class="easy-view relative" bind:this={easyViewDiv}>
        <div class="easy-view-text absolute top-[50%] left-[50%] flex flex-col items-center justify-center gap-[10px] text-center">
            <span>Easy view enabled, but seems like there's no strategy for this file.</span>
            <span>Please switch to file view.</span>
            <hr />
            <span>If you need more info please check console.</span>
        </div>

        {#key easyViewPlatesList}
            {#each Object.entries(easyViewPlatesList) as [name, data], i}
                <div
                    class="relative h-[50px] rounded-[5px] bg-[var(--easy-view-setting-background-color)] before:absolute before:bottom-0 before:left-0 before:h-[15px] before:w-full before:rounded-b-[5px] before:bg-[var(--easy-view-setting-secondary-background-color)] before:content-['']"
                    bind:this={platesList[i]}
                >
                    <div class="flex h-full w-full items-center justify-between px-[5px]" style="transform:translateY(-7.5px)">
                        <span class="name w-[220px] overflow-clip rounded-full text-nowrap" use:slideOnOverflow>{name}</span>
                        {#if data.type === "int"}
                            <input
                                type="number"
                                min={data.min}
                                max={data.max}
                                step={data.step}
                                value={data.default}
                                class="input w-[160px] [appearance:textfield] rounded-[5px] bg-[var(--easy-view-setting-secondary-background-color)] pl-[5px] outline-none [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none"
                                on:change={generateEasyConfig}
                            />
                        {:else if data.type === "bool"}
                            <select class="input rounded-[5px] bg-[var(--easy-view-setting-secondary-background-color)] outline-none" on:change={generateEasyConfig}>
                                {#if data.default === false}<option selected>false</option>{:else}<option>false</option>{/if}
                                {#if data.default === true}<option selected>true</option>{:else}<option>true</option>{/if}
                            </select>
                        {:else if data.type === "choose"}
                            <select class="input rounded-[5px] bg-[var(--easy-view-setting-secondary-background-color)] outline-none" on:change={generateEasyConfig}>
                                {#each data.options as option}
                                    {#if data.default === option}<option selected>{option}</option>{:else}<option>{option}</option>{/if}
                                {/each}
                            </select>
                        {:else}
                            <!--string and other unregistered will also work as string-->
                            <input
                                value={data.default}
                                class="input w-[160px] rounded-[5px] bg-[var(--easy-view-setting-secondary-background-color)] pl-[5px] outline-none"
                                on:change={generateEasyConfig}
                            />
                        {/if}
                    </div>
                    <span class="absolute bottom-0 left-[5px] text-[10px]">default: {defaultStrategy[name].default}</span>
                </div>
            {/each}
        {/key}
    </div>
    <div class="disabled flex h-full items-center justify-center" bind:this={fileViewDiv}>
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
            grid-template-columns: repeat(3, 400px);
            gap: 10px;
            grid-auto-rows: minmax(100px, auto);
            justify-content: center;
            align-items: center;

            @media (min-width: 1050px) and (max-width: 1220px) {
                grid-template-columns: repeat(2, 500px);
            }

            @media (max-width: 1049px) {
                grid-template-columns: 500px;
            }

            .easy-view-text {
                transform: translate(-50%, -50%);

                &:not(:only-child) {
                    display: none;
                }
            }
        }

        textarea {
            @include scrollbar.scrollbar;
        }
    }
</style>
