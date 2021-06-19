package guru.sfg.brewery.web.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

public class PasswordEncodingTest {
	static final String PASSWORD = "password";
	
	@Test
	void hashingExample() {
		System.out.println("hashingExample: " + DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
		System.out.println("hashingExample: " + DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
		
		String salted = PASSWORD + "ThisIsMySALTVALUE";
		System.out.println("hashingExample: " + DigestUtils.md5DigestAsHex(salted.getBytes()));
	}
	
	@Test
	void testNoOp() {
		PasswordEncoder noOp = NoOpPasswordEncoder.getInstance();
		String encodedPassword = noOp.encode(PASSWORD);
		System.out.println("testNoOp: " + encodedPassword);
		assertTrue(noOp.matches(PASSWORD, encodedPassword));
	}
	
	@Test
	void testLdap() {
		PasswordEncoder ldap = new LdapShaPasswordEncoder();
		System.out.println("testLdap: " + ldap.encode(PASSWORD));
		String encodedPassword = ldap.encode(PASSWORD);
		System.out.println("testLdap: " + encodedPassword);
		
		assertTrue(ldap.matches(PASSWORD, encodedPassword));
	}
}
