
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;

/**
 * Created by Lehyu on 2016/5/23.
 */
public class RSACoder extends BaseCoder {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String CIPHER_ALG = "RSA/ECB/NoPadding";
    public static final String PUBLIC_KEY = "RSAPublicKey";
    public static final String PRIVATE_KEY = "RSAPrivateKey";
    public static final String FILENAME = "key.txt";
    private JSONObject keyJson;
    private Cipher cipher;

    public RSACoder() {
        try {
            keyJson = initKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        PublicKey publicKey =  keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        JSONObject keyJson = new JSONObject();
        keyJson.put(PUBLIC_KEY, publicKey);
        keyJson.put(PRIVATE_KEY, privateKey);
        return keyJson;
    }

    private JSONObject initKeyFromFile() throws Exception {
        JSONObject keyJson = FileUtils.readJson(new File(FILENAME));
        if (keyJson == null){
            keyJson = initKey();
            //System.out.println("keyJson is null");
        }
        return keyJson;
    }

    public String getPublicKey(){
        try {
            PublicKey key = (PublicKey) keyJson.get(PUBLIC_KEY);
            byte[] keyByte = key.getEncoded();
            return parseByte2HexStr(keyByte);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decode(String encodedText){
        byte[] bytes = parseHexStr2Byte(encodedText);
        return decode(bytes);
    }

    public byte[] decode(byte[] encodedText){
        try {
            PrivateKey key = (PrivateKey) keyJson.get(PRIVATE_KEY);
            cipher = Cipher.getInstance(CIPHER_ALG);
            cipher.init(Cipher.DECRYPT_MODE, key);
            int length = encodedText.length;
            int offset = 0;
            int blockSize = 128;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int i = 0;
            while (length - offset > 0){
                byte[] cache ;
                if (length - offset > blockSize){
                    cache = cipher.doFinal(encodedText, offset, blockSize);
                }else {
                    cache = cipher.doFinal(encodedText, offset, length - offset);

                }
                out.write(cache, 0, cache.length);
                i++;
                offset = i*blockSize;
            }
            return out.toByteArray();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
