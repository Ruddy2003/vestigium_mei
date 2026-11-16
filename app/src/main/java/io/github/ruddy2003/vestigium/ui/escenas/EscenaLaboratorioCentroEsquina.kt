package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.CentroLab.Mesa
import io.github.ruddy2003.vestigium.ui.escenas.GaleriaCentro.barandilla
import io.github.ruddy2003.vestigium.ui.escenas.GeoCentro.G1
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.ARRIBA
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.caja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.cilindro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.linea
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Su esquina, en la galería del primer piso (`composicion_laboratorio_centro.md`, «Primer plano»): la mesa chica donde come
 * sola, de espaldas al ruido, entre la costilla y la barandilla, en tres cuartos y cortada por el borde de abajo; encima, la
 * bandeja de cuatro huecos, la taza de barro gris con la muesca, los auriculares grandes y la tableta de vidrio negro con la
 * esquina pegada con resina, y el objeto del día; la silla apartada, a escuadra; la barandilla de vidrio de cerca, con su
 * canto grueso y el pasamanos gastado; la flecha de color en el suelo de la galería; arriba, las sillas arrimadas a la
 * barandilla (alguien miró las pantallas). Todo en orden exacto: así se cuenta a ella, sin ella.
 */
internal object EsquinaCentro {
    private val C = ColorLaboratorio

    /** Dónde está la mesa chica (el centro de su pie) y hacia dónde va su lado largo; su tamaño. */
    val MESA: V3 = GeoCentro.aro(8.7f, G1, -105.6f)
    val U_MESA: V3 = V3(cos(GeoCentro.rad(-15.6f)), 0f, sin(GeoCentro.rad(-15.6f)))
    const val LADO = 0.74f
    const val ALTO = 0.74f
    /** La x del lienzo de la mesa, para el lado de la luz del gato. */
    const val MESA_X = 30f

    /** La silla apartada (el centro de su asiento en el suelo) y el sitio del objeto grande, junto a la mesa. */
    val SILLA: V3 = GeoCentro.aro(8.12f, G1, -107.0f)
    val PIE_GRANDE: V3 = GeoCentro.aro(8.55f, G1, -103.4f)

    /** La vela de su mesa, la noche larga. */
    val LAMPARA: V3 get() = enLaMesa(0.24f, 0.2f, 0.12f)

    /** Un punto sobre el tablero: [a] a lo largo, [b] de lado (metros desde el centro), [y] sobre el tablero. */
    fun enLaMesa(a: Float, b: Float, y: Float = 0f): V3 { val w = V3(-U_MESA.z, 0f, U_MESA.x); return MESA + U_MESA * a + w * b + V3(0f, ALTO + y, 0f) }

    fun sillaX(@Suppress("UNUSED_PARAMETER") m: Momento): Float = CamCentro.p(SILLA).x
    fun pieGrandeX(): Float = CamCentro.p(PIE_GRANDE).x

    /** El pasamanos de la barandilla de cerca, a la derecha de su mesa: el sitio alto del gato. */
    fun mirador(): Offset = CamCentro.p(GeoCentro.aro(GeoCentro.RV - 0.08f, G1 + GeoCentro.BARANDA + 0.08f, -93f))

    /** El claro de la mesa donde va el objeto chico del día. */
    val CLARO: V3 get() = enLaMesa(0.2f, -0.12f)

    fun DrawScope.esquina(v: VistaCentro) {
        suelo(v)
        sombras(v)
        barandilla(v, G1, GaleriaCentro.CERCA0 + 360f, GaleriaCentro.CERCA1 + 360f)
        if (CentroLab.sillasALaBarandilla(v.m)) sillasArrimadas(v)
        carro(v)
        silla(v)
        // El objeto grande del día, de pie en el suelo junto a la mesa y recostado en su canto.
        ObjetosLaboratorio.grande(v.m, v.pincel(PIE_GRANDE), CamCentro.p(PIE_GRANDE), CamCentro.p(enLaMesa(0.37f, -0.3f, -0.02f)), CamCentro.k(PIE_GRANDE) / 150f)?.let { it.pintar(this) }
        mesa(v)
        encima(v)
        if (!ObjetosLaboratorio.esGrande(v.m)) with(ObjetosLaboratorio) { sobreElSoporte(v.m, v.pincel(CLARO), CamCentro.p(CLARO), CamCentro.k(CLARO) / 150f) }
    }

