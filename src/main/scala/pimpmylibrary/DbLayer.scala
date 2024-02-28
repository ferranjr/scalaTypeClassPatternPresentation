package pimpmylibrary

import scala.collection.mutable

trait DbLayer[T, ID] {
  def insert(in: T): ID
  def get(id: ID): Option[T]
}

trait HasId[T, ID] {
  def id(in: T): ID
}

class HashMapDB[T, ID](
  implicit ev: HasId[T, ID]
)
  extends DbLayer[T, ID] {

  private val map: mutable.HashMap[ID, T] = new mutable.HashMap[ID, T]()

  override def insert(in: T): ID = {
    val id = ev.id(in)
    map.addOne((id, in))
    id
  }

  override def get(id: ID): Option[T] = map.get(id)
}

case class Foobar(id: Int, string: String)

object HashMapDBOps {
  implicit class HashMapDbExtraOps[T, ID](in: DbLayer[T, ID]) {
    def getUnsafe(id: ID): T =
      in.get(id).getOrElse(throw new RuntimeException("You are an idiot!"))
  }
}

object Foobar {
  implicit val hasId: HasId[Foobar, Int] = new HasId[Foobar, Int] {
    override def id(in: Foobar): Int = in.id
  }
}

object Sample extends App {
  import HashMapDBOps._
  val storage = new HashMapDB[Foobar, Int]

  storage.insert(Foobar(1, "foobar"))
  println(storage.get(1))
  println(storage.getUnsafe(1))
}
