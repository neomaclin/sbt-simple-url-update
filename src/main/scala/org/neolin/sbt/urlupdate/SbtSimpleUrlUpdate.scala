//Copyright (c) 2014 ICRL
//
//See the file license.txt for copying permission.

package org.neolin.sbt.urlupdate

import sbt._
import com.typesafe.sbt.web.{PathMapping, SbtWeb}
import com.typesafe.sbt.web.pipeline.Pipeline
import sbt.Keys._
import sbt.Task

object Import {

  val simpleUrlUpdate = TaskKey[Pipeline.Stage]("simple-url-update", "Update assets url in static css or js files with in asset pipeline.")
  
  object UrlUpdateKeys {
    val algorithms = SettingKey[Seq[String]]("digest-algorithms", "Types of checksum used in the digest pipeline to generate.")
  }
}

object SbtSimpleUrlUpdate extends AutoPlugin {

  override def requires = SbtWeb

  override def trigger = AllRequirements

  val autoImport = Import

  import SbtWeb.autoImport._
  import WebKeys._
  import autoImport._
  import UrlUpdateKeys._

  override def projectSettings: Seq[Setting[_]] = Seq(
    algorithms := Seq("md5"),
    includeFilter in simpleUrlUpdate := "*.css" || "*.js" ,
    excludeFilter in simpleUrlUpdate := HiddenFileFilter,
    simpleUrlUpdate := simpleURLUpdateFiles.value
  )

  private def updatePipeline(mappings: Seq[PathMapping], algorithm: String): String => String = {
    val reversePathMappings = mappings.map{ case (k, v) => (v, k) }.toMap
    
    def checksummedPath(path: String): String = {
	  
      val pathFile = sbt.file(path)
      reversePathMappings.get(path + "." + algorithm) match {
        case Some(file) => (pathFile.getParentFile / (IO.read(file) + "-" + pathFile.getName)).getPath
        case None => path
      }
    }
    val assetVersions = mappings.map{ 
      case (file, path) => path.replaceAll("\\\\","/") -> checksummedPath(path).replaceAll("\\\\","/")
    }.distinct.filterNot{ 
      case (originalPath, newPath) => originalPath == newPath 
    }

    Function.chain(
      assetVersions.map{ 
        case (originalPath, newPath) => (content: String) => content.replaceAll(originalPath, newPath)
      }
    )
  }

  def simpleURLUpdateFiles: Def.Initialize[Task[Pipeline.Stage]] = Def.task {
    mappings =>
      val targetDir = webTarget.value / simpleUrlUpdate.key.label
      val include = (includeFilter in simpleUrlUpdate).value
      val exclude = (excludeFilter in simpleUrlUpdate).value
      
      SbtWeb.syncMappings(
        streams.value.cacheDirectory,
        mappings,
        targetDir
      )

      val updateMappings = targetDir.***.get.toSet.filter(_.isFile).pair(relativeTo(targetDir))

      for {
        algorithm <- algorithms.value
        (file, path) <- updateMappings.filter(f => !f._1.isDirectory && include.accept(f._1) && !exclude.accept(f._1))
      } yield {
        IO.write(file, updatePipeline(updateMappings, algorithm)(IO.read(file)))
      }

      targetDir.***.get.toSet.filter(_.isFile).pair(relativeTo(targetDir))
  }
}
