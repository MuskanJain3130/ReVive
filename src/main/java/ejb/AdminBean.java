package ejb;

import entities.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import java.util.List;

/**
 *
 * @author muskanjain
 */
@Stateless
public class AdminBean implements AdminBeanLocal {
    
    @PersistenceContext(unitName="JPU")
    EntityManager em;

    // approve product
    public void approveProducts(int productId){
        Products p = em.find(Products.class, productId);
        p.setApprovalStatus("Approved");
    }

    // reject product
    public void rejectProducts(int productId){
        Products p = em.find(Products.class, productId);
        p.setApprovalStatus("Rejected");
    }

    // process return
    public void handleReturn(int returnId, boolean approve){
        ReturnRequests r = em.find(ReturnRequests.class, returnId);
        if (r != null) {
            r.setStatus(approve ? "Approved" : "Rejected");
            if (approve) {
                // Check if a refund already exists to avoid duplicates
                boolean refundExists = false;
                if (r.getRefundsCollection() != null) {
                    for (Refunds ref : r.getRefundsCollection()) {
                        if (!"Failed".equals(ref.getStatus())) {
                            refundExists = true;
                            break;
                        }
                    }
                }
                if (!refundExists) {
                    Refunds refund = new Refunds();
                    refund.setReturnid(r);
                    double amount = 0;
                    if (r.getOrderdetailid() != null) {
                        amount = r.getOrderdetailid().getPrice() * r.getOrderdetailid().getQuantity();
                    }
                    refund.setAmount(amount);
                    refund.setStatus("Pending");
                    refund.setProcessedAt(new java.util.Date());
                    em.persist(refund);
                }
            }
        }
    }

    // process refund
    public void processRefunds(int refundId){
        Refunds ref = em.find(Refunds.class, refundId);
        if (ref != null) {
            ref.setStatus("Processed");
            ref.setProcessedAt(new java.util.Date());
            if (ref.getReturnid() != null) {
                ref.getReturnid().setStatus("Completed");
                
                // Restock the returned product
                OrderDetails od = ref.getReturnid().getOrderdetailid();
                if (od != null && od.getProductid() != null) {
                    Products p = od.getProductid();
                    p.setQuantity(p.getQuantity() + od.getQuantity());
                    em.merge(p);
                }
            }
        }
    }

    @Override
    public List<ReturnRequests> getAllReturnRequests() {
        return em.createQuery("SELECT r FROM ReturnRequests r ORDER BY r.returnid DESC", ReturnRequests.class).getResultList();
    }

    @Override
    public List<Refunds> getAllRefunds() {
        return em.createQuery("SELECT r FROM Refunds r ORDER BY r.refundid DESC", Refunds.class).getResultList();
    }

    @Override
    public List<Orders> getAllOrders() {
        return em.createQuery("SELECT DISTINCT o FROM Orders o LEFT JOIN FETCH o.orderDetailsCollection LEFT JOIN FETCH o.paymentsCollection ORDER BY o.orderid DESC", Orders.class).getResultList();
    }

    @Override
    public void updateOrderStatus(int orderId, String status) {
        Orders o = em.find(Orders.class, orderId);
        if (o != null) {
            // If the order is being cancelled, restore the stock
            if ("Cancelled".equalsIgnoreCase(status) && !"Cancelled".equalsIgnoreCase(o.getStatus())) {
                if (o.getOrderDetailsCollection() != null) {
                    for (OrderDetails od : o.getOrderDetailsCollection()) {
                        if (od.getProductid() != null) {
                            Products p = od.getProductid();
                            p.setQuantity(p.getQuantity() + od.getQuantity());
                            em.merge(p);
                        }
                    }
                }
            }
            
            o.setStatus(status);
            em.merge(o);
        }
    }
}
