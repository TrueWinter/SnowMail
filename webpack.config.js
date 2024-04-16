const path = require('path');
const { ProvidePlugin } = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

/** @type {import('webpack').Configuration} */
const dashboard = {
  entry: './src/main/resources/web/src/index.tsx',
  output: {
    path: path.resolve(__dirname, 'src', 'main', 'resources', 'web', 'dist'),
    clean: true
  },
  resolve: {
    extensions: ['.js', '.jsx', '.ts', '.tsx', '.css', '.scss', '.sass'],
    modules: ['node_modules']
  },
  module: {
    rules: [{
      test: /\.tsx?$/,
      loader: 'babel-loader'
    }, {
      test: /\.css$/,
      use: [
        MiniCssExtractPlugin.loader,
        'css-loader',
        'postcss-loader'
      ]
    }]
  },
  devServer: {
    port: 8026,
    hot: true,
    static: {
      directory: path.resolve(__dirname, 'src', 'main', 'resources', 'web', 'dist')
    },
    client: {
      overlay: true
    },
    proxy: [{
      context: () => true,
      target: 'http://localhost:8025'
    }]
  },
  plugins: [
    new MiniCssExtractPlugin(),
    new ProvidePlugin({
      React: 'react'
    })
  ]
};

module.exports = [
  dashboard
];
