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
package com.github.ndrew.jasmine.mojo;

import com.github.ndrew.jasmine.model.Reporter;
import com.github.ndrew.jasmine.model.FileSystemReporter;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.EndsWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReporterRetrieverTest {

  @Mock
  ResourceRetriever resourceRetriever;

  @Mock
  MavenProject mavenProject;

  File targetDir = new File(".");

  @Mock
  File standardReporter;

  @Mock
  File junitXmlReporter;

  ReporterRetriever subject;

  @Before
  public void setUp() throws Exception {
    subject = new ReporterRetriever(resourceRetriever);

    when(resourceRetriever.getResourceAsFile("reporter", ReporterRetriever.STANDARD_REPORTER, mavenProject)).thenReturn(standardReporter);
    when(resourceRetriever.getResourceAsFile("reporter", ReporterRetriever.JUNIT_XML_REPORTER, mavenProject)).thenReturn(junitXmlReporter);
  }

  @Test
  public void itShouldRetrieveReporters() throws Exception {
    String customReporterPath = "/foo/bar";
    File customReporter = mock(File.class);
    when(resourceRetriever.getResourceAsFile("reporter", customReporterPath, mavenProject)).thenReturn(customReporter);
    List<Reporter> incomingReporters = Arrays.asList(new Reporter("STANDARD"), new Reporter(customReporterPath));

    List<Reporter> reporters = subject.retrieveReporters(incomingReporters, mavenProject);

    assertThat(reporters, hasSize(incomingReporters.size()));
    assertThat(reporters.get(0).reporterFile, is(standardReporter));
    assertThat(reporters.get(1).reporterFile, is(customReporter));
  }

  @Test
  public void itShouldRetrieveStandardReporterAsDefault() throws Exception {
    List<Reporter> reporters = subject.retrieveReporters(new ArrayList<Reporter>(), mavenProject);

    assertThat(reporters, hasSize(1));
    assertThat(reporters.get(0).reporterFile, is(standardReporter));
  }

  @Test
  public void itShouldRetrieveFileSystemReporters() throws Exception {
    String customReporterPath = "/foo/bar";
    File customReporter = mock(File.class);
    when(resourceRetriever.getResourceAsFile("reporter", customReporterPath, mavenProject)).thenReturn(customReporter);
    List<FileSystemReporter> incomingReporters = Arrays.asList(new FileSystemReporter("TEST-file.xml", "JUNIT_XML"), new FileSystemReporter("TEST-custom.file", customReporterPath));

    List<FileSystemReporter> reporters = subject.retrieveFileSystemReporters(incomingReporters, targetDir, mavenProject);

    assertThat(reporters, hasSize(incomingReporters.size()));
    assertThat(reporters.get(0).reporterFile, is(junitXmlReporter));
    assertThat(reporters.get(0).file.getAbsolutePath(), new EndsWith("TEST-file.xml"));
    assertThat(reporters.get(1).reporterFile, is(customReporter));
    assertThat(reporters.get(1).file.getAbsolutePath(), new EndsWith("TEST-custom.file"));
  }

  @Test
  public void itShouldRetrieveJUnitFileReporterAsDefault() throws Exception {
    List<FileSystemReporter> reporters = subject.retrieveFileSystemReporters(new ArrayList<FileSystemReporter>(), targetDir, mavenProject);

    assertThat(reporters, hasSize(1));
    assertThat(reporters.get(0).reporterFile, is(junitXmlReporter));
    assertThat(reporters.get(0).file.getAbsolutePath(), new EndsWith("TEST-jasmine.xml"));
  }
}
