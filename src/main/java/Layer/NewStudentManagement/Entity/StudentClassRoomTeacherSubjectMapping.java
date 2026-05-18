package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Table(name = "student_classroom_teacher_subject")
public class StudentClassRoomTeacherSubjectMapping {
    @EmbeddedId
    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private StudentClassRoomTeacherSubject studentClassRoomTeacherSubject;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private StudentSubject subject;
}
