package guru.sfg.brewery.security.listener;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthenticationFailureListener {

	@EventListener
	public void listen(AuthenticationFailureBadCredentialsEvent event) {
		log.debug("User logged in failed");
		Authentication authentication = event.getAuthentication();
		if(authentication.getPrincipal() instanceof String) {
			String user = (String) authentication.getPrincipal();
			log.debug("Attempted user name: " + user);
		}
		if(authentication.getDetails() instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
			log.debug("Source IP: " + details.getRemoteAddress());
		}
	}
}
