export function initTabsController() {
    alignHeaderButtons();
    handleAlignHeaderButtons();
    handleTabSwitching();
}

export function addTab() {
    // for plugins, in future
}

export function switchTab(id: string) {
    console.log("switching to tab", id);
    const tabs = document.querySelectorAll(".container > .tabs > .tab") as NodeListOf<HTMLImageElement>;
    const tab = document.querySelector(`#${id}`) as HTMLDivElement;

    tabs.forEach(tab => {
        tab.style.display = "none";
    });

    tab.style.display = "block";

}

function alignHeaderButtons() {
    const existingFillerDiv = document.querySelector(".header > .buttons .filler-div") as HTMLDivElement | null;
    if (existingFillerDiv != null) {
        existingFillerDiv.remove();
    }

    const leftDiv = document.querySelector(".header > .buttons > .left") as HTMLDivElement;
    const rightDiv = document.querySelector(".header > .buttons > .right") as HTMLDivElement;

    const leftCount = leftDiv.children.length;
    const rightCount = rightDiv.children.length;

    const fillerDiv = document.createElement("div");
    fillerDiv.style.width = "50px";
    fillerDiv.style.height = "50px";
    fillerDiv.classList.add("filler-div");
    
    if (leftCount > rightCount) {
        rightDiv.appendChild(fillerDiv);
    } else if (leftCount < rightCount) {
        rightDiv.appendChild(fillerDiv);
    }
}

function handleAlignHeaderButtons() {
    const followDiv = document.querySelector(".header > .buttons") as HTMLDivElement;

    const handleMutation = (mutationsList: MutationRecord[]) => {
        alignHeaderButtons();
    };

    const observer = new MutationObserver(handleMutation);

    observer.observe(followDiv, {childList: true, attributes: false, subtree: false});
}

function handleTabSwitching() {
    const buttons = document.querySelectorAll(".header > .buttons img") as NodeListOf<HTMLImageElement>;

    buttons.forEach(button => {
        button.addEventListener("click", () => {
            switchTab(`${button.id.replace("header-", "")}-tab`);
        });
    });
}