package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.ARRIBA
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.DOS_PI
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.FRENTE
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.angOjo
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.aro
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.caja
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.cara
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.cilindroEje
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.contacto
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.curva
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.extruido
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.palo
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.sombras
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.torneado
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.trazo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Los objetos grandes de la doctora ([ObjetosLaboratorio.GRANDES]): la bata, echada sobre el apoyo y cayendo hasta el
 * suelo; los guantes amarillos, colgados del canto del apoyo con sus pinzas «como orejas»; y la maceta de barro con su
 * grama, de pie en el suelo. Y el microscopio de latón, el más grande de los chicos («pesado como un niño»). Cada uno se
 * arma en metros y se pinta con la cámara y la luz de su [SitioObjeto]; los grandes pintan su propia sombra en el suelo.
 */
internal object GrandesLab {
    private val C = ColorLaboratorio
    private val T = TonosObj

    /** El canto donde se apoya un grande: el de [s] o, si el cuadro no lo da, uno a la altura de un taburete, algo más al fondo. */
    private fun apoyo(s: SitioObjeto): V3 = s.apoyo ?: (s.o + V3(0f, 0.62f, 0f) + s.fondo * 0.06f)

    private fun silueta(f: MarcoObj, ps: List<V3>): Path = poligono(envolvente(ps.map { f.p(it) }))

    /** Una dirección [n] de algo girado [g] radianes alrededor de la vertical. */
    private fun gira(n: V3, g: Float) = V3(n.x * cos(g) - n.z * sin(g), n.y, n.x * sin(g) + n.z * cos(g))

    // =================================================================
    //  Los guantes
    // =================================================================

    /**
     * Los guantes de goma fina, amarillos, colgados del canto del apoyo con dos pinzas de madera, los dedos abajo, «como
     * orejas»; uno con el remiendo en el índice. Se están secando: gotean en el suelo.
     */
    fun guantes(s: SitioObjeto, pie: Offset): Cosa {
        val ap = apoyo(s); val am = 1.4f; val f = MarcoObj(s, -0.15f, am, ap)
        val suelo = (s.o.y - ap.y) / am
        val guantes = listOf(Triple(-0.064f, 0.3f, -1f), Triple(0.062f, -0.22f, 1f))
        val puntos = guantes.flatMap { (x0, g, _) -> listOf(-0.05f to 0.012f, 0.05f to 0.012f, 0.05f to -0.2f, -0.05f to -0.2f, 0f to -0.31f).map { (gx, gy) -> enGuante(x0, g, gx, gy) } }
        val orden = guantes.sortedByDescending { (x0, g, _) -> (f.w(enGuante(x0, g, 0f, -0.15f)) - s.ojo).largo() }
        return Cosa(silueta(f, puntos + listOf(V3(-0.07f, 0.04f, -0.02f), V3(0.07f, 0.04f, -0.02f))), pie) {
            sombras(f, puntos, plano = suelo)
            // Lo que gotea: unas gotas y la mancha mojada en el suelo, bajo los dedos.
            for ((x0, _, _) in guantes) {
                val c = V3(x0, suelo + 0.001f, -0.03f)
                drawPath(f.forma(aro(c.x, c.y, c.z, 0.035f, 0.022f, 16)), lerp(C.SOMBRA, C.PISO, 0.4f), alpha = 0.3f, blendMode = BlendMode.Multiply)
                for (i in 0..2) { val q = c + V3(-0.012f + i * 0.012f, 0f, -0.006f * i); val o = f.p(q)
                    drawOval(f.c(q, lerp(T.AGUA, Color.White, 0.4f), ARRIBA).copy(alpha = 0.75f), o - Offset(0.004f * f.k, 0.0016f * f.k), Size(0.008f * f.k, 0.0032f * f.k)) }
            }
            for ((x0, g, pulgar) in orden) guante(f, x0, g, pulgar)
        }
    }

    /** Un punto del guante colgado en [x0], girado [g]: [gx] a lo ancho, [gy] de alto (desde la boca de la pinza). */
    private fun enGuante(x0: Float, g: Float, gx: Float, gy: Float, gz: Float = 0f) = V3(x0 + gx * cos(g) - gz * sin(g), gy, -0.016f + gx * sin(g) + gz * cos(g))

