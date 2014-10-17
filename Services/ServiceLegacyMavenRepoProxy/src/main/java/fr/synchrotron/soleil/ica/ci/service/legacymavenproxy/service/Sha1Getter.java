package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.service;

/**
 * Gregory Boissinot
 */
public class Sha1Getter {

    public String getSha1(byte[] binaryData) {
//        if (binaryData.length != 16 && binaryData.length != 20) {
//            int bitLength = binaryData.length * 8;
//            throw new IllegalArgumentException("Unrecognised length for binary data: " + bitLength + " bits");
//        }

        String retValue = "";

        for (int i = 0; i < binaryData.length; i++) {
            String t = Integer.toHexString(binaryData[i] & 0xff);

            if (t.length() == 1) {
                retValue += ("0" + t);
            } else {
                retValue += t;
            }
        }

        return retValue.trim();
    }
}
