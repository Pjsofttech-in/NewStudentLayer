package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentDepartment;
import Layer.NewStudentManagement.Entity.StudentGraduationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GraduationTypeRepository extends JpaRepository<StudentGraduationType,Long>
{

    @Query("SELECT s FROM StudentGraduationType s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentGraduationType> findAllByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT g FROM StudentGraduationType g WHERE g.stream.stream = :streamName AND g.branchCode=:branchCode")
    List<StudentGraduationType> findByStreamName(@Param("streamName") String streamName, @Param("branchCode") String branchCode);

    @Query("SELECT g.id FROM StudentGraduationType g " +
            "WHERE TRIM(LOWER(g.graduationType)) = TRIM(LOWER(:graduationType)) " +
            "AND g.stream.id = :streamId " +
            "AND g.branchCode = :branchCode")
    List<Long> findIdsByNameAndStreamAndBranchCode(@Param("graduationType") String graduationType,
                                                   @Param("streamId") Long streamId,
                                                   @Param("branchCode") String branchCode);


}
