package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Tienda
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Carreta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Corral
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.caballo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.caballoLejano
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.carreta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.corral
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.luzDePuerta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.rebanoApretado
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.perro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.bovino
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.sombraDeCarreta
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.borron
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.fijo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.rota
import io.github.ruddy2003.vestigium.ui.escenas.ValleMongol.MIRADA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sqrt

/**
 * El campamento, el plano medio: las otras tiendas escalonadas al pie del cerro, en diagonal, cada una más chica, con su
 * giro y su desgaste; detrás y a la derecha, el corral de piedra y estiércol con las ovejas apretadas y los caballos con
 * la grupa al viento, junto al río; una carreta parada. Entre todo, las sendas que pisan en la nieve: de la terraza de
 * Bat a las tiendas, al corral y al agujero del agua.
 */
internal object ValleCampamento {
    private val C = ColorMongol

    /** Dónde está cada tienda del campamento (al pie del cerro, en diagonal), su tamaño, su giro y su desgaste. */
    private class Puesto(val tienda: Tienda, val pie: Offset, val k: Float, val giro: Float, val desgaste: Float, val radio: Float = 3f)

    private val PUESTOS = listOf(
        Puesto(Tienda.MADRE, Offset(180f, 197f), 9.4f, 0.35f, 0.7f),
        Puesto(Tienda.TEMUR, Offset(236f, 189f), 8.6f, 0.15f, 0.35f),
        Puesto(Tienda.BOLAD, Offset(212f, 175f), 8.2f, 0.25f, 0.5f),
        Puesto(Tienda.MADRE_BOLAD, Offset(247f, 162f), 7.6f, 0.05f, 0.3f, 3.4f),
        Puesto(Tienda.VECINO, Offset(268f, 152.6f), 6.2f, -0.1f, 0.6f),
        Puesto(Tienda.CHAMAN, Offset(285f, 144.6f), 5f, -0.2f, 0.8f, 2.3f),
    )

    /** El corral del campamento, junto al río. */
    private val SITIO_CORRAL = Sitio(MIRADA, Offset(302f, 167f), 6.2f, -0.35f)
    private val MURO_CORRAL = listOf(Offset(-4.5f, 2.5f), Offset(-4.5f, -2f), Offset(0f, -3.4f), Offset(4.5f, -2.2f), Offset(5.2f, 2.4f))

    /** La carreta parada (y la de la carne en la matanza). */
    private val SITIO_CARRETA = Sitio(MIRADA, Offset(270f, 192f), 13f, -1.15f)

    /** Las antorchas de la parición, junto al corral (la parte viva pinta su llama). */
    val ANTORCHAS = listOf(Offset(279f, 172f), Offset(330f, 175f))

    /** El fuego del centro de cada tienda del campamento a esta hora: el de la gente que vive en ella. */
    private fun fuegoDe(m: Momento, t: Tienda): Float {
        val junta = EscenaMongol.dondeSeJuntan(m)
        if (junta != null) return if (junta == t) 1f else 0.2f
        val base = EscenaMongol.fuegoDentro(m)
        return when (t) { Tienda.MADRE -> if (EscenaMongol.madreEnferma(m)) maxOf(base, 0.8f) else base; else -> base * (0.85f + 0.05f * t.ordinal) }.coerceIn(0f, 1f)
    }

    private fun puertaDe(m: Momento, t: Tienda): Puerta = when {
        EscenaMongol.armado(m) < 1f -> Puerta.ABIERTA
        EscenaMongol.madreEnferma(m) && t == Tienda.MADRE -> Puerta.CERRADA
        EscenaMongol.luto(m) -> Puerta.CERRADA
        m.hacer == Hacer.GENTE && t == EscenaMongol.dondeSeJuntan(m) -> Puerta.ENTREABIERTA
        m.hacer == Hacer.GENTE && EscenaMongol.fiesta(m) -> Puerta.ENTREABIERTA
        m.hacer == Hacer.GENTE && (t == Tienda.BOLAD || t == Tienda.VECINO) -> Puerta.ENTREABIERTA
        else -> Puerta.CERRADA
    }

