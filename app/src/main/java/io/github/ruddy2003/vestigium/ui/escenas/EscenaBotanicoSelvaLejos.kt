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
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico.H
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.bajio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * Lo lejano de la selva: el cielo; la montaña de detrás de la casa, cubierta de selva en planos con la bruma entre ellos
 * (la cara de granito con su cascada, los árboles que sobresalen con la copa en pisos, las embaúbas plateadas, los
 * árboles en flor, las palmeras), y los rayos de sol que bajan entre las copas y encienden trozos de follaje; y, por el
 * claro de la derecha, la bahía como un espejo con la otra orilla, el Pan de Azúcar y el morro de al lado con su
 * reflejo, la playa en curva y el bergantín fondeado cuando está.
 */
internal object LejosSelva {
    private val C = ColorBotanico

    /** La cresta de la montaña (sube por la izquierda y se sale del cuadro; baja a la bahía por la derecha). */
    private val CRESTA = listOf(
        Offset(-14f, -50f), Offset(26f, -38f), Offset(60f, -52f), Offset(96f, -30f), Offset(124f, -22f), Offset(146f, -30f),
        Offset(170f, -14f), Offset(188f, 6f), Offset(200f, 12f), Offset(214f, 10f), Offset(228f, 18f), Offset(240f, 38f),
        Offset(252f, 62f), Offset(262f, 82f), Offset(272f, 98f), Offset(282f, 108f), Offset(294f, 115f), Offset(306f, 119.5f),
    )

    /** La orilla de la playa (de la izquierda, lejos, a la derecha, cerca): el agua llega hasta aquí. */
    val PLAYA = listOf(Offset(262f, 150f), Offset(282f, 153f), Offset(300f, 156.5f), Offset(318f, 158f), Offset(336f, 161f), Offset(352f, 165f), Offset(368f, 168f), Offset(386f, 173f))

    fun DrawScope.lejos(m: Momento, luz: LuzSelva.Luz) {
        val l = luz.l; val semilla = m.ficha.semilla
        cielo(l, H, semilla); astros(l, H, semilla); nubes(l, H, m.ficha.clima, semilla)
        bahia(m, luz)
        montana(m, luz)
    }

    // ================================================================ La bahía

    private fun DrawScope.bahia(m: Momento, luz: LuzSelva.Luz) {
        val l = luz.l; val p = luz.p; val az = Azar(Semilla.de("botanico", "bahia"))
        // La otra orilla: lomas bajas y azules, con la ciudad blanca al pie (de noche, sus luces).
        val orilla = perfil(az, 240f, ANCHO + 10f, H + 0.5f, 8f, 0.45f, 22)
        monte(l, orilla, H + 1.5f, lerp(C.SELVA, Color(0xFF7A90A8), 0.62f), 0.86f)
        for (i in 0..9) { val x = 296f + i * 8.5f + az.entre(-2f, 2f); val y = H - 0.6f + az.entre(-0.4f, 0.3f); drawRect(l.tono(lerp(C.CAL, C.ARENA, 0.2f), 0.75f), Offset(x, y - 1.2f), Size(az.entre(1.5f, 3f), 1.4f))
            if (l.noche > 0.5f && az.real() > 0.4f) drawCircle(C.LUMBRE.copy(alpha = 0.7f * l.noche), 0.5f, Offset(x + 1f, y - 0.5f)) }
        // Los morros de detrás, del otro lado de la boca de la bahía: pálidos y azules, con la bruma a sus pies.
        val tras = listOf(Offset(300f, H + 1f), Offset(318f, 108f), Offset(334f, 104f), Offset(350f, 99f), Offset(366f, 97.5f), Offset(380f, 101f), Offset(392f, 104f))
        monte(l, tras, H + 1.5f, lerp(C.SELVA, Color(0xFF8098B4), 0.7f), 0.92f)
        mancha(Offset(352f, H - 2f), 46f, 4f, lerp(l.bruma, l.tono(Color.White), 0.4f), 0.6f)
        // El morro de al lado (la Urca) y el Pan de Azúcar: granito con su cara de luz y su cara de sombra.
        morro(p, Offset(326f, H + 1f), 28f, 17f, 0.66f, az)
        panDeAzucar(p, Offset(SelvaBotanico.PAN_DE_AZUCAR.x, H + 1f), 48f, H + 1f - SelvaBotanico.PAN_DE_AZUCAR.y, 0.5f, az)
        // La bahía: quieta como un espejo, con lo que tiene encima reflejado, hasta la playa.
        val zona = Rect(236f, H, ANCHO, 176f)
        val contorno = poligono(listOf(Offset(236f, H), Offset(ANCHO, H), Offset(ANCHO, 176f)) + PLAYA.reversed())
        val viento = 0.08f + 0.5f * m.viento
        agua(l, zona, Azar(Semilla.de("botanico", "agua", m.ficha.dia)), propio = lerp(C.MAR, C.ORILLA, 0.12f), viento = viento, contorno = contorno, hondo = C.MAR_HONDO, orilla = C.ORILLA)
        clipPath(contorno) {
            val ref = Azar(Semilla.de("botanico", "reflejos"))
            // «Como un espejo»: el Pan de Azúcar del revés, con sus dos caras y sus chorreras, roto por las ondas.
            val k = 1f - 0.6f * viento
            reflejoDelPan(p, Offset(SelvaBotanico.PAN_DE_AZUCAR.x, H + 1f), 48f, H + 1f - SelvaBotanico.PAN_DE_AZUCAR.y, 0.5f, k)
            reflejoRoto(314f, 338f, H + 1.2f, 14f, l.tono(lerp(C.ROCA, C.SELVA, 0.4f), 0.6f), 0.5f * k, ref, 0.7f)
            reflejoRoto(236f, ANCHO, H + 1f, 6f, l.tono(lerp(C.SELVA, Color(0xFF7A90A8), 0.5f), 0.7f), 0.35f * k, ref, 0.7f)
            // La montaña también se refleja donde baja al agua.
            reflejoRoto(236f, 300f, H + 1f, 22f, l.tono(C.SELVA_OSC, 0.4f), 0.45f * k, ref, 0.8f)
        }
        estela(l, zona, Azar(Semilla.de("botanico", "estela bahia")))
        if (EscenaBotanico.bergantinEnLaBahia(m)) with(PiezasBotanico) {
            clipPath(Path().apply { addRect(Rect(0f, 0f, ANCHO, 176f)) }) {
                bergantinLejano(p, SelvaBotanico.BERGANTIN, 26f, -1f, 0.5f, Azar(Semilla.de("botanico", "bergantin lejano")), farol = if (l.noche > 0.3f) l.noche else 0f)
            }
        }
        // La playa: del agua a la arena seca, la franja de paso (el agua clara del bajío, la espuma, la arena mojada).
        bajio(l, curva(PLAYA), 8f, C.ORILLA)
        val seca = PLAYA.map { it + Offset(0f, 7f + (it.y - 150f) * 0.35f) }
        val mojada = PLAYA.map { it + Offset(0f, 2.2f) }
        drawPath(poligono(PLAYA + seca.reversed()), Brush.verticalGradient(listOf(l.tono(lerp(C.ARENA, C.TEJA, 0.06f), 0.3f), l.tono(C.ARENA, 0.15f)), 150f, 185f))
        drawPath(poligono(PLAYA + mojada.reversed()), l.tono(lerp(C.ARENA, C.MADERA, 0.35f), 0.25f).copy(alpha = 0.7f))
        drawPath(curva(PLAYA), l.tono(Color.White, 0.2f).copy(alpha = 0.8f * (1f - 0.5f * l.noche)), style = Stroke(1.1f, cap = StrokeCap.Round))
        drawPath(curva(PLAYA.map { it + Offset(-4f, -2f) }), l.tono(Color.White, 0.25f).copy(alpha = 0.35f * (1f - 0.5f * l.noche)), style = Stroke(0.7f, cap = StrokeCap.Round))
        // Una canoa varada en la arena, con su sombra.
        val c = Offset(330f, 165f)
        drawPath(Path().apply { moveTo(c + Offset(-11f, 0f)); cubicTo(c + Offset(-6f, 2.6f), c + Offset(6f, 2.6f), c + Offset(11f, -0.6f)); lineTo(c + Offset(10f, -1.6f)); cubicTo(c + Offset(5f, -0.6f), c + Offset(-5f, -0.6f), c + Offset(-11f, -1.4f)); close() }, p.por(C.MADERA_OSC, 0.2f, 0.2f))
        drawOval(luz.r.color.copy(alpha = 0.3f * luz.r.fuerza), Offset(c.x - 9f, c.y + 1.4f), Size(18f, 2f), blendMode = BlendMode.Multiply)
    }

