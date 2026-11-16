package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Jardines
import io.github.ruddy2003.vestigium.ui.escenas.HojasCentro.flor
import io.github.ruddy2003.vestigium.ui.escenas.HojasCentro.hojaJudia
import io.github.ruddy2003.vestigium.ui.escenas.HojasCentro.hojaLejos
import io.github.ruddy2003.vestigium.ui.escenas.HojasCentro.hojaTomate
import io.github.ruddy2003.vestigium.ui.escenas.HojasCentro.masa
import io.github.ruddy2003.vestigium.ui.escenas.HojasCentro.tomate
import io.github.ruddy2003.vestigium.ui.escenas.HojasCentro.vaina
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.caja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.cilindro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.linea
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.trazo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El verde del hall, sin árboles («los pasillos tienen nombres de árboles que nadie ha visto»), un hall lleno de cultivo bajo
 * luz artificial **[Diseño propio]** (lo cuida Botánica):
 * - **cortinas de judías** que caen del canto de las galerías de enfrente y que suben por hilos desde jardineras del comedor;
 * - **dos columnas de cultivo** en el suelo del comedor, cilindros de bandejas con su luz propia;
 * - **jardineras en los antepechos** de la primera galería, con lechugas y tomateras con fruta, que gotean;
 * - **tomateras** en macetones junto al mostrador;
 * - cerca, **la cortina** que cuelga junto a la costilla y **la jardinera grande** de la galería, a la derecha, cortada por el
 *   borde, con sus tomateras y sus hilos de judías.
 * Todo sigue el arco `jardines`, como los tanques del cuento: tras la siega, una pelusa de brotes en hilos casi desnudos; luego
 * las judías «hasta la mitad de los hilos» y los tomates verdes; llenos, hasta arriba, con vainas, flores y «los tomates se
 * doblan de rojo».
 */
internal object VerdeCentro {
    private val C = ColorLaboratorio

    /** Hasta dónde ha subido el verde por los hilos, de 0 a 1, según el tramo de los jardines. */
    fun subida(j: Jardines): Float = when (j) { Jardines.VACIOS -> 0f; Jardines.BROTES -> 0.12f; Jardines.MEDIO -> 0.5f; Jardines.LLENOS -> 1f }

    /** Cuánto han madurado los tomates, de 0 (verdes) a 1 (rojos); sin tomates tras la siega. */
    fun madurez(j: Jardines): Float = when (j) { Jardines.VACIOS, Jardines.BROTES -> -1f; Jardines.MEDIO -> 0.25f; Jardines.LLENOS -> 1f }

    /** Las columnas de judías que suben desde jardineras del comedor hasta el canto de la primera galería (grados). */
    private val COLUMNAS = listOf(31f, 150f, 176f)

    /** Las cascadas que caen del canto de las galerías: (desde, hasta) en grados, la altura del canto y cuánto caen como mucho. */
    private class Cascada(val g0: Float, val g1: Float, val y: Float, val largo: Float)
    private val CASCADAS = listOf(
        Cascada(48f, 62f, GeoCentro.G2 - GeoCentro.LOSA, 2.2f), Cascada(88f, 104f, GeoCentro.G2 - GeoCentro.LOSA, 2.6f), Cascada(128f, 142f, GeoCentro.G2 - GeoCentro.LOSA, 2.0f),
        Cascada(166f, 182f, GeoCentro.G2 - GeoCentro.LOSA, 2.4f), Cascada(68f, 82f, GeoCentro.G2 - GeoCentro.LOSA, 2.3f), Cascada(140f, 156f, GeoCentro.G1 - GeoCentro.LOSA, 2.0f),
        Cascada(36f, 44f, GeoCentro.G1 - GeoCentro.LOSA, 1.5f),
    )

    /** Las dos columnas de cultivo del comedor (x, z en metros). */
    val TORRES = listOf(0.0f to 6.8f, 6.9f to 4.2f)
    const val TORRE_ALTO = 2.5f
    const val TORRE_R = 0.55f

    fun DrawScope.columnas(v: VistaCentro) {
        val sube = subida(v.jardines)
        for (g in COLUMNAS) columna(v, g, sube)
        for (c in CASCADAS) cascada(v, c, sube)
        tomateras(v)
        antepechos(v)
    }

