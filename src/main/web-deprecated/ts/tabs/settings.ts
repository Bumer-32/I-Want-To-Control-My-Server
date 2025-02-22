import { Constants } from "../constants.js";
const selectorContainer = document.querySelector(
    "#settings-tab .container .tab-selector",
) as HTMLDivElement;
const tabsContainer = document.querySelector(
    "#settings-tab .container .settings-tab-tabs",
) as HTMLDivElement;

export default async function settingsInit() {
    selector();

    createSettingsFileTab(
        "server.properties",
        `${Constants.BASE_URL}/api/mcSettings`,
    );
    createSettingsFileTab(
        "iwtcms.conf",
        `${Constants.BASE_URL}/api/iwtcmsSettings`,
    );
}

function selector() {
    function processing() {
        const underline = selectorContainer.querySelector(
            ".underline",
        ) as HTMLSpanElement;

        function clickHandler() {
            changeTab();
            hoverHoloEffect();
            updateUnderline();
        }

        function changeTab() {
            const checkedInput = selectorContainer.querySelector(
                'input[type="radio"]:checked',
            ) as HTMLInputElement;
            const checkedLabel = selectorContainer.querySelector(
                `label[for="${checkedInput.id}"]`,
            ) as HTMLLabelElement;
            const tabDiv = document.getElementById(
                `settings_file|${checkedLabel.innerHTML}`,
            ) as HTMLDivElement;
            const tabs = tabsContainer.querySelectorAll(
                ".tab",
            ) as NodeListOf<HTMLDivElement>;

            tabs.forEach((tab) => {
                tab.classList.add("disabled");
            });
            tabDiv.classList.remove("disabled");
        }

        function hoverHoloEffect() {
            const labels = selectorContainer.querySelectorAll(
                "label",
            ) as NodeListOf<HTMLLabelElement>;
            const checkedInput = selectorContainer.querySelector(
                'input[type="radio"]:checked',
            ) as HTMLInputElement;
            const checkedLabel = selectorContainer.querySelector(
                `label[for="${checkedInput.id}"]`,
            ) as HTMLLabelElement;
            labels.forEach((label) => {
                label.classList.remove("hover-holo-effect");
            });
            checkedLabel.classList.add("hover-holo-effect");
        }

        function updateUnderline() {
            const checkedInput = selectorContainer.querySelector(
                'input[type="radio"]:checked',
            ) as HTMLInputElement;
            const checkedLabel = selectorContainer.querySelector(
                `label[for="${checkedInput.id}"]`,
            ) as HTMLLabelElement;

            const { offsetLeft, offsetWidth } = checkedLabel;

            let width = offsetWidth;

            const separator = checkedLabel.querySelector(
                ".separator",
            ) as HTMLSpanElement | null;
            if (separator != null) {
                width = offsetWidth - separator!.offsetWidth - 10; // - 10px because we have margins
            }

            underline.style.left = `${offsetLeft}px`;
            underline.style.width = `${width}px`;
        }

        selectorContainer.addEventListener("change", clickHandler);
        // window.addEventListener("resize", updateUnderline);
    }

    function separators() {
        const container = document.querySelector(
            "#settings-tab .container .tab-selector .selector",
        ) as HTMLDivElement;
        function updateSeparators() {
            const tabs = container.querySelectorAll(
                "div",
            ) as NodeListOf<HTMLDivElement>;
            const separators = container.querySelectorAll(
                ".separator",
            ) as NodeListOf<HTMLSpanElement>;

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

        const handleMutation = (mutationsList: MutationRecord[]) => {
            updateSeparators();
        };

        const observer = new MutationObserver(handleMutation);

        observer.observe(container, {
            childList: true,
            attributes: false,
            subtree: false,
        });
    }

    separators();
    processing();
}

function createSettingsFileTab(fileName: string, url: string) {
    const tabsSelectorDiv = document.querySelector(
        "#settings-tab .container .tab-selector .selector",
    ) as HTMLDivElement;
    const tabsDiv = document.querySelector(
        "#settings-tab .container .settings-tab-tabs",
    ) as HTMLDivElement;
    const fileViewButton = document.querySelector(
        "#settings-tab .container .tab-selector .controls-buttons .file-view",
    ) as HTMLSpanElement;
    const updateButton = document.querySelector(
        "#settings-tab .container .tab-selector .controls-buttons .update",
    ) as HTMLSpanElement;
    const saveButton = document.querySelector(
        "#settings-tab .container .tab-selector .controls-buttons .save",
    ) as HTMLSpanElement;

    // add button to selector
    const buttonDiv = document.createElement("div") as HTMLDivElement;

    const buttonInput = document.createElement("input") as HTMLInputElement;
    buttonInput.type = "radio";
    buttonInput.id = `settings-tab-subtab-selector-option${tabsSelectorDiv.children.length}`;
    buttonInput.name = "settings-tab-subtub-selector";
    buttonDiv.appendChild(buttonInput);

    const buttonLabel = document.createElement("label") as HTMLLabelElement;
    buttonLabel.htmlFor = `settings-tab-subtab-selector-option${tabsSelectorDiv.children.length}`;
    buttonLabel.innerHTML = fileName;
    buttonDiv.appendChild(buttonLabel);
    // add button to selector

    // add tab
    const tab = document.createElement("div") as HTMLDivElement;
    tab.classList.add("tab");
    if (tabsSelectorDiv.children.length != 0) tab.classList.add("disabled");
    tab.id = `settings_file|${fileName}`;

    const eazyView = document.createElement("div") as HTMLDivElement;
    eazyView.classList.add("eazy-view");
    tab.appendChild(eazyView);

    const fileView = document.createElement("div") as HTMLDivElement;
    fileView.classList.add("file-view");
    fileView.classList.add("disabled");
    tab.appendChild(fileView);
    const textarea = document.createElement("textarea") as HTMLTextAreaElement;
    textarea.spellcheck = false;
    textarea.wrap = "off";
    fileView.appendChild(textarea);
    // add tab

    // file view
    fileViewButton.addEventListener("click", () => {
        eazyView.classList.toggle("disabled");
        fileView.classList.toggle("disabled");
    });

    // update
    updateButton.addEventListener("click", () => {
        if (!tab.classList.contains("disabled")) {
            const sure = confirm(
                "Are you sure you want to update this file?\nThis will overwrite any changes you have made!",
            );
            if (sure) {
                update(tab, url);
            }
        }
    });

    // save
    saveButton.addEventListener("click", () => {
        if (!tab.classList.contains("disabled")) {
            const sure = confirm(
                "Are you sure you want to save this file?\nThis will overwrite the file on the server!\n\nIf something went wrong you can find a backup file in IWTCMS folder.",
            );
            if (sure) {
                fetch(url, {
                    method: "PUT",
                    body: textarea.value,
                });
            }
        }
    });

    update(tab, url);

    tabsSelectorDiv.appendChild(buttonDiv);
    tabsDiv.appendChild(tab);
}

async function update(tabContainer: HTMLDivElement, url: string) {
    console.log("updating", tabContainer.id, ":", url);

    const fileViewTextArea = tabContainer.querySelector(
        ".file-view textarea",
    ) as HTMLTextAreaElement;

    const response = await fetch(url);
    const text = await response.text();
    fileViewTextArea.value = text;

    updateEazyView(tabContainer, url);
}

async function updateEazyView(tabContainer: HTMLDivElement, url: string) {
    const fileViewTextArea = tabContainer.querySelector(
        ".file-view textarea",
    ) as HTMLTextAreaElement;
    const eazyView = tabContainer.querySelector(".eazy-view") as HTMLDivElement;

    const response = await fetch(url + "/types");
    const text = await response.text();
}

// TODO: add FORBIDDEN message to settings tab
