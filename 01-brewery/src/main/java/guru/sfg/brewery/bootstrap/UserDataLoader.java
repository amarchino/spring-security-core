package guru.sfg.brewery.bootstrap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.domain.security.User.UserBuilder;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserDataLoader implements CommandLineRunner {
	
	private final UserRepository userRepository;
	private final AuthorityRepository authorityRepository;
	private final PasswordEncoder passwordEncoder;
	
	private Map<String, Authority> autorities = new HashMap<>();
	
	@Override
	@Transactional
    public void run(String... args) {
		addAuthorities();
		log.debug("Authorities loaded: " + authorityRepository.count());
		addUsers();
		log.debug("Users loaded: " + userRepository.count());
    }

	private void addAuthorities() {
		autorities.put("ADMIN", addAuthority("ROLE_ADMIN"));
		autorities.put("USER", addAuthority("ROLE_USER"));
		autorities.put("CUSTOMER", addAuthority("ROLE_CUSTOMER"));
	}
	
	private Authority addAuthority(String role) {
		Optional<Authority> savedAuthority = authorityRepository.findByRole(role);
		if(savedAuthority.isPresent()) {
			log.debug("Role " + role + " already present");
			return savedAuthority.get();
		}
		log.debug("Role " + role + " created");
		return authorityRepository.save(Authority.builder().role(role).build());
	}

	private void addUsers() {
		addUser("spring", "guru", "ADMIN");
		addUser("user", "password", "USER");
		addUser("scott", "tiger", "CUSTOMER");
	}

	private User addUser(String username, String password, String... roles) {
		Optional<User> savedUser = userRepository.findByUsername(username);
		if(savedUser.isPresent()) {
			log.debug("User " + username + " already present");
			return savedUser.get();
		}
		log.debug("User " + username + " created");
		UserBuilder builder = User.builder()
			.username(username)
			.password(passwordEncoder.encode(password));
		for(String role : roles) {
			builder = builder.authority(autorities.get(role));
		}
		
		return userRepository.save(builder.build());
	}
}