    private fun DrawScope.guante(f: MarcoObj, x0: Float, g: Float, pulgar: Float) {
        fun q(gx: Float, gy: Float, gz: Float = 0f) = enGuante(x0, g, gx, gy, gz)
        val goma = C.AMARILLO
        val cI = f.c(q(-0.04f, -0.1f), goma, gira(V3(-0.65f, 0.05f, -0.75f), g)); val cM = f.c(q(0f, -0.1f), goma, gira(V3(0f, 0.05f, -1f), g))
        val cD = f.c(q(0.04f, -0.1f), goma, gira(V3(0.7f, -0.05f, -0.7f), g))
        fun tono(gx: Float) = if (gx < 0f) lerp(cM, cI, (-gx / 0.05f).coerceIn(0f, 1f)) else lerp(cM, cD, (gx / 0.05f).coerceIn(0f, 1f))
        // El pulgar, de lado (detrás de la palma donde nace).
        trazo(f, listOf(q(pulgar * 0.04f, -0.125f, 0.004f), q(pulgar * 0.064f, -0.17f, -0.002f), q(pulgar * 0.074f, -0.215f, -0.004f)), lerp(tono(pulgar * 0.05f), C.BARRO, 0.15f), 0.022f)
        // El puño y la palma, con el color de cada lado.
        val cuerpo = listOf(q(-0.05f, 0.012f), q(0.05f, 0.012f), q(0.049f, -0.06f), q(0.047f, -0.115f), q(0.046f, -0.19f), q(0.03f, -0.199f), q(-0.03f, -0.199f), q(-0.046f, -0.19f), q(-0.047f, -0.115f), q(-0.049f, -0.06f))
        val a = f.p(q(-0.05f, -0.1f)); val b = f.p(q(0.05f, -0.1f))
        drawPath(f.forma(cuerpo), Brush.linearGradient(0f to cI, 0.5f to cM, 1f to cD, start = a, end = b))
        // Los cuatro dedos, que cuelgan algo abiertos; el índice, junto al pulgar, con su remiendo.
        val largos = listOf(0.085f, 0.105f, 0.1f, 0.078f)
        for (i in 0 until 4) { val gx = -0.034f + i * 0.0227f; val fin = q(gx * 1.12f, -0.19f - largos[i])
            trazo(f, listOf(q(gx, -0.18f), q(gx * 1.05f, -0.19f - largos[i] * 0.5f), fin), tono(gx), 0.02f)
            trazo(f, listOf(q(gx - 0.004f, -0.2f, -0.004f), q(gx * 1.05f - 0.004f, -0.19f - largos[i] * 0.7f, -0.004f)), Color.White, 0.003f, 0.22f) }
        val iIndice = if (pulgar > 0f) 3 else 0; val gi = -0.034f + iIndice * 0.0227f; val yr = -0.19f - largos[iIndice] * 0.55f
        val parche = listOf(q(gi - 0.011f, yr + 0.012f, -0.006f), q(gi + 0.011f, yr + 0.013f, -0.006f), q(gi + 0.012f, yr - 0.012f, -0.006f), q(gi - 0.01f, yr - 0.013f, -0.006f))
        cara(f, parche, T.REMIENDO, gira(V3(0f, 0f, -1f), g))
        for (j in 0..5) { val e = j / 5f; drawCircle(lerp(T.REMIENDO, C.NEGRO, 0.45f), (0.0012f * f.k).coerceAtLeast(0.3f), f.p(q(gi - 0.011f + 0.022f * e, yr + 0.0125f, -0.007f))) }
        // El canto vuelto del puño, el brillo de la goma y la pinza de madera con su muelle.
        trazo(f, listOf(q(-0.05f, -0.004f, -0.003f), q(0.05f, -0.004f, -0.003f)), lerp(goma, C.BARRO, 0.3f), 0.011f)
        trazo(f, listOf(q(-0.022f, -0.03f, -0.006f), q(-0.024f, -0.17f, -0.006f)), Color.White, 0.005f, 0.3f)
        caja(f, x0 - 0.007f, x0 + 0.007f, -0.038f, 0.038f, -0.032f, -0.019f, T.PINZA)
        trazo(f, aro(x0, 0.004f, -0.033f, 0.006f, 0.002f, 10), f.c(V3(x0, 0f, -0.033f), C.ACERO, FRENTE), 0.0018f)
    }

    // =================================================================
    //  La bata
    // =================================================================

