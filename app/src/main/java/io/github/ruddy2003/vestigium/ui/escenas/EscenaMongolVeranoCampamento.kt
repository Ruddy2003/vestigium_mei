package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Tienda
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Carreta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.caballo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.carreta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.hogar
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.bovino
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.perro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.rebano
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.rebanoApretado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.sombraDeCarreta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * El plano medio del campamento de verano ([VeranoMongol]): las otras tiendas, escalonadas a la izquierda del pasillo
 * entre la carreta y la tienda de Bat, cada una de su tamaño, su giro y su desgaste, tapándose; otra carreta; el fuego
 * del campamento con su corro de fieltros a la hora de la gente; el rebaño, que de día bebe y pace junto al río y de
 * noche duerme junto a las tiendas; y lo de los días señalados: la lana de la esquila, el fieltro tendido, los bueyes y
 * los rollos del día que llegan, los caballos de fuera.
 */
internal object CampamentoVerano {
    private val C = ColorMongol
    private val MIRADA = VeranoMongol.MIRADA

    private class Puesto(val tienda: Tienda, val pie: Offset, val k: Float, val giro: Float, val desgaste: Float, val radio: Float = 3f)

    /**
     * Las otras tiendas. Ninguna en fila: suben en diagonal desde el pasillo hacia el río, y la de la madre (o, desde que
     * vuelve, la de Temür, que nunca coinciden en verano) queda junto a la de Bat, la más cercana.
     */
    private val PUESTOS = listOf(
        Puesto(Tienda.CHAMAN, Offset(20f, 147f), 2.6f, 0.9f, 0.85f, 2.3f),
        Puesto(Tienda.VECINO, Offset(50f, 150.4f), 3.8f, 0.3f, 0.6f),
        Puesto(Tienda.BOLAD, Offset(84f, 144.6f), 3.1f, -0.25f, 0.5f),
        Puesto(Tienda.MADRE_BOLAD, Offset(108f, 160f), 6.8f, 0.75f, 0.3f, 3.4f),
        Puesto(Tienda.MADRE, Offset(181f, 170f), 7.4f, 0.5f, 0.75f),
        Puesto(Tienda.TEMUR, Offset(178f, 169f), 7.6f, 0.2f, 0.12f),
    )

    /** La otra carreta, entre las tiendas; el día que llegan, otra más, cargada. */
    private val CARRETA_MEDIO = Sitio(MIRADA, Offset(154f, 157f), 5f, 1.25f)
    private val CARRETA_LLEGADA = Sitio(MIRADA, Offset(64f, 146f), 3.4f, 2.0f)

    private fun lejosDe(y: Float) = (0.06f + (176f - y) / 110f).coerceIn(0.02f, 0.4f)

    /** Los bueyes desuncidos del día que llegan, en el sitio de la cuerda de las yeguas (dónde pisan y hacia dónde miran). */
    private val BUEYES = listOf(Offset(166f, 216f) to 1f, Offset(196f, 190f) to -1f)
    private fun altoDeBuey(q: Offset) = 30f + (q.y - 216f) * 0.42f

    /** Donde se echa el perro de noche, junto al fuego del campamento. */
    private val PERRO_FUEGO = Offset(142f, 186f)

    private fun gerDe(m: Momento, u: Puesto, armado: Float, p: Pincel): Ger = Ger(Sitio(MIRADA, u.pie, u.k, u.giro), Semilla.de("mongol", "tienda", u.tienda.name), u.desgaste,
        if (EscenaMongol.madreEnferma(m) && u.tienda == Tienda.MADRE) Puerta.CERRADA else if (EscenaMongol.luto(m)) Puerta.CERRADA else Puerta.ABIERTA,
        EscenaMongol.fuegoDentro(m) * (if (u.tienda == Tienda.CHAMAN) 0.7f else 1f), 0f, armado, lejos = lejosDe(u.pie.y), detalle = u.k > 5f, radio = u.radio)

    /** Los aros de las tiendas del campamento, por donde sale el humo, con su tamaño (para la parte viva). */
    fun humeros(m: Momento): List<Pair<Offset, Float>> {
        if (EscenaMongol.armado(m) < 1f) return emptyList()
        val hay = EscenaMongol.tiendas(m).toSet()
        return PUESTOS.filter { it.tienda in hay }.map { Ger(Sitio(MIRADA, it.pie, it.k, it.giro), 0L, radio = it.radio).humero to it.k / 6f }
    }

