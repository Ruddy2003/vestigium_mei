package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Senal
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.canto
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.cara
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.p
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.tapa
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAntiguas.runa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar

/**
 * La esquina de la posada del Cuerno, el marco de la derecha: «una casa larga de piedra y madera al pie del paso, con un
 * cuerno de carnero clavado sobre la puerta y un establo más grande que la casa». Se mira a lo largo de su fachada, de
 * pie en el poyo ([FachadaPosada]): la planta baja de piedra sin labrar en hiladas, el piso de tablas con la ventana del cuarto
 * alto («la que da al paso»), el alero de losas que asoma arriba a la derecha, la puerta con el cuerno encima y la
 * piedra de su jamba con los años grabados «uno debajo de otro, hasta los que ya no se leen», la campana en su brazo, el
 * poyo, la argolla de las mulas, el ventanuco y, al fondo, la puerta grande del establo y la esquina.
 */
internal object PosadaAntiguas {
    private val C = ColorAntiguas

    /** De dónde sale el humo de la chimenea: un cañón que asoma por encima del alero, retirado en el tejado. */
    val CHIMENEA = Offset(351f, 50f)

    /** La puerta: de [PUERTA_Z0] a [PUERTA_Z1] metros, 2.05 de alto. */
    const val PUERTA_Z0 = 9.6f
    const val PUERTA_Z1 = 10.6f
    private const val PISO = 2.35f

    /** El poyo de piedra, junto a la puerta: de dónde a dónde va, su alto y lo que sale del muro. */
    const val POYO_Z0 = 11.0f
    const val POYO_Z1 = 12.6f
    const val POYO_ALTO = 0.45f
    const val POYO_FONDO = 0.42f

    /** La argolla donde se atan las mulas. */
    val ARGOLLA: Offset get() = p(13.55f, 1.15f)

    /** La forma de todo el muro, para la sombra que le sube y los recortes. */
    val MURO: Path by lazy { poligono(p(8.6f, -0.2f), p(FachadaPosada.Z_ESQUINA, -0.2f), p(FachadaPosada.Z_ESQUINA, FachadaPosada.ALERO), p(8.6f, FachadaPosada.ALERO)) }

    fun DrawScope.posada(v: VistaAntiguas) {
        val az = SitioAntiguas.fijo("posada")
        chimenea(v)
        muro(v, az)
        establo(v)
        ventanuco(v)
        puerta(v, az)
        cuartoAlto(v)
        alero(v, az)
        cuerno(v)
        campana(v)
        poyo(v)
        argolla(v)
        pieDelMuro(v, az)
    }

    /** El pincel de un punto del muro: la cara mira a la izquierda (a la plaza); le da el sol de la tarde y no el de la mañana. */
    private fun pincel(v: VistaAntiguas, z: Float, h: Float): Pincel = v.pincelEn(p(z, h), lumbre = if (h < 2.6f && z < 12f) (v.encendido.sala * v.noche * 0.5f * (1f - (z - 9.6f) / 2.4f)).coerceIn(0f, 1f) else 0f)

    private val CARA = V3(-0.992f, 0f, 0.128f)

