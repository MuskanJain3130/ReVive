/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package ejb;

import entities.Products;
import entities.Refunds;
import entities.ReturnRequests;
import entities.Users;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;


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
        r.setStatus(approve ? "Approved" : "Rejected");
    }

    // process refund
    public void processRefunds(int refundId){
        Refunds ref = em.find(Refunds.class, refundId);
        ref.setStatus("Processed");
    }

    // disable user
//    public void disableUser(int userId){
//        Users u = em.find(Users.class, userId);
//        u.setActive(false);
//    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