    /** Una columna de judías lejana: la jardinera, los hilos tensos y las hojas que suben por ellos, en masa, con las claras en el borde. */
    private fun DrawScope.columna(v: VistaCentro, g: Float, sube: Float) {
        val r = GeoCentro.RV - 0.5f; val y1 = GeoCentro.G1 - GeoCentro.LOSA
        val a = GeoCentro.rad(g); val t = V3(-sin(a), 0f, cos(a)); val n = V3(-cos(a), 0.2f, -sin(a))
        val pie = GeoCentro.aro(r, 0f, g)
        if (!CamCentro.enCuadro(CamCentro.p(pie), 20f)) return
        val lj = GaleriaCentro.lejos(pie)
        caja(v, pie, t, 1.6f, 0.5f, 0.45f, lerp(C.HORMIGON, C.TIERRA, 0.3f), lj, tapa = lerp(C.TIERRA, C.NEGRO, 0.25f))
        val az = Azar(Semilla.de("laboratorio", "centro", "columna", g.toInt()))
        val hilo = lerp(C.TELA, C.TIERRA, 0.3f)
        val hilos = 9
        for (i in 0 until hilos) {
            val s = -0.7f + 1.4f * i / (hilos - 1f)
            val b = pie + t * s + V3(0f, 0.45f, 0f); val c = GeoCentro.aro(r + 0.2f, y1, g) + t * (s * 0.85f)
            linea(v, b, c, hilo, n, 0.35f, 0.55f)
            if (sube <= 0f) continue
            val alto = (c.y - b.y) * sube
            var h = az.entre(0f, 0.1f)
            while (h < alto) {
                val w = b + (c - b) * (h / (c.y - b.y)) + V3(az.entre(-0.08f, 0.08f), 0f, az.entre(-0.08f, 0.08f))
                val delante = az.real() > 0.45f
                hojaLejos(v, w, 0.07f, HojasCentro.tonoAlAzar(az, delante), n, lj, az)
                if (v.jardines == Jardines.LLENOS && az.real() > 0.88f) drawCircle(v.c(w, C.TELA, n, lj), (0.025f * CamCentro.k(w)).coerceAtLeast(0.4f), CamCentro.p(w) + Offset(1f, 0f))
                h += 0.09f + az.entre(0f, 0.06f)
            }
        }
    }