    /** La cara del muro: la piedra en hiladas de la planta baja (cada piedra con su luz), las tablas del piso y su sombra arriba. */
    private fun DrawScope.muro(v: VistaAntiguas, az: Azar) {
        val t = v.sol.t(CARA)
        // La planta baja: piedra gris sin labrar, a hiladas desiguales, con la argamasa vieja entre una y otra.
        val piedra = lerp(lerp(C.PIEDRA, C.LANA, 0.15f), C.TIERRA, 0.1f)
        drawPath(cara(8.6f, 13.6f, -0.2f, PISO), pincel(v, 11f, 1.2f).por(lerp(piedra, C.ESCORIA, 0.3f), t - 0.2f))
        var h = -0.05f
        while (h < PISO - 0.1f) {
            val alto = az.entre(0.2f, 0.34f); var z = 8.6f + az.entre(-0.2f, 0.2f)
            while (z < 13.6f) {
                val largo = az.entre(0.3f, 0.7f) * (if (az.real() < 0.15f) 1.6f else 1f)
                val pe = pincel(v, z, h)
                val tono = lerp(piedra, if (az.real() < 0.5f) C.TIERRA else C.ROCA, az.entre(0f, 0.4f))
                piedraDelMuro(pe, z + largo / 2f, h + alto / 2f, largo / 2f - 0.025f, alto / 2f - 0.025f, tono, t + az.entre(-0.2f, 0.2f), az)
                z += largo
            }
            h += alto
        }
        // Las manchas de humedad que bajan del alero y el zócalo, más oscuro.
        for (k in 0..5) { val z = 9f + k * 0.8f + az.entre(0f, 0.4f); drawPath(cara(z, z + 0.12f, 1.2f, PISO), pincel(v, z, 1.8f).sombra(C.TIERRA).copy(alpha = 0.18f)) }
        drawPath(cara(8.6f, 13.6f, -0.1f, 0.3f), pincel(v, 11f, 0.1f).sombra(lerp(C.TIERRA, C.MUSGO, 0.4f)).copy(alpha = 0.35f))
        // El piso: troncos y tablas tendidos, en fuga con el muro, con su lado de luz.
        val madera = lerp(C.MADERA, C.LANA, 0.08f)
        drawPath(cara(8.6f, 13.6f, PISO, FachadaPosada.ALERO), pincel(v, 11f, 3f).por(madera, t - 0.1f))
        var hh = PISO + 0.02f
        while (hh < FachadaPosada.ALERO) {
            val pe = pincel(v, 11f, hh)
            drawLine(pe.sombra(lerp(madera, C.ESCORIA, 0.4f)), p(8.6f, hh), p(13.6f, hh), 0.9f)
            drawLine(pe.luz(madera).copy(alpha = 0.35f * v.r.dia), p(8.6f, hh + 0.06f), p(13.6f, hh + 0.06f), 0.5f)
            hh += 0.17f
        }
        drawLine(pincel(v, 11f, PISO).por(C.MADERA_OSC, 0f), p(8.6f, PISO), p(13.6f, PISO), 1.6f)
    }

    /** Una piedra del muro, de canto redondeado, centrada en ([z], [h]) con sus medios largos en metros: su cara, su labio de luz arriba y su sombra abajo. */
    private fun DrawScope.piedraDelMuro(pe: Pincel, z: Float, h: Float, rz: Float, rh: Float, tono: Color, t: Float, az: Azar) {
        // Una piedra de cuatro cantos algo torcidos (a lo largo del muro se ve muy de canto): la cara, el labio de luz
        // de arriba y la junta de sombra de abajo.
        fun j() = az.entre(-0.18f, 0.18f)
        val a = p(z - rz * (1f + j()), h + rh * (1f + j())); val b = p(z + rz * (1f + j()), h + rh * (1f + j()))
        val c = p(z + rz * (1f + j()), h - rh * (1f + j())); val d = p(z - rz * (1f + j()), h - rh * (1f + j()))
        drawPath(poligono(a, b, c, d), pe.por(tono, t))
        drawLine(pe.luz(lerp(tono, C.NIEVE, 0.3f)).copy(alpha = 0.45f), a, b, 0.6f)
        drawLine(pe.sombra(tono).copy(alpha = 0.6f), d, c, 0.7f)
    }

    /** El establo, «más grande que la casa»: tablas sobre un zócalo de piedra y su puerta grande, entornada a la hora de cuidar las mulas. */
    private fun DrawScope.establo(v: VistaAntiguas) {
        val t = v.sol.t(CARA)
        val tabla = lerp(C.MADERA, C.LANA, 0.12f)
        drawPath(cara(13.6f, FachadaPosada.Z_ESQUINA, -0.2f, 0.7f), pincel(v, 14.5f, 0.4f).por(C.PIEDRA, t - 0.1f))
        drawPath(cara(13.6f, FachadaPosada.Z_ESQUINA, 0.7f, FachadaPosada.ALERO), pincel(v, 14.5f, 2f).por(tabla, t))
        var z = 13.6f
        while (z < FachadaPosada.Z_ESQUINA) { drawLine(pincel(v, z, 2f).sombra(tabla).copy(alpha = 0.6f), p(z, 0.7f), p(z, FachadaPosada.ALERO), 0.5f); z += 0.3f }
        // La puerta grande, de dos hojas; a la hora de cuidar, una entornada (el hueco oscuro).
        val abierta = v.m.hacer == io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer.CUIDAR
        drawPath(cara(14.0f, 15.4f, 0f, 2.4f), pincel(v, 14.6f, 1.2f).por(C.MADERA, t - 0.25f))
        if (abierta) drawPath(cara(14.0f, 14.6f, 0f, 2.3f), pincel(v, 14.3f, 1f).medio(C.ESCORIA))
        drawLine(pincel(v, 14.6f, 1.2f).por(C.ESCORIA, 0f), p(14.0f, 1.2f), p(15.4f, 1.2f), 0.6f)
        // La esquina: las piedras grandes, una larga y una corta.
        var h = 0f; var k = 0
        while (h < FachadaPosada.ALERO) { val alto = 0.42f; val z0 = FachadaPosada.Z_ESQUINA - (if (k % 2 == 0) 0.7f else 0.4f); drawPath(cara(z0, FachadaPosada.Z_ESQUINA, h, h + alto - 0.03f), pincel(v, 15.8f, h).por(lerp(C.PIEDRA, C.NIEVE, 0.12f), t + 0.1f)); h += alto; k++ }
    }

