package Layer.NewStudentManagement.DTO;

public interface FeesRevenueProjection
{

    Double getTotalFees();      // from totalamount
    Double getTotalPaid();      // from paidAmount
    Double getTotalPending();   // from pendingAmount
}
