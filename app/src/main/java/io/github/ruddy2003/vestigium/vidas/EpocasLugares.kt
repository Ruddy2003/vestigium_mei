package io.github.ruddy2003.vestigium.vidas

/**
 * El lugar de una vida, para «Hoy en la historia» ([Epocas]; tanda H3).
 * - [wikidata]: los identificadores de Wikidata de su país, su región o su ciudad, de entonces y de ahora (un suceso de
 *   1923 en Chihuahua dice «México», Q96; uno de Tenochtitlan puede decir México o el Imperio mexica).
 * - [palabras]: para cuando Wikidata no dice nada. Las que empiezan con mayúscula son nombres de lugares, de gente o
 *   de Estados ([nombres]): además rescatan un acontecimiento en el que Wikidata pone su lugar en minoría. Las de
 *   minúscula son gentilicios («mexicano»): hablan de personas, que pueden estar en otra parte, así que solo cuentan
 *   sin Wikidata.
 * - [excepto]: frases que contienen una palabra pero no son su lugar («Nuevo México» no es México).
 *
 * Cada identificador sale de Wikipedia en español (el `wikibase_item` de la página de ese título, 13 nov 2026); el
 * comentario dice cuál.
 */
class LugarDeVida(val wikidata: Set<String>, val palabras: List<String>, val excepto: List<String> = emptyList()) {
    val nombres: List<String> get() = palabras.filter { it.first().isUpperCase() }
}

private val NO_ES_MEXICO = listOf("Nuevo México", "Golfo de México")

private val EGIPTO = LugarDeVida(
    setOf("Q11768", "Q79", "Q101583"), // Antiguo Egipto, Egipto, Tebas (Egipto)
    listOf("Egipto", "Tebas", "Ramsés", "egipcio", "egipcia", "egipcios"))

private val INGLATERRA_XIX = setOf("Q174193", "Q145", "Q21") // Reino Unido de Gran Bretaña e Irlanda, Reino Unido, Inglaterra
private val PALABRAS_INGLATERRA_XIX = listOf("Reino Unido", "Gran Bretaña", "Inglaterra", "Londres", "británico", "británica", "británicos", "inglés", "inglesa", "ingleses")

private val JAPON_EDO = LugarDeVida(
    setOf("Q17", "Q205662", "Q184963", "Q1490"), // Japón, Shogunato Tokugawa, Período Edo, Tokio
    listOf("Japón", "Edo", "Tokugawa", "Kioto", "japonés", "japonesa", "japoneses"))

private val ROMA_IMPERIAL = setOf("Q2277", "Q1747689") // Imperio romano, Antigua Roma