    /** El ventanuco de la sala: pequeño, con su reja; encendido de noche; con escarcha («helechos blancos») en invierno. */
    private fun DrawScope.ventanuco(v: VistaAntiguas) {
        val forma = cara(12.9f, 13.3f, 1.3f, 1.75f)
        val luz = v.encendido.sala * v.noche
        if (luz > 0.05f) {
            drawPath(forma, lerp(C.LUMBRE, Color(0xFFFFE2A8), 0.4f).copy(alpha = (0.4f + 0.6f * luz).coerceAtMost(1f)))
            resplandor(p(13.1f, 1.5f), 12f, 0.5f * luz, C.LUMBRE)
        } else drawPath(forma, pincel(v, 13.1f, 1.5f).medio(C.ESCORIA))
        if (v.invierno) drawPath(forma, C.NIEVE.copy(alpha = 0.35f))
        for (z in listOf(13.03f, 13.17f)) drawLine(pincel(v, z, 1.5f).medio(C.ESCORIA), p(z, 1.3f), p(z, 1.75f), 0.5f)
        drawPath(cara(12.85f, 13.35f, 1.24f, 1.3f, 0.06f), pincel(v, 13.1f, 1.3f).por(C.PIEDRA, 0.4f))
    }

    /**
     * La puerta: de canto, como se ve desde el poyo. La jamba de enfrente da la cara: es la piedra de los años, con una
     * marca por año, una debajo de otra, más gastadas las de abajo; la del año nuevo, recién tallada. De noche, si la
     * puerta está entornada, la luz de la sala la enciende de naranja.
     */
    private fun DrawScope.puerta(v: VistaAntiguas, az: Azar) {
        val pu = EscenaAntiguas.puerta(v.m)
        val luz = if (pu == Puerta.CERRADA) 0f else v.encendido.sala * maxOf(v.noche, 0.25f)
        // El hueco: la penumbra de la sala, o su luz.
        val hueco = cara(PUERTA_Z0, PUERTA_Z1, 0f, 2.05f)
        drawPath(hueco, if (luz > 0.05f) Brush.verticalGradient(listOf(lerp(C.MADERA_OSC, C.LUMBRE, luz * 0.8f), lerp(C.ESCORIA, C.LUMBRE, luz * 0.55f)), p(10f, 2f).y, p(10f, 0f).y) else Brush.verticalGradient(listOf(pincel(v, 10f, 1f).medio(C.ESCORIA), pincel(v, 10f, 1f).medio(C.MADERA_OSC)), 150f, 260f))
        // La jamba de enfrente: la piedra de los años (su cara mira a quien mira).
        val jamba = canto(PUERTA_Z1, 0f, -0.55f, 0f, 2.05f)
        val pj = pincel(v, PUERTA_Z1, 1f)
        val piedra = lerp(C.PIEDRA, C.NIEVE, 0.2f)
        // (Solo se ve dentro del hueco: el muro de este lado de la puerta la tapa.)
        clipPath(hueco) {
            drawPath(jamba, pj.por(piedra, v.sol.t(V3(0f, 0f, -1f)) + 0.1f))
            if (luz > 0.05f) drawPath(jamba, Brush.horizontalGradient(listOf(C.LUMBRE.copy(alpha = 0f), C.LUMBRE.copy(alpha = 0.55f * luz)), p(PUERTA_Z1, 1f).x, p(PUERTA_Z1, 1f, -0.4f).x), blendMode = BlendMode.Plus)
            clipPath(jamba) { anios(v, pj, piedra, az) }
            // La hoja de la puerta, abierta hacia dentro: solo su canto, si está cerrada.
            if (pu == Puerta.CERRADA) drawPath(canto(PUERTA_Z1 - 0.02f, -0.16f, -0.24f, 0f, 2.03f), pincel(v, PUERTA_Z1, 1f).por(C.MADERA, -0.2f))
            // El canto de la jamba de este lado, iluminado por dentro si hay luz.
            if (luz > 0.05f) drawLine(lerp(C.LUMBRE, Color(0xFFFFE0A0), 0.5f).copy(alpha = 0.8f * luz), p(PUERTA_Z0 + 0.03f, 0.05f), p(PUERTA_Z0 + 0.03f, 2f), 1.4f)
        }
        // El dintel: una piedra larga.
        drawPath(cara(PUERTA_Z0 - 0.25f, PUERTA_Z1 + 0.3f, 2.05f, 2.38f, 0.02f), pincel(v, 10f, 2.2f).por(lerp(C.PIEDRA, C.NIEVE, 0.15f), v.sol.t(CARA) + 0.15f))
        drawPath(tapa(PUERTA_Z0 - 0.25f, PUERTA_Z1 + 0.3f, 0f, 0.02f, 2.05f), pincel(v, 10f, 2f).sombra(C.PIEDRA))
        // El umbral, gastado.
        drawPath(tapa(PUERTA_Z0, PUERTA_Z1, 0f, 0.35f, 0.08f), pincel(v, 10f, 0f).por(lerp(C.PIEDRA, C.LANA, 0.25f), 0.4f))
        // Las ramas de pino de la Noche del Arpa, sobre el dintel.
        if (Senal.RAMAS_DE_PINO in v.senales) for (k in 0..6) { val q = p(PUERTA_Z0 + k * 0.17f, 2.3f, 0.05f); drawLine(pincel(v, 10f, 2.3f).por(C.PINO, 0.1f), q, q + Offset(az.entre(-5f, 5f), az.entre(4f, 9f)), 1.6f, StrokeCap.Round) }
    }

