# General

PoC for a GraphQL microservice. Explore Scala 3, particularly braceless syntax.

Following capabilities:
- [x] Scala 3 (braceless)
- [x] Federation support
- [x] Handle n+1 queries problem
- [x] Query ClickHouse
  - Custom decoders for CH datatypes
  - Automatic decoding to Scala types 
- [ ] Maybe distributed tracing
- [ ] Maybe caching with Redis
- [x] Maybe GraalVM native image compilation
- [ ] Maybe runnable image using GraalVM native image

Stack:

- zio
- zio-prelude (newtypes)
- zio-config
- zio-logging with slf4j
- zio-telemetry with opentelemtry sdk (distributed tracing)
- caliban (GraphQL+federation support)
- zquery (n+1 problem)
- tranzactio with anorm (zio wrapper for anorm)
- anorm (Query ClickHouse with nice decoding)
- sbt-tpolecat for scalac configuration

# Run locally

- Start ch docker container `(cd docker && docker-compose start)`
- Before first run init test data (do it once) `chmod +x docker/insert-ch-data.sh && docker/insert-ch-data.sh`
- Run `sbt run`

Example request:

```
curl --location 'http://localhost:8080/api/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"query {\n    persons {\n        persons {\n            name\n            address {\n                street\n            }\n        }\n    }\n}","variables":{}}'
```

# Native image 

## Compile using a container

Create executable `sbt graalvm-native-image:packageBin`, compiled in a container. 

- Produced to `.target/graalvm-native-image/zio-graphql-stack`.
- Runnable on ol9 / centos9 / rhel9

TODO: Unsure what's the best way to create a runnable image from native executable. Essentially `oraclelinux:9-slim` image can be used
to run the binary. sbt-native-packager docker plugin seems very awkward, in theory with some manual work it could be used.

### Create runnable image manually

Example Dockerfile:

```
FROM oraclelinux:9-slim
WORKDIR /app
COPY ./target/graalvm-native-image/zio-graphql-stack ./
ENTRYPOINT /app/zio-graphql-stack
```

In project directory run `docker build -t zio-graphql-stack:0.1.0-SNAPSHOT .`, then `docker run zio-graphql-stack:0.1.0-SNAPSHOT`.

## Compile locally

- Remove `graalVMNativeImageGraalVersion := Some("22.3.2")` from build.sbt
- Provide correct path `-H:ConfigurationFileDirectories` in build.sbt
- Provide native-image in PATH 
- Run `sbt graalvm-native-image:packageBin`

Above tested with graalvm-ce-java17-22.3.2.

# Endpoints

Http server @ localhost:8080

| Endpoint          | Description        |
|-------------------|--------------------|
| /api/graphql      | GraphQL api        |
| /graphiql         | GraphiQL           |
| /metrics          | Prometheus metrics |
| /health/liveness  | Liveness           |
| /health/readiness | Readiness          |
