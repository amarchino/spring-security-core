package guru.sfg.brewery.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;

/**
 * Created by jt on 6/12/20.
 */
@SpringBootTest
public class BeerControllerIT extends BaseIT {
	
	@Autowired private BeerRepository beerRepository;
	
	@DisplayName("Init new form")
	@Nested
	class InitNewForm {
		@ParameterizedTest(name = "#{index} with [{arguments}]")
		@MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
	    void initCreationForm(String user, String pwd) throws Exception{
	    	mockMvc.perform(get("/beers/new").with(httpBasic(user, pwd)))
	            .andExpect(status().isOk())
	            .andExpect(view().name("beers/createBeer"))
	            .andExpect(model().attributeExists("beer"));
	    }
		@Test
	    void initCreationFormNoAuth() throws Exception{
	    	mockMvc.perform(get("/beers/new"))
	            .andExpect(status().isUnauthorized());
	    }
	}
	@DisplayName("Init Find Beer Form")
    @Nested
    class FindForm{
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeersFormAUTH(String user, String pwd) throws Exception{
            mockMvc.perform(get("/beers/find")
                    .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/findBeers"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void findBeersWithAnonymous() throws Exception{
            mockMvc.perform(get("/beers/find").with(anonymous()))
                    .andExpect(status().isUnauthorized());
        }
    }
	
	@DisplayName("Process Find Beer Form")
    @Nested
    class ProcessFindForm{
        @Test
        void findBeerForm() throws Exception {
            mockMvc.perform(get("/beers").param("beerName", ""))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerFormAuth(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers").param("beerName", "")
                    .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }
    
	@DisplayName("Get Beer By Id")
    @Nested
    class GetByID {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void getBeerByIdAUTH(String user, String pwd) throws Exception{
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/beers/" + beer.getId())
                    .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/beerDetails"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void getBeerByIdNoAuth() throws Exception{
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/beers/" + beer.getId()))
                    .andExpect(status().isUnauthorized());
        }
    }
}
