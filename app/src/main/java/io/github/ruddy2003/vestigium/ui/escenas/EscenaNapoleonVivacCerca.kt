package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Fase
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Marmita
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.Pabellon
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.aguila
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.capote
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.cilindro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.hoguera
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.horquillas
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.marmita
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.mochila
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.nieveEncima
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.pabellon
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.tambor
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.MIRADA
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.k
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.w
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

/**
 * La base del vivac: el campamento de la compañía, siempre en su sitio (la indicación del autor: «como la Ruta de la
 * Seda»). Todo en metros, en el espacio de los ojos ([VivacNapoleon.w]), con las piezas de [PiezasNapoleon].
 *
 * - **El marco, a la izquierda**: la tienda de un oficial, de lona, de pared, con sus vientos tensos, sus costuras, su
 *   faldón con la cenefa, la puerta abierta con lo de dentro a la vista (el catre con la manta, la mesa plegable con sus
 *   papeles, el baúl) y su farol colgado del mástil; cortada por el borde de la izquierda, con el remate del mástil que
 *   se sale por arriba. La lona es translúcida: a contraluz el sol la atraviesa y de noche el farol la enciende por
 *   dentro.
 * - **El asta del águila**, plantada a la puerta de la tienda: la vertical que cruza el cielo (regla 6).
 * - **La diagonal grande**: la fila de fusiles en pabellón de la compañía, que sale de abajo a la izquierda y se aleja
 *   hacia el centro, con las mochilas y los capotes enrollados al pie; dos pabellones por rancho ([VivacNapoleon.pabellones]).
 * - **El rancho, a la derecha**: el fuego con la marmita para ocho en sus horquillas, las mochilas de los ocho
 *   alrededor, el tambor de pie, el tonel que hace de mesa (el mirador del gato), las ramas de dormir delante del fuego y,
 *   a la derecha, el tocón de partir leña con el objeto del día encima y el montón de leña con el hacha, cortado por el
 *   borde.
 *
 * Entre unas cosas y otras queda el suelo del gato, libre.
 */
internal object VivacCerca {
    private val C = ColorNapoleon

    // ---------------------------------------------------------------- Dónde está cada cosa

    /** El fuego del rancho (su centro, en el suelo) y de dónde sale su luz. */
    val FUEGO = Offset(292f, 240f)
    val LUZ_DEL_FUEGO = Offset(292f, 224f)

    /** La fila de pabellones: dónde está el primero (en metros) y cuánto se corre a la derecha por metro que se aleja. */
    private const val FILA_X0 = -1.3f
    private const val FILA_Z0 = 6.2f
    const val FILA_RUMBO = 0.16f
    private const val PASO = 2.5f

    /** Hasta dónde llega la fila de la compañía en este cuadro (más allá pasa el camino real). */
    const val FILA_HASTA = 34f

    /** Nuestro rancho enciende su fuego a esta distancia a la derecha de su par de pabellones (de este lado de la fila). */
    const val AL_FUEGO = 2.7f

    /** Los demás ranchos, del otro lado de la fila, a esta distancia: sus fuegos se ven entre los fusiles. */
    const val DETRAS = 2.2f

    /** La x (en metros) de la fila a la profundidad [z]. */
    fun filaEn(z: Float): Float = FILA_X0 + FILA_RUMBO * (z - FILA_Z0)

    /** Los sitios (en metros: x, z) de los pabellones de la fila de la compañía, de cerca a lejos. */
    val FILA: List<Pair<Float, Float>> by lazy {
        val az = VivacNapoleon.fijo("fila"); val lista = ArrayList<Pair<Float, Float>>(); var z = FILA_Z0
        while (z <= FILA_HASTA) { lista += (filaEn(z) + az.entre(-0.12f, 0.12f)) to z; z += PASO * az.entre(0.9f, 1.12f) }
        lista
    }

    /** Los fuegos de los ranchos a lo largo de la fila (en metros: x, z): el primero es el nuestro. */
    val FUEGOS: List<Pair<Float, Float>> by lazy {
        val z0 = VivacNapoleon.zEn(FUEGO.y); val x0 = (FUEGO.x - MIRADA.centro) * z0 / MIRADA.focal
        val lista = ArrayList<Pair<Float, Float>>(); lista += x0 to z0
        var z = 16f; val az = VivacNapoleon.fijo("fuegos")
        while (z <= FILA_HASTA + 1f) { lista += (filaEn(z) - DETRAS + az.entre(-0.3f, 0.3f)) to z; z += 2f * PASO }
        lista
    }

    private val S_FUEGO = PiezasNapoleon.sitio(MIRADA, FUEGO, 0.2f)

    /** La tienda de oficial: su sitio (el centro del suelo), girada para que la puerta mire a la derecha y hacia delante. */
    private const val T_GIRO = 0.05f
    private const val T_ANCHO = 1.3f
    private const val T_LARGO = 1.7f
    private const val T_PARED = 1.55f
    private const val T_CUMBRE = 3.65f
    /** La puerta va un poco a la derecha del centro del hastial (lo que se ve de él). */
    private const val T_PX = 0.3f
    /**
     * El centro del hastial de la puerta, en metros: la tienda mira a quien mira, cortada por el borde de la izquierda
     * por en medio del hastial y por arriba por el mástil; detrás se van su costado y su faldón de la derecha.
     */
    private val T_PUERTA = -2.62f to 9.2f
    val S_TIENDA = PiezasNapoleon.sitio(MIRADA, w(T_PUERTA.first - T_LARGO * sin(T_GIRO), T_PUERTA.second + T_LARGO * cos(T_GIRO)), T_GIRO)

    /** El asta del águila, plantada a la puerta de la tienda. */
    private val PIE_ASTA = w(0.15f, 10.8f)
    private val PUNTA_ASTA: Offset get() = PIE_ASTA + Offset(3f, -3.15f * k(10.8f))
    private val GRUESO_ASTA = 0.04f * k(10.8f)

    /** El tonel puesto de pie, que hace de mesa (y de mirador del gato). */
    private val S_TONEL = PiezasNapoleon.sitio(MIRADA, w(-0.18f, 6.55f), 0.3f)
    private const val TONEL_ALTO = 0.62f

    /** El tambor, de pie, entre la fila y el fuego. */
    private val S_TAMBOR = PiezasNapoleon.sitio(MIRADA, w(0.58f, 7.6f), -0.4f)

    /** El tocón de partir leña, con el objeto chico del día encima, a la derecha del fuego. */
    private val S_TOCON = PiezasNapoleon.sitio(MIRADA, Offset(350f, 243f), 0.3f)
    private const val TOCON_ALTO = 0.42f

    /** La cama de ramas delante del fuego (sus ejes: a lo largo, hacia el fuego; a lo ancho). */
    private val S_CAMA = PiezasNapoleon.sitio(MIRADA, Offset(252f, 263f), 0.55f)

    /** El montón de leña con el hacha, en la esquina de abajo a la derecha, cortado por el borde. */
    private val S_LENA = PiezasNapoleon.sitio(MIRADA, Offset(380f, 293f), -0.3f)

    /** Dónde se apoya el objeto chico del día: en lo alto del tocón. */
    val SOPORTE: Offset get() = S_TOCON.p(0f, TOCON_ALTO, 0f)

    /** Donde pisa el objeto grande del día (esos días no está el tocón) y hacia dónde se recuesta. */
    val PIE_GRANDE = Offset(352f, 252f)
    private val APOYO_GRANDE = Offset(336f, 140f)
    private val ESCALA_GRANDE = (PIE_GRANDE.y - MIRADA.horizonte) / PiezasNapoleon.OJO / ObjetosNapoleon.METRO

    // ---------------------------------------------------------------- El gato

    /** El suelo del gato: delante de todo, del primer pabellón al tocón. */
    const val SUELO_DESDE = 112f
    const val SUELO_HASTA = 326f

    /** Donde se sienta a mirar el objeto del día: el borde de su suelo más cercano a él. */
    const val GATO_MIRA_EL_OBJETO = 320f

    /** Donde se guarece: al pie del tonel. */
    const val ABRIGO = 150f

    /** El sitio alto del gato: la tapa del tonel. */
    val MIRADOR: Offset get() = S_TONEL.p(0f, TONEL_ALTO, 0f)

    // ---------------------------------------------------------------- Las sombras

    /** Lo que tumba sombra con el sol o la luna. */
    fun sombras(v: VistaVivac): List<VivacLuz.Sombra> = buildList {
        val m = v.m
        if (EscenaNapoleon.lugar(m) != EscenaNapoleon.Lugar.PUEBLO) {
            add(VivacLuz.Sombra(S_TIENDA.pie, siluetaDeLaTienda()))
            // Los vientos de la tienda, con sus estacas: rayas finas de sombra en la hierba.
            for (z in listOf(T_LARGO * 0.6f, 0f, -T_LARGO * 0.6f)) {
                val p0 = S_TIENDA.p(V3(T_ANCHO, T_PARED, z)); val p1 = S_TIENDA.p(V3(T_ANCHO + 0.95f, 0f, z * 1.05f)); val d = p1 - p0; val n = Offset(-d.y, d.x) / d.getDistance().coerceAtLeast(0.1f) * 0.5f
                add(VivacLuz.Sombra(p1, poligono(p0 + n, p1 + n, p1 - n, p0 - n), 0.8f))
            }
        }
        if (VivacNapoleon.aguila(m)) { val g = GRUESO_ASTA; add(VivacLuz.Sombra(PIE_ASTA, poligono(PIE_ASTA + Offset(-g, 0f), PIE_ASTA + Offset(g, 0f), PUNTA_ASTA + Offset(g * 2.4f, 0f), PUNTA_ASTA + Offset(-g * 2.4f, 0f)))) }
        for ((i, pz) in filaDeHoy(m).withIndex()) {
            val pb = pabellonDe(i, pz, v.nieve)
            add(VivacLuz.Sombra(pb.s.pie, PiezasNapoleon.siluetaDePabellon(pb) { pb.s.p(it) }))
            // El cuerpo de la sombra del pabellón: la pirámide de los fusiles, más tenue que sus cañones.
            add(VivacLuz.Sombra(pb.s.pie, poligono(envolvente(pb.culatas.map { pb.s.p(it) } + pb.s.p(pb.apice + V3(0f, 0.35f, 0f)))), 0.45f))
            // Y las mochilas de su pie (las de cerca).
            if (pz.second < 14f) for (q in mochilasDe(i, pz)) { val sm = PiezasNapoleon.sitio(MIRADA, q, 0f); add(VivacLuz.Sombra(q, caja(sm, 0.2f, 0.34f))) }
        }
        if (!VivacNapoleon.sinCompania(m)) for (q in listOf(Offset(326f, 229f), Offset(256f, 233f))) add(VivacLuz.Sombra(q, caja(PiezasNapoleon.sitio(MIRADA, q, 0f), 0.2f, 0.34f)))
        add(VivacLuz.Sombra(S_TONEL.pie, caja(S_TONEL, 0.3f, TONEL_ALTO)))
        if (!VivacNapoleon.sinCompania(m)) add(VivacLuz.Sombra(S_TAMBOR.pie, caja(S_TAMBOR, 0.22f, 0.42f)))
        if (!ObjetosNapoleon.esGrande(m)) add(VivacLuz.Sombra(S_TOCON.pie, caja(S_TOCON, 0.26f, TOCON_ALTO + 0.12f)))
        add(VivacLuz.Sombra(FUEGO, caja(S_FUEGO, 0.55f, if (EscenaNapoleon.marmita(m) == Marmita.AL_FUEGO) 0.95f else 0.8f)))
        add(VivacLuz.Sombra(S_LENA.pie, caja(S_LENA, 0.7f, 0.45f)))
    }

