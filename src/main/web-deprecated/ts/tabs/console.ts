// noinspection JSUnusedLocalSymbols
import { Constants } from "../constants.js";
import { isForbidden } from "../supply.js";
import { ToastSystem } from "../toastSystem.js";

let allowAutoScroll = true;
const consoleEl = document.querySelector(
  ".container .tabs #console-tab .container .console .console-text",
) as HTMLDivElement;

export default async function consoleInit() {
  autoScroll();
  await connect();
  await connectStats();
}

function autoScroll() {
  consoleEl.addEventListener("scroll", () => {
    allowAutoScroll =
      consoleEl.scrollTop + consoleEl.clientHeight >=
      consoleEl.scrollHeight - 5;
  });
}

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

  consoleEl.appendChild(log);

  if (allowAutoScroll) {
    consoleEl.scrollTo({
      top: consoleEl.scrollHeight,
      behavior: "smooth",
    });
  }
}

export let sendCommand = function (command: string) {};

async function connect() {
  const logsCount = document.querySelector(
    "#console-tab > .container > .windows > .console > .total-messages > .count",
  ) as HTMLSpanElement;
  const inputDiv = document.querySelector(
    "#console-tab > .container > .windows > .console > .input",
  ) as HTMLDivElement;
  const inputField = inputDiv.querySelector(".input-field") as HTMLInputElement;

  consoleEl.innerHTML = "";

  function onEnter(event: KeyboardEvent) {
    if (event.key === "Enter") {
      if (inputField.value.toLowerCase().trim() == "stop") {
        const sure = window.confirm(
          "Are you sure you want to stop the server?",
        );
        if (!sure) {
          return;
        }
      }
      sendCommand(inputField.value);
      inputField.value = "";
    }
  }

  if (!isForbidden("execute commands")) {
    inputDiv.classList.remove("disabled");

    inputField.addEventListener("keypress", (event) => onEnter(event));
  }

  if (!isForbidden("read logs history")) {
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
      if (!event.data.includes("Connected to /")) {
        addLog(event.data);
        logsCount.innerHTML = consoleEl.children.length.toString();
      }
    };

    ws.onclose = () => {
      inputField.removeEventListener("keypress", (event) => onEnter(event));

      if (!document.hidden) {
        ToastSystem.showInfo("Connection closed");
        ToastSystem.showInfo("Reconnecting... In 30 seconds");
        setTimeout(() => {
          connect();
          console.log("Reconnecting...");
        }, 30000);
      }
    };

    ws.onerror = () => {
      ToastSystem.showError("Connection error");
    };

    const visibilitychangeListener = (_: Event) => {
      if (document.hidden) {
        ws.close();
      } else {
        connect();
        document.removeEventListener(
          "visibilitychange",
          visibilitychangeListener,
        );
      }
    };
    document.addEventListener("visibilitychange", visibilitychangeListener);

    sendCommand = function (command: string) {
      ws.send(command);
    };
  }
}

async function connectStats() {
  const statsDiv = document.querySelector(
    "#console-tab > .container > .windows > .statistics",
  ) as HTMLDivElement;

  const cpuLoadValue = statsDiv.querySelector(
    ".cpu-load .value",
  ) as HTMLSpanElement;

  const ramUsageValue = statsDiv.querySelector(
    ".ram-usage .value",
  ) as HTMLSpanElement;

  const uptimeValue = statsDiv.querySelector(
    ".uptime .value",
  ) as HTMLSpanElement;

  const playersValue = statsDiv.querySelector(
    ".players .value",
  ) as HTMLSpanElement;
  const playersMaxValue = statsDiv.querySelector(
    ".players .max",
  ) as HTMLSpanElement;

  const ipAddrValue = statsDiv.querySelector(
    ".ip-addr .value",
  ) as HTMLSpanElement;

  const tpsValue = statsDiv.querySelector(".tps .value") as HTMLSpanElement;

  const serverTimeValue = statsDiv.querySelector(
    ".server-time .value",
  ) as HTMLSpanElement;

  cpuLoadValue.innerHTML = "none";
  ramUsageValue.innerHTML = "none";
  uptimeValue.innerHTML = "none";
  playersValue.innerHTML = "none";
  playersMaxValue.innerHTML = "none";
  ipAddrValue.innerHTML = "none";
  tpsValue.innerHTML = "none";
  serverTimeValue.innerHTML = "none";

  if (!isForbidden("access to server stats", statsDiv)) {
    const ws = new WebSocket(Constants.STATS_URL);
    ws.onmessage = (event) => {
      if (!event.data.includes("Connected to /")) {
        //console.log(event.data);
        const jsonData = JSON.parse(event.data);

        cpuLoadValue.innerHTML =
          jsonData.cpuUsage != null
            ? (jsonData.cpuUsage * 100).toString().split(".")[0] + "%"
            : "<a href='https://modrinth.com/mod/spark'>Needs Spark</a>";
        ramUsageValue.innerHTML =
          jsonData.memoryUsage.toString().split(".")[0] + "%";
        uptimeValue.innerHTML = new Date(jsonData.uptime)
          .toISOString()
          .slice(11, -1)
          .split(".")[0];
        playersValue.innerHTML = jsonData.playerCount;
        playersMaxValue.innerHTML = jsonData.maxPlayerCount;
        ipAddrValue.innerHTML = jsonData.ip;
        tpsValue.innerHTML =
          jsonData.tps != null
            ? Math.floor(jsonData.tps).toString()
            : "<a href='https://modrinth.com/mod/spark'>Needs Spark</a>";
        serverTimeValue.innerHTML = new Date(jsonData.serverTime)
          .toISOString()
          .slice(11, -1)
          .split(".")[0];
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
        document.removeEventListener(
          "visibilitychange",
          visibilitychangeListener,
        );
      }
    };
    document.addEventListener("visibilitychange", visibilitychangeListener);
  }
}
