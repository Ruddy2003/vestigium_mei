package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoH
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoV
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraDeFollaje
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * El abrigo de roca de Ama, visto desde dentro: es el primer cuadro del tema y el primero con luz de fuego.
 *
 * - **Lo que enmarca:** el techo de la visera, negro de humo sobre el hogar; a la izquierda, la pared clara del friso,
 *   con su repisa para las lámparas y los colores y, al fondo, la boca oscura de la galería; a la derecha, el pilar
 *   con las pieles colgadas que cortan el viento.
 * - **Por la boca:** el valle ([ValleCueva]) y el abedul del borde, que cruza el cuadro de abajo arriba.
 * - **Dentro:** el murete de piedras del labio, el lecho de pieles, la laja con el objeto del día, el hogar en su
 *   corro de piedras, la leña, el bastidor con una piel tensada y las azagayas apoyadas en el pilar.
 *
 * La luz: de día entra el sol por la boca y deja en el suelo una mancha que se mueve con la hora ([parcheDeSol]): a
 * las 7:00 se tiende hasta la pared del friso y a las 13:00 queda corta, junto al labio. Al caer la tarde y de noche
 * manda la hoguera: cada cosa pide su color a [LuzCueva.en] y su sombra se tumba lejos de la lumbre.
 *
 * **[Diseño propio]**: el cuento dice que Ama pinta «en lo hondo de la cueva». El cuadro trae el friso a la pared del
 * abrigo, junto a la entrada de la galería, para que se vea crecer desde donde se mira. **[Aproximación]**: con el
 * sol dentro del cuadro la luz viene de frente; las sombras de dentro caen hacia quien mira y de lado, que es lo que
 * haría un sol que entra por la boca.
 */
internal object AbrigoCueva {
    private val C = ColorCueva
    private const val H = ValleCueva.H

    /** El labio del abrigo: donde acaba el suelo de dentro y empieza la ladera. */
    const val LABIO = 199f
    const val PISO_GATO = 273f

    /** Donde arde el hogar, y el centro de la boca, por donde entra la claridad. */
    val HOGAR = Offset(234f, 252f)
    val BOCA = Offset(250f, 150f)
    private val LOSA = Offset(132f, 238f)
    private val LAMPARA = Offset(62f, 167.4f)
    private val LAMPARA_NUEVA = Offset(124f, 169.6f)
    private const val LENA = 305f

    private fun pts(vararg v: Float): List<Offset> = (0 until v.size / 2).map { Offset(v[it * 2], v[it * 2 + 1]) }

    private val BOCA_PTS = pts(
        151f, 200f, 149f, 178f, 153f, 152f, 147f, 130f, 151f, 106f, 159f, 88f, 171f, 74f, 187f, 64f, 205f, 57f, 223f, 51f, 241f, 45f, 259f, 39f, 277f, 35f, 295f, 32f, 311f, 31f,
        325f, 34f, 336f, 41f, 343f, 53f, 347f, 71f, 345f, 93f, 349f, 115f, 346f, 139f, 350f, 161f, 347f, 181f, 349f, 200f,
    )

    /** El contorno de la boca del abrigo: roca quebrada, no una curva. */
    private val FORMA_BOCA: Path by lazy {
        val az = Azar(Semilla.de("cueva", "boca")); val p = Path()
        p.moveTo(BOCA_PTS[0].x, BOCA_PTS[0].y)
        for (i in 1 until BOCA_PTS.size) { val a = BOCA_PTS[i - 1]; val b = BOCA_PTS[i]; p.lineTo((a.x + b.x) / 2f + az.entre(-2.2f, 2.2f), (a.y + b.y) / 2f + az.entre(-2.2f, 2.2f)); p.lineTo(b.x, b.y) }
        p.close(); p
    }

    /** Las lámparas de grasa encendidas junto al friso en este momento. */
    fun lamparas(m: Momento): List<Offset> = buildList {
        add(LAMPARA)
        if (EscenaCueva.velando(m)) return@buildList
        if (EscenaCueva.lamparaNueva(m)) add(LAMPARA_NUEVA)
        if (EscenaCueva.fiestaDelFriso(m)) { add(Offset(38f, 166.6f)); add(Offset(88f, 168.4f)); add(Offset(142f, 170.4f)) }
    }

    // ---------------------------------------------------------------- El sol que entra

    private fun fondoDelSol(r: Rayo): Float = (12f + 20f * r.cot).coerceAtMost(84f)
    private fun correDelSol(r: Rayo): Float = -r.lat * fondoDelSol(r) * 0.85f

    /** La mancha que el sol que entra por la boca deja en el suelo: larga y tendida con el sol bajo, corta a mediodía. */
    fun parcheDeSol(r: Rayo): Path {
        val d = fondoDelSol(r); val dx = correDelSol(r); val alto = LABIO - 31f
        // El contorno de la boca, tumbado en el suelo: lo que está a la altura h del labio cae a d·h/alto de fondo y se corre de lado lo mismo.
        return poligono(BOCA_PTS.map { p -> val t = ((LABIO - p.y) / alto).coerceIn(0f, 1f); Offset(p.x + dx * t, LABIO + 1f + d * t) })
    }

    /** Dónde cae en el suelo, con el sol [r], la sombra de algo que está en la boca a la altura [y] del cuadro y en la vertical [x]. */
    private fun caeDelSol(r: Rayo, x: Float, y: Float): Offset { val t = ((LABIO - y) / (LABIO - 31f)).coerceIn(0f, 1.1f); return Offset(x + correDelSol(r) * t, LABIO + 1f + fondoDelSol(r) * t) }

    /** El centro de esa mancha: de ahí rebota su luz. */
    fun centroDelSol(r: Rayo): Offset = Offset(250f + correDelSol(r) * 0.5f, LABIO + fondoDelSol(r) * 0.5f)

    /** El rayo del sol que entra: tumba las sombras de lado y hacia quien mira. */
    fun rayoQueEntra(r: Rayo): Rayo = Rayo(dia = r.dia, lat = r.lat, cot = r.cot, tan = r.tan, ax = r.ax.coerceIn(-2.6f, 2.6f), ay = (-r.ay).coerceAtMost(1.4f), fuerza = r.fuerza * 0.85f, color = r.color, contraste = r.contraste)

