// Una hoja de muestra de los trazos de Psicodelia v2 (ui/PsicodeliaTrazos.kt): TrazosCiudad con ARNES_CARA=base y
// TrazosTinta con ARNES_CARA=intensa (y ARNES_INTENSIDAD). No es un mueble ni una pantalla de la app: sirve para ver que
// los trazos de la base dibujan lo que dicen, antes de que las otras tandas los usen.
package arnes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.Cara
import io.github.ruddy2003.vestigium.ui.EstadoTema
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TrazosCiudad
import io.github.ruddy2003.vestigium.ui.TrazosCiudad.cables
import io.github.ruddy2003.vestigium.ui.TrazosCiudad.grano
import io.github.ruddy2003.vestigium.ui.TrazosCiudad.ladrillos
import io.github.ruddy2003.vestigium.ui.TrazosCiudad.persiana
import io.github.ruddy2003.vestigium.ui.TrazosCiudad.placaLetrero
import io.github.ruddy2003.vestigium.ui.TrazosCiudad.poste
import io.github.ruddy2003.vestigium.ui.TrazosCiudad.vineta
import io.github.ruddy2003.vestigium.ui.TrazosTinta
import io.github.ruddy2003.vestigium.ui.TrazosTinta.bandas
import io.github.ruddy2003.vestigium.ui.TrazosTinta.conRegistro
import io.github.ruddy2003.vestigium.ui.TrazosTinta.llenarForma
import io.github.ruddy2003.vestigium.ui.TrazosTinta.trama

@Composable
internal fun MuestraTrazos(oscuro: Boolean) {
    val medidor = rememberTextMeasurer()
    val cara = EstadoTema.cara
    val i = EstadoTema.intensidad
    Canvas(Modifier.fillMaxWidth().height(700.dp).padding(16.dp)) {
        when (cara) {
            Cara.BASE -> ciudad(oscuro, medidor)
            Cara.INTENSA -> tinta(oscuro, i, medidor)
        }
    }
}

private fun DrawScope.muestras(colores: List<Color>, y: Float) {
    val lado = (size.width - 8.dp.toPx() * (colores.size - 1)) / colores.size
    colores.forEachIndexed { k, c ->
        drawRect(c, Offset(k * (lado + 8.dp.toPx()), y), Size(lado, lado))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(k * (lado + 8.dp.toPx()), y), Size(lado, lado), style = Stroke(1f))
    }
}

