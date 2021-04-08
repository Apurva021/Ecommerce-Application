package com.apurva.gatewayservicev1;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class GatewayController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MyUserDetailsService myUserDetailsService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public boolean canAccess(HttpServletRequest request, Integer userIdInteger) {
		String jwtString = request.getHeader("Authorization").substring(7);
		String emailString = jwtUtil.extractUsername(jwtString);
		Integer idInteger = Integer.parseInt(jwtUtil.getPayload(jwtString));
		
		return idInteger.equals(userIdInteger);
	}
	
	@PostMapping("/signup")
	public String register(@RequestBody User user) throws Exception{
		User dupUser = userRepository.findByEmailString(user.getEmailString());
		
		if(dupUser != null) {
			throw new Exception(user.getEmailString() + " is already registerd!");
		}
		
		user.setPasswordString(bCryptPasswordEncoder.encode(user.getPasswordString()));
		userRepository.save(user);
		return "User registered";
	}
	
	@GetMapping("/login")
	public String loginPage(HttpServletRequest request) {
		return "Login Page " + jwtUtil.getFullName(request.getHeader("Authorization").substring(7));
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> loginFun(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
		
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsernameString(), authenticationRequest.getPasswordString()));
		}
		catch (BadCredentialsException e) {
			// TODO: handle exception
			throw new Exception("Incorrect username or password", e);
		}
		
		UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getUsernameString());
		
		Integer idInteger = userRepository.findByEmailString(authenticationRequest.getUsernameString()).getUserIdInteger();
		User user = userRepository.findByEmailString(authenticationRequest.getUsernameString());
		
		String jwtString = jwtUtil.generateToken(userDetails, Integer.toString(idInteger), user.getFirstNameString(), user.getLastNameString());
		
		return ResponseEntity.ok(new AuthenticationResponse(jwtString));
	}
	
	@PostMapping("/change-password/{userIdInteger}")
	public String changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest changePasswordRequest, @PathVariable Integer userIdInteger) throws Exception {
		if(!canAccess(request, userIdInteger)) {
			throw new Exception("Access denied to sensitive Resource!");
		}
		
		User user = userRepository.findByUserIdInteger(userIdInteger);
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(user.getEmailString(), changePasswordRequest.getCurrentPasswordString()));
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new Exception("Current password entered is wrong!");
		}
		
		if(!changePasswordRequest.getNewPasswordString().equals(changePasswordRequest.getConfirmNewPasswordString())) {
			throw new Exception("The new password fields do not match!");
		}
		
		user.setPasswordString(bCryptPasswordEncoder.encode(changePasswordRequest.getNewPasswordString()));
		userRepository.save(user);
		
		return "Password Updated Succesfully!";
		
		
	}
}
