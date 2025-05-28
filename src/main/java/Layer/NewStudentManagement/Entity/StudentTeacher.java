package Layer.NewStudentManagement.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class StudentTeacher
{
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String teacherName;
    @Column(unique = true)
    private String teacherEmail;
    private String password;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @JsonIgnore
    private String subjects;

    @JsonProperty("subjects")
    public List<String> getSubjectsAsList() {
        if (subjects == null || subjects.isBlank()) return List.of();
        return List.of(subjects.split("\\s*,\\s*"));
    }

    public void setSubjectsFromNames(List<String> subjectNames) {
        this.subjects = subjectNames != null ? String.join(",", subjectNames) : null;
    }
}
