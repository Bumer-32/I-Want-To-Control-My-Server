export default function main() {
    console.log("main");

    window.addEventListener("load", () => {
        // ? remove loading screen
        document.querySelector<HTMLDivElement>(".loading")!.style.display =
            "none";
        console.log("Loaded");
    });
}
