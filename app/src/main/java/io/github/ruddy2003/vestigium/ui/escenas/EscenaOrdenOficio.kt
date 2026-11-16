package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PatioOrden.ALA
import io.github.ruddy2003.vestigium.ui.escenas.PatioOrden.CAMARA
import io.github.ruddy2003.vestigium.ui.escenas.PatioOrden.Edificio
import io.github.ruddy2003.vestigium.ui.escenas.PatioOrden.SALA
import io.github.ruddy2003.vestigium.ui.escenas.PatioOrden.normalDe
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.caja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.palo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono

/**
 * Lo que hace que los edificios del patio no sean cajas **[Diseño propio]**: la Cámara con sus muros gruesos en talud,
 * sus contrafuertes y su puerta de hierro; las azoteas con su pretil, sus chimeneas y las vigas que asoman; las puertas de
 * madera, los postigos abiertos, la escalera de fuera del ala, el toldo de la enfermería, el tendedero de la azotea y la
 * parra seca junto a la puerta de la Cámara.
 */
internal object OficioOrden {
    private val C = ColorOrden
    private const val P = CamOrden.PATIO

    private fun mas(a: V3, n: V3, d: Float) = V3(a.x + n.x * d, a.y + n.y * d, a.z + n.z * d)

    // ---------------------------------------------------------------- La Cámara

    /** La Cámara: el talud al pie de los muros, los contrafuertes, la puerta de hierro y la parra seca. */
    fun DrawScope.camara(v: VistaOrden) {
        val e = CAMARA; val lz = v.lz
        for (i in listOf(0, 3)) talud(v, e, i)
        for ((i, u) in listOf(0 to 0.12f, 0 to 0.88f, 3 to 0.2f, 3 to 0.85f)) contrafuerte(v, e, i, u)
        puertaDeHierro(v)
        parra(v)
        vigas(v, e, 0); vigas(v, e, 3)
        pretil(v, e, 0.06f)
        // La carretilla y las cajas de la Cámara, junto a su puerta.
        val n = normalDe(e, 0); val pie = mas(e.enCara(0, 0.72f, P), n, 1.4f)
        caja(lz, pie.x, pie.z, e.giro, 0.8f, 0.8f, P, P + 0.7f, C.MADERA, 0.06f)
        caja(lz, pie.x + 0.2f, pie.z - 0.1f, e.giro + 0.3f, 0.6f, 0.6f, P + 0.7f, P + 1.2f, lerp(C.MADERA, C.ARENA, 0.3f), 0.06f)
    }

    /** El talud: la base del muro, más gruesa y en rampa, de piedra más oscura. */
    private fun DrawScope.talud(v: VistaOrden, e: Edificio, i: Int) {
        val n = normalDe(e, i); val a = e.enCara(i, -0.04f, P); val b = e.enCara(i, 1.04f, P)
        val forma = listOf(mas(a, n, 0.8f), mas(b, n, 0.8f), V3(b.x, P + 1.9f, b.z), V3(a.x, P + 1.9f, a.z))
        cara(v.lz, forma, V3(n.x, 0.45f, n.z), lerp(C.PIEDRA, C.ROCA, 0.35f), 0.06f)
        drawLine(lerp(v.lz.color(C.PIEDRA, V3(n.x, 1f, n.z), 0.06f), C.ARENA, 0.25f), CamOrden.p(a.x, P + 1.9f, a.z), CamOrden.p(b.x, P + 1.9f, b.z), 0.6f)
    }

