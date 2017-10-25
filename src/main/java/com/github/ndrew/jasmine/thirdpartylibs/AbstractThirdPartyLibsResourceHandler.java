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
package com.github.ndrew.jasmine.thirdpartylibs;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public abstract class AbstractThirdPartyLibsResourceHandler extends ResourceHandler {

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    if (baseRequest.isHandled()) {
      return;
    }

    String resourcePath = getResourcePath(target);
    InputStream resource = findResource(resourcePath);

    if (resource != null) {
      String javascript = IOUtils.toString(resource, "UTF-8");
      if ("/jasmine/boot.js".equals(target)) {
        javascript = javascript.replaceAll("window.onload =", "jasmine.boot =");
      }
      setHeaders(response, resourcePath, javascript);
      writeResponse(response, javascript);
      baseRequest.setHandled(true);
    }
  }

  protected abstract InputStream findResource(String resourcePath);

  private String getResourcePath(String url) {
    return url.replaceFirst("^/", "");
  }

  private void setHeaders(HttpServletResponse response, String resourcePath, String content) {
    response.setCharacterEncoding("UTF-8");

    if (resourcePath.endsWith(".css")) {
      response.setContentType("text/css");
    } else {
      response.setContentType("text/javascript");
    }
    response.addDateHeader("EXPIRES", 0L);
    response.setDateHeader(HttpHeaders.LAST_MODIFIED, new Date().getTime());
    try {
      int contentLength = content.getBytes("UTF-8").length;
      response.setHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(contentLength));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("The JVM does not support javascript default encoding.", e);
    }
  }

  private void writeResponse(HttpServletResponse response, String javascript) throws IOException {
    response.getWriter().write(javascript);
  }
}
