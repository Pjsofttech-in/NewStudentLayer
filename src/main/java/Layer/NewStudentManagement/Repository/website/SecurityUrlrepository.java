package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecurityUrlrepository extends JpaRepository<StudentWebSecurityUrl,Long> {
    boolean existsByBranchCode(String branchCode);

    @Query("SELECT s FROM StudentWebSecurityUrl s WHERE LOWER(TRIM(s.url)) = LOWER(TRIM(:url))")
    Optional<StudentWebSecurityUrl> findByUrl(@Param("url") String url);

    @Query("""
       SELECT s FROM StudentWebSecurityUrl s
       WHERE LOWER(TRIM(s.url)) = LOWER(TRIM(:url))
       AND s.branchCode = :branchCode
       """)
    Optional<StudentWebSecurityUrl> findByUrlAndBranchCode(
            @Param("url") String url,
            @Param("branchCode") String branchCode
    );

    @Query("SELECT s FROM StudentWebSecurityUrl s WHERE s.branchCode = :branchCode ORDER BY s.id DESC")
    List<StudentWebSecurityUrl> findAllByBranchCode(String branchCode);


}
