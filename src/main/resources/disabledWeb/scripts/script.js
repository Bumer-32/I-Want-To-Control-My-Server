async function loadGithubStars() {
    try {
        const request = fetch("https://corsproxy.io/?url=https://github.com/Bumer-32/I-Want-To-Control-My-Server");
        const html = (await request).text();

        const parser = new DOMParser();
        const doc = parser.parseFromString(await html, "text/html");

        const starsSpan = doc.querySelector('a[href$="/stargazers"] span');

        document.querySelector(".footer .github .stars").innerHTML = starsSpan.innerHTML.trim();
    } catch (error) {
        console.error("Failed to load github stars");
        console.error(error);
    }
}

loadGithubStars();

// ? happy birthday Bumer_32
// TODO: fix position
if (new Date().getMonth() === 1 && new Date().getDate() === 21) {
    console.log("Happy birthday Bumer_32! 🎉🎉🎉");
    const creatorElement = document.querySelector(".footer .authors .creator");
    creatorElement.innerHTML = creatorElement.innerHTML + " | Happy birthday Bumer_32! 🎉🎉🎉";
}

// ? iwtcms version
try {
    fetch(document.location.origin + "/api/version").then((response) => {
        if (!response.ok) {
            throw new Error("Network response was not ok " + response.statusText);
        }

        response.text().then((version) => {
            const iwtcmsLabel = document.querySelector(".footer .iwtcms-label");
            iwtcmsLabel.innerHTML = iwtcmsLabel.innerHTML + " " + version;
        });
    });
} catch (e) {
    console.error("Failed to load IWTCMS version");
    console.error(e);
}
