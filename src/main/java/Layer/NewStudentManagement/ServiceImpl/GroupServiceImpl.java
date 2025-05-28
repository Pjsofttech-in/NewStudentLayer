package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentGroup;
import Layer.NewStudentManagement.Repository.GroupRepository;
import Layer.NewStudentManagement.Service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService
{
    @Autowired
    private StaffService staffService;

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public StudentGroup createGroup(String role, String email, StudentGroup group)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create group");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        group.setRole(role);
        group.setBranchCode(branchCode);
        group.setCreatedByEmail(email);
        return groupRepository.save(group);

    }

    @Override
    public StudentGroup getGroupById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get group");
        }
        StudentGroup group = groupRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Group not found"));
        return group;

    }

    @Override
    public StudentGroup updateGroup(Long id,String role,String email,StudentGroup group)
    {
        if (!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update group");
        }
        StudentGroup existingGroup = groupRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Group not found"));
        existingGroup.setStudentGroup(group.getStudentGroup());
        return groupRepository.save(existingGroup);

    }

    @Override
    public void deleteGroupById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete group");
        }
        groupRepository.deleteById(id);
    }

    @Override
    public List<StudentGroup> getAllGroupByName(String role, String email)
    {
        if (!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get group");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        return groupRepository.getAllByBranchCode(branchCode);

    }
}
