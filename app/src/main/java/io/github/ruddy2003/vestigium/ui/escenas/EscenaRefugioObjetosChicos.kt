package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.brillo
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.caja
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.cara
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.caraDegradada
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.cil
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.disco
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.halo
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.raya
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.rayaLuz
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.sombra
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObjeto.trazo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los ocho objetos chicos de la residente, sobre la esquina de delante de la bancada ([ObjetosRefugio.chico]). Cada uno
 * recibe el punto de la tapa donde se apoya ([o], en la sala) y la tapa para recortar su sombra. Van en tres cuartos y con
 * la luz de la sala en cada cara ([TrazoObjeto]); los planos (la funda, la tarjeta, la foto, las páginas) van de pie o
 * recostados, porque la bancada se ve muy de canto y tumbados no se leerían.
 */
internal object ChicosRefugio {
    private val C = ColorRefugio
    private val PI_F = PI.toFloat()

    // ---- Los pocos colores propios: mezclas de la paleta del tema.
    private val VINILO = lerp(C.TINTA, C.SOMBRA, 0.15f)                          // el disco, negro azulado
    private val FUNDA = lerp(C.PAPEL, C.AMARILLO, 0.12f)                          // el papel de la funda, crema
    private val CUERPO = lerp(C.VERDE, C.TINTA, 0.3f)                            // la chapa de la linterna (la de la percha)
    private val BAQUELITA = lerp(C.ROJO, C.TINTA, 0.35f)                         // el puño de la manivela
    private val OLIVA = lerp(C.ALGA, C.TINTA, 0.55f)                             // la caja del contador
    private val CUERO = lerp(C.COBRE, C.TINTA, 0.45f)                            // la correa del contador
    private val LUZ_VERDE = Color(0xFF9CF5B0)                                    // lo que da luz: la lucecita del contador (el verde del tema)
    private val HOJALATA = lerp(C.ACERO, C.PAPEL, 0.25f)                         // la lata, la cajita de las pastillas
    private val ETIQUETA = lerp(C.ROJO, C.PAPEL, 0.3f)                           // el papel de la lata, rojo desvaído
    private val TARJETA = lerp(C.PAPEL, C.NARANJA, 0.16f)                        // la cartulina de la tarjeta de racionamiento
    private val TELA_ROJA = lerp(C.ROJO, C.TINTA, 0.45f)                         // la tela del tomo viejo
    private val TELA_VERDE = lerp(C.VERDE, C.TINTA, 0.38f)                       // la tela del cuaderno de ahora
    private val HOJA = lerp(C.PAPEL, Color.White, 0.2f)                          // las páginas
    private val CARTON = lerp(C.PAPEL, C.COBRE, 0.3f)                            // la carpetilla de la foto
    private val PASTILLA = lerp(C.PAPEL, Color.White, 0.55f)                     // las pastillas de yodo
    private val LIBRO = lerp(C.PAPEL, C.HORMIGON, 0.12f)                         // las hojas viejas de libro de las flores

    // =================================================================
    //  El disco
    // =================================================================

    /**
     * El disco: «de vinilo, negros, en fundas de papel con fotos de gente sonriente de antes de la guerra… algunos están tan
     * gastados que suenan como lluvia». Uno de diez pulgadas, de pie y recostado contra el tocadiscos de la sala, medio
     * sacado por arriba de su funda. La funda, en vez de la foto de gente, lleva formas de color de los cincuenta: un sol con
     * sus rayos, tres círculos como globos y una ola, con el cerco que deja el disco, las esquinas gastadas y una tira de
     * cinta en el canto. El disco, negro, con sus surcos, su brillo en cruz, sus rayas de gastado y la etiqueta de color.
     */
    fun DrawScope.disco(v: VistaRefugio, o: V3, tapa: Path) {
        val f = MarcoObjeto(o, -0.32f, 1.15f)
        val r = Recostada(0.04f, 0.04f, 0.2f)
        sombra(v, f, listOf(r.s(-ANCHO_FUNDA, 0f), r.s(ANCHO_FUNDA, 0f), r.s(ANCHO_FUNDA, 0f, 0.01f), r.s(-ANCHO_FUNDA, 0f, 0.01f)), listOf(r.s(-ANCHO_FUNDA, ALTO_FUNDA), r.s(ANCHO_FUNDA, ALTO_FUNDA), r.s(0f, ALTO_FUNDA + 0.1f)), o.y, tapa)
        vinilo(v, f, r)
        funda(v, f, r)
    }

    private const val ANCHO_FUNDA = 0.13f
    private const val ALTO_FUNDA = 0.26f

    /**
     * Un plano de pie, recostado [inclina] radianes hacia atrás: [s] da su punto a [u] de su eje (corrido [dx]), [h] metros
     * subiendo por él y [d] metros hacia atrás; su cara de delante mira a [n].
     */
    private class Recostada(val dx: Float, val z0: Float, inclina: Float) {
        private val ci = cos(inclina); private val si = sin(inclina)
        fun s(u: Float, h: Float, d: Float = 0f) = V3(u + dx, h * ci - d * si, z0 + h * si + d * ci)
        val n = V3(0f, si, -ci)
    }

    /** El disco, detrás de la cara de la funda (se ve lo que asoma por arriba): los surcos, el brillo en cruz, las rayas, la etiqueta. */
    private fun DrawScope.vinilo(v: VistaRefugio, f: MarcoObjeto, p: Recostada) {
        val n = p.n; val hc = ALTO_FUNDA * 0.5f + 0.125f; val r = 0.123f
        cara(v, f, (0..30).map { i -> val a = 2f * PI_F * i / 30; p.s(r * cos(a), hc + r * sin(a), 0.004f) }, n, VINILO)
        for (j in 0..6) { val rr = 0.05f + 0.011f * j
            trazo(v, f, (0..24).map { i -> val a = 2f * PI_F * i / 24; p.s(rr * cos(a), hc + rr * sin(a), 0.0035f) }, 0.0012f, lerp(VINILO, C.PAPEL, if (j % 2 == 0) 0.16f else 0.08f), n) }
        // El brillo en cruz de la luz en los surcos (del lado que más alumbra) y las rayas del gastado.
        val izq = TrazoObjeto.luz(v, f, p.s(-r, hc), V3(-1f, 0.3f, -0.6f), C.PAPEL); val der = TrazoObjeto.luz(v, f, p.s(r, hc), V3(1f, 0.3f, -0.6f), C.PAPEL)
        val a0 = if (TrazoObjeto.lum(izq) > TrazoObjeto.lum(der)) PI_F * 0.78f else PI_F * 0.22f
        for (lado in 0..1) for (j in 0..4) { val a = a0 + lado * PI_F; val rr = 0.058f + j * 0.013f
            trazo(v, f, (0..5).map { i -> val q = a - 0.3f + 0.6f * i / 5; p.s(rr * cos(q), hc + rr * sin(q), 0.003f) }, 0.0022f, lerp(VINILO, Color.White, 0.6f), n, if (lado == 0) 0.55f else 0.28f) }
        for ((k, q0) in listOf(0.3f, 1.9f, 2.6f).withIndex()) { val rr = 0.07f + 0.02f * k
            trazo(v, f, (0..4).map { i -> val q = q0 + 0.25f * i / 4; p.s(rr * cos(q), hc + rr * sin(q), 0.003f) }, 0.0015f, lerp(VINILO, C.ACERO, 0.6f), n, 0.6f) }
        // La etiqueta: naranja con su franja y el agujero.
        cara(v, f, (0..20).map { i -> val a = 2f * PI_F * i / 20; p.s(0.042f * cos(a), hc + 0.042f * sin(a), 0.003f) }, n, lerp(C.NARANJA, C.AMARILLO, 0.25f))
        cara(v, f, listOf(p.s(-0.04f, hc + 0.012f, 0.0028f), p.s(0.04f, hc + 0.012f, 0.0028f), p.s(0.038f, hc + 0.022f, 0.0028f), p.s(-0.038f, hc + 0.022f, 0.0028f)), n, C.PAPEL)
        disco(v, f, p.s(0f, hc, 0.0027f), n, 0.005f, C.TINTA, 8)
    }

