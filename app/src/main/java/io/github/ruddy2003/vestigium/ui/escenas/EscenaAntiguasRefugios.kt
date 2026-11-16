package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Refugio
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Senal
import io.github.ruddy2003.vestigium.ui.escenas.PasoAntiguas.CORNISA_ARRIBA
import io.github.ruddy2003.vestigium.ui.escenas.PasoAntiguas.LEJOS
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAntiguas.lucecita
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.PIEDRA_PARTIDA
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.REFUGIO_ARN
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.ZETAS
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.rota
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import kotlin.math.sin

/**
 * Lo que hay en la ladera del paso además del camino: los dos refugios (el de la Piedra Partida, a media subida, y el de
 * Arn, en el collado, según la obra), la fila de mulas de la compañía, la nieve (en manchas, o toda con la ceja colgada
 * de la cornisa), la cascada de la garganta y las linternas que se mueven de noche.
 */
internal object RefugiosAntiguas {
    private val C = ColorAntiguas

    // ---------------------------------------------------------------- Los refugios

    /**
     * El refugio de la Piedra Partida, a media subida: un bloque enorme partido en dos, con su raja, y una caseta de piedra
     * con tejado de losa arrimada a una de las mitades. «Dentro de cada uno hay leña y una vela para quien llegue».
     */
    fun DrawScope.refugioPiedraPartida(v: VistaAntiguas) {
        val q = PIEDRA_PARTIDA; val p = v.pincelEn(q); val roca = lerp(C.PIEDRA, C.ROCA, 0.3f)
        val lado = if (p.solALaDerecha) 1f else -1f
        drawOval(p.r.color.copy(alpha = 0.45f * p.r.fuerza), q + Offset(-14f - lado * 7f, -1.4f), Size(32f, 3.6f), blendMode = BlendMode.Multiply)
        val izq = poligono(q + Offset(-13f, 0.6f), q + Offset(-1.4f, 0.6f), q + Offset(-0.8f, -11.6f), q + Offset(-5f, -13.4f), q + Offset(-11.4f, -10.2f))
        val der = poligono(q + Offset(1f, 0.6f), q + Offset(9.6f, 0.6f), q + Offset(10.6f, -7f), q + Offset(6.6f, -12.4f), q + Offset(1.6f, -11.6f))
        drawPath(izq, p.por(roca, v.sol.t(V3(-0.6f, 0.3f, -0.7f)), LEJOS)); drawPath(der, p.por(roca, v.sol.t(V3(0.6f, 0.3f, -0.7f)), LEJOS))
        drawPath(poligono(q + Offset(-11.4f, -10.2f), q + Offset(-5f, -13.4f), q + Offset(-0.8f, -11.6f), q + Offset(-6f, -10.6f)), p.por(lerp(roca, C.NIEVE, 0.2f), 0.5f, LEJOS))
        drawLine(p.medio(C.ESCORIA, LEJOS), q + Offset(0f, 0.5f), q + Offset(0.4f, -12f), 1.3f)
        // La caseta, a la derecha del bloque, con su puerta oscura y su tejado de losa.
        val frente = poligono(q + Offset(9.6f, 0.7f), q + Offset(18f, 0.5f), q + Offset(18f, -5.6f), q + Offset(9.6f, -5.4f))
        drawPath(frente, p.por(C.PIEDRA, v.sol.t(V3(0f, 0f, -1f)), LEJOS))
        drawPath(poligono(q + Offset(18f, 0.5f), q + Offset(20f, -0.4f), q + Offset(20f, -6.2f), q + Offset(18f, -5.6f)), p.por(C.PIEDRA, v.sol.t(V3(0.9f, 0f, -0.3f)), LEJOS))
        drawPath(poligono(q + Offset(8.4f, -5.2f), q + Offset(19.2f, -5.8f), q + Offset(20.8f, -6.4f), q + Offset(16.6f, -9f), q + Offset(10.4f, -8.4f)), p.por(C.LOSA, v.sol.t(V3(0f, 0.8f, -0.5f)), LEJOS))
        drawRect(p.medio(C.ESCORIA, LEJOS), q + Offset(12.6f, -3.8f), Size(2.2f, 4.2f))
        if (v.nieveLadera > 0.3f) drawPath(poligono(q + Offset(8.2f, -8.4f), q + Offset(16.8f, -9.2f), q + Offset(20.8f, -6.8f), q + Offset(16.6f, -10.6f), q + Offset(10f, -10f)), p.por(C.NIEVE, 0.5f, LEJOS).copy(alpha = v.nieveLadera))
        // La noche de la tormenta, la vela del nicho encendida (la compañía encerrada dentro).
        if (v.clima == Clima.TORMENTA && EscenaAntiguas.hoyEs(v.m, "tormenta") && v.noche > 0.3f) lucecita(q + Offset(13.7f, -2f), 0.8f, v.noche)
    }

