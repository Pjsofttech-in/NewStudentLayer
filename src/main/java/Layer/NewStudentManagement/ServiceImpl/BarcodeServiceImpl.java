package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.BarcodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class BarcodeServiceImpl implements BarcodeService
{

    private static final String HR_INQUIRY_BASE_URL = "https://pjsofttech.in/schoolqrcode/";

    @Autowired
    private StaffService staffService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public byte[] generateQRCodeForHRInquiry(String role, String email) throws IOException, WriterException {
        if (!staffService.hasPermission(role, email, "Get"))
        {
            throw new RuntimeException("You don't have permission to generate QR code for inquiry");

        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        if (branchCode == null || branchCode.isEmpty()) {
            throw new RuntimeException("Branch code not found for email: " + email);
        }

        String encodedBranchCode = Base64.getUrlEncoder().encodeToString(branchCode.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.toUpperCase());
        claims.put("branchCode", encodedBranchCode);

        String jwt = jwtUtil.generateTokenWithClaims("user@gmail.com", claims, Duration.ofDays(90));

        String qrData = HR_INQUIRY_BASE_URL + "&token=" +jwt;

        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();

    }
}
