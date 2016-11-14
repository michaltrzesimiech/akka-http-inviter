name          := """inviter"""
version       := "0.0.1"
scalaVersion  := "2.11.8"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  	val akkaVersion = "2.4.11"
	Seq(
      "com.typesafe.akka"    %%    "akka-actor"                           % akkaVersion,
	  "com.typesafe.akka" 	 %%    "akka-http-core" 					  % akkaVersion,
	  "com.typesafe.akka" 	 %%    "akka-http-experimental" 			  % akkaVersion,
      "com.typesafe.akka"    %%    "akka-http-spray-json-experimental"    % akkaVersion,
	  "com.typesafe.akka" 	 %%    "akka-stream" 						  % akkaVersion,
      "com.typesafe.akka"	 %%    "akka-slf4j" 					 	  % akkaVersion,
	  "com.typesafe.akka" 	 %%    "akka-testkit" 						  % akkaVersion,
      "com.typesafe.akka" 	 %%    "akka-http-testkit" 					  % akkaVersion % "test",
      "ch.qos.logback" 		 % 	   "logback-classic" 					  % "1.0.9",
	  "org.scalactic" 		 %%    "scalactic" 							  % "3.0.0",
	  "org.scalatest" 		 %%    "scalatest" 							  % "3.0.0" 	% "test"
	)
}

fork in run := true

connectInput in run := true