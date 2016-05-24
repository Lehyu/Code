import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

/**
 * Created by Lehyu on 2016/5/23.
 */
public class FileUtils {
    public static JSONObject readJson(File file) {
        BufferedReader reader = null;
        try {
            if (!file.exists()) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
            StringBuffer sb = new StringBuffer();
            String line;
            while (((line = reader.readLine()) != null)) {
                sb.append(line);
            }

            JSONObject info = new JSONObject(sb.toString().trim());
            return info;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void saveJson(File file, JSONObject json){
        OutputStreamWriter output = null;
        try {
            output = new OutputStreamWriter(new FileOutputStream(file),"GBK");
            output.write(json.toString());
            output.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (output != null){
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
