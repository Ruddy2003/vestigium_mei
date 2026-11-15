package io.github.ruddy2003.vestigium.ui.logros

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.TrazosFrente
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Frente exterior
// =====================================================================

/** El gris translúcido de lo que aún no se gana: la misma silueta tenue que la figura sin ganar. */
private fun siluetaFrente(oscuro: Boolean): Color = if (oscuro) Color(0xFFE4E4E4).copy(alpha = 0.13f) else Color(0xFF1E1E1E).copy(alpha = 0.13f)

/** Un rectángulo dado en fracciones del lienzo. */
private fun DrawScope.fracR(x0: Float, y0: Float, x1: Float, y1: Float) = Rect(size.width * x0, size.height * y0, size.width * x1, size.height * y1)

/** Un punto dado en fracciones del lienzo. */
private fun DrawScope.fracP(x: Float, y: Float) = Offset(size.width * x, size.height * y)

/** Un polígono cerrado con sus vértices en fracciones del lienzo: x0, y0, x1, y1… */
private fun DrawScope.fracPoli(vararg f: Float): Path = Path().apply {
    var k = 0
    while (k + 1 < f.size) { val x = size.width * f[k]; val y = size.height * f[k + 1]; if (k == 0) moveTo(x, y) else lineTo(x, y); k += 2 }
    close()
}

/** La sombra al pie de la consola, de [x0] a [x1] (fracciones del ancho): dos óvalos bajos, el de adentro más hondo. */
private fun DrawScope.sombraConsola(x0: Float, x1: Float, oscuro: Boolean, y: Float = 0.955f) {
    val w = size.width; val h = size.height
    drawOval(Color.Black.copy(alpha = if (oscuro) 0.32f else 0.15f), Offset(w * x0, h * (y - 0.012f)), Size(w * (x1 - x0), h * 0.03f))
    drawOval(Color.Black.copy(alpha = if (oscuro) 0.22f else 0.1f), Offset(w * (x0 + 0.05f), h * (y - 0.006f)), Size(w * (x1 - x0 - 0.1f), h * 0.016f))
}

/**
 * La geometría de la sala: el lienzo ([w] × [h], en píxeles, con [u] píxeles por dp) se parte en [n] franjas de ancho [paso],
 * una por columna de trofeos, con el trofeo al centro de la suya ([cx]). Las torres de la sala van en los bordes de las
 * franjas ([borde]), con medio ancho [pil], que siempre queda libre entre dos pedestales; arriba, la viga de alto [friso];
 * abajo, el piso desde [suelo]. Lo que se repite por filas usa [modulo] (≈ 230 dp, el alto de una fila del hall).
 */
private class GeoSala(val w: Float, val h: Float, val u: Float, columnas: Int) {
    val n = columnas.coerceIn(1, 6)
    val paso = w / n
    val pil = minOf(paso * 0.12f, 15f * u)
    val friso = minOf(14f * u, h * 0.05f)
    val piso = minOf(16f * u, h * 0.06f)
    val suelo = h - piso
    val modulo = maxOf(230f * u, 1f)
    fun cx(i: Int) = (i + 0.5f) * paso
    fun borde(i: Int) = i * paso
    /** El vano de la franja [i] entre sus dos torres. */
    fun vano(i: Int) = Rect(borde(i) + pil, friso, borde(i + 1) - pil, suelo)
}

/** Repite [f] de [y0] a [y1] con [paso] (con su mínimo de un píxel y a lo más [tope] veces). */
private inline fun repetirSala(y0: Float, y1: Float, paso: Float, tope: Int = 40, f: (k: Int, y: Float) -> Unit) {
    var y = y0; var k = 0
    val p = maxOf(paso, 1f)
    while (y < y1 && k < tope) { f(k, y); y += p; k++ }
}

/** La sombra del marcador en el suelo, bajo [p]: se achica y se aclara cuando brinca ([salto], de 0 a 1). */
private fun DrawScope.sombraBaliza(p: Offset, ancho: Float, salto: Float, oscuro: Boolean) {
    val u = density
    val f = 1f - 0.35f * salto.coerceIn(0f, 1f)
    val a = ancho * f; val b = 3.2f * u * f
    drawOval(Color.Black.copy(alpha = (if (oscuro) 0.5f else 0.2f) * f), Offset(p.x - a / 2, p.y - b / 2), Size(a, b))
    drawOval(Color.Black.copy(alpha = (if (oscuro) 0.3f else 0.12f) * f), Offset(p.x - a * 0.3f, p.y - b * 0.3f), Size(a * 0.6f, b * 0.6f))
}

/**
 * El pie de la escena de victoria: la meta [m] subida [sobre] dp y corrida lo necesario para que una escena de [ancho] ×
 * [alto] (centrada en x, hacia arriba desde el pie) quepa en el lienzo con 2 dp de margen.
 */
private fun DrawScope.pieVictoriaFrente(m: Offset, ancho: Float, alto: Float, sobre: Float = 2f): Offset {
    val u = density; val mg = 2f * u
    val x = if (size.width > ancho + 2 * mg) m.x.coerceIn(ancho / 2 + mg, size.width - ancho / 2 - mg) else size.width / 2
    val y = if (size.height > alto + 2 * mg) (m.y - sobre * u).coerceIn(alto + mg, size.height - mg) else size.height - mg
    return Offset(x, y)
}

/** El grosor de un trazo de los objetos de Tu año: [f] veces el radio [r], pero nunca menos de 0.8 dp. */
private fun DrawScope.grosorFrente(r: Float, f: Float): Float = maxOf(r * f, 0.8f * density)

/** Una creciente: el círculo de centro [c] y radio [r] menos otro de radio r · [k] corrido ([dx], [dy]). */
private fun crecienteFrente(c: Offset, r: Float, dx: Float, dy: Float, k: Float = 0.8f): Path =
    Path.combine(PathOperation.Difference, Path().apply { addOval(Rect(c, r)) }, Path().apply { addOval(Rect(Offset(c.x + dx, c.y + dy), r * k)) })

/**
 * Frente exterior: los logros de la red, en la base escondida en ruinas. Los trofeos son parches de misión bordados e
 * insignias de la red; cada uno descansa en un tramo de consola: el tablero de verde casi negro con su fila de botones
 * cuadrados y sus pilotos, sobre el frente blanco y curvo de molduras redondas, con la pantallita de radar y la insignia de
 * la red en el metal del nivel; al pie, el zócalo con su franja de peligro. En el hall va sobre un módulo de la misma
 * consola, y su placa es la cinta de nombre bordada, como la de la chaqueta de vuelo.
 */
object LogrosFrente : DisenoLogros {
    private val BAQUELITA = TrazosFrente.BAQUELITA
    private val HUESO = TrazosFrente.HUESO
    private val HUESO_LUZ = Color(0xFFF6F5EF)
    private val HUESO_SOMBRA = Color(0xFFB4B7AC)
    private val CROMO = TrazosFrente.CROMO
    private val CROMO_LUZ = Color(0xFFE6E4DE)
    private val CROMO_SOMBRA = Color(0xFF7D7A73)
    private val PANTALLA = TrazosFrente.PANTALLA
    private val TRAZO = TrazosFrente.RADAR
    private val ROJO = TrazosFrente.PILOTO_ROJO
    private val AMBAR = TrazosFrente.PILOTO_AMBAR
    /** La consola oscura, de verde casi negro, y su canto donde le da la luz. */
    private val CONSOLA = TrazosFrente.CONSOLA_OSCURA
    private val CONSOLA_LUZ = Color(0xFF34453F)
    /** La penumbra verdosa de la base con las luces bajas. */
    private val PENUMBRA = Color(0xFF0E1614)
    /** La piedra de las ruinas, sus raíces, su musgo, el cobre de los tubos y el hormigón de la losa y del piso. */
    private val PIEDRA = TrazosFrente.PIEDRA
    private val PIEDRA_SOMBRA = TrazosFrente.PIEDRA_SOMBRA
    private val RAIZ = TrazosFrente.RAIZ
    private val MUSGO = TrazosFrente.MUSGO
    private val COBRE = TrazosFrente.COBRE
    private val LOSA = Color(0xFFA3A69B)
    /** El vidrio grabado: el trazo iluminado por el canto y el vidrio mismo (en penumbra y a la luz). */
    private val VIDRIO = TrazosFrente.VIDRIO
    private val VIDRIO_LUZ = TrazosFrente.VIDRIO_LUZ
    private val VIDRIO_TRAZO = TrazosFrente.VIDRIO_TRAZO
    private val PELIGRO = TrazosFrente.PELIGRO
    /** La luz cálida de las lámparas de cúpula. */
    private val LUZ = Color(0xFFFFF6E4)
    /** El mármol del Directorio y su bronce: solo para lo que es del régimen. */
    private val MARMOL = TrazosFrente.MARMOL
    private val BRONCE = TrazosFrente.BRONCE
    /** La sarga verde petróleo de la cinta de nombre bordada y el hilo hueso de sus letras. */
    private val SARGA = Color(0xFF283633)
    private val HILO = Color(0xFFEFE8D8)
    private val PAPEL = Color(0xFFF2ECDC)
    private val CARTON = Color(0xFFB59D72)

