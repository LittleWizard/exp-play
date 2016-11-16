package utils

import play.api.mvc.Result


object StaticContent {

  val cssHeaderName = "X-CSS"
  val jsHeaderName = "X-JS"

  def asHeaders(css: Seq[String], js: Seq[String]): Seq[(String, String)] = {
    Seq(cssHeaderName -> css.mkString(","), jsHeaderName -> js.mkString(","))
  }

  def mergeCssHeaders(results: Result*): Seq[String] = {
    mergeHeaderValues(cssHeaderName, parseCssHeader, results:_*)
  }


  def mergeJsHeaders(results: Result*): Seq[String] = {
    mergeHeaderValues(jsHeaderName, parseJsHeader, results:_*)
  }

  private def mergeHeaderValues(headerName: String, parseHeader: Result => Seq[String], results: Result*): Seq[String] = {
    results.flatMap(parseHeader).distinct
  }

  def parseCssHeader(result: Result): Seq[String] = parseHeader(cssHeaderName, result)

  def parseJsHeader(result: Result): Seq[String] = parseHeader(jsHeaderName, result)

  private def parseHeader(headerName: String, result: Result): Seq[String] = {
    result.header.headers.get(headerName).map(_.split(",").toVector).getOrElse(Vector.empty)
  }
}
