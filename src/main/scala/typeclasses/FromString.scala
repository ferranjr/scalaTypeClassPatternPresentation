package typeclasses
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{PathMatcher1, PathMatchers}
import org.bson.types.ObjectId

import java.util.UUID
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.Try

trait FromString[T] {
  def fromString(in: String): Try[T]
}

object FromString {
  def apply[T : FromString]: FromString[T] =
    implicitly[FromString[T]]

  implicit val uuidFromString: FromString[UUID] =
    (in: String) => Try(UUID.fromString(in))

  implicit val oidFromString: FromString[ObjectId] =
    (in: String) => Try(new ObjectId(in))
}

trait DirectivesExtraOpsAkkaHttp {
//
//  def pathMatcher[T](implicit fs: FromString[T]): PathMatcher1[T] =
//    PathMatchers.Segment.flatMap { path =>
//      fs.fromString(path).toOption
//    }

  implicit class SegmentAs(in: PathMatcher1[String]) {
    def as[T : FromString]: PathMatcher1[T] =
      in.flatMap(FromString[T].fromString(_).toOption)
  }

}

object Main extends App with DirectivesExtraOpsAkkaHttp {

    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val route =
      get {
        path("hello" / Segment.as[UUID]) { name =>
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Say hello to akka-http $name</h1>"))
        }
      }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
}