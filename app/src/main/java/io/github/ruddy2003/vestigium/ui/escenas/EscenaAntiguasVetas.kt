package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Senal
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Veta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAntiguas.lucecita
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.TORRENTE
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.VETAS
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.fijo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.alturaEn
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.borron
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.en
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.rota
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.sin

/**
 * La ladera de las vetas, la contradiagonal del cuadro: «al otro lado del torrente están las vetas de hierro, unas bocas
 * negras en la ladera con sus montones de escoria». Baja de detrás del pino a la garganta; arriba, la roca rojiza y en ella
 * la veta nueva según el arco (entera; una boca pequeña que humea y una senda recién marcada; tres bocas y un camino de
 * carros; hundida; tapada con piedras); abajo, junto al agua, las casas de los mineros («más pobres que las de la aldea y
 * más ruidosas»; vacías y atrancadas desde que se van) y unas terrazas de centeno.
 */
internal object VetasAntiguas {
    private val C = ColorAntiguas
    private const val LEJOS = 0.14f

    /** La forma de la ladera: su cresta y, abajo, la orilla de allá del torrente. */
    val FORMA: Path by lazy {
        val orilla = TORRENTE.filter { it.first.x <= 163f }.map { (q, w) -> q + Offset(0f, -w - 0.4f) }
        poligono(VETAS + orilla)
    }

    /** La boca de la veta nueva, en la roca rojiza; las otras dos, cuando da todo su hierro. */
    val BOCA = Offset(98f, 131f)
    private val BOCAS_NUEVAS = listOf(Offset(84f, 128f), Offset(112f, 137f))

    /**
     * Una boca vieja de la ladera: [q], dónde se abre; [r], su ancho (más chicas cuanto más arriba); [giro], cuánto se ladea
     * con la ladera, en grados; [escombrera], el largo de su lengua de escoria; [marcos], su entibado (0: la roca sola; 1: un
     * marco; 2: dos, el de dentro perdiéndose en lo oscuro); [hundida]: el monte se le vino encima y queda la hondonada con
     * dos troncos; [senda]: la que baja de ella a las casas de los mineros.
     */
    private class Vieja(val q: Offset, val r: Float, val giro: Float, val escombrera: Float, val marcos: Int, val hundida: Boolean = false, val senda: List<Offset>? = null)

    /** Cuatro bocas viejas, cada una distinta **[Diseño propio]**. */
    private val VIEJAS = listOf(
        Vieja(Offset(50f, 141f), 2.2f, -9f, 8f, 0),
        Vieja(Offset(126f, 157f), 2.8f, 4f, 9f, 0, hundida = true),
        Vieja(Offset(80f, 166f), 3.2f, 7f, 13f, 1),
        Vieja(Offset(148f, 177f), 3.9f, -4f, 19f, 2, senda = pts(148f, 180f, 157f, 188f, 150f, 196f, 156f, 204f)),
    )

    /** El camino de carros: de la boca nueva, en zetas, a las casas de los mineros. */
    val CARROS = pts(98f, 133f, 136f, 148f, 104f, 160f, 146f, 180f, 128f, 198f)

    /** La senda que abrió la guía para los mineros, con sus runas: de las casas a la boca, más empinada. */
    private val SENDA = pts(118f, 204f, 108f, 186f, 116f, 170f, 100f, 152f, 98f, 134f)

    /** Las casas de los mineros, al pie, junto al agua: dónde pisa cada una y su ancho. */
    val CASAS = listOf(Offset(104f, 214f) to 12f, Offset(120f, 210.6f) to 10f, Offset(136f, 207.8f) to 11f, Offset(152f, 205f) to 9f, Offset(166f, 203.4f) to 9f)

    fun DrawScope.ladera(v: VistaAntiguas) {
        val az = fijo("vetas")
        clipPath(FORMA) {
            if (v.nieveLadera >= 0.99f) with(VetasEnLaNieve) { ladera(v, VIEJAS.map { it.toBoca() } + bocasDeLaVeta(v)) }
            else {
                suelo(v, az)
                terrazas(v, az)
                rocaRojiza(v, az)
                for (b in VIEJAS) bocaVieja(v, b, az)
                vetaNueva(v, az)
                nieve(v, az)
            }
        }
        casas(v, az)
        // La cresta, rota por pinos sueltos y peñas, para que no sea una curva lisa.
        val az2 = fijo("vetas", "cresta")
        for (k in 0 until 16) {
            val x = 6f + k * 11f + az2.entre(-3f, 3f); val y = alturaEn(VETAS, x) + 1f
            if (k % 3 == 1) drawPath(TrazosValle.borron(Offset(x, y - 1.2f), az2.entre(2.5f, 4.5f), 1.8f, az2, 6, 0.3f, 0.8f), v.p.por(lerp(C.PIEDRA, C.TIERRA, 0.2f), v.sol.t(V3(0.4f, 0.6f, -0.7f)), LEJOS))
            else { val h = az2.entre(4f, 8f); drawPath(poligono(Offset(x, y - h), Offset(x + h * 0.3f, y + 1f), Offset(x - h * 0.3f, y + 1f)), v.p.por(lerp(C.PINO, C.ESCORIA, 0.15f), -0.1f, LEJOS)) }
        }
        // La cresta: un filo de luz (o de luna) contra la cara norte, que queda detrás.
        drawPath(curva(VETAS.dropLast(3)), (if (v.noche > 0.4f) C.LUNA.copy(alpha = 0.35f * v.luna + 0.05f) else v.p.luz(lerp(EscenaAntiguas.hierba(v.m), C.LANA, 0.3f), LEJOS).copy(alpha = 0.6f)), style = Stroke(1f))
    }

