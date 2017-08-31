package com.adaptris.core.varsub;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import com.adaptris.util.URLHelper;
import com.adaptris.util.URLString;

/**
 * <p>
 * Will load a properties file from either the String or URL path.
 * </p>
 * @author amcgrath
 *
 */
class PropertyFileLoader {
  
  public Properties load(String url) throws IOException {
    return load(new URLString(url));
  }
  
  public Properties load(URL url) throws IOException {
    return load(new URLString(url));
  }

  private Properties load(URLString loc) throws IOException {
    Properties result = new Properties();
    try (InputStream inputStream = URLHelper.connect(loc)) {
      if (inputStream == null){
        throw new FileNotFoundException(loc.toString());
      }
      result.load(inputStream);
    }
    return result;
  }

  public static Properties getEnvironment() {
    Properties result = new Properties();
    Map<String, String> env = System.getenv();
    for (String envName : env.keySet()) {
      result.put(envName, env.get(envName));
    }
    return result;
  }
}