    /** La funda: el papel, la ola, el sol con sus rayos, los globos, el cerco del disco, la cinta, las esquinas gastadas. */
    private fun DrawScope.funda(v: VistaRefugio, f: MarcoObjeto, p: Recostada) {
        val n = p.n; val w = ANCHO_FUNDA; val h = ALTO_FUNDA
        caraDegradada(v, f, listOf(p.s(-w, 0f), p.s(w, 0f), p.s(w, h - 0.03f), p.s(w - 0.025f, h), p.s(-w, h)), n, FUNDA, p.s(0f, h), p.s(0f, 0f))
        cara(v, f, listOf(p.s(-w, 0f), p.s(w, 0f)) + (0..8).map { i -> val u = w - 2f * w * i / 8; p.s(u, 0.075f + 0.012f * sin(i * 1.4f)) }, n, lerp(C.CHAPA, C.AZUL, 0.3f))
        for (k in 0 until 10) { val a = 2f * PI_F * k / 10; cara(v, f, listOf(p.s(-0.055f + 0.034f * cos(a - 0.12f), 0.185f + 0.034f * sin(a - 0.12f)), p.s(-0.055f + 0.06f * cos(a), 0.185f + 0.06f * sin(a)), p.s(-0.055f + 0.034f * cos(a + 0.12f), 0.185f + 0.034f * sin(a + 0.12f))), n, lerp(C.AMARILLO, C.PAPEL, 0.2f)) }
        cara(v, f, (0..16).map { i -> val a = 2f * PI_F * i / 16; p.s(-0.055f + 0.03f * cos(a), 0.185f + 0.03f * sin(a)) }, n, lerp(C.NARANJA, C.AMARILLO, 0.35f))
        for ((k, g) in listOf(Triple(0.045f, 0.16f, C.ROJO), Triple(0.085f, 0.13f, C.AMARILLO), Triple(0.06f, 0.1f, lerp(C.AZUL, C.PAPEL, 0.2f))).withIndex()) {
            cara(v, f, (0..12).map { i -> val a = 2f * PI_F * i / 12; p.s(g.first + (0.02f - 0.003f * k) * cos(a), g.second + (0.02f - 0.003f * k) * sin(a)) }, n, g.third)
            rayaLuz(v, f, p.s(g.first, g.second - 0.02f), p.s(g.first + 0.004f, g.second - 0.06f), 0.0015f, C.TINTA, n, 0.6f)
        }
        trazo(v, f, (0..30).map { i -> val a = 2f * PI_F * i / 30; p.s(0.11f * cos(a), h * 0.5f + 0.11f * sin(a), -0.0005f) }, 0.006f, lerp(FUNDA, Color.White, 0.5f), n, 0.35f)
        cara(v, f, listOf(p.s(-w, 0.02f, -0.0008f), p.s(-w + 0.018f, 0.02f, -0.0008f), p.s(-w + 0.018f, h - 0.02f, -0.0008f), p.s(-w, h - 0.02f, -0.0008f)), n, lerp(C.PAPEL, C.AMARILLO, 0.3f), 0.75f)
        for ((u, hh) in listOf(-w to 0f, w to 0f, -w to h)) cara(v, f, listOf(p.s(u, hh), p.s(u - 0.025f * kotlin.math.sign(u), hh), p.s(u, hh + if (hh == 0f) 0.025f else -0.025f)), n, lerp(FUNDA, C.PAPEL, 0.7f))
        rayaLuz(v, f, p.s(w, 0f), p.s(w, h - 0.03f), 0.003f, lerp(FUNDA, C.TINTA, 0.35f), V3(1f, 0f, 0f))
        rayaLuz(v, f, p.s(-w, h), p.s(w - 0.025f, h), 0.003f, lerp(FUNDA, Color.White, 0.3f), V3(0f, 1f, 0f))
    }

    // =================================================================
    //  La linterna
    // =================================================================

    /**
     * La linterna de dinamo: «no usan pilas…: tienen una manivela que se gira para cargarlas, y dan luz mientras se gira y un
     * rato después». Tumbada en la bancada con el faro hacia delante: el cuerpo de chapa verde (la de la percha) con sus aros
     * moleteados y la pintura gastada, el faro de cromo más ancho con su cristal, la caja de la dinamo debajo, con la
     * manivela abierta y su puño de baquelita roja, el interruptor de corredera y el cordón de colgar en la cola. Mientras
     * ella trabaja en la sala, recién cargada, todavía da luz: el cristal encendido, su halo y el abanico de luz en la tapa
     * **[Diseño propio]**; cuando se va, apagada, con el reflector a la vista.
     */
    fun DrawScope.linterna(v: VistaRefugio, o: V3, tapa: Path) {
        val f = MarcoObjeto(o + V3(0.03f, 0f, 0f), 0.62f, 2.15f)
        val y = 0.03f; val encendida = !EscenaRefugio.estaFuera(v.m)
        val izq = V3(-1f, 0f, 0f)
        sombra(v, f, listOf(V3(-0.07f, 0f, -0.02f), V3(0.07f, 0f, -0.024f), V3(0.07f, 0f, 0.024f), V3(-0.07f, 0f, 0.02f)), listOf(V3(-0.078f, 0.062f, 0f), V3(0.075f, 0.052f, 0f), V3(-0.078f, 0.062f, 0.03f), V3(0.075f, 0.052f, 0.02f), V3(0.055f, 0.045f, -0.06f)), o.y, tapa)
        if (encendida) clipPath(tapa) {
            // El abanico de luz que deja delante del faro, en la tapa.
            val l0 = f.p(-0.08f, 0f, 0f); val l1 = f.p(-0.34f, 0f, 0f)
            drawPath(CamRefugio.forma(listOf(f.w(-0.08f, 0f, -0.025f), f.w(-0.34f, 0f, -0.12f), f.w(-0.38f, 0f, 0f), f.w(-0.34f, 0f, 0.12f), f.w(-0.08f, 0f, 0.025f))),
                androidx.compose.ui.graphics.Brush.linearGradient(listOf(C.LAMPARA.copy(alpha = 0.4f), C.LAMPARA.copy(alpha = 0.12f), Color.Transparent), l0, l1), blendMode = androidx.compose.ui.graphics.BlendMode.Plus)
        }
        // La caja de la dinamo, debajo, que la sostiene en la tapa.
        caja(v, f, -0.025f, 0.052f, 0.001f, 0.026f, -0.024f, 0.024f, lerp(CUERPO, C.TINTA, 0.25f), lerp(CUERPO, C.TINTA, 0.1f))
        for (k in 0..3) rayaLuz(v, f, V3(-0.015f + 0.017f * k, 0.006f, -0.0245f), V3(-0.015f + 0.017f * k, 0.02f, -0.0245f), 0.004f, C.TINTA, V3(0f, 0f, -1f), 0.7f)
        // El cuerpo, con la cola y sus aros moleteados; la pintura gastada en el lomo.
        cil(v, f, V3(-0.04f, y, 0f), V3(0.075f, y, 0f), 0.022f, CUERPO, lerp(CUERPO, C.ACERO, 0.3f), 16)
        for (x in listOf(0.055f, 0.062f, 0.069f)) Trazo3.run { aro(f.w(x, y, 0f), f.n(1f, 0f, 0f), 0.0225f * f.amplia, 0.002f * f.amplia, TrazoObjeto.luz(v, f, V3(x, y + 0.02f, -0.01f), V3(0f, 0.6f, -0.8f), lerp(CUERPO, C.TINTA, 0.4f)), 14) }
        rayaLuz(v, f, V3(-0.03f, y + 0.021f, -0.004f), V3(0.04f, y + 0.021f, -0.004f), 0.004f, lerp(CUERPO, C.ACERO, 0.55f), V3(0f, 1f, 0f), 0.75f)
        caja(v, f, 0.002f, 0.024f, y + 0.019f, y + 0.026f, -0.006f, 0.006f, C.TINTA)
        // El faro: el cuello que se ensancha, el aro de cromo y el cristal (encendido o con su reflector).
        cil(v, f, V3(-0.058f, y, 0f), V3(-0.038f, y, 0f), 0.027f, CUERPO, CUERPO, 16)
        cil(v, f, V3(-0.078f, y, 0f), V3(-0.056f, y, 0f), 0.032f, lerp(C.ACERO, Color.White, 0.25f), lerp(C.ACERO, Color.White, 0.4f), 18)
        val lente = V3(-0.0785f, y, 0f)
        if (encendida) {
            disco(v, f, lente, izq, 0.027f, lerp(C.LAMPARA, Color.White, 0.55f), 18, luz = false)
            disco(v, f, lente + V3(-0.0005f, 0f, 0f), izq, 0.012f, Color.White, 12, luz = false)
            TrazoObjeto.run { halo(f, lente + V3(-0.004f, 0f, 0f), 0.07f, C.LAMPARA, 0.6f) }
        } else {
            disco(v, f, lente, izq, 0.027f, lerp(C.ACERO, Color.White, 0.5f), 18)
            disco(v, f, lente + V3(-0.0003f, 0f, 0f), izq, 0.017f, lerp(C.ACERO, C.TINTA, 0.2f), 14)
            disco(v, f, lente + V3(-0.0006f, 0f, 0f), izq, 0.006f, lerp(C.PAPEL, C.AMARILLO, 0.3f), 8)
            brillo(v, f, lente + V3(-0.001f, 0.016f, -0.012f), lente + V3(-0.001f, 0.02f, 0.004f), 0.004f, C.ACERO, V3(-1f, 0.4f, -0.4f), 0.9f)
        }
        manivelaYCola(v, f, y)
    }

