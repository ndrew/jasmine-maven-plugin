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

import com.github.ndrew.jasmine.io.scripts.BasicScriptResolver;
import com.github.ndrew.jasmine.io.scripts.ContextPathScriptResolver;
import com.github.ndrew.jasmine.io.scripts.ScriptResolver;
import com.github.ndrew.jasmine.config.JasmineConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;

public class CreatesRunner {

  private final JasmineConfiguration config;

  private final Log log;
  private final String runnerFileName;
  private final ReporterType reporterType;

  public CreatesRunner(JasmineConfiguration config, Log log, String runnerFileName, ReporterType reporterType) {
    this.config = config;
    this.runnerFileName = runnerFileName;
    this.reporterType = reporterType;
    this.log = log;
  }

  public String getRunnerFile() {
    return this.runnerFileName;
  }

  public void create() throws IOException {
    File runnerDestination = new File(this.config.getJasmineTargetDir(), this.runnerFileName);
    ScriptResolver resolver = new BasicScriptResolver(
      config.getBasedir(),
      config.getSources(),
      config.getSpecs(),
      config.getPreloadSources());
    resolver = new ContextPathScriptResolver(
      resolver,
      config.getSrcDirectoryName(),
      config.getSpecDirectoryName());

    SpecRunnerHtmlGenerator generator = new SpecRunnerHtmlGeneratorFactory().create(this.reporterType, this.config, resolver);

    String newRunnerHtml = generator.generate();
    if (this.newRunnerDiffersFromOldRunner(runnerDestination, newRunnerHtml)) {
      this.saveRunner(runnerDestination, newRunnerHtml);
    } else {
      this.log.info("Skipping spec runner generation, because an identical spec runner already exists.");
    }
  }

  private String existingRunner(File destination) throws IOException {
    String existingRunner = null;
    try {
      if (destination.exists()) {
        existingRunner = FileUtils.readFileToString(destination);
      }
    } catch (IOException e) {
      this.log.warn("An error occurred while trying to open an existing manual spec runner. Continuing.");
    }
    return existingRunner;
  }

  private boolean newRunnerDiffersFromOldRunner(File runnerDestination, String newRunner) throws IOException {
    return !StringUtils.equals(newRunner, this.existingRunner(runnerDestination));
  }

  private void saveRunner(File runnerDestination, String newRunner) throws IOException {
    FileUtils.writeStringToFile(runnerDestination, newRunner, this.config.getSourceEncoding());
  }
}
