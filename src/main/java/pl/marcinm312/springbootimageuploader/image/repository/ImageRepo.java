package pl.marcinm312.springbootimageuploader.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.marcinm312.springbootimageuploader.image.model.ImageEntity;
import pl.marcinm312.springbootimageuploader.user.model.AppUser;

import java.util.List;

@Repository
public interface ImageRepo extends JpaRepository<ImageEntity, Long> {

	@Query("SELECT i FROM ImageEntity i LEFT JOIN fetch i.appUser ORDER BY i.id DESC")
	List<ImageEntity> findAllByOrderByIdDesc();

	@Modifying
	@Query("UPDATE ImageEntity i SET i.appUser = null WHERE i.appUser = :user")
	void deleteUserFromImages(@Param("user") AppUser appUser);
}
