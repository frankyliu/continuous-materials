package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.exception;

/**
 * @author Gregory Boissinot
 */
public class MongoImportException extends RuntimeException {

    public MongoImportException(Throwable throwable) {
        super(throwable);
    }
}
