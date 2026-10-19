package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.TusNumeros
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Guias
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/** Los colores de las franjas: verdes, ámbar y rojos suaves, mezclados con el tema para que no se vean fuera de lugar. */
@Composable
internal fun colorEstado(e: Guias.Estado): Color {
    val cs = MaterialTheme.colorScheme
    val base = when (e) {
        Guias.Estado.BIEN -> Color(0xFF4E8F5A); Guias.Estado.CERCA -> Color(0xFFC99A2E); Guias.Estado.LEJOS -> Color(0xFFB5483A)
        Guias.Estado.INFO -> cs.tertiary; Guias.Estado.SIN_DATOS -> cs.outline
    }
    return lerp(base, cs.primary, 0.12f)
}

/**
 * Tu salud frente a las recomendaciones oficiales: pasos, actividad, sueño, azúcar, sal, fibra, alcohol, cafeína, corazón,
 * bienestar y aire; y tus vitaminas y minerales frente a la ingesta recomendada. Primero lo que vale la pena atender.
 */
@Composable
fun PantallaSalud(nav: Nav) {
    val ctx = LocalContext.current
    val c = Repo.config
    fun calcular() = TusNumeros.calcular(ctx).let { t -> Guias.evaluar(t) to Guias.micronutrientes() }
    val sexo = io.github.ruddy2003.vestigium.data.Perfiles.sexo()
    val datos by produceState(if (TusNumeros.sincrono) calcular() else null, sexo, c.perfil.estatura_cm) {
        if (!TusNumeros.sincrono) value = withContext(Dispatchers.Default) { calcular() }
    }
    Hoja("Salud", nav.atras, "Tus últimos 30 días frente a lo que recomiendan la OMS y otros organismos de salud.") {
        Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Referencias para", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            listOf("hombre" to "hombre", "mujer" to "mujer").forEach { (id, n) ->
                Ficha(n, sexo == id) {
                    val nuevo = if (sexo == id) "" else id
                    Repo.cambiarConfig { it.copy(salud_sexo = nuevo, perfil = it.perfil.copy(sexo = nuevo)) }
                }
            }
        }
        Ayuda("Algunas metas cambian (agua, hierro, vitaminas). Sin elegir, se usa el promedio de las dos.")
        val d = datos
        if (d == null) { Vacio("Calculando…"); return@Hoja }
        val (items, micros) = d
        val grupos = listOf(
            Guias.Estado.LEJOS to "Lo que vale la pena atender",
            Guias.Estado.CERCA to "Cerca de la meta",
            Guias.Estado.BIEN to "Vas bien",
            Guias.Estado.INFO to "Para tenerlo a la vista",
        )
        // Resumen de un vistazo.
        Tarjeta(Modifier.padding(top = 12.dp)) {
            val cuenta = items.groupingBy { it.estado }.eachCount()
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                listOf(Guias.Estado.BIEN to "bien", Guias.Estado.CERCA to "cerca", Guias.Estado.LEJOS to "atender", Guias.Estado.SIN_DATOS to "sin datos").forEach { (e, n) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${cuenta[e] ?: 0}", style = MaterialTheme.typography.headlineSmall, color = colorEstado(e))
                        Text(n, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Ayuda("No es consejo médico: son metas generales para adultos sanos. Si algo te preocupa, háblalo con un profesional de salud.", Modifier.padding(top = 8.dp))
        }
        grupos.forEachIndexed { i, (estado, titulo) ->
            val lista = items.filter { it.estado == estado }
            if (lista.isNotEmpty()) Seccion(titulo, numero = i + 1) {
                lista.forEach { TarjetaGuia(it) }
            }
        }
        if (micros.isNotEmpty()) Seccion("Vitaminas y minerales", "Promedio de tus últimos 30 días con registro en MacroFactor, frente a la ingesta recomendada de adultos de 19 a 50 años.", numero = grupos.size + 1) {
            Tarjeta {
                micros.sortedBy { it.pct ?: 0.0 }.forEach { m -> FilaMicro(m) }
                Text(Guias.FUENTE_MICROS.nombre, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline,
                    modifier = Modifier.padding(top = 10.dp).clickable { runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Guias.FUENTE_MICROS.url))) } })
                Ayuda("Lo que pasa del 100% no suele ser mejor. Las vitaminas de comida rara vez llegan al límite seguro; los suplementos sí pueden.")
            }
        }
        val sin = items.filter { it.estado == Guias.Estado.SIN_DATOS }
        if (sin.isNotEmpty()) Seccion("Sin datos todavía", numero = grupos.size + 2) {
            Tarjeta {
                sin.forEachIndexed { i, it ->
                    if (i > 0) HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text(it.titulo, style = MaterialTheme.typography.bodyLarge)
                    Ayuda("Meta: ${it.meta}." + if (it.como.isNotBlank()) " ${it.como}" else "")
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TarjetaGuia(it: Guias.Item) {
    val ctx = LocalContext.current
    var abierta by remember { mutableStateOf(false) }
    val punto = colorEstado(it.estado)
    Tarjeta(Modifier.padding(bottom = 10.dp)) {
        Column(Modifier.fillMaxWidth().clickable { abierta = !abierta }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(Modifier.padding(end = 8.dp).width(10.dp).height(10.dp)) { drawCircle(punto) }
                Text(it.titulo, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text(it.grupo.lowercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(it.texto, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
            if (it.valor != null && it.zonas.isNotEmpty()) Regla(it, Modifier.fillMaxWidth().padding(top = 10.dp).height(34.dp))
            Ayuda("Meta: ${it.meta}." + if (it.dias > 0) " Con ${it.dias} días de datos" + (if (it.hasta.isNotEmpty() && Fechas.diasEntre(it.hasta, Fechas.diaLogico()) > 10) ", hasta el ${Fechas.larga(it.hasta)}." else ".") else "",
                Modifier.padding(top = 6.dp))
            if (abierta) {
                if (it.detalle.isNotBlank()) Text(it.detalle, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp))
                it.fuente?.let { f ->
                    Text(f.nombre, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline,
                        modifier = Modifier.padding(top = 6.dp).clickable { runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(f.url))) } })
                }
            } else if (it.fuente != null) Text("de dónde sale ▾", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

/** La regla: franjas de color de la escala, la meta marcada y una aguja donde estás tú. */
@Composable
private fun Regla(it: Guias.Item, modifier: Modifier) {
    val colores = it.zonas.map { z -> colorEstado(z.estado) }
    val tinta = MaterialTheme.colorScheme.onBackground
    val fondo = MaterialTheme.colorScheme.background
    Canvas(modifier) {
        val alto = size.height * 0.36f; val y0 = size.height * 0.5f - alto / 2
        val span = (it.max - it.min).coerceAtLeast(1e-9)
        fun x(v: Double) = (((v - it.min) / span).coerceIn(0.0, 1.0) * size.width).toFloat()
        it.zonas.forEachIndexed { i, z ->
            val a = x(maxOf(z.desde, it.min)); val b = x(minOf(z.hasta, it.max))
            if (b > a) drawRoundRect(colores[i].copy(alpha = 0.55f), Offset(a, y0), Size(b - a, alto), CornerRadius(alto / 2))
        }
        val v = it.valor ?: return@Canvas
        val px = x(v)
        val p = Path().apply { moveTo(px, y0 - 2f); lineTo(px - 7.dp.toPx(), y0 - 11.dp.toPx()); lineTo(px + 7.dp.toPx(), y0 - 11.dp.toPx()); close() }
        drawPath(p, tinta)
        drawLine(tinta, Offset(px, y0 - 2f), Offset(px, y0 + alto + 3.dp.toPx()), 2.5.dp.toPx())
        drawCircle(fondo, 3.dp.toPx(), Offset(px, y0 + alto / 2))
    }
}

@Composable
private fun FilaMicro(m: Guias.Micro) {
    val pct = m.pct ?: 0.0
    val estado = when { pct < 66 -> Guias.Estado.LEJOS; pct < 100 -> Guias.Estado.CERCA; m.limite != null && (m.tuyo ?: 0.0) > m.limite -> Guias.Estado.LEJOS; else -> Guias.Estado.BIEN }
    val col = colorEstado(estado)
    val pista = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    Column(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(m.nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Text("${fmt(m.tuyo ?: 0.0)} de ${fmt(m.meta)} ${m.unidad} · ${pct.roundToInt()}%", style = MaterialTheme.typography.labelMedium, color = col)
        }
        Canvas(Modifier.fillMaxWidth().padding(top = 3.dp).height(7.dp)) {
            drawRoundRect(pista, Offset.Zero, size, CornerRadius(size.height / 2))
            val f = (pct / 150).coerceIn(0.0, 1.0).toFloat()
            drawRoundRect(col, Offset.Zero, Size(size.width * f, size.height), CornerRadius(size.height / 2))
            val meta = size.width * (100f / 150f)
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(meta, -2f), Offset(meta, size.height + 2f), 1.5f)
        }
    }
}

private fun fmt(x: Double): String = when { x >= 100 -> "%,d".format(x.roundToInt()); x >= 10 -> "%.0f".format(x); else -> "%.1f".format(x) }
