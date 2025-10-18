<script lang="ts">
    import { onMount } from "svelte";
    import type { UserData } from "../../../scripts/userData";
    import Constants from "../../../scripts/constants";
    import ToastSystem from "../../../scripts/toastSystem";

    export let user: UserData;
    export let currentUser: string
    export let permissionsList: string[];

    let newUser = structuredClone(user);
    let expandableHeight = 0
    let permissionsExpandableHeight = 0

    let permissions: Record<string, HTMLSelectElement> = {}

    let root: HTMLLIElement;
    let expandButton: HTMLSpanElement;
    let expandable: HTMLDivElement;
    let adminSelect: HTMLSelectElement;
    let permissionsExpandable: HTMLDivElement;

    onMount(() => {
        setTimeout(() => {
            expandable.style.maxHeight = "none";
            expandableHeight = expandable.offsetHeight;
            permissionsExpandableHeight = permissionsExpandable.offsetHeight;
            // console.log(expandableHeight)

            if (!user.admin) permissionsExpandable.classList.add("active");

            updateExpandableHeight();
            updatePermissionsExpandableHeight();
        }, 100);
    });

    function updateExpandableHeight() {
        if (expandable.classList.contains("active")) {
            expandable.style.maxHeight = `${expandableHeight}px`;
        } else {
            expandable.style.maxHeight = "0px";
        }
    }

    function updatePermissionsExpandableHeight() {
        if (permissionsExpandable.classList.contains("active")) {
            permissionsExpandable.style.maxHeight = `${permissionsExpandableHeight}px`;
        } else {
            permissionsExpandable.style.maxHeight = "0px";
        }
    }
</script>


<li class="bg-[var(--users-background-color)] rounded-[10px] w-[320px] py-[2px] mt-[10px] relative" bind:this={root}>
    <div class="flex justify-center items-center">
        <button
            class="material-symbols-rounded absolute left-[5px] [&.active]:rotate-90"
            style="transition: rotate 0.3s ease"
            bind:this={expandButton}
            on:click={() => {
                expandButton.classList.toggle("active");
                expandable.classList.toggle("active");

                updateExpandableHeight();
            }}
        >expand_circle_right</button>
        {user.username}
    </div>
    <div class="overflow-hidden" style="transition: max-height 0.3s ease" bind:this={expandable}>

        <div>
            <h3 class="pl-[10px] mt-[5px]">General</h3>
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
                    updatePermissionsExpandableHeight()
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

        <div class="overflow-hidden" style="transition: max-height 0.3s ease" bind:this={permissionsExpandable}>
            <h3 class="pl-[10px] mt-[15px]">Permissions</h3>
            <hr class="mx-[10px]" />

            <div class="pl-[15px] pt-[10px]">
                {#each permissionsList as permission}
                    {permission}
                    ->
                    <select
                        class="rounded-[5px] bg-[var(--users-buttons-bg-color)] text-[var(--users-background-color)] outline-none"
                        bind:this={permissions[permission]}
                        on:change={() => {
                            if (permissions[permission].value === "true") {
                                if (!newUser.permissions.includes(permission)) newUser.permissions.push(permission);
                            } else newUser.permissions = newUser.permissions.filter(per => per !== permission);
                        }}
                    >
                        {#if !user.permissions.includes(permission)}<option selected>false</option>{:else}<option>false</option>{/if}
                        {#if user.permissions.includes(permission)}<option selected>true</option>{:else}<option>true</option>{/if}
                    </select>

                    <br />
                {/each}
            </div>
        </div>

        <div class="flex justify-end mt-[5px] relative">
            {#if user.username === currentUser}
                <button class="bg-[var(--users-disabled-delete-button-bg-color)] text-[var(--users-buttons-bg-color)] rounded-[5px] ml-[5px] mb-[5px] px-[4px] absolute left-0" disabled>Delete</button>
            {:else}
                <button class="bg-[var(--users-delete-button-bg-color)] text-[var(--users-buttons-bg-color)] rounded-[5px] ml-[5px] mb-[5px] px-[4px] absolute left-0"
                        on:click={() => {
                            if (window.confirm("Are you sure you want to delete this user?")) {
                                fetch(Constants.DELETE_USER_URL, {
                                    method: "DELETE",
                                    headers: {
                                      "Content-Type": "application/json"
                                    },
                                    body: JSON.stringify({
                                        username: user.username
                                    })
                                }).then(res => {
                                    if (res.status === 200) root.remove()
                                    else {
                                        ToastSystem.addToQueue(`Error while deleting user: ${res.statusText}`, ToastSystem.ToastType.ERROR);

                                        expandButton.classList.remove("active");
                                        expandable.classList.remove("active");
                                        updateExpandableHeight();
                                    }
                                })
                            }
                        }}
                >Delete</button>
            {/if}

            <button class="bg-[var(--users-buttons-bg-color)] text-[var(--users-background-color)] rounded-[5px] mr-[5px] mb-[5px] px-[4px]"
                    on:click={() => {
                        expandButton.classList.remove("active");
                        expandable.classList.remove("active");
                        updateExpandableHeight();

                        newUser = structuredClone(user);

                        adminSelect.value = user.admin.toString();

                        Object.entries(permissions).forEach(([permission, select]) => {
                            select.value = user.permissions.includes(permission).toString();
                        });
                    }}
            >Cancel</button>

            <button class="bg-[var(--users-buttons-bg-color)] text-[var(--users-background-color)] rounded-[5px] mr-[5px] mb-[5px] px-[4px]"
                    on:click={async () => {
                        user = structuredClone(newUser);

                        expandButton.classList.remove("active");
                        expandable.classList.remove("active");
                        updateExpandableHeight();

                        let finalPermissions: String[];

                        if(newUser.admin) finalPermissions = [];
                        else finalPermissions = newUser.permissions

                        const response = await fetch(Constants.EDIT_PERMISSIONS_URL, {
                            method: "PUT",
                            headers: {
                                "Content-Type": "application/json",
                            },
                            body: JSON.stringify(
                                {
                                    "username": newUser.username,
                                    "permissions": finalPermissions,
                                }
                            )
                        })

                        ToastSystem.addToQueue(await response.text(), ToastSystem.ToastType.INFO)
                    }}
            >Save</button>
        </div>
    </div>
</li>
