import {defineConfig} from "vite";
import {svelte} from "@sveltejs/vite-plugin-svelte";
import {sveltePreprocess} from "svelte-preprocess";
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
// noinspection JSUnusedGlobalSymbols
export default defineConfig({
    plugins: [
        svelte({
            preprocess: sveltePreprocess(),
        }),
        tailwindcss(),
    ],
    root: "src",
    build: {
        outDir: "../build",
        emptyOutDir: true,
        sourcemap: true,
    },
    server: {
        host: "localhost",
        open: "/",
        proxy: {
            "/api": {
                target: "http://localhost:25566",
                changeOrigin: true,
                configure: (proxy) => {
                    proxy.on("proxyReq", (proxyReq, _req, _res) => {
                        proxyReq.removeHeader("origin");
                    });
                },
            },
            "/files": {
                target: "http://localhost:25566",
                changeOrigin: true,
            },
            "/ws": {
                target: "ws://localhost:25566",
                rewriteWsOrigin: true,
            },
        },
    },
});
