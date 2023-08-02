package pl.marcinm312.springbootimageuploader.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.marcinm312.springbootimageuploader.user.model.ActivationTokenEntity;

import java.util.Optional;

@Repository
public interface TokenRepo extends JpaRepository<ActivationTokenEntity, Long> {

	Optional<ActivationTokenEntity> findByValue(String value);
}
