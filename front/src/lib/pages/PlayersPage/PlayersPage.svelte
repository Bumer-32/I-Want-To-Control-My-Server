<script lang="ts">
    import Online from "./Online.svelte";
    import BannedPopup from "./BannedPopup.svelte";

    let banned: BannedPopup;
</script>

<div class="flex flex-col">
    <h1 class="flex content-center justify-center text-[36px]">Players</h1>
    <BannedPopup bind:this={banned}/>
<!--    <InventoryPopup/>-->
    <div class="page-container relative overflow-auto gap-[150px]">
        <button
                class="absolute top-[25px] rounded-[5px] bg-[var(--players-buttons-bg-color)] px-[4px]"
                on:click={() => {
                    banned.show();
                }}
        >Banned</button>
        <Online bannedPopup={banned} />
    </div>
</div>

<style lang="scss">
  @use "../../../styles/scrollbar";

  .page-container {
    @include  scrollbar.scrollbar;
  }
</style>