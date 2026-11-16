package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * La luz del centro, que es el cuadro (`composicion_laboratorio_centro.md`, «La luz»), con la lógica de la capilla,
 * Scriptorium y Renacimiento (`docs/diseno/ESCENAS.md`, sección 2 bis):
 * - **la luz tiene forma y cae sobre penumbra:** el haz del pozo deja en el suelo del comedor una mancha grande con la forma
 *   del aro (un círculo, porque el aro y el suelo son horizontales), cortada por la sombra de las aspas del ventilador (que
 *   giran en la parte viva) y, por la mañana, por la de la pasarela; lo demás queda en penumbra verde agua;
 * - **camina con la hora:** sesgada y cálida en el desayuno (a la izquierda del cuadro), a plomo y blanca a mediodía (en el
 *   medallón del centro), sesgada y dorada en la cena (en la cabecera, subiendo por el pie de la pared de pantallas)
 *   ([CentroLab.mancha]);
 * - **se ve en el aire:** el haz, del pozo a la mancha, con su polvo;
 * - **enciende lo pintado:** las mesas, las bandejas y las flechas que caen en la mancha suben de color (`VistaCentro.c`);
 * - **la segunda luz** es la pared de pantallas, que tiñe el suelo de su lado y se refleja en el pulido.
 */
internal object LuzCentro {
    private val C = ColorLaboratorio

    /** La mancha del pozo en el suelo: el círculo del aro llevado por el haz; lo que pasa de la pared de pantallas sube por ella. */
    fun mancha(v: VistaCentro, n: Int = 40): List<V3> {
        val mm = v.pozo.mancha; val d = v.pozo.d
        return (0 until n).map { i ->
            val a = i * 2f * PI.toFloat() / n
            val x = mm.x + GeoCentro.RO * cos(a); val z = mm.y + GeoCentro.RO * sin(a)
            val r = sqrt(x * x + z * z)
            if (r <= GeoCentro.RW - 0.05f) V3(x, 0.006f, z) else enLaPared(x, z, d)
        }
    }

    /** Dónde llega a la pared el rayo que en el suelo caería en ([x], [z]): se sigue hacia arriba hasta cruzar el radio de la pared. */
    private fun enLaPared(x: Float, z: Float, d: V3): V3 {
        // Del suelo hacia arriba, por la dirección contraria al haz, hasta el radio de la pared.
        val s = GeoCentro.YO / -d.y
        val ox = x - d.x * s; val oz = z - d.z * s
        var lo = 0f; var hi = 1f
        repeat(18) { val mid = (lo + hi) / 2f; val px = ox + (x - ox) * mid; val pz = oz + (z - oz) * mid; if (px * px + pz * pz > (GeoCentro.RW - 0.05f) * (GeoCentro.RW - 0.05f)) hi = mid else lo = mid }
        val px = ox + (x - ox) * lo; val pz = oz + (z - oz) * lo
        return V3(px, GeoCentro.YO * (1f - lo) + 0.006f, pz)
    }

    /**
     * La mancha en el suelo del comedor, recortada por lo que de verdad es suelo a la luz: el círculo del vacío y el tramo de
     * la pared de pantallas (donde no hay galerías). Lo que cae más allá queda bajo la losa de la primera galería.
     */
    fun formaDeLaMancha(v: VistaCentro): Path? {
        val circulo = CamCentro.forma(mancha(v)) ?: return null
        val r = Path(); r.op(circulo, sueloAlaLuz(), PathOperation.Intersect)
        return r
    }

    /** Lo que del suelo del comedor ve el pozo: el círculo del vacío y el tramo de la pared de pantallas. */
    private fun sueloAlaLuz(): Path {
        val vacio = CamCentro.forma((0 until 60).map { GeoCentro.aro(GeoCentro.RV, 0f, it * 6f) })!!
        val tramo = CamCentro.forma(listOf(V3(0f, 0f, 0f)) + (0..12).map { GeoCentro.aro(GeoCentro.RW, 0f, GeoCentro.PA0 + (GeoCentro.PA1 - GeoCentro.PA0) * it / 12f) })!!
        val r = Path(); r.op(vacio, tramo, PathOperation.Union)
        return r
    }

