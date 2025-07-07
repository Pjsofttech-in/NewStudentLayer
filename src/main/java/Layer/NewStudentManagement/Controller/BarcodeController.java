package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Service.BarcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
public class BarcodeController
{
    @Autowired
    private BarcodeService barcodeService;

    @GetMapping("/createSchoolFormBarcode")
    public ResponseEntity<byte[]> generateInquiryQRCode(@RequestParam String role, @RequestParam String email) {
        try {
            byte[] qrImage = barcodeService.generateQRCodeForHRInquiry(role, email);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrImage.length);
            return ResponseEntity.ok().headers(headers).body(qrImage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Failed to generate QR code: " + e.getMessage()).getBytes());
        }
    }

}
