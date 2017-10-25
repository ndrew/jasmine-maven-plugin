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

import com.github.ndrew.jasmine.model.ScriptSearch;
import com.github.ndrew.jasmine.io.ScansDirectory;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FindsScriptLocationsInDirectoryTest {

  private static final List<String> INCLUDES = asList("So in");
  private static final List<String> EXCLUDES = asList("So out");
  private static final String FILE_LOCATION = "blah/a.js";

  @Mock
  private ScansDirectory scansDirectory;
  @Mock
  private ConvertsFileToUriString convertsFileToUriString;

  @Spy
  private File directory = new File("Not quite a real directory");

  @InjectMocks
  private FindsScriptLocationsInDirectory subject;

  @Before
  public void directoryStubbing() {
    when(directory.canRead()).thenReturn(true);
  }

  @Test
  public void returnsEmptyWhenDirectoryDoesNotExist() throws IOException {
    List<String> result = subject.find(new ScriptSearch(new File("No way does this file exist"), null, null));

    assertThat(result, is(Collections.emptyList()));
  }

  @Test
  public void addsScriptLocationScannerFinds() throws IOException {
    String expected = "full blown file";
    when(scansDirectory.scan(directory, INCLUDES, EXCLUDES)).thenReturn(asList(FILE_LOCATION));
    when(convertsFileToUriString.convert(new File(directory, FILE_LOCATION))).thenReturn(expected);

    List<String> result = subject.find(new ScriptSearch(directory, INCLUDES, EXCLUDES));

    assertThat(result, hasItem(expected));
  }

}