    /**
     * Una cascada de judías que cae del canto de una galería, de su jardinera del antepecho: hebras que cuelgan a distintos
     * largos (más largas cuanto más llenos los jardines), con la masa oscura detrás y hojas claras delante, y gotas de riego
     * en la jardinera.
     */
    private fun DrawScope.cascada(v: VistaCentro, k: Cascada, sube: Float) {
        val gm = (k.g0 + k.g1) / 2f
        if (!CamCentro.enCuadro(CamCentro.p(GeoCentro.aro(GeoCentro.RV, k.y, gm)), 10f)) return
        val az = Azar(Semilla.de("laboratorio", "centro", "cascada", k.g0.toInt(), k.y.toInt()))
        val r = GeoCentro.RV - 0.12f
        val n = V3(-cos(GeoCentro.rad(gm)), 0.3f, -sin(GeoCentro.rad(gm)))
        val lj = GaleriaCentro.lejos(GeoCentro.aro(r, k.y, gm))
        // La jardinera, colgada en el canto: un cajón de metal con su tierra, y la chorrera del riego que baja por la losa.
        val pasos = ((k.g1 - k.g0) / 2f).toInt().coerceAtLeast(1)
        for (i in 0 until pasos) {
            val a0 = k.g0 + (k.g1 - k.g0) * i / pasos; val a1 = a0 + (k.g1 - k.g0) / pasos
            PiezasCentro.run { cara(v, PiezasCentro.tramoV(r - 0.12f, k.y - 0.05f, k.y + 0.3f, a0, a1), lerp(C.ACERO, C.HORMIGON, 0.35f), n, lj, siempre = true) }
        }
        val largoMax = k.largo * (0.15f + 0.85f * sube)
        val hebras = 12
        for (j in 0 until hebras) {
            val g = k.g0 + (k.g1 - k.g0) * (j + az.entre(-0.3f, 0.3f)) / (hebras - 1f)
            val largo = largoMax * az.entre(0.45f, 1f)
            val top = GeoCentro.aro(r - 0.15f, k.y, g); val pie = top + V3(0f, -largo, 0f)
            // Los colores de la hebra, arriba y abajo, por tono (el de la masa es el 0).
            val arriba = (0..3).map { v.c(top, HojasCentro.tono(it), n, lj * 0.6f) }; val abajo = (0..3).map { v.c(pie, HojasCentro.tono(it), n, lj * 0.6f) }
            fun color(t: Int, f: Float) = lerp(arriba[t], abajo[t], f)
            // La masa de la hebra y sus hojas, de arriba abajo.
            var h = 0f
            while (h < largo) {
                val w = top + V3(0f, -h, 0f) + V3(az.entre(-0.06f, 0.06f), 0f, az.entre(-0.06f, 0.06f))
                val f = h / largo.coerceAtLeast(0.01f)
                if (az.real() < 0.6f) mancha(CamCentro.p(w), 0.2f * CamCentro.k(w), 0.16f * CamCentro.k(w), color(0, f), 0.85f)
                hojaLejos(w, 0.095f * (1f - 0.3f * h / k.largo), color(HojasCentro.tonoAlAzar(az, az.real() > 0.4f), f), az)
                if (az.real() > 0.5f) hojaLejos(w + V3(az.entre(-0.1f, 0.1f), -0.05f, 0f), 0.08f, color(HojasCentro.tonoAlAzar(az, true), f), az)
                if (v.jardines == Jardines.LLENOS && az.real() > 0.9f) drawCircle(v.c(w, C.TELA, n, lj), (0.025f * CamCentro.k(w)).coerceAtLeast(0.4f), CamCentro.p(w))
                h += 0.11f + az.entre(0f, 0.07f)
            }
        }
        // Las gotas del riego que caen de la jardinera.
        repeat(4) {
            val w = GeoCentro.aro(r - 0.2f, k.y - 0.1f - az.entre(0f, 0.5f), az.entre(k.g0, k.g1))
            drawCircle(lerp(C.VIDRIO, Color.White, 0.5f).copy(alpha = 0.6f), (0.02f * CamCentro.k(w)).coerceAtLeast(0.35f), CamCentro.p(w))
        }
    }

    /** Las tomateras, en macetones junto al mostrador: matas oscuras con hojas claras y sus tomates. */
    private fun DrawScope.tomateras(v: VistaCentro) {
        val az = Azar(Semilla.de("laboratorio", "centro", "tomateras"))
        val sube = subida(v.jardines); val madura = madurez(v.jardines)
        for (g in listOf(126f, 170f, 30f)) {
            val c = GeoCentro.aro(GeoCentro.RV - 0.45f, 0f, g)
            if (!CamCentro.enCuadro(CamCentro.p(c), 10f)) continue
            val lj = GaleriaCentro.lejos(c)
            cilindro(v, c, 0.38f, 0.5f, lerp(C.BARRO, C.HORMIGON, 0.3f), lerp(C.TIERRA, C.NEGRO, 0.3f), rArriba = 0.42f)
            if (sube <= 0f) continue
            val alto = 0.3f + 1.3f * sube; val n = V3(0f, 0.3f, -1f)
            for (j in 0 until 22) {
                val w = c + V3(az.entre(-0.4f, 0.4f), 0.5f + az.entre(0f, alto), az.entre(-0.4f, 0.4f))
                hojaLejos(v, w, 0.12f, HojasCentro.tonoAlAzar(az, j % 2 == 0), n, lj, az)
            }
            if (madura >= 0f) repeat(8) {
                val w = c + V3(az.entre(-0.35f, 0.35f), 0.6f + az.entre(0f, alto * 0.8f), az.entre(-0.35f, 0.35f))
                tomate(v, w, 0.05f, (madura + az.entre(-0.2f, 0.1f)).coerceIn(0f, 1f))
            }
        }
    }

