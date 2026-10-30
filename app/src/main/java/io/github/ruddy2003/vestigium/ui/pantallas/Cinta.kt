package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.data.privado
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Sustancias
import io.github.ruddy2003.vestigium.sensores.Galeria
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TextoMarcado
import io.github.ruddy2003.vestigium.ui.figuraTema
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.esOscuro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime

/** Algo con hora en la cinta: un tramo (de [a] a [b]) o un instante (b = null), con su texto al tocarlo. */
private class Cosa(val pista: Int, val a: Long, val b: Long?, val color: Color, val rotulo: String, val detalle: String, val alto: Float = 1f)

/** Una pista de la cinta: su nombre y su ícono (dibujado, ver [io.github.ruddy2003.vestigium.ui.IconoPista]). */
private class PistaCinta(val nombre: String, val icono: String)

/** Lo que se pinta dentro de un bloque de la cinta. */
private enum class RellenoCinta { LISO, FILETE, RAYAS, DIAGONAL, PUNTOS, CONTORNO, DEGRADADO, SEGMENTOS, PUNTADA }

/**
 * Cómo se ve la cinta en cada tema: la esquina de los bloques ([radio] en dp; [cortado], en chaflán), lo que llevan dentro,
 * el guion de las horas menores (en dp; null, línea seguida) y el remate de las marcas sueltas. Los nodos, las horas
 * mayores y el «ahora» llevan además la figura del tema ([figuraTema]).
 */
private class PielCinta(val radio: Float, val cortado: Boolean, val relleno: RellenoCinta, val guion: FloatArray?, val cap: StrokeCap)

