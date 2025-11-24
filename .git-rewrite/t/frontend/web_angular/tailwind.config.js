/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
    // Ajoutez ces chemins si vous avez une structure différente
    "./projects/**/*.{html,ts}",
    "./libs/**/*.{html,ts}",
  ],
   theme: {
    extend: {
      colors: {
        jobiBlue: "#336781",
        jobiOrange: "#F45522",
        jobiGray: "#D4DDE2",
      }
    },
  },
  plugins: [],
}