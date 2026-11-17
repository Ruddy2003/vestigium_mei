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
import androidx.compose.ui.graphics.drawscope.clipRect
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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
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

    fun marcoDe(v: VistaNapoleon): Marco = TramosRetirada.marco(v.tramo)

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

    /**
     * La isba quemada: la esquina de delante de la pared que da al camino (sus ejes, los del mundo: x a la derecha, z al
     * fondo). La casa está a la orilla izquierda del camino, con esa pared un poco dentro de él, como en los pueblos de
     * camino: si estuviera más a la izquierda, a esta distancia quedaría fuera del cuadro.
     */
    private val S_CASA = PiezasNapoleon.sitio(MIRADA, RetiradaCamino.w(-2.2f, 10f), 0f)
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
        Marco.CASA -> S_CASA.p((ESTUFA_X0 + ESTUFA_X1) / 2f, ESTUFA_ALTO, (ESTUFA_Z0 + ESTUFA_Z1) / 2f)
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
                add(RetiradaLuz.Sombra(S_CASA.p(0f, 0f, 3.2f), siluetaDePared(PARED_CAMINO)))
                val zc = (ESTUFA_Z0 + ESTUFA_Z1) / 2f
                add(RetiradaLuz.Sombra(S_CASA.p(ESTUFA_X1, 0f, zc), poligono(listOf(S_CASA.p(ESTUFA_X0, 0f, zc), S_CASA.p(ESTUFA_X1, 0f, zc), S_CASA.p(ESTUFA_X1, ESTUFA_ALTO, zc), S_CASA.p(CHIMENEA_X1, CHIMENEA_ALTO, zc), S_CASA.p(CHIMENEA_X0, CHIMENEA_ALTO, zc), S_CASA.p(ESTUFA_X0, ESTUFA_ALTO, zc)))))
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
            // Solo lo de arriba: más abajo, el tronco es el de la corteza en escamas ([troncoDelPino]), que así se ve.
            clipRect(-60f, -60f, ANCHO + 60f, 42f) {
                pino(a, PIE_DEL_PINO, 760f, 430f, v.nieve, Azar(AZ_PINO), 0f, 1f, false, hastaY = 30f, caidaPropia = 0.17f + 0.12f * v.nieve + (if (honda) 0.24f else 0f), grande = true)
            }
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
            // Los días de las brasas, la casa humea por las vigas y laten las brasas; luego ya está fría.
            Marco.CASA -> if (brasasEnLaCasa(v.m)) {
                for ((i, vg) in VIGAS.withIndex()) {
                    val c = S_CASA.p((vg.first + vg.second) * 0.5f)
                    humo(v.l, c, 70f, 7f, reloj, v.m.viento + 0.2f, 0.4f, 0.4f, 71 + i, 6, 0.45f)
                    val az = Azar(Semilla.de("napoleon", "brasasCasa", i))
                    repeat(7) { j -> val q = S_CASA.p(vg.second + (vg.first - vg.second) * az.entre(0.05f, 0.95f)) + Offset(0f, az.entre(0f, 1.5f)); val al = (0.45f + 0.45f * sin(reloj * 1.6f + j * 2.1f + i)).coerceIn(0f, 1f)
                        drawCircle(Color(0xFFFF6A22).copy(alpha = al), az.entre(0.6f, 1.1f), q) }
                }
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

    /**
     * Las paredes de la isba quemada, en los ejes de [S_CASA] (x hacia el camino, z hacia el fondo): de dónde a dónde y
     * lo que queda de cada una (su alto, en metros, a lo largo), comidas por el fuego desde arriba. La que da al camino
     * queda alta en las dos esquinas y en el tramo de la ventana; la de delante, la que mira a quien mira, se quemó casi
     * entera (por eso se ve dentro la estufa); la del fondo se ve por dentro, negra.
     */
    private val PARED_CAMINO = Triple(V3(0f, 0f, 0f), V3(0f, 0f, 6.5f), listOf(0f to 2.25f, 0.05f to 2.05f, 0.14f to 1.3f, 0.25f to 0.6f, 0.37f to 0.45f, 0.48f to 0.8f, 0.58f to 1.5f, 0.66f to 1.95f, 0.83f to 2.0f, 0.92f to 1.85f, 1f to 2.15f))
    private val PARED_DELANTE = Triple(V3(0f, 0f, 0f), V3(-5.5f, 0f, 0f), listOf(0f to 2.25f, 0.025f to 2.0f, 0.04f to 0.62f, 0.1f to 0.42f, 0.2f to 0.3f, 0.45f to 0.45f, 0.64f to 0.28f, 1f to 0.55f))
    private val PARED_FONDO = Triple(V3(0f, 0f, 6.5f), V3(-5.5f, 0f, 6.5f), listOf(0f to 2.15f, 0.11f to 1.8f, 0.29f to 2.3f, 0.51f to 1.5f, 0.73f to 2.0f, 1f to 1.2f))

    /** La estufa rusa y su chimenea (de x0 a x1, de z0 a z1, su alto) y la boca del horno, en la cara que mira a quien mira. */
    private const val ESTUFA_X0 = -1.4f; private const val ESTUFA_X1 = -0.2f; private const val ESTUFA_Z0 = 0.9f; private const val ESTUFA_Z1 = 2.5f; private const val ESTUFA_ALTO = 1.95f
    private const val CHIMENEA_X0 = -0.95f; private const val CHIMENEA_X1 = -0.4f; private const val CHIMENEA_Z0 = 1.4f; private const val CHIMENEA_Z1 = 1.95f; private const val CHIMENEA_ALTO = 4.5f

    /** Las vigas caídas: de lo alto de la pared del camino al camino, y de la estufa al suelo de dentro. */
    private val VIGAS = listOf(V3(0.05f, 1.5f, 3.8f) to V3(1.6f, 0.1f, 0.4f), V3(-0.25f, 1.92f, 2.2f) to V3(-2.6f, 0.1f, 4.0f))

    /**
     * Si las vigas tienen todavía brasas: el día de Maloyaroslavets y el siguiente («pasan junto al pueblo, que arde»)
     * **[Aproximación]**: los demás días del camino viejo, los pueblos se quemaron al venir, hace dos meses, y están fríos.
     */
    fun brasasEnLaCasa(m: Momento): Boolean = EscenaNapoleon.tramo(m) == EscenaNapoleon.Tramo.MALOYAROSLAVETS && m.ficha.dia.substring(5) <= "10-25"

    /** La silueta de lo que queda de una pared (para su sombra). */
    private fun siluetaDePared(p: Triple<V3, V3, List<Pair<Float, Float>>>): Path {
        val (p0, p1, perfil) = p; val s = S_CASA
        return poligono(listOf(s.p(p0), s.p(p1)) + perfil.reversed().map { (t, h) -> s.p(p0 + (p1 - p0) * t + V3(0f, h, 0f)) })
    }

    /**
     * Lo que queda de una isba de madera quemada junto al camino, de cerca, para que se lea de un vistazo: las paredes de
     * troncos con lo de arriba carbonizado (negro, cuarteado en escamas con su brillo de carbón y su ceniza gris), la
     * esquina de troncos ensamblados que asoman, la estufa rusa de ladrillo encalado en pie con su boca y su chimenea (lo
     * único que no arde: la imagen de los pueblos quemados), el marco tallado de una ventana con su postigo colgando de
     * un gozne, las vigas caídas en diagonal (con brasas rojas los primeros días, [brasasEnLaCasa]), las tablas del tejado
     * y la paja quemada por el suelo, y al pie de la pared las cosas de la casa: un banco volcado, el caldero de hierro y
     * la rueca. De noche, las brasas dan una segunda lumbre, débil.
     */
    private fun DrawScope.casa(v: VistaNapoleon) {
        val s = S_CASA; val az = Azar(Semilla.de("napoleon", "isba"))
        val a = v.en(s.p(0f, 0f, 2f))
        val nieve = v.nieve
        val brasas = brasasEnLaCasa(v.m)
        // ---- La pared del fondo, vista por dentro: casi toda negra.
        paredQuemada(v, PARED_FONDO, V3(0f, 0f, -1f), true, az)
        // ---- La estufa rusa, de ladrillo encalado, y su chimenea.
        estufa(v, az)
        // ---- La viga que cayó dentro, apoyada en la estufa.
        viga(v, VIGAS[1].first, VIGAS[1].second, 0.12f, brasas, az)
        // ---- La pared de delante, quemada casi hasta el suelo, y la del camino, con la ventana.
        paredQuemada(v, PARED_DELANTE, V3(0f, 0f, -1f), false, az)
        paredQuemada(v, PARED_CAMINO, V3(1f, 0f, 0f), false, az, ventana = 0.68f to 0.81f)
        postigo(v, 0.68f * 6.5f)
        // ---- La esquina de troncos ensamblados: las cabezas de unos y otros, a hilos alternos, que asoman.
        esquina(v, az)
        // ---- El cabio que sigue en pie al fondo, roto y astillado.
        run {
            val q0 = s.p(0f, 2.1f, 6.45f); val q1 = s.p(-1.2f, 3.35f, 6.45f); val g = (0.13f * s.escala(0f, 6.45f)).coerceAtLeast(1f)
            drawLine(a.plano(lerp(C.CENIZA, Color.Black, 0.2f), q0, -0.5f), q0, q1, g, StrokeCap.Round)
            drawLine(lerp(C.CENIZA, C.LONA, 0.4f).copy(alpha = 0.6f), q0 + Offset(g * 0.3f, -g * 0.2f), q1 + Offset(g * 0.3f, -g * 0.2f), g * 0.25f, StrokeCap.Round)
            for (j in 0 until 4) { val d = Offset(az.entre(-3f, 1f), az.entre(-5f, -1.5f)); drawLine(C.CENIZA, q1, q1 + d, 0.8f, StrokeCap.Round) }
        }
        // ---- Las tablas del tejado y la paja quemada, por el suelo, entre la pared y el camino.
        repeat(14) {
            val x = az.entre(0.25f, 2.1f); val z = az.entre(-0.3f, 4.6f); val ang = az.entre(0f, 3.14f); val l = az.entre(0.35f, 0.7f); val w = 0.12f
            val e = V3(cos(ang), 0f, sin(ang)); val n = V3(-sin(ang), 0f, cos(ang))
            val q = listOf(V3(x, 0.02f, z), V3(x, 0.02f, z) + e * l, V3(x, 0.02f, z) + e * l + n * w, V3(x, 0.02f, z) + n * w)
            cara(s, a, q, V3(0f, 1f, 0f), lerp(C.CENIZA, C.MADERA_GRIS, az.entre(0.1f, 0.5f)))
        }
        repeat(30) {
            val q = s.p(az.entre(0.15f, 1.4f), 0.02f, az.entre(-0.2f, 5f)); val ang = az.entre(-0.6f, 0.6f); val l = az.entre(2f, 5f)
            drawLine(lerp(C.CENIZA, C.HOJA, az.entre(0f, 0.5f)).copy(alpha = 0.8f), q, q + Offset(cos(ang) * l, sin(ang) * l * 0.4f), 0.5f)
        }
        // ---- La viga caída de la pared al camino.
        viga(v, VIGAS[0].first, VIGAS[0].second, 0.14f, brasas, az)
        // ---- Las cosas de la casa, al pie de la pared: la rueca apoyada, el banco volcado, el caldero tumbado.
        rueca(v, V3(0.22f, 0f, 3.1f))
        banco(v, az)
        caldero(v, V3(1.25f, 0.17f, 0.15f))
        // ---- La primera nevada, si la hay, en lo de arriba.
        if (nieve > 0.1f) for (p in listOf(PARED_CAMINO, PARED_DELANTE)) nieveEncima(a, p.third.map { (t, h) -> s.p(p.first + (p.second - p.first) * t + V3(0f, h, 0f)) }.sortedBy { it.x }, (0.08f * s.escala(0f, 2f) * nieve).coerceAtMost(4f), az)
        // ---- De noche, la segunda lumbre: lo que todavía arde en las vigas alumbra un poco lo de alrededor.
        if (brasas && v.noche > 0.2f) for ((p0, p1) in VIGAS) {
            val c = s.p((p0 + p1) * 0.5f)
            drawCircle(Brush.radialGradient(listOf(C.LUMBRE.copy(alpha = 0.22f * v.noche), Color.Transparent), c, 0.9f * s.k), 0.9f * s.k, c, blendMode = BlendMode.Plus)
        }
    }

    /**
     * Una pared de troncos quemada: [p] (de dónde a dónde y su perfil), con la normal [n] de la cara que se ve. Cada
     * tronco con su lado de luz arriba y su junta oscura; lo de arriba, carbonizado: negro que se apaga hacia abajo, y en
     * el filo el carbón en escamas con su brillo plateado y su ceniza; [dentro]: la cara de dentro, negra casi entera.
     * [ventana]: de qué parte a qué parte de la pared está el hueco de la ventana, con su marco tallado y el postigo.
     */
    private fun DrawScope.paredQuemada(v: VistaNapoleon, p: Triple<V3, V3, List<Pair<Float, Float>>>, n: V3, dentro: Boolean, az: Azar, ventana: Pair<Float, Float>? = null) {
        val s = S_CASA; val (p0, p1, perfil) = p
        fun q(t: Float, h: Float) = s.p(p0 + (p1 - p0) * t + V3(0f, h, 0f))
        val a = v.en(q(0.5f, 0f))
        val borde = perfil.map { (t, h) -> q(t, h) }
        val forma = poligono(listOf(q(0f, 0f), q(1f, 0f)) + borde.reversed())
        val madera = if (dentro) lerp(C.CENIZA, C.MADERA_GRIS, 0.3f) else lerp(C.MADERA_GRIS, C.MADERA, 0.22f)
        val negro = a.cara(lerp(C.CENIZA, Color.Black, 0.35f), s.normal(n), centroDe(borde))
        drawPath(forma, a.cara(madera, s.normal(n), centroDe(borde)))
        clipPath(forma) {
            // Los troncos: la mitad de arriba mira al cielo, la de abajo al suelo; la junta, oscura.
            val dz = 0.24f; var y = 0f; var i = 0
            while (y < 2.5f) {
                val arriba = (0..16).map { j -> q(j / 16f, y + dz) }; val medio = (0..16).map { j -> q(j / 16f, y + dz * 0.5f) }; val abajo = (0..16).map { j -> q(j / 16f, y) }
                val tinte = lerp(madera, C.MADERA_GRIS, 0.12f * ((i * 7) % 3))
                drawPath(poligono(arriba + medio.reversed()), a.cara(tinte, s.normal(Vec.unidad(n + V3(0f, 0.65f, 0f))), medio[8]))
                drawPath(poligono(medio + abajo.reversed()), a.cara(tinte, s.normal(Vec.unidad(n + V3(0f, -0.55f, 0f))), medio[8]))
                drawPath(curva(abajo), mult(a.cara(madera, s.normal(n), abajo[8]), C.SOMBRA, 0.6f), style = Stroke((0.03f * s.escala(0f, 3f)).coerceAtLeast(0.5f)))
                repeat(3) { val t0 = az.entre(0.03f, 0.9f); val hh = y + dz * az.entre(0.35f, 0.75f); drawLine(mult(a.cara(tinte, s.normal(n), q(t0, hh)), C.SOMBRA, 0.45f), q(t0, hh), q(t0 + az.entre(0.04f, 0.1f), hh), 0.5f) }
                y += dz; i++
            }
            // La ventana: el hueco negro, el marco tallado con su frontón, chamuscado, y el postigo que cuelga de un gozne.
            if (ventana != null) {
                val (u0, u1) = ventana; val y0 = 0.85f; val y1 = 1.6f
                val hueco = listOf(q(u0, y0), q(u1, y0), q(u1, y1), q(u0, y1))
                drawPath(poligono(hueco), a.plano(lerp(C.CENIZA, Color.Black, 0.5f), centroDe(hueco), 0f))
                val marco = a.cara(lerp(C.LONA, C.MADERA_GRIS, 0.55f), s.normal(n), centroDe(hueco))
                val g = (0.05f * s.escala(0f, 5f)).coerceAtLeast(0.8f)
                drawPath(poligono(hueco), marco, style = Stroke(g))
                // El frontón tallado de encima, en pico, y el alféizar.
                val c = (u0 + u1) / 2f
                drawPath(poligono(q(u0 - 0.02f, y1 + 0.04f), q(c, y1 + 0.22f), q(u1 + 0.02f, y1 + 0.04f)), marco, style = Stroke(g * 0.8f))
                for (k in 1..3) { val t = u0 + (u1 - u0) * k / 4f; drawCircle(marco, g * 0.35f, q(t, y1 + 0.1f)) }
                drawLine(marco, q(u0 - 0.03f, y0 - 0.03f), q(u1 + 0.03f, y0 - 0.03f), g)
                drawPath(poligono(hueco), lerp(C.CENIZA, Color.Black, 0.3f).copy(alpha = 0.55f), style = Stroke(g * 0.4f))
            }
            // Lo carbonizado: desde el filo, el negro que se apaga hacia abajo, tramo a tramo.
            val hondo = if (dentro) 2.4f else 0.8f
            for (j in 0 until perfil.size - 1) {
                val (t0, h0) = perfil[j]; val (t1, h1) = perfil[j + 1]
                val a0 = q(t0, h0); val a1 = q(t1, h1); val b0 = q(t0, h0 - hondo); val b1 = q(t1, h1 - hondo)
                drawPath(poligono(a0, a1, b1, b0), Brush.verticalGradient(0f to negro, 0.4f to negro.copy(alpha = 0.88f), 1f to negro.copy(alpha = 0f), startY = minOf(a0.y, a1.y), endY = maxOf(b0.y, b1.y)))
            }
            // El carbón en escamas, pegado al filo: cuadritos apenas más negros, con su canto plateado (el brillo del carbón).
            val brillo = lerp(C.CENIZA, C.LONA, 0.6f)
            repeat(if (dentro) 30 else 70) {
                val t = az.entre(0f, 1f); val hTop = perfil.zipWithNext().first { (x0, x1) -> t <= x1.first }.let { (x0, x1) -> x0.second + (x1.second - x0.second) * (t - x0.first) / (x1.first - x0.first) }
                val h = hTop - az.entre(0.03f, 0.3f)
                if (h < 0.05f) return@repeat
                val c = q(t, h); val w = az.entre(0.05f, 0.1f) * s.escala(0f, 3f); val hh = w * az.entre(0.45f, 0.7f)
                drawRect(lerp(negro, Color.Black, 0.2f).copy(alpha = 0.7f), Offset(c.x - w / 2f, c.y - hh / 2f), Size(w, hh))
                drawLine(brillo.copy(alpha = 0.45f * (0.4f + 0.6f * v.r.dia)), Offset(c.x - w / 2f, c.y - hh / 2f), Offset(c.x + w * 0.3f, c.y - hh / 2f), 0.4f)
            }
        }
        // La ceniza posada en el filo, y el filo mismo, roto, con alguna astilla quemada.
        drawPath(curva(borde), lerp(C.CENIZA, C.LONA, 0.35f).copy(alpha = 0.7f), style = Stroke((0.025f * s.escala(0f, 3f)).coerceAtLeast(0.5f), cap = StrokeCap.Round))
        for (b in borde.drop(1).dropLast(1)) if (az.real() < 0.5f) drawLine(lerp(C.CENIZA, Color.Black, 0.2f), b, b + Offset(az.entre(-1.5f, 1.5f), -az.entre(1.5f, 4f)), 0.8f, StrokeCap.Round)
    }

    /**
     * El postigo de la ventana, colgando de un solo gozne: abierto hacia el camino y caído de lado, de tablas con su
     * remate tallado y la pintura azul desvaída, chamuscado por el canto de arriba.
     */
    private fun DrawScope.postigo(v: VistaNapoleon, z0: Float) {
        val s = S_CASA; val a = v.en(s.p(0.2f, 1.2f, z0))
        val d = Vec.unidad(V3(0.9f, 0f, -0.42f))
        val gozne = V3(0.03f, 1.58f, z0)
        val ancho = 0.42f
        // Caído: el canto de abajo se ha ido hacia fuera y hacia abajo (el gozne de abajo saltó).
        val q = listOf(gozne, gozne + d * ancho + V3(0f, -0.2f, 0f), gozne + d * (ancho + 0.12f) + V3(0f, -0.92f, 0f), gozne + d * 0.12f + V3(0f, -0.74f, 0f))
        val n = Vec.unidad(Vec.cruz(q[1] - q[0], q[3] - q[0])).let { if (s.seVe(q[0], it)) it else it * -1f }
        val pintura = lerp(C.AZUL, C.LONA, 0.45f)
        val f = cara(s, a, q, n, pintura) ?: return
        clipPath(f) {
            // Las tablas, de arriba abajo, y el travesaño; la pintura saltada; lo chamuscado de arriba.
            for (t in listOf(0.33f, 0.66f)) drawLine(mult(a.cara(pintura, s.normal(n), s.p(q[0])), C.SOMBRA, 0.5f), s.p(q[0] + (q[1] - q[0]) * t), s.p(q[3] + (q[2] - q[3]) * t), 0.5f)
            drawLine(a.cara(lerp(pintura, C.MADERA_GRIS, 0.5f), s.normal(n), s.p(q[0])), s.p(q[0] + (q[3] - q[0]) * 0.5f), s.p(q[1] + (q[2] - q[1]) * 0.5f), 1.2f)
            mancha(s.p(q[0] + (q[2] - q[0]) * 0.6f), 3f, 2f, a.cara(C.MADERA_GRIS, s.normal(n), s.p(q[0])), 0.6f)
            val yTop = minOf(s.p(q[0]).y, s.p(q[1]).y)
            drawRect(Brush.verticalGradient(0f to lerp(C.CENIZA, Color.Black, 0.3f), 1f to Color.Transparent, startY = yTop, endY = yTop + 6f), Offset(s.p(q[0]).x - 20f, yTop - 1f), Size(60f, 7f))
        }
        // El gozne que queda, de hierro.
        drawCircle(a.plano(C.HIERRO, s.p(gozne), 0f), 0.9f, s.p(gozne))
    }

    /** La esquina de delante: las cabezas de los troncos de las dos paredes, a hilos alternos, carbonizadas por arriba. */
    private fun DrawScope.esquina(v: VistaNapoleon, az: Azar) {
        val s = S_CASA; val a = v.en(s.pie)
        val alto = PARED_CAMINO.third.first().second
        for (j in 0 until 10) {
            val camino = j % 2 == 0
            val h = 0.12f + j * 0.12f * 2f / 2f + (if (camino) 0f else 0.12f)
            if (h > alto - 0.06f) break
            // Las del camino asoman hacia quien mira (la testa mira a −z); las de delante, hacia el camino (+x).
            val c3 = if (camino) V3(0f, h, -0.16f) else V3(0.16f, h, 0f)
            val eje = if (camino) V3(0f, 0f, 1f) else V3(1f, 0f, 0f)
            val r = 0.12f
            val testa = PiezasNapoleon.circulo(s, c3, eje, r, 16)
            val quemada = h > alto - 1.1f
            val mira = if (camino) V3(0f, 0f, -1f) else V3(1f, 0f, 0f)
            val col = a.cara(if (quemada) lerp(C.CENIZA, Color.Black, 0.35f) else lerp(lerp(C.LONA, C.MADERA_GRIS, 0.6f), C.MADERA, 0.2f), mira, s.p(c3))
            drawPath(poligono(testa.map { it + Offset(0.7f, 0.7f) }), mult(col, C.SOMBRA, 0.4f))
            drawPath(poligono(testa), col)
            val cp = s.p(c3)
            if (quemada) {
                // El brillo del carbón: un arco plateado arriba, y las grietas.
                drawPath(curva(testa.subList(9, 14)), lerp(C.CENIZA, C.LONA, 0.6f).copy(alpha = 0.7f), style = Stroke(0.6f))
                repeat(3) { val ang = az.entre(0f, 6.28f); drawLine(lerp(C.CENIZA, C.LONA, 0.3f).copy(alpha = 0.5f), cp, cp + Offset(cos(ang), sin(ang)) * (r * s.k * 0.8f), 0.4f) }
            } else {
                for (f in listOf(0.65f, 0.35f)) drawPath(poligono(PiezasNapoleon.circulo(s, c3 + mira * 0.001f, eje, r * f, 12)), mult(col, C.SOMBRA, 0.3f), style = Stroke(0.4f))
            }
        }
    }

    /**
     * La estufa rusa: el bloque de ladrillo encalado, con el encalado saltado a trozos (asoma el ladrillo), el tizne
     * sobre la boca del horno, la boca en arco con su poyo, el hueco chico de secar, y la chimenea de ladrillo, más
     * negra cuanto más arriba.
     */
    private fun DrawScope.estufa(v: VistaNapoleon, az: Azar) {
        val s = S_CASA; val a = v.en(s.p(-0.8f, 0f, 1.7f))
        val cal = lerp(C.LONA, C.NIEVE, 0.4f); val ladrillo = lerp(C.ROJO, C.MADERA, 0.45f)
        val caras = caja(s, a, ESTUFA_X0, ESTUFA_X1, 0f, ESTUFA_ALTO, ESTUFA_Z0, ESTUFA_Z1, cal, arriba = lerp(cal, C.CENIZA, 0.3f))
        for ((c3, f) in caras) clipPath(f) {
            val nn = s.normal(c3.n); val en = s.p(c3.centro)
            if (c3.arriba) { mancha(s.p(c3.en(0.5f, 0.5f)), 0.5f * s.k, 0.25f * s.k, a.cara(C.CENIZA, nn, en), 0.6f); return@clipPath }
            // La cornisa de arriba (una moldura, más oscura) y el zócalo tiznado de abajo; las grietas del encalado.
            linea(s, c3, 0f, 0.93f, 1f, 0.93f, mult(a.cara(cal, nn, en), C.SOMBRA, 0.35f), (0.04f * s.k).coerceAtLeast(0.8f))
            linea(s, c3, 0f, 0.97f, 1f, 0.97f, lerp(a.cara(cal, nn, en), Color.White, 0.2f), (0.02f * s.k).coerceAtLeast(0.4f))
            val y0 = s.p(c3.en(0.5f, 0f)).y; val y1 = s.p(c3.en(0.5f, 0.25f)).y
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to a.cara(C.CENIZA, nn, en).copy(alpha = 0.55f), startY = y1, endY = y0), Offset(-10f, y1), Size(300f, y0 - y1))
            repeat(4) { val u = az.entre(0.1f, 0.9f); val vv = az.entre(0.2f, 0.85f); drawPath(curva(listOf(s.p(c3.en(u, vv)), s.p(c3.en(u + az.entre(-0.06f, 0.06f), vv - 0.08f)), s.p(c3.en(u + az.entre(-0.08f, 0.08f), vv - 0.16f)))), mult(a.cara(cal, nn, en), C.SOMBRA, 0.45f), style = Stroke(0.4f)) }
            // Donde se saltó el encalado: asoma el ladrillo con sus hiladas.
            repeat(5) {
                val u = az.entre(0.05f, 0.9f); val w = az.entre(0.08f, 0.22f); val vv = az.entre(0.1f, 0.85f)
                val trozo = poligono(s.p(c3.en(u, vv)), s.p(c3.en(u + w, vv + az.entre(-0.03f, 0.03f))), s.p(c3.en(u + w * 0.9f, vv + 0.12f)), s.p(c3.en(u + w * 0.1f, vv + 0.1f)))
                drawPath(trozo, a.cara(ladrillo, nn, en))
                clipPath(trozo) { for (k in 0 until 4) { val hv = vv + k * 0.03f; linea(s, c3, u, hv, u + w, hv, mult(a.cara(ladrillo, nn, en), C.SOMBRA, 0.4f), 0.35f) } }
            }
            // La cara que mira a quien mira: la boca del horno en arco, negra, con el tizne que sube y el poyo debajo.
            if (c3.n.z < -0.5f) {
                val boca = (0..12).map { k -> val ang = PI.toFloat() * k / 12f; c3.en(0.5f - 0.2f * cos(ang), 0.42f + 0.2f * sin(ang)) } + listOf(c3.en(0.7f, 0.36f), c3.en(0.3f, 0.36f))
                val bp = boca.map { s.p(it) }
                // El tizne que sube de la boca, en lengua, y la boca negra en arco.
                val sobre = s.p(c3.en(0.5f, 0.75f)); val k = s.escala(ESTUFA_X1, ESTUFA_Z0)
                mancha(sobre, 0.28f * k, 0.32f * k, a.cara(C.CENIZA, nn, en), 0.55f)
                mancha(s.p(c3.en(0.5f, 0.66f)), 0.18f * k, 0.16f * k, a.cara(C.CENIZA, nn, en), 0.6f)
                drawPath(poligono(bp), lerp(C.CENIZA, Color.Black, 0.6f))
                drawPath(poligono(bp), a.cara(lerp(cal, C.CENIZA, 0.35f), nn, en), style = Stroke((0.03f * k).coerceAtLeast(0.6f)))
                linea(s, c3, 0.22f, 0.36f, 0.78f, 0.36f, a.cara(lerp(cal, C.CENIZA, 0.2f), nn, en), (0.05f * s.k).coerceAtLeast(1f))
                val nicho = listOf(c3.en(0.8f, 0.68f), c3.en(0.9f, 0.68f), c3.en(0.9f, 0.78f), c3.en(0.8f, 0.78f)).map { s.p(it) }
                drawPath(poligono(nicho), lerp(C.CENIZA, Color.Black, 0.4f))
            }
        }
        // La chimenea, de ladrillo con lo encalado abajo y el hollín arriba.
        val chim = caja(s, a, CHIMENEA_X0, CHIMENEA_X1, ESTUFA_ALTO, CHIMENEA_ALTO, CHIMENEA_Z0, CHIMENEA_Z1, ladrillo, arriba = C.CENIZA)
        for ((c3, f) in chim) clipPath(f) {
            if (c3.arriba) return@clipPath
            val nn = s.normal(c3.n); val en = s.p(c3.centro); val col = a.cara(ladrillo, nn, en)
            for (j in 0 until 34) {
                val vv = j / 34f; linea(s, c3, 0f, vv, 1f, vv, mult(col, C.SOMBRA, 0.4f), (0.012f * s.k).coerceAtLeast(0.25f))
                for (u in listOf(0.25f, 0.75f)) { val uu = if (j % 2 == 0) u else u - 0.25f; linea(s, c3, uu, vv, uu, vv + 1f / 34f, mult(col, C.SOMBRA, 0.4f), (0.01f * s.k).coerceAtLeast(0.2f)) }
            }
            val y0 = s.p(c3.en(0.5f, 0f)).y; val y1 = s.p(c3.en(0.5f, 1f)).y
            drawRect(Brush.verticalGradient(0f to a.cara(C.CENIZA, nn, en), 0.4f to Color.Transparent, 0.75f to Color.Transparent, 1f to a.cara(cal, nn, en).copy(alpha = 0.85f), startY = y1, endY = y0), Offset(-10f, y1), Size(300f, y0 - y1))
        }
        if (v.nieve > 0.1f) nieveEncima(a, listOf(s.p(ESTUFA_X0, ESTUFA_ALTO, ESTUFA_Z0), s.p((ESTUFA_X0 + ESTUFA_X1) / 2f, ESTUFA_ALTO, ESTUFA_Z0), s.p(ESTUFA_X1, ESTUFA_ALTO, ESTUFA_Z0)).sortedBy { it.x }, (0.08f * s.k * v.nieve).coerceAtMost(4f), az)
    }

    /** Una viga caída, quemada: negra, cuarteada de través con su canto de ceniza; con [brasas], rojas en las grietas. */
    private fun DrawScope.viga(v: VistaNapoleon, p0: V3, p1: V3, r: Float, brasas: Boolean, az: Azar) {
        val s = S_CASA; val a = v.en(s.p((p0 + p1) * 0.5f))
        val negro = lerp(C.CENIZA, Color.Black, 0.3f)
        val sil = cilindro(s, a, p1, p0 - p1, r, r * 0.85f, Vec.largo(p0 - p1), negro, lerp(C.CENIZA, C.MADERA, 0.35f), 12)
        clipPath(sil) {
            for (j in 0 until 30) {
                val t = j / 30f; val q = s.p(p1 + (p0 - p1) * t + V3(0f, r * 0.9f, 0f))
                drawLine(lerp(C.CENIZA, C.LONA, 0.3f).copy(alpha = 0.45f), q + Offset(-1.6f, -0.6f), q + Offset(1.4f, 2.6f), 0.5f)
                if (brasas && az.real() < 0.45f) drawLine(a.brasa(az.entre(0.3f, 0.8f)), q + Offset(-0.6f, 0.8f), q + Offset(0.6f, 2.2f), 0.7f)
            }
        }
        // La punta astillada, quemada.
        val pp = s.p(p0)
        for (j in 0 until 3) drawLine(negro, pp, pp + Offset(az.entre(-3f, 3f), az.entre(-3f, 1f)), 1f, StrokeCap.Round)
        if (brasas) repeat(6) { val q = s.p(p1 + (p0 - p1) * az.entre(0.05f, 0.95f)); drawCircle(a.brasa(az.entre(0.4f, 0.9f)), az.entre(0.6f, 1.2f), q + Offset(0f, az.entre(0f, 1.5f))) }
    }

    /**
     * La rueca de la casa, apoyada en la pared, sin quemar del todo: la pala tallada de pie (ancha arriba, con sus
     * orejas, el cuello estrecho), sobre su asiento, pintada de rojo desvaído, con el filo de arriba chamuscado.
     */
    private fun DrawScope.rueca(v: VistaNapoleon, en: V3) {
        val s = S_CASA; val a = v.en(s.p(en))
        val madera = lerp(C.MADERA, C.LONA, 0.25f); val pintura = lerp(C.ROJO, C.MADERA, 0.4f)
        // El asiento: una tabla en el suelo, hacia el camino.
        cara(s, a, listOf(en + V3(0f, 0.04f, -0.12f), en + V3(0.75f, 0.04f, -0.12f), en + V3(0.75f, 0.04f, 0.12f), en + V3(0f, 0.04f, 0.12f)), V3(0f, 1f, 0f), madera)
        // La pala, de pie y un poco recostada en la pared (mira al camino).
        val inc = V3(-0.14f, 1f, 0f)
        fun pt(w: Float, h: Float) = en + V3(0.05f, 0.04f, 0f) + Vec.unidad(inc) * (h * 1.2f) + V3(0f, 0f, w * 1.2f)
        val contorno = listOf(pt(-0.07f, 0f), pt(-0.05f, 0.35f), pt(-0.12f, 0.48f), pt(-0.16f, 0.78f), pt(-0.2f, 0.84f), pt(-0.14f, 0.9f), pt(-0.08f, 0.86f), pt(0f, 0.92f), pt(0.08f, 0.86f), pt(0.14f, 0.9f), pt(0.2f, 0.84f), pt(0.16f, 0.78f), pt(0.12f, 0.48f), pt(0.05f, 0.35f), pt(0.07f, 0f))
        val f = cara(s, a, contorno, Vec.unidad(V3(1f, 0.14f, 0f)), madera) ?: return
        clipPath(f) {
            // La pintura: un sol de rosetas y unas rayas, desvaídas; lo de arriba, negro del fuego.
            val c = s.p(pt(0f, 0.66f)); val k = s.escala(en.x, en.z)
            drawCircle(a.cara(pintura, V3(1f, 0.1f, 0f), c), 0.08f * k, c, style = Stroke(0.8f))
            drawCircle(a.cara(pintura, V3(1f, 0.1f, 0f), c), 0.03f * k, c)
            for (h in listOf(0.5f, 0.54f)) drawLine(a.cara(pintura, V3(1f, 0.1f, 0f), c), s.p(pt(-0.12f, h)), s.p(pt(0.12f, h)), 0.6f)
            val yTop = s.p(pt(0f, 0.92f)).y
            drawRect(Brush.verticalGradient(0f to lerp(C.CENIZA, Color.Black, 0.3f), 1f to Color.Transparent, startY = yTop, endY = yTop + 0.25f * k), Offset(c.x - 30f, yTop - 1f), Size(60f, 0.25f * k + 2f))
        }
    }

    /** El banco de la casa, volcado al pie de la pared: el tablón de canto y las dos patas al aire, una rota. */
    private fun DrawScope.banco(v: VistaNapoleon, az: Azar) {
        val s = S_CASA; val a = v.en(s.p(0.75f, 0f, 1.1f))
        val madera = lerp(C.MADERA, C.MADERA_GRIS, 0.35f)
        // El tablón, de canto en el suelo, a lo largo de la pared.
        caja(s, a, 0.62f, 0.68f, 0f, 0.3f, 0.3f, 1.9f, madera)
        // Las patas, que salen del tablón hacia el camino, al aire.
        for ((z, l) in listOf(0.5f to 0.42f, 1.65f to 0.22f)) {
            caja(s, a, 0.68f, 0.68f + l, 0.1f, 0.18f, z - 0.04f, z + 0.04f, lerp(madera, C.CENIZA, 0.15f))
            if (l < 0.3f) { val pp = s.p(0.68f + l, 0.14f, z); drawLine(lerp(C.LONA, C.MADERA, 0.4f), pp, pp + Offset(1.5f, -1.5f), 0.7f) }
        }
        // Lo chamuscado de un extremo.
        mancha(s.p(0.65f, 0.15f, 1.85f), 0.12f * s.k, 0.1f * s.k, lerp(C.CENIZA, Color.Black, 0.3f), 0.7f)
    }

    /** El caldero de hierro de la casa, tumbado en el suelo con la boca hacia quien mira, con su asa y su tizne. */
    private fun DrawScope.caldero(v: VistaNapoleon, en: V3) {
        val s = S_CASA; val a = v.en(s.p(en))
        val eje = Vec.unidad(V3(0.35f, 0.15f, 1f))
        val base = en + eje * 0.18f
        drawOval(v.p.sombra(C.BARRO).copy(alpha = 0.45f), Offset(s.p(en).x - 0.25f * s.k, s.p(en).y + 0.08f * s.k), Size(0.5f * s.k, 0.1f * s.k))
        cilindro(s, a, base, eje * -1f, 0.13f, 0.18f, 0.32f, C.HIERRO, null, 16, tapaAbajo = C.HIERRO)
        // La boca, negra por dentro, con su borde de luz.
        val boca = PiezasNapoleon.circulo(s, en - eje * 0.14f, eje, 0.18f, 18)
        drawPath(poligono(boca), lerp(C.CENIZA, Color.Black, 0.5f))
        drawPath(TrazosValle.cerrada(boca), a.plano(lerp(C.HIERRO, C.PLATA, 0.3f), s.p(en), 0.4f), style = Stroke(0.8f))
        // El asa, de alambre, caída.
        val q0 = boca[2]; val q1 = boca[9]; drawPath(curva(listOf(q0, (q0 + q1) / 2f + Offset(0f, 0.15f * s.k), q1)), a.plano(C.HIERRO, s.p(en), 0f), style = Stroke(0.6f))
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
        // La astilla grande, la que quedó en pie: larga, clara, de madera viva, con su sombra al lado y la corteza en el canto.
        run {
            val b = Offset(p.x + 3f, ROTURA.y + 8f); val t = Offset(p.x + 9f, ROTURA.y - 38f)
            val astilla = poligono(b + Offset(-4f, 0f), b + Offset(-1f, -14f), t, b + Offset(4.5f, -10f), b + Offset(5f, 0f))
            drawPath(astilla, a.plano(lerp(C.LONA, C.MADERA, 0.2f), b, 0.6f, 0f, 0.7f))
            drawLine(a.plano(lerp(C.LONA, C.MADERA, 0.5f), b, -0.6f), b + Offset(-3f, 0f), t, 1.2f, StrokeCap.Round)
            drawLine(lerp(a.plano(C.LONA, b, 0.8f), Color.White, 0.35f).copy(alpha = 0.8f * (0.3f + 0.7f * v.r.dia)), b + Offset(2.5f, -2f), t + Offset(0.5f, 3f), 0.8f, StrokeCap.Round)
            drawLine(a.plano(C.MADERA_OSC, b, 0f), b + Offset(4.5f, -1f), b + Offset(4f, -10f), 1.2f)
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
     * Un cajón de munición reventado: la caja verde, de pie, con lo de arriba abierto y negro por dentro, las tablas de la
     * tapa y del frente saltadas hacia fuera, astilladas; los flejes de hierro; el hollín que sube por los costados; una
     * rueda de pie detrás y otra tumbada delante; la tapa, tirada; las balas de cañón y los cartuchos de papel por el
     * suelo, y la nieve tiznada de negro alrededor.
     */
    private fun DrawScope.cajonReventado(v: VistaNapoleon) {
        val s = S_CAJON; val a = v.en(s.pie); val az = Azar(Semilla.de("napoleon", "cajon"))
        val verde = lerp(C.PINO_LUZ, C.CAPOTE, 0.35f)
        val negro = lerp(C.CENIZA, Color.Black, 0.3f)
        // La quemadura en la nieve: negra en medio, y alrededor el abanico de tierra que levantó la explosión, en rayos.
        val tierra = lerp(C.BARRO, C.CENIZA, 0.45f)
        for (j in 0 until 26) {
            val ang = 2f * PI.toFloat() * j / 26f + az.entre(-0.1f, 0.1f); val l = az.entre(1.1f, 2.6f)
            val p0 = V3(cos(ang) * 0.5f, 0.01f, sin(ang) * 0.35f); val p1 = V3(cos(ang) * l, 0.01f, sin(ang) * l * 0.7f)
            val q0 = s.p(p0); val q1 = s.p(p1)
            drawLine(tierra.copy(alpha = 0.55f), q0, q1, az.entre(1.5f, 3.5f), StrokeCap.Round)
            repeat(3) { val t = az.entre(0.4f, 1.15f); mancha(q0 + (q1 - q0) * t, az.entre(1f, 2.4f), az.entre(0.5f, 1.1f), tierra, 0.7f) }
        }
        mancha(s.p(0f, 0f, 0.05f), 1.25f * s.k, 0.4f * s.k, lerp(C.CENIZA, C.BARRO, 0.3f), 0.85f)
        mancha(s.p(0.1f, 0f, 0f), 0.85f * s.k, 0.24f * s.k, lerp(C.CENIZA, Color.Black, 0.3f), 0.9f)
        // La rueda de detrás, de pie, ladeada, con dos rayos rotos.
        rueda(s, a, V3(-0.78f, 0.56f, 0.42f), 0.56f, Vec.unidad(V3(1f, 0.08f, 0.2f)), 0.07f, 12, 2, C.MADERA_GRIS, 0f, 0.4f, v.nieve)
        // El cajón: verde, sin tapa; dentro, negro.
        val caras = caja(s, a, -0.6f, 0.6f, 0f, 0.5f, -0.33f, 0.33f, verde, arriba = negro)
        for ((c3, f) in caras) clipPath(f) {
            val n = s.normal(c3.n); val en = s.p(c3.centro)
            if (c3.arriba) {
                // Lo de dentro: los cartuchos quemados, rollos de papel negros con su canto gris.
                repeat(8) { val q = s.p(az.entre(-0.5f, 0.5f), 0.5f, az.entre(-0.25f, 0.25f)); drawLine(lerp(C.CENIZA, C.LONA, 0.3f), q, q + Offset(2.5f, 0.6f), 1.4f, StrokeCap.Round) }
                return@clipPath
            }
            // Las tablas del costado, los flejes de hierro y el hollín que sube desde arriba.
            for (u in listOf(0.33f, 0.66f)) linea(s, c3, u, 0f, u, 1f, mult(a.cara(verde, n, en), C.SOMBRA, 0.45f), (0.008f * s.k).coerceAtLeast(0.3f))
            for (u in listOf(0.1f, 0.9f)) linea(s, c3, u, 0f, u, 1f, a.cara(C.HIERRO, n, en), (0.03f * s.k).coerceAtLeast(0.6f))
            val yTop = s.p(c3.en(0.5f, 1f)).y; val yBot = s.p(c3.en(0.5f, 0f)).y
            drawRect(Brush.verticalGradient(0f to negro.copy(alpha = 0.9f), 0.55f to negro.copy(alpha = 0.25f), 1f to Color.Transparent, startY = yTop, endY = yBot), Offset(-10f, yTop - 2f), Size(400f, yBot - yTop + 4f))
        }
        // Las tablas saltadas: de los bordes de arriba, hacia fuera y hacia arriba, astilladas en la punta.
        val tablas = listOf(
            Triple(V3(-0.35f, 0.5f, -0.33f), V3(-0.25f, 0.7f, -0.66f), V3(1f, 0f, 0f)), Triple(V3(0.05f, 0.5f, -0.33f), V3(0.1f, 0.85f, -0.5f), V3(1f, 0f, 0f)),
            Triple(V3(0.42f, 0.5f, -0.33f), V3(0.45f, 0.4f, -0.9f), V3(1f, 0f, 0f)), Triple(V3(0.6f, 0.5f, -0.05f), V3(0.8f, 0.75f, 0.1f), V3(0f, 0f, 1f)),
            Triple(V3(0.6f, 0.5f, 0.2f), V3(0.9f, 0.3f, 0.3f), V3(0f, 0f, 1f)), Triple(V3(-0.6f, 0.5f, 0.0f), V3(-0.85f, 0.85f, 0.05f), V3(0f, 0f, 1f)),
            Triple(V3(-0.15f, 0.5f, -0.33f), V3(-0.1f, 0.95f, -0.25f), V3(1f, 0f, 0f)), Triple(V3(0.25f, 0.5f, -0.33f), V3(0.35f, 0.9f, -0.35f), V3(1f, 0f, 0f)),
            Triple(V3(-0.6f, 0.5f, -0.2f), V3(-0.95f, 0.45f, -0.3f), V3(0f, 0f, 1f)), Triple(V3(0.6f, 0.5f, -0.25f), V3(0.95f, 0.65f, -0.4f), V3(0f, 0f, 1f)),
        )
        // Las tablas que volaron y cayeron en la nieve, alrededor.
        repeat(7) {
            val x = az.entre(-1.9f, 2.1f); val z = az.entre(-1.3f, 0.9f); val ang = az.entre(0f, 3.1f); val l = az.entre(0.35f, 0.7f)
            val e = V3(cos(ang), 0f, sin(ang)); val nn = V3(-sin(ang), 0f, cos(ang))
            cara(s, a, listOf(V3(x, 0.02f, z), V3(x, 0.02f, z) + e * l, V3(x, 0.02f, z) + e * l + nn * 0.08f, V3(x, 0.02f, z) + nn * 0.08f), V3(0f, 1f, 0f), lerp(verde, C.LONA, az.entre(0.05f, 0.3f)))
        }
        for ((p0, d0, e) in tablas) {
            val d = Vec.unidad(d0); val l = az.entre(0.42f, 0.75f); val w = 0.09f
            val q = listOf(p0 - e * (w / 2f), p0 + e * (w / 2f), p0 + e * (w / 2f) + d * l, p0 - e * (w / 2f) + d * l)
            val nrm = Vec.unidad(Vec.cruz(e, d)).let { if (s.seVe(p0, it)) it else it * -1f }
            cara(s, a, q, nrm, lerp(verde, C.LONA, 0.15f))
            // La punta astillada, clara, y lo quemado del arranque.
            val punta = s.p(p0 + d * l); drawLine(a.plano(lerp(C.LONA, C.MADERA, 0.4f), punta, 0.5f), punta, punta + Offset(az.entre(-2f, 2f), -az.entre(1.5f, 3f)), 0.7f)
            drawLine(negro, s.p(p0 - e * (w / 2f)), s.p(p0 + e * (w / 2f)), (0.04f * s.k).coerceAtLeast(0.8f))
        }
        // La tapa, arrancada, de lado en la nieve.
        cara(s, a, listOf(V3(0.8f, 0f, -0.75f), V3(1.65f, 0.04f, -0.45f), V3(1.6f, 0.34f, -0.4f), V3(0.78f, 0.3f, -0.7f)), Vec.unidad(V3(-0.25f, 0.3f, -1f)), verde)
        // La rueda de delante, tumbada.
        rueda(s, a, V3(-1.3f, 0.06f, -0.55f), 0.52f, Vec.unidad(V3(0.12f, 1f, -0.3f)), 0.07f, 12, 4, C.MADERA_GRIS, 0f, 1.1f, v.nieve)
        // Las balas de cañón, sueltas, que rodaron: cada una con su surco en la nieve desde el cajón, negras, con su brillo y su sombra.
        repeat(9) {
            val bx = az.entre(-1.6f, 2.1f); val bz = az.entre(-1.3f, 0.4f)
            val q = s.p(bx, 0.06f, bz); val r = 0.065f * s.k
            val desde = s.p(bx * 0.35f, 0.01f, bz * 0.35f)
            drawLine(a.plano(C.NIEVE_SOMBRA, q, 0f).copy(alpha = 0.6f), desde, q, (0.08f * s.k).coerceAtLeast(1f), StrokeCap.Round)
            drawOval(v.p.sombra(C.NIEVE_SOMBRA).copy(alpha = 0.5f), Offset(q.x - r * 1.2f, q.y + r * 0.4f), Size(r * 2.6f, r * 0.8f))
            drawCircle(a.plano(C.HIERRO, q, 0f), r, q)
            drawCircle(lerp(a.arriba(C.HIERRO, q), Color.White, 0.3f), r * 0.35f, q + Offset(-r * 0.35f, -r * 0.4f))
        }
        repeat(10) { val q = s.p(az.entre(-1.6f, 1.9f), 0.02f, az.entre(-1.2f, 0.6f)); val ang = az.entre(0f, 3.1f); drawLine(a.arriba(C.LONA, q), q, q + Offset(cos(ang), sin(ang) * 0.4f) * 3f, 1.4f, StrokeCap.Round) }
        if (v.nieve > 0.3f) nieveEncima(a, listOf(s.p(-0.6f, 0.5f, 0.33f), s.p(-0.3f, 0.5f, 0.33f), s.p(0f, 0.5f, 0.33f)).sortedBy { it.x }, (0.03f * s.k * v.nieve), az)
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

    /** La esquina de una caja de munición enterrada en la nieve: dos caras de tablas verdes, el fleje, la tapa con su nieve. */
    private fun DrawScope.cajaEnterrada(v: VistaNapoleon, c: Offset) {
        val a = v.en(c); val verde = lerp(C.PINO_LUZ, C.CAPOTE, 0.4f)
        val frente = poligono(c + Offset(-14f, 0f), c + Offset(2f, 3f), c + Offset(2f, -9f), c + Offset(-14f, -12f))
        val lado = poligono(c + Offset(2f, 3f), c + Offset(13f, -2f), c + Offset(13f, -13f), c + Offset(2f, -9f))
        val tapa = poligono(c + Offset(-14f, -12f), c + Offset(2f, -9f), c + Offset(13f, -13f), c + Offset(-3f, -16f))
        drawPath(frente, a.cara(verde, V3(0f, 0f, -1f), c)); drawPath(lado, a.cara(verde, V3(1f, 0f, -0.2f), c)); drawPath(tapa, a.arriba(lerp(verde, C.NIEVE, 0.15f), c))
        drawLine(a.cara(C.HIERRO, V3(0f, 0f, -1f), c), c + Offset(-9f, 1f), c + Offset(-9f, -11f), 1.1f)
        drawLine(mult(a.cara(verde, V3(0f, 0f, -1f), c), C.SOMBRA, 0.45f), c + Offset(-14f, -6f), c + Offset(2f, -3f), 0.5f)
        if (v.nieve > 0.3f) drawPath(TrazosValle.blando(c + Offset(-1f, -14f), 13f, 3.2f, Azar(Semilla.de("napoleon", "caja", "nieve")), 8, 0.2f, 0.5f), a.arriba(C.NIEVE, c))
    }

    /** Un chacó medio enterrado, ladeado en la nieve: el fieltro negro, la placa de latón del águila, la escarapela y la borla roja. */
    private fun DrawScope.chacoEnterrado(v: VistaNapoleon, c: Offset) {
        val a = v.en(c); val fieltro = lerp(C.CENIZA, C.AZUL, 0.18f)
        // El cuerpo, ladeado: más ancho arriba que abajo.
        val cuerpo = poligono(c + Offset(-6f, 2f), c + Offset(4f, 4f), c + Offset(9f, -9f), c + Offset(-3f, -12f))
        drawPath(cuerpo, a.plano(fieltro, c, 0.3f))
        drawLine(lerp(a.plano(fieltro, c, 0.8f), Color.White, 0.15f), c + Offset(4f, 4f), c + Offset(9f, -9f), 0.8f)
        // La copa, una elipse arriba, con su ribete.
        drawOval(a.arriba(lerp(fieltro, C.CENIZA, 0.2f), c), Offset(c.x - 4.5f, c.y - 14.5f), Size(14f, 5f))
        drawOval(a.plano(C.LATON, c, 0f), Offset(c.x - 4.5f, c.y - 14.5f), Size(14f, 5f), style = Stroke(0.6f))
        // La placa en rombo, la escarapela y la borla.
        val pl = c + Offset(1.5f, -4f)
        drawPath(poligono(pl + Offset(0f, -3.2f), pl + Offset(2.4f, 0f), pl + Offset(0f, 3.2f), pl + Offset(-2.4f, 0f)), a.plano(C.LATON, pl, 0.4f))
        drawCircle(C.ROJO.let { a.plano(it, pl, 0f) }, 1.4f, pl + Offset(1f, -7f)); drawCircle(a.plano(C.NIEVE, pl, 0f), 0.7f, pl + Offset(1f, -7f))
        drawCircle(a.plano(C.ROJO, pl, 0.4f), 1.8f, c + Offset(4f, -15f))
        if (v.nieve > 0.3f) drawPath(TrazosValle.blando(c + Offset(3f, -15.5f), 6f, 1.6f, Azar(Semilla.de("napoleon", "chaco", "nieve")), 7, 0.2f, 0.5f), a.arriba(C.NIEVE, c))
    }

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
            if (i == 1) {
                // Dos abetos jóvenes enterrados hasta media altura, que asoman cargados entre la primera ola y la segunda.
                for ((pie, alto) in listOf(Offset(58f, 240f) to 70f, Offset(136f, 266f) to 44f)) pino(v.en(pie), pie, alto, alto * 0.72f, 1f, Azar(Semilla.de("napoleon", "abetito", pie.x.toInt())), 0f, 0.85f, caidaPropia = 0.62f)
                // La esquina de una caja de munición que asoma, con su fleje y su nieve encima.
                cajaEnterrada(v, Offset(160f, 250f))
            }
            if (i == 2) chacoEnterrado(v, Offset(40f, 264f))
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
