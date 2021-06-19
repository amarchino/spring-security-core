package guru.sfg.brewery.web.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

public class PasswordEncodingTest {
	static final String PASSWORD = "password";
	
	@Test
	void hashingExample() {
		System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
		System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
		
		String salted = PASSWORD + "ThisIsMySALTVALUE";
		System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes()));
	}
	
	@Test
	void testNoOp() {
		PasswordEncoder noOp = NoOpPasswordEncoder.getInstance();
		System.out.println(noOp.encode(PASSWORD));
		assertEquals(noOp.encode(PASSWORD), PASSWORD);
	}
}
