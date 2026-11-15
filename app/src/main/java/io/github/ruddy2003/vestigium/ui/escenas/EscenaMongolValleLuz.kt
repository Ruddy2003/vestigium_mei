package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * La luz del valle de invierno, con la lógica de los interiores aprobados (la capilla de Roma, Scriptorium y
 * Renacimiento) llevada a un sitio al aire libre:
 *
 * - **La sombra grande con su mancha de sol** ([sombraDelValle]). Con el sol bajo, el valle queda en penumbra azul
 *   violeta y el sol solo toca lo alto y una franja por donde le dejan pasar. Por la mañana el sol sale por la derecha,
 *   detrás de las lomas del otro lado del río: el fondo del valle (el campamento y el río) está en su sombra, y la
 *   terraza de Bat, que está más alta, ya tiene sol; la franja entra por el collado de la sierra y cruza el río, el
 *   corral y la carreta. Por la tarde el sol baja detrás del cerro: su sombra sale del pie y avanza sobre el campamento,
 *   la terraza y la tienda de Bat (por la que sube); la franja entra por una vaguada del cerro y cruza las tiendas hasta
 *   la terraza. De 11:00 a 15:00 no hay sombra grande. **[Aproximación]**: no es la sombra real de ese cerro con ese sol,
 *   como `solQueEntra` no es el recorrido real del sol por una ventana; sirve para que cada hora tenga su luz.
 * - **La luz en el aire** ([vaho]): el vaho de la helada sobre el río y entre las tiendas a primera hora, encendido
 *   donde lo atraviesa el sol raso, y el haz que baja del collado con los cristales de hielo brillando en él.
 * - **La luz enciende lo pintado** ([alSol]): lo que tiene color propio (la puerta, el cofre, las tiendas, el ovoo) se
 *   calienta donde le da el sol, con el `Pincel`; en la sombra grande baja y se enfría.
 * - **La noche a dos luces** ([luna], [solDeSombras], [plata], [filoDeLuna]): la lumbre de la puerta, el aro y el
 *   hogar, cálida, y la luna, fría, con su lado plata en lo que mira hacia ella, sus sombras azules sobre la nieve y la
 *   nieve y el hielo plateados donde les da.
 *
 * Lo que decide dónde cae la luz son funciones sin dibujo, que lee `EscenaMongolVallePrueba`.
 */
internal object ValleLuz {
    private val C = ColorMongol

    /** La luz de la luna (una luz: se pinta tal cual). */
    val LUNA = Color(0xFFC6D4F2)

    /** El color de la sombra de la luna sobre la nieve: azul hondo. */
    private val SOMBRA_DE_LUNA = Color(0xFF5A66AA)

    // ---------------------------------------------------------------- La sombra grande y su franja de sol

    /**
     * La sombra grande del valle a una hora: cuánta hay ([cuanta], de 0 a 1, con el sol y el cielo), de qué lado viene
     * ([manana]: de las lomas del otro lado del río; si no, del cerro), hasta dónde llega ([alcance], de 0 a 1) y la
     * franja de sol que la cruza, de [franjaA] (por donde entra) a [franjaB], con su medio ancho en cada punta.
     */
    class SombraDelValle(
        val cuanta: Float, val manana: Boolean, val alcance: Float,
        val franjaA: Offset, val franjaB: Offset, val anchoA: Float, val anchoB: Float,
    ) {
        /** El punto desde el que avanza la sombra y hacia dónde. */
        val origen: Offset get() = if (manana) Offset(392f, 120f) else Offset(120f, 120f)
        val hacia: Offset get() = if (manana) Offset(-0.6f, 0.8f) else Offset(0.447f, 0.894f)

        /** Hasta dónde llega, medido a lo largo de [hacia] desde [origen]. Por la tarde, 85 es el pie del cerro. */
        val frente: Float get() = if (manana) 40f + 230f * alcance else 85f + 200f * alcance

        /** Lo que ha avanzado la sombra hasta [p]. */
        fun s(p: Offset): Float { val d = p - origen; return d.x * hacia.x + d.y * hacia.y }

        /** Cuánto se nota, sin la franja: 0 a mediodía aunque el sol pegue fuerte. */
        val fuerza: Float get() = cuanta * (alcance / 0.12f).coerceIn(0f, 1f)
    }