    /**
     * La parte del haz que cae en el suelo de la primera galería (cuando la mancha va muy sesgada y la losa la corta): el
     * círculo del aro llevado a esa altura, entre el canto y la pared.
     */
    fun formaEnLaGaleria(v: VistaCentro): Path? {
        val mm = v.pozo.mancha; val k = (GeoCentro.YO - GeoCentro.G1) / GeoCentro.YO
        if (kotlin.math.sqrt(mm.x * mm.x + mm.y * mm.y) * k + GeoCentro.RO < GeoCentro.RV) return null
        val circulo = CamCentro.forma((0 until 32).map { i -> val a = i * 2f * PI.toFloat() / 32; V3(mm.x * k + GeoCentro.RO * cos(a), GeoCentro.G1 + 0.005f, mm.y * k + GeoCentro.RO * sin(a)) }) ?: return null
        val tramos = GaleriaCentro.tramos()
        val anillo = CamCentro.forma(tramos.map { GeoCentro.aro(GeoCentro.RW, GeoCentro.G1, it.first) } + tramos.reversed().map { GeoCentro.aro(GeoCentro.RV, GeoCentro.G1, it.second) }) ?: return null
        val r = Path(); r.op(circulo, anillo, PathOperation.Intersect)
        return r
    }

    /**
     * Lo que la luz deja en el suelo antes de las cosas: la mancha del pozo (un borde que se reconoce, un corazón más claro y
     * un halo blando alrededor), el reflejo de la pared de pantallas en el pulido y la claridad del jardín por su boca.
     */
    fun DrawScope.enElSuelo(v: VistaCentro) {
        reflejoDePantallas(v)
        val f = v.pozo.fuerza; if (f <= 0.02f) return
        val forma = formaDeLaMancha(v) ?: return
        val col = v.pozo.color
        val centro = CamCentro.p(V3(v.pozo.mancha.x, 0f, v.pozo.mancha.y)); val k = CamCentro.k(V3(v.pozo.mancha.x, 0f, v.pozo.mancha.y))
        // El halo: la luz que rebota alrededor de la mancha.
        mancha(centro, GeoCentro.RO * 2.1f * k, GeoCentro.RO * 0.9f * k, col, 0.16f * f)
        drawPath(forma, col.copy(alpha = (0.42f * f).coerceIn(0f, 1f)), blendMode = BlendMode.Plus)
        clipPath(forma) {
            mancha(centro, GeoCentro.RO * 1.1f * k, GeoCentro.RO * 0.45f * k, lerp(col, Color.White, 0.4f), 0.35f * f)
        }
    }

    /** La luz del haz en el suelo de la galería de enfrente, cuando la losa corta la mancha (se pinta después de la galería). */
    fun DrawScope.enLaGaleria(v: VistaCentro) {
        val f = v.pozo.fuerza; if (f <= 0.02f) return
        val forma = formaEnLaGaleria(v) ?: return
        drawPath(forma, v.pozo.color.copy(alpha = (0.4f * f).coerceIn(0f, 1f)), blendMode = BlendMode.Plus)
    }

    /** El reflejo de la pared de pantallas en el suelo pulido: columnas de su color, rotas en trazos, que se apagan al alejarse. */
    private fun DrawScope.reflejoDePantallas(v: VistaCentro) {
        if (v.pantalla <= 0.01f) return
        val az = Azar(Semilla.de("laboratorio", "centro", "reflejo pantallas"))
        val col = lerp(v.colorPantalla, Color.White, 0.15f)
        for (i in 0 until 22) {
            val g = GeoCentro.PA0 + 3f + (GeoCentro.PA1 - GeoCentro.PA0 - 6f) * i / 21f
            var r = GeoCentro.RW - 0.15f
            while (r > GeoCentro.RW - 3.6f) {
                val t = (GeoCentro.RW - r) / 3.6f
                val a = GeoCentro.aro(r, 0.004f, g - 0.9f); val b = GeoCentro.aro(r, 0.004f, g + 0.9f)
                drawLine(col.copy(alpha = (0.2f * v.pantalla * (1f - t) * (0.4f + 0.6f * az.real())).coerceIn(0f, 1f)), CamCentro.p(a), CamCentro.p(b), 0.9f, blendMode = BlendMode.Plus)
                r -= 0.18f + 0.25f * t
            }
        }
    }

