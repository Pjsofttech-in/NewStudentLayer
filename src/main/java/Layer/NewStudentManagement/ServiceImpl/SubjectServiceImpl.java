package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentSubject;
import Layer.NewStudentManagement.Repository.SubjectRepository;
import Layer.NewStudentManagement.Service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService
{

    @Autowired
    private StaffService staffService;

    @Autowired
    private SubjectRepository subjectRepository;


    @Override
    public StudentSubject createSubject(String role, String email, StudentSubject subject)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create subject");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        subject.setBranchCode(branchCode);
        subject.setRole(role);
        subject.setCreatedByEmail(email);
        return subjectRepository.save(subject);
    }

    @Override
    public StudentSubject getSubjectById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get subject");
        }
        StudentSubject subject = subjectRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Subject not found"));
        return subject;

    }

    @Override
    public StudentSubject updateSubject(Long id,String role,String email,StudentSubject subject)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update subject");
        }
        StudentSubject existingSubject = subjectRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Subject not found"));
        existingSubject.setSubject(subject.getSubject());
        return subjectRepository.save(existingSubject);

    }

    @Override
    public void deleteSubjectById(Long id,String role,String email)
    {
        if (!staffService.hasPermission(role,email,"Delete"))
            throw new RuntimeException("You don't have permission to delete subject");
        subjectRepository.deleteById(id);

    }

    @Override
    public List<StudentSubject> getAllSubject(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get subject");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        return subjectRepository.findAllByBranchCode(branchCode);

    }

}
