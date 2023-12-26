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
- [x] Maybe caching with Redis
- [x] Maybe GraalVM native image compilation
- [x] Maybe runnable image using GraalVM native image

Stack:

- zio
- zio-prelude (newtypes)
- zio-config
- jedis (redis client, gave up on zio-redis due to missing multi support)
- zio-telemetry with opentelemtry sdk (distributed tracing)
- caliban (GraphQL+federation support)
- zquery (n+1 problem)
- tranzactio with anorm (zio wrapper for anorm)
- anorm (Query ClickHouse with nice decoding)
- sbt-tpolecat for scalac configuration

# Run locally

## Local JVM

- Start ch docker container `(cd docker && docker-compose start)`
  - Init test data once when ch is up, run init script `docker/insert-ch-data.sh`
- Run `sbt run`

## Native image

- Publish app native image `sbt docker:publishLocal`
- Start ch docker container and app native image container `(cd docker && docker-compose --profile app start)`
  - Init test data once when ch is up, run init script `docker/insert-ch-data.sh`

## Example request

```
curl --location 'http://localhost:8080/api/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"query {\n    persons {\n        persons {\n            name\n            address {\n                street\n            }\n        }\n    }\n}","variables":{}}'
```

# Native image

sbt-native-packager docker plugin can be used to create a runnable image ´sbt docker:publishLocal`.

## Compile using a container

Compile native image in a container `sbt graalvm-native-image:packageBin`.

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
