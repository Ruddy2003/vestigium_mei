package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaNapoleon.H
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.sin

/**
 * La luz de la retirada, con la lógica de los cuadros aprobados (la luz con forma sobre penumbra de color, que camina
 * con la hora, que se ve en el aire y enciende lo pintado; la noche a dos luces de Renacimiento; la luz de fuego de
 * Prehistoria; la niebla por capas de Horror cósmico):
 *
 * - **El sol bajo del invierno** ([rayo]): sombras azules muy largas, que caen hacia el lado contrario al sol y caminan
 *   con la hora (`EscenaNapoleon.alturaDelSol` lo baja también a mediodía).
 * - **Las franjas de sol entre los troncos** ([franjas]): el bosque echa su sombra sobre la nieve y el camino, y entre
 *   tronco y tronco pasa el sol. Por la mañana el sol viene de la derecha, y son los pinos que quedan fuera del cuadro,
 *   a la derecha, los que tienden sus sombras a través del camino; por la tarde viene de la izquierda, y es el borde
 *   del bosque que se ve el que las tiende sobre el camino. **[Aproximación]**: la sombra de cada pino es su silueta
 *   tumbada como la de todo lo demás (`sombraArrojada`), no su sombra real con la perspectiva del suelo.
 * - **La bruma por capas** ([velo]) cuando nieva, hay niebla o nubes: entre la lejanía, el llano, el bosque y lo de
 *   delante, como en Horror cósmico.
 * - **La noche a dos luces** ([lumbre], [luna], [rayoDeLuna], [lucesDelSuelo]): el fuego del alto es el sol del cuadro:
 *   un círculo cálido sobre la nieve y las sombras de lo que tiene cerca abiertas en abanico desde él; contra la luna,
 *   que platea el llano y el camino y tiende sus propias sombras frías (también las franjas de los troncos). Con el
 *   rescoldo, el círculo es corto, pero la noche no queda gris.
 *
 * Lo que decide dónde cae la luz son funciones sin dibujo, que lee `EscenaNapoleonPrueba`.
 */
internal object RetiradaLuz {
    private val C = ColorNapoleon

    /** El color de la sombra de la luna sobre la nieve: azul hondo. */
    private val SOMBRA_DE_LUNA = Color(0xFF5A66AA)

    /** La luz de la luna (una luz: se pinta tal cual). */
    val LUNA = Color(0xFFC6D4F2)

    /**
     * El sol de la retirada: el de `Rayo`, con la sombra no más larga que tres veces lo alto **[Diseño propio]**: con el
     * sol de noviembre (`EscenaNapoleon.alturaDelSol`), la sombra de los fusiles cruzaría todo el cuadro.
     */
    fun rayo(l: Luces): Rayo {
        val r = Rayo(l); val k = (3f / r.cot.coerceAtLeast(0.01f)).coerceAtMost(1f)
        return if (k >= 1f) r else Rayo(r.dia, r.lat, r.cot * k, r.tan / k, r.ax * k, r.ay * k, r.fuerza, r.color, r.contraste)
    }

    /**
     * Lo que alumbra el fuego del alto a lo que tiene cerca, de 0 a algo más de 1: casi nada de día; del todo de noche;
     * antes si está cubierto o nevando («el fuego se enciende antes y manda»).
     */
    fun lumbre(fuego: Float, l: Luces, cubierto: Float): Float = fuego * (0.12f + 1.12f * maxOf(l.noche, 0.45f * cubierto * (1f - l.noche)))

    /** La luz de la luna, de 0 (de día, sin luna o con el cielo cubierto) a 1. */
    fun luna(l: Luces): Float = if (l.lunaAltura <= 0.05f) 0f else ((l.noche - 0.5f) / 0.3f).coerceIn(0f, 1f) * ((0.6f - l.nublado) / 0.3f).coerceIn(0f, 1f)

    /** El rayo de la luna: tumba sombras frías y suaves hacia el lado contrario. Null si no hay luna. */
    fun rayoDeLuna(l: Luces): Rayo? {
        val k = luna(l); if (k <= 0.02f) return null
        val cruda = ((l.lunaX - 0.5f) * 2.6f).coerceIn(-1f, 1f); val lat = if (abs(cruda) < 0.35f) (if (cruda < 0f) -0.35f else 0.35f) else cruda
        return rayoFijo(lat, 22f + 26f * l.lunaAltura, 0.62f * k, SOMBRA_DE_LUNA, 0.55f)
    }

    /** Con qué se tumban las sombras ahora: el sol si lo hay; si no, la luna; si tampoco, nada. */
    fun deSombras(v: VistaNapoleon): Rayo? = if (v.r.fuerza > 0.05f) v.r else v.rLuna

    // ---------------------------------------------------------------- Las franjas de sol entre los troncos

    /** La sombra de un pino en el suelo, como polígono del lienzo (para pintarla y para saber qué queda dentro). */
    class Franja(val forma: List<Offset>)

