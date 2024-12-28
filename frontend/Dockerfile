# Build stage
FROM node:18-alpine as build
WORKDIR /app

# Ajoutez le chemin relatif ../frontend/ devant chaque COPY
COPY ../frontend/package*.json ./
COPY ../frontend/tailwind.config.js ./
COPY ../frontend/postcss.config.js ./

# Installation des dépendances
RUN npm install

# Copie du code source avec les chemins relatifs
COPY ../frontend/src/ ./src/
COPY ../frontend/public/ ./public/

# Build de l'application
RUN npm run build

# Production stage
FROM nginx:alpine

# Copie de la configuration nginx
COPY ../frontend/nginx.conf /etc/nginx/conf.d/default.conf

# Copie des fichiers buildés depuis l'étape de build
COPY --from=build /app/build /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]