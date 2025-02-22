import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte'

// https://vite.dev/config/
export default defineConfig({
  plugins: [svelte()],
  root: "src/main/web",
  build: {
    outDir: "../../../build/resources/main/web"
  },
  server: {
    proxy: {
      "/apiList": "http://localhost:25566",
      "/api": "http://localhost:25566"
    }
  }
})
