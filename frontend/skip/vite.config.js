import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import dotenv from 'dotenv';
import path from 'path';

dotenv.config({ path: path.resolve(__dirname, '../../backend/skip/.env') });

// https://vite.dev/config/
export default defineConfig({
    
    define: {
        __APP_BASE__: JSON.stringify('http://192.168.80.55:8080')
    },
    plugins: [react(), tailwindcss()],
        server: {
            proxy: {
                '/api': {
                target: 'http://localhost:8080', // 백엔드 주소
                changeOrigin: true,
                secure: false,
            },
        },
    },
})
