<!--TODO: Rewrite to tailwind-->

<script lang="ts">
    import iwtcmsIcon from "../assets/icon_clearbg.png";
    import githubIcon from "../assets/github.svg";
    import modrinthIcon from "../assets/modrinth.svg";
    import { switchTab } from "../scripts/tabsController";
    import { onMount } from "svelte";
    import { logout } from "../scripts/auth";

    let leftDiv: HTMLDivElement;
    let rightDiv: HTMLDivElement;
    let buttonsDiv: HTMLDivElement;
    let colorModeSwitchInput: HTMLInputElement;

    function alignHeaderButtons() {
        const existingFillerDiv = document.querySelector("header .buttons .filler-div") as HTMLDivElement | null;
        if (existingFillerDiv != null) {
            existingFillerDiv.remove();
        }

        const leftCount = leftDiv!.children.length;
        const rightCount = rightDiv!.children.length;

        const fillerDiv = document.createElement("div");
        fillerDiv.style.width = "50px";
        fillerDiv.style.height = "50px";
        fillerDiv.style.display = "inline-block";
        fillerDiv.classList.add("filler-div");

        if (leftCount > rightCount) {
            rightDiv!.appendChild(fillerDiv);
        } else if (leftCount < rightCount) {
            rightDiv!.appendChild(fillerDiv);
        }
    }

    function handleAlignHeaderButtons() {
        const handleMutation = (_: MutationRecord[]) => {
            alignHeaderButtons();
        };

        const observer = new MutationObserver(handleMutation);

        observer.observe(buttonsDiv, {
            childList: true,
            attributes: false,
            subtree: false,
        });
    }

    function handleTabSwitching() {
        const buttons = document.querySelectorAll("header .buttons span") as NodeListOf<HTMLImageElement>;

        buttons.forEach((button) => {
            button.addEventListener("click", () => {
                switchTab(`${button.id.replace("header-", "")}-tab`);
            });
        });
    }

    onMount(() => {
        alignHeaderButtons();
        handleAlignHeaderButtons();
        handleTabSwitching();

        colorModeSwitchInput.onchange = () => {
            document.body.classList.toggle("light-mode-impl");
            localStorage.setItem("color-mode", colorModeSwitchInput.checked ? "light" : "dark");
        };

        if (localStorage.getItem("color-mode") == "light") {
            colorModeSwitchInput.checked = true;
            document.body.classList.add("light-mode-impl");
        }
    });
</script>

<header>
    <div class="menu">
        <label class="menu-button">
            <input type="checkbox" checked={false} />
            <img src={iwtcmsIcon} alt="IWTCMS logo" />
            <span class="menu-text">
                <span class="material-symbols-rounded">menu</span>
                <span class="text">menu</span>
            </span>
        </label>
        <div class="menu-items">
            <ul>
                <li>
                    <a href="https://modrinth.com/mod/i-want-to-control-my-server">
                        <img src={modrinthIcon} alt="modrinth" />
                        Modrinth
                    </a>
                </li>
                <li>
                    <a href="https://github.com/Bumer-32/I-Want-To-Control-My-Server">
                        <img src={githubIcon} alt="GitHub icon" />
                        GitHub
                    </a>
                </li>
                <li>
                    <a href="https://github.com/Bumer-32/I-Want-To-Control-My-Server/issues">
                        <span class="material-symbols-rounded">bug_report</span>
                        Bug tracker
                    </a>
                </li>
                <li>
                    <a href="files/iwtcms_client.zip">
                        <span class="material-symbols-rounded">download</span>
                        Python CLI
                    </a>
                </li>
                <li>
                    <button type="button" on:click={() => switchTab("users-tab")}>
                        <span class="material-symbols-rounded">account_circle</span>
                        Users
                    </button>
                </li>
                <li id="logout-button">
                    <button type="button" on:click={logout}>
                        <span class="material-symbols-rounded">logout</span>
                        Logout
                    </button>
                </li>
            </ul>
        </div>
    </div>

    <div class="buttons" bind:this={buttonsDiv}>
        <div class="left" bind:this={leftDiv}>
            <span class="material-symbols-rounded" id="header-settings">settings</span>
        </div>
        <div class="center">
            <!-- ! DEFAULT TAB -->
            <span class="material-symbols-rounded hover-holo-effect" id="header-console">terminal</span>
        </div>
        <div class="right" bind:this={rightDiv}>
            <span class="material-symbols-rounded" id="header-players">group</span>
        </div>
    </div>

    <div class="supply">
        <div class="color-mode">
            <label class="switch">
                <input type="checkbox" bind:this={colorModeSwitchInput} />
                <span class="slider round"></span>
            </label>
        </div>
    </div>
