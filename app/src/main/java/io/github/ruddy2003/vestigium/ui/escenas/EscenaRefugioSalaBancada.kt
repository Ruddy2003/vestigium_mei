package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Obra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * La bancada de trabajo y lo suyo: las patas de ángulo con la pintura saltada, el estante con la caja de piezas y el rollo
 * de alambre, los cajones (uno a medio abrir, con su etiqueta de cinta sin letras), la tapa de madera gastada con sus cercos
 * de aceite, el tornillo de banco en el canto; encima, el turno, de atrás adelante y tapándose: el microscopio, la gradilla
 * de tubitos, el tocadiscos pequeño, la botella, el filtro pequeño, la aceitera, el manómetro desmontado, el cuaderno abierto
 * con su lápiz, la fiambrera, los alicates, las juntas, la linterna de manivela; y lo de cada día. Y el taburete.
 */
internal object PiezasBancada {
    private val C = ColorRefugio
    private val K = CamRefugio
    private val R = EscenaRefugio
    private val S = SalaRefugio

    /** El taburete, a la izquierda de la bancada, entre ella y la bomba grande. */
    val TABURETE = V3(-2.12f, 0f, 2.62f)

    fun DrawScope.bancada(v: VistaRefugio) {
        val x0 = S.BANCADA_X0; val x1 = S.BANCADA_X1; val z0 = S.BANCADA_Z0; val z1 = S.BANCADA_Z1; val y = S.BANCADA_Y
        for ((x, z) in listOf(x0 + 0.04f to z1 - 0.04f, x1 - 0.04f to z1 - 0.04f)) pata(v, x, z, y)
        Trazo3.run { caja(v, x0 + 0.05f, x1 - 0.05f, 0.2f, 0.24f, z0 + 0.05f, z1 - 0.05f, lerp(C.CHAPA, C.HIERRO, 0.4f)) }
        abajo(v, x0, z0, z1)
        cajones(v, x0, z0, y)
        for ((x, z) in listOf(x0 + 0.04f to z0 + 0.04f, x1 - 0.04f to z0 + 0.04f)) pata(v, x, z, y)
        tapa(v, x0, x1, z0, z1, y)
        tornillo(v, x0, z0, y)
        encima(v, y)
        trapo(v, V3(-0.3f, y - 0.01f, z0 - 0.02f), 0)
        if (R.guirnalda(v.m)) guirnalda(v, V3(x0 + 0.05f, y - 0.02f, z0 - 0.03f), V3(x1 - 0.05f, y - 0.02f, z0 - 0.03f))
    }

    /** Una pata de ángulo de hierro, con la pintura saltada abajo. */
    private fun DrawScope.pata(v: VistaRefugio, x: Float, z: Float, y: Float) {
        Trazo3.run { caja(v, x - 0.03f, x + 0.03f, 0f, y - 0.05f, z - 0.03f, z + 0.03f, C.HIERRO) }
        Trazo3.run { barra(V3(x, 0.02f, z - 0.035f), V3(x, 0.16f, z - 0.035f), 0.05f, v.c(V3(x, 0.1f, z), K.FRENTE, C.OXIDO).copy(alpha = 0.7f), StrokeCap.Butt) }
    }

    /** Lo del estante de abajo: la caja de piezas de madera vieja con tubos y bridas asomando, y el rollo de alambre. */
    private fun DrawScope.abajo(v: VistaRefugio, x0: Float, z0: Float, z1: Float) {
        Trazo3.run { caja(v, x0 + 0.68f, x0 + 1.18f, 0.24f, 0.46f, z0 + 0.2f, z1 - 0.1f, lerp(C.COBRE, C.TINTA, 0.45f), lerp(C.COBRE, C.TINTA, 0.3f)) }
        Trazo3.run { barra(V3(x0 + 0.76f, 0.46f, 2.95f), V3(x0 + 0.92f, 0.64f, 3.05f), 0.045f, v.c(V3(x0, 0.5f, 3f), K.NADA, C.ACERO)); barra(V3(x0 + 0.98f, 0.46f, 3.0f), V3(x0 + 1.06f, 0.6f, 2.9f), 0.035f, v.c(V3(x0, 0.5f, 3f), K.NADA, C.COBRE)) }
        Trazo3.run { aro(V3(x0 + 0.85f, 0.5f, 2.98f), K.unidad(V3(0.3f, 0.2f, -1f)), 0.07f, 0.02f, v.c(V3(x0 + 0.85f, 0.5f, 2.9f), K.FRENTE, lerp(C.VERDE, C.HIERRO, 0.3f)), 12) }
        for (k in 0..4) Trazo3.run { aro(V3(x0 + 1.4f, 0.26f + 0.02f * k, 3.0f), K.ARRIBA, 0.1f - 0.004f * k, 0.02f, v.c(V3(x0 + 1.4f, 0.3f, 2.9f), K.FRENTE, lerp(C.COBRE, if (k % 2 == 0) C.TINTA else C.LATON, 0.25f)), 16) }
    }

