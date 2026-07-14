package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebMapAndImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapAndImagesRepository extends JpaRepository<StudentWebMapAndImages, Long> {

    @Query("SELECT m FROM StudentWebMapAndImages m WHERE m.branchCode = :branchCode ORDER BY m.id DESC")
    List<StudentWebMapAndImages> findAllByBranchCode(String branchCode);

    Optional<StudentWebMapAndImages> findFirstByBranchCode(String branchCode);

    
}