    /** La ladera: hierba abajo y roca arriba, con sus lomos y vaguadas, que mira a la derecha (le da el sol de la mañana). */
    private fun DrawScope.suelo(v: VistaAntiguas, az: Azar) {
        val p = v.p; val hierba = lerp(EscenaAntiguas.hierba(v.m), C.TIERRA, 0.22f); val roca = lerp(lerp(C.PIEDRA, C.ESCORIA, 0.25f), C.TIERRA, 0.3f)
        val t = ((v.sol.t(V3(0.55f, 0.45f, -0.7f)) - 0.25f) * 1.4f).coerceIn(-1f, 0.45f)
        drawRect(Brush.verticalGradient(0f to p.por(lerp(hierba, roca, 0.35f), t - 0.15f, LEJOS), 0.3f to p.por(hierba, t - 0.1f, LEJOS), 1f to p.por(lerp(hierba, C.PINO, 0.35f), t - 0.25f, LEJOS * 0.6f), startY = 96f, endY = 220f), Offset(-10f, 96f), Size(200f, 150f))
        // Los bosquetes de pinos: masas oscuras de copas puntiagudas, con su lado de luz.
        for ((cx, cy, r) in listOf(Triple(34f, 150f, 26f), Triple(150f, 182f, 20f), Triple(70f, 196f, 22f), Triple(118f, 138f, 12f))) {
            val bosque = ArrayList<Offset>()
            repeat((r * 1.6f).toInt()) { val q = Offset(cx + az.entre(-r, r), cy + az.entre(-r * 0.4f, r * 0.4f)); if (q.y > alturaEn(VETAS, q.x) + 4f) bosque += q }
            for (q in bosque.sortedBy { it.y }) {
                val h = 5f + (q.y - 110f) * 0.06f
                drawPath(poligono(Offset(q.x, q.y - h), Offset(q.x + h * 0.36f, q.y), Offset(q.x - h * 0.36f, q.y)), p.por(lerp(C.PINO, C.ESCORIA, 0.15f), -0.1f, LEJOS))
                drawPath(poligono(Offset(q.x, q.y - h), Offset(q.x + (if (p.solALaDerecha) h * 0.36f else -h * 0.36f), q.y), Offset(q.x, q.y)), p.luz(C.PINO, LEJOS).copy(alpha = 0.45f * p.r.dia))
            }
        }
        // Las vaguadas, en sombra del lado contrario al sol, y los lomos con su luz.
        for (x in listOf(30f, 70f, 118f, 160f)) {
            val y0 = alturaEn(VETAS, x)
            val r = rota(pts(x, y0 + 2f, x + 8f, y0 + 26f, x + 14f, y0 + 56f, x + 18f, y0 + 90f), az, 2f, 2)
            drawPath(poligono(r + r.reversed().map { it + Offset(7f, 3f) }), p.sombra(lerp(hierba, C.PINO, 0.3f), LEJOS).copy(alpha = 0.4f))
            drawPath(curva(r.map { it + Offset(-3f, 0f) }), p.luz(lerp(hierba, C.LANA, 0.2f), LEJOS).copy(alpha = 0.3f * p.r.dia), style = Stroke(2f))
        }
        val claros = ArrayList<Offset>(); val oscuros = ArrayList<Offset>()
        repeat(380) { val x = az.entre(0f, 186f); val q = Offset(x, az.entre(alturaEn(VETAS, x) + 2f, 222f)); if (az.real() < 0.5f) claros += q else oscuros += q }
        drawPoints(claros, PointMode.Points, p.luz(lerp(hierba, C.ORO, 0.25f), LEJOS).copy(alpha = 0.45f), 1f, StrokeCap.Round)
        drawPoints(oscuros, PointMode.Points, p.sombra(lerp(hierba, C.PINO, 0.4f), LEJOS).copy(alpha = 0.45f), 1f, StrokeCap.Round)
        // Pinos sueltos y matas en la ladera: manchas oscuras con su lado de luz y su sombra.
        repeat(16) {
            val x = az.entre(10f, 180f); val y = az.entre(alturaEn(VETAS, x) + 8f, 205f); if ((Offset(x, y) - BOCA).getDistance() < 26f) return@repeat
            val w = 1.8f + (y - 100f) * 0.03f
            drawOval(p.r.color.copy(alpha = 0.4f * p.r.fuerza), Offset(x - w - (if (p.solALaDerecha) 2f else -2f) * w, y - 0.6f), Size(w * 3f, w * 0.7f), blendMode = BlendMode.Multiply)
            drawPath(poligono(Offset(x, y - w * 3.6f), Offset(x + w, y), Offset(x - w, y)), p.por(C.PINO, 0.1f, LEJOS))
            drawPath(poligono(Offset(x, y - w * 3.6f), Offset(x + (if (p.solALaDerecha) w else -w), y), Offset(x, y)), p.luz(C.PINO, LEJOS).copy(alpha = 0.4f * p.r.dia))
        }
    }

