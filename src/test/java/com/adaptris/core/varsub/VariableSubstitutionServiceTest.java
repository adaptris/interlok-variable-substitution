package com.adaptris.core.varsub;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreException;
import com.adaptris.core.DefaultMessageFactory;
import com.adaptris.core.common.ConstantDataInputParameter;
import com.adaptris.core.common.StringPayloadDataInputParameter;
import com.adaptris.core.common.StringPayloadDataOutputParameter;
import com.adaptris.core.util.LifecycleHelper;

public class VariableSubstitutionServiceTest {

  @Test
  public void testDoService() throws IOException, CoreException, ParserConfigurationException, SAXException, URISyntaxException {
    VariableSubstitutionService service = new VariableSubstitutionService();
    service.setInput(new StringPayloadDataInputParameter());
    service.setOutput(new StringPayloadDataOutputParameter());
    service.setVariables(new ConstantDataInputParameter("var=variable"));

    String input = "some text with some ${var} key to replace";
    AdaptrisMessage msg = DefaultMessageFactory.getDefaultInstance().newMessage(input);

    LifecycleHelper.initAndStart(service);
    service.doService(msg);
    LifecycleHelper.close(service);

    assertEquals("some text with some variable key to replace", msg.getContent());
  }

  @Test
  public void testDoServiceTwiseSameKey()
      throws IOException, CoreException, ParserConfigurationException, SAXException, URISyntaxException {
    VariableSubstitutionService service = new VariableSubstitutionService();
    service.setInput(new StringPayloadDataInputParameter());
    service.setOutput(new StringPayloadDataOutputParameter());
    service.setVariables(new ConstantDataInputParameter("var=variable\nvar=var"));

    String input = "some text with some ${var} key to replace";
    AdaptrisMessage msg = DefaultMessageFactory.getDefaultInstance().newMessage(input);

    LifecycleHelper.initAndStart(service);
    service.doService(msg);
    LifecycleHelper.close(service);

    assertEquals("some text with some var key to replace", msg.getContent());
  }

}
