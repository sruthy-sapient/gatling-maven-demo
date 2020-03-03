package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class DummyAllEmployees extends Simulation {

  // Http Conf
  val httpConf = http.baseUrl("http://dummy.restapiexample.com")

  // Scenario Def
  val scn = scenario("Sample Testing")
    .exec(http("Get all employee data")
      .get("/api/v1/employees"))

  // Load Scenario
  setUp(scn.inject(atOnceUsers(1))).protocols(httpConf)

}