    fun DrawScope.campamento(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val hay = EscenaMongol.tiendas(m).toSet(); val armado = EscenaMongol.armado(m)
        val az = Azar(Semilla.de("mongol", "verano", "campamento"))
        val f = EscenaMongol.fuegoFuera(m); val fuego = VeranoMongol.FUEGO
        // De noche el fuego del campamento alumbra lo que tiene cerca: cuánto, según la distancia.
        fun calor(q: Offset) = (l.noche * f * (1f - (q - fuego).getDistance() / 70f)).coerceIn(0f, 0.85f)
        val puestos = PUESTOS.filter { it.tienda in hay }
        val tiendas = puestos.map { gerDe(m, it, armado, p.con(calor(it.pie))) to it }
        val otra = Carreta(CARRETA_MEDIO, Semilla.de("mongol", "carreta", "medio"), EscenaMongol.carretasCargadas(m) || VeranoMongol.fieltroTendido(m), lejos = lejosDe(CARRETA_MEDIO.pie.y), detalle = false)
        val llegada = VeranoMongol.llegada(m)
        val segunda = if (llegada) Carreta(CARRETA_LLEGADA, Semilla.de("mongol", "carreta", "llegada"), true, lejos = lejosDe(CARRETA_LLEGADA.pie.y), detalle = false) else null

        // Con lluvia, una franja de bruma entre el río y el campamento despega los planos.
        if (VeranoMongol.rioTurbio(m)) { mancha(Offset(170f, 141f), 240f, 5f, lerp(l.bruma, l.medio, 0.4f), 0.5f); mancha(Offset(250f, 116f), 200f, 4f, lerp(l.bruma, l.medio, 0.3f), 0.45f) }
        // Las sombras de las tiendas y de las carretas, en una sola capa.
        val bueyes = if (llegada && armado < 1f) BUEYES else emptyList()
        capaSombra(sol.r) { c ->
            for ((g, _) in tiendas) sombra(PiezasMongol.sombraDeGer(g, sol), c)
            sombraDeCarreta(otra, sol, c); if (segunda != null) sombraDeCarreta(segunda, sol, c)
            for ((q, mira) in bueyes) sombraArrojada(sol.r, PiezasMongol.siluetaDeBovino(q, altoDeBuey(q), mira), q, c, 150f, 150f)
        }

        // El rebaño de día, junto al río: bebe en la playa y pace en la orilla, en el hueco del pasillo.
        val cerca = EscenaMongol.ovejasCerca(m); val todas = EscenaMongol.ovejas(m)
        val lejanas = (todas * (1f - cerca)).toInt(); val juntas = todas - lejanas
        rebano(p, (0 until lejanas).map { val u = az.real(); Offset(146f + 82f * u + az.entre(-6f, 6f), 143.6f + az.entre(0f, 6f) + 2f * u) }, { y -> 2f + (y - 140f) * 0.17f }, az, { 0.3f }, pastan = 0.75f)

        // La semana del fieltro: las piezas nuevas, blancas, tendidas en la orilla, y el rollo con su palo.
        if (VeranoMongol.fieltroTendido(m)) fieltroEnLaHierba(p, sol, az)

        // Las tiendas y las carretas, de atrás adelante (lo de la llegada, delante de cada una).
        val cosas = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        for ((g, u) in tiendas) cosas += parte(u.pie.y) {
            if (llegada) huellaDeTienda(p, g)
            ger(g, p.con(calor(u.pie)), sol)
            if (llegada && armado < 1f) rollosEnElSuelo(p, sol, g, u.pie, Azar(Semilla.de("mongol", "rollos", u.tienda.name)))
        }
        cosas += parte(CARRETA_MEDIO.pie.y) { carreta(otra, p.con(calor(CARRETA_MEDIO.pie)), sol) }
        if (segunda != null) cosas += parte(CARRETA_LLEGADA.pie.y) { carreta(segunda, p, sol) }
        for ((q, mira) in bueyes) cosas += parte(q.y) { bovino(p, q, altoDeBuey(q), mira, PiezasMongol.BUEY, yak = false, pasta = mira > 0f, lejos = lejosDe(q.y) * 0.5f) }
        for ((_, pintar) in cosas.sortedBy { it.first }) pintar()

        // El fuego del campamento, con su corro de piedras; a la hora de la gente y de comer, los fieltros alrededor.
        if (VeranoMongol.corro(m)) corro(p.con(0.6f * l.noche * f), fuego)
        hogar(p, fuego, 15f, f, Azar(Semilla.de("mongol", "fuego", "verano")), 0.1f, olla = VeranoMongol.corro(m) && m.hacer == io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer.COMER)
        if (llegada) circuloNegro(p, Offset(150f, 183f))
        if (f > 0.05f) resplandor(fuego + Offset(0f, -3f), 46f, 0.62f * l.noche * f, C.LUMBRE)

        // De noche, las ovejas se juntan apretadas junto a las tiendas, entre la otra carreta y la cuerda; el perro, echado
        // junto al fuego, vigila.
        if (juntas > 0) rebanoApretado(p.con(0.3f * l.noche * f), sol, Offset(166f, 177f), 27f, 5.5f, juntas, { y -> 4.6f + (y - 176f) * 0.22f }, az, 0.12f, pastan = 0.15f)
        if (m.hacer == io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer.GENTE || m.hacer == io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer.DORMIR) perro(p.con(calor(PERRO_FUEGO)), PERRO_FUEGO, 9f, -1f, echado = true, lejos = 0.1f)

        // La lana de la esquila: montones grises, blancos y marrones junto a las tiendas.
        if (VeranoMongol.lanaEnElSuelo(m)) for ((q, w) in listOf(Offset(146f, 152f) to 9f, Offset(212f, 151f) to 8f, Offset(160f, 250f) to 14f)) montonDeLana(p, sol, q, w, Azar(Semilla.de("mongol", "lana", q.x.toInt())))

        // Los caballos de fuera, ensillados: el primero lo ata a la carreta el primer plano ([CercaVerano]); los otros, junto
        // al río, con su poste.
        val fuera = VeranoMongol.caballosDeFuera(m)
        val pelos = listOf(PiezasMongol.TORDO, PiezasMongol.ALAZAN, PiezasMongol.ISABELINO)
        for (i in 1 until fuera) {
            val q = listOf(Offset(0f, 0f), Offset(214f, 150f), Offset(229f, 147.6f))[i]
            val alto = 12.5f - (150f - q.y) * 0.4f
            sombraCorta(sol.r, q, alto * 0.9f, alto * 0.5f)
            caballo(p.con(calor(q)), q, alto, if (i % 2 == 0) 1f else -1f, pelos[i], pasta = i == 2, silla = true, cintas = EscenaMongol.fiesta(m) || VeranoMongol.carrera(m), lejos = lejosDe(q.y))
        }
        if (fuera > 1) { drawLine(p.medio(C.MADERA_OSC, 0.3f), Offset(222f, 150f), Offset(222f, 142f), 0.6f); drawLine(p.medio(C.CRIN, 0.3f), Offset(222f, 143f), Offset(216f, 141f), 0.3f) }
    }