    /**
     * El refugio de Arn, en el collado, según el arco: nada; los muros a medio hacer, «todavía sin tejado, con su andamio de
     * troncos», que suben con la obra; terminado, con el tejado de losa que «brilla» al sol (y a la luna). El día que se
     * empieza, el montón de piedras y los troncos; el día que se termina, una cinta en un palo. Grande para que se lea.
     */
    fun DrawScope.refugioDeArn(v: VistaAntiguas) {
        val p = v.pincelEn(REFUGIO_ARN); val q = REFUGIO_ARN; val lejos = 0.26f
        val estado = EscenaAntiguas.refugio(v.m)
        val piedra = lerp(C.PIEDRA, C.LANA, 0.1f)
        if (Senal.CIMIENTOS in v.senales || estado == Refugio.A_MEDIO) for (k in 0..2) drawLine(p.medio(lerp(C.MADERA, C.FRESNO, 0.3f), lejos), q + Offset(-11f + k * 1.4f, 0.5f), q + Offset(-6f + k * 1.8f, -2f), 0.8f)
        // El rellano del collado donde se asienta: piedra plana.
        drawPath(poligono(q + Offset(-9f, 1.2f), q + Offset(10f, 0.6f), q + Offset(9f, -0.8f), q + Offset(-8f, -0.4f)), p.por(lerp(piedra, C.NIEVE, 0.1f), 0.3f, lejos))
        val frenteT = v.sol.t(V3(-0.3f, 0f, -0.95f)); val ladoT = v.sol.t(V3(0.9f, 0f, -0.3f))
        when (estado) {
            Refugio.NADA -> if (Senal.CIMIENTOS in v.senales) repeat(7) { drawCircle(p.por(piedra, 0.2f, lejos), 1.3f, q + Offset(-4f + it * 1.7f, -0.8f - (it % 2) * 1f)) }
            Refugio.A_MEDIO -> {
                val h = 1.6f + 6.6f * (EscenaAntiguas.obra(v.m) / 0.999f)
                drawPath(poligono(q + Offset(-7f, 0.6f), q + Offset(5f, 0.6f), q + Offset(5f, -h), q + Offset(-7f, -h + 0.6f)), p.por(piedra, frenteT, lejos))
                drawPath(poligono(q + Offset(5f, 0.6f), q + Offset(8.6f, -0.6f), q + Offset(8.6f, -h - 1f), q + Offset(5f, -h)), p.por(piedra, ladoT, lejos))
                var hh = 1.6f; while (hh < h) { drawLine(p.sombra(piedra, lejos).copy(alpha = 0.7f), q + Offset(-7f, -hh + 0.6f), q + Offset(5f, -hh), 0.4f); hh += 1.6f }
                // El andamio de troncos: dos pies, un travesaño y una escalera apoyada.
                val tronco = p.medio(lerp(C.MADERA, C.FRESNO, 0.3f), lejos)
                drawLine(tronco, q + Offset(-8.6f, 0.6f), q + Offset(-8.2f, -h - 5f), 0.8f); drawLine(tronco, q + Offset(10f, 0f), q + Offset(9.6f, -h - 5.6f), 0.8f)
                drawLine(tronco, q + Offset(-9f, -h - 2.6f), q + Offset(10.4f, -h - 3.4f), 0.7f)
                drawLine(tronco.copy(alpha = 0.85f), q + Offset(-4f, 0.6f), q + Offset(-8f, -h - 1.6f), 0.6f)
            }
            Refugio.TERMINADO -> {
                drawPath(poligono(q + Offset(-7f, 0.6f), q + Offset(5f, 0.6f), q + Offset(5f, -6.8f), q + Offset(-7f, -6.2f)), p.por(piedra, frenteT, lejos))
                drawPath(poligono(q + Offset(5f, 0.6f), q + Offset(8.6f, -0.6f), q + Offset(8.6f, -7.8f), q + Offset(5f, -6.8f)), p.por(piedra, ladoT, lejos))
                drawLine(p.sombra(piedra, lejos).copy(alpha = 0.6f), q + Offset(-7f, -3f), q + Offset(5f, -3.2f), 0.4f)
                val tejado = poligono(q + Offset(-8.2f, -6f), q + Offset(5.8f, -7f), q + Offset(9.6f, -8f), q + Offset(0.8f, -13f), q + Offset(-4.6f, -11.8f))
                drawPath(tejado, p.por(C.LOSA, v.sol.t(V3(0.2f, 0.8f, -0.5f)) + 0.3f, lejos))
                // «Brilla»: el canto de las losas a contraluz o a la luna.
                drawLine(lerp(v.l.solColor, Color.White, 0.5f).copy(alpha = (0.75f * v.p.r.dia + 0.55f * v.luna).coerceIn(0f, 1f)), q + Offset(-7.6f, -6.2f), q + Offset(0.8f, -12.8f), 0.8f, StrokeCap.Round)
                drawRect(p.medio(C.ESCORIA, lejos), q + Offset(-2.2f, -3.8f), Size(2.2f, 4.4f))
                if (v.nieveArriba > 0.4f) drawPath(poligono(q + Offset(-7.2f, -7f), q + Offset(0.8f, -13.4f), q + Offset(9f, -8.4f), q + Offset(1f, -11.6f)), p.por(C.NIEVE, 0.5f, lejos).copy(alpha = v.nieveArriba))
                if (Senal.BANDERA_EN_EL_REFUGIO in v.senales) { drawLine(p.medio(C.FRESNO, lejos), q + Offset(8f, -7.6f), q + Offset(8.4f, -20f), 0.7f); drawPath(poligono(q + Offset(8.4f, -20f), q + Offset(15f, -18.4f), q + Offset(8.3f, -16.6f)), p.por(C.RUBI, 0.4f, lejos)) }
            }
        }
    }