    /** Las jardineras de los antepechos de la primera galería, del lado del vacío: lechugas y alguna tomatera con fruta, que gotean. */
    private fun DrawScope.antepechos(v: VistaCentro) {
        val sube = subida(v.jardines); val madura = madurez(v.jardines)
        val az = Azar(Semilla.de("laboratorio", "centro", "antepechos"))
        var g = 42f; var i = 0
        while (g < 182f) {
            val w = GeoCentro.aro(GeoCentro.RV - 0.18f, GeoCentro.G1 + 0.62f, g)
            val q = CamCentro.p(w); val k = CamCentro.k(w)
            if (CamCentro.enCuadro(q, 5f)) {
                val a = GeoCentro.rad(g); val u = V3(-sin(a), 0f, cos(a)); val n = V3(-cos(a), 0.6f, -sin(a)); val lj = GaleriaCentro.lejos(w)
                caja(v, w, u, 1.0f, 0.24f, 0.16f, lerp(C.BARRO, C.HORMIGON, 0.25f), lj, tapa = lerp(C.TIERRA, C.NEGRO, 0.3f))
                // La chorrera que baja por el canto de la losa, y una gota.
                trazo(v, listOf(GeoCentro.aro(GeoCentro.RV - 0.01f, GeoCentro.G1 - 0.02f, g), GeoCentro.aro(GeoCentro.RV - 0.01f, GeoCentro.G1 - GeoCentro.LOSA + 0.02f, g + 0.3f)), lerp(C.TIERRA, C.SOMBRA, 0.3f), n, 0.06f, 0.4f)
                drawCircle(lerp(C.VIDRIO, Color.White, 0.5f).copy(alpha = 0.55f), (0.02f * k).coerceAtLeast(0.35f), CamCentro.p(GeoCentro.aro(GeoCentro.RV - 0.2f, GeoCentro.G1 - 0.3f - az.entre(0f, 0.6f), g)))
                if (sube > 0f) {
                    if (i % 3 == 2 && madura >= 0f) {
                        // Una tomatera, con sus tomates.
                        for (j in 0 until 8) hojaLejos(v, w + u * az.entre(-0.4f, 0.4f) + V3(0f, 0.2f + az.entre(0f, 0.7f * sube), 0f), 0.09f, HojasCentro.tonoAlAzar(az, j % 2 == 0), n, lj, az)
                        repeat(3) { tomate(v, w + u * az.entre(-0.3f, 0.3f) + V3(0f, 0.25f + az.entre(0f, 0.45f * sube), 0f), 0.045f, (madura + az.entre(-0.2f, 0.05f)).coerceIn(0f, 1f)) }
                    } else for (j in 0..3) {
                        val p = CamCentro.p(w + u * (-0.36f + 0.24f * j) + V3(0f, 0.19f, 0f))
                        val t = (0.55f + 0.45f * sube) * az.entre(0.85f, 1.1f)
                        mancha(p, 0.12f * k * t, 0.07f * k * t, v.c(w, lerp(C.GRAMA, C.PAJA, 0.15f), n, lj), 0.95f)
                        mancha(p + Offset(0f, -0.02f * k), 0.06f * k * t, 0.035f * k * t, v.c(w, lerp(C.GRAMA, C.PAJA, 0.4f), n, lj), 0.95f)
                    }
                }
            }
            g += 7f; i++
        }
    }

    // =================================================================
    //  Las columnas de cultivo del comedor
    // =================================================================

