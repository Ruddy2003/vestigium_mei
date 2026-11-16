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
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Tarea
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.ARRIBA
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.Marco
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.cajaEn
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.cilindro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.palo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * La mesa larga de la grama, en el primer plano: entra por el borde de abajo a la izquierda y se aleja en diagonal hacia
 * el fondo izquierdo. Encima, en orden exacto (el autismo de la doctora se cuenta así: todo paralelo, todo a ras): las
 * bandejas de tierra gris con su tapa, en filas; la balanza fina con su urna de vidrio; el microscopio de latón; la
 * gradilla de tubos; las pipetas en su soporte; el plato negro con la pinza; la lupa; la tableta; la lámpara de su mesa,
 * el altavoz y la taza. Debajo, el cajón (con la piedra) y la tabla de abajo, con las cajas de tierra de arriba. Encima,
 * colgadas bajas a alturas distintas, las cinco lámparas de cultivo.
 *
 * **[Diseño propio]**: la mesa está algo girada respecto de las paredes (catorce grados): es una mesa suelta en medio de
 * la sala, y así su canto hace la diagonal grande del cuadro y la fila de lámparas se abre en abanico.
 */
internal object MesaLab {
    private val C = ColorLaboratorio
    private val G = 14f * PI.toFloat() / 180f

    /** Los ejes de la mesa: [Marco.w] (a lo largo desde el extremo de cerca, de lado desde el canto derecho, de alto). */
    val MK = Marco(V3(0.528f, 0f, 1.418f), V3(-sin(G), 0f, cos(G)))
    const val LARGO = 4.4f
    const val ANCHO = 0.9f
    const val ALTO = 0.92f
    const val GRUESO = 0.05f

    /** Dónde cuelga cada lámpara de cultivo (a lo largo, de lado, de alto), de la del fondo a la cercana. */
    val LAMPARAS = listOf(Triple(3.95f, 0.62f, 2.42f), Triple(3.25f, 0.38f, 2.3f), Triple(2.55f, 0.66f, 2.48f), Triple(1.8f, 0.4f, 2.34f), Triple(1.0f, 0.42f, 2.46f))

    /** Dónde está la lámpara de su mesa (la bombilla, bajo la pantalla) y dónde cae su luz. */
    val LAMPARA_MESA = MK.w(0.36f, 0.22f, ALTO + 0.42f)

    /** El claro de la mesa más cercano: ahí va el objeto chico del día. */
    val CLARO = MK.w(0.2f, 0.2f, ALTO)

    /** La bandeja cuarenta y siete: la que germina. */
    private const val B47 = 7

    // ---------------------------------------------------------------- Las cosas de la mesa

    /** Una bandeja de la mesa: dónde está (a lo largo, de lado) y su número en la fila. */
    class Bandeja(val a: Float, val b: Float, val i: Int)

    val BANDEJAS: List<Bandeja> = buildList {
        var i = 0
        for (fila in 0..1) for (j in 0 until 9) add(Bandeja(1.62f + j * 0.285f, 0.47f + fila * 0.22f, i++))
        for (j in 0 until 4) add(Bandeja(3.33f + j * 0.285f, 0.24f, i++))
    }

    /** Si la bandeja [i] tiene la tapa puesta a esta hora: en el desayuno y recogido, todas; trabajando o comiendo, algunas levantadas. */
    private fun tapada(v: VistaLab, i: Int, grama: Grama): Boolean {
        if (i == B47 && grama == Grama.HILO) return false
        return when (v.tarea) {
            Tarea.DESAYUNO, Tarea.RECOGIDO, Tarea.MUSICA -> true
            Tarea.TRABAJO, Tarea.COMIDA -> (i * 7 + 3) % 5 >= 2
        }
    }

    /** La mesa entera como una cosa de la sala: su esqueleto (para la sombra en el suelo) y su dibujo. */
    fun cosa(v: VistaLab): CosaLab {
        val esq = listOf(MK.w(0f, 0f, ALTO), MK.w(0f, ANCHO, ALTO), MK.w(LARGO, 0f, ALTO), MK.w(LARGO, ANCHO, ALTO), MK.w(0.1f, 0f, 0.1f), MK.w(LARGO - 0.1f, 0f, 0.1f))
        return CosaLab(CamLab.hondo(MK.o.x, MK.o.z), esq) { pintar(v) }
    }

    fun DrawScope.pintar(v: VistaLab) {
        debajo(v)
        tablero(v)
        with(LuzLab) { enLaMesa(v); sombrasEnLaMesa(v, esqueletos(v)) }
        encima(v)
    }

    /** Los cuerpos de lo que hay encima de la mesa, para sus sombras en el tablero. */
    private fun esqueletos(v: VistaLab): List<List<V3>> {
        fun caja(a0: Float, a1: Float, b0: Float, b1: Float, alto: Float) = listOf(MK.w(a0, b0, ALTO), MK.w(a1, b0, ALTO), MK.w(a1, b1, ALTO), MK.w(a0, b1, ALTO), MK.w(a0, b0, ALTO + alto), MK.w(a1, b0, ALTO + alto), MK.w(a1, b1, ALTO + alto), MK.w(a0, b1, ALTO + alto))
        val grama = EscenaLaboratorio.grama(v.m)
        return buildList {
            add(caja(0.92f, 1.12f, 0.13f, 0.29f, 0.4f))                            // el microscopio
            add(caja(2.0f, 2.3f, 0.14f, 0.42f, 0.28f))                             // la balanza en su urna
            add(caja(2.5f, 2.82f, 0.12f, 0.24f, 0.17f))                            // la gradilla
            add(caja(1.64f, 1.84f, 0.14f, 0.22f, 0.28f))                           // las pipetas
            add(caja(0.25f, 0.35f, 0.43f, 0.53f, 0.09f))                           // la taza
            add(caja(0.84f, 0.96f, 0.7f, 0.86f, 0.13f))                            // el altavoz
            add(caja(0.56f, 0.68f, 0.02f, 0.12f, 0.46f))                           // el pie y el brazo de la lámpara
            if (grama == Grama.TANQUE || grama == Grama.CAJAS) add(caja(3.3f, 4.3f, 0.18f, 0.86f, 0.22f))
            for (bd in BANDEJAS) add(caja(bd.a, bd.a + 0.24f, bd.b, bd.b + 0.17f, 0.06f))
        }
    }

    // ---------------------------------------------------------------- Lo de debajo

