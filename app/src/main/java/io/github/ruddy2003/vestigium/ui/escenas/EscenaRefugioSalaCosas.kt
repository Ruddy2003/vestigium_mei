package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Obra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Las cosas de la sala de bombas que se pintan por su hondo (las máquinas de [PiezasMaquinas], la bancada de
 * [PiezasBancada], los cubos, los bidones, la manguera, la caja de herramientas), lo que queda en el suelo (charcos, huellas,
 * el reguero del desagüe, las flores del sellado, los papelitos del primer día), lo del pasillo según el día y la lámpara
 * de jaula. Todo en metros, en la sala de [SalaRefugio].
 */
internal object PiezasSala {
    private val C = ColorRefugio
    private val K = CamRefugio
    private val R = EscenaRefugio
    private val S = SalaRefugio

    // ---------------------------------------------------------------- Sitios que el gato necesita saber

    /** La pata de delante de la bancada, en el suelo del gato. */
    val PATA_BANCADA_X: Float get() = K.p(V3(S.BANCADA_X0 + 0.06f, 0f, S.BANCADA_Z0 + 0.06f)).x
    /** Dónde deja ella las piezas de la bomba tres el día que la desmonta: en el suelo despejado, junto a la bancada. */
    val PIEZAS = V3(0.15f, 0f, 3.75f)
    /** El cubo de la gotera del tubo verde. */
    val CUBO = V3(-2.3f, 0f, 3.65f)
    val CUBO_X: Float get() = K.p(CUBO).x.coerceAtLeast(40f)
    /** El taburete, a la izquierda de la bancada. */
    val TABURETE_X: Float get() = K.p(PiezasBancada.TABURETE).x
    /** Encima de la voluta de la bomba grande, a la izquierda de la rueda roja: el sitio alto del gato. */
    val MIRADOR: Offset get() = K.p(V3(-3.45f, 1.34f, 3.2f))
    /** Debajo de la bancada. */
    val ABRIGO_X: Float get() = K.p(V3(-1.0f, 0f, S.BANCADA_Z1)).x
    /** La guirnalda o el árbol: el adorno que el gato mira. */
    val ADORNO_X: Float get() = 300f

    // ---------------------------------------------------------------- Las cosas del suelo, por su hondo

