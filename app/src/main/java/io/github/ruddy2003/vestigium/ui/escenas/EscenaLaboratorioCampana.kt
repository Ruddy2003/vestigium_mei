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
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Grama
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.ARRIBA
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.FRENTE
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.cajaEn
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.cilindro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.mitad
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.palo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * La campana de cultivo: «un cilindro de vidrio grueso en medio del laboratorio, de cuatro pasos de ancho, con una
 * esclusa, un catre, cajas de tierra gris de arriba y veinte bandejas de grama. Desde fuera parece un acuario». Fuera
 * del experimento está igual, vacía de persona, como cámara de pruebas, con sus bandejas en un estante **[Diseño propio]**.
 *
 * **Que el vidrio sea vidrio** (`composicion_laboratorio.md`, fallo propio 2): el canto grueso y verdoso en los bordes,
 * lo de detrás más frío y deformado (las líneas de la sala se tuercen hacia dentro), los brillos largos de cada luz donde
 * el cilindro la devuelve hacia los ojos ([brillos]), las gotas de condensación por dentro, y su mancha de luz curva en
 * el suelo (la pinta [LuzLab]). De noche «el vidrio se vuelve un espejo»: lo de dentro se apaga y mandan los reflejos.
 */
internal object CampanaLab {
    private val C = ColorLaboratorio
    val CENTRO = V3(1.5f, 0f, 5.1f)
    const val R = 1.1f
    const val ALTO = 2.85f
    const val ZOCALO = 0.16f

    /** La campana como cosa de la sala: se pinta en su turno y deja una sombra muy tenue (es de vidrio). */
    fun cosa(v: VistaLab): CosaLab = CosaLab(CamLab.hondo(CENTRO.x, CENTRO.z), PiezasLab.aro(CENTRO, R, 12) + PiezasLab.aro(V3(CENTRO.x, ZOCALO + 0.05f, CENTRO.z), R, 12)) { pintar(v) }

    /** El contorno de la campana en el lienzo, del zócalo al aro de arriba. */
    fun silueta(): Path {
        val arriba = mitad(V3(CENTRO.x, ALTO, CENTRO.z), R + 0.04f, cerca = true).map { CamLab.p(it) }
        val abajo = mitad(V3(CENTRO.x, 0f, CENTRO.z), R + 0.06f, cerca = true).map { CamLab.p(it) }
        return Path().apply { moveTo(arriba.first().x, arriba.first().y); for (p in arriba) lineTo(p.x, p.y); for (p in abajo.reversed()) lineTo(p.x, p.y); close() }
    }

    /** El cuerpo de vidrio, entre el zócalo y el aro de arriba. */
    private fun cuerpo(): Path {
        // El aro de arriba queda por encima de los ojos: el borde que se ve es el de delante.
        val arriba = mitad(V3(CENTRO.x, ALTO - 0.12f, CENTRO.z), R, cerca = true).map { CamLab.p(it) }
        val abajo = mitad(V3(CENTRO.x, ZOCALO + 0.08f, CENTRO.z), R, cerca = true).map { CamLab.p(it) }
        return Path().apply { moveTo(arriba.first().x, arriba.first().y); for (p in arriba) lineTo(p.x, p.y); for (p in abajo.reversed()) lineTo(p.x, p.y); close() }
    }

    /** El punto del vidrio de delante que se ve en la vertical [x] del lienzo, y su normal (hacia fuera). */
    private fun anguloDelanteroEn(fraccion: Float): Float {
        val a0 = atan2(-CENTRO.z, -CENTRO.x)
        return a0 - (Math.PI / 2).toFloat() + Math.PI.toFloat() * fraccion
    }

    // ---------------------------------------------------------------- El dibujo

