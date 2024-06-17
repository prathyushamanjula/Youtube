package com.qp.quantum_share.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qp.quantum_share.configuration.JwtUtilConfig;
import com.qp.quantum_share.dao.QuantumShareUserDao;
import com.qp.quantum_share.dto.QuantumShareUser;
import com.qp.quantum_share.response.ResponseStructure;
import com.qp.quantum_share.services.FacebookAccessTokenService;
import com.qp.quantum_share.services.InstagramService;
import com.qp.quantum_share.services.TelegramService;
import com.qp.quantum_share.services.TwitterService;
import com.qp.quantum_share.services.YoutubeService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/quantum-share")
public class SocialMediaLoginController {

	@Autowired
	ResponseStructure<String> structure;

	@Autowired
	FacebookAccessTokenService faceBookAccessTokenService;

	@Autowired
	QuantumShareUserDao userDao;

	@Autowired
	InstagramService instagramService;

	@Autowired
	JwtUtilConfig jwtUtilConfig;

	@Autowired
	HttpServletRequest request;

	@Autowired
	TelegramService telegramService;

	@Autowired
	TwitterService twitterService;

	@Autowired
	YoutubeService youtubeService;

	// facebook Login
	@PostMapping("/facebook/user/verify-token")
	public ResponseEntity<ResponseStructure<String>> callback(@RequestParam(required = false) String code) {
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			structure.setCode(115);
			structure.setMessage("Missing or invalid authorization token");
			structure.setStatus("error");
			structure.setPlatform(null);
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.UNAUTHORIZED);
		}
		String jwtToken = token.substring(7); // remove "Bearer " prefix
		String userId = jwtUtilConfig.extractUserId(jwtToken);
		QuantumShareUser user = userDao.fetchUser(userId);
		if (user == null) {
			structure.setCode(HttpStatus.NOT_FOUND.value());
			structure.setMessage("user doesn't exists, please signup");
			structure.setStatus("error");
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);
		}
		if (code == null) {
			structure.setCode(HttpStatus.BAD_REQUEST.value());
			structure.setMessage("Please accept all the permission while login");
			structure.setPlatform("facebook");
			structure.setStatus("error");
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.BAD_REQUEST);
		}
		return faceBookAccessTokenService.verifyToken(code, user);
	}

	// Instagram
	@PostMapping("/instagram/user/verify-token")
	public ResponseEntity<ResponseStructure<String>> callbackInsta(@RequestParam(required = false) String code) {
		System.out.println("instagram invoked");
		System.out.println(code);
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			structure.setCode(115);
			structure.setMessage("Missing or invalid authorization token");
			structure.setStatus("error");
			structure.setPlatform(null);
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.UNAUTHORIZED);
		}
		String jwtToken = token.substring(7); // remove "Bearer " prefix
		String userId = jwtUtilConfig.extractUserId(jwtToken);
		QuantumShareUser user = userDao.fetchUser(userId);
		if (user == null) {
			structure.setCode(HttpStatus.NOT_FOUND.value());
			structure.setMessage("user doesn't exists, please signup");
			structure.setStatus("error");
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);
		}
		if (code == null) {
			structure.setCode(HttpStatus.BAD_REQUEST.value());
			structure.setMessage("Please accept all the permission while login");
			structure.setPlatform("instagram");
			structure.setStatus("error");
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.BAD_REQUEST);
		}
		return instagramService.verifyToken(code, user);
	}

	// Twitter Connectivity
	@GetMapping("/twitter/user/connect")
	public ResponseEntity<ResponseStructure<String>> connectTwitter() {
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			structure.setCode(115);
			structure.setMessage("Missing or invalid authorization token");
			structure.setStatus("error");
			structure.setPlatform(null);
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.UNAUTHORIZED);
		}
		String jwtToken = token.substring(7);
		String userId = jwtUtilConfig.extractUserId(jwtToken);
		QuantumShareUser user = userDao.fetchUser(userId);
		if (user == null) {
			structure.setCode(HttpStatus.NOT_FOUND.value());
			structure.setMessage("User doesn't exists, Please Signup");
			structure.setStatus("error");
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);
		}
		return twitterService.getAuthorizationUrl(user);
	}

	// Twitter
	@PostMapping("/twitter/user/verify-token")
	public ResponseEntity<ResponseStructure<String>> callbackTwitter(@RequestParam(required = false) String code) {
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			structure.setCode(115);
			structure.setMessage("Missing or invalid authorization token");
			structure.setStatus("error");
			structure.setPlatform(null);
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.UNAUTHORIZED);
		}
		String jwtToken = token.substring(7); // remove "Bearer " prefix
		String userId = jwtUtilConfig.extractUserId(jwtToken);
		QuantumShareUser user = userDao.fetchUser(userId);
		if (user == null) {
			structure.setCode(HttpStatus.NOT_FOUND.value());
			structure.setMessage("user doesn't exists, please signup");
			structure.setStatus("error");
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);
		}
		if (code == null) {
			structure.setCode(HttpStatus.BAD_REQUEST.value());
			structure.setMessage("Please accept all the permission while login");
			structure.setPlatform("facebook");
			structure.setStatus("error");
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.BAD_REQUEST);
		}
		return twitterService.verifyToken(code, user);
	}

	// Telegram login
	@GetMapping("/telegram/user/connect")
	public ResponseEntity<ResponseStructure<String>> connectTelegram() {
		System.out.println("Telegram Invoked");
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			structure.setCode(115);
			structure.setMessage("Missing or invalid authorization token");
			structure.setStatus("error");
			structure.setPlatform(null);
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.UNAUTHORIZED);
		}
		String jwtToken = token.substring(7);
		String userId = jwtUtilConfig.extractUserId(jwtToken);
		QuantumShareUser user = userDao.fetchUser(userId);
		if (user == null) {
			structure.setCode(HttpStatus.NOT_FOUND.value());
			structure.setMessage("User doesn't exists, Please Signup");
			structure.setStatus("error");
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);
		}
		return telegramService.generateTelegramCode(user);
	}

	// Fetching Group Details
	@GetMapping("/telegram/user/groupDetails")
	public ResponseEntity<ResponseStructure<String>> getGroupDetails() {
		System.out.println("Telegram Invoked");
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			structure.setCode(115);
			structure.setMessage("Missing or invalid authorization token");
			structure.setStatus("error");
			structure.setPlatform(null);
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.UNAUTHORIZED);
		}
		String jwtToken = token.substring(7);
		String userId = jwtUtilConfig.extractUserId(jwtToken);
		QuantumShareUser user = userDao.fetchUser(userId);
		if (user == null) {
			structure.setCode(HttpStatus.NOT_FOUND.value());
			structure.setMessage("User doesn't exists, Please Signup");
			structure.setStatus("error");
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);
		}
		return telegramService.pollTelegramUpdates(user);
	}

	// Youtube Connection
	@GetMapping("/youtube/user/connect")
	public ResponseEntity<ResponseStructure<String>> connectYoutube() {
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			structure.setCode(115);
			structure.setMessage("Missing or invalid authorization token");
			structure.setStatus("error");
			structure.setPlatform(null);
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.UNAUTHORIZED);
		}
		String jwtToken = token.substring(7);
		String userId = jwtUtilConfig.extractUserId(jwtToken);
		QuantumShareUser user = userDao.fetchUser(userId);
		if (user == null) {
			structure.setCode(HttpStatus.NOT_FOUND.value());
			structure.setMessage("User doesn't exists, Please Signup");
			structure.setStatus("error");
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);
		}
		return youtubeService.getAuthorizationUrl(user);
	}

	// Youtube
	@PostMapping("/youtube/user/verify-token")
	public ResponseEntity<ResponseStructure<String>> callbackYoutube(@RequestParam(required = false) String code) {
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			structure.setCode(115);
			structure.setMessage("Missing or invalid authorization token");
			structure.setStatus("error");
			structure.setPlatform(null);
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.UNAUTHORIZED);
		}
		String jwtToken = token.substring(7); 
		String userId = jwtUtilConfig.extractUserId(jwtToken);
		QuantumShareUser user = userDao.fetchUser(userId);
		if (user == null) {
			structure.setCode(HttpStatus.NOT_FOUND.value());
			structure.setMessage("User doesn't Exists, Please Signup");
			structure.setStatus("error");
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);
		}
		if (code == null) {
			structure.setCode(HttpStatus.BAD_REQUEST.value());
			structure.setMessage("Please accept all the permission while login");
			structure.setPlatform("youtube");
			structure.setStatus("error");
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.BAD_REQUEST);
		}
		return youtubeService.verifyToken(code, user);
	}

}