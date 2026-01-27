<script lang="ts">
    import { onMount } from "svelte";
    import type { UserData } from "./userData";
    import Constants from "../../constants";
    import ToastSystem from "../../toastSystem";

    export let user: UserData;
    export let currentUser: string;
    export let permissionsList: string[];

    let newUser = structuredClone(user);

    let permissions: Record<string, HTMLSelectElement> = {};

    let root: HTMLLIElement;
    let expandButton: HTMLSpanElement;
    let expandable: HTMLDivElement;
    let adminSelect: HTMLSelectElement;
    let permissionsExpandable: HTMLDivElement;

    onMount(() => {
        if (!user.admin) permissionsExpandable.classList.add("active");
    });

    function updateExpandableHeight() {
        // ! Important to expand permissions before expandable
        if (permissionsExpandable.classList.contains("active")) {
            permissionsExpandable.style.maxHeight = `${permissionsExpandable.scrollHeight}px`;
        } else {
            permissionsExpandable.style.transition = "max-height 0.3s ease";
            permissionsExpandable.style.maxHeight = "0px";
            setTimeout(() => {
                permissionsExpandable.style.transition = "";
                if (expandable.classList.contains("active")) expandable.style.maxHeight = `${expandable.scrollHeight}px`;
            }, 300); // 300ms == 0.3s -> same as transition
        }

        if (expandable.classList.contains("active")) {
            expandable.style.maxHeight = `${expandable.scrollHeight}px`;
        } else {
            expandable.style.maxHeight = "0px";
        }
    }
</script>

<li class="relative mt-[10px] w-[320px] rounded-[10px] bg-[var(--users-background-color)] py-[2px]" bind:this={root}>
    <div class="flex items-center justify-center">
        <button
            class="material-symbols-rounded absolute left-[5px] [&.active]:rotate-90"
            style="transition: rotate 0.3s ease"
            bind:this={expandButton}
            on:click={() => {
                expandButton.classList.toggle("active");
                expandable.classList.toggle("active");

                updateExpandableHeight();
            }}>expand_circle_right</button
        >
        {user.username}
        {#if user.username === currentUser}
            <span class="absolute right-[5px] text-[var(--users-current-text-color)]"> (current)</span>
        {/if}
    </div>
    <div class="max-h-0 overflow-hidden" style="transition: max-height 0.3s ease" bind:this={expandable}>
        <div>
            <h3 class="mt-[5px] pl-[10px]">General</h3>
            <hr class="mx-[10px]" />

            <span class="pl-[15px]">id: {user.id}</span>
            <br />

            <span class="pl-[15px]">admin: </span>
            <select
                class="rounded-[5px] bg-[var(--users-buttons-bg-color)] text-[var(--users-background-color)] outline-none"
                bind:this={adminSelect}
                on:change={() => {
                    newUser.admin = adminSelect.value === "true";

                    if (!newUser.admin) permissionsExpandable.classList.add("active");
                    else permissionsExpandable.classList.remove("active");

                    updateExpandableHeight();
                }}
            >
                {#if user.username === currentUser && user.admin}
                    <option>true</option>
                {:else}
                    {#if user.admin === false}<option selected>false</option>{:else}<option>false</option>{/if}
                    {#if user.admin === true}<option selected>true</option>{:else}<option>true</option>{/if}
                {/if}
            </select>
        </div>

        <div class="max-h-0 overflow-hidden" bind:this={permissionsExpandable}>
            <h3 class="mt-[15px] pl-[10px]">Permissions</h3>
            <hr class="mx-[10px]" />

            <div class="pt-[10px] pl-[15px]">
                {#each permissionsList as permission}
                    {permission}
                    ->
                    <select
                        class="rounded-[5px] bg-[var(--users-buttons-bg-color)] text-[var(--users-background-color)] outline-none"
                        bind:this={permissions[permission]}
                        on:change={() => {
                            if (permissions[permission].value === "true") {
                                if (!newUser.permissions.includes(permission)) newUser.permissions.push(permission);
                            } else newUser.permissions = newUser.permissions.filter((per) => per !== permission);
                        }}
                    >
                        {#if !user.permissions.includes(permission)}<option selected>false</option>{:else}<option>false</option>{/if}
                        {#if user.permissions.includes(permission)}<option selected>true</option>{:else}<option>true</option>{/if}
                    </select>

                    <br />
                {/each}
            </div>
        </div>

        <div class="relative mt-[5px] flex justify-end">
            {#if user.username === currentUser}
                <button class="absolute left-0 mb-[5px] ml-[5px] rounded-[5px] bg-[var(--users-disabled-delete-button-bg-color)] px-[4px] text-[var(--users-buttons-bg-color)]" disabled>Delete</button>
            {:else}
                <button
                    class="absolute left-0 mb-[5px] ml-[5px] rounded-[5px] bg-[var(--users-delete-button-bg-color)] px-[4px] text-[var(--users-buttons-bg-color)]"
                    on:click={() => {
                        if (window.confirm("Are you sure you want to delete this user?")) {
                            fetch(Constants.DELETE_USER_URL, {
                                method: "DELETE",
                                headers: {
                                    "Content-Type": "application/json",
                                },
                                body: JSON.stringify({
                                    username: user.username,
                                }),
                            }).then((res) => {
                                if (res.status === 200) root.remove();
                                else {
                                    ToastSystem.addToQueue(`Error while deleting user: ${res.statusText}`, ToastSystem.ToastType.ERROR);

                                    expandButton.classList.remove("active");
                                    expandable.classList.remove("active");
                                    updateExpandableHeight();
                                }
                            });
                        }
                    }}>Delete</button
                >
            {/if}

            <button
                class="mr-[5px] mb-[5px] rounded-[5px] bg-[var(--users-buttons-bg-color)] px-[4px] text-[var(--users-background-color)]"
                on:click={() => {
                    expandButton.classList.remove("active");
                    expandable.classList.remove("active");
                    updateExpandableHeight();

                    newUser = structuredClone(user);

                    adminSelect.value = user.admin.toString();

                    Object.entries(permissions).forEach(([permission, select]) => {
                        select.value = user.permissions.includes(permission).toString();
                    });

                    if (!newUser.admin) permissionsExpandable.classList.add("active");
                    else permissionsExpandable.classList.remove("active");
                }}>Cancel</button
            >

            <button
                class="mr-[5px] mb-[5px] rounded-[5px] bg-[var(--users-buttons-bg-color)] px-[4px] text-[var(--users-background-color)]"
                on:click={async () => {
                    user = structuredClone(newUser);

                    expandButton.classList.remove("active");
                    expandable.classList.remove("active");
                    updateExpandableHeight();

                    let finalPermissions: String[];

                    if (newUser.admin) finalPermissions = [];
                    else finalPermissions = newUser.permissions;

                    const response = await fetch(Constants.EDIT_PERMISSIONS_URL, {
                        method: "PUT",
                        headers: {
                            "Content-Type": "application/json",
                        },
                        body: JSON.stringify({
                            username: newUser.username,
                            permissions: finalPermissions,
                        }),
                    });

                    ToastSystem.addToQueue(await response.text(), ToastSystem.ToastType.INFO);
                }}>Save</button
            >
        </div>
    </div>
</li>

<style lang="scss">
    @use "../../../styles/hover-holo-effect";

    button {
        @include hover-holo-effect.hover-holo-effect;
    }
</style>
