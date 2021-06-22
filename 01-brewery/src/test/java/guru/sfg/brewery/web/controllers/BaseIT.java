package guru.sfg.brewery.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public abstract class BaseIT {
	
	@Autowired WebApplicationContext wac;

    protected MockMvc mockMvc;
    
    @BeforeEach
    protected void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }
    
    protected RequestPostProcessor admin() {
    	return httpBasic("spring", "guru");
    }
    protected RequestPostProcessor user() {
    	return httpBasic("user", "password");
    }
    protected RequestPostProcessor customer() {
    	return httpBasic("scott", "tiger");
    }
    
    public static Stream<Arguments> getStreamAllUsers() {
    	return Stream.of(
    		Arguments.of("spring", "guru"),
    		Arguments.of("user", "password"),
    		Arguments.of("scott", "tiger")
    	);
    }
    public static Stream<Arguments> getStreamNotAdmin() {
    	return Stream.of(
    		Arguments.of("user", "password"),
    		Arguments.of("scott", "tiger")
    	);
    }
    public static Stream<Arguments> getStreamAdminCustomer() {
    	return Stream.of(
			Arguments.of("spring", "guru"),
			Arguments.of("scott", "tiger")
    	);
    }

}
