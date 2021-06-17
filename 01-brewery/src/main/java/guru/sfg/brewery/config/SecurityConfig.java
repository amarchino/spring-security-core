package guru.sfg.brewery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests(requests -> {
			requests.antMatchers("/", "/webjars/**", "/resources/**").permitAll();
			requests.antMatchers("/beers/find", "/beers*").permitAll();
		});
		http.authorizeRequests(requests -> requests.anyRequest().authenticated());
		http.formLogin();
		http.httpBasic();
	}
	
}
