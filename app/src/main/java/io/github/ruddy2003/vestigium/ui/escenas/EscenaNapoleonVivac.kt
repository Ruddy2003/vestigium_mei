package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Moscu
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaNapoleon.H
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaNapoleon.pintar
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * **EL VIVAC — CUADRO PROVISIONAL (tanda S14, primer agente).** No es el cuadro del vivac: es el alto del camino de la
 * retirada sin nieve (salvo la del invierno), sin lo abandonado y con la tienda de un oficial en lugar de la carroza,
 * para que todos los días de 2026 a 2032 tengan cuadro mientras llega el de verdad. El vivac de verdad (`composicion.md`,
 * cuadro B: las filas de fusiles en pabellón de la compañía, los fuegos hasta el horizonte, el horizonte de cada tramo
 * de la campaña) lo hará otro agente después de que el autor vea la retirada.
 *
 * Aquí caen también, por ahora, el cuartel, Moscú, el hospital de Prusia y el pueblo de Borgoña
 * ([EscenaNapoleon.provisional]). De ellos solo se nota Moscú, con sus cúpulas en el horizonte (entera, en llamas o
 * quemada: [EscenaNapoleon.moscu]), y el fuego de Smolensk ([EscenaNapoleon.smolensk]).
 */
internal object VivacNapoleon {
    private val C = ColorNapoleon

    fun seMueve(m: Momento): Boolean = true

    fun DrawScope.cuadro(m: Momento) { with(RetiradaNapoleon) { pintar(VistaNapoleon(m, vivac = true)) } }

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val v = VistaNapoleon(m, vivac = true)
        with(RetiradaNapoleon) { vivo(v, reloj) }
        val moscu = EscenaNapoleon.moscu(m)
        val arde = if (moscu == Moscu.ARDE) EscenaNapoleon.incendio(m) else if (EscenaNapoleon.smolensk(m)) 0.8f else 0f
        if (arde > 0f) for (i in 0 until 6) {
            val q = Offset(250f + i * 22f, H - 3f)
            llama(q, 4f + 3f * (i % 3), reloj, i + 90)
            humo(v.l, q + Offset(0f, -4f), 70f, 12f, reloj, 0.4f, 0.7f, 0.3f, i + 90, 7, 0.7f * arde)
        } else if (moscu == Moscu.QUEMADA) for (i in 0 until 3) humo(v.l, Offset(262f + i * 40f, H - 6f), 40f, 6f, reloj, 0.3f, 0.6f, 0.5f, i + 95, 5, 0.4f)
    }

    /** Lo que el vivac provisional pone en el horizonte: Moscú (con sus cúpulas) o Smolensk ardiendo. */
    fun DrawScope.horizonte(v: VistaNapoleon) {
        val moscu = EscenaNapoleon.moscu(v.m); val smolensk = EscenaNapoleon.smolensk(v.m)
        if (moscu == null && !smolensk) return
        val p = v.p; val az = Azar(Semilla.de("napoleon", "ciudad"))
        val quemada = moscu == Moscu.QUEMADA; val arde = moscu == Moscu.ARDE || smolensk
        // El caserío: una banda de tejados y muros, y las torres y cúpulas encima.
        val muro = if (quemada) C.CENIZA else lerp(C.LONA, C.ROJO, 0.25f)
        var x = 244f
        while (x < ANCHO + 6f) {
            val w = az.entre(5f, 11f); val h = az.entre(3f, 8f)
            drawRect(p.lado(muro, 0.6f, az.real() < 0.5f), Offset(x, H - h), Size(w, h + 1f))
            if (!quemada) drawPath(poligono(Offset(x - 0.5f, H - h), Offset(x + w * 0.5f, H - h - 2.5f), Offset(x + w + 0.5f, H - h)), p.medio(lerp(C.ROJO, C.CENIZA, 0.3f), 0.6f))
            x += w * az.entre(0.7f, 1f)
        }
        if (moscu != null) for (i in 0 until 9) {
            val cx = az.entre(250f, 372f); val h = az.entre(12f, 26f); val r = az.entre(2.2f, 4f)
            drawRect(p.lado(lerp(C.LONA, C.ROJO, 0.15f), 0.55f, cx > 300f), Offset(cx - r * 0.7f, H - h), Size(r * 1.4f, h))
            val oro = if (i % 3 == 0) C.AZUL else if (i % 3 == 1) C.ORO else lerp(C.PINO_LUZ, C.ORO, 0.3f)
            val cupula = poligono(listOf(Offset(cx - r, H - h), Offset(cx - r * 1.05f, H - h - r * 0.9f), Offset(cx, H - h - r * 2.4f), Offset(cx + r * 1.05f, H - h - r * 0.9f), Offset(cx + r, H - h)))
            drawPath(cupula, if (quemada) p.medio(C.CENIZA, 0.5f) else p.lado(oro, 0.45f, cx < 310f))
            drawLine(p.medio(C.ORO, 0.5f), Offset(cx, H - h - r * 2.4f), Offset(cx, H - h - r * 3.4f), 0.5f)
        }
        // El incendio: el cielo rojo sobre la ciudad, «rojo como de día».
        if (arde) {
            val k = if (smolensk) 0.7f else EscenaNapoleon.incendio(v.m)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, C.LUMBRE.copy(alpha = (0.25f + 0.35f * v.noche) * k)), H - 70f, H + 6f), Offset(220f, H - 70f), Size(ANCHO - 220f, 76f), blendMode = BlendMode.Plus)
            resplandor(Offset(310f, H - 4f), 90f, (0.3f + 0.6f * v.noche) * k)
            repeat(8) { mancha(Offset(az.entre(240f, ANCHO), H - az.entre(10f, 50f)), az.entre(12f, 28f), az.entre(5f, 10f), lerp(v.l.tono(C.CENIZA), C.LUMBRE, 0.25f * v.noche), 0.5f * k) }
        }
    }

    // ---------------------------------------------------------------- El gato

    fun luzDelGato(m: Momento, en: Offset): LuzGato = RetiradaNapoleon.luzDelGato(m, en)

    fun estorbos(m: Momento): List<Float> = listOf(RetiradaCerca.FUEGO.x)

    /** Las anclas del gato, como en la retirada; el mirador, en lo alto de la mochila de detrás del fuego. */
    fun anclas(m: Momento): List<Ancla> = RetiradaNapoleon.anclas(m).map { if (it.tipo == TipoAncla.MIRADOR) Ancla(TipoAncla.MIRADOR, 352f, 214f) else it }
}