    /**
     * Las sombras dentro de la mancha del pozo, en una capa: las de las mesas, los bancos y el estrado, tumbadas por el haz;
     * y por la mañana, la franja de la pasarela.
     */
    fun DrawScope.sombrasEnElSuelo(v: VistaCentro, esqueletos: List<List<V3>>) {
        val f = v.pozo.fuerza; if (f <= 0.05f) return
        val forma = formaDeLaMancha(v) ?: return
        val d = v.pozo.d
        fun alSuelo(q: V3): Offset { val s = q.y / -d.y; return CamCentro.p(V3(q.x + d.x * s, 0f, q.z + d.z * s)) }
        capaSombra(rayoFijo(0f, 60f, (0.8f * f).coerceIn(0f, 1f), C.SOMBRA)) { c ->
            clipPath(forma) {
                for (e in esqueletos) drawPath(poligono(envolvente(e.map { alSuelo(it) })), c.copy(alpha = 0.85f))
                // La pasarela: su losa y los pasamanos.
                val a = GeoCentro.PASA0; val b = GeoCentro.PASA1; val dir = (b - a).unidad(); val lado = V3(-dir.z, 0f, dir.x) * (GeoCentro.PASA_ANCHO / 2f)
                val y0 = GeoCentro.G2 - 0.32f
                val losa = listOf(a + lado, b + lado, b - lado, a - lado).map { alSuelo(V3(it.x, y0, it.z)) }
                drawPath(poligono(losa), c)
                for (s in listOf(-0.92f, 0.92f)) {
                    val p0 = alSuelo(a + lado * s + V3(0f, GeoCentro.BARANDA + 0.05f, 0f)); val p1 = alSuelo(b + lado * s + V3(0f, GeoCentro.BARANDA + 0.05f, 0f))
                    drawLine(c, p0, p1, 1.4f)
                }
            }
        }
    }

    // =================================================================
    //  El haz en el aire
    // =================================================================

    /** El haz del pozo en el aire: del aro a la mancha, una columna de luz con su polvo, más viva arriba. */
    fun DrawScope.haz(v: VistaCentro) {
        val f = v.pozo.fuerza; if (f <= 0.02f) return
        clipPath(sobreLaLosa()) { hazRecortado(v, f) }
    }

