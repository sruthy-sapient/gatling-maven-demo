package testpackage

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CsvFeeder extends Simulation {
  val httpConf = http.baseUrl("https://jsonplaceholder.typicode.com")
    .header("Accept", "application/json")

  val csvFeeder = csv("data/dataCsv.csv").circular

  def getSpecificData() = {
    repeat(4) {
      feed(csvFeeder)
        .exec(http("getting one data")
          .get("/posts/${id}")
          .check(status.in(200 to 399))
          .check(jsonPath("$.title").is("${title}"))
          .check(bodyString.saveAs("responseBody")))
        .exec {
          session => println(session("responseBody").as[String]); session
        }
    }

  }

  val scn = scenario("Validating CSV Feeder")
      .exec(getSpecificData())

  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))
}
