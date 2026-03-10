package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentEntranceExam;

import Layer.NewStudentManagement.Repository.EntranceExamRepository;
import Layer.NewStudentManagement.Service.EntranceExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntranceExamServiceImpl implements EntranceExamService
{

    @Autowired
    EntranceExamRepository entranceExamRepository;

    @Autowired
    StaffService staffService;

    @Override
    public StudentEntranceExam createIntranceExam(String role, String email,StudentEntranceExam exam)
    {

        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create IntranceExam");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role,email);

        if(entranceExamRepository.existsIntranceName(
                exam.getEntranceExamName(),branchCode))
        {
            throw new RuntimeException("Intrance Exam already exists for this branch");
        }

        exam.setBranchCode(branchCode);
        exam.setRole(role);
        exam.setCreatedByEmail(email);

        return entranceExamRepository.save(exam);
    }

    @Override
    public List<StudentEntranceExam> getAllIntranceExams(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to Get IntranceExam");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role,email);

        return entranceExamRepository.findAllByBranchCode(branchCode);

    }

    @Override
    public StudentEntranceExam getIntranceExamById(String role, String email,Long id)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to Get IntranceExam");
        }

        StudentEntranceExam entranceExam = entranceExamRepository.findById(id).orElseThrow(()->
                new RuntimeException("Entrance exam Not Found"));

        return entranceExam;


    }

    @Override
    public StudentEntranceExam updateIntranceExam(Long id, String role, String email,StudentEntranceExam exam)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to Get IntranceExam");
        }

        StudentEntranceExam entranceExam = entranceExamRepository.findById(id).orElseThrow(()->
                new RuntimeException("Entrance exam Not Found"));
        entranceExam.setEntranceExamName(exam.getEntranceExamName());
        return entranceExam;

    }

    @Override
    public String deleteIntranceExam(Long id,String role, String email)
    {

        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to Get IntranceExam");
        }

        StudentEntranceExam entranceExam = entranceExamRepository.findById(id).orElseThrow(()->
                new RuntimeException("Entrance exam Not Found"));

         entranceExamRepository.deleteById(id);
        return "Exam deleted successfully";

    }


}
