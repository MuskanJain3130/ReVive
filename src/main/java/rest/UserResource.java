/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

/**
 *
 * @author muskanjain
 */
import ejb.UserBeanLocal;
import entities.Orders;
import entities.Products;
import entities.ReturnRequests;
import entities.Reviews;
import entities.ShoppingCart;
import entities.Wishlist;
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

    // 1. Add Product 
    //testing done
    @POST
    @Path("addProduct/{sellerid}")
    public Response addProduct(Products p, @PathParam("sellerid") int sellerid) {
        userBean.addProduct(p, sellerid);
        return Response.ok("Product added successfully").build();
    }

    // 2. Update Product
    //testing done
    @PUT
    @Path("updateProduct")
    public Response updateProduct(Products p) {
        userBean.updateProduct(p);
        return Response.ok("Product updated successfully").build();
    }

    // 3. Add To Cart
    //testing done
    @POST
    @Path("addToCart/{buyerid}/{productid}/{quantity}")
    public Response addToCart(ShoppingCart c,
                              @PathParam("buyerid") int buyerid,
                              @PathParam("productid") int productid,
                              @PathParam("quantity") int quantity) {
        userBean.addToCart(c, buyerid, productid, quantity);
        return Response.ok("Added to cart successfully").build();
    }

    // 4. Place Order
    @POST
    @Path("placeOrder")
    public Response placeOrder(Orders order) {
        userBean.placeOrder(order);
        return Response.ok("Order placed successfully").build();
    }

    // 5. Add Wishlist
    //testing done
    @POST
    @Path("addWishlist/{buyerid}/{productid}")
    public Response addWishlist(Wishlist w,
                                @PathParam("buyerid") int buyerid,
                                @PathParam("productid") int productid) {
        userBean.addWishlist(w, buyerid, productid);
        return Response.ok("Wishlist added successfully").build();
    }

    // 6. Add Review
    //testing done
    @POST
    @Path("addReview/{orderdetailid}/{reviewerid}")
    public Response addReview(Reviews r,
                              @PathParam("orderdetailid") int orderdetailid,
                              @PathParam("reviewerid") int reviewerid) {
        userBean.addReview(r, orderdetailid, reviewerid);
        return Response.ok("Review added successfully").build();
    }

    // 7. Return Request
    //testing done
    @POST
    @Path("requestReturn")
    public Response requestReturn(ReturnRequests r) {
        userBean.requestReturn(r);
        return Response.ok("Return request submitted successfully").build();
    }

    // 8. My Products
    //testing done
    @GET
    @Path("myProducts/{userId}")
    public List<Products> myProducts(@PathParam("userId") int userId) {
        return userBean.myProducts(userId);
    }
}