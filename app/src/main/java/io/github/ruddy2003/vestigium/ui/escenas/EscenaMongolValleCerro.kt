package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
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
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.ovoo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.rebano
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.alturaEn
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.borron
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.en
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.fijo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.rota
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El cerro que abriga el valle, la diagonal grande del cuadro, en dos planos: **la cima** detrás, con el ovoo recortado
 * contra el cielo, y **un hombro** delante, más cerca, más cálido y con más detalle. Cada uno está hecho de caras: de
 * cada espolón baja una arista que separa una cara que mira a la derecha de otra que mira a la izquierda, y entre dos
 * espolones baja una cárcava. Con el sol de la mañana (a la derecha) se encienden unas caras y las otras quedan en
 * sombra azul; por la tarde, al revés (regla 1).
 *
 * El viento barre los lomos y deja asomar la hierba seca y la roca: la nieve se queda en las cárcavas, en las caras que
 * menos ven el sol y al pie del cerro, en ventisqueros (regla 4, sin agua: la nieve). Afloramientos de roca en estratos,
 * sus canchales debajo y matas secas rompen las caras (reglas 7 y 9). **[Diseño propio]**: el cuento solo dice «el cerro».
 */
internal object ValleCerro {
    private val C = ColorMongol

    /** La cresta de la cima, puesta a mano: sube de detrás de la tienda al ovoo y baja larga hasta donde asoma el río. */
    val CRESTA = pts(
        -6f, 70f, 12f, 66.4f, 28f, 65.8f, 44f, 62.8f, 58f, 63.4f, 72f, 60.2f, 88f, 59.6f, 102f, 56.8f, 116f, 57.6f, 128f, 55.2f, 140f, 55.8f,
        152f, 53.8f, 162f, 54.6f, 172f, 53.2f, 182f, 54.8f, 190f, 54.4f, 198f, 55.8f, 206f, 58.6f, 214f, 60.2f, 222f, 64f, 230f, 65.8f,
        238f, 70.4f, 246f, 73f, 254f, 78.2f, 262f, 81f, 270f, 86.4f, 278f, 89.6f, 286f, 95.8f, 292f, 100.4f, 298f, 105.2f, 303f, 110.6f,
        308f, 115.2f, 313f, 119f, 317f, 121.2f,
    )

    /** El pie del cerro: donde la ladera se encuentra con el fondo del valle (y donde se escalonan las tiendas). */
    val PIE = pts(120f, 214f, 140f, 207.6f, 158f, 199.4f, 176f, 190.6f, 196f, 180.4f, 212f, 171.8f, 230f, 162.4f, 248f, 153.6f, 264f, 146f, 278f, 138.4f, 290f, 132f, 300f, 127f, 310f, 123.4f, 317f, 121.2f)

    /** La cresta del hombro, el plano de delante: baja de detrás de la tienda y se junta con el pie hacia la derecha. */
    val HOMBRO = pts(56f, 89.6f, 76f, 92.6f, 92f, 93.4f, 108f, 96.8f, 122f, 98f, 136f, 101.6f, 150f, 102.6f, 162f, 106.8f, 174f, 108.2f, 186f, 112.6f, 198f, 114f, 210f, 118.4f, 222f, 120.2f, 234f, 124.8f, 244f, 126.2f, 254f, 130.4f, 264f, 133.6f, 272f, 136.6f, 279f, 138.4f)

    /** El ovoo, en la cima. */
    val OVOO = Offset(190f, 55.4f)

    /**
     * Una arista (espolón, `true`) o una cárcava, de arriba abajo; y la cara que queda a su derecha, con su normal (las
     * caras que miran a la derecha se encienden con el sol de la mañana; las que miran a la izquierda, por la tarde).
     */
    private class Linea(val puntos: List<Offset>, val espolon: Boolean, val cara: V3, val nieve: Receta? = null, val manto: Boolean = true)

    /**
     * Cómo junta la nieve una cárcava (o el abrigo de un espolón), cada una a su manera para que no salgan rayas
     * iguales y paralelas: los [tramos] que lleva (fracciones de la línea, de arriba abajo; con poca nieve solo quedan
     * los de abajo), su [ancho] relativo, si acaba en una hondonada ancha ([hoya], su tamaño) y cuánto se [tuerce] la
     * cinta fuera de la línea recta del pliegue. **[Diseño propio]**.
     */
    private class Receta(val tramos: List<Pair<Float, Float>>, val ancho: Float, val hoya: Float = 0f, val tuerce: Float = 0f)

    private val DERECHA = V3(0.74f, 0.42f, -0.52f)
    private val IZQUIERDA = V3(-0.74f, 0.42f, -0.52f)
    private val FRENTE = V3(-0.18f, 0.5f, -0.85f)

