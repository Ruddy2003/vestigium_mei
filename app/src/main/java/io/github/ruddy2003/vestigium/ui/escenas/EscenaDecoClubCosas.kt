package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDeco.Fase
import io.github.ruddy2003.vestigium.ui.escenas.PiezasDeco.Mesa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que hay en la sala del club, cada cosa donde la dejaría alguien (`composicion_deco.md`): el piano de Lou, de marco a
 * la izquierda, girado en tres cuartos delante de la esquina del estrado, con su taburete; en el estrado, el contrabajo
 * recostado en la pared, la batería y la corneta sobre una silla, y un atril; la fila de mesitas, que se aleja desde la
 * mesa cercana hacia el fondo a la derecha, y dos más detrás de la pista; la barra; el perchero al pie de la escalera; las
 * cajas de gaseosa en el rincón cercano; las dos lámparas colgantes; y delante, la mesa cercana (cortada por el borde de
 * abajo), con su lamparita, sus tazas, su cenicero y el objeto del día, y la silla girada con el abrigo.
 */
internal object CosasDeco {
    private val C = ColorDeco
    private val S = SalaDeco

    /** Lo que está a menos de esta distancia de los ojos se pinta después del aire (el haz de sol, el humo): queda delante. */
    const val CERCA = 3.45f

    // ---- El piano: su esquina cercana del frente en el suelo, y su giro (el teclado se aleja hacia el fondo a la derecha).
    val PIANO = Marco3(V3(-2.2f, 0f, 2.8f), 1.28f)
    /** La mesa cercana: el soporte del objeto del día. */
    val MESA_CERCA = V3(-0.45f, 0f, 2.38f)
    /** Las mesas de la fila y las de detrás de la pista (el club nuevo tiene las tres primeras). */
    val MESAS = listOf(V3(-0.05f, 0f, 3.8f), V3(0.18f, 0f, 5.0f), V3(0.05f, 0f, 6.4f), V3(-0.74f, 0f, 6.95f), V3(-1.48f, 0f, 6.9f))
    val TABURETE = V3(-1.22f, 0f, 3.0f)
    val SILLA_ABRIGO = V3(0.48f, 0f, 2.72f)
    val CAJAS = V3(0.82f, 0f, 2.95f)
    val PERCHERO = V3(0.42f, 0f, 7.45f)

    // ---- Para el gato y el objeto del día (en el lienzo).
    val TABURETE_X: Float = CamDeco.p(TABURETE).x
    val SILLA_ABRIGO_X: Float = CamDeco.p(SILLA_ABRIGO).x
    /** Encima del piano, en el extremo cercano de la tapa: el sitio alto del gato. */
    val MIRADOR: Offset = PIANO.p(0.32f, PiezasDeco.PIANO_ALTO, 0.3f)
    /** Dónde va el objeto chico: sobre la mesa cercana, en su borde de delante a la derecha. */
    val SOBRE: V3 = V3(MESA_CERCA.x + 0.1f, PiezasDeco.MESA_ALTO, MESA_CERCA.z - 0.13f)
    val OBJETO_X: Float = CamDeco.p(SOBRE).x
    /** El objeto grande pisa en el suelo junto a la mesa cercana y se recuesta en su canto. */
    val PIE_GRANDE = V3(0.08f, 0f, 2.95f)
    val APOYO_GRANDE = V3(-0.17f, PiezasDeco.MESA_ALTO - 0.02f, 2.46f)
    /** El gato se guarece bajo el teclado del piano. */
    val ABRIGO_X: Float = PIANO.p(0.55f, 0f, -0.3f).x
    /** El globo caído de la fiesta, junto a la silla del abrigo. */
    val GLOBO_X: Float = CamDeco.p(V3(0.3f, 0f, 2.6f)).x

    private const val FONDO_Z = 99f

