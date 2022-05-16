const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
  app.use(
    createProxyMiddleware('/v1/api',{
      target: 'http://localhost:8083',
      changeOrigin: true
    })
  );
};