// El modo `vida` del arnés: los muebles de la pestaña Vida de un tema (ui/vida/Vida<Tema>.kt, por `disenoVida`) y el
// tablero de notas de la pestaña Diario (`TableroDeNotas`, ui/vida/MuebleNotas.kt), con datos de ejemplo como los de la
// captura `temaVida` de Capturas.kt. Sirve para cualquier tema.
//
//   dibujar.sh vida <tema> <claro|oscuro> <salida.png>
//
// Ver LEEME.md, «El modo vida».
package arnes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Nota
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.luzCalida
import io.github.ruddy2003.vestigium.ui.luzEnMueble
import io.github.ruddy2003.vestigium.ui.luzEnPared
import io.github.ruddy2003.vestigium.ui.vida.EnNicho
import io.github.ruddy2003.vestigium.ui.vida.EstadoIcono
import io.github.ruddy2003.vestigium.ui.vida.EstadoMueble
import io.github.ruddy2003.vestigium.ui.vida.MuebleVida
import io.github.ruddy2003.vestigium.ui.vida.TableroDeNotas
import io.github.ruddy2003.vestigium.ui.vida.disenoVida
import kotlin.math.cos
import kotlin.math.sin

/**
 * La pestaña Vida de un tema, en el orden de `PantallaVida` (ui/pantallas/Vida.kt) pero solo con lo que dibuja `DisenoVida`:
 * Social (230 dp, con lámpara de noche), Mapas (220 dp), Otras vidas (190 dp, con lámpara), los tres íconos de «Tus mapas»
 * (cada uno en su lienzo de 70 dp de alto) y, aparte, el tablero de notas de la pestaña Diario (280 dp) con las cuatro
 * notas de ejemplo de `Capturas.notasYMetas`. Lo que va en los nichos (el grafo, el mapa para rascar, las medallas) son
 * figuras de relleno con los colores del esquema: el arnés no compila esas pantallas, solo enseña dónde caen y cómo se leen.
 */
