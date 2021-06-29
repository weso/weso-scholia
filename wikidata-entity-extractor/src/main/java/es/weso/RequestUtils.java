package es.weso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

public class RequestUtils {

    public static String executeRequest(HttpClient httpclient, HttpRequestBase request) throws IOException {
        HttpResponse response = httpclient.execute(request);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            try (InputStream instream = entity.getContent()) {
                return  IOUtils.toString(instream, "UTF-8");
            }
        }
        return null;
    }
}