    /**
     * Una columna de cultivo en el suelo del comedor: un cilindro de bandejas redondas, una sobre otra, con sus lechugas y su
     * tubo de luz en el eje (blanca y algo violeta, la de los jardines), sobre un pie de hormigón. La llama el comedor en su
     * turno, de atrás adelante con las mesas.
     */
    fun DrawScope.torre(v: VistaCentro, x: Float, z: Float) {
        val c = V3(x, 0f, z); val lj = GaleriaCentro.lejos(c) * 0.6f
        val sube = subida(v.jardines)
        val luz = lerp(C.LUZ_DIA, C.VIOLETA, 0.3f)
        cilindro(v, c, TORRE_R * 0.8f, 0.35f, lerp(C.HORMIGON, C.TIERRA, 0.2f), lerp(C.TIERRA, C.NEGRO, 0.3f))
        // El tubo de luz del eje, que se ve entre las bandejas.
        val q0 = CamCentro.p(c + V3(0f, 0.35f, 0f)); val q1 = CamCentro.p(c + V3(0f, TORRE_ALTO, 0f)); val k = CamCentro.k(c)
        drawLine(lerp(luz, Color.White, 0.5f), q0, q1, (0.14f * k).coerceAtLeast(0.8f))
        drawLine(luz.copy(alpha = 0.35f), q0, q1, (0.5f * k).coerceAtLeast(2f), blendMode = BlendMode.Plus)
        val az = Azar(Semilla.de("laboratorio", "centro", "torre", x.toInt()))
        val pisos = 5
        for (i in 0 until pisos) {
            val y = 0.5f + i * (TORRE_ALTO - 0.6f) / (pisos - 1)
            cilindro(v, c + V3(0f, y, 0f), TORRE_R, 0.08f, lerp(C.ACERO, C.HORMIGON, 0.4f), lerp(C.TIERRA, C.NEGRO, 0.2f))
            // Las lechugas del borde de la bandeja, que reciben la luz del tubo por dentro y la del hall por fuera.
            if (sube > 0f) for (j in 0 until 9) {
                val a = j * 40f + az.entre(-12f, 12f) + i * 17f
                val w = c + V3(TORRE_R * 0.92f * cos(GeoCentro.rad(a)), y + 0.1f, TORRE_R * 0.92f * sin(GeoCentro.rad(a)))
                if (!CamCentro.seVe(w, V3(cos(GeoCentro.rad(a)), 0f, sin(GeoCentro.rad(a))))) continue
                val p = CamCentro.p(w); val t = (0.5f + 0.5f * sube) * az.entre(0.8f, 1.15f)
                mancha(p, 0.1f * k * t, 0.065f * k * t, v.c(w, lerp(C.GRAMA, C.PAJA, 0.12f), V3(cos(GeoCentro.rad(a)), 0.5f, sin(GeoCentro.rad(a))), lj), 0.95f)
                mancha(p + Offset(0f, -0.02f * k), 0.05f * k * t, 0.03f * k * t, lerp(v.c(w, lerp(C.GRAMA, C.PAJA, 0.4f), V3(0f, 1f, 0f), lj), luz, 0.25f), 0.95f)
            }
        }
        // El tapón de arriba y el halo de su luz en el aire.
        cilindro(v, c + V3(0f, TORRE_ALTO, 0f), 0.2f, 0.08f, lerp(C.ACERO, C.NEGRO, 0.2f), lerp(C.ACERO, C.HORMIGON, 0.3f))
        mancha(CamCentro.p(c + V3(0f, TORRE_ALTO * 0.55f, 0f)), 0.9f * k, 1.4f * k, luz, 0.14f)
    }

    // =================================================================
    //  Lo de cerca
    // =================================================================

    /** Los hilos de la cortina de cerca, que cuelgan de la jardinera del borde de la segunda galería, junto a la costilla. */
    private val HILOS = listOf(-106.3f, -105.5f, -104.8f, -104.1f, -103.4f)
    private const val R_CORTINA = 7.62f

