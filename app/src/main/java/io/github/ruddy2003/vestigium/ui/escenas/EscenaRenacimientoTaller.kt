package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Estancia
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Lado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerpo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.SolQueEntra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.haloDeBruma
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.guirnalda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hazDeSol
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.manchaDeSol
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoH
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoV
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * La sala del taller: su caja en perspectiva (la [Estancia] de la biblioteca), el techo de vigas, las paredes, el
 * suelo de barro cocido y las dos ventanas. La grande, en la pared del fondo, da a la ciudad; la de la calle, en la
 * pared izquierda, es por donde entra el sol.
 *
 * **[Diseño propio]**: una sala de taller con techo de vigas y travesaños, paredes enlucidas y suelo de ladrillo es lo
 * corriente en una casa florentina de la época, pero esta sala no copia ninguna.
 */
internal object TallerRenacimiento {
    private val C = ColorRenacimiento
    private val R get() = EscenaRenacimiento

    /** La sala: los ojos de quien mira, de pie, quedan al 44 % del alto del cuadro. */
    val E = Estancia(fuga = Offset(180f, 124f), ojo = 300f, x0 = 76f, x1 = 312f, techo = 30f, suelo = 184f, cerca = 190f)
    const val SUELO = 184f

    // ---- La ventana grande, en la pared del fondo. El muro tiene [GRUESO] de fondo.
    const val VX0 = 134f
    const val VX1 = 264f
    const val VY0 = 52f
    const val VY1 = 148f
    private const val GRUESO = 22f

    /** Lo que mide cada hoja de la contraventana grande. */
    private const val HOJA = 65f

    // ---- La ventana de la calle, en la pared izquierda: de LT0 a LT1 de hondo, del dintel al alféizar.
    private const val LT0 = 22f
    private const val LT1 = 92f
    private const val LY0 = 66f
    private const val LY1 = 138f
    private const val GRUESO_CALLE = 14f

    /** A cuánto queda la casa de enfrente del muro de la ventana de la calle. */
    private const val CALLE = 44f

    /** Desde dónde alumbra el cielo de cada ventana. */
    val LUZ_GRANDE = Offset(199f, 104f)
    val LUZ_CALLE = E.p(76f, 104f, 56f)

    /** El hueco de la ventana grande por fuera del muro: por ahí se ve la ciudad. */
    val FUERA: Rect = E.s(-GRUESO).let { k -> Rect(180f + (VX0 - 180f) * k, 124f + (VY0 - 124f) * k, 180f + (VX1 - 180f) * k, 124f + (VY1 - 124f) * k) }

    private fun huecoCalle(hondo: Float): Path = poligono(E.p(76f - hondo, LY0, LT0), E.p(76f - hondo, LY0, LT1), E.p(76f - hondo, LY1, LT1), E.p(76f - hondo, LY1, LT0))

    /** Los cuatro claros de la ventana de la calle, entre los palos de su cruz: cada uno, sus esquinas (hondo, alto). */
    internal fun clarosDeLaCalle(): List<List<Pair<Float, Float>>> = buildList {
        val ts = listOf(LT0, (LT0 + LT1) / 2f, LT1); val ys = listOf(LY0, 98f, LY1); val r = 1.4f
        for (i in 0..1) for (j in 0..1) add(listOf(ts[i] + r to ys[j] + r, ts[i + 1] - r to ys[j] + r, ts[i + 1] - r to ys[j + 1] - r, ts[i] + r to ys[j + 1] - r))
    }

