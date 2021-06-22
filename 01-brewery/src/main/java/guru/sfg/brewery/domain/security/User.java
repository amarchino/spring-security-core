package guru.sfg.brewery.domain.security;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import guru.sfg.brewery.domain.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User implements UserDetails, CredentialsContainer {
	
	private static final long serialVersionUID = 1528485524463947681L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String username;
	private String password;
	
	@Singular
	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.EAGER)
	@JoinTable(name = "user_role",
		joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"),
		inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"))
	private Set<Role> roles;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Customer customer;

	@Builder.Default
	private Boolean accountNonExpired = Boolean.TRUE;
	@Builder.Default
	private Boolean accountNonLocked = Boolean.TRUE;
	@Builder.Default
	private Boolean credentialsNonExpired = Boolean.TRUE;
	@Builder.Default
	private Boolean enabled = Boolean.TRUE;
	
	public Set<GrantedAuthority> getAuthorities() {
		return this.roles
			.stream()
			.map(Role::getAuthorities)
			.flatMap(Set::stream)
			.map(a -> new SimpleGrantedAuthority(a.getPermission()))
			.collect(Collectors.toSet());
	}

	@Override
	public void eraseCredentials() {
		password = null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return Boolean.TRUE.equals(accountNonExpired);
	}

	@Override
	public boolean isAccountNonLocked() {
		return Boolean.TRUE.equals(accountNonLocked);
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return Boolean.TRUE.equals(credentialsNonExpired);
	}

	@Override
	public boolean isEnabled() {
		return Boolean.TRUE.equals(enabled);
	}

}
