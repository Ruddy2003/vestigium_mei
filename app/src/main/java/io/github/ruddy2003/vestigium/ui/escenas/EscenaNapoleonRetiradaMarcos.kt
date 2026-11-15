package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Fase
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.Carroza
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.armon
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.caja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.canon
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.carroza
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.cilindro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.cofre
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.espejo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.nieveEncima
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.pino
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.ramaGrande
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.rueda
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.ruedaSuelta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.tienda
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaNapoleon.MIRADA
import io.github.ruddy2003.vestigium.ui.escenas.TramosRetirada.Marco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los marcos de la retirada: lo grande de la izquierda, cortado por el borde, que cambia de un tramo a otro
 * ([TramosRetirada.Marco]). Cada marco pone sus cosas en el suelo (que se ordenan por hondo con las del alto, en
 * [RetiradaCerca.primerPlano]), lo que entra por arriba ([arriba]), lo que tumba sombra ([sombras]) y el sitio alto
 * del gato ([mirador]). Ninguno pasa de x ≈ 205 junto al alto (allí está el asta) ni tapa la zona de los astros: lo
 * alto se queda pegado al borde o entra por la esquina de arriba.
 */
internal object MarcosRetirada {
    private val C = ColorNapoleon

    fun marcoDe(v: VistaNapoleon): Marco = TramosRetirada.marco(if (v.vivac) null else v.tramo)

    // ---------------------------------------------------------------- Los sitios

    /** El pino grande: su pie, cortado por el borde de la izquierda. */
    val PIE_DEL_PINO = Offset(9f, 214f)
    private val AZ_PINO = Semilla.de("napoleon", "pino", "grande")

    private val S_COFRE = PiezasNapoleon.sitio(MIRADA, Offset(134f, 248f), -0.5f)
    private val S_ESPEJO = PiezasNapoleon.sitio(MIRADA, Offset(182f, 256f), -0.45f)
    private val S_RUEDA = PiezasNapoleon.sitio(MIRADA, Offset(6f, 302f), 0f)
    private val S_CARROZA = PiezasNapoleon.sitio(MIRADA, Offset(22f, 212f), -0.98f)
    private val S_TIENDA = PiezasNapoleon.sitio(MIRADA, Offset(78f, 184f), 0.55f)

    /** El espejo de la salida, plantado en el suelo del gato. */
    val ESPEJO: Offset get() = S_ESPEJO.pie

    /** La casa quemada: la esquina de delante de la pared que da al camino (sus ejes, los del mundo: x a la derecha, z al fondo). */
    private val S_CASA = PiezasNapoleon.sitio(MIRADA, RetiradaCamino.w(-3.1f, 9.2f), 0f)
    /** El cuadro tirado y el candelabro, en el camino, delante de la casa. */
    private val S_CUADRO = PiezasNapoleon.sitio(MIRADA, Offset(150f, 258f), 0.5f)
    private val S_CANDELABRO = PiezasNapoleon.sitio(MIRADA, Offset(186f, 268f), -0.3f)

    /** El cañón de la nieve, en la orilla izquierda del camino, y su armón detrás, en la cuneta. */
    private val S_CANON = PiezasNapoleon.sitio(MIRADA, Offset(78f, 232f), -0.85f)
    private const val LADEO_CANON = 0.2f
    private val S_ARMON = PiezasNapoleon.sitio(MIRADA, Offset(12f, 200f), 0.5f)

    /** Krasnoi: por dónde se tronchó el pino, y el cajón de munición reventado, delante, en la orilla del camino. */
    private val ROTURA = Offset(12f, 62f)
    private val S_CAJON = PiezasNapoleon.sitio(MIRADA, Offset(92f, 240f), 0.45f)

    /** El Berezina: la pila de troncos de los pontoneros (las testas miran a quien mira) y el caballete de repuesto. */
    private val S_PILA = PiezasNapoleon.sitio(MIRADA, RetiradaCamino.w(-2.25f, 9.6f), 0f)
    private val S_CABALLETE = PiezasNapoleon.sitio(MIRADA, RetiradaCamino.w(-3.6f, 12.6f), 0.25f)

    // ---------------------------------------------------------------- Lo que cuenta (lo vigila una prueba)

    /** El sitio alto del gato, en lo alto del marco de hoy. */
    fun mirador(mc: Marco): Offset = when (mc) {
        Marco.CARROZA, Marco.TIENDA -> carrozaDe(0f).let { c -> c.s.p(c.t(V3(c.w, 1.75f, 0.8f))) }
        Marco.CASA -> S_CASA.p(0.05f, 1.72f, 5.0f)
        Marco.CANON -> S_CANON.p(Vec.girarZ(V3(0f, 1.2f, 0.1f), LADEO_CANON))
        Marco.METRALLA -> S_CAJON.p(-0.9f, 0.9f, 0.6f)
        Marco.MADERA -> S_PILA.p(-0.25f, 1.15f, 0.4f)
        Marco.PINO -> Offset(92f, 226f)
    }.let { Offset(it.x.coerceIn(12f, 360f), it.y.coerceIn(30f, 260f)) }

    /** Lo plantado en el suelo del gato por el marco (el espejo de la salida). */
    fun estorbos(mc: Marco): List<Float> = if (mc == Marco.CARROZA) listOf(ESPEJO.x) else emptyList()

    private fun carrozaDe(nieve: Float) = Carroza(S_CARROZA, nieve)

    // ---------------------------------------------------------------- Las sombras

    /** Lo que el marco de hoy tumba con el sol o la luna (y, lo que está cerca, con el fuego). */
    fun sombras(v: VistaNapoleon): List<RetiradaLuz.Sombra> = buildList {
        val mc = marcoDe(v)
        if (TramosRetirada.conPinoGrande(mc) || mc == Marco.METRALLA) add(RetiradaLuz.Sombra(PIE_DEL_PINO, poligono(PIE_DEL_PINO + Offset(-20f, 0f), PIE_DEL_PINO + Offset(22f, 0f), Offset(8f, if (mc == Marco.METRALLA) ROTURA.y else -40f), Offset(-12f, if (mc == Marco.METRALLA) ROTURA.y else -40f))))
        when (mc) {
            Marco.CARROZA -> {
                add(RetiradaLuz.Sombra(S_CARROZA.pie, PiezasNapoleon.siluetaDeCarroza(carrozaDe(v.nieve))))
                add(RetiradaLuz.Sombra(S_COFRE.pie, silueta(S_COFRE, 0.45f, 0.62f)))
                add(RetiradaLuz.Sombra(S_ESPEJO.pie, silueta(S_ESPEJO, 0.26f, 0.7f)))
            }
            Marco.TIENDA -> add(RetiradaLuz.Sombra(S_TIENDA.pie, silueta(S_TIENDA, 1.4f, 2.6f)))
            Marco.CASA -> {
                add(RetiradaLuz.Sombra(S_CASA.p(0f, 0f, 3.5f), poligono(listOf(S_CASA.p(0f, 0f, 0f), S_CASA.p(0f, 0f, 7f)) + PERFIL_CASA.reversed().map { (z, h) -> S_CASA.p(0f, h, z) })))
                add(RetiradaLuz.Sombra(S_CASA.p(-0.9f, 0f, 4.5f), poligono(S_CASA.p(-1.18f, 0f, 4.5f), S_CASA.p(-0.62f, 0f, 4.5f), S_CASA.p(-0.62f, 4.6f, 4.5f), S_CASA.p(-1.18f, 4.6f, 4.5f))))
            }
            Marco.CANON -> {
                add(RetiradaLuz.Sombra(S_CANON.pie, silueta(S_CANON, 1.0f, 1.3f)))
                add(RetiradaLuz.Sombra(S_ARMON.pie, silueta(S_ARMON, 0.9f, 1.15f)))
            }
            Marco.METRALLA -> add(RetiradaLuz.Sombra(S_CAJON.pie, silueta(S_CAJON, 0.7f, 0.6f)))
            Marco.MADERA -> {
                add(RetiradaLuz.Sombra(S_PILA.pie, poligono(S_PILA.p(-0.8f, 0f, 0f), S_PILA.p(0.8f, 0f, 0f), S_PILA.p(0.3f, 1.15f, 0f), S_PILA.p(-0.3f, 1.15f, 0f))))
                add(RetiradaLuz.Sombra(S_CABALLETE.pie, poligono(S_CABALLETE.p(-0.95f, 0f, 0f), S_CABALLETE.p(0.95f, 0f, 0f), S_CABALLETE.p(0.25f, 2.5f, 0f), S_CABALLETE.p(-0.25f, 2.5f, 0f))))
            }
            Marco.PINO -> Unit
        }
    }

