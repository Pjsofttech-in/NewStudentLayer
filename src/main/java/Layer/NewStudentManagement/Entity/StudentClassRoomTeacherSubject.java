package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class StudentClassRoomTeacherSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private StudentClassRoom classRoom;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private StudentTeacher teacher;

    @ManyToMany
    @JoinTable(
            name = "student_classroom_teacher_subject",
            joinColumns = @JoinColumn(name = "assignment_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private List<StudentSubject> subjects;
}
