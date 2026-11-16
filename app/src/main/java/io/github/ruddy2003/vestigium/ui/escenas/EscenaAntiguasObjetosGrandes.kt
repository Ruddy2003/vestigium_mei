package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.contacto
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.empolvado
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.hoyo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.labio
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.punto
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.marca
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.sombra
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.trazo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.tubo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Los cinco objetos grandes de Brenna (`ObjetosAntiguas.grande`): de pie en las losas delante del banco, a la izquierda de
 * la puerta, y recostados o colgados en el muro del zaguán. Cada uno se entrega como [Cosa]: su silueta (para la sombra que
 * el cuadro tumba en el suelo de delante del pie), su pie y cómo se pinta; la sombra en el muro de detrás y en lo que el
 * cuadro no alcanza la pinta el propio objeto. Los cinco quedan entre la esquina del zaguán y la jamba de la puerta, a la
 * derecha del pie, para no meterse bajo la cabeza de las mulas cuando están atadas a la argolla.
 */
internal object GrandesAntiguas {
    private val C = ColorAntiguas
    private val O = ColorObjetos

    /** De noche les llega la lumbre de la puerta, a su derecha y algo por delante (en el mundo). */
    private val LUMBRE = V3(0.79f, 0.15f, -0.6f)

    /** La esquina del zaguán y la jamba de la izquierda de la puerta, a lo largo del muro desde el pie (en metros, en los ejes del muro). */
    private const val ESQUINA = -0.43f
    private const val JAMBA = 0.62f

    fun cosa(id: String, p: Pincel, pie: Offset, apoyo: Offset, escala: Float, e: EntornoObjeto): Cosa? {
        val lm = LuzObjeto(Sitio(SitioAntiguas.MIRADA, pie, escala * 48f, LuzObjeto.GIRO_MURO), p, LUMBRE, e)
        val zm = muroDesde(lm, apoyo)
        return when (id) {
            "baston" -> baston(lm, zm)
            "cuerda" -> CuerdaYCapa.cuerda(lm, zm)
            "capa" -> CuerdaYCapa.capa(lm, zm)
            "montura" -> montura(lm, zm)
            "botas" -> botas(lm, zm)
            else -> null
        }
    }

    // ---------------------------------------------------------------- Ayudas

    /** A cuántos metros del pie está el muro, según el punto de [apoyo] (que está en él); si no sale, 0,44 (el del cuadro). */
    private fun muroDesde(lm: LuzObjeto, apoyo: Offset): Float {
        val s = lm.s; val f = s.v.focal; val dx = apoyo.x - s.v.centro
        val cg = cos(s.giro); val sg = sin(s.giro)
        val den = dx * cg + f * sg
        if (kotlin.math.abs(den) < 1e-3f) return 0.44f
        val z = (f * s.mx(0f, 0f) - dx * s.z0) / den
        return if (z.isFinite() && z in 0.15f..1.2f) z else 0.44f
    }

    /** El muro del zaguán (entre la esquina y la jamba) y el suelo de delante, donde caen las sombras. */
    fun muro(lm: LuzObjeto, zm: Float): Path = poligono(lm.q(ESQUINA, 0f, zm), lm.q(JAMBA, 0f, zm), lm.q(JAMBA, 2.3f, zm), lm.q(ESQUINA, 2.3f, zm))
    fun suelo(lm: LuzObjeto, zm: Float): Path = poligono(lm.q(-1.5f, 0f, -0.9f), lm.q(1.5f, 0f, -0.9f), lm.q(1.5f, 0f, zm), lm.q(-1.5f, 0f, zm))

    /** La sombra de una cosa grande en el muro y en el suelo que el cuadro no pinta (de su pie hacia el fondo). */
    fun DrawScope.sombraGrande(lm: LuzObjeto, zm: Float, puntos: List<V3>) = sombra(lm, puntos, zm, suelo(lm, zm), muro(lm, zm), arriba = lm.s.pie.y - 1f)

    /** La silueta de una cosa en el lienzo: la envolvente de sus puntos. */
    fun silueta(lm: LuzObjeto, puntos: List<V3>): Path = poligono(envolvente(puntos.map { lm.q(it) }))

