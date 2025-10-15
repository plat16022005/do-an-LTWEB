// 📁 theme-toggle.js
const root = document.documentElement;
const switchInput = document.getElementById('theme-toggle');
const switchIcon = document.querySelector('.switch-icon');
const THEME_KEY = 'theme';

// Lấy theme đã lưu hoặc theo hệ thống
const savedTheme = localStorage.getItem(THEME_KEY);
const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
const isDark = savedTheme ? savedTheme === 'dark' : prefersDark;

// Áp dụng theme ban đầu
root.classList.toggle('dark-theme', isDark);
root.classList.toggle('light-theme', !isDark);
if (switchInput) switchInput.checked = isDark;
if (switchIcon) switchIcon.textContent = isDark ? '🌙' : '☀️';

// Khi người dùng gạt công tắc
if (switchInput) {
  switchInput.addEventListener('change', () => {
    const nowDark = switchInput.checked;
    root.classList.toggle('dark-theme', nowDark);
    root.classList.toggle('light-theme', !nowDark);
    localStorage.setItem(THEME_KEY, nowDark ? 'dark' : 'light');
    if (switchIcon) switchIcon.textContent = nowDark ? '🌙' : '☀️';
  });
}
