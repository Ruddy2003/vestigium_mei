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
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Tramo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.armon
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.canon
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.carro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.hoguera
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.pino
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.pinoAlbar
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaNapoleon.FUGA
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaNapoleon.H
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaNapoleon.MIRADA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.sin

/**
 * El camino y el bosque de la retirada: la diagonal y la contradiagonal del cuadro.
 *
 * Todo se pone en metros, en el espacio de los ojos (x a la derecha, z hacia el fondo, los ojos a 1.6 m: ver
 * `PiezasNapoleon.OJO`) y se proyecta con [w]: así el camino, el borde del bosque y lo abandonado se van juntos al punto de
 * fuga, y las cosas encogen con la distancia. El camino va casi recto hacia el fondo, un poco a la derecha ([RUMBO]);
 * mide unos 3.4 m de ancho, con la cuneta de quien mira a la derecha; el bosque empieza unos 6 m a su izquierda.
 *
 * - **El suelo** ([suelo]): el llano de la derecha con sus ventisqueros y su lejanía, la franja entre el camino y el
 *   bosque, y el camino: nieve pisada y sucia en medio y limpia en las orillas, con las rodadas, las huellas de botas y de
 *   cascos que se van al punto de fuga; antes de la nieve, barro helado, charcos y hojas.
 * - **El bosque** ([bosque]): pinos en tres filas, con la bruma entre ellas, cuyo borde de arriba baja de la izquierda
 *   al centro.
 * - **Lo abandonado** ([abandonado]): el cañón con su armón, carros volcados, cofres, fuegos apagados y postes, cada vez
 *   más chicos a lo largo del camino.
 */
internal object RetiradaCamino {
    private val C = ColorNapoleon

    /** Cuánto se corre el camino a la derecha por cada metro que se aleja: su punto de fuga queda en x = 235. */
    const val RUMBO = 0.08f

    /** Un punto del suelo (x, z en metros) en el lienzo. */
    fun w(x: Float, z: Float): Offset = Offset(MIRADA.centro + MIRADA.focal * x / z, H + MIRADA.focal * PiezasNapoleon.OJO / z)

    /** Cuántas unidades del lienzo mide un metro a la profundidad [z]. */
    fun k(z: Float): Float = MIRADA.focal / z

    /** La profundidad de un punto del suelo en el lienzo a la altura [y]. */
    fun zEn(y: Float): Float = MIRADA.focal * PiezasNapoleon.OJO / (y - H).coerceAtLeast(0.5f)

    /** El borde izquierdo y el derecho del camino, y el pie del bosque, a la profundidad [z] (en metros). */
    fun bordeIzq(z: Float): Float = -3.46f + RUMBO * z
    fun bordeDer(z: Float): Float = -0.09f + RUMBO * z
    fun pieDelBosque(z: Float): Float = -9.2f + RUMBO * z

    /** Las profundidades con que se arman los bordes, de cerca a lejos. */
    private val ZS = listOf(3.2f, 3.8f, 4.5f, 5.3f, 6.2f, 7.3f, 8.6f, 10.2f, 12f, 14.5f, 17.5f, 21f, 26f, 32f, 40f, 50f, 64f, 82f, 110f, 150f, 220f, 340f, 600f)

    /** Un borde puesto a mano: los puntos del borde [x] (en metros) con sus entrantes y salientes, que se apagan a lo lejos. */
    private fun borde(x: (Float) -> Float, semilla: String, amp: Float): List<Offset> {
        val az = Azar(Semilla.de("napoleon", "borde", semilla))
        return ZS.map { z -> val q = w(x(z) + az.entre(-amp, amp) * (0.4f + 0.6f * (8f / z).coerceAtMost(1f)), z); q }
    }

    val IZQ: List<Offset> = borde({ bordeIzq(it) }, "izq", 0.35f)
    val DER: List<Offset> = borde({ bordeDer(it) }, "der", 0.25f)
    val BOSQUE: List<Offset> = borde({ pieDelBosque(it) }, "bosque", 1.1f)

    /** Todo el suelo, del horizonte abajo. */
    fun sueloForma(): Path = poligono(listOf(Offset(-10f, H - 0.5f), Offset(ANCHO + 10f, H - 0.5f), Offset(ANCHO + 10f, ALTO + 10f), Offset(-10f, ALTO + 10f)))

    /** El camino, como forma. */
    fun camino(): Path = poligono(IZQ + DER.reversed())

    // ---------------------------------------------------------------- Los pinos del bosque

    /** Un pino del bosque, en metros: dónde pisa ([x], [z]), lo alto, lo ancho de las ramas de abajo, si tiene la punta rota. */
    class Pino(val x: Float, val z: Float, val alto: Float, val ancho: Float, val semilla: Long, val roto: Boolean, val fila: Int, val albar: Boolean = false) {
        val pie: Offset get() = w(x, z)
        val k: Float get() = k(z)
        val altoL: Float get() = alto * k
        val anchoL: Float get() = ancho * k