    fun seMueve(m: Momento): Boolean = true                                    // el fuego no se apaga nunca del todo

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val z = EscenaCueva.luzDeDentro(m); val l = z.fuera; val r = z.sol; val s = SolCueva(l, r); val semilla = m.semilla("cielo")
        cielo(l, H, semilla); astros(l, H, semilla)
        with(ValleCueva) { aurora(m, l) }
        nubes(l, H, m.ficha.clima, semilla)
        with(ValleCueva) { valle(m, s) }
        tumulo(m, s)
        with(ValleCueva) { abedulGrande(m, s) }
        // El velo del tiempo queda fuera: dentro del abrigo no llueve ni hay niebla.
        tiempoQuieto(l, m.ficha.clima, H, m.semilla("tiempo"))
        roca(m, z)
        suelo(m, z)
        val todas = cosas(m, z).sortedBy { it.pie.y }
        // Las sombras: de día, las del sol que entra, dentro de su mancha; de noche, las del fuego, lejos de la lumbre.
        val entra = rayoQueEntra(r)
        if (entra.fuerza > 0.05f) capaSombra(entra) { c ->
            clipPath(parcheDeSol(r)) {
                // La sombra del abedul de la boca, moteada: la copa (si tiene hoja) y el tronco.
                if (EscenaCueva.estacion(m) != Estacion.INVIERNO && EscenaCueva.nieve(m) <= 0.45f) { val a = caeDelSol(r, 198f, 112f); val b = caeDelSol(r, 332f, 38f); if (b.y - a.y > 6f) sombraDeFollaje(Azar(Semilla.de("cueva", "sombra", "abedul")), Rect(minOf(a.x, b.x), a.y, maxOf(a.x, b.x), b.y), entra, c, 34, 36) }
                val pie = caeDelSol(r, ValleCueva.ABEDUL.x, LABIO); val copa = caeDelSol(r, ValleCueva.ABEDUL.x + 14f, 60f)
                drawLine(c, pie, copa, 3.4f, StrokeCap.Round); for ((u, v) in listOf(0.5f to -18f, 0.62f to 22f, 0.76f to -14f, 0.86f to 16f)) { val q = pie + (copa - pie) * u; drawLine(c, q, q + Offset(v, (copa.y - pie.y) * 0.16f), 1.2f, StrokeCap.Round) }
                for (k in todas) sombraArrojada(entra, k.forma, k.pie, c, LABIO, LABIO)
            }
        }
        val manda = z.mandaElFuego
        if (manda > 0.05f) capaSombra(z.rayoDelFuego(HOGAR + Offset(-70f, 0f))) { c ->
            for (k in todas) if (abs(k.pie.x - HOGAR.x) > 20f) sombraArrojada(z.rayoDelFuego(k.pie), k.forma, k.pie, c, LABIO + 3f, LABIO + 3f)
            // Las piedras del corro: cada una tumba su sombra hacia fuera, en abanico.
            for (j in 0..13) {
                val a = if (j < 7) 3.36f + j * 0.45f else 0.2f + (j - 7) * 0.46f; val p = HOGAR + Offset(cos(a) * 39f, sin(a) * 11.4f)
                val u = Offset(cos(a), sin(a) * 0.42f).let { it / it.getDistance() }; val n = Offset(-u.y, u.x); val largo = 15f + 9f * abs(cos(a)) + (if (sin(a) > 0f) 5f else 0f)
                drawPath(poligono(p + n * 5.6f, p - n * 5.6f, p + u * largo * 1.5f - n * 9f, p + u * largo * 1.5f + n * 9f), Brush.linearGradient(0f to c, 0.5f to c.copy(alpha = 0.5f), 1f to c.copy(alpha = 0f), start = p, end = p + u * largo * 1.5f))
            }
            // Y lo menudo del suelo: cada guijarro, su raya de sombra lejos de la lumbre.
            for (g in GUIJARROS) { val p = Offset(g.a, g.b); val d = Offset(p.x - HOGAR.x, (p.y - HOGAR.y) * 2.4f); val lejos = d.getDistance(); if (lejos < 50f || lejos > 150f) continue; val u = Offset(d.x, d.y * 0.42f) / lejos; drawLine(c.copy(alpha = 0.7f * (1f - lejos / 160f)), p, p + u * (g.c * 2.2f + lejos * 0.02f), g.c * 0.8f, StrokeCap.Round) }
        }
        for (k in todas) with(k) { pintar() }
        adornos(m, z)
        grano(Azar(Semilla.de("cueva", "grano", "abrigo")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- La roca

    private fun DrawScope.roca(m: Momento, z: LuzCueva) {
        val az = Azar(Semilla.de("cueva", "roca")); val dia = (1f - z.noche).coerceIn(0f, 1f); val manda = z.mandaElFuego
        clipPath(FORMA_BOCA, ClipOp.Difference) {
            // ---- El techo: todo lo de arriba.
            val techo = lerp(C.ROCA, C.HOLLIN, 0.2f)
            planoH(z.a, Path().apply { addRect(Rect(0f, 0f, ANCHO, LABIO + 8f)) }, 0f, ANCHO, 34f, techo)
            // Los estratos de la visera, que siguen el arco de la boca.
            for (j in 0..6) {
                val pts = (0..10).map { i -> val x = 110f + i * 27f; Offset(x, 6f + j * 6.6f + 26f * ((x - 330f) / 220f) * ((x - 330f) / 220f) + az.entre(-1.4f, 1.4f)) }
                drawPath(curva(pts), z.en(Offset(240f, 20f), C.HOLLIN).copy(alpha = 0.26f), style = Stroke(0.7f)); drawPath(curva(pts.map { it + Offset(0f, 1.1f) }), z.en(Offset(240f, 20f), C.CALIZA).copy(alpha = 0.16f), style = Stroke(0.6f))
            }
            repeat(26) { val c = Offset(az.entre(100f, 380f), az.entre(2f, 50f)); mancha(c, az.entre(8f, 22f), az.entre(3f, 7f), z.en(c, if (it % 3 == 0) C.CALIZA else C.HOLLIN), 0.16f) }
            // Las lajas del techo: bloques que cuelgan, cada uno con su canto en sombra y su filo de claridad hacia la boca.
            repeat(18) {
                val c = Offset(az.entre(40f, 376f), az.entre(4f, 50f)); val w = az.entre(22f, 60f); val h = az.entre(2.4f, 6f); val sube = (c.x - 250f) * (c.x - 250f) / 4200f
                val abajo = Path().apply { moveTo(c.x - w, c.y + sube); cubicTo(c.x - w * 0.4f, c.y + h + sube * 0.6f, c.x + w * 0.3f, c.y + h * 1.2f + sube * 0.5f, c.x + w, c.y + h * 0.4f + sube) }
                drawPath(abajo, z.en(c, C.HOLLIN).copy(alpha = az.entre(0.3f, 0.6f)), style = Stroke(az.entre(1f, 2.6f), cap = StrokeCap.Round))
                drawPath(Path().apply { addPath(abajo, Offset(0f, -1.8f)) }, z.en(c, C.CALIZA, 1f).copy(alpha = 0.3f), style = Stroke(0.8f, cap = StrokeCap.Round))
            }
            // Hacia el fondo, a la izquierda, el techo se pierde en lo oscuro.
            drawRect(Brush.horizontalGradient(0f to z.dentro.tono(C.HOLLIN).copy(alpha = 0.6f), 1f to Color.Transparent, startX = 0f, endX = 170f), Offset(0f, 0f), Size(170f, 62f))
            // El hollín del hogar: una mancha negra sobre la lumbre, que se va hacia la boca.
            for ((x, y, rx, a) in listOf(Cuatro(234f, 30f, 62f, 0.5f), Cuatro(250f, 22f, 40f, 0.4f), Cuatro(288f, 20f, 36f, 0.28f), Cuatro(200f, 44f, 30f, 0.22f))) mancha(Offset(x, y), rx, rx * 0.34f, z.en(Offset(x, y), C.HOLLIN), a)
            // Líquenes cerca de la boca, donde da el aire.
            drawPoints((0 until 60).map { Offset(az.entre(250f, 372f), az.entre(8f, 30f)) }, PointMode.Points, z.en(Offset(310f, 20f), lerp(C.OCRE_AMARILLO, C.VERDE, 0.4f)).copy(alpha = 0.5f), 1.2f, StrokeCap.Round)

            // ---- La pared del friso, a la izquierda.
            val pared = poligono(listOf(Offset(-2f, 46f), Offset(40f, 42f), Offset(92f, 49f), Offset(132f, 58f), Offset(154f, 72f), Offset(160f, LABIO + 8f), Offset(-2f, LABIO + 8f)))
            val piedra = lerp(C.CALIZA, C.ROCA, 0.34f)
            planoH(z.a, pared, 0f, 158f, 132f, piedra, mira = 1f)
            clipPath(pared) {
                // Hacia el fondo, la pared se pierde en lo oscuro.
                drawRect(Brush.horizontalGradient(0f to z.en(Offset(6f, 150f), C.HOLLIN).copy(alpha = 0.6f), 1f to Color.Transparent, startX = 0f, endX = 70f), Offset(0f, 40f), Size(70f, 170f))
                // El relieve: bultos con su lado de luz hacia la boca y su lado de sombra hacia el fondo.
                repeat(16) {
                    val c = Offset(az.entre(6f, 150f), az.entre(52f, 196f)); val rx = az.entre(10f, 26f); val ry = az.entre(6f, 16f)
                    drawPath(FaunaCueva.masa(c + Offset(-2f, 1.6f), rx, ry, az, 8, 0.24f), z.en(c, C.ROCA_OSC).copy(alpha = 0.16f)); drawPath(FaunaCueva.masa(c + Offset(2f, -1f), rx * 0.8f, ry * 0.7f, az, 8, 0.24f), z.en(c, C.CALIZA, 1f).copy(alpha = 0.2f))
                }
                // El paño liso y claro del friso: la calcita blanca.
                val paño = FaunaCueva.masa(FrisoCueva.PANEL.center, FrisoCueva.PANEL.width * 0.54f, FrisoCueva.PANEL.height * 0.56f, Azar(Semilla.de("cueva", "panel")), 14, 0.07f)
                drawPath(paño, Brush.horizontalGradient(0f to z.en(Offset(20f, 106f), C.CALIZA, 1f).copy(alpha = 0.7f), 1f to z.en(Offset(140f, 106f), lerp(C.CALIZA, Color.White, 0.16f), 1f).copy(alpha = 0.92f), startX = 10f, endX = 148f))
                clipPath(paño) {
                    repeat(9) { val c = Offset(az.entre(18f, 142f), az.entre(60f, 154f)); mancha(c, az.entre(10f, 24f), az.entre(5f, 10f), z.en(c, if (it % 2 == 0) C.ROCA else lerp(C.CALIZA, C.OCRE_AMARILLO, 0.2f), 1f), 0.14f) }
                    // Los bultos de la roca, que ya tienen forma de animal: un filo de sombra por debajo.
                    for ((x, y, w) in listOf(Triple(50f, 112f, 30f), Triple(108f, 104f, 26f), Triple(118f, 142f, 22f), Triple(40f, 148f, 20f))) drawPath(Path().apply { moveTo(x - w, y + 4f); cubicTo(x - w * 0.4f, y + 9f, x + w * 0.4f, y + 9f, x + w, y + 3f) }, z.en(Offset(x, y), C.ROCA_OSC).copy(alpha = 0.2f), style = Stroke(2.2f, cap = StrokeCap.Round))
                }
                // Grietas y chorreras de humedad.
                for ((x0, y0) in listOf(8f to 60f, 96f to 52f, 150f to 80f, 60f to 160f, 126f to 176f)) drawPath(Path().apply { moveTo(x0, y0); var x = x0; var y = y0; repeat(6) { x += az.entre(-5f, 5f); y += az.entre(5f, 11f); lineTo(x, y) } }, z.en(Offset(x0, y0), C.HOLLIN).copy(alpha = 0.4f), style = Stroke(0.55f))
                repeat(8) { val x = az.entre(6f, 150f); val y = az.entre(46f, 70f); drawLine(z.en(Offset(x, y), C.ROCA_OSC).copy(alpha = 0.22f), Offset(x, y), Offset(x + az.entre(-2f, 2f), y + az.entre(14f, 40f)), az.entre(1f, 2.6f)) }
                // Al pie, bajo la repisa, la propia pared: dos estratos quebrados que la cruzan, un banco que sale a la derecha y grietas que bajan.
                for ((j, y0) in listOf(183.6f, 193f).withIndex()) {
                    val quiebros = (0..9).map { i -> Offset(32f + i * 14f + az.entre(-4f, 4f), y0 + az.entre(-2.4f, 2.4f) + i * 0.34f * (if (j == 0) 1f else -1f)) }
                    drawPath(Path().apply { moveTo(quiebros[0].x, quiebros[0].y); for (q in quiebros.drop(1)) lineTo(q.x, q.y) }, z.en(Offset(90f, y0), C.HOLLIN).copy(alpha = 0.42f), style = Stroke(if (j == 0) 1.4f else 0.8f))
                    drawPath(Path().apply { moveTo(quiebros[0].x, quiebros[0].y + 1.5f); for (q in quiebros.drop(1)) lineTo(q.x, q.y + 1.5f) }, z.en(Offset(90f, y0), C.CALIZA, 1f).copy(alpha = 0.36f), style = Stroke(0.6f))
                }
                drawPath(poligono(listOf(Offset(92f, 206f), Offset(97f, 191f), Offset(112f, 187f), Offset(140f, 188.4f), Offset(158f, 186f), Offset(160f, 206f))), z.en(Offset(128f, 198f), lerp(C.ROCA, C.HOLLIN, 0.2f), 1f).copy(alpha = 0.5f))
                drawPath(poligono(Offset(97f, 191f), Offset(112f, 187f), Offset(140f, 188.4f), Offset(158f, 186f), Offset(150f, 191.6f)), z.en(Offset(128f, 189f), C.CALIZA, 1f).copy(alpha = 0.6f))
                for ((x0, y0) in listOf(48f to 179f, 76f to 185f, 118f to 192f)) drawPath(Path().apply { moveTo(x0, y0); var x = x0; var y = y0; repeat(4) { x += az.entre(-3f, 3.6f); y += az.entre(3f, 6f); lineTo(x, y) } }, z.en(Offset(x0, y0), C.HOLLIN).copy(alpha = 0.4f), style = Stroke(0.6f))
                repeat(7) { val c = Offset(az.entre(40f, 150f), az.entre(180f, 200f)); mancha(c, az.entre(6f, 14f), az.entre(2f, 4f), z.en(c, if (it % 2 == 0) lerp(C.OCRE_AMARILLO, C.ROCA, 0.5f) else C.ROCA_OSC), 0.2f) }
                // El pliegue donde el techo se junta con la pared.
                drawPath(Path().apply { moveTo(-2f, 46f); lineTo(40f, 42f); lineTo(92f, 49f); lineTo(132f, 58f); lineTo(154f, 72f) }, z.en(Offset(80f, 46f), C.HOLLIN).copy(alpha = 0.5f), style = Stroke(1.6f))
            }
            // La luz del sol de la mañana, que se tiende hasta la pared; y la del fuego y las lámparas, de noche.
            val entra = (-correDelSol(z.sol) - 24f).coerceIn(0f, 70f)
            if (entra > 0f && z.sol.fuerza > 0.05f) clipPath(pared) { drawPath(poligono(Offset(160f, LABIO + 8f), Offset(160f, LABIO - entra * 1.6f), Offset(160f - entra * 1.5f, LABIO - entra * 0.7f), Offset(160f - entra * 1.5f, LABIO + 8f)), Brush.horizontalGradient(0f to Color.Transparent, 1f to lerp(z.fuera.solColor, Color(0xFFFFD49A), 0.4f).copy(alpha = 0.3f * z.sol.fuerza), startX = 160f - entra * 1.5f, endX = 160f), blendMode = BlendMode.Plus) }
            if (manda > 0.05f) {
                charcoDeLuz(z.fuego, Offset(236f, 34f), 104f, 26f, C.ROCA, 0.8f * manda)
                clipPath(pared) { charcoDeLuz(z.fuego, Offset(150f, 150f), 60f, 80f, C.CALIZA, 0.5f * manda) }
            }
            // ---- La galería: la boca oscura por donde se baja a lo hondo.
            val galeria = Path().apply { moveTo(-2f, 122f); cubicTo(6f, 104f, 26f, 104f, 32f, 124f); cubicTo(38f, 150f, 36f, 180f, 38f, LABIO + 8f); lineTo(-2f, LABIO + 8f); close() }
            drawPath(galeria, Brush.horizontalGradient(0f to z.dentro.tono(lerp(C.HOLLIN, C.SOMBRA, 0.2f)), 1f to z.en(Offset(36f, 160f), lerp(C.HOLLIN, C.ROCA, 0.3f)), startX = 0f, endX = 40f))
            drawPath(galeria, z.en(Offset(34f, 130f), C.CALIZA, 1f).copy(alpha = 0.5f), style = Stroke(0.8f))
            repisa(m, z)
            for (p in lamparas(m).takeIf { EscenaCueva.lampara(m) } ?: emptyList()) clipPath(pared) { charcoDeLuz(Foco(p, C.LUMBRE, 0.5f + 0.4f * z.noche, 40f), p + Offset(0f, -30f), 34f, 40f, C.CALIZA) }
            with(FrisoCueva) { friso(m, z) }

            // ---- El pilar de la derecha, con las pieles colgadas que cortan el viento.
            val pilar = poligono(listOf(Offset(338f, 30f), Offset(384f, 26f), Offset(384f, LABIO + 8f), Offset(344f, LABIO + 8f), Offset(346f, 140f), Offset(342f, 80f)))
            planoV(z.a, pilar, 362f, 30f, LABIO + 8f, lerp(C.ROCA, C.CALIZA, 0.2f), mira = -1f)
            clipPath(pilar) {
                repeat(9) { val c = Offset(az.entre(346f, 380f), az.entre(40f, 196f)); drawPath(FaunaCueva.masa(c, az.entre(6f, 14f), az.entre(8f, 18f), az, 7, 0.26f), z.en(c, if (it % 2 == 0) C.ROCA_OSC else C.CALIZA, -1f).copy(alpha = 0.2f)) }
                for (y in listOf(70f, 118f, 160f)) drawLine(z.en(Offset(360f, y), C.HOLLIN).copy(alpha = 0.34f), Offset(344f, y + az.entre(-3f, 3f)), Offset(382f, y + az.entre(-5f, 5f)), 0.6f)
                // Los bancos del pilar y una grieta que lo recorre de arriba abajo.
                for (y in listOf(46f, 62f, 98f, 134f, 150f, 176f, 190f)) { val a = Offset(343f + az.entre(0f, 6f), y + az.entre(-2f, 2f)); val b = Offset(a.x + az.entre(12f, 30f), a.y + az.entre(-2f, 2f)); drawLine(z.en(a, C.HOLLIN).copy(alpha = 0.4f), a, b, az.entre(0.8f, 1.8f), StrokeCap.Round); drawLine(z.en(a, C.CALIZA, -1f).copy(alpha = 0.4f), a + Offset(1f, -1.4f), b + Offset(-1f, -1.4f), 0.6f) }
                drawPath(Path().apply { moveTo(356f, 30f); var x = 356f; var y = 30f; repeat(12) { x += az.entre(-4f, 4.6f); y += az.entre(9f, 18f); lineTo(x, y) } }, z.en(Offset(360f, 110f), C.HOLLIN).copy(alpha = 0.4f), style = Stroke(0.7f))
                if (manda > 0.05f) charcoDeLuz(z.fuego, Offset(352f, 190f), 26f, 64f, C.ROCA, 0.5f * manda)
            }
            pielesColgadas(z)
            // El canto de la boca: el grueso de la roca, en facetas que recogen la claridad de fuera, cada una a su modo.
            val canto = Azar(Semilla.de("cueva", "canto")); val claridad = (0.62f * dia + 0.2f * z.noche * (1f - z.fuera.nublado)).coerceIn(0f, 1f)
            for (i in 0 until BOCA_PTS.size - 1) {
                val a = BOCA_PTS[i]; val b = BOCA_PTS[i + 1]; val medio = (a + b) / 2f; val n = (medio - BOCA).let { it / it.getDistance() }; val d = canto.entre(5f, 12f)
                val faceta = poligono(a, b, b + n * (d * canto.entre(0.7f, 1.2f)), a + n * d)
                drawPath(faceta, lerp(z.fuera.tono(lerp(C.CALIZA, Color.White, 0.3f)), z.en(medio, C.CALIZA), 0.3f).copy(alpha = claridad * canto.entre(0.3f, 0.9f)))
                drawLine(z.en(medio, C.HOLLIN).copy(alpha = 0.3f), b, b + n * d, 0.6f)
            }
            drawPath(FORMA_BOCA, z.fuera.tono(lerp(C.CALIZA, Color.White, 0.4f)).copy(alpha = claridad), style = Stroke(1.6f))
        }
    }

    /** La repisa de roca bajo el friso: las lámparas de grasa, la piedra de moler y los cuencos de color. */
    private fun DrawScope.repisa(m: Momento, z: LuzCueva) {
        val cara = poligono(Offset(30f, 170f), Offset(150f, 174f), Offset(152f, 181f), Offset(26f, 178f))
        drawPath(cara, z.en(Offset(90f, 178f), lerp(C.ROCA, C.HOLLIN, 0.3f), 1f)); drawPath(poligono(Offset(34f, 166.4f), Offset(148f, 170f), Offset(150f, 174f), Offset(30f, 170.6f)), z.en(Offset(90f, 168f), C.CALIZA, 1f))
        // La piedra de moler, con su polvo rojo; una concha con el amarillo; los tizones.
        drawOval(z.en(Offset(92f, 168f), C.ROCA), Offset(80f, 165.4f), Size(20f, 4.6f)); drawOval(z.en(Offset(92f, 168f), C.OCRE_ROJO), Offset(83f, 165.8f), Size(13f, 3f))
        drawOval(z.en(Offset(106f, 168f), C.HUESO), Offset(102f, 166.6f), Size(9f, 3.6f)); drawOval(z.en(Offset(106f, 168f), C.OCRE_AMARILLO), Offset(103.4f, 167f), Size(6f, 2.2f))
        for (j in 0..2) drawLine(z.en(Offset(74f, 168f), C.HOLLIN), Offset(70f + j * 2f, 168.4f), Offset(77f + j * 2f, 166.2f - j * 0.5f), 1.3f, StrokeCap.Round)
        // Las lámparas: un cuenco de piedra; la nueva, con mango.
        val encendida = EscenaCueva.lampara(m)
        for (p in listOf(LAMPARA, LAMPARA_NUEVA).filterIndexed { i, _ -> i == 0 || EscenaCueva.lamparaNueva(m) } + lamparas(m).drop(if (EscenaCueva.lamparaNueva(m)) 2 else 1)) {
            drawPath(Path().apply { moveTo(p.x - 5.4f, p.y - 1.6f); cubicTo(p.x - 5f, p.y + 2.6f, p.x + 5f, p.y + 2.6f, p.x + 5.4f, p.y - 1.6f); close() }, z.en(p, C.ROCA))
            drawOval(z.en(p, if (encendida) lerp(C.HUESO, C.LUMBRE, 0.5f) else C.HUESO), Offset(p.x - 5f, p.y - 2.8f), Size(10f, 2.4f))
            if (p == LAMPARA_NUEVA) drawLine(z.en(p, C.ROCA), p + Offset(5f, -0.6f), p + Offset(12f, 0f), 2f, StrokeCap.Round)
        }
    }

    /** Dos pieles colgadas de un palo encajado en el pilar: cortan el viento. */
    private fun DrawScope.pielesColgadas(z: LuzCueva) {
        drawLine(z.en(Offset(362f, 84f), C.MADERA), Offset(342f, 86f), Offset(382f, 82f), 2.2f, StrokeCap.Round)
        for ((x, w, largo, c) in listOf(Cuatro(350f, 20f, 74f, 0.3f), Cuatro(366f, 18f, 62f, 0f))) {
            val color = lerp(C.PIEL, C.PELO, c)
            val piel = Path().apply { moveTo(x, 85f); lineTo(x + w, 84f); cubicTo(x + w + 2f, 84f + largo * 0.5f, x + w - 1f, 84f + largo * 0.8f, x + w + 3f, 84f + largo); lineTo(x + w * 0.6f, 84f + largo - 5f); lineTo(x + w * 0.3f, 84f + largo + 2f); lineTo(x - 2f, 84f + largo - 6f); cubicTo(x + 2f, 84f + largo * 0.6f, x - 2f, 84f + largo * 0.3f, x, 85f); close() }
            drawPath(piel, Brush.horizontalGradient(listOf(z.en(Offset(x, 120f), lerp(color, C.HUESO, 0.2f), -1f), z.en(Offset(x + w, 120f), lerp(color, C.PIEL_OSC, 0.5f), -1f)), x, x + w))
            clipPath(piel) { for (j in 0..18) { val q = Offset(x + (j * 7 % w.toInt()).toFloat(), 90f + j * (largo / 20f)); drawLine(z.en(q, C.PIEL_OSC).copy(alpha = 0.4f), q, q + Offset(0.6f, 3.6f), 0.5f) } }
            for (j in 0..2) drawLine(z.en(Offset(x, 86f), C.HUESO), Offset(x + 3f + j * (w - 6f) / 2f, 82f), Offset(x + 3f + j * (w - 6f) / 2f, 88f), 0.8f)
        }
    }

    // ---------------------------------------------------------------- El suelo

    private val FORMA_SUELO: Path by lazy { poligono(listOf(Offset(-2f, 204f), Offset(40f, 205f), Offset(150f, 203f), Offset(152f, LABIO), Offset(216f, LABIO - 0.6f), Offset(262f, LABIO + 0.4f), Offset(302f, LABIO - 0.4f), Offset(347f, LABIO), Offset(350f, 203f), Offset(384f, 204f), Offset(384f, ALTO + 2f), Offset(-2f, ALTO + 2f))) }

    private fun DrawScope.suelo(m: Momento, z: LuzCueva) {
        val az = Azar(Semilla.de("cueva", "suelo")); val k = EscenaCueva.nieve(m); val manda = z.mandaElFuego
        // La nieve que el viento amontona en el labio, por fuera.
        if (k > 0.25f) drawPath(curva(listOf(Offset(214f, LABIO + 1f), Offset(236f, LABIO - 3.4f * k), Offset(262f, LABIO - 1.6f * k), Offset(296f, LABIO - 4f * k), Offset(330f, LABIO - 2f * k), Offset(348f, LABIO + 1f)), LABIO + 2f), z.fuera.tono(C.NIEVE))
        planoV(z.a, FORMA_SUELO, 190f, LABIO, ALTO, C.TIERRA)
        clipPath(FORMA_SUELO) {
            // Hacia el fondo de la izquierda, más oscuro; el paso pisado, de la boca al lecho, más claro.
            drawRect(Brush.horizontalGradient(0f to z.en(Offset(4f, 240f), C.HOLLIN).copy(alpha = 0.34f), 1f to Color.Transparent, startX = 0f, endX = 150f), Offset(0f, LABIO), Size(150f, 90f))
            drawPath(Path().apply { moveTo(262f, LABIO + 4f); cubicTo(220f, 226f, 170f, 262f, 60f, 268f) }, z.en(Offset(180f, 244f), lerp(C.TIERRA, C.CALIZA, 0.4f)).copy(alpha = 0.1f), style = Stroke(26f, cap = StrokeCap.Round))
            repeat(46) { val c = Offset(az.entre(0f, ANCHO), az.entre(LABIO + 4f, ALTO)); val w = az.entre(10f, 30f) * (0.6f + (c.y - LABIO) / 80f); mancha(c, w, w * 0.14f, z.en(c, if (it % 2 == 0) lerp(C.TIERRA, C.HOLLIN, 0.4f) else lerp(C.TIERRA, C.CALIZA, 0.35f)), 0.3f) }
            // La tierra pisada: rayas cortas, claras y oscuras, y los tizones que saltan del fuego.
            repeat(70) { val c = Offset(az.entre(0f, ANCHO), az.entre(LABIO + 5f, ALTO)); val w = az.entre(3f, 10f) * (0.6f + (c.y - LABIO) / 80f); drawLine(z.en(c, if (it % 2 == 0) lerp(C.TIERRA, C.HOLLIN, 0.5f) else lerp(C.TIERRA, C.CALIZA, 0.5f)).copy(alpha = 0.4f), c, c + Offset(w, az.entre(-0.4f, 0.4f)), 0.6f) }
            drawPoints((0 until 40).map { HOGAR + Offset(az.entre(-74f, 74f), az.entre(-6f, 26f)) }, PointMode.Points, z.dentro.tono(C.HOLLIN).copy(alpha = 0.7f), 1.5f, StrokeCap.Round)
            // La senda pisada, de la boca al lecho: tierra apretada, más clara y lisa, a manchas.
            for ((x, y, w) in listOf(Triple(262f, 206f, 20f), Triple(244f, 216f, 24f), Triple(222f, 228f, 22f), Triple(196f, 240f, 20f), Triple(150f, 262f, 30f), Triple(108f, 268f, 30f), Triple(66f, 270f, 26f))) { mancha(Offset(x, y), w, w * 0.22f, z.en(Offset(x, y), lerp(C.TIERRA, C.CALIZA, 0.45f)), 0.34f); mancha(Offset(x + 4f, y + 2.4f), w * 0.7f, w * 0.1f, z.en(Offset(x, y), lerp(C.TIERRA, C.HOLLIN, 0.4f)), 0.26f) }
            // El cenicero: la ceniza que se saca del hogar y se va esparciendo alrededor, gris clara, con sus carbones.
            val ceniza = Azar(Semilla.de("cueva", "cenicero"))
            drawPath(FaunaCueva.masa(HOGAR + Offset(-4f, 7f), 74f, 17f, ceniza, 16, 0.22f), Brush.radialGradient(0f to z.en(HOGAR + Offset(-50f, 8f), lerp(C.CENIZA, C.CALIZA, 0.3f)).copy(alpha = 0.75f), 0.7f to z.en(HOGAR + Offset(-50f, 8f), C.CENIZA).copy(alpha = 0.5f), 1f to Color.Transparent, center = HOGAR + Offset(-4f, 7f), radius = 76f))
            drawPath(FaunaCueva.masa(HOGAR + Offset(-58f, 12f), 20f, 6f, ceniza, 9, 0.3f), z.en(HOGAR + Offset(-58f, 12f), lerp(C.CENIZA, C.CALIZA, 0.4f)).copy(alpha = 0.7f))
            drawPoints((0 until 50).map { HOGAR + Offset(ceniza.entre(-70f, 66f), ceniza.entre(-6f, 22f)) }, PointMode.Points, z.en(HOGAR + Offset(-40f, 10f), C.CALIZA).copy(alpha = 0.6f), 1.2f, StrokeCap.Round)
            // Guijarros, lascas de pedernal y huesos partidos.
            for (g in GUIJARROS) { val c = Offset(g.a, g.b); val w = g.c; drawOval(z.en(c, C.HOLLIN).copy(alpha = 0.3f), Offset(c.x - w, c.y - w * 0.1f), Size(w * 2.2f, w * 0.7f)); drawOval(z.en(c, lerp(C.ROCA, C.CALIZA, g.d)), Offset(c.x - w, c.y - w * 0.5f), Size(w * 2f, w * 0.9f)); drawLine(z.en(c, C.CALIZA).copy(alpha = 0.6f), Offset(c.x - w * 0.5f, c.y - w * 0.4f), Offset(c.x + w * 0.4f, c.y - w * 0.44f), 0.5f) }
            repeat(12) { val c = Offset(az.entre(150f, 330f), az.entre(LABIO + 14f, ALTO - 6f)); drawPath(poligono(c, c + Offset(az.entre(2.4f, 4.4f), -az.entre(1f, 2f)), c + Offset(az.entre(4f, 6f), 0.6f)), z.en(c, lerp(C.HOLLIN, C.CALIZA, 0.5f))) }
            for ((x, y, ang) in listOf(Triple(180f, 262f, 0.3f), Triple(304f, 278f, -0.2f), Triple(76f, 266f, 0.1f))) { val a = Offset(x, y); val b = a + Offset(cos(ang) * 11f, sin(ang) * 3f); drawLine(z.en(a, C.HUESO), a, b, 1.7f, StrokeCap.Round); drawCircle(z.en(a, C.HUESO), 1.4f, a); drawCircle(z.en(b, C.HUESO), 1.5f, b) }
            // Una piel de reno tendida junto al fuego, para sentarse; encima, las hojas de pedernal y un candil de asta.
            val estera = FaunaCueva.cerrada(listOf(Offset(118f, 259f), Offset(130f, 261f), Offset(140f, 257f), Offset(160f, 256.4f), Offset(178f, 257f), Offset(188f, 254.6f), Offset(196f, 257f), Offset(190f, 262f), Offset(192f, 267f), Offset(200f, 271f), Offset(190f, 274f), Offset(178f, 270.6f), Offset(158f, 271.6f), Offset(140f, 271f), Offset(128f, 275f), Offset(118f, 273f), Offset(126f, 267f), Offset(124f, 263f)))
            drawPath(Path().apply { addPath(estera, Offset(1.4f, 1.8f)) }, z.dentro.tono(C.HOLLIN).copy(alpha = 0.3f))
            drawPath(estera, Brush.horizontalGradient(listOf(z.en(Offset(120f, 264f), lerp(C.PELO, C.ROCA, 0.3f), -1f), z.en(Offset(198f, 264f), lerp(C.PELO, C.HUESO, 0.4f), 1f)), 118f, 200f))
            clipPath(estera) { repeat(80) { val q = Offset(az.entre(118f, 200f), az.entre(255f, 275f)); drawLine(z.en(q, if (it % 2 == 0) C.HUESO else C.PIEL_OSC).copy(alpha = 0.4f), q, q + Offset(az.entre(1.4f, 3.4f), az.entre(-0.4f, 1f)), 0.5f) } }
            drawPath(estera, z.en(Offset(160f, 264f), C.HUESO, 1f).copy(alpha = 0.3f), style = Stroke(0.8f))
            for ((x, y, g) in listOf(Triple(146f, 262f, 0.3f), Triple(153f, 264f, -0.2f), Triple(160f, 261.6f, 0.5f))) drawPath(poligono(Offset(x, y), Offset(x + 6f, y - 1.6f + g), Offset(x + 7.6f, y - 0.4f + g), Offset(x + 1f, y + 1.3f)), z.en(Offset(x, y), lerp(C.HOLLIN, C.HIELO, 0.45f)))
            drawLine(z.en(Offset(170f, 265f), C.HUESO), Offset(166f, 266.6f), Offset(180f, 263f), 2f, StrokeCap.Round); drawLine(z.en(Offset(170f, 265f), C.HUESO), Offset(178f, 263.4f), Offset(181f, 260.6f), 1.4f, StrokeCap.Round)
            // El yunque de tallar, a la derecha: una piedra plana con su percutor, el núcleo de sílex y las lascas alrededor, que brillan.
            val yunque = Offset(364f, 268f); val talla = Azar(Semilla.de("cueva", "yunque"))
            drawOval(z.dentro.tono(C.HOLLIN).copy(alpha = 0.3f), Offset(yunque.x - 15f, yunque.y - 1f), Size(30f, 5f))
            drawPath(poligono(listOf(yunque + Offset(-13f, 1f), yunque + Offset(-11f, -6f), yunque + Offset(-4f, -8.4f), yunque + Offset(9f, -8f), yunque + Offset(13f, -4f), yunque + Offset(13f, 1f))), z.en(yunque, lerp(C.ROCA, C.HOLLIN, 0.2f), -1f))
            drawPath(poligono(yunque + Offset(-11f, -6f), yunque + Offset(-4f, -8.4f), yunque + Offset(9f, -8f), yunque + Offset(13f, -4f), yunque + Offset(-6f, -3.6f)), z.en(yunque, lerp(C.ROCA, C.CALIZA, 0.5f), -1f))
            drawPath(poligono(yunque + Offset(-3f, -6.4f), yunque + Offset(0f, -11f), yunque + Offset(5f, -10f), yunque + Offset(6f, -6f)), z.en(yunque, lerp(C.HOLLIN, C.HIELO, 0.3f), -1f)); drawOval(z.en(yunque, lerp(C.ROCA, C.CALIZA, 0.3f), -1f), Offset(yunque.x - 22f, yunque.y - 3f), Size(8f, 5f))
            drawPoints((0 until 34).map { yunque + Offset(talla.entre(-30f, 14f), talla.entre(-4f, 12f)) }, PointMode.Points, z.en(yunque, lerp(C.HOLLIN, C.HIELO, 0.55f), -1f), 1.5f, StrokeCap.Square)
            drawPoints((0 until 12).map { yunque + Offset(talla.entre(-26f, 12f), talla.entre(-3f, 11f)) }, PointMode.Points, z.en(yunque, C.HIELO, -1f), 1f, StrokeCap.Square)
            // Más huesos: una quijada, en el rincón del lecho.
            drawPath(poligono(Offset(84f, 278f), Offset(100f, 274.6f), Offset(104f, 277f), Offset(96f, 279f), Offset(88f, 281f)), z.en(Offset(92f, 278f), C.HUESO)); drawPoints(listOf(Offset(90f, 277.4f), Offset(93f, 276.8f), Offset(96f, 276.2f)), PointMode.Points, z.en(Offset(92f, 278f), C.PIEL_OSC), 0.9f, StrokeCap.Round)
            // La mancha de sol que entra por la boca, con su borde blando; de noche, la de la luna.
            val r = z.sol
            if (r.fuerza > 0.05f) { val parche = parcheDeSol(r); val c = lerp(z.fuera.solColor, Color(0xFFFFD49A), 0.4f); for (g in listOf(9f, 5f)) drawPath(parche, c.copy(alpha = 0.035f * r.fuerza), style = Stroke(g), blendMode = BlendMode.Plus); drawPath(parche, c.copy(alpha = 0.26f * r.fuerza), blendMode = BlendMode.Plus) }
            else if (z.noche > 0.6f && z.fuera.nublado < 0.5f) drawPath(poligono(Offset(160f, LABIO + 1f), Offset(340f, LABIO + 1f), Offset(318f, LABIO + 26f), Offset(138f, LABIO + 26f)), C.LUNA.copy(alpha = 0.1f * z.noche), blendMode = BlendMode.Plus)
            // El charco de luz del fuego.
            if (manda > 0.05f) { charcoDeLuz(z.fuego, HOGAR + Offset(0f, 6f), 120f, 30f, C.TIERRA, 0.9f * manda); charcoDeLuz(z.fuego, HOGAR + Offset(0f, 4f), 56f, 14f, C.CENIZA, 0.5f * manda) }
            // El rescoldo, por bajo que esté, deja de noche un charco cálido y corto alrededor del corro.
            if (z.noche > 0.3f) charcoDeLuz(Foco(HOGAR, C.LUMBRE, 0.9f * z.noche, 60f), HOGAR + Offset(0f, 7f), 78f, 19f, C.TIERRA, (1.1f - 0.6f * z.lumbre).coerceIn(0.3f, 1f))
            // Lejos de la lumbre, el suelo se pierde en lo oscuro: los rincones.
            if (manda > 0.05f) drawRect(Brush.radialGradient(0f to Color.Transparent, 0.45f to Color.Transparent, 1f to z.dentro.tono(C.HOLLIN).copy(alpha = 0.62f * manda), center = HOGAR + Offset(0f, -10f), radius = 250f), Offset(0f, LABIO), Size(ANCHO, ALTO - LABIO + 2f))
        }
        clipPath(FORMA_SUELO) { val dia = (1f - z.noche).coerceIn(0f, 1f); drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to z.dentro.tono(C.HOLLIN).copy(alpha = 0.34f * dia), startY = LABIO + 30f, endY = ALTO), Offset(0f, LABIO + 30f), Size(ANCHO, ALTO - LABIO - 28f)) }
        // Las piedras del labio, de la boca hacia la derecha: el umbral.
        var x = 216f
        while (x < 346f) {
            val w = az.entre(9f, 18f); val h = az.entre(3f, 6.4f); val c = Offset(x + w / 2f, LABIO + 2f)
            val piedra = poligono(Offset(x, LABIO + 4f), Offset(x + w * 0.16f, LABIO + 2f - h), Offset(x + w * 0.7f, LABIO + 1f - h), Offset(x + w, LABIO + 4f))
            drawPath(piedra, z.en(c, lerp(C.ROCA, C.CALIZA, az.real() * 0.5f))); drawLine(z.fuera.tono(lerp(C.CALIZA, C.NIEVE, k)).copy(alpha = 0.7f), Offset(x + w * 0.16f, LABIO + 2f - h), Offset(x + w * 0.7f, LABIO + 1f - h), 0.9f)
            x += w - 1.5f
        }
    }