    /**
     * Los cajones de la izquierda, colgados bajo la tapa: dos frentes de chapa con su tirador y su etiqueta de cinta (sin
     * letras); el de abajo, a medio abrir, con sus tornillos y juntas dentro.
     */
    private fun DrawScope.cajones(v: VistaRefugio, x0: Float, z0: Float, y: Float) {
        val xa = x0 + 0.06f; val xb = x0 + 0.62f
        Trazo3.run { caja(v, xa, xb, y - 0.36f, y - 0.1f, z0 + 0.02f, z0 + 0.5f, lerp(C.CHAPA, C.HIERRO, 0.3f)) }
        for ((k, fuera) in listOf(0f, 0.17f).withIndex()) {
            val ya = y - 0.23f - 0.13f * k; val yb = ya + 0.12f; val zf = z0 + 0.015f - fuera
            if (fuera > 0f) {
                Trazo3.run { caja(v, xa + 0.02f, xb - 0.02f, ya + 0.01f, yb - 0.03f, zf, z0 + 0.03f, lerp(C.CHAPA, C.HIERRO, 0.5f), lerp(C.TINTA, C.CHAPA, 0.2f)) }
                for (j in 0..4) { val p = V3(xa + 0.08f + 0.09f * j, yb - 0.025f, zf + 0.06f + 0.03f * (j % 2)); drawCircle(v.c(p, K.ARRIBA, if (j % 2 == 0) C.LATON else C.TINTA), 0.016f * K.k(p), K.p(p)) }
            }
            Trazo3.run {
                cara(v, listOf(V3(xa, ya, zf), V3(xb, ya, zf), V3(xb, yb, zf), V3(xa, yb, zf)), K.FRENTE, lerp(C.VERDE, C.CHAPA, 0.4f))
                barra(V3((xa + xb) / 2f - 0.06f, ya + 0.05f, zf - 0.015f), V3((xa + xb) / 2f + 0.06f, ya + 0.05f, zf - 0.015f), 0.018f, v.c(V3(xa, ya, zf), K.FRENTE, C.ACERO))
                cara(v, listOf(V3(xa + 0.05f, ya + 0.08f, zf - 0.004f), V3(xa + 0.17f, ya + 0.08f, zf - 0.004f), V3(xa + 0.17f, ya + 0.105f, zf - 0.004f), V3(xa + 0.05f, ya + 0.105f, zf - 0.004f)), K.FRENTE, lerp(C.PAPEL, C.AMARILLO, 0.2f))
            }
        }
        PiezasMaquinas.run { desconchones(v, V3((xa + xb) / 2f, y - 0.2f, z0), K.FRENTE, 0.45f, 0.2f, "cajones", 4) }
    }

    /** La tapa: la chapa con su canto y, encima, el tablero de madera gastada con sus vetas, cercos de aceite, rayas y el charco de la lámpara. */
    private fun DrawScope.tapa(v: VistaRefugio, x0: Float, x1: Float, z0: Float, z1: Float, y: Float) {
        Trazo3.run { caja(v, x0, x1, y - 0.1f, y - 0.04f, z0, z1, lerp(C.CHAPA, C.HIERRO, 0.2f), lerp(C.CHAPA, C.HIERRO, 0.2f)) }
        val madera = lerp(lerp(C.COBRE, C.AMARILLO, 0.25f), C.HORMIGON, 0.28f)
        Trazo3.run { caja(v, x0 - 0.02f, x1 + 0.02f, y - 0.04f, y, z0 - 0.02f, z1 + 0.01f, lerp(madera, C.TINTA, 0.25f), madera) }
        // El canto comido: mordiscos oscuros en el frente.
        for (k in 0..5) { val x = x0 + 0.1f + 0.28f * k; Trazo3.run { barra(V3(x, y - 0.02f, z0 - 0.025f), V3(x + 0.08f, y - 0.025f, z0 - 0.025f), 0.02f, v.c(V3(x, y, z0), K.FRENTE, lerp(madera, C.TINTA, 0.55f))) } }
        with(LuzRefugio) { charcoEnLaBancada(v, x0, x1, z0, z1, y) }
        val vz = SalaRefugio.azar("vetas")
        for (k in 1..6) { val z = z0 + (z1 - z0) * k / 7f + vz.entre(-0.02f, 0.02f); Trazo3.run { barra(V3(x0 + vz.entre(0f, 0.3f), y + 0.002f, z), V3(x1 - vz.entre(0f, 0.3f), y + 0.002f, z + vz.entre(-0.03f, 0.03f)), 0.008f, v.c(V3(-1f, y, z), K.ARRIBA, lerp(madera, C.TINTA, 0.35f)).copy(alpha = 0.6f)) } }
        // Los cercos de aceite y de las latas, y la mancha grande.
        for ((x, z, r) in listOf(Triple(-1.4f, 2.66f, 0.06f), Triple(-0.95f, 3.1f, 0.05f), Triple(-0.35f, 2.7f, 0.045f))) Trazo3.run { aro(V3(x, y + 0.002f, z), K.ARRIBA, r, 0.01f, v.c(V3(x, y, z), K.ARRIBA, lerp(madera, C.TINTA, 0.5f)).copy(alpha = 0.7f), 14) }
        Trazo3.run { cara(v, listOf(V3(x0 + 0.5f, y + 0.002f, z0 + 0.2f), V3(x0 + 0.85f, y + 0.002f, z0 + 0.15f), V3(x0 + 0.8f, y + 0.002f, z0 + 0.4f), V3(x0 + 0.45f, y + 0.002f, z0 + 0.42f)), K.ARRIBA, C.TINTA, alfa = 0.25f) }
    }

