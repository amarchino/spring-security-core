package guru.sfg.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;

import guru.sfg.brewery.domain.security.LoginFailure;

public interface LoginFailureRepository extends JpaRepository<LoginFailure, Integer> {
}
