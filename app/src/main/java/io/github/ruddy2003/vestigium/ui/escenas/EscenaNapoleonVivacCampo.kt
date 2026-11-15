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
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Fase
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.carro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.canon
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.hoguera
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.Campo
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.Fondo
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.H
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.MIRADA
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.k
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.w
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.zEn
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.exp
import kotlin.math.sin

/**
 * El campo del vivac: el llano del tramo, que cambia de lejos a cerca (la superficie grande, regla 4), el camino real con
 * el polvo de la columna (la contradiagonal), y los campamentos del ejército en filas que se van al mismo punto de fuga
 * que la nuestra, con sus pabellones, sus tiendas y sus fuegos: de noche, «hogueras hasta el horizonte».
 *
 * Todo se pone en metros con [VivacNapoleon.w]. Los fuegos lejanos son puntos, no piezas: cientos, y de noche cada uno
 * con su halo y su charco de luz en la hierba.
 */
internal object VivacCampo {
    private val C = ColorNapoleon

    // ---- Los colores del campo: mezclas de la paleta.
    private val HIERBA = lerp(C.PINO, C.HOJA, 0.58f)
    private val HIERBA_SECA = lerp(C.HOJA, C.PINO_LUZ, 0.35f)
    private val TRIGO = lerp(C.HOJA, C.LONA, 0.42f)
    private val TRIGO_VERDE = lerp(C.PINO_LUZ, C.HOJA, 0.4f)
    private val RASTROJO = lerp(C.HOJA, C.BARRO, 0.35f)
    private val ARENA = lerp(C.LONA, C.HOJA, 0.42f)
    private val VINA = lerp(C.PINO, C.HOJA, 0.42f)
    private val TIERRA = lerp(C.BARRO, C.HOJA, 0.3f)

    /** El prado del campamento, de cerca: la hierba de la estación, pisada. */
    private fun prado(v: VistaVivac): Color = when (v.m.ficha.estacion) {
        io.github.ruddy2003.vestigium.vidas.Estacion.PRIMAVERA -> HIERBA
        io.github.ruddy2003.vestigium.vidas.Estacion.VERANO -> lerp(HIERBA, C.HOJA, 0.38f)
        io.github.ruddy2003.vestigium.vidas.Estacion.OTONO -> lerp(HIERBA_SECA, C.BARRO, 0.25f)
        else -> lerp(HIERBA_SECA, C.BARRO, 0.35f)
    }

    /**
     * El color propio del campo, de lejos ([lejos] = 1) a cerca: de cerca, el prado del campamento, pisado; lejos, lo que
     * cubre el llano ese día (el trigo, el rastrojo, la arena, las viñas). Con nieve, nieve, sucia de cerca.
     */
    fun colorDelCampo(v: VistaVivac, lejos: Float): Color {
        if (v.campo == Campo.NIEVE) return lerp(C.NIEVE_SUCIA, C.NIEVE, (lejos * 9f).coerceIn(0f, 1f))
        val llano = when (v.campo) {
            Campo.HIERBA -> prado(v)
            Campo.TRIGO_VERDE -> TRIGO_VERDE
            Campo.TRIGO -> TRIGO
            Campo.RASTROJO -> RASTROJO
            Campo.ARENA -> ARENA
            Campo.VINA -> VINA
            Campo.NIEVE -> C.NIEVE
        }
        val cerca = if (v.campo == Campo.ARENA) lerp(ARENA, prado(v), 0.35f) else lerp(prado(v), TIERRA, 0.22f)
        return lerp(cerca, llano, ((lejos - 0.06f) * 12f).coerceIn(0f, 1f))
    }

    // ---------------------------------------------------------------- El camino real

    /** El camino real: el centro (x, en metros) a la profundidad [z]; se va hacia la izquierda del cuadro. */
    fun camino(z: Float): Float = 13.5f - 0.17f * z
    const val CAMINO_ANCHO = 3.6f

    /** El camino como forma, del borde de la derecha al horizonte. */
    fun caminoForma(): Path {
        val zs = listOf(18f, 22f, 27f, 33f, 40f, 50f, 64f, 82f, 110f, 150f, 220f, 340f, 600f)
        val az = VivacNapoleon.fijo("camino")
        val izq = zs.map { z -> w(camino(z) - CAMINO_ANCHO + az.entre(-0.3f, 0.3f), z) }
        val der = zs.map { z -> w(camino(z) + CAMINO_ANCHO + az.entre(-0.3f, 0.3f), z) }
        return poligono(izq + der.reversed())
    }

    // ---------------------------------------------------------------- El suelo

    fun DrawScope.campo(v: VistaVivac) {
        val p = v.p; val az = VivacNapoleon.fijo("campo", v.fondo.ordinal)
        // La base: de la lejanía azulada a la cercanía cálida.
        drawRect(Brush.verticalGradient(0f to v.suelo(0.85f), 0.06f to v.suelo(0.6f), 0.25f to v.suelo(0.3f), 0.55f to v.suelo(0.12f), 1f to v.suelo(0f), startY = H, endY = ALTO), Offset(-10f, H - 0.5f), Size(ANCHO + 20f, ALTO - H + 10f))
        // Las hazas: franjas de cultivo que se van al fondo, cada una de su color (trigo, barbecho, prado), con sus lindes.
        parcelas(v, az)
        // La textura de cerca: matas, rastrojo, surcos de la arena, la nieve con sus ondas.
        textura(v, az)
        // En el pueblo, las filas de cepas que se van por la ladera, y el murete de piedra seca en diagonal.
        if (v.fondo == Fondo.PUEBLO) vinas(v)
        // El río del Niemen, a media distancia, con los puentes de barcas (el 24 de junio) o helado (el 14 de diciembre).
        if (v.fondo == Fondo.NIEMEN) niemen(v)
        // El camino real, con sus rodadas.
        caminoReal(v)
    }

