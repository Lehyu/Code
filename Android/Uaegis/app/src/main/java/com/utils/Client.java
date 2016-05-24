package com.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

/**
 * Created by Lehyu on 2016/5/16.
 */
public class Client {
    private static Socket server;
    private static OutputStream output;
    private static InputStreamReader input;

    private static void connectToServer() throws IOException {
        server = new Socket(NetConfig.IP_ADDRESS, NetConfig.PORT);
        output = server.getOutputStream();
        input = new InputStreamReader(server.getInputStream(), NetConfig.ENCODE);
    }

    private static void close() throws IOException {
        server.close();
        input.close();
        output.close();
    }

    public static String doVerity(byte[] msg, byte[] signature, byte[] certByte){
        BufferedReader reader = null;
        try {


            connectToServer();
            reader =  new BufferedReader(new InputStreamReader(server.getInputStream(), NetConfig.ENCODE));
            StringBuilder sb = new StringBuilder();
            String line;
            while (!(line=reader.readLine().trim()).equals(NetConfig.SEND_KEY_DONE)){
                Log.v("accept:", line);
                sb.append(line);
            }
            RSACoder coder = new RSACoder(sb.toString());
            sb.delete(0, sb.length());

            X509Certificate x509cert = (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(certByte));
            PublicKey publicKey = x509cert.getPublicKey();
            Log.v("x509 publickey:", coder.parseByte2HexStr(publicKey.getEncoded()));

            Log.v("msg byte size:", "" + msg.length);
            Log.v("digest size:", ""+ signature.length);
            Log.v("msg", RSACoder.parseByte2HexStr(msg));
            Log.v("signature", RSACoder.parseByte2HexStr(signature));
            Log.v("certbyte", RSACoder.parseByte2HexStr(certByte));

            byte[] digest = decode(signature, x509cert.getPublicKey());
            Log.v("digest own", RSACoder.parseByte2HexStr(digest));

            Log.v("verity", String.valueOf(verity(x509cert, msg, digest)));

            String encodedMsg = coder.encrypt(msg);
            output.write((encodedMsg + "\n").getBytes());
            output.write((NetConfig.SEND_MSG_DONE + "\n").getBytes());

            String encodedSign = coder.encrypt(signature);
            output.write((encodedSign + "\n").getBytes());
            output.write((NetConfig.SEND_SIGN_DONE + "\n").getBytes());

            String encodedCert = coder.encrypt(certByte);
            output.write((encodedCert + "\n").getBytes());
            output.write((NetConfig.SEND_CERT_DONE+"\n").getBytes());

            String result = reader.readLine();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != reader){
                    reader.close();
                }
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static boolean verity(X509Certificate certificate, byte[] decodedText, byte[] digest) {
        Log.v("verity decodeText", RSACoder.parseByte2HexStr(decodedText));
        Log.v("verity digest", RSACoder.parseByte2HexStr(digest));
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
    private static byte[] decode(byte[] encodedText, PublicKey key){
        try {
            //Cipher cipher = Cipher.getInstance(key.getAlgorithm());
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
}