    /** Un punto de una cosa puesta en su propio sitio ([lo], en [en] de los ejes del muro), llevado a los ejes del muro (con lo que se haya agrandado). */
    fun enMuro(lm: LuzObjeto, lo: LuzObjeto, en: V3, a: V3): V3 = en + lm.girado(a, lo.s.giro - lm.s.giro) * (lo.s.k / lm.k(en.x, en.z))

    /** Una cosa puesta en [en] (en los ejes del muro), en el suelo, girada [giro] sobre sí y agrandada [aumento] veces. */
    fun sitioEn(lm: LuzObjeto, en: V3, giro: Float, aumento: Float = 1f): LuzObjeto {
        val pie = lm.q(en); val k = lm.k(en.x, en.z) * aumento
        return LuzObjeto(Sitio(lm.s.v, pie, k, giro), Pincel(lm.l, lm.r, lm.calida), LUMBRE, lm.e)
    }

    // ---------------------------------------------------------------- El bastón

    /**
     * El bastón de guía, «de fresno, de su altura y un palmo más, con punta de hierro y las runas del paso talladas de
     * arriba abajo», recostado en el muro: la contera de hierro con su anillo, el barro de abajo, la empuñadura oscura de
     * manos y la cabeza lisa, la correa de cuero, y en la cara que mira a quien mira, las marcas del paso una debajo de otra,
     * talladas (el surco en sombra y su labio de luz, nunca brillan). Va algo más grueso que uno de verdad (1,5 veces).
     */
    private fun baston(lm: LuzObjeto, zm: Float): Cosa {
        val pie = V3(-0.02f, 0f, -0.18f); val dz = zm - 0.035f - pie.z; val dx = 0.32f
        val arriba = V3(pie.x + dx, sqrt((1.9f * 1.9f - dz * dz - dx * dx).coerceAtLeast(1f)), pie.z + dz)
        val eje = (0..12).map { pie + (arriba - pie) * (it / 12f) }
        val env = listOf(pie + V3(-0.03f, 0f, 0f), pie + V3(0.03f, 0f, 0f), arriba + V3(-0.035f, 0.03f, 0f), arriba + V3(0.035f, 0.03f, 0f), arriba + V3(0f, 0.03f, -0.03f))
        return Cosa(silueta(lm, env), lm.s.pie) {
            hoyo(lm, pie.x, pie.z, 0.12f)
            contacto(lm, pie.x, pie.z, 0.1f, 0.06f)
            sombraGrande(lm, zm, env)
            palo(lm, eje, arriba)
            labio(lm, pie.x, pie.z, 0.14f)
        }
    }

    /** El fresno del bastón, oscurecido por los años y la grasa de las manos (más que la pala nueva de la esquina). */
    private val VIEJO = lerp(ColorAntiguas.FRESNO, ColorAntiguas.CUERO, 0.3f)

    private fun DrawScope.palo(lm: LuzObjeto, eje: List<V3>, arriba: V3) {
        val radios = eje.indices.map { i -> if (i == eje.size - 1) 0.036f else 0.031f - 0.003f * i / eje.size }
        tubo(lm, eje, radios, VIEJO, 0.65f)
        // El barro de abajo, la empuñadura oscura de manos y la cabeza.
        tubo(lm, listOf(eje[1], eje[2]), listOf(0.0315f, 0.031f), lerp(VIEJO, C.TIERRA, 0.45f), 0.3f)
        tubo(lm, listOf(eje[7] + (eje[8] - eje[7]) * 0.6f, eje[9] + (eje[10] - eje[9]) * 0.4f), listOf(0.0325f, 0.0325f), lerp(VIEJO, C.CUERO, 0.5f), 0.55f)
        tubo(lm, listOf(eje[11] + (eje[12] - eje[11]) * 0.6f, arriba, arriba + LuzObjeto.unidad(arriba - eje[11]) * 0.03f), listOf(0.034f, 0.038f, 0.025f), lerp(VIEJO, C.MADERA, 0.35f), 0.7f)
        // La contera de hierro, en punta, con su anillo.
        val p0 = eje[0]; val d = LuzObjeto.unidad(eje[1] - eje[0])
        tubo(lm, listOf(p0 - d * 0.015f, p0 + d * 0.05f, p0 + d * 0.12f), listOf(0.009f, 0.025f, 0.033f), O.HIERRO, 0.85f)
        tubo(lm, listOf(p0 + d * 0.115f, p0 + d * 0.135f), listOf(0.036f, 0.036f), O.HIERRO, 0.9f)
        marcasDelPaso(lm, eje)
        correaDelBaston(lm, eje)
        empolvado(lm, listOf(lm.q(arriba + V3(-0.03f, 0.03f, 0f)), lm.q(arriba + V3(0.03f, 0.034f, 0f))), 1.1f)
    }

