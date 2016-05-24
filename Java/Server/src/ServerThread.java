/**
 * Created by Lehyu on 2016/5/23.
 */

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.util.RandomAccess;
import java.util.regex.Pattern;

/**
 * Created by lehyu on 16-4-17.
 */
public class ServerThread implements Runnable {
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    //private final static String ROOT_PATH = "/home/lehyu/timecol";
    private Socket client = null;

    public ServerThread(Socket client) {
        this.client = client;

    }

    @Override
    public void run() {
        execute(client);
    }

    private void execute(Socket client) {
        BufferedReader input = null;
        OutputStream output = null;

        RSACoder coder = new RSACoder();
        try {
            input = new BufferedReader(new InputStreamReader(client.getInputStream(), NetConfig.ENCODE));
            output = client.getOutputStream();
            output.write((coder.getPublicKey()+"\n").getBytes());
            output.write((NetConfig.SEND_KEY_DONE+"\n").getBytes());
            //accept msg
            String encodedPlain = getData(input, NetConfig.SEND_MSG_DONE);
            byte[] msg = new String(coder.decode(encodedPlain)).trim().getBytes();
            System.out.println("msg:"+BaseCoder.parseByte2HexStr(msg));

            String encodeSign = getData(input, NetConfig.SEND_SIGN_DONE);
            byte[] signature = coder.decode(encodeSign);
            System.out.println("signature:"+BaseCoder.parseByte2HexStr(signature));

            //accept cert
            String encodedCert = getData(input, NetConfig.SEND_CERT_DONE);
            byte[] certByte = coder.decode(encodedCert);
            System.out.println("certByte:"+BaseCoder.parseByte2HexStr(certByte));
            //verity
            if(doVerity(msg, signature, certByte)){
                output.write((NetConfig.LEGAL +"\n").getBytes());
            }else{
                output.write((NetConfig.ILLEGAL +"\n").getBytes());
            }
            //send result
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(input, output);
        }
    }

    private void close(BufferedReader input, OutputStream output) {
        try{
            if (null != input){
                input.close();
            }if (null != output){
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean doVerity(byte[] decodeText, byte[] signature, byte[] certByte){
        //send rsa publickey to client
        X509Certificate x509cert = null;
        try {
            x509cert = (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(certByte));
            PublicKey publicKey = x509cert.getPublicKey();
            System.out.println("publickey:"+BaseCoder.parseByte2HexStr(publicKey.getEncoded()));
            byte[] digest = decode(signature, publicKey);

            String msg = new String(decodeText).trim();

            return verity(x509cert, msg.getBytes(), digest);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean verity(X509Certificate certificate, byte[] decodedText, byte[] digest){
        System.out.println("decodedText:"+BaseCoder.parseByte2HexStr(decodedText));
        System.out.println("digest:"+BaseCoder.parseByte2HexStr(digest));
        Signature signature;
        try {
            signature = Signature.getInstance(certificate.getSigAlgName());
            signature.initVerify(certificate);
            signature.update(decodedText);
            return signature.verify(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

    private byte[] decode(byte[] encodedText, PublicKey key){
        try {
            //System.out.println("server decode:"+key.getAlgorithm());
            //System.out.println("server format:"+key.getFormat());
            //Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            Cipher cipher = Cipher.getInstance("RSA/None/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            int length = encodedText.length;
            int offset = 0;
            int blockSize = cipher.getBlockSize();
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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getData(BufferedReader input, String flag) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        int i = 0;
        while (!(line = input.readLine().trim()).equals(flag)){
            sb.append(line);
        }
        return sb.toString();
    }
}