    /** Las cosas de la sala de este momento, cada una con su hondo, su esqueleto (para la sombra) y su pintura. */
    fun cosas(v: VistaRefugio): List<CosaRefugio> = buildList {
        val m = v.m
        val M = PiezasMaquinas; val B = PiezasBancada
        add(cosa(S.BOMBA, listOf(V3(-3.95f, 0f, 1.85f), V3(-2.45f, 0f, 1.85f), V3(-2.45f, 0f, 3.65f), V3(-3.95f, 0f, 3.65f), V3(-3.7f, 1.15f, 2.0f), V3(-2.85f, 1.15f, 2.0f), V3(-2.85f, 1.3f, 3.4f), V3(-3.7f, 1.3f, 3.4f))) { M.run { bombaGrande(v) } })
        add(cosa(S.BOMBA_2, listOf(V3(-4.1f, 0f, 4.3f), V3(-3.3f, 0f, 4.3f), V3(-3.3f, 0f, 5.1f), V3(-4.1f, 0f, 5.1f), V3(-3.95f, 1.4f, 4.5f), V3(-3.45f, 1.4f, 4.9f))) { M.run { bombaVertical(v, S.BOMBA_2) } })
        if (R.bombaTres(m)) {
            add(cosa(S.BOMBA_3, listOf(V3(-4.7f, 0f, 6.0f), V3(-3.6f, 0f, 6.0f), V3(-3.6f, 0f, 6.8f), V3(-4.7f, 0f, 6.8f), V3(-4.3f, 0.7f, 6.4f))) { M.run { bombaDesmontada(v) } })
            add(cosa(PIEZAS, listOf(PIEZAS + V3(-0.5f, 0f, -0.35f), PIEZAS + V3(0.5f, 0f, -0.35f), PIEZAS + V3(0.5f, 0f, 0.35f), PIEZAS + V3(-0.5f, 0f, 0.35f), PIEZAS + V3(0f, 0.15f, 0f))) { M.run { piezasDeLaBomba(v, PIEZAS) } })
        } else add(cosa(S.BOMBA_3, listOf(V3(-4.8f, 0f, 6.0f), V3(-3.7f, 0f, 6.0f), V3(-3.7f, 0f, 6.75f), V3(-4.8f, 0f, 6.75f), V3(-4.6f, 1.05f, 6.2f), V3(-3.9f, 1.05f, 6.6f))) { M.run { bombaVieja(v, S.BOMBA_3) } })
        add(cosa(S.FILTRO, K.circulo(V3(S.FILTRO.x, 0f, S.FILTRO.z), K.ARRIBA, S.FILTRO_R + 0.1f, 8) + K.circulo(V3(S.FILTRO.x, S.FILTRO_ALTO, S.FILTRO.z), K.ARRIBA, S.FILTRO_R, 8)) { M.run { filtro(v) } })
        add(cosa(CUBO, K.circulo(CUBO, K.ARRIBA, 0.17f, 6) + K.circulo(CUBO + V3(0f, 0.34f, 0f), K.ARRIBA, 0.19f, 6)) { cubo(v, CUBO, C.ACERO, if (R.bombaTres(m)) lerp(C.OXIDO, C.ROCA, 0.4f) else lerp(C.CHAPA, C.PISCINA, 0.3f)) })
        add(cosa(V3(0.5f, 0f, 3.8f), K.circulo(V3(0.5f, 0.05f, 3.8f), K.ARRIBA, 0.32f, 8)) { manguera(v, V3(0.5f, 0f, 3.8f)) })
        add(cosa(V3(0.55f, 0f, 4.75f), listOf(V3(0.22f, 0f, 4.58f), V3(0.88f, 0f, 4.58f), V3(0.88f, 0f, 4.92f), V3(0.22f, 0f, 4.92f), V3(0.22f, 0.3f, 4.58f), V3(0.88f, 0.3f, 4.92f))) { cajaDeHerramientas(v, V3(0.55f, 0f, 4.75f)) })
        add(cosa(V3(0.85f, 0f, 6.3f), K.circulo(V3(0.85f, 0f, 6.3f), K.ARRIBA, 0.3f, 8) + K.circulo(V3(0.85f, 0.88f, 6.3f), K.ARRIBA, 0.3f, 8)) { bidon(v, V3(0.85f, 0f, 6.3f), C.VERDE) })
        add(cosa(V3(-0.98f, 0f, 2.9f), listOf(V3(S.BANCADA_X0, 0f, S.BANCADA_Z0), V3(S.BANCADA_X1, 0f, S.BANCADA_Z0), V3(S.BANCADA_X1, 0f, S.BANCADA_Z1), V3(S.BANCADA_X0, 0f, S.BANCADA_Z1), V3(S.BANCADA_X0, S.BANCADA_Y, S.BANCADA_Z0), V3(S.BANCADA_X1, S.BANCADA_Y, S.BANCADA_Z0), V3(S.BANCADA_X1, S.BANCADA_Y, S.BANCADA_Z1), V3(S.BANCADA_X0, S.BANCADA_Y, S.BANCADA_Z1))) { B.run { bancada(v) } })
        val t = PiezasBancada.TABURETE
        add(cosa(t, K.circulo(t, K.ARRIBA, 0.17f, 6) + K.circulo(t + V3(0f, 0.64f, 0f), K.ARRIBA, 0.16f, 6)) { B.run { taburete(v, t) } })
        if (R.filtros(m)) {
            add(cosa(V3(-0.75f, 0f, 5.1f), K.circulo(V3(-0.75f, 0f, 5.1f), K.ARRIBA, 0.42f, 8)) { M.run { tapaDelFiltro(v, V3(-0.75f, 0f, 5.1f)) } })
            for ((k, q) in listOf(V3(-2.45f, 0f, 4.6f), V3(-0.45f, 0f, 4.2f), V3(-0.95f, 0f, 4.05f)).withIndex()) add(cosa(q, K.circulo(q, K.ARRIBA, 0.17f, 6) + K.circulo(q + V3(0f, 0.32f, 0f), K.ARRIBA, 0.17f, 6)) { cubo(v, q, if (k == 1) C.AZUL else C.ACERO, if (k == 2) lerp(C.PAPEL, C.ROCA, 0.4f) else C.BARRO) })
        }
        if (R.candado(m)) add(cosa(V3(-0.6f, 0f, 6.6f), listOf(V3(-1.0f, 0f, 6.4f), V3(-0.2f, 0f, 6.4f), V3(-0.2f, 0f, 6.8f), V3(-1.0f, 0f, 6.8f), V3(-0.6f, 0.75f, 6.6f))) { M.run { filtrosSaturados(v, V3(-0.6f, 0f, 6.6f)) } })
        if (R.botellasEnFila(m)) {
            // La cola de botellas entra del pasillo y llega hasta media sala.
            for (k in 0..6) { val q = V3(0.25f + 0.1f * (k % 2), 0f, 7.25f - 0.42f * k); add(cosa(q, K.circulo(q, K.ARRIBA, 0.08f, 5)) { botellaEnSuelo(v, q, k, 1.2f, false) }) }
        }
        if (R.hojasSecas(m)) for ((k, q) in listOf(V3(1.35f, 0f, 6.0f), V3(1.25f, 0f, 5.35f)).withIndex()) add(cosa(q, K.circulo(q, K.ARRIBA, 0.3f, 8) + K.circulo(q + V3(0f, 0.88f, 0f), K.ARRIBA, 0.3f, 8)) { bidon(v, q, if (k == 0) C.AZUL else C.ACERO) })
        if (R.herramientasPorTodas(m)) add(cosa(V3(0.2f, 0f, 5.6f), emptyList()) { herramientasEnElSuelo(v) })
        grande(v)?.let { add(it) }
    }

    private fun cosa(donde: V3, esqueleto: List<V3>, pintar: DrawScope.() -> Unit) = CosaRefugio(K.hondo(donde), esqueleto, pintar)

    /** Donde se echa la manta: sobre el asiento del taburete (su canto, 5 cm bajo el asiento, y su pie, debajo). */
    private val CANTO_MANTA: V3 get() = PiezasBancada.TABURETE + V3(0.18f, 0.61f, 0.08f)

