package Layer.NewStudentManagement.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class ReceiptUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static String generateTimestampReceiptId() {
        // 1. Get the current exact timestamp formatted (e.g., "20260523_113542")
        String timestamp = LocalDateTime.now().format(FORMATTER);

        // 2. Generate a 2-digit random suffix (from 10 to 99) as a collision buffer
        int randomSuffix = ThreadLocalRandom.current().nextInt(10, 100);

        // 3. Assemble the string safely under 40 characters
        return "REC_" + timestamp + "_" + randomSuffix;
    }
}