    /** El color en oscuro: la misma base con las luces bajas, hundida en su penumbra verdosa. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, PENUMBRA, 0.5f) else c

    /** El tono de la baliza y de la escena de la meta: de noche, apagado un poco, para que se lean sobre el mapa. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, PANTALLA, 0.2f) else c

    /** Un piloto de la consola: el aro de cromo, el vidrio de [luz] (apagado, hundido hacia la baquelita) y, encendido, su halo. */
    private fun DrawScope.piloto(c: Offset, r: Float, luz: Color, encendido: Boolean, oscuro: Boolean) {
        if (encendido) {
            drawCircle(luz.copy(alpha = if (oscuro) 0.2f else 0.14f), r * 2.6f, c)
            drawCircle(luz.copy(alpha = if (oscuro) 0.34f else 0.24f), r * 1.75f, c)
        }
        drawCircle(t(CROMO, oscuro), r * 1.38f, c)
        drawCircle(t(CROMO_SOMBRA, oscuro), r * 1.38f, c, style = Stroke(maxOf(r * 0.2f, 0.3f * density)))
        drawCircle(if (encendido) luz else mezcla(luz, BAQUELITA, if (oscuro) 0.74f else 0.62f), r, c)
        drawCircle(Color.White.copy(alpha = if (encendido) 0.75f else 0.22f), r * 0.32f, Offset(c.x - r * 0.36f, c.y - r * 0.36f))
    }

