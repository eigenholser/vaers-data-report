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

Sample output from data 2020 through April 13, 2021:

    ******* All COVID19 Vaccine Adverse Events Reported *******
    Total: 50,716
    Deaths: 2,119
    Non-fatal Injuries: 48,597
    Hospitalization Required: 4,805
    ER Visit Required: 29
    Life Threatening Injury: 1,441

    ******* Q1 2021 COVID19 Vaccine Adverse Events Reported *******
    Total: 40,044
    Deaths: 2,103
    Non-fatal Injuries: 37,941
    Hospitalization Required: 4,665
    ER Visit Required: 11
    Life Threatening Injury: 1,373

    ******* Q1 2020 All Vaccine Adverse Events Reported *******
    Total: 8,300
    Deaths: 33
    Non-fatal Injuries: 8,267
    Hospitalization Required: 379
    ER Visit Required: 43
    Life Threatening Injury: 55

## Fetch the datasets

Download the VAERS DATA and VAERS Vaccine files from
[VAERS Datasets](https://vaers.hhs.gov/data/datasets.html)

Save the datasets using the downloaded filename into `src/main/resources/datasets`.

Save them as `VAERSData.csv` and `VAERSVAX.csv`. Combine 2020 and 2021 data
by if you like. Remember to strip the headers before appending the
data.

These cannot be downloaded by machine because of the CAPTCHA. Drat!

The VaxType enum may be incomplete. If so, just add the enum exposed in the parse 
exception and rebuild. If at first you don't succeed, try try again.

## Compile

Build and run the jar file:

    mvn compile assembly:single
    java -jar target/vaers-1.0-SNAPSHOT-jar-with-dependencies.jar

