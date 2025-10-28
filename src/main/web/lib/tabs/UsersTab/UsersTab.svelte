<script lang="ts">
    import { onMount } from "svelte";
    import { checkAuth } from "../../../scripts/auth";
    import User from "./User.svelte";
    import Constants from "../../../scripts/constants";
    import type { UserData } from "../../../scripts/userData";
    import CreateUser from "./CreateUser.svelte";

    let createUser: CreateUser

    let users: UserData[];
    let permissionsList: string[];

    let key = 0;

    async function updateUsers() {
        users = await fetch(Constants.USERS_LIST_URL).then((res) => res.json());
        key++;
    }

    onMount(async () => {
        users = await fetch(Constants.USERS_LIST_URL).then((res) => res.json());
        permissionsList = await fetch(Constants.PERMISSIONS_LIST_URL).then((res) => res.json());
    });
</script>

<div id="users-tab" class="tab disabled">
    <div class="tab-container flex flex-col">
        <button class="create-user-button mt-[15px] rounded-[10px] bg-[var(--users-background-color)] px-[20px] py-[2px] active:px-[17px] active:py-[0px]" on:click={() => {createUser.show()}}>create user</button>

        <div class="users-container flex h-full w-full flex-col items-center justify-start overflow-y-scroll mt-[25px]">
            <CreateUser updateUsers={updateUsers} bind:this={createUser} />
            <ul>
                {#await checkAuth()}
                    ...
                {:then currentUser}
                    {#key key}
                        {#each users as user}
                            <User user={user} currentUser={currentUser ?? ""} permissionsList={permissionsList} />
                        {/each}
                    {/key}
                {/await}
            </ul>
        </div>
    </div>
</div>

<style lang="scss">
    @use "../../../styles/scrollbar";
    @use "../../../styles/hover-holo-effect";

    .users-container {
        @include scrollbar.scrollbar;
    }

    .create-user-button {
        @include hover-holo-effect.hover-holo-effect
    }
</style>
