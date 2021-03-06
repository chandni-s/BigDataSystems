import org.apache.spark.{SparkConf, SparkContext}

object TestSpark {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("SparkTest").setMaster("local")

    val sc = new SparkContext(conf)

    // create new RDD
        val text = sc.textFile("in/test.txt")
        val counts = text.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey(_+_)
        counts.saveAsTextFile("output")
  }

}
