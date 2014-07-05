package fr.synchrotron.soleil.ica.proxy.midlleware;

/**
 * @author Gregory Boissinot
 */
public interface ProxyMiddleware {

    public void pull(MiddlewareContext context);

    public void push(MiddlewareContext context);

}
