// adding an actual package here makes things work
// package sup

object Hi {
  def main(args: Array[String]) = {
    println("ohai!")

    // fails to compile (on my machine)
    val xs = Vector(1,2,3)

    // adding package-qualification instead makes things work
    //val xs = scala.collection.immutable.Vector(1,2,3)


    println("kthxbai!")
  }
}
