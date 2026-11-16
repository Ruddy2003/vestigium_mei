package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.brillo
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.caja
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.cara
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.caraDegradada
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.cil
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.disco
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.rayaLuz
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.sombra
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.trazo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los cuatro objetos grandes de la residente, junto a la punta de la bancada ([ObjetosRefugio.grande]). Cada uno recibe
 * su pie en el suelo ([suelo], en la sala), el canto donde se recuesta ([canto]: 5 cm por debajo de la tapa, en la punta)
 * y el pie en el lienzo para su [Cosa]. Como la punta de la bancada se ve casi de filo, la manta cae por el canto de
 * delante, que se ve de frente, y el mono, doblado, se queda de pie en la esquina, recostado en lo de atrás; el pie del
 * suelo queda en el borde de abajo del cuadro, así que lo que pisa el suelo (la llave, el tocadiscos) sube lo bastante
 * para verse entero (la llave pisa un palmo más al fondo).
 */
internal object GrandesRefugio {
    private val C = ColorRefugio
    private val K = CamRefugio
    private val PI_F = PI.toFloat()

    // ---- Los pocos colores propios: mezclas de la paleta del tema.
    private val HIERRO = lerp(C.ACERO, C.HIERRO, 0.4f)                          // la llave, de hierro forjado
    private val CINTA = lerp(C.TINTA, C.ROJO, 0.32f)                             // la cinta del mango, negra con algo de rojo viejo
    private val MONO = C.AZUL                                                    // la tela gruesa del mono
    private val LANA = lerp(lerp(C.ACERO, C.HORMIGON, 0.5f), C.TINTA, 0.12f)     // la manta gris
    private val MADERA = lerp(C.COBRE, C.TINTA, 0.2f)                            // la caja del tocadiscos (la del pequeño, más roja)
    private val REJILLA = lerp(C.PAPEL, C.COBRE, 0.35f)                          // la tela del altavoz

    private fun tapaDe(canto: V3) = canto.y + 0.05f

    // =================================================================
    //  La llave de válvulas
    // =================================================================

    /**
     * La llave de válvulas: «de hierro, larga como un brazo, con el mango forrado de cinta para que no resbale; fue de su
     * maestro… con ella se abren las ruedas rojas de las bombas». De pie en el suelo y recostada en el canto de la punta de la
     * bancada, con la cabeza arriba: la barra cuadrada de hierro forjado con su óxido y el brillo del uso, la cabeza en F (dos
     * dientes para agarrar el aro de una rueda) y el mango con las vueltas de cinta negra y el cabo suelto. Algo más larga que
     * un brazo (unos 95 cm), para que la cabeza asome por encima del canto **[Diseño propio]**.
     */
    fun llave(v: VistaRefugio, suelo: V3, canto: V3, pie: Offset): Cosa {
        // El pie, un palmo más al fondo que el que da el cuadro (que cae en el borde de abajo), para que el mango se vea;
        // la cabeza, en el canto de arriba de la punta.
        val pieLlave = suelo + V3(0f, 0f, 0.3f); val arriba = canto + V3(0.012f, 0.035f, 0.1f)
        val d = arriba - pieLlave; val eje = K.unidad(d); val largo = K.largo(d) + 0.09f
        val f = MarcoObjeto(pieLlave, 0f, 1f)
        val ojo = K.unidad(V3(-pieLlave.x, K.OJO - pieLlave.y - largo * 0.5f, -pieLlave.z))
        var lado = K.unidad(K.cruz(eje, ojo)); if (lado.x < 0f) lado = lado * -1f
        fun en(s: Float, l: Float = 0f) = eje * s + lado * l
        val dientes = listOf(en(largo, 0f), en(largo + 0.02f, 0.105f), en(largo - 0.085f, 0f), en(largo - 0.07f, 0.1f))
        val cuerpo = listOf(en(0f, -0.03f), en(0f, 0.03f), en(largo, -0.03f)) + dientes
        val forma = TrazoObjeto.silueta(f, cuerpo)
        return Cosa(forma, pie, bajoElFollaje = false) {
            sombra(v, f, TrazoObjeto.circulo(V3(0f, 0f, 0f), V3(0f, 1f, 0f), 0.04f, 8), cuerpo, 0f, null, 0.5f)
            // Donde descansa en el canto: una sombrita en la madera.
            drawCircle(C.SOMBRA.copy(alpha = 0.35f), 0.045f * K.k(arriba), K.p(arriba + V3(-0.01f, -0.02f, 0f)), blendMode = BlendMode.Multiply)
            barraDeHierro(v, f, eje, lado, largo)
            mango(v, f, eje, lado)
            cabeza(v, f, eje, lado, largo)
        }
    }

    /** La barra: cuadrada, de hierro, con el filo del lado de la luz, el óxido a trozos y el brillo gastado del medio. */
    private fun DrawScope.barraDeHierro(v: VistaRefugio, f: MarcoObjeto, eje: V3, lado: V3, largo: Float) {
        cil(v, f, eje * 0.3f, eje * (largo - 0.1f), 0.025f, HIERRO, lados = 4)
        val izq = TrazoObjeto.luz(v, f, eje * 0.6f, lado * -1f, C.ACERO); val der = TrazoObjeto.luz(v, f, eje * 0.6f, lado, C.ACERO)
        val s = if (TrazoObjeto.lum(izq) > TrazoObjeto.lum(der)) -1f else 1f
        brillo(v, f, eje * 0.32f + lado * (0.014f * s), eje * (largo - 0.11f) + lado * (0.014f * s), 0.007f, C.ACERO, lado * s, 0.8f)
        brillo(v, f, eje * 0.45f + lado * (0.004f * s), eje * 0.62f + lado * (0.004f * s), 0.008f, C.ACERO, lado * s, 0.9f)
        val az = Azar(Semilla.de("refugio", "llave", "oxido"))
        repeat(6) { val a = az.entre(0.34f, largo - 0.12f); val l = az.entre(0.02f, 0.06f); val off = az.entre(-0.01f, 0.01f)
            rayaLuz(v, f, eje * a + lado * off, eje * (a + l) + lado * off, 0.016f, lerp(C.OXIDO, C.NARANJA, 0.3f), lado * s, 0.6f) }
    }