    /** Las marcas de los años en la jamba: una raya con su tilde por año, de abajo (las más viejas, casi borradas) arriba. */
    private fun DrawScope.anios(v: VistaAntiguas, pj: Pincel, piedra: Color, az: Azar) {
        val nueva = Senal.RUNA_NUEVA in v.senales
        val filas = 26
        for (k in 0 until filas) {
            val h = 0.25f + 1.7f * k / filas
            val gastada = (1f - k / (filas - 1f)) * 0.85f
            val a = p(PUERTA_Z1, h, -0.04f); val b = p(PUERTA_Z1, h, -0.04f - az.entre(0.08f, 0.16f))
            val ultima = k == filas - 1
            val fresca = if (ultima && nueva) 1f else 0f
            val surco = lerp(pj.sombra(lerp(piedra, C.ESCORIA, 0.4f)), pj.luz(lerp(piedra, C.FRESNO, 0.5f)), fresca).copy(alpha = (0.85f * (1f - gastada)).coerceIn(0.08f, 1f))
            drawLine(surco, a, b, 0.7f, StrokeCap.Round)
            val mitad = (a + b) / 2f
            drawLine(surco, mitad, mitad + Offset(az.entre(-0.6f, 0.6f), if (k % 3 == 0) -1.6f else 1.4f), 0.55f, StrokeCap.Round)
        }
        if (nueva) drawCircle(lerp(C.FRESNO, C.NIEVE, 0.4f).copy(alpha = 0.5f), 2.4f, p(PUERTA_Z1, 1.95f, -0.12f))
    }