    private val CIMA = listOf(
        Linea(pts(-6f, 70f, -6f, 260f), false, IZQUIERDA),
        // Un hombro detrás de la tienda, con su cárcava: rompen la cara larga de la izquierda como el resto del cerro.
        Linea(pts(58f, 63.4f, 62f, 71f, 67f, 80f, 73f, 90f, 79f, 100f), true, DERECHA, Receta(listOf(0.42f to 0.7f), 0.8f, tuerce = 0.8f)),
        Linea(pts(88f, 59.6f, 91f, 68f, 95f, 77f, 100f, 87f, 105f, 98f), false, IZQUIERDA, Receta(listOf(0.4f to 0.66f), 0.85f, hoya = 0.9f, tuerce = -1f), manto = false),
        Linea(pts(116f, 57.6f, 122f, 72f, 128f, 88f, 132f, 104f), true, DERECHA),
        Linea(pts(140f, 55.8f, 147f, 68f, 152f, 82f, 158f, 96f, 162f, 110f), false, FRENTE, Receta(listOf(0.62f to 0.9f), 0.75f, tuerce = 1.3f)),
        Linea(pts(190f, 54.4f, 200f, 66f, 214f, 80f, 228f, 95f, 244f, 112f, 258f, 130f, 270f, 142f), true, DERECHA),
        Linea(pts(222f, 64f, 232f, 76f, 242f, 89f, 254f, 104f, 266f, 120f, 280f, 136f), false, IZQUIERDA, Receta(listOf(0.24f to 0.38f, 0.6f to 0.84f), 1.1f, hoya = 0.8f, tuerce = -1.6f)),
        Linea(pts(246f, 73f, 255f, 85f, 265f, 99f, 276f, 114f, 288f, 130f), true, DERECHA),
        Linea(pts(270f, 86.4f, 278f, 97f, 287f, 110f, 297f, 125f), false, IZQUIERDA, Receta(listOf(0.42f to 0.76f), 1.25f, hoya = 1.25f, tuerce = 1.1f)),
        Linea(pts(292f, 100.4f, 297f, 108f, 303f, 117f, 308f, 124f), true, DERECHA),
        Linea(pts(317f, 121.2f, 330f, 260f), false, V3(0f, 1f, 0f)),
    )

    private val DELANTE = listOf(
        Linea(pts(-6f, 92f, -6f, 260f), false, FRENTE),
        Linea(pts(150f, 102.6f, 154f, 118f, 158f, 136f, 163f, 156f, 168f, 178f, 172f, 200f), false, IZQUIERDA, Receta(listOf(0.3f to 0.56f), 1.2f, hoya = 1.3f, tuerce = 1f)),
        Linea(pts(174f, 108.2f, 180f, 122f, 184f, 138f, 188f, 156f, 192f, 176f), true, DERECHA, Receta(listOf(0.3f to 0.56f), 0.85f)),
        Linea(pts(198f, 114f, 203f, 127f, 209f, 141f, 215f, 156f, 220f, 170f), false, IZQUIERDA, Receta(listOf(0.44f to 0.6f, 0.7f to 0.88f), 0.75f, tuerce = -1.2f)),
        Linea(pts(222f, 120.2f, 228f, 132f, 234f, 144f, 240f, 156f), true, DERECHA),
        Linea(pts(244f, 126.2f, 249f, 136f, 254f, 147f), false, FRENTE, Receta(listOf(0.35f to 0.85f), 0.6f)),
        Linea(pts(262f, 133.2f, 266f, 141f, 270f, 146f), true, DERECHA),
        Linea(pts(279f, 138.4f, 290f, 260f), false, V3(0f, 1f, 0f)),
    )

    /** Los afloramientos de roca: dónde pisa cada uno, su ancho y si tiene canchal debajo. */
    private class Pena(val pie: Offset, val ancho: Float, val canchal: Boolean)

    private val PENAS_CIMA = listOf(
        Pena(Offset(128f, 62f), 7f, false), Pena(Offset(146f, 60.6f), 9f, true), Pena(Offset(168f, 60f), 6f, false),
        Pena(Offset(220f, 69.6f), 10f, true), Pena(Offset(262f, 92f), 6f, false), Pena(Offset(288f, 106f), 5f, true),
        // Las del hombro de la izquierda, detrás de la tienda.
        Pena(Offset(70f, 74f), 8f, true), Pena(Offset(101f, 80f), 6f, false),
    )
    private val PENAS_DELANTE = listOf(
        Pena(Offset(140f, 108f), 9f, true),
        Pena(Offset(164f, 113f), 12f, true), Pena(Offset(184f, 118.6f), 9f, false), Pena(Offset(204f, 123.4f), 14f, true),
        Pena(Offset(196f, 148f), 10f, true), Pena(Offset(236f, 136f), 8f, false), Pena(Offset(170f, 168f), 11f, false),
    )