    /** Una perilla de baquelita: la falda de cromo, la tapa negra con su brillo y la raya índice hacia [angulo] (grados). */
    private fun DrawScope.perilla(c: Offset, r: Float, angulo: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), r, c + Offset(r * 0.12f, r * 0.18f))
        drawCircle(t(CROMO, oscuro), r, c)
        drawCircle(t(CROMO_SOMBRA, oscuro), r, c, style = Stroke(maxOf(r * 0.14f, 0.3f * density)))
        val tapa = r * 0.74f
        drawCircle(t(BAQUELITA, oscuro), tapa, c)
        drawCircle(Color.White.copy(alpha = 0.14f), tapa * 0.5f, Offset(c.x - tapa * 0.3f, c.y - tapa * 0.32f))
        val a = Math.toRadians(angulo.toDouble()).toFloat()
        drawLine(HUESO_LUZ, c + Offset(cos(a), sin(a)) * (tapa * 0.2f), c + Offset(cos(a), sin(a)) * (tapa * 0.92f), maxOf(r * 0.16f, 0.4f * density), StrokeCap.Round)
    }

    /**
     * Una pantalla redonda de radar en [c]: el aro de cromo, el vidrio verde oscuro, la retícula, el barrido en [angulo] con su
     * estela y, si [eco], un contacto recién barrido. De noche, con su resplandor.
     */
    private fun DrawScope.pantallaRadar(c: Offset, r: Float, angulo: Float, oscuro: Boolean, eco: Boolean, grosor: Float) {
        if (oscuro) drawCircle(Brush.radialGradient(listOf(TRAZO.copy(alpha = 0.22f), Color.Transparent), c, r * 2.2f), r * 2.2f, c)
        drawCircle(t(CROMO, oscuro), r * 1.22f, c)
        drawCircle(t(CROMO_SOMBRA, oscuro), r * 1.22f, c, style = Stroke(grosor))
        drawCircle(PANTALLA, r, c)
        drawCircle(TRAZO.copy(alpha = 0.35f), r * 0.55f, c, style = Stroke(grosor * 0.7f))
        drawLine(TRAZO.copy(alpha = 0.3f), Offset(c.x - r, c.y), Offset(c.x + r, c.y), grosor * 0.6f)
        drawLine(TRAZO.copy(alpha = 0.3f), Offset(c.x, c.y - r), Offset(c.x, c.y + r), grosor * 0.6f)
        for (k in 0 until 5) drawArc(TRAZO.copy(alpha = 0.32f - k * 0.055f), angulo - (k + 1) * 10f, 10f, true, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r))
        val a = Math.toRadians(angulo.toDouble()).toFloat()
        drawLine(TRAZO, c, Offset(c.x + r * cos(a), c.y + r * sin(a)), grosor * 0.8f, StrokeCap.Round)
        if (eco) {
            val ae = a - 0.6f
            drawCircle(TRAZO, maxOf(r * 0.1f, 0.4f * density), Offset(c.x + r * 0.6f * cos(ae), c.y + r * 0.6f * sin(ae)))
        }
    }

    /**
     * Una chapa en el metal del nivel: la base, el filo de luz arriba, la sombra abajo y su contorno oscuro; la legendaria
     * ([EstadoPedestal.nivel] 4) lleva además su faceta blanca en diagonal.
     */
    private fun DrawScope.chapa(r: Rect, e: EstadoPedestal, radio: Float) {
        if (r.width < 1f || r.height < 1f) return
        val u = density
        val rr = radio.coerceAtMost(minOf(r.width, r.height) / 2)
        drawRoundRect(e.base, r.topLeft, r.size, CornerRadius(rr))
        val filo = (r.height * 0.22f).coerceIn(minOf(0.5f * u, r.height * 0.4f), r.height * 0.4f)
        drawRect(e.brillo, Offset(r.left + rr * 0.6f, r.top + filo * 0.25f), Size(r.width - rr * 1.2f, filo))
        drawRect(e.metalOscuro.copy(alpha = 0.5f), Offset(r.left + rr * 0.6f, r.bottom - filo), Size(r.width - rr * 1.2f, filo * 0.8f))
        drawRoundRect(e.metalOscuro, r.topLeft, r.size, CornerRadius(rr), style = Stroke(maxOf(minOf(r.width, r.height) * 0.08f, 0.45f * u)))
        if (e.nivel >= 4) drawLine(Color.White.copy(alpha = 0.85f), Offset(r.left + r.width * 0.16f, r.bottom - r.height * 0.18f),
            Offset(r.left + r.width * 0.26f, r.top + r.height * 0.18f), maxOf(r.height * 0.08f, 0.5f * u), StrokeCap.Round)
    }

    /**
     * El frente curvo de una consola en [r]: el canto de arriba recto y las esquinas de abajo en curva grande ([curva], en
     * fracción del ancho), como la consola que se dobla hacia el piso.
     */
    private fun frenteCurvo(r: Rect, curva: Float): Path = Path().apply {
        val cw = r.width * curva; val ch = minOf(r.height * 0.55f, cw * 1.4f)
        moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - ch)
        cubicTo(r.right, r.bottom - ch * 0.3f, r.right - cw * 0.35f, r.bottom, r.right - cw, r.bottom)
        lineTo(r.left + cw, r.bottom)
        cubicTo(r.left + cw * 0.35f, r.bottom, r.left, r.bottom - ch * 0.3f, r.left, r.bottom - ch)
        close()
    }

    /** El hueso de un frente de consola: la luz que baja de arriba, la cara de la izquierda iluminada y la de la derecha en sombra. */
    private fun DrawScope.luzConsola(p: Path, r: Rect, oscuro: Boolean) {
        clipPath(p) {
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.06f else 0.38f), Color.Transparent,
                Color.Black.copy(alpha = if (oscuro) 0.3f else 0.16f)), r.top, r.bottom), r.topLeft, r.size)
            drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.22f), r.topLeft, Size(r.width * 0.05f, r.height))
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(r.right - r.width * 0.07f, r.top), Size(r.width * 0.07f, r.height))
        }
    }

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * Una fila de [n] botones cuadrados de consola centrada en [c], cada [paso]: cada uno en su bisel negro, unos encendidos
     * (ámbar, verde del vidrio, hueso o rojo) y otros apagados, según [semilla]. De noche, los encendidos echan su halo.
     */
    private fun DrawScope.botones(c: Offset, n: Int, lado: Float, paso: Float, semilla: Int, oscuro: Boolean) {
        val colores = listOf(AMBAR, VIDRIO_TRAZO, HUESO_LUZ, AMBAR, ROJO, VIDRIO_TRAZO)
        for (k in 0 until n) {
            val col = colores[Math.floorMod(semilla * 3 + k * 5, colores.size)]
            with(TrazosFrente) {
                botonCuadrado(Offset(c.x + (k - (n - 1) / 2f) * paso, c.y), lado, if (oscuro) col else mezcla(col, Color.White, 0.12f), azar(semilla, k + 60) > 0.3f,
                    CONSOLA, if (oscuro) 1f else 0f)
            }
        }
    }

    /**
     * Las molduras redondas de la consola blanca, dentro de [forma]: [cuantas] bandas abombadas de [alto] desde [y0] hacia
     * abajo, cada una con su luz arriba, su sombra abajo y el surco entre una y otra, como el mostrador anillado de la base.
     */
    private fun DrawScope.molduras(forma: Path, r: Rect, y0: Float, alto: Float, cuantas: Int, oscuro: Boolean) {
        val u = density
        clipPath(forma) {
            for (k in 0 until cuantas) {
                val y = y0 + k * alto
                drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.08f else 0.45f), Color.Transparent,
                    Color.Black.copy(alpha = if (oscuro) 0.3f else 0.16f)), y, y + alto), Offset(r.left, y), Size(r.width, alto))
                drawLine(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.24f), Offset(r.left, y + alto), Offset(r.right, y + alto), maxOf(alto * 0.12f, 0.3f * u))
            }
        }
    }

    /**
     * Un tramo de consola de la base. Arriba, el tablero de verde casi negro, inclinado, con su fila de botones cuadrados
     * entre el piloto verde de «en línea» y el ámbar; el canto redondo de la consola blanca; abajo, su frente curvo, con la
     * ranura grabada, la pantallita de radar con su barrido a la izquierda, la perilla y dos pilotos a la derecha, al centro
     * la insignia de la red en el metal del nivel y, al pie, dos molduras redondas; luego el zócalo oscuro con su franja de
     * peligro.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val tapa = fracPoli(0.14f, 0.765f, 0.86f, 0.765f, 0.9f, 0.8f, 0.1f, 0.8f)
        val rf = fracR(0.1f, 0.8f, 0.9f, 0.935f)
        val frente = frenteCurvo(rf, 0.1f)
        val zocalo = fracR(0.2f, 0.93f, 0.8f, 0.955f)
        if (!e.hecho) { drawPath(Path().apply { addPath(tapa); addPath(frente); addRect(zocalo) }, siluetaFrente(oscuro)); return }
        sombraConsola(0.1f, 0.9f, oscuro)
        // El zócalo oscuro, metido bajo la curva, con su franja de peligro.
        drawRect(t(CONSOLA, oscuro), zocalo.topLeft, zocalo.size)
        if (fino) with(TrazosFrente) {
            franjaPeligro(Rect(zocalo.left + zocalo.width * 0.08f, zocalo.top + zocalo.height * 0.4f, zocalo.right - zocalo.width * 0.08f, zocalo.bottom - zocalo.height * 0.15f),
                t(PELIGRO, oscuro), t(BAQUELITA, oscuro), maxOf(h * 0.016f, 1.2f * u))
        }
        drawRect(Color.White.copy(alpha = 0.08f), zocalo.topLeft, Size(zocalo.width, maxOf(h * 0.003f, 0.4f * u)))
        // El frente blanco y curvo.
        drawPath(frente, t(HUESO, oscuro))
        luzConsola(frente, rf, oscuro)
        if (fino) molduras(frente, rf, rf.top + rf.height * 0.72f, rf.height * 0.13f, 2, oscuro)
        drawPath(frente, t(HUESO_SOMBRA, oscuro), style = Stroke(lw))
        if (fino) {
            // La ranura grabada bajo el canto.
            drawLine(Color.Black.copy(alpha = 0.25f), fracP(0.14f, 0.82f), fracP(0.86f, 0.82f), lw * 0.9f)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.5f), fracP(0.14f, 0.82f) + Offset(0f, lw), fracP(0.86f, 0.82f) + Offset(0f, lw), lw * 0.7f)
            // La pantallita de radar de la izquierda.
            pantallaRadar(fracP(0.27f, 0.861f), h * 0.026f, -50f - 120f * azar(e.semilla, 3), oscuro, muyFino, lw)
            // La perilla y los dos pilotos de la derecha.
            perilla(fracP(0.71f, 0.862f), h * 0.018f, 210f + 100f * azar(e.semilla, 4), oscuro)
            piloto(fracP(0.81f, 0.848f), h * 0.0068f, AMBAR, true, oscuro)
            piloto(fracP(0.81f, 0.877f), h * 0.0068f, ROJO, false, oscuro)
        }
        // La insignia de la red, en el metal del nivel, al centro del frente.
        val c = fracP(0.5f, 0.862f); val r = h * 0.031f
        drawCircle(Color.Black.copy(alpha = 0.2f), r, c + Offset(r * 0.12f, r * 0.16f))
        drawCircle(e.base, r, c)
        drawCircle(e.brillo, r * 0.8f, c, style = Stroke(maxOf(r * 0.1f, 0.4f * u)))
        drawCircle(e.metalOscuro, r, c, style = Stroke(maxOf(r * 0.12f, 0.45f * u)))
        if (fino) with(TrazosFrente) { emblemaRed(c, r * 0.48f, e.metalOscuro, maxOf(r * 0.1f, 0.5f * u)) }
        else drawCircle(e.metalOscuro, r * 0.3f, c)
        if (e.nivel >= 4 && fino) drawLine(Color.White.copy(alpha = 0.85f), Offset(c.x - r * 0.62f, c.y + r * 0.1f), Offset(c.x - r * 0.18f, c.y - r * 0.6f),
            maxOf(r * 0.12f, 0.5f * u), StrokeCap.Round)
        // El tablero oscuro de arriba, con el filo de luz de su canto, y el canto redondo de la consola blanca.
        drawPath(tapa, Brush.verticalGradient(listOf(t(CONSOLA_LUZ, oscuro), t(CONSOLA, oscuro)), h * 0.765f, h * 0.8f))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.22f), fracP(0.145f, 0.767f), fracP(0.855f, 0.767f), lw * 0.8f)
        drawPath(tapa, Color.Black.copy(alpha = 0.5f), style = Stroke(lw * 0.7f))
        val canto = fracR(0.095f, 0.795f, 0.905f, 0.81f)
        drawRoundRect(Brush.verticalGradient(listOf(t(HUESO_LUZ, oscuro), t(HUESO, oscuro), t(HUESO_SOMBRA, oscuro)), canto.top, canto.bottom), canto.topLeft, canto.size, CornerRadius(canto.height / 2))
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), canto.topLeft, canto.size, CornerRadius(canto.height / 2), style = Stroke(lw * 0.6f))
        if (fino) {
            // La fila de botones cuadrados, entre los dos pilotos.
            val cuantos = if (muyFino) 9 else 6
            val paso = w * 0.5f / cuantos
            botones(fracP(0.5f, 0.782f), cuantos, minOf(paso * 0.62f, h * 0.012f), paso, e.semilla, oscuro)
            piloto(fracP(0.19f, 0.782f), h * 0.005f, VIDRIO_TRAZO, true, oscuro)
            piloto(fracP(0.81f, 0.782f), h * 0.005f, AMBAR, azar(e.semilla, 30) > 0.3f, oscuro)
        }
    }

    /**
     * El módulo de consola del hall: el tablero oscuro con su hilera de botones cuadrados, el canto redondo de la consola
     * blanca y su frente curvo, con la pantallita de radar, la chapa del nivel con el emblema de la red al centro, los dos
     * pilotos de la derecha y las molduras redondas al pie.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val tapa = fracPoli(0.08f, 0f, 0.92f, 0f, 0.97f, 0.32f, 0.03f, 0.32f)
        val rf = fracR(0.03f, 0.34f, 0.97f, 1f)
        val frente = frenteCurvo(rf, 0.08f)
        if (!e.hecho) { drawPath(Path().apply { addPath(tapa); addPath(frente) }, siluetaFrente(oscuro)); return }
        // El frente blanco y curvo, con su luz y sus molduras.
        drawPath(frente, t(HUESO, oscuro))
        luzConsola(frente, rf, oscuro)
        molduras(frente, rf, rf.top + rf.height * 0.74f, rf.height * 0.13f, 2, oscuro)
        drawPath(frente, t(HUESO_SOMBRA, oscuro), style = Stroke(lw * 0.6f))
        // La pantallita de radar.
        pantallaRadar(fracP(0.17f, 0.62f), h * 0.17f, -60f - 90f * azar(e.semilla, 5), oscuro, false, lw * 0.6f)
        // La chapa del nivel, con el emblema.
        val pl = fracR(0.37f, 0.44f, 0.63f, 0.8f)
        chapa(pl, e, h * 0.08f)
        with(TrazosFrente) { emblemaRed(pl.center, pl.height * 0.27f, e.metalOscuro, maxOf(lw * 0.6f, 0.4f * u)) }
        // Los pilotos de la derecha.
        piloto(fracP(0.74f, 0.58f), h * 0.065f, AMBAR, true, oscuro)
        piloto(fracP(0.84f, 0.58f), h * 0.065f, ROJO, oscuro, oscuro)
        // El tablero oscuro, con el filo de luz de su canto, y la hilera de botones cuadrados.
        drawPath(tapa, Brush.verticalGradient(listOf(t(CONSOLA_LUZ, oscuro), t(CONSOLA, oscuro)), 0f, h * 0.32f))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.22f), fracP(0.09f, 0.03f), fracP(0.91f, 0.03f), lw * 0.6f)
        drawPath(tapa, Color.Black.copy(alpha = 0.5f), style = Stroke(lw * 0.4f))
        val paso = w * 0.07f
        botones(fracP(0.53f, 0.165f), 8, minOf(paso * 0.6f, h * 0.12f), paso, e.semilla + 7, oscuro)
        piloto(fracP(0.15f, 0.165f), h * 0.045f, VIDRIO_TRAZO, true, oscuro)
        // El canto redondo de la consola blanca, entre el tablero y el frente.
        val canto = fracR(0.025f, 0.3f, 0.975f, 0.39f)
        drawRoundRect(Brush.verticalGradient(listOf(t(HUESO_LUZ, oscuro), t(HUESO, oscuro), t(HUESO_SOMBRA, oscuro)), canto.top, canto.bottom), canto.topLeft, canto.size, CornerRadius(canto.height / 2))
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), canto.topLeft, canto.size, CornerRadius(canto.height / 2), style = Stroke(lw * 0.3f))
    }

    override val margenPlaca: Dp = 10.dp
    override fun tintaPlaca(oscuro: Boolean): Color = HILO
    override fun rotuloPlaca(oscuro: Boolean): Color = AMBAR

    /**
     * La cinta de nombre bordada, como la de la chaqueta de vuelo: la sarga petróleo con su trama en diagonal, el borde
     * sobrehilado en el hilo del nivel (las puntadas apretadas y ladeadas, con el brillo del hilo) y el pespunte que la cose,
     * por dentro.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val radio = minOf(6 * u, h / 2, w / 2)
        if (!e.hecho) { drawRoundRect(siluetaFrente(oscuro), Offset.Zero, size, CornerRadius(radio)); return }
        val forma = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(radio))) }
        drawPath(forma, if (oscuro) mezcla(SARGA, PENUMBRA, 0.2f) else SARGA)
        // La trama de la sarga, en diagonal.
        clipPath(forma) {
            val trama = Path()
            var x = -h; var k = 0
            while (x < w && k < 500) { trama.moveTo(x, h); trama.lineTo(x + h, 0f); x += maxOf(2.2f * u, 1f); k++ }
            drawPath(trama, Color.White.copy(alpha = 0.05f), style = Stroke(0.6f * u))
        }
        // El borde sobrehilado: la cinta de hilo del nivel…
        val b = minOf(2.8f * u, h * 0.2f)
        drawPath(Path().apply { addRoundRect(RoundRect(Rect(b / 2, b / 2, w - b / 2, h - b / 2), CornerRadius(maxOf(radio - b / 2, 0.5f * u)))) }, e.base, style = Stroke(b))
        // …con sus puntadas ladeadas por los cuatro cantos rectos…
        val puntadas = Path()
        val paso = maxOf(1.3f * u, 1f); val lado = 0.9f * u; val m = 0.3f * u
        var x = radio; var j = 0
        while (x < w - radio && j < 600) {
            puntadas.moveTo(x, m); puntadas.lineTo(x + lado, b - m)
            puntadas.moveTo(x, h - b + m); puntadas.lineTo(x + lado, h - m)
            x += paso; j++
        }
        var y = radio; j = 0
        while (y < h - radio && j < 300) {
            puntadas.moveTo(m, y); puntadas.lineTo(b - m, y + lado)
            puntadas.moveTo(w - b + m, y); puntadas.lineTo(w - m, y + lado)
            y += paso; j++
        }
        drawPath(puntadas, e.metalOscuro.copy(alpha = 0.55f), style = Stroke(0.5f * u))
        // …y el brillo del hilo, arriba.
        drawLine(e.brillo.copy(alpha = 0.8f), Offset(radio, b * 0.35f), Offset(w - radio, b * 0.35f), 0.5f * u)
        // El pespunte de adentro.
        val p = b + 1.2f * u
        if (w > 2 * p && h > 2 * p) drawPath(Path().apply { addRoundRect(RoundRect(Rect(p, p, w - p, h - p), CornerRadius(maxOf(radio - p, 0.5f * u)))) },
            e.base.copy(alpha = 0.55f), style = Stroke(0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.2f * u))))
    }

    // ---------------------------------------------------------------- el salón del hall

    /**
     * La sala de mando de la red, escondida en unas ruinas antiguas. El muro es de sillares viejos, con manchas de humedad y
     * musgo en las juntas; arriba, la losa de hormigón con sus manchas, los dos tubos de cobre que corren bajo ella sujetos con
     * abrazaderas y las raíces que cuelgan por encima de todo. En el fondo de cada vano se levanta un panel de vidrio grabado,
     * iluminado por el canto, con sus trazos rectos, sus curvas y sus órbitas en verde turquesa, bajo su riel y sobre su pie
     * oscuro con la rejilla de aire. Entre los vanos, las torres de consola de verde casi negro, de remate curvo: arriba, su
     * pantalla redonda de radar; luego, por cada tramo, la lámpara de cúpula blanca en su ménsula, la botonera de botones
     * cuadrados, la ventanilla de las dos bobinas de cinta, los pilotos y la rejilla. Abajo, el piso de hormigón con la franja
     * de peligro al pie del muro y los cables gruesos tirados de una torre a otra. De día, las lámparas alumbran la piedra; de
     * noche es la misma base en penumbra verdosa, con el vidrio, los pilotos y las lámparas encendidos.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoSala(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaSala(g, e, oscuro) }
    }

    private fun DrawScope.escenaSala(g: GeoSala, e: EstadoSalon, oscuro: Boolean) {
        val w = g.w; val h = g.h; val u = g.u
        /** El color [c] mezclado con el fondo: [k] = 0 es el fondo, 1 el color tal cual. */
        fun m(c: Color, k: Float) = mezcla(e.fondo, c, k)

        // El muro de sillares, tenue sobre el fondo, y la sombra que la losa echa sobre él.
        with(TrazosFrente) {
            muroSillares(Rect(0f, g.friso, w, g.suelo), 17f * u, m(PIEDRA, if (oscuro) 0.3f else 0.4f), m(PIEDRA_SOMBRA, if (oscuro) 0.45f else 0.36f),
                (if (oscuro) Color(0xFF04100A) else Color(0xFF34402F)).copy(alpha = if (oscuro) 0.55f else 0.26f), m(MUSGO, if (oscuro) 0.42f else 0.45f),
                e.semilla + 3, if (oscuro) 0.25f else 0.6f)
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.16f), Color.Transparent), g.friso, g.friso + 34f * u),
            Offset(0f, g.friso), Size(w, 34f * u))

        // El panel de vidrio grabado de cada vano, por tramo, con el estilo de trazos alternado.
        for (i in 0 until g.n) {
            val v = g.vano(i)
            repetirSala(v.top + 12f * u, v.bottom - 80f * u, g.modulo, 10) { k, y0 ->
                val pw = minOf(v.width * 0.84f, 136f * u); val ph = minOf(g.modulo * 0.54f, 124f * u, v.bottom - 16f * u - y0)
                if (ph > 50f * u) panelVidrio(Rect(g.cx(i) - pw / 2, y0, g.cx(i) + pw / 2, y0 + ph), e.fondo, oscuro, e.semilla + i * 7 + k * 3, (i + 2 * k) % 3)
            }
        }

        // Las torres de consola entre los vanos (y medias en los bordes).
        for (i in 0..g.n) torreSala(g, i, e, oscuro)

        // La losa del techo, con sus manchas de humedad y su canto.
        drawRect(Brush.verticalGradient(listOf(m(mezcla(LOSA, Color.Black, 0.2f), if (oscuro) 0.4f else 0.6f), m(LOSA, if (oscuro) 0.42f else 0.62f)), 0f, g.friso),
            Offset.Zero, Size(w, g.friso))
        for (k in 0 until (w / (26f * u)).toInt().coerceIn(3, 60)) {
            val c = Offset(w * azar(e.semilla + 11, k), g.friso * (0.15f + 0.7f * azar(e.semilla + 12, k)))
            val mw = (5f + 14f * azar(e.semilla + 13, k)) * u
            drawOval(m(Color(0xFF3F4A3A), if (oscuro) 0.3f else 0.4f).copy(alpha = 0.5f), Offset(c.x - mw / 2, c.y - 1.4f * u), Size(mw, (1.4f + 2f * azar(e.semilla + 14, k)) * u))
        }
        drawLine(LUZ.copy(alpha = if (oscuro) 0.05f else 0.3f), Offset(0f, g.friso - 1.2f * u), Offset(w, g.friso - 1.2f * u), 0.6f * u)
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.6f else 0.3f), Offset(0f, g.friso), Offset(w, g.friso), 0.8f * u)
        // Los dos tubos de cobre que corren bajo la losa, con sus abrazaderas en cada torre.
        val cobre = m(COBRE, if (oscuro) 0.5f else 0.75f)
        val ys = listOf(g.friso + 2.6f * u, g.friso + 7.2f * u)
        ys.forEach { y -> with(TrazosFrente) { tubo(Offset(-2f * u, y), Offset(w + 2f * u, y), 3.6f * u, cobre, if (oscuro) 0.2f else 1f, juntas = 52f * u) } }
        for (i in 0..g.n) {
            val x = g.borde(i)
            val a = Rect(x - 1.2f * u, ys.first() - 3.2f * u, x + 1.2f * u, ys.last() + 3.2f * u)
            drawRect(Color.Black.copy(alpha = 0.3f), a.topLeft + Offset(0.4f * u, 0.6f * u), a.size)
            drawRect(m(CONSOLA, if (oscuro) 0.75f else 0.7f), a.topLeft, a.size)
            drawLine(LUZ.copy(alpha = if (oscuro) 0.08f else 0.35f), Offset(a.left + 0.35f * u, a.top + 0.4f * u), Offset(a.left + 0.35f * u, a.bottom - 0.4f * u), 0.4f * u)
        }
        // El piloto rojo de «en el aire», en la losa, al centro: encendido de noche, en la ventana de transmisión.
        val aire = Rect(w / 2 - 8f * u, g.friso * 0.12f, w / 2 + 8f * u, g.friso * 0.12f + minOf(7f * u, g.friso * 0.45f))
        drawRoundRect(m(BAQUELITA, if (oscuro) 0.85f else 0.7f), aire.topLeft, aire.size, CornerRadius(1.6f * u))
        val lente = aire.deflate(1.3f * u)
        if (oscuro) drawCircle(ROJO.copy(alpha = 0.18f), 12f * u, aire.center)
        drawRoundRect(if (oscuro) ROJO else m(ROJO, 0.4f), lente.topLeft, lente.size, CornerRadius(1f * u))
        for (j in 1 until 4) drawLine(Color.Black.copy(alpha = 0.25f), Offset(lente.left + lente.width * j / 4f, lente.top), Offset(lente.left + lente.width * j / 4f, lente.bottom), 0.5f * u)

        // Las raíces que cuelgan de la losa: largas por encima de las torres y cortas en las orillas de los vanos, nunca por
        // el centro, donde va el trofeo.
        val raiz = if (oscuro) mezcla(RAIZ, PENUMBRA, 0.3f) else m(RAIZ, 0.85f)
        val brilloRaiz = if (oscuro) VIDRIO_TRAZO.copy(alpha = 0.16f) else Color(0xFFC9BC94).copy(alpha = 0.3f)
        val sombraRaiz = if (oscuro) 0.4f else 0.18f
        // Con muchas columnas las torres son angostas: las raíces, más finas y más cortas.
        val escala = (g.pil / (15f * u)).coerceIn(0.5f, 1f)
        with(TrazosFrente) {
            for (i in 0..g.n) {
                val x = g.borde(i); val s = e.semilla * 5 + i * 17
                raiz(Offset(x - g.pil * 0.45f, g.friso - 1f * u), (60f + 90f * azar(s, 1)) * escala * u, 2.2f * escala * u, mezcla(raiz, Color.Black, 0.12f), brilloRaiz, s + 1, sombra = sombraRaiz)
                raiz(Offset(x + g.pil * 0.3f, g.friso - 1f * u), (90f + 110f * azar(s, 2)) * escala * u, 2.6f * escala * u, raiz, brilloRaiz, s + 2, sombra = sombraRaiz)
                if (g.n <= 3 && azar(s, 3) > 0.4f) raiz(Offset(x + g.pil * 0.85f, g.friso), (24f + 30f * azar(s, 4)) * u, 1.3f * u, raiz, brilloRaiz, s + 3, ramas = false, sombra = sombraRaiz)
            }
            // Más abajo, alguna raíz que sale de una grieta entre los sillares, junto a las torres.
            for (i in 0..g.n) repetirSala(g.friso + g.modulo * 0.8f, g.suelo - 90f * u, g.modulo, 10) { k, y ->
                val s = e.semilla * 3 + i * 41 + k * 7
                if (azar(s, 1) > 0.45f) {
                    val lado = if (azar(s, 2) > 0.5f) 1f else -1f
                    val o = Offset(g.borde(i) + lado * (g.pil + (2f + 4f * azar(s, 3)) * u), y + 30f * u * azar(s, 4))
                    drawOval(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.28f), Offset(o.x - 3f * u, o.y - 1.4f * u), Size(6f * u, 2.8f * u))
                    raiz(o, (36f + 50f * azar(s, 5)) * u, 1.5f * u, raiz, brilloRaiz, s + 9, sombra = sombraRaiz)
                }
            }
            if (g.n <= 3) for (i in 0 until g.n) {
                val v = g.vano(i); val s = e.semilla * 7 + i * 29
                raiz(Offset(v.left + (5f + 6f * azar(s, 1)) * u, g.friso), (22f + 26f * azar(s, 2)) * u, 1.4f * u, raiz, brilloRaiz, s + 5, sombra = sombraRaiz)
                raiz(Offset(v.right - (5f + 6f * azar(s, 3)) * u, g.friso), (16f + 22f * azar(s, 4)) * u, 1.2f * u, raiz, brilloRaiz, s + 6, ramas = false, sombra = sombraRaiz)
            }
        }

        // El piso de hormigón, la franja de peligro al pie del muro y los cables gruesos tirados de torre a torre.
        drawRect(Brush.verticalGradient(listOf(m(LOSA, if (oscuro) 0.3f else 0.5f), m(mezcla(LOSA, Color.Black, 0.2f), if (oscuro) 0.3f else 0.5f)), g.suelo, h),
            Offset(0f, g.suelo), Size(w, h - g.suelo))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), Offset(0f, g.suelo), Size(w, 1f * u))
        val franja = Rect(0f, g.suelo + 1f * u, w, g.suelo + minOf(4.2f * u, (h - g.suelo) * 0.4f))
        with(TrazosFrente) { franjaPeligro(franja, m(PELIGRO, if (oscuro) 0.55f else 0.8f), m(BAQUELITA, if (oscuro) 0.8f else 0.75f), 6f * u) }
        val cables = Path()
        for (i in 0 until g.n) {
            val x0 = g.borde(i) + g.pil * 0.5f; val x1 = g.borde(i + 1) - g.pil * 0.5f
            val y = g.suelo + (h - g.suelo) * 0.7f
            cables.moveTo(x0, g.suelo - 2f * u)
            cables.cubicTo(x0 + 4f * u, y, x0 + (x1 - x0) * 0.3f, y + 2f * u, x0 + (x1 - x0) * 0.5f, y - 1f * u)
            cables.cubicTo(x0 + (x1 - x0) * 0.7f, y - 3f * u, x1 - 6f * u, y + 1f * u, x1, g.suelo - 2f * u)
        }
        with(TrazosFrente) { cableGrueso(cables, 2.2f * u, m(BAQUELITA, if (oscuro) 0.8f else 0.75f), if (oscuro) 0.3f else 1f, if (oscuro) 0.4f else 0.25f) }
    }

    /**
     * Un panel de vidrio grabado en [r], de pie en el vano: de noche, el resplandor verde que echa en la piedra; el vidrio,
     * de tono cercano al fondo, con los trazos iluminados por el canto ([estilo] de [TrazosFrente.trazosVidrio]), la luz que
     * sube del canto de abajo, los reflejos y el filo encendido; arriba, su riel oscuro y, abajo, su pie de consola con la
     * rejilla de aire.
     */
    private fun DrawScope.panelVidrio(r: Rect, fondo: Color, oscuro: Boolean, semilla: Int, estilo: Int) {
        val u = density
        fun m(c: Color, k: Float) = mezcla(fondo, c, k)
        if (oscuro) for (k in 0 until 4) {
            val e = (4 - k) * 6f * u
            drawRoundRect(VIDRIO_TRAZO.copy(alpha = 0.03f), Offset(r.left - e, r.top - e), Size(r.width + 2 * e, r.height + 2 * e), CornerRadius(e))
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.08f), r.topLeft + Offset(2.4f * u, 3f * u), r.size)
        drawRect(if (oscuro) m(VIDRIO, 0.62f) else m(VIDRIO_LUZ, 0.13f), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, VIDRIO_TRAZO.copy(alpha = if (oscuro) 0.18f else 0.16f)), r.bottom - r.height * 0.45f, r.bottom),
                Offset(r.left, r.bottom - r.height * 0.45f), Size(r.width, r.height * 0.45f))
            with(TrazosFrente) {
                trazosVidrio(r, if (oscuro) VIDRIO_TRAZO else mezcla(VIDRIO_LUZ, VIDRIO_TRAZO, 0.2f), 0.75f * u, if (oscuro) 0.46f else 0.36f, semilla, estilo)
            }
            listOf(0.1f to 0.05f, 0.2f to 0.025f).forEach { (f, a) ->
                drawLine(Color.White.copy(alpha = if (oscuro) a * 0.6f else a * 2.4f), Offset(r.left + r.width * f, r.bottom), Offset(r.left + r.width * (f + 0.34f), r.top), r.width * 0.05f)
            }
        }
        drawRect(VIDRIO_TRAZO.copy(alpha = if (oscuro) 0.5f else 0.45f), r.topLeft, r.size, style = Stroke(0.6f * u))
        drawLine(VIDRIO_TRAZO.copy(alpha = if (oscuro) 0.85f else 0.6f), Offset(r.left, r.bottom - 0.4f * u), Offset(r.right, r.bottom - 0.4f * u), 0.9f * u)
        // El riel de arriba, con su remate.
        val consola = m(CONSOLA, if (oscuro) 0.92f else 0.7f)
        val riel = Rect(r.left - 2f * u, r.top - 4.2f * u, r.right + 3f * u, r.top + 0.6f * u)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), riel.topLeft + Offset(0.6f * u, 1.2f * u), riel.size, CornerRadius(riel.height / 2))
        drawRoundRect(Brush.verticalGradient(listOf(m(CONSOLA_LUZ, if (oscuro) 0.92f else 0.75f), consola), riel.top, riel.bottom), riel.topLeft, riel.size, CornerRadius(riel.height / 2))
        drawLine(LUZ.copy(alpha = if (oscuro) 0.08f else 0.35f), Offset(riel.left + riel.height * 0.5f, riel.top + 0.8f * u), Offset(riel.right - riel.height * 0.5f, riel.top + 0.8f * u), 0.5f * u)
        drawCircle(m(CROMO, if (oscuro) 0.45f else 0.8f), riel.height * 0.32f, Offset(riel.right - riel.height * 0.55f, riel.center.y))
        // El pie de consola con su rejilla de aire.
        val pie = Rect(r.left - 3f * u, r.bottom, r.right + 3f * u, r.bottom + 10f * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.15f), Offset(pie.left + 2f * u, pie.bottom), Size(pie.width, 2.4f * u))
        drawRect(Brush.verticalGradient(listOf(m(CONSOLA_LUZ, if (oscuro) 0.92f else 0.75f), consola), pie.top, pie.bottom), pie.topLeft, pie.size)
        drawLine(LUZ.copy(alpha = if (oscuro) 0.06f else 0.3f), Offset(pie.left, pie.top + 0.5f * u), Offset(pie.right, pie.top + 0.5f * u), 0.5f * u)
        listOf(0.18f, 0.82f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.35f), Offset(pie.left + pie.width * f, pie.top + 1f * u), Offset(pie.left + pie.width * f, pie.bottom), 0.5f * u) }
        val rej = Rect(pie.center.x - pie.width * 0.16f, pie.top + pie.height * 0.3f, pie.center.x + pie.width * 0.16f, pie.bottom - pie.height * 0.25f)
        drawRect(m(CROMO, if (oscuro) 0.3f else 0.55f), rej.topLeft, rej.size)
        var x = rej.left + 1.2f * u
        while (x < rej.right - 0.8f * u) { drawLine(Color.Black.copy(alpha = 0.55f), Offset(x, rej.top + 0.7f * u), Offset(x, rej.bottom - 0.7f * u), 0.6f * u); x += 1.8f * u }
    }

    /**
     * La torre de consola [i] de la sala: el cuerpo de verde casi negro, de remate curvo, con su sombra en el muro, el filo de
     * luz de la izquierda y la mugre; arriba, la pantalla redonda de radar; por cada tramo, la lámpara de cúpula en su ménsula,
     * la botonera, la ventanilla de las bobinas, los pilotos y la rejilla.
     */
    private fun DrawScope.torreSala(g: GeoSala, i: Int, e: EstadoSalon, oscuro: Boolean) {
        val u = g.u
        fun m(c: Color, k: Float) = mezcla(e.fondo, c, k)
        val x = g.borde(i); val a = g.pil
        val arriba = g.friso + 2f * u
        val caja = Rect(x - a, arriba, x + a, g.suelo)
        val curva = minOf(a, 10f * u)
        val torre = Path().apply { addRoundRect(RoundRect(caja.left, caja.top, caja.right, caja.bottom, CornerRadius(curva), CornerRadius(curva), CornerRadius.Zero, CornerRadius.Zero)) }
        val k0 = if (oscuro) 0.9f else 0.66f
        val cuerpo = m(CONSOLA, k0)
        // Su sombra en el muro, a la derecha (la luz viene de arriba a la izquierda).
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f), Offset(caja.right, caja.top + 6f * u), Size(5f * u, caja.height))
        drawPath(torre, Brush.horizontalGradient(listOf(m(CONSOLA_LUZ, k0), cuerpo, m(mezcla(CONSOLA, Color.Black, 0.3f), k0)), caja.left, caja.right))
        clipPath(torre) {
            drawRect(LUZ.copy(alpha = if (oscuro) 0.05f else 0.22f), caja.topLeft, Size(1.2f * u, caja.height))
            for (j in 0 until 5) {
                val xm = caja.left + caja.width * azar(e.semilla + 60 + i, j)
                drawLine(LUZ.copy(alpha = if (oscuro) 0.02f else 0.05f), Offset(xm, caja.top + 20f * u), Offset(xm + 0.5f * u, caja.top + 20f * u + caja.height * azar(e.semilla + 61 + i, j) * 0.5f), (1f + 2f * azar(e.semilla + 62 + i, j)) * u, StrokeCap.Round)
            }
        }
        drawPath(torre, Color.Black.copy(alpha = if (oscuro) 0.6f else 0.35f), style = Stroke(0.6f * u))
        val cromo = m(CROMO, if (oscuro) 0.4f else 0.7f)
        val vidrio = m(PANTALLA, if (oscuro) 0.95f else 0.75f)
        val traza = TRAZO.copy(alpha = if (oscuro) 0.8f else 0.6f)
        // La pantalla de radar del remate (de noche, con su resplandor).
        val rr = minOf(a * 0.62f, 9f * u)
        val rc = Offset(x, arriba + curva * 0.5f + rr + 5f * u)
        if (oscuro) drawCircle(Brush.radialGradient(listOf(TRAZO.copy(alpha = 0.16f), Color.Transparent), rc, rr * 4.5f), rr * 4.5f, rc)
        drawCircle(cromo, rr * 1.25f, rc)
        drawCircle(vidrio, rr, rc)
        with(TrazosFrente) {
            reticula(rc, rr, traza.copy(alpha = traza.alpha * 0.5f), 0.4f * u, 2)
            barrido(rc, rr, 360f * azar(e.semilla + 40, i), traza, 0.6f * u)
        }
        // Los tramos de la torre.
        repetirSala(rc.y + rr + 16f * u, g.suelo - 60f * u, g.modulo, 10) { k, y0 ->
            // La lámpara de cúpula en su ménsula: alumbra la piedra de los dos lados.
            val rl = minOf(a * 0.62f, 6.5f * u)
            val boca = Offset(x, y0 + rl * 0.9f)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(x - a * 0.75f + 0.5f * u, boca.y + 0.8f * u), Size(a * 1.5f, 1.8f * u))
            drawRect(cromo, Offset(x - a * 0.75f, boca.y + 0.2f * u), Size(a * 1.5f, 1.6f * u))
            with(TrazosFrente) {
                lamparaCupula(boca, rl, 0f, m(TrazosFrente.PIZARRA, if (oscuro) 0.8f else 0.9f), if (oscuro) mezcla(HUESO_LUZ, PENUMBRA, 0.15f) else HUESO_LUZ, if (oscuro) 0.85f else 0.6f, LUZ)
            }
            // La botonera.
            val yb = boca.y + 9f * u
            val lado = minOf(a * 0.34f, 3.4f * u)
            for (fila in 0 until 2) botones(Offset(x, yb + fila * lado * 1.7f), 3, lado, lado * 1.65f, e.semilla + i * 11 + k * 5 + fila, oscuro)
            // La ventanilla de las dos bobinas de cinta.
            val rb = minOf(a * 0.56f, 7.5f * u)
            val vent = Rect(x - a + 3f * u, yb + lado * 3.2f, x + a - 3f * u, yb + lado * 3.2f + 4 * rb + 9f * u)
            drawRoundRect(Color.Black.copy(alpha = 0.5f), vent.inflate(0.6f * u).topLeft, vent.inflate(0.6f * u).size, CornerRadius(3.4f * u))
            drawRoundRect(vidrio, vent.topLeft, vent.size, CornerRadius(3f * u))
            listOf(0.25f + 0.5f * azar(e.semilla + i, k), 0.85f - 0.4f * azar(e.semilla + i, k)).forEachIndexed { j, lleno ->
                bobina(Offset(x, vent.top + 3f * u + rb + j * (2 * rb + 3f * u)), rb, 40f * azar(e.semilla + 77 + i, k + j), lleno,
                    m(CROMO_LUZ, if (oscuro) 0.4f else 0.75f), m(Color(0xFF6B4A2E), if (oscuro) 0.55f else 0.6f), vidrio)
            }
            drawArc(Color.White.copy(alpha = if (oscuro) 0.05f else 0.12f), 200f, 50f, false, vent.topLeft, Size(vent.width, vent.width), style = Stroke(1f * u))
            // Los pilotos, encendidos al azar (más vivos de noche).
            val yp = vent.bottom + 7f * u
            for (j in 0 until 3) {
                val p = Offset(x - a * 0.5f + j * a * 0.5f, yp)
                val col = if (j == 1) ROJO else if (j == 2) VIDRIO_TRAZO else AMBAR
                val prendido = azar(e.semilla + 90 + i, k * 3 + j) > 0.35f
                if (prendido && oscuro) drawCircle(col.copy(alpha = 0.22f), 3.6f * u, p)
                drawCircle(cromo, 1.8f * u, p)
                drawCircle(if (prendido) m(col, if (oscuro) 0.95f else 0.85f) else m(BAQUELITA, 0.6f), 1.25f * u, p)
            }
            // La rejilla de ventilación.
            for (j in 0 until 4) {
                val yr = yp + 7f * u + j * 3.2f * u
                drawRoundRect(Color.Black.copy(alpha = 0.45f), Offset(x - a * 0.55f, yr), Size(a * 1.1f, 1.5f * u), CornerRadius(0.75f * u))
                drawLine(LUZ.copy(alpha = if (oscuro) 0.04f else 0.14f), Offset(x - a * 0.5f, yr + 1.9f * u), Offset(x + a * 0.5f, yr + 1.9f * u), 0.4f * u)
            }
        }
    }

    /**
     * Una bobina de cinta en [c]: la cinta enrollada (más o menos llena, [lleno] de 0 a 1), las tres aspas de la brida giradas
     * [giro] grados, el aro y el cubo con su hueco.
     */
    private fun DrawScope.bobina(c: Offset, r: Float, giro: Float, lleno: Float, brida: Color, cinta: Color, hueco: Color) {
        val u = density
        drawCircle(cinta, r * (0.42f + 0.48f * lleno.coerceIn(0f, 1f)), c)
        for (k in 0 until 3) {
            val a = Math.toRadians((giro + k * 120f).toDouble()).toFloat()
            drawLine(brida, c, Offset(c.x + r * cos(a), c.y + r * sin(a)), maxOf(r * 0.2f, 0.8f * u), StrokeCap.Round)
        }
        drawCircle(brida, r, c, style = Stroke(maxOf(r * 0.1f, 0.6f * u)))
        drawCircle(brida, r * 0.3f, c)
        drawCircle(hueco, r * 0.12f, c)
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * La baliza de la célula, plantada en cada mundo: el trípode de cromo, la radio de maleta de baquelita con sus esquineras,
     * su asa, la rejilla del altavoz, el dial ámbar encendido y el piloto en el color de acento, y en su mástil la antena de
     * plato que apunta al cielo. Cuando brinca, transmite: las ondas salen de la antena.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        val x = e.punto.x
        sombraBaliza(e.punto, 15f * u, s, oscuro)
        val pie = e.punto.y - s * 3f * u
        val linea = if (oscuro) Color(0xFFE7E1D4).copy(alpha = 0.55f) else BAQUELITA
        // El trípode: la pata de atrás primero y luego las dos de adelante, con sus regatones.
        val cabeza = Offset(x, pie - 8.5f * u)
        drawLine(n(CROMO_SOMBRA, oscuro), cabeza, Offset(x + 1.4f * u, pie - 1.6f * u), 0.9f * u, StrokeCap.Round)
        listOf(-5.5f, 5.5f).forEach { dx ->
            val p = Offset(x + dx * u, pie)
            drawLine(linea.copy(alpha = 0.6f), cabeza, p, 1.5f * u, StrokeCap.Round)
            drawLine(n(CROMO, oscuro), cabeza, p, 0.9f * u, StrokeCap.Round)
            drawCircle(linea, 0.7f * u, p)
        }
        // La maleta: el cuerpo, las esquineras, el asa, la rejilla, el dial y el piloto.
        val m = Rect(x - 6.5f * u, pie - 16f * u, x + 6.5f * u, pie - 8.5f * u)
        drawArc(n(CROMO, oscuro), 180f, 180f, false, Offset(x - 2.6f * u, m.top - 2.2f * u), Size(5.2f * u, 4.4f * u), style = Stroke(0.8f * u))
        drawRoundRect(n(BAQUELITA, oscuro), m.topLeft, m.size, CornerRadius(1.4f * u))
        if (oscuro) drawRoundRect(linea, m.topLeft, m.size, CornerRadius(1.4f * u), style = Stroke(0.5f * u))
        drawLine(Color.White.copy(alpha = 0.18f), Offset(m.left + 1.4f * u, m.top + 0.6f * u), Offset(m.right - 1.4f * u, m.top + 0.6f * u), 0.5f * u)
        listOf(Offset(m.left, m.top), Offset(m.right - 1.6f * u, m.top), Offset(m.left, m.bottom - 1.6f * u), Offset(m.right - 1.6f * u, m.bottom - 1.6f * u)).forEach { o ->
            drawRect(n(CROMO, oscuro), o, Size(1.6f * u, 1.6f * u))
        }
        val altavoz = Offset(m.left + 3.4f * u, m.center.y + 0.4f * u)
        drawCircle(n(CROMO_SOMBRA, oscuro), 2.1f * u, altavoz)
        listOf(Offset(-0.7f, -0.7f), Offset(0.7f, -0.7f), Offset(-0.7f, 0.7f), Offset(0.7f, 0.7f)).forEach { d -> drawCircle(n(BAQUELITA, oscuro), 0.38f * u, altavoz + d * u) }
        val dial = Rect(m.left + 6.6f * u, m.top + 1.6f * u, m.right - 1.4f * u, m.top + 3.8f * u)
        if (oscuro) drawRect(AMBAR.copy(alpha = 0.3f), dial.inflate(0.5f * u).topLeft, dial.inflate(0.5f * u).size)
        drawRect(n(AMBAR, oscuro), dial.topLeft, dial.size)
        drawLine(n(BAQUELITA, oscuro), Offset(dial.left + dial.width * 0.62f, dial.top), Offset(dial.left + dial.width * 0.62f, dial.bottom), 0.4f * u)
        val luz = Offset(m.right - 2.4f * u, m.bottom - 2.3f * u)
        if (oscuro) { drawCircle(e.acento.copy(alpha = 0.14f), 5f * u, luz); drawCircle(e.acento.copy(alpha = 0.3f), 2.4f * u, luz) }
        drawCircle(e.acento, 0.95f * u, luz)
        // El mástil y la antena de plato, que mira al cielo hacia arriba a la izquierda.
        val tope = Offset(x + 2.6f * u, pie - 21f * u)
        drawLine(n(CROMO, oscuro), Offset(tope.x, m.top), tope, 0.8f * u)
        rotate(-35f, tope) {
            drawArc(n(CROMO, oscuro), 0f, 180f, true, Offset(tope.x - 5f * u, tope.y - 3f * u), Size(10f * u, 6f * u))
            drawArc(linea.copy(alpha = 0.7f), 0f, 180f, false, Offset(tope.x - 5f * u, tope.y - 3f * u), Size(10f * u, 6f * u), style = Stroke(0.5f * u))
            drawLine(n(CROMO_LUZ, oscuro), Offset(tope.x - 5f * u, tope.y), Offset(tope.x + 5f * u, tope.y), 0.6f * u)
            drawLine(n(CROMO_SOMBRA, oscuro), tope, Offset(tope.x, tope.y - 3.6f * u), 0.6f * u)
            drawCircle(n(CROMO_SOMBRA, oscuro), 0.7f * u, Offset(tope.x, tope.y - 3.8f * u))
        }
        // Las ondas de la transmisión, al brincar.
        if (s > 0.02f) {
            val a = Math.toRadians(35.0).toFloat()
            val antena = Offset(tope.x - 3.8f * u * sin(a), tope.y - 3.8f * u * cos(a))
            for (k in 1..3) {
                val rk = (1.5f + k * 2.2f + s * 2f) * u
                drawArc(e.acento.copy(alpha = (1f - s * 0.4f) * (0.95f - k * 0.22f)), 200f, 70f, false, Offset(antena.x - rk, antena.y - rk), Size(2 * rk, 2 * rk),
                    style = Stroke(0.8f * u, cap = StrokeCap.Round))
            }
        }
    }

    /**
     * La victoria de la red en Albar, la capital: el muro de mármol del Directorio, con sus sillares, sus vetas, su cornisa de
     * bronce con los dentículos y su anillo de bronce, y encima el emblema de la red pintado con aerosol en rojo, con su
     * rocío y sus chorreones; al pie, la plantilla de cartón con el emblema calado y la lata, y en el aire las octavillas que
     * vuelan. De noche, el haz del
     * reflector del Directorio barre el cielo sin encontrar a nadie y salen dos estrellas.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaFrente(e.meta, 56f * u, 48f * u)
        val x = b.x; val y = b.y
        val marmol = if (oscuro) mezcla(MARMOL, Color(0xFF2A3440), 0.55f) else MARMOL
        val veta = if (oscuro) Color(0xFF5E6A74) else Color(0xFFC9C3B6)
        val bronce = if (oscuro) mezcla(BRONCE, PENUMBRA, 0.3f) else BRONCE
        val linea = if (oscuro) Color(0xFFE7E1D4).copy(alpha = 0.45f) else BAQUELITA.copy(alpha = 0.7f)
        // De noche: el haz del reflector (el único degradado) y dos estrellas.
        if (oscuro) {
            val haz = Path().apply { moveTo(x + 27f * u, y - 8f * u); lineTo(x - 27f * u, y - 47f * u); lineTo(x - 14f * u, y - 48f * u); close() }
            drawPath(haz, Brush.linearGradient(listOf(Color(0xFFE8F0FF).copy(alpha = 0.16f), Color(0xFFE8F0FF).copy(alpha = 0.02f)),
                Offset(x + 27f * u, y - 8f * u), Offset(x - 20f * u, y - 47f * u)))
            drawCircle(Color.White.copy(alpha = 0.8f), 0.7f * u, Offset(x + 8f * u, y - 45f * u))
            drawCircle(Color.White.copy(alpha = 0.6f), 0.5f * u, Offset(x + 22f * u, y - 40f * u))
        }
        // El muro de mármol: la sombra al pie, los sillares con sus juntas y sus vetas, y la cara de la derecha en sombra.
        val muro = Rect(x - 24f * u, y - 30f * u, x + 18f * u, y)
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.15f), Offset(muro.left - 2f * u, y - 1.6f * u), Size(muro.width + 14f * u, 3.2f * u))
        drawRect(marmol, muro.topLeft, muro.size)
        listOf(10f, 20f).forEach { dy -> drawLine(veta, Offset(muro.left, y - dy * u), Offset(muro.right, y - dy * u), 0.6f * u) }
        listOf(0f to listOf(-10f, 8f), 10f to listOf(-19f, -1f, 12f), 20f to listOf(-10f, 8f)).forEach { (dy, xs) ->
            xs.forEach { dx -> drawLine(veta, Offset(x + dx * u, y - dy * u), Offset(x + dx * u, y - (dy + 10f) * u), 0.6f * u) }
        }
        drawPath(Path().apply { moveTo(muro.left + 3f * u, y - 24f * u); cubicTo(x - 12f * u, y - 27f * u, x - 8f * u, y - 18f * u, x + 4f * u, y - 22f * u) },
            veta.copy(alpha = 0.5f), style = Stroke(0.4f * u))
        drawPath(Path().apply { moveTo(x - 2f * u, y - 3f * u); cubicTo(x + 4f * u, y - 7f * u, x + 9f * u, y - 2f * u, muro.right - 2f * u, y - 6f * u) },
            veta.copy(alpha = 0.5f), style = Stroke(0.4f * u))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.15f else 0.07f), Offset(muro.right - 3f * u, muro.top), Size(3f * u, muro.height))
        drawRect(linea.copy(alpha = 0.35f), muro.topLeft, muro.size, style = Stroke(0.5f * u))
        // La cornisa de bronce con su brillo y sus dentículos.
        val cornisa = Rect(muro.left - 2f * u, muro.top - 3.4f * u, muro.right + 2f * u, muro.top)
        drawRect(bronce, cornisa.topLeft, cornisa.size)
        drawLine(mezcla(bronce, Color.White, 0.35f), Offset(cornisa.left, cornisa.top + 0.5f * u), Offset(cornisa.right, cornisa.top + 0.5f * u), 0.8f * u)
        var xd = muro.left + 1f * u; var kd = 0
        while (xd < muro.right - 1f * u && kd < 30) { drawRect(bronce.copy(alpha = 0.85f), Offset(xd, muro.top), Size(1.6f * u, 1.6f * u)); xd += 3.2f * u; kd++ }
        // El emblema de la red pintado con aerosol sobre el anillo de bronce del Directorio: el rocío, el trazo y los chorreones.
        val ce = Offset(x - 4f * u, y - 15.5f * u); val re = 7.2f * u
        drawCircle(bronce, 10f * u, ce, style = Stroke(2f * u))
        drawArc(mezcla(bronce, Color.White, 0.35f), 200f, 80f, false, Offset(ce.x - 10f * u, ce.y - 10f * u), Size(20f * u, 20f * u), style = Stroke(0.6f * u))
        with(TrazosFrente) {
            emblemaRed(ce, re, ROJO.copy(alpha = 0.18f), 3.6f * u)
            emblemaRed(ce, re, ROJO, 1.7f * u)
        }
        listOf(-3.4f to 4.6f, 1.4f to 6.4f, 4.8f to 3.2f).forEach { (dx, l) ->
            val p0 = Offset(ce.x + dx * u, ce.y + sqrt(maxOf(re * re - (dx * u) * (dx * u), 0f)))
            drawLine(ROJO, p0, Offset(p0.x, p0.y + l * u), 0.7f * u, StrokeCap.Round)
            drawCircle(ROJO, 0.75f * u, Offset(p0.x, p0.y + l * u))
        }
        // La plantilla de cartón apoyada al pie del muro, con el emblema calado y su borde manchado.
        val piv = Offset(x + 13f * u, y)
        rotate(12f, piv) {
            val pr = Rect(x + 8f * u, y - 13f * u, x + 18f * u, y)
            drawRect(n(CARTON, oscuro), pr.topLeft, pr.size)
            drawRect(linea.copy(alpha = 0.5f), pr.topLeft, pr.size, style = Stroke(0.5f * u))
            with(TrazosFrente) { emblemaRed(Offset(pr.center.x, pr.center.y - 0.5f * u), 2.8f * u, Color.Black.copy(alpha = 0.55f), 0.9f * u) }
            for (k in 0 until 6) drawCircle(ROJO.copy(alpha = 0.6f), (0.3f + 0.3f * azar(23, k)) * u, Offset(pr.left + pr.width * azar(29, k), pr.top + pr.height * azar(31, k)))
        }
        // La lata de aerosol, de pie junto a la plantilla.
        val lx = x + 23.5f * u
        drawRoundRect(n(CROMO, oscuro), Offset(lx - 1.8f * u, y - 8f * u), Size(3.6f * u, 8f * u), CornerRadius(0.8f * u))
        drawRect(ROJO, Offset(lx - 1.8f * u, y - 5.6f * u), Size(3.6f * u, 2.4f * u))
        drawLine(Color.White.copy(alpha = 0.5f), Offset(lx - 0.9f * u, y - 7.4f * u), Offset(lx - 0.9f * u, y - 0.8f * u), 0.5f * u)
        drawRoundRect(n(BAQUELITA, oscuro), Offset(lx - 1.2f * u, y - 9.6f * u), Size(2.4f * u, 1.8f * u), CornerRadius(0.6f * u))
        drawRect(n(BAQUELITA, oscuro), Offset(lx - 0.4f * u, y - 10.6f * u), Size(0.8f * u, 1f * u))
        // Las octavillas que vuelan.
        val papel = if (oscuro) mezcla(PAPEL, PENUMBRA, 0.35f) else PAPEL
        listOf(Triple(-17f, -38f, -18f), Triple(3f, -42f, 14f), Triple(15f, -35f, 32f)).forEach { (dx, dy, g) ->
            val c = Offset(x + dx * u, y + dy * u)
            rotate(g, c) {
                val hoja = Rect(c.x - 1.8f * u, c.y - 2.3f * u, c.x + 1.8f * u, c.y + 2.3f * u)
                drawRect(papel, hoja.topLeft, hoja.size)
                drawRect(linea.copy(alpha = 0.5f), hoja.topLeft, hoja.size, style = Stroke(0.4f * u))
                drawLine(ROJO, Offset(hoja.left + 0.6f * u, hoja.top + 1f * u), Offset(hoja.right - 0.6f * u, hoja.top + 1f * u), 0.6f * u)
                listOf(2.2f, 3.4f).forEach { yy -> drawLine(linea.copy(alpha = 0.45f), Offset(hoja.left + 0.6f * u, hoja.top + yy * u), Offset(hoja.right - 0.8f * u, hoja.top + yy * u), 0.35f * u) }
            }
        }
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Los objetos de Tu año de la red: la luna, la creciente del mundo ocupado, con la patrulla del Directorio (el bloque largo
     * con su anillo) que la cruza; la huella, la suela de la bota de vuelo con los galones de su dibujo y la ranura del tacón;
     * la pesa, la barra del desguace con dos bobinas de cinta por discos, con sus tres ventanas.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        when (e.forma) {
            "luna" -> lunaAnio(e)
            "huella" -> huellaAnio(e)
            "pesa" -> pesaAnio(e)
            else -> {}
        }
    }

    /**
     * La creciente con la boca arriba a la derecha y, si cabe, la patrulla del Directorio que pasa por delante de su lado oscuro:
     * el bloque largo con el anillo que lo ciñe, recortado contra el fondo.
     */
    private fun DrawScope.lunaAnio(e: EstadoObjeto) {
        val r = e.radio; val c = e.centro; val u = density
        drawPath(crecienteFrente(Offset(c.x - r * 0.18f, c.y + r * 0.04f), r * 0.82f, r * 0.46f, -r * 0.2f, 0.8f), e.color)
        if (r >= 7f * u) rotate(-18f, Offset(c.x + r * 0.3f, c.y - r * 0.1f)) {
            val y = c.y - r * 0.1f
            val cuerpo = Rect(c.x - r * 0.25f, y - r * 0.1f, c.x + r * 0.9f, y + r * 0.1f)
            val anillo = Rect(c.x - r * 0.02f, y - r * 0.3f, c.x + r * 0.16f, y + r * 0.3f)
            val margen = grosorFrente(r, 0.07f)
            drawRoundRect(e.fondo, cuerpo.inflate(margen).topLeft, cuerpo.inflate(margen).size, CornerRadius(r * 0.1f + margen))
            drawOval(e.fondo, anillo.inflate(margen).topLeft, anillo.inflate(margen).size)
            drawRoundRect(e.color, cuerpo.topLeft, cuerpo.size, CornerRadius(r * 0.1f))
            drawOval(e.color, anillo.topLeft, anillo.size, style = Stroke(grosorFrente(r, 0.07f)))
            drawCircle(e.fondo, maxOf(r * 0.035f, 0.4f * u), Offset(cuerpo.right - r * 0.16f, y))
        } else drawCircle(e.color, maxOf(r * 0.08f, 0.6f * u), Offset(c.x + r * 0.55f, c.y - r * 0.5f))
    }

    /** La suela entera de la bota de vuelo, ladeada: la puntera ancha, el talle y el tacón, con los galones en hueco. */
    private fun DrawScope.huellaAnio(e: EstadoObjeto) {
        val r = e.radio; val c = e.centro; val x = c.x; val y = c.y; val u = density
        rotate(-14f, c) {
            val suela = Path().apply {
                moveTo(x, y - r)
                cubicTo(x + r * 0.55f, y - r, x + r * 0.55f, y - r * 0.2f, x + r * 0.3f, y + r * 0.15f)
                cubicTo(x + r * 0.22f, y + r * 0.3f, x + r * 0.4f, y + r * 0.5f, x + r * 0.38f, y + r * 0.78f)
                cubicTo(x + r * 0.36f, y + r * 1.02f, x - r * 0.36f, y + r * 1.02f, x - r * 0.38f, y + r * 0.78f)
                cubicTo(x - r * 0.4f, y + r * 0.5f, x - r * 0.22f, y + r * 0.3f, x - r * 0.3f, y + r * 0.15f)
                cubicTo(x - r * 0.55f, y - r * 0.2f, x - r * 0.55f, y - r, x, y - r)
                close()
            }
            drawPath(suela, e.color)
            val g = grosorFrente(r, 0.1f)
            val cuantos = if (r < 7f * u) 3 else 4
            for (k in 0 until cuantos) {
                val yy = y - r * 0.64f + k * r * (if (cuantos == 3) 0.28f else 0.2f)
                val a = r * (0.27f - k * 0.025f)
                drawPath(Path().apply { moveTo(x - a, yy + r * 0.12f); lineTo(x, yy); lineTo(x + a, yy + r * 0.12f) }, e.fondo,
                    style = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            drawLine(e.fondo, Offset(x - r * 0.34f, y + r * 0.5f), Offset(x + r * 0.34f, y + r * 0.5f), grosorFrente(r, 0.08f))
        }
    }

    /** La barra con una bobina de cinta en cada punta, vista un poco de lado: los discos ovalados con sus tres ventanas, si cabe. */
    private fun DrawScope.pesaAnio(e: EstadoObjeto) {
        val r = e.radio; val c = e.centro; val x = c.x; val y = c.y; val u = density
        val gb = grosorFrente(r, 0.16f)
        drawRoundRect(e.color, Offset(x - r * 0.98f, y - gb / 2), Size(r * 1.96f, gb), CornerRadius(gb / 2))
        listOf(-1f, 1f).forEach { s ->
            val bc = Offset(x + s * r * 0.6f, y)
            val rx = r * 0.26f; val ry = r * 0.76f
            drawOval(e.color, Offset(bc.x - rx, bc.y - ry), Size(2 * rx, 2 * ry))
            if (r >= 6f * u) {
                for (k in 0 until 3) {
                    val a = Math.toRadians(k * 120.0 - 90.0).toFloat()
                    val p = Offset(bc.x + rx * 0.45f * cos(a), bc.y + ry * 0.52f * sin(a))
                    drawOval(e.fondo, Offset(p.x - rx * 0.24f, p.y - ry * 0.17f), Size(rx * 0.48f, ry * 0.34f))
                }
                drawOval(e.fondo, Offset(bc.x - rx * 0.14f, bc.y - ry * 0.08f), Size(rx * 0.28f, ry * 0.16f))
            }
        }
    }
}
