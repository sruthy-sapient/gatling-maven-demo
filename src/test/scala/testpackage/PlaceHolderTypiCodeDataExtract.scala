package testpackage

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class PlaceHolderTypiCodeDataExtract extends Simulation {
  val httpconf = http.baseUrl("https://jsonplaceholder.typicode.com")
    .header("Accept", "application/json")

  val scn = scenario("check extraction of data")

    .exec(http("getting some data")
      .get("/posts")
      .check(status.is(200))
      .check(jsonPath("$[1].id").saveAs("userId")))
    //  for debugging
    .exec { session => println(session); session }

    .exec(http("getting one data")
      .get("/posts/${userId}")
      .check(status.is(200))
      .check(jsonPath("$.title").is("qui est esse"))
      .check(bodyString.saveAs("responseBody")))
    //  for printing response body
    .exec { session => println(session("responseBody").as[String]); session }

  setUp(scn.inject(atOnceUsers(1)).protocols(httpconf))
}
