package io.github.ruddy2003.vestigium.ui.pantallas

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.CiudadVisitada
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.SitioMarcado
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.UUID
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min

// =====================================================================
//  Geometría del mapa: proyección Natural Earth y contornos ya proyectados
// =====================================================================

private object GeoMapa {
    const val LAT_MIN = -58.0
    const val LAT_MAX = 84.0

    /** Proyección Natural Earth (Šavrič y otros, 2011): curva suave, polos planos, poca deformación. */
    fun x(lon: Double, lat: Double): Float {
        val f = lat.coerceIn(LAT_MIN, LAT_MAX) * PI / 180; val l = lon * PI / 180
        val f2 = f * f; val f4 = f2 * f2
        return (l * (0.870700 - 0.131979 * f2 - 0.013791 * f4 + 0.003971 * f2 * f4 * f4 - 0.001529 * f4 * f4 * f4)).toFloat()
    }
    fun y(lat: Double): Float {
        val f = lat.coerceIn(LAT_MIN, LAT_MAX) * PI / 180
        val f2 = f * f; val f6 = f2 * f2 * f2
        return -(f * (1.007226 + 0.015085 * f2 - 0.044475 * f6 + 0.028874 * f6 * f2 - 0.005916 * f6 * f2 * f2)).toFloat()
    }

    val limites = RectF(x(-180.0, 0.0), y(LAT_MAX), x(180.0, 0.0), y(LAT_MIN))

    class Forma(val pais: Mundo.Pais, val path: Path, val partes: Array<FloatArray>, val caja: RectF, val etiqueta: Offset)

    @Volatile private var cache: List<Forma>? = null

    fun formas(ctx: Context): List<Forma> = cache ?: synchronized(this) {
        cache ?: Mundo.paises(ctx).filter { it.codigo != "AQ" }.map { p ->
            val path = Path().apply { fillType = Path.FillType.EVEN_ODD }
            val caja = RectF(Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE)
            val partes = Array(p.partes.size) { k ->
                val a = p.partes[k]; val n = a.size / 2
                FloatArray(n * 2).also { o ->
                    for (i in 0 until n) {
                        val lon = a[2 * i] / 100.0; val lat = a[2 * i + 1] / 100.0
                        val px = x(lon, lat); val py = y(lat)
                        o[2 * i] = px; o[2 * i + 1] = py
                        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                        caja.union(px, py)
                    }
                    path.close()
                }
            }
            Forma(p, path, partes, caja, Offset(x(p.etiquetaLon.toDouble(), p.etiquetaLat.toDouble()), y(p.etiquetaLat.toDouble())))
        }.also { cache = it }
    }

    fun dentro(f: Forma, x: Float, y: Float): Boolean {
        if (!f.caja.contains(x, y)) return false
        var adentro = false
        for (a in f.partes) {
            val n = a.size / 2; var j = n - 1
            for (i in 0 until n) {
                val xi = a[2 * i]; val yi = a[2 * i + 1]; val xj = a[2 * j]; val yj = a[2 * j + 1]
                if ((yi > y) != (yj > y) && x < (xj - xi) * (y - yi) / (yj - yi) + xi) adentro = !adentro
                j = i
            }
        }
        return adentro
    }

    /** Paralelos y meridianos cada 30°. */
    val reticula: Path by lazy {
        Path().apply {
            for (lat in listOf(-30.0, 0.0, 30.0, 60.0)) { moveTo(x(-180.0, lat), y(lat)); var lo = -180.0; while (lo <= 180.0) { lineTo(x(lo, lat), y(lat)); lo += 5.0 } }
            for (lon in -180..180 step 30) { moveTo(x(lon.toDouble(), LAT_MIN), y(LAT_MIN)); var la = LAT_MIN; while (la <= LAT_MAX) { lineTo(x(lon.toDouble(), la), y(la)); la += 2.0 } }
        }
    }
    val contorno: Path by lazy {
        Path().apply {
            moveTo(x(-180.0, LAT_MAX), y(LAT_MAX)); lineTo(x(180.0, LAT_MAX), y(LAT_MAX))
            var la = LAT_MAX; while (la >= LAT_MIN) { lineTo(x(180.0, la), y(la)); la -= 2.0 }
            lineTo(x(-180.0, LAT_MIN), y(LAT_MIN))
            la = LAT_MIN; while (la <= LAT_MAX) { lineTo(x(-180.0, la), y(la)); la += 2.0 }
            close()
        }
    }
}

/** La lámina que se rasca: oro, plata, cobre o acero según el tema. */
private data class Lamina(val oscura: Int, val media: Int, val clara: Int)

private fun lamina(t: TemaId): Lamina = when (t) {
    TemaId.ROMANO, TemaId.HELENO, TemaId.MEDIEVAL, TemaId.GOTICO, TemaId.ANDALUS, TemaId.NAPOLEON, TemaId.SEDA, TemaId.URSS ->
        Lamina(0xFF7A5A12.toInt(), 0xFFC8A040.toInt(), 0xFFF6E7A8.toInt())
    TemaId.RENACIMIENTO, TemaId.COLONOS -> Lamina(0xFF6E3A1C.toInt(), 0xFFB87A4A.toInt(), 0xFFF0CDA6.toInt())
    TemaId.BOTANICO -> Lamina(0xFF5C4A22.toInt(), 0xFFA8894E.toInt(), 0xFFE9DBAE.toInt())
    TemaId.CIBER -> Lamina(0xFF0B2530.toInt(), 0xFF2C6F80.toInt(), 0xFF8FEFFF.toInt())
    TemaId.DISTOPIA, TemaId.REFUGIO, TemaId.ORDEN -> Lamina(0xFF45494E.toInt(), 0xFF8B9197.toInt(), 0xFFD7DBDF.toInt())
    else -> Lamina(0xFF6A7078.toInt(), 0xFFB7BCC3.toInt(), 0xFFF2F4F6.toInt())
}

/**
 * El mapa para rascar. Los países donde has estado se ven a color; los demás siguen cubiertos por la lámina.
 * [rascando] es el país que se está descubriendo ahora (se rasca con trazos en zigzag, como con una moneda).
 */