</header>

<style lang="scss">
    @use "../styles/hover-holo-effect";
    @use "../styles/variables";
    @use "../styles/switch.css";

    header {
        position: absolute;
        top: 0;
        width: 100%;
        height: variables.$header-height;
        z-index: 10;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: var(--header-color);
        border-bottom: solid var(--header-bottom-border-color);

        .material-symbols-rounded,
        .color-mode,
        .menu .menu-button {
            @include hover-holo-effect.hover-holo-effect;
        }

        .material-symbols-rounded {
            font-size: variables.$logos-size;
            color: var(--header-icons-color);

            &:active {
                // press effect
                font-size: #{variables.$logos-size - 5px};
            }
        }

        .menu {
            position: absolute;
            top: 0;
            left: 0;

            .menu-button {
                margin-top: calc((#{variables.$header-height - variables.$logos-size}) / 2 - 10px);
                margin-left: calc((#{variables.$header-height - variables.$logos-size}) / 2);
                display: flex;
                flex-direction: column;
                align-items: center;

                &:active {
                    .material-symbols-rounded {
                        font-size: 15px;
                    }

                    .text {
                        font-size: 10px;
                    }

                    img {
                        height: variables.$logos-size - 5px;
                    }
                }

                img {
                    height: variables.$logos-size;
                }

                .menu-text {
                    user-select: none;
                    display: flex;
                    align-items: center;

                    .material-symbols-rounded {
                        transform: unset;
                        font-size: 20px;

                        &:hover {
                            filter: unset;
                        }

                        &:active {
                            font-size: 20px;
                        }
                    }

                    .text {
                        font-size: 15px;
                    }
                }
            }

            .menu-button input {
                display: none;
            }

            .menu-button:has(input:checked) ~ .menu-items {
                left: 25px;
            }

            .menu-items {
                position: absolute;
                top: 100px;
                left: -225px;
                background-color: var(--header-menu-background-color);
                border-radius: 10px;
                transition: left 0.3s ease;
                padding-top: 10px;
                padding-bottom: 10px;
                z-index: 50;

                ul {
                    margin: 0;
                    padding: 0;
                    list-style: none;

                    li button,
                    li a {
                        user-select: none;
                        padding: 10px;
                        width: 150px;
                        font-size: 18px;
                        display: flex;
                        align-items: center;
                        border: none;
                        background-color: transparent;
                        color: var(--header-menu-item-fg-color);
                        font-family: "Nunito", sans-serif;
                        font-weight: 700;
                        text-decoration: none;
                        box-sizing: border-box;

                        &:hover {
                            background-color: var(--header-menu-item-hover-background-color);
                        }

                        .material-symbols-rounded {
                            font-size: 20px;
                            margin-right: 10px;
                            color: var(--header-menu-item-fg-color);
                            filter: none;
                        }

                        :global(img) {
                            width: 20px;
                            height: 20px;
                            margin-right: 10px;
                        }
                    }
                }
            }
        }

        .buttons {
            display: flex;
            justify-items: center;
            align-items: center;

            .right {
                margin-left: 10px;
            }

            .center .material-symbols-rounded {
                font-size: variables.$logos-size + 20;

                &:active {
                    font-size: variables.$logos-size + 15;
                }
            }

            .left {
                margin-right: 10px;
            }

            .hover-holo-effect {
                @include hover-holo-effect.holo-effect;
            }
        }

        .supply {
            position: absolute;
            top: 0;
            right: 0;
            display: flex;
            align-items: center;
            height: 100%;

            .color-mode .switch {
                margin-right: calc((#{variables.$header-height - variables.$logos-size}) / 2);

                .slider {
                    background-color: var(--color-mode-switch-background-color);
                    &:before {
                        background-color: var(--color-mode-switch-handle-color);
                        content: "brightness_4";
                        font-family: "Material Symbols Rounded", sans-serif;
                        font-size: 20px;
                        font-weight: 100;
                        color: var(--color-mode-switch-handle-icon-color);
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        transition: transform 0.4s;
                    }
                }

                input {
                    &:checked + .slider:before {
                        content: "brightness_5";
                    }
                }
            }
        }
    }
</style>