    /**
     * Las viñas del pueblo: filas de cepas en sus estacas que se van al fondo (la diagonal del cuadro, donde en campaña
     * está la fila de pabellones), y el murete de piedra seca que las cierra **[Diseño propio]**, provisional.
     */
    private fun DrawScope.vinas(v: VistaVivac) {
        val p = v.p; val az = VivacNapoleon.fijo("vinas"); val hojas = VivacParra.hojas(v.m)
        val verde = lerp(C.PINO, C.HOJA, 0.5f)
        for (fila in 8 downTo 0) {
            val dx = 2.4f + fila * 1.7f
            var z = 18f + fila * 1.2f
            val pts = ArrayList<Offset>()
            while (z < 140f) { pts += w(VivacCerca.filaEn(z) + dx, z); z *= 1.1f }
            if (pts.size < 2) continue
            // La fila: un seto bajo y continuo de hojas (o los sarmientos podados en invierno), y las estacas.
            val altos = pts.map { 1.0f * k(VivacNapoleon.zEn(it.y)) }
            val lejos = VivacNapoleon.lejos(VivacNapoleon.zEn(pts.first().y))
            if (hojas > 0.05f) {
                val seto = poligono(pts + pts.indices.reversed().map { i -> pts[i] + Offset(0f, -altos[i] * (0.6f + 0.4f * hojas)) })
                drawPath(seto, p.medio(lerp(verde, C.PINO_OSC, 0.3f), lejos))
                drawPath(poligono(pts.indices.map { i -> pts[i] + Offset(0f, -altos[i] * (0.55f + 0.4f * hojas)) } + pts.indices.reversed().map { i -> pts[i] + Offset(0f, -altos[i] * (0.8f + 0.25f * hojas)) }), p.lado(verde, lejos).copy(alpha = 0.7f))
            }
            for ((i, q) in pts.withIndex()) {
                if (q.x > ANCHO + 6f) continue
                val kk = k(VivacNapoleon.zEn(q.y))
                drawLine(p.medio(C.MADERA_GRIS, lejos), q, q + Offset(0f, -altos[i] * 1.15f), (0.035f * kk).coerceIn(0.3f, 1.2f))
                if (hojas <= 0.05f) drawLine(p.medio(lerp(C.MADERA_OSC, C.CAPOTE, 0.25f), lejos), q, q + Offset(az.entre(-0.15f, 0.15f) * kk, -0.6f * kk), (0.06f * kk).coerceIn(0.4f, 2f), StrokeCap.Round)
            }
        }
        // El murete, de piedras sin argamasa, a lo largo de la diagonal.
        var z = 6.5f
        while (z < 60f) {
            val q = w(VivacCerca.filaEn(z) + 0.4f, z); val kk = k(z); val lejos = VivacNapoleon.lejos(z)
            val ancho = 0.5f * kk; val alto = 0.75f * kk
            for (j in 0 until 4) for (i in 0 until 2) {
                val c = q + Offset(az.entre(-0.3f, 0.3f) * ancho + i * ancho * 0.5f, -alto * (0.15f + 0.25f * j))
                val piedra = TrazosValle.borron(c, ancho * az.entre(0.25f, 0.35f), alto * 0.11f, az, 7, 0.2f, 0.8f)
                drawPath(piedra, p.lado(lerp(C.LONA, C.HOJA, 0.3f), lejos, alReves = j % 2 == 0))
                drawPath(piedra, p.sombra(C.CAPOTE, lejos).copy(alpha = 0.5f), style = Stroke((0.02f * kk).coerceAtLeast(0.3f)))
            }
            z *= 1.09f
        }
    }

