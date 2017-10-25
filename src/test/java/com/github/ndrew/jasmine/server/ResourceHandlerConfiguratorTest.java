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
package com.github.ndrew.jasmine.server;

import com.github.ndrew.jasmine.config.JasmineConfiguration;
import com.github.ndrew.jasmine.io.RelativizesFilePaths;
import com.github.ndrew.jasmine.model.ScriptSearch;
import com.github.ndrew.jasmine.mojo.Context;
import com.github.ndrew.jasmine.runner.CreatesRunner;
import com.github.ndrew.jasmine.thirdpartylibs.ProjectClassLoaderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceHandlerConfiguratorTest {

  private static final String WELCOME_FILE = "welcomefile";
  private static final String SOURCE_DIRECTORY = "sourcedir";
  private static final String SPEC_DIRECTORY = "specdir";
  private static final String BASE_DIRECTORY = "basedir";
  private static final String SOURCE_CONTEXT_ROOT = "sourceroot";
  private static final String SPEC_CONTEXT_ROOT = "specroot";

  private ResourceHandlerConfigurator configurator;

  @Mock
  private JasmineConfiguration configuration;

  @Mock
  private RelativizesFilePaths relativizesFilePaths;

  @Mock
  private File sourceDirectory;

  @Mock
  private File specDirectory;

  @Mock
  private File baseDirectory;

  @Mock
  private ScriptSearch sources;

  @Mock
  private ScriptSearch specs;

  @Mock
  private CreatesRunner createsRunner;

  @Mock
  private Context contextA;

  @Mock
  private Context contextB;

  private List<Context> contexts;

  @Before
  public void before() {
    contexts = Arrays.asList(contextA, contextB);
    this.configurator = new ResourceHandlerConfigurator(
      configuration,
      relativizesFilePaths,
      createsRunner);
  }

  @Test
  public void testCreateHandler() throws IOException {
    when(createsRunner.getRunnerFile()).thenReturn(WELCOME_FILE);

    when(sourceDirectory.getCanonicalPath()).thenReturn(SOURCE_DIRECTORY);
    when(specDirectory.getCanonicalPath()).thenReturn(SPEC_DIRECTORY);
    when(baseDirectory.getCanonicalPath()).thenReturn(BASE_DIRECTORY);

    when(configuration.getBasedir()).thenReturn(baseDirectory);
    when(configuration.getProjectClassLoader()).thenReturn(new ProjectClassLoaderFactory().create());

    when(contextA.getContextRoot()).thenReturn(SOURCE_CONTEXT_ROOT);
    when(contextA.getDirectory()).thenReturn(sourceDirectory);

    when(contextB.getContextRoot()).thenReturn(SPEC_CONTEXT_ROOT);
    when(contextB.getDirectory()).thenReturn(specDirectory);

    when(configuration.getContexts()).thenReturn(contexts);

    this.configurator.createHandler();
  }

}
