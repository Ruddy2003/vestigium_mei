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
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Senal
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.canto
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.cara
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.p
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.tapa
import io.github.ruddy2003.vestigium.ui.escenas.PosadaAntiguas.luzDeCara
import io.github.ruddy2003.vestigium.ui.escenas.PosadaAntiguas.pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar

/**
 * La puerta de la posada, lo que el cuadro tiene que dejar leer: el hueco con la sala adivinada dentro (la lumbre, las
 * jarras colgadas de la viga, las botas en fila), la hoja de tablas, el dintel de una piedra larga, **el cuerno de
 * carnero** clavado encima en su tabla, **la piedra de los años** en la jamba de la derecha («todos los años grabados en
 * esa piedra, uno debajo de otro, hasta los que ya no se leen»), la campana en su brazo, **el poyo** de piedra junto a la
 * puerta y la argolla de las mulas.
 */
internal object PuertaAntiguas {
    private val C = ColorAntiguas

    /** El hueco de la puerta, a lo largo del muro, y su alto. */
    const val PUERTA_S0 = 0.06f
    const val PUERTA_S1 = 0.96f
    const val PUERTA_ALTO = 1.82f
    private const val GRUESO = 0.36f
    private const val JAMBA_S0 = -0.2f

    /** El poyo de piedra, a la derecha de la puerta, bajo el cuerpo alto. */
    const val POYO_S0 = -0.34f
    const val POYO_S1 = -1.6f
    const val POYO_ALTO = 0.45f
    const val POYO_FONDO = 0.46f

    /** Dónde se pone el objeto chico del día: sobre la losa del poyo, hacia el canto (a la vista). */
    val SOBRE_EL_POYO: Offset get() = p(-0.62f, POYO_ALTO, POYO_FONDO * 0.55f)

    /** La argolla donde se atan las mulas, en el muro del zaguán, a la izquierda de la puerta. */
    val ARGOLLA: Offset get() = p(1.32f, 1.12f, 0.02f)

    /** La otra argolla, en la piedra de la esquina del zaguán (la de los días en que el sitio del banco está ocupado). */
    val ARGOLLA_ESQUINA: Offset get() = p(2.17f, 1.05f, 0.02f)

    /** El umbral, en el lienzo: de dónde sale la luz de la sala. */
    val UMBRAL: Offset get() = p((PUERTA_S0 + PUERTA_S1) / 2f, 0f)

    fun DrawScope.puerta(v: VistaAntiguas, az: Azar) {
        val pu = EscenaAntiguas.puerta(v.m)
        val luz = if (pu == Puerta.CERRADA) 0f else v.encendido.sala * maxOf(v.noche, 0.3f)
        hueco(v, pu, luz, az)
        jambaIzquierda(v)
        piedraDeLosAnios(v, az)
        dintel(v, az)
        cuerno(v)
        campana(v)
        argolla(v)
        poyo(v)
    }

    // ---------------------------------------------------------------- El hueco

