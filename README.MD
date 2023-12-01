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
- [x] Maybe GraalVM native image

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

# Endpoints
Http server @ localhost:8080

| Endpoint          | Description        |
|-------------------|--------------------|
| /api/graphql      | GraphQL api        |
| /graphiql         | GraphiQL           |
| /metrics          | Prometheus metrics |
| /health/liveness  | Liveness           |
| /health/readiness | Readiness          |

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

Requires local graalvm jdk. Tested with graalvm-ce-java17-22.3.2. 

Create image `sbt graalvm-native-image:packageBin`.
