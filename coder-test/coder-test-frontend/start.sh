#!/bin/bash

echo "程序员技术练兵场 - 前端启动脚本"
echo "================================"

# 检查 Node.js 环境
echo "检查 Node.js 环境..."
if ! command -v node &> /dev/null; then
    echo "错误：未检测到 Node.js，请先安装 Node.js 16 或更高版本"
    exit 1
fi

echo "Node.js 环境检测通过"

# 检查依赖包
echo "检查依赖包..."
if [ ! -d "node_modules" ]; then
    echo "正在安装依赖包..."
    npm install
    if [ $? -ne 0 ]; then
        echo "依赖安装失败，请检查网络连接"
        exit 1
    fi
else
    echo "依赖包已存在"
fi

echo "启动开发服务器..."
echo "请确保后端服务已在 http://localhost:8123 运行"
echo "前端将在 http://localhost:3000 启动"
echo "================================"

npm run dev
