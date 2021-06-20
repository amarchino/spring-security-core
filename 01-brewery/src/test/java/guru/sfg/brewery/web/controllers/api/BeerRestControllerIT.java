package guru.sfg.brewery.web.controllers.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {
	
	@Autowired private BeerRepository beerRepository;
	
	@Test
	void findBeers() throws Exception {
		mockMvc.perform(get("/api/v1/beer"))
			.andExpect(status().isOk());
	}

	@Test
	void findBeerByUpc() throws Exception {
		mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
			.andExpect(status().isOk());
	}
	
	
	
	@Test
	void findBeerFormADMIN() throws Exception {
		mockMvc.perform(
			get("/beers")
			.param("beerName", "")
			.with(httpBasic("spring", "guru"))
		)
		.andExpect(status().isOk());
	}
	
	@DisplayName("Delete Tests")	
	@Nested
	class DeleteTests {
		Random random = new Random();
		private Beer getBeerToDelete() {
			return beerRepository.saveAndFlush(
				Beer.builder()
				.beerName("Delete me beer")
				.beerStyle(BeerStyleEnum.IPA)
				.minOnHand(12)
				.quantityToBrew(200)
				.upc(Integer.toString(random.nextInt(Integer.MAX_VALUE)))
				.build());
		}
		
		@Test
		void findBeerById() throws Exception {
			mockMvc.perform(get("/api/v1/beer/" + getBeerToDelete().getId()))
				.andExpect(status().isOk());
		}
		
		@Test
		void deleteBeer() throws Exception {
			mockMvc.perform(
				delete("/api/v1/beer/" + getBeerToDelete().getId())
				.header("Api-Key", "spring")
				.header("Api-Secret", "guru")
			)
			.andExpect(status().isOk());
		}
		@Test
		void deleteBeerBadCredentials() throws Exception {
			mockMvc.perform(
				delete("/api/v1/beer/" + getBeerToDelete().getId())
				.header("Api-Key", "spring")
				.header("Api-Secret", "guruXXXX")
			)
			.andExpect(status().isUnauthorized());
		}
		@Test
		void deleteBeerHttpBasic() throws Exception {
			mockMvc.perform(
				delete("/api/v1/beer/" + getBeerToDelete().getId())
				.with(httpBasic("spring", "guru"))
			)
			.andExpect(status().is2xxSuccessful());
		}
		@Test
		void deleteBeerHttpBasicUserRole() throws Exception {
			mockMvc.perform(
				delete("/api/v1/beer/" + getBeerToDelete().getId())
				.with(httpBasic("user", "password"))
			)
			.andExpect(status().isForbidden());
		}
		@Test
		void deleteBeerHttpBasicCustomerRole() throws Exception {
			mockMvc.perform(
				delete("/api/v1/beer/" + getBeerToDelete().getId())
				.with(httpBasic("scott", "tiger"))
			)
			.andExpect(status().isForbidden());
		}
		@Test
		void deleteBeerNoAuth() throws Exception {
			mockMvc.perform(
				delete("/api/v1/beer/" + getBeerToDelete().getId())
			)
			.andExpect(status().isUnauthorized());
		}
		@Test
		void deleteBeerUrlParams() throws Exception {
			mockMvc.perform(
				delete("/api/v1/beer/" + getBeerToDelete().getId())
				.param("Api-Key", "spring")
				.param("Api-Secret", "guru")
			)
			.andExpect(status().isOk());
		}
		@Test
		void deleteBeerUrlParamsBadCredentials() throws Exception {
			mockMvc.perform(
					delete("/api/v1/beer/" + getBeerToDelete().getId())
					.param("Api-Key", "spring")
					.param("Api-Secret", "guruXXXX")
			)
			.andExpect(status().isUnauthorized());
		}
	}
}