    // ---------------------------------------------------------------- Las cosas

    private data class Cuatro(val a: Float, val b: Float, val c: Float, val d: Float)

    /** Los guijarros del suelo: dónde están (a, b), su tamaño (c) y lo claros que son (d). */
    private val GUIJARROS: List<Cuatro> by lazy { val az = Azar(Semilla.de("cueva", "guijarros")); (0 until 46).map { val y = az.entre(LABIO + 6f, ALTO - 2f); Cuatro(az.entre(4f, ANCHO - 4f), y, az.entre(1.6f, 4.6f) * (0.7f + (y - LABIO) / 90f), az.real()) } }

    /** La sombra de contacto de una cosa: un óvalo oscuro bajo su pie. */
    private fun DrawScope.asiento(z: LuzCueva, pie: Offset, ancho: Float) { drawOval(z.dentro.tono(C.HOLLIN).copy(alpha = 0.32f), Offset(pie.x - ancho / 2f, pie.y - 2.4f), Size(ancho, 5.4f)) }

    private fun cosas(m: Momento, z: LuzCueva): List<Cosa> = listOf(murete(m, z), lecho(m, z), losa(m, z), hogar(m, z), lena(m, z), bastidor(m, z), lanzas(m, z))

    /** El murete de piedras del labio, a la izquierda de la boca: corta el viento a ras de suelo. */
    private fun murete(m: Momento, z: LuzCueva): Cosa {
        val forma = poligono(listOf(Offset(151f, 203f), Offset(152f, 187f), Offset(166f, 182.6f), Offset(190f, 181.6f), Offset(208f, 185f), Offset(217f, 192f), Offset(218f, 203f)))
        return Cosa(forma, Offset(184f, 203f), false) {
            val az = Azar(Semilla.de("cueva", "murete")); val k = EscenaCueva.nieve(m)
            drawPath(forma, z.en(Offset(184f, 194f), lerp(C.ROCA, C.HOLLIN, 0.4f)))
            // Piedras apiladas en seco, desiguales: las grandes abajo, calzadas con otras chicas; ninguna hilera va a cordel.
            var y = 203.4f; var fila = 0
            while (y > 185.4f) {
                val alto = if (fila == 0) az.entre(7.4f, 9f) else az.entre(4.6f, 7.4f)
                var x = 151.6f + fila * 1.4f + az.entre(-1f, 3f); val fin = 218.6f - fila * 3.2f - az.entre(0f, 5f)
                while (x < fin - 3f) {
                    val w = (if (az.entero(4) == 0) az.entre(5f, 8f) else az.entre(10f, 21f)).coerceAtMost(fin - x); val h = alto * az.entre(0.7f, 1.16f); val c = Offset(x + w / 2f, y - h / 2f + az.entre(-1.2f, 0.6f))
                    val color = lerp(C.ROCA, C.CALIZA, az.entre(0.05f, 0.75f)); val piedra = FaunaCueva.masa(c, w * 0.54f, h * 0.56f, az, 7, 0.22f)
                    drawPath(piedra, Brush.verticalGradient(listOf(z.en(c, lerp(color, Color.White, 0.14f)), z.en(c, color), z.en(c, lerp(color, C.HOLLIN, 0.36f))), c.y - h * 0.5f, c.y + h * 0.5f))
                    if (y - alto < 187f) drawLine(z.fuera.tono(lerp(C.CALIZA, C.NIEVE, k)).copy(alpha = 0.6f), Offset(c.x - w * 0.3f, c.y - h * 0.54f), Offset(c.x + w * 0.3f, c.y - h * 0.5f), 0.8f)
                    x += w - az.entre(0f, 1.4f)
                }
                y -= alto * 0.84f; fila++
            }
            if (k > 0.4f) drawPath(curva(listOf(Offset(160f, 183f), Offset(176f, 180.4f), Offset(196f, 180f), Offset(210f, 185f)), 185f), z.fuera.tono(C.NIEVE).copy(alpha = 0.9f))
        }
    }

