/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/JerseyClient.java to edit this template
 */
package client;

/**
 * Jersey REST client generated for REST resource:AdminResource [admin]<br>
 * USAGE:
 * <pre>
 *        AdminClient client = new AdminClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author taman
 */
public class AdminClient {

    private jakarta.ws.rs.client.WebTarget webTarget;
    private jakarta.ws.rs.client.Client client;

    private static String getBaseUri() {
        try {
            jakarta.faces.context.FacesContext facesContext = jakarta.faces.context.FacesContext.getCurrentInstance();
            if (facesContext != null) {
                jakarta.servlet.http.HttpServletRequest request = (jakarta.servlet.http.HttpServletRequest) facesContext.getExternalContext().getRequest();
                if (request != null) {
                    String scheme = request.getScheme();
                    String serverName = request.getServerName();
                    int port = request.getServerPort();
                    String contextPath = request.getContextPath();
                    return scheme + "://" + serverName + ":" + port + contextPath + "/api";
                }
            }
        } catch (Exception e) {
            // Fallback
        }
        return "http://localhost:8085/ReVive/api";
    }

    public AdminClient() {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(getBaseUri()).path("admin");
    }

    public jakarta.ws.rs.core.Response approveProduct(String productId) throws jakarta.ws.rs.ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("approveProduct/{0}", new Object[]{productId}))
                .request().put(jakarta.ws.rs.client.Entity.json(""), jakarta.ws.rs.core.Response.class);
    }

    public jakarta.ws.rs.core.Response rejectProduct(String productId) throws jakarta.ws.rs.ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("rejectProduct/{0}", new Object[]{productId}))
                .request().put(jakarta.ws.rs.client.Entity.json(""), jakarta.ws.rs.core.Response.class);
    }

    public jakarta.ws.rs.core.Response handleReturn(String returnId, String approve) throws jakarta.ws.rs.ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("handleReturn/{0}/{1}", new Object[]{returnId, approve}))
                .request().put(jakarta.ws.rs.client.Entity.json(""), jakarta.ws.rs.core.Response.class);
    }

    public jakarta.ws.rs.core.Response processRefund(String refundId) throws jakarta.ws.rs.ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("processRefund/{0}", new Object[]{refundId}))
                .request().put(jakarta.ws.rs.client.Entity.json(""), jakarta.ws.rs.core.Response.class);
    }

    public void close() {
        client.close();
    }
}
