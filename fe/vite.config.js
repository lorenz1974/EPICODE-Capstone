import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      "@components": path.resolve(__dirname, 'src/components/'),
      "@assets": path.resolve(__dirname, 'src/assets/'),
      "@utils": path.resolve(__dirname, 'src/utils/'),
      "@static": path.resolve(__dirname, 'src/pages/static/'),
      "@constants": path.resolve(__dirname, 'src/constants/constants'),
      "@endpoints": path.resolve(__dirname, 'src/constants/endpoints'),
      "@pages": path.resolve(__dirname, 'src/pages/'),
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }

  }
})
