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
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDeco.Fase
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDeco.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * La luz del club, que es el cuadro (`composicion_deco.md`, «La luz»). Sigue la lógica de la capilla, Scriptorium y
 * Renacimiento (`docs/diseno/ESCENAS.md`, sección 2 bis): la luz tiene forma y cae sobre una penumbra de color; camina
 * con la hora; se ve en el aire; enciende lo pintado; y de noche hay varias luces de color opuesto a la vez.
 *
 * - **De día** (cerrado, y en el ensayo): la penumbra cálida de la madera y de la pared roja, y el sol que entra por las
 *   ventanas altas en haces con polvo y deja su mancha, con la reja, en el suelo, en las mesas, en el estrado y en la
 *   pared ([manchas]); camina con la hora ([EscenaDeco.sol]). A las 7:00, la luz fría y gris que baja de la acera.
 * - **El ensayo**: además, el charco verde y cálido de la lámpara del piano sobre el teclado y la partitura.
 * - **De noche**, a tres luces: (1) las cálidas de dentro (la lámpara déco, su pareja del fondo, los apliques, las
 *   lamparitas de las mesas, la de la barra), con el humo en capas que encienden en conos; (2) el neón de la calle, rojo y
 *   jade, que entra por las ventanas, tiñe la pared, la escalera y la pista, y late (en la parte viva); (3) el azul frío
 *   de la noche en el hueco de la escalera. Cada luz tumba sus sombras ([sombras]) y la pista encerada las devuelve.
 */
internal object LuzDeco {
    private val C = ColorDeco
    private val S = SalaDeco

    // =================================================================
    //  Las luces de la sala
    // =================================================================

    /** Dónde está la luz de una lámpara colgante (su vidrio), en la sala. */
    fun bombilla(techo: V3, cuelga: Float): V3 = V3(techo.x, techo.y - cuelga + 0.08f, techo.z)

    /**
     * Las luces de este momento, para el `Alumbrado`. De día, las dos ventanas (el cielo de la calle), el rebote de la mancha
     * de sol y la puerta de arriba si está abierta; de noche, las lámparas, el neón de la calle por las ventanas y por la
     * puerta, y el azul de la noche por el hueco de la escalera. **[Diseño propio]**: cuánto da cada una.
     */
    fun focos(v: VistaDeco): List<Foco> = buildList {
        val ext = v.ext; val dia = (1f - ext.noche).coerceIn(0f, 1f); val nub = 1f - 0.4f * ext.nublado
        val cielo = lerp(lerp(ext.medio, Color.White, 0.45f), Color(0xFFB8C2E8), if (v.sol == null) 0.4f else 0f)
        val suelo = CamDeco.p(0f, 0f, 5f).y
        // ---- Las ventanas: el cielo de la calle, de día.
        for ((z0, z1) in S.VENTANAS) { val c = CamDeco.p(S.DER, (S.VY0 + S.VY1) / 2f, (z0 + z1) / 2f)
            if (dia > 0.02f) add(Foco(c, cielo, 0.3f * dia * nub, 140f, suelo)) }
        // ---- De noche, la luz fría de la calle por las ventanas: la otra luz, la de color opuesto a las lámparas.
        if (v.farola > 0.05f) for ((z0, z1) in S.VENTANAS) add(Foco(CamDeco.p(S.DER, S.VY0, (z0 + z1) / 2f), C.FAROLA, 0.2f * v.farola, 165f, CamDeco.p(0f, 0f, z1).y))
        // ---- El sol que entra: el rebote de su mancha.
        v.sol?.let { s -> manchaCentro(v)?.let { add(Foco(it, s.color, 0.38f * s.fuerza, 110f, it.y)) } }
        // ---- La puerta de arriba, abierta o entornada: el día, o de noche el rojo del letrero y el azul de la noche.
        val abre = when (v.puerta) { Puerta.CERRADA -> 0.12f; Puerta.ENTORNADA -> 0.45f; Puerta.ABIERTA -> 1f }
        val puerta = CamDeco.p((S.PUERTA_X0 + S.PUERTA_X1) / 2f, 2.7f, S.FONDO - 0.3f)
        if (dia > 0.02f) add(Foco(puerta, cielo, 0.35f * dia * abre * nub, 70f, CamDeco.p(1.1f, 0f, 6.5f).y))
        if (v.neon > 0.02f) add(Foco(puerta, C.NEON, 0.3f * v.neon * abre, 70f, CamDeco.p(1.1f, 0f, 6.5f).y))
        if (ext.noche > 0.3f) add(Foco(CamDeco.p(1.1f, 3.4f, S.FONDO - 0.2f), C.NOCHE, 0.3f * ext.noche * (0.4f + 0.6f * abre), 90f, CamDeco.p(1.1f, 0f, 6.5f).y))
        // ---- El neón de enfrente, por las ventanas (rojo por la primera, jade por la segunda).
        if (v.neon > 0.02f) for ((i, z) in S.VENTANAS.withIndex()) if (i < EscenaDeco.letreros(v.m)) {
            val c = CamDeco.p(S.DER - 0.4f, S.VY1 - 0.1f, (z.first + z.second) / 2f)
            add(Foco(c, if (i == 0) C.NEON else C.NEON_JADE, 0.32f * v.neon, 115f, CamDeco.p(0f, 0f, z.second).y))
        }
        // ---- Las lámparas de dentro.
        val e = v.enc; val k = 0.6f + 0.4f * ext.noche
        if (e.deco > 0.02f) {
            add(Foco(CamDeco.p(bombilla(S.LAMPARA_CERCA, 0.44f)), C.LAMPARA, 0.4f * e.deco * k, 90f, CamDeco.p(S.LAMPARA_CERCA.x, 0f, S.LAMPARA_CERCA.z).y))
            add(Foco(CamDeco.p(bombilla(S.LAMPARA_FONDO, 0.36f)), C.LAMPARA, 0.3f * e.deco * k, 68f, CamDeco.p(S.LAMPARA_FONDO.x, 0f, S.LAMPARA_FONDO.z).y))
            for (ap in S.APLIQUES) add(Foco(CamDeco.p(ap), lerp(C.LAMPARA, C.ORO, 0.2f), 0.2f * e.deco * k, 36f, CamDeco.p(ap.x, 0f, ap.z).y))
        }
        if (e.barra > 0.02f && !v.nuevo) add(Foco(CamDeco.p(bombilla(S.LAMPARA_BARRA, 0.52f)), C.LAMPARA, 0.34f * e.barra * k, 46f, CamDeco.p(S.LAMPARA_BARRA.x, 0f, S.LAMPARA_BARRA.z).y))
        if (e.piano > 0.02f) { val lp = CosasDeco.PIANO.w(PiezasDeco.LAMPARA_PIANO.x, PiezasDeco.LAMPARA_PIANO.y - 0.05f, PiezasDeco.LAMPARA_PIANO.z - 0.1f)
            add(Foco(CamDeco.p(lp), lerp(C.LAMPARA, Color(0xFFE8FFD8), 0.25f), 0.5f * e.piano * k, 40f, CamDeco.p(lp.x, 0f, lp.z).y)) }
        if (e.mesas > 0.02f) for (c in mesasEncendidas(v)) add(Foco(CamDeco.p(c.x, PiezasDeco.MESA_ALTO + 0.2f, c.z), C.LAMPARA, 0.22f * e.mesas * k, 18f + 44f / c.z, CamDeco.p(c.x, 0f, c.z).y))
        if (e.guardia > 0.02f || v.fase == Fase.ABIERTO || v.fase == Fase.DESPUES) add(Foco(CamDeco.p(S.GUARDIA), lerp(C.LAMPARA, Color.White, 0.4f), 0.24f * maxOf(e.guardia, 0.7f), 48f, CamDeco.p(1.1f, 0f, 6.9f).y))
    }

