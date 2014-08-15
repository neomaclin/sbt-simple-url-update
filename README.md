sbt-simple-url-update
==========

[sbt-web] plugin for updating the static assets which having the url refering to other asset.


Add plugin
----------

Add the plugin to `project/plugins.sbt`. For example:

```scala
resolvers += "Bintray repository" at "http://dl.bintray.com/neomaclin/sbt-plugins/"

addSbtPlugin("org.neolin.sbt" % "sbt-simple-url-update" % "1.0.0")
```

Your project's build file also needs to enable sbt-web plugins. For example with build.sbt:

    lazy val root = (project.in file(".")).enablePlugins(SbtWeb)

As with all sbt-web asset pipeline plugins you must declare their order of execution e.g.:

```scala
pipelineStages := Seq(digest, simpleUrlUpdate, digest, gzip)
```

Configuration
-------------


### Algorithms

Supported hash algorithms are `md5` and `sha1`. The default is to only update the asset having
`md5` checksum files. To configure this, modify the `algorithms`
setting. For example, to also generate`sha1` checksum files:

```scala
UrlUpdateKeys.algorithms += "sha1"
```

### Filters

Include and exclude filters can be provided. For example, to only create
checksum files for `.js` files:

```scala
includeFilter in simpleUrlUpdate := "*.js"
```

default is to have `.css` and `.js` files updated.

```scala
includeFilter in simpleUrlUpdate := "*.js" || "*.css"
```


Copyright (c) 2014 ICRL

See the file license.txt for copying permission.