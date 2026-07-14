package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebTestimonials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestimonialsRepository extends JpaRepository<StudentWebTestimonials, Long> {

    @Query("SELECT t FROM StudentWebTestimonials t WHERE t.branchCode = :branchCode ORDER BY t.testimonialId DESC")
    List<StudentWebTestimonials> findAllByBranchCode(String branchCode);
}