        /** Su silueta en el lienzo (el tronco y la copa en pisos, dentada; el pino albar, el tronco alto y la copa arriba), para la sombra que tumba. */
        fun silueta(): List<Offset> {
            val p = pie; val az = Azar(semilla + 7)
            if (albar) {
                // El tronco desnudo hasta casi dos tercios y la copa, una nube irregular.
                val tr = 0.2f * k; val copa = altoL * PiezasNapoleon.COPA_ALBAR; val w = anchoL * 0.5f
                val izq = listOf(p + Offset(-tr, 0f), Offset(p.x - tr * 0.7f, p.y - copa), Offset(p.x - w * az.entre(0.6f, 0.9f), p.y - copa - altoL * 0.06f),
                    Offset(p.x - w, p.y - copa - altoL * 0.16f), Offset(p.x - w * 0.6f, p.y - altoL * 0.82f), Offset(p.x - w * 0.7f, p.y - altoL * 0.9f))
                val der = listOf(p + Offset(tr, 0f), Offset(p.x + tr * 0.7f, p.y - copa), Offset(p.x + w * az.entre(0.6f, 0.9f), p.y - copa - altoL * 0.04f),
                    Offset(p.x + w, p.y - copa - altoL * 0.18f), Offset(p.x + w * 0.7f, p.y - altoL * 0.84f), Offset(p.x + w * 0.4f, p.y - altoL * 0.94f))
                return izq + listOf(Offset(p.x, p.y - altoL)) + der.reversed()
            }
            val limpio = 1.2f * k; val tr = 0.22f * k
            val izq = ArrayList<Offset>(); val der = ArrayList<Offset>()
            izq += p + Offset(-tr, 0f); der += p + Offset(tr, 0f)
            izq += p + Offset(-tr, -limpio); der += p + Offset(tr, -limpio)
            val pisos = 7
            for (i in 0 until pisos) {
                val t = i / pisos.toFloat(); val y = p.y - limpio - (altoL - limpio) * t
                val ww = anchoL * 0.5f * (1f - t) * az.entre(0.8f, 1.1f)
                izq += Offset(p.x - ww, y + 0.03f * altoL); izq += Offset(p.x - ww * 0.45f, y - 0.05f * altoL)
                der += Offset(p.x + ww, y + 0.03f * altoL); der += Offset(p.x + ww * 0.45f, y - 0.05f * altoL)
            }
            return izq + listOf(Offset(p.x, p.y - altoL)) + der.reversed()
        }
    }

    /** Los pinos del borde del bosque, en tres filas: la del borde, algo salida y desigual, y dos más hondas. */
    private val PINOS: List<Pino> by lazy {
        val az = Azar(Semilla.de("napoleon", "bosque", "pinos")); val lista = ArrayList<Pino>()
        for (fila in 0..2) {
            var z = 8.5f + fila * 1.2f
            while (z < 520f) {
                val x = pieDelBosque(z) - fila * az.entre(1.6f, 3.8f) + (if (fila == 0) az.entre(-0.8f, 2.6f) else az.entre(-1.2f, 1.2f))
                val alto = az.entre(9f, 17f) * (if (fila == 0 && az.real() < 0.25f) az.entre(0.45f, 0.75f) else 1f)
                val albar = fila == 0 && az.real() < 0.32f
                lista += Pino(x, z, if (albar) alto * 1.15f else alto, alto * (if (albar) az.entre(0.3f, 0.4f) else az.entre(0.26f, 0.44f)), az.entero(1_000_000).toLong(), az.real() < 0.14f, fila, albar)
                z *= az.entre(1.05f, 1.42f)
            }
        }
        lista
    }

    /** Los pinos del borde del bosque (para pintarlos y para las franjas de la tarde). */
    fun pinosDelBorde(): List<Pino> = PINOS.filter { it.fila == 0 }

    /** Los pinos de la derecha, fuera del cuadro, que por la mañana tienden sus sombras a través del camino. */
    fun pinosDeLaDerecha(): List<Pino> = DERECHA

    private val DERECHA: List<Pino> by lazy {
        val az = Azar(Semilla.de("napoleon", "bosque", "derecha")); val lista = ArrayList<Pino>()
        // Los de fuera del cuadro, cerca: pinos albares, de tronco alto y desnudo, que tienden franjas de sombra.
        var z = 4.2f
        while (z < 30f) {
            val x = z * 0.42f + az.entre(1.2f, 3.4f)
            val alto = az.entre(13f, 19f)
            lista += Pino(x, z, alto, alto * az.entre(0.28f, 0.36f), az.entero(1_000_000).toLong(), false, 0, albar = true)
            z *= az.entre(1.3f, 1.6f)
        }
        // El bosquecillo de la derecha, que se ve: pinos albares y algún abeto, más allá de la granja.
        z = 72f
        while (z < 170f) {
            val xl = az.entre(336f, 404f); val x = (xl - MIRADA.centro) * z / MIRADA.focal
            val albar = az.real() < 0.7f; val alto = az.entre(11f, 17f)
            lista += Pino(x, z, alto, alto * (if (albar) az.entre(0.3f, 0.38f) else az.entre(0.3f, 0.4f)), az.entero(1_000_000).toLong(), false, 0, albar)
            z *= az.entre(1.1f, 1.25f)
        }
        lista
    }

