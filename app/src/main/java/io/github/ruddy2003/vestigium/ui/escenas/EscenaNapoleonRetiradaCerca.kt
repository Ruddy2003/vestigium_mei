package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Fase
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Marmita
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.Carroza
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.Pabellon
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.aguila
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.capote
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.carroza
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.cofre
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.espejo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.fusil
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.hoguera
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.horquillas
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.marmita
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.mochila
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.pabellon
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.pino
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.ramas
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.ruedaSuelta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.tambor
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.tienda
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaNapoleon.MIRADA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * El primer plano de la retirada:
 * - **El alto, a la derecha**: el fuego de ruedas y tablas sobre la nieve derretida y negra; la marmita en sus
 *   horquillas; el fusil con la carne en la bayoneta; los fusiles en pabellón, con el chacó colgado y las cartucheras
 *   (el vertical que cruza las franjas, regla 6); las mochilas con la manta enrollada, los capotes, el tambor cortado
 *   por el borde; el asta del águila envuelta en trapos, apoyada en el pabellón, en diagonal (la otra que cruza). De
 *   noche, las ramas y las mantas de dormir. Lo que está o no a cada hora lo dicen [EscenaNapoleon.pabellon],
 *   [EscenaNapoleon.marmita], [EscenaNapoleon.carneEnLaBayoneta] y [EscenaNapoleon.camas].
 * - **Lo abandonado, en el centro**: el cofre abierto con el candelabro, las telas y el cuadro del santo; el espejo roto
 *   que devuelve el cielo; la rueda suelta de la carroza, echada y cortada por el borde de abajo.
 * - **El marco, a la izquierda y arriba**: la carroza rota volcada, con la rueda que cruza el cielo, al pie del pino
 *   grande, cortado por el borde, cuyas ramas cargadas de nieve entran por arriba.
 */
internal object RetiradaCerca {
    private val C = ColorNapoleon

    /** Dónde arde el fuego del alto (su centro, en el suelo) y de dónde sale su luz. */
    val FUEGO = Offset(306f, 246f)
    val LUZ_DEL_FUEGO = Offset(306f, 228f)

    /** El sitio de cada cosa. */
    private val S_FUEGO = PiezasNapoleon.sitio(MIRADA, FUEGO, 0.2f)
    private val S_PABELLON = PiezasNapoleon.sitio(MIRADA, Offset(262f, 230f), 0.35f)
    private val S_SOPORTE = PiezasNapoleon.sitio(MIRADA, Offset(236f, 257f), -0.45f)
    private val S_MOCHILA2 = PiezasNapoleon.sitio(MIRADA, Offset(214f, 268f), 0.5f)
    private val S_MOCHILA3 = PiezasNapoleon.sitio(MIRADA, Offset(352f, 236f), -0.2f)
    private val S_TAMBOR = PiezasNapoleon.sitio(MIRADA, Offset(364f, 270f), 0.4f)
    private val S_COFRE = PiezasNapoleon.sitio(MIRADA, Offset(150f, 250f), -0.42f)
    private val S_ESPEJO = PiezasNapoleon.sitio(MIRADA, Offset(194f, 271f), 0.25f)
    private val S_RUEDA = PiezasNapoleon.sitio(MIRADA, Offset(74f, 292f), 0f)
    private val S_CARROZA = PiezasNapoleon.sitio(MIRADA, Offset(22f, 212f), -0.98f)
    private val S_TIENDA = PiezasNapoleon.sitio(MIRADA, Offset(78f, 184f), 0.55f)

    /** El pino grande: su pie, cortado por el borde de la izquierda. */
    val PIE_DEL_PINO = Offset(2f, 214f)

    /** Donde se apoya el objeto del día: encima de la mochila de pie, junto al pabellón. */
    val SOPORTE: Offset get() = S_SOPORTE.pie
    val ESPEJO: Offset get() = S_ESPEJO.pie

    /** El pabellón de los fusiles. */
    private val PABELLON = Pabellon(S_PABELLON, Semilla.de("napoleon", "pabellon"), 4, 2, true)

    /** La carroza volcada. */
    private fun carrozaDe(nieve: Float) = Carroza(S_CARROZA, nieve)

    /** El sitio alto del gato: en el costado de arriba de la carroza, lejos de la portezuela. */
    val MIRADOR: Offset get() = carrozaDe(0f).let { c -> c.s.p(c.t(V3(c.w, 1.75f, 0.8f))).let { Offset(it.x.coerceIn(12f, 360f), it.y.coerceIn(30f, 260f)) } }

