package guru.sfg.brewery.security;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserUnlockService {

	private final UserRepository userRepository;
	
	@Scheduled(fixedRate = 300000)
	public void unlockAccounts() {
		log.debug("Running unlock accounts");
		List<User> lockedUsers = userRepository.findAllByAccountNonLockedAndLastModifiedDateIsBefore(Boolean.FALSE, Timestamp.valueOf(LocalDateTime.now().minusSeconds(30)));
		if(!lockedUsers.isEmpty()) {
			log.debug("Locked accounts found. Unlocking...");
			lockedUsers.forEach(user -> user.setAccountNonLocked(Boolean.TRUE));
			userRepository.saveAll(lockedUsers);
		}
	}
}
