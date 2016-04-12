package services
import reactivemongo.api.MongoDriver
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
/**
  * Created by knoldus on 10/4/16.
  */
class DBManager {
  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))
  val db = Await.result(connection.database("employee"), 10.seconds)

}
object DBManager extends DBManager
