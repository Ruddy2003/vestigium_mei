package io.github.ruddy2003.vestigium.ui.escenas

import io.github.ruddy2003.vestigium.data.EspecialLeido
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import io.github.ruddy2003.vestigium.vidas.Franja
import java.time.LocalDate

// =====================================================================
//  Las reglas puras del cuento con su cuadro (parte 8, encargo B; `docs/diseno/CUADROS_PLAN.md`)
// =====================================================================
//
// Sin nada de Android, para probarlas con JUnit en la computadora: cuándo suena el ambiente y cómo sube y baja, cuál de
// sus archivos toca, cuándo un cuento cuenta como leído y cómo se nombra el cuento de un día pasado. El sonido de verdad
// (el reproductor, el foco del audio, el ciclo de vida) está en `AmbienteCuadro.kt`.

/** Cuándo y cómo suenan el ambiente del cuento y el ronroneo del gato. */
object ReglasAmbiente {
    /** El volumen del ambiente, de 0 a 1 (el del reproductor, sobre el volumen de medios del teléfono): suave, pero se oye. */
    const val VOLUMEN = 0.42f
    /** El volumen mientras otro sonido corto (una notificación) pide paso: se agacha y vuelve. */
    const val VOLUMEN_AGACHADO = 0.12f
    /** Lo que tarda en subir al abrir el cuento. */
    const val ENTRADA_MS = 2000L
    /** Lo que tarda en callarse al salir. */
    const val SALIDA_MS = 1500L
    /** Cada cuánto se mueve el volumen durante un fundido. */
    const val PASO_MS = 40L
    /** El volumen del ronroneo (como los demás efectos de la app, en el canal de los sonidos de la interfaz). */
    const val VOLUMEN_RONRONEO = 0.7f
    /** El ronroneo común: el gato es el mismo animal en todos los temas. */
    const val RONRONEO = "s_gato_ronroneo"

    /**
     * Si el ambiente puede **empezar** a sonar. Decisión del director (17 nov 2026): **no depende del ajuste general de
     * sonidos** (`Config.sonidos`, que viene apagado), solo de su propio interruptor ([interruptor], `Config.ambiente_cuadro`)
     * y de que el teléfono esté en modo normal ([timbreNormal]: ni silencio ni vibración). Tampoco empieza durante una
     * [llamada] ni si ya suena música de otra app ([hayMusica]): no se le pone encima.
     */
    fun puedeEmpezar(interruptor: Boolean, timbreNormal: Boolean, llamada: Boolean, hayMusica: Boolean): Boolean =
        interruptor && timbreNormal && !llamada && !hayMusica

    /**
     * Si el ambiente que ya suena puede seguir. Lo mismo que [puedeEmpezar] salvo la música: una vez que suena, «hay música»
     * sería él mismo; la música de otra app que empiece después le quita el foco del audio y eso lo calla.
     */
    fun puedeSeguir(interruptor: Boolean, timbreNormal: Boolean, llamada: Boolean): Boolean = interruptor && timbreNormal && !llamada

    /** El ronroneo sí sigue el ajuste general de sonidos, como los demás efectos ([sonidos], `Config.sonidos`), y el modo normal. */
    fun puedeRonronear(sonidos: Boolean, timbreNormal: Boolean, llamada: Boolean): Boolean = sonidos && timbreNormal && !llamada

    /** Si a esa luz del cuadro le toca el ambiente de noche: la noche y la luz tenue (la nave de Espacio). */
    fun esNoche(franja: Franja): Boolean = franja.oscura

    /** Los archivos que podrían sonar, en orden de preferencia: de noche, el de noche y, si no lo hay, el de siempre. */
    fun nombres(tema: String, noche: Boolean): List<String> =
        if (noche) listOf("a_${tema}_ambiente_noche", "a_${tema}_ambiente") else listOf("a_${tema}_ambiente")

    /** El primero de [nombres] que [existe]; null si el tema no tiene ambiente (todavía). */
    fun elegir(tema: String, noche: Boolean, existe: (String) -> Boolean): String? = nombres(tema, noche).firstOrNull(existe)

    /**
     * El volumen de un fundido a los [ms] de empezar, yendo de [desde] a [hasta] en [duracion] ms. La curva es suave en los
     * dos extremos (smoothstep): no arranca de golpe ni frena de golpe. Antes de empezar da [desde]; después, [hasta].
     */
    fun rampa(desde: Float, hasta: Float, ms: Long, duracion: Long): Float {
        if (duracion <= 0L || ms >= duracion) return hasta
        if (ms <= 0L) return desde
        val x = ms.toFloat() / duracion
        val s = x * x * (3f - 2f * x)
        return desde + (hasta - desde) * s
    }
}

/** Cuándo cuenta como leído un cuento, y cómo se llama el de un día pasado. */
object LecturaCuento {
    /**
     * Si el marcador del final del cuento (una franja de 1 dp justo debajo de su último párrafo) se ve: su rectángulo en la
     * ventana, ya recortado por lo que lo contiene ([arriba] y [abajo], en pixeles), no está vacío y cae dentro del alto de
     * la ventana ([altoVentana]). **Leer es llegar al final**, sin tiempo mínimo (decisión del director, 17 nov 2026).
     */
    fun alaVista(arriba: Float, abajo: Float, altoVentana: Float): Boolean =
        abajo - arriba > 0.5f && arriba >= 0f && abajo <= altoVentana + 0.5f

    /** Los días especiales del día del cuento tal como eran al leerlo (`CuentoLeido.especiales`). */
    fun especiales(ficha: FichaDelDia): List<EspecialLeido> = ficha.especiales.map { EspecialLeido(it.dia.id, it.inicio.toString()) }.distinct()

    private val MESES = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")

    /** «21 de marzo» o, si no es del año de [hoy], «21 de marzo de 2026». Si [dia] no es una fecha, la devuelve tal cual. */
    fun fecha(dia: String, hoy: String): String {
        val d = runCatching { LocalDate.parse(dia.take(10)) }.getOrNull() ?: return dia
        val h = runCatching { LocalDate.parse(hoy.take(10)) }.getOrNull()
        val base = "${d.dayOfMonth} de ${MESES[d.monthValue - 1]}"
        return if (h != null && h.year == d.year) base else "$base de ${d.year}"
    }

    /** El título de la sección del cuento: «Hoy, en su vida»; «Ayer, en su vida»; «El 21 de marzo, en su vida». */
    fun tituloSeccion(dia: String, hoy: String): String {
        if (dia == hoy) return "Hoy, en su vida"
        val d = runCatching { LocalDate.parse(dia.take(10)) }.getOrNull()
        val h = runCatching { LocalDate.parse(hoy.take(10)) }.getOrNull()
        if (d != null && h != null && d.plusDays(1) == h) return "Ayer, en su vida"
        return "El ${fecha(dia, hoy)}, en su vida"
    }

    /** El título de la pantalla: «El cuento de hoy» o «El cuento del 21 de marzo». */
    fun tituloPantalla(dia: String, hoy: String): String = if (dia == hoy) "El cuento de hoy" else "El cuento del ${fecha(dia, hoy)}"
}
