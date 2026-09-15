package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<StudentSubject, Long> {
    @Query("SELECT s FROM StudentSubject s " +
            "WHERE (:branchCode IS NULL OR s.branchCode = :branchCode) ")
    List<StudentSubject> findSubjectsByFilters(@Param("branchCode") String branchCode);

    @Query("SELECT s FROM StudentSubject s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentSubject> findAllByBranchCode(@Param("branchCode") String branchCode);


    @Query("SELECT s FROM StudentSubject s JOIN s.teachers t WHERE t.id = :teacherId")
    List<StudentSubject> findSubjectsByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT s FROM StudentSubject s WHERE s.subject = :subjectName " +
            "AND (s.branchCode = :branchCode) ")
    Optional<StudentSubject> findSubjectByName(@Param("subjectName") String subjectName,
                                               @Param("branchCode") String branchCode);
}