    /** Las terrazas de centeno de la aldea, en la ladera baja: verdes en primavera, de oro en verano, segadas (con sus gavillas la semana de la siega) y pardas en invierno. */
    private fun DrawScope.terrazas(v: VistaAntiguas, az: Azar) {
        val p = v.p; val md = v.m.ficha.dia.substring(5)
        val segado = md >= "09-06" || md < "03-01"
        val centeno = when {
            segado -> lerp(C.ORO, C.TIERRA, 0.5f)
            md < "06-01" -> lerp(C.ALERCE, C.PRADO, 0.4f)
            md < "07-20" -> lerp(C.ALERCE, C.ORO, 0.45f)
            else -> lerp(C.ORO, C.LANA, 0.2f)
        }
        for (k in 0 until 4) {
            val y = 180f + k * 7.4f; val x0 = 18f + k * 3f; val x1 = 92f - k * 4f
            val banda = poligono(pts(x0, y, x1, y - 2.4f, x1 + 1f, y + 5f, x0 - 1f, y + 7f))
            drawPath(banda, p.por(centeno, 0.25f, LEJOS))
            // El murete de piedra seca del borde de abajo.
            drawLine(p.sombra(C.PIEDRA, LEJOS).copy(alpha = 0.8f), Offset(x0 - 1f, y + 7f), Offset(x1 + 1f, y + 5f), 1.4f)
            drawLine(p.luz(C.PIEDRA, LEJOS).copy(alpha = 0.5f), Offset(x0 - 1f, y + 6.3f), Offset(x1 + 1f, y + 4.3f), 0.5f)
            if (!segado) clipPath(banda) { repeat(30) { val q = Offset(az.entre(x0, x1), az.entre(y - 2f, y + 6f)); drawLine(p.luz(lerp(centeno, C.LANA, 0.3f), LEJOS).copy(alpha = 0.5f), q, q + Offset(0.6f, -1.6f), 0.4f) } }
            if (Senal.GAVILLAS in v.senales) for (j in 0 until 6) { val q = Offset(x0 + 6f + j * (x1 - x0 - 10f) / 6f, y + 4f); drawPath(poligono(q + Offset(-1.2f, 0f), q + Offset(1.2f, 0f), q + Offset(0.4f, -3.4f), q + Offset(-0.4f, -3.4f)), p.por(C.ORO, 0.3f, LEJOS)); drawLine(p.sombra(C.ORO, LEJOS), q + Offset(-1f, -1.4f), q + Offset(1f, -1.4f), 0.4f) }
        }
    }

    /** La roca rojiza de arriba, donde se abre la veta nueva: un crestón quebrado con su cara de luz y su polvo rojo. */
    private fun DrawScope.rocaRojiza(v: VistaAntiguas, az: Azar) {
        val p = v.p
        val forma = TrazosValle.cerrada(rota(pts(68f, 120f, 84f, 114f, 102f, 118f, 122f, 126f, 128f, 140f, 114f, 147f, 92f, 145f, 74f, 138f), az, 1.6f, 2))
        val roca = lerp(lerp(C.ROJIZA, C.PIEDRA_OSC, 0.4f), C.TIERRA, 0.15f)
        val t = v.sol.t(V3(0.5f, 0.4f, -0.75f))
        drawPath(forma, p.por(roca, t - 0.2f, LEJOS))
        clipPath(forma) {
            // Los bloques del crestón, cada uno con su cara de luz, y las grietas.
            repeat(14) {
                val q = Offset(az.entre(68f, 126f), az.entre(116f, 146f)); val w = az.entre(4f, 9f)
                val bloque = TrazosValle.borron(q, w, w * 0.6f, az, 6, 0.25f, 0.9f)
                drawPath(bloque, p.por(lerp(roca, C.ROJIZA, az.entre(0f, 0.4f)), t + az.entre(-0.3f, 0.2f), LEJOS))
                drawLine(p.luz(lerp(roca, C.ORO, 0.3f), LEJOS).copy(alpha = 0.4f * p.r.dia), q + Offset(-w * 0.6f, -w * 0.4f), q + Offset(w * 0.5f, -w * 0.5f), 0.6f)
                drawLine(p.sombra(lerp(roca, C.ESCORIA, 0.5f), LEJOS).copy(alpha = 0.7f), q + Offset(-w * 0.6f, w * 0.45f), q + Offset(w * 0.6f, w * 0.4f), 0.7f)
            }
        }
    }

    private fun Vieja.toBoca() = VetasEnLaNieve.Boca(q, r, giro, escombrera, hundida)

