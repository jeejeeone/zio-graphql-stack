package com.zap.http

import zio.*
import zio.http.*

// Practice

object HelloWorld extends ZIOAppDefault:
  case class Zup(v: String)

  val service = ZIO.serviceWith[Zup](v => Response.text(v.toString))

  val ok = Handler.fromZIO(service)

  val textRoute = Method.GET / "text" -> ok.provideLayer(ZLayer.succeed(Zup("zop")))

  val zop = handler {
    (r: Request) => Response.json("""{"greetings": "Hello World!"}""")
  }

  val zup = handler {
    (r: Request) => zop
  }.flatten

  val jsonRoute = Method.GET / "json" -> handler {
    (r: Request) => Response.json("""{"greetings": "Hello World!"}""")
  }

  // Create HTTP route
  val app = Routes(textRoute, jsonRoute).toHttpApp

  // Run it like any simple app
  override val run = Server.serve(app).provide(Server.default)