    /** La ventana del cuarto alto, «la de la ventana al paso»: con sus postigos; encendida en la crianza y cuando viven arriba; con los pañales tendidos en la crianza de día. */
    private fun DrawScope.cuartoAlto(v: VistaAntiguas) {
        val forma = cara(10.95f, 11.55f, 2.6f, 3.2f)
        val luz = v.encendido.cuartoAlto * maxOf(v.noche, 0.2f)
        if (luz > 0.05f) {
            drawPath(forma, lerp(C.LUMBRE, Color(0xFFFFE6B0), 0.45f).copy(alpha = (0.45f + 0.55f * luz).coerceAtMost(1f)))
            resplandor(p(11.25f, 2.9f), 16f, 0.5f * luz, C.LUMBRE)
        } else drawPath(forma, pincel(v, 10.1f, 3.1f).medio(C.ESCORIA))
        // Los postigos abiertos contra el muro y el alféizar.
        val pe = pincel(v, 11.25f, 2.9f)
        drawPath(cara(10.7f, 10.95f, 2.6f, 3.2f, 0.03f), pe.por(lerp(C.PINO, C.MADERA, 0.5f), v.sol.t(CARA)))
        drawPath(cara(11.55f, 11.8f, 2.6f, 3.2f, 0.03f), pe.por(lerp(C.PINO, C.MADERA, 0.5f), v.sol.t(CARA) - 0.1f))
        drawPath(tapa(10.9f, 11.6f, 0f, 0.12f, 2.58f), pe.por(C.MADERA, 0.4f))
        if (EscenaAntiguas.cuna(v.m) && v.noche < 0.5f) for (k in 0..2) { val q = p(10.98f + k * 0.2f, 2.5f, 0.08f); drawPath(poligono(q, q + Offset(-3f, 0f), q + Offset(-2.6f, 6f), q + Offset(0.2f, 5f)), pe.por(C.LANA, 0.5f)) }
    }

    /** El alero de losas: asoma por la esquina de arriba a la derecha; las cabezas de las vigas y el canto de las losas; con nieve, su ceja y sus carámbanos. */
    private fun DrawScope.alero(v: VistaAntiguas, az: Azar) {
        val pe = pincel(v, 11f, 3.6f)
        val al = FachadaPosada.ALERO; val z1 = FachadaPosada.Z_ESQUINA + 0.2f
        // El vuelo del alero, visto desde abajo: oscuro, con las cabezas de las vigas.
        drawPath(tapa(8.4f, z1, 0f, 0.16f, al), pe.sombra(C.MADERA_OSC))
        var z = 8.7f
        while (z < FachadaPosada.Z_ESQUINA) { drawLine(pincel(v, z, al).por(C.MADERA, -0.1f).copy(alpha = 0.7f), p(z, al - 0.02f, 0.02f), p(z, al - 0.02f, 0.15f), FachadaPosada.escala(z) * 0.035f); z += 0.6f }
        // El canto de las losas: dos hiladas, con sus juntas y el musgo.
        drawPath(cara(8.4f, z1, al, al + 0.07f, 0.17f), pe.por(lerp(C.LOSA, C.ESCORIA, 0.2f), 0.1f))
        drawPath(cara(8.4f, z1, al + 0.07f, al + 0.13f, 0.12f), pe.por(lerp(C.LOSA, C.MUSGO, 0.25f), 0.35f))
        z = 8.5f
        while (z < z1) { drawLine(pe.sombra(C.ESCORIA).copy(alpha = 0.7f), p(z, al, 0.17f), p(z, al + 0.07f, 0.17f), 0.5f); z += az.entre(0.25f, 0.45f) }
        drawLine(pe.luz(lerp(C.LOSA, C.NIEVE, 0.4f)).copy(alpha = 0.5f), p(8.4f, al + 0.13f, 0.12f), p(z1, al + 0.13f, 0.12f), 0.7f)
        if (v.nieveAbajo > 0.15f) {
            drawPath(cara(8.4f, z1, al + 0.12f, al + 0.12f + 0.26f * v.nieveAbajo, 0.14f), pe.por(C.NIEVE, 0.4f).copy(alpha = v.nieveAbajo))
            if (v.invierno) { var zz = 8.8f; while (zz < 15.6f) { val q = p(zz, al, 0.17f); drawLine(pe.luz(lerp(C.NIEVE, C.AGUA, 0.25f)).copy(alpha = 0.7f), q, q + Offset(0f, az.entre(2f, 7f) * FachadaPosada.escala(zz) / 50f), 0.6f, StrokeCap.Round); zz += az.entre(0.3f, 0.8f) } }
        }
    }