    // ---------------------------------------------------------------- El suelo de la galería

    /** La flecha de color de la galería (la del pasillo del Almendro, verde) y el desgaste del paso junto a la barandilla. */
    private fun DrawScope.suelo(v: VistaCentro) {
        val r = 7.98f; val ancho = 0.08f
        val ps = (0..10).map { i -> -97f + 18f * i / 10f }
        for (i in 0 until ps.size - 1) {
            val f = CamCentro.forma(listOf(GeoCentro.aro(r - ancho, G1 + 0.002f, ps[i]), GeoCentro.aro(r - ancho, G1 + 0.002f, ps[i + 1]), GeoCentro.aro(r + ancho, G1 + 0.002f, ps[i + 1]), GeoCentro.aro(r + ancho, G1 + 0.002f, ps[i]))) ?: continue
            drawPath(f, v.c(GeoCentro.aro(r, G1, ps[i]), lerp(C.GRAMA, C.PISO, 0.4f), ARRIBA).copy(alpha = 0.7f))
        }
        for (g in listOf(-90f, -82f)) {
            val punta = listOf(GeoCentro.aro(r - 0.2f, G1 + 0.003f, g), GeoCentro.aro(r, G1 + 0.003f, g + 2.0f), GeoCentro.aro(r + 0.2f, G1 + 0.003f, g), GeoCentro.aro(r, G1 + 0.003f, g + 0.7f))
            CamCentro.forma(punta)?.let { drawPath(it, v.c(GeoCentro.aro(r, G1, g), C.GRAMA, ARRIBA)) }
        }
        // La luz del vacío, que entra por la barandilla y se tiende en el pulido; y el reflejo roto del canto del vidrio.
        for ((a, b) in GaleriaCentro.tramos(-110f + 360f, -40f + 360f, 4f)) {
            val f = CamCentro.forma(listOf(GeoCentro.aro(GeoCentro.RV, G1 + 0.002f, a), GeoCentro.aro(GeoCentro.RV, G1 + 0.002f, b), GeoCentro.aro(GeoCentro.RV + 0.75f, G1 + 0.002f, b), GeoCentro.aro(GeoCentro.RV + 0.75f, G1 + 0.002f, a))) ?: continue
            val p0 = CamCentro.p(GeoCentro.aro(GeoCentro.RV, G1, a)); val p1 = CamCentro.p(GeoCentro.aro(GeoCentro.RV + 0.75f, G1, a))
            drawPath(f, Brush.linearGradient(listOf(lerp(v.colorDifusa, v.fondo, 0.3f).copy(alpha = 0.34f * (0.3f + 0.7f * v.difusa)), Color.Transparent), p0, p1), blendMode = BlendMode.Plus)
        }
        val az0 = Azar(Semilla.de("laboratorio", "centro", "reflejo baranda"))
        var g = -104f
        while (g < -44f) {
            for (j in 0 until 5) {
                val r0 = GeoCentro.RV + 0.08f + j * 0.12f + az0.entre(0f, 0.05f)
                val a = CamCentro.p(GeoCentro.aro(r0, G1 + 0.002f, g)); val b = CamCentro.p(GeoCentro.aro(r0, G1 + 0.002f, g + az0.entre(0.8f, 2.4f)))
                drawLine(lerp(C.VIDRIO, Color.White, 0.5f).copy(alpha = (0.16f * (1f - j / 5f) * az0.entre(0.4f, 1f)).coerceIn(0f, 1f)), a, b, 0.8f, blendMode = BlendMode.Plus)
            }
            g += 3f + az0.entre(0f, 2f)
        }
        // El granito del pulido: motas claras y oscuras en el suelo de cerca, y las juntas entre losas a lo ancho.
        val az1 = Azar(Semilla.de("laboratorio", "centro", "granito"))
        val claras = ArrayList<Offset>(); val oscuras = ArrayList<Offset>()
        repeat(700) { val q = CamCentro.p(GeoCentro.aro(az1.entre(8.25f, 9.6f), G1 + 0.002f, az1.entre(-112f, -72f))); if (CamCentro.enCuadro(q, 0f)) { if (it % 2 == 0) claras += q else oscuras += q } }
        drawPoints(claras, androidx.compose.ui.graphics.PointMode.Points, lerp(C.TELA, Color.White, 0.2f).copy(alpha = 0.16f), 0.8f, StrokeCap.Round)
        drawPoints(oscuras, androidx.compose.ui.graphics.PointMode.Points, lerp(C.SOMBRA, C.NEGRO, 0.3f).copy(alpha = 0.18f), 0.8f, StrokeCap.Round)
        for (rr in listOf(8.55f, 9.3f)) PiezasCentro.run { trazo(v, PiezasCentro.arco(rr, G1 + 0.002f, -112f, -50f, 30), lerp(C.SOMBRA, C.PISO, 0.4f), ARRIBA, 0.012f, 0.4f) }
        // El paso gastado y las rayas de las sillas, junto a la barandilla.
        val az = Azar(Semilla.de("laboratorio", "centro", "esquina suelo"))
        repeat(16) {
            val w = GeoCentro.aro(az.entre(7.95f, 8.9f), G1, az.entre(-104f, -60f)); val q = CamCentro.p(w); if (!CamCentro.enCuadro(q, 0f)) return@repeat
            val k = CamCentro.k(w)
            mancha(q, az.entre(0.15f, 0.45f) * k, az.entre(0.03f, 0.08f) * k, v.c(w, lerp(C.PISO, C.SOMBRA, 0.3f), ARRIBA), 0.18f)
        }
    }

