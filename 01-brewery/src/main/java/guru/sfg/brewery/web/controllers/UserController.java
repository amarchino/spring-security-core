package guru.sfg.brewery.web.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {
	
	private final UserRepository userRepository;
	private final GoogleAuthenticator googleAuthenticator;
	
	@GetMapping("/register2fa")
	public String register2fa(Model model) {
		
		User user = getUser();
		String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthURL("SFG", user.getUsername(), googleAuthenticator.createCredentials(user.getUsername()));
		log.debug("Google QR URL: " + otpAuthURL);
		model.addAttribute("googleurl", otpAuthURL);
		
		return "user/register2fa";
	}

	@PostMapping("/register2fa")
	public String confirm2fa(@RequestParam Integer verifyCode) {
		User user = getUser();
		log.debug("Entered code is " + verifyCode);
		if(!googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
			return "user/register2fa";
		}
		User savedUser = userRepository.findById(user.getId()).orElseThrow();
		savedUser.setUseGoogle2Fa(Boolean.TRUE);
		userRepository.save(savedUser);
		return "index";
	}
	
	@GetMapping("/verify2fa")
	public String verify2fa() {
		return "user/verify2fa";
	}
	
	@PostMapping("/verify2fa")
	public String verifyPostOf2fa(@RequestParam Integer verifyCode) {
		User user = getUser();
		log.debug("Entered code is " + verifyCode);
		if(!googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
			return "user/verify2fa";
		}
		user.setGoogle2FaRequired(Boolean.FALSE);
		return "index";
	}
	
	private User getUser() {
		return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
