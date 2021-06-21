package guru.sfg.brewery.security.perms.beer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('beer.create')")
public @interface BeerCreatePermission {

}