    /** El pie y la punta del asta del águila: apoyada en el cruce de las bayonetas del pabellón. */
    private val PIE_ASTA = Offset(330f, 271f)
    private fun puntaAsta(): Offset { val apice = S_PABELLON.p(PABELLON.apice); val d = apice - PIE_ASTA; return PIE_ASTA + d * 1.36f }

    /** El fusil de la carne, que sale del pabellón y se inclina sobre el fuego. */
    private val FUSIL_CULATA = V3(-0.95f, 0f, -0.4f)
    private val FUSIL_BOCA = V3(-0.05f, 0.62f, 0.05f)

    // ---------------------------------------------------------------- Las sombras

    /** Lo que tumba sombra con el sol o la luna. */
    fun sombras(v: VistaNapoleon): List<RetiradaLuz.Sombra> = buildList {
        val ts = 0.022f * S_PABELLON.k
        add(RetiradaLuz.Sombra(PIE_DEL_PINO, poligono(PIE_DEL_PINO + Offset(-20f, 0f), PIE_DEL_PINO + Offset(22f, 0f), Offset(8f, -40f), Offset(-12f, -40f))))
        if (RetiradaNapoleon.carroza(v.m)) add(RetiradaLuz.Sombra(S_CARROZA.pie, PiezasNapoleon.siluetaDeCarroza(carrozaDe(v.nieve))))
        else add(RetiradaLuz.Sombra(S_TIENDA.pie, siluetaCaja(S_TIENDA, 1.4f, 2.6f)))
        if (EscenaNapoleon.pabellon(v.m)) add(RetiradaLuz.Sombra(S_PABELLON.pie, PiezasNapoleon.siluetaDePabellon(PABELLON) { S_PABELLON.p(it) }))
        if (!v.vivac) add(RetiradaLuz.Sombra(S_COFRE.pie, siluetaCaja(S_COFRE, 0.45f, 0.62f)))
        add(RetiradaLuz.Sombra(S_SOPORTE.pie, siluetaCaja(S_SOPORTE, 0.22f, 0.46f)))
        add(RetiradaLuz.Sombra(S_MOCHILA3.pie, siluetaCaja(S_MOCHILA3, 0.22f, 0.46f)))
        add(RetiradaLuz.Sombra(S_TAMBOR.pie, siluetaCaja(S_TAMBOR, 0.22f, 0.44f)))
        if (EscenaNapoleon.pabellon(v.m)) { val pa = puntaAsta(); add(RetiradaLuz.Sombra(PIE_ASTA, poligono(PIE_ASTA + Offset(-ts, 0f), PIE_ASTA + Offset(ts, 0f), pa + Offset(ts * 2f, 0f), pa + Offset(-ts * 2f, 0f)))) }
        if (EscenaNapoleon.marmita(v.m) == Marmita.AL_FUEGO) add(RetiradaLuz.Sombra(FUEGO, siluetaCaja(S_FUEGO, 0.55f, 0.95f)))
    }

    /** Lo que tumba sombra con la luz del fuego, en abanico: lo que está cerca de él. */
    fun sombrasDelFuego(v: VistaNapoleon): List<RetiradaLuz.Sombra> = sombras(v).filter { (it.pie - FUEGO).getDistance() in 20f..240f }

    private fun siluetaCaja(s: Sitio, ancho: Float, alto: Float): Path = poligono(listOf(s.p(-ancho, 0f, 0f), s.p(ancho, 0f, 0f), s.p(ancho * 0.85f, alto, 0f), s.p(-ancho * 0.85f, alto, 0f)))

    // ---------------------------------------------------------------- El primer plano