    /**
     * El objeto grande del día, como cosa del cuadro. Se pinta después de la bancada (o del taburete) en que se apoya,
     * aunque su pie quede más al fondo. La manta no cabe en la punta de la bancada, que se ve de filo: va echada sobre el
     * taburete, donde cae por los dos lados y se lee como manta **[Diseño propio]**.
     */
    private fun grande(v: VistaRefugio): CosaRefugio? {
        val m = v.m
        val enTaburete = m.ficha.objeto == "manta"
        val pie = if (enTaburete) V3(CANTO_MANTA.x, 0f, CANTO_MANTA.z) else S.PIE_GRANDE
        val apoyo = if (enTaburete) CANTO_MANTA else S.APOYO_GRANDE
        val o = ObjetosRefugio.grande(m, v.pincel(pie), K.p(pie), K.p(apoyo), K.k(pie) / 120f) ?: return null
        val antes = if (enTaburete) K.hondo(PiezasBancada.TABURETE) else K.hondo(V3(-0.98f, 0f, 2.9f))
        return CosaRefugio(minOf(K.hondo(pie), antes - 0.01f), listOf(pie)) { o.pintar(this) }
    }

    /**
     * Un cubo de chapa abollado, con su asa, su borde brillante y lo que tiene dentro ([dentro]: agua, barro, arena), con
     * la pintura saltada abajo.
     */
    fun DrawScope.cubo(v: VistaRefugio, c: V3, chapa: Color, dentro: Color) {
        Trazo3.run {
            cilindro(v, c, c + V3(0f, 0.34f, 0f), 0.18f, chapa, lerp(chapa, C.TINTA, 0.3f), lados = 16, tapas = false)
            disco(v, c + V3(0f, 0.29f, 0f), K.ARRIBA, 0.175f, dentro, lados = 16)
            aro(c + V3(0f, 0.34f, 0f), K.ARRIBA, 0.18f, 0.016f, v.c(c + V3(0f, 0.34f, 0f), K.ARRIBA, lerp(chapa, Color.White, 0.35f)), 18)
            aro(c + V3(0f, 0.08f, 0f), K.ARRIBA, 0.172f, 0.012f, v.c(c, K.NADA, lerp(chapa, C.TINTA, 0.25f)), 18)
            comba(c + V3(-0.18f, 0.31f, 0f), c + V3(0.18f, 0.31f, 0f), -0.14f, 0.012f, v.c(c, K.NADA, C.ACERO))
        }
        // La abolladura y el óxido del culo.
        Trazo3.run { disco(v, c + V3(0.05f, 0.18f, -0.178f), K.FRENTE, 0.05f, lerp(chapa, C.TINTA, 0.35f), lados = 8) }
        Trazo3.run { barra(c + V3(-0.12f, 0.03f, -0.13f), c + V3(0.1f, 0.03f, -0.15f), 0.04f, v.c(c, K.FRENTE, C.OXIDO).copy(alpha = 0.6f)) }
    }

    /** Un bidón de chapa, de pie, con sus aros, su tapón, su etiqueta de cinta y la pintura saltada. */
    private fun DrawScope.bidon(v: VistaRefugio, c: V3, color: Color) {
        Trazo3.run {
            cilindro(v, c, c + V3(0f, 0.88f, 0f), 0.29f, color, lerp(color, C.TINTA, 0.2f), lados = 18)
            for (y in listOf(0.29f, 0.59f)) aro(c + V3(0f, y, 0f), K.ARRIBA, 0.297f, 0.025f, v.c(c + V3(0.29f, y, 0f), K.DER, lerp(color, C.TINTA, 0.35f)), 20)
            aro(c + V3(0f, 0.88f, 0f), K.ARRIBA, 0.29f, 0.02f, v.c(c + V3(0f, 0.88f, 0f), K.ARRIBA, lerp(color, Color.White, 0.25f)), 20)
            disco(v, c + V3(0.12f, 0.885f, -0.05f), K.ARRIBA, 0.045f, C.HIERRO, lados = 8)
            cara(v, listOf(c + V3(-0.12f, 0.42f, -0.29f), c + V3(0.06f, 0.42f, -0.29f), c + V3(0.06f, 0.48f, -0.29f), c + V3(-0.12f, 0.48f, -0.29f)), K.FRENTE, lerp(C.PAPEL, C.AMARILLO, 0.2f))
        }
        PiezasMaquinas.run { desconchones(v, c + V3(0f, 0.45f, -0.29f), K.FRENTE, 0.35f, 0.7f, "bidon${c.z}", 6) }
        Trazo3.run { barra(c + V3(-0.1f, 0.88f, -0.26f), c + V3(-0.08f, 0.55f, -0.29f), 0.03f, v.c(c, K.FRENTE, lerp(C.TINTA, color, 0.3f)).copy(alpha = 0.6f)) }
    }

    /** La manguera enrollada en el suelo, de goma roja gastada, con su boca de latón. */
    private fun DrawScope.manguera(v: VistaRefugio, c: V3) {
        val goma = lerp(C.TINTA, C.ROJO, 0.4f)
        for (k in 0..3) Trazo3.run { aro(c + V3(0.02f * k, 0.04f + 0.04f * k, 0f), K.ARRIBA, 0.3f - 0.025f * k, 0.05f, v.c(c + V3(0f, 0.1f, -0.3f), K.ARRIBA, goma), 22) }
        Trazo3.run { aro(c + V3(0.06f, 0.2f, 0f), K.ARRIBA, 0.22f, 0.012f, v.c(c + V3(0f, 0.2f, -0.2f), K.ARRIBA, lerp(goma, Color.White, 0.3f)).copy(alpha = 0.6f), 20) }
        Trazo3.run { comba(c + V3(0.25f, 0.06f, -0.15f), c + V3(0.6f, 0.025f, -0.55f), 0.0f, 0.05f, v.c(c, K.ARRIBA, goma)); cilindro(v, c + V3(0.6f, 0.025f, -0.55f), c + V3(0.66f, 0.03f, -0.66f), 0.035f, C.LATON, lados = 8) }
    }

