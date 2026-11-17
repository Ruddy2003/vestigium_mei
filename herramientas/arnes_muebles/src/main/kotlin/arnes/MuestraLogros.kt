// Una hoja de muestra de los logros de Psicodelia v2 (ui/logros/LogrosPsicodelia.kt) y de sus figuras de pantalla
// (ui/PsicodeliaPantallas.kt), en la cara (ARNES_CARA) y la intensidad (ARNES_INTENSIDAD) del arnés: los nueve trofeos sobre
// su pedestal en los cuatro metales, a su tamaño grande y chico, uno sin ganar, el pedestal y la placa del hall, el salón,
// el plano de la campaña con el trolebús y la meta, y los objetos de Tu año. No es una pantalla de la app.
package arnes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.EstadoTema
import io.github.ruddy2003.vestigium.ui.PantallasPsicodelia
import io.github.ruddy2003.vestigium.ui.logros.EstadoMarcador
import io.github.ruddy2003.vestigium.ui.logros.EstadoObjeto
import io.github.ruddy2003.vestigium.ui.logros.EstadoPedestal
import io.github.ruddy2003.vestigium.ui.logros.EstadoSalon
import io.github.ruddy2003.vestigium.ui.logros.EstadoVictoria
import io.github.ruddy2003.vestigium.ui.logros.LogrosPsicodelia
import io.github.ruddy2003.vestigium.ui.logros.TrofeoPsi

/** Los metales de los niveles, como `METAL` de pantallas/Trofeos.kt: oscuro, base y brillo. */
private val METALES = listOf(
    Triple(Color(0xFF6E4423), Color(0xFFB27A45), Color(0xFFE9BE8F)),
    Triple(Color(0xFF6F7781), Color(0xFFBFC6CF), Color(0xFFF4F7FA)),
    Triple(Color(0xFF8E6812), Color(0xFFE3B748), Color(0xFFFFEBA6)),
    Triple(Color(0xFF237E95), Color(0xFF7FD8E8), Color(0xFFE9FCFF)),
)
private val CATEGORIAS = listOf("Diario", "Mundo", "Cuerpo", "Mente", "Cultura", "Gente", "Constancia", "Curiosos", "Tuyos")
private val PARADAS = listOf(Offset(0.24f, 0.72f), Offset(0.08f, 0.38f), Offset(0.36f, 0.12f), Offset(0.6f, 0.6f), Offset(0.38f, 0.72f), Offset(0.39f, 0.46f), Offset(0.9f, 0.74f))

@Composable
private fun Rotulo(t: String) = Text(t, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 11.sp, modifier = Modifier.padding(top = 10.dp, bottom = 3.dp))

/** Como `dibujarTrofeoTema` de pantallas/Trofeos.kt: el pedestal del tema y, encima, la figura. */
private fun DrawScope.trofeo(cat: String, nivel: Int, hecho: Boolean, oscuro: Boolean, tinta: Color) {
    val (mo, mb, mbr) = METALES[nivel - 1]
    with(LogrosPsicodelia) {
        pedestalTrofeo(EstadoPedestal(hecho, nivel, mo, mb, mbr, cat.hashCode()), oscuro)
        trofeo(cat, TrofeoPsi(hecho, nivel, mo, mb, mbr, tinta.copy(alpha = 0.13f), tinta.luminance() > 0.5f))
    }
}

@Composable
private fun Trofeo(cat: String, nivel: Int, alto: Dp, hecho: Boolean = true) {
    val cs = MaterialTheme.colorScheme
    val oscuro = cs.background.luminance() < 0.5f
    Canvas(Modifier.width(alto * 0.82f).height(alto)) { trofeo(cat, nivel, hecho, oscuro, cs.onSurface) }
}

@Composable
internal fun MuestraLogros(oscuro: Boolean) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("cara ${EstadoTema.cara} · i = ${EstadoTema.intensidad} · ${if (oscuro) "oscuro" else "claro"}", color = cs.onBackground, fontSize = 13.sp)
        Rotulo("los nueve trofeos: diario · mundo · cuerpo · mente · cultura · gente · constancia · curiosos · tuyos (bronce, plata, oro, legendario)")
        listOf(0..2, 3..5, 6..8).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                fila.forEach { j -> Column { Trofeo(CATEGORIAS[j], j % 4 + 1, 150.dp); Text(CATEGORIAS[j], color = cs.onBackground, fontSize = 11.sp) } }
            }
        }
        Rotulo("a 56 dp (como en Vida y en Tu año) y uno sin ganar")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CATEGORIAS.forEachIndexed { j, c -> Trofeo(c, (j % 4) + 1, 56.dp) }
            Trofeo("Cultura", 1, 56.dp, hecho = false)
        }
        Rotulo("el pedestal del hall y la placa (la entrada del baile)")
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            (1..4).forEach { n ->
                val (mo, mb, mbr) = METALES[n - 1]
                Column {
                    Canvas(Modifier.width(84.dp).height(22.dp)) { with(LogrosPsicodelia) { pedestalHall(EstadoPedestal(true, n, mo, mb, mbr, n), oscuro) } }
                    Canvas(Modifier.width(110.dp).height(46.dp)) { with(LogrosPsicodelia) { placaHall(EstadoPedestal(true, n, mo, mb, mbr, n), oscuro) } }
                    Text("Racha de 30", color = LogrosPsicodelia.tintaPlaca(oscuro), fontSize = 11.sp, modifier = Modifier.padding(start = 26.dp))
                }
            }
        }
        Rotulo("el salón del hall (tres columnas)")
        Box(Modifier.fillMaxWidth().height(330.dp)) {
            Canvas(Modifier.matchParentSize()) {
                drawRect(cs.background)
                with(LogrosPsicodelia) { escenaSalon(EstadoSalon(3, cs.background, 0), oscuro) }
            }
            Row(Modifier.fillMaxWidth().padding(top = 70.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("Diario", "Gente", "Constancia").forEachIndexed { j, c -> Trofeo(c, j + 2, 120.dp) }
            }
        }
        Rotulo("el plano de la campaña, con el camino, las paradas, el trolebús (y su salto) y la meta")
        Canvas(Modifier.fillMaxWidth().height(((1080 / 2.625f - 32) / 2.4f).dp)) {
            drawRect(cs.surface)
            val pts = PARADAS.map { Offset(it.x * size.width, it.y * size.height) }
            with(PantallasPsicodelia) {
                fondoCampana(oscuro, 3f / 7f, pts)
                val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (p in pts.drop(1)) lineTo(p.x, p.y) }
                caminoCampana(camino, oscuro)
                pts.forEachIndexed { j, p -> paradaCampana(p, 4.dp.toPx(), cs.primary, j == 6, oscuro) }
            }
            with(LogrosPsicodelia) {
                marcadorCampana(EstadoMarcador(pts[3], 0f, cs.tertiary), oscuro)
                marcadorCampana(EstadoMarcador(pts[1], 1f, cs.tertiary), oscuro)
                victoriaCampana(EstadoVictoria(pts[6], cs.tertiary), oscuro)
            }
        }
        Rotulo("los objetos de Tu año: luna · huella · pesa (a 6, 12 y 20 dp)")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("luna", "huella", "pesa").forEach { f ->
                listOf(6f, 12f, 20f).forEach { r ->
                    Canvas(Modifier.width((r * 2.6f).dp).height((r * 2.6f).dp)) {
                        with(LogrosPsicodelia) { objetoAnio(EstadoObjeto(f, center, r.dp.toPx(), cs.primary, cs.surface), oscuro) }
                    }
                }
            }
        }
    }
}
