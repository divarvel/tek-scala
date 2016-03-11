package controllers

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.ws._

import models.finance_api._

@Singleton
class HomeController @Inject() (ws: WSClient) extends Controller {

  def index = Action {
    Ok("hello world")
  }

  def info = Action.async { request =>
    val stocks = request.queryString.get("stock") match {
      case Some(seq) => seq.toList
      case None      => Nil
    }

    val data = getResults(stocks, ws)

    def toResult(yahooResponse: YahooResponse): Result = {
      val fields = getFields(yahooResponse)
      Ok(
        Json.obj(
          "bestStocks" -> bestStocks(fields)
        )
      )
    }

    data map { result =>
      result map (toResult(_)) getOrElse InternalServerError
    }
  }

}
