package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Colgado
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Tienda
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Carreta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.caballo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.caballoLejano
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.carreta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.hogar
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.luzDePuerta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.odre
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.ovoo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.rebano
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.sombraDeCarreta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * El campamento de verano de Bat: el segundo cuadro de Mongol (`composicion.md` de la tanda S12, cuadro B), del 25 de
 * mayo al 24 de septiembre. Un sitio abierto, de cielo grande: se mira de pie entre una carreta y la tienda de Bat, por
 * el pasillo que queda entre las dos, hacia el río. El horizonte va a y = 105 (el 37 % del alto).
 *
 * - **Marco (izquierda y arriba):** la carreta en reposo, de ruedas altas, en tres cuartos, cortada por la izquierda,
 *   cargada; las varas apoyadas en el suelo se alejan hacia el fondo. Atada a ella sube una vara larga hasta el borde
 *   de arriba, y de su punta baja una cuerda de crin que cruza el cielo hasta el techo de la tienda de Bat: de ella
 *   cuelga lo del día ([EscenaMongol.colgado]).
 * - **Derecha, cerca:** la tienda de Bat, cortada por la derecha, con la puerta abierta hacia la izquierda; junto a la
 *   puerta, el odre grande en su armazón, con el palo de batir y los cubos.
 * - **Diagonal grande:** la cuerda larga de las yeguas, de una estaca cercana hacia el fondo, con las yeguas y sus
 *   potros, cada vez más chicos.
 * - **Contradiagonal:** el río ancho y lento, con bancos de arena, que entra por la derecha, lejos, y cruza en ese hacia
 *   la izquierda.
 * - **Plano medio:** las otras tiendas, escalonadas a la izquierda del pasillo; el fuego del campamento.
 * - **Lejos:** la estepa verde en lomas, las manadas como puntos, el humo de otro campamento en un recodo, las montañas
 *   azules y, sobre las lomas de la izquierda, el cerro del valle de invierno con su ovoo **[Aproximación]**: el cuento
 *   dice que desde el ovoo se ven los dos sitios, no a qué distancia está.
 *
 * Como [ValleMongol], deja **el encaje**; las funciones van por planos ([lejos], [rio], [llano], [campamento],
 * [primerPlano], [marco]) para que otro las siga.
 */
internal object VeranoMongol {
    private val C = ColorMongol
    const val H = 105f
    val MIRADA = Mirada(H, 560f)

    const val PISO_GATO = 272f

    /** La carreta del marco: la rueda de delante, de unas 95 unidades, con el cubo hacia (70, 215). */
    val SITIO_CARRETA = Sitio(MIRADA, Offset(41f, 253.5f), 56f, 2.6f)

    /** La tienda de Bat, cortada por la derecha; la puerta mira a la izquierda y hacia quien mira. */
    val SITIO_BAT = Sitio(MIRADA, Offset(300f, 214f), 32f, -0.62f)

    /** El odre en su armazón, junto a la puerta. */
    val ODRE = Offset(224f, 244f)

    /** La estaca cercana de la cuerda de las yeguas y su otra punta, al fondo. */
    val ESTACA = Offset(150f, 224f)
    val FONDO_CUERDA = Offset(214f, 160f)

    /** La vara de la carreta: su pie, atado a la carreta, y su punta, fuera del cuadro por arriba. */
    val VARA_PIE = Offset(112f, 174f)
    val VARA_PUNTA = Offset(121f, -8f)

    /** El fieltro doblado donde se deja el objeto del día. */
    val FIELTRO_OBJETO = Offset(196f, 258f)

    /** El fuego del campamento, en el pasillo, detrás de las yeguas. */
    val FUEGO = Offset(176f, 176f)

    fun seMueve(m: Momento): Boolean = true

