import { isForbidden } from "../supply.js";

let allowAutoScroll = true;
const consoleEl = document.querySelector(".container .tabs #console-tab .container .console .console-text") as HTMLDivElement;

export async function consoleInit() {
    autoScroll();
    connect();
}

async function autoScroll() {
    

    consoleEl.addEventListener("scroll", () => {
        const isAtBottom = consoleEl.scrollTop + consoleEl.clientHeight >= consoleEl.scrollHeight - 5;

        allowAutoScroll = isAtBottom;
    });
}

async function addLog(text: string) {
    const log = document.createElement("span");
    log.innerHTML = text;
    consoleEl.appendChild(log);

    if (allowAutoScroll) {
        consoleEl.scrollTo({
            top: consoleEl.scrollHeight,
            behavior: "smooth",
        });
    }
}

async function connect() {
    if(!isForbidden("read real time logs", consoleEl)) {
        // TODO
    }

}