    /**
     * La cortina de judías de cerca, que cuelga de la jardinera del borde de la galería de arriba, junto a la costilla: las
     * guías bajan por sus hilos tanto como han crecido (tras la siega, casi nada); las hojas una por una, distintas (tamaño,
     * giro, tono, alguna mordida o amarilla), oscuras por dentro y claras en el borde que da al vacío; vainas y flores.
     */
    fun DrawScope.cortina(v: VistaCentro) {
        val sube = subida(v.jardines)
        val az = Azar(Semilla.de("laboratorio", "centro", "cortina"))
        val arriba = GeoCentro.G2 - 0.05f
        val a = GeoCentro.rad(-104.8f)
        caja(v, GeoCentro.aro(R_CORTINA - 0.05f, arriba - 0.35f, -104.8f), V3(-sin(a), 0f, cos(a)), 1.1f, 0.32f, 0.35f, lerp(C.HORMIGON, C.TIERRA, 0.25f), tapa = lerp(C.TIERRA, C.NEGRO, 0.3f))
        val hilo = lerp(C.TELA, C.TIERRA, 0.25f)
        val abajo = GeoCentro.G1 + 1.45f
        for (g in HILOS) linea(v, GeoCentro.aro(R_CORTINA, arriba - 0.35f, g), GeoCentro.aro(R_CORTINA + 0.02f, abajo, g), hilo, V3(0f, 0f, -1f), 0.6f, 0.8f)
        if (sube <= 0f) return
        val hasta = arriba - 0.35f - (arriba - 0.35f - abajo) * sube
        val n = V3(0.5f, 0.3f, 0.8f)
        for (pasada in 0..1) for ((i, g) in HILOS.withIndex()) {
            var y = arriba - 0.42f - az.entre(0f, 0.1f)
            val fin = hasta + az.entre(-0.25f, 0.25f) * sube
            while (y > fin) {
                val w = GeoCentro.aro(R_CORTINA + az.entre(-0.06f, 0.04f), y, g + az.entre(-0.35f, 0.35f))
                val ang = 180f + az.entre(-75f, 75f) + (if (i % 2 == 0) 25f else -25f)
                val delante = az.real() > 0.45f
                if ((pasada == 0) != delante) hojaJudia(v, w, ang, az.entre(0.65f, 1.25f), HojasCentro.tonoAlAzar(az, delante), n, az, mordida = delante && az.real() > 0.85f)
                if (pasada == 1 && v.jardines == Jardines.LLENOS && az.real() > 0.8f) vaina(v, w, az)
                if (pasada == 1 && v.jardines == Jardines.LLENOS && az.real() > 0.88f) flor(v, w + V3(0f, -0.04f, 0f))
                y -= 0.1f + az.entre(0f, 0.08f)
            }
        }
    }

    /** La jardinera grande de la galería: dónde está (su centro en el suelo) y hacia dónde va su largo. */
    val JARDINERA: V3 = GeoCentro.aro(8.95f, GeoCentro.G1, -82f)
    private val U_JARDINERA: V3 = V3(cos(GeoCentro.rad(-82f + 90f)), 0f, sin(GeoCentro.rad(-82f + 90f)))