    /** El bosquecillo de la derecha que se ve (de lejos a cerca). */
    fun bosquecillo(): List<Pino> = DERECHA.filter { it.z >= 70f }.sortedByDescending { it.z }

    // ---------------------------------------------------------------- El suelo

    fun DrawScope.suelo(v: VistaNapoleon) {
        val l = v.l; val p = v.p; val az = Azar(Semilla.de("napoleon", "suelo"))
        val sinNieve = v.nieve < 0.5f
        // La base: de la lejanía azulada a la cercanía cálida (la superficie grande cambia de lejos a cerca).
        drawRect(Brush.verticalGradient(0f to v.suelo(0.75f), 0.08f to v.suelo(0.5f), 0.35f to v.suelo(0.22f), 1f to v.suelo(0f), startY = H, endY = ALTO), Offset(-10f, H - 0.5f), Size(ANCHO + 20f, ALTO - H + 10f))
        // Sin nieve (o con la primera, que no cuaja): el rastrojo del otoño, en surcos que se van al fondo, y la escarcha.
        if (v.nieve < 0.85f) rastrojo(v, az)
        // Los ventisqueros del llano y de la franja del bosque: lomos blandos con su cresta al sol y su lado de sombra.
        if (v.nieve > 0.2f) ventisqueros(v, az)
        // Las ondas del llano a lo lejos, con la sombra azul en los hondos.
        for (i in 0 until 7) {
            val z = 30f * kotlin.math.exp(i * 0.42f); val y = w(0f, z).y
            val x0 = w(bordeDer(z) + 1f, z).x
            val onda = Path().apply { moveTo(x0, y); var x = x0; while (x < ANCHO + 10f) { val dx = az.entre(18f, 44f) * (70f / z).coerceIn(0.4f, 1.5f); cubicTo(x + dx * 0.3f, y - az.entre(0.4f, 1.6f), x + dx * 0.7f, y + az.entre(-0.3f, 0.8f), x + dx, y + az.entre(-0.5f, 0.5f)); x += dx } }
            drawPath(onda, p.arriba(lerp(C.NIEVE_SOMBRA, C.HOJA, if (sinNieve) 0.5f else 0f), 0.6f).copy(alpha = 0.55f), style = Stroke(0.9f + 6f / z * 10f))
        }
        // El camino.
        camino(v, az)
        // Las sendas que van del camino a los fuegos del llano, pisadas.
        val senda = listOf(w(bordeDer(24f) + 0.2f, 24f), w(4f, 30f), w(9f, 36f), w(16f, 44f))
        drawPath(curva(senda), p.arriba(if (sinNieve) lerp(C.BARRO, C.CENIZA, 0.2f) else C.NIEVE_SUCIA, 0.3f).copy(alpha = 0.6f), style = Stroke(1.2f, cap = StrokeCap.Round))
        // Los postes del camino, de madera, cada vez más chicos, en la orilla de la derecha.
        for (z in listOf(18f, 34f, 64f, 120f)) {
            val q = w(bordeDer(z) + 0.6f, z); val kk = k(z); val alto = 1.4f * kk
            val a = v.en(q)
            drawRect(a.plano(C.MADERA_GRIS, q, if (v.p.solALaDerecha) 0.8f else -0.8f, (z / 200f).coerceAtMost(0.8f)), Offset(q.x - 0.07f * kk, q.y - alto), Size(0.14f * kk, alto))
            drawRect(a.plano(C.MADERA_GRIS, q, if (v.p.solALaDerecha) -0.8f else 0.8f, (z / 200f).coerceAtMost(0.8f)), Offset(q.x + (if (v.p.solALaDerecha) -0.07f else 0f) * kk, q.y - alto), Size(0.07f * kk, alto))
            if (v.nieve > 0.3f) drawRect(a.arriba(C.NIEVE, q, (z / 200f).coerceAtMost(0.8f)), Offset(q.x - 0.09f * kk, q.y - alto - 0.05f * kk), Size(0.18f * kk, 0.07f * kk))
        }
    }

