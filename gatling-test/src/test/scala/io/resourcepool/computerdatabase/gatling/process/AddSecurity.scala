package io.resourcepool.computerdatabase.gatling.process

import java.util.UUID

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import com.typesafe.config.ConfigFactory

/**
  * Created by Cédric Cousseran on 29/03/16.
  * Process to add a computer when Spring Security is enabled.
  */
object AddSecurity {
  val config = ConfigFactory.load

  val feederName = Iterator.continually(Map("addComputerName" -> UUID.randomUUID))

  val feederAdd = csv("data/addComputer.csv").random

  val add = exec {
    http("AddSecurity: Add page")
      .get(config.getString("application.urls.addPage")).check(status.is(200))
      .check(
        css(config.getString("application.urls.idElement.add.csrf").get, "value").saveAs("csrf_token"))
  }.exitHereIfFailed
    .pause(3, 10)
    .feed(feederName)
    .feed(feederAdd)
    .exec {
      http("AddSecurity: Add post")
        .post(config.getString("application.urls.addPost").get)
        .formParam(config.getString("application.urls.form.add.name").get, "${addComputerName}")
        .formParam(config.getString("application.urls.form.add.introduced").get, "${addComputerIntroduced}")
        .formParam(config.getString("application.urls.form.add.discontinued").get, "${addComputerDiscontinued}")
        .formParam(config.getString("application.urls.form.add.companyId").get, "${addComputerCompany}")
        .formParam(config.getString("application.urls.form.add.csrf").get, "${csrf_token}")
    }.pause(3, 10)
}
