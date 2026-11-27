package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.Frente
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TrazosFrente
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.sombraSuave
import io.github.ruddy2003.vestigium.ui.selloCarta
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Frente exterior
// =====================================================================

/**
 * Frente exterior: el archivo de despachos de la red, en la base escondida en ruinas. El estante es de chapa color hueso,
 * abierto por detrás: se ve el muro de sillares viejos, con su humedad y su musgo, un panel de vidrio grabado empotrado en
 * cada repisa y las raíces que asoman bajo las tablas y bajan por la piedra. Los costados son montantes de canto redondo con
 * un piloto ámbar y su placa grabada en cada nivel (a la izquierda) o una palanca de baquelita (a la derecha). Arriba, bajo
 * la losa de hormigón de donde cuelgan las raíces, la consola curva: el tubo de luz, los mazos de cable, la pantalla redonda
 * de radar con su retícula y su barrido, la fila de
 * pilotos y de palancas, la pantalla de segmentos de la frecuencia con su perilla, la placa grabada del archivo, la rejilla
 * del altavoz y los cascos colgados de su gancho, con el cable rizado que cae por delante. Las tablas son chapa doblada en un
 * labio redondo, con la pintura saltada, y cada ciclo va en una placa de baquelita grabada. Abajo, el zócalo con sus
 * rejillas y el piso de hormigón con la franja de peligro, el cable grueso y la cinta del teletipo enrollada. Cada entrada es uno de tres objetos del archivo: el dosier
 * de lona acero con su rótulo ámbar y la foto engrapada que asoma, la carpeta de despacho de cartón hueso con su correa de
 * baquelita y la cinta perforada que sale por arriba, o el cartucho de datos de baquelita con su muesca, su pestaña ámbar y
 * su etiqueta rotulada; de noche, la luna en lugar del sello. De noche el tubo del techo se apaga y quedan el resplandor
 * verde del radar, del vidrio y de los pilotos, en la penumbra verdosa de la base. Distinto del Ideario, que son las latas de microfilm del panel falso de la sala de radar.
 * La tapa del diario es el dosier del día, de frente, con la foto engrapada, el rótulo ámbar y la hoja de texto en claro
 * pegada; al abrirse, la tapa gira sobre el lomo y deja ver la hoja de despacho cifrado.
 */
object EstanteFrente : DisenoBiblioteca {
    // ---------------------------------------------------------------- la paleta de la sala
    private val HUESO = Color(0xFFE4DED1)
    private val PANEL = Color(0xFFF0EBE0)
    private val HUESO_OSC = Color(0xFFD4CCBC)
    private val BORDE = Color(0xFFA69E8E)
    private val PIZARRA = Color(0xFF23252A)
    private val TENUE = Color(0xFF5A5D63)
    /** El ámbar quemado de la tinta de los sellos y los rótulos impresos. */
    private val AMBAR = Color(0xFFB85C0F)
    /** El piloto ámbar encendido y el rótulo del dosier. */
    private val PILOTO = Color(0xFFF2A33A)
    private val PILOTO_ROJO = Color(0xFFD9372B)
    private val ACERO = Color(0xFF3E4C57)
    private val BAQUELITA = Color(0xFF2A2622)
    private val CROMO = Color(0xFFB9B6AE)
    private val RADAR = Color(0xFF9FD8A8)
    private val PANTALLA = Color(0xFF0F1A16)
    private val VERDE = Color(0xFF4F7A5A)
    /** La hoja de despacho, un poco más clara que el hueso de la consola. */
    private val PAPEL = Color(0xFFF2EDE1)
    /** El cartón hueso de las carpetas de despacho (el mismo de la funda de las transmisiones). */
    private val CARTON = Color(0xFFCFC7B5)
    /** La oblea ámbar del sello de la célula. */
    private val OBLEA = Color(0xFFE8902C)
    /** La sala con las luces bajas (modo oscuro) y el petróleo de la madrugada (de noche). */
    private val SALA = Color(0xFF121518)
    private val NOCHE = Color(0xFF0B1519)
    /** La sombra del cuarto, verde negro (la de la piedra húmeda), y la penumbra verdosa de la base de noche. */
    private val SOMBRA_CUARTO = Color(0xFF16201C)
    private val PENUMBRA = Color(0xFF0E1614)
    /** Las ruinas que esconden la base: la piedra (a la luz y en sombra), la raíz, el musgo, el cobre y el hormigón de la losa. */
    private val PIEDRA = TrazosFrente.PIEDRA
    private val PIEDRA_SOMBRA = TrazosFrente.PIEDRA_SOMBRA
    private val RAIZ = TrazosFrente.RAIZ
    private val MUSGO = TrazosFrente.MUSGO
    private val COBRE = TrazosFrente.COBRE
    /** El vidrio grabado: el trazo iluminado por el canto y el vidrio mismo (en penumbra y a la luz). */
    private val VIDRIO = TrazosFrente.VIDRIO
    private val VIDRIO_LUZ = TrazosFrente.VIDRIO_LUZ
    private val VIDRIO_TRAZO = TrazosFrente.VIDRIO_TRAZO
    private val PELIGRO = TrazosFrente.PELIGRO
    /** La lona acero de los dosieres, cada uno de un lote un poco distinto. */
    private val LONAS = listOf(Color(0xFF3E4C57), Color(0xFF46535C), Color(0xFF384650), Color(0xFF4A5753), Color(0xFF424A55))

    private val TITULO: FontFamily = LetraExtra.michroma
    private val TEXTO: FontFamily = LetraExtra.chivo
    private val MONO: FontFamily = LetraExtra.chivoMono

    /** Un esquema cualquiera para el sello de la célula, que no lo usa (solo pide uno). */
    private val ESQUEMA = lightColorScheme()

    /** El color con la luz del momento: en modo oscuro, la sala con las luces bajas; de noche, además, el petróleo de la madrugada. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, SALA, 0.44f)
        if (noche) r = mezcla(r, NOCHE, 0.5f)
        return r
    }

    /** El papel de las etiquetas y las hojas: se hunde menos que lo demás, para que lo escrito se lea en oscuro y de noche. */
    private fun papel(oscuro: Boolean, noche: Boolean): Color {
        var r = PAPEL
        if (oscuro) r = mezcla(r, SALA, 0.2f)
        if (noche) r = mezcla(r, NOCHE, 0.22f)
        return r
    }

    /** El ámbar de los rótulos: se hunde poco, como un piloto, para que lo grabado encima se siga leyendo. */
    private fun ambar(oscuro: Boolean, noche: Boolean): Color {
        var r = PILOTO
        if (oscuro) r = mezcla(r, SALA, 0.2f)
        if (noche) r = mezcla(r, NOCHE, 0.24f)
        return r
    }

    // =================================================================
    //  Ayudantes de dibujo de la sala
    // =================================================================

