package se.vgregion.service.xmlsig;


import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.security.MessageDigest;

import static org.junit.Assert.assertEquals;

public class ZfpEcb {

    @Test
    public void zfpEcbSpike() throws Exception {
        // dicom -> study-uid

         String id = "1.2.752.30.104.1144953.9231176.20130413113626";
        //String id = "1.2.752.30.104.9910751.7741310.20081111190143";


        String time = "20140206160000";
        String user = "TestUser";
        String password = "<Omitted>";
        String original = "sui="+id+"&lights=off&un="+user+"&pw="+password+"|"+time;
        // ZFP?mode=Inbound#pl=<encypted-playload>
        byte[] enc = original.getBytes("utf-8");

        System.out.println(enc.length);
        System.out.println(bytesToHex(enc));

        MessageDigest md = MessageDigest.getInstance("SHA-1");

        byte[] secret = "secretkey1234567".getBytes();

//        byte[] hashedSecret = md.digest(secret);
//        System.out.println("Secret = " + bytesToHex(hashedSecret));

        byte[] keyBytes = hexStringToByteArray("<Omitted>");

        byte[] chiperText = encrypt(enc, keyBytes);


        String chipherTextString = Base64.encodeBase64String(chiperText);
        String urlChiperText = URLEncoder.encode(chipherTextString, "UTF-8");

       // assertEquals(
       //         "Z/nOUbe7J8ua2B1IOas6rZAYHxm9RxHl26LUGwBfOEPG93uW4N6VXJTmp/r9lMITkNOuDtD0K9TzCBW9F35p5x6JK+qfyGKoDbZ4hS1Pb1gn6f2qx4LztQ==",
       //         chipherText
       // );

        System.out.println("Encrypted = " + chipherTextString);
        System.out.println("Encrypted URL Encoded = " + urlChiperText);
        String plainText = decrypt(chiperText, keyBytes);
        assertEquals(original, plainText);
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
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

    public byte[] encrypt(byte[] message, byte[] keyBytes) throws Exception
    {
        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(message);
    }

    public String decrypt(byte[] message, byte[] keyBytes) throws Exception
    {
        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final Cipher decipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        decipher.init(Cipher.DECRYPT_MODE, key);

        //final byte[] encData = new sun.misc.BASE64Decoder().decodeBuffer(message);
        final byte[] plainText = decipher.doFinal(message);

        return new String(plainText, "UTF-8");

    }

}
