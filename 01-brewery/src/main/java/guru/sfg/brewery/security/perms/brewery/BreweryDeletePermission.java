package guru.sfg.brewery.security.perms.brewery;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('brewery.delete')")
public @interface BreweryDeletePermission {

}
