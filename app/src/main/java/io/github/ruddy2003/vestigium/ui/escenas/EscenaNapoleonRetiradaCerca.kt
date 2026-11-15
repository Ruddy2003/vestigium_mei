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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * El primer plano de la retirada:
 * - **El alto, a la derecha**: el fuego de ruedas y tablas sobre la nieve derretida y negra; la marmita en sus
 *   horquillas; el fusil con la carne en la bayoneta; los fusiles en pabellón, con el chacó colgado y las cartucheras
 *   (el vertical que cruza las franjas, regla 6), y a su izquierda, plantada sola, el asta del águila envuelta en trapos
 *   (la otra vertical; no lo toca, para que el pabellón se lea limpio). A la derecha del fuego y un poco más cerca, el
 *   objeto del día: el chico sobre la mochila de pie, con un capote viejo en el suelo; el grande, de pie en ese mismo
 *   sitio (esos días no está la mochila). El tambor, cortado por el borde. De madrugada, la cama de ramas de abeto con
 *   la manta echada. Lo que está o no a cada hora lo dicen [EscenaNapoleon.pabellon], [EscenaNapoleon.marmita],
 *   [EscenaNapoleon.carneEnLaBayoneta] y [EscenaNapoleon.camas].
 * - **Lo abandonado, en el centro**: el cofre abierto con el candelabro, las telas y el cuadro del santo; el espejo roto
 *   que devuelve el cielo; la rueda suelta de la carroza, echada y cortada por el borde de abajo.
 * - **El marco, a la izquierda y arriba**: la carroza rota volcada, con la rueda que cruza el cielo, al pie del pino
 *   grande, cortado por el borde, cuyas ramas cargadas de nieve entran por arriba.
 */
internal object RetiradaCerca {
    private val C = ColorNapoleon

    /** Dónde arde el fuego del alto (su centro, en el suelo) y de dónde sale su luz. */
    val FUEGO = Offset(296f, 238f)
    val LUZ_DEL_FUEGO = Offset(296f, 222f)

    /**
     * El sitio de cada cosa, escalonado en hondo (regla 5): en las dos esquinas de abajo, cortadas por el borde, la rueda
     * suelta y el tambor tumbado; un par de metros más allá, el cofre medio hundido y girado, con el espejo apoyado en su
     * costado; más allá todavía, el alto alrededor del fuego. Entre unos y otros queda el suelo del gato, libre.
     */
    private val S_FUEGO = PiezasNapoleon.sitio(MIRADA, FUEGO, 0.2f)
    private val S_PABELLON = PiezasNapoleon.sitio(MIRADA, Offset(250f, 220f), 0.35f)
    private val S_SOPORTE = PiezasNapoleon.sitio(MIRADA, Offset(350f, 243f), -0.25f)
    private val S_TAMBOR = PiezasNapoleon.sitio(MIRADA, Offset(374f, 306f), 0.75f)
    private val S_COFRE = PiezasNapoleon.sitio(MIRADA, Offset(134f, 248f), -0.5f)
    private val S_ESPEJO = PiezasNapoleon.sitio(MIRADA, Offset(182f, 256f), -0.45f)
    private val S_RUEDA = PiezasNapoleon.sitio(MIRADA, Offset(6f, 302f), 0f)
    private val S_CARROZA = PiezasNapoleon.sitio(MIRADA, Offset(22f, 212f), -0.98f)
    private val S_TIENDA = PiezasNapoleon.sitio(MIRADA, Offset(78f, 184f), 0.55f)

    /** El pino grande: su pie, cortado por el borde de la izquierda. */
    val PIE_DEL_PINO = Offset(9f, 214f)

    /**
     * Donde se apoya el objeto chico del día: encima de la mochila de pie, a la derecha del fuego y un poco más cerca que
     * él (delante de sus horquillas), que la alumbra de lado sin taparla: las llamas, la marmita y el fusil de la carne
     * quedan a su izquierda, y cabe entero el más ancho (la bayoneta con su vaina) sin salirse por el borde.
     */
    val SOPORTE: Offset get() = S_SOPORTE.pie

