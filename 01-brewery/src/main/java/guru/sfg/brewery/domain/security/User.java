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
public class User {
	
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

	@Builder.Default
	private Boolean accountNonExpired = Boolean.TRUE;
	@Builder.Default
	private Boolean accountNonLocked = Boolean.TRUE;
	@Builder.Default
	private Boolean credentialsNonExpired = Boolean.TRUE;
	@Builder.Default
	private Boolean enabled = Boolean.TRUE;
	
	public Set<Authority> getAuthorities() {
		return this.roles
			.stream()
			.map(Role::getAuthorities)
			.flatMap(Set::stream)
			.collect(Collectors.toSet());
	}

}