    /** Las hazas del llano: polígonos que siguen las líneas de fuga, de colores vecinos, con su linde. */
    private fun DrawScope.parcelas(v: VistaVivac, az: Azar) {
        if (v.campo == Campo.NIEVE) return
        val p = v.p
        val colores = when (v.campo) {
            Campo.TRIGO -> listOf(TRIGO, lerp(TRIGO, C.HOJA, 0.4f), RASTROJO, lerp(TRIGO, HIERBA, 0.45f), lerp(TRIGO, C.LONA, 0.3f))
            Campo.TRIGO_VERDE -> listOf(TRIGO_VERDE, lerp(HIERBA, C.HOJA, 0.2f), lerp(TRIGO_VERDE, C.HOJA, 0.3f), lerp(TIERRA, C.HOJA, 0.3f), lerp(HIERBA, TRIGO_VERDE, 0.5f))
            Campo.RASTROJO -> listOf(RASTROJO, TIERRA, lerp(RASTROJO, C.HOJA, 0.4f), HIERBA_SECA, lerp(TIERRA, C.CENIZA, 0.15f))
            Campo.ARENA -> listOf(ARENA, lerp(ARENA, HIERBA_SECA, 0.5f), lerp(ARENA, C.LONA, 0.3f), HIERBA_SECA)
            Campo.VINA -> listOf(VINA, lerp(VINA, C.HOJA, 0.3f), TIERRA, HIERBA)
            Campo.HIERBA, Campo.NIEVE -> listOf(HIERBA, lerp(HIERBA, C.HOJA, 0.35f), HIERBA_SECA, TIERRA)
        }
        // Las hazas, de lejos a cerca, en el llano de detrás del campamento (de z = 30 al horizonte).
        val bandas = listOf(22f, 30f, 42f, 58f, 80f, 110f, 160f, 240f, 400f, 900f)
        for (i in 0 until bandas.size - 1) {
            val z0 = bandas[i]; val z1 = bandas[i + 1]
            var x = -60f - z1 * 0.4f
            while (x < 60f + z1 * 0.4f) {
                val ancho = az.entre(10f, 34f) * (z1 / 60f).coerceAtLeast(1f)
                val col = colores[az.entero(colores.size)]
                val lejos = VivacNapoleon.lejos((z0 + z1) / 2f)
                val forma = poligono(w(x, z0), w(x + ancho, z0), w(x + ancho * 1.1f, z1), w(x + ancho * 0.02f, z1))
                drawPath(forma, p.arriba(col, lejos).copy(alpha = 0.85f))
                // La linde: una raya de hierba más oscura, o un surco.
                drawLine(p.sombra(lerp(col, C.PINO, 0.3f), lejos).copy(alpha = 0.5f), w(x, z0), w(x + ancho * 0.02f, z1), (0.25f * k(z0)).coerceIn(0.3f, 1.4f))
                // Los surcos del trigo o del arado, que se van al fondo.
                if (v.campo == Campo.TRIGO || v.campo == Campo.RASTROJO || v.campo == Campo.VINA) clipPath(forma) {
                    val n = (ancho / 1.6f).toInt().coerceIn(2, 14)
                    for (j in 1 until n) { val u = j / n.toFloat(); drawLine(p.sombra(col, lejos).copy(alpha = 0.35f), w(x + ancho * u, z0), w(x + ancho * (0.02f + 1.08f * u), z1), (0.08f * k(z0)).coerceIn(0.25f, 0.9f)) }
                }
                x += ancho
            }
        }
        // Las matas y los setos sueltos del llano, con su sombrita, para que no sea un tapiz.
        repeat(40) {
            val z = 22f * exp(az.entre(0f, 2.6f)); val x = az.entre(-30f, 30f) * (z / 30f); val q = w(x, z)
            if (q.x < -5f || q.x > ANCHO + 5f) return@repeat
            val kk = k(z); val lejos = VivacNapoleon.lejos(z)
            sombraCorta(v.r, q, 1.4f * kk, 0.9f * kk)
            drawOval(p.medio(lerp(C.PINO, C.HOJA, 0.35f), lejos), Offset(q.x - 0.7f * kk, q.y - 0.9f * kk), Size(1.4f * kk, 0.95f * kk))
            drawOval(p.lado(lerp(C.PINO_LUZ, C.HOJA, 0.3f), lejos).copy(alpha = 0.6f), Offset(q.x + (if (p.solALaDerecha) 0f else -0.6f) * kk, q.y - 0.88f * kk), Size(0.6f * kk, 0.5f * kk))
        }
    }

