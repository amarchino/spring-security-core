package guru.sfg.brewery.security.google;

import java.util.List;

import org.springframework.stereotype.Component;

import com.warrenstrange.googleauth.ICredentialRepository;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleCredentialRepository implements ICredentialRepository {
	
	private final UserRepository userRepository;

	@Override
	public String getSecretKey(String userName) {
		User user = userRepository.findByUsername(userName).orElseThrow();
		return user.getGoogle2FaSecret();
	}

	@Override
	public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
		User user = userRepository.findByUsername(userName).orElseThrow();
		user.setGoogle2FaSecret(secretKey);
		user.setUseGoogle2Fa(Boolean.TRUE);
		userRepository.save(user);
	}

}