    /** Donde se sienta el gato a mirar el objeto del día: el borde de su suelo más cercano a él. */
    const val GATO_MIRA_EL_OBJETO = 306f
    val ESPEJO: Offset get() = S_ESPEJO.pie

    /** El pabellón de los fusiles. */
    private val PABELLON = Pabellon(S_PABELLON, Semilla.de("napoleon", "pabellon"), 4, 2, true)

    /** La carroza volcada. */
    private fun carrozaDe(nieve: Float) = Carroza(S_CARROZA, nieve)

    /** El sitio alto del gato: en el costado de arriba de la carroza, lejos de la portezuela. */
    val MIRADOR: Offset get() = carrozaDe(0f).let { c -> c.s.p(c.t(V3(c.w, 1.75f, 0.8f))).let { Offset(it.x.coerceIn(12f, 360f), it.y.coerceIn(30f, 260f)) } }

    /**
     * El pie y la punta del asta del águila: plantada sola en la nieve a la izquierda del pabellón, entre él y el espejo,
     * apenas inclinada hacia él pero sin tocarlo (a dos palmos de sus fusiles a todo lo alto): el pabellón se lee limpio.
     */
    private val PIE_ASTA = Offset(212f, 240f)
    private val PUNTA_ASTA = Offset(222f, 84f)
    private val GRUESO_ASTA = 0.034f * (PIE_ASTA.y - MIRADA.horizonte) / PiezasNapoleon.OJO

    /**
     * Donde pisa el objeto grande del día y hacia dónde se recuesta: en el sitio de la mochila del soporte (esos días no
     * está), a la derecha del fuego y un poco más cerca, donde la lumbre lo alumbra de lado y nada lo tapa (ni las llamas,
     * ni el fusil de la carne, ni el pabellón); lejos del suelo del gato. Su escala es la de las cosas a esa distancia.
     */
    val PIE_GRANDE = Offset(353f, 252f)
    private val APOYO_GRANDE = Offset(338f, 140f)
    private val ESCALA_GRANDE = (PIE_GRANDE.y - MIRADA.horizonte) / PiezasNapoleon.OJO / ObjetosNapoleon.METRO

