# Stage 1: Build the React application
FROM node:18 AS build
WORKDIR /app

# copy package files first for better caching
COPY package*.json ./
RUN npm ci

# copy rest of source and build
COPY . .
RUN npm run build

# Stage 2: Serve the static files with Nginx
FROM nginx:stable-alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]