package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class DataForTcDTO {
    private String fullName;
    private String motherName;
    private String fathersName;
    private String gender;
    private String bloodGroup;
    private String email;
    private LocalDate dateOfBirth;
    private String contact;
    private String institutionType;
    private Long standardId;
    private String standardName;
    private Long mediumId;
    private String mediumName;
    private String streamName;
    private Long streamId;
    private String groupName;
    private Long graduationTypeId;
    private String graduationType;
    private Long degreeNameId;
    private String degreeName;

    private Long certificationId;
    private String certification;

    private String departmentName;
    private String academicYear;
    private String registrationNumber;
    private Integer rollNo;
    private String permanentAddress;
    private boolean duplicateTc;
    private boolean isTcGenrated;

    public DataForTcDTO(String fullName, String motherName, String fathersName, String gender, String bloodGroup,
                        String email, LocalDate dateOfBirth, String contact, String institutionType,
                        Long standardId, String standardName, Long mediumId, String mediumName,
                        String streamName, Long streamId, String groupName, Long graduationTypeId,
                        String graduationType, Long degreeNameId, String degreeName, Long departmentId,
                        String departmentName, String academicYear, String registrationNumber,
                        Integer rollNo, String permanentAddress,boolean duplicateTc,boolean isTcGenrated) {
        this.fullName = fullName;
        this.motherName = motherName;
        this.fathersName = fathersName;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.contact = contact;
        this.institutionType = institutionType;
        this.standardId = standardId;
        this.standardName = standardName;
        this.mediumId = mediumId;
        this.mediumName = mediumName;
        this.streamName = streamName;
        this.streamId = streamId;
        this.groupName = groupName;
        this.graduationTypeId = graduationTypeId;
        this.graduationType = graduationType;
        this.degreeNameId = degreeNameId;
        this.degreeName = degreeName;
        this.departmentName = departmentName;
        this.academicYear = academicYear;
        this.registrationNumber = registrationNumber;
        this.rollNo = rollNo;
        this.permanentAddress = permanentAddress;
        this.duplicateTc = duplicateTc;
        this.isTcGenrated = isTcGenrated;
    }

}
