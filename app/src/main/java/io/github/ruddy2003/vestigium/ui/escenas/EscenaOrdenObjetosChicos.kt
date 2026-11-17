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
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.brillo
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.caja
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.cara
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.cilindro
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.disco
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.halo
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.raya
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.revolucion
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.sombra
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.trazo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los siete objetos chicos de la escriba, sobre el soporte. Cada uno recibe el punto del soporte en metros de la cámara
 * ([o]) y se arma con un [MarcoOrden]: el giro (para que se vea en tres cuartos), el vuelco de lo plano (recostado hacia
 * quien mira: la tapa del soporte se ve muy de canto, a unos 21° desde los ojos) y cuánto se amplía. **[Diseño propio]**
 * todo lo que no dice el cuento: qué los acompaña y cómo están puestos.
 */
internal object ChicosOrden {
    private val C = ColorOrden
    private val K = ColorObjetoOrden
    private fun az(vararg partes: Any) = Azar(Semilla.de("orden", "objetos", *partes))

    /**
     * Cuánto más grandes que de verdad van todos (además de lo propio de cada uno): el soporte está a unos cinco metros y
     * medio, donde un metro mide unas 40 unidades del lienzo, y a tamaño de teléfono no se leían **[Diseño propio]**.
     */
    private const val MAS = 1.95f

    /** La tapa del soporte bajo [o], para recortar las sombras: unos 60 × 90 cm. */
    private fun tapa(o: V3): Path = TrazoOrden.tapa(o, 0.3f, 0.45f)

    // =================================================================
    //  El candil
    // =================================================================

    /**
     * El candil de latón, «el signo de la orden y también su lámpara: un vaso de latón con aceite de cabra y una mecha»:
     * un candil de garabato **[Diseño propio]** (la forma de los candiles de aceite de siempre): la cazoleta con el aceite y
     * el pico donde descansa la mecha, el platillo de abajo que recoge lo que gotea, el vástago y el gancho de colgarlo. De
     * día, la mecha negra; con la corona de candiles, su llama, su halo y su charco en el soporte, y su luz en el propio
     * latón.
     */
    fun DrawScope.candil(lo0: LuzObjetoOrden, o: V3) {
        val f = MarcoOrden.de(o, 0.35f, 2.6f * MAS)
        val arde = ObjetosOrden.candilEncendido(lo0.m)
        val punta = V3(0.062f, 0.106f, -0.035f)
        val lo = if (arde) lo0.con(Lumbre(f.w(punta + V3(0f, 0.03f, 0f)), 0.95f, 0.45f)) else lo0
        val cuerpo = listOf(V3(-0.06f, 0f, -0.06f), V3(0.06f, 0f, 0.06f), V3(0f, 0.25f, 0.05f), V3(0.065f, 0.1f, -0.04f), V3(-0.05f, 0.1f, 0.04f)).map { f.w(it) }
        sombra(lo, TrazoOrden.circulo(V3(0f, 0f, 0f), V3(0f, 1f, 0f), 0.058f, 10).map { f.w(it) }, cuerpo, o.y, tapa(o), 0.45f)
        if (arde) charco(lo, f, punta)
        platillo(lo, f)
        // El vástago de detrás, del canto del platillo al gancho, y el gancho de colgar.
        cilindro(lo, f, V3(0f, 0.01f, 0.056f), V3(0f, 0.2f, 0.05f), 0.0045f, C.LATON, null, 8)
        trazo(lo, f, TrazoOrden.curva(V3(0f, 0.198f, 0.05f), V3(0f, 0.24f, 0.052f), V3(0f, 0.255f, 0.0f), V3(0f, 0.225f, -0.004f), 8), 0.009f, K.LATON_OSC, V3(-0.4f, 0.3f, -1f))
        trazo(lo, f, TrazoOrden.curva(V3(0f, 0.2f, 0.05f), V3(-0.002f, 0.238f, 0.05f), V3(-0.002f, 0.25f, 0.006f), V3(0f, 0.226f, -0.002f), 8), 0.004f, lerp(C.LATON, Color.White, 0.25f), V3(-0.6f, 0.5f, -1f), 0.8f)
        // El hollín que deja la llama en el vástago, encima del pico.
        raya(f, V3(0.004f, 0.13f, 0.047f), V3(0.002f, 0.185f, 0.048f), 0.008f, C.HOLLIN.copy(alpha = 0.45f))
        cazoleta(lo, f, punta)
        if (arde) llamaDelCandil(lo, f, punta)
    }

    /** El platillo de abajo: un plato hondo de latón con su borde vuelto y una gota de aceite que brilla. */
    private fun DrawScope.platillo(lo: LuzObjetoOrden, f: MarcoOrden) {
        revolucion(lo, f, V3(0f, 0f, 0f), V3(0f, 0.016f, 0f), listOf(0f to 0.036f, 0.45f to 0.052f, 1f to 0.062f), C.LATON, null, 18)
        // Por dentro, más hondo y más oscuro; el borde, claro.
        disco(lo, f, V3(0f, 0.0165f, 0f), V3(0f, 1f, 0f), 0.06f, K.LATON_OSC, 18)
        disco(lo, f, V3(0f, 0.012f, 0f), V3(0f, 1f, 0f), 0.04f, lerp(K.LATON_OSC, C.HOLLIN, 0.3f), 14)
        val borde = TrazoOrden.circulo(V3(0f, 0.0168f, 0f), V3(0f, 1f, 0f), 0.061f, 20, 3.3f, 6.1f)
        trazo(lo, f, borde, 0.004f, lerp(C.LATON, Color.White, 0.3f), V3(0f, 1f, -0.4f), 0.85f)
        disco(lo, f, V3(-0.02f, 0.0125f, -0.018f), V3(0f, 1f, 0f), 0.006f, lerp(C.LATON, Color.White, 0.55f), 8, alfa = 0.7f)
    }

    /** La cazoleta del aceite, sujeta al vástago, con su pico y la mecha; el aceite brilla dentro. */
    private fun DrawScope.cazoleta(lo: LuzObjetoOrden, f: MarcoOrden, punta: V3) {
        val y0 = 0.072f; val y1 = 0.102f
        // El pico: una canal pellizcada que sale hacia delante a la derecha.
        val a = V3(0.024f, y1, -0.03f); val b = V3(0.036f, y1, -0.012f)
        cara(lo, f, listOf(a, b, punta + V3(0.004f, 0f, 0.006f), punta + V3(-0.004f, 0.002f, -0.004f)), V3(0.2f, 1f, -0.2f), lerp(C.LATON, Color.White, 0.08f))
        cara(lo, f, listOf(a, punta + V3(-0.004f, 0.002f, -0.004f), punta + V3(-0.002f, -0.012f, -0.002f), V3(0.02f, y0 + 0.006f, -0.024f)), V3(0.3f, 0f, -1f), C.LATON)
        revolucion(lo, f, V3(0f, y0, 0f), V3(0f, y1, 0f), listOf(0f to 0.018f, 0.35f to 0.036f, 0.8f to 0.046f, 1f to 0.048f), C.LATON, null, 18)
        // El aceite de cabra, oscuro y con su brillo; el canto del vaso, claro.
        disco(lo, f, V3(0f, y1 - 0.004f, 0f), V3(0f, 1f, 0f), 0.044f, lerp(C.MADERA, C.HOLLIN, 0.55f), 16)
        disco(lo, f, V3(-0.012f, y1 - 0.0035f, 0.008f), V3(0f, 1f, 0f), 0.012f, lerp(C.LATON, Color.White, 0.4f), 10, alfa = 0.55f)
        trazo(lo, f, TrazoOrden.circulo(V3(0f, y1, 0f), V3(0f, 1f, 0f), 0.047f, 20, 3.0f, 6.3f), 0.0045f, lerp(C.LATON, Color.White, 0.35f), V3(0f, 1f, -0.5f), 0.9f)
        // Dos golpes de martillo en la panza, del lado de la sombra.
        disco(lo, f, V3(-0.035f, y0 + 0.016f, -0.025f), V3(-0.6f, -0.2f, -0.8f), 0.006f, K.LATON_OSC, 8, alfa = 0.7f)
        // La mecha: de algodón, tendida en el pico, con la punta quemada.
        trazo(lo, f, listOf(V3(0.006f, y1 - 0.002f, -0.008f), V3(0.03f, y1 + 0.002f, -0.022f), punta + V3(0f, 0.004f, 0f)), 0.009f, lerp(C.PAPEL, C.MADERA, 0.35f), V3(0f, 1f, -0.3f))
        disco(lo, f, punta + V3(0.001f, 0.005f, -0.001f), V3(0.4f, 0.6f, -0.6f), 0.006f, C.HOLLIN, 8)
    }

