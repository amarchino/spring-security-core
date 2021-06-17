package guru.sfg.brewery.web.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import guru.sfg.brewery.domain.Beer;

/**
 * Created by jt on 6/12/20.
 */
@WebMvcTest
public class BeerControllerIT extends BaseIT {

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
    
    @Test
    void findBeersWithHttpBasic() throws Exception{
    	when(beerRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(Beer.builder().build()));
        mockMvc.perform(
    			get("/beers/" + UUID.randomUUID())
    			.with(httpBasic("spring", "guru"))
    		)
            .andExpect(status().isOk())
            .andExpect(view().name("beers/beerDetails"))
            .andExpect(model().attributeExists("beer"));
    }

}