    /** Cuánto de bajo va el sol, de 0 (de 11:00 a 15:00) a 1 (el sol raso de las 7:00 y de las 19:00); 0 de noche. */
    fun bajo(l: Luces): Float = if (l.solAltura < -0.05f) 0f else ((1.25f - l.solAltura) / 0.8f).coerceIn(0f, 1f)

    /** El sol que hay, de 0 a 1: nada de noche ni con el cielo cubierto. */
    fun sol(l: Luces): Float = (Rayo(l).fuerza / 0.72f).coerceIn(0f, 1f)

    /**
     * La sombra grande del valle con las luces [l] **[Aproximación]** (ver arriba). Con el cielo cubierto (más de la
     * mitad) no hay: no hay sol que la arroje.
     */
    fun sombraDelValle(l: Luces): SombraDelValle {
        val cuanta = sol(l) * ((0.55f - l.nublado) / 0.25f).coerceIn(0f, 1f)
        val b = bajo(l)
        return if (l.solX > 0.6f) {
            // La mañana: la franja baja del collado de la sierra y cruza el río, el corral y la carreta.
            val u = 1f - b
            SombraDelValle(cuanta, true, b, Offset(374f, 135f + 6f * u), Offset(228f + 18f * u, 207f + 7f * u), 5f + 4f * u, 13f + 9f * u)
        } else {
            // La tarde: entra por una vaguada del pie del cerro, que se corre a la izquierda mientras el sol baja.
            val x = 252f - 54f * b
            val a = Offset(x, TrazosValle.alturaEn(ValleCerro.PIE, x) + 2f)
            val d = Offset(0.32f + 0.24f * b, 1f).let { it / it.getDistance() }
            SombraDelValle(cuanta, false, b, a, a + d * 150f, 4f + 2f * b, 18f + 14f * b)
        }
    }

    /** Cuánto está [p] dentro de la franja de sol, de 0 a 1 (con el borde blando). */
    fun enFranja(s: SombraDelValle, p: Offset): Float {
        val ab = s.franjaB - s.franjaA; val largo = ab.getDistance(); val u = ab / largo; val d = p - s.franjaA
        val t = (d.x * u.x + d.y * u.y) / largo
        if (t < -0.05f || t > 1.05f) return 0f
        val w = s.anchoA + (s.anchoB - s.anchoA) * t.coerceIn(0f, 1f)
        return ((w + 3f - abs(d.x * u.y - d.y * u.x)) / 6f).coerceIn(0f, 1f)
    }

    /**
     * Cuánta sombra grande hay en [p], de 0 a 1. [arriba]: si [p] está en la terraza o en la tienda de Bat, más altas
     * que el campamento: la sombra de la mañana no les llega.
     */
    fun sombraEn(s: SombraDelValle, p: Offset, arriba: Boolean = false): Float {
        if (s.fuerza <= 0f || (s.manana && arriba)) return 0f
        return s.fuerza * ((s.frente + 12f - s.s(p)) / 24f).coerceIn(0f, 1f) * (1f - enFranja(s, p))
    }

    /** La franja de sol, como forma. */
    fun formaDeLaFranja(s: SombraDelValle): Path {
        val ab = s.franjaB - s.franjaA; val u = ab / ab.getDistance(); val n = Offset(-u.y, u.x)
        return poligono(s.franjaA + n * s.anchoA, s.franjaB + n * s.anchoB, s.franjaB - n * s.anchoB, s.franjaA - n * s.anchoA)
    }

    /** La orilla de allá del río, de lejos a cerca, hasta el borde del cuadro. */
    private val ALLA: List<Offset> = ValleRio.CORTES.map { it.second }.filter { it.x <= 392f } + Offset(392f, 169.3f)

    /** El fondo del valle: del pie del cerro al talud de la terraza, con el río. Lo que tapa la sombra de la mañana. */
    fun fondo(): Path = poligono(ValleCerro.PIE + ALLA + listOf(Offset(392f, 219.4f)) + ValleCerca.TALUD.reversed())

    /** Todo lo que está más bajo que las lomas de enfrente y el cerro: el fondo del valle y la terraza. */
    fun todo(): Path = poligono(ValleCerro.PIE + ALLA + listOf(Offset(392f, ALTO + 6f), Offset(-10f, ALTO + 6f), Offset(-10f, 222f)))