    /** El tornillo de banco, grande, en el canto de la izquierda: el cuerpo verde, las mordazas, el husillo y la palanca. */
    private fun DrawScope.tornillo(v: VistaRefugio, x0: Float, z0: Float, y: Float) {
        val verde = lerp(C.VERDE, C.HIERRO, 0.35f)
        Trazo3.run {
            caja(v, x0 + 0.02f, x0 + 0.26f, y, y + 0.16f, z0 - 0.04f, z0 + 0.2f, verde)
            caja(v, x0 + 0.02f, x0 + 0.26f, y + 0.03f, y + 0.17f, z0 - 0.15f, z0 - 0.08f, verde)
            caja(v, x0 + 0.03f, x0 + 0.25f, y + 0.16f, y + 0.19f, z0 - 0.09f, z0 - 0.04f, lerp(C.ACERO, Color.White, 0.1f))
            barra(V3(x0 + 0.14f, y + 0.08f, z0 - 0.16f), V3(x0 + 0.14f, y + 0.08f, z0 - 0.36f), 0.026f, v.c(V3(x0, y, z0), K.NADA, C.ACERO))
            barra(V3(x0 + 0.02f, y + 0.08f, z0 - 0.36f), V3(x0 + 0.27f, y + 0.06f, z0 - 0.36f), 0.02f, v.c(V3(x0, y, z0), K.NADA, lerp(C.ACERO, Color.White, 0.2f)))
        }
        PiezasMaquinas.run { desconchones(v, V3(x0 + 0.14f, y + 0.08f, z0 - 0.04f), K.FRENTE, 0.2f, 0.1f, "tornillo", 3) }
    }

    // ---------------------------------------------------------------- Lo de encima

    /** Lo de encima de la bancada, de atrás adelante. */
    private fun DrawScope.encima(v: VistaRefugio, y: Float) {
        val m = v.m
        val zb = 3.08f; val zm = 2.92f; val zf = 2.74f
        microscopio(v, V3(-1.55f, y, zb))
        if (R.senal(m)) radio(v, V3(-1.1f, y, zb)) else gradilla(v, V3(-1.12f, y, zb))
        if (R.tocadiscosRoto(m)) tocadiscosGrande(v, V3(-0.62f, y, zb)) else if (R.laSalaPinta(m, "tocadiscos")) tocadiscos(v, V3(-0.66f, y, zb), R.lampara(m) == Obra.BANCADA || R.racionamiento(m) && R.lampara(m) != null)
        botella(v, V3(-0.26f, y, zb + 0.02f), if (R.racionamiento(m)) 0.15f else 0.75f, R.bombaTres(m))
        aceitera(v, V3(-1.72f, y, zm + 0.08f))
        filtroPequeno(v, V3(-1.33f, y, zm))
        if (R.latas(m)) latas(v, V3(-0.95f, y, zm))
        if (R.florDeVerdad(m)) florEnFrasco(v, V3(-0.3f, y, 2.9f), R.florSeca(m))
        manometroSuelto(v, V3(-1.6f, y, zf))
        if (R.laSalaPinta(m, "cuaderno")) cuaderno(v, V3(-1.2f, y, zf), R.cuadernoAbierto(m))
        fiambrera(v, V3(-0.84f, y, zf + 0.02f), R.fiambreraAbierta(m))
        alicates(v, V3(-0.98f, y, zf - 0.12f))
        juntas(v, V3(-1.45f, y, zf - 0.13f))
        if (R.gafasDeCarton(m)) gafas(v, V3(-1.48f, y, 2.66f))
        if (R.mascarillas(m)) jabon(v, V3(-0.68f, y, 2.66f))
        if (R.herramientasPorTodas(m)) herramientas(v, y)
        if (R.sellado(m) || R.luto(m)) florDePapel(v, V3(-1.42f, y + 0.02f, 2.68f), 0.05f)
        with(ObjetosRefugio) { sobreElSoporte(m, v.pincel(S.SOPORTE), K.p(S.SOPORTE), K.k(S.SOPORTE) / 120f) }
    }

    /** El microscopio: la base, el brazo curvo, la platina con su portaobjetos, el tubo y el ocular, y el espejito. */
    private fun DrawScope.microscopio(v: VistaRefugio, c: V3) {
        val negro = lerp(C.TINTA, C.CHAPA, 0.2f)
        Trazo3.run {
            caja(v, c.x - 0.1f, c.x + 0.1f, c.y, c.y + 0.04f, c.z - 0.08f, c.z + 0.1f, C.TINTA)
            caja(v, c.x - 0.03f, c.x + 0.03f, c.y + 0.04f, c.y + 0.3f, c.z + 0.05f, c.z + 0.1f, negro)
            caja(v, c.x - 0.08f, c.x + 0.08f, c.y + 0.13f, c.y + 0.15f, c.z - 0.06f, c.z + 0.06f, C.ACERO)
            cara(v, listOf(V3(c.x - 0.05f, c.y + 0.152f, c.z - 0.02f), V3(c.x + 0.05f, c.y + 0.152f, c.z - 0.02f), V3(c.x + 0.05f, c.y + 0.152f, c.z + 0.01f), V3(c.x - 0.05f, c.y + 0.152f, c.z + 0.01f)), K.ARRIBA, lerp(Color.White, C.PISCINA, 0.2f))
            cilindro(v, c + V3(0f, 0.18f, -0.01f), c + V3(0f, 0.38f, 0.04f), 0.028f, lerp(C.TINTA, C.ACERO, 0.45f), lados = 10)
            cilindro(v, c + V3(0f, 0.38f, 0.04f), c + V3(0f, 0.42f, 0.045f), 0.032f, C.TINTA, lados = 10)
            disco(v, c + V3(0f, 0.08f, -0.02f), K.unidad(V3(0f, 0.5f, -1f)), 0.03f, lerp(C.ACERO, Color.White, 0.4f), lados = 8)
            cilindro(v, c + V3(0.035f, 0.24f, 0.08f), c + V3(0.07f, 0.24f, 0.08f), 0.025f, C.ACERO, lados = 8)
        }
    }

