package algo

import scala.annotation.tailrec
import scala.io.Source

/**
 * FIXME Rewrite this in a more functionnal way.
 */
case class CSA(timetable: Timetable) {
  val inConnection = Array.fill[Int](CSA.MaxStations)(Int.MaxValue)
  val earliestArrival = Array.fill[Int](CSA.MaxStations)(Int.MaxValue)

  private def loop(arrivalStation: Int): Unit = {
    var earliest = Int.MaxValue

    @tailrec
    def inner(conns: Seq[(Connection, Int)]): Unit = {
      conns match {
        case Seq() =>
          ()
        case (connection, index) +: _ if connection.arrivalTimestamp > earliest =>
          ()
        case (connection, index) +: tail =>
          if (leavesAfterArrival(connection) && optimizesArrivalTime(connection)) {
            earliestArrival(connection.arrivalStation) = connection.arrivalTimestamp
            inConnection(connection.arrivalStation) = index
            if (connection.arrivalStation == arrivalStation) {
              earliest = Math.min(earliest, connection.arrivalTimestamp)
            }
          }
          inner(tail)
      }
    }
    inner(timetable.connections.zipWithIndex)
  }

  def leavesAfterArrival(connection: Connection): Boolean = {
    connection.departureTimestamp >= earliestArrival(connection.departureStation)
  }

  def optimizesArrivalTime(connection: Connection): Boolean = {
    connection.arrivalTimestamp < earliestArrival(connection.arrivalStation)
  }

  private def printResult(arrivalStation: Int): Unit = {
    inConnection(arrivalStation) match {
      case Int.MaxValue => println("No solution")
      case _ => {
        var route = Array[Connection]()
        var lastConnectionIndex = inConnection(arrivalStation)
        while (lastConnectionIndex != Int.MaxValue) {
          val connection: Connection = timetable.connections(lastConnectionIndex)
          route = route :+ connection
          lastConnectionIndex = inConnection(connection.departureStation)
        }
        route.reverse.foreach { connection =>
          println(connection.toString)
        }
      }
    }
  }

  def compute(departureStation: Int, arrivalStation: Int, departureTime: Int) = {
    earliestArrival(departureStation) = departureTime

    if (departureStation <= CSA.MaxStations && arrivalStation <= CSA.MaxStations) {
      loop(arrivalStation)
    }

    printResult(arrivalStation)
  }

}

object CSA {
  val MaxStations = 5000000

  def main(args: Array[String]) {
    val timetable = Timetable.parse(Source.fromFile("src/main/resources/bench_data_48h").getLines())
    val csa: CSA = CSA(timetable)
    csa.compute(29377, 29458, 0)
  }
}