    /** Donde pisa la tienda de Bat: por ahí empieza a subirle la sombra de la tarde. */
    private val PIE_DE_BAT = Offset(150f, 262f)

    /**
     * Pinta la sombra grande sobre lo que ya hay en [region]: una capa que multiplica (como `capaSombra`), con el frente
     * blando y la franja de sol recortada. Si se pasa la [tienda] de Bat (la tarde), la sombra no la toca por donde está
     * el suelo: le sube desde abajo según la sombra pasa su pie.
     */
    fun DrawScope.sombraGrande(s: SombraDelValle, r: Rayo, region: Path, tienda: Path?) {
        val k = s.fuerza
        if (k <= 0.02f) return
        val c = lerp(r.color, Color.White, 0.14f)
        drawIntoCanvas { tela ->
            tela.saveLayer(Rect(0f, 0f, ANCHO, ALTO), Paint().apply { blendMode = BlendMode.Multiply; alpha = (0.82f * k).coerceIn(0f, 1f) })
            val a = s.origen + s.hacia * (s.frente - 12f); val b = s.origen + s.hacia * (s.frente + 12f)
            val brocha = Brush.linearGradient(0f to c, 1f to c.copy(alpha = 0f), start = a, end = b)
            if (tienda != null) {
                clipPath(tienda, ClipOp.Difference) { drawPath(region, brocha) }
                val sube = ((s.frente - s.s(PIE_DE_BAT)) / 60f).coerceIn(0f, 1f)
                if (sube > 0f) {
                    val y = ALTO + 8f - (ALTO + 24f) * sube
                    clipPath(tienda) { drawRect(Brush.verticalGradient(0f to c.copy(alpha = 0f), 1f to c, startY = y - 16f, endY = y + 16f), Offset(-10f, y - 16f), Size(220f, ALTO + 26f - y)) }
                }
            } else drawPath(region, brocha)
            // La franja: el sol que el cerro (o el collado) deja pasar, con el borde blando.
            val franja = formaDeLaFranja(s)
            drawPath(franja, Color.Black, blendMode = BlendMode.DstOut)
            for ((w, al) in listOf(10f to 0.22f, 5.5f to 0.3f, 2.4f to 0.45f)) drawPath(franja, Color.Black.copy(alpha = al), style = Stroke(w), blendMode = BlendMode.DstOut)
            tela.restore()
        }
    }

    /** La franja de sol encendida sobre la nieve y lo que pisa en ella: brilla más cuanto más bajo va el sol. */
    fun DrawScope.luzDeLaFranja(s: SombraDelValle, l: Luces, region: Path) {
        val k = s.fuerza * (0.4f + 0.6f * s.alcance)
        if (k <= 0.02f) return
        val c = lerp(l.solColor, Color(0xFFFFC98A), 0.45f)
        val franja = formaDeLaFranja(s)
        clipPath(region) {
            drawPath(franja, c.copy(alpha = 0.17f * k), blendMode = BlendMode.Plus)
            for ((w, al) in listOf(9f to 0.025f, 4.5f to 0.035f)) drawPath(franja, c.copy(alpha = al * k), style = Stroke(w), blendMode = BlendMode.Plus)
        }
    }

    // ---------------------------------------------------------------- La luz enciende lo pintado

    /**
     * El pincel para algo pintado (la puerta, el cofre, una tienda) donde le da [luz] de sol (de 0 a 1): su color sube
     * y se calienta, más con el sol bajo. De noche o en la sombra queda como está (la sombra ya lo enfría).
     */
    fun alSol(p: Pincel, luz: Float): Pincel {
        val k = sol(p.l) * luz.coerceIn(0f, 1f) * (0.12f + 0.3f * bajo(p.l))
        if (k <= 0.01f) return p
        return Pincel(p.l, p.r, k, lerp(p.l.solColor, Color(0xFFFFC98A), 0.5f))
    }

    /** Lo mismo, para lo que pisa en [punto] del suelo (con la sombra grande y su franja). */
    fun alSol(p: Pincel, s: SombraDelValle, punto: Offset, arriba: Boolean = false): Pincel =
        alSol(p, (1f - sombraEn(s, punto, arriba)) * (1f + 0.4f * enFranja(s, punto)))