    /** La cama de dormir, en la nieve delante del fuego: sus ejes van a lo largo (x, hacia el fuego: los pies) y a lo ancho (z). */
    private val S_CAMA = PiezasNapoleon.sitio(MIRADA, Offset(266f, 261f), 0.6f)

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
        if (!ObjetosNapoleon.esGrande(v.m)) add(RetiradaLuz.Sombra(S_SOPORTE.pie, siluetaCaja(S_SOPORTE, 0.22f, 0.46f)))
        add(RetiradaLuz.Sombra(S_TAMBOR.pie, siluetaCaja(S_TAMBOR, 0.3f, 0.42f)))
        if (!v.vivac) add(RetiradaLuz.Sombra(S_ESPEJO.pie, siluetaCaja(S_ESPEJO, 0.26f, 0.7f)))
        if (EscenaNapoleon.pabellon(v.m)) { val pa = PUNTA_ASTA; add(RetiradaLuz.Sombra(PIE_ASTA, poligono(PIE_ASTA + Offset(-ts, 0f), PIE_ASTA + Offset(ts, 0f), pa + Offset(ts * 2f, 0f), pa + Offset(-ts * 2f, 0f)))) }
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
        if (RetiradaNapoleon.carroza(m)) cosas += parte(S_CARROZA.pie.y) {
            val c = carrozaDe(nieve)
            carroza(c, v.en(S_CARROZA.pie))
            plataDeLuna(v, PiezasNapoleon.siluetaDeCarroza(c), 0f, 150f, 0.16f)
        }
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
        // La mochila de pie con el objeto chico encima, y un capote viejo en el suelo, a la derecha del fuego: los días del
        // objeto grande, ese sitio es suyo.
        if (!ObjetosNapoleon.esGrande(m)) {
            cosas += parte(S_SOPORTE.pie.y) {
                val a = v.en(S_SOPORTE.pie)
                val (_, sobre) = mochila(S_SOPORTE, a, 41L, false, C.CAPOTE, 0f, nieve)
                with(ObjetosNapoleon) { sobreElSoporte(m, v.p.con(a.calor(sobre)), sobre, 1.1f) }
            }
            cosas += parte(258f) { val q = Offset(332f, 259f); capote(v.en(q), q, 30f, 10f, Azar(51L), false, 0f, nieve) }
        }
        // La cama de ramas de abeto con la manta encima, delante del fuego (entre él y el suelo del gato), mientras duermen.
        if (EscenaNapoleon.camas(m)) cosas += parte(S_CAMA.pie.y) { cama(v) }
        // El tambor, tumbado en la esquina de abajo y cortado por el borde.
        cosas += parte(S_TAMBOR.pie.y) { tambor(S_TAMBOR, v.en(S_TAMBOR.pie), true, 0f, nieve) }
        // El asta del águila, plantada en la nieve: está mientras está la compañía (de marcha, va con ella).
        if (EscenaNapoleon.pabellon(m)) cosas += parte(PIE_ASTA.y) {
            val a = v.en(PIE_ASTA)
            mancha(PIE_ASTA + Offset(0f, 0.5f), GRUESO_ASTA * 3f, GRUESO_ASTA * 0.9f, v.p.sombra(C.NIEVE_SOMBRA), 0.7f)
            aguila(a, PIE_ASTA, PUNTA_ASTA, GRUESO_ASTA, EscenaNapoleon.aguilaEnvuelta(m), EscenaNapoleon.luto(m), Azar(57L), 0f, nieve)
            // El labio de nieve que le tapa el regatón: está hundida, no posada.
            if (nieve > 0.3f) mancha(PIE_ASTA + Offset(0f, 0.6f), GRUESO_ASTA * 2.2f, GRUESO_ASTA * 0.8f, a.arriba(C.NIEVE, PIE_ASTA), 0.9f)
        }
        // El objeto grande del día, de pie a la derecha del fuego (pinta él mismo su sombra y su hoyo en la nieve).
        val enElGrande = PIE_GRANDE + Offset(0f, -40f)
        ObjetosNapoleon.grande(m, v.p.con(v.a.calor(enElGrande)), PIE_GRANDE, APOYO_GRANDE, ESCALA_GRANDE)?.let { g -> cosas += parte(g.pie.y) { g.pintar(this) } }
        // Lo abandonado del centro (en la retirada).
        if (!v.vivac) {
            cosas += parte(S_COFRE.pie.y) { cofre(S_COFRE, v.en(S_COFRE.pie), 1f, 0f, nieve) }
            cosas += parte(S_ESPEJO.pie.y) { espejo(S_ESPEJO, v.en(S_ESPEJO.pie), v.l.alto, v.l.bajo, 0f, nieve) }
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

    /**
     * La cama de dormir junto al fuego, tendida en la nieve en perspectiva (en metros, en los ejes de [S_CAMA]; los pies,
     * hacia el fuego): una capa de ramas de abeto cortadas, planas, con el tallo hacia el medio y la punta abierta hacia
     * fuera, unas sobre otras, cada una con su forma de pluma (ancha en medio, aguda en la punta) y sus brotes en espiga;
     * encima, la manta de lana echada hacia los pies, como la deja quien se acaba de levantar: con el embozo vuelto, que
     * enseña el revés, y el canto que cae del lado de quien mira. Las ramas de la cabecera quedan a la vista.
     */
    private fun DrawScope.cama(v: VistaNapoleon) {
        val s = S_CAMA; val a = v.en(s.pie); val p = v.p
        val az = Azar(Semilla.de("napoleon", "cama"))
        fun q(x: Float, z: Float, y: Float = 0f) = s.p(x, y, z)
        // La nieve hundida bajo la cama.
        mancha(q(0f, 0f), 0.75f * s.escala(0f, 0f), 0.2f * s.escala(0f, 0f), p.sombra(C.NIEVE_SOMBRA), 0.5f)
        // ---- Las ramas: doce, en dos capas; las de abajo, largas y abiertas hacia las puntas y los lados.
        for (i in 0 until 12) {
            val arriba = i >= 7
            val haciaLosPies = i % 2 == 1
            val sg = if (haciaLosPies) 1f else -1f
            val bx = sg * az.entre(-0.08f, 0.12f) + (if (arriba) -0.2f else 0f)
            val bz = az.entre(-0.22f, 0.22f) * (if (arriba) 0.6f else 1f)
            val ang = (if (haciaLosPies) 0f else PI.toFloat()) + sg * bz * 1.4f + az.entre(-0.25f, 0.25f)
            val largo = (if (haciaLosPies) az.entre(0.45f, 0.58f) else az.entre(0.42f, 0.52f)) * (if (arriba) 0.75f else 1f)
            val ancho = az.entre(0.13f, 0.17f)
            val y = 0.02f + i * 0.006f
            val dx = cos(ang); val dz = sin(ang)
            fun ex(t: Float) = bx + dx * largo * t
            fun ez(t: Float) = bz + dz * largo * t
            fun w(t: Float) = ancho * sin(PI.toFloat() * (0.1f + 0.9f * t)) * (1f - 0.3f * t)
            val n = 14
            val izq = ArrayList<Offset>(); val der = ArrayList<Offset>()
            for (j in 0..n) {
                val t = j / n.toFloat(); val ww = w(t) * (if (j % 2 == 0) 1f else 0.7f)
                izq += q(ex(t) - dz * ww, ez(t) + dx * ww, y); der += q(ex(t) + dz * ww, ez(t) - dx * ww, y)
            }
            val forma = poligono(izq + der.reversed())
            val en = q(ex(0.5f), ez(0.5f))
            drawPath(forma, a.arriba(lerp(C.PINO_OSC, C.PINO, 0.3f + 0.15f * (i % 3)), en))
            clipPath(forma) {
                // Los brotes, en espiga hacia la punta, alternando el verde de dentro y el claro de las puntas.
                for (j in 1 until n) {
                    val t = j / n.toFloat(); val t2 = (t + 0.1f).coerceAtMost(1f); val ww = w(t2) * 1.05f
                    val col = a.arriba(if (j % 3 == 0) C.PINO_LUZ else lerp(C.PINO, C.PINO_LUZ, 0.35f), en)
                    val c = q(ex(t), ez(t), y)
                    drawLine(col, c, q(ex(t2) - dz * ww, ez(t2) + dx * ww, y), 1f, StrokeCap.Round)
                    drawLine(col, c, q(ex(t2) + dz * ww, ez(t2) - dx * ww, y), 1f, StrokeCap.Round)
                }
            }
            // El tallo, con el corte claro en la base; en la punta, la nieve que traía.
            drawLine(a.arriba(C.MADERA, en), q(ex(0f), ez(0f), y), q(ex(0.8f), ez(0.8f), y), 1f, StrokeCap.Round)
            drawCircle(a.arriba(C.HOJA, en), 0.8f, q(ex(0f), ez(0f), y))
            if (v.nieve > 0.4f) for (j in 0 until 3) {
                val t = az.entre(0.7f, 0.95f); val o = az.entre(-0.6f, 0.6f) * w(t)
                drawCircle(a.arriba(C.NIEVE, en).copy(alpha = 0.8f), az.entre(0.35f, 0.6f), q(ex(t) - dz * o, ez(t) + dx * o, y + 0.01f))
            }
        }
        // ---- La manta, echada hacia los pies.
        val lana = lerp(C.CAPOTE, C.MADERA, 0.18f)
        val dentro = lerp(lana, C.LONA, 0.35f)
        // Más estrecha y más corta que las ramas: asoman sus puntas por los lados y por los pies. La tela no está tiesa:
        // su borde sube donde se apoya en una rama y cae entre dos, y hacia los pies se amontona.
        val xb = -0.08f; val x1 = 0.48f; val zw = 0.24f; val h = 0.08f
        val borde = (0..6).map { j -> val z = -zw + 2f * zw * j / 6f; V3(xb + az.entre(-0.03f, 0.03f) + 0.05f * sin(z * 6f), h + az.entre(-0.005f, 0.02f), z) }
        val lejos = (1..5).map { j -> V3(xb + (x1 - xb) * j / 6f, az.entre(0.03f, 0.1f), zw + az.entre(-0.04f, 0.04f)) }
        val pies = (0..4).map { j -> V3(x1 + az.entre(-0.05f, 0.03f), az.entre(0.03f, 0.1f), zw - 2f * zw * j / 4f) }
        val cerca = (5 downTo 1).map { j -> V3(xb + (x1 - xb) * j / 6f, az.entre(0.015f, 0.09f), -zw - az.entre(-0.03f, 0.04f)) }
        fun en(w: V3) = q(w.x, w.z, w.y)
        // El canto que cae del lado de quien mira, hasta la nieve: alto donde la tela está levantada, nada donde toca.
        val canto = listOf(borde.first()) + cerca.reversed() + listOf(pies.last())
        val nDelante = s.normal(Vec.unidad(V3(0f, 0.2f, -1f)))
        drawPath(suave(canto.map { en(it) } + canto.reversed().map { q(it.x, it.z - 0.035f, 0.012f) }), a.cara(lana, nDelante, q(0.2f, -zw)))
        val manta = suave((borde + lejos + pies + cerca).map { en(it) })
        drawPath(manta, a.arriba(lana, q(0.25f, 0f)))
        clipPath(manta) {
            // Las arrugas: lomos claros y senos oscuros, blandos; y el bulto de los pies, donde se amontona.
            for ((x, z, claro) in listOf(Triple(0.12f, -0.1f, true), Triple(0.22f, 0.04f, false), Triple(0.3f, 0.15f, true), Triple(0.08f, 0.16f, false), Triple(0.34f, -0.06f, false))) {
                val c = q(x, z, h)
                mancha(c, 0.16f * s.escala(x, z), 0.06f * s.escala(x, z), if (claro) a.cara(lana, s.normal(Vec.unidad(V3(0.3f, 1f, -0.4f))), c) else p.sombra(lana), 0.75f)
            }
            val bulto = q(x1 - 0.08f, 0.02f, h + 0.03f)
            mancha(bulto, 0.14f * s.escala(x1, 0f), 0.07f * s.escala(x1, 0f), a.cara(dentro, s.normal(Vec.unidad(V3(-0.2f, 1f, -0.5f))), bulto), 0.8f)
            if (EscenaNapoleon.helada(v.m)) for (j in 0 until 16) drawCircle(a.arriba(C.NIEVE, q(0.3f, 0f)).copy(alpha = 0.55f), 0.5f, q(az.entre(xb, x1), az.entre(-zw, zw), h))
        }
        // El embozo vuelto: una franja del revés, más clara, tendida sobre la manta junto al borde; y la raya oscura del
        // doblez, que es la sombra del borde sobre las ramas.
        val ancho = 0.09f
        val embozo = suave(borde.map { q(it.x, it.z, it.y + 0.012f) } + borde.reversed().map { q(it.x + ancho + 0.02f * sin(it.z * 9f), it.z, it.y + 0.012f) })
        drawPath(embozo, a.arriba(dentro, q(xb, 0f)))
        val pliegue = Path().apply { val b0 = en(borde[0]); moveTo(b0.x, b0.y); for (j in 1 until borde.size) { val b = en(borde[j]); lineTo(b.x, b.y) } }
        drawPath(pliegue, p.sombra(lana).copy(alpha = 0.8f), style = Stroke(1.1f, cap = StrokeCap.Round))
    }

    /**
     * La plata de la luna sobre una cosa grande que queda lejos del fuego (la carroza): un lavado frío y claro sobre su
     * [forma], de [x0] a [x1], más fuerte del lado de la luna, para que de noche se lea fuera del círculo del fuego.
     */
    private fun DrawScope.plataDeLuna(v: VistaNapoleon, forma: Path, x0: Float, x1: Float, fuerza: Float) {
        val k = (fuerza * v.luna).coerceIn(0f, 1f); if (k <= 0.01f) return
        val haciaLaLuna = v.l.lunaX > 0.5f
        val fuerte = RetiradaLuz.LUNA.copy(alpha = k); val flojo = RetiradaLuz.LUNA.copy(alpha = k * 0.3f)
        clipPath(forma) {
            drawRect(Brush.horizontalGradient(if (haciaLaLuna) listOf(flojo, fuerte) else listOf(fuerte, flojo), x0, x1), Offset(x0, 0f), Size(x1 - x0, ALTO), blendMode = BlendMode.Plus)
        }
    }

    /** Un contorno cerrado y blando que pasa cerca de los [puntos] (curvas por los puntos medios, sin esquinas). */
    private fun suave(puntos: List<Offset>): Path = Path().apply {
        val n = puntos.size
        fun medio(i: Int) = (puntos[i % n] + puntos[(i + 1) % n]) / 2f
        val m0 = medio(0); moveTo(m0.x, m0.y)
        for (i in 1..n) {
            val desde = medio(i - 1); val c = puntos[i % n]; val hasta = medio(i)
            val c1 = desde + (c - desde) * (2f / 3f); val c2 = hasta + (c - hasta) * (2f / 3f)
            cubicTo(c1.x, c1.y, c2.x, c2.y, hasta.x, hasta.y)
        }
        close()
    }

    /** El tronco del pino grande, con su corteza, su lado de luz, la nieve pegada del lado del viento y los muñones de las ramas muertas. */
    private fun DrawScope.troncoDelPino(v: VistaNapoleon) {
        val a = v.en(PIE_DEL_PINO); val p = PIE_DEL_PINO
        val tronco = Path().apply { moveTo(p.x - 24f, p.y + 2f); cubicTo(p.x - 18f, p.y - 10f, p.x - 15f, p.y - 60f, p.x - 13f, -10f); lineTo(p.x + 13f, -10f); cubicTo(p.x + 15f, p.y - 60f, p.x + 18f, p.y - 12f, p.x + 26f, p.y + 2f); close() }
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
            // Las brasas que laten entre la leña y la ceniza (la leña y la ceniza ya están en la parte quieta).
            val ab = Azar(Semilla.de("napoleon", "brasas"))
            repeat(11) { i -> val q = FUEGO + Offset(ab.entre(-0.24f, 0.24f) * k, ab.entre(-0.09f, 0.05f) * k); val a = (0.4f + 0.4f * sin(reloj * 1.4f + i * 2.3f)) * (f / 0.3f).coerceAtMost(1f)
                drawCircle(Color(0xFFFF6A22).copy(alpha = a.coerceIn(0f, 1f)), ab.entre(0.01f, 0.022f) * k, q); drawCircle(Color(0xFFFFB050).copy(alpha = (a * 0.1f).coerceIn(0f, 1f)), 0.035f * k, q, blendMode = BlendMode.Plus) }
            // Las llamas: lenguas chicas y en capas (naranja fuera, ámbar, núcleo casi blanco), que se mecen cada una a su
            // ritmo; más altas en medio, donde se cruzan los rayos y las tablas. Con el rescoldo, dos o tres que asoman.
            llamas(FUEGO + Offset(0f, -0.1f * k), k, f, reloj, v.noche)
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
        // De noche, el humo se tiñe por abajo con la luz del fuego.
        if (f > 0.3f && v.noche > 0.3f) mancha(LUZ_DEL_FUEGO + Offset(4f, -0.75f * k), 0.22f * k, 0.35f * k, C.LUMBRE, 0.16f * f * v.noche)
        // El vapor de la marmita.
        if (EscenaNapoleon.marmita(v.m) == Marmita.AL_FUEGO) humo(l, FUEGO + Offset(0f, -0.95f * k), 40f, 6f, reloj, v.m.viento, 0f, 0f, 5, 5, 0.4f)
    }

    /** Una lengua de fuego: la base ancha y redonda, la punta que se mece [vaiven]. */
    private fun lenguaSuave(pie: Offset, ancho: Float, alto: Float, vaiven: Float): Path = Path().apply {
        moveTo(pie.x - ancho, pie.y)
        cubicTo(pie.x - ancho * 0.95f, pie.y - alto * 0.38f, pie.x - ancho * 0.3f + vaiven * 0.5f, pie.y - alto * 0.68f, pie.x + vaiven, pie.y - alto)
        cubicTo(pie.x + ancho * 0.3f + vaiven * 0.5f, pie.y - alto * 0.6f, pie.x + ancho * 0.95f, pie.y - alto * 0.32f, pie.x + ancho, pie.y)
        cubicTo(pie.x + ancho * 0.45f, pie.y + ancho * 0.3f, pie.x - ancho * 0.45f, pie.y + ancho * 0.3f, pie.x - ancho, pie.y)
        close()
    }

    /**
     * Las llamas del fuego del alto, en [centro]: de 2 (el rescoldo) a 9 (encendido) lenguas, más chicas de día, cada
     * una en tres capas con su propio vaivén, y el corazón claro abajo. A partir de la hoguera de Prehistoria y del
     * brasero de Renacimiento (muchas llamas chicas, no tres grandes).
     */
    private fun DrawScope.llamas(centro: Offset, k: Float, f: Float, reloj: Float, noche: Float) {
        val tam = f.coerceAtMost(1f) * (0.55f + 0.45f * noche)
        val az = Azar(Semilla.de("napoleon", "llamas"))
        val n = if (f >= 0.45f) 9 else 3
        // El corazón del fuego, abajo, casi blanco.
        drawOval(Color(0xFFFFD27A).copy(alpha = (0.75f * (f / 0.5f).coerceAtMost(1f)).coerceIn(0f, 1f)), centro - Offset(0.2f * k * tam.coerceAtLeast(0.4f), 0.03f * k), Size(0.4f * k * tam.coerceAtLeast(0.4f), 0.06f * k))
        for (i in 0 until n) {
            val u = (i + 0.5f) / n - 0.5f + az.entre(-0.04f, 0.04f)
            val fase = az.entre(0f, 6.28f)
            val late = 0.78f + 0.22f * sin(reloj * (4.5f + 0.7f * i) + fase)
            val alto = k * 0.32f * (1f - 1.7f * u * u).coerceAtLeast(0.3f) * tam * late * az.entre(0.65f, 1.1f) * (if (f < 0.45f) 0.6f else 1f)
            if (alto < 1f) continue
            val ancho = alto * 0.24f + 0.015f * k
            val vaiven = alto * 0.2f * sin(reloj * 3.7f + fase) + alto * 0.06f * sin(reloj * 11f + fase * 2f)
            val p = centro + Offset(u * (if (f < 0.45f) 0.25f else 0.44f) * k, kotlin.math.abs(u) * 0.05f * k)
            drawPath(lenguaSuave(p, ancho, alto, vaiven), Color(0xFFE0541C).copy(alpha = 0.6f))
            drawPath(lenguaSuave(p + Offset(0f, 0.005f * k), ancho * 0.66f, alto * 0.74f, vaiven * 0.75f), Color(0xFFFF9A30).copy(alpha = 0.85f))
            drawPath(lenguaSuave(p + Offset(0f, 0.008f * k), ancho * 0.34f, alto * 0.45f, vaiven * 0.45f), Color(0xFFFFE9AE).copy(alpha = 0.95f))
        }
    }

    /** Lo mismo que [Pabellon], con la nieve de hoy. */
    private fun Pabellon.copia(nieve: Float) = Pabellon(s, semilla, cuantos, cartucheras, chaco, nieve)
}