    /** La llama del candil (quieta: la parte viva no es de los objetos), su halo y su luz en el aire. */
    private fun DrawScope.llamaDelCandil(lo: LuzObjetoOrden, f: MarcoOrden, punta: V3) {
        val pie = f.p(punta + V3(0f, 0.006f, 0f)); val alto = (0.075f * f.k).coerceIn(3f, 30f)
        val k = 0.4f + 0.6f * lo.noche
        resplandor(pie + Offset(0f, -alto * 0.4f), alto * 4.2f, 0.75f * k, C.LUMBRE)
        llama(pie, alto, 0f, 7)
        halo(f, punta + V3(0f, 0.03f, 0f), 0.05f, Color(0xFFFFF0C0), 0.5f * k)
    }

    /** El charco de luz del candil en el soporte, debajo de todo. */
    private fun DrawScope.charco(lo: LuzObjetoOrden, f: MarcoOrden, punta: V3) {
        val c = f.p(V3(punta.x * 0.6f, 0f, punta.z * 0.6f)); val r = 0.22f * f.k
        val k = (0.25f + 0.55f * lo.noche).coerceIn(0f, 1f)
        drawOval(Brush.radialGradient(listOf(C.LUMBRE.copy(alpha = 0.55f * k), C.LUMBRE.copy(alpha = 0.18f * k), Color.Transparent), c, r), Offset(c.x - r, c.y - r * 0.42f), Size(r * 2f, r * 0.84f), blendMode = BlendMode.Plus)
    }

    // =================================================================
    //  La pluma
    // =================================================================

    /**
     * La pluma de acero «de una caja de antes del Fuego que tenía cien iguales»: el mango de madera clara con su virola de
     * acero, metida en el tintero de vidrio cuadrado, oscuro de tinta; delante, dos plumines de repuesto que brillan, todo
     * sobre un trapo con borrones de tinta.
     */
    fun DrawScope.pluma(lo: LuzObjetoOrden, o: V3) {
        val f = MarcoOrden.de(o, -0.25f, 3.0f * MAS)
        val d = TrazoOrden.unidad(V3(0.42f, 1f, 0.22f))
        val boca = V3(0.02f, 0.062f, 0.03f)
        val cuerpo = listOf(V3(-0.075f, 0f, 0.05f), V3(0.07f, 0f, -0.06f), V3(0.05f, 0.06f, 0.05f), boca + d * 0.17f).map { f.w(it) }
        sombra(lo, listOf(V3(-0.07f, 0f, -0.05f), V3(0.07f, 0f, -0.05f), V3(0.07f, 0f, 0.05f), V3(-0.07f, 0f, 0.05f)).map { f.w(it) }, cuerpo, o.y, tapa(o), 0.25f)
        trapo(lo, f, az("trapo"))
        plumin(lo, f.mas(-0.05f, 0.004f, -0.025f, amp = 1.7f), V3(0f, 0f, 0f), TrazoOrden.unidad(V3(0.55f, 0f, -1f)))
        plumin(lo, f.mas(-0.03f, 0.004f, -0.005f, amp = 1.7f), V3(0f, 0f, 0f), TrazoOrden.unidad(V3(-0.9f, 0f, -0.5f)))
        tintero(lo, f.mas(boca.x, 0.003f, boca.z))
        mangoDePluma(lo, f, boca + d * 0.012f, d)
    }

    /** El trapo de limpiar la pluma: un paño de lino arrugado, con un pico doblado y los borrones de tinta. */
    private fun DrawScope.trapo(lo: LuzObjetoOrden, f: MarcoOrden, a: Azar) {
        val borde = listOf(V3(-0.075f, 0.002f, -0.05f), V3(-0.02f, 0.003f, -0.058f), V3(0.04f, 0.002f, -0.055f), V3(0.075f, 0.003f, -0.035f), V3(0.07f, 0.002f, 0.02f),
            V3(0.06f, 0.004f, 0.055f), V3(0.0f, 0.003f, 0.06f), V3(-0.06f, 0.002f, 0.05f), V3(-0.08f, 0.004f, 0.0f))
        cara(lo, f, borde, V3(0f, 1f, 0f), K.LINO)
        for (z in listOf(-0.02f, 0.025f)) trazo(lo, f, listOf(V3(-0.07f, 0.006f, z + 0.01f), V3(-0.01f, 0.008f, z - 0.004f), V3(0.06f, 0.006f, z + 0.006f)), 0.005f, lerp(K.LINO, C.SOMBRA, 0.35f), V3(-0.3f, 0.7f, -0.6f), 0.7f)
        cara(lo, f, listOf(V3(0.04f, 0.003f, -0.055f), V3(0.075f, 0.003f, -0.035f), V3(0.05f, 0.014f, -0.03f)), V3(0.3f, 0.6f, -0.7f), lerp(K.LINO, C.ARENA, 0.2f))
        repeat(5) { i ->
            val c = V3(a.entreF(-0.06f, 0.05f), 0.0045f, a.entreF(-0.045f, 0.045f)); val r = if (i < 2) a.entreF(0.008f, 0.014f) else a.entreF(0.003f, 0.006f)
            disco(lo, f, c, V3(0f, 1f, 0f), r, K.TINTA, 9, alfa = 0.75f)
        }
    }

