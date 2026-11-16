package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.ARRIBA
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.IZQ
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.Marco
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.cajaEn
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.cilindro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.palo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * Lo que hay en el suelo del laboratorio fuera de la mesa y de la campana: el estante de las semillas bajo la pantalla (con
 * sus frascos, el saco y las macetas de barro apiladas), la caja de tierra de arriba abierta con su pala, el cubo y el
 * taburete de Lauro; y lo que dejan los estados: el catre, los cubos de lejía, la sábana y la mascarilla de la cuarentena,
 * la cinta amarilla y la silla del vigía del cierre, el taburete pegado al vidrio del experimento, y las banderas de papel
 * de la Fundación.
 */
internal object CosasLab {
    private val C = ColorLaboratorio

    /** El taburete de Lauro: junto a la mesa; en el experimento, pegado al vidrio de la campana. */
    fun taburete(m: Momento): V3 = if (EscenaLaboratorio.enLaCampana(m)) V3(1.05f, 0f, 3.75f) else V3(1.15f, 0f, 3.02f)

    /** La caja de tierra de arriba, abierta, y el cubo, en el suelo, delante del estante de las semillas. */
    val CAJA = V3(2.25f, 0f, 2.5f)
    val CUBO = V3(2.55f, 0f, 2.28f)

    fun cosas(v: VistaLab): List<CosaLab> = buildList {
        val m = v.m
        add(CosaLab(CamLab.hondo(2.8f, 3.6f), caja3(2.62f, 2.98f, 0f, SalaLab.EY, SalaLab.EZ0, SalaLab.EZ1)) { estanteDeSemillas(v) })
        add(CosaLab(CamLab.hondo(CAJA.x, CAJA.z), caja3(CAJA.x - 0.26f, CAJA.x + 0.26f, 0f, 0.3f, CAJA.z - 0.18f, CAJA.z + 0.18f)) { cajaDeTierra(v) })
        add(CosaLab(CamLab.hondo(CUBO.x, CUBO.z), PiezasLab.aro(CUBO, 0.15f, 8) + PiezasLab.aro(CUBO + V3(0f, 0.32f, 0f), 0.17f, 8)) { cubo(v, CUBO, lerp(C.ACERO, C.HORMIGON, 0.3f)) })
        val t = taburete(m)
        add(CosaLab(CamLab.hondo(t.x, t.z), PiezasLab.aro(t, 0.17f, 8) + PiezasLab.aro(t + V3(0f, 0.62f, 0f), 0.17f, 8)) { taburete(v, t) })
        if (EscenaLaboratorio.cuarentena(m)) {
            val catre = Marco(V3(-0.1f, 0f, 6.55f), V3(1f, 0f, -0.02f))
            add(CosaLab(CamLab.hondo(0.6f, 6.3f), listOf(catre.w(0f, 0f, 0f), catre.w(1.9f, 0f, 0f), catre.w(1.9f, 0.7f, 0f), catre.w(0f, 0.7f, 0f), catre.w(0f, 0f, 0.45f), catre.w(1.9f, 0.7f, 0.45f))) { catre(v, catre) })
            for ((i, q) in listOf(V3(-0.85f, 0f, 6.55f), V3(-0.5f, 0f, 6.7f)).withIndex()) add(CosaLab(CamLab.hondo(q.x, q.z), PiezasLab.aro(q, 0.14f, 8) + PiezasLab.aro(q + V3(0f, 0.3f, 0f), 0.16f, 8)) { cubo(v, q, if (i == 0) C.TELA else lerp(C.TELA, C.AMARILLO, 0.2f)) })
        }
        if (EscenaLaboratorio.cierre(m)) {
            val s = V3(0.65f, 0f, 6.55f)
            add(CosaLab(CamLab.hondo(s.x, s.z), caja3(s.x - 0.22f, s.x + 0.22f, 0f, 0.9f, s.z - 0.22f, s.z + 0.22f)) { silla(v, s) })
        }
    }

    /** Los ocho puntos de una caja, para su sombra. */
    fun caja3(x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float): List<V3> = listOf(V3(x0, y0, z0), V3(x1, y0, z0), V3(x1, y0, z1), V3(x0, y0, z1), V3(x0, y1, z0), V3(x1, y1, z0), V3(x1, y1, z1), V3(x0, y1, z1))

    // ---------------------------------------------------------------- El estante de las semillas

