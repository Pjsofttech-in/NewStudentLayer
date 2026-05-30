package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentFeeComponentsMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String componentName; // e.g., "Tuition Fee", "Hostel Fee", "Trip", "Gathering"

    @Column(nullable = false)
    private boolean isAcademic = true; // true = Academic, false = Miscellaneous
}
