/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

/**
 *
 * @author muskanjain
 */

import ejb.AdminBeanLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource {

    @EJB
    private AdminBeanLocal adminBean;

    // 1. Approve Product
    @PUT
    @Path("approveProduct/{productId}")
    public Response approveProduct(@PathParam("productId") int productId) {
        adminBean.approveProducts(productId);
        return Response.ok("Product approved successfully").build();
    }

    // 2. Reject Product
    @PUT
    @Path("rejectProduct/{productId}")
    public Response rejectProduct(@PathParam("productId") int productId) {
        adminBean.rejectProducts(productId);
        return Response.ok("Product rejected successfully").build();
    }

    // 3. Handle Return
    @PUT
    @Path("handleReturn/{returnId}/{approve}")
    public Response handleReturn(@PathParam("returnId") int returnId,
                                 @PathParam("approve") boolean approve) {
        adminBean.handleReturn(returnId, approve);
        return Response.ok("Return handled successfully").build();
    }

    // 4. Process Refund
    @PUT
    @Path("processRefund/{refundId}")
    public Response processRefund(@PathParam("refundId") int refundId) {
        adminBean.processRefunds(refundId);
        return Response.ok("Refund processed successfully").build();
    }
}
