package typeclasses

import shapeless.{::, Generic, HList, HNil, Lazy}

trait Show[-T] {
  def show(in: T): String
}

object Show {
  // Apply method allows to nicely invoke the Show[T] avoiding the whole implicitly[Show[T]]
  def apply[T : Show]: Show[T] = implicitly[Show[T]]

  // Basic types,
  // since we have only one method Scala allows us to rewrite
  // the implementation as an Abstract Function
  implicit val stringShow: Show[String] = new Show[String] {
    override def show(in: String): String = in
  }
  implicit val intShow: Show[Int] = (in: Int) => in.toString
  implicit val doubleShow: Show[Double] = (in: Double) => in.toString
  implicit val floatShow: Show[Float] = (in: Float) => in.toString
  implicit def iterableOnce[T : Show]: Show[IterableOnce[T]] =
    (in: IterableOnce[T]) => in.iterator.map(Show[T].show).mkString("[",",","]")

  // This would be a Show implementation for any type that already implements our ToString
  // This is a common practice to extend compose with other case classes,
  // imagine we have an type class that converts from A to B and another from B to C, you can easily
  // create the A to C using this approach.
  implicit def toStringShow[T : ToString]: Show[T] =
    (in: T) => ToString[T].toString(in)

  // Extercise to do:
  //    Create a type class that converts from T to MongoObject..
  //    Create one implementation of the previous one that converts from circe.Json to MongoObject
  //    Define case classes that have circe.encoder and enjoy the free convertion


  // Check: https://www.youtube.com/watch?v=Zt6LjUnOcFQ for more details of all this, specially the auto derivation
  implicit val showHNil: Show[HNil] = (in: HNil) => ""

  implicit def showList[H, T <: HList](
    implicit
    showHead: Lazy[Show[H]],
    showTail: Show[T]
  ): Show[H :: T] = new Show[H :: T] {
    override def show(in: H :: T): String =
      in match {
        case head :: tail =>
          val headStr = showHead.value.show(head)
          val tailStr = showTail.show(tail)
          s"${headStr} | ${tailStr}"
      }
  }

  implicit def showGeneric[A, L](
    implicit
    gen: Generic.Aux[A, L],
    showA: Lazy[Show[L]]
  ): Show[A] = (in: A) => showA.value.show(gen.to(in))

  // Generic show method to be used as Show.show(X)
  def show[T](in: T)(implicit ev: Show[T]): String = ev.show(in)
}

object ShowExtras {

  // Pimp My Library Pattern, adding a post fix show method to any type there is an implementation of Show in scope:
  // This implies we can use 1.show as supposed to Show.show(1) or Show[T].show(in)
  implicit class showOps[T : Show](in: T) {
    def show: String = Show[T].show(in)
  }
}

object Logger {
  import ShowExtras._

  def log[T](in: T)(implicit ev: Show[T]): Unit =
    println(s"log: ${in.show}")
}

object Test extends App {
  case class Aloha(str: String, int: Int)

  Logger.log("aloha")
  Logger.log(1)
  Logger.log(2f)
  Logger.log(2.0)
  Logger.log(List(1,2,3,4))
  Logger.log(Some(1))
  Logger.log(None:Option[Int])
  Logger.log((1, "String"))
  Logger.log(Aloha("String", 1))
}