    /**
     * Las marcas del paso, talladas de arriba abajo en la cara que mira a quien mira: la runa de cada piedra marcada, de la
     * del collado (arriba) a la del puente (abajo), las del cuadro por referencia ([TrazosObjetos.RUNAS_DEL_CAMINO]); las de
     * abajo, más gastadas por las manos y el barro.
     */
    private fun DrawScope.marcasDelPaso(lm: LuzObjeto, eje: List<V3>) {
        val runas = TrazosObjetos.RUNAS_DEL_CAMINO.reversed()
        val a = eje.first(); val b = eje.last(); val paso = 0.6f / runas.size.coerceAtLeast(1)
        for ((i, r) in runas.withIndex()) {
            val c = lm.q(a + (b - a) * (0.78f - i * paso) + lm.girado(lm.aCamara, -lm.s.giro) * 0.024f)
            marca(lm, r, c, 0.055f * lm.k(), lerp(VIEJO, C.LANA, 0.2f), gastada = 0.04f * i)
        }
    }

    /** La correa de cuero de la mano, que sale de un agujero cerca de la cabeza y cuelga en lazo. */
    private fun DrawScope.correaDelBaston(lm: LuzObjeto, eje: List<V3>) {
        val a = eje[10] + V3(-0.02f, 0f, -0.02f)
        val lazo = listOf(a, a + V3(-0.04f, -0.08f, -0.02f), a + V3(-0.03f, -0.19f, -0.03f), a + V3(0.0f, -0.24f, -0.035f), a + V3(0.03f, -0.18f, -0.025f), a + V3(0.03f, -0.06f, -0.01f), a + V3(0.02f, 0f, 0f))
        tubo(lm, TrazosObjetos.suave(lazo), TrazosObjetos.suave(lazo).map { 0.007f }, lerp(C.CUERO, C.ESCORIA, 0.2f), 0.35f)
    }

    // ---------------------------------------------------------------- La montura

    /** El canto de delante del banco (en los ejes del muro) y la altura de su asiento: el banco del cuadro está a 0,36 m del muro. */
    private fun frenteDelBanco(zm: Float) = zm - 0.34f
    private const val ASIENTO = 0.46f

    /**
     * La montura de carga, «de haya forrada de fieltro, con ganchos para las cargas»: echada a horcajadas sobre el banco,
     * como sobre el lomo de la mula (o el caballete del cuadro), para revisarla: la almohada de fieltro que cae por delante
     * del banco, acolchada en cuarterones, con su ribete cosido y un remiendo; la tabla de haya encima, con sus remaches; los
     * dos arcos de haya que pasan por encima del asiento, oscuros de manos arriba; los ganchos de hierro de las cargas; la
     * cincha que cae al suelo con su hebilla de madera y la baticola colgando por el canto. 1,2 veces más grande.
     */
    private fun montura(lm: LuzObjeto, zm: Float): Cosa {
        val zf = frenteDelBanco(zm); val y = ASIENTO
        val env = listOf(V3(-0.42f, y + 0.14f, zf), V3(0.44f, y + 0.14f, zf), V3(-0.4f, 0.16f, zf - 0.05f), V3(0.42f, 0.16f, zf - 0.05f),
            V3(-0.24f, y + 0.35f, zf + 0.15f), V3(0.24f, y + 0.35f, zf + 0.15f), V3(-0.22f, 0.012f, zf - 0.18f), V3(0.48f, y - 0.2f, zf - 0.02f))
        return Cosa(silueta(lm, env), lm.s.pie) {
            contacto(lm, 0.0f, zf - 0.1f, 0.6f, 0.12f, fuerza = 0.3f)
            sombraGrande(lm, zm, env)
            lomo(lm, zf, y)
            almohada(lm, zf, y)
            tablaDeDelante(lm, zf, y)
            for (x in listOf(0.24f, -0.24f)) arco(lm, x, zf, y)
            cumbrera(lm, zf, y)
            for (x in listOf(0.24f, -0.24f)) gancho(lm, x, zf, y)
            correas(lm, zf, y)
            for (x in listOf(-0.24f, 0.24f)) empolvado(lm, listOf(lm.q(x, y + 0.355f, zf + 0.1f), lm.q(x, y + 0.36f, zf + 0.18f)), 1.1f)
        }
    }

