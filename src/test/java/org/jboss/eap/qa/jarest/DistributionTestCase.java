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

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class DistributionTestCase {
    private static DistributionDir distributionDir;
    private static Configuration config = Configuration.get();

    @BeforeAll
    public static void setup() {
        String jarestInputDir = System.getProperty("jarest.input.dir", "N/A");
        distributionDir = DistributionDir.at(Paths.get(jarestInputDir));
        System.out.println("Testing: " + distributionDir);
    }

    @Test
    void checkUnexpectedOverlayDirectoryForJava9() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        distributionDir.jars()
                .filter(jar -> config.expectedMRv9JarNames().stream()
                        .noneMatch(jarName -> jar.baseFileName().startsWith(jarName))
                )
                .forEach(jar -> {
                            softly.assertThat(
                                    jar.hasOverlayDirectoryFor(9))
                                    .as("Artifact %s is not expected to be Multi-Release JAR", jar.file())
                                    .isFalse();
                        }
                );
        softly.assertAll();
    }

    @Test
    void checkExpectedOverlayDirectoryForJava9() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        distributionDir.jars()
                .filter(jar -> config.expectedMRv9JarNames().stream()
                        .anyMatch(jarName -> jar.baseFileName().startsWith(jarName))
                )
                .forEach(jar -> {
                            softly.assertThat(
                                    jar.hasOverlayDirectoryFor(9))
                                    .as("Artifact %s is expected to be Multi-Release JAR", jar.file())
                                    .isTrue();
                        }
                );
        softly.assertAll();
    }

    @Test
    void checkOverlayDirectoryAndManifestEntryForExpectedMRJars() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        distributionDir.jars()
                .filter(jar -> config.expectedMRv9JarNames().stream()
                        .anyMatch(jarName -> jar.baseFileName().startsWith(jarName))
                )
                .forEach(jar -> {
                            softly.assertThat(
                                    jar.hasOverlayDirectory()
                                            && jar.isMultiReleaseJar())
                                    .as("Artifact %s is expected to be Multi-Release JAR, jar.isMultiReleaseJar() returns %s",
                                            jar.file(), jar.isMultiReleaseJar())
                                    .isTrue();
                        }
                );
        softly.assertAll();
    }

    @Test
    void atMostMRv9JARsPresent() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        distributionDir.jars()
                .filter(jar -> jar.hasOverlayDirectory())
                .forEach(jar -> {
                            softly.assertThat(jar.hasJustOverlayDirectoriesFor(9))
                                    .as("Artifact %s is expected to be at most versions/9 Multi-Release JAR", jar.file())
                                    .isTrue();
                        }
                );
        softly.assertAll();
    }
}
