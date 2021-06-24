package guru.sfg.brewery.security.listener;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthenticationSuccessListener {
	@EventListener
	public void listen(AuthenticationSuccessEvent event) {
		log.debug("User logged in ok");
		Authentication authentication = event.getAuthentication();
		if(authentication.getPrincipal() instanceof User) {
			User user = (User) authentication.getPrincipal();
			log.debug("User name logged in: " + user.getUsername());
		}
		if(authentication.getDetails() instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
			log.debug("Source IP: " + details.getRemoteAddress());
		}
	}
}
