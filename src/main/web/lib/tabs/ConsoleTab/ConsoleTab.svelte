<script lang="ts">
    import ConsoleWindow from "./ConsoleWindow.svelte";
    import StatsWindow from "./StatsWindow.svelte";
</script>

<div id="console-tab" style="display: block; /* DEFAULT TAB */">
    <!-- ! DEFAULT TAB -->
    <div class="tab-container flex-col">
        <h1 class="flex content-center justify-center text-[36px]">Console</h1>
        <div class="flex h-full w-full justify-center">
            <StatsWindow />
            <ConsoleWindow />
        </div>
    </div>
</div>