    /** La gradilla de tubitos de muestra: «tubitos de cristal numerados» (sin números), con el agua de cada punto. */
    private fun DrawScope.gradilla(v: VistaRefugio, c: V3) {
        Trazo3.run { caja(v, c.x - 0.17f, c.x + 0.17f, c.y, c.y + 0.08f, c.z - 0.05f, c.z + 0.05f, lerp(C.AMARILLO, C.COBRE, 0.4f)) }
        val aguas = listOf(lerp(C.PISCINA, Color.White, 0.5f), lerp(C.PISCINA, C.ROCA, 0.4f), lerp(C.BARRO, C.ROCA, 0.4f), lerp(C.PISCINA, Color.White, 0.6f), lerp(C.ALGA, C.PISCINA, 0.4f), lerp(C.PISCINA, Color.White, 0.3f))
        for (k in 0..5) {
            val x = c.x - 0.14f + 0.056f * k
            Trazo3.run {
                cilindro(v, V3(x, c.y + 0.01f, c.z), V3(x, c.y + 0.13f, c.z), 0.016f, aguas[k], lados = 6)
                cilindro(v, V3(x, c.y + 0.13f, c.z), V3(x, c.y + 0.19f, c.z), 0.016f, lerp(Color.White, C.PISCINA, 0.2f).copy(alpha = 0.7f), lados = 6, tapas = false)
                disco(v, V3(x, c.y + 0.195f, c.z), K.ARRIBA, 0.017f, C.ROJO, lados = 6)
            }
        }
    }

    /** La radio de la sala, encendida el día de la señal: la caja, el dial verde, la antena. */
    private fun DrawScope.radio(v: VistaRefugio, c: V3) {
        Trazo3.run {
            caja(v, c.x - 0.17f, c.x + 0.17f, c.y, c.y + 0.2f, c.z - 0.08f, c.z + 0.08f, lerp(C.COBRE, C.TINTA, 0.45f))
            cara(v, listOf(V3(c.x - 0.14f, c.y + 0.06f, c.z - 0.082f), V3(c.x + 0.02f, c.y + 0.06f, c.z - 0.082f), V3(c.x + 0.02f, c.y + 0.15f, c.z - 0.082f), V3(c.x - 0.14f, c.y + 0.15f, c.z - 0.082f)), K.FRENTE, lerp(C.ALGA, Color.White, 0.5f))
            barra(c + V3(0.12f, 0.2f, 0f), c + V3(0.22f, 0.62f, 0.05f), 0.007f, v.c(c, K.NADA, C.ACERO))
        }
        val d = K.p(V3(c.x - 0.06f, c.y + 0.1f, c.z - 0.085f))
        drawCircle(Brush.radialGradient(listOf(C.ALGA.copy(alpha = 0.5f), Color.Transparent), d, 0.28f * K.k(c)), 0.28f * K.k(c), d, blendMode = BlendMode.Plus)
    }

    /**
     * El tocadiscos pequeño de la sala, sin tapa: la caja baja de madera con sus dos perillas de latón, el plato con su disco
     * negro (con su brillo en anillo y su etiqueta), el eje, el brazo de metal que sale de su poste con el contrapeso detrás y
     * la cabeza sobre el disco; al lado, apoyada plana, la funda de papel de otro disco.
     */
    private fun DrawScope.tocadiscos(v: VistaRefugio, c: V3, sonando: Boolean) {
        val madera = lerp(C.COBRE, C.TINTA, 0.22f)
        Trazo3.run {
            caja(v, c.x - 0.2f, c.x + 0.2f, c.y, c.y + 0.1f, c.z - 0.15f, c.z + 0.15f, madera, lerp(madera, C.PAPEL, 0.12f))
            for (dx in listOf(-0.13f, -0.06f)) disco(v, V3(c.x + dx, c.y + 0.05f, c.z - 0.152f), K.FRENTE, 0.018f, C.LATON, lados = 8)
            cilindro(v, c + V3(-0.04f, 0.1f, 0f), c + V3(-0.04f, 0.115f, 0f), 0.13f, lerp(C.ACERO, C.TINTA, 0.4f), lados = 22)
            cilindro(v, c + V3(-0.04f, 0.115f, 0f), c + V3(-0.04f, 0.122f, 0f), 0.125f, C.TINTA, lados = 22)
        }
        val plato = c + V3(-0.04f, 0.123f, 0f)
        Trazo3.run {
            aro(plato, K.ARRIBA, 0.085f, 0.006f, lerp(C.TINTA, Color.White, 0.3f).copy(alpha = 0.6f), 18)
            disco(v, plato + V3(0f, 0.001f, 0f), K.ARRIBA, 0.045f, if (sonando) C.ROJO else lerp(C.PAPEL, C.NARANJA, 0.3f), lados = 12)
            cilindro(v, plato, plato + V3(0f, 0.025f, 0f), 0.005f, C.ACERO, lados = 6)
            // El brazo: el poste atrás a la derecha, el contrapeso, la barra y la cabeza sobre el disco.
            val poste = c + V3(0.14f, 0.1f, 0.09f)
            cilindro(v, poste, poste + V3(0f, 0.06f, 0f), 0.022f, C.ACERO, lados = 8)
            val giro = poste + V3(0f, 0.065f, 0f)
            val cabeza = if (sonando) plato + V3(0.06f, 0.01f, -0.07f) else c + V3(0.17f, 0.13f, -0.08f)
            barra(giro + V3(0.03f, 0f, 0.05f), giro, 0.03f, v.c(giro, K.NADA, C.TINTA))
            barra(giro, cabeza + V3(0f, 0.02f, 0f), 0.01f, v.c(giro, K.ARRIBA, lerp(C.ACERO, Color.White, 0.35f)))
            caja(v, cabeza.x - 0.02f, cabeza.x + 0.02f, cabeza.y, cabeza.y + 0.02f, cabeza.z - 0.03f, cabeza.z + 0.01f, lerp(C.ACERO, C.TINTA, 0.3f))
        }
        // La funda de papel de otro disco, plana junto a la caja, con su círculo de color.
        val f = c + V3(0.33f, 0.003f, -0.05f)
        Trazo3.run {
            cara(v, listOf(f + V3(-0.12f, 0f, -0.14f), f + V3(0.14f, 0f, -0.12f), f + V3(0.12f, 0f, 0.15f), f + V3(-0.14f, 0f, 0.13f)), K.ARRIBA, lerp(C.PAPEL, C.AZUL, 0.3f))
            disco(v, f + V3(0f, 0.002f, 0f), K.ARRIBA, 0.08f, lerp(C.NARANJA, C.AMARILLO, 0.3f), lados = 14)
            disco(v, f + V3(0f, 0.003f, 0f), K.ARRIBA, 0.025f, C.TINTA, lados = 10)
        }
    }

