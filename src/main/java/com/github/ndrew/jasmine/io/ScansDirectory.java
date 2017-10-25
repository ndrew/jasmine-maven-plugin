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
package com.github.ndrew.jasmine.io;

import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class ScansDirectory {

  public final static List<String> DEFAULT_INCLUDES = asList("**" + File.separator + "*.js", "**" + File.separator + "*.coffee");

  private final DirectoryScanner directoryScanner = new DirectoryScanner();

  public List<String> scan(File directory, List<String> includes, List<String> excludes) {
    Set<String> set = new LinkedHashSet<String>();
    for (String include : includes) {
      set.addAll(performScan(directory, include, excludes));
    }
    return new ArrayList<String>(set);
  }

  private List<String> performScan(File directory, String include, List<String> excludes) {
    directoryScanner.setBasedir(directory);
    directoryScanner.setIncludes(new String[]{include});
    directoryScanner.setExcludes(excludes.toArray(new String[]{}));
    directoryScanner.addDefaultExcludes();
    directoryScanner.scan();
    ArrayList<String> result = new ArrayList<String>(asList(directoryScanner.getIncludedFiles()));
    Collections.sort(result);
    return result;
  }

}