    private fun DrawScope.hazRecortado(v: VistaCentro, f: Float) {
        val col = v.pozo.color
        val arriba = (0 until 24).map { CamCentro.p(GeoCentro.aro(GeoCentro.RO * 0.97f, GeoCentro.YO, it * 15f)) }
        val abajo = mancha(v, 24).map { CamCentro.p(it) }
        val forma = poligono(envolvente(arriba + abajo))
        val y0 = arriba.minOf { it.y }; val y1 = abajo.maxOf { it.y }
        drawPath(forma, Brush.verticalGradient(0f to col.copy(alpha = (0.34f * f).coerceIn(0f, 1f)), 0.45f to col.copy(alpha = (0.17f * f).coerceIn(0f, 1f)), 1f to col.copy(alpha = (0.1f * f).coerceIn(0f, 1f)), startY = y0, endY = y1), blendMode = BlendMode.Plus)
        // Un corazón más estrecho y más claro.
        val nucleo = poligono(envolvente((0 until 16).map { CamCentro.p(GeoCentro.aro(GeoCentro.RO * 0.5f, GeoCentro.YO, it * 22.5f)) } +
            (0 until 16).map { val a = it * 22.5f; CamCentro.p(V3(v.pozo.mancha.x + GeoCentro.RO * 0.5f * cos(GeoCentro.rad(a)), 0f, v.pozo.mancha.y + GeoCentro.RO * 0.5f * sin(GeoCentro.rad(a)))) }))
        drawPath(nucleo, Brush.verticalGradient(0f to lerp(col, Color.White, 0.3f).copy(alpha = (0.2f * f).coerceIn(0f, 1f)), 1f to Color.Transparent, startY = y0, endY = y1), blendMode = BlendMode.Plus)
        // El polvo quieto que brilla en el haz.
        val az = Azar(Semilla.de("laboratorio", "centro", "polvo"))
        clipPath(forma) {
            repeat(70) {
                val t = az.real(); val a = az.entre(0f, 2f * PI.toFloat()); val r = GeoCentro.RO * sqrt(az.real())
                val y = GeoCentro.YO * (1f - t)
                val w = V3(v.pozo.mancha.x * t + r * cos(a), y, v.pozo.mancha.y * t + r * sin(a))
                val q = CamCentro.p(w)
                drawCircle(lerp(col, Color.White, 0.6f).copy(alpha = (0.55f * f * az.real()).coerceIn(0f, 1f)), az.entre(0.3f, 0.75f), q)
            }
        }
    }

    // =================================================================
    //  El velo
    // =================================================================

    /** La penumbra de color de los bordes del cuadro (verde agua de día, azul hondo de noche): el hall no está todo alumbrado. */
    fun DrawScope.velo(v: VistaCentro) {
        val col = lerp(lerp(C.SOMBRA, C.VIDRIO, 0.25f), lerp(C.SOMBRA, Color(0xFF141A34), 0.4f), v.noche)
        drawRect(Brush.radialGradient(0f to Color.Transparent, 0.55f to Color.Transparent, 1f to col.copy(alpha = 0.3f + 0.25f * v.noche), center = Offset(170f, 140f), radius = 300f), Offset.Zero, Size(ANCHO, ALTO), blendMode = BlendMode.Multiply)
    }

    // =================================================================
    //  Lo que se mueve
    // =================================================================

    /**
     * La sombra de las aspas del ventilador, que giran despacio dentro de la mancha; el polvo que sube y baja en el haz; el
     * elevador que sube y baja; el latido azul de la sala del destello; las llamas de las velas.
     */
    /**
     * Lo del comedor que se ve desde la esquina: lo que queda por encima del canto de la losa de cerca, menos la mesa chica y su
     * silla. La parte viva del suelo del comedor (la sombra de las aspas, las llamas) se recorta con esto, para no pintar
     * encima de la galería.
     */
    fun comedorVisto(): Path {
        val zona = sobreLaLosa()
        val mesa = poligono(envolvente(PiezasCentro.esquinas(EsquinaCentro.MESA, EsquinaCentro.U_MESA, EsquinaCentro.LADO + 0.1f, EsquinaCentro.LADO + 0.1f, EsquinaCentro.ALTO + 0.3f).map { CamCentro.p(it) } +
            PiezasCentro.esquinas(EsquinaCentro.SILLA, EsquinaCentro.U_MESA, 0.5f, 0.5f, 0.9f).map { CamCentro.p(it) }))
        val r = Path(); r.op(zona, mesa, PathOperation.Difference)
        return r
    }

    /** Lo que queda por encima del canto de la losa de cerca (lo de más abajo es el suelo de la galería, delante del vacío). */
    fun sobreLaLosa(): Path {
        val canto = (0..36).map { CamCentro.p(GeoCentro.aro(GeoCentro.RV, GeoCentro.G1, -135f + (GeoCentro.PA0 + 135f) * it / 36f)) }
        return poligono(listOf(Offset(-60f, -60f), Offset(-60f, canto.first().y)) + canto + listOf(Offset(canto.last().x, -60f)))
    }

