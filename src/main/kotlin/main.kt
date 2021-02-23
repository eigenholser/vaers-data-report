import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.BufferedReader
import java.io.FileReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.StreamSupport

enum class VaxType {
    CHOL, COVID19,
    DTAPHEPBIP, DT, DTAP,
    FLU4, FLUX, FLU3, FLUC3, FLUC4, FLUR4, FLUA3, FLUA4,
    HEP, HEPA, HIBV, HPV4, HPV9,
    RV1,
    MMR, MNQ,
    PPV, PNC13,
    TTOX, TDAP,
    UNK,
    VARZOS, VARCEL
}

data class VaersVax(
    val vaersId: Int,
    val vaxType: VaxType,
    val vaxManu: String,
    val vaxLot: String,
    val vaxDoseSeries: String,
    val vaxRoute: String,
    val vaxSite: String,
    val vaxName: String
)

data class VaersData(
    val vaersId: Int,
    val recvDate: LocalDate?,
    val state: String,
    val ageYears: String,
    val cageYr: String,
    val cageMo: String,
    val sex: String,
    val rptDate: LocalDate?,
    val died: Boolean,
    val dateDied: LocalDate?,
    val lThreat: String?,
    val erVisit: Boolean,
    val hospital: Boolean,
    val hospDays: Int?,
    val xStay: String,
    val disable: Boolean,
    val recovd: String?,
    val vaxDate: LocalDate?,
    val onsetDate: LocalDate?,
    val numDays: Int?,
    val labData: String,
    val vAdminBy: String,
    val vFundBy: String,
    val otherMeds: String,
    val currIll: String,
    val history: String,
    val priorVax: String,
    val spltType: String,
    val formVers: Int?,
    val todaysDate: LocalDate?,
    val birthDefect: Boolean,
    val ofcVisit: Boolean,
    val erEdVisit: Boolean,
    val allergies: String
)

fun main(args: Array<String>) {
    val vaersVaxMap = parseVaersVax()
    val vaersDataMap = parseVaersData()
    val covidVaersData = covidDataMap(vaersDataMap, vaersVaxMap)
    covidReportSummary(covidVaersData)
}

fun covidReportSummary(covidVaersData: Map<Int, VaersData>): Unit {
    println("******* COVID19 Vaccine Adverse Events Reported *******")
    println("Total: ${"%,d".format(covidVaersData.count())}")
    println("Deaths: ${"%,d".format(covidVaersData.keys.filter { covidVaersData[it]!!.died }.count())}")
    println("Other Injuries: ${"%,d".format(covidVaersData.keys.filter { !covidVaersData[it]!!.died }.count())}")
    println("Hospitalization Required: ${"%,d".format(covidVaersData.keys.filter { covidVaersData[it]!!.hospital }.count())}")
    println("ER Visit Required: ${"%,d".format(covidVaersData.keys.filter { covidVaersData[it]!!.erVisit }.count())}")
}

fun covidDataMap(vaersDataMap: Map<Int, VaersData>, vaersVaxMap: Map<Int, VaersVax>): Map<Int, VaersData> {
    val covidVaersData: MutableMap<Int, VaersData> = HashMap<Int, VaersData>()
    vaersVaxMap.keys.forEach {
        if (vaersDataMap.containsKey(it)) {
            vaersDataMap[it]?.let { it1 -> covidVaersData.put(it, it1) }
        }
    }
    return covidVaersData
}