    /** Lo de encima del asiento, detrás: el fieltro que cruza el lomo y la tabla de detrás, que asoma entre los arcos. */
    private fun DrawScope.lomo(lm: LuzObjeto, zf: Float, y: Float) {
        drawPath(poligono(lm.q(-0.4f, y + 0.12f, zf + 0.29f), lm.q(0.4f, y + 0.12f, zf + 0.29f), lm.q(0.4f, y + 0.17f, zf + 0.3f), lm.q(-0.4f, y + 0.17f, zf + 0.3f)), lm.c(O.HAYA, V3(0f, 0.3f, -1f), -0.2f))
        drawPath(poligono(lm.q(-0.38f, y + 0.13f, zf), lm.q(0.38f, y + 0.13f, zf), lm.q(0.38f, y + 0.135f, zf + 0.29f), lm.q(-0.38f, y + 0.135f, zf + 0.29f)), lm.c(FIELTRO_VIEJO, V3(0f, 1f, -0.1f), 0.05f))
    }

    /** La almohada de fieltro de este lado, que cae por delante del banco: acolchada, ribeteada y con un remiendo. */
    private fun DrawScope.almohada(lm: LuzObjeto, zf: Float, y: Float) {
        val pts = listOf(V3(-0.39f, y + 0.12f, zf - 0.005f), V3(0.39f, y + 0.12f, zf - 0.005f), V3(0.41f, 0.28f, zf - 0.04f), V3(0.36f, 0.2f, zf - 0.05f), V3(0.18f, 0.155f, zf - 0.056f),
            V3(0f, 0.145f, zf - 0.058f), V3(-0.18f, 0.155f, zf - 0.056f), V3(-0.36f, 0.2f, zf - 0.05f), V3(-0.41f, 0.28f, zf - 0.04f))
        val forma = poligono(pts.map { lm.q(it) })
        val n = V3(0f, 0.15f, -1f)
        drawPath(forma, Brush.verticalGradient(listOf(lm.c(FIELTRO_VIEJO, V3(0f, 0.6f, -0.8f), 0.1f), lm.c(FIELTRO_VIEJO, n), lm.c(FIELTRO_VIEJO, V3(0f, -0.4f, -0.9f), -0.15f)), lm.q(0f, y + 0.12f, zf).y, lm.q(0f, 0.16f, zf).y))
        clipPath(forma) {
            val costura = lm.c(lerp(FIELTRO_VIEJO, C.ESCORIA, 0.45f), n).copy(alpha = 0.7f)
            for (h in listOf(0.43f, 0.33f, 0.24f)) drawLine(costura, lm.q(-0.4f, h, zf - 0.03f), lm.q(0.4f, h, zf - 0.03f), 0.5f)
            for (x in listOf(-0.13f, 0.13f)) drawLine(costura, lm.q(x, y + 0.12f, zf - 0.01f), lm.q(x, 0.15f, zf - 0.06f), 0.5f)
            drawPath(poligono(lm.q(0.17f, 0.31f, zf - 0.04f), lm.q(0.3f, 0.31f, zf - 0.04f), lm.q(0.3f, 0.21f, zf - 0.05f), lm.q(0.17f, 0.21f, zf - 0.05f)), lm.c(lerp(FIELTRO_VIEJO, C.LANA, 0.35f), n, -0.05f))
            drawPath(forma, lm.c(lerp(FIELTRO_VIEJO, C.ESCORIA, 0.35f), n).copy(alpha = 0.55f), style = Stroke(2.4f))
        }
        val ribete = lm.c(C.LANA, n, 0.1f).copy(alpha = 0.8f)
        for (j in 0 until 16) { val t = j / 16f; val a = lm.q(-0.37f + 0.74f * t, 0.19f + 0.02f * kotlin.math.abs(t - 0.5f) * 2f, zf - 0.055f); drawLine(ribete, a, a + Offset(1.2f, 0f), 0.5f) }
    }

