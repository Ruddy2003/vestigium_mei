package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que se ve desde la azotea de Pay, de lejos a cerca: los cerros del este y el valle del Nilo; la montaña de Tebas
 * con su cima, el paso y el barranco de la tumba del rey; la ladera de las tumbas de los obreros; y los tejados de la
 * aldea.
 *
 * **[Aproximación]**, sin fuente comprobada en la sesión salvo lo que se dice en el informe: que desde la aldea se vea
 * el valle (lo dice el cuento: «desde el tejado ve el valle verde y el río»; la aldea real está en una hondonada entre
 * cerros); la forma de los templos de la orilla (un pilono de dos torres con sus mástiles, salas detrás y dos colosos
 * sentados delante de otro); las pirámides pequeñas de adobe sobre las capillas de las tumbas (las nombra el cuento) y
 * las casas de azotea plana pegadas a los dos lados de una sola calle, dentro de un muro.
 */
internal object LejosEgipto {
    private val C = ColorEgipto

    /** El horizonte: al 39 % del alto. Así el valle tiene sitio debajo. */
    const val H = 110f

    /** Donde arde la lumbre de las chozas del paso, y las dos bocas de tumba del barranco. */
    private val PASO_0 = Offset(41f, 77f)
    private val SETI = Offset(178f, 130f)
    private val SIPTAH = Offset(226f, 142f)

    /** La montaña y el barranco se dibujan estrechados: así el valle tiene sitio a la derecha. */
    private const val EX = 0.86f
    val PASO = Offset(PASO_0.x * EX, PASO_0.y)
    val BOCA_SETI = Offset(SETI.x * EX, SETI.y)
    val BOCA_SIPTAH = Offset(SIPTAH.x * EX, SIPTAH.y)

    /** El borde de la montaña contra el cielo, puesto a mano: el paso a la izquierda y la cima en forma de pirámide. */
    private val CRESTA = listOf(
        Offset(-6f, 96f), Offset(6f, 90f), Offset(16f, 88f), Offset(24f, 83f), Offset(32f, 80.4f), Offset(40f, 79.6f), Offset(47f, 80f), Offset(52f, 75f), Offset(60f, 72f), Offset(66f, 66f),
        Offset(76f, 64f), Offset(84f, 58f), Offset(90f, 57f), Offset(97f, 50f), Offset(104f, 48.6f), Offset(110f, 43f), Offset(116f, 39f), Offset(121f, 37.6f), Offset(126f, 40f), Offset(130f, 46f),
        Offset(137f, 49f), Offset(142f, 56f), Offset(150f, 58f), Offset(156f, 64f), Offset(166f, 67f), Offset(172f, 74f), Offset(182f, 77f), Offset(190f, 85f), Offset(200f, 89f), Offset(208f, 97f),
        Offset(220f, 101f), Offset(230f, 108f), Offset(242f, 113f), Offset(254f, 120f), Offset(268f, 124f), Offset(288f, 130f),
    )

    /** Un trazo quebrado por [puntos]. */
    private fun quebrada(puntos: List<Offset>): Path = Path().apply { moveTo(puntos[0].x, puntos[0].y); for (q in puntos.drop(1)) lineTo(q.x, q.y) }

    private fun crestaEn(x: Float): Float {
        for (i in 0 until CRESTA.size - 1) { val a = CRESTA[i]; val b = CRESTA[i + 1]; if (x >= a.x && x <= b.x) return a.y + (b.y - a.y) * (x - a.x) / (b.x - a.x) }
        return if (x < CRESTA.first().x) CRESTA.first().y else CRESTA.last().y
    }

