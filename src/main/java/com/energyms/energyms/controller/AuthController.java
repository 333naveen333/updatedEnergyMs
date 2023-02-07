package com.energyms.energyms.controller;

import com.energyms.energyms.dto.LoginDto;
import com.energyms.energyms.dto.SignUpDto;
import com.energyms.energyms.jwt.JwtTokenProvider;
import com.energyms.energyms.model.User;
import com.energyms.energyms.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtTokenProvider tokenProvider;

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Validated @RequestBody SignUpDto signUpDto) {

		if (userRepository.existsByEmailId(signUpDto.getEmailId())) {
			return new ResponseEntity<>("emailId is already taken!", HttpStatus.BAD_REQUEST);
		}

		User user = new User();
		user.setFullName(signUpDto.getFullName());
		user.setEmailId(signUpDto.getEmailId());

		user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
		user.setMobileNumber(signUpDto.getMobileNumber());

		userRepository.save(user);
		return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
	}

	@PostMapping("/login")
	public ResponseEntity<?> authentication(@RequestBody LoginDto loginDto) throws Exception {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmailId(), loginDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = tokenProvider.generateToken(authentication);
		return ResponseEntity.ok((token));
	}

}
