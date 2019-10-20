// Databricks notebook source
// Goal: Predict Price of a stock (Price = last amount that was paid by an investor during a trade)
// /FileStore/tables/usa_stocks_priceVol_11102017.csv
// /FileStore/tables/one_comp.csv
// /FileStore/tables/Test.csv
val sparkDF = spark.read.format("csv")
.option("header", "true")
.option("inferSchema", "true")
.load("/FileStore/tables/NASDAQ_all_yrs.csv")

// COMMAND ----------

// Register spark SQL tables

sparkDF.createOrReplaceTempView("sparkDF")

// COMMAND ----------

display(sparkDF)

// COMMAND ----------

// Table projections
sparkDF.select("Date", "High", "Low", "Open", "Close", "Volume").show(5)

// COMMAND ----------

// MAGIC %sql 
// MAGIC DROP TABLE IF EXISTS cleaned_stocks;
// MAGIC 
// MAGIC CREATE TABLE cleaned_stocks AS
// MAGIC SELECT date(date),
// MAGIC   int(Volume) as Volume, 
// MAGIC   double(High) as High,
// MAGIC   double(Low) as Low,
// MAGIC   double(Open) as Open,
// MAGIC   double(Close) as Close
// MAGIC FROM sparkDF

// COMMAND ----------

// Using the dataset API
val cleanedStocks = spark.table("cleaned_stocks")
display(cleanedStocks.groupBy("date").avg("Close"))

// COMMAND ----------

display(cleanedStocks)

// COMMAND ----------

// find out the most profitable day of the year for NASDAQ and by how much

val combo = spark.sql("""
  SELECT date, 
    AVG(Open) - AVG(Close) as AvgGainLoss
  FROM cleaned_stocks 
  WHERE NOT (open = 100 or close = 100) and date > '2000-01-01' 
  GROUP BY date
  ORDER BY AvgGainLoss DESC
  limit 500""")

display(combo)

// COMMAND ----------

sqlContext.cacheTable("cleaned_stocks")

// COMMAND ----------

// Convert back to a dataset from a table
val cleanedStocks = spark.sql("SELECT * FROM cleaned_stocks")
val tickerStocks = cleanedStocks
  .groupBy("date")
  .avg()
  .na.drop()  //na.drop() will return rows where all values are non-null.

display(tickerStocks)

// COMMAND ----------

val newDF = sparkDF.selectExpr("cast(Date as int) year", 
                        "High", 
                        "Low", 
                        "Open", 
                        "Close",
                        "Volume")
// deal with na values
val df2 = newDF.na.fill(0)


val Array(trainingData, testData) = df2.randomSplit(Array(0.7, 0.3))
// Going to cache the data to make sure things stay snappy!
trainingData.cache()
testData.cache()

// df2.printSchema()
// // based on high, low, open - predict the next highest closing price for nasdaq
val featureCols = Array("High", "Low", "Open", "year")

//We define the assembler to collect the columns into a new column with a single vector - "features"
val assembler = new VectorAssembler()
  .setInputCols(featureCols)
  .setOutputCol("features")

// import org.apache.spark.ml.regression.LinearRegression
val lrModel = new LinearRegression()
  .setLabelCol("Close")       // setting label column
  .setFeaturesCol("features") // setting features column
  .setElasticNetParam(0.5)


//creating pipeline
import org.apache.spark.ml.{Pipeline, PipelineStage}
val pipeline = new Pipeline().setStages(Array(assembler,lrModel))

// //fitting the model
val lr = pipeline.fit(trainingData)

// COMMAND ----------

val holdout = lr
  .transform(testData)
  .selectExpr("prediction as raw_prediction", 
    "double(round(prediction)) as prediction", 
    "Close", 
    """CASE double(round(prediction)) = Close 
  WHEN true then 1
  ELSE 0
END as equal""")
display(holdout)

// COMMAND ----------

display(holdout.selectExpr("sum(equal)/sum(1)"))

// COMMAND ----------

import org.apache.spark.mllib.evaluation.RegressionMetrics

val rm = new RegressionMetrics(
  holdout.select("prediction", "Close").rdd.map(x =>
  (x(0).asInstanceOf[Double], x(1).asInstanceOf[Double])))

println("MSE: " + rm.meanSquaredError)
println("MAE: " + rm.meanAbsoluteError)
println("RMSE Squared: " + rm.rootMeanSquaredError)
println("R Squared: " + rm.r2)
println("Explained Variance: " + rm.explainedVariance + "\n")


// COMMAND ----------

import org.apache.spark.ml.regression.RandomForestRegressor
import org.apache.spark.ml.tuning.{ParamGridBuilder, CrossValidator}

import org.apache.spark.ml.evaluation.RegressionEvaluator

import org.apache.spark.ml.{Pipeline, PipelineStage}

val rfModel = new RandomForestRegressor()
  .setLabelCol("Close")
  .setFeaturesCol("features")

val paramGrid = new ParamGridBuilder()
  .addGrid(rfModel.maxDepth, Array(5, 10))
  .addGrid(rfModel.numTrees, Array(20, 60))
  .build()

val steps:Array[PipelineStage] = Array(assembler, rfModel)

val pipeline = new Pipeline().setStages(steps)
//val pipeline = new Pipeline().setStages(Array(assembler,lrModel))

val cv = new CrossValidator() // you can feel free to change the number of folds used in cross validation as well
  .setEstimator(pipeline) // the estimator can also just be an individual model rather than a pipeline
  .setEstimatorParamMaps(paramGrid)
  .setEvaluator(new RegressionEvaluator().setLabelCol("Close"))

val pipelineFitted = cv.fit(trainingData)

// COMMAND ----------

println("The Best Parameters:\n--------------------")
println(pipelineFitted.bestModel.asInstanceOf[org.apache.spark.ml.PipelineModel].stages(0))
pipelineFitted
  .bestModel.asInstanceOf[org.apache.spark.ml.PipelineModel]
  .stages(0)
  .extractParamMap

// COMMAND ----------

val holdout2 = pipelineFitted.bestModel
  .transform(testData)
  .selectExpr("prediction as raw_prediction", 
    "double(round(prediction)) as prediction", 
    "Close", 
    """CASE double(round(prediction)) = Close 
  WHEN true then 1
  ELSE 0
END as equal""")
display(holdout2)

// COMMAND ----------

display(holdout2.selectExpr("sum(equal)/sum(1)"))

// COMMAND ----------

// have to do a type conversion for RegressionMetrics
val rm2 = new RegressionMetrics(
  holdout2.select("prediction", "Close").rdd.map(x =>
  (x(0).asInstanceOf[Double], x(1).asInstanceOf[Double])))

println("MSE: " + rm2.meanSquaredError)
println("MAE: " + rm2.meanAbsoluteError)
println("RMSE Squared: " + rm2.rootMeanSquaredError)
println("R Squared: " + rm2.r2)
println("Explained Variance: " + rm2.explainedVariance + "\n")

// COMMAND ----------


