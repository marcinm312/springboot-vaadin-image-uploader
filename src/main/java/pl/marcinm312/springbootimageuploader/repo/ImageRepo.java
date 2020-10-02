package pl.marcinm312.springbootimageuploader.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.marcinm312.springbootimageuploader.model.Image;

@Repository
public interface ImageRepo extends JpaRepository<Image, Long> {

}
