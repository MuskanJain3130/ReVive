/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package ejb;

import jakarta.ejb.Local;

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
    
}
