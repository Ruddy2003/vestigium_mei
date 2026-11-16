package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Costa
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Mar
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.barcoLejano
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.bote
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.Postura
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.pelicano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nube
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.sin

/**
 * El mar del barco: el cielo, el horizonte del banco (la costa algunos días), los otros barcos esponjeros fondeados «cada
 * uno en su sitio», los pelícanos posados en el agua y, sobre todo, **el agua transparente** ([AguaClara]) con su fondo,
 * la sombra del barco y del bote en la arena, y el bote amarrado al costado con su cubo de cristal.
 */
internal object MarAbismo {
    private val C = ColorAbismo
    private val B = BarcoAbismo

    /** El agua del barco: la superficie a los ojos más el franco, el fondo seis metros más abajo (aparentes); el banco hondo, más. */
    fun agua(v: BarcoAbismo.Vista): AguaClara {
        val hondo = if (EscenaAbismo.hoyEs(v.m, "hondo") || EscenaAbismo.hoyEs(v.m, "hondo_otra_vez")) 8f else 4.5f
        return AguaClara(v.l, EscenaAbismo.rayo(v.l), B.H, B.F, B.CX, B.OJO + B.FRANCO, hondo, EscenaAbismo.fondo(v.m), vientoDelMar(v.mar))
    }

    private fun vientoDelMar(mar: Mar): Float = when (mar) { Mar.CALMA -> 0.02f; Mar.CLARA -> 0.22f; Mar.NORTE -> 0.85f; Mar.TORMENTA -> 1f; Mar.HURACAN -> 0.7f }

    /** El agua que se ve: a la derecha de la regala de estribor y, por encima de la proa, hasta el horizonte. */
    fun aguaVisible(): Path {
        val borda = (0..24).map { i -> val vv = 0.85f + (B.RODA - 0.85f) * i / 24f; B.pb(B.estribor(vv), B.regala(vv), vv) }
        val proa = B.pb(B.EJE, B.regala(B.RODA), B.RODA)
        return poligono(listOf(Offset(ANCHO + 10f, ALTO + 10f), borda.first().copy(y = ALTO + 10f)) + borda + listOf(proa, Offset(-10f, proa.y + 2f), Offset(-10f, B.H - 2f), Offset(ANCHO + 10f, B.H - 2f)))
    }

    /** El bote: amarrado al costado de estribor, a proa de quien mira, con su proa hacia la del barco. */
    val SITIO_BOTE: Sitio get() { val c = B.cam(4.3f, 7.4f); return Sitio(B.MIRADA, B.pc(c.x, -B.FRANCO, c.y), B.k(c.y), B.GIRO + 0.12f) }
    const val BOTE_LARGO = 3.3f
    const val BOTE_MANGA = 1.25f

    /** Dónde suben las burbujas del buzo (en metros del barco: u, v), y en el lienzo. */
    private const val BURBUJAS_U = 6.6f
    private const val BURBUJAS_V = 5.2f
    fun burbujas(): Offset { val c = B.cam(BURBUJAS_U, BURBUJAS_V); return B.pc(c.x, -B.FRANCO, c.y) }
    fun kBurbujas(): Float = B.k(B.cam(BURBUJAS_U, BURBUJAS_V).y)

    /** Los otros barcos esponjeros, fondeados en su sitio: (x, y del pie en el lienzo, largo, rumbo). Lejos, cada uno distinto. */
    private val OTROS = listOf(floatArrayOf(250f, 103.5f, 44f, -1f), floatArrayOf(352f, 99.5f, 24f, 1f), floatArrayOf(28f, 101f, 26f, 1f), floatArrayOf(182f, 98.6f, 15f, 1f))