    private fun gerDe(m: Momento, u: Puesto, nieve: Float): Ger = Ger(
        Sitio(MIRADA, u.pie, u.k, u.giro), Semilla.de("mongol", "tienda", u.tienda.name), u.desgaste, puertaDe(m, u.tienda), fuegoDe(m, u.tienda), nieve,
        EscenaMongol.armado(m), lejos = 0.06f + (197f - u.pie.y) / 260f, detalle = u.k > 7f, radio = u.radio,
    )

    fun DrawScope.campamento(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val l = p.l; val hay = EscenaMongol.tiendas(m).toSet(); val armado = EscenaMongol.armado(m)
        val az = fijo("campamento")
        val puestos = PUESTOS.filter { it.tienda in hay }
        val tiendas = puestos.map { gerDe(m, it, nieve) }
        // El suelo pisado alrededor de las tiendas: la nieve sucia, el estiércol, la ceniza tirada.
        val pisado = lerp(lerp(C.TIERRA, C.ESTIERCOL, 0.3f), C.NIEVE, nieve * 0.5f)
        for (u in puestos) { val w = u.k * u.radio * 1.9f; drawPath(borron(u.pie + Offset(w * 0.15f, w * 0.06f), w, w * 0.2f, az, 11, 0.25f, 0.5f), p.arriba(pisado, 0.15f).copy(alpha = 0.5f)) }
        sendas(m, p, nieve, az)
        // Donde estuvo la tienda de la madre: la tierra quemada, la ceniza y unas varas negras de la celosía.
        if (EscenaMongol.sitioDeLaMadre(m)) {
            // El día que muere, la ceniza fresca y las varas negras; después, solo el cerco oscuro, que se va borrando.
            val c = PUESTOS[0].pie; val fresco = ValleMongol.cenizaFresca(m); val k = ValleMongol.quemado(m)
            drawPath(borron(c, 30f, 6.6f, az, 12, 0.18f, 0.6f), p.medio(lerp(C.HOLLIN, C.TIERRA, 0.35f), 0.1f).copy(alpha = k))
            drawPath(borron(c + Offset(2f, 0.6f), 18f, 3.6f, az, 10, 0.25f, 0.6f), p.medio(lerp(C.HOLLIN, C.PIEDRA, 0.3f), 0.1f).copy(alpha = 0.8f * k))
            if (fresco) repeat(7) { val q = c + Offset(az.entre(-18f, 18f), az.entre(-3f, 3f)); drawLine(p.medio(C.HOLLIN, 0.1f), q, q + Offset(az.entre(-5f, 5f), az.entre(-1.4f, 0.6f)), 0.7f, StrokeCap.Round) }
            if (nieve > 0.2f) repeat(5) { val q = c + Offset(az.entre(-22f, 22f), az.entre(-4f, 4f)); drawPath(borron(q, az.entre(2f, 5f), 0.8f, az, 6, 0.3f), p.arriba(C.NIEVE, 0.1f).copy(alpha = 0.7f * nieve)) }
        }
        // El día de la mudanza: la tierra negra del invierno pasado, donde vuelven a ponerse las tiendas.
        if (armado < 1f) for (u in puestos) { val w = u.k * u.radio * 1.15f; drawPath(borron(u.pie + Offset(0f, 0.5f), w, w * 0.2f, az, 12, 0.15f, 0.6f), p.medio(lerp(C.HOLLIN, C.TIERRA, 0.5f), 0.1f).copy(alpha = 0.8f * (1f - armado))) }

        val corral = Corral(SITIO_CORRAL, MURO_CORRAL, Semilla.de("mongol", "corral", "medio"), 1.1f, 0.6f, abierto = 3, nieve = nieve, lejos = 0.2f, detalle = true)
        val carreta = Carreta(SITIO_CARRETA, Semilla.de("mongol", "carreta", "valle"), EscenaMongol.carretasCargadas(m), EscenaMongol.cuna(m), lejos = 0.08f)
        val caballos = caballosDelCorral(m)
        capaSombra(sol.r) { c ->
            for (g in tiendas) sombra(PiezasMongol.sombraDeGer(g, sol), c)
            sombraDeCarreta(carreta, sol, c)
            sombraDeMuro(SITIO_CORRAL, MURO_CORRAL, 1.1f, sol, c)
            if (ValleMongol.carneColgada(m)) sombraDeVaras(sol, c)
        }
        // La luz de las puertas abiertas en la nieve, de noche.
        for (g in tiendas) if (g.puerta != Puerta.CERRADA && g.puertaSeVe) luzDePuerta(g, l.noche * g.dentro * 0.9f, 2.6f)
        // De atrás adelante: el corral con las ovejas, las tiendas, los caballos, la carreta.
        corral(corral, p, sol)
        val enCorral = ValleMongol.ovejasEnElCorral(m)
        // La mancha del rebaño crece con las ovejas que hay: pocas se juntan en un rincón, en varias hileras que se tapan;
        // nunca una sola fila larga contra el muro del fondo.
        if (enCorral > 0) {
            val rx = (2.3f * sqrt(enCorral.toFloat())).coerceIn(5f, 19f); val ry = (rx * 0.3f).coerceIn(2.4f, 4.4f)
            rebanoApretado(p, sol, Offset(303f + (19f - rx) * 0.3f, 167f - ry * 0.25f), rx, ry, enCorral, { y -> 3.4f + (y - 160f) * 0.08f }, az, 0.16f, pastan = 0.25f, corderos = (ValleMongol.corderos(m) * 0.7f).toInt())
        }
        if (ValleMongol.antorchas(m)) for (q in ANTORCHAS) resplandor(q + Offset(0f, -10f), 34f, 0.55f * l.noche, C.LUMBRE)
        for (g in tiendas.sortedBy { it.s.pie.y }) ger(g, p, sol)
        // Con el cielo muy cubierto las luces se encienden antes: el fuego de dentro asoma por el aro y por la rendija de
        // la puerta.
        val cub = ValleMongol.cubierto(l)
        if (cub > 0.05f) for (g in tiendas) if (g.fieltro >= 0.999f) {
            val f = cub * (0.45f + 0.55f * g.dentro)
            resplandor(g.humero + Offset(0f, -1.5f), 5f + g.s.k * 1.1f, 0.7f * f, C.LUMBRE)
            if (g.puertaSeVe) drawLine(C.LUMBRE.copy(alpha = (0.9f * f).coerceIn(0f, 1f)), g.pared(-g.mediaPuerta, 0.05f), g.pared(-g.mediaPuerta, g.altoPuerta), (0.05f * g.s.k).coerceIn(0.4f, 1.2f), blendMode = BlendMode.Plus)
        }
        // Los caballos del campamento, con la grupa al viento; el potro blanco desde que nació.
        for ((j, h) in caballos.withIndex()) caballoLejano(p, sol, h.first, 9.6f + (h.first.y - 172f) * 0.12f, if (j % 3 == 0) 1f else -1f, h.second, 0.14f, pasta = j % 2 == 0)
        visitas(m, p, sol)
        muertos(m, p, nieve)
        if (ValleMongol.bueyes(m)) { val buey = lerp(C.CUERO, C.HOLLIN, 0.35f); bovino(p, Offset(246f, 197f), 15f, 1f, buey, yak = false, lejos = 0.08f); bovino(p, Offset(225f, 201f), 15.5f, -1f, lerp(buey, C.FIELTRO, 0.3f), yak = false, pasta = true, lejos = 0.08f) }
        carreta(carreta, p, sol)
        if (ValleMongol.carneColgada(m)) carne(m, p, sol)
        if (EscenaMongol.pielDeLobo(m)) pielTendida(p, SITIO_CARRETA.p(0f, carreta.tabla + 0.75f, 0.3f), 16f, lerp(C.PIEDRA, C.HOLLIN, 0.3f))
    }

