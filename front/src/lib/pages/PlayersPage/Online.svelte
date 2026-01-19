<script lang="ts">
    import Player from "./Player.svelte";
    import { onMount } from "svelte";
    import Constants from "../../constants";
    import ToastSystem from "../../toastSystem";
    import { PlayerData } from "./playerData";
    import BannedPopup from "./BannedPopup.svelte";

    export let bannedPopup: BannedPopup;

    let players: PlayerData.PlayerData[] = [];

    async function connect() {
        const ws = new WebSocket(Constants.PLAYERS_URL);

        ws.onmessage = (event) => {
            const jsonData = JSON.parse(event.data);
            const isAllData = jsonData.isAllData;
            const playersArr = jsonData.players as Array<PlayerData.PlayerData>;
            if (isAllData) {
                players = playersArr;
            } else {
                const newPlayer = playersArr[0];
                const username = newPlayer.username;
                const player = players.find((player) => player.username === username)!;

                if (player.inventory !== null && newPlayer.inventory === null) {
                    newPlayer.inventory = player.inventory;
                }

                players[players.indexOf(player)] = newPlayer;
            }
        };

        ws.onclose = () => {
            if (document.hidden) {
                setTimeout(() => {
                    connect();
                }, 30000);
            }
        };

        ws.onerror = (error) => {
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

<div class="grid-rows-[minmax(100px, auto)] grid gap-[10px] rounded-[10px]" class:grid-cols-1={players.length === 1} class:grid-cols-2={players.length === 2} class:grid-cols-3={players.length >= 3}>
    <span class="[&:not(:only-child)]:hidden">There's no players connected to server</span>
    {#each players as player}
        <Player player={player} bannedPopup={bannedPopup} />
    {/each}
</div>

<style lang="scss"></style>
