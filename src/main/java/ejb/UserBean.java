/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package ejb;

import entities.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 *
 * @author muskanjain
 */
@Stateless
//@RolesAllowed({"BUYER","SELLER"})
public class UserBean implements UserBeanLocal{

    @PersistenceContext
    EntityManager em;

    // register product (seller)
    public void addProduct(Products p,int sellerid){
        
        Users seller=em.find(Users.class, sellerid);
        p.setSellerid(seller);
        p.setApprovalStatus("Pending");
        if(p.getCreatedAt() == null){
        p.setCreatedAt(new Date());  // auto-set current timestamp
        }
        System.out.println(p);
        em.persist(p);
    }

    // update product
//    public void updateProduct(Products p){
//        em.merge(p);
//    }
    
    public void updateProduct(Products p) {
    Products existing = em.find(Products.class, p.getProductid());
    if (existing != null) {
        if (p.getTitle() != null) existing.setTitle(p.getTitle());
        if (p.getDescription() != null) existing.setDescription(p.getDescription());
        if (p.getPrice() != null) existing.setPrice(p.getPrice());
        if (p.getProductCondition() != null) existing.setProductCondition(p.getProductCondition());
        if (p.getQuantity() != null) existing.setQuantity(p.getQuantity());
        if (p.getStatus() != null) existing.setStatus(p.getStatus());
        if (p.getApprovalStatus() != null) existing.setApprovalStatus(p.getApprovalStatus());
        em.merge(existing);
    }
}

    // add to cart
//    public void addToCart(ShoppingCart c,int buyerid,int productid,int quantity){
//        Users buyer=em.find(Users.class, buyerid);
//        Products product=em.find(Products.class, productid);
//        c.setUserid(buyer);
//        c.setProductid(product);
//        c.setQuantity(quantity);
//        em.persist(c);
//    }
    
    public void addToCart(ShoppingCart c,int buyerid,int productid,int quantity){
        Users buyer = em.find(Users.class, buyerid);
        Products product = em.find(Products.class, productid);
        c.setUserid(buyer);
        c.setProductid(product);
        c.setQuantity(quantity);
    
        // Set the addedAt timestamp
        c.setAddedAt(new Date());
    
        em.persist(c);
    }

    // place order
    public void placeOrder(Orders order){
    em.persist(order);

    if(order.getOrderDetailsCollection() != null){
        for(OrderDetails d : order.getOrderDetailsCollection()){

            d.setOrderid(order);
            em.persist(d);

            Products p = em.find(Products.class, d.getProductid().getProductid());

            if (p != null) {
                p.setQuantity(p.getQuantity() - d.getQuantity());
            }
        }
    }
}
    
    

    // wishlist
    public void addWishlist(Wishlist w,int buyerid,int productid){
        Users buyer=em.find(Users.class, buyerid);
        Products product=em.find(Products.class, productid);
        w.setUserid(buyer);
        w.setProductid(product);
        em.persist(w);
    }

    // review
    public void addReview(Reviews r,int orderdetailid, int reviwerid){
        Users buyer=em.find(Users.class, reviwerid);
        OrderDetails orderdetail=em.find(OrderDetails.class, orderdetailid);
        r.setOrderdetailid(orderdetail);
        r.setReviewerid(buyer);
        if(r.getCreatedAt() == null){
        r.setCreatedAt(new Date());  // auto-set current timestamp
        }
        em.persist(r);
    }

    // return request
    public void requestReturn(ReturnRequests r){
        if(r.getRequestedAt()== null){
        r.setRequestedAt(new Date());  // auto-set current timestamp
        }
        em.persist(r);
    }

    // user products
//    public List<Products> myProducts(int userId){
//        return em.createQuery(
//          "SELECT p FROM Product p WHERE p.seller.userid=:id",
//          Products.class)
//          .setParameter("id", userId)
//          .getResultList();
//    }
    
    // user products
    public List<Products> myProducts(int userId){
        return em.createQuery(
        "SELECT p FROM Products p WHERE p.sellerid.userid = :id",
        Products.class)
        .setParameter("id", userId)
        .getResultList();
    }

//    @Override
//    public void addProduct(Products p) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
}