    /**
     * Las sombras en el suelo de la galería, en una capa: la luz del hall entra por el vacío, así que la mesa, la silla y los
     * montantes de la barandilla tumban su sombra hacia quien mira.
     */
    private fun DrawScope.sombras(v: VistaCentro) {
        val luz = V3(0f, 8.5f, 0f)
        fun alSuelo(q: V3): Offset { val s = ((q.y - G1) / (luz.y - q.y)).coerceIn(0f, 4f); return CamCentro.p(V3(q.x + (q.x - luz.x) * s, G1, q.z + (q.z - luz.z) * s)) }
        capaSombra(rayoFijo(0f, 55f, (0.45f * (1f - 0.5f * v.noche)).coerceIn(0f, 1f), C.SOMBRA)) { c ->
            drawPath(poligono(envolvente(PiezasCentro.esquinas(MESA, U_MESA, LADO, LADO, ALTO, ALTO - 0.04f).map { alSuelo(it) })), c.copy(alpha = 0.75f))
            drawPath(poligono(envolvente(PiezasCentro.esquinas(SILLA, uSilla(), 0.42f, 0.42f, 0.46f).map { alSuelo(it) })), c.copy(alpha = 0.55f))
            // Los montantes de la barandilla: rayas cortas al pie del vidrio.
            var g = -104f
            while (g < -40f) { drawLine(c.copy(alpha = 0.5f), alSuelo(GeoCentro.aro(GeoCentro.RV - 0.06f, G1, g)), alSuelo(GeoCentro.aro(GeoCentro.RV - 0.06f, G1 + GeoCentro.BARANDA, g)), 1.2f); g += 6f }
            // Bajo la mesa, la sombra de contacto.
            drawPath(poligono(envolvente(PiezasCentro.esquinas(MESA, U_MESA, LADO, LADO, 0.02f).map { CamCentro.p(it) })), c.copy(alpha = 0.6f))
        }
    }