    /**
     * La caja de herramientas roja, abierta: la chapa abollada con la pintura saltada en los cantos, la bandeja levantada, y
     * asomando las herramientas que se reconocen (el martillo, la llave inglesa, el destornillador).
     */
    private fun DrawScope.cajaDeHerramientas(v: VistaRefugio, c: V3) {
        val rojo = lerp(C.ROJO, C.TINTA, 0.1f)
        Trazo3.run {
            caja(v, c.x - 0.32f, c.x + 0.32f, 0f, 0.24f, c.z - 0.15f, c.z + 0.15f, rojo, lerp(C.ROJO, C.TINTA, 0.55f))
            cara(v, listOf(V3(c.x - 0.32f, 0.24f, c.z + 0.15f), V3(c.x + 0.32f, 0.24f, c.z + 0.15f), V3(c.x + 0.32f, 0.4f, c.z + 0.3f), V3(c.x - 0.32f, 0.4f, c.z + 0.3f)), K.unidad(V3(0f, 0.7f, -0.7f)), lerp(rojo, C.TINTA, 0.15f))
            caja(v, c.x - 0.28f, c.x + 0.05f, 0.25f, 0.3f, c.z - 0.12f, c.z + 0.05f, lerp(C.ROJO, C.TINTA, 0.3f))
            // El martillo, la llave inglesa y el destornillador.
            barra(V3(c.x - 0.2f, 0.3f, c.z), V3(c.x - 0.02f, 0.42f, c.z - 0.02f), 0.025f, v.c(c, K.NADA, lerp(C.COBRE, C.AMARILLO, 0.3f)))
            barra(V3(c.x - 0.06f, 0.4f, c.z - 0.06f), V3(c.x + 0.04f, 0.44f, c.z + 0.02f), 0.045f, v.c(c, K.NADA, C.ACERO), StrokeCap.Butt)
            barra(V3(c.x + 0.08f, 0.24f, c.z + 0.02f), V3(c.x + 0.24f, 0.36f, c.z + 0.05f), 0.03f, v.c(c, K.NADA, lerp(C.ACERO, Color.White, 0.15f)))
            disco(v, V3(c.x + 0.25f, 0.37f, c.z + 0.05f), K.unidad(V3(0.6f, 0.4f, -0.6f)), 0.035f, lerp(C.ACERO, Color.White, 0.15f), lados = 8)
            barra(V3(c.x + 0.12f, 0.24f, c.z - 0.06f), V3(c.x + 0.2f, 0.3f, c.z - 0.1f), 0.022f, v.c(c, K.NADA, C.AMARILLO))
        }
        // La pintura saltada en los cantos y una abolladura.
        Trazo3.run {
            barra(V3(c.x - 0.32f, 0.235f, c.z - 0.152f), V3(c.x - 0.12f, 0.235f, c.z - 0.152f), 0.02f, v.c(c, K.FRENTE, C.ACERO).copy(alpha = 0.8f))
            barra(V3(c.x + 0.2f, 0.01f, c.z - 0.152f), V3(c.x + 0.32f, 0.03f, c.z - 0.152f), 0.025f, v.c(c, K.FRENTE, C.OXIDO).copy(alpha = 0.8f))
            disco(v, V3(c.x + 0.05f, 0.12f, c.z - 0.153f), K.FRENTE, 0.05f, lerp(rojo, C.TINTA, 0.35f), lados = 8)
        }
    }

    /**
     * Las herramientas por el suelo de la cuarentena, en el claro de delante: la llave grande, el martillo, el destornillador,
     * una llave inglesa y un trapo tirado.
     */
    private fun DrawScope.herramientasEnElSuelo(v: VistaRefugio) {
        val acero = v.c(V3(0.2f, 0f, 4.4f), K.ARRIBA, lerp(C.ACERO, Color.White, 0.15f))
        Trazo3.run {
            cara(v, listOf(V3(-0.05f, 0.006f, 4.9f), V3(0.4f, 0.006f, 4.85f), V3(0.45f, 0.006f, 5.2f), V3(0.0f, 0.006f, 5.25f)), K.ARRIBA, lerp(C.PAPEL, C.ROCA, 0.35f))
            barra(V3(-0.05f, 0.025f, 4.3f), V3(0.45f, 0.025f, 4.6f), 0.05f, acero)
            disco(v, V3(-0.08f, 0.03f, 4.28f), K.ARRIBA, 0.08f, lerp(C.ACERO, C.HIERRO, 0.2f), lados = 8)
            barra(V3(0.1f, 0.025f, 5.55f), V3(0.4f, 0.025f, 5.45f), 0.04f, v.c(V3(0.2f, 0f, 5.5f), K.ARRIBA, lerp(C.COBRE, C.AMARILLO, 0.3f)))
            barra(V3(0.38f, 0.04f, 5.36f), V3(0.42f, 0.04f, 5.56f), 0.08f, v.c(V3(0.4f, 0f, 5.5f), K.ARRIBA, C.HIERRO), StrokeCap.Butt)
            barra(V3(0.0f, 0.02f, 3.75f), V3(0.2f, 0.02f, 3.9f), 0.035f, v.c(V3(0.1f, 0f, 3.8f), K.ARRIBA, C.AMARILLO))
            barra(V3(0.2f, 0.02f, 3.9f), V3(0.36f, 0.02f, 4.02f), 0.012f, acero)
        }
    }

