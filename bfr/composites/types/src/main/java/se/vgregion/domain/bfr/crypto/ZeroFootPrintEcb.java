package se.vgregion.domain.bfr.crypto;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.domain.decorators.Maybe;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Date;
import java.util.TimeZone;

public class ZeroFootPrintEcb {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroFootPrintEcb.class);

    private static final String KEY_SPEC_TYPE = "DESede";
    private static final String CRYPTO = "DESede/ECB/PKCS7Padding";
    private static final String STUDY_UID = "<study-uid>";
    private static final String USER_NAME = "<user-name>";
    private static final String PASSWORD = "<password>";
    private static final String TIME = "<datetime-created>";
    private static final String PATTERN =  "viewall&sui="+STUDY_UID+"&un="+USER_NAME+"&custom="+USER_NAME+"&pw="+PASSWORD+"|"+TIME;

// Mycket möjlig todo - implementera en huvudstudie med flera komplementstudier. Detta kräver andra parametrar så det
// kan vara enklast att ha två olika patterns beroende på om endast en eller flera studier ska visas. Tomma värden ska
// inte skickas med, d.v.s. hela parametern ska bort om inget värde finns.
//    private static final String PATTERN =  "viewall&primarySui="+STUDY_UID+"&un="+USER_NAME+"&pw="+PASSWORD+"|"+TIME;
/* lägg till sui={studyUid}\{studyUid}\{studyUid} */

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static Maybe<String> getToken(
            String dicomStudyId,
            String username,
            String password,
            String key
        ) {

        String creationDateString = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss", TimeZone.getTimeZone("UTC"));

        // Trim as we only should have two ms digits in datetime string.

        String plainText = PATTERN.
                replace(STUDY_UID, dicomStudyId).
                replace(USER_NAME, username).
                replace(PASSWORD, password).
                replace(TIME, creationDateString);

        LOGGER.info("Plaintext token: " + plainText);

        try {
            byte[] encrypted = encrypt(
                    plainText.getBytes("UTF-8"),
                    hexStringToByteArray(key)
            );

            String tokenB64 = Base64.encodeBase64String(encrypted);
            URLCodec codec = new URLCodec("UTF-8");
            String tokenB64Url = codec.encode(tokenB64);
            return Maybe.some(tokenB64Url);

        } catch (Exception e) {
            LOGGER.error("Error when processing token for study uid {}", dicomStudyId, e);
        }

        return Maybe.none();
    }

    static byte[] encrypt(byte[] message, byte[] keyBytes) throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException,
            NoSuchProviderException {
        Cipher cipher = getCipher(keyBytes, Cipher.ENCRYPT_MODE);
        return cipher.doFinal(message);
    }

    private static Cipher getCipher(byte[] keyBytes, int mode) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            NoSuchProviderException {
        SecretKey key = new SecretKeySpec(keyBytes, KEY_SPEC_TYPE);
        Cipher cipher = Cipher.getInstance(CRYPTO, "BC");
        cipher.init(mode, key);
        return cipher;
    }
}
