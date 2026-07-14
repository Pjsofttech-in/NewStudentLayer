package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebSlideBar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SlideBarRepository extends JpaRepository<StudentWebSlideBar, Long> {

    @Query("SELECT s FROM StudentWebSlideBar s WHERE s.branchCode = :branchCode ORDER BY s.id DESC")
    List<StudentWebSlideBar> findAllByBranchCode(String branchCode);

    Optional<StudentWebSlideBar> findFirstByBranchCode(String branchCode);

}