private fun pielCinta(tema: TemaId): PielCinta = when (tema) {
    // Sillares con el filo claro arriba.
    TemaId.ROMANO -> PielCinta(0f, false, RellenoCinta.FILETE, null, StrokeCap.Square)
    // Bloques de fósforo con líneas de barrido y horas punteadas.
    TemaId.CIBER -> PielCinta(0f, false, RellenoCinta.RAYAS, floatArrayOf(1.5f, 3f), StrokeCap.Butt)
    // Bloques como letreros: esquinas redondas y su tubo por dentro.
    TemaId.NEON -> PielCinta(8f, false, RellenoCinta.CONTORNO, floatArrayOf(5f, 2f), StrokeCap.Round)
    // Bloques con su contorno, como relieves.
    TemaId.HELENO -> PielCinta(2f, false, RellenoCinta.CONTORNO, floatArrayOf(6f, 3f), StrokeCap.Round)
    // Bloques en segmentos, como la cenefa de rectángulos de las pinturas, y las horas en trazos cortos de cálamo.
    TemaId.EGIPTO -> PielCinta(3f, false, RellenoCinta.SEGMENTOS, floatArrayOf(2f, 4f), StrokeCap.Square)
    // Tinta que se carga arriba y se aclara.
    TemaId.MEDIEVAL -> PielCinta(1f, false, RellenoCinta.DEGRADADO, null, StrokeCap.Round)
    // Sillares tallados: chaflán en las esquinas, la junta de mortero y las horas como saeteras.
    TemaId.TEMPLARIO -> PielCinta(2.5f, true, RellenoCinta.FILETE, floatArrayOf(2f, 4f), StrokeCap.Square)
    // Sello rayado del expediente.
    TemaId.DISTOPIA -> PielCinta(0f, false, RellenoCinta.DIAGONAL, floatArrayOf(8f, 2f), StrokeCap.Square)
    // Indicador en segmentos.
    TemaId.REFUGIO -> PielCinta(6f, false, RellenoCinta.SEGMENTOS, floatArrayOf(3f, 3f), StrokeCap.Round)
    // Placa de acero en chaflán.
    TemaId.ORDEN -> PielCinta(3f, true, RellenoCinta.CONTORNO, floatArrayOf(10f, 3f), StrokeCap.Butt)
    // Muestra punteada sobre papel milimétrico.
    TemaId.LABORATORIO -> PielCinta(3f, false, RellenoCinta.PUNTOS, floatArrayOf(1f, 3f), StrokeCap.Round)
    // Retazo de tela pespunteado.
    TemaId.COLONOS -> PielCinta(2f, false, RellenoCinta.PUNTADA, floatArrayOf(5f, 5f), StrokeCap.Round)
    // Galón con brillo.
    TemaId.NAPOLEON -> PielCinta(1.5f, false, RellenoCinta.FILETE, floatArrayOf(4f, 2f, 1f, 2f), StrokeCap.Square)
    // Tallos redondeados.
    TemaId.BOTANICO -> PielCinta(10f, false, RellenoCinta.LISO, floatArrayOf(1f, 4f), StrokeCap.Round)
    // Bastón de caramelo.
    TemaId.NAVIDAD -> PielCinta(6f, false, RellenoCinta.DIAGONAL, floatArrayOf(4f, 4f), StrokeCap.Round)
    // Seda tejida.
    TemaId.SEDA -> PielCinta(5f, false, RellenoCinta.RAYAS, floatArrayOf(6f, 2f), StrokeCap.Round)
    // Vidriera emplomada.
    TemaId.GOTICO -> PielCinta(0f, false, RellenoCinta.SEGMENTOS, null, StrokeCap.Butt)
    // Barra luminosa de la consola.
    TemaId.ESPACIO -> PielCinta(3f, false, RellenoCinta.DEGRADADO, floatArrayOf(1f, 5f), StrokeCap.Round)
    // Ristras de burbujas: bloques muy redondos llenos de puntos.
    TemaId.ABISMO -> PielCinta(7f, false, RellenoCinta.PUNTOS, floatArrayOf(0.1f, 3.5f), StrokeCap.Round)
    // Azulejo en chaflán con su punteado.
    TemaId.ANDALUS -> PielCinta(2.5f, true, RellenoCinta.PUNTOS, floatArrayOf(3f, 2f), StrokeCap.Square)
    // Fieltro cosido.
    TemaId.MONGOL -> PielCinta(4f, false, RellenoCinta.PUNTADA, floatArrayOf(7f, 3f), StrokeCap.Round)
    // Sombreado a pluma.
    TemaId.RENACIMIENTO -> PielCinta(0f, false, RellenoCinta.DIAGONAL, floatArrayOf(10f, 4f), StrokeCap.Round)
    // Bloques planos de cartel.
    TemaId.URSS -> PielCinta(0f, false, RellenoCinta.LISO, null, StrokeCap.Square)
    // Bandas de cartel con el filo claro.
    TemaId.REVOLUCION -> PielCinta(0.5f, true, RellenoCinta.FILETE, floatArrayOf(5f, 2f), StrokeCap.Butt)
    // Losas de piedra labrada.
    TemaId.ANTIGUAS -> PielCinta(2f, true, RellenoCinta.CONTORNO, floatArrayOf(2f, 6f), StrokeCap.Square)
}

/** Un tramo de la cinta con la piel del tema: su forma (esquina o chaflán) y lo que lleva dentro. */
private fun DrawScope.bloqueCinta(p: PielCinta, color: Color, a: Offset, t: Size) {
    val d = density
    val k = minOf(p.radio * d, t.width / 2, t.height / 2)
    val forma = Path().apply {
        if (p.cortado) {
            moveTo(a.x + k, a.y); lineTo(a.x + t.width - k, a.y); lineTo(a.x + t.width, a.y + k); lineTo(a.x + t.width, a.y + t.height - k)
            lineTo(a.x + t.width - k, a.y + t.height); lineTo(a.x + k, a.y + t.height); lineTo(a.x, a.y + t.height - k); lineTo(a.x, a.y + k); close()
        } else addRoundRect(RoundRect(a.x, a.y, a.x + t.width, a.y + t.height, CornerRadius(k, k)))
    }
    drawPath(forma, color)
    val claro = Color.White.copy(alpha = 0.3f); val oscuro = Color.Black.copy(alpha = 0.22f)
    val der = a.x + t.width; val abajo = a.y + t.height
    clipPath(forma) {
        when (p.relleno) {
            RellenoCinta.LISO -> {}
            RellenoCinta.FILETE -> drawRect(claro, a, Size(t.width, 2f * d))
            RellenoCinta.RAYAS -> { var y = a.y + 1.5f * d; while (y < abajo) { drawLine(oscuro, Offset(a.x, y), Offset(der, y), 0.8f * d); y += 3f * d } }
            RellenoCinta.DIAGONAL -> { var x = a.x - t.height; while (x < der) { drawLine(claro, Offset(x, abajo), Offset(x + t.height, a.y), 1.6f * d); x += 6f * d } }
            RellenoCinta.PUNTOS -> {
                var y = a.y + 2.5f * d
                while (y < abajo) { var x = a.x + 2.5f * d; while (x < der) { drawCircle(claro, 0.9f * d, Offset(x, y)); x += 5f * d }; y += 5f * d }
            }
            RellenoCinta.CONTORNO -> drawPath(forma, oscuro, style = Stroke(2f * d))
            RellenoCinta.DEGRADADO -> drawRect(Brush.verticalGradient(listOf(claro, Color.Transparent, oscuro), a.y, abajo), a, t)
            RellenoCinta.SEGMENTOS -> { var x = a.x + 7f * d; while (x < der) { drawLine(oscuro.copy(alpha = 0.45f), Offset(x, a.y), Offset(x, abajo), 1.4f * d); x += 7f * d } }
            RellenoCinta.PUNTADA -> if (t.width > 5f * d) drawLine(claro.copy(alpha = 0.7f), Offset(a.x + 2f * d, a.y + t.height / 2), Offset(der - 2f * d, a.y + t.height / 2), 1f * d,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f * d, 2.5f * d)))
        }
    }
}

