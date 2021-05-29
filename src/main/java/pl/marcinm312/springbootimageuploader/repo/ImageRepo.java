package pl.marcinm312.springbootimageuploader.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.Image;

import java.util.List;

@Repository
public interface ImageRepo extends JpaRepository<Image, Long> {

	@Query("SELECT i FROM Image i LEFT JOIN fetch i.appUser ORDER BY i.id DESC")
	List<Image> findAllByOrderByIdDesc();

	@Modifying
	@Query("UPDATE Image i SET i.appUser = null WHERE i.appUser = :user")
	void deleteUserFromImages(@Param("user") AppUser appUser);
}