    /** Las bocas de la veta nueva que hay hoy (para taparlas de nieve): ninguna con la roca entera o la boca hundida. */
    private fun bocasDeLaVeta(v: VistaAntiguas): List<VetasEnLaNieve.Boca> = when (EscenaAntiguas.veta(v.m)) {
        Veta.ENTERA -> emptyList()
        Veta.HUNDIDA -> listOf(VetasEnLaNieve.Boca(BOCA, 3.4f, 0f, 14f, true))
        Veta.PEQUENA -> listOf(VetasEnLaNieve.Boca(BOCA, 2.8f, 0f, 9f))
        Veta.MEJOR -> BOCAS_NUEVAS.map { VetasEnLaNieve.Boca(it, 2.4f, 0f, 8f) } + VetasEnLaNieve.Boca(BOCA, 3.4f, 0f, 16f)
        Veta.TAPADA -> listOf(VetasEnLaNieve.Boca(BOCA, 3.4f, 0f, 14f))
    }

    /**
     * Una boca vieja: la escombrera de escoria, ya con hierba por los bordes, la senda que baja de ella y el hueco con su
     * entibado, ladeado con la ladera; o, la hundida, su hondonada.
     */
    private fun DrawScope.bocaVieja(v: VistaAntiguas, b: Vieja, az: Azar) {
        val p = v.p; val hierba = lerp(EscenaAntiguas.hierba(v.m), C.TIERRA, 0.22f)
        b.senda?.let { drawPath(curva(it), p.por(lerp(C.TIERRA, C.LANA, 0.35f), 0.2f, LEJOS).copy(alpha = 0.7f), style = Stroke(0.8f, cap = StrokeCap.Round)) }
        if (b.hundida) { hondonada(p, b, hierba, az); return }
        escombrera(p, b.q, b.escombrera, lerp(lerp(C.PIEDRA_OSC, C.ESCORIA, 0.2f), C.TIERRA, 0.15f), az, hierba)
        rotate(b.giro, b.q) { boca(p, b.q, b.r, b.marcos) }
    }

    /** La boca hundida: la media luna de tierra clara donde se vino abajo el monte, su sombra, dos troncos tumbados y la escombrera comida por la hierba. */
    private fun DrawScope.hondonada(p: Pincel, b: Vieja, hierba: Color, az: Azar) {
        val q = b.q; val r = b.r
        escombrera(p, q + Offset(0f, 1f), b.escombrera, lerp(C.PIEDRA_OSC, hierba, 0.45f), az, hierba)
        drawPath(poligono(listOf(q + Offset(-r * 1.8f, -r * 0.1f), q + Offset(-r * 1.2f, -r * 1.5f), q + Offset(r * 0.2f, -r * 1.9f), q + Offset(r * 1.6f, -r * 1.2f), q + Offset(r * 1.9f, 0.2f), q + Offset(r * 1.1f, -r * 0.7f), q + Offset(-r * 0.9f, -r * 0.8f))), p.por(lerp(C.TIERRA, C.ROJIZA, 0.3f), 0.25f, LEJOS))
        drawPath(TrazosValle.borron(q + Offset(0f, -r * 0.4f), r * 1.3f, r * 0.5f, az, 8, 0.3f), p.sombra(lerp(C.TIERRA, C.ESCORIA, 0.4f), LEJOS))
        val madera = p.por(C.MADERA, 0f, LEJOS)
        drawLine(madera, q + Offset(-r, -r * 0.1f), q + Offset(-r * 0.2f, -r * 1.2f), 0.8f)
        drawLine(madera, q + Offset(r * 0.3f, 0f), q + Offset(r * 1.3f, -r * 0.6f), 0.8f)
    }

