package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Florencia, como se ve por la ventana grande del taller: el cielo del día, las colinas con sus cipreses y sus villas,
 * varias filas de tejados y torres y, más grande que todo, la cúpula de la catedral con su linterna y su campanario.
 *
 * Lo comprobado y lo que no:
 * - **Comprobado en la sesión** (Wikipedia en inglés, «Florence Cathedral» y «Giotto's Campanile»): la cúpula es de
 *   ocho lados y su tambor tiene óculos; la linterna se terminó en 1461 y lleva una bola de cobre dorado con su cruz,
 *   que esa página fecha en 1469 (otras fuentes dan 1471: no se comprobó cuál); el campanario es cuadrado, revestido de
 *   mármol blanco, verde y rojo, y acaba en una terraza, sin aguja.
 * - **[Aproximación]**, sin comprobar: que en 1490 el tambor no tuviera todavía galería (va de piedra desnuda), que los
 *   nervios de fuera sean de mármol blanco sobre teja roja (así se ve hoy) y las proporciones de todo, que están
 *   dibujadas de memoria, sin plano delante. La torre almenada de la izquierda evoca la del palacio de la Señoría.
 * - **[Diseño propio]** Dónde queda cada cosa: desde qué casa se mira, qué torres se ven a los lados y cómo son las
 *   casas de delante. No retrata una vista real.
 */
internal object CiudadRenacimiento {
    private val C = ColorRenacimiento
    private val R get() = EscenaRenacimiento

    /** La altura de los ojos: el horizonte de la ciudad. */
    private const val H = 124f

    /** El eje de la cúpula y el del campanario. */
    private const val CUPULA = 208f
    private const val CAMPANARIO = 172f

    /** Dónde está la plaza de la catedral, de donde sube el humo del carro de Pascua. */
    private val PLAZA = Offset(186f, 118f)

    /** Las antorchas que arden de noche en lo alto de la torre del palacio y del campanario, y en la plaza. */
    private val ANTORCHAS = listOf(Offset(143.4f, 86f), Offset(150.6f, 86f), Offset(CAMPANARIO - 4f, 72.4f), Offset(CAMPANARIO + 4f, 72.4f), Offset(190f, 121f))

    /** La bola dorada de la linterna. */
    val BOLA = Offset(CUPULA, 61.6f)

    private class Casa(val x: Float, val ancho: Float, val pie: Float, val alto: Float, val muro: Int, val tejado: Float, val ventanas: Int, val torre: Boolean)

    /** Una fila de casas con semilla fija: son siempre las mismas. */
    private fun fila(nombre: String, x0: Float, x1: Float, pie: Float, alto0: Float, alto1: Float, ancho0: Float, ancho1: Float, torres: Int): List<Casa> = buildList {
        val az = Azar(Semilla.de("renacimiento", "fila", nombre)); var x = x0; var faltan = torres
        while (x < x1) {
            val w = az.entre(ancho0, ancho1); val torre = faltan > 0 && az.real() < 0.16f
            if (torre) faltan--
            add(Casa(x, if (torre) w * 0.5f else w, pie + az.entre(-1f, 1f), if (torre) alto1 * az.entre(1.5f, 2f) else az.entre(alto0, alto1), az.entero(4), az.entre(0.22f, 0.36f), 1 + az.entero(3), torre))
            x += (if (torre) w * 0.5f else w) + az.entre(-0.6f, 1.4f)
        }
    }

    private val LEJOS = fila("lejos", 128f, 268f, 120f, 3f, 5.4f, 5f, 9f, 3)
    private val MEDIA = fila("media", 126f, 270f, 126f, 6f, 10f, 8f, 13f, 2)
    private val CERCA = fila("cerca", 124f, 272f, 134f, 9f, 14f, 13f, 20f, 1)

    private val MUROS = listOf(C.MURO, lerp(C.OCRE, C.MURO, 0.5f), lerp(C.MURO, C.COTTO, 0.3f), lerp(C.MURO, C.PIEDRA, 0.4f))

    // ---------------------------------------------------------------- Lo quieto

    fun DrawScope.vista(m: Momento, ext: Luces, caja: Rect) {
        val r = Rayo(ext); val luz = if (r.lat > 0f) 1f else -1f; val nieve = R.nevado(m); val clima = m.ficha.clima
        val semilla = m.semilla("cielo")
        // El cielo del día, a media escala: por la ventana cabe un buen trozo, con su sol o su luna.
        val hor = (H - caja.top) / 0.5f
        translate(199f - 95f, caja.top) { scale(0.5f, 0.5f, Offset.Zero) { cielo(ext, hor, semilla); astros(ext, hor, semilla); nubes(ext, hor, clima, semilla) } }
        colinas(m, ext, r, luz, nieve)
        for (c in LEJOS) casa(m, ext, c, luz, r, nieve, 0.5f)
        catedral(m, ext, r, luz, nieve)
        torres(m, ext, r, luz, nieve)
        // La bruma del río entre las filas, que las despega.
        mancha(Offset(200f, 121f), 90f, 3.2f, lerp(ext.bruma, Color.White, 0.2f), 0.26f + 0.2f * ext.nublado)
        for (c in MEDIA) casa(m, ext, c, luz, r, nieve, 0.26f)
        for (c in CERCA) casa(m, ext, c, luz, r, nieve, 0.06f)
        tejadoDeDelante(m, ext, r, luz, nieve)
        delDia(m, ext, r)
        // El tiempo: el velo de la lluvia o de la niebla, que «tapa la cúpula», y la claridad de la nieve.
        if (clima == Clima.NIEBLA) {
            drawRect(Brush.verticalGradient(0f to ext.bruma.copy(alpha = 0.78f), 0.6f to ext.bruma.copy(alpha = 0.6f), 1f to ext.bruma.copy(alpha = 0.3f), startY = caja.top, endY = caja.bottom), caja.topLeft, caja.size)
            val az = m.azar("niebla"); repeat(4) { mancha(Offset(az.entre(caja.left, caja.right), az.entre(96f, 134f)), az.entre(30f, 60f), az.entre(4f, 8f), lerp(ext.bruma, Color.White, 0.3f), 0.5f) }
        } else tiempoQuieto(ext, clima, H, semilla)
    }

    /**
     * Dos filas de colinas que asoman por encima de los tejados, a los lados de la catedral: la de lejos, azulada; la
     * de cerca, con sus bancales, olivares (tronco y copa gris plata), cipreses en hilera y unas villas con su torre.
     */
    private fun DrawScope.colinas(m: Momento, ext: Luces, r: Rayo, luz: Float, nieve: Boolean) {
        val verde = when (m.ficha.estacion) {
            Estacion.VERANO, Estacion.SECAS, Estacion.COSECHA -> lerp(C.VERDE, C.OCRE, 0.36f)
            Estacion.OTONO -> lerp(C.VERDE, C.OCRE, 0.5f)
            Estacion.INVIERNO -> lerp(C.VERDE, C.PIEDRA, 0.45f)
            Estacion.PRIMAVERA, Estacion.LLUVIAS, Estacion.INUNDACION, Estacion.SIEMBRA, Estacion.NINGUNA -> C.VERDE
        }
        val suelo = if (nieve) lerp(verde, C.YESO, 0.7f) else verde
        val lejos = Path().apply { moveTo(120f, H); lineTo(120f, 99f); cubicTo(136f, 91f, 152f, 93f, 168f, 95f); cubicTo(192f, 98f, 214f, 96f, 232f, 93f); cubicTo(246f, 88f, 262f, 90f, 276f, 94f); lineTo(276f, H); close() }
        drawPath(lejos, ext.tono(lerp(suelo, C.ULTRAMAR, 0.36f), 0.9f))
        clipPath(lejos) {
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to lerp(ext.bruma, Color.White, 0.25f).copy(alpha = 0.55f), startY = 92f, endY = 110f), Offset(120f, 86f), Size(160f, 38f))
            // Sus bosques, en manchas más oscuras, y el lado que da la espalda al sol.
            val az = Azar(Semilla.de("renacimiento", "monte")); repeat(10) { mancha(Offset(az.entre(124f, 272f), az.entre(93f, 101f)), az.entre(4f, 9f), az.entre(1.2f, 2.4f), ext.tono(lerp(suelo, C.ULTRAMAR, 0.6f), 0.9f), 0.4f) }
        }
        val cerca = Path().apply { moveTo(120f, H); lineTo(120f, 107f); cubicTo(130f, 100f, 144f, 99f, 156f, 102.6f); cubicTo(166f, 105.6f, 176f, 106f, 190f, 107f); lineTo(230f, 106.6f); cubicTo(240f, 102f, 250f, 98.6f, 262f, 99.6f); cubicTo(268f, 100f, 272f, 101.6f, 276f, 103f); lineTo(276f, H); close() }
        drawPath(cerca, ext.tono(suelo, 0.56f))
        clipPath(cerca) {
            // El lado de la sombra de cada loma, y los bancales, que siguen la ladera.
            drawPath(Path().apply { moveTo(if (luz > 0f) 120f else 146f, 96f); lineTo(if (luz > 0f) 142f else 166f, 96f); lineTo(if (luz > 0f) 134f else 158f, H); lineTo(if (luz > 0f) 120f else 144f, H); close() }, C.SOMBRA.copy(alpha = 0.24f * r.dia), blendMode = BlendMode.Multiply)
            drawPath(Path().apply { moveTo(if (luz > 0f) 232f else 258f, 96f); lineTo(if (luz > 0f) 250f else 276f, 96f); lineTo(if (luz > 0f) 244f else 276f, H); lineTo(if (luz > 0f) 230f else 252f, H); close() }, C.SOMBRA.copy(alpha = 0.2f * r.dia), blendMode = BlendMode.Multiply)
            for (k in 0..3) { val y = 105f + k * 2.6f
                drawPath(Path().apply { moveTo(122f, y + 2f); cubicTo(134f, y - 3f, 148f, y - 3f, 166f, y + 1.6f) }, ext.tono(lerp(suelo, C.OCRE, 0.5f), 0.56f).copy(alpha = 0.6f), style = Stroke(0.5f))
                drawPath(Path().apply { moveTo(232f, y + 1f); cubicTo(244f, y - 4.4f, 258f, y - 5f, 276f, y - 1.6f) }, ext.tono(lerp(suelo, C.OCRE, 0.5f), 0.56f).copy(alpha = 0.6f), style = Stroke(0.5f)) }
        }
        // Los olivares: cada olivo, su tronco torcido y su copa redonda, gris plata, con el lado de la luz.
        val az = Azar(Semilla.de("renacimiento", "olivos")); val plata = if (nieve) lerp(C.VERDE, C.YESO, 0.6f) else lerp(C.VERDE, C.PIEDRA, 0.5f)
        for ((x0, x1, y0, n) in listOf(listOf(140f, 166f, 107f, 9f), listOf(234f, 258f, 105f, 9f))) repeat(n.toInt()) {
            val fila = it % 3; val pie = Offset(x0 + (x1 - x0) * (it / 3) / 2.6f + fila * 1.4f + az.entre(-0.6f, 0.6f), y0 + fila * 3f + az.entre(-0.3f, 0.3f)); val rad = 1.5f + fila * 0.2f
            drawLine(ext.tono(lerp(C.VIGA, C.CARBON, 0.2f), 0.5f), pie, pie + Offset(-0.4f, -1.9f), 0.5f)
            drawOval(ext.tono(lerp(plata, C.CARBON, 0.35f), 0.5f), Offset(pie.x - rad - 0.3f, pie.y - 1.6f - rad * 1.3f), Size(rad * 2f, rad * 1.5f))
            drawOval(ext.tono(lerp(plata, C.YESO, 0.25f), 0.5f), Offset(pie.x - rad * 0.6f - 0.3f + luz * rad * 0.4f, pie.y - 1.7f - rad * 1.3f), Size(rad * 1.2f, rad * 0.9f))
            drawOval(C.SOMBRA.copy(alpha = 0.26f * r.dia), Offset(pie.x - rad - luz * 1.2f, pie.y - 0.4f), Size(rad * 2f, 0.9f), blendMode = BlendMode.Multiply)
        }
        // Las villas y un convento: su muro con el lado de luz, su tejado, sus ventanas y la torre.
        for ((k, v) in listOf(Triple(144f, 101f, 8f), Triple(160f, 104.4f, 5f), Triple(238f, 103.6f, 6f), Triple(252f, 99.4f, 9f)).withIndex()) { val (x, y, w) = v; val h = 3.4f + (k % 2) * 0.8f
            drawRect(ext.cara(C.MURO, luz * 0.7f, 0.5f), Offset(x, y), Size(w, h)); drawRect(ext.cara(C.MURO, -luz, 0.5f), Offset(if (luz > 0f) x else x + w * 0.62f, y), Size(w * 0.38f, h))
            drawPath(poligono(Offset(x - 0.6f, y), Offset(x + w + 0.6f, y), Offset(x + w * 0.74f, y - 1.7f), Offset(x + w * 0.26f, y - 1.7f)), ext.tono(if (nieve) C.YESO else C.TEJA, 0.5f))
            for (j in 0 until (w / 2.2f).toInt()) drawRect(ext.tono(lerp(C.CARBON, C.VIGA, 0.3f), 0.5f).copy(alpha = 0.8f), Offset(x + 1f + j * 2.2f, y + 1.1f), Size(0.7f, 1.2f))
            if (k % 3 == 0) { drawRect(ext.cara(C.MURO, luz * 0.6f, 0.5f), Offset(x + w - 2.2f, y - 4.4f), Size(2.2f, 4.6f)); drawPath(poligono(Offset(x + w - 2.6f, y - 4.4f), Offset(x + w + 0.4f, y - 4.4f), Offset(x + w - 1.1f, y - 5.8f)), ext.tono(if (nieve) C.YESO else C.TEJA, 0.5f)) }
        }
        // Los cipreses: en hilera por el camino de cada villa, y sueltos en la cresta.
        val az2 = Azar(Semilla.de("renacimiento", "cipreses"))
        for ((x0, y0, n) in listOf(Triple(139.4f, 103.4f, 2), Triple(153.4f, 103.6f, 3), Triple(165.6f, 106.6f, 2), Triple(232.6f, 106f, 2), Triple(245f, 102.6f, 3), Triple(262.4f, 101.6f, 3))) repeat(n) { cipres(ext, Offset(x0 + it * 2.5f + az2.entre(-0.3f, 0.3f), y0 + az2.entre(-0.3f, 0.5f)), az2.entre(6.4f, 9.6f), luz, 0.5f, az2) }
    }

    /** Un ciprés: el tronco, y la copa en llama, de borde quebrado, con su lado de luz y su lado de sombra. */
    private fun DrawScope.cipres(ext: Luces, pie: Offset, alto: Float, luz: Float, lejos: Float, az: Azar) {
        val w = alto * 0.17f; val oscuro = lerp(C.VERDE, C.CARBON, 0.55f)
        drawLine(ext.tono(lerp(C.VIGA, C.CARBON, 0.2f), lejos), pie, pie + Offset(0f, -alto * 0.2f), 0.6f)
        drawOval(C.SOMBRA.copy(alpha = 0.22f), Offset(pie.x - w - luz * 1.6f, pie.y - 0.4f), Size(w * 2.4f, 0.8f), blendMode = BlendMode.Multiply)
        val copa = Path().apply {
            moveTo(pie.x - w, pie.y - alto * 0.14f)
            for (k in 1..4) { val u = k / 5f; lineTo(pie.x - w * (1f - u * 0.86f) - az.entre(0f, 0.3f), pie.y - alto * (0.14f + 0.86f * u)) }
            lineTo(pie.x + az.entre(-0.2f, 0.2f), pie.y - alto)
            for (k in 4 downTo 1) { val u = k / 5f; lineTo(pie.x + w * (1f - u * 0.86f) + az.entre(0f, 0.3f), pie.y - alto * (0.14f + 0.86f * u)) }
            lineTo(pie.x + w, pie.y - alto * 0.14f); close()
        }
        drawPath(copa, ext.tono(oscuro, lejos))
        clipPath(copa) { drawRect(ext.tono(lerp(C.VERDE, C.OCRE, 0.2f), lejos).copy(alpha = 0.75f), Offset(if (luz > 0f) pie.x + w * 0.1f else pie.x - w, pie.y - alto), Size(w * 0.9f, alto)) }
    }

    /** Una casa: su fachada con ventanas, el lado en sombra, el alero y el tejado de teja, que se ve desde arriba. */
    private fun DrawScope.casa(m: Momento, ext: Luces, c: Casa, luz: Float, r: Rayo, nieve: Boolean, lejos: Float) {
        val muro = MUROS[c.muro]; val arriba = c.pie - c.alto; val teja = if (nieve) lerp(C.TEJA, C.YESO, 0.82f) else lerp(C.TEJA, C.MURO, 0.1f * c.muro)
        drawRect(ext.cara(muro, luz * 0.5f, lejos), Offset(c.x, arriba), Size(c.ancho, c.alto + 4f))
        // El lado que da la espalda al sol, y las manchas del enlucido.
        val sombra = c.ancho * 0.3f
        drawRect(ext.cara(muro, -1f, lejos), Offset(if (luz > 0f) c.x else c.x + c.ancho - sombra, arriba), Size(sombra, c.alto + 4f))
        if (lejos < 0.4f) { val az = Azar(Semilla.de("renacimiento", "casa", c.x.toInt())); repeat(2) { mancha(Offset(c.x + az.entre(1f, c.ancho - 1f), arriba + az.entre(1f, c.alto)), az.entre(1.5f, 3f), az.entre(1.5f, 3.4f), ext.tono(C.VIGA, lejos), 0.14f) } }
        if (c.torre) {
            // Una casa-torre: de piedra parda, casi ciega. Cada una remata a su modo: almenas, tejadillo, galería de madera o desmochada.
            val parda = lerp(C.OCRE, C.PIEDRA, 0.55f)
            drawRect(ext.cara(parda, luz * 0.5f, lejos), Offset(c.x, arriba), Size(c.ancho, c.alto + 4f)); drawRect(ext.cara(parda, -1f, lejos), Offset(if (luz > 0f) c.x else c.x + c.ancho * 0.6f, arriba), Size(c.ancho * 0.4f, c.alto + 4f))
            for (k in 1..3) drawLine(ext.tono(lerp(parda, C.VIGA, 0.5f), lejos).copy(alpha = 0.3f), Offset(c.x, arriba + c.alto * k / 4f), Offset(c.x + c.ancho, arriba + c.alto * k / 4f), 0.3f)
            when (c.muro) {
                0 -> { drawRect(ext.cara(parda, luz * 0.4f, lejos), Offset(c.x - 0.8f, arriba - 1f), Size(c.ancho + 1.6f, 1.6f)); var x = c.x - 0.8f; while (x < c.x + c.ancho + 0.4f) { drawRect(ext.cara(parda, luz * 0.4f, lejos), Offset(x, arriba - 2.2f), Size(0.9f, 1.3f)); x += 1.7f } }
                1 -> drawPath(poligono(Offset(c.x - 0.8f, arriba), Offset(c.x + c.ancho + 0.8f, arriba), Offset(c.x + c.ancho / 2f, arriba - 2.6f)), ext.tono(if (nieve) C.YESO else C.TEJA, lejos))
                2 -> { drawRect(ext.tono(lerp(C.VIGA, C.CARBON, 0.2f), lejos), Offset(c.x - 1.1f, arriba + 1.4f), Size(c.ancho + 2.2f, 1.7f)); drawRect(ext.tono(if (nieve) C.YESO else lerp(C.TEJA, C.VIGA, 0.3f), lejos), Offset(c.x - 1.3f, arriba + 0.6f), Size(c.ancho + 2.6f, 0.8f)); drawRect(ext.tono(if (nieve) C.YESO else lerp(C.TEJA, C.VIGA, 0.3f), lejos), Offset(c.x - 0.4f, arriba - 0.7f), Size(c.ancho + 0.8f, 0.9f)) }
                else -> drawPath(poligono(Offset(c.x, arriba), Offset(c.x + c.ancho, arriba), Offset(c.x + c.ancho, arriba - 1.6f), Offset(c.x + c.ancho * 0.5f, arriba - 0.5f)), ext.cara(parda, luz * 0.4f, lejos))
            }
            for (k in 0..1) drawRect(ext.tono(C.CARBON, lejos).copy(alpha = 0.7f), Offset(c.x + c.ancho * 0.4f, arriba + c.alto * (0.28f + 0.3f * k)), Size(c.ancho * 0.2f, 1.3f))
            return
        }
        // El tejado: el faldón que mira hacia aquí, con sus canales y sus hiladas de teja, y el alero con su sombra.
        val alto = (c.alto * c.tejado).coerceAtLeast(1.6f); val cerca = lejos < 0.2f
        val faldon = poligono(Offset(c.x - 0.8f, arriba), Offset(c.x + c.ancho + 0.8f, arriba), Offset(c.x + c.ancho - 1f, arriba - alto), Offset(c.x + 1f, arriba - alto))
        drawPath(faldon, ext.cara(teja, 0.3f * r.dia, lejos))
        if (lejos < 0.4f) clipPath(faldon) {
            var y = arriba - 0.7f; while (y > arriba - alto) { drawLine(ext.tono(lerp(teja, C.VIGA, 0.5f), lejos).copy(alpha = 0.4f), Offset(c.x - 1f, y), Offset(c.x + c.ancho + 1f, y), 0.3f); y -= 0.9f }
            var x = c.x; while (x < c.x + c.ancho + 1f) { drawLine(ext.tono(lerp(teja, C.VIGA, 0.6f), lejos).copy(alpha = if (cerca) 0.6f else 0.4f), Offset(x, arriba), Offset(x + (c.x + c.ancho / 2f - x) * 0.1f, arriba - alto), 0.35f); if (cerca) drawLine(ext.tono(lerp(teja, C.YESO, 0.4f), lejos).copy(alpha = 0.4f), Offset(x + 0.6f, arriba), Offset(x + 0.6f + (c.x + c.ancho / 2f - x) * 0.1f, arriba - alto), 0.3f); x += if (cerca) 1.5f else 1.2f }
            // El lado del tejado que da la espalda al sol.
            drawRect(C.SOMBRA.copy(alpha = 0.2f * r.dia), Offset(if (luz > 0f) c.x - 1f else c.x + c.ancho * 0.66f, arriba - alto), Size(c.ancho * 0.34f + 1f, alto), blendMode = BlendMode.Multiply)
        }
        drawLine(ext.tono(lerp(teja, C.YESO, 0.3f), lejos), Offset(c.x + 1f, arriba - alto), Offset(c.x + c.ancho - 1f, arriba - alto), 0.5f)
        // El alero: el filo de las tejas, las cabezas de los canes y su sombra en la fachada.
        drawRect(ext.tono(lerp(teja, C.VIGA, 0.45f), lejos), Offset(c.x - 0.8f, arriba - 0.2f), Size(c.ancho + 1.6f, 0.6f))
        drawRect(C.SOMBRA.copy(alpha = 0.36f * r.dia + 0.1f), Offset(c.x, arriba + 0.4f), Size(c.ancho, if (cerca) 1.5f else 0.9f), blendMode = BlendMode.Multiply)
        // Las ventanas, con su postigo; de noche, muchas con luz hasta las once, y alguna que vela.
        val az = Azar(Semilla.de("renacimiento", "ventanas", c.x.toInt()))
        val deNoche = ext.noche > 0.5f; val velada = m.hora >= 17.0 && m.hora < 23.0
        val hueco = ext.tono(lerp(C.CARBON, C.VIGA, 0.3f), lejos)
        // Algunas casas de cerca tienen arriba una logia abierta (la altana), con sus pilares y su ropa a secar.
        val logia = cerca && c.alto > 10.4f && c.muro % 2 == 1
        if (logia) {
            drawRect(hueco.copy(alpha = 0.86f), Offset(c.x + 0.8f, arriba + 1.6f), Size(c.ancho - 1.6f, 3.4f))
            var x = c.x + 0.8f; while (x < c.x + c.ancho - 0.6f) { drawRect(ext.cara(muro, luz * 0.6f, lejos), Offset(x, arriba + 1.6f), Size(0.8f, 3.4f)); x += 2.7f }
            drawRect(ext.cara(lerp(muro, C.PIEDRA, 0.3f), luz * 0.3f, lejos), Offset(c.x + 0.4f, arriba + 4.6f), Size(c.ancho - 0.8f, 0.7f))
            if (!deNoche) drawRect(ext.cara(C.YESO, luz * 0.5f, lejos), Offset(c.x + c.ancho * 0.36f, arriba + 2.2f), Size(2.2f, 2.2f))
        }
        val baja = if (logia) 4.8f else 0f
        for (k in 0 until c.ventanas) {
            val x = c.x + c.ancho * (k + 0.5f) / c.ventanas - 0.7f; val y = arriba + 2.2f + baja + az.entre(0f, 1.2f); val w = if (cerca) 1.8f else 1.1f; val h = if (cerca) 2.8f else 1.6f
            val suerte = az.real()
            val conLuz = deNoche && suerte < (if (R.sanJuan(m)) 0.8f else if (velada) 0.55f else 0.1f)
            if (cerca) drawRect(ext.cara(lerp(muro, C.PIEDRA, 0.4f), luz * 0.3f, lejos), Offset(x - 0.5f, y - 0.5f), Size(w + 1f, h + 1.1f))
            drawRect(if (conLuz) lerp(C.LAMPARA, C.OCRE, 0.15f) else hueco.copy(alpha = 0.85f), Offset(x, y), Size(w, h))
            if (cerca && !conLuz && k % 2 == 0) drawRect(ext.tono(lerp(C.VERDE, C.VIGA, 0.5f), lejos), Offset(x, y), Size(w * 0.5f, h))
            if (conLuz && lejos < 0.4f) mancha(Offset(x + w / 2f, y + h / 2f), 3.4f, 3.4f, C.LAMPARA, 0.34f)
            if (cerca && c.alto > 11f && !logia) { val conLuz2 = deNoche && velada && suerte > 0.7f
                drawRect(if (conLuz2) lerp(C.LAMPARA, C.OCRE, 0.15f) else hueco.copy(alpha = 0.8f), Offset(x, y + 5f), Size(w, 2.2f)); if (conLuz2) mancha(Offset(x + w / 2f, y + 6f), 3f, 3f, C.LAMPARA, 0.3f) }
        }
        // Una chimenea, con su sombra tendida en el tejado.
        if (lejos < 0.4f && c.muro % 2 == 0) { val x = c.x + c.ancho * 0.7f
            drawRect(ext.cara(lerp(C.MURO, C.COTTO, 0.4f), luz * 0.5f, lejos), Offset(x, arriba - alto - 2.2f), Size(1.5f, 2.6f)); drawRect(ext.tono(teja, lejos), Offset(x - 0.4f, arriba - alto - 2.8f), Size(2.3f, 0.7f))
            drawLine(C.SOMBRA.copy(alpha = 0.3f * r.dia), Offset(x + 0.7f, arriba - alto + 0.4f), Offset(x + 0.7f - luz * 2.4f, arriba - alto * 0.3f), 1f) }
    }

    /** La catedral: la nave, las tribunas, el tambor, la cúpula con sus nervios, la linterna y el campanario. */
    private fun DrawScope.catedral(m: Momento, ext: Luces, r: Rayo, luz: Float, nieve: Boolean) {
        val lejos = 0.34f; val marmol = lerp(C.YESO, C.MURO, 0.2f); val teja = if (nieve) lerp(C.TEJA, C.YESO, 0.85f) else C.TEJA
        val piedra = lerp(C.OCRE, C.PIEDRA, 0.6f)
        fun cara(c: Color, lado: Float) = ext.cara(c, lado, lejos)
        // La nave, del campanario al tambor: el muro de mármol con sus fajas verdes y su tejado.
        drawRect(cara(marmol, luz * 0.4f), Offset(CAMPANARIO + 3f, 108f), Size(CUPULA - 22f - CAMPANARIO, 16f))
        for (y in listOf(111f, 114.6f, 118f)) drawLine(ext.tono(C.VERDE, lejos).copy(alpha = 0.55f), Offset(CAMPANARIO + 3f, y), Offset(CUPULA - 20f, y), 0.4f)
        drawPath(poligono(Offset(CAMPANARIO + 2f, 108f), Offset(CUPULA - 19f, 108f), Offset(CUPULA - 19f, 104f), Offset(CAMPANARIO + 5f, 104f)), cara(teja, 0.3f * r.dia))
        // Las tribunas, con su media cúpula de teja cada una.
        for (dx in listOf(-25f, 25f)) {
            val x = CUPULA + dx
            drawRect(cara(marmol, if (dx * luz > 0f) 0.7f else -0.8f), Offset(x - 8f, 110f), Size(16f, 14f))
            for (y in listOf(113f, 117f)) drawLine(ext.tono(C.VERDE, lejos).copy(alpha = 0.5f), Offset(x - 8f, y), Offset(x + 8f, y), 0.4f)
            drawPath(Path().apply { moveTo(x - 8.6f, 110f); cubicTo(x - 8f, 103f, x + 8f, 103f, x + 8.6f, 110f); close() }, cara(teja, if (dx * luz > 0f) 0.5f else -0.6f))
        }
        drawRect(cara(marmol, luz * 0.3f), Offset(CUPULA - 18f, 108f), Size(36f, 16f))
        // El tambor: tres caras a la vista, de piedra desnuda, cada una con su óculo.
        for ((x0, x1, lado) in listOf(Triple(-21f, -9f, -1f), Triple(-9f, 9f, 0f), Triple(9f, 21f, 1f))) {
            drawRect(cara(piedra, if (lado == 0f) 0.2f else lado * luz * 0.9f), Offset(CUPULA + x0, 97f), Size(x1 - x0, 11.4f))
            val o = Offset(CUPULA + (x0 + x1) / 2f, 102.4f); val rx = if (lado == 0f) 2.6f else 1.7f
            drawOval(ext.tono(lerp(C.CARBON, C.VIGA, 0.3f), lejos), Offset(o.x - rx, o.y - 2.6f), Size(rx * 2f, 5.2f)); drawOval(ext.tono(marmol, lejos), Offset(o.x - rx, o.y - 2.6f), Size(rx * 2f, 5.2f), style = Stroke(0.5f))
        }
        drawRect(ext.tono(marmol, lejos), Offset(CUPULA - 22f, 96f), Size(44f, 1.5f)); drawRect(ext.tono(lerp(piedra, C.VIGA, 0.3f), lejos), Offset(CUPULA - 21.6f, 107.6f), Size(43.2f, 0.9f))
        // La cúpula, apuntada: tres gajos a la vista entre cuatro nervios.
        val alto = 30f; val base = 96f; val punta = base - alto
        fun borde(k: Float): Path = Path().apply { moveTo(CUPULA + 21f * k, base); cubicTo(CUPULA + 21f * k, base - alto * 0.52f, CUPULA + 11f * k, punta + alto * 0.14f, CUPULA + 3f * k, punta) }
        val casco = Path().apply { moveTo(CUPULA - 21f, base); cubicTo(CUPULA - 21f, base - alto * 0.52f, CUPULA - 11f, punta + alto * 0.14f, CUPULA - 3f, punta); lineTo(CUPULA + 3f, punta); cubicTo(CUPULA + 11f, punta + alto * 0.14f, CUPULA + 21f, base - alto * 0.52f, CUPULA + 21f, base); close() }
        drawPath(casco, cara(teja, 0.1f))
        clipPath(casco) {
            // El gajo del lado del sol, más claro; el otro, en sombra violeta.
            drawRect(cara(teja, 0.8f * r.dia + 0.1f), Offset(if (luz > 0f) CUPULA + 7f else CUPULA - 22f, punta), Size(15f, alto)); drawRect(cara(teja, -1f), Offset(if (luz > 0f) CUPULA - 22f else CUPULA + 7f, punta), Size(15f, alto))
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.16f), startY = punta, endY = base), Offset(CUPULA - 22f, punta), Size(44f, alto), blendMode = BlendMode.Multiply)
            // Las hiladas de teja, que siguen la curva.
            if (!nieve) for (k in 1..9) { val y = base - k * 3f; drawLine(ext.tono(lerp(C.TEJA, C.VIGA, 0.5f), lejos).copy(alpha = 0.3f), Offset(CUPULA - 22f, y + 0.8f), Offset(CUPULA + 22f, y + 0.8f), 0.3f) }
        }
        // Los nervios de mármol: los dos del contorno y los dos de dentro.
        val nervio = ext.tono(lerp(marmol, Color.White, 0.2f), lejos)
        for (k in listOf(-1f, 1f)) drawPath(borde(k), nervio, style = Stroke(1.5f, cap = StrokeCap.Round))
        for (k in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(CUPULA + 8f * k, base); cubicTo(CUPULA + 8f * k, base - alto * 0.5f, CUPULA + 5f * k, punta + alto * 0.2f, CUPULA + 1.4f * k, punta) }, nervio, style = Stroke(1.2f, cap = StrokeCap.Round))
        // La linterna: su anillo, el templete con sus contrafuertes, el cono y la bola dorada con la cruz.
        drawRect(ext.tono(marmol, lejos), Offset(CUPULA - 4.4f, punta - 1f), Size(8.8f, 1.6f))
        drawRect(cara(marmol, luz * 0.4f), Offset(CUPULA - 2.6f, punta - 7f), Size(5.2f, 6f)); drawRect(cara(marmol, -1f), Offset(if (luz > 0f) CUPULA - 2.6f else CUPULA + 0.8f, punta - 7f), Size(1.8f, 6f))
        drawRect(ext.tono(lerp(C.CARBON, C.VIGA, 0.3f), lejos).copy(alpha = 0.7f), Offset(CUPULA - 0.5f, punta - 6f), Size(1f, 3.6f))
        for (k in listOf(-1f, 1f)) drawPath(poligono(Offset(CUPULA + 2.6f * k, punta - 5f), Offset(CUPULA + 4.6f * k, punta - 1f), Offset(CUPULA + 2.6f * k, punta - 1f)), ext.tono(marmol, lejos))
        drawPath(poligono(Offset(CUPULA - 3f, punta - 7f), Offset(CUPULA + 3f, punta - 7f), Offset(CUPULA, punta - 11.4f)), cara(marmol, luz * 0.5f))
        drawCircle(ext.tono(C.ORO, 0.15f), 1.25f, BOLA); drawLine(ext.tono(C.ORO, 0.15f), BOLA + Offset(0f, -1.2f), BOLA + Offset(0f, -3.4f), 0.5f); drawLine(ext.tono(C.ORO, 0.15f), BOLA + Offset(-0.9f, -2.5f), BOLA + Offset(0.9f, -2.5f), 0.5f)
        if (r.dia > 0.3f) drawCircle(Color.White.copy(alpha = 0.7f * r.dia), 0.45f, BOLA + Offset(luz * 0.4f, -0.4f))
        // Tras el rayo, el andamio de la linterna: palos, tablones, cruces de cuerda y una lona, que la abrazan entera.
        if (R.andamioEnLaLinterna(m)) { val palo = ext.tono(lerp(C.VIGA, C.MADERA, 0.3f), 0.16f); val arr = punta - 15.6f
            drawPath(poligono(Offset(CUPULA - 7f, punta - 5.4f), Offset(CUPULA - 0.6f, punta - 5.4f), Offset(CUPULA - 0.8f, punta + 0.6f), Offset(CUPULA - 7f, punta + 0.2f)), ext.cara(lerp(C.YESO, C.OCRE, 0.35f), luz * 0.4f, 0.16f))
            for (dx in listOf(-7.2f, -2.4f, 2.4f, 7.2f)) drawLine(palo, Offset(CUPULA + dx, punta + 1.6f), Offset(CUPULA + dx * 0.96f, arr), 0.75f)
            for ((k, y) in listOf(punta - 0.4f, punta - 5.4f, punta - 10.4f, arr + 0.6f).withIndex()) { drawLine(palo, Offset(CUPULA - 8.2f, y), Offset(CUPULA + 8.2f, y), if (k % 2 == 0) 1.1f else 0.75f)
                if (k < 3) { drawLine(palo.copy(alpha = 0.8f), Offset(CUPULA + 2.4f, y), Offset(CUPULA + 7.2f, y - 5f), 0.45f); drawLine(palo.copy(alpha = 0.8f), Offset(CUPULA + 7.2f, y), Offset(CUPULA + 2.4f, y - 5f), 0.45f) } }
            // La escalera de mano y la polea con su cuerda, que baja por el casco.
            drawLine(palo, Offset(CUPULA - 5.6f, punta - 5.4f), Offset(CUPULA - 3.6f, punta - 10.4f), 0.5f); drawLine(palo, Offset(CUPULA - 4.4f, punta - 5.4f), Offset(CUPULA - 2.4f, punta - 10.4f), 0.5f)
            drawLine(palo, Offset(CUPULA + 7.2f, arr + 0.6f), Offset(CUPULA + 11f, arr - 0.6f), 0.7f); drawLine(ext.tono(C.CARBON, 0.16f).copy(alpha = 0.7f), Offset(CUPULA + 10.6f, arr - 0.4f), Offset(CUPULA + 13f, punta + 9f), 0.35f) }
        // La nieve cuaja en los nervios y en el cono.
        if (nieve) { drawPath(casco, ext.tono(C.YESO, lejos).copy(alpha = 0.3f)); drawPath(poligono(Offset(CUPULA - 3f, punta - 7f), Offset(CUPULA + 3f, punta - 7f), Offset(CUPULA, punta - 11.4f)), ext.tono(C.YESO, lejos)) }
        // El campanario: cuadrado, con sus fajas de mármol, sus ventanas partidas y el remate plano.
        val x0 = CAMPANARIO - 4.4f; val arriba = 74f
        drawRect(cara(marmol, luz * 0.5f), Offset(x0, arriba), Size(8.8f, H - arriba)); drawRect(cara(marmol, -1f), Offset(if (luz > 0f) x0 else x0 + 5.6f, arriba), Size(3.2f, H - arriba))
        for ((k, y) in listOf(84.6f, 93f, 101f, 109f, 116f).withIndex()) drawLine(ext.tono(if (k % 2 == 0) C.VERDE else lerp(C.CINABRIO, C.YESO, 0.5f), lejos).copy(alpha = 0.7f), Offset(x0, y), Offset(x0 + 8.8f, y), 0.6f)
        val hueco = ext.tono(lerp(C.CARBON, C.VIGA, 0.3f), lejos).copy(alpha = 0.8f)
        drawRect(hueco, Offset(CAMPANARIO - 2.4f, 76.6f), Size(1.4f, 6.4f)); drawRect(hueco, Offset(CAMPANARIO - 0.7f, 76.6f), Size(1.4f, 6.4f)); drawRect(hueco, Offset(CAMPANARIO + 1f, 76.6f), Size(1.4f, 6.4f))
        for (y in listOf(87f, 95.6f)) for (dx in listOf(-1.7f, 0.5f)) drawRect(hueco, Offset(CAMPANARIO + dx, y), Size(1.2f, 4f))
        drawRect(ext.tono(lerp(marmol, C.PIEDRA, 0.2f), lejos), Offset(x0 - 1.1f, arriba - 1.6f), Size(11f, 2f)); drawRect(C.SOMBRA.copy(alpha = 0.3f * r.dia), Offset(x0, arriba + 0.4f), Size(8.8f, 0.8f), blendMode = BlendMode.Multiply)
        if (nieve) drawRect(ext.tono(C.YESO, lejos), Offset(x0 - 1.1f, arriba - 2.2f), Size(11f, 0.9f))
        // De noche, la luna deja un filo frío en el gajo que mira hacia ella.
        if (ext.noche > 0.6f && ext.nublado < 0.5f) { val k = if (ext.lunaX > 0.5f) 1f else -1f
            clipPath(casco) { drawRect(Brush.horizontalGradient(0f to C.LUNA.copy(alpha = 0.3f * ext.noche), 1f to Color.Transparent, startX = CUPULA + 21f * k, endX = CUPULA + 3f * k), Offset(CUPULA - 22f, punta), Size(44f, alto)) }
            drawPath(borde(k), C.LUNA.copy(alpha = 0.7f * ext.noche), style = Stroke(0.9f, cap = StrokeCap.Round))
            drawRect(C.LUNA.copy(alpha = 0.4f * ext.noche), Offset(if (k > 0f) x0 + 8f else x0, arriba), Size(0.8f, H - arriba - 16f)) }
        // Y las luces de la ciudad la alumbran desde abajo: un resplandor cálido en el tambor y en las tribunas.
        if (ext.noche > 0.5f && m.hora >= 17.0 && m.hora < 23.0) mancha(Offset(CUPULA, 112f), 36f, 9f, C.LAMPARA, 0.2f)
    }

    /** Las otras torres: la del palacio, con sus almenas voladas, y la de una iglesia a la derecha. */
    private fun DrawScope.torres(m: Momento, ext: Luces, r: Rayo, luz: Float, nieve: Boolean) {
        val lejos = 0.4f; val parda = lerp(C.OCRE, C.PIEDRA, 0.5f)
        fun cara(c: Color, lado: Float) = ext.cara(c, lado, lejos)
        // La torre del palacio.
        val x = 147f
        drawRect(cara(parda, luz * 0.5f), Offset(x - 2.6f, 92f), Size(5.2f, 32f)); drawRect(cara(parda, -1f), Offset(if (luz > 0f) x - 2.6f else x + 0.6f, 92f), Size(2f, 32f))
        drawRect(cara(parda, luz * 0.4f), Offset(x - 4.2f, 87.4f), Size(8.4f, 5f)); for (k in 0..3) drawRect(cara(parda, luz * 0.4f), Offset(x - 4.2f + k * 2.3f, 86f), Size(1.3f, 1.6f))
        drawRect(C.SOMBRA.copy(alpha = 0.3f * r.dia + 0.06f), Offset(x - 2.6f, 92.4f), Size(5.2f, 1.2f), blendMode = BlendMode.Multiply)
        drawRect(cara(parda, luz * 0.5f), Offset(x - 1.6f, 80.6f), Size(3.2f, 6f)); drawRect(ext.tono(lerp(C.CARBON, C.VIGA, 0.3f), lejos).copy(alpha = 0.7f), Offset(x - 0.6f, 82f), Size(1.2f, 2.6f))
        drawPath(poligono(Offset(x - 2f, 80.6f), Offset(x + 2f, 80.6f), Offset(x, 77.4f)), ext.tono(if (nieve) C.YESO else lerp(C.TEJA, C.VIGA, 0.3f), lejos))
        // El palacio al pie, con su almenado.
        drawRect(cara(parda, luz * 0.4f), Offset(x - 9f, 108f), Size(15f, 16f)); for (k in 0..5) drawRect(cara(parda, luz * 0.4f), Offset(x - 9f + k * 2.6f, 106.8f), Size(1.5f, 1.4f))
        for (k in 0..3) drawRect(ext.tono(lerp(C.CARBON, C.VIGA, 0.3f), lejos).copy(alpha = 0.6f), Offset(x - 7.4f + k * 3.4f, 111f), Size(1f, 2f))
        // La iglesia de la derecha: su campanario con chapitel y la nave.
        val y = 246f
        drawRect(cara(lerp(C.MURO, C.COTTO, 0.2f), luz * 0.5f), Offset(y - 2.2f, 96f), Size(4.4f, 28f)); drawRect(cara(lerp(C.MURO, C.COTTO, 0.2f), -1f), Offset(if (luz > 0f) y - 2.2f else y + 0.6f, 96f), Size(1.6f, 28f))
        drawRect(ext.tono(lerp(C.CARBON, C.VIGA, 0.3f), lejos).copy(alpha = 0.7f), Offset(y - 0.8f, 98f), Size(1.6f, 3.4f))
        drawPath(poligono(Offset(y - 2.8f, 96f), Offset(y + 2.8f, 96f), Offset(y, 88.6f)), ext.tono(if (nieve) C.YESO else C.TEJA, lejos))
        drawRect(cara(C.MURO, luz * 0.3f), Offset(y + 2.2f, 110f), Size(16f, 14f)); drawPath(poligono(Offset(y + 1.6f, 110f), Offset(y + 19f, 110f), Offset(y + 10.4f, 105.4f)), cara(if (nieve) C.YESO else C.TEJA, 0.3f * r.dia))
        // En las fiestas, telas de colores en las torres; en luto, un paño negro; con el rey de Francia, pendones azules.
        val tela = when { R.guerra(m) -> C.ULTRAMAR; R.luto(m) -> Color(0xFF2A2630); R.sanJuan(m) || R.fiesta(m) -> C.CINABRIO; else -> null }
        if (tela != null) {
            drawRect(ext.tono(tela, 0.25f), Offset(x - 1.9f, 94f), Size(3.8f, 9f)); drawRect(ext.tono(if (R.luto(m) || R.guerra(m)) tela else C.YESO, 0.25f), Offset(x - 1.9f, 97f), Size(3.8f, 2.4f))
            drawLine(ext.tono(C.CARBON, 0.3f), Offset(CAMPANARIO, 72.4f), Offset(CAMPANARIO, 66f), 0.4f); drawPath(poligono(Offset(CAMPANARIO, 66f), Offset(CAMPANARIO + 5f, 67.4f), Offset(CAMPANARIO, 69f)), ext.tono(tela, 0.25f))
        }
    }

    /** El tejado de la casa de enfrente, justo bajo la ventana: las tejas, la cumbrera, una azotea de madera y las palomas. */
    private fun DrawScope.tejadoDeDelante(m: Momento, ext: Luces, r: Rayo, luz: Float, nieve: Boolean) {
        val teja = if (nieve) lerp(C.TEJA, C.YESO, 0.82f) else C.TEJA
        val faldon = poligono(listOf(Offset(120f, 152f), Offset(120f, 138f), Offset(168f, 135f), Offset(236f, 137f), Offset(280f, 141f), Offset(280f, 152f)))
        drawPath(faldon, ext.cara(teja, 0.36f * r.dia))
        clipPath(faldon) {
            // Las canales de teja, que bajan hacia quien mira, y las hiladas.
            var x = 122f; while (x < 280f) { drawLine(ext.tono(lerp(teja, C.VIGA, 0.55f)).copy(alpha = 0.55f), Offset(x + (x - 199f) * -0.08f, 134f), Offset(x + (x - 199f) * 0.06f, 152f), 0.6f); drawLine(ext.tono(lerp(teja, C.YESO, 0.35f)).copy(alpha = 0.4f), Offset(x + 1.3f + (x - 199f) * -0.08f, 134f), Offset(x + 1.3f + (x - 199f) * 0.06f, 152f), 0.4f); x += 3.4f }
            for (y in listOf(139.6f, 143.4f, 147.4f)) drawLine(ext.tono(lerp(teja, C.VIGA, 0.5f)).copy(alpha = 0.3f), Offset(120f, y), Offset(280f, y + 1f), 0.4f)
            // Tejas de otro tono, repuestas, y líquenes.
            val az = Azar(Semilla.de("renacimiento", "tejado"))
            repeat(14) { val o = Offset(az.entre(124f, 276f), az.entre(137f, 150f)); drawRect(ext.tono(if (nieve) C.YESO else if (it % 3 == 0) lerp(C.TEJA, C.OCRE, 0.5f) else lerp(C.TEJA, C.VIGA, 0.3f)).copy(alpha = 0.5f), o, Size(az.entre(2f, 3.4f), az.entre(1.6f, 3f))) }
            if (!nieve) repeat(6) { mancha(Offset(az.entre(124f, 276f), az.entre(138f, 150f)), az.entre(2f, 5f), az.entre(1f, 2.2f), ext.tono(lerp(C.VERDE, C.OCRE, 0.4f)), 0.3f) }
        }
        // La cumbrera, con su filo de luz.
        drawPath(Path().apply { moveTo(120f, 138f); lineTo(168f, 135f); lineTo(236f, 137f); lineTo(280f, 141f) }, ext.tono(lerp(teja, C.YESO, 0.3f)), style = Stroke(1.1f))
        // Una chimenea de ladrillo con su sombrerete, y su sombra en el tejado.
        val ch = Offset(156f, 136f)
        drawLine(C.SOMBRA.copy(alpha = 0.34f * r.dia), ch + Offset(1.6f, 1f), ch + Offset(1.6f - luz * 7f, 8f), 3f)
        drawRect(ext.cara(lerp(C.MURO, C.COTTO, 0.5f), luz * 0.6f), ch + Offset(0f, -8f), Size(3.6f, 9.4f)); drawRect(ext.cara(lerp(C.MURO, C.COTTO, 0.5f), -1f), ch + Offset(if (luz > 0f) 0f else 2.2f, -8f), Size(1.4f, 9.4f))
        drawPath(poligono(ch + Offset(-1f, -8f), ch + Offset(4.6f, -8f), ch + Offset(1.8f, -10.6f)), ext.tono(teja))
        // La azotea de madera de la casa vecina, con una sábana tendida.
        val az0 = Offset(238f, 137.6f)
        for (dx in listOf(0f, 14f)) drawLine(ext.tono(C.VIGA), az0 + Offset(dx, 0f), az0 + Offset(dx, -9f), 0.8f)
        drawLine(ext.tono(C.VIGA), az0 + Offset(-1f, -9f), az0 + Offset(15f, -9f), 1f); drawLine(ext.tono(lerp(C.VIGA, C.CARBON, 0.3f)), az0 + Offset(0f, -4f), az0 + Offset(14f, -4f), 0.5f)
        if (m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES || m.ficha.clima == Clima.VIENTO) {
            drawPath(poligono(az0 + Offset(2f, -8.4f), az0 + Offset(8f, -8.4f), az0 + Offset(8.4f, -3f), az0 + Offset(2.6f, -2.4f)), ext.cara(C.YESO, luz * 0.5f))
            drawPath(poligono(az0 + Offset(9.4f, -8.4f), az0 + Offset(12.6f, -8.4f), az0 + Offset(12.4f, -4.6f), az0 + Offset(9.6f, -4.4f)), ext.cara(lerp(C.ULTRAMAR, C.YESO, 0.5f), luz * 0.5f))
        }
        // Las palomas del tejado: de día, posadas en la cumbrera.
        if (ext.noche < 0.5f && !R.campanas(m)) { val az = m.azar("palomas"); repeat(2 + az.entero(3)) { val x = az.entre(176f, 226f); val y = 136f + (x - 168f) * 0.03f
            drawOval(ext.tono(lerp(C.PIEDRA, C.CARBON, 0.3f)), Offset(x - 1.5f, y - 2.2f), Size(3f, 2f)); drawCircle(ext.tono(lerp(C.PIEDRA, C.CARBON, 0.3f)), 0.7f, Offset(x + 1.3f, y - 2.6f)) } }
        if (nieve) drawPath(Path().apply { moveTo(120f, 138f); lineTo(168f, 135f); lineTo(236f, 137f); lineTo(280f, 141f) }, ext.tono(C.YESO), style = Stroke(1.6f))
    }

    /** Lo que hoy pasa en la ciudad y se ve desde aquí. */
    private fun DrawScope.delDia(m: Momento, ext: Luces, r: Rayo) {
        // Las palomas que levantan el vuelo cuando tocan todas las campanas.
        if (R.campanas(m) && ext.noche < 0.6f) { val az = m.azar("vuelo"); val puntos = ArrayList<Offset>()
            repeat(22) { val o = Offset(az.entre(150f, 240f), az.entre(64f, 100f)); puntos += o; puntos += o + Offset(1.4f, -0.8f); puntos += o + Offset(-1.4f, -0.8f) }
            drawPoints(puntos, PointMode.Points, ext.tono(lerp(C.CARBON, C.PIEDRA, 0.3f)).copy(alpha = 0.8f), 0.9f, StrokeCap.Round) }
        // El carro de Pascua: por la mañana, el humo de los cohetes sube de la plaza de la catedral.
        if (R.carroDeFuego(m) && m.hora >= 8.0 && m.hora < 14.0) for (k in 0..6) mancha(PLAZA + Offset(k * 1.4f - 3f, -k * 6.4f), 3.4f + k * 1.5f, 3.6f + k * 1.2f, ext.tono(C.YESO, 0.2f), 0.5f - 0.05f * k)
        // Hasta las once, la ciudad vela: su resplandor sobre los tejados y las antorchas de las torres.
        if (ext.noche > 0.5f && m.hora >= 17.0 && m.hora < 23.0) {
            mancha(Offset(198f, 122f), 66f, 8f, C.LAMPARA, 0.22f); mancha(Offset(198f, 128f), 60f, 5f, Color(0xFFFF9A4A), 0.12f)
            for (p in ANTORCHAS) { mancha(p, 4.4f, 4.4f, C.LAMPARA, 0.5f); drawCircle(Color(0xFFFFE2A8), 0.7f, p) }
        }
        // La noche de San Juan: hogueras en las plazas y luminarias en lo alto de las torres.
        if (R.sanJuan(m) && ext.noche > 0.5f) {
            for (p in listOf(Offset(147f, 80f), Offset(CAMPANARIO, 73f), Offset(246f, 88f), BOLA + Offset(0f, 6f))) { mancha(p, 5f, 5f, C.LAMPARA, 0.5f); drawCircle(Color(0xFFFFE2A8), 0.8f, p) }
            for (p in listOf(PLAZA, Offset(160f, 126f), Offset(228f, 127f))) mancha(p, 9f, 5f, Color(0xFFFF9A4A), 0.36f)
        }
        // Con el rey de Francia dentro, el polvo de los caballos sobre las calles.
        if (R.guerra(m) && ext.noche < 0.6f) for (x in listOf(150f, 190f, 232f)) mancha(Offset(x, 123f), 14f, 4f, ext.tono(lerp(C.MURO, C.OCRE, 0.4f), 0.3f), 0.4f)
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, ext: Luces, reloj: Float, caja: Rect) {
        tiempoVivo(ext, m.ficha.clima, reloj, H, m.semilla("tiempo"))
        // Las chispas del carro de fuego.
        if (R.carroDeFuego(m) && m.hora >= 8.0 && m.hora < 14.0) {
            val az = Azar(Semilla.de("renacimiento", "carro", (reloj * 0.8f).toInt())); val k = (reloj * 0.8f) % 1f
            repeat(2) { val c = PLAZA + Offset(az.entre(-6f, 8f), -az.entre(6f, 22f)); val col = listOf(C.ORO, C.CINABRIO, C.YESO)[az.entero(3)]
                for (j in 0 until 8) { val t = 6.2832f * j / 8f; drawCircle(lerp(col, Color.White, 0.4f).copy(alpha = 1f - k), 0.6f, c + Offset(cos(t), sin(t)) * (1f + 5f * k)) } }
        }
        // Las antorchas de las torres, que tiemblan.
        if (ext.noche > 0.5f && m.hora >= 17.0 && m.hora < 23.0) for ((i, p) in ANTORCHAS.withIndex()) drawCircle(Color(0xFFFFD890).copy(alpha = 0.45f + 0.35f * sin(reloj * 6f + i * 1.9f)), 0.9f, p)
        // Las hogueras de San Juan, que laten.
        if (R.sanJuan(m) && ext.noche > 0.5f) for ((i, p) in listOf(PLAZA, Offset(160f, 126f), Offset(228f, 127f)).withIndex()) drawCircle(Color(0xFFFFC070).copy(alpha = 0.5f + 0.4f * sin(reloj * 5f + i * 2f)), 1.1f, p)
        // Con todas las campanas, las palomas dan vueltas sobre los tejados.
        if (R.campanas(m) && ext.noche < 0.6f) { val puntos = ArrayList<Offset>()
            for (j in 0 until 9) { val t = reloj * 0.5f + j * 0.7f; val o = Offset(196f + 40f * cos(t) + j * 2f, 84f + 9f * sin(t * 1.3f) + j); puntos += o; puntos += o + Offset(1.4f, -0.8f * sin(reloj * 9f + j)); puntos += o + Offset(-1.4f, -0.8f * sin(reloj * 9f + j)) }
            drawPoints(puntos, PointMode.Points, ext.tono(lerp(C.CARBON, C.PIEDRA, 0.3f)).copy(alpha = 0.85f), 0.9f, StrokeCap.Round) }
    }
}