    /** El mango: las vueltas de cinta negra, en espiral, con su canto más claro, y el cabo suelto. */
    private fun DrawScope.mango(v: VistaRefugio, f: MarcoObjeto, eje: V3, lado: V3) {
        cil(v, f, eje * 0.0f, eje * 0.32f, 0.034f, CINTA, lados = 8)
        for (k in 0..10) { val c = eje * (0.02f + 0.028f * k)
            rayaLuz(v, f, c + lado * 0.03f + eje * -0.007f, c - lado * 0.03f + eje * 0.011f, 0.006f, lerp(CINTA, C.PAPEL, 0.35f), V3(0f, 0f, 0f), 0.85f) }
        val c = eje * 0.32f
        cara(v, f, listOf(c + lado * 0.03f, c + lado * 0.031f + eje * 0.02f, c + lado * 0.075f + eje * 0.0f, c + lado * 0.062f + eje * -0.026f), V3(0f, 0f, -1f), lerp(CINTA, C.PAPEL, 0.2f))
        // El extremo del mango, con su agujero y un aro de alambre para colgarla.
        Trazo3.run { aro(f.w(eje * -0.01f), f.n(lado), 0.022f, 0.005f, TrazoObjeto.luz(v, f, eje * -0.01f, lado, C.ACERO), 12) }
    }

    /** La cabeza en F: el tocho forjado y los dos dientes que agarran el aro de la rueda, con sus golpes y su filo. */
    private fun DrawScope.cabeza(v: VistaRefugio, f: MarcoObjeto, eje: V3, lado: V3, largo: Float) {
        cil(v, f, eje * (largo - 0.12f), eje * largo, 0.032f, HIERRO, lerp(HIERRO, C.ACERO, 0.4f), lados = 4)
        for ((a, b) in listOf(eje * largo to eje * (largo + 0.02f) + lado * 0.105f, eje * (largo - 0.085f) to eje * (largo - 0.07f) + lado * 0.1f)) {
            rayaLuz(v, f, a, b, 0.048f, HIERRO, V3(0f, 0f, -1f), punta = StrokeCap.Square)
            rayaLuz(v, f, a + eje * 0.015f, b + eje * 0.015f, 0.009f, lerp(HIERRO, Color.White, 0.35f), eje, 0.85f)
            rayaLuz(v, f, b - lado * 0.016f + eje * -0.016f, b + eje * 0.022f, 0.032f, lerp(HIERRO, C.OXIDO, 0.3f), V3(0f, 0f, -1f), punta = StrokeCap.Square)
        }
        brillo(v, f, eje * (largo - 0.11f) + lado * 0.016f, eje * (largo - 0.01f) + lado * 0.016f, 0.006f, C.ACERO, lado, 0.8f)
    }

    // =================================================================
    //  El mono azul
    // =================================================================

    /**
     * El mono azul: «azul, de una tela gruesa…, con un número grande pintado en la espalda y un bolsillo para el contador…;
     * cada uno lo distingue por los remiendos». Doblado como se dobla una camisa, con las mangas y las perneras metidas
     * detrás, y dejado de pie en la esquina de la bancada, recostado en lo de atrás (el tocadiscos y la botella), para que se
     * vea su delantero: el cuello vuelto, la tira de botones, el bolsillo del contador con su tapeta, el cinturón con su
     * hebilla, el puño de una manga que asoma, el grueso de los dobleces en el canto, y los remiendos de otros azules con sus
     * puntadas. **Sin el número** (el cuadro no lleva cifras) y **nunca con forma de cuerpo**: un bulto doblado, sin hombros
     * ni piernas **[Diseño propio]**.
     */
    fun mono(v: VistaRefugio, suelo: V3, canto: V3, pie: Offset): Cosa {
        val tapa = tapaDe(canto); val frente = TrazoObjeto.cantoDelante(V3(canto.x - 0.05f, tapa, canto.z))
        val f = MarcoObjeto(V3(canto.x - 0.17f, tapa, frente + 0.17f), 0.14f, 1.15f)
        val b = BultoMono()
        val forma = TrazoObjeto.silueta(f, listOf(b.q(-b.ancho, 0f), b.q(b.ancho, 0f), b.q(b.ancho, b.alto), b.q(-b.ancho, b.alto), b.q(0f, b.alto + 0.02f, 0.03f)))
        return Cosa(forma, pie, bajoElFollaje = false) {
            val dentro = TrazoObjeto.tapaBajo(V3(canto.x - 0.2f, tapa, canto.z))
            sombra(v, f, listOf(b.q(-b.ancho, 0f), b.q(b.ancho, 0f), b.q(b.ancho, 0f, b.grueso), b.q(-b.ancho, 0f, b.grueso)), listOf(b.q(-b.ancho, b.alto), b.q(b.ancho, b.alto), b.q(b.ancho, b.alto, b.grueso), b.q(-b.ancho, b.alto, b.grueso)), tapa, dentro, 0.5f)
            bultoDelMono(v, f, b)
            delanteroDelMono(v, f, b)
        }
    }

    /**
     * Las medidas del mono doblado, recostado [inclina] radianes desde la vertical: [q] da el punto a [u] de su eje, [s] metros
     * subiendo por él y [d] metros hacia delante (su grueso, [grueso]).
     */
    private class BultoMono {
        val ancho = 0.15f; val alto = 0.3f; val grueso = 0.065f; val inclina = 0.62f
        private val si = sin(inclina); private val ci = cos(inclina)
        fun q(u: Float, s: Float, d: Float = 0f) = V3(u, s * ci + d * si, s * si - d * ci + 0.04f)
        /** La cara de delante mira hacia aquí. */
        val n = V3(0f, si, -ci)
    }

