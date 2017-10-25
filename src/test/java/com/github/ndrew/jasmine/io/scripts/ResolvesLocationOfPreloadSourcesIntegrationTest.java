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
package com.github.ndrew.jasmine.io.scripts;

import com.github.ndrew.jasmine.io.CreatesTempDirectories;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;


public class ResolvesLocationOfPreloadSourcesIntegrationTest {

  private static final String SPEC = "spec";
  private static final String SOURCE = "source";
  private static final String SEPARATED_DIR = "separatedDir";

  private ResolvesLocationOfPreloadSources subject = new ResolvesLocationOfPreloadSources();

  private CreatesTempDirectories createsTempDirectories = new CreatesTempDirectories();
  private File sourceDir = createsTempDirectories.create(SOURCE);
  private File specDir = createsTempDirectories.create(SPEC);
  private File separatedDir = createsTempDirectories.create(SEPARATED_DIR);

  @After
  public void deleteTempDirs() {
    sourceDir.delete();
    specDir.delete();
    separatedDir.delete();
  }

  @Test
  public void returnsListWhenSourcesIsNull() {
    List<String> result = subject.resolve(null, sourceDir, specDir);

    assertThat(result, is(not(nullValue())));
  }


  @Test
  public void loadsSourceFileWhenItExistsUnderSourceAndSpec() throws IOException {
    String expected = "panda";
    new File(sourceDir, expected).createNewFile();
    new File(specDir, expected).createNewFile();
    List<String> preloadSources = asList(expected);

    List<String> result = subject.resolve(preloadSources, sourceDir, specDir);


    assertThat(result.size(), is(1));
    assertThat(result.get(0), containsString(SOURCE));
    assertThat(result.get(0), containsString(expected));
  }

  @Test
  public void loadsSpecFileWhenItExistsUnderSpec() throws IOException {
    String expected = "panda";
    new File(specDir, expected).createNewFile();
    List<String> preloadSources = asList(expected);

    List<String> result = subject.resolve(preloadSources, sourceDir, specDir);


    assertThat(result.size(), is(1));
    assertThat(result.get(0), containsString(SPEC));
    assertThat(result.get(0), containsString(expected));
  }

  @Test
  public void loadsExistentFileWithURIIfItIsNotInEitherSourceAndSpecFolders() throws Exception {
    String expected = "panda";
    new File(separatedDir, expected).createNewFile();
    List<String> preloadSources = asList(separatedDir.getAbsolutePath() + File.separator + expected);

    List<String> result = subject.resolve(preloadSources, sourceDir, specDir);

    assertThat(result.size(), is(1));
    assertThat(result.get(0), containsString("file:/"));
  }

  @Test
  public void printsAsIsWhenItDoesNotExist() {
    String expected = "telnet://woahItsTelnet";
    List<String> preloadSources = asList(expected);

    List<String> result = subject.resolve(preloadSources, sourceDir, specDir);

    assertThat(result.size(), is(1));
    assertThat(result.get(0), is(expected));
  }
//
}
