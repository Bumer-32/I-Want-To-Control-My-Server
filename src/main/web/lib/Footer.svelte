<script lang="ts">
    import { onMount } from "svelte";
    import githubIcon from "../assets/github.svg";
    import { getVersion } from "../scripts/supply";

    let creatorElement: HTMLSpanElement;
    let iwtcmsLabel: HTMLSpanElement;
    let starsElement: HTMLSpanElement;

    onMount(() => {
        async function loadGithubStars() {
            try {
                const request = fetch("https://corsproxy.io/?url=https://github.com/Bumer-32/I-Want-To-Control-My-Server");
                const html = (await request).text();

                const parser = new DOMParser();
                const doc = parser.parseFromString(await html, "text/html");

                const starsSpan = doc.querySelector<HTMLSpanElement>('a[href$="/stargazers"] span')!;

                starsElement.innerHTML = starsSpan.innerHTML.trim();
            } catch (error) {
                console.error("Failed to load github stars");
                console.error(error);
            }
        }

        loadGithubStars();

        // ? happy birthday Bumer_32
        if (new Date().getMonth() == 1 && new Date().getDate() == 21) {
            console.log("Happy birthday Bumer_32! 🎉🎉🎉");
            creatorElement.innerHTML = creatorElement.innerHTML + " | Happy birthday Bumer_32! 🎉🎉🎉";
        }

        // ? iwtcms version
        getVersion().then((version) => {
            iwtcmsLabel.innerHTML = iwtcmsLabel.innerHTML + " " + version;
        });
    });
</script>

<footer class="footer">
    <div class="left">
        <span class="iwtcms-label btn-shine" bind:this={iwtcmsLabel}>IWTCMS</span>

        <div class="authors">
            <span bind:this={creatorElement}>Created by <a href="https://github.com/Bumer-32">Bumer_32</a></span>
            <span>Spatial thanks for Crazy Potatto</span>
        </div>
    </div>

    <a href="https://github.com/Bumer-32/I-Want-To-Control-My-Server" class="github">
        <img src={githubIcon} alt="GitHub icon" />
        <span>Star on GitHub</span>
        <span class="material-symbols-rounded">star</span>
        <span bind:this={starsElement}></span>
    </a>
</footer>

<style lang="scss">
    @use "../styles/hover-holo-effect";
    @use "../styles/variables";
    @use "../styles/btn-shine.css";

    .footer {
        position: absolute;
        background-color: var(--footer-color);
        width: 100%;
        height: variables.$footer-height;
        bottom: 0;
        display: flex;
        align-items: center;

        .left {
            display: flex;
            align-items: center;

            .iwtcms-label {
                position: static;
                transform: translate(0, 0);
                background: linear-gradient(to right, var(--footer-iwtcms-label-color-1) 0, var(--footer-iwtcms-label-color-2) 10%, var(--footer-iwtcms-label-color-3) 20%) 0;
                -webkit-background-clip: text;
                background-clip: text;
                margin-left: -30px;
                margin-right: -30px;
            }

            .authors {
                font-size: 8px;
                display: flex;
                flex-direction: column;
                color: var(--footer-authors-color);

                a {
                    color: var(--footer-authors-url-color);
                    text-decoration: none;
                }
            }
        }

        .github {
            position: absolute;
            bottom: 5px;
            right: 10px;
            font-size: 14px;
            text-decoration: none;
            display: flex;
            align-items: center;
            color: var(--footer-github-title-color);
            gap: 5px;
            transition:
                filter 0.3s ease,
                box-shadow 0.3s ease;

            &:hover {
                @include hover-holo-effect.hover-holo-effect;

                .material-symbols-rounded {
                    color: var(--footer-github-star-hover-color);
                }
            }

            img {
                width: 20px;
                height: 20px;
            }

            .material-symbols-rounded {
                font-size: 12px;
                color: var(--footer-github-star-color);
            }
        }
    }
</style>
