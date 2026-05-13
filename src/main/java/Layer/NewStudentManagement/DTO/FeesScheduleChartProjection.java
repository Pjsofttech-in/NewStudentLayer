package Layer.NewStudentManagement.DTO;


public interface FeesScheduleChartProjection
{
    String getYear();
    Boolean getIsPaid();
    String getMonthName();
    Double getAmount();
    String getDayOfMonth();
}
