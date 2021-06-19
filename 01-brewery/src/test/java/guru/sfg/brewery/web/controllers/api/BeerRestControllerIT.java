package guru.sfg.brewery.web.controllers.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import guru.sfg.brewery.web.controllers.BaseIT;

@WebMvcTest
public class BeerRestControllerIT extends BaseIT {
	
	@Test
	void findBeers() throws Exception {
		mockMvc.perform(get("/api/v1/beer"))
			.andExpect(status().isOk());
	}

	@Test
	void findBeerById() throws Exception {
		mockMvc.perform(get("/api/v1/beer/" + UUID.randomUUID()))
			.andExpect(status().isOk());
	}
	
	@Test
	void findBeerByUpc() throws Exception {
		mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
			.andExpect(status().isOk());
	}
	
	@Test
	void deleteBeer() throws Exception {
		mockMvc.perform(
			delete("/api/v1/beer/" + UUID.randomUUID())
			.header("Api-Key", "spring")
			.header("Api-Secret", "guru")
		)
		.andExpect(status().isOk());
	}
	@Test
	void deleteBeerBadCredentials() throws Exception {
		mockMvc.perform(
			delete("/api/v1/beer/" + UUID.randomUUID())
			.header("Api-Key", "spring")
			.header("Api-Secret", "guruXXXX")
		)
		.andExpect(status().isUnauthorized());
	}
	@Test
	void deleteBeerHttpBasic() throws Exception {
		mockMvc.perform(
			delete("/api/v1/beer/" + UUID.randomUUID())
			.with(httpBasic("spring", "guru"))
		)
		.andExpect(status().is2xxSuccessful());
	}
	@Test
	void deleteBeerNoAuth() throws Exception {
		mockMvc.perform(
			delete("/api/v1/beer/" + UUID.randomUUID())
		)
		.andExpect(status().isUnauthorized());
	}
}
