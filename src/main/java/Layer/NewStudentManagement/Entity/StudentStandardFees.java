package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentStandardFees
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sfid;
    private String standardName;
    private String mediumName;
    private double tuitionFee;
    private double admissionFee;
    private double practicalFee;
    private double computerClassFee;
    private double examFees;
    private double uniformFee;
    private double transportBusFee;
    private double hostelFee;
    private double buildingFundFee;
    private double libraryFees;
    private double sportFees;
    private double GST;
    private Double feesAmount;
    private String institutionType;
    private String streamName;
    private String graduationTypeName;
    private String degreeName;
    private String departmentName;


    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;


    @ManyToOne
    @JoinColumn(name = "standard_id")
    private StudentStandard standard;

    @ManyToOne
    @JoinColumn(name = "medium_id")
    private StudentMedium medium;

    @ManyToOne
    @JoinColumn(name = "stream_id")
    private StudentStream stream;

    @ManyToOne
    @JoinColumn(name = "graduation_type_id")
    private StudentGraduationType graduationType;

    @ManyToOne
    @JoinColumn(name = "degree_id")
    private StudentDegreeName degree;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private StudentDepartment department;


}
