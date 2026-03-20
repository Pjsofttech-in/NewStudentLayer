//package Layer.NewStudentManagement.Entity;
//
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.Email;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.List;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//public class StudentDepartment
//{
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String departmentName;
//    @Email
//    private String createdByEmail;
//
//    private String role;
//    private String branchCode;
//
//    @ManyToOne
//    @JoinColumn(name = "degree_name_id")
//    private StudentDegreeName degreeName;
//
//
//}
