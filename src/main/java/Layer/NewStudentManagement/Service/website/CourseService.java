package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebCourse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {
    StudentWebCourse createCourse(StudentWebCourse webCourse, String role, String email, MultipartFile courseImage, String url);
    List<StudentWebCourse> getAllCoursesByBranchCode(String role, String email, String branchCode, String url);
    StudentWebCourse updateCourse(int id, StudentWebCourse webCourse, String role, String email, MultipartFile courseImage, String url);
    void deleteCourse(int id, String role, String email, String url);
    StudentWebCourse getCourseById(int id, String role, String email, String url);
}