    /** El cañón de la chimenea que asoma por encima del alero, retirado en el tejado **[Aproximación]**: así se ve su humo. */
    private fun DrawScope.chimenea(v: VistaAntiguas) {
        val pe = v.pincelEn(CHIMENEA)
        val q = CHIMENEA
        drawPath(poligono(q + Offset(-5f, 2f), q + Offset(3.6f, 2f), q + Offset(3.6f, 22f), q + Offset(-5f, 22f)), pe.por(C.PIEDRA, v.sol.t(V3(-0.9f, 0f, -0.4f))))
        drawPath(poligono(q + Offset(3.6f, 2f), q + Offset(6.4f, 1f), q + Offset(6.4f, 22f), q + Offset(3.6f, 22f)), pe.por(C.PIEDRA, v.sol.t(V3(0.9f, 0f, -0.4f))))
        drawPath(poligono(q + Offset(-6.4f, 2.4f), q + Offset(7.6f, 1.2f), q + Offset(7f, -0.8f), q + Offset(-6f, 0.2f)), pe.por(C.LOSA, 0.4f))
        if (v.nieveAbajo > 0.3f) drawPath(poligono(q + Offset(-6.2f, 0.2f), q + Offset(7.2f, -0.8f), q + Offset(6.4f, -2.4f), q + Offset(-5.6f, -1.2f)), pe.por(C.NIEVE, 0.5f).copy(alpha = v.nieveAbajo))
    }

    /**
     * El cuerno de carnero clavado sobre la puerta, en su tabla: grueso en la base, con sus anillos, que da la vuelta hacia
     * fuera. Los días de luto, con un lazo negro **[Diseño propio]**.
     */
    private fun DrawScope.cuerno(v: VistaAntiguas) {
        val pe = pincel(v, 10.1f, 2.6f)
        drawPath(cara(9.85f, 10.4f, 2.42f, 2.86f, 0.03f), pe.por(C.MADERA, v.sol.t(CARA)))
        val base = p(10.12f, 2.64f, 0.06f); val s = FachadaPosada.escala(10.1f) / 56f
        val cuerno = Path().apply {
            moveTo(base + Offset(2f * s, -5f * s))
            cubicTo(base + Offset(-6f * s, -11f * s), base + Offset(-16f * s, -6f * s), base + Offset(-15f * s, 3f * s))
            cubicTo(base + Offset(-14f * s, 10f * s), base + Offset(-6f * s, 11f * s), base + Offset(-4f * s, 5f * s))
            cubicTo(base + Offset(-3f * s, 2f * s), base + Offset(-7f * s, 1f * s), base + Offset(-8f * s, 4f * s))
            cubicTo(base + Offset(-10f * s, 0f), base + Offset(-6f * s, -4f * s), base + Offset(-1f * s, -1f * s))
            cubicTo(base + Offset(1f * s, 0f), base + Offset(2f * s, 2f * s), base + Offset(2.6f * s, 3f * s))
            close()
        }
        val hueso = lerp(C.LANA, C.ORO, 0.2f)
        drawPath(cuerno, Brush.linearGradient(listOf(pe.luz(hueso), pe.sombra(lerp(hueso, C.CUERO, 0.4f))), base + Offset(0f, -8f * s), base + Offset(-12f * s, 8f * s)))
        clipPath(cuerno) { for (k in 0..7) drawCircle(pe.sombra(lerp(hueso, C.CUERO, 0.5f)).copy(alpha = 0.5f), 4f * s + k * 1.4f * s, base + Offset(-8f * s, 1f * s), style = Stroke(0.4f * s)) }
        if (Senal.LAZO_NEGRO in v.senales) { drawLine(pe.medio(C.ESCORIA), base + Offset(-2f * s, -4f * s), base + Offset(-3f * s, 10f * s), 1.4f * s); drawLine(pe.medio(C.ESCORIA), base + Offset(-2f * s, -4f * s), base + Offset(1f * s, 9f * s), 1.2f * s) }
    }

