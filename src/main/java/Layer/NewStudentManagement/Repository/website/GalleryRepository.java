package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GalleryRepository extends JpaRepository<StudentWebGallery, Long> {
    @Query("SELECT g FROM StudentWebGallery g WHERE g.branchCode = :branchCode ORDER BY g.galleryId DESC")
    List<StudentWebGallery> findAllByBranchCode(String branchCode);

    Optional<StudentWebGallery> findFirstByBranchCode(String branchCode);
}