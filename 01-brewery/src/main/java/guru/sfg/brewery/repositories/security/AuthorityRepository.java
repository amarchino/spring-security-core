package guru.sfg.brewery.repositories.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import guru.sfg.brewery.domain.security.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
	Optional<Authority> findByRole(String role);
}