    /** Lo que tumba sombra con la luz del fuego, en abanico: lo que está cerca de él (la tienda no: queda lejos). */
    fun sombrasDelFuego(v: VistaVivac): List<VivacLuz.Sombra> = sombras(v).filter { (it.pie - FUEGO).getDistance() in 18f..230f && it.pie != S_TIENDA.pie }

    private fun caja(s: Sitio, ancho: Float, alto: Float): Path = poligono(listOf(s.p(-ancho, 0f, 0f), s.p(ancho, 0f, 0f), s.p(ancho * 0.85f, alto, 0f), s.p(-ancho * 0.85f, alto, 0f)))

    /** La silueta de la tienda (para su sombra): los dos hastiales y la cumbrera. */
    fun siluetaDeLaTienda(): Path {
        val s = S_TIENDA; val w = T_ANCHO; val d = T_LARGO
        val pts = listOf(V3(-w, 0f, -d), V3(w, 0f, -d), V3(w, T_PARED, -d), V3(0f, T_CUMBRE, -d), V3(-w, T_PARED, -d), V3(-w, 0f, d), V3(w, 0f, d), V3(w, T_PARED, d), V3(0f, T_CUMBRE, d), V3(-w, T_PARED, d)).map { s.p(it) }
        return poligono(envolvente(pts))
    }

    // ---------------------------------------------------------------- La fila de pabellones

    /** Los pabellones de la fila que se ven hoy (los primeros [VivacNapoleon.pabellones]). */
    fun filaDeHoy(m: Momento): List<Pair<Float, Float>> = FILA.take(VivacNapoleon.pabellones(m).coerceAtMost(FILA.size))

    /** Dónde están las mochilas al pie del pabellón [i] (en el lienzo): del lado del fuego las dos primeras, del otro las demás. */
    private fun mochilasDe(i: Int, pz: Pair<Float, Float>): List<Offset> {
        val az = VivacNapoleon.fijo("mochilas", i)
        val lado = if (i < 2) 1f else -1f
        val n = if (pz.second < 14f) 2 else 1
        return (0 until n).map { j -> w(pz.first + lado * (0.9f + j * 0.5f) + az.entre(-0.08f, 0.08f), pz.second + 0.3f + az.entre(-0.35f, 0.35f) + j * 0.4f) }
    }

    private fun pabellonDe(i: Int, pz: Pair<Float, Float>, nieve: Float): Pabellon {
        val az = VivacNapoleon.fijo("pabellon", i)
        val s = PiezasNapoleon.sitio(MIRADA, w(pz.first, pz.second), az.entre(-0.6f, 0.6f))
        return Pabellon(s, Semilla.de("napoleon", "vivac", "pabellon", i), if (az.real() < 0.6f) 4 else 3, if (i % 3 == 2) 1 else 2, i % 2 == 0, nieve)
    }

    // ---------------------------------------------------------------- La base

    fun DrawScope.base(v: VistaVivac) {
        val m = v.m
        sueloCercano(v)
        val cosas = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        if (EscenaNapoleon.lugar(m) == EscenaNapoleon.Lugar.PUEBLO) cosas += parte(-1f) { with(VivacParra) { parra(v) } }
        else cosas += parte(S_TIENDA.pie.y) { tienda(v) }
        // La fila de pabellones, con las mochilas al pie (del lado del fuego) y los capotes enrollados.
        for ((i, pz) in filaDeHoy(m).withIndex()) {
            val pb = pabellonDe(i, pz, v.nieve)
            cosas += parte(pb.s.pie.y) {
                val lejos = VivacNapoleon.lejos(pz.second)
                pabellon(pb, v.en(pb.s.pie), lejos)
                ultimaLuz(v, pb)
            }
            // Lo de los cuatro al pie del pabellón, cada uno a su modo: mochilas de pie o echadas, con la manta o el capote
            // enrollado encima (de colores distintos), y algún capote tirado.
            val az = VivacNapoleon.fijo("mochilas", i, "estilo")
            for ((j, q) in mochilasDe(i, pz).withIndex()) {
                val s = PiezasNapoleon.sitio(MIRADA, q, az.entre(-0.9f, 0.9f) + 0.4f)
                val tumbada = az.real() < 0.5f
                val rollo = listOf(C.CAPOTE, lerp(C.CAPOTE, C.LONA, 0.3f), lerp(C.CAPOTE, C.MADERA, 0.35f), lerp(C.AZUL, C.CAPOTE, 0.55f))[az.entero(4)]
                val sem = az.entero(1000).toLong(); val capote = j == 1 && i % 3 == 1
                val lejos = VivacNapoleon.lejos(pz.second)
                cosas += parte(q.y) {
                    if (capote) { val kk = k(VivacNapoleon.zEn(q.y)); capote(v.en(q), q, 1.1f * kk, 0.3f * kk, Azar(sem), false, lejos, v.nieve, rollo) }
                    else mochila(s, v.en(q), sem, tumbada, rollo, lejos, v.nieve)
                }
            }
        }
        // El asta del águila.
        if (VivacNapoleon.aguila(m)) cosas += parte(PIE_ASTA.y) {
            val a = v.en(PIE_ASTA)
            mancha(PIE_ASTA + Offset(0f, 0.5f), GRUESO_ASTA * 3f, GRUESO_ASTA, v.p.sombra(lerp(C.BARRO, C.HOJA, 0.3f)), 0.6f)
            aguila(a, PIE_ASTA, PUNTA_ASTA, GRUESO_ASTA, false, EscenaNapoleon.luto(m), Azar(57L), 0f, v.nieve)
            if (EscenaNapoleon.fiesta(m)) { bandera(v); corona(v, PUNTA_ASTA + Offset(0f, 2.4f * GRUESO_ASTA)) }
        }
        // El tonel de pie, con lo que hay encima.
        cosas += parte(S_TONEL.pie.y) { tonel(v) }
        // El fuego del rancho, con las horquillas y la marmita.
        cosas += parte(FUEGO.y) {
            val a = v.en(FUEGO)
            hoguera(S_FUEGO, a, v.fuego, v.nieve, Semilla.de("napoleon", "vivac", "hoguera"))
            when (EscenaNapoleon.marmita(m)) {
                Marmita.AL_FUEGO -> { val vara = horquillas(S_FUEGO, a); marmita(S_FUEGO, a, vara + V3(0f, -0.48f, 0f), hirviendo = v.fuego > 0.6f) }
                Marmita.AL_LADO -> { horquillas(S_FUEGO, a); marmita(S_FUEGO, a, V3(-0.95f, 0f, -0.35f), tapaEnElSuelo = true) }
                Marmita.NINGUNA -> { horquillas(S_FUEGO, a); if (!VivacNapoleon.sinCompania(m)) marmita(S_FUEGO, a, V3(-0.95f, 0f, -0.35f), tapaEnElSuelo = true) }
            }
        }
        // Las mochilas del rancho alrededor del fuego (de asiento y, de noche, de almohada): mientras está la compañía.
        if (!VivacNapoleon.sinCompania(m)) {
            for ((j, sitio) in listOf(Offset(326f, 229f) to 1.2f, Offset(256f, 233f) to -0.6f).withIndex()) {
                val s = PiezasNapoleon.sitio(MIRADA, sitio.first, sitio.second)
                cosas += parte(sitio.first.y) { mochila(s, v.en(sitio.first), 300L + j, j == 0, C.CAPOTE, 0f, v.nieve) }
            }
            cosas += parte(S_TAMBOR.pie.y) { tambor(S_TAMBOR, v.en(S_TAMBOR.pie), false, 0f, v.nieve) }
        }
        // Las ramas de dormir delante del fuego; de madrugada, con la manta echada.
        cosas += parte(S_CAMA.pie.y) { cama(v) }
        // El tocón con el objeto chico del día (los días del grande, el grande va en su sitio).
        if (!ObjetosNapoleon.esGrande(m)) cosas += parte(S_TOCON.pie.y) {
            val a = v.en(S_TOCON.pie)
            tocon(S_TOCON, a, v)
            with(ObjetosNapoleon) { sobreElSoporte(m, v.p.con(a.calor(SOPORTE)), SOPORTE, 1.1f) }
        }
        val enElGrande = PIE_GRANDE + Offset(0f, -40f)
        ObjetosNapoleon.grande(m, v.p.con(v.a.calor(enElGrande)), PIE_GRANDE, APOYO_GRANDE, ESCALA_GRANDE)?.let { g -> cosas += parte(g.pie.y) { g.pintar(this) } }
        // El montón de leña con el hacha, cortado por el borde.
        cosas += parte(S_LENA.pie.y) { lena(v) }
        // Un capote tendido en la esquina de abajo, a la izquierda, cortado por el borde (de noche, la cama de otro).
        cosas += parte(ALTO + 1f) {
            val q = Offset(22f, 282f)
            capote(v.en(q), q, 64f, 20f, Azar(71L), v.fase == Fase.SUENO, 0f, v.nieve)
        }
        for ((_, pintar) in cosas.sortedBy { it.first }) pintar()
        // Al atardecer el latón brilla rojo, como los cañones de los fusiles: el tambor, el águila, la perilla del mástil.
        val tarde = VivacLuz.ultimaLuz(v.l)
        if (tarde > 0.05f) {
            val lado = if (v.p.solALaDerecha) 1f else -1f; val rojo = Color(0xFFFF8A40)
            val brillos = buildList {
                if (!VivacNapoleon.sinCompania(m)) add(S_TAMBOR.p(V3(0.12f * lado, 0.24f, -0.1f)) to 0.16f * S_TAMBOR.k)
                if (VivacNapoleon.aguila(m)) add(PUNTA_ASTA + Offset(0f, 1.5f * GRUESO_ASTA) to 3.5f * GRUESO_ASTA)
                if (EscenaNapoleon.lugar(m) != EscenaNapoleon.Lugar.PUEBLO) add(S_TIENDA.p(V3(0f, T_CUMBRE + 0.45f, -T_LARGO - 0.02f)) to 0.1f * S_TIENDA.k)
            }
            for ((q, r) in brillos) drawCircle(Brush.radialGradient(listOf(rojo.copy(alpha = (0.55f * tarde).coerceIn(0f, 1f)), Color.Transparent), q, r), r, q, blendMode = BlendMode.Plus)
        }
    }

