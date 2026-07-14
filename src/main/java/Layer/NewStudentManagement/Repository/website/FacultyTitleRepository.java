package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebFacultyTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacultyTitleRepository extends JpaRepository<StudentWebFacultyTitle, Long> {
    @Query("SELECT f FROM StudentWebFacultyTitle f WHERE f.branchCode = :branchCode ORDER BY f.id DESC")
    List<StudentWebFacultyTitle> findAllByBranchCode(String branchCode);
}
