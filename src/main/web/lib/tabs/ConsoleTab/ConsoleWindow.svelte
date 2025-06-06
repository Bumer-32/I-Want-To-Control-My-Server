<script lang="ts">
    import { onMount } from "svelte";
    import ToastSystem from "../../../scripts/toastSystem";
    import Constants from "../../../scripts/constants";
    import { isAllowed } from "../../../scripts/auth";

    let consoleDiv: HTMLDivElement
    let consoleScroll: HTMLDivElement;
    let totalMessages: HTMLSpanElement;
    let messageFieldDiv: HTMLDivElement;
    let messageFieldInput: HTMLInputElement;

    let allowAutoScroll = true;

    async function addLog(text: string) {
        const log = document.createElement("span");

        const logPattern = /\[(\d{2}:\d{2}:\d{2})] \[(.*?\/\w+)] \((.*?)\) (.*)/;
        const match = text.match(logPattern);

        if (!match) {
            log.textContent = text; // If no match, just display the text
        } else {
            const timestamp = match[1];
            const level = match[2].split("/")[1].toLowerCase();
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
            if (level === "info") {
                levelSpan.style.color = "var(--console-log-level-info-color)";
            } else if (level === "warn") {
                levelSpan.style.color = "var(--console-log-level-warn-color)";
            } else if (level === "error") {
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

        consoleScroll.appendChild(log);

        if (allowAutoScroll) {
            consoleScroll.scrollTo({
                top: consoleScroll.scrollHeight,
                behavior: "smooth",
            });
        }
    }

    function onEnter(event: KeyboardEvent) {
        if (event.key === "Enter") {
            if (messageFieldInput.value.toLowerCase().trim() == "stop") {
                const sure = window.confirm("Are you sure you want to stop the server?");
                if (!sure) {
                    return;
                }
            }
            sendCommand(messageFieldInput.value);
            messageFieldInput.value = "";
        }
    }

    // noinspection JSUnusedLocalSymbols
    export let sendCommand = function (command: string) {};

    async function connect() {
        consoleScroll.innerHTML = "";

        if (!await isAllowed("execute commands")) messageFieldDiv.classList.add("disabled");

        messageFieldInput.addEventListener("keypress", (event) => onEnter(event));

        if (await isAllowed("read logs history")) {
            try {
                const response = await fetch(Constants.LOGS_HISTORY_URL);
                if (!response.ok) {
                    ToastSystem.addToQueue(`Error: ${response.statusText}`, ToastSystem.ToastType.ERROR);
                    return;
                }

                const logs = await response.json();
                logs.forEach((log: string) => {
                    addLog(log);
                    totalMessages.innerHTML = logs.length.toString();
                });
            } catch (error) {
                console.error(error)
                ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
            }
        }

        if (!await isAllowed("read real time logs")) {
            consoleDiv.classList.add("forbidden");
        }

        const ws = new WebSocket(Constants.CONSOLE_URL);
        ws.onmessage = (event) => {
            if (!event.data.includes("Connected to /")) {
                addLog(event.data);
                totalMessages.innerHTML = consoleScroll.children.length.toString();
            }
        };

        ws.onclose = () => {
            messageFieldInput.removeEventListener("keypress", (event) => onEnter(event));

            if (!document.hidden) {
                ToastSystem.addToQueue("Connection closed", ToastSystem.ToastType.ERROR);
                ToastSystem.addToQueue("Reconnecting... In 30 seconds", ToastSystem.ToastType.ERROR);
                setTimeout(() => {
                    connect();
                    console.log("Reconnecting...");
                }, 30000);
            }
        };

        ws.onerror = () => {
            ToastSystem.addToQueue("Connection error", ToastSystem.ToastType.ERROR);
        };

        const visibilitychangeListener = (_: Event) => {
            if (document.hidden) {
                ws.close();
            } else {
                connect();
                document.removeEventListener("visibilitychange", visibilitychangeListener);
            }
        };
        document.addEventListener("visibilitychange", visibilitychangeListener);

        sendCommand = function (command: string) {
            ws.send(command);
        };
    }

    onMount(() => {
        consoleScroll.addEventListener("scroll", () => {
            allowAutoScroll = consoleScroll.scrollTop + consoleScroll.clientHeight >= consoleScroll.scrollHeight - 5;
        });

        connect();
    });
</script>

<div class="console relative mr-[60px] ml-[25px] flex h-[90%] max-w-[1200px] flex-col content-center rounded-[10px] bg-[var(--console-background-color)] items-center" bind:this={consoleDiv}>
    <div class="console-text absolute top-[8px] flex flex-col overflow-x-hidden overflow-y-auto scroll-smooth" bind:this={consoleScroll}>
        <!-- * Content will be auto generated by js -->
    </div>
    <div class="total-messages absolute right-[35px] text-[12px]">
        <span>Total messages: </span>
        <span class="count" bind:this={totalMessages}>
            <!-- * Content will be auto generated by js -->
            0
        </span>
    </div>
    <div class="input absolute bottom-0 left-0 flex w-full items-center" bind:this={messageFieldDiv}>
        <span class="material-symbols-rounded">keyboard_arrow_right</span>
        <input class="w-full border-none bg-transparent focus:outline-none text-[var(--console-text-color)] pr-[10px]" type="text" placeholder="help" bind:this={messageFieldInput} />
    </div>
</div>

<style lang="scss">
    @use "../../../styles/variables";
    @use "../../../styles/scrollbar";
    @use "../../../styles/forbidden";

    .console {
        width: variables.$console-width;

        @include scrollbar.scrollbar;

        .console-text {
            width: calc(100% - 32px);
            height: calc(100% - #{variables.$console-input-height} - 24px);
        }

        .total-messages {
            bottom: variables.$console-input-height;
        }

        .input input {
            height: variables.$console-input-height;
        }
    }
    @include forbidden.forbidden;
</style>
