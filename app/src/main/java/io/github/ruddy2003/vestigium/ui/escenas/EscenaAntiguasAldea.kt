package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Senal
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAntiguas.lucecita
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAntiguas.runa
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.BAJADA
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.PIEDRA_DEL_CUERNO
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.PUENTE_ACA
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.PUENTE_ALLA
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.TORRENTE
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.fijo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.borron
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import kotlin.math.sin

/**
 * El fondo del valle, de este lado del torrente: el torrente mismo (la superficie con hondura del cuadro: pozas verdes y
 * hondas, saltos blancos entre las piedras, las orillas mojadas, el reflejo del puente), el puente de troncos, un trozo de
 * la aldea («cuarenta casas de piedra con tejados de losa… Todas las puertas miran al paso») en tres hileras que bajan, con
 * la casa del Gremio y la herrería, y la plaza con la piedra donde suena el cuerno, donde se ponen las fiestas.
 */
internal object AldeaAntiguas {
    private val C = ColorAntiguas

    /** Una casa de la aldea: dónde pisa la esquina de delante, el ancho de su testero y de su costado, el alto del muro y del tejado. */
    class Casa(val pie: Offset, val testero: Float, val costado: Float, val alto: Float, val tejado: Float, val espejo: Boolean = false, val gremio: Boolean = false, val fragua: Boolean = false, val madera: Boolean = false, val corral: Boolean = false)

    /** Las casas, de atrás adelante: tres hileras que bajan hacia la izquierda. */
    val CASAS = listOf(
        Casa(Offset(70f, 222f), 12f, 18f, 8f, 7.6f, madera = true), Casa(Offset(96f, 220f), 9f, 13f, 7f, 5.4f, espejo = true), Casa(Offset(122f, 218.6f), 14f, 12f, 9f, 8.4f, corral = true),
        Casa(Offset(150f, 217f), 10f, 15f, 6.6f, 6.6f, espejo = true, madera = true), Casa(Offset(174f, 216f), 13f, 10f, 7.4f, 5.6f, fragua = true),
        Casa(Offset(62f, 238f), 16f, 22f, 11f, 9.6f, corral = true), Casa(Offset(110f, 236f), 22f, 28f, 14f, 12f, gremio = true), Casa(Offset(150f, 233f), 12f, 17f, 9f, 9f, espejo = true, madera = true),
        Casa(Offset(186f, 231f), 14f, 12f, 10f, 7f, corral = true),
        Casa(Offset(84f, 256f), 20f, 26f, 14f, 12f, espejo = true, madera = true), Casa(Offset(132f, 254f), 17f, 21f, 12f, 9f),
    )

    /** Dónde sube el humo de cada chimenea (la parte viva). */
    val CHIMENEAS: List<Offset> get() = CASAS.filterIndexed { i, _ -> i % 2 == 0 || CASAS[i].fragua }.map { chimenea(it) }

    private fun chimenea(c: Casa): Offset = c.pie + Offset((if (c.espejo) -1f else 1f) * c.costado * 0.55f, -c.alto - c.tejado * 0.8f - c.costado * 0.09f)

    /** La plaza: la tierra pisada delante de la posada, hasta el puente. */
    val PLAZA = pts(172f, 228f, 200f, 220f, 236f, 215f, 268f, 213f, 270f, 246f, 240f, 252f, 204f, 251f, 178f, 245f)

    fun DrawScope.valle(v: VistaAntiguas) {
        val az = fijo("aldea")
        suelo(v, az)
        torrente(v, az)
        puente(v)
        for (c in CASAS.take(5)) casa(v, c, az)
        plaza(v, az)
        molino(v, az)
        for (c in CASAS.drop(5)) casa(v, c, az)
        fiestas(v, az)
    }

    // ---------------------------------------------------------------- El suelo

    /** La orilla de acá: prado en pendiente hacia el agua, con la tierra pisada del camino; con nieve, blanca y con zanjas entre las casas. */
    private fun DrawScope.suelo(v: VistaAntiguas, az: Azar) {
        val p = v.p; val hierba = EscenaAntiguas.hierba(v.m)
        val orilla = TORRENTE.map { (q, w) -> q + Offset(0f, w + 0.4f) }.reversed()
        val forma = poligono(orilla + listOf(Offset(392f, 190f), Offset(392f, 268f), Offset(-10f, 268f)))
        drawPath(forma, Brush.verticalGradient(0f to p.medio(lerp(hierba, C.PINO, 0.2f), 0.1f), 1f to p.medio(lerp(hierba, C.TIERRA, 0.3f), 0.04f), startY = 200f, endY = 268f))
        clipPath(forma) {
            val puntos = (0 until 300).map { Offset(az.entre(-6f, 386f), az.entre(206f, 268f)) }
            drawPoints(puntos.filterIndexed { i, _ -> i % 2 == 0 }, PointMode.Points, p.luz(lerp(hierba, C.ORO, 0.25f), 0.08f).copy(alpha = 0.45f), 1.1f, StrokeCap.Round)
            drawPoints(puntos.filterIndexed { i, _ -> i % 2 == 1 }, PointMode.Points, p.sombra(lerp(hierba, C.PINO, 0.4f), 0.08f).copy(alpha = 0.45f), 1.1f, StrokeCap.Round)
            // La orilla: piedras mojadas y grava, la franja de paso entre el prado y el agua.
            for ((q, w) in TORRENTE) { repeat(3) { val c = q + Offset(az.entre(-8f, 8f), w + az.entre(0.5f, 3f)); drawPath(borron(c, az.entre(1.2f, 2.6f), 0.9f, az, 7, 0.3f), p.por(lerp(C.PIEDRA, C.ESCORIA, 0.25f), 0.2f, 0.1f)) } }
            if (v.nieveAbajo > 0.02f) nieve(v, az)
        }
    }

