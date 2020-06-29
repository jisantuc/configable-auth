import java.io.File
import java.net.URLClassLoader

trait HasConvertibleX {
  var x: String
  def withX(newX: String): HasConvertibleX = {
    this.x = newX
    this
  }

  def reversed: String =
    this.x.reverse
}

object Loader extends App {

  val url = new File(args.head).toURI.toURL
  val className = args.drop(1).head
  println(s"Url is: $url")
  val loader = new URLClassLoader(Array(url))
  println("loader created")
  val cls = loader.loadClass(className)
  println(s"""class name: ${cls.getName}""")
  val foo = cls.newInstance()
  println(s"Made a foo: $foo")
  val casted = foo.asInstanceOf[HasConvertibleX]
  casted.withX("wow a new string")
  println(s"Foo reversed: ${casted.reversed}")
}