    private fun silueta(s: Sitio, ancho: Float, alto: Float): Path = poligono(listOf(s.p(-ancho, 0f, 0f), s.p(ancho, 0f, 0f), s.p(ancho * 0.85f, alto, 0f), s.p(-ancho * 0.85f, alto, 0f)))

    // ---------------------------------------------------------------- Las cosas del suelo

    /** Pone las cosas del marco de hoy en la lista del primer plano, cada una con su hondo. */
    fun cosas(v: VistaNapoleon, cosas: MutableList<Pair<Float, DrawScope.() -> Unit>>) {
        val nieve = v.nieve
        val mc = marcoDe(v)
        if (TramosRetirada.conPinoGrande(mc)) cosas += parte(PIE_DEL_PINO.y) { troncoDelPino(v) }
        when (mc) {
            Marco.CARROZA -> {
                cosas += parte(S_CARROZA.pie.y) {
                    val c = carrozaDe(nieve)
                    carroza(c, v.en(S_CARROZA.pie))
                    plataDeLuna(v, PiezasNapoleon.siluetaDeCarroza(c), 0f, 150f, 0.16f)
                }
                cosas += parte(S_COFRE.pie.y) { cofre(S_COFRE, v.en(S_COFRE.pie), 1f, 0f, nieve) }
                cosas += parte(S_ESPEJO.pie.y) { espejo(S_ESPEJO, v.en(S_ESPEJO.pie), v.l.alto, v.l.bajo, 0f, nieve) }
                cosas += parte(ALTO + 2f) { ruedaSuelta(S_RUEDA, v.en(S_RUEDA.pie), nieve) }
            }
            Marco.TIENDA -> cosas += parte(S_TIENDA.pie.y) { tienda(S_TIENDA, v.en(S_TIENDA.pie), if (v.noche > 0.4f && v.fase != Fase.MARCHA) 1f else 0f, nieve) }
            Marco.CASA -> {
                cosas += parte(S_CASA.pie.y) { casa(v) }
                cosas += parte(S_CUADRO.pie.y) { cuadroTirado(v) }
                cosas += parte(S_CANDELABRO.pie.y) { candelabro(v) }
            }
            Marco.CANON -> {
                cosas += parte(S_ARMON.pie.y) { val a = v.en(S_ARMON.pie); armon(S_ARMON, a, nieve); hundido(v, S_ARMON, 1.5f, 0.5f) }
                cosas += parte(S_CANON.pie.y) {
                    val a = v.en(S_CANON.pie)
                    val sil = canon(S_CANON, a, LADEO_CANON, nieve)
                    hundido(v, S_CANON, 1.7f, 1.2f)
                    plataDeLuna(v, sil, 0f, 200f, 0.12f)
                }
            }
            Marco.METRALLA -> {
                cosas += parte(PIE_DEL_PINO.y) { troncoTronchado(v); astillasYRamas(v) }
                cosas += parte(S_CAJON.pie.y) { cajonReventado(v) }
            }
            Marco.MADERA -> {
                cosas += parte(S_CABALLETE.pie.y) { caballete(v) }
                cosas += parte(S_PILA.pie.y) { pila(v) }
            }
            Marco.PINO -> cosas += parte(ALTO) { ventisquero(v) }
        }
    }

