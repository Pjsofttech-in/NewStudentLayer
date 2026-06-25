package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentPromotionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentPromotionRepository extends JpaRepository<StudentPromotionRecord,Long>
{

    @Query("SELECT r FROM StudentPromotionRecord r WHERE r.student.id = :studentId AND r.isCurrent = true")
    Optional<StudentPromotionRecord> findCurrentByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT r FROM StudentPromotionRecord r WHERE r.student.id = :studentId ORDER BY r.promotionDate, r.id DESC")
    List<StudentPromotionRecord> findAllByStudentIdOrderByPromotionDate(@Param("studentId") Long studentId);

    @Query("""
            SELECT r FROM StudentPromotionRecord r 
            WHERE r.student.id = :studentId 
            AND (:standardId IS NULL OR r.standard.sid = :standardId)
            AND (:departmentName IS NULL OR r.departmentName = :departmentName)
            AND r.isCurrent = true
            """)
    Optional<StudentPromotionRecord> findCurrentByStudentIdStandardId(@Param("studentId") Long studentId,
                                                                      @Param("standardId") Long standardId,
                                                                      @Param("departmentName") String departmentName);


//    List<StudentPromotionRecord> findAllByStudentIdOrderByPromotionDateDesc(Long studentId);

}
