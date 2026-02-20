import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:3000',  // API Gateway
        changeOrigin: true,
        // Don't rewrite path - API Gateway expects /api prefix
        configure: (proxy, options) => {
          proxy.on('proxyReq', (proxyReq, req, res) => {
            // Log requests
            console.log(`[Proxy] ${req.method} ${req.url}`);
            
            // Forward Authorization header
            const token = req.headers.authorization;
            if (token) {
              proxyReq.setHeader('Authorization', token);
              console.log('[Proxy] Authorization header forwarded');
            }
          });

          proxy.on('proxyRes', (proxyRes, req, res) => {
            // Log responses
            console.log(`[Proxy Response] ${proxyRes.statusCode} ${req.url}`);
          });

          proxy.on('error', (err, req, res) => {
            console.error(`[Proxy Error] ${err.message}`);
            res.writeHead(503, {
              'Content-Type': 'application/json',
            });
            res.end(JSON.stringify({
              message: 'Backend service unavailable',
              error: err.message,
            }));
          });
        },
      },
    },
  },
})
