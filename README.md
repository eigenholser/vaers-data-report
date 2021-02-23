# VAERS Analysis

## Fetch the datasets

Download the VAERS DATA and VAERS Vaccine files from
[VAERS Datasets](https://vaers.hhs.gov/data/datasets.html)

Save the datasets using the downloaded filename into `src/main/resources/datasets`.

## Compile

Build and run the jar file:

    mvn compile assembly:single
    java -jar target/vaers-1.0-SNAPSHOT-jar-with-dependencies.jar

