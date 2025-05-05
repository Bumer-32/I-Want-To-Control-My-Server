<script lang="ts">
    import readConfig, { type AvailableConfigs, type AvailableConfigSetting, type Strategy } from "../../../scripts/configsManager";
    import Constants from "../../../scripts/constants";
    import { slideOnOverflow } from "../../../scripts/slideOnOverflow";
    import ToastSystem from "../../../scripts/toastSystem";
    import DevMenu from "../../DevMenu.svelte";
    import { onMount } from "svelte";
    import YAML from "yaml";

    let selectorContainer: HTMLDivElement;
    let selectorUnderline: HTMLSpanElement;
    let tabsContainer: HTMLDivElement;
    let fileViewButton: HTMLSpanElement;
    let updateButton: HTMLSpanElement;
    let saveButton: HTMLSpanElement;

    function selector() {
        function processing() {
            function clickHandler() {
                changeTab();
                hoverHoloEffect();
                updateUnderline();
            }

            function changeTab() {
                const checkedInput = selectorContainer.querySelector('input[type="radio"]:checked') as HTMLInputElement;
                const checkedLabel = selectorContainer.querySelector(`label[for="${checkedInput.id}"]`) as HTMLLabelElement;
                const tabDiv = document.getElementById(`settings_file|${checkedLabel.innerHTML}`) as HTMLDivElement;
                const tabs = tabsContainer.querySelectorAll(".tab") as NodeListOf<HTMLDivElement>;

                tabs.forEach((tab) => {
                    tab.classList.add("disabled");
                });
                tabDiv.classList.remove("disabled");
            }

            function hoverHoloEffect() {
                const labels = selectorContainer.querySelectorAll("label") as NodeListOf<HTMLLabelElement>;
                const checkedInput = selectorContainer.querySelector('input[type="radio"]:checked') as HTMLInputElement;
                const checkedLabel = selectorContainer.querySelector(`label[for="${checkedInput.id}"]`) as HTMLLabelElement;
                labels.forEach((label) => {
                    label.classList.remove("hover-holo-effect");
                });
                checkedLabel.classList.add("hover-holo-effect");
            }

            function updateUnderline() {
                const checkedInput = selectorContainer.querySelector('input[type="radio"]:checked') as HTMLInputElement;
                const checkedLabel = selectorContainer.querySelector(`label[for="${checkedInput.id}"]`) as HTMLLabelElement;

                const { offsetLeft, offsetWidth } = checkedLabel;

                let width = offsetWidth;

                const separator = checkedLabel.querySelector(".separator") as HTMLSpanElement | null;
                if (separator != null) {
                    width = offsetWidth - separator!.offsetWidth - 10; // - 10px because we have margins
                }

                selectorUnderline.style.left = `${offsetLeft}px`;
                selectorUnderline.style.width = `${width}px`;
            }

            selectorContainer.addEventListener("change", clickHandler);
            // window.addEventListener("resize", updateUnderline);
        }

        function separators() {
            function updateSeparators() {
                const tabs = selectorContainer.querySelectorAll("div") as NodeListOf<HTMLDivElement>;
                const separators = selectorContainer.querySelectorAll(".separator") as NodeListOf<HTMLSpanElement>;

                separators.forEach((separator) => {
                    separator.remove();
                });

                tabs.forEach((tab) => {
                    if (tab != tabs[tabs.length - 1]) {
                        const separator = document.createElement("span");
                        separator.innerHTML = "|";
                        separator.classList.add("separator");
                        tab.appendChild(separator);
                    }
                });
            }

            const handleMutation = (_: MutationRecord[]) => {
                updateSeparators();
            };

            const observer = new MutationObserver(handleMutation);

            observer.observe(selectorContainer, {
                childList: true,
                attributes: false,
                subtree: false,
            });
        }

        separators();
        processing();
    }

    function createSettingsFileTab(fileName: string, url: string) {
        // add button to selector
        const buttonDiv = document.createElement("div") as HTMLDivElement;

        const buttonInput = document.createElement("input") as HTMLInputElement;
        buttonInput.type = "radio";
        buttonInput.id = `settings-tab-subtab-selector-option${selectorContainer.children.length}`;
        buttonInput.name = "settings-tab-subtub-selector";
        if (fileName == "server.properties") buttonInput.checked = true; // default tab
        buttonDiv.appendChild(buttonInput);

        const buttonLabel = document.createElement("label") as HTMLLabelElement;
        buttonLabel.htmlFor = `settings-tab-subtab-selector-option${selectorContainer.children.length}`;
        buttonLabel.innerHTML = fileName;
        buttonDiv.appendChild(buttonLabel);
        // add button to selector

        // * add tab
        const tab = document.createElement("div") as HTMLDivElement;
        tab.classList.add("tab");
        if (fileName != "server.properties") tab.classList.add("disabled");
        tab.id = `settings_file|${fileName}`;

        const eazyView = document.createElement("div") as HTMLDivElement;
        const eazyViewTextDiv = document.createElement("div") as HTMLDivElement;
        const eazyViewText1 = document.createElement("span") as HTMLSpanElement;
        const eazyViewText2 = document.createElement("span") as HTMLSpanElement;
        const separator = document.createElement("hr") as HTMLHRElement;
        const eazyViewText3 = document.createElement("span") as HTMLSpanElement;
        eazyView.classList.add("eazy-view");
        eazyViewTextDiv.classList.add("eazy-view-text");
        eazyViewText1.innerHTML = "Eazy view enabled, but seems like there's no strategy for this file.";
        eazyViewText2.innerHTML = "Please switch to file view.";
        eazyViewText3.innerHTML = "If you need more info please check console.";
        eazyViewTextDiv.appendChild(eazyViewText1);
        eazyViewTextDiv.appendChild(eazyViewText2);
        eazyViewTextDiv.appendChild(separator);
        eazyViewTextDiv.appendChild(eazyViewText3);
        eazyView.appendChild(eazyViewTextDiv);
        tab.appendChild(eazyView);

        const fileView = document.createElement("div") as HTMLDivElement;
        fileView.classList.add("file-view");
        fileView.classList.add("disabled");
        tab.appendChild(fileView);

        const textarea = document.createElement("textarea") as HTMLTextAreaElement;
        textarea.spellcheck = false;
        textarea.wrap = "off";
        fileView.appendChild(textarea);
        // * add tab

        // file view
        fileViewButton.addEventListener("click", () => {
            eazyView.classList.toggle("disabled");
            fileView.classList.toggle("disabled");
        });

        // update
        updateButton.addEventListener("click", () => {
            if (!tab.classList.contains("disabled")) {
                const sure = confirm("Are you sure you want to update this file?\nThis will overwrite any changes you have made!");
                if (sure) {
                    updateFileView(tab, url);
                    updateEazyView(tab, url, fileName);
                }
            }
        });

        // save
        saveButton.addEventListener("click", () => {
            console.log(url);
            if (!tab.classList.contains("disabled")) {
                const sure = confirm("Are you sure you want to save this file?\nThis will overwrite the file on the server!\n\nIf something went wrong you can find a backup file in IWTCMS folder.");
                if (sure) {
                    fetch(url, {
                        method: "PUT",
                        headers: {
                            "Content-Type": "text/plain",
                        },
                        body: textarea.value,
                    });
                }
            }
        });

        updateFileView(tab, url);
        updateEazyView(tab, url, fileName);

        selectorContainer.appendChild(buttonDiv);
        tabsContainer.appendChild(tab);
    }

    async function updateFileView(tabContainer: HTMLDivElement, url: string) {
        try {
            console.log("updating", tabContainer.id, ":", url);

            const fileViewTextArea = tabContainer.querySelector(".file-view textarea") as HTMLTextAreaElement;

            const response = await fetch(url);
            fileViewTextArea.value = await response.text();
        } catch (error) {
            ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
        }
    }

    async function updateEazyView(tabContainer: HTMLDivElement, url: string, selectorName: string) {
        try {
            const availableConfigsResponse = await fetch(Constants.CONFIG_URL);
            if (availableConfigsResponse.status != 200) return;

            const strategyResponse = await fetch(url + "/strategy");
            if (strategyResponse.status != 200) return;

            const configFileResponse = await fetch(url);
            if (configFileResponse.status != 200) return;

            const fileViewTextArea = tabContainer.querySelector(".file-view textarea") as HTMLTextAreaElement;
            const eazyView = tabContainer.querySelector(".eazy-view") as HTMLDivElement;
            const availableConfigs: AvailableConfigs = YAML.parse(await availableConfigsResponse.text());
            const strategy: Strategy = YAML.parse(await strategyResponse.text());

            (eazyView.querySelectorAll("div") as NodeListOf<HTMLDivElement>).forEach((div) => {
                if (!div.classList.contains("eazy-view-text")) div.remove();
            });

            const configs = await readConfig(selectorName, availableConfigs, strategy, await configFileResponse.text());

            if (configs == null) return;

            Object.keys(configs).forEach((key) => {
                const settingDiv = document.createElement("div");
                const settingText = document.createElement("span");
                const settingInput = document.createElement("input");
                settingDiv.classList.add("setting");
                settingText.innerHTML = key;
                settingDiv.style.backgroundColor = "var(--eazy-view-setting-background-color)";

                switch(configs[key].type) {
                    case "string":
                        settingInput.placeholder = (strategy[key].default !== null ? strategy[key].default.toString() : "");
                        settingInput.value = (configs[key].default !== null ? configs[key].default.toString() : "");
                        break;
                    case "bool":
                        settingInput.type = "checkbox";
                        settingInput.checked = configs[key]!.default as boolean
                        break;
                    case "int":
                        settingInput.type = "number";
                        settingInput.placeholder = (strategy[key].default !== null ? strategy[key].default.toString() : "");
                        settingInput.value = (configs[key].default !== null ? configs[key].default.toString() : "");
                        if (configs[key].max != null) settingInput.max = configs[key].max.toString();
                        if (configs[key].min != null) settingInput.min = configs[key].min.toString();
                        if (configs[key].step != null && configs[key].max != null && configs[key].min != null) {
                            settingInput.type = "range";
                            settingInput.step = configs[key].step.toString();
                        }
                        break;
                }

                settingDiv.appendChild(settingText);
                settingDiv.appendChild(settingInput);

                eazyView.appendChild(settingDiv);
                
                slideOnOverflow(settingText);
            })
        } catch (error) {
            ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
        }
    }

    // TODO: add FORBIDDEN message to settings tab

    onMount(() => {
        try {
            selector();

            fetch(Constants.CONFIG_URL).then(async (response) => {
                const configs: AvailableConfigs = YAML.parse(await response.text());
                Object.values(configs).forEach((config: AvailableConfigSetting) => {
                    createSettingsFileTab(config.selector_name, `${Constants.BASE_URL}api/config/${config.selector_name}`);
                });
            });
        } catch (error) {
            ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
        }
    });
