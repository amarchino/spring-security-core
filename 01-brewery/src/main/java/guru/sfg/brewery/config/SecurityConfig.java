package guru.sfg.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactory;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
		RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/**"));
		filter.setAuthenticationManager(authenticationManager);
		return filter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(restHeaderAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
		
		http.csrf().disable();
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
			//.password(encoder.encode("guru"))
			.password("{ldap}{SSHA}TeIPoQ1l74LT8Lvf8EzQrLWxUwH1LEdCWndc4w==")
			.roles("ADMIN")
			.and()
			.withUser("user")
			//.password(encoder.encode("password"))
			.password("{noop}password")
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
		return SfgPasswordEncoderFactory.createDelegatingPasswordEncoder();
	}
	
}