    fun DrawScope.primerPlano(v: VistaNapoleon) {
        val m = v.m; val nieve = v.nieve; val az = Azar(Semilla.de("napoleon", "cerca"))
        sueloCercano(v, az)
        val fase = v.fase
        val cosas = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        // El tronco del pino grande (las ramas van en el marco).
        cosas += parte(PIE_DEL_PINO.y) { troncoDelPino(v) }
        // La carroza volcada (en el vivac, la tienda de oficial).
        if (RetiradaNapoleon.carroza(m)) cosas += parte(S_CARROZA.pie.y) { carroza(carrozaDe(nieve), v.en(S_CARROZA.pie)) }
        else cosas += parte(S_TIENDA.pie.y) { tienda(S_TIENDA, v.en(S_TIENDA.pie), if (v.noche > 0.4f && fase != Fase.MARCHA) 1f else 0f, nieve) }
        // El fuego, con las horquillas y la marmita.
        cosas += parte(FUEGO.y) {
            val a = v.en(FUEGO)
            hoguera(S_FUEGO, a, v.fuego, nieve, Semilla.de("napoleon", "hoguera"))
            when (EscenaNapoleon.marmita(m)) {
                Marmita.AL_FUEGO -> { val vara = horquillas(S_FUEGO, a); marmita(S_FUEGO, a, vara + V3(0f, -0.48f, 0f), hirviendo = v.fuego > 0.6f) }
                Marmita.AL_LADO -> marmita(S_FUEGO, a, V3(-0.95f, 0f, -0.35f), tapaEnElSuelo = true)
                Marmita.NINGUNA -> Unit
            }
        }
        // Los fusiles en pabellón, cuando está la compañía; con la carne, el fusil sobre el fuego.
        if (EscenaNapoleon.pabellon(m)) cosas += parte(S_PABELLON.pie.y) { pabellon(PABELLON.copia(nieve), v.en(S_PABELLON.pie)) }
        if (EscenaNapoleon.carneEnLaBayoneta(m)) cosas += parte(FUEGO.y + 12f) { fusil(S_FUEGO, v.en(FUEGO), FUSIL_CULATA, FUSIL_BOCA, true, 0.1f, 0f, nieve, carne = true) }
        // Las mochilas y los capotes: de marcha queda una, con un capote viejo (lo que no se pudo cargar).
        val conGente = fase != Fase.MARCHA
        cosas += parte(S_SOPORTE.pie.y) {
            val a = v.en(S_SOPORTE.pie)
            val (_, sobre) = mochila(S_SOPORTE, a, 41L, false, C.CAPOTE, 0f, nieve)
            with(ObjetosNapoleon) { sobreElSoporte(m, v.p.con(a.calor(sobre)), sobre, 1.1f) }
        }
        if (conGente) cosas += parte(S_MOCHILA2.pie.y) { mochila(S_MOCHILA2, v.en(S_MOCHILA2.pie), 42L, true, lerp(C.CAPOTE, C.LONA, 0.4f), 0f, nieve) }
        cosas += parte(S_MOCHILA3.pie.y) { mochila(S_MOCHILA3, v.en(S_MOCHILA3.pie), 43L, false, lerp(C.CAPOTE, C.AZUL, 0.2f), 0f, nieve) }
        cosas += parte(254f) { val q = Offset(352f, 254f); capote(v.en(q), q, 46f, 15f, Azar(51L), false, 0f, nieve) }
        if (conGente) cosas += parte(242f) { val q = Offset(222f, 242f); capote(v.en(q), q, 34f, 10f, Azar(52L), true, 0f, nieve) }
        // Las ramas y las mantas de dormir, alrededor del fuego.
        if (EscenaNapoleon.camas(m)) cosas += parte(268f) {
            val q = Offset(268f, 270f); ramas(v.en(q), q, 70f, 16f, Azar(53L))
            capote(v.en(q), q + Offset(6f, -2f), 42f, 11f, Azar(54L), true, 0f, nieve * 0.5f)
            val q2 = Offset(338f, 276f); ramas(v.en(q2), q2, 56f, 12f, Azar(55L)); capote(v.en(q2), q2 + Offset(-4f, -1f), 38f, 10f, Azar(56L), false, 0f, nieve * 0.5f)
        }
        // El tambor, cortado por el borde.
        cosas += parte(S_TAMBOR.pie.y) { tambor(S_TAMBOR, v.en(S_TAMBOR.pie), false, 0f, nieve) }
        // El asta del águila, apoyada en el pabellón: está mientras está la compañía (de marcha, va con ella).
        if (EscenaNapoleon.pabellon(m)) cosas += parte(PIE_ASTA.y) {
            aguila(v.en(PIE_ASTA), PIE_ASTA, puntaAsta(), 0.034f * S_SOPORTE.k, EscenaNapoleon.aguilaEnvuelta(m), EscenaNapoleon.luto(m), Azar(57L), 0f, nieve)
        }
        // El objeto grande del día, apoyado en el pabellón (o en la mochila).
        ObjetosNapoleon.grande(m, v.p.con(v.a.calor(Offset(282f, 250f))), Offset(282f, 252f), Offset(270f, 176f), 1.1f)?.let { g -> cosas += parte(g.pie.y) { g.pintar(this) } }
        // Lo abandonado del centro (en la retirada).
        if (!v.vivac) {
            cosas += parte(S_COFRE.pie.y) { cofre(S_COFRE, v.en(S_COFRE.pie), v.nieve, 0f, nieve) }
            cosas += parte(S_ESPEJO.pie.y) { espejo(S_ESPEJO, v.en(S_ESPEJO.pie), v.l.alto, v.l.bajo) }
            cosas += parte(ALTO + 2f) { ruedaSuelta(S_RUEDA, v.en(S_RUEDA.pie), nieve) }
        }
        for ((_, pintar) in cosas.sortedBy { it.first }) pintar()
    }