    /** La marca que deja una tienda en la hierba donde se arma: un círculo de hierba aplastada y amarillenta, con la del fuego del centro. */
    private fun DrawScope.huellaDeTienda(p: Pincel, g: Ger) {
        val anillo = (0 until 24).map { val t = (2 * PI * it / 24).toFloat(); g.pared(t, 0f, g.radio + 0.5f) }
        drawPath(poligono(anillo), p.medio(lerp(C.ESTEPA, C.FIELTRO, 0.25f), g.lejos).copy(alpha = 0.7f))
        val centro = (0 until 12).map { val t = (2 * PI * it / 12).toFloat(); g.pared(t, 0f, 0.5f) }
        drawPath(poligono(centro), p.medio(C.HOLLIN, g.lejos).copy(alpha = 0.6f))
    }

    /** Los rollos de fieltro que esperan en la hierba, junto a la tienda a medio armar. */
    private fun DrawScope.rollosEnElSuelo(p: Pincel, sol: Sol3, g: Ger, pie: Offset, az: Azar) {
        val k = g.s.escala(0f, 0f)
        for (i in 0 until 2) {
            val c = pie + Offset((g.radio + 0.8f + i * 0.5f) * k * (if (i == 0) 1f else -1f), (0.6f + 0.3f * i) * k * 0.3f)
            val w = 1.3f * k; val r = 0.22f * k
            sombraCorta(sol.r, c, w, r)
            drawRoundRect(Brush.verticalGradient(listOf(p.luz(C.FIELTRO, g.lejos), p.sombra(C.FIELTRO, g.lejos)), c.y - 2f * r, c.y), Offset(c.x - w / 2f, c.y - 2f * r), Size(w, 2f * r), androidx.compose.ui.geometry.CornerRadius(r))
            drawOval(p.medio(lerp(C.FIELTRO, C.TIERRA, 0.2f), g.lejos), Offset(c.x + w / 2f - r * 0.7f, c.y - 2f * r), Size(r * 1.4f, 2f * r))
            for (u in listOf(0.25f, 0.75f)) drawLine(p.sombra(C.CRIN, g.lejos), Offset(c.x - w / 2f + w * u, c.y - 2f * r), Offset(c.x - w / 2f + w * u, c.y), (0.04f * k).coerceAtLeast(0.3f))
        }
    }

