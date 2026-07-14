package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<StudentWebCourse, Integer> {

    @Query("SELECT c FROM StudentWebCourse c WHERE c.branchCode = :branchCode ORDER BY c.id DESC")
    List<StudentWebCourse> findAllByBranchCode(String branchCode);
}