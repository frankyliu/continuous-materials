package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.exception;


/**
 * @author Gregory Boissinot
 */
public class DistribPackagerException extends RuntimeException {

    public DistribPackagerException(String message) {
        super(message);
    }

    public DistribPackagerException(Throwable cause) {
        super(cause);
    }

}
