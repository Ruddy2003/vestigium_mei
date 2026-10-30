package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.data.privado
import io.github.ruddy2003.vestigium.data.esAnimo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Meandro
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Romano
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.figEstrella
import io.github.ruddy2003.vestigium.ui.figOndulado
import io.github.ruddy2003.vestigium.ui.figPoligono
import io.github.ruddy2003.vestigium.ui.figuraTema
import java.time.LocalDate
import io.github.ruddy2003.vestigium.ui.elegirFecha

/** Fecha que otra pantalla le pide abrir al Día. */
object Seleccion {
    var pedida by mutableStateOf<String?>(null)
    /** Día que abre la pantalla Privado (desde «privado de este día» en Día). */
    var privado by mutableStateOf<String?>(null)
    var ultimoMes by mutableStateOf<String?>(null)
    /** Sección de Día a la que hay que llevar al abrirlo (por ejemplo, desde el atajo «Diario de hoy»). */
    var seccion by mutableStateOf<String?>(null)
    /** Día que se está llenando en la hoja de la noche, y día al que vuelve Día al cerrarla. */
    var noche by mutableStateOf<String?>(null)
    var diaAbierto by mutableStateOf<String?>(null)
    /** Días sin llenar que se están rellenando juntos en la hoja de la noche («ponerte al día», N-09). */
    var ponerse by mutableStateOf<List<String>>(emptyList())
    /** El Día se abre con la corrección del sueño abierta (acción del buscador, N-22). */
    var editarSueno by mutableStateOf(false)
    /** Texto con el que se abre el buscador (una @persona o un #tema del resumen semanal). */
    var buscar: String? = null
    /** Día para el que se abre Notas desde «＋ agregar a este día» (N-26). */
    var notaPara: String? = null
    /** Entrada abierta en la biblioteca del diario. */
    var entrada by mutableStateOf<String?>(null)
    /** Sueño abierto en la biblioteca de sueños, y carta abierta en la de cartas. */
    var sueno by mutableStateOf<String?>(null)
    var carta by mutableStateOf<String?>(null)
    /** Pestaña de la biblioteca: «diario», «suenos» o «cartas». */
    var biblioteca by mutableStateOf("diario")
    /** Día abierto en la cinta de 24 horas. */
    var cinta by mutableStateOf<String?>(null)
    /** Día abierto en la libreta de momentos. */
    var momentos by mutableStateOf<String?>(null)
    /**
     * Abre el Día de [fecha] encima de la pantalla actual, para que «atrás» regrese a ella (N-12). La pestaña Hoy no cambia
     * de fecha por esto.
     */
    fun abrir(nav: Nav, fecha: String) { pedida = fecha; ultimoMes = Fechas.mesDe(fecha); nav.ir("dia_fecha") }

    /** Olvida las fechas que quedaron de la última vez (noche, cinta, libreta), para abrir esas pantallas en hoy. */
    fun limpiarFechas() { noche = null; cinta = null; momentos = null }
}

private data class CapaCal(val id: String, val nombre: String, val divergente: Boolean, val valor: (String) -> Double?, val rango: (List<Double>) -> Pair<Double, Double>, val texto: (Double) -> String)

private fun capas(): List<CapaCal> {
    val mu = Repo.momentos.filter { it.esAnimo && it.valencia != null }.groupBy { it.fecha }
    val gastos = Repo.gastos.groupBy { it.dia }.mapValues { e -> e.value.sumOf { it.monto } }
    val fijo = { a: Double, b: Double -> { _: List<Double> -> a to b } }
    val datos = { xs: List<Double> -> (xs.minOrNull() ?: 0.0) to (xs.maxOrNull()?.takeIf { it > 0 } ?: 1.0) }
    return listOf(
        CapaCal("animo", "ánimo", true, { f -> Repo.dias[f]?.valencia?.toDouble() ?: mu[f]?.mapNotNull { it.valencia }?.average() }, fijo(1.0, 7.0)) { "%.1f".format(it) },
        CapaCal("sueno", "sueño", true, { f -> Repo.salud[f]?.sueno_min?.let { it / 60.0 } }, fijo(5.0, 8.5)) { "%.1f h".format(it) },
        CapaCal("pasos", "pasos", false, { f -> Repo.salud[f]?.pasos?.toDouble() }, datos) { "%,.0f".format(it) },
        CapaCal("pantalla", "pantalla", false, { f -> Repo.pantalla[f]?.total_min?.toDouble() }, datos) { "${(it / 60).toInt()} h ${(it % 60).toInt()} m" },
        CapaCal("gasto", "gasto", false, { f -> gastos[f] }, datos) { "$%,.0f".format(it) },
        CapaCal("fotos", "fotos", false, { f -> Repo.fotos[f]?.fotos?.toDouble() }, datos) { "%.0f".format(it) },
        CapaCal("momentos", "momentos", false, { f -> Repo.momentos.count { it.fecha == f && it.tipo != "muestreo" && !it.privado }.toDouble().takeIf { it > 0 } }, datos) { "%.0f".format(it) },
        CapaCal("lluvia", "lluvia", false, { f -> Repo.entorno[f]?.precip_mm }, datos) { "%.1f mm".format(it) },
    )
}

