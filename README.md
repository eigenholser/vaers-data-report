# Vaccine Adverse Events Data Analysis for COVID19

The United States Federal Government Department of Health and Human Services
operates the Vaccine Adverse Event Reporting System. This is a voluntary reporting
system for vaccine adverse events. It has been estimated that [fewer than 1% of
vaccine adverse events are reported (PDF).](https://digital.ahrq.gov/sites/default/files/docs/publication/r18hs017045-lazarus-final-report-2011.pdf)

This is a very simple tool written in Kotlin that will correlate the VAERS ID
for COVID19 vaccinations in the Vaccination dataset with the VAERS ID in the
adverse events dataset and perform some simple aggregations on different
adverse events.

Future developments may produce some tabular output.

Sample output from data downloaded February 22, 2021:

    ******* COVID19 Vaccine Adverse Events Reported *******
    Total: 5,214
    Deaths: 783
    Other Injuries: 4,431
    Hospitalization Required: 1,713
    ER Visit Required: 3

## Fetch the datasets

Download the VAERS DATA and VAERS Vaccine files from
[VAERS Datasets](https://vaers.hhs.gov/data/datasets.html)

Save the datasets using the downloaded filename into `src/main/resources/datasets`.

## Compile

Build and run the jar file:

    mvn compile assembly:single
    java -jar target/vaers-1.0-SNAPSHOT-jar-with-dependencies.jar

