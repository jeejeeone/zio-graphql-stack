# General

PoC for a GraphQL microservice. Explore Scala 3, particularly braceless syntax.

Following capabilities:
- Federation support
- Handle n+1 queries problem
- Query ClickHouse
  - Custom decoders for CH datatypes
  - Automatic decoding to Scala types 
- Distributed tracing
- Maybe caching with Redis
- Maybe GraalVM native image

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