    fun DrawScope.pintar(v: VistaLab) {
        val m = v.m
        val noche = v.noche
        val dentro = v.campana
        val experimento = EscenaLaboratorio.enLaCampana(m)
        val forma = cuerpo()
        // ---- Lo de detrás, visto a través: más frío, más verde y algo más oscuro (dos vidrios gruesos).
        clipPath(forma) {
            drawPath(forma, lerp(C.VIDRIO, C.SOMBRA, if (noche) 0.55f else 0.2f).copy(alpha = if (noche) 0.45f else 0.24f), blendMode = BlendMode.Multiply)
            refraccion(v)
        }
        // ---- El zócalo de acero, con sus pernos.
        cilindro(v, CENTRO, R + 0.06f, ZOCALO, lerp(C.ACERO, C.NEGRO, 0.1f), tapa = null)
        val pernos = mitad(V3(CENTRO.x, ZOCALO * 0.55f, CENTRO.z), R + 0.065f, cerca = true, n = 14)
        for (q in pernos.drop(1).dropLast(1)) drawCircle(v.c(q, lerp(C.ACERO, Color.White, 0.3f), q - CENTRO), (0.012f * CamLab.k(q)).coerceAtLeast(0.4f), CamLab.p(q))
        // ---- Lo de dentro: el estante con las bandejas de prueba (o, en el experimento, el catre, las cajas y las veinte bandejas).
        clipPath(forma) {
            interior(v, experimento)
            if (dentro > 0.02f) {
                val col = EscenaLaboratorio.colorDeLaCampana(m)
                val c = CamLab.p(CENTRO.x, 1.3f, CENTRO.z); val k = CamLab.k(CENTRO)
                drawOval(Brush.radialGradient(listOf(col.copy(alpha = 0.32f * dentro), col.copy(alpha = 0.1f * dentro), Color.Transparent), c, 1.3f * k), c - Offset(1.3f * k, 1.3f * k), Size(2.6f * k, 2.6f * k), blendMode = BlendMode.Plus)
                // La lámpara de dentro: un tubo bajo la tapa, sobre el estante (de día, de cultivo; de noche, el ámbar bajo).
                val t0 = CamLab.p(CENTRO.x - 0.5f, 2.2f, CENTRO.z + 0.1f); val t1 = CamLab.p(CENTRO.x + 0.55f, 2.2f, CENTRO.z + 0.22f)
                drawLine(lerp(C.ACERO, C.NEGRO, 0.3f), t0 + Offset(0f, -1.2f), t1 + Offset(0f, -1.2f), 2.2f)
                drawLine(lerp(col, Color.White, 0.55f).copy(alpha = (0.95f * dentro).coerceIn(0f, 1f)), t0, t1, 1.3f, StrokeCap.Round)
                // Su luz cae sobre las bandejas del estante: un cono en el aire húmedo de dentro.
                val b0 = CamLab.p(CENTRO.x - 0.6f, 0.5f, CENTRO.z + 0.1f); val b1 = CamLab.p(CENTRO.x + 0.65f, 0.5f, CENTRO.z + 0.25f)
                drawPath(poligono(t0, t1, b1, b0), Brush.verticalGradient(0f to col.copy(alpha = 0.22f * dentro), 1f to col.copy(alpha = 0.04f * dentro), startY = t0.y, endY = b0.y), blendMode = BlendMode.Plus)
            }
            // De noche, lo de dentro se apaga y el vidrio devuelve la sala: «se vuelve un espejo».
            if (noche) drawPath(forma, lerp(C.SOMBRA, C.NEGRO, 0.4f).copy(alpha = (0.42f * (1f - 0.7f * dentro)).coerceIn(0f, 1f)))
        }
        // ---- El vidrio de delante: el tinte, los montantes, la esclusa, el canto grueso, los brillos y las gotas.
        vidrio(v, forma, experimento)
        // ---- Los aros de acero, arriba y abajo, y la tapa con el conducto del aire de arriba.
        aros(v)
    }