</script>

<div id="settings-tab" class="tab disabled">
    <div class="container">
        <div class="tab-selector">
            <!-- ? Add underline -->
            <span class="underline" bind:this={selectorUnderline}></span>

            <div class="selector" bind:this={selectorContainer}>
                <!-- * Content will be auto generated by js -->
            </div>

            <div class="controls-buttons">
                <span class="material-symbols-rounded" title="Update" bind:this={updateButton}>update</span>
                <span class="material-symbols-rounded" title="File view" bind:this={fileViewButton}>file_open</span>
                <span class="material-symbols-rounded" title="Save" bind:this={saveButton}>save</span>
            </div>
        </div>

        <div class="settings-tab-tabs" bind:this={tabsContainer}>
            <!-- * Content will be auto generated by js -->
        </div>

        <DevMenu />
    </div>
</div>

<style lang="scss">
    @use "../../../styles/hover-holo-effect";
    @use "../../../styles/scrollbar";
    @use "../../../styles/variables";

    #settings-tab {
        .container {
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
            height: variables.$container-height;

            .tab-selector {
                position: absolute;
                top: 25px;
                display: flex;
                align-items: center;
                //position: relative;

                .selector {
                    display: flex;
                    align-items: center;

                    :global(.separator) {
                        margin-right: 5px;
                        margin-left: 5px;
                        opacity: 50%;
                    }

                    :global(label) {
                        transition:
                            filter 0.3s ease,
                            box-shadow 0.3s ease;
                    }

                    :global(label:hover) {
                        @include hover-holo-effect.hover-holo-effect;
                    }

                    :global(input) {
                        display: none;
                    }
                }

                .controls-buttons {
                    display: flex;
                    position: absolute;
                    right: -100px;
                    gap: 10px;
                    font-size: 24px;

                    .material-symbols-rounded {
                        cursor: pointer;
                        width: 24px;
                        display: flex;
                        align-items: center;
                        justify-content: center;

                        // hover holo effect
                        transition:
                            filter 0.3s ease,
                            box-shadow 0.3s ease;
                        &:hover {
                            @include hover-holo-effect.hover-holo-effect;
                        }

                        &:active {
                            // press effect
                            font-size: 19px;
                        }
                    }
                }

                .underline {
                    background-color: var(--main-text-color);
                    position: absolute;
                    bottom: -2px;
                    height: 2px;
                    border-radius: 2px;
                    transition:
                        left 0.3s ease,
                        width 0.3s ease;
                    pointer-events: none;

                    left: 0;
                    width: 126px; // needs for initial position, idk why, but first time function sets left 0 and width 0
                }
            }

            .settings-tab-tabs {
                $space: 80px;

                width: 100%;
                height: calc(100% - $space);
                margin-top: $space;
                overflow-x: hidden;
                overflow-y: auto;

                @include scrollbar.scrollbar;

                :global(.tab) {
                    height: 100%;
                }

                :global(.eazy-view) {
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
                }

                :global(.eazy-view .eazy-view-text) {
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    align-items: center;
                    gap: 10px;
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    text-align: center;

                    &:not(:only-child) {
                        display: none;
                    }
                }

                :global(.eazy-view .setting) {
                    display: flex;
                    flex-direction: column;
                }
                :global(.eazy-view .setting span) {
                    font-size: 18px;
                    text-wrap: nowrap;
                    overflow: hidden;
                }

                :global(.file-view) {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 100%;

                    @include scrollbar.scrollbar;
                }

                :global(.tab .file-view textarea) {
                    width: 80%;
                    height: 80%;
                    resize: none;
                    background-color: var(--file-view-background-color);
                    color: var(--file-view-text-color);
                    border: none;
                    border-radius: 10px;
                    font-size: 15px;
                    padding: 15px;

                    &:focus {
                        outline: none;
                    }
                }
            }
        }
    }
</style>
