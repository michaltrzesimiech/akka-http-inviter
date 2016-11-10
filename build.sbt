name          := """inviter"""
version       := "0.0.1"
scalaVersion  := "2.11.8"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

resolvers += Resolver.jcenterRepo

libraryDependencies ++=  Seq(
                      "com.typesafe.akka"    %%    "akka-actor"                           % "2.4.12",
                      "com.typesafe.akka"    %%    "akka-http-spray-json-experimental"    % "2.4.11",
					  "com.typesafe.akka" 	 %%    "akka-stream" 						  % "2.4.12",
					  "com.typesafe.akka" 	 %%    "akka-http-core" 					  % "2.4.11",
					  "com.typesafe.akka" 	 %%    "akka-http-experimental" 			  % "2.4.11",
                      "net.codingwell"       %%    "scala-guice"              			  % "4.1.0"
)

fork in run := true

