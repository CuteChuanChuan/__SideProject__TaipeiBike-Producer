package repositories

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repositories.BikeDataLoader

class BikeDataLoaderSpec extends AnyFlatSpec with Matchers {
  
  "BikeDataLoader" should "load data" in {
    val loadResult = BikeDataLoader.loadData
    
    loadResult match {
      case Right(json) =>
        json.isArray shouldBe true
        json.asArray.get.size should be > 0
      case Left(error) => fail(error)
    }
  }

}
