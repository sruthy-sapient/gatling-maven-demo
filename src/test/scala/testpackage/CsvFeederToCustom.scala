package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CsvFeederToCustom extends Simulation {

  val httpConf = http.baseUrl("https://jsonplaceholder.typicode.com")
    .header("Accept", "application/json")
//    proxy if traffic needs to be recoded and viewed in swagger
//    .proxy(Proxy("localhost",8080))

  val idNumber = (1 to 4).iterator
  var title = List("sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
  "qui est esse",
  "ea molestias quasi exercitationem repellat qui ipsa sit aut",
  "eum et est occaecati").iterator

  val customFeeder = Iterator.continually(Map(
    "id" -> idNumber.next(),
    "title" -> title.next()
  ))

  def getSpecificData() = {
    repeat(4) {
      feed(customFeeder)
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
