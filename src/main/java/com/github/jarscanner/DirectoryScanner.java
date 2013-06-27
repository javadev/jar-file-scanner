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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang.StringUtils;

/**
 * Directory scanner.
 *
 * @author Valentyn Kolesnikov
 * @version $Revision$ $Date$
 */
public class DirectoryScanner {
    public List<File> scanFiles(String dirName) throws IOException {
        List<File> result = new ArrayList<File>();
        File file = new File(dirName);
LOG.info("Scan folder - " + dirName);
            String addDirName = StringUtils.remove(
                file.getAbsolutePath().replace("\\", "/"), dirName).replaceFirst(".", "");
            if (file.isDirectory()) {
                Collection<File> filesInDir = FileUtils.listFiles(file,
                    FileFileFilter.FILE, null);
                if (!filesInDir.isEmpty()) {
                    for (File file2 : filesInDir) {
                        result.add(file2);
                    }
                }
            }
        return result;
    }

    public boolean isExist(String dirName) {
        return new File(dirName).exists();
    }
}
