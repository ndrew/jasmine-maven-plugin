/*-
 * #%L
 * jasmine-maven-plugin
 * %%
 * Copyright (C) 2010 - 2017 Justin Searls
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.ndrew.jasmine.coffee;

import com.github.ndrew.jasmine.config.JasmineConfiguration;
import com.github.ndrew.jasmine.format.BuildsJavaScriptToWriteFailureHtml;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HandlesRequestsForCoffeeTest {
  private static final String COFFEE = "coffee";

  @InjectMocks
  HandlesRequestsForCoffee subject = new HandlesRequestsForCoffee(null);

  @Mock
  private CoffeeScript coffeeScript = new CoffeeScript();
  @Mock
  private BuildsJavaScriptToWriteFailureHtml buildsJavaScriptToWriteFailureHtml;
  @Mock
  private JasmineConfiguration configuration;

  @Mock
  private Request baseRequest;
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private HttpServletResponse response;
  @Mock
  private Resource resource;

  @Before
  public void stubResourceInputStream() throws IOException {
    when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(COFFEE.getBytes()));
  }

  @Before
  public void defaultCoffeeStubbing() throws IOException {
    when(coffeeScript.compile(COFFEE)).thenReturn("blarg!");
  }

  @Before
  public void defaultCoffeeScriptCompilationConfigurationStubbing() throws IOException {
    when(configuration.isCoffeeScriptCompilationEnabled()).thenReturn(true);
  }

  @Test
  public void setsBaseRequestHandledToTrue() throws IOException {
    subject.handle(baseRequest, response, resource);

    verify(baseRequest).setHandled(true);
  }

  @Test
  public void setsMimeToJavaScript() throws IOException {
    subject.handle(baseRequest, response, resource);

    verify(response).setContentType("text/javascript");
  }

  @Test
  public void setCharacterEncodingToJavaScript() throws IOException {
    subject.handle(baseRequest, response, resource);

    verify(response).setCharacterEncoding("UTF-8");
  }

  @Test
  public void setsResourceLastModifiedOnResponseHeader() throws IOException {
    long expected = 98123l;
    when(resource.lastModified()).thenReturn(expected);

    subject.handle(baseRequest, response, resource);

    verify(response).setDateHeader(HttpHeaders.LAST_MODIFIED, expected);
  }

  @Test
  public void whenCoffeeCompilesThenWriteIt() throws IOException {
    String expected = "javascript";
    when(coffeeScript.compile(COFFEE)).thenReturn(expected);

    subject.handle(baseRequest, response, resource);

    verify(response.getWriter()).write(expected);
    verify(response).setHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(expected.length()));
  }

  @Test
  public void whenCoffeeCompilesHasMultiByteThenWriteIt() throws IOException {
    String expected = "あいうえお.coffee";
    when(coffeeScript.compile(COFFEE)).thenReturn(expected);

    subject.handle(baseRequest, response, resource);

    verify(response.getWriter()).write(expected);
    verify(response).setHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(expected.getBytes("UTF-8").length));
  }

  @Test
  public void whenCoffeeCompilationFailsThenWriteTheErrorOutInItsStead() throws IOException {
    String name = "some-file.coffee";
    String message = "messages";
    String expected = "CoffeeScript Error: failed to compile <code>" + name + "</code>. <br/>Error message:<br/><br/><code>" + message + "</code>";
    when(resource.getName()).thenReturn(name);
    when(coffeeScript.compile(COFFEE)).thenThrow(new RuntimeException(message));
    when(buildsJavaScriptToWriteFailureHtml.build(expected)).thenReturn("win");

    subject.handle(baseRequest, response, resource);

    verify(response.getWriter()).write("win");
  }

  @Test
  public void whenCoffeeRequestWithREQUIRE_JSThenWriteCoffee() throws IOException {
    when(configuration.isCoffeeScriptCompilationEnabled()).thenReturn(false);

    subject.handle(baseRequest, response, resource);

    verify(response.getWriter()).write(COFFEE);
    verify(response).setHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(COFFEE.length()));
  }

}
