/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.eap.qa.jarest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

public class DistributionDir {
    private final Path rootDirectory;

    public static DistributionDir at(Path rootDirectory) {
        if (!Files.isDirectory(rootDirectory)) {
            throw new IllegalArgumentException("Directory expected: " + rootDirectory);
        }
        return new DistributionDir(rootDirectory.toAbsolutePath().normalize());
    }

    private DistributionDir(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public Path rootDirectory() {
        return rootDirectory;
    }

    public Stream<Path> files() throws IOException {
        return Files.walk(rootDirectory)
                .filter(Files::isRegularFile);
    }

    public Stream<Path> jars() throws IOException {
        return Files.walk(rootDirectory)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".jar"));
    }

    public Stream<ZipFile> jarsAsZipFiles() throws IOException {
        return jars()
                .map(path -> {
                    try {
                        return new ZipFile(path.toFile());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    public Stream<ZipFile> jarsAsJarFiles() throws IOException {
        return jars()
                .map(path -> {
                    try {
                        return new JarFile(path.toFile(), false);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }
    @Override
    public String toString() {
        return "Distribution directory " + rootDirectory;
    }
}
