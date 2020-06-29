class Foo() {
  var x: String = ""
  def reversed: String = x.reverse

  def withX(newX: String) = {
    this.x = newX
    this
  }
}