    /** El tocadiscos del comedor, roto, abierto en la bancada para arreglarlo (el día `tocadiscos`): la caja grande, el plato fuera, las piezas. */
    private fun DrawScope.tocadiscosGrande(v: VistaRefugio, c: V3) {
        val madera = lerp(C.COBRE, C.TINTA, 0.15f)
        Trazo3.run {
            caja(v, c.x - 0.28f, c.x + 0.22f, c.y, c.y + 0.16f, c.z - 0.17f, c.z + 0.15f, madera, lerp(C.TINTA, madera, 0.3f))
            cilindro(v, c + V3(0.32f, 0f, -0.05f), c + V3(0.32f, 0.02f, -0.05f), 0.15f, C.ACERO, lados = 18)
            for (k in 0..3) disco(v, c + V3(-0.1f + 0.07f * k, 0.165f, -0.05f + 0.03f * (k % 2)), K.ARRIBA, 0.025f, if (k % 2 == 0) C.LATON else C.COBRE, lados = 8)
            barra(c + V3(-0.2f, 0.17f, -0.1f), c + V3(0.1f, 0.17f, 0.05f), 0.006f, v.c(c, K.ARRIBA, C.ROJO))
        }
    }

    /** La botella de agua, de vidrio, con [cuanta] agua (de 0 a 1): en el racionamiento, «dos dedos»; el día de la bomba tres, marrón. */
    private fun DrawScope.botella(v: VistaRefugio, c: V3, cuanta: Float, marron: Boolean = false) {
        val alto = 0.3f
        Trazo3.run {
            cilindro(v, c, c + V3(0f, alto * cuanta, 0f), 0.05f, if (marron) lerp(C.OXIDO, C.ROCA, 0.3f) else lerp(C.PISCINA, Color.White, 0.25f), lados = 10)
            cilindro(v, c + V3(0f, alto * cuanta, 0f), c + V3(0f, alto, 0f), 0.05f, lerp(Color.White, C.PISCINA, 0.3f).copy(alpha = 0.5f), lados = 10, tapas = false)
            cilindro(v, c + V3(0f, alto, 0f), c + V3(0f, alto + 0.08f, 0f), 0.02f, lerp(Color.White, C.PISCINA, 0.3f).copy(alpha = 0.6f), lados = 8)
            disco(v, c + V3(0f, alto + 0.082f, 0f), K.ARRIBA, 0.022f, C.ROJO, lados = 8)
            barra(c + V3(-0.028f, 0.02f, -0.04f), c + V3(-0.028f, alto - 0.02f, -0.04f), 0.008f, Color.White.copy(alpha = 0.45f))
        }
    }

    /** La aceitera de lata, con su pico largo. */
    private fun DrawScope.aceitera(v: VistaRefugio, c: V3) {
        Trazo3.run {
            cilindro(v, c, c + V3(0f, 0.08f, 0f), 0.06f, lerp(C.ROJO, C.OXIDO, 0.35f), lados = 12)
            cilindro(v, c + V3(0f, 0.08f, 0f), c + V3(0f, 0.11f, 0f), 0.035f, lerp(C.ACERO, C.OXIDO, 0.3f), lados = 10)
            barra(c + V3(0f, 0.11f, 0f), c + V3(0.12f, 0.24f, -0.05f), 0.01f, v.c(c, K.NADA, C.LATON))
        }
    }

    /** Un filtro pequeño de cartucho, de pie: el cuerpo de papel plisado entre dos tapas de chapa. */
    private fun DrawScope.filtroPequeno(v: VistaRefugio, c: V3) {
        Trazo3.run {
            cilindro(v, c, c + V3(0f, 0.02f, 0f), 0.065f, C.ACERO, lados = 12)
            cilindro(v, c + V3(0f, 0.02f, 0f), c + V3(0f, 0.17f, 0f), 0.06f, lerp(C.PAPEL, C.BARRO, 0.3f), lados = 12)
            cilindro(v, c + V3(0f, 0.17f, 0f), c + V3(0f, 0.19f, 0f), 0.065f, C.ACERO, lados = 12)
        }
        for (k in 0..4) { val a = -0.6f + 0.3f * k; val p = c + V3(sin(a) * 0.061f, 0.02f, -cos(a) * 0.061f); Trazo3.run { barra(p, p + V3(0f, 0.15f, 0f), 0.006f, v.c(p, K.FRENTE, lerp(C.PAPEL, C.TINTA, 0.35f))) } }
    }

