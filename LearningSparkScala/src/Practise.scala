object Practise {

  def variables: Unit = {
    // var is mutable
    var a: Int = 0
    println("Var a: " + a)

    // val is immutable - cannot change
    val b: Int = 42
    println("Val b: " + b)

    // double
    var c = 12.5
    println("Double c: " + c)

    // float
    var d = 13.5f
    println("Float d: " + d)

    // the last expression is always evaluated as operation and its value gets assigned to variable
    val x = {val aa = 5; val bb = 10; val c = 20; a+b+c}
    println("Multiple expression x: " + x)

    // lazy initialization of variable: allocate memory to variable only when its used. if unused, no mem alloc
    lazy val y = 1000;
    println("Lazy init y " + y )
  }

  def stringInterpolation: Unit = {

    val name = "Barbara"
    val age = 28

    // String concatenation
    println("Person name: " + name + " age is " + age)

    // String interpolation
    println(s"Person name $name and \n age is $age")

    // String interpolation type safe - if someone tries to change age from int to decimal or float (use %f)
    // - due to type safe it'll become invalid
    println(f"Person name $name%s age is $age%d old")

    // raw wont escape any special characters
    println(raw"this wont escape any special \n char")

  }

  def doWhile : Unit = {

    var x = 0

    // do-while, the code executes at least once as it runs do before evaluating while
    do {
      println(f"x is $x%d")
      x += 1
    } while (x < 10)

  }

  def forLoop : Unit = {

    // TO is inclusive range from 0-5
    for (i <- 0 to 5) {
      println(f"i from 0 to 5: $i%d")
    }

    // Until is inclusive start, exclusive end
    for (i <- 0 until 6) {
      println(f"i from 0 Until 6 : $i%d")
    }

    // nested loop
    for (i <- 0.to(5); j <- 1.until(3)) {
      println(f"nested outerloop i: $i%d inner loop j: $j%d")
    }

    val lst = List (1, 2, 3, 4, 5, 6, 7, 8,33, 25, 263)
    // for loop as an expression - yield allows to store result into a new list and use it later - if is filtering
    val result = for {i <- lst; if i <5 } yield {
      // conditions to perform logic to create required new list
      i * i
    }
    println(s"new result list is: $result")

  }

  def main(Args: Array[String]) = {
    //variables
    //stringInterpolation
    // doWhile
    forLoop
  }

}