    /** Un contorno de piel con el pelo en mechones: la elipse de [c], con el borde en picos. */
    private fun peluda(c: Offset, rx: Float, ry: Float, az: Azar, picos: Int = 46, largo: Float = 2.2f): Path = poligono((0 until picos).map { i ->
        val a = i * 6.2832f / picos; val k = 1f + (if (i % 2 == 0) largo else -largo * 0.3f) * az.entre(0.5f, 1.2f) / rx
        c + Offset(cos(a) * rx * k * (1f + 0.08f * sin(a * 3f + 1f)), sin(a) * ry * k * (1f + 0.1f * cos(a * 2f)))
    })

    /** El lecho: una piel grande de bisonte, oscura y lanuda; encima, una de reno, clara, medio doblada; y un rollo de piel por cabecera. */
    private fun lecho(m: Momento, z: LuzCueva): Cosa {
        val forma = Path().apply { moveTo(6f, 247f); cubicTo(4f, 226f, 24f, 214f, 48f, 213f); cubicTo(72f, 211f, 100f, 219f, 110f, 232f); cubicTo(114f, 240f, 112f, 245f, 108f, 248f); close() }
        return Cosa(forma, Offset(58f, 248f), false) {
            val az = Azar(Semilla.de("cueva", "lecho"))
            asiento(z, Offset(58f, 248f), 112f)
            // La piel de bisonte.
            val bisonte = peluda(Offset(58f, 233f), 52f, 14f, az, 56, 2.6f); val pardo = lerp(C.PIEL_OSC, C.HOLLIN, 0.2f)
            drawPath(bisonte, Brush.horizontalGradient(listOf(z.en(Offset(10f, 232f), pardo, -1f), z.en(Offset(104f, 232f), lerp(pardo, C.PELO, 0.4f), 1f)), 6f, 110f))
            clipPath(bisonte) {
                drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to z.dentro.tono(C.HOLLIN).copy(alpha = 0.5f), startY = 236f, endY = 248f), Offset(4f, 236f), Size(110f, 13f))
                repeat(120) { val q = Offset(az.entre(8f, 110f), az.entre(220f, 247f)); drawLine(z.en(q, if (it % 3 == 0) C.PELO else C.HOLLIN).copy(alpha = 0.4f), q, q + Offset(az.entre(0.6f, 2.4f), az.entre(1.4f, 3.4f)), 0.6f) }
            }
            // La de reno, encima: el pelo gris pardo, la grupa blanca y un pico doblado que enseña el cuero.
            val reno = peluda(Offset(68f, 229f), 34f, 9f, az, 40, 1.8f); val gris = lerp(C.PELO, C.ROCA, 0.4f)
            drawPath(reno, Brush.horizontalGradient(listOf(z.en(Offset(36f, 229f), lerp(gris, C.PIEL_OSC, 0.3f), -1f), z.en(Offset(80f, 229f), gris, 1f), z.en(Offset(100f, 229f), lerp(C.HUESO, Color.White, 0.2f), 1f)), 34f, 102f))
            clipPath(reno) { repeat(70) { val q = Offset(az.entre(36f, 102f), az.entre(221f, 237f)); drawLine(z.en(q, if (it % 2 == 0) C.HUESO else C.PIEL_OSC, 1f).copy(alpha = 0.4f), q, q + Offset(az.entre(1f, 2.6f), az.entre(0.4f, 1.6f)), 0.5f) } }
            val doblez = poligono(Offset(40f, 233f), Offset(56f, 236.6f), Offset(46f, 238.6f), Offset(36f, 236f))
            drawPath(doblez, z.en(Offset(46f, 236f), lerp(C.PIEL, C.HUESO, 0.3f), 1f)); drawPath(doblez, z.en(Offset(46f, 236f), C.PIEL_OSC).copy(alpha = 0.5f), style = Stroke(0.5f))
            // El rollo de piel, atado con dos correas: la cabecera.
            val rollo = Path().apply { moveTo(12f, 232f); cubicTo(10f, 222f, 18f, 216f, 26f, 217f); lineTo(46f, 220f); cubicTo(52f, 222f, 52f, 232f, 46f, 234f); lineTo(22f, 237f); cubicTo(16f, 237f, 13f, 235f, 12f, 232f); close() }
            drawPath(rollo, Brush.verticalGradient(listOf(z.en(Offset(30f, 218f), lerp(C.PIEL, C.HUESO, 0.25f), 1f), z.en(Offset(30f, 228f), C.PIEL), z.en(Offset(30f, 236f), C.PIEL_OSC)), 216f, 237f))
            drawOval(z.en(Offset(46f, 227f), lerp(C.PIEL, C.PIEL_OSC, 0.4f), 1f), Offset(41.4f, 220.2f), Size(9f, 13.6f)); for (rr in listOf(3.4f, 1.8f)) drawOval(z.en(Offset(46f, 227f), C.PIEL_OSC).copy(alpha = 0.7f), Offset(46f - rr, 227f - rr * 1.5f), Size(rr * 2f, rr * 3f), style = Stroke(0.6f))
            for (x in listOf(20f, 33f)) drawPath(Path().apply { moveTo(x, 217.4f + (x - 20f) * 0.12f); cubicTo(x - 3f, 224f, x - 3f, 231f, x + 0.6f, 237f) }, z.en(Offset(x, 226f), C.HUESO), style = Stroke(1f))
        }
    }

    /** La laja donde Ama deja sus cosas: encima, el objeto del día. */
    private fun losa(m: Momento, z: LuzCueva): Cosa {
        val forma = poligono(listOf(Offset(100f, 250f), Offset(102f, 240f), Offset(110f, 236.4f), Offset(158f, 235.6f), Offset(165f, 240f), Offset(166f, 250f)))
        return Cosa(Path().apply { addPath(forma); addRect(Rect(112f, 218f, 154f, 238f)) }, Offset(132f, 251f), false) {
            asiento(z, Offset(133f, 251f), 76f)
            drawPath(forma, Brush.verticalGradient(listOf(z.en(Offset(132f, 242f), lerp(C.ROCA, C.CALIZA, 0.3f)), z.en(Offset(132f, 250f), lerp(C.ROCA, C.HOLLIN, 0.4f))), 240f, 250f))
            drawPath(poligono(listOf(Offset(102f, 240f), Offset(110f, 236.4f), Offset(158f, 235.6f), Offset(165f, 240f), Offset(160f, 242f), Offset(106f, 242.6f))), z.en(Offset(132f, 238f), lerp(C.CALIZA, C.ROCA, 0.2f)))
            drawLine(z.en(Offset(132f, 242f), C.HOLLIN).copy(alpha = 0.4f), Offset(118f, 242.4f), Offset(124f, 249f), 0.5f)
            val p = LOSA
            val t = ObjetosCueva.Tinte({ z.en(p, lerp(it, Color.White, 0.16f), if (HOGAR.x > p.x) 1f else -1f) }, { z.en(p, it) }, { z.en(p, lerp(it, C.HOLLIN, 0.3f), -1f) })
            with(ObjetosCueva) { objeto(m.ficha.objeto, t, p, z.noche) }
        }
    }

    /** El hogar: un corro grande de piedras, la ceniza y los leños. La llama va en la parte viva. */
    private fun hogar(m: Momento, z: LuzCueva): Cosa {
        val forma = Path().apply { moveTo(HOGAR.x - 41f, HOGAR.y + 9f); cubicTo(HOGAR.x - 36f, HOGAR.y - 13f, HOGAR.x + 36f, HOGAR.y - 13f, HOGAR.x + 41f, HOGAR.y + 9f); close() }
        return Cosa(forma, HOGAR + Offset(0f, 10f), false) {
            val az = Azar(Semilla.de("cueva", "hogar")); val f = EscenaCueva.lumbre(m).coerceIn(0f, 1.3f); val grande = EscenaCueva.fuegoGrande(m); val vivo = f.coerceAtMost(1f)
            asiento(z, HOGAR + Offset(0f, 10f), 92f)
            // Las piedras del corro: por dentro, tiznadas y encendidas por la lumbre; por fuera, del color de la cueva.
            fun piedra(a: Float, delante: Boolean) {
                val c = HOGAR + Offset(cos(a) * 37f, sin(a) * 10.6f); val w = az.entre(8f, 12f); val h = az.entre(6f, 8.6f); val color = lerp(C.ROCA, C.HOLLIN, az.entre(0.1f, 0.4f))
                val fuera = z.en(c, lerp(color, C.CALIZA, 0.2f), if (c.x < HOGAR.x) -1f else 1f); val dentro = lerp(z.en(c, lerp(color, C.HOLLIN, 0.4f)), C.LUMBRE, (0.2f + 0.45f * vivo))
                val forma = FaunaCueva.masa(c + Offset(0f, -h * 0.34f), w, h, az, 8, 0.14f)
                drawPath(forma, Brush.horizontalGradient(if (c.x < HOGAR.x) listOf(fuera, dentro) else listOf(dentro, fuera), c.x - w, c.x + w))
                // De frente, la cara que da a quien mira queda a contraluz; arriba, un filo de lumbre.
                if (delante) drawPath(FaunaCueva.masa(c + Offset(0f, h * 0.05f), w * 0.92f, h * 0.6f, az, 7, 0.14f), z.dentro.tono(lerp(color, C.HOLLIN, 0.5f)).copy(alpha = 0.5f + 0.3f * z.noche))
                drawLine(lerp(z.en(c, C.CALIZA), C.LUMBRE, 0.5f * vivo).copy(alpha = 0.8f), c + Offset(-w * 0.6f, -h * 1.1f), c + Offset(w * 0.5f, -h * 1.16f), 0.9f, StrokeCap.Round)
            }
            for (j in 0..6) piedra(3.36f + j * 0.45f, false)
            drawOval(z.en(HOGAR, lerp(C.CENIZA, C.HOLLIN, 0.4f)), Offset(HOGAR.x - 33f, HOGAR.y - 8.6f), Size(66f, 18f))
            drawOval(lerp(z.en(HOGAR, C.HOLLIN), lerp(C.LUMBRE, C.OCRE_ROJO, 0.4f), (0.3f + 0.5f * f).coerceAtMost(0.9f)), Offset(HOGAR.x - 22f, HOGAR.y - 5.4f), Size(44f, 12f))
            drawOval(lerp(C.LUMBRE, Color.White, 0.3f).copy(alpha = (0.2f + 0.5f * vivo)), Offset(HOGAR.x - 11f, HOGAR.y - 2.6f), Size(22f, 6f))
            for ((a, b) in listOf(Offset(-28f, 3f) to Offset(10f, -9f), Offset(26f, 4f) to Offset(-8f, -10f), Offset(-9f, 6f) to Offset(19f, -4f)) + if (grande) listOf(Offset(-19f, -1f) to Offset(5f, -21f), Offset(19f, -1f) to Offset(-3f, -24f)) else emptyList()) {
                drawLine(z.en(HOGAR, lerp(C.HOLLIN, C.MADERA, 0.4f)), HOGAR + a, HOGAR + b, 6f, StrokeCap.Round); drawLine(lerp(z.en(HOGAR, C.MADERA), C.LUMBRE, 0.5f * vivo), HOGAR + a + Offset(0f, -1.7f), HOGAR + b + Offset(0f, -1.7f), 1.6f, StrokeCap.Round)
                drawCircle(lerp(C.OCRE_ROJO, C.LUMBRE, 0.6f).copy(alpha = (0.4f + 0.5f * f).coerceAtMost(1f)), 2.6f, HOGAR + b)
                drawCircle(z.en(HOGAR, lerp(C.MADERA, C.OCRE_AMARILLO, 0.4f)), 2.6f, HOGAR + a); drawCircle(z.en(HOGAR, C.MADERA).copy(alpha = 0.7f), 1.3f, HOGAR + a, style = Stroke(0.5f))
            }
            for (j in 0..6) piedra(0.2f + j * 0.46f, true)
            // Una piedra plana junto al fuego, para asar.
            drawOval(z.en(HOGAR + Offset(-50f, 8f), lerp(C.ROCA, C.CALIZA, 0.4f)), Offset(HOGAR.x - 62f, HOGAR.y + 5f), Size(18f, 5.4f))
        }
    }

    /** La leña: un montón de leños cortos y ramas, a mano del fuego. */
    private fun lena(m: Momento, z: LuzCueva): Cosa {
        val forma = poligono(listOf(Offset(284f, 264f), Offset(288f, 251f), Offset(302f, 245f), Offset(320f, 247f), Offset(326f, 256f), Offset(325f, 264f)))
        return Cosa(forma, Offset(LENA, 265f), false) {
            val az = Azar(Semilla.de("cueva", "lena")); val hoy = m.azar("lena"); val cuantos = 5 + hoy.entero(4)
            asiento(z, Offset(LENA, 265f), 46f)
            // Los leños, vistos de testa, en dos o tres hileras; encima, unas ramas.
            val sitios = listOf(Offset(291f, 260f), Offset(301f, 260.6f), Offset(311f, 260f), Offset(320f, 260.4f), Offset(296f, 253.6f), Offset(306f, 253f), Offset(316f, 253.6f), Offset(301f, 247.6f), Offset(311f, 247.4f))
            for (c in sitios.take(cuantos)) {
                val rr = az.entre(4.4f, 5.4f); val corteza = lerp(C.MADERA, if (az.entero(3) == 0) C.HUESO else C.PIEL_OSC, 0.4f)
                drawCircle(z.en(c, corteza, -1f), rr, c); drawCircle(z.en(c, lerp(C.MADERA, C.OCRE_AMARILLO, 0.5f), -1f), rr * 0.74f, c); drawCircle(z.en(c, C.MADERA).copy(alpha = 0.6f), rr * 0.4f, c, style = Stroke(0.5f)); drawCircle(z.en(c, C.PIEL_OSC), 0.6f, c)
            }
            for (j in 0..2) drawLine(z.en(Offset(306f, 246f), lerp(C.MADERA, C.HOLLIN, 0.3f)), Offset(266f + j * 4f, 250f - j * 1.6f), Offset(310f - j * 3f, 243f + j * 1.4f), 1.3f, StrokeCap.Round)
        }
    }

    /** El bastidor: cuatro palos atados, y dentro una piel tensada con correas para rasparla. */
    private fun bastidor(m: Momento, z: LuzCueva): Cosa {
        val a = Offset(304f, 241f); val b = Offset(352f, 237f); val c = Offset(350f, 164f); val d = Offset(309f, 168f)
        val forma = poligono(a, b, c, d)
        return Cosa(forma, Offset(328f, 242f), false) {
            asiento(z, Offset(328f, 242f), 56f)
            val palo = lerp(C.MADERA, C.HUESO, 0.2f)
            // La piel: el lado de la carne, claro, con las patas tiradas hacia las esquinas.
            val piel = Path().apply {
                moveTo(309f, 235f); cubicTo(314f, 226f, 312f, 214f, 313f, 204f); cubicTo(313f, 190f, 311f, 180f, 313f, 173f); cubicTo(322f, 178f, 338f, 176f, 346f, 169f)
                cubicTo(344f, 180f, 346f, 196f, 345f, 208f); cubicTo(345f, 218f, 344f, 226f, 348f, 232f); cubicTo(338f, 228f, 320f, 229f, 309f, 235f); close()
            }
            val reciente = EscenaCueva.caza(m)
            val color = if (reciente) lerp(C.PIEL, C.OCRE_ROJO, 0.22f) else lerp(C.PIEL, C.HUESO, 0.35f)
            drawPath(piel, Brush.horizontalGradient(listOf(z.en(Offset(310f, 200f), lerp(color, Color.White, 0.1f), -1f), z.en(Offset(346f, 200f), lerp(color, C.PIEL_OSC, 0.3f), -1f)), 309f, 348f))
            clipPath(piel) { val az = Azar(Semilla.de("cueva", "piel")); repeat(10) { val q = Offset(az.entre(312f, 344f), az.entre(176f, 230f)); mancha(q, az.entre(4f, 9f), az.entre(3f, 7f), z.en(q, if (it % 2 == 0) C.PIEL_OSC else C.HUESO, -1f), 0.2f) }; for (j in 0..5) drawLine(z.en(Offset(328f, 200f), C.HUESO, -1f).copy(alpha = 0.3f), Offset(315f + j * 5f, 182f + j * 2f), Offset(318f + j * 5f, 222f - j), 0.6f) }
            drawPath(piel, z.en(Offset(328f, 200f), C.PIEL_OSC, -1f).copy(alpha = 0.6f), style = Stroke(0.6f))
            // Las correas, del borde de la piel a los palos.
            for ((p, q) in listOf(Offset(313f, 204f) to Offset(306.6f, 204f), Offset(313f, 186f) to Offset(307.6f, 188f), Offset(345f, 208f) to Offset(351f, 206f), Offset(345f, 188f) to Offset(350.6f, 186f), Offset(322f, 176.6f) to Offset(322f, 167.6f), Offset(338f, 175f) to Offset(338f, 165.6f), Offset(322f, 230f) to Offset(322f, 239.6f), Offset(338f, 229f) to Offset(338f, 238.4f)))
                drawLine(z.en(p, lerp(C.PIEL_OSC, C.HUESO, 0.3f)), p, q, 0.7f)
            // Los cuatro palos, con sus ataduras en las esquinas.
            for ((p, q) in listOf(a + Offset(0f, 2f) to d + Offset(0f, -4f), b + Offset(0f, 2f) to c + Offset(0f, -4f), d + Offset(-4f, 0f) to c + Offset(4f, 0f), a + Offset(-3f, -3f) to b + Offset(3f, -3f))) { drawLine(z.en(p, lerp(palo, C.HOLLIN, 0.4f), -1f), p + Offset(0.8f, 0.6f), q + Offset(0.8f, 0.6f), 3f, StrokeCap.Round); drawLine(z.en(p, palo, -1f), p, q, 2.2f, StrokeCap.Round) }
            for (e in listOf(a + Offset(0f, -3f), b + Offset(0f, -3f), c, d)) { drawCircle(z.en(e, C.PIEL_OSC), 2.4f, e); drawLine(z.en(e, C.HUESO), e + Offset(-2f, -1.6f), e + Offset(2f, 1.6f), 0.6f) }
            // El raspador y las virutas, al pie.
            drawPath(poligono(Offset(300f, 245f), Offset(304f, 241f), Offset(310f, 243.6f), Offset(306f, 246f)), z.en(Offset(304f, 244f), lerp(C.HOLLIN, C.CALIZA, 0.5f)))
        }
    }

    /** Las azagayas apoyadas en el pilar y las astas de la caza, amontonadas al pie. */
    private fun lanzas(m: Momento, z: LuzCueva): Cosa {
        val forma = poligono(Offset(354f, 240f), Offset(376f, 240f), Offset(380f, 118f), Offset(368f, 118f))
        return Cosa(forma, Offset(364f, 241f), false) {
            asiento(z, Offset(364f, 241f), 34f)
            for ((x0, x1, y1) in listOf(Triple(358f, 371f, 112f), Triple(364f, 376f, 104f), Triple(371f, 379f, 120f))) {
                val a = Offset(x0, 238f); val b = Offset(x1, y1)
                drawLine(z.en(Offset(x0, 180f), lerp(C.MADERA, C.HOLLIN, 0.3f), -1f), a + Offset(0.7f, 0f), b + Offset(0.7f, 0f), 1.9f, StrokeCap.Round); drawLine(z.en(Offset(x0, 180f), lerp(C.MADERA, C.HUESO, 0.2f), -1f), a, b, 1.2f, StrokeCap.Round)
                drawPath(poligono(b + Offset(-1.4f, 1f), b + Offset(1.6f, 1f), b + Offset(1.2f, -11f)), z.en(b, C.HUESO, -1f)); drawLine(z.en(b, C.OCRE_ROJO, -1f), b + Offset(-1.6f, 3f), b + Offset(1.8f, 2.4f), 1.5f)
            }
            // Las astas: más, después de la caza de otoño.
            val cuantas = if (EscenaCueva.caza(m)) 3 else 1
            for (j in 0 until cuantas) {
                val o = Offset(352f + j * 6f, 240f - j * 3f); val hueso = lerp(C.HUESO, C.PIEL, 0.3f + 0.1f * j)
                val vara = Path().apply { moveTo(o.x, o.y); cubicTo(o.x + 4f, o.y - 12f, o.x + 14f, o.y - 16f, o.x + 22f, o.y - 12f) }
                drawPath(vara, z.en(o, lerp(hueso, C.HOLLIN, 0.3f), -1f), style = Stroke(2.8f, cap = StrokeCap.Round)); drawPath(vara, z.en(o, hueso, -1f), style = Stroke(1.5f, cap = StrokeCap.Round))
                for ((u, v) in listOf(4f to -9f, 10f to -14f, 17f to -14f)) drawLine(z.en(o, hueso, -1f), o + Offset(u, v), o + Offset(u - 2f, v - 8f), 1.3f, StrokeCap.Round)
            }
        }
    }

    /** Dónde arde la lámpara del luto, al pie del túmulo. */
    private val LUTO = Offset(296f, 197f)

    /** El túmulo de Yemi, fuera, junto a la senda: un montón de piedras con ocre rojo y un asta encima. Desde el día en que muere. */
    private fun DrawScope.tumulo(m: Momento, s: SolCueva) {
        if (!EscenaCueva.paso(m, 3, "02-10")) return
        val az = Azar(Semilla.de("cueva", "tumulo")); val k = EscenaCueva.nieve(m); val o = Offset(276f, 199f)
        drawOval(s.l.tono(C.SOMBRA).copy(alpha = 0.34f * s.r.fuerza), Offset(o.x - 22f + s.r.ax.coerceIn(-2f, 2f) * 5f, o.y - 3f), Size(46f, 6f))
        for ((dx, dy, w) in listOf(Triple(-15f, 0f, 13f), Triple(-3f, 0.6f, 15f), Triple(11f, 0f, 13f), Triple(-9f, -6.6f, 13f), Triple(4f, -7f, 14f), Triple(-3f, -13f, 12f), Triple(-2f, -18.4f, 8f)))
            with(FaunaCueva) { pena(s, o + Offset(dx, dy), w, w * 0.6f, az, k * 0.7f, 0.02f, lerp(C.ROCA, C.CALIZA, az.entre(0.2f, 0.6f))) }
        // El ocre rojo, espolvoreado por encima, y un asta de reno clavada.
        for ((dx, dy) in listOf(-8f to -10f, 4f to -11f, -2f to -17f, -3f to -22f)) mancha(o + Offset(dx, dy), 7f, 3f, s.medio(C.OCRE_ROJO), if (EscenaCueva.luto(m)) 0.75f else 0.45f)
        val asta = Path().apply { moveTo(o.x - 1f, o.y - 22f); cubicTo(o.x + 2f, o.y - 30f, o.x + 8f, o.y - 35f, o.x + 13f, o.y - 34f) }
        drawPath(asta, s.sombra(C.PIEL_OSC), style = Stroke(2.2f, cap = StrokeCap.Round)); drawPath(asta, s.luz(C.HUESO), style = Stroke(1.2f, cap = StrokeCap.Round))
        for ((u, v) in listOf(2f to -28f, 7f to -33f)) drawLine(s.luz(C.HUESO), o + Offset(u, v), o + Offset(u - 3f, v - 6f), 1.1f, StrokeCap.Round)
        if (EscenaCueva.luto(m)) { drawOval(s.medio(C.ROCA), Offset(LUTO.x - 4f, LUTO.y - 1f), Size(8f, 3f)) }
    }

    /** Lo que cuelga o se añade los días señalados: la carne de la caza y las ramas de la noche más larga. */
    private fun DrawScope.adornos(m: Momento, z: LuzCueva) {
        if (EscenaCueva.caza(m)) {
            // Un palo encajado en la roca, en la esquina de la boca, con tiras de carne puestas a secar.
            val a = Offset(148f, 112f); val b = Offset(208f, 62f)
            drawLine(z.en(a, lerp(C.MADERA, C.HOLLIN, 0.3f)), a, b, 2.4f, StrokeCap.Round); drawLine(z.en(a, C.MADERA), a + Offset(0f, -0.8f), b + Offset(0f, -0.8f), 1f, StrokeCap.Round)
            val az = Azar(Semilla.de("cueva", "carne"))
            for (j in 1..8) { val q = a + (b - a) * (j / 9f); val largo = az.entre(12f, 20f); drawPath(poligono(q + Offset(-1.6f, 0f), q + Offset(1.6f, 0f), q + Offset(1.2f + az.entre(-1f, 1f), largo), q + Offset(-1.2f, largo - 2f)), z.en(q, lerp(C.OCRE_ROJO, C.PIEL_OSC, az.entre(0.2f, 0.6f)))); drawLine(z.en(q, C.HUESO).copy(alpha = 0.5f), q + Offset(-0.4f, 2f), q + Offset(-0.2f, largo - 3f), 0.4f) }
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val z = EscenaCueva.luzDeDentro(m); val l = z.fuera; val f = EscenaCueva.lumbre(m); val manda = z.mandaElFuego
        // Fuera: la nieve o la lluvia que cae, los destellos del río y la aurora.
        clipPath(FORMA_BOCA) {
            with(ValleCueva) { aurora(m, l, reloj) }
            if (EscenaCueva.hielo(m) < 0.85f && l.noche < 0.5f) destellos(l, Rect(160f, 162f, 345f, 179f), reloj, m.semilla("rio"), 12)
            tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
        }
        if (EscenaCueva.luto(m)) { llama(LUTO + Offset(0f, -1f), 5f, reloj, 7); resplandor(LUTO + Offset(0f, -3f), 14f, 0.4f * (0.4f + 0.6f * l.noche), C.LUMBRE) }
        // El fuego: el humo que sube al techo y se va por la boca, las llamas, las chispas y el resplandor, que late.
        humo(l, HOGAR + Offset(0f, -18f - 14f * f), 150f, 9f + 5f * f, reloj, 0.25f + 0.5f * m.viento, 0.3f, 0f, 5, 9, (0.3f + 0.3f * f).coerceAtMost(0.7f))
        if (f < 0.5f) brasas(l, HOGAR + Offset(0f, 1f), 36f, reloj)
        val late = 0.5f + 0.5f * sin(reloj * 7.3f) * sin(reloj * 3.1f + 1f)
        if (manda > 0.05f) {
            resplandor(HOGAR + Offset(0f, -6f), 84f + 10f * late, (0.2f + 0.12f * late) * manda, C.LUMBRE)
            // Las sombras que tiemblan: en la pared del friso, por debajo de la repisa, la del murete baila con la llama.
            val tiembla = sin(reloj * 6.1f) * 2.4f + sin(reloj * 13.7f) * 0.9f
            clipRect(0f, 150f, 150f, LABIO + 6f) { drawPath(poligono(Offset(60f + tiembla, 204f), Offset(64f + tiembla * 1.6f, 172f - tiembla), Offset(110f + tiembla, 160f - tiembla * 0.6f), Offset(150f, 168f), Offset(150f, 204f)), lerp(C.HOLLIN, C.SOMBRA, 0.4f).copy(alpha = (0.1f + 0.05f * late) * manda)) }
        }
        val llamas = when { f >= 1.2f -> listOf(-19f to 38f, -8f to 56f, 5f to 64f, 17f to 42f, 0f to 34f, -13f to 24f, 12f to 26f); f >= 0.75f -> listOf(-14f to 26f, -3f to 40f, 10f to 30f, 3f to 20f, 17f to 16f); f >= 0.45f -> listOf(-9f to 14f, 3f to 21f, 12f to 12f); else -> listOf(1f to 9f, -8f to 5f) }
        for ((dx, alto) in llamas) llama(HOGAR + Offset(dx, 0f), alto * (0.6f + 0.4f * f.coerceAtMost(1f)), reloj, dx.toInt() + 20)
        if (f >= 0.75f) {
            // Las chispas, que suben y se apagan.
            val chispas = (0 until 9).map { j -> val t = ((reloj * (0.5f + 0.07f * j) + j * 0.37f) % 1f + 1f) % 1f; HOGAR + Offset(sin(j * 2.1f + t * 5f) * (6f + 10f * t), -14f - 56f * t) }
            drawPoints(chispas, PointMode.Points, lerp(C.LUMBRE, Color.White, 0.3f).copy(alpha = 0.8f), 1.1f, StrokeCap.Round)
        }
        // Las lámparas de grasa.
        if (EscenaCueva.lampara(m)) for ((j, p) in lamparas(m).withIndex()) { llama(p + Offset(0f, -2f), 5.4f, reloj, j + 3); resplandor(p + Offset(0f, -5f), 13f, 0.4f * (0.4f + 0.6f * l.noche), C.LUMBRE) }
    }

    // ---------------------------------------------------------------- El gato

    /** Lo plantado en el tramo por donde camina el gato: el hogar (también en brasas) y el montón de leña. */
    fun estorbos(m: Momento): List<Float> = listOf(HOGAR.x, LENA)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 18f, PISO_GATO, hasta = 350f))
        add(Ancla(TipoAncla.MIRADOR, 182f, 182.4f))                              // encima del murete, mirando el valle
        add(Ancla(TipoAncla.OBJETO, LOSA.x + 44f, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 60f, PISO_GATO))                             // junto al lecho, al fondo
        if (EscenaCueva.lumbre(m) >= 0.45f) add(Ancla(TipoAncla.FUEGO, HOGAR.x - 46f, PISO_GATO, hasta = HOGAR.x))
    }
}
