# Vaccine Adverse Events Data Analysis for COVID19

## Fetch the datasets

Download the VAERS DATA and VAERS Vaccine files from
[VAERS Datasets](https://vaers.hhs.gov/data/datasets.html)

Save the datasets using the downloaded filename into `src/main/resources/datasets`.

## Compile

Build and run the jar file:

    mvn compile assembly:single
    java -jar target/vaers-1.0-SNAPSHOT-jar-with-dependencies.jar

## Sample Output

From data downloaded February 22, 2021:

    ******* COVID19 Vaccine Adverse Events Reported *******
    Total: 5,214
    Deaths: 783
    Other: 4,431
    Hospitalization Required: 1,713
    ER Visit Required: 3
