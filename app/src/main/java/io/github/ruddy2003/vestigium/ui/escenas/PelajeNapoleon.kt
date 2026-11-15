package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Napoleónico: gris liso de pecho blanco, con su collar carmesí de medalla de oro y su bicornio azul tinta
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/napoleon_*`, `herramientas/gato/gato8.py`): gris liso, sin rayas
// (#A4A3A4, la mezcla del papel de despacho y el azul pizarra), de pecho blanco (#D6D6D6), ojos de oro (#B8892B), un
// collar carmesí (#A3172E) con una medalla de oro claro (#D9B25B) y un bicornio azul tinta (#14213D), puesto de
// delante atrás, con su ribete de oro (#B8892B) y su presilla de oro claro. Sus dos acciones propias, las mismas que el
// de pixel (`gato17.py`): mirar por el catalejo (latón #B8892B, anillo azul tinta, ocular de oro claro) y leer un
// despacho sellado (papel crema #F8F3E6, renglones de tinta y el lacre carmesí).
//
// El cuento de Jean-Baptiste lleva chacó (es soldado de línea, no general), pero el gato es el del tema y el de pixel
// lleva bicornio: aquí se sigue al de pixel, para que al cambiar de gato siga siendo el mismo.

private val N_MANTO = Color(0xFFA4A3A4)
private val N_CLARO = Color(0xFFD6D6D6)
private val N_SOMBRA = Color(0xFF7C7F87)
private val N_OJOS = Color(0xFFB8892B)
private val N_CARMESI = Color(0xFFA3172E)
private val N_CARMESI_OSC = Color(0xFF6E0F1F)
private val N_ORO = Color(0xFFB8892B)
private val N_ORO_CLARO = Color(0xFFD9B25B)
private val N_ORO_LUZ = Color(0xFFF2D78E)
private val N_TINTA = Color(0xFF14213D)
private val N_TINTA_LUZ = Color(0xFF3A4A72)
private val N_TENUE = Color(0xFF4F5874)
private val N_PAPEL = Color(0xFFF8F3E6)
private val N_PAPEL_OSC = Color(0xFFD9CDB0)

private fun unidadN(v: Offset, siNo: Offset = Offset(1f, 0f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** Una puntita de la mano (del color del manto) encima de lo que sostiene, para que se vea que lo agarra. */
private fun DrawScope.dedos(luz: LuzGato, en: Offset, r: Float = 1.25f) {
    drawOval(luz.tono(N_MANTO), Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f))
    drawOval(luz.tono(N_SOMBRA).copy(alpha = 0.6f), Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f), style = Stroke(0.25f))
    drawLine(luz.tono(N_SOMBRA).copy(alpha = 0.7f), en + Offset(-0.1f, -r * 0.6f), en + Offset(0.1f, r * 0.5f), 0.2f)
}

/** La medalla de oro bajo el collar, por delante; el bicornio con su ribete, su presilla y la sombra que echa. */
private val puestoNapoleon: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo)
    val lado = if (luz.rayo.lat >= 0f) 1f else -1f
    // ---- La medalla: un disco de oro con su cinta corta carmesí y un punto de brillo.
    if (p.giro >= -0.25f) {
        // Cuelga del collar mismo (que pasa por p.cuello), un poco hacia el pecho.
        val sitio = p.cuello + Offset(1.2f * s, 1.5f)
        drawPath(Path().apply { moveTo(sitio.x - 0.7f, sitio.y - 1.6f); lineTo(sitio.x + 0.7f, sitio.y - 1.6f); lineTo(sitio.x + 0.5f, sitio.y - 0.3f); lineTo(sitio.x - 0.5f, sitio.y - 0.3f); close() }, luz.tono(N_CARMESI_OSC))
        drawCircle(luz.tono(N_ORO), 1.25f, sitio + Offset(0f, 0.8f))
        drawCircle(luz.tono(N_ORO_CLARO), 0.85f, sitio + Offset(0f, 0.8f))
        drawCircle(N_ORO_LUZ.copy(alpha = 0.85f * (1f - 0.5f * luz.noche)), 0.32f, sitio + Offset(-0.35f * lado, 0.45f))
    }
    // ---- El bicornio, puesto de delante atrás: de perfil es una media luna ancha con sus dos puntas; de frente o de
    // espaldas, un abanico estrecho y alto.
    val r = Gatuno.R_CABEZA
    val c = p.cabeza + Offset(-0.3f * s, -4.2f)                                   // el centro de la base
    val medio = r * (0.62f + 0.78f * s)                                          // la mitad del ancho que se ve
    val alto = 6.6f - 0.6f * s
    val cae = 1.5f * s                                                           // cuánto bajan las dos puntas
    val tinta = luz.tono(N_TINTA); val tintaLuz = luz.tono(lerp(N_TINTA, N_TENUE, 0.75f)); val tintaOsc = luz.tono(lerp(N_TINTA, Color(0xFF070B18), 0.4f))
    // La sombra del ala sobre la frente y las orejas, recortada a la cabeza.
    clipPath(Path().apply { addOval(Rect(p.cabeza, r * 1.05f)) }) {
        drawOval(Color(0xFF0A0E1C).copy(alpha = 0.3f * (1f - 0.6f * luz.noche)), Offset(c.x - medio * 0.75f, c.y - 0.4f), Size(medio * 1.5f, 3.6f))
    }
    val sombrero = Path().apply {
        moveTo(c.x - medio, c.y + cae)
        cubicTo(c.x - medio * 0.55f, c.y + 0.6f, c.x - medio * 0.45f, c.y - alto * 0.55f, c.x - medio * 0.12f, c.y - alto * 0.95f)
        cubicTo(c.x - medio * 0.04f, c.y - alto * 1.02f, c.x + medio * 0.04f, c.y - alto * 1.02f, c.x + medio * 0.12f, c.y - alto * 0.95f)
        cubicTo(c.x + medio * 0.45f, c.y - alto * 0.55f, c.x + medio * 0.55f, c.y + 0.6f, c.x + medio, c.y + cae)
        cubicTo(c.x + medio * 0.6f, c.y + 0.9f, c.x + medio * 0.3f, c.y + 1.1f, c.x, c.y + 1.1f)
        cubicTo(c.x - medio * 0.3f, c.y + 1.1f, c.x - medio * 0.6f, c.y + 0.9f, c.x - medio, c.y + cae)
        close()
    }
    drawPath(sombrero, Brush.horizontalGradient(if (lado > 0f) listOf(tintaOsc, tinta, tintaLuz) else listOf(tintaLuz, tinta, tintaOsc), c.x - medio, c.x + medio))
    clipPath(sombrero) {
        // El fieltro: el ala de este lado, levantada, deja ver una franja más oscura abajo (la copa por dentro).
        drawPath(Path().apply { moveTo(c.x - medio, c.y + cae + 0.4f); cubicTo(c.x - medio * 0.4f, c.y - 0.6f, c.x + medio * 0.4f, c.y - 0.6f, c.x + medio, c.y + cae + 0.4f); lineTo(c.x + medio, c.y + 3f); lineTo(c.x - medio, c.y + 3f); close() }, tintaOsc.copy(alpha = 0.75f))
        // El brillo del fieltro, del lado de la luz.
        drawPath(Path().apply { moveTo(c.x + lado * medio * 0.15f, c.y - alto * 0.85f); cubicTo(c.x + lado * medio * 0.4f, c.y - alto * 0.5f, c.x + lado * medio * 0.55f, c.y - 1f, c.x + lado * medio * 0.75f, c.y) }, luz.tono(N_TINTA_LUZ).copy(alpha = 0.55f), style = Stroke(0.7f, cap = StrokeCap.Round))
    }
    // El ribete de oro por todo el canto de arriba.
    drawPath(Path().apply {
        moveTo(c.x - medio, c.y + cae)
        cubicTo(c.x - medio * 0.55f, c.y + 0.6f, c.x - medio * 0.45f, c.y - alto * 0.55f, c.x - medio * 0.12f, c.y - alto * 0.95f)
        cubicTo(c.x - medio * 0.04f, c.y - alto * 1.02f, c.x + medio * 0.04f, c.y - alto * 1.02f, c.x + medio * 0.12f, c.y - alto * 0.95f)
        cubicTo(c.x + medio * 0.45f, c.y - alto * 0.55f, c.x + medio * 0.55f, c.y + 0.6f, c.x + medio, c.y + cae)
    }, luz.tono(N_ORO), style = Stroke(0.55f, cap = StrokeCap.Round))
    // La presilla de oro y su botón, que suben por el ala de delante (de espaldas no se ven).
    if (co > -0.3f) {
        val x0 = c.x + medio * 0.34f * s
        val arriba = Offset(x0 + 0.2f * s, c.y - alto * 0.82f); val abajo = Offset(x0 + 0.5f * s, c.y + 0.4f)
        drawLine(luz.tono(N_ORO), abajo, arriba, 1.1f, StrokeCap.Round)
        drawLine(luz.tono(N_ORO_CLARO), abajo + Offset(-0.15f, 0f), arriba + Offset(-0.15f, 0f), 0.45f, StrokeCap.Round)
        drawCircle(luz.tono(N_ORO_CLARO), 0.85f, abajo + Offset(0f, -0.6f))
        drawCircle(N_ORO_LUZ.copy(alpha = 0.8f * (1f - 0.5f * luz.noche)), 0.3f, abajo + Offset(-0.25f * lado, -0.85f))
    }
    // Las dos puntas, un poco más claras en el canto.
    if (s > 0.5f) for (l in listOf(-1f, 1f)) drawCircle(luz.tono(N_TINTA_LUZ), 0.45f, Offset(c.x + l * medio * 0.97f, c.y + cae - 0.2f))
}

// ---- Mira por el catalejo (sentado).

private fun poseCatalejo(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Lo saca a la altura del pecho (como el de pixel), se lo lleva al ojo, barre el horizonte y lo baja.
    val alzado = listOf(0f, 0.6f, 1f, 1f, 1f, 0.5f)[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5f + listOf(0f, 0.2f, 0.4f, 0.7f, 0.9f, 0.3f)[q], -25.8f - 0.3f * alzado), morro = 0.2f - 0.12f * alzado + listOf(0f, 0f, 0f, -0.06f, -0.1f, 0f)[q], orejas = 1f,
        ojos = if (q == 3) 0.35f else 1f,
        manoC = Offset(8.6f + 2.4f * alzado, -11.4f - 9.4f * alzado), manoL = Offset(9.6f + 1.8f * alzado, -9.6f - 7f * alzado),
        cola2 = listOf(2.3f, 2.5f, 2.7f, 2.6f, 2.4f, 2.2f)[q],
    )
}

/** El catalejo de latón: tres tubos que se estrechan, el forro de cuero azul tinta del tubo grueso y el ocular de oro. */
private val catalejoDelGato: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val alzado = listOf(0f, 0.6f, 1f, 1f, 1f, 0.5f)[q]
    // Del ocular al objetivo: abajo, en el pecho, apunta al frente y arriba; en el ojo, casi horizontal y barriendo.
    val ojo = p.cabeza + Offset(4.4f, 0.3f)
    val pecho = p.manoC + Offset(-3.6f, 1.6f)
    val a = pecho + (ojo - pecho) * alzado
    val inclina = listOf(-0.42f, -0.3f, -0.08f, -0.14f, -0.02f, -0.25f)[q]
    val d = unidadN(Offset(1f, inclina)); val n = Offset(-d.y, d.x)
    val largo = 14.5f
    val b = a + d * largo
    val laton = luz.tono(N_ORO); val claro = luz.tono(N_ORO_CLARO); val osc = luz.tono(lerp(N_ORO, Color(0xFF3A2410), 0.45f))
    fun tubo(k0: Float, k1: Float, g0: Float, g1: Float, color: Color) {
        val p0 = a + d * (largo * k0); val p1 = a + d * (largo * k1)
        val forma = Path().apply { moveTo(p0.x + n.x * g0, p0.y + n.y * g0); lineTo(p1.x + n.x * g1, p1.y + n.y * g1); lineTo(p1.x - n.x * g1, p1.y - n.y * g1); lineTo(p0.x - n.x * g0, p0.y - n.y * g0); close() }
        drawPath(forma, color)
        // Luz arriba (del lado de −n, que apunta hacia arriba) y sombra abajo, a lo largo.
        drawLine(Color.White.copy(alpha = 0.35f * (1f - 0.5f * luz.noche)), p0 - n * (g0 * 0.45f), p1 - n * (g1 * 0.45f), 0.35f)
        drawLine(osc.copy(alpha = 0.6f), p0 + n * (g0 * 0.7f), p1 + n * (g1 * 0.7f), 0.4f)
    }
    // El ocular, el tubo fino, el mediano y el grueso (con su forro azul), y el parasol del objetivo.
    tubo(0f, 0.1f, 1.0f, 1.0f, claro)
    tubo(0.1f, 0.34f, 1.15f, 1.15f, laton)
    tubo(0.34f, 0.56f, 1.4f, 1.4f, laton)
    tubo(0.56f, 0.92f, 1.75f, 1.75f, luz.tono(N_TINTA))
    tubo(0.92f, 1f, 1.95f, 2.05f, laton)
    // Los anillos de cada empalme.
    for (k in listOf(0.1f, 0.34f, 0.56f, 0.92f)) { val o = a + d * (largo * k); val g = if (k < 0.3f) 1.3f else if (k < 0.5f) 1.6f else 1.95f; drawLine(claro, o + n * g, o - n * g, 0.55f); drawLine(osc.copy(alpha = 0.7f), o + n * g + d * 0.4f, o - n * g + d * 0.4f, 0.25f) }
    // El vidrio del objetivo, con un destello que se mueve mientras barre.
    val boca = b
    drawOval(luz.tono(Color(0xFF2B3A5C)), Offset(boca.x - 0.55f, boca.y - 1.8f), Size(1.1f, 3.6f))
    drawOval(Color(0xFFDDE8FF).copy(alpha = (0.35f + 0.4f * (if (q == 3) 1f else 0f)) * (1f - 0.4f * luz.noche)), Offset(boca.x - 0.25f, boca.y - 1.2f), Size(0.5f, 1.1f))
    // La mano que lo sostiene por el tubo grueso; con el catalejo en el ojo, la otra le ayuda junto al ocular.
    dedos(luz, a + d * (largo * 0.66f) + n * 0.4f)
    if (alzado > 0.8f) dedos(luz, a + d * (largo * 0.26f) + n * 0.9f, 1.05f)
}

// ---- Lee un despacho sellado (sentado).

private fun poseDespacho(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Con el despacho en las dos manos, la cabeza baja y va renglón por renglón; al final lo levanta un poco y parpadea.
    val renglon = listOf(0f, 0.3f, 0.6f, 1f, 0.5f, 0f)[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.4f + 0.5f * renglon, -25.4f + 0.7f * renglon), morro = 0.42f + 0.22f * renglon, orejas = if (q == 4) 0.85f else 1f,
        ojos = listOf(0.8f, 0.55f, 0.55f, 0.5f, 0.25f, 1f)[q],
        manoC = Offset(9.6f, -10.4f - 0.6f * (if (q == 5) 1f else 0f)), manoL = Offset(11.6f, -11.6f),
        cola2 = listOf(2.2f, 2.3f, 2.5f, 2.6f, 2.4f, 2.2f)[q],
    )
}

/** El despacho: un pliego crema con su doblez, cuatro renglones de tinta y el sello de lacre carmesí abajo. */
private val despacho: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val sube = if (q == 5) -0.6f else 0f
    // El pliego, un poco ladeado hacia el gato, con el borde de abajo apoyado en las manos.
    val o = Offset(p.manoC.x - 2.6f, p.manoC.y - 9.4f + sube)                     // la esquina de arriba, del lado del gato
    val ancho = 9.6f; val alto = 10.6f
    val esq = listOf(o, o + Offset(ancho, 0.9f), o + Offset(ancho - 0.5f, alto + 0.9f), o + Offset(-0.4f, alto))
    val hoja = Path().apply {
        moveTo(esq[0].x, esq[0].y)
        cubicTo(esq[0].x + 3f, esq[0].y - 0.4f, esq[1].x - 3f, esq[1].y - 0.2f, esq[1].x, esq[1].y)
        lineTo(esq[2].x, esq[2].y)
        cubicTo(esq[2].x - 3f, esq[2].y + 0.3f, esq[3].x + 3f, esq[3].y + 0.5f, esq[3].x, esq[3].y)
        close()
    }
    // La sombra que echa sobre el pecho.
    drawPath(Path().apply { addPath(hoja, Offset(-0.9f, 0.9f)) }, Color(0xFF1B2550).copy(alpha = 0.22f))
    drawPath(hoja, Brush.verticalGradient(listOf(luz.tono(N_PAPEL), luz.tono(lerp(N_PAPEL, N_PAPEL_OSC, 0.45f))), o.y, o.y + alto + 1f))
    clipPath(hoja) {
        // El doblez del medio (el despacho viajó doblado en tres).
        val y1 = o.y + alto * 0.36f; val y2 = o.y + alto * 0.68f
        for (y in listOf(y1, y2)) { drawLine(luz.tono(N_PAPEL_OSC), Offset(o.x - 1f, y), Offset(o.x + ancho + 1f, y + 0.9f), 0.5f); drawLine(Color.White.copy(alpha = 0.5f), Offset(o.x - 1f, y + 0.4f), Offset(o.x + ancho + 1f, y + 1.3f), 0.3f) }
        // Los renglones: trazos de tinta que suben y bajan un poco, más cortos el último de cada párrafo.
        val largos = listOf(0.86f, 0.78f, 0.84f, 0.5f)
        for ((i, l) in largos.withIndex()) {
            val y = o.y + 1.8f + i * 1.75f + if (i == 3) 0.5f else 0f
            val x0 = o.x + 1.2f + if (i == 0) 0.8f else 0f
            val x1 = o.x + 1.2f + (ancho - 2.2f) * l
            val renglon = Path().apply {
                moveTo(x0, y + (x0 - o.x) * 0.09f)
                var x = x0; var k = 0
                while (x < x1) { val x2 = minOf(x + 1.3f, x1); cubicTo(x + 0.4f, y - 0.35f + (x - o.x) * 0.09f, x2 - 0.4f, y + (if (k % 2 == 0) 0.35f else 0.1f) + (x2 - o.x) * 0.09f, x2, y + (x2 - o.x) * 0.09f); x = x2; k++ }
            }
            drawPath(renglon, luz.tono(N_TINTA).copy(alpha = 0.78f), style = Stroke(0.36f, cap = StrokeCap.Round))
        }
        // Una rúbrica corta abajo, a la derecha.
        val ry = o.y + alto * 0.84f; val rx = o.x + ancho * 0.52f
        drawPath(Path().apply { moveTo(rx, ry); cubicTo(rx + 0.8f, ry - 1.2f, rx + 1.4f, ry + 0.8f, rx + 2.2f, ry - 0.3f); cubicTo(rx + 2.8f, ry - 1f, rx + 3.4f, ry + 0.7f, rx + 3.9f, ry + 0.3f) }, luz.tono(N_TINTA).copy(alpha = 0.7f), style = Stroke(0.3f, cap = StrokeCap.Round))
    }
    drawPath(hoja, luz.tono(N_PAPEL_OSC), style = Stroke(0.3f))
    // El sello de lacre, abajo a la izquierda, con su cinta carmesí que asoma y la marca en relieve.
    val sello = o + Offset(2.4f, alto - 1.6f)
    drawPath(Path().apply { moveTo(sello.x - 0.5f, sello.y + 1f); lineTo(sello.x - 1.4f, sello.y + 3.6f); lineTo(sello.x - 0.6f, sello.y + 3.1f); lineTo(sello.x - 0.1f, sello.y + 3.9f); lineTo(sello.x + 0.4f, sello.y + 1.2f); close() }, luz.tono(N_CARMESI_OSC))
    val gotas = listOf(Offset(0f, 0f) to 1.75f, Offset(1.1f, 0.6f) to 0.9f, Offset(-1.2f, 0.5f) to 0.8f, Offset(0.3f, -1.3f) to 0.75f)
    for ((d, rr) in gotas) drawCircle(luz.tono(N_CARMESI), rr, sello + d)
    drawCircle(luz.tono(N_CARMESI_OSC), 1.05f, sello, style = Stroke(0.3f))
    drawCircle(Color(0xFFFFB0B8).copy(alpha = 0.45f * (1f - 0.5f * luz.noche)), 0.35f, sello + Offset(-0.6f, -0.6f))
    // Las manos que lo sostienen por el borde de abajo.
    dedos(luz, esq[3] + (esq[2] - esq[3]) * 0.22f + Offset(0f, -0.3f))
    dedos(luz, esq[3] + (esq[2] - esq[3]) * 0.78f + Offset(0f, -0.2f), 1.1f)
    // Al pasar la vista por el último renglón, el borde de arriba se dobla un poco por su peso.
    if (q == 3 || q == 4) drawPath(Path().apply { moveTo(esq[1].x - 2.2f, esq[1].y - 0.2f); cubicTo(esq[1].x - 1.2f, esq[1].y + 0.6f, esq[1].x - 0.4f, esq[1].y + 1.2f, esq[1].x, esq[1].y + 2.1f); lineTo(esq[1].x, esq[1].y); close() }, luz.tono(lerp(N_PAPEL, N_PAPEL_OSC, 0.7f)))
    if (abs(sube) > 0f) drawLine(Color.White.copy(alpha = 0.3f), esq[0], esq[1], 0.3f)
}

/** El gato de Napoleónico. */
internal val PELAJE_NAPOLEON = Pelaje(
    manto = N_MANTO, claro = N_CLARO, marca = N_SOMBRA, dibujo = DibujoPelaje.LISO, ojos = N_OJOS,
    nariz = Color(0xFFE296A0), collar = N_CARMESI, accesorio = puestoNapoleon,
    especiales = listOf(EspecialGato("mira por el catalejo", true, 6, ::poseCatalejo, catalejoDelGato), EspecialGato("lee un despacho sellado", true, 6, ::poseDespacho, despacho)),
)
