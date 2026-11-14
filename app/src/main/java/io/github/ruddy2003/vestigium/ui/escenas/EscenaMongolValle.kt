package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Tienda
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Carreta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Corral
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.argal
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.caballo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.caballoLejano
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.carreta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.corral
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.hogar
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.luzDePuerta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.ovoo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.rebano
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.sombraDeCarreta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.seMueve
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.sin

/**
 * El valle de invierno de Bat: el primer cuadro de Mongol (`composicion.md` de la tanda S12, cuadro A). Un sitio
 * cerrado y al abrigo, mirado de pie junto a la tienda de Bat, valle adentro. El horizonte va a y = 118 y solo se ve en
 * la mitad derecha.
 *
 * - **Marco (izquierda y arriba):** la tienda de Bat, grande y muy cerca, en tres cuartos, cortada por el borde
 *   izquierdo; la puerta mira a la derecha y hacia quien mira. La pértiga del lazo, apoyada en el alero, cruza en
 *   diagonal hasta el borde de arriba. El humo del aro entra por arriba (parte viva).
 * - **Diagonal grande:** la ladera del cerro, que baja de detrás de la tienda hacia el centro derecha, con el ovoo en
 *   la cima, recortado contra el cielo.
 * - **Contradiagonal:** el río, helado la mitad del año, que viene del fondo, hace un meandro y sale por la derecha.
 * - **Plano medio:** las otras tiendas, escalonadas al pie del cerro; detrás y a la derecha, el corral con las ovejas
 *   apretadas, los caballos y una carreta parada. Lo separa del primer plano un talud bajo **[Diseño propio]**: el
 *   campamento está un poco más abajo que la tienda de Bat, y por eso se ven los techos desde arriba.
 * - **Lejos:** al otro lado del río, la estepa en lomas que se enciman, los humos de otros campamentos y las montañas.
 * - **Primer plano:** la esquina del corral de piedra y estiércol, cortada por la derecha, con su cancilla y el montón
 *   de argal arrimado; el suelo pisado de delante de la puerta; el hogar de fuera con la olla grande **[Diseño propio]**.
 *
 * Este archivo deja **el encaje**: los planos, las masas y las piezas en su sitio, de día y de noche. Las funciones van
 * por planos ([lejos], [cerro], [fondoDelValle], [rio], [campamento], [primerPlano], [marco]) para que se puedan
 * seguir afinando una a una.
 */
internal object ValleMongol {
    private val C = ColorMongol

    /** El horizonte (solo asoma a la derecha del cerro). */
    const val H = 118f
    val MIRADA = Mirada(H, 560f)

    /** El suelo del gato: delante de la puerta, por delante del hogar y del corral. */
    const val PISO_GATO = 270f

    /** La tienda de Bat: muy cerca, cortada por la izquierda; la puerta mira a la derecha y hacia quien mira. */
    val SITIO_BAT = Sitio(MIRADA, Offset(-66f, 234f), 72f, 1.16f)

    /** El hogar de fuera, retirado del paso del gato. */
    val HOGAR = Offset(214f, 243f)

    /** Donde se pone el objeto del día: sobre un cofre bajo, junto a la puerta. */
    val COFRE = Offset(170f, 252f)

    /** La cresta del cerro, puesta a mano: sube de detrás de la tienda a la cima del ovoo y baja larga hacia el río. */
    private val CRESTA = listOf(
        Offset(-6f, 72f), Offset(16f, 68f), Offset(40f, 66.5f), Offset(62f, 63f), Offset(86f, 63.6f), Offset(108f, 60.5f), Offset(130f, 61f), Offset(150f, 58.4f),
        Offset(168f, 60f), Offset(180f, 59f), Offset(190f, 58.6f), Offset(200f, 60.2f), Offset(212f, 64f), Offset(222f, 69.5f), Offset(236f, 74f), Offset(248f, 81f),
        Offset(258f, 84.5f), Offset(270f, 91f), Offset(284f, 96f), Offset(296f, 102.5f), Offset(306f, 105f), Offset(318f, 110.5f), Offset(330f, 113.6f), Offset(344f, 117f), Offset(356f, 118.4f),
    )

    /** El pie del cerro: donde la ladera se encuentra con el fondo del valle (y donde se escalonan las tiendas). */
    private val PIE = listOf(
        Offset(120f, 214f), Offset(150f, 204f), Offset(172f, 192f), Offset(196f, 181f), Offset(214f, 170f), Offset(236f, 160f), Offset(254f, 151f), Offset(272f, 143f),
        Offset(290f, 135f), Offset(306f, 128.5f), Offset(322f, 123f), Offset(338f, 119.5f), Offset(356f, 118.4f),
    )

    /** El río: la línea del medio, del horizonte al borde derecho, con su meandro; y su ancho en cada punto. */
    private val RIO = listOf(
        Offset(318f, 118.8f) to 1.4f, Offset(330f, 121.5f) to 2.5f, Offset(338f, 125f) to 3.5f, Offset(328f, 129.5f) to 5f, Offset(312f, 134f) to 6.5f, Offset(304f, 140f) to 8f,
        Offset(314f, 148f) to 10f, Offset(334f, 157f) to 13f, Offset(352f, 166f) to 17f, Offset(368f, 176f) to 22f, Offset(384f, 186f) to 27f, Offset(400f, 196f) to 31f,
    )

