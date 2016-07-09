from pyspark import SparkContext
from pyspark.sql.types import StringType
from pyspark.sql import SQLContext
from pyspark.sql.types import *
import numpy as np

# initialize environment
sc = SparkContext()
sqlContext = SQLContext(sc)
# specify data columns and untis
units = ['knot', 'in_Hg', 'celsius', 'degree', 'force_pound']
tounits = ['m/s', 'pascal', 'kelvin', 'radian', 'newton']
name = ['v','p','t','a','w']
# read data from file
data = sc.textFile("thetrain.csv")
data = data.filter(lambda line: line.strip()) # remove empty lines
# read header and data
header = data.first()
header = map(lambda(x): x.strip(), header.split(','))
print header
data = data.filter(lambda line: line != header)
raw_data = data.map(lambda line: map(float, line.split(',')))
print raw_data
# set data tag
fields = [StructField(field_name, DoubleType(), True) for field_name in name]
schema = StructType(fields)
schema_data = sqlContext.createDataFrame(raw_data, schema)
schema_data.registerTempTable("thetrain")
# make unit transformer

# make data transformer