    /** El ancho de un texto, para ajustar placas y rótulos a lo que dicen. */
    private fun anchoTx(medidor: TextMeasurer, texto: String, letra: FontFamily, tam: Float, espaciado: Float = 0f, peso: FontWeight = FontWeight.Normal): Float =
        medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, fontWeight = peso, letterSpacing = espaciado.sp), maxLines = 1, softWrap = false).size.width.toFloat()

    /** Un texto alineado a la izquierda dentro de [r]: [textoEn] en una caja de su ancho medido. */
    private fun DrawScope.aIzquierda(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                                     espaciado: Float = 0f, peso: FontWeight = FontWeight.Normal, minimo: Float = 6f) {
        val ancho = anchoTx(medidor, texto, letra, tam, espaciado, peso) + 2f
        textoEn(medidor, texto, Rect(r.left, r.top, minOf(r.right, r.left + ancho), r.bottom), letra, color, tam, peso = peso, espaciado = espaciado, minimo = minimo)
    }

    /**
     * Un texto en [r]: en un renglón si cabe a [tam]; si no cabe y tiene un « · », se parte ahí en dos renglones a [k] veces
     * el tamaño (la clave arriba, la fecha abajo). [r] debe tener alto para los dos.
     */
    private fun DrawScope.enDos(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                                espaciado: Float = 0f, k: Float = 0.8f) {
        val i = texto.indexOf(" · ")
        if (i <= 0 || anchoTx(medidor, texto, letra, tam, espaciado) <= r.width) {
            textoEn(medidor, texto, r, letra, color, tam, espaciado = espaciado, minimo = tam * 0.5f)
            return
        }
        val mitad = r.top + r.height / 2
        textoEn(medidor, texto.substring(0, i).trim(), Rect(r.left, r.top, r.right, mitad), letra, color, tam * k, espaciado = espaciado, minimo = tam * 0.42f)
        textoEn(medidor, texto.substring(i + 3).trim(), Rect(r.left, mitad, r.right, r.bottom), letra, color, tam * k, espaciado = espaciado, minimo = tam * 0.42f)
    }

    /** Dibuja [bloque] con la transformación afín x' = a·x + c·y + tx, y' = b·x + d·y + ty (para las tapas que giran). */
    private fun DrawScope.afin(a: Float, b: Float, c: Float, d: Float, tx: Float, ty: Float, bloque: DrawScope.() -> Unit) {
        withTransform({ transform(Matrix(floatArrayOf(a, b, 0f, 0f, c, d, 0f, 0f, 0f, 0f, 1f, 0f, tx, ty, 0f, 1f))) }) { bloque() }
    }

    /** La pestaña [k] de las cinco: exactamente una quinta parte del ancho [w] (ahí se toca). */
    private fun quinta(k: Int, w: Float, arriba: Float, abajo: Float): Rect = Rect(w * k / 5f, arriba, w * (k + 1) / 5f, abajo)

    /**
     * El emblema de la red, el mismo de todo el tema ([TrazosFrente.emblemaRed]): el círculo roto arriba a la derecha, la
     * célula en el centro y los tres trazos de la chispa que se abren y se escapan por el hueco.
     */
    private fun DrawScope.emblema(c: Offset, r: Float, color: Color, grosor: Float) {
        with(TrazosFrente) { emblemaRed(c, r, color, grosor) }
    }

    /** La luna creciente, como figura rellena (para los sellos y las etiquetas de noche). */
    private fun luna(c: Offset, r: Float): Path {
        val a = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
        val b = Path().apply { addOval(Rect(c.x - r * 0.45f, c.y - r * 1.15f, c.x + r * 1.35f, c.y + r * 0.62f)) }
        return Path().apply { op(a, b, PathOperation.Difference) }
    }

    /** Un tornillo de ranura, con su sombra y la ranura girada [giro] grados. */
    private fun DrawScope.tornillo(o: Offset, r: Float, color: Color, giro: Float = 30f) {
        val u = density
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(o.x + 0.4f * u, o.y + 0.5f * u))
        drawCircle(color, r, o)
        drawCircle(mezcla(color, Color.Black, 0.4f), r, o, style = Stroke(0.5f * u))
        rotate(giro, o) { drawLine(mezcla(color, Color.Black, 0.55f), Offset(o.x - r * 0.7f, o.y), Offset(o.x + r * 0.7f, o.y), maxOf(0.5f * u, r * 0.28f)) }
    }

    /**
     * Un piloto de la consola en su aro de cromo: encendido, con su halo (más fuerte con [fuerza]) y el brillo del vidrio;
     * apagado, el vidrio oscuro del mismo color.
     */
    private fun DrawScope.piloto(c: Offset, r: Float, luz: Color, encendido: Boolean, aro: Color = CROMO, fuerza: Float = 1f) {
        val u = density
        if (encendido) drawCircle(Brush.radialGradient(listOf(luz.copy(alpha = (0.42f * fuerza).coerceAtMost(0.8f)), luz.copy(alpha = 0f)), c, r * 3.4f), r * 3.4f, c)
        drawCircle(Color.Black.copy(alpha = 0.35f), r * 1.45f, Offset(c.x + 0.4f * u, c.y + 0.6f * u))
        drawCircle(aro, r * 1.42f, c)
        drawCircle(mezcla(aro, Color.Black, 0.35f), r * 1.42f, c, style = Stroke(0.6f * u))
        drawCircle(if (encendido) luz else mezcla(luz, Color.Black, 0.64f), r, c)
        if (encendido) drawCircle(mezcla(luz, Color.White, 0.55f), r * 0.5f, Offset(c.x - r * 0.15f, c.y - r * 0.15f))
        drawCircle(Color.White.copy(alpha = if (encendido) 0.7f else 0.28f), r * 0.22f, Offset(c.x - r * 0.38f, c.y - r * 0.4f))
    }

    /** Una palanca de la consola: la placa de cromo con su tuerca y el mango de baquelita, hacia arriba o hacia abajo. */
    private fun DrawScope.palanca(c: Offset, alto: Float, arriba: Boolean, aro: Color, mango: Color) {
        val u = density
        val pl = Rect(c.x - alto * 0.32f, c.y - alto * 0.5f, c.x + alto * 0.32f, c.y + alto * 0.5f)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), pl.topLeft + Offset(0.5f * u, 0.8f * u), pl.size, CornerRadius(alto * 0.12f))
        drawRoundRect(aro, pl.topLeft, pl.size, CornerRadius(alto * 0.12f))
        drawRoundRect(Color.White.copy(alpha = 0.3f), pl.topLeft, Size(pl.width, pl.height * 0.16f), CornerRadius(alto * 0.12f))
        drawCircle(mezcla(aro, Color.Black, 0.35f), alto * 0.17f, c)
        drawCircle(mezcla(aro, Color.White, 0.3f), alto * 0.11f, c)
        val punta = Offset(c.x, c.y + (if (arriba) -1f else 1f) * alto * 0.62f)
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(c.x + 0.8f * u, c.y + 1f * u), Offset(punta.x + 1.2f * u, punta.y + 1.4f * u), alto * 0.13f, StrokeCap.Round)
        drawLine(mango, c, punta, alto * 0.13f, StrokeCap.Round)
        drawCircle(mango, alto * 0.11f, punta)
        drawCircle(Color.White.copy(alpha = 0.25f), alto * 0.04f, Offset(punta.x - alto * 0.03f, punta.y - alto * 0.03f))
    }

    /** La rejilla de un altavoz: el marco redondeado y sus ranuras horizontales. */
    private fun DrawScope.rejilla(r: Rect, marco: Color, ranura: Color) {
        val u = density
        drawRoundRect(marco, r.topLeft, r.size, CornerRadius(minOf(r.height, r.width) * 0.2f))
        drawRoundRect(Color.Black.copy(alpha = 0.25f), r.topLeft, r.size, CornerRadius(minOf(r.height, r.width) * 0.2f), style = Stroke(0.6f * u))
        var y = r.top + 3f * u
        while (y < r.bottom - 2.5f * u) {
            drawLine(ranura, Offset(r.left + 3f * u, y), Offset(r.right - 3f * u, y), 1.3f * u, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = 0.18f), Offset(r.left + 3f * u, y + 1f * u), Offset(r.right - 3f * u, y + 1f * u), 0.5f * u)
            y += 3.2f * u
        }
    }

    /** Una grapa de cromo de [a] a [b], con sus dos patas dobladas y su brillo. */
    private fun DrawScope.grapa(a: Offset, b: Offset) {
        val u = density
        drawLine(Color.Black.copy(alpha = 0.35f), a + Offset(0.5f * u, 0.7f * u), b + Offset(0.5f * u, 0.7f * u), 1.4f * u, StrokeCap.Round)
        drawLine(CROMO, a, b, 1.1f * u, StrokeCap.Round)
        drawLine(Color.White.copy(alpha = 0.7f), a + (b - a) * 0.2f, a + (b - a) * 0.55f, 0.4f * u, StrokeCap.Round)
        listOf(a, b).forEach { o -> drawCircle(mezcla(CROMO, Color.Black, 0.45f), 0.7f * u, o) }
    }

    /**
     * Una foto en blanco y negro con su orilla blanca: de día, alguien de hombros contra el muro del puerto, con una antena
     * detrás; de noche, el cielo con sus estrellas y la luna. Un poco de grano encima.
     */
    private fun DrawScope.foto(r: Rect, semilla: Int, oscuro: Boolean, noche: Boolean) {
        val u = density
        val orilla = minOf(r.width, r.height) * 0.08f
        drawRect(Color.Black.copy(alpha = 0.28f), r.topLeft + Offset(0.8f * u, 1.2f * u), r.size)
        drawRect(t(Color(0xFFF2EFE7), oscuro, false), r.topLeft, r.size)
        val im = r.deflate(orilla)
        clipRect(im.left, im.top, im.right, im.bottom) {
            if (noche) {
                drawRect(t(Color(0xFF1E2A2D), oscuro, false), im.topLeft, im.size)
                for (k in 0 until 14) drawCircle(Color.White.copy(alpha = 0.4f + 0.5f * azar(semilla, 60 + k)), (0.3f + 0.5f * azar(semilla, 70 + k)) * u,
                    Offset(im.left + im.width * azar(semilla, 80 + k), im.top + im.height * azar(semilla, 90 + k) * 0.7f))
                drawPath(luna(Offset(im.left + im.width * 0.68f, im.top + im.height * 0.3f), im.width * 0.13f), Color(0xFFE9E6DC))
                drawRect(t(Color(0xFF0E1517), oscuro, false), Offset(im.left, im.bottom - im.height * 0.22f), Size(im.width, im.height * 0.22f))
            } else {
                drawRect(Brush.verticalGradient(listOf(t(Color(0xFFB6B8B2), oscuro, false), t(Color(0xFF8E918C), oscuro, false)), im.top, im.bottom), im.topLeft, im.size)
                // El muro de ladrillo del puerto, en hiladas, y la antena.
                val muro = im.top + im.height * 0.48f
                drawRect(t(Color(0xFF6F726D), oscuro, false), Offset(im.left, muro), Size(im.width, im.bottom - muro))
                var y = muro + im.height * 0.08f
                while (y < im.bottom) { drawLine(Color.Black.copy(alpha = 0.18f), Offset(im.left, y), Offset(im.right, y), 0.4f * u); y += im.height * 0.08f }
                val ax = im.left + im.width * (0.78f + 0.12f * azar(semilla, 5))
                drawLine(t(Color(0xFF3E413E), oscuro, false), Offset(ax, muro), Offset(ax, im.top + im.height * 0.08f), maxOf(0.5f * u, im.width * 0.025f))
                drawLine(t(Color(0xFF3E413E), oscuro, false), Offset(ax - im.width * 0.08f, im.top + im.height * 0.16f), Offset(ax + im.width * 0.08f, im.top + im.height * 0.16f), maxOf(0.4f * u, im.width * 0.02f))
                // La persona, de hombros, un poco de lado.
                val cx = im.left + im.width * (0.36f + 0.18f * azar(semilla, 3))
                val sombra = t(Color(0xFF3A3D3B), oscuro, false)
                drawRoundRect(sombra, Offset(cx - im.width * 0.34f, im.top + im.height * 0.66f), Size(im.width * 0.68f, im.height * 0.5f), CornerRadius(im.width * 0.24f))
                drawRect(sombra, Offset(cx - im.width * 0.07f, im.top + im.height * 0.5f), Size(im.width * 0.14f, im.height * 0.2f))
                drawCircle(sombra, im.width * 0.16f, Offset(cx, im.top + im.height * 0.42f))
                drawCircle(Color.White.copy(alpha = 0.12f), im.width * 0.16f, Offset(cx - im.width * 0.05f, im.top + im.height * 0.39f))
            }
            for (k in 0 until 18) drawCircle((if (k % 2 == 0) Color.White else Color.Black).copy(alpha = 0.08f), 0.4f * u,
                Offset(im.left + im.width * azar(semilla, 200 + k), im.top + im.height * azar(semilla, 220 + k)))
        }
        drawRect(Color.Black.copy(alpha = 0.12f), im.topLeft, im.size, style = Stroke(0.5f * u))
    }

    /**
     * El sello de goma de la célula, entintado: el aro doble, el emblema de la red al centro (de noche, la luna creciente) y,
     * si hay letra y cabe, «K-7» abajo; la tinta es desigual, con los huecos donde la goma no tocó el [fondo].
     */
    private fun DrawScope.selloGoma(medidor: TextMeasurer?, c: Offset, r: Float, tinta: Color, fondo: Color, semilla: Int, noche: Boolean) {
        val u = density
        drawCircle(tinta, r, c, style = Stroke(maxOf(0.9f * u, r * 0.09f)))
        drawCircle(tinta.copy(alpha = tinta.alpha * 0.7f), r * 0.8f, c, style = Stroke(maxOf(0.5f * u, r * 0.035f)))
        val ce = Offset(c.x, c.y - r * (if (medidor != null && r > 10 * u) 0.14f else 0.02f))
        if (noche) drawPath(luna(ce, r * 0.36f), tinta)
        else emblema(Offset(ce.x - r * 0.06f, ce.y + r * 0.06f), r * 0.36f, tinta, maxOf(0.6f * u, r * 0.065f))
        if (medidor != null && r > 10 * u)
            textoEn(medidor, "K-7", Rect(c.x - r * 0.5f, c.y + r * 0.36f, c.x + r * 0.5f, c.y + r * 0.72f), MONO, tinta, r / u * 0.3f, minimo = 3f)
        val k = (r / (14 * u)).coerceIn(0.4f, 2f)
        for (j in 0 until 10) drawCircle(fondo.copy(alpha = 0.55f), (0.4f + 0.7f * azar(semilla, 90 + j)) * u * k,
            Offset(c.x + r * (azar(semilla, 100 + j) * 2 - 1) * 0.9f, c.y + r * (azar(semilla, 110 + j) * 2 - 1) * 0.9f))
    }

    /**
     * La cinta perforada del teletipo a lo largo de [r] (de izquierda a derecha, o de arriba abajo con [vertical]): la fila
     * de agujeritos de arrastre y, a sus lados, las cinco pistas de agujeros grandes donde va el mensaje, sacadas de
     * [semilla] (siempre las mismas).
     */
    private fun DrawScope.cintaPerforada(r: Rect, cinta: Color, hueco: Color, semilla: Int, vertical: Boolean = false) {
        val u = density
        drawRect(cinta, r.topLeft, r.size)
        val ancho = if (vertical) r.width else r.height
        val largo = if (vertical) r.height else r.width
        val paso = ancho / 6f
        fun punto(a: Float, b: Float) = if (vertical) Offset(r.left + b, r.top + a) else Offset(r.left + a, r.top + b)
        val grandes = ArrayList<Offset>(); val chicos = ArrayList<Offset>()
        var a = paso * 0.7f; var i = 0
        while (a < largo - paso * 0.4f) {
            chicos.add(punto(a, paso * 2.5f))
            val m = 1 + (azar(semilla, i) * 31f).toInt()
            for (k in 0 until 5) if ((m and (1 shl k)) != 0) grandes.add(punto(a, paso * ((if (k < 2) k else k + 1) + 0.5f)))
            a += paso; i++
        }
        drawPoints(grandes, PointMode.Points, hueco, paso * 0.62f, StrokeCap.Round)
        drawPoints(chicos, PointMode.Points, hueco, paso * 0.32f, StrokeCap.Round)
        if (vertical) drawRect(Color.Black.copy(alpha = 0.1f), Offset(r.right - 0.7f * u, r.top), Size(0.7f * u, r.height))
        else drawRect(Color.Black.copy(alpha = 0.1f), Offset(r.left, r.bottom - 0.7f * u), Size(r.width, 0.7f * u))
    }

    /** Los segmentos que se encienden en cada cifra: a (1) arriba, b (2) y c (4) a la derecha, d (8) abajo, e (16) y f (32) a la izquierda, g (64) en medio. */
    private val SEGMENTOS = intArrayOf(63, 6, 91, 79, 102, 109, 125, 7, 127, 111)

    /** Un segmento [k] de una cifra en la celda de [l], [t] con ancho [w], alto [h] y grosor [s], inclinado como en las pantallas. */
    private fun segmento(k: Int, l: Float, t: Float, w: Float, h: Float, s: Float): Path {
        val g = s * 0.2f
        val iz = l + s / 2; val de = l + w - s / 2
        val ar = t + s / 2; val me = t + h / 2; val ab = t + h - s / 2
        val inclina = w * 0.14f / h
        fun x(px: Float, py: Float) = px + (me - py) * inclina
        val p = Path()
        fun horizontal(y: Float) {
            val a = iz + g; val b = de - g
            p.moveTo(x(a, y), y); p.lineTo(x(a + s / 2, y - s / 2), y - s / 2); p.lineTo(x(b - s / 2, y - s / 2), y - s / 2)
            p.lineTo(x(b, y), y); p.lineTo(x(b - s / 2, y + s / 2), y + s / 2); p.lineTo(x(a + s / 2, y + s / 2), y + s / 2); p.close()
        }
        fun vertical(xx: Float, y1: Float, y2: Float) {
            val a = y1 + g; val b = y2 - g
            p.moveTo(x(xx, a), a); p.lineTo(x(xx + s / 2, a + s / 2), a + s / 2); p.lineTo(x(xx + s / 2, b - s / 2), b - s / 2)
            p.lineTo(x(xx, b), b); p.lineTo(x(xx - s / 2, b - s / 2), b - s / 2); p.lineTo(x(xx - s / 2, a + s / 2), a + s / 2); p.close()
        }
        when (k) {
            0 -> horizontal(ar)
            1 -> vertical(de, ar, me)
            2 -> vertical(de, me, ab)
            3 -> horizontal(ab)
            4 -> vertical(iz, me, ab)
            5 -> vertical(iz, ar, me)
            else -> horizontal(me)
        }
        return p
    }

    /**
     * Un número en la pantalla de segmentos de la consola, centrado en [r]: cada cifra con sus siete segmentos inclinados,
     * los encendidos en [luz] (con su halo si [halo]) y los apagados apenas en [apagado], el «8» fantasma de las pantallas de
     * verdad. También dibuja los dos puntos, el punto y el guion; lo demás deja su hueco.
     */
    private fun DrawScope.segmentos(texto: String, r: Rect, luz: Color, apagado: Color, halo: Boolean = false) {
        if (texto.isEmpty() || r.width <= 1f || r.height <= 1f) return
        val anchos = texto.map { if (it == ':' || it == '.') 0.42f else 1f }
        val total = anchos.sum()
        val paso = minOf(r.height * 0.6f, r.width / total)
        val alto = minOf(r.height, paso / 0.6f)
        val celda = paso * 0.76f
        val s = celda * 0.19f
        var x = r.center.x - paso * total / 2f
        val y0 = r.center.y - alto / 2f
        texto.forEachIndexed { i, ch ->
            val l = x + (paso * anchos[i] - (if (anchos[i] < 1f) s else celda)) / 2f
            when {
                ch.isDigit() -> {
                    val m = SEGMENTOS[ch - '0']
                    for (k in 0 until 7) {
                        val p = segmento(k, l, y0, celda, alto, s)
                        val enc = (m and (1 shl k)) != 0
                        if (enc && halo) drawPath(p, luz.copy(alpha = luz.alpha * 0.3f), style = Stroke(s * 0.9f, join = StrokeJoin.Round))
                        drawPath(p, if (enc) luz else apagado)
                    }
                }
                ch == '-' -> drawPath(segmento(6, l, y0, celda, alto, s), luz)
                ch == ':' -> listOf(0.32f, 0.68f).forEach { f -> drawRect(luz, Offset(l, y0 + alto * f - s / 2), Size(s, s)) }
                ch == '.' -> drawRect(luz, Offset(l, y0 + alto - s), Size(s, s))
            }
            x += paso * anchos[i]
        }
    }

    /**
     * La pantalla redonda de radar en su bisel: el vidrio verde oscuro, la retícula (dos círculos, la cruz y las marcas del
     * borde), el barrido con su estela que se apaga hacia atrás en [angulo] (grados), los ecos y el reflejo del vidrio;
     * [brillo] sube en la sala a oscuras.
     */
    private fun DrawScope.radar(c: Offset, r: Float, angulo: Float, bisel: Color, semilla: Int, brillo: Float = 1f) {
        val u = density
        if (brillo > 1f) drawCircle(Brush.radialGradient(listOf(RADAR.copy(alpha = 0.16f * (brillo - 0.6f)), RADAR.copy(alpha = 0f)), c, r * 2.6f), r * 2.6f, c)
        drawCircle(Color.Black.copy(alpha = 0.3f), r * 1.25f, Offset(c.x + 1f * u, c.y + 1.6f * u))
        drawCircle(bisel, r * 1.23f, c)
        drawCircle(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.28f)), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)), r * 1.23f, c)
        drawCircle(mezcla(bisel, Color.Black, 0.55f), r * 1.07f, c)
        drawCircle(PANTALLA, r, c)
        clipPath(Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }) {
            drawCircle(Brush.radialGradient(listOf(VERDE.copy(alpha = (0.3f * brillo).coerceAtMost(0.6f)), Color.Transparent), c, r), r, c)
            val traza = RADAR.copy(alpha = 0.3f)
            listOf(0.34f, 0.67f).forEach { f -> drawCircle(traza, r * f, c, style = Stroke(0.7f * u)) }
            drawLine(traza, Offset(c.x - r, c.y), Offset(c.x + r, c.y), 0.6f * u)
            drawLine(traza, Offset(c.x, c.y - r), Offset(c.x, c.y + r), 0.6f * u)
            for (k in 0 until 36) {
                val a = k * PI.toFloat() / 18f
                val adentro = if (k % 3 == 0) 0.88f else 0.94f
                drawLine(traza, Offset(c.x + cos(a) * r * adentro, c.y + sin(a) * r * adentro), Offset(c.x + cos(a) * r, c.y + sin(a) * r), 0.6f * u)
            }
            // La estela del barrido: capas que se suman hacia la línea que barre.
            for (k in 0 until 9) drawArc(RADAR.copy(alpha = (0.045f * brillo).coerceAtMost(0.08f)), angulo - 7f * (k + 1), 7f * (k + 1), true,
                Offset(c.x - r, c.y - r), Size(r * 2, r * 2))
            val a = Math.toRadians(angulo.toDouble())
            drawLine(RADAR.copy(alpha = 0.95f), c, Offset(c.x + cos(a).toFloat() * r, c.y + sin(a).toFloat() * r), maxOf(0.8f * u, r * 0.035f), StrokeCap.Round)
            // Los ecos: más vivos los que el barrido acaba de pasar.
            for (k in 0 until 4) {
                val g = angulo - 10f - 70f * azar(semilla, 300 + k)
                val d = r * (0.25f + 0.65f * azar(semilla, 310 + k))
                val ga = Math.toRadians(g.toDouble())
                val vivo = 1f - (angulo - g) / 90f
                val o = Offset(c.x + cos(ga).toFloat() * d, c.y + sin(ga).toFloat() * d)
                drawCircle(RADAR.copy(alpha = 0.25f * vivo), r * 0.08f, o)
                drawCircle(RADAR.copy(alpha = 0.5f + 0.45f * vivo), maxOf(0.7f * u, r * 0.035f), o)
            }
            drawArc(Color.White.copy(alpha = 0.1f), 200f, 70f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
        }
        for (k in 0 until 4) {
            val a = (45f + 90f * k) * PI.toFloat() / 180f
            tornillo(Offset(c.x + cos(a) * r * 1.15f, c.y + sin(a) * r * 1.15f), maxOf(0.8f * u, r * 0.045f), mezcla(bisel, CROMO, 0.5f))
        }
    }

    /** El borde de la consola en penumbra: el velo parejo y, desde [luz], el círculo donde se lee, entibiado con [tinte]. */
    private fun DrawScope.penumbra(luz: Offset, tinte: Color, velo: Float, orilla: Float) {
        val radio = maxOf(size.width, size.height) * 0.85f
        drawRect(Color.Black.copy(alpha = velo))
        drawRect(Brush.radialGradient(0f to Color.Transparent, 0.42f to Color.Black.copy(alpha = 0.05f), 0.76f to Color.Black.copy(alpha = orilla * 0.6f),
            1f to Color.Black.copy(alpha = orilla), center = luz, radius = radio))
        drawRect(Brush.radialGradient(listOf(tinte.copy(alpha = 0.1f), Color.Transparent), center = luz, radius = radio * 0.55f))
    }

    /** La trama de la lona: hilos finos a lo largo y a lo ancho dentro de [r]. */
    private fun DrawScope.trama(r: Rect, color: Color, paso: Float) {
        val u = density
        var x = r.left
        while (x < r.right) { drawLine(color, Offset(x, r.top), Offset(x, r.bottom), 0.4f * u); x += paso }
        var y = r.top
        while (y < r.bottom) { drawLine(color, Offset(r.left, y), Offset(r.right, y), 0.4f * u); y += paso }
    }

    // =================================================================
    //  El mueble y cada entrada (en MuebleEstanteFrente*.kt)
    // =================================================================

    override val corona = MuebleEstanteFrente.CORONA
    override val base = MuebleEstanteFrente.BASE
    override val lado = MuebleEstanteFrente.LADO
    override val altoRepisa: Dp = MuebleEstanteFrente.ALTO_REPISA
    override val tabla: Dp = MuebleEstanteFrente.TABLA
    override val separa: Dp = MuebleEstanteFrente.SEPARA
    override val letra: FontFamily = MONO
    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoBiblioteca.auraEnReposo]). */
    override val auraEnReposo = false

    override fun ancho(palabras: Int): Dp = MuebleEstanteFrente.ancho(palabras)
    override fun alto(semilla: Int, palabras: Int): Float = MuebleEstanteFrente.alto(semilla, palabras)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) =
        with(MuebleEstanteFrente) { estante(d, medidor, oscuro, noche) }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) =
        with(MuebleEstanteFrente) { volumen(v, medidor, oscuro) }

    // =================================================================
    //  La tapa: el dosier del día
    // =================================================================

    override val altoTapa: Dp = 190.dp
    override val cajaTapa = Rect(0.32f, 0.445f, 0.915f, 0.78f)
    override val letraTapa: FontFamily = TEXTO
    override val tamTapa = 12.5f
    override val lineasTapa = 3
    override val centradoTapa = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = Color(0xFF1E2024)
    override fun palabrasTapa(n: Int): String = "${Frente.contador(n)} PALABRAS EN CLARO"

    /** Las letras y cifras de los grupos cifrados (sin I ni O, que se confunden con el 1 y el 0). */
    private const val CIFRA = "ABCDEFGHJKLMNPQRSTUVWXYZ0123456789"

    /** Un renglón de [n] grupos cifrados de cinco signos, sacado de [semilla] y [linea]: siempre el mismo para el mismo día. */
    private fun grupos(semilla: Int, linea: Int, n: Int, soloCifras: Boolean = false): String = (0 until n).joinToString("  ") { g ->
        (0 until 5).map { k ->
            val x = azar(semilla + linea * 131, g * 7 + k)
            if (soloCifras) ('0' + (x * 9.99f).toInt()) else CIFRA[(x * (CIFRA.length - 0.01f)).toInt()]
        }.joinToString("")
    }

    /**
     * El dosier del día, de frente: la lona acero con su trama, el lomo doblado a la izquierda y la pestaña de atrás con el
     * número del despacho; la foto engrapada, el rótulo ámbar remachado con «DESPACHO» y la operación del día, la hoja de
     * texto en claro pegada (donde se lee lo escrito), el sello de la célula en tinta ámbar, el emblema con la célula y, al
     * pie, las palabras en la pantalla de segmentos. Lo ciñen dos esquineras elásticas. Al abrirse, las esquineras se sueltan
     * y la tapa gira sobre el lomo: debajo está la hoja de despacho cifrado, con sus grupos a máquina y su sello. De noche es
     * la escucha nocturna: la foto es de cielo, el sello lleva la luna y el radar tiñe de verde la esquina.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val hoja = papel(oscuro, e.noche)
        val tinta = tintaTapa(oscuro, e.noche)
        val lona = c(LONAS[Math.floorMod(e.semilla, LONAS.size)])
        val dentroLona = mezcla(lona, Color.Black, 0.3f)
        val cuerpo = Rect(w * 0.015f, h * 0.075f, w * 0.975f, h * 0.955f)
        val x0 = cuerpo.left + w * 0.035f
        val cara = Rect(x0, cuerpo.top, cuerpo.right, cuerpo.bottom)
        val numero = Frente.contador(Math.floorMod(e.semilla, 9000) + 1000)
        // Donde descansa: la mesa de la consola y la sombra del dosier, del color del cuarto, hacia donde no llega la luz (de día y
        // de noche la luz viene de la izquierda; en el sueño manda el verde del radar, de arriba a la derecha).
        mesaTapa(cuerpo, oscuro, e.noche)
        val hacia = if (e.noche) -1f else 1f
        val tinte = if (e.noche) NOCHE else SOMBRA_CUARTO
        drawRoundRect(tinte.copy(alpha = if (oscuro) 0.6f else 0.3f), Offset(cuerpo.left + hacia * 4 * u, cuerpo.top + 3 * u), cuerpo.size, CornerRadius(6 * u))
        drawRect(tinte.copy(alpha = if (oscuro) 0.55f else 0.32f), Offset(cuerpo.left + hacia * 7 * u, cuerpo.bottom - 1.5f * u), Size(cuerpo.width, 3 * u))
        // La pestaña de la tapa de atrás, con el número del despacho.
        val pes = Rect(w * 0.66f, h * 0.012f, w * 0.9f, cuerpo.top + 3 * u)
        drawPath(Path().apply {
            moveTo(pes.left, pes.bottom); lineTo(pes.left + 6 * u, pes.top); lineTo(pes.right - 6 * u, pes.top); lineTo(pes.right, pes.bottom); close()
        }, mezcla(lona, Color.Black, 0.12f))
        val pe = Rect(pes.left + 9 * u, pes.top + 2 * u, pes.right - 9 * u, cuerpo.top - 0.5f * u)
        drawRect(hoja, pe.topLeft, pe.size)
        textoEn(medidor, "Nº $numero", pe, MONO, tinta, 8f, minimo = 4.5f)
        // La tapa de atrás, que se ve al abrir, con la hoja de despacho cifrado.
        if (a > 0.01f) {
            drawRoundRect(dentroLona, cara.topLeft, cara.size, CornerRadius(6 * u))
            val hd = Rect(cara.left + w * 0.035f, cara.top + h * 0.06f, cara.right - w * 0.03f, cara.bottom - h * 0.05f)
            for (j in 2 downTo 1) drawRect(mezcla(hoja, Color.Black, 0.1f * j), Offset(hd.left + j * 1.4f * u, hd.top + j * 1.4f * u), hd.size)
            drawRect(hoja, hd.topLeft, hd.size)
            textoEn(medidor, if (e.noche) "ESCUCHA NOCTURNA" else "DESPACHO CIFRADO", Rect(hd.left + 8 * u, hd.top + 4 * u, hd.left + hd.width * 0.66f, hd.top + 18 * u), TITULO, tinta, 9.5f, espaciado = 1f, minimo = 5f)
            textoEn(medidor, "Nº $numero", Rect(hd.left + hd.width * 0.7f, hd.top + 4 * u, hd.right - 8 * u, hd.top + 18 * u), MONO, tinta.copy(alpha = 0.8f), 9f, minimo = 5f)
            drawLine(c(AMBAR), Offset(hd.left + 8 * u, hd.top + 20 * u), Offset(hd.right - 8 * u, hd.top + 20 * u), 1f * u)
            var ry = hd.top + 24 * u; var k = 0
            while (ry + 11 * u < hd.bottom - 6 * u) {
                aIzquierda(medidor, grupos(e.semilla, k, 6), Rect(hd.left + 8 * u, ry, hd.right - 8 * u, ry + 11 * u), MONO, tinta.copy(alpha = 0.75f), 9f, minimo = 5f)
                ry += 13 * u; k++
            }
            val so = Offset(hd.right - hd.width * 0.16f, hd.bottom - hd.height * 0.26f)
            rotate(-12f, so) { selloGoma(medidor, so, 17 * u, c(OBLEA).copy(alpha = 0.7f), hoja, e.semilla, e.noche) }
            grapa(Offset(hd.left + 4 * u, hd.top + 12 * u), Offset(hd.left + 4 * u, hd.top + 26 * u))
        }
        // La tapa de delante, que gira sobre el lomo y se ve cada vez más de canto.
        val ang = a * 1.45f
        val co = cos(ang); val si = sin(ang)
        if (a > 0.01f) drawRect(Color.Black.copy(alpha = 0.32f * si), Offset(x0 + cara.width * co, cara.top), Size(14 * u * si, cara.height))
        afin(co, -si * 0.1f, 0f, 1f, x0 - x0 * co, x0 * si * 0.1f) {
            drawRoundRect(lonaConLuz(lona, cara, oscuro, e.noche), cara.topLeft, cara.size, CornerRadius(6 * u))
            clipRect(cara.left, cara.top, cara.right, cara.bottom) {
                trama(cara, Color.White.copy(alpha = 0.045f), 1.8f * u)
                // El corte para el dedo, en el canto libre.
                drawCircle(dentroLona, 8 * u, Offset(cara.right, cara.center.y))
                drawCircle(Color.Black.copy(alpha = 0.25f), 8 * u, Offset(cara.right, cara.center.y), style = Stroke(0.8f * u))
            }
            drawRoundRect(Color.Black.copy(alpha = 0.3f), cara.topLeft, cara.size, CornerRadius(6 * u), style = Stroke(0.8f * u))
            drawLine(Color.White.copy(alpha = 0.14f), Offset(cara.left + 6 * u, cara.top + 1 * u), Offset(cara.right - 6 * u, cara.top + 1 * u), 1f * u)
            // La foto, engrapada por arriba.
            val fr = Rect(cara.left + w * 0.03f, h * 0.13f, cara.left + w * 0.03f + w * 0.19f, h * 0.6f)
            rotate(-3f, fr.center) {
                foto(fr, e.semilla, oscuro, e.noche)
                grapa(Offset(fr.left + fr.width * 0.18f, fr.top + 2.5f * u), Offset(fr.left + fr.width * 0.4f, fr.top + 2.5f * u))
                grapa(Offset(fr.right - fr.width * 0.4f, fr.top + 2.5f * u), Offset(fr.right - fr.width * 0.18f, fr.top + 2.5f * u))
            }
            // El rótulo ámbar remachado: «DESPACHO» y la operación del día.
            val ro = Rect(w * 0.3f, h * 0.1f, w * 0.935f, h * 0.335f)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), ro.topLeft + Offset(0.8f * u, 1.4f * u), ro.size, CornerRadius(3 * u))
            drawRoundRect(ambar(oscuro, e.noche), ro.topLeft, ro.size, CornerRadius(3 * u))
            drawRoundRect(Color.White.copy(alpha = 0.2f), ro.topLeft, Size(ro.width, ro.height * 0.18f), CornerRadius(3 * u))
            listOf(ro.left + 5 * u, ro.right - 5 * u).forEach { x -> tornillo(Offset(x, ro.center.y), 1.6f * u, c(CROMO)) }
            val dentro = Rect(ro.left + 11 * u, ro.top, ro.right - 11 * u, ro.bottom)
            textoEn(medidor, if (e.noche) "ESCUCHA" else "DESPACHO", Rect(dentro.left, dentro.top + 2 * u, dentro.right, dentro.top + dentro.height * 0.5f), TITULO, c(BAQUELITA), 14f, espaciado = 2.4f, minimo = 7f)
            drawLine(c(BAQUELITA).copy(alpha = 0.45f), Offset(dentro.left, dentro.top + dentro.height * 0.52f), Offset(dentro.right, dentro.top + dentro.height * 0.52f), 0.7f * u)
            enDos(medidor, e.fecha, Rect(dentro.left, dentro.top + dentro.height * 0.55f, dentro.right, dentro.bottom - 1.5f * u), MONO, c(BAQUELITA), 10f, k = 0.84f)
            // La hoja de texto en claro, pegada.
            val hj = Rect(w * 0.3f, h * 0.37f, w * 0.935f, h * 0.8f)
            drawRect(Color.Black.copy(alpha = 0.25f), hj.topLeft + Offset(0.8f * u, 1.4f * u), hj.size)
            drawRect(hoja, hj.topLeft, hj.size)
            aIzquierda(medidor, if (e.noche) "SUEÑO EN CLARO" else "TEXTO EN CLARO", Rect(w * 0.32f, hj.top + 1.5f * u, w * 0.7f, h * 0.428f), MONO, c(TENUE), 7f, espaciado = 0.8f, minimo = 4f)
            textoEn(medidor, "+", Rect(w * 0.88f, hj.top + 1.5f * u, w * 0.92f, h * 0.428f), MONO, c(AMBAR), 9f)
            drawLine(c(AMBAR).copy(alpha = 0.7f), Offset(w * 0.32f, h * 0.432f), Offset(w * 0.915f, h * 0.432f), 0.7f * u)
            // El sello de la célula, en tinta ámbar, a caballo entre la hoja y la lona.
            val so = Offset(w * 0.285f, h * 0.755f)
            rotate(-16f, so) { selloGoma(medidor, so, 15 * u, c(OBLEA).copy(alpha = 0.62f), lona, e.semilla, e.noche) }
            // Al pie: el emblema con la célula y las palabras en la pantalla de segmentos.
            val pie = h * 0.885f
            val grabado = mezcla(HUESO, lona, if (oscuro) 0.3f else 0.1f)
            emblema(Offset(cara.left + w * 0.048f, pie + 1 * u), 5.5f * u, c(PILOTO), 1.2f * u)
            aIzquierda(medidor, "CÉLULA K-7", Rect(cara.left + w * 0.085f, pie - 6 * u, w * 0.29f, pie + 6 * u), MONO, grabado, 8.5f, espaciado = 0.6f, minimo = 5f)
            val pa = Rect(w * 0.55f, h * 0.835f, w * 0.72f, h * 0.935f)
            drawRoundRect(c(BAQUELITA), pa.inflate(1.4f * u).topLeft, pa.inflate(1.4f * u).size, CornerRadius(2.5f * u))
            drawRoundRect(Color(0xFF1A1208), pa.topLeft, pa.size, CornerRadius(2 * u))
            segmentos(if (e.vacio || e.palabras <= 0) "----" else Frente.contador(e.palabras), pa.deflate(2 * u), PILOTO, PILOTO.copy(alpha = 0.08f), halo = oscuro || e.noche)
            aIzquierda(medidor, palabrasTapa(e.palabras).substringAfter(" "), Rect(w * 0.735f, pie - 6 * u, w * 0.925f, pie + 6 * u), MONO, grabado, 7f, minimo = 4f)
            // Las esquineras elásticas que lo tienen cerrado.
            if (a < 0.02f) listOf(cara.top to 1f, cara.bottom to -1f).forEach { (y, s) ->
                val p1 = Offset(cara.right - 15 * u, y); val p2 = Offset(cara.right, y + s * 15 * u)
                drawLine(Color.Black.copy(alpha = 0.3f), p1 + Offset(0f, 1.5f * u), p2 + Offset(0f, 1.5f * u), 4.6f * u)
                drawLine(c(BAQUELITA), p1, p2, 4f * u)
                drawLine(Color.White.copy(alpha = 0.12f), p1 + Offset(1f * u, s * 1f * u), p2 + Offset(-1f * u, -s * 1f * u), 0.6f * u)
            }
            cantosTapa(cara, oscuro, e.noche)
            if (a > 0f) drawRoundRect(Color.Black.copy(alpha = 0.45f * (1f - co)), cara.topLeft, cara.size, CornerRadius(6 * u))
        }
        // El lomo doblado, que no se mueve.
        val lomo = Rect(cuerpo.left, cuerpo.top, x0, cuerpo.bottom)
        drawRoundRect(mezcla(lona, Color.Black, 0.2f), lomo.topLeft, lomo.size, CornerRadius(4 * u))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(lomo.left + lomo.width * 0.3f, lomo.top + 2 * u), Size(lomo.width * 0.2f, lomo.height - 4 * u))
        drawLine(Color.Black.copy(alpha = 0.45f), Offset(x0, cuerpo.top + 2 * u), Offset(x0, cuerpo.bottom - 2 * u), 1.2f * u)
    }

    /**
     * La lona de la tapa con la luz que le llega desde arriba: de día, como siempre; de noche, tibia arriba (la lámpara del cuarto)
     * y hundida abajo en la penumbra verde; en el sueño, el verde frío del radar arriba y la noche abajo. El pie queda siempre más
     * hondo que antes, para que lo grabado abajo (la célula, las palabras) no se lea peor; el lado lo dan los cantos.
     */
    private fun lonaConLuz(lona: Color, r: Rect, oscuro: Boolean, noche: Boolean): Brush = when {
        noche -> Brush.verticalGradient(listOf(mezcla(lona, RADAR, if (oscuro) 0.14f else 0.1f), lona, mezcla(mezcla(lona, Color.Black, 0.18f), NOCHE, 0.3f)), r.top, r.bottom)
        oscuro -> Brush.verticalGradient(listOf(mezcla(lona, PILOTO, 0.1f), lona, mezcla(mezcla(lona, Color.Black, 0.18f), PENUMBRA, 0.3f)), r.top, r.bottom)
        else -> Brush.verticalGradient(listOf(mezcla(lona, Color.White, 0.08f), lona, mezcla(lona, Color.Black, 0.18f)), r.top, r.bottom)
    }

    /**
     * Los cantos de la tapa que miran a la luz: de día, el filo claro de la izquierda; de noche, el tibio a la izquierda (más vivo
     * abajo, junto al piloto) y el frío del radar a la derecha; en el sueño, el frío arriba y a la derecha.
     */
    private fun DrawScope.cantosTapa(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        val izq = Rect(r.left + 0.5f * u, r.top + 7 * u, r.left + 1.8f * u, r.bottom - 7 * u)
        val der = Rect(r.right - 1.8f * u, r.top + 7 * u, r.right - 0.5f * u, r.bottom - 7 * u)
        val arriba = Rect(r.left + 7 * u, r.top + 0.4f * u, r.right - 7 * u, r.top + 1.6f * u)
        when {
            noche -> {
                val f = if (oscuro) 0.6f else 0.42f
                drawRect(Brush.horizontalGradient(listOf(RADAR.copy(alpha = 0f), RADAR.copy(alpha = f)), arriba.left, arriba.right), arriba.topLeft, arriba.size)
                drawRect(Brush.verticalGradient(listOf(RADAR.copy(alpha = f), RADAR.copy(alpha = 0.05f)), der.top, der.bottom), der.topLeft, der.size)
            }
            oscuro -> {
                drawRect(Brush.verticalGradient(listOf(PILOTO.copy(alpha = 0.18f), PILOTO.copy(alpha = 0.55f)), izq.top, izq.bottom), izq.topLeft, izq.size)
                drawRect(Brush.verticalGradient(listOf(RADAR.copy(alpha = 0.32f), RADAR.copy(alpha = 0.06f)), der.top, der.bottom), der.topLeft, der.size)
            }
            else -> drawRect(Color.White.copy(alpha = 0.2f), izq.topLeft, izq.size)
        }
    }

    /**
     * La mesa de la consola donde descansa el dosier: la cara de arriba de chapa hueso, detrás de su pie, y el labio curvo con su
     * brillo y su línea de sombra. La luz la cruza a lo ancho: de día y de noche viene de la izquierda (de noche, el piloto ámbar
     * chico del labio es la llama: su charco tibio; lo de la derecha se hunde en la penumbra verde); en el sueño, el verde del
     * radar alumbra la derecha y el piloto arde bajo.
     */
    private fun DrawScope.mesaTapa(cuerpo: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val y0 = cuerpo.bottom - 3 * u
        val yl = cuerpo.bottom + (h - cuerpo.bottom) * 0.35f
        if (h - yl < 2 * u) return
        fun c(x: Color) = t(x, oscuro, noche)
        fun luz(x: Color): List<Color> = when {
            noche -> listOf(mezcla(c(x), NOCHE, 0.4f), c(x), mezcla(c(x), RADAR, if (oscuro) 0.18f else 0.1f))
            oscuro -> listOf(mezcla(c(x), PILOTO, 0.22f), c(x), mezcla(c(x), PENUMBRA, 0.5f))
            else -> listOf(mezcla(x, Color.White, 0.25f), x, mezcla(x, BORDE, 0.35f))
        }
        drawRect(Brush.horizontalGradient(luz(HUESO_OSC), 0f, w), Offset(0f, y0), Size(w, yl - y0))
        drawRect(Brush.horizontalGradient(luz(HUESO), 0f, w), Offset(0f, yl), Size(w, h - yl))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.55f), Offset(0f, yl + 0.8f * u), Offset(w, yl + 0.8f * u), 0.8f * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), Offset(0f, h - 1.6f * u), Size(w, 1.6f * u))
        drawLine(SOMBRA_CUARTO.copy(alpha = 0.4f), Offset(0f, yl), Offset(w, yl), 0.6f * u)
        piloto(Offset(cuerpo.left + 12 * u, (yl + h) / 2f), 1.7f * u, PILOTO, true, c(CROMO), if (noche) 0.5f else if (oscuro) 1.5f else 0.6f)
    }

    // =================================================================
    //  La página abierta y el escritor
    // =================================================================

    override val altoEncabezado: Dp = 120.dp

    private val HORA = Regex("""\d{1,2}:\d{2}""")

    /**
     * La cabecera de la hoja de despacho: la tarjeta con la cinta perforada del teletipo arriba y las cruces de la retícula en
     * las esquinas; a la izquierda, el sello de la célula entintado (de noche, con la luna); al centro, el despacho y su número
     * (la primera parte del rótulo), la operación del día en grande, la fecha a máquina, el detalle y un renglón de grupos
     * cifrados; a la derecha, el recuadro de la ventana de transmisión con su hora y el canal (si el rótulo no trae hora, la
     * pantallita del radar con la segunda parte del rótulo).
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tinta = p.tinta; val acento = p.acento
        val hoja = mezcla(mezcla(p.color, if (p.oscura) Color.Black else Color.White, if (p.oscura) 0.2f else 0.32f), Color.Black, if (oscuro && !p.oscura) 0.24f else 0f)
        val caja = Rect(1.5f * u, 1.5f * u, w - 1.5f * u, h - 1.5f * u)
        drawRoundRect(Color.Black.copy(alpha = 0.14f), Offset(caja.left + 1.5f * u, caja.top + 2.5f * u), caja.size, CornerRadius(6 * u))
        drawRoundRect(hoja, caja.topLeft, caja.size, CornerRadius(6 * u))
        drawRoundRect(tinta.copy(alpha = 0.55f), caja.topLeft, caja.size, CornerRadius(6 * u), style = Stroke(1.1f * u))
        // La cinta perforada del teletipo, pegada a lo largo de arriba.
        cintaPerforada(Rect(caja.left + 8 * u, caja.top + 5 * u, caja.right - 8 * u, caja.top + 15 * u), mezcla(hoja, tinta, 0.07f), tinta.copy(alpha = 0.45f), e.numero)
        // Las cruces de la retícula en las esquinas.
        listOf(Offset(caja.left + 7 * u, caja.top + 22 * u), Offset(caja.right - 7 * u, caja.top + 22 * u), Offset(caja.left + 7 * u, caja.bottom - 7 * u), Offset(caja.right - 7 * u, caja.bottom - 7 * u)).forEach { o ->
            drawLine(acento.copy(alpha = 0.6f), Offset(o.x - 3.5f * u, o.y), Offset(o.x + 3.5f * u, o.y), 0.8f * u)
            drawLine(acento.copy(alpha = 0.6f), Offset(o.x, o.y - 3.5f * u), Offset(o.x, o.y + 3.5f * u), 0.8f * u)
        }
        // El sello de la célula, a la izquierda.
        val so = Offset(caja.left + 40 * u, caja.top + 20 * u + (caja.height - 20 * u) / 2f)
        rotate(-9f, so) { selloGoma(medidor, so, 25 * u, acento.copy(alpha = 0.85f), hoja, e.numero, e.noche) }
        val col = caja.left + 76 * u
        drawLine(tinta.copy(alpha = 0.45f), Offset(col, caja.top + 22 * u), Offset(col, caja.bottom - 8 * u), 0.8f * u)
        drawLine(tinta.copy(alpha = 0.25f), Offset(col + 2.5f * u, caja.top + 22 * u), Offset(col + 2.5f * u, caja.bottom - 8 * u), 0.6f * u)
        // El recuadro de la derecha: la ventana de transmisión, o el radar si no hay hora.
        val partes = e.rotulo.split(" · ", limit = 2)
        val segunda = partes.getOrNull(1).orEmpty()
        val hora = HORA.find(segunda)?.value
        val rc = Rect(caja.right - 86 * u, caja.top + 21 * u, caja.right - 8 * u, caja.bottom - 9 * u)
        drawRoundRect(tinta.copy(alpha = 0.06f), rc.topLeft, rc.size, CornerRadius(6 * u))
        drawRoundRect(tinta.copy(alpha = 0.55f), rc.topLeft, rc.size, CornerRadius(6 * u), style = Stroke(0.9f * u))
        if (hora != null) {
            val nombre = segunda.replace(hora, "").trim().ifEmpty { "VENTANA" }
            textoEn(medidor, nombre, Rect(rc.left + 4 * u, rc.top + 4 * u, rc.right - 4 * u, rc.top + 16 * u), MONO, acento, 8f, espaciado = 1f, minimo = 5f)
            drawLine(acento.copy(alpha = 0.5f), Offset(rc.left + 8 * u, rc.top + 18 * u), Offset(rc.right - 8 * u, rc.top + 18 * u), 0.6f * u)
            textoEn(medidor, hora, Rect(rc.left + 3 * u, rc.top + 20 * u, rc.right - 3 * u, rc.bottom - 18 * u), MONO, tinta, 22f, peso = FontWeight.Medium, minimo = 10f)
            textoEn(medidor, "CANAL %02d".format(Math.floorMod(e.numero * 7, 99) + 1), Rect(rc.left + 4 * u, rc.bottom - 17 * u, rc.right - 4 * u, rc.bottom - 4 * u), MONO, p.tenue, 8f, espaciado = 0.6f, minimo = 5f)
        } else {
            val rr = minOf(rc.width, rc.height - 16 * u) * 0.34f
            radar(Offset(rc.center.x, rc.top + 6 * u + rr * 1.25f), rr, 300f, mezcla(hoja, tinta, 0.2f), e.numero, if (p.oscura) 1.4f else 1f)
            textoEn(medidor, segunda.ifEmpty { if (e.noche) "ESCUCHA" else "CANAL" }, Rect(rc.left + 3 * u, rc.bottom - 15 * u, rc.right - 3 * u, rc.bottom - 3 * u), MONO, acento, 7.5f, minimo = 4.5f)
        }
        // El centro: el rótulo, la operación, la fecha, el detalle y los grupos cifrados.
        val cx0 = col + 9 * u; val cx1 = rc.left - 8 * u
        aIzquierda(medidor, partes[0].uppercase(), Rect(cx0, caja.top + 20 * u, cx1, caja.top + 33 * u), MONO, acento, 9f, espaciado = 0.6f, minimo = 5f)
        val i = e.fecha.indexOf(" · ")
        val clave = if (i > 0) e.fecha.substring(0, i) else e.fecha
        val resto = if (i > 0) e.fecha.substring(i + 3) else ""
        aIzquierda(medidor, clave, Rect(cx0, caja.top + 35 * u, cx1, caja.top + 60 * u), TITULO, tinta, 15f, espaciado = 0.6f, minimo = 6f)
        if (resto.isNotEmpty()) aIzquierda(medidor, resto, Rect(cx0, caja.top + 61 * u, cx1, caja.top + 76 * u), MONO, tinta, 12f, minimo = 6f)
        var dx = cx0
        while (dx < cx1) { drawLine(tinta.copy(alpha = 0.35f), Offset(dx, caja.top + 77.5f * u), Offset(minOf(dx + 2 * u, cx1), caja.top + 77.5f * u), 0.6f * u); dx += 4 * u }
        aIzquierda(medidor, e.detalle, Rect(cx0, caja.top + 79 * u, cx1, caja.top + 94 * u), TEXTO, p.tenue, 10.5f, minimo = 6f)
        aIzquierda(medidor, grupos(e.numero, 0, 4, soloCifras = true), Rect(cx0, caja.top + 96 * u, cx1, caja.bottom - 6 * u), MONO, p.tenue.copy(alpha = 0.7f), 8f, espaciado = 0.4f, minimo = 4.5f)
    }

    /**
     * La hoja de despacho, mecanografiada: el filete ámbar del margen con su marca cada renglón y una más larga (con su cruz)
     * cada cinco; en el margen derecho, la columna de grupos de la libreta de un solo uso, tachados los que ya se gastaron; al
     * centro, el emblema de la red como marca de agua; al pie, la tira de cinta perforada. En modo oscuro, la sala con las luces
     * bajas: el piloto ámbar de la consola alumbra el centro y el radar tiñe de verde la esquina. De noche, la escucha: los
     * círculos del radar y su barrido cruzan la hoja desde abajo.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tinta = p.tinta
        // El filete del margen, con su marca en cada renglón.
        val xm = 30 * u
        drawLine(p.acento.copy(alpha = 0.32f), Offset(xm, 0f), Offset(xm, h), 0.9f * u)
        var y = 72 * u; var k = 0
        while (y < h - 50 * u) {
            val larga = k % 5 == 0
            drawLine(p.acento.copy(alpha = if (larga) 0.45f else 0.25f), Offset(xm - (if (larga) 7f else 3.5f) * u, y), Offset(xm, y), 0.8f * u)
            if (larga) {
                drawLine(tinta.copy(alpha = 0.2f), Offset(13 * u, y), Offset(19 * u, y), 0.7f * u)
                drawLine(tinta.copy(alpha = 0.2f), Offset(16 * u, y - 3 * u), Offset(16 * u, y + 3 * u), 0.7f * u)
            }
            y += 24 * u; k++
        }
        // La columna de la libreta de un solo uso, en el margen derecho.
        val x0 = w - 22 * u
        y = 78 * u; k = 0
        val gastados = ((h - 130 * u) / (24 * u) * 0.4f).toInt()
        while (y < h - 56 * u) {
            for (j in 0 until 5) {
                val gx = x0 + j * 3f * u
                val alto = (if (azar(k, j) > 0.5f) 4f else 3f) * u
                drawLine(tinta.copy(alpha = 0.14f), Offset(gx, y - alto), Offset(gx + 0.6f * u, y), 0.9f * u)
            }
            if (k < gastados) drawLine(p.acento.copy(alpha = 0.3f), Offset(x0 - 1.5f * u, y - 1.5f * u), Offset(x0 + 15 * u, y - 2.5f * u), 0.8f * u)
            y += 24 * u; k++
        }
        // El emblema de la red, como marca de agua.
        val ce = Offset(w / 2, h * 0.52f); val re = w * 0.24f
        emblema(ce, re, tinta.copy(alpha = 0.035f), 5 * u)
        drawCircle(tinta.copy(alpha = 0.025f), re * 1.3f, ce, style = Stroke(1.5f * u))
        // La tira de cinta perforada al pie.
        cintaPerforada(Rect(38 * u, h - 30 * u, w - 30 * u, h - 21 * u), mezcla(p.color, tinta, 0.05f), tinta.copy(alpha = 0.13f), 23)
        // En modo oscuro, el piloto de la consola y el verde del radar.
        if (oscuro && !p.oscura) {
            penumbra(Offset(w * 0.32f, h * 0.28f), PILOTO, velo = 0.2f, orilla = 0.6f)
            mancha(Offset(w, h), w * 0.8f, RADAR.copy(alpha = 0.06f))
        }
        // De noche, los círculos del radar y su barrido, desde la esquina de abajo.
        if (noche) {
            val o = Offset(-w * 0.05f, h * 1.02f)
            val traza = p.detalle
            listOf(0.4f, 0.75f, 1.1f, 1.45f).forEach { f -> drawCircle(traza.copy(alpha = 0.07f), w * f, o, style = Stroke(0.9f * u)) }
            for (j in 0 until 8) drawArc(traza.copy(alpha = 0.006f), -60f - 5f * (j + 1), 5f * (j + 1), true, Offset(o.x - w * 1.5f, o.y - w * 1.5f), Size(w * 3f, w * 3f))
            val a = Math.toRadians(-60.0)
            drawLine(traza.copy(alpha = 0.12f), o, Offset(o.x + cos(a).toFloat() * w * 1.5f, o.y + sin(a).toFloat() * w * 1.5f), 1f * u)
            for (j in 0 until 5) {
                val e = Offset(w * (0.15f + 0.7f * azar(91, j)), h * (0.3f + 0.6f * azar(93, j)))
                drawLine(traza.copy(alpha = 0.12f), Offset(e.x - 3 * u, e.y), Offset(e.x + 3 * u, e.y), 0.8f * u)
                drawLine(traza.copy(alpha = 0.12f), Offset(e.x, e.y - 3 * u), Offset(e.x, e.y + 3 * u), 0.8f * u)
            }
        }
    }

    /** La barra de arriba es el canto de la consola: el labio redondo color hueso, la tira de luz ámbar y sus pilotos. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(if (p.oscura) mezcla(p.color, Color.Black, 0.28f) else mezcla(mezcla(p.color, HUESO_OSC, 0.3f), Color.Black, if (oscuro) 0.3f else 0f))
        val labio = Rect(0f, h - 7 * u, w, h)
        drawRect(Brush.verticalGradient(listOf(t(PANEL, oscuro, noche), t(HUESO_OSC, oscuro, noche), t(BORDE, oscuro, noche)), labio.top, labio.bottom), labio.topLeft, labio.size)
        drawLine(Color.White.copy(alpha = if (oscuro || noche) 0.12f else 0.4f), Offset(0f, labio.top + 0.8f * u), Offset(w, labio.top + 0.8f * u), 0.8f * u)
        drawLine(p.acento.copy(alpha = 0.7f), Offset(0f, labio.top - 1.2f * u), Offset(w, labio.top - 1.2f * u), 1f * u)
        var x = 18 * u; var k = 0
        while (x < w - 6 * u) {
            piloto(Offset(x, labio.center.y), 1.3f * u, if (k % 4 == 3) RADAR else PILOTO, k % 3 != 2, t(CROMO, oscuro, noche), if (oscuro || noche) 1.2f else 0.6f)
            x += 64 * u; k++
        }
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(0f, h - 1 * u), Size(w, 1 * u))
    }

    /** El botón de transmitir: la tecla iluminada de la consola, con su bisel de cromo y la lente ámbar encendida. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(2 * u, 2 * u, w - 3 * u, h - 3.5f * u)
        val rad = minOf(r.height / 2f, 13 * u)
        if (oscuro || noche) drawRoundRect(PILOTO.copy(alpha = 0.22f), r.inflate(3 * u).topLeft, r.inflate(3 * u).size, CornerRadius(rad + 3 * u))
        drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(1.2f * u, 2.2f * u), r.size, CornerRadius(rad))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(CROMO, Color.White, 0.35f), CROMO, mezcla(CROMO, Color.Black, 0.35f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(rad))
        val lente = r.deflate(2.4f * u)
        val rl = CornerRadius((rad - 2.4f * u).coerceAtLeast(0f))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(PILOTO, Color.White, 0.35f), PILOTO, mezcla(PILOTO, AMBAR, 0.55f)), lente.top, lente.bottom), lente.topLeft, lente.size, rl)
        var y = lente.top + 2 * u
        while (y < lente.bottom - 1 * u) { drawLine(Color.White.copy(alpha = 0.08f), Offset(lente.left + 4 * u, y), Offset(lente.right - 4 * u, y), 0.5f * u); y += 2.2f * u }
        drawRoundRect(Color.White.copy(alpha = 0.32f), Offset(lente.left + 3 * u, lente.top + 1.2f * u), Size(lente.width - 6 * u, lente.height * 0.28f), rl)
        drawRoundRect(AMBAR.copy(alpha = 0.6f), lente.topLeft, lente.size, rl, style = Stroke(0.8f * u))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFF2A1600)

    // =================================================================
    //  La libreta de momentos: el cartucho de datos en la grabadora
    // =================================================================

    override val altoLibreta: Dp = 268.dp

    /**
     * El puesto de grabación de la célula, en la misma sala del Ideario y del estante: el cartucho de datos de pie en la ranura
     * de la grabadora, sobre el pupitre de chapa hueso, con el flexo y el osciloscopio ([MuebleLibretaFrente]).
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) =
        with(MuebleLibretaFrente) { libreta(e, medidor, oscuro) }

    /** Cada día, un cartucho chico de aluminio con la pestaña del color del ánimo ([IndiceLibretaFrente]). */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) =
        with(IndiceLibretaFrente) { pestana(e, medidor, oscuro) }

    /** El rótulo de un ciclo: la tira de la consola con sus dos pilotos y la placa de baquelita grabada entre dos cruces de la retícula. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val tira = Rect(0f, 10 * u, w, h - 10 * u)
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(tira.left, tira.top + 2 * u), tira.size)
        drawRect(Brush.verticalGradient(listOf(c(PANEL), c(HUESO), c(BORDE)), tira.top, tira.bottom), tira.topLeft, tira.size)
        drawRect(c(CROMO), tira.topLeft, Size(w, 1 * u))
        listOf(9 * u, w - 9 * u).forEach { x -> piloto(Offset(x, h / 2), 2f * u, PILOTO, true, c(CROMO), if (oscuro) 1.2f else 0.7f) }
        val txt = texto.uppercase()
        val tam = 10.5f
        val largo = (anchoTx(medidor, txt, MONO, tam, 1.4f) + 30 * u).coerceAtMost(w - 64 * u)
        val pl = Rect(w / 2 - largo / 2, h / 2 - 10 * u, w / 2 + largo / 2, h / 2 + 10 * u)
        listOf(pl.left - 12 * u, pl.right + 12 * u).forEach { x ->
            drawLine(c(AMBAR), Offset(x - 4 * u, h / 2), Offset(x + 4 * u, h / 2), 1f * u)
            drawLine(c(AMBAR), Offset(x, h / 2 - 4 * u), Offset(x, h / 2 + 4 * u), 1f * u)
        }
        drawRoundRect(Color.Black.copy(alpha = 0.3f), pl.topLeft + Offset(0.8f * u, 1.4f * u), pl.size, CornerRadius(4 * u))
        drawRoundRect(c(CROMO), pl.inflate(1.2f * u).topLeft, pl.inflate(1.2f * u).size, CornerRadius(5 * u))
        drawRoundRect(c(BAQUELITA), pl.topLeft, pl.size, CornerRadius(4 * u))
        drawRoundRect(Color.White.copy(alpha = 0.07f), pl.topLeft, Size(pl.width, pl.height * 0.4f), CornerRadius(4 * u))
        listOf(pl.left + 7 * u, pl.right - 7 * u).forEach { x -> tornillo(Offset(x, h / 2), 1.8f * u, c(CROMO)) }
        val dentro = Rect(pl.left + 13 * u, pl.top, pl.right - 13 * u, pl.bottom)
        textoEn(medidor, txt, dentro.translate(0f, 0.7f * u), MONO, Color.Black.copy(alpha = 0.6f), tam, espaciado = 1.4f, minimo = 6f)
        textoEn(medidor, txt, dentro, MONO, mezcla(HUESO, PILOTO, if (oscuro) 0.25f else 0f), tam, espaciado = 1.4f, minimo = 6f)
    }

    // =================================================================
    //  Las cartas: transmisiones cifradas
    // =================================================================

    /** Media pieza de una oblea que se parte por una línea quebrada a la altura [y]: la de arriba ([arriba]) o la de abajo. */
    private fun mitad(r: Rect, y: Float, arriba: Boolean, semilla: Int): Path = Path().apply {
        val dientes = 6
        val amp = r.height * 0.1f
        moveTo(r.left - 2f, if (arriba) r.top - 2f else r.bottom + 2f)
        lineTo(r.left - 2f, y)
        for (i in 1..dientes) {
            val x = r.left + r.width * i / dientes
            lineTo(x - r.width / dientes * 0.5f, y + (if (i % 2 == 0) amp else -amp) * (0.5f + 0.9f * azar(semilla, i)))
            lineTo(x + if (i == dientes) 2f else 0f, y)
        }
        lineTo(r.right + 2f, if (arriba) r.top - 2f else r.bottom + 2f)
        close()
    }

    /** La solapa que gira sobre su bisagra horizontal en [bisagra]: con [abrir] en 0 cuelga, en 1 apunta arriba y se ve por dentro. */
    private fun DrawScope.solapa(bisagra: Float, abrir: Float, bloque: DrawScope.(Boolean) -> Unit) {
        val k = cos(PI * abrir.coerceIn(0f, 1f)).toFloat()
        val esc = if (abs(k) < 0.02f) 0.02f * (if (k < 0f) -1f else 1f) else k
        withTransform({ scale(1f, esc, Offset(size.width / 2f, bisagra)) }) { bloque(k < 0f) }
    }

    /**
     * La funda de la transmisión: cartón hueso de consola forrado de acero, con el filo de acero remachado a la izquierda. La
     * solapa recta, de esquinas redondas, lleva impreso «TRANSMISIÓN CIFRADA» con su filete ámbar; en el cuerpo, la etiqueta a
     * máquina pegada (a quién va y las fechas) con su casilla de «DESTINO», la tira de cinta perforada del teletipo y el número de la
     * transmisión. El cierre es la correa de baquelita que baja por el canto derecho hasta su hebilla de cromo (al atarse, se
     * va ciñendo; suelta, cuelga de la solapa). El sello es la oblea ámbar de la célula, a caballo sobre el borde de la
     * solapa: brilla cuando la carta ya se puede abrir y se parte al abrirla. Dentro va la hoja del teletipo, con su cinta.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val cu = e.cuerpo
        val s = Rect(cu.left + 3 * u, cu.top + 2 * u, cu.right - 3 * u, cu.bottom - 4 * u)
        val w = s.width; val h = s.height
        val k = (w / (164f * u)).coerceIn(0.8f, 2.2f)
        val bis = s.top
        val altoSol = h * 0.3f
        val borde = bis + altoSol
        val ab = e.abrir.coerceIn(0f, 1f)
        val cerrada = ab < 0.5f
        val tinta = PIZARRA
        val etiqueta = papel(oscuro, false)
        val rad = 8 * u * k
        // La oblea de la célula, a caballo sobre el borde de la solapa.
        val ro = minOf(w, h) * 0.13f
        val pc = Offset(s.left + w * 0.5f, borde)
        val pr = Rect(pc.x - ro * 1.1f, pc.y - ro * 1.1f, pc.x + ro * 1.1f, pc.y + ro * 1.1f)
        fun DrawScope.oblea(alfa: Float) {
            val col = mezcla(c(OBLEA), Color(0xFFFFC266), e.brillo * 0.5f)
            selloCarta(TemaId.FRENTE, pc, ro, col.copy(alpha = alfa.coerceIn(0f, 1f)), ESQUEMA)
        }
        // La correa de baquelita: baja por el canto derecho, de la solapa hasta la hebilla.
        val xc = s.left + w * 0.83f
        val anchoC = 7 * u * k
        val hebilla = Offset(xc, borde + h * 0.16f)

        sombraSuave(s, rad, 6 * u, 0.3f)
        // ---- El forro de acero (lo que se ve por la boca abierta).
        drawRoundRect(c(ACERO), s.topLeft, s.size, CornerRadius(rad))
        // ---- La solapa: por fuera el cartón impreso; por dentro, el acero del forro.
        fun DrawScope.tapaSolapa(adentro: Boolean) {
            val p = Path().apply {
                moveTo(s.left, bis); lineTo(s.right, bis); lineTo(s.right, borde - rad); quadraticTo(s.right, borde, s.right - rad, borde)
                lineTo(s.left + rad, borde); quadraticTo(s.left, borde, s.left, borde - rad); close()
            }
            if (adentro) {
                drawPath(p, c(mezcla(ACERO, Color.Black, 0.15f)))
                drawLine(Color.White.copy(alpha = 0.1f), Offset(s.left + w * 0.06f, borde - h * 0.05f), Offset(s.right - w * 0.06f, borde - h * 0.05f), 0.8f * u)
                return
            }
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(s.left + rad, borde + 1.2f * u), Offset(s.right - rad, borde + 1.2f * u), 1.4f * u)
            drawPath(p, Brush.verticalGradient(listOf(c(Color(0xFFE6E0D2)), c(CARTON)), bis, borde))
            drawPath(p, c(Color(0xFF9D9584)), style = Stroke(0.8f * u))
            textoEn(medidor, "TRANSMISIÓN CIFRADA", Rect(s.left + w * 0.08f, bis + altoSol * 0.14f, s.left + w * 0.72f, bis + altoSol * 0.5f), TITULO, c(tinta), 7.5f * k, espaciado = 0.8f, minimo = 4.5f)
            drawLine(c(AMBAR), Offset(s.left + w * 0.08f, bis + altoSol * 0.56f), Offset(s.left + w * 0.72f, bis + altoSol * 0.56f), 1f * u)
            // La correa suelta, colgando de su remache en la solapa.
            if (e.cordon <= 0.001f) {
                drawCircle(c(CROMO), 2 * u * k, Offset(xc, bis + altoSol * 0.3f))
                rotate(8f, Offset(xc, bis + altoSol * 0.3f)) {
                    drawRoundRect(c(BAQUELITA), Offset(xc - anchoC / 2, bis + altoSol * 0.3f), Size(anchoC, altoSol * 1.05f), CornerRadius(anchoC / 2))
                    drawRect(Color.White.copy(alpha = 0.12f), Offset(xc - anchoC / 2 + 1 * u, bis + altoSol * 0.34f), Size(0.8f * u, altoSol * 0.9f))
                    for (j in 0 until 3) drawCircle(Color.Black.copy(alpha = 0.6f), 0.9f * u * k, Offset(xc, bis + altoSol * (0.95f + 0.12f * j)))
                }
                drawCircle(c(CROMO), 2 * u * k, Offset(xc, bis + altoSol * 0.3f), style = Stroke(0.6f * u))
            }
            if (e.romper > 0f) withTransform({ translate(-1f * u * e.romper, -2.5f * u * e.romper); rotate(-6f * e.romper, pc) }) {
                clipPath(mitad(pr, pc.y, true, e.semilla)) { oblea(1f) }
            }
        }
        if (!cerrada) solapa(bis, ab) { tapaSolapa(it) }

        // ---- La hoja del teletipo que sale.
        if (e.subir > 0f) {
            val arriba0 = s.top + h * 0.05f
            val alza = minOf(e.subir.coerceIn(0f, 1f) * h * 0.9f, (arriba0 - 4 * u).coerceAtLeast(0f))
            val hj = Rect(s.left + w * 0.07f, arriba0 - alza, s.right - w * 0.07f, arriba0 - alza + h * 0.9f)
            drawRect(Color.Black.copy(alpha = 0.22f), hj.topLeft + Offset(1 * u, 1.5f * u), hj.size)
            val papelCarta = if (oscuro) mezcla(e.hoja, Color.Black, 0.12f) else e.hoja
            drawRect(papelCarta, hj.topLeft, hj.size)
            cintaPerforada(Rect(hj.left, hj.top, hj.right, hj.top + 6 * u * k), mezcla(papelCarta, tinta, 0.08f), e.renglones.copy(alpha = 0.5f), e.semilla)
            var y = hj.top + 6 * u * k + 11 * u
            while (y < hj.bottom - 3 * u) { drawLine(e.renglones, Offset(hj.left + 5 * u, y), Offset(hj.right - 5 * u, y), 0.7f * u); y += 11 * u }
            drawLine(c(AMBAR).copy(alpha = 0.45f), Offset(hj.left + 3.5f * u, hj.top + 6 * u * k), Offset(hj.left + 3.5f * u, hj.bottom), 0.7f * u)
        }

        // ---- El cuerpo: el cartón hueso, el filo de acero remachado, la etiqueta, la cinta y el número.
        drawRoundRect(Brush.verticalGradient(listOf(c(CARTON), c(mezcla(CARTON, Color(0xFF9D9584), 0.25f))), s.top, s.bottom), s.topLeft, s.size, CornerRadius(rad))
        for (i in 0 until 16) {
            val y = s.top + h * azar(e.semilla, i)
            drawLine(c(Color(0xFF9D9584)).copy(alpha = 0.35f), Offset(s.left + w * azar(e.semilla, i + 20), y), Offset(s.left + w * azar(e.semilla, i + 20) + 12 * u, y), 0.5f * u)
        }
        drawRoundRect(c(Color(0xFF9D9584)), s.topLeft, s.size, CornerRadius(rad), style = Stroke(0.8f * u))
        if (!cerrada) drawRect(Color.Black.copy(alpha = 0.35f), Offset(s.left + rad, s.top), Size(w - 2 * rad, 1.6f * u))
        val filo = Rect(s.left, s.top + rad, s.left + w * 0.06f, s.bottom - rad)
        drawRect(c(ACERO), filo.topLeft, filo.size)
        drawRect(Color.White.copy(alpha = 0.12f), filo.topLeft, Size(1 * u, filo.height))
        for (i in 0 until 4) tornillo(Offset(filo.center.x, s.top + h * (0.42f + 0.15f * i)), 1.4f * u * k, c(CROMO))
        // La etiqueta mecanografiada con a quién va y las fechas.
        val et = Rect(s.left + w * 0.11f, s.top + h * 0.47f, s.left + w * 0.76f, s.top + h * 0.8f)
        rotate(-0.7f, et.center) {
            drawRect(Color.Black.copy(alpha = 0.2f), et.topLeft + Offset(0.6f * u, 1f * u), et.size)
            drawRect(etiqueta, et.topLeft, et.size)
            textoEn(medidor, "DESTINO", Rect(et.left + 2 * u, et.top + 1 * u, et.left + 30 * u * k, et.top + et.height * 0.3f), MONO, c(AMBAR), 6f * k, espaciado = 0.6f, minimo = 3.5f)
            drawLine(tinta.copy(alpha = 0.18f), Offset(et.left + 3 * u, et.top + et.height * 0.64f), Offset(et.right - 3 * u, et.top + et.height * 0.64f), 0.6f * u)
            textoEn(medidor, e.linea1, Rect(et.left + 3 * u, et.top + et.height * 0.22f, et.right - 3 * u, et.top + et.height * 0.62f), MONO, tinta, 11f, peso = FontWeight.Medium, minimo = 6.5f)
            textoEn(medidor, e.linea2, Rect(et.left + 3 * u, et.top + et.height * 0.64f, et.right - 3 * u, et.bottom - 1 * u), TEXTO, tinta.copy(alpha = 0.85f), 9f, minimo = 5.5f)
        }
        cintaPerforada(Rect(s.left + w * 0.11f, s.bottom - h * 0.13f, s.left + w * 0.62f, s.bottom - h * 0.05f), c(Color(0xFFE6E0D2)), tinta.copy(alpha = 0.5f), e.semilla + 3)
        textoEn(medidor, "Nº " + Frente.contador(Math.floorMod(e.semilla, 9000) + 1000), Rect(s.left + w * 0.62f, s.bottom - h * 0.15f, s.left + w * 0.78f, s.bottom - h * 0.03f), MONO, tinta.copy(alpha = 0.7f), 7f * k, minimo = 4f)

        // ---- La solapa cerrada, encima.
        if (cerrada) solapa(bis, ab) { tapaSolapa(it) }
        // ---- La correa ceñida: baja desde la solapa hasta la hebilla, a medida que se ata.
        if (e.cordon > 0f && cerrada) {
            val y0 = bis + altoSol * 0.3f
            val fin = s.bottom - 1 * u
            val y1 = y0 + (fin - y0) * e.cordon.coerceIn(0f, 1f)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(xc - anchoC / 2 + 1 * u, y0), Size(anchoC, y1 - y0))
            drawRect(c(BAQUELITA), Offset(xc - anchoC / 2, y0), Size(anchoC, y1 - y0))
            drawRect(Color.White.copy(alpha = 0.12f), Offset(xc - anchoC / 2 + 1 * u, y0), Size(0.8f * u, y1 - y0))
            drawCircle(c(CROMO), 2 * u * k, Offset(xc, y0))
            if (y1 > hebilla.y + 3 * u) {
                val hb = Rect(xc - anchoC * 0.85f, hebilla.y - 4 * u * k, xc + anchoC * 0.85f, hebilla.y + 4 * u * k)
                drawRoundRect(c(CROMO), hb.topLeft, hb.size, CornerRadius(1.5f * u), style = Stroke(1.4f * u))
                drawLine(c(CROMO), Offset(xc, hb.top), Offset(xc, hb.bottom), 1f * u)
            }
        }
        // ---- La oblea: entera (y, al sellar, cayendo desde grande), o partida por el borde de la solapa.
        if (e.romper <= 0f) {
            if (e.brillo > 0f) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB44A).copy(alpha = 0.6f * e.brillo), Color.Transparent), center = pc, radius = ro * 2.4f), ro * 2.4f, pc)
            if (e.alfaSello > 0f) withTransform({ scale(e.escalaSello, e.escalaSello, pc) }) { oblea(e.alfaSello) }
        } else {
            withTransform({ translate(1f * u * e.romper, 3f * u * e.romper); rotate(-4f + 8f * e.romper, pc) }) {
                clipPath(mitad(pr, pc.y, false, e.semilla)) { oblea(1f) }
            }
        }
    }

    // La hoja queda arriba, entre su cinta perforada y la boca de la funda.
    override val cajaPrevia = Rect(0.13f, 0.17f, 0.87f, 0.32f)

    override val altoBuzon: Dp = 210.dp

    /**
     * La estación de recepción de la célula, en la pared de paneles perforados: arriba, el teletipo receptor en su caja curva
     * color hueso, con la cinta que le sale y cuelga, su placa grabada y el piloto «CANAL ABIERTO», que se enciende cuando hay
     * transmisiones listas; a un lado la rejilla del altavoz, al otro el cable que sube a la antena con su piloto rojo. Debajo,
     * los tres casilleros de chapa: «EN CLARO» (las listas, con la oblea que brilla), «EN ESPERA» (las cifradas, con la correa)
     * y «ARCHIVADAS» (las abiertas, con la hoja que asoma); cada uno con su contador de segmentos y su piloto. Caben ocho
     * fundas por casillero; si hay más, se dice cuántas más. Abajo, el labio de la consola.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF07090A), 0.45f) else c(x)
        val hoja = papel(oscuro, false)
        // ---- La pared perforada.
        drawRect(f(HUESO_OSC))
        val agujeros = ArrayList<Offset>()
        var py = 5 * u; var fila = 0
        while (py < h) {
            var px = if (fila % 2 == 0) 4 * u else 8 * u
            while (px < w) { agujeros.add(Offset(px, py)); px += 8 * u }
            py += 7 * u; fila++
        }
        drawPoints(agujeros, PointMode.Points, f(BORDE).copy(alpha = 0.45f), 1.5f * u, StrokeCap.Round)
        // ---- El teletipo receptor, con su cinta.
        val caja = Rect(w * 0.3f, 6 * u, w * 0.7f, 56 * u)
        val listas = e.listas > 0
        drawRoundRect(Color.Black.copy(alpha = 0.3f), caja.topLeft + Offset(2 * u, 3 * u), caja.size, CornerRadius(13 * u))
        drawRoundRect(Brush.verticalGradient(listOf(c(PANEL), c(HUESO), c(HUESO_OSC)), caja.top, caja.bottom), caja.topLeft, caja.size, CornerRadius(13 * u))
        drawRoundRect(c(BORDE), caja.topLeft, caja.size, CornerRadius(13 * u), style = Stroke(1f * u))
        val ranura = Rect(caja.left + caja.width * 0.12f, caja.top + 7 * u, caja.right - caja.width * 0.12f, caja.top + 10 * u)
        drawRoundRect(c(BAQUELITA), ranura.topLeft, ranura.size, CornerRadius(1.5f * u))
        // La cinta que sale por la ranura y cuelga por delante, hacia la izquierda.
        val sale = Offset(caja.left + caja.width * 0.14f, ranura.center.y)
        rotate(118f, sale) {
            val tira = Rect(sale.x, sale.y - 3 * u, sale.x + 48 * u, sale.y + 3 * u)
            cintaPerforada(tira, hoja, c(BAQUELITA).copy(alpha = 0.6f), 51)
            drawRect(Color.Black.copy(alpha = 0.25f), tira.topLeft, tira.size, style = Stroke(0.5f * u))
        }
        val placa = Rect(caja.left + caja.width * 0.1f, caja.top + 16 * u, caja.right - 56 * u, caja.bottom - 9 * u)
        drawRoundRect(c(BAQUELITA), placa.topLeft, placa.size, CornerRadius(3 * u))
        textoEn(medidor, "RECEPCIÓN", Rect(placa.left + 3 * u, placa.top + 1 * u, placa.right - 3 * u, placa.center.y + 1 * u), TITULO, c(HUESO), 8f, espaciado = 1f, minimo = 4.5f)
        textoEn(medidor, "CÉLULA K-7", Rect(placa.left + 3 * u, placa.center.y, placa.right - 3 * u, placa.bottom - 1 * u), MONO, c(HUESO).copy(alpha = 0.8f), 6.5f, minimo = 4f)
        val pa = Offset(caja.right - 30 * u, placa.top + 6 * u)
        piloto(pa, 3.2f * u, PILOTO, listas, c(CROMO), if (oscuro) 1.6f else 1.1f)
        textoEn(medidor, "CANAL ABIERTO", Rect(pa.x - 22 * u, pa.y + 6 * u, pa.x + 22 * u, pa.y + 14 * u), MONO, c(BAQUELITA).copy(alpha = 0.8f), 5.5f, minimo = 3.5f)
        rejilla(Rect(w * 0.08f, 14 * u, w * 0.24f, 48 * u), c(HUESO), c(BAQUELITA).copy(alpha = 0.7f))
        // El cable que sube a la antena, con su piloto rojo.
        val ax = w * 0.86f
        drawLine(f(BAQUELITA), Offset(caja.right, caja.center.y), Offset(ax, caja.center.y), 2 * u)
        drawLine(f(BAQUELITA), Offset(ax, caja.center.y), Offset(ax, 0f), 2 * u)
        drawRect(c(CROMO), Offset(ax - 4 * u, caja.center.y - 14 * u), Size(8 * u, 6 * u))
        piloto(Offset(ax + 12 * u, caja.center.y - 11 * u), 2.2f * u, PILOTO_ROJO, true, c(CROMO), if (oscuro) 1.4f else 0.9f)

        // ---- Los tres casilleros.
        val baseY = h - 14 * u
        val grupos = listOf(Triple("EN CLARO", e.listas, 0), Triple("EN ESPERA", e.selladas, 1), Triple("ARCHIVADAS", e.abiertas, 2))
        val anchoB = (w - 32 * u) / 3f
        grupos.forEach { (nombre, cuantas, tipo) ->
            val bx = 12 * u + tipo * (anchoB + 4 * u)
            // El contador de segmentos y la placa con el nombre.
            val ve = Rect(bx + anchoB * 0.18f, 64 * u, bx + anchoB * 0.82f, 80 * u)
            drawRoundRect(c(BAQUELITA), ve.inflate(1.6f * u).topLeft, ve.inflate(1.6f * u).size, CornerRadius(3 * u))
            drawRoundRect(Color(0xFF1A1208), ve.topLeft, ve.size, CornerRadius(2 * u))
            segmentos(Frente.contador(cuantas), ve.deflate(2 * u), if (tipo == 0 && cuantas > 0) PILOTO else PILOTO.copy(alpha = 0.7f), PILOTO.copy(alpha = 0.07f), halo = oscuro || (tipo == 0 && cuantas > 0))
            piloto(Offset(bx + anchoB * 0.08f, ve.center.y), 2.2f * u, if (tipo == 1) PILOTO_ROJO else PILOTO, cuantas > 0, c(CROMO), if (oscuro) 1.3f else 0.8f)
            textoEn(medidor, nombre, Rect(bx, 84 * u, bx + anchoB, 94 * u), MONO, f(PIZARRA), 7.5f, espaciado = 0.8f, minimo = 4.5f)
            // El casillero: el marco de chapa, el fondo en sombra y las fundas apiladas.
            val b = Rect(bx, 98 * u, bx + anchoB, baseY)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), b.topLeft + Offset(1.5f * u, 2 * u), b.size, CornerRadius(6 * u))
            drawRoundRect(Brush.verticalGradient(listOf(c(PANEL), c(HUESO_OSC)), b.top, b.bottom), b.topLeft, b.size, CornerRadius(6 * u))
            val hueco = b.deflate(4 * u)
            drawRoundRect(c(Color(0xFF26292C)), hueco.topLeft, hueco.size, CornerRadius(4 * u))
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent), hueco.top, hueco.top + 12 * u), hueco.topLeft, Size(hueco.width, 12 * u))
            val vis = cuantas.coerceAtMost(8)
            for (j in 0 until vis) {
                val gy = hueco.bottom - 9 * u - j * 7.5f * u
                val gx = hueco.left + 4 * u + (azar(tipo * 13 + 5, j) - 0.5f) * 4 * u
                val gw = hueco.width - 8 * u
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(gx + 1 * u, gy + 1.5f * u), Size(gw, 6 * u))
                drawRect(c(mezcla(CARTON, Color(0xFF9D9584), 0.25f * (j % 3))), Offset(gx, gy), Size(gw, 6 * u))
                drawRect(c(ACERO), Offset(gx, gy), Size(gw * 0.06f, 6 * u))
                when (tipo) {
                    0 -> {
                        drawCircle(Color(0xFFFFB44A).copy(alpha = 0.35f), 5 * u, Offset(gx + gw * 0.5f, gy + 3 * u))
                        drawCircle(OBLEA, 2.8f * u, Offset(gx + gw * 0.5f, gy + 3 * u))
                        drawCircle(Color.White.copy(alpha = 0.4f), 0.9f * u, Offset(gx + gw * 0.5f - 0.8f * u, gy + 2.2f * u))
                    }
                    1 -> {
                        drawRect(c(BAQUELITA), Offset(gx + gw * 0.8f, gy - 0.5f * u), Size(4 * u, 7 * u))
                        drawCircle(c(mezcla(OBLEA, Color.Black, 0.3f)), 2.4f * u, Offset(gx + gw * 0.5f, gy + 3 * u))
                    }
                    else -> {
                        drawRect(hoja, Offset(gx + gw * (0.12f + 0.1f * azar(31, j)), gy - 3.5f * u), Size(gw * 0.55f, 4 * u))
                        drawLine(PIZARRA.copy(alpha = 0.3f), Offset(gx + gw * 0.18f, gy - 2 * u), Offset(gx + gw * 0.5f, gy - 2 * u), 0.6f * u)
                    }
                }
            }
            if (cuantas > 8) textoEn(medidor, "+${cuantas - 8}", Rect(hueco.left, hueco.top + 2 * u, hueco.right, hueco.top + 14 * u), MONO, PILOTO, 10f, peso = FontWeight.Medium)
            if (cuantas == 0) emblema(hueco.center, minOf(hueco.width, hueco.height) * 0.18f, c(HUESO).copy(alpha = 0.18f), 1.3f * u)
        }
        // ---- El labio de la consola.
        drawRect(Brush.verticalGradient(listOf(c(PANEL), c(HUESO), c(BORDE)), baseY + 2 * u, h), Offset(0f, baseY + 2 * u), Size(w, h - baseY - 2 * u))
        drawRect(c(CROMO), Offset(0f, baseY + 2 * u), Size(w, 1.2f * u))
    }

    // =================================================================
    //  Lo de alrededor: el dial, el buscador, las obras y los recuerdos
    // =================================================================

    override val altoPestanas: Dp = 76.dp

    /**
     * El emblema de cada biblioteca, como trazo dentro de [r]: 0, el diario (el dosier con su pestaña y la foto); 1, los
     * momentos (el cartucho con su muesca y su etiqueta); 2, los sueños (la luna); 3, el ideario (la chispa, sola: la célula y sus tres trazos); 4, las cartas
     * (la antena con sus ondas).
     */
    private fun iconoBiblioteca(k: Int, r: Rect): Path = Path().apply {
        val w = r.width; val h = r.height; val x = r.left; val y = r.top
        when (k) {
            0 -> {
                moveTo(x + w * 0.06f, y + h * 0.2f); lineTo(x + w * 0.4f, y + h * 0.2f); lineTo(x + w * 0.48f, y + h * 0.08f); lineTo(x + w * 0.94f, y + h * 0.08f)
                lineTo(x + w * 0.94f, y + h * 0.94f); lineTo(x + w * 0.06f, y + h * 0.94f); close()
                addRect(Rect(x + w * 0.18f, y + h * 0.36f, x + w * 0.46f, y + h * 0.7f))
                moveTo(x + w * 0.56f, y + h * 0.46f); lineTo(x + w * 0.82f, y + h * 0.46f)
                moveTo(x + w * 0.56f, y + h * 0.62f); lineTo(x + w * 0.78f, y + h * 0.62f)
            }
            1 -> {
                moveTo(x + w * 0.06f, y + h * 0.12f); lineTo(x + w * 0.38f, y + h * 0.12f); lineTo(x + w * 0.38f, y + h * 0.24f); lineTo(x + w * 0.62f, y + h * 0.24f)
                lineTo(x + w * 0.62f, y + h * 0.12f); lineTo(x + w * 0.94f, y + h * 0.12f); lineTo(x + w * 0.94f, y + h * 0.9f); lineTo(x + w * 0.06f, y + h * 0.9f); close()
                addRect(Rect(x + w * 0.22f, y + h * 0.36f, x + w * 0.78f, y + h * 0.66f))
                moveTo(x + w * 0.2f, y + h * 0.78f); lineTo(x + w * 0.8f, y + h * 0.78f)
            }
            2 -> addPath(luna(Offset(x + w * 0.46f, y + h * 0.54f), minOf(w, h) * 0.4f))
            3 -> {
                val o = Offset(x + w * 0.24f, y + h * 0.8f)
                addOval(Rect(o.x - w * 0.07f, o.y - w * 0.07f, o.x + w * 0.07f, o.y + w * 0.07f))
                listOf(-80.0 to 0.72f, -50.0 to 0.9f, -20.0 to 0.72f).forEach { (g, l) ->
                    val an = Math.toRadians(g)
                    moveTo(o.x + (w * 0.18f * cos(an)).toFloat(), o.y + (w * 0.18f * sin(an)).toFloat())
                    lineTo(o.x + (w * l * cos(an)).toFloat(), o.y + (w * l * sin(an)).toFloat())
                }
            }
            else -> {
                val c = Offset(x + w * 0.5f, y + h * 0.3f)
                moveTo(c.x, c.y); lineTo(x + w * 0.3f, y + h * 0.94f); moveTo(c.x, c.y); lineTo(x + w * 0.7f, y + h * 0.94f)
                moveTo(x + w * 0.38f, y + h * 0.62f); lineTo(x + w * 0.62f, y + h * 0.62f)
                listOf(0.18f, 0.34f).forEach { rr ->
                    arcTo(Rect(c.x - w * rr * 1.4f, c.y - w * rr * 1.4f, c.x + w * rr * 1.4f, c.y + w * rr * 1.4f), -150f, 40f, true)
                    arcTo(Rect(c.x - w * rr * 1.4f, c.y - w * rr * 1.4f, c.x + w * rr * 1.4f, c.y + w * rr * 1.4f), -70f, 40f, true)
                }
            }
        }
    }

    /**
     * Las pestañas son el dial de sintonía del receptor: la caja de consola color hueso con sus esquinas redondas y, detrás
     * del vidrio, la escala impresa con sus marcas de frecuencia, partida en cinco bandas; en cada una, el emblema, el nombre y
     * la cuenta en segmentos. La aguja roja marca la banda elegida, que se ilumina en ámbar y lleva su raya roja al pie; abajo, la fila de pilotos,
     * encendido el de la banda elegida.
     */
    override fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val crema = Color(0xFFEDE4CC)
        val caja = Rect(1 * u, 2 * u, w - 1 * u, h - 2 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), caja.topLeft + Offset(1 * u, 2 * u), caja.size, CornerRadius(13 * u))
        drawRoundRect(Brush.verticalGradient(listOf(c(PANEL), c(HUESO), c(HUESO_OSC)), caja.top, caja.bottom), caja.topLeft, caja.size, CornerRadius(13 * u))
        drawRoundRect(c(BORDE), caja.topLeft, caja.size, CornerRadius(13 * u), style = Stroke(1f * u))
        // La ventana de vidrio con la escala.
        val vi = Rect(7 * u, 7 * u, w - 7 * u, h - 17 * u)
        drawRoundRect(c(CROMO), vi.inflate(1.5f * u).topLeft, vi.inflate(1.5f * u).size, CornerRadius(8 * u))
        drawRoundRect(if (oscuro) Color(0xFF15110C) else Color(0xFF241E16), vi.topLeft, vi.size, CornerRadius(7 * u))
        clipPath(Path().apply { addRoundRect(RoundRect(vi, CornerRadius(7 * u))) }) {
            if (oscuro) drawRect(Brush.verticalGradient(listOf(PILOTO.copy(alpha = 0.08f), Color.Transparent), vi.top, vi.bottom), vi.topLeft, vi.size)
            val marcas = 60
            for (i in 0..marcas) {
                val x = vi.left + vi.width * i / marcas
                val mayor = i % 6 == 0
                drawLine(crema.copy(alpha = if (mayor) 0.6f else 0.32f), Offset(x, vi.top + 2 * u), Offset(x, vi.top + (if (mayor) 8f else 5f) * u), 0.6f * u)
            }
            for (k in 0 until 5) {
                val q = quinta(k, w, vi.top, vi.bottom)
                val banda = Rect(maxOf(q.left, vi.left), vi.top, minOf(q.right, vi.right), vi.bottom)
                val sel = k == e.actual
                if (sel) drawRect(Brush.verticalGradient(listOf(PILOTO.copy(alpha = 0.3f), PILOTO.copy(alpha = 0.08f)), banda.top, banda.bottom), banda.topLeft, banda.size)
                if (k > 0) drawLine(crema.copy(alpha = 0.22f), Offset(q.left, vi.top + 10 * u), Offset(q.left, vi.bottom - 3 * u), 0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u)))
                val tono = if (sel) PILOTO else crema.copy(alpha = 0.62f)
                val ic = Rect(banda.center.x - 7 * u, vi.top + 10 * u, banda.center.x + 7 * u, vi.top + 23 * u)
                drawPath(iconoBiblioteca(k, ic), tono, style = Stroke(1.1f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
                textoEn(medidor, e.nombres.getOrElse(k) { "" }.uppercase(), Rect(banda.left + 2 * u, vi.top + 24 * u, banda.right - 2 * u, vi.top + 34 * u), MONO, tono, 8f, espaciado = 0.4f, minimo = 4.5f)
                segmentos(Frente.contador(e.cuentas.getOrElse(k) { 0 }), Rect(banda.center.x - 15 * u, vi.top + 36 * u, banda.center.x + 15 * u, vi.bottom - 2.5f * u),
                    if (sel) PILOTO else crema.copy(alpha = 0.5f), crema.copy(alpha = 0.06f), halo = sel)
            }
            // La aguja roja, sobre la banda elegida.
            val xa = quinta(e.actual.coerceIn(0, 4), w, 0f, h).center.x
            drawLine(PILOTO_ROJO.copy(alpha = 0.3f), Offset(xa, vi.top), Offset(xa, vi.top + 10 * u), 3 * u)
            drawLine(PILOTO_ROJO, Offset(xa, vi.top), Offset(xa, vi.top + 10 * u), 1.2f * u)
            drawPath(Path().apply { moveTo(xa - 3.5f * u, vi.top); lineTo(xa + 3.5f * u, vi.top); lineTo(xa, vi.top + 5 * u); close() }, PILOTO_ROJO)
            val bs = quinta(e.actual.coerceIn(0, 4), w, 0f, h)
            drawLine(PILOTO_ROJO, Offset(maxOf(bs.left, vi.left) + 6 * u, vi.bottom - 1.5f * u), Offset(minOf(bs.right, vi.right) - 6 * u, vi.bottom - 1.5f * u), 1.2f * u, StrokeCap.Round)
            // El reflejo del vidrio.
            drawPath(Path().apply { moveTo(vi.left + vi.width * 0.12f, vi.top); lineTo(vi.left + vi.width * 0.2f, vi.top); lineTo(vi.left + vi.width * 0.1f, vi.bottom); lineTo(vi.left + vi.width * 0.02f, vi.bottom); close() }, Color.White.copy(alpha = 0.05f))
        }
        // La fila de pilotos.
        for (k in 0 until 5) piloto(Offset(quinta(k, w, 0f, h).center.x, h - 9.5f * u), 2f * u, PILOTO, k == e.actual, c(CROMO), if (oscuro) 1.3f else 0.9f)
    }

    // ---------------------------------------------------------------- el buscador

    override val altoCampo: Dp = 52.dp
    override val cajaCampo = Rect(0.17f, 0.24f, 0.84f, 0.76f)
    override val letraCampo: FontFamily = MONO
    override fun tintaCampo(oscuro: Boolean): Color = PILOTO

    /**
     * El buscador es la línea de consulta de la consola: la caja color hueso de esquinas redondas con la ventanilla oscura donde
     * se escribe en ámbar. A la izquierda, la pantallita de radar, que barre más viva con el foco; a la derecha, la palanca, que
     * se sube con el foco, y su piloto, que se enciende.
     */
    override fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRoundRect(Brush.verticalGradient(listOf(c(PANEL), c(HUESO), c(HUESO_OSC)), 0f, h), Offset.Zero, size, CornerRadius(13 * u))
        drawRoundRect(c(BORDE), Offset.Zero, size, CornerRadius(13 * u), style = Stroke(1f * u))
        val vi = Rect(w * 0.15f, h * 0.16f, w * 0.86f, h * 0.84f)
        drawRoundRect(c(CROMO), vi.inflate(1.4f * u).topLeft, vi.inflate(1.4f * u).size, CornerRadius(7 * u))
        drawRoundRect(Color(0xFF1A1208), vi.topLeft, vi.size, CornerRadius(6 * u))
        if (e.enfocado) drawRoundRect(PILOTO.copy(alpha = 0.08f), vi.topLeft, vi.size, CornerRadius(6 * u))
        var y = vi.top + 2 * u
        while (y < vi.bottom) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(vi.left + 3 * u, y), Offset(vi.right - 3 * u, y), 0.6f * u); y += 2.5f * u }
        val rr = minOf(h * 0.3f, w * 0.05f)
        radar(Offset(w * 0.075f, h / 2), rr, if (e.enfocado) 340f else 300f, c(HUESO_OSC), 7, if (e.enfocado) 1.7f else 0.9f)
        palanca(Offset(w * 0.9f, h / 2), h * 0.42f, e.enfocado, c(CROMO), c(BAQUELITA))
        piloto(Offset(w * 0.955f, h / 2), 2.4f * u, PILOTO, e.enfocado, c(CROMO), 1.2f)
        if (e.vacio) {
            val r2 = Rect(w * cajaCampo.left, h * cajaCampo.top, w * cajaCampo.right, h * cajaCampo.bottom)
            aIzquierda(medidor, e.pista, r2, MONO, PILOTO.copy(alpha = 0.5f), 14f, minimo = 9f)
        }
    }

    // ---------------------------------------------------------------- las obras

    /**
     * La biblioteca de la célula: la pared de paneles perforados de la sala, con el riel de cromo arriba y sus dos focos que
     * alumbran las obras, los mazos de cable en los costados y, abajo, el labio de la repisa de chapa hueso con sus pilotos.
     */
    override fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(Brush.verticalGradient(listOf(c(HUESO_OSC), c(HUESO)), 0f, h))
        val agujeros = ArrayList<Offset>()
        var py = 16 * u; var fila = 0
        while (py < h - 24 * u) {
            var px = if (fila % 2 == 0) 6 * u else 10 * u
            while (px < w) { agujeros.add(Offset(px, py)); px += 8 * u }
            py += 7 * u; fila++
        }
        drawPoints(agujeros, PointMode.Points, c(BORDE).copy(alpha = 0.5f), 1.5f * u, StrokeCap.Round)
        // Los mazos de cable de los costados.
        listOf(3 * u, w - 10 * u).forEach { x -> for (j in 0 until 3) drawLine(c(listOf(BAQUELITA, ACERO, Color(0xFF5B4A3A))[j]), Offset(x + j * 2.4f * u, 0f), Offset(x + j * 2.4f * u, h), 2 * u) }
        // El riel de cromo con sus dos focos.
        drawRect(c(CROMO), Offset(0f, 4 * u), Size(w, 4 * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, 8 * u), Size(w, 1.5f * u))
        listOf(0.26f, 0.74f).forEach { fx ->
            val o = Offset(w * fx, 9 * u)
            drawPath(Path().apply { moveTo(o.x - 5 * u, o.y + 7 * u); lineTo(o.x + 5 * u, o.y + 7 * u); lineTo(o.x + 30 * u, h - 24 * u); lineTo(o.x - 30 * u, h - 24 * u); close() },
                PILOTO.copy(alpha = if (oscuro) 0.1f else 0.06f))
            drawRoundRect(c(BAQUELITA), Offset(o.x - 5 * u, o.y), Size(10 * u, 8 * u), CornerRadius(2 * u))
            drawOval(mezcla(PILOTO, Color.White, 0.5f), Offset(o.x - 4 * u, o.y + 6 * u), Size(8 * u, 2.4f * u))
        }
        // El labio de la repisa, con sus pilotos y la placa grabada.
        val y0 = h - 24 * u
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, y0 - 3 * u), Size(w, 3 * u))
        drawRect(Brush.verticalGradient(listOf(c(PANEL), c(HUESO), c(BORDE)), y0, h), Offset(0f, y0), Size(w, h - y0))
        drawRect(c(CROMO), Offset(0f, y0), Size(w, 1.4f * u))
        for (k in 0 until 8) drawRect(Color.Black.copy(alpha = 0.06f), Offset(w * (0.06f + 0.12f * k), y0 + 6 * u), Size(w * 0.05f, 1.2f * u))
        listOf(14 * u, 26 * u, w - 26 * u, w - 14 * u).forEachIndexed { k, x -> piloto(Offset(x, (y0 + h) / 2f + 1 * u), 1.8f * u, if (k == 1) RADAR else PILOTO, k != 2, c(CROMO), if (oscuro) 1.2f else 0.7f) }
    }

    /** Un título partido por palabras en a lo más tres renglones que caben en [r] (el último se achica si hace falta). */
    private fun DrawScope.tituloEn(medidor: TextMeasurer, titulo: String, r: Rect, letra: FontFamily, color: Color, tam: Float, peso: FontWeight) {
        val palabras = titulo.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (palabras.isEmpty()) return
        val renglones = mutableListOf<String>()
        var actual = ""
        palabras.forEach { p ->
            val prueba = if (actual.isEmpty()) p else "$actual $p"
            if (actual.isNotEmpty() && anchoTx(medidor, prueba, letra, tam, peso = peso) > r.width && renglones.size < 2) { renglones.add(actual); actual = p }
            else { actual = prueba }
        }
        renglones.add(actual)
        val alto = r.height / 3f
        var y = r.center.y - alto * renglones.size / 2f
        renglones.forEach { s ->
            textoEn(medidor, s, Rect(r.left, y, r.right, y + alto), letra, color, tam, peso = peso, minimo = tam * 0.5f)
            y += alto
        }
    }

    /**
     * La portada de una obra es un dosier de la célula: la lona acero con su trama y el lomo doblado, el rótulo ámbar con el
     * tipo, la ficha de papel engrapada con el título a máquina, el autor y el año, el sello de la célula en tinta ámbar y,
     * abajo, el número del archivo y la tira de cinta perforada. En miniatura, solo la inicial en el rótulo ámbar.
     */
    override fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = w / u
        fun c(x: Color) = t(x, oscuro, false)
        val lona = c(LONAS[Math.floorMod(e.semilla, LONAS.size)])
        val hoja = papel(oscuro, false)
        drawRect(Brush.verticalGradient(listOf(mezcla(lona, Color.White, 0.08f), lona, mezcla(lona, Color.Black, 0.2f)), 0f, h))
        trama(Rect(0f, 0f, w, h), Color.White.copy(alpha = 0.045f), maxOf(1.4f * u, w * 0.02f))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset.Zero, Size(w * 0.07f, h))
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(w * 0.07f, 0f), Offset(w * 0.07f, h), maxOf(0.6f * u, w * 0.008f))
        if (e.mini) {
            val r = Rect(w * 0.22f, h * 0.3f, w * 0.86f, h * 0.7f)
            drawRoundRect(ambar(oscuro, false), r.topLeft, r.size, CornerRadius(w * 0.05f))
            textoEn(medidor, e.titulo.trim().take(1).uppercase().ifEmpty { "+" }, r, TITULO, c(BAQUELITA), s * 0.36f, minimo = 5f)
            return
        }
        // El rótulo ámbar con el tipo.
        val ro = Rect(w * 0.14f, h * 0.05f, w * 0.92f, h * 0.14f)
        drawRoundRect(ambar(oscuro, false), ro.topLeft, ro.size, CornerRadius(w * 0.02f))
        textoEn(medidor, e.tipo.uppercase(), ro.deflate(w * 0.02f), MONO, c(BAQUELITA), s * 0.075f, espaciado = s * 0.012f, minimo = 3.5f)
        // La ficha engrapada con el título, el autor y el año.
        val fi = Rect(w * 0.16f, h * 0.2f, w * 0.9f, h * 0.68f)
        drawRect(Color.Black.copy(alpha = 0.28f), fi.topLeft + Offset(w * 0.015f, w * 0.02f), fi.size)
        drawRect(hoja, fi.topLeft, fi.size)
        listOf(0.25f, 0.62f).forEach { fx -> grapa(Offset(fi.left + fi.width * fx, fi.top + w * 0.03f), Offset(fi.left + fi.width * (fx + 0.14f), fi.top + w * 0.03f)) }
        tituloEn(medidor, e.titulo.uppercase(), Rect(fi.left + w * 0.05f, fi.top + h * 0.05f, fi.right - w * 0.05f, fi.top + fi.height * 0.72f), TEXTO, PIZARRA, s * 0.1f, FontWeight.SemiBold)
        val pie = listOf(e.autor, e.anio).filter { it.isNotBlank() }.joinToString(" · ")
        if (pie.isNotEmpty()) {
            drawLine(c(AMBAR), Offset(fi.left + w * 0.06f, fi.top + fi.height * 0.75f), Offset(fi.right - w * 0.06f, fi.top + fi.height * 0.75f), maxOf(0.5f * u, s * 0.008f * u))
            textoEn(medidor, pie, Rect(fi.left + w * 0.04f, fi.top + fi.height * 0.78f, fi.right - w * 0.04f, fi.bottom - h * 0.01f), MONO, PIZARRA.copy(alpha = 0.85f), s * 0.062f, minimo = 3.5f)
        }
        // El sello de la célula, a caballo sobre la esquina de la ficha.
        val so = Offset(w * 0.78f, h * 0.72f)
        rotate(-14f, so) { selloGoma(medidor, so, w * 0.14f, c(OBLEA).copy(alpha = 0.7f), lona, e.semilla, false) }
        // El número del archivo y la tira de cinta perforada.
        textoEn(medidor, "Nº " + (variante(e.semilla, 90) + 10) + "-" + (variante(e.semilla / 7, 900) + 100), Rect(w * 0.12f, h * 0.76f, w * 0.6f, h * 0.84f),
            MONO, mezcla(HUESO, lona, 0.15f), s * 0.065f, minimo = 3.5f)
        cintaPerforada(Rect(w * 0.12f, h * 0.87f, w * 0.92f, h * 0.93f), c(PAPEL), c(BAQUELITA).copy(alpha = 0.6f), e.semilla)
    }

    /** Cuál de [n] variantes le toca a una obra según su [semilla], que puede venir negativa. */
    private fun variante(semilla: Int, n: Int): Int = Math.floorMod(semilla, n.coerceAtLeast(1))

    // ---------------------------------------------------------------- los recuerdos

    override val margenRecuerdo: Dp = 14.dp
    override val altoRotuloRecuerdo: Dp = 46.dp
    override fun tintaRecuerdo(oscuro: Boolean): Color = if (oscuro) Color(0xFFE7E1D4) else PIZARRA

    /** «Hace un año» o «Hace N años», como lo dice la ficha del recuerdo. */
    private fun haceAnios(n: Int): String = if (n == 1) "Hace un año" else "Hace $n años"

    /** El clima sin su signo (☀ ☂ ❄), que las letras del tema no traen. */
    private fun sinSigno(clima: String): String = clima.dropWhile { !it.isLetterOrDigit() }.trim()

    /**
     * El recuerdo es la ficha del registro de recepción: el panel color hueso de esquinas redondas con su cabecera; a la
     * izquierda, los años en la pantalla de segmentos; al lado, «Hace N años» y, debajo, la fecha y el clima a máquina; a la
     * derecha, el piloto encendido del color del ánimo (apagado si no hay). Debajo, el filete ámbar doble, el margen y los
     * renglones del registro, y el emblema de la red como marca de agua.
     */
    override fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ficha = if (oscuro) Color(0xFF1E2327) else PANEL
        val cabeza = if (oscuro) Color(0xFF262C31) else HUESO_OSC
        val tinta = tintaRecuerdo(oscuro)
        val cab = altoRotuloRecuerdo.toPx()
        val forma = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(13 * u))) }
        drawPath(forma, ficha)
        clipPath(forma) {
            drawRect(cabeza, Offset.Zero, Size(w, cab))
            var y = cab + 16 * u
            while (y < h - 6 * u) { drawLine(tinta.copy(alpha = 0.07f), Offset(0f, y), Offset(w, y), 0.6f * u); y += 17 * u }
            drawLine(PILOTO.copy(alpha = 0.35f), Offset(8 * u, cab), Offset(8 * u, h), 0.8f * u)
            if (h > cab + 40 * u) emblema(Offset(w - 38 * u, h - 24 * u), 18 * u, tinta.copy(alpha = 0.05f), 2.6f * u)
        }
        drawLine(if (oscuro) PILOTO else AMBAR, Offset(0f, cab - 1 * u), Offset(w, cab - 1 * u), 1.2f * u)
        drawLine((if (oscuro) PILOTO else AMBAR).copy(alpha = 0.5f), Offset(0f, cab + 1.5f * u), Offset(w, cab + 1.5f * u), 0.6f * u)
        drawPath(forma, Color.Black.copy(alpha = 0.2f), style = Stroke(1f * u))
        // Los años en la pantalla de segmentos.
        val ve = Rect(10 * u, 9 * u, 40 * u, cab - 9 * u)
        drawRoundRect(BAQUELITA, ve.inflate(1.4f * u).topLeft, ve.inflate(1.4f * u).size, CornerRadius(3 * u))
        drawRoundRect(Color(0xFF1A1208), ve.topLeft, ve.size, CornerRadius(2 * u))
        segmentos("%02d".format(e.anios.coerceIn(0, 99)), ve.deflate(2.5f * u), PILOTO, PILOTO.copy(alpha = 0.08f), halo = oscuro)
        // «Hace N años», la fecha y el clima.
        val der = w - 30 * u
        val col = Rect(48 * u, 6 * u, der, cab - 6 * u)
        aIzquierda(medidor, haceAnios(e.anios).uppercase(), Rect(col.left, col.top, col.right, col.top + col.height * 0.48f), TITULO, if (oscuro) PILOTO else AMBAR, 9f, espaciado = 0.6f, minimo = 6f)
        val clima = sinSigno(e.clima)
        aIzquierda(medidor, if (clima.isEmpty()) e.fecha else e.fecha + " · " + clima, Rect(col.left, col.top + col.height * 0.5f, col.right, col.bottom), MONO, tinta, 10.5f, minimo = 7f)
        // El piloto del ánimo.
        val pa = Offset(w - 16 * u, cab / 2f)
        if (e.animo != null) piloto(pa, 4.4f * u, e.animo, true, if (oscuro) mezcla(CROMO, SALA, 0.3f) else CROMO, if (oscuro) 1.2f else 0.8f)
        else piloto(pa, 4.4f * u, PILOTO, false, if (oscuro) mezcla(CROMO, SALA, 0.3f) else CROMO)
    }
}
