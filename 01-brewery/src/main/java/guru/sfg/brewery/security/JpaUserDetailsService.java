package guru.sfg.brewery.security;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class JpaUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Setting User info via JPA");
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User name: " + username + " not found"));
		Set<Authority> authorities = user.getAuthorities();
		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				Boolean.TRUE.equals(user.getEnabled()),
				Boolean.TRUE.equals(user.getAccountNonExpired()),
				Boolean.TRUE.equals(user.getCredentialsNonExpired()),
				Boolean.TRUE.equals(user.getAccountNonLocked()),
				authorities != null && !authorities.isEmpty()
					? authorities
						.stream()
						.map(Authority::getPermission)
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toSet())
					: Collections.emptySet());
	}
	
}
