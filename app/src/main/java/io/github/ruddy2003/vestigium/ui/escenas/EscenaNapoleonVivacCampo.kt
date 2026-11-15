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
import androidx.compose.ui.graphics.drawscope.clipRect
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
import kotlin.math.abs
import kotlin.math.cos
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

    // ---- Los colores del campo: los de [ColorVivac].
    private val V = ColorVivac

    /** El prado del campamento, de cerca: la hierba de la estación. */
    fun prado(v: VistaVivac): Color = when (v.m.ficha.estacion) {
        io.github.ruddy2003.vestigium.vidas.Estacion.PRIMAVERA -> V.PRADO
        io.github.ruddy2003.vestigium.vidas.Estacion.VERANO -> lerp(V.PRADO, V.PRADO_SECO, 0.22f)
        io.github.ruddy2003.vestigium.vidas.Estacion.OTONO -> lerp(V.PRADO_SECO, V.PRADO, 0.25f)
        else -> lerp(V.PRADO_SECO, C.BARRO, 0.3f)
    }

    /** El color de lo que cubre el llano hoy (el trigo, el rastrojo, la arena, las viñas, el prado). */
    private fun llano(v: VistaVivac): Color = when (v.campo) {
        Campo.HIERBA -> prado(v)
        Campo.TRIGO_VERDE -> V.TRIGO_VERDE
        Campo.TRIGO -> V.TRIGO
        Campo.RASTROJO -> V.RASTROJO
        Campo.ARENA -> V.ARENA
        Campo.VINA -> V.VINA
        Campo.NIEVE -> C.NIEVE
    }

    /**
     * El color propio del campo, de lejos ([lejos] = 1) a cerca: de cerca, el prado del campamento (en Polonia, la arena
     * con su hierba rala); lejos, lo que cubre el llano ese día. Con nieve, nieve, sucia de cerca.
     */
    fun colorDelCampo(v: VistaVivac, lejos: Float): Color {
        if (v.campo == Campo.NIEVE) return lerp(C.NIEVE_SUCIA, C.NIEVE, (lejos * 9f).coerceIn(0f, 1f))
        val cerca = if (v.campo == Campo.ARENA) lerp(V.ARENA, prado(v), 0.3f) else prado(v)
        return lerp(cerca, llano(v), ((lejos - 0.048f) * 40f).coerceIn(0f, 1f))
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
        if (v.fondo == Fondo.PUEBLO || v.fondo == Fondo.FRANCIA) vinas(v)
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
        val verde = V.VINA
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
        // El murete, de piedras sin argamasa, a lo largo de la diagonal (en el pueblo, donde no está la fila de fusiles).
        if (v.fondo != Fondo.PUEBLO) return
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
            Campo.TRIGO -> listOf(V.TRIGO, lerp(V.TRIGO, C.HOJA, 0.3f), V.RASTROJO, prado(v), V.TRIGO, V.BARBECHO)
            Campo.TRIGO_VERDE -> listOf(V.TRIGO_VERDE, V.PRADO, lerp(V.PRADO, V.TRIGO, 0.55f), V.BARBECHO, V.TRIGO_VERDE)
            Campo.RASTROJO -> listOf(V.RASTROJO, V.BARBECHO, V.PRADO_SECO, lerp(V.RASTROJO, V.TRIGO, 0.4f), prado(v))
            Campo.ARENA -> listOf(V.ARENA, lerp(V.ARENA, V.PRADO_SECO, 0.5f), V.PRADO_SECO, lerp(V.ARENA, V.BREZO, 0.35f), V.ARENA)
            Campo.VINA -> listOf(V.VINA, V.VINA, prado(v), V.TRIGO, V.BARBECHO)
            Campo.HIERBA, Campo.NIEVE -> listOf(prado(v), lerp(V.PRADO, V.PRADO_LUZ, 0.35f), V.PRADO_SECO, V.TRIGO_VERDE, V.BARBECHO)
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
                val esquinas = listOf(w(x, z0), w(x + ancho, z0), w(x + ancho * 1.1f, z1), w(x + ancho * 0.02f, z1))
                if (esquinas.maxOf { it.x } < -4f || esquinas.minOf { it.x } > ANCHO + 4f) { x += ancho; continue }
                val forma = poligono(esquinas)
                drawPath(forma, p.arriba(col, lejos))
                // La linde: una raya de hierba más oscura, o un surco.
                drawLine(p.medio(lerp(V.PRADO, C.PINO, 0.3f), lejos).copy(alpha = 0.8f), w(x, z0), w(x + ancho * 0.02f, z1), (0.5f * k(z0)).coerceIn(0.4f, 2.2f))
                // Los surcos del trigo o del arado, que se van al fondo.
                if (col != V.PRADO && col != V.BARBECHO && z0 < 160f) clipPath(forma) {
                    val n = (ancho / (if (col == V.VINA) 1.2f else 2f)).toInt().coerceIn(2, 12)
                    for (j in 1 until n) { val u = j / n.toFloat(); drawLine(if (col == V.VINA) p.medio(lerp(V.VINA, C.PINO, 0.45f), lejos) else p.sombra(col, lejos).copy(alpha = 0.4f), w(x + ancho * u, z0), w(x + ancho * (0.02f + 1.08f * u), z1), (0.08f * k(z0)).coerceIn(0.25f, 0.9f)) }
                }
                x += ancho
            }
        }
        // Las matas y los setos sueltos del llano, con su sombrita, para que no sea un tapiz.
        repeat(28) {
            val z = 22f * exp(az.entre(0f, 2.6f)); val x = az.entre(-30f, 30f) * (z / 30f); val q = w(x, z)
            if (q.x < -5f || q.x > ANCHO + 5f) return@repeat
            val kk = k(z); val lejos = VivacNapoleon.lejos(z)
            sombraCorta(v.r, q, 1.4f * kk, 0.9f * kk)
            drawOval(p.medio(lerp(V.PRADO, C.PINO, 0.45f), lejos), Offset(q.x - 0.7f * kk, q.y - 0.9f * kk), Size(1.4f * kk, 0.95f * kk))
            drawOval(p.lado(lerp(V.PRADO, V.PRADO_LUZ, 0.3f), lejos).copy(alpha = 0.6f), Offset(q.x + (if (p.solALaDerecha) 0f else -0.6f) * kk, q.y - 0.88f * kk), Size(0.6f * kk, 0.5f * kk))
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
                drawLine(p.sombra(V.ARENA, VivacNapoleon.lejos(zEn(q.y))).copy(alpha = 0.4f), q, q + Offset(az.entre(0.6f, 1.6f) * kk, az.entre(-0.05f, 0.05f) * kk), (0.04f * kk).coerceIn(0.3f, 1f), StrokeCap.Round)
            }
            else -> Unit
        }
        // El prado del campamento: pinceladas de hierba más seca y más verde que rompen el degradado (regla 7), la paja
        // dorada que se esparce alrededor del rancho y de la fila, y las sendas pisadas, claras, del campamento al camino.
        if (v.campo != Campo.NIEVE) {
            val pr = prado(v)
            repeat(14) {
                val y = az.entre(150f, ALTO); val q = Offset(az.entre(-20f, ANCHO + 20f), y); val kk = k(zEn(y))
                drawPath(TrazosValle.blando(q, az.entre(1.6f, 3.6f) * kk, az.entre(0.25f, 0.55f) * kk, az, 9, 0.25f), p.arriba(if (it % 2 == 0) lerp(pr, V.PRADO_LUZ, 0.5f) else lerp(pr, C.PINO, 0.22f), VivacNapoleon.lejos(zEn(y))).copy(alpha = 0.38f))
            }
            for ((x0, z0, x1, z1) in listOf(floatArrayOf(2.4f, 6f, 9.5f, 26f), floatArrayOf(-0.4f, 9f, 6.8f, 34f), floatArrayOf(4.5f, 5.5f, 1.6f, 7.4f))) {
                val eje = (0..12).map { i -> val t = i / 12f; w(x0 + (x1 - x0) * t + 0.3f * sin(t * 9f), z0 + (z1 - z0) * t) }
                val zs = (0..12).map { i -> z0 + (z1 - z0) * i / 12f }
                val tierra = p.arriba(V.TIERRA, VivacNapoleon.lejos((z0 + z1) / 2f))
                drawPath(TrazosValle.cinta(eje, zs.map { 0.42f * k(it) }), tierra.copy(alpha = 0.42f))
                drawPath(TrazosValle.cinta(eje, zs.map { 0.2f * k(it) }), tierra.copy(alpha = 0.5f))
            }
            // La paja dorada: briznas sueltas alrededor del rancho, de las camas y al pie de la fila.
            for ((cx, cz) in listOf(1.2f to 6.4f, -0.4f to 7.8f, 0.6f to 11f, 2.5f to 9f)) repeat(26) {
                val q = w(cx + az.entre(-1.2f, 1.2f), cz + az.entre(-0.8f, 0.8f)); val kk = k(zEn(q.y)); val ang = az.entre(-0.7f, 0.7f); val l = az.entre(0.12f, 0.3f) * kk
                drawLine(p.arriba(if (it % 3 == 0) lerp(V.PAJA, C.LONA, 0.3f) else V.PAJA), q, q + Offset(cos(ang) * l, sin(ang) * l * 0.25f), (0.014f * kk).coerceIn(0.35f, 1.3f), StrokeCap.Round)
            }
            // Las flores del campo, en manchas que se ven: amapolas y acianos en el prado y en las lindes del trigo
            // (**[Aproximación]**), de mayo a agosto; en la arena de Polonia y del Niemen, el brezo.
            val mes = v.m.ficha.dia.substring(5, 7).toInt()
            if (mes in 5..8) {
                val manchas = listOf(-3.8f to 9.5f, 3.6f to 12f, 0.8f to 17f, 6.5f to 22f, -1.5f to 26f, 9f to 30f, 3f to 40f, -6f to 46f)
                val brezo = v.campo == Campo.ARENA
                for ((mi, mz) in manchas.withIndex()) {
                    val (mx, zc) = mz
                    repeat(if (zc < 15f) 28 else 18) {
                        val q = w(mx + az.entre(-1.4f, 1.4f) * (zc / 12f), zc + az.entre(-1.5f, 1.5f) * (zc / 12f)); val kk = k(zEn(q.y))
                        if (q.x < -4f || q.x > ANCHO + 4f) return@repeat
                        val r = (0.055f * kk).coerceIn(0.45f, 2.4f)
                        val col = if (brezo) V.BREZO else if ((it + mi) % 4 == 0) V.ACIANO else V.AMAPOLA
                        drawCircle(p.luz(col, VivacNapoleon.lejos(zEn(q.y))), r, q + Offset(0f, -0.18f * kk))
                        if (r > 1.2f) drawCircle(p.sombra(col, 0f), r * 0.45f, q + Offset(0f, -0.18f * kk + r * 0.3f))
                    }
                }
            }
        }
        // Las matas: en el prado del campamento, mechones de hierba con su lado de luz; más allá de la linde (a 13 m), lo
        // del campo: las cañas del trigo con su espiga, el rastrojo corto y claro, la hierba rala de la arena.
        if (v.campo != Campo.NIEVE) {
            val color = prado(v); val cultivo = llano(v)
            repeat(240) {
                val y = 285f - 150f * az.real() * az.real(); val q = Offset(az.entre(-6f, ANCHO + 6f), y); val z = zEn(y); val kk = k(z); val lejos = VivacNapoleon.lejos(z)
                if (z < 13.5f || v.campo == Campo.HIERBA || v.campo == Campo.VINA) {
                    val alto = az.entre(0.07f, 0.22f) * kk; if (alto < 0.8f) return@repeat
                    for (j in 0 until 4) { val inc = az.entre(-0.35f, 0.35f) * alto; drawLine(if (j % 2 == 0) p.lado(lerp(color, V.PRADO_LUZ, 0.4f), lejos) else p.medio(lerp(color, C.PINO, 0.25f), lejos), q + Offset(az.entre(-0.05f, 0.05f) * kk, 0f), q + Offset(inc, -alto), (0.014f * kk).coerceIn(0.3f, 1.1f), StrokeCap.Round) }
                } else when (v.campo) {
                    Campo.TRIGO, Campo.TRIGO_VERDE -> {
                        val alto = az.entre(0.7f, 0.95f) * kk; if (alto < 1.2f) return@repeat
                        for (j in 0 until 3) { val inc = az.entre(-0.08f, 0.08f) * alto; val top = q + Offset(inc + az.entre(-0.1f, 0.1f) * kk, -alto)
                            drawLine(p.medio(lerp(cultivo, C.HOJA, 0.2f), lejos), q + Offset(az.entre(-0.08f, 0.08f) * kk, 0f), top, (0.012f * kk).coerceIn(0.3f, 0.9f))
                            drawLine(p.lado(lerp(cultivo, C.LONA, 0.15f), lejos), top, top + Offset(inc * 0.2f, -0.09f * kk), (0.03f * kk).coerceIn(0.5f, 2f), StrokeCap.Round) }
                    }
                    Campo.RASTROJO -> { val alto = 0.1f * kk; for (j in 0 until 5) { val b = q + Offset((j - 2) * 0.05f * kk, 0f); drawLine(p.lado(lerp(cultivo, C.LONA, 0.2f), lejos), b, b + Offset(az.entre(-0.02f, 0.02f) * kk, -alto), (0.012f * kk).coerceIn(0.3f, 0.9f)) } }
                    Campo.ARENA -> if (az.real() < 0.35f) { val alto = az.entre(0.08f, 0.2f) * kk; for (j in 0 until 3) drawLine(p.lado(lerp(V.PRADO_SECO, C.LONA, 0.15f), lejos), q, q + Offset(az.entre(-0.3f, 0.3f) * alto, -alto), (0.012f * kk).coerceIn(0.3f, 0.9f), StrokeCap.Round) }
                    Campo.HIERBA, Campo.VINA, Campo.NIEVE -> Unit
                }
            }
            // La linde entre el prado del campamento y el campo: la hierba alta y el borde de las cañas, roto, a 13 m.
            if (v.campo == Campo.TRIGO || v.campo == Campo.TRIGO_VERDE || v.campo == Campo.RASTROJO) {
                val borde = (0..30).map { i -> val x = -14f + 30f * i / 30f; w(x + az.entre(-0.3f, 0.3f), 13.2f + az.entre(-0.5f, 0.6f)) }
                val alto = if (v.campo == Campo.RASTROJO) 0.12f else 0.85f
                val masa = poligono(borde + borde.reversed().map { it + Offset(0f, -alto * k(13.2f) * az.entre(0.85f, 1.1f)) })
                drawPath(masa, p.medio(lerp(cultivo, C.HOJA, 0.15f), 0.05f))
                drawPath(poligono(borde.map { it + Offset(0f, -alto * k(13.2f) * 0.75f) } + borde.reversed().map { it + Offset(0f, -alto * k(13.2f)) }), p.lado(lerp(cultivo, C.LONA, 0.2f), 0.05f))
            }
        }
    }

    /** El camino real, con sus rodadas y la tierra de las orillas; con nieve, pisado y sucio. */
    private fun DrawScope.caminoReal(v: VistaVivac) {
        // El día del Niemen el camino llega a los puentes y sigue del otro lado: no pasa por encima del agua.
        if (v.fondo == Fondo.NIEMEN) {
            clipRect(-10f, 142.5f, ANCHO + 10f, ALTO + 10f) { caminoRealSinRio(v) }
            clipRect(-10f, H - 2f, ANCHO + 10f, 127.2f) { caminoRealSinRio(v) }
        } else caminoRealSinRio(v)
    }

    private fun DrawScope.caminoRealSinRio(v: VistaVivac) {
        val p = v.p; val forma = caminoForma()
        val tierra = if (v.nieve > 0.5f) C.NIEVE_SUCIA else if (v.campo == Campo.ARENA) lerp(V.ARENA, C.LONA, 0.3f) else V.CAMINO
        drawPath(forma, Brush.verticalGradient(listOf(p.arriba(tierra, 0.85f), p.arriba(tierra, 0.4f)), H, 160f))
        clipPath(forma) {
            for (dx in listOf(-1.6f, -0.9f, 0.9f, 1.6f)) {
                val pts = listOf(18f, 24f, 32f, 44f, 62f, 90f, 140f, 240f, 600f).map { z -> w(camino(z) + dx, z) }
                for (i in 0 until pts.size - 1) drawLine(p.sombra(tierra, 0.6f).copy(alpha = 0.55f), pts[i], pts[i + 1], (0.12f * k(zEn(pts[i].y))).coerceIn(0.3f, 1.2f))
            }
        }
        // Las orillas: hierba más alta y pisada, una franja de paso entre el camino y el campo (regla 9).
        val az = VivacNapoleon.fijo("orillas")
        for (lado in listOf(-1f, 1f)) for (i in 0 until 30) {
            val z = 20f * exp(az.entre(0f, 2.8f)); val q = w(camino(z) + lado * (CAMINO_ANCHO + az.entre(-0.2f, 0.4f)), z); val kk = k(z)
            if (q.x < -4f || q.x > ANCHO + 4f) continue
            drawOval(p.medio(if (v.nieve > 0.5f) C.NIEVE_SOMBRA else lerp(V.PRADO, C.PINO, 0.2f), VivacNapoleon.lejos(z)).copy(alpha = 0.5f), Offset(q.x - 0.7f * kk, q.y - 0.1f * kk), Size(1.4f * kk, 0.2f * kk))
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
            // El agua: honda y verde azulada bajo la otra orilla (donde se refleja el bosque), azul del cielo hacia acá,
            // con los brillos tendidos del sol, más del lado del sol.
            val hondo = p.sombra(lerp(C.PINO, C.AZUL, 0.45f), 0.3f); val cielo = lerp(p.medio(lerp(C.AZUL, C.PLATA, 0.35f), 0.25f), l.medio, 0.3f)
            drawPath(rio, Brush.verticalGradient(listOf(hondo, lerp(hondo, cielo, 0.6f), cielo), 126f, 141f))
            clipPath(rio) {
                drawRect(Brush.verticalGradient(listOf(mult(hondo, C.SOMBRA, 0.5f).copy(alpha = 0.8f), Color.Transparent), 126f, 130.5f), Offset(0f, 125f), Size(ANCHO, 6f))
                val sol = l.solX * ANCHO; val brillo = lerp(l.bajo, Color.White, 0.55f)
                repeat(36) { val q = Offset(az.entre(0f, ANCHO), az.entre(129f, 141f)); val cerca = (1f - abs(q.x - sol) / 200f).coerceIn(0.2f, 1f)
                    drawLine(brillo.copy(alpha = 0.55f * cerca * (0.4f + 0.6f * (1f - v.l.noche))), q, q + Offset(az.entre(2f, 9f) * (q.y - 120f) / 14f, 0f), 0.45f) }
            }
            // Los puentes de barcas, donde el camino llega al río y a su derecha: la fila de barcas negras, con su
            // reflejo, y el tablero claro encima, con su baranda, de orilla a orilla.
            for ((i, x) in listOf(198f, 268f, 338f).withIndex()) {
                val y0 = TrazosValle.alturaEn(alla, x); val y1 = TrazosValle.alturaEn(aca, x + 10f)
                val a0 = Offset(x, y0); val a1 = Offset(x + 10f + i * 2f, y1)
                val n = 7
                for (j in 0..n) { val q = a0 + (a1 - a0) * (j / n.toFloat()); val t = 0.5f + 1.3f * j / n
                    drawLine(p.sombra(C.MADERA_OSC, 0.4f).copy(alpha = 0.5f), q + Offset(0f, 0.4f * t), q + Offset(0f, 1.6f * t), 2.4f * t)
                    drawOval(p.sombra(C.MADERA_OSC, 0.4f), Offset(q.x - 2.4f * t, q.y - 0.45f * t), Size(4.8f * t, 1.2f * t)) }
                drawLine(p.sombra(C.MADERA_OSC, 0.4f), a0 + Offset(0f, -0.4f), a1 + Offset(0f, -0.9f), 2.6f)
                drawLine(p.luz(C.MADERA_GRIS, 0.4f), a0 + Offset(0f, -0.9f), a1 + Offset(0f, -1.8f), 1.8f)
                drawLine(p.medio(C.MADERA_OSC, 0.4f), a0 + Offset(1.6f, -1.8f), a1 + Offset(3.6f, -3.6f), 0.4f)
            }
        }
        // La orilla de acá: la arena y el talud, que hacen de franja de paso entre el agua y el campo.
        drawPath(TrazosValle.cerrada(aca.map { it + Offset(0f, -0.4f) } + aca.reversed().map { it + Offset(0f, 2.6f) }), p.arriba(if (helado) C.NIEVE else lerp(V.ARENA, C.LONA, 0.2f), 0.5f))
    }

    // ---------------------------------------------------------------- Los campamentos del ejército

    /** Una fila de campamento: su desplazamiento (en metros) respecto a la nuestra y desde dónde se ve. */
    private class Fila(val dx: Float, val desde: Float)

    /**
     * Las filas del ejército, paralelas a la de la compañía y con su mismo punto de fuga: la nuestra sigue más allá del
     * camino (las otras compañías del batallón); a la izquierda, detrás de la tienda, los otros batallones; a la derecha,
     * del otro lado del camino, otro regimiento. **[Diseño propio]**: el orden del campamento.
     */
    private val FILAS = listOf(Fila(0f, 52f), Fila(-9f, 34f), Fila(-18f, 48f), Fila(-30f, 64f), Fila(-46f, 90f), Fila(-66f, 130f), Fila(8f, 44f), Fila(16f, 64f), Fila(26f, 96f), Fila(38f, 140f))

    /** Si algo del campamento lejano en [q] se ve: dentro del cuadro y no detrás de la tienda (que tapa la izquierda hasta su costado). */
    private fun seVe(q: Offset): Boolean = q.x < ANCHO + 4f && q.x > (if (q.y < 122.5f) 108f else 128f)

    /** Un fuego lejano: dónde (en el lienzo), a qué profundidad, y su orden (los primeros se encienden antes). */
    class FuegoLejano(val p: Offset, val z: Float, val orden: Int)

    /**
     * Los fuegos de los campamentos: uno por rancho a lo largo de cada fila (así se leen las filas que se van al punto de
     * fuga), y más allá de la última, hasta el horizonte, los de otros cuerpos, puntos sueltos. En orden de encendido, al
     * azar: con menos fuegos, el campamento entero se ralea.
     */
    val FUEGOS_LEJANOS: List<FuegoLejano> by lazy {
        val az = VivacNapoleon.fijo("fuegosLejanos"); val lista = ArrayList<FuegoLejano>()
        for (f in FILAS) {
            var z = f.desde + az.entre(0f, 3f)
            while (z < 340f) {
                val x = VivacCerca.filaEn(z) + f.dx - VivacCerca.DETRAS + az.entre(-0.4f, 0.4f)
                val q = w(x, z)
                if (seVe(q) && !enElCamino(x, z)) lista += FuegoLejano(q, z, az.entero(100000))
                z += 3.8f * az.entre(0.85f, 1.2f) * (1f + z / 90f)
            }
        }
        repeat(70) { val z = 340f * exp(az.entre(0f, 0.9f)); val x = az.entre(-0.5f, 1.4f) * z * 0.34f; val q = w(x, z); if (seVe(q) && !enElCamino(x, z)) lista += FuegoLejano(q, z, az.entero(100000)) }
        lista.sortedBy { it.orden }
    }

    private fun enElCamino(x: Float, z: Float): Boolean = kotlin.math.abs(x - camino(z)) < CAMINO_ANCHO + 1.2f

    /** El día del Niemen, lo que caería en el agua (de 40 a 122 m): ahí no se pinta campamento, para que se vea el río. */
    private fun enElRio(v: VistaVivac, z: Float): Boolean = v.fondo == Fondo.NIEMEN && z in 40f..122f

    /** Los fuegos lejanos de hoy (los primeros [VivacNapoleon.fuegosDelEjercito], con los huecos de la compañía mermada). */
    fun fuegosDeHoy(m: Momento): List<FuegoLejano> {
        val n = VivacNapoleon.fuegosDelEjercito(m); if (n <= 0) return emptyList()
        val hueco = 1f - VivacNapoleon.fuegosDeLaCompania(m) / 17f
        val az = VivacNapoleon.fijo("huecos")
        return FUEGOS_LEJANOS.filter { az.real() >= hueco * 0.7f }.take(n)
    }

    /** Los fuegos de los otros ranchos de la compañía, a lo largo de la fila (sin el nuestro, que está en la base). */
    fun ranchos(m: Momento): List<Pair<Float, Float>> = VivacCerca.FUEGOS.drop(1).take((VivacNapoleon.fuegosDeLaCompania(m) - 1).coerceAtLeast(0))

    /** Lo que tumba sombra en el campo: las tiendas y los carros del parque (lo que se pinta: con la compañía, lo que se ve). */
    fun sombras(v: VistaVivac): List<VivacLuz.Sombra> = buildList {
        if (VivacNapoleon.sinCompania(v.m)) return@buildList
        if (v.fondo != Fondo.NIEMEN) for (s in carros()) add(VivacLuz.Sombra(s.pie, poligono(s.p(-1.1f, 0f, 0f), s.p(1.1f, 0f, 0f), s.p(0.9f, 2.2f, 0f), s.p(-0.9f, 2.2f, 0f))))
        for ((q, kk) in tiendas()) if (seVe(q) && !enElRio(v, MIRADA.focal / kk)) add(VivacLuz.Sombra(q, poligono(q + Offset(-1.3f * kk, 0f), q + Offset(1.3f * kk, 0f), q + Offset(0f, -2.4f * kk))))
    }

    /** Los carros del bagaje, aparcados a la derecha entre la fila y el camino (esperan para salir detrás de la columna). */
    private fun carros(): List<Sitio> = listOf(w(6.6f, 17f) to 0.9f, w(8.8f, 21f) to 1.1f, w(5.2f, 25f) to 0.7f).map { (q, g) -> PiezasNapoleon.sitio(MIRADA, q, g) }

    /** Las tiendas de los oficiales de las otras compañías (en el lienzo, con su escala), del lado de la fila que se aleja del punto de fuga: así no tapan los fuegos de más allá. */
    private fun tiendas(): List<Pair<Offset, Float>> {
        val lista = ArrayList<Pair<Offset, Float>>()
        for (f in FILAS) { var z = f.desde + 6f; while (z < 300f) { val x = VivacCerca.filaEn(z) + f.dx + (if (f.dx > 0f) 4.5f else -4.5f); val q = w(x, z); if (q.x > -10f && q.x < ANCHO + 10f && !enElCamino(x, z)) lista += q to k(z); z += 34f * (1f + z / 200f) } }
        return lista
    }

    /** El parque de cañones, más allá del camino: los cañones en fila con la boca hacia el campo. */
    private fun canones(): List<Sitio> = listOf(66f, 70f, 74f, 78f).map { z -> PiezasNapoleon.sitio(MIRADA, w(camino(z) + 9f, z), -1.2f) }

    fun DrawScope.campamentos(v: VistaVivac) {
        val m = v.m; val p = v.p
        val conCompania = !VivacNapoleon.sinCompania(m)
        val enPie = VivacNapoleon.pabellones(m) > 0
        val noche = EscenaNapoleon.fuegosLejos(m)
        // Los sotos del llano, a media distancia: lo de cada tierra (pinos, árboles de hoja, álamos), delante de la lejanía.
        arboledas(v)
        // De día, los fuegos de los otros campamentos: la ceniza en el suelo (y, por la mañana, un hilo de humo, en la parte viva).
        if (!noche && conCompania) for (f in FUEGOS_LEJANOS.take(120)) {
            if (f.z > 160f || enElRio(v, f.z)) continue
            val kk = k(f.z); drawOval(p.arriba(lerp(C.CENIZA, C.BARRO, 0.3f), VivacNapoleon.lejos(f.z)).copy(alpha = 0.75f), Offset(f.p.x - 0.5f * kk, f.p.y - 0.13f * kk), Size(kk, 0.26f * kk))
        }
        // De noche, encima del campamento, el resplandor de todos los fuegos en la calima.
        val encendidos = fuegosDeHoy(m).filter { !enElRio(v, it.z) }
        if (encendidos.isNotEmpty()) {
            val cuanto = (encendidos.size / 300f).coerceIn(0.3f, 1f) * v.noche
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, C.LUMBRE.copy(alpha = 0.16f * cuanto)), H - 24f, H + 1f), Offset(108f, H - 24f), Size(ANCHO - 108f, 25f), blendMode = BlendMode.Plus)
        }
        // Los pabellones de las filas, de lejos a cerca (de noche, solo los de menos de 110 m: los de más allá no se ven, y
        // los fuegos se ven a través de los fusiles).
        if (conCompania && enPie) {
            val hasta = if (noche) 110f else 420f
            val pabellones = ArrayList<Pair<Float, Offset>>()
            for ((fi, f) in FILAS.withIndex()) {
                var z = f.desde; val az = VivacNapoleon.fijo("pabellonesLejos", fi)
                while (z < hasta) { val x = VivacCerca.filaEn(z) + f.dx + az.entre(-0.1f, 0.1f); val q = w(x, z); if (seVe(q) && !enElCamino(x, z) && !enElRio(v, z)) pabellones += z to q; z += 1.9f * (1f + z / 120f) }
            }
            for ((z, q) in pabellones.sortedByDescending { it.first }) pabellonLejano(v, q, k(z))
        }
        // Luego, por su distancia: las tiendas, los cañones del parque y los fuegos.
        val cosas = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        if (conCompania) {
            for ((i, t) in tiendas().withIndex()) { val (q, kk) = t; val z = MIRADA.focal / kk; if (seVe(q) && !enElRio(v, z)) cosas += z to { tiendaLejana(v, q, kk, i % 3 != 1) } }
            if (v.fondo != Fondo.NIEMEN) for (s in canones()) cosas += s.z0 to { canon(s, v.en(s.pie), 0f, v.nieve, VivacNapoleon.lejos(s.z0)) }
        }
        val humo = lerp(v.l.tono(lerp(C.CENIZA, C.CAPOTE, 0.5f)), C.LUMBRE, 0.35f)
        for (f in encendidos) cosas += (f.z - 0.5f) to { fuegoLejano(v, f, humo) }
        for ((_, pintar) in cosas.sortedByDescending { it.first }) pintar()
        // Los carros del bagaje, delante de todo eso (el día del Niemen no: ya pasaron a la otra orilla, y el río se ve entero).
        if (conCompania && v.fondo != Fondo.NIEMEN) for (s in carros().sortedBy { it.pie.y }) carro(s, v.en(s.pie), v.nieve, Semilla.de("napoleon", "vivac", "carro", s.pie.x.toInt()), VivacNapoleon.lejos(s.z0), cargado = true)
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
    }

    /**
     * Un fuego lejano encendido: hasta el horizonte (a más de 170 m), un punto; más cerca, con su halo y su charco de luz
     * en la hierba; a menos de 95 m, con su llama y, dos de cada tres, la columna de humo encendida por abajo.
     */
    private fun DrawScope.fuegoLejano(v: VistaVivac, f: FuegoLejano, humo: Color) {
        val kk = k(f.z); val r = (0.28f * kk).coerceIn(0.32f, 2.4f); val brillo = 0.55f + 0.45f * (f.orden % 100) / 100f
        val color = lerp(Color(0xFFFFD27A), Color(0xFFFF8A3A), (f.z / 400f).coerceIn(0f, 1f))
        if (f.z > 170f) { drawCircle(color.copy(alpha = brillo), (r * 0.6f).coerceAtLeast(0.42f), f.p + Offset(0f, -r * 0.4f)); return }
        val cerca = f.z < 95f
        if (cerca && f.orden % 3 != 0) {
            val alto = (5f * kk).coerceIn(7f, 30f); val ancho = (0.8f * kk).coerceIn(1.6f, 6f)
            drawOval(Brush.verticalGradient(listOf(Color.Transparent, humo.copy(alpha = 0.22f * v.noche), lerp(humo, C.LUMBRE, 0.5f).copy(alpha = 0.4f * v.noche * brillo)), f.p.y - alto, f.p.y - r), Offset(f.p.x - ancho / 2f + 0.3f * ancho, f.p.y - alto), Size(ancho, alto - r))
        }
        drawOval(C.LUMBRE.copy(alpha = (if (cerca) 0.3f else 0.16f) * v.noche * brillo), Offset(f.p.x - r * 3.4f, f.p.y - r * 0.55f), Size(r * 6.8f, r * 1.3f), blendMode = BlendMode.Plus)
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB45E).copy(alpha = 0.42f * brillo), Color.Transparent), f.p + Offset(0f, -r), r * 2.2f), r * 2.2f, f.p + Offset(0f, -r), blendMode = BlendMode.Plus)
        drawCircle(color.copy(alpha = brillo), r * 0.55f, f.p + Offset(0f, -r * 0.5f))
        if (cerca) {
            val h = r * 1.8f
            drawPath(Path().apply { moveTo(f.p.x - r * 0.4f, f.p.y); cubicTo(f.p.x - r * 0.4f, f.p.y - h * 0.5f, f.p.x - r * 0.1f, f.p.y - h * 0.7f, f.p.x + r * 0.1f, f.p.y - h); cubicTo(f.p.x + r * 0.2f, f.p.y - h * 0.6f, f.p.x + r * 0.4f, f.p.y - h * 0.4f, f.p.x + r * 0.4f, f.p.y); close() }, Color(0xFFFFB050).copy(alpha = 0.9f * brillo))
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

    /** Qué árbol crece en el llano de cada tramo. */
    private enum class Arbol { PINO, HOJA, ALAMO }

    private fun arbolDe(f: Fondo): Arbol = when (f) {
        Fondo.POLONIA, Fondo.NIEMEN, Fondo.RUSIA, Fondo.SMOLENSK, Fondo.BORODINO, Fondo.MOSCU, Fondo.PRUSIA, Fondo.HOSPITAL -> Arbol.PINO
        Fondo.ALEMANIA, Fondo.RIN, Fondo.CUARTEL -> Arbol.HOJA
        Fondo.FRANCIA, Fondo.CIEN_DIAS, Fondo.PUEBLO -> Arbol.ALAMO
    }

    /**
     * Los sotos del llano: grupos de tres a seis árboles a media distancia (de 80 a 230 m), a la derecha del camino y,
     * uno, entre la tienda y la fila; cada árbol con su lado de luz y su sombra corta. Hacen de «algo delante» de la
     * lejanía (regla 10) y dicen la tierra del tramo: pinos albares en Polonia y Rusia, árboles de hoja en Alemania,
     * álamos en Francia y en Bélgica **[Aproximación]**.
     */
    private fun DrawScope.arboledas(v: VistaVivac) {
        val tipo = arbolDe(v.fondo); val az = VivacNapoleon.fijo("sotos", v.fondo.ordinal); val p = v.p; val lado = if (p.solALaDerecha) 1f else -1f
        val desnudo = v.m.ficha.estacion == io.github.ruddy2003.vestigium.vidas.Estacion.INVIERNO || v.nieve > 0.4f
        val sotos = listOf(Triple(232f, 135f, 4), Triple(364f, 165f, 4), Triple(150f, 150f, 3))
        for ((sx, z0, n) in sotos.sortedByDescending { it.second }) {
            val arboles = (0 until n).map { j -> val z = z0 * az.entre(0.92f, 1.12f); val x = (sx + az.entre(-12f, 12f) + (j - n / 2f) * 6f - MIRADA.centro) * z / MIRADA.focal; x to z }
            for ((x, z) in arboles.sortedByDescending { it.second }) {
                if (enElCamino(x, z)) continue
                val q = w(x, z); val kk = k(z); val lejos = VivacNapoleon.lejos(z)
                val alto = when (tipo) { Arbol.PINO -> az.entre(13f, 19f); Arbol.HOJA -> az.entre(9f, 14f); Arbol.ALAMO -> az.entre(15f, 21f) } * kk
                sombraCorta(v.r, q, alto * 0.45f, alto * 0.6f)
                when (tipo) {
                    Arbol.PINO -> PiezasNapoleon.run { pinoAlbar(v.en(q), q, alto, alto * az.entre(0.3f, 0.38f), v.nieve * 0.8f, Azar(az.entero(100000).toLong()), lejos, (kk / 10f).coerceIn(0.25f, 0.8f)) }
                    Arbol.HOJA, Arbol.ALAMO -> {
                        val tronco = lerp(C.MADERA, C.CAPOTE, 0.35f)
                        drawLine(p.medio(tronco, lejos), q, q + Offset(0f, -alto * 0.45f), (alto * 0.04f).coerceAtLeast(0.5f))
                        val hoja = if (v.m.ficha.estacion == io.github.ruddy2003.vestigium.vidas.Estacion.OTONO) lerp(C.HOJA, C.LATON, 0.3f) else lerp(C.PINO, C.HOJA, if (tipo == Arbol.ALAMO) 0.38f else 0.45f)
                        if (desnudo) {
                            // Desnudo: la copa es una neblina de ramillas (un huso en el álamo, una bola en el de hoja) con
                            // las ramas gruesas que se ven dentro.
                            val ancho = if (tipo == Arbol.ALAMO) 0.12f else 0.3f
                            val huso = TrazosValle.cerrada(listOf(q + Offset(-alto * ancho * 0.6f, -alto * 0.3f), q + Offset(-alto * ancho, -alto * 0.6f), q + Offset(-alto * ancho * 0.4f, -alto * 0.95f), q + Offset(0f, -alto), q + Offset(alto * ancho * 0.45f, -alto * 0.94f), q + Offset(alto * ancho, -alto * 0.6f), q + Offset(alto * ancho * 0.6f, -alto * 0.3f)))
                            drawPath(huso, p.medio(lerp(tronco, C.CAPOTE, 0.3f), lejos).copy(alpha = 0.4f))
                            for (j in 0 until 7) { val a0 = q + Offset(0f, -alto * (0.32f + 0.08f * j)); val sg = if (j % 2 == 0) -1f else 1f
                                drawLine(p.medio(tronco, lejos).copy(alpha = 0.75f), a0, a0 + Offset(sg * alto * ancho * 0.7f, -alto * (if (tipo == Arbol.ALAMO) 0.3f else 0.2f)), (alto * 0.008f).coerceAtLeast(0.3f)) }
                        }
                        else if (tipo == Arbol.ALAMO) {
                            val copa = TrazosValle.cerrada(listOf(q + Offset(-alto * 0.09f, -alto * 0.22f), q + Offset(-alto * 0.13f, -alto * 0.6f), q + Offset(-alto * 0.04f, -alto), q + Offset(alto * 0.06f, -alto * 0.95f), q + Offset(alto * 0.13f, -alto * 0.55f), q + Offset(alto * 0.08f, -alto * 0.22f)))
                            drawPath(copa, p.medio(hoja, lejos))
                            drawPath(TrazosValle.cerrada(listOf(q + Offset(lado * alto * 0.02f, -alto * 0.3f), q + Offset(lado * alto * 0.11f, -alto * 0.55f), q + Offset(lado * alto * 0.03f, -alto * 0.9f))), p.lado(lerp(hoja, C.LONA, 0.15f), lejos).copy(alpha = 0.7f))
                        } else for (j in 0 until 5) {
                            val c = q + Offset(az.entre(-0.22f, 0.22f) * alto, -alto * az.entre(0.55f, 0.85f)); val r = alto * az.entre(0.18f, 0.27f)
                            drawCircle(p.medio(lerp(hoja, C.PINO_OSC, 0.25f), lejos), r, c)
                            drawCircle(p.lado(lerp(hoja, C.LONA, 0.12f), lejos).copy(alpha = 0.75f), r * 0.6f, c + Offset(lado * r * 0.3f, -r * 0.3f))
                        }
                    }
                }
            }
        }
    }

    /** Un pabellón lejano: tres fusiles en pirámide con el brillo de las bayonetas, y dos mochilas al pie. */
    private fun DrawScope.pabellonLejano(v: VistaVivac, q: Offset, kk: Float) {
        val p = v.p; val lejos = VivacNapoleon.lejos(VivacNapoleon.MIRADA.focal / kk); val h = 1.45f * kk; val a = 0.38f * kk
        val col = p.medio(lerp(C.MADERA, C.HIERRO, 0.4f), lejos); val g = (0.05f * kk).coerceIn(0.3f, 1.6f)
        val top = q + Offset(0f, -h)
        // Lejos (a más de 110 m, menos de 8 px), una uve con su bayoneta: lo demás no se ve.
        if (kk < 5.1f) { drawLine(col, q + Offset(-a, 0f), top, g); drawLine(col, q + Offset(a, 0f), top, g); drawLine(p.luz(C.PLATA, lejos), top, top + Offset(0f, -0.4f * kk), 0.3f); return }
        drawLine(col, q + Offset(-a, 0f), top, g); drawLine(col, q + Offset(a, 0.05f * kk), top, g); drawLine(p.lado(lerp(C.MADERA, C.HIERRO, 0.4f), lejos), q + Offset(0.1f * kk, -0.08f * kk), top, g)
        drawLine(p.luz(C.PLATA, lejos), top, top + Offset(0f, -0.4f * kk), (g * 0.6f).coerceAtLeast(0.3f))
        drawRect(p.lado(C.PIEL, lejos), q + Offset(0.5f * kk, -0.3f * kk), Size(0.35f * kk, 0.3f * kk))
        drawRect(p.medio(C.CAPOTE, lejos), q + Offset(0.5f * kk, -0.38f * kk), Size(0.35f * kk, 0.09f * kk))
        // «La última luz del día» también en los cañones de lejos.
        val tarde = VivacLuz.ultimaLuz(v.l)
        if (tarde > 0.05f) drawLine(Color(0xFFFF9A5A).copy(alpha = 0.7f * tarde), q + Offset(a * 0.5f, -h * 0.5f), top, (g * 0.7f).coerceAtLeast(0.3f), blendMode = BlendMode.Plus)
    }

    /** Una tienda de oficial lejana, de lona, con su lado de luz y su puerta (de noche, si está [encendida], con su farol dentro). */
    private fun DrawScope.tiendaLejana(v: VistaVivac, q: Offset, kk: Float, encendida: Boolean = true) {
        val p = v.p; val lejos = VivacNapoleon.lejos(VivacNapoleon.MIRADA.focal / kk); val lado = if (p.solALaDerecha) 1f else -1f
        val w = 1.3f * kk; val h = 2.5f * kk; val fondo = 1.1f * kk
        val frente = poligono(q + Offset(-w, 0f), q + Offset(w, 0f), q + Offset(w, -h * 0.5f), q + Offset(0f, -h), q + Offset(-w, -h * 0.5f))
        val costado = poligono(listOf(q + Offset(w * lado, 0f), q + Offset(w * lado + fondo * lado, -fondo * 0.25f), q + Offset(w * lado + fondo * lado, -h * 0.5f - fondo * 0.25f), q + Offset(lado * fondo, -h - fondo * 0.25f), q + Offset(0f, -h), q + Offset(w * lado, -h * 0.5f)))
        drawPath(costado, p.luz(C.LONA, lejos))
        drawPath(frente, p.frente(C.LONA, lejos))
        val farol = if (encendida) VivacNapoleon.farol(v.m) else 0f
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
        val color = lerp(l.tono(lerp(V.ARENA, C.LONA, 0.3f)), l.solColor, 0.25f + 0.4f * bajo)
        for (i in 0 until 34) {
            val z = 26f * exp(az.entre(0f, 2.7f)); val q = w(camino(z) + az.entre(-2f, 2f), z); val kk = k(z)
            if (q.x < -30f || q.x > ANCHO + 30f || enElRio(v, z)) continue
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
        for ((i, f) in FUEGOS_LEJANOS.sortedBy { it.z }.filterIndexed { j, _ -> j % 3 == 0 }.withIndex()) {
            if (i >= hilos) break
            if (enElRio(v, f.z)) continue
            humo(l, f.p, 18f + 300f / f.z, 1.2f + 30f / f.z, reloj, m.viento + 0.25f, 0.3f, VivacNapoleon.lejos(f.z), i + 60, 4, 0.35f)
        }
        // El polvo que levanta la columna, que pasa.
        val polvo = VivacNapoleon.polvo(m)
        if (polvo > 0.05f) {
            val az = VivacNapoleon.fijo("polvoVivo"); val color = lerp(l.tono(lerp(V.ARENA, C.LONA, 0.3f)), l.solColor, 0.4f)
            for (i in 0 until 6) {
                val z = 28f * exp(az.entre(0f, 2.2f)); val kk = k(z); val t = ((reloj * az.entre(0.03f, 0.06f) + az.real()) % 1f)
                val q = w(camino(z) + (t - 0.5f) * 8f, z) + Offset(0f, -0.6f * kk - t * 0.8f * kk)
                mancha(q, 2.2f * kk, 0.6f * kk, color, 0.14f * polvo * sin(t * 3.1416f))
            }
        }
    }

}