/**
 * La cinta del día: 29 horas (de las 0:00 a las 5:00 del día siguiente) en una tira que se desliza, con una pista por
 * cada cosa que pasó: cielo, sueño, siestas y enfoque, lugares, ejercicio, pantalla, notificaciones, luz, música,
 * momentos, sustancias (y la cafeína y el alcohol estimados en el cuerpo), comidas, gastos y fotos. Tocar algo lo explica abajo.
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun PantallaCinta(nav: Nav) {
    var fecha by remember { mutableStateOf(Seleccion.cinta ?: Fechas.diaLogico()) }
    LaunchedEffect(fecha) { Seleccion.cinta = fecha }
    val ctx = LocalContext.current
    val cs = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    val tema = Estilo.tema
    val piel = pielCinta(tema)
    val ini = Fechas.inicioDia(fecha)
    val fin = ini + 29 * 3_600_000L
    val musica by produceState(emptyList<Pair<Long, String>>(), fecha) {
        value = withContext(Dispatchers.IO) { runCatching { Repo.escuchas(fecha, Fechas.sumar(fecha, 1)).map { Fechas.millis(it.ts) to (it.titulo + if (it.artista.isNotBlank()) " · ${it.artista}" else "") } }.getOrDefault(emptyList()) }
    }
    val fotos by produceState(emptyList<Long>(), fecha) {
        value = withContext(Dispatchers.IO) { runCatching { if (Galeria.tienePermiso(ctx)) Galeria.delDia(ctx, fecha).map { it.ms } else emptyList() }.getOrDefault(emptyList()) }
    }
    val comidas by produceState(emptyList<Pair<Long, String>>(), fecha) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                Repo.comidas(fecha, fecha).mapNotNull { c ->
                    val h = runCatching { LocalTime.parse(c.hora.take(5)) }.getOrNull() ?: return@mapNotNull null
                    Fechas.millisDe(java.time.LocalDateTime.of(Fechas.parse(c.fecha), h)) to c.alimento
                }
            }.getOrDefault(emptyList())
        }
    }

    // La cafeína y el alcohol con su hora (de MacroFactor o anotados), desde el día anterior para lo que sigue en el cuerpo.
    val dosis by produceState(emptyList<Sustancias.Dosis>() to emptyList<Sustancias.Dosis>(), fecha, Repo.consumos) {
        value = withContext(Dispatchers.IO) { runCatching { Sustancias.dosis(Fechas.sumar(fecha, -1), Fechas.sumar(fecha, 1)) }.getOrDefault(emptyList<Sustancias.Dosis>() to emptyList<Sustancias.Dosis>()) }
    }
    val pistas = ArrayList<PistaCinta>()
    val cosas = ArrayList<Cosa>()
    fun pista(nombre: String, icono: String): Int { pistas += PistaCinta(nombre, icono); return pistas.lastIndex }
    fun hm(ms: Long) = Fechas.horaDe(Fechas.isoDeMillis(ms))

    // Sueño: la noche que terminó esta mañana y la que empieza esta noche.
    val noches = listOf(fecha, Fechas.sumar(fecha, 1)).mapNotNull { f -> Repo.salud[f]?.let { s -> if (s.sueno_inicio != null && s.sueno_fin != null) s else null } }
    val tramos = Repo.tramos.filter { it.fecha == fecha }
    if (noches.isNotEmpty() || tramos.any { it.tipo == "siesta" }) {
        val p = pista("sueño", "sueno")
        noches.forEach { s ->
            val a = Fechas.millis(s.sueno_inicio!!); val b = Fechas.millis(s.sueno_fin!!)
            cosas += Cosa(p, a, b, cs.secondary, "${(s.sueno_min ?: 0) / 60} h", "Dormiste de ${hm(a)} a ${hm(b)}" + (s.sueno_min?.let { " · ${it / 60} h ${it % 60} min" } ?: ""))
        }
        tramos.filter { it.tipo == "siesta" }.forEach { t ->
            val a = Fechas.millis(t.inicio)
            cosas += Cosa(p, a, a + t.min * 60_000L, cs.tertiary, "siesta", "Siesta de ${t.min} min a las ${hm(a)}")
        }
    }
    val enfoque = tramos.filter { it.tipo != "siesta" }
    if (enfoque.isNotEmpty()) {
        val p = pista("enfoque", "enfoque")
        val colores = listOf(cs.primary, cs.tertiary, cs.secondary)
        enfoque.forEach { t ->
            val a = Fechas.millis(t.inicio)
            val i = Catalogo.ENFOQUE.indexOfFirst { it.id == t.tipo }.coerceAtLeast(0)
            cosas += Cosa(p, a, a + t.min * 60_000L, colores[i % 3], Catalogo.nombreTramo(t.tipo).lowercase(),
                "${Catalogo.nombreTramo(t.tipo)}: ${t.min} min desde las ${hm(a)}" + if (t.nota.isNotBlank()) " — ${t.nota}" else "")
        }
    }
    val visitas = Repo.visitas.filter { Fechas.millis(it.entrada) < fin && (it.salida?.let { s -> Fechas.millis(s) } ?: System.currentTimeMillis()) > ini }
    if (visitas.isNotEmpty()) {
        val p = pista("lugares", "lugares")
        val ids = visitas.map { it.lugar }.distinct()
        val colores = listOf(cs.primary, cs.secondary, cs.tertiary)
        visitas.forEach { v ->
            val a = Fechas.millis(v.entrada); val b = v.salida?.let { Fechas.millis(it) } ?: System.currentTimeMillis()
            val n = Repo.lugares.firstOrNull { it.id == v.lugar }?.nombre ?: "lugar"
            cosas += Cosa(p, a, b, colores[ids.indexOf(v.lugar) % 3], n, "$n: de ${hm(a)} a ${if (v.salida == null) "ahora" else hm(b)}")
        }
    }
    val ses = Repo.sesiones.filter { it.fecha == fecha }
    val esc = Repo.ascensos.filter { it.fecha == fecha && it.inicio != null }
    if (ses.isNotEmpty() || esc.isNotEmpty()) {
        val p = pista("ejercicio", "ejercicio")
        ses.forEach { s -> val a = Fechas.millis(s.inicio); val b = Fechas.millis(s.fin); cosas += Cosa(p, a, b, cs.tertiary, s.tipo_nombre.lowercase(), "${s.tipo_nombre}: ${s.min} min desde las ${hm(a)}") }
        esc.forEach { x ->
            val h = runCatching { LocalTime.parse(x.inicio!!.take(5)) }.getOrNull() ?: return@forEach
            val a = Fechas.millisDe(java.time.LocalDateTime.of(Fechas.parse(x.fecha), h))
            cosas += Cosa(p, a, a + (x.min ?: 90) * 60_000L, cs.primary, "escalada", "Escalada en ${x.lugar.ifBlank { "el muro" }}: ${x.bloques.size} bloques")
        }
    }
    Repo.pantalla[fecha]?.takeIf { it.por_hora_min.sum() > 0 }?.let { pd ->
        val h = pd.por_hora_min
        val p = pista("pantalla", "pantalla")
        // Con la ventana del día lógico (v13), las horas 0 a 4 son de la madrugada siguiente.
        fun desde(i: Int) = ini + (if (pd.ventana == "logica" && i < 5) i + 24 else i) * 3_600_000L
        h.forEachIndexed { i, m -> if (m > 0) cosas += Cosa(p, desde(i), desde(i) + 3_600_000L, cs.primary, "", "De ${i}:00 a ${i + 1}:00: $m min de pantalla", alto = (m / 60f).coerceIn(0.08f, 1f)) }
    }
    Repo.notifs[fecha]?.por_hora?.takeIf { it.sum() > 0 }?.let { h ->
        val p = pista("avisos", "avisos")
        val max = (h.maxOrNull() ?: 1).coerceAtLeast(1)
        h.forEachIndexed { i, n -> if (n > 0) cosas += Cosa(p, ini + i * 3_600_000L, ini + (i + 1) * 3_600_000L, cs.secondary, "", "De ${i}:00 a ${i + 1}:00: $n notificaciones", alto = (n.toFloat() / max).coerceIn(0.08f, 1f)) }
    }
    Repo.conexiones[fecha]?.let { cx ->
        if (cx.luz_n.sum() > 0) {
            val p = pista("luz", "luz")
            val vals = cx.luz_suma.indices.map { i -> if (cx.luz_n[i] > 0) cx.luz_suma[i] / cx.luz_n[i] else 0.0 }
            vals.forEachIndexed { i, v -> if (v > 0) cosas += Cosa(p, ini + i * 3_600_000L, ini + (i + 1) * 3_600_000L, Color(0xFFE8B923), "", "De ${i}:00 a ${i + 1}:00: unos ${v.toInt()} lux" + if (v > 1000) " (luz de exterior)" else "",
                alto = (kotlin.math.ln(1 + v) / kotlin.math.ln(1 + 20000.0)).toFloat().coerceIn(0.08f, 1f)) }
        }
    }
    if (musica.isNotEmpty()) { val p = pista("música", "musica"); musica.forEach { (t, s) -> cosas += Cosa(p, t, null, cs.primary, "", "${hm(t)} · $s") } }
    val mom = Repo.momentos.filter { it.fecha == fecha && !it.privado }
    if (mom.isNotEmpty()) {
        val p = pista("momentos", "momentos")
        mom.forEach { m ->
            val t = Fechas.millis(m.ts)
            val color = if (m.tipo == "recaida") cs.error else Tonos.valencia(m.valencia?.toDouble(), oscuro) ?: cs.secondary
            val dur = m.duracion_min?.takeIf { it > 0 }
            cosas += Cosa(p, t, dur?.let { t + it * 60_000L }, color, Catalogo.titulo(m, Repo.config.discreto).take(1),
                "${hm(t)} · ${Catalogo.titulo(m, Repo.config.discreto)}" + (Catalogo.pensamientoDe(m)?.takeIf { Catalogo.emocionesDe(m).isNotEmpty() && !Repo.config.discreto }
                    ?.let { p0 -> " · pensando: " + (Catalogo.PENSAMIENTOS.firstOrNull { it.id == p0 }?.nombre?.lowercase() ?: p0) } ?: "") + (dur?.let { " · $it min" } ?: "") + (m.valencia?.let { " · $it/7" } ?: "") + (if (m.texto.isNotBlank()) " — ${m.texto}" else "") +
                    (Repo.lugares.firstOrNull { it.id == m.lugar }?.nombre?.let { " · en $it" } ?: ""))
        }
    }
    // Sustancias: el nivel que dijiste (barras), y la cafeína y el alcohol estimados en el cuerpo.
    val consumos = Repo.consumos.filter { Sustancias.visible(it) && Fechas.millis(it.inicio) < fin && Sustancias.finEfectivo(it) > ini - 6 * 3_600_000L }
    if (consumos.isNotEmpty()) {
        val p = pista("sustancias", "sustancias")
        val paso = 10 * 60_000L
        consumos.forEach { x ->
            val a = Fechas.millis(x.inicio)
            val nombre = Catalogo.nombreSustancia(x)
            if (Catalogo.sustancia(x.sustancia)?.conNivel == true) {
                val z = Sustancias.finEfectivo(x)
                var t = a
                var algo = false
                while (t < z) {
                    val n = Sustancias.nivelEn(x, t + paso / 2)
                    if (n != null && n > 0) { algo = true
                        cosas += Cosa(p, t, t + paso, Color(0xFFB0567A), "", "${hm(t)} · $nombre: nivel ~${"%.0f".format(n)}/10" + resumenConsumo(x).let { r -> if (r.isNotBlank()) " ($r)" else "" }, alto = (n / 10f).toFloat().coerceIn(0.08f, 1f)) }
                    t += paso
                }
                if (!algo) cosas += Cosa(p, a, z.coerceAtLeast(a + paso), Color(0xFFB0567A), nombre.lowercase(), "${hm(a)} · $nombre" + resumenConsumo(x).let { r -> if (r.isNotBlank()) " · $r" else "" })
            } else cosas += Cosa(p, a, null, Color(0xFF8C6A4A), "", "${hm(a)} · $nombre" + (x.cantidad?.let { q -> " · ${q.toInt()} ${Catalogo.sustancia(x.sustancia)?.unidad ?: ""}" } ?: ""))
        }
    }
    if (dosis.first.isNotEmpty() && Sustancias.visible("cafeina")) {
        val p = pista("cafeína en el cuerpo", "cafeina")
        val paso = 20 * 60_000L
        val instantes = (ini until fin step paso).toList()
        val v = Sustancias.cafeinaEn(dosis.first, instantes)
        instantes.zip(v).forEach { (t, mg) -> if (mg >= 5) cosas += Cosa(p, t, t + paso, Color(0xFF8C6A4A), "", "${hm(t)} · unos ${mg.toInt()} mg de cafeína en el cuerpo (estimado)", alto = (mg / 250.0).toFloat().coerceIn(0.06f, 1f)) }
        dosis.first.filter { it.ms in ini..fin }.forEach { d -> cosas += Cosa(p, d.ms, null, cs.onSurface, "", "${hm(d.ms)} · ${d.cantidad.toInt()} mg de cafeína") }
    }
    if (dosis.second.isNotEmpty() && Sustancias.visible("alcohol")) {
        val paso = 10 * 60_000L
        val instantes = (ini until fin step paso).toList()
        val v = Sustancias.alcoholEn(dosis.second, instantes)
        if (v.any { it > 0.01 }) {
            val p = pista("alcohol estimado", "sustancias")
            instantes.zip(v).forEach { (t, gl) -> if (gl > 0.01) cosas += Cosa(p, t, t + paso, Color(0xFF7A4FB0), "", "${hm(t)} · alcohol en sangre ≈ ${"%.2f".format(gl)} g/L (Widmark, estimado)", alto = (gl / 1.5).toFloat().coerceIn(0.06f, 1f)) }
        }
    }
    if (comidas.isNotEmpty()) { val p = pista("comidas", "comidas"); comidas.forEach { (t, s) -> cosas += Cosa(p, t, null, cs.tertiary, "", "${hm(t)} · $s") } }
    val gastos = Repo.gastos.filter { it.dia == fecha }
    if (gastos.isNotEmpty()) {
        val p = pista("gastos", "gastos")
        gastos.forEach { g ->
            runCatching { Fechas.millisDe(java.time.LocalDateTime.of(Fechas.parse(g.fecha), LocalTime.parse(g.hora.take(5)))) }.getOrNull()?.let { t ->
                cosas += Cosa(p, t, null, cs.error, "", "${g.hora} · $" + "%,.0f".format(g.monto) + " en ${g.categoria.lowercase()}" + if (g.nota.isNotBlank()) " — ${g.nota}" else "")
            }
        }
    }
    if (fotos.isNotEmpty()) { val p = pista("fotos", "fotos"); fotos.forEach { t -> cosas += Cosa(p, t, null, cs.secondary, "", "${hm(t)} · una foto") } }

    var elegida by remember(fecha) { mutableStateOf<Cosa?>(null) }
    val scroll = rememberScrollState()
    val densidad = LocalDensity.current
    val anchoHora = 64.dp
    LaunchedEffect(fecha) {
        kotlinx.coroutines.delay(60)
        val hora = if (fecha == Fechas.diaLogico()) (java.time.LocalTime.now().hour - 3).coerceAtLeast(0) else 6
        scroll.scrollTo(with(densidad) { (anchoHora * hora).roundToPx() })
    }

    Hoja("La cinta", nav.atras, "Tu día hora por hora. Desliza a los lados y toca algo para ver qué fue.") {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { fecha = Fechas.sumar(fecha, -1) }) { Text("‹", style = MaterialTheme.typography.titleLarge) }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(Estilo.fecha(fecha), style = MaterialTheme.typography.titleMedium, color = cs.primary)
                Text(Fechas.titulo(fecha).lowercase(), style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
            }
            TextButton(onClick = { fecha = Fechas.sumar(fecha, 1) }, enabled = fecha < Fechas.diaLogico()) { Text("›", style = MaterialTheme.typography.titleLarge) }
        }
        val e = Repo.entorno[fecha]
        if (e != null) Ayuda(listOfNotNull(e.t_max?.let { "máx ${it.toInt()}°" }, e.t_min?.let { "mín ${it.toInt()}°" }, e.precip_mm?.takeIf { it > 0.2 }?.let { "lluvia ${"%.1f".format(it)} mm" },
            e.amanecer?.let { "amanece ${it.takeLast(5)}" }, e.atardecer?.let { "anochece ${it.takeLast(5)}" }).joinToString(" · "), Modifier.padding(bottom = 6.dp))
        if (pistas.isEmpty()) { Ayuda("Este día no tiene nada con hora todavía."); return@Hoja }
        val alto = 34.dp
        val medidor = rememberTextMeasurer()
        val estiloRotulo = MaterialTheme.typography.labelSmall.copy(color = cs.onPrimary)
        val estiloEje = MaterialTheme.typography.labelSmall.copy(color = cs.onSurfaceVariant)
        val noche = cs.surfaceVariant
        val linea = cs.outline
        fun hora(s: String?) = s?.let { runCatching { LocalTime.parse(it.takeLast(5)) }.getOrNull() }
        val amanecer = hora(e?.amanecer); val atardecer = hora(e?.atardecer)
        Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
            Column(Modifier.width(30.dp)) {
                pistas.forEach { p -> Box(Modifier.height(alto), contentAlignment = Alignment.Center) { io.github.ruddy2003.vestigium.ui.IconoPista(p.icono, cs.primary, tam = 24.dp, tema = tema) } }
            }
            Box(Modifier.weight(1f).horizontalScroll(scroll)) {
                Canvas(Modifier.width(anchoHora * 29).height(alto * pistas.size + 22.dp).pointerInput(fecha, cosas.size) {
                    detectTapGestures { o ->
                        val h = alto.toPx()
                        val i = (o.y / h).toInt()
                        val ms = ini + ((o.x / size.width) * (fin - ini)).toLong()
                        val tol = (fin - ini) * (14.dp.toPx() / size.width)
                        elegida = cosas.filter { it.pista == i }.minByOrNull { c ->
                            val b = c.b
                            if (b != null && ms in c.a..b) 0L else minOf(kotlin.math.abs(ms - c.a), kotlin.math.abs(ms - (b ?: c.a)))
                        }?.takeIf { c -> val b = c.b; (b != null && ms in c.a..b) || minOf(kotlin.math.abs(ms - c.a), kotlin.math.abs(ms - (b ?: c.a))) < tol }
                    }
                }) {
                    val h = alto.toPx()
                    val total = h * pistas.size
                    fun x(ms: Long) = ((ms - ini).toFloat() / (fin - ini)) * size.width
                    // La noche, sombreada con el amanecer y el atardecer del día.
                    if (amanecer != null && atardecer != null) {
                        val a = x(ini + amanecer.toSecondOfDay() * 1000L); val t = x(ini + atardecer.toSecondOfDay() * 1000L)
                        drawRect(noche, Offset(0f, 0f), Size(a, total))
                        drawRect(Brush.horizontalGradient(listOf(noche.copy(alpha = 0f), noche), t, t + 40f), Offset(t, 0f), Size(size.width - t, total))
                    }
                    // Las horas: las menores con el guion del tema; las mayores (cada 6) con su figura arriba.
                    val guion = piel.guion?.let { g -> PathEffect.dashPathEffect(FloatArray(g.size) { g[it] * density }) }
                    for (hh in 0..29) {
                        val xa = x(ini + hh * 3_600_000L)
                        val mayor = hh % 6 == 0
                        drawLine(linea.copy(alpha = if (mayor) 0.6f else 0.2f), Offset(xa, 0f), Offset(xa, total), 1f, pathEffect = if (mayor) null else guion)
                        if (mayor) figuraTema(tema, linea.copy(alpha = 0.75f), Offset(xa, 3.5.dp.toPx()), 2.6.dp.toPx(), noche)
                        val r = medidor.measure("${hh % 24}", estiloEje)
                        drawText(r, topLeft = Offset((xa - r.size.width / 2).coerceIn(0f, size.width - r.size.width), total + 3f))
                    }
                    pistas.indices.forEach { i -> drawLine(linea.copy(alpha = 0.35f), Offset(0f, (i + 1) * h), Offset(size.width, (i + 1) * h), 0.6f) }
                    cosas.forEach { c ->
                        val y = c.pista * h
                        val b = c.b
                        if (b != null) {
                            val xa = x(c.a).coerceIn(0f, size.width); val xb = x(b).coerceIn(0f, size.width)
                            if (xb > xa) {
                                val alt = h * 0.64f * c.alto
                                bloqueCinta(piel, c.color.copy(alpha = if (c === elegida) 1f else 0.78f), Offset(xa, y + h * 0.18f + (h * 0.64f - alt)), Size(xb - xa - 1f, alt))
                                if (c.rotulo.isNotBlank() && xb - xa > 34f) {
                                    val r = medidor.measure(c.rotulo.take(((xb - xa) / 14f).toInt().coerceAtLeast(2)), estiloRotulo)
                                    drawText(r, topLeft = Offset(xa + 4f, y + (h - r.size.height) / 2))
                                }
                            }
                        } else {
                            val xa = x(c.a)
                            if (xa in 0f..size.width) {
                                if (c.rotulo.isNotBlank()) {
                                    figuraTema(tema, c.color, Offset(xa, y + h / 2), h * 0.3f, lerp(c.color, Color.Black, 0.3f))
                                    val r = medidor.measure(c.rotulo, estiloRotulo)
                                    drawText(r, topLeft = Offset(xa - r.size.width / 2, y + (h - r.size.height) / 2))
                                } else drawLine(c.color, Offset(xa, y + h * 0.2f), Offset(xa, y + h * 0.8f), 3f, cap = piel.cap)
                                if (c === elegida) drawCircle(c.color, h * 0.4f, Offset(xa, y + h / 2), style = Stroke(2f))
                            }
                        }
                    }
                    val ahora = System.currentTimeMillis()
                    if (ahora in ini..fin) {
                        val xa = x(ahora)
                        drawLine(cs.error, Offset(xa, 0f), Offset(xa, total), 2f)
                        figuraTema(tema, cs.error, Offset(xa, 4.5.dp.toPx()), 4.5.dp.toPx(), cs.surface)
                    }
                }
            }
        }
        // La leyenda con los mismos íconos.
        androidx.compose.foundation.layout.FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)) {
            pistas.forEach { p ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    io.github.ruddy2003.vestigium.ui.IconoPista(p.icono, cs.onSurfaceVariant, tam = 16.dp, tema = tema)
                    Text(" " + p.nombre, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                }
            }
        }
        Tarjeta(Modifier.padding(top = 10.dp)) {
            val c = elegida
            if (c == null) Ayuda("Toca un bloque o una marca de la cinta.")
            else TextoMarcado(c.detalle, style = MaterialTheme.typography.bodyMedium)
        }
        TextButton(onClick = { Seleccion.abrir(nav, fecha) }, modifier = Modifier.padding(top = 4.dp)) { Text("abrir este día ›", style = MaterialTheme.typography.labelLarge, color = cs.primary) }
    }
}