    /**
     * Lo de detrás deformado: el rincón de las dos paredes y el pie de la pared del fondo se ven, a través del cilindro,
     * torcidos hacia su eje y más juntos, como en un acuario. **[Aproximación]**: no es la refracción exacta; basta con que
     * las líneas rectas de detrás dejen de serlo.
     */
    private fun DrawScope.refraccion(v: VistaLab) {
        val c = CamLab.p(CENTRO.x, 1.4f, CENTRO.z).x; val k = CamLab.k(CENTRO); val r = R * k
        fun torcer(x: Float): Float { val d = ((x - c) / r).coerceIn(-1f, 1f); return c + r * (d * 0.62f + 0.18f * d * d * d) }
        val esquina = CamLab.p(SalaLab.XD, 0f, SalaLab.ZF); val esquinaArriba = CamLab.p(SalaLab.XD, SalaLab.TECHO, SalaLab.ZF)
        val linea = lerp(C.HORMIGON, C.SOMBRA, 0.5f)
        val xr = torcer(esquina.x)
        drawLine(v.c(V3(SalaLab.XD, 1f, SalaLab.ZF), linea, FRENTE).copy(alpha = 0.6f), Offset(xr, esquinaArriba.y), Offset(xr, esquina.y + 4f), 0.9f)
        // El pie de la pared del fondo y el del rincón, curvados.
        val pie = Path(); var primero = true
        for (i in 0..20) { val x = CamLab.p(-0.6f + i * 0.18f, 0f, SalaLab.ZF); val p = Offset(torcer(x.x), x.y + 3f * (1f - ((x.x - c) / r).let { it * it })); if (primero) { pie.moveTo(p.x, p.y); primero = false } else pie.lineTo(p.x, p.y) }
        drawPath(pie, v.c(V3(1f, 0f, SalaLab.ZF), lerp(C.PISO, C.SOMBRA, 0.5f), ARRIBA).copy(alpha = 0.55f), style = Stroke(0.8f))
        // El tubo del canal de cables de la pared derecha, que baja torcido.
        val t = CamLab.p(SalaLab.XD - 0.05f, 2.0f, 5.6f)
        drawLine(v.c(V3(SalaLab.XD, 2f, 5.6f), lerp(C.ACERO, C.HORMIGON, 0.3f), PiezasLab.IZQ).copy(alpha = 0.4f), Offset(torcer(t.x), t.y - 40f), Offset(torcer(t.x) + 2f, t.y + 30f), 1.2f)
    }

    /** Lo que hay dentro de la campana. */
    private fun DrawScope.interior(v: VistaLab, experimento: Boolean) {
        val m = v.m
        val grama = EscenaLaboratorio.grama(m)
        // El suelo de dentro: la chapa del zócalo, con tierra derramada.
        drawPath(poligono(PiezasLab.aro(V3(CENTRO.x, ZOCALO, CENTRO.z), R - 0.02f, 20).map { CamLab.p(it) }), v.c(V3(CENTRO.x, ZOCALO, CENTRO.z), lerp(C.ACERO, C.TIERRA, 0.4f), ARRIBA))
        // El estante de acero con las bandejas de prueba, contra el vidrio de detrás.
        val mk = PiezasLab.Marco(V3(CENTRO.x - 0.6f, ZOCALO, CENTRO.z + 0.12f), V3(1f, 0f, 0.12f).unidad())
        val rack = lerp(C.ACERO, C.NEGRO, 0.35f)
        for (x in listOf(0.0f, 1.2f)) for (b in listOf(0f, -0.34f)) palo(v, mk.w(x, b, 0f), mk.w(x, b, 1.95f), 0.03f, rack, filo = false)
        val tablas = if (experimento) listOf(0.55f, 1.05f, 1.55f) else listOf(0.45f, 1.0f, 1.55f)
        for ((ti, y) in tablas.withIndex()) {
            cajaEn(v, mk, 0f, 1.2f, -0.34f, 0f, y - 0.03f, y, rack)
            for (i in 0..3) bandejaDentro(v, mk, 0.05f + i * 0.29f, -0.26f, y, grama, ti * 4 + i)
        }
        // Las mangueras que entran por el zócalo (el agua y el aire de arriba) y el higrómetro colgado del estante.
        val m0 = CamLab.p(CENTRO.x - 0.9f, ZOCALO + 0.05f, CENTRO.z - 0.4f)
        val m1 = mk.p(0.1f, -0.1f, tablas.first())
        drawPath(Path().apply { moveTo(m0.x, m0.y); cubicTo(m0.x + 6f, m0.y + 2f, m1.x - 4f, m1.y + 14f, m1.x, m1.y) }, v.c(mk.w(0f, 0f, 0.3f), lerp(C.NEGRO, C.BARRO, 0.2f), PiezasLab.FRENTE), style = Stroke(1.1f, cap = StrokeCap.Round))
        val hg = mk.w(0.95f, 0.02f, 1.75f); val khg = CamLab.k(hg)
        drawCircle(v.c(hg, C.LATON, PiezasLab.FRENTE), 0.05f * khg, CamLab.p(hg)); drawCircle(v.c(hg, C.TELA, PiezasLab.FRENTE), 0.038f * khg, CamLab.p(hg))
        drawLine(v.c(hg, C.AVISO, PiezasLab.FRENTE), CamLab.p(hg), CamLab.p(hg) + Offset(0.025f * khg, -0.018f * khg), 0.5f)
        if (!experimento) {
            // Dos cajas de tierra de arriba en el suelo de dentro, esperando.
            for ((i, x) in listOf(-0.7f, 0.45f).withIndex()) {
                val cm = PiezasLab.Marco(V3(CENTRO.x + x, ZOCALO, CENTRO.z - 0.55f + 0.15f * i), V3(1f, 0f, 0.15f * (i * 2 - 1)).unidad())
                cajaEn(v, cm, 0f, 0.4f, -0.15f, 0.15f, 0f, 0.22f, C.MADERA) { cara, f -> if (cara == 3) drawPath(f, v.c(cm.w(0.2f, 0f, 0.22f), C.TIERRA, ARRIBA).copy(alpha = 0.9f)) }
            }
            return
        }
        // El catre plegable entre las cajas de tierra gris, con la manta revuelta y la mascarilla de tela.
        val catre = PiezasLab.Marco(V3(CENTRO.x - 0.6f, ZOCALO, CENTRO.z - 0.3f), V3(0.94f, 0f, -0.34f).unidad())
        cajaEn(v, catre, 0f, 1.6f, -0.3f, 0.3f, 0.38f, 0.44f, lerp(C.TELA, C.CIAN, 0.15f))
        cajaEn(v, catre, 0.1f, 1.0f, -0.28f, 0.28f, 0.44f, 0.5f, lerp(C.TELA, C.TIERRA, 0.3f))
        for (a in listOf(0.1f, 1.5f)) palo(v, catre.w(a, 0.25f, 0f), catre.w(a + 0.08f, -0.25f, 0.38f), 0.02f, C.ACERO, filo = false)
        // Las cajas de tierra de arriba, en el suelo.
        for ((i, x) in listOf(-0.75f, -0.35f, 0.55f).withIndex()) {
            val cm = PiezasLab.Marco(V3(CENTRO.x + x, ZOCALO, CENTRO.z - 0.65f + 0.1f * i), V3(1f, 0f, 0.2f * (i - 1)).unidad())
            cajaEn(v, cm, 0f, 0.38f, -0.15f, 0.15f, 0f, 0.26f, C.MADERA) { cara, f -> if (cara == 3) drawPath(f, v.c(cm.w(0.19f, 0f, 0.26f), C.TIERRA, ARRIBA).copy(alpha = 0.85f)) }
        }
        // La lámpara de dentro, colgada.
        val lp = V3(CENTRO.x, 2.25f, CENTRO.z)
        palo(v, V3(lp.x, ALTO - 0.15f, lp.z), lp, 0.008f, C.NEGRO, filo = false)
        drawCircle(lerp(Color(0xFFFFD08A), Color.White, 0.4f), 0.06f * CamLab.k(lp), CamLab.p(lp))
    }

