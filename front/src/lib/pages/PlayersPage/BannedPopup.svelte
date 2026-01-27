<script lang="ts">
    import Constants from "../../constants";
    import ToastSystem from "../../toastSystem";

    let container: HTMLDivElement;
    let addresses: boolean = false;

    let key = 0;

    export function show() {
        container.classList.remove("hidden");
    }

    export function update() {
        key++;
    }
</script>

<div class="absolute top-0 flex hidden h-full w-screen items-center justify-center" bind:this={container}>
    <div class="absolute z-[99] h-full w-screen backdrop-blur-[20px]"></div>
    <div class="absolute z-[100] items-center rounded-[10px] bg-[var(--players-background-color)] pl-px">
        <div class="flex w-[300px] flex-col items-center gap-[5px] p-[10px]">
            <h1>Banned</h1>

            <div class="flex gap-[10px]">
                <button class="w-[80px] rounded-[5px]" class:bg-[var(--players-buttons-bg-color)]={!addresses} on:click={() => (addresses = false)}>Players</button>
                <button class="w-[80px] rounded-[5px]" class:bg-[var(--players-buttons-bg-color)]={addresses} on:click={() => (addresses = true)}>Addresses</button>
            </div>

            {#key key}
                <ul class="max-h-[500px] w-full overflow-auto py-[5px]" class:hidden={addresses}>
                    <li class="flex items-center justify-center [&:not(:only-child)]:hidden">No banned players</li>
                    {#await fetch(Constants.BAN_LIST_PLAYER_URL).then((r) => r.json())}}
                    {:then list}
                        {#each list as target}
                            <li class="mt-[5px] flex w-full justify-around">
                                <span>{target.target}</span>
                                <button
                                    class="rounded-[5px] bg-[var(--players-pardon-button-bg-color)]"
                                    on:click={() => {
                                        fetch(Constants.PARDON_PLAYER_URL, {
                                            method: "POST",
                                            headers: {
                                                "Content-Type": "application/json",
                                            },
                                            body: JSON.stringify({
                                                username: target.target,
                                            }),
                                        }).then((res) => {
                                            if (res.status === 200) {
                                                ToastSystem.addToQueue(`Pardon player ${target.target}`, ToastSystem.ToastType.INFO);
                                                key++;
                                            } else {
                                                ToastSystem.addToQueue(`Fail on pardon target ${res.statusText}`, ToastSystem.ToastType.ERROR);
                                            }
                                        });
                                    }}>pardon</button
                                >
                            </li>
                        {/each}
                    {/await}
                </ul>
                <ul class="max-h-[500px] w-full overflow-auto py-[5px]" class:hidden={!addresses}>
                    <li class="flex items-center justify-center [&:not(:only-child)]:hidden">No banned addresses</li>
                    {#await fetch(Constants.BAN_IP_LIST_PLAYER_URL).then((r) => r.json())}}
                    {:then list}
                        {#each list as target}
                            <li class="mt-[5px] flex w-full justify-around">
                                <span>{target.target}</span>
                                <button
                                    class="rounded-[5px] bg-[var(--players-pardon-button-bg-color)]"
                                    on:click={() => {
                                        fetch(Constants.PARDON_IP_PLAYER_URL, {
                                            method: "POST",
                                            headers: {
                                                "Content-Type": "application/json",
                                            },
                                            body: JSON.stringify({
                                                ip: target.target,
                                            }),
                                        }).then((res) => {
                                            if (res.status === 200) {
                                                ToastSystem.addToQueue(`Pardon address ${target.target}`, ToastSystem.ToastType.INFO);
                                                key++;
                                            } else {
                                                ToastSystem.addToQueue(`Fail on pardon target ${res.statusText}`, ToastSystem.ToastType.ERROR);
                                            }
                                        });
                                    }}>pardon</button
                                >
                            </li>
                        {/each}
                    {/await}
                </ul>
            {/key}

            <button
                class="rounded-[5px] bg-[var(--players-buttons-bg-color)] px-[4px]"
                on:click={() => {
                    container.classList.add("hidden");
                }}>Cancel</button
            >
        </div>
    </div>
</div>

<style lang="scss">
    @use "../../../styles/hover-holo-effect";
    @use "../../../styles/scrollbar";

    button {
        @include hover-holo-effect.hover-holo-effect;
    }

    ul {
        @include scrollbar.scrollbar;
    }
</style>
