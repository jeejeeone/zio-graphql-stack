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