    /** La textura del campo de cerca: matas de hierba, espigas, rastrojo, la arena con sus rizos, o la nieve con sus ondas. */
    private fun DrawScope.textura(v: VistaVivac, az: Azar) {
        val p = v.p
        when (v.campo) {
            Campo.NIEVE -> {
                for (i in 0 until 8) {
                    val z = 12f * exp(i * 0.45f); val y = w(0f, z).y
                    val onda = Path().apply { moveTo(-10f, y); var x = -10f; while (x < ANCHO + 10f) { val dx = az.entre(18f, 44f) * (30f / z).coerceIn(0.4f, 1.6f); cubicTo(x + dx * 0.3f, y - az.entre(0.4f, 1.6f), x + dx * 0.7f, y + az.entre(-0.3f, 0.8f), x + dx, y + az.entre(-0.5f, 0.5f)); x += dx } }
                    drawPath(onda, p.arriba(C.NIEVE_SOMBRA, VivacNapoleon.lejos(z)).copy(alpha = 0.5f), style = Stroke(0.9f + 30f / z))
                }
                if (v.r.fuerza > 0.2f) repeat(60) { val q = Offset(az.entre(0f, ANCHO), az.entre(170f, ALTO)); drawCircle(Color.White.copy(alpha = 0.6f * v.r.fuerza), az.entre(0.3f, 0.7f) * (q.y / 230f), q, blendMode = BlendMode.Plus) }
            }
            Campo.ARENA -> repeat(60) {
                val q = Offset(az.entre(-10f, ANCHO), az.entre(150f, ALTO)); val kk = k(zEn(q.y))
                drawLine(p.sombra(ARENA, VivacNapoleon.lejos(zEn(q.y))).copy(alpha = 0.4f), q, q + Offset(az.entre(0.6f, 1.6f) * kk, az.entre(-0.05f, 0.05f) * kk), (0.04f * kk).coerceIn(0.3f, 1f), StrokeCap.Round)
            }
            else -> Unit
        }
        // El prado del campamento: pinceladas de hierba más seca y más verde que rompen el degradado (regla 7), y las sendas
        // pisadas que van del campamento al camino.
        if (v.campo != Campo.NIEVE) {
            val pr = prado(v)
            repeat(26) {
                val y = az.entre(150f, ALTO); val q = Offset(az.entre(-20f, ANCHO + 20f), y); val kk = k(zEn(y))
                mancha(q, az.entre(2.5f, 6f) * kk, az.entre(0.4f, 0.9f) * kk, p.arriba(if (it % 2 == 0) lerp(pr, C.HOJA, 0.4f) else lerp(pr, C.PINO, 0.25f), VivacNapoleon.lejos(zEn(y))), 0.45f)
            }
            val senda = lerp(TIERRA, C.LONA, 0.25f)
            for ((x0, z0, x1, z1) in listOf(floatArrayOf(2.4f, 6f, 9.5f, 26f), floatArrayOf(-0.4f, 9f, 6.8f, 34f))) {
                for (i in 0 until 40) {
                    val t = i / 40f; val z = z0 + (z1 - z0) * t; val q = w(x0 + (x1 - x0) * t + 0.3f * sin(t * 9f), z); val kk = k(z)
                    mancha(q, 0.55f * kk, 0.1f * kk, p.arriba(senda, VivacNapoleon.lejos(z)), 0.5f)
                }
            }
            // Las flores del campo: amapolas y acianos entre el trigo y en las lindes (**[Aproximación]**), de abril a agosto.
            val mes = v.m.ficha.dia.substring(5, 7).toInt()
            if (mes in 5..8) repeat(90) {
                val y = 145f + 140f * az.real() * az.real(); val q = Offset(az.entre(-6f, ANCHO + 6f), y); val kk = k(zEn(y))
                val r = (0.04f * kk).coerceIn(0.35f, 2.2f)
                drawCircle(p.luz(if (it % 3 == 0) lerp(C.AZUL, C.PLATA, 0.35f) else lerp(C.ROJO, C.LUMBRE, 0.15f), VivacNapoleon.lejos(zEn(y))), r, q + Offset(0f, -0.15f * kk))
            }
        }
        // Las matas de hierba (o las espigas, o el rastrojo) de cerca, en mechones con su lado de luz.
        if (v.campo != Campo.NIEVE) {
            val color = prado(v)
            repeat(420) {
                val y = 285f - 150f * az.real() * az.real(); val q = Offset(az.entre(-6f, ANCHO + 6f), y); val z = zEn(y); val kk = k(z); val lejos = VivacNapoleon.lejos(z)
                val alto = az.entre(0.06f, 0.2f) * kk; if (alto < 0.6f) return@repeat
                for (j in 0 until 4) { val inc = az.entre(-0.35f, 0.35f) * alto; drawLine(if (j % 2 == 0) p.lado(lerp(color, C.LONA, 0.12f), lejos) else p.medio(lerp(color, C.PINO, 0.2f), lejos), q + Offset(az.entre(-0.05f, 0.05f) * kk, 0f), q + Offset(inc, -alto), (0.012f * kk).coerceIn(0.3f, 1f), StrokeCap.Round) }
            }
        }
    }

    /** El camino real, con sus rodadas y la tierra de las orillas; con nieve, pisado y sucio. */
    private fun DrawScope.caminoReal(v: VistaVivac) {
        val p = v.p; val forma = caminoForma()
        val tierra = if (v.nieve > 0.5f) C.NIEVE_SUCIA else if (v.campo == Campo.ARENA) lerp(ARENA, C.LONA, 0.2f) else lerp(TIERRA, C.LONA, 0.28f)
        drawPath(forma, Brush.verticalGradient(listOf(p.arriba(tierra, 0.85f), p.arriba(tierra, 0.4f)), H, 160f))
        clipPath(forma) {
            for (dx in listOf(-1.6f, -0.9f, 0.9f, 1.6f)) {
                val pts = listOf(18f, 24f, 32f, 44f, 62f, 90f, 140f, 240f, 600f).map { z -> w(camino(z) + dx, z) }
                for (i in 0 until pts.size - 1) drawLine(p.sombra(tierra, 0.6f).copy(alpha = 0.55f), pts[i], pts[i + 1], (0.12f * k(zEn(pts[i].y))).coerceIn(0.3f, 1.2f))
            }
        }
        // Las orillas: hierba más alta y pisada, una franja de paso entre el camino y el campo (regla 9).
        val az = VivacNapoleon.fijo("orillas")
        for (lado in listOf(-1f, 1f)) for (i in 0 until 40) {
            val z = 20f * exp(az.entre(0f, 2.8f)); val q = w(camino(z) + lado * (CAMINO_ANCHO + az.entre(-0.2f, 0.4f)), z); val kk = k(z)
            if (q.x < -4f || q.x > ANCHO + 4f) continue
            mancha(q, 0.8f * kk, 0.14f * kk, p.medio(if (v.nieve > 0.5f) C.NIEVE_SOMBRA else lerp(C.PINO_LUZ, C.HOJA, 0.4f), VivacNapoleon.lejos(z)), 0.6f)
        }
    }