    fun DrawScope.mar(v: BarcoAbismo.Vista) {
        val m = v.m; val l = v.l
        val grados = v.escora * 180f / PI.toFloat()
        rotate(grados, Offset(ANCHO / 2f, B.H)) {
            cielo(l, B.H, m.semilla("cielo"))
            astros(l, B.H, m.semilla("astros"))
            cometa(v)
            nubesDelGolfo(v)
            costa(v)
            val a = agua(v)
            val zona = Rect(-40f, B.H, ANCHO + 40f, ALTO + 30f)
            with(a) { pintar(zona, Semilla.de("abismo", "banco"), sombrasEnElFondo(v), m.semilla("agua")) }
            estela(l, Rect(0f, B.H, ANCHO, ALTO), Azar(m.semilla("estela")))
            otrosBarcos(v)
            pelicanos(v)
            boteAmarrado(v)
        }
    }

    /** Las manchas de sombra en el fondo, en metros (x, z) de la cámara: la del casco del barco y la del bote, tumbadas por el sol a través del agua. */
    private fun sombrasEnElFondo(v: BarcoAbismo.Vista): List<List<Offset>> {
        val r = EscenaAbismo.rayo(v.l); if (r.fuerza < 0.05f) return emptyList()
        val sol = Sol3(r); val hondo = agua(v).hondo
        val casco = (0..16).map { i -> val vv = B.POPA + (B.RODA - B.POPA) * i / 16f; B.cam(B.estribor(vv) + 0.1f, vv) } + (16 downTo 0).map { i -> val vv = B.POPA + (B.RODA - B.POPA) * i / 16f; B.cam(B.babor(vv) - 0.1f, vv) }
        // **[Aproximación]**: la sombra se corre por el agua con el sol, pero nunca más de unos metros (así se ve, y camina con la hora).
        val d = Offset((sol.sx * hondo).coerceIn(-2.5f, 3f) + 0.8f, (sol.sz * hondo).coerceIn(1.2f, 4.5f))
        val bote = SITIO_BOTE.let { s -> (0..10).map { i -> val a = i * 2f * PI.toFloat() / 10f; Offset(s.mx(BOTE_MANGA * 0.5f * kotlin.math.cos(a), BOTE_LARGO * 0.5f * sin(a)), s.mz(BOTE_MANGA * 0.5f * kotlin.math.cos(a), BOTE_LARGO * 0.5f * sin(a))) } }
        return listOf(casco.map { it + d }, bote.map { it + d })
    }

    /** El cielo del golfo: las nubes del tiempo de hoy y, en la estación de las lluvias, las torres de la tormenta de la tarde sobre el horizonte. */
    private fun DrawScope.nubesDelGolfo(v: BarcoAbismo.Vista) {
        val m = v.m; val l = v.l
        nubes(l, B.H, m.ficha.clima, m.semilla("nubes"))
        val lluvias = m.ficha.estacion == io.github.ruddy2003.vestigium.vidas.Estacion.LLUVIAS
        if (lluvias && (m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES)) {
            // Las torres de la tarde: crecen desde el mediodía, lejos, al oeste (a la izquierda).
            val crece = ((m.hora.toFloat() - 11f) / 6f).coerceIn(0.25f, 1f)
            val az = Azar(m.semilla("torres"))
            var x = az.entre(-30f, 40f)
            repeat(3) { val w = az.entre(56f, 96f); nube(l, x, B.H + 2f, w, az, carga = 0.25f * crece, lejos = 0.75f, alza = 1.6f + 1.4f * crece); x += w * az.entre(0.55f, 0.9f) }
        }
    }

    /** La cola del cometa Halley de noche, «como un velo blanco» que cruza las estrellas. */
    private fun DrawScope.cometa(v: BarcoAbismo.Vista) {
        val f = EscenaAbismo.cometa(v.m) * v.l.estrellas * (1f - v.l.nublado)
        if (f < 0.02f) return
        val cabeza = Offset(118f, 70f)
        for (i in 0..14) {
            val k = i / 14f
            mancha(cabeza + Offset(-70f * k, -40f * k - 6f * k * k), 3f + 22f * k, 1.6f + 5f * k, Color(0xFFEFF2FF), 0.26f * f * (1f - 0.75f * k))
        }
        drawCircle(Color(0xFFFFFFF0).copy(alpha = 0.9f * f), 1.3f, cabeza)
    }

