/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ejb.EJB;
import ejb.AdminBeanLocal;   // change package if different
import ejb.UserBeanLocal;
import entities.*;



/**
 *
 * @author muskanjain
 */
@WebServlet(name = "TestServlet", urlPatterns = {"/TestServlet"})
public class TestServlet extends HttpServlet {
    @EJB
//    private AdminBeanLocal admin;
    private UserBeanLocal user;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            
            /* TODO output your page here. You may use following sample code. */
            out.println("<html><body>");

    try {

//        admin.approveProducts(1);
//        admin.rejectProducts(2);
//        admin.handleReturn(2, true);
//        admin.processRefunds(3);

          Products p = new Products();
                p.setTitle("Test Laptop");
                p.setDescription("i5 8GB RAM");
                p.setPrice(45000.0);
                p.setQuantity(5);
                p.setStatus("Active");
                
                p.setCreatedAt(new java.util.Date());   
                System.out.println(p);
                user.addProduct(p,3);

                // -------- 2️⃣ Add Wishlist --------
                Wishlist w = new Wishlist(); // existing product id
                user.addWishlist(w, 1, 2);

                // -------- 3️⃣ Add Review --------
                Reviews r = new Reviews();
                
                r.setRating(5);
                r.setComment("Very Good Product");

                user.addReview(r,1,2);

                // -------- 4️⃣ Add To Cart --------
                ShoppingCart cart = new ShoppingCart();
                user.addToCart(cart,2,1,1);

                out.println("<h2>UserBean methods executed successfully ✅</h2>");

//        out.println("<h2>Admin EJB methods executed successfully ✅</h2>");

    } catch (Exception e) {
        out.println("<h2>Error: " + e.getMessage() + "</h2>");
        e.printStackTrace(out);
    }

    out.println("</body></html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