    /**
     * El tintero de vidrio grueso, cuadrado, con los hombros biselados y el cuello redondo: oscuro de la tinta que lleva
     * dentro, con los cantos claros del vidrio y una chorrera.
     */
    private fun DrawScope.tintero(lo: LuzObjetoOrden, f: MarcoOrden) {
        val vidrio = lerp(C.VIDRIO, K.TINTA, 0.8f); val canto = lerp(C.VIDRIO, Color.White, 0.25f)
        val a = 0.028f; val h = 0.038f; val b = 0.015f; val hh = 0.05f
        caja(lo, f, -a, a, 0f, h, -a, a, vidrio, vidrio)
        // Los hombros biselados: cuatro caras inclinadas hasta el cuello.
        val abajo = listOf(V3(-a, h, -a), V3(a, h, -a), V3(a, h, a), V3(-a, h, a)); val arriba = listOf(V3(-b, hh, -b), V3(b, hh, -b), V3(b, hh, b), V3(-b, hh, b))
        val normales = listOf(V3(0f, 0.8f, -1f), V3(1f, 0.8f, 0f), V3(0f, 0.8f, 1f), V3(-1f, 0.8f, 0f))
        for (i in 0 until 4) {
            val q = listOf(abajo[i], abajo[(i + 1) % 4], arriba[(i + 1) % 4], arriba[i])
            if (TrazoOrden.seVe(f.w(TrazoOrden.centro(q)), f.n(normales[i]))) cara(lo, f, q, normales[i], lerp(vidrio, C.VIDRIO, 0.15f), claro = 0.1f)
        }
        cilindro(lo, f, V3(0f, hh, 0f), V3(0f, hh + 0.01f, 0f), 0.012f, lerp(vidrio, C.VIDRIO, 0.2f), lerp(C.VIDRIO, C.HOLLIN, 0.3f), 12)
        disco(lo, f, V3(0f, hh + 0.0102f, 0f), V3(0f, 1f, 0f), 0.0085f, K.TINTA, 12)
        // Los cantos del vidrio, que cogen la luz, y la chorrera de tinta.
        raya(f, V3(-a, 0.002f, -a), V3(-a, h, -a), 0.003f, lo.color(canto, f.w(V3(-a, h / 2f, -a)), f.n(V3(-1f, 0f, -1f))).copy(alpha = 0.85f))
        raya(f, V3(-a, h, -a), V3(a, h, -a), 0.0025f, lo.color(canto, f.w(V3(0f, h, -a)), f.n(V3(0f, 1f, -1f))).copy(alpha = 0.8f))
        brillo(lo, f, V3(-a + 0.006f, 0.008f, -a - 0.0005f), V3(-a + 0.006f, h - 0.006f, -a - 0.0005f), 0.004f, C.VIDRIO, V3(0f, 0.2f, -1f), 1f)
        trazo(lo, f, listOf(V3(0.006f, hh + 0.008f, -0.01f), V3(0.012f, h + 0.004f, -0.022f), V3(0.014f, h - 0.012f, -a - 0.0006f)), 0.0045f, K.TINTA, V3(0f, 0.3f, -1f))
    }

    /** La pluma en el tintero: la virola de acero con su brillo y el mango de madera clara, que sube en diagonal. */
    private fun DrawScope.mangoDePluma(lo: LuzObjetoOrden, f: MarcoOrden, a: V3, d: V3) {
        val v1 = a + d * 0.03f; val fin = a + d * 0.175f
        cilindro(lo, f, a, v1, 0.0062f, K.ACERO, null, 10)
        revolucion(lo, f, v1, fin, listOf(0f to 0.0066f, 0.25f to 0.0056f, 0.9f to 0.0048f, 1f to 0.004f), lerp(C.MADERA, C.ARENA, 0.4f), lerp(C.MADERA, C.ARENA, 0.2f), 10)
        // Donde se agarra, más oscuro de los dedos; el brillo del acero y el de la madera.
        cilindro(lo, f, v1, v1 + d * 0.035f, 0.0068f, lerp(C.MADERA, C.HOLLIN, 0.2f), null, 10)
        brillo(lo, f, a + V3(-0.004f, 0f, -0.003f), v1 + V3(-0.004f, 0f, -0.003f), 0.0025f, K.ACERO, V3(-0.7f, 0.2f, -0.7f), 1f)
        brillo(lo, f, v1 + d * 0.05f + V3(-0.004f, 0f, -0.003f), fin + V3(-0.003f, 0f, -0.002f), 0.002f, lerp(C.MADERA, C.ARENA, 0.4f), V3(-0.7f, 0.2f, -0.7f), 0.6f)
    }

    /** Un plumín de acero: la punta con su raja y su ojo, que brilla. */
    private fun DrawScope.plumin(lo: LuzObjetoOrden, f: MarcoOrden, c: V3, d: V3) {
        val l = V3(-d.z, 0f, d.x)
        val forma = listOf(c + l * 0.0045f, c + d * 0.012f + l * 0.004f, c + d * 0.024f, c + d * 0.012f - l * 0.004f, c - l * 0.0045f)
        cara(lo, f, forma, V3(0f, 1f, 0f), lerp(K.ACERO, Color.White, 0.2f), claro = 0.3f)
        raya(f, c + d * 0.012f, c + d * 0.024f, 0.0009f, C.HOLLIN.copy(alpha = 0.8f))
        brillo(lo, f, c + l * 0.002f, c + d * 0.014f + l * 0.002f, 0.0016f, K.ACERO, V3(0f, 1f, -0.4f), 1f)
    }

    // =================================================================
    //  El cuaderno
    // =================================================================

    /**
     * El cuaderno de papel de trapo, «cosidos a mano; las escribas llevan uno al cuello en cada patrulla, atado con un
     * cordón»: abierto y recostado hacia quien mira, con las hojas de canto barbado, los renglones sin letras y un apunte del
     * páramo (una meseta y una torre caída), las puntadas en el lomo y el cordón carmesí desvaído, con su nudo, que da la
     * vuelta en el soporte.
     */
    fun DrawScope.cuaderno(lo: LuzObjetoOrden, o: V3) {
        val base = MarcoOrden.de(o, 0.18f, 2.2f * MAS)
        val f = base.mas(0f, 0.012f, 0f, vuelco = 0.62f)
        val cuerpo = listOf(V3(-0.11f, 0f, -0.075f), V3(0.11f, 0f, -0.075f), V3(0.11f, 0f, 0.075f), V3(-0.11f, 0f, 0.075f), V3(0f, 0.025f, 0f)).map { f.w(it) }
        sombra(lo, listOf(V3(-0.1f, -0.012f, -0.07f), V3(0.1f, -0.012f, -0.07f), V3(0.1f, -0.012f, 0f), V3(-0.1f, -0.012f, 0f)).map { f.w(it) }, cuerpo, o.y, tapa(o), 0.3f)
        cordon(lo, base)
        // Lo que lo levanta: un rollo de su propio cordón y una piedra plana debajo del canto de atrás.
        revolucion(lo, base, V3(-0.02f, 0f, 0.055f), V3(-0.02f, 0.03f, 0.055f), listOf(0f to 0.04f, 0.7f to 0.036f, 1f to 0.02f), K.PIEDRA_CANTO, K.PIEDRA_CANTO, 10)
        tapas(lo, f)
        for (lado in listOf(-1f, 1f)) hojas(lo, f, lado)
        renglones(lo, f, az("cuaderno"))
        apunte(lo, f)
        // Las puntadas del lomo, en la canal: hilo grueso que entra y sale.
        for (i in 0 until 5) { val z = -0.06f + i * 0.03f; raya(f, V3(0f, 0.0125f, z), V3(0f, 0.0125f, z + 0.016f), 0.0035f, lo.color(K.CORDON, f.w(V3(0f, 0.012f, z)), f.n(V3(0f, 1f, 0f)))) }
    }

