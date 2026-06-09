import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings

val appName = "sdec-threadinfo-api"

ThisBuild / majorVersion := 0

lazy val compilerSettings = Seq(
  scalaVersion := "3.3.7",
  scalacOptions += "-Wconf:src=routes/.*:s",
  semanticdbEnabled := true
)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(
    JUnitXmlReportPlugin
  )
  .settings(
    ScoverageKeys.coverageExcludedFiles := Seq(
      "<empty>",
      "Reverse.*",
      ".*.Module",
      ".*.model.*",
      ".*.config.*",
      "uk.gov.hmrc.BuildInfo",
      "app.*",
      "prod.*",
      ".*Routes.*",
      "testOnly.*",
      "testOnlyDoNotUseInAppConf.*"
    ).mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageFailOnMinimum    := true,
    ScoverageKeys.coverageHighlighting     := true,
    ScoverageKeys.coverageDataDir := target.value / "scoverage-report",
    Compile / scalafmtOnCompile            := true,
    Test / scalafmtOnCompile               := true,
    PlayKeys.playDefaultPort               := 4001,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    compilerSettings
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(
    DefaultBuildSettings.itSettings(),
    compilerSettings
  )
  .settings(libraryDependencies ++= AppDependencies.it)

inThisBuild(
  List(
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision
  )
)

addCommandAlias(
  "prePrChecks",
  "; scalafmtCheckAll; scalafmtSbtCheck; scalafixAll --check"
)
addCommandAlias(
  "checkCodeCoverage",
  "; clean; coverage; test; it/test; coverageReport"
)
addCommandAlias("lint", "; scalafmtAll; scalafmtSbt; scalafixAll")
addCommandAlias("prePush", "; reload; clean; compile; test; it/test; lint;")
