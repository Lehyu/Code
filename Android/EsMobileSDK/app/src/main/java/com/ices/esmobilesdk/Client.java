package com.ices.esmobilesdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

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

    public String CooperateWithServer(String msg){
        BufferedReader reader = null;
        try {
            connectToServer();
            output.write((msg + "\n").getBytes(NetConfig.ENCODE));
            reader = new BufferedReader(input);

            String result = reader.readLine();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
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
}
