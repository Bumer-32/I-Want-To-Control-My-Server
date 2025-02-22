<script lang="ts">
    import iwtcmsIcon from "../assets/icon_clearbg.png";
    import githubIcon from "../assets/github.svg";
    import modrinthIcon from "../assets/modrinth.svg";
    import { switchTab } from "../scripts/tabsController";
    import { onMount } from "svelte";

    let leftDiv: HTMLDivElement;
    let rightDiv: HTMLDivElement;

    function alignHeaderButtons() {
        const existingFillerDiv = document.querySelector(".header .buttons .filler-div") as HTMLDivElement | null;
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
        const followDiv = document.querySelector<HTMLDivElement>(".header .buttons")!;

        const handleMutation = (_: MutationRecord[]) => {
            alignHeaderButtons();
        };

        const observer = new MutationObserver(handleMutation);

        observer.observe(followDiv, {
            childList: true,
            attributes: false,
            subtree: false,
        });
    }

    function handleTabSwitching() {
        const buttons = document.querySelectorAll(".header .buttons span") as NodeListOf<HTMLImageElement>;

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
    });
</script>

<header>
    <div class="menu">
        <label class="menu-button">
            <input type="checkbox" />
            <img src={iwtcmsIcon} alt="IWTCMS logo" />
            <span class="menu-text">
                <span class="material-symbols-rounded">menu</span>
                <span class="text">menu</span>
            </span>
        </label>
        <div class="menu-items">
            <ul>
                <li>
                    <button
                        type="button"
                        on:click={() => window.location.assign("https://modrinth.com/mod/i-want-to-control-my-server")}
                        on:keydown={(e) => e.key === "Enter" && window.location.assign("https://modrinth.com/mod/i-want-to-control-my-server")}
                    >
                        <img src={modrinthIcon} alt="modrinth" />
                        Modrinth
                    </button>
                </li>
                <li>
                    <button
                        type="button"
                        on:click={() => window.location.assign("https://github.com/Bumer-32/I-Want-To-Control-My-Server")}
                        on:keydown={(e) => e.key === "Enter" && window.location.assign("https://github.com/Bumer-32/I-Want-To-Control-My-Server")}
                    >
                        <img src={githubIcon} alt="GitHub icon" />
                        GitHub
                    </button>
                </li>
                <li>
                    <button
                        type="button"
                        on:click={() => window.location.assign("files/iwtcms_client.zip")}
                        on:keydown={(e) => e.key === "Enter" && window.location.assign("files/iwtcms_client.zip")}
                    >
                        <span class="material-symbols-rounded">download</span>
                        Python CLI
                    </button>
                </li>
                <li id="logout-button">
                    <button type="button">
                        <span class="material-symbols-rounded">logout</span>
                        Logout
                    </button>
                </li>
            </ul>
        </div>
    </div>

    <div class="buttons">
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
                <input type="checkbox" />
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
        transition: background-color 0.3s ease;
        border-bottom: solid var(--header-bottom-border-color);

        .material-symbols-rounded,
        .color-mode,
        .menu .menu-button {
            // hover holo effect
            transition:
                filter 0.3s ease,
                box-shadow 0.3s ease;

            &:hover {
                @extend .hover-holo-effect;
            }
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
                transition:
                    left 0.3s ease,
                    background-color 0.3s ease;
                padding-top: 10px;
                padding-bottom: 10px;
                z-index: 50;

                ul {
                    margin: 0;
                    padding: 0;
                    list-style: none;

                    li button {
                        user-select: none;
                        padding: 10px;
                        width: 150px;
                        font-size: 18px;
                        display: flex;
                        align-items: center;
                        border: none;
                        background-color: transparent;
                        color: var(--header-menu-item-fg-color);
                        transition: color 0.3s ease;
                        font-family: "Nunito", sans-serif;
                        font-weight: 700;

                        &:hover {
                            background-color: var(--header-menu-item-hover-background-color);
                        }

                        .material-symbols-rounded {
                            font-size: 20px;
                            margin-right: 10px;
                            color: var(--header-menu-item-fg-color);
                            transition: color 0.3s ease;
                        }

                        img {
                            width: 20px;
                            height: 20px;
                            margin-right: 10px;
                            transition:
                                filter 0.3s ease,
                                color 0.3s ease;
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
                    transition: background-color 0.3s ease;

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
                        transition:
                            background-color 0.3s ease,
                            color 0.3s ease,
                            transform 0.4s;
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
