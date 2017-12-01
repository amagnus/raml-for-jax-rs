/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.jaxrs.generator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.raml.api.RamlApi;
import org.raml.emitter.IndentedAppendableEmitter;
import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.raml.core.DefaultRamlConfiguration;
import org.raml.jaxrs.raml.core.OneStopShop;
import org.raml.utilities.IndentedAppendable;
import org.raml.v2.api.RamlModelResult;

public class DiffingUtil {

  public void startDiff(Configuration config, File raml) {
    try {
      // get a model from RAML
      RamlScanner scan = new RamlScanner(config);
      RamlModelResult ramlModel = scan.handleResult(raml);

      // get a model from jaxrs
      String applicationName =
          FilenameUtils.removeExtension(raml.getName());
      RamlConfiguration ramlConfiguration =
          DefaultRamlConfiguration.forApplication(applicationName, null);

      OneStopShop oneStopShop = OneStopShop.builder()
          .withJaxRsClassesRoot(config.getInputPath().toPath())
          .withSourceCodeRoot(config.getSourceDirectory().toPath())
          .withRamlOutputFile(config.getOutputDirectory().toPath())
          .withRamlConfiguration(ramlConfiguration)
          .build();

      RamlApi api = oneStopShop.parseJaxRsAndOutputRamlTwo();

      StringBuilder builder = new StringBuilder();
      IndentedAppendableEmitter.create(IndentedAppendable.forNumSpaces(2, builder));

      InputStream is = new ByteArrayInputStream(builder.toString().getBytes());
      RamlModelResult compared = scan.handleResult(is, config.getOutputDirectory().getAbsolutePath());

      // compare both models


    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