    // ---------------------------------------------------------------- La mesa chica y la silla

    /**
     * La silla, del lado de la barandilla, de espaldas al vacío («sola, de espaldas al ruido»), apartada de la mesa y a
     * escuadra con ella: su respaldo mira al hall.
     */
    private fun uSilla(): V3 { val w = V3(-cos(GeoCentro.rad(-107.0f)), 0f, -sin(GeoCentro.rad(-107.0f))); return V3(w.z, 0f, -w.x) }

    /** La silla apartada de la mesa, a escuadra con ella: asiento, respaldo y patas de metal. */
    private fun DrawScope.silla(v: VistaCentro) {
        val u = uSilla(); val w = V3(-u.z, 0f, u.x)
        val metal = lerp(C.ACERO, C.NEGRO, 0.15f)
        val asiento = lerp(C.MADERA, C.TELA, 0.25f)
        for ((a, b) in listOf(-0.18f to -0.17f, 0.18f to -0.17f, 0.18f to 0.17f, -0.18f to 0.17f)) {
            val p = SILLA + u * a + w * b
            linea(v, p, p + V3(0f, 0.44f, 0f), metal, V3(0f, 0f, -1f), (0.022f * CamCentro.k(p)).coerceIn(0.6f, 4f))
        }
        caja(v, SILLA + V3(0f, 0.42f, 0f), u, 0.44f, 0.42f, 0.035f, lerp(asiento, C.NEGRO, 0.2f), tapa = asiento)
        // El respaldo, del lado de la barandilla: dos montantes de metal y una tabla curvada, más clara arriba (donde se agarra).
        val r0 = SILLA + w * 0.19f
        for (s in listOf(-0.18f, 0.18f)) linea(v, r0 + u * s + V3(0f, 0.45f, 0f), r0 + u * s + V3(0f, 0.84f, 0f), metal, w * -1f, (0.02f * CamCentro.k(r0)).coerceIn(0.5f, 3f))
        caja(v, r0 + V3(0f, 0.66f, 0f), u, 0.42f, 0.025f, 0.17f, asiento, tapa = lerp(asiento, Color.White, 0.3f))
        linea(v, r0 + u * -0.2f + V3(0f, 0.835f, 0f), r0 + u * 0.2f + V3(0f, 0.835f, 0f), lerp(asiento, Color.White, 0.35f), V3(0f, 1f, 0f), 0.6f, 0.8f)
    }

    /** La mesa chica: el tablero de madera con su canto, más clara donde se apoyan los brazos, y las patas de metal. */
    private fun DrawScope.mesa(v: VistaCentro) {
        val w = V3(-U_MESA.z, 0f, U_MESA.x)
        val metal = lerp(C.ACERO, C.NEGRO, 0.25f)
        for ((a, b) in listOf(-0.32f to -0.32f, 0.32f to -0.32f, 0.32f to 0.32f, -0.32f to 0.32f)) {
            val p = MESA + U_MESA * a + w * b
            linea(v, p, p + V3(0f, ALTO - 0.04f, 0f), metal, V3(0f, 0f, -1f), (0.035f * CamCentro.k(p)).coerceIn(0.8f, 7f))
        }
        val madera = lerp(C.MADERA, C.BARRO, 0.12f)
        caja(v, MESA + V3(0f, ALTO - 0.04f, 0f), U_MESA, LADO, LADO, 0.04f, lerp(madera, C.NEGRO, 0.15f), tapa = lerp(madera, C.TELA, 0.12f))
        // Las vetas y el desgaste del tablero.
        val az = Azar(Semilla.de("laboratorio", "centro", "mesa chica"))
        for (i in 0 until 9) {
            val b = -0.33f + 0.66f * i / 8f + az.entre(-0.01f, 0.01f)
            linea(v, enLaMesa(-0.36f, b, 0.001f), enLaMesa(0.36f, b + az.entre(-0.02f, 0.02f), 0.001f), lerp(madera, C.NEGRO, 0.35f), ARRIBA, 0.5f, 0.25f)
        }
        val q = CamCentro.p(enLaMesa(-0.1f, -0.25f)); val k = CamCentro.k(MESA)
        mancha(q, 0.22f * k, 0.06f * k, v.c(enLaMesa(0f, 0f), lerp(madera, C.TELA, 0.4f), ARRIBA), 0.3f)
    }