    /** La manivela, abierta, en el costado de la dinamo (el cubo, el brazo y el puño de baquelita), la cola y el cordón. */
    private fun DrawScope.manivelaYCola(v: VistaRefugio, f: MarcoObjeto, y: Float) {
        val cubo = V3(0.018f, 0.014f, -0.025f)
        disco(v, f, cubo, V3(0f, 0f, -1f), 0.011f, lerp(C.ACERO, Color.White, 0.15f), 12)
        val codo = V3(0.06f, 0.036f, -0.03f)
        rayaLuz(v, f, cubo + V3(0f, 0f, -0.002f), codo, 0.007f, lerp(C.ACERO, Color.White, 0.25f), V3(0f, 0.3f, -1f))
        cil(v, f, codo, codo + V3(0.004f, 0.002f, -0.03f), 0.0075f, BAQUELITA, lerp(BAQUELITA, C.PAPEL, 0.25f), 10)
        disco(v, f, cubo + V3(0f, 0f, -0.001f), V3(0f, 0f, -1f), 0.004f, C.TINTA, 6)
        // La cola y el cordón de colgar, flojo en la tapa.
        disco(v, f, V3(0.0755f, y, 0f), V3(1f, 0f, 0f), 0.02f, lerp(CUERPO, C.TINTA, 0.3f), 14)
        Trazo3.run { aro(f.w(0.081f, y, 0f), f.n(0f, 0f, 1f), 0.007f * f.amplia, 0.0025f * f.amplia, TrazoObjeto.luz(v, f, V3(0.08f, y, 0f), V3(1f, 0f, 0f), C.ACERO), 8) }
        trazo(v, f, listOf(V3(0.082f, y - 0.006f, 0f), V3(0.1f, 0.004f, 0.01f), V3(0.12f, 0.002f, -0.01f), V3(0.11f, 0.002f, -0.04f), V3(0.09f, 0.002f, -0.03f)), 0.003f, lerp(C.PAPEL, C.HORMIGON, 0.3f), V3(0f, 1f, 0f))
    }

    // =================================================================
    //  El contador
    // =================================================================

    /**
     * El contador de radiación: «se lleva en la muñeca… una lucecita verde que se pondría amarilla, y luego roja… nunca ha
     * cambiado de color». Colgado de su correa del canto de la tapa, delante de la esquina: la correa de cuero sale de la
     * hebilla, cruza la tapa, dobla por el canto y baja hasta la caja, que cuelga de pie, mirando a los ojos; de la caja cae el
     * otro cabo con sus agujeros. La caja, de chapa verde oliva con sus tornillos y su ruedecilla; delante, la esfera con la
     * aguja en el verde (un arco verde, amarillo y rojo, sin cifras) y tres lucecitas en fila: la verde encendida, con su
     * halo (más fuerte de noche), la ámbar y la roja apagadas.
     */
    fun DrawScope.contador(v: VistaRefugio, o: V3, tapa: Path) {
        val canto = TrazoObjeto.cantoDelante(o)
        val base = MarcoObjeto(o, 0f, 1f)
        val zc = canto - o.z                                                  // el canto, en las medidas de [base]
        // La correa sobre la tapa, del canto a la hebilla, y su hebilla.
        val ancho = 0.03f
        val cinta = listOf(V3(-0.02f, 0.003f, zc), V3(0.0f, 0.003f, zc + 0.06f), V3(0.04f, 0.003f, zc + 0.1f), V3(0.07f, 0.003f, zc + 0.12f))
        sombra(v, base, cinta.map { it + V3(0f, -0.003f, 0f) }, cinta, o.y, tapa, 0.3f)
        trazo(v, base, cinta, ancho, CUERO, V3(0f, 1f, 0f))
        trazo(v, base, cinta.map { it + V3(0f, 0.001f, 0f) }, 0.003f, lerp(CUERO, C.PAPEL, 0.35f), V3(0f, 1f, 0f), 0.6f)
        caja(v, base, 0.06f, 0.09f, 0.002f, 0.008f, zc + 0.105f, zc + 0.135f, C.LATON)
        // Por el canto y abajo, hasta la caja.
        val bajada = listOf(V3(-0.02f, 0.003f, zc), V3(-0.022f, -0.01f, zc - 0.006f), V3(-0.022f, -0.045f, zc - 0.008f))
        trazo(v, base, bajada, ancho, CUERO, V3(0f, 0.2f, -1f))
        cajaDelContador(v, MarcoObjeto(base.w(-0.022f, -0.05f, zc - 0.03f), 0.12f, 3.0f))
    }

    /** La caja del contador, de pie, colgada: la chapa, la esfera, las tres lucecitas, la ruedecilla y el otro cabo de la correa. */
    private fun DrawScope.cajaDelContador(v: VistaRefugio, f: MarcoObjeto) {
        val x0 = -0.034f; val x1 = 0.034f; val y0 = -0.054f; val z0 = -0.012f; val z1 = 0.012f
        caja(v, f, -0.012f, 0.012f, -0.006f, 0.003f, -0.006f, 0.006f, lerp(OLIVA, C.ACERO, 0.4f))
        caja(v, f, x0, x1, y0, -0.004f, z0, z1, OLIVA, lerp(OLIVA, C.PAPEL, 0.15f))
        rayaLuz(v, f, V3(x0, -0.004f, z0), V3(x1, -0.004f, z0), 0.003f, lerp(OLIVA, C.PAPEL, 0.4f), V3(0f, 0.7f, -0.7f), 0.8f)
        for (q in listOf(V3(x0 + 0.005f, -0.009f, z0), V3(x1 - 0.005f, -0.009f, z0), V3(x0 + 0.005f, y0 + 0.005f, z0), V3(x1 - 0.005f, y0 + 0.005f, z0))) disco(v, f, q + V3(0f, 0f, -0.0005f), V3(0f, 0f, -1f), 0.002f, C.ACERO, 6)
        // La esfera: el cristal claro, el arco de tres colores, la aguja en el verde y el brillo.
        val e = V3(0f, -0.022f, z0 - 0.0006f); val fr = V3(0f, 0f, -1f)
        disco(v, f, e, fr, 0.016f, lerp(C.ACERO, C.TINTA, 0.3f), 18)
        disco(v, f, e + V3(0f, 0f, -0.0003f), fr, 0.0135f, lerp(C.PAPEL, Color.White, 0.2f), 18)
        for ((k, col) in listOf(C.ALGA, C.AMARILLO, C.ROJO).withIndex()) {
            val a0 = PI_F * (0.95f - 0.3f * k); val a1 = a0 - 0.3f * PI_F
            trazo(v, f, (0..5).map { i -> val a = a0 + (a1 - a0) * i / 5; e + V3(0.0105f * cos(a), 0.0105f * sin(a), -0.0006f) }, 0.0028f, col, fr)
        }
        val ag = PI_F * 0.8f
        raya(f, e + V3(0f, -0.002f, -0.0008f), e + V3(0.011f * cos(ag), 0.011f * sin(ag), -0.0008f), 0.0012f, C.TINTA)
        disco(v, f, e + V3(0f, 0f, -0.0009f), fr, 0.0018f, C.TINTA, 6)
        brillo(v, f, e + V3(-0.009f, 0.007f, -0.001f), e + V3(-0.002f, 0.011f, -0.001f), 0.002f, C.ACERO, V3(-0.3f, 0.5f, -1f), 0.8f)
        // Las tres lucecitas: la verde encendida con su halo; la ámbar y la roja, apagadas.
        for ((k, col) in listOf(LUZ_VERDE, lerp(C.AMARILLO, C.TINTA, 0.55f), lerp(C.ROJO, C.TINTA, 0.5f)).withIndex()) {
            val q = V3(-0.016f + 0.016f * k, -0.045f, z0 - 0.0008f)
            disco(v, f, q, fr, 0.0058f, lerp(C.ACERO, C.TINTA, 0.2f), 10)
            disco(v, f, q + V3(0f, 0f, -0.0003f), fr, 0.0042f, col, 10, luz = k != 0)
            if (k == 0) { disco(v, f, q + V3(-0.001f, 0.001f, -0.0005f), fr, 0.0016f, Color.White, 6, luz = false); halo(f, q, 0.022f, LUZ_VERDE, 0.4f + 0.45f * v.noche) }
        }
        // La ruedecilla del costado y el otro cabo de la correa, que cuelga con sus agujeros.
        cil(v, f, V3(x1, -0.02f, 0f), V3(x1 + 0.006f, -0.02f, 0f), 0.006f, C.ACERO, lerp(C.ACERO, Color.White, 0.3f), 10)
        caja(v, f, -0.012f, 0.012f, y0 - 0.004f, y0, -0.006f, 0.006f, lerp(OLIVA, C.ACERO, 0.4f))
        val cabo = listOf(V3(0f, y0 - 0.004f, 0f), V3(0.002f, y0 - 0.03f, -0.002f), V3(0.006f, y0 - 0.05f, -0.004f))
        trazo(v, f, cabo, 0.012f, CUERO, fr)
        for (k in 1..3) disco(v, f, V3(0.002f + 0.0015f * k, y0 - 0.012f * k - 0.006f, -0.008f), fr, 0.0018f, lerp(C.TINTA, CUERO, 0.2f), 6)
    }