    /** Las patas, la tabla de abajo con las cajas de tierra de arriba y los sacos, y el cajón del extremo de cerca. */
    private fun DrawScope.debajo(v: VistaLab) {
        val acero = lerp(C.ACERO, C.NEGRO, 0.1f)
        // La tabla de abajo y lo que guarda: cajas de tierra, bandejas apiladas, un saco.
        cajaEn(v, MK, 0.55f, LARGO - 0.08f, 0.05f, ANCHO - 0.05f, 0.2f, 0.23f, lerp(C.ACERO, C.HORMIGON, 0.2f))
        val az = Azar(Semilla.de("laboratorio", "debajo"))
        for ((i, a) in listOf(0.9f, 1.55f, 2.45f, 3.3f).withIndex()) {
            val alto = 0.22f + 0.05f * (i % 2)
            cajaEn(v, MK, a, a + 0.5f, 0.12f, 0.6f, 0.23f, 0.23f + alto, if (i == 2) lerp(C.TELA, C.TIERRA, 0.45f) else C.MADERA) { cara, f ->
                if (cara == 3 && i != 2) drawPath(f, v.c(MK.w(a + 0.25f, 0.36f, 0.23f + alto), C.TIERRA, ARRIBA).copy(alpha = 0.9f))
                if (cara == 2 && i != 2) for (t in 1..2) drawLine(v.c(MK.w(a, 0f, 0.4f), lerp(C.MADERA, C.NEGRO, 0.4f), MK.n(0f, -1f, 0f)).copy(alpha = 0.5f), MK.p(a + 0.02f, 0.12f, 0.23f + alto * t / 3f), MK.p(a + 0.48f, 0.12f, 0.23f + alto * t / 3f), 0.4f)
            }
            if (az.real() < 0.5f) mancha(MK.p(a + 0.25f, 0.1f, 0.23f), 6f, 1.4f, C.TIERRA, 0.4f)
        }
        // Las patas: tubo de acero cuadrado, con sus tacos.
        for (a in listOf(0.06f, 2.2f, LARGO - 0.06f)) for (b in listOf(0.06f, ANCHO - 0.06f)) {
            palo(v, MK.w(a, b, 0f), MK.w(a, b, ALTO - GRUESO), 0.045f, acero)
            drawOval(v.c(MK.w(a, b, 0f), C.NEGRO, ARRIBA), MK.p(a, b, 0f) - Offset(0.04f * CamLab.k(MK.w(a, b, 0f)), 0.012f * CamLab.k(MK.w(a, b, 0f))), Size(0.08f * CamLab.k(MK.w(a, b, 0f)), 0.024f * CamLab.k(MK.w(a, b, 0f))))
        }
        // El travesaño de abajo, a lo largo.
        palo(v, MK.w(0.06f, 0.06f, 0.2f), MK.w(LARGO - 0.06f, 0.06f, 0.2f), 0.03f, acero, filo = false)
        // El cajón del extremo de cerca: su frente, con el tirador de latón y una etiqueta de color.
        cajaEn(v, MK, 0.02f, 0.5f, 0.04f, ANCHO - 0.04f, ALTO - GRUESO - 0.16f, ALTO - GRUESO, lerp(C.ACERO, C.HORMIGON, 0.45f)) { cara, f ->
            if (cara == 0) {
                drawPath(f, Color.Black.copy(alpha = 0.04f))
                drawLine(v.c(MK.w(0f, 0.45f, ALTO - 0.13f), C.SOMBRA, MK.n(-1f, 0f, 0f)).copy(alpha = 0.5f), MK.p(0.02f, 0.06f, ALTO - GRUESO - 0.155f), MK.p(0.02f, ANCHO - 0.06f, ALTO - GRUESO - 0.155f), 0.6f)
                val t = MK.p(0.019f, ANCHO / 2f, ALTO - GRUESO - 0.08f); val k = CamLab.k(MK.w(0f, ANCHO / 2f, 0f))
                drawLine(v.c(MK.w(0f, ANCHO / 2f, ALTO - 0.1f), C.LATON, MK.n(-1f, 0.3f, 0f)), t - Offset(0.07f * k, 0.02f * k), t + Offset(0.07f * k, -0.02f * k), (0.018f * k).coerceAtLeast(0.7f), StrokeCap.Round)
                drawRect(v.c(MK.w(0f, 0.2f, ALTO - 0.1f), C.CIAN, MK.n(-1f, 0f, 0f)), MK.p(0.019f, 0.22f, ALTO - GRUESO - 0.05f), Size(0.06f * k, 0.025f * k))
            }
        }
    }

    // ---------------------------------------------------------------- El tablero

    /** El color del tablero: madera vieja, gastada y encerada, lo cálido del primer plano. */
    val TABLERO = lerp(C.MADERA, C.TELA, 0.3f)