    /**
     * El estante de las semillas, bajo la pantalla: un mueble de acero de tres tablas, con frascos de semillas en fila, el
     * saco de grama atado y las macetas de barro apiladas encima («una que ya ha sido maceta cuatro veces»). En el cierre,
     * precintado con cinta amarilla, y con el hueco del saco que se llevó Lauro.
     */
    private fun DrawScope.estanteDeSemillas(v: VistaLab) {
        val x0 = SalaLab.EX; val x1 = SalaLab.XD - 0.02f; val z0 = SalaLab.EZ0; val z1 = SalaLab.EZ1
        val acero = lerp(C.ACERO, C.HORMIGON, 0.25f)
        val tablas = listOf(0.1f, 0.5f, SalaLab.EY)
        // Los montantes y las tablas.
        for (z in listOf(z0, z1)) for (x in listOf(x0)) palo(v, V3(x, 0f, z), V3(x, SalaLab.EY, z), 0.03f, acero)
        for (y in tablas) PiezasLab.run { caja(v, x0, x1, y - 0.025f, y, z0, z1, acero) }
        // Los frascos de semillas, en fila, cada uno con su cinta.
        val az = Azar(Semilla.de("laboratorio", "frascos"))
        for ((ti, y) in listOf(0.1f, 0.5f).withIndex()) {
            var z = z1 - 0.12f
            while (z > z0 + 0.1f) {
                val saco = ti == 1 && z < z0 + 0.62f && z > z0 + 0.12f
                if (!saco) {
                    val c = V3(x0 + 0.15f, y, z); val alto = 0.2f + 0.04f * az.real()
                    cilindro(v, c, 0.055f, alto, lerp(C.VIDRIO, C.TIERRA, 0.12f), lerp(C.NEGRO, C.ACERO, 0.3f))
                    // Lo de dentro: semillas pardas hasta su altura.
                    val dentro = PiezasLab.mitad(c + V3(0f, alto * (0.4f + 0.4f * az.real()), 0f), 0.05f, true).map { CamLab.p(it) } + PiezasLab.mitad(c + V3(0f, 0.01f, 0f), 0.05f, true).reversed().map { CamLab.p(it) }
                    drawPath(poligono(dentro), v.c(c, lerp(C.MADERA, C.TIERRA, 0.3f + 0.3f * az.real()), IZQ).copy(alpha = 0.85f))
                    drawLine(v.c(c, if (az.real() > 0.6f) C.AMARILLO else C.CIAN, IZQ), CamLab.p(c.x - 0.055f, y + alto * 0.55f, z), CamLab.p(c.x - 0.055f, y + alto * 0.7f, z), 1f)
                }
                z -= 0.15f
            }
        }
        // El saco de semillas de grama, atado, en la tabla del medio (en el cierre, solo el hueco con el polvo).
        val saco = V3(x0 + 0.17f, 0.5f, z0 + 0.36f)
        if (!EscenaLaboratorio.cierre(v.m)) {
            val p = CamLab.p(saco); val k = CamLab.k(saco)
            val forma = Path().apply { moveTo(p.x - 0.14f * k, p.y); cubicTo(p.x - 0.16f * k, p.y - 0.2f * k, p.x - 0.1f * k, p.y - 0.3f * k, p.x - 0.03f * k, p.y - 0.34f * k)
                lineTo(p.x + 0.03f * k, p.y - 0.34f * k); cubicTo(p.x + 0.1f * k, p.y - 0.3f * k, p.x + 0.16f * k, p.y - 0.2f * k, p.x + 0.14f * k, p.y); close() }
            drawPath(forma, androidx.compose.ui.graphics.Brush.horizontalGradient(0f to v.c(saco, lerp(C.MADERA, C.AMARILLO, 0.3f), V3(-1f, 0.2f, -0.6f)), 1f to v.c(saco, lerp(C.MADERA, C.TIERRA, 0.3f), V3(0.2f, 0.2f, -1f)), startX = p.x - 0.15f * k, endX = p.x + 0.15f * k))
            drawLine(v.c(saco, C.MADERA, IZQ), Offset(p.x - 0.04f * k, p.y - 0.3f * k), Offset(p.x + 0.04f * k, p.y - 0.3f * k), 0.02f * k)
        } else {
            mancha(CamLab.p(saco), 0.12f * CamLab.k(saco), 0.03f * CamLab.k(saco), lerp(C.TIERRA, C.HORMIGON, 0.3f), 0.5f)
        }
        // Las macetas de barro apiladas encima, en dos torres.
        for ((j, z) in listOf(z0 + 0.35f, z0 + 0.75f).withIndex()) for (i in 0 until 4 - j) {
            val c = V3(x0 + 0.18f, SalaLab.EY + i * 0.07f, z); cilindro(v, c, 0.1f - 0.004f * i, 0.11f, if ((i + j) % 3 == 2) lerp(C.BARRO, C.TIERRA, 0.3f) else C.BARRO, tapa = if (i == 3 - j) lerp(C.BARRO, C.NEGRO, 0.4f) else null)
        }
        // El cierre: la cinta amarilla, en cruz, por delante del estante.
        if (EscenaLaboratorio.cierre(v.m)) for ((a, b) in listOf(V3(x0 - 0.01f, 0.05f, z0) to V3(x0 - 0.01f, SalaLab.EY, z1), V3(x0 - 0.01f, SalaLab.EY, z0) to V3(x0 - 0.01f, 0.05f, z1)))
            drawLine(v.c((a + b) * 0.5f, C.AMARILLO, IZQ), CamLab.p(a), CamLab.p(b), (0.035f * CamLab.k((a + b) * 0.5f)).coerceAtLeast(1f))
    }

