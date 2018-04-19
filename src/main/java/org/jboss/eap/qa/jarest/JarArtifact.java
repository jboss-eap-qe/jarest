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
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipFile;

public class JarArtifact extends Artifact {

    public JarArtifact(Path file) {
        super(file);
    }

    public boolean hasClasses() {
        try (ZipFile zip = new ZipFile(file.toFile())) {
            return zip.stream()
                    .anyMatch(entry -> entry.getName().endsWith(".class"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public boolean hasOverlayDirectoryFor(int javaVersion) {
        try (ZipFile zip = new ZipFile(file.toFile())) {
            return zip.stream()
                    .anyMatch(entry -> entry.getName().contains("META-INF/versions/" + javaVersion));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public boolean hasJustOverlayDirectoriesFor(int... javaVersions) {
        try (ZipFile zip = new ZipFile(file.toFile())) {
            return zip.stream()
                    .filter(entry -> entry.getName().matches("META-INF/versions/\\d(.*)"))
                    .allMatch(entry -> Arrays.stream(javaVersions)
                                                .mapToObj(i -> "META-INF/versions/" + i)
                                                .anyMatch(entry.getName()::contains));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public boolean hasOverlayDirectory() {
        try (ZipFile zip = new ZipFile(file.toFile())) {
            return zip.stream()
                    .anyMatch(entry -> entry.getName().contains("META-INF/versions"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<String> getSpecificationVersionFromManifest() {
        return getMainAttributeFromManifest("Specification-Version");
    }

    public Optional<String> getImplementationVersionFromManifest() {
        return getMainAttributeFromManifest("Implementation-Version");
    }

    public Optional<String> getAutomaticModuleNameFromManifest() {
        return getMainAttributeFromManifest("Automatic-Module-Name");
    }

    public Optional<String> getMultiReleaseFromManifest() {
        return getMainAttributeFromManifest("Multi-Release");
    }

    public boolean isMultiReleaseJar() {
        return getMultiReleaseFromManifest()
                .map(Boolean::valueOf).orElse(false);
    }

    private Optional<String> getMainAttributeFromManifest(String attributeName) {
        try (JarFile jar = new JarFile(file.toFile(), false)) {
            Manifest manifest = jar.getManifest();
            if (manifest == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(manifest.getMainAttributes().getValue(attributeName));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}