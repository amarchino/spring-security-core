package guru.sfg.brewery.security.perms.customer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('customer.update')")
public @interface CustomerUpdatePermission {

}
