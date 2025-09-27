@echo off
echo 程序员技术练兵场 - 前端启动脚本
echo ================================

echo 检查 Node.js 环境...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未检测到 Node.js，请先安装 Node.js 16 或更高版本
    pause
    exit /b 1
)

echo Node.js 环境检测通过

echo 检查依赖包...
if not exist "node_modules" (
    echo 正在安装依赖包...
    npm install
    if %errorlevel% neq 0 (
        echo 依赖安装失败，请检查网络连接
        pause
        exit /b 1
    )
) else (
    echo 依赖包已存在
)

echo 启动开发服务器...
echo 请确保后端服务已在 http://localhost:8123 运行
echo 前端将在 http://localhost:3000 启动
echo ================================
npm run dev

pause
