/*
 * $Id$
 *
 * Copyright 2013 Valentyn Kolesnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jarscanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

/**
 * Xml generator.
 *
 * @author vko
 * @version $Revision$ $Date$
 */
public class XmlGenerator {
    private List<String> duplicates;
    private String outXml;
    public XmlGenerator(List<String> duplicates, String outXml) {
        this.duplicates = duplicates;
        this.outXml = outXml;
    }

    public void generate() {
        Properties p = new Properties();
        p.setProperty("resource.loader", "string");
        p.setProperty("resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        Velocity.init(p);

        Template template = getTemplate("com/github/jarscanner/jar-data.vm");
        VelocityContext context = new VelocityContext();
        context.put("duplicates", duplicates);
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(outXml), "utf-8");
            template.merge(context, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LOG.error(e, e.getMessage());
        }
    }

    private Template getTemplate(final String templatePath) {
        if (!Velocity.resourceExists(templatePath)) {
            StringResourceRepository repo = StringResourceLoader.getRepository();
            repo.putStringResource(templatePath, getTemplateFromResource(templatePath));
        }
        return Velocity.getTemplate(templatePath);
    }

    /**
     * Read a template into memory
     *
     * @param templatePath
     * @return
     */
    private String getTemplateFromResource(final String templatePath) {
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(templatePath);
            return IOUtils.toString(stream, "UTF-8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
