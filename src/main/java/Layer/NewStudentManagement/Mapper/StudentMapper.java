package Layer.NewStudentManagement.Mapper;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class StudentMapper {

    public StudentDTO toStudentDTO(StudentEntity student) {
        if (student == null) return null;

        StudentDTO dto = new StudentDTO();

        dto.setId(student.getId());
        dto.setTitle(student.getTitle());
        dto.setFullName(student.getFullName());
        dto.setGender(student.getGender());
        dto.setBloodGroup(student.getBloodGroup());
        dto.setMotherTongue(student.getMotherTongue());
        dto.setMaritalStatus(student.getMaritalStatus());
        dto.setContact(student.getContact());
        dto.setAge(student.getAge());
        dto.setEmail(student.getEmail());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setBirthPlace(student.getBirthPlace());
        dto.setBirthCountry(student.getBirthCountry());
        dto.setPancardNumber(student.getPancardNumber());
        dto.setAadharNumber(student.getAadharNumber());
        dto.setRollNo(student.getRollNo());
        dto.setStandardName(student.getStandardName());
        dto.setAcademicYear(student.getAcademicYear());
        dto.setUdiseNo(student.getUdiseNo());
        dto.setApaarId(student.getApaarId());
        dto.setMediumName(student.getMediumName());
        dto.setEnrollmentDate(student.getEnrollmentDate());
        dto.setApprovalDate(student.getApprovalDate());
        dto.setStatus(student.getStatus());
        dto.setApplyFor(student.getApplyFor());
        dto.setStreamName(student.getStreamName());
        dto.setGroupName(student.getGroupName());
        dto.setDepartmentName(student.getDepartmentName());
        dto.setSemister(student.getSemister());
        dto.setInstitutionType(student.getInstitutionType());
        dto.setPassword(student.getPassword());
        dto.setRegistrationNumber(student.getRegistrationNumber());
        dto.setFormStatus(student.getFormStatus());
        dto.setReason(student.getReason());
        dto.setFatherEmailId(student.getFatherEmailId());
//            dto.setDiscount(student.getDiscount());
        dto.setTcGenrated(student.isTcGenrated());
        dto.setOldRegisterPhoto(student.getOldRegisterPhoto());
        dto.setApplicationNumber(student.getApplicationNumber());
        dto.setEntranceExamName(student.getEntranceExamName());
        dto.setEntranceExam(student.isEntranceExam());
        dto.setEntranceMarks(student.getEntranceMarks());
        dto.setEMarksOutOff(student.getEMarksOutOff());
        if (student.getClassRoom() != null) {
            dto.setClasssRoomId(student.getClassRoom().getId());
        } else {
            dto.setClasssRoomId(null);
        }
        if (student.getStandard() != null) {
            dto.setStandardId(student.getStandard().getSid());
            dto.setStandardName(student.getStandard().getStandardName());
        }

        if (student.getMedium() != null) {
            dto.setMediumId(student.getMedium().getMid());
            dto.setMediumName(student.getMedium().getMediumName());
        }

        if (student.getStream() != null) {
            dto.setStreamId(student.getStream().getId());
            dto.setStreamName(student.getStream().getStream());
        }
        if (student.getDegreeName() != null) {
            dto.setDegreeNameId(student.getDegreeName().getId());
            dto.setDegreeName(student.getDegreeName().getDegreeName());
        }
        if (student.getGraduationType() != null) {
            dto.setGraduationTypeId(student.getGraduationType().getId());
            dto.setGraduationType(student.getGraduationType().getGraduationType());
        }

        dto.setCreatedByEmail(student.getCreatedByEmail());
        dto.setRole(student.getRole());
        dto.setBranchCode(student.getBranchCode());

        dto.setAddress(toStudentAddressDTO(student.getAddress()));
        dto.setPermanentEducationNumber(student.getPermanentEducationNumber());

        dto.setEducationList(student.getEducationList() != null ? student.getEducationList().stream().map(this::toStudentEducationDTO).collect(Collectors.toList()) : null);

        if (student.getCollegeDetails() != null) {
            dto.setCollegeDetailsDTO(toStudentCollegeDetailsDTO(student.getCollegeDetails()));
        }

        if (student.getAdditionalInfo() != null) {
            dto.setAdditionalInfo(toStudentAdditionalInfoDTO(student.getAdditionalInfo()));
        } else {
            dto.setAdditionalInfo(null);
        }

        if (student.getReligion() != null) {
            dto.setReligion(toStudentReligionDTO(student.getReligion()));
        } else {
            dto.setReligion(null);
        }

        if (student.getSports() != null) {
            dto.setSports(toStudentSportsDTO(student.getSports()));
        } else {
            dto.setSports(null);
        }

        if (student.getDocuments() != null) {
            dto.setDocuments(toStudentDocumentDTO(student.getDocuments()));
        } else {
            dto.setDocuments(null);
        }

        return dto;
    }

    public static StudentCollegeDetailsDTO toStudentCollegeDetailsDTO(StudentCollegeDetails obj) {
        StudentCollegeDetailsDTO dto = new StudentCollegeDetailsDTO();
        if (obj != null) {
            dto.setAbcId(obj.getAbcId());
            dto.setDteNumber(obj.getDteNumber());
            dto.setGeneralRegistrationNumber(obj.getGeneralRegistrationNumber());
            dto.setEnrollmentNumber(obj.getEnrollmentNumber());
            return dto;
        }
        return dto;
    }

    public StudentAddressDTO toStudentAddressDTO(StudentAddress address) {
        if (address == null) return null;

        StudentAddressDTO dto = new StudentAddressDTO();

        dto.setId(address.getId());
        dto.setPermanentAddress(address.getPermanentAddress());
        dto.setPlandmark(address.getPlandmark());
        dto.setPDistrict(address.getPDistrict());
        dto.setPTaluka(address.getPTaluka());
        dto.setPcountry(address.getPcountry());
        dto.setPcity(address.getPcity());
        dto.setPState(address.getPState());
        dto.setState(address.getState());
        dto.setPpincode(address.getPpincode());
        dto.setAddress(address.getAddress());
        dto.setCountry(address.getCountry());
        dto.setDistrict(address.getDistrict());
        dto.setCity(address.getCity());
        dto.setTaluka(address.getTaluka());
        dto.setLandmark(address.getLandmark());
        dto.setPincode(address.getPincode());
        dto.setNationality(address.getNationality());
        dto.setMotherName(address.getMotherName());
        dto.setFatherProfession(address.getFatherProfession());
        dto.setFathersName(address.getFathersName());
        dto.setFathersContact(address.getFathersContact());
        dto.setWhatsappNumber(address.getWhatsappNumber());
        dto.setSameAsCurrent(address.isSameAsCurrent());
        dto.setIncomeRanges(address.getIncomeRanges());

        return dto;
    }

    public StudentEducationDTO toStudentEducationDTO(StudentEducation education) {
        if (education == null) return null;

        StudentEducationDTO dto = new StudentEducationDTO();

        dto.setId(education.getId());
        dto.setClassGrade(education.getClassGrade());
        dto.setLastYear(education.getLastYear());
        dto.setSchoolName(education.getSchoolName());
        dto.setTotalMarks(education.getTotalMarks());
        dto.setObtainedMarks(education.getObtainedMarks());
        dto.setCgpa(education.getCgpa());
        dto.setPercentage(education.getPercentage());
        dto.setGrade(education.getGrade());
        dto.setPreStandard(education.getPreStandard());
        dto.setCollegeName(education.getCollegeName());
        dto.setPassOutyear(education.getPassOutyear());
        dto.setCity(education.getCity());
        dto.setExamMode(education.getExamMode());
        dto.setPreBoard(education.getPreBoard());
        dto.setEnterExamName(education.getEnterExamName());
        dto.setReasonOfLeavingSchool(education.getReasonOfLeavingSchool());
        dto.setEnrollmentNumber(education.getEnrollmentNumber());
        dto.setExamBoard(education.getExamBoard());
        dto.setExamUniversity(education.getExamUniversity());

        return dto;
    }

    public StudentAdditionalInfoDTO toStudentAdditionalInfoDTO(StudentAdditionalInfo additionalInfo) {
        if (additionalInfo == null) return null;

        StudentAdditionalInfoDTO dto = new StudentAdditionalInfoDTO();

        dto.setId(additionalInfo.getId());
        dto.setHandicap(additionalInfo.isHandicap());
        dto.setEarthquake(additionalInfo.isEarthquake());
        dto.setEarthquakeNumber(additionalInfo.getEarthquakeNumber());
        dto.setProjectDifferentiated(additionalInfo.isProjectDifferentiated());
        dto.setProjectDifferentiatedNumber(additionalInfo.getProjectDifferentiatedNumber());
        dto.setEbc(additionalInfo.isEbc());
        dto.setScholarship(additionalInfo.isScholarship());
        dto.setScholarshipName(additionalInfo.getScholarshipName());
        dto.setSpecialPercentage(additionalInfo.getSpecialPercentage());
        dto.setDisabilityType(additionalInfo.getDisabilityType());

        return dto;
    }

    public StudentReligionDTO toStudentReligionDTO(StudentReligion religion) {
        if (religion == null) return null;

        StudentReligionDTO dto = new StudentReligionDTO();

        dto.setId(religion.getId());
        dto.setReligion(religion.getReligion());
        dto.setMinority(religion.isMinority());
        dto.setCastCategory(religion.getCastCategory());
        dto.setMinorityType(religion.getMinorityType());
        dto.setCasteCertificateNumber(religion.getCasteCertificateNumber());
        dto.setCasteValidation(religion.getCasteValidation());
        dto.setCasteValidationNumber(religion.getCasteValidationNumber());
        dto.setSubCaste(religion.getSubCaste());
        dto.setDomicileBool(religion.isDomicileBool());
        dto.setDomicileNumber(religion.getDomicileNumber());

        return dto;
    }

    public StudentSportsDTO toStudentSportsDTO(StudentSports sports) {
        if (sports == null) return null;

        StudentSportsDTO dto = new StudentSportsDTO();

        dto.setId(sports.getId());
        dto.setHeight(sports.getHeight());
        dto.setWeight(sports.getWeight());
        dto.setRole(sports.getRole());
        dto.setSportYesNo(sports.getSportYesNo());
        dto.setSportsName(sports.getSportsName());
        dto.setSportParticipation(sports.getSportParticipation());
        dto.setNoOfYearsPlayed(sports.getNoOfYearsPlayed());
        dto.setLevelOfParticipation(sports.getLevelOfParticipation());
        dto.setSportsInjuries(sports.getSportsInjuries());
        dto.setAchievement(sports.getAchievement());
        dto.setInternationaldetail(sports.getInternationaldetail());
        return dto;
    }

    public static StudentDocumentDTO toStudentDocumentDTO(StudentDocument document) {
        if (document == null) return null;

        StudentDocumentDTO dto = new StudentDocumentDTO();
        dto.setId(document.getId());
        dto.setStudentId(document.getStudent().getId());
        dto.setStudentPhoto(document.getStudentPhoto());
        dto.setAadharcardPhoto(document.getAadharcardPhoto());
        dto.setPancardPhoto(document.getPancardPhoto());
        dto.setCasteValidationPhoto(document.getCasteValidationPhoto());
        dto.setCasteCertificatePhoto(document.getCasteCertificatePhoto());
        dto.setLeavingCertificatePhoto(document.getLeavingCertificatePhoto());
        dto.setDomicilePhoto(document.getDomicilePhoto());
        dto.setBirthCertificatePhoto(document.getBirthCertificatePhoto());
        dto.setDisabilityCertificate(document.getDisabilityCertificate());
        dto.setStudentSignPhoto(document.getStudentSignPhoto());

        dto.setMarksheet10thCert(document.getMarksheet10thCert());
        dto.setMarksheet12thCert(document.getMarksheet12thCert());
        dto.setGraduationMarksheetCert(document.getGraduationMarksheetCert());
        dto.setNonCreamyLayerCert(document.getNonCreamyLayerCert());
        dto.setIncomeCertificateCert(document.getIncomeCertificateCert());

        return dto;
    }


}
