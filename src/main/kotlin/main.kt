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
    ADEN_4_7, ANTH,
    BCG,
    CHOL, COVID19,
    DF, DTAPIPVHIB, DTAPHEPBIP, DTAPIPV, DT, DTOX, DTP, DTAP,
    FLU3, FLU4, FLUN3, FLUN4, FLUR3, FLUX, FLUC3, FLUC4, FLUR4, FLUA3, FLUA4,
    HBHEPB, HEP, HEPA, HEPAB, HIBV, HPV2, HPV4, HPV9, HPVX,
    IPV,
    JEV1, JEVX,
    MEN, MENB, MENHIB, MMR, MMRV, MNQ,
    OPV,
    PNC, PNC10, PPV, PNC13,
    RAB, RV1, RV5, RVX, RUB,
    SMALL,
    TD, TDAP, TTOX, TYP,
    UNK,
    VARZOS, VARCEL,
    YF
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
    val lThreat: Boolean?,
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

fun String.toLocalDate() =
    LocalDate.parse(this, DateTimeFormatter.ISO_DATE)

fun main(args: Array<String>) {
    val vaersVaxMapCovid = parseVaersVax(listOf(VaxType.COVID19))
    val vaersVaxMapAll = parseVaersVax(null)

    val vaersDataMapCovid = parseVaersData(null)
    val q12020vaersDataMapAll = parseVaersData(Pair("2020-01-01".toLocalDate(), "2020-03-31".toLocalDate()))
    val q12021VaersDataMapCovid = parseVaersData(Pair("2021-01-01".toLocalDate(), "2021-03-31".toLocalDate()))

    val covidVaersData = vaersDataMap(vaersDataMapCovid, vaersVaxMapCovid)
    val q12020VaersData = vaersDataMap(q12020vaersDataMapAll, vaersVaxMapAll)
    val q12021VaersDataCovid = vaersDataMap(q12021VaersDataMapCovid, vaersVaxMapCovid)
    covidReportSummary(covidVaersData)
    q12021CovidReportSummary(q12021VaersDataCovid)
    q12020ReportSummary(q12020VaersData)
}

fun covidReportSummary(covidVaersData: Map<Int, VaersData>): Unit {
    println("******* All COVID19 Vaccine Adverse Events Reported *******")
    println("Total: ${"%,d".format(covidVaersData.count())}")
    println("Deaths: ${"%,d".format(covidVaersData.keys.filter { covidVaersData[it]!!.died }.count())}")
    println("Non-fatal Injuries: ${"%,d".format(covidVaersData.keys.filter { !covidVaersData[it]!!.died }.count())}")
    println(
        "Hospitalization Required: ${
            "%,d".format(covidVaersData.keys.filter { covidVaersData[it]!!.hospital }.count())
        }"
    )
    println("ER Visit Required: ${"%,d".format(covidVaersData.keys.filter { covidVaersData[it]!!.erVisit }.count())}")
    println(
        "Life Threatening Injury: ${
            "%,d".format(covidVaersData.keys.filter { covidVaersData[it]!!.lThreat!! }.count())
        }"
    )
    println()
}

fun q12021CovidReportSummary(covidVaersData: Map<Int, VaersData>): Unit {
    println("******* Q1 2021 COVID19 Vaccine Adverse Events Reported *******")
    println("Total: ${"%,d".format(covidVaersData.count())}")
    println("Deaths: ${"%,d".format(covidVaersData.keys.filter { covidVaersData[it]!!.died }.count())}")
    println("Non-fatal Injuries: ${"%,d".format(covidVaersData.keys.filter { !covidVaersData[it]!!.died }.count())}")
    println(
        "Hospitalization Required: ${
            "%,d".format(covidVaersData.keys.filter { covidVaersData[it]!!.hospital }.count())
        }"
    )
    println("ER Visit Required: ${"%,d".format(covidVaersData.keys.filter { covidVaersData[it]!!.erVisit }.count())}")
    println(
        "Life Threatening Injury: ${
            "%,d".format(covidVaersData.keys.filter { covidVaersData[it]!!.lThreat!! }.count())
        }"
    )
    println()
}

fun q12020ReportSummary(vaersData: Map<Int, VaersData>) {
    println("******* Q1 2020 All Vaccine Adverse Events Reported *******")
    println("Total: ${"%,d".format(vaersData.count())}")
    println("Deaths: ${"%,d".format(vaersData.keys.filter { vaersData[it]!!.died }.count())}")
    println("Non-fatal Injuries: ${"%,d".format(vaersData.keys.filter { !vaersData[it]!!.died }.count())}")
    println(
        "Hospitalization Required: ${
            "%,d".format(vaersData.keys.filter { vaersData[it]!!.hospital }.count())
        }"
    )
    println("ER Visit Required: ${"%,d".format(vaersData.keys.filter { vaersData[it]!!.erVisit }.count())}")
    println(
        "Life Threatening Injury: ${
            "%,d".format(vaersData.keys.filter { vaersData[it]!!.lThreat!! }.count())
        }"
    )
    println()
}

fun vaersDataMap(vaersDataMap: Map<Int, VaersData>, vaersVaxMap: Map<Int, VaersVax>): Map<Int, VaersData> {
    val vaersData: MutableMap<Int, VaersData> = HashMap<Int, VaersData>()
    vaersVaxMap.keys.forEach {
        if (vaersDataMap.containsKey(it)) {
            vaersDataMap[it]?.let { it1 -> vaersData.put(it, it1) }
        }
    }
    return vaersData
}

fun parseVaersVax(vaxType: List<VaxType>?): Map<Int, VaersVax> {
    val csvData = BufferedReader(FileReader("src/main/resources/datasets/VAERSVAX.csv"))
    val parser: CSVParser = CSVParser.parse(
        csvData, CSVFormat.RFC4180 //
            .withFirstRecordAsHeader() //
            .withIgnoreSurroundingSpaces()
    )

    val splitItr = Spliterators.spliteratorUnknownSize(parser.iterator(), Spliterator.ORDERED)
    val csvRecordStream = StreamSupport.stream(splitItr, false)

    val vaersVaxMap: Map<Int, VaersVax> = csvRecordStream
        .filter(Predicate<CSVRecord> {
            if (vaxType != null) {
                vaxType!!.contains(VaxType.valueOf(it.get("VAX_TYPE")))
            } else {
                true
            }
        })
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

fun parseVaersData(dateRange: Pair<LocalDate, LocalDate>?): Map<Int, VaersData> {
    val startDate = if (dateRange == null) null else dateRange!!.first
    val endDate = if (dateRange == null) null else dateRange!!.second
    val csvData = BufferedReader(FileReader("src/main/resources/datasets/VAERSData.csv"))
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
                it.get("L_THREAT")?.toLowerCase() == "y",
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
        .filter {
            if (dateRange != null) {
                if (it.recvDate == null) false else it.recvDate!! >= startDate && it.recvDate <= endDate
            }  else {
                true
            }
        }
        .collect(Collectors.toList())
        .associateBy({ it.vaersId }, { it })

//    vaersDataMap.keys.forEach { println(vaersDataMap[it]) }
    return vaersDataMap
}

fun stringToLocalDate(date: String): LocalDate {
    return try {
        LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yy", Locale.ENGLISH))
    } catch (e: Exception) {
        LocalDate.parse(date, java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy", java.util.Locale.ENGLISH))
    }
}