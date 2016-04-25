/**
  * Created by aphex on 4/25/16.
  */

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import shapeless.HList._
import shapeless._
import shapeless.syntax.std.traversable._

import scala.language.postfixOps

case class CurrencySymbol(flag: String,
                          country: String,
                          name: String,
                          iso: String,
                          symbol: String,
                          hexSymbol: String)

object Main extends App {
  val browser = JsoupBrowser()
  val HOME = "http://www.currencysymbols.in/"
  val doc = browser.get(HOME)

  // Extract the main currency table (there should be only one)
  val table: List[Element] = doc >> elementList("#main > table")

  // From the table, extract each row
  val tableRows: List[Element] = table >> elementList("tr") flatten

  // Skip the first row, because it's just headers
  tableRows.tail foreach { tr =>
    // lol, this dumb site has all the tables cells in <th> tags for some inscrutable reason
    val cells: List[Element] = tr >> elementList("th")

    val imageUrl: String = HOME + (cells.head >> attr("src")("img"))

    val rest: List[String] = cells.tail map (text(_))

    val strings: List[String] = imageUrl +: rest

    // this is just a fancy way of globbing all the strings into the case class
    val cs = CurrencySymbol.tupled(strings.toHList[String :: String :: String :: String :: String :: String :: HNil].get.tupled)

    println(cs)
  }

}
