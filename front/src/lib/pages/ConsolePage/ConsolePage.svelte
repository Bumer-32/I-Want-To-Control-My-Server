<script lang="ts">
    import ConsoleWindow from "./ConsoleWindow.svelte";
    import StatsWindow from "./StatsWindow.svelte";
</script>

<div>
    <h1 class="flex content-center justify-center text-[36px]">Console</h1>
    <div class="page-container flex-col">
        <div class="flex h-full w-full justify-center">
            <StatsWindow />
            <ConsoleWindow />
        </div>
    </div>
</div>