    /** Un contrafuerte contra la cara [i], a lo largo [u]: un machón de piedra que se estrecha hacia arriba. */
    private fun DrawScope.contrafuerte(v: VistaOrden, e: Edificio, i: Int, u: Float) {
        val n = normalDe(e, i); val c = mas(e.enCara(i, u, P), n, 0.55f)
        val (ancho, fondo) = if (i == 0 || i == 1) 0.9f to 1.1f else 1.1f to 0.9f
        caja(v.lz, c.x, c.z, e.giro, ancho, fondo, P, P + (e.alto - P) * 0.5f, lerp(C.PIEDRA, C.ROCA, 0.2f), 0.06f, arriba = C.LOSA)
        caja(v.lz, c.x - n.x * 0.15f, c.z - n.z * 0.15f, e.giro, ancho * 0.85f, fondo * 0.7f, P + (e.alto - P) * 0.5f, P + (e.alto - P) * 0.72f, lerp(C.PIEDRA, C.ROCA, 0.2f), 0.06f, arriba = C.LOSA)
    }

    /** La puerta de hierro de la Cámara, en su marco de piedra clara, con sus remaches; el inventario, abierta de par en par. */
    private fun DrawScope.puertaDeHierro(v: VistaOrden) {
        val e = CAMARA; val n = normalDe(e, 0); val lz = v.lz
        val u0 = 0.42f; val u1 = 0.6f; val h1 = P + 2.5f
        val marco = listOf(e.enCara(0, u0 - 0.04f, P), e.enCara(0, u1 + 0.04f, P), e.enCara(0, u1 + 0.04f, h1 + 0.3f), e.enCara(0, u0 - 0.04f, h1 + 0.3f)).map { mas(it, n, 0.02f) }
        drawPath(poligono(marco.map { CamOrden.p(it) }), lz.color(lerp(C.PIEDRA, C.ARENA, 0.4f), n, 0.06f))
        val hueco = listOf(e.enCara(0, u0, P), e.enCara(0, u1, P), e.enCara(0, u1, h1), e.enCara(0, u0, h1)).map { mas(it, n, 0.03f) }
        if (EscenaOrden.inventario(v.m)) {
            drawPath(poligono(hueco.map { CamOrden.p(it) }), v.l.tono(lerp(C.HOLLIN, C.ROCA_HONDA, 0.3f)))
            val hoja = listOf(hueco[0], mas(hueco[0], n, 1.1f), V3(hueco[0].x + n.x * 1.1f, h1, hueco[0].z + n.z * 1.1f), hueco[3])
            drawPath(poligono(hoja.map { CamOrden.p(it) }), lz.color(C.HIERRO, normalDe(e, 2), 0.06f))
            return
        }
        drawPath(poligono(hueco.map { CamOrden.p(it) }), lz.color(C.HIERRO, n, 0.06f))
        val k = CamOrden.k(hueco[0].x, hueco[0].z); val clavo = lz.color(C.HIERRO_CLARO, V3(n.x, 0.5f, n.z), 0.06f)
        for (f in 0 until 4) for (c in 0 until 3) drawCircle(clavo, (k * 0.04f).coerceAtLeast(0.35f), CamOrden.p(mas(e.enCara(0, u0 + (u1 - u0) * (0.2f + c * 0.3f), P + 0.4f + f * 0.6f), n, 0.04f)))
        drawLine(lz.color(C.HIERRO_CLARO, n, 0.06f), CamOrden.p(mas(e.enCara(0, (u0 + u1) / 2f, P), n, 0.04f)), CamOrden.p(mas(e.enCara(0, (u0 + u1) / 2f, h1), n, 0.04f)), (k * 0.03f).coerceAtLeast(0.4f))
    }

