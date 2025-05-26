<script lang="ts">
    import { onMount } from "svelte";
    import { isForbidden } from "../../../scripts/auth";
    import ToastSystem from "../../../scripts/toastSystem";
    import Constants from "../../../scripts/constants";
    import ConsoleWindow from "./ConsoleWindow.svelte";
    import StatsWindow from "./StatsWindow.svelte";

    // let statsDiv: HTMLDivElement;
    // let cpuLoadValue: HTMLSpanElement;
    // let ramUsageValue: HTMLSpanElement;
    // let uptimeValue: HTMLSpanElement;
    // let playersValue: HTMLSpanElement;
    // let playersMaxValue: HTMLSpanElement;
    // let ipAddrValue: HTMLSpanElement;
    // let tpsValue: HTMLSpanElement;
    // let serverTimeValue: HTMLSpanElement;

    // let consoleScroll: HTMLDivElement;
    // let totalMessages: HTMLSpanElement;
    // let messageFieldDiv: HTMLDivElement;
    // let messageFieldInput: HTMLInputElement;

    // let allowAutoScroll = true;

    // async function addLog(text: string) {
    //     const log = document.createElement("span");

    //     const logPattern = /\[(\d{2}:\d{2}:\d{2})] \[(.*?\/\w+)] \((.*?)\) (.*)/;
    //     const match = text.match(logPattern);

    //     if (!match) {
    //         log.textContent = text; // If no match, just display the text
    //     } else {
    //         const timestamp = match[1];
    //         const level = match[2].split("/")[1].toLowerCase();
    //         const logger = match[3];
    //         const message = match[4];

    //         // timestamp
    //         const timestampSpan = document.createElement("span");
    //         timestampSpan.textContent = `[${timestamp}] `;
    //         timestampSpan.style.color = "var(--console-timestamp-color)";
    //         log.appendChild(timestampSpan);

    //         // log level
    //         const levelSpan = document.createElement("span");
    //         levelSpan.textContent = `[${match[2]}] `;
    //         if (level === "info") {
    //             levelSpan.style.color = "var(--console-log-level-info-color)";
    //         } else if (level === "warn") {
    //             levelSpan.style.color = "var(--console-log-level-warn-color)";
    //         } else if (level === "error") {
    //             levelSpan.style.color = "var(--console-log-level-error-color)";
    //         }
    //         log.appendChild(levelSpan);

    //         // logger
    //         const loggerSpan = document.createElement("span");
    //         loggerSpan.textContent = `(${logger}) `;
    //         loggerSpan.style.color = "var(--console-logger-color)";
    //         log.appendChild(loggerSpan);

    //         // message
    //         const messageSpan = document.createElement("span");
    //         messageSpan.textContent = message;
    //         log.appendChild(messageSpan);
    //     }

    //     consoleScroll.appendChild(log);

    //     if (allowAutoScroll) {
    //         consoleScroll.scrollTo({
    //             top: consoleScroll.scrollHeight,
    //             behavior: "smooth",
    //         });
    //     }
    // }

    // // noinspection JSUnusedLocalSymbols
    // export let sendCommand = function (command: string) {};

    // async function connect() {
    //     consoleScroll.innerHTML = "";

    //     function onEnter(event: KeyboardEvent) {
    //         if (event.key === "Enter") {
    //             if (messageFieldInput.value.toLowerCase().trim() == "stop") {
    //                 const sure = window.confirm("Are you sure you want to stop the server?");
    //                 if (!sure) {
    //                     return;
    //                 }
    //             }
    //             sendCommand(messageFieldInput.value);
    //             messageFieldInput.value = "";
    //         }
    //     }

    //     if (!(await isForbidden("execute commands"))) {
    //         messageFieldDiv.classList.remove("disabled");

    //         messageFieldInput.addEventListener("keypress", (event) => onEnter(event));
    //     }

    //     if (!(await isForbidden("read logs history"))) {
    //         try {
    //             const response = await fetch(Constants.LOGS_HISTORY_URL);
    //             if (!response.ok) {
    //                 ToastSystem.addToQueue(`Error: ${response.statusText}`, ToastSystem.ToastType.ERROR);
    //                 return;
    //             }

    //             const logs = await response.json();
    //             logs.forEach((log: string) => {
    //                 addLog(log);
    //                 totalMessages.innerHTML = logs.length.toString();
    //             });
    //         } catch (error) {
    //             ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
    //         }
    //     }

    //     if (!(await isForbidden("read real time logs", consoleScroll))) {
    //         const ws = new WebSocket(Constants.CONSOLE_URL);
    //         ws.onmessage = (event) => {
    //             if (!event.data.includes("Connected to /")) {
    //                 addLog(event.data);
    //                 totalMessages.innerHTML = consoleScroll.children.length.toString();
    //             }
    //         };

    //         ws.onclose = () => {
    //             messageFieldInput.removeEventListener("keypress", (event) => onEnter(event));

    //             if (!document.hidden) {
    //                 ToastSystem.addToQueue("Connection closed", ToastSystem.ToastType.ERROR);
    //                 ToastSystem.addToQueue("Reconnecting... In 30 seconds", ToastSystem.ToastType.ERROR);
    //                 setTimeout(() => {
    //                     connect();
    //                     console.log("Reconnecting...");
    //                 }, 30000);
    //             }
    //         };

    //         ws.onerror = () => {
    //             ToastSystem.addToQueue("Connection error", ToastSystem.ToastType.ERROR);
    //         };

    //         const visibilitychangeListener = (_: Event) => {
    //             if (document.hidden) {
    //                 ws.close();
    //             } else {
    //                 connect();
    //                 document.removeEventListener("visibilitychange", visibilitychangeListener);
    //             }
    //         };
    //         document.addEventListener("visibilitychange", visibilitychangeListener);

    //         sendCommand = function (command: string) {
    //             ws.send(command);
    //         };
    //     }
    // }

    // async function connectStats() {
    //     cpuLoadValue.innerHTML = "none";
    //     ramUsageValue.innerHTML = "none";
    //     uptimeValue.innerHTML = "none";
    //     playersValue.innerHTML = "none";
    //     playersMaxValue.innerHTML = "none";
    //     ipAddrValue.innerHTML = "none";
    //     tpsValue.innerHTML = "none";
    //     serverTimeValue.innerHTML = "none";

    //     if (!(await isForbidden("access to server stats", statsDiv))) {
    //         const ws = new WebSocket(Constants.STATS_URL);
    //         ws.onmessage = (event) => {
    //             if (!event.data.includes("Connected to /")) {
    //                 //console.log(event.data);
    //                 const jsonData = JSON.parse(event.data);

    //                 cpuLoadValue.innerHTML = jsonData.cpuUsage != null ? (jsonData.cpuUsage * 100).toString().split(".")[0] + "%" : "<a href='https://modrinth.com/mod/spark'>Needs Spark</a>";
    //                 ramUsageValue.innerHTML = jsonData.memoryUsage.toString().split(".")[0] + "%";
    //                 uptimeValue.innerHTML = new Date(jsonData.uptime).toISOString().slice(11, -1).split(".")[0];
    //                 playersValue.innerHTML = jsonData.playerCount;
    //                 playersMaxValue.innerHTML = jsonData.maxPlayerCount;
    //                 ipAddrValue.innerHTML = jsonData.ip;
    //                 tpsValue.innerHTML = jsonData.tps != null ? Math.floor(jsonData.tps).toString() : "<a href='https://modrinth.com/mod/spark'>Needs Spark</a>";
    //                 serverTimeValue.innerHTML = jsonData.serverTime.split(".")[0].split("T")[1];
    //             }
    //         };

    //         ws.onclose = () => {
    //             if (!document.hidden) {
    //                 setTimeout(() => {
    //                     connectStats();
    //                 }, 30000);
    //             }
    //         };

    //         ws.onerror = (error) => {
    //             console.error(error);
    //         };

    //         const visibilitychangeListener = () => {
    //             if (document.hidden) {
    //                 ws.close();
    //             } else {
    //                 connectStats();
    //                 document.removeEventListener("visibilitychange", visibilitychangeListener);
    //             }
    //         };
    //         document.addEventListener("visibilitychange", visibilitychangeListener);
    //     }
    // }

    // onMount(() => {
    //     consoleScroll.addEventListener("scroll", () => {
    //         allowAutoScroll = consoleScroll.scrollTop + consoleScroll.clientHeight >= consoleScroll.scrollHeight - 5;
    //     });

    //     connect();
    //     connectStats();
    // });
</script>

<div id="console-tab" style="display: block; /* DEFAULT TAB */">
    <!-- ! DEFAULT TAB -->
    <div class="container flex-col">
        <h1 class="flex content-center justify-center text-[2.215rem]">Console</h1>
        <div class="flex h-full w-full justify-center">
            <StatsWindow />
            <ConsoleWindow />
        </div>
    </div>
</div>