    /** Todas las cosas de la sala en este momento, de la más lejana a la más cercana. */
    fun cosas(v: VistaDeco): List<Cosa3> {
        val m = v.m; val lista = ArrayList<Cosa3>()
        val prov = v.prov; val fase = v.fase
        // ---- Las lámparas colgantes (la cercana hace de marco por arriba) y la de la barra.
        val deco = v.enc.deco
        lista += Cosa3(S.LAMPARA_FONDO.z, emptyList()) { with(PiezasDeco) { lamparaColgante(v, S.LAMPARA_FONDO, 0.36f, 0.2f, deco) } }
        lista += Cosa3(S.LAMPARA_CERCA.z, emptyList()) { with(PiezasDeco) { lamparaColgante(v, S.LAMPARA_CERCA, 0.44f, 0.27f, deco) } }
        if (!v.nuevo) lista += Cosa3(S.LAMPARA_BARRA.z, emptyList()) { with(PiezasDeco) { lamparaColgante(v, S.LAMPARA_BARRA, 0.52f, 0.12f, v.enc.barra) } }
        if (EscenaDeco.ventilador(m)) lista += Cosa3(4.2f, emptyList()) { with(PiezasDeco) { ventiladorDeTecho(v, V3(-1.35f, S.TECHO, 4.2f), 0.3f, !giraElVentilador(v)) } }
        // ---- La barra.
        lista += barra(v)
        // ---- El estrado: el contrabajo, la batería, la silla con la corneta, el atril.
        if (!prov) {
            lista += PiezasDeco.contrabajo(v, Marco3(V3(-3.0f, S.ESTRADO_ALTO, 5.5f), 1.0f), -0.2f)
            lista += PiezasDeco.bateria(v, V3(-2.32f, S.ESTRADO_ALTO, 7.15f), if (fase == Fase.ABIERTO) v.enc.deco else 0f)
            lista += Cosa3(6.3f, listOf(V3(-2.15f, S.ESTRADO_ALTO, 6.1f), V3(-1.8f, S.ESTRADO_ALTO, 6.5f), V3(-2f, S.ESTRADO_ALTO + 0.9f, 6.5f))) {
                with(PiezasDeco) { silla(v, V3(-1.98f, S.ESTRADO_ALTO, 6.3f), 2.4f, false); corneta(v, V3(-1.98f, S.ESTRADO_ALTO + 0.46f, 6.26f)) } }
            lista += Cosa3(6.0f, listOf(V3(-2.55f, S.ESTRADO_ALTO, 6.0f), V3(-2.55f, S.ESTRADO_ALTO + 1.25f, 6.0f))) { with(PiezasDeco) { atril(v, V3(-2.55f, S.ESTRADO_ALTO, 6.0f), 0.9f) } }
        } else {
            // En la gira y sin club: el contrabajo y la batería no están; las sillas, apiladas en el estrado.
            lista += Cosa3(6.6f, listOf(V3(-2.6f, S.ESTRADO_ALTO, 6.4f), V3(-2f, S.ESTRADO_ALTO, 6.8f), V3(-2.3f, S.ESTRADO_ALTO + 1.6f, 6.6f))) {
                with(PiezasDeco) { for (k in 0..2) silla(v, V3(-2.3f + k * 0.03f, S.ESTRADO_ALTO + k * 0.11f, 6.6f), 2.2f, false) } }
        }
        // ---- El piano de Lou y su taburete.
        val estado = PiezasDeco.EstadoPiano(
            partitura = !prov, lampara = if (prov) 0f else v.enc.piano, metronomo = fase == Fase.ENSAYO && m.ficha.objeto != "metronomo",
            funda = prov, luto = EscenaDeco.luto(m) && !prov, flores = (EscenaDeco.luto(m) || EscenaDeco.boda(m)) && !prov,
            quemaduras = 2 + 2 * m.ficha.temporada, panuelo = fase == Fase.ABIERTO && m.ficha.objeto != "panuelo",
        )
        lista += PiezasDeco.piano(v, PIANO, estado)
        lista += Cosa3(TABURETE.z, listOf(V3(TABURETE.x - 0.2f, 0f, TABURETE.z - 0.2f), V3(TABURETE.x + 0.2f, 0f, TABURETE.z + 0.2f), V3(TABURETE.x, 0.52f, TABURETE.z))) { taburete(v) }
        // ---- Las mesas de la fila, con sus sillas.
        val n = EscenaDeco.mesas(m); val puestas = EscenaDeco.puestas(m); val arriba = EscenaDeco.sillasArriba(m)
        for (i in 0 until n - 1) {
            val c = MESAS[i]
            // La mesa i+1 (la cercana es la 0): puesta si le toca; con las sillas arriba las últimas (las más lejanas).
            val puesta = i + 1 < puestas; val sillasArriba = !puesta && (n - 1 - i) <= arriba
            lista += mesaConSillas(v, c, i + 1, puesta, sillasArriba)
        }
        // ---- La mesa cercana (cortada por el borde de abajo), con el objeto del día, y la silla con el abrigo.
        lista += mesaCercana(v)
        if (!prov) lista += Cosa3(SILLA_ABRIGO.z, listOf(V3(SILLA_ABRIGO.x - 0.2f, 0f, SILLA_ABRIGO.z - 0.2f), V3(SILLA_ABRIGO.x + 0.2f, 0f, SILLA_ABRIGO.z + 0.2f), V3(SILLA_ABRIGO.x, 0.95f, SILLA_ABRIGO.z + 0.2f))) {
            with(PiezasDeco) { silla(v, SILLA_ABRIGO, 2.5f, false, abrigo = if (fase == Fase.ABIERTO || fase == Fase.ENSAYO || fase == Fase.PREPARA) lerp(C.PANO, C.CORAL, 0.25f) else null) } }
        // ---- El perchero al pie de la escalera, las cajas de gaseosa en el rincón cercano, el cubo de la mañana.
        lista += PiezasDeco.perchero(v, PERCHERO, EscenaDeco.sombreros(m), EscenaDeco.nevada(m) && fase == Fase.ABIERTO)
        lista += PiezasDeco.cajas(v, CAJAS, 0.25f, if (prov) 2 else 3)
        if (!prov && fase == Fase.CERRADO && m.hora < 12.0) lista += PiezasDeco.cubo(v, V3(-1.05f, 0f, 4.7f))
        // El cubo con el bloque de hielo del día de calor, junto a la barra.
        if (EscenaDeco.calor(m)) lista += hielo(v, V3(-0.25f, 0f, 7.1f))
        // Las sillas apiladas en el rincón, cuando el club está cerrado por la gira o sin club.
        if (prov) lista += Cosa3(5.6f, listOf(V3(-0.7f, 0f, 5.4f), V3(-0.2f, 0f, 5.8f), V3(-0.45f, 1.4f, 5.6f))) { with(PiezasDeco) { for (k in 0..3) silla(v, V3(-0.45f + k * 0.02f, k * 0.12f, 5.6f), 2.0f + k * 0.05f, false) } }
        return lista.sortedByDescending { it.hondo }
    }