@Composable
internal fun MapaRascar(visitados: Set<String>, modifier: Modifier = Modifier, rascando: String? = null, avance: Float = 1f, elegido: String? = null,
                       zoom: Float = 1f, pan: Offset = Offset.Zero) {
    val ctx = LocalContext.current
    val formas = remember { GeoMapa.formas(ctx) }
    val cs = MaterialTheme.colorScheme
    val tema = Estilo.tema
    val oscuro = esOscuro()
    val lam = lamina(tema)
    val paleta = remember(cs, oscuro) {
        listOf(cs.primary, cs.secondary, cs.tertiary, lerp(cs.primary, cs.secondary, 0.5f), lerp(cs.primary, cs.surface, 0.3f), lerp(cs.secondary, cs.surface, 0.3f), lerp(cs.tertiary, cs.surface, 0.3f)).map { it.toArgb() }
    }
    val dens = LocalDensity.current.density
    val pinceles = remember(lam, cs) {
        object {
            val lamina = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = android.graphics.LinearGradient(-2.8f, -1.6f, -1.9f, -0.2f, intArrayOf(lam.oscura, lam.media, lam.clara, lam.media, lam.oscura, lam.media), null, android.graphics.Shader.TileMode.MIRROR)
            }
            val brillo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = android.graphics.LinearGradient(-2.8f, 1.2f, -2.2f, -1.6f, intArrayOf(0x00FFFFFF, 0x30FFFFFF, 0x00FFFFFF), null, android.graphics.Shader.TileMode.MIRROR)
            }
            val color = Paint(Paint.ANTI_ALIAS_FLAG)
            val borde = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; color = cs.onSurface.copy(alpha = 0.45f).toArgb() }
            val bordeLamina = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; color = lam.oscura }
            val reticula = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; color = cs.outline.copy(alpha = 0.35f).toArgb() }
            val mar = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = lerp(cs.surface, cs.primary, if (oscuro) 0.08f else 0.05f).toArgb() }
            val rasca = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND }
            val marca = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; color = cs.onSurface.toArgb() }
        }
    }
    Canvas(modifier.clipToBounds()) {
        val L = GeoMapa.limites
        val s = min(size.width / L.width(), size.height / L.height())
        val bx = (size.width - L.width() * s) / 2 - L.left * s
        val by = (size.height - L.height() * s) / 2 - L.top * s
        val k = s * zoom
        val c = drawContext.canvas.nativeCanvas
        c.save()
        c.translate(bx * zoom + pan.x + (size.width / 2) * (1 - zoom), by * zoom + pan.y + (size.height / 2) * (1 - zoom))
        c.scale(k, k)
        val px = 1f / k
        c.drawPath(GeoMapa.contorno, pinceles.mar)
        pinceles.reticula.strokeWidth = 0.8f * dens * px
        c.drawPath(GeoMapa.reticula, pinceles.reticula)
        pinceles.borde.strokeWidth = 0.6f * dens * px
        pinceles.bordeLamina.strokeWidth = 0.5f * dens * px
        val r = 2.4f * dens * px
        for (f in formas) {
            val codigo = f.pais.codigo
            val visto = codigo in visitados
            pinceles.color.color = paleta[(f.pais.color - 1).coerceAtLeast(0) % paleta.size]
            if (f.pais.diminuto) {
                // Los países diminutos (el Vaticano, Singapur, Malta…) son un punto que también se rasca.
                c.drawCircle(f.etiqueta.x, f.etiqueta.y, r, if (visto) pinceles.color else pinceles.lamina)
                c.drawCircle(f.etiqueta.x, f.etiqueta.y, r, if (visto) pinceles.borde else pinceles.bordeLamina)
                if (codigo == elegido) { pinceles.marca.strokeWidth = 1.6f * dens * px; c.drawCircle(f.etiqueta.x, f.etiqueta.y, r * 2.2f, pinceles.marca) }
                continue
            }
            if (visto && codigo != rascando) {
                c.drawPath(f.path, pinceles.color); c.drawPath(f.path, pinceles.borde)
            } else {
                c.drawPath(f.path, pinceles.lamina); c.drawPath(f.path, pinceles.brillo); c.drawPath(f.path, pinceles.bordeLamina)
                if (codigo == rascando && avance > 0f) {
                    // Rascado en zigzag: la lámina se va quitando a trazos hasta dejar el país a color.
                    c.save(); c.clipPath(f.path)
                    if (avance >= 1f) c.drawPath(f.path, pinceles.color)
                    else {
                        val b = f.caja; val filas = 7
                        val zig = Path().apply {
                            moveTo(b.left, b.top)
                            for (i in 0 until filas) {
                                val y0 = b.top + b.height() * i / filas; val y1 = b.top + b.height() * (i + 1) / filas
                                lineTo(if (i % 2 == 0) b.right else b.left, (y0 + y1) / 2); lineTo(if (i % 2 == 0) b.left else b.right, y1)
                            }
                        }
                        val pm = PathMeasure(zig, false); val parte = Path()
                        pm.getSegment(0f, pm.length * avance, parte, true)
                        pinceles.rasca.color = pinceles.color.color
                        pinceles.rasca.strokeWidth = b.height() / filas * 1.35f + 4 * px
                        c.drawPath(parte, pinceles.rasca)
                    }
                    c.restore()
                    c.drawPath(f.path, pinceles.borde)
                }
            }
            if (codigo == elegido) { pinceles.marca.strokeWidth = 1.8f * dens * px; c.drawPath(f.path, pinceles.marca) }
        }
        c.restore()
    }
}

/** Convierte un toque en pantalla a coordenadas del mapa y busca el país (primero los diminutos, que son un punto). */
private fun paisTocado(ctx: Context, toque: Offset, w: Float, h: Float, zoom: Float, pan: Offset, dens: Float): Mundo.Pais? {
    val L = GeoMapa.limites
    val s = min(w / L.width(), h / L.height())
    val bx = (w - L.width() * s) / 2 - L.left * s
    val by = (h - L.height() * s) / 2 - L.top * s
    val k = s * zoom
    val tx = bx * zoom + pan.x + (w / 2) * (1 - zoom); val ty = by * zoom + pan.y + (h / 2) * (1 - zoom)
    val x = (toque.x - tx) / k; val y = (toque.y - ty) / k
    val formas = GeoMapa.formas(ctx)
    val radio = 12f * dens / k
    formas.filter { it.pais.diminuto }.minByOrNull { (it.etiqueta - Offset(x, y)).getDistance() }?.let { if ((it.etiqueta - Offset(x, y)).getDistance() < radio) return it.pais }
    return formas.firstOrNull { !it.pais.diminuto && GeoMapa.dentro(it, x, y) }?.pais
}