    // ---------------------------------------------------------------- Lo que queda en el suelo de la sala

    /**
     * Lo que queda en el suelo de la sala, antes de las cosas: el reguero del desagüe que viene de la gotera, los charcos
     * que devuelven la luz de la lámpara, las huellas mojadas; y según el día, el barro de las botas que entra del pasillo,
     * las flores del sellado en el umbral, los papelitos de colores del primer día del «abierto».
     */
    fun DrawScope.enElSuelo(v: VistaRefugio) {
        val m = v.m
        // El reguero: de la gotera a la rejilla del desagüe.
        val reguero = listOf(V3(-2.25f, 0.002f, 3.85f), V3(-2.1f, 0.002f, 4.5f), V3(-2.2f, 0.002f, 5.2f), V3(-2.05f, 0.002f, 5.75f))
        Trazo3.run { for (i in 0 until reguero.size - 1) barra(reguero[i], reguero[i + 1], 0.1f, lerp(C.TINTA, C.PISCINA, 0.15f).copy(alpha = 0.35f)) }
        for ((c, r) in listOf(CUBO + V3(0.15f, 0f, 0.35f) to 0.45f, V3(-0.9f, 0f, 5.3f) to 0.35f, V3(-2.9f, 0f, 5.4f) to 0.3f)) charco(v, c, r)
        // Las huellas mojadas de ella, de la bomba a la bancada.
        for (k in 0..4) huella(v, V3(-2.0f + 0.32f * k, 0f, 3.9f - 0.18f * k + 0.08f * (k % 2)), lerp(C.TINTA, C.CHAPA, 0.3f), 0.22f, k)
        // El barro de las botas: de la boca del pasillo a la bancada, por el claro del suelo que se ve.
        if (R.barroDeBotas(m)) for (k in 0..8) huella(v, V3(0.08f + 0.2f * (k % 2), 0f, 7.35f - 0.4f * k), C.BARRO, 0.9f, k, 1.35f)
        // Las flores de papel del sellado: un camino de flores del umbral hacia delante, y una fila en el umbral.
        if (R.sellado(m)) {
            for (k in 0..5) PiezasBancada.run { florDePapel(v, V3(0.18f + 0.1f * (k % 2), 0.04f, 7.2f - 0.45f * k), 0.17f, listOf(C.ROJO, C.PAPEL, C.AMARILLO, C.AZUL)[k % 4]) }
            for (k in 0..3) PiezasBancada.run { florDePapel(v, V3(S.PORTON_X0 + 1.0f + 0.5f * k, 0.04f, S.FONDO - 0.35f), 0.15f, listOf(C.PAPEL, C.ROJO, C.AZUL, C.AMARILLO)[k % 4]) }
        }
        if (R.primerDia(m)) papelitos(v, S.PORTON_X0, S.PORTON_X1, 4.6f, S.FONDO, "sala", 60)
    }

    /** Un charco en el suelo, en [c], de [r] metros: oscuro y mojado, con el brillo de lo que lo alumbra. */
    private fun DrawScope.charco(v: VistaRefugio, c: V3, r: Float) {
        val az = SalaRefugio.azar("charco${(c.x * 10).toInt()}")
        val pts = (0 until 11).map { i -> val a = 6.283f * i / 11; val rr = r * az.entre(0.65f, 1.1f); V3(c.x + cos(a) * rr * 1.3f, 0.003f, c.z + sin(a) * rr) }
        Trazo3.run { cara(v, pts, K.ARRIBA, lerp(C.TINTA, C.CHAPA, 0.3f), alfa = 0.35f) }
        // El brillo: la luz que llega al charco, devuelta como un espejo.
        val luz = v.c(c, K.ARRIBA, Color.White)
        val p = K.p(c); val k = K.k(c)
        drawOval(Brush.radialGradient(listOf(luz.copy(alpha = 0.45f), luz.copy(alpha = 0.12f), Color.Transparent), p, r * k), Offset(p.x - r * k, p.y - r * k * 0.25f), androidx.compose.ui.geometry.Size(2f * r * k, 0.5f * r * k), blendMode = BlendMode.Plus)
        if (v.obras.isNotEmpty()) drawOval(lerp(C.LAMPARA, Color.White, 0.4f).copy(alpha = 0.3f * v.deLampara(c)), Offset(p.x - 0.3f * r * k, p.y - 0.05f * r * k), androidx.compose.ui.geometry.Size(0.6f * r * k, 0.1f * r * k), blendMode = BlendMode.Plus)
    }