    /** El morro de al lado (la Urca): redondo y bajo, cubierto de selva, con lajas de granito que asoman. */
    private fun DrawScope.morro(p: Pincel, pie: Offset, ancho: Float, alto: Float, lejos: Float, az: Azar) {
        val l = p.l; val cx = pie.x; val base = pie.y; val lado = if (p.solALaDerecha) 1f else -1f
        val pts = listOf(Offset(cx - ancho / 2f, base), Offset(cx - ancho * 0.4f, base - alto * 0.5f), Offset(cx - ancho * 0.22f, base - alto * 0.88f), Offset(cx - ancho * 0.02f, base - alto),
            Offset(cx + ancho * 0.18f, base - alto * 0.94f), Offset(cx + ancho * 0.36f, base - alto * 0.62f), Offset(cx + ancho / 2f, base))
        val forma = curva(pts, base + 1f)
        val verde = lerp(C.SELVA, C.HOJA, 0.12f)
        drawPath(forma, Brush.horizontalGradient(listOf(l.tono(lerp(verde, Color(0xFF4A4C7A), 0.35f), lejos), l.tono(verde, lejos), l.tono(lerp(verde, C.HOJA_LUZ, 0.3f * p.r.dia), lejos)), cx - lado * ancho / 2f, cx + lado * ancho / 2f))
        clipPath(forma) {
            // Las lajas de granito: una cara lisa y pálida del lado del mar, con su chorrera.
            val roca = lerp(C.ROCA, C.TEJA, 0.1f)
            val laja = curva(listOf(Offset(cx + ancho * 0.05f, base - alto * 0.95f), Offset(cx + ancho * 0.2f, base - alto * 0.8f), Offset(cx + ancho * 0.3f, base - alto * 0.45f), Offset(cx + ancho * 0.12f, base - alto * 0.4f), Offset(cx, base - alto * 0.7f), Offset(cx + ancho * 0.05f, base - alto * 0.95f)))
            drawPath(laja, l.tono(lerp(roca, if (lado > 0f) Color(0xFFFFE6C4) else Color(0xFF4A4C7A), 0.3f), lejos))
            drawLine(l.tono(lerp(roca, C.BREA, 0.4f), lejos).copy(alpha = 0.4f), Offset(cx + ancho * 0.14f, base - alto * 0.85f), Offset(cx + ancho * 0.18f, base - alto * 0.45f), 0.8f)
            // El rizo de las copas, en puntos, con su lado de luz.
            val copas = ArrayList<Offset>(); val luces = ArrayList<Offset>()
            repeat(40) { val x = cx + az.entre(-0.5f, 0.5f) * ancho; val y = base - alto * az.entre(0f, 0.9f); copas += Offset(x, y); if (az.real() < 0.5f) luces += Offset(x + lado * 0.8f, y - 0.8f) }
            drawPoints(copas, PointMode.Points, l.tono(lerp(C.SELVA, C.SELVA_OSC, 0.4f), lejos), 2.6f, StrokeCap.Round)
            drawPoints(luces, PointMode.Points, l.tono(lerp(C.HOJA, C.HOJA_LUZ, 0.3f), lejos).copy(alpha = 0.3f + 0.5f * p.r.dia), 1.4f, StrokeCap.Round)
            if (l.noche > 0.6f && l.nublado < 0.5f) drawRect(Brush.horizontalGradient(listOf(EscenaBotanico.LUZ_DE_LUNA.copy(alpha = 0.25f * l.noche), Color.Transparent), cx + ancho / 2f, cx), Offset(cx - ancho / 2f, base - alto), Size(ancho, alto))
        }
    }

