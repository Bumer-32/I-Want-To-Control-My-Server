<script lang="ts">
    import { onMount } from "svelte";
    import Constants from "../../../scripts/constants";
    import ToastSystem from "../../../scripts/toastSystem";

    export let updateUsers: () => void;

    let form: HTMLFormElement;

    export function show() {
        form.classList.remove("hidden");
    }

    onMount(() => {
        form.addEventListener("submit", (event) => {
            event.preventDefault();
            const formData = new FormData(form);
            const username = formData.get("username") as string;
            const password = formData.get("password") as string;
            const passwordConfirm = formData.get("password_confirm") as string;

            if (password !== passwordConfirm) {
                alert("Passwords don't match!");
                return;
            }

            fetch(Constants.CREATE_USER_URL, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    username: username,
                    password: password,
                    admin: false,
                    permissions: [],
                }),
            }).then(async (res) => {
                if (res.status === 200) {
                    form.reset();
                    form.classList.add("hidden");
                    updateUsers();

                    ToastSystem.addToQueue(`User ${username} has been successfully created`, ToastSystem.ToastType.INFO);
                } else {
                    ToastSystem.addToQueue(`Error while creating user (${res.status}): ${await res.text()}`, ToastSystem.ToastType.ERROR);
                }
            });
        });
    });
</script>

<form class="mt-[10px] flex hidden w-[320px] flex-col items-center rounded-[10px] bg-[var(--users-background-color)] py-[2px]" bind:this={form}>
    <h1>User creation</h1>
    <label for="username" class="mt-[5px]">Username</label>
    <input class="rounded-[5px] bg-[var(--users-buttons-bg-color)] text-[var(--users-background-color)]" type="text" name="username" required />

    <label for="password" class="mt-[15px]">Password</label>
    <input class="rounded-[5px] bg-[var(--users-buttons-bg-color)] text-[var(--users-background-color)]" type="password" name="password" required />

    <label for="password_confirm" class="mt-[3px]">Confirm Password</label>
    <input class="rounded-[5px] bg-[var(--users-buttons-bg-color)] text-[var(--users-background-color)]" type="password" name="password_confirm" required />

    <div class="mt-[15px] mb-[10px]">
        <button
            class="rounded-[5px] bg-[var(--users-buttons-bg-color)] px-[4px] text-[var(--users-background-color)]"
            type="reset"
            on:click={() => {
                form.classList.add("hidden");
            }}>Cancel</button
        >
        <button class="rounded-[5px] bg-[var(--users-buttons-bg-color)] px-[4px] text-[var(--users-background-color)]" type="submit">Submit</button>
    </div>
</form>

<style lang="scss">
    @use "../../../styles/hover-holo-effect";

    button,
    input {
        @include hover-holo-effect.hover-holo-effect;
    }
</style>