    /** La parra seca junto a la puerta de la Cámara: dos postes, el emparrado de palos y los sarmientos retorcidos, sin hoja. */
    private fun DrawScope.parra(v: VistaOrden) {
        val e = CAMARA; val n = normalDe(e, 0); val lz = v.lz
        val a = e.enCara(0, 0.66f, P); val b = e.enCara(0, 0.96f, P)
        val pa = mas(a, n, 1.8f); val pb = mas(b, n, 1.8f)
        for (q in listOf(pa, pb)) palo(lz, q, V3(q.x, P + 2.6f, q.z), 0.13f, C.MADERA_OSC, 0.06f)
        palo(lz, V3(pa.x, P + 2.6f, pa.z), V3(pb.x, P + 2.6f, pb.z), 0.12f, C.MADERA_OSC, 0.06f)
        for (t in listOf(0.2f, 0.5f, 0.8f)) palo(lz, V3(a.x + (b.x - a.x) * t, P + 2.8f, a.z + (b.z - a.z) * t), V3(pa.x + (pb.x - pa.x) * t, P + 2.6f, pa.z + (pb.z - pa.z) * t), 0.07f, C.MADERA, 0.06f)
        val sarmiento = v.l.tono(lerp(C.MADERA_OSC, C.ARENA, 0.15f), 0.06f); val az = PiezasOrden.fijo("parra")
        val tronco = (0..6).map { j -> val t = j / 6f; CamOrden.p(pa.x + az.entre(-0.15f, 0.15f), P + 2.6f * t, pa.z + az.entre(-0.15f, 0.15f)) }
        drawPath(Path().apply { moveTo(tronco[0].x, tronco[0].y); for (q in tronco.drop(1)) lineTo(q.x, q.y) }, sarmiento, style = Stroke(1f, cap = StrokeCap.Round))
        repeat(9) {
            val t = az.entre(0f, 1f); val s0 = V3(pa.x + (pb.x - pa.x) * t, P + 2.65f, pa.z + (pb.z - pa.z) * t)
            val q0 = CamOrden.p(s0); val q1 = CamOrden.p(s0.x + az.entre(-0.6f, 0.6f), P + az.entre(1.6f, 2.5f), s0.z + az.entre(-0.4f, 0.4f))
            val m = Offset((q0.x + q1.x) / 2f + az.entre(-3f, 3f), (q0.y + q1.y) / 2f + az.entre(-2f, 2f))
            drawPath(Path().apply { moveTo(q0.x, q0.y); cubicTo(m.x, m.y, m.x, q1.y, q1.x, q1.y) }, sarmiento, style = Stroke(0.5f, cap = StrokeCap.Round))
        }
    }

    // ---------------------------------------------------------------- Las azoteas y los muros

    /** El pretil de la azotea: la losa de dentro, más baja y en sombra, deja ver el borde del muro. */
    fun DrawScope.pretil(v: VistaOrden, e: Edificio, bruma: Float) {
        val dentro = PiezasOrden.esquinas(e.s, e.v, e.giro, e.ancho - 0.6f, e.fondo - 0.6f).map { CamOrden.p(it.x, e.alto, it.y) }
        drawPath(poligono(dentro), v.lz.color(if (v.nieve > 0f) C.NIEVE else lerp(C.LOSA, C.HOLLIN, 0.22f), V3(0.3f, 1f, 0.3f), bruma))
    }

    /** Las cabezas de las vigas del techo, que asoman en lo alto de la cara [i], con su sombra corta debajo. */
    fun DrawScope.vigas(v: VistaOrden, e: Edificio, i: Int) {
        val n = normalDe(e, i); if (!CamOrden.seVe(e.enCara(i, 0.5f, e.alto - 1f), n)) return
        val largo = if (i < 2) e.ancho else e.fondo; val cuantas = (largo / 1.1f).toInt()
        for (j in 1 until cuantas) {
            val a = e.enCara(i, j / cuantas.toFloat(), e.alto - 0.75f)
            palo(v.lz, a, mas(a, n, 0.45f), 0.2f, C.MADERA_OSC, 0.08f)
        }
    }

    /** Una chimenea en la azotea, con su sombrerete de losa. */
    private fun DrawScope.chimenea(v: VistaOrden, e: Edificio, u: Float, w: Float, bruma: Float) {
        val c = PiezasOrden.esquinas(e.s, e.v, e.giro, e.ancho * (1f - 2f * u), e.fondo * (1f - 2f * w))[0]
        caja(v.lz, c.x, c.y, e.giro, 0.7f, 0.7f, e.alto, e.alto + 1.4f, lerp(C.PIEDRA, C.HOLLIN, 0.15f), bruma)
        caja(v.lz, c.x, c.y, e.giro, 0.95f, 0.95f, e.alto + 1.4f, e.alto + 1.55f, C.LOSA, bruma)
    }