    /** El horizonte: «donde no se ve tierra»; algunos días, la costa baja de Florida, «una raya de árboles», al este. */
    private fun DrawScope.costa(v: BarcoAbismo.Vista) {
        val l = v.l
        when (EscenaAbismo.costa(v.m)) {
            Costa.NINGUNA -> Unit
            Costa.RAYA -> {
                val az = Azar(Semilla.de("abismo", "costa"))
                val pts = ArrayList<Offset>(); var x = 236f
                pts += Offset(x, B.H)
                while (x < ANCHO + 6f) { pts += Offset(x, B.H - az.entre(1.2f, 3.6f)); x += az.entre(2f, 5f) }
                pts += Offset(ANCHO + 6f, B.H)
                drawPath(poligono(pts), l.tono(lerp(C.HIERBA, C.MAR_HONDO, 0.4f), 0.88f))
                drawRect(l.tono(C.ARENA, 0.8f), Offset(236f, B.H - 0.6f), Size(ANCHO - 236f, 0.8f))
            }
            Costa.ENSENADA -> {
                // La ensenada entre manglares: masas bajas y oscuras a los dos lados, cerca.
                val az = Azar(Semilla.de("abismo", "manglar"))
                for ((x0, x1) in listOf(-10f to 140f, 250f to 390f)) {
                    val pts = ArrayList<Offset>(); var x = x0; pts += Offset(x0, B.H + 3f)
                    while (x < x1) { pts += Offset(x, B.H - az.entre(5f, 14f)); x += az.entre(4f, 9f) }
                    pts += Offset(x1, B.H + 3f)
                    drawPath(poligono(pts), l.tono(lerp(C.HIERBA, C.BREA, 0.35f), 0.45f))
                }
            }
        }
    }

    /** Los otros barcos esponjeros: cada uno en su sitio, con sus ristras, su reflejo y, de noche, su farol lejos. */
    private fun DrawScope.otrosBarcos(v: BarcoAbismo.Vista) {
        val m = v.m
        val cuantos = 2 + (Azar(m.semilla("otros")).entero(3))
        val farol = if (EscenaAbismo.farolesLejos(m)) 0.8f * v.l.noche else 0f
        for (i in 0 until cuantos) {
            val o = OTROS[i]
            val pie = Offset(o[0], o[1]); val lejos = (1f - (o[1] - B.H) / 14f).coerceIn(0.35f, 0.85f)
            val p = v.p
            reflejoRoto(pie.x - o[2] * 0.55f, pie.x + o[2] * 0.55f, pie.y + 0.4f, o[2] * 0.22f, v.l.tono(C.BLANCO, lejos), 0.35f, Azar(m.semilla("refl", i)), 0.8f)
            barcoLejano(p, pie, o[2], o[3], Semilla.de("abismo", "otro", i), lejos, ristras = 0.3f + 0.6f * EscenaAbismo.esponjas(m), farol = farol,
                luto = EscenaAbismo.luto(m), empavesado = EscenaAbismo.fiesta(m))
        }
        // El barco de al lado, el día del buzo paralizado: más cerca, con su bomba parada.
        if (EscenaAbismo.paralisis(m)) barcoLejano(v.p, Offset(330f, 112f), 70f, -1f, Semilla.de("abismo", "vecino"), 0.25f, ristras = 0.5f)
    }

    /** Los pelícanos posados en el agua «a mirar, como viejos en una plaza». */
    private fun DrawScope.pelicanos(v: BarcoAbismo.Vista) {
        if (v.mar == Mar.TORMENTA || v.mar == Mar.HURACAN) return
        val az = Azar(v.m.semilla("pelicanos"))
        val n = az.entero(4)
        for (i in 0 until n) {
            val z = az.entre(14f, 26f); val x = az.entre(1.5f, 6.5f) * z / 6f
            val pie = B.pc(x, -B.FRANCO, z)
            if (pie.x < 250f || pie.x > ANCHO - 8f) continue
            pelicano(v.p, pie, B.k(z), if (az.real() > 0.5f) 1f else -1f, Postura.NADANDO, Semilla.de("abismo", "pelicano", i), lejos = 0.15f)
        }
    }