    /** Los centros de las mesas con la lamparita encendida (la cercana y las de la fila que están puestas). */
    fun mesasEncendidas(v: VistaDeco): List<V3> {
        if (v.fase != Fase.ABIERTO || v.prov) return emptyList()
        val puestas = EscenaDeco.puestas(v.m); val n = EscenaDeco.mesas(v.m)
        return listOf(CosasDeco.MESA_CERCA) + CosasDeco.MESAS.take(n - 1).filterIndexed { i, _ -> i + 1 < puestas }
    }

    // =================================================================
    //  La mancha de sol (y la de neón) que entra por las ventanas
    // =================================================================

    /** Los cristales de una ventana, entre los barrotes de su reja: cada uno, sus cuatro esquinas (z, y). */
    private fun cristales(z0: Float, z1: Float): List<List<Pair<Float, Float>>> = (0..3).map { k ->
        val a = z0 + (z1 - z0) * k / 4f + 0.025f; val b = z0 + (z1 - z0) * (k + 1) / 4f - 0.025f
        listOf(a to S.VY0, b to S.VY0, b to S.VY1, a to S.VY1)
    }

    /** Lo que da un rayo que sale de la pared de las ventanas por (z, y), viajando hacia [d], al llegar al plano [plano]. */
    private fun cae(z: Float, y: Float, d: V3, plano: Int, altura: Float = 0f): V3 = when (plano) {
        0 -> { val t = (y - altura) / -d.y; V3(S.DER + d.x * t, altura, z + d.z * t) }        // el suelo (o la tapa del estrado)
        1 -> { val t = (S.DER - S.IZQ) / -d.x; V3(S.IZQ, y + d.y * t, z + d.z * t) }          // la pared de la izquierda
        else -> { val t = if (d.z > 0.01f) (S.FONDO - z) / d.z else 99f; V3(S.DER + d.x * t, y + d.y * t, S.FONDO) }   // la pared del fondo
    }

    /** La mancha de luz que entra por las ventanas con la dirección [d], en el [plano] dado, como un camino del lienzo. */
    fun mancha(d: V3, plano: Int, altura: Float = 0f): Path {
        val camino = Path()
        if (plano == 2 && d.z <= 0.01f) return camino
        for ((z0, z1) in S.VENTANAS) for (c in cristales(z0, z1)) camino.addPath(poligono(c.map { (z, y) -> CamDeco.p(cae(z, y, d, plano, altura)) }))
        return camino
    }

    /** El centro de la mancha de sol en el suelo (de ahí rebota su luz), o null. */
    fun manchaCentro(v: VistaDeco): Offset? {
        val d = v.dirSol ?: return null
        val z = (S.VENTANAS[0].first + S.VENTANAS[1].second) / 2f; val q = cae(z, (S.VY0 + S.VY1) / 2f, d, 0)
        val x = q.x.coerceIn(S.IZQ, S.DER); return CamDeco.p(x, 0f, q.z.coerceIn(2.6f, S.FONDO))
    }

    /**
     * Pinta una mancha de luz de color [color] y fuerza [k] en [forma], recortada a [donde]: primero enciende lo que hay
     * debajo (el color propio sube, como en la capilla) y luego le suma la luz, con su penumbra de borde.
     */
    private fun DrawScope.luzEn(forma: Path, donde: Path, color: Color, k: Float, penumbra: Boolean = true) {
        if (k <= 0.01f) return
        clipPath(donde) {
            drawPath(forma, color.copy(alpha = (0.55f * k).coerceIn(0f, 1f)), blendMode = BlendMode.Overlay)
            if (penumbra) for (ancho in listOf(7f, 4f, 2f)) drawPath(forma, color.copy(alpha = (0.035f * k).coerceIn(0f, 1f)), style = Stroke(ancho), blendMode = BlendMode.Plus)
            drawPath(forma, color.copy(alpha = (0.2f * k).coerceIn(0f, 1f)), blendMode = BlendMode.Plus)
        }
    }