    /**
     * La escombrera de una boca: la explanada plana delante y la lengua de escoria que cae por la ladera, ancha abajo, con
     * su lado de luz, sus regueros y las piedras que ruedan hasta el pie.
     */
    private fun DrawScope.escombrera(p: Pincel, q: Offset, largo: Float, color: Color, az: Azar, abajo: Color = color) {
        val lado = if (p.solALaDerecha) 1f else -1f
        val explanada = poligono(q + Offset(-largo * 0.32f, 0.4f), q + Offset(largo * 0.4f, 0.4f), q + Offset(largo * 0.34f, 2.2f), q + Offset(-largo * 0.28f, 2.4f))
        val lengua = poligono(rota(listOf(q + Offset(-largo * 0.22f, 2f), q + Offset(largo * 0.3f, 2f), q + Offset(largo * 0.55f, largo * 0.7f), q + Offset(largo * 0.62f, largo * 1.15f)), az, 1.2f, 2) + rota(listOf(q + Offset(largo * 0.62f, largo * 1.15f), q + Offset(largo * 0.2f, largo * 1.25f), q + Offset(-largo * 0.25f, largo * 1.18f), q + Offset(-largo * 0.5f, largo * 1.05f)), az, 1.6f, 2) + listOf(q + Offset(-largo * 0.42f, largo * 0.6f)))
        // De su color arriba al de la ladera abajo, donde la escoria se esparce y la hierba la va tapando.
        drawPath(lengua, Brush.verticalGradient(listOf(p.por(color, 0f, LEJOS), p.por(lerp(color, abajo, 0.6f), 0f, LEJOS)), q.y, q.y + largo * 1.2f))
        clipPath(lengua) {
            drawRect(Brush.horizontalGradient(listOf(p.sombra(color, LEJOS).copy(alpha = if (lado > 0f) 0.55f else 0f), Color.Transparent, p.luz(lerp(color, C.PIEDRA, 0.3f), LEJOS).copy(alpha = if (lado > 0f) 0f else 0.35f)), q.x - largo * 0.5f, q.x + largo * 0.6f), Offset(q.x - largo, q.y), Size(largo * 2f, largo * 1.2f))
            for (k in 0..3) { val x = q.x - largo * 0.2f + k * largo * 0.18f; drawLine(p.sombra(lerp(color, C.ESCORIA, 0.4f), LEJOS).copy(alpha = 0.5f), Offset(x, q.y + 3f), Offset(x + az.entre(-2f, 3f), q.y + largo * 1.05f), 0.6f) }
            drawPoints((0 until (largo * 2.4f).toInt()).map { q + Offset(az.entre(-largo * 0.45f, largo * 0.5f), az.entre(2f, largo * 1.1f)) }, PointMode.Points, p.luz(lerp(color, C.PIEDRA, 0.55f), LEJOS).copy(alpha = 0.6f), 0.9f, StrokeCap.Round)
        }
        drawPath(explanada, p.por(lerp(color, C.TIERRA, 0.3f), 0.35f, LEJOS))
        // Las piedras que ruedan hasta el pie de la lengua.
        repeat(5) { drawCircle(p.por(color, 0.1f, LEJOS), az.entre(0.5f, 1.1f), q + Offset(az.entre(-largo * 0.3f, largo * 0.5f), largo * az.entre(1.05f, 1.3f))) }
    }

    /**
     * La boca de una mina de [r] de ancho: el hueco negro y hondo, con su entibado (con [marcos] 1, los dos pies de troncos y
     * el cabezal; con 2, además, otro marco más adentro que se pierde en lo oscuro; con 0, la roca sola), el canto de la
     * roca de encima y su sombra.
     */
    private fun DrawScope.boca(p: Pincel, q: Offset, r: Float, marcos: Int = 2) {
        val alto = r * 1.5f
        // El corte en la ladera: el labio de roca encima, en sombra, de borde roto.
        drawPath(poligono(listOf(q + Offset(-r * 1.4f, -alto * 0.6f), q + Offset(-r * 0.9f, -alto - r * 0.55f), q + Offset(0f, -alto - r * 0.75f), q + Offset(r * 1f, -alto - r * 0.5f), q + Offset(r * 1.4f, -alto * 0.55f), q + Offset(r, -alto), q + Offset(-r, -alto))), p.sombra(lerp(C.ROJIZA, C.ESCORIA, 0.45f), LEJOS).copy(alpha = 0.75f))
        drawPath(poligono(q + Offset(-r, 0.6f), q + Offset(r, 0.6f), q + Offset(r * 0.9f, -alto), q + Offset(-r * 0.9f, -alto)), p.medio(C.ESCORIA, LEJOS))
        if (marcos == 0) return
        val madera = p.por(C.MADERA, 0.2f, LEJOS); val hondo = p.sombra(C.MADERA_OSC, LEJOS)
        // El marco de dentro, más chico y oscuro.
        if (marcos >= 2) {
            drawLine(hondo, q + Offset(-r * 0.55f, 0.4f), q + Offset(-r * 0.5f, -alto * 0.7f), 0.5f); drawLine(hondo, q + Offset(r * 0.55f, 0.4f), q + Offset(r * 0.5f, -alto * 0.7f), 0.5f)
            drawLine(hondo, q + Offset(-r * 0.6f, -alto * 0.7f), q + Offset(r * 0.6f, -alto * 0.7f), 0.5f)
        }
        // El marco de la boca: los pies, el cabezal que sobresale y su sombra.
        drawLine(madera, q + Offset(-r, 0.6f), q + Offset(-r * 0.92f, -alto), 0.9f); drawLine(madera, q + Offset(r, 0.6f), q + Offset(r * 0.92f, -alto), 0.9f)
        drawLine(madera, q + Offset(-r * 1.3f, -alto), q + Offset(r * 1.3f, -alto), 1.1f, StrokeCap.Round)
        drawLine(p.luz(C.FRESNO, LEJOS).copy(alpha = 0.5f * p.r.dia), q + Offset(-r * 1.3f, -alto - 0.5f), q + Offset(r * 1.3f, -alto - 0.5f), 0.4f)
    }

