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
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Moscu
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.Fondo
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.H
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.k
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.w
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * La lejanía del vivac: el cielo y el horizonte de cada tramo de la campaña ([VivacNapoleon.Fondo]). Lo lejano con
 * profundidad (regla 10): lomas que se enciman, cada una con su cara de luz y de sombra; bosques por delante y por
 * detrás; los pueblos en dos o tres hileras, cada casa con su lado de luz y su sombra corta, con la iglesia del sitio.
 *
 * Cada tramo, lo suyo (lo que dice el cuento):
 * - **Francia**: lomas suaves de labranza, hileras de álamos **[Aproximación]**, un pueblo de piedra rubia y teja con su
 *   campanario cuadrado (el de Borgoña lo describe así el cuento; los de la Champaña, **[Aproximación]**).
 * - **Alemania**: «casas con vigas de madera pintadas, tejados empinados, iglesias con torres en punta», en colinas con
 *   bosques de hoja.
 * - **Polonia**: «bosques de pinos y pueblos de casas de madera con techo de paja».
 * - **El Niemen**: el río ancho con los puentes de barcas de los pontoneros y, del otro lado, «Solo bosque»; el 14 de
 *   diciembre, helado, y del otro lado «un pueblo con casas de verdad, con humo en las chimeneas».
 * - **Rusia**: el llano sin fin, bosques de pinos bajos y aldeas de casas de madera.
 * - **Smolensk**: «murallas viejas sobre un río», con sus torres, y la ciudad que arde.
 * - **Borodino**: «unos reductos de tierra llenos de cañones… en el humo».
 * - **Moscú**: «cientos de cúpulas doradas y de colores, torres, palacios»; luego en llamas; luego quemada («un campo de
 *   chimeneas negras», «con sus cúpulas que todavía brillan»).
 * - **Prusia** (y el hospital): pueblos de ladrillo con tejados empinados nevados y humo en las chimeneas.
 * - **El Rin**: el río ancho y, del otro lado, Maguncia con su catedral de torres rojas **[Aproximación]**.
 * - **Los Cien Días**: lomas de trigo alto, una granja tapiada, hileras de álamos **[Aproximación]**.
 * - **El cuartel** (provisional): la ciudad con su muralla y el convento viejo con su espadaña, en invierno.
 * - **El pueblo** (provisional): la ladera de viñas en filas, el pueblo de piedra rubia con su iglesia, el río y los álamos.
 */
internal object VivacLejos {
    private val C = ColorNapoleon

    /** Dónde se apoyan las nubes (el horizonte que se le da a `nubes`). */
    const val CIELO = H - 4f

    /** Dónde arde la ciudad (Moscú, Smolensk): el centro de su resplandor. */
    val ARDE = Offset(306f, H - 10f)

    // ---- Los colores del paisaje: mezclas de la paleta del tema.
    private val VERDE = lerp(C.PINO, C.HOJA, 0.55f)                              // la hierba de primavera, verde apagado
    private val VERDE_OSC = lerp(C.PINO, C.HOJA, 0.25f)
    private val OCRE = lerp(C.HOJA, C.LONA, 0.3f)                                // el trigo maduro
    private val PIEDRA = lerp(C.LONA, C.HOJA, 0.28f)                             // la piedra rubia de Francia
    private val TEJA = lerp(C.ROJO, C.MADERA, 0.45f)
    private val PIZARRA = lerp(C.HIERRO, C.AZUL, 0.3f)
    private val CAL = lerp(C.LONA, C.NIEVE, 0.5f)
    private val PAJA = lerp(C.HOJA, C.CAPOTE, 0.35f)
    private val LADRILLO = lerp(C.ROJO, C.MADERA, 0.38f)
    private val TRONCO = lerp(C.MADERA, C.CAPOTE, 0.35f)

    // ---------------------------------------------------------------- El cielo

    fun DrawScope.cielo(v: VistaVivac) {
        val m = v.m
        cielo(v.l, H, m.semilla("cielo"))
        astros(v.l, H, m.semilla("astros"))
        nubes(v.l, CIELO, m.ficha.clima, m.semilla("nubes"))
        // La ciudad que arde: el cielo rojo encima («el cielo está rojo como de día») y el humo pesado, quieto.
        if (v.incendio > 0.01f) {
            val k = v.incendio
            drawRect(Brush.radialGradient(listOf(Color(0xFFFF8A3A).copy(alpha = (0.22f + 0.45f * v.noche) * k), Color(0xFFB0402A).copy(alpha = (0.14f + 0.25f * v.noche) * k), Color.Transparent), ARDE, 190f), Offset(0f, 0f), Size(ANCHO, H + 2f), blendMode = BlendMode.Plus)
            val az = Azar(Semilla.de("napoleon", "vivac", "humo", "incendio"))
            repeat(14) {
                val q = Offset(ARDE.x + az.entre(-90f, 70f) + it * 2f, H - az.entre(14f, 90f))
                val col = lerp(v.l.tono(lerp(C.CENIZA, C.CAPOTE, 0.3f)), Color(0xFFE8703A), (0.25f + 0.4f * v.noche) * (1f - (H - q.y) / 110f))
                mancha(q, az.entre(18f, 46f), az.entre(8f, 18f), col, 0.6f * k)
            }
        }
    }

    // ---------------------------------------------------------------- La lejanía