    /**
     * Lo que la luz deja en las superficies grandes, antes de pintar las cosas: la mancha del sol (con la reja) en el suelo,
     * en la pared de la izquierda y en la del fondo; de noche la del neón de enfrente; los charcos de las lámparas en el
     * suelo y su reflejo en la cera; el abanico de los apliques en la pared; y la luz de la puerta en el hueco.
     */
    fun DrawScope.enLasSuperficies(v: VistaDeco) {
        val suelo = S.formaDelSuelo()
        v.sol?.let { s -> val d = v.dirSol!!; val col = lerp(s.color, Color(0xFFFFD49A), 0.35f)
            luzEn(mancha(d, 0), suelo, col, 1.6f * s.fuerza)
            luzEn(mancha(d, 1), S.formaParedIzquierda(), col, 1.0f * s.fuerza)
            luzEn(mancha(d, 2), S.formaParedFondo(), col, 1.1f * s.fuerza)
        }
        if (v.farola > 0.05f) {
            luzEn(mancha(v.dirFarola, 0), suelo, C.FAROLA, 1.6f * v.farola)
            luzEn(mancha(v.dirFarola, 1), S.formaParedIzquierda(), C.FAROLA, 0.5f * v.farola)
        }
        if (v.neon > 0.05f && !v.prov) for ((i, w) in S.VENTANAS.withIndex()) if (i < EscenaDeco.letreros(v.m)) {
            val d = v.dirNeon; val forma = Path(); for (c in cristales(w.first, w.second)) forma.addPath(poligono(c.map { (z, y) -> CamDeco.p(cae(z, y, d, 0)) }))
            luzEn(forma, suelo, if (i == 0) C.NEON else C.NEON_JADE, 0.55f * v.neon, penumbra = false)
        }
        // Los charcos de las lámparas en el suelo, y su reflejo en la cera (una columna de luz hacia los ojos).
        clipPath(suelo) {
            for (f in v.focos) {
                if (f.fuerza < 0.25f || f.color == C.NOCHE || f.color == C.NEON_JADE) continue
                val pie = Offset(f.p.x, f.suelo); val ancho = f.alcance * 0.7f
                drawOval(Brush.radialGradient(listOf(f.color.copy(alpha = (0.2f * f.fuerza).coerceIn(0f, 1f)), f.color.copy(alpha = (0.1f * f.fuerza).coerceIn(0f, 1f)), Color.Transparent), pie, ancho), pie - Offset(ancho, ancho * 0.22f), Size(2f * ancho, ancho * 0.44f), blendMode = BlendMode.Plus)
                reflejo(pie, f.color, f.fuerza, ancho * 0.12f, (ALTO - pie.y).coerceAtMost(f.alcance * 0.6f))
            }
        }
        // Los apliques lavan la pared hacia arriba en abanico (y encienden el friso que tienen encima).
        if (v.enc.deco > 0.02f) clipPath(S.formaParedIzquierda()) { for (ap in S.APLIQUES) {
            val p = CamDeco.p(ap); val k = CamDeco.k(ap.z); val f = v.enc.deco
            val abanico = Path().apply { moveTo(p.x, p.y + 0.05f * k); lineTo(p.x - 0.5f * k * 0.6f, p.y - 0.75f * k); lineTo(p.x + 0.5f * k * 0.6f, p.y - 0.75f * k); close() }
            drawPath(abanico, Brush.verticalGradient(0f to C.LAMPARA.copy(alpha = 0.4f * f), 1f to Color.Transparent, startY = p.y, endY = p.y - 0.8f * k), blendMode = BlendMode.Plus)
            drawPath(Path().apply { moveTo(p.x, p.y - 0.05f * k); lineTo(p.x - 0.18f * k, p.y + 0.4f * k); lineTo(p.x + 0.18f * k, p.y + 0.4f * k); close() }, Brush.verticalGradient(0f to C.LAMPARA.copy(alpha = 0.18f * f), 1f to Color.Transparent, startY = p.y, endY = p.y + 0.4f * k), blendMode = BlendMode.Plus)
        } }
    }

    /** El reflejo de una luz en la cera de la pista: trazos tendidos que bajan desde [pie], cada vez más rotos. */
    private fun DrawScope.reflejo(pie: Offset, color: Color, fuerza: Float, ancho: Float, largo: Float) {
        val az = Azar(Semilla.de("deco", "reflejo", pie.x.toInt(), pie.y.toInt()))
        var y = pie.y + 1f
        while (y < pie.y + largo) {
            val k = (y - pie.y) / largo
            val w = ancho * (0.6f + 0.6f * az.real()) * (1f + 0.6f * k)
            drawLine(color.copy(alpha = (0.32f * fuerza * (1f - k) * (0.5f + 0.5f * az.real())).coerceIn(0f, 1f)), Offset(pie.x - w, y), Offset(pie.x + w, y), 1.1f, StrokeCap.Round, blendMode = BlendMode.Plus)
            y += 1.4f + 1.6f * k
        }
    }

