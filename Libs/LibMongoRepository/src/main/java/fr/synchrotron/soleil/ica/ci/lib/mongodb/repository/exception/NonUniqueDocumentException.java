package fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.exception;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBException;

/**
 * @author Gregory Boissinot
 */
public class NonUniqueDocumentException extends MongoDBException {

    public NonUniqueDocumentException(String s) {
        super(s);
    }

}
