# 部署说明

## 开发环境启动

### 前提条件
1. 确保已安装 Node.js 16 或更高版本
2. 确保后端服务已在 `http://localhost:8123` 运行

### Windows 用户
双击运行 `start.bat` 文件，或在命令行中执行：
```cmd
start.bat
```

### macOS/Linux 用户
在终端中执行：
```bash
./start.sh
```

### 手动启动
1. 安装依赖：
```bash
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

访问地址：`http://localhost:3000`

## 生产环境部署

### 1. 构建项目
```bash
npm run build
```
构建完成后，`dist` 目录包含所有静态文件。

### 2. 部署到服务器

#### 使用 Nginx
```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /path/to/dist;
    index index.html;

    # 处理 Vue Router 的历史模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 代理到后端
    location /api {
        proxy_pass http://localhost:8123;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

#### 使用 Apache
在 `dist` 目录创建 `.htaccess` 文件：
```apache
<IfModule mod_rewrite.c>
  RewriteEngine On
  RewriteBase /
  RewriteRule ^index\.html$ - [L]
  RewriteCond %{REQUEST_FILENAME} !-f
  RewriteCond %{REQUEST_FILENAME} !-d
  RewriteRule . /index.html [L]
</IfModule>
```

### 3. 环境变量配置
在生产环境中，可以创建 `.env.production` 文件配置环境变量：
```
VITE_API_BASE_URL=https://your-api-domain.com/api
```

## Docker 部署

### 1. 创建 Dockerfile
```dockerfile
# 构建阶段
FROM node:18-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# 生产阶段
FROM nginx:alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### 2. 创建 nginx.conf
```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8123;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 3. 构建和运行
```bash
docker build -t coder-test-frontend .
docker run -p 80:80 coder-test-frontend
```

## 常见问题

### 1. 跨域问题
如果遇到跨域问题，确保：
- 开发环境中 Vite 代理配置正确
- 生产环境中 Nginx/Apache 代理配置正确
- 后端服务允许跨域请求

### 2. 路由问题
如果页面刷新后出现 404：
- 确保服务器配置了 Vue Router 的历史模式支持
- 检查 `try_files` 配置是否正确

### 3. API 请求失败
检查：
- 后端服务是否正常运行
- API 地址是否正确
- 网络连接是否正常

### 4. 构建失败
常见原因：
- Node.js 版本过低
- 依赖包版本冲突
- 内存不足

解决方案：
```bash
# 清除缓存
npm cache clean --force

# 删除 node_modules 重新安装
rm -rf node_modules package-lock.json
npm install
```