    // =================================================================
    //  La ración
    // =================================================================

    /**
     * La ración: «se recoge en el comedor con una tarjeta perforada…; los domingos hay un extra, de las latas de antes de la
     * guerra». Una lata vieja cerrada (el papel rojo desvaído con una franja crema y un círculo amarillo, sin letras; el
     * óxido, la abolladura, los bordes del cierre) y, recostada en ella, la tarjeta de cartulina con su franja de color, la
     * esquina cortada y sus casillas: las ya perforadas (los días gastados del mes) dejan ver lo de detrás.
     */
    fun DrawScope.racion(v: VistaRefugio, o: V3, tapa: Path) {
        val f = MarcoObjeto(o, -0.15f, 2.25f)
        val c = V3(0.035f, 0f, 0.035f); val r = 0.042f; val h = 0.11f
        val ancho = 0.08f; val alto = 0.072f; val inclina = 0.5f; val z0 = -0.03f
        val ci = cos(inclina); val si = sin(inclina)
        fun t(u: Float, hh: Float, d: Float = 0f) = V3(u, hh * ci - d * si, z0 + hh * si + d * ci)
        sombra(v, f, TrazoObjeto.circulo(c, V3(0f, 1f, 0f), r, 10) + listOf(t(-ancho, 0f), t(ancho, 0f)), TrazoObjeto.circulo(c + V3(0f, h, 0f), V3(0f, 1f, 0f), r, 8) + listOf(t(-ancho, alto), t(ancho, alto)), o.y, tapa)
        // La lata: la hojalata, el papel, la franja, el sol del papel, el óxido, la abolladura, los cierres y la tapa.
        cil(v, f, c, c + V3(0f, h, 0f), r, HOJALATA, lerp(HOJALATA, Color.White, 0.2f), 16)
        cil(v, f, c + V3(0f, 0.012f, 0f), c + V3(0f, h - 0.012f, 0f), r + 0.0006f, ETIQUETA, ETIQUETA, 16, tapas = false)
        cil(v, f, c + V3(0f, 0.052f, 0f), c + V3(0f, 0.068f, 0f), r + 0.001f, lerp(C.PAPEL, C.AMARILLO, 0.15f), lerp(C.PAPEL, C.AMARILLO, 0.15f), 16, tapas = false)
        val ojo = f.haciaLosOjos(c)
        disco(v, f, c + V3(0f, 0.088f, 0f) + ojo * (r + 0.0015f) + V3(-ojo.z, 0f, ojo.x) * 0.012f, ojo, 0.011f, lerp(C.AMARILLO, C.NARANJA, 0.2f), 12)
        disco(v, f, c + V3(0f, 0.03f, 0f) + ojo * (r + 0.0015f) + V3(ojo.z, 0f, -ojo.x) * 0.02f, ojo, 0.008f, lerp(C.OXIDO, ETIQUETA, 0.3f), 8)
        disco(v, f, c + V3(0f, 0.04f, 0f) + ojo * (r + 0.0012f) + V3(-ojo.z, 0f, ojo.x) * 0.02f, ojo, 0.007f, lerp(ETIQUETA, C.TINTA, 0.35f), 8)
        for (y in listOf(0.004f, h - 0.003f)) Trazo3.run { aro(f.w(c + V3(0f, y, 0f)), f.n(0f, 1f, 0f), (r + 0.002f) * f.amplia, 0.004f * f.amplia, TrazoObjeto.luz(v, f, c + V3(0f, y, -r), V3(0f, 0.3f, -1f), lerp(HOJALATA, Color.White, 0.3f)), 18) }
        Trazo3.run { aro(f.w(c + V3(0f, h + 0.001f, 0f)), f.n(0f, 1f, 0f), (r - 0.008f) * f.amplia, 0.002f * f.amplia, TrazoObjeto.luz(v, f, c + V3(0f, h, 0f), V3(0f, 1f, 0f), lerp(HOJALATA, C.TINTA, 0.25f)), 16) }
        // La tarjeta, recostada en la lata: la cartulina, la franja, la esquina cortada, las casillas y las perforadas.
        val esquina = 0.012f
        val carton = listOf(t(-ancho, 0f), t(ancho, 0f), t(ancho, alto), t(-ancho + esquina, alto), t(-ancho, alto - esquina))
        val n = V3(0f, si, -ci)
        caraDegradada(v, f, carton, n, TARJETA, t(0f, alto), t(0f, 0f))
        cara(v, f, listOf(t(-ancho + 0.004f, alto - 0.016f, -0.0005f), t(ancho - 0.004f, alto - 0.016f, -0.0005f), t(ancho - 0.004f, alto - 0.006f, -0.0005f), t(-ancho + esquina + 0.002f, alto - 0.006f, -0.0005f)), n, lerp(C.CHAPA, C.PAPEL, 0.2f))
        val gastadas = 4 + (v.m.azar("racion", "tarjeta").real() * 9f).toInt()
        var i = 0
        for (fila in 0..2) for (col in 0..7) {
            val q = t(-ancho + 0.016f + 0.0185f * col, 0.012f + 0.016f * fila, -0.0006f)
            if (i < gastadas) disco(v, f, q, n, 0.0042f, lerp(C.TINTA, C.SOMBRA, 0.3f), 8)
            else Trazo3.run { aro(f.w(q), f.n(n), 0.004f * f.amplia, 0.0012f * f.amplia, TrazoObjeto.luz(v, f, q, n, lerp(TARJETA, C.COBRE, 0.5f)), 8) }
            i++
        }
        rayaLuz(v, f, t(-ancho, 0f, -0.0005f), t(ancho, 0f, -0.0005f), 0.002f, lerp(TARJETA, C.TINTA, 0.3f), n)
        rayaLuz(v, f, t(-ancho + esquina, alto), t(ancho, alto), 0.002f, lerp(TARJETA, Color.White, 0.4f), V3(0f, 1f, 0f))
    }

    // =================================================================
    //  El cuaderno
    // =================================================================