    /** Las sendas que se pisan en la nieve (o en la escarcha): de la terraza a las tiendas, al corral y al agujero del agua. */
    private fun DrawScope.sendas(m: Momento, p: Pincel, nieve: Float, az: Azar) {
        // Con el cielo cubierto, lo pisado se ve más oscuro: es lo que despega los planos cuando no hay sombras.
        val cub = ValleMongol.cubierto(p.l)
        val pisada = lerp(lerp(C.TIERRA, C.ESTIERCOL, 0.25f + 0.3f * cub), C.NIEVE, (0.35f + 0.4f * nieve) * (1f - 0.45f * cub))
        val rutas = listOf(
            pts(234f, 205f, 238f, 196f, 250f, 186f, 270f, 178f, 290f, 173f),
            pts(290f, 173f, 306f, 172f, 322f, 170f, 338f, 168.4f, 347f, 166.8f),
            pts(238f, 196f, 226f, 188f, 214f, 182f, 196f, 196f, 186f, 201f),
            pts(250f, 186f, 252f, 172f, 262f, 160f, 276f, 151f),
        )
        for (r in rutas) {
            val rr = rota(r, az, 0.8f, 2)
            val camino = curva(rr)
            val ancho = 2.6f + (rr.sumOf { it.y.toDouble() }.toFloat() / rr.size - 150f) * 0.06f
            drawPath(camino, p.sombra(pisada, 0.12f).copy(alpha = 0.55f), style = Stroke(ancho + 1.2f, cap = StrokeCap.Round))
            drawPath(camino, p.arriba(pisada, 0.12f).copy(alpha = 0.8f), style = Stroke(ancho, cap = StrokeCap.Round))
            // Las huellas de los cascos.
            val huellas = (0 until 14).map { val i = az.entero(rr.size - 1); rr[i] + (rr[i + 1] - rr[i]) * az.real() + Offset(az.entre(-1f, 1f) * ancho * 0.3f, 0f) }
            drawPoints(huellas, PointMode.Points, p.sombra(C.ESTIERCOL, 0.12f).copy(alpha = 0.6f), 0.7f, StrokeCap.Round)
        }
    }