    /** Un manómetro desmontado, tumbado en la tapa, con su racor de latón: mirando arriba. */
    private fun DrawScope.manometroSuelto(v: VistaRefugio, c: V3) {
        Trazo3.run {
            cilindro(v, c + V3(0f, 0.005f, 0f), c + V3(0f, 0.045f, 0f), 0.09f, C.LATON, lados = 18)
            disco(v, c + V3(0f, 0.047f, 0f), K.ARRIBA, 0.075f, lerp(C.PAPEL, Color.White, 0.2f), lados = 18)
            barra(c + V3(0f, 0.05f, 0f), c + V3(0.04f, 0.05f, -0.04f), 0.008f, v.c(c, K.ARRIBA, C.TINTA))
            barra(c + V3(0.09f, 0.025f, 0f), c + V3(0.17f, 0.025f, 0.02f), 0.03f, v.c(c, K.NADA, C.LATON))
        }
    }

    /** Los alicates, tumbados. */
    private fun DrawScope.alicates(v: VistaRefugio, c: V3) {
        Trazo3.run {
            barra(c, c + V3(0.12f, 0.004f, 0.05f), 0.016f, v.c(c, K.ARRIBA, lerp(C.ROJO, C.TINTA, 0.2f)))
            barra(c + V3(0f, 0.004f, 0.025f), c + V3(0.12f, 0.004f, 0.05f), 0.016f, v.c(c, K.ARRIBA, lerp(C.ROJO, C.TINTA, 0.3f)))
            barra(c + V3(0.12f, 0.006f, 0.05f), c + V3(0.2f, 0.006f, 0.06f), 0.018f, v.c(c, K.ARRIBA, lerp(C.ACERO, Color.White, 0.2f)))
        }
    }

    /** Juntas de goma sueltas: aros negros. */
    private fun DrawScope.juntas(v: VistaRefugio, c: V3) {
        for ((k, d) in listOf(V3(0f, 0.004f, 0f), V3(0.08f, 0.004f, 0.03f), V3(0.04f, 0.012f, -0.01f)).withIndex()) Trazo3.run { aro(c + d, K.ARRIBA, 0.035f + 0.01f * k, 0.012f, v.c(c, K.ARRIBA, lerp(C.TINTA, C.CHAPA, 0.2f)), 12) }
    }

    /** Las latas de antes de la guerra, abiertas (el postre del año nuevo, la tarta de los fundadores): dos latas con su tapa doblada. */
    private fun DrawScope.latas(v: VistaRefugio, c: V3) {
        for ((k, d) in listOf(V3(0f, 0f, 0f), V3(0.13f, 0f, 0.05f)).withIndex()) {
            val q = c + d; val etiqueta = if (k == 0) lerp(C.ROJO, C.PAPEL, 0.15f) else lerp(C.AZUL, C.PAPEL, 0.2f)
            Trazo3.run {
                cilindro(v, q, q + V3(0f, 0.11f, 0f), 0.05f, etiqueta, lerp(C.ACERO, Color.White, 0.2f), lados = 12)
                aro(q + V3(0f, 0.11f, 0f), K.ARRIBA, 0.05f, 0.008f, v.c(q, K.ARRIBA, lerp(C.ACERO, Color.White, 0.4f)), 12)
                disco(v, q + V3(0f, 0.105f, 0f), K.ARRIBA, 0.045f, lerp(C.AMARILLO, C.NARANJA, 0.3f + 0.3f * k), lados = 12)
                cara(v, listOf(q + V3(-0.03f, 0.11f, 0.04f), q + V3(0.03f, 0.11f, 0.04f), q + V3(0.025f, 0.18f, 0.07f), q + V3(-0.025f, 0.18f, 0.07f)), K.FRENTE, lerp(C.ACERO, Color.White, 0.3f))
            }
        }
        Trazo3.run { barra(c + V3(-0.1f, 0.006f, -0.06f), c + V3(0.02f, 0.006f, -0.1f), 0.012f, v.c(c, K.ARRIBA, C.ACERO)) }
    }

    /** La flor de verdad que trajo Noa, en un frasco (seca, un mes después). */
    private fun DrawScope.florEnFrasco(v: VistaRefugio, c: V3, seca: Boolean) {
        Trazo3.run {
            cilindro(v, c, c + V3(0f, 0.13f, 0f), 0.04f, lerp(Color.White, C.PISCINA, 0.3f).copy(alpha = 0.6f), lados = 10)
            val tallo = c + V3(0.02f, 0.34f, -0.01f)
            barra(c + V3(0f, 0.1f, 0f), tallo, 0.007f, v.c(c, K.NADA, if (seca) lerp(C.ALGA, C.COBRE, 0.6f) else C.ALGA))
            for (k in 0 until 6) { val a = 2f * PI.toFloat() * k / 6; disco(v, tallo + V3(cos(a) * 0.028f, sin(a) * 0.016f, -0.005f), K.FRENTE, 0.022f, if (seca) lerp(C.COBRE, C.PAPEL, 0.3f) else lerp(C.AMARILLO, Color.White, 0.2f), lados = 8) }
            disco(v, tallo + V3(0f, 0f, -0.01f), K.FRENTE, 0.015f, C.NARANJA, lados = 8)
        }
    }

