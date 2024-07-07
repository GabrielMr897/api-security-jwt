package com.api.security.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

public class ReadJsonFileToObject {
  
  public JSONObject read() throws IOException {
    String file = "src/main/resources/open-api/response.json";
    String content = new String(Files.readAllBytes(Paths.get(file)));
    return new JSONObject(content);
  }
}