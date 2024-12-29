import { Constants } from "../constants.js";
import { isForbidden } from "../supply.js";
import { ToastSystem } from "../toastSystem.js";

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

    const logPattern = /\[(\d{2}:\d{2}:\d{2})\] \[(.*?\/\w+)\] \((.*?)\) (.*)/;
    const match = text.match(logPattern);

    if (!match) {
        log.textContent = text;  // If no match, just display the text
    } else {
        const timestamp = match[1];
        const level = match[2].split('/')[1].toLowerCase();
        const logger = match[3];
        const message = match[4];

        // timestamp
        const timestampSpan = document.createElement("span");
        timestampSpan.textContent = `[${timestamp}] `;
        timestampSpan.style.color = "var(--console-timestamp-color)";
        log.appendChild(timestampSpan);

        // log level
        const levelSpan = document.createElement("span");
        levelSpan.textContent = `[${match[2]}] `;
        if (level === 'info') {
            levelSpan.style.color = "var(--console-log-level-info-color)";
        } else if (level === 'warn') {
            levelSpan.style.color = "var(--console-log-level-warn-color)";
        } else if (level === 'error') {
            levelSpan.style.color = "var(--console-log-level-error-color)";
        }
        log.appendChild(levelSpan);

        // logger
        const loggerSpan = document.createElement("span");
        loggerSpan.textContent = `(${logger}) `;
        loggerSpan.style.color = "var(--console-logger-color)";
        log.appendChild(loggerSpan);

        // message
        const messageSpan = document.createElement("span");
        messageSpan.textContent = message;
        log.appendChild(messageSpan);
    }

    consoleEl.appendChild(log);

    if (allowAutoScroll) {
        consoleEl.scrollTo({
            top: consoleEl.scrollHeight,
            behavior: "smooth",
        });
    }
}

export let sendCommand = function (command: string) {}

async function connect() {

    const logsCount = (document.querySelector(".container > .tabs > #console-tab > .container > .console > .total-messages > .count") as HTMLSpanElement);
    const inputDiv = (document.querySelector(".container > .tabs > #console-tab > .container > .console > .input") as HTMLDivElement)
    const inputField = inputDiv.querySelector(".input-field") as HTMLInputElement;

    consoleEl.innerHTML = ""

    function onEnter(event: KeyboardEvent) {
        if (event.key === 'Enter') {
            if (inputField.value.toLowerCase().trim() == "stop") {
                const sure = window.confirm("Are you sure you want to stop the server?");
                if (!sure) {
                    return;
                }
            }
            sendCommand(inputField.value);
            inputField.value = "";
        }
    }

    if (!isForbidden("execute commands")) {
        inputDiv.classList.remove("disabled")

        inputField.addEventListener("keypress", event => onEnter(event));
    }

    if (!isForbidden("read logs history", consoleEl)) {
        try {
            const response = await fetch(Constants.LOGS_HISTORY_URL);
            if (!response.ok) {
                ToastSystem.showError(`Error: ${response.statusText}`);
                return;
            }

            const logs = await response.json();
            logs.forEach((log: string) => {
                addLog(log);
                logsCount.innerHTML = logs.length.toString();
            });
        } catch (error) {
            ToastSystem.showError(`Error: ${error}`);
        }
    }

    if (!isForbidden("read real time logs", consoleEl)) {
        const ws = new WebSocket(Constants.CONSOLE_URL);
        ws.onmessage = (event) => {
            if (event.data != "Connected to /ws/console") {
                addLog(event.data);
                logsCount.innerHTML = consoleEl.children.length.toString();
            }
        };

        ws.onclose = () => {
            inputField.removeEventListener("keypress", event => onEnter(event));
            ToastSystem.showInfo("Connection closed");
            ToastSystem.showInfo("Reconnecting... In 30 seconds");
            setTimeout(() => {
                connect();
                console.log("Reconnecting...");
            }, 30000);
        };

        ws.onerror = () => {
            ToastSystem.showError("Connection error");
        };

        sendCommand = function (command: string) {
            ws.send(command);
        }
    }

}