    /**
     * La bata blanca de cuello verde, echada sobre el apoyo y cayendo hasta el suelo, nunca con forma de cuerpo: encima, el
     * bulto doblado con el cuello; delante, el faldón con sus pliegues, el canto cian, los botones y los bolsillos «dados de
     * sí por las semillas» (dos se han caído); a un lado, la manga con la quemadura de ácido.
     */
    fun bata(s: SitioObjeto, pie: Offset): Cosa {
        val ap = apoyo(s); val am = 1.1f; val f = MarcoObj(s, -0.25f, am, ap)
        val suelo = (s.o.y - ap.y) / am
        val bulto = listOf(-0.17f to -0.01f, 0.16f to -0.014f, 0.15f to 0.09f, 0.08f to 0.15f, -0.1f to 0.16f, -0.165f to 0.08f)
        val charco = listOf(-0.235f to -0.05f, -0.02f to -0.06f, 0.0f to -0.11f, -0.09f to -0.15f, -0.2f to -0.14f, -0.25f to -0.1f)
        val esqueleto = bulto.map { V3(it.first, 0.035f, it.second) } + charco.map { V3(it.first, suelo + 0.02f, it.second) } +
            listOf(V3(0.15f, -0.42f, -0.06f), V3(0.25f, -0.56f, -0.08f), V3(0.17f, -0.57f, -0.08f))
        val tela = lerp(C.TELA, Color.White, 0.5f)
        return Cosa(silueta(f, esqueleto + listOf(V3(-0.225f, -0.36f, 0f))), pie) {
            sombras(f, esqueleto, plano = suelo)
            // La otra manga, que cuelga por detrás a la izquierda.
            cara(f, listOf(V3(-0.155f, 0.02f, 0.01f), V3(-0.2f, 0f, 0.01f), V3(-0.226f, -0.36f, -0.01f), V3(-0.18f, -0.37f, -0.01f)), lerp(tela, C.SOMBRA, 0.1f), V3(-0.8f, 0f, -0.5f))
            // Lo que llega al suelo por la izquierda: el bajo, arrugado.
            extruido(f, charco, suelo, suelo + 0.022f, lerp(tela, C.SOMBRA, 0.12f), tela)
            for (i in 0..2) trazo(f, listOf(V3(-0.21f + i * 0.07f, suelo + 0.0225f, -0.065f), V3(-0.19f + i * 0.07f, suelo + 0.0225f, -0.135f)), lerp(tela, C.SOMBRA, 0.45f), 0.003f, 0.45f)
            // El bulto doblado encima del apoyo y el doblez sobre el canto.
            extruido(f, bulto, 0f, 0.035f, lerp(tela, C.SOMBRA, 0.08f), tela)
            trazo(f, listOf(V3(-0.15f, 0.0355f, 0.05f), V3(0.14f, 0.0355f, 0.04f)), lerp(tela, C.SOMBRA, 0.4f), 0.003f, 0.5f)
            cara(f, listOf(V3(-0.17f, 0.035f, -0.01f), V3(0.16f, 0.035f, -0.014f), V3(0.165f, -0.03f, -0.046f), V3(-0.175f, -0.03f, -0.044f)), tela, V3(0f, 0.55f, -0.85f))
            faldon(f, tela, suelo)
            cuello(f)
            manga(f, tela)
            // Dos semillas que se cayeron del bolsillo.
            for (i in 0..1) { val q = V3(0.04f + i * 0.03f, suelo + 0.001f, -0.12f - i * 0.02f); val o = f.p(q)
                drawOval(f.c(q, T.SEMILLA, ARRIBA), o - Offset(0.004f * f.k, 0.0025f * f.k), Size(0.008f * f.k, 0.005f * f.k)) }
        }
    }

