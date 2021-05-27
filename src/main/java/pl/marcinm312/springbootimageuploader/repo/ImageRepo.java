package pl.marcinm312.springbootimageuploader.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.marcinm312.springbootimageuploader.model.Image;

import java.util.List;

@Repository
public interface ImageRepo extends JpaRepository<Image, Long> {

	@Query("SELECT i FROM Image i LEFT JOIN fetch i.appUser ORDER BY i.id DESC")
	List<Image> findAllByOrderByIdDesc();
}
