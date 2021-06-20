package guru.sfg.brewery.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BreweryControllerIT extends BaseIT {
	
	@Test
	void listBreweriesCUSTOMER() throws Exception {
		mockMvc.perform(
			get("/brewery/breweries")
			.with(httpBasic("scott", "tiger"))
		)
		.andExpect(status().isOk());
	}

	@Test
	void listBreweriesUNAUTHENTICATED() throws Exception {
		mockMvc.perform(get("/brewery/breweries"))
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	void listBreweriesADMIN() throws Exception {
		mockMvc.perform(
			get("/brewery/breweries")
			.with(httpBasic("user", "password"))
		)
		.andExpect(status().isForbidden());
	}
	
	@Test
	void listBreweriesUSER() throws Exception {
		mockMvc.perform(
			get("/brewery/breweries")
			.with(httpBasic("spring", "guru"))
		)
		.andExpect(status().isForbidden());
	}
	
	@Test
	void getBreweriesJsonCUSTOMER() throws Exception {
		mockMvc.perform(
			get("/brewery/api/v1/breweries")
			.with(httpBasic("scott", "tiger"))
		)
		.andExpect(status().isOk());
	}
	@Test
	void getBreweriesJsonUNAUTHENTICATED() throws Exception {
		mockMvc.perform(
			get("/brewery/api/v1/breweries")
		)
		.andExpect(status().isUnauthorized());
	}
	@Test
	void getBreweriesJsonADMIN() throws Exception {
		mockMvc.perform(
			get("/brewery/api/v1/breweries")
			.with(httpBasic("spring", "guru"))
		)
		.andExpect(status().isForbidden());
	}
	@Test
	void getBreweriesJsonUSER() throws Exception {
		mockMvc.perform(
			get("/brewery/api/v1/breweries")
			.with(httpBasic("user", "password"))
		)
		.andExpect(status().isForbidden());
	}
}