    /**
     * El tablero: una tabla gruesa de madera vieja sobre el bastidor de acero **[Diseño propio]**: es lo cálido del primer
     * plano contra el blanco frío de la sala. Con su veta a lo largo, las juntas de las tablas, los cercos de las bandejas
     * quitadas, la tierra gris que se pega «por la electricidad», una quemadura y el canto comido.
     */
    private fun DrawScope.tablero(v: VistaLab) {
        val madera = TABLERO
        val arriba = listOf(MK.w(0f, 0f, ALTO), MK.w(LARGO, 0f, ALTO), MK.w(LARGO, ANCHO, ALTO), MK.w(0f, ANCHO, ALTO))
        PiezasLab.run { caraGrande(v, arriba, madera, ARRIBA, a = MK.w(0f, ANCHO / 2f, ALTO), b = MK.w(LARGO, ANCHO / 2f, ALTO), pasos = 6) }
        // El canto de cerca (que mira a quien mira), más oscuro, con la luz en la arista.
        cajaEn(v, MK, 0f, LARGO, 0f, ANCHO, ALTO - GRUESO, ALTO - 0.001f, lerp(madera, C.NEGRO, 0.25f))
        drawLine(v.c(MK.w(1f, 0f, ALTO), lerp(madera, Color.White, 0.5f), ARRIBA).copy(alpha = 0.7f), MK.p(0f, 0f, ALTO), MK.p(LARGO, 0f, ALTO), 0.7f)
        drawLine(v.c(MK.w(0f, 0.4f, ALTO), lerp(madera, Color.White, 0.5f), ARRIBA).copy(alpha = 0.6f), MK.p(0f, 0f, ALTO), MK.p(0f, ANCHO, ALTO), 0.7f)
        clipPath(poligono(arriba.map { CamLab.p(it) })) {
            val az = Azar(Semilla.de("laboratorio", "tablero"))
            // Las juntas de las tablas, a lo largo, y la veta, que ondula.
            val oscura = lerp(madera, C.NEGRO, 0.35f)
            for (b in listOf(0.22f, 0.45f, 0.68f)) drawLine(v.c(MK.w(1f, b, ALTO), oscura, ARRIBA).copy(alpha = 0.7f), MK.p(0f, b, ALTO), MK.p(LARGO, b, ALTO), 0.6f)
            repeat(26) {
                val b = az.entre(0.02f, ANCHO - 0.02f); val a0 = az.entre(-0.2f, LARGO - 0.4f); val largo = az.entre(0.4f, 1.6f); val onda = az.entre(-0.015f, 0.015f)
                val veta = Path().apply { val p0 = MK.p(a0, b, ALTO); moveTo(p0.x, p0.y); val c1 = MK.p(a0 + largo * 0.33f, b + onda, ALTO); val c2 = MK.p(a0 + largo * 0.66f, b - onda, ALTO); val p1 = MK.p(a0 + largo, b + onda * 0.5f, ALTO); cubicTo(c1.x, c1.y, c2.x, c2.y, p1.x, p1.y) }
                drawPath(veta, v.c(MK.w(a0 + largo / 2f, b, ALTO), oscura, ARRIBA).copy(alpha = 0.3f), style = Stroke(0.45f))
            }
            // Los cercos de las bandejas quitadas y la tierra gris pegada.
            repeat(18) { val a = az.entre(0.2f, LARGO - 0.2f); val b = az.entre(0.05f, ANCHO - 0.05f); val q = MK.w(a, b, ALTO); val k = CamLab.k(q)
                mancha(CamLab.p(q), az.entre(0.02f, 0.08f) * k, az.entre(0.008f, 0.022f) * k, if (it % 2 == 0) lerp(C.TIERRA, madera, 0.25f) else lerp(madera, C.NEGRO, 0.3f), 0.32f) }
            // Una quemadura de ácido, vieja, cerca del microscopio.
            val q = MK.w(1.3f, 0.12f, ALTO); val k = CamLab.k(q)
            mancha(CamLab.p(q), 0.05f * k, 0.014f * k, lerp(madera, C.NEGRO, 0.6f), 0.55f)
            // El canto comido, a golpes.
            repeat(9) { val a = az.entre(0.1f, LARGO - 0.3f); val p = MK.p(a, 0.008f, ALTO); val kk = CamLab.k(MK.w(a, 0f, ALTO))
                drawLine(v.c(MK.w(a, 0f, ALTO), lerp(madera, Color.White, 0.3f), ARRIBA).copy(alpha = 0.6f), p, MK.p(a + az.entre(0.03f, 0.1f), 0.01f, ALTO), (0.01f * kk).coerceAtLeast(0.5f)) }
        }
    }

    // ---------------------------------------------------------------- Lo de encima

    private fun DrawScope.encima(v: VistaLab) {
        val m = v.m
        val grama = EscenaLaboratorio.grama(m)
        // De lejos a cerca: las bandejas (y el tanque o las cajas al fondo), la gradilla, la balanza, las pipetas, la lupa,
        // el microscopio, el plato negro, el cuaderno, la tableta, el altavoz, la taza, la comida y la lámpara.
        val conTanque = grama == Grama.TANQUE || grama == Grama.CAJAS
        for (bd in BANDEJAS.sortedByDescending { it.a }) {
            if (conTanque && bd.a > 3.25f) continue
            bandeja(v, bd, grama)
        }
        if (conTanque) tanque(v)
        gradilla(v, 2.5f, 0.12f)
        balanza(v, 2.0f, 0.14f)
        pipetas(v, 1.64f, 0.14f)
        lupa(v, if (EscenaLaboratorio.germina(m)) BANDEJAS[B47].a else 1.5f, if (EscenaLaboratorio.germina(m)) BANDEJAS[B47].b else 0.4f)
        cuaderno(v, 1.22f, 0.5f)
        platoNegro(v, 1.36f, 0.3f)
        if (EscenaLaboratorio.cajaDeHilda(m)) cajaDeCarton(v, 1.0f, 0.45f)
        microscopio(v, 0.92f, 0.13f)
        altavoz(v, 0.84f, 0.7f)
        tableta(v, 0.62f, 0.4f)
        when (v.tarea) {
            Tarea.DESAYUNO -> desayuno(v, 0.36f, 0.66f)
            Tarea.COMIDA -> bandejaDeComida(v, 0.34f, 0.62f)
            Tarea.TRABAJO, Tarea.MUSICA, Tarea.RECOGIDO -> {}
        }
        if (SalaLab.laSalaPinta(m, "taza")) taza(v, 0.3f, 0.48f)
        lamparaDeMesa(v)
    }