    /**
     * El Niemen, a media distancia: el agua ancha que refleja el cielo, más honda lejos y clara en la orilla, con los tres
     * puentes de barcas de los pontoneros (el 24 de junio); o helado y nevado, con el rastro de la columna que lo cruzó
     * (el 14 de diciembre).
     */
    private fun DrawScope.niemen(v: VistaVivac) {
        val p = v.p; val l = v.l; val az = VivacNapoleon.fijo("niemen")
        val helado = v.nieve > 0.4f || v.m.ficha.dia.substring(5) >= "12-01"
        val aca = (0..18).map { i -> val x = -10f + 400f * i / 18f; Offset(x, 138f + 4f * sin(i * 0.8f) + az.entre(-1.4f, 1.4f) - x * 0.012f) }
        val alla = (0..18).map { i -> val x = -10f + 400f * i / 18f; Offset(x, 127.5f + az.entre(-0.6f, 0.6f) - x * 0.004f) }
        val rio = poligono(alla + aca.reversed())
        if (helado) {
            drawPath(rio, Brush.verticalGradient(listOf(p.arriba(lerp(C.NIEVE, C.PLATA, 0.3f), 0.7f), p.arriba(lerp(C.NIEVE, C.NIEVE_SOMBRA, 0.4f), 0.5f)), 127f, 142f))
            clipPath(rio) { repeat(20) { val q = Offset(az.entre(0f, ANCHO), az.entre(128f, 142f)); drawLine(p.sombra(C.NIEVE_SOMBRA, 0.6f).copy(alpha = 0.4f), q, q + Offset(az.entre(6f, 20f), 0.2f), 0.5f) } }
        } else {
            drawPath(rio, Brush.verticalGradient(listOf(lerp(l.bajo, p.medio(lerp(C.AZUL, C.PINO, 0.4f), 0.6f), 0.4f), p.medio(lerp(C.AZUL, C.PINO_LUZ, 0.5f), 0.45f)), 127f, 142f))
            clipPath(rio) {
                drawRect(Brush.verticalGradient(listOf(l.bajo.copy(alpha = 0.4f), Color.Transparent), 127f, 136f), Offset(0f, 127f), Size(ANCHO, 15f))
                repeat(30) { val q = Offset(az.entre(0f, ANCHO), az.entre(128f, 142f)); drawLine(lerp(l.bajo, Color.White, 0.3f).copy(alpha = 0.3f), q, q + Offset(az.entre(2f, 7f), 0f), 0.4f) }
            }
            // Los puentes de barcas: la fila de barcas y el tablero encima, de orilla a orilla.
            for ((i, x) in listOf(70f, 196f, 300f).withIndex()) {
                val y0 = TrazosValle.alturaEn(alla, x); val y1 = TrazosValle.alturaEn(aca, x + 10f)
                val a0 = Offset(x, y0); val a1 = Offset(x + 10f + i * 2f, y1)
                val n = 7
                for (j in 0..n) { val q = a0 + (a1 - a0) * (j / n.toFloat()); val t = 0.5f + 1.3f * j / n
                    drawOval(p.medio(C.MADERA_OSC, 0.55f), Offset(q.x - 2.2f * t, q.y - 0.4f * t), Size(4.4f * t, 1.1f * t)) }
                drawLine(p.luz(C.MADERA_GRIS, 0.55f), a0 + Offset(0f, -0.8f), a1 + Offset(0f, -1.6f), 1.6f)
            }
        }
        // La orilla de acá: la arena y el talud, que hacen de franja de paso entre el agua y el campo.
        drawPath(TrazosValle.cerrada(aca.map { it + Offset(0f, -0.4f) } + aca.reversed().map { it + Offset(0f, 2.6f) }), p.arriba(if (helado) C.NIEVE else lerp(ARENA, C.LONA, 0.2f), 0.5f))
    }

    // ---------------------------------------------------------------- Los campamentos del ejército

    /** Una fila de campamento: su desplazamiento (en metros) respecto a la nuestra y desde dónde se ve. */
    private class Fila(val dx: Float, val desde: Float)

    /**
     * Las filas del ejército, paralelas a la de la compañía y con su mismo punto de fuga: la nuestra sigue más allá del
     * camino (las otras compañías del batallón); a la izquierda, detrás de la tienda, los otros batallones; a la derecha,
     * lejos, otro regimiento. **[Diseño propio]**: el orden del campamento.
     */
    private val FILAS = listOf(Fila(0f, 52f), Fila(-13f, 30f), Fila(-27f, 48f), Fila(-42f, 72f), Fila(-60f, 110f), Fila(16f, 84f), Fila(32f, 140f))

    /** Un fuego lejano: dónde (en el lienzo), a qué profundidad, y su orden (los primeros se encienden antes). */
    class FuegoLejano(val p: Offset, val z: Float, val orden: Int)

    /** Los fuegos de los campamentos, de cerca a lejos, por orden de importancia (los de las filas primero; luego, sueltos hasta el horizonte). */
    val FUEGOS_LEJANOS: List<FuegoLejano> by lazy {
        val az = VivacNapoleon.fijo("fuegosLejanos"); val lista = ArrayList<FuegoLejano>()
        for ((fi, f) in FILAS.withIndex()) {
            var z = f.desde + az.entre(0f, 3f)
            while (z < 340f) {
                val x = VivacCerca.filaEn(z) + f.dx - VivacCerca.DETRAS + az.entre(-0.4f, 0.4f)
                val q = w(x, z)
                if (q.x > -4f && q.x < ANCHO + 4f && !enElCamino(x, z)) lista += FuegoLejano(q, z, fi * 1000 + lista.size)
                z += 3.8f * az.entre(0.85f, 1.2f) * (1f + z / 90f)
            }
        }
        // Los de otros cuerpos, sueltos por el llano hasta el horizonte.
        repeat(120) { val z = 70f * exp(az.entre(0f, 2.1f)); val x = az.entre(-1.2f, 1.6f) * z * 0.34f; val q = w(x, z); if (q.x > -4f && q.x < ANCHO + 4f && !enElCamino(x, z)) lista += FuegoLejano(q, z, 9000 + it) }
        lista.sortedBy { it.orden }
    }

    private fun enElCamino(x: Float, z: Float): Boolean = kotlin.math.abs(x - camino(z)) < CAMINO_ANCHO + 1.2f

