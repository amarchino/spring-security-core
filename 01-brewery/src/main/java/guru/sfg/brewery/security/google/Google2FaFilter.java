package guru.sfg.brewery.security.google;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import guru.sfg.brewery.domain.security.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class Google2FaFilter extends GenericFilterBean {
	
	private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null && !authenticationTrustResolver.isAnonymous(authentication)) {
			log.debug("Processing 2FA filter");
			if(authentication.getPrincipal() != null && authentication.getPrincipal() instanceof User) {
				User user = (User) authentication.getPrincipal();
				if(user.getUseGoogle2Fa() && user.getGoogle2FaRequired()) {
					log.debug("2FA required");
					// TODO add failure handler
				}
			}
		}
		
		chain.doFilter(req, res);
	}

}