// =====================================================================
//  Pantalla principal
// =====================================================================

private val PERIODOS_MUNDO = listOf("todo" to "todo", "anio" to "este año", "365" to "12 meses")

private fun desdeDe(periodo: String): Int? = when (periodo) {
    "anio" -> LocalDate.now().withDayOfYear(1).toEpochDay().toInt()
    "365" -> LocalDate.now().minusDays(365).toEpochDay().toInt()
    else -> null
}

/** Resumen calculado fuera del hilo principal y vuelto a calcular cuando cambian las ciudades o los países marcados. */
@Composable
internal fun rememberResumen(periodo: String): Mundo.Resumen? {
    Mundo.resumenFijo?.let { return it }
    val ctx = LocalContext.current
    val c = Repo.config
    val clave = listOf(c.ciudades_visitadas, c.paises_rascados, c.paises_tapados, c.unesco_marcados, c.unesco_descartados, Mundo.version)
    val r by produceState<Mundo.Resumen?>(null, periodo, clave) {
        value = withContext(Dispatchers.IO) { runCatching { Mundo.resumen(ctx, desdeDe(periodo)) }.getOrNull() }
    }
    return r
}

private fun dias(n: Int) = if (n == 1) "1 día" else "%,d días".format(n)
private fun horas(h: Double) = when { h < 1 -> "${(h * 60).toInt()} min"; h < 100 -> "%.0f h".format(h); else -> "%,.0f h".format(h) }
private fun mesAnio(d: Int): String = LocalDate.ofEpochDay(d.toLong()).let { it.month.getDisplayName(java.time.format.TextStyle.SHORT, Fechas.ES).replace(".", "") + " " + it.year }