    /**
     * El faldón que cuelga del canto, más largo por la izquierda (llega al suelo) que por la derecha: franjas con los pliegues,
     * el canto cian, tres botones y los dos bolsillos dados de sí.
     */
    private fun DrawScope.faldon(f: MarcoObj, tela: Color, suelo: Float) {
        val n = 8; val pliegue = listOf(-0.45f, 0.15f, 0.5f, -0.25f, 0.35f, -0.5f, 0.2f, 0.45f)
        fun abajoY(x: Float) = (suelo + 0.02f) + ((x + 0.2f) / 0.35f).coerceIn(0f, 1f) * (-0.42f - suelo - 0.02f)
        fun arriba(i: Int) = V3(-0.175f + 0.31f * i / n, -0.03f, -0.046f)
        fun abajo(i: Int): V3 { val x = -0.2f + 0.35f * i / n; return V3(x, abajoY(x) + 0.01f * sin(i * 2.1f), -0.062f) }
        drawPath(f.forma((0..n).map { arriba(it) } + (n downTo 0).map { abajo(it) }), f.c(V3(0f, -0.25f, -0.055f), tela, FRENTE))
        for (i in 0 until n) {
            cara(f, listOf(arriba(i), arriba(i + 1), abajo(i + 1), abajo(i)), tela, V3(pliegue[i], 0f, -1f))
            if (i < n - 1 && pliegue[i] > pliegue[i + 1]) trazo(f, listOf(arriba(i + 1), abajo(i + 1)), lerp(tela, C.SOMBRA, 0.5f), 0.0025f, 0.45f)
        }
        trazo(f, (0..n).map { abajo(it) }, lerp(tela, C.SOMBRA, 0.35f), 0.006f, 0.6f)
        // El canto cian por delante y los botones.
        trazo(f, listOf(V3(-0.055f, -0.03f, -0.0465f), V3(-0.066f, abajoY(-0.066f), -0.0615f)), f.c(V3(-0.06f, -0.2f, -0.05f), C.CIAN, FRENTE), 0.008f)
        for (y in listOf(-0.12f, -0.24f, -0.36f)) { val q = V3(-0.043f - (y + 0.03f) * 0.02f, y, -0.053f)
            drawCircle(f.c(q, lerp(C.TELA, C.ACERO, 0.45f), FRENTE), (0.006f * f.k).coerceAtLeast(0.5f), f.p(q)) }
        // Los bolsillos: la boca abierta, la costura y, en uno, las semillas que asoman.
        for ((i, b) in listOf(Triple(0.0f, -0.22f, -0.33f), Triple(-0.17f, -0.38f, -0.49f)).withIndex()) {
            val (x0, y0, y1) = b; val z = -0.058f
            val bolsa = listOf(V3(x0, y0, z - 0.004f), V3(x0 + 0.1f, y0, z - 0.004f), V3(x0 + 0.105f, y1, z - 0.012f), V3(x0 + 0.005f, y1, z - 0.012f))
            cara(f, bolsa, tela, V3(0f, -0.25f, -1f))
            cara(f, listOf(bolsa[0], bolsa[1], V3(x0 + 0.095f, y0 - 0.02f, z - 0.012f), V3(x0 + 0.005f, y0 - 0.02f, z - 0.012f)), lerp(tela, C.SOMBRA, 0.5f), FRENTE)
            trazo(f, listOf(bolsa[0], bolsa[1]), lerp(tela, Color.White, 0.5f), 0.003f, 0.8f)
            trazo(f, listOf(bolsa[1], bolsa[2], bolsa[3], bolsa[0]), lerp(tela, C.SOMBRA, 0.45f), 0.0018f, 0.6f)
            if (i == 0) for (j in 0..2) { val q = V3(x0 + 0.028f + j * 0.022f, y0 - 0.009f, z - 0.013f)
                drawCircle(f.c(q, T.SEMILLA, FRENTE), (0.0042f * f.k).coerceAtLeast(0.4f), f.p(q)) }
        }
    }

    /** El cuello verde de Botánica, que cae sobre el doblez del canto con su solapa. */
    private fun DrawScope.cuello(f: MarcoObj) {
        val ps = listOf(V3(-0.172f, 0.036f, -0.011f), V3(-0.03f, 0.036f, -0.0125f), V3(-0.04f, -0.03f, -0.047f), V3(-0.072f, -0.11f, -0.05f), V3(-0.118f, -0.045f, -0.048f), V3(-0.176f, -0.08f, -0.047f))
        cara(f, ps, C.CUELLO, V3(0f, 0.35f, -1f))
        trazo(f, listOf(ps[0], ps[1]), lerp(C.CUELLO, C.TELA, 0.45f), 0.003f, 0.8f)
        trazo(f, listOf(ps[2], ps[3], ps[4]), lerp(C.CUELLO, C.NEGRO, 0.35f), 0.0022f, 0.6f)
    }