    fun seMueve(m: Momento): Boolean = true                                    // el humo de las tiendas no para

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaMongol.luces(m); val r = Rayo(l); val sol = Sol3(r); val p = Pincel(l, r)
        val nieve = EscenaMongol.nieve(m)
        lejos(m, p, sol, nieve)
        fondoDelValle(m, p, sol, nieve)
        rio(m, p, sol, nieve)
        cerro(m, p, sol, nieve)
        campamento(m, p, sol, nieve)
        primerPlano(m, p, sol, nieve)
        marco(m, p, sol, nieve)
        tiempoQuieto(l, m.ficha.clima, H, m.semilla("tiempo"))
        grano(Azar(Semilla.de("mongol", "grano", "valle")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- Lejos: el cielo, las montañas y la estepa del otro lado del río

    private fun DrawScope.lejos(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val l = p.l; val semilla = m.semilla("cielo")
        cielo(l, H, semilla); astros(l, H, semilla); nubes(l, H, m.ficha.clima, semilla)
        val fijo = Azar(Semilla.de("mongol", "valle", "montes"))
        // Las montañas azules, en dos filas, con su nieve.
        val alta = perfil(fijo, 180f, 400f, H - 3f, 20f, 0.7f, 26)
        monte(l, alta, H + 2f, C.MONTE, 0.86f)
        clipPath(curva(alta, H + 2f)) { for (q in alta) mancha(q + Offset(0f, 3f), 7f, 2.6f, p.luz(C.NIEVE, 0.7f), 0.55f) }
        monte(l, perfil(fijo, 200f, 400f, H - 1f, 11f, 0.45f, 20), H + 2f, lerp(C.MONTE, C.ESTEPA, 0.25f), 0.74f)
        // Las lomas de la estepa, al otro lado del río: cuatro que se enciman, cada una más cerca y más cálida.
        val estepa = lerp(C.ESTEPA, C.NIEVE, nieve * 0.85f)
        for (j in 0 until 4) {
            val lejosJ = 0.62f - 0.12f * j
            val cresta = perfil(Azar(Semilla.de("mongol", "valle", "loma", j)), 236f - 6f * j, 400f, H + 1f + 3.2f * j, 3.4f + 1.2f * j, 0.25f, 12)
            monte(l, cresta, H + 22f, lerp(estepa, if (j % 2 == 0) C.TIERRA else C.MONTE, 0.25f - 0.05f * j), lejosJ)
            drawPath(curva(cresta), p.luz(estepa, lejosJ).copy(alpha = 0.5f), style = Stroke(0.6f))
        }
        // Otro campamento lejos, en una loma: tres tiendas con su sombra corta (su humo va en la parte viva).
        val v = MIRADA
        for ((j, x) in listOf(352f, 360f, 367f).withIndex()) {
            val g = Ger(Sitio(v, Offset(x, H + 7.5f + j * 0.4f), 1.5f, 0.3f), 900L + j, 0.6f, lejos = 0.55f, detalle = false)
            sombraCorta(sol.r, Offset(x, H + 7.6f + j * 0.4f), 4f, 2f)
            ger(g, p, sol)
        }
    }

    // ---------------------------------------------------------------- El fondo del valle

    private fun DrawScope.fondoDelValle(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val l = p.l; val az = Azar(Semilla.de("mongol", "valle", "fondo"))
        val pasto = lerp(C.ESTEPA, C.TIERRA, 0.15f)
        val suelo = lerp(pasto, C.NIEVE, nieve)
        val forma = Path().apply { moveTo(0f, H + 18f); lineTo(ANCHO, H + 4f); lineTo(ANCHO, ALTO); lineTo(0f, ALTO); close() }
        drawPath(forma, Brush.verticalGradient(0f to p.por(suelo, 0.3f, 0.45f), 0.4f to p.por(suelo, 0.4f, 0.15f), 1f to p.por(suelo, 0.2f, 0f), startY = H, endY = ALTO))
        clipPath(forma) {
            // Las rachas del viento en la nieve o en la hierba seca: lomos claros y hondonadas en sombra.
            repeat(26) { val c = Offset(az.entre(150f, 400f), az.entre(H + 4f, 230f)); val k = ((c.y - H) / 110f).coerceIn(0f, 1f); mancha(c, az.entre(14f, 40f) * (0.5f + k), 1.2f + 3f * k, if (it % 2 == 0) p.por(suelo, -0.6f, 0.3f * (1f - k)) else p.luz(lerp(suelo, Color.White, 0.3f), 0.3f * (1f - k)), 0.32f) }
            if (nieve < 0.7f) repeat(160) { val q = Offset(az.entre(160f, 400f), az.entre(H + 6f, 230f)); val k = ((q.y - H) / 110f).coerceIn(0f, 1f); drawLine(p.por(lerp(C.ESTEPA, C.TIERRA, az.real() * 0.5f), if (it % 3 == 0) 0.6f else -0.4f, 0.4f * (1f - k)).copy(alpha = 1f - nieve), q, q + Offset(az.entre(-0.6f, 0.8f), -(0.8f + 2.6f * k)), 0.4f + 0.4f * k) }
        }
    }

    // ---------------------------------------------------------------- El río

    private fun bordes(): Pair<List<Offset>, List<Offset>> {
        val izq = ArrayList<Offset>(); val der = ArrayList<Offset>()
        for ((i, par) in RIO.withIndex()) {
            val (c, w) = par; val a = RIO[maxOf(i - 1, 0)].first; val b = RIO[minOf(i + 1, RIO.size - 1)].first
            val d = b - a; val n = Offset(-d.y, d.x) / d.getDistance().coerceAtLeast(0.01f)
            // La perspectiva aplasta el ancho: se ensancha más de lado que de alto.
            val nn = Offset(n.x, n.y * 0.62f)
            izq += c + nn * (w / 2f); der += c - nn * (w / 2f)
        }
        return izq to der
    }

    /** El río: helado (con ventisqueros, grietas y el agujero del agua con su cubo) o, en primavera y en otoño, abierto, con sus reflejos. */
    private fun DrawScope.rio(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val l = p.l; val az = Azar(Semilla.de("mongol", "valle", "rio")); val hielo = EscenaMongol.hielo(m)
        val (izq, der) = bordes()
        val forma = poligono(izq + der.reversed())
        // La orilla: una franja de grava y tierra oscura, más ancha que el agua.
        val orilla = poligono(izq.mapIndexed { i, q -> q + Offset(0f, -1.2f - RIO[i].second * 0.12f) } + der.reversed().mapIndexed { i, q -> q + Offset(0f, 1.5f + RIO[RIO.size - 1 - i].second * 0.14f) })
        drawPath(orilla, p.por(lerp(C.TIERRA, C.HOLLIN, 0.25f), -0.4f, 0.25f))
        val agua = lerp(C.RIO, C.HIELO, 0.25f)
        drawPath(forma, Brush.verticalGradient(0f to lerp(p.medio(C.RIO_HONDO, 0.6f), l.bajo, 0.4f), 0.5f to p.medio(C.RIO, 0.2f), 1f to p.medio(lerp(C.RIO, C.HIELO, 0.4f), 0f), startY = H, endY = 214f))
        clipPath(forma) {
            if (hielo < 1f) {
                // El agua abierta: refleja el cielo y la orilla; rizos tendidos.
                val k = 1f - hielo
                drawRect(Brush.verticalGradient(listOf(l.bajo.copy(alpha = 0.5f * k), l.medio.copy(alpha = 0.3f * k)), H, 214f), Offset(290f, H), Size(110f, 100f))
                repeat(60) { val q = Offset(az.entre(290f, 400f), az.entre(H + 2f, 214f)); val kk = ((q.y - H) / 96f); drawLine(lerp(l.bajo, Color.White, 0.3f).copy(alpha = 0.35f * k), q, q + Offset(2f + 8f * kk, 0f), 0.5f + 0.5f * kk, StrokeCap.Round) }
                reflejoRoto(300f, 400f, 150f, 30f, p.sombra(lerp(C.ESTEPA, C.TIERRA, 0.4f), 0.2f), 0.3f * k, az)
            }
            if (hielo > 0f) {
                // El hielo: claro, con su brillo, ventisqueros de nieve que corren con el río y grietas.
                val hielo2 = lerp(C.HIELO, C.RIO, 0.38f)
                drawPath(forma, Brush.verticalGradient(0f to lerp(p.luz(hielo2, 0.5f), l.bajo, 0.3f).copy(alpha = hielo), 0.5f to p.por(hielo2, 0.1f, 0.2f).copy(alpha = hielo), 1f to p.por(lerp(hielo2, C.RIO_HONDO, 0.2f), -0.2f).copy(alpha = hielo), startY = H, endY = 214f))
                // El brillo del cielo en el hielo liso, en bandas a lo largo del río.
                for (i in 3 until RIO.size - 1) { val c = (RIO[i].first + RIO[i + 1].first) / 2f; mancha(c + Offset(0f, -RIO[i].second * 0.1f), RIO[i].second * 0.6f, RIO[i].second * 0.07f + 0.5f, lerp(l.medio, Color.White, 0.3f), 0.35f * hielo) }
                repeat(18) { val i = az.entero(RIO.size - 1); val c = RIO[i].first + (RIO[i + 1].first - RIO[i].first) * az.real(); val w = RIO[i].second; mancha(c + Offset(az.entre(-w, w) * 0.4f, 0f), w * az.entre(0.4f, 0.9f), w * 0.08f + 0.6f, p.luz(C.NIEVE, 0.2f), 0.7f * hielo * (0.4f + 0.6f * nieve)) }
                repeat(14) { val i = az.entero(RIO.size - 1); val c = RIO[i].first + (RIO[i + 1].first - RIO[i].first) * az.real(); val w = RIO[i].second
                    val q = c + Offset(az.entre(-w, w) * 0.4f, az.entre(-1f, 1f)); drawPath(Path().apply { moveTo(q); lineTo(q + Offset(w * 0.2f, w * 0.03f)); lineTo(q + Offset(w * 0.3f, -w * 0.02f)); lineTo(q + Offset(w * 0.5f, w * 0.02f)) }, p.sombra(C.RIO_HONDO, 0.2f).copy(alpha = 0.5f * hielo), style = Stroke(0.35f + w * 0.015f)) }
                // El agujero del agua, con su cubo de cuero al lado, cerca de la orilla de acá.
                if (hielo > 0.6f) {
                    val c = Offset(346f, 168f)
                    drawOval(p.por(C.NIEVE, -0.5f), Offset(c.x - 8f, c.y - 2.4f), Size(16f, 5.4f))
                    drawOval(p.medio(C.RIO_HONDO), Offset(c.x - 6f, c.y - 1.6f), Size(12f, 3.6f))
                    drawOval(lerp(p.medio(C.RIO), l.bajo, 0.3f).copy(alpha = 0.6f), Offset(c.x - 4f, c.y - 1.2f), Size(7f, 1.4f))
                    val cubo = c + Offset(12f, 1.6f)
                    drawPath(poligono(cubo + Offset(-3f, -5.6f), cubo + Offset(3.2f, -5.6f), cubo + Offset(2.4f, 0f), cubo + Offset(-2.4f, 0f)), p.por(C.CUERO, if (p.solALaDerecha) 0.5f else -0.5f))
                    drawOval(p.medio(C.HOLLIN), Offset(cubo.x - 3.2f, cubo.y - 6.6f), Size(6.4f, 2f)); drawLine(p.medio(C.CRIN), cubo + Offset(-3f, -6f), cubo + Offset(0f, -10f), 0.5f); drawLine(p.medio(C.CRIN), cubo + Offset(3f, -6f), cubo + Offset(0f, -10f), 0.5f)
                }
            }
            // La luna o el sol bajo en el hielo y en el agua.
            estela(l, Rect(290f, H, 400f, 214f), m.azar("estela"))
        }
        // Las piedras y las matas secas de la orilla de acá (la franja de paso entre el río y el suelo).
        for (i in 4 until RIO.size) {
            val q = der[i]; val w = RIO[i].second
            repeat(3) { val c = q + Offset(az.entre(-w * 0.4f, w * 0.4f), az.entre(0.8f, 2.4f) + w * 0.06f); val rr = 0.6f + w * az.entre(0.03f, 0.07f)
                drawOval(p.por(C.PIEDRA, -0.4f, 0.1f), Offset(c.x - rr, c.y - rr * 0.5f), Size(rr * 2f, rr)); drawOval(p.por(lerp(C.PIEDRA, C.NIEVE, nieve * 0.7f), 0.6f, 0.1f), Offset(c.x - rr * 0.8f, c.y - rr * 0.62f), Size(rr * 1.5f, rr * 0.55f)) }
            val mata = q + Offset(az.entre(-w * 0.3f, w * 0.3f), 2f + w * 0.1f)
            for (j in 0..5) drawLine(p.por(lerp(C.ESTEPA, C.CUERO, 0.4f), if (j % 2 == 0) 0.4f else -0.6f, 0.1f), mata, mata + Offset(az.entre(-1f, 1f) * w * 0.08f, -(1f + w * 0.08f) * az.entre(0.6f, 1.2f)), 0.4f + w * 0.01f, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- El cerro, con el ovoo

    /**
     * Los pliegues de la ladera, puestos a mano: cada uno baja de la cresta hacia el valle torciéndose. Del lado del sol
     * tienen un filo de luz; del otro, la sombra se va apagando hacia el pliegue siguiente.
     */
    private val PLIEGUES = listOf(
        listOf(Offset(52f, 66f), Offset(60f, 84f), Offset(74f, 104f), Offset(84f, 128f), Offset(100f, 150f), Offset(112f, 176f)),
        listOf(Offset(108f, 61f), Offset(118f, 78f), Offset(126f, 96f), Offset(142f, 116f), Offset(150f, 140f), Offset(166f, 162f), Offset(176f, 186f)),
        listOf(Offset(162f, 60f), Offset(170f, 76f), Offset(184f, 92f), Offset(192f, 112f), Offset(208f, 132f), Offset(218f, 156f)),
        listOf(Offset(214f, 65f), Offset(222f, 82f), Offset(238f, 98f), Offset(246f, 116f), Offset(262f, 136f)),
        listOf(Offset(258f, 85f), Offset(268f, 100f), Offset(282f, 112f), Offset(292f, 126f)),
    )

    /** Los peñascos: grupos de rocas en la cresta y en los pliegues (centro, tamaño, cuántas). */
    private val PENASCOS = listOf(Triple(Offset(84f, 68f), 11f, 3), Triple(Offset(150f, 62f), 7f, 2), Triple(Offset(128f, 102f), 13f, 3), Triple(Offset(232f, 88f), 9f, 2), Triple(Offset(200f, 126f), 10f, 2), Triple(Offset(272f, 108f), 6f, 2))

    private fun DrawScope.cerro(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val l = p.l; val az = Azar(Semilla.de("mongol", "valle", "cerro"))
        val hierba = lerp(C.ESTEPA, C.TIERRA, 0.22f)
        // El viento barre el cerro: la nieve se queda en las hondonadas y deja ver la hierba seca en los lomos.
        val piel = lerp(hierba, C.NIEVE, nieve * 0.72f)
        val forma = poligono(CRESTA + PIE.reversed() + listOf(Offset(-6f, 230f)))
        // La ladera mira a quien mira y a la derecha: con el sol de la mañana, a la luz; por la tarde, en sombra.
        val tLadera = sol.t(V3(0.45f, 0.55f, -0.7f))
        drawPath(forma, Brush.verticalGradient(0f to p.por(piel, tLadera + 0.15f, 0.36f), 0.5f to p.por(piel, tLadera, 0.24f), 1f to p.por(piel, tLadera - 0.1f, 0.12f), startY = 58f, endY = 214f))
        clipPath(forma) {
            val hacia = if (p.solALaDerecha) 1f else -1f
            // El viento barre la nieve de los lomos: la hierba seca asoma en manchas pardas a lo largo de cada pliegue
            // (del lado del sol) y en la cresta; en las hondonadas la nieve se queda.
            val parda = lerp(hierba, C.CUERO, 0.25f)
            for (pl in PLIEGUES) for (i in 0 until pl.size - 1) {
                val a = pl[i]; val b = pl[i + 1]
                repeat(3) { val c = a + (b - a) * az.real() + Offset(hacia * az.entre(1f, 6f), 0f); val k = ((c.y - 60f) / 140f).coerceIn(0f, 1f); mancha(c, az.entre(4f, 10f) * (0.6f + k), az.entre(1.2f, 2.6f) * (0.6f + k), p.por(parda, tLadera + 0.2f, 0.25f * (1f - k)), (0.75f - 0.25f * nieve) * az.entre(0.5f, 1f)) }
            }
            repeat(30) {
                val c = Offset(az.entre(10f, 340f), az.entre(64f, 200f)); val k = ((c.y - 60f) / 140f).coerceIn(0f, 1f)
                mancha(c, az.entre(6f, 18f) * (0.6f + k), az.entre(1.2f, 3f) * (0.6f + k), p.por(parda, tLadera, 0.25f * (1f - k)), (0.6f - 0.3f * nieve) * az.entre(0.4f, 1f))
            }
            for (i in 0 until CRESTA.size - 1) mancha((CRESTA[i] + CRESTA[i + 1]) / 2f + Offset(0f, 3f), 10f, 2.6f, p.por(parda, tLadera + 0.3f, 0.35f), 0.55f - 0.2f * nieve)
            // Los pliegues: la sombra va del lado contrario al sol y se apaga poco a poco; el filo, del lado de la luz.
            for (pl in PLIEGUES) {
                val linea = curva(pl)
                val lado = pl.map { it + Offset(-hacia * 16f, 3f) }
                val banda = poligono(pl + lado.reversed())
                drawPath(banda, Brush.linearGradient(0f to p.por(piel, -1f, 0.25f).copy(alpha = 0.55f), 1f to Color.Transparent, start = pl[pl.size / 2], end = lado[pl.size / 2]))
                drawPath(linea, p.luz(lerp(piel, Color.White, 0.35f), 0.2f).copy(alpha = 0.55f * sol.r.dia + 0.15f), style = Stroke(0.9f, cap = StrokeCap.Round))
                // Las cárcavas: la nieve se queda en el fondo del pliegue, ancha, con su borde roto.
                val hondo = pl.mapIndexed { i, q -> q + Offset(-hacia * (4f + 3f * (i % 2)), 2f) }
                drawPath(curva(hondo), p.por(C.NIEVE, -0.2f, 0.2f).copy(alpha = 0.75f * nieve), style = Stroke(5.5f, cap = StrokeCap.Round))
                drawPath(curva(hondo.map { it + Offset(hacia * 1.6f, -0.8f) }), p.luz(C.NIEVE, 0.2f).copy(alpha = 0.8f * nieve), style = Stroke(2.2f, cap = StrokeCap.Round))
            }
            // Los peñascos, en grupos: rocas de borde quebrado con su cara de luz, su sombra y la nieve encima.
            for ((c, w, n) in PENASCOS) repeat(n) { k -> pena(p, c + Offset((k - (n - 1) / 2f) * w * 1.3f + az.entre(-0.3f, 0.3f) * w, az.entre(-0.2f, 0.5f) * w + k * 0.6f), w * az.entre(0.6f, 1.05f) * (if (k == 0) 1.2f else 1f), az, nieve, 0.25f) }
            // Las matas y la hierba que salen de la nieve, en hileras que siguen la ladera.
            repeat(420) {
                val x = az.entre(0f, 360f); val y = az.entre(58f, 214f)
                val k = ((y - 58f) / 150f).coerceIn(0f, 1f)
                if (az.real() < nieve * 0.6f) return@repeat
                drawLine(p.por(lerp(hierba, C.CUERO, az.real() * 0.4f), if (it % 3 == 0) 0.7f else -0.4f, 0.3f * (1f - k)), Offset(x, y), Offset(x + az.entre(-0.5f, 0.9f), y - (0.6f + 2.2f * k)), 0.35f + 0.4f * k)
            }
        }
        // La cresta, recortada contra el cielo: un filo de luz y la hierba que asoma.
        drawPath(curva(CRESTA), p.luz(lerp(piel, Color.White, 0.25f), 0.3f).copy(alpha = 0.6f), style = Stroke(0.8f))
        for (q in CRESTA) for (j in 0..2) drawLine(p.por(hierba, -0.2f, 0.35f).copy(alpha = 1f - 0.7f * nieve), q + Offset(j * 2f - 2f, 0.4f), q + Offset(j * 2f - 2f + az.entre(-0.6f, 0.6f), -1.6f), 0.5f)
        // El ovoo en la cima.
        val pie = Offset(190f, 60.4f); val alto = 21f
        capaSombra(sol.r) { c -> sombraArrojada(sol.r, PiezasMongol.siluetaDeOvoo(pie, alto), pie, c, 61f, 61f) }
        ovoo(p, pie, alto, EscenaMongol.cintasDelOvoo(m), Azar(Semilla.de("mongol", "ovoo")), 0.2f, nieve, m.viento)
        // «La última luz del día se queda un momento en lo alto del cerro, sobre el ovoo.»
        if (l.solAltura in -0.05f..0.25f && l.noche < 0.6f) mancha(pie + Offset(0f, -4f), 46f, 10f, l.solColor, 0.25f * (1f - l.noche))
    }

    /** Una roca de la ladera: de borde quebrado y con altura, la cara del sol clara, la otra en sombra, nieve en lo alto y su sombra corta. */
    private fun DrawScope.pena(p: Pincel, c: Offset, w: Float, az: Azar, nieve: Float, lejos: Float) {
        val hacia = if (p.solALaDerecha) 1f else -1f
        val h = w * az.entre(0.55f, 0.9f)
        val borde = listOf(c + Offset(-w, 0.4f), c + Offset(-w * 0.86f, -h * 0.45f), c + Offset(-w * 0.5f, -h * az.entre(0.75f, 0.95f)), c + Offset(-w * 0.08f, -h), c + Offset(w * 0.32f, -h * az.entre(0.7f, 0.9f)), c + Offset(w * 0.72f, -h * 0.5f), c + Offset(w, -h * 0.08f), c + Offset(w * 0.96f, 0.5f))
        val roca = lerp(C.PIEDRA, C.HOLLIN, 0.35f)
        drawOval(p.sombra(C.NIEVE, lejos).copy(alpha = 0.5f), Offset(c.x - w * 1.1f - hacia * w * 0.6f, c.y - h * 0.08f), Size(w * 2.6f, h * 0.32f))
        drawPath(poligono(borde), p.por(roca, -0.8f, lejos))
        // La cara que mira al sol: de la arista del medio hacia ese lado.
        val arista = c + Offset(-w * 0.08f, -h)
        val cara = if (hacia > 0f) poligono(listOf(arista, borde[4], borde[5], borde[6], borde[7], c + Offset(w * 0.1f, 0.5f))) else poligono(listOf(arista, borde[2], borde[1], borde[0], c + Offset(-w * 0.15f, 0.5f)))
        drawPath(cara, p.por(lerp(roca, C.PIEDRA, 0.55f), 0.75f, lejos))
        drawLine(p.sombra(C.HOLLIN, lejos).copy(alpha = 0.7f), arista, c + Offset(hacia * w * 0.1f, 0.5f), 0.5f)
        drawLine(p.sombra(C.HOLLIN, lejos).copy(alpha = 0.5f), borde[2] + Offset(0f, h * 0.2f), borde[2] + Offset(w * 0.1f, h * 0.7f), 0.4f)
        // La nieve que se queda en lo alto y la que el viento arrima al pie.
        if (nieve > 0.1f) {
            drawPath(poligono(listOf(borde[1], borde[2], borde[3], borde[4], borde[5], borde[5] + Offset(-w * 0.1f, h * 0.18f), borde[3] + Offset(w * 0.05f, h * 0.22f), borde[2] + Offset(0f, h * 0.25f), borde[1] + Offset(w * 0.08f, h * 0.15f))), p.luz(C.NIEVE, lejos).copy(alpha = nieve))
            drawOval(p.por(C.NIEVE, 0.3f, lejos).copy(alpha = nieve), Offset(c.x - w * 1.1f, c.y - h * 0.18f), Size(w * 2.2f, h * 0.36f))
        }
    }

    // ---------------------------------------------------------------- El campamento: el plano medio

    /** Dónde está cada tienda del campamento (al pie del cerro, en diagonal), su tamaño, su giro y su desgaste. */
    private class Puesto(val tienda: Tienda, val pie: Offset, val k: Float, val giro: Float, val desgaste: Float)

    private val PUESTOS = listOf(
        Puesto(Tienda.MADRE, Offset(183f, 196f), 9.6f, 0.35f, 0.7f),
        Puesto(Tienda.TEMUR, Offset(232f, 185f), 8.6f, 0.15f, 0.35f),
        Puesto(Tienda.BOLAD, Offset(214f, 172f), 8.2f, 0.25f, 0.5f),
        Puesto(Tienda.MADRE_BOLAD, Offset(242f, 160f), 7.8f, 0.05f, 0.3f),
        Puesto(Tienda.VECINO, Offset(268f, 150f), 6f, -0.1f, 0.6f),
        Puesto(Tienda.CHAMAN, Offset(286f, 142f), 4.6f, -0.2f, 0.8f),
    )

    /** El fuego del centro de cada tienda del campamento a esta hora: el de la gente que vive en ella. */
    private fun fuegoDe(m: Momento, t: Tienda): Float {
        val junta = EscenaMongol.dondeSeJuntan(m)
        if (junta != null) return if (junta == t) 1f else 0.2f
        val base = EscenaMongol.fuegoDentro(m)
        return when (t) { Tienda.MADRE -> if (EscenaMongol.madreEnferma(m)) maxOf(base, 0.8f) else base; else -> base * (0.85f + 0.05f * t.ordinal) }.coerceIn(0f, 1f)
    }

    private fun gerDe(m: Momento, u: Puesto, nieve: Float, armado: Float): Ger = Ger(Sitio(MIRADA, u.pie, u.k, u.giro), Semilla.de("mongol", "tienda", u.tienda.name), u.desgaste,
        if (EscenaMongol.madreEnferma(m) && u.tienda == Tienda.MADRE) EscenaMongol.Puerta.CERRADA else if (m.hacer == Hacer.GENTE && u.tienda == EscenaMongol.dondeSeJuntan(m)) EscenaMongol.Puerta.ENTREABIERTA else EscenaMongol.Puerta.CERRADA,
        fuegoDe(m, u.tienda), nieve, armado, lejos = 0.08f + (196f - u.pie.y) / 300f, detalle = u.k > 8f,
        radio = if (u.tienda == Tienda.MADRE_BOLAD) 3.4f else if (u.tienda == Tienda.CHAMAN) 2.3f else 3f)

    private fun DrawScope.campamento(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val l = p.l; val hay = EscenaMongol.tiendas(m).toSet(); val armado = EscenaMongol.armado(m)
        val az = Azar(Semilla.de("mongol", "valle", "campamento"))
        // El suelo del campamento: tierra pisada y nieve sucia entre las tiendas; la tierra quemada donde estuvo la tienda de la madre.
        val pisado = lerp(lerp(C.TIERRA, C.ESTIERCOL, 0.3f), C.NIEVE, nieve * 0.55f)
        mancha(Offset(232f, 172f), 80f, 22f, p.por(pisado, 0.2f, 0.15f), 0.55f)
        if (EscenaMongol.sitioDeLaMadre(m)) { val c = PUESTOS[0].pie; drawOval(p.medio(lerp(C.HOLLIN, C.TIERRA, 0.3f), 0.1f), Offset(c.x - 28f, c.y - 6f), Size(56f, 12f)); drawOval(p.medio(C.HOLLIN, 0.1f).copy(alpha = 0.6f), Offset(c.x - 8f, c.y - 2f), Size(16f, 4f)) }
        // El corral de detrás, con las ovejas apretadas; los caballos; la carreta parada.
        val corral = Corral(Sitio(MIRADA, Offset(300f, 150f), 5.2f, -0.35f), listOf(Offset(-4.5f, 2.5f), Offset(-4.5f, -2f), Offset(0f, -3.4f), Offset(4.5f, -2.2f), Offset(5.2f, 2.4f)), Semilla.de("mongol", "corral", "medio"), 1.1f, 0.6f, nieve = nieve, lejos = 0.25f, detalle = true)
        val ovejas = (EscenaMongol.ovejas(m) * EscenaMongol.ovejasCerca(m)).toInt()
        val sitios = (0 until ovejas).map { Offset(az.entre(282f, 324f), az.entre(140f, 152f)) }
        val carreta = Carreta(Sitio(MIRADA, Offset(160f, 186f), 9f, -0.9f), Semilla.de("mongol", "carreta", "valle"), EscenaMongol.carretasCargadas(m), EscenaMongol.cuna(m), lejos = 0.1f)
        val tiendas = PUESTOS.filter { it.tienda in hay }.map { gerDe(m, it, nieve, armado) }
        capaSombra(sol.r) { c ->
            for (g in tiendas) sombra(PiezasMongol.sombraDeGer(g, sol), c)
            sombraDeCarreta(carreta, sol, c)
        }
        corral(corral, p, sol)
        rebano(p, sitios, { y -> 3.6f + (y - 140f) * 0.12f }, az, { 0.25f }, pastan = 0.2f)
        // Los caballos del campamento, con la grupa al viento, junto al corral.
        val caballos = EscenaMongol.caballos(m)
        for (j in 0 until caballos) {
            val pie = Offset(330f + (j % 4) * 9f + az.entre(-2f, 2f), 150f + (j / 4) * 4f + az.entre(-1f, 1f))
            caballoLejano(p, sol, pie, 9f + (j / 4) * 1.4f, if (j % 3 == 0) 1f else -1f, listOf(PiezasMongol.BAYO, PiezasMongol.TORDO, PiezasMongol.NEGRO, PiezasMongol.ISABELINO, PiezasMongol.ALAZAN)[j % 5], 0.2f, pasta = j % 2 == 0)
        }
        for (g in tiendas.sortedBy { it.s.pie.y }) ger(g, p, sol)
        carreta(carreta, p, sol)
    }

    // ---------------------------------------------------------------- El primer plano

    /** El borde de la terraza de la tienda de Bat (el talud que baja al campamento), puesto a mano. */
    private val TALUD = listOf(Offset(140f, 214f), Offset(158f, 209f), Offset(172f, 207.5f), Offset(186f, 205f), Offset(200f, 204.6f), Offset(214f, 201.5f), Offset(228f, 201f), Offset(244f, 199.4f), Offset(258f, 201.6f), Offset(270f, 203f), Offset(282f, 207.5f), Offset(296f, 210f), Offset(314f, 214f), Offset(334f, 213f), Offset(352f, 216.5f), Offset(372f, 216f), Offset(392f, 219f))

    /** La esquina del corral de delante: el pie de la esquina en el lienzo, y el muro que se aleja hacia el centro. */
    private val SITIO_CORRAL = Sitio(MIRADA, Offset(372f, 282f), 74.5f, 0f)
    private val MURO_CORRAL = listOf(Offset(3.4f, 0.5f), Offset(0f, 0f), Offset(-0.2f, 1.1f), Offset(-0.42f, 2.2f), Offset(-0.6f, 3.2f), Offset(3.2f, 2.8f))
    private val ARGAL = Offset(256f, 252f)

    private fun DrawScope.primerPlano(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val l = p.l; val az = Azar(Semilla.de("mongol", "valle", "cerca"))
        // La terraza de la tienda de Bat; su borde, un talud de tierra en sombra con piedras y matas, baja al campamento.
        val suelo = lerp(lerp(C.TIERRA, C.ESTEPA, 0.4f), C.NIEVE, nieve * 0.8f)
        val terraza = poligono(TALUD + listOf(Offset(392f, ALTO + 2f), Offset(100f, ALTO + 2f)))
        drawPath(terraza, Brush.verticalGradient(0f to p.por(suelo, 0.45f, 0.05f), 1f to p.por(suelo, 0.15f), startY = 200f, endY = ALTO))
        val talud = poligono(TALUD.map { it + Offset(0f, -0.4f) } + TALUD.reversed().mapIndexed { k, q -> q + Offset(0f, 3.2f + 1.6f * sin(k * 1.7f)) })
        drawPath(talud, p.por(lerp(C.TIERRA, C.ESTIERCOL, 0.3f), -0.7f, 0.05f))
        drawPath(curva(TALUD), p.luz(lerp(suelo, Color.White, 0.3f), 0.05f).copy(alpha = 0.7f), style = Stroke(0.9f))
        for ((k, q) in TALUD.withIndex()) if (k % 2 == 0) { val rr = az.entre(1.2f, 2.6f); drawOval(p.por(C.PIEDRA, -0.4f), Offset(q.x - rr, q.y + 1f), Size(rr * 2f, rr)); drawOval(p.por(lerp(C.PIEDRA, C.NIEVE, nieve), 0.7f), Offset(q.x - rr * 0.8f, q.y + 0.7f), Size(rr * 1.4f, rr * 0.5f)) }
        clipPath(terraza) {
            // La nieve pisada y sucia de delante de la puerta: huellas, estiércol, paja.
            val pisado = lerp(lerp(C.TIERRA, C.ESTIERCOL, 0.4f), C.NIEVE, nieve * 0.45f)
            mancha(Offset(176f, 262f), 110f, 28f, p.por(pisado, 0f), 0.6f)
            // La senda pisada, de la puerta al campamento: nieve sucia, barro helado y las huellas de los cascos.
            val senda = listOf(Offset(150f, 286f), Offset(160f, 262f), Offset(176f, 244f), Offset(196f, 228f), Offset(214f, 214f), Offset(226f, 204f))
            for (i in 0 until senda.size - 1) drawLine(p.por(lerp(pisado, C.ESTIERCOL, 0.35f), -0.1f).copy(alpha = 0.7f), senda[i], senda[i + 1], 26f - i * 4f, StrokeCap.Round)
            for (i in 0 until senda.size - 1) repeat(6) { val c = senda[i] + (senda[i + 1] - senda[i]) * az.real() + Offset(az.entre(-8f, 8f) * (1f - i / 6f), 0f); val w = 2.6f - i * 0.3f; drawOval(p.por(C.ESTIERCOL, -0.6f).copy(alpha = 0.55f), Offset(c.x - w, c.y - w * 0.35f), Size(w * 2f, w * 0.75f)); drawArc(p.luz(lerp(pisado, C.NIEVE, 0.5f)).copy(alpha = 0.6f), 180f, 180f, false, Offset(c.x - w, c.y - w * 0.4f), Size(w * 2f, w * 0.8f), style = Stroke(0.4f)) }
            repeat(26) { val c = Offset(az.entre(120f, 380f), az.entre(206f, 285f)); val k = (c.y - 200f) / 85f; mancha(c, az.entre(12f, 30f) * (0.5f + k), (1.2f + 2.6f * k), if (it % 2 == 0) p.luz(lerp(suelo, Color.White, 0.3f)) else p.por(suelo, -0.9f), 0.45f) }
            repeat(70) { val c = Offset(az.entre(110f, 300f), az.entre(232f, 285f)); drawOval(p.por(pisado, -0.8f).copy(alpha = 0.45f), Offset(c.x - 2f, c.y - 0.7f), Size(4f, 1.6f)) }
            repeat(40) { val c = Offset(az.entre(100f, 330f), az.entre(214f, 285f)); drawLine(p.luz(C.ESTEPA).copy(alpha = 0.6f), c, c + Offset(az.entre(-3f, 3f), az.entre(-0.6f, 0.6f)), 0.4f) }
            repeat(18) { val c = Offset(az.entre(120f, 340f), az.entre(220f, 285f)); val rr = az.entre(1.4f, 3f); drawOval(p.por(C.ESTIERCOL, -0.3f), Offset(c.x - rr, c.y - rr * 0.4f), Size(rr * 2f, rr * 0.8f)) }
        }
        // La tienda de Bat, su sombra y el charco de luz de su puerta.
        val bat = Ger(SITIO_BAT, Semilla.de("mongol", "tienda", "BAT"), 0.45f, EscenaMongol.puerta(m), EscenaMongol.fuegoDentro(m), nieve, EscenaMongol.armado(m), radio = 3f)
        // La esquina del corral de piedra y estiércol, cortada por la derecha, con su cancilla en el muro que se aleja.
        val corral = Corral(SITIO_CORRAL, MURO_CORRAL, Semilla.de("mongol", "corral", "cerca"), 0.74f, 0.5f, abierto = 2, nieve = nieve)
        // Dentro del corral: estiércol pisado y paja.
        val dentro = poligono(MURO_CORRAL.map { SITIO_CORRAL.p(it.x, 0f, it.y) })
        drawPath(dentro, p.por(lerp(C.ESTIERCOL, C.NIEVE, nieve * 0.35f), 0.1f))
        clipPath(dentro) { repeat(30) { val c = Offset(az.entre(290f, 390f), az.entre(232f, 285f)); drawLine(p.luz(C.ESTEPA).copy(alpha = 0.6f), c, c + Offset(az.entre(-4f, 4f), az.entre(-0.8f, 0.8f)), 0.5f) } }
        val cosas = listOf(
            parte(HOGAR.y) { hogar(p, HOGAR, 50f, EscenaMongol.fuegoFuera(m), Azar(Semilla.de("mongol", "hogar")), 0f, true, nieve) },
            parte(COFRE.y) { cofre(m, p, sol) },
            parte(ARGAL.y) { argal(p, ARGAL, 40f, Azar(Semilla.de("mongol", "argal")), 0f, nieve) },
        )
        capaSombra(sol.r) { c ->
            sombra(PiezasMongol.sombraDeGer(bat, sol), c)
            sombraArrojada(sol.r, poligono(Offset(194f, 243f), Offset(234f, 243f), Offset(226f, 214f), Offset(202f, 214f)), HOGAR, c, 200f, 200f)
            sombraArrojada(sol.r, poligono(ARGAL + Offset(-15f, 0f), ARGAL + Offset(28f, 0f), ARGAL + Offset(26f, -20f), ARGAL + Offset(-2f, -30f)), ARGAL, c, 200f, 200f)
        }
        luzDePuerta(bat, l.noche * bat.dentro * 1.1f, 4.2f)
        if (EscenaMongol.fuegoFuera(m) > 0.05f) resplandor(HOGAR + Offset(0f, -4f), 70f, 0.5f * l.noche * EscenaMongol.fuegoFuera(m), C.LUMBRE)
        // El Negro, atado junto a la puerta al anochecer; de día va montado. (El caballo del correo, los días de noticias.)
        if (EscenaMongol.negroAtado(m) || EscenaMongol.caballoAjeno(m)) {
            val pie = Offset(246f, 208f)
            capaSombra(sol.r) { c -> sombraArrojada(sol.r, PiezasMongol.siluetaDeCaballo(pie, 34f, -1f), pie, c, 200f, 200f) }
            caballo(p.con(0.35f * l.noche * bat.dentro), pie, 34f, -1f, if (EscenaMongol.negroAtado(m)) PiezasMongol.NEGRO else PiezasMongol.ALAZAN, silla = EscenaMongol.caballoAjeno(m))
        }
        ger(bat, p, sol)
        corral(corral, p, sol)
        for ((_, pintar) in cosas.sortedBy { it.first }) pintar()
    }

    /** El cofre bajo de junto a la puerta, donde se deja el objeto del día. */
    private fun DrawScope.cofre(m: Momento, p: Pincel, sol: Sol3) {
        val s = Sitio(MIRADA, COFRE, 60f, 0.9f)
        val forma = poligono(s.p(-0.35f, 0f, -0.2f), s.p(0.35f, 0f, -0.2f), s.p(0.35f, 0f, 0.2f), s.p(-0.35f, 0f, 0.2f))
        drawPath(forma, p.sombra(C.HOLLIN).copy(alpha = 0.3f))
        val caras = listOf(
            listOf(V3(-0.35f, 0f, -0.2f), V3(0.35f, 0f, -0.2f), V3(0.35f, 0.32f, -0.2f), V3(-0.35f, 0.32f, -0.2f)) to V3(0f, 0f, -1f),
            listOf(V3(0.35f, 0f, -0.2f), V3(0.35f, 0f, 0.2f), V3(0.35f, 0.32f, 0.2f), V3(0.35f, 0.32f, -0.2f)) to V3(1f, 0f, 0f),
            listOf(V3(-0.35f, 0f, -0.2f), V3(-0.35f, 0f, 0.2f), V3(-0.35f, 0.32f, 0.2f), V3(-0.35f, 0.32f, -0.2f)) to V3(-1f, 0f, 0f),
            listOf(V3(-0.35f, 0.32f, -0.2f), V3(0.35f, 0.32f, -0.2f), V3(0.35f, 0.32f, 0.2f), V3(-0.35f, 0.32f, 0.2f)) to V3(0f, 1f, 0f),
        )
        for ((pts, n) in caras) {
            if (!s.seVe(pts[0] + (pts[2] - pts[0]) * 0.5f, n)) continue
            val f = poligono(pts.map { s.p(it) })
            val tt = sol.t(s.normal(n)) + (if (n.y > 0f) 0.3f else 0f)
            drawPath(f, p.por(C.ROJO, tt))
            clipPath(f) {
                // Los flejes de cobre, la veta de la tapa, el roce de los cantos.
                val a = s.p(pts[0]); val b = s.p(pts[1]); val c = s.p(pts[3])
                for (u in listOf(0.14f, 0.86f)) drawLine(p.por(C.ORO, tt), a + (b - a) * u, c + (b - a) * u, 1.6f)
                drawLine(p.por(lerp(C.ROJO, C.HOLLIN, 0.4f), tt - 0.4f), a + (c - a) * 0.82f, b + (c - a) * 0.82f, 0.5f)
                if (n.z < 0f || n.x != 0f) { val m0 = a + (b - a) * 0.5f + (c - a) * 0.5f; drawCircle(p.por(C.ORO, tt).copy(alpha = 0.55f), 3.2f, m0, style = Stroke(0.6f)); drawCircle(p.por(C.ORO, tt).copy(alpha = 0.55f), 1.5f, m0, style = Stroke(0.6f)) }
            }
            drawPath(f, p.luz(lerp(C.ROJO, C.FIELTRO, 0.4f)).copy(alpha = 0.6f), style = Stroke(0.5f))
        }
    }

    // ---------------------------------------------------------------- El marco: la pértiga del lazo

    private fun DrawScope.marco(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        // La pértiga del lazo, apoyada en el alero de la tienda: cruza en diagonal hasta el borde de arriba.
        val pie = Offset(-12f, ALTO + 6f); val apoyo = Offset(96f, 117f); val punta = Offset(170f, -6f)
        val madera = lerp(C.MADERA, C.PIEDRA, 0.25f)
                drawLine(p.sombra(madera), pie, punta, 3.2f, StrokeCap.Round)
        drawLine(p.luz(lerp(madera, C.FIELTRO, 0.2f)), pie + Offset(if (p.solALaDerecha) 0.9f else -0.9f, 0f), punta + Offset(if (p.solALaDerecha) 0.5f else -0.5f, 0f), 1.1f, StrokeCap.Round)
        // Las vueltas de crin donde se ata el lazo, y la cuerda que cuelga un poco.
        for (j in 0..3) { val c = apoyo + (punta - apoyo) * (0.62f + j * 0.025f); drawLine(p.medio(C.CRIN), c + Offset(-2.4f, -0.6f), c + Offset(2.4f, 0.6f), 1.2f) }
        val a = apoyo + (punta - apoyo) * 0.66f
        drawPath(Path().apply { moveTo(a); cubicTo(a + Offset(4f, 14f), a + Offset(10f, 20f), a + Offset(12f, 28f)) }, p.medio(C.CRIN), style = Stroke(0.7f))
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = EscenaMongol.luces(m)
        // El humo de la tienda de Bat entra por arriba, del aro que queda fuera del cuadro.
        val f = EscenaMongol.fuegoDentro(m)
        if (EscenaMongol.armado(m) >= 1f) humo(l, Offset(-30f, 30f), 90f, 10f + 6f * f, reloj, maxOf(m.viento, 0.5f), 0.25f, 0f, 3, 9, 0.35f + 0.4f * f)
        // El de las otras tiendas, «como hilos grises hacia las estrellas».
        val hay = EscenaMongol.tiendas(m).toSet()
        for ((j, u) in PUESTOS.withIndex()) {
            if (u.tienda !in hay || EscenaMongol.armado(m) < 1f) continue
            val g = gerDe(m, u, 0f, 1f)
            humo(l, g.humero, 40f + u.k * 2f, 2f + u.k * 0.4f, reloj, m.viento, 0.2f, 0.2f, j + 5, 7, 0.3f + 0.4f * g.dentro)
        }
        for ((j, x) in listOf(352f, 367f).withIndex()) humo(l, Offset(x, H + 4f), 22f, 1.6f, reloj, m.viento, 0.2f, 0.6f, j + 20, 5, 0.3f)
        // El hogar de fuera.
        val ff = EscenaMongol.fuegoFuera(m)
        if (ff > 0.05f) { for ((dx, a) in listOf(-4f to 10f, 2f to 14f, 7f to 8f)) llama(HOGAR + Offset(dx, -2f), a * ff, reloj, dx.toInt() + 40); humo(l, HOGAR + Offset(0f, -30f), 60f, 6f, reloj, m.viento, 0.2f, 0f, 9, 8, 0.4f * ff) }
        else brasas(l, HOGAR + Offset(0f, -1f), 10f, reloj)
        if (EscenaMongol.hielo(m) < 0.5f && l.noche < 0.5f) destellos(l, Rect(300f, 140f, 380f, 214f), reloj, m.semilla("rio"), 12)
        tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    fun luzDelGato(m: Momento, en: Offset): LuzGato { val l = EscenaMongol.luces(m); return LuzGato(Rayo(l), l.noche) { l.tono(it) } }

    /** Lo plantado en el suelo del gato: nada (el hogar y el cofre quedan detrás de su paso). */
    fun estorbos(m: Momento): List<Float> = emptyList()

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 150f, PISO_GATO, hasta = 316f))
        add(Ancla(TipoAncla.MIRADOR, 330f, 238f))                                // en lo alto del muro del corral
        add(Ancla(TipoAncla.OBJETO, COFRE.x, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 160f, PISO_GATO))                            // junto a la puerta
        if (EscenaMongol.fuegoFuera(m) >= 0.45f) add(Ancla(TipoAncla.FUEGO, HOGAR.x + 30f, PISO_GATO, hasta = HOGAR.x))
    }
}
