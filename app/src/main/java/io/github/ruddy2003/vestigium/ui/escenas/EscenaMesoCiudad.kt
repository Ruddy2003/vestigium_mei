package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * La ciudad de K'an: la plaza estucada, el templo mayor, el palacio, el juego de pelota, las estelas de los señores de
 * antes, la estela nueva y la aguada. **[Aproximación]**: los edificios mayas iban estucados y pintados, de rojo sobre
 * todo (se comprobó en la sesión: ver el informe); qué partes iban en rojo y cuáles en crema es elección de este cuadro.
 */
internal object CiudadMeso {
    private val C = ColorMeso
    private const val H = EscenaMeso.H

    /** La plaza: de su borde lejano, al pie del palacio y del templo, hasta el pie de la plataforma de la casa. */
    private val PLAZA = listOf(Offset(52f, 159f), Offset(304f, 157f), Offset(342f, 172f), Offset(384f, 193f), Offset(384f, 216f), Offset(-4f, 216f), Offset(-4f, 199f), Offset(22f, 178f))
    const val BORDE = 157f

    /** El templo mayor: dónde pisa, cuántos cuerpos tiene y cuánto mide cada uno. */
    const val TX = 262f
    const val TY = 161f
    private const val CUERPOS = 9
    private const val ALTO_CUERPO = 8.2f
    private const val MEDIO_ABAJO = 58f
    private const val MEDIO_ARRIBA = 21f
    val CIMA = TY - CUERPOS * ALTO_CUERPO                                       // 87.2: la terraza de arriba

    /** La estela nueva: dónde se levanta y cuánto mide. */
    val ESTELA = Offset(152f, 204f)
    const val ALTO_ESTELA = 56f
    const val ANCHO_ESTELA = 16.4f

    /** La aguada: el depósito de la plaza. */
    val AGUADA = Rect(258f, 189.5f, 344f, 206f)

    /** Las estelas de los señores de antes, en fila. */
    private val ANTIGUAS = listOf(Offset(192f, 181f), Offset(206f, 180.6f), Offset(220f, 180.2f), Offset(233f, 179.8f))

    /** Los dos braseros de piedra del pie de la escalinata, y el adoratorio bajo que hay delante. */
    val BRASEROS = listOf(Offset(TX - 17f, TY + 4f), Offset(TX + 17f, TY + 4f))
    val ADORATORIO = Offset(TX, 179f)

    private fun plazaForma(): Path = poligono(PLAZA)

    /** Si el punto ([x], [y]) queda tapado del todo por el palacio o por el templo mayor (con margen): la selva de detrás no hace falta pintarla ahí. */
    fun tapa(x: Float, y: Float): Boolean = (x > 86f && x < 172f && y > 140f) || (y > CIMA + 8f && kotlin.math.abs(x - TX) < medio(y) - 14f)

    /** El medio ancho de la pirámide a la altura [y]. */
    private fun medio(y: Float): Float = MEDIO_ABAJO + (MEDIO_ARRIBA - MEDIO_ABAJO) * ((TY - y) / (CUERPOS * ALTO_CUERPO)).coerceIn(0f, 1f)

    /** La silueta del templo mayor, para su sombra. */
    private fun siluetaDelTemplo(): Path = poligono(listOf(Offset(TX - MEDIO_ABAJO, TY), Offset(TX - MEDIO_ARRIBA, CIMA), Offset(TX - 15f, CIMA), Offset(TX - 15f, CIMA - 14f), Offset(TX - 11f, CIMA - 19f), Offset(TX - 8f, CIMA - 36f), Offset(TX + 8f, CIMA - 36f), Offset(TX + 11f, CIMA - 19f), Offset(TX + 15f, CIMA - 14f), Offset(TX + 15f, CIMA), Offset(TX + MEDIO_ARRIBA, CIMA), Offset(TX + MEDIO_ABAJO, TY)))