    /** La manga que cuelga a la derecha, más abajo que el faldón, con el puño y la quemadura de ácido (una mancha parda de borde comido, con su agujero). */
    private fun DrawScope.manga(f: MarcoObj, tela: Color) {
        val tA = V3(0.125f, -0.02f, -0.058f); val tB = V3(0.2f, -0.03f, -0.054f); val bB = V3(0.252f, -0.56f, -0.08f); val bA = V3(0.168f, -0.57f, -0.086f)
        for (j in 0 until 3) { val e0 = j / 3f; val e1 = (j + 1) / 3f
            cara(f, listOf(tA + (tB - tA) * e0, tA + (tB - tA) * e1, bA + (bB - bA) * e1, bA + (bB - bA) * e0), tela, V3(-0.6f + 0.6f * j, 0f, -1f)) }
        trazo(f, listOf(tA, bA), lerp(tela, C.SOMBRA, 0.45f), 0.0025f, 0.55f)
        cara(f, listOf(bA + (tA - bA) * 0.08f, bB + (tB - bB) * 0.08f, bB, bA), lerp(tela, C.SOMBRA, 0.12f), V3(0.2f, -0.2f, -1f))
        trazo(f, listOf(bA + (tA - bA) * 0.08f, bB + (tB - bB) * 0.08f), lerp(tela, C.SOMBRA, 0.4f), 0.002f, 0.6f)
        val c = V3(0.205f, -0.36f, -0.075f)
        val az = Azar(Semilla.de("laboratorio", "obj", "quemadura"))
        val borde = (0 until 14).map { i -> val a = i * DOS_PI / 14; val r = 0.022f * (0.7f + 0.5f * az.real()); c + V3(r * cos(a), r * 1.25f * sin(a), -0.001f) }
        drawPath(f.forma((0 until 14).map { i -> val a = i * DOS_PI / 14; c + V3(0.034f * cos(a), 0.042f * sin(a), -0.0005f) }), f.c(c, lerp(tela, C.AMARILLO, 0.5f), FRENTE), alpha = 0.35f)
        drawPath(f.forma(borde), f.c(c, T.QUEMADURA, FRENTE))
        drawPath(f.forma(borde.map { c + (it - c) * 0.55f }), f.c(c, lerp(T.QUEMADURA, C.NEGRO, 0.35f), FRENTE))
        drawPath(f.forma(borde.map { c + (it - c) * 0.25f }), lerp(C.NEGRO, C.SOMBRA, 0.3f))
    }

    // =================================================================
    //  La maceta
    // =================================================================

    /**
     * La maceta de barro cocido, de pie en su plato, con la grama que ella siembra (hojas finas que se arquean y espigas en
     * dedos) en tierra ya limpia y oscura. «Una que ya ha sido maceta cuatro veces»: cuatro cercos de sal en el barro y un
     * desconchón en el borde.
     */
    fun maceta(s: SitioObjeto, pie: Offset): Cosa {
        val f = MarcoObj(s, 0f, 1.15f)
        val yBorde = 0.248f; val rB = 0.15f
        val hojas = hojasDeGrama(yBorde - 0.012f)
        val esqueleto = aro(0f, 0f, 0f, 0.14f, n = 10) + aro(0f, yBorde, 0f, rB, n = 10) + hojas.map { it.last() }
        return Cosa(silueta(f, esqueleto), pie) {
            contacto(f, 0f, 0f, 0.15f, 0.15f, alfa = 0.45f)
            sombras(f, esqueleto)
            // El plato, con el agua del riego.
            torneado(f, 0f, 0f, listOf(0f to 0.122f, 0.022f to 0.142f), lerp(C.BARRO, C.TIERRA, 0.15f), 0.1f)
            drawPath(f.forma(aro(0f, 0.022f, 0f, 0.142f, n = 30)), f.c(V3(0f, 0.022f, 0f), lerp(C.BARRO, C.NEGRO, 0.35f), ARRIBA))
            // El cuerpo y el borde, con los cercos de sal, las motas y el desconchón.
            val ae = angOjo(f, V3(0f, 0.12f, 0f))
            val cuerpo = torneado(f, 0f, 0f, listOf(0.008f to 0.1f, 0.03f to 0.104f, 0.12f to 0.12f, 0.205f to 0.135f), C.BARRO, 0.1f)
            clipPath(cuerpo) {
                val az = Azar(Semilla.de("laboratorio", "obj", "maceta"))
                for ((i, h) in listOf(0.048f, 0.088f, 0.128f, 0.168f).withIndex()) {
                    val r = 0.1f + 0.035f * (h - 0.008f) / 0.197f
                    val linea = (0..20).map { j -> val a = ae - 1.45f + 2.9f * j / 20; V3(r * cos(a), h + 0.004f * sin(j * 1.3f + i), r * sin(a)) }
                    val sal = f.c(V3(r * cos(ae), h, r * sin(ae)), T.SALITRE, V3(cos(ae), 0f, sin(ae)))
                    trazo(f, linea, sal, 0.0045f, 0.4f + 0.12f * i)
                    trazo(f, linea.map { it + V3(0f, -0.007f, 0f) }, sal, 0.0022f, 0.2f)
                }
                repeat(26) { val a = ae + az.entre(-1.4f, 1.4f); val y = az.entre(0.015f, 0.2f); val r = 0.1f + 0.035f * y / 0.2f
                    drawCircle(lerp(C.BARRO, C.NEGRO, 0.35f).copy(alpha = 0.45f), (0.002f * f.k).coerceAtLeast(0.3f), f.p(V3(r * cos(a), y, r * sin(a)))) }
                trazo(f, aro(0f, 0.012f, 0f, 0.101f, n = 14, a0 = ae - 1.4f, a1 = ae + 1.4f), lerp(C.GRAMA, C.TIERRA, 0.5f), 0.008f, 0.35f)
            }
            torneado(f, 0f, 0f, listOf(0.203f to 0.146f, yBorde to rB), lerp(C.BARRO, C.TELA, 0.08f), 0.12f)
            val ad = ae + 0.65f
            cara(f, listOf(V3(rB * cos(ad - 0.13f), yBorde, rB * sin(ad - 0.13f)), V3(rB * cos(ad + 0.12f), yBorde, rB * sin(ad + 0.12f)), V3(rB * 0.99f * cos(ad + 0.07f), yBorde - 0.017f, rB * 0.99f * sin(ad + 0.07f)), V3(rB * cos(ad - 0.06f), yBorde - 0.012f, rB * sin(ad - 0.06f))),
                lerp(C.BARRO, C.TELA, 0.35f), V3(cos(ad), 0.3f, sin(ad)))
            // La boca: el canto de arriba, la pared de dentro y la tierra limpia, oscura, con algún terrón gris.
            drawPath(f.forma(aro(0f, yBorde, 0f, rB, n = 32)), f.c(V3(0f, yBorde, 0f), lerp(C.BARRO, C.TELA, 0.12f), ARRIBA))
            val boca = f.forma(aro(0f, yBorde, 0f, 0.128f, n = 32))
            clipPath(boca) {
                drawPath(boca, f.c(V3(0f, yBorde - 0.01f, 0f), lerp(C.BARRO, C.NEGRO, 0.45f), V3(-cos(ae), 0.2f, -sin(ae))))
                drawPath(f.forma(aro(-cos(ae) * 0.006f, yBorde - 0.02f, -sin(ae) * 0.006f, 0.128f, n = 32)), f.c(V3(0f, yBorde - 0.02f, 0f), C.TIERRA_LIMPIA, ARRIBA))
                val az = Azar(Semilla.de("laboratorio", "obj", "terrones"))
                repeat(9) { val a = az.entre(0f, DOS_PI); val r = 0.11f * sqrt(az.real()); val q = V3(r * cos(a), yBorde - 0.019f, r * sin(a))
                    drawCircle(f.c(q, if (it % 3 == 0) C.TIERRA else lerp(C.TIERRA_LIMPIA, C.NEGRO, 0.3f), ARRIBA), (0.006f * f.k).coerceAtLeast(0.4f), f.p(q)) }
            }
            // La grama: las hojas de atrás primero.
            for (h in hojas.sortedByDescending { (f.w(it.first()) - s.ojo).largo() }) hoja(f, h)
        }
    }