    /** Si el ventilador gira: mientras hay alguien en el club (si no, está quieto y se le ven las aspas). */
    fun giraElVentilador(v: VistaDeco): Boolean = !v.prov && (v.fase == Fase.ABIERTO || v.fase == Fase.ENSAYO || v.fase == Fase.PREPARA)

    /**
     * Una mesita de la fila con sus sillas: puesta, con las sillas alrededor (giradas como las dejan los que se levantan),
     * abrigos en algunos respaldos y sombreros en la mesa; sin poner, con las dos sillas patas arriba encima.
     */
    private fun mesaConSillas(v: VistaDeco, c: V3, i: Int, puesta: Boolean, sillasArriba: Boolean): Cosa3 {
        val m = v.m; val az = Azar(Semilla.de("deco", "mesa", i, m.ficha.dia))
        val abierto = v.fase == Fase.ABIERTO || v.fase == Fase.DESPUES
        val tazas = if (puesta && abierto) 1 + az.entero(3) else 0
        val cenicero = if (puesta && abierto) 2 + az.entero(5) else if (puesta) 0 else 0
        val sombrero = puesta && v.fase == Fase.ABIERTO && az.real() > 0.45f
        val lampara = if (puesta && v.fase == Fase.ABIERTO) v.enc.mesas else 0f
        val mesa = Mesa(c, puesta, lampara, sillasArriba, tazas, cenicero, sombrero, i)
        val esq = listOf(V3(c.x - 0.45f, 0f, c.z - 0.45f), V3(c.x + 0.45f, 0f, c.z - 0.45f), V3(c.x - 0.45f, 0f, c.z + 0.45f), V3(c.x + 0.45f, 0f, c.z + 0.45f), V3(c.x, PiezasDeco.MESA_ALTO + (if (sillasArriba) 0.5f else 0.25f), c.z))
        return Cosa3(c.z - 0.3f, esq) {
            with(PiezasDeco) {
                // Las sillas de detrás de la mesa, luego la mesa, luego las de delante.
                val sillas = if (sillasArriba) emptyList() else listOf(
                    Triple(V3(c.x + 0.05f + 0.1f * az.real(), 0f, c.z + 0.42f), 0.15f + 0.4f * az.real(), false),
                    Triple(V3(c.x - 0.44f, 0f, c.z + 0.08f), 1.6f + 0.4f * az.real(), puesta && abierto && az.real() > 0.5f),
                )
                for ((o, g, abrigo) in sillas.filter { it.first.z >= c.z }) silla(v, o, g, false, if (abrigo) lerp(C.PANO, C.CAOBA, az.real() * 0.6f) else null)
                mesita(v, mesa)
                if (sillasArriba) { silla(v, V3(c.x - 0.08f, PiezasDeco.MESA_ALTO, c.z + 0.06f), 0.4f, true); silla(v, V3(c.x + 0.1f, PiezasDeco.MESA_ALTO, c.z - 0.05f), 3.4f, true) }
                for ((o, g, abrigo) in sillas.filter { it.first.z < c.z }) silla(v, o, g, false, if (abrigo) lerp(C.PANO, C.CAOBA, az.real() * 0.6f) else null)
            }
        }
    }