    /** Una mancha de borde irregular alrededor de [c]. */
    fun masa(az: Azar, c: Offset, rx: Float, ry: Float, puntas: Int = 9, quebrado: Float = 0.3f): Path = Path().apply {
        for (i in 0 until puntas) { val a = i * 6.2832f / puntas; val k = 1f + az.entre(-quebrado, quebrado); val p = c + Offset(cos(a) * rx * k, sin(a) * ry * k); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
        close()
    }

    // ---------------------------------------------------------------- El valle del Nilo

    /**
     * El valle, por la boca de la hondonada: los cerros del este, Tebas en la otra orilla, el río, los campos con sus
     * palmares y sus pueblos, y al borde del desierto los templos de los reyes. Con la crecida, el agua cubre los
     * campos hasta casi los pies de los templos y los pueblos quedan como islas.
     */
    fun DrawScope.valle(m: Momento, s: SolEgipto) {
        val l = s.l; val az = Azar(Semilla.de("egipto", "valle")); val agua = EscenaEgipto.rio(m); val dia = s.r.dia
        monte(l, perfil(Azar(Semilla.de("egipto", "este")), 110f, 394f, H + 1f, 17f, 0.3f, 28), H + 2f, lerp(C.ROCA, C.CALIZA, 0.3f), 0.92f)
        monte(l, perfil(Azar(Semilla.de("egipto", "este2")), 180f, 394f, H + 1f, 9f, 0.22f, 22), H + 2f, C.ROCA, 0.8f)
        // La otra orilla: una franja de palmares con los templos de Tebas, sus pilonos y sus dos obeliscos.
        drawRect(l.tono(lerp(C.PALMA, C.VERDE, 0.5f), 0.82f), Offset(110f, H - 0.6f), Size(ANCHO - 110f, 4.6f))
        for (j in 0..44) { val x = 196f + j * 4.2f + az.entre(-2f, 2f); drawOval(l.tono(C.PALMA, 0.74f), Offset(x, H - 1.9f - az.real()), Size(az.entre(3f, 6f), 2.5f)) }
        for ((x, w) in TEBAS) {
            drawRect(s.frente(C.ARENA, 0.7f), Offset(x, H - 2.6f), Size(w, 3.4f)); for (d in listOf(0f, w * 0.44f)) drawPath(poligono(Offset(x + d, H + 0.8f), Offset(x + d + 0.8f, H - 5.4f), Offset(x + d + w * 0.3f - 0.8f, H - 5.4f), Offset(x + d + w * 0.3f, H + 0.8f)), s.luz(C.ARENA, 0.66f))
            if (w > 15f) for (d in listOf(w * 0.74f, w * 0.86f)) { drawLine(s.luz(C.ROCA, 0.6f), Offset(x + d, H + 0.6f), Offset(x + d, H - 8.6f), 0.7f); drawLine(s.luz(C.OCRE, 0.4f), Offset(x + d, H - 8.6f), Offset(x + d, H - 9.8f), 0.7f) }
        }
        // La arena del borde del desierto, por debajo de los campos.
        drawRect(Brush.verticalGradient(0f to l.tono(lerp(C.ARENA, C.CALIZA, 0.2f), 0.36f), 1f to l.tono(C.ARENA, 0.2f), startY = 146f, endY = 200f), Offset(150f, 146f), Size(ANCHO - 150f, 54f))
        // El campo: del río al borde del desierto, que baja hacia la derecha: el valle es una cuña que se abre. Va en
        // franjas que se ensanchan hacia quien mira, partidas en parcelas. El color lo da la estación, sin mezclas que
        // lo apaguen: rastrojo pardo, limo negro, verde de siembra u oro de espiga.
        val seco = lerp(lerp(C.ARENA, C.BARRO, 0.36f), C.ESTERA, 0.2f); val oro = lerp(C.OCRE, C.TRIGO, 0.3f)
        val campo = lerp(lerp(lerp(seco, C.LIMO, EscenaEgipto.limo(m)), C.VERDE, EscenaEgipto.verdor(m)), oro, EscenaEgipto.maduro(m))
        val hondo = lerp(campo, C.BARRO, 0.3f); val claro = lerp(campo, C.CALIZA, 0.26f)
        val otro = lerp(lerp(campo, C.PALMA, 0.5f * EscenaEgipto.verdor(m)), C.OCRE, 0.4f * EscenaEgipto.maduro(m))
        val abajo = pieDelDesierto(ANCHO)
        fun franja(j: Int) = ORILLA_CERCA + (abajo - ORILLA_CERCA) * (j / 9f) * (0.4f + 0.6f * j / 9f)
        val forma = poligono(Offset(110f, ORILLA_CERCA), Offset(ANCHO, ORILLA_CERCA), Offset(ANCHO, abajo), Offset(228f, pieDelDesierto(228f)), Offset(110f, pieDelDesierto(228f)))
        clipPath(forma) {
            for (j in 0..8) { val y0 = franja(j); val y1 = franja(j + 1); val lejos = 0.5f - j * 0.05f
                drawRect(l.tono(lerp(campo, if (j % 2 == 0) otro else hondo, az.entre(0.05f, 0.2f)), lejos), Offset(110f, y0), Size(ANCHO - 110f, y1 - y0 + 0.4f))
                var x = 190f + az.entre(0f, 20f); while (x < ANCHO) { val w = az.entre(12f, 34f) * (0.6f + j * 0.13f); val que = az.entero(4)
                    drawRect(l.tono(lerp(campo, when (que) { 0 -> otro; 1 -> hondo; 2 -> claro; else -> campo }, az.entre(0.2f, 0.55f)), lejos), Offset(x, y0), Size(w, y1 - y0))
                    // Los surcos de cada parcela.
                    if (j > 2) for (i in 1 until (w / 2.4f).toInt()) drawLine(l.tono(lerp(campo, C.BARRO, 0.4f), lejos).copy(alpha = 0.22f), Offset(x + i * 2.4f, y0 + 0.4f), Offset(x + i * 2.4f + (x - 290f) * 0.012f * (y1 - y0), y1 - 0.2f), 0.3f)
                    x += w + az.entre(0f, 10f) }
                drawLine(l.tono(lerp(C.NILO, C.LIMO, 0.35f), 0.5f).copy(alpha = 0.6f), Offset(110f, y1), Offset(ANCHO, y1), 0.4f)
            }
            // Las acequias, que bajan del río torciendo entre las parcelas, con su hilera de matas.
            for (cauce in listOf(listOf(Offset(252f, ORILLA_CERCA), Offset(246f, H + 18f), Offset(262f, H + 30f), Offset(254f, H + 46f)), listOf(Offset(336f, ORILLA_CERCA), Offset(346f, H + 16f), Offset(332f, H + 30f), Offset(350f, H + 44f), Offset(344f, H + 62f)))) {
                drawPath(curva(cauce), lerp(l.tono(C.NILO, 0.5f), l.bajo, 0.3f), style = Stroke(1f)); drawPath(curva(cauce.map { it + Offset(1.3f, 0f) }), l.tono(C.PALMA, 0.5f).copy(alpha = 0.6f), style = Stroke(0.6f)) }
        }
        // El río: una hoja clara entre las dos orillas. Con el sol bajo del este, brilla como un cuchillo.
        val nilo = lerp(l.tono(C.NILO, 0.5f), l.bajo, 0.28f)
        drawRect(Brush.verticalGradient(0f to lerp(nilo, l.bajo, 0.4f), 1f to nilo, startY = ORILLA_LEJOS, endY = ORILLA_CERCA), Offset(110f, ORILLA_LEJOS), Size(ANCHO - 110f, ORILLA_CERCA - ORILLA_LEJOS))
        for (j in 0..16) { val x = az.entre(190f, ANCHO); val y = ORILLA_LEJOS + az.entre(0.8f, 4f); drawLine(lerp(l.bajo, Color.White, 0.3f).copy(alpha = 0.4f), Offset(x, y), Offset(x + az.entre(6f, 20f), y), 0.4f) }
        // Los bancos de arena y las velas de dos barcas.
        for ((x, w) in listOf(236f to 26f, 310f to 18f)) drawOval(l.tono(lerp(C.ARENA, C.CALIZA, 0.3f), 0.6f), Offset(x, ORILLA_LEJOS + 1.2f), Size(w, 1.3f))
        for ((x, k) in listOf(268f to 1f, 352f to 0.8f)) { val y = ORILLA_LEJOS + 3.4f; drawLine(s.medio(C.MADERA_OSC, 0.4f), Offset(x - 3f * k, y), Offset(x + 3f * k, y), 0.8f * k, StrokeCap.Round); drawPath(poligono(Offset(x - 2f * k, y - 0.8f), Offset(x + 0.4f * k, y - 5.4f * k), Offset(x + 2.6f * k, y - 1f)), s.luz(C.LINO, 0.36f)) }
        val filo = (dia * (1f - l.solAltura).coerceIn(0f, 1f) * (1f - l.nublado)).coerceIn(0f, 1f)
        if (filo > 0.05f) { val x = l.solX * ANCHO; drawLine(Color.White.copy(alpha = 0.8f * filo), Offset(x - 50f, ORILLA_LEJOS + 1.8f), Offset(x + 46f, ORILLA_LEJOS + 1.8f), 1.1f, StrokeCap.Round); mancha(Offset(x, ORILLA_LEJOS + 1.8f), 60f, 2.6f, l.solColor, 0.5f * filo) }
        if (l.noche > 0.4f && l.nublado < 0.6f) { val x = l.lunaX * ANCHO; drawLine(C.LUNA.copy(alpha = 0.6f * l.noche), Offset(x - 24f, ORILLA_LEJOS + 2f), Offset(x + 24f, ORILLA_LEJOS + 2f), 0.9f, StrokeCap.Round) }
        // La crecida: el agua sale del cauce y cubre las franjas, de la más lejana a la más cercana, hasta casi el pie de los templos.
        if (agua > 0.02f) {
            val fondo = hastaDondeLlegaElAgua(agua, ANCHO); val lago = Rect(110f, ORILLA_CERCA - 0.4f, ANCHO, fondo)
            val contorno = poligono(Offset(110f, lago.top), Offset(ANCHO, lago.top), Offset(ANCHO, fondo), Offset(228f, hastaDondeLlegaElAgua(agua, 228f)), Offset(110f, hastaDondeLlegaElAgua(agua, 228f)))
            agua(l, lago, Azar(Semilla.de("egipto", "crecida")), lerp(C.NILO, C.LOZA, 0.2f), m.viento, contorno, lerp(C.NILO, C.LIMO, 0.25f), lerp(C.NILO, C.CALIZA, 0.4f))
            clipPath(contorno) {
                // El cielo en el agua quieta y, de noche, la luna.
                mancha(Offset(310f, (lago.top + lago.bottom) / 2f), 110f, lago.height * 0.4f, l.bajo, 0.24f * dia)
                if (l.noche > 0.4f && l.nublado < 0.6f) mancha(Offset(l.lunaX * ANCHO, (lago.top + lago.bottom) / 2f), 30f, lago.height * 0.5f, C.LUNA, 0.3f * l.noche)
                // Los diques, que cruzan el agua como rayas oscuras, con sus matas.
                for (j in 0..5) { val y = lago.top + 2f + (abajo - lago.top) * (0.1f + j * 0.16f); val x0 = az.entre(200f, 250f); val x1 = az.entre(320f, ANCHO)
                    drawLine(l.tono(C.LIMO, 0.5f).copy(alpha = 0.6f), Offset(x0, y), Offset(x1, y + az.entre(-0.6f, 0.6f)), 0.5f + j * 0.08f); for (i in 0..5) drawOval(l.tono(C.PALMA, 0.5f), Offset(x0 + (x1 - x0) * az.real(), y - 1.2f), Size(2.4f, 1.4f)) }
            }
            // El borde, que avanza en lenguas sobre la tierra.
            val borde = Azar(Semilla.de("egipto", "borde")); var x = 200f; while (x < ANCHO) { val w = borde.entre(14f, 40f); drawOval(lerp(nilo, l.tono(C.LIMO, 0.5f), 0.3f), Offset(x, hastaDondeLlegaElAgua(agua, x + w / 2f) - 1.3f), Size(w, borde.entre(1.4f, 3f))); x += w * borde.entre(0.6f, 1.3f) }
        }
        // Los palmares y los pueblos, en sus montículos: con la crecida quedan como islas, cada una con su reflejo.
        val ap = Azar(Semilla.de("egipto", "palmares"))
        for ((n, c) in pueblos().withIndex()) {
            val t = PUEBLOS[n].second; val k = 0.62f + t * 0.8f; val lejos = 0.52f - t * 0.34f; val mojado = c.y < hastaDondeLlegaElAgua(agua, c.x)
            if (mojado) { reflejoRoto(c.x - 14f * k, c.x + 14f * k, c.y + 2.2f * k, 6f * k, l.tono(C.PALMA, lejos), 0.5f, ap, 0.9f); reflejoRoto(c.x - 9f * k, c.x + 9f * k, c.y + 2.2f * k, 3.4f * k, l.tono(C.ENLUCIDO, lejos), 0.4f, ap, 0.9f) }
            drawOval(l.tono(if (mojado) C.LIMO else lerp(campo, C.BARRO, 0.3f), lejos), Offset(c.x - 15f * k, c.y - 1.2f * k), Size(30f * k, 4.2f * k))
            if (mojado) drawOval(l.tono(lerp(C.BARRO, C.ARENA, 0.4f), lejos), Offset(c.x - 13f * k, c.y - 1.6f * k), Size(26f * k, 2.6f * k))
            if (n % 3 == 1) sicomoro(s, c + Offset(-9f * k, 0.4f * k), 6.4f * k, lejos, ap)
            if (n % 2 == 0) for (j in 0..3) { val q = c + Offset((-8f + j * 5.4f) * k, -0.4f * k); val h = ap.entre(2.6f, 4.4f) * k
                drawRect(s.frente(if (j == 1) C.ENLUCIDO else C.ADOBE, lejos), Offset(q.x, q.y - h), Size(4.4f * k, h)); drawRect(s.lado(C.ADOBE, lejos), Offset(q.x + (if (s.solALaDerecha) 3.2f * k else 0f), q.y - h), Size(1.2f * k, h)); drawRect(s.arriba(C.ENLUCIDO, lejos), Offset(q.x, q.y - h - 0.5f * k), Size(4.4f * k, 0.7f * k))
                drawRect(s.sombra(C.HOLLIN, lejos).copy(alpha = 0.6f), Offset(q.x + 1.6f * k, q.y - h * 0.5f), Size(1f * k, h * 0.5f)) }
            repeat(4 + ap.entero(4)) { val pie = c + Offset(ap.entre(-12f, 12f) * k, ap.entre(-0.6f, 1.2f) * k); palmita(s, pie, ap.entre(5f, 8.4f) * k, lejos, ap) }
        }
        // Los dos colosos sentados, solos en el campo, mirando al río; y los templos de los reyes al borde del desierto.
        for (x in COLOSOS) { if (COLOSO_Y < hastaDondeLlegaElAgua(agua, x)) reflejoRoto(x - 4f, x + 5f, COLOSO_Y + 1.2f, 6f, l.tono(C.ROCA, 0.3f), 0.45f, az, 1f); coloso(s, Offset(x, COLOSO_Y), 1.3f, 0.3f) }
        for (j in 0..5) { val x = 232f + j * 27f + az.entre(-6f, 6f); acacia(s, Offset(x, pieDelDesierto(x) + az.entre(1f, 3.4f)), az.entre(3.6f, 5f), 0.3f, az) }
        templo(m, s, Offset(238f, pieDelDesierto(238f) + 1.6f), 0.5f, 0.32f); templo(m, s, TEMPLO, 1.1f, 0.24f)
        // Las noches de Opet, las luces de los templos de la otra orilla, y su reflejo en el río.
        if (EscenaEgipto.lucesDeTebas(m) && l.noche > 0.3f) { val an = Azar(Semilla.de("egipto", "opet"))
            for ((x, w) in TEBAS) { resplandor(Offset(x + w / 2f, H - 2f), 16f, 0.5f * l.noche, C.LUMBRE); repeat(7) { drawCircle(C.LUMBRE.copy(alpha = 0.9f * l.noche), 0.6f, Offset(x + an.entre(-4f, w + 4f), H - an.entre(0f, 4f))) }
                reflejoRoto(x - 2f, x + w + 2f, ORILLA_LEJOS + 0.6f, 3.8f, C.LUMBRE, 0.5f * l.noche, an, 0.8f) } }
        // La barca de Amón, dorada, cruza el río con su cortejo de barcas.
        if (EscenaEgipto.barcaDeAmon(m)) { val y = ORILLA_LEJOS + 2.8f
            for ((x, k) in listOf(296f to 1f, 280f to 0.5f, 312f to 0.5f, 324f to 0.4f)) { drawPath(Path().apply { moveTo(x - 6f * k, y - 1f * k); cubicTo(x - 3f * k, y + 1.4f * k, x + 3f * k, y + 1.4f * k, x + 6f * k, y - 1.6f * k) }, s.luz(if (k == 1f) C.OCRE else C.MADERA, 0.2f), style = Stroke(1.3f * k, cap = StrokeCap.Round))
                if (k == 1f) { drawRect(s.luz(C.OCRE, 0.15f), Offset(x - 1.8f, y - 4.4f), Size(3.6f, 3.4f)); drawLine(s.luz(C.LINO, 0.2f), Offset(x - 1.8f, y - 4.4f), Offset(x + 1.8f, y - 4.4f), 0.6f); if (l.noche > 0.3f) resplandor(Offset(x, y - 2f), 9f, 0.6f * l.noche, C.LUMBRE) } } }
    }

    private const val ORILLA_LEJOS = H + 3.4f
    private const val ORILLA_CERCA = H + 8.4f
    private val TEBAS = listOf(250f to 20f, 326f to 13f)
    private val COLOSOS = listOf(347f, 359f)
    private const val COLOSO_Y = 159f

    /**
     * Dónde acaban los campos y empieza el desierto, a la altura [x] del cuadro: el borde baja hacia la derecha, y por
     * eso el valle es una cuña que se abre.
     */
    fun pieDelDesierto(x: Float): Float = 150f + (x - 228f).coerceAtLeast(0f) * 0.145f

    private val TEMPLO = Offset(284f, pieDelDesierto(284f) + 1.4f)

    /** Los pueblos del valle: dónde está cada uno (su x) y cuán cerca de quien mira, de 0 (junto al río) a 1 (al borde del desierto). */
    private val PUEBLOS = listOf(236f to 0.2f, 272f to 0.13f, 308f to 0.2f, 346f to 0.12f, 374f to 0.22f, 254f to 0.46f, 296f to 0.4f, 338f to 0.44f, 372f to 0.52f, 232f to 0.76f, 270f to 0.62f, 362f to 0.8f, 318f to 0.6f)
    private fun pueblos(): List<Offset> = PUEBLOS.map { (x, t) -> Offset(x, ORILLA_CERCA + t * (pieDelDesierto(x) - ORILLA_CERCA)) }

    /** Hasta qué altura del cuadro llega el agua de la crecida a la altura [x], con el río en [agua] (de 0 a 1). */
    fun hastaDondeLlegaElAgua(agua: Float, x: Float = 300f): Float = ORILLA_CERCA + (pieDelDesierto(x) - 2f - ORILLA_CERCA) * agua.coerceIn(0f, 1f)

    /** Un sicomoro lejano: la copa redonda y ancha, más oscura que las palmeras, sobre un tronco corto. */
    private fun DrawScope.sicomoro(s: SolEgipto, pie: Offset, alto: Float, lejos: Float, az: Azar) {
        drawLine(s.sombra(C.MADERA_OSC, lejos), pie, pie + Offset(0f, -alto * 0.5f), alto * 0.14f)
        val copa = masa(az, pie + Offset(0f, -alto * 0.7f), alto * 0.62f, alto * 0.4f, 9, 0.22f); drawPath(copa, s.medio(lerp(C.PALMA, C.VERDE, 0.2f), lejos))
        clipPath(copa) { drawOval(s.luz(lerp(C.PALMA, C.VERDE, 0.5f), lejos), Offset(pie.x + (if (s.solALaDerecha) 0f else -alto * 0.7f), pie.y - alto * 1.2f), Size(alto * 0.7f, alto * 0.6f)); drawOval(s.sombra(C.PALMA, lejos), Offset(pie.x - alto * 0.6f, pie.y - alto * 0.62f), Size(alto * 1.2f, alto * 0.4f)) }
    }

    /** Una acacia del borde del desierto: el tronco torcido y la copa chata, en capas. */
    private fun DrawScope.acacia(s: SolEgipto, pie: Offset, alto: Float, lejos: Float, az: Azar) {
        val cruz = pie + Offset(az.entre(-0.2f, 0.2f) * alto, -alto * 0.6f)
        drawLine(s.sombra(C.MADERA_OSC, lejos), pie, cruz, 0.6f); for (dx in listOf(-0.5f, 0.5f)) drawLine(s.sombra(C.MADERA_OSC, lejos), cruz, cruz + Offset(dx * alto, -alto * 0.3f), 0.4f)
        drawOval(s.sombra(lerp(C.PALMA, C.ESTERA, 0.3f), lejos), Offset(cruz.x - alto * 0.8f, cruz.y - alto * 0.4f), Size(alto * 1.6f, alto * 0.36f)); drawOval(s.luz(lerp(C.VERDE, C.ESTERA, 0.3f), lejos), Offset(cruz.x - alto * 0.7f, cruz.y - alto * 0.5f), Size(alto * 1.3f, alto * 0.24f))
    }

    /** Una palmera lejana: el tronco, algo torcido, y un penacho de pencas que caen. */
    private fun DrawScope.palmita(s: SolEgipto, pie: Offset, alto: Float, lejos: Float, az: Azar) {
        val copa = pie + Offset(az.entre(-0.14f, 0.14f) * alto, -alto)
        drawLine(s.medio(lerp(C.MADERA, C.BARRO, 0.4f), lejos), pie, copa, (0.3f + alto * 0.05f).coerceAtMost(0.9f), StrokeCap.Round)
        val r = alto * 0.44f
        for (j in 0..6) { val a = -3.3f + j * 0.58f; val punta = copa + Offset(cos(a) * r, sin(a) * r * 0.7f + r * 0.5f * (if (j == 0 || j == 6) 1f else 0.25f))
            drawPath(Path().apply { moveTo(copa.x, copa.y); cubicTo(copa.x + cos(a) * r * 0.5f, copa.y + sin(a) * r * 0.9f, punta.x, punta.y - r * 0.5f, punta.x, punta.y) }, if ((cos(a) > 0f) == s.solALaDerecha) s.luz(C.PALMA, lejos) else s.sombra(C.PALMA, lejos), style = Stroke((0.4f + alto * 0.05f).coerceAtMost(1f), cap = StrokeCap.Round)) }
    }

    /** Un coloso sentado, de perfil, mirando al río (a la derecha): el trono, las piernas, el torso y la cabeza con su tocado. */
    private fun DrawScope.coloso(s: SolEgipto, pie: Offset, k: Float, lejos: Float) {
        fun p(x: Float, y: Float) = pie + Offset(x * k, y * k)
        val piedra = lerp(C.ROCA, C.ARENA, 0.4f)
        drawPath(poligono(listOf(p(-2.6f, 0f), p(-2.6f, -6.4f), p(-1f, -6.4f), p(-1f, -9.4f), p(0.8f, -9.4f), p(0.8f, -6.2f), p(1.2f, -4f), p(3f, -4f), p(3f, 0f))), s.frente(piedra, lejos))
        drawRect(s.lado(piedra, lejos, alReves = true), p(-2.6f, -6.4f), Size(1.1f * k, 6.4f * k)); drawRect(s.arriba(piedra, lejos), p(1.2f, -4.2f), Size(1.8f * k, 0.6f * k))
        drawRect(s.sombra(C.ROCA_OSC, lejos), p(-3.2f, 0f), Size(7f * k, 1.2f * k))
    }

    /**
     * Un templo de los reyes muertos, visto de costado desde el desierto: el muro de adobe del recinto, las salas que
     * suben en escalones y, del lado del río, el pilono de dos torres con sus mástiles y sus gallardetes.
     */
    private fun DrawScope.templo(m: Momento, s: SolEgipto, pie: Offset, k: Float, lejos: Float) {
        val l = s.l; fun p(x: Float, y: Float) = pie + Offset(x * k, y * k)
        val piedra = lerp(C.ARENA, C.CALIZA, 0.35f); val az = Azar(Semilla.de("egipto", "templo", pie.x.toInt()))
        // El recinto, de adobe, con sus almacenes abovedados detrás.
        drawRect(s.frente(C.ADOBE, lejos), p(-34f, -4.6f), Size(72f * k, 4.6f * k)); drawRect(s.arriba(C.ENLUCIDO, lejos), p(-34f, -5.2f), Size(72f * k, 0.8f * k))
        for (j in 0..7) drawOval(s.frente(lerp(C.ADOBE, C.BARRO, 0.2f), lejos), p(-32f + j * 4.2f, -7.6f), Size(4.4f * k, 4f * k))
        // Las salas: tres cuerpos, cada uno más alto hacia el pilono, con su cornisa y sus columnas adivinadas.
        for ((x, w, h) in listOf(Triple(-6f, 12f, 8f), Triple(6f, 11f, 10f), Triple(17f, 7f, 11.4f))) {
            drawRect(s.frente(piedra, lejos), p(x, -h), Size(w * k, h * k)); drawRect(s.arriba(C.CALIZA, lejos), p(x - 0.4f, -h - 0.8f), Size((w + 0.8f) * k, 1f * k))
            for (j in 1 until (w / 2.2f).toInt()) drawLine(s.sombra(piedra, lejos).copy(alpha = 0.7f), p(x + j * 2.2f, -h + 1.6f), p(x + j * 2.2f, -1f), 0.5f * k)
            drawRect(s.sombra(piedra, lejos).copy(alpha = 0.5f), p(x, -h), Size(w * k, 1.2f * k))
        }
        // El pilono: dos torres en talud, la puerta entre las dos, la moldura de arriba y cuatro mástiles.
        for (x in listOf(24f, 33.4f)) { val torre = poligono(p(x, 0f), p(x + 0.9f, -15f), p(x + 7.5f, -15f), p(x + 8.4f, 0f))
            drawPath(torre, s.frente(piedra, lejos)); clipPath(torre) { drawRect(s.lado(piedra, lejos), p(if (s.solALaDerecha) x + 6f else x, -15f), Size(2.4f * k, 15f * k)); repeat(4) { drawLine(s.sombra(C.ROCA, lejos).copy(alpha = 0.3f), p(x + az.entre(1f, 7f), -az.entre(3f, 13f)), p(x + az.entre(1f, 7f), -az.entre(1f, 12f)), 0.4f * k) } }
            drawRect(s.arriba(C.CALIZA, lejos), p(x + 0.5f, -15.9f), Size(7.4f * k, 1.1f * k)) }
        drawRect(s.frente(piedra, lejos), p(32.4f, -9f), Size(1.2f * k, 9f * k)); drawRect(s.sombra(C.HOLLIN, lejos).copy(alpha = 0.8f), p(32.5f, -6.4f), Size(1f * k, 6.4f * k))
        for ((j, x) in listOf(25.6f, 30.4f, 35f, 39.8f).withIndex()) { drawLine(s.medio(C.MADERA, lejos), p(x, -1f), p(x, -22f), 0.5f * k); val g = p(x, -22f); val ondea = if (m.viento > 0.5f) 1.2f else 0f
            if (!EscenaEgipto.luto(m)) drawPath(poligono(g, g + Offset(-4.4f * k, (0.8f + ondea) * k), g + Offset(0f, 2f * k)), s.medio(if (j % 2 == 0) C.TERRACOTA else C.LINO, lejos * 0.6f)) }
        // El coloso de delante, y de noche las antorchas de la puerta.
        coloso(s, p(46f, 0f), 0.9f * k, lejos)
        if (l.noche > 0.35f) { resplandor(p(33f, -4f), 12f * k, 0.5f * l.noche, C.LUMBRE); for (x in listOf(31.4f, 34.6f)) drawCircle(C.LUMBRE.copy(alpha = l.noche), 0.7f * k, p(x, -5f)) }
    }

    // ---------------------------------------------------------------- La montaña

    /** La montaña de Tebas: la cima en forma de pirámide, las paredes de caliza acanaladas sobre sus pedreras, las barrancas y el paso con sus chozas. */
    fun DrawScope.montana(m: Momento, s: SolEgipto) { scale(EX, 1f, Offset.Zero) { montanaSinEstrechar(m, s) } }

    private fun DrawScope.montanaSinEstrechar(m: Momento, s: SolEgipto) {
        val l = s.l; val az = Azar(Semilla.de("egipto", "montana")); val lejos = 0.17f; val dia = s.r.dia
        val forma = poligono(CRESTA + listOf(Offset(288f, 140f), Offset(250f, 152f), Offset(200f, 158f), Offset(150f, 158f), Offset(-6f, 160f)))
        val roca = lerp(C.ROCA, C.ARENA, 0.12f); val clara = s.luz(lerp(C.ROCA, C.CALIZA, 0.5f), lejos); val oscura = s.sombra(lerp(C.ROCA, C.ROCA_OSC, 0.7f), lejos)
        drawPath(forma, Brush.verticalGradient(0f to s.medio(lerp(roca, C.ROCA_OSC, 0.14f), lejos + 0.08f), 0.55f to s.medio(roca, lejos), 1f to s.medio(lerp(C.ARENA, C.CALIZA, 0.35f), lejos - 0.12f), startY = 38f, endY = 158f))
        clipPath(forma) {
            // La masa entera: el lado que mira al sol, más claro; el otro, en sombra.
            drawRect(Brush.horizontalGradient(0f to (if (s.solALaDerecha) oscura else clara).copy(alpha = 0.34f * dia), 0.5f to Color.Transparent, 1f to (if (s.solALaDerecha) clara else oscura).copy(alpha = 0.34f * dia), startX = 0f, endX = 290f), Offset(-6f, 30f), Size(300f, 130f))
            // Las barrancas: cada una, un cauce que baja torciendo y se abre en abanico; una pared al sol y otra en sombra.
            for (x in listOf(10f, 36f, 62f, 88f, 108f, 134f, 150f, 176f, 204f, 236f)) {
                val y = crestaEn(x) + az.entre(8f, 16f); val largo = az.entre(46f, 78f); val tuerce = az.entre(-14f, 14f); val ancho = az.entre(5f, 10f); val fin = Offset(x + tuerce, y + largo)
                val cauce = Path().apply { moveTo(x, y); cubicTo(x + tuerce * 0.2f - ancho * 0.3f, y + largo * 0.3f, fin.x - ancho, y + largo * 0.7f, fin.x - ancho * 1.5f, fin.y); lineTo(fin.x + ancho * 1.5f, fin.y); cubicTo(fin.x + ancho, y + largo * 0.7f, x + tuerce * 0.2f + ancho * 0.3f, y + largo * 0.3f, x, y); close() }
                drawPath(cauce, Brush.horizontalGradient(0f to (if (s.solALaDerecha) clara else oscura).copy(alpha = 0.42f), 0.5f to oscura.copy(alpha = 0.18f), 1f to (if (s.solALaDerecha) oscura else clara).copy(alpha = 0.42f), startX = x - ancho * 1.5f + tuerce * 0.5f, endX = x + ancho * 1.5f + tuerce * 0.5f))
                drawPath(Path().apply { moveTo(x, y); cubicTo(x + tuerce * 0.2f, y + largo * 0.3f, fin.x, y + largo * 0.7f, fin.x, fin.y) }, oscura.copy(alpha = 0.5f), style = Stroke(0.5f))
            }
            // Las paredes: tres fajas de caliza que siguen la cresta, hechas de contrafuertes (una cara al sol y otra en sombra), cada una sobre su pedrera.
            for ((n, faja) in listOf(Triple(8f, 6f, 0.45f), Triple(27f, 14f, 0.85f), Triple(53f, 9f, 0.65f)).withIndex()) { val (baja, alto, cuanta) = faja
                // La faja entera, algo más oscura, para que los contrafuertes no queden sueltos.
                val arriba = CRESTA.map { Offset(it.x, it.y + baja + 3f * sin(it.x * 0.05f + baja)) }
                drawPath(Path().apply { addPath(quebrada(arriba)); for (q in arriba.reversed()) lineTo(q.x, q.y + alto * (0.5f + 0.4f * sin(q.x * 0.033f + baja * 0.7f))); close() }, oscura.copy(alpha = 0.2f * cuanta))
                // La repisa de arriba: una raya clara y seguida.
                drawPath(quebrada(arriba), clara.copy(alpha = 0.55f * cuanta), style = Stroke(0.7f))
                var x = -6f
                while (x < 290f) { val w = az.entre(3.2f, 8.6f); val y0 = crestaEn(x + w / 2f) + baja + 3f * sin(x * 0.05f + baja); val h = alto * (0.55f + 0.5f * sin(x * 0.033f + baja * 0.7f)) * az.entre(0.45f, 1.4f) * (if (az.real() < 0.16f) 0f else 1f)
                    if (h > 2.6f && (n == 1 || sin(x * 0.07f + baja) > -0.3f)) {
                        val filo = x + w * (if (s.solALaDerecha) 0.42f else 0.58f)
                        // La pedrera, al pie: un abanico claro que se pierde ladera abajo.
                        drawPath(poligono(Offset(x - 2f, y0 + h + az.entre(7f, 15f)), Offset(filo, y0 + h - 1f), Offset(x + w + 2f, y0 + h + az.entre(7f, 15f))), clara.copy(alpha = 0.2f * cuanta))
                        // Cada contrafuerte cuelga de la repisa y se deshace hacia abajo, en la pedrera.
                        for ((cara, color) in listOf(poligono(Offset(x, y0), Offset(filo, y0), Offset(filo, y0 + h + 1.2f), Offset(x, y0 + h)) to (if (s.solALaDerecha) oscura else clara), poligono(Offset(filo, y0), Offset(x + w, y0), Offset(x + w, y0 + h), Offset(filo, y0 + h + 1.2f)) to (if (s.solALaDerecha) clara else oscura)))
                            drawPath(cara, Brush.verticalGradient(0f to color.copy(alpha = 0.5f * cuanta), 0.55f to color.copy(alpha = 0.42f * cuanta), 1f to color.copy(alpha = 0f), startY = y0, endY = y0 + h + 1.2f))
                        drawLine(oscura.copy(alpha = 0.6f * cuanta), Offset(x, y0 + 0.6f), Offset(x + az.entre(-0.6f, 0.6f), y0 + h), 0.5f)
                        // La repisa de arriba y un estrato a media altura.
                        if (h > 7f) drawLine(oscura.copy(alpha = 0.34f), Offset(x, y0 + h * 0.5f), Offset(x + w, y0 + h * 0.5f + az.entre(-0.6f, 0.6f)), 0.4f)
                    }
                    x += w }
            }
            // Las piedras sueltas de las laderas.
            repeat(40) { val x = az.entre(0f, 280f); val y = crestaEn(x) + az.entre(24f, 90f); mancha(Offset(x, y), az.entre(6f, 16f), az.entre(1.4f, 3.4f), if (it % 2 == 0) clara else oscura, 0.2f) }
            drawPoints((0 until 140).map { val x = az.entre(0f, 280f); Offset(x, crestaEn(x) + az.entre(6f, 92f)) }, PointMode.Points, oscura.copy(alpha = 0.5f), 0.8f, StrokeCap.Round)
            // La cima, que se enciende la primera al amanecer y la última al anochecer.
            val arrebol = (1f - kotlin.math.abs(l.solAltura - 0.05f) / 0.3f).coerceIn(0f, 1f) * (1f - l.nublado)
            if (arrebol > 0.05f) drawRect(Brush.verticalGradient(0f to lerp(C.TERRACOTA, C.LUMBRE, 0.5f).copy(alpha = 0.5f * arrebol), 1f to Color.Transparent, startY = 36f, endY = 74f), Offset(60f, 36f), Size(130f, 38f))
            // De noche, la luna en las repisas.
            if (l.noche > 0.4f && l.nublado < 0.6f) drawPath(quebrada(CRESTA.map { it + Offset(0f, 1.2f) }), C.LUNA.copy(alpha = 0.3f * l.noche), style = Stroke(1.1f))
            // El agua que baja por las barrancas el día que llueve en el desierto.
            if (EscenaEgipto.torrente(m)) for (x in listOf(88f, 134f, 204f)) { var q = Offset(x, crestaEn(x) + 6f); val hilo = Path().apply { moveTo(q.x, q.y); repeat(7) { q += Offset(az.entre(-5f, 7f), az.entre(8f, 14f)); lineTo(q.x, q.y) } }
                drawPath(hilo, l.tono(lerp(C.NILO, C.CALIZA, 0.5f), 0.2f).copy(alpha = 0.8f), style = Stroke(1.4f, cap = StrokeCap.Round)); drawPath(hilo, Color.White.copy(alpha = 0.3f), style = Stroke(0.4f)) }
        }
        // El sendero del paso, que sube en revueltas desde la aldea.
        val sendero = listOf(Offset(160f, 158f), Offset(138f, 150f), Offset(124f, 146f), Offset(104f, 133f), Offset(84f, 122f), Offset(98f, 110f), Offset(72f, 102f), Offset(82f, 92f), Offset(58f, 86f), Offset(46f, 80f))
        drawPath(curva(sendero), s.luz(C.CALIZA, 0.2f).copy(alpha = 0.8f), style = Stroke(1.1f, cap = StrokeCap.Round))
        drawPath(curva(sendero.map { it + Offset(0f, 0.9f) }), s.sombra(C.ROCA_OSC, 0.2f).copy(alpha = 0.4f), style = Stroke(0.5f))
        // El paso: el puesto de vigilancia y la fila de chozas de piedra seca de la cuadrilla.
        drawRect(s.frente(lerp(C.ROCA, C.CALIZA, 0.3f), 0.2f), Offset(28f, 73.4f), Size(4.6f, 7f)); drawRect(s.lado(C.ROCA, 0.2f), Offset(if (s.solALaDerecha) 31.4f else 28f, 73.4f), Size(1.2f, 7f)); drawRect(s.arriba(C.CALIZA, 0.2f), Offset(27.6f, 72.6f), Size(5.4f, 1f)); drawRect(s.sombra(C.HOLLIN, 0.2f).copy(alpha = 0.7f), Offset(29.6f, 75f), Size(1.2f, 1.6f))
        for (j in 0..4) { val x = 36f + j * 5f + az.entre(-0.8f, 0.8f); val y = 79.8f - (j % 2) * 0.8f; val h = az.entre(2.4f, 3.8f); val w = az.entre(3f, 4.2f); drawRect(s.frente(lerp(C.ROCA, C.CALIZA, 0.4f), 0.2f), Offset(x, y - h), Size(w, h + 0.4f)); drawRect(s.lado(C.ROCA, 0.2f), Offset(if (s.solALaDerecha) x + w - 0.9f else x, y - h), Size(0.9f, h + 0.4f)); drawRect(s.sombra(C.HOLLIN, 0.2f).copy(alpha = 0.7f), Offset(x + 1.1f, y - h * 0.6f), Size(1f, h * 0.6f + 0.4f)); drawRect(s.arriba(C.CALIZA, 0.2f), Offset(x - 0.3f, y - h - 0.6f), Size(w + 0.6f, 0.8f)) }
        if (EscenaEgipto.lumbreDelPaso(m) && l.noche > 0.2f) { resplandor(PASO_0, 16f, 0.75f * l.noche, C.LUMBRE); mancha(PASO_0 + Offset(0f, 1f), 12f, 3f, C.LUMBRE, 0.4f * l.noche) }
    }

    // ---------------------------------------------------------------- El barranco de la tumba del rey

    private val SITIOS_SETI = listOf(Offset(157f, 146f), Offset(200f, 150f), Offset(149f, 152f), Offset(170f, 152.6f), Offset(188f, 154f))
    private val SITIOS_SIPTAH = listOf(Offset(246f, 151f), Offset(208f, 153f), Offset(256f, 155f), Offset(234f, 155.6f), Offset(252f, 146f))

    /**
     * La boca de una tumba que pisa en [pie]: la rampa de lascas que baja de ella, el rebaje en la roca bajo su visera,
     * el dintel tallado y el hueco, oscuro, de [ancho] por [alto] unidades; o tapiada.
     */
    private fun DrawScope.boca(s: SolEgipto, pie: Offset, ancho: Float, alto: Float, sellada: Boolean, alumbrada: Float) {
        val a = ancho / 2f; val roca = lerp(C.CALIZA, C.ROCA, 0.36f)
        // La rampa de lascas: un abanico que baja de la boca y se abre.
        val rampa = Path().apply { moveTo(pie.x - a - 1f, pie.y); lineTo(pie.x + a + 1f, pie.y); cubicTo(pie.x + a * 1.5f, pie.y + 5f, pie.x + a * 2.2f, pie.y + 9f, pie.x + a * 2.5f, pie.y + 13f); lineTo(pie.x - a * 2.3f, pie.y + 13f); cubicTo(pie.x - a * 1.9f, pie.y + 8f, pie.x - a * 1.4f, pie.y + 4f, pie.x - a - 1f, pie.y); close() }
        drawPath(rampa, Brush.verticalGradient(0f to s.arriba(if (sellada) lerp(C.CALIZA, C.ARENA, 0.5f) else C.LINO, 0.1f), 1f to s.arriba(lerp(C.CALIZA, C.ARENA, 0.4f), 0.1f).copy(alpha = 0.2f), startY = pie.y, endY = pie.y + 13f))
        for (j in 0..3) drawLine(s.sombra(C.ROCA, 0.1f).copy(alpha = 0.34f), Offset(pie.x - a * (1.06f + j * 0.34f), pie.y + 1.6f + j * 3f), Offset(pie.x + a * (1.06f + j * 0.38f), pie.y + 1.6f + j * 3f), 0.4f)
        // El rebaje en la roca, quebrado, en sombra bajo la visera de piedra.
        val corte = poligono(listOf(pie + Offset(-a - 3.6f, 0f), pie + Offset(-a - 2.2f, -alto * 0.6f), pie + Offset(-a - 3f, -alto - 4f), pie + Offset(-a + 1.4f, -alto - 7.4f), pie + Offset(a - 1f, -alto - 6.2f), pie + Offset(a + 3f, -alto - 4.4f), pie + Offset(a + 2.2f, -alto * 0.5f), pie + Offset(a + 3.4f, 0f)))
        drawPath(corte, s.medio(roca, 0.14f))
        clipPath(corte) {
            drawRect(Brush.verticalGradient(0f to s.sombra(C.ROCA_OSC, 0.14f).copy(alpha = 0.7f), 1f to Color.Transparent, startY = pie.y - alto - 8f, endY = pie.y - alto + 2f), Offset(pie.x - a - 5f, pie.y - alto - 8f), Size(ancho + 10f, 10f))
            drawRect(s.lado(roca, 0.14f, alReves = true).copy(alpha = 0.6f), Offset(pie.x - a - 4f, pie.y - alto - 8f), Size(4f, alto + 8f)); drawRect(s.lado(roca, 0.14f).copy(alpha = 0.6f), Offset(pie.x + a, pie.y - alto - 8f), Size(4f, alto + 8f))
        }
        drawPath(corte, s.sombra(C.ROCA_OSC, 0.14f).copy(alpha = 0.5f), style = Stroke(0.5f))
        val hueco = Rect(pie.x - a, pie.y - alto, pie.x + a, pie.y)
        // El dintel tallado: dos fajas rebajadas, cada una con su sombra debajo.
        drawRect(s.medio(lerp(C.CALIZA, C.ROCA, 0.18f), 0.12f), Offset(hueco.left - 1.4f, hueco.top - 3.6f), Size(ancho + 2.8f, 3.6f))
        drawLine(s.sombra(C.ROCA_OSC, 0.12f).copy(alpha = 0.7f), Offset(hueco.left - 1.4f, hueco.top - 1.8f), Offset(hueco.right + 1.4f, hueco.top - 1.8f), 0.5f); drawLine(s.luz(C.LINO, 0.12f).copy(alpha = 0.7f), Offset(hueco.left - 1.4f, hueco.top - 3.6f), Offset(hueco.right + 1.4f, hueco.top - 3.6f), 0.5f)
        if (sellada) {
            // Tapiada con bloques, enlucida y con los sellos de la necrópolis.
            drawRect(s.frente(lerp(C.CALIZA, C.ARENA, 0.4f), 0.14f), hueco.topLeft, hueco.size)
            for (j in 0..4) { val y = hueco.top + j * alto / 5f; drawLine(s.sombra(C.ROCA, 0.14f).copy(alpha = 0.6f), Offset(hueco.left, y), Offset(hueco.right, y), 0.4f); for (i in 0..2) drawLine(s.sombra(C.ROCA, 0.14f).copy(alpha = 0.5f), Offset(hueco.left + (i + (j % 2) * 0.5f) * ancho / 3f, y), Offset(hueco.left + (i + (j % 2) * 0.5f) * ancho / 3f, y + alto / 5f), 0.4f) }
            for (q in listOf(Offset(-2.4f, -alto * 0.7f), Offset(2f, -alto * 0.45f), Offset(-1f, -alto * 0.25f))) drawOval(s.medio(lerp(C.TERRACOTA, C.ADOBE, 0.5f), 0.1f), pie + q + Offset(-1.2f, -0.8f), Size(2.4f, 1.6f))
            drawRect(Brush.verticalGradient(0f to s.sombra(C.ROCA_OSC, 0.14f).copy(alpha = 0.5f), 1f to Color.Transparent, startY = hueco.top, endY = hueco.top + 4f), hueco.topLeft, Size(ancho, 4f))
        } else {
            // El hueco: negro arriba, donde no entra la luz, y algo más claro abajo, donde empieza la rampa que baja.
            drawRect(Brush.verticalGradient(0f to lerp(s.sombra(C.HOLLIN, 0.06f), C.LUMBRE, 0.5f * alumbrada), 0.7f to lerp(s.sombra(C.HOLLIN, 0.08f), C.LUMBRE, 0.4f * alumbrada), 1f to lerp(s.sombra(C.MADERA_OSC, 0.1f), C.LUMBRE, 0.3f * alumbrada), startY = hueco.top, endY = hueco.bottom), hueco.topLeft, hueco.size)
            for (j in 0..2) drawLine(s.luz(C.CALIZA, 0.1f).copy(alpha = 0.42f - j * 0.13f), Offset(hueco.left + 1.6f + j * 1.2f, hueco.bottom - 1f - j * 2.2f), Offset(hueco.right - 1.6f - j * 1.2f, hueco.bottom - 1f - j * 2.2f), 0.6f)
            // El grueso de la roca, que se ve por dentro del lado que mira al sol.
            drawRect(Brush.verticalGradient(0f to s.sombra(roca, 0.12f).copy(alpha = 0.5f), 1f to s.medio(roca, 0.12f).copy(alpha = 0.9f), startY = hueco.top, endY = hueco.bottom), Offset(if (s.solALaDerecha) hueco.left else hueco.right - 1.8f, hueco.top), Size(1.8f, alto))
        }
    }

    /** Un montón de lascas de caliza: un cono con su cara de luz y su cara de sombra, y las lascas sueltas al pie. */
    private fun DrawScope.monton(s: SolEgipto, pie: Offset, ancho: Float, alto: Float, viejo: Boolean, az: Azar) {
        val piedra = if (viejo) lerp(C.CALIZA, C.ARENA, 0.4f) else C.CALIZA; val cima = pie + Offset(az.entre(-0.2f, 0.2f) * ancho, -alto)
        // Un montón de escombro: romo, con una joroba a un lado; no un cono.
        val forma = Path().apply { moveTo(pie.x - ancho / 2f, pie.y); cubicTo(pie.x - ancho * 0.42f, pie.y - alto * 0.7f, cima.x - ancho * 0.22f, cima.y - alto * 0.06f, cima.x, cima.y); cubicTo(cima.x + ancho * 0.16f, cima.y + alto * 0.04f, cima.x + ancho * 0.2f, cima.y + alto * 0.34f, cima.x + ancho * 0.3f, cima.y + alto * 0.36f); cubicTo(pie.x + ancho * 0.42f, pie.y - alto * 0.5f, pie.x + ancho * 0.46f, pie.y - alto * 0.2f, pie.x + ancho / 2f, pie.y); close() }
        drawPath(forma, Brush.horizontalGradient(0f to s.luz(C.LINO, 0.1f), 0.45f to s.medio(piedra, 0.12f), 1f to s.sombra(lerp(piedra, C.ROCA, 0.3f), 0.12f), startX = if (s.solALaDerecha) pie.x + ancho / 2f else pie.x - ancho / 2f, endX = if (s.solALaDerecha) pie.x - ancho / 2f else pie.x + ancho / 2f))
        clipPath(forma) {
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to s.sombra(lerp(piedra, C.ROCA, 0.3f), 0.12f).copy(alpha = 0.4f), startY = pie.y - alto * 0.4f, endY = pie.y), Offset(pie.x - ancho, pie.y - alto * 0.4f), Size(ancho * 2f, alto * 0.4f))
            repeat((ancho * 1.2f).toInt()) { val q = pie + Offset(az.entre(-ancho / 2f, ancho / 2f), -az.entre(0f, alto)); drawLine(s.sombra(C.ROCA, 0.12f).copy(alpha = 0.5f), q, q + Offset(az.entre(0.8f, 2f), az.entre(-0.6f, 0.6f)), 0.4f) }
        }
        drawPoints((0 until (ancho * 0.9f).toInt()).map { pie + Offset(az.entre(-ancho * 0.7f, ancho * 0.7f), az.entre(-0.4f, 2f)) }, PointMode.Points, s.luz(piedra, 0.1f), 1f, StrokeCap.Round)
    }

    /**
     * El andamio de la boca, de palos atados: cuatro pies derechos fuera del hueco, algo torcidos y de alturas
     * desiguales; dos largueros que sobresalen; dos riostras; un tablón a media altura y otro arriba, sobre el dintel;
     * y la escalera de mano, apoyada en diagonal. [a] es la mitad del ancho de la boca.
     */
    private fun DrawScope.andamio(s: SolEgipto, pie: Offset, a: Float, alto: Float) {
        val claro = s.luz(C.ESTERA, 0.06f); val palo = s.medio(lerp(C.MADERA, C.ESTERA, 0.35f), 0.06f); val osc = s.sombra(C.MADERA_OSC, 0.06f)
        // Cada vara: su sombra debajo, para que se lea sobre la roca clara, y su lomo claro, para que se lea sobre el hueco.
        fun vara(p: Offset, q: Offset, g: Float = 1f) { drawLine(osc, p + Offset(0.5f, 0.4f), q + Offset(0.5f, 0.4f), 1.5f * g, StrokeCap.Round); drawLine(palo, p, q, 1f * g, StrokeCap.Round); drawLine(claro, p + Offset(-0.25f, 0f), q + Offset(-0.25f, 0f), 0.4f * g) }
        val bajo = pie.y - alto * 0.46f; val arriba = pie.y - alto - 4.4f
        val pies = listOf(Triple(-2.3f, 0.3f, 13f), Triple(-1.3f, -0.24f, 5f), Triple(1.34f, 0.26f, 10f), Triple(2.24f, -0.3f, 2.6f))
        // Los tablones, detrás de los palos: el de media altura, a la derecha, y el de arriba, a la izquierda, cada uno con su canto en sombra.
        for ((x0, y) in listOf(0.7f to bajo, -2.6f to arriba)) { drawRect(claro, Offset(pie.x + x0 * a, y - 2f), Size(1.9f * a, 1.5f)); drawRect(osc.copy(alpha = 0.8f), Offset(pie.x + x0 * a, y - 0.5f), Size(1.9f * a, 0.7f)) }
        val cesto = Offset(pie.x + 1.8f * a, bajo - 2f); drawPath(poligono(cesto + Offset(-1.6f, 0f), cesto + Offset(-2.1f, -2.4f), cesto + Offset(2.1f, -2.4f), cesto + Offset(1.6f, 0f)), s.frente(C.ESTERA, 0.06f)); drawOval(s.luz(C.LINO, 0.06f), cesto + Offset(-2f, -3.2f), Size(4f, 1.5f))
        for ((x, tuerce, sobra) in pies) vara(pie + Offset(x * a, 1.6f + abs(x) * 1.2f), Offset(pie.x + x * a + tuerce * 3f, arriba - sobra))
        vara(Offset(pie.x - 2.7f * a, bajo + 0.7f), Offset(pie.x + 2.5f * a, bajo - 0.5f), 0.9f); vara(Offset(pie.x - 2.6f * a, arriba + 0.5f), Offset(pie.x + 2.9f * a, arriba - 0.7f), 0.9f)
        vara(pie + Offset(-2.3f * a, 1f), Offset(pie.x - 1.3f * a, bajo), 0.7f); vara(Offset(pie.x + 1.34f * a, bajo), Offset(pie.x + 2.24f * a, arriba), 0.7f)
        for ((x, _, _) in pies) for (y in listOf(bajo, arriba)) { drawCircle(osc, 1.1f, Offset(pie.x + x * a, y)); drawCircle(claro, 0.7f, Offset(pie.x + x * a - 0.1f, y - 0.1f)) }
        // La escalera de mano, en diagonal, del suelo al tablón de media altura.
        val p0 = pie + Offset(3.7f * a, 5f); val p1 = Offset(pie.x + 2.1f * a, bajo - 3.4f)
        for (d in listOf(0f, 2.8f)) vara(p0 + Offset(d, 0f), p1 + Offset(d, 0f), 0.7f); for (j in 1..5) { val q = p0 + (p1 - p0) * (j / 6f); drawLine(osc, q, q + Offset(2.8f, 0f), 0.7f); drawLine(claro, q + Offset(0f, -0.3f), q + Offset(2.8f, -0.3f), 0.3f) }
    }

    /**
     * El barranco blanco de la falda de la montaña, con la tumba del rey: la boca, el andamio, los montones de lascas
     * y lo que pase ese día (el trineo del entierro, el pabellón de la visita, las antorchas, el agua de la lluvia).
     */
    fun DrawScope.barranco(m: Momento, s: SolEgipto) { scale(EX, 1f, Offset.Zero) { barrancoSinEstrechar(m, s) } }

    private fun DrawScope.barrancoSinEstrechar(m: Momento, s: SolEgipto) {
        val l = s.l; val az = Azar(Semilla.de("egipto", "barranco"))
        val borde = listOf(Offset(144f, 116f), Offset(150f, 108f), Offset(158f, 105f), Offset(164f, 98f), Offset(174f, 96.4f), Offset(184f, 93f), Offset(193f, 95f), Offset(199f, 101f), Offset(207f, 103f), Offset(213f, 110f), Offset(223f, 112f), Offset(231f, 119f), Offset(242f, 121f), Offset(250f, 128f), Offset(262f, 132f))
        val forma = Path().apply { addPath(quebrada(borde)); lineTo(266f, 157f); lineTo(140f, 157f); close() }
        drawPath(forma, Brush.verticalGradient(0f to s.frente(lerp(C.CALIZA, C.ROCA, 0.24f), 0.2f), 0.55f to s.frente(C.CALIZA, 0.16f), 1f to s.arriba(lerp(C.CALIZA, C.ARENA, 0.3f), 0.1f), startY = 93f, endY = 157f))
        clipPath(forma) {
            // La pared: las grietas que bajan, los estratos y la sombra bajo el borde.
            drawPath(quebrada(borde.map { it + Offset(0f, 2f) }), s.sombra(C.ROCA_OSC, 0.2f).copy(alpha = 0.5f), style = Stroke(3f))
            // Los contrafuertes de la pared: una cara al sol y otra en sombra, hasta la pedrera.
            var bx = 142f
            while (bx < 264f) { val w = az.entre(5f, 11f); val y0 = borde.minByOrNull { kotlin.math.abs(it.x - (bx + w / 2f)) }!!.y + 2f; val y1 = az.entre(126f, 136f); val filo = bx + w * (if (s.solALaDerecha) 0.4f else 0.6f)
                if (y1 > y0 + 4f) { drawPath(poligono(Offset(bx, y0), Offset(filo, y0), Offset(filo, y1 + 2f), Offset(bx, y1)), (if (s.solALaDerecha) s.sombra(lerp(C.CALIZA, C.ROCA, 0.5f), 0.18f) else s.luz(C.LINO, 0.16f)).copy(alpha = 0.5f)); drawPath(poligono(Offset(filo, y0), Offset(bx + w, y0), Offset(bx + w, y1), Offset(filo, y1 + 2f)), (if (s.solALaDerecha) s.luz(C.LINO, 0.16f) else s.sombra(lerp(C.CALIZA, C.ROCA, 0.5f), 0.18f)).copy(alpha = 0.5f))
                    drawPath(poligono(Offset(bx - 2f, y1 + az.entre(6f, 12f)), Offset(filo, y1), Offset(bx + w + 2f, y1 + az.entre(6f, 12f))), s.luz(C.LINO, 0.14f).copy(alpha = 0.4f)) }
                bx += w }
            repeat(26) { val x = az.entre(146f, 252f); val y0 = az.entre(100f, 124f); drawLine(s.sombra(C.ROCA, 0.18f).copy(alpha = 0.5f), Offset(x, y0), Offset(x + az.entre(-2f, 2f), y0 + az.entre(6f, 18f)), 0.5f) }
            for (j in 0..3) drawPath(quebrada(borde.map { it + Offset(0f, 8f + j * 6f + 2f * sin(it.x * 0.1f + j)) }), s.sombra(C.ROCA, 0.18f).copy(alpha = 0.3f), style = Stroke(0.5f))
            // El lado del barranco que queda en sombra, según de dónde venga el sol.
            drawRect(Brush.horizontalGradient(if (s.solALaDerecha) listOf(Color.Transparent, s.sombra(C.ROCA, 0.18f).copy(alpha = 0.44f * s.r.dia)) else listOf(s.sombra(C.ROCA, 0.18f).copy(alpha = 0.44f * s.r.dia), Color.Transparent), 150f, 252f), Offset(142f, 96f), Size(116f, 40f))
            // El suelo: los derrubios blancos y el camino pisado hasta la boca.
            repeat(30) { mancha(Offset(az.entre(146f, 262f), az.entre(136f, 157f)), az.entre(5f, 14f), az.entre(1f, 2.4f), if (it % 2 == 0) s.luz(C.LINO, 0.1f) else s.sombra(C.ARENA, 0.1f), 0.4f) }
            if (EscenaEgipto.torrente(m)) { val agua = Path().apply { moveTo(190f, 112f); cubicTo(196f, 126f, 184f, 136f, 198f, 144f); cubicTo(206f, 148f, 200f, 150f, 210f, 153f) }
                drawPath(agua, l.tono(lerp(C.NILO, C.ARENA, 0.4f), 0.1f).copy(alpha = 0.85f), style = Stroke(4f, cap = StrokeCap.Round)); drawPath(agua, Color.White.copy(alpha = 0.4f), style = Stroke(0.8f)) }
        }
        val nueva = EscenaEgipto.tumbaNueva(m); val obra = EscenaEgipto.tumba(m); val noche = l.noche
        val antorchas = if (EscenaEgipto.antorchasDeLaObra(m)) noche else 0f
        // La tumba de Seti II: en obra hasta el entierro; después, sellada, con sus montones ya del color de la arena.
        val viejos = EscenaEgipto.sellada(m)
        boca(s, SETI, 17f, 26f, viejos, antorchas)
        val cuantosSeti = if (nueva || viejos) 5 else EscenaEgipto.montones(m)
        val sitiosSeti = SITIOS_SETI
        for (j in 0 until cuantosSeti) monton(s, sitiosSeti[j], 20f + j % 3 * 4f, (if (nueva || viejos) 12f else 6f + 8f * obra) + j % 2 * 2f, viejos, az)
        if (!viejos && EscenaEgipto.andamio(m)) andamio(s, SETI, 8.5f, 26f)
        // La de Siptah: el primer corte, la boca que crece y sus montones, que empiezan de cero.
        if (nueva) {
            val alto = 21f * (0.3f + 0.7f * (obra / 0.3f).coerceIn(0f, 1f))
            boca(s, SIPTAH, 16f, alto, false, antorchas)
            val sitios = SITIOS_SIPTAH
            for (j in 0 until EscenaEgipto.montones(m)) monton(s, sitios[j], 12f + 18f * obra + j % 2 * 2f, 4f + 15f * obra + j % 2, false, az)
            if (EscenaEgipto.andamio(m)) andamio(s, SIPTAH, 8f, alto)
        }
        val activa = if (nueva) SIPTAH else SETI
        // Lo de la obra: los cestos de las lascas, el trineo de madera y las tinajas del agua.
        if (!viejos || nueva) {
            for (q in listOf(Offset(-16f, 3f), Offset(-11f, 5f), Offset(13f, 6f))) { val c = activa + q; drawPath(poligono(c + Offset(-1.8f, 0f), c + Offset(-2.4f, -2.6f), c + Offset(2.4f, -2.6f), c + Offset(1.8f, 0f)), s.frente(C.ESTERA, 0.08f)); drawOval(s.luz(C.CALIZA, 0.06f), c + Offset(-2.2f, -3.4f), Size(4.4f, 1.6f)) }
            val t = activa + Offset(-22f, 9f); drawLine(s.medio(C.MADERA, 0.06f), t, t + Offset(11f, 0.6f), 1.2f, StrokeCap.Round); drawLine(s.medio(C.MADERA, 0.06f), t + Offset(11f, 0.6f), t + Offset(13f, -1.4f), 1f, StrokeCap.Round); drawRect(s.luz(C.CALIZA, 0.06f), t + Offset(2f, -3f), Size(6f, 2.6f))
            for (d in listOf(0f, 3.4f)) drawOval(s.frente(C.TERRACOTA, 0.08f), activa + Offset(9f + d, -3.6f), Size(2.8f, 4.2f))
        }
        // El entierro del rey: el trineo con su capilla dorada y los estandartes, del camino a la boca.
        if (EscenaEgipto.entierro(m)) { val c = SETI + Offset(-26f, 12f)
            drawLine(s.medio(C.MADERA_OSC, 0.06f), c + Offset(-9f, 0f), c + Offset(9f, 0f), 1.2f, StrokeCap.Round); drawLine(s.medio(C.MADERA_OSC, 0.06f), c + Offset(9f, 0f), c + Offset(11.4f, -2.4f), 1f, StrokeCap.Round)
            drawRect(s.frente(C.OCRE, 0.05f), c + Offset(-6f, -9f), Size(12f, 8.4f)); drawRect(s.luz(C.LINO, 0.05f), c + Offset(-7f, -10.6f), Size(14f, 1.8f)); for (j in 0..3) drawLine(s.sombra(C.MADERA_OSC, 0.05f), c + Offset(-4.6f + j * 3f, -8.4f), c + Offset(-4.6f + j * 3f, -1f), 0.5f)
            for (j in 0..6) { val q = Offset(150f + j * 7f, 152.4f - j * 0.8f); drawLine(s.medio(C.MADERA, 0.06f), q, q + Offset(0f, -9f), 0.5f); drawRect(s.medio(if (j % 2 == 0) C.TERRACOTA else C.LINO, 0.05f), q + Offset(0f, -9f), Size(2.4f, 3.6f)) } }
        // La visita del visir o del canciller: un pabellón de rayas delante de la tumba, con sus dos abanicos.
        if (EscenaEgipto.visita(m)) { val c = activa + Offset(-30f, 12f)
            for (x in listOf(-8f, 8f)) drawLine(s.medio(C.MADERA, 0.06f), c + Offset(x, 0f), c + Offset(x, -11f), 0.7f)
            val lona = poligono(c + Offset(-10f, -11f), c + Offset(0f, -14.6f), c + Offset(10f, -11f), c + Offset(10f, -9f), c + Offset(-10f, -9f)); drawPath(lona, s.luz(C.LINO, 0.05f)); clipPath(lona) { for (j in 0..4) drawRect(s.medio(C.TERRACOTA, 0.05f), c + Offset(-10f + j * 4.6f, -15f), Size(2.2f, 7f)) }
            for (x in listOf(-13f, 13f)) { drawLine(s.medio(C.OCRE, 0.05f), c + Offset(x, 0f), c + Offset(x, -13f), 0.5f); drawOval(s.luz(C.LINO, 0.05f), c + Offset(x - 2f, -16.6f), Size(4f, 4f)) } }
        // De noche, mientras hay prisa, dos antorchas en la boca: alumbran la pared y los montones.
        if (antorchas > 0.05f) { resplandor(activa + Offset(0f, -10f), 30f, 0.7f * antorchas, C.LUMBRE); mancha(activa + Offset(0f, 6f), 26f, 5f, C.LUMBRE, 0.3f * antorchas) }
    }

    /** Las sombras cortas de lo que hay en el barranco y en el valle. Va dentro de una capa de sombra. */
    fun DrawScope.sombrasLejanas(m: Momento, r: Rayo, c: Color) {
        val dx = (r.ax * 5f).coerceIn(-14f, 14f)
        fun charco(q: Offset, w: Float) = sombra(Path().apply { addOval(Rect(q.x - w / 2f + dx, q.y - 1f, q.x + w / 2f + dx, q.y + 1.6f)) }, c)
        scale(EX, 1f, Offset.Zero) {
            for (q in SITIOS_SETI.take(if (EscenaEgipto.tumbaNueva(m) || EscenaEgipto.sellada(m)) 5 else EscenaEgipto.montones(m))) charco(q, 20f)
            if (EscenaEgipto.tumbaNueva(m)) for (q in SITIOS_SIPTAH.take(EscenaEgipto.montones(m))) charco(q, 12f + 16f * EscenaEgipto.tumba(m))
        }
        sombra(poligono(TEMPLO + Offset(26f, 0f), TEMPLO + Offset(46f, 0f), TEMPLO + Offset(46f + dx * 1.6f, 3.4f), TEMPLO + Offset(26f + dx * 1.6f, 3.4f)), c)
        for (x in COLOSOS) charco(Offset(x, COLOSO_Y + 0.4f), 8f)
        for (p in pueblos()) if (p.y > hastaDondeLlegaElAgua(EscenaEgipto.rio(m), p.x)) charco(p + Offset(0f, 1.6f), 22f)
    }

    // ---------------------------------------------------------------- La ladera de las tumbas

    /** Las capillas de la ladera, de la más lejana a la más cercana: dónde pisa cada una y cuánto mide su pirámide. */
    private val CAPILLAS = listOf(
        Triple(18f, 141f, 6.4f), Triple(42f, 139f, 7f), Triple(68f, 141.6f, 6.6f), Triple(94f, 141f, 7.4f), Triple(110f, 147f, 6.4f),
        Triple(8f, 158f, 11f), Triple(40f, 156f, 12f), Triple(76f, 159f, 11f),
        Triple(22f, 182f, 18f), Triple(64f, 180f, 16.4f), Triple(104f, 183f, 15f),
    )

    /** La de Tiy, la madre de Pay: entre las de en medio, recién enlucida. */
    val TIY = Triple(116f, 165f, 11.4f)

    /**
     * Una capilla de tumba: el patio con su tapia, el pórtico con la puerta y, encima, la pirámide pequeña de adobe
     * enlucido, con su punta de piedra y el nicho de la estela. [fresca]: recién hecha, blanca y sin desconchones.
     */
    private fun DrawScope.capilla(s: SolEgipto, x: Float, y: Float, h: Float, fresca: Boolean, lampara: Float, flores: Boolean, az: Azar) {
        val lejos = (0.3f - h * 0.012f).coerceIn(0.04f, 0.3f); val cal = if (fresca) C.LINO else lerp(C.CALIZA, C.ADOBE, 0.24f)
        // La tapia del patio y el pórtico.
        drawRect(s.frente(lerp(C.ADOBE, C.CALIZA, 0.3f), lejos), Offset(x - h * 0.8f, y - h * 0.26f), Size(h * 1.6f, h * 0.26f)); drawRect(s.arriba(C.ENLUCIDO, lejos), Offset(x - h * 0.8f, y - h * 0.3f), Size(h * 1.6f, h * 0.07f))
        val portico = poligono(Offset(x - h * 0.4f, y), Offset(x - h * 0.36f, y - h * 0.56f), Offset(x + h * 0.36f, y - h * 0.56f), Offset(x + h * 0.4f, y))
        drawPath(portico, s.frente(cal, lejos)); drawRect(s.lado(cal, lejos), Offset(if (s.solALaDerecha) x + h * 0.24f else x - h * 0.38f, y - h * 0.56f), Size(h * 0.13f, h * 0.56f)); drawRect(s.arriba(C.LINO, lejos), Offset(x - h * 0.42f, y - h * 0.62f), Size(h * 0.84f, h * 0.08f))
        drawRect(lerp(s.sombra(C.HOLLIN, lejos), C.LUMBRE, 0.7f * lampara), Offset(x - h * 0.1f, y - h * 0.36f), Size(h * 0.2f, h * 0.36f)); drawRect(s.medio(C.TERRACOTA, lejos), Offset(x - h * 0.13f, y - h * 0.42f), Size(h * 0.26f, h * 0.06f))
        // La pirámide: empinada, con una cara al sol y otra en sombra.
        val pie = y - h * 0.6f; val punta = Offset(x, pie - h * 0.86f)
        drawPath(poligono(Offset(x - h * 0.34f, pie), punta, Offset(x + h * 0.04f, pie)), s.lado(cal, lejos, alReves = true)); drawPath(poligono(Offset(x + h * 0.04f, pie), punta, Offset(x + h * 0.34f, pie)), s.lado(cal, lejos))
        drawPath(poligono(punta + Offset(-h * 0.07f, h * 0.18f), punta, punta + Offset(h * 0.07f, h * 0.18f)), s.medio(C.ROCA_OSC, lejos))
        drawRect(s.sombra(C.BARRO, lejos), Offset(x - h * 0.05f, pie - h * 0.4f), Size(h * 0.1f, h * 0.14f))
        if (!fresca && h > 9f) repeat(3) { val q = Offset(x + az.entre(-0.2f, 0.2f) * h, pie - az.entre(0.06f, 0.4f) * h); drawPath(masa(az, q, h * 0.07f, h * 0.05f, 6, 0.4f), s.medio(C.ADOBE, lejos).copy(alpha = 0.8f)) }
        if (flores) { drawLine(s.medio(C.PALMA, lejos), Offset(x - h * 0.3f, y - h * 0.5f), Offset(x + h * 0.3f, y - h * 0.5f), (h * 0.07f).coerceAtLeast(0.6f)); for (j in 0..4) drawCircle(s.luz(if (j % 2 == 0) C.LOZA else C.LINO, lejos), (h * 0.05f).coerceAtLeast(0.5f), Offset(x - h * 0.26f + j * h * 0.13f, y - h * 0.5f)) }
        if (lampara > 0.05f) { resplandor(Offset(x, y - h * 0.2f), h * 0.9f, 0.6f * lampara, C.LUMBRE); mancha(Offset(x, y + 1f), h * 0.6f, h * 0.12f, C.LUMBRE, 0.3f * lampara) }
    }

    /** La ladera de la aldea: la pendiente de piedra y arena, los senderos y las tumbas de los obreros con sus pirámides. */
    fun DrawScope.ladera(m: Momento, s: SolEgipto) {
        val l = s.l; val az = Azar(Semilla.de("egipto", "ladera"))
        val borde = listOf(Offset(-6f, 134f), Offset(20f, 130f), Offset(48f, 133f), Offset(80f, 129.4f), Offset(110f, 139f), Offset(120f, 150f), Offset(130f, 157f), Offset(162f, 160f), Offset(178f, 162f), Offset(188f, 174f))
        val forma = Path().apply { addPath(curva(borde)); lineTo(190f, 210f); lineTo(-6f, 210f); close() }
        drawPath(forma, Brush.verticalGradient(0f to s.arriba(lerp(C.ARENA, C.CALIZA, 0.35f), 0.26f), 1f to s.arriba(lerp(C.ARENA, C.ROCA, 0.2f), 0.04f), startY = 128f, endY = 200f))
        clipPath(forma) {
            // Las terrazas de las tumbas: cada escalón, con su muro de piedra seca en sombra, y el pedregal.
            for (j in 0..3) { val y = 146f + j * 14f + j * j; drawPath(curva(listOf(Offset(-6f, y + 2f), Offset(40f, y), Offset(90f, y + 3f), Offset(150f, y + 6f + j * 2f))), s.sombra(C.ROCA, 0.2f - j * 0.04f).copy(alpha = 0.46f), style = Stroke(1.2f + j * 0.3f)); drawPath(curva(listOf(Offset(-6f, y + 0.8f), Offset(40f, y - 1.2f), Offset(90f, y + 1.8f), Offset(150f, y + 4.8f + j * 2f))), s.luz(C.CALIZA, 0.2f - j * 0.04f).copy(alpha = 0.6f), style = Stroke(0.6f)) }
            repeat(60) { val q = Offset(az.entre(-4f, 180f), az.entre(132f, 198f)); val k = 0.4f + (q.y - 130f) / 70f; mancha(q, az.entre(5f, 16f) * k, az.entre(1f, 2.6f) * k, if (it % 3 == 0) s.luz(C.CALIZA, 0.1f) else s.sombra(C.ROCA, 0.1f), 0.22f) }
            repeat(34) { val q = Offset(az.entre(-4f, 176f), az.entre(136f, 198f)); val k = 0.4f + (q.y - 130f) / 60f; drawOval(s.sombra(C.ROCA_OSC, 0.1f), Offset(q.x, q.y), Size(3f * k, 1.6f * k)); drawOval(s.luz(C.CALIZA, 0.1f), Offset(q.x + (if (s.solALaDerecha) 0.8f * k else 0f), q.y - 0.4f * k), Size(2.2f * k, 1.1f * k)) }
            // Los senderos: de la puerta del muro a las capillas y al sendero del paso.
            for (camino in listOf(listOf(Offset(186f, 166f), Offset(160f, 156f), Offset(150f, 150f)), listOf(Offset(170f, 160f), Offset(120f, 164f), Offset(84f, 168f), Offset(44f, 176f), Offset(10f, 186f)), listOf(Offset(118f, 164f), Offset(96f, 152f), Offset(60f, 148f), Offset(20f, 146f)))) drawPath(curva(camino), s.luz(C.LINO, 0.1f).copy(alpha = 0.55f), style = Stroke(1.2f, cap = StrokeCap.Round))
            // De noche, la luna en la piedra.
            if (l.noche > 0.4f && l.nublado < 0.6f) mancha(Offset(l.lunaX * ANCHO - 150f, 160f), 90f, 22f, C.LUNA, 0.1f * l.noche)
        }
    }

    /** Las capillas de la ladera, encima de sus sombras. */
    fun DrawScope.capillas(m: Momento, s: SolEgipto) {
        val l = s.l; val az = Azar(Semilla.de("egipto", "capillas"))
        val lamparas = if (EscenaEgipto.lamparasDeLasTumbas(m)) l.noche.coerceAtLeast(0.12f) else 0f; val flores = EscenaEgipto.barcaDeAmon(m)
        val todas = (CAPILLAS + (if (EscenaEgipto.tumbaDeTiy(m)) listOf(TIY) else emptyList())).sortedBy { it.second }
        for (c in todas) { val suya = c === TIY
            capilla(s, c.first, c.second, c.third, suya && !EscenaEgipto.paso(m, 5, "01-01"), if (suya && (EscenaEgipto.hoyEs(m, "muere_tiy") || EscenaEgipto.hoyEs(m, "huy_carta"))) maxOf(lamparas, 0.5f) else if (c.third > 9f || az.real() > 0.4f) lamparas else 0f, flores || (suya && EscenaEgipto.hoyEs(m, "muere_tiy")), az) }
    }

    /** Las sombras de las pirámides sobre la ladera: un triángulo tumbado que se alarga con el sol bajo. */
    fun DrawScope.sombrasDeLaLadera(m: Momento, r: Rayo, c: Color) {
        for (k in CAPILLAS + (if (EscenaEgipto.tumbaDeTiy(m)) listOf(TIY) else emptyList())) { val x = k.first; val y = k.second; val h = k.third
            val dx = (r.ax * h * 0.9f).coerceIn(-h * 2.4f, h * 2.4f)
            sombra(poligono(Offset(x - h * 0.4f, y), Offset(x + h * 0.4f, y), Offset(x + dx + h * 0.1f, y - h * 0.16f + 1f), Offset(x + dx - h * 0.1f, y - h * 0.2f + 1f)), c) }
    }

    // ---------------------------------------------------------------- La aldea

    /** Las cuatro filas de azoteas, de la más lejana a la más cercana: a qué altura quedan y cuánto se agrandan. */
    private val FILAS = listOf(163f to 0.5f, 170f to 0.64f, 178f to 0.8f, 187f to 1f)
    private const val FUGA_X = 199f

    private fun calleIzq(fila: Int) = 206f - (3 - fila) * 3.4f
    private fun calleDer(fila: Int) = 222f - (3 - fila) * 6.6f
    private fun bordeIzq(fila: Int) = 96f + (3 - fila) * 18f
    private fun bordeDer(fila: Int) = 292f - (3 - fila) * 17f

    /** Las bocas de humo de las cocinas de la aldea (los patios del fondo de cada casa), para la parte viva. */
    val COCINAS = listOf(Offset(132f, 176f), Offset(176f, 168f), Offset(244f, 176f), Offset(270f, 185f), Offset(228f, 161.4f), Offset(160f, 185f))

    /** Cuántas azoteas tienen luz a esta hora, de 0 a 1: muchas al anochecer, pocas de madrugada, y menos si los hombres están en el paso. */
    fun encendidas(m: Momento): Float { val n = m.luces().noche; if (n < 0.3f) return 0f
        val hora = m.hora; val despiertos = if (hora >= 19.0 && hora < 22.0) 0.75f else if (hora >= 22.0 && hora < 23.4) 0.4f else if (hora >= 4.4 && hora < 6.0) 0.5f else 0.12f
        return despiertos * (if (EscenaEgipto.enElPaso(m)) 0.55f else 1f) * (if (EscenaEgipto.fiesta(m) && !EscenaEgipto.enElPaso(m) && hora >= 19.0) 1.3f else 1f) }

    /**
     * La aldea desde arriba: el muro del norte con su puerta, la calle estrecha entre las dos hileras de casas y, sobre
     * cada azotea, lo que la gente tiene en ella (toldos de estera, la caseta de la escalera, tinajas, graneros, ropa).
     */
    fun DrawScope.aldea(m: Momento, s: SolEgipto) {
        val l = s.l; val az = Azar(Semilla.de("egipto", "aldea")); val luces = encendidas(m); val hoy = m.azar("aldea")
        // El muro del norte, de adobe, y su puerta: abierta de día, cerrada al caer la tarde.
        drawRect(s.frente(lerp(C.ADOBE, C.CALIZA, 0.2f), 0.14f), Offset(150f, 156f), Size(104f, 8f)); drawRect(s.arriba(C.ENLUCIDO, 0.14f), Offset(150f, 155.2f), Size(104f, 1.2f))
        for (j in 0..10) drawLine(s.sombra(C.BARRO, 0.14f).copy(alpha = 0.3f), Offset(154f + j * 9.6f + az.entre(-2f, 2f), 157f), Offset(154f + j * 9.6f, 163f), 0.4f)
        val cerrada = l.noche > 0.3f || EscenaEgipto.polvo(m)
        drawRect(if (cerrada) s.frente(C.MADERA, 0.1f) else s.sombra(C.HOLLIN, 0.1f), Offset(196.4f, 158f), Size(5.6f, 6f)); drawRect(s.medio(C.TERRACOTA, 0.1f), Offset(195.6f, 157.2f), Size(7.2f, 1f))
        // Las tinajas del agua, junto a la puerta: ahí descarga el aguador.
        for (j in 0..2) drawOval(s.frente(C.TERRACOTA, 0.1f), Offset(204.4f + j * 3f, 160f), Size(2.6f, 4f))
        // La calle: una rendija en sombra, con una pared al sol y las puertas pintadas de rojo.
        val calle = poligono(Offset(calleIzq(3), 196f), Offset(calleDer(3), 196f), Offset(calleDer(0) - 0.6f, 162f), Offset(calleIzq(0) + 0.6f, 162f))
        drawPath(calle, s.sombra(lerp(C.BARRO, C.ADOBE, 0.3f), 0.06f))
        val alSol = if (s.solALaDerecha) poligono(Offset(calleIzq(3), 196f), Offset(calleIzq(3) + 6f, 196f), Offset(calleIzq(0) + 2.4f, 162f), Offset(calleIzq(0) + 0.6f, 162f)) else poligono(Offset(calleDer(3) - 6f, 196f), Offset(calleDer(3), 196f), Offset(calleDer(0) - 0.6f, 162f), Offset(calleDer(0) - 2.4f, 162f))
        drawPath(alSol, s.luz(C.ENLUCIDO, 0.06f).copy(alpha = 0.3f + 0.6f * s.r.dia * s.r.fuerza))
        for (j in 0..3) { val t = j / 3.4f; val x = (if (s.solALaDerecha) calleIzq(3) + 2.4f else calleDer(3) - 4.4f) + (FUGA_X - 212f) * t * 0.5f; val y = 193f - t * 28f; drawRect(s.medio(C.TERRACOTA, 0.04f), Offset(x, y - 4.6f * (1f - t * 0.6f)), Size(2f * (1f - t * 0.5f), 4.6f * (1f - t * 0.6f))) }
        if (luces > 0f) drawPath(calle, C.LUMBRE.copy(alpha = 0.3f * luces * l.noche))
        // Las azoteas, fila por fila.
        for ((fila, par) in FILAS.withIndex()) { val (y, k) = par; val abajo = if (fila < 3) FILAS[fila + 1].first else 198f; val lejos = 0.14f - fila * 0.04f
            for (lado in 0..1) { var x = if (lado == 0) bordeIzq(fila) else calleDer(fila); val fin = if (lado == 0) calleIzq(fila) else bordeDer(fila)
                while (x < fin - 6f * k) { val w = minOf(az.entre(26f, 46f) * k, fin - x); val alza = az.entre(-2.2f, 2.2f) * k; val techo = y + alza
                    casaDeAzotea(m, s, Rect(x, techo, x + w, abajo + 1f), k, lejos, az, luces, hoy, fila)
                    x += w } } }
    }

    /** Una casa de la aldea vista desde otra azotea: la franja del terrado, el pretil, el muro con sus desconchones y lo que hay arriba. */
    private fun DrawScope.casaDeAzotea(m: Momento, s: SolEgipto, r: Rect, k: Float, lejos: Float, az: Azar, luces: Float, hoy: Azar, fila: Int) {
        val l = s.l; val encalada = az.real() < 0.3f; val muro = if (encalada) lerp(C.ENLUCIDO, C.CALIZA, 0.4f) else lerp(C.ADOBE, C.ARENA, az.entre(0f, 0.3f))
        // El muro, con sus hiladas, las cabezas de las vigas, las chorreras y los desconchones.
        drawRect(Brush.verticalGradient(0f to s.frente(muro, lejos), 1f to s.frente(lerp(muro, C.BARRO, 0.22f), lejos), startY = r.top, endY = r.bottom), r.topLeft, r.size)
        drawRect(s.lado(muro, lejos), Offset(if (s.solALaDerecha) r.right - 1.4f * k else r.left, r.top), Size(1.4f * k, r.height))
        if (k > 0.6f) { var y = r.top + 3f * k; while (y < r.bottom) { drawLine(s.sombra(C.BARRO, lejos).copy(alpha = 0.2f), Offset(r.left, y), Offset(r.right, y), 0.3f); y += 2.6f * k } }
        var bx = r.left + 2f * k; while (bx < r.right - 1f) { drawCircle(s.sombra(C.MADERA_OSC, lejos), 0.6f * k, Offset(bx, r.top + 2.4f * k)); bx += 4.4f * k }
        repeat(2) { val x = az.entre(r.left + 1f, r.right - 2f); drawRect(Brush.verticalGradient(0f to s.sombra(C.BARRO, lejos).copy(alpha = 0.4f), 1f to Color.Transparent, startY = r.top + 2f * k, endY = r.top + 9f * k), Offset(x, r.top + 2f * k), Size(az.entre(0.8f, 2f) * k, 7f * k)) }
        if (az.real() < 0.6f) drawPath(masa(az, Offset(az.entre(r.left + 3f, r.right - 3f), r.top + az.entre(4f, 8f) * k), 3.4f * k, 2f * k, 7, 0.4f), s.frente(if (encalada) C.ADOBE else lerp(C.BARRO, C.ADOBE, 0.4f), lejos).copy(alpha = 0.8f))
        // El pretil y la franja del terrado que se alcanza a ver.
        drawRect(s.arriba(lerp(muro, C.CALIZA, 0.3f), lejos), Offset(r.left - 0.4f, r.top - 1.2f * k), Size(r.width + 0.8f, 1.6f * k))
        drawRect(s.arriba(lerp(C.ADOBE, C.ARENA, 0.5f), lejos), Offset(r.left + 1f, r.top - 2.6f * k), Size(r.width - 2f, 1.5f * k))
        // Lo que hay en la azotea: cada casa, lo suyo.
        val piso = r.top - 2.2f * k; var x = r.left + az.entre(2f, 6f) * k
        while (x < r.right - 7f * k) {
            when (az.entero(7)) {
                0 -> { // El toldo de estera, sobre dos palos, y su sombra debajo.
                    val w = minOf(13f * k, r.right - x - 1f); drawRect(s.sombra(C.BARRO, lejos).copy(alpha = 0.3f), Offset(x, piso - 1f * k), Size(w, 1.4f * k))
                    for (d in listOf(0.6f * k, w - 0.6f * k)) drawLine(s.medio(C.MADERA, lejos), Offset(x + d, piso), Offset(x + d, piso - 8f * k), 0.7f * k)
                    drawPath(poligono(Offset(x - 1f * k, piso - 8f * k), Offset(x + w + 1f * k, piso - 8.8f * k), Offset(x + w + 1f * k, piso - 7.4f * k), Offset(x - 1f * k, piso - 6.6f * k)), s.arriba(C.ESTERA, lejos)); drawLine(s.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.5f), Offset(x - 1f * k, piso - 6.6f * k), Offset(x + w + 1f * k, piso - 7.4f * k), 0.4f)
                    x += w + 2f * k }
                1 -> { // La caseta de la escalera, con su hueco; de noche, la luz que sube de la casa.
                    val con = luces > 0f && hoy.real() < luces
                    drawRect(s.frente(lerp(C.ADOBE, C.CALIZA, 0.3f), lejos), Offset(x, piso - 6.4f * k), Size(7f * k, 6.4f * k)); drawRect(s.lado(C.ADOBE, lejos), Offset(if (s.solALaDerecha) x + 5.6f * k else x, piso - 6.4f * k), Size(1.4f * k, 6.4f * k)); drawRect(s.arriba(C.ENLUCIDO, lejos), Offset(x - 0.4f * k, piso - 7f * k), Size(7.8f * k, 0.9f * k))
                    drawRect(if (con) lerp(s.sombra(C.HOLLIN, lejos), C.LUMBRE, 0.85f) else s.sombra(C.HOLLIN, lejos), Offset(x + 2f * k, piso - 4.6f * k), Size(2.8f * k, 4.6f * k))
                    if (con) { resplandor(Offset(x + 3.4f * k, piso - 2.4f * k), 9f * k, 0.6f * l.noche, C.LUMBRE); mancha(Offset(x + 3.4f * k, piso + 0.4f * k), 8f * k, 1.4f * k, C.LUMBRE, 0.36f * l.noche) }
                    x += 9f * k }
                2 -> { // Unas tinajas.
                    for (j in 0..(1 + az.entero(2))) { val tinaja = Path().apply { addOval(Rect(x + j * 3.6f * k, piso - 5f * k, x + j * 3.6f * k + 3.2f * k, piso)) }; drawPath(tinaja, s.frente(if (j % 2 == 0) C.TERRACOTA else lerp(C.TERRACOTA, C.BARRO, 0.4f), lejos)); drawOval(s.luz(C.TERRACOTA, lejos).copy(alpha = 0.7f), Offset(x + j * 3.6f * k + (if (s.solALaDerecha) 1.6f else 0.4f) * k, piso - 4.4f * k), Size(1.2f * k, 3f * k)) }
                    x += 11f * k }
                3 -> { // El granero: una media naranja de barro.
                    val g = Path().apply { moveTo(x, piso); cubicTo(x, piso - 8f * k, x + 8f * k, piso - 8f * k, x + 8f * k, piso); close() }
                    drawPath(g, s.frente(C.ENLUCIDO, lejos)); clipPath(g) { drawRect(s.lado(C.ENLUCIDO, lejos), Offset(if (s.solALaDerecha) x + 5.4f * k else x, piso - 8f * k), Size(2.6f * k, 8f * k)) }; drawRect(s.sombra(C.MADERA_OSC, lejos), Offset(x + 3.2f * k, piso - 6.6f * k), Size(1.6f * k, 1.2f * k))
                    x += 10f * k }
                4 -> { // La ropa tendida: lino blanco en una cuerda entre dos cañas.
                    val w = minOf(12f * k, r.right - x - 1f); for (d in listOf(0f, w)) drawLine(s.medio(C.ESTERA, lejos), Offset(x + d, piso), Offset(x + d, piso - 7f * k), 0.5f * k); drawLine(s.sombra(C.MADERA_OSC, lejos), Offset(x, piso - 7f * k), Offset(x + w, piso - 7f * k), 0.3f)
                    if (hoy.real() < 0.7f && !EscenaEgipto.polvo(m)) for (j in 0..2) drawRect(s.frente(if (j == 1) lerp(C.LINO, C.NILO, 0.3f) else C.LINO, lejos), Offset(x + 1f * k + j * w * 0.3f, piso - 7f * k), Size(w * 0.22f, hoy.entre(3f, 5f) * k))
                    x += w + 2f * k }
                else -> x += az.entre(4f, 9f) * k
            }
        }
        if (fila == 3) { // En la casa de al lado, lo que se ve de cerca: una escalera de mano apoyada y unas esteras al sol.
            drawRect(s.arriba(C.ESTERA, lejos), Offset(r.left + r.width * 0.3f, r.top - 2.4f), Size(12f, 1.2f)) }
    }

    /** El borde de la hondonada, entre el valle y la aldea: arena, piedras y el camino por donde suben los burros con el agua. */
    fun DrawScope.borde(m: Momento, s: SolEgipto) {
        val az = Azar(Semilla.de("egipto", "borde2"))
        val arriba = listOf(Offset(184f, 157f), Offset(206f, 155f), Offset(228f, 156f), Offset(250f, 157.4f), Offset(266f, 160.6f), Offset(284f, 162.6f), Offset(304f, 166.4f), Offset(328f, 169f), Offset(352f, 173.6f), Offset(386f, 178f))
        val forma = Path().apply { addPath(curva(arriba)); lineTo(386f, 206f); lineTo(184f, 206f); close() }
        drawPath(forma, Brush.verticalGradient(0f to s.arriba(lerp(C.ARENA, C.CALIZA, 0.3f), 0.2f), 1f to s.arriba(lerp(C.ARENA, C.ROCA, 0.25f), 0.04f), startY = 155f, endY = 204f))
        clipPath(forma) {
            repeat(40) { val q = Offset(az.entre(186f, ANCHO), az.entre(157f, 204f)); val k = 0.5f + (q.y - 150f) / 50f; mancha(q, az.entre(6f, 18f) * k, az.entre(1f, 2.4f) * k, if (it % 3 == 0) s.luz(C.CALIZA, 0.1f) else s.sombra(C.ROCA, 0.1f), 0.24f) }
            repeat(26) { val q = Offset(az.entre(250f, ANCHO), az.entre(160f, 204f)); val k = 0.5f + (q.y - 150f) / 44f; drawOval(s.sombra(C.ROCA_OSC, 0.08f), Offset(q.x, q.y), Size(3.4f * k, 1.7f * k)); drawOval(s.luz(C.CALIZA, 0.08f), Offset(q.x + (if (s.solALaDerecha) 0.9f * k else 0f), q.y - 0.4f * k), Size(2.4f * k, 1.2f * k)) }
            // El camino del río: baja de la puerta del muro, tuerce entre las piedras y se pierde hacia los templos.
            val camino = listOf(Offset(204f, 166f), Offset(232f, 163f), Offset(258f, 168f), Offset(284f, 170f), Offset(312f, 176f), Offset(336f, 178f), Offset(356f, 184f))
            drawPath(curva(camino), s.luz(C.LINO, 0.1f).copy(alpha = 0.6f), style = Stroke(1.6f, cap = StrokeCap.Round)); drawPath(curva(camino.map { it + Offset(0f, 1f) }), s.sombra(C.ROCA_OSC, 0.1f).copy(alpha = 0.3f), style = Stroke(0.5f))
            if (s.l.noche > 0.4f && s.l.nublado < 0.6f) mancha(Offset(s.l.lunaX * ANCHO, 170f), 80f, 18f, C.LUNA, 0.1f * s.l.noche)
        }
    }
}