    /** El bulto: el canto de los dobleces (a la derecha), los dobleces redondos de abajo y de arriba, y la cara de delante. */
    private fun DrawScope.bultoDelMono(v: VistaRefugio, f: MarcoObjeto, b: BultoMono) {
        val w = b.ancho; val h = b.alto; val g = b.grueso
        // El canto de la derecha: las capas de la tela doblada.
        caraDegradada(v, f, listOf(b.q(w, 0.01f, 0f), b.q(w, h - 0.01f, 0f), b.q(w, h - 0.01f, g), b.q(w, 0.01f, g)), V3(1f, 0f, 0f), lerp(MONO, C.TINTA, 0.25f), b.q(w, h, g), b.q(w, 0f, g))
        for (k in 1..3) rayaLuz(v, f, b.q(w + 0.001f, 0.015f, g * k / 4f), b.q(w + 0.001f, h - 0.015f, g * k / 4f), 0.004f, lerp(MONO, if (k % 2 == 0) C.PAPEL else C.TINTA, 0.3f), V3(1f, 0f, 0f), 0.8f)
        // Los dobleces de abajo y de arriba, redondos.
        cil(v, f, b.q(-w + 0.012f, 0.012f, g * 0.5f), b.q(w - 0.003f, 0.012f, g * 0.5f), g * 0.5f, lerp(MONO, C.TINTA, 0.1f), lerp(MONO, C.TINTA, 0.3f), 12)
        cil(v, f, b.q(-w + 0.02f, h - 0.012f, g * 0.45f), b.q(w - 0.02f, h - 0.012f, g * 0.45f), g * 0.45f, MONO, lerp(MONO, C.TINTA, 0.3f), 12)
        // La cara de delante, con la luz que cae de arriba abajo, y los costados redondeados de las mangas metidas.
        val cara = listOf(b.q(-w, 0.02f, g), b.q(w, 0.02f, g), b.q(w - 0.004f, h - 0.02f, g), b.q(-w + 0.01f, h - 0.02f, g))
        caraDegradada(v, f, cara, b.n, MONO, b.q(0f, h, g), b.q(0f, 0f, g))
        for (u in listOf(-w + 0.012f, w - 0.012f)) rayaLuz(v, f, b.q(u, 0.03f, g + 0.001f), b.q(u, h - 0.03f, g + 0.001f), 0.012f, lerp(MONO, C.TINTA, 0.28f), b.n, 0.7f)
        rayaLuz(v, f, b.q(-w + 0.02f, h - 0.005f, g * 0.9f), b.q(w - 0.02f, h - 0.005f, g * 0.9f), 0.006f, lerp(MONO, C.PAPEL, 0.3f), V3(0f, 1f, -0.3f), 0.7f)
    }

    /** El delantero: el cuello, la tira de botones, el bolsillo del contador, el cinturón, el puño que asoma y los remiendos. */
    private fun DrawScope.delanteroDelMono(v: VistaRefugio, f: MarcoObjeto, b: BultoMono) {
        val h = b.alto; val d = b.grueso + 0.002f; val n = b.n
        fun q(u: Float, s: Float, mas: Float = 0f) = b.q(u, s, d + mas)
        // El cuello vuelto: la tira de atrás y las dos puntas.
        cara(v, f, listOf(q(-0.07f, h - 0.012f), q(0.07f, h - 0.012f), q(0.06f, h - 0.035f), q(-0.06f, h - 0.035f)), n, lerp(MONO, C.TINTA, 0.35f))
        for (lado in listOf(-1f, 1f)) cara(v, f, listOf(q(lado * 0.004f, h - 0.03f, 0.002f), q(lado * 0.075f, h - 0.018f, 0.002f), q(lado * 0.05f, h - 0.085f, 0.003f)), n, lerp(MONO, C.PAPEL, 0.12f))
        // La tira de los botones, hasta abajo.
        rayaLuz(v, f, q(0.012f, h - 0.05f, 0.001f), q(0.012f, 0.03f, 0.001f), 0.006f, lerp(MONO, C.TINTA, 0.45f), n)
        for (k in 0..3) disco(v, f, q(0f, h - 0.1f - 0.052f * k, 0.002f), n, 0.0075f, lerp(C.ACERO, C.LATON, 0.5f), 8)
        // El bolsillo del contador: el cuerpo, la tapeta y su botón.
        val bol = listOf(q(-0.125f, 0.135f, 0.001f), q(-0.035f, 0.135f, 0.001f), q(-0.035f, 0.215f, 0.001f), q(-0.125f, 0.215f, 0.001f))
        cara(v, f, bol, n, lerp(MONO, C.TINTA, 0.12f)); puntadas(v, f, bol, n)
        cara(v, f, listOf(q(-0.13f, 0.2f, 0.002f), q(-0.03f, 0.2f, 0.002f), q(-0.035f, 0.228f, 0.002f), q(-0.125f, 0.228f, 0.002f)), n, lerp(MONO, C.TINTA, 0.32f))
        disco(v, f, q(-0.08f, 0.205f, 0.003f), n, 0.0065f, lerp(C.ACERO, C.LATON, 0.5f), 8)
        // El cinturón con su hebilla de chapa.
        cara(v, f, listOf(q(-0.15f, 0.06f, 0.001f), q(0.15f, 0.06f, 0.001f), q(0.15f, 0.088f, 0.001f), q(-0.15f, 0.088f, 0.001f)), n, lerp(MONO, C.TINTA, 0.3f))
        cara(v, f, listOf(q(0.022f, 0.055f, 0.003f), q(0.064f, 0.055f, 0.003f), q(0.064f, 0.093f, 0.003f), q(0.022f, 0.093f, 0.003f)), n, lerp(C.ACERO, C.LATON, 0.35f))
        cara(v, f, listOf(q(0.03f, 0.063f, 0.004f), q(0.056f, 0.063f, 0.004f), q(0.056f, 0.085f, 0.004f), q(0.03f, 0.085f, 0.004f)), n, lerp(MONO, C.TINTA, 0.3f))
        // El puño de una manga, que asoma abajo a la izquierda, con su botón.
        cara(v, f, listOf(q(-0.155f, 0.012f, 0.004f), q(-0.08f, 0.012f, 0.004f), q(-0.083f, 0.05f, 0.004f), q(-0.155f, 0.05f, 0.004f)), n, lerp(MONO, C.TINTA, 0.18f))
        rayaLuz(v, f, q(-0.155f, 0.03f, 0.005f), q(-0.083f, 0.03f, 0.005f), 0.003f, lerp(MONO, C.TINTA, 0.45f), n, 0.8f)
        disco(v, f, q(-0.1f, 0.022f, 0.006f), n, 0.005f, lerp(C.ACERO, C.LATON, 0.5f), 6)
        // Los remiendos: uno grande de azul desvaído en el pecho y otro gris abajo, con sus puntadas.
        val r1 = listOf(q(0.03f, 0.13f, 0.001f), q(0.128f, 0.135f, 0.001f), q(0.125f, 0.225f, 0.001f), q(0.032f, 0.22f, 0.001f))
        cara(v, f, r1, n, lerp(MONO, C.PAPEL, 0.42f)); puntadas(v, f, r1, n)
        val r2 = listOf(q(0.045f, 0.012f, 0.001f), q(0.12f, 0.014f, 0.001f), q(0.118f, 0.05f, 0.001f), q(0.047f, 0.048f, 0.001f))
        cara(v, f, r2, n, lerp(MONO, C.HORMIGON, 0.55f)); puntadas(v, f, r2, n)
        // Las costuras de los hombros y el desgaste de los cantos.
        for (lado in listOf(-1f, 1f)) rayaLuz(v, f, q(lado * 0.075f, h - 0.02f, 0.001f), q(lado * 0.14f, h - 0.05f, 0.001f), 0.0025f, lerp(MONO, C.PAPEL, 0.35f), n, 0.7f)
    }

