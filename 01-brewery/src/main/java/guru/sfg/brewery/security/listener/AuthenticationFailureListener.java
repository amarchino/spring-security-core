package guru.sfg.brewery.security.listener;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.repositories.security.LoginFailureRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener {

	private final LoginFailureRepository loginFailureRepository;
	private final UserRepository userRepository;

	@EventListener
	public void listen(AuthenticationFailureBadCredentialsEvent event) {
		log.debug("User logged in failed");
		LoginFailure.LoginFailureBuilder builder = LoginFailure.builder()
				.cause("BadCredentials");
		Authentication authentication = event.getAuthentication();
		if(authentication.getPrincipal() instanceof String) {
			String username = (String) authentication.getPrincipal();
			log.debug("Attempted user name: " + username);
			builder.username(username);
			userRepository.findByUsername(username).ifPresent(user -> builder.user(user));
		}
		if(authentication.getDetails() instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
			log.debug("Source IP: " + details.getRemoteAddress());
			builder.sourceIp(details.getRemoteAddress());
		}
		LoginFailure loginFailure = loginFailureRepository.save(builder.build());
		log.debug("Login failure saved. Id: " + loginFailure.getId());
	}
}