    /** La caja de madera con tierra gris de arriba, abierta, con su pala de mano clavada. */
    private fun DrawScope.cajaDeTierra(v: VistaLab) {
        val mk = Marco(CAJA + V3(-0.25f, 0f, -0.15f), V3(0.98f, 0f, 0.2f).let { val l = kotlin.math.sqrt(it.x * it.x + it.z * it.z); V3(it.x / l, 0f, it.z / l) })
        cajaEn(v, mk, 0f, 0.52f, -0.34f, 0f, 0f, 0.3f, C.MADERA) { cara, f ->
            if (cara == 3) drawPath(f, v.c(mk.w(0.26f, -0.17f, 0.3f), C.TIERRA, ARRIBA))
            if (cara == 0 || cara == 2) for (t in 1..2) drawLine(v.c(mk.w(0f, 0f, 0.15f), lerp(C.MADERA, C.NEGRO, 0.4f), mk.n(-1f, 0f, 0f)).copy(alpha = 0.6f), mk.p(if (cara == 0) 0f else 0.02f, if (cara == 0) -0.02f else 0f, 0.3f * t / 3f), mk.p(if (cara == 0) 0f else 0.5f, if (cara == 0) -0.32f else 0f, 0.3f * t / 3f), 0.5f)
        }
        // La tierra amontonada y la pala de mano, clavada, con su mango de madera.
        val c = mk.w(0.3f, -0.18f, 0.31f); val p = CamLab.p(c); val k = CamLab.k(c)
        mancha(p, 0.14f * k, 0.035f * k, v.c(c, lerp(C.TIERRA, Color.White, 0.15f), ARRIBA), 0.7f)
        palo(v, c + V3(0f, 0.02f, 0f), c + V3(0.06f, 0.2f, -0.04f), 0.025f, C.MADERA)
        drawPath(poligono(p + Offset(-0.03f * k, 0f), p + Offset(0.03f * k, 0f), p + Offset(0.02f * k, 0.04f * k), p + Offset(-0.02f * k, 0.04f * k)), v.c(c, C.ACERO, IZQ))
    }

