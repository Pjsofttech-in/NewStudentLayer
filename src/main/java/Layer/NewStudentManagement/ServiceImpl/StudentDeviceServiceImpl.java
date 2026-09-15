package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.DeviceRegistrationRequest;
import Layer.NewStudentManagement.Entity.StudentDeviceEntity;
import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Repository.StudentDeviceRepository;
import Layer.NewStudentManagement.Repository.StudentRepository;
import Layer.NewStudentManagement.Service.StudentDeviceService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StudentDeviceServiceImpl implements StudentDeviceService {

    private final StudentDeviceRepository studentDeviceRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public void registerDevice(DeviceRegistrationRequest request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException("User is not authenticated");
        }

        String email = authentication.getName();

        StudentEntity student =
                studentRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        StudentDeviceEntity device =
                studentDeviceRepository
                        .findByFirebaseInstallationId(request.getFid())
                        .orElse(null);

        if (device == null) {

            device = new StudentDeviceEntity();

            device.setFirebaseInstallationId(request.getFid());
            device.setStudent(student);
            device.setActive(true);
            device.setCreatedAt(LocalDateTime.now());

        } else {

            /*
             * The same Firebase installation is registering again.
             *
             * This can happen when:
             * - app is reopened
             * - user logs in again
             * - app updates
             * - device registration is refreshed
             */

            device.setStudent(student);
            device.setActive(true);
        }

        device.setDeviceType(request.getDeviceType());
        device.setDeviceName(request.getDeviceName());
        device.setAppVersion(request.getAppVersion());
        device.setLastSeenAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());

        studentDeviceRepository.save(device);
    }

    @Override
    @Transactional
    public void deactivateDevice(String fid) {

        StudentDeviceEntity device =
                studentDeviceRepository
                        .findByFirebaseInstallationId(fid)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Device not found"
                                )
                        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        if (!device.getStudent()
                .getEmail()
                .equalsIgnoreCase(email)) {

            throw new RuntimeException(
                    "You are not authorized to modify this device"
            );
        }

        device.setActive(false);
        device.setUpdatedAt(LocalDateTime.now());

        studentDeviceRepository.save(device);
    }
}