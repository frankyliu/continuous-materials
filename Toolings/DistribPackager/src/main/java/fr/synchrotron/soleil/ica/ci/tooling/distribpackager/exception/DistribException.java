package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.exception;


/**
 * @author Gregory Boissinot
 */
public class DistribException extends RuntimeException {

    public DistribException(String message) {
        super(message);
    }

    public DistribException(Throwable cause) {
        super(cause);
    }

}