    /** Las puntadas de un remiendo: rayitas cortas a lo largo de su borde, de hilo claro. */
    private fun DrawScope.puntadas(v: VistaRefugio, f: MarcoObjeto, borde: List<V3>, n: V3) {
        for (i in borde.indices) { val a = borde[i]; val b = borde[(i + 1) % borde.size]
            for (k in 0..4) { val t0 = (k + 0.15f) / 5f; val t1 = (k + 0.6f) / 5f
                rayaLuz(v, f, a + (b - a) * t0 + n * 0.0008f, a + (b - a) * t1 + n * 0.0008f, 0.0022f, lerp(C.PAPEL, MONO, 0.15f), n, 0.85f) } }
    }

    // =================================================================
    //  La manta gris
    // =================================================================

    /**
     * La manta gris: «grises, de una lana que pica, con el escudo del refugio bordado en una esquina…; la de la residente
     * tiene un agujero quemado». Doblada y echada sobre la esquina de la punta de la bancada, cae por el canto de delante en
     * pliegues, con la esquina más baja: la lana con sus bolitas, el ribete de punto de manta, el escudo bordado (inventado:
     * partido de verde azulado y rojo, con la Puerta y su rueda en un círculo y tres ondas de agua, en hilo ocre, sin letras
     * ni cifras) y el agujero quemado, con su cerco chamuscado **[Diseño propio]**.
     */
    fun manta(v: VistaRefugio, suelo: V3, canto: V3, pie: Offset): Cosa {
        val tapa = tapaDe(canto); val frente = TrazoObjeto.cantoDelante(V3(canto.x - 0.05f, tapa, canto.z))
        val f = MarcoObjeto(V3(canto.x, tapa, frente), 0f, 1.1f)
        val caida = Caida()
        val forma = TrazoObjeto.silueta(f, caida.abajo + caida.arriba + listOf(V3(-0.36f, 0.035f, 0.22f), V3(0.03f, 0.035f, 0.22f)))
        return Cosa(forma, pie, bajoElFollaje = false) {
            val dentro = TrazoObjeto.tapaBajo(V3(canto.x - 0.2f, tapa, canto.z))
            sombra(v, f, listOf(V3(-0.36f, 0f, 0f), V3(0.03f, 0f, 0f), V3(0.03f, 0f, 0.22f), V3(-0.36f, 0f, 0.22f)), listOf(V3(-0.36f, 0.035f, 0.02f), V3(0.03f, 0.035f, 0.02f), V3(0.03f, 0.035f, 0.22f), V3(-0.36f, 0.035f, 0.22f)), tapa, dentro, 0.5f)
            // Lo que cuelga echa su sombra en el frente de la bancada, detrás: una franja oscura junto a su borde de arriba.
            drawPath(TrazoObjeto.silueta(f, caida.arriba.map { it + V3(0.02f, -0.02f, 0.02f) } + caida.abajo.map { it + V3(0.03f, 0.03f, 0.02f) }), C.SOMBRA.copy(alpha = 0.3f), blendMode = BlendMode.Multiply)
            encimaDeLaTapa(v, f)
            pliegues(v, f, caida)
            quemado(v, f, V3(-0.27f, -0.07f, -0.016f))
            escudo(v, f, V3(-0.085f, -0.27f, -0.03f), 1.2f)
        }
    }

    /**
     * La forma de lo que cuelga por el canto: [arriba], el canto de la tapa de izquierda a derecha; [abajo], el borde de
     * abajo de izquierda a derecha, que baja hasta la esquina (a la derecha, la más baja) y sube por la punta.
     */
    private class Caida {
        val arriba: List<V3> = (0..13).map { i -> V3(-0.36f + 0.395f * i / 13f, 0.02f, -0.012f) }
        val abajo: List<V3> = (0..13).map { i -> val t = i / 13f
            val y = if (t < 0.88f) -0.12f - 0.47f * Math.pow((t / 0.88f).toDouble(), 1.7).toFloat() + 0.018f * sin(t * 17f) else -0.59f + (t - 0.88f) / 0.12f * 0.22f
            V3(-0.36f + 0.395f * t, y, -0.02f - 0.016f * sin(t * 11f)) }
    }

