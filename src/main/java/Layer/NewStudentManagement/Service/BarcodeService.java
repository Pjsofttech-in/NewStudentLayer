package Layer.NewStudentManagement.Service;

import com.google.zxing.WriterException;

import java.io.IOException;

public interface BarcodeService
{
    byte[] generateQRCodeForHRInquiry(String role, String email) throws IOException, WriterException;
}
