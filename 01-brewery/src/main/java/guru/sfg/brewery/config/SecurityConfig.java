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
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.security.JpaUserDetailsService;
import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.RestUrlParamAuthFilter;
import guru.sfg.brewery.security.google.Google2FaFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final UserRepository userRepository;
	private final PersistentTokenRepository persistentTokenRepository;
	private final Google2FaFilter google2FaFilter;
	private final PasswordEncoder passwordEncoder;
	
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
		http.addFilterBefore(google2FaFilter, SessionManagementFilter.class);
		
		http.cors();
		http.csrf().ignoringAntMatchers("/h2-console/**", "/api/**");
		http.authorizeRequests(authorizeRequestsCustomizer ->
			authorizeRequestsCustomizer
				.antMatchers("/h2-console/**").permitAll() // do not use in production
				.antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
				.anyRequest().authenticated()
		);

		http.formLogin(formLoginCustomizer ->
			formLoginCustomizer
				.loginProcessingUrl("/login")
				.loginPage("/").permitAll()
				.successForwardUrl("/")
				.defaultSuccessUrl("/")
				.failureUrl("/?error")
		);
		http.rememberMe(rememberMeCustomizer -> 
			rememberMeCustomizer
//				.key("sfg-key")
//				.userDetailsService(userDetailsService())
				.tokenRepository(persistentTokenRepository)
		);
		
		http.logout(logoutCustomizer ->
			logoutCustomizer
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout", HttpMethod.GET.name()))
				.logoutSuccessUrl("/?logout")
				.permitAll()
		);

		http.httpBasic();
		// H2 console config
		http.headers().frameOptions(frameOptionsCustomizer -> frameOptionsCustomizer.sameOrigin());
	}
	
	@Override
	@Bean
	protected UserDetailsService userDetailsService() {
		return new JpaUserDetailsService(userRepository);
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
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
		auth.inMemoryAuthentication().withUser("scott").password(passwordEncoder.encode("tiger")).roles("CUSTOMER");
	}
	
}