    /**
     * La mesa cercana, cortada por el borde de abajo: el soporte del objeto del día. Siempre sin sillas encima (la de Lou
     * entre pase y pase); puesta mientras el club está abierto o preparado, y desnuda (con sus tazas de anoche por la
     * mañana) el resto del día.
     */
    private fun mesaCercana(v: VistaDeco): Cosa3 {
        val m = v.m; val c = MESA_CERCA
        val puesta = !v.prov && (v.fase == Fase.ABIERTO || v.fase == Fase.PREPARA || v.fase == Fase.DESPUES)
        val anoche = !v.prov && ((v.fase == Fase.CERRADO && m.hora < 9.0) || v.fase == Fase.DESPUES)
        val mesa = Mesa(c, puesta, if (v.fase == Fase.ABIERTO) v.enc.mesas else 0f, false,
            tazas = if (v.fase == Fase.ABIERTO || anoche) 2 else 0, cenicero = if (v.fase == Fase.ABIERTO) 3 else if (anoche) 6 else 0, sombrero = false, semilla = 0)
        val esq = listOf(V3(c.x - 0.35f, 0f, c.z - 0.35f), V3(c.x + 0.35f, 0f, c.z - 0.35f), V3(c.x - 0.35f, 0f, c.z + 0.35f), V3(c.x + 0.35f, 0f, c.z + 0.35f), V3(c.x, PiezasDeco.MESA_ALTO, c.z))
        return Cosa3(c.z - 0.3f, esq) {
            with(PiezasDeco) { mesita(v, mesa) }
            // El vaso de agua de verdad y el sombrero de iglesia de su madre.
            if (EscenaDeco.madre(m)) with(PiezasDeco) { taza(v, V3(c.x - 0.12f, PiezasDeco.MESA_ALTO, c.z - 0.05f), vaso = true); sombrero(v, V3(c.x + 0.05f, PiezasDeco.MESA_ALTO, c.z + 0.12f), 0f, lerp(C.PANO, C.JADE, 0.5f), cloche = true) }
            // El objeto del día, sobre la mesa (los chicos); los grandes van aparte, al pie de la mesa.
            if (!ObjetosDeco.esGrande(m)) with(ObjetosDeco) { sobreElSoporte(m, v.pincel(CamDeco.p(SOBRE)), CamDeco.p(SOBRE), CamDeco.k(SOBRE.z) / 120f) }
            ObjetosDeco.grande(m, v.pincel(CamDeco.p(PIE_GRANDE)), CamDeco.p(PIE_GRANDE), CamDeco.p(APOYO_GRANDE), CamDeco.k(PIE_GRANDE.z) / 120f)?.let { g -> g.pintar(this) }
        }
    }