    /** Las tapas: cartón forrado de tela parda, más grandes que las hojas, con las esquinas gastadas. */
    private fun DrawScope.tapas(lo: LuzObjetoOrden, f: MarcoOrden) {
        val tela = lerp(C.MADERA, C.TELA_VIEJA, 0.5f)
        for (s in listOf(-1f, 1f)) {
            val x1 = s * 0.112f
            cara(lo, f, listOf(V3(0f, 0f, -0.078f), V3(x1, 0f, -0.077f), V3(x1 * 1.01f, 0f, 0.074f), V3(0f, 0f, 0.078f)), V3(0f, 1f, 0f), tela)
            cara(lo, f, listOf(V3(x1, 0f, -0.077f), V3(x1, 0.005f, -0.077f), V3(x1, 0.005f, 0.074f), V3(x1, 0f, 0.074f)), V3(s, 0f, 0f), lerp(tela, C.HOLLIN, 0.2f))
            cara(lo, f, listOf(V3(0f, 0f, -0.078f), V3(x1, 0f, -0.077f), V3(x1, 0.005f, -0.077f), V3(0f, 0.005f, -0.078f)), V3(0f, 0f, -1f), lerp(tela, C.HOLLIN, 0.15f))
        }
    }

    /** El taco de hojas de un lado ([lado] −1 la izquierda): se abomba hacia la canal, con el canto barbado. */
    private fun DrawScope.hojas(lo: LuzObjetoOrden, f: MarcoOrden, lado: Float) {
        val a = az("hojas", lado)
        val xs = listOf(0f, 0.012f, 0.035f, 0.07f, 0.1f).map { it * lado }
        val ys = listOf(0.012f, 0.02f, 0.022f, 0.019f, 0.014f)
        for (i in 0 until xs.size - 1) {
            val z0 = -0.072f + a.entreF(-0.002f, 0.002f); val z1 = 0.071f + a.entreF(-0.002f, 0.002f)
            val n = TrazoOrden.unidad(V3(-(ys[i + 1] - ys[i]) * lado, xs[i + 1] * lado - xs[i] * lado, 0f))
            cara(lo, f, listOf(V3(xs[i], ys[i], z0), V3(xs[i + 1], ys[i + 1], z0), V3(xs[i + 1], ys[i + 1], z1), V3(xs[i], ys[i], z1)), n, K.PAPEL_VIEJO, claro = 0.1f)
        }
        // El canto de las hojas, de frente: capas finas, algo desiguales (el barbado del papel de trapo).
        val x = xs.last(); val canto = listOf(V3(x, 0.005f, -0.073f), V3(x, ys.last(), -0.072f), V3(x, ys.last(), 0.071f), V3(x, 0.005f, 0.072f))
        cara(lo, f, canto, V3(lado, 0f, 0f), lerp(K.PAPEL_VIEJO, C.ARENA, 0.4f))
        val delante = listOf(V3(0f, 0.005f, -0.074f), V3(x, 0.005f, -0.073f), V3(x, ys.last(), -0.072f), V3(xs[2], ys[2], -0.073f), V3(0f, ys[0], -0.074f))
        cara(lo, f, delante, V3(0f, 0f, -1f), lerp(K.PAPEL_VIEJO, C.ARENA, 0.3f))
        for (j in 1..3) { val y = 0.005f + j * 0.003f; raya(f, V3(0.004f * lado, y, -0.0745f), V3(x, y, -0.0735f), 0.0007f, lerp(C.ARENA, C.MADERA, 0.4f).copy(alpha = 0.6f)) }
    }

    /** Los renglones, a mano y sin letras: rayas de tinta parda de largos distintos. */
    private fun DrawScope.renglones(lo: LuzObjetoOrden, f: MarcoOrden, a: Azar) {
        val tinta = lerp(K.TINTA, C.MADERA, 0.35f)
        for (lado in listOf(-1f, 1f)) {
            val filas = if (lado < 0f) 11 else 5
            for (i in 0 until filas) {
                val z = -0.058f + i * 0.0115f; val x0 = 0.016f; val x1 = 0.016f + a.entreF(0.04f, 0.075f)
                val y = 0.0215f + 0.0012f
                trazo(lo, f, listOf(V3(x0 * lado, y, z), V3((x0 + x1) / 2f * lado, y + 0.0004f, z + 0.0008f), V3(x1 * lado, y - 0.0015f, z)), 0.0016f, tinta, V3(0f, 1f, 0f), 0.75f)
            }
        }
    }

    /** El apunte de la hoja de la derecha: una meseta con su torre caída, a pluma, y el sol. */
    private fun DrawScope.apunte(lo: LuzObjetoOrden, f: MarcoOrden) {
        val y = 0.0225f; val tinta = lerp(K.TINTA, C.MADERA, 0.25f)
        val perfil = listOf(V3(0.02f, y, 0.06f), V3(0.035f, y, 0.06f), V3(0.042f, y, 0.034f), V3(0.072f, y, 0.032f), V3(0.08f, y, 0.06f), V3(0.094f, y, 0.06f))
        trazo(lo, f, perfil, 0.0018f, tinta, V3(0f, 1f, 0f), 0.85f)
        trazo(lo, f, listOf(V3(0.06f, y, 0.032f), V3(0.058f, y, 0.016f), V3(0.064f, y, 0.012f)), 0.0016f, tinta, V3(0f, 1f, 0f), 0.85f)
        trazo(lo, f, TrazoOrden.circulo(V3(0.084f, y, 0.016f), V3(0f, 1f, 0f), 0.005f, 10), 0.0013f, tinta, V3(0f, 1f, 0f), 0.75f)
    }

    /** El cordón de colgar al cuello: sale del lomo con su nudo y da la vuelta, flojo, por el soporte. */
    private fun DrawScope.cordon(lo: LuzObjetoOrden, f: MarcoOrden) {
        val nudo = V3(0f, 0.016f, -0.07f)
        val vuelta = TrazoOrden.curva(nudo, V3(0.06f, 0.004f, -0.13f), V3(0.2f, 0.003f, -0.09f), V3(0.16f, 0.003f, 0.0f), 10) +
            TrazoOrden.curva(V3(0.16f, 0.003f, 0.0f), V3(0.13f, 0.003f, 0.08f), V3(0.02f, 0.003f, 0.11f), V3(0f, 0.016f, 0.07f), 10).drop(1)
        trazo(lo, f, vuelta, 0.0065f, lerp(K.CORDON, C.HOLLIN, 0.3f), V3(0f, 1f, 0f))
        trazo(lo, f, vuelta, 0.0035f, K.CORDON, V3(0f, 1f, -0.3f), claro = 0.15f)
        disco(lo, f, nudo + V3(0f, 0.004f, -0.004f), V3(0f, 0.6f, -0.8f), 0.0075f, K.CORDON, 8)
    }

    // =================================================================
    //  La lupa
    // =================================================================