    /** Una huella de bota en [c] (la punta hacia la sala), de [color] y [alfa], de [talla]: la suela y el tacón. */
    private fun DrawScope.huella(v: VistaRefugio, c: V3, color: Color, alfa: Float, k: Int, talla: Float = 1f) {
        val lado = if (k % 2 == 0) -1f else 1f
        val suela = listOf(V3(-0.05f, 0f, 0.02f), V3(0.05f, 0f, 0.0f), V3(0.06f, 0f, -0.12f), V3(0.0f, 0f, -0.16f), V3(-0.055f, 0f, -0.11f)).map { c + V3(it.x * lado * talla, 0.003f, it.z * talla) }
        Trazo3.run { cara(v, suela, K.ARRIBA, color, alfa = alfa) }
        Trazo3.run { cara(v, listOf(V3(-0.045f, 0.003f, 0.06f), V3(0.045f, 0.003f, 0.06f), V3(0.04f, 0.003f, 0.13f), V3(-0.04f, 0.003f, 0.13f)).map { V3(c.x + it.x * talla, 0.003f, c.z + it.z * talla) }, K.ARRIBA, color, alfa = alfa) }
    }

    /** Los papelitos de colores del primer día, por el suelo, entre [x0] y [x1] y de [z0] a [z1]. */
    private fun DrawScope.papelitos(v: VistaRefugio, x0: Float, x1: Float, z0: Float, z1: Float, que: String, cuantos: Int, enPasillo: Boolean = false) {
        val az = SalaRefugio.azar("papelitos$que")
        val colores = listOf(C.ROJO, C.AMARILLO, C.AZUL, C.ALGA, lerp(C.PAPEL, C.NARANJA, 0.4f))
        repeat(cuantos) {
            val q = V3(az.entre(x0, x1), 0.004f, az.entre(z0, z1)); val a = az.entre(0f, 3.1f); val r = az.entre(0.05f, 0.09f)
            Trazo3.run { cara(v, listOf(q + V3(cos(a) * r, 0f, sin(a) * r), q + V3(-sin(a) * r, 0f, cos(a) * r), q + V3(-cos(a) * r, 0f, -sin(a) * r), q + V3(sin(a) * r, 0f, -cos(a) * r)), K.ARRIBA, colores[it % colores.size], enPasillo) }
        }
    }

    /** La guirnalda de flores de papel colgada del arco del portón el primer día del «abierto». */
    fun DrawScope.guirnaldaDelPorton(v: VistaRefugio) {
        val z = S.FONDO - 0.06f
        val a = V3(S.PORTON_X0 - 0.15f, S.ARRANQUE + 0.25f, z); val b = V3(S.PORTON_X1 + 0.15f, S.ARRANQUE + 0.25f, z)
        Trazo3.run { comba(a, b, 0.45f, 0.012f, v.c(K.medio(a, b), K.NADA, C.TINTA)) }
        val colores = listOf(C.ROJO, C.AMARILLO, C.AZUL, lerp(C.PAPEL, C.NARANJA, 0.4f), C.ALGA)
        for (k in 1..13) {
            val t = k / 14f; val q = V3(a.x + (b.x - a.x) * t, a.y - 0.45f * 4f * t * (1f - t), z - 0.02f)
            PiezasBancada.run { florDePapel(v, q, 0.07f, colores[k % colores.size], K.FRENTE) }
        }
    }

    // ---------------------------------------------------------------- Lo del pasillo

    /**
     * Lo que hay en el suelo del pasillo según el día: el árbol de cable con sus lucecitas, el grifo medidor y las botellas
     * en fila del racionamiento, la ventanilla con botellas de la cuarentena, las cestas de algas de la cosecha, las flores
     * de papel del sellado y el barro de las botas, las hojas secas del «abierto» y, el primer día, los papelitos y las
     * huellas de todos hacia la Puerta.
     */
    fun DrawScope.delPasillo(v: VistaRefugio) {
        val m = v.m; val P = PasilloRefugio
        if (R.sellado(m)) {
            for (k in 0..7) { val q = V3(P.CENTRO_X - 1.5f + 0.42f * k, 0.05f, P.LINEA_Z - 0.2f); Trazo3.run { disco(v, q, K.unidad(V3(0f, 0.6f, -0.8f)), 0.14f, listOf(C.ROJO, C.PAPEL, C.AMARILLO)[k % 3], true, 7) } }
            for (k in 0..9) { val q = V3(P.IZQ + 0.62f + 0.08f * (k % 2), 0.05f, P.BOCA + 0.4f + 1.3f * k); PiezasBancada.run { florDePapel(v, q, 0.16f, listOf(C.ROJO, C.PAPEL, C.AMARILLO, C.AZUL)[k % 4]) } }
        }
        if (R.barroDeBotas(m)) for (k in 0..17) huella(v, V3(P.IZQ + 0.7f + 0.16f * (k % 2), 0f, P.BOCA + 0.2f + 0.8f * k), C.BARRO, 0.85f, k)
        if (R.hojasSecas(m)) { val az = Azar(Semilla.de(m.ficha.semilla, "hojas")); repeat(40) { val q = V3(az.entre(P.IZQ + 0.2f, P.DER - 0.2f), 0.005f, az.entre(P.BOCA, P.PUERTA_Z - 1f)); Trazo3.run { disco(v, q, K.ARRIBA, az.entre(0.06f, 0.12f), lerp(C.COBRE, C.AMARILLO, az.real()), true, 5) } } }
        if (R.primerDia(m)) {
            papelitos(v, P.IZQ + 0.2f, P.DER - 0.2f, P.BOCA, P.BOCA + 9f, "pasillo", 90, true)
            for (k in 0..23) huella(v, V3(P.CENTRO_X - 0.9f + 0.6f * (k % 4), 0f, P.BOCA + 0.6f + 0.9f * (k / 2)), lerp(C.TINTA, C.ROCA, 0.4f), 0.5f, k)
        }
        if (R.cosecha(m)) for ((k, q) in listOf(V3(P.DER - 0.55f, 0f, 11.2f), V3(P.DER - 0.6f, 0f, 11.9f), V3(P.IZQ + 0.6f, 0f, 13.0f), V3(P.IZQ + 0.55f, 0f, 14.2f), V3(P.DER - 0.5f, 0f, 14.6f), V3(P.IZQ + 0.6f, 0f, P.BOCA + 0.7f)).sortedByDescending { it.z }.withIndex()) cesta(v, q + V3(0.12f * (k % 2), 0f, 0f))
        if (R.botellasEnFila(m)) {
            val g = V3(P.IZQ + 0.1f, 1.0f, P.BOCA + 0.5f)
            Trazo3.run { caja(v, g.x, g.x + 0.2f, g.y - 0.17f, g.y + 0.28f, g.z - 0.17f, g.z + 0.17f, C.LATON, enPasillo = true); barra(g + V3(0.2f, 0f, 0f), g + V3(0.34f, -0.12f, 0f), 0.045f, v.c(g, K.NADA, C.LATON, true), StrokeCap.Butt); disco(v, g + V3(0.12f, 0.14f, -0.18f), K.FRENTE, 0.08f, lerp(C.PAPEL, Color.White, 0.3f), true, 12) }
            for (k in 11 downTo 0) botellaEnSuelo(v, V3(P.IZQ + 0.45f + 0.06f * (k % 2), 0f, P.BOCA + 0.7f + 0.5f * k), k, 1.15f, true)
        }
        if (R.mascarillas(m)) {
            for (k in 0..2) botellaEnSuelo(v, V3(P.IZQ + 0.35f, 0f, P.COMPUERTA_Z0 - 0.3f - 0.3f * k), k, 1.15f, true)
            Trazo3.run { caja(v, P.IZQ + 0.2f, P.IZQ + 0.6f, 0f, 0.25f, P.COMPUERTA_Z0 - 1.6f, P.COMPUERTA_Z0 - 1.2f, lerp(C.PAPEL, C.COBRE, 0.4f), enPasillo = true) }
        }
        if (R.arbolDeCable(m)) arbol(v, P.ARBOL, R.lucesDelArbol(m))
    }