    /** Una bandeja de tierra gris de arriba, con su tapa (o levantada, apoyada al lado), su cinta de color y su brote. */
    private fun DrawScope.bandeja(v: VistaLab, bd: Bandeja, grama: Grama) {
        val a = bd.a; val b = bd.b; val la = 0.24f; val lb = 0.17f; val alto = 0.055f
        val brote = when (grama) { Grama.TIERRA -> false; Grama.HILO -> bd.i == B47 || bd.i < EscenaLaboratorio.brotes(v.m) && bd.i % 3 == 1; Grama.MATAS, Grama.TANQUE, Grama.CAJAS -> bd.i % 7 != 3 }
        cajaEn(v, MK, a, a + la, b, b + lb, ALTO, ALTO + alto, lerp(C.TELA, C.HORMIGON, 0.3f)) { cara, f ->
            when (cara) {
                3 -> {
                    // La tierra, con su grano; donde le da la lámpara, casi blanca.
                    val dentro = poligono(listOf(MK.p(a + 0.015f, b + 0.015f, ALTO + alto), MK.p(a + la - 0.015f, b + 0.015f, ALTO + alto), MK.p(a + la - 0.015f, b + lb - 0.015f, ALTO + alto), MK.p(a + 0.015f, b + lb - 0.015f, ALTO + alto)))
                    val q = MK.w(a + la / 2f, b + lb / 2f, ALTO + alto)
                    drawPath(dentro, v.c(q, if (grama == Grama.MATAS && brote) lerp(C.TIERRA, C.TIERRA_LIMPIA, 0.15f) else C.TIERRA, ARRIBA))
                    val az = Azar(Semilla.de("laboratorio", "bandeja", bd.i))
                    repeat(6) { val p = MK.p(a + az.entre(0.03f, la - 0.03f), b + az.entre(0.03f, lb - 0.03f), ALTO + alto); drawCircle(v.c(q, lerp(C.TIERRA, C.NEGRO, 0.3f), ARRIBA), 0.35f, p) }
                    if (brote) grama(v, a + la / 2f, b + lb / 2f, ALTO + alto, if (grama == Grama.HILO) (if (bd.i == B47) 1 else 2) else 6 + bd.i % 4, grama == Grama.HILO && bd.i == B47)
                }
                0, 2 -> {
                    // La cinta de color con la fecha (una raya, sin letras).
                    if (cara == 0) drawPath(poligono(MK.p(a, b + 0.03f, ALTO + 0.018f), MK.p(a, b + 0.1f, ALTO + 0.018f), MK.p(a, b + 0.1f, ALTO + 0.04f), MK.p(a, b + 0.03f, ALTO + 0.04f)), v.c(MK.w(a, b, ALTO), if (bd.i % 4 == 0) C.AMARILLO else C.CIAN, MK.n(-1f, 0f, 0f)))
                    if (cara == 2) drawLine(v.c(MK.w(a, b, ALTO), C.CIAN, MK.n(0f, -1f, 0f)), MK.p(a + 0.04f, b, ALTO + 0.03f), MK.p(a + 0.12f, b, ALTO + 0.03f), 0.9f)
                }
                else -> {}
            }
        }
        // La tapa: un capuchón de plástico transparente, con su brillo; si no, apoyada de canto junto a la bandeja.
        if (tapada(v, bd.i, grama)) {
            val t = listOf(MK.p(a - 0.005f, b - 0.005f, ALTO + alto + 0.005f), MK.p(a + la + 0.005f, b - 0.005f, ALTO + alto + 0.005f), MK.p(a + la - 0.02f, b + 0.02f, ALTO + alto + 0.06f), MK.p(a + 0.02f, b + lb - 0.02f, ALTO + alto + 0.06f), MK.p(a - 0.005f, b + lb + 0.005f, ALTO + alto + 0.005f))
            val tapa = poligono(listOf(t[0], t[1], MK.p(a + la + 0.005f, b + lb + 0.005f, ALTO + alto + 0.005f), t[4]))
            val cielo = poligono(listOf(MK.p(a + 0.02f, b + 0.02f, ALTO + alto + 0.06f), MK.p(a + la - 0.02f, b + 0.02f, ALTO + alto + 0.06f), MK.p(a + la - 0.02f, b + lb - 0.02f, ALTO + alto + 0.06f), MK.p(a + 0.02f, b + lb - 0.02f, ALTO + alto + 0.06f)))
            drawPath(tapa, v.c(MK.w(a + la / 2f, b + lb / 2f, ALTO + 0.1f), C.VIDRIO, ARRIBA).copy(alpha = 0.32f))
            drawPath(cielo, v.c(MK.w(a + la / 2f, b + lb / 2f, ALTO + 0.12f), lerp(C.VIDRIO, Color.White, 0.5f), ARRIBA).copy(alpha = 0.3f))
            drawLine(Color.White.copy(alpha = 0.3f + 0.25f * v.general), t[0], t[1], 0.5f)
        } else if (bd.i % 2 == 0) {
            val y0 = ALTO + 0.002f
            drawPath(poligono(MK.p(a + 0.02f, b - 0.005f, y0), MK.p(a + la - 0.02f, b - 0.005f, y0), MK.p(a + la - 0.02f, b - 0.03f, y0 + 0.15f), MK.p(a + 0.02f, b - 0.03f, y0 + 0.15f)), v.c(MK.w(a, b, ALTO + 0.08f), C.VIDRIO, MK.n(0f, -1f, 0.2f)).copy(alpha = 0.3f))
        }
    }

    /** Las hojas de grama de una bandeja: [n] hojas finas; con [hilo], un solo hilo verde, «del grosor de un pelo, torcido». */
    private fun DrawScope.grama(v: VistaLab, a: Float, b: Float, y: Float, n: Int, hilo: Boolean) {
        val q = MK.w(a, b, y); val k = CamLab.k(q); val p = CamLab.p(q)
        val verde = lerp(v.c(V3(q.x, q.y + 0.05f, q.z), C.GRAMA, ARRIBA), C.GRAMA, 0.25f)
        if (hilo) {
            drawPath(Path().apply { moveTo(p.x, p.y); cubicTo(p.x + 0.01f * k, p.y - 0.03f * k, p.x - 0.015f * k, p.y - 0.04f * k, p.x + 0.004f * k, p.y - 0.065f * k) }, lerp(verde, Color.White, 0.15f), style = Stroke((0.004f * k).coerceAtLeast(0.45f), cap = StrokeCap.Round))
            drawCircle(lerp(C.GRAMA, Color.White, 0.5f), 0.6f, Offset(p.x + 0.004f * k, p.y - 0.065f * k))
            return
        }
        val az = Azar(Semilla.de("laboratorio", "grama", (a * 100).toInt(), (b * 100).toInt()))
        repeat(n) { val dx = az.entre(-0.08f, 0.08f) * k; val dy = az.entre(-0.03f, 0.03f) * k; val alto = az.entre(0.04f, 0.1f) * k; val tuerce = az.entre(-0.03f, 0.03f) * k
            drawLine(if (it % 3 == 0) lerp(verde, C.HOJA_OSC, 0.4f) else verde, Offset(p.x + dx, p.y + dy), Offset(p.x + dx + tuerce, p.y + dy - alto), (0.008f * k).coerceAtLeast(0.45f), StrokeCap.Round) }
    }

    /** El tanque de tierra que la grama ya limpió: tierra oscura y viva, con la grama espesa (desde el día que funciona). */
    private fun DrawScope.tanque(v: VistaLab) {
        val a0 = 3.3f; val a1 = 4.3f; val b0 = 0.18f; val b1 = 0.86f; val alto = 0.22f
        cajaEn(v, MK, a0, a1, b0, b1, ALTO, ALTO + alto, lerp(C.ACERO, C.HORMIGON, 0.2f)) { cara, f ->
            if (cara == 3) {
                drawPath(poligono(MK.p(a0 + 0.03f, b0 + 0.03f, ALTO + alto), MK.p(a1 - 0.03f, b0 + 0.03f, ALTO + alto), MK.p(a1 - 0.03f, b1 - 0.03f, ALTO + alto), MK.p(a0 + 0.03f, b1 - 0.03f, ALTO + alto)), v.c(MK.w((a0 + a1) / 2f, 0.5f, ALTO + alto), C.TIERRA_LIMPIA, ARRIBA))
            }
            if (cara == 2) drawLine(v.c(MK.w(a0, b0, ALTO), C.CIAN, MK.n(0f, -1f, 0f)), MK.p(a0 + 0.1f, b0, ALTO + 0.12f), MK.p(a0 + 0.3f, b0, ALTO + 0.12f), 1f)
        }
        val az = Azar(Semilla.de("laboratorio", "tanque"))
        for (i in 0 until 9) for (j in 0 until 3) { val a = a0 + 0.08f + i * 0.105f; val b = b0 + 0.12f + j * 0.2f + az.entre(-0.04f, 0.04f)
            grama(v, a, b, ALTO + alto, 7, false) }
        // El cristal de muestra: una franja de la pared del tanque es de vidrio, y se ven las capas de tierra.
    }

