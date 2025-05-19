<script lang="ts">
    import { onMount } from "svelte";
    import { isForbidden } from "../../../scripts/auth";
    import ToastSystem from "../../../scripts/toastSystem";
    import Constants from "../../../scripts/constants";

    let statsDiv: HTMLDivElement;
    let cpuLoadValue: HTMLSpanElement;
    let ramUsageValue: HTMLSpanElement;
    let uptimeValue: HTMLSpanElement;
    let playersValue: HTMLSpanElement;
    let playersMaxValue: HTMLSpanElement;
    let ipAddrValue: HTMLSpanElement;
    let tpsValue: HTMLSpanElement;
    let serverTimeValue: HTMLSpanElement;

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

    // noinspection JSUnusedLocalSymbols
    export let sendCommand = function (command: string) {};

    async function connect() {
        consoleScroll.innerHTML = "";

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

        if (!(await isForbidden("execute commands"))) {
            messageFieldDiv.classList.remove("disabled");

            messageFieldInput.addEventListener("keypress", (event) => onEnter(event));
        }

        if (!(await isForbidden("read logs history"))) {
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
                ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
            }
        }

        if (!(await isForbidden("read real time logs", consoleScroll))) {
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
    }

    async function connectStats() {
        cpuLoadValue.innerHTML = "none";
        ramUsageValue.innerHTML = "none";
        uptimeValue.innerHTML = "none";
        playersValue.innerHTML = "none";
        playersMaxValue.innerHTML = "none";
        ipAddrValue.innerHTML = "none";
        tpsValue.innerHTML = "none";
        serverTimeValue.innerHTML = "none";

        if (!(await isForbidden("access to server stats", statsDiv))) {
            const ws = new WebSocket(Constants.STATS_URL);
            ws.onmessage = (event) => {
                if (!event.data.includes("Connected to /")) {
                    //console.log(event.data);
                    const jsonData = JSON.parse(event.data);

                    cpuLoadValue.innerHTML = jsonData.cpuUsage != null ? (jsonData.cpuUsage * 100).toString().split(".")[0] + "%" : "<a href='https://modrinth.com/mod/spark'>Needs Spark</a>";
                    ramUsageValue.innerHTML = jsonData.memoryUsage.toString().split(".")[0] + "%";
                    uptimeValue.innerHTML = new Date(jsonData.uptime).toISOString().slice(11, -1).split(".")[0];
                    playersValue.innerHTML = jsonData.playerCount;
                    playersMaxValue.innerHTML = jsonData.maxPlayerCount;
                    ipAddrValue.innerHTML = jsonData.ip;
                    tpsValue.innerHTML = jsonData.tps != null ? Math.floor(jsonData.tps).toString() : "<a href='https://modrinth.com/mod/spark'>Needs Spark</a>";
                    serverTimeValue.innerHTML = jsonData.serverTime.split(".")[0].split("T")[1];
                }
            };

            ws.onclose = () => {
                if (!document.hidden) {
                    setTimeout(() => {
                        connectStats();
                    }, 30000);
                }
            };

            ws.onerror = (error) => {
                console.error(error);
            };

            const visibilitychangeListener = () => {
                if (document.hidden) {
                    ws.close();
                } else {
                    connectStats();
                    document.removeEventListener("visibilitychange", visibilitychangeListener);
                }
            };
            document.addEventListener("visibilitychange", visibilitychangeListener);
        }
    }

    onMount(() => {
        consoleScroll.addEventListener("scroll", () => {
            allowAutoScroll = consoleScroll.scrollTop + consoleScroll.clientHeight >= consoleScroll.scrollHeight - 5;
        });

        connect();
        connectStats();
    });
</script>

<div id="console-tab" class="tab" style="display: block; /* DEFAULT TAB */">
    <!-- ! DEFAULT TAB -->
    <div class="container">
        <h1>Console</h1>
        <div class="windows"></div>
    </div>
</div>

<style lang="scss">
    @use "../../../styles/variables";
    @use "../../../styles/scrollbar";
    @use "../../../styles/forbidden";

    #console-tab {
        .container {
            width: 100%;
            height: variables.$container-height;
            display: flex;
            flex-direction: column;

            h1 {
                display: flex;
                justify-content: center;
                align-items: center;
            }

            .windows {
                width: 100%;
                height: 100%;
                display: flex;
                justify-content: center;

                .statistics {
                    background-color: var(--console-background-color);
                    padding: 10px;
                    border-radius: 10px;
                    height: fit-content;
                    margin-left: 60px;
                    width: 250px;
                    transition: background-color ease 0.3s;

                    h3 {
                        margin-top: 0;
                        margin-bottom: 10px;
                        display: flex;
                        justify-content: center;
                    }

                    :global(a) {
                        text-decoration: none;
                        color: #597cef;
                    }
                }
                @include forbidden.forbidden(130px);

                .console {
                    width: variables.$console-width;
                    height: 90%;
                    background-color: var(--console-background-color);
                    border-radius: 10px;
                    display: flex;
                    flex-direction: column;
                    position: relative;
                    align-items: center;
                    margin-left: 25px;
                    margin-right: 60px;
                    max-width: 1200px;
                    transition: background-color ease 0.3s;

                    @include scrollbar.scrollbar;
                    @include forbidden.forbidden(423px);

                    .console-text {
                        position: absolute;
                        width: calc(100% - 32px);
                        height: calc(100% - #{variables.$console-input-height} - 24px);
                        top: 8px;
                        display: flex;
                        flex-direction: column;
                        overflow-y: auto;
                        overflow-x: hidden;
                        scroll-behavior: smooth;
                    }

                    .total-messages {
                        position: absolute;
                        right: 35px;
                        bottom: variables.$console-input-height;
                        font-size: 12px;
                    }

                    .input {
                        position: absolute;
                        bottom: 0;
                        left: 0;
                        display: flex;
                        align-items: center;
                        width: 100%;

                        input {
                            width: 100%;
                            height: variables.$console-input-height;
                            background-color: transparent;
                            border: none;
                            color: var(--console-text-color);
                            transition: color ease 0.3s;

                            &:focus {
                                outline: none;
                            }
                        }
                    }
                }
            }
        }
    }
</style>
