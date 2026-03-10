package Layer.NewStudentManagement.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentFees
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fid;
    private String studentName;
    private Integer rollNo;
    private String standardName;
    private String mediumName;
    private String streamName;//  For Jr. College students
    private String groupName;
    private String degreeName;         //  For UG/PG students
    private String departmentName;     //  For UG/PG students
//    private String feesType;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate approvalDate;

    private String feesStatus;
    private String feesCollectionType;

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
    private double discount;
    private double discountedAmount;
    private double GST;
    private Double feesAmount;      // fees total before discount
    private Double totalamount;         // fees total after discount
//    private double lateFeeCharges;
    private Long sfid;
    private double paidAmount;         // total paid by student
    private double pendingAmount;     // remaining amount
//    private String feesPaymentType;   // OneTime, Monthly, Installment

    private String institutionType;
    @Email
    private String createdByEmail;

    private String role;
    private String branchCode;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "standard_id", nullable = true)
    private StudentStandard standard;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "medium_id", nullable = true)
    private StudentMedium medium;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "stream_id", nullable = true)
    private StudentStream stream;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "group_id", nullable = true)
    private StudentGroup group;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "degree_id", nullable = true)
    private StudentDegreeName degree;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "department_id", nullable = true)
    private StudentDepartment department;

    @OneToMany(mappedBy = "studentFees", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentFeeSchedule> scheduleList = new ArrayList<>();


}