    /**
     * «La última luz del día se queda en los cañones de los fusiles puestos en pabellón, que brillan rojos un momento»:
     * con el sol bajo de la tarde, un filo rojizo en los cañones y las bayonetas, del lado del sol.
     */
    private fun DrawScope.ultimaLuz(v: VistaVivac, pb: Pabellon) {
        val tarde = VivacLuz.ultimaLuz(v.l); if (tarde <= 0.02f) return
        val s = pb.s; val lado = if (v.p.solALaDerecha) 1f else -1f
        val c = lerp(Color(0xFFFF7A3A), Color(0xFFFFC27A), 0.25f).copy(alpha = (0.95f * tarde).coerceIn(0f, 1f))
        for ((i, cu) in pb.culatas.withIndex()) {
            val b = pb.bocas[i]; val e = Vec.unidad(b - cu); val t = b + e * 0.46f
            val p0 = s.p(cu + (b - cu) * 0.45f); val p1 = s.p(t)
            val n = Offset(lado * 0.04f * s.k * 0.5f, 0f)
            drawLine(c, p0 + n, p1 + n, (0.02f * s.k).coerceIn(0.45f, 1.8f), StrokeCap.Round, blendMode = BlendMode.Plus)
            drawCircle(c.copy(alpha = (0.6f * tarde).coerceIn(0f, 1f)), (0.03f * s.k).coerceIn(0.6f, 2.4f), p1, blendMode = BlendMode.Plus)
        }
    }

    // ---------------------------------------------------------------- El suelo de cerca

