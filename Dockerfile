from node:20
workdir /src
copy package.json .
run npm install
run npm install react-router-dom
run npm install axios
copy . .
expose 3000
cmd ["npm", "start"]