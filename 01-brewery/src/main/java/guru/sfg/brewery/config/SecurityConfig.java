package guru.sfg.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.security.JpaUserDetailsService;
import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.RestUrlParamAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final UserRepository userRepository;
	
	private RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
		RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/**"));
		filter.setAuthenticationManager(authenticationManager);
		return filter;
	}
	private RestUrlParamAuthFilter restUrlParamAuthFilter(AuthenticationManager authenticationManager) {
		RestUrlParamAuthFilter filter = new RestUrlParamAuthFilter(new AntPathRequestMatcher("/**"));
		filter.setAuthenticationManager(authenticationManager);
		return filter;
	}
	
	@Bean
	public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
		return new SecurityEvaluationContextExtension();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(restHeaderAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(restUrlParamAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
		
		http.csrf().ignoringAntMatchers("/h2-console/**", "/api/**");
		http.authorizeRequests(requests ->
			requests
				.antMatchers("/h2-console/**").permitAll() // do not use in production
				.antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
				.anyRequest().authenticated()
		);

		http.formLogin(loginConfigurer ->
			loginConfigurer
				.loginProcessingUrl("/login")
				.loginPage("/").permitAll()
				.successForwardUrl("/")
				.defaultSuccessUrl("/")
				.failureUrl("/?error")
		);
		http.logout(logoutConfigurer ->
			logoutConfigurer
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout", HttpMethod.GET.name()))
				.logoutSuccessUrl("/?logout")
				.permitAll()
		);

		http.httpBasic();
		// H2 console config
		http.headers().frameOptions(fo -> fo.sameOrigin());
	}
	
	@Override
	@Bean
	protected UserDetailsService userDetailsService() {
		return new JpaUserDetailsService(userRepository);
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder encoder = passwordEncoder();
		auth.userDetailsService(userDetailsService());
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