    /** La nieve de delante, con su textura: huellas, lo pisado alrededor del alto, el brillo de los cristales al sol. */
    private fun DrawScope.sueloCercano(v: VistaNapoleon, az: Azar) {
        val p = v.p
        // Lo pisado alrededor del fuego y entre las mochilas: la nieve aplastada y gris.
        if (v.nieve > 0.3f) {
            val pisado = TrazosValle.blando(Offset(296f, 258f), 92f, 26f, Azar(61L), 11, 0.18f)
            drawPath(pisado, p.arriba(lerp(C.NIEVE, C.NIEVE_SUCIA, 0.55f), 0f).copy(alpha = 0.75f))
            repeat(40) { val q = Offset(az.entre(220f, 380f), az.entre(240f, 284f)); drawOval(p.arriba(C.NIEVE_SUCIA, 0f).copy(alpha = 0.5f), q, androidx.compose.ui.geometry.Size(az.entre(3f, 6f), az.entre(1.4f, 2.4f))) }
        }
        // Los cristales de la nieve que brillan al sol (en la parte quieta: no parpadean).
        if (v.nieve > 0.5f && v.r.fuerza > 0.2f) repeat(70) {
            val q = Offset(az.entre(0f, ANCHO), az.entre(180f, ALTO)); val r = az.entre(0.3f, 0.7f) * (q.y / 230f)
            if (RetiradaLuz.sombraEn(v.franjas, q) < 0.5f) drawCircle(Color.White.copy(alpha = 0.7f * v.r.fuerza), r, q, blendMode = BlendMode.Plus)
        }
    }

    // ---------------------------------------------------------------- El pino grande

    private val AZ_PINO = Semilla.de("napoleon", "pino", "grande")

    /** El tronco del pino grande, con su corteza, su lado de luz, la nieve pegada del lado del viento y los muñones de las ramas muertas. */
    private fun DrawScope.troncoDelPino(v: VistaNapoleon) {
        val a = v.en(PIE_DEL_PINO); val p = PIE_DEL_PINO
        val tronco = Path().apply { moveTo(p.x - 24f, p.y + 2f); cubicTo(p.x - 18f, p.y - 10f, p.x - 15f, p.y - 60f, p.x - 13f, -10f); lineTo(p.x + 13f, -10f); cubicTo(p.x + 15f, p.y - 60f, p.x + 18f, p.y - 12f, p.x + 26f, p.y + 2f); close() }
        drawPath(tronco, Brush.horizontalGradient(listOf(a.plano(C.MADERA_OSC, p, -0.9f), a.plano(C.MADERA_OSC, p, 0f), a.plano(lerp(C.MADERA_OSC, C.MADERA, 0.4f), p, 0.9f), a.plano(C.MADERA_OSC, p, 0.4f)), p.x - 24f, p.x + 26f))
        clipPath(tronco) {
            // La corteza en placas, con las grietas oscuras.
            val az = Azar(AZ_PINO + 1)
            repeat(70) {
                val x = az.entre(p.x - 18f, p.x + 20f); val y = az.entre(-10f, p.y); val h = az.entre(8f, 22f)
                drawLine(a.plano(lerp(C.CENIZA, C.MADERA_OSC, 0.3f), Offset(x, y), 0f), Offset(x, y), Offset(x + az.entre(-1f, 1f), y + h), az.entre(0.6f, 1.4f), StrokeCap.Round)
                if (az.real() < 0.4f) drawLine(a.plano(C.MADERA, Offset(x, y), 0.9f).copy(alpha = 0.6f), Offset(x + 1.2f, y + 2f), Offset(x + 1.4f, y + h * 0.6f), 0.7f, StrokeCap.Round)
            }
            // La nieve pegada en la corteza, del lado del viento, y la resina.
            if (v.nieve > 0.3f) repeat(10) { val y = az.entre(20f, p.y - 10f); mancha(Offset(p.x - 12f, y), 4f, az.entre(5f, 12f), a.plano(C.NIEVE, Offset(p.x - 12f, y), -0.6f), 0.7f * v.nieve) }
            mancha(Offset(p.x + 8f, 150f), 2f, 5f, a.plano(lerp(C.LATON, C.MADERA, 0.4f), p, 0.5f), 0.6f)
        }
        // La raíz que asoma y la nieve al pie, arrimada.
        if (v.nieve > 0.2f) drawPath(TrazosValle.blando(p + Offset(2f, -2f), 34f, 7f, Azar(AZ_PINO + 2), 9, 0.2f, 0.5f), a.arriba(C.NIEVE, p))
        else drawPath(TrazosValle.blando(p + Offset(2f, -1f), 30f, 5f, Azar(AZ_PINO + 2), 9, 0.2f, 0.5f), a.arriba(lerp(C.HOJA, C.BARRO, 0.4f), p))
    }