    private fun bocaDeLaCalle(): List<Pair<Float, Float>> = listOf(LT0 to LY1, LT0 to LY0, LT1 to LY0, LT1 to LY1)

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val l = R.luces(m); val f = R.focos(m); val a = Alumbrado(l, f); val ext = m.luces(); val sol = R.solQueEntra(m)
        drawRect(l.tono(C.VIGA), Offset.Zero, Size(ANCHO, ALTO))
        paredes(m, a)
        techo(a)
        enLosMuros(m, a)
        suelo(m, a)
        ventanaGrande(m, a, ext)
        ventanaDeLaCalle(m, a, ext)
        with(ObraRenacimiento) { enLaPared(m, a) }
        rincones(l)
        if (sol != null) {
            // En un suelo de ladrillo la mancha se nota menos que en uno claro: se pinta con algo más de fuerza.
            val fuerte = SolQueEntra(sol.e, sol.a, (sol.fuerza * 2.3f).coerceAtMost(2.3f), sol.color, sol.deLuna)
            // La mancha no cae donde da la sombra de las cosas: la mesa, los caballetes y lo demás la cortan.
            with(ObraRenacimiento) { sinLasSombras(m, a, R.rayoDelSol(sol)) { manchaDeSol(E, clarosDeLaCalle(), fuerte, Lado.IZQUIERDA, 0.5f) } }
            hazDeSol(E, bocaDeLaCalle(), sol, Azar(Semilla.de("renacimiento", "polvo")), Lado.IZQUIERDA, 40)
            // La claridad que la mancha devuelve a lo que tiene alrededor.
            val centro = R.centroDeLaMancha(sol); mancha(centro, 50f, 22f, sol.color, 0.12f * sol.fuerza)
        }
        // Lo que cada luz encendida alumbra: la pared y el suelo de junto a la lámpara, y el suelo de alrededor del hornillo.
        for (q in f) if (q.p == ObraRenacimiento.LUZ_LAMPARA) {
            charcoDeLuz(q, Offset(100f, 150f), 44f, 40f, C.MURO, 0.7f)
            clipPath(E.formaDelSuelo()) { charcoDeLuz(q, Offset(130f, 252f), 96f, 22f, C.COTTO, 0.8f) }
        } else if (q.p == ObraRenacimiento.LUZ_LUMBRE) clipPath(E.formaDelSuelo()) { charcoDeLuz(q, Offset(270f, 254f), 60f, 16f, C.COTTO, 0.9f) }
        with(ObraRenacimiento) { cosasDelTaller(m, a, f) }
        adornos(m, l, a)
        // El aire de la sala: el halo de cada luz encendida y la penumbra de los bordes.
        for (q in f.drop(2)) if (q.fuerza > 0.3f && (q.p == ObraRenacimiento.LUZ_LAMPARA || q.p == ObraRenacimiento.LUZ_LUMBRE)) { mancha(q.p, q.alcance * 0.6f, q.alcance * 0.46f, q.color, 0.1f * q.fuerza.coerceAtMost(1.2f)); haloDeBruma(q.p, q.alcance * 0.3f, q.alcance * 0.26f, q.color, 0.5f * q.fuerza.coerceAtMost(1.2f)) }
        drawRect(Brush.radialGradient(0f to Color.Transparent, 0.58f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.26f), center = Offset(186f, 146f), radius = 292f), Offset.Zero, Size(ANCHO, ALTO), blendMode = BlendMode.Multiply)
        grano(Azar(Semilla.de("renacimiento", "grano")), 0.55f + 0.35f * (1f - l.noche))
    }

    // ---------------------------------------------------------------- La caja

    /** El enlucido de las tres paredes, con su zócalo, sus manchas y sus desconchones. */
    private fun DrawScope.paredes(m: Momento, a: Alumbrado) {
        planoH(a, E.formaDelFondo(), E.x0, E.x1, 104f, C.MURO)
        planoH(a, E.formaIzquierda(), 0f, E.x0, 110f, lerp(C.MURO, C.VIGA, 0.1f), mira = 1f)
        planoH(a, E.formaDerecha(), E.x1, ANCHO, 110f, lerp(C.MURO, C.VIGA, 0.06f), mira = -1f)
        val az = Azar(Semilla.de("renacimiento", "muro"))
        // El zócalo, que de tanto barrer y arrimar tablas está más oscuro y rozado.
        val zocalo = lerp(C.MURO, C.COTTO, 0.36f)
        planoH(a, poligono(Offset(E.x0, 166f), Offset(E.x1, 166f), Offset(E.x1, SUELO), Offset(E.x0, SUELO)), E.x0, E.x1, 175f, zocalo)
        drawPath(poligono(E.p(E.x0, 166f), E.p(E.x0, SUELO), E.p(E.x0, SUELO, E.cerca), E.p(E.x0, 166f, E.cerca)), a.en(E.p(E.x0, 175f, 60f), lerp(zocalo, C.VIGA, 0.12f), 1f))
        drawPath(poligono(E.p(E.x1, 166f), E.p(E.x1, SUELO), E.p(E.x1, SUELO, E.cerca), E.p(E.x1, 166f, E.cerca)), a.en(E.p(E.x1, 175f, 60f), lerp(zocalo, C.VIGA, 0.1f), -1f))
        drawLine(a.en(Offset(194f, 166f), lerp(C.MURO, C.VIGA, 0.4f)).copy(alpha = 0.6f), Offset(E.x0, 166f), Offset(E.x1, 166f), 0.7f)
        clipPath(E.formaDelFondo()) {
            // Manchas de humedad que bajan del techo, cercos de humo sobre el hornillo y roces.
            repeat(12) { val o = Offset(az.entre(E.x0, E.x1), az.entre(32f, 150f)); mancha(o, az.entre(8f, 22f), az.entre(10f, 26f), a.en(o, lerp(C.MURO, C.VIGA, 0.5f)), 0.13f) }
            repeat(7) { val o = Offset(az.entre(E.x0, E.x1), az.entre(30f, 60f)); mancha(o, az.entre(5f, 10f), az.entre(14f, 30f), a.en(o, C.VIGA), 0.1f) }
            // Desconchones: el enlucido saltado deja ver el ladrillo.
            for ((x, y, w) in listOf(Triple(92f, 44f, 9f), Triple(300f, 152f, 12f), Triple(118f, 160f, 8f), Triple(280f, 40f, 7f))) {
                val forma = Path().apply { moveTo(x, y); lineTo(x + w * 0.6f, y - 2f); lineTo(x + w, y + 1.5f); lineTo(x + w * 0.8f, y + 5f); lineTo(x + w * 0.2f, y + 4.4f); close() }
                drawPath(forma, a.en(Offset(x, y), lerp(C.COTTO, C.MURO, 0.3f))); drawPath(forma, a.en(Offset(x, y), C.YESO).copy(alpha = 0.6f), style = Stroke(0.5f))
            }
            // Grietas finas.
            for ((x0, y0) in listOf(108f to 30f, 290f to 30f, 250f to 150f)) { var p = Offset(x0, y0); repeat(6) { val q = p + Offset(az.entre(-3f, 3f), az.entre(4f, 8f)); drawLine(a.en(p, C.VIGA).copy(alpha = 0.35f), p, q, 0.4f); p = q } }
            // Donde los aprendices limpian el pincel: toques de color en la pared, junto a su rincón.
            val toques = listOf(C.ULTRAMAR, C.CINABRIO, C.OCRE, C.VERDE, C.YESO)
            repeat(16) { val o = Offset(az.entre(80f, 108f), az.entre(150f, 166f)); drawLine(a.en(o, toques[it % 5]).copy(alpha = 0.7f), o, o + Offset(az.entre(1.5f, 4f), az.entre(-1.2f, 1.2f)), az.entre(0.6f, 1.3f), StrokeCap.Round) }
        }
        clipPath(E.formaIzquierda()) {
            repeat(8) { val o = E.p(E.x0, az.entre(40f, 180f), az.entre(4f, 124f)); mancha(o, az.entre(5f, 12f), az.entre(8f, 22f), a.en(o, lerp(C.MURO, C.VIGA, 0.5f), 1f), 0.16f) }
        }
        clipPath(E.formaDerecha()) {
            repeat(6) { val o = E.p(E.x1, az.entre(40f, 180f), az.entre(4f, 100f)); mancha(o, az.entre(5f, 12f), az.entre(8f, 22f), a.en(o, lerp(C.MURO, C.VIGA, 0.5f), -1f), 0.16f) }
        }
    }

    /**
     * Lo que los años han dejado en los muros: desconchones grandes que enseñan el ladrillo, el hollín del hornillo en
     * el techo, salpicaduras, clavos y cuerdas; y lo que cuelga de ellos: los yesos, el delantal, las brochas.
     */
    private fun DrawScope.enLosMuros(m: Momento, a: Alumbrado) {
        val az = Azar(Semilla.de("renacimiento", "muros")); val ladrillo = lerp(C.COTTO, C.MURO, 0.25f)
        fun desconchon(forma: Path, en: Offset, mira: Float) {
            drawPath(forma, a.en(en, ladrillo, mira))
            clipPath(forma) { val b = forma.getBounds(); var y = b.top + 1.4f; var k = 0; while (y < b.bottom) { drawLine(a.en(en, lerp(C.MURO, C.YESO, 0.3f), mira).copy(alpha = 0.7f), Offset(b.left, y), Offset(b.right, y), 0.4f); var x = b.left + (if (k % 2 == 0) 2f else 5f); while (x < b.right) { drawLine(a.en(en, lerp(C.MURO, C.YESO, 0.3f), mira).copy(alpha = 0.7f), Offset(x, y - 2.6f), Offset(x, y), 0.4f); x += 6f }; y += 2.6f; k++ } }
            drawPath(forma, a.en(en, C.YESO, mira).copy(alpha = 0.7f), style = Stroke(0.6f)); drawPath(forma, C.SOMBRA.copy(alpha = 0.3f), style = Stroke(0.4f))
        }
        // En la pared del fondo, sobre la ventana y junto al rincón.
        desconchon(Path().apply { moveTo(144f, 36f); lineTo(156f, 34f); lineTo(166f, 38f); lineTo(163f, 44f); lineTo(152f, 46f); lineTo(142f, 42f); close() }, Offset(154f, 40f), 0f)
        desconchon(Path().apply { moveTo(236f, 33f); lineTo(252f, 35f); lineTo(256f, 42f); lineTo(246f, 46f); lineTo(238f, 41f); close() }, Offset(246f, 40f), 0f)
        desconchon(Path().apply { moveTo(78f, 118f); lineTo(84f, 116f); lineTo(88f, 124f); lineTo(86f, 136f); lineTo(79f, 138f); close() }, Offset(82f, 126f), 0f)
        // En la pared de la calle, bajo la ventana, y en la de la derecha.
        desconchon(poligono(listOf(E.p(E.x0, 150f, 20f), E.p(E.x0, 147f, 34f), E.p(E.x0, 156f, 40f), E.p(E.x0, 168f, 34f), E.p(E.x0, 166f, 20f))), E.p(E.x0, 156f, 28f), 1f)
        desconchon(poligono(listOf(E.p(E.x1, 150f, 78f), E.p(E.x1, 146f, 92f), E.p(E.x1, 160f, 98f), E.p(E.x1, 168f, 84f))), E.p(E.x1, 156f, 88f), -1f)
        // El hollín del hornillo, que ha ido tiznando las vigas y el techo de encima; y el de la lámpara, en la pared.
        clipPath(E.formaDelTecho()) { for (k in 0..3) mancha(Offset(278f + k * 9f, 10f + k * 2f), 34f - k * 5f, 9f, a.en(Offset(290f, 12f), C.CARBON), 0.2f) }
        mancha(Offset(97f, 150f), 5f, 12f, a.en(Offset(97f, 150f), C.CARBON), 0.12f)
        // Encima del estante, colgados de sus clavos: un medallón de yeso con un perfil, un pie de yeso y la escuadra.
        val yeso = lerp(C.YESO, C.MURO, 0.12f)
        for ((x, y) in listOf(92f to 41f, 108f to 40f, 121f to 40f)) { drawCircle(a.en(Offset(x, y), C.CARBON), 0.6f, Offset(x, y)); drawLine(a.en(Offset(x, y), lerp(C.VIGA, C.MADERA, 0.3f)), Offset(x, y), Offset(x, y + 3f), 0.4f) }
        drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(86.6f, 44.6f), Size(12f, 13f), blendMode = BlendMode.Multiply)
        cuerpo(a, Path().apply { addOval(Rect(86f, 44f, 98f, 57f)) }, 86f, 98f, 50f, yeso); drawOval(a.en(Offset(92f, 50f), lerp(yeso, C.PIEDRA, 0.4f)), Offset(87.4f, 45.4f), Size(9.2f, 10.2f), style = Stroke(0.6f))
        drawPath(Path().apply { moveTo(90.6f, 47.4f); cubicTo(93.6f, 46.6f, 95f, 48.6f, 94f, 50.4f); lineTo(95.4f, 51.6f); lineTo(94f, 52f); cubicTo(94.4f, 54f, 92.6f, 55f, 90.6f, 54.6f); close() }, a.en(Offset(92f, 50f), lerp(yeso, C.PIEDRA, 0.35f)))
        drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(104.6f, 44f), Size(8f, 12f), blendMode = BlendMode.Multiply)
        cuerpo(a, Path().apply { moveTo(106f, 43f); lineTo(110f, 43f); lineTo(110.6f, 52f); cubicTo(113f, 53f, 115.6f, 54.4f, 115f, 56f); lineTo(105f, 56f); cubicTo(104.4f, 52f, 105.4f, 47f, 106f, 43f); close() }, 104.6f, 115.4f, 50f, yeso)
        drawPath(poligono(listOf(Offset(118f, 43f), Offset(119.6f, 43f), Offset(119.6f, 54.4f), Offset(127f, 54.4f), Offset(127f, 56f), Offset(118f, 56f))), a.en(Offset(122f, 50f), lerp(C.MADERA, C.YESO, 0.2f)))
        // Sobre el dintel de la ventana, que hace de repisa: un busto de yeso, dos vasijas y unos rollos tumbados.
        drawRect(C.SOMBRA.copy(alpha = 0.24f), Offset(170f, 40.6f), Size(62f, 3.4f), blendMode = BlendMode.Multiply)
        val busto = Path().apply { moveTo(196f, 44f); lineTo(196.6f, 41.4f); cubicTo(194f, 40.6f, 194f, 36f, 198f, 35.6f); cubicTo(202f, 36f, 202f, 40.6f, 199.4f, 41.4f); lineTo(200f, 44f); lineTo(203f, 44f); lineTo(193f, 44f); close() }
        cuerpo(a, busto, 193f, 203f, 40f, yeso)
        for ((k, x) in listOf(176f, 184f, 214f).withIndex()) { val h = 5.4f + k * 0.8f; cuerpo(a, Path().apply { moveTo(x - 2f, 44f); cubicTo(x - 3.6f, 44f - h * 0.5f, x - 2.4f, 44f - h, x - 1.2f, 44f - h); lineTo(x + 1.2f, 44f - h); cubicTo(x + 2.4f, 44f - h, x + 3.6f, 44f - h * 0.5f, x + 2f, 44f); close() }, x - 3f, x + 3f, 41f, if (k == 1) lerp(C.COTTO, C.VERDE, 0.5f) else C.COTTO) }
        for (k in 0..1) { drawLine(a.en(Offset(226f, 42f), C.PAPEL), Offset(220f, 42.6f - k * 2f), Offset(236f, 42.2f - k * 2f), 2f, StrokeCap.Round); drawCircle(a.en(Offset(236f, 42f), lerp(C.PAPEL, C.VIGA, 0.4f)), 0.9f, Offset(236f, 42.2f - k * 2f)) }
        // En la pared de la calle, bajo la ventana: el delantal de moler, colgado de un clavo, con sus manchas.
        val clavo = E.p(E.x0, 146f, 52f)
        val delantal = poligono(listOf(E.p(E.x0, 150f, 44f), E.p(E.x0, 150f, 62f), E.p(E.x0, 178f, 66f), E.p(E.x0, 180f, 40f)))
        drawPath(Path().apply { addPath(delantal, Offset(1.6f, 1.4f)) }, C.SOMBRA.copy(alpha = 0.32f), blendMode = BlendMode.Multiply)
        drawLine(a.en(clavo, lerp(C.VIGA, C.MADERA, 0.3f), 1f), clavo, E.p(E.x0, 150f, 46f), 0.5f); drawLine(a.en(clavo, lerp(C.VIGA, C.MADERA, 0.3f), 1f), clavo, E.p(E.x0, 150f, 60f), 0.5f); drawCircle(a.en(clavo, C.CARBON), 0.7f, clavo)
        drawPath(delantal, a.en(E.p(E.x0, 164f, 52f), lerp(C.YESO, C.OCRE, 0.3f), 1f))
        clipPath(delantal) {
            for (k in 1..3) drawLine(a.en(E.p(E.x0, 164f, 52f), lerp(C.YESO, C.VIGA, 0.4f), 1f).copy(alpha = 0.5f), E.p(E.x0, 150f, 44f + k * 4.6f), E.p(E.x0, 180f, 40f + k * 6.6f), 0.5f)
            val toques = listOf(C.ULTRAMAR, C.CINABRIO, C.OCRE, C.VERDE, C.CARBON)
            repeat(12) { val o = E.p(E.x0, az.entre(154f, 178f), az.entre(42f, 64f)); drawOval(a.en(o, toques[it % 5], 1f).copy(alpha = 0.7f), o, Size(az.entre(1.4f, 3.6f), az.entre(1.2f, 3f))) }
        }
        // Salpicaduras de color y de yeso en el zócalo de esa pared, junto a la mesa de moler.
        val toques = listOf(C.ULTRAMAR, C.CINABRIO, C.OCRE, C.YESO)
        repeat(16) { val o = E.p(E.x0, az.entre(170f, 183.6f), az.entre(60f, 122f)); drawOval(a.en(o, toques[it % 4], 1f).copy(alpha = 0.42f), o, Size(az.entre(0.8f, 2.6f), az.entre(0.6f, 1.6f))) }
        // En la pared de la derecha, lo que asoma tras el caballete: una mascarilla de yeso y un manojo de brochas.
        val mk = E.p(E.x1, 70f, 88f)
        drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(mk.x - 5f, mk.y - 6f), Size(11f, 15f), blendMode = BlendMode.Multiply)
        cuerpo(a, Path().apply { moveTo(mk.x - 5f, mk.y - 3f); cubicTo(mk.x - 5f, mk.y - 9f, mk.x + 5f, mk.y - 9f, mk.x + 5f, mk.y - 3f); cubicTo(mk.x + 5f, mk.y + 4f, mk.x + 2f, mk.y + 8f, mk.x, mk.y + 8f); cubicTo(mk.x - 2f, mk.y + 8f, mk.x - 5f, mk.y + 4f, mk.x - 5f, mk.y - 3f); close() }, mk.x - 5f, mk.x + 5f, mk.y, yeso)
        for (dx in listOf(-2f, 2f)) drawOval(a.en(mk, lerp(yeso, C.VIGA, 0.5f)), Offset(mk.x + dx - 1.2f, mk.y - 3f), Size(2.4f, 1.3f)); drawLine(a.en(mk, lerp(yeso, C.VIGA, 0.4f)), mk + Offset(0f, -1f), mk + Offset(0.4f, 3f), 0.6f); drawLine(a.en(mk, lerp(yeso, C.VIGA, 0.5f)), mk + Offset(-1.6f, 5f), mk + Offset(1.6f, 5f), 0.6f)
        val br = E.p(E.x1, 104f, 96f)
        drawLine(a.en(br, lerp(C.VIGA, C.MADERA, 0.3f)), br, br + Offset(0f, 6f), 0.5f); drawCircle(a.en(br, C.CARBON), 0.7f, br)
        for ((k, dx) in listOf(-3f, -1f, 1f, 3f).withIndex()) { drawLine(a.en(br, lerp(C.MADERA, C.YESO, 0.2f), -1f), br + Offset(dx * 0.3f, 6f), br + Offset(dx, 20f + k % 2 * 2f), 0.9f); drawLine(a.en(br, if (k % 2 == 0) lerp(C.OCRE, C.YESO, 0.3f) else lerp(C.VIGA, C.CARBON, 0.3f), -1f), br + Offset(dx, 20f + k % 2 * 2f), br + Offset(dx * 1.1f, 25f + k % 2 * 2f), 1.8f) }
    }

    /** El techo: seis vigas que vienen del fondo hacia quien mira y, entre ellas, los travesaños y el ladrillo. */
    private fun DrawScope.techo(a: Alumbrado) {
        val tabla = lerp(C.VIGA, C.COTTO, 0.34f)
        planoH(a, E.formaDelTecho(), 0f, ANCHO, 14f, tabla)
        clipPath(E.formaDelTecho()) {
            // Los travesaños, de pared a pared.
            var t = 6f
            while (t < 90f) { val y = E.p(180f, E.techo, t).y; drawLine(a.en(Offset(180f, y), lerp(C.VIGA, C.CARBON, 0.3f)), E.p(E.x0, E.techo, t), E.p(E.x1, E.techo, t), 1.1f * E.s(t)); drawLine(a.en(Offset(180f, y), lerp(tabla, C.MURO, 0.25f)).copy(alpha = 0.5f), E.p(E.x0, E.techo, t + 1.6f), E.p(E.x1, E.techo, t + 1.6f), 0.4f); t += 9f }
        }
        // Las vigas: la cara de abajo y la que mira a quien mira el cuadro.
        val canto = 8f; val ancho = 4.6f
        for (i in 0..5) {
            val x = E.x0 + i * E.anchura / 5f; val hacia = if (x < E.fuga.x) 1f else -1f
            val abajo = poligono(E.p(x - ancho, E.techo + canto), E.p(x + ancho, E.techo + canto), E.p(x + ancho, E.techo + canto, E.cerca), E.p(x - ancho, E.techo + canto, E.cerca))
            val lado = poligono(E.p(x + ancho * hacia, E.techo), E.p(x + ancho * hacia, E.techo + canto), E.p(x + ancho * hacia, E.techo + canto, E.cerca), E.p(x + ancho * hacia, E.techo, E.cerca))
            val o = E.p(x, E.techo + canto, 30f)
            drawPath(lado, a.en(o, lerp(C.VIGA, C.MADERA, 0.25f), hacia)); drawPath(abajo, a.en(o, lerp(C.VIGA, C.CARBON, 0.25f)))
            drawLine(a.en(o, lerp(C.MADERA, C.YESO, 0.2f)).copy(alpha = 0.55f), E.p(x + ancho * hacia, E.techo + canto), E.p(x + ancho * hacia, E.techo + canto, E.cerca), 0.6f)
            // La cabeza de la viga, empotrada en la pared del fondo.
            drawRect(a.en(E.p(x, E.techo + 4f), lerp(C.VIGA, C.CARBON, 0.4f)), Offset(x - ancho, E.techo), Size(ancho * 2f, canto))
        }
    }

    /** El suelo de ladrillo, a tresbolillo: cada ladrillo de su tono, gastado por donde se pasa y con el polvo del oficio. */
    private fun DrawScope.suelo(m: Momento, a: Alumbrado) {
        val az = Azar(Semilla.de("renacimiento", "suelo")); val n = 12; val w = E.anchura / n; val hondo = w * 0.62f
        clipPath(E.formaDelSuelo()) {
            planoV(a, E.formaDelSuelo(), 180f, SUELO, ALTO, lerp(C.COTTO, C.CARBON, 0.12f))
            var j = 0; var t0 = 0f
            while (t0 < E.cerca) {
                val corre = if (j % 2 == 0) 0f else w / 2f
                for (i in -1..n) {
                    val x0 = E.x0 + i * w + corre
                    val base = lerp(C.COTTO, if ((i + j) % 3 == 0) C.MURO else C.CARBON, az.entre(0.02f, 0.22f))
                    val o = E.p(x0 + w / 2f, SUELO, t0 + hondo / 2f)
                    drawPath(poligono(E.p(x0 + 0.5f, SUELO, t0 + 0.4f), E.p(x0 + w - 0.5f, SUELO, t0 + 0.4f), E.p(x0 + w - 0.5f, SUELO, t0 + hondo - 0.4f), E.p(x0 + 0.5f, SUELO, t0 + hondo - 0.4f)), a.en(o, base, 0f, 0.4f * (1f - t0 / 200f)))
                }
                t0 += hondo; j++
            }
            // Las juntas de mortero, que se ven más de cerca, y algún ladrillo roto o repuesto.
            for (k in 0..7) { val x = E.x0 + (k * 2 - 1) * w; drawLine(a.en(E.p(x, SUELO, 150f), lerp(C.MURO, C.VIGA, 0.4f)).copy(alpha = 0.3f), E.p(x, SUELO, 110f), E.p(x, SUELO, 190f), 0.6f) }
            repeat(9) { val x0 = E.x0 + az.entero(n) * w; val t0 = az.entre(60f, 180f); val o = E.p(x0 + w / 2f, SUELO, t0)
                if (it % 3 == 0) drawPath(poligono(E.p(x0 + 1f, SUELO, t0), E.p(x0 + w * 0.6f, SUELO, t0), E.p(x0 + w * 0.3f, SUELO, t0 + hondo * 0.8f)), a.en(o, lerp(C.COTTO, C.CARBON, 0.45f)).copy(alpha = 0.6f))
                else drawPath(poligono(E.p(x0 + 0.6f, SUELO, t0), E.p(x0 + w - 0.6f, SUELO, t0), E.p(x0 + w - 0.6f, SUELO, t0 + hondo - 0.6f), E.p(x0 + 0.6f, SUELO, t0 + hondo - 0.6f)), a.en(o, lerp(C.COTTO, if (it % 2 == 0) C.YESO else C.OCRE, 0.34f)).copy(alpha = 0.7f)) }
            // Años de oficio en el suelo: el cerco blanco del yeso bajo el cubo, el polvo de color junto a la mesa de moler y las pisadas que lo reparten.
            mancha(Offset(246f, 214f), 22f, 6f, a.en(Offset(246f, 214f), C.YESO), 0.4f)
            for ((x, y, c) in listOf(Triple(150f, 250f, C.OCRE), Triple(120f, 254f, C.ULTRAMAR), Triple(186f, 252f, C.CINABRIO), Triple(96f, 250f, C.VERDE))) mancha(Offset(x, y), 16f, 4.6f, a.en(Offset(x, y), c), 0.22f)
            for (k in 0..6) { val o = Offset(150f + k * 16f + az.entre(-3f, 3f), 256f + (k % 2) * 6f + k * 1.4f); drawOval(a.en(o, lerp(C.YESO, C.OCRE, 0.3f)).copy(alpha = 0.26f), o, Size(7f, 2.6f)) }
            // El paso de años: una senda más clara de la puerta a la ventana, y el desgaste alrededor de la mesa.
            for (k in 0..6) { val t = 16f + k * 26f; val o = E.p(196f + az.entre(-10f, 10f), SUELO, t); mancha(o, 26f * E.s(t), 5.5f * E.s(t), a.en(o, C.MURO), 0.12f) }
            // El polvo de yeso y de colores que barren cada mañana, y lo que gotea de los caballetes.
            repeat(90) { val o = E.p(az.entre(E.x0 + 4f, E.x1 - 4f), SUELO, az.entre(2f, 186f)); drawCircle(a.en(o, C.YESO).copy(alpha = az.entre(0.2f, 0.55f)), az.entre(0.4f, 1.2f), o) }
            val gotas = listOf(C.ULTRAMAR, C.CINABRIO, C.OCRE, C.VERDE)
            repeat(26) { val o = Offset(az.entre(268f, 352f), az.entre(196f, 214f)); drawOval(a.en(o, gotas[it % 4]).copy(alpha = 0.55f), o, Size(az.entre(1.2f, 3.2f), az.entre(0.6f, 1.2f))) }
            repeat(14) { val o = Offset(az.entre(60f, 124f), az.entre(222f, 236f)); drawOval(a.en(o, gotas[it % 4]).copy(alpha = 0.5f), o, Size(az.entre(1.2f, 3f), az.entre(0.6f, 1.2f))) }
            // Lo que el suelo devuelve de la ventana grande.
            mancha(E.p(199f, SUELO, 30f), 70f, 13f, a.focos[0].color, 0.12f * a.focos[0].fuerza)
        }
        drawLine(a.en(Offset(194f, SUELO), lerp(C.VIGA, C.CARBON, 0.4f)).copy(alpha = 0.55f), Offset(E.x0, SUELO), Offset(E.x1, SUELO), 0.9f)
    }

    /** La penumbra de los rincones, que es lo que da hondura a la caja. */
    private fun DrawScope.rincones(l: Luces) {
        val c = C.SOMBRA.copy(alpha = 0.28f)
        drawRect(Brush.horizontalGradient(0f to c, 1f to Color.Transparent, startX = E.x0, endX = E.x0 + 18f), Offset(E.x0, E.techo), Size(18f, SUELO - E.techo), blendMode = BlendMode.Multiply)
        drawRect(Brush.horizontalGradient(0f to Color.Transparent, 1f to c, startX = E.x1 - 18f, endX = E.x1), Offset(E.x1 - 18f, E.techo), Size(18f, SUELO - E.techo), blendMode = BlendMode.Multiply)
        drawRect(Brush.verticalGradient(0f to c, 1f to Color.Transparent, startY = E.techo, endY = E.techo + 12f), Offset(E.x0, E.techo), Size(E.anchura, 12f), blendMode = BlendMode.Multiply)
        clipPath(E.formaDelSuelo()) { drawRect(Brush.verticalGradient(0f to c, 1f to Color.Transparent, startY = SUELO, endY = SUELO + 13f), Offset(E.x0 - 80f, SUELO), Size(E.anchura + 160f, 13f), blendMode = BlendMode.Multiply) }
        if (l.noche > 0.3f) drawRect(Brush.verticalGradient(0f to c.copy(alpha = 0.3f * l.noche), 1f to Color.Transparent, startY = 0f, endY = 60f), Offset.Zero, Size(ANCHO, 60f), blendMode = BlendMode.Multiply)
    }

    // ---------------------------------------------------------------- La ventana grande

    private fun DrawScope.ventanaGrande(m: Momento, a: Alumbrado, ext: Luces) {
        val dia = (1f - ext.noche).coerceIn(0f, 1f); val claro = lerp(ext.medio, Color.White, 0.5f)
        // Los derrames del muro: de día son lo más claro de la pared.
        val derrame = lerp(a.en(LUZ_GRANDE, C.MURO), claro, 0.36f * dia)
        drawRect(derrame, Offset(VX0, VY0), Size(VX1 - VX0, VY1 - VY0))
        // Las jambas, gruesas: la que mira al sol, clara; la otra, en sombra violeta. De noche, la que mira a la luna.
        val r = Rayo(ext); val alSol = if (r.lat > 0f) -1f else 1f
        val clara = lerp(a.en(LUZ_GRANDE, C.MURO), claro, 0.6f * dia + 0.16f * ext.noche); val oscura = lerp(a.en(LUZ_GRANDE, lerp(C.MURO, C.PIEDRA, 0.3f)), C.SOMBRA, 0.34f)
        drawPath(poligono(Offset(VX0, VY0), FUERA.topLeft, FUERA.bottomLeft, Offset(VX0, VY1)), if (alSol < 0f) clara else oscura)
        drawPath(poligono(Offset(VX1, VY0), FUERA.topRight, FUERA.bottomRight, Offset(VX1, VY1)), if (alSol > 0f) clara else oscura)
        for (k in 1..4) { val y = VY0 + (VY1 - VY0) * k / 5f; val yf = FUERA.top + FUERA.height * k / 5f; drawLine(a.en(LUZ_GRANDE, C.PIEDRA).copy(alpha = 0.3f), Offset(VX0, y), Offset(FUERA.left, yf), 0.4f); drawLine(a.en(LUZ_GRANDE, C.PIEDRA).copy(alpha = 0.3f), Offset(VX1, y), Offset(FUERA.right, yf), 0.4f) }
        drawPath(poligono(Offset(VX0, VY0), Offset(VX1, VY0), FUERA.topRight, FUERA.topLeft), lerp(derrame, a.en(LUZ_GRANDE, C.VIGA), 0.5f))
        drawPath(poligono(Offset(VX0, VY1), Offset(VX1, VY1), FUERA.bottomRight, FUERA.bottomLeft), lerp(a.en(LUZ_GRANDE, C.PIEDRA), claro, 0.4f * dia))
        clipRect(FUERA.left, FUERA.top, FUERA.right, FUERA.bottom) { with(CiudadRenacimiento) { vista(m, ext, FUERA) } }
        // La sombra que el dintel y las jambas dejan en lo que se ve, para que el hueco tenga fondo.
        drawRect(Brush.verticalGradient(0f to C.SOMBRA.copy(alpha = 0.22f), 1f to Color.Transparent, startY = FUERA.top, endY = FUERA.top + 5f), FUERA.topLeft, Size(FUERA.width, 5f), blendMode = BlendMode.Multiply)
        // El marco de piedra gris, con su filo de luz por dentro, y el alféizar que sobresale.
        val piedra = a.en(Offset(199f, VY0), C.PIEDRA)
        drawRect(piedra, Offset(VX0 - 2.2f, VY0 - 2.2f), Size(VX1 - VX0 + 4.4f, VY1 - VY0 + 4.4f), style = Stroke(4.4f))
        drawRect(lerp(claro, piedra, 0.4f).copy(alpha = 0.6f * dia + 0.15f), Offset(VX0, VY0), Size(VX1 - VX0, VY1 - VY0), style = Stroke(0.7f))
        drawRect(a.en(Offset(199f, VY0 - 6f), lerp(C.PIEDRA, C.CARBON, 0.2f)), Offset(VX0 - 7f, VY0 - 8f), Size(VX1 - VX0 + 14f, 3.6f))
        drawRect(a.en(Offset(199f, VY1 + 3f), lerp(C.PIEDRA, C.YESO, 0.25f)), Offset(VX0 - 8f, VY1 + 1.6f), Size(VX1 - VX0 + 16f, 2.2f))
        drawRect(a.en(Offset(199f, VY1 + 5f), lerp(C.PIEDRA, C.CARBON, 0.25f)), Offset(VX0 - 7f, VY1 + 3.8f), Size(VX1 - VX0 + 14f, 2.6f))
        // Manchas de lluvia vieja bajo el alféizar.
        for (x in listOf(142f, 178f, 226f, 254f)) drawLine(a.en(Offset(x, VY1 + 12f), C.VIGA).copy(alpha = 0.16f), Offset(x, VY1 + 6.4f), Offset(x + 0.6f, VY1 + 20f), 1.6f, StrokeCap.Round)
        // Con nieve, un ribete blanco en el alféizar de fuera.
        if (R.nevado(m)) drawRect(a.en(Offset(199f, VY1), C.YESO), Offset(FUERA.left, FUERA.bottom - 2.2f), Size(FUERA.width, 2.2f))
        // Las dos hojas de la contraventana.
        val abre = R.abierta(m)
        hoja(a, VX0, 1f, if (R.entornada(m) && abre > 0.9f) 0.69f else abre)
        hoja(a, VX1, -1f, abre)
        // La claridad que el cielo deja en la pared de alrededor.
        mancha(LUZ_GRANDE, 104f, 76f, claro, 0.1f * dia * (1f - 0.4f * ext.nublado) * (0.5f + 0.5f * abre))
    }

    /**
     * Una hoja de la contraventana, con la bisagra en [bisagra] y que cierra hacia [hacia] (1, a la derecha; −1, a la
     * izquierda). [abre]: de 0 (cerrada, tapando media ventana) a 1 (abierta del todo, de canto hacia quien mira).
     */
    private fun DrawScope.hoja(a: Alumbrado, bisagra: Float, hacia: Float, abre: Float) {
        val ang = (PI / 2 * abre.coerceIn(0f, 1f)).toFloat(); val x = bisagra + hacia * HOJA * cos(ang); val t = HOJA * sin(ang)
        val forma = poligono(Offset(bisagra, VY0), E.p(x, VY0, t), E.p(x, VY1, t), Offset(bisagra, VY1))
        val o = E.p((bisagra + x) / 2f, 100f, t / 2f); val madera = lerp(C.VIGA, C.MADERA, 0.45f)
        // La cara de dentro mira al centro de la ventana cuando está abierta, y a la sala cuando está cerrada.
        drawPath(forma, lerp(a.en(o, madera, hacia * abre), a.en(o, lerp(madera, C.CARBON, 0.3f)), 0.5f * (1f - abre)))
        clipPath(forma) {
            // Las tablas, los dos travesaños con sus clavos y el desgaste de abajo.
            for (k in 1..3) { val u = k / 4f; drawLine(a.en(o, C.CARBON).copy(alpha = 0.4f), E.p(bisagra + (x - bisagra) * u, VY0, t * u), E.p(bisagra + (x - bisagra) * u, VY1, t * u), 0.5f) }
            for (y in listOf(VY0 + 14f, VY1 - 16f)) {
                drawLine(a.en(o, lerp(C.VIGA, C.CARBON, 0.4f)), Offset(bisagra, y), E.p(x, y, t), 2.6f)
                for (k in 0..4) { val u = 0.1f + k * 0.2f; drawCircle(a.en(o, lerp(C.PIEDRA, C.CARBON, 0.4f)), 0.6f, E.p(bisagra + (x - bisagra) * u, y, t * u)) }
            }
            drawPath(poligono(Offset(bisagra, VY1 - 7f), E.p(x, VY1 - 7f, t), E.p(x, VY1, t), Offset(bisagra, VY1)), a.en(o, C.YESO).copy(alpha = 0.14f))
        }
        drawLine(a.en(o, lerp(C.MADERA, C.YESO, 0.3f)).copy(alpha = 0.7f), E.p(x, VY0, t), E.p(x, VY1, t), 0.8f)
        // Las dos bisagras de hierro.
        for (y in listOf(VY0 + 14f, VY1 - 16f)) drawRect(a.en(Offset(bisagra, y), C.CARBON), Offset(bisagra - 1.4f, y - 2f), Size(2.8f, 4f))
    }

    // ---------------------------------------------------------------- La ventana de la calle

    private fun DrawScope.ventanaDeLaCalle(m: Momento, a: Alumbrado, ext: Luces) {
        val dia = (1f - ext.noche).coerceIn(0f, 1f); val claro = lerp(ext.medio, Color.White, 0.5f)
        val dentro = huecoCalle(0f); val fuera = huecoCalle(GRUESO_CALLE)
        drawPath(dentro, lerp(a.en(LUZ_CALLE, C.MURO), claro, 0.42f * dia))
        clipPath(dentro) {
            clipPath(fuera) { calle(m, ext) }
            // La cruz de madera de la ventana, a contraluz.
            val palo = lerp(a.en(LUZ_CALLE, C.VIGA), Color(0xFF1A1620), 0.35f)
            drawLine(palo, E.p(E.x0 - GRUESO_CALLE, LY0, (LT0 + LT1) / 2f), E.p(E.x0 - GRUESO_CALLE, LY1, (LT0 + LT1) / 2f), 1.6f)
            drawLine(palo, E.p(E.x0 - GRUESO_CALLE, 98f, LT0), E.p(E.x0 - GRUESO_CALLE, 98f, LT1), 1.5f)
        }
        // El marco de piedra y el alféizar.
        drawPath(dentro, a.en(LUZ_CALLE + Offset(14f, 20f), C.PIEDRA, 1f), style = Stroke(2.6f))
        drawPath(dentro, lerp(claro, C.PIEDRA, 0.3f).copy(alpha = 0.5f * dia), style = Stroke(0.6f))
        drawLine(a.en(LUZ_CALLE + Offset(0f, 40f), lerp(C.PIEDRA, C.YESO, 0.2f), 1f), E.p(E.x0, LY1 + 1.6f, LT0 - 3f), E.p(E.x0, LY1 + 1.6f, LT1 + 3f), 3.2f)
        // Su postigo, abierto contra la pared, hacia quien mira.
        val postigo = poligono(E.p(E.x0, LY0, LT1 + 3f), E.p(E.x0, LY0, LT1 + 36f), E.p(E.x0, LY1, LT1 + 36f), E.p(E.x0, LY1, LT1 + 3f))
        val o = E.p(E.x0, 100f, LT1 + 18f); val madera = lerp(C.VIGA, C.MADERA, 0.4f)
        drawPath(postigo, a.en(o, madera, 1f))
        clipPath(postigo) {
            for (k in 1..3) drawLine(a.en(o, C.CARBON).copy(alpha = 0.4f), E.p(E.x0, LY0, LT1 + 3f + k * 8.2f), E.p(E.x0, LY1, LT1 + 3f + k * 8.2f), 0.5f)
            for (y in listOf(LY0 + 10f, LY1 - 12f)) drawLine(a.en(o, lerp(C.VIGA, C.CARBON, 0.4f), 1f), E.p(E.x0, y, LT1 + 3f), E.p(E.x0, y, LT1 + 36f), 2.4f * E.s(LT1 + 18f))
        }
        mancha(LUZ_CALLE, 44f, 56f, claro, 0.1f * dia * (1f - 0.4f * ext.nublado))
    }

    /**
     * Lo que se ve por la ventana de la calle. La calle es estrecha: la casa de enfrente queda a [CALLE] del muro, y por
     * el hueco, que es hondo, se la ve en escorzo hacia el fondo: de su pared caben el tramo de −49 a 4 de hondo y de 44
     * a 143 de alto. En ese tramo van el alero con sus canes, dos ventanas con marco de piedra y postigos (una, con una
     * maceta; la otra, con luz de noche) y la cuerda de la ropa de ventana a ventana.
     */
    private fun DrawScope.calle(m: Momento, ext: Luces) {
        val r = Rayo(ext); val x = E.x0 - CALLE
        drawRect(Brush.verticalGradient(0f to ext.alto, 1f to ext.bajo, startY = 20f, endY = 110f), Offset(0f, 0f), Size(90f, 160f))
        if (ext.nublado > 0.3f) drawRect(lerp(ext.medio, Color(0xFF9AA0A8), 0.4f).copy(alpha = 0.6f * ext.nublado), Offset(0f, 0f), Size(90f, 160f))
        if (ext.estrellas > 0.3f) { val az = Azar(Semilla.de("renacimiento", "estrellas")); repeat(9) { drawCircle(Color.White.copy(alpha = 0.7f * ext.estrellas), 0.4f, Offset(az.entre(28f, 54f), az.entre(44f, 76f))) } }
        // El muro de enfrente, con el sol de la mañana de lleno y en sombra por la tarde.
        val alSol = r.lat < 0f; val tejaArriba = 84f
        val muro = ext.cara(lerp(C.OCRE, C.MURO, 0.5f), if (alSol) 0.7f * r.dia else -0.5f, 0.1f)
        val casa = poligono(E.p(x, tejaArriba, -80f), E.p(x, tejaArriba, 70f), E.p(x, 200f, 70f), E.p(x, 200f, -80f))
        drawPath(casa, muro)
        clipPath(casa) {
            val az = Azar(Semilla.de("renacimiento", "calle"))
            repeat(8) { val o = E.p(x, az.entre(92f, 142f), az.entre(-48f, 4f)); mancha(o, az.entre(2f, 4f), az.entre(4f, 9f), ext.tono(C.VIGA, 0.1f), 0.18f) }
            // Un desconchón que enseña el ladrillo, y la faja de piedra bajo las ventanas.
            drawPath(poligono(E.p(x, 126f, -22f), E.p(x, 124f, -14f), E.p(x, 134f, -10f), E.p(x, 138f, -20f)), ext.tono(lerp(C.COTTO, C.MURO, 0.3f), 0.1f))
            drawPath(poligono(E.p(x, 121f, -80f), E.p(x, 121f, 70f), E.p(x, 123f, 70f), E.p(x, 123f, -80f)), ext.cara(lerp(C.PIEDRA, C.MURO, 0.3f), if (alSol) 0.4f else -0.4f, 0.1f))
            // La sombra del alero, tendida en la fachada.
            drawPath(poligono(E.p(x, tejaArriba, -80f), E.p(x, tejaArriba, 70f), E.p(x, tejaArriba + 9f, 70f), E.p(x, tejaArriba + 9f, -80f)), C.SOMBRA.copy(alpha = 0.4f * r.dia + 0.1f), blendMode = BlendMode.Multiply)
            // Dos ventanas con su marco de piedra y sus postigos; de noche, una con luz.
            for ((k, t0) in listOf(-42f, -15f).withIndex()) {
                val y0 = 96f; val y1 = 119f; val ancho = 14f
                val marco = poligono(E.p(x, y0 - 2f, t0 - 2f), E.p(x, y0 - 2f, t0 + ancho + 2f), E.p(x, y1 + 2.4f, t0 + ancho + 2f), E.p(x, y1 + 2.4f, t0 - 2f))
                val hueco = poligono(E.p(x, y0, t0), E.p(x, y0, t0 + ancho), E.p(x, y1, t0 + ancho), E.p(x, y1, t0))
                val conLuz = k == 1 && ext.noche > 0.5f && m.hora >= 18.0 && m.hora < 22.5
                drawPath(marco, ext.cara(lerp(C.PIEDRA, C.YESO, 0.2f), if (alSol) 0.5f * r.dia else -0.3f, 0.1f))
                drawPath(hueco, if (conLuz) lerp(C.LAMPARA, C.OCRE, 0.2f) else ext.tono(lerp(C.CARBON, C.VIGA, 0.4f), 0.1f))
                // Un postigo cerrado a medias (de noche con luz, abiertos los dos).
                if (!conLuz) { drawPath(poligono(E.p(x, y0, t0), E.p(x, y0, t0 + ancho * 0.48f), E.p(x, y1, t0 + ancho * 0.48f), E.p(x, y1, t0)), ext.tono(lerp(C.VERDE, C.VIGA, 0.4f), 0.1f))
                    for (y in listOf(y0 + 6f, y1 - 6f)) drawLine(ext.tono(lerp(C.VIGA, C.CARBON, 0.4f), 0.1f), E.p(x, y, t0), E.p(x, y, t0 + ancho * 0.48f), 0.6f) }
                else { drawLine(ext.tono(C.CARBON, 0.1f), E.p(x, y0, t0 + ancho / 2f), E.p(x, y1, t0 + ancho / 2f), 0.6f); mancha(E.p(x, (y0 + y1) / 2f, t0 + ancho / 2f), 10f, 14f, C.LAMPARA, 0.34f) }
                // La sombra del marco, y en la primera, una maceta con su mata en el alféizar.
                drawPath(poligono(E.p(x, y0, t0), E.p(x, y0, t0 + ancho), E.p(x, y0 + 2.4f, t0 + ancho), E.p(x, y0 + 2.4f, t0)), C.SOMBRA.copy(alpha = 0.3f), blendMode = BlendMode.Multiply)
                if (k == 0) { val o = E.p(x, y1, t0 + 10f); drawRect(ext.tono(C.COTTO, 0.1f), Offset(o.x - 1.4f, o.y - 3.4f), Size(2.8f, 3.4f)); drawCircle(ext.tono(lerp(C.VERDE, C.CARBON, 0.2f), 0.1f), 2.4f, o + Offset(0f, -5.4f)); drawCircle(ext.tono(C.CINABRIO, 0.1f), 0.8f, o + Offset(-0.8f, -6.2f)); drawCircle(ext.tono(C.CINABRIO, 0.1f), 0.7f, o + Offset(1f, -4.6f)) }
            }
            // La cuerda de la ropa, de ventana a ventana, con una camisa, una sábana y un paño de color.
            if (m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES || m.ficha.clima == Clima.VIENTO) {
                drawLine(ext.tono(C.CARBON, 0.1f), E.p(x, 124.6f, -48f), E.p(x, 126.6f, 4f), 0.5f)
                val luzRopa = if (alSol) 0.6f * r.dia else -0.3f
                for ((k, t0) in listOf(-45f, -31f, -12f).withIndex()) { val w = listOf(11f, 16f, 9f)[k]; val cae = listOf(140f, 137f, 141f)[k]
                    val c = listOf(C.YESO, lerp(C.YESO, C.MURO, 0.3f), lerp(C.ULTRAMAR, C.YESO, 0.4f))[k]
                    val pano = poligono(listOf(E.p(x, 125f, t0), E.p(x, 125.6f, t0 + w), E.p(x, cae - 1f, t0 + w), E.p(x, cae + 0.6f, t0 + w * 0.5f), E.p(x, cae, t0)))
                    drawPath(Path().apply { addPath(pano, Offset(1.2f, 1.4f)) }, C.SOMBRA.copy(alpha = 0.3f * r.dia), blendMode = BlendMode.Multiply)
                    drawPath(pano, ext.cara(c, luzRopa, 0.1f))
                    drawLine(ext.tono(lerp(c, C.VIGA, 0.5f), 0.1f).copy(alpha = 0.6f), E.p(x, 126f, t0 + w * 0.5f), E.p(x, cae, t0 + w * 0.5f), 0.4f) }
            }
        }
        // El alero, que vuela sobre la calle: por debajo, sus canes de madera; por encima, el faldón de teja con sus canales.
        val teja = if (R.nevado(m)) lerp(C.TEJA, C.YESO, 0.8f) else C.TEJA
        val vuelo = poligono(E.p(x + 12f, tejaArriba - 5f, -80f), E.p(x + 12f, tejaArriba - 5f, 70f), E.p(x, tejaArriba, 70f), E.p(x, tejaArriba, -80f))
        drawPath(vuelo, ext.tono(lerp(C.VIGA, C.CARBON, 0.2f), 0.1f))
        for (k in 0..9) { val t0 = -50f + k * 6.4f; drawLine(ext.tono(lerp(C.MADERA, C.VIGA, 0.4f), 0.1f), E.p(x, tejaArriba, t0), E.p(x + 12f, tejaArriba - 5f, t0), 1.1f) }
        val faldon = poligono(E.p(x + 12f, tejaArriba - 5f, -80f), E.p(x + 12f, tejaArriba - 5f, 70f), E.p(x - 12f, tejaArriba - 19f, 70f), E.p(x - 12f, tejaArriba - 19f, -80f))
        drawPath(faldon, ext.cara(teja, 0.36f * r.dia, 0.1f))
        clipPath(faldon) {
            for (k in 0..17) { val t0 = -52f + k * 3.4f; drawLine(ext.tono(lerp(teja, C.VIGA, 0.55f), 0.1f).copy(alpha = 0.6f), E.p(x + 12f, tejaArriba - 5f, t0), E.p(x - 12f, tejaArriba - 19f, t0), 0.5f) }
            for (k in 1..3) drawLine(ext.tono(lerp(teja, C.YESO, 0.3f), 0.1f).copy(alpha = 0.4f), E.p(x + 12f - k * 6f, tejaArriba - 5f - k * 3.5f, -80f), E.p(x + 12f - k * 6f, tejaArriba - 5f - k * 3.5f, 70f), 0.4f)
        }
        drawLine(ext.tono(lerp(teja, C.YESO, 0.35f), 0.1f), E.p(x + 12f, tejaArriba - 5f, -80f), E.p(x + 12f, tejaArriba - 5f, 70f), 0.9f)
        // Una chimenea en el tejado, contra el cielo.
        val ch = E.p(x - 4f, tejaArriba - 14f, -24f)
        drawRect(ext.cara(lerp(C.MURO, C.COTTO, 0.5f), if (alSol) 0.6f * r.dia else -0.5f, 0.1f), ch + Offset(-2f, -10f), Size(4f, 10f)); drawPath(poligono(ch + Offset(-3f, -10f), ch + Offset(3f, -10f), ch + Offset(0f, -13f)), ext.tono(teja, 0.1f))
        if (m.ficha.clima == Clima.NIEBLA) drawRect(ext.bruma.copy(alpha = 0.5f), Offset(0f, 0f), Size(90f, 160f))
        if (m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA) { val az = m.azar("lluvia_calle"); repeat(22) { val o = Offset(az.entre(26f, 56f), az.entre(40f, 140f)); drawLine(Color.White.copy(alpha = 0.3f), o, o + Offset(-1.2f, 5f), 0.5f) } }
    }

    // ---------------------------------------------------------------- Los adornos del día

    private fun DrawScope.adornos(m: Momento, l: Luces, a: Alumbrado) {
        if (R.fiesta(m)) {
            val az = Azar(Semilla.de("renacimiento", "guirnalda"))
            guirnalda(l, Offset(VX0 - 8f, VY0 - 7f), Offset(VX1 + 8f, VY0 - 7f), 9f, az, hoja = lerp(C.VERDE, C.CARBON, 0.2f), flores = if (R.mayo(m)) listOf(C.YESO, lerp(C.CINABRIO, C.YESO, 0.6f)) else listOf(C.CINABRIO, C.ORO), grueso = 2.8f)
        }
        if (R.mayo(m)) {
            // El primero de mayo: ramas en flor atadas a la contraventana y metidas en un cántaro del alféizar.
            val az = Azar(Semilla.de("renacimiento", "mayo"))
            for ((pie, lado) in listOf(Offset(146f, VY1 + 1f) to 1f, Offset(252f, VY1 + 1f) to -1f)) {
                repeat(5) { k ->
                    val punta = pie + Offset(lado * az.entre(2f, 20f), -az.entre(18f, 40f)); val codo = pie + Offset(lado * az.entre(0f, 8f), -az.entre(8f, 16f))
                    val rama = Path().apply { moveTo(pie.x, pie.y); cubicTo(codo.x, codo.y, codo.x, codo.y, punta.x, punta.y) }
                    drawPath(rama, a.en(codo, lerp(C.VIGA, C.CARBON, 0.3f)), style = Stroke(0.8f, cap = StrokeCap.Round))
                    repeat(7) { val u = az.entre(0.3f, 1f); val q = pie + (punta - pie) * u + Offset(az.entre(-2.4f, 2.4f), az.entre(-2.4f, 2.4f)); drawCircle(a.en(q, if (it % 3 == 0) lerp(C.CINABRIO, C.YESO, 0.66f) else C.YESO), az.entre(0.8f, 1.5f), q) }
                    if (k % 2 == 0) drawCircle(a.en(punta, C.VERDE), 1.1f, punta)
                }
                val cantaro = Path().apply { moveTo(pie.x - 4f, pie.y); cubicTo(pie.x - 6.4f, pie.y - 6f, pie.x - 3f, pie.y - 9f, pie.x - 2f, pie.y - 10f); lineTo(pie.x + 2f, pie.y - 10f); cubicTo(pie.x + 3f, pie.y - 9f, pie.x + 6.4f, pie.y - 6f, pie.x + 4f, pie.y); close() }
                drawPath(cantaro, a.en(pie, C.COTTO, -lado))
            }
        }
        if (R.luto(m)) {
            // El luto: un paño negro sobre el dintel de la ventana grande, con sus dos caídas.
            val negro = a.en(Offset(199f, VY0 - 4f), Color(0xFF2A2630))
            drawPath(Path().apply { moveTo(VX0 - 9f, VY0 - 9f); lineTo(VX1 + 9f, VY0 - 9f); lineTo(VX1 + 9f, VY0 + 22f); lineTo(VX1 + 3f, VY0 + 16f); lineTo(VX1 + 3f, VY0 - 3f)
                cubicTo(230f, VY0 + 3f, 168f, VY0 + 3f, VX0 - 3f, VY0 - 3f); lineTo(VX0 - 3f, VY0 + 16f); lineTo(VX0 - 9f, VY0 + 22f); close() }, negro)
            drawLine(lerp(negro, Color.White, 0.12f), Offset(VX0 - 9f, VY0 - 9f), Offset(VX1 + 9f, VY0 - 9f), 0.7f)
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val ext = m.luces()
        with(ObraRenacimiento) { llamas(m, reloj) }
        clipRect(FUERA.left, FUERA.top, FUERA.right, FUERA.bottom) { with(CiudadRenacimiento) { vivo(m, ext, reloj, FUERA) } }
        // El relámpago, que se cuela por las dos ventanas.
        if (m.ficha.clima == Clima.TORMENTA) { val k = sin(reloj * 0.9f) * sin(reloj * 3.7f + 1f); if (k > 0.86f) { drawPath(huecoCalle(0f), Color.White.copy(alpha = 0.5f)); drawRect(Color.White.copy(alpha = 0.3f), FUERA.topLeft, FUERA.size); drawRect(Color.White.copy(alpha = 0.05f), Offset.Zero, Size(ANCHO, ALTO)) } }
    }

    /** La llama de la lámpara de la mesa, con su resplandor. */
    fun DrawScope.llamaDeLaLampara(reloj: Float) { resplandor(ObraRenacimiento.LUZ_LAMPARA, 15f, 0.55f, C.LAMPARA); llama(ObraRenacimiento.LUZ_LAMPARA + Offset(0f, 2.4f), 4.6f, reloj, 11) }
}
