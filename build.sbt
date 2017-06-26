name := "incentives-engine-demo"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies += "org.drools" % "drools-core" % "6.1.0.Final"

libraryDependencies += "org.drools" % "drools-compiler" % "6.1.0.Final"

libraryDependencies += "org.drools" % "knowledge-api" % "6.1.0.Final"

libraryDependencies += "joda-time" % "joda-time" % "2.9.7"

libraryDependencies += "junit" % "junit" % "4.12" % Test

libraryDependencies +=  "com.novocode" % "junit-interface" % "0.11" % Test

resolvers += "jboss-releases" at "https://repository.jboss.org/nexus/content/repositories/releases"

resolvers += "jboss-jsr94" at "http://repository.jboss.org/nexus/content/groups/public-jboss"

resolvers += "sonatype-public" at "https://oss.sonatype.org/content/groups/public"
