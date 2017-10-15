# ride-share-service

ride-share-service is a http service that responds with census data based on zip code.


[![Play](https://img.shields.io/badge/play-v2.6.3-yellow.svg)](https://www.playframework.com)  

The ride-share-service application is a fully REST application.

    git clone git@gitlab.com:jaycain/ride-share-service.git
    cd ride-share-service

Then run sbt.

    sbt run ( or sbt if you have it setup)
    open http://localhost:9000

## Testing

#### Test and Code coverage

For testing and code coverage run the following commands.

    sbt clean coverage test
    sbt coverageReport
    

You can look at coverage metrics in the /ride-share-service/target/scala-2.12/scoverage-report/index.html

## Docker

If using a MAC make sure you have the [Docker Tools](https://www.docker.com/toolbox) package installed.

#### Building Docker Instance
    sbt docker:publishLocal

#### Running docker instance

    Run ride-share-service docker instance
    1)  docker run -p 9000:9000 weride-share/ride-share-service:1.0
    Note: You can run on any port open port you like (i.e docker run -p 9999:9000 weride-share/ride-share-service:1.0.0 )
    
#### Stop docker instance
    In a terminal
    1)  docker ps  (lists running docker containers)
    2)  docker stop [Container ID]# lagom-scala-kubernetes
# lagom-scala-kubernetes
# lagom-scala-kubernetes
# lagom-scala-kubernetes
# lagom-scala-kubernetes