    /** Una bandeja de prueba en el estante de la campana, con la grama según el arco. */
    private fun DrawScope.bandejaDentro(v: VistaLab, mk: PiezasLab.Marco, a: Float, b: Float, y: Float, grama: Grama, i: Int) {
        cajaEn(v, mk, a, a + 0.25f, b, b + 0.2f, y, y + 0.07f, lerp(C.NEGRO, C.ACERO, 0.45f)) { cara, f ->
            if (cara == 3) {
                drawPath(f, v.c(mk.w(a + 0.12f, b + 0.1f, y + 0.07f), C.TIERRA, ARRIBA))
                val brota = when (grama) { Grama.TIERRA -> 0; Grama.HILO -> if (i % 4 == 1) 3 else 0; Grama.MATAS, Grama.TANQUE, Grama.CAJAS -> 8 }
                for (j in 0 until brota) { val p = mk.p(a + 0.03f + j * 0.026f, b + 0.1f, y + 0.07f); val k = CamLab.k(mk.w(a, b, y))
                    drawLine(lerp(v.c(mk.w(a, b, y + 0.1f), C.GRAMA, ARRIBA), C.GRAMA, 0.3f), p, p + Offset(0.008f * k * (j % 3 - 1), -(0.06f + 0.02f * (j % 2)) * k), 0.7f, StrokeCap.Round) }
            }
        }
    }