/** El lugar de cada vida real con época, por su id. Las vidas inventadas y las de hoy no tienen ([Epocas.de]). */
val LUGARES: Map<String, LugarDeVida> = mapOf(
    // ---- Las doce de todos los temas (las de hoy, mundo2025 y mexico2025, no tienen época)
    "paleolitico" to LugarDeVida(emptySet(), listOf("África oriental")),
    "neolitico" to LugarDeVida(setOf("Q192522"), listOf("Çatalhöyük", "Anatolia")), // Çatalhöyük
    "egipto" to EGIPTO,
    "roma" to LugarDeVida(ROMA_IMPERIAL + setOf("Q220", "Q38"), // + Roma, Italia
        listOf("Roma", "Imperio romano", "Trajano", "Adriano", "romano", "romana", "romanos")),
    "medieval" to LugarDeVida(setOf("Q179876", "Q21", "Q145"), // Reino de Inglaterra, Inglaterra, Reino Unido
        listOf("Inglaterra", "Eduardo I", "Eduardo II", "inglés", "inglesa", "ingleses")),
    "tenochtitlan" to LugarDeVida(setOf("Q2608489", "Q13695", "Q96", "Q170603"), // Imperio mexica, México-Tenochtitlan, México, Nueva España
        listOf("Tenochtitlan", "México", "Moctezuma", "Nueva España", "Hernán Cortés", "mexica", "mexicas", "azteca", "aztecas"), NO_ES_MEXICO),
    "edo" to JAPON_EDO,
    "manchester" to LugarDeVida(INGLATERRA_XIX + "Q18125", PALABRAS_INGLATERRA_XIX + "Manchester"), // + Mánchester
    "hacienda" to LugarDeVida(setOf("Q96", "Q80903"), // México, Estado de Hidalgo
        listOf("México", "Hidalgo", "Porfirio Díaz", "mexicano", "mexicana", "mexicanos"), NO_ES_MEXICO),
    "cdmx1960" to LugarDeVida(setOf("Q96", "Q1489"), // México, Ciudad de México
        listOf("México", "Ciudad de México", "Guadalajara", "Monterrey", "Veracruz", "Puebla", "mexicano", "mexicana", "mexicanos"), NO_ES_MEXICO),

    // ---- Las de un tema
    "legionario" to LugarDeVida(ROMA_IMPERIAL + setOf("Q173082", "Q1653"), // + Dacia, Danubio
        listOf("Imperio romano", "Trajano", "Dacia", "Danubio", "romano", "romanos", "dacios")),
    "remero" to LugarDeVida(setOf("Q11772", "Q1524", "Q5690", "Q41"), // Antigua Grecia, Atenas, Esparta, Grecia
        listOf("Atenas", "Esparta", "Pericles", "Peloponeso", "Grecia", "ateniense", "atenienses", "espartanos", "griegos")),
    "escriba_tumba" to EGIPTO,
    "copista" to LugarDeVida(setOf("Q12548", "Q1720", "Q584", "Q183"), // Sacro Imperio, Maguncia, Rin, Alemania
        listOf("Sacro Imperio", "Maguncia", "Rin", "Federico Barbarroja", "Conrado III", "Alemania")),
    "sargento" to LugarDeVida(setOf("Q55502", "Q12546", "Q126084"), // Reino de Jerusalén, Cruzadas, Acre
        listOf("Jerusalén", "Acre", "Tierra Santa", "Luis IX", "cruzada", "cruzados", "templarios")),
    "cantero" to LugarDeVida(setOf("Q70972", "Q142", "Q41604", "Q90"), // Reino de Francia, Francia, Amiens, París
        listOf("Francia", "Amiens", "París", "Luis IX", "francés", "francesa", "franceses")),
    "aprendiz" to LugarDeVida(setOf("Q148540", "Q2044", "Q38"), // República de Florencia, Florencia, Italia
        listOf("Florencia", "Médici", "Savonarola", "Italia", "florentino", "florentina")),
    "grumete" to LugarDeVida(setOf("Q778", "Q2467", "Q1247", "Q161885"), // Bahamas, Nasáu, Mar Caribe, Reino de Gran Bretaña
        listOf("Bahamas", "Nasáu", "Nassau", "Caribe", "Barbanegra", "Jamaica", "Antillas", "piratas")),
    "cordobesa" to LugarDeVida(setOf("Q171740", "Q5818", "Q123559", "Q29"), // Califato de Córdoba, Córdoba, Al-Ándalus, España
        listOf("Córdoba", "Al-Ándalus", "Abderramán", "Alhakén", "Almanzor", "califa", "omeya")),
    "escriba_maya" to LugarDeVida(setOf("Q28567", "Q662263", "Q13703"), // Cultura maya, Yaxchilán, Mesoamérica
        listOf("Yaxchilán", "Palenque", "Tikal", "Copán", "maya", "mayas")),
    "pastor_mongol" to LugarDeVida(setOf("Q12557", "Q711"), // Imperio mongol, Mongolia
        listOf("Mongolia", "Gengis Kan", "Temuyín", "mongol", "mongoles")),
    "mercader_sogdiano" to LugarDeVida(setOf("Q5753", "Q6501000", "Q9683", "Q148"), // Samarcanda, Chang'an, Dinastía Tang, China
        listOf("Samarcanda", "Sogdiana", "Chang'an", "Tang", "An Lushan", "Talas", "sogdiano", "sogdianos")),
    "posadera" to JAPON_EDO,
    "granadero" to LugarDeVida(setOf("Q71084", "Q142", "Q34266", "Q179250"), // Primer Imperio francés, Francia, Imperio ruso, Invasión de Rusia
        listOf("Napoleón", "Francia", "Rusia", "Moscú", "francés", "franceses", "ruso", "rusos")),
    "pianista" to LugarDeVida(setOf("Q30", "Q60", "Q189074"), // Estados Unidos, Nueva York, Harlem
        listOf("Estados Unidos", "Nueva York", "Harlem", "Chicago", "estadounidense", "estadounidenses")),
    "lavandera" to LugarDeVida(setOf("Q70972", "Q142", "Q90", "Q6534"), // Reino de Francia, Francia, París, Revolución francesa
        listOf("Francia", "París", "Luis XVI", "Bastilla", "Versalles", "francés", "francesa", "franceses")),
    "naturalista" to LugarDeVida(setOf("Q155", "Q414", "Q298", "Q38095", "Q419"), // Brasil, Argentina, Chile, Galápagos, Perú
        listOf("Brasil", "Argentina", "Chile", "Galápagos", "Perú", "Uruguay", "Tierra del Fuego")),
    "obrera_moscu" to LugarDeVida(setOf("Q15180", "Q159", "Q649"), // Unión Soviética, Rusia, Moscú
        listOf("Unión Soviética", "URSS", "Moscú", "Kremlin", "Brézhnev", "soviético", "soviética", "soviéticos")),
    "buzo_esponjas" to LugarDeVida(setOf("Q812", "Q647103", "Q41", "Q527809", "Q30"), // Florida, Tarpon Springs, Grecia, Kálimnos, Estados Unidos
        listOf("Florida", "Tarpon Springs", "Grecia", "Kálimnos", "griego", "griegos")),
    "fogonero" to LugarDeVida(INGLATERRA_XIX, PALABRAS_INGLATERRA_XIX),
    "pintora_cueva" to LugarDeVida(emptySet(), listOf("Lascaux", "Altamira")),
    // Tom (escribiente) y Carmen (operadora) no tienen cuento, así que no hace falta su lugar.
)
