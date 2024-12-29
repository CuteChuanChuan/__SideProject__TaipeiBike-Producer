package app

import org.slf4j.{ Logger, LoggerFactory }
import repositories.{ BikeDataLoader, BikeDataWriter }

import java.util.{ Timer, TimerTask }

object Producer {

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  def main(array: Array[String]): Unit = {
    val timer = new Timer()

    timer.scheduleAtFixedRate(
      new TimerTask {
        override def run(): Unit = {
          logger.info("Starting data processing...")

          BikeDataLoader.loadData match {
            case Right(data) =>
              BikeDataWriter.writeData(data) match {
                case Right(result) => logger.info(s"Success to write data to DB: $result")
                case Left(error)   => logger.error(s"Fail to write data to DB: $error")
              }
            case Left(error) => logger.error(s"Fail to get data from API: $error")
          }

          logger.info("Processing completed.")
        }
      },
      0,
      60000
    )
  }

}
