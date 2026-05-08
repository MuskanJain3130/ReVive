package ejb;

import entities.*;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import jakarta.security.enterprise.identitystore.PasswordHash;
import java.util.Date;
import java.util.List;

/**
 *
 * @author muskanjain
 */
@Stateless
public class UserBean implements UserBeanLocal {

    @PersistenceContext(unitName="JPU")
    EntityManager em;

    @Inject
    private PasswordHash passwordHash;

    // register product (seller)
    @Override
    public void addProduct(Products p, int sellerid) {
        Users seller = em.find(Users.class, sellerid);
        p.setSellerid(seller);
        p.setApprovalStatus("Pending");
        if (p.getCreatedAt() == null) {
            p.setCreatedAt(new Date());
        }
        em.persist(p);
    }

    @Override
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

    @Override
    public void addToCart(ShoppingCart c, int buyerid, int productid, int quantity) {
        Users buyer = em.find(Users.class, buyerid);
        Products product = em.find(Products.class, productid);
        c.setUserid(buyer);
        c.setProductid(product);
        c.setQuantity(quantity);
        c.setAddedAt(new Date());
        em.persist(c);
    }

    @Override
    public void placeOrder(Orders order) {
        em.persist(order);
        if (order.getOrderDetailsCollection() != null) {
            for (OrderDetails d : order.getOrderDetailsCollection()) {
                d.setOrderid(order);
                em.persist(d);
                Products p = em.find(Products.class, d.getProductid().getProductid());
                if (p != null) {
                    p.setQuantity(p.getQuantity() - d.getQuantity());
                }
            }
        }
    }

    @Override
    public void addWishlist(Wishlist w, int buyerid, int productid) {
        Users buyer = em.find(Users.class, buyerid);
        Products product = em.find(Products.class, productid);
        w.setUserid(buyer);
        w.setProductid(product);
        em.persist(w);
    }

    @Override
    public void addReview(Reviews r, int orderdetailid, int reviwerid) {
        Users buyer = em.find(Users.class, reviwerid);
        OrderDetails orderdetail = em.find(OrderDetails.class, orderdetailid);
        r.setOrderdetailid(orderdetail);
        r.setReviewerid(buyer);
        if (r.getCreatedAt() == null) {
            r.setCreatedAt(new Date());
        }
        em.persist(r);
    }

    @Override
    public void requestReturn(ReturnRequests r) {
        if (r.getRequestedAt() == null) {
            r.setRequestedAt(new Date());
        }
        em.persist(r);
    }

    @Override
    public List<Products> myProducts(int userId) {
        return em.createQuery(
                "SELECT p FROM Products p WHERE p.sellerid.userid = :id",
                Products.class)
                .setParameter("id", userId)
                .getResultList();
    }

    @Override
    public Users getUserByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM Users u WHERE u.username = :u", Users.class)
                    .setParameter("u", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Products> getAllProducts() {
        return em.createNamedQuery("Products.findAll", Products.class).getResultList();
    }

    @Override
    public List<Products> getAllApprovedProducts() {
        return em.createQuery("SELECT p FROM Products p WHERE p.approvalStatus = 'Approved'", Products.class).getResultList();
    }

    @Override
    public void registerUser(Users u, int roleId) {
        RoleMaster role = em.find(RoleMaster.class, roleId);
        u.setRoleid(role);

        if (u.getPassword() != null && !u.getPassword().isEmpty()) {
            String encodedPassword = passwordHash.generate(u.getPassword().toCharArray());
            u.setPassword(encodedPassword);
        }

        if (u.getCreatedAt() == null) {
            u.setCreatedAt(new Date());
        }
        em.persist(u);
    }

    @Override
    public Products getProductById(int id) {
        return em.find(Products.class, id);
    }

    @Override
    public List<Reviews> getReviewsForProduct(int productId) {
        return em.createQuery("SELECT r FROM Reviews r WHERE r.orderdetailid.productid.productid = :productId ORDER BY r.created_at DESC", Reviews.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    @Override
    public OrderDetails getOrderDetailsForUserProduct(int userId, int productId) {
        try {
            return em.createQuery("SELECT d FROM OrderDetails d WHERE d.orderid.buyerid.userid = :userId AND d.productid.productid = :productId ORDER BY d.orderdetailid DESC", OrderDetails.class)
                     .setParameter("userId", userId)
                     .setParameter("productId", productId)
                     .setMaxResults(1)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Addresses> getAddressesForUser(int userId) {
        return em.createQuery("SELECT a FROM Addresses a WHERE a.userid.userid = :userId", Addresses.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public void addAddress(Addresses address, int userId) {
        Users user = em.find(Users.class, userId);
        if(user != null) {
            address.setUserid(user);
            em.persist(address);
        }
    }

    @Override
    public void processCheckout(Orders order, List<OrderDetails> details, Payments payment) {
        if (details == null || details.isEmpty()) {
            throw new RuntimeException("No products found in order details! Checkout aborted.");
        }
        
        // Ensure address is managed
        if (order.getAddressid() != null) {
            Addresses addr = em.find(Addresses.class, order.getAddressid().getAddressid());
            order.setAddressid(addr);
        }
        
        em.persist(order);
        for(OrderDetails d : details) {
            // Fetch managed entity to avoid "new object through relationship" error
            Products p = em.find(Products.class, d.getProductid().getProductid());
            if (p != null) {
                d.setProductid(p);
                d.setOrderid(order);
                em.persist(d);
                
                // Update stock
                p.setQuantity(Math.max(0, p.getQuantity() - d.getQuantity()));
                em.merge(p);
            }
        }
        payment.setOrderid(order);
        em.persist(payment);
    }

    @Override
    public List<Orders> getOrdersForBuyer(int buyerId) {
        // Use JOIN FETCH to avoid lazy loading issues and ensure data is present
        return em.createQuery("SELECT DISTINCT o FROM Orders o LEFT JOIN FETCH o.orderDetailsCollection LEFT JOIN FETCH o.paymentsCollection WHERE o.buyerid.userid = :id ORDER BY o.orderid DESC", Orders.class)
                .setParameter("id", buyerId)
                .getResultList();
    }

    @Override
    public List<OrderDetails> getSalesForSeller(int sellerId) {
        return em.createQuery("SELECT d FROM OrderDetails d WHERE d.productid.sellerid.userid = :id ORDER BY d.orderid.orderdate DESC", OrderDetails.class)
                .setParameter("id", sellerId)
                .getResultList();
    }

    @Override
    public void updateUserInfo(Users user) {
        Users existing = em.find(Users.class, user.getUserid());
        if (existing != null) {
            existing.setUsername(user.getUsername());
            existing.setEmail(user.getEmail());
            existing.setPhone(user.getPhone());
            em.merge(existing);
        }
    }
}