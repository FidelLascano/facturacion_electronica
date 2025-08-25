# syntax=docker/dockerfile:1
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json* pnpm-lock.yaml* yarn.lock* ./
RUN npm i --silent || true
COPY . .
RUN npm run build

FROM node:20-alpine
WORKDIR /app
ENV VITE_API_BASE=http://localhost:8080
COPY --from=build /app/dist ./dist
EXPOSE 5173
CMD ["npx","vite","preview","--host","0.0.0.0","--port","5173"]
