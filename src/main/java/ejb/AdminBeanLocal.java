package ejb;

import entities.Refunds;
import entities.ReturnRequests;
import entities.Orders;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author muskanjain
 */
@Local
public interface AdminBeanLocal {
    public void approveProducts(int productId);
    public void rejectProducts(int productId);
    public void handleReturn(int returnId, boolean approve);
    public void processRefunds(int refundId);
    
    public List<ReturnRequests> getAllReturnRequests();
    public List<Refunds> getAllRefunds();
    public List<Orders> getAllOrders();
    public void updateOrderStatus(int orderId, String status);
}