    /** El rastrojo del otoño: la tierra parda con surcos y matas, la escarcha si hiela y, con la primera nieve, manchas blancas. */
    private fun DrawScope.rastrojo(v: VistaNapoleon, az: Azar) {
        val p = v.p; val sinNieve = 1f - (v.nieve / 0.85f).coerceIn(0f, 1f)
        // Los surcos del campo, a la derecha, que se van al fondo.
        for (i in 0 until 16) {
            val x = 1.5f + i * 1.6f
            val q0 = w(x + RUMBO * 4f, 4f); val q1 = w(x + RUMBO * 160f, 160f)
            drawLine(p.arriba(lerp(C.HOJA, C.BARRO, 0.6f), 0.3f).copy(alpha = 0.35f * sinNieve), q0, q1, 0.8f)
        }
        // Las matas secas y las hojas.
        repeat(120) {
            val z = 4f * kotlin.math.exp(az.entre(0f, 3.6f)); val x = if (az.real() < 0.5f) az.entre(0f, 24f) + RUMBO * z else pieDelBosque(z) + az.entre(0f, 5.5f)
            val q = w(x, z); val kk = k(z)
            val c = p.arriba(if (az.real() < 0.5f) C.HOJA else lerp(C.HOJA, C.ROJO, 0.3f), (z / 120f).coerceAtMost(0.8f))
            mancha(q, 0.25f * kk, 0.07f * kk, c, 0.6f * sinNieve)
        }
        // La escarcha de la mañana: un velo claro y frío, más en lo que no da el sol.
        if (v.helada) drawRect(Brush.verticalGradient(0f to C.NIEVE.copy(alpha = 0.05f), 1f to lerp(C.NIEVE, C.NIEVE_SOMBRA, 0.3f).copy(alpha = 0.22f), startY = H, endY = ALTO), Offset(-10f, H), Size(ANCHO + 20f, ALTO - H))
        // La primera nieve, que cuaja en manchas en el campo y no en el camino.
        if (v.nieve > 0.1f) repeat(70) {
            val z = 3.5f * kotlin.math.exp(az.entre(0f, 3.8f)); val x = if (az.real() < 0.55f) az.entre(0.5f, 26f) + RUMBO * z else pieDelBosque(z) + az.entre(0f, 5f)
            val q = w(x, z); val kk = k(z)
            drawPath(TrazosValle.blando(q, az.entre(0.4f, 1.1f) * kk, az.entre(0.06f, 0.16f) * kk, az, 8, 0.25f), v.en(q).arriba(C.NIEVE, q, (z / 120f).coerceAtMost(0.8f)).copy(alpha = (v.nieve / 0.4f).coerceIn(0f, 0.95f)))
        }
    }

    /** Los ventisqueros: lomos de nieve tendidos, con la cresta encendida del lado del sol y la sombra azul del otro. */
    private fun DrawScope.ventisqueros(v: VistaNapoleon, az: Azar) {
        val k0 = ((v.nieve - 0.2f) / 0.6f).coerceIn(0f, 1f)
        val lado = if (v.p.solALaDerecha) 1f else -1f
        repeat(26) { i ->
            val z = 4.2f * kotlin.math.exp(az.entre(0f, 3.4f))
            val derecha = i % 3 != 0
            val x = if (derecha) bordeDer(z) + az.entre(1.2f, 18f) else pieDelBosque(z) + az.entre(0.6f, 4.5f)
            val q = w(x, z); val kk = k(z); val largo = az.entre(1.6f, 4.5f) * kk; val alto = az.entre(0.12f, 0.3f) * kk
            if (q.x > ANCHO + largo || q.x < -largo) return@repeat
            val lejos = (z / 100f).coerceAtMost(0.8f)
            val a = v.en(q)
            val lomo = TrazosValle.blando(q + Offset(0f, -alto * 0.3f), largo * 0.5f, alto, az, 9, 0.12f, 0.45f)
            drawPath(lomo, a.arriba(C.NIEVE, q, lejos).copy(alpha = k0))
            // La cara de sombra, del lado contrario al sol, y la cresta encendida.
            clipPath(lomo) {
                mancha(q + Offset(-lado * largo * 0.3f, alto * 0.3f), largo * 0.36f, alto * 0.7f, a.plano(C.NIEVE_SOMBRA, q, -lado, lejos), 0.42f * k0)
                drawLine(lerp(a.arriba(C.NIEVE, q, lejos), Color.White, 0.5f).copy(alpha = 0.8f * k0 * (0.3f + 0.7f * v.r.dia)), q + Offset(-largo * 0.35f + lado * largo * 0.1f, -alto * 1.1f), q + Offset(largo * 0.35f + lado * largo * 0.1f, -alto * 1.05f), (alto * 0.25f).coerceIn(0.3f, 2.5f), StrokeCap.Round)
            }
        }
        // Las estrías del viento en la nieve (sastrugi), finas y tendidas, más de cerca.
        repeat(46) {
            val z = 3.8f * kotlin.math.exp(az.entre(0f, 2.6f)); val derecha = az.real() < 0.7f
            val x = if (derecha) bordeDer(z) + az.entre(0.6f, 10f) else pieDelBosque(z) + az.entre(0.4f, 5f)
            val q = w(x, z); val kk = k(z); val l = az.entre(0.6f, 1.6f) * kk
            drawLine(v.en(q).plano(C.NIEVE_SOMBRA, q, 0f, (z / 100f).coerceAtMost(0.8f)).copy(alpha = 0.35f * k0), q, q + Offset(l, az.entre(-0.05f, 0.05f) * kk), (0.03f * kk).coerceIn(0.3f, 1.4f), StrokeCap.Round)
        }
    }