    /** Los fuegos lejanos de hoy (los primeros [VivacNapoleon.fuegosDelEjercito], con los huecos de la compañía mermada). */
    fun fuegosDeHoy(m: Momento): List<FuegoLejano> {
        val n = VivacNapoleon.fuegosDelEjercito(m); if (n <= 0) return emptyList()
        val hueco = 1f - VivacNapoleon.fuegosDeLaCompania(m) / 17f
        val az = VivacNapoleon.fijo("huecos")
        return FUEGOS_LEJANOS.filter { az.real() >= hueco * 0.7f }.take(n)
    }

    /** Los fuegos de los otros ranchos de la compañía, a lo largo de la fila (sin el nuestro, que está en la base). */
    fun ranchos(m: Momento): List<Pair<Float, Float>> = VivacCerca.FUEGOS.drop(1).take((VivacNapoleon.fuegosDeLaCompania(m) - 1).coerceAtLeast(0))

    /** Lo que tumba sombra en el campo: las tiendas y los carros del parque. */
    fun sombras(v: VistaVivac): List<RetiradaLuz.Sombra> = buildList {
        for (s in carros()) add(RetiradaLuz.Sombra(s.pie, poligono(s.p(-1.1f, 0f, 0f), s.p(1.1f, 0f, 0f), s.p(0.9f, 2.2f, 0f), s.p(-0.9f, 2.2f, 0f))))
        for ((q, kk) in tiendas()) add(RetiradaLuz.Sombra(q, poligono(q + Offset(-1.3f * kk, 0f), q + Offset(1.3f * kk, 0f), q + Offset(0f, -2.4f * kk))))
    }

    /** Los carros del bagaje, aparcados a la derecha entre la fila y el camino (esperan para salir detrás de la columna). */
    private fun carros(): List<Sitio> = listOf(w(6.6f, 17f) to 0.9f, w(8.8f, 21f) to 1.1f, w(5.2f, 25f) to 0.7f).map { (q, g) -> PiezasNapoleon.sitio(MIRADA, q, g) }

    /** Las tiendas de los oficiales de las otras compañías (en el lienzo, con su escala). */
    private fun tiendas(): List<Pair<Offset, Float>> {
        val lista = ArrayList<Pair<Offset, Float>>()
        for (f in FILAS) { var z = f.desde + 6f; while (z < 300f) { val x = VivacCerca.filaEn(z) + f.dx - 4.5f; val q = w(x, z); if (q.x > -10f && q.x < ANCHO + 10f && !enElCamino(x, z)) lista += q to k(z); z += 34f * (1f + z / 200f) } }
        return lista
    }

    /** El parque de cañones, más allá del camino: los cañones en fila con la boca hacia el campo. */
    private fun canones(): List<Sitio> = listOf(66f, 70f, 74f, 78f).map { z -> PiezasNapoleon.sitio(MIRADA, w(camino(z) + 9f, z), -1.2f) }