    /** Una puerta de madera en la cara [i], con su dintel de piedra y sus tablas. */
    private fun DrawScope.puertaDeMadera(v: VistaOrden, e: Edificio, i: Int, u: Float, ancho: Float, alto: Float, bruma: Float) {
        val n = normalDe(e, i); val du = ancho / 2f / (if (i < 2) e.ancho else e.fondo)
        val q = listOf(e.enCara(i, u - du, P), e.enCara(i, u + du, P), e.enCara(i, u + du, P + alto), e.enCara(i, u - du, P + alto)).map { mas(it, n, 0.02f) }
        drawPath(poligono(q.map { CamOrden.p(it) }), v.lz.color(C.MADERA_OSC, n, bruma))
        val tabla = v.l.tono(lerp(C.MADERA_OSC, C.HOLLIN, 0.35f), bruma)
        for (t in listOf(0.33f, 0.66f)) drawLine(tabla, CamOrden.p(q[0].x + (q[1].x - q[0].x) * t, P, q[0].z + (q[1].z - q[0].z) * t), CamOrden.p(q[0].x + (q[1].x - q[0].x) * t, P + alto, q[0].z + (q[1].z - q[0].z) * t), 0.4f)
        drawLine(v.lz.color(lerp(C.PIEDRA, C.ARENA, 0.4f), n, bruma), CamOrden.p(q[3]), CamOrden.p(q[2]), (CamOrden.k(q[0].x, q[0].z) * 0.2f).coerceAtLeast(0.6f))
    }

    /** Los postigos de madera, abiertos contra el muro a los dos lados de una ventana (de día). */
    private fun DrawScope.postigos(v: VistaOrden, e: Edificio, i: Int, u: Float, h: Float, ancho: Float, alto: Float, bruma: Float) {
        if (v.l.noche > 0.5f || EscenaOrden.postigos(v.m)) return
        val n = normalDe(e, i); val largo = if (i < 2) e.ancho else e.fondo; val du = ancho / largo
        for (lado in listOf(-1f, 1f)) {
            val u0 = u + lado * du * 0.55f; val u1 = u + lado * du * 1.05f
            val q = listOf(e.enCara(i, u0, h), e.enCara(i, u1, h), e.enCara(i, u1, h + alto), e.enCara(i, u0, h + alto)).map { CamOrden.p(mas(it, n, 0.04f)) }
            drawPath(poligono(q), v.lz.color(lerp(C.MADERA, C.VIDRIO, 0.2f), n, bruma))
        }
    }

    // ---------------------------------------------------------------- El ala y la sala

    /** El ala: la puerta de la enfermería con su toldo, los postigos, la escalera de fuera a la azotea, la chimenea y el tendedero. */
    fun DrawScope.ala(v: VistaOrden, bruma: Float) {
        val e = ALA; val lz = v.lz
        pretil(v, e, bruma)
        vigas(v, e, 0)
        chimenea(v, e, 0.18f, 0.25f, bruma)
        puertaDeMadera(v, e, 0, 0.12f, 1.1f, 2.1f, bruma)
        postigos(v, e, 0, 0.3f, P + 1.3f, 1.2f, 1.3f, bruma)
        // El toldo de la enfermería: una tela parda tendida en dos palos, sobre la puerta y la ventana.
        val n = normalDe(e, 0); val a = e.enCara(0, 0.02f, P + 2.6f); val b = e.enCara(0, 0.42f, P + 2.6f)
        val fa = V3(a.x + n.x * 1.6f, P + 2.1f, a.z + n.z * 1.6f); val fb = V3(b.x + n.x * 1.6f, P + 2.1f, b.z + n.z * 1.6f)
        for (q in listOf(fa, fb)) palo(lz, V3(q.x, P, q.z), q, 0.08f, C.MADERA_OSC, bruma)
        cara(lz, listOf(a, b, fb, fa), V3(n.x * 0.3f, 1f, n.z * 0.3f), lerp(C.TELA_VIEJA, C.ARENA, 0.25f), bruma)
        escalera(v, e, bruma)
        tendedero(v, e, bruma)
    }