@Composable
internal fun Vida() {
    val cs = MaterialTheme.colorScheme
    val d = disenoVida(Estilo.tema)
    val oscuro = esOscuro()
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Social (RecuadroSocial, ui/pantallas/Social.kt): semilla 5, con lámpara.
        RecuadroArnes("Social", "6 personas · 2 por clasificar · red y cielo", 230.dp, luz = true) {
            Canvas(Modifier.fillMaxSize()) { with(d) { muebleSocial(EstadoMueble(cs.surface, cs.primary, cs.secondary, cs.onSurface, 5), oscuro) } }
            EnNicho(d.area(MuebleVida.SOCIAL)) { GrafoDeRelleno() }
        }
        // Mapas (RecuadroMapas, ui/pantallas/Galerias.kt): semilla 9, sin lámpara.
        RecuadroArnes("Mapas", "4 países · 9 ciudades · 3 sitios del Patrimonio Mundial", 220.dp, luz = false) {
            Canvas(Modifier.fillMaxSize()) { with(d) { muebleMapas(EstadoMueble(cs.surface, cs.primary, cs.secondary, cs.onSurface, 9), oscuro) } }
            EnNicho(d.area(MuebleVida.MAPAS)) { MapaDeRelleno() }
        }
        // Otras vidas (RecuadroOtrasVidas, ui/pantallas/OtrasVidas.kt): semilla 7, con lámpara; cinco medallas en fila.
        RecuadroArnes("Otras vidas", "Un día en otra época, junto al tuyo", 190.dp, luz = true) {
            Canvas(Modifier.fillMaxSize()) { with(d) { muebleOtrasVidas(EstadoMueble(cs.surface, cs.primary, cs.secondary, cs.onSurface, 7), oscuro) } }
            EnNicho(d.area(MuebleVida.OTRAS_VIDAS)) { MedallasDeRelleno() }
        }
        // Los íconos de «Tus mapas» (TarjetaMapa, ui/pantallas/Galerias.kt), sin el marco del mapa (marcoDelMapa).
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("unesco" to "Patrimonio", "rascar" to "Rascar", "lugares" to "Lugares").forEach { (id, titulo) ->
                Column(Modifier.weight(1f).clip(MaterialTheme.shapes.medium).background(cs.surface).padding(8.dp)) {
                    Canvas(Modifier.fillMaxWidth().height(70.dp)) {
                        with(d) { iconoMapa(id, EstadoIcono(cs.primary, cs.secondary, cs.surface, cs.onSurface), cs.surface.luminance() < 0.5f) }
                    }
                    Text(titulo, style = MaterialTheme.typography.titleSmall, color = cs.onSurface, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
        // El tablero de la pestaña Diario (ui/pantallas/Diarios.kt): en su escena con 10 dp de relleno, sin lámpara.
        Text("Diario · el tablero de notas", style = MaterialTheme.typography.titleLarge, color = cs.onBackground, modifier = Modifier.padding(top = 12.dp))
        EscenaArnes(Modifier.fillMaxWidth(), relleno = PaddingValues(10.dp), luz = false, llena = false) {
            TableroDeNotas(notasEjemplo()) { }
        }
    }
}

/** Las notas de `Capturas.notasYMetas`: una idea fijada, un propósito, un recordatorio y un pensamiento. */
private fun notasEjemplo(): List<Nota> {
    val hoy = System.getenv("ARNES_HOY")?.takeIf { it.isNotBlank() } ?: Fechas.diaLogico()
    return listOf(
        Nota("n1", hoy, hoy, "Probar escribir a mano antes de dormir; con @ana salió la idea de #lectura.", "idea", fijada = true),
        Nota("n2", hoy, hoy, "Que el lunes empiece sin el teléfono en la cama.", "proposito"),
        Nota("n3", hoy, Fechas.sumar(hoy, -2), "Llamar a @luis por lo del viaje.", "recordatorio", recordar_el = hoy),
        Nota("n4", hoy, Fechas.sumar(hoy, -5), "Me gusta más el parque temprano, cuando no hay nadie.", "pensamiento"),
    )
}

/** Como `Recuadro` de ui/pantallas/Galerias.kt: el título y el detalle encima, y el dibujo en su escena. */
@Composable
private fun RecuadroArnes(titulo: String, detalle: String, alto: Dp, luz: Boolean, dibujo: @Composable BoxScope.() -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(Estilo.seccion(titulo, null).first, style = MaterialTheme.typography.titleLarge, color = cs.onBackground)
                Text(detalle, style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, maxLines = 2)
            }
            Text("→", style = MaterialTheme.typography.titleLarge, color = cs.primary)
        }
        EscenaArnes(Modifier.fillMaxWidth().height(alto), luz = luz, llena = true, contenido = dibujo)
    }
}

/**
 * Como `Escena` de ui/Escena.kt, sin la pared del tema (`paredTema`, que arrastra media app): el mismo degradado de fondo,
 * las mismas esquinas y la misma lámpara de los modos oscuros (`luzEnPared` y `luzEnMueble`, sacadas tal cual con `piezas`).
 */
@Composable
private fun EscenaArnes(modifier: Modifier, relleno: PaddingValues = PaddingValues(0.dp), luz: Boolean, llena: Boolean, contenido: @Composable BoxScope.() -> Unit) {
    val cs = MaterialTheme.colorScheme
    val tema = Estilo.tema
    val oscuro = esOscuro()
    val lampara = luz && oscuro && !tema.sinDiseno
    val calida = luzCalida(cs.primary)
    val base = lerp(lerp(cs.surface, cs.primary, 0.05f), cs.onSurface, if (oscuro) 0.04f else 0.025f)
    val arriba = lerp(base, if (oscuro) Color.White else cs.surface, if (oscuro) 0.03f else 0.35f)
    val abajo = lerp(base, cs.onSurface, if (oscuro) 0.05f else 0.06f)
    val forma = MaterialTheme.shapes.medium
    Box(
        modifier
            .graphicsLayer { shape = forma; clip = true; compositingStrategy = CompositingStrategy.Offscreen }
            .background(Brush.verticalGradient(listOf(arriba, base, abajo)))
            .drawWithContent {
                if (lampara) luzEnPared(calida)
                drawContent()
                if (lampara) luzEnMueble(calida, llena)
            }
            .padding(relleno),
        content = contenido,
    )
}

