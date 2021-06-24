package guru.sfg.brewery.security.listener;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.User;
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
		
		if(loginFailure.getUser() != null) {
			lockUserAccount(loginFailure.getUser());
		}
	}

	private void lockUserAccount(User user) {
		List<LoginFailure> failures = loginFailureRepository.findAllByUserAndCreationDateIsAfter(user, Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
		if(failures.size() > 3) {
			log.debug("Locking user account...");
			user.setAccountNonLocked(Boolean.FALSE);
			userRepository.save(user);
		}
	}
}
