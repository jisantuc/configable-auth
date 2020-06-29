object Test2 extends App {
  val foo: Test.Foo = new Test.Foo("abcde")
  println("Reversed: " ++ foo.reversed)
}
