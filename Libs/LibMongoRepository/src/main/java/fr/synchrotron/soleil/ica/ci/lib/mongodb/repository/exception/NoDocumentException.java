package fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.exception;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBException;

/**
 * @author Gregory Boissinot
 */
public class NoDocumentException extends MongoDBException {

    public NoDocumentException(String s) {
        super(s);
    }

}
