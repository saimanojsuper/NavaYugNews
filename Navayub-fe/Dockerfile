# Stage 1: Build the React app
FROM node:16-alpine as build

WORKDIR /navayug

# Copy package.json and install dependencies
COPY package.json package-lock.json ./
RUN npm install

# Copy the rest of the application files
COPY . .

# Build the app
RUN npm run build

# Stage 2: Serve the React app using Nginx
FROM nginx:stable-alpine

# Copy the build output to Nginx's default static file directory
COPY --from=build /navayug/build /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Start Nginx when the container starts
CMD ["nginx", "-g", "daemon off;"]

#Able to run the docker image but it's not able to acccess the localhost url of the backend
