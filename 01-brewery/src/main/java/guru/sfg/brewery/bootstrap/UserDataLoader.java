package guru.sfg.brewery.bootstrap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
@Order(1)
public class UserDataLoader implements CommandLineRunner {
	
	private final UserRepository userRepository;
	private final AuthorityRepository authorityRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	
	private Map<ValuedEnum, Authority> autorities = new HashMap<>();
	private Map<ValuedEnum, Role> roles = new HashMap<>();
	
	@Override
	@Transactional
    public void run(String... args) {
		// Authorities
		Stream.of(BeerAuthorities.values()).forEach(this::addAuthority);
		Stream.of(BreweryAuthorities.values()).forEach(this::addAuthority);
		Stream.of(CustomerAuthorities.values()).forEach(this::addAuthority);
		Stream.of(BeerOrderAuthorities.values()).forEach(this::addAuthority);
		Stream.of(CustomerBeerOrderAuthorities.values()).forEach(this::addAuthority);
		log.debug("Authorities loaded: " + authorityRepository.count());
		
		// Roles
		addRole(Roles.ADMIN, BeerAuthorities.CREATE, BeerAuthorities.READ, BeerAuthorities.UPDATE, BeerAuthorities.DELETE,
				BreweryAuthorities.CREATE, BreweryAuthorities.READ, BreweryAuthorities.UPDATE, BreweryAuthorities.DELETE,
				CustomerAuthorities.CREATE, CustomerAuthorities.READ, CustomerAuthorities.UPDATE, CustomerAuthorities.DELETE,
				BeerOrderAuthorities.CREATE, BeerOrderAuthorities.READ, BeerOrderAuthorities.UPDATE, BeerOrderAuthorities.DELETE, BeerOrderAuthorities.PICKUP);
		addRole(Roles.CUSTOMER, BeerAuthorities.READ, CustomerAuthorities.READ, BreweryAuthorities.READ,
				CustomerBeerOrderAuthorities.CREATE, CustomerBeerOrderAuthorities.READ, CustomerBeerOrderAuthorities.UPDATE, CustomerBeerOrderAuthorities.DELETE, CustomerBeerOrderAuthorities.PICKUP);
		addRole(Roles.USER, BeerAuthorities.READ);
		log.debug("Roles loaded: " + roleRepository.count());
		
		// Users
		addUser("spring", "guru", Roles.ADMIN);
		addUser("user", "password", Roles.USER);
		addUser("scott", "tiger", Roles.CUSTOMER);
		log.debug("Users loaded: " + userRepository.count());
    }

	
	private Authority addAuthority(ValuedEnum authorityEnum) {
		Optional<Authority> savedAuthority = authorityRepository.findByPermission(authorityEnum.value());
		if(savedAuthority.isPresent()) {
			log.debug("Authority " + authorityEnum.asString() + " already present");
			return savedAuthority.get();
		}
		log.debug("Authority " + authorityEnum + " created");
		Authority authority = authorityRepository.save(Authority.builder().permission(authorityEnum.value()).build());
		autorities.put(authorityEnum, authority);
		return authority;
	}
	
	private Role addRole(ValuedEnum roleEnum, ValuedEnum... authorityValues) {
		Optional<Role> savedRole = roleRepository.findByName(roleEnum.value());
		if(savedRole.isPresent()) {
			log.debug("Role " + roleEnum.asString() + " already present");
			return savedRole.get();
		}
		log.debug("Role " + roleEnum + " created");
		Role role = roleRepository.save(
				Role.builder()
				.name(roleEnum.value())
				.authorities(Stream.of(authorityValues).map(autorities::get).collect(Collectors.toSet()))
				.build());
		roles.put(roleEnum, role);
		return role;
	}

	private User addUser(String username, String password, ValuedEnum... roleValues) {
		Optional<User> savedUser = userRepository.findByUsername(username);
		if(savedUser.isPresent()) {
			log.debug("User " + username + " already present");
			return savedUser.get();
		}
		log.debug("User " + username + " created");

		return userRepository.save(User.builder()
			.username(username)
			.password(passwordEncoder.encode(password))
			.roles(Stream.of(roleValues).map(roles::get).collect(Collectors.toSet()))
			.build());
	}
	
	private static interface ValuedEnum {
		default String value() {
			return type() + "." + name().toLowerCase();
		}
		String name();
		String type();
		default String asString() {
			return type() + "." + name();
		}
	}
	private static enum BeerAuthorities implements ValuedEnum {
		CREATE, UPDATE, READ, DELETE;
		@Override
		public String type() {
			return "beer";
		}
	}
	private static enum CustomerAuthorities implements ValuedEnum {
		CREATE, UPDATE, READ, DELETE;
		@Override
		public String type() {
			return "customer";
		}
	}
	private static enum BreweryAuthorities implements ValuedEnum {
		CREATE, UPDATE, READ, DELETE;
		@Override
		public String type() {
			return "brewery";
		}
	}
	private static enum BeerOrderAuthorities implements ValuedEnum {
		CREATE, UPDATE, READ, DELETE, PICKUP;
		@Override
		public String type() {
			return "order";
		}
	}
	private static enum CustomerBeerOrderAuthorities implements ValuedEnum {
		CREATE, UPDATE, READ, DELETE, PICKUP;
		@Override
		public String type() {
			return "customer.order";
		}
	}
	private static enum Roles implements ValuedEnum {
		ADMIN, CUSTOMER, USER;
		@Override
		public String value() {
			return name();
		}
		@Override
		public String type() {
			return "";
		}
	}
}
