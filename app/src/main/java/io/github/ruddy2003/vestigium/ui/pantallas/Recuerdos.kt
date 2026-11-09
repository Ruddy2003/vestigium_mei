package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.biblioteca.EstadoRecuerdo
import io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.TextoMarcado
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.esOscuro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Lo que se sabe de un día pasado: dónde estuviste, qué sonaba, una línea del diario, el clima y tu ánimo. */
class Recuerdo(val fecha: String, val anios: Int, val lugares: List<String>, val cancion: String?, val linea: String?, val clima: String?, val animo: Int?, val foto: String?) {
    val vacio: Boolean get() = lugares.isEmpty() && cancion == null && linea == null && clima == null && animo == null && foto == null
}

object Recuerdos {
    /** Signo y palabra del clima a partir del código del servicio meteorológico (WMO). */
    fun clima(codigo: Int?, tMax: Double?): String? {
        val t = tMax?.let { " ${it.toInt()}°" } ?: ""
        val c = when (codigo) {
            null -> null
            0 -> "☀ despejado"; 1, 2 -> "⛅ algo nublado"; 3 -> "☁ nublado"; 45, 48 -> "≋ niebla"
            in 51..57 -> "☂ llovizna"; in 61..67, in 80..82 -> "☂ lluvia"; in 71..77, 85, 86 -> "❄ nieve"; in 95..99 -> "⚡ tormenta"
            else -> null
        }
        return when { c != null -> c + t; t.isNotEmpty() -> "máx$t"; else -> null }
    }

    /** La primera frase del diario (o el comienzo), sin cortar palabras. */
    fun primeraLinea(nota: String): String? {
        val t = nota.trim().replace(Regex("\\s+"), " ")
        if (t.isEmpty()) return null
        val fin = Regex("[.!?…](\\s|$)").find(t)?.range?.first?.plus(1)
        val corta = if (fin != null && fin in 20..170) t.take(fin) else t.take(150).substringBeforeLast(' ') + if (t.length > 150) "…" else ""
        return corta
    }

    fun de(fecha: String, anios: Int): Recuerdo {
        val d = Repo.dias[fecha]
        val ini = Fechas.inicioDiaLogico(fecha); val fin = Fechas.finDiaLogico(fecha)
        val lugares = Repo.visitas.filter { val a = Fechas.millis(it.entrada); a in ini until fin }.mapNotNull { v -> Repo.lugares.firstOrNull { it.id == v.lugar }?.nombre }.distinct()
        val cancion = runCatching { Repo.escuchas(fecha, fecha).groupingBy { it.titulo + if (it.artista.isNotBlank()) " · ${it.artista}" else "" }.eachCount().maxByOrNull { it.value }?.key }.getOrNull()
        val e = Repo.entorno[fecha]
        return Recuerdo(fecha, anios, lugares, cancion, d?.nota?.let { primeraLinea(it) }, clima(e?.codigo, e?.t_max), d?.valencia, d?.foto)
    }
}

/**
 * «Hace un año» en Día: lo de esta misma fecha en años anteriores (hasta cinco), cada año en su tarjeta.
 * Toca una para abrir ese día.
 */
@Composable
fun HaceUnAnio(fecha: String, nav: Nav) {
    val anios by produceState(emptyList<Recuerdo>(), fecha, Repo.dias.size) {
        value = withContext(Dispatchers.IO) {
            (1..5).mapNotNull { k -> val f = Fechas.iso(Fechas.parse(fecha).minusYears(k.toLong())); runCatching { Recuerdos.de(f, k) }.getOrNull()?.takeIf { !it.vacio } }
        }
    }
    if (anios.isEmpty()) return
    Seccion("Este día hace un año", if (anios.size > 1) "Y más atrás: lo que pasaba en esta misma fecha." else null, plegable = true) {
        anios.forEach { r -> TarjetaRecuerdo(r, Modifier.padding(bottom = 8.dp)) { Seleccion.abrir(nav, r.fecha) } }
    }
}

/**
 * La tarjeta de un recuerdo, dibujada por el tema ([io.github.ruddy2003.vestigium.ui.biblioteca.DisenoBiblioteca.recuerdo]): en la
 * franja de arriba, hace cuántos años, la fecha, el clima y el ánimo; debajo, la foto, los lugares, la canción y la primera línea del diario.
 */
@Composable
fun TarjetaRecuerdo(r: Recuerdo, modifier: Modifier = Modifier, abrir: () -> Unit) {
    val d = disenoBiblioteca(Estilo.tema)
    val oscuro = esOscuro()
    val medidor = rememberTextMeasurer(cacheSize = 16)
    val tinta = d.tintaRecuerdo(oscuro)
    val cuando = if (r.anios == 1) "Hace un año" else "Hace ${r.anios} años"
    val e = EstadoRecuerdo(r.anios, Fechas.titulo(r.fecha).lowercase(), r.clima.orEmpty(), r.animo?.let { Tonos.valencia(it.toDouble(), oscuro) }, r.fecha.hashCode())
    Box(modifier.fillMaxWidth().drawBehind { with(d) { recuerdo(e, medidor, oscuro) } }
        .semantics(mergeDescendants = true) { contentDescription = cuando + ", " + e.fecha + (r.animo?.let { ", ánimo $it de 7" } ?: "") }
        .clickable(onClick = abrir)) {
        Row(Modifier.padding(start = d.margenRecuerdo, end = d.margenRecuerdo, bottom = d.margenRecuerdo, top = d.altoRotuloRecuerdo).heightIn(min = 8.dp),
            verticalAlignment = Alignment.Top) {
            if (r.foto != null) Box(Modifier.size(64.dp).clip(RoundedCornerShape(6.dp))) {
                MiniaturaDe(Repo.dias[r.fecha], 300, Modifier.size(64.dp))
            }
            Column(Modifier.weight(1f).padding(start = if (r.foto != null) 12.dp else 0.dp)) {
                if (r.lugares.isNotEmpty()) Text("⌂ " + r.lugares.take(3).joinToString(", "), style = MaterialTheme.typography.bodySmall, color = tinta)
                r.cancion?.let { Text("♫ $it", style = MaterialTheme.typography.bodySmall, color = tinta, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp)) }
                r.linea?.let { TextoMarcado("«$it»", style = MaterialTheme.typography.bodyMedium, color = tinta, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 6.dp)) }
            }
        }
    }
}