    fun DrawScope.lejos(v: VistaVivac) {
        when (v.fondo) {
            Fondo.FRANCIA -> francia(v, false)
            Fondo.PUEBLO -> francia(v, true)
            Fondo.CIEN_DIAS -> cienDias(v)
            Fondo.ALEMANIA -> alemania(v, false)
            Fondo.RIN -> alemania(v, true)
            Fondo.POLONIA -> polonia(v)
            Fondo.NIEMEN -> niemen(v)
            Fondo.RUSIA -> rusia(v)
            Fondo.SMOLENSK -> smolensk(v)
            Fondo.BORODINO -> borodino(v)
            Fondo.MOSCU -> moscu(v)
            Fondo.PRUSIA -> prusia(v, false)
            Fondo.HOSPITAL -> prusia(v, true)
            Fondo.CUARTEL -> cuartel(v)
        }
        // La calima del horizonte, que despega la lejanía del campo.
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, v.l.bruma.copy(alpha = 0.3f)), H - 3f, H + 1.5f), Offset(0f, H - 3f), Size(ANCHO, 4.5f))
    }

    // ---------------------------------------------------------------- Las piezas lejanas

    /** El color de las lomas lejanas hoy: la hierba de la estación, el trigo, el rastrojo o la nieve. */
    private fun tierra(v: VistaVivac): Color {
        val base = when (v.m.ficha.estacion) {
            Estacion.PRIMAVERA -> VERDE
            Estacion.VERANO -> lerp(VERDE, OCRE, 0.45f)
            Estacion.OTONO -> lerp(OCRE, C.BARRO, 0.35f)
            else -> lerp(C.HOJA, C.BARRO, 0.45f)
        }
        return lerp(base, C.NIEVE, (v.nieve * 1.2f).coerceAtMost(1f))
    }

    /** La cresta de una loma, de lado a lado, a [alto] sobre el horizonte, con [semilla] y [ondas] ondulaciones. */
    private fun cresta(alto: Float, semilla: String, ondas: Float = 5f, desde: Float = -20f, hasta: Float = ANCHO + 20f, base: Float = H): List<Offset> {
        val az = Azar(Semilla.de("napoleon", "vivac", "loma", semilla)); val f1 = az.entre(0f, 6f); val f2 = az.entre(0f, 6f)
        return (0..26).map { i -> val u = i / 26f; val x = desde + (hasta - desde) * u
            Offset(x, base - alto * (0.5f + 0.28f * sin(u * ondas + f1) + 0.16f * sin(u * ondas * 2.3f + f2) + az.entre(-0.05f, 0.05f))) }
    }

    /**
     * Una loma lejana con su cara de sombra (del lado contrario al sol) y su cresta al sol, y en ella, si [bosque], manchas
     * de bosque (pinos o árboles de hoja). Devuelve su forma.
     */
    private fun DrawScope.loma(v: VistaVivac, cr: List<Offset>, color: Color, lejos: Float, bosque: Int = 0, pinos: Boolean = true, fondo: Float = H + 4f): Path {
        val p = v.p; val lado = if (p.solALaDerecha) 1f else -1f
        val forma = curva(cr, fondo)
        drawPath(forma, p.medio(color, lejos))
        clipPath(forma) {
            for (i in 1 until cr.size - 1) {
                val a = cr[i - 1]; val b = cr[i]; val c = cr[i + 1]
                if ((b.y - a.y) * lado > 0f) drawPath(poligono(a, b, Offset(b.x, fondo), Offset(a.x, fondo)), p.sombra(color, lejos).copy(alpha = 0.45f * v.r.dia + 0.1f))
                if (c.y > b.y && b.y < a.y) mancha(b + Offset(0f, 1.5f), 9f, 2f, p.luz(color, lejos), 0.45f * v.r.dia)
            }
            if (bosque > 0) {
                val az = Azar(Semilla.de("napoleon", "vivac", "bosque", cr[3].y.toInt(), bosque))
                repeat(bosque) {
                    val cx = az.entre(-10f, ANCHO + 10f); val y = TrazosValle.alturaEn(cr, cx) + az.entre(1f, 8f)
                    manchaDeBosque(v, cx, y, az.entre(16f, 50f), az.entre(2.4f, 4.8f) * (1.25f - lejos * 0.6f), lejos, pinos, az)
                }
            }
        }
        return forma
    }

    /**
     * Una mancha de bosque lejano, apoyada en [y]: árboles chicos uno junto a otro, cada uno con su lado de luz; los pinos,
     * de copa en punta y desigual; los de hoja, redondos.
     */
    private fun DrawScope.manchaDeBosque(v: VistaVivac, cx: Float, y: Float, ancho: Float, alto: Float, lejos: Float, pinos: Boolean, az: Azar) {
        val p = v.p; val n = (ancho / (alto * 0.55f)).toInt().coerceIn(3, 30)
        val oscuro = if (pinos) C.PINO_OSC else lerp(C.PINO, C.HOJA, if (v.m.ficha.estacion == Estacion.OTONO) 0.6f else 0.3f)
        val claro = if (pinos) C.PINO else lerp(oscuro, C.HOJA, 0.35f)
        for (i in 0 until n) {
            val x = cx - ancho / 2f + ancho * i / n + az.entre(-1f, 1f); val h = alto * az.entre(0.7f, 1.3f) * (1f - 0.5f * kotlin.math.abs(i - n / 2f) / n)
            val y0 = y + az.entre(-0.8f, 0.8f)
            if (pinos) {
                val sil = poligono(listOf(Offset(x - h * 0.3f, y0), Offset(x - h * 0.2f, y0 - h * 0.4f), Offset(x - h * 0.08f, y0 - h * 0.55f), Offset(x, y0 - h), Offset(x + h * 0.1f, y0 - h * 0.6f), Offset(x + h * 0.22f, y0 - h * 0.42f), Offset(x + h * 0.3f, y0)))
                drawPath(sil, p.medio(oscuro, lejos))
                drawPath(poligono(Offset(x, y0 - h), Offset(x + h * 0.3f * (if (p.solALaDerecha) 1f else -1f), y0), Offset(x, y0)), p.lado(claro, lejos).copy(alpha = 0.6f))
            } else {
                drawOval(p.medio(oscuro, lejos), Offset(x - h * 0.42f, y0 - h), Size(h * 0.84f, h))
                val lado = if (p.solALaDerecha) 1f else -1f
                drawOval(p.luz(claro, lejos).copy(alpha = 0.55f * v.r.dia + 0.15f), Offset(x - h * 0.3f + lado * h * 0.1f, y0 - h * 0.95f), Size(h * 0.5f, h * 0.55f))
            }
        }
    }

    /** Una hilera de álamos lejanos a lo largo de [desde]–[hasta], a la profundidad que da su y. */
    private fun DrawScope.alamos(v: VistaVivac, desde: Offset, hasta: Offset, cuantos: Int, alto: Float, lejos: Float, semilla: Int) {
        val p = v.p; val az = Azar(Semilla.de("napoleon", "vivac", "alamos", semilla)); val lado = if (p.solALaDerecha) 1f else -1f
        val hoja = when (v.m.ficha.estacion) { Estacion.OTONO -> lerp(C.HOJA, C.LATON, 0.3f); Estacion.INVIERNO -> TRONCO; else -> lerp(C.PINO, C.HOJA, 0.4f) }
        val desnudo = v.m.ficha.estacion == Estacion.INVIERNO || v.nieve > 0.4f
        for (i in 0 until cuantos) {
            val t = (i + az.entre(-0.2f, 0.2f)) / cuantos; val q = desde + (hasta - desde) * t; val h = alto * az.entre(0.8f, 1.15f) * (q.y - H + 4f) / (desde.y - H + 4f).coerceAtLeast(1f)
            if (h < 2f) continue
            sombraCorta(v.r, q, h * 0.3f, h * 0.6f)
            drawLine(p.medio(TRONCO, lejos), q, q + Offset(0f, -h * 0.4f), (h * 0.04f).coerceAtLeast(0.4f))
            val copa = poligono(listOf(q + Offset(-h * 0.1f, -h * 0.25f), q + Offset(-h * 0.13f, -h * 0.6f), q + Offset(-h * 0.05f, -h * 0.95f), q + Offset(0f, -h), q + Offset(h * 0.06f, -h * 0.92f), q + Offset(h * 0.13f, -h * 0.55f), q + Offset(h * 0.1f, -h * 0.25f)))
            if (desnudo) { for (j in 0 until 5) { val a0 = q + Offset(0f, -h * (0.3f + 0.12f * j)); drawLine(p.medio(TRONCO, lejos).copy(alpha = 0.8f), a0, a0 + Offset((if (j % 2 == 0) -1f else 1f) * h * 0.1f, -h * 0.2f), 0.4f) } }
            else { drawPath(copa, p.medio(hoja, lejos)); clipPath(copa) { drawRect(p.lado(lerp(hoja, C.LONA, 0.15f), lejos), q + Offset(if (lado > 0f) 0f else -h * 0.2f, -h), Size(h * 0.2f, h)) } }
        }
    }

    /** Los estilos de casa de cada tierra. */
    internal enum class Estilo { PIEDRA, ENTRAMADO, MADERA, LADRILLO }

    /**
     * Una casa lejana, en tres cuartos: el muro de delante, el costado (en sombra o al sol), el tejado con sus dos caras y,
     * según el [estilo], las vigas pintadas, los troncos, la paja o la chimenea. [pie]: la esquina de abajo del frente; [w],
     * [h]: el ancho y lo alto de la pared, en el lienzo. Con [nieve], el tejado blanco; con [quemada], solo los muros negros.
     */
    private fun DrawScope.casa(v: VistaVivac, pie: Offset, w: Float, h: Float, estilo: Estilo, lejos: Float, az: Azar, quemada: Boolean = false, humea: Boolean = false) {
        val p = v.p; val lado = if (p.solALaDerecha) 1f else -1f
        val fondo = w * az.entre(0.45f, 0.7f); val dx = fondo * 0.55f * (if (pie.x < ANCHO / 2f) 1f else -1f); val dy = -fondo * 0.18f
        val pendiente = when (estilo) { Estilo.PIEDRA -> 0.4f; Estilo.ENTRAMADO -> 0.85f; Estilo.MADERA -> 0.7f; Estilo.LADRILLO -> 0.8f }
        val muro = when (estilo) { Estilo.PIEDRA -> PIEDRA; Estilo.ENTRAMADO -> CAL; Estilo.MADERA -> lerp(C.MADERA, C.MADERA_GRIS, 0.5f); Estilo.LADRILLO -> LADRILLO }
        val techo = when (estilo) { Estilo.PIEDRA -> TEJA; Estilo.ENTRAMADO -> lerp(TEJA, PIZARRA, 0.5f); Estilo.MADERA -> PAJA; Estilo.LADRILLO -> PIZARRA }
        val a = pie; val b = pie + Offset(w, 0f); val c = b + Offset(dx, dy); val d = a + Offset(dx, dy)
        sombraCorta(v.r, pie + Offset(w / 2f, 0f), w * 1.3f, h * 1.2f)
        if (quemada) {
            val negro = lerp(C.CENIZA, muro, 0.15f)
            drawPath(poligono(a, b, b + Offset(0f, -h * 0.7f), a + Offset(w * 0.6f, -h * 0.4f), a + Offset(0f, -h * 0.9f)), p.medio(negro, lejos))
            drawLine(p.medio(lerp(C.CENIZA, C.LONA, 0.3f), lejos), a + Offset(w * 0.75f, 0f), a + Offset(w * 0.75f, -h * 1.5f), (w * 0.12f).coerceAtLeast(0.6f))
            return
        }
        // El costado.
        val costado = poligono(b, c, c + Offset(0f, -h), b + Offset(0f, -h))
        val costadoAlSol = (dx > 0f) == (lado > 0f)
        drawPath(costado, if (costadoAlSol) p.luz(muro, lejos) else p.sombra(muro, lejos))
        // El frente (el hastial si el tejado va de lado a lado: aquí, el frente es el muro largo).
        val frente = poligono(a, b, b + Offset(0f, -h), a + Offset(0f, -h))
        drawPath(frente, p.frente(muro, lejos))
        // Lo propio del estilo, en el frente.
        when (estilo) {
            Estilo.ENTRAMADO -> clipPath(frente) { val col = p.medio(C.MADERA_OSC, lejos); for (j in 0..3) { val x = a.x + w * j / 3f; drawLine(col, Offset(x, a.y), Offset(x, a.y - h), (w * 0.04f).coerceAtLeast(0.3f)) }
                drawLine(col, a + Offset(0f, -h * 0.5f), b + Offset(0f, -h * 0.5f), (w * 0.04f).coerceAtLeast(0.3f)); drawLine(col, a + Offset(0f, -h * 0.5f), a + Offset(w / 3f, -h), (w * 0.03f).coerceAtLeast(0.25f)) }
            Estilo.MADERA -> clipPath(frente) { val col = mult(p.medio(muro, lejos), C.SOMBRA, 0.35f); for (j in 1..4) { val y = a.y - h * j / 5f; drawLine(col, Offset(a.x, y), Offset(b.x, y), 0.3f) } }
            Estilo.PIEDRA, Estilo.LADRILLO -> Unit
        }
        // La puerta y una ventana (de noche, encendida en los pueblos con gente).
        if (w > 4f) {
            drawRect(p.sombra(C.MADERA_OSC, lejos), a + Offset(w * 0.15f, -h * 0.55f), Size(w * 0.14f, h * 0.55f))
            val encendida = v.noche > 0.5f && az.real() < 0.5f && v.fondo != Fondo.MOSCU
            drawRect(if (encendida) lerp(C.LUMBRE, Color(0xFFFFE0A0), 0.4f) else p.sombra(C.CENIZA, lejos), a + Offset(w * 0.55f, -h * 0.7f), Size(w * 0.16f, h * 0.22f))
        }
        // El tejado: la cara de delante y la de detrás, con su alero.
        val alto = w * 0.5f * pendiente + 0.5f
        val cumbreA = Offset(a.x + dx * 0.5f, a.y - h - alto + dy * 0.5f); val cumbreB = cumbreA + Offset(w, 0f)
        val faldon = poligono(a + Offset(-w * 0.05f, -h), b + Offset(w * 0.05f, -h), cumbreB, cumbreA)
        val nevado = v.nieve > 0.35f
        drawPath(faldon, if (nevado) p.arriba(C.NIEVE, lejos) else p.luz(techo, lejos))
        val hastial = poligono(b + Offset(w * 0.05f, -h), c + Offset(0f, -h), cumbreB)
        drawPath(hastial, if (costadoAlSol) p.luz(muro, lejos) else p.sombra(muro, lejos))
        drawPath(poligono(cumbreB, c + Offset(w * 0.05f, -h), c + Offset(w * 0.05f, -h + 0.6f), cumbreB + Offset(0f, 0.6f)), p.sombra(if (nevado) C.NIEVE else techo, lejos))
        if (estilo == Estilo.MADERA && !nevado) drawLine(p.sombra(techo, lejos), cumbreA, cumbreB, 0.6f)
        // La chimenea, con su humo (lo pinta la parte viva).
        if (estilo == Estilo.LADRILLO || estilo == Estilo.PIEDRA || humea) drawRect(p.medio(LADRILLO, lejos), cumbreA + Offset(w * 0.68f, -alto * 0.15f), Size((w * 0.08f).coerceAtLeast(0.6f), alto * 0.45f))
    }

    /**
     * Un pueblo lejano en dos o tres hileras, cada una algo más grande, más nítida y más abajo que la de atrás, con su
     * iglesia en medio, árboles por delante y por detrás. [x0]–[x1]: de dónde a dónde; [y]: la línea de la hilera de
     * delante; [tam]: el ancho de una casa de delante.
     */
    private fun DrawScope.pueblo(v: VistaVivac, x0: Float, x1: Float, y: Float, tam: Float, estilo: Estilo, iglesia: Iglesia?, lejos: Float, semilla: Int, quemado: Boolean = false, arboles: Boolean = true) {
        val az = Azar(Semilla.de("napoleon", "vivac", "pueblo", semilla))
        // Los árboles de detrás.
        if (arboles) manchaDeBosque(v, (x0 + x1) / 2f + az.entre(-10f, 10f), y - tam * 0.5f, (x1 - x0) * 0.9f, tam * 0.9f, lejos + 0.05f, estilo == Estilo.MADERA, az)
        for (fila in 2 downTo 0) {
            val yf = y - fila * tam * 0.32f; val t = tam * (1f - 0.22f * fila); var x = x0 + az.entre(0f, t * 0.5f) + fila * t * 0.3f
            val lj = lejos + 0.05f * fila
            if (fila == 1 && iglesia != null) iglesia(v, Offset((x0 + x1) / 2f + az.entre(-t, t), yf), t * 1.1f, iglesia, lj, quemado)
            while (x < x1 - t * 0.5f) {
                val ww = t * az.entre(0.7f, 1.1f); val hh = t * az.entre(0.45f, 0.62f) * (if (estilo == Estilo.MADERA) 0.75f else 1f)
                casa(v, Offset(x, yf + az.entre(-0.6f, 0.6f)), ww, hh, estilo, lj, az, quemado && az.real() < 0.8f)
                x += ww * az.entre(1.25f, 1.9f)
            }
        }
        // Los árboles de delante, sueltos.
        if (arboles) repeat(3) { val cx = az.entre(x0, x1); manchaDeBosque(v, cx, y + tam * 0.15f, tam * 1.2f, tam * 0.7f, lejos - 0.05f, estilo == Estilo.MADERA, az) }
    }

    /** Las iglesias de cada tierra. */
    internal enum class Iglesia { CAMPANARIO, AGUJA, CUPULAS, ESPADANA, CATEDRAL }

    /** Una iglesia lejana: la nave con su tejado y la torre, con el remate de su tierra. */
    private fun DrawScope.iglesia(v: VistaVivac, pie: Offset, tam: Float, tipo: Iglesia, lejos: Float, quemada: Boolean = false) {
        val p = v.p; val lado = if (p.solALaDerecha) 1f else -1f
        val muro = when (tipo) { Iglesia.CUPULAS -> CAL; Iglesia.AGUJA, Iglesia.CATEDRAL -> lerp(PIEDRA, LADRILLO, if (tipo == Iglesia.CATEDRAL) 0.7f else 0.2f); else -> PIEDRA }
        val nave = Size(tam * 1.4f, tam * 0.6f)
        sombraCorta(v.r, pie + Offset(nave.width / 2f, 0f), nave.width * 1.2f, tam)
        drawRect(p.frente(muro, lejos), pie + Offset(0f, -nave.height), nave)
        drawPath(poligono(pie + Offset(-tam * 0.05f, -nave.height), pie + Offset(nave.width + tam * 0.05f, -nave.height), pie + Offset(nave.width * 0.85f, -nave.height - tam * 0.35f), pie + Offset(nave.width * 0.15f, -nave.height - tam * 0.35f)), p.luz(if (v.nieve > 0.35f) C.NIEVE else if (tipo == Iglesia.CUPULAS) lerp(C.PINO_LUZ, C.PLATA, 0.3f) else TEJA, lejos))
        val tx = pie.x + (if (tipo == Iglesia.CATEDRAL) nave.width * 0.1f else -tam * 0.1f); val tw = tam * 0.38f
        val th = when (tipo) { Iglesia.CAMPANARIO -> tam * 1.4f; Iglesia.AGUJA -> tam * 1.2f; Iglesia.CUPULAS -> tam * 1.1f; Iglesia.ESPADANA -> tam * 0.95f; Iglesia.CATEDRAL -> tam * 1.8f }
        val torres = if (tipo == Iglesia.CATEDRAL) listOf(tx, tx + nave.width * 0.65f) else listOf(tx)
        for (x in torres) {
            if (quemada) { drawRect(p.medio(lerp(C.CENIZA, muro, 0.3f), lejos), Offset(x, pie.y - th * 0.8f), Size(tw, th * 0.8f)); continue }
            drawRect(p.frente(muro, lejos), Offset(x, pie.y - th), Size(tw, th))
            drawRect(if (lado > 0f) p.luz(muro, lejos) else p.sombra(muro, lejos), Offset(x + tw, pie.y - th + tw * 0.1f), Size(tw * 0.4f, th - tw * 0.1f))
            drawRect(p.sombra(C.CENIZA, lejos), Offset(x + tw * 0.32f, pie.y - th * 0.85f), Size(tw * 0.36f, th * 0.12f))
            val cima = Offset(x + tw * 0.5f, pie.y - th)
            when (tipo) {
                Iglesia.CAMPANARIO -> drawPath(poligono(cima + Offset(-tw * 0.62f, 0f), cima + Offset(0f, -tw * 0.5f), cima + Offset(tw * 0.62f + tw * 0.4f, 0f)), p.luz(TEJA, lejos))
                Iglesia.AGUJA, Iglesia.CATEDRAL -> { drawPath(poligono(cima + Offset(-tw * 0.55f, 0f), cima + Offset(0f, -tw * 2.6f), cima + Offset(tw * 0.55f, 0f)), p.medio(PIZARRA, lejos)); drawPath(poligono(cima + Offset(0f, -tw * 2.6f), cima + Offset(tw * 0.55f * lado, 0f), cima), p.lado(PIZARRA, lejos).copy(alpha = 0.6f)) }
                Iglesia.CUPULAS -> cupula(v, cima, tw * 0.75f, lerp(C.PINO_LUZ, C.PLATA, 0.25f), lejos)
                Iglesia.ESPADANA -> { drawRect(p.frente(muro, lejos), Offset(x - tw * 0.1f, cima.y - tw * 0.6f), Size(tw * 1.2f, tw * 0.6f)); drawRect(p.sombra(C.CENIZA, lejos), Offset(x + tw * 0.3f, cima.y - tw * 0.48f), Size(tw * 0.4f, tw * 0.32f)) }
            }
        }
    }

    /** Una cúpula de bulbo con su linterna y su cruz: [c] es el centro de su base, [r] el radio. */
    private fun DrawScope.cupula(v: VistaVivac, c: Offset, r: Float, color: Color, lejos: Float, brillo: Float = 0.5f) {
        val p = v.p; val lado = if (p.solALaDerecha) 1f else -1f
        val forma = Path().apply {
            moveTo(c.x - r * 0.75f, c.y)
            cubicTo(c.x - r * 1.25f, c.y - r * 0.7f, c.x - r * 0.9f, c.y - r * 1.35f, c.x, c.y - r * 2.1f)
            cubicTo(c.x + r * 0.9f, c.y - r * 1.35f, c.x + r * 1.25f, c.y - r * 0.7f, c.x + r * 0.75f, c.y)
            close()
        }
        drawPath(forma, p.medio(color, lejos))
        clipPath(forma) {
            drawRect(p.sombra(color, lejos), Offset(c.x - (if (lado > 0f) r * 1.3f else -r * 0.1f), c.y - r * 2.2f), Size(r * 1.2f, r * 2.3f))
            mancha(c + Offset(lado * r * 0.35f, -r * 1.1f), r * 0.35f, r * 0.55f, lerp(p.luz(color, lejos), Color.White, 0.3f), brillo * v.r.dia + 0.15f)
        }
        drawLine(p.medio(C.ORO, lejos), c + Offset(0f, -r * 2.1f), c + Offset(0f, -r * 2.9f), (r * 0.12f).coerceAtLeast(0.4f))
        drawLine(p.medio(C.ORO, lejos), c + Offset(-r * 0.22f, -r * 2.6f), c + Offset(r * 0.22f, -r * 2.6f), (r * 0.1f).coerceAtLeast(0.35f))
    }

    // ---------------------------------------------------------------- Los tramos

    private fun DrawScope.francia(v: VistaVivac, borgona: Boolean) {
        val t = tierra(v)
        loma(v, cresta(22f, "fr-a", 3.4f), lerp(t, v.l.bruma, 0.2f), 0.48f, 6, false)
        val medio = cresta(14f, "fr-b", 4.2f, 110f, 400f)
        loma(v, medio, t, 0.34f, 4, false)
        if (borgona) {
            // La ladera de viñas en filas, que baja del pueblo: renglones oscuros que siguen la loma.
            val vinas = curva(cresta(24f, "vinas", 2.2f, 150f, 400f), H + 2f)
            drawPath(vinas, v.p.medio(lerp(t, VERDE_OSC, 0.45f), 0.3f))
            clipPath(vinas) { for (j in 0 until 22) { val y = H - 24f + j * 1.3f; drawLine(v.p.sombra(VERDE_OSC, 0.3f).copy(alpha = 0.65f), Offset(150f, y + 5f), Offset(400f, y - 4f), 0.6f) } }
            pueblo(v, 222f, 332f, H - 13f, 10f, Estilo.PIEDRA, Iglesia.CAMPANARIO, 0.24f, 1)
            drawPath(curva(listOf(Offset(110f, H + 0.6f), Offset(220f, H - 0.3f), Offset(300f, H + 0.9f), Offset(390f, H + 0.3f)), H + 2f), v.p.medio(lerp(v.l.bajo, C.AZUL, 0.25f), 0.3f))
            alamos(v, Offset(392f, H + 1f), Offset(150f, H + 0.5f), 18, 22f, 0.3f, 1)
        } else {
            pueblo(v, 240f, 336f, TrazosValle.alturaEn(medio, 288f) + 3f, 9f, Estilo.PIEDRA, Iglesia.CAMPANARIO, 0.3f, 2)
            alamos(v, Offset(392f, H + 1.5f), Offset(150f, H + 0.3f), 22, 26f, 0.3f, 2)
        }
    }

    private fun DrawScope.cienDias(v: VistaVivac) {
        val t = tierra(v)
        loma(v, cresta(16f, "cd-a", 3f), lerp(t, v.l.bruma, 0.15f), 0.44f, 4, false)
        val cr = cresta(12f, "cd-b", 4.5f, 110f, 400f)
        loma(v, cr, lerp(t, OCRE, 0.35f), 0.3f, 2, false)
        // La granja tapiada en lo alto de la loma: el muro largo, las casas de piedra y el huerto.
        val y = TrazosValle.alturaEn(cr, 280f) + 3f
        drawRect(v.p.frente(PIEDRA, 0.28f), Offset(226f, y - 5f), Size(96f, 5f))
        drawRect(v.p.sombra(PIEDRA, 0.28f).copy(alpha = 0.5f), Offset(226f, y - 1.5f), Size(96f, 1.5f))
        pueblo(v, 232f, 318f, y - 2f, 9f, Estilo.PIEDRA, null, 0.27f, 3, arboles = true)
        alamos(v, Offset(150f, H + 0.5f), Offset(220f, H - 0.5f), 6, 20f, 0.3f, 3)
        if (VivacNapoleon.batalla(v.m)) humoDeBatalla(v, 140f, 380f, 0.9f)
    }

    private fun DrawScope.alemania(v: VistaVivac, rin: Boolean) {
        val t = tierra(v)
        loma(v, cresta(40f, "al-a", 2.6f, 100f, 420f), lerp(lerp(t, C.PINO, 0.35f), v.l.bruma, 0.25f), 0.5f, 16, false)
        val medio = cresta(24f, "al-b", 4f, 120f, 420f)
        loma(v, medio, lerp(t, VERDE_OSC, 0.25f), 0.34f, 12, false)
        if (rin) {
            // El Rin, ancho, y del otro lado Maguncia con su catedral de torres rojas.
            pueblo(v, 190f, 360f, H - 7f, 9f, Estilo.ENTRAMADO, Iglesia.CATEDRAL, 0.28f, 4, arboles = false)
            val rio = poligono(listOf(Offset(110f, H - 3.5f), Offset(ANCHO + 10f, H - 4.5f), Offset(ANCHO + 10f, H + 2.5f), Offset(110f, H + 3.5f)))
            drawPath(rio, Brush.verticalGradient(listOf(lerp(v.l.bajo, C.AZUL, 0.2f), lerp(v.l.medio, C.AZUL, 0.35f)), H - 4.5f, H + 3.5f))
            drawLine(v.l.bajo.copy(alpha = 0.6f), Offset(110f, H - 2.6f), Offset(ANCHO + 10f, H - 3.6f), 0.6f)
            for (i in 0 until 8) drawLine(lerp(v.l.bajo, Color.White, 0.3f).copy(alpha = 0.4f), Offset(130f + i * 31f, H - 1f + (i % 3)), Offset(142f + i * 31f, H - 1f + (i % 3)), 0.5f)
        } else {
            pueblo(v, 214f, 330f, TrazosValle.alturaEn(medio, 270f) + 6f, 9.5f, Estilo.ENTRAMADO, Iglesia.AGUJA, 0.3f, 5)
            frutales(v, 300f, 392f, H + 1f, 16f, 0.3f, 5)
            if (VivacNapoleon.batalla(v.m)) humoDeBatalla(v, 120f, 380f, 0.9f)
        }
    }

    private fun DrawScope.polonia(v: VistaVivac) {
        val t = tierra(v)
        loma(v, cresta(8f, "po-a", 3f), lerp(t, v.l.bruma, 0.2f), 0.46f, 0, true)
        // «Bosques de pinos»: una pared de pinos a la derecha, alta y oscura, y otra más baja al fondo.
        bandaDeBosque(v, 120f, 390f, H + 1.2f, 9f, 0.42f, 1)
        pinar(v, 262f, 392f, H + 2f, 30f, 0.26f, 2)
        pueblo(v, 150f, 236f, H + 1f, 8.5f, Estilo.MADERA, null, 0.3f, 6, arboles = false)
    }

    private fun DrawScope.niemen(v: VistaVivac) {
        val helado = v.nieve > 0.4f || v.m.ficha.dia.substring(5) >= "12-01"
        loma(v, cresta(8f, "ni-a", 3f), lerp(tierra(v), v.l.bruma, 0.2f), 0.46f, 0, true)
        if (helado) {
            // El 14 de diciembre: del otro lado, «un pueblo con casas de verdad, con humo en las chimeneas».
            pinar(v, 300f, 392f, H + 1f, 18f, 0.32f, 3)
            pueblo(v, 160f, 296f, H, 9f, Estilo.LADRILLO, Iglesia.AGUJA, 0.3f, 7)
        } else {
            // El 24 de junio: «Solo bosque».
            bandaDeBosque(v, 110f, 390f, H + 1f, 11f, 0.42f, 4)
            pinar(v, 130f, 392f, H + 1.5f, 22f, 0.3f, 4)
        }
    }

    private fun DrawScope.rusia(v: VistaVivac) {
        val t = tierra(v)
        loma(v, cresta(9f, "ru-a", 2.6f), lerp(t, v.l.bruma, 0.15f), 0.42f, 5, true)
        bandaDeBosque(v, 110f, 390f, H + 1.2f, 8f, 0.42f, 7)
        pinar(v, 300f, 392f, H + 1.5f, 20f, 0.3f, 5)
        pueblo(v, 160f, 262f, H + 0.5f, 9f, Estilo.MADERA, null, 0.28f, 8, arboles = false)
        pinar(v, 120f, 168f, H + 1f, 14f, 0.32f, 6)
    }

    private fun DrawScope.smolensk(v: VistaVivac) {
        val t = tierra(v)
        loma(v, cresta(9f, "sm-a", 3f), lerp(t, v.l.bruma, 0.15f), 0.44f, 4, true)
        // La ciudad sobre su loma: «murallas viejas sobre un río», con sus torres; dentro, las casas de madera que arden.
        val loma = cresta(26f, "sm-b", 2f, 190f, 400f)
        loma(v, loma, lerp(t, OCRE, 0.2f), 0.32f)
        val quemado = v.m.hora >= 12.0 || EscenaNapoleon.diaNumero(v.m, "smolensk") == 2
        pueblo(v, 214f, 372f, H - 19f, 8f, Estilo.MADERA, Iglesia.CUPULAS, 0.28f, 9, quemado = quemado, arboles = false)
        muralla(v, 196f, 384f, H - 8f, 8f, 0.28f)
        // El río al pie de la loma.
        drawPath(curva(listOf(Offset(150f, H + 0.6f), Offset(250f, H - 0.4f), Offset(390f, H + 0.6f)), H + 2.5f), v.p.medio(lerp(v.l.bajo, C.AZUL, 0.25f), 0.3f))
    }

    private fun DrawScope.borodino(v: VistaVivac) {
        val t = tierra(v)
        loma(v, cresta(12f, "bo-a", 3f), lerp(t, v.l.bruma, 0.15f), 0.44f, 5, true)
        val lomas = cresta(18f, "bo-b", 4f, 100f, 420f)
        loma(v, lomas, t, 0.32f, 3, true)
        // Los reductos: montones de tierra con sus troneras, en lo alto de las lomas.
        for ((i, x) in listOf(170f, 252f, 336f).withIndex()) {
            val y = TrazosValle.alturaEn(lomas, x) + 2f; val ww = 30f - i * 3f; val hh = 6.5f
            val red = poligono(Offset(x - ww * 0.6f, y), Offset(x - ww * 0.42f, y - hh), Offset(x + ww * 0.4f, y - hh), Offset(x + ww * 0.6f, y))
            drawPath(red, v.p.medio(lerp(C.BARRO, t, 0.3f), 0.3f))
            val sg = if (v.p.solALaDerecha) 1f else -1f
            drawPath(poligono(Offset(x + ww * 0.4f * sg, y - hh), Offset(x + ww * 0.6f * sg, y), Offset(x, y), Offset(x, y - hh)), v.p.lado(C.BARRO, 0.3f).copy(alpha = 0.6f))
            for (j in 0 until 4) drawRect(v.p.sombra(C.CENIZA, 0.3f), Offset(x - ww * 0.35f + j * ww * 0.22f, y - hh + 0.2f), Size(2f, 2.2f))
        }
        if (VivacNapoleon.batalla(v.m)) humoDeBatalla(v, 130f, 390f, 1f)
    }

    private fun DrawScope.moscu(v: VistaVivac) {
        val estado = EscenaNapoleon.moscu(v.m) ?: Moscu.ENTERA
        val t = tierra(v)
        loma(v, cresta(8f, "mo-a", 2.6f), lerp(t, v.l.bruma, 0.2f), 0.46f, 4, true)
        val quemada = estado == Moscu.QUEMADA; val arde = estado == Moscu.ARDE
        val az = Azar(Semilla.de("napoleon", "vivac", "moscu"))
        // La ciudad enorme: muchas hileras de casas y palacios que se pierden; quemada, chimeneas negras.
        for (fila in 3 downTo 0) {
            var x = 120f + fila * 6f; val y = H - 2f - fila * 4.2f; val tam = 8f - fila * 1.2f
            while (x < ANCHO + 6f) {
                val ww = tam * az.entre(0.7f, 1.5f); val hh = tam * az.entre(0.4f, 0.75f)
                casa(v, Offset(x, y), ww, hh, if (az.real() < 0.6f) Estilo.MADERA else Estilo.PIEDRA, 0.34f + 0.04f * fila, az, quemada || (arde && az.real() < 0.5f))
                x += ww * az.entre(1.05f, 1.5f)
            }
        }
        // El Kremlin: la muralla roja con sus torres, y las cúpulas doradas y de colores encima.
        muralla(v, 214f, 362f, H - 5f, 8f, 0.3f, lerp(C.ROJO, LADRILLO, 0.4f))
        val colores = listOf(C.ORO, lerp(C.PINO_LUZ, C.PLATA, 0.2f), C.ORO, C.AZUL, C.ORO, lerp(C.ROJO, C.LATON, 0.3f), C.ORO)
        for (i in 0 until 18) {
            val cx = az.entre(136f, 376f); val h = az.entre(14f, 34f) * (if (cx in 230f..350f) 1.25f else 0.85f); val r = az.entre(2.6f, 5f)
            val muro = lerp(CAL, C.ROJO, if (i % 4 == 0) 0.3f else 0.05f)
            if (quemada && az.real() < 0.5f) { drawRect(v.p.medio(lerp(C.CENIZA, muro, 0.2f), 0.3f), Offset(cx - r * 0.7f, H - h * 0.7f), Size(r * 1.4f, h * 0.7f)); continue }
            drawRect(v.p.frente(if (quemada) lerp(muro, C.CENIZA, 0.6f) else muro, 0.3f), Offset(cx - r * 0.7f, H - h), Size(r * 1.4f, h))
            drawRect(v.p.lado(if (quemada) lerp(muro, C.CENIZA, 0.6f) else muro, 0.3f), Offset(cx + (if (v.p.solALaDerecha) r * 0.2f else -r * 0.7f), H - h), Size(r * 0.5f, h))
            cupula(v, Offset(cx, H - h), r, colores[i % colores.size], 0.26f, if (quemada) 0.9f else 0.6f)
        }
        if (arde) drawOval(Brush.radialGradient(listOf(Color(0xFFFF7A30).copy(alpha = 0.55f * v.incendio), Color.Transparent), Offset(270f, H), 150f), Offset(110f, H - 40f), Size(320f, 46f), blendMode = BlendMode.Plus)
    }

    private fun DrawScope.prusia(v: VistaVivac, hospital: Boolean) {
        val t = tierra(v)
        loma(v, cresta(14f, "pr-a", 3f), lerp(t, v.l.bruma, 0.15f), 0.44f, 6, true)
        loma(v, cresta(8f, "pr-b", 4f, 110f, 400f), t, 0.32f, 3, true)
        if (hospital) {
            // La ciudad con la iglesia grande donde están los catres (el hospital): las ventanas encendidas de noche.
            pueblo(v, 160f, 370f, H - 2f, 10f, Estilo.LADRILLO, Iglesia.CATEDRAL, 0.26f, 10)
        } else {
            pueblo(v, 190f, 340f, H - 1f, 9.5f, Estilo.LADRILLO, Iglesia.AGUJA, 0.28f, 11)
        }
        alamos(v, Offset(340f, H + 1f), Offset(392f, H + 0.5f), 5, 18f, 0.3f, 4)
    }

    private fun DrawScope.cuartel(v: VistaVivac) {
        val t = tierra(v)
        loma(v, cresta(16f, "cu-a", 3f), lerp(t, v.l.bruma, 0.15f), 0.44f, 5, false)
        // La ciudad del cuartel, con su muralla, y el convento viejo con su espadaña y su claustro.
        pueblo(v, 170f, 370f, H - 6f, 9.5f, Estilo.PIEDRA, Iglesia.CAMPANARIO, 0.27f, 12, arboles = false)
        iglesia(v, Offset(232f, H - 3f), 13f, Iglesia.ESPADANA, 0.24f)
        muralla(v, 150f, 384f, H, 6f, 0.26f, PIEDRA)
    }

    /**
     * Un pinar lejano: pinos albares de tronco desnudo y copa en masas tendidas, de [x0] a [x1], apoyados en [y], el más
     * alto [alto]. Cada copa con su lado de luz y su nieve si la hay.
     */
    private fun DrawScope.pinar(v: VistaVivac, x0: Float, x1: Float, y: Float, alto: Float, lejos: Float, semilla: Int) {
        val az = Azar(Semilla.de("napoleon", "vivac", "pinar", semilla)); val p = v.p; val lado = if (p.solALaDerecha) 1f else -1f
        // La masa del pinar: las copas se tocan y hacen un techo desigual; debajo, la penumbra y los troncos.
        val copas = ArrayList<Triple<Offset, Float, Float>>()
        var x = x0
        while (x < x1) { val h = alto * az.entre(0.6f, 1f); copas += Triple(Offset(x, y + az.entre(-1f, 1.2f)), h, az.entre(0.24f, 0.36f) * h); x += az.entre(2.4f, 4.6f) * (alto / 22f).coerceAtLeast(0.6f) }
        val sombraBajo = p.medio(lerp(C.PINO_OSC, C.CENIZA, 0.3f), lejos)
        // La penumbra del sotobosque, entre el suelo y las copas.
        val bajo = Path().apply { moveTo(x0 - 2f, y + 1.5f); for ((q, h, _) in copas) lineTo(q.x, q.y - h * 0.52f); lineTo(x1 + 2f, y + 1.5f); close() }
        drawPath(bajo, sombraBajo.copy(alpha = 0.55f))
        for ((q, h, _) in copas) drawLine(p.medio(lerp(C.MADERA, C.ROJO, 0.2f), lejos), q, q + Offset(0f, -h * 0.8f), (h * 0.022f).coerceAtLeast(0.4f))
        for ((q, h, w) in copas) {
            val c = q + Offset(0f, -h * 0.78f)
            val masa = TrazosValle.borron(c, w, h * 0.16f, az, 11, 0.32f, 0.75f)
            drawPath(masa, p.medio(C.PINO_OSC, lejos))
            val luz = TrazosValle.borron(c + Offset(lado * w * 0.25f, -h * 0.05f), w * 0.55f, h * 0.09f, az, 9, 0.35f, 0.7f)
            drawPath(luz, p.lado(C.PINO, lejos).copy(alpha = 0.7f))
            if (v.nieve > 0.4f) drawPath(TrazosValle.borron(c + Offset(0f, -h * 0.08f), w * 0.6f, h * 0.05f, az, 8, 0.3f, 0.6f), p.arriba(C.NIEVE, lejos))
        }
    }

    /** Unos frutales y árboles de hoja lejanos en hilera (los de los caminos de Alemania), de copa redonda. */
    private fun DrawScope.frutales(v: VistaVivac, x0: Float, x1: Float, y: Float, alto: Float, lejos: Float, semilla: Int) {
        val az = Azar(Semilla.de("napoleon", "vivac", "frutales", semilla)); val p = v.p; val lado = if (p.solALaDerecha) 1f else -1f
        val desnudo = v.m.ficha.estacion == Estacion.INVIERNO || v.nieve > 0.4f
        val hoja = when (v.m.ficha.estacion) { Estacion.OTONO -> lerp(C.HOJA, C.LATON, 0.3f); else -> lerp(C.PINO, C.HOJA, 0.42f) }
        var x = x0
        while (x < x1) {
            val h = alto * az.entre(0.7f, 1f); val q = Offset(x, y + az.entre(-0.5f, 1f))
            sombraCorta(v.r, q, h * 0.5f, h * 0.5f)
            drawLine(p.medio(TRONCO, lejos), q, q + Offset(0f, -h * 0.5f), (h * 0.05f).coerceAtLeast(0.5f))
            if (desnudo) for (j in 0 until 4) { val a0 = q + Offset(0f, -h * 0.45f); drawLine(p.medio(TRONCO, lejos), a0, a0 + Offset((j - 1.5f) * h * 0.15f, -h * 0.4f), 0.5f) }
            else for (j in 0 until 4) {
                val c = q + Offset(az.entre(-0.2f, 0.2f) * h, -h * az.entre(0.55f, 0.85f)); val r = h * az.entre(0.2f, 0.3f)
                drawCircle(p.medio(hoja, lejos), r, c)
                drawCircle(p.lado(lerp(hoja, C.LONA, 0.12f), lejos).copy(alpha = 0.7f), r * 0.6f, c + Offset(lado * r * 0.3f, -r * 0.25f))
            }
            x += az.entre(8f, 15f)
        }
    }

    // ---------------------------------------------------------------- Lo que se repite

    /** Una banda de bosque de pinos que se apoya en el horizonte: borde de arriba desigual, con claros y sus puntas al sol. */
    private fun DrawScope.bandaDeBosque(v: VistaVivac, x0: Float, x1: Float, y: Float, alto: Float, lejos: Float, semilla: Int) {
        val az = Azar(Semilla.de("napoleon", "vivac", "banda", semilla))
        val sil = Path().apply {
            moveTo(x0, y); var x = x0
            while (x < x1) { val h = alto * az.entre(0.6f, 1f) * (if (az.real() < 0.1f) 0.55f else 1f); val ww = az.entre(1.6f, 3f); lineTo(x + ww * 0.15f, y - h * 0.62f); lineTo(x + ww * 0.32f, y - h * 0.8f); lineTo(x + ww * 0.5f, y - h); lineTo(x + ww * 0.68f, y - h * 0.78f); lineTo(x + ww * 0.85f, y - h * 0.6f); x += ww * az.entre(0.75f, 1f) }
            lineTo(x1, y); close()
        }
        drawPath(sil, v.p.medio(C.PINO_OSC, lejos))
        clipPath(sil) {
            // La luz de un lado en cada copa, la bruma al pie y la nieve encima si la hay.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, v.l.bruma.copy(alpha = 0.22f)), y - alto * 0.5f, y), Offset(x0, y - alto - 2f), Size(x1 - x0, alto + 2f))
            repeat(((x1 - x0) / 3f).toInt()) { val x = az.entre(x0, x1); mancha(Offset(x + (if (v.p.solALaDerecha) 0.6f else -0.6f), y - alto * az.entre(0.4f, 0.9f)), 0.8f, alto * 0.25f, v.p.luz(C.PINO, lejos), 0.45f * v.r.dia) }
            if (v.nieve > 0.4f) repeat(((x1 - x0) / 2f).toInt()) { val x = az.entre(x0, x1); mancha(Offset(x, y - alto * az.entre(0.3f, 0.9f)), 1.2f, 0.4f, v.p.arriba(C.NIEVE, lejos), 0.7f) }
        }
    }

    /** Una muralla vieja con sus torres, de [x0] a [x1], apoyada en [y]. */
    private fun DrawScope.muralla(v: VistaVivac, x0: Float, x1: Float, y: Float, alto: Float, lejos: Float, color: Color = lerp(C.ROJO, LADRILLO, 0.6f)) {
        val p = v.p; val lado = if (p.solALaDerecha) 1f else -1f
        drawRect(p.frente(color, lejos), Offset(x0, y - alto), Size(x1 - x0, alto))
        drawRect(p.sombra(color, lejos).copy(alpha = 0.5f), Offset(x0, y - alto * 0.25f), Size(x1 - x0, alto * 0.25f))
        var x = x0 + 2f; val az = Azar(Semilla.de("napoleon", "vivac", "muralla", x0.toInt()))
        while (x < x1) { drawRect(p.frente(color, lejos), Offset(x, y - alto - 1.4f), Size(1.6f, 1.4f)); x += 3.2f }
        x = x0 + az.entre(4f, 12f)
        while (x < x1 - 4f) {
            val tw = alto * 0.9f; val th = alto * az.entre(1.6f, 2.3f)
            drawRect(p.frente(color, lejos), Offset(x, y - th), Size(tw, th))
            drawRect(if (lado > 0f) p.luz(color, lejos) else p.sombra(color, lejos), Offset(x + tw, y - th + 0.5f), Size(tw * 0.35f, th - 0.5f))
            drawPath(poligono(Offset(x - 0.6f, y - th), Offset(x + tw * 0.5f, y - th - tw * 1.1f), Offset(x + tw + 0.6f + tw * 0.35f, y - th)), p.medio(lerp(PIZARRA, C.PINO_LUZ, 0.3f), lejos))
            x += az.entre(18f, 34f)
        }
    }

    /** El humo de la batalla: bancos blancos y pesados a ras de las lomas, con fogonazos (de día). */
    private fun DrawScope.humoDeBatalla(v: VistaVivac, x0: Float, x1: Float, k: Float) {
        val az = Azar(Semilla.de("napoleon", "vivac", "batalla", v.m.hora.toInt()))
        val blanco = lerp(v.l.tono(C.NIEVE), v.l.bruma, 0.25f)
        repeat(16) {
            val q = Offset(az.entre(x0, x1), H - az.entre(2f, 14f))
            mancha(q, az.entre(10f, 26f), az.entre(4f, 8f), blanco, 0.8f * k)
            mancha(q + Offset(5f, 3f), az.entre(8f, 18f), az.entre(3f, 6f), v.l.tono(lerp(C.NIEVE, C.CAPOTE, 0.4f)), 0.5f * k)
            if (az.real() < 0.3f) drawCircle(Color(0xFFFFD27A).copy(alpha = 0.8f * k), 1.2f, q + Offset(0f, 3f))
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: VistaVivac, reloj: Float) {
        val l = v.l
        // La ciudad que arde: llamas sobre los tejados y columnas de humo.
        if (v.incendio > 0.01f) {
            val az = Azar(Semilla.de("napoleon", "vivac", "llamas", "ciudad"))
            for (i in 0 until 9) {
                val q = Offset(az.entre(190f, 376f), H - az.entre(1f, 8f))
                llama(q, az.entre(2.5f, 6f) * v.incendio, reloj, i + 120)
                if (i % 2 == 0) humo(l, q + Offset(0f, -4f), 80f, 14f, reloj, v.m.viento + 0.3f, 0.75f, 0.3f, i + 120, 8, 0.75f * v.incendio)
            }
            resplandor(ARDE, 90f, (0.25f + 0.6f * v.noche) * v.incendio)
        } else if (EscenaNapoleon.moscu(v.m) == Moscu.QUEMADA) {
            for (i in 0 until 4) humo(l, Offset(196f + i * 46f, H - 4f), 46f, 6f, reloj, v.m.viento + 0.2f, 0.55f, 0.5f, i + 130, 5, 0.45f)
        }
        // El humo de las chimeneas de los pueblos de Prusia y del hospital, en invierno.
        if (v.fondo == Fondo.PRUSIA || v.fondo == Fondo.HOSPITAL || (v.fondo == Fondo.NIEMEN && v.nieve > 0.4f))
            for (i in 0 until 4) humo(l, Offset(206f + i * 28f, H - 9f), 22f, 2.5f, reloj, v.m.viento + 0.15f, 0.25f, 0.6f, i + 140, 4, 0.4f)
        if (VivacNapoleon.batalla(v.m)) for (i in 0 until 3) humo(l, Offset(120f + i * 90f, H - 8f), 26f, 7f, reloj, 0.5f, 0.1f, 0.6f, i + 150, 5, 0.5f)
        if (v.fondo == Fondo.SMOLENSK) for (i in 0 until 3) humo(l, Offset(270f + i * 34f, H - 14f), 70f, 12f, reloj, v.m.viento + 0.3f, 0.7f, 0.35f, i + 160, 7, 0.7f)
    }

}