private fun DrawScope.ciudad(oscuro: Boolean, medidor: TextMeasurer) {
    val c = TrazosCiudad
    val panel = Rect(0f, 0f, size.width, 330.dp.toPx())
    clipRect(panel.left, panel.top, panel.right, panel.bottom) {
        if (oscuro) drawRect(c.PARED_NOCHE, panel.topLeft, panel.size)
        else drawRect(Brush.verticalGradient(listOf(c.BRUMA, c.LUZ_BAJA), panel.top, panel.bottom * 0.8f), panel.topLeft, panel.size)
        if (!oscuro) drawCircle(c.SOL_VELADO, 22.dp.toPx(), Offset(panel.width * 0.14f, panel.height * 0.48f))
        val suelo = panel.height * 0.84f
        var x = -6.dp.toPx(); var k = 0
        while (x < panel.width) {
            val ancho = (54 + (k * 7) % 12).dp.toPx(); val alto = (110 + (k * 13) % 24).dp.toPx()
            val col = c.CASAS[k % c.CASAS.size].let { if (oscuro) c.enSombra(it, 0.7f) else it }
            drawRect(col, Offset(x, suelo - alto), Size(ancho, alto))
            drawRect(c.alSol(col), Offset(x + ancho * 0.08f, suelo - alto + 12.dp.toPx()), Size(ancho * 0.17f, alto - 20.dp.toPx()))
            drawRect(c.enSombra(col), Offset(x + ancho * 0.43f, suelo - alto + 12.dp.toPx()), Size(ancho * 0.17f, alto - 20.dp.toPx()))
            drawRect(c.enSombra(col), Offset(x + ancho - 4.dp.toPx(), suelo - alto), Size(4.dp.toPx(), alto))
            x += ancho + 2.dp.toPx(); k++
        }
        drawRect(c.enSombra(c.CASA_GRIS, 0.6f), Offset(0f, suelo), Size(panel.width, panel.height - suelo))
        poste(Offset(panel.width * 0.33f, suelo), panel.height * 0.8f, 5.dp.toPx())
        poste(Offset(panel.width * 0.9f, suelo), panel.height * 0.8f, 5.dp.toPx())
        cables(Offset(0f, panel.height * 0.27f), Offset(panel.width, panel.height * 0.27f), 8.dp.toPx(), 3.dp.toPx(), 1.4.dp.toPx())
        vineta(if (oscuro) c.VINETA_NOCHE else c.VINETA_DIA)
        grano(if (oscuro) c.GRANO_NOCHE else c.GRANO_DIA, zona = panel)
    }
    val y2 = panel.bottom + 16.dp.toPx()
    ladrillos(Rect(0f, y2, size.width * 0.48f, y2 + 110.dp.toPx()), 9.dp.toPx())
    persiana(Rect(size.width * 0.52f, y2, size.width, y2 + 110.dp.toPx()), 9, abierta = 0.4f)
    val placa = Rect(size.width * 0.18f, y2 + 126.dp.toPx(), size.width * 0.82f, y2 + 176.dp.toPx())
    placaLetrero(placa)
    val t = medidor.measure("LADO A · PISTA 4", TextStyle(fontFamily = LetraExtra.franklinTitular, fontSize = 20.sp, color = c.SOMBRA))
    drawText(t, topLeft = Offset(placa.center.x - t.size.width / 2f, placa.center.y - t.size.height / 2f))
    muestras(listOf(c.BRUMA, c.LUZ_BAJA, c.SOL_VELADO, c.CASA_GRIS, c.CASA_PINTADA, c.AL_SOL, c.SOMBRA, c.POSTE, c.PASAMANOS, c.COCHE), y2 + 196.dp.toPx())
    muestras(listOf(c.PARED_NOCHE, c.LAMPARA, c.LAMPARA_LUZ, c.MADERA, c.ETIQUETA, c.LETRERO, c.VINETA, c.pelicula(Color(0xFF2060E0)), c.pelicula(Color.Red), c.pelicula(Color.White)), y2 + 240.dp.toPx())
}