fun parseVaersVax(): Map<Int, VaersVax> {
    val csvData = BufferedReader(FileReader("src/main/resources/datasets/2021VAERSVAX.csv"))
    val parser: CSVParser = CSVParser.parse(
        csvData, CSVFormat.RFC4180 //
            .withFirstRecordAsHeader() //
            .withIgnoreSurroundingSpaces()
    )

    val splitItr = Spliterators.spliteratorUnknownSize(parser.iterator(), Spliterator.ORDERED)
    val csvRecordStream = StreamSupport.stream(splitItr, false)

    val vaersVaxMap: Map<Int, VaersVax> = csvRecordStream
        .filter(Predicate<CSVRecord> { VaxType.valueOf(it.get("VAX_TYPE")) == VaxType.COVID19 })
        .map {
            VaersVax(
                it.get("VAERS_ID").toInt(),
                VaxType.valueOf(it.get("VAX_TYPE")),
                it.get("VAX_MANU"),
                it.get("VAX_LOT"),
                it.get("VAX_DOSE_SERIES"),
                it.get("VAX_ROUTE"),
                it.get("VAX_SITE"),
                it.get("VAX_NAME")
            )
        }
        .collect(Collectors.toList())
        .associateBy({ it.vaersId }, { it })

//    vaersVaxMap.keys.forEach { println(vaersVaxMap[it]) }
    return vaersVaxMap
}

fun parseVaersData(): Map<Int, VaersData> {
    val csvData = BufferedReader(FileReader("src/main/resources/datasets/2021VAERSData.csv"))
    val parser: CSVParser = CSVParser.parse(
        csvData, CSVFormat.RFC4180 //
            .withFirstRecordAsHeader() //
            .withIgnoreSurroundingSpaces()
    )

    val splitItr = Spliterators.spliteratorUnknownSize(parser.iterator(), Spliterator.ORDERED)
    val csvRecordStream = StreamSupport.stream(splitItr, false)

    val vaersDataMap: Map<Int, VaersData> = csvRecordStream
        .map {
            VaersData(
                it.get("VAERS_ID").toInt(),
                if (it.get("RECVDATE") == null) null else stringToLocalDate(it.get("RECVDATE")),
                it.get("STATE"),
                it.get("AGE_YRS"),
                it.get("CAGE_YR"),
                it.get("CAGE_MO"),
                it.get("SEX"),
                if (it.get("RPT_DATE").isBlank()) null else stringToLocalDate(it.get("RPT_DATE")),
                it.get("DIED")?.toLowerCase() == "y",
                if (it.get("DATEDIED").isBlank()) null else stringToLocalDate(it.get("DATEDIED")),
                it.get("L_THREAT"),
                it.get("ER_VISIT")?.toLowerCase() == "y",
                it.get("HOSPITAL")?.toLowerCase() == "y",
                if (it.get("HOSPDAYS").isBlank()) null else it.get("HOSPDAYS").toInt(),
                it.get("X_STAY"),
                it.get("DISABLE")?.toLowerCase() == "y",
                it.get("RECOVD"),
                if (it.get("VAX_DATE").isBlank()) null else stringToLocalDate(it.get("VAX_DATE")),
                if (it.get("ONSET_DATE").isBlank()) null else stringToLocalDate(it.get("ONSET_DATE")),
                if (it.get("NUMDAYS").isBlank()) null else it.get("NUMDAYS").toInt(),
                it.get("LAB_DATA"),
                it.get("V_ADMINBY"),
                it.get("V_FUNDBY"),
                it.get("OTHER_MEDS"),
                it.get("CUR_ILL"),
                it.get("HISTORY"),
                it.get("PRIOR_VAX"),
                it.get("SPLTTYPE"),
                it.get("FORM_VERS").toInt(),
                if (it.get("TODAYS_DATE").isBlank()) null else stringToLocalDate(it.get("TODAYS_DATE")),
                it.get("BIRTH_DEFECT")?.toLowerCase() == "y",
                it.get("OFC_VISIT")?.toLowerCase() == "y",
                it.get("ER_ED_VISIT")?.toLowerCase() == "y",
                it.get("ALLERGIES")
            )
        }
        .collect(Collectors.toList())
        .associateBy({ it.vaersId }, { it })

//    vaersDataMap.keys.forEach { println(vaersDataMap[it]) }
    return vaersDataMap
}

fun stringToLocalDate(date: String): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("MM/dd/yy", Locale.ENGLISH)
    return LocalDate.parse(date, formatter)
}