    /**
     * El cuaderno del turno: «tiene cien años de letras distintas…; las primeras páginas las escribieron los que cerraron la
     * Puerta». Recostado en dos tomos viejos apilados (los de antes, de tela roja y verde gastada, con el lomo a la vista, sus
     * nervios, el tejuelo en blanco y la cinta en las esquinas): abierto mientras ella trabaja ([abierto]), con sus rayas
     * escritas en tintas distintas (sin letras), la raya del margen, el lápiz y la cinta de señal; cerrado, con su tapa verde
     * gastada, el lomo de cinta y los papelitos de señal que asoman.
     */
    fun DrawScope.cuaderno(v: VistaRefugio, o: V3, tapa: Path, abierto: Boolean) {
        val f = MarcoObjeto(o, -0.22f, 1.25f)
        val inc = 0.46f; val ci = cos(inc); val si = sin(inc); val z0 = -0.12f
        fun q(u: Float, s: Float, d: Float = 0f) = V3(u, s * si + d * ci, z0 + s * ci - d * si)
        val fondo = 0.2f
        sombra(v, f, listOf(V3(-0.16f, 0f, z0), V3(0.16f, 0f, z0), V3(0.14f, 0f, 0.27f), V3(-0.15f, 0f, 0.27f)), listOf(q(-0.16f, fondo), q(0.16f, fondo), V3(0.14f, 0.09f, 0.27f), V3(-0.15f, 0.09f, 0.27f)), o.y, tapa)
        // Los tomos viejos.
        tomo(v, f, -0.15f, 0.14f, 0f, 0.045f, 0.03f, 0.27f, TELA_ROJA)
        tomo(v, f, -0.13f, 0.13f, 0.045f, 0.088f, 0.05f, 0.26f, lerp(C.CHAPA, C.TINTA, 0.35f))
        val n = V3(0f, ci, -si)
        // La tapa de debajo, que asoma por los cantos.
        caraDegradada(v, f, listOf(q(-0.165f, -0.004f), q(0.165f, -0.004f), q(0.165f, fondo + 0.006f), q(-0.165f, fondo + 0.006f)), n, TELA_VERDE, q(0f, fondo), q(0f, 0f))
        if (!abierto) { cerrado(v, f, ::q, n, fondo); return }
        // Las dos páginas, que se abomban hacia el lomo, y la raya del lomo.
        for (lado in listOf(-1f, 1f)) {
            val pts = (0..8).map { i -> val u = lado * (0.006f + 0.152f * i / 8f); u to 0.012f * (1f - i / 8f) * (1f - i / 8f) }
            val forma = pts.map { (u, d) -> q(u, 0.004f, d + 0.004f) } + pts.reversed().map { (u, d) -> q(u, fondo - 0.004f, d + 0.004f) }
            caraDegradada(v, f, forma, n, if (lado < 0f) lerp(HOJA, C.PAPEL, 0.5f) else HOJA, q(lado * 0.15f, fondo * 0.5f, 0.004f), q(lado * 0.01f, fondo * 0.5f, 0.016f))
            // El margen y las rayas escritas, en tres tintas: las de hace cien años, desvaídas; las de ayer, azules.
            rayaLuz(v, f, q(lado * 0.035f, 0.01f, 0.012f), q(lado * 0.035f, fondo - 0.01f, 0.012f), 0.0018f, lerp(C.ROJO, C.PAPEL, 0.45f), n, 0.6f)
            for (k in 0..9) {
                val s = fondo - 0.02f - 0.0175f * k
                val tinta = when { lado < 0f && k < 6 -> lerp(C.COBRE, C.PAPEL, 0.35f); lado < 0f -> lerp(C.TINTA, C.PAPEL, 0.35f); k < 5 -> lerp(C.TINTA, C.AZUL, 0.5f); else -> lerp(C.AZUL, C.PAPEL, 0.2f) }
                val largo = 0.06f + 0.06f * ((k * 7 + (if (lado < 0f) 3 else 5)) % 5) / 4f
                if (lado > 0f && k > 7) continue
                var u = 0.042f
                while (u < 0.042f + largo) { val l = 0.012f + 0.01f * (((k + u * 100f).toInt() * 13) % 3); val ue = (u + l).coerceAtMost(0.15f)
                    rayaLuz(v, f, q(lado * u, s, 0.0125f * (1f - u / 0.15f) + 0.0045f), q(lado * ue, s, 0.0125f * (1f - ue / 0.15f) + 0.0045f), 0.0022f, tinta, n, 0.85f); u += l + 0.006f }
            }
        }
        rayaLuz(v, f, q(0f, 0.004f, 0.015f), q(0f, fondo - 0.004f, 0.015f), 0.0025f, lerp(HOJA, C.TINTA, 0.35f), n, 0.6f)
        // La cinta de señal que cae por el canto, y el lápiz cruzado en la página de la derecha.
        trazo(v, f, listOf(q(0.002f, 0.02f, 0.016f), q(0.006f, 0.0f, 0.01f), q(0.01f, -0.015f, -0.01f), q(0.008f, -0.03f, -0.03f)), 0.008f, C.ROJO, n)
        val l0 = q(0.04f, 0.035f, 0.012f); val l1 = q(0.14f, 0.1f, 0.006f)
        rayaLuz(v, f, l0, l1, 0.0085f, C.AMARILLO, n)
        rayaLuz(v, f, l1, l1 + V3(0.008f, 0.004f, 0.002f), 0.006f, lerp(C.ROJO, C.PAPEL, 0.4f), n)
        rayaLuz(v, f, l0, l0 + V3(-0.01f, -0.005f, -0.002f), 0.005f, lerp(C.COBRE, C.PAPEL, 0.4f), n)
        brillo(v, f, l0 + V3(0.01f, 0.007f, 0.001f), l1 + V3(-0.01f, 0.003f, 0.001f), 0.002f, C.AMARILLO, V3(0f, 1f, -0.3f), 0.5f)
    }

    /** El cuaderno cerrado: la tapa de tela verde gastada, el lomo de cinta, el tejuelo en blanco, los papelitos que asoman. */
    private fun DrawScope.cerrado(v: VistaRefugio, f: MarcoObjeto, q: (Float, Float, Float) -> V3, n: V3, fondo: Float) {
        val g = 0.022f
        for (k in 0..4) { val u = -0.12f + 0.055f * k; val alto = 0.02f + 0.012f * (k % 3)
            cara(v, f, listOf(q(u, fondo, g * 0.5f), q(u + 0.03f, fondo, g * 0.5f), q(u + 0.028f, fondo + alto, g * 0.5f), q(u + 0.002f, fondo + alto, g * 0.5f)), n, listOf(C.PAPEL, lerp(C.AMARILLO, C.PAPEL, 0.4f), lerp(C.PISCINA, C.PAPEL, 0.5f))[k % 3]) }
        caraDegradada(v, f, listOf(q(0.162f, 0f, 0f), q(0.162f, fondo, 0f), q(0.162f, fondo, g), q(0.162f, 0f, g)), V3(1f, 0f, 0f), HOJA, q(0.162f, fondo, 0f), q(0.162f, 0f, 0f))
        val cubierta = listOf(q(-0.16f, 0f, g), q(0.16f, 0f, g), q(0.16f, fondo, g), q(-0.16f, fondo, g))
        caraDegradada(v, f, cubierta, n, TELA_VERDE, q(0f, fondo, g), q(0f, 0f, g))
        cara(v, f, listOf(q(-0.16f, 0f, g + 0.0005f), q(-0.13f, 0f, g + 0.0005f), q(-0.13f, fondo, g + 0.0005f), q(-0.16f, fondo, g + 0.0005f)), n, lerp(C.PAPEL, C.AMARILLO, 0.3f), 0.85f)
        cara(v, f, listOf(q(-0.03f, 0.09f, g + 0.0005f), q(0.08f, 0.09f, g + 0.0005f), q(0.08f, 0.14f, g + 0.0005f), q(-0.03f, 0.14f, g + 0.0005f)), n, lerp(C.PAPEL, C.HORMIGON, 0.15f))
        rayaLuz(v, f, q(-0.03f, 0.09f, g + 0.0007f), q(0.08f, 0.09f, g + 0.0007f), 0.0015f, C.TINTA, n, 0.5f)
        for ((a, b) in listOf(q(0.16f, fondo, g) to q(0.13f, fondo, g), q(0.16f, 0f, g) to q(0.14f, 0f, g))) rayaLuz(v, f, a, b, 0.006f, lerp(TELA_VERDE, C.PAPEL, 0.45f), n, 0.8f)
    }