    /** Una botella de agua en el suelo, de pie (vacía o con agua), de [talla]: con su tapón de color. */
    private fun DrawScope.botellaEnSuelo(v: VistaRefugio, c: V3, k: Int, talla: Float, enPasillo: Boolean) {
        val r = 0.065f * talla; val alto = 0.36f * talla
        val agua = (k * 37 % 10) / 10f
        Trazo3.run {
            cilindro(v, c, c + V3(0f, alto * agua, 0f), r, lerp(C.PISCINA, Color.White, 0.3f), lados = 8, enPasillo = enPasillo)
            cilindro(v, c + V3(0f, alto * agua, 0f), c + V3(0f, alto, 0f), r, lerp(Color.White, C.PISCINA, 0.35f).copy(alpha = 0.7f), lados = 8, enPasillo = enPasillo, tapas = false)
            cilindro(v, c + V3(0f, alto, 0f), c + V3(0f, alto + 0.09f * talla, 0f), r * 0.38f, lerp(Color.White, C.PISCINA, 0.35f), lados = 6, enPasillo = enPasillo)
            disco(v, c + V3(0f, alto + 0.092f * talla, 0f), K.ARRIBA, r * 0.42f, if (k % 3 == 0) C.ROJO else C.AZUL, enPasillo, 6)
        }
    }

    /** Una cesta de algas de la cosecha, en el suelo del pasillo. */
    private fun DrawScope.cesta(v: VistaRefugio, c: V3) {
        Trazo3.run {
            cilindro(v, c, c + V3(0f, 0.32f, 0f), 0.25f, lerp(C.COBRE, C.PAPEL, 0.35f), lados = 14, enPasillo = true, tapas = false)
            disco(v, c + V3(0f, 0.33f, 0f), K.ARRIBA, 0.24f, lerp(C.ALGA, C.TINTA, 0.2f), true, 14)
        }
        val az = Azar(Semilla.de("refugio", "algas", (c.z * 10).toInt()))
        repeat(6) { val q = c + V3(az.entre(-0.15f, 0.15f), 0.36f, az.entre(-0.12f, 0.12f)); drawCircle(v.c(q, K.ARRIBA, C.ALGA, true), 0.05f * K.k(q), K.p(q)) }
    }

