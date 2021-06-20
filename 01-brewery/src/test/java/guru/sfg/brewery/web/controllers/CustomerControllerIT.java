package guru.sfg.brewery.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created by jt on 6/12/20.
 */
@SpringBootTest
public class CustomerControllerIT extends BaseIT {
	
	@ParameterizedTest(name = "#{index} with [{arguments}]")
	@MethodSource("guru.sfg.brewery.web.controllers.CustomerControllerIT#getStreamAdminCustomer")
    void listCustomersAUTH(String user, String pwd) throws Exception{
    	mockMvc.perform(get("/customers").with(httpBasic(user, pwd)))
            .andExpect(status().isOk());
    }
	@Test
    void listCustomersNOAUTH() throws Exception{
    	mockMvc.perform(get("/customers").with(httpBasic("user", "password")))
            .andExpect(status().isForbidden());
    }
	@Test
    void listCustomersNOTLOGGEDIN() throws Exception{
    	mockMvc.perform(get("/customers"))
            .andExpect(status().isUnauthorized());
    }
}
