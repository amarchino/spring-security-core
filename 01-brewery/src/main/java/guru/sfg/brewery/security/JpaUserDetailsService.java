package guru.sfg.brewery.security;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class JpaUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User name: " + username + " not found"));
		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				Boolean.TRUE.equals(user.getEnabled()),
				Boolean.TRUE.equals(user.getAccountNonExpired()),
				Boolean.TRUE.equals(user.getCredentialsNonExpired()),
				Boolean.TRUE.equals(user.getAccountNonLocked()),
				user.getAuthorities() != null && !user.getAuthorities().isEmpty()
					? user.getAuthorities()
						.stream()
						.map(Authority::getRole)
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toSet())
					: Collections.emptySet());
	}
	
}