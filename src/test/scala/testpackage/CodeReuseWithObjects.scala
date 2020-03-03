package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CodeReuseWithObjects extends Simulation {

  val httpConf = http.baseUrl("https://jsonplaceholder.typicode.com")
    .header("Accept", "application/json")


  def getSomeData() = {
    repeat(4) {
      exec(http("getting some data")
        .get("/posts")
        .check(status.in(200 to 399))
          .check()
        .check(bodyString.saveAs("responseBody")))
        .exec {
          session => println(session("responseBody").as[String]); session
        }
        .pause(1)
    }
  }

  def getSingleData() = {
    exec(http("gettinf single data")
      .get("/posts/2")
      .check(status.in(200 to 210))
      .check(bodyString.saveAs("responseBody")))
      .exec { session => println(session("responseBody").as[String]); session
      }
  }

  val scn = scenario("code reuse")
    .exec(getSomeData())
    .exec(getSingleData())

  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))
}