    /** La hierba aplastada del campamento, la paja, lo pisado alrededor del fuego y, de marcha, las huellas de las culatas. */
    private fun DrawScope.sueloCercano(v: VistaVivac) {
        val p = v.p; val az = VivacNapoleon.fijo("suelo", "cerca")
        val nevado = v.nieve > 0.5f
        val pisado = if (nevado) p.arriba(lerp(C.NIEVE_SUCIA, C.NIEVE_SOMBRA, 0.3f)) else lerp(v.suelo(0f), mult(v.suelo(0f), lerp(C.BARRO, C.HOJA, 0.5f), 0.45f), 0.55f)
        val cuanto = if (nevado) 0.45f else 0.5f
        // Lo pisado alrededor del rancho y a lo largo de la fila: tierra y hierba aplastada (o nieve hollada), en manchas
        // de borde roto que siguen el paso de la gente, no un óvalo.
        val azp = Azar(61L)
        repeat(7) { val q = Offset(azp.entre(236f, 336f), azp.entre(242f, 266f)); drawPath(TrazosValle.borron(q, azp.entre(16f, 30f), azp.entre(4f, 8f), azp, 10, 0.3f, 0.7f), pisado.copy(alpha = cuanto * 0.7f)) }
        val franja = (0..8).map { i -> val z = 6f + i * 3.6f; w(filaEn(z) + 1.1f, z) }
        for ((i, q) in franja.withIndex()) {
            val kk = k(VivacNapoleon.zEn(q.y)); val ab = Azar(80L + i)
            drawPath(TrazosValle.borron(q, 1.5f * kk, 0.35f * kk, ab, 10, 0.32f, 0.7f), pisado.copy(alpha = cuanto * 0.6f))
        }
        // La paja y las briznas sueltas, más gruesas cerca.
        val paja = lerp(C.HOJA, C.LONA, 0.35f)
        repeat(90) {
            val q = Offset(az.entre(0f, ANCHO), az.entre(200f, ALTO)); val kk = k(VivacNapoleon.zEn(q.y))
            val ang = az.entre(-0.6f, 0.6f); val l = az.entre(0.08f, 0.22f) * kk
            drawLine(p.arriba(if (az.real() < 0.5f) paja else lerp(paja, C.HOJA, 0.6f)).copy(alpha = 0.75f), q, q + Offset(cos(ang) * l, sin(ang) * l * 0.3f), (0.012f * kk).coerceIn(0.3f, 1.1f), StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- La tienda de oficial

    /**
     * La tienda de un oficial (el soldado duerme al raso; «las tiendas de los oficiales», dice el cuento): de pared, de
     * lona cruda, con la puerta en el hastial. Medidas **[Aproximación]**: 2.6 m de ancho, 3.4 de largo, 1.55 de pared y
     * 3.65 de cumbrera. Lo de dentro y la cenefa azul con su ribete rojo son **[Diseño propio]**.
     */
    private fun DrawScope.tienda(v: VistaVivac) {
        val s = S_TIENDA; val a = v.en(s.pie); val w = T_ANCHO; val d = T_LARGO; val pared = T_PARED; val cumbre = T_CUMBRE
        val az = VivacNapoleon.fijo("tienda"); val lona = lerp(C.LONA, C.HOJA, 0.12f)
        val farol = VivacNapoleon.farol(v.m)
        // Las caras: los dos costados, los dos hastiales y los dos faldones del techo.
        val caras = listOf(
            listOf(V3(-w, 0f, -d), V3(w, 0f, -d), V3(w, pared, -d), V3(0f, cumbre, -d), V3(-w, pared, -d)) to V3(0f, 0f, -1f),
            listOf(V3(w, 0f, d), V3(-w, 0f, d), V3(-w, pared, d), V3(0f, cumbre, d), V3(w, pared, d)) to V3(0f, 0f, 1f),
            listOf(V3(w, 0f, -d), V3(w, 0f, d), V3(w, pared, d), V3(w, pared, -d)) to V3(1f, 0f, 0f),
            listOf(V3(-w, 0f, d), V3(-w, 0f, -d), V3(-w, pared, -d), V3(-w, pared, d)) to V3(-1f, 0f, 0f),
            listOf(V3(w, pared, -d), V3(w, pared, d), V3(0f, cumbre, d), V3(0f, cumbre, -d)) to Vec.unidad(V3(cumbre - pared, w, 0f)),
            listOf(V3(-w, pared, d), V3(-w, pared, -d), V3(0f, cumbre, -d), V3(0f, cumbre, d)) to Vec.unidad(V3(-(cumbre - pared), w, 0f)),
        ).sortedByDescending { (q, _) -> s.hondo(q.reduce { acc, x -> acc + x } * (1f / q.size)) }
        for ((q, n) in caras) {
            val f = cara(s, a, q, n, lona) ?: continue
            val qq = q.map { s.p(it) }; val c = centroDe(qq)
            val col = a.cara(lona, s.normal(n), c); val nn = s.normal(n)
            val techo = abs(n.y) > 0.5f; val hastial = q.size == 5; val derecha = n.x > 0.5f
            clipPath(f) {
                // Los paños: tiras de lona de tonos un poco distintos (unas más viejas, otras más nuevas), de abajo arriba en
                // las paredes y a lo largo en el techo.
                for (j in 0 until 6) {
                    val tono = when ((j + az.entero(3)) % 3) { 0 -> continue; 1 -> lerp(lona, C.HOJA, 0.2f); else -> lerp(lona, C.NIEVE, 0.32f) }
                    val u0 = j / 6f; val u1 = (j + 1) / 6f
                    val tira = if (!techo) { val a0 = q[0] + (q[1] - q[0]) * u0; val a1 = q[0] + (q[1] - q[0]) * u1; listOf(a0, a1, a1 + V3(0f, cumbre, 0f), a0 + V3(0f, cumbre, 0f)) }
                    else listOf(q[0] + (q[3] - q[0]) * u0, q[1] + (q[2] - q[1]) * u0, q[1] + (q[2] - q[1]) * u1, q[0] + (q[3] - q[0]) * u1)
                    drawPath(poligono(tira.map { s.p(it) }), a.cara(tono, nn, c).copy(alpha = 0.6f))
                }
                // Los remiendos: un paño nuevo, más blanco, y uno viejo, gris, cosidos con su pespunte (en el hastial, lejos
                // de la puerta).
                if (!techo) for (j in 0 until 2) {
                    val u = if (hastial) az.entre(0.05f, 0.3f) else az.entre(0.12f, 0.75f); val h0 = az.entre(0.45f, 1.05f)
                    val ancho = if (j == 0) 0.46f else 0.32f; val alto = if (j == 0) 0.4f else 0.3f
                    val a0 = q[0] + (q[1] - q[0]) * u; val paso = Vec.unidad(q[1] - q[0]) * ancho
                    val r = listOf(a0 + V3(0f, h0, 0f), a0 + paso + V3(0f, h0 + 0.02f, 0f), a0 + paso + V3(0f, h0 + alto, 0f), a0 + V3(0f, h0 + alto - 0.02f, 0f)).map { s.p(it) }
                    val parche = poligono(r); val pc = a.cara(if (j == 0) lerp(lona, C.NIEVE, 0.45f) else lerp(lona, C.CENIZA, 0.22f), nn, centroDe(r))
                    drawPath(parche, pc)
                    drawPath(parche, mult(pc, C.SOMBRA, 0.45f).copy(alpha = 0.7f), style = Stroke(0.4f))
                    drawPath(parche, lerp(pc, Color.White, 0.25f).copy(alpha = 0.8f), style = Stroke((0.012f * s.k).coerceAtLeast(0.3f), pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.035f * s.k, 0.025f * s.k))))
                }
                // El uso: los chorreones de la lluvia bajo el alero y la cumbre, el hollín del rancho arriba (del lado del
                // fuego), la línea que deja el agua de los charcos y el barro salpicado abajo.
                val sucio = a.cara(lerp(lona, C.BARRO, 0.5f), nn, c)
                if (!techo) {
                    repeat(if (hastial) 11 else 7) {
                        val u = az.entre(0.03f, 0.97f); val b = q[0] + (q[1] - q[0]) * u
                        val tope = if (hastial) pared + (cumbre - pared) * (1f - abs(b.x) / w) else pared
                        val p0 = s.p(b + V3(0f, tope - 0.03f, 0f)); val p1 = s.p(b + V3(0f, tope - az.entre(0.35f, 1.15f), 0f))
                        drawLine(Brush.linearGradient(listOf(sucio.copy(alpha = 0.42f), Color.Transparent), p0, p1), p0, p1, az.entre(0.05f, 0.13f) * s.k, StrokeCap.Round)
                    }
                    val linea = Path()
                    for (i in 0..18) { val u = i / 18f; val b = q[0] + (q[1] - q[0]) * u; val pt = s.p(b + V3(0f, 0.34f + 0.06f * sin(u * 23f + 1.3f) + 0.03f * sin(u * 61f), 0f)); if (i == 0) linea.moveTo(pt.x, pt.y) else linea.lineTo(pt.x, pt.y) }
                    drawPath(linea, sucio.copy(alpha = 0.4f), style = Stroke(0.03f * s.k))
                    val bajo = qq.maxOf { it.y }
                    drawRect(Brush.verticalGradient(listOf(Color.Transparent, a.cara(lerp(lona, C.BARRO, 0.6f), nn, c).copy(alpha = 0.8f)), bajo - 0.32f * s.k, bajo), Offset(qq.minOf { it.x }, bajo - 0.32f * s.k), Size(qq.maxOf { it.x } - qq.minOf { it.x }, 0.32f * s.k))
                }
                val hollin = a.cara(lerp(lona, C.CENIZA, 0.55f), nn, c)
                if (hastial) mancha(s.p(V3(0.3f, cumbre - 0.75f, q[0].z)), 0.75f * s.k, 0.55f * s.k, hollin, 0.34f)
                if (derecha) mancha(s.p(V3(w, pared - 0.25f, -d * 0.45f)), 1.1f * s.k, 0.3f * s.k, hollin, 0.3f)
                if (techo && n.x > 0f) mancha(s.p(V3(w * 0.25f, cumbre - 0.3f, -d * 0.3f)), 1.4f * s.k, 0.35f * s.k, hollin, 0.3f)
                // La lona a contraluz: el sol que la atraviesa desde el otro lado la enciende, y se ven en sombra lo de
                // dentro que toca la pared y las varas.
                val bajo = ((0.95f - v.l.solAltura) / 0.75f).coerceIn(0f, 1f)
                val contraluz = maxOf((-v.sol.t(nn)).coerceIn(0f, 1f) * (v.r.fuerza / 0.72f), if (hastial) 0.8f * bajo * v.l.solFuerza * v.r.dia else 0f) * (1f - 0.8f * v.cubierto)
                if (contraluz > 0.02f) drawPath(f, Brush.verticalGradient(listOf(lerp(v.l.solColor, Color(0xFFFFE2A8), 0.5f).copy(alpha = (0.36f * contraluz).coerceIn(0f, 1f)), lerp(v.l.solColor, Color(0xFFFFE2A8), 0.5f).copy(alpha = (0.16f * contraluz).coerceIn(0f, 1f))), qq.minOf { it.y }, qq.maxOf { it.y }), blendMode = BlendMode.Plus)
                // De noche, el farol de dentro la enciende como una linterna de papel: la lona se apaga con la noche y,
                // alrededor del farol, se pone naranja; lo de dentro tumba en ella sus sombras, más grandes.
                if (farol > 0.02f) {
                    val foco = when {
                        hastial -> s.p(V3(FAROL.x, FAROL.y - 0.35f, q[0].z))
                        techo -> s.p(V3(w * 0.5f * sign(n.x), (pared + cumbre) / 2f, FAROL.z))
                        else -> s.p(V3(q[0].x, FAROL.y - 0.45f, FAROL.z))
                    }
                    val radio = (if (techo) 1.5f else if (hastial) 2.1f else 2.6f) * s.k
                    drawPath(f, lerp(Color.White, Color(0xFF6A5C8E), (if (techo) 0.66f else 0.55f) * farol * v.noche), blendMode = BlendMode.Multiply)
                    drawPath(f, Brush.radialGradient(listOf(Color(0xFFF2A050).copy(alpha = ((if (techo) 0.3f else 0.72f) * farol).coerceIn(0f, 1f)), Color(0xFFC8642E).copy(alpha = ((if (techo) 0.1f else 0.3f) * farol).coerceIn(0f, 1f)), Color.Transparent), foco, radio), blendMode = BlendMode.Plus)
                }
                if (hastial && q[0].z < 0f) {
                    val sombraDentro = mult(col, C.SOMBRA, 0.6f)
                    if (contraluz > 0.05f) for (o in JUNTO_AL_HASTIAL) {
                        val forma = poligono(o.map { s.p(V3(it.x, it.y, -d)) })
                        drawPath(forma, sombraDentro.copy(alpha = (0.25f * contraluz).coerceIn(0f, 1f)), style = Stroke(1.6f)); drawPath(forma, sombraDentro.copy(alpha = (0.6f * contraluz).coerceIn(0f, 1f)))
                    }
                    if (farol > 0.05f) for (o in JUNTO_AL_HASTIAL) {
                        val forma = poligono(o.map { val k = (-d - FAROL.z) / (it.z - FAROL.z); s.p(FAROL + (it - FAROL) * k) })
                        drawPath(forma, C.CENIZA.copy(alpha = (0.25f * farol).coerceIn(0f, 1f)), style = Stroke(2.2f)); drawPath(forma, C.CENIZA.copy(alpha = (0.5f * farol).coerceIn(0f, 1f)))
                    }
                }
                if (derecha) {
                    val sombraDentro = mult(col, C.SOMBRA, 0.6f)
                    if (contraluz > 0.05f) for (o in JUNTO_A_LA_PARED) {
                        val forma = poligono(o.map { s.p(V3(w, it.y, it.z)) })
                        drawPath(forma, sombraDentro.copy(alpha = (0.2f * contraluz).coerceIn(0f, 1f)), style = Stroke(1.6f)); drawPath(forma, sombraDentro.copy(alpha = (0.42f * contraluz).coerceIn(0f, 1f)))
                    }
                    if (farol > 0.05f) for (o in JUNTO_A_LA_PARED) {
                        val forma = poligono(o.map { val k = (w - FAROL.x) / (it.x - FAROL.x); s.p(FAROL + (it - FAROL) * k) })
                        drawPath(forma, C.CENIZA.copy(alpha = (0.25f * farol).coerceIn(0f, 1f)), style = Stroke(2.2f)); drawPath(forma, C.CENIZA.copy(alpha = (0.5f * farol).coerceIn(0f, 1f)))
                    }
                }
                // Los paños cosidos: costuras oscuras con el pespunte claro al lado (de noche, contra la luz, oscuras del todo).
                val costura = mult(col, C.SOMBRA, 0.36f + 0.3f * farol).copy(alpha = 0.85f)
                val pespunte = lerp(col, Color.White, 0.22f).copy(alpha = 0.5f * (1f - farol))
                for (j in 1..5) {
                    val u = j / 6f
                    val (b0, b1) = if (!techo) { val a0 = q[0] + (q[1] - q[0]) * u; s.p(a0) to s.p(a0 + V3(0f, cumbre, 0f)) } else s.p(q[0] + (q[3] - q[0]) * u) to s.p(q[1] + (q[2] - q[1]) * u)
                    drawLine(costura, b0, b1, (0.014f * s.k).coerceIn(0.45f, 1.1f))
                    drawLine(pespunte, b0 + Offset(0.025f * s.k, 0f), b1 + Offset(0.025f * s.k, 0f), 0.35f)
                }
                // Las varas de dentro, que se ven a través de la lona encendida (de noche o a contraluz).
                val trasluz = maxOf(farol, contraluz * 0.6f)
                if (trasluz > 0.05f && !techo) {
                    val vara = mult(col, C.MADERA_OSC, 0.65f).copy(alpha = (0.65f * trasluz).coerceIn(0f, 1f))
                    if (hastial) drawLine(vara, s.p(V3(0f, 0f, q[0].z * 0.98f)), s.p(V3(0f, cumbre, q[0].z * 0.98f)), 0.08f * s.k)
                    else { drawLine(vara, s.p(V3(q[0].x, cumbre - 0.05f, -d)), s.p(V3(q[0].x, cumbre - 0.05f, d)), 0.06f * s.k); drawLine(vara, s.p(V3(q[0].x, 0f, 0f)), s.p(V3(q[0].x, pared, 0f)), 0.05f * s.k) }
                }
                // Los pliegues de la lona tensa: del remate a las estacas, una arruga oscura con su filo de luz al lado.
                if (hastial) {
                    val apice = qq[3]
                    for (u in listOf(0.18f, 0.5f, 0.82f)) {
                        val b = qq[0] + (qq[1] - qq[0]) * u
                        val m1 = apice + (b - apice) * 0.15f; val m2 = apice + (b - apice) * 0.7f
                        drawLine(mult(col, C.SOMBRA, 0.35f).copy(alpha = 0.4f), m1, m2, 0.05f * s.k, StrokeCap.Round)
                        drawLine(lerp(col, Color.White, 0.18f).copy(alpha = 0.35f * (1f - farol)), m1 + Offset(0.04f * s.k, 0f), m2 + Offset(0.04f * s.k, 0f), 0.025f * s.k, StrokeCap.Round)
                    }
                }
            }
            drawPath(f, mult(col, C.SOMBRA, 0.3f).copy(alpha = 0.7f), style = Stroke(0.6f))
        }
        // La cenefa: una tira festoneada bajo el alero de los costados, azul con ribete rojo.
        for (x in listOf(-w, w)) {
            val nn = V3(if (x > 0f) 1f else -1f, 0f, 0f)
            if (!s.seVe(V3(x, pared, 0f), nn)) continue
            val pts = ArrayList<Offset>()
            val pasos = 12
            for (i in 0..pasos) { val z = -d + 2f * d * i / pasos; pts += s.p(V3(x * 1.01f, pared, z)) }
            for (i in pasos downTo 0) { val z = -d + 2f * d * i / pasos; pts += s.p(V3(x * 1.01f, pared - (if (i % 2 == 0) 0.2f else 0.12f), z)) }
            val f = poligono(pts)
            drawPath(f, a.cara(C.AZUL, s.normal(nn), centroDe(pts)))
            drawPath(f, a.cara(C.ROJO, s.normal(nn), centroDe(pts)), style = Stroke(0.03f * s.k))
        }
        // La puerta del hastial de delante, abierta, con los faldones recogidos y atados, y lo de dentro.
        if (s.seVe(V3(0f, 1f, -d), V3(0f, 0f, -1f))) puerta(v, a, farol)
        // Los vientos del costado: tensos desde el alero hasta sus estacas, con el tensor de madera.
        for (z in listOf(d * 0.6f, 0f, -d * 0.6f)) viento(a, s.p(V3(w, pared, z)), V3(w + 0.95f, 0f, z * 1.05f))
        // El mástil de delante asoma por arriba con su perilla de latón; la cuerda de la cumbrera baja hasta su estaca.
        val m0 = s.p(V3(0f, cumbre, -d - 0.02f)); val m1 = s.p(V3(0f, cumbre + 0.45f, -d - 0.02f))
        drawLine(a.plano(C.MADERA, m0, 0f), m0, m1, (0.05f * s.k).coerceIn(0.5f, 4f), StrokeCap.Round)
        drawCircle(a.plano(C.LATON, m1, 0.5f), (0.06f * s.k).coerceAtLeast(0.5f), m1)
        viento(a, s.p(V3(0f, cumbre + 0.3f, -d)), V3(0.15f, 0f, -d - 1.5f))
        if (v.nieve > 0.15f) {
            val borde = listOf(s.p(V3(-w, pared, -d)), s.p(V3(0f, cumbre, -d)), s.p(V3(w, pared, -d))).sortedBy { it.x }
            nieveEncima(a, borde, (0.07f * s.k * v.nieve).coerceAtMost(6f), az)
        }
    }

    /** Un viento de la tienda: la cuerda tensa desde [desde] hasta la estaca clavada en [estaca] (ejes de la tienda), con su tensor. */
    private fun DrawScope.viento(a: Alumbre, desde: Offset, estaca: V3) {
        val s = S_TIENDA; val p1 = s.p(estaca); val ke = s.escala(estaca.x, estaca.z)
        val cuerda = a.plano(lerp(C.LONA, C.MADERA, 0.4f), p1, 0.6f)
        drawLine(mult(cuerda, C.SOMBRA, 0.4f), desde + Offset(0f, 0.4f), p1 + Offset(0f, 0.4f), (0.016f * ke).coerceIn(0.5f, 1.3f))
        drawLine(cuerda, desde, p1, (0.014f * ke).coerceIn(0.45f, 1.1f))
        // El tensor: una tablilla atravesada por la cuerda, cerca de la estaca.
        val t = desde + (p1 - desde) * 0.8f; val e = (p1 - desde) / (p1 - desde).getDistance().coerceAtLeast(1f); val nrm = Offset(-e.y, e.x)
        drawLine(a.plano(C.MADERA, t, 0.5f), t - nrm * (0.05f * ke) - e * (0.02f * ke), t + nrm * (0.05f * ke) + e * (0.02f * ke), (0.035f * ke).coerceIn(0.6f, 2f), StrokeCap.Round)
        // La estaca, clavada inclinada hacia fuera, con su lado de luz y la muesca de la cuerda.
        val cabeza = p1 + Offset(0.07f * ke, -0.24f * ke)
        drawLine(a.plano(C.MADERA_OSC, p1, -0.5f), p1 + Offset(-0.01f * ke, 0.03f * ke), cabeza, (0.05f * ke).coerceIn(0.8f, 2.6f), StrokeCap.Round)
        drawLine(a.plano(C.MADERA, p1, 0.8f), p1 + Offset(0.006f * ke, 0.01f * ke), cabeza + Offset(0.012f * ke, 0f), (0.02f * ke).coerceIn(0.4f, 1.2f), StrokeCap.Round)
    }

    /**
     * Lo de dentro junto al hastial de la puerta (en los ejes de la tienda), que a contraluz (el sol bajo detrás de la
     * tienda) y de noche (con el farol detrás) se ve en sombra en la lona **[Diseño propio]**: el sable colgado del mástil
     * (que también se ve por la puerta), el morral y el bicornio en sus clavos, y la mesa plegable con la botella.
     */
    private val JUNTO_AL_HASTIAL: List<List<V3>> = run {
        val z = -T_LARGO + 0.06f; val zm = -T_LARGO + 0.15f
        listOf(
            listOf(V3(0.02f, 1.4f, z), V3(0.05f, 1.4f, z), V3(0.07f, 0.55f, z), V3(0.04f, 0.55f, z)),
            listOf(V3(-0.04f, 2.32f, z), V3(-0.3f, 2.28f, z), V3(-0.33f, 2.02f, z), V3(-0.2f, 1.96f, z), V3(-0.05f, 2.0f, z)),
            listOf(V3(-0.2f, 2.5f, z), V3(0.2f, 2.5f, z), V3(0.13f, 2.66f, z), V3(0f, 2.6f, z), V3(-0.13f, 2.67f, z)),
            listOf(V3(0.98f, 0.7f, zm), V3(1.3f, 0.7f, zm), V3(1.3f, 0.76f, zm), V3(0.98f, 0.76f, zm)),
            listOf(V3(1.02f, 0f, zm), V3(1.06f, 0f, zm), V3(1.22f, 0.7f, zm), V3(1.18f, 0.7f, zm)),
            listOf(V3(1.22f, 0f, zm), V3(1.26f, 0f, zm), V3(1.06f, 0.7f, zm), V3(1.02f, 0.7f, zm)),
            listOf(V3(1.08f, 0.76f, zm), V3(1.13f, 0.76f, zm), V3(1.12f, 0.98f, zm), V3(1.09f, 0.98f, zm)),
        )
    }

    /** Dónde cuelga el farol, dentro de la tienda (en sus ejes): de la cumbrera, a la vista por la puerta. */
    private val FAROL = V3(T_PX - 0.15f, 1.95f, -T_LARGO + 0.7f)

    /**
     * Lo de dentro junto a la pared de la derecha (en los ejes de la tienda), que se ve en sombra a través de la lona a
     * contraluz y, de noche, tumba en ella su sombra agrandada por el farol: la mesa plegable con la botella, la silla de
     * tijera y la silla de montar en su caballete **[Diseño propio]**.
     */
    private val JUNTO_A_LA_PARED: List<List<V3>> = listOf(
        listOf(V3(1.0f, 0.7f, -1.05f), V3(1.0f, 0.7f, -0.25f), V3(1.0f, 0.77f, -0.25f), V3(1.0f, 0.77f, -1.05f)),
        listOf(V3(1.0f, 0f, -0.95f), V3(1.0f, 0f, -0.87f), V3(1.0f, 0.7f, -0.35f), V3(1.0f, 0.7f, -0.43f)),
        listOf(V3(1.0f, 0f, -0.35f), V3(1.0f, 0f, -0.43f), V3(1.0f, 0.7f, -0.95f), V3(1.0f, 0.7f, -0.87f)),
        listOf(V3(1.0f, 0.77f, -0.92f), V3(1.0f, 0.77f, -0.82f), V3(1.0f, 1.0f, -0.85f), V3(1.0f, 1.0f, -0.89f)),
        listOf(V3(1.05f, 0.44f, 0.05f), V3(1.05f, 0.44f, 0.45f), V3(1.05f, 0.49f, 0.45f), V3(1.05f, 0.49f, 0.05f)),
        listOf(V3(1.05f, 0.44f, 0.4f), V3(1.05f, 0.44f, 0.46f), V3(1.05f, 0.98f, 0.49f), V3(1.05f, 0.98f, 0.43f)),
        listOf(V3(1.05f, 0f, 0.05f), V3(1.05f, 0f, 0.1f), V3(1.05f, 0.44f, 0.4f), V3(1.05f, 0.44f, 0.35f)),
        listOf(V3(1.05f, 0f, 0.4f), V3(1.05f, 0f, 0.45f), V3(1.05f, 0.44f, 0.1f), V3(1.05f, 0.44f, 0.05f)),
        listOf(V3(1.0f, 0f, 0.85f), V3(1.0f, 0f, 0.92f), V3(1.0f, 0.82f, 1.17f), V3(1.0f, 0.82f, 1.1f)),
        listOf(V3(1.0f, 0f, 1.45f), V3(1.0f, 0f, 1.52f), V3(1.0f, 0.82f, 1.2f), V3(1.0f, 0.82f, 1.13f)),
        listOf(V3(1.0f, 0.78f, 0.8f), V3(1.0f, 0.95f, 0.84f), V3(1.0f, 0.99f, 0.98f), V3(1.0f, 0.9f, 1.15f), V3(1.0f, 0.97f, 1.38f), V3(1.0f, 1.1f, 1.5f), V3(1.0f, 0.86f, 1.55f), V3(1.0f, 0.6f, 1.47f), V3(1.0f, 0.64f, 0.84f)),
    )

    /**
     * El hueco de la puerta, abierto: se ve dentro (en perspectiva, con los ejes de la tienda) la lona del fondo y la de la
     * izquierda, que dejan pasar el día; el mástil de atrás; el catre de campaña con la manta y la almohada; el cofre de
     * madera con sus cantoneras y correas; y el farol colgado de la cumbrera, que de noche lo enciende todo. Los faldones,
     * recogidos a los lados y atados con su cinta roja.
     */
    private fun DrawScope.puerta(v: VistaVivac, a: Alumbre, farol: Float) {
        val s = S_TIENDA; val d = T_LARGO; val w = T_ANCHO; val px = T_PX
        fun q(x: Float, y: Float, z: Float) = s.p(V3(x, y, z))
        fun cuatro(vararg c: Float) = poligono((0 until c.size / 3).map { i -> q(c[3 * i], c[3 * i + 1], c[3 * i + 2]) })
        val hueco = listOf(q(px - 0.66f, 0f, -d - 0.01f), q(px + 0.66f, 0f, -d - 0.01f), q(px + 0.2f, 2.3f, -d - 0.01f), q(px - 0.2f, 2.3f, -d - 0.01f))
        val forma = poligono(hueco)
        val pd = a.p.con(0.8f * farol); val dia = 1f - v.noche
        val lonaDentro = lerp(C.LONA, C.HOJA, 0.28f)
        fun lonaA(k: Float) = lerp(pd.sombra(lonaDentro), pd.luz(lonaDentro), (k * dia + 0.45f * farol).coerceIn(0f, 1f))
        drawPath(forma, pd.sombra(lonaDentro))
        clipPath(forma) {
            // La lona de dentro: el fondo, que deja pasar el día (más clara arriba); la pared de la izquierda y su faldón de techo.
            drawPath(cuatro(-w, 0f, d, w, 0f, d, w, T_PARED, d, 0f, T_CUMBRE, d, -w, T_PARED, d), lonaA(0.62f))
            drawPath(cuatro(-w, 0f, -d, -w, 0f, d, -w, T_PARED, d, -w, T_PARED, -d), lonaA(0.42f))
            drawPath(cuatro(-w, T_PARED, -d, -w, T_PARED, d, 0f, T_CUMBRE, d, 0f, T_CUMBRE, -d), lonaA(0.3f))
            for (j in 1..3) { val z = -d + 2f * d * j / 4f; drawLine(mult(lonaA(0.42f), C.SOMBRA, 0.4f).copy(alpha = 0.7f), q(-w, 0f, z), q(-w, T_PARED, z), 0.5f) }
            // El suelo: la tierra, con una estera de paja en medio.
            drawPath(cuatro(-w, 0f, -d, w, 0f, -d, w, 0f, d, -w, 0f, d), pd.sombra(lerp(C.BARRO, C.HOJA, 0.35f)))
            drawPath(cuatro(-0.4f, 0f, -1.2f, 0.95f, 0f, -1.2f, 0.95f, 0f, 0.8f, -0.4f, 0f, 0.8f), pd.medio(lerp(ColorVivac.PAJA, C.BARRO, 0.3f)))
            // El mástil de atrás y la cumbrera.
            drawLine(pd.sombra(C.MADERA_OSC), q(0f, 0f, d - 0.03f), q(0f, T_CUMBRE, d - 0.03f), 0.06f * s.escala(0f, d))
            // El cofre, al fondo: madera con cantoneras de latón, dos correas y la tapa abombada.
            val cx0 = -0.45f; val cx1 = 0.2f; val cz0 = 1.1f; val cz1 = 1.48f; val ch = 0.46f
            drawPath(cuatro(cx1, 0f, cz0, cx1, 0f, cz1, cx1, ch, cz1, cx1, ch, cz0), pd.sombra(lerp(C.MADERA, C.CUERO, 0.35f)))
            drawPath(cuatro(cx0, 0f, cz0, cx1, 0f, cz0, cx1, ch, cz0, cx0, ch, cz0), pd.medio(lerp(C.MADERA, C.CUERO, 0.35f)))
            drawPath(cuatro(cx0, ch, cz0, cx1, ch, cz0, cx1, ch + 0.08f, cz0 + 0.1f, cx1, ch + 0.1f, (cz0 + cz1) / 2f, cx0, ch + 0.1f, (cz0 + cz1) / 2f, cx0, ch + 0.08f, cz0 + 0.1f), pd.luz(lerp(C.MADERA, C.CUERO, 0.25f)))
            for (u in listOf(0.28f, 0.72f)) { val x = cx0 + (cx1 - cx0) * u; drawLine(pd.medio(lerp(C.CORREA, C.MADERA, 0.5f)), q(x, 0f, cz0 - 0.005f), q(x, ch + 0.08f, cz0 + 0.05f), 0.04f * s.escala(x, cz0)) }
            for (x in listOf(cx0, cx1)) for (y in listOf(0f, ch - 0.07f)) drawPath(cuatro(x - 0.03f, y, cz0 - 0.01f, x + 0.03f, y, cz0 - 0.01f, x + 0.03f, y + 0.07f, cz0 - 0.01f, x - 0.03f, y + 0.07f, cz0 - 0.01f), pd.luz(C.LATON))
            // El catre de campaña, contra la pared de la izquierda: las patas en tijera, el larguero, la manta azul que
            // cuelga por el lado y la almohada.
            val kx0 = -w + 0.08f; val kx1 = -0.55f; val kz0 = -0.1f; val kz1 = 1.55f; val kh = 0.42f
            for (z in listOf(kz0 + 0.1f, (kz0 + kz1) / 2f, kz1 - 0.1f)) {
                drawLine(pd.sombra(C.MADERA_OSC), q(kx1, 0f, z - 0.16f), q(kx1, kh, z + 0.16f), 0.035f * s.escala(kx1, z))
                drawLine(pd.sombra(C.MADERA_OSC), q(kx1, 0f, z + 0.16f), q(kx1, kh, z - 0.16f), 0.035f * s.escala(kx1, z))
            }
            drawLine(pd.medio(C.MADERA), q(kx1, kh, kz0), q(kx1, kh, kz1), 0.04f * s.escala(kx1, kz0))
            val manta = lerp(C.AZUL, C.CAPOTE, 0.25f)
            drawPath(cuatro(kx0, kh + 0.06f, kz0, kx1 + 0.04f, kh + 0.06f, kz0, kx1 + 0.04f, kh + 0.06f, kz1 - 0.3f, kx0, kh + 0.06f, kz1 - 0.3f), pd.luz(manta))
            drawPath(cuatro(kx1 + 0.04f, kh + 0.06f, kz0, kx1 + 0.04f, kh + 0.06f, kz1 - 0.3f, kx1 + 0.05f, kh - 0.2f, kz1 - 0.35f, kx1 + 0.05f, kh - 0.18f, kz0 + 0.05f), pd.medio(manta))
            drawPath(cuatro(kx0, kh + 0.06f, kz0, kx1 + 0.04f, kh + 0.06f, kz0, kx1 + 0.05f, kh - 0.18f, kz0 + 0.05f, kx0, kh - 0.15f, kz0), pd.sombra(manta))
            drawPath(cuatro(kx0 + 0.05f, kh + 0.06f, kz1 - 0.32f, kx1, kh + 0.06f, kz1 - 0.32f, kx1, kh + 0.16f, kz1 - 0.12f, kx0 + 0.05f, kh + 0.16f, kz1 - 0.12f), pd.luz(lerp(C.LONA, C.NIEVE, 0.4f)))
            // El farol colgado de la cumbrera: la cuerda, el cuerpo con sus vidrios y el sombrerete.
            val fc = s.p(FAROL); val fk = s.escala(FAROL.x, FAROL.z)
            drawLine(pd.sombra(C.HIERRO), q(FAROL.x, T_CUMBRE - 0.1f, FAROL.z), fc + Offset(0f, -0.14f * fk), (0.012f * fk).coerceAtLeast(0.35f))
            if (farol > 0.02f) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFC870).copy(alpha = (0.85f * farol).coerceIn(0f, 1f)), Color(0xFFE8782E).copy(alpha = (0.35f * farol).coerceIn(0f, 1f)), Color.Transparent), fc, 1.3f * fk), 1.3f * fk, fc, blendMode = BlendMode.Plus)
            val cuerpo = poligono(fc + Offset(-0.07f * fk, -0.12f * fk), fc + Offset(0.07f * fk, -0.12f * fk), fc + Offset(0.06f * fk, 0.1f * fk), fc + Offset(-0.06f * fk, 0.1f * fk))
            drawPath(cuerpo, if (farol > 0.02f) lerp(pd.medio(C.LATON), Color(0xFFFFE0A0), 0.9f * farol) else pd.luz(lerp(C.PLATA, C.LONA, 0.3f)))
            drawPath(cuerpo, pd.sombra(C.HIERRO), style = Stroke((0.014f * fk).coerceAtLeast(0.35f)))
            drawLine(pd.sombra(C.HIERRO), fc + Offset(0f, -0.12f * fk), fc + Offset(0f, 0.1f * fk), (0.01f * fk).coerceAtLeast(0.3f))
            drawPath(poligono(fc + Offset(-0.085f * fk, -0.12f * fk), fc + Offset(0f, -0.21f * fk), fc + Offset(0.085f * fk, -0.12f * fk)), pd.sombra(C.HIERRO))
            // El mástil de delante, que asoma abajo, a la izquierda del hueco, con el sable colgado.
            drawLine(pd.sombra(C.MADERA), q(0f, 0f, -d + 0.04f), q(0f, T_CUMBRE, -d + 0.04f), 0.07f * s.escala(0f, -d))
            drawPath(poligono(JUNTO_AL_HASTIAL[0].map { s.p(it) }), pd.luz(C.PLATA))
            drawLine(pd.medio(C.LATON), s.p(JUNTO_AL_HASTIAL[0][0]), s.p(JUNTO_AL_HASTIAL[0][1]), (0.04f * s.k).coerceAtLeast(0.6f))
            // La penumbra que cae de los bordes del hueco hacia dentro (de día).
            drawPath(forma, Brush.horizontalGradient(listOf(mult(pd.sombra(lonaDentro), C.SOMBRA, 0.8f).copy(alpha = 0.55f * (1f - farol)), Color.Transparent, Color.Transparent, mult(pd.sombra(lonaDentro), C.SOMBRA, 0.8f).copy(alpha = 0.3f * (1f - farol))), hueco[0].x, hueco[1].x))
        }
        // Los faldones de la puerta, recogidos a los lados: la lona doblada en pliegues, atada a media altura con su cinta.
        for (sg in listOf(-1f, 1f)) {
            val t0 = q(px + 0.2f * sg, 2.3f, -d - 0.02f); val t1 = q(px + 0.66f * sg, 0f, -d - 0.02f)
            val nudo = q(px + 0.8f * sg, 1.0f, -d - 0.05f); val fuera = q(px + 0.94f * sg, 1.0f, -d - 0.06f); val pie = q(px + 0.86f * sg, 0f, -d - 0.04f)
            val faldon = poligono(listOf(t0, nudo + Offset(0f, -0.05f * s.k), fuera, pie, t1, nudo + Offset(-0.1f * sg * s.k, 0f)))
            val col = a.cara(lerp(C.LONA, C.HOJA, 0.16f), s.normal(V3(0.4f * sg, 0f, -1f)), nudo)
            drawPath(faldon, col)
            drawPath(faldon, mult(col, C.SOMBRA, 0.3f).copy(alpha = 0.6f), style = Stroke(0.5f))
            drawLine(mult(col, C.SOMBRA, 0.45f), t0, nudo, 0.6f); drawLine(mult(col, C.SOMBRA, 0.45f), nudo, pie, 0.6f)
            drawLine(mult(col, C.SOMBRA, 0.3f).copy(alpha = 0.6f), t0 + (nudo - t0) * 0.5f, t1 + (pie - t1) * 0.4f, 0.45f)
            drawLine(a.plano(C.ROJO, nudo, 0f), nudo + Offset(-0.07f * s.k, -0.02f * s.k), fuera + Offset(0.02f * sg * s.k, 0.02f * s.k), (0.035f * s.k).coerceAtLeast(0.5f))
        }
    }

    // ---------------------------------------------------------------- El tonel, el tocón, la leña, la cama

    /** El tonel de pie que hace de mesa: duelas, aros de hierro; encima, una botella y un vaso de lata (de noche, la vela). */
    private fun DrawScope.tonel(v: VistaVivac) {
        val s = S_TONEL; val a = v.en(s.pie); val r = 0.27f
        val madera = lerp(C.MADERA, C.MADERA_GRIS, 0.35f)
        cilindro(s, a, V3(0f, 0f, 0f), V3(0f, 1f, 0f), r * 0.9f, r, TONEL_ALTO * 0.5f, madera, null, 16)
        cilindro(s, a, V3(0f, TONEL_ALTO * 0.5f, 0f), V3(0f, 1f, 0f), r, r * 0.9f, TONEL_ALTO * 0.5f, madera, lerp(madera, C.LONA, 0.2f), 16)
        // Las duelas y los aros.
        val k = s.k
        for (y in listOf(0.08f, 0.24f, TONEL_ALTO - 0.22f, TONEL_ALTO - 0.06f)) {
            val ry = r * (if (y < TONEL_ALTO * 0.5f) 0.9f + 0.1f * (y / (TONEL_ALTO * 0.5f)) else 1f - 0.1f * ((y - TONEL_ALTO * 0.5f) / (TONEL_ALTO * 0.5f)))
            val pts = (0..10).map { i -> val t = PI.toFloat() * (0.05f + 0.9f * i / 10f); s.p(V3(-ry * cos(t), y, -ry * sin(t))) }
            for (i in 0 until pts.size - 1) drawLine(a.plano(C.HIERRO, pts[i], if (i < 5) -0.6f else 0.6f), pts[i], pts[i + 1], (0.02f * k).coerceIn(0.4f, 1.6f))
        }
        for (i in 0 until 9) { val t = PI.toFloat() * (0.1f + 0.8f * i / 8f); val p0 = s.p(V3(-r * 0.9f * cos(t), 0.02f, -r * 0.9f * sin(t))); val p1 = s.p(V3(-r * cos(t), TONEL_ALTO * 0.5f, -r * sin(t))); val p2 = s.p(V3(-r * 0.9f * cos(t), TONEL_ALTO, -r * 0.9f * sin(t)))
            val col = mult(a.plano(madera, p1, -cos(t)), C.SOMBRA, 0.3f); drawLine(col, p0, p1, 0.3f); drawLine(col, p1, p2, 0.3f) }
        // Lo de encima: una botella verde oscura y un vaso de lata; de noche, un cabo de vela en su gollete (da luz).
        val tapa = s.p(V3(0.06f, TONEL_ALTO, 0.04f))
        val botella = s.p(V3(-0.08f, TONEL_ALTO, -0.02f))
        drawPath(poligono(listOf(botella + Offset(-0.04f * k, 0f), botella + Offset(0.04f * k, 0f), botella + Offset(0.04f * k, -0.17f * k), botella + Offset(0.015f * k, -0.22f * k), botella + Offset(0.015f * k, -0.3f * k), botella + Offset(-0.015f * k, -0.3f * k), botella + Offset(-0.015f * k, -0.22f * k), botella + Offset(-0.04f * k, -0.17f * k))), a.plano(lerp(C.PINO, C.CENIZA, 0.3f), botella, 0f))
        drawLine(a.plano(lerp(C.PINO_LUZ, Color.White, 0.3f), botella, if (v.p.solALaDerecha) 0.9f else -0.9f), botella + Offset((if (v.p.solALaDerecha) 0.025f else -0.025f) * k, -0.03f * k), botella + Offset((if (v.p.solALaDerecha) 0.025f else -0.025f) * k, -0.15f * k), 0.012f * k)
        cilindro(s, a, V3(0.1f, TONEL_ALTO, 0.02f), V3(0f, 1f, 0f), 0.045f, 0.05f, 0.1f, C.PLATA, lerp(C.PLATA, C.CENIZA, 0.4f), 10)
        if (VivacNapoleon.farol(v.m) > 0.3f && v.fase != Fase.SUENO && !VivacNapoleon.sinCompania(v.m)) {
            val vela = botella + Offset(0f, -0.3f * k)
            drawRect(a.plano(C.CORREA, vela, 0f), vela + Offset(-0.012f * k, -0.07f * k), Size(0.024f * k, 0.07f * k))
        }
        if (v.nieve > 0.3f) mancha(tapa, r * 0.7f * k, r * 0.2f * k, a.arriba(C.NIEVE, tapa), 0.7f * v.nieve)
    }

    /** El tocón de partir leña: la corteza, los anillos de la cara de arriba, los tajos del hacha; las astillas al pie. */
    private fun DrawScope.tocon(s: Sitio, a: Alumbre, v: VistaVivac) {
        val r = 0.26f; val k = s.k
        cilindro(s, a, V3(0f, 0f, 0f), V3(0f, 1f, 0f), r * 1.08f, r, TOCON_ALTO, lerp(C.MADERA_OSC, C.MADERA_GRIS, 0.3f), lerp(C.MADERA, C.LONA, 0.3f), 16)
        val top = V3(0f, TOCON_ALTO + 0.002f, 0f)
        val c = s.p(top)
        // Los anillos y la grieta.
        for (j in 1..3) { val pts = PiezasNapoleon.circulo(s, top, V3(0f, 1f, 0f), r * j / 4f, 18); drawPath(TrazosValle.cerrada(pts), a.arriba(lerp(C.MADERA, C.HOJA, 0.3f), c), style = Stroke(0.012f * k)) }
        drawLine(a.arriba(C.MADERA_OSC, c), s.p(top + V3(-0.02f, 0f, 0.02f)), s.p(top + V3(-r * 0.9f, 0f, -0.06f)), 0.016f * k)
        // Los tajos del hacha en el canto.
        for (j in 0 until 3) { val ang = 3.6f + j * 0.35f; val q0 = s.p(V3(r * cos(ang), TOCON_ALTO - 0.02f, r * sin(ang))); drawLine(a.plano(C.MADERA_OSC, q0, 0f), q0, q0 + Offset(0.03f * k, 0.06f * k), 0.015f * k) }
        // Las astillas al pie.
        val az = VivacNapoleon.fijo("astillas")
        repeat(8) { val q = s.p(V3(az.entre(-0.55f, 0.55f), 0f, az.entre(-0.45f, 0.1f))); val ang = az.entre(0f, 3.14f); val l = az.entre(0.06f, 0.14f) * k
            drawLine(a.arriba(lerp(C.MADERA, C.LONA, 0.35f), q), q, q + Offset(cos(ang) * l, sin(ang) * l * 0.3f), (0.014f * k).coerceAtLeast(0.35f), StrokeCap.Round) }
        if (v.nieve > 0.3f) mancha(c, r * 0.8f * k, r * 0.25f * k, a.arriba(C.NIEVE, c), 0.6f * v.nieve)
    }

    /** El montón de leña: troncos partidos, con su cara de corte clara y la corteza; el hacha clavada en uno. */
    private fun DrawScope.lena(v: VistaVivac) {
        val s = S_LENA; val a = v.en(s.pie); val az = VivacNapoleon.fijo("lena"); val k = s.k
        val troncos = ArrayList<Pair<V3, Float>>()
        for (fila in 0..2) for (j in 0 until 4 - fila) troncos += V3(-0.5f + j * 0.15f + fila * 0.075f + az.entre(-0.02f, 0.02f), 0.06f + fila * 0.1f, az.entre(-0.05f, 0.05f)) to az.entre(0.045f, 0.065f)
        for ((c, r) in troncos.sortedBy { it.first.y }) {
            val corte = c + V3(0f, 0f, -0.35f)
            cilindro(s, a, c + V3(0f, 0f, 0.3f), V3(0f, 0f, -1f), r * az.entre(0.9f, 1.1f), r, 0.65f, lerp(C.MADERA_OSC, C.MADERA_GRIS, az.entre(0f, 0.5f)), lerp(C.MADERA, C.LONA, 0.4f), 16)
            val q = s.p(corte); drawCircle(a.cara(lerp(C.MADERA, C.HOJA, 0.25f), s.normal(V3(0f, 0f, -1f)), q), r * 0.35f * s.escala(c.x, -0.35f), q, style = Stroke(0.01f * k))
        }
        // El hacha, clavada en el tronco de arriba, con el mango hacia arriba.
        val h0 = s.p(V3(-0.28f, 0.3f, -0.12f)); val h1 = s.p(V3(-0.42f, 0.6f, -0.26f))
        drawLine(a.plano(C.MADERA, h0, -0.5f), h0, h1, (0.035f * k).coerceAtLeast(0.6f), StrokeCap.Round)
        drawPath(poligono(h0 + Offset(-0.07f * k, 0.015f * k), h0 + Offset(0.045f * k, -0.02f * k), h0 + Offset(0.06f * k, 0.045f * k), h0 + Offset(-0.045f * k, 0.06f * k)), a.plano(C.HIERRO, h0, 0.6f))
        if (v.nieve > 0.2f) for ((c, r) in troncos.filter { it.first.y > 0.3f }) { val q = s.p(c + V3(0f, r, 0f)); mancha(q, 0.35f * k, 0.05f * k, a.arriba(C.NIEVE, q), 0.8f * v.nieve) }
    }

    /**
     * Las ramas de dormir delante del fuego: «cortar ramas para dormir encima y no en el barro». Tendidas en perspectiva
     * (en metros, en los ejes de [S_CAMA]; los pies, hacia el fuego): ramas de abeto o de pino cortadas, planas, cada una
     * con su forma de pluma y sus brotes en espiga, unas sobre otras. De madrugada, con la manta echada (el embozo vuelto)
     * y la mochila de almohada; de día, aplastadas, con el hueco de quien durmió. La hechura es la de la cama de la
     * retirada (`RetiradaCerca`), sin la nieve encima.
     */
    private fun DrawScope.cama(v: VistaVivac) {
        val s = S_CAMA; val a = v.en(s.pie); val p = v.p
        val az = Azar(Semilla.de("napoleon", "vivac", "cama"))
        fun q(x: Float, z: Float, y: Float = 0f) = s.p(x, y, z)
        mancha(q(0f, 0f), 0.78f * s.escala(0f, 0f), 0.2f * s.escala(0f, 0f), p.sombra(lerp(C.BARRO, C.HOJA, 0.3f)), 0.45f)
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
            // De día, las ramas de anoche se secan y se aplastan: más pardas.
            val seca = if (v.fase == Fase.MARCHA) 0.3f else 0f
            drawPath(forma, a.arriba(lerp(lerp(C.PINO_OSC, C.PINO, 0.3f + 0.15f * (i % 3)), C.HOJA, seca), en))
            clipPath(forma) {
                for (j in 1 until n) {
                    val t = j / n.toFloat(); val t2 = (t + 0.1f).coerceAtMost(1f); val ww = w(t2) * 1.05f
                    val col = a.arriba(lerp(if (j % 3 == 0) C.PINO_LUZ else lerp(C.PINO, C.PINO_LUZ, 0.35f), C.HOJA, seca), en)
                    val c = q(ex(t), ez(t), y)
                    drawLine(col, c, q(ex(t2) - dz * ww, ez(t2) + dx * ww, y), 1f, StrokeCap.Round)
                    drawLine(col, c, q(ex(t2) + dz * ww, ez(t2) - dx * ww, y), 1f, StrokeCap.Round)
                }
            }
            drawLine(a.arriba(C.MADERA, en), q(ex(0f), ez(0f), y), q(ex(0.8f), ez(0.8f), y), 1f, StrokeCap.Round)
            drawCircle(a.arriba(C.HOJA, en), 0.8f, q(ex(0f), ez(0f), y))
        }
        when (v.fase) {
            Fase.SUENO -> {
                // La manta, echada hacia los pies, con el embozo vuelto; la mochila de almohada en la cabecera.
                val lana = lerp(C.CAPOTE, C.MADERA, 0.18f); val dentro = lerp(lana, C.LONA, 0.35f)
                val xb = -0.08f; val x1 = 0.48f; val zw = 0.24f; val h = 0.08f
                val borde = (0..6).map { j -> val z = -zw + 2f * zw * j / 6f; V3(xb + az.entre(-0.03f, 0.03f) + 0.05f * sin(z * 6f), h + az.entre(-0.005f, 0.02f), z) }
                val lejos = (1..5).map { j -> V3(xb + (x1 - xb) * j / 6f, az.entre(0.03f, 0.1f), zw + az.entre(-0.04f, 0.04f)) }
                val pies = (0..4).map { j -> V3(x1 + az.entre(-0.05f, 0.03f), az.entre(0.03f, 0.1f), zw - 2f * zw * j / 4f) }
                val cerca = (5 downTo 1).map { j -> V3(xb + (x1 - xb) * j / 6f, az.entre(0.015f, 0.09f), -zw - az.entre(-0.03f, 0.04f)) }
                fun en(w: V3) = q(w.x, w.z, w.y)
                val canto = listOf(borde.first()) + cerca.reversed() + listOf(pies.last())
                val nDelante = s.normal(Vec.unidad(V3(0f, 0.2f, -1f)))
                drawPath(TrazosValle.cerrada(canto.map { en(it) } + canto.reversed().map { q(it.x, it.z - 0.035f, 0.012f) }), a.cara(lana, nDelante, q(0.2f, -zw)))
                val manta = TrazosValle.cerrada((borde + lejos + pies + cerca).map { en(it) })
                drawPath(manta, a.arriba(lana, q(0.25f, 0f)))
                clipPath(manta) {
                    for ((x, z, claro) in listOf(Triple(0.12f, -0.1f, true), Triple(0.22f, 0.04f, false), Triple(0.3f, 0.15f, true), Triple(0.08f, 0.16f, false), Triple(0.34f, -0.06f, false))) {
                        val c = q(x, z, h)
                        mancha(c, 0.16f * s.escala(x, z), 0.06f * s.escala(x, z), if (claro) a.cara(lana, s.normal(Vec.unidad(V3(0.3f, 1f, -0.4f))), c) else p.sombra(lana), 0.75f)
                    }
                    for (u in listOf(0.12f, 0.38f)) drawLine(a.arriba(lerp(lana, C.AZUL, 0.4f), q(u, 0f)), q(u, -zw, h), q(u + 0.03f, zw, h), 0.03f * s.k)
                }
                val ancho = 0.09f
                val embozo = TrazosValle.cerrada(borde.map { q(it.x, it.z, it.y + 0.012f) } + borde.reversed().map { q(it.x + ancho + 0.02f * sin(it.z * 9f), it.z, it.y + 0.012f) })
                drawPath(embozo, a.arriba(dentro, q(xb, 0f)))
                val sm = PiezasNapoleon.sitio(MIRADA, q(-0.36f, 0.02f), 0.55f + 1.4f)
                mochila(sm, a, 77L, true, C.CAPOTE, 0f, v.nieve)
            }
            Fase.MARCHA -> mancha(q(0.15f, 0f, 0.05f), 0.3f * s.k, 0.07f * s.k, p.sombra(C.PINO), 0.5f)
            Fase.ALTO, Fase.VIVAC -> Unit
        }
    }

    /**
     * Los días de fiesta, la bandera del regimiento desplegada bajo el águila («la bandera del regimiento», dice el cuento):
     * las tres bandas azul, blanca y roja en vertical, con el fleco de oro, que ondea con sus pliegues de luz y de sombra
     * **[Aproximación]**: sin letras ni bordados (el cuadro no lleva texto).
     */
    private fun DrawScope.bandera(v: VistaVivac) {
        val asta0 = PUNTA_ASTA + Offset(0f, 3.4f * GRUESO_ASTA); val alto = 1.0f * k(10.8f); val largo = 1.15f * k(10.8f)
        val a = v.en(asta0); val n = 12
        fun borde(t: Float, abajo: Float): Offset = asta0 + Offset(largo * t, abajo * alto + sin(t * 5.2f + 0.6f) * 3.2f * t + t * 6f)
        val bandas = listOf(C.AZUL, lerp(C.NIEVE, C.LONA, 0.3f), C.ROJO)
        for ((b, col) in bandas.withIndex()) {
            val t0 = b / 3f; val t1 = (b + 1) / 3f
            val pts = (0..n).map { borde(t0 + (t1 - t0) * it / n, 0f) } + (n downTo 0).map { borde(t0 + (t1 - t0) * it / n, 1f) }
            drawPath(poligono(pts), a.plano(col, asta0, 0f))
            // Los pliegues: donde la tela se curva hacia el sol, más clara; donde se aleja, en sombra.
            for (j in 0 until n) { val t = t0 + (t1 - t0) * j / n; val ond = kotlin.math.cos(t * 5.2f + 0.6f)
                if (ond > 0.3f) drawLine(a.plano(lerp(col, Color.White, 0.25f), asta0, 0.8f).copy(alpha = 0.5f), borde(t, 0.05f), borde(t, 0.95f), largo / n * 0.8f)
                if (ond < -0.4f) drawLine(mult(a.plano(col, asta0, 0f), C.SOMBRA, 0.5f).copy(alpha = 0.5f), borde(t, 0.05f), borde(t, 0.95f), largo / n * 0.8f) }
        }
        val fleco = (0..30).map { borde(it / 30f, 1f) }
        for (i in 0 until fleco.size - 1) drawLine(a.plano(C.ORO, fleco[i], 0.5f), fleco[i], fleco[i + 1] + Offset(0f, 1.2f), 1.2f)
    }

    /** Una corona de laurel colgada bajo el águila los días de fiesta **[Diseño propio]**. */
    private fun DrawScope.corona(v: VistaVivac, c: Offset) {
        val a = v.en(c); val r = GRUESO_ASTA * 2.6f
        for (i in 0 until 14) { val t = 2f * PI.toFloat() * i / 14f; val q = c + Offset(cos(t) * r, sin(t) * r * 0.9f)
            drawOval(a.plano(if (i % 2 == 0) C.PINO_LUZ else lerp(C.PINO, C.HOJA, 0.4f), q, cos(t)), Offset(q.x - r * 0.3f, q.y - r * 0.16f), Size(r * 0.6f, r * 0.32f)) }
        drawLine(a.plano(C.ROJO, c, 0f), c + Offset(0f, r * 0.9f), c + Offset(-r * 0.2f, r * 2.2f), r * 0.18f)
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: VistaVivac, reloj: Float) {
        val l = v.l; val f = v.fuego; val k = S_FUEGO.k
        if (f > 0.02f) {
            resplandor(LUZ_DEL_FUEGO, (70f + 60f * f) * (0.6f + 0.4f * v.noche), (0.25f + 0.75f * v.noche) * f * (0.92f + 0.08f * sin(reloj * 7f)))
            val ab = Azar(Semilla.de("napoleon", "vivac", "brasas"))
            repeat(11) { i -> val q = FUEGO + Offset(ab.entre(-0.24f, 0.24f) * k, ab.entre(-0.09f, 0.05f) * k); val al = (0.4f + 0.4f * sin(reloj * 1.4f + i * 2.3f)) * (f / 0.3f).coerceAtMost(1f)
                drawCircle(Color(0xFFFF6A22).copy(alpha = al.coerceIn(0f, 1f)), ab.entre(0.01f, 0.022f) * k, q) }
            llamas(FUEGO + Offset(0f, -0.1f * k), k, f, reloj, v.noche)
            val az = Azar(Semilla.de("napoleon", "vivac", "chispas"))
            repeat(8) { i ->
                val t = ((reloj * az.entre(0.3f, 0.6f) + az.real()) % 1f); val q = FUEGO + Offset(az.entre(-0.2f, 0.2f) * k + sin(reloj * 3f + i) * 3f, -0.3f * k - t * 1.6f * k)
                drawCircle(Color(0xFFFFC060).copy(alpha = ((1f - t) * f).coerceIn(0f, 1f)), 0.5f + 0.4f * (1f - t), q)
            }
            if (f >= 0.45f) humo(l, FUEGO + Offset(0f, -0.4f * k), 120f, 14f, reloj, v.m.viento + 0.15f, 0.22f + 0.3f * (1f - f), 0f, 4, 9, 0.45f + 0.35f * (1f - f))
            else {
                humo(l, FUEGO + Offset(0f, -0.2f * k), 70f, 5f, reloj, v.m.viento + 0.15f, 0.35f, 0f, 4, 6, 0.32f)
                // Con el sol bajo, el humo del rescoldo se enciende a contraluz: dorado.
                val bajo = (1f - v.l.solAltura / 0.5f).coerceIn(0f, 1f) * (v.r.fuerza / 0.72f).coerceIn(0f, 1f)
                if (bajo > 0.05f) mancha(FUEGO + Offset(4f + 3f * sin(reloj * 0.4f), -0.55f * k), 0.35f * k, 0.6f * k, lerp(v.l.solColor, Color(0xFFFFC98A), 0.4f), 0.3f * bajo)
            }
            if (f > 0.3f && v.noche > 0.3f) mancha(LUZ_DEL_FUEGO + Offset(4f, -0.75f * k), 0.22f * k, 0.35f * k, C.LUMBRE, 0.16f * f * v.noche)
        }
        if (EscenaNapoleon.marmita(v.m) == Marmita.AL_FUEGO) humo(l, FUEGO + Offset(0f, -0.95f * k), 40f, 6f, reloj, v.m.viento, 0f, 0f, 5, 5, 0.4f)
        // La llama del farol de la tienda y la de la vela del tonel, que tiemblan.
        val farol = VivacNapoleon.farol(v.m)
        if (farol > 0.02f) {
            val s = S_TIENDA; val c = s.p(FAROL); val kk = s.escala(FAROL.x, FAROL.z)
            if (EscenaNapoleon.lugar(v.m) != EscenaNapoleon.Lugar.PUEBLO) drawCircle(Color(0xFFFFE6A8).copy(alpha = (0.9f * farol).coerceIn(0f, 1f)), 0.03f * kk * (0.9f + 0.1f * sin(reloj * 11f)), c)
            if (v.fase != Fase.SUENO && !VivacNapoleon.sinCompania(v.m) && farol > 0.3f) {
                val sk = S_TONEL.k; val vela = S_TONEL.p(V3(-0.08f, TONEL_ALTO, -0.02f)) + Offset(0f, -0.38f * sk)
                drawOval(Color(0xFFFFE0A0).copy(alpha = farol), Offset(vela.x - 0.012f * sk, vela.y - 0.035f * sk + sin(reloj * 9f) * 0.2f), Size(0.024f * sk, 0.05f * sk))
                drawCircle(Brush.radialGradient(listOf(Color(0xFFFFC870).copy(alpha = 0.3f * farol), Color.Transparent), vela, 0.4f * sk), 0.4f * sk, vela, blendMode = BlendMode.Plus)
            }
        }
    }

    /** Una lengua de fuego: la base ancha y redonda, la punta que se mece [vaiven]. */
    private fun lengua(pie: Offset, ancho: Float, alto: Float, vaiven: Float): Path = Path().apply {
        moveTo(pie.x - ancho, pie.y)
        cubicTo(pie.x - ancho * 0.95f, pie.y - alto * 0.38f, pie.x - ancho * 0.3f + vaiven * 0.5f, pie.y - alto * 0.68f, pie.x + vaiven, pie.y - alto)
        cubicTo(pie.x + ancho * 0.3f + vaiven * 0.5f, pie.y - alto * 0.6f, pie.x + ancho * 0.95f, pie.y - alto * 0.32f, pie.x + ancho, pie.y)
        cubicTo(pie.x + ancho * 0.45f, pie.y + ancho * 0.3f, pie.x - ancho * 0.45f, pie.y + ancho * 0.3f, pie.x - ancho, pie.y)
        close()
    }

    /** Las llamas del fuego del rancho: de 2 (el rescoldo) a 9 lenguas, en tres capas, cada una con su vaivén. */
    private fun DrawScope.llamas(centro: Offset, k: Float, f: Float, reloj: Float, noche: Float) {
        val tam = f.coerceAtMost(1f) * (0.55f + 0.45f * noche)
        val az = Azar(Semilla.de("napoleon", "vivac", "llamas"))
        val n = if (f >= 0.45f) 9 else 3
        drawOval(Color(0xFFFFD27A).copy(alpha = (0.75f * (f / 0.5f).coerceAtMost(1f)).coerceIn(0f, 1f)), centro - Offset(0.2f * k * tam.coerceAtLeast(0.4f), 0.03f * k), Size(0.4f * k * tam.coerceAtLeast(0.4f), 0.06f * k))
        for (i in 0 until n) {
            val u = (i + 0.5f) / n - 0.5f + az.entre(-0.04f, 0.04f)
            val fase = az.entre(0f, 6.28f)
            val late = 0.78f + 0.22f * sin(reloj * (4.5f + 0.7f * i) + fase)
            val alto = k * 0.32f * (1f - 1.7f * u * u).coerceAtLeast(0.3f) * tam * late * az.entre(0.65f, 1.1f) * (if (f < 0.45f) 0.6f else 1f)
            if (alto < 1f) continue
            val ancho = alto * 0.24f + 0.015f * k
            val vaiven = alto * 0.2f * sin(reloj * 3.7f + fase) + alto * 0.06f * sin(reloj * 11f + fase * 2f)
            val p = centro + Offset(u * (if (f < 0.45f) 0.25f else 0.44f) * k, abs(u) * 0.05f * k)
            drawPath(lengua(p, ancho, alto, vaiven), Color(0xFFE0541C).copy(alpha = 0.6f))
            drawPath(lengua(p + Offset(0f, 0.005f * k), ancho * 0.66f, alto * 0.74f, vaiven * 0.75f), Color(0xFFFF9A30).copy(alpha = 0.85f))
            drawPath(lengua(p + Offset(0f, 0.008f * k), ancho * 0.34f, alto * 0.45f, vaiven * 0.45f), Color(0xFFFFE9AE).copy(alpha = 0.95f))
        }
    }
}
