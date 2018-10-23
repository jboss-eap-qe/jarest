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
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
                                    .as("Artifact %s is not expected to be Multi-Release JAR with overlay 9", jar.file())
                                    .isFalse();
                        }
                );
        softly.assertAll();
    }

    @Test
    void checkUnexpectedOverlayDirectoryForJava10() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        distributionDir.jars()
                .filter(jar -> config.expectedMRv10JarNames().stream()
                        .noneMatch(jarName -> jar.baseFileName().startsWith(jarName))
                )
                .forEach(jar -> {
                            softly.assertThat(
                                    jar.hasOverlayDirectoryFor(10))
                                    .as("Artifact %s is not expected to be Multi-Release JAR with overlay 10", jar.file())
                                    .isFalse();
                        }
                );
        softly.assertAll();
    }

    @Test
    void checkUnexpectedOverlayDirectoryForJava11() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        distributionDir.jars()
                .filter(jar -> config.expectedMRv11JarNames().stream()
                        .noneMatch(jarName -> jar.baseFileName().startsWith(jarName))
                )
                .forEach(jar -> {
                            softly.assertThat(
                                    jar.hasOverlayDirectoryFor(11))
                                    .as("Artifact %s is not expected to be Multi-Release JAR with overlay 11", jar.file())
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
    void checkIfOverlayDirectoryPresentWhenManifestEntryDefined() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        distributionDir.jars()
                .filter(jar -> jar.isMultiReleaseJar())
                .forEach(jar -> {
                            softly.assertThat(
                                    jar.hasOverlayDirectory())
                                    .as("Artifact %s is expected to have META-INF/versions overlay directory", jar.file())
                                    .isTrue();
                        }
                );
        softly.assertAll();
    }

    @Test
    void checkIfManifestEntryDefinedWhenOverlayDirectoryPresent() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        distributionDir.jars()
                .filter(jar -> jar.hasOverlayDirectory())
                .forEach(jar -> {
                            softly.assertThat(
                                    jar.isMultiReleaseJar())
                                    .as("Artifact %s is expected to have 'Multi-Release: true' entry in META-INF/MANIFEST.MF", jar.file())
                                    .isTrue();
                        }
                );
        softly.assertAll();
    }

    @Test
    void atMostMRv9or10JARsPresent() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        distributionDir.jars()
                .filter(jar -> jar.hasOverlayDirectory())
                .forEach(jar -> {
                            softly.assertThat(jar.hasJustOverlayDirectoriesFor(9) ||
                                                jar.hasJustOverlayDirectoriesFor(10))
                                    .as("Artifact %s is expected to be at most versions/9 or versions/10 Multi-Release JAR", jar.file())
                                    .isTrue();
                        }
                );
        softly.assertAll();
    }

    @Test
    void checkAutomaticModuleNamePresence() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        Map<String, String> expectedNames = config.expectedAutomaticModuleNames();
        distributionDir.jars()
                .filter(jar -> expectedNames.containsKey(jar.baseFileNameWithoutVersion()))
                .sorted(Comparator.comparing(jar -> jar.baseFileName()))
                .forEach(jar -> {
                            softly.assertThat(
                                    jar.getAutomaticModuleNameFromManifest().isPresent())
                                    .as("Artifact %s has no Automatic-Module-Name", jar.baseFileName())
                                    .isTrue();
                        }
                );
        softly.assertAll();
    }

    @Test
    void checkExpectedAutomaticModuleNames() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        Map<String, String> expectedNames = config.expectedAutomaticModuleNames();
        distributionDir.jars()
                .filter(jar -> jar.getAutomaticModuleNameFromManifest().isPresent())
                .filter(jar -> expectedNames.containsKey(jar.baseFileNameWithoutVersion()))
                .sorted(Comparator.comparing(jar -> jar.baseFileName()))
                .forEach(jar -> {
                            String moduleName = jar.getAutomaticModuleNameFromManifest().get();
//                            System.out.println(jar
//                                    + "\t-\t" + jar.baseFileNameWithoutVersion()
//                                    + "\t-\t" + moduleName);
                            softly.assertThat(
                                    expectedNames.get(jar.baseFileNameWithoutVersion()).equals(moduleName))
                                    .as("Artifact %s has unexpected Automatic-Module-Name %s, expected was %s",
                                            jar.baseFileName(), moduleName, expectedNames.get(jar.baseFileNameWithoutVersion()))
                                    .isTrue();
                        }
                );
        softly.assertAll();
    }

    @Test
    void listUntrackedFilesWithAutomaticModuleNames() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        Map<String, String> expectedNames = config.expectedAutomaticModuleNames();
        distributionDir.jars()
                .filter(jar -> jar.getAutomaticModuleNameFromManifest().isPresent())
                .filter(jar -> ! expectedNames.containsKey(jar.baseFileNameWithoutVersion()))
                .sorted(Comparator.comparing(jar -> jar.baseFileName()))
                .forEach(jar -> {
                            String moduleName = jar.getAutomaticModuleNameFromManifest().get();
                            System.out.println(jar
                                    + "\t-\t" + moduleName);
//                            softly.assertThat(
//                                    expectedNames.get(jar.baseFileNameWithoutVersion()).equals(moduleName))
//                                    .as("Artifact %s has unexpected Automatic-Module-Name %s, expected was %s",
//                                            jar.baseFileName(), moduleName, expectedNames.get(jar.baseFileNameWithoutVersion()))
//                                    .isTrue();
                        }
                );
        softly.assertAll();
    }

    @Test
    void checkUnexpectedJarsWithModuleInfoClass() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        distributionDir.jars()
                .filter(jar -> config.expectedModuleInfoJarNames().stream()
                        .noneMatch(jarName -> jar.baseFileName().startsWith(jarName))
                )
                .forEach(jar -> {
                            softly.assertThat(
                                    jar.hasModuleInfoClass())
                                    .as("Artifact %s is not expected to contain module-info.class", jar.baseFileName())
                                    .isFalse();
                        }
                );
        softly.assertAll();
    }

    @Test
    void checkExpectedJarsWithModuleInfoClass() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        distributionDir.jars()
                .filter(jar -> config.expectedModuleInfoJarNames().stream()
                        .anyMatch(jarName -> jar.baseFileName().startsWith(jarName))
                )
                .forEach(jar -> {
                            softly.assertThat(
                                    jar.hasModuleInfoClass())
                                    .as("Artifact %s is expected to contain module-info.class", jar.baseFileName())
                                    .isTrue();
                        }
                );
        softly.assertAll();
    }

    @Test
    void duplicatedPackagesInJars() throws IOException {
        SoftAssertions softly = new SoftAssertions();
        Map<String, List<String>> packagesInJars = new TreeMap<>();
        Set<String> clientJarNames = config.clientJarNames();

        distributionDir.jars()
                .filter(jar -> ! clientJarNames.contains(jar.baseFileName()))
                .forEach(jar ->
                            jar.packages().forEach(pkg -> {
                                if (packagesInJars.containsKey(pkg)) {
                                    packagesInJars.get(pkg).add(jar.baseFileName());
                                } else {
                                    packagesInJars.put(pkg, Lists.newArrayList(jar.baseFileName()));
                                }
                            })
                );

        packagesInJars.entrySet()
                .forEach(entry -> {
                            softly.assertThat(entry.getValue().size())
                                    .as("Package %s is present in multiple jars %s", entry.getKey(), entry.getValue())
                                    .isEqualTo(1);
                        }
                );

        softly.assertAll();
    }
}