    fun DrawScope.cerro(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val pasto = EscenaMongol.pasto(m)
        // La hierba seca del cerro: un ocre pardo, más oscuro que el fieltro y que la nieve (los tres valores del cuadro).
        val hierba = lerp(lerp(C.ESTEPA, C.CUERO, 0.3f), lerp(C.VERDE, C.ESTEPA, 0.35f), 0.45f * pasto)
        // La cima, detrás: un poco más azulada.
        val cima = poligono(CRESTA + PIE.reversed() + listOf(Offset(-6f, 230f)))
        // La hora azul: el resplandor que queda en el horizonte recorta la cresta y el ovoo.
        with(ValleLuz) { haloDeLaHoraAzul(m, p.l, CRESTA) }
        plano(p, sol, nieve, pasto, cima, CRESTA, CIMA, PENAS_CIMA, hierba, 0.13f, fijo("cima"))
        // Con el sol raso, lo alto es lo último (o lo primero) que alumbra: la cima se dora.
        val dorado = ValleLuz.sol(p.l) * ValleLuz.bajo(p.l)
        if (dorado > 0.05f) clipPath(cima) { drawRect(Brush.verticalGradient(0f to lerp(p.l.solColor, Color(0xFFFFC98A), 0.4f).copy(alpha = 0.16f * dorado), 1f to Color.Transparent, startY = 52f, endY = 112f), Offset(0f, 50f), Size(330f, 62f), blendMode = BlendMode.Plus) }
        // El ovoo, recortado contra el cielo, con su sombra en la cima (la del sol o la de la luna); sus cintas se encienden
        // con el sol.
        val alto = 24f
        val so = ValleLuz.solDeSombras(sol, p.l)
        capaSombra(so.r) { c -> sombraArrojada(so.r, PiezasMongol.siluetaDeOvoo(OVOO, alto), OVOO, c, OVOO.y + 0.6f, OVOO.y + 0.6f) }
        ovoo(ValleLuz.alSol(p, 1.3f), OVOO, alto, EscenaMongol.cintasDelOvoo(m), Azar(Semilla.de("mongol", "ovoo")), 0.12f, nieve, m.viento)
        // «La última luz del día se queda un momento en lo alto del cerro, sobre el ovoo.»
        val l = p.l
        if (l.solAltura in -0.05f..0.25f && l.noche < 0.6f) mancha(OVOO + Offset(0f, -2f), 60f, 9f, l.solColor, 0.28f * (1f - l.noche))
        // Entre la cima y el hombro, un poco de aire: despega los dos planos.
        val hombro = poligono(listOf(Offset(-6f, 92f)) + HOMBRO + PIE.filter { it.x < 279f }.reversed() + listOf(Offset(-6f, 230f)))
        clipPath(cima) { drawPath(poligono(HOMBRO.map { it + Offset(0f, 3f) } + HOMBRO.reversed().map { it + Offset(0f, -18f) }), Brush.verticalGradient(0f to Color.Transparent, 1f to lerp(l.bruma, l.bajo, 0.4f).copy(alpha = 0.3f * (1f - 0.6f * l.noche)), startY = 76f, endY = 138f)) }
        // El hombro, delante: más cálido, con la roca más grande.
        plano(p, sol, nieve, pasto, hombro, HOMBRO, DELANTE, PENAS_DELANTE, lerp(hierba, C.CUERO, 0.12f), 0.03f, fijo("hombro"))
        // La nieve arrimada al pie del cerro: un ventisquero largo de borde festoneado y blando, y motas por encima.
        if (nieve > 0.1f) {
            val az = fijo("pie")
            val arriba = PIE.indices.flatMap { i -> if (i == PIE.size - 1) listOf(PIE[i]) else { val a = PIE[i]; val b = PIE[i + 1]; (0 until 2).map { k -> a + (b - a) * (k / 2f) } } }
                .map { q -> q + Offset(0f, -(3f + 9f * nieve) * (0.55f + 0.7f * az.real())) }
            val apron = curva(arriba).apply { for (q in PIE.reversed()) lineTo(q + Offset(0f, 2f)); close() }
            drawPath(apron, p.por(C.NIEVE, sol.t(V3(0.1f, 0.8f, -0.6f)) + 0.15f, 0.06f).copy(alpha = (nieve * 1.3f).coerceAtMost(1f)))
            drawPath(curva(arriba), p.luz(C.NIEVE, 0.06f).copy(alpha = 0.7f * nieve), style = Stroke(0.7f))
            drawPoints(arriba.flatMap { q -> (0 until 3).map { q + Offset(az.entre(-5f, 5f), -az.entre(1f, 6f)) } }, PointMode.Points, p.luz(C.NIEVE, 0.06f).copy(alpha = 0.8f * nieve), 1.2f, StrokeCap.Round)
        }
        // De día, el rebaño pace en la ladera del hombro: «las ovejas, por las laderas del valle». Apretado, para que se
        // lea como una mancha de lomos claros y no como puntos sueltos.
        val fuera = ValleMongol.ovejasEnLaLadera(m)
        if (fuera > 0) {
            val az = Azar(Semilla.de("mongol", "valle", "ladera", m.ficha.dia))
            val centro = az.entre(206f, 236f)
            val sitios = (0 until fuera).map { i ->
                val x = centro + az.entre(-1f, 1f) * (6f + 0.7f * i) + (if (i % 3 == 0) 8f else 0f)
                val y0 = alturaEn(HOMBRO, x) + 10f; val y1 = alturaEn(PIE, x) - 7f
                Offset(x, (y0 + (y1 - y0) * (0.55f + az.entre(-0.18f, 0.18f) * (0.5f + i / fuera.toFloat()))).coerceIn(y0, maxOf(y0, y1)))
            }
            rebano(p, sitios, { y -> 3.4f + (y - 128f) * 0.05f }, az, { 0.08f }, pastan = 0.85f)
        }
    }

