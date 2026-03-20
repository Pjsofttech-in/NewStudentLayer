package Layer.NewStudentManagement.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentSubject
{
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String subject;
    private String institutionType;
    private String graduationTypeName;
    private String streamName;
    private String degreeName;
    private String departmentName;
    @Email
    private String createdByEmail;

    private String role;
    private String branchCode;

    @ManyToMany(mappedBy = "subjects")
    private List<StudentTeacher> teachers;

    @ManyToOne
    @JoinColumn(name = "graduation_type_id", nullable = true)
    private StudentGraduationType graduationType;

    @ManyToOne
    @JoinColumn(name = "stream_id", nullable = true)
    private StudentStream stream;

    @ManyToOne
    @JoinColumn(name = "degree_id",nullable = true)
    private StudentDegreeName degree;



}