    /** El taburete del piano: asiento redondo de cuero que sube con su rosca, sobre tres patas de garra. */
    private fun androidx.compose.ui.graphics.drawscope.DrawScope.taburete(v: VistaDeco) {
        val c = TABURETE
        with(PiezasDeco) {
            for (k in 0..2) { val a = (0.3 + k * 2.0 * PI / 3.0).toFloat(); palo(v, V3(c.x + 0.05f * cos(a), 0.3f, c.z + 0.05f * sin(a)), V3(c.x + 0.2f * cos(a), 0.02f, c.z + 0.2f * sin(a)), 0.04f, C.CAOBA) }
            palo(v, V3(c.x, 0.28f, c.z), V3(c.x, 0.47f, c.z), 0.06f, C.LATON)
        }
        val top = aro(V3(c.x, 0.52f, c.z), 0.18f); val canto = aro(V3(c.x, 0.47f, c.z), 0.18f)
        drawPath(poligono((0..11).map { CamDeco.p(canto[it]) } + (11 downTo 0).map { CamDeco.p(top[it]) }), v.c(V3(c.x, 0.5f, c.z - 0.18f), C.CAOBA, V3(0f, 0f, -1f)))
        drawPath(CamDeco.forma(top), v.c(V3(c.x, 0.52f, c.z), lerp(C.CAOBA, C.CORAL, 0.3f), V3(0f, 1f, 0f)))
        drawPath(CamDeco.forma(top), v.c(V3(c.x, 0.52f, c.z), C.ORO, V3(0f, 1f, 0f)).copy(alpha = 0.6f), style = Stroke(0.5f))
    }

    /**
     * La barra, contra la pared del fondo: frente de laca con chevrones de oro y su zócalo de latón; encima, la cafetera de
     * cromo (el café que no es café), una tetera y las tazas; detrás, el espejo y los estantes (en la pared).
     */
    private fun barra(v: VistaDeco): Cosa3 {
        val x0 = S.BARRA_X0; val x1 = S.BARRA_X1; val z0 = S.BARRA_Z; val h = S.BARRA_ALTO
        val esq = listOf(V3(x0, 0f, z0), V3(x1, 0f, z0), V3(x0, h, z0), V3(x1, h, z0))
        return Cosa3(z0, esq) {
            val frente = CamDeco.forma(listOf(V3(x0, 0f, z0), V3(x1, 0f, z0), V3(x1, h, z0), V3(x0, h, z0)))
            drawPath(frente, Brush.horizontalGradient(0f to v.c(V3(x0, h / 2f, z0), C.LACA, V3(0f, 0f, -1f)), 1f to v.c(V3(x1, h / 2f, z0), C.LACA, V3(0f, 0f, -1f)), startX = CamDeco.p(x0, 0f, z0).x, endX = CamDeco.p(x1, 0f, z0).x))
            clipPath(frente) {
                var x = x0 + 0.08f; var i = 0
                while (x < x1) { val col = v.c(V3(x, h / 2f, z0), if (i % 3 == 1) C.JADE else C.ORO, V3(0f, 0f, -1f))
                    drawLine(col, CamDeco.p(x, 0.12f, z0), CamDeco.p(x, h - 0.1f, z0), 0.5f)
                    if (i % 3 == 1) { drawLine(col, CamDeco.p(x - 0.06f, 0.5f, z0), CamDeco.p(x, 0.62f, z0), 0.5f); drawLine(col, CamDeco.p(x, 0.62f, z0), CamDeco.p(x + 0.06f, 0.5f, z0), 0.5f) }
                    x += 0.15f; i++ }
            }
            // El zócalo de latón y la tapa de caoba, con su barra de apoyo.
            drawPath(CamDeco.forma(listOf(V3(x0, 0f, z0 - 0.01f), V3(x1, 0f, z0 - 0.01f), V3(x1, 0.1f, z0 - 0.01f), V3(x0, 0.1f, z0 - 0.01f))), v.c(V3(-1f, 0.05f, z0), C.LATON, V3(0f, 0f, -1f)))
            drawPath(CamDeco.forma(listOf(V3(x0 - 0.04f, h, z0 - 0.05f), V3(x1 + 0.04f, h, z0 - 0.05f), V3(x1 + 0.04f, h, S.FONDO - 0.2f), V3(x0 - 0.04f, h, S.FONDO - 0.2f))), v.c(V3(-1f, h, z0), C.CAOBA, V3(0f, 1f, 0f)))
            drawPath(CamDeco.forma(listOf(V3(x0 - 0.04f, h - 0.05f, z0 - 0.05f), V3(x1 + 0.04f, h - 0.05f, z0 - 0.05f), V3(x1 + 0.04f, h, z0 - 0.05f), V3(x0 - 0.04f, h, z0 - 0.05f))), v.c(V3(-1f, h, z0), lerp(C.CAOBA, C.MARFIL, 0.15f), V3(0f, 0f, -1f)))
            with(PiezasDeco) { palo(v, V3(x0, 0.22f, z0 - 0.12f), V3(x1, 0.22f, z0 - 0.12f), 0.035f, C.LATON) }
            // La cafetera de cromo, la tetera y las tazas.
            val cafe = V3(-1.45f, h, z0 + 0.2f); val k = CamDeco.k(cafe.z); val pc = CamDeco.p(cafe)
            drawRect(Brush.horizontalGradient(0f to v.c(cafe, C.CROMO, V3(-1f, 0f, 0f)), 0.4f to v.c(cafe, lerp(C.CROMO, C.MARFIL, 0.5f), V3(0f, 0f, -1f)), 1f to v.c(cafe, lerp(C.CROMO, C.HIERRO, 0.4f), V3(1f, 0f, 0f)), startX = pc.x - 0.1f * k, endX = pc.x + 0.1f * k), Offset(pc.x - 0.1f * k, pc.y - 0.42f * k), Size(0.2f * k, 0.42f * k))
            drawArc(v.c(cafe, C.CROMO, V3(0f, 1f, -0.5f)), 180f, 180f, true, Offset(pc.x - 0.1f * k, pc.y - 0.5f * k), Size(0.2f * k, 0.16f * k))
            drawCircle(v.c(cafe, C.LATON, V3(0f, 0f, -1f)), 0.02f * k, Offset(pc.x, pc.y - 0.55f * k))
            drawLine(v.c(cafe, C.LACA, V3(0f, 0f, -1f)), Offset(pc.x + 0.05f * k, pc.y - 0.1f * k), Offset(pc.x + 0.13f * k, pc.y - 0.1f * k), 0.5f)
            with(PiezasDeco) { for ((i, x) in listOf(-1.15f, -0.95f, -0.62f).withIndex()) taza(v, V3(x, h, z0 + 0.15f + 0.05f * i)) }
            // Las botellas rotas de la redada, en el suelo delante de la barra.
            if (EscenaDeco.redada(v.m)) { val az = Azar(Semilla.de("deco", "redada")); repeat(10) { val q = CamDeco.p(az.entre(x0, x1), 0f, z0 - az.entre(0.05f, 0.6f)); drawLine(v.c(V3(-1f, 0f, z0), lerp(C.JADE, C.CROMO, 0.4f), V3(0f, 1f, 0f)), q, q + Offset(az.entre(-2f, 2f), az.entre(-0.6f, 0.6f)), 0.7f, StrokeCap.Round) } }
        }
    }

