import { defineConfig } from "vite";
import { svelte } from "@sveltejs/vite-plugin-svelte";
import { sveltePreprocess } from "svelte-preprocess";
import { hocon } from "hocon-web";
import fs from "fs";

let apiURL = "http://localhost:25566";
let apiWsURL = "ws://localhost:25566";
let autoOpen = ""

try {
    const hoconInstance = await hocon();
    const file = fs.readFileSync("./run/iwtcms/iwtcms.conf", "utf-8");
    const cfg = new hoconInstance.Config(file);
    const json = JSON.parse(cfg.toJSON());

    console.log("Config file loaded");

    const prefix = json.ssl["use SSL"] ? "https" : "http";
    const wsPrefix = json.ssl["use SSL"] ? "wss" : "ws";
    apiURL = `${prefix}://${json.server.ip}:${json.server.port}`;
    apiWsURL = `${wsPrefix}://${json.server.ip}:${json.server.port}`;

    autoOpen = json.web["auto open IWTCMS page on startup"] ? "/" : "";

    cfg.delete();
} catch (e) {
    console.error(e);
}

console.log("Api URL: ", apiURL);
console.log("Api WS URL: ", apiWsURL);

// https://vite.dev/config/
// noinspection JSUnusedGlobalSymbols
export default defineConfig({
    plugins: [
        svelte({
            preprocess: sveltePreprocess(),
        }),
    ],
    root: "src/main/web",
    build: {
        outDir: "../../../build/resources/main/web",
        sourcemap: true,
    },
    server: { 
        open: autoOpen,
        proxy: {
            "/apiList": {
                target: apiURL,
                changeOrigin: true,
            },
            "/api": {
                target: apiURL,
                changeOrigin: true,
                configure: (proxy) => {
                    proxy.on("error", (err, _req, _res) => {
                        console.log("proxy error", err);
                    });
                    proxy.on("proxyReq", (proxyReq, req, _res) => {
                        proxyReq.removeHeader("origin");
                        console.log("Sending Request to the Target:", req.method, req.url);
                    });
                    proxy.on("proxyRes", (proxyRes, req, _res) => {
                        console.log("Received Response from the Target:", proxyRes.statusCode, req.url);
                    });
                },
            },
            "/files": {
                target: apiURL,
                changeOrigin: true,
            },
            "/ws": {
                target: apiWsURL,
                rewriteWsOrigin: true,
            },
        },
    },
});
