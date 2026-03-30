/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package ejb;

import entities.*;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface UserBeanLocal {

    void addProduct(Products p,int sellerid);

    void updateProduct(Products p);

    void addToCart(ShoppingCart c,int buyerid,int productid,int quantity);

    void placeOrder(Orders order);

    void addWishlist(Wishlist w,int buyerid, int productid);

    void addReview(Reviews r,int orderdetailid,int reviwerid);

    void requestReturn(ReturnRequests r);

    List<Products> myProducts(int userId);

    Users getUserByUsername(String username);

    List<Products> getAllProducts();

    List<Products> getAllApprovedProducts();

    void registerUser(Users u, int roleId);
}