//tailwind.config.js

/** @type {import('tailwindcss').Config} */
// const spacing = {};
// for (let i = 0; i < 3000; i++) {
//   spacing[i] = i + "px";
// }
module.exports = {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    // spacing,
    extend: {
      // 自定义
      width: {
        1: "1vw",
        2: "2vw",
        // 以此类推，直到
        100: "100vw",
      },
      height: {
        1: "1vw",
        2: "2vw",
        // 以此类推，直到
        100: "100vw",
      },
      fontSize: {
        "1vw": "1vw",
        "2vw": "2vw",
        "3vw": "3vw",
      },
    },
  },
  plugins: [],
};
