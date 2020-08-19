import React from 'react';
import GoogleLogin from 'react-google-login';
import Axios from 'axios';

const config = {
  headers: {
    'Content-Type': 'application/json; charset=UTF-8;',
  },
};

const responseGoogle = async (response) => {
  console.log(response);
  let jwtToken = await Axios.post(
    // await : async 함수가 됨
    'http://localhost:8080/oauth/jwt/google',
    JSON.stringify(response),
    config
  );
  if (jwtToken.status === 200) {
    localStorage.setItem('jwtToken', jwtToken.data);
  }
  console.log(2, jwtToken.data);
};

const Login = () => {
  return (
    <GoogleLogin
      clientId="245333231817-h0qpdelt6f21b3getlibl6r9vr1s17n6.apps.googleusercontent.com"
      buttonText="Login"
      onSuccess={responseGoogle}
      onFailure={responseGoogle}
      cookiePolicy={'single_host_origin'}
    />
  );
};

export default Login;
