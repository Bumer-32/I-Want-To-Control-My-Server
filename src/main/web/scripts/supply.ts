import Constants from "./constants";

export async function getVersion(): Promise<string> {
    try {
        const request = await fetch(Constants.VERSION_URL);
        const version = await request.text();
        return version;
    } catch (e) {
        return "";
    }
}