    /** El balde con el bloque de hielo del día de calor, que suda en el suelo. */
    private fun hielo(v: VistaDeco, o: V3): Cosa3 = Cosa3(o.z, listOf(V3(o.x - 0.2f, 0f, o.z - 0.2f), V3(o.x + 0.2f, 0f, o.z + 0.2f), V3(o.x, 0.45f, o.z))) {
        val k = CamDeco.k(o.z); val p = CamDeco.p(o)
        val balde = Path().apply { moveTo(p.x - 0.18f * k, p.y - 0.28f * k); lineTo(p.x + 0.18f * k, p.y - 0.28f * k); lineTo(p.x + 0.15f * k, p.y); lineTo(p.x - 0.15f * k, p.y); close() }
        drawPath(balde, Brush.horizontalGradient(0f to v.c(o, C.CROMO, V3(-1f, 0f, 0f)), 1f to v.c(o, lerp(C.CROMO, C.HIERRO, 0.4f), V3(1f, 0f, 0f)), startX = p.x - 0.18f * k, endX = p.x + 0.18f * k))
        drawRect(v.c(V3(o.x, 0.4f, o.z), lerp(C.MARFIL, C.NOCHE, 0.3f), V3(0f, 0f, -1f)).copy(alpha = 0.9f), Offset(p.x - 0.1f * k, p.y - 0.46f * k), Size(0.2f * k, 0.2f * k))
        drawRect(Color.White.copy(alpha = 0.3f), Offset(p.x - 0.1f * k, p.y - 0.46f * k), Size(0.05f * k, 0.2f * k))
        drawOval(v.c(o, lerp(C.NOCHE, C.ROBLE, 0.4f), V3(0f, 1f, 0f)).copy(alpha = 0.5f), Offset(p.x - 0.3f * k, p.y - 0.02f * k), Size(0.6f * k, 0.06f * k), blendMode = BlendMode.SrcOver)
    }
}