@Composable
fun PantallaMundo(nav: Nav) {
    val ctx = LocalContext.current
    var periodo by remember { mutableStateOf("todo") }
    var vista by remember { mutableStateOf("ciudades") }
    var todas by remember { mutableStateOf(false) }
    val r = rememberResumen(periodo)
    val total = rememberResumen("todo")
    val sitios = remember { Mundo.sitios(ctx) }

    Hoja("Mundo", nav.atras, "Ciudades y países donde has estado, sacados de tus ubicaciones y de los que añadas a mano. Todo se calcula en el teléfono, sin internet.") {
        val t = total
        if (t == null) { Ayuda("Calculando dónde has estado…", Modifier.padding(top = 12.dp)); return@Hoja }
        val paises = t.paises.mapNotNull { Mundo.pais(ctx, it.clave) }
        val tierra = remember { Mundo.paises(ctx).filter { it.codigo != "AQ" }.sumOf { it.areaKm2.toLong() } }
        val pct = paises.sumOf { it.areaKm2.toLong() } * 100.0 / tierra
        val continentes = paises.map { it.continente }.filter { it < 6 }.toSet().size
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra("${t.paises.size}", "países", Modifier.weight(1f))
            Cifra("${t.ciudades.size}", "ciudades", Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra("$continentes de 6", "continentes", Modifier.weight(1f))
            Cifra(if (pct < 10) "%.1f%%".format(pct) else "%.0f%%".format(pct), "de la tierra firme", Modifier.weight(1f))
        }

        Seccion("Mapa para rascar", "Cada país donde estuviste queda descubierto. Ábrelo para acercarte y rascar a mano los que visitaste antes de usar la app.", numero = 1) {
            Surface(Modifier.fillMaxWidth().clickable { nav.ir("rascar") }, shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                MapaRascar(t.codigosPaises, Modifier.fillMaxWidth().aspectRatio(2.1f).padding(6.dp))
            }
        }

        Seccion("Dónde has pasado más tiempo", "Días en los que tu ubicación cayó ahí (más los que anotaste a mano) y las horas registradas.", numero = 2) {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Ficha("ciudades", vista == "ciudades") { vista = "ciudades" }
                Ficha("países", vista == "paises") { vista = "paises" }
                Spacer(Modifier.width(10.dp))
                PERIODOS_MUNDO.forEach { (id, n) -> Ficha(n, periodo == id) { periodo = id } }
            }
            Spacer(Modifier.height(10.dp))
            val l = r?.let { if (vista == "ciudades") it.ciudades else it.paises }
            when {
                l == null -> Ayuda("Calculando…")
                l.isEmpty() -> Vacio("Nada en este periodo. Activa la ubicación en segundo plano o añade ciudades a mano.")
                else -> Tarjeta {
                    val max = l.first().totalDias.coerceAtLeast(1)
                    val mostrar = if (todas) l.take(60) else l.take(10)
                    mostrar.forEachIndexed { i, e ->
                        if (i > 0) HorizontalDivider(Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        FilaEstancia(i + 1, e, max, vista == "ciudades")
                    }
                    if (l.size > 10) TextButton(onClick = { todas = !todas }) { Text(if (todas) "ver menos" else "ver ${min(l.size, 60) - 10} más") }
                }
            }
        }

        Seccion("Ciudades visitadas", "Las que salen de tus ubicaciones y las que añadas a mano, de la más reciente a la más antigua.", numero = 3) {
            BotonPrincipal("Añadir una ciudad", Modifier.fillMaxWidth()) { nav.ir("ciudad_nueva") }
            Spacer(Modifier.height(10.dp))
            val manuales = Repo.config.ciudades_visitadas
            val lista = t.ciudades.sortedByDescending { it.ultimo ?: -1 }
            var borrar by remember { mutableStateOf<CiudadVisitada?>(null) }
            if (lista.isEmpty()) Vacio("Todavía ninguna.")
            else Tarjeta {
                var abiertas by remember { mutableStateOf(false) }
                (if (abiertas) lista else lista.take(12)).forEachIndexed { i, e ->
                    if (i > 0) HorizontalDivider(Modifier.padding(vertical = 5.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${Mundo.bandera(e.pais)}  ${e.nombre}", style = MaterialTheme.typography.bodyLarge)
                            Ayuda(listOfNotNull(Mundo.pais(ctx, e.pais)?.nombre, dias(e.totalDias),
                                e.ultimo?.let { "última vez ${mesAnio(it)}" }, if (e.manual) "a mano" else null).joinToString(" · "))
                        }
                        val propia = manuales.firstOrNull { m -> Mundo.ciudadEn(ctx, m.lat, m.lon, m.pais).let { ci -> "c$ci" == e.clave } || "m${m.id}" == e.clave }
                        if (propia != null) TextButton(onClick = { borrar = propia }) { Text("quitar", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                }
                if (lista.size > 12) TextButton(onClick = { abiertas = !abiertas }) { Text(if (abiertas) "ver menos" else "ver las ${lista.size}") }
            }
            borrar?.let { b ->
                AlertDialog(onDismissRequest = { borrar = null }, title = { Text("¿Quitar ${b.nombre}?") },
                    text = { Text("Se borra lo que anotaste a mano. Si tus ubicaciones pasan por ahí, la ciudad seguirá apareciendo.") },
                    confirmButton = { TextButton(onClick = { Repo.cambiarConfig { c -> c.copy(ciudades_visitadas = c.ciudades_visitadas.filter { it.id != b.id }) }; borrar = null }) { Text("quitar") } },
                    dismissButton = { TextButton(onClick = { borrar = null }) { Text("cancelar") } })
            }
        }

        Seccion("Lugares de tus fotos", "Casi todas las fotos de la cámara guardan dónde y cuándo se tomaron. Con eso se llenan tus viajes de antes de usar la app. De cada foto se guarda solo el lugar y la hora; la imagen nunca se copia.", numero = 4, plegable = true) {
            FotosConLugar()
        }

        Seccion("Patrimonio Mundial", "Los sitios de la lista de la UNESCO. Se marcan solos cuando tu ubicación (o una foto) pasa cerca; también puedes marcarlos a mano.", numero = 5) {
            val vistos = t.sitios
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Cifra("${vistos.size}", "de ${"%,d".format(sitios.size)} sitios", Modifier.weight(1f))
                Cifra("${sitios.count { it.i in vistos && it.c == "c" }}", "culturales", Modifier.weight(1f))
                Cifra("${sitios.count { it.i in vistos && it.c != "c" }}", "naturales o mixtos", Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            val recientes = sitios.filter { it.i in vistos }.sortedByDescending { vistos[it.i] ?: 0 }.take(6)
            if (recientes.isNotEmpty()) Tarjeta {
                recientes.forEachIndexed { i, s ->
                    if (i > 0) HorizontalDivider(Modifier.padding(vertical = 5.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Text("${s.p.joinToString("") { Mundo.bandera(it) }}  ${s.n}", style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Ayuda("${s.categoria} · inscrito en ${s.a}" + (vistos[s.i]?.takeIf { it > 0 }?.let { " · ${mesAnio(it)}" } ?: ""))
                }
            }
            BotonSecundario("ver los ${"%,d".format(sitios.size)} sitios", Modifier.padding(top = 8.dp)) { nav.ir("unesco") }
        }
        Ayuda(Mundo.CREDITOS, Modifier.padding(top = 20.dp, bottom = 30.dp))
    }
}

/** Revisar las fotos del teléfono e importar el Takeout de Google Fotos. */
@Composable
private fun FotosConLugar() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val sentir = rememberSentir()
    var estado by remember { mutableStateOf<String?>(null) }
    var ocupado by remember { mutableStateOf(false) }
    var cuenta by remember { mutableIntStateOf(io.github.ruddy2003.vestigium.sensores.FotosLugar.conLugar(ctx)) }
    fun revisar() {
        if (ocupado) return
        ocupado = true
        scope.launch {
            val r = withContext(Dispatchers.IO) {
                io.github.ruddy2003.vestigium.sensores.FotosLugar.revisar(ctx) { i, n -> scope.launch { estado = "Revisando ${"%,d".format(i)} de ${"%,d".format(n)} fotos…" } }
            }
            estado = r.toString(); cuenta = io.github.ruddy2003.vestigium.sensores.FotosLugar.conLugar(ctx); ocupado = false
            if (r.nuevas > 0) sentir(Sentidos.Evento.GUARDAR)
        }
    }
    val permisos = androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()) { revisar() }
    val zips = androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isEmpty() || ocupado) return@rememberLauncherForActivityResult
        ocupado = true
        scope.launch {
            val r = withContext(Dispatchers.IO) { io.github.ruddy2003.vestigium.sensores.FotosLugar.importarTakeout(ctx, uris) { t -> scope.launch { estado = t } } }
            estado = if (r.revisadas == 0) "No encontré datos de fotos en ese archivo. ¿Es el Takeout de Google Fotos?" else "Takeout: ${"%,d".format(r.conLugar)} fotos con lugar, ${"%,d".format(r.nuevas)} nuevas en tu mapa."
            cuenta = io.github.ruddy2003.vestigium.sensores.FotosLugar.conLugar(ctx); ocupado = false
            if (r.nuevas > 0) sentir(Sentidos.Evento.GUARDAR)
        }
    }
    // Las fotos nuevas se revisan solas cada vez que abres esta pantalla (si ya diste permiso).
    LaunchedEffect(Unit) {
        if (io.github.ruddy2003.vestigium.sensores.Galeria.tienePermiso(ctx) && io.github.ruddy2003.vestigium.sensores.FotosLugar.ultimaRevision(ctx) > 0) {
            val r = withContext(Dispatchers.IO) { io.github.ruddy2003.vestigium.sensores.FotosLugar.revisar(ctx) }
            if (r.revisadas > 0) { estado = r.toString(); cuenta = io.github.ruddy2003.vestigium.sensores.FotosLugar.conLugar(ctx) }
        }
    }
    Tarjeta {
        Text(if (cuenta > 0) "${"%,d".format(cuenta)} fotos con lugar en tu mapa" else "Todavía no se han revisado tus fotos", style = MaterialTheme.typography.bodyLarge)
        io.github.ruddy2003.vestigium.sensores.FotosLugar.ultimaRevision(ctx).takeIf { it > 0 }?.let { Ayuda("Última revisión: " + Fechas.relativo(Fechas.diaDeMillis(it))) }
        estado?.let { Ayuda(it, Modifier.padding(top = 6.dp)) }
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonPrincipal(if (ocupado) "revisando…" else "Revisar mis fotos", Modifier.weight(1f), habilitado = !ocupado) {
                val faltan = (io.github.ruddy2003.vestigium.sensores.Galeria.permisos().toList() + io.github.ruddy2003.vestigium.sensores.FotosLugar.PERMISO)
                    .filter { ctx.checkSelfPermission(it) != android.content.pm.PackageManager.PERMISSION_GRANTED }
                if (faltan.isEmpty()) revisar() else permisos.launch(faltan.toTypedArray())
            }
        }
        BotonSecundario("importar Takeout de Google Fotos (.zip)", Modifier.padding(top = 8.dp), habilitado = !ocupado) { zips.launch(arrayOf("application/zip", "application/x-zip-compressed", "application/octet-stream")) }
        Ayuda("Para las fotos que solo están en la nube: en takeout.google.com elige solo «Google Fotos», archivos .zip de 2 GB, y aquí elige uno o varios. Se leen solo los datos de fecha y lugar (y, si vienen las fotos, la marca y modelo de la cámara); puedes borrar el .zip después.", Modifier.padding(top = 8.dp))
    }
    if (!ocupado && io.github.ruddy2003.vestigium.sensores.FotosLugar.enCatalogo() > 0) OrigenFotos(cuenta) { cuenta = io.github.ruddy2003.vestigium.sensores.FotosLugar.conLugar(ctx) }
}

/**
 * De dónde vienen tus fotos con lugar: cada aparato o carpeta, con cuántas fotos y de qué años. Marcas cuáles son tuyos
 * (las demás no cuentan), cuáles tenían el reloj mal (se usa la fecha del archivo) y revisas las fotos que te ponen en lugares raros.
 */
@Composable
private fun OrigenFotos(cuenta: Int, cambio: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val c = Repo.config
    var abierto by remember { mutableStateOf(false) }
    var version by remember { mutableIntStateOf(0) }
    var aplicando by remember { mutableStateOf(false) }
    val aparatos by produceState<List<io.github.ruddy2003.vestigium.sensores.FotosLugar.Aparato>?>(null, version, abierto) {
        if (abierto) value = withContext(Dispatchers.IO) { io.github.ruddy2003.vestigium.sensores.FotosLugar.aparatos() }
    }
    val raras by produceState<List<io.github.ruddy2003.vestigium.sensores.FotosLugar.Rara>?>(null, version, abierto) {
        if (abierto) value = withContext(Dispatchers.IO) { runCatching { io.github.ruddy2003.vestigium.sensores.FotosLugar.raras(ctx) }.getOrDefault(emptyList()) }
    }
    fun cambiar(f: (io.github.ruddy2003.vestigium.data.Config) -> io.github.ruddy2003.vestigium.data.Config) {
        Repo.cambiarConfig(f)
        aplicando = true
        scope.launch {
            withContext(Dispatchers.IO) { io.github.ruddy2003.vestigium.sensores.FotosLugar.aplicar(ctx) }
            aplicando = false; version++; cambio()
        }
    }
    Tarjeta(Modifier.padding(top = 10.dp)) {
        Row(Modifier.fillMaxWidth().clickable { abierto = !abierto }, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("De dónde vienen tus fotos", style = MaterialTheme.typography.titleMedium)
                Ayuda("Elige qué aparatos son tuyos, corrige relojes mal puestos y revisa las fotos que te ponen en lugares raros.")
            }
            Text(if (abierto) "▲" else "▼", color = MaterialTheme.colorScheme.primary)
        }
        if (!abierto) return@Tarjeta
        if (aplicando) Ayuda("Rehaciendo tu mapa…", Modifier.padding(top = 6.dp))
        val lista = aparatos
        if (lista == null) { Ayuda("Contando…", Modifier.padding(top = 6.dp)); return@Tarjeta }
        val mios = c.fotos_aparatos.toSet()
        androidx.compose.foundation.layout.Spacer(Modifier.height(10.dp)); Etiqueta("Aparatos y carpetas")
        Ayuda(if (mios.isEmpty()) "Ahora cuentan todos. Marca los tuyos (también tus teléfonos anteriores) y los demás dejarán de contar." else "Cuentan solo los marcados. Las fotos con fecha imposible (antes de 2004 o en el futuro) nunca cuentan, salvo que digas que el reloj de ese aparato estaba mal.")
        lista.forEach { a ->
            val nombre = a.nombre.ifEmpty { "importadas antes (sin saber el aparato)" }
            val marcado = mios.isEmpty() || a.nombre in mios
            Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Checkbox(checked = marcado, onCheckedChange = { v ->
                        val base = if (mios.isEmpty()) lista.map { it.nombre }.toSet() else mios
                        val nuevo = if (v) base + a.nombre else base - a.nombre
                        cambiar { it.copy(fotos_aparatos = if (nuevo.size == lista.size) emptyList() else nuevo.toList()) }
                    })
                    Column(Modifier.weight(1f)) {
                        Text(nombre, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Ayuda(listOfNotNull("${"%,d".format(a.fotos)} fotos",
                            if (a.desde > 0) { val d = Fechas.diaDeMillis(a.desde).take(4); val h = Fechas.diaDeMillis(a.hasta).take(4); if (d == h) d else "$d–$h" } else null,
                            if (a.imposibles > 0) "${a.imposibles} con fecha imposible" else null,
                            if (a.compartidas > 0) "${a.compartidas} compartidas" else null).joinToString(" · "))
                    }
                }
                if (a.imposibles > 0 || a.nombre in c.fotos_sin_fecha) Row(Modifier.padding(start = 48.dp), verticalAlignment = Alignment.CenterVertically) {
                    val malo = a.nombre in c.fotos_sin_fecha
                    androidx.compose.material3.Switch(checked = malo, onCheckedChange = { v -> cambiar { it.copy(fotos_sin_fecha = if (v) it.fotos_sin_fecha + a.nombre else it.fotos_sin_fecha - a.nombre) } })
                    Text("su reloj estaba mal: usar la fecha del archivo", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
        if (lista.any { it.compartidas > 0 }) Interruptor("Contar fotos compartidas por otros", c.fotos_compartidas,
            "De Google Fotos: álbumes compartidos y fotos de tu pareja. Normalmente no son tuyas, aunque a veces estuviste ahí.") { v -> cambiar { it.copy(fotos_compartidas = v) } }
        val rs = raras
        androidx.compose.foundation.layout.Spacer(Modifier.height(14.dp)); Etiqueta("Fotos raras")
        when {
            rs == null -> Ayuda("Buscando…")
            rs.isEmpty() -> Ayuda("Ninguna foto te pone en un lugar raro.")
            else -> {
                Ayuda("Te ponen en un país donde quizá no estuviste. No cuentan hasta que digas que sí estuviste.")
                rs.take(60).forEach { r ->
                    val decidido = c.fotos_revisadas[r.clave]
                    Column(Modifier.fillMaxWidth().padding(top = 10.dp)) {
                        Text("${Mundo.bandera(r.pais)}  ${Mundo.pais(ctx, r.pais)?.nombre ?: r.pais} · ${Fechas.larga(r.fecha)}", style = MaterialTheme.typography.bodyLarge)
                        Ayuda("${r.fotos} ${if (r.fotos == 1) "foto" else "fotos"} de ${r.aparatos.take(2).joinToString(", ") { it.ifEmpty { "aparato desconocido" } }} · ${r.motivo}")
                        Row(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Ficha("sí estuve", decidido == true) { cambiar { it.copy(fotos_revisadas = it.fotos_revisadas + (r.clave to true)) } }
                            Ficha("no cuenta", decidido != true) { cambiar { it.copy(fotos_revisadas = it.fotos_revisadas + (r.clave to false)) } }
                        }
                    }
                }
                if (rs.size > 60) Ayuda("…y ${rs.size - 60} grupos más.", Modifier.padding(top = 6.dp))
            }
        }
        Ayuda("En tu mapa cuentan ${"%,d".format(cuenta)} de ${"%,d".format(io.github.ruddy2003.vestigium.sensores.FotosLugar.enCatalogo())} fotos con lugar.", Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun FilaEstancia(pos: Int, e: Mundo.Estancia, max: Int, ciudad: Boolean) {
    val ctx = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(Estilo.numeral(pos), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(34.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${Mundo.bandera(e.pais)}  ${e.nombre}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(dias(e.totalDias), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
            Box(Modifier.fillMaxWidth().padding(vertical = 4.dp).height(6.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(3.dp))) {
                Box(Modifier.fillMaxWidth((e.totalDias.toFloat() / max).coerceIn(0.02f, 1f)).height(6.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp)))
            }
            Ayuda(listOfNotNull(
                if (ciudad) Mundo.pais(ctx, e.pais)?.nombre else null,
                if (e.horas > 0) "≈ ${horas(e.horas)} registradas" else null,
                e.primero?.let { p -> e.ultimo?.let { u -> if (mesAnio(p) == mesAnio(u)) mesAnio(p) else "${mesAnio(p)} – ${mesAnio(u)}" } },
            ).joinToString(" · "))
        }
    }
}

// =====================================================================
//  Mapa para rascar a pantalla completa
// =====================================================================

@Composable
fun PantallaRascar(nav: Nav) {
    val ctx = LocalContext.current
    val sentir = rememberSentir()
    val scope = rememberCoroutineScope()
    val r = rememberResumen("todo")
    var zoom by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var elegido by remember { mutableStateOf<Mundo.Pais?>(null) }
    var rascando by remember { mutableStateOf<String?>(null) }
    val avance = remember { Animatable(1f) }
    var tam by remember { mutableStateOf(Offset(1f, 1f)) }
    val dens = LocalDensity.current.density
    val visitados = r?.codigosPaises ?: emptySet()
    var area by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }
    var iniciado by remember { mutableStateOf(false) }
    /** Acerca el mapa a un país (lo deja al centro). */
    fun centrar(p: Mundo.Pais, z: Float) {
        val f = GeoMapa.formas(ctx).firstOrNull { it.pais.codigo == p.codigo } ?: return
        val w = area.width.toFloat(); val h = area.height.toFloat()
        if (w <= 0f || h <= 0f) return
        val L = GeoMapa.limites
        val s = min(w / L.width(), h / L.height())
        val bx = (w - L.width() * s) / 2 - L.left * s; val by = (h - L.height() * s) / 2 - L.top * s
        val base = Offset(bx + f.etiqueta.x * s, by + f.etiqueta.y * s)
        zoom = z
        val lim = Offset(w * (z - 1) / 2 + w * 0.2f, h * (z - 1) / 2 + h * 0.2f)
        pan = Offset((-z * (base.x - w / 2)).coerceIn(-lim.x, lim.x), (-z * (base.y - h / 2)).coerceIn(-lim.y, lim.y))
    }
    // Al abrir: acercado a tu región (el país donde más días has pasado), para que el mapa llene la pantalla.
    LaunchedEffect(r, area) {
        if (!iniciado && r != null && area.width > 0) {
            iniciado = true
            r.paises.firstOrNull()?.let { e -> Mundo.pais(ctx, e.clave)?.let { centrar(it, 2.2f) } }
        }
    }

    fun rascar(p: Mundo.Pais) {
        Repo.cambiarConfig { c -> c.copy(paises_rascados = (c.paises_rascados + p.codigo).distinct(), paises_tapados = c.paises_tapados - p.codigo) }
        rascando = p.codigo
        Sentidos.rascar(ctx)
        scope.launch {
            avance.snapTo(0f)
            avance.animateTo(1f, tween(if (Repo.config.animaciones) 1100 else 0, easing = LinearEasing))
            rascando = null
        }
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = nav.atras) { Text(Estilo.atras, color = MaterialTheme.colorScheme.primary) }
            Column(Modifier.weight(1f)) {
                Text(Estilo.pantalla("Mapa para rascar", io.github.ruddy2003.vestigium.ui.Romano.latin("Mapa para rascar")).first, style = MaterialTheme.typography.titleLarge)
                Ayuda("${visitados.size} países · toca un país para verlo; pellizca para acercarte")
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth().onSizeChanged { area = it }
            .pointerInput(Unit) {
                detectTransformGestures { centroide, desplazamiento, factor, _ ->
                    val nz = (zoom * factor).coerceIn(1f, 16f)
                    val w = size.width.toFloat(); val h = size.height.toFloat()
                    // Acerca alrededor de los dedos: el punto bajo ellos se queda donde está.
                    val centro = Offset(w / 2, h / 2)
                    val p = (centroide - centro - pan) / zoom
                    pan = centroide - centro - p * nz + desplazamiento
                    zoom = nz
                    val lim = Offset(w * (zoom - 1) / 2 + w * 0.2f, h * (zoom - 1) / 2 + h * 0.2f)
                    pan = Offset(pan.x.coerceIn(-lim.x, lim.x), pan.y.coerceIn(-lim.y, lim.y))
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { zoom = 1f; pan = Offset.Zero },
                    onTap = { o ->
                        elegido = paisTocado(ctx, o, size.width.toFloat(), size.height.toFloat(), zoom, pan, dens)
                        if (elegido != null) sentir(Sentidos.Evento.ELEGIR)
                    },
                )
            }) {
            // El mapa se dibuja con el centro como origen del acercamiento.
            MapaRascar(visitados, Modifier.fillMaxSize(), rascando, avance.value, elegido?.codigo, zoom, panAMapa(pan, zoom))
        }
        val p = elegido
        Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
            Column(Modifier.padding(16.dp)) {
                if (p == null) {
                    Ayuda("Toca un país para verlo y rascarlo. Los que salen de tus ubicaciones necesitan al menos media hora ahí, para que no cuenten los que solo sobrevolaste. Dos toques: todo el mundo.")
                    val mios = r?.paises.orEmpty().mapNotNull { Mundo.pais(ctx, it.clave) }
                    if (mios.isNotEmpty()) Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        mios.forEach { m -> Ficha("${m.bandera} ${m.nombre}", false) { elegido = m; centrar(m, max(zoom, 3f)) } }
                    }
                }
                else {
                    val e = r?.paises?.firstOrNull { it.clave == p.codigo }
                    val cuantas = r?.ciudades?.count { it.pais == p.codigo } ?: 0
                    Text("${p.bandera}  ${p.nombre}", style = MaterialTheme.typography.titleLarge)
                    Ayuda(listOfNotNull(Mundo.CONTINENTES.getOrNull(p.continente), "%,d km²".format(p.areaKm2),
                        e?.let { dias(it.totalDias) }, e?.horas?.takeIf { it > 0 }?.let { "≈ ${horas(it)}" },
                        if (cuantas > 0) "$cuantas ciudad${if (cuantas == 1) "" else "es"}" else null).joinToString(" · "))
                    Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val manual = p.codigo in Repo.config.paises_rascados
                        val auto = e != null && !manual
                        when {
                            p.codigo !in visitados -> BotonPrincipal("Rascar: estuve aquí") { rascar(p) }
                            manual -> BotonSecundario("tapar otra vez") { Repo.cambiarConfig { c -> c.copy(paises_rascados = c.paises_rascados - p.codigo) }; sentir(Sentidos.Evento.TOQUE) }
                            auto -> BotonSecundario("no cuenta (solo pasé)") { Repo.cambiarConfig { c -> c.copy(paises_tapados = (c.paises_tapados + p.codigo).distinct()) }; sentir(Sentidos.Evento.TOQUE) }
                        }
                    }
                }
            }
        }
    }
}

/** El desplazamiento de los gestos se mide desde el centro; el dibujo lo necesita igual. */
private fun panAMapa(pan: Offset, zoom: Float) = pan

// =====================================================================
//  Añadir una ciudad
// =====================================================================

@Composable
fun PantallaCiudadNueva(nav: Nav) {
    val ctx = LocalContext.current
    val sentir = rememberSentir()
    var texto by remember { mutableStateOf("") }
    var elegida by remember { mutableIntStateOf(-1) }
    var desde by remember { mutableStateOf<String?>(null) }
    var hasta by remember { mutableStateOf<String?>(null) }
    var dias by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    var resultados by remember { mutableStateOf<List<Int>>(emptyList()) }
    LaunchedEffect(texto) {
        delay(220)
        resultados = withContext(Dispatchers.IO) { runCatching { Mundo.buscarCiudad(ctx, texto) }.getOrDefault(emptyList()) }
    }
    fun fecha(actual: String?, poner: (String) -> Unit) {
        val p = actual?.let { Fechas.parse(it) } ?: LocalDate.now()
        DatePickerDialog(ctx, { _, y, m, d -> poner(Fechas.iso(LocalDate.of(y, m + 1, d))) }, p.year, p.monthValue - 1, p.dayOfMonth).show()
    }
    Hoja("Añadir ciudad", nav.atras, "Busca la ciudad por su nombre. Si sabes cuándo fuiste, ponlo: así cuenta en el tiempo de cada ciudad y país.") {
        val c = remember { Mundo.ciudades(ctx) }
        if (elegida < 0) {
            OutlinedTextField(texto, { texto = it }, Modifier.fillMaxWidth().padding(top = 12.dp), singleLine = true, placeholder = { Text("nombre de la ciudad") })
            Spacer(Modifier.height(8.dp))
            if (texto.length >= 2 && resultados.isEmpty()) Ayuda("Nada con ese nombre. Prueba sin acentos o con el nombre local.")
            resultados.forEach { i ->
                Row(Modifier.fillMaxWidth().clickable { elegida = i; sentir(Sentidos.Evento.ELEGIR) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("${Mundo.bandera(c.pais(i))}  ${c.nombre(i)}", style = MaterialTheme.typography.bodyLarge)
                        val raiz = c.raiz(i)
                        Ayuda(listOfNotNull(Mundo.pais(ctx, c.pais(i))?.nombre, poblacion(c.pob[i]), if (raiz != i) "parte de ${c.nombre(raiz)}" else null).joinToString(" · "))
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            }
        } else {
            val i = elegida
            Tarjeta {
                Text("${Mundo.bandera(c.pais(i))}  ${c.nombre(i)}", style = MaterialTheme.typography.titleLarge)
                Ayuda(listOfNotNull(Mundo.pais(ctx, c.pais(i))?.nombre, poblacion(c.pob[i])).joinToString(" · "))
                TextButton(onClick = { elegida = -1 }) { Text("elegir otra") }
            }
            Seccion("¿Cuándo?", "Opcional. Con fechas cuenta los días exactos; si no las recuerdas, pon solo cuántos días fueron.") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BotonSecundario(desde?.let { "desde ${Fechas.corto(it)}" } ?: "desde…") { fecha(desde) { desde = it; if (hasta == null || hasta!! < it) hasta = it } }
                    BotonSecundario(hasta?.let { "hasta ${Fechas.corto(it)}" } ?: "hasta…") { fecha(hasta ?: desde) { hasta = it } }
                }
                if (desde == null) OutlinedTextField(dias, { dias = it.filter { ch -> ch.isDigit() }.take(4) }, Modifier.padding(top = 8.dp).width(180.dp), singleLine = true,
                    label = { Text("días (aprox.)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(nota, { nota = it }, Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("nota (opcional)") })
            }
            BotonPrincipal("Guardar", Modifier.fillMaxWidth().padding(top = 18.dp)) {
                val nueva = CiudadVisitada(UUID.randomUUID().toString().take(8), c.nombre(i), c.pais(i), c.lat[i] / 1e5, c.lon[i] / 1e5,
                    desde, hasta, if (desde == null) dias.toIntOrNull() else null, nota.trim())
                Repo.cambiarConfig { cf -> cf.copy(ciudades_visitadas = cf.ciudades_visitadas + nueva, paises_tapados = cf.paises_tapados - nueva.pais) }
                sentir(Sentidos.Evento.GUARDAR)
                nav.atras()
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

private fun poblacion(p: Int): String? = when {
    p <= 0 -> null
    p >= 1_000_000 -> "%.1f millones de hab.".format(p / 1e6)
    p >= 10_000 -> "%,d mil hab.".format(p / 1000)
    else -> "%,d hab.".format(p)
}

// =====================================================================
//  Patrimonio Mundial
// =====================================================================

@Composable
fun PantallaUnesco(nav: Nav) {
    val ctx = LocalContext.current
    val sentir = rememberSentir()
    val sitios = remember { Mundo.sitios(ctx) }
    val r = rememberResumen("todo")
    var texto by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("todos") }
    var abiertos by remember { mutableStateOf(setOf<String>()) }
    var detalle by remember { mutableStateOf<Mundo.Sitio?>(null) }
    val vistos = r?.sitios ?: emptyMap()
    val q = Mundo.normalizar(texto)

    Hoja("Patrimonio Mundial", nav.atras, "Los ${"%,d".format(sitios.size)} sitios de la lista de la UNESCO, por país. ✓ quiere decir que estuviste ahí.") {
        OutlinedTextField(texto, { texto = it }, Modifier.fillMaxWidth().padding(top = 12.dp), singleLine = true, placeholder = { Text("buscar un sitio o un país") })
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("todos" to "todos", "visitados" to "visitados", "pendientes" to "pendientes", "c" to "culturales", "n" to "naturales", "m" to "mixtos", "peligro" to "en peligro").forEach { (id, n) -> Ficha(n, filtro == id) { filtro = id } }
        }
        val lista = sitios.filter { s ->
            (q.isEmpty() || Mundo.normalizar(s.n).contains(q) || s.p.any { Mundo.normalizar(Mundo.pais(ctx, it)?.nombre ?: it).contains(q) }) && when (filtro) {
                "visitados" -> s.i in vistos; "pendientes" -> s.i !in vistos; "peligro" -> s.enPeligro; "c", "n", "m" -> s.c == filtro; else -> true
            }
        }
        // Agrupados por país (un sitio compartido aparece en cada uno de sus países).
        val grupos = lista.flatMap { s -> s.p.ifEmpty { listOf("ZZ") }.map { it to s } }.groupBy({ it.first }, { it.second })
            .toList().sortedWith(compareByDescending<Pair<String, List<Mundo.Sitio>>> { g -> g.second.count { it.i in vistos } }.thenBy { Mundo.pais(ctx, it.first)?.nombre ?: it.first })
        if (grupos.isEmpty()) Vacio("Nada con ese filtro.")
        grupos.forEach { (cod, l) ->
            val nombre = Mundo.pais(ctx, cod)?.nombre ?: "Varios países"
            val n = l.count { it.i in vistos }
            val abierto = cod in abiertos || (q.isNotEmpty() && grupos.size <= 6) || (filtro == "visitados")
            Row(Modifier.fillMaxWidth().clickable { abiertos = if (cod in abiertos) abiertos - cod else abiertos + cod }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("${Mundo.bandera(cod)}  $nombre", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text(if (n > 0) "$n de ${l.size}" else "${l.size}", style = MaterialTheme.typography.labelLarge, color = if (n > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                Text(if (abierto) "  ▾" else "  ▸", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (abierto) Tarjeta {
                l.sortedBy { it.n }.forEachIndexed { i, s ->
                    if (i > 0) HorizontalDivider(Modifier.padding(vertical = 5.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Row(Modifier.fillMaxWidth().clickable { detalle = s }, verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(26.dp).border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                            .background(if (s.i in vistos) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                            if (s.i in vistos) Text("✓", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelLarge)
                        }
                        Column(Modifier.weight(1f).padding(start = 10.dp)) {
                            Text(s.n, style = MaterialTheme.typography.bodyMedium)
                            Ayuda(listOfNotNull(s.categoria, "${s.a}", if (s.enPeligro) "en peligro" else null, if (s.p.size > 1) "compartido por ${s.p.size} países" else null).joinToString(" · "))
                        }
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
        Ayuda("Datos: Centro del Patrimonio Mundial de la UNESCO (lista vigente a agosto de 2026). Los nombres van como los publica la UNESCO en inglés.", Modifier.padding(top = 16.dp, bottom = 30.dp))
    }

    detalle?.let { s ->
        val visto = s.i in vistos
        val manual = Repo.config.unesco_marcados.any { it.id == s.i }
        AlertDialog(onDismissRequest = { detalle = null },
            title = { Text(s.n) },
            text = {
                Column {
                    Text(s.p.joinToString(", ") { "${Mundo.bandera(it)} ${Mundo.pais(ctx, it)?.nombre ?: it}" }, style = MaterialTheme.typography.bodyMedium)
                    Ayuda("${s.categoria} · inscrito en ${s.a}" + if (s.enPeligro) " · en la lista del patrimonio en peligro" else "")
                    Ayuda(when {
                        visto && manual -> "Lo marcaste a mano."
                        visto -> "Tu ubicación pasó cerca" + (vistos[s.i]?.takeIf { it > 0 }?.let { " en ${mesAnio(it)}" } ?: "") + "."
                        else -> "Todavía no."
                    }, Modifier.padding(top = 8.dp))
                    TextButton(onClick = { runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(s.url))) } }) { Text("ver en whc.unesco.org") }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    Repo.cambiarConfig { c ->
                        when {
                            !visto -> c.copy(unesco_marcados = c.unesco_marcados + SitioMarcado(s.i, Fechas.diaLogico()), unesco_descartados = c.unesco_descartados - s.i)
                            manual -> c.copy(unesco_marcados = c.unesco_marcados.filter { it.id != s.i })
                            else -> c.copy(unesco_descartados = (c.unesco_descartados + s.i).distinct())
                        }
                    }
                    sentir(if (!visto) Sentidos.Evento.LOGRO else Sentidos.Evento.TOQUE)
                    detalle = null
                }) { Text(when { !visto -> "estuve aquí"; manual -> "quitar"; else -> "no cuenta" }) }
            },
            dismissButton = { TextButton(onClick = { detalle = null }) { Text("cerrar") } })
    }
}