    /** La mancha de sol en la tapa del estrado (que se pinta encima del suelo) y la luz de la puerta escalones abajo. */
    fun DrawScope.manchaEnElEstrado(v: VistaDeco) {
        v.sol?.let { s -> val d = v.dirSol!!; val col = lerp(s.color, Color(0xFFFFD49A), 0.35f)
            val tapa = CamDeco.forma(listOf(V3(S.IZQ, S.ESTRADO_ALTO, S.ESTRADO_Z0), V3(S.ESTRADO_X, S.ESTRADO_ALTO, S.ESTRADO_Z0), V3(S.ESTRADO_X, S.ESTRADO_ALTO, S.FONDO), V3(S.IZQ, S.ESTRADO_ALTO, S.FONDO)))
            luzEn(mancha(d, 0, S.ESTRADO_ALTO), tapa, col, 1.3f * s.fuerza) }
        if (v.farola > 0.05f) { val tapa = CamDeco.forma(listOf(V3(S.IZQ, S.ESTRADO_ALTO, S.ESTRADO_Z0), V3(S.ESTRADO_X, S.ESTRADO_ALTO, S.ESTRADO_Z0), V3(S.ESTRADO_X, S.ESTRADO_ALTO, S.FONDO), V3(S.IZQ, S.ESTRADO_ALTO, S.FONDO)))
            luzEn(mancha(v.dirFarola, 0, S.ESTRADO_ALTO), tapa, C.FAROLA, 0.8f * v.farola) }
        // La luz que baja por la escalera desde la puerta: más fuerte arriba, en cada huella.
        val abre = when (v.puerta) { Puerta.CERRADA -> 0.1f; Puerta.ENTORNADA -> 0.4f; Puerta.ABIERTA -> 1f }
        val dia = (1f - v.ext.noche).coerceIn(0f, 1f)
        val color = lerp(lerp(v.ext.medio, Color.White, 0.4f), lerp(C.NOCHE, C.NEON, 0.45f), v.ext.noche.coerceIn(0f, 1f))
        val k = abre * (0.6f * dia + 0.8f * v.ext.noche)
        if (k > 0.02f) {
            // A contraluz: la luz de arriba cae en las huellas y en el canto de cada peldaño; las tabicas, que miran a los
            // ojos, quedan en sombra (más cuanto más arriba, donde la luz de la puerta manda).
            val n = S.PELDANOS; val sube = S.ESC_ALTO / n; val huella = (S.ESC_Z1 - S.ESC_Z0) / n
            for (i in n - 1 downTo 0) { val z = S.ESC_Z0 + i * huella; val y0 = i * sube; val y1 = (i + 1) * sube; val t = (i + 1) / n.toFloat()
                val tabica = CamDeco.forma(listOf(V3(S.ESC_X0, y0, z), V3(S.DER, y0, z), V3(S.DER, y1, z), V3(S.ESC_X0, y1, z)))
                drawPath(tabica, C.SOMBRA.copy(alpha = (0.4f * k * t).coerceIn(0f, 0.6f)), blendMode = BlendMode.Multiply)
                if (y1 < CamDeco.OJO) { val h = CamDeco.forma(listOf(V3(S.ESC_X0 + 0.1f, y1, z), V3(S.DER - 0.05f, y1, z), V3(S.DER - 0.05f, y1, z + huella), V3(S.ESC_X0 + 0.1f, y1, z + huella)))
                    drawPath(h, color.copy(alpha = (0.4f * k * (0.3f + 0.7f * t)).coerceIn(0f, 1f)), blendMode = BlendMode.Plus) }
                drawLine(color.copy(alpha = (0.75f * k * (0.35f + 0.65f * t)).coerceIn(0f, 1f)), CamDeco.p(S.ESC_X0 + 0.05f, y1, z - 0.03f), CamDeco.p(S.DER, y1, z - 0.03f), 0.9f, blendMode = BlendMode.Plus)
            }
            mancha(CamDeco.p(1.15f, 2.4f, S.FONDO - 0.1f), 18f, 26f, color, 0.35f * k)
        }
    }

    // =================================================================
    //  Las sombras
    // =================================================================

    /** Cuánto alumbran las lámparas colgantes, y dónde están. */
    private fun lamparas(v: VistaDeco): List<Pair<V3, Float>> {
        val e = v.enc.deco; if (e <= 0.02f) return emptyList()
        return listOf(bombilla(S.LAMPARA_CERCA, 0.44f) to 1f * e, bombilla(S.LAMPARA_FONDO, 0.36f) to 0.75f * e)
    }

    private fun alSuelo(p: V3, desde: V3): V3 {
        val y = p.y.coerceAtMost(desde.y - 0.12f); val k = (desde.y / (desde.y - y)).coerceAtMost(7f)
        return V3(desde.x + (p.x - desde.x) * k, 0f, desde.z + (p.z - desde.z) * k)
    }