    /** El contorno del Pan de Azúcar (de izquierda a derecha, en fracciones de su ancho y su alto): el hombro con su repisa a la izquierda, la cara casi vertical del mar a la derecha. **[Aproximación]**. */
    private val PAN = listOf(-0.5f to 0f, -0.47f to 0.1f, -0.42f to 0.26f, -0.36f to 0.38f, -0.29f to 0.44f, -0.22f to 0.48f, -0.18f to 0.6f, -0.14f to 0.76f, -0.08f to 0.91f, 0f to 0.99f, 0.07f to 1f, 0.15f to 0.95f, 0.23f to 0.82f, 0.3f to 0.62f, 0.35f to 0.4f, 0.4f to 0.2f, 0.45f to 0.07f, 0.5f to 0f)

    /**
     * El Pan de Azúcar: un domo de granito, con la cara que mira al sol cálida y clara y la otra azulada, y el paso
     * entre las dos redondo (es un domo, no un recorte); las chorreras oscuras que bajan siguiendo la curva de la roca;
     * el filo de luz del lado del sol; la selva en su pie y en la repisa del hombro. De noche, la luna platea su cara.
     */
    private fun DrawScope.panDeAzucar(p: Pincel, pie: Offset, ancho: Float, alto: Float, lejos: Float, az: Azar) {
        val l = p.l; val cx = pie.x; val base = pie.y; val lado = if (p.solALaDerecha) 1f else -1f
        fun en(u: Float, v: Float) = Offset(cx + u * ancho, base - v * alto)
        val silueta = curva(PAN.map { (u, v) -> en(u, v) }, base + 1f)
        val roca = lerp(C.ROCA, C.TEJA, 0.14f)
        val luna = l.noche > 0.6f && l.nublado < 0.5f
        val claro = l.tono(lerp(roca, if (luna) EscenaBotanico.LUZ_DE_LUNA else Color(0xFFFFE4C0), (if (luna) 0.35f else 0.42f * p.r.dia + 0.08f)), lejos)
        val oscuro = l.tono(lerp(roca, Color(0xFF46487A), 0.48f), lejos)
        val medio = lerp(claro, oscuro, 0.5f)
        drawPath(silueta, Brush.verticalGradient(listOf(oscuro, lerp(oscuro, l.tono(C.SELVA_OSC, lejos), 0.25f)), base - alto, base))
        clipPath(silueta) {
            // La cara de luz: del paso de sombra (curvo, más hacia la sombra abajo, donde el domo se ensancha) al filo.
            val paso = (0..8).map { i -> val v = 1.02f - i * 0.13f; en(-lado * (0.02f + 0.12f * (1f - v.coerceIn(0f, 1f))) + lado * 0.04f * sin(v * 5f), v) }
            val cara = poligono(paso + listOf(en(lado * 0.8f, -0.1f), en(lado * 0.8f, 1.1f)))
            drawPath(cara, Brush.horizontalGradient(listOf(medio, claro, lerp(claro, Color.White, 0.08f)), cx - lado * 0.05f * ancho, cx + lado * 0.45f * ancho))
            // El filo entre las dos caras, blando.
            drawPath(curva(paso), lerp(medio, oscuro, 0.3f).copy(alpha = 0.45f), style = Stroke(2.2f, cap = StrokeCap.Round))
            // Las chorreras: rayas oscuras que bajan por la roca, abriéndose con la curva del domo, más marcadas en medio.
            val chorros = ArrayList<Offset>()
            repeat(16) { val u0 = az.entre(-0.34f, 0.36f); val v0 = az.entre(0.55f, 0.97f); val v1 = az.entre(0.12f, 0.42f)
                val pts = (0..4).map { j -> val v = v0 + (v1 - v0) * j / 4f; en(u0 * (1f + 0.35f * (v0 - v)) + az.entre(-0.008f, 0.008f), v) }
                for (j in 0 until 4) { chorros += pts[j]; chorros += pts[j + 1] } }
            drawPoints(chorros, PointMode.Lines, l.tono(lerp(roca, C.BREA, 0.55f), lejos).copy(alpha = 0.4f), 0.9f, StrokeCap.Round)
            val finos = ArrayList<Offset>()
            repeat(12) { val u0 = az.entre(-0.3f, 0.3f); val v0 = az.entre(0.7f, 0.95f); finos += en(u0, v0); finos += en(u0 * 1.25f, v0 - az.entre(0.25f, 0.5f)) }
            drawPoints(finos, PointMode.Lines, l.tono(lerp(roca, C.BREA, 0.4f), lejos).copy(alpha = 0.3f), 0.5f, StrokeCap.Round)
            // La selva en la repisa del hombro y en el pie, que sube más por las vaguadas.
            val selva = l.tono(lerp(C.SELVA, C.SELVA_OSC, 0.25f), lejos); val selvaLuz = l.tono(lerp(C.HOJA, C.HOJA_LUZ, 0.25f), lejos).copy(alpha = 0.3f + 0.5f * p.r.dia)
            val repisa = (0..8).map { i -> val u = -0.5f + 0.34f * i / 8f; en(u, 0.4f + 0.09f * sin(i * 1.1f) + 0.04f * az.real() + (u + 0.5f) * 0.25f) }
            drawPath(curva(repisa + listOf(en(-0.12f, 0.5f), en(-0.1f, 0.38f)), null).apply { lineTo(en(-0.5f, 0.2f).x, en(-0.5f, 0.2f).y); close() }, selva)
            val falda = (0..14).map { i -> val u = -0.5f + i / 14f; en(u, 0.1f + 0.08f * az.real() + (if (i % 4 == 1) 0.08f else 0f)) }
            drawPath(curva(falda, base + 2f), selva)
            val motas = ArrayList<Offset>(); repeat(26) { val i = az.entero(falda.size); motas += falda[i] + Offset(az.entre(-2f, 2f), az.entre(0f, 2f)) }
            repeat(10) { val i = az.entero(repisa.size); motas += repisa[i] + Offset(az.entre(-1.5f, 1.5f), az.entre(0f, 1.5f)) }
            drawPoints(motas, PointMode.Points, selvaLuz, 1.8f, StrokeCap.Round)
        }
        // El filo de luz del lado del sol, por el canto.
        drawPath(curva(PAN.filter { (u, _) -> u * lado > 0.02f }.map { (u, v) -> en(u, v) }), lerp(claro, Color.White, 0.25f).copy(alpha = 0.5f * (0.3f + 0.7f * p.r.dia)), style = Stroke(0.7f, cap = StrokeCap.Round))
    }