    /**
     * Las sombras del bosque sobre la nieve: con el sol (o la luna) a la derecha, las de los pinos que quedan fuera del
     * cuadro, a la derecha; con él a la izquierda, las del borde del bosque. Nada con el cielo cubierto o sin luna.
     */
    fun franjas(v: VistaNapoleon): List<Franja> {
        val r = deSombras(v) ?: return emptyList()
        if (r.fuerza <= 0.05f) return emptyList()
        val arboles = if (r.lat > 0f) RetiradaCamino.pinosDeLaDerecha() else RetiradaCamino.pinosDelBorde()
        return arboles.map { Franja(tumbada(r, it.silueta(), it.pie)) }.filter { f -> f.forma.maxOf { it.x } > -40f && f.forma.minOf { it.x } < ANCHO + 40f && f.forma.maxOf { it.y } > H && f.forma.minOf { it.y } < ALTO + 20f }
    }

    /** Una silueta tumbada en el suelo desde [pie] con el rayo [r]: la misma cuenta que `sombraArrojada`. */
    fun tumbada(r: Rayo, silueta: List<Offset>, pie: Offset): List<Offset> = silueta.map { q -> val h = pie.y - q.y; Offset(q.x + r.ax * h, pie.y + r.ay * h) }

    /** Cuánta sombra del bosque hay en [p], de 0 a 1. */
    fun sombraEn(franjas: List<Franja>, p: Offset): Float = if (franjas.any { dentro(it.forma, p) }) 1f else 0f

    private fun dentro(q: List<Offset>, p: Offset): Boolean {
        var c = false; var j = q.size - 1
        for (i in q.indices) { val a = q[i]; val b = q[j]; if ((a.y > p.y) != (b.y > p.y) && p.x < (b.x - a.x) * (p.y - a.y) / (b.y - a.y) + a.x) c = !c; j = i }
        return c
    }

    // ---------------------------------------------------------------- Las sombras sobre el suelo

    /**
     * La capa de las sombras del suelo, con el sol o la luna: las franjas del bosque y la sombra larga de cada cosa (la
     * carroza, el pino, el cañón, los fusiles, las mochilas…). De noche, antes, la luna platea la nieve. Una sola capa.
     */
    fun DrawScope.sombrasDelSuelo(v: VistaNapoleon) {
        val suelo = RetiradaCamino.sueloForma()
        if (v.luna > 0.02f) plata(v, suelo)
        val r = deSombras(v) ?: return
        val cosas = RetiradaCamino.sombras(v) + RetiradaCerca.sombras(v)
        capaSombra(r) { c ->
            clipPath(suelo) { for (f in v.franjas) drawPath(poligono(f.forma), c) }
            for (s in cosas) sombraArrojada(r, s.forma, s.pie, c, H, H)
        }
    }

    /** La nieve plateada por la luna: más del lado de la luna. Va antes de las sombras de luna, que la cortan en azul hondo. */
    private fun DrawScope.plata(v: VistaNapoleon, suelo: Path) {
        val k = v.luna * (0.35f + 0.65f * v.nieve); if (k <= 0.02f) return
        val derecha = v.l.lunaX > 0.5f
        val cerca = LUNA.copy(alpha = (0.2f * k).coerceIn(0f, 1f)); val lejos = LUNA.copy(alpha = (0.07f * k).coerceIn(0f, 1f))
        drawPath(suelo, Brush.horizontalGradient(0f to (if (derecha) lejos else cerca), 1f to (if (derecha) cerca else lejos), startX = 0f, endX = ANCHO), blendMode = BlendMode.Plus)
    }

    /** Una cosa que tumba sombra en el suelo: su [pie] y su silueta en el lienzo. */
    class Sombra(val pie: Offset, val forma: Path)

    // ---------------------------------------------------------------- La luz del fuego

    /** El fuego del alto como foco: su luz cae sobre lo que tiene cerca y tumba sus sombras lejos de él. */
    fun foco(v: VistaNapoleon): Foco = Foco(RetiradaCerca.LUZ_DEL_FUEGO, C.LUMBRE, v.lumbre, 104f, suelo = RetiradaCerca.FUEGO.y)

    /** Cuánto manda el fuego sobre el sol y la luna, de 0 a 1: decide de quién son las sombras de lo que tiene cerca. */
    fun mandaElFuego(v: VistaNapoleon): Float = (v.lumbre * 1.5f - v.r.fuerza).coerceIn(0f, 1f)