    // ---------------------------------------------------------------- La compañía

    /**
     * La compañía en el camino, contada por sus mulas: una fila de mulas pequeñas, cargadas y sin nadie **[Diseño propio]**,
     * que sube por las zetas por la mañana y baja por la tarde (`EscenaAntiguas.filaEnElCamino`).
     */
    fun DrawScope.filaDeMulas(v: VistaAntiguas) {
        val u = EscenaAntiguas.filaEnElCamino(v.m) ?: return
        val n = EscenaAntiguas.mulasEnLaFila(v.m)
        val largo = largoDelCamino()
        for (j in 0 until n) {
            val d = u * largo - j * 6f; if (d < 0f) break
            val q = puntoDelCamino(d)
            val k = 0.55f + ((q.y - 108f) / 90f).coerceIn(0f, 1f) * 1f
            mulita(v.pincelEn(q), q, k, if (EscenaAntiguas.companiaEnElCamino(v.m) && v.m.hora < 12.5) 1f else -1f, j)
        }
    }

    private fun largoDelCamino(): Float = (0 until ZETAS.size - 1).sumOf { (ZETAS[it + 1] - ZETAS[it]).getDistance().toDouble() }.toFloat()

    private fun puntoDelCamino(d: Float): Offset {
        var resto = d
        for (i in 0 until ZETAS.size - 1) { val a = ZETAS[i]; val b = ZETAS[i + 1]; val l = (b - a).getDistance(); if (resto <= l) return a + (b - a) * (resto / l); resto -= l }
        return ZETAS.last()
    }

