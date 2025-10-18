<script lang="ts">
    import { onMount } from "svelte";
    import { checkAuth } from "../../../scripts/auth";
    import User from "./User.svelte";
    import Constants from "../../../scripts/constants";
    import type { UserData } from "../../../scripts/userData";
    
    let users: UserData[];
    let permissionsList: string[];

    onMount(async () => {
        users = await fetch(Constants.USERS_LIST_URL).then((res) => res.json());
        permissionsList = await fetch(Constants.PERMISSIONS_LIST_URL).then((res) => res.json());
    })
</script>

<div id="users-tab" class="tab disabled">
    <div class="tab-container relative">
        <div class="bg-[var(--users-background-color)] rounded-[10px] px-[30px] flex justify-center items-center absolute top-[25px] flex-col">
            <h1>Current user</h1>
            <h2>
                {#await checkAuth()}
                    ...
                {:then user}
                    {user}
                {/await}
            </h2>
        </div>

        <div class="users-container absolute top-[100px] overflow-y-scroll w-full h-full flex items-center justify-start flex-col">
            <ul>
                {#await checkAuth()}
                    ...
                {:then currentUser}
                    {#each users as user}
                        <User user={user} currentUser={currentUser ?? ""} permissionsList={permissionsList} />
                    {/each}
                {/await}

            </ul>
        </div>
    </div>
</div>

<style lang="scss">
    @use "../../../styles/scrollbar";
    .users-container {
        @include scrollbar.scrollbar;
    }
</style>
