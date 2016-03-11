package models

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import play.api.libs.json._
import play.api.libs.ws._

object finance_api {
  case class ResultsMetadata(
    `type`: String,
    start: Double,
    count: Double
  )
  case class Fields(
    change: BigDecimal,
    chg_percent: BigDecimal,
    day_high: BigDecimal,
    day_low: BigDecimal,
    issuer_name: String,
    issuer_name_lang: String,
    name: String,
    price: BigDecimal,
    symbol: String,
    ts: BigDecimal,
    `type`: String,
    utctime: String,
    volume: BigDecimal,
    year_high: BigDecimal,
    year_low: BigDecimal
  )
  case class Resource(
    classname: String,
    fields: Fields
  )
  case class Resources(
    resource: Resource
  )
  case class ResultsList(
    meta: ResultsMetadata,
    resources: List[Resources]
  )
  case class YahooResponse(
    list: ResultsList
  )

  implicit val resultsMetadataFormat = Json.format[ResultsMetadata]
  implicit val fieldsFormat = Json.format[Fields]
  implicit val resourceFormat = Json.format[Resource]
  implicit val resourcesFormat = Json.format[Resources]
  implicit val resultsListFormat = Json.format[ResultsList]
  implicit val yahooResponseFormat = Json.format[YahooResponse]

  def parseYahooResponse(js: JsValue): Option[YahooResponse] = js.validate[YahooResponse].asOpt

  def getResults(stocks: List[String], ws: WSClient): Future[Option[YahooResponse]] = {
    val stockList = stocks.mkString(",")
    val request =
      ws.url("http://finance.yahoo.com/webservice/v1/symbols/" + stockList + "/quote")
        .withHeaders("Accept" -> "application/json")
        .withRequestTimeout(10000.millis)
        .withQueryString("format" -> "json", "view" -> "detail")

    val response = request.get()


    response map (_.json) map (parseYahooResponse _)
  }

  def getFields(yahooResponse: YahooResponse): List[Fields] = yahooResponse.list.resources.map(_.resource.fields)

  def bestStocks(stocks: List[Fields]): List[Fields] = stocks.sortBy(_.price).reverse
  def risingStocks(stocks: List[Fields]): List[Fields] = ???
  def fallingStocks(stocks: List[Fields]): List[Fields] = ???

  def bestStock(stocks: List[Fields]): Option[Fields] = ???
  def meanPrice(stocks: List[Fields]): BigDecimal = ???

  // …

}
