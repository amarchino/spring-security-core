package guru.sfg.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests(requests ->
			requests.antMatchers("/", "/webjars/**", "/resources/**").permitAll()
				.antMatchers("/beers/find", "/beers*").permitAll()
				.antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
				.mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll()
		);
		http.authorizeRequests(requests -> requests.anyRequest().authenticated());
		http.formLogin();
		http.httpBasic();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder encoder = passwordEncoder();
		auth.inMemoryAuthentication()
			.withUser("spring")
			.password(encoder.encode("guru"))
			.roles("ADMIN")
			.and()
			.withUser("user")
			.password(encoder.encode("password"))
			.roles("USER");
		auth.inMemoryAuthentication().withUser("scott").password(encoder.encode("tiger")).roles("CUSTOMER");
	}
	
//	@Override
//	@Bean
//	protected UserDetailsService userDetailsService() {
//		
//		
//		UserDetails admin = User.withDefaultPasswordEncoder()
//				.username("spring")
//				.password("guru")
//				.roles("ADMIN")
//				.build();
//		UserDetails user = User.withDefaultPasswordEncoder()
//				.username("user")
//				.password("password")
//				.roles("USER")
//				.build();
//		return new InMemoryUserDetailsManager(admin, user);
//	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new StandardPasswordEncoder();
	}
	
}