@Composable
fun PantallaCalendario(nav: Nav) {
    val ctx = LocalContext.current
    var mes by remember { mutableStateOf(Seleccion.ultimoMes ?: Fechas.mesDe(Fechas.diaLogico())) }
    var capaId by remember { mutableStateOf("animo") }
    var verFotos by remember { mutableStateOf(false) }
    val oscuro = esOscuro()
    val todas = capas()
    val capa = todas.first { it.id == capaId }
    val primero = Fechas.parse("$mes-01")
    val hoy = Fechas.diaLogico()
    val dias = (0 until primero.lengthOfMonth()).map { Fechas.iso(primero.plusDays(it.toLong())) }
    val valores = dias.associateWith { capa.valor(it) }
    val (lo, hi) = capa.rango(valores.values.filterNotNull())

    fun ir(fecha: String) { Seleccion.abrir(nav, fecha) }

    Hoja("Calendario", nav.atras, subtitulo = "Toca un día para abrirlo en el editor. El color muestra la capa elegida.") {
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { mes = Fechas.mesDe(Fechas.iso(primero.minusMonths(1))) }, shape = RoundedCornerShape(4.dp)) { Text("‹", fontSize = 18.sp) }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${io.github.ruddy2003.vestigium.ui.Estilo.mes(primero.monthValue)} · ${io.github.ruddy2003.vestigium.ui.Estilo.numeral(primero.year)}", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                Text("// ${Fechas.mesTitulo(mes).lowercase()}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            OutlinedButton(onClick = { mes = Fechas.mesDe(Fechas.iso(primero.plusMonths(1))) }, shape = RoundedCornerShape(4.dp)) { Text("›", fontSize = 18.sp) }
        }

        Row(Modifier.fillMaxWidth().padding(top = 14.dp)) {
            io.github.ruddy2003.vestigium.ui.Estilo.semana.forEach { d -> Text(d, Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        val desfase = primero.dayOfWeek.value - 1
        val celdas = List(desfase) { null } + dias
        celdas.chunked(7).forEach { semana ->
            Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (i in 0 until 7) {
                    val f = semana.getOrNull(i)
                    if (f == null) { Spacer(Modifier.weight(1f)); continue }
                    val v = valores[f]
                    val t = if (v == null) null else if (hi > lo) ((v - lo) / (hi - lo)).toFloat() else 0.5f
                    val fondo = when {
                        t == null -> MaterialTheme.colorScheme.surface
                        capa.divergente -> Tonos.divergente(t, oscuro)
                        else -> Tonos.secuencial(t, oscuro)
                    }
                    Celda(Modifier.weight(1f), f, fondo, f == hoy, f > hoy, verFotos) { ir(f) }
                }
            }
        }

        Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(if (capa.divergente) capa.texto(lo) else "0", style = MaterialTheme.typography.labelSmall)
            Row(Modifier.weight(1f).padding(horizontal = 8.dp).height(8.dp)) {
                for (k in 0..10) Box(Modifier.weight(1f).height(8.dp).background(if (capa.divergente) Tonos.divergente(k / 10f, oscuro) else Tonos.secuencial(k / 10f, oscuro)))
            }
            Text(capa.texto(hi), style = MaterialTheme.typography.labelSmall)
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("▣ fotos", verFotos) { verFotos = !verFotos }
            todas.forEach { c -> Ficha(c.nombre, c.id == capaId) { capaId = c.id } }
        }
        Ayuda("▪ diario escrito · • momentos · ✦ hito · sello: día sellado (ánimo y diario) · KAL, NON, ID: calendas, nonas e idus del calendario romano.", Modifier.padding(top = 8.dp))

        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonSecundario("hoy") { ir(hoy) }
            BotonSecundario("ir a una fecha…") {
                elegirFecha(ctx, hoy) { ir(it) }
            }
        }

        Seccion("Este mes") {
            Tarjeta {
                val conDiario = dias.count { Repo.dias[it] != null }
                val transcurridos = dias.count { it <= hoy }
                val vals = dias.mapNotNull { Repo.dias[it]?.valencia }
                Clave("dias", "$conDiario de $transcurridos con registro")
                if (vals.isNotEmpty()) Clave("animus", "%.2f de media · ${vals.size} días".format(vals.average()))
                val mejor = dias.filter { Repo.dias[it]?.valencia != null }.maxByOrNull { Repo.dias[it]!!.valencia!! }
                mejor?.let { Clave("optimus", Fechas.corto(it) + " · " + Repo.dias[it]?.valencia) }
                val mom = Repo.momentos.count { it.fecha.startsWith(mes) && it.tipo != "muestreo" && !it.privado }
                Clave("momenta", "$mom")
                val gasto = Repo.gastos.filter { it.fecha.startsWith(mes) }.sumOf { it.monto }
                if (gasto > 0) Clave("svmptvs", "$%,.0f".format(gasto))
                val hitos = dias.flatMap { Repo.dias[it]?.hitos ?: emptyList() }
                if (hitos.isNotEmpty()) Clave("memorabilia", hitos.distinct().take(8).joinToString(", "))
                val esc = Repo.ascensos.count { it.fecha.startsWith(mes) }
                if (esc > 0) Clave("ascensvs", "$esc sesiones")
            }
        }
    }
}

