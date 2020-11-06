module.exports = {
    devServer: {
        proxy: {
            '/api': {
                target: 'https://pic-bed.xyz/api',
                secure: true,
                changeOrigin: true,
                pathRewrite: {
                    '^/api': ''
                }
            }
        }
    }
}