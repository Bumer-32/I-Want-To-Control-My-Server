<script lang="ts">
    import { onMount } from "svelte";
    import { checkAuth } from "../../auth";
    import User from "./User.svelte";
    import Constants from "../../constants";
    import type { UserData } from "./userData";
    import CreateUserPopup from "./CreateUserPopup.svelte";

    let createUser: CreateUserPopup;

    let users: UserData[];
    let permissionsList: string[];

    async function updateUsers() {
        users = await fetch(Constants.USERS_LIST_URL).then((res) => res.json());
    }

    onMount(async () => {
        users = await fetch(Constants.USERS_LIST_URL).then((res) => res.json());
        permissionsList = await fetch(Constants.PERMISSIONS_LIST_URL).then((res) => res.json());
    });
</script>

<div>
    <h1 class="flex content-center justify-center text-[36px]">Users</h1>
    <CreateUserPopup updateUsers={updateUsers} bind:this={createUser} />
    <div class="page-container flex flex-col">
        <button
            class="create-user-button mt-[15px] rounded-[10px] bg-[var(--users-background-color)] px-[20px] py-[2px] active:px-[17px] active:py-[0px]"
            on:click={() => {
                createUser.show();
            }}>create user</button
        >

        <div class="users-container mt-[25px] flex h-full w-full flex-col items-center justify-start overflow-y-scroll">
            <ul>
                {#await checkAuth()}
                    ...
                {:then currentUser}
                    {#key users}
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
        @include hover-holo-effect.hover-holo-effect;
    }
</style>
