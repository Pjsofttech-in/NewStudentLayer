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
    private Double totalamount;


    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;


    @ManyToOne
    @JoinColumn(name = "standard_id", nullable = false)
    private StudentStandard standard;

    @ManyToOne
    @JoinColumn(name = "medium_id", nullable = false)
    private StudentMedium medium;

}