    private fun siluetaDeLaEstela(m: Momento): Path = Path().apply {
        val e = ESTELA; val w = ANCHO_ESTELA / 2f
        when (EscenaMeso.estela(m)) {
            EscenaMeso.Estela.SIN -> Unit
            EscenaMeso.Estela.PIEDRA -> addRect(Rect(e.x - 27f, e.y - 14f, e.x + 27f, e.y))
            EscenaMeso.Estela.EN_OBRA -> { addRect(Rect(e.x - w, e.y - ALTO_ESTELA, e.x + w, e.y)); for (x in listOf(-13f, 13f)) addRect(Rect(e.x + x - 0.8f, e.y - ALTO_ESTELA - 7f, e.x + x + 0.8f, e.y)); for (y in listOf(15f, 30f, 45f, 59f)) addRect(Rect(e.x - 14f, e.y - y - 0.6f, e.x + 14f, e.y - y + 0.6f)) }
            EscenaMeso.Estela.LEVANTADA -> addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(e.x - w, e.y - ALTO_ESTELA, e.x + w, e.y), CornerRadius(4f)))
        }
    }

    // ---------------------------------------------------------------- La parte quieta

    fun DrawScope.ciudad(m: Momento, l: Luces, r: Rayo) {
        val fijo = Azar(Semilla.de("meso", "ciudad"))
        plaza(m, l, r, fijo)
        palacio(m, l, r, fijo)
        juegoDePelota(m, l, r)
        // Las sombras sobre la plaza: el templo, el palacio, las estelas y el andamio.
        capaSombra(r) { c ->
            clipPath(plazaForma()) {
                sombraArrojada(r, siluetaDelTemplo(), Offset(TX, TY), c, BORDE, BORDE)
                sombraArrojada(r, Path().apply { addRect(Rect(66f, 123f, 190f, 162f)) }, Offset(128f, 162f), c, BORDE, BORDE)
                for (p in ANTIGUAS) sombraArrojada(r, Path().apply { addRect(Rect(p.x - 2.3f, p.y - 13f, p.x + 2.3f, p.y)) }, p, c, BORDE, BORDE)
                for (b in BRASEROS) sombraArrojada(r, Path().apply { addRect(Rect(b.x - 2.2f, b.y - 9f, b.x + 2.2f, b.y)) }, b, c, BORDE, BORDE)
                if (EscenaMeso.estela(m) == EscenaMeso.Estela.EN_OBRA) sombraArrojada(r, Path().apply { addRect(Rect(ESTELA.x + 21f, ESTELA.y - 22f, ESTELA.x + 52f, ESTELA.y - 15f)) }, ESTELA + Offset(36f, -1f), c, BORDE, BORDE)
                sombraArrojada(r, Path().apply { addRect(Rect(52f, 172f, 86f, 184f)); addRect(Rect(99f, 172f, 133f, 184f)) }, Offset(92f, 184f), c, BORDE, BORDE)
                sombraArrojada(r, Path().apply { addRect(Rect(ADORATORIO.x - 17f, ADORATORIO.y - 6.6f, ADORATORIO.x + 17f, ADORATORIO.y)) }, ADORATORIO, c, BORDE, BORDE)
                sombraArrojada(r, siluetaDeLaEstela(m), ESTELA, c, BORDE, BORDE)
            }
        }
        temploMayor(m, l, r, fijo)
        aguada(m, l, r, fijo)
        for (p in ANTIGUAS) estelaAntigua(l, r, p, fijo)
        cosasDeLaPlaza(m, l, r)
        if (EscenaMeso.comerciantes(m)) mercaderes(l, r)
        estelaNueva(m, l, r, fijo)
        if (EscenaMeso.fiesta(m) && !EscenaMeso.wayeb(m)) estandartes(m, l, r)
        if (EscenaMeso.guerra(m)) for (x in listOf(84f, 128f, 172f)) escudo(l, Offset(x, 151f))
        lucesDeLaNoche(m, l)
        if (EscenaMeso.lucero(m)) { val p = Offset(96f, H - 22f); drawCircle(Color.White.copy(alpha = 0.95f), 1.5f, p); drawCircle(Color(0xFFFFF3C8).copy(alpha = 0.3f), 4.4f, p); for (d in listOf(Offset(4f, 0f), Offset(0f, 4f))) drawLine(Color.White.copy(alpha = 0.5f), p - d, p + d, 0.4f) }
    }

    /** La plaza estucada: blanca y grande, con sus losas, sus manchas de humedad y, con lluvia, sus charcos. */
    private fun DrawScope.plaza(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val forma = plazaForma(); val mojado = EscenaMeso.mojado(m)
        val propio = if (mojado) lerp(C.ESTUCO, C.PIEDRA_OSC, 0.35f) else if (EscenaMeso.lluvias(m)) lerp(C.ESTUCO, C.PIEDRA, 0.2f) else C.ESTUCO
        drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(propio, C.PIEDRA, 0.2f), 0.34f), l.tono(propio, 0.16f), l.tono(lerp(propio, Color(0xFFF6EBD0), 0.4f), 0.04f)), BORDE, 216f))
        clipPath(forma) {
            // Las juntas de las losas: unas huyen hacia el fondo y otras cruzan, más juntas cuanto más lejos.
            val junta = l.tono(lerp(C.PIEDRA, C.PIEDRA_OSC, 0.6f), 0.14f).copy(alpha = 0.42f)
            for (i in -9..9) drawLine(junta, Offset(190f + i * 9f, BORDE), Offset(190f + i * 46f, 218f), 0.4f)
            var y = BORDE + 2.4f; var paso = 2.6f
            while (y < 216f) { drawLine(junta.copy(alpha = 0.34f), Offset(0f, y), Offset(ANCHO, y), 0.4f); y += paso; paso *= 1.28f }
            // Humedad, líquenes y polvo rojo cerca de los edificios pintados.
            repeat(34) { val p = Offset(az.entre(0f, ANCHO), az.entre(BORDE, 214f)); val k = (p.y - BORDE) / 58f; mancha(p, az.entre(5f, 18f) * (0.5f + k), az.entre(1f, 2.8f) * (0.5f + k), l.tono(when (it % 4) { 0 -> lerp(C.PIEDRA_OSC, C.SELVA, 0.3f); 1 -> Color(0xFFF8F0DC); 2 -> lerp(C.PIEDRA, C.TIERRA, 0.3f); else -> C.PIEDRA }, 0.2f), az.entre(0.18f, 0.36f)) }
            // El camino blanco que entra en la plaza por la izquierda, algo más alto que ella y con su bordillo.
            drawPath(poligono(Offset(-4f, 188f), Offset(48f, 186.6f), Offset(56f, 192f), Offset(-4f, 196f)), l.tono(Color(0xFFF8F2E2), 0.1f)); drawLine(mult(l.tono(C.ESTUCO, 0.1f), r.color, 0.7f).copy(alpha = 0.5f), Offset(-4f, 196f), Offset(56f, 192f), 0.8f)
            repeat(6) { mancha(Offset(az.entre(206f, 318f), az.entre(162f, 170f)), az.entre(6f, 12f), 1.4f, l.tono(C.ROJO, 0.24f), 0.2f) }
            // Losas rotas y hierba en las grietas.
            val hierba = ArrayList<Offset>(60)
            repeat(30) { val p = Offset(az.entre(4f, 376f), az.entre(172f, 214f)); hierba.add(p); hierba.add(p + Offset(az.entre(-1f, 1f), -az.entre(1f, 2.6f))) }
            drawPoints(hierba, PointMode.Lines, l.tono(lerp(C.SELVA, C.HOJA_LUZ, 0.4f), 0.1f), 0.6f, StrokeCap.Round)
            // El resol del mediodía sobre el estuco, o el cielo en la plaza mojada («la plaza se vuelve un espejo»).
            if (mojado) {
                drawRect(Brush.verticalGradient(listOf(l.medio.copy(alpha = 0.34f), l.alto.copy(alpha = 0.12f)), BORDE, 216f), Offset(0f, BORDE), Size(ANCHO, 60f))
                reflejoRoto(TX - 46f, TX + 46f, TY + 1f, 34f, l.tono(C.ROJO, 0.2f), 0.5f, az, 1.3f)
                reflejoRoto(TX - 9f, TX + 9f, TY + 1f, 40f, l.tono(C.ESTUCO, 0.2f), 0.5f, az, 1.3f)
                reflejoRoto(70f, 188f, 163f, 16f, l.tono(C.ESTUCO, 0.2f), 0.4f, az, 1.3f)
                reflejoRoto(ESTELA.x - 8f, ESTELA.x + 8f, ESTELA.y + 1f, 10f, l.tono(C.PIEDRA_OSC, 0.1f), 0.4f, az, 1.2f)
                repeat(7) { val p = Offset(az.entre(20f, 250f), az.entre(176f, 212f)); drawOval(l.medio.copy(alpha = 0.5f), Offset(p.x - 9f, p.y - 1.2f), Size(az.entre(12f, 26f), 2.6f)) }
            } else if (r.dia > 0.5f) drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.1f * r.fuerza)), 176f, 216f), Offset(0f, 176f), Size(ANCHO, 40f))
        }
    }

    /** Un cuerpo de edificio estucado: la cara de frente con su zócalo rojo, su moldura con sombra y sus chorreras. */
    private fun DrawScope.cuerpo(l: Luces, r: Rayo, z: Rect, propio: Color, lejos: Float, az: Azar, banda: Color? = null) {
        val lado = if (r.lat >= 0f) 1f else -1f
        drawRect(Brush.horizontalGradient(listOf(l.cara(propio, -lado * 0.35f, lejos), l.cara(propio, lado * 0.55f, lejos)), z.left, z.right), z.topLeft, z.size)
        if (banda != null) drawRect(l.tono(banda, lejos), Offset(z.left, z.bottom - z.height * 0.3f), Size(z.width, z.height * 0.3f))
        // Chorreras de lluvia y manchas de humedad.
        repeat((z.width / 9f).toInt()) { val x = az.entre(z.left, z.right); drawLine(l.tono(lerp(C.PIEDRA_OSC, C.SELVA_OSC, 0.3f), lejos).copy(alpha = az.entre(0.12f, 0.3f)), Offset(x, z.top), Offset(x + az.entre(-0.6f, 0.6f), z.top + z.height * az.entre(0.3f, 0.9f)), az.entre(0.5f, 1.4f)) }
        // La moldura de arriba y la sombra que deja.
        drawRect(l.cara(lerp(propio, C.ESTUCO, 0.5f), 0.7f, lejos), Offset(z.left - 0.8f, z.top - 1.2f), Size(z.width + 1.6f, 1.6f))
        drawRect(mult(l.tono(propio, lejos), r.color, 0.7f).copy(alpha = 0.3f + 0.25f * r.dia), Offset(z.left, z.top + 0.4f), Size(z.width, 1.1f))
    }

    /** El palacio del señor: una galería larga de vanos sobre su plataforma, un segundo cuerpo más atrás y su crestería baja. */
    private fun DrawScope.palacio(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val lado = if (r.lat >= 0f) 1f else -1f; val lejos = 0.26f
        val vano = l.tono(lerp(C.TINTA, Color(0xFF3A3050), 0.5f), lejos * 0.8f)
        val conLuz = l.noche > 0.35f && !EscenaMeso.wayeb(m) && (m.hora >= 18.0 || m.hora < 0.5)
        // El segundo cuerpo, más atrás, con su crestería.
        drawRect(l.cara(C.ESTUCO, 0.3f, lejos + 0.06f), Offset(124f, 113.6f), Size(13f, 9f)); for (x in listOf(126.4f, 129.8f, 133.2f)) drawRect(vano.copy(alpha = 0.7f), Offset(x, 115.4f), Size(1.5f, 5f))
        cuerpo(l, r, Rect(98f, 122.6f, 162f, 135f), C.ESTUCO, lejos + 0.04f, az, C.ROJO)
        for (i in 0..3) { val x = 106f + i * 15.4f; drawRect(vano, Offset(x, 126f), Size(4.2f, 9f)); if (conLuz && i % 2 == 1) drawRect(C.LUMBRE.copy(alpha = 0.6f), Offset(x, 126f), Size(4.2f, 9f)) }
        // El flanco que se ve, a la derecha, y la galería de abajo.
        drawPath(poligono(Offset(184f, 136.6f), Offset(191f, 134.4f), Offset(191f, 150f), Offset(184f, 152f)), l.cara(C.ESTUCO, lado * 0.9f, lejos))
        cuerpo(l, r, Rect(72f, 136.6f, 184f, 152f), C.ESTUCO, lejos, az, C.ROJO)
        for (i in 0..7) {
            val x = 79f + i * 13.4f
            drawRect(vano, Offset(x, 140.4f), Size(4.8f, 11.6f)); drawRect(mult(l.tono(C.ESTUCO, lejos), r.color, 0.6f).copy(alpha = 0.4f * r.dia), Offset(x + (if (lado > 0f) 0f else 3.6f), 140.4f), Size(1.2f, 11.6f))
            if (conLuz && (i == 2 || i == 5 || i == 6)) drawRect(C.LUMBRE.copy(alpha = 0.72f), Offset(x, 140.4f), Size(4.8f, 11.6f))
            else if (i % 3 == 0) drawRect(l.tono(lerp(C.ROJO, C.MAIZ, 0.4f), lejos), Offset(x, 140.4f), Size(4.8f, 4.2f))            // una cortina a medio recoger
        }
        // El friso de la galería: una greca de estuco en relieve, pintada, y desconchones que enseñan la piedra.
        val greca = ArrayList<Offset>(80)
        for (i in 0..17) { val x = 75f + i * 6.1f; greca.add(Offset(x, 139f)); greca.add(Offset(x + 2.6f, 139f)); greca.add(Offset(x + 2.6f, 139f)); greca.add(Offset(x + 2.6f, 137.6f)); greca.add(Offset(x + 2.6f, 137.6f)); greca.add(Offset(x + 4.6f, 137.6f)) }
        drawPoints(greca, PointMode.Lines, l.tono(C.ROJO, lejos).copy(alpha = 0.8f), 0.6f)
        for ((x, y, w) in listOf(Triple(92f, 146f, 5f), Triple(151f, 143.6f, 4f), Triple(176f, 148f, 4.4f), Triple(118f, 130f, 3.6f), Triple(152f, 128f, 3f))) { mancha(Offset(x, y), w, w * 0.6f, l.tono(C.PIEDRA_OSC, lejos), 0.5f); mancha(Offset(x + 0.6f, y + 0.4f), w * 0.6f, w * 0.34f, l.tono(lerp(C.PIEDRA, C.TIERRA, 0.3f), lejos), 0.6f) }
        // La plataforma, con su escalinata.
        cuerpo(l, r, Rect(66f, 152f, 190f, 162f), lerp(C.ESTUCO, C.PIEDRA, 0.25f), lejos - 0.03f, az)
        drawPath(poligono(Offset(116f, 152f), Offset(142f, 152f), Offset(145f, 162f), Offset(113f, 162f)), l.cara(lerp(C.ESTUCO, Color.White, 0.2f), 0.5f, lejos - 0.03f))
        for (i in 1..5) drawLine(l.tono(C.PIEDRA_OSC, lejos).copy(alpha = 0.45f), Offset(115.6f - i * 0.5f, 152f + i * 1.66f), Offset(142.4f + i * 0.5f, 152f + i * 1.66f), 0.4f)
        drawPath(poligono(Offset(190f, 152f), Offset(196f, 150.4f), Offset(196f, 160.6f), Offset(190f, 162f)), l.cara(lerp(C.ESTUCO, C.PIEDRA, 0.25f), lado * 0.9f, lejos))
    }

    /**
     * El juego de pelota: dos edificios largos y paralelos, con el callejón en medio. Se mira por la cabecera: se ve
     * el corte de cada uno (su cabecera, con el zócalo rojo), su banqueta y el talud rojo que baja al callejón; entre
     * los dos, el callejón, que huye. **[Aproximación]**: sin aros, que en las tierras bajas del
     * Clásico no eran lo común; lleva tres marcadores redondos en el suelo del callejón.
     */
    private fun DrawScope.juegoDePelota(m: Momento, l: Luces, r: Rayo) {
        val lado = if (r.lat >= 0f) 1f else -1f; val lejos = 0.16f
        val estuco = lerp(C.ESTUCO, C.PIEDRA, 0.15f)
        // Se mira por la cabecera, callejón adentro: los dos edificios huyen hacia su punto de fuga, en el horizonte.
        val fuga = Offset(99f, EscenaMeso.H)
        fun at(p: Offset) = p + (fuga - p) * 0.23f                                     // el mismo punto, al fondo del edificio
        val suelo = 184f; val alto = 172f
        val a0 = Offset(52f, alto); val a1 = Offset(73f, alto); val a2 = Offset(86f, suelo); val a3 = Offset(52f, suelo)
        val b0 = Offset(99f, suelo); val b1 = Offset(112f, alto); val b2 = Offset(133f, alto); val b3 = Offset(133f, suelo)
        // El suelo del callejón, en sombra, con sus tres marcadores redondos.
        drawPath(poligono(a2, b0, at(b0), at(a2)), mult(l.tono(estuco, lejos), r.color, 0.5f))
        for (t in listOf(0.18f, 0.5f, 0.84f)) { val c = Offset(92.5f, suelo); val q = c + (at(c) - c) * t; drawOval(l.tono(C.PIEDRA_OSC, lejos), Offset(q.x - 2f * (1f - t * 0.3f), q.y - 0.6f), Size(4f * (1f - t * 0.3f), 1.2f)) }
        // Los dos taludes, que bajan al callejón: rojos; uno toma la luz y el otro queda en sombra. Y las banquetas de arriba.
        for ((arriba, abajo, mira) in listOf(Triple(a1, a2, 1f), Triple(b1, b0, -1f))) {
            val talud = poligono(arriba, abajo, at(abajo), at(arriba))
            drawPath(talud, Brush.verticalGradient(listOf(l.cara(C.ROJO, mira * lado * 0.9f, lejos + 0.03f), l.cara(lerp(C.ROJO, C.PIEDRA_OSC, 0.3f), mira * lado * 0.9f, lejos)), at(arriba).y, suelo))
            for (t in listOf(0.3f, 0.6f)) drawLine(l.tono(lerp(C.ROJO, C.TINTA, 0.3f), lejos).copy(alpha = 0.35f), arriba + (at(arriba) - arriba) * t, abajo + (at(abajo) - abajo) * t, 0.4f)
            drawLine(l.tono(Color.White, lejos).copy(alpha = 0.55f), arriba, at(arriba), 0.6f)
        }
        drawPath(poligono(a0, a1, at(a1), at(a0)), l.cara(estuco, 0.9f, lejos)); drawPath(poligono(b1, b2, at(b2), at(b1)), l.cara(estuco, 0.9f, lejos))
        // Los muros de fuera, que solo asoman, y las dos cabeceras, de frente: el corte de cada edificio, con su talud.
        drawPath(poligono(b2, b3, at(b3), at(b2)), l.cara(estuco, lado * 0.9f, lejos)); drawPath(poligono(a0, a3, at(a3), at(a0)), l.cara(estuco, -lado * 0.9f, lejos))
        for ((cara, x0, x1) in listOf(Triple(poligono(a0, a1, a2, a3), 52f, 86f), Triple(poligono(b0, b1, b2, b3), 99f, 133f))) {
            drawPath(cara, Brush.horizontalGradient(listOf(l.cara(estuco, -lado * 0.3f, lejos - 0.04f), l.cara(estuco, lado * 0.4f, lejos - 0.04f)), x0, x1))
            clipPath(cara) {
                drawRect(l.tono(C.ROJO, lejos - 0.04f), Offset(x0, suelo - 3.6f), Size(x1 - x0, 3.6f))
                drawRect(mult(l.tono(estuco, lejos), r.color, 0.7f).copy(alpha = 0.3f + 0.25f * r.dia), Offset(x0, alto), Size(x1 - x0, 1f))
                for (i in 0..4) drawLine(l.tono(lerp(C.PIEDRA_OSC, C.SELVA_OSC, 0.3f), lejos).copy(alpha = 0.14f + 0.06f * (i % 3)), Offset(x0 + 3f + i * 6.4f, alto + 1f), Offset(x0 + 3.4f + i * 6.4f, alto + 4f + (i % 3) * 2f), 0.5f + 0.3f * (i % 2))
            }
            drawPath(cara, mult(l.tono(estuco, lejos), r.color, 0.8f).copy(alpha = 0.35f), style = Stroke(0.4f))
        }
        if (EscenaMeso.juegoDePelota(m)) {
            // Día de juego: los estandartes de los dos bandos en lo alto, y la pelota de hule, en el callejón.
            for ((x, c) in listOf(56f to C.JADE, 69f to C.JADE, 116f to C.MAIZ, 129f to C.MAIZ)) { drawLine(l.tono(C.MADERA, 0.1f), Offset(x, alto), Offset(x, alto - 15f), 0.7f); drawPath(poligono(Offset(x, alto - 15f), Offset(x + 6f, alto - 13.4f), Offset(x, alto - 9f)), l.tono(c, 0.1f)) }
            drawCircle(l.tono(C.TINTA, 0.1f), 1.7f, Offset(93f, suelo - 2.4f)); drawCircle(Color.White.copy(alpha = 0.3f), 0.5f, Offset(92.5f, suelo - 3f))
        }
    }

    /** El templo mayor: una pirámide de nueve cuerpos pintada de rojo, con su escalinata, su templo y su crestería. */
    private fun DrawScope.temploMayor(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val lado = if (r.lat >= 0f) 1f else -1f; val lejos = 0.2f
        val sombraK = 0.3f + 0.3f * r.dia
        // El flanco de la izquierda, que se ve en escorzo.
        val flanco = poligono(Offset(TX - MEDIO_ABAJO, TY), Offset(TX - MEDIO_ARRIBA, CIMA), Offset(TX - MEDIO_ARRIBA - 5f, CIMA + 1.4f), Offset(TX - MEDIO_ABAJO - 15f, TY - 3.4f))
        drawPath(flanco, l.cara(lerp(C.ROJO, C.PIEDRA_OSC, 0.2f), -lado * 0.9f, lejos + 0.04f))
        clipPath(flanco) { for (i in 1 until CUERPOS) { val y = TY - i * ALTO_CUERPO; drawLine(l.tono(C.ESTUCO, lejos + 0.04f).copy(alpha = 0.5f), Offset(TX - medio(y) - 16f, y - 2.6f), Offset(TX - medio(y), y), 0.8f) } }
        // Los nueve cuerpos, en talud: rojo, con su moldura clara y la sombra que deja en el de abajo.
        for (i in 0 until CUERPOS) {
            val y1 = TY - i * ALTO_CUERPO; val y0 = y1 - ALTO_CUERPO
            val a = medio(y1); val b = medio(y0) + 1.6f
            val cara = poligono(Offset(TX - a, y1), Offset(TX - b, y0), Offset(TX + b, y0), Offset(TX + a, y1))
            drawPath(cara, Brush.horizontalGradient(listOf(l.cara(C.ROJO, -lado * 0.3f, lejos), l.cara(lerp(C.ROJO, Color(0xFFD08462), 0.2f), lado * 0.6f, lejos)), TX - a, TX + a))
            clipPath(cara) {
                repeat(5) { val x = az.entre(TX - a, TX + a); drawLine(l.tono(lerp(C.PIEDRA_OSC, C.SELVA_OSC, 0.4f), lejos).copy(alpha = az.entre(0.14f, 0.34f)), Offset(x, y0), Offset(x + az.entre(-0.5f, 0.5f), y0 + ALTO_CUERPO * az.entre(0.4f, 1f)), az.entre(0.5f, 1.6f)) }
                if (az.entero(2) == 0) mancha(Offset(az.entre(TX - a, TX + a), y1 - 2f), az.entre(4f, 9f), 2f, l.tono(C.ESTUCO, lejos), 0.35f)      // un desconchón: asoma el estuco de debajo
            }
            drawRect(l.cara(C.ESTUCO, 0.6f, lejos), Offset(TX - b - 0.6f, y0 - 0.2f), Size(b * 2f + 1.2f, 1.7f))
            drawRect(mult(l.tono(C.ROJO, lejos), r.color, 0.8f).copy(alpha = sombraK), Offset(TX - b, y0 + 1.5f), Size(b * 2f, 1.3f))
        }
        // La escalinata, con sus alfardas.
        val esc = poligono(Offset(TX - 10f, TY), Offset(TX - 7f, CIMA), Offset(TX + 7f, CIMA), Offset(TX + 10f, TY))
        drawPath(esc, Brush.verticalGradient(listOf(l.cara(C.ESTUCO, 0.5f, lejos + 0.05f), l.cara(lerp(C.ESTUCO, Color.White, 0.25f), 0.8f, lejos)), CIMA, TY))
        clipPath(esc) { var y = CIMA + 1.2f; while (y < TY) { drawLine(mult(l.tono(C.ESTUCO, lejos), r.color, 0.7f).copy(alpha = 0.5f), Offset(TX - 11f, y), Offset(TX + 11f, y), 0.45f); y += 1.75f } }
        for (s in listOf(-1f, 1f)) drawPath(poligono(Offset(TX + s * 10f, TY), Offset(TX + s * 7f, CIMA), Offset(TX + s * 9f, CIMA), Offset(TX + s * 13f, TY)), l.cara(lerp(C.ROJO, C.TINTA, 0.15f), s * lado * 0.6f, lejos))
        // El templo de arriba: su puerta, su friso y la crestería calada.
        cuerpo(l, r, Rect(TX - 15f, CIMA - 14f, TX + 15f, CIMA), C.ESTUCO, lejos, az, C.ROJO)
        drawRect(l.tono(lerp(C.TINTA, Color(0xFF3A3050), 0.4f), lejos * 0.6f), Offset(TX - 3f, CIMA - 10f), Size(6f, 10f))
        for (x in listOf(-10.6f, 7.4f)) drawRect(l.tono(lerp(C.TINTA, Color(0xFF3A3050), 0.4f), lejos * 0.6f), Offset(TX + x, CIMA - 9f), Size(3.2f, 9f))
        drawPath(poligono(Offset(TX - 16f, CIMA - 14f), Offset(TX - 12f, CIMA - 19.6f), Offset(TX + 12f, CIMA - 19.6f), Offset(TX + 16f, CIMA - 14f)), l.cara(C.ROJO, lado * 0.3f, lejos))
        val peine = poligono(Offset(TX - 11f, CIMA - 19.6f), Offset(TX - 8f, CIMA - 36f), Offset(TX + 8f, CIMA - 36f), Offset(TX + 11f, CIMA - 19.6f))
        drawPath(peine, Brush.horizontalGradient(listOf(l.cara(C.ESTUCO, -lado * 0.4f, lejos), l.cara(C.ESTUCO, lado * 0.7f, lejos)), TX - 11f, TX + 11f))
        clipPath(peine) {
            for (fila in 0..1) for (i in 0..3) drawRect(l.alto.copy(alpha = 0.75f), Offset(TX - 7.4f + i * 4.2f + fila * 0.6f, CIMA - 33f + fila * 7f), Size(1.5f, 4.2f))
            drawRect(l.tono(C.ROJO, lejos).copy(alpha = 0.7f), Offset(TX - 11f, CIMA - 27f), Size(22f, 1.2f)); drawCircle(l.tono(C.ROJO, lejos).copy(alpha = 0.6f), 2.2f, Offset(TX, CIMA - 23f))
        }
        drawRect(l.cara(C.ESTUCO, 0.8f, lejos), Offset(TX - 8.8f, CIMA - 37f), Size(17.6f, 1.4f))
        // El brasero de lo alto de la escalinata.
        drawRect(l.tono(C.BARRO, lejos * 0.5f), Offset(TX - 1.6f, CIMA - 2.4f), Size(3.2f, 2.4f))
    }

    /** Una estela de un señor de antes: la piedra en pie, con restos de pintura roja, y su altar redondo delante. */
    private fun DrawScope.estelaAntigua(l: Luces, r: Rayo, p: Offset, az: Azar) {
        val lado = if (r.lat >= 0f) 1f else -1f; val h = 11f + az.entre(0f, 3f)
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p.x - 2.3f, p.y - h, p.x + 2.3f, p.y), CornerRadius(1.6f))) }
        drawPath(forma, Brush.horizontalGradient(listOf(l.cara(C.PIEDRA, -lado * 0.8f, 0.14f), l.cara(C.PIEDRA, lado * 0.8f, 0.14f)), p.x - 2.3f, p.x + 2.3f))
        drawRect(l.tono(C.ROJO, 0.14f).copy(alpha = 0.5f), Offset(p.x - 1.5f, p.y - h + 2f), Size(3f, h * 0.5f)); drawLine(l.tono(C.PIEDRA_OSC, 0.14f), Offset(p.x - 1.2f, p.y - h * 0.4f), Offset(p.x + 1.2f, p.y - h * 0.4f), 0.4f)
        drawOval(l.cara(C.PIEDRA, 0.4f, 0.12f), Offset(p.x - 3.4f, p.y + 1.6f), Size(6.8f, 2.2f)); drawOval(l.cara(C.PIEDRA, -0.5f, 0.12f), Offset(p.x - 3.4f, p.y + 2.4f), Size(6.8f, 2.2f)); drawOval(l.cara(C.PIEDRA, 0.6f, 0.12f), Offset(p.x - 3.4f, p.y + 1.4f), Size(6.8f, 2f))
    }

    /** Lo que hay siempre en la plaza: los braseros del templo, el adoratorio, las tinajas de quien va por agua y el camino blanco. */
    private fun DrawScope.cosasDeLaPlaza(m: Momento, l: Luces, r: Rayo) {
        val lado = if (r.lat >= 0f) 1f else -1f
        // El adoratorio: una plataforma baja, con sus cuatro escalones al frente y su zócalo rojo.
        val a = ADORATORIO
        drawPath(poligono(a + Offset(-17f, -6.6f), a + Offset(-14f, -10f), a + Offset(14f, -10f), a + Offset(17f, -6.6f)), l.cara(C.ESTUCO, 0.9f, 0.14f))
        drawRect(Brush.horizontalGradient(listOf(l.cara(C.ESTUCO, -lado * 0.4f, 0.14f), l.cara(C.ESTUCO, lado * 0.5f, 0.14f)), a.x - 17f, a.x + 17f), a + Offset(-17f, -6.6f), Size(34f, 6.6f))
        drawRect(l.tono(C.ROJO, 0.14f), a + Offset(-17f, -2.4f), Size(34f, 2.4f))
        drawRect(mult(l.tono(C.ESTUCO, 0.14f), r.color, 0.7f).copy(alpha = 0.3f + 0.2f * r.dia), a + Offset(-17f, -6.6f), Size(34f, 0.9f))
        // Su altar: una piedra redonda y baja, encima.
        drawOval(l.cara(C.PIEDRA, -0.3f, 0.14f), a + Offset(-4f, -10.4f), Size(8f, 2.6f)); drawOval(l.cara(C.PIEDRA, 0.7f, 0.14f), a + Offset(-4f, -11.6f), Size(8f, 2.6f))
        drawPath(poligono(a + Offset(-4f, -6f), a + Offset(4f, -6f), a + Offset(5.4f, 0.6f), a + Offset(-5.4f, 0.6f)), l.cara(lerp(C.ESTUCO, Color.White, 0.2f), 0.6f, 0.12f))
        for (i in 1..3) drawLine(l.tono(C.PIEDRA_OSC, 0.14f).copy(alpha = 0.5f), a + Offset(-4.4f - i * 0.3f, -6f + i * 1.6f), a + Offset(4.4f + i * 0.3f, -6f + i * 1.6f), 0.4f)
        // Los braseros: dos incensarios altos de piedra, tiznados por arriba.
        for (b in BRASEROS) {
            drawPath(poligono(b + Offset(-1.6f, 0f), b + Offset(-1.2f, -6f), b + Offset(-2.6f, -9f), b + Offset(2.6f, -9f), b + Offset(1.2f, -6f)).apply { lineTo(b.x + 1.6f, b.y); close() }, Brush.horizontalGradient(listOf(l.cara(C.PIEDRA, -lado * 0.7f, 0.16f), l.cara(C.PIEDRA, lado * 0.7f, 0.16f)), b.x - 2.6f, b.x + 2.6f))
            drawOval(l.tono(C.TINTA, 0.16f), Offset(b.x - 2.6f, b.y - 9.8f), Size(5.2f, 1.6f))
            drawRect(l.tono(C.ROJO, 0.16f), Offset(b.x - 1.5f, b.y - 4f), Size(3f, 1f))
        }
        // Las tinajas de quien baja por agua, dejadas en el borde de la aguada.
        if (!EscenaMeso.wayeb(m) && !EscenaMeso.mojado(m)) for ((i, x) in listOf(250f, 255f, 246.4f).withIndex()) {
            val c = Offset(x, 199f + i * 1.4f)
            drawOval(l.cara(if (i == 1) lerp(C.BARRO, C.TINTA, 0.25f) else C.BARRO, lado * 0.4f, 0.06f), Offset(c.x - 2.4f, c.y - 5f), Size(4.8f, 5.4f)); drawRect(l.tono(lerp(C.BARRO, C.TINTA, 0.4f), 0.06f), Offset(c.x - 1.1f, c.y - 6.2f), Size(2.2f, 1.6f))
        }
    }

    /** Los mercaderes de la costa: dos toldos y sus bultos, en la plaza. */
    private fun DrawScope.mercaderes(l: Luces, r: Rayo) {
        for ((x, c) in listOf(92f to C.MAIZ, 118f to C.JADE)) {
            for (dx in listOf(-9f, 9f)) drawLine(l.tono(C.MADERA, 0.08f), Offset(x + dx, 201f), Offset(x + dx, 188f), 0.8f)
            drawPath(poligono(Offset(x - 11f, 189f), Offset(x + 11f, 188f), Offset(x + 12f, 192f), Offset(x - 12f, 193f)), l.cara(c, if (r.lat >= 0f) 0.5f else -0.2f, 0.08f))
            for (i in 0..3) drawOval(l.tono(listOf(C.BARRO, C.ESTUCO, C.PALMA, C.ROJO)[i], 0.08f), Offset(x - 8f + i * 4.4f, 198f), Size(4f, 3f))
        }
    }

    /** Un estandarte de guerra: el escudo redondo en su asta, delante del palacio. */
    private fun DrawScope.escudo(l: Luces, p: Offset) {
        drawLine(l.tono(C.MADERA, 0.2f), p, p + Offset(0f, -15f), 0.8f)
        drawCircle(l.tono(C.ROJO, 0.2f), 2.8f, p + Offset(0f, -15f)); drawCircle(l.tono(C.MAIZ, 0.2f), 1.2f, p + Offset(0f, -15f))
        for (i in 0..2) drawLine(l.tono(C.JADE, 0.2f), p + Offset(-2f + i * 2f, -12.4f), p + Offset(-2.6f + i * 2.6f, -8f), 0.7f)
    }

    /** Los estandartes de fiesta: astas altas con tiras de tela y plumas, en la plaza. */
    private fun DrawScope.estandartes(m: Momento, l: Luces, r: Rayo) {
        val colores = listOf(C.JADE, C.ROJO, C.MAIZ)
        for ((i, x) in listOf(196f, 228f, 268f, 296f, 178f).withIndex()) {
            val pie = Offset(x, if (i < 4) 186f else 203f); val alto = if (i < 4) 22f else 30f
            drawLine(l.tono(C.MADERA, 0.12f), pie, pie + Offset(0f, -alto), 0.8f)
            for (j in 0..2) drawPath(poligono(pie + Offset(0f, -alto + j * 4f), pie + Offset(5.6f + j * 0.6f, -alto + 1.4f + j * 4f), pie + Offset(0f, -alto + 3.4f + j * 4f)), l.cara(colores[(i + j) % 3], if (r.lat >= 0f) 0.5f else -0.2f, 0.12f))
            drawCircle(l.tono(C.JADE, 0.12f), 1.3f, pie + Offset(0f, -alto - 1f))
        }
    }

    /** La aguada: el depósito de la plaza, lleno con las lluvias y bajo en secas, con su borde de piedra y sus reflejos. */
    private fun DrawScope.aguada(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val z = AGUADA; val llena = if (EscenaMeso.lluvias(m)) 1f else 0.7f
        drawOval(l.cara(lerp(C.PIEDRA, C.ESTUCO, 0.4f), 0.5f, 0.1f), Offset(z.left - 2.4f, z.top - 1.6f), Size(z.width + 4.8f, z.height + 3.2f))
        drawOval(l.tono(lerp(C.TIERRA, C.PIEDRA_OSC, 0.4f), 0.1f), z.topLeft, z.size)
        val a = Rect(z.center.x - z.width / 2f * llena, z.center.y - z.height / 2f * llena + 1f, z.center.x + z.width / 2f * llena, z.center.y + z.height / 2f * llena + 1f)
        val agua = Path().apply { addOval(a) }
        drawPath(agua, Brush.verticalGradient(listOf(lerp(l.tono(C.AGUADA, 0.1f), l.medio, 0.4f), l.tono(lerp(C.AGUADA, C.SELVA_OSC, 0.35f), 0.04f)), a.top, a.bottom))
        clipPath(agua) {
            // Lo que el agua devuelve: el rojo del templo y el cielo.
            reflejoRoto(a.left, a.right, a.top, a.height * 0.8f, l.tono(C.ROJO, 0.14f), 0.34f, az, 1.2f)
            if (r.dia > 0.3f) drawRect(Color.White.copy(alpha = 0.16f * r.fuerza), Offset(a.left, a.top), Size(a.width, 1.8f))
            // Hojas de ninfa, y alguna flor con las lluvias.
            val hojas = ArrayList<Offset>(14); repeat(12) { hojas.add(Offset(az.entre(a.left + 4f, a.right - 4f), az.entre(a.top + 2f, a.bottom - 1.6f))) }
            drawPoints(hojas, PointMode.Points, l.tono(lerp(C.SELVA, C.HOJA_LUZ, 0.4f), 0.06f), 2.4f, StrokeCap.Round)
            if (EscenaMeso.lluvias(m)) drawPoints(hojas.take(4).map { it + Offset(0.8f, -0.6f) }, PointMode.Points, l.tono(Color(0xFFF6E8EE), 0.06f), 1.2f, StrokeCap.Round)
        }
        drawOval(mult(l.tono(C.PIEDRA, 0.1f), r.color, 0.8f).copy(alpha = 0.4f), a.topLeft, a.size, style = Stroke(0.7f))
    }

    // ---------------------------------------------------------------- La estela nueva

    /** Un bloque de glifos: un cuadrito de esquinas redondas con algo dentro. No se lee: es su forma. */
    private fun DrawScope.glifo(p: Offset, t: Float, tinta: Color, relleno: Color?) {
        if (relleno != null) drawRoundRect(relleno, p, Size(t, t), CornerRadius(t * 0.3f))
        drawRoundRect(tinta, p, Size(t, t), CornerRadius(t * 0.3f), style = Stroke(maxOf(0.3f, t * 0.16f)))
        drawCircle(tinta, t * 0.16f, p + Offset(t * 0.36f, t * 0.42f)); drawLine(tinta, p + Offset(t * 0.56f, t * 0.3f), p + Offset(t * 0.76f, t * 0.7f), maxOf(0.25f, t * 0.12f))
    }

    /** La estela del fin del k'atun: el sitio marcado, la piedra tumbada, la piedra en su andamio o la estela levantada. */
    private fun DrawScope.estelaNueva(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val e = ESTELA; val w = ANCHO_ESTELA / 2f; val lado = if (r.lat >= 0f) 1f else -1f
        val piedra = lerp(C.PIEDRA, C.ESTUCO, 0.3f)
        fun cara(z: Rect, k: Float = 1f) = drawRect(Brush.horizontalGradient(listOf(l.cara(piedra, -lado * 0.5f * k, 0.06f), l.cara(piedra, lado * 0.7f * k, 0.06f)), z.left, z.right), z.topLeft, z.size)
        when (EscenaMeso.estela(m)) {
            EscenaMeso.Estela.SIN -> {
                // El sitio, marcado con cal y cuatro estacas, entre las estelas de antes y la casa.
                drawOval(l.tono(Color.White, 0.06f).copy(alpha = 0.6f), Offset(e.x - 10f, e.y - 3f), Size(20f, 5f), style = Stroke(0.8f))
                for (d in listOf(Offset(-10f, -0.5f), Offset(10f, -0.5f), Offset(-6f, -3.4f), Offset(6f, 2f))) drawLine(l.tono(C.MADERA, 0.06f), e + d, e + d + Offset(0f, -3.4f), 0.8f)
            }
            EscenaMeso.Estela.PIEDRA -> {
                // La piedra recién traída de la cantera, tumbada sobre sus rodillos, con las sogas y las palancas.
                for (i in 0..5) drawOval(l.tono(C.MADERA, 0.06f), Offset(e.x - 27f + i * 10f, e.y - 2.6f), Size(4.6f, 3.2f))
                val bloque = poligono(Offset(e.x - 28f, e.y - 3f), Offset(e.x - 26f, e.y - 14f), Offset(e.x + 25f, e.y - 15.6f), Offset(e.x + 28f, e.y - 4.4f))
                drawPath(bloque, Brush.verticalGradient(listOf(l.cara(piedra, 0.8f, 0.06f), l.cara(piedra, -0.5f, 0.06f)), e.y - 15.6f, e.y - 3f))
                clipPath(bloque) { for (i in 0..5) drawLine(l.tono(C.PIEDRA_OSC, 0.06f).copy(alpha = 0.3f), Offset(e.x - 20f + i * 8f, e.y - 13f), Offset(e.x - 22f + i * 8f + az.entre(-2f, 2f), e.y - 3f), 0.5f) }
                for (x in listOf(-17f, 14f)) drawLine(l.tono(C.PALMA, 0.06f), Offset(e.x + x, e.y - 14.6f), Offset(e.x + x - 1f, e.y - 3.4f), 1f)
                drawLine(l.tono(C.MADERA, 0.06f), Offset(e.x + 28f, e.y), Offset(e.x + 38f, e.y - 15f), 1.2f); drawLine(l.tono(C.PALMA, 0.06f), Offset(e.x - 27f, e.y - 9f), Offset(e.x - 44f, e.y - 1f), 0.8f)
            }
            EscenaMeso.Estela.EN_OBRA -> {
                val t = EscenaMeso.tallado(m); val z = Rect(e.x - w, e.y - ALTO_ESTELA, e.x + w, e.y)
                // La enramada de los escultores, al lado: cuatro palos y un techo de palma, con su banco, su cántaro y los troncos con que trajeron la piedra.
                val q = Offset(e.x + 36f, e.y - 1f)
                for (dx in listOf(-11f, 11f)) drawLine(l.tono(lerp(C.MADERA, C.TINTA, 0.2f), 0.08f), q + Offset(dx * 0.86f, -4f), q + Offset(dx * 0.86f, -17f), 0.9f)
                drawRect(mult(l.tono(C.ESTUCO, 0.1f), r.color, 0.7f).copy(alpha = 0.3f + 0.3f * r.dia), q + Offset(-12f, -4f), Size(26f, 4.4f))
                drawRect(l.cara(lerp(C.MADERA, C.PALMA, 0.3f), 0.5f, 0.06f), q + Offset(-6f, -4.6f), Size(11f, 1.4f)); for (dx in listOf(-5f, 4f)) drawLine(l.tono(C.MADERA, 0.06f), q + Offset(dx, -3.4f), q + Offset(dx, -0.6f), 0.8f)
                drawOval(l.cara(C.BARRO, lado * 0.4f, 0.06f), Offset(q.x + 7f, q.y - 6.4f), Size(4.6f, 5.6f)); drawRect(l.tono(lerp(C.BARRO, C.TINTA, 0.4f), 0.06f), Offset(q.x + 8.4f, q.y - 7.6f), Size(1.9f, 1.6f))
                for (dx in listOf(-12.6f, 12.6f)) drawLine(l.tono(C.MADERA, 0.06f), q + Offset(dx, 0f), q + Offset(dx, -18f), 1.1f)
                val techo = poligono(q + Offset(-15f, -16f), q + Offset(-12f, -21f), q + Offset(13f, -21.6f), q + Offset(16f, -16.6f))
                drawPath(techo, l.cara(C.PALMA, 0.6f, 0.06f)); drawPath(poligono(q + Offset(-15f, -16f), q + Offset(16f, -16.6f), q + Offset(16f, -14.6f), q + Offset(-15f, -14f)), l.cara(lerp(C.PALMA, C.MADERA, 0.4f), -0.3f, 0.06f))
                clipPath(techo) { for (i in 0..7) drawLine(l.tono(lerp(C.PALMA, C.MADERA, 0.5f), 0.06f).copy(alpha = 0.6f), q + Offset(-14f + i * 4.2f, -16f), q + Offset(-12f + i * 3.6f, -21.6f), 0.4f) }
                val fleco = ArrayList<Offset>(40); for (i in 0..15) { val x = q.x - 15f + i * 2.06f; fleco.add(Offset(x, q.y - 14.4f)); fleco.add(Offset(x - 0.4f, q.y - 12f - (i % 3) * 0.6f)) }
                drawPoints(fleco, PointMode.Lines, l.tono(lerp(C.PALMA, C.MADERA, 0.3f), 0.06f), 0.7f, StrokeCap.Round)
                for (i in 0..2) { val o = q + Offset(20f + i * 0.8f, 2.4f - i * 2.6f); drawLine(l.cara(lerp(C.MADERA, C.CORTEZA, 0.3f), 0.5f, 0.05f), o, o + Offset(17f, -1f), 2.6f, StrokeCap.Round); drawCircle(l.tono(lerp(C.MADERA, C.AMATE, 0.5f), 0.05f), 1.3f, o) }
                // Las lascas y el polvo de cal, al pie.
                drawOval(l.tono(Color.White, 0.06f).copy(alpha = 0.5f), Offset(e.x - 20f, e.y - 2.4f), Size(40f, 6f))
                val lascas = ArrayList<Offset>(40); repeat((8 + 30 * t).toInt()) { lascas.add(Offset(e.x + az.entre(-19f, 19f), e.y + az.entre(-1.6f, 3f))) }
                drawPoints(lascas, PointMode.Points, l.tono(piedra, 0.04f), 1.5f, StrokeCap.Square)
                // Los palos de detrás del andamio.
                for (x in listOf(-10f, 10f)) drawLine(l.tono(lerp(C.MADERA, C.TINTA, 0.3f), 0.08f), Offset(e.x + x, e.y - 1.6f), Offset(e.x + x * 0.94f, e.y - ALTO_ESTELA - 6f), 1f)
                // La piedra: arriba, lo ya tallado; abajo, el dibujo en rojo que hizo él.
                cara(z)
                val corte = z.top + z.height * t
                clipPath(Path().apply { addRect(Rect(z.left, z.top, z.right, corte)) }) { relieve(l, r, z, false) }
                clipPath(Path().apply { addRect(Rect(z.left, corte, z.right, z.bottom)) }) {
                    val rojo = l.tono(C.ROJO, 0.06f).copy(alpha = 0.7f)
                    drawRect(rojo, Offset(z.left + 1.4f, z.top + 1.6f), Size(z.width - 2.8f, z.height - 3.2f), style = Stroke(0.4f))
                    for (i in 0..9) glifo(Offset(z.left + 2f, z.top + 4f + i * 4.9f), 3.4f, rojo, null)
                    drawOval(rojo, Offset(z.left + 8f, z.top + 8f), Size(6f, 8f), style = Stroke(0.4f)); drawLine(rojo, Offset(z.left + 11f, z.top + 16f), Offset(z.left + 11f, z.bottom - 6f), 0.4f); drawLine(rojo, Offset(z.left + 7.4f, z.top + 26f), Offset(z.left + 15f, z.top + 22f), 0.4f)
                }
                drawLine(l.tono(Color.White, 0.06f).copy(alpha = 0.7f), Offset(z.left, corte), Offset(z.right, corte), 0.6f)
                // El andamio: los palos de delante, los travesaños atados y el tablón a la altura del trabajo.
                val palo = l.tono(C.MADERA, 0.04f); val paloLuz = l.cara(C.MADERA, 0.8f, 0.04f)
                for (x in listOf(-13f, 13f)) { drawLine(palo, Offset(e.x + x, e.y + 1f), Offset(e.x + x * 0.96f, e.y - ALTO_ESTELA - 7f), 1.5f, StrokeCap.Round); drawLine(paloLuz.copy(alpha = 0.6f * r.dia), Offset(e.x + x + lado * 0.4f, e.y + 1f), Offset(e.x + x * 0.96f + lado * 0.4f, e.y - ALTO_ESTELA - 7f), 0.5f) }
                for (y in listOf(15f, 30f, 45f, 59f)) { drawLine(palo, Offset(e.x - 14.6f, e.y - y), Offset(e.x + 14.6f, e.y - y - 0.6f), 1.1f, StrokeCap.Round); for (x in listOf(-13f, 13f)) drawCircle(l.tono(C.PALMA, 0.04f), 1f, Offset(e.x + x * 0.97f, e.y - y)) }
                val tablon = (corte + 7f).coerceIn(z.top + 8f, e.y - 6f)
                drawRect(l.cara(lerp(C.MADERA, C.PALMA, 0.4f), 0.5f, 0.04f), Offset(e.x - 15.6f, tablon), Size(31.2f, 1.6f)); drawRect(mult(l.tono(C.MADERA, 0.04f), r.color, 0.8f).copy(alpha = 0.5f), Offset(e.x - 15.6f, tablon + 1.6f), Size(31.2f, 0.9f))
                // Las herramientas de los escultores, dejadas en el tablón: el mazo, los cinceles de piedra y el calabazo del agua.
                drawRect(l.tono(C.PIEDRA_OSC, 0.04f), Offset(e.x + 8f, tablon - 2.2f), Size(3.6f, 2.2f)); drawLine(l.tono(C.MADERA, 0.04f), Offset(e.x + 9.8f, tablon - 2f), Offset(e.x + 14f, tablon - 4.4f), 0.7f)
                drawCircle(l.cara(C.MAIZ, 0.4f, 0.04f), 1.7f, Offset(e.x - 11f, tablon - 1.7f))
                // La escalera de palos, apoyada.
                val a0 = Offset(e.x - 23f, e.y + 2f); val a1 = Offset(e.x - 14.6f, tablon)
                for (d in listOf(0f, 2.6f)) drawLine(palo, a0 + Offset(d, 0f), a1 + Offset(d, 0f), 0.8f)
                for (i in 1..6) { val p = a0 + (a1 - a0) * (i / 7f); drawLine(palo, p, p + Offset(2.6f, 0f), 0.6f) }
            }
            EscenaMeso.Estela.LEVANTADA -> {
                val z = Rect(e.x - w, e.y - ALTO_ESTELA, e.x + w, e.y)
                val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(z, CornerRadius(4f))) }
                // El canto, del lado que se ve, y la cara.
                drawPath(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(z.left - 2.2f, z.top + 1f, z.right - 2.2f, z.bottom - 0.6f), CornerRadius(4f))) }, l.cara(piedra, -lado * 0.9f, 0.06f))
                clipPath(forma) { cara(z); relieve(l, r, z, true) }
                drawPath(forma, mult(l.tono(piedra, 0.06f), r.color, 0.7f).copy(alpha = 0.5f), style = Stroke(0.5f))
                // Su altar redondo, delante.
                drawOval(l.cara(piedra, -0.5f, 0.04f), Offset(e.x - 9f, e.y + 3.2f), Size(18f, 5f)); drawRect(l.cara(piedra, -0.3f, 0.04f), Offset(e.x - 9f, e.y + 3.4f), Size(18f, 2.6f)); drawOval(l.cara(piedra, 0.7f, 0.04f), Offset(e.x - 9f, e.y + 1f), Size(18f, 5f))
                if (EscenaMeso.copal(m) || EscenaMeso.finDelKatun(m)) {
                    // La ofrenda del día: el incensario, flores y un cuenco.
                    drawRect(l.tono(C.BARRO, 0.04f), Offset(e.x - 2f, e.y - 0.6f), Size(4f, 3.6f)); drawOval(l.tono(lerp(C.BARRO, C.TINTA, 0.4f), 0.04f), Offset(e.x - 2.4f, e.y - 1.4f), Size(4.8f, 1.6f))
                    drawPoints(listOf(Offset(e.x - 5.4f, e.y + 2.6f), Offset(e.x - 6.6f, e.y + 3.4f), Offset(e.x + 5.6f, e.y + 2.8f), Offset(e.x + 6.8f, e.y + 3.6f)), PointMode.Points, l.tono(C.MAIZ, 0.04f), 1.5f, StrokeCap.Round)
                }
            }
        }
    }

    /** El relieve de la estela: el marco, el señor de pie con su tocado (una silueta sencilla) y las columnas de glifos. Con [pintada], con su color. */
    private fun DrawScope.relieve(l: Luces, r: Rayo, z: Rect, pintada: Boolean) {
        val lado = if (r.lat >= 0f) 1f else -1f
        val hondo = mult(l.tono(lerp(C.PIEDRA, C.ESTUCO, 0.3f), 0.06f), r.color, 0.9f).copy(alpha = 0.75f); val filo = l.tono(Color.White, 0.06f).copy(alpha = 0.5f * r.dia + 0.1f)
        if (pintada) drawRect(l.tono(C.ROJO, 0.06f).copy(alpha = 0.8f), Offset(z.left + 1.2f, z.top + 1.6f), Size(z.width - 2.4f, z.height - 3f))
        drawRect(hondo, Offset(z.left + 1.2f, z.top + 1.6f), Size(z.width - 2.4f, z.height - 3f), style = Stroke(0.6f))
        // El señor: el tocado de plumas, la cabeza de perfil, el cuerpo con su barra y los pies.
        val cuerpo = if (pintada) l.tono(lerp(C.ESTUCO, C.MAIZ, 0.2f), 0.06f) else l.tono(lerp(C.PIEDRA, C.ESTUCO, 0.5f), 0.06f)
        val cx = z.left + z.width * 0.66f
        val figura = Path().apply {
            moveTo(cx - 2.6f, z.top + 12f); cubicTo(cx - 5f, z.top + 6f, cx - 1f, z.top + 3f, cx + 2.6f, z.top + 4f); cubicTo(cx + 4.4f, z.top + 6f, cx + 3.6f, z.top + 10f, cx + 2.4f, z.top + 12.4f)
            lineTo(cx + 3.4f, z.top + 16f); lineTo(cx + 3f, z.bottom - 12f); lineTo(cx + 3.8f, z.bottom - 4f); lineTo(cx - 3.6f, z.bottom - 4f); lineTo(cx - 3f, z.bottom - 12f); lineTo(cx - 3.6f, z.top + 16f); close()
        }
        drawPath(figura, cuerpo); drawPath(figura, hondo, style = Stroke(0.5f))
        drawLine(filo, Offset(cx - 2.6f * lado, z.top + 13f), Offset(cx - 3.2f * lado, z.bottom - 6f), 0.5f)
        if (pintada) { drawRect(l.tono(C.JADE, 0.06f), Offset(cx - 3.4f, z.top + 17f), Size(6.8f, 2f)); drawCircle(l.tono(C.JADE, 0.06f), 1.1f, Offset(cx + 0.6f, z.top + 8f)) }
        drawLine(hondo, Offset(cx - 5.4f, z.top + 22f), Offset(cx + 4.4f, z.top + 18f), 0.9f)                           // la barra que sostiene
        for (i in 0..2) drawLine(if (pintada) l.tono(C.JADE, 0.06f) else hondo, Offset(cx - 1f + i * 1.4f, z.top + 4f), Offset(cx - 4f + i * 2.2f, z.top + 1.6f), 0.7f)   // las plumas del tocado
        // La columna de glifos, a un lado, y la fecha, abajo.
        for (i in 0..9) glifo(Offset(z.left + 1.8f, z.top + 4f + i * 4.6f), 3.3f, hondo, if (pintada) l.tono(C.ESTUCO, 0.06f) else null)
        for (i in 0..2) glifo(Offset(z.left + 6.4f + i * 3.2f, z.bottom - 4.6f), 2.6f, hondo, if (pintada) l.tono(C.ESTUCO, 0.06f) else null)
    }

    // ---------------------------------------------------------------- La noche

    /** Lo que alumbra de noche en la ciudad: el fuego de lo alto del templo, los braseros del palacio y las antorchas de la plaza en fiesta. */
    private fun DrawScope.lucesDeLaNoche(m: Momento, l: Luces) {
        if (l.noche < 0.2f) return
        if (EscenaMeso.fuegoDelTemplo(m)) {
            val f = EscenaMeso.FUEGO_TEMPLO
            resplandor(f + Offset(0f, -6f), 30f, 0.85f * l.noche); resplandor(f + Offset(0f, 14f), 20f, 0.4f * l.noche)
            // La luz sube por la fachada del templo y baja por la escalinata.
            drawRect(C.LUMBRE.copy(alpha = 0.24f * l.noche), Offset(TX - 15f, CIMA - 14f), Size(30f, 14f), blendMode = BlendMode.Plus)
            drawPath(poligono(Offset(TX - 7f, CIMA), Offset(TX + 7f, CIMA), Offset(TX + 9f, CIMA + 30f), Offset(TX - 9f, CIMA + 30f)), Brush.verticalGradient(listOf(C.LUMBRE.copy(alpha = 0.3f * l.noche), Color.Transparent), CIMA, CIMA + 30f), blendMode = BlendMode.Plus)
        }
        if (EscenaMeso.fuegoDelTemplo(m)) for (b in BRASEROS) { resplandor(b + Offset(0f, -10f), 15f, 0.7f * l.noche); mancha(b + Offset(0f, 2f), 20f, 5f, C.LUMBRE, 0.3f * l.noche); mancha(b + Offset(0f, -13f), 15f, 11f, C.LUMBRE, 0.26f * l.noche) }
        if (!EscenaMeso.wayeb(m) && (m.hora >= 18.0 || m.hora < 0.5)) {
            for (x in listOf(108f, 162.4f)) { resplandor(Offset(x, 150f), 13f, 0.6f * l.noche); mancha(Offset(x, 163.4f), 17f, 3.6f, C.LUMBRE, 0.3f * l.noche) }
            if (EscenaMeso.fiesta(m)) for (x in listOf(196f, 228f, 268f, 296f)) { resplandor(Offset(x, 183f), 11f, 0.7f * l.noche); mancha(Offset(x, 186.8f), 14f, 3.4f, C.LUMBRE, 0.32f * l.noche) }
        }
        // La luna en el estuco: un filo frío en lo alto de la plaza.
        if (l.estrellas > 0.3f) drawRect(Brush.verticalGradient(listOf(Color(0xFFBCC8F0).copy(alpha = 0.1f * l.estrellas), Color.Transparent), BORDE, 190f), Offset(40f, BORDE), Size(300f, 33f), blendMode = BlendMode.Plus)
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.ciudadViva(m: Momento, l: Luces, reloj: Float) {
        val viento = m.viento * 0.7f + 0.12f
        val calma = EscenaMeso.wayeb(m)
        // El humo de los fogones de las casas, al amanecer y al caer la tarde.
        if (!calma && (m.hora in 5.0..8.0 || m.hora in 17.0..20.4)) { humo(l, Offset(84f, 141f), 22f, 7f, reloj, viento, 0.15f, 0.3f, 1, 6, 0.7f); humo(l, Offset(343f, 119f), 20f, 6f, reloj, viento, 0.15f, 0.3f, 2, 6, 0.7f) }
        // El copal de lo alto del templo: humo blanco, que sube derecho.
        if (EscenaMeso.copalEnElTemplo(m)) humo(l, Offset(TX, CIMA - 3f), 46f, 16f, reloj, viento * 0.5f, 0f, 0.14f, 3, 9, 1f)
        if ((EscenaMeso.copal(m) || EscenaMeso.finDelKatun(m)) && EscenaMeso.estela(m) == EscenaMeso.Estela.LEVANTADA) humo(l, ESTELA + Offset(0f, -2f), 30f, 10f, reloj, viento * 0.5f, 0f, 0.04f, 4, 7, 0.9f)
        if (EscenaMeso.fuegoDelTemplo(m)) {
            llama(EscenaMeso.FUEGO_TEMPLO, 5.4f, reloj, 5)
            for ((i, b) in BRASEROS.withIndex()) llama(b + Offset(0f, -9f), 4.6f, reloj, 11 + i)
            if (l.noche > 0.4f) reflejo(Offset(AGUADA.center.x - 14f, AGUADA.top + 2f), AGUADA.height * 0.6f, C.LUMBRE, 0.5f * l.noche, reloj, 3)
        }
        if (!calma && l.noche > 0.3f && EscenaMeso.fiesta(m)) for ((i, x) in listOf(196f, 228f, 268f, 296f).withIndex()) llama(Offset(x, 184f), 3.6f, reloj, 7 + i)
        // La quema de la milpa: las llamas y el humo pardo, que se lleva el viento; y las milpas lejanas.
        if (EscenaMeso.milpa(m) == EscenaMeso.Milpa.ARDE) {
            for ((i, f) in SelvaMeso.FUEGOS.withIndex()) { llama(f, 4.6f + (i % 2) * 1.6f, reloj, 20 + i); if (i % 2 == 0) humo(l, f + Offset(0f, -3f), 74f, 30f, reloj, viento + 0.2f, 0.5f, 0.1f, 30 + i, 9, 0.9f) }
            for ((i, p) in listOf(Offset(58f, 125f), Offset(212f, 127.6f), Offset(286f, 124.6f)).withIndex()) humo(l, p, 40f, 16f, reloj, viento + 0.2f, 0.45f, 0.6f, 40 + i, 7, 0.8f)
        }
        // La guerra de la ciudad vecina: humo negro, lejos, al oeste.
        if (EscenaMeso.guerra(m)) humo(l, Offset(34f, H - 4f), 52f, 22f, reloj, viento + 0.15f, 0.8f, 0.7f, 50, 8, 0.9f)
        // Le piden lluvia a Chaak en la cueva del cerro: un hilo de copal, lejos.
        if (EscenaMeso.hoyEs(m, "chaak")) humo(l, Offset(302f, H + 2f), 24f, 6f, reloj, viento * 0.5f, 0f, 0.7f, 51, 6, 0.8f)
        // Los destellos de la aguada, de día; las luciérnagas de la milpa, de noche.
        if (l.noche < 0.5f && !EscenaMeso.mojado(m)) destellos(l, AGUADA, reloj, m.semilla("aguada"), 9)
        val estado = EscenaMeso.milpa(m)
        if (l.noche > 0.5f && !EscenaMeso.mojado(m) && estado != EscenaMeso.Milpa.ARDE && estado != EscenaMeso.Milpa.CENIZA && m.ficha.clima != Clima.NIEBLA) {
            val az = Azar(Semilla.de("meso", "luciernagas"))
            for (grupo in 0..2) {
                val brillo = (0.5f + 0.5f * sin(reloj * (1.1f + grupo * 0.37f) + grupo * 2.1f)).let { it * it }
                val puntos = ArrayList<Offset>(14)
                repeat(14) { puntos.add(Offset(az.entre(304f, 366f) + 1.6f * sin(reloj * 0.6f + it), az.entre(130f, 186f) + 1.2f * sin(reloj * 0.5f + it * 1.7f))) }
                drawPoints(puntos, PointMode.Points, Color(0xFFE6F08A).copy(alpha = 0.25f + 0.7f * brillo), 1.3f, StrokeCap.Round)
                drawPoints(puntos, PointMode.Points, Color(0xFFE6F08A).copy(alpha = 0.16f * brillo), 3.6f, StrokeCap.Round)
            }
        }
        // La tormenta que saca de la cueva una nube de murciélagos, al caer la tarde.
        if (EscenaMeso.murcielagos(m)) {
            val az = Azar(Semilla.de("meso", "murcielagos")); val alas = ArrayList<Offset>(120)
            repeat(40) { val p = Offset(250f + az.entre(-60f, 90f) + 14f * sin(reloj * 0.4f + it), 60f + az.entre(-34f, 44f) + 6f * sin(reloj * 0.7f + it * 1.3f)); val w = 1.6f + 0.8f * sin(reloj * 9f + it); alas.add(p + Offset(-w, -0.6f)); alas.add(p); alas.add(p); alas.add(p + Offset(w, -0.6f)) }
            drawPoints(alas, PointMode.Lines, Color(0xFF241E2C).copy(alpha = 0.8f), 0.7f, StrokeCap.Round)
        }
    }
}