    /** Una mula lejana, de lado, con su carga: el lomo con los fardos claros, las patas, la cabeza baja y las orejas. */
    private fun DrawScope.mulita(p: Pincel, pie: Offset, k: Float, va: Float, j: Int) {
        val pelo = if (j % 3 == 1) C.MULA_PARDA else C.MULA
        drawOval(p.r.color.copy(alpha = 0.45f * p.r.fuerza), pie + Offset(-3f * k, -0.4f * k), Size(6f * k, 0.9f * k), blendMode = BlendMode.Multiply)
        for (dx in listOf(-1.6f, -1f, 1f, 1.6f)) drawLine(p.sombra(pelo, LEJOS), pie + Offset(dx * k, -1.6f * k), pie + Offset(dx * k * 1.05f, 0f), 0.35f * k)
        drawOval(p.por(pelo, 0.1f, LEJOS), pie + Offset(-2.3f * k, -3.2f * k), Size(4.6f * k, 1.9f * k))
        val cab = pie + Offset(va * 2.6f * k, -3f * k)
        drawLine(p.por(pelo, 0f, LEJOS), pie + Offset(va * 1.8f * k, -2.6f * k), cab + Offset(va * 0.6f * k, 0.6f * k), 0.9f * k, StrokeCap.Round)
        drawLine(p.sombra(pelo, LEJOS), cab, cab + Offset(-va * 0.2f * k, -1.2f * k), 0.25f * k)
        drawOval(p.por(lerp(C.LANA, C.CUERO, 0.3f + 0.2f * (j % 2)), 0.3f, LEJOS), pie + Offset(-1.7f * k, -4.4f * k), Size(3.2f * k, 1.6f * k))
    }

    // ---------------------------------------------------------------- La nieve de la ladera

    /**
     * La nieve de la ladera ([VistaAntiguas.nieveLadera]): de arriba abajo, con el borde festoneado; con el paso cerrado,
     * toda, «una ladera blanca y lisa que sube hasta el cielo», sin camino ni piedras ni puente: los alerces pelados
     * asomando, los lomos marcados por la luz y la ceja de nieve colgada sobre la cornisa.
     */
    fun DrawScope.nieve(v: VistaAntiguas, az: Azar) {
        val n = v.nieveLadera; if (n <= 0.02f) return
        val p = v.p
        if (n < 0.99f) {
            with(PiezasAntiguas) { manchasDeNieve(p, 150f, 360f, { 100f }, { x -> 112f + 96f * n + 6f * sin(x * 0.09f) + 4f * sin(x * 0.21f) }, az, LEJOS) }
            if (n > 0.55f) ceja(v)
            return
        }
        clipPath(PasoAntiguas.FORMA) {
            drawRect(Brush.verticalGradient(0f to p.por(C.NIEVE, 0.35f, LEJOS), 1f to p.por(C.NIEVE, 0.05f, LEJOS * 0.8f), startY = 108f, endY = 212f), Offset(150f, 100f), Size(212f, 116f))
            // Los lomos y las canales de la ladera bajo la nieve: bajan en abanico desde el collado, cada uno con su lado
            // de sombra azul y su filo de luz; la banda de la cornisa, que asoma lisa; abajo, la nieve se arruga.
            val lado = if (p.solALaDerecha) -1f else 1f
            for (k in 0 until 7) {
                val pie = Offset(166f + k * 22f + az.entre(-5f, 5f), 210f)
                val arriba = Offset(250f + k * 9f, 122f + kotlin.math.abs(k - 3) * 3f)
                val r = rota(listOf(arriba, arriba + (pie - arriba) * 0.35f + Offset(az.entre(-6f, 6f), 0f), arriba + (pie - arriba) * 0.7f + Offset(az.entre(-6f, 6f), 0f), pie), az, 2.2f, 2)
                drawPath(curva(r.map { it + Offset(lado * 2.2f, 0f) }), p.sombra(C.NIEVE_SOMBRA, LEJOS).copy(alpha = 0.32f), style = Stroke(3.6f + k * 0.3f, cap = StrokeCap.Round))
                drawPath(curva(r), p.luz(C.NIEVE, LEJOS).copy(alpha = 0.45f * p.r.dia + 0.1f), style = Stroke(1f, cap = StrokeCap.Round))
            }
            drawPath(curva(CORNISA_ARRIBA.map { it + Offset(0f, 8f) }), p.sombra(C.NIEVE_SOMBRA, LEJOS).copy(alpha = 0.4f), style = Stroke(6f))
            repeat(9) { val q = Offset(az.entre(166f, 300f), az.entre(176f, 206f)); drawPath(TrazosValle.blando(q, az.entre(6f, 14f), az.entre(1.4f, 2.4f), az, 9, 0.2f), p.sombra(C.NIEVE_SOMBRA, LEJOS).copy(alpha = 0.25f)) }
            // Los alerces pelados que asoman de la nieve, grises.
            repeat(40) { val q = Offset(az.entre(164f, 300f), az.entre(168f, 208f)); val h = 3f + (q.y - 160f) * 0.16f; drawLine(p.medio(lerp(C.MADERA, C.PIEDRA, 0.5f), LEJOS).copy(alpha = 0.7f), q, q + Offset(0f, -h), 0.5f) }
        }
        ceja(v)
    }

