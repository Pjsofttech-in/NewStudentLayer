package Layer.NewStudentManagement.DTO;

import java.time.LocalDate;

public interface UpcomingBirthdayProjection
{
    Long getId();
    String getFullName();
    String getGender();
    String getRollNo();
    String getStreamName();
    String getMediumName();
    String getGroupName();
    String getSemister();
    String getInstitutionType();
    Long getClassId();
    String getDivision();
    String getStandard();
    String getGraduationType();

    // SQL returns date as string → Spring converts to LocalDate
    LocalDate getDateOfBirth();
}