    fun DrawScope.campamentos(v: VistaVivac) {
        val m = v.m; val p = v.p
        val conCompania = !VivacNapoleon.sinCompania(m)
        val enPie = VivacNapoleon.pabellones(m) > 0
        val noche = EscenaNapoleon.fuegosLejos(m)
        // De lejos a cerca: los pabellones y las tiendas de las filas, y los fuegos (de día, la ceniza y el humo).
        if (conCompania) {
            for ((fi, f) in FILAS.withIndex()) {
                var z = f.desde; val az = VivacNapoleon.fijo("pabellonesLejos", fi)
                val lista = ArrayList<Offset>()
                while (z < 420f) { val x = VivacCerca.filaEn(z) + f.dx + az.entre(-0.1f, 0.1f); if (!enElCamino(x, z)) lista += w(x, z); z += 1.9f * (1f + z / 120f) }
                if (enPie) for (q in lista.asReversed()) pabellonLejano(v, q, k(zEn(q.y)))
            }
            for ((q, kk) in tiendas().sortedBy { it.first.y }) tiendaLejana(v, q, kk)
            for (s in canones()) canon(s, v.en(s.pie), 0f, v.nieve, VivacNapoleon.lejos(s.z0))
            for (s in carros().sortedBy { it.pie.y }) carro(s, v.en(s.pie), v.nieve, Semilla.de("napoleon", "vivac", "carro", s.pie.x.toInt()), VivacNapoleon.lejos(s.z0), cargado = true)
        }
        // Los fuegos de los otros ranchos de la compañía, a lo largo de la fila.
        val fuegoRancho = if (enPie) v.fuego.coerceAtLeast(if (noche) 0.6f else 0f) else v.fuego
        for ((i, pz) in ranchos(m).withIndex().reversed()) {
            val q = w(pz.first, pz.second); val s = PiezasNapoleon.sitio(MIRADA, q, i * 0.9f)
            hoguera(s, v.en(q), fuegoRancho, v.nieve, Semilla.de("napoleon", "vivac", "rancho", i), VivacNapoleon.lejos(pz.second))
            // La llama, quieta (va en la parte quieta para que los fusiles de delante la tapen): tres lenguas en capas.
            if (fuegoRancho > 0.4f) llamaQuieta(q + Offset(0f, -0.06f * s.k), s.k, fuegoRancho, v.noche, i)
        }
        // El polvo de la columna sobre el camino (de día, de marcha): masas que se levantan, doradas con el sol bajo.
        val polvo = VivacNapoleon.polvo(m)
        if (polvo > 0.02f) polvoDeLaColumna(v, polvo)
        // Los fuegos lejanos encendidos: un punto, su halo y su charco de luz en la hierba.
        for (f in fuegosDeHoy(m).asReversed()) {
            val kk = k(f.z); val r = (0.28f * kk).coerceIn(0.32f, 2.4f); val brillo = 0.55f + 0.45f * ((f.orden * 7919) % 100) / 100f
            drawOval(C.LUMBRE.copy(alpha = 0.14f * v.noche * brillo), Offset(f.p.x - r * 2.6f, f.p.y - r * 0.5f), Size(r * 5.2f, r * 1.1f), blendMode = BlendMode.Plus)
            drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB45E).copy(alpha = 0.42f * brillo), Color.Transparent), f.p + Offset(0f, -r), r * 2.2f), r * 2.2f, f.p + Offset(0f, -r), blendMode = BlendMode.Plus)
            drawCircle(lerp(Color(0xFFFFD27A), Color(0xFFFF8A3A), (f.z / 400f).coerceIn(0f, 1f)).copy(alpha = brillo), r * 0.55f, f.p + Offset(0f, -r * 0.5f))
        }
        // De día, los fuegos de los otros campamentos: la ceniza (y, por la mañana, un hilo de humo, en la parte viva).
        if (!noche && conCompania) for (f in FUEGOS_LEJANOS.take(120)) {
            if (f.z > 160f) continue
            val kk = k(f.z); drawOval(p.arriba(lerp(C.CENIZA, C.BARRO, 0.3f), VivacNapoleon.lejos(f.z)).copy(alpha = 0.75f), Offset(f.p.x - 0.5f * kk, f.p.y - 0.13f * kk), Size(kk, 0.26f * kk))
        }
    }

    /** Una llama quieta de un fuego a media distancia: tres lenguas en capas, del naranja al casi blanco. */
    private fun DrawScope.llamaQuieta(pie: Offset, k: Float, f: Float, noche: Float, semilla: Int) {
        val az = Azar(Semilla.de("napoleon", "vivac", "llama", semilla))
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB45E).copy(alpha = 0.35f * noche * f), Color.Transparent), pie + Offset(0f, -0.2f * k), 0.9f * k), 0.9f * k, pie + Offset(0f, -0.2f * k), blendMode = BlendMode.Plus)
        for (j in 0 until 3) {
            val u = (j - 1) * 0.12f * k; val alto = k * (0.32f + 0.1f * (1 - kotlin.math.abs(j - 1))) * f * (0.6f + 0.4f * noche) * az.entre(0.8f, 1.1f)
            for ((esc, col) in listOf(1f to Color(0xFFE0541C).copy(alpha = 0.7f), 0.7f to Color(0xFFFF9A30).copy(alpha = 0.9f), 0.4f to Color(0xFFFFE9AE))) {
                val p = pie + Offset(u, 0f); val w = alto * 0.26f * esc; val h = alto * esc
                drawPath(Path().apply { moveTo(p.x - w, p.y); cubicTo(p.x - w, p.y - h * 0.4f, p.x - w * 0.2f, p.y - h * 0.7f, p.x + w * 0.2f, p.y - h); cubicTo(p.x + w * 0.4f, p.y - h * 0.6f, p.x + w, p.y - h * 0.35f, p.x + w, p.y); close() }, col)
            }
        }
    }

    /** Un pabellón lejano: tres fusiles en pirámide con el brillo de las bayonetas, y dos mochilas al pie. */
    private fun DrawScope.pabellonLejano(v: VistaVivac, q: Offset, kk: Float) {
        val p = v.p; val lejos = VivacNapoleon.lejos(VivacNapoleon.MIRADA.focal / kk); val h = 1.45f * kk; val a = 0.38f * kk
        val col = p.medio(lerp(C.MADERA, C.HIERRO, 0.4f), lejos); val g = (0.05f * kk).coerceIn(0.3f, 1.6f)
        val top = q + Offset(0f, -h)
        drawLine(col, q + Offset(-a, 0f), top, g); drawLine(col, q + Offset(a, 0.05f * kk), top, g); drawLine(p.lado(lerp(C.MADERA, C.HIERRO, 0.4f), lejos), q + Offset(0.1f * kk, -0.08f * kk), top, g)
        drawLine(p.luz(C.PLATA, lejos), top, top + Offset(0f, -0.4f * kk), (g * 0.6f).coerceAtLeast(0.3f))
        drawRect(p.lado(C.PIEL, lejos), q + Offset(0.5f * kk, -0.3f * kk), Size(0.35f * kk, 0.3f * kk))
        drawRect(p.medio(C.CAPOTE, lejos), q + Offset(0.5f * kk, -0.38f * kk), Size(0.35f * kk, 0.09f * kk))
        // «La última luz del día» también en los cañones de lejos.
        val tarde = VivacLuz.ultimaLuz(v.l)
        if (tarde > 0.05f) drawLine(Color(0xFFFF9A5A).copy(alpha = 0.7f * tarde), q + Offset(a * 0.5f, -h * 0.5f), top, (g * 0.7f).coerceAtLeast(0.3f), blendMode = BlendMode.Plus)
    }

    /** Una tienda de oficial lejana, de lona, con su lado de luz y su puerta (de noche, encendida). */
    private fun DrawScope.tiendaLejana(v: VistaVivac, q: Offset, kk: Float) {
        val p = v.p; val lejos = VivacNapoleon.lejos(VivacNapoleon.MIRADA.focal / kk); val lado = if (p.solALaDerecha) 1f else -1f
        val w = 1.3f * kk; val h = 2.5f * kk; val fondo = 1.1f * kk
        val frente = poligono(q + Offset(-w, 0f), q + Offset(w, 0f), q + Offset(w, -h * 0.5f), q + Offset(0f, -h), q + Offset(-w, -h * 0.5f))
        val costado = poligono(listOf(q + Offset(w * lado, 0f), q + Offset(w * lado + fondo * lado, -fondo * 0.25f), q + Offset(w * lado + fondo * lado, -h * 0.5f - fondo * 0.25f), q + Offset(lado * fondo, -h - fondo * 0.25f), q + Offset(0f, -h), q + Offset(w * lado, -h * 0.5f)))
        drawPath(costado, p.luz(C.LONA, lejos))
        drawPath(frente, p.frente(C.LONA, lejos))
        val farol = VivacNapoleon.farol(v.m)
        drawPath(poligono(q + Offset(-w * 0.25f, 0f), q + Offset(w * 0.25f, 0f), q + Offset(0f, -h * 0.75f)), if (farol > 0.1f) lerp(p.sombra(C.CENIZA, lejos), C.LUMBRE, 0.8f * farol) else p.sombra(C.CENIZA, lejos))
        if (farol > 0.1f) drawPath(frente, Color(0xFFFFB45E).copy(alpha = 0.35f * farol), blendMode = BlendMode.Plus)
        if (v.nieve > 0.35f) drawLine(p.arriba(C.NIEVE, lejos), q + Offset(-w * 0.9f, -h * 0.55f), q + Offset(0f, -h), (0.12f * kk).coerceAtLeast(0.4f))
    }

    /**
     * El polvo de la columna: una calima baja y larga sobre el camino, que se levanta poco y se deshace a los lados; más
     * espesa lejos (donde va la cabeza de la columna) y dorada con el sol bajo, que se ve en el aire.
     */
    private fun DrawScope.polvoDeLaColumna(v: VistaVivac, k0: Float) {
        val l = v.l; val az = VivacNapoleon.fijo("polvo")
        val bajo = (1f - l.solAltura / 0.6f).coerceIn(0f, 1f)
        val color = lerp(l.tono(lerp(ARENA, C.LONA, 0.3f)), l.solColor, 0.25f + 0.4f * bajo)
        for (i in 0 until 34) {
            val z = 26f * exp(az.entre(0f, 2.7f)); val q = w(camino(z) + az.entre(-2f, 2f), z); val kk = k(z)
            if (q.x < -30f || q.x > ANCHO + 30f) continue
            mancha(q + Offset(0f, -0.7f * kk), az.entre(1.6f, 3.2f) * kk, az.entre(0.45f, 0.8f) * kk, color, (0.16f + 0.12f * bajo) * k0)
        }
        // La luz del sol en el polvo, del lado del sol: un velo cálido que se levanta un poco más.
        if (v.r.fuerza > 0.1f) for (i in 0 until 5) {
            val z = 34f * exp(i * 0.45f); val q = w(camino(z), z); val kk = k(z)
            mancha(q + Offset((if (v.p.solALaDerecha) 1f else -1f) * 1.2f * kk, -1.4f * kk), 2.6f * kk, 1.1f * kk, lerp(l.solColor, Color.White, 0.3f), 0.12f * k0 * (0.4f + bajo))
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: VistaVivac, reloj: Float) {
        val l = v.l; val m = v.m
        // El humo de los fuegos de los ranchos; de noche, tiñido por abajo.
        val noche = EscenaNapoleon.fuegosLejos(m)
        for ((i, pz) in ranchos(m).withIndex()) {
            val q = w(pz.first, pz.second); val kk = k(pz.second)
            val f = if (VivacNapoleon.pabellones(m) > 0) v.fuego.coerceAtLeast(if (noche) 0.6f else 0f) else v.fuego
            if (f > 0.02f) {
                humo(l, q + Offset(0f, -0.3f * kk), 26f * kk / 10f + 20f, 1.4f * kk, reloj, m.viento + 0.2f, 0.25f, VivacNapoleon.lejos(pz.second), i + 40, 5, 0.45f)
            }
        }
        // De mañana, los hilos de humo de la ceniza de los otros campamentos; al caer la tarde, los de sus fuegos.
        val h = m.hora.toFloat()
        val hilos = if (h in 4f..10f || h >= 17f || h < 4f) 8 else 0
        for ((i, f) in FUEGOS_LEJANOS.take(60).withIndex()) {
            if (i >= hilos) break
            humo(l, f.p, 18f + 300f / f.z, 1.2f + 30f / f.z, reloj, m.viento + 0.25f, 0.3f, VivacNapoleon.lejos(f.z), i + 60, 4, 0.35f)
        }
        // El polvo que levanta la columna, que pasa.
        val polvo = VivacNapoleon.polvo(m)
        if (polvo > 0.05f) {
            val az = VivacNapoleon.fijo("polvoVivo"); val color = lerp(l.tono(lerp(ARENA, C.LONA, 0.3f)), l.solColor, 0.4f)
            for (i in 0 until 6) {
                val z = 28f * exp(az.entre(0f, 2.2f)); val kk = k(z); val t = ((reloj * az.entre(0.03f, 0.06f) + az.real()) % 1f)
                val q = w(camino(z) + (t - 0.5f) * 8f, z) + Offset(0f, -0.6f * kk - t * 0.8f * kk)
                mancha(q, 2.2f * kk, 0.6f * kk, color, 0.14f * polvo * sin(t * 3.1416f))
            }
        }
    }

}
