/**
 * 生成 tabBar 图标的脚本
 * 运行方式: node generate-icons.js
 * 
 * 需要先安装依赖: npm install sharp
 */

const fs = require('fs');
const path = require('path');

// 确保目录存在
const iconsDir = path.join(__dirname, 'src', 'static', 'icons');
if (!fs.existsSync(iconsDir)) {
  fs.mkdirSync(iconsDir, { recursive: true });
}

// 简单的 SVG 图标模板
const createSvgIcon = (paths, color) => `
<svg xmlns="http://www.w3.org/2000/svg" width="81" height="81" viewBox="0 0 24 24" fill="${color}">
  ${paths}
</svg>
`;

// 图标路径定义
const icons = {
  home: '<path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>',
  diary: '<path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>',
  chat: '<path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H6l-2 2V4h16v12z"/>',
  profile: '<path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>'
};

const grayColor = '#999999';
const purpleColor = '#7B68EE';

// 生成 SVG 文件
Object.entries(icons).forEach(([name, paths]) => {
  // 普通状态（灰色）
  const normalSvg = createSvgIcon(paths, grayColor);
  fs.writeFileSync(path.join(iconsDir, `${name}.svg`), normalSvg.trim());
  
  // 选中状态（紫色）
  const activeSvg = createSvgIcon(paths, purpleColor);
  fs.writeFileSync(path.join(iconsDir, `${name}-active.svg`), activeSvg.trim());
  
  console.log(`生成图标: ${name}.svg, ${name}-active.svg`);
});

console.log('\n图标生成完成！');
console.log('注意: SVG 图标已生成，但 UniApp tabBar 需要 PNG 格式。');
console.log('请执行以下步骤:');
console.log('1. 在浏览器中打开 SVG 文件');
console.log('2. 截图或使用在线工具转换为 81x81 的 PNG');
console.log('3. 或安装 sharp 后运行: npm install sharp && node generate-icons.js --png');
