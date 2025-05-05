<script lang="ts"></script>

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

<style lang="scss"></style>