package guru.sfg.brewery.web.controllers.api;

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
}
