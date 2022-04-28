import axios from 'axios';

const Axios = axios.create({
  baseURL: 'http://k6s104.p.ssafy.io:8081',
  headers: {
    'Content-type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTION',
    'Access-Control-Allow-Headers':
      'Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization',
    'Access-Control-Expose-Headers': 'Content-Length,Content-Range',
  },
});

function getAxios(){
    return Axios;
};

export default getAxios;