    /** La tabla de haya de este lado, a lo largo, sobre la almohada, con sus remaches. */
    private fun DrawScope.tablaDeDelante(lm: LuzObjeto, zf: Float, y: Float) {
        drawPath(poligono(lm.q(-0.43f, y + 0.09f, zf - 0.015f), lm.q(0.43f, y + 0.09f, zf - 0.015f), lm.q(0.43f, y + 0.15f, zf - 0.01f), lm.q(-0.43f, y + 0.15f, zf - 0.01f)), lm.c(O.HAYA, V3(0f, 0.2f, -1f)))
        drawPath(poligono(lm.q(-0.43f, y + 0.15f, zf - 0.01f), lm.q(0.43f, y + 0.15f, zf - 0.01f), lm.q(0.43f, y + 0.155f, zf + 0.04f), lm.q(-0.43f, y + 0.155f, zf + 0.04f)), lm.c(O.HAYA, V3(0f, 1f, 0f), 0.15f))
        for (x in listOf(-0.4f, -0.24f, 0.24f, 0.4f)) punto(lm.q(x, y + 0.12f, zf - 0.018f), 1.3f, 1.3f, lm.c(O.HIERRO, V3(0f, 0f, -1f), 0.35f))
    }

    /** El fieltro de la montura, viejo y polvoriento: el del caballete del cuadro, más apagado. */
    private val FIELTRO_VIEJO = lerp(ColorObjetos.FIELTRO, ColorAntiguas.TIERRA, 0.3f)

    /**
     * Un arco de haya, de tabla a tabla por encima del asiento, en punta: los dos brazos se cruzan arriba y asoman un poco
     * por encima de la cruz (como en las monturas de carga de aspa), oscuros de manos.
     */
    private fun DrawScope.arco(lm: LuzObjeto, x: Float, zf: Float, y: Float) {
        for (lado in listOf(1f, -1f)) {
            val a = if (lado > 0f) V3(x, y + 0.14f, zf + 0.01f) else V3(x, y + 0.14f, zf + 0.29f)
            val cima = V3(x, y + 0.34f, zf + 0.15f); val sale = cima + (cima - a) * 0.22f
            val eje = listOf(a, a + (cima - a) * 0.5f + V3(0f, 0.015f, 0f), cima, sale)
            tubo(lm, eje, listOf(0.024f, 0.023f, 0.022f, 0.02f), if (lado > 0f) O.HAYA else lerp(O.HAYA, C.MADERA_OSC, 0.2f), 0.6f)
            tubo(lm, listOf(cima, sale), listOf(0.023f, 0.021f), lerp(O.HAYA, C.MADERA_OSC, 0.5f), 0.6f)
        }
        punto(lm.q(x, y + 0.34f, zf + 0.15f), 1.5f, 1.5f, lm.c(O.HIERRO, V3(0f, 0.3f, -1f), 0.3f))
    }

    /** La cumbrera: el listón que une las dos aspas por arriba, a lo largo. */
    private fun DrawScope.cumbrera(lm: LuzObjeto, zf: Float, y: Float) {
        tubo(lm, listOf(V3(-0.3f, y + 0.325f, zf + 0.15f), V3(0.3f, y + 0.325f, zf + 0.15f)), listOf(0.016f, 0.016f), lerp(O.HAYA, C.MADERA, 0.3f), 0.55f)
    }

    /** Un gancho de hierro en la cara de delante de un arco, para colgar la carga. */
    private fun DrawScope.gancho(lm: LuzObjeto, x: Float, zf: Float, y: Float) {
        val a = V3(x * 1.25f, y + 0.15f, zf - 0.015f)
        val eje = listOf(a, a + V3(0f, -0.004f, -0.06f), a + V3(0.005f, -0.07f, -0.085f), a + V3(0.005f, -0.11f, -0.07f), a + V3(0f, -0.09f, -0.04f))
        tubo(lm, eje, eje.map { 0.009f }, O.HIERRO, 0.85f)
    }

