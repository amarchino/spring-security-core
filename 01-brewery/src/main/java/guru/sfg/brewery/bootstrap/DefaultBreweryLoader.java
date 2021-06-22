/*
 *  Copyright 2020 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package guru.sfg.brewery.bootstrap;

import java.util.Set;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerInventory;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.BeerOrderLine;
import guru.sfg.brewery.domain.Brewery;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.domain.OrderStatusEnum;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.BreweryRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by jt on 2019-01-26.
 */
@RequiredArgsConstructor
@Component
@Order(2)
@Slf4j
public class DefaultBreweryLoader implements CommandLineRunner {

    public static final String TASTING_ROOM = "Tasting Room";

    public static final String ST_PETE_DISTRIBUTING = "St Pete Distributing";
    public static final String DUNEDIN_DISTRIBUTING = "Dunedin Distributing";
    public static final String KEY_WEST_DISTRIBUTING = "Key West Distributing";
    
    public static final String STPETE_USER = "stpete";
    public static final String DUNEDIN_USER = "dunedin";
    public static final String KEYWEST_USER = "keywest";
    
    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";
    
    private final PasswordEncoder passwordEncoder;

    private final BreweryRepository breweryRepository;
    private final BeerRepository beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        loadBreweryData();
        loadTastingRoomData();
        loadCustomers();
    }

    private void loadTastingRoomData() {
        Customer tastingRoom = Customer.builder()
                .customerName(TASTING_ROOM)
                .apiKey(UUID.randomUUID())
                .build();

        customerRepository.save(tastingRoom);

        beerRepository.findAll().forEach(beer -> {
            beerOrderRepository.save(BeerOrder.builder()
                    .customer(tastingRoom)
                    .orderStatus(OrderStatusEnum.NEW)
                    .beerOrderLines(Set.of(BeerOrderLine.builder()
                            .beer(beer)
                            .orderQuantity(2)
                            .build()))
                    .build());
        });
    }

    private void loadBreweryData() {
        if (breweryRepository.count() == 0) {
            breweryRepository.save(Brewery
                    .builder()
                    .breweryName("Cage Brewing")
                    .build());

            Beer mangoBobs = Beer.builder()
                    .beerName("Mango Bobs")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_1_UPC)
                    .build();

            beerRepository.save(mangoBobs);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(mangoBobs)
                    .quantityOnHand(500)
                    .build());

            Beer galaxyCat = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_2_UPC)
                    .build();

            beerRepository.save(galaxyCat);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(galaxyCat)
                    .quantityOnHand(500)
                    .build());

            Beer pinball = Beer.builder()
                    .beerName("Pinball Porter")
                    .beerStyle(BeerStyleEnum.PORTER)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_3_UPC)
                    .build();

            beerRepository.save(pinball);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(pinball)
                    .quantityOnHand(500)
                    .build());

        }
    }
    
    private void loadCustomers() {
    	Role customerRole = roleRepository.findByName("CUSTOMER").orElseThrow();
    	
    	// Create customers
    	Customer stPeteCustomer = createCustomer(ST_PETE_DISTRIBUTING);
    	Customer dunedinCustomer = createCustomer(DUNEDIN_DISTRIBUTING);
    	Customer keyWestCustomer = createCustomer(KEY_WEST_DISTRIBUTING);
    	
    	// Create user
    	User stPeteUser = createUser(STPETE_USER, "password", stPeteCustomer, customerRole);
    	User dunedinUser = createUser(DUNEDIN_USER, "password", dunedinCustomer, customerRole);
    	User keyWestUser = createUser(KEYWEST_USER, "password", keyWestCustomer, customerRole);
    	
    	// Create order
    	createOrder(stPeteCustomer);
    	createOrder(dunedinCustomer);
    	createOrder(keyWestCustomer);
    	
    	log.debug("Orders loaded: " + beerOrderRepository.count());
    }
    
    private Customer createCustomer(String name) {
    	return customerRepository.save(Customer.builder()
    			.customerName(name)
    			.apiKey(UUID.randomUUID())
    			.build());
    }
    private User createUser(String name, String password, Customer customer, Role role) {
    	return userRepository.save(User.builder()
    			.username(name)
    			.password(passwordEncoder.encode(password))
    			.customer(customer)
    			.role(role)
    			.build());
    }
    private BeerOrder createOrder(Customer customer) {
    	return beerOrderRepository.save(BeerOrder.builder()
    			.customer(customer)
    			.orderStatus(OrderStatusEnum.NEW)
    			.beerOrderLines(Set.of(
    					BeerOrderLine.builder()
    					.beer(beerRepository.findByUpc(BEER_1_UPC))
    					.orderQuantity(2)
    					.build()
    			))
    			.build());
    }
}