    /**
     * La lupa: «la lente de un aparato de antes del Fuego… montada en un aro de latón de la fragua», con su mango de madera
     * atado con cordel. Junto a ella, los tornillos de una máquina en fila sobre un trapo, «por orden» (`PiezasEscriba`); uno
     * se ve grande a través de la lente. Con sol, en su sombra, el punto de luz que junta la lente.
     */
    fun DrawScope.lupa(lo: LuzObjetoOrden, o: V3) {
        val f = MarcoOrden.de(o, 0.1f, 2.7f * MAS)
        val anillo = f.mas(0.018f, 0.014f, -0.004f, vuelco = 0.55f)
        val cuerpo = (TrazoOrden.circulo(V3(0f, 0f, 0f), V3(0f, 1f, 0f), 0.05f, 8).map { anillo.w(it) } + listOf(f.w(V3(-0.07f, 0.01f, -0.04f))))
        sombra(lo, listOf(V3(-0.075f, 0f, -0.045f), V3(0.07f, 0f, -0.03f), V3(0.06f, 0f, 0.05f), V3(-0.06f, 0f, 0.05f)).map { f.w(it) }, cuerpo, o.y, tapa(o), 0.2f)
        foco(lo, anillo)
        trapoDeTornillos(lo, f)
        mango(lo, anillo)
        aro(lo, anillo)
        lente(lo, anillo)
    }

    /** El trapo con los tornillos en fila: cabezas redondas con su ranura y su sombrita. */
    private fun DrawScope.trapoDeTornillos(lo: LuzObjetoOrden, f: MarcoOrden) {
        cara(lo, f, listOf(V3(-0.04f, 0.002f, -0.005f), V3(0.085f, 0.002f, 0.0f), V3(0.088f, 0.002f, 0.06f), V3(-0.035f, 0.003f, 0.055f)), V3(0f, 1f, 0f), lerp(K.LINO, C.TELA, 0.25f))
        for (i in 0 until 6) {
            val c = V3(-0.025f + i * 0.019f, 0.004f, 0.035f + i * 0.0012f); val f2 = f.mas(c.x, c.y, c.z)
            cilindro(lo, f2, V3(0f, 0f, 0f), V3(0f, 0.004f, 0f), 0.0062f - i * 0.0003f, K.ACERO, lerp(K.ACERO, Color.White, 0.2f), 10)
            raya(f2, V3(-0.004f, 0.0042f, 0f), V3(0.004f, 0.0042f, 0f), 0.0012f, C.HOLLIN.copy(alpha = 0.8f))
        }
    }

    /** El mango: virola de latón y madera atada con vueltas de cordel, con su bola al final. */
    private fun DrawScope.mango(lo: LuzObjetoOrden, f: MarcoOrden) {
        val a = V3(-0.046f, 0f, -0.018f); val d = TrazoOrden.unidad(V3(-1f, -0.15f, -0.45f))
        cilindro(lo, f, a, a + d * 0.016f, 0.0068f, C.LATON, C.LATON, 10)
        val b = a + d * 0.016f; val c = b + d * 0.075f
        cilindro(lo, f, b, c, 0.0085f, lerp(C.MADERA, C.HOLLIN, 0.15f), null, 10)
        for (i in 1..5) { val q = b + d * (0.012f * i); raya(f, q + V3(0f, 0.0085f, 0f), q + V3(0f, -0.006f, 0.004f), 0.0028f, lerp(C.ARENA, C.MADERA, 0.4f).copy(alpha = 0.8f)) }
        revolucion(lo, f, c, c + d * 0.014f, listOf(0f to 0.0085f, 0.5f to 0.0105f, 1f to 0.002f), lerp(C.MADERA, C.HOLLIN, 0.25f), null, 10)
    }

    /** El aro de latón de la fragua: grueso, algo desigual, con marcas de martillo. */
    private fun DrawScope.aro(lo: LuzObjetoOrden, f: MarcoOrden) {
        revolucion(lo, f, V3(0f, -0.004f, 0f), V3(0f, 0.004f, 0f), listOf(0f to 0.05f, 0.5f to 0.0515f, 1f to 0.05f), C.LATON, null, 22)
        val fuera = TrazoOrden.circulo(V3(0f, 0.004f, 0f), V3(0f, 1f, 0f), 0.05f, 24).dropLast(1)
        val dentro = TrazoOrden.circulo(V3(0f, 0.004f, 0f), V3(0f, 1f, 0f), 0.04f, 24).dropLast(1)
        val forma = Path().apply { addPath(TrazoOrden.forma(fuera.map { f.w(it) })); addPath(TrazoOrden.forma(dentro.reversed().map { f.w(it) })) }
        drawPath(forma, lo.color(C.LATON, f.w(V3(0f, 0.004f, 0f)), f.n(V3(0f, 1f, 0f)), 0.15f))
        val a = az("aro")
        repeat(7) { val ang = a.entreF(0f, 6.28f); val q = V3(0.045f * cos(ang), 0.0045f, 0.045f * sin(ang)); disco(lo, f, q, V3(0f, 1f, 0f), 0.003f, K.LATON_OSC, 6, alfa = 0.6f) }
        trazo(lo, f, TrazoOrden.circulo(V3(0f, 0.0045f, 0f), V3(0f, 1f, 0f), 0.05f, 16, 3.6f, 5.6f), 0.0025f, lerp(C.LATON, Color.White, 0.45f), V3(0f, 1f, -0.4f), 0.85f)
    }

    /** La lente: un velo de vidrio, el tornillo agrandado que se ve a través, el canto oscuro y el brillo curvo. */
    private fun DrawScope.lente(lo: LuzObjetoOrden, f: MarcoOrden) {
        val forma = disco(lo, f, V3(0f, 0.004f, 0f), V3(0f, 1f, 0f), 0.04f, C.VIDRIO, 20, alfa = 0.22f)
        clipPath(forma) {
            // El tornillo de debajo, grande: la cabeza con su ranura y la caña con la rosca.
            val tor = f.mas(0.006f, -0.004f, 0.012f, giro = 0.5f, amp = 1.9f)
            cilindro(lo, tor, V3(-0.008f, 0f, 0f), V3(0.016f, 0f, 0f), 0.0035f, K.ACERO, null, 8)
            for (i in 0 until 5) raya(tor, V3(-0.004f + i * 0.004f, 0.0036f, -0.002f), V3(-0.002f + i * 0.004f, 0.0036f, 0.002f), 0.0009f, C.HOLLIN.copy(alpha = 0.6f))
            revolucion(lo, tor, V3(-0.014f, 0f, 0f), V3(-0.008f, 0f, 0f), listOf(0f to 0.0065f, 1f to 0.0065f), K.ACERO, lerp(K.ACERO, Color.White, 0.2f), 12)
            drawPath(forma, Brush.radialGradient(listOf(Color.Transparent, C.HOLLIN.copy(alpha = 0.3f)), f.p(V3(0f, 0.004f, 0f)), 0.042f * f.k))
        }
        trazo(lo, f, TrazoOrden.circulo(V3(0f, 0.0045f, 0f), V3(0f, 1f, 0f), 0.03f, 10, 3.4f, 4.6f), 0.004f, Color.White, V3(0f, 1f, -0.3f), 0.55f)
    }