    /** La cincha, que cae por delante al suelo con su hebilla de madera, y la baticola, que cuelga por el canto del banco. */
    private fun DrawScope.correas(lm: LuzObjeto, zf: Float, y: Float) {
        val cuero = lerp(C.CUERO, C.ESCORIA, 0.15f)
        val baticola = listOf(V3(0.4f, y + 0.1f, zf - 0.01f), V3(0.47f, y - 0.06f, zf - 0.03f), V3(0.48f, y - 0.2f, zf - 0.02f), V3(0.44f, y - 0.16f, zf + 0.01f), V3(0.42f, y + 0.02f, zf + 0.02f))
        tubo(lm, TrazosObjetos.suave(baticola), TrazosObjetos.suave(baticola).map { 0.014f }, lerp(cuero, C.ESCORIA, 0.2f), 0.3f)
        val cincha = listOf(V3(0.04f, 0.17f, zf - 0.058f), V3(0.06f, 0.08f, zf - 0.07f), V3(0.03f, 0.014f, zf - 0.13f), V3(-0.1f, 0.014f, zf - 0.18f), V3(-0.22f, 0.014f, zf - 0.12f))
        tubo(lm, TrazosObjetos.suave(cincha), TrazosObjetos.suave(cincha).map { 0.022f }, cuero, 0.35f)
        drawCircle(lm.c(O.HAYA, V3(0f, 1f, -0.5f), 0.1f), 0.035f * lm.k(), lm.q(-0.25f, 0.016f, zf - 0.1f), style = Stroke(0.013f * lm.k()))
    }

    // ---------------------------------------------------------------- Las botas

    /** El perfil de una bota, de lado (x hacia la puntera, y hacia arriba), en metros: suela, puntera, empeine, caña y talón. */
    private val BOTA = listOf(
        -0.19f to 0f, 0.23f to 0f, 0.255f to 0.04f, 0.22f to 0.09f, 0.1f to 0.13f, 0.035f to 0.21f, 0.045f to 0.4f, 0.05f to 0.44f,
        -0.13f to 0.44f, -0.145f to 0.27f, -0.19f to 0.08f,
    )

    /**
     * Las botas de guía, «de cuero de vaca engrasado, con suela doble claveteada», puestas a secar: una de pie, de lado,
     * con su cordón y paja metida en la caña; la otra tumbada delante, enseñando la suela doble con sus clavos (un cerco por
     * el borde, filas en la planta y la herradura del tacón). Así no se leen como un par de piernas. 1,45 veces más grandes.
     */
    private fun botas(lm: LuzObjeto, zm: Float): Cosa {
        val enA = V3(0.17f, 0f, 0.03f); val enB = V3(-0.13f, 0f, -0.15f)
        val la = sitioEn(lm, enA, LuzObjeto.giroDeFrente(lm.q(enA), lm.k(enA.x, enA.z)) + 0.3f, 1.12f)
        val lb = sitioEn(lm, enB, LuzObjeto.giroDeFrente(lm.q(enB), lm.k(enB.x, enB.z)) - 0.15f, 1.12f)
        val env = BOTA.flatMap { (x, y) -> listOf(enMuro(lm, la, enA, V3(x, y, -0.06f)), enMuro(lm, la, enA, V3(x, y, 0.06f))) } +
            listOf(V3(-0.2f, 0f, -0.05f), V3(0.24f, 0f, -0.05f), V3(-0.2f, 0.16f, 0.06f), V3(0.24f, 0.13f, 0.06f)).map { enMuro(lm, lb, enB, it) }
        return Cosa(silueta(lm, env), lm.s.pie) {
            hoyo(lm, 0.02f, -0.06f, 0.7f)
            contacto(la, 0.02f, 0f, 0.46f, 0.14f)
            contacto(lb, 0f, 0.03f, 0.48f, 0.2f)
            sombraGrande(lm, zm, env)
            botaDePie(la)
            botaTumbada(lb)
            labio(lm, 0.02f, -0.07f, 0.75f)
        }
    }

