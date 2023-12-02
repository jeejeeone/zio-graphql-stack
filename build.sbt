ThisBuild / scalaVersion     := "3.3.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.zap"
ThisBuild / organizationName := "zap"

enablePlugins(GraalVMNativeImagePlugin)

lazy val root = (project in file("."))
  .settings(
    name := "zio-graphql-stack",
    libraryDependencies ++= Seq(
      "dev.zio"                 %% "zio"                               % "2.0.19",
      "io.github.gaelrenoux"    %% "tranzactio-core"                   % "5.0.1",
      "io.github.gaelrenoux"    %% "tranzactio-anorm"                  % "5.0.1",
      "org.playframework.anorm" %% "anorm"                             % "2.7.0",
      "com.clickhouse"           % "clickhouse-jdbc"                   % "0.5.0",
      "org.lz4"                  % "lz4-java"                          % "1.8.0",
      "com.github.ghostdogpr"   %% "caliban"                           % "2.4.3",
      "com.github.ghostdogpr"   %% "caliban-quick"                     % "2.4.3",
      "com.github.ghostdogpr"   %% "caliban-tracing"                   % "2.4.3",
      "dev.zio"                 %% "zio-prelude"                       % "1.0.0-RC21",
      "dev.zio"                 %% "zio-config"                        % "4.0.0-RC16",
      "dev.zio"                 %% "zio-config-magnolia"               % "4.0.0-RC16",
      "dev.zio"                 %% "zio-logging"                       % "2.1.15",
      "dev.zio"                 %% "zio-metrics-connectors"            % "2.2.1",
      "dev.zio"                 %% "zio-metrics-connectors-prometheus" % "2.2.1",
      "dev.zio"                 %% "zio-test"                          % "2.0.19" % Test,
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    graalVMNativeImageOptions ++= Seq(
      "--enable-http",
      "--enable-url-protocols=http,https",
      "--install-exit-handlers",
      "-Djdk.http.auth.tunneling.disabledSchemes=",
      "--initialize-at-run-time=io.netty.channel.DefaultFileRegion",
      "--initialize-at-run-time=io.netty.channel.epoll.Native",
      "--initialize-at-run-time=io.netty.channel.epoll.Epoll",
      "--initialize-at-run-time=io.netty.channel.epoll.EpollEventLoop",
      "--initialize-at-run-time=io.netty.channel.epoll.EpollEventArray",
      "--initialize-at-run-time=io.netty.channel.kqueue.KQueue",
      "--initialize-at-run-time=io.netty.channel.kqueue.KQueueEventLoop",
      "--initialize-at-run-time=io.netty.channel.kqueue.KQueueEventArray",
      "--initialize-at-run-time=io.netty.channel.kqueue.Native",
      "--initialize-at-run-time=io.netty.channel.unix.Limits",
      "--initialize-at-run-time=io.netty.channel.unix.Errors",
      "--initialize-at-run-time=io.netty.channel.unix.IovArray",
      "--initialize-at-run-time=io.netty.handler.ssl.BouncyCastleAlpnSslUtils",
      "--initialize-at-run-time=io.netty.handler.codec.compression.ZstdOptions",
      "--initialize-at-run-time=io.netty.incubator.channel.uring.Native",
      "--initialize-at-run-time=io.netty.incubator.channel.uring.IOUring",
      "--initialize-at-run-time=io.netty.incubator.channel.uring.IOUringEventLoopGroup",
      "--initialize-at-run-time=io.netty.channel.DefaultChannelId",
      "--initialize-at-build-time=org.slf4j.LoggerFactory",
      "-H:+ReportExceptionStackTraces",
      s"-H:ConfigurationFileDirectories=${baseDirectory.value.getAbsolutePath}/graal/native-image",
    ),
  )
