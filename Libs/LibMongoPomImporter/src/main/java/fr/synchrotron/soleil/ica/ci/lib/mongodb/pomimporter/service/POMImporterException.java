package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

/**
 * @author Gregory Boissinot
 */
public class POMImporterException extends RuntimeException {

    public POMImporterException(String message) {
        super(message);
    }

    public POMImporterException(String message, Throwable cause) {
        super(message, cause);
    }

    public POMImporterException(Throwable cause) {
        super(cause);
    }

}