    /**
     * El hueco: el grueso del muro a la izquierda (lo que se ve de la jamba de dentro) y, por la puerta abierta, la sala:
     * el suelo de losas, la pared del fondo con la lumbre y su resplandor, la viga con las jarras colgadas y las botas en
     * fila junto al fuego. Entornada, la hoja tapa casi todo y deja una raja de luz; cerrada, la hoja de tablas con sus
     * herrajes, y de noche la luz por las rendijas.
     */
    private fun DrawScope.hueco(v: VistaAntiguas, pu: Puerta, luz: Float, az: Azar) {
        val hueco = cara(PUERTA_S0, PUERTA_S1, 0f, PUERTA_ALTO)
        val pe = pincel(v, 0.5f, 1f)
        val arriba = p(0.5f, PUERTA_ALTO).y; val abajo = p(0.5f, 0f).y
        clipPath(hueco) {
            if (pu != Puerta.CERRADA) sala(v, luz, arriba, abajo, az)
            // El grueso del muro de la izquierda (mira a la derecha): piedra, encendida por dentro de noche.
            val derrame = canto(PUERTA_S1, 0f, -GRUESO, 0f, PUERTA_ALTO)
            val tj = v.sol.t(FachadaPosada.CANTO) - 0.35f; val piedraJ = lerp(C.PIEDRA, C.TIERRA, 0.25f)
            drawPath(derrame, Brush.horizontalGradient(listOf(pe.por(piedraJ, tj + 0.15f), pe.por(lerp(piedraJ, C.ESCORIA, 0.35f), tj - 0.4f)), p(PUERTA_S1, 1f).x, p(PUERTA_S1, 1f, -GRUESO).x))
            if (luz > 0.05f) drawPath(derrame, Brush.horizontalGradient(listOf(C.LUMBRE.copy(alpha = 0f), C.LUMBRE.copy(alpha = 0.85f * luz)), p(PUERTA_S1, 1f).x, p(PUERTA_S1, 1f, -GRUESO).x), blendMode = BlendMode.Plus)
            for (h in listOf(0.45f, 0.95f, 1.4f)) drawLine(pe.sombra(C.PIEDRA).copy(alpha = 0.6f), p(PUERTA_S1, h), p(PUERTA_S1, h, -GRUESO), 0.6f)
            when (pu) {
                Puerta.ABIERTA -> drawLine(pe.por(C.MADERA_OSC, -0.3f), p(PUERTA_S0 + 0.03f, 0f, -GRUESO), p(PUERTA_S0 + 0.03f, PUERTA_ALTO, -GRUESO), 2.2f)
                Puerta.ENTORNADA -> hoja(v, pe, PUERTA_S0, PUERTA_S0 + 0.5f, -GRUESO + 0.04f, luz)
                Puerta.CERRADA -> hoja(v, pe, PUERTA_S0, PUERTA_S1, -GRUESO + 0.04f, v.encendido.sala * v.noche)
            }
            // La sombra del dintel, arriba.
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f * (1f - 0.6f * luz)), Color.Transparent), arriba, arriba + (abajo - arriba) * 0.2f), Offset(280f, arriba), Size(80f, (abajo - arriba) * 0.2f))
        }
        // El umbral, de una piedra gastada en medio.
        drawPath(tapa(PUERTA_S0 - 0.04f, PUERTA_S1 + 0.04f, -0.05f, 0.3f, 0.07f), pe.por(lerp(C.PIEDRA, C.LANA, 0.25f), 0.45f))
        drawPath(cara(PUERTA_S0 - 0.04f, PUERTA_S1 + 0.04f, 0f, 0.07f, 0.3f), pe.por(C.PIEDRA, luzDeCara(v) - 0.1f))
        if (luz > 0.05f) drawLine(lerp(C.LUMBRE, Color(0xFFFFE6B0), 0.4f).copy(alpha = 0.7f * luz), p(PUERTA_S0 + 0.05f, 0.072f, 0.02f), p(PUERTA_S1 - 0.1f, 0.072f, 0.02f), 1f)
    }

    /** La sala vista por la puerta, de [arriba] a [abajo] en el lienzo. */
    private fun DrawScope.sala(v: VistaAntiguas, luz: Float, arriba: Float, abajo: Float, az: Azar) {
        val fondo = lerp(lerp(C.MADERA_OSC, C.ESCORIA, 0.45f), C.LUMBRE, 0.14f + 0.5f * luz)
        val alto = abajo - arriba
        // Lo que se ve de la sala va del canto de dentro de la jamba de la izquierda a la jamba de la derecha.
        val xa = p(PUERTA_S1, 1f, -GRUESO).x; val xb = p(PUERTA_S0, 1f).x; val w = xb - xa
        drawRect(Brush.verticalGradient(0f to lerp(fondo, Color.Black, 0.35f), 0.55f to fondo, 1f to lerp(fondo, C.MADERA, 0.25f), startY = arriba, endY = abajo), Offset(290f, arriba), Size(60f, alto))
        // La lumbre del fondo: un resplandor bajo, a la derecha, y las brasas.
        val fuego = Offset(xa + w * 0.66f, arriba + alto * 0.72f)
        mancha(fuego, 16f, alto * 0.22f, C.LUMBRE, (0.35f + 0.55f * luz).coerceAtMost(1f))
        drawCircle(lerp(C.LUMBRE, Color(0xFFFFE6A0), 0.5f).copy(alpha = (0.5f + 0.5f * luz).coerceAtMost(1f)), 2.2f, fuego + Offset(0f, 4f))
        drawRect(lerp(C.ESCORIA, C.LUMBRE, 0.2f), fuego + Offset(-7f, 6f), Size(14f, 2f))
        // El suelo de la sala: losas que la luz de la lumbre toca.
        val suelo = arriba + alto * 0.82f
        drawRect(Brush.verticalGradient(listOf(lerp(fondo, C.PIEDRA, 0.2f), lerp(lerp(C.PIEDRA, C.LUMBRE, 0.25f + 0.35f * luz), C.ESCORIA, 0.35f)), suelo, abajo), Offset(290f, suelo), Size(60f, abajo - suelo))
        for (k in 0..2) drawLine(Color.Black.copy(alpha = 0.25f), Offset(290f, suelo + (abajo - suelo) * (0.3f + 0.25f * k)), Offset(350f, suelo + (abajo - suelo) * (0.25f + 0.25f * k)), 0.6f)
        // Las botas en fila junto al fuego, a secar.
        for (k in 0..3) {
            val q = Offset(xa + w * 0.08f + k * 3.2f, suelo + 1.4f)
            drawRect(lerp(C.CUERO, C.ESCORIA, 0.45f), q + Offset(0f, -4.6f), Size(1.8f, 4.6f))
            drawRect(lerp(C.CUERO, C.ESCORIA, 0.45f), q + Offset(0f, -1f), Size(2.8f, 1.2f))
            drawLine(lerp(C.CUERO, C.LUMBRE, 0.5f).copy(alpha = 0.5f + 0.5f * luz), q + Offset(1.6f, -4.4f), q + Offset(1.6f, -0.4f), 0.4f)
        }
        // La viga con las jarras colgadas, «de barro cocido con una raya negra», cada una con su marca.
        val viga = arriba + alto * 0.14f
        drawRect(lerp(C.MADERA_OSC, C.ESCORIA, 0.3f), Offset(290f, viga - 1.6f), Size(60f, 2.4f))
        for (k in 0..4) {
            val q = Offset(xa + w * (0.12f + 0.2f * k), viga + 1f + (k % 2) * 0.6f)
            val barro = lerp(C.ROJIZA, C.LANA, 0.25f)
            drawLine(C.ESCORIA, q, q + Offset(0f, 1.4f), 0.4f)
            drawPath(poligono(q + Offset(-1.3f, 1.4f), q + Offset(1.3f, 1.4f), q + Offset(1.6f, 4.6f), q + Offset(-1.6f, 4.6f)), lerp(lerp(barro, C.ESCORIA, 0.35f), C.LUMBRE, 0.25f * luz))
            drawLine(C.ESCORIA.copy(alpha = 0.8f), q + Offset(-1.5f, 3f), q + Offset(1.5f, 3f), 0.5f)
        }
        // Las mantas tendidas en las vigas, al fondo, y la mesa pegada al fuego.
        drawPath(poligono(Offset(xa + w * 0.42f, viga + 1.2f), Offset(xa + w * 0.86f, viga + 1.2f), Offset(xa + w * 0.84f, viga + 11f), Offset(xa + w * 0.46f, viga + 9.6f)), lerp(lerp(C.RUBI, C.CUERO, 0.4f), Color.Black, 0.4f - 0.2f * luz))
        drawRect(lerp(C.MADERA, C.ESCORIA, 0.4f), Offset(xa + w * 0.05f, suelo - 9f), Size(w * 0.45f, 1.6f))
        // De noche la sala entera se enciende: la luz de la lumbre llena el hueco.
        if (luz > 0.05f) drawRect(Brush.radialGradient(listOf(Color(0xFFFFE0A8).copy(alpha = 0.55f * luz), C.LUMBRE.copy(alpha = 0.3f * luz), Color.Transparent), fuego, alto * 0.7f), Offset(290f, arriba), Size(60f, alto), blendMode = BlendMode.Plus)
        drawLine(lerp(C.MADERA, C.ESCORIA, 0.5f), Offset(xa + w * 0.12f, suelo - 8f), Offset(xa + w * 0.12f, suelo - 2f), 0.8f)
    }

    /** La hoja de la puerta, de tablas con dos barras de hierro y su anilla, entre [s0] y [s1], metida [fuera] metros; con luz por las rendijas. */
    private fun DrawScope.hoja(v: VistaAntiguas, pe: Pincel, s0: Float, s1: Float, fuera: Float, luz: Float) {
        val madera = lerp(C.MADERA, C.LANA, 0.06f)
        val forma = cara(s0, s1, 0.02f, PUERTA_ALTO, fuera)
        drawPath(forma, pe.por(madera, luzDeCara(v) - 0.25f))
        var s = s0 + 0.15f
        while (s < s1) { drawLine(pe.sombra(lerp(madera, C.ESCORIA, 0.5f)), p(s, 0.02f, fuera), p(s, PUERTA_ALTO, fuera), 0.8f); s += 0.15f }
        for (h in listOf(0.35f, 1.45f)) drawLine(pe.medio(C.ESCORIA), p(s0, h, fuera + 0.01f), p(s1, h, fuera + 0.01f), 1.4f)
        drawCircle(pe.medio(C.ESCORIA), 1.4f, p(s1 - 0.14f, 0.95f, fuera + 0.02f), style = Stroke(0.6f))
        if (luz > 0.05f) {
            val rendija = lerp(C.LUMBRE, Color(0xFFFFE2A0), 0.4f).copy(alpha = (0.85f * luz).coerceIn(0f, 1f))
            drawLine(rendija, p(s1, 0.02f, fuera), p(s1, PUERTA_ALTO, fuera), 1.2f)
            drawLine(rendija, p(s0, 0.03f, fuera), p(s1, 0.03f, fuera), 1f)
        }
    }

    // ---------------------------------------------------------------- El marco

    /** La jamba de la izquierda: tres piedras grandes una sobre otra. */
    private fun DrawScope.jambaIzquierda(v: VistaAntiguas) {
        val t = luzDeCara(v); val piedra = lerp(C.PIEDRA, C.NIEVE, 0.12f)
        for ((h0, h1) in listOf(0f to 0.62f, 0.64f to 1.22f, 1.24f to PUERTA_ALTO)) {
            val pe = pincel(v, PUERTA_S1 + 0.12f, h0)
            drawPath(cara(PUERTA_S1, PUERTA_S1 + 0.24f + (h0 * 0.07f), h0, h1 - 0.02f, 0.015f), pe.por(piedra, t + 0.08f))
            drawLine(pe.luz(C.NIEVE).copy(alpha = 0.35f), p(PUERTA_S1, h1 - 0.03f, 0.015f), p(PUERTA_S1 + 0.24f, h1 - 0.03f, 0.015f), 0.6f)
        }
    }

    /**
     * La piedra de los años, la jamba de la derecha: una losa alta, más clara que el muro, con una marca por año, en dos
     * columnas al tresbolillo: las de arriba, nuevas; las de abajo, gastadas por las botas y la lluvia hasta no leerse. Cada
     * marca es de la familia de las del paso (un palo con una raya a cada lado, a alturas distintas, que suben o bajan),
     * distinta de las demás, unas talladas hondas (el surco oscuro y su labio de luz) y otras someras. La del año nuevo,
     * recién tallada, clara. Marcas, no números.
     */
    private fun DrawScope.piedraDeLosAnios(v: VistaAntiguas, az: Azar) {
        val pe = pincel(v, -0.07f, 1f)
        val piedra = lerp(lerp(C.PIEDRA, C.NIEVE, 0.24f), C.LANA, 0.1f)
        val forma = cara(PUERTA_S0, JAMBA_S0, 0f, PUERTA_ALTO + 0.02f, 0.025f)
        drawPath(forma, pe.por(piedra, luzDeCara(v) + 0.12f))
        // Su canto de la derecha (el que mira a quien mira, si sobresale) y el labio de luz.
        drawPath(canto(JAMBA_S0, 0f, 0.025f, 0f, PUERTA_ALTO + 0.02f), pe.por(piedra, v.sol.t(FachadaPosada.CANTO)))
        val nueva = Senal.RUNA_NUEVA in v.senales
        val filas = 10
        val oscuro = pe.sombra(lerp(piedra, C.ESCORIA, 0.5f)); val labio = pe.luz(lerp(piedra, C.NIEVE, 0.4f)); val fresco = pe.luz(lerp(piedra, C.FRESNO, 0.5f))
        clipPath(forma) {
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, pe.sombra(lerp(piedra, C.TIERRA, 0.4f)).copy(alpha = 0.45f)), p(0f, 0.6f).y, p(0f, 0f).y), Offset(320f, p(0f, 0.6f).y), Size(40f, p(0f, 0f).y - p(0f, 0.6f).y))
            for (k in 0 until filas * 2) {
                val fila = k / 2; val col = k % 2
                val h = PUERTA_ALTO - 0.13f - 1.58f * (fila + 0.5f * col) / filas
                val c = p(if (col == 0) -0.045f else -0.135f, h, 0.03f)
                val gastada = (k / (filas * 2 - 1f)).let { it * it } * az.entre(0.7f, 1.1f)
                val honda = az.real01() < 0.35f && gastada < 0.6f
                val fresca = if (k == 0 && nueva) 1f else 0f
                val a = (0.95f * (1f - gastada)).coerceIn(0.08f, 1f)
                marcaDeAnio(lerp(oscuro, fresco, fresca).copy(alpha = a), if (honda && fresca == 0f) labio.copy(alpha = 0.6f * a) else null, c, az.entre(2f, 2.6f), if (honda) 0.85f else 0.55f, az)
            }
        }
        if (nueva) drawCircle(lerp(C.FRESNO, C.NIEVE, 0.5f).copy(alpha = 0.45f), 2.6f, p(-0.045f, PUERTA_ALTO - 0.13f, 0.03f))
    }

    /**
     * Una marca de año en [c], de [m] de medio alto: el palo y una raya a cada lado, a alturas distintas, las dos hacia
     * arriba o las dos hacia abajo, de largos distintos; con [labio], el filo de luz del surco hondo.
     */
    private fun DrawScope.marcaDeAnio(surco: Color, labio: Color?, c: Offset, m: Float, grueso: Float, az: Azar) {
        val sube = if (az.real01() < 0.5f) -1f else 1f; val lado = if (az.real01() < 0.5f) -1f else 1f
        val ya = az.entre(-0.7f, -0.2f) * m; val yb = az.entre(0.05f, 0.55f) * m
        val la = az.entre(0.45f, 0.8f) * m; val lb = az.entre(0.4f, 0.75f) * m
        val trazos = listOf(
            c + Offset(az.entre(-0.2f, 0.2f), -m) to c + Offset(0f, m),
            c + Offset(0f, ya) to c + Offset(lado * la * 0.75f, ya + sube * la * 0.65f),
            c + Offset(0f, yb) to c + Offset(-lado * lb * 0.75f, yb + sube * lb * 0.65f),
        )
        if (labio != null) for ((a, b) in trazos) drawLine(labio, a + Offset(0.45f, 0.45f), b + Offset(0.45f, 0.45f), grueso * 0.7f, StrokeCap.Round)
        for ((a, b) in trazos) drawLine(surco, a, b, grueso, StrokeCap.Round)
    }

    /** El dintel: una piedra larga y tosca, con su canto de abajo en sombra; con las ramas de pino de la Noche del Arpa. */
    private fun DrawScope.dintel(v: VistaAntiguas, az: Azar) {
        val pe = pincel(v, 0.5f, 1.95f)
        val piedra = lerp(C.PIEDRA, C.NIEVE, 0.16f)
        drawPath(cara(JAMBA_S0 - 0.04f, PUERTA_S1 + 0.28f, PUERTA_ALTO, PUERTA_ALTO + 0.24f, 0.03f), pe.por(piedra, luzDeCara(v) + 0.14f))
        drawLine(pe.luz(C.NIEVE).copy(alpha = 0.4f), p(JAMBA_S0 - 0.04f, PUERTA_ALTO + 0.23f, 0.03f), p(PUERTA_S1 + 0.28f, PUERTA_ALTO + 0.23f, 0.03f), 0.7f)
        drawPath(tapa(PUERTA_S0, PUERTA_S1, -GRUESO, 0.03f, PUERTA_ALTO), pe.sombra(lerp(C.PIEDRA, C.ESCORIA, 0.3f)))
        if (Senal.RAMAS_DE_PINO in v.senales) for (k in 0..8) {
            val q = p(PUERTA_S0 - 0.05f + k * 0.13f, PUERTA_ALTO + 0.16f, 0.07f)
            val punta = q + Offset(az.entre(-6f, 6f), az.entre(5f, 11f))
            drawLine(pe.por(C.PINO, 0.1f), q, punta, 2f, StrokeCap.Round)
            for (j in 1..3) { val r = q + (punta - q) * (j / 4f); drawLine(pe.por(lerp(C.PINO, C.ALERCE, 0.3f), 0.25f), r, r + Offset(az.entre(-3f, 3f), 2f), 0.8f, StrokeCap.Round) }
        }
    }

    /**
     * El cuerno de carnero clavado sobre la puerta, en su tabla: grueso en la base, con sus anillos, que da la vuelta y
     * media en espiral; de hueso viejo, más oscuro en la punta. Los días de luto, con un lazo negro **[Diseño propio]**.
     */
    private fun DrawScope.cuerno(v: VistaAntiguas) {
        val pe = pincel(v, 0.5f, 2.3f)
        val tabla = cara(0.24f, 0.78f, PUERTA_ALTO + 0.27f, PUERTA_ALTO + 0.72f, 0.04f)
        drawPath(tabla, pe.por(lerp(C.MADERA, C.PIEDRA, 0.2f), luzDeCara(v) - 0.05f))
        drawPath(tabla, pe.sombra(C.MADERA_OSC).copy(alpha = 0.8f), style = Stroke(0.7f))
        val c = p(0.52f, PUERTA_ALTO + 0.5f, 0.1f); val s = FachadaPosada.escala(0.5f) * 0.0105f
        // El cuerno: un tubo que se adelgaza a lo largo de una espiral de vuelta y media, de la base (arriba, contra la
        // tabla) hacia fuera y abajo, y la punta que vuelve hacia delante; cada anillo con su arruga.
        val hueso = lerp(C.LANA, C.ORO, 0.22f)
        val n = 26
        val eje = (0..n).map { i -> val f = i / n.toFloat(); val ang = 1.2f + 5.4f * f; val r = (15f - 9.5f * f) * s; c + Offset(kotlin.math.cos(ang) * r * 1.15f, -kotlin.math.sin(ang) * r) }
        val anchos = (0..n).map { i -> (9.5f - 7.6f * i / n.toFloat()) * s }
        val luzDe = Offset(if (pe.solALaDerecha) 0.6f else -0.6f, -0.8f)
        for (i in n - 1 downTo 0) {
            val d = eje[i + 1] - eje[i]; val largo = d.getDistance().coerceAtLeast(0.01f); val nr = Offset(-d.y, d.x) / largo
            val tramo = poligono(eje[i] + nr * anchos[i] * 0.5f, eje[i + 1] + nr * anchos[i + 1] * 0.5f, eje[i + 1] - nr * anchos[i + 1] * 0.5f, eje[i] - nr * anchos[i] * 0.5f)
            val t = (nr.x * luzDe.x + nr.y * luzDe.y) * 0.6f + 0.15f - 0.3f * i / n
            drawPath(tramo, Brush.linearGradient(listOf(pe.por(hueso, t + 0.35f), pe.por(lerp(hueso, C.CUERO, 0.35f), t - 0.45f)), eje[i] + nr * anchos[i] * 0.5f, eje[i] - nr * anchos[i] * 0.5f))
            if (i % 2 == 0) drawLine(pe.sombra(lerp(hueso, C.CUERO, 0.6f)).copy(alpha = 0.55f), eje[i] + nr * anchos[i] * 0.48f, eje[i] - nr * anchos[i] * 0.48f, 0.45f)
        }
        // La punta, más oscura, y el clavo de la base.
        drawCircle(pe.sombra(lerp(hueso, C.CUERO, 0.7f)), anchos.last() * 0.6f, eje.last())
        drawCircle(pe.medio(C.ESCORIA), 1.1f, eje.first() + Offset(1.5f * s, -1f * s))
        if (Senal.LAZO_NEGRO in v.senales) {
            val q = eje[n / 3]
            drawLine(pe.medio(C.ESCORIA), q, q + Offset(-3f, 12f), 1.6f, StrokeCap.Round); drawLine(pe.medio(C.ESCORIA), q, q + Offset(2f, 11f), 1.4f, StrokeCap.Round)
            drawCircle(pe.medio(C.ESCORIA), 2f, q)
        }
    }

    /** La campana de la posada, en su brazo de hierro, en la esquina del cuerpo alto: la que avisa de los cuernos del paso. */
    private fun DrawScope.campana(v: VistaAntiguas) {
        val pe = pincel(v, -0.3f, 2.3f)
        val a = p(-0.3f, 2.32f); val b = p(-0.3f, 2.32f, 0.55f)
        drawLine(pe.medio(C.ESCORIA), a, b, 1.2f, StrokeCap.Round)
        drawLine(pe.medio(C.ESCORIA), p(-0.3f, 2.02f), (a + b) / 2f, 0.8f)
        val k = FachadaPosada.escala(-0.3f, 0.55f) / 50f; val c = b + Offset(0f, 2.4f * k)
        val bronce = lerp(C.ORO, C.CUERO, 0.3f)
        drawLine(pe.medio(C.ESCORIA), b, c, 0.6f)
        drawPath(poligono(c + Offset(-1.8f * k, 0f), c + Offset(1.8f * k, 0f), c + Offset(3f * k, 5f * k), c + Offset(-3f * k, 5f * k)), Brush.horizontalGradient(listOf(pe.luz(bronce), pe.sombra(bronce)), c.x - 3f * k, c.x + 3f * k))
    }

    /** La argolla de hierro, empotrada en una piedra, con el ronzal de las mulas cuando están. */
    private fun DrawScope.argolla(v: VistaAntiguas) {
        for ((s, h, en) in listOf(Triple(1.32f, 1.12f, ARGOLLA), Triple(2.17f, 1.05f, ARGOLLA_ESQUINA))) {
            val pe = pincel(v, s, h)
            drawCircle(pe.medio(C.ESCORIA), 2.4f, en + Offset(-0.4f, 2.2f), style = Stroke(0.9f))
            drawCircle(pe.luz(C.PIEDRA_OSC).copy(alpha = 0.6f), 0.9f, en)
        }
    }

    /** El poyo de piedra: la losa de encima, gastada y más clara, sobre dos piedras; su frente en sombra. Encima, el objeto chico del día. */
    private fun DrawScope.poyo(v: VistaAntiguas) {
        val pe = pincel(v, -0.9f, 0.3f)
        val piedra = lerp(C.PIEDRA, C.LANA, 0.14f)
        val t = luzDeCara(v)
        // Las dos piedras de debajo y el hueco en sombra entre ellas.
        drawPath(cara(POYO_S0, POYO_S1, 0f, POYO_ALTO - 0.1f, POYO_FONDO - 0.08f), pe.sombra(lerp(piedra, C.ESCORIA, 0.4f)))
        for ((s0, s1) in listOf(POYO_S0 - 0.02f to POYO_S0 - 0.32f, -1.1f to POYO_S1)) {
            drawPath(cara(s0, s1, 0f, POYO_ALTO - 0.1f, POYO_FONDO - 0.06f), pe.por(piedra, t - 0.1f))
        }
        drawPath(canto(POYO_S0 - 0.02f, 0f, POYO_FONDO - 0.06f, 0f, POYO_ALTO - 0.1f), pe.por(piedra, v.sol.t(FachadaPosada.CANTO) - 0.05f))
        // La losa: su canto y su tapa.
        drawPath(cara(POYO_S0, POYO_S1, POYO_ALTO - 0.1f, POYO_ALTO, POYO_FONDO), pe.por(piedra, t + 0.05f))
        drawPath(canto(POYO_S0, 0f, POYO_FONDO, POYO_ALTO - 0.1f, POYO_ALTO), pe.por(piedra, v.sol.t(FachadaPosada.CANTO) + 0.1f))
        drawPath(tapa(POYO_S0, POYO_S1, 0f, POYO_FONDO, POYO_ALTO), pe.por(lerp(piedra, C.NIEVE, 0.22f), 0.5f))
        drawLine(pe.luz(C.NIEVE).copy(alpha = 0.5f), p(POYO_S0, POYO_ALTO, POYO_FONDO), p(POYO_S1, POYO_ALTO, POYO_FONDO), 0.8f)
        if (v.nieveAbajo > 0.3f) {
            val n = v.nieveAbajo
            drawPath(poligono(listOf(p(POYO_S0 - 0.05f, POYO_ALTO, 0.05f), p(POYO_S1, POYO_ALTO, 0.05f), p(POYO_S1, POYO_ALTO, POYO_FONDO - 0.02f), p(POYO_S0 - 0.05f, POYO_ALTO, POYO_FONDO - 0.02f), p(POYO_S0 - 0.05f, POYO_ALTO + 0.1f * n, POYO_FONDO * 0.5f), p(POYO_S1, POYO_ALTO + 0.12f * n, POYO_FONDO * 0.5f))), pe.por(C.NIEVE, 0.5f).copy(alpha = n))
            drawLine(pe.sombra(C.NIEVE_SOMBRA).copy(alpha = 0.6f * n), p(POYO_S0 - 0.05f, POYO_ALTO, POYO_FONDO - 0.02f), p(POYO_S1, POYO_ALTO, POYO_FONDO - 0.02f), 1f)
        }
        // De noche, la lumbre de la puerta le enciende el canto del lado de la puerta.
        if (v.encendido.sala * v.noche > 0.05f && EscenaAntiguas.puerta(v.m) != Puerta.CERRADA) resplandor(p(POYO_S0 - 0.1f, POYO_ALTO, POYO_FONDO), 12f, 0.35f * v.encendido.sala * v.noche, C.LUMBRE)
    }
}