    /** El círculo negro del fuego del verano pasado: ceniza vieja y hierba quemada, con sus piedras **[Diseño propio]**. */
    private fun DrawScope.circuloNegro(p: Pincel, c: Offset) {
        mancha(c, 9f, 2.2f, p.medio(C.HOLLIN, 0.1f), 0.75f)
        mancha(c, 5f, 1.2f, p.medio(lerp(C.HOLLIN, C.PIEDRA, 0.4f), 0.1f), 0.6f)
        for (j in 0 until 7) { val a = (2 * PI * j / 7).toFloat(); val q = c + Offset(cos(a) * 8f, sin(a) * 2f); drawOval(p.por(C.PIEDRA, if (sin(a) > 0f) 0.3f else -0.4f, 0.1f), Offset(q.x - 1.2f, q.y - 0.8f), Size(2.4f, 1.3f)) }
    }

    /** Los fieltros de sentarse alrededor del fuego, con el cubo del airag y los cuencos (sin nadie: se ve lo que dejan). */
    private fun DrawScope.corro(p: Pincel, c: Offset) {
        for ((d, w, col) in listOf(Triple(Offset(-16f, 5f), 12f, C.FIELTRO), Triple(Offset(15f, 4f), 10f, lerp(C.FIELTRO, C.ROJO, 0.3f)), Triple(Offset(-2f, -6f), 9f, C.FIELTRO_VIEJO))) {
            val q = c + d
            drawPath(poligono(q + Offset(-w / 2f, 1.4f), q + Offset(w / 2f, 1.2f), q + Offset(w / 2f - 1.6f, -1.4f), q + Offset(-w / 2f + 1.2f, -1.2f)), p.arriba(col, 0.1f))
            drawPath(poligono(q + Offset(-w / 2f, 1.4f), q + Offset(w / 2f, 1.2f), q + Offset(w / 2f - 1.6f, -1.4f), q + Offset(-w / 2f + 1.2f, -1.2f)), p.medio(C.ROJO, 0.1f).copy(alpha = 0.6f), style = Stroke(0.4f))
        }
        // El cubo del airag y dos cuencos boca abajo.
        val b = c + Offset(22f, 2f)
        drawPath(poligono(b + Offset(-2.4f, -5f), b + Offset(2.4f, -5f), b + Offset(2f, 0f), b + Offset(-2f, 0f)), p.por(C.CUERO, if (p.solALaDerecha) 0.3f else -0.2f, 0.1f))
        drawOval(p.luz(C.FIELTRO, 0.1f), Offset(b.x - 2.4f, b.y - 5.6f), Size(4.8f, 1.2f))
        for (dx in listOf(-24f, 6f)) { val q = c + Offset(dx, 7f); drawArc(p.por(C.MADERA, 0.2f, 0.1f), 180f, 180f, true, Offset(q.x - 1.4f, q.y - 1.2f), Size(2.8f, 2.4f)) }
    }

