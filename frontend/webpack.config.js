const autoprefixer = require('autoprefixer');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const path = require('path');
const webpack = require('webpack');

const dev = process.env.NODE_ENV === 'development';

const outDir = path.join(__dirname, 'build', 'assets');
const srcDir = path.join(__dirname, 'src', 'assets');

module.exports = {
  context: srcDir,
  devServer: {
    historyApiFallback: true,
    inline: true,
    proxy: {
      '/graphql': {
        target: 'http://localhost:4040',
      }
    },
  },
  devtool: 'source-map',
  entry: {
    app: ['babel-polyfill', './'],
  },
  module: {
    loaders: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'babel',
        query: {
          babelrc: false,
          passPerPreset: true,
          presets: [
            {
              plugins: [
                path.join(__dirname, 'tools', 'babelRelayPlugin'),
              ],
            },
            'react',
            'es2015',
            'stage-0',
          ],
        },
      },
      {
        test: /\.css$/,
        loader: ExtractTextPlugin.extract('style', ['css']),
      },
      {
        test: /\.scss$/,
        loader: ExtractTextPlugin.extract(
          'style',
          ['css?modules&importLoaders=1&localIdentName=[local]-[hash:base64]', 'postcss', 'sass']
        ),
      },
    ],
  },
  output: {
    filename: '[name].[chunkhash].js',
    path: outDir,
  },
  plugins: [
    new ExtractTextPlugin('[name].[contenthash].css', { allChunks: true, disable: dev }),
    new HtmlWebpackPlugin({
      template: path.join(srcDir, 'index.html'),
    }),
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify(dev ? 'development' : 'production')
    }),
    new webpack.optimize.OccurrenceOrderPlugin(),
    ...(dev ? [] : [new webpack.optimize.UglifyJsPlugin()]),
  ],
  postcss: () => [autoprefixer],
};
