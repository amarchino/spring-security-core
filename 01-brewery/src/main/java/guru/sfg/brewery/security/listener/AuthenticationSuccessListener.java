package guru.sfg.brewery.security.listener;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.LoginSuccess;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginSuccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {
	
	private final LoginSuccessRepository loginSuccessRepository;
	
	@EventListener
	public void listen(AuthenticationSuccessEvent event) {
		log.debug("User logged in ok");
		Authentication authentication = event.getAuthentication();
		
		LoginSuccess.LoginSuccessBuilder builder = LoginSuccess.builder();
		
		if(authentication.getPrincipal() instanceof User) {
			User user = (User) authentication.getPrincipal();
			builder.user(user);
			log.debug("User name logged in: " + user.getUsername());
		}
		if(authentication.getDetails() instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
			builder.sourceIp(details.getRemoteAddress());
			log.debug("Source IP: " + details.getRemoteAddress());
		}
		
		LoginSuccess loginSuccess = loginSuccessRepository.save(builder.build());
		log.debug("Login success saved. Id: " + loginSuccess.getId());
	}
}
