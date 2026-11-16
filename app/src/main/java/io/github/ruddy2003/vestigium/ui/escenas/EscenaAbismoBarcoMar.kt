package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Costa
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Mar
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.barcoLejano
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.bote
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.cabo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.manguera
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.Postura
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.pelicano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * El mar del barco: el cielo, el horizonte del banco (la costa algunos días), los otros barcos esponjeros fondeados «cada
 * uno en su sitio», los pelícanos posados en el agua y, sobre todo, **el agua transparente** ([AguaClara]) con su fondo,
 * la sombra del barco y del bote en la arena (que camina con la hora: [BarcoAbismo.corrimientoDeLaSombra]), los bancos de
 * peces, la cadena del ancla que baja y se pierde, la manguera y la cuerda de vida que bajan tensas hacia el buzo, el bote
 * amarrado al costado con su cubo de cristal, y de noche la calle de la luna.
 */
internal object MarAbismo {
    private val C = ColorAbismo
    private val B = BarcoAbismo

    /** El agua del barco: la superficie a los ojos más el franco, el fondo cuatro metros y medio más abajo (aparentes); el banco hondo, más. */
    fun agua(v: BarcoAbismo.Vista): AguaClara {
        val hondo = if (EscenaAbismo.hoyEs(v.m, "hondo") || EscenaAbismo.hoyEs(v.m, "hondo_otra_vez")) 8f else 4.5f
        return AguaClara(v.l, EscenaAbismo.rayo(v.l), B.H, B.F, B.CX, B.OJO + B.FRANCO, hondo, EscenaAbismo.fondo(v.m), vientoDelMar(v.mar))
    }

    private fun vientoDelMar(mar: Mar): Float = when (mar) { Mar.CALMA -> 0.02f; Mar.CLARA -> 0.22f; Mar.NORTE -> 0.85f; Mar.TORMENTA -> 1f; Mar.HURACAN -> 0.7f }

    /** La regala de estribor por fuera, en el lienzo, de popa a proa: el borde del agua que se ve. */
    private val REGALA_FUERA: List<Offset> by lazy { (0..32).map { i -> val vv = 0.85f + (B.RODA - 0.85f) * i / 32f; B.pb(B.estribor(vv) + 0.06f, B.regala(vv), vv) } }

    /** El borde izquierdo del agua que se ve en la altura [y] del lienzo: la regala; por encima de la proa, el borde del cuadro. */
    fun bordeDelAgua(y: Float): Float {
        val r = REGALA_FUERA
        if (y < r.last().y) return -10f
        for (i in 0 until r.size - 1) {
            val a = r[i]; val b = r[i + 1]
            if (y <= a.y && y >= b.y) return a.x + (b.x - a.x) * (a.y - y) / (a.y - b.y).coerceAtLeast(0.001f)
        }
        val a = r[0]; val b = r[1]
        return a.x + (b.x - a.x) * (a.y - y) / (a.y - b.y).coerceAtLeast(0.001f)
    }

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

    /**
     * Dónde está el buzo, en el fondo (en metros del barco: u, v): a unos nueve metros del costado, por la aleta de proa.
     * Encima suben sus burbujas. **[Diseño propio]**: más lejos del costado que lo que daría la manguera recta, para que la
     * manguera y la cuerda de vida se vean entrar en el agua (pegadas al casco, la regala las taparía).
     */
    const val BUZO_U = 10f
    const val BUZO_V = 6.5f
    fun burbujas(): Offset { val c = B.cam(BUZO_U, BUZO_V); return B.pc(c.x, -B.FRANCO, c.y) }
    fun kBurbujas(): Float = B.k(B.cam(BUZO_U, BUZO_V).y)

