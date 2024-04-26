const path = require('path');
const { ProvidePlugin } = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');

/** @type {import('webpack').Configuration} */
const dashboard = {
  entry: './snowmail/src/main/resources/web/src/index.tsx',
  output: {
    path: path.resolve(__dirname, 'snowmail', 'src', 'main', 'resources', 'web', 'dist'),
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
    client: {
      overlay: true
    },
    proxy: [{
      context: () => true,
      target: 'http://localhost:8025'
    }]
  },
  optimization: {
    minimizer: [
      new TerserPlugin(),
      new CssMinimizerPlugin()
    ]
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