    /** Un tomo viejo, tumbado, con el lomo a la derecha: la tela, el canto de las hojas, los nervios y el tejuelo del lomo. */
    private fun DrawScope.tomo(v: VistaRefugio, f: MarcoObjeto, x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float, tela: Color) {
        caja(v, f, x0, x1, y0, y1, z0, z1, tela, lerp(tela, C.PAPEL, 0.08f))
        cara(v, f, listOf(V3(x0 + 0.004f, y0 + 0.005f, z0 - 0.0005f), V3(x1 - 0.012f, y0 + 0.005f, z0 - 0.0005f), V3(x1 - 0.012f, y1 - 0.005f, z0 - 0.0005f), V3(x0 + 0.004f, y1 - 0.005f, z0 - 0.0005f)), V3(0f, 0f, -1f), lerp(HOJA, C.HORMIGON, 0.3f))
        for (k in 1..3) rayaLuz(v, f, V3(x0 + 0.01f, y0 + 0.005f + (y1 - y0 - 0.01f) * k / 4f, z0 - 0.001f), V3(x1 - 0.015f, y0 + 0.005f + (y1 - y0 - 0.01f) * k / 4f, z0 - 0.001f), 0.0008f, lerp(HOJA, C.TINTA, 0.3f), V3(0f, 0f, -1f), 0.5f)
        for (z in listOf(z0 + 0.04f, z1 - 0.05f)) rayaLuz(v, f, V3(x1 + 0.001f, y0 + 0.004f, z), V3(x1 + 0.001f, y1 - 0.004f, z), 0.005f, lerp(tela, C.PAPEL, 0.3f), V3(1f, 0f, 0f))
        cara(v, f, listOf(V3(x1 + 0.0015f, y0 + 0.01f, z0 + 0.08f), V3(x1 + 0.0015f, y0 + 0.01f, z0 + 0.14f), V3(x1 + 0.0015f, y1 - 0.01f, z0 + 0.14f), V3(x1 + 0.0015f, y1 - 0.01f, z0 + 0.08f)), V3(1f, 0f, 0f), lerp(C.PAPEL, C.HORMIGON, 0.2f))
        cara(v, f, listOf(V3(x0, y1 + 0.0005f, z0), V3(x0 + 0.04f, y1 + 0.0005f, z0), V3(x0, y1 + 0.0005f, z0 + 0.04f)), V3(0f, 1f, 0f), lerp(C.PAPEL, C.AMARILLO, 0.3f), 0.8f)
    }

    // =================================================================
    //  La pastilla
    // =================================================================

    /**
     * La pastilla de yodo: «en una cajita metálica que cada uno lleva en el bolsillo del mono…; a los niños les dicen que son
     * caramelos de emergencia». La cajita de hojalata con su franja naranja pintada, abierta: la tapa levantada, con un
     * círculo repujado por dentro; dentro, en su hueco, las pastillas blancas con su raya; una, fuera, en la tapa de la
     * bancada.
     */
    fun DrawScope.pastilla(v: VistaRefugio, o: V3, tapa: Path) {
        val f = MarcoObjeto(o, -0.4f, 3.6f)
        val x0 = -0.034f; val x1 = 0.034f; val y1 = 0.012f; val z0 = -0.022f; val z1 = 0.022f
        val ang = 1.85f; val dir = V3(0f, sin(ang), -cos(ang)); val nTapa = V3(0f, -cos(ang), -sin(ang))
        fun l(x: Float, s: Float, d: Float = 0f) = V3(x, y1 + dir.y * s + nTapa.y * d, z1 + dir.z * s + nTapa.z * d)
        sombra(v, f, listOf(V3(x0, 0f, z0), V3(x1, 0f, z0), V3(x1, 0f, z1), V3(x0, 0f, z1)), listOf(V3(x0, y1, z0), V3(x1, y1, z0), l(x0, 0.044f), l(x1, 0.044f)), o.y, tapa)
        caja(v, f, x0, x1, 0.001f, y1, z0, z1, HOJALATA, lerp(HOJALATA, C.TINTA, 0.4f))
        // La franja pintada y su pintura saltada en el canto.
        cara(v, f, listOf(V3(x0, 0.003f, z0 - 0.0004f), V3(x1, 0.003f, z0 - 0.0004f), V3(x1, 0.008f, z0 - 0.0004f), V3(x0, 0.008f, z0 - 0.0004f)), V3(0f, 0f, -1f), lerp(C.NARANJA, C.PAPEL, 0.15f))
        rayaLuz(v, f, V3(x0 + 0.01f, 0.006f, z0 - 0.0006f), V3(x0 + 0.018f, 0.006f, z0 - 0.0006f), 0.002f, HOJALATA, V3(0f, 0f, -1f))
        rayaLuz(v, f, V3(x0, y1, z0), V3(x1, y1, z0), 0.0018f, lerp(HOJALATA, Color.White, 0.5f), V3(0f, 0.7f, -0.7f), 0.9f)
        // El hueco de dentro y las pastillas.
        cara(v, f, listOf(V3(x0 + 0.003f, y1 + 0.0003f, z0 + 0.003f), V3(x1 - 0.003f, y1 + 0.0003f, z0 + 0.003f), V3(x1 - 0.003f, y1 + 0.0003f, z1 - 0.003f), V3(x0 + 0.003f, y1 + 0.0003f, z1 - 0.003f)), V3(0f, 1f, 0f), lerp(C.HORMIGON, C.TINTA, 0.35f))
        for ((k, p) in listOf(V3(-0.02f, 0f, -0.008f), V3(-0.004f, 0f, -0.009f), V3(0.012f, 0f, -0.007f), V3(-0.012f, 0f, 0.008f), V3(0.005f, 0f, 0.008f), V3(0.02f, 0f, 0.009f)).withIndex()) {
            if (k == 5) continue
            pildora(v, f, p + V3(0f, y1 - 0.002f, 0f), k)
        }
        // La tapa levantada: el canto, la cara de dentro, el círculo repujado y su brillo.
        caja(v, f, x0, x1, y1, y1 + 0.003f, z1 - 0.001f, z1 + 0.001f, HOJALATA)
        val dentro = listOf(l(x0, 0f), l(x1, 0f), l(x1, 0.044f), l(x0, 0.044f))
        caraDegradada(v, f, dentro, nTapa, lerp(HOJALATA, Color.White, 0.15f), l(0f, 0.044f), l(0f, 0f))
        Trazo3.run { aro(f.w(l(0f, 0.022f, 0.0006f)), f.n(nTapa), 0.012f * f.amplia, 0.0016f * f.amplia, TrazoObjeto.luz(v, f, l(0f, 0.022f), nTapa, lerp(HOJALATA, C.TINTA, 0.25f)), 16) }
        val eu = V3(1f, 0f, 0f); val ev = dir
        disco(v, f, l(0f, 0.022f, 0.0008f), nTapa, 0.0045f, lerp(HOJALATA, C.TINTA, 0.2f), 10)
        for (j in 0 until 8) { val a = PI_F * j / 4f; val c0 = l(0f, 0.022f, 0.0009f)
            rayaLuz(v, f, c0 + eu * (0.0065f * cos(a)) + ev * (0.0065f * sin(a)), c0 + eu * (0.0095f * cos(a)) + ev * (0.0095f * sin(a)), 0.0014f, lerp(HOJALATA, C.TINTA, 0.25f), nTapa) }
        brillo(v, f, l(x0 + 0.006f, 0.04f, 0.001f), l(x0 + 0.006f, 0.008f, 0.001f), 0.0025f, HOJALATA, nTapa, 0.8f)
        rayaLuz(v, f, l(x0, 0.044f), l(x1, 0.044f), 0.0018f, lerp(HOJALATA, Color.White, 0.4f), V3(0f, 1f, 0f))
        // La que está fuera, delante, en la bancada.
        pildora(v, f, V3(0.05f, 0f, -0.032f), 5)
    }

