resolvers += Classpaths.sbtPluginSnapshots

addSbtPlugin("com.nurun.sbt" % "sbt-simple-url-update" % sys.props("project.version"))
