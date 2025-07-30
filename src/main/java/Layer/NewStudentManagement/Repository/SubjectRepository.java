package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<StudentSubject,Long>
{
        @Query("SELECT s FROM StudentSubject s " +
                "WHERE (:branchCode IS NULL OR s.branchCode = :branchCode) " +
                "AND (:institutionType IS NULL OR LOWER(s.institutionType) = LOWER(:institutionType)) " +
                "AND (:graduationTypeName IS NULL OR LOWER(s.graduationTypeName) = LOWER(:graduationTypeName)) " +
                "AND (:streamName IS NULL OR LOWER(s.streamName) = LOWER(:streamName)) " +
                "AND (:degreeName IS NULL OR LOWER(s.degreeName) = LOWER(:degreeName)) " +
                "AND (:departmentName IS NULL OR LOWER(s.departmentName) = LOWER(:departmentName))")
        List<StudentSubject> findSubjectsByFilters(@Param("branchCode") String branchCode,
                                                   @Param("institutionType") String institutionType,
                                                   @Param("graduationTypeName") String graduationTypeName,
                                                   @Param("streamName") String streamName,
                                                   @Param("degreeName") String degreeName,
                                                   @Param("departmentName") String departmentName);

    @Query("SELECT s FROM StudentSubject s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentSubject> findAllByBranchCode(@Param("branchCode") String branchCode);

}
