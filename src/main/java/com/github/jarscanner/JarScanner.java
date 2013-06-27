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

import java.io.File;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Scans directory with jar files.
 *
 * @author Valentyn Kolesnikov
 * @version $Revision$ $Date$
 */
public class JarScanner {
    private String[] args;
    public JarScanner(String[] args) {
        this.args = args;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            LOG.info("Scan jars.");
            LOG.info("Usage: java -jar jarscanner-1.0 scan jar_directory output.xml");
            return;
        }
        if (args[0].trim().equalsIgnoreCase("scan")) {
            new JarScanner(args).scan();
        }
    }

    public void scan() throws Exception {
        String jarDirectory = args[1].trim();
        String outXml = args[2].trim();
        LOG.info("jar directory:" + jarDirectory);
        LOG.info("out xml:" + outXml);
        List<File> jarFiles = new DirectoryScanner().scanFiles(jarDirectory);
        Map<String, List<String>> classNamesInJars = new LinkedHashMap<String, List<String>>();
        for (File file : jarFiles) {
            if (file.getName().endsWith(".jar")) {
                List<String> classes = findClassesInJar(file.getAbsolutePath());
                classNamesInJars.put(file.getName(), classes);
                LOG.info(" --> " + file.getName() + " (" + classes.size() + ")");
            }
        }
        Map <String, List<String>> duplicates = new LinkedHashMap<String, List<String>>();
        for (Map.Entry<String, List<String>> entry : classNamesInJars.entrySet()) {
            for (String value : entry.getValue()) {
                if (duplicates.get(value) == null) {
                    List<String> packages = new ArrayList<String>();
                    packages.add(entry.getKey());
                    duplicates.put(value, packages);
                } else {
                    duplicates.get(value).add(entry.getKey());
                }
            }
        }
        List<String> classDuplicates = new ArrayList<String>();
        for (Map.Entry<String, List<String>> entry : duplicates.entrySet()) {
            if (entry.getValue().size() > 1 && entry.getValue().toString().contains("portal-impl")) {
                LOG.info(" Duplicates for class '" + entry.getKey() + "' in packages - " + entry.getValue());
                classDuplicates.add(entry.getKey());
            }
        }
        java.util.Collections.sort(classDuplicates);
        new XmlGenerator(classDuplicates, outXml).generate();
    }

    private List<String> findClassesInJar(String jarFilename) throws Exception {
        List<String> classFiles = new ArrayList<String>();
        JarFile jarFile = new JarFile(jarFilename);
        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            final String entryName = entry.getName();
            if (entryName.endsWith(".class")) {
                classFiles.add(entryName);
            }
        }
        return classFiles;
    }
}