    /** La ceja de nieve colgada sobre la cornisa, «que cualquier día caerá». */
    private fun DrawScope.ceja(v: VistaAntiguas) {
        val p = v.p
        val ceja = CORNISA_ARRIBA.map { it + Offset(0f, 1.6f) }
        val labio = ceja.mapIndexed { i, q -> q + Offset(-1.4f, 3f + 1.6f * sin(i * 1.3f)) }
        drawPath(poligono(ceja.map { it + Offset(0f, -2.6f) } + labio.reversed()), p.por(C.NIEVE, 0.45f, LEJOS))
        drawPath(curva(labio), p.sombra(C.NIEVE_SOMBRA, LEJOS).copy(alpha = 0.75f), style = Stroke(1.4f))
        for ((i, q) in labio.withIndex()) if (i % 2 == 1) drawLine(p.luz(lerp(C.NIEVE, C.AGUA, 0.2f), LEJOS).copy(alpha = 0.6f), q, q + Offset(0f, 1.8f + i % 3), 0.6f)
    }

    // ---------------------------------------------------------------- El agua y las luces

    /** La cascada de la garganta, entre la ladera de las vetas y la del paso: un hilo blanco que salta al torrente; helada con el paso cerrado. */
    fun DrawScope.cascada(v: VistaAntiguas, az: Azar) {
        val p = v.p
        val hilo = rota(pts(166f, 168f, 165f, 180f, 166f, 192f, 164f, 202f, 165f, 211f), az, 0.8f, 2)
        val agua = if (v.invierno) p.por(lerp(C.NIEVE, C.AGUA, 0.3f), 0.2f, LEJOS) else p.luz(lerp(C.NIEVE, C.AGUA, 0.2f), LEJOS)
        drawPath(curva(hilo), p.sombra(C.ESCORIA, LEJOS).copy(alpha = 0.4f), style = Stroke(3f, cap = StrokeCap.Round))
        drawPath(curva(hilo), agua.copy(alpha = 0.9f), style = Stroke(1.4f, cap = StrokeCap.Round))
        if (!v.invierno) drawPoints(hilo.map { it + Offset(az.entre(-1f, 1f), 0f) }, PointMode.Points, lerp(v.l.bajo, Color.White, 0.6f).copy(alpha = 0.7f * (1f - 0.6f * v.noche)), 1f, StrokeCap.Round)
    }

    /**
     * Las linternas que se mueven en la ladera de noche (parte viva): las de la búsqueda de Arn por todo el collado y las de
     * los de rescate que suben por la cara norte con la linterna de mina.
     */
    fun DrawScope.linternasEnLaLadera(v: VistaAntiguas, reloj: Float) {
        if (v.noche < 0.4f) return
        val busqueda = EscenaAntiguas.hoyEs(v.m, "busqueda") || EscenaAntiguas.hoyEs(v.m, "arn_falta")
        val rescate = EscenaAntiguas.hoyEs(v.m, "rescate")
        val f = v.noche
        if (busqueda) for (j in 0 until 7) {
            val base = listOf(Offset(214f, 150f), Offset(236f, 128f), Offset(262f, 120f), Offset(290f, 112f), Offset(196f, 162f), Offset(250f, 134f), Offset(280f, 116f))[j]
            val q = base + Offset(3f * sin(reloj * 0.17f + j * 1.9f), 1.2f * sin(reloj * 0.23f + j))
            lucecita(q, 0.65f, f * (0.75f + 0.25f * sin(reloj * 1.3f + j)))
        }
        if (rescate) for (j in 0 until 5) {
            val t = ((reloj * 0.012f + j * 0.16f) % 1f)
            val q = Offset(176f + 24f * t, 150f - 60f * t)
            lucecita(q, 0.6f, f * 0.9f)
        }
    }
}
