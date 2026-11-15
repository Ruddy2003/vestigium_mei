package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Botánico: atigrado beige tostado, con su collar verde musgo de joya verde escarabajo y su salacot crema
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/botanico_*`, `herramientas/gato/gato8.py`): atigrado, de manto beige
// tostado (#BE9D7E, entre el papel del herbario y el óxido) con rayas pardas (#937051), pecho y vientre de papel
// (#F7F2E4), ojos verde musgo (#69844B), un collar verde musgo (#3F5A2C) con una joya verde escarabajo (#2E5E5A) y un
// salacot crema (#EAE2D0) con su cinta verde musgo. Sus dos acciones propias, las mismas que el de pixel
// (`gato17.py`): oler una flor (de pie; pétalos terracota #C9604A, centro amarillo #E0B04F, tallo musgo) y seguir con
// la vista a una mariposa (sentado; alas de arriba naranjas #D9772E, las de abajo verde escarabajo).

private val B_MANTO = Color(0xFFBE9D7E)
private val B_RAYA = Color(0xFF937051)
private val B_CLARO = Color(0xFFF7F2E4)
private val B_OJOS = Color(0xFF69844B)
private val B_MUSGO = Color(0xFF3F5A2C)
private val B_MUSGO_LUZ = Color(0xFF6E8C48)
private val B_ESCARABAJO = Color(0xFF2E5E5A)
private val B_ESCARABAJO_LUZ = Color(0xFF6FC2B0)
private val B_SALACOT = Color(0xFFEAE2D0)
private val B_SALACOT_OSC = Color(0xFFB8A988)
private val B_SALACOT_LUZ = Color(0xFFFBF8EF)
private val B_TINTA = Color(0xFF2A2318)
private val B_PETALO = Color(0xFFC9604A)
private val B_PETALO_LUZ = Color(0xFFE08A6E)
private val B_CENTRO = Color(0xFFE0B04F)
private val B_ALA = Color(0xFFD9772E)
private val B_ALA_LUZ = Color(0xFFF0A25A)

private fun unidadB(v: Offset, siNo: Offset = Offset(1f, 0f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** La joya verde escarabajo del collar, por delante, y el salacot con su cinta y la sombra que echa su ala. */
private val puestoBotanico: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f)
    val lado = if (luz.rayo.lat >= 0f) 1f else -1f
    // ---- La joya: un escarabajo de piedra verde, ovalado, con su raya y su brillo metálico.
    if (p.giro >= -0.25f) {
        val cuello = unidadB(p.cabeza - p.hombro)
        val sitio = p.cuello - cuello * 2.8f + Offset(1.4f * s, 1.9f)
        drawOval(luz.tono(B_ESCARABAJO), Offset(sitio.x - 1f, sitio.y - 1.2f), Size(2f, 2.6f))
        drawLine(luz.tono(lerp(B_ESCARABAJO, B_TINTA, 0.5f)), sitio + Offset(0f, -1f), sitio + Offset(0f, 1.3f), 0.2f)
        drawOval(B_ESCARABAJO_LUZ.copy(alpha = 0.75f * (1f - 0.5f * luz.noche)), Offset(sitio.x - 0.75f, sitio.y - 0.8f), Size(0.55f, 1.2f))
    }
    // ---- El salacot: la copa en media bola, la cinta musgo en su base, el ala ancha alrededor y un botón arriba.
    val r = Gatuno.R_CABEZA
    val c = p.cabeza + Offset(-0.5f * s, -4.6f)                                   // el centro del ala
    val anchoAla = r * 1.32f; val altoAla = 1.9f
    val anchoCopa = r * 0.86f; val altoCopa = 6.4f
    val claro = luz.tono(B_SALACOT_LUZ); val medio = luz.tono(B_SALACOT); val osc = luz.tono(B_SALACOT_OSC)
    val degradado = { a: Float, b: Float -> Brush.horizontalGradient(if (lado > 0f) listOf(osc, medio, claro) else listOf(claro, medio, osc), a, b) }
    // La sombra del ala en la frente y las orejas: recortada a la cabeza.
    clipPath(Path().apply { addOval(androidx.compose.ui.geometry.Rect(p.cabeza, r * 1.05f)) }) {
        drawOval(Color(0xFF1E1810).copy(alpha = 0.26f * (1f - 0.6f * luz.noche)), Offset(c.x - anchoAla * 0.8f, c.y - 0.2f), Size(anchoAla * 1.6f, 4.2f))
    }
    // La parte de atrás del ala (se ve por debajo, más oscura).
    drawOval(luz.tono(lerp(B_SALACOT_OSC, B_TINTA, 0.2f)), Offset(c.x - anchoAla, c.y - altoAla * 0.5f), Size(anchoAla * 2f, altoAla * 1.25f))
    // La copa.
    val copa = Path().apply {
        moveTo(c.x - anchoCopa, c.y)
        cubicTo(c.x - anchoCopa, c.y - altoCopa * 0.75f, c.x - anchoCopa * 0.55f, c.y - altoCopa, c.x, c.y - altoCopa)
        cubicTo(c.x + anchoCopa * 0.55f, c.y - altoCopa, c.x + anchoCopa, c.y - altoCopa * 0.75f, c.x + anchoCopa, c.y)
        close()
    }
    drawPath(copa, degradado(c.x - anchoCopa, c.x + anchoCopa))
    // Las costuras de los gajos de la copa, que bajan del botón.
    for (k in listOf(-0.5f, 0f, 0.5f)) {
        val abajo = Offset(c.x + k * anchoCopa * 1.1f, c.y - 1.6f)
        drawPath(Path().apply { moveTo(c.x, c.y - altoCopa + 0.3f); cubicTo(c.x + k * anchoCopa * 0.6f, c.y - altoCopa + 0.6f, abajo.x, abajo.y - 2f, abajo.x, abajo.y) }, osc.copy(alpha = 0.45f), style = Stroke(0.22f))
    }
    // El brillo de la copa, del lado de la luz.
    drawOval(claro.copy(alpha = 0.6f), Offset(c.x + lado * anchoCopa * 0.25f - 0.7f, c.y - altoCopa * 0.82f), Size(1.4f, altoCopa * 0.42f))
    // La cinta musgo, con su lazo atrás.
    val cinta = Path().apply {
        moveTo(c.x - anchoCopa, c.y - 0.2f); lineTo(c.x - anchoCopa * 0.98f, c.y - 1.6f)
        cubicTo(c.x - anchoCopa * 0.5f, c.y - 1.95f, c.x + anchoCopa * 0.5f, c.y - 1.95f, c.x + anchoCopa * 0.98f, c.y - 1.6f)
        lineTo(c.x + anchoCopa, c.y - 0.2f); close()
    }
    drawPath(cinta, luz.tono(B_MUSGO))
    drawLine(luz.tono(B_MUSGO_LUZ).copy(alpha = 0.7f), Offset(c.x - anchoCopa * 0.9f, c.y - 1.5f), Offset(c.x + anchoCopa * 0.9f, c.y - 1.5f), 0.25f)
    val nudo = Offset(c.x - anchoCopa * 0.82f * (if (abs(p.giro) < 0.75f) 1f else 0f), c.y - 0.9f)
    if (abs(p.giro) < 0.75f) {
        drawPath(Path().apply { moveTo(nudo.x, nudo.y); lineTo(nudo.x - 1.6f, nudo.y + 2.2f); lineTo(nudo.x - 0.7f, nudo.y + 2.4f); close() }, luz.tono(B_MUSGO))
        drawPath(Path().apply { moveTo(nudo.x, nudo.y); lineTo(nudo.x - 0.6f, nudo.y + 2.6f); lineTo(nudo.x + 0.3f, nudo.y + 2.5f); close() }, luz.tono(lerp(B_MUSGO, B_TINTA, 0.3f)))
    }
    // El ala de delante: la mitad que se ve por encima, con su canto.
    val ala = Path().apply {
        moveTo(c.x - anchoAla, c.y)
        cubicTo(c.x - anchoAla, c.y + altoAla * 0.62f, c.x - anchoAla * 0.5f, c.y + altoAla, c.x, c.y + altoAla)
        cubicTo(c.x + anchoAla * 0.5f, c.y + altoAla, c.x + anchoAla, c.y + altoAla * 0.62f, c.x + anchoAla, c.y)
        cubicTo(c.x + anchoAla * 0.8f, c.y + altoAla * 0.2f, c.x + anchoCopa, c.y + 0.3f, c.x + anchoCopa, c.y)
        lineTo(c.x - anchoCopa, c.y)
        cubicTo(c.x - anchoCopa, c.y + 0.3f, c.x - anchoAla * 0.8f, c.y + altoAla * 0.2f, c.x - anchoAla, c.y)
        close()
    }
    drawPath(ala, degradado(c.x - anchoAla, c.x + anchoAla))
    drawPath(Path().apply { moveTo(c.x - anchoAla, c.y); cubicTo(c.x - anchoAla, c.y + altoAla * 0.62f, c.x - anchoAla * 0.5f, c.y + altoAla, c.x, c.y + altoAla); cubicTo(c.x + anchoAla * 0.5f, c.y + altoAla, c.x + anchoAla, c.y + altoAla * 0.62f, c.x + anchoAla, c.y) }, osc, style = Stroke(0.35f, cap = StrokeCap.Round))
    // El botón de ventilación de lo alto.
    drawOval(medio, Offset(c.x - 0.9f, c.y - altoCopa - 0.7f), Size(1.8f, 1f))
    drawOval(osc, Offset(c.x - 0.9f, c.y - altoCopa - 0.7f), Size(1.8f, 1f), style = Stroke(0.2f))
}

// ---- Huele una flor (de pie).

private fun poseFlor(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // De pie, estira el cuello hacia la flor, la huele con los ojos entornados, se aparta un poco y vuelve.
    val baja = listOf(0f, 0.6f, 1f, 1f, 0.45f, 0.75f)[q]
    return Gatuno.DE_PIE.copy(
        hombro = Gatuno.DE_PIE.hombro + Offset(0.6f * baja, 1.2f * baja),
        cabeza = Offset(14.4f + 2.6f * baja, -21.6f + 5.6f * baja), morro = 0.08f + 0.22f * baja, orejas = 0.95f,
        ojos = listOf(1f, 0.7f, 0.25f, 0.15f, 0.8f, 0.4f)[q], cola0 = 0.6f, cola1 = 0.5f,
        cola2 = listOf(0.9f, 1.1f, 1.4f, 1.6f, 1.3f, 1f)[q],
    )
}

/** La flor: tallo musgo con su hoja, cinco pétalos terracota y el centro amarillo; al olerla suelta polen y un pétalo. */
private val flor: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val mece = listOf(0f, 0.2f, 0.5f, 0.7f, 0.4f, 0.1f)[q]
    val pie = Offset(24.2f, 0f)
    val cabeza = Offset(23.6f + mece, -13.4f)
    val tallo = luz.tono(B_MUSGO); val hoja = luz.tono(B_MUSGO_LUZ)
    drawOval(Color.Black.copy(alpha = 0.18f), Offset(pie.x - 3f, -0.7f), Size(6f, 1.4f))
    drawPath(Path().apply { moveTo(pie.x, pie.y); cubicTo(pie.x + 0.6f, pie.y - 4f, cabeza.x - 0.8f, cabeza.y + 6f, cabeza.x, cabeza.y + 1f) }, tallo, style = Stroke(0.75f, cap = StrokeCap.Round))
    // Dos hojas, una a cada lado del tallo.
    for ((base, punta) in listOf(Offset(24.4f, -3.2f) to Offset(28.2f, -6.4f), Offset(24f, -5.6f) to Offset(20.8f, -8.2f))) {
        val d = punta - base; val n = unidadB(Offset(-d.y, d.x)) * 1.1f
        drawPath(Path().apply { moveTo(base.x, base.y); cubicTo(base.x + d.x * 0.3f + n.x, base.y + d.y * 0.3f + n.y, base.x + d.x * 0.75f + n.x, base.y + d.y * 0.75f + n.y, punta.x, punta.y); cubicTo(base.x + d.x * 0.75f - n.x, base.y + d.y * 0.75f - n.y, base.x + d.x * 0.3f - n.x, base.y + d.y * 0.3f - n.y, base.x, base.y); close() }, if (punta.x > base.x) hoja else tallo)
        drawLine(tallo.copy(alpha = 0.7f), base, base + d * 0.85f, 0.2f)
    }
    // Los pétalos, en una corola un poco vuelta hacia el gato.
    val petalo = luz.tono(B_PETALO); val petaloLuz = luz.tono(B_PETALO_LUZ)
    for (i in 0 until 5) {
        if (i == 4 && q >= 3) continue                                        // el que se le cae
        val a = (-PI / 2 + i * 2 * PI / 5 + 0.25).toFloat()
        val dir = Offset(cos(a) * 0.8f, sin(a))
        val punta = cabeza + dir * 3f
        val n = Offset(-dir.y, dir.x) * 1.3f
        drawPath(Path().apply { moveTo(cabeza.x, cabeza.y); cubicTo(cabeza.x + dir.x * 1.2f + n.x, cabeza.y + dir.y * 1.2f + n.y, punta.x + n.x * 0.8f, punta.y + n.y * 0.8f, punta.x, punta.y); cubicTo(punta.x - n.x * 0.8f, punta.y - n.y * 0.8f, cabeza.x + dir.x * 1.2f - n.x, cabeza.y + dir.y * 1.2f - n.y, cabeza.x, cabeza.y); close() }, if (i % 2 == 0) petalo else petaloLuz)
    }
    drawCircle(luz.tono(B_CENTRO), 1.1f, cabeza)
    drawCircle(luz.tono(lerp(B_CENTRO, B_TINTA, 0.35f)), 0.45f, cabeza + Offset(0.3f, 0.3f))
    // El polen que se levanta cuando le toca la nariz.
    if (q == 2 || q == 3) for (k in 0..3) drawCircle(luz.tono(B_CENTRO).copy(alpha = 0.8f - 0.15f * k), 0.28f, cabeza + Offset(-1.4f - k * 0.9f, -1.6f - (k % 2) * 0.9f - (q - 2) * 0.8f))
    // El pétalo que cae, dando vueltas.
    if (q >= 3) {
        val caida = (q - 3) / 2f
        val o = cabeza + Offset(1.6f + 1.8f * caida, 3f + 7.6f * caida)
        rotate(40f + 110f * caida, o) { drawOval(petalo, Offset(o.x - 1.3f, o.y - 0.6f), Size(2.6f, 1.2f)) }
    }
}

// ---- Sigue a una mariposa (sentado).

private fun poseMariposa(c: Int): Pose {
    val q = Math.floorMod(c, 8)
    // Sentado, la sigue con la cabeza de un lado a otro; cuando pasa cerca, levanta la mano.
    val gira = listOf(0.1f, 0.35f, 0.65f, 0.85f, 0.7f, 0.4f, 0.15f, 0f)[q]
    val alto = listOf(0.3f, 0.6f, 0.9f, 0.7f, 0.4f, 0.2f, 0.5f, 0.4f)[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.6f + 1.4f * gira, -26.2f - 0.8f * alto), morro = -0.25f - 0.35f * alto, orejas = 1f,
        ojos = if (q == 6) 0.3f else 1f, cara = 0.15f + 0.5f * gira,
        manoC = if (q == 3) Offset(9.4f, -22f) else if (q == 4) Offset(8f, -12f) else Gatuno.SENTADO.manoC,
        cola2 = listOf(2.2f, 2.4f, 2.6f, 2.8f, 2.7f, 2.5f, 2.3f, 2.2f)[q],
    )
}

/** La mariposa: da vueltas delante del gato, aleteando; alas de arriba naranjas de punta oscura, las de abajo verde escarabajo. */
private val mariposa: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 8)
    val a = (2 * PI * q / 8).toFloat()
    val o = Offset(20f + 6.4f * cos(a), -39f + 3.4f * sin(a) + 2f * sin(2 * a))
    val abre = listOf(1f, 0.35f, 0.9f, 0.2f, 1f, 0.45f, 0.85f, 0.25f)[q]                // cuánto se ven las alas abiertas
    val vuelta = if (sin(a) > 0f) -1f else 1f
    val naranja = luz.tono(B_ALA); val naranjaLuz = luz.tono(B_ALA_LUZ); val verde = luz.tono(B_ESCARABAJO); val tinta = luz.tono(B_TINTA)
    for (lado in listOf(-1f, 1f)) {
        val w = lado * abre
        // El ala de abajo, verde escarabajo, redonda, con una cola corta.
        drawPath(Path().apply {
            moveTo(o.x, o.y + 0.2f)
            cubicTo(o.x + w * 2.6f, o.y + 0.2f, o.x + w * 3f, o.y + 2.6f, o.x + w * 1.4f, o.y + 3.2f)
            lineTo(o.x + w * 0.9f, o.y + 4.2f); lineTo(o.x + w * 0.4f, o.y + 2.6f); close()
        }, verde)
        // El ala de arriba, naranja, grande, de punta oscura con dos motas claras.
        val ala = Path().apply {
            moveTo(o.x, o.y - 0.2f)
            cubicTo(o.x + w * 1.6f, o.y - 3.6f, o.x + w * 4.4f, o.y - 4.4f, o.x + w * 4.6f, o.y - 3f)
            cubicTo(o.x + w * 4.6f, o.y - 1.2f, o.x + w * 2.6f, o.y + 0.4f, o.x, o.y + 0.4f); close()
        }
        drawPath(ala, if (lado == vuelta) naranjaLuz else naranja)
        if (abre > 0.5f) {
            clipPath(ala) { drawCircle(tinta, 1.7f * abre, Offset(o.x + w * 4.6f, o.y - 3.6f)) }
            drawCircle(luz.tono(B_SALACOT_LUZ), 0.3f, Offset(o.x + w * 3.6f, o.y - 3.2f))
            drawCircle(luz.tono(B_SALACOT_LUZ), 0.25f, Offset(o.x + w * 4.2f, o.y - 2.4f))
        }
    }
    // El cuerpo y las antenas.
    drawLine(tinta, o + Offset(0f, -1.1f), o + Offset(0f, 2.2f), 0.7f, StrokeCap.Round)
    for (lado in listOf(-1f, 1f)) drawLine(tinta, o + Offset(0f, -1.1f), o + Offset(lado * 1.2f, -3f), 0.2f, StrokeCap.Round)
}

/** El gato de Botánico. */
internal val PELAJE_BOTANICO = Pelaje(
    manto = B_MANTO, claro = B_CLARO, marca = B_RAYA, dibujo = DibujoPelaje.ATIGRADO, ojos = B_OJOS,
    nariz = Color(0xFFE296A0), collar = B_MUSGO, accesorio = puestoBotanico,
    especiales = listOf(EspecialGato("huele una flor", false, 6, ::poseFlor, flor), EspecialGato("sigue a una mariposa", true, 8, ::poseMariposa, mariposa)),
)
