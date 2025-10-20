/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/**/*.{html,ts}', // Scans all HTML and TypeScript files in src/
  ],
  theme: {
    extend: {
      colors: {
        primary: '#336781', // Single color
      },
    },
  },
  plugins: [],
};
