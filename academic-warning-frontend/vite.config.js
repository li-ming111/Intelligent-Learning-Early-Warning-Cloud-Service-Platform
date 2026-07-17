import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 5173,
    host: true,
    allowedHosts: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8076',
        changeOrigin: true,
        secure: false,
        ws: true,
        logLevel: 'debug'
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets'
  }
})
