package typeclasses

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ShowSpec
  extends AnyFlatSpec
    with Matchers {

  "Show.show" should "display a string for basic types" in {

    Show.show("foobar") shouldBe "foobar"
    Show.show(2f) shouldBe "2.0"
    Show.show(1) shouldBe "1"
    Show.show(2.1) shouldBe "2.1"
  }

  it should "work for iterableOnce" in {
    Show.show(None:Option[String]) shouldBe "[]"
    Show.show(Some("Aloha")) shouldBe "[Aloha]"
    Show.show(List(1, 2 , 3)) shouldBe "[1,2,3]"
  }
}
