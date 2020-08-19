package com.cos.jwtex01.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwtex01.config.jwt.JwtProperties;
import com.cos.jwtex01.config.oauth.provider.GoogleUser;
import com.cos.jwtex01.config.oauth.provider.OAuthUserInfo;
import com.cos.jwtex01.model.User;
import com.cos.jwtex01.repository.UserRepository;

import lombok.RequiredArgsConstructor;

// token 발급만 해줌
@RestController
@RequiredArgsConstructor // autowired
public class JwtCreateController {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	// 리액트가 보내는 response를 받음
	@PostMapping("/oauth/jwt/google")
	public String jwtCreate(@RequestBody Map<String, Object> data) {
		System.out.println("jwtCreate 실행");
		System.out.println(data.get("profileObj"));
		OAuthUserInfo googleUser = new GoogleUser((Map<String, Object>) data.get("profileObj"));

		User userEntity = userRepository.findByUsername(googleUser.getProvider() + "_" + googleUser.getProviderId());
		
		// MyBatis는 저장하고 해당 username으로 찾기 
		if (userEntity == null) {
			User userRequest = User.builder()
					.username(googleUser.getProvider() + "_" + googleUser.getProviderId())
					.password(bCryptPasswordEncoder.encode("홍차")).email(googleUser.getEmail())
					.provider(googleUser.getProvider()).providerId(googleUser.getProviderId()).roles("ROLE_USER").build();

			userEntity = userRepository.save(userRequest);
		} 
		

		// 회원가입하고 회원가입한 아이디로 entity 넣기
		// jwtToken 생성
		String jwtToken = JWT.create()
				.withSubject(userEntity.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
				.withClaim("id", userEntity.getId())
				.withClaim("username", userEntity.getUsername())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET));

		System.out.println(googleUser.getName());
		return jwtToken;
	}
}