    /** Los caballos que se ven junto al corral: de noche todos; de día pacen fuera la mitad (el resto va con los jinetes). */
    private fun caballosDelCorral(m: Momento): List<Pair<Offset, Color>> {
        val az = fijo("caballos")
        val dia = m.hacer == Hacer.TRABAJO || m.hacer == Hacer.COMER
        val n = (EscenaMongol.caballos(m) * (if (dia) 0.5f else 1f)).toInt()
        val pelos = listOf(PiezasMongol.BAYO, PiezasMongol.TORDO, PiezasMongol.ISABELINO, PiezasMongol.ALAZAN, PiezasMongol.BAYO, PiezasMongol.TORDO, PiezasMongol.ALAZAN, PiezasMongol.ISABELINO, PiezasMongol.BAYO, PiezasMongol.TORDO)
        val lista = (0 until n).map { j -> Offset(314f + (j % 4) * 8.6f + az.entre(-2f, 2f), 176f + (j / 4) * 5f + az.entre(-1.2f, 1.2f)) to pelos[j % pelos.size] }.toMutableList()
        if (EscenaMongol.potroBlanco(m) && !ValleMongol.potroEnElRincon(m)) lista += Offset(334f, 186.6f) to PiezasMongol.BLANCO
        return lista
    }

    /** Los caballos ensillados de los que vienen de visita, atados a una cuerda junto a las tiendas. */
    private fun DrawScope.visitas(m: Momento, p: Pincel, sol: Sol3) {
        val n = ValleMongol.caballosDeVisita(m); if (n == 0) return
        val cuerda = pts(192f, 192f, 228f, 196.6f)
        drawPath(curva(cuerda), p.medio(C.CRIN, 0.1f), style = Stroke(0.5f))
        for (x in listOf(192f, 228f)) drawLine(p.medio(C.MADERA_OSC, 0.1f), Offset(x, 199f), Offset(x, 191f), 0.9f)
        for (j in 0 until n) caballo(p, Offset(198f + j * 11f, 201f + j * 0.6f), 13f, if (j % 2 == 0) -1f else 1f, listOf(PiezasMongol.TORDO, PiezasMongol.ALAZAN, PiezasMongol.BAYO)[j % 3], silla = true, lejos = 0.1f, cintas = EscenaMongol.fiesta(m))
    }