    private fun hierba(m: Momento): Color = lerp(lerp(C.ESTEPA, C.VERDE, (EscenaMongol.pasto(m) * 1.3f).coerceIn(0f, 1f)), C.ESTEPA, if (m.ficha.dia.substring(5) >= "09-01") 0.35f else 0f)

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaMongol.luces(m); val r = Rayo(l); val sol = Sol3(r); val p = Pincel(l, r)
        lejos(m, p, sol)
        llano(m, p, sol)
        rio(m, p, sol)
        campamento(m, p, sol)
        primerPlano(m, p, sol)
        marco(m, p, sol)
        tiempoQuieto(l, m.ficha.clima, H, m.semilla("tiempo"))
        grano(Azar(Semilla.de("mongol", "grano", "verano")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- Lejos

    private fun DrawScope.lejos(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val semilla = m.semilla("cielo")
        cielo(l, H, semilla); astros(l, H, semilla); nubes(l, H, m.ficha.clima, semilla)
        val fijo = Azar(Semilla.de("mongol", "verano", "montes")); val verde = hierba(m)
        // Las montañas azules, lejísimos, y el cerro del valle de invierno, a la izquierda, con su ovoo.
        monte(l, perfil(fijo, -20f, 400f, H - 2f, 13f, 0.6f, 30), H + 2f, C.MONTE, 0.9f)
        val cerro = listOf(Offset(-10f, H + 1f), Offset(6f, H - 6f), Offset(20f, H - 11f), Offset(34f, H - 14f), Offset(44f, H - 15.4f), Offset(54f, H - 13.6f), Offset(70f, H - 8f), Offset(88f, H - 3f), Offset(104f, H + 1f))
        monte(l, cerro, H + 3f, lerp(verde, C.MONTE, 0.45f), 0.72f)
        ovoo(p, Offset(44f, H - 15f), 4.4f, EscenaMongol.cintasDelOvoo(m), Azar(Semilla.de("mongol", "ovoo")), 0.7f, 0f, m.viento, EscenaMongol.diaDelOvoo(m))
        // La estepa en lomas que se enciman, cada una más cerca, más verde y más nítida.
        for (j in 0 until 4) {
            val lejosJ = 0.66f - 0.13f * j
            val cresta = perfil(Azar(Semilla.de("mongol", "verano", "loma", j)), -20f, 400f, H + 1.6f + 3.4f * j, 2.6f + 1.4f * j, 0.25f, 14)
            monte(l, cresta, H + 30f, lerp(verde, if (j % 2 == 0) C.ESTEPA else C.MONTE, 0.3f - 0.06f * j), lejosJ)
            drawPath(curva(cresta), p.luz(lerp(verde, C.ESTEPA, 0.5f), lejosJ).copy(alpha = 0.45f), style = Stroke(0.6f))
        }
        // Las manadas de los otros campamentos: puntos con su sombra.
        val az = Azar(Semilla.de("mongol", "verano", "manadas", m.ficha.dia.substring(5, 7)))
        for (g in 0 until 3) { val c = Offset(az.entre(150f, 370f), az.entre(H + 4f, H + 12f)); repeat(5 + az.entero(6)) { val q = c + Offset(az.entre(-9f, 9f), az.entre(-1.2f, 1.2f)); sombraCorta(sol.r, q, 1.2f, 1f); drawOval(p.por(if (it % 3 == 0) C.FIELTRO else C.CUERO, 0.2f, 0.5f), Offset(q.x - 0.9f, q.y - 1.3f), Size(1.8f, 1.2f)) } }
        // Otro campamento, en un recodo del río, lejos: dos tiendas y su humo (el humo va en la parte viva).
        for ((j, x) in listOf(330f, 337f).withIndex()) { val g = Ger(Sitio(MIRADA, Offset(x, H + 6f + j * 0.6f), 1.3f, 0.2f), 950L + j, 0.5f, lejos = 0.6f, detalle = false); sombraCorta(sol.r, Offset(x, H + 6f + j * 0.6f), 3.6f, 2f); ger(g, p, sol) }
    }

    // ---------------------------------------------------------------- El llano

    private fun DrawScope.llano(m: Momento, p: Pincel, sol: Sol3) {
        val az = Azar(Semilla.de("mongol", "verano", "llano")); val verde = hierba(m)
        val forma = Path().apply { moveTo(0f, H + 10f); lineTo(ANCHO, H + 8f); lineTo(ANCHO, ALTO); lineTo(0f, ALTO); close() }
        drawPath(forma, Brush.verticalGradient(0f to p.por(verde, 0.35f, 0.45f), 0.35f to p.por(verde, 0.3f, 0.2f), 1f to p.por(lerp(verde, C.TIERRA, 0.15f), 0.1f), startY = H + 8f, endY = ALTO))
        clipPath(forma) {
            // Las rachas del viento en la hierba alta: lomos claros que la vuelven del revés, hondonadas oscuras.
            repeat(30) { val c = Offset(az.entre(0f, ANCHO), az.entre(H + 12f, 285f)); val k = ((c.y - H) / 180f).coerceIn(0f, 1f); mancha(c, az.entre(18f, 60f) * (0.5f + k), 1.2f + 4f * k, if (it % 2 == 0) p.luz(lerp(verde, C.ESTEPA, 0.4f), 0.3f * (1f - k)) else p.por(lerp(verde, C.MONTE, 0.2f), -0.7f, 0.3f * (1f - k)), 0.3f) }
            // La senda pisada, de la puerta hacia el río.
            val senda = listOf(Offset(250f, 250f), Offset(232f, 226f), Offset(214f, 204f), Offset(206f, 186f), Offset(202f, 170f), Offset(206f, 158f))
            for (i in 0 until senda.size - 1) drawLine(p.por(lerp(verde, C.TIERRA, 0.55f), 0.2f).copy(alpha = 0.55f), senda[i], senda[i + 1], 14f - i * 2.2f, StrokeCap.Round)
            // Las matas de hierba: más altas y más juntas cerca, vencidas hacia donde sopla; las flores, en manchones.
            val tumba = 2f + 5f * m.viento; val flores = EscenaMongol.flores(m)
            val amarillas = ArrayList<Offset>(); val moradas = ArrayList<Offset>()
            for (i in 0 until 1300) {
                val u = az.real(); val q = Offset(az.entre(-6f, ANCHO + 6f), H + 10f + (ALTO - H - 8f) * u * (0.35f + 0.65f * u))
                val k = ((q.y - H) / (ALTO - H)).coerceIn(0f, 1f); val h = (1f + 9f * k) * az.entre(0.6f, 1.4f)
                val tipo = az.entero(4); val base = when (tipo) { 0 -> lerp(verde, C.ESTEPA, 0.45f); 1 -> lerp(verde, C.MONTE, 0.2f); else -> verde }
                val n = 3 + az.entero(4)
                for (j in 0 until n) { val t = j / (n - 1f) - 0.5f; drawLine(if (j % 3 == 0) p.luz(lerp(base, C.FIELTRO, 0.15f), 0.3f * (1f - k)) else if (j % 3 == 1) p.por(base, -0.7f, 0.3f * (1f - k)) else p.medio(base, 0.3f * (1f - k)), Offset(q.x + t * 1.5f * k, q.y), Offset(q.x + t * 3f * (0.3f + k) + tumba * k * az.entre(0.4f, 1.2f), q.y - h * az.entre(0.6f, 1.1f)), 0.35f + 0.55f * k, StrokeCap.Round) }
                val flor = az.entero(14); if (flor == 0 && az.real() < flores) amarillas += q + Offset(tumba * k, -h) else if (flor == 1 && az.real() < flores) moradas += q + Offset(tumba * k, -h * 0.8f)
            }
            for ((lista, tinta) in listOf(amarillas to C.ORO, moradas to lerp(C.SOMBRA, C.ROJO, 0.3f))) { drawPoints(lista.filter { it.y < 190f }, PointMode.Points, p.luz(tinta, 0.2f), 1.1f, StrokeCap.Round); drawPoints(lista.filter { it.y >= 190f }, PointMode.Points, p.luz(tinta), 2f, StrokeCap.Round) }
        }
    }

    // ---------------------------------------------------------------- El río

    /** La línea del medio del río y su ancho: entra por la derecha, lejos, pasa detrás de la tienda y cruza en ese hacia la izquierda. */
    private val RIO = listOf(
        Offset(392f, 113f) to 3f, Offset(360f, 115f) to 4f, Offset(320f, 117.5f) to 5f, Offset(280f, 121f) to 6.5f, Offset(244f, 126f) to 8f, Offset(214f, 132f) to 10f,
        Offset(196f, 139f) to 12f, Offset(200f, 145f) to 13f, Offset(186f, 150f) to 14f, Offset(150f, 152f) to 15f, Offset(110f, 150f) to 15f, Offset(70f, 146f) to 14f, Offset(30f, 143f) to 13f, Offset(-12f, 141f) to 12f,
    )

    private fun DrawScope.rio(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val az = Azar(Semilla.de("mongol", "verano", "rio")); val verde = hierba(m)
        val izq = ArrayList<Offset>(); val der = ArrayList<Offset>()
        for ((i, par) in RIO.withIndex()) {
            val (c, w) = par; val a = RIO[maxOf(i - 1, 0)].first; val b = RIO[minOf(i + 1, RIO.size - 1)].first
            val d = b - a; val n = Offset(-d.y, d.x) / d.getDistance().coerceAtLeast(0.01f); val nn = Offset(n.x * 0.5f, n.y)
            izq += c + nn * (w / 2f); der += c - nn * (w / 2f)
        }
        val agua = poligono(izq + der.reversed())
        // La orilla: los bancos de arena y de piedras (la franja de paso entre la hierba y el agua).
        val arena = poligono(izq.mapIndexed { i, q -> q + Offset(0f, -1.4f - RIO[i].second * 0.12f) } + der.reversed().mapIndexed { i, q -> q + Offset(0f, 2f + RIO[RIO.size - 1 - i].second * 0.2f) })
        drawPath(arena, p.por(lerp(C.ESTEPA, C.FIELTRO, 0.3f), 0.3f, 0.3f))
        clipPath(arena) { repeat(40) { val q = Offset(az.entre(-10f, 390f), az.entre(H + 6f, 160f)); drawOval(p.por(C.PIEDRA, if (it % 2 == 0) 0.6f else -0.5f, 0.3f), Offset(q.x - 1f, q.y - 0.4f), Size(2f, 0.8f)) } }
        // El agua: refleja el cielo; honda en el medio, clara en las orillas.
        val hondo = lerp(C.RIO, C.RIO_HONDO, 0.3f)
        drawPath(agua, Brush.verticalGradient(0f to lerp(l.bajo, p.medio(C.RIO, 0.5f), 0.35f), 0.5f to lerp(l.medio, p.medio(hondo, 0.2f), 0.55f), 1f to lerp(l.alto, p.medio(C.RIO, 0.1f), 0.6f), startY = H + 6f, endY = 162f))
        clipPath(agua) {
            // Las nubes y la orilla de allá, reflejadas; los bancos de arena que asoman en medio; rizos lentos.
            for (i in 0 until RIO.size - 1) reflejoRoto(izq[i].x.coerceAtMost(izq[i + 1].x), izq[i].x.coerceAtLeast(izq[i + 1].x), minOf(izq[i].y, izq[i + 1].y), RIO[i].second * 0.3f, p.por(lerp(verde, C.TIERRA, 0.3f), -0.4f, 0.2f), 0.5f, az)
            for ((c, w) in listOf(Offset(160f, 152f) to 18f, Offset(260f, 123f) to 9f)) { drawOval(p.por(lerp(C.ESTEPA, C.FIELTRO, 0.4f), 0.4f, 0.15f), Offset(c.x - w, c.y - 1.2f), Size(w * 2f, 2.8f)); drawOval(p.por(C.TIERRA, -0.5f, 0.15f).copy(alpha = 0.5f), Offset(c.x - w * 0.9f, c.y + 0.6f), Size(w * 1.8f, 1.2f)) }
            repeat(70) { val q = Offset(az.entre(-10f, 400f), az.entre(H + 6f, 162f)); val k = ((q.y - H) / 57f).coerceIn(0f, 1f); drawLine(lerp(l.bajo, Color.White, 0.35f).copy(alpha = 0.3f), q, q + Offset(2f + 7f * k, 0f), 0.4f + 0.4f * k, StrokeCap.Round) }
            estela(l, Rect(-10f, H + 6f, 400f, 162f), m.azar("estela"))
        }
        drawPath(curva(der), p.luz(lerp(C.ESTEPA, C.FIELTRO, 0.5f), 0.2f).copy(alpha = 0.6f), style = Stroke(0.7f))
    }

    // ---------------------------------------------------------------- El campamento (el plano medio)

    private class Puesto(val tienda: Tienda, val pie: Offset, val k: Float, val giro: Float, val desgaste: Float)

    /** Las otras tiendas, escalonadas a la izquierda del pasillo; la de Temür, desde que vuelve, junto a la de Bat. */
    private val PUESTOS = listOf(
        Puesto(Tienda.CHAMAN, Offset(30f, 152f), 3.6f, 0.4f, 0.8f),
        Puesto(Tienda.VECINO, Offset(70f, 156f), 5f, 0.5f, 0.6f),
        Puesto(Tienda.MADRE_BOLAD, Offset(112f, 163f), 7.4f, 0.55f, 0.3f),
        Puesto(Tienda.BOLAD, Offset(150f, 166f), 6.4f, 0.2f, 0.5f),
        Puesto(Tienda.MADRE, Offset(192f, 175f), 9f, 0.35f, 0.7f),
        Puesto(Tienda.TEMUR, Offset(250f, 166f), 7f, -0.1f, 0.35f),
    )

    private fun gerDe(m: Momento, u: Puesto, armado: Float): Ger = Ger(Sitio(MIRADA, u.pie, u.k, u.giro), Semilla.de("mongol", "tienda", u.tienda.name), u.desgaste,
        if (EscenaMongol.madreEnferma(m) && u.tienda == Tienda.MADRE) EscenaMongol.Puerta.CERRADA else EscenaMongol.Puerta.ABIERTA,
        EscenaMongol.fuegoDentro(m), 0f, armado, lejos = 0.1f + (176f - u.pie.y) / 120f, detalle = u.k > 7f,
        radio = if (u.tienda == Tienda.MADRE_BOLAD) 3.4f else if (u.tienda == Tienda.CHAMAN) 2.3f else 3f)

    private fun DrawScope.campamento(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val hay = EscenaMongol.tiendas(m).toSet(); val armado = EscenaMongol.armado(m)
        val az = Azar(Semilla.de("mongol", "verano", "campamento"))
        val tiendas = PUESTOS.filter { it.tienda in hay }.map { gerDe(m, it, armado) }
        val otraCarreta = Carreta(Sitio(MIRADA, Offset(92f, 176f), 7f, 1.9f), Semilla.de("mongol", "carreta", "medio"), EscenaMongol.carretasCargadas(m), lejos = 0.15f, detalle = false)
        // El rebaño, de noche, junto a las tiendas; de día pace lejos, en el llano junto al río.
        val cerca = EscenaMongol.ovejasCerca(m); val todas = EscenaMongol.ovejas(m)
        val lejanas = (todas * (1f - cerca)).toInt(); val juntas = todas - lejanas
        capaSombra(sol.r) { c -> for (g in tiendas) sombra(PiezasMongol.sombraDeGer(g, sol), c); sombraDeCarreta(otraCarreta, sol, c) }
        rebano(p, (0 until lejanas).map { Offset(az.entre(230f, 380f), az.entre(H + 14f, H + 22f)) }, { y -> 2f + (y - H) * 0.08f }, az, { 0.4f }, pastan = 0.8f)
        carreta(otraCarreta, p, sol)
        for (g in tiendas.sortedBy { it.s.pie.y }) ger(g, p, sol)
        rebano(p, (0 until juntas).map { Offset(az.entre(40f, 140f), az.entre(172f, 186f)) }, { y -> 4f + (y - 170f) * 0.2f }, az, { 0.1f }, pastan = 0.1f)
        // El fuego del campamento, con su corro de piedras.
        val f = EscenaMongol.fuegoFuera(m)
        hogar(p, FUEGO, 16f, f, Azar(Semilla.de("mongol", "fuego", "verano")), 0.1f, olla = false)
        if (f > 0.05f) resplandor(FUEGO + Offset(0f, -3f), 40f, 0.6f * l.noche * f, C.LUMBRE)
    }

    // ---------------------------------------------------------------- El primer plano

    private fun DrawScope.primerPlano(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val az = Azar(Semilla.de("mongol", "verano", "cerca")); val verde = hierba(m)
        val bat = Ger(SITIO_BAT, Semilla.de("mongol", "tienda", "BAT"), 0.35f, EscenaMongol.puerta(m), EscenaMongol.fuegoDentro(m), 0f, EscenaMongol.armado(m))
        val carreta = Carreta(SITIO_CARRETA, Semilla.de("mongol", "carreta", "verano"), EscenaMongol.carretasCargadas(m) || !EscenaMongol.partida(m), EscenaMongol.cuna(m))
        // La hierba pisada del pasillo, con flores amarillas y moradas en los bordes.
        val pisada = lerp(verde, C.TIERRA, 0.35f)
        mancha(Offset(200f, 262f), 120f, 26f, p.por(pisada, 0.1f), 0.6f)
        // La yeguada, en la cuerda larga: de la estaca cercana hacia el fondo, cada vez más chica.
        val yeguas = EscenaMongol.yeguasEnLaCuerda(m)
        val puestos = listOf(0.12f, 0.38f, 0.6f, 0.8f)
        val potros = listOf(0.24f, 0.5f, 0.7f, 0.9f)
        fun en(t: Float) = ESTACA + (FONDO_CUERDA - ESTACA) * t
        fun talla(t: Float) = 36f * (1f - 0.6f * t)
        fun yegua(t: Float) = en(t) + Offset(-12f * (1f - t) - 3f, 3f * (1f - t))
        fun potro(t: Float) = en(t) + Offset(5f * (1f - t) + 2f, 1f)
        val bichos = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        val pelos = listOf(PiezasMongol.BAYO, PiezasMongol.TORDO, PiezasMongol.ISABELINO, PiezasMongol.ALAZAN)
        if (yeguas) for ((j, t) in puestos.withIndex()) { val q = yegua(t); bichos += parte(q.y) { caballo(p, q, talla(t), if (j % 2 == 0) -1f else 1f, pelos[j], pasta = j == 1) } }
        for ((j, t) in potros.withIndex()) { val q = potro(t); bichos += parte(q.y) { caballo(p, q, talla(t) * 0.6f, if (j % 2 == 0) 1f else -1f, if (j == 0 && EscenaMongol.potroBlanco(m)) PiezasMongol.BLANCO else pelos[(j + 2) % 4], potro = true) } }
        capaSombra(sol.r) { c ->
            sombra(PiezasMongol.sombraDeGer(bat, sol), c)
            sombraDeCarreta(carreta, sol, c)
            if (yeguas) for (t in puestos) { val q = yegua(t); sombraArrojada(sol.r, PiezasMongol.siluetaDeCaballo(q, talla(t), 1f), q, c, 150f, 150f) }
            if (EscenaMongol.odre(m)) sombraArrojada(sol.r, PiezasMongol.siluetaDeOdre(ODRE, 54f), ODRE, c, 150f, 150f)
        }
        luzDePuerta(bat, maxOf(l.noche - 0.1f, 0f) * maxOf(bat.dentro, 0.6f), 5f)
        // La cuerda de las yeguas: de estaca en estaca, baja, con sus nudos.
        val linea = cuerda(ESTACA + Offset(0f, -6f), FONDO_CUERDA + Offset(0f, -3f), 2f, 16)
        drawPath(Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }, p.medio(C.CRIN), style = Stroke(0.9f))
        for (t in listOf(0f, 0.5f)) { val q = en(t); drawLine(p.por(C.MADERA, 0.4f), q, q + Offset(0f, -10f * (1f - 0.6f * t)), 2f * (1f - 0.5f * t), StrokeCap.Round) }
        for ((_, pintar) in bichos.sortedBy { it.first }) pintar()
        // La tienda de Bat y el odre junto a su puerta.
        ger(bat, p, sol)
        if (EscenaMongol.odre(m)) odre(p.con((0.5f * l.noche * bat.dentro).coerceIn(0f, 1f)), ODRE, 54f, Azar(Semilla.de("mongol", "odre")))
        // El fieltro doblado donde se deja el objeto del día.
        // (un fieltro de sentarse, viejo, con su ribete, una esquina doblada y la hierba que lo levanta por debajo)
        val o = FIELTRO_OBJETO
        drawOval(p.sombra(C.TIERRA).copy(alpha = 0.35f), Offset(o.x - 22f, o.y - 2f), Size(46f, 8f))
        val fieltro = Path().apply { moveTo(o + Offset(-19f, 2f)); cubicTo(o + Offset(-8f, 3.6f), o + Offset(4f, 2.4f), o + Offset(15f, 3.4f)); lineTo(o + Offset(21f, -4.4f)); cubicTo(o + Offset(10f, -6.4f), o + Offset(-2f, -5f), o + Offset(-11f, -6.4f)); close() }
        drawPath(fieltro, Brush.verticalGradient(listOf(p.luz(lerp(C.FIELTRO, C.ESTEPA, 0.3f)), p.por(lerp(C.FIELTRO, C.ESTEPA, 0.3f), -0.2f)), o.y - 6f, o.y + 3f))
        drawPath(fieltro, p.por(C.ROJO, 0.2f), style = Stroke(1.1f))
        drawPath(poligono(o + Offset(15f, 3.4f), o + Offset(21f, -4.4f), o + Offset(13f, -1.6f)), p.por(lerp(C.FIELTRO, C.TIERRA, 0.4f), -0.6f))
        for (k in 0..4) drawLine(p.sombra(lerp(C.FIELTRO, C.ESTEPA, 0.3f)).copy(alpha = 0.35f), o + Offset(-14f + k * 6f, -4.6f), o + Offset(-15f + k * 6.4f, 2.2f), 0.4f)
        // La carreta del marco.
        carreta(carreta, p.con((0.4f * l.noche * bat.dentro).coerceIn(0f, 1f)), sol)
        // Las matas altas de delante, con flores.
        val flores = EscenaMongol.flores(m)
        for ((x0, x1) in listOf(-4f to 60f, 300f to 384f)) {
            var x = x0
            while (x < x1) {
                val q = Offset(x, ALTO + 1f); val h = az.entre(8f, 18f)
                drawLine(if (az.entero(3) == 0) p.luz(lerp(verde, C.ESTEPA, 0.3f)) else p.por(verde, -0.6f), q, q + Offset(3f + az.entre(-2f, 2f), -h), 1f, StrokeCap.Round)
                if (az.real() < flores * 0.3f) drawCircle(p.luz(if (az.entero(2) == 0) C.ORO else lerp(C.SOMBRA, C.ROJO, 0.3f)), 1.3f, q + Offset(3f, -h))
                x += az.entre(1.6f, 3.4f)
            }
        }
    }

    // ---------------------------------------------------------------- El marco: la vara de la carreta y la cuerda con lo colgado

    private fun DrawScope.marco(m: Momento, p: Pincel, sol: Sol3) {
        val madera = lerp(C.MADERA, C.PIEDRA, 0.2f)
        drawLine(p.sombra(madera), VARA_PIE, VARA_PUNTA, 3.4f, StrokeCap.Round)
        drawLine(p.luz(lerp(madera, C.FIELTRO, 0.2f)), VARA_PIE + Offset(if (p.solALaDerecha) 1f else -1f, 0f), VARA_PUNTA + Offset(if (p.solALaDerecha) 0.6f else -0.6f, 0f), 1.1f, StrokeCap.Round)
        // Las vueltas de crin con que se ata la vara a la carreta.
        for (j in 0..3) { val c = VARA_PIE + (VARA_PUNTA - VARA_PIE) * (0.04f + j * 0.02f); drawLine(p.medio(C.CRIN), c + Offset(-2.6f, 0f), c + Offset(2.6f, 0.8f), 1.2f) }
        // La cuerda de crin que cruza el cielo, de la punta de la vara al techo de la tienda de Bat.
        val desde = VARA_PIE + (VARA_PUNTA - VARA_PIE) * 0.94f; val hasta = Offset(268f, 140f)
        val linea = cuerda(desde, hasta, 24f, 30)
        drawPath(Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }, p.medio(C.CRIN), style = Stroke(1f, cap = StrokeCap.Round))
        // Lo que cuelga, según el día.
        val az = Azar(Semilla.de("mongol", "colgado"))
        when (EscenaMongol.colgado(m)) {
            Colgado.CUAJADA -> for (i in 4 until linea.size - 3 step 3) { val q = linea[i]; val w = 2.2f + az.entre(0f, 1.4f); val h = 5f + az.entre(0f, 3f)
                drawLine(p.medio(C.CRIN), q, q + Offset(0f, 2f), 0.5f)
                drawPath(Path().apply { moveTo(q + Offset(-w * 0.4f, 2f)); cubicTo(q + Offset(-w * 1.2f, 2f + h * 0.4f), q + Offset(-w, 2f + h), q + Offset(0f, 2f + h)); cubicTo(q + Offset(w, 2f + h), q + Offset(w * 1.2f, 2f + h * 0.4f), q + Offset(w * 0.4f, 2f)); close() },
                    Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.FIELTRO), p.luz(C.FIELTRO)) else listOf(p.luz(C.FIELTRO), p.sombra(C.FIELTRO)), q.x - w, q.x + w)) }
            Colgado.LANA -> for (i in 3 until linea.size - 3 step 3) { val q = linea[i]; mancha(q + Offset(0f, 4f), 5f, 4f, p.luz(C.LANA), 0.95f); mancha(q + Offset(1f, 6f), 4f, 3f, p.sombra(C.LANA), 0.5f) }
            Colgado.FIELTRO -> for (i in 3 until linea.size - 4 step 5) { val q = linea[i]; drawRect(Brush.horizontalGradient(listOf(p.luz(C.FIELTRO), p.por(C.FIELTRO, -0.4f)), q.x - 4f, q.x + 4f), Offset(q.x - 4f, q.y), Size(8f, 14f)) }
            Colgado.CINTAS -> for (i in 2 until linea.size - 2 step 2) { val q = linea[i]; drawLine(p.por(if (i % 3 == 0) C.FIELTRO else C.AZUL, 0.3f), q, q + Offset(2f * m.viento + 1f, 8f), 1.4f, StrokeCap.Round) }
            Colgado.NADA -> Unit
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = EscenaMongol.luces(m)
        val bat = Ger(SITIO_BAT, 0L)
        humo(l, bat.humero, 70f, 7f, reloj, m.viento, 0.2f, 0f, 3, 9, 0.2f + 0.4f * EscenaMongol.fuegoDentro(m))
        for ((j, x) in listOf(330f, 337f).withIndex()) humo(l, Offset(x, H + 3f), 18f, 1.4f, reloj, m.viento, 0.2f, 0.6f, j + 20, 5, 0.3f)
        val f = EscenaMongol.fuegoFuera(m)
        if (f > 0.15f) { for ((dx, a) in listOf(-2f to 6f, 2f to 8f)) llama(FUEGO + Offset(dx, -1f), a * f, reloj, dx.toInt() + 30); humo(l, FUEGO + Offset(0f, -10f), 50f, 4f, reloj, m.viento, 0.15f, 0.1f, 8, 7, 0.35f * f) }
        else brasas(l, FUEGO, 6f, reloj)
        if (l.noche < 0.5f) destellos(l, Rect(0f, H + 8f, 400f, 160f), reloj, m.semilla("rio"), 18)
        tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    fun luzDelGato(m: Momento, en: Offset): LuzGato { val l = EscenaMongol.luces(m); return LuzGato(Rayo(l), l.noche) { l.tono(it) } }

    fun estorbos(m: Momento): List<Float> = emptyList()

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 130f, PISO_GATO, hasta = 296f))
        add(Ancla(TipoAncla.MIRADOR, 92f, 198f))                                 // en la plataforma de la carreta
        add(Ancla(TipoAncla.OBJETO, FIELTRO_OBJETO.x, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 140f, PISO_GATO))                            // a la sombra de la carreta
    }
}
