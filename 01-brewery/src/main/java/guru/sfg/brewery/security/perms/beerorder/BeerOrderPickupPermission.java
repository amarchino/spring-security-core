package guru.sfg.brewery.security.perms.beerorder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.pickup') OR ( hasAuthority('customer.order.pickup') AND @beerOrderAuthenticationManager.customerIdMatches(authentication, #customerId))")
public @interface BeerOrderPickupPermission {

}
