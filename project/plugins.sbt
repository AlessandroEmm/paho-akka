
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers ++= Seq(
  "DefaultMavenRepository" at "https://repo1.maven.org/maven2/",
  "JavaNet1Repository" at "http://download.java.net/maven/1/",
  Resolver.sonatypeRepo("public"),
  Resolver.typesafeRepo("releases"),
  "inspired snapshots" at "https://artifacts.dev.inspired.ag/artifactory/inspired-snapshots/",
  "inspired releases" at "https://artifacts.dev.inspired.ag/artifactory/inspired-releases/"
)

libraryDependencies += "com.softwaremill.sttp" %% "okhttp-handler" % "0.0.12"