    // ---------------------------------------------------------------- La luz en el aire

    /**
     * Cuánto vaho de la helada hay, de 0 a 1: las mañanas frías y claras, de las 5:30 a las 9:30, más con la nieve, la
     * escarcha y el río helado. Con el cielo cubierto se lo lleva la bruma de siempre.
     */
    fun vaho(m: Momento, l: Luces): Float {
        val frio = maxOf(EscenaMongol.nieve(m), if (EscenaMongol.helada(m)) 0.7f else 0f, if (EscenaMongol.hielo(m) > 0.3f) 0.6f else 0f)
        val h = m.hora.toFloat()
        val hora = when { h < 5.4f || h > 9.6f -> 0f; h < 6.5f -> (h - 5.4f) / 1.1f; h < 8f -> 1f; else -> (9.6f - h) / 1.6f }
        return (frio * hora * ((0.6f - l.nublado) / 0.3f).coerceIn(0f, 1f)).coerceIn(0f, 1f)
    }

    /** Dónde se ve el sol en el cielo (el de `astros`, aproximado: solo para saber de dónde baja el haz). */
    private fun discoDelSol(l: Luces) = Offset(l.solX * ANCHO, ValleMongol.H - l.solAltura * (ValleMongol.H - 8f))

    /**
     * El vaho de la helada: bandas blandas sobre el río y entre las tiendas, frías en la sombra; donde las atraviesa
     * la franja de sol, encendidas. Por encima, el haz que baja del collado hasta la franja, con los cristales de hielo
     * que brillan en él (como `hazDeSol`).
     */
    fun DrawScope.vaho(m: Momento, l: Luces, s: SombraDelValle) {
        val k = vaho(m, l); if (k <= 0.02f) return
        val az = Azar(Semilla.de("mongol", "valle", "vaho"))
        val frio = lerp(l.bajo, l.tono(C.NIEVE), 0.4f)
        val calido = lerp(l.solColor, Color(0xFFFFC98A), 0.4f)
        val rio = ValleRio.CORTES.map { (a, b) -> (a + b) / 2f to (b - a).getDistance() }
        fun bancos(color: Color, alfa: Float, plus: Boolean) {
            for ((c, w) in rio) mancha(c + Offset(0f, -1.5f - w * 0.12f), w * 0.85f + 6f, 2.6f + w * 0.1f, color, alfa)
            // Entre las tiendas: una capa baja que se asienta en lo hondo, más espesa abajo.
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 0.75f to color.copy(alpha = 0.8f * alfa), 1f to color.copy(alpha = 0.3f * alfa), startY = 148f, endY = 214f), Offset(150f, 148f), Size(242f, 66f), blendMode = if (plus) BlendMode.Plus else BlendMode.SrcOver)
        }
        bancos(frio, 0.24f * k, false)
        val sol = sol(l) * s.cuanta
        if (sol > 0.02f) {
            val franja = formaDeLaFranja(s)
            // El vaho que cruza la franja se enciende (y un poco donde ya hay sol: la terraza y lo alto).
            clipPath(franja) { bancos(calido, 0.3f * k * sol, true) }
            // El haz: del collado (o de la vaguada) a la franja, casi invisible salvo por los cristales de hielo.
            val boca = if (s.manana) Offset(306f, 106f) else s.franjaA + Offset(0f, -6f)
            val haz = poligono(boca + Offset(-3f, 0f), boca + Offset(3f, 0f), s.franjaA + Offset(s.anchoA, 0f), s.franjaA - Offset(s.anchoA, 0f))
            drawPath(haz, calido.copy(alpha = 0.06f * k * sol), blendMode = BlendMode.Plus)
            repeat(40) {
                val t = az.real(); val q = boca + (s.franjaA - boca) * t + Offset(az.entre(-1f, 1f) * (2f + s.anchoA * t), az.entre(-1f, 1f))
                drawCircle(calido.copy(alpha = (0.7f * k * sol * az.real()).coerceIn(0f, 1f)), az.entre(0.25f, 0.55f), q, blendMode = BlendMode.Plus)
            }
            // Y los cristales en la franja misma, sobre la nieve.
            repeat(30) {
                val t = az.real(); val ab = s.franjaB - s.franjaA; val u = ab / ab.getDistance(); val n = Offset(-u.y, u.x)
                val q = s.franjaA + ab * t + n * (az.entre(-1f, 1f) * (s.anchoA + (s.anchoB - s.anchoA) * t)) + Offset(0f, -az.entre(0f, 8f))
                drawCircle(Color.White.copy(alpha = (0.6f * k * sol * az.real()).coerceIn(0f, 1f)), az.entre(0.25f, 0.6f), q, blendMode = BlendMode.Plus)
            }
        }
        // Lo que deja el discurrir de la luz por arriba: el disco del sol se ve a través del vaho.
        if (sol > 0.02f && s.manana) mancha(discoDelSol(l), 22f, 22f, calido, 0.12f * k * sol)
    }

    /** El aliento de un animal con frío, a la altura de [hocico]; [hacia] es adonde mira (1 a la derecha). */
    fun DrawScope.aliento(l: Luces, hocico: Offset, hacia: Float, talla: Float, k: Float) {
        if (k <= 0.02f) return
        val c = lerp(l.tono(C.NIEVE), LUNA, 0.25f * l.noche)
        mancha(hocico + Offset(hacia * talla * 0.5f, -talla * 0.2f), talla * 0.7f, talla * 0.4f, c, 0.35f * k)
        mancha(hocico + Offset(hacia * talla * 1.1f, -talla * 0.45f), talla * 0.9f, talla * 0.5f, c, 0.18f * k)
    }

    /** Cuánto se ve el aliento: con frío, a primera hora y de noche. */
    fun frio(m: Momento, l: Luces): Float = (maxOf(EscenaMongol.nieve(m), if (EscenaMongol.helada(m)) 0.6f else 0f) * maxOf(l.noche, if (m.hora < 10.0) 1f else 0.3f)).coerceIn(0f, 1f)

    // ---------------------------------------------------------------- La noche a dos luces

    /** La luz de la luna, de 0 (de día, sin luna o con el cielo cubierto) a 1. */
    fun luna(l: Luces): Float = if (l.lunaAltura <= 0.05f) 0f else ((l.noche - 0.5f) / 0.3f).coerceIn(0f, 1f) * ((0.6f - l.nublado) / 0.3f).coerceIn(0f, 1f)

    /** La lumbre de la tienda de Bat que sale por la puerta y el aro, de 0 a 1: la otra luz de la noche. */
    fun lumbre(m: Momento, l: Luces): Float = l.noche * EscenaMongol.fuegoDentro(m)

    /** El rayo de la luna: tumba sombras frías y suaves hacia el lado contrario. Null si no hay luna. */
    fun rayoDeLuna(l: Luces): Rayo? {
        val k = luna(l); if (k <= 0.02f) return null
        val cruda = ((l.lunaX - 0.5f) * 2.6f).coerceIn(-1f, 1f); val lat = if (abs(cruda) < 0.35f) (if (cruda < 0f) -0.35f else 0.35f) else cruda
        return rayoFijo(lat, 24f + 30f * l.lunaAltura, 0.52f * k, SOMBRA_DE_LUNA, 0.6f)
    }

    /** El sol con que se tumban las sombras arrojadas: de día el de siempre; de noche, el de la luna. */
    fun solDeSombras(sol: Sol3, l: Luces): Sol3 = if (sol.r.fuerza > 0.05f) sol else rayoDeLuna(l)?.let { Sol3(it) } ?: sol

    /**
     * La nieve (o la hierba helada) plateada por la luna en [forma]: más del lado de la luna y menos del otro. Va antes
     * de las sombras de luna, que la cortan en azul hondo.
     */
    fun DrawScope.plata(forma: Path, l: Luces, nieve: Float, fuerza: Float = 1f) {
        val k = luna(l) * fuerza * (0.4f + 0.6f * nieve)
        if (k <= 0.02f) return
        val derecha = l.lunaX > 0.5f
        val cerca = LUNA.copy(alpha = 0.22f * k); val lejos = LUNA.copy(alpha = 0.05f * k)
        drawPath(forma, Brush.horizontalGradient(0f to (if (derecha) lejos else cerca), 1f to (if (derecha) cerca else lejos), startX = 60f, endX = ANCHO), blendMode = BlendMode.Plus)
    }

    /** El filo de luna de lo que tiene la silueta [forma]: una tira fina por dentro del borde que mira a la luna. */
    fun DrawScope.filoDeLuna(forma: Path, l: Luces, ancho: Float = 1.3f, fuerza: Float = 1f) {
        val k = luna(l) * fuerza; if (k <= 0.02f) return
        val dx = if (l.lunaX > 0.5f) -1f else 1f
        clipPath(forma) { translate(dx * ancho * 0.75f, ancho * 0.65f) { drawPath(forma, LUNA.copy(alpha = (0.36f * k).coerceIn(0f, 1f)), style = Stroke(ancho), blendMode = BlendMode.Plus) } }
    }

    /** El filo de luna en lo alto de un muro (de piedra y estiércol): la línea de su canto, del lado de la luna más clara. */
    fun DrawScope.filoDelMuro(canto: List<Offset>, l: Luces, ancho: Float, fuerza: Float = 1f) {
        val k = luna(l) * fuerza; if (k <= 0.02f || canto.size < 2) return
        drawPath(curva(canto), LUNA.copy(alpha = (0.22f * k).coerceIn(0f, 1f)), style = Stroke(ancho), blendMode = BlendMode.Plus)
    }

    /** El filo de luna en lo alto de una rueda de la carreta [c] (su llanta, del lado de arriba). */
    fun DrawScope.filoDeRueda(c: PiezasMongol.Carreta, l: Luces, ancho: Float) {
        val k = luna(l); if (k <= 0.02f) return
        val giro = if (l.lunaX > 0.5f) -0.5f else 0.5f
        for (lado in listOf(-1f, 1f)) {
            val arco = (0..10).map { j -> val a = giro + (j / 10f - 0.5f) * 2.2f; c.s.p(c.eje * lado * 1.04f, c.radio + c.radio * cos(a), c.zEje + c.radio * sin(a)) }
            drawPath(curva(arco), LUNA.copy(alpha = (0.45f * k).coerceIn(0f, 1f)), style = Stroke(ancho), blendMode = BlendMode.Plus)
        }
    }

    /**
     * El lomo de un caballo lejano plateado por la luna: una raya corta a la altura de la cruz, del lado de la grupa
     * a la cruz. A ese tamaño no hace falta recortar la silueta (y cuesta: son muchos).
     */
    fun DrawScope.lomoDeLuna(pie: Offset, alto: Float, mira: Float, pasta: Boolean, l: Luces) {
        val k = luna(l); if (k <= 0.02f) return
        val u = alto * 0.95f / 10f; val y = pie.y - 9.4f * u + (if (pasta) 0.4f * u else 0f)
        drawLine(LUNA.copy(alpha = (0.4f * k).coerceIn(0f, 1f)), Offset(pie.x - 3.6f * u * mira, y + 0.2f * u), Offset(pie.x + 2.6f * u * mira, y), 0.55f * u, StrokeCap.Round, blendMode = BlendMode.Plus)
    }

    /**
     * La hora azul: después de ponerse el sol queda resplandor en el horizonte; un halo detrás de la cresta del cerro
     * la recorta (y al ovoo) contra el cielo. De 0 a 1 según la hora.
     */
    fun horaAzul(m: Momento, l: Luces): Float {
        val h = m.hora.toFloat()
        val k = when { h < 20.2f || h > 22.4f -> 0f; h < 20.8f -> (h - 20.2f) / 0.6f; else -> (22.4f - h) / 1.6f }
        return k * ((0.7f - l.nublado) / 0.4f).coerceIn(0f, 1f)
    }

    /** El halo de la hora azul detrás de [cresta], antes de pintar el cerro (que tapa la mitad de abajo). */
    fun DrawScope.haloDeLaHoraAzul(m: Momento, l: Luces, cresta: List<Offset>) {
        val k = horaAzul(m, l); if (k <= 0.02f) return
        val c = lerp(l.bajo, Color(0xFFFFA070), 0.35f)
        for ((w, a) in listOf(34f to 0.05f, 20f to 0.07f, 9f to 0.1f)) drawPath(curva(cresta), c.copy(alpha = a * k), style = Stroke(w), blendMode = BlendMode.Plus)
    }
}
