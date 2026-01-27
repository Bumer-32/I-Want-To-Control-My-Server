<script lang="ts">
    import Constants from "../../constants";
    import ToastSystem from "../../toastSystem";
    import { PlayerData } from "./playerData";
    import BannedPopup from "./BannedPopup.svelte";

    export let player: PlayerData.PlayerData;
    export let bannedPopup: BannedPopup;

    function capitalize(s: string): string {
        return s ? s[0].toUpperCase() + s.slice(1).toLowerCase() : s;
    }
</script>

<div class="flex justify-between gap-[10px] rounded-[5px] bg-[var(--players-background-color)] p-[5px]">
    <div class="flex flex-col">
        <img src="https://vzge.me/face/64/{player.username}.png" class="h-[64px] w-[64px] rounded-[10px]" alt="H" />
        <!--I tried to change UA as matched in service notice, but I can't do it in browser and I don't want to write request in ktor for this-->
        <div class="flex w-[70px] flex-col">
            <span>x: {Math.round(player.pos.x)}</span>
            <span class="mt-[-10px]">y: {Math.round(player.pos.y)}</span>
            <span class="mt-[-10px]">z: {Math.round(player.pos.z)}</span>
        </div>
    </div>

    <div class="flex flex-col justify-between">
        <div class="flex flex-col items-center">
            <span>{player.username}</span>
            <span class="mt-[-10px]">[{capitalize(player.gameMode.toLowerCase())}]</span>
            <span class="mt-[-8px]">({capitalize(PlayerData.PermissionLevel[player.permissionLevel].toLowerCase())})</span>
        </div>
        <div class="flex flex-col gap-[5px]">
            <button
                class="rounded-[5px] bg-[var(--players-buttons-bg-color)] px-[4px]"
                on:click={() => {
                    if (player.permissionLevel !== PlayerData.PermissionLevel.Owner) {
                        if (window.confirm(`Are you sure want to op ${player.username}?`)) {
                            fetch(Constants.OP_PLAYER_URL, {
                                method: "POST",
                                headers: {
                                    "Content-Type": "application/json",
                                },
                                body: JSON.stringify({
                                    username: player.username,
                                }),
                            }).then((res) => {
                                if (res.status === 200) ToastSystem.addToQueue(`${player.username} now an operator`, ToastSystem.ToastType.INFO);
                                else {
                                    ToastSystem.addToQueue(`Error while op ${res.statusText}`, ToastSystem.ToastType.ERROR);
                                }
                            });
                        }
                    } else {
                        fetch(Constants.DEOP_PLAYER_URL, {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                            },
                            body: JSON.stringify({
                                username: player.username,
                            }),
                        }).then((res) => {
                            if (res.status === 200) ToastSystem.addToQueue(`${player.username} are no longer an operator`, ToastSystem.ToastType.INFO);
                            else {
                                ToastSystem.addToQueue(`Error while deopping ${res.statusText}`, ToastSystem.ToastType.ERROR);
                            }
                        });
                    }
                }}
            >
                {#if player.permissionLevel !== PlayerData.PermissionLevel.Owner}
                    Op
                {:else}
                    Deop
                {/if}
            </button>
            <!--            <button class="bg-[var(&#45;&#45;players-buttons-bg-color)] rounded-[5px] px-[4px]">Inventory</button>-->
        </div>
    </div>

    <div class="flex flex-col justify-end gap-[5px]">
        <button
            class="rounded-[5px] bg-[var(--players-buttons-bg-color)] px-[4px]"
            on:click={() => {
                fetch(Constants.KILL_PLAYER_URL, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        username: player.username,
                    }),
                }).then((res) => {
                    if (res.status === 200) ToastSystem.addToQueue(`Killed ${player.username}`, ToastSystem.ToastType.INFO);
                    else {
                        ToastSystem.addToQueue(`Error when killing user ${res.statusText}`, ToastSystem.ToastType.ERROR);
                    }
                });
            }}>Kill</button
        >
        <button
            class="rounded-[5px] bg-[var(--players-buttons-bg-color)] px-[4px]"
            on:click={() => {
                let reason = window.prompt("Reason?");
                fetch(Constants.KICK_PLAYER_URL, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        username: player.username,
                        reason: reason,
                    }),
                }).then((res) => {
                    if (res.status === 200) ToastSystem.addToQueue(`Kicked ${player.username}`, ToastSystem.ToastType.INFO);
                    else {
                        ToastSystem.addToQueue(`Error when kicking user ${res.statusText}`, ToastSystem.ToastType.ERROR);
                    }
                });
            }}>Kick</button
        >
        <button
            class="rounded-[5px] bg-[var(--players-buttons-bg-color)] px-[4px]"
            on:click={() => {
                let reason = window.prompt("Reason?");
                fetch(Constants.BAN_PLAYER_URL, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        username: player.username,
                        reason: reason,
                    }),
                }).then((res) => {
                    if (res.status === 200) {
                        bannedPopup.update();
                        ToastSystem.addToQueue(`Banned ${player.username}`, ToastSystem.ToastType.INFO);
                    } else {
                        ToastSystem.addToQueue(`Error when banning user ${res.statusText}`, ToastSystem.ToastType.ERROR);
                    }
                });
            }}>Ban</button
        >
        <button
            class="rounded-[5px] bg-[var(--players-buttons-bg-color)] px-[4px]"
            on:click={() => {
                let reason = window.prompt("Reason?");
                fetch(Constants.BAN_IP_PLAYER_URL, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        username: player.username,
                        reason: reason,
                    }),
                }).then((res) => {
                    if (res.status === 200) {
                        bannedPopup.update();
                        ToastSystem.addToQueue(`Ip banned ${player.username}`, ToastSystem.ToastType.INFO);
                    } else {
                        ToastSystem.addToQueue(`Error when ip banning user ${res.statusText}`, ToastSystem.ToastType.ERROR);
                    }
                });
            }}>Ban Ip</button
        >
    </div>
</div>

<style lang="scss">
    @use "../../../styles/hover-holo-effect";

    button {
        @include hover-holo-effect.hover-holo-effect;
    }
</style>