private fun DrawScope.tinta(oscuro: Boolean, i: Float, medidor: TextMeasurer) {
    val t = TrazosTinta
    val m = t.medidas(i)
    val mag = t.tinta(TrazosTinta.Tinta.MAGENTA, oscuro, i)
    val ber = t.tinta(TrazosTinta.Tinta.BERMELLON, oscuro, i)
    val ult = t.tinta(TrazosTinta.Tinta.ULTRAMAR, oscuro, i)
    val panel = Rect(0f, 0f, size.width, 360.dp.toPx())
    drawRect(t.papel(oscuro), panel.topLeft, panel.size)
    val marco = t.arco(panel.deflate(6.dp.toPx()), 60.dp.toPx(), 2.dp.toPx())
    val centro = Offset(panel.center.x, panel.height * 0.52f)
    val dentro: DrawScope.() -> Unit = {
        if (oscuro) {
            conRegistro(TrazosTinta.Tinta.ULTRAMAR, m.registro) {
                bandas(centro, panel.height, m.bandasPorLado, 12.dp.toPx(), m.amplitud.dp.toPx(), m.grosor.dp.toPx(), ult, fase = 0.06f)
            }
            conRegistro(TrazosTinta.Tinta.MAGENTA, m.registro) {
                bandas(centro + Offset(6.dp.toPx(), 0f), panel.height, m.bandasPorLado, 12.dp.toPx(), m.amplitud.dp.toPx(), m.grosor.dp.toPx() * 0.75f, mag, fase = 0.06f)
            }
            if (m.bermellon > 0f) conRegistro(TrazosTinta.Tinta.BERMELLON, m.registro) {
                bandas(centro - Offset(4.dp.toPx(), 0f), panel.height, m.bandasPorLado, 12.dp.toPx(), m.amplitud.dp.toPx(), m.grosor.dp.toPx() * 0.5f,
                    ber.copy(alpha = m.bermellon), fase = 0.06f)
            }
        } else {
            // Los rayos plenos y, entre ellos, la mitad de cada hueco grabada con trama de líneas (los medios tonos).
            drawPath(t.rayos(centro, 84.dp.toPx(), panel.width, 44, 22.dp.toPx(), 2.4f), t.grabado(false))
            // La misma familia girada un sector cae en los huecos.
            rotate(360f / 44f, centro) {
                trama(t.rayos(centro, 84.dp.toPx(), panel.width, 44, 22.dp.toPx(), 2.4f), 3.dp.toPx(), 1.1.dp.toPx(), t.grabado(false), 0f)
            }
        }
    }
    // Con i ≥ 0.9 lo de dentro se sale del marco del cartel, pero no del panel de la muestra.
    if (m.fueraDelMarco) clipRect(panel.left, panel.top, panel.right, panel.bottom) { dentro() } else clipPath(marco) { dentro() }
    for (k in m.anillos downTo 1) {
        val r = (54 + k * 9).dp.toPx()
        drawCircle(listOf(mag, ult, ber)[k % 3], r, centro, style = Stroke(3.dp.toPx()))
    }
    drawCircle(t.papel(oscuro), 56.dp.toPx(), centro)
    drawCircle(if (oscuro) TrazosTinta.CREMA else t.papel(false), 48.dp.toPx(), centro)
    if (!oscuro) drawCircle(ult, 52.dp.toPx(), centro, style = Stroke(6.dp.toPx()))
    for (k in 0 until 4) drawCircle(t.grabado(oscuro).copy(alpha = if (oscuro) 0.25f else 0.8f), (42 - k * 5).dp.toPx(), centro, style = Stroke(1f))
    drawCircle(mag, 17.dp.toPx(), centro)
    drawCircle(t.papel(oscuro), 3.dp.toPx(), centro)
    drawPath(marco, t.grabado(oscuro), style = Stroke(2.dp.toPx()))
    // El título que llena su forma, doblado por la onda de la intensidad, con su segunda pasada en magenta corrida.
    val letras = t.letras(medidor, "LADO B", TextStyle(fontFamily = LetraExtra.bloque, fontSize = 48.sp))
    val caja = Rect(16.dp.toPx(), panel.bottom + 16.dp.toPx(), size.width - 16.dp.toPx(), panel.bottom + 96.dp.toPx())
    if (m.registroTitulo > 0f) {
        val d = m.registroTitulo.dp.toPx()
        llenarForma(letras, caja.translate(d, d * 0.6f), t.rol(TrazosTinta.Rol.PRIMARIO, oscuro, i), TrazosTinta.FormaLetras.BARRIL, m.ondaTitulo.dp.toPx())
    }
    llenarForma(letras, caja, if (oscuro) TrazosTinta.CREMA else TrazosTinta.GRABAR, TrazosTinta.FormaLetras.BARRIL, m.ondaTitulo.dp.toPx())
    val caja2 = Rect(40.dp.toPx(), caja.bottom + 12.dp.toPx(), size.width - 40.dp.toPx(), caja.bottom + 64.dp.toPx())
    llenarForma(letras, caja2, ult, TrazosTinta.FormaLetras.ARCO)
    val y = caja2.bottom + 16.dp.toPx()
    muestras(listOf(mag, ber, ult, t.grabado(oscuro), t.sobreimpresion(mag, ult, oscuro),
        t.rol(TrazosTinta.Rol.PRIMARIO, oscuro, i), t.rol(TrazosTinta.Rol.SECUNDARIO, oscuro, i), t.rol(TrazosTinta.Rol.TERCIARIO, oscuro, i)), y)
}
