package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Abismo: gris azulado liso, de pecho claro y ojos de ámbar, con la escafandra de latón (el peto y el casco)
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/abismo_*`, `herramientas/gato/gato20c.py`): un gato liso, sin rayas,
// gris azulado (#8C9FAE), de pecho claro (#D3DEE5), ojos de ámbar (#E8B84A) y nariz rosa (#C9868A), con la escafandra
// de buzo de latón: el peto, «una placa ancha al pie del cuello, con pernos» (#B8913E, pernos #EBD27E), y el casco,
// «cúpula opaca que tapa las orejas, ojo de buey al frente por el que se ve la cara, otro arriba, pernos en la base y
// la válvula del aire atrás» (latón #C9A24A, válvula #8C6A1F, vidrio #A8DCE6). Es el casco de cobre y bronce de Nikos
// («tres ventanillas», «la válvula para que salga el aire viejo»); en el barco hay otros dos colgados del palo.
//
// Sus dos acciones propias, las del de pixel (`gato20c.py`): nadar entre burbujas (de pie; las burbujas suben de la
// válvula del casco) y seguir la luz de un pez abisal (sentado; el rape con su señuelo de luz verde agua). En el cuadro
// el gato está sobre la cubierta y no bajo el agua: nada en el aire, como en el de pixel, y el pez va en un frasco de
// agua de mar sobre la cubierta, con una ramita de coral (**[Diseño propio]**).
//
// El casco va sujeto al peto, como el de verdad, y no gira con la cabeza: el motor le dice al accesorio hacia dónde
// está vuelto el cuerpo, pero no la cabeza ni cuánto la baja. Por eso el ojo de buey de delante es grande y llega
// hasta el canto del casco: así la cara se ve siempre, y los bigotes y la mano que se lame salen por él sin cruzar el
// latón.
//
// Lo que no se puede llevar al cuadro con el `Pelaje` de hoy: el gato de noche del de pixel, bioluminiscente (el cuerpo
// casi negro con el contorno verde agua, los fotóforos violeta y azules del costado y los ojos que brillan): el cuadro
// es el mismo en claro y en oscuro, y el pelaje no tiene un dibujo que se encienda de noche; y el contorno oscuro del
// de pixel (#0B2536), que en el cuadro hace el borde del motor.

private val A_MANTO = Color(0xFF8C9FAE)
private val A_CLARO = Color(0xFFD3DEE5)
private val A_SOMBRA = Color(0xFF667C8D)
private val A_OJOS = Color(0xFFE8B84A)
private val A_NARIZ = Color(0xFFC9868A)
private val A_TINTA = Color(0xFF0B2536)
private val A_PETO = Color(0xFFB8913E)
private val A_PETO_OSC = Color(0xFF7A5A1E)
private val A_LATON = Color(0xFFC9A24A)
private val A_LATON_LUZ = Color(0xFFE6C878)
private val A_VALVULA = Color(0xFF8C6A1F)
private val A_PERNO = Color(0xFFEBD27E)
private val A_VIDRIO = Color(0xFFA8DCE6)
private val A_ESPUMA = Color(0xFFE6F6FA)
private val A_BURBUJA = Color(0xFF6FB3CF)
private val A_BRILLO = Color(0xFFF6FDFF)
private val A_PEZ = Color(0xFF2E4456)
private val A_SENUELO = Color(0xFF5CF2C6)
private val A_CORAL = Color(0xFFC0563F)
private val A_ARENA = Color(0xFFE4D3A4)

private fun unidadA(v: Offset, siNo: Offset = Offset(0f, 1f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/**
 * Dónde va cada pieza de la escafandra en una postura. El casco es una bola de radio [R] puesta sobre la cabeza y
 * sujeta al peto: sigue al cuerpo ([s]: cuánto de perfil; [co]: cuánto de frente, o de espaldas si es negativo), no a la
 * cabeza. Por abajo se corta donde se apoya en el peto, hacia el cuello ([abajo]), y nunca baja de los pies del gato.
 */
private class Escafandra(val p: PuntosGato) {
    private val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
    /** Hacia el cuello, desde la cabeza: por ahí se apoya el casco en el peto. */
    val abajo = unidadA(p.cuello - p.cabeza)
    val atraves = Offset(-abajo.y, abajo.x)
    val c = p.cabeza + Offset(0.3f * s, -2.6f)
    /** El ojo de buey de delante, por el que se ve la cara: solo si la cara queda de este lado. */
    val conCara = co > -0.25f
    val ventana = p.cabeza + Offset(4.6f * s, 1f)
    val rv = 5.6f
    /** El peto: una placa ancha atravesada al pie del cuello. */
    val peto = c + abajo * (R * 0.8f + 0.9f)
    /** La válvula del aire, atrás y arriba: su base en el casco y su boca, por donde salen las burbujas. */
    val valvula = c + Offset(-0.8f * R * s, -0.5f * R)
    val boca = valvula + Offset(-2.4f * s, -2f + 0.6f * (1f - s))

    /** El casco, cortado por el peto y por el suelo. */
    fun bola(): Path {
        val bola = Path().apply { addOval(Rect(c, R)) }
        // El medio plano de lo que queda por encima del corte del cuello.
        val corte = c + abajo * (R * 0.8f)
        val lejos = 40f
        val medio = Path().apply {
            moveTo(corte + atraves * lejos); lineTo(corte - atraves * lejos); lineTo(corte - atraves * lejos - abajo * lejos); lineTo(corte + atraves * lejos - abajo * lejos); close()
        }
        val arriba = Path.combine(PathOperation.Intersect, bola, medio)
        // Echado, el casco llega al suelo: se apoya en él y no se hunde.
        if (c.y + R < 0.3f) return arriba
        return Path.combine(PathOperation.Intersect, arriba, Path().apply { addRect(Rect(-60f, -80f, 60f, 0.3f)) })
    }

    companion object {
        /** El radio del casco: tapa las orejas, como el de pixel. */
        const val R = 10f
    }
}

/**
 * Pinta [bloque] sin tapar la cola cuando el gato está de espaldas y la lleva en alto, por delante del casco (como
 * `debajoDeLaCabeza` en `Pelajes.kt`).
 */
private inline fun DrawScope.sinTaparLaCola(p: PuntosGato, s: Float, co: Float, crossinline bloque: DrawScope.() -> Unit) {
    if (p.giro >= -0.25f) { bloque(); return }
    val raiz = p.cadera + Offset(-4f * s, -1.4f - 2.2f * abs(co))
    val v = p.puntaCola - raiz
    val l = sqrt(v.x * v.x + v.y * v.y)
    if (l < 2f) { bloque(); return }
    val n = Offset(-v.y / l, v.x / l) * 2.6f
    clipPath(Path().apply { moveTo(raiz + n); lineTo(p.puntaCola + n); lineTo(p.puntaCola - n); lineTo(raiz - n); close() }, ClipOp.Difference) { bloque() }
}

/** Un ojo de buey chico: el vidrio, su brillo y el aro de latón. */
private fun DrawScope.ojoDeBuey(luz: LuzGato, o: Offset, rx: Float, ry: Float) {
    drawOval(luz.tono(lerp(A_VIDRIO, A_TINTA, 0.55f)), Offset(o.x - rx, o.y - ry), Size(rx * 2f, ry * 2f))
    drawOval(luz.tono(A_VIDRIO).copy(alpha = 0.85f), Offset(o.x - rx * 0.62f, o.y - ry * 0.7f), Size(rx * 0.9f, ry * 0.8f))
    drawOval(luz.tono(A_PERNO), Offset(o.x - rx - 0.35f, o.y - ry - 0.35f), Size(rx * 2f + 0.7f, ry * 2f + 0.7f), style = Stroke(0.7f))
    drawOval(luz.tono(A_VALVULA).copy(alpha = 0.7f), Offset(o.x - rx - 0.75f, o.y - ry - 0.75f), Size(rx * 2f + 1.5f, ry * 2f + 1.5f), style = Stroke(0.25f))
}

/** Un perno: la cabeza clara con su sombra. */
private fun DrawScope.perno(luz: LuzGato, o: Offset, r: Float = 0.5f) {
    drawCircle(luz.tono(A_VALVULA), r * 1.15f, o + Offset(0.12f, 0.15f))
    drawCircle(luz.tono(A_PERNO), r, o)
}

/**
 * La escafandra: el peto con sus pernos y el casco de latón, con el ojo de buey de delante (por el que se ve la cara),
 * el de arriba, los de los lados cuando se le ve de frente o de espaldas, la válvula del aire atrás, la junta del cuello
 * y sus pernos, el brillo del metal del lado de la luz y la sombra del otro.
 */
private val escafandra: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val e = Escafandra(p)
    val R = Escafandra.R
    val c = e.c
    val lado = if (luz.rayo.lat >= 0f) 1f else -1f
    val laton = luz.tono(A_LATON); val luzLaton = luz.tono(A_LATON_LUZ); val oscuro = luz.tono(A_VALVULA)
    val hondo = luz.tono(lerp(A_VALVULA, A_TINTA, 0.45f))
    sinTaparLaCola(p, e.s, e.co) {
        // ---- El peto: una placa ancha atravesada al pie del cuello, con su canto oscuro y tres pernos.
        val largo = 5.2f; val grueso = 2.4f
        val a = e.peto - e.atraves * largo; val b = e.peto + e.atraves * largo
        clipRect(-60f, -80f, 60f, 0.3f) {
            drawLine(luz.tono(A_PETO_OSC), a + Offset(0.3f, 0.45f), b + Offset(0.3f, 0.45f), grueso * 2f + 0.6f, StrokeCap.Round)
            drawLine(Brush.linearGradient(listOf(luz.tono(lerp(A_PETO, A_LATON_LUZ, 0.45f)), luz.tono(A_PETO), luz.tono(A_PETO_OSC)), e.peto - e.abajo * grueso, e.peto + e.abajo * grueso), a, b, grueso * 2f, StrokeCap.Round)
            // El reborde de arriba, donde enrosca el casco.
            drawLine(luz.tono(A_PETO_OSC).copy(alpha = 0.8f), a - e.abajo * (grueso * 0.55f), b - e.abajo * (grueso * 0.55f), 0.5f, StrokeCap.Round)
            for (f in listOf(-0.72f, 0f, 0.72f)) perno(luz, e.peto + e.atraves * (largo * f) + e.abajo * (grueso * 0.3f), 0.42f)
        }
        val forma = e.bola()
        val hueco = Path().apply { addOval(Rect(e.ventana, e.rv)) }
        val casco = if (e.conCara) Path.combine(PathOperation.Difference, forma, hueco) else forma
        // ---- El canto oscuro del casco, corrido hacia el lado de la sombra, como el borde del gato.
        clipRect(-60f, -80f, 60f, 0.3f) { drawPath(Path().apply { addPath(casco, Offset(-0.5f * lado, 0.55f)) }, hondo) }
        // ---- El casco: el latón, con la luz arriba y del lado del sol, y el ojo de buey de delante hueco.
        val brillo = c + Offset(0.35f * R * lado, -0.42f * R)
        drawPath(casco, Brush.radialGradient(listOf(luzLaton, laton, laton, oscuro), brillo, R * 1.55f))
        clipPath(casco) {
            // La sombra de abajo, hacia el cuello.
            drawCircle(Brush.verticalGradient(listOf(Color.Transparent, hondo.copy(alpha = 0.45f)), c.y + R * 0.15f, c.y + R), R, c)
            // La costura de la cúpula: una vuelta un poco por debajo del ecuador.
            drawArc(oscuro.copy(alpha = 0.55f), 15f, 150f, false, Offset(c.x - R * 0.98f, c.y - R * 0.55f), Size(R * 1.96f, R * 1.25f), style = Stroke(0.35f))
            drawArc(luzLaton.copy(alpha = 0.35f), 20f, 140f, false, Offset(c.x - R * 0.98f, c.y - R * 0.5f), Size(R * 1.96f, R * 1.25f), style = Stroke(0.25f))
            // Dos abolladuras del oficio: un golpe en la cúpula y otro junto a la junta.
            drawOval(oscuro.copy(alpha = 0.3f), Offset(c.x - 0.45f * R * lado - 1.1f, c.y - 0.62f * R), Size(2.2f, 1.2f))
            drawOval(luzLaton.copy(alpha = 0.35f), Offset(c.x - 0.45f * R * lado - 1f, c.y - 0.68f * R), Size(1.8f, 0.6f))
            drawOval(oscuro.copy(alpha = 0.25f), Offset(c.x + 0.2f * R * lado - 0.9f, c.y + 0.5f * R), Size(1.8f, 1f))
            // La junta del cuello: una franja más oscura donde el casco enrosca en el peto, con sus pernos.
            val corte = c + e.abajo * (R * 0.8f); val medio = e.atraves * (R * 0.6f)
            drawLine(hondo.copy(alpha = 0.7f), corte - medio - e.abajo * 0.8f, corte + medio - e.abajo * 0.8f, 1.7f)
            drawLine(luzLaton.copy(alpha = 0.5f), corte - medio - e.abajo * 1.75f, corte + medio - e.abajo * 1.75f, 0.35f)
            for (f in listOf(-0.62f, -0.21f, 0.21f, 0.62f)) perno(luz, corte + e.atraves * (R * f) - e.abajo * 0.75f, 0.48f)
            // El brillo del latón: una mancha larga arriba, del lado de la luz, que de noche casi se apaga.
            drawOval(luz.brillo.copy(alpha = 0.55f * (1f - 0.65f * luz.noche)), Offset(brillo.x - 1.6f, brillo.y - 0.7f), Size(3.2f, 1.4f))
            drawOval(luz.brillo.copy(alpha = 0.3f * (1f - 0.6f * luz.noche)), Offset(brillo.x + 1.6f * lado - 0.5f, brillo.y + 1.2f), Size(1f, 0.6f))
        }
        // De noche, un filo de luz por el canto de arriba para que el casco no se pierda.
        if (luz.noche > 0.05f) clipPath(casco) {
            drawArc(luz.brillo.copy(alpha = 0.55f * luz.noche), 195f, 150f, false, Offset(c.x - R + 0.5f, c.y - R + 0.5f), Size(R * 2f - 1f, R * 2f - 1f), style = Stroke(0.9f, cap = StrokeCap.Round))
        }
        // ---- El ojo de buey de delante: el vidrio (se ve la cara a través), su reflejo y su aro grueso con pernos.
        if (e.conCara) {
            val vidrio = Path.combine(PathOperation.Intersect, hueco, forma)
            drawPath(vidrio, luz.tono(A_VIDRIO).copy(alpha = 0.16f))
            clipPath(vidrio) {
                drawArc(Color.White.copy(alpha = 0.55f * (1f - 0.5f * luz.noche)), 200f, 70f, false, Offset(e.ventana.x - e.rv * 0.72f, e.ventana.y - e.rv * 0.72f), Size(e.rv * 1.44f, e.rv * 1.44f), style = Stroke(0.6f, cap = StrokeCap.Round))
                drawCircle(Color.White.copy(alpha = 0.5f * (1f - 0.5f * luz.noche)), 0.35f, e.ventana + Offset(-e.rv * 0.2f, -e.rv * 0.62f))
            }
            clipPath(forma) {
                drawCircle(hondo, e.rv + 0.25f, e.ventana, style = Stroke(0.6f))
                drawCircle(Brush.radialGradient(listOf(luzLaton, laton, oscuro), e.ventana + Offset(-0.4f * e.rv, -0.6f * e.rv), e.rv * 2.2f), e.rv + 1f, e.ventana, style = Stroke(1.4f))
                for (i in 0 until 6) {
                    val ang = (PI / 3 * i + PI / 6).toFloat()
                    perno(luz, e.ventana + Offset(cos(ang), sin(ang)) * (e.rv + 1f), 0.42f)
                }
            }
        }
        // ---- Los ojos de buey chicos: el de arriba siempre; los de los lados, de frente o de espaldas.
        ojoDeBuey(luz, c + Offset(-0.22f * R * e.s, -0.68f * R), 1.25f * (0.55f + 0.45f * e.s), 1f)
        if (abs(e.co) > 0.55f) for (l in listOf(-1f, 1f)) ojoDeBuey(luz, c + Offset(l * 0.74f * R, 0.06f * R), 1f, 1.3f)
        else if (!e.conCara) ojoDeBuey(luz, c + Offset(0.58f * R, 0.06f * R), 1.1f, 1.3f)
        // ---- La válvula del aire: de perfil, un tubo corto hacia atrás y arriba; de espaldas, su boca redonda.
        if (e.s > 0.3f) {
            drawLine(hondo, e.valvula + Offset(0.3f, 0.4f), e.boca + Offset(0.3f, 0.4f), 2.6f, StrokeCap.Round)
            drawLine(oscuro, e.valvula, e.boca, 2.2f, StrokeCap.Round)
            drawLine(luz.tono(A_PERNO).copy(alpha = 0.7f), e.valvula + Offset(0.2f, -0.6f), e.boca + Offset(0.2f, -0.6f), 0.4f, StrokeCap.Round)
            drawCircle(luz.tono(A_PERNO), 1.15f, e.boca, style = Stroke(0.5f))
        } else if (!e.conCara) {
            drawCircle(hondo, 2.1f, e.valvula + Offset(0.2f, 0.3f)); drawCircle(oscuro, 1.9f, e.valvula)
            drawCircle(luz.tono(A_PERNO), 1.9f, e.valvula, style = Stroke(0.5f)); drawCircle(hondo, 0.7f, e.valvula)
        }
    }
}

// ---- Nada entre burbujas (de pie).

private fun poseNadar(c: Int): Pose {
    val q = Math.floorMod(c, 4)
    // Como el de pixel: se despega del suelo, bracea con las de delante, patalea con las de atrás y estira la cola.
    val u = (2 * PI * q / 4).toFloat(); val s = sin(u); val co = cos(u)
    val dy = -4.4f - 0.8f * s
    val hombro = Offset(6.6f, -15f + dy); val cadera = Offset(-7.2f, -14.2f + dy)
    return Gatuno.DE_PIE.copy(
        cadera = cadera, hombro = hombro, cabeza = Offset(13.4f, -23.4f + dy), morro = -0.14f + 0.03f * s, orejas = 0.9f,
        manoC = hombro + Offset(6.4f + 2.3f * co, 5.6f - 1.8f * s), manoL = hombro + Offset(5.4f - 2.1f * co, 6.6f + 1.8f * s),
        pieC = cadera + Offset(-5.8f - 1.4f * co, 8.6f + 1.4f * s), pieL = cadera + Offset(-4.8f + 1.4f * co, 9.4f - 1.4f * s),
        cola0 = 0.22f + 0.05f * s, cola1 = 0.06f + 0.1f * s, cola2 = -0.02f + 0.16f * co, ojos = if (q == 2) 0.45f else 1f,
    )
}

/** Las burbujas que suben de la válvula del casco, cada una más grande cuanto más sube, con su brillo. */
private val burbujas: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val e = Escafandra(p)
    val q = Math.floorMod(c, 4)
    // Las burbujas devuelven la luz: de noche se apagan menos que el resto.
    fun brilla(color: Color) = lerp(luz.tono(color), color, 0.3f * luz.noche)
    val borde = brilla(A_BURBUJA); val dentro = brilla(A_ESPUMA); val claro = brilla(A_BRILLO)
    for (j in 0..2) {
        val f = ((q + j * 4f / 3f) % 4f) / 4f
        val o = e.boca + Offset(1.5f * sin(f * 5f + j) - 2.4f * f, -1.6f - f * 12f)
        val r = 0.85f + 0.75f * f + if (j == 1) 0.2f else 0f
        drawCircle(dentro.copy(alpha = 0.4f), r, o)
        drawCircle(borde, r, o, style = Stroke(0.42f))
        drawCircle(claro.copy(alpha = 0.9f), r * 0.32f, o + Offset(-r * 0.35f, -r * 0.35f))
    }
    // Una burbujita que acaba de salir, pegada a la boca.
    if (q % 2 == 0) drawCircle(borde.copy(alpha = 0.8f), 0.45f, e.boca + Offset(-0.4f, -1f), style = Stroke(0.25f))
}

// ---- Sigue la luz de un pez abisal (sentado).

/** El frasco: dónde pisa, su ancho, su alto y hasta dónde llega el agua. */
private val FRASCO = Offset(20.4f, 0f)
private const val FRASCO_ANCHO = 5.2f
private const val FRASCO_ALTO = 12.4f
private const val FRASCO_AGUA = 10.4f

/** Dónde va el pez en el frasco en cada cuadro, y hacia dónde mira (1: hacia la derecha). */
private fun pezEn(q: Int): Pair<Offset, Float> {
    val a = (2 * PI * q / 6).toFloat()
    val o = FRASCO + Offset(2.5f * cos(a), -5.2f + 1.7f * sin(a))
    return o to (if (sin(a + 0.4f) > 0f) -1f else 1f)
}

private fun posePez(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val (pez, _) = pezEn(q)
    // Sentado delante del frasco, sigue al pez con la cabeza; en el cuarto cuadro le da al vidrio con la mano.
    val sigue = (pez.x - FRASCO.x) / 2.5f
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.2f + 0.5f * sigue, -25.4f + 0.3f * (pez.y - FRASCO.y + 5.2f)), morro = 0.36f + 0.08f * sigue + 0.05f * (pez.y - FRASCO.y + 5.2f), orejas = 1f,
        ojos = if (q == 5) 0.25f else 1f,
        manoC = if (q == 3) Offset(14.6f, -7f) else if (q == 4) Offset(11.6f, -3.8f) else Gatuno.SENTADO.manoC,
        cola2 = listOf(2.3f, 2.5f, 2.7f, 2.8f, 2.6f, 2.4f)[q],
    )
}

/**
 * El frasco de agua de mar sobre la cubierta, con arena y una ramita de coral en el fondo, y dentro el rape, que da
 * vueltas con su señuelo de luz verde agua (la luz es propia: de noche alumbra el vidrio y la cara del gato).
 */
private val frascoConPez: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val (pez, mira) = pezEn(q)
    val x = FRASCO.x; val w = FRASCO_ANCHO; val alto = FRASCO_ALTO
    val lado = if (luz.rayo.lat >= 0f) 1f else -1f
    val vidrio = luz.tono(A_VIDRIO); val tinta = luz.tono(A_TINTA)
    // La sombra en la cubierta.
    drawOval(Color.Black.copy(alpha = 0.18f), Offset(x - w - 1.2f - 1.6f * lado, -1.2f), Size(w * 2f + 2.4f, 1.6f))
    // El cuerpo del frasco: panzudo abajo, con el cuello más estrecho y el labio.
    val cuerpo = Path().apply {
        moveTo(x - w * 0.72f, -alto); lineTo(x - w * 0.72f, -alto + 1.2f)
        cubicTo(Offset(x - w * 1.08f, -alto + 2.4f), Offset(x - w, -alto + 3.4f), Offset(x - w, -alto + 4.6f))
        lineTo(x - w, -1f); cubicTo(Offset(x - w, -0.3f), Offset(x - w + 0.6f, 0f), Offset(x - w + 1.2f, 0f))
        lineTo(x + w - 1.2f, 0f); cubicTo(Offset(x + w - 0.6f, 0f), Offset(x + w, -0.3f), Offset(x + w, -1f))
        lineTo(x + w, -alto + 4.6f); cubicTo(Offset(x + w, -alto + 3.4f), Offset(x + w * 1.08f, -alto + 2.4f), Offset(x + w * 0.72f, -alto + 1.2f))
        lineTo(x + w * 0.72f, -alto); close()
    }
    clipPath(cuerpo) {
        // El agua de mar, verde botella, más honda abajo; la arena y el coral del fondo.
        drawRect(Brush.verticalGradient(listOf(luz.tono(Color(0xFF6FB8AE)).copy(alpha = 0.55f), luz.tono(Color(0xFF2A8680)).copy(alpha = 0.75f)), -FRASCO_AGUA, 0f), Offset(x - w - 1f, -FRASCO_AGUA), Size(w * 2f + 2f, FRASCO_AGUA))
        drawOval(luz.tono(A_ARENA), Offset(x - w - 0.5f, -1.9f), Size(w * 2f + 1f, 2.8f))
        val coral = luz.tono(A_CORAL)
        val pie = Offset(x + w * 0.45f, -1.1f)
        drawLine(coral, pie, pie + Offset(-0.4f, -2.8f), 0.55f, StrokeCap.Round)
        drawLine(coral, pie + Offset(-0.2f, -1.4f), pie + Offset(-1.4f, -2.6f), 0.45f, StrokeCap.Round)
        drawLine(coral, pie + Offset(-0.3f, -2.2f), pie + Offset(0.6f, -3.4f), 0.4f, StrokeCap.Round)
        // El pez: un cuerpo oscuro y redondo con la boca de dientes, la cola, el ojo y la caña con el señuelo.
        val cuerpoPez = luz.tono(A_PEZ)
        drawOval(cuerpoPez, Offset(pez.x - 1.8f, pez.y - 1.3f), Size(3.6f, 2.6f))
        drawPath(Path().apply { moveTo(pez + Offset(-1.4f * mira, 0f)); lineTo(pez + Offset(-3f * mira, -1.1f)); lineTo(pez + Offset(-2.6f * mira, 0f)); lineTo(pez + Offset(-3f * mira, 1.1f)); close() }, cuerpoPez)
        drawPath(Path().apply { moveTo(pez + Offset(0.8f * mira, 0.4f)); lineTo(pez + Offset(1.9f * mira, 0.2f)); lineTo(pez + Offset(1.5f * mira, 1f)); close() }, luz.tono(A_ESPUMA))
        drawCircle(luz.tono(A_BURBUJA), 0.35f, pez + Offset(0.7f * mira, -0.5f))
        val punta = pez + Offset(2.2f * mira, -2.6f)
        drawPath(Path().apply { moveTo(pez + Offset(0.3f * mira, -1.2f)); cubicTo(pez + Offset(0.8f * mira, -2.6f), pez + Offset(1.6f * mira, -2.9f), punta) }, cuerpoPez, style = Stroke(0.22f, cap = StrokeCap.Round))
        val late = if (q % 2 == 0) 0.6f else 0.85f
        drawCircle(A_SENUELO, late, punta)
        drawCircle(Color.White.copy(alpha = 0.8f), late * 0.4f, punta)
        // La superficie del agua, con su menisco claro, y dos burbujitas que suben.
        drawLine(vidrio.copy(alpha = 0.8f), Offset(x - w, -FRASCO_AGUA), Offset(x + w, -FRASCO_AGUA), 0.35f)
        for (j in 0..1) drawCircle(vidrio.copy(alpha = 0.8f), 0.28f, Offset(x - 1.6f + 3f * j, -2.6f - ((q + 3 * j) % 6) * 1.1f), style = Stroke(0.18f))
        // El vidrio: el reflejo largo del lado de la luz y el lado de la sombra un poco más oscuro.
        drawLine(Color.White.copy(alpha = 0.55f * (1f - 0.5f * luz.noche)), Offset(x + w * 0.62f * lado, -alto + 4.4f), Offset(x + w * 0.62f * lado, -1.6f), 0.7f, StrokeCap.Round)
        drawRect(tinta.copy(alpha = 0.12f), Offset(if (lado > 0f) x - w - 1f else x + w * 0.4f, -alto), Size(w * 0.6f + 1f, alto))
    }
    drawPath(cuerpo, vidrio.copy(alpha = 0.9f), style = Stroke(0.4f))
    drawPath(cuerpo, tinta.copy(alpha = 0.35f), style = Stroke(0.15f))
    // El labio del frasco, más grueso.
    drawOval(vidrio, Offset(x - w * 0.78f, -alto - 0.55f), Size(w * 1.56f, 1.1f), style = Stroke(0.45f))
    // El señuelo da luz propia: de día apenas se nota; de noche alumbra el frasco y lo que tiene cerca.
    resplandor(pez + Offset(2.2f * mira, -2.6f), 6f + 7f * luz.noche, 0.25f + 0.6f * luz.noche, A_SENUELO)
}

/** El gato de Abismo. */
internal val PELAJE_ABISMO = Pelaje(
    manto = A_MANTO, claro = A_CLARO, marca = A_SOMBRA, dibujo = DibujoPelaje.LISO, ojos = A_OJOS,
    nariz = A_NARIZ, accesorio = escafandra,
    especiales = listOf(EspecialGato("nada entre burbujas", false, 4, ::poseNadar, burbujas), EspecialGato("sigue la luz de un pez abisal", true, 6, ::posePez, frascoConPez)),
)
