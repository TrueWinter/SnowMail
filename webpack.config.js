const path = require('path');
const { ProvidePlugin } = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');
const { BundleAnalyzerPlugin } = require('webpack-bundle-analyzer');
const CopyPlugin = require('copy-webpack-plugin');

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
      test: /\.(j|t)sx?$/,
      loader: 'babel-loader',
      // It should work without specifying the options here, but it doesn't
      options: {
        presets: [
          '@babel/preset-env',
          '@babel/preset-react',
          '@babel/preset-typescript'
        ]
      }
    }, {
      test: /\.css$/,
      use: [
        MiniCssExtractPlugin.loader,
        'css-loader',
        'postcss-loader'
      ]
    }, {
      test: /\.png$/,
      type: 'asset/resource'
    }]
  },
  devServer: {
    port: 8026,
    // HMR doesn't work with module chunks
    hot: false,
    devMiddleware: {
      writeToDisk: true,
    },
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
    }),
    new CopyPlugin({
      patterns: [
        'snowmail/src/main/resources/web/src/snowmail-icon.png'
      ]
    })
  ]
};

/** @type {import('webpack').Configuration} */
const formClient = {
  mode: 'production',
  entry: {
    index: './npm/web/index.jsx'
  },
  output: {
    path: path.resolve(__dirname, 'npm', 'dist', 'main'),
    clean: true,
    library: {
      type: 'module'
    },
  },
  experiments: {
    outputModule: true
  },
  resolve: {
    extensions: ['.js', '.jsx', '.ts', '.tsx', '.css', '.scss', '.sass'],
    modules: ['node_modules'],
    alias: {
      react: 'preact/compat',
      'react-dom/test-utils': 'preact/test-utils',
      'react-dom': 'preact/compat', // Must be below test-utils
      'react/jsx-runtime': 'preact/jsx-runtime'
    }
  },
  module: {
    rules: [{
      test: /\.jsx?$/,
      loader: 'babel-loader',
      // It should work without specifying the options here, but it doesn't
      options: {
        presets: [
          '@babel/preset-env',
          ['@babel/preset-react', {
            pragma: 'h',
            pragmaFrag: 'Fragment',
          }]
        ]
      }
    }, {
      test: /\.css$/,
      use: [
        MiniCssExtractPlugin.loader,
        'css-loader',
        'postcss-loader'
      ]
    }]
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: 'styles.css'
    }),
    new BundleAnalyzerPlugin({
      analyzerMode: 'static',
      openAnalyzer: false
    }),
    new ProvidePlugin({
      h: ['preact', 'h'],
      Fragment: ['preact', 'Fragment']
    })
  ]
};

/** @type {import('webpack').Configuration} */
const formServer = {
  mode: 'production',
  entry: {
    index: './npm/web/server.jsx'
  },
  target: 'node',
  devtool: false,
  output: {
    path: path.resolve(__dirname, 'npm', 'dist', 'server'),
    clean: true,
    chunkFormat: 'module',
    library: {
      type: 'module'
    },
  },
  experiments: {
    outputModule: true
  },
  resolve: {
    extensions: ['.js', '.jsx', '.ts', '.tsx', '.css', '.scss', '.sass'],
    modules: ['node_modules'],
    alias: {
      react: 'preact/compat',
      'react-dom/test-utils': 'preact/test-utils',
      'react-dom': 'preact/compat', // Must be below test-utils
      'react/jsx-runtime': 'preact/jsx-runtime'
    }
  },
  module: {
    rules: [{
      test: /\.jsx?$/,
      loader: 'babel-loader',
      // It should work without specifying the options here, but it doesn't
      options: {
        presets: [
          '@babel/preset-env',
          ['@babel/preset-react', {
            pragma: 'h',
            pragmaFrag: 'Fragment',
          }]
        ]
      }
    }, {
      test: /\.css$/,
      use: [
        MiniCssExtractPlugin.loader,
        'css-loader',
        'postcss-loader'
      ]
    }]
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: 'styles.css'
    }),
    new ProvidePlugin({
      h: ['preact', 'h'],
      Fragment: ['preact', 'Fragment']
    })
  ]
};

module.exports = [
  dashboard,
  formClient,
  formServer
];