    /** La bota de pie, de lado: la cara de cuero con su brillo de grasa, las arrugas del tobillo, el cordón, la suela doble y la paja. */
    private fun DrawScope.botaDePie(lo: LuzObjeto) {
        val z = -0.055f; val n = V3(0f, 0f, -1f)
        val lado = poligono(BOTA.map { (x, y) -> lo.q(x, y, z) })
        val x0 = lo.q(-0.19f, 0.2f, z).x; val x1 = lo.q(0.25f, 0.2f, z).x
        drawPath(lado, Brush.horizontalGradient(listOf(lo.c(O.BOTA, V3(-0.6f, 0f, -0.8f)), lo.c(O.BOTA, n, 0.1f), lo.c(O.BOTA, V3(0.6f, 0.2f, -0.8f))), x0, x1))
        clipPath(lado) {
            for (j in 0 until 4) trazo(listOf(lo.q(-0.12f, 0.15f + j * 0.03f, z), lo.q(-0.04f, 0.17f + j * 0.028f, z), lo.q(0.04f, 0.19f + j * 0.025f, z)), lo.c(O.BOTA, n, -0.6f).copy(alpha = 0.5f), 0.45f)
            trazo(listOf(lo.q(-0.12f, 0.36f, z), lo.q(-0.1f, 0.22f, z), lo.q(0.0f, 0.1f, z), lo.q(0.15f, 0.06f, z)), lo.base.luz(lerp(O.BOTA, C.NIEVE, 0.35f)).copy(alpha = 0.45f * lo.r.contraste), 0.9f)
            drawRect(lo.c(lerp(C.TIERRA, O.BOTA, 0.3f), n).copy(alpha = 0.55f), lo.q(-0.2f, 0.07f, z), androidx.compose.ui.geometry.Size(lo.q(0.27f, 0f, z).x - lo.q(-0.2f, 0f, z).x, lo.q(0f, 0f, z).y - lo.q(0f, 0.07f, z).y))
            // El tacón, de suelas apiladas, y la costura de la puntera.
            drawPath(poligono(lo.q(-0.2f, 0.04f, z), lo.q(-0.09f, 0.04f, z), lo.q(-0.095f, 0.085f, z), lo.q(-0.195f, 0.085f, z)), lo.c(O.SUELA, n, -0.05f))
            for (h in listOf(0.055f, 0.07f)) drawLine(lo.c(O.SUELA, n, 0.3f).copy(alpha = 0.6f), lo.q(-0.2f, h, z), lo.q(-0.093f, h, z), 0.4f)
            trazo(listOf(lo.q(0.135f, 0.042f, z), lo.q(0.12f, 0.085f, z), lo.q(0.16f, 0.118f, z)), lo.c(O.BOTA, n, -0.7f).copy(alpha = 0.7f), 0.55f)
        }
        // La suela doble, con los clavos asomando por el canto.
        for ((y0, y1) in listOf(0f to 0.02f, 0.02f to 0.04f)) drawPath(poligono(lo.q(-0.195f, y0, z - 0.004f), lo.q(0.235f, y0, z - 0.004f), lo.q(0.24f, y1, z - 0.004f), lo.q(-0.19f, y1, z - 0.004f)), lo.c(O.SUELA, n, if (y0 > 0f) 0.15f else -0.1f))
        for (j in 0 until 12) punto(lo.q(-0.18f + j * 0.036f, 0.01f, z - 0.006f), 1.1f, 1f, lo.c(O.HIERRO, n, 0.5f))
        // La boca de la caña, con la paja metida para que se seque.
        val boca = poligono((0 until 14).map { j -> val a = 2f * PI.toFloat() * j / 14f; lo.q(-0.04f + 0.09f * cos(a), 0.44f, 0.05f * sin(a)) })
        drawPath(boca, lo.c(C.ESCORIA, V3(0f, 1f, 0f), -0.3f))
        val paja = lo.c(lerp(C.ORO, C.LANA, 0.35f), V3(0f, 0.6f, -0.8f), -0.05f)
        for (j in 0 until 6) { val b = lo.q(-0.1f + j * 0.022f, 0.44f, -0.01f + 0.02f * ((j * 7) % 3 - 1)); drawLine(paja, b, b + Offset((j % 3 - 1f) * 1.1f, -2.2f - (j * 5) % 3 * 0.6f), 0.45f, StrokeCap.Round) }
        cordon(lo, z)
    }

    /** El cordón de la bota de pie: cruzado por los ganchos del empeine y con los cabos sueltos. */
    private fun DrawScope.cordon(lo: LuzObjeto, z: Float) {
        val c = lo.c(lerp(C.CUERO, C.LANA, 0.25f), V3(0.5f, 0.3f, -0.8f), 0.15f)
        val ganchos = (0 until 5).map { j -> val t = j / 4f; lo.q(0.09f - 0.05f * t, 0.14f + 0.27f * t, z - 0.01f) }
        for (i in 0 until ganchos.size - 1) drawLine(c, ganchos[i], ganchos[i + 1] + Offset(1.5f, 0f), 0.6f)
        for (g in ganchos) punto(g, 1f, 1f, lo.c(O.HIERRO, V3(0f, 0f, -1f), 0.5f))
        val nudo = ganchos.last()
        trazo(listOf(nudo, nudo + Offset(2f, 3f), nudo + Offset(1.5f, 9f), nudo + Offset(3f, 13f)), c, 0.6f)
        trazo(listOf(nudo, nudo + Offset(-1f, 4f), nudo + Offset(-2.5f, 10f)), c, 0.6f)
    }

