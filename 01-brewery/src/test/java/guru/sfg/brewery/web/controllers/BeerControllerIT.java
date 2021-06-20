package guru.sfg.brewery.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;

/**
 * Created by jt on 6/12/20.
 */
@SpringBootTest
public class BeerControllerIT extends BaseIT {
	
	@Autowired private BeerRepository beerRepository;
	
	@Test
    void initCreationForm() throws Exception{
    	mockMvc.perform(get("/beers/new").with(httpBasic("user", "password")))
            .andExpect(status().isOk())
            .andExpect(view().name("beers/createBeer"))
            .andExpect(model().attributeExists("beer"));
    }
	
	@Test
    void initCreationFormScottTiger() throws Exception{
    	mockMvc.perform(get("/beers/new").with(httpBasic("scott", "tiger")))
            .andExpect(status().isOk())
            .andExpect(view().name("beers/createBeer"))
            .andExpect(model().attributeExists("beer"));
    }

    @Test
    void findBeers() throws Exception{
    	mockMvc.perform(get("/beers/find"))
            .andExpect(status().isOk())
            .andExpect(view().name("beers/findBeers"))
            .andExpect(model().attributeExists("beer"));
    }
    
    @Test
    void findBeersWithAnonymous() throws Exception{
        mockMvc.perform(
    			get("/beers/find")
    			.with(anonymous())
    		)
            .andExpect(status().isOk())
            .andExpect(view().name("beers/findBeers"))
            .andExpect(model().attributeExists("beer"));
    }
    
    @DisplayName("Find Tests")	
	@Nested
	class FindTests {
		Random random = new Random();
		private Beer getBeerToFind() {
			return beerRepository.saveAndFlush(
				Beer.builder()
				.beerName("Find me beer")
				.beerStyle(BeerStyleEnum.IPA)
				.minOnHand(12)
				.quantityToBrew(200)
				.upc(Integer.toString(random.nextInt(Integer.MAX_VALUE)))
				.build());
		}
		@Test
		void findBeersWithHttpBasic() throws Exception{
			mockMvc.perform(
					get("/beers/" + getBeerToFind().getId())
					.with(httpBasic("spring", "guru"))
					)
			.andExpect(status().isOk())
			.andExpect(view().name("beers/beerDetails"))
			.andExpect(model().attributeExists("beer"));
		}
    }
    

}