    /** El vidrio de delante: tinte, montantes, la esclusa, el canto grueso y verdoso, los brillos de las luces y las gotas. */
    private fun DrawScope.vidrio(v: VistaLab, forma: Path, experimento: Boolean) {
        val m = v.m
        val izq = CamLab.p(mitad(CENTRO, R, true).first()).x; val der = CamLab.p(mitad(CENTRO, R, true).last()).x
        clipPath(forma) {
            // El tinte: más denso hacia los cantos, donde se mira a través de más vidrio.
            val canto = lerp(C.VIDRIO, C.GRAMA, 0.35f)
            drawRect(Brush.horizontalGradient(0f to canto.copy(alpha = 0.55f), 0.07f to canto.copy(alpha = 0.2f), 0.2f to C.VIDRIO.copy(alpha = 0.08f), 0.8f to C.VIDRIO.copy(alpha = 0.08f), 0.93f to canto.copy(alpha = 0.22f), 1f to canto.copy(alpha = 0.6f), startX = izq, endX = der), Offset(izq, 0f), Size(der - izq, 285f))
            // El filo claro del canto, a un dedo de cada borde.
            drawLine(Color.White.copy(alpha = 0.18f + 0.1f * v.general), Offset(izq + 2.2f, 20f), Offset(izq + 2.2f, 250f), 0.6f)
            drawLine(Color.White.copy(alpha = 0.12f + 0.08f * v.general), Offset(der - 2.4f, 20f), Offset(der - 2.4f, 250f), 0.6f)
            brillos(v)
            gotas(v, experimento)
            if (experimento) {
                // El polvo gris en el vidrio, más abajo, y la marca de una mano.
                drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to lerp(C.TIERRA, C.HORMIGON, 0.3f).copy(alpha = 0.28f), startY = CamLab.p(CENTRO.x, 1.2f, CENTRO.z - R).y, endY = CamLab.p(CENTRO.x, ZOCALO, CENTRO.z - R).y), Offset(izq, 0f), Size(der - izq, 285f))
                val mano = CamLab.p(CENTRO.x - 0.4f, 1.25f, CENTRO.z - R + 0.12f); val k = CamLab.k(CENTRO)
                drawOval(lerp(C.TIERRA, Color.White, 0.2f).copy(alpha = 0.3f), mano - Offset(0.05f * k, 0.04f * k), Size(0.1f * k, 0.09f * k))
                for (d in 0..3) drawLine(lerp(C.TIERRA, Color.White, 0.2f).copy(alpha = 0.3f), mano + Offset((d - 1.5f) * 0.024f * k, -0.04f * k), mano + Offset((d - 1.5f) * 0.03f * k, -0.11f * k), 0.022f * k, StrokeCap.Round)
            }
        }
        // Los montantes de acero, finos, que dicen la curva del cilindro.
        for (f in listOf(0.18f, 0.5f, 0.82f)) {
            val a = anguloDelanteroEn(f); val q0 = V3(CENTRO.x + R * cos(a), ZOCALO, CENTRO.z + R * sin(a)); val q1 = V3(q0.x, ALTO - 0.1f, q0.z)
            val n = V3(cos(a), 0f, sin(a))
            drawLine(v.c(q0 + V3(0f, 1.4f, 0f), C.ACERO, n), CamLab.p(q0), CamLab.p(q1), (0.03f * CamLab.k(q0)).coerceAtLeast(0.8f))
            drawLine(v.c(q0 + V3(0f, 1.4f, 0f), lerp(C.ACERO, Color.White, 0.4f), n).copy(alpha = 0.6f), CamLab.p(q0) + Offset(-0.4f, 0f), CamLab.p(q1) + Offset(-0.4f, 0f), 0.4f)
        }
        // La esclusa: una portezuela en el vidrio, del lado de la mesa, con su junta de goma y su palanca.
        val e0 = anguloDelanteroEn(0.22f); val e1 = anguloDelanteroEn(0.42f)
        val puertita = (0..8).map { i -> val a = e0 + (e1 - e0) * i / 8f; V3(CENTRO.x + R * cos(a), 0f, CENTRO.z + R * sin(a)) }
        val marco = puertita.map { CamLab.p(it.x, ZOCALO + 0.1f, it.z) } + puertita.reversed().map { CamLab.p(it.x, 2.05f, it.z) }
        drawPath(poligono(marco), v.c(puertita[4] + V3(0f, 1f, 0f), lerp(C.ACERO, C.HORMIGON, 0.2f), puertita[4] - CENTRO), style = Stroke(1.4f))
        drawPath(poligono(marco), v.c(puertita[4] + V3(0f, 1f, 0f), C.NEGRO, puertita[4] - CENTRO).copy(alpha = 0.6f), style = Stroke(0.45f))
        val pal = puertita[7]; val pp = CamLab.p(pal.x, 1.1f, pal.z); val kp = CamLab.k(pal)
        drawLine(v.c(pal + V3(0f, 1.1f, 0f), lerp(C.ACERO, Color.White, 0.3f), pal - CENTRO), pp, pp + Offset(0f, 0.22f * kp), (0.03f * kp).coerceAtLeast(0.8f), StrokeCap.Round)
        // El intercomunicador, en el zócalo junto a la esclusa (una rejilla y un piloto).
        val ic = puertita[0]; val pi = CamLab.p(ic.x, 1.25f, ic.z); val ki = CamLab.k(ic)
        drawRect(v.c(ic + V3(0f, 1.25f, 0f), lerp(C.ACERO, C.HORMIGON, 0.3f), ic - CENTRO), pi - Offset(0.05f * ki, 0.07f * ki), Size(0.1f * ki, 0.14f * ki))
        for (j in 0..3) drawLine(v.c(ic, C.NEGRO, FRENTE).copy(alpha = 0.6f), pi + Offset(-0.035f * ki, (-0.04f + j * 0.022f) * ki), pi + Offset(0.035f * ki, (-0.04f + j * 0.022f) * ki), 0.4f)
        drawCircle(if (experimento) lerp(C.GRAMA, Color.White, 0.4f) else lerp(C.ACERO, C.NEGRO, 0.3f), 0.5f, pi + Offset(0f, 0.05f * ki))
    }

    /**
     * Los brillos largos: cada luz que alumbra la campana se ve reflejada como una franja vertical en el punto del
     * cilindro cuya cara está a medio camino entre la luz y los ojos. De día, las lámparas de cultivo (a la izquierda) y la
     * pantalla (a la derecha); de noche, además, la lámpara de su mesa, cálida, y el violeta de la puerta.
     */
    private fun DrawScope.brillos(v: VistaLab) {
        val ojos = V3(0f, CamLab.OJO, 0f)
        for (f in v.luces) {
            if (f.fuerza < 0.08f || f.de == Fuente.CAMPANA || f.de == Fuente.REBOTE || f.de == Fuente.PANEL) continue
            val alC = (V3(f.w.x, 0f, f.w.z) - V3(CENTRO.x, 0f, CENTRO.z)).unidad(); val alO = (V3(ojos.x, 0f, ojos.z) - V3(CENTRO.x, 0f, CENTRO.z)).unidad()
            val n = (alC + alO).unidad(); if (n.largo() < 0.1f) continue
            val q = V3(CENTRO.x + n.x * R, 0f, CENTRO.z + n.z * R)
            if (!CamLab.seVe(V3(q.x, 1f, q.z), n)) continue
            val dist = (f.w - V3(q.x, 1.3f, q.z)).largo()
            val k = (f.fuerza / (1f + dist * dist / (f.alcance * f.alcance * 3f)) * (if (v.noche) 1.4f else 0.8f)).coerceIn(0f, 1f)
            if (k < 0.04f) continue
            val p0 = CamLab.p(q.x, ALTO - 0.2f, q.z); val p1 = CamLab.p(q.x, ZOCALO + 0.15f, q.z)
            val ancho = (if (f.de == Fuente.PANTALLA || f.de == Fuente.PUERTA) 0.09f else 0.035f) * CamLab.k(q)
            val altoLuz = ((f.w.y - ZOCALO) / (ALTO - ZOCALO)).coerceIn(0.15f, 0.9f)
            val centroY = p1.y + (p0.y - p1.y) * altoLuz
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 0.3f to f.color.copy(alpha = 0.35f * k), (centroY - p0.y) / (p1.y - p0.y).coerceAtLeast(1f) to f.color.copy(alpha = 0.75f * k), 0.85f to f.color.copy(alpha = 0.25f * k), 1f to Color.Transparent, startY = p0.y, endY = p1.y),
                Offset(p0.x - ancho / 2f, p0.y), Size(ancho, p1.y - p0.y), blendMode = BlendMode.Screen)
        }
    }

    /** Las gotas de condensación por dentro: más en la mitad de abajo, cada una con su punto de luz, alguna con su chorrera. */
    private fun DrawScope.gotas(v: VistaLab, experimento: Boolean) {
        val az = Azar(Semilla.de("laboratorio", "gotas"))
        val n = if (experimento) 70 else 46
        repeat(n) {
            val f = az.entre(0.08f, 0.92f); val y = ZOCALO + 0.2f + (ALTO - 0.6f) * (az.real() * az.real())
            val a = anguloDelanteroEn(f); val q = V3(CENTRO.x + R * cos(a), y, CENTRO.z + R * sin(a)); val p = CamLab.p(q); val k = CamLab.k(q)
            val r = az.entre(0.006f, 0.016f) * k
            drawCircle(v.c(q, lerp(C.VIDRIO, C.SOMBRA, 0.3f), FRENTE).copy(alpha = 0.35f), r, p)
            drawCircle(Color.White.copy(alpha = 0.35f + 0.25f * v.general), r * 0.35f, p + Offset(-r * 0.3f, -r * 0.3f))
            if (az.real() < 0.15f) drawLine(v.c(q, C.VIDRIO, FRENTE).copy(alpha = 0.3f), p, p + Offset(0f, az.entre(0.05f, 0.2f) * k), r * 0.5f)
        }
    }

    /** Los aros de acero (abajo, sobre el zócalo; arriba, con sus pernos) y la tapa, con el conducto que sube al techo. */
    private fun DrawScope.aros(v: VistaLab) {
        // El aro de abajo, que sujeta el vidrio.
        val abajoF = mitad(V3(CENTRO.x, ZOCALO, CENTRO.z), R + 0.03f, true); val abajoA = mitad(V3(CENTRO.x, ZOCALO + 0.09f, CENTRO.z), R + 0.03f, true)
        drawPath(poligono(abajoF.map { CamLab.p(it) } + abajoA.reversed().map { CamLab.p(it) }), Brush.horizontalGradient(0f to v.c(abajoF.first() + V3(0f, 0.05f, 0f), C.ACERO, abajoF.first() - CENTRO), 0.5f to v.c(abajoF[abajoF.size / 2], C.ACERO, abajoF[abajoF.size / 2] - CENTRO), 1f to v.c(abajoF.last(), C.ACERO, abajoF.last() - CENTRO), startX = CamLab.p(abajoF.first()).x, endX = CamLab.p(abajoF.last()).x))
        // El aro de arriba: más ancho, por encima de los ojos (se ve su cara de abajo y su canto).
        val y0 = ALTO - 0.14f; val y1 = ALTO
        val d0 = mitad(V3(CENTRO.x, y0, CENTRO.z), R + 0.04f, true); val d1 = mitad(V3(CENTRO.x, y1, CENTRO.z), R + 0.04f, true)
        drawPath(poligono(d0.map { CamLab.p(it) } + d1.reversed().map { CamLab.p(it) }), Brush.horizontalGradient(0f to v.c(d0.first(), C.ACERO, d0.first() - CENTRO), 0.5f to v.c(d0[d0.size / 2], lerp(C.ACERO, C.HORMIGON, 0.2f), d0[d0.size / 2] - CENTRO), 1f to v.c(d0.last(), C.ACERO, d0.last() - CENTRO), startX = CamLab.p(d0.first()).x, endX = CamLab.p(d0.last()).x))
        for (q in mitad(V3(CENTRO.x, y0 + 0.07f, CENTRO.z), R + 0.045f, true, 12).drop(1).dropLast(1)) drawCircle(v.c(q, lerp(C.ACERO, Color.White, 0.3f), q - CENTRO), 0.5f, CamLab.p(q))
        // La tapa (su cara de abajo, apenas) y el tubo del aire de arriba, que sube al conducto del techo con su abrazadera.
        val tubo = V3(SalaLab.CONDUCTO_X, y1, CENTRO.z + 0.1f)
        cilindro(v, tubo, 0.15f, SalaLab.CONDUCTO_Y - y1, lerp(C.ACERO, C.HORMIGON, 0.2f), tapa = null)
        cilindro(v, V3(tubo.x, y1 + 0.05f, tubo.z), 0.19f, 0.06f, lerp(C.ACERO, C.NEGRO, 0.2f), tapa = null)
    }
}
