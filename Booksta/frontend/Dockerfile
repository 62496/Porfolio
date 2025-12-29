FROM node:18-alpine
WORKDIR /app
COPY /booksta/package*.json ./
RUN npm install
COPY booksta .
EXPOSE 3000
CMD ["npm", "start"]