    /** Las hojas de grama y sus espigas, como curvas de la maceta (de la base a la punta), siempre las mismas. */
    private fun hojasDeGrama(y0: Float): List<List<V3>> {
        val az = Azar(Semilla.de("laboratorio", "obj", "grama"))
        val hojas = ArrayList<List<V3>>()
        repeat(54) {
            val ang = az.entre(0f, DOS_PI); val r = 0.11f * sqrt(az.real()); val dir = V3(cos(ang + az.entre(-0.5f, 0.5f)), 0f, sin(ang + az.entre(-0.5f, 0.5f)))
            val b = V3(r * cos(ang), y0, r * sin(ang)); val h = az.entre(0.12f, 0.31f); val ladeo = az.entre(0.25f, 0.95f)
            hojas += curva(b, b + V3(0f, h * 0.55f, 0f), b + dir * (ladeo * h * 0.42f) + V3(0f, h * 0.95f, 0f), b + dir * (ladeo * h * 0.75f) + V3(0f, h * (1f - ladeo * 0.35f), 0f), 10)
        }
        // Las espigas, en dedos, como las de la grama de los caminos.
        repeat(6) {
            val ang = az.entre(0f, DOS_PI); val r = 0.08f * az.real(); val b = V3(r * cos(ang), y0, r * sin(ang)); val h = az.entre(0.33f, 0.42f)
            val ladeo = V3(cos(ang), 0f, sin(ang)) * az.entre(0.02f, 0.07f)
            hojas += curva(b, b + V3(0f, h * 0.4f, 0f), b + ladeo + V3(0f, h * 0.8f, 0f), b + ladeo * 1.5f + V3(0f, h, 0f), 8)
        }
        return hojas
    }

