package guru.sfg.brewery.security.google;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest.StaticResourceRequestMatcher;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import guru.sfg.brewery.domain.security.User;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Google2FaFilter extends GenericFilterBean {
	
	private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();
	private final Google2FaFailureHandler google2FaFailureHandler = new Google2FaFailureHandler();
	
	private final RequestMatcher urlIs2FA = new AntPathRequestMatcher("/user/verify2fa");
	private final RequestMatcher urlResources = new AntPathRequestMatcher("/resources/**");
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		StaticResourceRequestMatcher staticResourcesRequestMatcher = PathRequest.toStaticResources().atCommonLocations();
		if(urlIs2FA.matches(req) || urlResources.matches(req) || staticResourcesRequestMatcher.matches(req)) {
			chain.doFilter(req, res);
			return;
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null && !authenticationTrustResolver.isAnonymous(authentication)) {
			log.debug("Processing 2FA filter");
			if(authentication.getPrincipal() != null && authentication.getPrincipal() instanceof User) {
				User user = (User) authentication.getPrincipal();
				if(user.getUseGoogle2Fa() && user.getGoogle2FaRequired()) {
					log.debug("2FA required");
					google2FaFailureHandler.onAuthenticationFailure(req, res, null);
					return;
				}
			}
		}
		
		chain.doFilter(req, res);
	}

}
