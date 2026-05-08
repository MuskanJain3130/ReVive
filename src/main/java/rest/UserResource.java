/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

import ejb.UserBeanLocal;
import entities.Orders;
import entities.Products;
import entities.ReturnRequests;
import entities.Reviews;
import entities.ShoppingCart;
import entities.Wishlist;
import entities.OrderDetails;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @EJB
    private UserBeanLocal userBean;

    @POST
    @Path("addProduct/{sellerid}")
    public Response addProduct(Products p, @PathParam("sellerid") int sellerid) {
        userBean.addProduct(p, sellerid);
        return Response.ok("Product added successfully").build();
    }

    @PUT
    @Path("updateProduct")
    public Response updateProduct(Products p) {
        userBean.updateProduct(p);
        return Response.ok("Product updated successfully").build();
    }

    @POST
    @Path("addToCart/{buyerid}/{productid}/{quantity}")
    public Response addToCart(ShoppingCart c,
                              @PathParam("buyerid") int buyerid,
                              @PathParam("productid") int productid,
                              @PathParam("quantity") int quantity) {
        userBean.addToCart(c, buyerid, productid, quantity);
        return Response.ok("Added to cart successfully").build();
    }

    @POST
    @Path("placeOrder")
    public Response placeOrder(Orders order) {
        userBean.placeOrder(order);
        return Response.ok("Order placed successfully").build();
    }

    @POST
    @Path("addWishlist/{buyerid}/{productid}")
    public Response addWishlist(Wishlist w,
                                @PathParam("buyerid") int buyerid,
                                @PathParam("productid") int productid) {
        userBean.addWishlist(w, buyerid, productid);
        return Response.ok("Wishlist added successfully").build();
    }

    @POST
    @Path("addReview/{orderdetailid}/{reviewerid}")
    public Response addReview(Reviews r,
                              @PathParam("orderdetailid") int orderdetailid,
                              @PathParam("reviewerid") int reviewerid) {
        userBean.addReview(r, orderdetailid, reviewerid);
        return Response.ok("Review added successfully").build();
    }

    @POST
    @Path("requestReturn")
    public Response requestReturn(ReturnRequests r) {
        userBean.requestReturn(r);
        return Response.ok("Return request submitted successfully").build();
    }

    @GET
    @Path("myProducts/{userId}")
    public List<Products> myProducts(@PathParam("userId") int userId) {
        return userBean.myProducts(userId);
    }

    @GET
    @Path("allProducts")
    public List<Products> getAllProducts() {
        return userBean.getAllProducts();
    }

    @GET
    @Path("approvedProducts")
    public List<Products> getAllApprovedProducts() {
        return userBean.getAllApprovedProducts();
    }

    @POST
    @Path("register/{roleid}")
    public Response registerUser(entities.Users u, @PathParam("roleid") int roleid) {
        userBean.registerUser(u, roleid);
        return Response.ok("User registered successfully").build();
    }

    @GET
    @Path("getUserByUsername/{username}")
    public entities.Users getUserByUsername(@PathParam("username") String username) {
        return userBean.getUserByUsername(username);
    }

    @GET
    @Path("product/{id}")
    public Products getProductById(@PathParam("id") int id) {
        return userBean.getProductById(id);
    }

    @GET
    @Path("product/{id}/reviews")
    public List<Reviews> getReviewsForProduct(@PathParam("id") int id) {
        return userBean.getReviewsForProduct(id);
    }

    @GET
    @Path("orderDetails/{userId}/{productId}")
    public OrderDetails getOrderDetailsForUserProduct(@PathParam("userId") int userId, @PathParam("productId") int productId) {
        return userBean.getOrderDetailsForUserProduct(userId, productId);
    }
}