    /** Los animales muertos de hambre en el invierno malo: bultos de lana sobre la nieve dura, con las patas tiesas. */
    private fun DrawScope.muertos(m: Momento, p: Pincel, nieve: Float) {
        val n = ValleMongol.muertosEnLaNieve(m); if (n == 0) return
        for ((j, q) in listOf(Offset(296f, 182f), Offset(318f, 190f), Offset(262f, 179f)).take(n).withIndex()) {
            val w = 4f + j * 0.6f
            drawOval(p.r.color.copy(alpha = 0.3f), Offset(q.x - w, q.y - 0.4f), Size(w * 2.2f, 1.2f), blendMode = BlendMode.Multiply)
            drawOval(p.por(C.LANA, 0.1f, 0.12f), Offset(q.x - w, q.y - w * 0.5f), Size(w * 2f, w * 0.7f))
            drawLine(p.medio(C.CABEZA, 0.12f), q + Offset(-w * 0.2f, -w * 0.2f), q + Offset(-w * 0.1f, -w * 0.9f), 0.5f); drawLine(p.medio(C.CABEZA, 0.12f), q + Offset(w * 0.3f, -w * 0.2f), q + Offset(w * 0.5f, -w * 0.85f), 0.5f)
            drawOval(p.medio(C.CABEZA, 0.12f), Offset(q.x - w * 1.3f, q.y - w * 0.3f), Size(w * 0.6f, w * 0.36f))
            drawPath(borron(q + Offset(0f, -w * 0.15f), w * 0.9f, w * 0.18f, Azar(Semilla.de("mongol", "muerto", j)), 7, 0.3f), p.arriba(C.NIEVE, 0.12f).copy(alpha = 0.8f * nieve))
        }
    }

    /** Las varas de la carreta, alzadas y atadas, de las que cuelga la carne de la matanza congelándose. **[Diseño propio]**: cómo se alzan. */
    private fun varas(): List<Pair<Offset, Offset>> {
        val s = SITIO_CARRETA
        return listOf(s.p(-0.42f, 0.95f, -1.1f) to s.p(-0.36f, 2.9f, -2.2f), s.p(0.42f, 0.95f, -1.1f) to s.p(0.36f, 2.9f, -2.2f))
    }

    private fun DrawScope.sombraDeVaras(sol: Sol3, c: Color) {
        val s = SITIO_CARRETA
        fun suelo(x: Float, y: Float, z: Float): Offset = s.pm(s.mx(x, z) + sol.sx * y, 0f, s.mz(x, z) + sol.sz * y)
        drawLine(c, suelo(-0.42f, 0.95f, -1.1f), suelo(-0.36f, 2.9f, -2.2f), 1.6f); drawLine(c, suelo(0.42f, 0.95f, -1.1f), suelo(0.36f, 2.9f, -2.2f), 1.6f)
        for (k in 0 until 6) { val x = -0.32f + 0.13f * k; drawOval(c, suelo(x, 2.2f, -2.2f) - Offset(2f, 0.7f), Size(4f, 1.4f)) }
    }

    /** La carne colgada de las varas, roja y blanca, y los perros que esperan debajo. */
    private fun DrawScope.carne(m: Momento, p: Pincel, sol: Sol3) {
        val s = SITIO_CARRETA; val madera = lerp(C.MADERA, C.TIERRA, 0.2f); val az = fijo("carne")
        val (a, b) = varas()
        for ((x, y) in listOf(a, b)) { drawLine(p.sombra(madera, 0.08f), x, y, 1.6f, StrokeCap.Round); drawLine(p.luz(madera, 0.08f), x + Offset(0.4f, 0f), y + Offset(0.4f, 0f), 0.5f, StrokeCap.Round) }
        val barra = a.second to b.second
        drawLine(p.medio(madera, 0.08f), barra.first, barra.second, 1.2f, StrokeCap.Round)
        // Los cuartos: rojos, con la grasa blanca y la escarcha.
        for (k in 0 until 6) {
            val t = 0.06f + 0.176f * k; val top = barra.first + (barra.second - barra.first) * t
            val largo = az.entre(9f, 14f); val w = az.entre(2.6f, 3.6f)
            drawLine(p.medio(C.CRIN, 0.08f), top, top + Offset(0f, 1.6f), 0.4f)
            val pieza = Path().apply { moveTo(top + Offset(-w * 0.6f, 1.4f)); lineTo(top + Offset(w * 0.6f, 1.4f)); lineTo(top + Offset(w, 1.4f + largo * 0.6f)); lineTo(top + Offset(w * 0.2f, 1.4f + largo)); lineTo(top + Offset(-w * 0.8f, 1.4f + largo * 0.7f)); close() }
            drawPath(pieza, p.por(lerp(C.ROJO, C.CUERO, 0.2f), if (p.solALaDerecha) 0.1f else -0.1f, 0.08f))
            drawLine(p.luz(C.FIELTRO, 0.08f), top + Offset(-w * 0.3f, 2.4f), top + Offset(-w * 0.1f, 1.4f + largo * 0.8f), 0.6f)
            drawLine(p.luz(lerp(C.ROJO, C.FIELTRO, 0.5f), 0.08f).copy(alpha = 0.7f), top + Offset(w * 0.4f, 2f), top + Offset(w * 0.6f, 1.4f + largo * 0.5f), 0.4f)
        }
        // Los perros, sentados debajo, mirando.
        for ((j, q) in listOf(s.p(-0.2f, 0f, -2.6f), s.p(0.5f, 0f, -2.2f)).withIndex()) perro(p, q, 7f + j * 0.8f, if (j == 0) 1f else -1f, echado = j == 1, lejos = 0.08f)
    }