/**
 * La forma de la celda en cada tema. Las esquinas que se cortan o redondean mucho no tocan la de arriba a la izquierda,
 * donde va el número.
 */
private fun formaCelda(tema: TemaId): Shape = when (tema) {
    TemaId.ROMANO -> RoundedCornerShape(1.dp)
    TemaId.CIBER -> RectangleShape
    TemaId.NEON -> RectangleShape
    TemaId.HELENO -> RoundedCornerShape(2.dp)
    TemaId.MEDIEVAL -> RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp, bottomEnd = 7.dp, bottomStart = 1.dp)
    TemaId.DISTOPIA -> RectangleShape
    TemaId.REFUGIO -> RoundedCornerShape(7.dp)
    TemaId.ORDEN -> CutCornerShape(topEnd = 8.dp)
    TemaId.LABORATORIO -> RoundedCornerShape(5.dp)
    TemaId.COLONOS -> RoundedCornerShape(2.dp)
    TemaId.NAPOLEON -> CutCornerShape(2.dp)
    TemaId.BOTANICO -> RoundedCornerShape(topStart = 9.dp, topEnd = 1.dp, bottomEnd = 9.dp, bottomStart = 1.dp)
    TemaId.NAVIDAD -> RoundedCornerShape(4.dp)
    TemaId.SEDA -> RectangleShape
    TemaId.GOTICO -> RectangleShape
    TemaId.ESPACIO -> RoundedCornerShape(3.dp)
    TemaId.ANDALUS -> RectangleShape
    TemaId.MONGOL -> RoundedCornerShape(8.dp)
    TemaId.RENACIMIENTO -> RectangleShape
    TemaId.URSS -> RectangleShape
    TemaId.REVOLUCION -> RoundedCornerShape(1.dp)
    TemaId.ANTIGUAS -> CutCornerShape(4.dp)
}

/**
 * El marco de la celda en cada tema, encima del color de la capa: tesela, píxel de fósforo, entablamento, folio con rúbrica,
 * expediente, mampara, ficha perforada, papel milimétrico, retazo cosido, placa, hoja de herbario, nieve, celosía, vidriera,
 * mira, alicatado, fieltro, trazo de construcción, cartel, franja tricolor y losa. [hoy] lo marca más fuerte; [sellado]
 * (ánimo y diario del día, como en la hoja de la noche) pone el sello del día a la derecha, con la figura del tema impresa.
 * Todo va pegado al borde o en el hueco de la derecha, para no tapar el número ni las marcas.
 */
