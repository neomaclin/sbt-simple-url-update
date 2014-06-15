lazy val root = (project in file(".")).enablePlugins(SbtWeb)

pipelineStages := Seq(simpleUrlUpdate)

// for checking that the produced pipeline mappings are correct

val expectedContents = Set("img/08e31f7ccd6c10d6133027cd5173b0ac-a.png","")

val checkUpdatedContents = taskKey[Unit]("check if the static file had the updated asset url references")

checkUpdatedContents := {
  val mappings = WebKeys.pipeline.value
  val contents = (mappings.filterNot(x => x._1.isDirectory).filter(x => x._2.endsWith("css") || x._2.endsWith("js") ).map(x =>IO.read(x._1))).toSet
  if (contents != expectedContents) sys.error(s"Expected $expectedContents but contents are $contents")
}