    /** Una piel tendida (la del lobo sobre la carreta): el cuerpo plano, las patas abiertas, la cola. */
    fun DrawScope.pielTendida(p: Pincel, c: Offset, largo: Float, pelo: Color, lejos: Float = 0.08f) {
        val u = largo
        fun q(x: Float, y: Float) = c + Offset(x * u, y * u)
        val forma = poligono(listOf(q(-0.5f, 0f), q(-0.62f, -0.18f), q(-0.4f, -0.12f), q(-0.2f, -0.2f), q(0.2f, -0.2f), q(0.44f, -0.12f), q(0.62f, -0.2f), q(0.52f, 0.02f), q(0.62f, 0.2f), q(0.4f, 0.12f), q(-0.4f, 0.14f), q(-0.62f, 0.2f)))
        drawPath(forma, p.por(pelo, 0.2f, lejos))
        drawLine(p.sombra(lerp(pelo, C.HOLLIN, 0.4f), lejos), q(-0.5f, 0f), q(0.5f, 0f), 0.12f * u, StrokeCap.Round)
        drawLine(p.medio(pelo, lejos), q(-0.5f, 0f), q(-0.9f, 0.1f), 0.1f * u, StrokeCap.Round)
    }

    /** La sombra de un muro de corral en el suelo: cada tramo, tumbado con el sol. Va dentro de una `capaSombra`. */
    fun DrawScope.sombraDeMuro(s: Sitio, puntos: List<Offset>, alto: Float, sol: Sol3, c: Color) {
        for (i in 0 until puntos.size - 1) {
            val a = puntos[i]; val b = puntos[i + 1]
            fun suelo(x: Float, z: Float, y: Float) = s.pm(s.mx(x, z) + sol.sx * y, 0f, s.mz(x, z) + sol.sz * y)
            drawPath(poligono(suelo(a.x, a.y, 0f), suelo(b.x, b.y, 0f), suelo(b.x, b.y, alto), suelo(a.x, a.y, alto)), c)
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    /** El humo de cada tienda armada, «como hilos grises hacia las estrellas». */
    fun DrawScope.humos(m: Momento, l: Luces, reloj: Float) {
        // El día que se quema la tienda de la madre, un hilo de humo oscuro sube de la ceniza.
        if (ValleMongol.cenizaFresca(m)) humo(l, PUESTOS[0].pie + Offset(0f, -2f), 46f, 5f, reloj, m.viento, 0.55f, 0.1f, 31, 7, 0.6f)
        if (EscenaMongol.armado(m) < 1f) return
        val hay = EscenaMongol.tiendas(m).toSet()
        for ((j, u) in PUESTOS.withIndex()) {
            if (u.tienda !in hay) continue
            val g = gerDe(m, u, 0f)
            humo(l, g.humero, 40f + u.k * 2f, 2f + u.k * 0.4f, reloj, m.viento, 0.2f, 0.2f, j + 5, 7, 0.3f + 0.4f * g.dentro)
        }
    }

}
