package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ETIQUETA_ARCHIVO
import io.github.ruddy2003.vestigium.ui.FormaFicha
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.altoBarraVentana
import io.github.ruddy2003.vestigium.ui.archivoIdeas
import io.github.ruddy2003.vestigium.ui.colorEtiqueta
import io.github.ruddy2003.vestigium.ui.dibujarContenedor
import io.github.ruddy2003.vestigium.ui.dibujarInteriorArchivo
import io.github.ruddy2003.vestigium.ui.formaDeFicha
import io.github.ruddy2003.vestigium.ui.margenLado
import io.github.ruddy2003.vestigium.ui.margenSuperior
import io.github.ruddy2003.vestigium.ui.materia
import io.github.ruddy2003.vestigium.ui.adornoFicha as adornoFichaViejo
import io.github.ruddy2003.vestigium.ui.tintaInterior as tintaInteriorVieja

/**
 * El dibujo de antes (las formas compartidas de [io.github.ruddy2003.vestigium.ui.FormaArchivo]), mientras a un tema no le
 * llega su lote con el mueble propio. Se ve como antes: los contenedores sueltos sobre el interior del archivo.
 */
class DisenoAnterior(private val t: TemaId) : DisenoIdeas {
    private val a = archivoIdeas(t)
    private val m = materia(t)
    private val tira = a.formaFicha == FormaFicha.TIRA

    override val cols = 2
    override val proporcion = 1.55f
    override val corona = 0f
    override val base = 0f
    override val lado = 0.028f
    override val hueco = 0.028f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        clipRect { dibujarInteriorArchivo(a, m) }
        drawRect(Color.Black.copy(alpha = 0.25f), Offset.Zero, size, style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        if (c.vacio) return
        dibujarContenedor(a, m, t, c.n)
    }

    override val rotulo: Rect = ETIQUETA_ARCHIVO
    override fun tintaRotulo(oscuro: Boolean): Color = colorEtiqueta(a, m).second
    override val letraRotulo: FontFamily? = null
    override val salida = 0f
    override val placa: Rect? = null
    override fun tintaPlaca(oscuro: Boolean): Color = tintaInteriorVieja(a, m)

    override fun DrawScope.interior(oscuro: Boolean) = dibujarInteriorArchivo(a, m)
    override fun tintaInterior(oscuro: Boolean): Color = tintaInteriorVieja(a, m)
    override val pared: Dp = 0.dp
    override val columnas = if (tira) 3 else 2
    override val lineas = if (tira) 9 else 4

    override fun forma(semilla: Int): Shape = formaDeFicha(a.formaFicha, semilla)
    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) = adornoFichaViejo(a.formaFicha, p, m, grande)
    override val margenes = MargenesFicha(margenSuperior(a.formaFicha), margenLado(a.formaFicha), 6.dp,
        margenSuperior(a.formaFicha, true), margenLado(a.formaFicha, true), 22.dp)
    override val proporcionFicha = if (tira) 0.5f else 1.2f
    override val anchoGrande = 1f
    override val proporcionGrande = 0.78f
    override val barra = a.formaFicha == FormaFicha.VENTANA
    override fun altoBarra(grande: Boolean): Dp = altoBarraVentana(grande)
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val c = when (id) { "convence" -> p.acento; "piensa" -> p.detalle; "duda" -> p.tenue; "desacuerdo" -> Color(0xFFB3261E); else -> return }
        drawCircle(c, size.minDimension * 0.32f, center)
    }

    override fun DrawScope.marcaFijada(p: Pagina) {
        val w = size.width; val h = size.height
        drawPath(Path().apply { moveTo(w / 2, h * 0.12f); lineTo(w * 0.86f, h / 2); lineTo(w / 2, h * 0.88f); lineTo(w * 0.14f, h / 2); close() }, p.acento)
    }

    override val desorden = if (a.formaFicha == FormaFicha.RECORTE || a.formaFicha == FormaFicha.LAJA) 1.5f else 0f
}