    /** El punto de luz que junta la lente en el soporte, dentro de su sombra. */
    private fun DrawScope.foco(lo: LuzObjetoOrden, f: MarcoOrden) {
        if (lo.fuerzaDeSombra < 0.15f || lo.noche > 0.3f) return
        val c = TrazoOrden.aCiudadela(f.w(V3(0f, 0f, 0f))); val corre = lo.lz.corre
        val d = (c.y - (f.o.y - 0.014f * f.amplia)).coerceAtLeast(0f)
        val q = CamOrden.p(c.x + corre.x * d, c.y - d, c.z + corre.y * d)
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFFF4D0).copy(alpha = 0.85f), Color.Transparent), q, 0.012f * f.k), 0.012f * f.k, q, blendMode = BlendMode.Plus)
    }

    // =================================================================
    //  El destornillador
    // =================================================================

    /**
     * El destornillador, «un mango con nueve puntas que se cambian», que ella lleva en el cinturón: la funda de cuero
     * abierta y recostada, con las nueve puntas de acero en sus presillas (cada una con su forma de punta), y el mango de
     * madera con su virola y una punta puesta, delante.
     */
    fun DrawScope.destornillador(lo: LuzObjetoOrden, o: V3) {
        val base = MarcoOrden.de(o, -0.15f, 2.4f * MAS)
        val f = base.mas(0f, 0.006f, 0.01f, vuelco = 0.75f)
        val cuerpo = listOf(V3(-0.085f, 0f, -0.05f), V3(0.085f, 0f, -0.05f), V3(0.085f, 0.012f, 0.06f), V3(-0.085f, 0.012f, 0.06f)).map { f.w(it) } + listOf(base.w(V3(0.08f, 0.012f, -0.07f)))
        sombra(lo, listOf(V3(-0.085f, 0f, -0.04f), V3(0.085f, 0f, -0.04f), V3(0.085f, 0f, 0.02f), V3(-0.085f, 0f, 0.02f)).map { base.w(it) }, cuerpo, o.y, tapa(o), 0.3f)
        // Lo que la sostiene: el cinturón enrollado detrás.
        revolucion(lo, base, V3(-0.07f, 0.02f, 0.045f), V3(0.07f, 0.02f, 0.045f), listOf(0f to 0.022f, 0.1f to 0.024f, 0.9f to 0.024f, 1f to 0.022f), K.CUERO_OSC, lerp(K.CUERO_OSC, C.HOLLIN, 0.3f), 10)
        funda(lo, f)
        for (i in 0 until 9) punta(lo, f, i)
        mangoDestornillador(lo, base)
    }

    /** La funda de cuero abierta: la hoja con sus costuras, la tira de las presillas y la solapa caída. */
    private fun DrawScope.funda(lo: LuzObjetoOrden, f: MarcoOrden) {
        cara(lo, f, listOf(V3(-0.085f, 0f, -0.045f), V3(0.085f, 0f, -0.045f), V3(0.083f, 0f, 0.05f), V3(-0.083f, 0f, 0.05f)), V3(0f, 1f, 0f), K.CUERO)
        // La solapa, que cae por delante del soporte, de canto.
        cara(lo, f, listOf(V3(-0.085f, 0f, -0.045f), V3(0.085f, 0f, -0.045f), V3(0.08f, -0.03f, -0.06f), V3(-0.08f, -0.03f, -0.06f)), V3(0f, 0.3f, -1f), lerp(K.CUERO, C.HOLLIN, 0.12f))
        for (z in listOf(-0.04f, 0.045f)) for (i in 0 until 16) { val x = -0.078f + i * 0.0105f; raya(f, V3(x, 0.0008f, z), V3(x + 0.006f, 0.0008f, z), 0.0011f, lerp(K.CUERO, C.ARENA, 0.45f).copy(alpha = 0.7f)) }
        // La tira de las presillas, más oscura, cosida de través.
        cara(lo, f, listOf(V3(-0.08f, 0.004f, -0.004f), V3(0.08f, 0.004f, -0.004f), V3(0.08f, 0.004f, 0.016f), V3(-0.08f, 0.004f, 0.016f)), V3(0f, 1f, 0f), K.CUERO_OSC)
        // El gastado del cuero, más claro donde roza.
        disco(lo, f, V3(0.04f, 0.001f, 0.03f), V3(0f, 1f, 0f), 0.022f, lerp(K.CUERO, C.ARENA, 0.3f), 10, alfa = 0.35f)
    }

    /** Una de las nueve puntas en su presilla: una varilla de acero hexagonal con su punta propia (plana, de cruz, fina…). */
    private fun DrawScope.punta(lo: LuzObjetoOrden, f: MarcoOrden, i: Int) {
        val x = -0.068f + i * 0.017f
        val a = V3(x, 0.008f, 0.032f); val b = V3(x, 0.008f, -0.024f)
        cilindro(lo, f, a, b, 0.0042f, K.ACERO, lerp(K.ACERO, Color.White, 0.15f), 6)
        // La punta, que asoma por delante.
        val tip = b + V3(0f, 0f, -0.008f)
        when (i % 3) {
            0 -> cara(lo, f, listOf(b + V3(-0.0042f, 0f, 0f), b + V3(0.0042f, 0f, 0f), tip + V3(0.0022f, 0f, 0f), tip + V3(-0.0022f, 0f, 0f)), V3(0f, 1f, -0.2f), K.ACERO, claro = 0.15f)
            1 -> cara(lo, f, listOf(b + V3(-0.004f, 0f, 0f), b + V3(0.004f, 0f, 0f), tip), V3(0f, 1f, -0.2f), K.ACERO, claro = 0.15f)
            else -> cilindro(lo, f, b, tip, 0.0018f, K.ACERO, null, 6)
        }
        brillo(lo, f, a + V3(-0.0015f, 0.004f, -0.004f), b + V3(-0.0015f, 0.004f, 0.004f), 0.0016f, K.ACERO, V3(0f, 1f, -0.3f), 0.9f)
        // La presilla que la sujeta.
        raya(f, V3(x - 0.006f, 0.012f, 0.006f), V3(x + 0.006f, 0.012f, 0.006f), 0.004f, lo.color(K.CUERO_OSC, f.w(V3(x, 0.012f, 0.006f)), f.n(V3(0f, 1f, -0.3f))))
    }

    /** El mango: madera dura con estrías, la virola de acero y la punta plana puesta. */
    private fun DrawScope.mangoDestornillador(lo: LuzObjetoOrden, f: MarcoOrden) {
        val a = V3(-0.07f, 0.013f, -0.075f); val d = TrazoOrden.unidad(V3(1f, 0f, 0.12f))
        val b = a + d * 0.085f
        revolucion(lo, f, a, b, listOf(0f to 0.011f, 0.1f to 0.0135f, 0.7f to 0.0135f, 0.9f to 0.0115f, 1f to 0.009f), lerp(C.MADERA, C.ROCA, 0.25f), lerp(C.MADERA, C.HOLLIN, 0.2f), 12)
        for (i in 0 until 4) { val q = a + d * (0.015f + i * 0.014f); raya(f, q + V3(0f, 0.012f, -0.006f), q + d * 0.01f + V3(0f, 0.012f, -0.006f), 0.0014f, C.HOLLIN.copy(alpha = 0.35f)) }
        val c = b + d * 0.018f
        cilindro(lo, f, b, c, 0.0075f, K.ACERO, null, 10)
        val e = c + d * 0.03f
        cilindro(lo, f, c, e, 0.0035f, K.ACERO, null, 6)
        cara(lo, f, listOf(e + V3(0f, 0.0035f, 0f), e + V3(0f, -0.0035f, 0f), e + d * 0.01f + V3(0f, -0.0012f, 0f), e + d * 0.01f + V3(0f, 0.0012f, 0f)), V3(0f, 0.2f, -1f), K.ACERO, claro = 0.2f)
        brillo(lo, f, b + V3(0f, 0.006f, -0.004f), c + V3(0f, 0.006f, -0.004f), 0.0022f, K.ACERO, V3(0f, 1f, -0.5f), 1f)
    }

    // =================================================================
    //  La brújula
    // =================================================================

    /**
     * La brújula, «con la aguja temblorosa y el cristal rayado»: la caja de latón con la tapa abierta hacia atrás, la rosa
     * con sus marcas (rayas, sin letras), la aguja de dos colores con el carmesí al norte, y una segunda aguja, tenue, a un
     * lado: la que tiembla. El cristal, con sus rayas y su brillo; el cordón de colgar, suelto.
     */
    fun DrawScope.brujula(lo: LuzObjetoOrden, o: V3) {
        val base = MarcoOrden.de(o, 0.25f, 3.0f * MAS)
        val f = base.mas(0f, 0.004f, 0f, vuelco = 0.5f)
        val cuerpo = listOf(V3(-0.035f, 0f, -0.035f), V3(0.035f, 0f, -0.035f), V3(0.035f, 0f, 0.035f), V3(-0.035f, 0f, 0.035f), V3(0f, 0.07f, 0.04f)).map { f.w(it) }
        sombra(lo, TrazoOrden.circulo(V3(0f, 0f, 0.01f), V3(0f, 1f, 0f), 0.034f, 10).map { base.w(it) }, cuerpo, o.y, tapa(o), 0.35f)
        trazo(lo, base, TrazoOrden.curva(V3(0.03f, 0.004f, -0.025f), V3(0.07f, 0.002f, -0.05f), V3(0.09f, 0.002f, 0f), V3(0.06f, 0.002f, 0.04f), 10), 0.0035f, lerp(C.MADERA, C.HOLLIN, 0.2f), V3(0f, 1f, 0f))
        // Lo que la recuesta: una cuña de piedra detrás.
        caja(lo, base, -0.03f, 0.03f, 0f, 0.02f, 0.02f, 0.05f, K.PIEDRA_CANTO, lerp(K.PIEDRA_CANTO, C.ARENA, 0.2f))
        tapaDeLaBrujula(lo, f)
        revolucion(lo, f, V3(0f, 0f, 0f), V3(0f, 0.014f, 0f), listOf(0f to 0.031f, 0.3f to 0.034f, 1f to 0.034f), C.LATON, null, 20)
        trazo(lo, f, TrazoOrden.circulo(V3(0f, 0.014f, 0f), V3(0f, 1f, 0f), 0.034f, 18, 3.2f, 6.2f), 0.004f, lerp(C.LATON, Color.White, 0.35f), V3(0f, 1f, -0.4f), 0.9f)
        disco(lo, f, V3(0f, 0.014f, 0f), V3(0f, 1f, 0f), 0.03f, K.LATON_OSC, 20)
        rosa(lo, f)
        cristal(lo, f)
    }

    /** La tapa, abierta hacia atrás sobre su bisagra: se le ve el lado de dentro, de latón más claro y gastado. */
    private fun DrawScope.tapaDeLaBrujula(lo: LuzObjetoOrden, f: MarcoOrden) {
        val tp = f.mas(0f, 0.014f, 0.034f, vuelco = -1.95f)
        revolucion(lo, tp, V3(0f, 0f, -0.034f), V3(0f, 0.006f, -0.034f), listOf(0f to 0.034f, 1f to 0.033f), C.LATON, lerp(C.LATON, C.ARENA, 0.2f), 18)
        disco(lo, tp, V3(0f, -0.0005f, -0.034f), V3(0f, -1f, 0f), 0.029f, lerp(C.LATON, Color.White, 0.12f), 16)
        raya(tp, V3(-0.018f, -0.001f, -0.05f), V3(0.012f, -0.001f, -0.02f), 0.001f, K.LATON_OSC.copy(alpha = 0.7f))
        cilindro(lo, f, V3(-0.008f, 0.014f, 0.035f), V3(0.008f, 0.014f, 0.035f), 0.003f, K.LATON_OSC, null, 6)
    }

    /** La rosa: el papel viejo con su aro, ocho marcas (cuatro largas), la punta carmesí del norte y la aguja que tiembla. */
    private fun DrawScope.rosa(lo: LuzObjetoOrden, f: MarcoOrden) {
        val y = 0.0105f
        disco(lo, f, V3(0f, y, 0f), V3(0f, 1f, 0f), 0.026f, K.PAPEL_VIEJO, 20)
        val tinta = lerp(K.TINTA, C.MADERA, 0.2f)
        for (i in 0 until 16) {
            val a = i * 0.3927f; val r0 = if (i % 4 == 0) 0.016f else if (i % 2 == 0) 0.019f else 0.022f
            raya(f, V3(r0 * cos(a), y + 0.0003f, r0 * sin(a)), V3(0.025f * cos(a), y + 0.0003f, 0.025f * sin(a)), if (i % 4 == 0) 0.0016f else 0.0009f, tinta.copy(alpha = 0.85f))
        }
        cara(lo, f, listOf(V3(-0.003f, y + 0.0004f, 0.019f), V3(0.003f, y + 0.0004f, 0.019f), V3(0f, y + 0.0004f, 0.026f)), V3(0f, 1f, 0f), C.CARMESI)
        // La aguja que tiembla: su sombra de un instante antes, tenue, y la de ahora.
        aguja(lo, f, 1.95f, 0.3f)
        aguja(lo, f, 1.72f, 1f)
        disco(lo, f, V3(0f, y + 0.0022f, 0f), V3(0f, 1f, 0f), 0.0025f, C.LATON, 8)
    }

    private fun DrawScope.aguja(lo: LuzObjetoOrden, f: MarcoOrden, ang: Float, alfa: Float) {
        val y = 0.012f; val d = V3(cos(ang), 0f, sin(ang)); val l = V3(-d.z, 0f, d.x)
        val norte = listOf(l * 0.003f + V3(0f, y, 0f), d * 0.021f + V3(0f, y, 0f), l * -0.003f + V3(0f, y, 0f))
        val sur = listOf(l * 0.003f + V3(0f, y, 0f), d * -0.019f + V3(0f, y, 0f), l * -0.003f + V3(0f, y, 0f))
        cara(lo, f, norte, V3(0f, 1f, 0f), C.CARMESI, alfa, 0.1f)
        cara(lo, f, sur, V3(0f, 1f, 0f), K.ACERO, alfa, 0.1f)
    }

    /** El cristal: un velo, las rayas finas que le hizo el páramo y el brillo. */
    private fun DrawScope.cristal(lo: LuzObjetoOrden, f: MarcoOrden) {
        disco(lo, f, V3(0f, 0.0135f, 0f), V3(0f, 1f, 0f), 0.03f, C.VIDRIO, 18, alfa = 0.2f)
        val a = az("cristal")
        repeat(6) {
            val c = V3(a.entreF(-0.016f, 0.016f), 0.0137f, a.entreF(-0.016f, 0.016f)); val ang = a.entreF(0f, 3.14f); val l = a.entreF(0.008f, 0.02f)
            raya(f, c + V3(-cos(ang) * l / 2f, 0f, -sin(ang) * l / 2f), c + V3(cos(ang) * l / 2f, 0f, sin(ang) * l / 2f), 0.0007f, Color.White.copy(alpha = 0.55f))
        }
        trazo(lo, f, TrazoOrden.circulo(V3(0f, 0.014f, 0f), V3(0f, 1f, 0f), 0.022f, 8, 3.5f, 4.5f), 0.003f, Color.White, V3(0f, 1f, -0.3f), 0.5f)
    }

    // =================================================================
    //  La copia del Código
    // =================================================================

    /**
     * La copia del Código: «cuarenta artículos y cabe en una hoja». Una sola hoja de papel de trapo, enrollada a medias por
     * el canto de atrás y sujeta delante con un canto del páramo: arriba, el candil de la orden dibujado a pluma; debajo, dos
     * columnas de veinte renglones sin letras, cada uno con su marca carmesí al empezar.
     */
    fun DrawScope.codigo(lo: LuzObjetoOrden, o: V3) {
        val base = MarcoOrden.de(o, -0.12f, 2.2f * MAS)
        val f = base.mas(0f, 0.004f, 0f, vuelco = 0.62f)
        val cuerpo = listOf(V3(-0.075f, 0f, -0.1f), V3(0.075f, 0f, -0.1f), V3(0.075f, 0.05f, 0.075f), V3(-0.075f, 0.05f, 0.075f)).map { f.w(it) }
        sombra(lo, listOf(V3(-0.075f, 0f, -0.1f), V3(0.075f, 0f, -0.1f), V3(0.075f, 0f, -0.04f), V3(-0.075f, 0f, -0.04f)).map { f.w(it) }, cuerpo, o.y, tapa(o), 0.25f)
        rollo(lo, f, detras = true)
        cara(lo, f, listOf(V3(-0.075f, 0f, -0.105f), V3(0.075f, 0f, -0.103f), V3(0.075f, 0f, 0.045f), V3(-0.075f, 0f, 0.045f)), V3(0f, 1f, 0f), K.PAPEL_VIEJO, claro = 0.12f)
        manchas(lo, f)
        cabecera(lo, f)
        articulos(lo, f, az("codigo"))
        rollo(lo, f, detras = false)
        canto(lo, f.mas(-0.055f, 0f, -0.088f))
    }

    /** Lo enrollado del canto de atrás: media vuelta y algo más, con su revés a la vista; [detras] pinta la parte de atrás. */
    private fun DrawScope.rollo(lo: LuzObjetoOrden, f: MarcoOrden, detras: Boolean) {
        val r = 0.022f; val c = V3(0f, r, 0.045f)
        val pasos = 10
        for (i in 0 until pasos) {
            val a0 = -1.5708f + i * 4.4f / pasos; val a1 = a0 + 4.4f / pasos
            val atras = cos((a0 + a1) / 2f) > 0f
            if (atras != detras) continue
            val p0 = c + V3(0f, r * sin(a0), r * cos(a0)); val p1 = c + V3(0f, r * sin(a1), r * cos(a1))
            val n = V3(0f, sin((a0 + a1) / 2f), cos((a0 + a1) / 2f))
            cara(lo, f, listOf(V3(-0.075f, p0.y, p0.z), V3(0.075f, p0.y, p0.z), V3(0.075f, p1.y, p1.z), V3(-0.075f, p1.y, p1.z)), n, if (i > 5) lerp(K.PAPEL_VIEJO, C.ARENA, 0.25f) else K.PAPEL_VIEJO)
        }
    }

    /** Las manchas del uso: una de agua y el sucio de los dedos en el canto. */
    private fun DrawScope.manchas(lo: LuzObjetoOrden, f: MarcoOrden) {
        disco(lo, f, V3(0.045f, 0.0004f, 0.02f), V3(0f, 1f, 0f), 0.018f, K.MANCHA, 12, alfa = 0.22f)
        disco(lo, f, V3(0.06f, 0.0004f, -0.09f), V3(0f, 1f, 0f), 0.012f, lerp(K.MANCHA, C.HOLLIN, 0.3f), 10, alfa = 0.2f)
    }

    /** El candil de la orden dibujado arriba de la hoja: el vaso y la llama, a pluma, en latón y carmesí. */
    private fun DrawScope.cabecera(lo: LuzObjetoOrden, f: MarcoOrden) {
        val y = 0.0006f; val c = V3(0f, y, -0.088f)
        cara(lo, f, listOf(c + V3(-0.009f, 0f, 0.006f), c + V3(0.009f, 0f, 0.006f), c + V3(0.012f, 0f, -0.002f), c + V3(-0.012f, 0f, -0.002f)), V3(0f, 1f, 0f), lerp(C.LATON, C.MADERA, 0.3f))
        cara(lo, f, listOf(c + V3(-0.004f, 0f, -0.003f), c + V3(0.004f, 0f, -0.003f), c + V3(0f, 0f, -0.016f)), V3(0f, 1f, 0f), C.CARMESI)
        trazo(lo, f, listOf(V3(-0.05f, y, -0.075f), V3(-0.016f, y, -0.075f)), 0.0014f, K.TINTA, V3(0f, 1f, 0f), 0.6f)
        trazo(lo, f, listOf(V3(0.016f, y, -0.075f), V3(0.05f, y, -0.075f)), 0.0014f, K.TINTA, V3(0f, 1f, 0f), 0.6f)
    }

    /** Los cuarenta artículos: dos columnas de veinte renglones, cada uno con su marca carmesí; los últimos se meten en el rollo. */
    private fun DrawScope.articulos(lo: LuzObjetoOrden, f: MarcoOrden, a: Azar) {
        val tinta = lerp(K.TINTA, C.MADERA, 0.2f)
        for (col in 0..1) {
            val x0 = if (col == 0) -0.066f else 0.006f
            for (i in 0 until 20) {
                val z = -0.066f + i * 0.0058f; if (z > 0.043f) continue
                val largo = if (i % 5 == 4) a.entreF(0.02f, 0.035f) else a.entreF(0.046f, 0.058f)
                raya(f, V3(x0, 0.0006f, z), V3(x0 + 0.004f, 0.0006f, z), 0.0024f, C.CARMESI.copy(alpha = 0.8f))
                raya(f, V3(x0 + 0.007f, 0.0006f, z), V3(x0 + 0.007f + largo, 0.0006f, z + a.entreF(-0.0006f, 0.0006f)), 0.0015f, tinta.copy(alpha = 0.6f))
            }
        }
    }

    /** El canto que sujeta la hoja: una piedra rodada del páramo, gris rojiza, con su lado de luz y unas pintas. */
    private fun DrawScope.canto(lo: LuzObjetoOrden, f: MarcoOrden) {
        revolucion(lo, f, V3(0f, 0f, 0f), V3(0f, 0.03f, 0.002f), listOf(0f to 0.018f, 0.25f to 0.027f, 0.6f to 0.025f, 0.85f to 0.016f, 1f to 0.003f), K.PIEDRA_CANTO, null, 12)
        val a = az("canto")
        repeat(5) { disco(lo, f, V3(a.entreF(-0.016f, 0.016f), a.entreF(0.008f, 0.022f), -0.024f), V3(0f, 0.3f, -1f), 0.0025f, lerp(K.PIEDRA_CANTO, C.HOLLIN, 0.4f), 6, alfa = 0.6f) }
    }

    private fun Azar.entreF(a: Float, b: Float): Float = a + (b - a) * (entero(10000) / 10000f)
}
