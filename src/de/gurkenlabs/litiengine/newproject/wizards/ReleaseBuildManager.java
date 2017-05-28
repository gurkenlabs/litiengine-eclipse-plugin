package de.gurkenlabs.litiengine.newproject.wizards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ReleaseBuildManager {
  private static final String RELEASE_URL = "https://api.github.com/repos/gurkenlabs/litiengine/releases";

  public static List<EngineRelease> getLitiEngineReleases() {
    ArrayList<EngineRelease> releases = new ArrayList<>();
    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(RELEASE_URL);
    request.addHeader("content-type", "application/json");
    HttpResponse result;
    try {
      result = httpClient.execute(request);

      String json = EntityUtils.toString(result.getEntity(), "UTF-8");
      JsonParser parser = new JsonParser();
      JsonArray o = parser.parse(json).getAsJsonArray();
      for (int i = 0; i < o.size(); i++) {
        JsonElement ele = o.get(i);
        releases.add(new EngineRelease(ele));
      }
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return releases;
  }
}
