package client;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.client.Entity;
import java.text.MessageFormat;

public class UserClient {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/ReVive/api";

    public UserClient() {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("user");
    }

    public Response requestReturn(entities.ReturnRequests r) throws ClientErrorException {
        return webTarget.path("requestReturn").request(MediaType.APPLICATION_JSON).post(Entity.entity(r, MediaType.APPLICATION_JSON), Response.class);
    }

    public <T> T myProducts(jakarta.ws.rs.core.GenericType<T> responseType, String userId) throws ClientErrorException {
        return webTarget.path(MessageFormat.format("myProducts/{0}", new Object[]{userId})).request(MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getAllProducts(jakarta.ws.rs.core.GenericType<T> responseType) throws ClientErrorException {
        return webTarget.path("allProducts").request(MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getAllApprovedProducts(jakarta.ws.rs.core.GenericType<T> responseType) throws ClientErrorException {
        return webTarget.path("approvedProducts").request(MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response registerUser(entities.Users u, String roleId) throws ClientErrorException {
        return webTarget.path(MessageFormat.format("register/{0}", new Object[]{roleId})).request(MediaType.APPLICATION_JSON).post(Entity.entity(u, MediaType.APPLICATION_JSON), Response.class);
    }

    public <T> T getUserByUsername(Class<T> responseType, String username) throws ClientErrorException {
        return webTarget.path(MessageFormat.format("getUserByUsername/{0}", new Object[]{username})).request(MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response placeOrder(entities.Orders order) throws ClientErrorException {
        return webTarget.path("placeOrder").request(MediaType.APPLICATION_JSON).post(Entity.entity(order, MediaType.APPLICATION_JSON), Response.class);
    }

    public Response addProduct(entities.Products p, String sellerid) throws ClientErrorException {
        return webTarget.path(MessageFormat.format("addProduct/{0}", new Object[]{sellerid})).request(MediaType.APPLICATION_JSON).post(Entity.entity(p, MediaType.APPLICATION_JSON), Response.class);
    }

    public Response updateProduct(entities.Products p) throws ClientErrorException {
        return webTarget.path("updateProduct").request(MediaType.APPLICATION_JSON).put(Entity.entity(p, MediaType.APPLICATION_JSON), Response.class);
    }

    public Response addWishlist(entities.Wishlist w, String buyerid, String productid) throws ClientErrorException {
        return webTarget.path(MessageFormat.format("addWishlist/{0}/{1}", new Object[]{buyerid, productid})).request(MediaType.APPLICATION_JSON).post(Entity.entity(w, MediaType.APPLICATION_JSON), Response.class);
    }

    public Response addToCart(entities.ShoppingCart c, String buyerid, String productid, String quantity) throws ClientErrorException {
        return webTarget.path(MessageFormat.format("addToCart/{0}/{1}/{2}", new Object[]{buyerid, productid, quantity})).request(MediaType.APPLICATION_JSON).post(Entity.entity(c, MediaType.APPLICATION_JSON), Response.class);
    }

    public Response addReview(entities.Reviews r, String orderdetailid, String reviewerid) throws ClientErrorException {
        return webTarget.path(MessageFormat.format("addReview/{0}/{1}", new Object[]{orderdetailid, reviewerid})).request(MediaType.APPLICATION_JSON).post(Entity.entity(r, MediaType.APPLICATION_JSON), Response.class);
    }

    public void close() {
        client.close();
    }
}
