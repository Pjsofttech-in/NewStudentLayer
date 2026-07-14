package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebFaculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacultyRepository extends JpaRepository<StudentWebFaculty, Long> {

    @Query("SELECT f FROM StudentWebFaculty f WHERE f.branchCode = :branchCode ORDER BY f.id DESC")
    List<StudentWebFaculty> findAllByBranchCode(String branchCode);
}