    /** Una pastilla blanca: el canto, la cara de arriba y su raya. */
    private fun DrawScope.pildora(v: VistaRefugio, f: MarcoObjeto, c: V3, k: Int) {
        cil(v, f, c, c + V3(0f, 0.0035f, 0f), 0.0062f, lerp(PASTILLA, C.HORMIGON, 0.25f), PASTILLA, 12)
        val a = 0.6f * k
        rayaLuz(v, f, c + V3(-0.005f * cos(a), 0.0037f, -0.005f * sin(a)), c + V3(0.005f * cos(a), 0.0037f, 0.005f * sin(a)), 0.0012f, lerp(PASTILLA, C.TINTA, 0.35f), V3(0f, 1f, 0f), 0.7f)
    }

    // =================================================================
    //  La foto
    // =================================================================

    /**
     * La foto de fuera: «pocas, de antes de la guerra…: playas, ciudades, montañas, gente en un parque; se pasan de mano en
     * mano». De pie, un poco recostada, en su cartón de fotógrafo con el filete repujado y la pata de cartón detrás; la foto,
     * coloreada a mano y desvaída, con su borde blanco dentado, un pliegue y una esquina doblada: una montaña con nieve
     * sobre un lago, o una playa con su roca y su sol, según el día (sin gente) **[Diseño propio]**.
     */
    fun DrawScope.foto(v: VistaRefugio, o: V3, tapa: Path) {
        val f = MarcoObjeto(o, -0.12f, 1.8f)
        val ancho = 0.1f; val alto = 0.13f; val inc = 0.2f; val z0 = 0.0f
        val ci = cos(inc); val si = sin(inc)
        fun s(u: Float, h: Float, d: Float = 0f) = V3(u, h * ci + d * si, z0 + h * si - d * ci)
        val n = V3(0f, si, -ci)
        sombra(v, f, listOf(s(-ancho, 0f), s(ancho, 0f), V3(0.01f, 0f, 0.09f)), listOf(s(-ancho, alto), s(ancho, alto)), o.y, tapa)
        // La pata de cartón de detrás, que asoma por el costado.
        rayaLuz(v, f, s(0.03f, alto * 0.75f, -0.002f), V3(0.035f, 0.002f, 0.09f), 0.012f, lerp(CARTON, C.TINTA, 0.3f), V3(1f, 0.2f, 0f))
        // El cartón: el grueso del canto, la cara con su luz y el filete repujado.
        cara(v, f, listOf(s(ancho, 0f), s(ancho, alto), s(ancho, alto, -0.004f), s(ancho, 0f, -0.004f)), V3(1f, 0f, 0f), lerp(CARTON, C.TINTA, 0.25f))
        caraDegradada(v, f, listOf(s(-ancho, 0f), s(ancho, 0f), s(ancho, alto), s(-ancho, alto)), n, CARTON, s(-ancho * 0.5f, alto), s(ancho * 0.5f, 0f))
        trazo(v, f, listOf(s(-ancho + 0.008f, 0.008f, 0.0005f), s(ancho - 0.008f, 0.008f, 0.0005f), s(ancho - 0.008f, alto - 0.008f, 0.0005f), s(-ancho + 0.008f, alto - 0.008f, 0.0005f), s(-ancho + 0.008f, 0.008f, 0.0005f)), 0.0025f, lerp(CARTON, Color.White, 0.35f), n, 0.7f)
        // El borde blanco de la foto, dentado abajo, con la esquina de arriba a la derecha doblada.
        val b0 = -ancho + 0.016f; val b1 = ancho - 0.016f; val c0 = 0.02f; val c1 = alto - 0.018f
        cara(v, f, listOf(s(b0, c0, 0.001f), s(b1, c0, 0.001f), s(b1, c1 - 0.016f, 0.001f), s(b1 - 0.016f, c1, 0.001f), s(b0, c1, 0.001f)), n, lerp(C.PAPEL, Color.White, 0.45f))
        cara(v, f, listOf(s(b1, c1 - 0.016f, 0.0015f), s(b1 - 0.016f, c1, 0.0015f), s(b1 - 0.014f, c1 - 0.014f, 0.004f)), n, lerp(C.PAPEL, C.HORMIGON, 0.25f))
        for (k in 0..13) { val u = b0 + (b1 - b0) * k / 14f; cara(v, f, listOf(s(u, c0, 0.0012f), s(u + (b1 - b0) / 14f, c0, 0.0012f), s(u + (b1 - b0) / 28f, c0 - 0.004f, 0.0012f)), n, lerp(C.PAPEL, Color.White, 0.45f)) }
        // La foto.
        val u0 = b0 + 0.008f; val u1 = b1 - 0.008f; val h0 = c0 + 0.008f; val h1 = c1 - 0.008f
        fun en(u: Float, h: Float) = s(u0 + (u1 - u0) * u, h0 + (h1 - h0) * h, 0.0016f)
        val marco = listOf(en(0f, 0f), en(1f, 0f), en(1f, 0.82f), en(0.86f, 1f), en(0f, 1f))
        clipPath(CamRefugio.forma(marco.map { f.w(it) })) { if (v.m.azar("foto", "cual").real() < 0.5f) montana(v, f, n, ::en) else playa(v, f, n, ::en) }
        // El pliegue que la cruza.
        rayaLuz(v, f, en(0.68f, 1f), en(0.92f, 0f), 0.0013f, Color.White, n, 0.5f)
    }

    /** La montaña de la foto: el cielo, dos cimas con nieve, el monte oscuro de delante, el lago y la orilla. */
    private fun DrawScope.montana(v: VistaRefugio, f: MarcoObjeto, n: V3, en: (Float, Float) -> V3) {
        val k = 0.3f                                                          // lo desvaído de una foto coloreada a mano
        cara(v, f, listOf(en(0f, 0f), en(1f, 0f), en(1f, 1f), en(0f, 1f)), n, lerp(lerp(C.PISCINA, C.PAPEL, 0.35f), C.PAPEL, k))
        cara(v, f, listOf(en(0f, 0.45f), en(0.18f, 0.7f), en(0.32f, 0.88f), en(0.45f, 0.62f), en(0.6f, 0.78f), en(0.8f, 0.55f), en(1f, 0.5f), en(1f, 0.3f), en(0f, 0.3f)), n, lerp(lerp(C.AZUL, C.SOMBRA, 0.3f), C.PAPEL, k))
        cara(v, f, listOf(en(0.24f, 0.78f), en(0.32f, 0.88f), en(0.39f, 0.75f), en(0.34f, 0.72f), en(0.3f, 0.76f)), n, lerp(Color.White, C.PAPEL, 0.25f))
        cara(v, f, listOf(en(0.54f, 0.71f), en(0.6f, 0.78f), en(0.67f, 0.7f), en(0.62f, 0.68f)), n, lerp(Color.White, C.PAPEL, 0.25f))
        cara(v, f, listOf(en(0f, 0.32f), en(0.25f, 0.48f), en(0.42f, 0.36f), en(0.62f, 0.44f), en(1f, 0.3f), en(1f, 0.2f), en(0f, 0.2f)), n, lerp(lerp(C.VERDE, C.TINTA, 0.3f), C.PAPEL, k * 0.6f))
        cara(v, f, listOf(en(0f, 0.22f), en(1f, 0.22f), en(1f, 0.05f), en(0f, 0.05f)), n, lerp(lerp(C.PISCINA, C.CHAPA, 0.4f), C.PAPEL, k))
        for (j in 0..2) rayaLuz(v, f, en(0.1f + 0.25f * j, 0.15f - 0.03f * j), en(0.22f + 0.25f * j, 0.15f - 0.03f * j), 0.0012f, Color.White, n, 0.5f)
        cara(v, f, listOf(en(0f, 0.08f), en(0.35f, 0.04f), en(0.7f, 0.07f), en(1f, 0.03f), en(1f, 0f), en(0f, 0f)), n, lerp(lerp(C.ALGA, C.COBRE, 0.3f), C.PAPEL, k))
    }