    /**
     * Un plano del cerro: su [forma], su [cresta], sus aristas y cárcavas ([lineas], de izquierda a derecha), sus
     * afloramientos de roca, el color de su hierba seca y lo lejos que está.
     */
    private fun DrawScope.plano(p: Pincel, sol: Sol3, nieve: Float, pasto: Float, forma: Path, cresta: List<Offset>, lineas: List<Linea>, penas: List<Pena>, hierba: Color, lejos: Float, az: Azar) {
        val l = p.l
        val cerca = lejos < 0.08f
        // Las líneas, rotas a mano; cada cara se alarga por abajo (el plano la recorta).
        val rotas = lineas.map { li -> if (li.puntos.size > 2) rota(li.puntos, az, 1.4f, 2) else li.puntos }
        fun larga(r: List<Offset>): List<Offset> { val d = r.last() - r[r.size - 2]; return r + (r.last() + d / d.getDistance().coerceAtLeast(0.01f) * 90f) }
        val piel = lerp(hierba, C.NIEVE, 0.08f * nieve)
        val hacia = if (p.solALaDerecha) -1f else 1f
        drawPath(forma, p.medio(piel, lejos))
        clipPath(forma) {
            // Cada cara entre dos líneas, con su luz: de la arista a la cárcava, a lo ancho de la cara.
            for (i in 0 until rotas.size - 1) {
                val a = larga(rotas[i]); val b = larga(rotas[i + 1])
                val arriba = cresta.filter { it.x > a.first().x && it.x < b.first().x }
                val cara = poligono(a + b.reversed() + arriba.reversed())
                // La luz de la cara, con el contraste estirado: la que da la espalda al sol se va de verdad a la sombra azul.
                val n = lineas[i].cara; val t = ((sol.t(n) - 0.35f) * 1.6f).coerceIn(-1f, 1f)
                val claro = if (lineas[i].espolon) 0.18f else -0.18f
                drawPath(cara, Brush.linearGradient(0f to p.por(piel, t + claro, lejos), 1f to p.por(piel, t - claro, lejos), start = en(a, 0.25f), end = en(b, 0.25f)))
            }
            // Las cárcavas: el pliegue hondo, en sombra del lado contrario al sol, con una rama que se abre a media ladera
            // y la nieve dentro: una lengua que se ensancha hacia abajo, de borde roto.
            for ((i, li) in lineas.withIndex()) {
                if (li.espolon || li.puntos.size < 3) continue
                val r = rotas[i]
                val ancho = if (cerca) 3f else 1.8f
                val pliegue = poligono(r + r.reversed().mapIndexed { k, q -> q + Offset(hacia * ancho * (0.4f + 0.6f * (r.size - 1 - k) / r.size), 1f) })
                drawPath(pliegue, p.sombra(lerp(lerp(piel, C.TIERRA, 0.5f * (1f - nieve)), C.VERDE, (1.5f * pasto).coerceAtMost(0.7f)), lejos).copy(alpha = 0.8f))
                val brote = en(r, az.entre(0.35f, 0.6f)); val rama = rota(listOf(brote, brote + Offset(-hacia * az.entre(5f, 9f), az.entre(-9f, -5f)), brote + Offset(-hacia * az.entre(8f, 14f), az.entre(-16f, -11f))), az, 0.8f, 2)
                drawPath(Path().apply { moveTo(rama[0]); for (q in rama.drop(1)) lineTo(q) }, p.sombra(piel, lejos).copy(alpha = 0.65f), style = Stroke(ancho * 0.5f, cap = StrokeCap.Round))
            }
            // Los ventisqueros: la nieve que junta el viento, blanda y redonda (nunca en picos): metida en las cárcavas, al
            // abrigo de los espolones (a su derecha: el viento sopla del noroeste **[Aproximación]**) y abajo en las caras
            // que menos ven el sol. Cada uno es una sola forma de borde curvo, con su lomo claro y su vientre azul; alrededor,
            // motas de nieve sueltas: el paso de la nieve a la hierba seca.
            if (nieve > 0.08f) {
                val motas = ArrayList<Offset>()
                val luna = ValleLuz.luna(l)
                fun ventisquero(forma: Path, contorno: List<Offset>, t: Float, abajo: Float) {
                    drawPath(forma, p.sombra(lerp(C.NIEVE, C.HIELO, 0.25f), lejos))
                    // El lomo: la misma forma, un poco más arriba, sin tapar el vientre de abajo.
                    clipPath(forma) { drawPath(forma.let { val q = Path(); q.addPath(it, Offset(0f, -abajo)); q }, p.por(C.NIEVE, t + 0.15f, lejos)) }
                    // De noche, la nieve que mira a la luna se platea.
                    if (luna > 0.02f) drawPath(forma, ValleLuz.LUNA.copy(alpha = (0.16f * luna * (t + 0.5f)).coerceIn(0f, 1f)), blendMode = BlendMode.Plus)
                    for (q in contorno) if (az.real() < 0.6f) repeat(2) { motas += q + Offset(az.entre(-4f, 4f), az.entre(-3.5f, 1f)) }
                }
                val talla = if (cerca) 1f else 0.7f
                // Abajo, en las caras que miran a la izquierda: un manto de borde festoneado y redondo, apartado del espolón.
                for (i in 1 until rotas.size - 1) {
                    val n = lineas[i].cara; if (n.x > -0.3f || !lineas[i].manto || rotas[i + 1].size < 3) continue
                    val t = ((sol.t(n) - 0.35f) * 1.6f).coerceIn(-1f, 1f)
                    val ra = rotas[i]; val rb = rotas[i + 1]
                    // Solo abajo, y cada uno empieza a otra altura: no bandas largas iguales.
                    val f0 = 0.92f - 0.34f * nieve + az.entre(-0.1f, 0.06f)
                    val apartaA = if (lineas[i].espolon) 0.22f else 0.04f; val apartaB = if (lineas[i + 1].espolon) 0.22f else 0.04f
                    fun lado(f: Float, desdeA: Boolean): Offset { val qa = en(ra, f); val qb = en(rb, f); return if (desdeA) qa + (qb - qa) * apartaA else qb + (qa - qb) * apartaB }
                    val filas = 5
                    val izq = (0..filas).map { k -> lado(f0 + (1f - f0) * k / filas, true) + Offset(az.entre(-1f, 1f), 0f) }
                    val der = (0..filas).map { k -> lado(f0 + (1f - f0) * k / filas, false) + Offset(az.entre(-1f, 1f), 0f) }
                    val ancho = (der[0] - izq[0]).getDistance()
                    if (ancho < 4f) continue
                    // El borde de arriba: festones redondos.
                    val festones = (1..4).map { k -> izq[0] + (der[0] - izq[0]) * (k / 5f) + Offset(0f, (if (k % 2 == 0) -1f else 1f) * az.entre(1f, 2.6f) * (0.5f + ancho / 40f)) }
                    val contorno = listOf(izq[0]) + festones + der + listOf(der.last() + Offset(0f, 40f), izq.last() + Offset(0f, 40f)) + izq.drop(1).reversed()
                    ventisquero(TrazosValle.cerrada(contorno), listOf(izq[0]) + festones + der.take(3) + izq.take(3), t, 2.2f)
                }
                // En las cárcavas, cada una según su receta: tramos cortos o partidos, de ancho desigual (más anchos
                // hacia abajo, de puntas redondas), torcidos fuera de la recta del pliegue; alguna acaba en una
                // hondonada ancha donde la nieve se junta.
                val desde = 0.1f + 0.4f * (1f - nieve)
                for ((i, li) in lineas.withIndex()) {
                    val rc = li.nieve ?: continue
                    if (li.espolon) continue
                    val r = rotas[i]; val t = sol.t(V3(-hacia * 0.3f, 0.6f, -0.75f))
                    for ((a, b) in rc.tramos) {
                        val f0 = maxOf(a, desde); val f1 = b; if (f1 - f0 < 0.06f) continue
                        val d = en(r, f1) - en(r, f0); val normal = Offset(-d.y, d.x) / d.getDistance().coerceAtLeast(0.01f)
                        val eje = (0..6).map { k -> en(r, f0 + (f1 - f0) * k / 6f) + Offset(-hacia * 0.6f, 0.6f) + normal * (rc.tuerce * talla * sin(3.1416f * k / 6f)) }
                        val anchos = (0..6).map { k -> val f = f0 + (f1 - f0) * k / 6f; 1.9f * talla * rc.ancho * (0.5f + 0.9f * f) * (0.5f + 0.7f * nieve) * (0.7f + 0.5f * sin(3.1416f * k / 6f)) * az.entre(0.85f, 1.15f) }
                        ventisquero(TrazosValle.cinta(eje, anchos), eje, t, 0.9f)
                    }
                    if (rc.hoya > 0f) {
                        val c = en(r, rc.tramos.last().second) + Offset(-hacia * 1f, 1.5f * talla)
                        val rx = 6.5f * rc.hoya * talla * (0.45f + 0.6f * nieve); val ry = rx * 0.4f
                        // Lomo de arriba abultado y desigual; el de abajo, casi tendido sobre la ladera.
                        val borde = (0 until 12).map { j -> val ang = 6.2832f * j / 12f; val arriba = sin(ang) < 0f; val k = if (arriba) az.entre(0.75f, 1.2f) else az.entre(0.92f, 1.05f); c + Offset(cos(ang) * rx * k, sin(ang) * ry * k * (if (arriba) 1.15f else 0.45f)) }
                        ventisquero(TrazosValle.cerrada(borde), borde.filter { it.y <= c.y }, t, 1.6f)
                    }
                }
                // Al abrigo de algunos espolones (no de todos): una lengua pegada al lomo, del lado contrario al viento.
                for ((i, li) in lineas.withIndex()) {
                    val rc = li.nieve ?: continue
                    if (!li.espolon) continue
                    val r = rotas[i]
                    for ((a, b) in rc.tramos) {
                        val f0 = a + (b - a) * 0.4f * (1f - nieve); val f1 = b
                        val d = en(r, f1) - en(r, f0); val normal = Offset(-d.y, d.x) / d.getDistance().coerceAtLeast(0.01f)
                        val eje = (0..7).map { k -> en(r, f0 + (f1 - f0) * k / 7f) + Offset(4.5f * talla, 0.8f) + normal * (rc.tuerce * talla * sin(3.1416f * k / 7f)) }
                        val anchos = (0..7).map { k -> 2.4f * talla * rc.ancho * (0.5f + 0.6f * nieve) * sin(3.1416f * (k + 0.5f) / 8f).let { it * 0.7f + 0.3f } }
                        ventisquero(TrazosValle.cinta(eje, anchos), eje, sol.t(V3(0.5f, 0.55f, -0.65f)), 1f)
                    }
                }
                drawPoints(motas, PointMode.Points, p.luz(C.NIEVE, lejos).copy(alpha = 0.85f), if (cerca) 1.2f else 0.85f, StrokeCap.Round)
            }
            // Los espolones: un filo de luz del lado del sol.
            for ((i, li) in lineas.withIndex()) {
                if (!li.espolon) continue
                val r = rotas[i]
                drawPath(Path().apply { moveTo(r[0]); for (q in r.drop(1)) lineTo(q) }, p.luz(lerp(piel, C.FIELTRO, 0.3f), lejos).copy(alpha = 0.25f + 0.4f * p.r.dia), style = Stroke(if (cerca) 1.1f else 0.7f, cap = StrokeCap.Round))
            }
            // La hierba seca: pinceladas cortas del color de la piel, con su luz y su sombra; más grandes abajo.
            val y0 = cresta.minOf { it.y }
            val claros = ArrayList<Offset>(); val oscuros = ArrayList<Offset>()
            // (Todo esto llega hasta la izquierda, detrás de la tienda, con la misma densidad que en el resto.)
            repeat(if (cerca) 550 else 395) { val q = Offset(az.entre(30f, 320f), az.entre(y0, 214f)); if (az.real() < 0.5f) claros += q else oscuros += q }
            drawPoints(claros, PointMode.Points, p.luz(lerp(piel, C.ESTEPA, 0.5f), lejos).copy(alpha = 0.45f), if (cerca) 1f else 0.7f, StrokeCap.Round)
            drawPoints(oscuros, PointMode.Points, p.sombra(lerp(piel, C.CUERO, 0.4f), lejos).copy(alpha = 0.4f), if (cerca) 1f else 0.7f, StrokeCap.Round)
            // Las manchas de la hierba: más rubia en los lomos, más parda en las vaguadas, a pinceladas tendidas.
            repeat(if (cerca) 29 else 21) {
                val x = az.entre(40f, 316f); val yA = alturaEn(cresta, x) + 3f; val yB = alturaEn(PIE, x) - 3f; if (yB <= yA) return@repeat
                val q = Offset(x, az.entre(yA, yB)); val w = az.entre(6f, 18f) * (if (cerca) 1.3f else 1f)
                drawPath(borron(q, w, w * 0.24f, az, 9, 0.35f), (if (it % 2 == 0) p.luz(lerp(piel, C.ESTEPA, 0.6f), lejos) else p.por(lerp(piel, C.CUERO, 0.45f), -0.2f, lejos)).copy(alpha = 0.5f * (1f - 0.6f * nieve)))
            }
            // Los manchones de matorral: un velo pardo con su punteado de matas, en las vaguadas y al pie de las rocas.
            repeat(if (cerca) 12 else 10) {
                val x = az.entre(40f, 312f); val yA = alturaEn(cresta, x) + 6f; val yB = alturaEn(PIE, x) - 4f; if (yB <= yA) return@repeat
                val q = Offset(x, az.entre(yA, yB)); val w = az.entre(8f, 18f) * (if (cerca) 1.3f else 1f)
                drawPath(borron(q, w, w * 0.3f, az, 10, 0.4f), p.sombra(lerp(piel, C.CUERO, 0.5f), lejos).copy(alpha = 0.4f * (1f - 0.7f * nieve)))
                val matas = (0 until (w * 1.4f).toInt()).map { q + Offset(az.entre(-w, w) * 0.85f, az.entre(-w, w) * 0.24f) }
                drawPoints(matas, PointMode.Points, p.sombra(lerp(C.CUERO, C.HOLLIN, 0.4f), lejos).copy(alpha = 0.75f), if (cerca) 1.5f else 1f, StrokeCap.Round)
                drawPoints(matas.map { it + Offset(if (p.solALaDerecha) 0.35f else -0.35f, -0.4f) }, PointMode.Points, p.luz(lerp(C.CUERO, C.ESTEPA, 0.5f), lejos).copy(alpha = 0.6f), if (cerca) 0.8f else 0.5f, StrokeCap.Round)
            }
            // Las matas de espino: bultos oscuros con su luz y su sombra en el suelo.
            repeat(if (cerca) 34 else 32) {
                val x = az.entre(if (cerca) 110f else 50f, if (cerca) 278f else 314f); val yA = alturaEn(cresta, x) + 6f; val yB = alturaEn(PIE, x) - 5f; if (yB <= yA) return@repeat
                val q = Offset(x, az.entre(yA, yB)); val w = (1.4f + (q.y - 110f) * 0.03f).coerceAtLeast(0.9f) * (if (cerca) 1f else 0.7f)
                // Una mata de varas: ramitas que salen de un punto, oscuras, con alguna clara del lado del sol; su sombra tendida.
                drawOval(p.r.color.copy(alpha = 0.4f * p.r.fuerza), q + Offset(-w + p.r.ax.coerceIn(-2f, 2f) * w * 0.6f, -w * 0.15f), Size(w * 2.6f, w * 0.5f), blendMode = BlendMode.Multiply)
                for (j in 0..6) { val punta = q + Offset(az.entre(-1f, 1f) * w, -w * az.entre(0.6f, 1.3f)); drawLine((if (j % 3 == 0) p.luz(lerp(C.CUERO, C.ESTEPA, 0.4f), lejos) else p.por(lerp(C.CUERO, C.HOLLIN, 0.3f), -0.3f, lejos)), q, punta, (0.18f * w).coerceIn(0.3f, 0.8f), StrokeCap.Round) }
            }
            // Los afloramientos de roca rota en estratos, con su canchal.
            for (pe in penas) pena(p, pe, nieve, lejos, az)
        }
        // La cresta, recortada contra el cielo (o contra la cima): un filo de luz, o de luna.
        val filo = if (l.noche > 0.4f) C.LUNA.copy(alpha = 0.4f * l.noche * (1f - l.nublado)) else p.luz(lerp(piel, C.FIELTRO, 0.5f), lejos).copy(alpha = 0.6f)
        drawPath(curva(cresta), filo, style = Stroke(if (cerca) 1.1f else 0.8f))
        // La hierba que asoma por la cresta.
        for (i in cresta.indices step 2) { val q = cresta[i]; for (j in 0..1) drawLine(p.por(hierba, -0.2f, lejos).copy(alpha = 1f - 0.6f * nieve), q + Offset(j * 1.6f - 0.8f, 0.4f), q + Offset(j * 1.6f - 0.8f + az.entre(-0.6f, 0.6f), -1.5f), 0.5f) }
    }

