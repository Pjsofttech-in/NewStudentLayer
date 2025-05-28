package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentGroup;

import java.util.List;

public interface GroupService
{
    StudentGroup createGroup(String role,String email,StudentGroup group);
    StudentGroup getGroupById(Long id,String role,String email);
    StudentGroup updateGroup(Long id,String role,String email,StudentGroup group);
    void deleteGroupById(Long id,String role,String email);
    List<StudentGroup> getAllGroupByName(String role, String email);
}
