package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebVisionMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisionMissionRepository extends JpaRepository<StudentWebVisionMission, Long> {

    @Query("SELECT v FROM StudentWebVisionMission v WHERE v.branchCode = :branchCode ORDER BY v.id DESC")
    List<StudentWebVisionMission> findAllByBranchCode(String branchCode);

    Optional<StudentWebVisionMission> findFirstByBranchCode(String branchCode);

}