    /**
     * Las sombras de las cosas, en una sola capa: el sol las tumba dentro de su mancha (de lado, lejos de las ventanas, más
     * largas con el sol bajo); de día, la luz de las ventanas deja otra suave hacia la izquierda; de noche, cada lámpara
     * colgante tumba la de lo que tiene cerca, lejos de ella, y la abre en abanico. Debajo de cada cosa, su sombra de
     * contacto.
     */
    fun DrawScope.sombras(v: VistaDeco, cosas: List<Cosa3>) {
        val suelo = S.formaDelSuelo()
        val conCuerpo = cosas.filter { it.esqueleto.size >= 3 }
        // La sombra de contacto, siempre.
        clipPath(suelo) { for (k in conCuerpo) { val pies = k.esqueleto.filter { it.y < 0.3f }; if (pies.size < 3) continue
            drawPath(poligono(envolvente(pies.map { CamDeco.p(it) })), C.SOMBRA.copy(alpha = 0.32f), blendMode = BlendMode.Multiply)
            drawPath(poligono(envolvente(pies.map { CamDeco.p(it) })), C.SOMBRA.copy(alpha = 0.18f), style = Stroke(3f), blendMode = BlendMode.Multiply) } }
        val dia = (1f - v.ext.noche).coerceIn(0f, 1f); val lamps = lamparas(v)
        capaSombra(rayoFijo(-0.5f, 45f, 0.75f, C.SOMBRA)) { c ->
            clipPath(suelo) {
                // La de las ventanas, de día: suave y hacia la izquierda.
                if (dia > 0.05f) { val d = V3(-1f, -0.95f, 0.1f)
                    for (k in conCuerpo) drawPath(poligono(envolvente(k.esqueleto.map { CamDeco.p(V3(it.x + d.x * it.y / -d.y, 0f, it.z + d.z * it.y / -d.y)) })), c.copy(alpha = 0.3f * dia * (1f - 0.5f * v.ext.nublado))) }
                // La del sol, solo dentro de su mancha.
                val d = v.dirSol; val s = v.sol
                if (d != null && s != null) clipPath(mancha(d, 0)) {
                    for (k in conCuerpo) drawPath(poligono(envolvente(k.esqueleto.map { CamDeco.p(V3(it.x + d.x * it.y / -d.y, 0f, it.z + d.z * it.y / -d.y)) })), c.copy(alpha = (0.95f * s.fuerza).coerceIn(0f, 1f)))
                }
                // La de la farola, de noche, dentro de su mancha fría.
                if (v.farola > 0.05f) { val f = v.dirFarola; clipPath(mancha(f, 0)) {
                    for (k in conCuerpo) drawPath(poligono(envolvente(k.esqueleto.map { CamDeco.p(V3(it.x + f.x * it.y / -f.y, 0f, it.z + f.z * it.y / -f.y)) })), c.copy(alpha = (0.8f * v.farola).coerceIn(0f, 1f))) } }
                // Las de las lámparas, de noche.
                for (k in conCuerpo) {
                    val pie = k.esqueleto.reduce { a, b -> V3(a.x + b.x, 0f, a.z + b.z) }.let { V3(it.x / k.esqueleto.size, 0f, it.z / k.esqueleto.size) }
                    val manda = lamps.maxByOrNull { (l, f) -> f / (1f + PiezasDeco.lejos(l, pie).let { it * it } / 4f) } ?: continue
                    val (l, f) = manda; val dist = PiezasDeco.lejos(l, pie)
                    val alfa = (0.85f * f / (1f + dist * dist / 5f)).coerceIn(0f, 1f); if (alfa < 0.05f) continue
                    drawPath(poligono(envolvente(k.esqueleto.map { CamDeco.p(alSuelo(it, l)) })), c.copy(alpha = alfa))
                }
            }
        }
    }

    // =================================================================
    //  El aire: el haz de sol con su polvo, el humo, los conos de luz
    // =================================================================

