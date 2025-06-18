package com.adaptris.core.varsub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

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
  public void testDoServiceWithLogMasking() throws IOException, CoreException, ParserConfigurationException, SAXException, URISyntaxException {
    VariableSubstitutionService service = new VariableSubstitutionService();
    service.setInput(new StringPayloadDataInputParameter());
    service.setOutput(new StringPayloadDataOutputParameter());
    service.setVariables(new ConstantDataInputParameter(String.format("var=variable\n%s=%s", LogMasking.LOG_MASKING_CONFIG_KEY, "var")));

    String input = "some text with some ${var} key to replace";
    AdaptrisMessage msg = DefaultMessageFactory.getDefaultInstance().newMessage(input);

    service = spy(service);

    AtomicReference<Processor> processor = new AtomicReference<>();
    AtomicReference<VariableExpander> expander = new AtomicReference<>();
    when(service.buildProcessor(any(Properties.class))).thenAnswer((props) -> {
      processor.set(spy(new Processor(props.getArgument(0))));
      when(processor.get().buildVariableExpander(any(), any())).thenAnswer((args) -> {
        expander.set(spy(new VariableExpander("{", "}")));
        return expander.get();
      });
      return processor.get();
    });

    LifecycleHelper.initAndStart(service);
    service.doService(msg);
    LifecycleHelper.close(service);

    verify(expander.get(), times(1)).doLog(anyString(), eq(LogMasking.DEFAULT_LOG_MASK));
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