    /** Lo que queda sobre la tapa: la manta doblada, con su doblez redondo atrás y el ribete. */
    private fun DrawScope.encimaDeLaTapa(v: VistaRefugio, f: MarcoObjeto) {
        val arriba = V3(0f, 1f, 0f); val y = 0.034f
        caraDegradada(v, f, listOf(V3(-0.36f, y, -0.012f), V3(0.035f, y, -0.012f), V3(0.03f, y, 0.22f), V3(-0.35f, y, 0.22f)), arriba, LANA, V3(-0.2f, y, 0.22f), V3(-0.2f, y, 0f))
        cil(v, f, V3(-0.35f, 0.018f, 0.22f), V3(0.03f, 0.018f, 0.22f), 0.018f, lerp(LANA, C.PAPEL, 0.1f), lados = 10)
        lana(v, f, listOf(V3(-0.36f, y, -0.012f), V3(0.035f, y, -0.012f), V3(0.03f, y, 0.2f), V3(-0.35f, y, 0.2f)), arriba, "tapa")
    }

    /**
     * Lo que cuelga: tiras de arriba abajo con su luz, cada una un poco girada (la cresta y el valle de cada pliegue, de
     * anchos distintos), la vuelta del canto, la lana, la línea de cada valle y el ribete de punto de manta del borde.
     */
    private fun DrawScope.pliegues(v: VistaRefugio, f: MarcoObjeto, c: Caida) {
        val az = Azar(Semilla.de("refugio", "manta", "pliegues"))
        val giros = FloatArray(c.arriba.size); var fase = 0f
        for (i in giros.indices) { fase += az.entre(0.7f, 1.3f); giros[i] = 0.85f * sin(fase) }
        for (i in 0 until c.arriba.size - 1) {
            val a = c.arriba[i]; val b = c.arriba[i + 1]; val p = c.abajo[i]; val q = c.abajo[i + 1]
            val g = (giros[i] + giros[i + 1]) / 2f
            caraDegradada(v, f, listOf(a, b, q, p), V3(g, 0.12f, -1f), LANA, K.medio(a, b), K.medio(p, q))
            // El valle de cada pliegue, más hondo, y la cresta, más clara: así se leen aunque la luz llegue pareja.
            val pliegue = listOf(a, b, q, p).map { f.p(it) }
            if (g < 0f) drawPath(poligono(pliegue), C.SOMBRA.copy(alpha = 0.35f * -g), blendMode = BlendMode.Multiply)
            else drawPath(poligono(pliegue), lerp(LANA, C.PAPEL, 0.6f).copy(alpha = 0.16f * g))
            if (giros[i] > 0f && giros[i + 1] <= 0f) trazo(v, f, listOf(b, b + (q - b) * 0.5f + V3(0.004f, 0f, 0f), q), 0.006f, lerp(LANA, C.TINTA, 0.4f), V3(0f, 0f, -1f), 0.55f)
        }
        // La vuelta del canto, redonda (la manta dobla sobre la tapa).
        cil(v, f, V3(-0.36f, 0.012f, -0.006f), V3(0.035f, 0.012f, -0.006f), 0.022f, lerp(LANA, C.PAPEL, 0.08f), lados = 10, tapas = false)
        lana(v, f, c.arriba + c.abajo.reversed(), V3(0f, 0f, -1f), "caida")
        // El ribete: una franja más oscura por el borde de abajo, con las puntadas de punto de manta.
        trazo(v, f, c.abajo.map { it + V3(0f, 0.007f, -0.003f) }, 0.014f, lerp(LANA, C.TINTA, 0.25f), V3(0f, 0f, -1f))
        for (i in 0 until c.abajo.size - 1) for (k in 0..2) { val t = k / 3f; val p = c.abajo[i] + (c.abajo[i + 1] - c.abajo[i]) * t
            rayaLuz(v, f, p + V3(0f, 0.0f, -0.005f), p + V3(0f, 0.018f, -0.005f), 0.0025f, lerp(LANA, C.PAPEL, 0.4f), V3(0f, 0f, -1f), 0.75f) }
    }

    /** El agujero quemado en lo que cuelga: el cerco chamuscado, el hueco oscuro (se ve la bancada detrás) y los pelos quemados. */
    private fun DrawScope.quemado(v: VistaRefugio, f: MarcoObjeto, h: V3) {
        val fr = V3(0f, 0f, -1f)
        val az = Azar(Semilla.de("refugio", "manta", "agujero"))
        cara(v, f, (0 until 13).map { i -> val a = 2f * PI_F * i / 13; val r = 0.042f * az.entre(0.75f, 1.2f); h + V3(cos(a) * r, sin(a) * r, 0f) }, fr, lerp(C.COBRE, C.TINTA, 0.4f), 0.85f)
        cara(v, f, (0 until 11).map { i -> val a = 2f * PI_F * i / 11; val r = 0.023f * az.entre(0.6f, 1.25f); h + V3(cos(a) * r, sin(a) * r, -0.0005f) }, fr, lerp(C.TINTA, C.SOMBRA, 0.25f))
        for (k in 0..6) { val a = 2f * PI_F * k / 7 + 0.3f; rayaLuz(v, f, h + V3(cos(a) * 0.022f, sin(a) * 0.022f, -0.001f), h + V3(cos(a) * 0.034f, sin(a) * 0.033f, -0.001f), 0.0025f, C.TINTA, fr, 0.7f) }
    }

    /** Las bolitas y los pelos de la lana, en la forma [borde] (clara y oscura, a puntos), con una semilla fija. */
    private fun DrawScope.lana(v: VistaRefugio, f: MarcoObjeto, borde: List<V3>, n: V3, que: String) {
        val forma = CamRefugio.forma(borde.map { f.w(it) }); if (forma.isEmpty) return
        val az = Azar(Semilla.de("refugio", "manta", que))
        val xs = borde.map { it.x }; val ys = borde.map { it.y }; val zs = borde.map { it.z }
        clipPath(forma) {
            repeat(70) { val p = V3(az.entre(xs.min(), xs.max()), az.entre(ys.min(), ys.max()), az.entre(zs.min(), zs.max()))
                val claro = az.real() < 0.5f; val d = V3(az.entre(-0.008f, 0.008f), az.entre(-0.008f, 0.008f), 0f)
                rayaLuz(v, f, p, p + d, 0.0035f, lerp(LANA, if (claro) C.PAPEL else C.TINTA, 0.3f), n, 0.55f) }
        }
    }