    /** Los otros barcos esponjeros, fondeados en su sitio: (x, y del pie en el lienzo, largo, rumbo). Lejos, cada uno distinto. */
    private val OTROS = listOf(floatArrayOf(250f, 103.5f, 46f, -1f), floatArrayOf(352f, 99.5f, 26f, 1f), floatArrayOf(28f, 101f, 28f, 1f), floatArrayOf(182f, 98.6f, 16f, 1f))

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
            with(a) {
                pintar(zona, Semilla.de("abismo", "banco"), sombrasEnElFondo(v, a), m.semilla("agua"), ::bordeDelAgua) {
                    cadenaBajoElAgua(v, a)
                    if (EscenaAbismo.buzoAbajo(m)) buzoBajoElAgua(v, a)
                }
                calleDeLuna(Rect(0f, B.H, ANCHO, ALTO), v.luna, m.semilla("luna"), ::bordeDelAgua)
            }
            if (v.luna < 0.05f) estela(l, Rect(0f, B.H, ANCHO, ALTO), Azar(m.semilla("estela")))
            otrosBarcos(v)
            pelicanos(v)
            orillaDelCasco(v)
            cadenaSobreElAgua(v)
            boteAmarrado(v)
            if (EscenaAbismo.buzoAbajo(m)) buzoSobreElAgua(v)
        }
    }

    /**
     * Las manchas de sombra en el fondo, en metros (x, z) de la cámara: la del casco del barco y la del bote, corridas por
     * el sol a través del agua ([BarcoAbismo.corrimientoDeLaSombra]).
     */
    private fun sombrasEnElFondo(v: BarcoAbismo.Vista, a: AguaClara): List<List<Offset>> {
        if (a.r.fuerza < 0.05f) return emptyList()
        val d = B.corrimientoDeLaSombra(a)
        // Del costado de popa de quien mira a la roda (lo de más a popa queda debajo y detrás de los ojos y no se ve).
        val casco = (0..14).map { i -> val vv = 1.4f + (B.RODA - 1.4f) * i / 14f; B.cam(B.estribor(vv) + 0.1f, vv) } + (14 downTo 0).map { i -> val vv = 1.4f + (B.RODA - 1.4f) * i / 14f; B.cam(B.babor(vv) - 0.1f, vv) }
        val bote = SITIO_BOTE.let { s -> (0..10).map { i -> val t = i * 2f * PI.toFloat() / 10f; Offset(s.mx(BOTE_MANGA * 0.5f * cos(t), BOTE_LARGO * 0.5f * sin(t)), s.mz(BOTE_MANGA * 0.5f * cos(t), BOTE_LARGO * 0.5f * sin(t))) } }
        val db = a.corre * a.hondo
        return listOf(casco.map { it + d }, bote.map { it + db })
    }

    /** Las mismas sombras, en el lienzo (para que la red de luz no entre en ellas). */
    private fun sombrasEnElLienzo(v: BarcoAbismo.Vista, a: AguaClara): List<Path> =
        sombrasEnElFondo(v, a).mapNotNull { q -> val pts = q.filter { it.y > 0.5f }.map { a.enElFondo(it.x, it.y) }; if (pts.size >= 3) poligono(pts) else null }

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

    /**
     * El horizonte: «donde no se ve tierra»; algunos días, la costa baja de Florida, «una raya de árboles», al este; el día
     * del huracán, la ensenada entre manglares: dos masas de copas redondas, cada una distinta, la lejana más pálida, con
     * las raíces al pie y su reflejo.
     */
    private fun DrawScope.costa(v: BarcoAbismo.Vista) {
        val l = v.l
        when (EscenaAbismo.costa(v.m)) {
            Costa.NINGUNA -> Unit
            Costa.RAYA -> {
                val az = Azar(Semilla.de("abismo", "costa"))
                val pts = ArrayList<Offset>(); var x = 236f
                pts += Offset(x, B.H)
                while (x < ANCHO + 6f) { val w = az.entre(2f, 6f); pts += Offset(x + w * 0.5f, B.H - az.entre(1.2f, 3.6f)); x += w }
                pts += Offset(ANCHO + 6f, B.H)
                drawPath(curva(pts), l.tono(lerp(C.HIERBA, C.MAR_HONDO, 0.4f), 0.88f))
                drawRect(l.tono(C.ARENA, 0.8f), Offset(236f, B.H - 0.6f), Size(ANCHO - 236f, 0.8f))
            }
            Costa.ENSENADA -> {
                // La ensenada entre manglares: copas redondas de tamaños distintos, en dos filas, con la cara de luz arriba.
                for ((capa, tramos) in listOf(0 to listOf(-10f to 120f, 268f to 392f), 1 to listOf(-10f to 70f, 318f to 392f))) {
                    val az = Azar(Semilla.de("abismo", "manglar", capa))
                    val lejos = if (capa == 0) 0.62f else 0.4f
                    val verde = lerp(C.HIERBA, C.BREA, 0.25f + 0.12f * capa)
                    for ((x0, x1) in tramos) {
                        var x = x0; val base = B.H + 2.5f + capa * 2f
                        val alto = if (capa == 0) 9f else 14f
                        drawRect(l.tono(lerp(verde, C.BREA, 0.3f), lejos), Offset(x0, base - alto * 0.45f), Size(x1 - x0, alto * 0.45f + 0.5f))
                        while (x < x1) {
                            val r = az.entre(3.5f, 8.5f) * (alto / 12f); val y = base - alto * az.entre(0.45f, 0.95f)
                            drawOval(l.tono(verde, lejos), Offset(x - r, y - r * 0.8f), Size(r * 2.1f, r * 1.7f))
                            drawOval(l.tono(lerp(verde, C.ARENA, 0.25f), lejos), Offset(x - r * 0.7f, y - r * 0.85f), Size(r * 1.3f, r * 0.7f))
                            x += r * az.entre(0.9f, 1.6f)
                        }
                        // Las raíces al pie: patas finas sobre el agua.
                        var xr = x0 + 2f
                        while (xr < x1) { drawLine(l.tono(lerp(verde, C.BREA, 0.5f), lejos), Offset(xr, base - 1.2f), Offset(xr + az.entre(-1.2f, 1.2f), base + 0.6f), 0.4f); xr += az.entre(1.4f, 3.2f) }
                        reflejoRoto(x0, x1, base + 0.6f, 4f + capa * 2f, l.tono(verde, lejos), 0.35f, Azar(Semilla.de("abismo", "manglarRefl", capa)), 0.8f)
                    }
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
            val pie = Offset(o[0], o[1]); val lejos = (0.55f - (o[1] - B.H) / 18f - o[2] / 140f).coerceIn(0.18f, 0.55f)
            val p = v.p
            reflejoRoto(pie.x - o[2] * 0.55f, pie.x + o[2] * 0.55f, pie.y + 0.4f, o[2] * 0.26f, v.l.tono(C.BLANCO, lejos), 0.42f, Azar(m.semilla("refl", i)), 0.8f)
            reflejoRoto(pie.x - o[2] * 0.4f, pie.x + o[2] * 0.4f, pie.y + 0.4f, o[2] * 0.1f, v.l.tono(C.AZUL, lejos), 0.4f, Azar(m.semilla("reflAzul", i)), 0.6f)
            barcoLejano(p, pie, o[2], o[3], Semilla.de("abismo", "otro", i), lejos, ristras = 0.3f + 0.6f * EscenaAbismo.esponjas(m), farol = farol,
                luto = EscenaAbismo.luto(m), empavesado = EscenaAbismo.fiesta(m))
        }
        // El barco de al lado, el día del buzo paralizado: más cerca, con su bomba parada.
        if (EscenaAbismo.paralisis(m)) {
            reflejoRoto(330f - 40f, 330f + 40f, 112.4f, 18f, v.l.tono(C.BLANCO, 0.15f), 0.45f, Azar(m.semilla("reflVecino")), 0.8f)
            barcoLejano(v.p, Offset(330f, 112f), 74f, -1f, Semilla.de("abismo", "vecino"), 0.14f, ristras = 0.5f)
        }
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
            reflejoRoto(pie.x - B.k(z) * 0.4f, pie.x + B.k(z) * 0.4f, pie.y + 0.3f, B.k(z) * 0.35f, v.l.tono(lerp(C.PLOMO, C.BLANCO, 0.4f)), 0.35f, Azar(v.m.semilla("pelRefl", i)), 0.6f)
            pelicano(v.p, pie, B.k(z), if (az.real() > 0.5f) 1f else -1f, Postura.NADANDO, Semilla.de("abismo", "pelicano", i), lejos = 0.12f)
        }
    }

    /**
     * La franja de paso entre el casco y el agua: junto al costado el agua es más honda de color (el casco le quita el
     * cielo que devuelve) y la rompe una línea de chapoteo. **[Aproximación]**: desde la cubierta, el agua que lame el casco
     * queda tapada por la regala y el reflejo del costado no se ve; lo que sí se ve, a uno o dos metros, es esta franja
     * oscura, y sin ella la regala se pegaba al agua como un recorte.
     */
    private fun DrawScope.orillaDelCasco(v: BarcoAbismo.Vista) {
        val r = REGALA_FUERA
        val oscuro = v.l.tono(lerp(C.MAR_HONDO, C.SOMBRA, 0.2f))
        val n = r.indices.map { i -> val a = r[(i - 1).coerceAtLeast(0)]; val b = r[(i + 1).coerceAtMost(r.size - 1)]; val t = b - a; var q = Offset(-t.y, t.x) / t.getDistance().coerceAtLeast(0.01f); if (q.x < 0f) q = -q; q }
        for ((k, al) in listOf(1.5f to 0.3f, 4f to 0.2f, 7.5f to 0.12f, 12f to 0.06f)) {
            drawPath(curva(r.mapIndexed { i, p -> p + n[i] * (k * B.k(B.cam(B.estribor(1f), 1f + i * 0.33f).y) / 60f) }), oscuro.copy(alpha = al), style = Stroke(3.5f, cap = StrokeCap.Round))
        }
        if (v.mar != Mar.CALMA && v.l.noche < 0.6f) {
            val az = Azar(Semilla.de("abismo", "chapoteo"))
            for (i in 2 until r.size - 2 step 2) { val p = r[i] + n[i] * az.entre(3f, 6f); drawLine(lerp(v.l.bajo, Color.White, 0.4f).copy(alpha = 0.3f), p, p + Offset(az.entre(2f, 5f), -0.5f), 0.6f, StrokeCap.Round) }
        }
    }

    // ---------------------------------------------------------------- La cadena del ancla

    /** La cadena del ancla: del escobén de la proa de estribor baja en catenaria hasta el agua y, dentro, se pierde hacia el fondo (en metros del barco: u, h, v). */
    private val ESCOBEN = V3(B.estribor(10.6f) - 0.12f, 0.95f, 10.6f)
    private val ENTRA = V3(4.2f, -B.FRANCO, 13.2f)
    private val ANCLA = V3(7.5f, -B.FRANCO - 4.4f, 23f)

    private fun cadenaArriba(): List<Offset> = (0..10).map { i -> val t = i / 10f; val q = ESCOBEN + (ENTRA - ESCOBEN) * t; B.pb(q.x, q.y - 0.6f * 4f * t * (1f - t) * 0.5f, q.z) }

    private fun DrawScope.cadenaSobreElAgua(v: BarcoAbismo.Vista) {
        val pts = cadenaArriba(); val p = v.p
        val g = 0.07f * B.k(B.cam(ENTRA.x, ENTRA.z).y)
        val hierro = lerp(C.PLOMO, C.COBRE_OSC, 0.25f)
        drawPath(curva(pts), p.sombra(hierro), style = Stroke(g * 1.4f, cap = StrokeCap.Round))
        drawPath(curva(pts), p.medio(hierro), style = Stroke(g, cap = StrokeCap.Butt, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 1.1f, g * 0.5f))))
        drawPath(curva(pts.map { it + Offset(-g * 0.2f, -g * 0.25f) }), p.luz(hierro).copy(alpha = 0.5f), style = Stroke(g * 0.3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 0.6f, g * 1f))))
        // Donde entra, el agua hace su corro.
        val e = pts.last(); drawOval(lerp(v.l.bajo, Color.White, 0.35f).copy(alpha = 0.35f * (1f - v.l.noche)), Offset(e.x - g * 2.5f, e.y - g * 0.4f), Size(g * 5f, g * 0.9f), style = Stroke(0.5f))
    }

    private fun DrawScope.cadenaBajoElAgua(v: BarcoAbismo.Vista, a: AguaClara) {
        with(a) { hundido((0..10).map { i -> val q = ENTRA + (ANCLA - ENTRA) * (i / 10f); val c = B.cam(q.x, q.z); V3(c.x, -B.FRANCO - q.y, c.y) }, lerp(C.PLOMO, C.BREA, 0.3f), 0.06f, cadena = true) }
    }

    /** Los puntos de la cadena en el lienzo, desde que entra en el agua (para el verde vivo de la noche). */
    private fun cadenaEnElLienzo(a: AguaClara): List<Offset> = (0..6).map { i -> val q = ENTRA + (ANCLA - ENTRA) * (i / 10f); val c = B.cam(q.x, q.z); a.sobre(c.x, -B.FRANCO - q.y, c.y) }

    // ---------------------------------------------------------------- El buzo, abajo

    /** La manguera y la cuerda de vida, en metros del barco (u, h, v): de la regala, por fuera, tensas hacia el buzo. */
    private fun lineaAlBuzo(vRegala: Float, lado: Float): Pair<List<V3>, List<V3>> {
        val a = V3(B.estribor(vRegala) + 0.12f, B.regala(vRegala) - 0.08f, vRegala)
        val buzo = V3(BUZO_U + lado * 0.3f, -B.FRANCO - 3.6f, BUZO_V + lado * 0.2f)
        val tAgua = (a.y + B.FRANCO) / (a.y - buzo.y)
        val e = a + (buzo - a) * tAgua
        val fuera = (0..6).map { i -> val t = i / 6f; val q = a + (e - a) * t; V3(q.x, q.y - 0.18f * 4f * t * (1f - t), q.z) }
        val dentro = (0..8).map { i -> val t = i / 8f; e + (buzo - e) * t }
        return fuera to dentro
    }

    private fun DrawScope.buzoSobreElAgua(v: BarcoAbismo.Vista) {
        val (manga, _) = lineaAlBuzo(B.BAJADA_V, 0f); val (cuerda, _) = lineaAlBuzo(B.BAJADA_V + 0.45f, 1f)
        val kk = B.k(B.cam(B.estribor(B.BAJADA_V), B.BAJADA_V).y)
        manguera(v.p, manga.map { B.pb(it.x, it.y, it.z) }, 0.05f * kk)
        cabo(v.p, cuerda.map { B.pb(it.x, it.y, it.z) }, 0.022f * kk)
        for (q in listOf(manga.last(), cuerda.last())) { val e = B.pb(q.x, q.y, q.z); drawOval(lerp(v.l.bajo, Color.White, 0.4f).copy(alpha = 0.4f), Offset(e.x - 3f, e.y - 0.5f), Size(6f, 1.1f), style = Stroke(0.5f)) }
    }

    private fun DrawScope.buzoBajoElAgua(v: BarcoAbismo.Vista, a: AguaClara) {
        for ((vr, lado, color, g) in listOf(Quad(B.BAJADA_V, 0f, lerp(C.BREA, C.PLOMO, 0.3f), 0.05f), Quad(B.BAJADA_V + 0.45f, 1f, C.CANAMO, 0.025f))) {
            val dentro = lineaAlBuzo(vr, lado).second.map { val c = B.cam(it.x, it.z); V3(c.x, -B.FRANCO - it.y, c.y) }
            with(a) { hundido(dentro, color, g) }
        }
    }

    private data class Quad(val a: Float, val b: Float, val c: Color, val d: Float)

    /** El bote auxiliar, amarrado al costado, con su reflejo y su sombra. */
    private fun DrawScope.boteAmarrado(v: BarcoAbismo.Vista) {
        val s = SITIO_BOTE; val e = s.escala(0f, 0f)
        val flot = PiezasAbismo.boteFlotacion(s, BOTE_LARGO, BOTE_MANGA)
        val bb = flot.getBounds()
        // El reflejo del casco blanco y de su franja azul, y la sombra que el bote echa en el agua junto a él.
        reflejoRoto(bb.left, bb.right, bb.bottom - bb.height * 0.3f, e * 0.5f, v.l.tono(C.BLANCO), 0.5f, Azar(91), 0.9f)
        reflejoRoto(bb.left + bb.width * 0.1f, bb.right - bb.width * 0.1f, bb.bottom - bb.height * 0.3f, e * 0.16f, v.l.tono(C.AZUL), 0.45f, Azar(92), 0.7f)
        drawPath(flot, v.l.tono(C.MAR_HONDO).copy(alpha = 0.35f))
        bote(v.p, s, v.sol, BOTE_LARGO, BOTE_MANGA, Semilla.de("abismo", "bote"))
        // La boza: el cabo del bote a la regala.
        val proa = s.p(0f, 0.4f, BOTE_LARGO * 0.48f)
        val regala = B.pb(B.estribor(8.6f), B.regala(8.6f), 8.6f)
        cabo(v.p, listOf(proa, (proa + regala) * 0.5f + Offset(0f, 3f), regala), 0.5f)
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
                    redDeLuz(zona, Rect(0f, B.H, ANCHO, ALTO), reloj, sombrasEnElLienzo(v, a), ::bordeDelAgua)
                    if (EscenaAbismo.buzoAbajo(m)) burbujas(burbujas(), kBurbujas(), reloj)
                    // El verde vivo de noche: donde el agua toca el bote, la cadena del ancla y la hornacina sobre el agua.
                    val verde = if (v.mar == Mar.TORMENTA) 0.4f else 1f
                    if (l.noche > 0.45f) verdeVivo(listOf(SITIO_BOTE.p(0f, 0f, 0f), SITIO_BOTE.p(0f, 0f, BOTE_LARGO * 0.45f), SITIO_BOTE.p(0f, 0f, -BOTE_LARGO * 0.45f), cadenaArriba().last()), cadenaEnElLienzo(a),
                        Rect(0f, B.H, ANCHO, ALTO), reloj, verde * (l.noche - 0.45f) / 0.55f, ::bordeDelAgua)
                }
                // Los faroles de los otros barcos, temblando en el agua; y la lamparilla y el farol de a bordo.
                if (EscenaAbismo.farolesLejos(m)) {
                    val cuantos = 2 + (Azar(m.semilla("otros")).entero(3))
                    for (i in 0 until cuantos) { val o = OTROS[i]; reflejo(Offset(o[0] + o[3] * -0.18f * o[2], o[1]), 10f + o[2] * 0.25f, C.LUMBRE, 0.7f * l.noche, reloj, i) }
                }
            }
        }
        tiempoVivo(l, m.ficha.clima, reloj, B.H, m.semilla("tiempo"))
    }

    /** La caja de la hornacina del icono en el lienzo (para que lo del agua no la pise). */
    fun iconoCaja(): Rect { val c = B.ICONO; val k = B.K_ICONO; return Rect(c.x - 0.22f * k, c.y - 0.5f * k, c.x + 0.24f * k, c.y + 1.2f * k) }
}