    /** La nieve de abajo: un manto que borra el prado; con el paso cerrado, las sendas entre las casas, «zanjas de nieve más altas que un niño». */
    private fun DrawScope.nieve(v: VistaAntiguas, az: Azar) {
        val p = v.p; val n = v.nieveAbajo
        if (n < 0.99f) {
            // Manchas de nieve vieja o recién caída en el prado, más donde da la sombra.
            repeat((40 + 140 * n).toInt()) { val q = Offset(az.entre(-6f, 386f), az.entre(206f, 266f)); val w = az.entre(2f, 7f) * (0.6f + n) * (0.6f + (q.y - 200f) / 70f)
                drawPath(TrazosValle.blando(q, w, w * 0.28f, az, 8, 0.25f), p.por(C.NIEVE, 0.3f, 0.06f)); drawLine(p.sombra(C.NIEVE_SOMBRA, 0.06f).copy(alpha = 0.5f), q + Offset(-w * 0.7f, w * 0.3f), q + Offset(w * 0.7f, w * 0.3f), 0.6f) }
            return
        }
        drawRect(Brush.verticalGradient(listOf(p.por(C.NIEVE, 0.4f, 0.1f), p.por(C.NIEVE, 0.15f, 0.04f)), 200f, 268f), Offset(-10f, 196f), Size(400f, 74f))
        val zanja = p.sombra(C.NIEVE_SOMBRA, 0.06f)
        for (r in listOf(pts(40f, 246f, 70f, 240f, 100f, 244f, 126f, 240f, 160f, 238f, 196f, 236f, 226f, 238f), pts(110f, 238f, 104f, 230f, 112f, 222f), pts(160f, 238f, 168f, 226f))) {
            drawPath(curva(r), zanja.copy(alpha = 0.55f), style = Stroke(3.2f, cap = StrokeCap.Round))
            drawPath(curva(r.map { it + Offset(0f, -1.4f) }), p.luz(C.NIEVE, 0.06f).copy(alpha = 0.6f), style = Stroke(0.8f, cap = StrokeCap.Round))
        }
    }

    // ---------------------------------------------------------------- El torrente

    /**
     * El torrente: honda y verde en las pozas, más clara donde corre sobre las piedras, con los saltos blancos y las piedras
     * que asoman; devuelve el cielo en lo quieto y el puente encima. Con lluvia, crecido y turbio. Con el paso cerrado,
     * tapado por la nieve: solo un hilo negro de agua que corre y el hielo de las orillas.
     */
    private fun DrawScope.torrente(v: VistaAntiguas, az: Azar) {
        val p = v.p; val l = v.l
        val crecido = v.clima == Clima.LLUVIA || v.clima == Clima.TORMENTA || EscenaAntiguas.hoyEs(v.m, "abre_paso")
        val ancho = if (crecido) 1.25f else 1f
        val izq = TORRENTE.map { (q, w) -> q + Offset(0f, -w * ancho) }; val der = TORRENTE.map { (q, w) -> q + Offset(0f, w * ancho) }
        val agua = poligono(izq + der.reversed())
        if (v.invierno) {
            drawPath(agua, p.por(C.NIEVE, 0.25f, 0.12f))
            val hilo = TORRENTE.map { (q, w) -> q + Offset(0f, w * 0.2f * sin(q.x * 0.1f)) }
            drawPath(curva(hilo), p.medio(C.AGUA_HONDA, 0.12f).copy(alpha = 0.85f), style = Stroke(1.6f, cap = StrokeCap.Round))
            drawPath(curva(izq.map { it + Offset(0f, 1f) }), p.sombra(C.NIEVE_SOMBRA, 0.12f).copy(alpha = 0.6f), style = Stroke(1.2f))
            return
        }
        val turbio = if (crecido) 0.45f else 0f
        val hondo = lerp(C.AGUA_HONDA, C.TIERRA, turbio); val claro = lerp(lerp(C.AGUA, C.NIEVE, 0.25f), C.TIERRA, turbio)
        drawPath(agua, Brush.verticalGradient(0f to lerp(p.medio(hondo, 0.15f), l.bajo, 0.15f), 0.5f to p.medio(lerp(hondo, claro, 0.4f), 0.1f), 1f to p.medio(claro, 0.05f), startY = 190f, endY = 252f))
        clipPath(agua) {
            // Lo quieto devuelve el cielo; las pozas, hondas; el agua que corre, en rayas que siguen la corriente.
            for ((i, qw) in TORRENTE.withIndex()) {
                val (q, w) = qw
                if (i % 3 == 0) mancha(q + Offset(0f, -w * 0.2f), w * 2.4f, w * 0.5f, lerp(l.bajo, Color.White, 0.25f), 0.14f * (1f - v.noche * 0.7f))
                // Las pozas, verdes y hondas, con su borde más claro.
                if (i % 4 == 1) { mancha(q, w * 2.4f, w * 0.75f, p.medio(lerp(lerp(hondo, C.PINO, 0.35f), C.ESCORIA, 0.15f), 0.1f), 0.75f); mancha(q + Offset(-w * 0.4f, -w * 0.2f), w * 1.2f, w * 0.35f, p.medio(lerp(C.AGUA, C.ALERCE, 0.3f), 0.08f), 0.4f) }
                repeat(4) { val c = q + Offset(az.entre(-9f, 9f), az.entre(-w, w) * 0.8f); drawLine(lerp(l.bajo, Color.White, 0.45f).copy(alpha = 0.3f + 0.2f * az.real()), c, c + Offset(-az.entre(3f, 7f), az.entre(0.4f, 1.4f)), 0.5f, StrokeCap.Round) }
            }
            // Las piedras que asoman, con su espuma aguas arriba.
            repeat(16) {
                val (q, w) = TORRENTE[1 + az.entero(TORRENTE.size - 2)]
                val c = q + Offset(az.entre(-6f, 6f), az.entre(-w, w) * 0.7f); val r = az.entre(0.9f, 1.4f) + w * 0.18f
                drawPath(borron(c, r * 1.3f, r * 0.8f, az, 7, 0.3f), p.por(lerp(C.PIEDRA, C.ESCORIA, 0.35f), 0.3f, 0.1f))
                drawPath(curva(pts(c.x + r * 1.3f, c.y - r * 0.3f, c.x + r * 2.2f, c.y, c.x + r * 1.3f, c.y + r * 0.5f)), lerp(l.bajo, Color.White, 0.7f).copy(alpha = 0.75f * (1f - 0.5f * v.noche)), style = Stroke(r * 0.6f, cap = StrokeCap.Round))
            }
            // Los saltos: el agua que cae entre las piedras, en bandas blancas de espuma de orilla a orilla.
            for (k in listOf(3, 7, 11, 15)) {
                if (k >= TORRENTE.size) continue
                val (q, w) = TORRENTE[k]
                val espuma = lerp(l.bajo, Color.White, 0.75f).copy(alpha = (0.85f * (1f - 0.55f * v.noche)).coerceIn(0f, 1f))
                drawPath(curva(listOf(q + Offset(1.5f, -w * 1.1f), q + Offset(-1f, 0f), q + Offset(1f, w * 1.1f))), espuma, style = Stroke(w * 0.55f, cap = StrokeCap.Round))
                repeat(6) { val c = q + Offset(az.entre(-6f, 0f), az.entre(-w, w)); drawLine(espuma.copy(alpha = espuma.alpha * 0.6f), c, c + Offset(-az.entre(2f, 5f), az.entre(-0.4f, 0.4f)), 0.6f, StrokeCap.Round) }
            }
            // El reflejo del puente.
            if (EscenaAntiguas.hayPuente(v.m)) reflejoRoto(PUENTE_ALLA.x - 2f, PUENTE_ACA.x + 2f, SitioAntiguas.torrenteEn((PUENTE_ACA.x + PUENTE_ALLA.x) / 2f) - 2f, 8f, p.sombra(C.MADERA_OSC, 0.1f), 0.5f, az, 0.9f)
            // De noche, la luna en el agua.
            if (v.luna > 0.05f) for ((q, w) in TORRENTE.filterIndexed { i, _ -> i % 2 == 0 }) drawLine(C.LUNA.copy(alpha = 0.4f * v.luna), q + Offset(-3f, -w * 0.3f), q + Offset(3f, -w * 0.3f), 0.7f, StrokeCap.Round)
        }
        // La orilla de allá, mojada: un filo oscuro bajo la ladera.
        drawPath(curva(izq), p.sombra(lerp(C.TIERRA, C.ESCORIA, 0.4f), 0.12f).copy(alpha = 0.7f), style = Stroke(1.2f))
        drawPath(curva(der), p.luz(lerp(C.PIEDRA, C.NIEVE, 0.3f), 0.08f).copy(alpha = 0.5f), style = Stroke(0.8f))
    }