    /**
     * La veta nueva, según [EscenaAntiguas.veta]: la roca entera; una boca pequeña con su polvo rojo y la senda de la guía;
     * tres bocas, el montón rojo y el camino de carros con sus carros; la boca hundida (con el polvo y las linternas del
     * rescate los días del derrumbe); la boca tapada con piedras (y las lámparas del luto del Hierro).
     */
    private fun DrawScope.vetaNueva(v: VistaAntiguas, az: Azar) {
        val p = v.p; val veta = EscenaAntiguas.veta(v.m)
        if (EscenaAntiguas.sendaDeLaGuia(v.m)) senda(v, az)
        when (veta) {
            Veta.ENTERA -> Unit
            Veta.PEQUENA -> { montonRojo(p, BOCA, 10f, az); boca(p, BOCA, 2.8f, 1) }
            Veta.MEJOR -> { caminoDeCarros(v); montonRojo(p, BOCA, 18f, az); for ((i, q) in BOCAS_NUEVAS.withIndex()) { montonRojo(p, q, 7f + 2f * i, az); rotate(if (i == 0) -6f else 5f, q) { boca(p, q, 2.4f, i) } }; boca(p, BOCA, 3.6f); carros(v) }
            Veta.HUNDIDA -> { caminoDeCarros(v); montonRojo(p, BOCA, 20f, az); hundida(v, az) }
            Veta.TAPADA -> { caminoDeCarros(v, viejo = true); montonRojo(p, BOCA, 18f, az); tapada(v, az) }
        }
        if (Senal.POLVO_ROJO in v.senales || Senal.BOCA_HUNDIDA in v.senales) mancha(BOCA + Offset(4f, -8f), 22f, 10f, p.medio(lerp(C.ROJIZA, C.LANA, 0.4f), LEJOS), 0.5f)
    }

    /** El montón de roca sacada de la boca: rojo, en abanico, con su lado de sombra. */
    private fun DrawScope.montonRojo(p: Pincel, q: Offset, tam: Float, az: Azar) {
        escombrera(p, q, tam * 1.1f, lerp(lerp(C.ROJIZA, C.TIERRA, 0.3f), C.ESCORIA, 0.1f), az)
        val fan = poligono(q + Offset(-2f, 0.6f), q + Offset(3f, 0.6f), q + Offset(tam * 0.9f, tam * 1.1f), q + Offset(-tam * 0.2f, tam * 1.3f), q + Offset(-tam * 0.6f, tam * 0.9f))
        val tierra = lerp(lerp(C.ROJIZA, C.TIERRA, 0.35f), C.ESCORIA, 0.15f)
        drawPath(fan, p.por(tierra, 0f, LEJOS))
        clipPath(fan) { drawPoints((0 until (tam * 3).toInt()).map { q + Offset(az.entre(-tam * 0.6f, tam), az.entre(0.5f, tam * 1.3f)) }, PointMode.Points, p.sombra(tierra, LEJOS).copy(alpha = 0.8f), 1f, StrokeCap.Round)
            drawPoints((0 until (tam * 2).toInt()).map { q + Offset(az.entre(-tam * 0.6f, tam), az.entre(0.5f, tam * 1.3f)) }, PointMode.Points, p.luz(lerp(tierra, C.ORO, 0.3f), LEJOS).copy(alpha = 0.6f), 0.7f, StrokeCap.Round) }
    }

    /** La senda de la guía: un hilo de tierra con piedras marcadas pequeñas, recién grabadas el día que la abre. */
    private fun DrawScope.senda(v: VistaAntiguas, az: Azar) {
        val p = v.p
        drawPath(curva(SENDA), p.por(lerp(C.TIERRA, C.LANA, 0.4f), 0.2f, LEJOS).copy(alpha = 0.8f), style = Stroke(1f, cap = StrokeCap.Round))
        for (t in listOf(0.2f, 0.45f, 0.7f)) with(PasoAntiguas) { mojon(p, en(SENDA, t) + Offset(2.4f, 0.4f), 0.55f, Runa.CAMINO, az, LEJOS, fresca = if (Senal.RUNAS_FRESCAS in v.senales) 1f else 0f) }
    }

    /** El camino de carros, en zetas, con sus rodadas. [viejo]: sin uso, comido por la hierba. */
    private fun DrawScope.caminoDeCarros(v: VistaAntiguas, viejo: Boolean = false) {
        val p = v.p; val tierra = lerp(C.TIERRA, C.ROJIZA, 0.25f)
        drawPath(curva(CARROS.map { it + Offset(0f, 1.2f) }), p.sombra(tierra, LEJOS).copy(alpha = if (viejo) 0.25f else 0.55f), style = Stroke(1.6f, cap = StrokeCap.Round))
        drawPath(curva(CARROS), p.por(tierra, 0.3f, LEJOS).copy(alpha = if (viejo) 0.4f else 0.95f), style = Stroke(2.2f, cap = StrokeCap.Round))
    }

    /** Los carros de hierro que «bajan cargados todo el día» (sin mulas ni hombres a esta distancia): cajones con sus ruedas. El día de los carros, más. */
    private fun DrawScope.carros(v: VistaAntiguas) {
        if (v.noche > 0.5f || v.invierno) return
        val n = if (Senal.CARROS in v.senales) 5 else 2
        val corre = (v.m.hora.toFloat() * 0.07f) % 1f
        for (j in 0 until n) {
            val q = en(CARROS, ((j.toFloat() / n + corre) % 1f) * 0.92f + 0.04f)
            drawOval(v.p.r.color.copy(alpha = 0.4f * v.p.r.fuerza), q + Offset(-3f, -0.5f), Size(6f, 1.2f), blendMode = BlendMode.Multiply)
            drawRect(v.p.por(C.MADERA, 0.2f, LEJOS), q + Offset(-2.4f, -3.2f), Size(4.8f, 2.2f))
            drawRect(v.p.por(lerp(C.ROJIZA, C.ESCORIA, 0.3f), 0.3f, LEJOS), q + Offset(-2.2f, -3.9f), Size(4.4f, 0.9f))
            for (dx in listOf(-1.4f, 1.4f)) drawCircle(v.p.medio(C.ESCORIA, LEJOS), 0.8f, q + Offset(dx, -0.8f))
        }
    }