    /** La campana de la posada, en su brazo de hierro: la que avisa de los cuernos del paso. */
    private fun DrawScope.campana(v: VistaAntiguas) {
        val pe = pincel(v, 12.3f, 2.2f)
        val a = p(12.3f, 2.25f); val b = p(12.3f, 2.25f, 0.55f)
        drawLine(pe.medio(C.ESCORIA), a, b, 0.9f, StrokeCap.Round)
        drawLine(pe.medio(C.ESCORIA), p(12.3f, 1.95f), (a + b) / 2f, 0.6f)
        val s = FachadaPosada.escala(12.3f) / 50f; val c = b + Offset(0f, 2.2f * s)
        val bronce = lerp(C.ORO, C.CUERO, 0.3f)
        drawPath(poligono(c + Offset(-1.6f * s, 0f), c + Offset(1.6f * s, 0f), c + Offset(2.6f * s, 4.4f * s), c + Offset(-2.6f * s, 4.4f * s)), Brush.horizontalGradient(listOf(pe.luz(bronce), pe.sombra(bronce)), c.x - 2.6f * s, c.x + 2.6f * s))
        drawLine(pe.medio(C.ESCORIA), b, c, 0.5f)
    }

    /** El poyo de piedra: la losa de encima, su canto y su frente, gastados. Encima, el objeto chico del día. */
    private fun DrawScope.poyo(v: VistaAntiguas) {
        val pe = pincel(v, 11.8f, 0.3f)
        val piedra = lerp(C.PIEDRA, C.LANA, 0.15f)
        drawPath(cara(POYO_Z0, POYO_Z1, 0f, POYO_ALTO, POYO_FONDO), pe.por(piedra, v.sol.t(CARA) - 0.05f))
        drawPath(canto(POYO_Z0, 0f, POYO_FONDO, 0f, POYO_ALTO), pe.por(piedra, v.sol.t(V3(0f, 0f, -1f))))
        drawPath(tapa(POYO_Z0, POYO_Z1, 0f, POYO_FONDO + 0.04f, POYO_ALTO), pe.por(lerp(piedra, C.NIEVE, 0.2f), 0.45f))
        if (v.nieveAbajo > 0.3f) drawPath(tapa(POYO_Z0 + 0.1f, POYO_Z1, 0.02f, POYO_FONDO, POYO_ALTO + 0.06f), pe.por(C.NIEVE, 0.5f).copy(alpha = v.nieveAbajo))
    }

    /** Dónde se pone el objeto chico del día: sobre la losa del poyo, hacia el canto (a la vista). */
    val SOBRE_EL_POYO: Offset get() = p(11.6f, POYO_ALTO, POYO_FONDO * 0.55f)

    /** La argolla de hierro, con el ronzal de las mulas cuando están. */
    private fun DrawScope.argolla(v: VistaAntiguas) {
        val pe = pincel(v, 13.55f, 1.15f)
        drawCircle(pe.medio(C.ESCORIA), 1.6f, ARGOLLA + Offset(-0.6f, 1.2f), style = Stroke(0.6f))
        drawCircle(pe.luz(C.PIEDRA_OSC).copy(alpha = 0.6f), 0.6f, ARGOLLA)
    }

    /** El pie del muro: la franja de paso entre la piedra y el suelo (hierba, ortigas, piedras caídas); con nieve, el ventisquero arrimado. */
    private fun DrawScope.pieDelMuro(v: VistaAntiguas, az: Azar) {
        var z = 8.8f
        val hierba = EscenaAntiguas.hierba(v.m)
        while (z < FachadaPosada.Z_ESQUINA) {
            if (z !in PUERTA_Z0..PUERTA_Z1 && z !in POYO_Z0..POYO_Z1) {
                val q = p(z, 0f, 0.05f); val pe = v.pincelEn(q); val k = FachadaPosada.escala(z) / 60f
                for (j in 0..2) drawLine(pe.por(lerp(hierba, C.PINO, 0.3f), 0.1f), q, q + Offset(az.entre(-2f, 2f) * k, -az.entre(2f, 5f) * k), 0.5f * k, StrokeCap.Round)
            }
            z += az.entre(0.2f, 0.5f)
        }
        if (v.nieveAbajo > 0.2f) {
            val borde = (0..14).map { i -> val zz = 8.6f + i * 0.53f; p(zz, 0.25f + 0.35f * v.nieveAbajo * (0.7f + 0.3f * kotlin.math.sin(i * 1.7f)), 0.02f) }
            val manto = poligono(borde + listOf(p(FachadaPosada.Z_ESQUINA, -0.1f, 0.6f), p(8.6f, -0.1f, 0.8f)))
            val pe = v.pincelEn(p(12f, 0f))
            drawPath(manto, pe.por(C.NIEVE, 0.35f).copy(alpha = v.nieveAbajo))
        }
    }
}