private fun DrawScope.marcoCelda(tema: TemaId, forma: Shape, hoy: Boolean, sellado: Boolean, linea: Color, acento: Color, oro: Color, sobre: Color) {
    val s = density
    val w = size.width; val h = size.height
    val borde = forma.createOutline(size, layoutDirection, this)
    // La celda va recortada con su forma: del trazo solo se ve la mitad de adentro.
    fun contorno(color: Color, grosor: Float) = drawOutline(borde, color, style = Stroke(grosor * s * 2))
    val pc = Offset(w - 7f * s, h * 0.55f)
    fun sello(base: Color) {
        drawCircle(base, 5.2f * s, pc)
        drawCircle(sobre.copy(alpha = 0.5f), 4.3f * s, pc, style = Stroke(0.6f * s))
        figuraTema(tema, sobre, pc, 3.2f * s, base)
    }
    when (tema) {
        // Tesela: el borde de teselas de un mosaico; hoy, alternando pórfido y oro.
        TemaId.ROMANO -> {
            val lado = (if (hoy) 2.6f else 2.2f) * s; val paso = lado + 1.1f * s
            var i = 0; var x = 0f
            while (x < w) { val col = if (!hoy) linea else if (i % 2 == 0) acento else oro; drawRect(col, Offset(x, 0f), Size(lado, lado)); drawRect(col, Offset(x, h - lado), Size(lado, lado)); x += paso; i++ }
            var y = paso
            while (y < h - lado) { val col = if (!hoy) linea else if (i % 2 == 0) acento else oro; drawRect(col, Offset(0f, y), Size(lado, lado)); drawRect(col, Offset(w - lado, y), Size(lado, lado)); y += paso; i++ }
            if (sellado) sello(acento)
        }
        // Píxel de fósforo: líneas de barrido; hoy, el borde encendido con su halo y el cursor bajo el número.
        TemaId.CIBER -> {
            var y = 2f * s
            while (y < h) { drawLine(Color.Black.copy(alpha = 0.14f), Offset(0f, y), Offset(w, y), 0.6f * s); y += 2.6f * s }
            if (hoy) { contorno(acento.copy(alpha = 0.35f), 3f); contorno(acento, 1.2f); drawRect(acento, Offset(4f * s, 20f * s), Size(6f * s, 1.6f * s)) } else contorno(linea, 0.6f)
            if (sellado) sello(acento)
        }
        TemaId.NEON -> {
            var y = 2f * s
            while (y < h) { drawLine(Color.Black.copy(alpha = 0.14f), Offset(0f, y), Offset(w, y), 0.6f * s); y += 2.6f * s }
            if (hoy) { contorno(acento.copy(alpha = 0.35f), 3f); contorno(acento, 1.2f); drawRect(acento, Offset(4f * s, 20f * s), Size(6f * s, 1.6f * s)) } else contorno(linea, 0.6f)
            if (sellado) sello(acento)
        }
        // Entablamento: doble filete arriba y abajo; hoy, un frontón de oro.
        TemaId.HELENO -> {
            val col = if (hoy) acento else linea; val g = (if (hoy) 1.2f else 0.7f) * s
            drawLine(col, Offset(0f, g / 2), Offset(w, g / 2), g); drawLine(col, Offset(0f, 2.2f * s), Offset(w, 2.2f * s), g * 0.6f)
            drawLine(col, Offset(0f, h - g / 2), Offset(w, h - g / 2), g); drawLine(col, Offset(0f, h - 2.2f * s), Offset(w, h - 2.2f * s), g * 0.6f)
            drawLine(col.copy(alpha = 0.5f), Offset(0.4f * s, 0f), Offset(0.4f * s, h), 0.6f * s); drawLine(col.copy(alpha = 0.5f), Offset(w - 0.4f * s, 0f), Offset(w - 0.4f * s, h), 0.6f * s)
            if (hoy) drawPath(Path().apply { moveTo(w / 2 - 6f * s, 3.2f * s); lineTo(w / 2 + 6f * s, 3.2f * s); lineTo(w / 2, 0f); close() }, oro)
            if (sellado) sello(oro)
        }
        // Folio: la rúbrica roja al margen y la esquina gastada; el día sellado lleva lacre.
        TemaId.MEDIEVAL -> {
            drawRect((if (hoy) acento else Color(0xFFA3312F)).copy(alpha = 0.85f), Offset.Zero, Size((if (hoy) 2.6f else 1.8f) * s, h))
            contorno(if (hoy) oro else linea, if (hoy) 1.4f else 0.5f)
            drawPath(Path().apply { moveTo(w - 6f * s, h); lineTo(w, h - 6f * s); lineTo(w - 6f * s, h - 6f * s); close() }, linea.copy(alpha = 0.35f))
            if (sellado) {
                val lacre = Color(0xFF9E2A22)
                drawPath(figOndulado(pc, 5.6f * s, 7, 0.14f), lacre)
                figuraTema(tema, Color(0xFFE9C9A0), pc, 3.2f * s, lacre)
            }
        }
        // Expediente: borde de ficha y pestaña; el día sellado lleva un timbre de goma torcido.
        TemaId.DISTOPIA -> {
            contorno(if (hoy) acento else linea, if (hoy) 1.6f else 0.9f)
            drawRect(if (hoy) acento else linea, Offset(w * 0.55f, 0f), Size(w * 0.45f, 2.6f * s))
            if (sellado) rotate(-14f, pc) {
                val tinta = Color(0xFFB3261E)
                drawRect(tinta, Offset(pc.x - 5.5f * s, pc.y - 3.6f * s), Size(11f * s, 7.2f * s), style = Stroke(s))
                figuraTema(tema, tinta, pc, 2.6f * s, Color.White)
            }
        }
        // Mampara del refugio: esquinas redondas y remaches; hoy, el fósforo encendido.
        TemaId.REFUGIO -> {
            if (hoy) { contorno(acento.copy(alpha = 0.3f), 3f); contorno(acento, 1.2f) } else contorno(linea, 0.8f)
            drawCircle(if (hoy) acento else linea, 0.9f * s, Offset(w / 2, 1.8f * s)); drawCircle(if (hoy) acento else linea, 0.9f * s, Offset(w / 2, h - 1.8f * s))
            if (sellado) sello(acento)
        }
        // Ficha perforada: la esquina cortada y las perforaciones; hoy, de latón.
        TemaId.ORDEN -> {
            contorno(if (hoy) oro else linea, if (hoy) 1.3f else 0.8f)
            if (hoy) drawLine(oro, Offset(w - 10.5f * s, 0f), Offset(w, 10.5f * s), s)
            for (k in 0 until 3) drawRect(linea.copy(alpha = 0.7f), Offset(w - 3.4f * s, h * 0.22f + k * 3.2f * s), Size(1.4f * s, 2.2f * s))
            if (sellado) sello(acento)
        }
        // Papel milimétrico; hoy, las marcas de la mira en los cuatro lados.
        TemaId.LABORATORIO -> {
            val rejilla = linea.copy(alpha = 0.16f)
            var x = 5f * s; while (x < w) { drawLine(rejilla, Offset(x, 0f), Offset(x, h), 0.5f * s); x += 5f * s }
            var y = 5f * s; while (y < h) { drawLine(rejilla, Offset(0f, y), Offset(w, y), 0.5f * s); y += 5f * s }
            contorno(if (hoy) acento else linea, if (hoy) 1.2f else 0.6f)
            if (hoy) {
                drawLine(acento, Offset(w / 2, 0f), Offset(w / 2, 2.6f * s), s); drawLine(acento, Offset(w / 2, h), Offset(w / 2, h - 2.6f * s), s)
                drawLine(acento, Offset(0f, h * 0.4f), Offset(2.6f * s, h * 0.4f), s); drawLine(acento, Offset(w, h * 0.4f), Offset(w - 2.6f * s, h * 0.4f), s)
            }
            if (sellado) sello(oro)
        }
        // Retazo cosido: el pespunte por dentro del borde.
        TemaId.COLONOS -> {
            contorno(if (hoy) acento else linea, if (hoy) 1.2f else 0.5f)
            drawRoundRect((if (hoy) acento else linea).copy(alpha = 0.8f), Offset(1.6f * s, 1.6f * s), Size(w - 3.2f * s, h - 3.2f * s), CornerRadius(1.5f * s),
                style = Stroke(0.6f * s, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.8f * s, 1.4f * s))))
            if (sellado) sello(acento)
        }
        // Placa imperial: filete de oro por dentro con sus rosetas en las esquinas.
        TemaId.NAPOLEON -> {
            contorno(if (hoy) acento else linea, if (hoy) 1.4f else 0.6f)
            val m = 1.6f * s
            drawRect(oro.copy(alpha = if (hoy) 1f else 0.6f), Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(0.5f * s))
            listOf(Offset(m, m), Offset(w - m, m), Offset(m, h - m), Offset(w - m, h - m)).forEach { drawCircle(oro, 0.9f * s, it) }
            if (sellado) sello(acento)
        }
        // Hoja de herbario: forma de hoja con su nervadura; hoy, clavada con alfiler.
        TemaId.BOTANICO -> {
            contorno(if (hoy) acento else linea, if (hoy) 1.2f else 0.6f)
            drawLine(linea.copy(alpha = 0.22f), Offset(2f * s, h - 2f * s), Offset(w - 2f * s, 2f * s), 0.6f * s)
            if (hoy) { drawLine(Color(0xFF5A4A3A), Offset(w / 2, 0f), Offset(w / 2, 5f * s), 0.7f * s); drawCircle(acento, 1.8f * s, Offset(w / 2, 1.8f * s)) }
            if (sellado) sello(oro)
        }
        // Nieve sobre el borde de arriba; hoy, el borde de bastón de caramelo.
        TemaId.NAVIDAD -> {
            if (hoy) {
                drawOutline(borde, Color.White, style = Stroke(2.4f * s))
                drawOutline(borde, acento, style = Stroke(2.4f * s, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f * s, 3f * s))))
            } else contorno(linea, 0.6f)
            var x = 0f; var k = 0
            while (x < w + 2f * s) { drawCircle(Color.White.copy(alpha = 0.85f), (if (k % 2 == 0) 2.2f else 1.6f) * s, Offset(x, 0f)); x += 3.2f * s; k++ }
            if (sellado) sello(acento)
        }
        // Celosía: esquinas en escuadra doble; el día sellado lleva el sello de cinabrio.
        TemaId.SEDA -> {
            contorno(if (hoy) acento else linea, if (hoy) 1.3f else 0.5f)
            val m = 1.4f * s; val l = 6f * s; val l2 = 3.5f * s; val m2 = 2.8f * s
            drawPath(Path().apply {
                moveTo(m, l); lineTo(m, m); lineTo(l, m); moveTo(w - l, m); lineTo(w - m, m); lineTo(w - m, l)
                moveTo(w - m, h - l); lineTo(w - m, h - m); lineTo(w - l, h - m); moveTo(l, h - m); lineTo(m, h - m); lineTo(m, h - l)
                moveTo(w - l2 - m2, m2); lineTo(w - m2, m2); lineTo(w - m2, m2 + l2)
                moveTo(m2, h - m2 - l2); lineTo(m2, h - m2); lineTo(m2 + l2, h - m2)
            }, if (hoy) oro else linea, style = Stroke(0.7f * s))
            if (sellado) {
                val cinabrio = Color(0xFFB8322A); val papel = Color(0xFFF3E3C8)
                drawRect(cinabrio, Offset(pc.x - 4.6f * s, pc.y - 4.6f * s), Size(9.2f * s, 9.2f * s))
                drawRect(papel.copy(alpha = 0.8f), Offset(pc.x - 3.4f * s, pc.y - 3.4f * s), Size(6.8f * s, 6.8f * s), style = Stroke(0.6f * s))
                figuraTema(tema, papel, pc, 2.6f * s, cinabrio)
            }
        }
        // Vidriera: el plomo del borde y la lanceta por dentro; hoy, plomo dorado y una estrella.
        TemaId.GOTICO -> {
            val plomo = Color(0xFF1C1A24).copy(alpha = 0.75f)
            contorno(if (hoy) oro else plomo, if (hoy) 1.6f else 1.1f)
            drawPath(Path().apply {
                moveTo(2.4f * s, h); lineTo(2.4f * s, h * 0.5f); quadraticTo(2.4f * s, h * 0.26f, w / 2, h * 0.2f)
                quadraticTo(w - 2.4f * s, h * 0.26f, w - 2.4f * s, h * 0.5f); lineTo(w - 2.4f * s, h)
            }, plomo.copy(alpha = 0.35f), style = Stroke(0.7f * s))
            if (hoy) drawPath(figEstrella(Offset(w / 2, 3.4f * s), 2.4f * s, 1f * s, 5), oro)
            if (sellado) sello(acento)
        }
        // Mira de la pantalla de control: solo las esquinas.
        TemaId.ESPACIO -> {
            val col = if (hoy) acento else linea; val l = 5f * s
            drawPath(Path().apply {
                moveTo(0f, l); lineTo(0f, 0f); lineTo(l, 0f); moveTo(w - l, 0f); lineTo(w, 0f); lineTo(w, l)
                moveTo(w, h - l); lineTo(w, h); lineTo(w - l, h); moveTo(l, h); lineTo(0f, h); lineTo(0f, h - l)
            }, col, style = Stroke((if (hoy) 1.4f else 0.8f) * s * 2))
            if (hoy) contorno(acento.copy(alpha = 0.35f), 0.5f)
            if (sellado) sello(acento)
        }
        // Alicatado: doble filete y los rombos del lazo arriba y abajo.
        TemaId.ANDALUS -> {
            contorno(if (hoy) oro else linea, if (hoy) 1.3f else 0.6f)
            drawRect(oro.copy(alpha = 0.5f), Offset(1.8f * s, 1.8f * s), Size(w - 3.6f * s, h - 3.6f * s), style = Stroke(0.5f * s))
            listOf(Offset(w / 2, 0f), Offset(w / 2, h)).forEach { drawPath(figPoligono(it, 2.2f * s, 4, -90f), if (hoy) oro else linea) }
            if (sellado) sello(oro)
        }
        // Fieltro: la franja de cielo arriba y la puntada por dentro.
        TemaId.MONGOL -> {
            drawRect(Brush.verticalGradient(listOf(acento.copy(alpha = if (hoy) 0.55f else 0.25f), Color.Transparent), 0f, 4f * s), Offset.Zero, Size(w, 4f * s))
            contorno(if (hoy) acento else linea, if (hoy) 1.3f else 0.6f)
            drawRoundRect(linea.copy(alpha = 0.5f), Offset(2f * s, 2f * s), Size(w - 4f * s, h - 4f * s), CornerRadius(6f * s),
                style = Stroke(0.5f * s, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * s, 1.6f * s))))
            if (sellado) sello(oro)
        }
        // Trazo de construcción: diagonales y el círculo inscrito; hoy, en sanguina.
        TemaId.RENACIMIENTO -> {
            val guia = linea.copy(alpha = 0.22f)
            drawLine(guia, Offset.Zero, Offset(w, h), 0.5f * s); drawLine(guia, Offset(w, 0f), Offset(0f, h), 0.5f * s)
            drawCircle(if (hoy) acento.copy(alpha = 0.6f) else guia, minOf(w, h) / 2 - s, center, style = Stroke((if (hoy) 0.8f else 0.5f) * s))
            contorno(if (hoy) acento else linea, if (hoy) 1.2f else 0.5f)
            if (sellado) sello(acento)
        }
        // Cartel constructivista: barra gruesa abajo y cuña roja en la esquina.
        TemaId.URSS -> {
            contorno(if (hoy) acento else linea, if (hoy) 1.6f else 0.5f)
            drawRect(if (hoy) acento else linea, Offset(0f, h - 2.4f * s), Size(w, 2.4f * s))
            drawPath(Path().apply { moveTo(w - 6f * s, 0f); lineTo(w, 0f); lineTo(w, 6f * s); close() }, acento.copy(alpha = 0.85f))
            if (sellado) sello(acento)
        }
        // La franja tricolor abajo; hoy, también arriba.
        TemaId.REVOLUCION -> {
            contorno(if (hoy) acento else linea, if (hoy) 1.3f else 0.5f)
            val tri = listOf(Color(0xFF1F3A93), Color(0xFFF4F1EA), Color(0xFFC8102E))
            tri.forEachIndexed { i, col -> drawRect(col, Offset(w * i / 3f, h - 1.8f * s), Size(w / 3f, 1.8f * s)) }
            if (hoy) tri.forEachIndexed { i, col -> drawRect(col, Offset(w * i / 3f, 0f), Size(w / 3f, 1.8f * s)) }
            if (sellado) sello(acento)
        }
        // Losa de piedra: chaflanes y bisel; hoy, una runa grabada arriba.
        TemaId.ANTIGUAS -> {
            contorno(if (hoy) acento else linea, if (hoy) 1.3f else 0.7f)
            val m = 1.8f * s; val k = 3.2f * s
            drawPath(Path().apply {
                moveTo(m + k, m); lineTo(w - m - k, m); lineTo(w - m, m + k); lineTo(w - m, h - m - k); lineTo(w - m - k, h - m); lineTo(m + k, h - m)
                lineTo(m, h - m - k); lineTo(m, m + k); close()
            }, Color.White.copy(alpha = 0.18f), style = Stroke(0.6f * s))
            if (hoy) {
                drawLine(acento, Offset(w / 2, 0.6f * s), Offset(w / 2, 4f * s), 0.8f * s)
                drawLine(acento, Offset(w / 2 - 2f * s, 0.6f * s), Offset(w / 2 + 2f * s, 3f * s), 0.8f * s)
                drawLine(acento, Offset(w / 2 + 2f * s, 0.6f * s), Offset(w / 2 - 2f * s, 3f * s), 0.8f * s)
            }
            if (sellado) sello(oro)
        }
    }
}