    /**
     * El camino: la nieve pisada, sucia en medio y limpia en las orillas, con los bancos de nieve que deja la columna a
     * los lados; las rodadas y las huellas de botas y de cascos, frescas o tapadas según [EscenaNapoleon.huellas]; antes
     * de la nieve, el barro helado con sus charcos (que devuelven el cielo) y las hojas.
     */
    private fun DrawScope.camino(v: VistaNapoleon, az: Azar) {
        val p = v.p; val forma = camino()
        val nieve = v.nieve
        val pisada = lerp(lerp(C.BARRO, C.CENIZA, 0.15f), C.NIEVE_SUCIA, nieve)
        drawPath(forma, Brush.verticalGradient(0f to p.arriba(pisada, 0.7f), 0.2f to p.arriba(pisada, 0.35f), 1f to p.arriba(pisada, 0f), startY = H, endY = ALTO))
        val huellas = EscenaNapoleon.huellas(v.m)
        clipPath(forma) {
            // Las orillas: la nieve limpia y alta que la columna empuja a los lados, con su canto en sombra hacia el camino.
            if (nieve > 0.3f) for ((orilla, signo) in listOf(IZQ to 1f, DER to -1f)) {
                val pts = orilla.map { it }
                for (i in 0 until pts.size - 1) {
                    val z = ZS[i]; val kk = k(z); val q0 = pts[i]; val q1 = pts[i + 1]
                    val ancho = 0.5f * kk * nieve
                    drawPath(poligono(q0, q1, q1 + Offset(signo * ancho * k(ZS[i + 1]) / kk, 0f), q0 + Offset(signo * ancho, 0f)), p.arriba(C.NIEVE, (z / 120f).coerceAtMost(0.8f)))
                    drawLine(v.a.plano(C.NIEVE_SOMBRA, q0, -signo * (if (p.solALaDerecha) 1f else -1f), (z / 120f).coerceAtMost(0.8f)), q0 + Offset(signo * ancho, 0f), q1 + Offset(signo * ancho * k(ZS[i + 1]) / kk, 0f), (0.12f * kk).coerceIn(0.4f, 5f))
                }
            }
            // Los charcos del barro (sin nieve), que devuelven el cielo; helados por la mañana.
            if (nieve < 0.5f) repeat(9) {
                val z = 3.6f * kotlin.math.exp(az.entre(0f, 2.6f)); val x = (bordeIzq(z) + bordeDer(z)) / 2f + az.entre(-1.2f, 1.2f)
                val q = w(x, z); val kk = k(z)
                val charco = TrazosValle.blando(q, az.entre(0.4f, 0.9f) * kk, az.entre(0.06f, 0.12f) * kk, az, 9, 0.25f)
                drawPath(charco, Brush.verticalGradient(listOf(lerp(v.l.medio, v.l.alto, 0.4f), v.l.bajo), q.y - 0.12f * kk, q.y + 0.12f * kk))
                drawPath(charco, p.arriba(if (v.helada) C.NIEVE else C.BARRO, 0f).copy(alpha = if (v.helada) 0.35f else 0.2f), style = Stroke((0.03f * kk).coerceIn(0.3f, 1.5f)))
            }
            // Las rodadas: pares de surcos que se van al fondo, oscuros, con el canto de luz.
            for ((j, xd) in listOf(-2.6f to 0.35f, -1.95f to -0.3f, -1.2f to 0.25f, -0.65f to -0.4f).withIndex()) {
                val (x0, de) = xd
                val pts = ZS.map { z -> w(x0 + RUMBO * z + de * sin(z * 0.17f + j * 1.7f) * (z / 12f).coerceAtMost(1f) + 0.12f * sin(z * 0.9f + j), z) }
                val alfa = 0.55f * (0.4f + 0.6f * huellas)
                for (i in 0 until pts.size - 1) {
                    val z = ZS[i]; val kk = k(z); val lejos = (z / 120f).coerceAtMost(0.85f)
                    drawLine(p.arriba(lerp(C.BARRO, C.CENIZA, 0.35f), lejos).copy(alpha = alfa), pts[i], pts[i + 1], (0.12f * kk).coerceIn(0.3f, 7f), StrokeCap.Round)
                    drawLine(p.arriba(lerp(pisada, Color.White, 0.25f), lejos).copy(alpha = alfa * 0.8f), pts[i] + Offset(0.08f * kk, 0f), pts[i + 1] + Offset(0.08f * k(ZS[i + 1]), 0f), (0.035f * kk).coerceIn(0.25f, 2f), StrokeCap.Round)
                }
            }
            // Las huellas de botas y de cascos, muchas, que se tapan unas a otras.
            val n = (110 * huellas).toInt() + 20
            repeat(n) {
                val z = 3.4f * kotlin.math.exp(az.entre(0f, 3.4f)); val x = (bordeIzq(z) + bordeDer(z)) / 2f + az.entre(-1.6f, 1.6f)
                val q = w(x, z); val kk = k(z); val lejos = (z / 120f).coerceAtMost(0.85f)
                val col = p.arriba(lerp(pisada, C.CENIZA, 0.35f), lejos).copy(alpha = 0.6f * (0.35f + 0.65f * huellas))
                if (az.real() < 0.75f) {
                    // Una bota: un óvalo alargado hacia el fondo, con su tacón.
                    drawOval(col, Offset(q.x - 0.05f * kk, q.y - 0.02f * kk), Size(0.1f * kk, 0.05f * kk))
                    drawOval(col, Offset(q.x - 0.04f * kk, q.y + 0.04f * kk), Size(0.08f * kk, 0.03f * kk))
                } else {
                    // Un casco: una herradura.
                    drawArc(col, 200f, 140f, false, Offset(q.x - 0.07f * kk, q.y - 0.05f * kk), Size(0.14f * kk, 0.09f * kk), style = Stroke((0.025f * kk).coerceIn(0.25f, 2f)))
                }
            }
            // El trineo del emperador: dos patines recién pasados por encima de todo.
            if (EscenaNapoleon.trineo(v.m)) for (dx in listOf(-0.42f, 0.42f)) {
                val pts = ZS.map { z -> w(-1.5f + dx + RUMBO * z + 0.2f * sin(z * 0.13f), z) }
                drawPath(curva(pts), p.arriba(C.NIEVE_SOMBRA, 0.2f).copy(alpha = 0.85f), style = Stroke(1.4f, cap = StrokeCap.Round))
                drawPath(curva(pts.map { it + Offset(0.8f, -0.3f) }), p.arriba(lerp(C.NIEVE, Color.White, 0.4f), 0.2f).copy(alpha = 0.7f), style = Stroke(0.6f, cap = StrokeCap.Round))
            }
            // Hojas y paja en el barro.
            if (nieve < 0.5f) repeat(40) {
                val z = 3.4f * kotlin.math.exp(az.entre(0f, 2.4f)); val x = (bordeIzq(z) + bordeDer(z)) / 2f + az.entre(-1.7f, 1.7f)
                val q = w(x, z); val kk = k(z)
                drawOval(p.arriba(if (az.real() < 0.6f) C.HOJA else lerp(C.HOJA, C.ROJO, 0.4f), 0.1f), Offset(q.x - 0.04f * kk, q.y - 0.015f * kk), Size(0.08f * kk, 0.03f * kk))
            }
        }
        // El canto del camino: una franja de paso entre lo pisado y el campo (regla 9).
        for (orilla in listOf(IZQ, DER)) drawPath(curva(orilla), p.arriba(if (nieve > 0.4f) C.NIEVE_SOMBRA else lerp(C.BARRO, C.HOJA, 0.4f), 0.2f).copy(alpha = 0.6f), style = Stroke(1f, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- El bosque

    fun DrawScope.bosque(v: VistaNapoleon) {
        val az = Azar(Semilla.de("napoleon", "bosque", "masa"))
        // La masa del bosque, oscura, del pie del bosque hacia arriba: lo que se ve entre pino y pino.
        val pinos = PINOS.sortedByDescending { it.z }
        val tope = pinos.filter { it.fila == 2 }.sortedBy { it.pie.x }.map { Offset(it.pie.x, it.pie.y - it.altoL * 0.86f) }
        val masa = poligono(listOf(Offset(-20f, -20f)) + tope.filter { it.x > -20f && it.x < FUGA.x + 2f } + listOf(FUGA + Offset(4f, 0.5f)) + BOSQUE.reversed() + listOf(Offset(-20f, BOSQUE.first().y)))
        val a = v.a
        drawPath(masa, Brush.horizontalGradient(0f to a.plano(C.PINO_OSC, Offset(60f, 140f), 0f, 0.15f), 0.6f to a.plano(C.PINO_OSC, Offset(160f, 130f), 0f, 0.45f), 1f to a.plano(C.PINO, FUGA, 0f, 0.9f), startX = 0f, endX = FUGA.x))
        // Los troncos de dentro, verticales y oscuros, junto al pie.
        clipPath(masa) {
            repeat(70) {
                val z = 10f * kotlin.math.exp(az.entre(0f, 3.6f)); val x = pieDelBosque(z) - az.entre(0.5f, 6f)
                val q = w(x, z); val kk = k(z)
                drawLine(a.plano(lerp(C.MADERA_OSC, C.CENIZA, 0.5f), q, 0f, (z / 160f).coerceAtMost(0.9f)), q, q + Offset(0f, -az.entre(2f, 5f) * kk), (0.22f * kk).coerceIn(0.3f, 9f))
            }
        }
        // Los pinos, de atrás adelante: los lejanos, siluetas con poco detalle; los del borde, enteros.
        for (pn in pinos) {
            if (v.tramo == Tramo.BEREZINA && pn.z > RetiradaLejos.ORILLA_ACA - 1f && pn.z < RetiradaLejos.ORILLA_ALLA + 2f) continue
            val lejos = (pn.z / 180f).coerceIn(0f, 0.9f) + 0.12f * pn.fila
            val det = (pn.k / 24f).coerceIn(0.15f, 1f) * (1f - 0.3f * pn.fila)
            val q = pn.pie
            if (q.x < -pn.anchoL || q.x > ANCHO + pn.anchoL) continue
            if (pn.albar) pinoAlbar(v.en(q), q, pn.altoL, pn.anchoL, v.nieve * 0.7f, Azar(pn.semilla), lejos.coerceAtMost(0.92f), det)
            else pino(v.en(q), q, pn.altoL, pn.anchoL, v.nieve * (if (pn.fila == 0) 0.62f else 0.45f), Azar(pn.semilla), lejos.coerceAtMost(0.92f), det, pn.roto)
        }
        // El pie del bosque: la nieve amontonada contra los troncos, con su canto azul, y la sombra honda de dentro.
        val pie = BOSQUE
        if (v.nieve > 0.3f) {
            val banco = pie.map { it + Offset(0f, -2f) }
            val forma = poligono(pie.map { it + Offset(0f, 1.5f) } + banco.reversed().mapIndexed { i, q -> q + Offset(0f, -(0.35f * k(ZS[ZS.size - 1 - i])).coerceAtMost(9f) * v.nieve) })
            drawPath(forma, v.p.arriba(C.NIEVE, 0.2f))
            drawPath(curva(pie), v.a.plano(C.NIEVE_SOMBRA, pie[3], 0f, 0.2f), style = Stroke(1.2f))
        } else drawPath(curva(pie), v.p.arriba(lerp(C.HOJA, C.BARRO, 0.5f), 0.2f), style = Stroke(2.2f))
    }

    // ---------------------------------------------------------------- Lo abandonado

    /** El sitio del cañón, a la izquierda del camino, en el plano medio, y el de su armón, detrás. */
    val CANON: Sitio = PiezasNapoleon.sitio(MIRADA, w(bordeIzq(20f) - 0.35f, 20f), 0.62f)
    val ARMON: Sitio = PiezasNapoleon.sitio(MIRADA, w(bordeIzq(25f) - 2.3f, 25f), -0.4f)

    /** Una cosa abandonada a lo largo del camino: a qué profundidad, a qué lado y qué es. */
    class Abandonada(val z: Float, val x: Float, val que: Int, val giro: Float, val semilla: Long) {
        val sitio: Sitio get() = PiezasNapoleon.sitio(MIRADA, w(x, z), giro)
    }

    /** La fila de lo abandonado, de cerca a lejos (0: carro volcado, 1: fuego apagado, 2: cofre, 3: carro de pie, 4: caja, 5: cañón). */
    private val FILA: List<Abandonada> by lazy {
        val az = Azar(Semilla.de("napoleon", "abandonadas")); val lista = ArrayList<Abandonada>()
        val tipos = listOf(0, 1, 4, 0, 1, 2, 0, 1, 5, 0, 1, 0)
        var z = 28f
        for ((i, t) in tipos.withIndex()) {
            val izq = i % 4 != 3
            val x = if (izq) bordeIzq(z) - az.entre(0.6f, 2.4f) else bordeDer(z) + az.entre(0.8f, 3f)
            lista += Abandonada(z, x, t, az.entre(-1.2f, 1.2f), az.entero(1_000_000).toLong())
            z *= az.entre(1.18f, 1.32f)
        }
        lista
    }

    /** Lo abandonado que se ve hoy: más cuanto más avanza la retirada. */
    fun fila(v: VistaNapoleon): List<Abandonada> = if (v.vivac) emptyList() else FILA.take(RetiradaNapoleon.abandonadas(v.m)).filter { v.tramo != Tramo.BEREZINA || it.z < RetiradaLejos.ORILLA_ACA - 2f }

    /** Los fuegos de la compañía a lo largo del camino (y de los otros grupos, lejos), en el llano y en la franja del bosque. */
    fun fuegosDelCamino(v: VistaNapoleon): List<Offset> {
        val n = RetiradaNapoleon.fuegosDelCamino(v.m); val az = Azar(Semilla.de("napoleon", "fuegos"))
        return (0 until 5).map { i -> val z = 16f * kotlin.math.exp(i * 0.36f + az.entre(0f, 0.2f)); val x = if (i % 2 == 0) bordeDer(z) + az.entre(1.5f, 5f) else bordeIzq(z) - az.entre(1f, 3f); w(x, z) }.take(n)
    }

    /** Los fuegos de los otros grupos, lejos en el llano. */
    fun fuegosAjenos(v: VistaNapoleon): List<Offset> {
        val az = Azar(Semilla.de("napoleon", "ajenos"))
        return (0 until RetiradaNapoleon.fuegosAjenos(v.m)).map { i -> val z = 70f + i * 26f + az.entre(0f, 20f); w(bordeDer(z) + az.entre(3f, 26f), z) }
    }

    /** Lo que tumba sombra en el plano medio: el cañón, el armón, lo abandonado. */
    fun sombras(v: VistaNapoleon): List<RetiradaLuz.Sombra> = buildList {
        if (!v.vivac) {
            add(RetiradaLuz.Sombra(CANON.pie, siluetaSimple(CANON, 1.9f, 1.5f)))
            add(RetiradaLuz.Sombra(ARMON.pie, siluetaSimple(ARMON, 1.6f, 1.3f)))
            for (c in fila(v)) if (c.que != 1) add(RetiradaLuz.Sombra(c.sitio.pie, siluetaSimple(c.sitio, 1.3f, if (c.que == 3) 1.8f else 1f)))
        }
    }

    private fun siluetaSimple(s: Sitio, ancho: Float, alto: Float): Path = poligono(listOf(s.p(-ancho, 0f, 0f), s.p(ancho, 0f, 0f), s.p(ancho * 0.8f, alto, 0f), s.p(-ancho * 0.8f, alto, 0f)))

    fun DrawScope.abandonado(v: VistaNapoleon) {
        val nieve = v.nieve
        // Los fuegos apagados y los de la compañía, círculos negros en la nieve (de noche, encendidos: su llama va en la parte viva).
        val encendidos = EscenaNapoleon.fuegosLejos(v.m) || v.fase != Fase.MARCHA
        for ((i, q) in fuegosDelCamino(v).withIndex()) {
            val z = zEn(q.y); val s = PiezasNapoleon.sitio(MIRADA, q, 0.3f * i)
            hoguera(s, v.en(q), if (encendidos) 0.7f else if (v.fase == Fase.MARCHA && v.m.hora < 10.0) 0.15f else 0f, nieve, Semilla.de("napoleon", "fuego", i), (z / 120f).coerceAtMost(0.8f))
        }
        if (v.vivac) return
        // La fila de lo abandonado, de lejos a cerca.
        for (c in fila(v).sortedByDescending { it.z }) {
            val s = c.sitio; val lejos = (c.z / 140f).coerceAtMost(0.85f); val a = v.en(s.pie)
            when (c.que) {
                0 -> carro(s, a, nieve, c.semilla, lejos)
                1 -> hoguera(s, a, 0f, nieve, c.semilla, lejos)
                2, 4 -> with(PiezasNapoleon) { caja(s, a, -0.45f, 0.45f, 0f, 0.5f, -0.3f, 0.3f, if (c.que == 2) C.MADERA_GRIS else lerp(C.PINO_LUZ, C.CAPOTE, 0.35f), lejos) }
                3 -> carro(s, a, nieve, c.semilla, lejos, cargado = true)
                else -> canon(s, a, 0.1f, nieve, lejos)
            }
        }
        // El cañón y su armón.
        val l1 = (25f / 140f); val l0 = (20f / 140f)
        armon(ARMON, v.en(ARMON.pie), nieve, l1)
        canon(CANON, v.en(CANON.pie), 0.16f, nieve, l0)
        // Un carro que pasa cargado el primer día, en el camino, lejos.
        if (EscenaNapoleon.cargados(v.m)) { val s = PiezasNapoleon.sitio(MIRADA, w(-1.8f + RUMBO * 38f, 38f), 0f); carro(s, v.en(s.pie), nieve, 77L, 38f / 140f, cargado = true) }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: VistaNapoleon, reloj: Float) {
        val encendidos = EscenaNapoleon.fuegosLejos(v.m) || v.fase != Fase.MARCHA
        for ((i, q) in fuegosDelCamino(v).withIndex()) {
            val kk = k(zEn(q.y))
            if (encendidos) {
                resplandor(q + Offset(0f, -0.3f * kk), 2.4f * kk, 0.25f + 0.6f * v.noche)
                llama(q + Offset(0f, -0.1f * kk), 0.7f * kk, reloj, i + 30)
                humo(v.l, q + Offset(0f, -0.8f * kk), 6f * kk, 0.6f * kk, reloj, v.m.viento, 0.25f, 0.4f, i + 30, 5, 0.35f)
            } else if (v.fase == Fase.MARCHA && v.m.hora < 11.0) humo(v.l, q, 5f * kk, 0.5f * kk, reloj, v.m.viento, 0.2f, 0.4f, i + 30, 5, 0.3f)
        }
        // Los fuegos de los otros grupos, lejos: de noche su luz; de día un hilo de humo.
        for ((i, q) in fuegosAjenos(v).withIndex()) {
            val kk = k(zEn(q.y))
            if (EscenaNapoleon.fuegosLejos(v.m)) { resplandor(q, 3f * kk, 0.3f + 0.6f * v.noche); llama(q, 0.8f * kk, reloj, i + 50) }
            humo(v.l, q, 9f * kk, 0.8f * kk, reloj, v.m.viento, 0.25f, 0.6f, i + 50, 5, 0.3f)
        }
        // Los fuegos apagados de la fila, de día, todavía humean un poco.
        if (!v.vivac && v.r.dia > 0.3f) for ((i, c) in fila(v).withIndex()) if (c.que == 1 && i % 2 == 0) { val q = c.sitio.pie; val kk = k(c.z); humo(v.l, q, 4f * kk, 0.5f * kk, reloj, v.m.viento, 0.3f, 0.5f, i + 70, 4, 0.25f) }
    }
}
