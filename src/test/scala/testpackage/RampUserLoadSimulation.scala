package testpackage

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class RampUserLoadSimulation extends Simulation {

  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  def userCount: Int = getProperty("USERS", "3").toInt

  def rampUser: Int = getProperty("RAMP_USERS", "6").toInt

  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt

  before {
    println(s"user count: ${userCount}")
    println(s"ramp users: ${rampUser}")
    println(s"ramp duration: ${rampDuration}")
  }

  after(
    println("Tests are completed")
  )

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
    exec(http("getting single data")
      .get("/posts/2")
      .check(status.in(200 to 210))
      .check(bodyString.saveAs("responseBody")))
      .exec { session => println(session("responseBody").as[String]); session
      }
  }

  val scn = scenario("Load Testing")
    .exec(getSomeData())
    .exec(getSingleData())

  setUp(
    scn.inject(
      nothingFor(5 seconds),
      atOnceUsers(userCount),
      rampUsers(rampUser).during(rampDuration seconds)
    )
      //      inferHtmlResources --> gatling will download css or javascript that is available in the page
      .protocols(httpConf.inferHtmlResources())
  )

}