    /** El contorno de la suela, de la puntera al talón, a lo largo (x) y a lo ancho (y), en metros. */
    private val SUELA = listOf(
        0.255f to 0.05f, 0.24f to 0.085f, 0.19f to 0.105f, 0.1f to 0.1f, 0.02f to 0.078f, -0.07f to 0.074f, -0.16f to 0.085f, -0.2f to 0.06f,
        -0.205f to 0.03f, -0.16f to 0.008f, -0.07f to 0.012f, 0.02f to 0.012f, 0.1f to 0f, 0.19f to 0.004f, 0.24f to 0.018f,
    )

    /** La bota tumbada de canto delante, con la suela hacia quien mira: el cuero de la caña detrás y la suela doble con sus clavos. */
    private fun DrawScope.botaTumbada(lo: LuzObjeto) {
        val n = V3(0f, 0f, -1f)
        val cana = poligono(listOf(lo.q(-0.19f, 0.02f, 0.02f), lo.q(-0.2f, 0.15f, 0.08f), lo.q(-0.05f, 0.16f, 0.1f), lo.q(0.12f, 0.12f, 0.08f), lo.q(0.24f, 0.07f, 0.04f), lo.q(0.2f, 0.02f, 0.02f)))
        drawPath(cana, lo.c(O.BOTA, V3(0f, 1f, -0.3f), 0.05f))
        drawPath(poligono((0 until 12).map { j -> val a = 2f * PI.toFloat() * j / 12f; lo.q(-0.215f, 0.09f + 0.055f * cos(a), 0.07f + 0.045f * sin(a)) }), lo.c(C.ESCORIA, V3(-1f, 0f, 0f), -0.2f))
        val suela = poligono(SUELA.map { (x, y) -> lo.q(x, y + 0.012f, -0.045f) })
        val capa2 = poligono(SUELA.map { (x, y) -> lo.q(x * 0.99f, y + 0.012f, -0.022f) })
        drawPath(capa2, lo.c(lerp(O.SUELA, C.CUERO, 0.4f), V3(0f, 0.3f, -1f), 0.2f))
        drawPath(suela, lo.c(O.SUELA, n, 0.05f))
        clipPath(suela) {
            drawPath(suela, lo.c(lerp(O.SUELA, C.TIERRA, 0.5f), n).copy(alpha = 0.6f), style = Stroke(2f))
            clavos(lo)
        }
    }

    /** Los clavos de la suela: un cerco por el borde, tres filas en la planta y la herradura del tacón. */
    private fun DrawScope.clavos(lo: LuzObjeto) {
        val n = V3(0f, 0f, -1f); val z = -0.046f
        val cabeza = lo.c(O.HIERRO, n, 0.35f); val brillo = lo.base.luz(lerp(O.HIERRO, C.NIEVE, 0.6f)).copy(alpha = (0.2f + 0.6f * lo.r.contraste).coerceIn(0f, 1f))
        fun clavo(x: Float, y: Float) { val q = lo.q(x, y + 0.012f, z); punto(q, 1.3f, 1.2f, cabeza); punto(q + Offset(-0.25f, -0.25f), 0.5f, 0.45f, brillo) }
        for (i in SUELA.indices) { val (xa, ya) = SUELA[i]; val (xb, yb) = SUELA[(i + 1) % SUELA.size]; clavo((xa * 0.9f + xb * 0.1f) * 0.92f, 0.052f + (ya - 0.052f) * 0.78f) }
        for (fila in 0 until 3) for (j in 0 until 4) clavo(0.07f + j * 0.04f, 0.03f + fila * 0.024f)
        for (j in 0 until 5) { val a = PI.toFloat() * (0.2f + 0.6f * j / 4f); clavo(-0.15f + 0.035f * sin(a), 0.045f + 0.03f * cos(a)) }
    }
}
