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
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAbismo.grande
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAbismo.sobreElSoporte
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.aduja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.cabo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.hornacina
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.monton
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.ristra
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.bita
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.farolDeMuelle
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.mesaDeCafe
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.redTendida
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.ristraSeca
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.saco
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.sacoEsquinas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo de cerca en el muelle: las tablas de este muelle, en fuga hacia el mismo punto que la fila de barcos, con su canto, su
 * desgaste y sus cosas a distintas profundidades (la bita con la amarra de Nikos, el cajón con el objeto del día, los sacos
 * de esponjas, la red y las ristras tendidas a secar en su cuerda, el farol, la báscula), el café de este lado (su toldo a
 * rayas arriba y una de sus mesas, con las tazas, el tavli y el comboloi, cortados por el borde de la derecha; su casa, fuera
 * del cuadro, solo da su sombra y, de noche, su luz) y la popa del barco de Nikos, amarrado detrás de quien mira: la regala
 * azul que da la vuelta a la popa, la tablazón por dentro y por fuera, la caña del timón, la hornacina del icono con su
 * lamparilla, lo que hay en cubierta, las defensas, y la botavara abierta sobre el muelle con la vela aferrada y las ristras.
 */
internal object CercaMuelle {
    private val C = ColorAbismo
    private val M = MuelleAbismo

    // ---------------------------------------------------------------- El barco de Nikos (en los ejes del río)

    private const val EJE = MuelleAbismo.NIKOS_EJE
    private const val POPA = MuelleAbismo.NIKOS_POPA
    private const val B = MuelleAbismo.NIKOS_MANGA / 2f
    /** El radio de la popa redonda, la regala y la cubierta (en metros sobre las tablas del muelle). */
    private const val R = 1.45f
    private const val REGALA = 0.62f
    private const val CUBIERTA = 0.08f
    private const val ZC = POPA - R

    /** Un punto de la regala de Nikos: [a] de 0 (estribor, junto al muelle) a π (babor), pasando por la popa; o por los costados, hacia proa. */
    private fun borde(a: Float, h: Float): Offset = M.pm(EJE + B * cos(a), h, ZC + R * sin(a))

    /** La hornacina del icono, clavada en la cara de dentro de la popa, un poco a estribor, mirando a proa. */
    val ICONO: Offset get() = M.pm(EJE + B * cos(1.05f) * 0.86f, CUBIERTA + 0.36f, ZC + R * sin(1.05f) * 0.9f)
    val K_ICONO: Float get() = M.k(EJE + B * 0.5f, ZC + R * 0.8f)

    /** La botavara: del palo mayor (detrás de quien mira) a su extremo, abierta sobre el muelle. */
    private val BOTAVARA_0 = V3(EJE, 2.05f, -3.2f)
    private val BOTAVARA_1 = V3(0.35f, 3.1f, 4.8f)
    private fun enBotavara(t: Float): V3 = BOTAVARA_0 + (BOTAVARA_1 - BOTAVARA_0) * t
    private fun pv(a: V3): Offset = M.pm(a.x, a.y, a.z)

    /** Dónde cuelga la ristra [i] de [n] a lo largo de la botavara: lejos de quien mira, hacia su extremo. */
    private fun tRistra(i: Int, n: Int): Float = 0.98f - (if (n > 3) 0.34f else 0.2f) * i / maxOf(n - 1, 1).toFloat() + listOf(0f, 0.012f, -0.01f, 0.006f)[i % 4]
    private fun largoRistra(i: Int): Float = listOf(0.78f, 0.62f, 0.92f, 0.7f, 0.55f, 0.85f, 0.66f)[i % 7]

    // ---------------------------------------------------------------- Las cosas del muelle (en los ejes del río)

    /** La bita donde va la amarra de popa de Nikos: en el paso del gato. */
    val BITA: Sitio get() = M.sitio(0.3f, 3.45f, 0.2f)
    /** El farol del muelle, junto al canto. */
    private const val FAROL_X = 2.2f
    private const val FAROL_Z = 7.7f
    private const val FAROL_ALTO = 3.0f
    val FAROL: Offset get() = M.pm(FAROL_X, FAROL_ALTO + 0.2f, FAROL_Z)
    /** La luz del café de este lado (su puerta, fuera del cuadro a la derecha): se derrama en las tablas desde el borde. */
    val LUZ_DEL_CAFE: Offset get() = Offset(ANCHO + 30f, 228f)
    /** La mesa del café, cerca y cortada por el borde de la derecha. */
    private val MESA: Sitio get() = M.sitio(2.05f, 3.4f, 0.5f)
    /** El tendedero: la cuerda de las ristras y la red, de un poste junto al canto a otro bajo el toldo. */
    private val TENDEDERO_0 = V3(0.6f, 2.0f, 11.5f)
    private val TENDEDERO_1 = V3(4.4f, 2.1f, 12f)
    /** La casa del café y la de al lado, fuera del cuadro a la derecha (solo dan su sombra), y el callejón entre las dos. */
    private val CASAS = listOf(floatArrayOf(6.2f, -8f, 2.6f, 6.8f), floatArrayOf(6.6f, 6.4f, 34f, 5.6f))
    /** El toldo del café: de la fachada a su volante, sobre la terraza. */
    private const val TOLDO_X = 4.2f
    private const val TOLDO_Z0 = -3f
    private const val TOLDO_Z1 = 8.6f
    private const val TOLDO_H = 2.7f

    /** Lo alto de cerca: el farol, los postes del tendedero y la botavara (para la zona de los astros). */
    internal fun altos(m: Momento): List<Offset> = buildList {
        add(M.pm(FAROL_X, FAROL_ALTO + 0.55f, FAROL_Z))
        for (q in listOf(TENDEDERO_0, TENDEDERO_1)) add(pv(q))
        if (M.nikosAmarrado(m)) { for (i in 0..10) add(pv(enBotavara(i / 10f))); add(pv(BOTAVARA_1 + V3(-0.8f, 6f, -2f))) }
    }

    // ---------------------------------------------------------------- Lo de cerca

    fun DrawScope.cerca(v: MuelleAbismo.Vista) {
        val m = v.m
        tablas(v)
        sombras(v)
        cosasDelMuelle(v)
        if (M.nikosAmarrado(m)) nikos(v) else amarradero(v)
        if (M.nikosAmarrado(m)) botavara(v)
        nocheEnElMuelle(v)
    }

    // ---------------------------------------------------------------- Las tablas

