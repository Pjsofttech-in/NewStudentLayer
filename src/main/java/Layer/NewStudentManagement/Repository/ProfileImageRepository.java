package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileImageRepository extends JpaRepository<StudentProfileImage, Long>
{

    List<StudentProfileImage> findByProfileId(Long profileId);
}
