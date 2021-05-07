package com.apurva.gatewayservicev1;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.apurva.gatewayservicev1.kafka.KafkaController;

@Controller
@RequestMapping
public class GatewayController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private KafkaController kafkaController;
	
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
	
	/**
	 * Instead of canAccess now use cookies to implement access-control
	 * @param request
	 * @return
	 */
	public String getJwtToken(HttpServletRequest request) {
		Cookie cookies[] = request.getCookies();
		for(Cookie cookie: cookies) {
			if(cookie.getName().equals("authCookie")) {
				return cookie.getValue();
			}
		}
		
		return "";
	}
	/*
	@GetMapping("/signup")
	public String getSignUpPage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		SignUpRequest signUpRequest = new SignUpRequest();
		model.addAttribute("signUpRequest", signUpRequest);
		return "signup";
	}
	
	@PostMapping(path="/signup")
	public String register(@ModelAttribute("signUpRequest") SignUpRequest signUpRequest, HttpServletResponse response) throws Exception{
		User user = new User();
		
		
		user.setEmailString(signUpRequest.getEmailString());
		user.setFirstNameString(signUpRequest.getFirstNameString());
		user.setLastNameString(signUpRequest.getLastNameString());
		user.setPhoneNumberString(signUpRequest.getPhoneNumberString());
		
		User dupUser = userRepository.findByEmailString(user.getEmailString());
		
		if(dupUser != null) {
			throw new Exception(user.getEmailString() + " is already registerd!");
		}
		
		if(!signUpRequest.getPasswordString().equals(signUpRequest.getConfirmPasswordString())) {
			throw new Exception("Passwrds dont match!!");
		}
		
		user.setPasswordString(bCryptPasswordEncoder.encode(signUpRequest.getPasswordString()));
		user.setConfirmationTokenString(UUID.randomUUID().toString());
		user.setSeller(false);
		user.setEnabled(false);
		userRepository.save(user);
		kafkaController.signUpNotification(user);
		response.sendRedirect("/authenticate");
		
		return "authenticate";
	}
	*/
	
	@PostMapping(path = "/signup")
	public void registerUser(@ModelAttribute SignUpRequest signUpRequest, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		model.addAttribute("signUpRequest", signUpRequest);
		
		User dupUser = userRepository.findByEmailString(signUpRequest.getEmailString());
		
		if(dupUser != null) {
			throw new Exception("Email already registered!");
		}
		
		if(!signUpRequest.getPasswordString().equals(signUpRequest.getConfirmPasswordString())) {
			throw new Exception("The passwords do not mathch!");
		}
		
		User user = new User();
		user.setEmailString(signUpRequest.getEmailString());
		user.setPhoneNumberString(signUpRequest.getPhoneNumberString());
		user.setFirstNameString(signUpRequest.getFirstNameString());
		user.setLastNameString(signUpRequest.getLastNameString());
		user.setPasswordString(bCryptPasswordEncoder.encode(signUpRequest.getPasswordString()));
		user.setConfirmationTokenString(UUID.randomUUID().toString());
		user.setSeller(false);
		user.setEnabled(false);
		userRepository.save(user);
		
		response.sendRedirect("/api/user/authenticate?verify=email");
		
	}
	
	@GetMapping("/confirm-account")
	public String confirmAccount(HttpServletRequest request, @RequestParam String confirmationToken) {
		User user = userRepository.findByConfirmationTokenString(confirmationToken);
		
		if(user == null) {
			return "Invalid Confirmation Token";
		}
		
		if(user.isEnabled()) {
			return "Email already Verified!";
		}
		
		user.setEnabled(true);
		userRepository.save(user);
		return "Email Verified";
	}
	
	@GetMapping("/login")
	public String loginPage(HttpServletRequest request, HttpServletResponse response) {
		return "Login Page " + jwtUtil.getFullName(((String) request.getAttribute("Authorization")).substring(7)) 
		+ " isSeller:" + jwtUtil.isSeller(((String) request.getAttribute("Authorization")).substring(7));
	}
	
	@GetMapping("/authenticate")
	public String getAuthPage(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		AuthenticationRequest authenticationRequest = new AuthenticationRequest();
		model.addAttribute("authenticationRequest", authenticationRequest);
		if(request.getCookies() != null) {
			Cookie[] cookies = request.getCookies();
			for(Cookie cookie: cookies) {
				if(cookie.getName().equals("authCookie")) {
					//System.out.println("ALREADY LOGGED IN");
					try {
						response.sendRedirect("/hello");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		
		return "authenticate";
	}
	
	@PostMapping(path="/authenticate")
	public String loginFun(@ModelAttribute("authenticationRequest") AuthenticationRequest authenticationRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		try {
			User user = userRepository.findByEmailString(authenticationRequest.getUsernameString());
			if(user == null) {
				throw new Exception("User not found exception!");
			}
		} catch(Exception e) {
			return "redirect:api/user/authenticate?verify=credentials";
		}
		
		try {
			User user = userRepository.findByEmailString(authenticationRequest.getUsernameString());
			if(!user.isEnabled()) {
				throw new Exception("Account not verifed");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			return "redirect:api/user/authenticate?verify=email";
		}
		
		
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsernameString(), authenticationRequest.getPasswordString()));
		}
		catch (BadCredentialsException e) {
			// TODO: handle exception
			return "redirect:api/user/authenticate?verify=credentials";
		}
		
		UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getUsernameString());
		
		Integer idInteger = userRepository.findByEmailString(authenticationRequest.getUsernameString()).getUserIdInteger();
		User user = userRepository.findByEmailString(authenticationRequest.getUsernameString());
		
		String jwtString = jwtUtil.generateToken(userDetails, Integer.toString(idInteger), user.getFirstNameString(), user.getLastNameString(), user.isSeller());
		
		
		response.setHeader("Authorization", "Bearer " + jwtString);
		Cookie cookie = new Cookie("authCookie", jwtString);
		cookie.setMaxAge(60 * 60 * 10);
		cookie.setPath("/");
		response.addCookie(cookie);
		
		response.sendRedirect("/hello");
		
		return "hello";
		
	}
	
	@GetMapping("/hello")
	public String hello(HttpServletRequest request, Model model) throws Exception{
		Cookie[] cookies = request.getCookies();
		String jwtString = "";
		
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("authCookie")) {
				jwtString = cookie.getValue();
			}
		}
		
		String jwtTokenString = jwtString;
		
		
		String usernameString = jwtUtil.extractUsername(jwtTokenString);
		
		model.addAttribute("name", usernameString);
		
		return "hello";
	}
	
	
	@PostMapping("/change-password")
	public String changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest changePasswordRequest) throws Exception {
		
		String jwtString = ((String) request.getAttribute("Authorization")).substring(7);
		
		Integer userIdInteger = Integer.parseInt(jwtUtil.getPayload(jwtString));
		
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
		
		return kafkaController.passwordUpdate(user);
	}
	
	@GetMapping("/forgot-password/{userEmailId}")
	public String forgotPassword(HttpServletRequest request ,@PathVariable String userEmailId) throws Exception {
		User user = userRepository.findByEmailString(userEmailId);
		if(user == null) {
			throw new Exception("No such email registered!");
		}
		
		StringBuilder newPasswordBuilder = new StringBuilder();
		String candidateString = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		for(int i=0;i<8;++i) {
			newPasswordBuilder.append(candidateString.charAt(random.nextInt(candidateString.length())));
		}
		//this email needs to be sent to the user from the email service
		
		
		user.setPasswordString(bCryptPasswordEncoder.encode(newPasswordBuilder.toString()));
		userRepository.save(user);
		
		return kafkaController.forgotPassword(user, newPasswordBuilder.toString());
		
	
	}
	
	@GetMapping("/signout")
	public String logoutFunction(Model model ,HttpServletRequest request, HttpServletResponse response) throws Exception {
		Cookie cookie = new Cookie("authCookie", "");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
		return "signout";
	}
	
	@PostMapping("/signout")
	public ResponseEntity<?> logoutPostFunction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Cookie cookie = new Cookie("authCookie", "");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		response.addCookie(cookie);
		
		return new ResponseEntity<>("",HttpStatus.OK);
	}
	
}
