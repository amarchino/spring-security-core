package guru.sfg.brewery.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestHeaderAuthFilter extends AbstractAuthenticationProcessingFilter {
	
	public RestHeaderAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (this.logger.isDebugEnabled()) {
			log.debug("Request is to process authentication");
		}
		Authentication authenticationResult = attemptAuthentication(request, response);
		if(authenticationResult != null) {
			successfulAuthentication(request, response, chain, authenticationResult);
		} else {
			chain.doFilter(request, response);
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
		SecurityContextHolder.getContext().setAuthentication(authResult);
		if (this.logger.isDebugEnabled()) {
			this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authResult));
		}
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
		String userName = getUsername(request);
		String password = getPassword(request);
		
		if(userName == null) {
			userName = "";
		}
		if(password == null) {
			password = "";
		}
		log.debug("Authenticating user: " + userName);
		
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
		if(!ObjectUtils.isEmpty(userName)) {
			return getAuthenticationManager().authenticate(token);
		}
		return null;
	}

	private String getPassword(HttpServletRequest request) {
		return request.getHeader("Api-Secret");
	}

	private String getUsername(HttpServletRequest request) {
		return request.getHeader("Api-Key");
	}

}