/** En el nicho de Social: seis personas unidas, con su nombre (como el grafo de `dibujarGrafo`, en simple). */
@Composable
private fun GrafoDeRelleno() {
    val cs = MaterialTheme.colorScheme
    Box(Modifier.fillMaxSize().padding(6.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val c = center
            val r = size.minDimension * 0.34f
            val puntos = (0 until 6).map { k -> val a = k * 1.047f + 0.3f; Offset(c.x + r * 1.5f * cos(a), c.y + r * sin(a)) }
            puntos.forEach { p -> drawLine(cs.onSurface.copy(alpha = 0.45f), c, p, 1.2.dp.toPx()) }
            drawLine(cs.onSurface.copy(alpha = 0.3f), puntos[0], puntos[1], 1.dp.toPx())
            drawLine(cs.onSurface.copy(alpha = 0.3f), puntos[3], puntos[4], 1.dp.toPx())
            drawCircle(cs.primary, 9.dp.toPx(), c)
            puntos.forEachIndexed { k, p -> drawCircle(if (k % 2 == 0) cs.secondary else cs.tertiary, 6.dp.toPx(), p) }
        }
        Text("tú", style = MaterialTheme.typography.labelSmall, color = cs.onSurface, modifier = Modifier.align(Alignment.Center).padding(top = 30.dp))
        Text("ana", style = MaterialTheme.typography.labelSmall, color = cs.onSurface, modifier = Modifier.align(Alignment.TopEnd))
        Text("luis", style = MaterialTheme.typography.labelSmall, color = cs.onSurface, modifier = Modifier.align(Alignment.BottomStart))
    }
}

/** En el nicho de Mapas: un mapamundi de manchas con cuatro países rascados (en el lugar de `MapaRascar`). */
@Composable
private fun MapaDeRelleno() {
    val cs = MaterialTheme.colorScheme
    Canvas(Modifier.fillMaxSize().padding(2.dp)) {
        val w = size.width; val h = size.height
        val tierra = cs.onSurface.copy(alpha = 0.22f)
        listOf(Triple(0.2f, 0.35f, 0.16f), Triple(0.27f, 0.7f, 0.09f), Triple(0.52f, 0.33f, 0.1f), Triple(0.55f, 0.65f, 0.11f), Triple(0.75f, 0.38f, 0.17f), Triple(0.85f, 0.75f, 0.07f))
            .forEach { (x, y, r) -> drawOval(tierra, Offset(w * (x - r), h * (y - r * 0.7f)), androidx.compose.ui.geometry.Size(w * r * 2, h * r * 1.4f)) }
        listOf(0.22f to 0.36f, 0.53f to 0.32f, 0.56f to 0.62f, 0.27f to 0.68f).forEach { (x, y) -> drawCircle(cs.primary, w * 0.025f, Offset(w * x, h * y)) }
        drawRect(cs.onSurface.copy(alpha = 0.35f), style = Stroke(1.dp.toPx()))
    }
}

/** En el nicho de Otras vidas: cinco medallas en fila, cada una con su año debajo (en el lugar de `MedallaVida`). */
@Composable
private fun MedallasDeRelleno() {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        listOf("1340", "1789", "1967", "2003", "3150").forEachIndexed { k, anio ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Canvas(Modifier.width(46.dp).height(46.dp)) {
                    drawCircle(if (k == 2) cs.primary else cs.secondary)
                    drawCircle(cs.surface, size.minDimension * 0.36f)
                    drawCircle(cs.onSurface.copy(alpha = 0.6f), size.minDimension * 0.36f, style = Stroke(1.dp.toPx()))
                }
                Text(anio, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
    }
}