    /** La playa de la foto: el cielo, el sol, el mar con su espuma, la arena y la roca de un lado. */
    private fun DrawScope.playa(v: VistaRefugio, f: MarcoObjeto, n: V3, en: (Float, Float) -> V3) {
        val k = 0.3f
        cara(v, f, listOf(en(0f, 0f), en(1f, 0f), en(1f, 1f), en(0f, 1f)), n, lerp(lerp(C.PISCINA, C.PAPEL, 0.4f), C.PAPEL, k))
        cara(v, f, (0..10).map { i -> val a = 2f * PI_F * i / 10; en(0.72f + 0.07f * cos(a), 0.78f + 0.1f * sin(a)) }, n, lerp(lerp(C.AMARILLO, Color.White, 0.4f), C.PAPEL, k * 0.5f))
        cara(v, f, listOf(en(0f, 0.55f), en(1f, 0.55f), en(1f, 0.28f), en(0f, 0.34f)), n, lerp(lerp(C.PISCINA, C.CHAPA, 0.45f), C.PAPEL, k))
        trazo(v, f, (0..8).map { i -> en(i / 8f, 0.33f - 0.06f * i / 8f + 0.015f * sin(i * 1.7f)) }, 0.003f, Color.White, n, 0.8f)
        cara(v, f, listOf(en(0f, 0.34f), en(1f, 0.28f), en(1f, 0f), en(0f, 0f)), n, lerp(lerp(C.PAPEL, C.AMARILLO, 0.35f), C.COBRE, 0.12f))
        cara(v, f, listOf(en(0f, 0.25f), en(0f, 0.72f), en(0.08f, 0.76f), en(0.18f, 0.6f), en(0.26f, 0.42f), en(0.3f, 0.3f), en(0.2f, 0.2f)), n, lerp(lerp(C.ROCA, C.TINTA, 0.2f), C.PAPEL, k * 0.6f))
        rayaLuz(v, f, en(0.08f, 0.7f), en(0.16f, 0.55f), 0.002f, lerp(C.ROCA, C.PAPEL, 0.5f), n, 0.7f)
        for (j in 0..2) rayaLuz(v, f, en(0.45f + 0.15f * j, 0.47f - 0.05f * j), en(0.55f + 0.15f * j, 0.47f - 0.05f * j), 0.001f, Color.White, n, 0.5f)
    }

    // =================================================================
    //  La flor de papel
    // =================================================================

    /**
     * La flor de papel: «de papel, de las hojas viejas de los libros…, dobladas y pintadas; se ponen en las mesas en las
     * fiestas…, delante de la Puerta». Dos, tumbadas en la bancada con la corola hacia los ojos: una grande pintada de rojo y
     * otra más chica de amarillo. Cada pétalo, una hoja de libro doblada, con sus renglones grises (sin letras) que se ven
     * bajo la pintura y el canto más pintado; en medio, el papel enrollado. El tallo, de alambre forrado de papel verde, y una
     * hoja verde con su doblez.
     */
    fun DrawScope.flor(v: VistaRefugio, o: V3, tapa: Path) {
        val f = MarcoObjeto(o, 0f, 2.3f)
        val grande = V3(-0.025f, 0.042f, -0.02f); val chica = V3(0.075f, 0.03f, 0.03f)
        val ejeG = V3(-0.2f, 0.55f, -1f); val ejeC = V3(0.35f, 0.6f, -1f)
        sombra(v, f, TrazoObjeto.circulo(grande + V3(0f, -0.04f, 0.01f), V3(0f, 1f, 0f), 0.03f, 8) + listOf(V3(0.1f, 0f, 0.15f)), TrazoObjeto.circulo(grande, CamRefugio.unidad(ejeG), 0.055f, 10) + TrazoObjeto.circulo(chica, CamRefugio.unidad(ejeC), 0.04f, 8), o.y, tapa)
        // Los tallos y la hoja, detrás.
        for ((c, fin) in listOf(grande to V3(0.12f, 0.004f, 0.16f), chica to V3(0.17f, 0.004f, 0.12f))) {
            val tallo = listOf(c + V3(0.01f, -0.01f, 0.03f), c + V3(0.05f, -0.025f, 0.08f), fin)
            trazo(v, f, tallo, 0.006f, lerp(C.ALGA, C.TINTA, 0.3f))
            for (k in 1..4) { val t = k / 5f; val p = tallo[1] + (fin - tallo[1]) * t; rayaLuz(v, f, p + V3(-0.003f, 0.003f, 0f), p + V3(0.003f, -0.002f, 0.004f), 0.002f, lerp(C.ALGA, C.PAPEL, 0.3f), V3(0f, 1f, 0f), 0.7f) }
        }
        hojaVerde(v, f, V3(0.06f, 0.008f, 0.09f))
        corola(v, f, chica, CamRefugio.unidad(ejeC), 0.04f, lerp(C.AMARILLO, C.NARANJA, 0.2f), 5)
        corola(v, f, grande, CamRefugio.unidad(ejeG), 0.058f, lerp(C.ROJO, C.NARANJA, 0.15f), 6)
    }

    /** Una hoja de papel pintada de verde, tumbada, con su doblez en medio. */
    private fun DrawScope.hojaVerde(v: VistaRefugio, f: MarcoObjeto, c: V3) {
        val punta = c + V3(0.06f, 0.002f, -0.03f); val ancho = 0.02f
        cara(v, f, listOf(c, c + V3(0.025f, 0.006f, -0.03f) + V3(0f, 0f, -ancho * 0.4f), punta, c + V3(0.035f, 0.004f, 0.0f) + V3(0f, 0f, ancho * 0.4f)), V3(0f, 1f, 0f), lerp(C.ALGA, LIBRO, 0.25f))
        rayaLuz(v, f, c, punta, 0.0018f, lerp(C.ALGA, C.TINTA, 0.35f), V3(0f, 1f, 0f), 0.8f)
    }

    /**
     * Una corola de papel en [c], mirando a [eje], de radio [r], pintada de [pintura]: tres vueltas de pétalos (de fuera
     * adentro), cada uno con sus renglones bajo la pintura y el canto más pintado, y el papel enrollado en medio.
     */
    private fun DrawScope.corola(v: VistaRefugio, f: MarcoObjeto, c: V3, eje: V3, r: Float, pintura: Color, n: Int) {
        val u = CamRefugio.unidad(CamRefugio.cruz(eje, V3(0f, 1f, 0f))); val w = CamRefugio.cruz(u, eje)
        fun en(rad: Float, a: Float, hondo: Float) = c + u * (rad * cos(a)) + w * (rad * sin(a)) + eje * hondo
        for ((vuelta, datos) in listOf(Triple(1f, n, -0.004f), Triple(0.7f, n - 1, 0.006f), Triple(0.45f, n - 2, 0.013f)).withIndex()) {
            val rv = r * datos.first; val cuantos = datos.second; val desfase = 0.5f * vuelta
            for (j in 0 until cuantos) {
                val a = desfase + 2f * PI_F * j / cuantos; val ancho = PI_F / cuantos * 1.15f
                val borde = listOf(en(rv * 0.2f, a - ancho * 0.5f, datos.third), en(rv * 0.75f, a - ancho, datos.third + 0.003f), en(rv * 1.02f, a - ancho * 0.55f, datos.third + 0.008f), en(rv * 1.08f, a, datos.third + 0.01f), en(rv * 1.02f, a + ancho * 0.55f, datos.third + 0.008f), en(rv * 0.75f, a + ancho, datos.third + 0.003f), en(rv * 0.2f, a + ancho * 0.5f, datos.third))
                val nn = CamRefugio.unidad(eje + (u * cos(a) + w * sin(a)) * (0.5f - 0.25f * vuelta))
                cara(v, f, borde, nn, LIBRO)
                // Los renglones de la página, que se ven bajo la pintura.
                for (k in 0..2) { val rr = rv * (0.4f + 0.18f * k); rayaLuz(v, f, en(rr, a - ancho * 0.45f, datos.third + 0.004f), en(rr, a + ancho * 0.35f, datos.third + 0.004f), 0.0011f, lerp(C.TINTA, LIBRO, 0.35f), nn, 0.55f) }
                cara(v, f, borde, nn, pintura, 0.55f)
                trazo(v, f, borde.subList(1, 6), 0.0035f, lerp(pintura, C.TINTA, 0.15f), nn, 0.9f)
            }
        }
        // El papel enrollado del centro: una espiral.
        trazo(v, f, (0..16).map { i -> val t = i / 16f; en(r * 0.22f * (1f - 0.8f * t), 6f * PI_F * t, 0.016f) }, 0.004f, lerp(LIBRO, pintura, 0.6f), eje)
        disco(v, f, en(0f, 0f, 0.017f), eje, r * 0.06f, lerp(pintura, C.TINTA, 0.3f), 8)
    }
}
