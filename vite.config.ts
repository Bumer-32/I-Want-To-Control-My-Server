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
        proxy: {
            "/apiList": "http://localhost:25566",
            "/api": "http://localhost:25566",
        },
    },
});