    /** Una hoja de grama (o una espiga, si es de las altas y rectas): gruesa abajo, fina arriba, con el lado de luz del sitio. */
    private fun DrawScope.hoja(f: MarcoObj, h: List<V3>) {
        val b = h.first(); val p = h.last(); val alto = p.y - b.y
        val dir = V3(p.x - b.x, 0f, p.z - b.z).let { if (it.largo() < 0.001f) V3(1f, 0f, 0f) else it.unidad() }
        val espiga = h.size == 9
        val verde = if (espiga) lerp(C.GRAMA, C.PAJA, 0.45f) else lerp(C.GRAMA, C.HOJA_OSC, ((b.x * 37f + b.z * 53f) % 1f + 1f) % 1f * 0.45f)
        val col = f.c(h[h.size / 2], verde, V3(dir.x * 0.7f, 0.6f, dir.z * 0.7f))
        if (espiga) {
            trazo(f, h, col, 0.0022f)
            for (j in 0 until 4) { val a = j * 1.6f; trazo(f, listOf(p, p + V3(0.018f * cos(a), -0.012f, 0.018f * sin(a)), p + V3(0.03f * cos(a + 0.2f), -0.03f, 0.03f * sin(a + 0.2f))), lerp(col, C.PAJA, 0.3f), 0.0028f) }
            return
        }
        trazo(f, h.subList(0, 7), col, 0.0058f)
        trazo(f, h.subList(6, h.size), lerp(col, Color.White, 0.08f), 0.0034f)
        if (alto > 0.2f) trazo(f, h.subList(2, 6).map { it + V3(0f, 0f, -0.001f) }, lerp(col, Color.White, 0.35f), 0.0015f, 0.5f)
    }

    // =================================================================
    //  El microscopio
    // =================================================================

    /**
     * El microscopio de antes del sellado, de latón y vidrio, «pesado como un niño»: el pie de herradura negro, la columna,
     * la platina con su muestra de grama, el espejo, el brazo curvo, los tornillos de enfoque (el grande, el que se atasca)
     * y el tubo con el ocular. Va de pie en el soporte, sin ampliar: ya es grande.
     */
    fun DrawScope.microscopio(s: SitioObjeto) {
        val f = MarcoObj(s, s.alineado + 0.5f, 1.15f)
        val lat = lerp(C.LATON, C.AMARILLO, 0.2f); val neg = lerp(C.NEGRO, C.LATON, 0.1f)
        val herradura = (0..12).map { i -> val a = (-2.45f + 4.9f * i / 12); 0.02f + 0.072f * cos(a) to 0.068f * sin(a) } + (12 downTo 0).map { i -> val a = (-2.45f + 4.9f * i / 12); 0.02f + 0.03f * cos(a) to 0.027f * sin(a) }
        sombras(f, herradura.map { V3(it.first, 0f, it.second) } + listOf(V3(0.045f, 0.12f, 0f), V3(-0.005f, 0.345f, 0f), V3(0.072f, 0.22f, 0f), V3(-0.05f, 0.135f, 0.043f), V3(-0.05f, 0.135f, -0.043f), V3(0.04f, 0.135f, 0.043f)))
        contacto(f, 0.02f, 0f, 0.075f, 0.07f, alfa = 0.45f)
        extruido(f, herradura, 0f, 0.022f, neg, lerp(neg, C.ACERO, 0.18f))
        val az = Azar(Semilla.de("laboratorio", "obj", "microscopio"))
        repeat(10) { val q = V3(0.02f + 0.05f * cos(az.entre(-2.3f, 2.3f)), 0.0222f, az.entre(-0.05f, 0.05f)); drawCircle(lerp(C.GRAMA, C.ACERO, 0.55f).copy(alpha = 0.4f), (0.0018f * f.k).coerceAtLeast(0.3f), f.p(q)) }
        // El espejo, bajo la platina, que recoge la luz.
        palo(f, V3(0.045f, 0.07f, 0f), V3(0.0f, 0.07f, 0f), 0.006f, lat)
        val nE = V3(0.25f, 1f, -0.25f).unidad(); val e0 = V3(-0.006f, 0.066f, 0f)
        cilindroEje(f, e0, e0 + nE * 0.006f, 0.022f, lat, lerp(C.VIDRIO, Color.White, 0.55f))
        val brazoCerca = f.alOjo(V3(0.03f, 0.2f, 0f)).x > 0.2f
        if (!brazoCerca) brazo(f, lat)
        // La columna, la charnela y la ménsula de la platina.
        cilindroEje(f, V3(0.045f, 0.02f, 0f), V3(0.045f, 0.112f, 0f), 0.0125f, lat, null, metal = 0.6f)
        cilindroEje(f, V3(0.045f, 0.12f, -0.02f), V3(0.045f, 0.12f, 0.02f), 0.016f, lat, lerp(lat, Color.White, 0.2f))
        palo(f, V3(0.045f, 0.124f, 0f), V3(0.012f, 0.129f, 0f), 0.016f, lat)
        // La platina negra, con su agujero, las pinzas y la muestra de grama en su cristal.
        caja(f, -0.05f, 0.04f, 0.128f, 0.136f, -0.043f, 0.043f, neg)
        drawPath(f.forma(aro(-0.005f, 0.1362f, 0f, 0.01f, n = 16)), lerp(C.NEGRO, C.SOMBRA, 0.2f))
        cara(f, listOf(V3(-0.042f, 0.1366f, -0.013f), V3(0.03f, 0.1366f, -0.013f), V3(0.03f, 0.1366f, 0.013f), V3(-0.042f, 0.1366f, 0.013f)), lerp(C.VIDRIO, Color.White, 0.3f), ARRIBA, 0.6f)
        drawCircle(f.c(V3(-0.005f, 0.137f, 0f), C.GRAMA, ARRIBA), (0.004f * f.k).coerceAtLeast(0.5f), f.p(V3(-0.005f, 0.137f, 0f)))
        for (sz in listOf(-1f, 1f)) palo(f, V3(-0.045f, 0.1375f, sz * 0.03f), V3(-0.022f, 0.1375f, sz * 0.014f), 0.004f, lat)
        // El objetivo, el tubo con su anillo negro, la cremallera y el ocular.
        cilindroEje(f, V3(-0.005f, 0.141f, 0f), V3(-0.005f, 0.158f, 0f), 0.0095f, lerp(lat, Color.White, 0.15f), null, metal = 0.6f)
        cilindroEje(f, V3(-0.005f, 0.158f, 0f), V3(-0.005f, 0.312f, 0f), 0.0175f, lat, null, metal = 0.7f)
        cilindroEje(f, V3(-0.005f, 0.27f, 0f), V3(-0.005f, 0.279f, 0f), 0.0178f, neg, null)
        palo(f, V3(0.013f, 0.2f, 0f), V3(0.013f, 0.29f, 0f), 0.005f, neg, 0f)
        cilindroEje(f, V3(-0.005f, 0.312f, 0f), V3(-0.005f, 0.338f, 0f), 0.0115f, neg, null)
        cilindroEje(f, V3(-0.005f, 0.336f, 0f), V3(-0.005f, 0.345f, 0f), 0.013f, lat, lerp(C.NEGRO, C.VIDRIO, 0.3f))
        drawCircle(Color.White.copy(alpha = 0.45f), (0.0025f * f.k).coerceAtLeast(0.4f), f.p(V3(-0.009f, 0.3452f, -0.004f)))
        if (brazoCerca) brazo(f, lat)
    }

