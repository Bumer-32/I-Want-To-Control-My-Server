import { defineConfig } from "vite";
import { svelte } from "@sveltejs/vite-plugin-svelte";
import { sveltePreprocess } from "svelte-preprocess";

// https://vite.dev/config/
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
        open: "/",
        proxy: {
            "/apiList": {
                target: "http://localhost:25566",
                changeOrigin: true,
            },
            "/api": {
                target: "http://localhost:25566",
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
            "/ws": {
                target: "ws://localhost:25566",
                rewriteWsOrigin: true,
            },
        },
    },
});