@Composable
private fun Celda(modifier: Modifier, fecha: String, fondo: Color, hoy: Boolean, futuro: Boolean, verFotos: Boolean, onClick: () -> Unit) {
    val d = Repo.dias[fecha]
    // Con «fotos», los días con foto del día la muestran de fondo (las letras van en blanco sobre una sombra).
    val conFoto = verFotos && d?.foto != null
    val tinta = if (conFoto) Color.White else Tonos.sobre(fondo)
    val nMom = Repo.momentos.count { it.fecha == fecha && it.tipo != "muestreo" && !it.privado }
    val marca = io.github.ruddy2003.vestigium.ui.Estilo.marca(fecha)
    // Sellado: con ánimo y diario, lo mismo que pide la hoja de la noche para «día sellado».
    val sellado = d != null && d.valencia != null && d.nota.isNotBlank()
    val tema = Estilo.tema
    val forma = formaCelda(tema)
    val cs = MaterialTheme.colorScheme
    Box(
        modifier.aspectRatio(0.78f).clip(forma).background(fondo)
            .drawWithContent { drawContent(); marcoCelda(tema, forma, hoy, sellado, cs.outline, cs.primary, cs.tertiary, cs.onPrimary) }
            .clickable(onClick = onClick),
    ) {
        if (conFoto) {
            Miniatura(android.net.Uri.parse(d!!.foto), 200, Modifier.matchParentSize())
            Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.28f)))
        }
        Box(Modifier.matchParentSize().padding(3.dp)) {
        Text(fecha.takeLast(2).trimStart('0'), style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), color = if (futuro) tinta.copy(alpha = 0.4f) else tinta,
            modifier = Modifier.align(Alignment.TopStart))
        if (marca != null) Text(marca, style = MaterialTheme.typography.titleLarge.copy(fontSize = 7.sp, letterSpacing = 0.5.sp), color = tinta.copy(alpha = 0.7f), modifier = Modifier.align(Alignment.TopEnd))
        Row(Modifier.align(Alignment.BottomStart), verticalAlignment = Alignment.CenterVertically) {
            if (d != null && d.nota.isNotBlank()) Box(Modifier.size(5.dp).background(tinta))
            if (d != null && d.hitos.isNotEmpty()) Text("✦", fontSize = 8.sp, color = tinta, modifier = Modifier.padding(start = 2.dp))
            if (nMom > 0) Text("•".repeat(minOf(nMom, 3)), fontSize = 9.sp, color = tinta, modifier = Modifier.padding(start = 2.dp))
        }
        if (d?.foto != null && !conFoto) Box(Modifier.align(Alignment.BottomEnd).size(4.dp).background(MaterialTheme.colorScheme.primary))
        }
    }
}

