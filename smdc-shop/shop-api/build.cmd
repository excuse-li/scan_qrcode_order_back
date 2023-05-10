mvn clean
mvn install
docker rmi -f shop-api:1.0
docker build -t shop-api:1.0 .
