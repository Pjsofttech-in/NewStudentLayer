package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentCourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseTypeRepository extends JpaRepository<StudentCourseType, Long> {
    @Query("SELECT s FROM StudentCourseType s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentCourseType> findAllByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT s.id FROM StudentCourseType s WHERE s.courseType =:courseType AND s.branchCode =:branchCode ORDER BY s.id DESC")
    List<Long> findAllIdsByName(@Param("courseType") String courseType, String branchCode);

    @Query("SELECT s FROM StudentCourseType s WHERE s.graduationType.id = :graduationTypeId ORDER BY s.id DESC")
    List<StudentCourseType> findAllByGraduationType(@Param("graduationTypeId") Long graduationTypeId);
}
