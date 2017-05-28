package de.gurkenlabs.litiengine.newproject.wizards;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class EngineRelease {
  private final String name;
  private final int id;
  private String mainAssetDownloadLink;
  private String mainAssetName;
  private final JsonElement json;

  public EngineRelease(JsonElement json) {
    this.json = json;
    JsonArray assets = json.getAsJsonObject().get("assets").getAsJsonArray();
    this.name = json.getAsJsonObject().get("name").getAsString();
    this.id = json.getAsJsonObject().get("id").getAsInt();
    for (int j = 0; j < assets.size(); j++) {
      JsonElement asset = assets.get(j);
      String assetName = asset.getAsJsonObject().get("name").getAsString();
      if (assetName.endsWith(".zip")) {
        this.mainAssetDownloadLink = asset.getAsJsonObject().get("browser_download_url").getAsString();
        this.mainAssetName = assetName;
        break;
      }
    }
  }

  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }

  public String getMainAssetDownloadLink() {
    return mainAssetDownloadLink;
  }

  public String getMainAssetName() {
    return mainAssetName;
  }

  public JsonElement getJson() {
    return json;
  }
}
