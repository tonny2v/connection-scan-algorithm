package parser.ratp

case class Route(routeId: Long, routeShortName: String, routeLongName: String, routeDesc: String)

object Route {
  def parse(fields: Map[String, String]) = {
    Route(
      fields("route_id").toLong,
      fields("route_short_name"),
      fields("route_long_name"),
      fields("route_desc")
    )
  }
}