    /** La gradilla de madera con sus tubos de muestra, cada uno con su tapón de color. */
    private fun DrawScope.gradilla(v: VistaLab, a: Float, b: Float) {
        cajaEn(v, MK, a, a + 0.32f, b, b + 0.12f, ALTO, ALTO + 0.03f, C.MADERA)
        cajaEn(v, MK, a, a + 0.32f, b, b + 0.12f, ALTO + 0.09f, ALTO + 0.11f, C.MADERA)
        for (i in 0 until 8) { val aa = a + 0.03f + i * 0.037f; val base = MK.w(aa, b + 0.06f, ALTO + 0.01f)
            palo(v, base, base + V3(0f, 0.15f, 0f), 0.014f, lerp(C.VIDRIO, C.TIERRA, 0.15f + 0.1f * (i % 3)), filo = false)
            val tp = CamLab.p(base + V3(0f, 0.16f, 0f)); drawCircle(v.c(base, if (i % 3 == 0) C.AMARILLO else C.CIAN, ARRIBA), (0.009f * CamLab.k(base)).coerceAtLeast(0.6f), tp) }
    }

    /** La balanza fina, con su urna de vidrio (otra vez el vidrio) y el platillo dentro. */
    private fun DrawScope.balanza(v: VistaLab, a: Float, b: Float) {
        cajaEn(v, MK, a, a + 0.3f, b, b + 0.28f, ALTO, ALTO + 0.06f, lerp(C.TELA, C.HORMIGON, 0.2f))
        val y0 = ALTO + 0.06f; val y1 = y0 + 0.22f
        // La urna: las caras de vidrio con su canto.
        cajaEn(v, MK, a + 0.01f, a + 0.29f, b + 0.01f, b + 0.27f, y0, y1, C.VIDRIO.copy(alpha = 0.28f))
        for ((aa, bb) in listOf(a + 0.01f to b + 0.01f, a + 0.29f to b + 0.01f, a + 0.01f to b + 0.27f)) palo(v, MK.w(aa, bb, y0), MK.w(aa, bb, y1), 0.006f, C.ACERO, filo = false)
        drawLine(Color.White.copy(alpha = 0.4f), MK.p(a + 0.01f, b + 0.01f, y1), MK.p(a + 0.29f, b + 0.01f, y1), 0.5f)
        val pl = MK.w(a + 0.15f, b + 0.14f, y0 + 0.03f); cilindro(v, pl, 0.06f, 0.006f, C.ACERO)
    }

    /** Las seis pipetas en su soporte, de pie, cada una con su pera de goma. */
    private fun DrawScope.pipetas(v: VistaLab, a: Float, b: Float) {
        cajaEn(v, MK, a, a + 0.2f, b, b + 0.08f, ALTO, ALTO + 0.02f, lerp(C.ACERO, C.HORMIGON, 0.3f))
        palo(v, MK.w(a + 0.1f, b + 0.06f, ALTO), MK.w(a + 0.1f, b + 0.06f, ALTO + 0.28f), 0.01f, C.ACERO, filo = false)
        for (i in 0 until 6) { val aa = a + 0.02f + i * 0.032f; val base = MK.w(aa, b + 0.03f, ALTO + 0.03f)
            if (v.tarea == Tarea.TRABAJO && i == 2) continue                              // la tres, la buena, está fuera
            palo(v, base, base + V3(0f, 0.24f, 0f), 0.006f, lerp(C.VIDRIO, Color.White, 0.3f), filo = false)
            drawCircle(v.c(base, lerp(C.AVISO, C.BARRO, 0.5f), ARRIBA), (0.012f * CamLab.k(base)).coerceAtLeast(0.6f), CamLab.p(base + V3(0f, 0.25f, 0f))) }
        if (v.tarea == Tarea.TRABAJO) { val p0 = MK.w(1.95f, 0.42f, ALTO + 0.008f); palo(v, p0, MK.w(2.2f, 0.4f, ALTO + 0.008f), 0.007f, lerp(C.VIDRIO, Color.White, 0.3f), filo = false)
            drawCircle(v.c(p0, lerp(C.AVISO, C.BARRO, 0.5f), ARRIBA), (0.013f * CamLab.k(p0)).coerceAtLeast(0.6f), CamLab.p(p0)) }
    }

    /** La lupa de pie, con su aro de latón; el día que germina, sobre la bandeja cuarenta y siete. */
    private fun DrawScope.lupa(v: VistaLab, a: Float, b: Float) {
        val pie = MK.w(a + 0.12f, b - 0.06f, ALTO)
        cilindro(v, pie, 0.05f, 0.015f, C.NEGRO)
        val cab = MK.w(a + 0.1f, b + 0.06f, ALTO + 0.2f)
        palo(v, pie, MK.w(a + 0.12f, b - 0.06f, ALTO + 0.22f), 0.01f, C.ACERO, filo = false)
        palo(v, MK.w(a + 0.12f, b - 0.06f, ALTO + 0.22f), cab, 0.008f, C.ACERO, filo = false)
        val p = CamLab.p(cab); val k = CamLab.k(cab)
        drawOval(v.c(cab, lerp(C.VIDRIO, Color.White, 0.3f), ARRIBA).copy(alpha = 0.45f), p - Offset(0.06f * k, 0.025f * k), Size(0.12f * k, 0.05f * k))
        drawOval(v.c(cab, C.LATON, V3(0f, 0.6f, -1f)), p - Offset(0.06f * k, 0.025f * k), Size(0.12f * k, 0.05f * k), style = Stroke((0.008f * k).coerceAtLeast(0.6f)))
    }

