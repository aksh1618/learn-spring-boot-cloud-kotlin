curl -s localhost:8080/limits | fx .
curl -s localhost:8080/limits | fx .
curl -s localhost:8080/limits | fx .
curl -s localhost:8888/limits-service/default | fx .
curl -s localhost:8888/limits-service/qa | fx .
curl -s localhost:8080/limits | fx .
curl -s localhost:8080/limits | fx .
curl -s localhost:8000/from/a/to/b | fx .
curl -i localhost:8000/from/a/to/b
curl -s localhost:8000/from/USD/to/INR | fx .
curl -s localhost:8100/from/USD/to/INR/quantity/1 | fx .
curl -s localhost:8100/from/USD/to/INR/quantity/35 | fx .
curl -is localhost:8100/from/USD/to/INR/quantity/35
curl -is localhost:8100/from/USD/to/INR/quantity/35
curl -is localhost:8100/from/USD/to/INR/quantity/35
curl -is localhost:8765/currency-conversion-service/from/USD/to/INR/quantity/35
curl -is localhost:8765/currency-conversion-service/from/USD/to/INR/quantity/35
# Starting zipkin server with already running rabbitmq
docker run -d -p 9411:9411 -e RABBIT_ADDRESS=localhost openzipkin/zipkin
curl -s localhost:8080/limits | fx .
curl -isX POST localhost:8080/actuator/bus-refresh
curl -s localhost:8081/limits | fx .
