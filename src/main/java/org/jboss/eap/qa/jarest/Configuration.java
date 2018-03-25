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

import java.util.Set;

public final class Configuration {
    private static Configuration instance;

    public synchronized static Configuration get() {
        if (instance != null) {
            return instance;
        }

        instance = new Configuration();
        return instance;
    }

    private Configuration() {
    }

    // TODO consider PathMatcher
    public Set<String> expectedMRv9JarNames() {
        return Set.of(
                "wildfly-elytron-tool.jar",
                "jboss-cli-client.jar",
                "jboss-client.jar",
                "jboss-modules.jar",
                "wildfly-common-",
                "jboss-marshalling-2",  // vs. jboss-marshalling-river-2.0.4.Final.jar
                "openjdk-orb-"
        );
    }
}
