package guru.sfg.brewery.config;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.ICredentialRepository;

@Configuration
public class SecurityBeans {

	@Bean
	public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
		JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl = new JdbcTokenRepositoryImpl();
		jdbcTokenRepositoryImpl.setDataSource(dataSource);
		return jdbcTokenRepositoryImpl;
	}
	
	@Bean
	public AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
	}
	
	@Bean
	public GoogleAuthenticator googleAuthenticator(ICredentialRepository iCredentialRepository) {
		GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder configBuilder = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder();
		configBuilder
			.setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(60))
			.setWindowSize(10)
			.setNumberOfScratchCodes(0);
		GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(configBuilder.build());
		googleAuthenticator.setCredentialRepository(iCredentialRepository);
		return googleAuthenticator;
	}
}
