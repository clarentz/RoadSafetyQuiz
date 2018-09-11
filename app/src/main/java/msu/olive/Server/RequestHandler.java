package msu.olive.Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    public String sendPostRequest(String URL, HashMap<String,String> data){
        StringBuilder sb = new StringBuilder();
        try {
            java.net.URL url=new URL(URL);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream outputStream=connection.getOutputStream();
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            writer.write(getPostDataString(data));
            writer.flush();
            writer.close();
            outputStream.close();
            int requestcode=connection.getResponseCode();
            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));

            sb=new StringBuilder();
            String reponse;
            while ((reponse=reader.readLine())!=null){
                sb.append(reponse);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String getPostDataString(HashMap<String, String> data) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