    /**
     * La jardinera grande de la galería, a la derecha, cortada por el borde: un cajón de hormigón con su tierra, sus manchas de
     * agua y su chorrera; dos tomateras con su tutor, de hojas grandes una por una y sus tomates; y los hilos de judías que
     * suben al techo de la galería de arriba, con las hojas hasta donde han crecido.
     */
    fun DrawScope.jardineraGrande(v: VistaCentro) {
        val sube = subida(v.jardines); val madura = madurez(v.jardines)
        val o = JARDINERA; val u = U_JARDINERA; val w0 = V3(-u.z, 0f, u.x)
        val alto = 0.42f
        val barro = lerp(lerp(C.BARRO, C.LAMPARA, 0.2f), C.TELA, 0.22f)
        val tierra = lerp(C.TIERRA_LIMPIA, C.TIERRA, 0.25f)
        caja(v, o, u, 1.3f, 0.5f, alto, barro, tapa = tierra)
        for ((ps, nn) in PiezasCentro.carasDeCaja(o, u, 1.3f, 0.5f, alto)) {
            if (!CamCentro.seVe(ps[0], nn)) continue
            val f = CamCentro.forma(ps) ?: continue
            drawPath(f, androidx.compose.ui.graphics.Brush.verticalGradient(listOf(lerp(barro, C.SOMBRA, 0.55f).copy(alpha = 0.55f), lerp(barro, C.SOMBRA, 0.3f).copy(alpha = 0.6f)),
                startY = CamCentro.p(ps[3]).y, endY = CamCentro.p(ps[0]).y))
        }
        // El labio de arriba, más claro, y la banda de humedad de abajo.
        caja(v, o + V3(0f, alto - 0.05f, 0f), u, 1.36f, 0.56f, 0.06f, lerp(barro, C.TELA, 0.3f), tapa = tierra)
        caja(v, o, u, 1.31f, 0.51f, 0.09f, lerp(barro, C.SOMBRA, 0.35f), tapa = lerp(barro, C.SOMBRA, 0.3f))
        // La chorrera y la mancha de agua del riego en el cajón.
        val az = Azar(Semilla.de("laboratorio", "centro", "jardinera grande"))
        for (s in listOf(-0.4f, 0.15f, 0.5f)) trazo(v, listOf(o + u * s + w0 * -0.26f + V3(0f, alto - 0.06f, 0f), o + u * (s + 0.02f) + w0 * -0.26f + V3(0f, 0.08f, 0f)), lerp(C.TIERRA, C.SOMBRA, 0.35f), w0 * -1f, 0.03f, 0.4f)
        // La cama de lechugas, en tres filas, que se salen por encima del labio.
        if (sube > 0f) for (fila in -1..1) for (j in 0 until 8) {
            val w = o + u * (-0.56f + 0.16f * j + 0.05f * fila) + w0 * (0.16f * fila) + V3(0f, alto + 0.03f, 0f)
            val t = (0.55f + 0.6f * sube) * az.entre(0.85f, 1.15f)
            for (h in 0..4) hojaJudia(v, w + V3(az.entre(-0.05f, 0.05f), 0.02f, az.entre(-0.05f, 0.05f)), h * 72f + az.entre(-20f, 20f), 1.1f * t, if (h < 2) 1 else 2, V3(0f, 1f, 0.3f), az)
        }
        val n = V3(-0.6f, 0.4f, 0.8f)
        // Los hilos de judías, del cajón al techo de la galería de arriba.
        val techo = GeoCentro.G2 - GeoCentro.LOSA
        for (j in 0 until 5) {
            val s = -0.6f + 0.3f * j
            val b = o + u * s + V3(0f, alto, 0f); val c = o + u * (s * 0.8f) + w0 * -0.2f + V3(0f, techo - GeoCentro.G1, 0f)
            linea(v, b, c, lerp(C.TELA, C.PAJA, 0.3f), n, 0.9f, 0.9f)
            if (sube <= 0f) continue
            var h = 0.1f
            while (h < (c.y - b.y) * sube) {
                val w = b + (c - b) * (h / (c.y - b.y)) + V3(az.entre(-0.05f, 0.05f), 0f, az.entre(-0.05f, 0.05f))
                hojaJudia(v, w, az.entre(-80f, 80f), az.entre(0.75f, 1.2f), HojasCentro.tonoAlAzar(az, az.real() > 0.4f), n, az, mordida = az.real() > 0.9f)
                if (v.jardines == Jardines.LLENOS && az.real() > 0.82f) vaina(v, w, az)
                h += 0.16f + az.entre(0f, 0.1f)
            }
        }
        // Las dos tomateras con su tutor: hojas grandes y los tomates.
        for ((i, s) in listOf(-0.35f, 0.4f).withIndex()) {
            val base = o + u * s + V3(0f, alto, 0f)
            val altoT = 0.2f + 0.9f * sube
            linea(v, base, base + V3(0f, 1.1f, 0f), lerp(C.MADERA, C.TIERRA, 0.3f), n, 1.2f, 0.9f)
            if (sube <= 0f) continue
            val hojas = (6 + 22 * sube).toInt()
            for (j in 0 until hojas) {
                val f = az.real(); val w = base + V3(az.entre(-0.3f, 0.3f) * (0.4f + 0.6f * f), altoT * f, az.entre(-0.3f, 0.3f) * (0.4f + 0.6f * f))
                hojaTomate(v, w, az.entre(-120f, 120f), az.entre(0.7f, 1.2f) * (0.6f + 0.4f * sube), HojasCentro.tonoAlAzar(az, j % 3 != 0), n, az)
            }
            if (madura >= 0f) repeat(3 + i) {
                val w = base + V3(az.entre(-0.18f, 0.18f), altoT * az.entre(0.25f, 0.8f), az.entre(-0.18f, 0.18f))
                for (r in 0..2) tomate(v, w + V3(r * 0.06f - 0.06f, -r * 0.03f, 0f), 0.04f, (madura + az.entre(-0.25f, 0.05f) + r * 0.08f).coerceIn(0f, 1f))
            }
        }
    }
}