    /** El microscopio de latón y vidrio, de antes del sellado, «pesado como un niño». */
    private fun DrawScope.microscopio(v: VistaLab, a: Float, b: Float) {
        if (!SalaLab.laSalaPinta(v.m, "microscopio")) return
        val lat = C.LATON; val neg = lerp(C.NEGRO, C.LATON, 0.15f)
        // El pie en herradura, negro, y la platina.
        cajaEn(v, MK, a, a + 0.2f, b, b + 0.16f, ALTO, ALTO + 0.035f, neg)
        val col = MK.w(a + 0.16f, b + 0.08f, ALTO + 0.035f)
        palo(v, col, col + V3(0f, 0.16f, 0f), 0.035f, lat)
        cajaEn(v, MK, a + 0.03f, a + 0.16f, b + 0.02f, b + 0.14f, ALTO + 0.11f, ALTO + 0.125f, neg)
        // El brazo curvo y el tubo inclinado con el ocular.
        val brazo = col + V3(0f, 0.16f, 0f); val tubo0 = MK.w(a + 0.08f, b + 0.08f, ALTO + 0.16f); val tubo1 = MK.w(a + 0.13f, b + 0.08f, ALTO + 0.36f)
        palo(v, brazo, MK.w(a + 0.12f, b + 0.08f, ALTO + 0.26f), 0.03f, lat)
        palo(v, tubo0, tubo1, 0.042f, lat)
        palo(v, tubo1, tubo1 + V3(0f, 0.04f, 0f), 0.03f, neg)
        // El revólver de los objetivos y el tornillo que se atasca.
        palo(v, tubo0, tubo0 + V3(0f, -0.035f, 0f), 0.02f, lerp(lat, Color.White, 0.25f))
        val tor = MK.w(a + 0.17f, b + 0.08f, ALTO + 0.2f); drawCircle(v.c(tor, lat, MK.n(0f, -1f, 0.2f)), (0.025f * CamLab.k(tor)).coerceAtLeast(0.8f), CamLab.p(tor))
        // El brillo del latón, que de noche coge la lámpara.
        val br = CamLab.p(MK.w(a + 0.105f, b + 0.06f, ALTO + 0.28f)); drawLine(Color.White.copy(alpha = 0.4f + 0.2f * v.mesa), br, br + Offset(0.6f, -3f), 0.6f)
    }

    /** El plato negro con la pinza y unas semillas pardas contadas en fila. */
    private fun DrawScope.platoNegro(v: VistaLab, a: Float, b: Float) {
        val c = MK.w(a, b, ALTO); cilindro(v, c, 0.075f, 0.012f, C.NEGRO)
        val k = CamLab.k(c)
        if (SalaLab.laSalaPinta(v.m, "semilla")) for (i in 0 until 5) drawCircle(v.c(c, lerp(C.MADERA, C.TIERRA, 0.3f), ARRIBA), (0.006f * k).coerceAtLeast(0.45f), MK.p(a - 0.03f + i * 0.015f, b, ALTO + 0.013f))
        palo(v, MK.w(a + 0.02f, b - 0.09f, ALTO + 0.005f), MK.w(a + 0.16f, b - 0.09f, ALTO + 0.005f), 0.006f, C.ACERO, filo = false)
    }

    /** El cuaderno abierto, de papel de fibra, con el lápiz paralelo y la regla paralela al lápiz. */
    private fun DrawScope.cuaderno(v: VistaLab, a: Float, b: Float) {
        if (!SalaLab.laSalaPinta(v.m, "cuaderno")) return
        val papel = lerp(C.TELA, C.TIERRA, 0.18f)
        val abierto = v.tarea == Tarea.TRABAJO || v.tarea == Tarea.COMIDA
        val l = 0.22f; val w = if (abierto) 0.32f else 0.16f
        drawPath(poligono(MK.p(a, b, ALTO + 0.004f), MK.p(a + l, b, ALTO + 0.004f), MK.p(a + l, b + w, ALTO + 0.004f), MK.p(a, b + w, ALTO + 0.004f)), v.c(MK.w(a + l / 2f, b + w / 2f, ALTO), papel, ARRIBA))
        if (abierto) {
            drawLine(v.c(MK.w(a, b + w / 2f, ALTO), lerp(papel, C.SOMBRA, 0.4f), ARRIBA), MK.p(a, b + w / 2f, ALTO + 0.005f), MK.p(a + l, b + w / 2f, ALTO + 0.005f), 0.5f)
            for (i in 1..6) for (s in listOf(0.02f, w / 2f + 0.02f)) drawLine(v.c(MK.w(a, b, ALTO), lerp(papel, C.NEGRO, 0.45f), ARRIBA).copy(alpha = 0.5f), MK.p(a + 0.02f + i * 0.028f, b + s, ALTO + 0.005f), MK.p(a + 0.02f + i * 0.028f, b + s + w / 2f - 0.05f, ALTO + 0.005f), 0.3f)
        } else drawLine(v.c(MK.w(a, b, ALTO), lerp(C.TELA, C.TIERRA, 0.4f), ARRIBA), MK.p(a, b + 0.01f, ALTO + 0.005f), MK.p(a + l, b + 0.01f, ALTO + 0.005f), 0.6f)
        // El lápiz y la regla, paralelos al canto.
        palo(v, MK.w(a, b - 0.04f, ALTO + 0.006f), MK.w(a + 0.18f, b - 0.04f, ALTO + 0.006f), 0.008f, C.AMARILLO, filo = false)
        drawPath(poligono(MK.p(a, b - 0.08f, ALTO + 0.003f), MK.p(a + 0.3f, b - 0.08f, ALTO + 0.003f), MK.p(a + 0.3f, b - 0.11f, ALTO + 0.003f), MK.p(a, b - 0.11f, ALTO + 0.003f)), v.c(MK.w(a, b - 0.1f, ALTO), lerp(C.ACERO, Color.White, 0.3f), ARRIBA))
    }

    /** La tableta: una placa de vidrio negro con una esquina pegada con resina; encendida mientras trabaja. */
    private fun DrawScope.tableta(v: VistaLab, a: Float, b: Float) {
        if (!SalaLab.laSalaPinta(v.m, "tableta")) return
        val l = 0.17f; val w = 0.24f
        val ps = listOf(MK.p(a, b, ALTO + 0.008f), MK.p(a + l, b, ALTO + 0.008f), MK.p(a + l, b + w, ALTO + 0.008f), MK.p(a, b + w, ALTO + 0.008f))
        drawPath(poligono(ps), v.c(MK.w(a, b, ALTO), C.NEGRO, ARRIBA))
        if (v.tarea == Tarea.TRABAJO || EscenaLaboratorio.humo(v.m)) {
            val d = listOf(MK.p(a + 0.02f, b + 0.02f, ALTO + 0.009f), MK.p(a + l - 0.02f, b + 0.02f, ALTO + 0.009f), MK.p(a + l - 0.02f, b + w - 0.02f, ALTO + 0.009f), MK.p(a + 0.02f, b + w - 0.02f, ALTO + 0.009f))
            drawPath(poligono(d), lerp(C.CIAN, C.NEGRO, 0.5f))
            for (i in 1..4) drawLine(lerp(C.CIAN, Color.White, 0.3f).copy(alpha = 0.6f), MK.p(a + 0.03f + i * 0.025f, b + 0.04f, ALTO + 0.01f), MK.p(a + 0.03f + i * 0.025f, b + 0.04f + 0.03f * i, ALTO + 0.01f), 0.4f)
        }
        // La esquina pegada con resina, ámbar.
        drawPath(poligono(ps[2], MK.p(a + l - 0.04f, b + w, ALTO + 0.009f), MK.p(a + l, b + w - 0.05f, ALTO + 0.009f)), lerp(v.c(MK.w(a, b, ALTO), C.LATON, ARRIBA), C.LATON, 0.3f).copy(alpha = 0.8f))
        drawLine(Color.White.copy(alpha = 0.25f + 0.15f * v.general), ps[0], ps[1], 0.5f)
    }