    /** La boca hundida: la roca caída tapando el hueco, los troncos del marco rotos y, los días del derrumbe, las linternas del rescate. */
    private fun DrawScope.hundida(v: VistaAntiguas, az: Azar) {
        val p = v.p
        // El monte caído sobre la boca, la cicatriz clara en la roca de encima y los troncos del entibado rotos.
        drawPath(TrazosValle.borron(BOCA + Offset(0f, -9f), 8f, 5f, az, 9, 0.35f), p.por(lerp(C.ROJIZA, C.LANA, 0.35f), 0.3f, LEJOS))
        drawPath(TrazosValle.borron(BOCA + Offset(0f, -2f), 9f, 5.4f, az, 9, 0.35f), p.por(lerp(C.ROJIZA, C.PIEDRA, 0.4f), 0.1f, LEJOS))
        val madera = p.por(C.MADERA, 0.1f, LEJOS)
        drawLine(madera, BOCA + Offset(-6f, 0f), BOCA + Offset(-1.6f, -7f), 1f); drawLine(madera, BOCA + Offset(6f, 0.4f), BOCA + Offset(0.8f, -6f), 1f); drawLine(madera, BOCA + Offset(-5f, -4f), BOCA + Offset(5f, -2f), 0.9f)
        repeat(10) { drawCircle(p.por(C.PIEDRA, -0.1f, LEJOS), az.entre(0.7f, 1.8f), BOCA + Offset(az.entre(-9f, 9f), az.entre(-6f, 2f))) }
        if (Senal.BOCA_HUNDIDA in v.senales) for (k in 0..3) lucecita(BOCA + Offset(-7f + k * 4.6f, 2f + (k % 2)), 0.7f, maxOf(0.6f, v.noche))
    }

    /** La boca tapada con piedras: un muro de piedra seca en el hueco. El día del Hierro sin fiesta, sus lámparas encendidas. */
    private fun DrawScope.tapada(v: VistaAntiguas, az: Azar) {
        val p = v.p
        boca(p, BOCA, 3.8f)
        // El muro de piedra seca que cierra el hueco, piedra a piedra, más claro que la boca.
        for (fila in 0..3) for (k in 0..3) drawCircle(p.por(lerp(C.PIEDRA, C.LANA, 0.2f), 0.35f - fila * 0.08f, LEJOS), 0.95f, BOCA + Offset(-2.8f + k * 1.85f + (fila % 2) * 0.9f, -0.6f - fila * 1.3f))
        if (Senal.LAMPARAS_EN_LA_BOCA in v.senales) for (k in 0..2) lucecita(BOCA + Offset(-5f + k * 5f, 1.4f), 0.75f, maxOf(0.7f, v.noche))
    }

    /** La nieve que va y viene en la ladera: como la del paso, de arriba abajo; las bocas que tapa, con su visera. La entera, en [VetasEnLaNieve]. */
    private fun DrawScope.nieve(v: VistaAntiguas, az: Azar) {
        val n = v.nieveLadera; if (n <= 0.02f || n >= 0.99f) return
        val p = v.p
        run {
            // La ladera de las vetas es más baja que el paso: la nieve se le va antes.
            val k = ((n - 0.35f) / 0.65f); if (k <= 0f) return
            with(PiezasAntiguas) { manchasDeNieve(p, -10f, 186f, { x -> alturaEn(VETAS, x) }, { x -> alturaEn(VETAS, x) + 6f + 120f * k + 5f * sin(x * 0.13f) }, az, LEJOS, 1.2f) }
            for (b in VIEJAS.map { it.toBoca() } + bocasDeLaVeta(v)) if (b.q.y < alturaEn(VETAS, b.q.x) + 6f + 120f * k) with(VetasEnLaNieve) { boca(p, b) }
        }
    }

