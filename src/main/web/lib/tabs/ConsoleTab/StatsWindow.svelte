<script lang="ts">
    import { onMount } from "svelte";
    import { isAllowed } from "../../../scripts/auth";
    import Constants from "../../../scripts/constants";
    import ToastSystem from "../../../scripts/toastSystem";

    let statsDiv: HTMLDivElement;
    let cpuLoadValue: HTMLSpanElement;
    let ramUsageValue: HTMLSpanElement;
    let uptimeValue: HTMLSpanElement;
    let playersValue: HTMLSpanElement;
    let playersMaxValue: HTMLSpanElement;
    let ipAddrValue: HTMLSpanElement;
    let tpsValue: HTMLSpanElement;
    let serverTimeValue: HTMLSpanElement;

    async function connect() {
        cpuLoadValue.innerHTML = "none";
        ramUsageValue.innerHTML = "none";
        uptimeValue.innerHTML = "none";
        playersValue.innerHTML = "none";
        playersMaxValue.innerHTML = "none";
        ipAddrValue.innerHTML = "none";
        tpsValue.innerHTML = "none";
        serverTimeValue.innerHTML = "none";

        if (!(await isAllowed("access to server stats"))) {
            statsDiv.classList.add("forbidden");
            return;
        }

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
                    connect();
                }, 30000);
            }
        };

        ws.onerror = (error) => {
            console.error(error);
            ToastSystem.addToQueue(`Error: ${error}`, ToastSystem.ToastType.ERROR);
        };

        const visibilitychangeListener = () => {
            if (document.hidden) {
                ws.close();
            } else {
                connect();
                document.removeEventListener("visibilitychange", visibilitychangeListener);
            }
        };
        document.addEventListener("visibilitychange", visibilitychangeListener);
    }

    onMount(() => {
        connect();
    });
</script>

<div class="statistics" bind:this={statsDiv}>
    <h3>Statistics</h3>
    <div class="cpu-load">
        <span>CPU: </span>
        <span class="value" bind:this={cpuLoadValue}>none</span>
    </div>
    <div class="ram-usage">
        <span>RAM: </span>
        <span class="value" bind:this={ramUsageValue}>none</span>
    </div>
    <div class="uptime">
        <span>Uptime: </span>
        <span class="value" bind:this={uptimeValue}>none</span>
    </div>
    <div class="players">
        <span>Players: </span>
        <span class="value" bind:this={playersValue}>none</span>
        <span>/</span>
        <span class="max" bind:this={playersMaxValue}>none</span>
    </div>
    <div class="ip-addr">
        <span>IP: </span>
        <span class="value spoiler" bind:this={ipAddrValue}>none</span>
    </div>
    <div class="tps">
        <span>TPS: </span>
        <span class="value" bind:this={tpsValue}>none</span>
    </div>
    <div class="server-time">
        <span>Server time: </span>
        <span class="value" bind:this={serverTimeValue}>none</span>
    </div>
</div>

<style lang="scss">
    @use "../../../styles/forbidden";

    .statistics {
        background-color: var(--console-background-color);
        padding: 10px;
        border-radius: 10px;
        height: fit-content;
        margin-left: 60px;
        width: 250px;
        min-width: 175px;

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

    @include forbidden.forbidden();
</style>
