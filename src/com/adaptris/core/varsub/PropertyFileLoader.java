package com.adaptris.core.varsub;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class PropertyFileLoader {
  
  public Properties load(String url) throws IOException {
    return this.load(new File(url).toURI().toURL());
  }
  
  public Properties load(URL url) throws IOException {
    InputStream inputStream = null;
    try {
      URLConnection connection = url.openConnection();
      inputStream = connection.getInputStream();
      
      Properties returnedProperties = new Properties();
      returnedProperties.load(inputStream);
      return returnedProperties;
    } finally {
      try {
        inputStream.close();
      } catch (Exception e) {
        // close silently
      }
    }
  }

}