    /**
     * Las casas de los mineros, al pie de la ladera: casuchas de piedra con techo de losa y tepe, su puerta y su ventana
     * (encendida de noche) y su humo. Desde que se van, las puertas atrancadas con tablas, sin humo ni luz, y el perro que
     * no quiso irse.
     */
    private fun DrawScope.casas(v: VistaAntiguas, az: Azar) {
        val p = v.p; val vacias = EscenaAntiguas.casasVacias(v.m); val luz = v.encendido.mineros * v.noche
        for ((i, cw) in CASAS.withIndex()) {
            val (q, w) = cw; val h = w * 0.55f
            val pe = v.pincelEn(q)
            drawOval(pe.r.color.copy(alpha = 0.45f * pe.r.fuerza), q + Offset(-w * 0.6f - (if (pe.solALaDerecha) 3f else -3f), -1f), Size(w * 1.5f, 2.4f), blendMode = BlendMode.Multiply)
            val frente = poligono(q + Offset(-w * 0.5f, 0.5f), q + Offset(w * 0.25f, 0.5f), q + Offset(w * 0.25f, -h), q + Offset(-w * 0.5f, -h))
            val lado = poligono(q + Offset(w * 0.25f, 0.5f), q + Offset(w * 0.55f, -0.6f), q + Offset(w * 0.55f, -h - 0.8f), q + Offset(w * 0.25f, -h))
            drawPath(frente, pe.por(lerp(C.PIEDRA, C.TIERRA, 0.3f), v.sol.t(V3(-0.3f, 0f, -0.95f)), LEJOS))
            drawPath(lado, pe.por(lerp(C.PIEDRA, C.TIERRA, 0.3f), v.sol.t(V3(0.95f, 0f, -0.3f)), LEJOS))
            val techo = poligono(q + Offset(-w * 0.62f, -h + 0.3f), q + Offset(w * 0.3f, -h + 0.3f), q + Offset(w * 0.62f, -h - 1f), q + Offset(w * 0.05f, -h - w * 0.42f), q + Offset(-w * 0.4f, -h - w * 0.36f))
            drawPath(techo, pe.por(lerp(C.LOSA, C.MUSGO, 0.35f), v.sol.t(V3(0f, 0.8f, -0.55f)), LEJOS))
            if (v.nieveAbajo > 0.2f) drawPath(techo, pe.por(C.NIEVE, 0.5f, LEJOS).copy(alpha = v.nieveAbajo))
            val puerta = q + Offset(-w * 0.26f, 0.5f)
            drawRect(pe.medio(C.ESCORIA, LEJOS), puerta + Offset(0f, -h * 0.75f), Size(w * 0.18f, h * 0.75f))
            if (vacias) { drawLine(pe.por(C.FRESNO, 0.2f, LEJOS), puerta + Offset(-0.4f, -h * 0.65f), puerta + Offset(w * 0.2f, -h * 0.1f), 0.5f); drawLine(pe.por(C.FRESNO, 0.2f, LEJOS), puerta + Offset(-0.4f, -h * 0.1f), puerta + Offset(w * 0.2f, -h * 0.65f), 0.5f) }
            val ventana = q + Offset(w * 0.02f, -h * 0.6f)
            if (luz > 0.05f && !vacias && i != 3) { drawRect(lerp(C.LUMBRE, Color(0xFFFFE0A0), 0.4f).copy(alpha = luz), ventana, Size(1.4f, 1.3f)); resplandor(ventana + Offset(0.7f, 0.6f), 6f, 0.5f * luz, C.LUMBRE) }
            else drawRect(pe.medio(C.ESCORIA, LEJOS).copy(alpha = 0.8f), ventana, Size(1.4f, 1.3f))
        }
        if (vacias && v.noche < 0.6f) {
            // El perro que no quiso irse, echado delante de la última casa.
            val q = CASAS[2].first + Offset(6f, 3f)
            drawOval(p.por(lerp(C.CUERO, C.LANA, 0.3f), 0.2f, LEJOS), q + Offset(-2f, -1.6f), Size(4f, 1.6f))
            drawCircle(p.por(lerp(C.CUERO, C.LANA, 0.3f), 0.25f, LEJOS), 0.9f, q + Offset(2f, -1.9f))
        }
    }

    /** Lo que se mueve de la ladera de las vetas: el humo de la boca nueva y el de las casas de los mineros. */
    fun DrawScope.humos(v: VistaAntiguas, reloj: Float) {
        val l = v.l; val veta = EscenaAntiguas.veta(v.m)
        val trabajo = v.m.hora >= 6.0 && v.m.hora < 19.0 && !v.invierno && EscenaAntiguas.hayMineros(v.m)
        if (trabajo && (veta == Veta.PEQUENA || veta == Veta.MEJOR)) humo(l, BOCA + Offset(0f, -4f), 40f, 5f, reloj, v.m.viento, 0.3f, 0.15f, 31, 7, 0.55f)
        if (EscenaAntiguas.hayMineros(v.m)) for ((i, cw) in CASAS.withIndex()) if (i % 2 == 0) humo(l, cw.first + Offset(cw.second * 0.1f, -cw.second * 0.95f), 26f, 2.4f, reloj, v.m.viento, 0.25f, 0.15f, 40 + i, 5, 0.4f)
    }

    /** La fila de linternas de los mineros que bajan de las vetas al anochecer, «como una procesión» (parte viva). */
    fun DrawScope.filaDeLinternas(v: VistaAntiguas, reloj: Float) {
        val n = v.encendido.filaDeMineros; if (n == 0 || v.noche < 0.3f) return
        val ruta = if (EscenaAntiguas.sendaDeLaGuia(v.m)) SENDA.reversed() else CARROS
        for (j in 0 until n) {
            val t = ((reloj * 0.006f + j / n.toFloat() * 0.8f) % 1f)
            lucecita(en(ruta, t), 0.55f, v.noche * 0.9f)
        }
    }
}