    /**
     * La luz en el aire, entre las cosas lejanas y las cercanas: el haz de sol de cada ventana hasta su mancha, con motas
     * de polvo; el humo en capas bajo el techo, que las lámparas encienden en conos; y el resplandor del neón junto a las
     * ventanas.
     */
    fun DrawScope.aire(v: VistaDeco) {
        val s = v.sol; val d = v.dirSol
        if (s != null && d != null) {
            val col = lerp(s.color, Color(0xFFFFD49A), 0.3f); val az = Azar(Semilla.de("deco", "polvo"))
            for ((z0, z1) in S.VENTANAS) for (c in cristales(z0, z1)) {
                for (i in c.indices) { val (za, ya) = c[i]; val (zb, yb) = c[(i + 1) % c.size]
                    drawPath(poligono(CamDeco.p(S.DER, ya, za), CamDeco.p(S.DER, yb, zb), CamDeco.p(fin(zb, yb, d)), CamDeco.p(fin(za, ya, d))), col.copy(alpha = 0.045f * s.fuerza), blendMode = BlendMode.Plus) }
            }
            // El polvo: motas quietas a lo largo del haz (las que se mueven van en la parte viva).
            repeat(70) { val w = S.VENTANAS[it % 2]; val z = az.entre(w.first, w.second); val y = az.entre(S.VY0, S.VY1); val t = az.entre(0.05f, 0.95f)
                val a = V3(S.DER, y, z); val b = fin(z, y, d); val q = CamDeco.p(V3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t))
                drawCircle(col.copy(alpha = (0.55f * s.fuerza * az.real()).coerceIn(0f, 1f)), az.entre(0.3f, 0.75f), q) }
        }
        // El humo bajo el techo: capas tendidas, más espesas cuanto más público; encendidas donde hay una lámpara.
        val humo = v.humo
        if (humo > 0.02f) {
            val az = Azar(Semilla.de("deco", "humo"))
            for (capa in 0..4) { val y = 2.25f - capa * 0.14f; val z = 3.2f + capa * 0.9f
                val c = CamDeco.p(-0.8f + az.entre(-0.6f, 0.6f), y, z)
                mancha(c, 90f + capa * 16f, 5f + capa * 1.5f, v.c2(c, C.HUMO), 0.13f * humo) }
            // Los conos de luz que las lámparas encienden en el humo.
            for ((l, f) in lamparas(v)) { val p = CamDeco.p(l); val k = CamDeco.k(l.z); val pie = CamDeco.p(l.x, 0.6f, l.z)
                drawPath(Path().apply { moveTo(p.x - 0.18f * k, p.y); lineTo(p.x + 0.18f * k, p.y); lineTo(pie.x + 0.8f * k, pie.y); lineTo(pie.x - 0.8f * k, pie.y); close() },
                    Brush.verticalGradient(0f to C.LAMPARA.copy(alpha = (0.07f * f * (0.3f + humo)).coerceIn(0f, 1f)), 1f to Color.Transparent, startY = p.y, endY = pie.y), blendMode = BlendMode.Plus) }
            for (c in mesasEncendidas(v)) { val p = CamDeco.p(c.x, 1.1f, c.z); mancha(p, 0.4f * CamDeco.k(c.z), 0.25f * CamDeco.k(c.z), C.LAMPARA, 0.1f * humo * v.enc.mesas) }
        }
        // El aire teñido junto a las ventanas, de noche, por el neón.
        if (v.neon > 0.05f && !v.prov) for ((i, w) in S.VENTANAS.withIndex()) if (i < EscenaDeco.letreros(v.m)) {
            val c = CamDeco.p(S.DER - 0.5f, S.VY0, (w.first + w.second) / 2f)
            mancha(c, 34f, 22f, if (i == 0) C.NEON else C.NEON_JADE, 0.14f * v.neon * (0.5f + v.humo))
        }
    }

    /** Hasta dónde llega el haz que sale de la ventana por (z, y): el suelo o la pared de enfrente, lo que encuentre antes. */
    private fun fin(z: Float, y: Float, d: V3): V3 {
        val alSuelo = y / -d.y; val aLaPared = (S.DER - S.IZQ) / -d.x
        val t = minOf(alSuelo, aLaPared); return V3(S.DER + d.x * t, y + d.y * t, z + d.z * t)
    }

    // =================================================================
    //  Los adornos de los días señalados
    // =================================================================

    /**
     * La fiesta: serpentinas que cuelgan del techo y farolillos de papel en una cuerda que cruza la sala; en Nochevieja y
     * año nuevo, globos (uno en el suelo, junto a la silla del abrigo) y gorros de papel en las mesas; en Navidad, acebo
     * sobre el friso y un arbolito en el rincón de la barra; en la boda, una guirnalda de flores blancas. **[Diseño propio]**.
     */
    fun DrawScope.adornos(v: VistaDeco) {
        val m = v.m; if (v.prov && !EscenaDeco.fiesta(m)) return
        if (EscenaDeco.fiesta(m)) {
            val az = Azar(Semilla.de("deco", "fiesta", m.ficha.dia))
            // Los farolillos, en una cuerda de la pared de la izquierda a la viga del hueco.
            val a = V3(S.IZQ, 2.3f, 3.6f); val b = V3(S.POZO, 2.25f, 6.6f)
            val puntos = (0..14).map { i -> val t = i / 14f; V3(a.x + (b.x - a.x) * t, a.y - 0.22f * sin(PI.toFloat() * t), a.z + (b.z - a.z) * t) }
            for (i in 0 until puntos.size - 1) drawLine(v.c2(CamDeco.p(puntos[i]), C.HIERRO), CamDeco.p(puntos[i]), CamDeco.p(puntos[i + 1]), 0.4f)
            val colores = if (EscenaDeco.boda(m)) listOf(C.MARFIL, lerp(C.MARFIL, C.CORAL, 0.2f)) else listOf(C.CORAL, C.ORO, C.JADE)
            for (i in 1 until puntos.size - 1 step 2) { val q = puntos[i]; val p = CamDeco.p(q); val k = CamDeco.k(q.z); val col = colores[i / 2 % colores.size]
                val luz = if (v.enc.deco > 0.1f || v.neon > 0.3f) 0.5f else 0f
                drawOval(Brush.horizontalGradient(0f to v.c(q, col, V3(-1f, 0f, -0.5f)), 0.5f to lerp(v.c(q, col, V3(0f, 0f, -1f)), lerp(col, C.LAMPARA, 0.5f), luz), 1f to v.c(q, lerp(col, C.LACA, 0.3f), V3(1f, 0f, -0.5f)), startX = p.x - 0.1f * k, endX = p.x + 0.1f * k), Offset(p.x - 0.1f * k, p.y), Size(0.2f * k, 0.26f * k))
                for (r in 1..2) drawLine(v.c(q, lerp(col, C.LACA, 0.4f), V3(0f, 0f, -1f)).copy(alpha = 0.5f), Offset(p.x - 0.1f * k, p.y + r * 0.087f * k), Offset(p.x + 0.1f * k, p.y + r * 0.087f * k), 0.35f) }
            // Las serpentinas, que caen del techo rizándose.
            repeat(9) { i -> val x = az.entre(-2.6f, 0.2f); val z = az.entre(2.8f, 6.8f); val col = listOf(C.CORAL, C.ORO, C.JADE, C.MARFIL)[i % 4]
                val largo = az.entre(0.4f, 0.9f); val tira = Path(); val p0 = CamDeco.p(x, S.TECHO, z); tira.moveTo(p0.x, p0.y)
                for (j in 1..24) { val t = j / 24f; val q = CamDeco.p(x + 0.03f * sin(t * 26f + i), S.TECHO - largo * t, z + 0.03f * cos(t * 26f + i)); tira.lineTo(q.x, q.y) }
                drawPath(tira, v.c(V3(x, 2f, z), col, V3(0f, 0f, -1f)), style = Stroke(0.025f * CamDeco.k(z), cap = StrokeCap.Round)) }
            if (EscenaDeco.finDeAnio(m)) {
                // Globos atados al barandal y uno en el suelo; gorros de papel en las mesas.
                for ((i, q) in listOf(V3(0.5f, 1.9f, 5.4f), V3(0.48f, 2.05f, 5.6f), V3(-3.2f, 2.0f, 4.4f)).withIndex()) {
                    val p = CamDeco.p(q); val r = 0.15f * CamDeco.k(q.z); val col = listOf(C.CORAL, C.ORO, C.JADE, C.CORAL)[i]
                    drawCircle(Brush.radialGradient(listOf(lerp(lerp(v.c(q, col, V3(0f, 0f, -1f)), col, 0.35f), C.MARFIL, 0.4f), lerp(v.c(q, col, V3(0f, 0f, -1f)), col, 0.25f), v.c(q, lerp(col, C.LACA, 0.4f), V3(1f, 0f, 0f))), p - Offset(r * 0.3f, r * 0.3f), r * 1.3f), r, p)
                    if (i < 3) drawLine(v.c(q, C.MARFIL, V3(0f, 0f, -1f)).copy(alpha = 0.6f), p + Offset(0f, r), CamDeco.p(q.x, 0.9f, q.z), 0.35f) }
                // Un gorro de papel caído en el suelo, con su serpentina enroscada (lo que mira el gato).
                run { val q = V3(0.3f, 0f, 2.75f); val p = CamDeco.p(q); val k = CamDeco.k(q.z)
                    drawPath(poligono(p + Offset(-0.05f * k, 0f), p + Offset(0.07f * k, -0.03f * k), p + Offset(0.16f * k, 0.02f * k)), v.c(q, C.ORO, V3(0f, 0.6f, -1f)))
                    drawCircle(v.c(q, C.CORAL, V3(0f, 1f, -0.5f)), 0.02f * k, p + Offset(0.16f * k, 0.02f * k))
                    val tira = Path(); for (j in 0..20) { val t = j / 20f; val o = p + Offset(-0.1f * k - t * 0.3f * k + 0.03f * k * sin(t * 20f), 0.01f * k + 0.025f * k * cos(t * 20f)); if (j == 0) tira.moveTo(o.x, o.y) else tira.lineTo(o.x, o.y) }
                    drawPath(tira, v.c(q, C.JADE, V3(0f, 1f, -0.5f)), style = Stroke(0.012f * k)) }
                for (c in CosasDeco.MESAS.take(3)) { val q = V3(c.x + 0.1f, PiezasDeco.MESA_ALTO, c.z); val p = CamDeco.p(q); val k = CamDeco.k(q.z)
                    drawPath(poligono(p + Offset(-0.06f * k, 0f), p + Offset(0.06f * k, 0f), p + Offset(0f, -0.16f * k)), v.c(q, C.ORO, V3(0f, 0f, -1f))) }
            }
            if (EscenaDeco.navidad(m)) {
                // El acebo sobre el friso de la pared de la izquierda, y el arbolito en el rincón de la barra.
                for (z in listOf(4.4f, 5.2f, 6f, 6.8f, 7.6f)) { val q = V3(S.IZQ + 0.02f, S.FRISO_Y1 + 0.03f, z); val p = CamDeco.p(q); val k = CamDeco.k(z)
                    for (j in -2..2) drawOval(v.c(q, lerp(C.JADE, C.LACA, 0.3f), V3(1f, 0f, 0f)), p + Offset(j * 0.04f * k * 0.5f, -0.02f * k), Size(0.06f * k * 0.5f, 0.03f * k))
                    drawCircle(v.c(q, C.CORAL, V3(1f, 0f, 0f)), 0.018f * k, p) }
                val pie = V3(-0.1f, 0f, 7.75f); val p = CamDeco.p(pie); val k = CamDeco.k(pie.z)
                for (j in 0..3) { val y0 = 0.25f + j * 0.28f; val w = 0.42f - j * 0.09f
                    drawPath(poligono(CamDeco.p(pie.x - w, y0, pie.z), CamDeco.p(pie.x + w, y0, pie.z), CamDeco.p(pie.x, y0 + 0.42f, pie.z)), v.c(V3(pie.x, y0, pie.z), lerp(C.JADE, C.LACA, 0.2f + 0.1f * j), V3(0f, 0f, -1f))) }
                drawRect(v.c(pie, C.CORAL, V3(0f, 0f, -1f)), p - Offset(0.08f * k, 0.25f * k), Size(0.16f * k, 0.25f * k))
                val bolas = Azar(Semilla.de("deco", "arbol")); repeat(10) { val q = CamDeco.p(pie.x + bolas.entre(-0.3f, 0.3f), bolas.entre(0.35f, 1.1f), pie.z - 0.05f); drawCircle(lerp(C.ORO, C.LAMPARA, v.noche * 0.6f), 0.022f * k, q) }
            }
        }
        if (EscenaDeco.boda(m)) {
            // La guirnalda de flores blancas a lo largo del barandal.
            val n = S.PELDANOS; val sube = S.ESC_ALTO / n; val huella = (S.ESC_Z1 - S.ESC_Z0) / n
            for (i in 0 until n) { val z = S.ESC_Z0 + i * huella + 0.1f; val y = i * sube + 0.95f; val q = V3(S.ESC_X0 - 0.04f, y, z); val p = CamDeco.p(q)
                drawCircle(v.c(q, C.MARFIL, V3(-1f, 0f, -0.5f)), 0.05f * CamDeco.k(z), p); drawCircle(v.c(q, lerp(C.JADE, C.LACA, 0.2f), V3(-1f, 0f, -0.5f)), 0.03f * CamDeco.k(z), p + Offset(1.4f, 0.8f)) }
        }
    }

    // =================================================================
    //  El velo: la penumbra de los bordes
    // =================================================================

    /** La penumbra de color de los bordes y, con humo, el velo que lo junta todo. */
    fun DrawScope.velo(v: VistaDeco) {
        if (v.humo > 0.05f) drawRect(v.c2(Offset(220f, 120f), C.HUMO).copy(alpha = 0.03f + 0.06f * v.humo), Offset.Zero, Size(ANCHO, ALTO))
        drawRect(Brush.radialGradient(0f to Color.Transparent, 0.55f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.34f + 0.12f * v.noche), center = Offset(220f, 150f), radius = 300f), Offset.Zero, Size(ANCHO, ALTO), blendMode = BlendMode.Multiply)
    }

    // =================================================================
    //  Lo que se mueve
    // =================================================================

    /**
     * La parte viva, barata: las motas de polvo que flotan en el haz de sol, las hebras de humo que se desplazan bajo el
     * techo y suben de los ceniceros, el neón que late (un resplandor que sube y baja en la pared y en la pista), el ventilador
     * que gira, y la lluvia o la nieve detrás de los cristales.
     */
    fun DrawScope.vivo(v: VistaDeco, reloj: Float) {
        val s = v.sol; val d = v.dirSol
        if (s != null && d != null) {
            val az = Azar(Semilla.de("deco", "motas")); val col = lerp(s.color, Color.White, 0.4f)
            repeat(26) { i -> val w = S.VENTANAS[i % 2]; val z = az.entre(w.first, w.second); val y = az.entre(S.VY0, S.VY1)
                val t = ((az.real() + reloj * az.entre(0.004f, 0.012f)) % 1f); val a = V3(S.DER, y, z); val b = fin(z, y, d)
                val q = CamDeco.p(V3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t + 0.05f * sin(reloj * 0.7f + i), a.z + (b.z - a.z) * t))
                drawCircle(col.copy(alpha = (0.6f * s.fuerza * (0.4f + 0.6f * sin(reloj + i).let { it * it })).coerceIn(0f, 1f)), 0.55f, q) }
        }
        if (v.humo > 0.02f) {
            val az = Azar(Semilla.de("deco", "hebras"))
            repeat(7) { i -> val y = az.entre(1.7f, 2.35f); val z = az.entre(3f, 7f); val x = ((az.entre(-3.4f, 0.4f) + reloj * 0.04f * (1 + i % 3) + 3.4f) % 3.8f) - 3.4f
                val p = CamDeco.p(x, y, z); mancha(p, 30f + 8f * (i % 3), 4f + i % 2, v.c2(p, C.HUMO), 0.12f * v.humo) }
            for (c in mesasEncendidas(v).take(4)) { val base = CamDeco.p(c.x - 0.1f, PiezasDeco.MESA_ALTO + 0.05f, c.z - 0.1f); val k = CamDeco.k(c.z)
                for (j in 0..4) { val t = ((reloj * 0.12f + j / 5f + c.x) % 1f); drawCircle(v.c2(base, C.HUMO).copy(alpha = (0.18f * (1f - t)).coerceIn(0f, 1f)), (0.02f + 0.06f * t) * k, base + Offset(sin(t * 6f + j) * 0.04f * k, -t * 0.6f * k)) } }
        }
        // El neón que late: el resplandor de la ventana y su mancha en la pista suben y bajan.
        if (v.neon > 0.05f && !v.prov) for ((i, w) in S.VENTANAS.withIndex()) if (i < EscenaDeco.letreros(v.m)) {
            val late = 0.5f + 0.5f * sin(reloj * (if (i == 0) 2.1f else 1.3f) + i); val parpadeo = if (sin(reloj * 7.3f + i * 2f) > 0.97f) 0.3f else 1f
            val col = if (i == 0) C.NEON else C.NEON_JADE
            val c = CamDeco.p(S.DER, (S.VY0 + S.VY1) / 2f, (w.first + w.second) / 2f)
            drawCircle(Brush.radialGradient(listOf(col.copy(alpha = (0.16f * v.neon * late * parpadeo).coerceIn(0f, 1f)), Color.Transparent), c, 26f), 26f, c, blendMode = BlendMode.Plus)
            val q = cae((w.first + w.second) / 2f, (S.VY0 + S.VY1) / 2f, v.dirNeon, 0); val pq = CamDeco.p(q)
            drawOval(Brush.radialGradient(listOf(col.copy(alpha = (0.12f * v.neon * late * parpadeo).coerceIn(0f, 1f)), Color.Transparent), pq, 30f), pq - Offset(30f, 8f), Size(60f, 16f), blendMode = BlendMode.Plus)
        }
        // El ventilador, que gira: un disco borroso de aspas.
        if (EscenaDeco.ventilador(v.m) && CosasDeco.giraElVentilador(v)) { val c = CamDeco.p(-1.35f, S.TECHO - 0.32f, 4.2f); val k = CamDeco.k(4.2f)
            drawOval(v.c2(c, C.ROBLE_OSC).copy(alpha = 0.22f), c - Offset(0.56f * k, 0.06f * k), Size(1.12f * k, 0.12f * k))
            val a = reloj * 9f; for (i in 0..3) { val b = a + i * PI.toFloat() / 2f; drawLine(v.c2(c, C.ROBLE_OSC).copy(alpha = 0.35f), c, c + Offset(cos(b) * 0.55f * k, sin(b) * 0.06f * k), 1.2f) } }
        // La lluvia y la nieve, detrás de los cristales.
        val clima = v.m.ficha.clima
        if (clima == Clima.LLUVIA || clima == Clima.TORMENTA || clima == Clima.NIEVE) for ((z0, z1) in S.VENTANAS) {
            val vidrio = CamDeco.forma(listOf(V3(S.DER + S.MURO, S.VY0, z0), V3(S.DER + S.MURO, S.VY0, z1), V3(S.DER + S.MURO, S.VY1, z1), V3(S.DER + S.MURO, S.VY1, z0)))
            clipPath(vidrio) { val az = Azar(Semilla.de("deco", "gotas", z0)); val b = vidrio.getBounds()
                repeat(16) { i -> val x = az.entre(b.left, b.right); val y = ((az.entre(0f, 1f) + reloj * (if (clima == Clima.NIEVE) 0.15f else 1.4f)) % 1f) * b.height + b.top
                    if (clima == Clima.NIEVE) drawCircle(Color.White.copy(alpha = 0.7f), 0.6f, Offset(x + sin(reloj + i) * 1.5f, y))
                    else drawLine(Color.White.copy(alpha = 0.4f), Offset(x, y), Offset(x - 0.6f, y + 3f), 0.4f) } }
        }
        // El relámpago, que blanquea los cristales.
        if (clima == Clima.TORMENTA) { val k = sin(reloj * 0.9f) * sin(reloj * 3.7f + 1f); if (k > 0.86f) for ((z0, z1) in S.VENTANAS) drawPath(CamDeco.forma(listOf(V3(S.DER + S.MURO, S.VY0, z0), V3(S.DER + S.MURO, S.VY0, z1), V3(S.DER + S.MURO, S.VY1, z1), V3(S.DER + S.MURO, S.VY1, z0))), Color.White.copy(alpha = 0.5f)) }
    }
}