    /**
     * El escudo bordado, en [c] (sobre lo que cuelga, mirando a delante), [k] veces el tamaño de uno de verdad: un escudo
     * partido de verde azulado y rojo, con un círculo crema (la Puerta) y su rueda, tres ondas de agua y el filete de hilo
     * ocre, en puntadas.
     */
    private fun DrawScope.escudo(v: VistaRefugio, f: MarcoObjeto, c: V3, k: Float) {
        val fr = V3(0f, 0f, -1f); val w = 0.045f * k; val h = 0.055f * k
        val borde = listOf(V3(-w, h, 0f), V3(w, h, 0f), V3(w, h * 0.1f, 0f)) + (0..8).map { i -> val t = i / 8f; V3(w * cos(PI_F * t * 0.5f) * (1f - t * 0.1f), h * 0.1f - h * 1.1f * sin(PI_F * t * 0.5f), 0f) }.drop(1) +
            (0..8).map { i -> val t = 1f - i / 8f; V3(-w * cos(PI_F * t * 0.5f) * (1f - t * 0.1f), h * 0.1f - h * 1.1f * sin(PI_F * t * 0.5f), 0f) }.drop(1) + listOf(V3(-w, h * 0.1f, 0f))
        val pts = borde.map { c + it }
        cara(v, f, pts, fr, lerp(C.CHAPA, C.PAPEL, 0.1f))
        val mitad = pts.filter { it.y <= c.y + h * 0.15f }
        cara(v, f, listOf(c + V3(-w, h * 0.15f, 0f), c + V3(w, h * 0.15f, 0f)) + mitad.filter { it.y < c.y + h * 0.1f }.sortedBy { -it.x }, fr, lerp(C.ROJO, C.PAPEL, 0.08f))
        // La Puerta: un círculo crema con su rueda.
        val p = c + V3(0f, h * 0.45f, -0.0005f)
        cara(v, f, (0..14).map { i -> val a = 2f * PI_F * i / 14; p + V3(0.022f * k * cos(a), 0.022f * k * sin(a), 0f) }, fr, C.PAPEL)
        Trazo3.run { aro(f.w(p + V3(0f, 0f, -0.0005f)), f.n(fr), 0.011f * k * f.amplia, 0.0028f * f.amplia, TrazoObjeto.luz(v, f, p, fr, lerp(C.ROJO, C.TINTA, 0.2f)), 12) }
        for (j in 0 until 4) { val a = PI_F * j / 4; rayaLuz(v, f, p + V3(0.011f * k * cos(a), 0.011f * k * sin(a), -0.001f), p - V3(0.011f * k * cos(a), 0.011f * k * sin(a), 0.001f), 0.0022f, lerp(C.ROJO, C.TINTA, 0.2f), fr) }
        // Tres ondas de agua.
        for (j in 0..2) { val y = -h * 0.25f - j * 0.011f * k
            trazo(v, f, (0..8).map { i -> val t = i / 8f; c + V3(-w * 0.6f + 1.2f * w * t, y + 0.004f * k * sin(t * 4f * PI_F), -0.001f) }, 0.0028f, C.PAPEL, fr) }
        // El filete de hilo ocre y sus puntadas.
        trazo(v, f, pts + pts.first(), 0.005f, lerp(C.AMARILLO, C.COBRE, 0.35f), fr)
        for (i in pts.indices step 2) { val a = pts[i]; rayaLuz(v, f, a + V3(0f, 0f, -0.0015f), a + (c - a) * 0.08f + V3(0f, 0f, -0.0015f), 0.002f, lerp(C.AMARILLO, C.PAPEL, 0.3f), fr, 0.8f) }
    }

    // =================================================================
    //  El tocadiscos del comedor
    // =================================================================

    /**
     * El tocadiscos del comedor: «una caja de madera con una aguja…; lo limpia cada domingo con un pincel». Hoy lo trajo a
     * la sala **[Diseño propio]**: un mueble de madera rojiza sobre cuatro patas abiertas con sus contera de latón, de pie
     * en el suelo junto a la punta de la bancada; delante, el altavoz grande con su tela y sus listones; la tapa levantada,
     * con su escuadra; dentro, el plato con su disco, el brazo con la aguja, y el pincel de limpiarlo sobre el disco. La
     * veta, los cantos gastados, un cerco de vaso. Ese día la sala no pinta su tocadiscos pequeño.
     */
    fun tocadiscos(v: VistaRefugio, suelo: V3, canto: V3, pie: Offset): Cosa {
        val f = MarcoObjeto(suelo + V3(0.27f, 0f, 0.1f), 0.45f, 1f)
        val x0 = -0.26f; val x1 = 0.26f; val z0 = -0.18f; val z1 = 0.18f; val y0 = 0.32f; val y1 = 0.58f
        val tapaAng = 1.35f; val tapaDir = V3(0f, sin(tapaAng), cos(tapaAng)); val tapaFondo = 0.36f
        val esqueleto = listOf(V3(x0, 0f, z0), V3(x1, 0f, z0), V3(x1, 0f, z1), V3(x0, 0f, z1), V3(x0, y1, z0), V3(x1, y1, z0), V3(x1, y1, z1) + tapaDir * tapaFondo, V3(x0, y1, z1) + tapaDir * tapaFondo)
        val forma = TrazoObjeto.silueta(f, esqueleto)
        return Cosa(forma, pie, bajoElFollaje = false) {
            sombra(v, f, listOf(V3(x0, 0f, z0), V3(x1, 0f, z0), V3(x1, 0f, z1), V3(x0, 0f, z1)), esqueleto, 0f, null, 0.45f)
            for ((x, z) in listOf(x1 to z1, x0 to z1, x1 to z0, x0 to z0)) pata(v, f, x, z, y0)
            caja(v, f, x0, x1, y0, y1, z0, z1, MADERA, lerp(MADERA, C.TINTA, 0.45f))
            veta(v, f, x0, x1, y0, y1, z0)
            altavoz(v, f, x0, x1, y0, y1, z0)
            plato(v, f, y1)
            tapaLevantada(v, f, x0, x1, y1, z1, tapaDir, tapaFondo)
        }
    }