    /** Un montón de lana recién esquilada: vellones apilados de tres colores, con su lado de luz y su sombra. */
    fun DrawScope.montonDeLana(p: Pincel, sol: Sol3, pie: Offset, ancho: Float, az: Azar, lejos: Float = 0.05f) {
        sombraCorta(sol.r, pie, ancho * 1.1f, ancho * 0.4f)
        val colores = listOf(C.LANA, lerp(C.LANA, C.PIEDRA, 0.5f), lerp(C.LANA, C.CUERO, 0.55f), lerp(C.LANA, Color.White, 0.3f))
        val n = (ancho * 1.2f).toInt().coerceIn(5, 18)
        for (i in 0 until n) {
            val k = i / n.toFloat(); val w = ancho * az.entre(0.24f, 0.4f) * (1f - 0.4f * k)
            val c = pie + Offset(az.entre(-0.42f, 0.42f) * ancho * (1f - 0.6f * k), -ancho * 0.12f - ancho * 0.42f * k + az.entre(-1f, 1f))
            val col = colores[az.entero(colores.size)]
            val forma = Path().apply { moveTo(c + Offset(-w, w * 0.3f)); cubicTo(c + Offset(-w * 1.1f, -w * 0.4f), c + Offset(-w * 0.2f, -w * 0.8f), c + Offset(w * 0.3f, -w * 0.6f)); cubicTo(c + Offset(w * 1.1f, -w * 0.5f), c + Offset(w * 1.1f, w * 0.3f), c + Offset(w * 0.6f, w * 0.4f)); close() }
            drawPath(forma, Brush.verticalGradient(listOf(p.luz(col, lejos), p.medio(col, lejos), p.sombra(col, lejos)), c.y - w * 0.8f, c.y + w * 0.4f))
            if (ancho > 9f) repeat(3) { val q = c + Offset(az.entre(-w, w) * 0.6f, az.entre(-w, w) * 0.3f); drawOval(p.sombra(col, lejos).copy(alpha = 0.3f), Offset(q.x - w * 0.15f, q.y - w * 0.06f), Size(w * 0.3f, w * 0.14f)) }
        }
    }

    /** El fieltro nuevo, tendido en la orilla: piezas blancas en perspectiva, con el borde irregular, y el rollo grande con su palo y la cuerda de arrastrar. */
    private fun DrawScope.fieltroEnLaHierba(p: Pincel, sol: Sol3, az: Azar) {
        for ((c, w) in listOf(Offset(166f, 152f) to 24f, Offset(116f, 158f) to 20f, Offset(206f, 147f) to 16f)) {
            val h = w * 0.16f
            val borde = (0 until 12).map { j -> val t = j / 12f * 2f * PI.toFloat(); c + Offset(cos(t) * w * 0.5f * az.entre(0.9f, 1.05f) * (if (kotlin.math.abs(cos(t)) > 0.6f) 1f else 1.08f), sin(t) * h * az.entre(0.85f, 1.05f)) }
            sombraCorta(sol.r, c + Offset(0f, h), w, 0.6f)
            drawPath(poligono(borde), p.arriba(lerp(C.FIELTRO, Color.White, 0.45f), 0.12f))
            drawPath(poligono(borde), p.medio(C.FIELTRO_VIEJO, 0.12f).copy(alpha = 0.5f), style = Stroke(0.35f))
            repeat(4) { val q = c + Offset(az.entre(-w, w) * 0.35f, az.entre(-h, h) * 0.5f); drawLine(p.sombra(C.FIELTRO, 0.12f).copy(alpha = 0.3f), q, q + Offset(az.entre(2f, 5f), 0.2f), 0.3f) }
        }
        // El rollo grande, con el palo que lo atraviesa y la cuerda para arrastrarlo a caballo.
        val r0 = Offset(140f, 166f); val largo = 16f; val rad = 2.6f
        sombraCorta(sol.r, r0, largo, rad * 2f)
        drawLine(p.medio(C.MADERA_OSC, 0.1f), r0 + Offset(-largo * 0.6f, -rad), r0 + Offset(largo * 0.6f, -rad - 0.4f), 0.8f, StrokeCap.Round)
        drawRoundRect(Brush.verticalGradient(listOf(p.luz(C.FIELTRO, 0.1f), p.medio(C.FIELTRO, 0.1f), p.sombra(C.FIELTRO, 0.1f)), r0.y - 2f * rad, r0.y), Offset(r0.x - largo / 2f, r0.y - 2f * rad), Size(largo, 2f * rad), androidx.compose.ui.geometry.CornerRadius(rad))
        for (u in listOf(0.2f, 0.5f, 0.8f)) drawLine(p.sombra(lerp(C.FIELTRO, C.CRIN, 0.4f), 0.1f), Offset(r0.x - largo / 2f + largo * u, r0.y - 2f * rad), Offset(r0.x - largo / 2f + largo * u, r0.y), 0.4f)
        drawPath(Path().apply { moveTo(r0 + Offset(largo * 0.6f, -rad)); cubicTo(r0 + Offset(largo * 0.9f, -rad + 2f), r0 + Offset(largo * 1.2f, 1f), r0 + Offset(largo * 1.5f, 0.4f)) }, p.medio(C.CRIN, 0.1f), style = Stroke(0.45f))
    }
}