    /**
     * Lo que entra por arriba: las ramas bajas del pino grande, cargadas de nieve, sin tapar el sol de la mañana ni la
     * luna (en el último tramo, más cargadas y más caídas); en Krasnoi, la rama del pino de al lado, que entra por la
     * esquina. La casa y la madera no tienen nada arriba: su marco es lo alto que sube del suelo (la chimenea, el
     * caballete).
     */
    fun DrawScope.arriba(v: VistaNapoleon) {
        val mc = marcoDe(v)
        val a = v.en(Offset(80f, 40f))
        if (TramosRetirada.conPinoGrande(mc)) {
            val honda = mc == Marco.PINO
            pino(a, PIE_DEL_PINO, 760f, 430f, v.nieve, Azar(AZ_PINO), 0f, 1f, false, hastaY = 30f, caidaPropia = 0.17f + 0.12f * v.nieve + (if (honda) 0.08f else 0f), grande = true)
        }
        if (mc == Marco.METRALLA) {
            val az = Azar(Semilla.de("napoleon", "pino", "vecino"))
            val lado = if (v.p.solALaDerecha) 1f else -1f
            ramaGrande(a, Offset(-14f, 10f), 1f, 150f, 46f, 0.22f + 0.1f * v.nieve, v.nieve, az, 0f, lado)
            ramaGrande(a, Offset(-14f, -14f), 1f, 196f, 52f, 0.12f + 0.1f * v.nieve, v.nieve, az, 0f, lado)
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: VistaNapoleon, reloj: Float) {
        when (marcoDe(v)) {
            // La casa todavía humea por dentro: dos hilos de humo de entre los troncos caídos.
            Marco.CASA -> {
                humo(v.l, S_CASA.p(-1.6f, 0.6f, 2.4f), 70f, 7f, reloj, v.m.viento + 0.2f, 0.4f, 0.4f, 71, 6, 0.4f)
                humo(v.l, S_CASA.p(-0.8f, 0.4f, 6.2f), 50f, 5f, reloj, v.m.viento + 0.2f, 0.4f, 0.4f, 72, 5, 0.3f)
            }
            // El cajón reventado todavía echa un hilo de humo negro.
            Marco.METRALLA -> humo(v.l, S_CAJON.p(0.1f, 0.3f, 0f), 60f, 6f, reloj, v.m.viento + 0.2f, 0.5f, 0.3f, 73, 6, 0.4f)
            Marco.CARROZA, Marco.CANON, Marco.MADERA, Marco.PINO, Marco.TIENDA -> Unit
        }
    }

    // ================================================================= El pino grande

    /** El tronco del pino grande, con su corteza, su lado de luz, la nieve pegada del lado del viento y los muñones de las ramas muertas. */
    private fun DrawScope.troncoDelPino(v: VistaNapoleon, hastaY: Float = -10f) {
        val a = v.en(PIE_DEL_PINO); val p = PIE_DEL_PINO
        val tronco = Path().apply { moveTo(p.x - 24f, p.y + 2f); cubicTo(p.x - 18f, p.y - 10f, p.x - 15f, p.y - 60f, p.x - 13f, hastaY); lineTo(p.x + 13f, hastaY); cubicTo(p.x + 15f, p.y - 60f, p.x + 18f, p.y - 12f, p.x + 26f, p.y + 2f); close() }
        // El tronco como un cuerpo redondo: la sombra del lado contrario a la luz, la luz que rebota en el canto.
        val corteza = lerp(C.MADERA_OSC, C.ROJO, 0.12f)
        val lado = if (a.p.solALaDerecha) 1f else -1f
        val izq = a.plano(corteza, p, -1f); val der = a.plano(lerp(corteza, C.MADERA, 0.35f), p, 1f, 0f, 0.6f); val mid = a.plano(corteza, p, 0f)
        drawPath(tronco, Brush.horizontalGradient(0f to (if (lado > 0f) izq else der), 0.25f to mid, 0.7f to (if (lado > 0f) der else izq), 1f to lerp(izq, mid, 0.5f), startX = p.x - 24f, endX = p.x + 26f))
        clipPath(tronco) {
            // La corteza en escamas: placas redondeadas, alternadas, cada una con su filo de luz arriba y su grieta oscura
            // alrededor; más chicas y rojizas arriba, más anchas y grises abajo.
            val az = Azar(AZ_PINO + 1)
            var y = -10f
            while (y < p.y) {
                val alto = az.entre(9f, 15f) * (0.7f + 0.5f * (y / p.y).coerceIn(0f, 1f))
                var x = p.x - 22f + az.entre(-3f, 3f)
                while (x < p.x + 24f) {
                    val ancho = az.entre(5f, 9f); val c = Offset(x + ancho / 2f, y + alto / 2f + az.entre(-2f, 2f))
                    val u = ((c.x - (p.x - 18f)) / 40f).coerceIn(0f, 1f); val luz = if (lado > 0f) u else 1f - u
                    val placa = TrazosValle.blando(c, ancho * 0.48f, alto * 0.46f, az, 8, 0.18f, 0.85f)
                    val col = lerp(lerp(a.plano(corteza, c, -1f), a.plano(lerp(corteza, C.MADERA, 0.4f), c, 1f, 0f, 0.6f), luz), a.plano(lerp(C.CAPOTE, corteza, 0.5f), c, 0f), (c.y / p.y).coerceIn(0f, 1f) * 0.4f)
                    drawPath(placa, col)
                    drawPath(placa, mult(col, C.SOMBRA, 0.55f), style = Stroke(0.9f))
                    drawLine(lerp(col, Color.White, 0.15f + 0.2f * luz), c + Offset(-ancho * 0.3f, -alto * 0.36f), c + Offset(ancho * 0.25f, -alto * 0.4f), 0.6f, StrokeCap.Round)
                    x += ancho * az.entre(0.85f, 1.05f)
                }
                y += alto * az.entre(0.75f, 0.95f)
            }
            // La nieve pegada en la corteza, del lado del viento, y la resina.
            if (v.nieve > 0.3f) repeat(10) { val y = az.entre(maxOf(20f, hastaY + 10f), p.y - 10f); mancha(Offset(p.x - 12f, y), 4f, az.entre(5f, 12f), a.plano(C.NIEVE, Offset(p.x - 12f, y), -0.6f), 0.7f * v.nieve) }
            mancha(Offset(p.x + 8f, 150f), 2f, 5f, a.plano(lerp(C.LATON, C.MADERA, 0.4f), p, 0.5f), 0.6f)
        }
        // La raíz que asoma y la nieve al pie, arrimada.
        if (v.nieve > 0.2f) drawPath(TrazosValle.blando(p + Offset(2f, -2f), 34f, 7f, Azar(AZ_PINO + 2), 9, 0.2f, 0.5f), a.arriba(C.NIEVE, p))
        else drawPath(TrazosValle.blando(p + Offset(2f, -1f), 30f, 5f, Azar(AZ_PINO + 2), 9, 0.2f, 0.5f), a.arriba(lerp(C.HOJA, C.BARRO, 0.4f), p))
    }

    /**
     * La plata de la luna sobre una cosa grande que queda lejos del fuego: un lavado frío y claro sobre su [forma], de
     * [x0] a [x1], más fuerte del lado de la luna, para que de noche se lea fuera del círculo del fuego.
     */
    private fun DrawScope.plataDeLuna(v: VistaNapoleon, forma: Path, x0: Float, x1: Float, fuerza: Float) {
        val k = (fuerza * v.luna).coerceIn(0f, 1f); if (k <= 0.01f) return
        val haciaLaLuna = v.l.lunaX > 0.5f
        val fuerte = RetiradaLuz.LUNA.copy(alpha = k); val flojo = RetiradaLuz.LUNA.copy(alpha = k * 0.3f)
        clipPath(forma) {
            drawRect(Brush.horizontalGradient(if (haciaLaLuna) listOf(flojo, fuerte) else listOf(fuerte, flojo), x0, x1), Offset(x0, 0f), Size(x1 - x0, ALTO), blendMode = BlendMode.Plus)
        }
    }

    /** La nieve amontonada contra el pie de una cosa pesada ([s]), que la hunde: un lomo blando de [ancho] y [fondo] metros, con su canto de luz. */
    private fun DrawScope.hundido(v: VistaNapoleon, s: Sitio, ancho: Float, fondo: Float) {
        if (v.nieve < 0.2f) return
        val a = v.en(s.pie); val az = Azar(Semilla.de("napoleon", "hundido", s.pie.x.toInt()))
        val pts = (0..10).map { i -> val t = i / 10f; s.p(-ancho / 2f + ancho * t, 0.12f * v.nieve * sin(PI.toFloat() * t) * az.entre(0.6f, 1.2f), -fondo / 2f + az.entre(-0.1f, 0.1f)) }
        drawPath(TrazosValle.cerrada(pts + pts.reversed().map { it + Offset(0f, 0.12f * s.k) }), a.arriba(C.NIEVE, s.pie))
        drawPath(curva(pts), a.arriba(lerp(C.NIEVE, Color.White, 0.35f), s.pie), style = Stroke((0.025f * s.k).coerceIn(0.4f, 2f), cap = StrokeCap.Round))
    }

    // ================================================================= La casa quemada

    /** Lo que queda de la pared que da al camino: su alto (en metros) a lo largo del fondo (z), comida por el fuego desde arriba. */
    private val PERFIL_CASA = listOf(0f to 2.5f, 0.45f to 2.3f, 0.9f to 1.7f, 1.6f to 1.45f, 2.4f to 1.05f, 3.3f to 0.9f, 4.1f to 1.25f, 4.9f to 1.7f, 5.6f to 1.85f, 6.3f to 1.6f, 7f to 2.05f)

    private fun altoDeLaPared(z: Float): Float {
        val p = PERFIL_CASA
        for (i in 0 until p.size - 1) if (z <= p[i + 1].first) { val t = (z - p[i].first) / (p[i + 1].first - p[i].first); return p[i].second + (p[i + 1].second - p[i].second) * t }
        return p.last().second
    }

    /**
     * Lo que queda de una casa de madera quemada junto al camino, de cerca: la pared que da al camino, de troncos
     * redondos, cada uno con su lado de luz y su junta, comida por el fuego desde arriba (negra y cuarteada en el borde,
     * con lenguas de hollín que bajan), con el hueco de la ventana; las cabezas de los troncos en la esquina; por encima
     * de lo que queda, la estufa encalada y su chimenea de ladrillo, en pie y tiznada; un cabio del tejado que sigue en
     * pie, roto; la viga del caballete caída de la pared al camino, y troncos quemados por el suelo. Sin nieve (es el
     * camino viejo de antes de la nieve) salvo la primera nevada, que blanquea lo de arriba.
     */
    private fun DrawScope.casa(v: VistaNapoleon) {
        val s = S_CASA; val az = Azar(Semilla.de("napoleon", "casa"))
        val a = v.en(s.p(0f, 0f, 3f))
        val nieve = v.nieve
        // ---- La estufa y la chimenea, detrás de la pared (dentro de la casa).
        val cal = lerp(C.LONA, C.NIEVE, 0.35f)
        val estufa = caja(s, a, -2.3f, -0.45f, 0f, 1.85f, 3.4f, 5.1f, cal)
        for ((c3, f) in estufa) clipPath(f) {
            mancha(s.p(c3.en(0.5f, 0.95f)), 0.5f * s.k, 0.2f * s.k, a.cara(C.CENIZA, s.normal(c3.n), s.p(c3.centro)), 0.6f)
            if (!c3.arriba) mancha(s.p(c3.en(0.45f, 0.45f)), 0.22f * s.k, 0.18f * s.k, a.cara(C.CENIZA, s.normal(c3.n), s.p(c3.centro)), 0.85f)
        }
        val ladrillo = lerp(C.ROJO, C.MADERA, 0.45f)
        val chim = caja(s, a, -1.18f, -0.62f, 1.85f, 4.6f, 4.22f, 4.78f, ladrillo, arriba = C.CENIZA)
        for ((c3, f) in chim) clipPath(f) {
            if (c3.arriba) return@clipPath
            val col = a.cara(ladrillo, s.normal(c3.n), s.p(c3.centro))
            // Las hiladas de ladrillo, con la llaga a tresbolillo; el encalado que queda abajo; el hollín arriba.
            for (j in 0 until 38) {
                val vv = j / 38f; linea(s, c3, 0f, vv, 1f, vv, mult(col, C.SOMBRA, 0.4f), (0.012f * s.k).coerceAtLeast(0.25f))
                for (u in listOf(0.25f, 0.75f)) { val uu = if (j % 2 == 0) u else u - 0.25f; linea(s, c3, uu, vv, uu, vv + 1f / 38f, mult(col, C.SOMBRA, 0.4f), (0.01f * s.k).coerceAtLeast(0.2f)) }
            }
            val y0 = s.p(c3.en(0.5f, 0f)).y; val y1 = s.p(c3.en(0.5f, 1f)).y
            drawRect(Brush.verticalGradient(0f to a.cara(C.CENIZA, s.normal(c3.n), s.p(c3.centro)), 0.35f to Color.Transparent, 0.7f to Color.Transparent, 1f to a.cara(cal, s.normal(c3.n), s.p(c3.centro)).copy(alpha = 0.7f), startY = y1, endY = y0), Offset(-10f, y1), Size(300f, y0 - y1))
            mancha(s.p(c3.en(0.3f, 0.2f)), 0.08f * s.k, 0.3f * s.k, a.cara(cal, s.normal(c3.n), s.p(c3.centro)), 0.5f)
        }
        if (nieve > 0.1f) nieveEncima(a, listOf(s.p(-2.3f, 1.85f, 3.4f), s.p(-1.4f, 1.85f, 3.4f), s.p(-0.45f, 1.85f, 3.4f)).sortedBy { it.x }, (0.1f * s.k * nieve).coerceAtMost(4f), az)
        // ---- El cabio que sigue en pie al fondo, roto, y el otro, caído hacia dentro.
        run {
            val q0 = s.p(0f, 2.0f, 6.85f); val q1 = s.p(-1.35f, 3.25f, 6.85f)
            drawLine(a.plano(lerp(C.CENIZA, C.MADERA_OSC, 0.3f), q0, -0.5f), q0, q1, (0.13f * s.escala(0f, 6.85f)).coerceAtLeast(1f), StrokeCap.Round)
            drawLine(a.plano(lerp(C.CENIZA, C.MADERA, 0.3f), q0, 0.7f), q0 + Offset(0.6f, -0.4f), q1 + Offset(0.6f, -0.4f), (0.04f * s.escala(0f, 6.85f)).coerceAtLeast(0.4f), StrokeCap.Round)
            // La punta quemada, astillada.
            for (j in 0 until 3) { val d = Offset(az.entre(-2.5f, 0.5f), az.entre(-4f, -1.5f)); drawLine(a.plano(C.CENIZA, q1, 0f), q1, q1 + d, 0.8f, StrokeCap.Round) }
            val r0 = s.p(-0.2f, 1.6f, 6.4f); val r1 = s.p(-1.9f, 0.3f, 6.0f)
            drawLine(a.plano(C.CENIZA, r0, 0f), r0, r1, (0.12f * s.escala(0f, 6.2f)).coerceAtLeast(1f), StrokeCap.Round)
        }
        // ---- La pared que da al camino.
        val base0 = s.p(0f, 0f, 0f); val base1 = s.p(0f, 0f, 7f)
        val borde = PERFIL_CASA.map { (z, h) -> s.p(0f, h, z) }
        val forma = poligono(listOf(base0) + borde + listOf(base1))
        val madera = lerp(C.MADERA_GRIS, C.MADERA, 0.25f)
        val nPared = V3(1f, 0f, 0f)
        drawPath(forma, a.cara(madera, s.normal(nPared), centroDe(borde)))
        clipPath(forma) {
            // Los troncos redondos, uno sobre otro (0.24 m): la mitad de arriba mira al cielo, la de abajo al suelo.
            val dz = 0.24f
            var y = 0f; var i = 0
            while (y < 2.6f) {
                val arribaLog = (0..14).map { j -> s.p(0f, y + dz, 7f * j / 14f) }
                val medioLog = (0..14).map { j -> s.p(0f, y + dz * 0.5f, 7f * j / 14f) }
                val abajoLog = (0..14).map { j -> s.p(0f, y, 7f * j / 14f) }
                val tinte = lerp(madera, C.MADERA_GRIS, 0.15f * ((i * 7) % 3))
                drawPath(poligono(arribaLog + medioLog.reversed()), a.cara(tinte, s.normal(Vec.unidad(V3(1f, 0.65f, 0f))), medioLog[7]))
                drawPath(poligono(medioLog + abajoLog.reversed()), a.cara(tinte, s.normal(Vec.unidad(V3(1f, -0.55f, 0f))), medioLog[7]))
                // La junta oscura entre tronco y tronco, con su musgo de calafatear.
                drawPath(curva(abajoLog), mult(a.cara(madera, s.normal(nPared), abajoLog[7]), C.SOMBRA, 0.6f), style = Stroke((0.03f * s.escala(0f, 3f)).coerceAtLeast(0.5f)))
                drawPath(curva(medioLog.map { it + Offset(0f, -0.06f * s.escala(0f, 3f)) }), lerp(a.cara(tinte, s.normal(Vec.unidad(V3(1f, 0.8f, 0f))), medioLog[7]), Color.White, 0.12f).copy(alpha = 0.55f), style = Stroke((0.012f * s.escala(0f, 3f)).coerceAtLeast(0.3f)))
                // Las grietas de la madera seca, a lo largo.
                repeat(3) { val z0 = az.entre(0.2f, 6.4f); val q0 = s.p(0f, y + dz * az.entre(0.35f, 0.75f), z0); val q1 = s.p(0f, y + dz * az.entre(0.35f, 0.75f), z0 + az.entre(0.3f, 0.8f)); drawLine(mult(a.cara(tinte, s.normal(nPared), q0), C.SOMBRA, 0.45f), q0, q1, 0.5f) }
                y += dz; i++
            }
            // El hueco de la ventana, con su marco quemado (los tablones de alrededor), y lo negro de dentro.
            val vq = listOf(s.p(0f, 0.85f, 5.25f), s.p(0f, 0.85f, 6.0f), s.p(0f, 1.5f, 6.0f), s.p(0f, 1.5f, 5.25f))
            drawPath(poligono(vq), a.plano(lerp(C.CENIZA, Color.Black, 0.3f), centroDe(vq), 0f))
            drawPath(poligono(vq), a.cara(lerp(C.CENIZA, C.MADERA_GRIS, 0.4f), s.normal(nPared), centroDe(vq)), style = Stroke((0.06f * s.escala(0f, 5.6f)).coerceAtLeast(0.8f)))
            // Lo quemado: desde el borde de arriba, el negro que se apaga hacia abajo (el hollín), tramo a tramo, y en el
            // filo el carbón cuarteado en escamas; algún tronco quemado de parte a parte, más abajo.
            val negro = a.cara(lerp(C.CENIZA, Color.Black, 0.3f), s.normal(nPared), centroDe(borde))
            val hondo = 0.75f
            for (j in 0 until PERFIL_CASA.size - 1) {
                val (z0, h0) = PERFIL_CASA[j]; val (z1, h1) = PERFIL_CASA[j + 1]
                val t0 = s.p(0f, h0, z0); val t1 = s.p(0f, h1, z1); val b0 = s.p(0f, h0 - hondo, z0); val b1 = s.p(0f, h1 - hondo, z1)
                val y0 = minOf(t0.y, t1.y); val y1 = maxOf(b0.y, b1.y)
                drawPath(poligono(t0, t1, b1, b0), Brush.verticalGradient(0f to negro, 0.35f to negro.copy(alpha = 0.85f), 1f to negro.copy(alpha = 0f), startY = y0, endY = y1))
            }
            val carbon = poligono(borde + borde.reversed().mapIndexed { j, q -> val z = PERFIL_CASA[PERFIL_CASA.size - 1 - j].first; q + Offset(0f, 0.3f * s.escala(0f, z)) })
            clipPath(carbon) {
                // El carbón en escamas: cuadritos con su filo gris, como la madera quemada.
                repeat(160) {
                    val z = az.entre(0f, 7f); val h = altoDeLaPared(z) - az.entre(0.02f, 0.6f); val kk = s.escala(0f, z)
                    val q = s.p(0f, h, z); val w = az.entre(0.05f, 0.12f) * kk; val hh = w * az.entre(0.5f, 0.8f)
                    drawRect(lerp(negro, C.CENIZA, 0.5f).copy(alpha = 0.7f), Offset(q.x - w / 2f, q.y - hh / 2f), Size(w, hh), style = Stroke(0.35f))
                }
            }
        }
        // Las brasas que quedan en lo alto de la pared, de noche, que laten (en la parte quieta, apenas).
        if (v.noche > 0.4f) repeat(6) { val z = az.entre(0.5f, 6.8f); val q = s.p(0f, altoDeLaPared(z) - 0.05f, z); drawCircle(a.brasa(0.4f).copy(alpha = 0.6f), 0.8f, q) }
        // Las cabezas de los troncos en la esquina de delante: el ensamble, con sus anillos.
        for (j in 0 until 11) {
            val h = 0.12f + j * 0.24f; if (h > PERFIL_CASA.first().second - 0.05f) break
            val c = s.p(0.06f, h, -0.12f); val r = 0.13f * s.k
            drawCircle(mult(a.cara(madera, V3(0f, 0f, -1f), c), C.SOMBRA, 0.35f), r * 1.08f, c + Offset(0.6f, 0.6f))
            drawCircle(a.cara(lerp(C.LONA, C.MADERA_GRIS, 0.55f), V3(0f, 0f, -1f), c), r, c)
            drawCircle(mult(a.cara(C.MADERA, V3(0f, 0f, -1f), c), C.SOMBRA, 0.3f), r * 0.6f, c, style = Stroke(0.5f))
            drawCircle(mult(a.cara(C.MADERA, V3(0f, 0f, -1f), c), C.SOMBRA, 0.3f), r * 0.28f, c, style = Stroke(0.4f))
            // Lo quemado de la cabeza: los de arriba, negros.
            if (h > PERFIL_CASA.first().second - 0.8f) drawCircle(lerp(C.CENIZA, Color.Black, 0.2f).copy(alpha = 0.75f), r, c)
        }
        if (nieve > 0.1f) nieveEncima(a, borde.sortedBy { it.x }, (0.08f * s.escala(0f, 3f) * nieve).coerceAtMost(4f), az)
        // ---- La viga del caballete, caída de lo alto de la pared al camino, quemada, con su punta astillada.
        val v0 = V3(0.15f, 1.62f, 5.2f); val v1 = V3(2.6f, 0.1f, 1.5f)
        val carbonOsc = lerp(C.CENIZA, Color.Black, 0.3f)
        val sil = cilindro(s, a, v1, v0 - v1, 0.14f, 0.12f, Vec.largo(v0 - v1), carbonOsc, lerp(C.CENIZA, C.MADERA, 0.35f), 12)
        clipPath(sil) {
            // La madera quemada en cuadros: grietas de través, con su canto gris de ceniza.
            for (j in 0 until 26) { val t = j / 26f; val q = s.p(v1 + (v0 - v1) * t + V3(0f, 0.13f, 0f)); drawLine(lerp(C.CENIZA, C.LONA, 0.25f).copy(alpha = 0.45f), q + Offset(-1.5f, -0.6f), q + Offset(1.4f, 2.4f), 0.5f) }
        }
        if (nieve > 0.1f) nieveEncima(a, (0..6).map { s.p(v1 + (v0 - v1) * (it / 6f) + V3(0f, 0.14f, 0f)) }.sortedBy { it.x }, (0.05f * s.k * nieve).coerceAtMost(3f), az)
        // ---- Troncos quemados por el suelo, entre la pared y el camino.
        for (j in 0 until 5) {
            val x0 = az.entre(0.3f, 2.2f); val z0 = az.entre(0.4f, 3.6f); val ang = az.entre(-0.9f, 0.9f); val l = az.entre(0.9f, 2.0f)
            val p0 = V3(x0, 0.1f, z0); val e = V3(sin(ang), 0f, cos(ang))
            val tronco = cilindro(s, a, p0, e, 0.11f, 0.1f, l, lerp(carbonOsc, C.MADERA_OSC, az.entre(0f, 0.2f)), lerp(C.CENIZA, C.MADERA, 0.3f), 10)
            clipPath(tronco) { for (k in 0 until 9) { val q = s.p(p0 + e * (l * k / 9f) + V3(0f, 0.1f, 0f)); drawLine(lerp(C.CENIZA, C.LONA, 0.25f).copy(alpha = 0.4f), q + Offset(-1f, -0.6f), q + Offset(1f, 1.8f), 0.45f) } }
        }
    }

    /**
     * Un cuadro de los de Moscú, tirado en el barro boca arriba, con su marco dorado roto por una esquina y el lienzo
     * oscuro y rajado (una figura que no se distingue).
     */
    private fun DrawScope.cuadroTirado(v: VistaNapoleon) {
        val s = S_CUADRO; val a = v.en(s.pie); val az = Azar(Semilla.de("napoleon", "cuadroTirado"))
        val w = 0.45f; val d = 0.36f; val y = 0.03f; val m = 0.08f
        fun q(x: Float, z: Float, h: Float = y) = s.p(x, h, z)
        // La sombra de contacto.
        drawPath(poligono(q(-w - 0.04f, -d - 0.04f, 0f), q(w + 0.04f, -d - 0.04f, 0f), q(w + 0.04f, d + 0.04f, 0f), q(-w - 0.04f, d + 0.04f, 0f)), v.p.sombra(C.BARRO).copy(alpha = 0.45f))
        // El marco: cuatro listones dorados con su moldura, el de la esquina de delante roto y saltado.
        val oro = C.ORO
        val listones = listOf(
            listOf(q(-w, -d), q(w, -d), q(w - m, -d + m), q(-w + m, -d + m)), listOf(q(w, -d), q(w, d), q(w - m, d - m), q(w - m, -d + m)),
            listOf(q(w, d), q(-w, d), q(-w + m, d - m), q(w - m, d - m)), listOf(q(-w, d), q(-w, -d * 0.2f), q(-w + m, -d * 0.2f), q(-w + m, d - m)),
        )
        for ((j, l) in listones.withIndex()) {
            drawPath(poligono(l), a.cara(oro, Vec.unidad(V3(if (j == 1) 0.4f else if (j == 3) -0.4f else 0f, 1f, if (j == 0) -0.4f else if (j == 2) 0.4f else 0f)), centroDe(l)))
            drawLine(lerp(a.arriba(oro, centroDe(l)), Color.White, 0.35f), l[0], l[1], 0.5f)
        }
        // El listón suelto, saltado al lado.
        val t0 = q(-w - 0.15f, -d - 0.1f); val t1 = q(-w + 0.25f, -d - 0.32f)
        drawLine(a.arriba(oro, t0), t0, t1, (m * s.k * 0.7f).coerceAtLeast(1f), StrokeCap.Butt)
        // El lienzo: oscuro, con el barniz que devuelve el cielo, y la raja.
        val lienzo = listOf(q(-w + m, -d + m), q(w - m, -d + m), q(w - m, d - m), q(-w + m, d - m))
        drawPath(poligono(lienzo), a.arriba(lerp(C.MADERA_OSC, C.ROJO, 0.25f), centroDe(lienzo)))
        mancha(s.p(0.05f, y, 0.05f), 0.18f * s.k, 0.08f * s.k, a.arriba(lerp(C.ORO, C.MADERA_OSC, 0.55f), centroDe(lienzo)), 0.6f)
        drawLine(lerp(v.l.alto, Color.White, 0.2f).copy(alpha = 0.25f * (1f - v.noche)), q(-w + m * 1.5f, -d + m * 2f), q(w - m * 2f, d - m * 3f), 1.4f)
        drawPath(curva(listOf(q(-0.1f, -d + m), q(0.02f, -0.05f), q(-0.05f, 0.15f))), Color.Black.copy(alpha = 0.5f), style = Stroke(0.8f))
        // La escarcha o la nieve, por encima.
        if (v.nieve > 0.1f || v.helada) repeat(12) { mancha(q(az.entre(-w, w), az.entre(-d, d), y + 0.01f), az.entre(0.6f, 1.6f), 0.4f, a.arriba(C.NIEVE, s.pie), 0.5f) }
    }

    /** El candelabro de plata de tres brazos, tirado de lado en el barro: «pesa como un niño». */
    private fun DrawScope.candelabro(v: VistaNapoleon) {
        val s = S_CANDELABRO; val a = v.en(s.pie)
        val plata = C.PLATA
        // Tumbado: el pie redondo hacia la izquierda, el fuste a lo largo del suelo y los tres brazos abiertos.
        val pie = V3(-0.25f, 0.08f, 0f); val cima = V3(0.22f, 0.07f, -0.04f)
        drawOval(v.p.sombra(C.BARRO).copy(alpha = 0.4f), Offset(s.p(pie).x - 3f, s.p(pie).y - 1f), Size(s.p(cima).x - s.p(pie).x + 8f, 4f))
        val base = PiezasNapoleon.circulo(s, pie, V3(1f, 0f, 0f), 0.08f, 14)
        drawPath(poligono(base), a.cara(plata, V3(-1f, 0.2f, -0.2f), s.p(pie)))
        cilindro(s, a, pie, cima - pie, 0.022f, 0.016f, Vec.largo(cima - pie), plata, plata, 8)
        for (ang in listOf(-0.7f, 0f, 0.7f)) {
            val b = cima + V3(0f, 0.01f, 0f); val p1 = b + V3(0.12f, 0.03f + 0.12f * sin(ang), 0.12f * cos(ang) - 0.12f)
            val q0 = s.p(b); val q1 = s.p(p1)
            drawLine(a.cara(plata, V3(0f, 1f, -0.3f), q0), q0, q1, 1.4f, StrokeCap.Round)
            drawCircle(a.cara(plata, V3(0f, 1f, -0.3f), q1), 1.6f, q1)
            drawCircle(lerp(a.arriba(plata, q1), Color.White, 0.6f), 0.6f, q1 + Offset(-0.5f, -0.6f))
        }
        val bq = s.p(pie + V3(0.02f, 0.06f, -0.02f))
        drawCircle(lerp(a.arriba(plata, bq), Color.White, 0.5f).copy(alpha = 0.8f), 0.9f, bq)
    }

    // ================================================================= Krasnoi

    /**
     * El tronco del pino grande, tronchado por una bala: lo que queda en pie, con la corteza, y en lo alto la rotura,
     * en astillas largas y claras de madera viva; en el costado, la herida blanca donde dio otra.
     */
    private fun DrawScope.troncoTronchado(v: VistaNapoleon) {
        troncoDelPino(v, ROTURA.y + 6f)
        val a = v.en(PIE_DEL_PINO); val az = Azar(Semilla.de("napoleon", "rotura"))
        val viva = lerp(C.LONA, C.MADERA, 0.35f)
        val p = PIE_DEL_PINO
        // Las astillas de la rotura: dientes largos de madera clara, con su canto oscuro de corteza.
        for (j in 0 until 9) {
            val x = p.x - 12f + j * 3.2f + az.entre(-1f, 1f); val h = az.entre(6f, 22f) * (if (j in 3..5) 1.3f else 0.8f)
            val b = Offset(x, ROTURA.y + 7f); val t = Offset(x + az.entre(-3f, 3f), ROTURA.y + 7f - h)
            val astilla = poligono(b + Offset(-2.2f, 0f), t, b + Offset(2.2f, 0f))
            drawPath(astilla, a.plano(viva, b, if (j < 4) -0.5f else 0.6f, 0f, 0.6f))
            drawLine(a.plano(lerp(viva, C.MADERA_OSC, 0.4f), b, 0f), b + Offset(-1.6f, 0f), t, 0.5f)
        }
        // La herida de la otra bala, en el costado: la corteza arrancada y la madera viva.
        val herida = TrazosValle.blando(Offset(p.x + 9f, 150f), 6f, 11f, az, 9, 0.3f, 0.6f)
        drawPath(herida, a.plano(viva, Offset(p.x + 9f, 150f), 0.6f, 0f, 0.5f))
        drawPath(herida, a.plano(C.MADERA_OSC, Offset(p.x + 9f, 150f), 0f), style = Stroke(0.8f))
        repeat(4) { val q = Offset(p.x + 9f + az.entre(-3f, 3f), 150f + az.entre(-8f, 8f)); drawLine(a.plano(lerp(viva, C.MADERA, 0.5f), q, 0f), q, q + Offset(az.entre(-1f, 1f), az.entre(2f, 5f)), 0.5f) }
        if (v.nieve > 0.3f) drawPath(TrazosValle.blando(Offset(p.x, ROTURA.y + 6f), 12f, 2.5f, az, 7, 0.2f, 0.5f), a.arriba(C.NIEVE, ROTURA))
    }

    /**
     * Lo que la bala arrancó del pino y quedó al pie, sobre la nieve: astillas largas de madera viva y ramas rotas de la
     * copa (la copa cayó hacia atrás, fuera del cuadro), con la nieve que se sacudió encima.
     */
    private fun DrawScope.astillasYRamas(v: VistaNapoleon) {
        val az = Azar(Semilla.de("napoleon", "astillas")); val p = PIE_DEL_PINO
        val viva = lerp(C.LONA, C.MADERA, 0.35f)
        val lado = if (v.p.solALaDerecha) 1f else -1f
        // Las ramas rotas, tiradas: la rama madre y las ramillas con sus agujas, en abanico.
        for (j in 0 until 4) {
            val r0 = p + Offset(az.entre(14f, 60f), az.entre(2f, 18f)); val ang = az.entre(-0.5f, 0.35f); val l = az.entre(30f, 52f)
            val r1 = r0 + Offset(cos(ang) * l, sin(ang) * l * 0.4f)
            val a = v.en(r0)
            drawLine(a.plano(lerp(C.MADERA_OSC, C.CENIZA, 0.25f), r0, 0f), r0, r1, 2.2f, StrokeCap.Round)
            repeat(10) { i ->
                val t = 0.1f + i * 0.088f; val q = r0 + (r1 - r0) * t; val sg = if (i % 2 == 0) -1f else 1f
                val f = q + Offset(az.entre(-3f, 5f), sg * az.entre(4f, 9f) - 3f)
                drawLine(a.plano(C.PINO_OSC, q, 0f), q, f, 2.6f, StrokeCap.Round)
                drawLine(a.plano(if (i % 3 == 0) C.PINO_LUZ else C.PINO, q, lado * 0.5f), q + Offset(0.5f, -0.4f), f + Offset(0.6f, -0.3f), 1f, StrokeCap.Round)
                if (v.nieve > 0.3f && sg < 0f) drawLine(a.arriba(C.NIEVE, f), f + Offset(-1.2f, -0.6f), f + Offset(1.4f, -0.2f), 1.2f, StrokeCap.Round)
            }
        }
        // Las astillas, clavadas en la nieve o tendidas.
        repeat(7) {
            val q = p + Offset(az.entre(10f, 80f), az.entre(4f, 24f)); val ang = az.entre(-2.6f, -0.4f); val l = az.entre(8f, 18f)
            val f = q + Offset(cos(ang) * l, sin(ang) * l * 0.6f)
            drawLine(v.en(q).plano(viva, q, 0.5f, 0f, 0.6f), q, f, az.entre(1f, 2f), StrokeCap.Round)
            drawLine(v.en(q).plano(lerp(viva, C.MADERA_OSC, 0.4f), q, -0.5f), q + Offset(0.5f, 0.6f), f + Offset(0.5f, 0.6f), 0.5f)
        }
    }

    /**
     * Un cajón de munición reventado: lo que queda de la caja verde, abierta y astillada, la tapa arrancada a un lado, la
     * rueda del armón suelta, las balas de cañón por el suelo y la nieve tiznada de negro alrededor.
     */
    private fun DrawScope.cajonReventado(v: VistaNapoleon) {
        val s = S_CAJON; val a = v.en(s.pie); val az = Azar(Semilla.de("napoleon", "cajon"))
        val verde = lerp(C.PINO_LUZ, C.CAPOTE, 0.35f)
        // La quemadura en la nieve: negra en medio, gris hacia fuera.
        mancha(s.p(0f, 0f, 0.1f), 1.5f * s.k, 0.4f * s.k, lerp(C.CENIZA, C.NIEVE_SUCIA, 0.4f), 0.7f)
        mancha(s.p(0.1f, 0f, 0f), 0.8f * s.k, 0.22f * s.k, C.CENIZA, 0.8f)
        // La rueda de detrás, tirada y medio hundida.
        rueda(s, a, V3(-0.9f, 0.25f, 0.6f), 0.58f, Vec.unidad(V3(0.25f, 1f, -0.2f)), 0.07f, 12, 3, C.MADERA_GRIS, 0f, 0.4f, v.nieve)
        // El cajón: el fondo y dos costados, sin tapa, con los bordes rotos.
        caja(s, a, -0.55f, 0.55f, 0.02f, 0.12f, -0.32f, 0.32f, verde)
        for ((p0, p1, n) in listOf(Triple(V3(-0.55f, 0f, -0.32f), V3(0.55f, 0f, -0.32f), V3(0f, 0f, -1f)), Triple(V3(0.55f, 0f, -0.32f), V3(0.55f, 0f, 0.32f), V3(1f, 0f, 0f)))) {
            val alto = { t: Float -> 0.42f * (0.45f + 0.55f * sin(t * 7f + p0.x).let { it * it }) }
            val q = (0..6).map { i -> val t = i / 6f; p0 + (p1 - p0) * t + V3(0f, alto(t), 0f) }
            val f = cara(s, a, listOf(p0, p1) + q.reversed(), n, verde) ?: continue
            clipPath(f) { for (t in listOf(0.33f, 0.66f)) { val r0 = s.p(p0 + (p1 - p0) * t); drawLine(mult(a.cara(verde, s.normal(n), r0), C.SOMBRA, 0.5f), r0, r0 + Offset(0f, -0.5f * s.k), 0.6f) } }
            // Las astillas del borde roto, y lo negro de la explosión.
            for (pt in q) drawLine(a.cara(lerp(verde, C.LONA, 0.3f), s.normal(n), s.p(pt)), s.p(pt), s.p(pt) + Offset(az.entre(-1.5f, 1.5f), -az.entre(1f, 4f)), 0.6f)
            drawPath(poligono(q.map { s.p(it) } + q.reversed().map { s.p(it) + Offset(0f, 0.12f * s.k) }), C.CENIZA.copy(alpha = 0.6f))
        }
        // La tapa, arrancada, de lado en la nieve.
        cara(s, a, listOf(V3(0.7f, 0f, -0.6f), V3(1.5f, 0.05f, -0.35f), V3(1.45f, 0.35f, -0.3f), V3(0.68f, 0.3f, -0.55f)), Vec.unidad(V3(-0.2f, 0.3f, -1f)), verde)
        // Las balas de cañón, sueltas: negras, con su brillo y su sombra.
        repeat(7) {
            val q = s.p(az.entre(-1.2f, 1.6f), 0.06f, az.entre(-0.9f, 0.4f)); val r = 0.06f * s.k
            drawOval(v.p.sombra(C.NIEVE_SOMBRA).copy(alpha = 0.5f), Offset(q.x - r * 1.2f, q.y + r * 0.4f), Size(r * 2.6f, r * 0.8f))
            drawCircle(a.plano(C.HIERRO, q, 0f), r, q)
            drawCircle(lerp(a.arriba(C.HIERRO, q), Color.White, 0.3f), r * 0.35f, q + Offset(-r * 0.35f, -r * 0.4f))
        }
        if (v.nieve > 0.3f) nieveEncima(a, listOf(s.p(-0.9f, 0.85f, 0.6f), s.p(-0.7f, 0.82f, 0.62f), s.p(-0.5f, 0.75f, 0.62f)).sortedBy { it.x }, (0.04f * s.k * v.nieve), az)
    }

    // ================================================================= El Berezina

    /** Las filas de la pila de troncos: cuántos en cada una, de abajo arriba. */
    private val FILAS_PILA = listOf(5, 4, 4, 3)
    private const val R_TRONCO = 0.16f

    /**
     * La pila de troncos de los pontoneros, junto al camino: troncos de pino de cuatro metros, apilados en filas, con las
     * testas hacia quien mira (los anillos, la médula, la raja del secado, la resina), la corteza a lo largo, la nieve en
     * lo alto y unas tablas apoyadas en un costado; un hacha clavada en una testa.
     */
    private fun DrawScope.pila(v: VistaNapoleon) {
        val s = S_PILA; val a = v.en(s.pie); val az = Azar(Semilla.de("napoleon", "pila"))
        val r = R_TRONCO; val largo = 4.2f
        val corteza = lerp(C.MADERA_OSC, C.ROJO, 0.14f); val testa = lerp(C.LONA, C.MADERA, 0.32f)
        // Los calces, en el suelo.
        for (x in listOf(-0.55f, 0.55f)) caja(s, a, x - 0.08f, x + 0.08f, 0f, 0.1f, 0.2f, largo - 0.2f, C.MADERA_GRIS)
        val centros = ArrayList<V3>()
        for ((fila, n) in FILAS_PILA.withIndex()) {
            val y = 0.1f + r + fila * r * 1.72f
            for (i in 0 until n) centros += V3((i - (n - 1) / 2f) * r * 2.02f + (if (fila % 2 == 1) az.entre(-0.03f, 0.03f) else 0f), y, az.entre(-0.06f, 0.14f))
        }
        for ((j, c) in centros.withIndex()) {
            val rr = r * az.entre(0.88f, 1.08f)
            cilindro(s, a, c, V3(0f, 0f, 1f), rr, rr * 0.92f, largo, lerp(corteza, C.CAPOTE, az.entre(0f, 0.25f)), null, 14, 0f, tapaAbajo = testa)
            // La testa: los anillos, la médula, la raja y la resina.
            val ct = s.p(c)
            for (f in listOf(0.78f, 0.55f, 0.32f)) drawPath(TrazosValle.cerrada(PiezasNapoleon.circulo(s, c + V3(0f, 0f, -0.002f), V3(0f, 0f, 1f), rr * f, 16)), a.cara(lerp(testa, C.MADERA, 0.5f), V3(0f, 0f, -1f), ct).copy(alpha = 0.7f), style = Stroke(0.4f))
            drawCircle(a.cara(C.MADERA, V3(0f, 0f, -1f), ct), (0.025f * s.k).coerceAtLeast(0.5f), ct)
            val ang = az.entre(0f, 6.28f); drawLine(a.cara(C.MADERA_OSC, V3(0f, 0f, -1f), ct), ct, ct + Offset(cos(ang), sin(ang)) * (rr * s.k * 0.9f), 0.5f)
            if (az.real() < 0.3f) mancha(ct + Offset(az.entre(-2f, 2f), az.entre(-2f, 2f)), 1.2f, 1.6f, a.cara(lerp(C.LATON, C.MADERA, 0.3f), V3(0f, 0f, -1f), ct), 0.7f)
            // La nieve en lo alto de cada tronco de la fila de arriba (y en las testas, un ribete).
            if (v.nieve > 0.2f && (c.y > 0.1f + r + (FILAS_PILA.size - 1) * r * 1.72f - 0.01f || az.real() < 0.25f)) {
                val q0 = s.p(c + V3(-rr * 0.8f, rr * 0.9f, 0f)); val q1 = s.p(c + V3(rr * 0.8f, rr * 0.9f, 0f))
                drawLine(a.arriba(C.NIEVE, ct), q0, q1, (0.06f * s.k * v.nieve).coerceIn(0.6f, 4f), StrokeCap.Round)
            }
            if (j == 2) {
                // El hacha, clavada en la testa: la hoja de hierro y el mango hacia fuera, a la derecha.
                val h0 = s.p(c + V3(0.02f, 0.05f, -0.03f)); val h1 = s.p(c + V3(0.55f, 0.32f, -0.45f))
                drawLine(a.plano(lerp(C.MADERA, C.LONA, 0.3f), h0, 0.6f), h0, h1, (0.035f * s.k).coerceAtLeast(1f), StrokeCap.Round)
                drawPath(poligono(h0 + Offset(-0.07f * s.k, -0.03f * s.k), h0 + Offset(0.03f * s.k, -0.06f * s.k), h0 + Offset(0.05f * s.k, 0.06f * s.k), h0 + Offset(-0.08f * s.k, 0.08f * s.k)), a.plano(C.HIERRO, h0, 0.4f))
            }
        }
        // Lo de arriba de la pila, con nieve: una capa seguida sobre la fila de arriba, hacia el fondo.
        if (v.nieve > 0.2f) {
            val yTop = 0.1f + r + (FILAS_PILA.size - 1) * r * 1.72f + r
            val n = FILAS_PILA.last()
            val borde = (0..8).map { i -> s.p(-n * r + 2f * n * r * i / 8f, yTop, 0.15f) }
            nieveEncima(a, borde, (0.12f * s.k * v.nieve).coerceAtMost(6f), az)
        }
        // Las tablas apoyadas en el costado de la derecha, de canto.
        for (j in 0 until 3) {
            val x = 0.75f + j * 0.06f; val z = 0.7f + j * 0.55f
            cara(s, a, listOf(V3(x + 0.38f, 0f, z), V3(x + 0.38f, 0f, z + 0.28f), V3(x, 1.15f, z + 0.28f), V3(x, 1.15f, z)), Vec.unidad(V3(1f, 0.32f, 0f)), lerp(C.MADERA_GRIS, C.MADERA, 0.35f + 0.1f * j))
        }
    }

    /** El caballete de repuesto de los puentes: dos patas en A, el cabezal arriba y la riostra en cruz, con nieve en el cabezal. */
    private fun DrawScope.caballete(v: VistaNapoleon) {
        val s = S_CABALLETE; val a = v.en(s.pie); val az = Azar(Semilla.de("napoleon", "caballete"))
        val madera = lerp(C.MADERA_GRIS, C.MADERA, 0.3f)
        val alto = 2.5f
        for (sg in listOf(-1f, 1f)) cilindro(s, a, V3(sg * 0.95f, 0f, 0f), V3(-sg * 0.7f, alto, 0f), 0.09f, 0.08f, Vec.largo(V3(-sg * 0.7f, alto, 0f)), madera, madera, 10)
        cilindro(s, a, V3(-0.6f, alto - 0.05f, 0f), V3(1f, 0f, 0f), 0.11f, 0.11f, 1.2f, madera, lerp(madera, C.LONA, 0.3f), 10)
        cilindro(s, a, V3(-0.75f, 0.55f, -0.08f), V3(1.4f, 1.3f, 0f), 0.06f, 0.06f, Vec.largo(V3(1.4f, 1.3f, 0f)), lerp(madera, C.CENIZA, 0.15f), madera, 8)
        // Los clavos de forja del cabezal.
        for (x in listOf(-0.25f, 0.25f)) drawCircle(a.plano(C.HIERRO, s.p(x, alto - 0.05f, -0.12f), 0f), (0.02f * s.k).coerceAtLeast(0.5f), s.p(x, alto - 0.05f, -0.12f))
        if (v.nieve > 0.2f) nieveEncima(a, listOf(s.p(-0.6f, alto + 0.07f, 0f), s.p(0f, alto + 0.07f, 0f), s.p(0.6f, alto + 0.07f, 0f)).sortedBy { it.x }, (0.08f * s.k * v.nieve), az)
        hundido(v, s, 2.2f, 0.4f)
    }

    // ================================================================= El final

    /**
     * La nieve honda al pie del pino grande: un ventisquero largo que el viento ha tallado en olas, cada una con su
     * cornisa que cuelga hacia el camino, su sombra azul debajo y su filo encendido; las estrías del viento encima; una
     * rueda de un carro enterrado que asoma su arco (lo que queda de lo que se dejó atrás **[Diseño propio]**); y una rama
     * del pino rota por el peso, medio enterrada.
     */
    private fun DrawScope.ventisquero(v: VistaNapoleon) {
        val a = v.en(Offset(90f, 236f)); val az = Azar(Semilla.de("napoleon", "ventisquero"))
        val lado = if (v.p.solALaDerecha) 1f else -1f
        val nieve = v.nieve
        val sombraAzul = lerp(C.NIEVE_SOMBRA, C.AZUL, 0.18f)
        val nCara = Vec.unidad(V3(lado * 0.15f, 0.35f, -1f)); val nArriba = Vec.unidad(V3(-lado * 0.1f, 1f, -0.25f))
        // Las olas, de atrás adelante: cada una, una cresta que baja de la izquierda (cortada por el borde) hacia el camino.
        val olas = listOf(
            listOf(Offset(-10f, 194f), Offset(24f, 199f), Offset(62f, 209f), Offset(98f, 221f), Offset(128f, 234f), Offset(152f, 248f), Offset(170f, 262f), Offset(182f, 290f)),
            listOf(Offset(-10f, 226f), Offset(20f, 228f), Offset(52f, 236f), Offset(86f, 246f), Offset(114f, 258f), Offset(140f, 272f), Offset(156f, 292f)),
            listOf(Offset(-10f, 258f), Offset(18f, 259f), Offset(48f, 265f), Offset(80f, 274f), Offset(104f, 284f), Offset(118f, 296f)),
        )
        for ((i, cresta) in olas.withIndex()) {
            val forma = TrazosValle.cerrada(cresta + listOf(cresta.last() + Offset(0f, 12f), Offset(-10f, 300f)))
            val y0 = cresta.first().y; val y1 = cresta.first().y + 60f
            // La cara que mira a quien mira: del filo, que coge la luz, a lo hondo, azul.
            drawPath(forma, Brush.verticalGradient(0f to a.cara(C.NIEVE, nArriba, cresta[3]), 0.18f to a.cara(C.NIEVE, nCara, cresta[3]), 1f to a.cara(sombraAzul, nCara, cresta[3]), startY = y0, endY = y1))
            clipPath(forma) {
                // Las estrías del viento, tendidas a lo largo de la ola.
                repeat(14) {
                    val t = az.entre(0.05f, 0.9f); val q = TrazosValle.en(cresta, t) + Offset(az.entre(-6f, 6f), az.entre(4f, 22f)); val l = az.entre(10f, 28f)
                    drawLine(a.cara(sombraAzul, nCara, q).copy(alpha = 0.45f), q, q + Offset(l, l * 0.25f), az.entre(0.4f, 0.9f), StrokeCap.Round)
                }
            }
            // La cornisa: el labio que cuelga, con su sombra azul debajo, y el filo encendido.
            drawPath(curva(cresta.map { it + Offset(1.5f, 3f) }), a.cara(sombraAzul, V3(0f, -0.5f, -1f), cresta[3]).copy(alpha = 0.8f), style = Stroke(2.8f - i * 0.6f, cap = StrokeCap.Round))
            drawPath(curva(cresta), lerp(a.cara(C.NIEVE, nArriba, cresta[3]), Color.White, 0.5f).copy(alpha = 0.85f * (0.35f + 0.65f * v.r.dia)), style = Stroke(1.3f - i * 0.25f, cap = StrokeCap.Round))
            if (i == 0) {
                // La rueda del carro enterrado, que asoma el arco por encima de la primera ola: la llanta, la nieve encima, tres rayos.
                val c = Offset(118f, 246f); val r = 26f
                val arco = (0..14).map { j -> val ang = PI.toFloat() * (1.15f + 0.7f * j / 14f); c + Offset(cos(ang) * r, sin(ang) * r * 0.55f) }
                drawPath(curva(arco), a.plano(C.MADERA_GRIS, c, 0f), style = Stroke(3.2f, cap = StrokeCap.Round))
                drawPath(curva(arco.map { it + Offset(0f, -1.6f) }), a.plano(C.HIERRO, c, 0f), style = Stroke(1f, cap = StrokeCap.Round))
                for (ang in listOf(1.3f, 1.5f, 1.7f)) { val e = c + Offset(cos(PI.toFloat() * ang) * r * 0.95f, sin(PI.toFloat() * ang) * r * 0.52f); drawLine(a.plano(C.MADERA_GRIS, e, lado * 0.4f), c + Offset(0f, -6f), e, 1.6f, StrokeCap.Round) }
                if (nieve > 0.3f) drawPath(curva(arco.subList(3, 12).map { it + Offset(0f, -2.4f) }), a.arriba(C.NIEVE, c), style = Stroke(2f, cap = StrokeCap.Round))
                drawPath(TrazosValle.blando(c + Offset(0f, 2f), 34f, 6f, az, 9, 0.2f, 0.5f), a.cara(C.NIEVE, nCara, c))
            }
        }
        // La rama rota del pino, caída y medio enterrada en la primera ola: el muñón astillado, la rama y las ramillas que asoman.
        val r0 = Offset(28f, 206f); val r1 = Offset(92f, 226f)
        drawLine(a.plano(lerp(C.MADERA_OSC, C.CENIZA, 0.25f), r0, 0f), r0, r1, 3.2f, StrokeCap.Round)
        drawLine(a.plano(lerp(C.LONA, C.MADERA, 0.4f), r0, 0.6f), r0, r0 + Offset(-4f, -2f), 2.2f, StrokeCap.Round)
        repeat(8) { i ->
            val t = 0.15f + i * 0.1f; val q = r0 + (r1 - r0) * t
            val f = q + Offset(az.entre(-4f, 8f), az.entre(-12f, -5f))
            drawLine(a.plano(C.PINO_OSC, q, 0f), q, f, 2.4f, StrokeCap.Round)
            drawLine(a.plano(C.PINO, q, lado * 0.4f), q + Offset(0.6f, 0f), f + Offset(0.8f, 0.5f), 1f, StrokeCap.Round)
            if (nieve > 0.3f) drawLine(a.arriba(C.NIEVE, f), f + Offset(-1f, -0.5f), f + Offset(1.5f, 0.3f), 1.4f, StrokeCap.Round)
        }
        repeat(3) { val t = az.entre(0.2f, 0.9f); mancha(r0 + (r1 - r0) * t + Offset(0f, 1.5f), az.entre(6f, 9f), 2.4f, a.arriba(C.NIEVE, r0), 0.95f) }
    }
}
