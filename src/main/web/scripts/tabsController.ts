export function addTab() {
    // for plugins, in future
}

export function switchTab(id: string) {
    console.log("switching to tab", id);
    const tabs = document.querySelectorAll("main > .tabs > .tab") as NodeListOf<HTMLDivElement>;
    const tab = document.querySelector<HTMLDivElement>(`#${id}`)!;

    tabs.forEach((tab) => {
        tab.classList.add("disabled");
    });

    tab.classList.remove("disabled");

    const buttons = document.querySelectorAll("header .buttons span") as NodeListOf<HTMLDivElement>;
    buttons.forEach((button) => {
        button.classList.remove("hover-holo-effect");
    });

    document.querySelector<HTMLSpanElement>(`#header-${id.replace("-tab", "")}`)!.classList.add("hover-holo-effect");
}