    /** El brazo curvo de latón, de la charnela al tubo, con los dos tornillos de enfoque. */
    private fun DrawScope.brazo(f: MarcoObj, lat: Color) {
        val puntos = curva(V3(0.045f, 0.125f, 0f), V3(0.08f, 0.18f, 0f), V3(0.077f, 0.262f, 0f), V3(0.016f, 0.292f, 0f), 14)
        val medio = puntos[7]
        trazo(f, puntos, f.c(medio, lat, V3(0.5f, 0.3f, -1f)), 0.024f)
        trazo(f, puntos, lerp(f.c(medio, lat, V3(0.6f, 0.9f, -0.6f)), Color.White, 0.3f), 0.007f, 0.75f)
        trazo(f, puntos.map { it + V3(0f, 0f, -0.012f) }, lerp(f.c(medio, lat, V3(-0.2f, 0.2f, -1f)), C.NEGRO, 0.2f), 0.004f, 0.5f)
        val lejos = if (f.alOjo(V3(0.03f, 0.26f, 0f)).z > 0f) -1f else 1f
        palo(f, V3(0.03f, 0.258f, -0.03f), V3(0.03f, 0.258f, 0.03f), 0.007f, lerp(lat, C.NEGRO, 0.3f), 0f)
        for (sz in listOf(lejos, -lejos)) {
            val a = V3(0.03f, 0.258f, sz * 0.024f); val b = V3(0.03f, 0.258f, sz * 0.036f)
            cilindroEje(f, a, b, 0.017f, lat, lerp(lat, Color.White, 0.25f), metal = 0.5f)
            for (j in 0 until 8) { val ang = j * DOS_PI / 8; val q = V3(0.03f + 0.017f * cos(ang), 0.258f + 0.017f * sin(ang), sz * 0.03f)
                trazo(f, listOf(q + V3(0f, 0f, -0.005f), q + V3(0f, 0f, 0.005f)), lerp(lat, C.NEGRO, 0.4f), 0.0012f, 0.6f) }
        }
    }
}
