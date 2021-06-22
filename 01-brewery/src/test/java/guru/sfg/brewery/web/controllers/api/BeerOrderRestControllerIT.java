package guru.sfg.brewery.web.controllers.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto;

@SpringBootTest
class BeerOrderRestControllerIT extends BaseIT {

	public static final String API_ROOT = "/api/v1/customers/";

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BeerOrderRepository beerOrderRepository;

	@Autowired
	BeerRepository beerRepository;

	@Autowired
	ObjectMapper objectMapper;

	Customer stPeteCustomer;
	Customer dunedinCustomer;
	Customer keyWestCustomer;
	List<Beer> loadedBeers;

	@BeforeEach
	protected void setUp() {
		super.setUp();
		stPeteCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.ST_PETE_DISTRIBUTING).orElseThrow();
		dunedinCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.DUNEDIN_DISTRIBUTING).orElseThrow();
		keyWestCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.KEY_WEST_DISTRIBUTING).orElseThrow();
		loadedBeers = beerRepository.findAll();
	}

	@DisplayName("Create Test")
	@Nested
	class CreateOrderTests {

		@Test
		void notAuth() throws Exception {
			BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

			mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(beerOrderDto))
			).andExpect(status().isUnauthorized());
		}

		@WithUserDetails(value = "spring", userDetailsServiceBeanName = "")
		@Test
		void userAdmin() throws Exception {
			BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

			mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(beerOrderDto))
			).andExpect(status().isCreated());
		}

		@WithUserDetails(value = DefaultBreweryLoader.STPETE_USER)
		@Test
		void userAuthCustomer() throws Exception {
			BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

			mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(beerOrderDto))
			).andExpect(status().isCreated());
		}

		@WithUserDetails(value = DefaultBreweryLoader.KEYWEST_USER)
		@Test
		void userNOTAuthCustomer() throws Exception {
			BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

			mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(beerOrderDto))
			).andExpect(status().isForbidden());
		}

	}

	@DisplayName("List Test")
	@Nested
	class ListOrderTests {
		@Test
		void notAuth() throws Exception {
			mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
				.andExpect(status().isUnauthorized());
		}
	
		@WithUserDetails(value = "spring")
		@Test
		void adminAuth() throws Exception {
			mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
				.andExpect(status().isOk());
		}
	
		@WithUserDetails(value = DefaultBreweryLoader.STPETE_USER)
		@Test
		void customerAuth() throws Exception {
			mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
				.andExpect(status().isOk());
		}
	
		@WithUserDetails(value = DefaultBreweryLoader.DUNEDIN_USER)
		@Test
		void customerNOTAuth() throws Exception {
			mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
				.andExpect(status().isForbidden());
		}
	}
	
	@DisplayName("Get Test")
	@Nested
	@Transactional
	class GetOrderTests {
		@Test
		void notAuth() throws Exception {
			BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
			mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
				.andExpect(status().isUnauthorized());
		}
	
		@WithUserDetails(value = "spring")
		@Test
		void adminAuth() throws Exception {
			BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
			mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
				.andExpect(status().isOk());
		}
	
		@WithUserDetails(value = DefaultBreweryLoader.STPETE_USER)
		@Test
		void customerAuth() throws Exception {
			BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
			mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
				.andExpect(status().isOk());
		}
	
		@WithUserDetails(value = DefaultBreweryLoader.DUNEDIN_USER)
		@Test
		void customerNOTAuth() throws Exception {
			BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
			mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
				.andExpect(status().isForbidden());
		}
	}

	@DisplayName("Pickup Test")
	@Nested
	@Transactional
	class PickupOrderTests {
		@Test
		void notAuth() throws Exception {
			BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
			mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
				.andExpect(status().isUnauthorized());
		}
	
		@WithUserDetails(value = "spring")
		@Test
		void adminAuth() throws Exception {
			BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
			mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
				.andExpect(status().isNoContent());
		}
	
		@WithUserDetails(value = DefaultBreweryLoader.STPETE_USER)
		@Test
		void customerAuth() throws Exception {
			BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
			mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
				.andExpect(status().isNoContent());
		}
	
		@WithUserDetails(value = DefaultBreweryLoader.DUNEDIN_USER)
		@Test
		void customerNOTAuth() throws Exception {
			BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
			mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
				.andExpect(status().isForbidden());
		}
	}

	private BeerOrderDto buildOrderDto(Customer customer, UUID beerId) {
		List<BeerOrderLineDto> orderLines = Arrays
				.asList(BeerOrderLineDto.builder().id(UUID.randomUUID()).beerId(beerId).orderQuantity(5).build());

		return BeerOrderDto.builder().customerId(customer.getId()).customerRef("123")
				.orderStatusCallbackUrl("http://example.com").beerOrderLines(orderLines).build();
	}
}
