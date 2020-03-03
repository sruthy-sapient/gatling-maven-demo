package testpackage

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class PlaceHolderTypiCode extends Simulation {

  val httpConf = http.baseUrl("https://jsonplaceholder.typicode.com")
    .header("Accept", "application/json")

  val scn = scenario("Random Open API")

    .exec(http("getting some data")
      .get("/posts")
      .check(status.is(200))
      .check(status.in(200 to 210))
      .check(jsonPath("$..title").exists))
    .pause(3)

    .exec(http("gettinf single data")
      .get("/posts/2")
      .check(status.not(400), status.in(200 to 210))
      .check(jsonPath("$.title").is("qui est esse")))
    .pause(1, 7)

  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))

}