    /** Un cubo de chapa con su asa. */
    private fun DrawScope.cubo(v: VistaLab, c: V3, propio: Color) {
        cilindro(v, c, 0.15f, 0.32f, propio, tapa = lerp(propio, C.NEGRO, 0.3f))
        val a = CamLab.p(c + V3(-0.15f, 0.3f, 0f)); val b = CamLab.p(c + V3(0.15f, 0.3f, 0f)); val k = CamLab.k(c)
        drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x, a.y - 0.18f * k, b.x, b.y - 0.18f * k, b.x, b.y) }, v.c(c, C.ACERO, PiezasLab.FRENTE), style = Stroke(0.6f))
    }

    /** El taburete de Lauro: asiento redondo de madera sobre tres patas de acero, con un aro para los pies. */
    private fun DrawScope.taburete(v: VistaLab, t: V3) {
        val alto = 0.62f
        for (i in 0..2) { val a = i * 2.094f + 0.5f; palo(v, t + V3(0.17f * kotlin.math.cos(a), 0f, 0.17f * kotlin.math.sin(a)), t + V3(0.1f * kotlin.math.cos(a), alto, 0.1f * kotlin.math.sin(a)), 0.022f, C.ACERO) }
        val aro = PiezasLab.mitad(t + V3(0f, 0.22f, 0f), 0.14f, true).map { CamLab.p(it) }
        for (i in 0 until aro.size - 1) drawLine(v.c(t + V3(0f, 0.22f, 0f), C.ACERO, PiezasLab.FRENTE), aro[i], aro[i + 1], 0.8f)
        cilindro(v, t + V3(0f, alto, 0f), 0.17f, 0.04f, C.MADERA)
    }

    /** El catre de la cuarentena, junto a la puerta, con la sábana revuelta; la mascarilla de tela colgada en su cabecera. */
    private fun DrawScope.catre(v: VistaLab, mk: Marco) {
        cajaEn(v, mk, 0f, 1.9f, 0f, 0.7f, 0.36f, 0.42f, lerp(C.TELA, C.CIAN, 0.15f))
        cajaEn(v, mk, 0.15f, 1.3f, 0.05f, 0.65f, 0.42f, 0.5f, lerp(C.TELA, C.TIERRA, 0.2f))
        for (a in listOf(0.05f, 1.85f)) for (b in listOf(0.05f, 0.65f)) palo(v, mk.w(a, b, 0f), mk.w(a, b, 0.36f), 0.02f, C.ACERO, filo = false)
        // La sábana en el suelo, ante la puerta.
        drawPath(poligono(listOf(V3(-0.7f, 0.003f, 6.2f), V3(0.35f, 0.003f, 6.1f), V3(0.45f, 0.003f, 5.8f), V3(-0.6f, 0.003f, 5.75f)).map { CamLab.p(it) }), v.c(V3(0f, 0f, 6f), C.TELA, ARRIBA))
        val p = CamLab.p(mk.w(1.88f, 0.35f, 0.7f)); val k = CamLab.k(mk.w(1.88f, 0.35f, 0.7f))
        drawRect(v.c(mk.w(1.9f, 0.35f, 0.7f), lerp(C.TELA, C.CIAN, 0.25f), PiezasLab.FRENTE), p - Offset(0.06f * k, 0f), Size(0.12f * k, 0.07f * k))
    }

    /** La silla del vigía, junto a la puerta, en el cierre. */
    private fun DrawScope.silla(v: VistaLab, s: V3) {
        val mk = Marco(s + V3(-0.2f, 0f, -0.2f), V3(1f, 0f, 0f))
        for (a in listOf(0.02f, 0.38f)) for (b in listOf(-0.02f, -0.38f)) palo(v, mk.w(a, b, 0f), mk.w(a, b, 0.45f), 0.02f, C.ACERO, filo = false)
        cajaEn(v, mk, 0f, 0.4f, -0.4f, 0f, 0.45f, 0.48f, C.MADERA)
        cajaEn(v, mk, 0f, 0.4f, -0.4f, -0.37f, 0.48f, 0.9f, C.MADERA)
    }

    // ---------------------------------------------------------------- Lo de los días

    /**
     * Las banderas de papel de la Fundación, colgadas de una cuerda entre el cable de una lámpara y la campana, con los
     * colores de los departamentos; la de Botánica, verde, la más grande y en medio.
     */
    fun DrawScope.adornos(v: VistaLab) {
        if (EscenaLaboratorio.fundacion(v.m)) {
            val a = CamLab.p(MesaLab.lampara(4).x, 2.6f, MesaLab.lampara(4).z); val b = CamLab.p(CampanaLab.CENTRO.x - 0.6f, 2.75f, CampanaLab.CENTRO.z - 0.7f)
            banderines(v.l, a, b, 14f, listOf(C.CIAN, lerp(C.AMARILLO, C.TELA, 0.2f), C.GRAMA, lerp(C.AVISO, C.TELA, 0.3f), C.VIOLETA), 9f)
        }
        // La noche larga: los cestos de fruta seca y las velas (las llamas, en la parte viva).
        if (EscenaLaboratorio.velas(v.m) || EscenaLaboratorio.nocheLarga(v.m)) for ((a, b) in LucesLab.VELAS) {
            val c = MesaLab.MK.w(a, b, MesaLab.ALTO); cilindro(v, c, 0.025f, 0.12f, lerp(C.VELA, C.AMARILLO, 0.3f))
        }
        // El apagón: las linternas de manivela, cargándose en la mesa.
        if (EscenaLaboratorio.apagon(v.m)) for (i in 0..1) { val c = MesaLab.MK.w(0.9f + 0.25f * i, 0.3f, MesaLab.ALTO)
            cajaEn(v, Marco(c, MesaLab.MK.u), 0f, 0.18f, -0.05f, 0.05f, 0f, 0.09f, lerp(C.AMARILLO, C.NEGRO, 0.4f)) }
    }
}