    /** El reflejo del Pan de Azúcar en el agua quieta: la silueta del revés, más honda y azul, con sus dos caras, rota por las ondas. */
    private fun DrawScope.reflejoDelPan(p: Pincel, pie: Offset, ancho: Float, alto: Float, lejos: Float, quieta: Float) {
        val l = p.l; val cx = pie.x; val base = pie.y + 0.4f; val lado = if (p.solALaDerecha) 1f else -1f
        fun en(u: Float, v: Float) = Offset(cx + u * ancho, base + v * alto * 0.85f)
        val forma = curva(PAN.map { (u, v) -> en(u, v) }, base - 1f)
        val roca = lerp(C.ROCA, C.TEJA, 0.14f); val agua = l.tono(lerp(C.MAR, C.MAR_HONDO, 0.4f))
        val claro = lerp(l.tono(lerp(roca, Color(0xFFFFE4C0), 0.3f * p.r.dia), lejos), agua, 0.35f); val oscuro = lerp(l.tono(lerp(roca, Color(0xFF46487A), 0.5f), lejos), agua, 0.35f)
        drawPath(forma, Brush.horizontalGradient(listOf(oscuro, oscuro, claro), cx - lado * ancho * 0.5f, cx + lado * ancho * 0.4f), alpha = (0.75f * quieta).coerceIn(0f, 1f))
        // Las ondas que lo rompen: rayas del color del agua, más juntas hacia abajo.
        clipPath(forma) { var y = base + 1.2f; var i = 0
            while (y < base + alto) { drawLine(lerp(agua, l.alto, 0.25f).copy(alpha = 0.55f), Offset(cx - ancho * 0.6f, y), Offset(cx + ancho * 0.6f, y + 0.2f), 0.5f + 0.02f * (y - base)); y += 2.4f - 0.012f * (y - base) * (if (i % 2 == 0) 1f else 0.5f); i++ } }
    }

    // ================================================================ La montaña

