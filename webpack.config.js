const path = require('path')

module.exports = {
    mode: 'development',
    entry: {
        index: path.resolve(__dirname, 'src/main/client/index.js')
    },
    output: {
        path: __dirname,
        filename: 'build.js'
    },
    devServer: {
        compress: true,
        port: 9000,
    },
};