    /** El bote auxiliar, amarrado al costado, con su reflejo y su sombra. */
    private fun DrawScope.boteAmarrado(v: BarcoAbismo.Vista) {
        val s = SITIO_BOTE; val e = s.escala(0f, 0f)
        val flot = PiezasAbismo.boteFlotacion(s, BOTE_LARGO, BOTE_MANGA)
        val bb = flot.getBounds()
        // El reflejo del casco blanco y la sombra que el bote echa en el agua junto a él.
        reflejoRoto(bb.left, bb.right, bb.bottom - bb.height * 0.3f, e * 0.45f, v.l.tono(C.BLANCO), 0.45f, Azar(91), 0.9f)
        drawPath(flot, v.l.tono(C.MAR_HONDO).copy(alpha = 0.35f))
        bote(v.p, s, v.sol, BOTE_LARGO, BOTE_MANGA, Semilla.de("abismo", "bote"))
        // La boza: el cabo del bote a la regala.
        val proa = s.p(0f, 0.4f, BOTE_LARGO * 0.48f)
        val regala = B.pb(B.estribor(8.6f), B.regala(8.6f), 8.6f)
        drawLine(v.p.sombra(C.CANAMO), proa, regala, 0.6f)
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: BarcoAbismo.Vista, reloj: Float) {
        val m = v.m; val l = v.l
        val a = agua(v)
        val zona = aguaVisible()
        val sinIcono = Path().apply { addRect(iconoCaja()) }
        clipPath(zona) {
            clipPath(sinIcono, ClipOp.Difference) {
                destellos(l, Rect(0f, B.H + 2f, ANCHO, ALTO), reloj, m.semilla("destellos"), if (v.mar == Mar.CALMA) 16 else 30)
                with(a) {
                    val sombra = sombrasEnElFondo(v).firstOrNull()?.let { q -> val pts = q.filter { it.y > 0.5f }.map { enElFondo(it.x, it.y) }; if (pts.size >= 3) poligono(pts) else null }
                    redDeLuz(zona, Rect(0f, B.H, ANCHO, ALTO), reloj, sombra)
                    if (EscenaAbismo.buzoAbajo(m)) burbujas(burbujas(), kBurbujas(), reloj)
                    // El verde vivo de noche: donde el agua toca el bote, la manguera y la cadena del ancla.
                    val verde = if (v.mar == Mar.TORMENTA) 0.4f else 1f
                    if (l.noche > 0.45f) verdeVivo(listOf(SITIO_BOTE.p(0f, 0f, 0f), SITIO_BOTE.p(0f, 0f, BOTE_LARGO * 0.45f), B.pb(B.estribor(10.5f) + 0.8f, -B.FRANCO, 10.5f), burbujas()), Rect(0f, B.H, ANCHO, ALTO), reloj, verde * (l.noche - 0.45f) / 0.55f)
                }
                // Los faroles de los otros barcos, temblando en el agua.
                if (EscenaAbismo.farolesLejos(m)) {
                    val cuantos = 2 + (Azar(m.semilla("otros")).entero(3))
                    for (i in 0 until cuantos) { val o = OTROS[i]; reflejo(Offset(o[0] + o[3] * -0.18f * o[2], o[1]), 10f + o[2] * 0.25f, C.LUMBRE, 0.7f * l.noche, reloj, i) }
                }
            }
        }
        tiempoVivo(l, m.ficha.clima, reloj, B.H, m.semilla("tiempo"))
    }

    /** La caja de la hornacina del icono en el lienzo (para que lo del agua no la pise). */
    fun iconoCaja(): Rect { val c = B.ICONO; val k = B.K_ICONO; return Rect(c.x - 0.2f * k, c.y - 0.36f * k, c.x + 0.2f * k, c.y + 0.6f * k) }
}