    /**
     * Lo que el fuego deja en el suelo, de noche (y al caer la tarde o con el cielo cubierto): el círculo cálido sobre
     * la nieve, y las sombras de lo que tiene cerca abiertas en abanico desde él; los charcos de luz de los demás fuegos.
     */
    fun DrawScope.lucesDelSuelo(v: VistaNapoleon) {
        val manda = mandaElFuego(v)
        val f = foco(v)
        if (manda > 0.02f) {
            // Las sombras en abanico: cada cosa la suya, lejos del fuego, más flojas cuanto más lejos está.
            val cosas = RetiradaCerca.sombrasDelFuego(v)
            val rf = rayoFijo(1f, 30f, 0.66f * manda, C.SOMBRA)
            capaSombra(rf) { c ->
                for (s in cosas) {
                    val d = (s.pie - f.p).getDistance(); val k = (1f / (1f + d * d / (150f * 150f))).coerceIn(0f, 1f)
                    sombraArrojada(rayoDeFoco(f, s.pie, 1f, c), s.forma, s.pie, c.copy(alpha = k), H, H)
                }
            }
        }
        // El círculo cálido sobre la nieve, que se suma a lo que hay; más ancho cuanto más arde.
        val k = v.lumbre * (0.3f + 0.7f * v.l.noche)
        if (k > 0.02f) {
            val nieve = lerp(C.BARRO, C.NIEVE, v.nieve)
            charcoDeLuz(f, RetiradaCerca.FUEGO + Offset(-6f, 4f), 120f * (0.55f + 0.45f * v.fuego), 34f * (0.55f + 0.45f * v.fuego), nieve, 0.7f * (0.3f + 0.7f * v.l.noche))
            charcoDeLuz(f, RetiradaCerca.FUEGO, 50f, 15f, nieve, 0.35f * (0.4f + 0.6f * v.l.noche))
        }
    }

    /**
     * Lo último de la noche, encima de todo: los rincones lejos del fuego se hunden en azul (como el abrigo de
     * Prehistoria) y la luz de la lumbre sube por lo que tiene cerca.
     */
    fun DrawScope.noche(v: VistaNapoleon) {
        val n = v.l.noche; if (n <= 0.3f) return
        val f = RetiradaCerca.LUZ_DEL_FUEGO
        val hondo = lerp(Color.White, Color(0xFF6A6CA8), 0.5f * (n - 0.3f) / 0.7f)
        drawRect(Brush.radialGradient(0f to Color.White, 0.45f to Color.White, 1f to hondo, center = f, radius = 330f), Offset.Zero, Size(ANCHO, ALTO), blendMode = BlendMode.Multiply)
    }

    // ---------------------------------------------------------------- La bruma por capas

    /**
     * El aire entre los planos cuando nieva, hay niebla, nubes bajas o llueve: una banda de [y0] a [y1] después de cada
     * plano, más espesa abajo (la bruma se asienta en lo hondo) y más espesa detrás que delante. Así cada plano se despega
     * del de atrás aunque no haya sombras.
     */
    fun DrawScope.velo(v: VistaNapoleon, cuanto: Float, y0: Float, y1: Float) {
        val k = when (v.m.ficha.clima) { Clima.NIEVE -> 0.85f; Clima.NIEBLA -> 1f; Clima.NUBES -> 0.32f; Clima.LLUVIA, Clima.TORMENTA -> 0.45f; else -> 0f } * cuanto
        if (k <= 0.01f) return
        val color = lerp(v.l.bruma, v.l.bajo, 0.35f)
        drawRect(Brush.verticalGradient(0f to color.copy(alpha = 0f), 0.35f to color.copy(alpha = 0.55f * k), 0.62f to color.copy(alpha = k), 0.8f to color.copy(alpha = 0.6f * k), 1f to color.copy(alpha = 0f), startY = y0, endY = y1), Offset(0f, y0), Size(ANCHO, y1 - y0))
    }

    // ---------------------------------------------------------------- Lo que se mueve

    /** La ventisca baja: la nieve que el viento arrastra a ras del suelo, en hilos que pasan; el sol, si lo hay, la enciende. */
    fun DrawScope.ventisca(v: VistaNapoleon, reloj: Float) {
        val az = Azar(Semilla.de("napoleon", "ventisca"))
        val c = lerp(v.l.tono(C.NIEVE), Color.White, 0.3f)
        repeat(14) {
            val y = az.entre(170f, 284f); val largo = az.entre(30f, 80f) * (y / 220f); val rapidez = az.entre(40f, 80f)
            val x = ((az.entre(0f, ANCHO + 120f) + reloj * rapidez) % (ANCHO + 120f)) - 60f
            val onda = 1.5f * sin(reloj * 1.7f + y)
            drawLine(c.copy(alpha = 0.18f * (y / 284f)), Offset(x, y + onda), Offset(x + largo, y + onda - 1f), 1.2f * (y / 220f), StrokeCap.Round)
        }
    }

    /** Una mancha de luz en el aire (el aliento, el humo del rescoldo, la ventisca) donde la atraviesa el sol bajo. */
    fun DrawScope.alContraluz(v: VistaNapoleon, c: Offset, rx: Float, ry: Float, k: Float) {
        val bajo = (1f - v.l.solAltura / 0.5f).coerceIn(0f, 1f) * (v.r.fuerza / 0.72f)
        if (bajo * k <= 0.02f) return
        mancha(c, rx, ry, lerp(v.l.solColor, Color(0xFFFFC98A), 0.4f), 0.22f * bajo * k)
    }

    /** Un azar fijo de la luz (lo que no cambia de un día a otro). */
    fun fijo(vararg partes: Any): Azar = Azar(Semilla.de("napoleon", "luz", *partes))
}