    /**
     * El puente de troncos: tres troncos juntos, de orilla a orilla, con su pasamanos de un palo sobre dos estacas y su
     * sombra en el agua. No está con el camino borrado («ni puente»).
     */
    private fun DrawScope.puente(v: VistaAntiguas) {
        if (!EscenaAntiguas.hayPuente(v.m)) return
        val p = v.pincelEn(PUENTE_ACA); val a = PUENTE_ACA; val b = PUENTE_ALLA
        val d = b - a; val n = Offset(-d.y, d.x) / d.getDistance()
        drawPath(poligono(a + n * 5f + Offset(3f, 2f), b + n * 4f + Offset(3f, 2f), b - n * 3f + Offset(3f, 2f), a - n * 3f + Offset(3f, 2f)), p.r.color.copy(alpha = 0.5f * p.r.fuerza), blendMode = BlendMode.Multiply)
        for (k in -1..1) {
            val o = n * (k * 2.8f)
            drawLine(p.sombra(C.MADERA_OSC, 0.08f), a + o + Offset(0f, 1f), b + o + Offset(0f, 1f), 3f, StrokeCap.Round)
            drawLine(p.por(lerp(C.MADERA, C.FRESNO, 0.25f), 0.3f, 0.08f), a + o, b + o, 2.5f, StrokeCap.Round)
            drawLine(p.luz(C.FRESNO, 0.08f).copy(alpha = 0.6f * p.r.dia), a + o + Offset(0f, -0.7f), b + o + Offset(0f, -0.7f), 0.6f, StrokeCap.Round)
            drawCircle(p.por(C.FRESNO, 0.4f, 0.08f), 1.3f, a + o)
        }
        // Las piedras de los estribos, en cada orilla.
        for (q in listOf(a, b)) { drawPath(borron(q + Offset(0f, 1.6f), 5f, 2.4f, fijo("estribo", q.x), 8, 0.3f), p.por(C.PIEDRA, 0.15f, 0.08f)) }
        // El pasamanos, del lado de aguas abajo.
        val e1 = a + n * 4.4f; val e2 = b + n * 4.2f; val em = (e1 + e2) / 2f
        for ((q, h) in listOf(e1 to 8f, em to 7.2f, e2 to 6.6f)) drawLine(p.por(C.MADERA_OSC, 0.1f, 0.08f), q, q + Offset(0f, -h), 0.9f)
        drawLine(p.por(C.MADERA, 0.25f, 0.08f), e1 + Offset(0f, -7.6f), e2 + Offset(0f, -6.2f), 0.9f, StrokeCap.Round)
        if (v.nieveAbajo > 0.3f) drawLine(p.luz(C.NIEVE, 0.08f).copy(alpha = v.nieveAbajo), a + Offset(0f, -1.6f), b + Offset(0f, -1.6f), 2.6f, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- Las casas

    /**
     * Una casa de piedra con tejado de losa, en tres cuartos: el testero (la cara del piñón, con su puerta: «todas las
     * puertas miran al paso») y el costado, cada uno con su luz y con su piedra; el piñón de tablas en las que lo tienen;
     * el tejado empinado de losas en hiladas desiguales, con su caballete de losas cruzadas y su alero; la chimenea; el
     * corral de piedra seca arrimado, en las que lo tienen; una ventana pequeña que se enciende de noche (y la noche de las
     * linternas, con su linterna y la nieve dorada debajo). La del Gremio, más grande y más vieja; la herrería, con su boca
     * encendida y su humo.
     */
    private fun DrawScope.casa(v: VistaAntiguas, c: Casa, az: Azar) {
        val e = if (c.espejo) -1f else 1f
        val pie = c.pie; val p = v.pincelEn(pie + Offset(0f, -c.alto * 0.5f))
        val lejos = ((250f - pie.y) / 120f).coerceIn(0f, 0.3f)
        val sube = c.costado * 0.09f
        val t0 = pie + Offset(-e * c.testero, 0f); val t1 = pie
        val s1 = pie + Offset(e * c.costado, -sube)
        val cima = (t0 + t1) / 2f + Offset(0f, -c.alto - c.tejado)
        val cimaAtras = cima + Offset(e * c.costado, -sube)
        val muro = lerp(lerp(C.PIEDRA, C.LANA, 0.26f), if (c.gremio) C.ROCA else C.TIERRA, az.entre(0.08f, 0.24f))
        val tTestero = ((v.sol.t(V3(-e * 0.72f, 0f, -0.7f)) - 0.2f) * 1.8f).coerceIn(-1f, 1f)
        val tCostado = ((v.sol.t(V3(e * 0.72f, 0f, -0.7f)) - 0.2f) * 1.8f).coerceIn(-1f, 1f)
        // La sombra en el suelo, del lado contrario al sol.
        drawOval(p.r.color.copy(alpha = 0.5f * p.r.fuerza), Offset(minOf(t0.x, s1.x) - (if (p.solALaDerecha) 7f else -2f), pie.y - 2.8f), Size(c.testero + c.costado + 7f, 5f), blendMode = BlendMode.Multiply)
        if (c.corral) corral(v, c, e, p, lejos, az)
        val testero = poligono(listOf(t0, t1, t1 + Offset(0f, -c.alto), cima, t0 + Offset(0f, -c.alto)))
        val costado = poligono(listOf(t1, s1, s1 + Offset(0f, -c.alto), t1 + Offset(0f, -c.alto)))
        drawPath(testero, p.por(muro, tTestero, lejos))
        drawPath(costado, p.por(muro, tCostado, lejos))
        // La piedra: hiladas quebradas y alguna piedra suelta más clara o más oscura, en las dos caras.
        val junta = p.sombra(muro, lejos).copy(alpha = 0.4f)
        for (k in 1..3) {
            val y = -c.alto * k / 4f + az.entre(-0.4f, 0.4f)
            drawLine(junta, t0 + Offset(0f, y), t1 + Offset(0f, y + az.entre(-0.3f, 0.3f)), 0.35f)
            drawLine(junta, t1 + Offset(0f, y), s1 + Offset(0f, y - sube * 0.1f), 0.35f)
        }
        repeat((c.costado * 0.5f).toInt()) {
            val f = az.entre(0.05f, 0.95f); val q = t1 + (s1 - t1) * f + Offset(0f, -c.alto * az.entre(0.1f, 0.9f))
            drawRect(p.por(lerp(muro, if (it % 2 == 0) C.NIEVE else C.ESCORIA, 0.2f), tCostado, lejos), q, Size(c.costado * 0.12f, c.alto * 0.08f))
        }
        // El piñón de tablas, en las casas que lo tienen; y la puerta en el testero, oscura, con su dintel.
        if (c.madera) {
            val pinon = poligono(listOf(t0 + Offset(0f, -c.alto * 0.92f), t1 + Offset(0f, -c.alto * 0.92f), cima))
            val tabla = lerp(C.MADERA, C.PIEDRA, 0.3f)
            drawPath(pinon, p.por(tabla, tTestero - 0.1f, lejos))
            clipPath(pinon) { var x = minOf(t0.x, t1.x); while (x < maxOf(t0.x, t1.x)) { drawLine(p.sombra(tabla, lejos).copy(alpha = 0.6f), Offset(x, cima.y), Offset(x, t1.y - c.alto * 0.9f), 0.4f); x += 1.6f } }
        }
        val pm = t0 + (t1 - t0) * 0.52f
        drawRect(p.medio(lerp(C.MADERA_OSC, C.ESCORIA, 0.3f), lejos), pm + Offset(-c.testero * 0.12f, -c.alto * 0.62f), Size(c.testero * 0.24f, c.alto * 0.62f))
        drawLine(p.por(lerp(muro, C.NIEVE, 0.3f), tTestero + 0.3f, lejos), pm + Offset(-c.testero * 0.16f, -c.alto * 0.64f), pm + Offset(c.testero * 0.16f, -c.alto * 0.64f), 0.7f)
        // El tejado de losa: el faldón que se ve, con sus hiladas desiguales, su alero y su caballete.
        val alero = Offset(0f, 1f)
        val abajoT = t1 + Offset(0f, -c.alto) + alero + Offset(-e * 0.8f, 0f); val abajoS = s1 + Offset(0f, -c.alto) + alero + Offset(e * 1f, 0f)
        val faldon = poligono(listOf(cima + Offset(-e * 0.6f, -0.4f), cimaAtras + Offset(e * 0.6f, -0.4f), abajoS, abajoT))
        val losa = lerp(lerp(C.LOSA, C.ESCORIA, 0.38f), C.MUSGO, az.entre(0f, 0.22f))
        val tTejado = ((v.sol.t(V3(e * 0.4f, 0.8f, -0.45f)) - 0.3f) * 1.4f).coerceIn(-1f, 1f)
        drawPath(faldon, p.por(losa, tTejado, lejos))
        clipPath(faldon) {
            val filas = (c.tejado / 1.6f).toInt().coerceIn(3, 7)
            for (k in 1..filas) {
                val f = k / (filas + 1f)
                val a = cima + (abajoT - cima) * f; val b = cimaAtras + (abajoS - cimaAtras) * f
                drawLine(p.sombra(losa, lejos).copy(alpha = 0.55f), a, b, 0.5f)
                drawLine(p.luz(losa, lejos).copy(alpha = 0.25f * v.r.dia), a + Offset(0f, -0.5f), b + Offset(0f, -0.5f), 0.3f)
                // Las juntas de las losas de esa hilada, a tresbolillo.
                var g = (if (k % 2 == 0) 0.08f else 0.16f) + az.entre(0f, 0.05f)
                while (g < 1f) { val q = a + (b - a) * g; drawLine(p.sombra(losa, lejos).copy(alpha = 0.35f), q, q + (abajoT - cima) * (1f / (filas + 1f)), 0.3f); g += az.entre(0.12f, 0.2f) }
            }
        }
        // El caballete: losas cruzadas sobre la cumbrera, y el alero en sombra.
        drawLine(p.sombra(losa, lejos), cima, cimaAtras, 1.4f)
        drawLine(p.luz(lerp(losa, C.NIEVE, 0.3f), lejos).copy(alpha = 0.6f), cima + Offset(0f, -0.6f), cimaAtras + Offset(0f, -0.6f), 0.6f)
        drawLine(p.sombra(lerp(losa, C.ESCORIA, 0.3f), lejos), abajoT, abajoS, 0.9f)
        drawLine(p.sombra(losa, lejos), t0 + Offset(0f, -c.alto) + Offset(-e * 0.8f, 0.8f), cima + Offset(-e * 0.6f, -0.4f), 0.8f)
        if (v.nieveAbajo > 0.15f) {
            drawPath(faldon, p.por(C.NIEVE, 0.45f, lejos).copy(alpha = (0.4f + 0.6f * v.nieveAbajo).coerceAtMost(1f)))
            drawLine(p.sombra(C.NIEVE_SOMBRA, lejos), abajoT, abajoS, 1f + v.nieveAbajo)
        }
        // Con lluvia, el tejado mojado brilla en el alero.
        if ((v.clima == io.github.ruddy2003.vestigium.vidas.Clima.LLUVIA || v.clima == io.github.ruddy2003.vestigium.vidas.Clima.TORMENTA) && v.nieveAbajo < 0.15f) {
            drawLine(lerp(v.l.alto, Color.White, 0.3f).copy(alpha = 0.45f), abajoT + Offset(0f, -1.2f), abajoS + Offset(0f, -1.2f), 0.9f)
            drawLine(lerp(v.l.alto, Color.White, 0.3f).copy(alpha = 0.3f), cima + Offset(0f, 0.6f), cimaAtras + Offset(0f, 0.6f), 0.6f)
        }
        // La chimenea, con su remate de losa.
        val ch = chimenea(c)
        drawRect(p.por(muro, tCostado, lejos), ch + Offset(-1.3f, 0f), Size(2.6f, c.tejado * 0.7f))
        drawRect(p.por(muro, tTestero, lejos), ch + Offset(-2.1f, 0f), Size(0.8f, c.tejado * 0.7f))
        drawRect(p.por(C.LOSA, 0.4f, lejos), ch + Offset(-1.8f, -0.7f), Size(3.6f, 0.9f))
        ventana(v, c, e, s1, t1, lejos, p)
        if (c.fragua) {
            // La herrería: la boca de la fragua abierta en el costado, roja, y su resplandor de noche.
            val boca = t1 + (s1 - t1) * 0.25f + Offset(0f, -c.alto * 0.18f)
            drawRect(Color(0xFFB8442A).copy(alpha = 0.85f), boca + Offset(-1.2f, -1.6f), Size(2.4f, 2.2f))
            if (v.encendido.aldea > 0.1f || v.noche < 0.5f) resplandor(boca, 6f + 8f * v.noche, 0.35f + 0.3f * v.noche, Color(0xFFFF7A38))
        }
    }

    /** El corral de piedra seca, arrimado al costado: un murete bajo con su portillo de tablas, y un montón de heno o de leña. */
    private fun DrawScope.corral(v: VistaAntiguas, c: Casa, e: Float, p: Pincel, lejos: Float, az: Azar) {
        val a = c.pie + Offset(e * c.costado * 0.9f, -c.costado * 0.09f + 3f); val b = a + Offset(e * c.costado * 0.7f, -0.5f)
        val alto = c.alto * 0.3f
        val muro = lerp(C.PIEDRA, C.TIERRA, 0.25f)
        drawPath(poligono(listOf(c.pie + Offset(e * c.costado * 0.5f, 2.6f), a, b, b + Offset(0f, -alto), a + Offset(0f, -alto), c.pie + Offset(e * c.costado * 0.5f, 2.6f - alto))), p.por(muro, 0.1f, lejos))
        var x = 0f
        while (x < 1f) { val q = c.pie + Offset(e * c.costado * 0.5f, 2.6f) + (b - c.pie - Offset(e * c.costado * 0.5f, 2.6f)) * x; drawCircle(p.sombra(muro, lejos).copy(alpha = 0.5f), 0.5f, q + Offset(0f, -alto * az.entre(0.2f, 0.8f))); x += 0.08f }
        drawRect(p.por(C.MADERA, -0.1f, lejos), a + Offset(-e * 1.6f - 1.4f, -alto - 0.6f), Size(2.8f, alto + 0.6f))
        val heno = b + Offset(-e * 2f, -alto - 0.4f)
        drawPath(TrazosValle.blando(heno, 2.6f, 2.2f, az, 8, 0.2f), p.por(if (v.nieveAbajo > 0.5f) C.NIEVE else lerp(C.ORO, C.TIERRA, 0.4f), 0.2f, lejos))
    }

    /**
     * El molino del torrente, junto al puente: una caseta de piedra con su tejado de losa, arrimada a la orilla, y la rueda
     * de palas de madera metida en el agua, con su espuma. En invierno, la rueda parada y con hielo.
     */
    private fun DrawScope.molino(v: VistaAntiguas, az: Azar) {
        val c = Casa(Offset(244f, 214f), 9f, 11f, 7f, 5.6f, espejo = true)
        casa(v, c, az)
        val p = v.pincelEn(c.pie)
        val eje = c.pie + Offset(-c.testero - 2.6f, -4.2f); val r = 4.6f
        val madera = lerp(C.MADERA, C.MADERA_OSC, 0.3f)
        drawCircle(p.sombra(madera, 0.06f), r, eje, style = Stroke(1f))
        for (k in 0 until 8) { val a = k * 0.785f; val q = eje + Offset(kotlin.math.cos(a) * r, kotlin.math.sin(a) * r); drawLine(p.por(madera, 0.2f, 0.06f), eje, q, 0.6f); drawLine(p.por(madera, 0.3f, 0.06f), q, q + Offset(-kotlin.math.sin(a) * 1.2f, kotlin.math.cos(a) * 1.2f), 1f) }
        drawCircle(p.medio(C.ESCORIA, 0.06f), 0.8f, eje)
        if (!v.invierno) for (k in 0..2) drawLine(lerp(v.l.bajo, Color.White, 0.7f).copy(alpha = 0.7f * (1f - 0.5f * v.noche)), eje + Offset(-r + k * 2f, r - 0.6f), eje + Offset(-r + k * 2f - 1.6f, r + 1f), 0.8f, StrokeCap.Round)
        else drawLine(p.luz(C.NIEVE, 0.06f), eje + Offset(-r, -r * 0.2f), eje + Offset(r, -r * 0.4f), 1.2f, StrokeCap.Round)
    }

    /** La ventana del costado: oscura de día; de noche, encendida con su halo; la noche de las linternas, con la linterna y la nieve dorada debajo. */
    private fun DrawScope.ventana(v: VistaAntiguas, c: Casa, e: Float, s1: Offset, t1: Offset, lejos: Float, p: Pincel) {
        val q = t1 + (s1 - t1) * 0.5f + Offset(0f, -c.alto * 0.6f)
        val w = c.costado * 0.13f; val h = c.alto * 0.22f
        val luz = (if (c.gremio) v.encendido.gremio else v.encendido.aldea) * v.noche
        val linterna = Senal.LINTERNAS_EN_LAS_VENTANAS in v.senales
        if (luz > 0.05f || linterna && v.noche > 0.3f) {
            val f = maxOf(luz, if (linterna) v.noche else 0f)
            drawRect(lerp(C.LUMBRE, Color(0xFFFFE6A8), 0.4f).copy(alpha = f.coerceIn(0f, 1f)), q + Offset(-w / 2f, -h / 2f), Size(w, h))
            resplandor(q, 7f + c.alto * 0.5f, 0.45f * f, C.LUMBRE)
            if (linterna) { lucecita(q + Offset(0f, h * 0.7f), 0.8f, f); if (v.nieveAbajo > 0.5f) mancha(q + Offset(0f, c.alto * 0.62f), 10f, 2.4f, C.LUMBRE, 0.35f * f) }
        } else {
            drawRect(p.medio(C.ESCORIA, lejos), q + Offset(-w / 2f, -h / 2f), Size(w, h))
            drawLine(p.luz(C.PIEDRA, lejos).copy(alpha = 0.5f), q + Offset(-w / 2f - 0.4f, h / 2f + 0.3f), q + Offset(w / 2f + 0.4f, h / 2f + 0.3f), 0.5f)
        }
    }

    // ---------------------------------------------------------------- La plaza

    /** La plaza: tierra pisada y alguna losa, el camino que baja al puente y la piedra donde suena el cuerno. */
    private fun DrawScope.plaza(v: VistaAntiguas, az: Azar) {
        val p = v.pincelEn(Offset(220f, 236f)); val forma = TrazosValle.cerrada(PLAZA)
        val tierra = lerp(C.TIERRA, C.LANA, 0.25f)
        if (v.nieveAbajo < 0.99f) {
            drawPath(forma, p.por(tierra, 0.25f, 0.04f).copy(alpha = 1f - 0.7f * v.nieveAbajo))
            clipPath(forma) {
                repeat(40) { val q = Offset(az.entre(172f, 270f), az.entre(212f, 254f)); drawPath(borron(q, az.entre(2f, 4f), az.entre(0.8f, 1.4f), az, 6, 0.25f), p.por(C.PIEDRA, 0.2f, 0.04f).copy(alpha = 0.5f)) }
                drawPoints((0 until 160).map { Offset(az.entre(172f, 270f), az.entre(212f, 254f)) }, PointMode.Points, p.sombra(tierra, 0.04f).copy(alpha = 0.5f), 0.9f, StrokeCap.Round)
            }
        }
        // El camino que baja al puente: más pisado, con sus rodadas.
        if (!v.invierno) drawPath(curva(BAJADA.drop(3)), p.por(lerp(tierra, C.LANA, 0.3f), 0.3f, 0.04f).copy(alpha = 0.7f), style = Stroke(4.4f, cap = StrokeCap.Round))
        else drawPath(curva(BAJADA.drop(2)), p.sombra(C.NIEVE_SOMBRA, 0.04f).copy(alpha = 0.5f), style = Stroke(4f, cap = StrokeCap.Round))
        piedraDelCuerno(v, p)
    }

    /** La piedra de la plaza «donde suena el cuerno»: un pilar de piedra con un peldaño, con una runa y su liquen. */
    private fun DrawScope.piedraDelCuerno(v: VistaAntiguas, p: Pincel) {
        val q = PIEDRA_DEL_CUERNO; val piedra = lerp(C.PIEDRA, C.NIEVE, 0.18f)
        val lado = if (p.solALaDerecha) 1f else -1f
        drawOval(p.r.color.copy(alpha = 0.5f * p.r.fuerza), Offset(q.x - 8f - lado * 8f, q.y - 1.6f), Size(22f, 4f), blendMode = BlendMode.Multiply)
        drawPath(poligono(q + Offset(-7f, 1f), q + Offset(7f, 1f), q + Offset(6f, -2.4f), q + Offset(-6f, -2.4f)), p.por(piedra, 0.1f, 0.04f))
        drawPath(poligono(q + Offset(-6f, -2.4f), q + Offset(6f, -2.4f), q + Offset(5.6f, -3.2f), q + Offset(-5.6f, -3.2f)), p.por(piedra, 0.5f, 0.04f))
        val pilar = poligono(q + Offset(-3.2f, -2.6f), q + Offset(3.4f, -2.6f), q + Offset(3f, -20f), q + Offset(0.6f, -22.4f), q + Offset(-2.8f, -20.6f))
        drawPath(pilar, p.por(piedra, 0.15f, 0.04f))
        drawPath(poligono(q + Offset(-lado * 3.2f, -2.6f), q + Offset(-lado * 0.4f, -2.6f), q + Offset(-lado * 0.3f, -22f), q + Offset(-lado * 2.8f, -20.6f)), p.sombra(piedra, 0.04f).copy(alpha = 0.8f))
        runa(p, Runa.CAMINO, q + Offset(lado * 0.8f, -12f), 6f, piedra, 0.04f)
        for (k in 0..3) drawCircle(p.medio(C.LIQUEN, 0.04f).copy(alpha = 0.7f), 0.6f, q + Offset(-1.5f + k * 1.1f, -17f + k * 3.4f))
        if (v.nieveAbajo > 0.3f) drawPath(poligono(q + Offset(-3f, -20.6f), q + Offset(0.6f, -22.8f), q + Offset(3.2f, -20f), q + Offset(0f, -19.4f)), p.por(C.NIEVE, 0.5f, 0.04f))
    }

    /** Lo que ponen en la plaza los días del cuento. */
    private fun DrawScope.fiestas(v: VistaAntiguas, az: Azar) {
        val p = v.pincelEn(Offset(262f, 236f)); val s = v.senales
        if (Senal.TOLDOS in s) toldos(v, p)
        if (Senal.HERRADURAS in s) herraduras(v, p)
        if (Senal.MESAS_EN_LA_NIEVE in s) mesas(v, p)
        if (Senal.BASTONES in s) for (k in 0 until 6) { val b = PIEDRA_DEL_CUERNO + Offset(-6f + k * 2.4f, 0.4f); drawLine(p.por(C.FRESNO, 0.3f - k * 0.05f, 0.04f), b, b + Offset(3f - k * 0.9f, -19f + (k % 2) * 2f), 0.7f, StrokeCap.Round) }
        if (Senal.PEREGRINOS in s && v.m.hora < 9.0) repeat(8) { val q = Offset(176f + it * 9f + az.entre(-2f, 2f), 238f + (it % 3) * 3f); drawOval(p.por(lerp(C.LANA, C.CUERO, (it % 3) * 0.25f), 0.3f, 0.04f), q + Offset(-3f, -3f), Size(6f, 3.6f)); drawLine(p.por(C.FRESNO, 0.3f, 0.04f), q + Offset(2f, 0f), q + Offset(3f, -14f), 0.6f) }
        if (Senal.CARRO_DE_MUDANZA in s && v.noche < 0.6f) carro(p, Offset(232f, 240f))
    }

    /** La feria: «han llenado la plaza de toldos de colores», con sus palos y sus fardos debajo. */
    private fun DrawScope.toldos(v: VistaAntiguas, p: Pincel) {
        val colores = listOf(C.RUBI, C.ORO, lerp(C.LANA, C.NIEVE, 0.3f), lerp(C.PINO, C.ALERCE, 0.4f))
        for ((k, x) in listOf(186f, 222f, 252f).withIndex()) {
            val y = 246f + (k % 2) * 5f; val w = 28f
            drawOval(p.r.color.copy(alpha = 0.5f * p.r.fuerza), Offset(x - w * 0.6f, y - 2f), Size(w * 1.2f, 4f), blendMode = BlendMode.Multiply)
            for (dx in listOf(-w / 2f, w / 2f)) drawLine(p.por(C.MADERA, 0.1f, 0.04f), Offset(x + dx, y), Offset(x + dx, y - 20f), 0.9f)
            val tela = poligono(Offset(x - w / 2f - 1f, y - 20f), Offset(x + w / 2f + 1f, y - 20f), Offset(x + w / 2f - 1f, y - 25f), Offset(x - w / 2f + 1f, y - 25f))
            drawPath(tela, p.por(colores[k], 0.35f, 0.04f))
            for (j in 0..4) drawPath(Path().apply { val x0 = x - w / 2f - 1f + j * (w + 2f) / 5f; moveTo(x0, y - 20f); cubicTo(x0 + 1f, y - 17f, x0 + 4f, y - 17f, x0 + (w + 2f) / 5f, y - 20f) }, p.por(colores[k], -0.2f, 0.04f), style = Stroke(1f))
            drawRect(p.por(lerp(C.LANA, C.CUERO, 0.3f), 0.2f, 0.04f), Offset(x - 7f, y - 6f), Size(14f, 6f))
            for (j in 0..2) drawCircle(p.por(listOf(C.ORO, C.RUBI, C.ROJIZA)[j], 0.3f, 0.04f), 1.4f, Offset(x - 4f + j * 4f, y - 7f))
        }
    }

    /** La fiesta del Hierro: las herraduras del año colgadas en una cuerda de lado a lado de la plaza, y el barril. */
    private fun DrawScope.herraduras(v: VistaAntiguas, p: Pincel) {
        val a = Offset(176f, 228f); val b = Offset(258f, 216f)
        for (q in listOf(a, b)) drawLine(p.por(C.MADERA, 0.1f, 0.04f), q + Offset(0f, 16f), q + Offset(0f, -2f), 0.8f)
        val c = cuerda(a, b, 8f, 14)
        drawPath(curva(c), p.medio(C.CUERO, 0.04f), style = Stroke(0.5f))
        for (i in 1 until c.size - 1) drawArc(p.por(C.PIEDRA_OSC, 0.4f, 0.04f), 200f, 140f, false, c[i] + Offset(-1.2f, 0.6f), Size(2.4f, 2.6f), style = Stroke(0.6f))
        val q = PIEDRA_DEL_CUERNO + Offset(16f, 2f)
        drawPath(poligono(q + Offset(-4f, 0f), q + Offset(4f, 0f), q + Offset(4.6f, -9f), q + Offset(-4.6f, -9f)), p.por(C.MADERA, 0.2f, 0.04f))
        for (y in listOf(-2f, -7f)) drawLine(p.medio(C.ESCORIA, 0.04f), q + Offset(-4.4f, y), q + Offset(4.4f, y), 0.6f)
    }

    /** Año nuevo: «sacar las mesas a la nieve» para el almuerzo de todos, y las antorchas clavadas (encendidas de noche). */
    private fun DrawScope.mesas(v: VistaAntiguas, p: Pincel) {
        for ((k, x) in listOf(178f, 222f).withIndex()) {
            val y = 244f + k * 4f
            drawRect(p.por(C.MADERA, 0.4f, 0.04f), Offset(x, y - 6f), Size(40f, 2f))
            for (dx in listOf(3f, 37f)) drawLine(p.medio(C.MADERA_OSC, 0.04f), Offset(x + dx, y - 4f), Offset(x + dx, y), 0.8f)
            drawRect(p.luz(C.NIEVE, 0.04f).copy(alpha = 0.7f), Offset(x, y - 6.6f), Size(40f, 0.8f))
        }
        for (q in listOf(Offset(176f, 248f), Offset(254f, 250f), Offset(214f, 240f))) {
            drawLine(p.medio(C.MADERA_OSC, 0.04f), q, q + Offset(0f, -14f), 0.8f)
            if (v.noche > 0.3f) lucecita(q + Offset(0f, -15f), 1.1f, v.noche, Color(0xFFFF9A40))
        }
    }

    /** El día que se van los mineros: un carro cargado con sus cosas, que sale por la plaza hacia el valle. */
    private fun DrawScope.carro(p: Pincel, q: Offset) {
        drawOval(p.r.color.copy(alpha = 0.45f * p.r.fuerza), q + Offset(-12f, -1.4f), Size(26f, 3.6f), blendMode = BlendMode.Multiply)
        drawRect(p.por(C.MADERA, 0.2f, 0.04f), q + Offset(-10f, -8f), Size(20f, 4f))
        for (k in 0..3) drawOval(p.por(listOf(C.LANA, C.CUERO, C.RUBI, C.FRESNO)[k], 0.3f, 0.04f), q + Offset(-9f + k * 4.6f, -13f - (k % 2) * 2f), Size(5f, 5f))
        for (dx in listOf(-6f, 6f)) { drawCircle(p.medio(C.MADERA_OSC, 0.04f), 3.4f, q + Offset(dx, -2.6f), style = Stroke(0.8f)); drawCircle(p.medio(C.ESCORIA, 0.04f), 0.7f, q + Offset(dx, -2.6f)) }
    }

    /** Lo que se mueve de la aldea: el humo de las chimeneas y el de la herrería («el único ruido del día» en invierno). */
    fun DrawScope.humos(v: VistaAntiguas, reloj: Float) {
        val l = v.l
        for ((i, q) in CHIMENEAS.withIndex()) humo(l, q, 30f + (q.y - 200f) * 0.4f, 2.6f + (q.y - 200f) * 0.06f, reloj, v.m.viento, 0.22f, 0.1f, 60 + i, 5, 0.45f)
    }
}