    /** Lo de encima, según la hora: la bandeja de cuatro huecos (en el desayuno y la comida), la taza, los auriculares y la tableta. */
    private fun DrawScope.encima(v: VistaCentro) {
        val hoy = v.m.ficha.objeto
        fun pinta(id: String) = !(hoy == id && id in ObjetosLaboratorio.DIBUJADOS)
        val come = v.mesa == Mesa.DESAYUNO || v.mesa == Mesa.COMIDA || v.mesa == Mesa.FIESTA
        if (come && !EscenaLaboratorio.arriba(v.m)) bandeja(v)
        if (pinta("tableta")) tableta(v)
        if (pinta("auriculares") && !EscenaLaboratorio.arriba(v.m)) auriculares(v)
        if (pinta("taza")) taza(v)
        if (v.velas) {
            cilindro(v, LAMPARA - V3(0f, 0.12f, 0f), 0.03f, 0.12f, C.TELA, lerp(C.TELA, C.AMARILLO, 0.2f))
            val q = CamCentro.p(LAMPARA + V3(0f, 0.03f, 0f)); val k = CamCentro.k(LAMPARA)
            drawOval(lerp(C.VELA, Color.White, 0.5f), q - Offset(0.012f * k, 0.035f * k), androidx.compose.ui.geometry.Size(0.024f * k, 0.045f * k))
            mancha(q, 0.12f * k, 0.12f * k, C.VELA, 0.3f)
        }
    }

    /** La bandeja de cuatro huecos: en cada hueco, lo suyo (la ensalada de los jardines, el tomate, el puré, el pan). */
    private fun DrawScope.bandeja(v: VistaCentro) {
        val o = enLaMesa(-0.08f, 0.1f)
        caja(v, o, U_MESA, 0.42f, 0.32f, 0.025f, lerp(C.ACERO, C.CIAN, 0.2f), tapa = lerp(C.ACERO, Color.White, 0.35f))
        val comidas = if (v.mesa == Mesa.DESAYUNO) listOf(C.TELA, C.TOMATE, lerp(C.MADERA, C.TELA, 0.4f), C.GRAMA) else listOf(C.GRAMA, C.TOMATE, lerp(C.TELA, C.AMARILLO, 0.2f), lerp(C.MADERA, C.TELA, 0.4f))
        for ((i, col) in comidas.withIndex()) {
            val a = if (i % 2 == 0) -0.1f else 0.1f; val b = if (i < 2) -0.07f else 0.07f
            val c = enLaMesa(-0.08f + a, 0.1f + b, 0.026f)
            val hueco = listOf(c + U_MESA * -0.085f + V3(-U_MESA.z, 0f, U_MESA.x) * -0.055f, c + U_MESA * 0.085f + V3(-U_MESA.z, 0f, U_MESA.x) * -0.055f, c + U_MESA * 0.085f + V3(-U_MESA.z, 0f, U_MESA.x) * 0.055f, c + U_MESA * -0.085f + V3(-U_MESA.z, 0f, U_MESA.x) * 0.055f)
            cara(v, hueco, lerp(C.ACERO, C.SOMBRA, 0.3f), ARRIBA, siempre = true)
            // Lo que queda en el hueco (en la comida, a medias).
            val q = CamCentro.p(c); val k = CamCentro.k(c)
            val queda = if (v.mesa == Mesa.COMIDA) 0.55f else 1f
            mancha(q, 0.06f * k * queda, 0.025f * k * queda, v.c(c, col, ARRIBA), 0.95f)
            if (col == C.TOMATE) drawCircle(v.c(c, lerp(C.TOMATE, Color.White, 0.2f), ARRIBA), 0.018f * k, q + Offset(0.01f * k, -0.006f * k))
        }
    }

