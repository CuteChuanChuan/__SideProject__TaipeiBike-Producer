package config

object MongoConfig {

  private val hostEnv                 = "MONGO_HOST"
  private val hostEnvDefault          = "mongodb://localhost:27017"
  private val dbEnv                   = "MONGO_DB"
  private val dbEnvDefault            = "bike_data"
  private val rawCollectionEnv        = "MONGO_COLLECTION_RAW"
  private val rawCollectionEnvDefault = "raw"
  private val aggCollectionEnv        = "MONGO_COLLECTION_AGGREGATE"
  private val aggCollectionEnvDefault = "aggregate"

  def loadMongoHost:     String = sys.env.getOrElse(hostEnv, hostEnvDefault)
  def loadMongoDB:       String = sys.env.getOrElse(dbEnv, dbEnvDefault)
  def loadRawCollection: String = sys.env.getOrElse(rawCollectionEnv, rawCollectionEnvDefault)
  def loadAggCollection: String = sys.env.getOrElse(aggCollectionEnv, aggCollectionEnvDefault)

}