    /**
     * Un afloramiento de roca: una masa quebrada con su cara de luz, su cara de sombra y su tapa (con nieve si la hay),
     * las rayas de los estratos y, si tiene, el canchal que cae en abanico debajo: piedras sueltas más claras que la hierba.
     */
    private fun DrawScope.pena(p: Pincel, pe: Pena, nieve: Float, lejos: Float, az: Azar) {
        val w = pe.ancho; val h = w * az.entre(0.45f, 0.65f); val c = pe.pie; val lado = if (p.solALaDerecha) 1f else -1f
        val roca = lerp(C.PIEDRA, C.HOLLIN, 0.25f); val clara = lerp(C.PIEDRA, C.FIELTRO, 0.2f)
        if (pe.canchal) {
            val fan = poligono(listOf(c + Offset(-w * 0.3f, 0f), c + Offset(w * 0.3f, 0f), c + Offset(w * 0.9f + az.entre(0f, 3f), w * 1.6f), c + Offset(0f, w * 1.9f), c + Offset(-w * 0.8f, w * 1.5f)))
            drawPath(fan, p.por(lerp(clara, C.ESTEPA, 0.3f), 0.1f, lejos).copy(alpha = 0.55f))
            val piedras = (0 until (w * 2.2f).toInt()).map { val f = az.real(); c + Offset(az.entre(-1f, 1f) * (w * 0.3f + f * w * 0.7f), 1f + f * w * 1.6f) }
            drawPoints(piedras, PointMode.Points, p.sombra(roca, lejos).copy(alpha = 0.6f), 0.6f + w * 0.05f, StrokeCap.Round)
            drawPoints(piedras.map { it + Offset(lado * 0.25f, -0.3f) }, PointMode.Points, p.luz(clara, lejos), 0.4f + w * 0.035f, StrokeCap.Round)
        }
        // Un montón de tres o cuatro bolos de roca de distinto tamaño, uno encima de otro (como los berrocales de la
        // estepa): cada uno con su cara de luz y su cara de sombra, las grietas, y la nieve solo en alguna repisa.
        val n = 3 + az.entero(2)
        val bolos = (0 until n).map { k ->
            val grande = k == 1
            val r = w * (if (grande) az.entre(0.3f, 0.38f) else az.entre(0.17f, 0.27f))
            val x = (k - (n - 1) / 2f) * w * 0.3f + az.entre(-0.06f, 0.06f) * w
            val y = if (grande) -r * 0.55f else -r * az.entre(0.2f, 0.45f)
            Triple(c + Offset(x, y), r, az.entre(0f, 6.28f))
        }.sortedBy { it.first.y }
        // La sombra del montón, tendida del lado contrario al sol.
        drawPath(borron(c + Offset(-lado * w * 0.3f, h * 0.12f), w * 0.62f, h * 0.22f, az, 8, 0.25f), p.r.color.copy(alpha = 0.45f * p.r.fuerza), blendMode = BlendMode.Multiply)
        for ((q, r, giro) in bolos) {
            val puntos = (0 until 7).map { j -> val a = giro + j * 0.8976f; val rr = r * az.entre(0.78f, 1.08f); q + Offset(kotlin.math.cos(a) * rr, kotlin.math.sin(a) * rr * 0.78f).let { if (it.y > q.y + r * 0.45f) Offset(it.x, q.y + r * 0.45f) else it } }
            drawPath(poligono(puntos), p.por(roca, -0.45f, lejos))
            // La cara del sol: los puntos de ese lado y el centro, un poco más arriba.
            val luz = puntos.filter { (it.x - q.x) * lado > -r * 0.15f }.sortedBy { kotlin.math.atan2(it.y - q.y, it.x - q.x) }
            if (luz.size >= 2) drawPath(poligono(luz + (q + Offset(-lado * r * 0.15f, -r * 0.1f))), p.por(lerp(roca, clara, 0.5f), 0.55f, lejos))
            drawLine(p.sombra(C.HOLLIN, lejos).copy(alpha = 0.5f), q + Offset(-lado * r * 0.1f, -r * 0.6f), q + Offset(lado * r * 0.05f, r * 0.35f), 0.35f + 0.02f * w)
            if (nieve > 0.25f && az.real() < 0.5f) drawPath(borron(q + Offset(lado * r * 0.1f, -r * 0.62f), r * 0.45f, r * 0.12f + 0.2f, az, 6, 0.3f, 0.6f), p.por(C.NIEVE, 0.5f, lejos).copy(alpha = (0.3f + 0.6f * nieve).coerceAtMost(1f)))
        }
    }
}