    private fun DrawScope.montana(m: Momento, luz: LuzSelva.Luz) {
        val l = luz.l; val r = luz.r; val az = Azar(Semilla.de("botanico", "montana"))
        val p = luz.p
        // La montaña baja al agua por la derecha: su pie sigue la orilla de la bahía, con sus peñas y su espuma.
        val pie = listOf(Offset(298f, 121.5f), Offset(287f, 125f), Offset(279f, 131f), Offset(270f, 136f), Offset(264f, 146f), Offset(257f, 158f), Offset(250f, 176f), Offset(-14f, 176f))
        val forma = curva(CRESTA).apply { for (q in pie) lineTo(q.x, q.y); close() }
        drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(lerp(C.SELVA, C.SELVA_OSC, 0.3f), Color(0xFF6A8098), 0.32f), 0.5f), l.tono(lerp(C.SELVA, C.SELVA_OSC, 0.35f), 0.36f)), -30f, 160f))
        clipPath(forma) {
            // El primer plano de la montaña, el más lejano: copas chicas en lote que siguen la ladera, y el granito.
            loteDeCopas(l, r, az, -14f, 300f, -40f, 120f, 2.6f, 0.62f) { x -> crestaEn(x) }
            granito(luz, az)
            bruma(luz, 70f, 10f, 0.8f, az)
            // El segundo plano: un espolón que baja de la izquierda, con árboles más grandes que sobresalen.
            val espolon = perfil(Azar(Semilla.de("botanico", "espolon")), -14f, 268f, 118f, 46f, 0.5f, 26).map { Offset(it.x, it.y + (it.x / 268f) * 10f) }
            monte(l, espolon, 175f, lerp(C.SELVA, Color(0xFF6A8098), 0.3f), 0.54f)
            clipPath(curva(espolon, 175f)) { loteDeCopas(l, r, az, -14f, 270f, 70f, 140f, 3.6f, 0.52f) { x -> espolon.minByOrNull { abs(it.x - x) }!!.y } }
            for (x in listOf(30f, 98f, 150f, 214f)) emergente(l, r, Offset(x + az.entre(-8f, 8f), espolon.minByOrNull { abs(it.x - x) }!!.y + 6f), az.entre(6f, 9f), 0.5f, az)
            bruma(luz, 118f, 9f, 0.7f, az)
            // El tercero, al pie: la ladera cercana, con copas grandes, palmeras y algún árbol en flor.
            val ladera = perfil(Azar(Semilla.de("botanico", "ladera")), -14f, 262f, 148f, 30f, 0.45f, 22).map { Offset(it.x, it.y + (it.x / 262f) * 6f) }
            monte(l, ladera, 175f, lerp(C.SELVA, Color(0xFF6A8098), 0.12f), 0.4f)
            clipPath(curva(ladera, 175f)) { loteDeCopas(l, r, az, -14f, 266f, 100f, 170f, 5.4f, 0.38f) { x -> ladera.minByOrNull { abs(it.x - x) }!!.y } }
            for (x in listOf(12f, 66f, 124f, 186f, 238f)) emergente(l, r, Offset(x + az.entre(-10f, 10f), ladera.minByOrNull { abs(it.x - x) }!!.y + 8f), az.entre(9f, 13f), 0.36f, az)
            for (x in listOf(48f, 112f, 168f, 226f)) embauba(l, r, Offset(x + az.entre(-8f, 8f), ladera.minByOrNull { abs(it.x - x) }!!.y + 14f), az.entre(10f, 15f), 0.36f, az)
            for (x in listOf(84f, 140f, 200f, 250f)) palmaLejana(l, r, Offset(x + az.entre(-6f, 6f), ladera.minByOrNull { abs(it.x - x) }!!.y + 16f), az.entre(18f, 26f), 0.36f, az)
            bruma(luz, 150f, 8f, 0.6f, az)
            // Los rayos de sol que bajan entre las copas y encienden lo que tocan.
            rayos(luz, Rect(-10f, -20f, 300f, 175f), 7, Semilla.de("botanico", "rayos montana", m.ficha.dia), 1f)
        }
        // La orilla de la montaña: peñas de granito con su lado de luz y la espuma blanca a su pie.
        val pz = Azar(Semilla.de("botanico", "penas orilla"))
        for (i in 0 until 6) {
            val q = pie[i] + (pie[i + 1] - pie[i]) * pz.entre(0.2f, 0.8f); val e = pz.entre(2.2f, 4.2f)
            val pena = Path().apply { moveTo(q + Offset(-e, 0.5f)); cubicTo(q + Offset(-e, -e * 0.8f), q + Offset(e * 0.5f, -e), q + Offset(e, 0.5f)); close() }
            drawPath(pena, Brush.horizontalGradient(listOf(luz.p.sombra(C.ROCA, 0.4f), luz.p.luz(C.ROCA, 0.4f)), q.x - e * (if (luz.p.solALaDerecha) 1f else -1f), q.x + e * (if (luz.p.solALaDerecha) 1f else -1f)))
            drawLine(l.tono(Color.White, 0.3f).copy(alpha = 0.7f * (1f - 0.5f * l.noche)), q + Offset(-e * 1.3f, 0.8f), q + Offset(e * 1.3f, 0.8f), 0.7f, StrokeCap.Round)
        }
    }

    /** La altura de la cresta en [x]. */
    private fun crestaEn(x: Float): Float {
        val i = CRESTA.indexOfLast { it.x <= x }.coerceIn(0, CRESTA.size - 2); val a = CRESTA[i]; val b = CRESTA[i + 1]
        return a.y + (b.y - a.y) * ((x - a.x) / (b.x - a.x)).coerceIn(0f, 1f)
    }

    /**
     * Un lote de copas que cubre una ladera de [x0] a [x1] y de la cresta ([cresta]) hasta [y1]: cada copa, un borbotón
     * oscuro con su lado de luz arriba y del lado del sol, en puntos; entre ellas, los huecos hondos. No van en filas: se
     * siembran al azar, de tamaños distintos, y alguna sobresale.
     */
    private fun DrawScope.loteDeCopas(l: Luces, r: io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo, az: Azar, x0: Float, x1: Float, y0: Float, y1: Float, tam: Float, lejos: Float, cresta: (Float) -> Float) {
        val lado = if (r.lat >= 0f) 1f else -1f
        val dia = 0.25f + 0.75f * r.dia
        val cHondo = l.tono(mult(lerp(C.SELVA, C.SELVA_OSC, 0.75f), r.color, 0.55f), lejos); val cCuerpo = l.tono(lerp(C.SELVA, C.HOJA, 0.12f), lejos)
        val cMedio = l.tono(lerp(C.SELVA, C.HOJA, 0.38f), lejos); val cClaro = l.tono(lerp(C.HOJA, C.HOJA_LUZ, 0.4f), lejos).copy(alpha = 0.8f * dia)
        // Se siembran por bandas, de la cresta hacia abajo: así cada copa tapa un poco la de detrás, como en una ladera.
        val bandas = 7; val n = ((x1 - x0) * (y1 - y0) / (tam * tam) * 0.42f).toInt().coerceAtMost(1900)
        for (b in 0 until bandas) {
            val hondo = ArrayList<Offset>(); val cuerpo = ArrayList<Offset>(); val medio = ArrayList<Offset>(); val claro = ArrayList<Offset>(); val flor = ArrayList<Offset>(); val flor2 = ArrayList<Offset>()
            val f0 = b / bandas.toFloat(); val f1 = (b + 1) / bandas.toFloat()
            repeat(n / bandas) {
                val x = az.entre(x0, x1); val top = maxOf(y0, cresta(x)); val y = top + (y1 - top) * az.entre(f0, f1)
                // Cada copa, un borbotón: el hueco hondo debajo, dos o tres gajos de verde y el filo de luz arriba, del lado del sol.
                val t = tam * az.entre(0.6f, 1.5f) * (1f + 0.5f * f0) * (if (az.entero(14) == 0) 1.8f else 1f)
                hondo += Offset(x + az.entre(-0.3f, 0.3f) * t, y + t * 0.45f)
                repeat(2 + az.entero(2)) { cuerpo += Offset(x + az.entre(-0.55f, 0.55f) * t, y + az.entre(-0.2f, 0.25f) * t) }
                medio += Offset(x + lado * t * 0.18f, y - t * 0.12f)
                if (az.real() < 0.7f) claro += Offset(x + lado * t * az.entre(0.15f, 0.45f), y - t * az.entre(0.3f, 0.45f))
                val raro = az.entero(70)
                if (raro == 0) flor += Offset(x, y - t * 0.15f) else if (raro == 1) flor2 += Offset(x, y - t * 0.15f)
            }
            val k = 1f + 0.5f * f0
            drawPoints(hondo, PointMode.Points, cHondo, tam * 1.9f * k, StrokeCap.Round)
            drawPoints(cuerpo, PointMode.Points, cCuerpo, tam * 1.05f * k, StrokeCap.Round)
            drawPoints(medio, PointMode.Points, cMedio, tam * 0.8f * k, StrokeCap.Round)
            drawPoints(claro, PointMode.Points, cClaro, tam * 0.42f * k, StrokeCap.Round)
            // Los árboles en flor: el ipê amarillo y la quaresmeira morada **[Diseño propio]**.
            drawPoints(flor, PointMode.Points, l.tono(lerp(C.LATON, C.ARENA, 0.3f), lejos), tam * 1.2f * k, StrokeCap.Round)
            drawPoints(flor2, PointMode.Points, l.tono(lerp(C.SOMBRA, C.TEJA, 0.3f), lejos), tam * 1.2f * k, StrokeCap.Round)
        }
    }

    /** Un árbol que sobresale del dosel: el tronco claro a la vista y la copa en pisos, tendida, con la sombra debajo de cada piso. */
    fun DrawScope.emergente(l: Luces, r: io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo, pie: Offset, tam: Float, lejos: Float, az: Azar) {
        val lado = if (r.lat >= 0f) 1f else -1f
        val tronco = l.tono(lerp(C.ROCA, C.LONA, 0.35f), lejos)
        val cima = pie + Offset(az.entre(-0.2f, 0.2f) * tam, -tam * 2.6f)
        drawLine(tronco, pie, cima, max(0.6f, tam * 0.14f))
        for (s in listOf(-1f, 1f)) drawLine(tronco, pie + (cima - pie) * 0.55f, cima + Offset(s * tam * 0.9f, tam * 0.2f), max(0.4f, tam * 0.07f))
        // Los pisos de la copa: el de abajo más ancho; cada uno con su enves en sombra y su haz al sol.
        for (piso in 0..2) {
            val c = cima + Offset(az.entre(-0.2f, 0.2f) * tam, -piso * tam * 0.45f + tam * 0.2f); val rx = tam * (1.5f - 0.35f * piso) * az.entre(0.9f, 1.1f); val ry = tam * 0.32f
            val forma = Path().apply { moveTo(c + Offset(-rx, ry * 0.3f)); cubicTo(c + Offset(-rx * 0.8f, -ry * 1.2f), c + Offset(rx * 0.8f, -ry * 1.3f), c + Offset(rx, ry * 0.3f)); cubicTo(c + Offset(rx * 0.5f, ry * 0.7f), c + Offset(-rx * 0.5f, ry * 0.7f), c + Offset(-rx, ry * 0.3f)); close() }
            drawPath(forma, l.tono(mult(lerp(C.SELVA, C.SELVA_OSC, 0.4f), r.color, 0.45f), lejos))
            drawPath(Path().apply { moveTo(c + Offset(-rx * 0.85f, 0f)); cubicTo(c + Offset(-rx * 0.6f, -ry * 1.1f), c + Offset(rx * 0.6f, -ry * 1.2f), c + Offset(rx * 0.9f, ry * 0.1f)); cubicTo(c + Offset(rx * 0.4f, -ry * 0.2f), c + Offset(-rx * 0.4f, -ry * 0.1f), c + Offset(-rx * 0.85f, 0f)); close() }, l.tono(lerp(C.SELVA, C.HOJA, 0.35f), lejos))
            val claros = (0 until 9).map { val a = az.entre(3.4f, 6.0f); c + Offset(cos(a) * rx * az.entre(0.3f, 0.95f) + lado * rx * 0.15f, sin(a) * ry * 1.1f) }
            drawPoints(claros, PointMode.Points, l.tono(lerp(C.HOJA, C.HOJA_LUZ, 0.45f), lejos).copy(alpha = 0.4f + 0.5f * r.dia), max(1f, tam * 0.35f), StrokeCap.Round)
        }
    }

    /** Una embaúba: el tronco blanco y las ramas en candelabro, cada una con su ramillete de hojas plateadas en estrella. */
    fun DrawScope.embauba(l: Luces, r: io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo, pie: Offset, tam: Float, lejos: Float, az: Azar) {
        val blanco = l.tono(lerp(C.LONA, C.ROCA, 0.25f), lejos)
        val cima = pie + Offset(az.entre(-0.1f, 0.1f) * tam, -tam * 1.6f)
        drawLine(blanco, pie, cima, max(0.5f, tam * 0.06f))
        val puntas = (0 until 4).map { i -> val a = -1.57f + (i - 1.5f) * 0.55f; val b = cima + Offset(cos(a) * tam * 0.7f, sin(a) * tam * 0.55f)
            drawPath(Path().apply { moveTo(cima); cubicTo(cima + Offset(cos(a) * tam * 0.5f, 0f), b + Offset(0f, tam * 0.15f), b) }, blanco, style = Stroke(max(0.4f, tam * 0.035f))); b }
        val plata = l.tono(lerp(C.HOJA_LUZ, C.LONA, 0.45f), lejos); val enves = l.tono(lerp(C.SELVA, C.ROCA, 0.3f), lejos)
        for (b in puntas) for (j in 0 until 7) { val a = j * 0.9f + az.entre(-0.2f, 0.2f); val q = b + Offset(cos(a) * tam * 0.28f, sin(a) * tam * 0.16f + tam * 0.05f)
            drawLine(if (sin(a) > 0f) enves else plata, b, q, max(0.6f, tam * 0.08f), StrokeCap.Round) }
    }

    /** Una palmera lejana que sobresale del dosel: el tronco fino y la estrella de pencas. */
    fun DrawScope.palmaLejana(l: Luces, r: io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo, pie: Offset, alto: Float, lejos: Float, az: Azar) {
        val lado = if (r.lat >= 0f) 1f else -1f
        val cima = pie + Offset(az.entre(-0.08f, 0.08f) * alto, -alto)
        drawPath(Path().apply { moveTo(pie); cubicTo(pie + Offset(0f, -alto * 0.5f), cima + Offset(-(cima.x - pie.x), alto * 0.3f), cima) }, l.tono(lerp(C.ROCA, C.ARENA, 0.2f), lejos), style = Stroke(max(0.4f, alto * 0.025f)))
        for (j in 0 until 9) { val a = -3.1f + j * 0.39f + az.entre(-0.1f, 0.1f); val largo = alto * az.entre(0.2f, 0.28f)
            val fin = cima + Offset(cos(a) * largo, sin(a) * largo * 0.55f + largo * 0.4f * abs(cos(a)))
            drawPath(Path().apply { moveTo(cima); cubicTo(cima + Offset(cos(a) * largo * 0.5f, sin(a) * largo * 0.6f - largo * 0.15f), fin, fin) }, l.tono(if (cos(a) * lado > -0.2f) lerp(C.SELVA, C.HOJA_LUZ, 0.35f * r.dia + 0.1f) else lerp(C.SELVA, C.SELVA_OSC, 0.5f), lejos), style = Stroke(max(0.5f, alto * 0.03f), cap = StrokeCap.Round)) }
    }

    /** La cara de granito de la montaña, con sus regueros, y la cascada que baja por la quebrada. */
    private fun DrawScope.granito(luz: LuzSelva.Luz, az: Azar) {
        val l = luz.l; val r = luz.r; val lado = if (r.lat >= 0f) 1f else -1f
        val roca = lerp(C.ROCA, C.TEJA, 0.1f)
        // La pared: lisa, casi vertical, con su lado de luz y la selva que se agarra a sus repisas.
        val pared = curva(listOf(Offset(198f, 62f), Offset(197f, 40f), Offset(203f, 22f), Offset(211f, 16f), Offset(219f, 18f), Offset(227f, 28f), Offset(232f, 46f), Offset(235f, 64f)), 70f)
        drawPath(pared, Brush.horizontalGradient(listOf(l.tono(lerp(roca, Color(0xFF4A4C7A), 0.4f), 0.55f), l.tono(lerp(roca, C.SELVA_OSC, 0.15f), 0.55f), l.tono(lerp(roca, Color(0xFFFFE8C0), 0.3f * r.dia), 0.55f)), 216f - lado * 20f, 216f + lado * 20f))
        clipPath(pared) {
            repeat(14) { val x = az.entre(197f, 235f); drawLine(l.tono(lerp(roca, C.BREA, 0.5f), 0.55f).copy(alpha = 0.45f), Offset(x, az.entre(18f, 40f)), Offset(x + az.entre(-1.5f, 1.5f), az.entre(46f, 70f)), az.entre(0.5f, 1.4f)) }
            // La selva que se agarra a las repisas de la pared: matas sueltas, más en las grietas y al pie.
            val matas = ArrayList<Offset>(); val luces = ArrayList<Offset>()
            for (j in 0..5) { val y0 = 26f + j * 8f + az.entre(-2f, 2f); var x = 197f + az.entre(0f, 6f)
                while (x < 236f) { val largo = az.entre(2f, 7f); if (az.real() < 0.55f) { matas += Offset(x, y0 + (x - 197f) * 0.06f); matas += Offset(x + largo, y0 + (x + largo - 197f) * 0.06f + az.entre(-0.6f, 0.6f)); if (az.real() < 0.6f) { luces += Offset(x + 0.5f, y0 + (x - 197f) * 0.06f - 1f); luces += Offset(x + largo * 0.6f, y0 + (x - 197f) * 0.06f - 1.2f) } }; x += largo + az.entre(1f, 5f) } }
            drawPoints(matas, PointMode.Lines, l.tono(lerp(C.SELVA, C.SELVA_OSC, 0.3f), 0.55f), 2.4f, StrokeCap.Round)
            drawPoints(luces, PointMode.Lines, l.tono(lerp(C.HOJA, C.HOJA_LUZ, 0.3f), 0.55f).copy(alpha = 0.7f), 1.1f, StrokeCap.Round)
        }
        // La cascada: un hilo blanco que salta de la quebrada, con su nube de agua al pie.
        val crecida = if (SelvaBotanico.arroyoCrecido(luz.m)) 1.6f else 1f
        val caida = listOf(Offset(172f, 6f), Offset(173f, 22f), Offset(171f, 40f), Offset(174f, 58f), Offset(172f, 76f))
        drawPath(curva(caida), l.tono(lerp(C.SELVA_OSC, C.BREA, 0.3f), 0.4f), style = Stroke(4f * crecida, cap = StrokeCap.Round))
        drawPath(curva(caida), l.tono(lerp(C.LONA, C.ORILLA, 0.25f), 0.4f), style = Stroke(1.2f * crecida, cap = StrokeCap.Round, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(6f, 2f))))
        mancha(Offset(172f, 78f), 9f, 4f, l.tono(C.LONA, 0.4f), 0.45f)
    }

    /** La bruma entre dos planos de la selva: bancos largos y blandos, más espesos por la mañana y con lluvia o niebla. */
    fun DrawScope.bruma(luz: LuzSelva.Luz, y: Float, alto: Float, cuanta: Float, az: Azar) {
        val k = (luz.bruma * cuanta).coerceIn(0f, 1f); if (k < 0.02f) return
        val l = luz.l
        val c = if (luz.directa > 0.3f && !luz.deLuna) lerp(lerp(l.bruma, l.tono(Color.White), 0.4f), luz.color, 0.25f) else lerp(l.bruma, l.tono(Color.White), 0.35f)
        repeat(7) { mancha(Offset(az.entre(-20f, 300f), y + az.entre(-alto, alto) * 0.5f), az.entre(50f, 120f), alto * az.entre(0.6f, 1.1f), c, 0.85f * k) }
    }

    /**
     * Los rayos de sol que bajan entre las copas: haces largos con el borde blando, inclinados según el sol (por la
     * mañana bajan desde la derecha, a mediodía casi a plomo, por la tarde desde la izquierda), con el trozo de follaje
     * que encienden al final. Más vivos con la bruma de la mañana. De noche, con la luna, unos pocos y fríos.
     */
    fun DrawScope.rayos(luz: LuzSelva.Luz, zona: Rect, cuantos: Int, semilla: Long, fuerza: Float) {
        val r = luz.r
        if (luz.directa < 0.15f) return
        val k = luz.directa * fuerza * (if (luz.deLuna) 0.35f else 0.55f + 0.7f * luz.bruma)
        val color = if (luz.deLuna) EscenaBotanico.LUZ_DE_LUNA else lerp(luz.l.solColor, Color(0xFFFFE6B0), 0.4f)
        val az = Azar(semilla)
        val dx = (-r.lat * r.cot * 0.45f).coerceIn(-1.3f, 1.3f)
        repeat(cuantos) {
            val x0 = az.entre(zona.left, zona.right); val y0 = az.entre(zona.top, zona.top + zona.height * 0.35f); val largo = az.entre(0.45f, 0.85f) * zona.height
            val w0 = az.entre(2f, 5f); val w1 = w0 * az.entre(2f, 3.2f)
            val a = Offset(x0, y0); val b = Offset(x0 + dx * largo, y0 + largo)
            val haz = poligono(a + Offset(-w0, 0f), a + Offset(w0, 0f), b + Offset(w1, 0f), b + Offset(-w1, 0f))
            drawPath(haz, Brush.linearGradient(listOf(color.copy(alpha = (0.27f * k).coerceIn(0f, 1f)), color.copy(alpha = (0.11f * k).coerceIn(0f, 1f)), Color.Transparent), a, b), blendMode = BlendMode.Plus)
            drawPath(poligono(a + Offset(-w0 * 0.4f, 0f), a + Offset(w0 * 0.4f, 0f), b + Offset(w1 * 0.4f, 0f), b + Offset(-w1 * 0.4f, 0f)), Brush.linearGradient(listOf(color.copy(alpha = (0.18f * k).coerceIn(0f, 1f)), Color.Transparent), a, b), blendMode = BlendMode.Plus)
            // Lo que encienden: un trozo de follaje verde amarillo donde llegan.
            if (!luz.deLuna) { val q = a + (b - a) * az.entre(0.55f, 0.8f); mancha(q, w1 * 1.6f, w1 * 0.7f, luz.l.tono(lerp(C.HOJA_LUZ, C.LATON, 0.3f)), (0.35f * k).coerceIn(0f, 1f)) }
        }
    }

    /** El velo de la lluvia o de la nieve y los bancos de niebla sobre lo lejano: van antes del jardín, para que lo cercano quede limpio. */
    fun DrawScope.veloDeLoLejano(m: Momento, luz: LuzSelva.Luz) {
        val l = luz.l
        when (m.ficha.clima) {
            Clima.LLUVIA, Clima.TORMENTA -> { val velo = lerp(l.bruma, l.medio, 0.4f); drawRect(Brush.verticalGradient(listOf(velo.copy(alpha = 0.18f), velo.copy(alpha = 0.42f), velo.copy(alpha = 0.2f)), -10f, 180f), Offset(0f, -10f), Size(ANCHO, 190f)) }
            Clima.NIEBLA -> { val b = lerp(l.bruma, Color.White, 0.25f); drawRect(Brush.verticalGradient(listOf(b.copy(alpha = 0.55f), b.copy(alpha = 0.78f), b.copy(alpha = 0.6f)), -10f, 180f), Offset(0f, -10f), Size(ANCHO, 190f)) }
            Clima.NIEVE -> drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFFE8EDF2)).copy(alpha = 0.32f)), -10f, 170f), Offset(0f, -10f), Size(ANCHO, 180f))
            else -> Unit
        }
    }
}
