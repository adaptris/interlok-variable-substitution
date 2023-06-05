package com.adaptris.core.varsub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.adaptris.core.Adapter;
import com.adaptris.core.CoreException;
import com.adaptris.interlok.junit.scaffolding.BaseCase;
import com.adaptris.util.GuidGenerator;
import com.adaptris.util.URLString;

public class XStreamMarshallerTest extends BaseCase {

  private static final String PROPS_VARIABLES_ADAPTER = "varsub.variables.adapter.xml";
  private static final String SAMPLE_SUBSTITUTION_PROPERTIES = "varsub.variables.properties";

  private File variablesAdapterFile;

  @BeforeEach
  public void setUp() throws Exception {
    variablesAdapterFile = new File(PROPERTIES.getProperty(PROPS_VARIABLES_ADAPTER));
  }

  private XStreamMarshaller createMarshaller() throws Exception {
    XStreamMarshaller marshaller = new XStreamMarshaller();
    marshaller.addVariablePropertiesUrls(PROPERTIES.getProperty(SAMPLE_SUBSTITUTION_PROPERTIES));
    return marshaller;
  }

  @Test
  public void testUnmarshal_String() throws Exception {
    Adapter adapter = (Adapter) createMarshaller().withUseHostname(true)
        .unmarshal(IOUtils.toString(variablesAdapterFile.toURI().toURL(), Charset.defaultCharset()));
    doStandardAssertions(adapter);
  }

  @Test
  public void testUnmarshal_File() throws Exception {
    Adapter adapter = (Adapter) createMarshaller().unmarshal(variablesAdapterFile);
    doStandardAssertions(adapter);
  }

  @Test
  public void testUnmarshal_File_NonExistent() throws Exception {
    try {
      createMarshaller().unmarshal(new File(new GuidGenerator().safeUUID()));
      fail();
    } catch (CoreException expected) {
    }
  }

  @Test
  public void testUnmarshal_Reader() throws Exception {
    Adapter adapter = (Adapter) createMarshaller().unmarshal(new FileReader(variablesAdapterFile));
    doStandardAssertions(adapter);
  }

  @Test
  public void testUnmarshal_Reader_Failure() throws Exception {
    try {
      createMarshaller().unmarshal(new Reader() {

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
          throw new IOException();
        }

        @Override
        public void close() throws IOException {
        }

      });
      fail();
    } catch (CoreException expected) {
    }
  }

  @Test
  public void testUnmarshal_InputStream() throws Exception {
    Adapter adapter = (Adapter) createMarshaller().unmarshal(new FileInputStream(variablesAdapterFile));
    doStandardAssertions(adapter);
  }

  @Test
  public void testUnmarshal_InputStream_Failure() throws Exception {
    try {
      createMarshaller().unmarshal(new InputStream() {

        @Override
        public int read() throws IOException {
          throw new IOException();
        }

      });
      fail();
    } catch (CoreException expected) {
    }
  }

  @Test
  public void testUnmarshal_URLString() throws Exception {
    Adapter adapter = (Adapter) createMarshaller().unmarshal(new URLString(variablesAdapterFile.toURI().toURL()));
    doStandardAssertions(adapter);
  }

  @Test
  public void testUnmarshal_URLString_NonExistent() throws Exception {
    try {
      createMarshaller().unmarshal(new URLString(new File(new GuidGenerator().safeUUID()).toURI().toURL()));
      fail();
    } catch (CoreException expected) {
    }
  }

  @Test
  public void testUnmarshal_URL() throws Exception {
    Adapter adapter = (Adapter) createMarshaller().unmarshal(variablesAdapterFile.toURI().toURL());
    doStandardAssertions(adapter);
  }

  @Test
  public void testUnmarshal_URL_NonExistent() throws Exception {
    try {
      createMarshaller().unmarshal(new File(new GuidGenerator().safeUUID()).toURI().toURL());
      fail();
    } catch (CoreException expected) {
    }
  }

  @Test
  public void testUnmarshal_Null() throws Exception {
    try {
      createMarshaller().unmarshal((String) null);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void testUnmarshal_NoSubstitution() throws Exception {
    String xml = IOUtils.toString(variablesAdapterFile.toURI().toURL(), Charset.defaultCharset());
    Adapter adapter = (Adapter) new XStreamMarshaller().unmarshal(xml);
    assertEquals("${adapter.id}", adapter.getUniqueId());
  }

  @Test
  public void testUnmarshal_WithVariablePrefixSuffix() throws Exception {
    XStreamMarshaller marshaller = createMarshaller();
    marshaller.setVariablePrefix(Constants.DEFAULT_VARIABLE_PREFIX);
    marshaller.setVariablePostfix(Constants.DEFAULT_VARIABLE_POSTFIX);
    Adapter adapter = (Adapter) marshaller.unmarshal(IOUtils.toString(variablesAdapterFile.toURI().toURL(), Charset.defaultCharset()));
    doStandardAssertions(adapter);
  }

  @Test
  public void testUnmarshal_WithLogging() throws Exception {
    XStreamMarshaller marshaller = createMarshaller();
    marshaller.setSubstitutionType(VariableSubstitutionType.SIMPLE_WITH_LOGGING);
    Adapter adapter = (Adapter) marshaller.unmarshal(IOUtils.toString(variablesAdapterFile.toURI().toURL(), Charset.defaultCharset()));
    doStandardAssertions(adapter);
  }

  @Test
  public void testUnmarshal_SubstitutionType() throws Exception {
    XStreamMarshaller marshaller = createMarshaller();
    marshaller.setSubstitutionType(VariableSubstitutionType.SIMPLE);
    Adapter adapter = (Adapter) marshaller.unmarshal(variablesAdapterFile);
    doStandardAssertions(adapter);
  }

  private void doStandardAssertions(Adapter adapter) {
    assertEquals("MyAdapterID", adapter.getUniqueId());
    assertEquals("MyChannelID", adapter.getChannelList().get(0).getUniqueId());
    assertEquals("AnotherChannelId", adapter.getChannelList().get(1).getUniqueId());
    assertEquals("MyWorkflowID1", adapter.getChannelList().get(0).getWorkflowList().get(0).getUniqueId());
    assertEquals("MyWorkflowID2", adapter.getChannelList().get(0).getWorkflowList().get(1).getUniqueId());
    assertEquals("MyWorkflowID1", adapter.getChannelList().get(1).getWorkflowList().get(0).getUniqueId());
    assertEquals("MyWorkflowID2", adapter.getChannelList().get(1).getWorkflowList().get(1).getUniqueId());
  }

}