    /** La escalera de fuera del ala, de piedra, arrimada a su cara de delante: sube de la derecha a la azotea. */
    private fun DrawScope.escalera(v: VistaOrden, e: Edificio, bruma: Float) {
        val n = normalDe(e, 0); val lz = v.lz; val pasos = 9
        for (j in 0 until pasos) {
            val u1 = 0.96f - j * 0.045f; val u0 = u1 - 0.045f
            val h = P + (e.alto - P) * (j + 1) / pasos
            val q = listOf(e.enCara(0, u0, P), e.enCara(0, u1, P)).map { it }
            val pie0 = q[0]; val pie1 = q[1]
            caja(lz, (pie0.x + pie1.x) / 2f + n.x * 0.55f, (pie0.z + pie1.z) / 2f + n.z * 0.55f, e.giro, 0.045f * e.ancho + 0.02f, 1.1f, if (j == 0) P else h - (e.alto - P) / pasos * 1.5f, h, lerp(C.PIEDRA, C.ARENA, 0.12f), bruma, arriba = lerp(C.LOSA, C.ARENA, 0.2f))
        }
    }

    /** El tendedero de la azotea del ala: dos palos, la cuerda y la ropa tendida (paños, nunca figuras). */
    private fun DrawScope.tendedero(v: VistaOrden, e: Edificio, bruma: Float) {
        if (v.l.noche > 0.6f || v.m.ficha.clima == io.github.ruddy2003.vestigium.vidas.Clima.LLUVIA) return
        val q = PiezasOrden.esquinas(e.s, e.v, e.giro, e.ancho - 1.4f, e.fondo - 2.2f)
        val a = V3(q[0].x, e.alto, q[0].y); val b = V3(q[1].x, e.alto, q[1].y)
        for (p in listOf(a, b)) palo(v.lz, p, V3(p.x, e.alto + 1.6f, p.z), 0.07f, C.MADERA_OSC, bruma)
        val cuerda = v.l.tono(C.MADERA_OSC, bruma)
        val pa = CamOrden.p(a.x, e.alto + 1.55f, a.z); val pb = CamOrden.p(b.x, e.alto + 1.55f, b.z)
        drawLine(cuerda, pa, pb, 0.4f)
        val telas = listOf(C.PAPEL, lerp(C.TELA, C.VIDRIO, 0.3f), C.CARMESI, lerp(C.ARENA, C.PAPEL, 0.4f))
        for ((j, t) in listOf(0.15f, 0.38f, 0.6f, 0.8f).withIndex()) {
            val c = V3(a.x + (b.x - a.x) * t, e.alto + 1.55f, a.z + (b.z - a.z) * t); val d = V3(a.x + (b.x - a.x) * (t + 0.13f), e.alto + 1.55f, a.z + (b.z - a.z) * (t + 0.13f))
            val cae = 0.6f + 0.25f * (j % 2)
            val pano = listOf(c, d, V3(d.x, d.y - cae, d.z), V3(c.x, c.y - cae * 0.9f, c.z)).map { CamOrden.p(it) }
            drawPath(poligono(pano), v.lz.color(telas[j], V3(0f, 0f, -1f), bruma))
        }
    }

    /** La sala del Código: la puerta grande de madera bajo las ventanas, el pretil, las vigas y su chimenea. */
    fun DrawScope.sala(v: VistaOrden, bruma: Float) {
        val e = SALA
        pretil(v, e, bruma)
        vigas(v, e, 0)
        chimenea(v, e, 0.12f, 0.3f, bruma)
        puertaDeMadera(v, e, 0, 0.5f, 1.8f, 3f, bruma)
    }
}