    fun DrawScope.vivo(v: VistaCentro, reloj: Float) {
        val f = v.pozo.fuerza
        val visto = comedorVisto()
        val forma = if (f > 0.05f) formaDeLaMancha(v)?.let { val r = Path(); r.op(it, visto, PathOperation.Intersect); r } else null
        if (forma != null) {
            val d = v.pozo.d
            val giro = BovedaCentro.giro(v, reloj)
            val sombra = lerp(C.SOMBRA, Color(0xFF2A3050), 0.3f).copy(alpha = 0.42f * f)
            clipPath(forma) {
                for (i in 0 until BovedaCentro.ASPAS) {
                    val ps = BovedaCentro.aspa(giro + i * 2f * PI.toFloat() / BovedaCentro.ASPAS, BovedaCentro.VENTILADOR_Y)
                    drawPath(poligono(ps.map { q -> val s = q.y / -d.y; CamCentro.p(V3(q.x + d.x * s, 0f, q.z + d.z * s)) }), sombra)
                }
                val cubo = (0 until 10).map { val a = it * 36f; val q = GeoCentro.aro(0.55f, BovedaCentro.VENTILADOR_Y, a); val s = q.y / -d.y; CamCentro.p(V3(q.x + d.x * s, 0f, q.z + d.z * s)) }
                drawPath(poligono(cubo), sombra)
            }
            // Las motas que flotan en el haz.
            val az = Azar(Semilla.de("laboratorio", "centro", "motas"))
            repeat(14) { i ->
                val t = ((az.real() + reloj * az.entre(0.004f, 0.012f)) % 1f); val a = az.entre(0f, 6.28f) + 0.2f * sin(reloj * 0.3f + i); val r = GeoCentro.RO * 0.8f * sqrt(az.real())
                val w = V3(v.pozo.mancha.x * t + r * cos(a), GeoCentro.YO * (1f - t), v.pozo.mancha.y * t + r * sin(a))
                drawCircle(lerp(v.pozo.color, Color.White, 0.6f).copy(alpha = (0.5f * f * (0.4f + 0.6f * sin(reloj * 0.7f + i).let { it * it })).coerceIn(0f, 1f)), 0.55f, CamCentro.p(w))
            }
        }
        // El elevador que se mueve, entre la primera y la segunda galería, despacio.
        val ciclo = ((reloj * 0.012f) % 2f).let { if (it > 1f) 2f - it else it }
        val suave = ciclo * ciclo * (3f - 2f * ciclo)
        with(GaleriaCentro) { cabinaDeVidrio(v, GaleriaCentro.CABINA_VIVA, GeoCentro.G1 + 0.02f + (GeoCentro.G2 - GeoCentro.G1) * suave) }
        // El latido de la sala del destello.
        if (v.destello > 0.01f) {
            val c = GeoCentro.aro(GeoCentro.RW - 0.4f, 1.35f, GeoCentro.DESTELLO)
            val late = 0.5f + 0.5f * sin(reloj * 1.6f)
            mancha(CamCentro.p(c), 1.6f * CamCentro.k(c), 1.2f * CamCentro.k(c), Color(0xFF9FD0FF), 0.12f * v.destello * late)
        }
        // Las llamas de las velas de la noche larga.
        if (v.velas) clipPath(visto) { for (t in ComedorCentro.MESAS) for (i in 0..2) {
            val c = V3(t.x0 + (t.x1 - t.x0) * (0.2f + 0.3f * i), ComedorCentro.ALTO_MESA + 0.17f, t.z)
            val q = CamCentro.p(c); val k = CamCentro.k(c)
            val late = 0.8f + 0.2f * sin(reloj * 7f + i * 2f + t.z)
            drawOval(lerp(C.VELA, Color.White, 0.5f), q - Offset(0.012f * k, 0.05f * k * late), Size(0.024f * k + 0.4f, 0.05f * k * late + 0.6f))
        } }
    }

    @Suppress("unused") private fun usar(az: Azar) = az.real()
}