    /**
     * El árbol de Nochebuena: «hecho de cable verde y trozos de tubo, con luces pequeñas que solo se encienden esta noche y
     * bolas de papel de colores». Un tronco de tubo, ramas de cable en pisos, bolas de papel.
     */
    private fun DrawScope.arbol(v: VistaRefugio, pie: V3, luces: Boolean) {
        Trazo3.run { cilindro(v, pie, pie + V3(0f, 1.7f, 0f), 0.05f, C.ACERO, lados = 8, enPasillo = true) }
        val colores = listOf(C.ROJO, C.AMARILLO, C.AZUL, C.NARANJA)
        for (piso in 0..5) {
            val y = 0.35f + 0.24f * piso; val r = 0.55f - 0.08f * piso
            for (k in 0 until 7) {
                val a = 2f * PI.toFloat() * k / 7 + 0.4f * piso
                val punta = pie + V3(cos(a) * r, y - 0.12f, sin(a) * r)
                Trazo3.run { barra(pie + V3(0f, y, 0f), punta, 0.025f, v.c(punta, K.NADA, lerp(C.ALGA, C.CHAPA, 0.3f), true)) }
                if ((k + piso) % 3 == 0) Trazo3.run { disco(v, punta + V3(0f, -0.05f, 0f), K.FRENTE, 0.05f, colores[(k + piso) % 4], true, 8) }
                if (luces && (k + piso) % 2 == 0) { val p = K.p(punta); drawCircle(lerp(C.GUARDIA, Color.White, 0.5f), 0.02f * K.k(punta), p); drawCircle(Brush.radialGradient(listOf(C.GUARDIA.copy(alpha = 0.5f), Color.Transparent), p, 0.1f * K.k(punta)), 0.1f * K.k(punta), p, blendMode = BlendMode.Plus) }
            }
        }
        Trazo3.run { disco(v, pie + V3(0f, 1.75f, 0f), K.FRENTE, 0.07f, C.AMARILLO, true, 5) }
    }

    // ---------------------------------------------------------------- Las lámparas de trabajo

    /**
     * La lámpara de jaula: la pantallita de chapa, la bombilla y su jaula de alambre, el gancho, y el cable que sube a un
     * tubo y cuelga en comba hasta el enchufe. Encendida, donde está la obra; apagada, colgada donde quedó (sobre el filtro).
     */
    fun DrawScope.lamparas(v: VistaRefugio) {
        val encendidas = v.obras
        val sitios = if (encendidas.isEmpty()) listOf(Obra.FILTRO) else encendidas
        for (o in sitios) lampara(v, S.bombilla(o), S.haciaDonde(o), encendidas.isNotEmpty())
    }

    private fun DrawScope.lampara(v: VistaRefugio, b: V3, eje: V3, encendida: Boolean) {
        val gancho = V3(b.x, 2.84f, b.z)
        val hierro = v.c(b, K.NADA, C.HIERRO)
        Trazo3.run {
            barra(gancho, b + V3(0f, 0.17f, 0f), 0.012f, hierro)
            comba(gancho, V3(b.x + 0.9f, 2.88f, b.z + 0.5f), 0.35f, 0.012f, hierro)
            aro(gancho + V3(0f, 0.03f, 0f), K.FRENTE, 0.03f, 0.008f, v.c(gancho, K.NADA, C.ACERO), 8)
        }
        val atras = b - eje * 0.15f; val boca = b - eje * 0.05f
        val borde = K.circulo(boca, eje, 0.14f, 18); val cima = K.circulo(atras, eje, 0.045f, 18)
        val pantalla = poligono(K.casco((borde + cima).map { K.p(it) }))
        val bocaSeVe = K.seVe(boca, eje)
        val pb = K.p(b); val kb = K.k(b)
        val alambre = if (encendida) lerp(C.TINTA, C.LAMPARA, 0.25f) else v.c(b, K.NADA, lerp(C.LATON, C.TINTA, 0.6f))
        val chapa = if (encendida) lerp(C.NARANJA, C.TINTA, 0.2f) else lerp(C.NARANJA, C.TINTA, 0.62f)
        fun jaula() {
            val u = K.unidad(K.cruz(eje, if (kotlin.math.abs(eje.y) > 0.9f) K.DER else K.ARRIBA)); val w = K.cruz(eje, u)
            for (k in 0 until 6) {
                val a = 2f * PI.toFloat() * k / 6 + 0.3f
                val d = u * cos(a) + w * sin(a)
                val arriba = b - eje * 0.05f + d * 0.13f; val medio = b + eje * 0.08f + d * 0.08f; val abajo = b + eje * 0.16f
                Trazo3.run { barra(arriba, medio, 0.008f, alambre); barra(medio, abajo, 0.008f, alambre) }
            }
            Trazo3.run { aro(b + eje * 0.05f, eje, 0.105f, 0.008f, alambre, 14) }
        }
        fun bombilla() {
            if (encendida) drawCircle(Brush.radialGradient(listOf(Color.White, lerp(C.LAMPARA, Color.White, 0.5f), C.LAMPARA.copy(alpha = 0f)), pb, 0.09f * kb), 0.09f * kb, pb)
            else drawCircle(v.c(b, K.NADA, lerp(C.PAPEL, C.TINTA, 0.6f)), 0.04f * kb, pb)
        }
        if (bocaSeVe) {
            drawPath(pantalla, v.c(atras, eje * -1f, chapa))
            if (encendida) drawPath(K.forma(borde), lerp(C.LAMPARA, Color.White, 0.25f).copy(alpha = 0.9f))
            bombilla(); jaula()
        } else {
            bombilla(); jaula()
            if (encendida) drawCircle(Brush.radialGradient(listOf(C.LAMPARA.copy(alpha = 0.55f), Color.Transparent), pb, 0.32f * kb), 0.32f * kb, pb, blendMode = BlendMode.Plus)
            drawPath(pantalla, v.c(atras, eje * -1f, chapa))
            if (encendida) drawPath(pantalla, lerp(C.LAMPARA, Color.White, 0.4f).copy(alpha = 0.6f), style = Stroke(0.9f))
        }
    }
}
