package data

import play.api.libs.json.Json


case class Response(id: String, delay: Long)

object Response {
  implicit val responseWrites = Json.writes[Response]
}
