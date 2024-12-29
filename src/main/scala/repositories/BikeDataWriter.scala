package repositories

import config.MongoConfig
import org.mongodb.scala._
import io.circe.Json
import org.bson.Document

import scala.concurrent.Await
import java.time.Instant
import java.time.format.DateTimeFormatter
import scala.concurrent.duration.DurationInt
import scala.util.{ Failure, Success, Try }
import org.slf4j.{ Logger, LoggerFactory }

trait DataWriter {
  def writeData(data: Json): Either[String, String]
}

object BikeDataWriter extends DataWriter {

  private val logger:              Logger = LoggerFactory.getLogger(getClass)
  private val mongoHost:           String = MongoConfig.loadMongoHost
  private val mongoDB:             String = MongoConfig.loadMongoDB
  private val rawCollection:       String = MongoConfig.loadRawCollection
  private val aggregateCollection: String = MongoConfig.loadAggCollection

  private val mongoClient:            MongoClient               = MongoClient(mongoHost)
  private val mongoDatabase:          MongoDatabase             = mongoClient.getDatabase(mongoDB)
  private val mongoRawDataCollection: MongoCollection[Document] =
    mongoDatabase.getCollection(rawCollection)

  override def writeData(data: Json): Either[String, String] =
    Try {
      val idx      = getCurrentTimeUTC
      val document = transformData(data)

      val observable = mongoRawDataCollection.insertOne(document)
      val result     = Await.result(observable.toFuture(), 10.seconds)
      println(result)

      s"Data inserted successfully with _id: $idx."
    } match {
      case Success(value)     => Right(value)
      case Failure(exception) => Left(exception.getMessage)
    }

  private def getCurrentTimeUTC: String =
    DateTimeFormatter.ISO_INSTANT.format(Instant.now())

  private def transformData(jsonArray: Json): Document = {
    val stations = jsonArray.asArray.getOrElse(Vector.empty)
    val document = new Document()
    val idx      = getCurrentTimeUTC
    document.put("_id", idx)
    document.put("numStations", stations.size)
    document.put("createdAt", idx)

    var numStationAvailable:   Int = 0
    var numStationUnavailable: Int = 0

    stations.foreach { station =>
      val sno = station.hcursor.get[String]("sno").getOrElse("")
      if (sno.nonEmpty) {
        val parsedDoc = Document.parse(s"""{"array": ${station.noSpaces}}""")
        val arrayData = parsedDoc.get("array")
        document.put(sno, arrayData)
      }
      station.hcursor.get[String]("act").getOrElse("") match {
        case "1" => numStationAvailable += 1
        case "0" => numStationUnavailable += 1
        case _   => None
      }
    }
    document.put("numStationsAvailable", numStationAvailable)
    document.put("numStationsUnavailable", numStationUnavailable)
    document
  }
}