    /** Una flor de papel (la del sellado, la del luto), de [r] metros, tumbada. */
    fun DrawScope.florDePapel(v: VistaRefugio, c: V3, r: Float, color: Color = lerp(C.PAPEL, C.ROJO, 0.35f), normal: V3 = K.ARRIBA) {
        Trazo3.run {
            for (k in 0 until 5) { val a = 2f * PI.toFloat() * k / 5; disco(v, c + V3(cos(a) * r * 0.65f, 0f, sin(a) * r * 0.6f), normal, r * 0.6f, color, lados = 7) }
            disco(v, c + V3(0f, 0.004f, 0f), normal, r * 0.35f, C.AMARILLO, lados = 6)
        }
    }

    /** El cuaderno del turno: abierto, con sus rayas sin letras y el lápiz; o cerrado, con su tapa verde gastada. */
    private fun DrawScope.cuaderno(v: VistaRefugio, c: V3, abierto: Boolean) {
        val y = c.y + 0.014f
        if (!abierto) { Trazo3.run { caja(v, c.x - 0.15f, c.x + 0.15f, c.y, c.y + 0.035f, c.z - 0.12f, c.z + 0.12f, lerp(C.VERDE, C.TINTA, 0.35f), lerp(C.VERDE, C.PAPEL, 0.15f)) }; return }
        Trazo3.run { caja(v, c.x - 0.25f, c.x + 0.25f, c.y, c.y + 0.01f, c.z - 0.16f, c.z + 0.16f, lerp(C.VERDE, C.TINTA, 0.3f)) }
        for (lado in listOf(-1f, 1f)) {
            val xa = c.x + (if (lado < 0f) -0.23f else 0f); val xb = c.x + (if (lado < 0f) 0f else 0.23f)
            Trazo3.run { cara(v, listOf(V3(xa, y, c.z - 0.15f), V3(xb, y + 0.005f * -lado, c.z - 0.15f), V3(xb, y + 0.005f * -lado, c.z + 0.15f), V3(xa, y, c.z + 0.15f)), K.ARRIBA, lerp(C.PAPEL, Color.White, 0.25f)) }
            for (k in 1..7) { val z = c.z - 0.15f + 0.3f * k / 8f; Trazo3.run { barra(V3(xa + 0.02f, y + 0.002f, z), V3(xb - 0.02f, y + 0.002f, z), 0.005f, v.c(c, K.ARRIBA, lerp(C.AZUL, C.PAPEL, 0.4f)).copy(alpha = 0.7f)) } }
            for (k in 1..5) { val z = c.z - 0.15f + 0.3f * k / 8f - 0.012f; val largo = 0.07f + 0.11f * ((k * 7 + (if (lado < 0f) 3 else 1)) % 5) / 5f; Trazo3.run { barra(V3(xa + 0.025f, y + 0.003f, z), V3(xa + 0.025f + largo, y + 0.003f, z), 0.006f, v.c(c, K.ARRIBA, C.TINTA).copy(alpha = 0.6f)) } }
        }
        Trazo3.run { barra(V3(c.x + 0.06f, y + 0.012f, c.z + 0.06f), V3(c.x + 0.26f, y + 0.012f, c.z - 0.07f), 0.016f, v.c(c, K.ARRIBA, C.AMARILLO)); barra(V3(c.x + 0.26f, y + 0.012f, c.z - 0.07f), V3(c.x + 0.29f, y + 0.012f, c.z - 0.09f), 0.012f, v.c(c, K.ARRIBA, C.TINTA)) }
    }

    /** La fiambrera de lata: cerrada, o abierta con su tapa al lado (la hora de comer). */
    private fun DrawScope.fiambrera(v: VistaRefugio, c: V3, abierta: Boolean) {
        Trazo3.run { caja(v, c.x - 0.12f, c.x + 0.12f, c.y, c.y + 0.08f, c.z - 0.08f, c.z + 0.08f, C.ACERO, if (abierta) lerp(C.ALGA, C.TINTA, 0.35f) else lerp(C.ACERO, Color.White, 0.2f)) }
        if (abierta) Trazo3.run { caja(v, c.x + 0.14f, c.x + 0.36f, c.y, c.y + 0.015f, c.z - 0.07f, c.z + 0.09f, lerp(C.ACERO, Color.White, 0.15f)); barra(c + V3(0.02f, 0.082f, 0f), c + V3(0.22f, 0.03f, -0.1f), 0.01f, v.c(c, K.NADA, C.ACERO)) }
    }

    /** Las gafas de cartón y celofán del sol falso. */
    private fun DrawScope.gafas(v: VistaRefugio, c: V3) {
        Trazo3.run {
            for (dx in listOf(-0.05f, 0.05f)) { disco(v, c + V3(dx, 0.006f, 0f), K.ARRIBA, 0.04f, lerp(C.PAPEL, C.TINTA, 0.1f), lados = 10); disco(v, c + V3(dx, 0.008f, 0f), K.ARRIBA, 0.03f, lerp(C.NARANJA, C.AMARILLO, 0.4f), lados = 10) }
            barra(c + V3(-0.09f, 0.006f, 0f), c + V3(-0.11f, 0.006f, 0.13f), 0.009f, v.c(c, K.ARRIBA, C.PAPEL))
        }
    }

    /** El jabón amarillo de la cuarentena, grande, en su platito de lata. */
    private fun DrawScope.jabon(v: VistaRefugio, c: V3) {
        Trazo3.run {
            cilindro(v, c, c + V3(0f, 0.012f, 0f), 0.09f, lerp(C.ACERO, Color.White, 0.2f), lados = 14)
            caja(v, c.x - 0.07f, c.x + 0.07f, c.y + 0.012f, c.y + 0.06f, c.z - 0.04f, c.z + 0.04f, lerp(C.AMARILLO, C.PAPEL, 0.2f), lerp(C.AMARILLO, Color.White, 0.3f))
        }
    }

