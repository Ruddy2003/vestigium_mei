package io.github.ruddy2003.vestigium.data

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt

/** Fase lunar calculada en el teléfono a partir de una luna nueva de referencia (6 ene 2000, 18:14 UTC). */
object Luna {
    private const val SINODICO = 29.530588853
    private const val NUEVA_REF = 947_182_440_000L

    /** 0 = luna nueva, 0.5 = llena, de vuelta a 1. Se evalúa al mediodía local del día. */
    fun fase(fecha: String): Double {
        val ms = Fechas.millisDe(Fechas.parse(fecha).atTime(12, 0))
        val dias = (ms - NUEVA_REF) / 86_400_000.0
        val edad = ((dias % SINODICO) + SINODICO) % SINODICO
        return edad / SINODICO
    }

    fun iluminacion(f: Double): Double = (1 - cos(2 * PI * f)) / 2

    fun nombre(f: Double): String = when {
        f < 0.034 || f >= 0.966 -> "luna nueva"
        f < 0.216 -> "creciente"
        f < 0.284 -> "cuarto creciente"
        f < 0.466 -> "gibosa creciente"
        f < 0.534 -> "luna llena"
        f < 0.716 -> "gibosa menguante"
        f < 0.784 -> "cuarto menguante"
        else -> "menguante"
    }

    fun latin(f: Double): String = when {
        f < 0.034 || f >= 0.966 -> "LVNA NOVA"
        f < 0.466 -> "LVNA CRESCENS"
        f < 0.534 -> "LVNA PLENA"
        else -> "LVNA DECRESCENS"
    }

    /** Glifo de la fase para mostrarlo junto a la fecha. */
    fun glifo(f: Double): String = when {
        f < 0.034 || f >= 0.966 -> "●"
        f < 0.216 -> "☽"
        f < 0.284 -> "◐"
        f < 0.466 -> "◐"
        f < 0.534 -> "○"
        f < 0.716 -> "◑"
        f < 0.784 -> "◑"
        else -> "☾"
    }
}

/** Códigos de tiempo de la Organización Meteorológica Mundial (los que usa Open-Meteo). */
object Clima {
    fun descripcion(c: Int): String = when (c) {
        0 -> "despejado"; 1 -> "casi despejado"; 2 -> "parcialmente nublado"; 3 -> "nublado"
        45, 48 -> "niebla"
        51, 53, 55, 56, 57 -> "llovizna"
        61, 63, 65, 66, 67 -> "lluvia"
        71, 73, 75, 77 -> "nieve"
        80, 81, 82 -> "chubascos"
        85, 86 -> "nevadas"
        95, 96, 99 -> "tormenta"
        else -> "código $c"
    }

    fun latin(c: Int): String = when (c) {
        0, 1 -> "SERENVM"; 2, 3 -> "NVBILVM"; 45, 48 -> "NEBVLA"
        in 51..67, in 80..82 -> "PLVVIA"; in 71..77, 85, 86 -> "NIX"; in 95..99 -> "TEMPESTAS"
        else -> "CAELVM"
    }
}

/**
 * Grados de escalada. Todo se convierte a una cifra para poder graficar:
 * boulder en escala V (VB = -1, V0 = 0…; Fontainebleau se traduce a V), vía en YDS (5.10a = 10, 5.10b = 10.25…).
 */
object Grados {
    val V: List<String> = listOf("VB") + (0..12).map { "V$it" }
    val FONT: List<String> = listOf("4", "4+", "5", "5+", "6A", "6A+", "6B", "6B+", "6C", "6C+", "7A", "7A+", "7B", "7B+", "7C", "7C+", "8A")
    val YDS: List<String> = listOf("5.6", "5.7", "5.8", "5.9") + (10..13).flatMap { n -> listOf("a", "b", "c", "d").map { "5.$n$it" } }

