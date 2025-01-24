export default async function settingsInit() {
    tabs();
}

function tabs() {
    function underlineProcessing() {
        const container = document.querySelector("#settings-tab .container .tab-selector") as HTMLDivElement
        const underline = container.querySelector(".underline") as HTMLSpanElement

        function updateUnderline() {
            const checkedInput = container.querySelector('input[type="radio"]:checked') as HTMLInputElement;
            const checkedLabel = container.querySelector(`label[for="${checkedInput.id}"]`) as HTMLLabelElement;

            const { offsetLeft, offsetWidth } = checkedLabel;

            let width = offsetWidth

            const separator = checkedLabel.querySelector(".separator") as HTMLSpanElement | null
            if (separator != null) {
                width = offsetWidth - separator!.offsetWidth - 10 // - 10px because we have margins
            }

            underline.style.left = `${offsetLeft}px`;
            underline.style.width = `${width}px`
        }

        container.addEventListener("change", updateUnderline);
        window.addEventListener("resize", updateUnderline);
    }

    function createSeparators() {
        const container = document.querySelector("#settings-tab .container .tab-selector") as HTMLDivElement
        const  labels = container.querySelectorAll("label") as NodeListOf<HTMLLabelElement>

        labels.forEach(label => {
            if (label != labels[labels.length - 1]) {
                const separator = document.createElement("span")
                separator.innerHTML = "|"
                separator.classList.add("separator")
                label.appendChild(separator)
            }
        });
    }

    createSeparators();
    underlineProcessing();
}