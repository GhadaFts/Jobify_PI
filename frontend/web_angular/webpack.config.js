module.exports = {
  module: {
    rules: [
      {
        test: /\.component\.scss$/, // Apply to component SCSS files only
        use: [
          'style-loader',              // Injects styles into DOM for components
          'css-loader',
          {
            loader: 'postcss-loader',
            options: {
              postcssOptions: {
                config: './postcss.config.js'
              }
            }
          },
          'sass-loader'
        ]
      }
    ]
  }
};