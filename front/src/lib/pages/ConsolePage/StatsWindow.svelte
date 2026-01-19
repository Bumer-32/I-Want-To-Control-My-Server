<script lang="ts">
    import { onMount } from "svelte";
    import { isAllowed } from "../../auth";
    import Constants from "../../constants";
    import ToastSystem from "../../toastSystem";

    let statsDiv: HTMLDivElement;
    let cpuLoadValue: HTMLSpanElement;
    let ramUsageValue: HTMLSpanElement;
    let uptimeValue: HTMLSpanElement;
    let playersValue: HTMLSpanElement;
    let playersMaxValue: HTMLSpanElement;
    let ipAddrValue: HTMLSpanElement;
    let serverVersionValue: HTMLSpanElement;
    let platformValue: HTMLSpanElement;
    let iwtcmsVersionValue: HTMLSpanElement;
    let tpsValue: HTMLSpanElement;
    let serverTimeValue: HTMLSpanElement;

    async function connect() {
        cpuLoadValue.innerHTML = "none";
        ramUsageValue.innerHTML = "none";
        uptimeValue.innerHTML = "none";
        playersValue.innerHTML = "none";
        playersMaxValue.innerHTML = "none";
        ipAddrValue.innerHTML = "none";
        serverVersionValue.innerHTML = "none";
        platformValue.innerHTML = "none";
        iwtcmsVersionValue.innerHTML = "none";
        tpsValue.innerHTML = "none";
        serverTimeValue.innerHTML = "none";

        if (!(await isAllowed("server.stats.read"))) {
            statsDiv.classList.add("forbidden");
            return;
        }

        const ws = new WebSocket(Constants.STATS_URL);
        ws.onmessage = (event) => {
            const jsonData = JSON.parse(event.data);

            cpuLoadValue.innerHTML = jsonData.cpuUsage != null ? (jsonData.cpuUsage * 100).toString().split(".")[0] + "%" : "<a href='https://modrinth.com/mod/spark'>Needs Spark</a>";
            ramUsageValue.innerHTML = jsonData.memoryUsage.toString().split(".")[0] + "%";
            uptimeValue.innerHTML = new Date(jsonData.uptime).toISOString().slice(11, -1).split(".")[0];
            playersValue.innerHTML = jsonData.playerCount;
            playersMaxValue.innerHTML = jsonData.maxPlayerCount;
            ipAddrValue.innerHTML = jsonData.ip;
            serverVersionValue.innerHTML = jsonData.serverVersion;
            platformValue.innerHTML = jsonData.platform;
            iwtcmsVersionValue.innerHTML = jsonData.iwtcmsVersion;
            tpsValue.innerHTML = jsonData.tps != null ? Math.floor(jsonData.tps).toString() : "<a href='https://modrinth.com/mod/spark'>Needs Spark</a>";
            serverTimeValue.innerHTML = jsonData.serverTime.split(".")[0].split("T")[1];
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
    <div>
        <span>CPU: </span>
        <span class="value" bind:this={cpuLoadValue}>none</span>
    </div>
    <div>
        <span>RAM: </span>
        <span class="value" bind:this={ramUsageValue}>none</span>
    </div>
    <div>
        <span>Uptime: </span>
        <span class="value" bind:this={uptimeValue}>none</span>
    </div>
    <div>
        <span>Players: </span>
        <span class="value" bind:this={playersValue}>none</span>
        <span>/</span>
        <span class="max" bind:this={playersMaxValue}>none</span>
    </div>
    <div>
        <span>IP: </span>
        <span class="value spoiler" bind:this={ipAddrValue}>none</span>
    </div>
    <div>
        <span>Server version: </span>
        <span class="value" bind:this={serverVersionValue}>none</span>
    </div>
    <div>
        <span>Platform: </span>
        <span class="value" bind:this={platformValue}>none</span>
    </div>
    <div>
        <span>IWTCMS version: </span>
        <span class="value" bind:this={iwtcmsVersionValue}>none</span>
    </div>
    <div>
        <span>TPS: </span>
        <span class="value" bind:this={tpsValue}>none</span>
    </div>
    <div>
        <span>Server time: </span>
        <span class="value" bind:this={serverTimeValue}>none</span>
    </div>
</div>

<style lang="scss">
    @use "../../../styles/spoiler";

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
</style>