    /**
     * El marco: las ramas bajas del pino grande, cargadas de nieve, que entran por arriba hasta la mitad del cuadro sin
     * tapar el sol de la mañana ni la luna de la noche. De noche, el fuego las alumbra por debajo, un poco.
     */
    fun DrawScope.marco(v: VistaNapoleon) {
        val a = v.en(Offset(80f, 40f)); val p = PIE_DEL_PINO
        pino(a, p, 760f, 430f, v.nieve, Azar(AZ_PINO), 0f, 1f, false, hastaY = 30f, caidaPropia = 0.17f + 0.12f * v.nieve, grande = true)
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: VistaNapoleon, reloj: Float) {
        val l = v.l; val f = v.fuego; val k = S_FUEGO.k
        if (f > 0.02f) {
            resplandor(LUZ_DEL_FUEGO, (70f + 60f * f) * (0.6f + 0.4f * v.noche), (0.25f + 0.75f * v.noche) * f * (0.92f + 0.08f * sin(reloj * 7f)))
            if (f >= 0.45f) {
                for ((dx, h) in listOf(-0.14f to 0.4f, 0.05f to 0.56f, 0.18f to 0.36f, -0.02f to 0.3f)) llama(FUEGO + Offset(dx * k, -0.12f * k), h * k * f * (0.5f + 0.5f * v.noche), reloj, (dx * 100f).toInt() + 3)
            } else {
                // El rescoldo: alguna llama chica que asoma y las brasas que laten.
                brasas(l, FUEGO + Offset(0f, -2f), 0.5f * k, reloj)
                llama(FUEGO + Offset(4f, -0.08f * k), 0.22f * k * (f / 0.3f).coerceAtMost(1f), reloj, 9)
            }
            // Las chispas que suben.
            val az = Azar(Semilla.de("napoleon", "chispas"))
            repeat(8) { i ->
                val t = ((reloj * az.entre(0.3f, 0.6f) + az.real()) % 1f); val q = FUEGO + Offset(az.entre(-0.2f, 0.2f) * k + sin(reloj * 3f + i) * 3f, -0.3f * k - t * 1.6f * k)
                drawCircle(Color(0xFFFFC060).copy(alpha = ((1f - t) * f).coerceIn(0f, 1f)), 0.5f + 0.4f * (1f - t), q)
            }
        }
        // El humo: del fuego o del rescoldo (de día, más a la vista); la ceniza fría ya no echa.
        if (f > 0.02f) humo(l, FUEGO + Offset(0f, -0.4f * k), 120f, 14f, reloj, v.m.viento + 0.15f, 0.22f + 0.3f * (1f - f), 0f, 4, 9, 0.45f + 0.35f * (1f - f))
        if (f > 0.02f) with(RetiradaLuz) { alContraluz(v, FUEGO + Offset(10f, -60f), 26f, 40f, 1f) }
        // El vapor de la marmita.
        if (EscenaNapoleon.marmita(v.m) == Marmita.AL_FUEGO) humo(l, FUEGO + Offset(0f, -0.95f * k), 40f, 6f, reloj, v.m.viento, 0f, 0f, 5, 5, 0.4f)
    }

    /** Lo mismo que [Pabellon], con la nieve de hoy. */
    private fun Pabellon.copia(nieve: Float) = Pabellon(s, semilla, cuantos, cartucheras, chaco, nieve)
}
