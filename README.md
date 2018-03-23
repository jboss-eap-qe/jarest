# jarest
jar(t)est - testsuite for structural checks for Java 9+

## Running the Testsuite

Ensure you have JDK 9 (or newer) and  Maven 3.5 (or newer) installed

> java -version

> mvn -version

To run against released WildFly (will be downloaded) use following command:

> mvn clean test

To run against specific released WildFly use following command:

> mvn clean test -Dversion.org.jboss.wildfly.dist=11.0.0.Final

To run against local (e.g. latest WildFly master build) bits use following command:

> mvn clean test -Djarest.input.dir=/Users/rsvoboda/TESTING/wildfly-12.0.0.Final

To run against remote bits use following command(s):
 * automation expects that root directory name starts with `wildfly` or `jboss`
> mvn clean test -Djarest.input.zip.url=file:///Users/rsvoboda/TESTING/wildfly-12.0.0.Final.zip

> mvn clean test -Djarest.input.zip.url=http://download.jboss.org/wildfly/12.0.0.Final/wildfly-12.0.0.Final.zip

To run against remote bits with option to specify zip root directory name use following command:

> mvn clean test -Djarest.input.zip.url=file:///Users/rsvoboda/TESTING/wildfly-12.0.0.Final.zip -Djarest.input.zip.root.dir.name=wildfly
