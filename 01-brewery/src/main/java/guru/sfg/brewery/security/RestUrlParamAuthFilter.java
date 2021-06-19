package guru.sfg.brewery.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

public class RestUrlParamAuthFilter extends AbstractRestAuthFilter {
	
	public RestUrlParamAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
	}
	
	@Override
	protected String getPassword(HttpServletRequest request) {
		return request.getParameter("Api-Secret");
	}

	@Override
	protected String getUsername(HttpServletRequest request) {
		return request.getParameter("Api-Key");
	}

}