    /** Una pata torneada, abierta hacia fuera, con su contera de latón. */
    private fun DrawScope.pata(v: VistaRefugio, f: MarcoObjeto, x: Float, z: Float, y0: Float) {
        val arriba = V3(x * 0.88f, y0, z * 0.82f); val abajo = V3(x * 1.06f, 0.03f, z * 1.05f)
        cil(v, f, abajo, arriba, 0.017f, lerp(MADERA, C.TINTA, 0.2f), lados = 8)
        cil(v, f, abajo + V3(0f, -0.03f, 0f), abajo, 0.013f, C.LATON, lerp(C.LATON, Color.White, 0.3f), lados = 8)
    }

    /** La veta de la madera en el frente y el costado, los cantos gastados y un cerco de vaso. */
    private fun DrawScope.veta(v: VistaRefugio, f: MarcoObjeto, x0: Float, x1: Float, y0: Float, y1: Float, z0: Float) {
        val az = Azar(Semilla.de("refugio", "tocadiscos", "veta"))
        for (k in 0..5) { val y = y0 + 0.02f + (y1 - y0 - 0.04f) * k / 5f + az.entre(-0.008f, 0.008f)
            rayaLuz(v, f, V3(x0 - 0.0008f, y, z0 + 0.01f), V3(x0 - 0.0008f, y + az.entre(-0.01f, 0.01f), -z0 - 0.01f), 0.002f, lerp(MADERA, C.TINTA, 0.35f), V3(-1f, 0f, 0f), 0.6f) }
        rayaLuz(v, f, V3(x0, y1, z0), V3(x1, y1, z0), 0.006f, lerp(MADERA, C.PAPEL, 0.3f), V3(0f, 0.7f, -0.7f), 0.8f)
        rayaLuz(v, f, V3(x0, y0, z0), V3(x0, y1, z0), 0.005f, lerp(MADERA, C.PAPEL, 0.25f), V3(-0.7f, 0f, -0.7f), 0.7f)
        Trazo3.run { aro(f.w(V3(x0 - 0.001f, y0 + 0.17f, 0.05f)), f.n(-1f, 0f, 0f), 0.03f, 0.004f, TrazoObjeto.luz(v, f, V3(x0, y0 + 0.17f, 0.05f), V3(-1f, 0f, 0f), lerp(MADERA, C.PAPEL, 0.3f)).copy(alpha = 0.6f), 14) }
    }

    /** El altavoz del frente: la tela, sus listones de madera, el marco y las dos perillas. */
    private fun DrawScope.altavoz(v: VistaRefugio, f: MarcoObjeto, x0: Float, x1: Float, y0: Float, y1: Float, z0: Float) {
        val fr = V3(0f, 0f, -1f); val z = z0 - 0.001f
        val a0 = x0 + 0.04f; val a1 = x1 - 0.1f; val b0 = y0 + 0.035f; val b1 = y1 - 0.04f
        caraDegradada(v, f, listOf(V3(a0, b0, z), V3(a1, b0, z), V3(a1, b1, z), V3(a0, b1, z)), fr, REJILLA, V3(a0, b1, z), V3(a1, b0, z))
        for (k in 1..8) rayaLuz(v, f, V3(a0 + 0.003f, b0 + (b1 - b0) * k / 9f, z - 0.0005f), V3(a1 - 0.003f, b0 + (b1 - b0) * k / 9f, z - 0.0005f), 0.0012f, lerp(REJILLA, C.TINTA, 0.25f), fr, 0.5f)
        for (k in 0..4) { val x = a0 + 0.015f + (a1 - a0 - 0.03f) * k / 4f
            caja(v, f, x - 0.008f, x + 0.008f, b0, b1, z - 0.012f, z, lerp(MADERA, C.TINTA, 0.1f)) }
        for (k in 0..3) rayaLuz(v, f, listOf(V3(a0, b0, z), V3(a1, b0, z), V3(a1, b1, z), V3(a0, b1, z))[k], listOf(V3(a1, b0, z), V3(a1, b1, z), V3(a0, b1, z), V3(a0, b0, z))[k], 0.008f, lerp(MADERA, C.TINTA, 0.15f), fr)
        for (k in 0..1) { val q = V3(x1 - 0.05f, y0 + 0.08f + 0.1f * k, z - 0.002f)
            cil(v, f, q, q + V3(0f, 0f, -0.016f), 0.016f, lerp(C.TINTA, MADERA, 0.25f), C.LATON, 10) }
    }

