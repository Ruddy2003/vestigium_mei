package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.farol
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.ristra
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.bita
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.iglesia
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.lonja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.mesaDeCafe
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.palmera
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * El muelle largo de la derecha, con su pueblo: el borde de tablas y pilotes que se aleja hacia el centro (la
 * contradiagonal), las bitas, los cafés con sus mesas y sillas fuera (tazas, un tablero de tavli, un comboloi: sin
 * personas, lo que dejan), las cuerdas de esponjas tendidas a secar, la palmera que enmarca por la derecha, la lonja
 * («un patio grande con cuartos alrededor, cada capitán tiene su puerta») y, detrás, más alta, la iglesia de San Nicolás
 * de madera blanca con su cúpula azul y su campanario. Todo en tres cuartos, con su lado de luz y de sombra.
 */
internal object OrillaMuelle {
    private val C = ColorAbismo
    private val M = MuelleAbismo

    /** Dónde está la luz del café (el farol de su puerta) y la iglesia, en el lienzo. */
    val CAFE: Offset get() = M.pm(11.2f, 2.3f, 21.5f)
    val IGLESIA: Offset get() = M.pm(29f, 3f, 58f)

    fun DrawScope.orilla(v: MuelleAbismo.Vista) {
        val m = v.m; val p = v.p
        val zLejos = 160f
        // Las tablas del muelle largo y su canto, que da al río (una cara de madera con los pilotes).
        val tablas = poligono(listOf(M.pm(M.BORDE_DERECHA, 0f, M.DESDE_DERECHA), M.pm(M.BORDE_DERECHA, 0f, zLejos), M.pm(60f, 0f, zLejos), M.pm(60f, 0f, M.DESDE_DERECHA)))
        drawPath(tablas, p.arriba(lerp(C.MADERA_GRIS, C.ARENA, 0.3f), 0.2f))
        clipPath(tablas) {
            var z = M.DESDE_DERECHA
            while (z < zLejos) { drawLine(p.sombra(C.MADERA_GRIS, 0.2f).copy(alpha = 0.5f), M.pm(M.BORDE_DERECHA, 0f, z), M.pm(40f, 0f, z), 0.5f); z *= 1.07f }
        }
        val canto = poligono(listOf(M.pm(M.BORDE_DERECHA, 0f, M.DESDE_DERECHA), M.pm(M.BORDE_DERECHA, 0f, zLejos), M.pm(M.BORDE_DERECHA, M.AGUA, zLejos), M.pm(M.BORDE_DERECHA, M.AGUA, M.DESDE_DERECHA)))
        drawPath(canto, p.lado(C.MADERA_OSC, 0.15f, alReves = true))
        var zp = M.DESDE_DERECHA
        while (zp < zLejos) { val kk = M.k(zp); drawLine(p.lado(C.MADERA_GRIS, 0.15f, alReves = true), M.pm(M.BORDE_DERECHA - 0.08f, 0.05f, zp), M.pm(M.BORDE_DERECHA - 0.08f, M.AGUA - 0.2f, zp), 0.2f * kk); drawLine(p.sombra(C.BREA).copy(alpha = 0.5f), M.pm(M.BORDE_DERECHA - 0.02f, 0.05f, zp), M.pm(M.BORDE_DERECHA - 0.02f, M.AGUA - 0.2f, zp), 0.06f * kk); zp += 4.5f }
        // Las cosas del muelle, con sus sombras.
        val bitas = listOf(18f, 30f, 46f).map { M.sitio(M.BORDE_DERECHA + 0.5f, it) }
        val cafe1 = M.sitio(9.9f, 19.5f, 0.4f); val cafe2 = M.sitio(10.6f, 24f, -0.3f)
        val lonjaS = M.sitio(13.6f, 27f, 0f)
        capaSombra(v.r, 0.85f) { c ->
            clipPath(tablas) {
                for (s in bitas) sombraCorta(v.r, s.pie, 0.3f * s.k, 0.4f * s.k)
                for (s in listOf(cafe1, cafe2)) drawPath(sombraDe(s, v.sol, esquinas(-0.6f, 0.6f, 0f, 0.85f, -0.6f, 0.6f)), c)
                drawPath(sombraDe(lonjaS, v.sol, esquinas(0f, 6f, 0f, 4.4f, 0f, 26f)), c)
                drawPath(sombraDe(M.sitio(9.9f, 16.2f), v.sol, esquinas(-0.25f, 0.25f, 0f, 9f, -0.25f, 0.25f)), c)
            }
        }
        // La iglesia, detrás, cortada por el borde de la derecha; luego la lonja y las cuerdas de esponjas.
        iglesia(p, M.sitio(29f, 58f, -0.25f), v.sol, v.luces.iglesia * v.l.noche + (if (v.luces.iglesia > 0f) 0.15f else 0f), 0.42f)
        lonja(p, lonjaS, v.sol, 26f, Semilla.de("abismo", "lonja"), v.luces.cafes * v.l.noche, 0.25f)
        // Las ristras tendidas a secar en un cabo entre dos postes, delante de la lonja.
        val a = M.pm(12.2f, 2.2f, 30f); val b = M.pm(12.2f, 2.2f, 42f)
        for (q in listOf(M.pm(12.2f, 0f, 30f) to a, M.pm(12.2f, 0f, 42f) to b)) drawLine(p.medio(C.MADERA_GRIS, 0.25f), q.first, q.second, 0.8f)
        drawLine(p.sombra(C.CANAMO, 0.25f), a, b, 0.4f)
        for (i in 0 until 7) { val t = (i + 0.5f) / 7f; val zz = 30f + 12f * t; val q = M.pm(12.2f, 2.15f, zz); val kk = M.k(zz); ristra(p, q, 1.1f * kk, 0.06f * kk, 8, Semilla.de("abismo", "tendida", i), lejos = 0.25f) }
        for (s in bitas.reversed()) bita(p, s, v.sol, true, 0.1f)
        // Los cafés: dos mesas con sus sillas, el farol de la puerta y la fachada del café.
        val fachada = poligono(listOf(M.pm(11.8f, 0f, 18.2f), M.pm(11.8f, 0f, 26.5f), M.pm(11.8f, 3.4f, 26.5f), M.pm(11.8f, 3.4f, 18.2f)))
        drawPath(fachada, p.lado(lerp(C.BLANCO, C.ESPONJA, 0.3f), 0.1f, alReves = true))
        clipPath(fachada) {
            for (i in 0 until 3) { val z0 = 19f + i * 2.5f; drawPath(poligono(listOf(M.pm(11.75f, 0.1f, z0), M.pm(11.75f, 0.1f, z0 + 1.4f), M.pm(11.75f, 2.3f, z0 + 1.4f), M.pm(11.75f, 2.3f, z0))), lerp(p.sombra(C.BREA, 0.1f), C.LUMBRE, 0.75f * v.luces.cafes * v.l.noche)) }
            drawPath(poligono(listOf(M.pm(11.75f, 0f, 15.2f), M.pm(11.75f, 0f, 24.5f), M.pm(11.75f, 0.45f, 24.5f), M.pm(11.75f, 0.45f, 15.2f))), p.medio(lerp(C.AZUL, C.BLANCO, 0.25f), 0.1f))
        }
        // El toldo del café, a rayas azules y blancas.
        val t0 = M.pm(11.8f, 2.6f, 18.2f); val t1 = M.pm(11.8f, 2.6f, 26f); val t2 = M.pm(10.0f, 2.2f, 26f); val t3 = M.pm(10.0f, 2.2f, 18.2f)
        val toldo = poligono(listOf(t0, t1, t2, t3))
        drawPath(toldo, p.medio(C.BLANCO, 0.1f))
        clipPath(toldo) { for (i in 0 until 9) { val zz = 18.2f + i * 0.87f; drawLine(p.medio(C.AZUL, 0.1f), M.pm(11.8f, 2.6f, zz), M.pm(10.0f, 2.2f, zz), 0.3f * M.k(zz)) } }
        mesaDeCafe(v.pincel(cafe2.pie), cafe2, v.sol, Semilla.de("abismo", "mesa", 2), 0.1f)
        mesaDeCafe(v.pincel(cafe1.pie), cafe1, v.sol, Semilla.de("abismo", "mesa", 1), 0.08f)
        farol(v.pincel(CAFE), CAFE, M.k(21.5f), v.luces.cafes * v.l.noche)
        if (v.luces.cafes > 0.02f && v.l.noche > 0.2f) resplandor(M.pm(10.5f, 0.5f, 21f), 50f, 0.5f * v.luces.cafes * v.l.noche, C.LUMBRE)
        // La palmera que enmarca por la derecha, cortada por el borde.
        palmera(p, M.pm(9.9f, 0f, 16.2f), 7f * M.k(16.2f), Semilla.de("abismo", "palmaMuelle"), viento = m.viento)
    }
}
