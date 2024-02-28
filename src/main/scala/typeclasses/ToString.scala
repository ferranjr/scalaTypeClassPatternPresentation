package typeclasses

import org.bson.types.ObjectId

import java.util.UUID


trait ToString[T] {
  def toString(in: T): String
}

object ToString {
  def apply[T : ToString]: ToString[T] = implicitly[ToString[T]]

  implicit val uuidToString: ToString[UUID] =
    (in: UUID) => in.toString

  implicit val mongoObjectId: ToString[ObjectId] =
    (in: ObjectId) => in.toHexString
}