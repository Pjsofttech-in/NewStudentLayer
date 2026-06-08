//package Layer.NewStudentManagement.Entity;
//
//import Layer.NewStudentManagement.Service.FeesService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DatabaseCleanupScheduler {
//
//    @Autowired
//    FeesService feesService;
//
//    // ⚡ Configuration option A: Runs exactly every 5 seconds (5000 milliseconds)
////    @Scheduled(fixedRate = 5000)
////    public void clearExpiredSessions() {
////        System.out.println("Fixed Rate Task Executing at: " + LocalDateTime.now());
////    }
//
//    // ⏱️ Configuration
////    @Scheduled(cron = "0 0 0 * * ?")//option B: Runs daily at 12:00 AM (Midnight) using a Cron Expression
//    @Scheduled(cron = "0 */1 * * * ?")//option B: Runs daily at 12:00 AM (Midnight) using a Cron Expression
//    public void generateDailyFeeReports() {
//        feesService.getFeesDueInDays(7);
//    }
//}
