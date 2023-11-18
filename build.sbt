ThisBuild / scalaVersion     := "3.3.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "zio-graphql-stack",
    libraryDependencies ++= Seq(
      "dev.zio"                     %% "zio"                               % "2.0.19",
      "io.github.gaelrenoux"        %% "tranzactio-core"                   % "5.0.1",
      "io.github.gaelrenoux"        %% "tranzactio-anorm"                  % "5.0.1",
      "org.playframework.anorm"     %% "anorm"                             % "2.7.0",
      "com.github.ghostdogpr"       %% "caliban"                           % "2.4.3",
      "com.clickhouse"               % "clickhouse-jdbc"                   % "0.5.0",
      "org.lz4"                      % "lz4-java"                          % "1.8.0",
      "com.github.ghostdogpr"       %% "caliban-zio-http"                  % "2.4.3",
      "com.github.ghostdogpr"       %% "caliban-quick"                     % "2.4.3",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"                  % "1.2.11",
      "dev.zio"                     %% "zio-prelude"                       % "1.0.0-RC21",
      "dev.zio"                     %% "zio-config"                        % "4.0.0-RC16",
      "dev.zio"                     %% "zio-config-magnolia"               % "4.0.0-RC16",
      "dev.zio"                     %% "zio-logging"                       % "2.1.15",
      "dev.zio"                     %% "zio-logging-slf4j"                 % "2.1.15",
      "dev.zio"                     %% "zio-metrics-connectors"            % "2.2.1",
      "dev.zio"                     %% "zio-metrics-connectors-prometheus" % "2.2.1",
      "dev.zio"                     %% "zio-test"                          % "2.0.19" % Test,
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
  )