    /** El altavoz de la mesa: una caja con su rejilla redonda (la música de la noche). */
    private fun DrawScope.altavoz(v: VistaLab, a: Float, b: Float) {
        cajaEn(v, MK, a, a + 0.12f, b, b + 0.16f, ALTO, ALTO + 0.13f, lerp(C.MADERA, C.NEGRO, 0.35f)) { cara, f ->
            if (cara == 0) { val c = MK.p(a - 0.001f, b + 0.08f, ALTO + 0.065f); val k = CamLab.k(MK.w(a, b, ALTO))
                drawCircle(v.c(MK.w(a, b + 0.08f, ALTO + 0.06f), lerp(C.NEGRO, C.ACERO, 0.25f), MK.n(-1f, 0f, 0f)), 0.045f * k, c)
                for (r in listOf(0.03f, 0.018f)) drawCircle(v.c(MK.w(a, b, ALTO), C.ACERO, MK.n(-1f, 0f, 0f)).copy(alpha = 0.4f), r * k, c, style = Stroke(0.4f)) }
        }
        if (v.tarea == Tarea.MUSICA) drawCircle(lerp(C.GRAMA, Color.White, 0.5f), 0.55f, MK.p(a - 0.001f, b + 0.14f, ALTO + 0.115f))
    }

    /** La taza de barro gris, con el borde grueso y la muesca; la infusión, que se le enfría siempre. */
    private fun DrawScope.taza(v: VistaLab, a: Float, b: Float) {
        val c = MK.w(a, b, ALTO); val r = 0.045f; val alto = 0.09f
        cilindro(v, c, r, alto, lerp(C.BARRO, C.TIERRA, 0.55f), tapa = null)
        val boca = PiezasLab.aro(c + V3(0f, alto, 0f), r, 16).map { CamLab.p(it) }
        drawPath(poligono(boca), v.c(c + V3(0f, alto, 0f), lerp(C.BARRO, C.NEGRO, 0.3f), ARRIBA))
        drawPath(poligono(PiezasLab.aro(c + V3(0f, alto - 0.015f, 0f), r * 0.78f, 16).map { CamLab.p(it) }), v.c(c + V3(0f, alto, 0f), lerp(C.BARRO, C.GRAMA, 0.4f), ARRIBA))
        // El asa.
        val asa = c + V3(r * 0.9f, alto * 0.55f, -r * 0.4f); val k = CamLab.k(asa)
        drawArc(v.c(asa, lerp(C.BARRO, C.TIERRA, 0.55f), ARRIBA), -90f, 180f, false, CamLab.p(asa) - Offset(0f, 0.025f * k), Size(0.03f * k, 0.05f * k), style = Stroke((0.012f * k).coerceAtLeast(0.6f)))
    }

    /** El desayuno, a las 7:00: un tomate de los jardines y un trozo de pan, sobre una servilleta. */
    private fun DrawScope.desayuno(v: VistaLab, a: Float, b: Float) {
        drawPath(poligono(MK.p(a, b, ALTO + 0.002f), MK.p(a + 0.2f, b, ALTO + 0.002f), MK.p(a + 0.2f, b + 0.16f, ALTO + 0.002f), MK.p(a, b + 0.16f, ALTO + 0.002f)), v.c(MK.w(a, b, ALTO), C.TELA, ARRIBA))
        val t = MK.w(a + 0.06f, b + 0.08f, ALTO + 0.03f); val k = CamLab.k(t)
        drawCircle(Brush.radialGradient(listOf(lerp(v.c(t, C.TOMATE, V3(-0.3f, 1f, -1f)), Color.White, 0.2f), v.c(t, C.TOMATE, V3(0.5f, 0f, -0.5f))), CamLab.p(t) - Offset(0.01f * k, 0.01f * k), 0.04f * k), 0.035f * k, CamLab.p(t))
        cajaEn(v, MK, a + 0.1f, a + 0.18f, b + 0.03f, b + 0.13f, ALTO + 0.002f, ALTO + 0.05f, lerp(C.MADERA, C.AMARILLO, 0.35f))
    }

    /** La comida, a las 13:00: la bandeja de cuatro huecos. */
    private fun DrawScope.bandejaDeComida(v: VistaLab, a: Float, b: Float) {
        cajaEn(v, MK, a, a + 0.26f, b, b + 0.18f, ALTO, ALTO + 0.02f, lerp(C.ACERO, C.HORMIGON, 0.4f))
        val comida = listOf(C.GRAMA, lerp(C.AMARILLO, C.BARRO, 0.4f), lerp(C.TELA, C.TIERRA, 0.2f), C.TOMATE)
        for (i in 0..1) for (j in 0..1) { val aa = a + 0.02f + i * 0.12f; val bb = b + 0.02f + j * 0.08f
            drawPath(poligono(MK.p(aa, bb, ALTO + 0.021f), MK.p(aa + 0.1f, bb, ALTO + 0.021f), MK.p(aa + 0.1f, bb + 0.06f, ALTO + 0.021f), MK.p(aa, bb + 0.06f, ALTO + 0.021f)), v.c(MK.w(aa, bb, ALTO + 0.02f), comida[i * 2 + j], ARRIBA)) }
    }

