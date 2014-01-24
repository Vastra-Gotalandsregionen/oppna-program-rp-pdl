package se.vgregion.service.xmlsig;


import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

import static org.junit.Assert.assertEquals;

public class ZfpEcb {

    @Test
    public void zfpEcbSpike() throws Exception {
        // dicom -> study-uid
        String original = "sui=1.2.3.45.6789.0&lights=off&pw=yP9a5foBZtU=&un=testUser|20131213134546";
        // ZFP?mode=Inbound#pl=<encypted-playload>
        byte[] enc = original.getBytes("utf-8");
        byte[] encPad = new byte[]{0x07,0x07,0x07,0x07,0x07,0x07,0x07}; //
        byte[] con = new byte[enc.length + encPad.length];
        System.arraycopy(enc, 0, con, 0, enc.length);
        System.arraycopy(encPad, 0, con, enc.length, encPad.length);

        System.out.println(enc.length);
        System.out.println(bytesToHex(enc));

        MessageDigest md = MessageDigest.getInstance("SHA-1");

        byte[] secret = "secretkey1234567".getBytes();

        byte[] hashedSecret = md.digest(secret);
        System.out.println("Secret = " + bytesToHex(hashedSecret));
        byte[] keyBytes = hexStringToByteArray("b5254f86bc85d0a4fdc81f76986b8a7cc29d437016ba6b08");

        byte[] chiperText = encrypt(enc, keyBytes);


        String chipherText = Base64.encodeBase64String(chiperText);

       // assertEquals(
       //         "Z/nOUbe7J8ua2B1IOas6rZAYHxm9RxHl26LUGwBfOEPG93uW4N6VXJTmp/r9lMITkNOuDtD0K9TzCBW9F35p5x6JK+qfyGKoDbZ4hS1Pb1gn6f2qx4LztQ==",
       //         chipherText
       // );

        System.out.println("Encrypted = " + chipherText);

        String plainText = decrypt(chiperText, keyBytes);
        assertEquals(original, plainText);
        System.out.println("Plain text = " + plainText);
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

        return new String(plainText);

    }

}
