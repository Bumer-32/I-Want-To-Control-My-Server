<script lang="ts">
    import { onMount } from "svelte";
    import { type AvailableConfigs, type AvailableConfigSetting } from "../../../scripts/configsManager";
    import Tab from "./Tab.svelte";

    export let configs: AvailableConfigs;
    export let tabsContainer: HTMLDivElement;
    export let tabs: Tab[];

    let selectorContainer: HTMLDivElement;
    let selectorUnderline: HTMLSpanElement;
    let updateButton: HTMLSpanElement;
    let fileViewButton: HTMLSpanElement;
    let saveButton: HTMLSpanElement;

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

    function underline() {
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

    export function changeTab() {
        const checkedInput = selectorContainer.querySelector('input[type="radio"]:checked') as HTMLInputElement;
        const checkedLabel = selectorContainer.querySelector(`label[for="${checkedInput.id}"]`) as HTMLLabelElement;
        const tabDiv = document.getElementById(`settings_file|${checkedLabel.innerHTML}`) as HTMLDivElement;
        const tabs = tabsContainer.querySelectorAll(".tab") as NodeListOf<HTMLDivElement>;

        tabs.forEach((tab) => {
            tab.classList.add("disabled");
        });
        tabDiv.classList.remove("disabled");
    }

    onMount(() => {
        separators();

        function onClick() {
            underline();
            changeTab();
        }

        selectorContainer.addEventListener("change", onClick);

        Object.values(configs).forEach((config: AvailableConfigSetting) => {
            const buttonDiv = document.createElement("div") as HTMLDivElement;

            const buttonInput = document.createElement("input") as HTMLInputElement;
            buttonInput.type = "radio";
            buttonInput.id = `settings-tab-subtab-selector-option${selectorContainer.children.length}`;
            buttonInput.name = "settings-tab-subtub-selector";
            if (selectorContainer.children.length < 1) buttonInput.checked = true; // default tab
            buttonDiv.appendChild(buttonInput);

            const buttonLabel = document.createElement("label") as HTMLLabelElement;
            buttonLabel.htmlFor = `settings-tab-subtab-selector-option${selectorContainer.children.length}`;
            buttonLabel.innerHTML = config.selector_name;
            buttonDiv.appendChild(buttonLabel);

            selectorContainer.appendChild(buttonDiv);
        });

        changeTab(); // open default tab

        // controls buttons
        updateButton.addEventListener("click", () => {
            tabs.forEach((tab) => {
                tab.update();
            });
        });

        fileViewButton.addEventListener("click", () => {
            tabs.forEach((tab) => {
                tab.fileView();
            })
        });

        saveButton.addEventListener("click", () => {
            tabs.forEach((tab) => {
                tab.save();
            });
        });
    });
</script>

<div class="selector absolute top-[25px] flex items-center">
    <!-- ? Add underline -->
    <span
        class="pointer-events-none absolute bottom-[-2px] left-0 h-[2px] w-[126px] rounded-[2px] bg-[var(--main-text-color)]"
        style="transition: left 0.3s ease, width 0.3s ease;"
        bind:this={selectorUnderline}
    ></span>

    <div class="flex items-center" bind:this={selectorContainer}>
        <!-- * Content will be auto generated by js -->
    </div>

    <div class="absolute right-[-100px] flex items-center justify-center gap-[10px]">
        <span class="material-symbols-rounded w-[24px] cursor-pointer text-center text-[24px]" style="transition: font-size 0.05s ease;" title="Update" bind:this={updateButton}>update</span>
        <span class="material-symbols-rounded w-[24px] cursor-pointer text-center text-[24px]" style="transition: font-size 0.05s ease;" title="File view" bind:this={fileViewButton}>file_open</span>
        <span class="material-symbols-rounded w-[24px] cursor-pointer text-center text-[24px]" style="transition: font-size 0.05s ease;" title="Save" bind:this={saveButton}>save</span>
    </div>
</div>

<style lang="scss">
    @use "../../../styles/hover-holo-effect";

    .selector {
        .material-symbols-rounded:active {
            // idk why, but tailwind with active:text-[19px] doesn't work
            font-size: 19px;
        }

        :global(.separator) {
            margin-right: 5px;
            margin-left: 5px;
            opacity: 50%;
            user-select: none;
        }

        :global(input) {
            display: none;
        }

        :global(label) {
            user-select: none;
            @include hover-holo-effect.hover-holo-effect;
        }

        .material-symbols-rounded {
            @include hover-holo-effect.hover-holo-effect;
        }
    }
</style>