    private fun DrawScope.tablas(v: MuelleAbismo.Vista) {
        val p = v.p; val l = v.l
        val forma = poligono(listOf(M.pm(0f, 0f, 0.6f), M.pm(0f, 0f, 400f), M.pm(40f, 0f, 400f), M.pm(40f, 0f, 0.6f), Offset(ANCHO + 20f, ALTO + 20f), Offset(M.pm(0f, 0f, 0.6f).x, ALTO + 20f)))
        val madera = lerp(C.MADERA_GRIS, C.MADERA, 0.42f)
        val mojado = M.mojado(v.m)
        val base = if (mojado) lerp(madera, lerp(C.MADERA_OSC, C.MAR_HONDO, 0.25f), 0.6f) else madera
        drawPath(forma, Brush.verticalGradient(listOf(p.arriba(base, 0.25f), p.arriba(base, 0.05f), p.arriba(lerp(base, C.MADERA_OSC, 0.12f))), M.H, ALTO))
        val az = Azar(Semilla.de("abismo", "tablasMuelle"))
        clipPath(forma) {
            // Las tablas, cada una de su tono, con sus juntas oscuras en fuga y los topes escalonados.
            var x = 0.05f
            while (x < 7.5f) {
                val a = M.pm(x, 0f, 0.6f); val b = M.pm(x, 0f, 260f); val a2 = M.pm(x + 0.22f, 0f, 0.6f); val b2 = M.pm(x + 0.22f, 0f, 260f)
                val tono = lerp(base, listOf(C.ARENA, C.MADERA_OSC, C.LONA, C.MADERA_GRIS)[az.entero(4)], az.entre(0.04f, 0.2f))
                drawPath(poligono(a, a2, b2, b), p.arriba(tono, 0.1f).copy(alpha = 0.75f))
                drawLine(p.sombra(C.BREA, 0.05f).copy(alpha = 0.6f), a, b, 0.7f)
                var z = az.entre(0.6f, 4f); while (z < 30f) { drawLine(p.sombra(C.BREA, 0.05f).copy(alpha = 0.5f), M.pm(x, 0f, z), M.pm(x + 0.22f, 0f, z), 0.6f); if (z < 6f) for (u in listOf(0.05f, 0.17f)) drawCircle(p.sombra(C.PLOMO).copy(alpha = 0.6f), 0.5f, M.pm(x + u, 0f, z + 0.08f)); z += az.entre(2.5f, 5.5f) }
                x += 0.24f
            }
            // El desgaste: el paso gastado, las manchas de brea y de agua, la sal; de noche, la plata de la luna en lo gastado.
            repeat(16) { val q = M.pm(az.entre(0.2f, 3.5f), 0f, az.entre(1.5f, 16f)); val kk = M.k(1f, 5f); mancha(q, az.entre(0.3f, 0.9f) * kk, az.entre(0.06f, 0.15f) * kk, p.sombra(lerp(base, C.BREA, 0.45f)), 0.3f) }
            repeat(10) { val q = M.pm(az.entre(0.2f, 3.5f), 0f, az.entre(1.5f, 14f)); val kk = M.k(1f, 5f); mancha(q, az.entre(0.3f, 0.8f) * kk, az.entre(0.05f, 0.12f) * kk, p.luz(lerp(base, C.BLANCO, 0.55f)), 0.22f) }
            if (mojado) {
                // Los charcos en lo gastado devuelven el cielo; las tablas mojadas, en largo, el farol y el agua.
                for ((x, z, w) in listOf(Triple(1.5f, 5.2f, 1.2f), Triple(0.7f, 9.5f, 1.6f), Triple(2.2f, 13f, 2f), Triple(1.1f, 3.6f, 0.8f))) { val q = M.pm(x, 0f, z); val kk = M.k(x, z); mancha(q, w * kk, 0.12f * kk, lerp(l.bajo, Color.White, 0.25f), 0.5f) }
                var x = 0.1f; while (x < 4f) { drawLine(lerp(l.medio, Color.White, 0.2f).copy(alpha = 0.16f), M.pm(x, 0f, 2.5f), M.pm(x, 0f, 60f), 0.5f); x += 0.48f }
            }
            if (v.luna > 0.05f) {
                // La plata de la luna en las tablas, entre los charcos cálidos: un lavado frío que se abre hacia el canto.
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, C.LUNA.copy(alpha = 0.2f * v.luna)), M.H, ALTO), Offset(0f, M.H), Size(ANCHO, ALTO - M.H), blendMode = BlendMode.Screen)
                mancha(M.pm(1.2f, 0f, 6f), 90f, 18f, C.LUNA, 0.14f * v.luna)
            }
        }
        // El canto: la tabla de borde, gastada, con su filo de luz y la sombra que deja hacia el agua.
        val c0 = M.pm(0f, 0.11f, 0.6f); val c1 = M.pm(0f, 0.11f, 260f); val c2 = M.pm(0.22f, 0.11f, 260f); val c3 = M.pm(0.22f, 0.11f, 0.6f)
        drawPath(poligono(c0, c1, c2, c3), p.arriba(lerp(madera, C.BLANCO, 0.25f)))
        drawPath(poligono(c0, c1, M.pm(0f, 0f, 260f), M.pm(0f, 0f, 0.6f)), p.lado(C.MADERA_OSC, alReves = true))
        drawLine(p.luz(lerp(madera, C.BLANCO, 0.4f)), c0, c1, 0.8f)
    }

    // ---------------------------------------------------------------- Las sombras

    /** Un punto en las tablas a donde cae la sombra de (x, h, z), con la reja de esta hora (en los ejes del río). */
    private fun enElSuelo(r: Offset, x: Float, h: Float, z: Float, suelo: Float = 0f): Offset = M.pm(x + r.x * (h - suelo), suelo, z + r.y * (h - suelo))

    /** La sombra de un palo de [a] a [b] (de [g] metros de grueso) en las tablas. */
    private fun palo(r: Offset, a: V3, b: V3, g: Float): Path {
        val pa = enElSuelo(r, a.x, a.y, a.z); val pb = enElSuelo(r, b.x, b.y, b.z)
        val d = pb - pa; val n = d.getDistance().coerceAtLeast(0.01f); val nn = Offset(-d.y / n, d.x / n) * (g * M.k(a.x, a.z) / 2f).coerceAtLeast(0.4f)
        return poligono(pa + nn, pb + nn, pb - nn, pa - nn)
    }

    private fun DrawScope.sombras(v: MuelleAbismo.Vista) {
        val r = v.reja ?: return
        val m = v.m
        capaSombra(v.r) { c ->
            clipPath(poligono(listOf(M.pm(0f, 0f, 0.6f), M.pm(0f, 0f, 400f), M.pm(40f, 0f, 400f), Offset(ANCHO + 20f, ALTO + 20f), Offset(M.pm(0f, 0f, 0.6f).x, ALTO + 20f)))) {
                // Las casas de la derecha, fuera del cuadro: con el sol bajo de la mañana su sombra tapa el muelle, salvo la
                // franja que entra por el callejón (la mancha de sol que camina con la hora).
                for (cs in CASAS) {
                    val (x0, z0, z1, h) = cs.toList()
                    val pts = listOf(V3(x0, h, z0), V3(x0, h, z1), V3(x0 + 9f, h, z1), V3(x0 + 9f, h, z0), V3(x0, 0f, z0), V3(x0, 0f, z1))
                    sombra(poligono(envolvente(pts.map { enElSuelo(r, it.x, it.y, it.z) })), c)
                }
                // El toldo del café, fuera del cuadro, sobre su terraza.
                val toldo = listOf(V3(TOLDO_X, TOLDO_H, TOLDO_Z0), V3(TOLDO_X, TOLDO_H, TOLDO_Z1), V3(6.2f, TOLDO_H + 0.6f, TOLDO_Z1), V3(6.2f, TOLDO_H + 0.6f, TOLDO_Z0))
                sombra(poligono(toldo.map { enElSuelo(r, it.x, it.y, it.z) }), c)
                // El farol, los postes del tendedero, la cuerda y lo que cuelga de ella: la reja.
                sombra(palo(r, V3(FAROL_X, 0f, FAROL_Z), V3(FAROL_X, FAROL_ALTO, FAROL_Z), 0.12f), c)
                sombra(palo(r, V3(FAROL_X, FAROL_ALTO, FAROL_Z), V3(FAROL_X, FAROL_ALTO + 0.45f, FAROL_Z), 0.3f), c)
                for (q in listOf(TENDEDERO_0, TENDEDERO_1)) sombra(palo(r, q.copy(y = 0f), q, 0.12f), c)
                sombra(palo(r, TENDEDERO_0, TENDEDERO_1, 0.03f), c)
                for (i in 0 until 7) { val t = 0.1f + 0.12f * i; val a = TENDEDERO_0 + (TENDEDERO_1 - TENDEDERO_0) * t; sombra(palo(r, a + V3(0f, -0.05f, 0f), a + V3(0f, -1.0f - 0.25f * (i % 3), 0f), if (i == 3) 0.6f else 0.2f), c) }
                // La bita, el cajón, los sacos, la mesa con sus sillas.
                sombra(sombraDe(BITA, v.sol, esquinas(-0.15f, 0.15f, 0f, 0.38f, -0.15f, 0.15f)), c)
                sombra(sombraDe(M.SITIO_CAJON, v.sol, esquinas(-0.34f, 0.34f, 0f, M.CAJON_ALTO, -0.25f, 0.25f)), c)
                for ((i, s) in sacos().withIndex()) sombra(sombraDe(s, v.sol, sacoEsquinas(0.62f, i % 2 == 1)), c)
                sombra(sombraDe(MESA, v.sol, esquinas(-0.34f, 0.34f, 0.7f, 0.76f, -0.34f, 0.34f)), c)
                sombra(sombraDe(MESA, v.sol, esquinas(-0.05f, 0.05f, 0f, 0.74f, -0.05f, 0.05f)), c)
                // La botavara de Nikos con su vela y sus ristras, y su amantillo: por la tarde cruzan las tablas.
                if (M.nikosAmarrado(m)) {
                    sombra(palo(r, BOTAVARA_0, BOTAVARA_1, 0.32f), c)
                    sombra(palo(r, BOTAVARA_1, BOTAVARA_1 + V3(-0.8f, 6f, -2f), 0.03f), c)
                    for (i in 0 until M.ristrasDeLaBotavara(m)) { val a = enBotavara(tRistra(i, M.ristrasDeLaBotavara(m))); sombra(palo(r, a, a + V3(0f, -largoRistra(i), 0f), 0.2f), c) }
                }
            }
        }
        // De noche, el farol del muelle tumba su propia sombra, cálida en su charco y abierta en abanico.
        if (v.farol > 0.05f) {
            val f = Foco(FAROL, C.LUMBRE, 1f, 120f, suelo = M.pm(FAROL_X, 0f, FAROL_Z).y)
            for (cosa in listOf(BITA to 0.38f, M.SITIO_CAJON to M.CAJON_ALTO, sacos()[0] to 0.7f)) {
                val (s, h) = cosa
                val rr = rayoDeFoco(f, s.pie, 0.5f * v.farol, lerp(C.SOMBRA, C.MAR_HONDO, 0.3f))
                capaSombra(rr) { c -> val d = s.pie - FAROL.copy(y = f.suelo); val n = d.getDistance().coerceAtLeast(1f); val u = d / n; val largo = h * s.k * 1.8f; val w = 0.3f * s.k
                    sombra(poligono(s.pie + Offset(-u.y, u.x) * w, s.pie + u * largo + Offset(-u.y, u.x) * w * 1.4f, s.pie + u * largo - Offset(-u.y, u.x) * w * 1.4f, s.pie - Offset(-u.y, u.x) * w), c) }
                break                                                              // una sola capa: la de la bita, la más cercana al farol
            }
        }
    }

    // ---------------------------------------------------------------- Las cosas del muelle

    private fun sacos(): List<Sitio> = listOf(M.SITIO_SACOS, M.sitio(2.15f, 5.7f, -0.4f), M.sitio(2.0f, 6.9f, 0.9f))

    private fun DrawScope.cosasDelMuelle(v: MuelleAbismo.Vista) {
        val m = v.m; val p = v.p
        // Lejos: la báscula de la lonja y una carretilla con sacos, junto al tendedero.
        bascula(v, M.sitio(2.2f, 11.5f, 0.3f))
        // El tendedero: los dos postes, la cuerda, las ristras de esponjas y la red con su aro, a secar.
        val t0 = pv(TENDEDERO_0); val t1 = pv(TENDEDERO_1)
        for (q in listOf(TENDEDERO_0, TENDEDERO_1)) { val a = M.pm(q.x, 0f, q.z); val b = pv(q); val kk = M.k(q.x, q.z); drawLine(p.medio(C.MADERA_GRIS), a, b, 0.11f * kk); drawLine(p.luz(C.MADERA_GRIS).copy(alpha = 0.5f), a + Offset(0.03f * kk, 0f), b + Offset(0.03f * kk, 0f), 0.03f * kk) }
        drawPath(curva(combada(t0, t1, 3f, 8)), p.sombra(C.CANAMO), style = Stroke(0.6f))
        for (i in 0 until 7) {
            val t = 0.1f + 0.12f * i; val a = TENDEDERO_0 + (TENDEDERO_1 - TENDEDERO_0) * t; val q = pv(a) + Offset(0f, 3f * 4f * t * (1f - t)); val kk = M.k(a.x, a.z)
            if (i == 3) redTendida(p, q + Offset(-0.25f * kk, 0f), q + Offset(0.25f * kk, 0f), 1.0f * kk)
            else ristraSeca(p, q, (1.0f + 0.25f * (i % 3)) * kk, 0.055f * kk, Semilla.de("abismo", "tendida", i), racimo = i % 2 == 0)
        }
        // El farol del muelle, junto al canto.
        farolDeMuelle(v.pincel(FAROL), M.pm(FAROL_X, 0f, FAROL_Z), M.k(FAROL_X, FAROL_Z), FAROL_ALTO, v.farol)
        // Los sacos de esponjas apretadas, unos de pie y otros echados, y un montón suelto: el día de la lonja, más.
        val ss = sacos()
        for ((i, s) in ss.withIndex().sortedByDescending { it.value.z0 }) saco(v.pincel(s.pie), s, v.sol, 0.62f, Semilla.de("abismo", "saco", i), i % 2 == 1)
        if (EscenaAbismo.hoyEs(m, "zarpa")) for (i in 0 until 3) { val bs = M.sitio(1.0f + 0.55f * i, 6.2f + 0.3f * i, 0f); val kb = bs.escala(0f, 0f); val bx0 = bs.pie.x - 0.26f * kb; val bx1 = bs.pie.x + 0.26f * kb
            drawRect(Brush.horizontalGradient(listOf(p.sombra(C.MADERA_OSC), p.medio(C.MADERA), p.luz(C.MADERA)), bx0, bx1), Offset(bx0, bs.pie.y - 0.66f * kb), Size(bx1 - bx0, 0.66f * kb))
            for (h in listOf(0.1f, 0.56f)) drawLine(p.sombra(C.PLOMO), Offset(bx0, bs.pie.y - h * kb), Offset(bx1, bs.pie.y - h * kb), 0.03f * kb) }
        if (EscenaAbismo.cargadoEnElMuelle(m) || EscenaAbismo.hoyEs(m, "zarpa")) monton(v.pincel(M.pm(3.1f, 0f, 6.4f)), M.pm(3.1f, 0f, 6.4f), 0.9f * M.k(3.1f, 6.4f), 8, Semilla.de("abismo", "montonMuelle"), true)
        // La amarra de popa de Nikos: de su cornamusa a la bita, tensa; y la aduja del resto, en las tablas.
        val bita = BITA
        if (M.nikosAmarrado(m)) cabo(p, combada(M.pm(EJE + B * 0.92f, REGALA + 0.05f, ZC + 0.2f), bita.p(0f, 0.3f, 0f), 2.5f, 10), 0.05f * bita.k)
        aduja(p, M.pm(0.95f, 0f, 5.2f), 0.34f * M.k(0.95f, 5.2f), 0.12f * M.k(0.95f, 5.2f), 4, 0.05f * M.k(0.95f, 5.2f), C.CANAMO)
        bita(v.pincel(bita.pie), bita, true)
        // La Epifanía: los cirios y los ramos que dejaron en el canto, y el agua bendita.
        if (EscenaAbismo.cruz(m)) for (i in 0 until 7) {
            val z = 5.0f + i * 1.3f; val q = M.pm(0.25f, 0f, z); val kk = M.k(0.25f, z)
            if (i % 2 == 0) { drawLine(p.luz(lerp(C.BLANCO, C.ESPONJA, 0.3f)), q, q + Offset(0f, -0.35f * kk), 0.05f * kk); llamaQuieta(q + Offset(0f, -0.35f * kk), 0.05f * kk, v.l.noche) }
            else { for (j in 0 until 5) drawCircle(if (j % 2 == 0) p.luz(C.BLANCO) else p.medio(C.ROJO), 0.045f * kk, q + Offset((j - 2) * 0.03f * kk, -0.12f * kk - (j % 2) * 0.03f * kk)); drawLine(p.medio(C.HIERBA), q, q + Offset(0f, -0.1f * kk), 0.02f * kk) }
        }
        // El cajón del primer plano con el objeto del día.
        val cajon = M.SITIO_CAJON
        for (cara in visibles(cajon, carasDeCaja(-0.34f, 0.34f, 0f, M.CAJON_ALTO, -0.25f, 0.25f))) {
            val t = luzDe(cajon, v.sol, cara); val f = forma(cajon, cara)
            drawPath(f, v.pincel(cajon.pie).por(lerp(C.MADERA_GRIS, C.MADERA, 0.55f), t))
            clipPath(f) { for (kk in 1 until 4) linea(cajon, cara, 0f, kk / 4f, 1f, kk / 4f, p.sombra(C.BREA).copy(alpha = 0.55f), 0.8f); if (!cara.arriba) linea(cajon, cara, 0.5f, 0.15f, 0.5f, 0.85f, p.sombra(C.BREA).copy(alpha = 0.3f), 2.2f) }
            drawPath(f, p.sombra(C.BREA).copy(alpha = 0.45f), style = Stroke(0.6f))
        }
        sobreElSoporte(m, v.pincel(M.soporte()), M.soporte(), M.escalaEn(3.7f))
        grande(m, v.pincel(M.PIE_GRANDE), M.PIE_GRANDE, M.APOYO_GRANDE, M.escalaEn(3.35f))?.pintar?.invoke(this)
        // La mesa del café, cerca y cortada por el borde de la derecha, con sus dos sillas, las tazas, el tavli y el comboloi.
        mesaDeCafe(v.pincel(MESA.pie), MESA, v.sol, Semilla.de("abismo", "mesa", m.semilla("mesa") % 3), sillas = 1)
    }

    /** La báscula de la lonja: una plataforma de tablas con su columna de hierro y el brazo con la pesa. */
    private fun DrawScope.bascula(v: MuelleAbismo.Vista, s: Sitio) {
        val p = v.p; val e = s.escala(0f, 0f)
        for (cara in visibles(s, carasDeCaja(-0.45f, 0.45f, 0f, 0.18f, -0.35f, 0.35f))) drawPath(forma(s, cara), p.por(C.MADERA, luzDe(s, v.sol, cara)))
        val a = s.p(0f, 0.18f, 0.3f); val b = s.p(0f, 1.3f, 0.3f)
        drawLine(p.medio(C.PLOMO), a, b, 0.06f * e)
        drawLine(p.luz(C.LATON), b, s.p(0.55f, 1.32f, 0.3f), 0.035f * e)
        drawRect(p.medio(C.PLOMO), Offset(s.p(0.4f, 1.3f, 0.3f).x - 0.04f * e, s.p(0.4f, 1.3f, 0.3f).y), Size(0.08f * e, 0.12f * e))
        drawCircle(p.luz(C.LATON), 0.06f * e, b)
        // Una ristra a medio pesar sobre la plataforma.
        ristra(p, s.p(-0.1f, 0.5f, 0f), 0.35f * e, 0.07f * e, 5, Semilla.de("abismo", "bascula"))
    }

    /** Una llamita quieta (la parte quieta del cirio; la viva la mueve). */
    private fun DrawScope.llamaQuieta(c: Offset, r: Float, noche: Float) {
        resplandor(c, r * 6f, 0.3f + 0.6f * noche, C.LUMBRE)
        drawOval(Color(0xFFFFE6A0), Offset(c.x - r * 0.4f, c.y - r * 1.2f), Size(r * 0.8f, r * 1.4f))
    }

    // ---------------------------------------------------------------- El barco de Nikos

    private fun DrawScope.nikos(v: MuelleAbismo.Vista) {
        val p = v.p; val m = v.m; val l = v.l
        val pc = v.pincel(ICONO)
        val n = 20
        val giro = (0..n).map { PI.toFloat() * it / n }                              // de estribor (0) a babor (π) por la popa
        // La cubierta: las tablas que corren a lo largo del barco, gastadas, con lo que hay encima.
        val zb = M.zCerca(EJE - B); val ze = M.zCerca(EJE + B)
        val cub = poligono(giro.map { borde(it, CUBIERTA) } + listOf(M.pm(EJE - B, CUBIERTA, zb), M.pm(EJE + B, CUBIERTA, ze)))
        drawPath(cub, pc.arriba(lerp(C.MADERA, C.MADERA_GRIS, 0.35f)))
        clipPath(cub) {
            for (i in -8..8) drawLine(p.sombra(C.BREA).copy(alpha = 0.45f), M.pm(EJE + i * 0.25f, CUBIERTA, POPA + 0.2f), M.pm(EJE + i * 0.25f, CUBIERTA, M.zCerca(EJE + i * 0.25f)), 0.6f)
            // La luz de la lamparilla en las tablas de la popa, de noche: su charco.
            if (l.noche > 0.2f) mancha(M.pm(EJE + 0.75f, CUBIERTA, POPA - 0.6f), 0.55f * K_ICONO, 0.2f * K_ICONO, C.LUMBRE, 0.4f * v.lamparilla * l.noche)
            // La sombra de la regala de estribor en la cubierta, del lado contrario al sol.
            if (v.r.fuerza > 0.05f && p.solALaDerecha) drawPath(poligono(listOf(borde(0f, CUBIERTA), M.pm(EJE + B, CUBIERTA, ze), M.pm(EJE + B - 0.6f, CUBIERTA, ze), M.pm(EJE + B - 0.6f, CUBIERTA, ZC))), C.SOMBRA.copy(alpha = 0.3f * v.r.fuerza), blendMode = BlendMode.Multiply)
        }
        // La cara de dentro de la borda: la de babor y la de la popa (la de estribor da la espalda a quien mira).
        val dentro = poligono(giro.drop(n / 4).map { borde(it, REGALA) } + giro.drop(n / 4).reversed().map { borde(it, CUBIERTA) } + listOf())
        val babor = poligono(listOf(borde(PI.toFloat(), REGALA), M.pm(EJE - B, REGALA, zb), M.pm(EJE - B, CUBIERTA, zb), borde(PI.toFloat(), CUBIERTA)))
        val blancoDentro = lerp(C.BLANCO, C.MADERA_GRIS, 0.25f)
        drawPath(babor, pc.por(blancoDentro, v.sol.t(V3(CGs(), 0f, -SGs()))))
        drawPath(dentro, Brush.horizontalGradient(listOf(pc.por(blancoDentro, -0.2f), pc.por(blancoDentro, 0.35f)), borde(PI.toFloat(), 0f).x, borde(0.3f, 0f).x))
        clipPath(dentro) {
            // Las tablas de la borda, por dentro, con las cuadernas que asoman y las chorreras de óxido.
            for (h in listOf(0.24f, 0.4f)) drawPath(curva(giro.map { borde(it, h) }), p.sombra(blancoDentro).copy(alpha = 0.5f), style = Stroke(0.6f))
            for (i in 2 until n step 2) { val a = giro[i]; drawLine(p.sombra(C.MADERA_OSC).copy(alpha = 0.45f), borde(a, REGALA - 0.04f), borde(a, CUBIERTA + 0.02f), 0.06f * M.k(EJE, ZC)) }
            // La luz de la lamparilla en la borda: su halo naranja alrededor de la hornacina.
            if (l.noche > 0.2f) mancha(ICONO, 0.75f * K_ICONO, 0.36f * K_ICONO, C.LUMBRE, 0.5f * v.lamparilla * l.noche)
        }
        // Lo que hay en cubierta, en la popa: la caña del timón, la aduja, el cesto de esponjas, la manta, el cubo, el farol.
        val cana0 = M.pm(EJE + 0.05f, CUBIERTA + 0.5f, POPA - 0.3f); val cana1 = M.pm(EJE + 0.85f, CUBIERTA + 0.78f, ZC - 1.3f)
        drawCircle(p.sombra(C.MADERA_OSC), 0.09f * M.k(EJE, POPA), M.pm(EJE + 0.05f, CUBIERTA + 0.45f, POPA - 0.3f))
        drawLine(pc.sombra(C.MADERA_OSC), cana0, cana1, 0.075f * M.k(EJE, ZC), StrokeCap.Round)
        drawLine(pc.luz(C.MADERA), cana0 + Offset(0f, -0.02f * M.k(EJE, ZC)), cana1 + Offset(0f, -0.02f * M.k(EJE, ZC)), 0.025f * M.k(EJE, ZC), StrokeCap.Round)
        aduja(pc, M.pm(EJE - 0.75f, CUBIERTA, ZC + 0.3f), 0.32f * M.k(EJE, ZC), 0.11f * M.k(EJE, ZC), 3, 0.05f * M.k(EJE, ZC), C.CANAMO)
        val cesto = M.pm(EJE + 1.25f, CUBIERTA, ZC - 0.2f); val kc = M.k(EJE + 1.2f, ZC)
        monton(pc, cesto, 0.55f * kc, if (EscenaAbismo.cargadoEnElMuelle(m)) 7 else 4, Semilla.de("abismo", "cestoNikos"), true)
        if (EscenaAbismo.mantas(m)) { val q = M.pm(EJE - 0.2f, CUBIERTA, ZC - 0.8f); drawOval(pc.por(lerp(C.ROJO, C.MADERA_OSC, 0.45f), 0.2f), Offset(q.x - 0.5f * kc, q.y - 0.18f * kc), Size(1f * kc, 0.3f * kc)) }
        // El barril del agua dulce, atado a la borda de babor, y el cubo de lavar las esponjas.
        val barril = M.sitio(EJE - 1.45f, ZC - 0.1f, 0f, CUBIERTA); val kb = barril.escala(0f, 0f)
        val bx0 = barril.pie.x - 0.28f * kb; val bx1 = barril.pie.x + 0.28f * kb
        drawRect(Brush.horizontalGradient(listOf(pc.sombra(C.MADERA_OSC), pc.medio(C.MADERA), pc.luz(C.MADERA)), bx0, bx1), Offset(bx0, barril.pie.y - 0.7f * kb), Size(bx1 - bx0, 0.7f * kb))
        for (h in listOf(0.1f, 0.6f)) drawLine(pc.sombra(C.PLOMO), Offset(bx0, barril.pie.y - h * kb), Offset(bx1, barril.pie.y - h * kb), 0.035f * kb)
        drawOval(pc.arriba(C.MADERA_OSC), Offset(bx0, barril.pie.y - 0.75f * kb), Size(bx1 - bx0, 0.1f * kb))
        val cubo = M.sitio(EJE + 0.55f, ZC - 0.55f, 0f, CUBIERTA); val kq = cubo.escala(0f, 0f)
        drawPath(poligono(cubo.pie + Offset(-0.15f * kq, -0.3f * kq), cubo.pie + Offset(0.15f * kq, -0.3f * kq), cubo.pie + Offset(0.12f * kq, 0f), cubo.pie + Offset(-0.12f * kq, 0f)), Brush.horizontalGradient(listOf(pc.sombra(C.MADERA_GRIS), pc.luz(C.MADERA_GRIS)), cubo.pie.x - 0.15f * kq, cubo.pie.x + 0.15f * kq))
        drawOval(pc.medio(lerp(C.MAR, l.alto, 0.4f)), Offset(cubo.pie.x - 0.14f * kq, cubo.pie.y - 0.33f * kq), Size(0.28f * kq, 0.06f * kq))
        // La hornacina del icono, clavada en la popa por dentro, con su lamparilla.
        hornacina(pc, ICONO, K_ICONO * 1.05f, v.lamparilla, mira = 0.4f, luto = EscenaAbismo.luto(m), velas = EscenaAbismo.hoyEs(m, "san_nicolas") || EscenaAbismo.cruz(m))
        if (l.noche > 0.2f) resplandor(ICONO, 1.0f * K_ICONO, 0.32f * l.noche, C.LUMBRE)
        // La regala azul, que da la vuelta a la popa, con su filo de luz y, de noche, la plata de la luna.
        val regala = curva(giro.map { borde(it, REGALA) })
        drawPath(regala, pc.por(C.AZUL, 0.25f), style = Stroke(0.075f * M.k(EJE, ZC), cap = StrokeCap.Round))
        drawPath(Path().apply { addPath(regala, Offset(0f, -0.025f * M.k(EJE, ZC))) }, pc.luz(lerp(C.AZUL, C.BLANCO, 0.4f)).copy(alpha = 0.6f), style = Stroke(0.02f * M.k(EJE, ZC), cap = StrokeCap.Round))
        if (v.luna > 0.05f) drawPath(Path().apply { addPath(regala, Offset(0f, -0.03f * M.k(EJE, ZC))) }, C.LUNA.copy(alpha = 0.5f * v.luna), style = Stroke(0.015f * M.k(EJE, ZC)))
        // La boda: guirnaldas de flores blancas y cintas en la regala de la popa **[Diseño propio]**.
        if (EscenaAbismo.hoyEs(m, "boda")) {
            val pts = (0..10).map { borde(PI.toFloat() * (0.05f + 0.9f * it / 10f), REGALA) }
            for (i in 0 until pts.size - 1) { val a = pts[i]; val b = pts[i + 1]; val kk = M.k(EJE, ZC)
                val c = combada(a, b, 0.12f * kk, 6)
                drawPath(curva(c), pc.medio(C.HIERBA), style = Stroke(0.04f * kk, cap = StrokeCap.Round))
                for ((j, q) in c.withIndex()) if (j % 2 == 1) drawCircle(pc.luz(C.BLANCO), 0.035f * kk, q) }
            val q = borde(0.9f, REGALA); val kk = M.k(EJE, ZC)
            for (sl in listOf(-1f, 1f)) drawLine(pc.luz(lerp(C.BLANCO, C.AZUL, 0.15f)), q, q + Offset(sl * 0.06f * kk, 0.45f * kk), 0.03f * kk, StrokeCap.Round)
        }
        // El costado de estribor, por fuera, junto al muelle: blanco con la franja azul, la tablazón, las defensas.
        val fuera = poligono(listOf(borde(0f, REGALA), M.pm(EJE + B, REGALA, ze), M.pm(EJE + B, 0f, ze), M.pm(EJE + B, 0f, ZC)))
        val tFuera = v.sol.t(V3(CGs(), 0f, -SGs()))
        drawPath(fuera, pc.por(C.BLANCO, tFuera))
        clipPath(fuera) {
            // La franja azul bajo la regala, las juntas de la tablazón, la sombra que sube del hueco entre el casco y el canto.
            drawPath(poligono(listOf(borde(0f, REGALA), M.pm(EJE + B, REGALA, ze), M.pm(EJE + B, REGALA - 0.17f, ze), borde(0f, REGALA - 0.17f))), pc.por(C.AZUL, tFuera))
            for (h in listOf(0.36f, 0.22f, 0.09f)) drawLine(pc.sombra(C.BLANCO).copy(alpha = 0.55f), M.pm(EJE + B, h, ZC), M.pm(EJE + B, h, ze), 0.7f)
            drawPath(poligono(listOf(M.pm(EJE + B, 0.16f, ZC), M.pm(EJE + B, 0.16f, ze), M.pm(EJE + B, 0f, ze), M.pm(EJE + B, 0f, ZC))), Brush.verticalGradient(listOf(Color.Transparent, pc.sombra(lerp(C.BLANCO, C.MAR_HONDO, 0.4f)).copy(alpha = 0.7f)), borde(0f, 0.16f).y, borde(0f, 0f).y))
            val az = Azar(Semilla.de("abismo", "costadoNikos"))
            repeat(7) { val z = az.entre(ze, ZC); drawLine(pc.sombra(lerp(C.BLANCO, C.COBRE_OSC, 0.35f)).copy(alpha = 0.45f), M.pm(EJE + B, REGALA - 0.18f, z), M.pm(EJE + B, REGALA - az.entre(0.35f, 0.55f), z), 0.05f * M.k(EJE + B, z)) }
            repeat(4) { val z = az.entre(ze, ZC); mancha(M.pm(EJE + B, az.entre(0.1f, 0.4f), z), 0.25f * M.k(EJE + B, z), 0.05f * M.k(EJE + B, z), pc.sombra(C.BLANCO), 0.35f) }
        }
        drawLine(pc.luz(C.BLANCO).copy(alpha = 0.7f), borde(0f, REGALA - 0.18f), M.pm(EJE + B, REGALA - 0.18f, ze), 0.6f)
        // Las defensas de cuerda, colgadas entre el casco y el canto.
        for (z in listOf(0.9f, 1.9f)) { val q = M.pm(EJE + B + 0.06f, REGALA - 0.35f, z); val kk = M.k(EJE + B, z); drawLine(p.sombra(C.CANAMO), M.pm(EJE + B, REGALA, z), q, 0.02f * kk); drawOval(pc.por(C.CANAMO, 0.2f), Offset(q.x - 0.09f * kk, q.y - 0.05f * kk), Size(0.18f * kk, 0.34f * kk)) }
    }

    /**
     * El amarradero vacío de Nikos, el verano del cuarto («ningún barco: están todos en el mar»): los dos pilotes donde
     * amarraba, con las amarras sueltas que cuelgan al agua, las defensas colgadas del canto y una red echada sobre él a
     * secar. Que se note la ausencia **[Diseño propio]**.
     */
    private fun DrawScope.amarradero(v: MuelleAbismo.Vista) {
        val p = v.p; val l = v.l
        for ((x, z) in listOf(-3.9f to 6.8f, -2.0f to 2.6f)) {
            val a = M.pm(x, 1.5f, z); val b = M.pm(x, M.AGUA, z); val kk = M.k(x, z)
            reflejoRotoCerca(b, (b.y - a.y) * 0.8f, 0.36f * kk, p.medio(C.MADERA_GRIS))
            PiezasAbismoTierra.run { pilote(p, a, b, 0.36f * kk) }
            // La amarra suelta: del pilote cae al agua y se pierde.
            cabo(p, listOf(a + Offset(0f, 0.15f * kk), a + Offset(0.3f * kk, 0.5f * kk), a + Offset(0.5f * kk, 1.1f * kk), b + Offset(0.6f * kk, 0f)), 0.045f * kk)
        }
        // Las defensas de cuerda colgadas del canto, sin barco que proteger, y la red echada sobre el canto.
        for (z in listOf(2.2f, 4.6f)) { val q = M.pm(-0.05f, -0.45f, z); val kk = M.k(0f, z); drawLine(p.sombra(C.CANAMO), M.pm(0.1f, 0.05f, z), q, 0.025f * kk); drawOval(p.por(C.CANAMO, 0.2f), Offset(q.x - 0.1f * kk, q.y - 0.06f * kk), Size(0.2f * kk, 0.36f * kk)) }
        redTendida(p, M.pm(0.25f, 0.08f, 3.0f), M.pm(0.25f, 0.08f, 5.6f), 0.5f * M.k(0.25f, 4f))
        if (l.noche < 0.75f) PiezasAbismoTierra.run { pelicano(p, M.pm(-2.0f, 1.5f, 2.6f), M.k(-2.0f, 2.6f), 1f, PiezasAbismoTierra.Postura.POSADO, Semilla.de("abismo", "pelicanoVacio")) }
        // La amarra de Nikos, suelta en la bita, que cae al agua: el barco no está.
        cabo(p, listOf(BITA.p(0f, 0.3f, 0f), BITA.p(-0.4f, 0.05f, -0.2f), M.pm(-0.1f, 0.0f, 3.6f), M.pm(-0.6f, M.AGUA, 3.4f)), 0.05f * BITA.k)
    }

    /** El reflejo de un pilote cercano: trazos que se corren hacia abajo. */
    private fun DrawScope.reflejoRotoCerca(pie: Offset, alto: Float, ancho: Float, color: Color) {
        val az = Azar(Semilla.de("abismo", "pilote", pie.x.toInt()))
        var y = pie.y; while (y < pie.y + alto) { val k = (y - pie.y) / alto; val c = az.entre(-1f, 1f) * (1f + 4f * k); drawLine(color.copy(alpha = 0.45f * (1f - k)), Offset(pie.x - ancho / 2f + c, y), Offset(pie.x + ancho / 2f + c, y), 1f); y += 1.6f }
    }

    /** La dirección de estribor (+x del río) en los ejes de quien mira, para el lado de luz del costado. */
    private fun CGs(): Float = cos(MuelleAbismo.GIRO)
    private fun SGs(): Float = sin(MuelleAbismo.GIRO)

    // ---------------------------------------------------------------- La botavara

    private fun DrawScope.botavara(v: MuelleAbismo.Vista) {
        val p = v.p; val m = v.m
        val a = pv(BOTAVARA_0); val b = pv(BOTAVARA_1)
        // El amantillo, que sube del extremo de la botavara al tope del palo (fuera del cuadro).
        drawLine(p.sombra(C.CANAMO).copy(alpha = 0.85f), b, pv(BOTAVARA_1 + V3(-0.8f, 6f, -2f)), 0.6f)
        // La botavara y la vela aferrada encima, con sus tomadores, y el pico bajado sobre ella.
        val pasos = 14
        val ks = (0..pasos).map { M.k(enBotavara(it / pasos.toFloat()).x, enBotavara(it / pasos.toFloat()).z) }
        val ejes = (0..pasos).map { pv(enBotavara(it / pasos.toFloat())) }
        for (i in 0 until pasos) drawLine(p.sombra(C.MADERA_OSC), ejes[i], ejes[i + 1], 0.12f * ks[i], StrokeCap.Round)
        val vela = (0..pasos).map { i -> ejes[i] + Offset(0f, -0.16f * ks[i] * (1f - 0.35f * i / pasos)) }
        if (EscenaAbismo.hoyEs(m, "zarpa")) {
            // La mañana de zarpar: la mayor suelta de sus tomadores, colgando en senos de la botavara, lista para izar.
            for (i in 2 until pasos - 1 step 3) { val a0 = ejes[i]; val a1 = ejes[minOf(i + 3, pasos)]; val kk = ks[i]
                drawPath(Path().apply { moveTo(a0.x, a0.y); cubicTo(a0.x, a0.y + 0.36f * kk, a1.x, a1.y + 0.36f * kk, a1.x, a1.y); close() }, p.por(lerp(C.LONA, C.BLANCO, 0.3f), -0.1f))
                drawLine(p.sombra(C.LONA), Offset((a0.x + a1.x) / 2f, (a0.y + a1.y) / 2f), Offset((a0.x + a1.x) / 2f, (a0.y + a1.y) / 2f + 0.24f * kk), 0.02f * kk) }
        }
        for (i in 0 until pasos) {
            val g = 0.3f * ks[i] * (1f - 0.3f * i / pasos)
            drawLine(p.por(lerp(C.LONA, C.BLANCO, 0.25f), -0.1f), vela[i], vela[i + 1], g, StrokeCap.Round)
            drawLine(p.por(lerp(C.LONA, C.BLANCO, 0.3f), 0.5f), vela[i] + Offset(0f, -g * 0.22f), vela[i + 1] + Offset(0f, -g * 0.22f), g * 0.45f, StrokeCap.Round)
            if (i % 2 == 1) drawLine(p.sombra(C.CANAMO), vela[i] + Offset(0f, -g * 0.5f), ejes[i] + Offset(0f, 0.08f * ks[i]), 0.025f * ks[i])
        }
        drawLine(p.medio(C.MADERA), vela[2] + Offset(0f, -0.18f * ks[2]), vela[pasos - 3] + Offset(0f, -0.14f * ks[pasos - 3]), 0.07f * ks[pasos / 2], StrokeCap.Round)
        drawLine(p.luz(C.MADERA).copy(alpha = 0.6f), a, b, 0.03f * ks[pasos / 2], StrokeCap.Round)
        // Las ristras que cuelgan de la botavara, a secar sobre el muelle y el agua.
        for (i in 0 until M.ristrasDeLaBotavara(m)) {
            val t = tRistra(i, M.ristrasDeLaBotavara(m)); val q3 = enBotavara(t); val q = pv(q3) + Offset(0f, 0.06f * M.k(q3.x, q3.z)); val kk = M.k(q3.x, q3.z)
            ristraSeca(p, q, largoRistra(i) * kk, 0.05f * kk, Semilla.de("abismo", "ristraMuelle", i), racimo = i % 2 == 0, sesgo = 0.04f * (i % 3 - 1))
        }
    }

    // ---------------------------------------------------------------- El toldo del café

    private fun DrawScope.toldo(v: MuelleAbismo.Vista) {
        val p = v.p
        val n = 14
        val lona = poligono(listOf(M.pm(TOLDO_X, TOLDO_H, TOLDO_Z0), M.pm(TOLDO_X, TOLDO_H, TOLDO_Z1), M.pm(6.2f, TOLDO_H + 0.6f, TOLDO_Z1), M.pm(6.2f, TOLDO_H + 0.6f, TOLDO_Z0)))
        // Por debajo (se ve desde abajo): la lona a contraluz, con las rayas que el sol transparenta.
        drawPath(lona, p.sombra(lerp(C.LONA, C.BLANCO, 0.35f)))
        clipPath(lona) { for (i in 0 until n step 2) { val z0 = TOLDO_Z0 + (TOLDO_Z1 - TOLDO_Z0) * i / n; val z1 = TOLDO_Z0 + (TOLDO_Z1 - TOLDO_Z0) * (i + 1) / n; drawPath(poligono(listOf(M.pm(TOLDO_X, TOLDO_H, z0), M.pm(TOLDO_X, TOLDO_H, z1), M.pm(6.2f, TOLDO_H + 0.6f, z1), M.pm(6.2f, TOLDO_H + 0.6f, z0))), lerp(p.sombra(C.AZUL), p.medio(C.AZUL), 0.3f * v.r.fuerza)) } }
        // El volante festoneado, que cuelga del borde.
        for (i in 0 until n) {
            val z0 = TOLDO_Z0 + (TOLDO_Z1 - TOLDO_Z0) * i / n; val z1 = TOLDO_Z0 + (TOLDO_Z1 - TOLDO_Z0) * (i + 1) / n
            val a = M.pm(TOLDO_X, TOLDO_H, z0); val b = M.pm(TOLDO_X, TOLDO_H, z1); val kk = M.k(TOLDO_X, (z0 + z1) / 2f); val c = Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f + 0.28f * kk)
            drawPath(Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(b.x, b.y + 0.14f * kk); cubicTo(b.x, b.y + 0.24f * kk, c.x, c.y, c.x, c.y); cubicTo(c.x, c.y, a.x, a.y + 0.24f * kk, a.x, a.y + 0.14f * kk); close() }, if (i % 2 == 0) p.por(C.AZUL, 0.3f) else p.por(C.BLANCO, 0.3f))
        }
        drawLine(p.sombra(C.BREA), M.pm(TOLDO_X, TOLDO_H, TOLDO_Z0), M.pm(TOLDO_X, TOLDO_H, TOLDO_Z1), 0.9f)
        // Los dos postes de hierro del toldo.
        for (z in listOf(3.3f, TOLDO_Z1 - 0.1f)) { val kk = M.k(TOLDO_X, z); drawLine(p.medio(lerp(C.PLOMO, C.BREA, 0.4f)), M.pm(TOLDO_X, 0f, z), M.pm(TOLDO_X, TOLDO_H, z), 0.07f * kk) }
    }

    // ---------------------------------------------------------------- La noche en el muelle

    private fun DrawScope.nocheEnElMuelle(v: MuelleAbismo.Vista) {
        val l = v.l
        if (l.noche < 0.2f) return
        // El charco del farol en las tablas y su luz en el canto.
        if (v.farol > 0.02f) { val pie = M.pm(FAROL_X, 0f, FAROL_Z); val kk = M.k(FAROL_X, FAROL_Z); mancha(pie + Offset(0.3f * kk, 0.1f * kk), 2.6f * kk, 0.7f * kk, C.LUMBRE, 0.42f * v.farol) }
        // La luz del café de este lado, que se derrama desde el borde de la derecha por las tablas y la mesa.
        if (v.cafe > 0.02f) { mancha(Offset(ANCHO + 10f, 236f), 140f, 46f, C.LUMBRE, 0.45f * v.cafe); mancha(Offset(ANCHO, 168f), 60f, 34f, C.LUMBRE, 0.3f * v.cafe) }
    }

    // ---------------------------------------------------------------- Los reflejos y lo que se mueve

    /** Lo de este muelle que se refleja en el río: el muelle tapa casi todo su propio reflejo; queda la amarra. */
    @Suppress("UNUSED_PARAMETER")
    fun DrawScope.reflejos(v: MuelleAbismo.Vista, az: Azar) = Unit

    /** Las luces de este muelle que caen en el agua: ninguna (el muelle las tapa). */
    @Suppress("UNUSED_PARAMETER")
    fun lucesEnElAgua(v: MuelleAbismo.Vista): List<Triple<Float, Float, Float>> = emptyList()

    fun DrawScope.vivo(v: MuelleAbismo.Vista, reloj: Float) {
        val m = v.m
        // La llama de la lamparilla de Nikos y la del farol del muelle.
        if (M.nikosAmarrado(m)) llama(PiezasAbismo.llamaDeLaLamparilla(ICONO, K_ICONO * 1.05f), 0.05f * K_ICONO, reloj, 3)
        if (v.farol > 0.05f) llama(FAROL + Offset(0f, 0.06f * M.k(FAROL_X, FAROL_Z)), 0.08f * M.k(FAROL_X, FAROL_Z), reloj, 5)
        // Los cirios de la Epifanía.
        if (EscenaAbismo.cruz(m)) for (i in 0 until 7 step 2) { val z = 5.0f + i * 1.3f; val kk = M.k(0.25f, z); llama(M.pm(0.25f, 0f, z) + Offset(0f, -0.35f * kk), 0.06f * kk, reloj, i) }
    }
}