    /** El plato con su disco y su etiqueta, el brazo con la aguja sobre el surco, y el pincel encima. */
    private fun DrawScope.plato(v: VistaRefugio, f: MarcoObjeto, y1: Float) {
        val c = V3(-0.05f, y1, 0.0f); val arriba = V3(0f, 1f, 0f)
        cil(v, f, c, c + V3(0f, 0.015f, 0f), 0.14f, lerp(C.ACERO, C.TINTA, 0.4f), C.TINTA, 22)
        val d = c + V3(0f, 0.018f, 0f)
        cil(v, f, d - V3(0f, 0.003f, 0f), d, 0.128f, C.TINTA, lerp(C.TINTA, C.SOMBRA, 0.15f), 22)
        for (k in 0..3) Trazo3.run { aro(f.w(d + V3(0f, 0.0005f, 0f)), f.n(arriba), (0.06f + 0.017f * k) * f.amplia, 0.002f, TrazoObjeto.luz(v, f, d, arriba, lerp(C.TINTA, C.PAPEL, 0.3f)).copy(alpha = 0.5f), 20) }
        disco(v, f, d + V3(0f, 0.001f, 0f), arriba, 0.045f, lerp(C.ROJO, C.PAPEL, 0.15f), 14)
        cil(v, f, d, d + V3(0f, 0.022f, 0f), 0.004f, C.ACERO, lados = 6)
        // El brazo: el poste, el contrapeso, la barra y la cabeza con la aguja sobre el disco.
        val poste = V3(0.17f, y1, 0.1f)
        cil(v, f, poste, poste + V3(0f, 0.05f, 0f), 0.02f, lerp(C.ACERO, Color.White, 0.15f), lados = 10)
        val giro = poste + V3(0f, 0.055f, 0f); val cabeza = d + V3(0.09f, 0.022f, -0.06f)
        cil(v, f, giro, giro + V3(0.03f, 0f, 0.05f), 0.016f, C.TINTA, lados = 8)
        rayaLuz(v, f, giro, cabeza + V3(0.01f, 0.012f, 0f), 0.009f, lerp(C.ACERO, Color.White, 0.35f), arriba)
        caja(v, f, cabeza.x - 0.018f, cabeza.x + 0.018f, cabeza.y, cabeza.y + 0.016f, cabeza.z - 0.025f, cabeza.z + 0.01f, lerp(C.ACERO, C.TINTA, 0.3f))
        rayaLuz(v, f, cabeza + V3(0f, 0f, -0.01f), cabeza + V3(0f, -0.016f, -0.012f), 0.003f, lerp(C.ACERO, Color.White, 0.4f), V3(0f, 0f, -1f))
        brillo(v, f, giro + V3(-0.02f, 0.006f, -0.02f), cabeza + V3(0.02f, 0.018f, 0.02f), 0.003f, C.ACERO, arriba, 0.6f)
        // El pincel de limpiarlo: el mango de madera clara, la virola y el pelo oscuro.
        val m0 = d + V3(-0.11f, 0.006f, -0.07f); val m1 = d + V3(0.0f, 0.008f, 0.02f)
        rayaLuz(v, f, m0, m1, 0.011f, lerp(C.COBRE, C.PAPEL, 0.45f), arriba)
        rayaLuz(v, f, m1, m1 + V3(0.025f, 0.001f, 0.02f), 0.015f, C.LATON, arriba, punta = StrokeCap.Butt)
        val p0 = m1 + V3(0.025f, 0.001f, 0.02f)
        cara(v, f, listOf(p0 + V3(-0.012f, 0.002f, 0.01f), p0 + V3(0.012f, 0.002f, -0.01f), p0 + V3(0.045f, 0.0f, 0.03f), p0 + V3(0.025f, 0.0f, 0.05f)), arriba, lerp(C.TINTA, C.COBRE, 0.35f))
        for (k in 0..3) rayaLuz(v, f, p0 + V3(-0.006f + 0.005f * k, 0.003f, 0.005f - 0.005f * k), p0 + V3(0.03f + 0.004f * k, 0.002f, 0.04f - 0.004f * k), 0.0015f, lerp(C.COBRE, C.PAPEL, 0.3f), arriba, 0.6f)
    }

    /** La tapa levantada: la caja poco honda de la tapa, vista por dentro, con su veta y la escuadra que la sostiene. */
    private fun DrawScope.tapaLevantada(v: VistaRefugio, f: MarcoObjeto, x0: Float, x1: Float, y1: Float, z1: Float, dir: V3, fondo: Float) {
        val bis = V3(0f, y1, z1)
        val nDentro = V3(0f, -dir.z, dir.y).let { if (it.z > 0f) it * -1f else it }
        fun t(x: Float, s: Float, d: Float = 0f) = V3(x, bis.y + dir.y * s + nDentro.y * d, bis.z + dir.z * s + nDentro.z * d)
        // El canto de la tapa (su grueso) y la cara de dentro.
        cara(v, f, listOf(t(x0, 0f), t(x0, fondo), t(x0, fondo, 0.035f), t(x0, 0f, 0.035f)), V3(-1f, 0f, 0f), MADERA)
        caraDegradada(v, f, listOf(t(x0, 0f, 0.035f), t(x1, 0f, 0.035f), t(x1, fondo, 0.035f), t(x0, fondo, 0.035f)), nDentro, lerp(MADERA, C.PAPEL, 0.25f), t(0f, fondo, 0.035f), t(0f, 0f, 0.035f))
        cara(v, f, listOf(t(x0 + 0.02f, 0.02f, 0.012f), t(x1 - 0.02f, 0.02f, 0.012f), t(x1 - 0.02f, fondo - 0.02f, 0.012f), t(x0 + 0.02f, fondo - 0.02f, 0.012f)), nDentro, lerp(lerp(C.COBRE, C.AMARILLO, 0.3f), C.PAPEL, 0.25f))
        // La etiqueta de papel pegada por dentro, en blanco, con sus rayas.
        cara(v, f, listOf(t(-0.07f, 0.22f, 0.01f), t(0.07f, 0.22f, 0.01f), t(0.07f, 0.29f, 0.01f), t(-0.07f, 0.29f, 0.01f)), nDentro, lerp(C.PAPEL, C.AMARILLO, 0.15f))
        for (k in 0..2) rayaLuz(v, f, t(-0.055f, 0.235f + 0.018f * k, 0.009f), t(0.04f - 0.02f * k, 0.235f + 0.018f * k, 0.009f), 0.002f, lerp(C.TINTA, C.PAPEL, 0.3f), nDentro, 0.7f)
        for (k in 0..4) rayaLuz(v, f, t(x0 + 0.03f, 0.04f + 0.065f * k, 0.011f), t(x1 - 0.03f, 0.05f + 0.065f * k, 0.011f), 0.002f, lerp(MADERA, C.TINTA, 0.35f), nDentro, 0.55f)
        rayaLuz(v, f, t(x0, fondo, 0.035f), t(x1, fondo, 0.035f), 0.006f, lerp(MADERA, C.PAPEL, 0.3f), dir, 0.8f)
        // La escuadra de latón del costado.
        rayaLuz(v, f, V3(x1 - 0.01f, y1 + 0.005f, 0.05f), t(x1 - 0.01f, 0.18f, 0.02f), 0.004f, C.LATON, V3(1f, 0f, 0f))
    }
}