    /** La caja de cartón de Hilda, con sus veinte cuadernos de tapas de tela (el día que deja el laboratorio). */
    private fun DrawScope.cajaDeCarton(v: VistaLab, a: Float, b: Float) {
        cajaEn(v, MK, a, a + 0.36f, b, b + 0.28f, ALTO, ALTO + 0.24f, lerp(C.MADERA, C.AMARILLO, 0.3f))
        for (i in 0 until 6) cajaEn(v, MK, a + 0.03f + i * 0.05f, a + 0.07f + i * 0.05f, b + 0.03f, b + 0.25f, ALTO + 0.24f, ALTO + 0.29f, listOf(lerp(C.AVISO, C.TIERRA, 0.5f), lerp(C.CIAN, C.TIERRA, 0.5f), lerp(C.AMARILLO, C.TIERRA, 0.45f))[i % 3])
    }

    /**
     * La lámpara de su mesa: un brazo articulado de acero con la pantalla verde de Botánica, en el extremo de cerca. Encendida
     * (la luz cálida, «la última que se apaga»), la bombilla y el interior de la pantalla brillan.
     */
    private fun DrawScope.lamparaDeMesa(v: VistaLab) {
        val base = MK.w(0.62f, 0.06f, ALTO); val codo = MK.w(0.6f, 0.08f, ALTO + 0.4f); val cab = MK.w(0.4f, 0.2f, ALTO + 0.47f)
        cilindro(v, base, 0.06f, 0.025f, lerp(C.NEGRO, C.ACERO, 0.3f))
        palo(v, base + V3(0f, 0.02f, 0f), codo, 0.016f, C.ACERO)
        palo(v, codo, cab, 0.014f, C.ACERO)
        drawCircle(v.c(codo, C.LATON, PiezasLab.FRENTE), (0.015f * CamLab.k(codo)).coerceAtLeast(0.7f), CamLab.p(codo))
        // La pantalla: un cono verde, más abierto abajo.
        val bombilla = LAMPARA_MESA; val k = CamLab.k(bombilla); val p = CamLab.p(cab); val pb = CamLab.p(bombilla)
        val ancho = 0.11f * k
        val pantalla = Path().apply { moveTo(p.x - 0.03f * k, p.y - 0.01f * k); lineTo(p.x + 0.03f * k, p.y - 0.01f * k); lineTo(pb.x + ancho, pb.y + 0.01f * k); lineTo(pb.x - ancho, pb.y + 0.01f * k); close() }
        drawPath(pantalla, Brush.horizontalGradient(0f to v.c(cab, C.CUELLO, V3(-1f, 0.3f, -0.5f)), 0.5f to v.c(cab, lerp(C.CUELLO, Color.White, 0.2f), V3(0f, 0.5f, -1f)), 1f to v.c(cab, C.CUELLO, V3(1f, 0.3f, -0.5f)), startX = pb.x - ancho, endX = pb.x + ancho))
        val enc = v.mesa
        drawOval(lerp(v.c(bombilla, C.CUELLO, V3(0f, -1f, 0f)), lerp(C.LAMPARA, Color.White, 0.5f), enc), pb - Offset(ancho, 0.012f * k), Size(ancho * 2f, 0.03f * k))
        if (enc > 0.05f) drawOval(Brush.radialGradient(listOf(C.LAMPARA.copy(alpha = 0.5f * enc), Color.Transparent), pb, ancho * 2.2f), pb - Offset(ancho * 2.2f, ancho * 1.1f), Size(ancho * 4.4f, ancho * 2.2f), blendMode = BlendMode.Plus)
    }

    // ---------------------------------------------------------------- Las lámparas de cultivo

    /** Dónde está la lámpara de cultivo [i] (el centro de su boca), en la sala. */
    fun lampara(i: Int): V3 { val (a, b, y) = LAMPARAS[i]; return MK.w(a, b, y) }

    /**
     * Las cinco lámparas de cultivo, colgadas de su cable: pantallas de chapa, el borde de la boca encendido (blanco de
     * día, violeta de noche) y el cable que sube al riel del techo.
     */
    fun DrawScope.lamparasDeCultivo(v: VistaLab) {
        for (i in 0 until EscenaLaboratorio.LAMPARAS) {
            val c = lampara(i); val k = CamLab.k(c); val enc = if (v.noche) v.cultivoNoche * 0.8f else v.cultivo[i]
            // El cable, hasta fuera del cuadro por arriba.
            drawLine(v.c(c, C.NEGRO, PiezasLab.FRENTE), CamLab.p(c.x, c.y + 0.16f, c.z), CamLab.p(c.x, SalaLab.TECHO - 0.15f, c.z), 0.6f)
            // La pantalla: una campana de chapa, de lado.
            val p = CamLab.p(c); val arriba = CamLab.p(c.x, c.y + 0.2f, c.z); val r = 0.2f * k
            val chapa = Path().apply { moveTo(arriba.x - r * 0.25f, arriba.y); lineTo(arriba.x + r * 0.25f, arriba.y); cubicTo(arriba.x + r * 0.5f, arriba.y + (p.y - arriba.y) * 0.3f, p.x + r, p.y - r * 0.2f, p.x + r, p.y); lineTo(p.x - r, p.y); cubicTo(p.x - r, p.y - r * 0.2f, arriba.x - r * 0.5f, arriba.y + (p.y - arriba.y) * 0.3f, arriba.x - r * 0.25f, arriba.y); close() }
            drawPath(chapa, Brush.horizontalGradient(0f to v.c(c, lerp(C.ACERO, C.HORMIGON, 0.3f), V3(-1f, 0.4f, -0.4f)), 0.4f to v.c(c, lerp(C.ACERO, Color.White, 0.2f), V3(0f, 0.6f, -1f)), 1f to v.c(c, lerp(C.ACERO, C.NEGRO, 0.1f), V3(1f, 0.2f, -0.4f)), startX = p.x - r, endX = p.x + r))
            // La boca, vista un poco desde arriba: el borde y, dentro, la luz.
            val boca = Size(r * 2f, r * 0.34f)
            drawOval(lerp(v.c(c, lerp(C.ACERO, C.NEGRO, 0.4f), V3(0f, -1f, 0f)), lerp(v.colorCultivo, Color.White, 0.5f), enc), p - Offset(r, r * 0.17f), boca)
            if (enc > 0.05f) drawOval(Brush.radialGradient(listOf(v.colorCultivo.copy(alpha = 0.4f * enc), Color.Transparent), p, r * 1.5f), p - Offset(r * 1.5f, r * 0.6f), Size(r * 3f, r * 1.2f), blendMode = BlendMode.Plus)
        }
    }

    /** Lo que se ve de la mesa en el lienzo (el tablero), para recortar los charcos de luz. */
    fun formaDelTablero(): Path = poligono(listOf(MK.p(0f, 0f, ALTO), MK.p(LARGO, 0f, ALTO), MK.p(LARGO, ANCHO, ALTO), MK.p(0f, ANCHO, ALTO)))
}
