package pl.marcinm312.springbootimageuploader.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.marcinm312.springbootimageuploader.user.model.Token;

import java.util.Optional;

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {

	Optional<Token> findByValue(String value);
}
