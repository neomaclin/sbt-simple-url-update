resolvers += Classpaths.sbtPluginSnapshots

addSbtPlugin("org.neolin.sbt" % "sbt-simple-url-update" % sys.props("project.version"))