    /** La taza de barro gris, de borde grueso y con la muesca; la infusión que se le enfría siempre. */
    private fun DrawScope.taza(v: VistaCentro) {
        val c = enLaMesa(0.22f, -0.24f)
        val barro = lerp(C.TIERRA, C.BARRO, 0.25f)
        cilindro(v, c, 0.045f, 0.09f, barro, lerp(barro, C.NEGRO, 0.2f), rArriba = 0.048f)
        val q = CamCentro.p(c + V3(0f, 0.088f, 0f)); val k = CamCentro.k(c)
        drawOval(v.c(c, lerp(C.MADERA, C.GRAMA, 0.3f), ARRIBA), q - Offset(0.038f * k, 0.012f * k), androidx.compose.ui.geometry.Size(0.076f * k, 0.024f * k))
        // El asa y la muesca del borde.
        val a = CamCentro.p(c + U_MESA * 0.05f + V3(0f, 0.07f, 0f)); val b = CamCentro.p(c + U_MESA * 0.05f + V3(0f, 0.025f, 0f))
        drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x + 0.04f * k, a.y, b.x + 0.04f * k, b.y, b.x, b.y) }, v.c(c, barro, V3(1f, 0f, -1f)), style = Stroke(0.012f * k, cap = StrokeCap.Round))
        drawLine(v.c(c, lerp(barro, C.NEGRO, 0.4f), ARRIBA), q + Offset(-0.03f * k, -0.006f * k), q + Offset(-0.02f * k, -0.003f * k), 0.012f * k)
    }

    /** Los auriculares grandes, de los que tapan la oreja entera, con la espuma cambiada tres veces: echados en la mesa. */
    private fun DrawScope.auriculares(v: VistaCentro) {
        val c1 = enLaMesa(-0.22f, -0.24f, 0.03f); val c2 = enLaMesa(-0.02f, -0.3f, 0.03f)
        val k = CamCentro.k(c1)
        val negro = lerp(C.NEGRO, C.ACERO, 0.15f)
        val p1 = CamCentro.p(c1); val p2 = CamCentro.p(c2)
        // La diadema, en arco, y las dos copas.
        val medio = Offset((p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f - 0.07f * k)
        drawPath(Path().apply { moveTo(p1.x, p1.y); cubicTo(p1.x, medio.y, p2.x, medio.y, p2.x, p2.y) }, v.c(c1, negro, ARRIBA), style = Stroke(0.02f * k, cap = StrokeCap.Round))
        for (p in listOf(p1, p2)) {
            drawOval(v.c(c1, negro, V3(0f, 0.5f, -1f)), p - Offset(0.05f * k, 0.03f * k), androidx.compose.ui.geometry.Size(0.1f * k, 0.06f * k))
            drawOval(v.c(c1, lerp(C.NEGRO, C.SOMBRA, 0.4f), ARRIBA), p - Offset(0.033f * k, 0.02f * k), androidx.compose.ui.geometry.Size(0.066f * k, 0.04f * k))
            drawCircle(lerp(C.ACERO, Color.White, 0.4f).copy(alpha = 0.6f), 0.008f * k, p + Offset(-0.03f * k, -0.012f * k))
        }
    }

    /** La tableta de vidrio negro, más vieja que ella, con una esquina pegada con resina. */
    private fun DrawScope.tableta(v: VistaCentro) {
        val ps = listOf(enLaMesa(0.02f, 0.28f, 0.002f), enLaMesa(0.27f, 0.28f, 0.002f), enLaMesa(0.27f, 0.1f, 0.002f), enLaMesa(0.02f, 0.1f, 0.002f))
        cara(v, ps, C.NEGRO, ARRIBA, siempre = true)
        val q = ps.map { CamCentro.p(it) }
        drawLine(Color.White.copy(alpha = 0.18f), q[0], q[2], 0.8f)
        // La esquina de resina, ámbar.
        val r = listOf(enLaMesa(0.22f, 0.28f, 0.003f), enLaMesa(0.27f, 0.28f, 0.003f), enLaMesa(0.27f, 0.23f, 0.003f))
        cara(v, r, lerp(C.LATON, C.BARRO, 0.3f), ARRIBA, siempre = true)
    }

    /** El carro de las bandejas, a la derecha, cortado por el borde: un armazón de acero con tres baldas de bandejas usadas (vacío en la cena). */
    private fun DrawScope.carro(v: VistaCentro) {
        val o = GeoCentro.aro(9.25f, G1, -81.5f); val a = GeoCentro.rad(-81.5f + 90f); val u = V3(cos(a), 0f, sin(a)); val w = V3(-u.z, 0f, u.x)
        val metal = lerp(C.ACERO, C.HORMIGON, 0.15f)
        val llenas = when (v.mesa) { Mesa.COMIDA, Mesa.FIESTA -> 3; Mesa.DESAYUNO -> 2; Mesa.CENA, Mesa.CENSO -> 0 }
        for ((i, y) in listOf(0.18f, 0.55f, 0.92f).withIndex()) {
            caja(v, o + V3(0f, y, 0f), u, 0.9f, 0.5f, 0.025f, metal, tapa = lerp(metal, Color.White, 0.2f))
            if (i < llenas) for (j in 0 until 5) caja(v, o + V3(0f, y + 0.03f + j * 0.035f, 0f) + u * ((j % 2) * 0.04f - 0.02f), u, 0.46f, 0.36f, 0.03f, lerp(C.TELA, C.CIAN, 0.2f), tapa = lerp(C.TELA, Color.White, 0.3f))
        }
        for ((sa, sb) in listOf(-1f to -1f, 1f to -1f, 1f to 1f, -1f to 1f)) {
            val p = o + u * (0.44f * sa) + w * (0.24f * sb)
            linea(v, p + V3(0f, 0.08f, 0f), p + V3(0f, 1.02f, 0f), lerp(metal, C.NEGRO, 0.25f), w * -1f, (0.022f * CamCentro.k(p)).coerceIn(0.6f, 3f))
            cilindro(v, p, 0.045f, 0.08f, C.NEGRO, null)
        }
        linea(v, o + u * -0.46f + w * -0.26f + V3(0f, 1.02f, 0f), o + u * -0.46f + w * 0.26f + V3(0f, 1.02f, 0f), lerp(metal, Color.White, 0.3f), PiezasCentro.ARRIBA, 1.4f)
    }

    /** Arriba: dos sillas arrimadas a la barandilla, de cara a la pared de pantallas, una de ellas un poco ladeada. */
    private fun DrawScope.sillasArrimadas(v: VistaCentro) {
        for ((i, g) in listOf(-86f, -82.5f).withIndex()) {
            val p = GeoCentro.aro(8.3f, G1, g); val a = GeoCentro.rad(g + 90f + i * 12f)
            caja(v, p + V3(0f, 0.43f, 0f), V3(cos(a), 0f, sin(a)), 0.42f, 0.42f, 0.04f, lerp(C.CIAN, C.ACERO, 0.45f))
            for (s in listOf(-0.18f, 0.18f)) { val q = p + V3(cos(a), 0f, sin(a)) * s; linea(v, q, q + V3(0f, 0.43f, 0f), C.ACERO, V3(0f, 0f, -1f), 0.8f) }
        }
    }
}
