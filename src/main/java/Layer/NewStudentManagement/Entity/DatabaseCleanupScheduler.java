//package Layer.NewStudentManagement.Entity;
//
//import Layer.NewStudentManagement.Service.NotificationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DatabaseCleanupScheduler {
//
////    @Autowired
////    FeesService feesService;
//
//    @Autowired
//    NotificationService notificationService;
//
//    // ⏱️ Configuration
////    @Scheduled(cron = "0 0 0 * * ?")//option B: Runs daily at 12:00 AM (Midnight) using a Cron Expression
////    @Scheduled(cron = "0 */1 * * * ?")//option B: Runs daily at 12:00 AM (Midnight) using a Cron Expression
////    public void generateDailyFeeReports() {
////        feesService.getFeesDueInDays(7);
////    }
//
////    @Scheduled(fixedRate = 5000) //for testing runs every 5 secs
//    @Scheduled(cron = "0 0 0 * * ?")//option B: Runs daily at 12:00 AM (Midnight) using a Cron Expression
//    public void generateDailyFeeReports() {
//        notificationService.deleteOlderNotification();
//    }
//}
