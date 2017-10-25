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
package com.github.ndrew.jasmine.runner;

import com.github.ndrew.jasmine.mojo.AbstractJasmineMojo;
import com.github.ndrew.jasmine.io.scripts.ScriptResolver;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpecRunnerHtmlGeneratorFactoryTest {

  private SpecRunnerHtmlGeneratorFactory specRunnerHtmlGeneratorFactory;

  @Before
  public void init() {
    specRunnerHtmlGeneratorFactory = new SpecRunnerHtmlGeneratorFactory();
  }

  private HtmlGeneratorConfiguration setupWithTemplate(SpecRunnerTemplate o) {
    HtmlGeneratorConfiguration generatorConfiguration = mock(HtmlGeneratorConfiguration.class);
    when(generatorConfiguration.getSpecRunnerTemplate()).thenReturn(o);
    return generatorConfiguration;
  }

  @Test
  public void shouldReturnDefaultImplementationWhenCalledWithDefault() throws Exception {
    assertThat(specRunnerHtmlGeneratorFactory.createHtmlGenerator(setupWithTemplate(SpecRunnerTemplate.DEFAULT)), instanceOf(DefaultSpecRunnerHtmlGenerator.class));
  }

  @Test
  public void shouldCreateHtmlGeneratorWhenPassedValidInput() {
    AbstractJasmineMojo mock = mock(AbstractJasmineMojo.class);
    when(mock.getSpecRunnerTemplate()).thenReturn(SpecRunnerTemplate.DEFAULT);
    assertThat(specRunnerHtmlGeneratorFactory.create(ReporterType.HtmlReporter, mock, mock(ScriptResolver.class)), instanceOf(DefaultSpecRunnerHtmlGenerator.class));
  }

  @Test
  public void shouldWrapIOException() throws IOException {
    AbstractJasmineMojo mock = mock(AbstractJasmineMojo.class);
    when(mock.getSpecRunnerTemplate()).thenReturn(SpecRunnerTemplate.DEFAULT);
    ScriptResolver mock1 = mock(ScriptResolver.class);
    // doThrow(new IOException("Foo")).when(mock1).resolveScripts();
    try {
      specRunnerHtmlGeneratorFactory.create(ReporterType.HtmlReporter, mock, mock1);
    } catch (InstantiationError e) {
      assertThat(e.getMessage(), is("Foo"));
    }

  }
}
