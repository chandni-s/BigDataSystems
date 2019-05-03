for f in *.csv
do
    filename=$(basename "$f")
    extension="${filename##*.}"
    filename="${filename%.*}"
    /usr/local/Cellar/mongodb/4.0.3_1/bin/mongoimport --host gettingstarted-shard-00-00-vu2fs.azure.mongodb.net:27017,gettingstarted-shard-00-01-vu2fs.azure.mongodb.net:27017,gettingstarted-shard-00-02-vu2fs.azure.mongodb.net:27017 --ssl --username chani --password chani --authenticationDatabase admin -d Northwind --collection "$filename" --type csv --file "$f" --headerline
done