    private val FONT_A_V = mapOf(
        "3" to -1.0, "4" to 0.0, "4+" to 0.5, "5" to 1.0, "5+" to 2.0, "6A" to 3.0, "6A+" to 3.5, "6B" to 4.0, "6B+" to 4.5,
        "6C" to 5.0, "6C+" to 5.5, "7A" to 6.0, "7A+" to 7.0, "7B" to 8.0, "7B+" to 8.5, "7C" to 9.0, "7C+" to 10.0,
        "8A" to 11.0, "8A+" to 12.0, "8B" to 13.0, "8B+" to 14.0, "8C" to 15.0,
    )
    private val RE_V = Regex("^V(\\d{1,2})([+-])?$")
    private val RE_YDS = Regex("^5\\.(\\d{1,2})([ABCD])?$")

    fun escala(g: String): String {
        val t = g.trim().uppercase()
        return when {
            t == "VB" || RE_V.matches(t) -> "V"
            FONT_A_V.containsKey(t) -> "Font"
            RE_YDS.matches(t) -> "YDS"
            else -> "otra"
        }
    }

    fun valor(g: String): Double? {
        val t = g.trim().uppercase()
        if (t == "VB") return -1.0
        RE_V.matchEntire(t)?.let { m -> return m.groupValues[1].toDouble() + when (m.groupValues[2]) { "+" -> 0.33; "-" -> -0.33; else -> 0.0 } }
        FONT_A_V[t]?.let { return it }
        RE_YDS.matchEntire(t)?.let { m ->
            val l = m.groupValues[2]
            return m.groupValues[1].toDouble() + if (l.isEmpty()) 0.0 else "ABCD".indexOf(l) * 0.25
        }
        return null
    }

    /** Nombre legible para una cifra, en la escala del grado de ejemplo. */
    fun nombre(v: Double, ejemplo: String?): String {
        return when (ejemplo?.let { escala(it) }) {
            "Font" -> FONT_A_V.entries.minByOrNull { abs(it.value - v) }?.key ?: "V${v.roundToInt()}"
            "YDS" -> {
                val n = v.toInt(); val r = v - n
                if (n < 10) "5.$n" else "5.$n" + "abcd"[(r / 0.25).roundToInt().coerceIn(0, 3)]
            }
            else -> if (v < 0) "VB" else "V${v.roundToInt()}"
        }
    }

    fun encadenado(estilo: String): Boolean = estilo != "proyecto" && estilo != "fallido"

    val ESTILOS = listOf(
        Opcion("a_vista", "A vista", "Primer intento sin ver a nadie ni saber cómo"),
        Opcion("flash", "Flash", "Primer intento, ya sabiendo cómo"),
        Opcion("encadenado", "Encadenado", "Lo sacaste después de varios intentos"),
        Opcion("repetido", "Repetido", "Ya lo habías hecho antes"),
        Opcion("proyecto", "Proyecto", "Lo intentaste y aún no sale"),
    )
    val ANGULOS = listOf(Opcion("placa", "Placa"), Opcion("vertical", "Vertical"), Opcion("desplome", "Desplome"), Opcion("techo", "Techo"))
    val RASGOS = listOf("regletas", "romos", "pinzas", "agujeros", "volumenes", "dinamico", "coordinacion", "compresion", "talon", "punta", "fuerza", "equilibrio", "resistencia")
    /** Cómo se lee cada rasgo (los ids no cambian): con acento y mayúscula. */
    private val NOMBRES_RASGO = mapOf("regletas" to "Regletas", "romos" to "Romos", "pinzas" to "Pinzas", "agujeros" to "Agujeros", "volumenes" to "Volúmenes",
        "dinamico" to "Dinámico", "coordinacion" to "Coordinación", "compresion" to "Compresión", "talon" to "Talón", "punta" to "Punta", "fuerza" to "Fuerza",
        "equilibrio" to "Equilibrio", "resistencia" to "Resistencia")
    fun nombreRasgo(id: String): String = NOMBRES_RASGO[id] ?: id.replace('_', ' ')
}