    /** La cuarentena: ella lleva las bombas sola y las herramientas quedan por todas partes (en la tapa y en el suelo). */
    private fun DrawScope.herramientas(v: VistaRefugio, y: Float) {
        Trazo3.run {
            barra(V3(-1.75f, y + 0.01f, 2.62f), V3(-1.45f, y + 0.01f, 2.6f), 0.022f, v.c(V3(-1.6f, y, 2.6f), K.ARRIBA, C.ACERO))
            disco(v, V3(-1.78f, y + 0.012f, 2.62f), K.ARRIBA, 0.035f, C.ACERO, lados = 8)
            barra(V3(-0.6f, y + 0.012f, 3.0f), V3(-0.35f, y + 0.012f, 2.9f), 0.02f, v.c(V3(-0.5f, y, 2.9f), K.ARRIBA, lerp(C.AMARILLO, C.TINTA, 0.3f)))
            barra(V3(-0.35f, y + 0.012f, 2.9f), V3(-0.25f, y + 0.012f, 2.86f), 0.008f, v.c(V3(-0.5f, y, 2.9f), K.ARRIBA, C.ACERO))
        }
    }

    /**
     * Un trapo colgado del canto, de verdad: una tela con peso, que cae en dos o tres pliegues, el borde de abajo deshilachado
     * y una mancha de aceite; cada uno distinto ([k]).
     */
    fun DrawScope.trapo(v: VistaRefugio, c: V3, k: Int) {
        val tela = listOf(lerp(C.PAPEL, C.ROCA, 0.35f), lerp(C.ROJO, C.PAPEL, 0.45f), lerp(C.AZUL, C.PAPEL, 0.4f))[k % 3]
        val ancho = 0.2f + 0.04f * (k % 2); val cae = 0.3f + 0.05f * k
        val n = K.unidad(V3(0.1f, 0f, -1f))
        val borde = (0..6).map { i -> val t = i / 6f; c + V3(-ancho / 2f + ancho * t + 0.02f * sin(t * 9f), -cae - 0.04f * ((i * 5 + k) % 3) / 2f, -0.015f - 0.01f * (i % 2)) }
        val pts = listOf(c + V3(-ancho / 2f, 0f, 0f), c + V3(ancho / 2f, 0f, 0f)) + borde.reversed()
        Trazo3.run { caraDegradada(v, pts, n, tela, c, c + V3(0f, -cae, 0f)) }
        for (j in 1..2) { val x = -ancho / 2f + ancho * j / 3f; Trazo3.run { barra(c + V3(x, -0.02f, -0.016f), c + V3(x + 0.015f, -cae + 0.03f, -0.02f), 0.012f, v.c(c, n, lerp(tela, C.TINTA, 0.3f)).copy(alpha = 0.7f)) } }
        Trazo3.run { disco(v, c + V3(0.02f, -cae * 0.55f, -0.02f), n, 0.04f, lerp(tela, C.TINTA, 0.45f), lados = 8) }
        Trazo3.run { barra(c + V3(-ancho / 2f, 0.004f, -0.004f), c + V3(ancho / 2f, 0.004f, -0.004f), 0.018f, v.c(c, K.ARRIBA, lerp(tela, Color.White, 0.2f))) }
    }

    /** La guirnalda de flores de papel colgada del canto de la bancada, de [a] a [b]. */
    private fun DrawScope.guirnalda(v: VistaRefugio, a: V3, b: V3) {
        Trazo3.run { comba(a, b, 0.14f, 0.007f, v.c(K.medio(a, b), K.NADA, C.TINTA)) }
        val colores = listOf(C.ROJO, C.AMARILLO, C.AZUL, lerp(C.PAPEL, C.NARANJA, 0.4f), C.ALGA)
        for (k in 1..8) {
            val t = k / 9f; val q = V3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t - 0.14f * 4f * t * (1f - t), a.z - 0.01f)
            Trazo3.run { disco(v, q, K.FRENTE, 0.045f, colores[k % colores.size], lados = 7); disco(v, q + K.FRENTE * 0.005f, K.FRENTE, 0.015f, C.PAPEL, lados = 6) }
        }
    }

    /** El taburete de hierro con el asiento de madera gastada y un trapo encima. */
    fun DrawScope.taburete(v: VistaRefugio, c: V3) {
        val alto = 0.62f
        for (k in 0..2) { val a = 2f * PI.toFloat() * k / 3 + 0.4f; Trazo3.run { barra(c + V3(cos(a) * 0.17f, 0f, sin(a) * 0.17f), c + V3(cos(a) * 0.1f, alto, sin(a) * 0.1f), 0.028f, v.c(c + V3(0f, 0.3f, 0f), K.NADA, C.HIERRO)) } }
        Trazo3.run { aro(c + V3(0f, 0.22f, 0f), K.ARRIBA, 0.145f, 0.018f, v.c(c, K.NADA, C.HIERRO), 14) }
        Trazo3.run { cilindro(v, c + V3(0f, alto, 0f), c + V3(0f, alto + 0.04f, 0f), 0.16f, lerp(lerp(C.COBRE, C.AMARILLO, 0.2f), C.TINTA, 0.25f), lados = 16) }
        trapo(v, c + V3(0.08f, alto + 0.04f, -0.12f), 2)
    }
}
