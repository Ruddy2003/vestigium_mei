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
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.sombraSuave
import io.github.ruddy2003.vestigium.ui.selloCarta
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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
    private val ROJO = Color(0xFFA8201A)
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
    /** La penumbra verdosa de la base en modo oscuro y el verde casi negro de la madrugada. */
    private val PENUMBRA = Color(0xFF0E1614)
    private val MADRUGADA = Color(0xFF07110D)
    /** Las ruinas que esconden la base: la piedra (a la luz y en sombra), la raíz, el musgo, el cobre y el hormigón de la losa. */
    private val PIEDRA = TrazosFrente.PIEDRA
    private val PIEDRA_SOMBRA = TrazosFrente.PIEDRA_SOMBRA
    private val RAIZ = TrazosFrente.RAIZ
    private val MUSGO = TrazosFrente.MUSGO
    private val COBRE = TrazosFrente.COBRE
    private val LOSA = Color(0xFFA3A69B)
    private val HUMEDAD = Color(0xFF34402F)
    /** La consola oscura, de verde casi negro, y su canto donde le da la luz. */
    private val CONSOLA = TrazosFrente.CONSOLA_OSCURA
    private val CONSOLA_LUZ = Color(0xFF34453F)
    /** El vidrio grabado: el trazo iluminado por el canto y el vidrio mismo (en penumbra y a la luz). */
    private val VIDRIO = TrazosFrente.VIDRIO
    private val VIDRIO_LUZ = TrazosFrente.VIDRIO_LUZ
    private val VIDRIO_TRAZO = TrazosFrente.VIDRIO_TRAZO
    private val PELIGRO = TrazosFrente.PELIGRO
    /** La lona acero de los dosieres, cada uno de un lote un poco distinto. */
    private val LONAS = listOf(Color(0xFF3E4C57), Color(0xFF46535C), Color(0xFF384650), Color(0xFF4A5753), Color(0xFF424A55))
    /** El cartón hueso de las carpetas de despacho: más o menos gastado. */
    private val CARTONES = listOf(Color(0xFFCFC7B5), Color(0xFFC6BDA8), Color(0xFFD8D1C0))
    /** Las carcasas de los cartuchos: baquelita negra cálida, alguna gris pizarra. */
    private val CARCASAS = listOf(Color(0xFF2A2622), Color(0xFF2A2622), Color(0xFF34363B))

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

    /**
     * El color del lugar (la piedra, la losa, el piso, las raíces y el vidrio de la base): en modo oscuro, la base en su
     * penumbra verdosa; de noche, además, el verde casi negro de la madrugada.
     */
    private fun lugar(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, PENUMBRA, 0.6f)
        if (noche) r = mezcla(r, MADRUGADA, 0.5f)
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

    /** El ciclo corto para los lomos delgados: «CICLO 11» se vuelve «C-11». */
    private fun cicloCorto(mes: String): String {
        val m = mes.uppercase()
        return if (m.startsWith("CICLO ")) "C-" + m.removePrefix("CICLO ").substringBefore(" ") else m
    }

    /** Cuál de los tres objetos del archivo es una entrada: 0, el dosier; 1, la carpeta de despacho; 2, el cartucho de datos. */
    private fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 9), 5)
        return if (k < 2) 0 else if (k == 2) 1 else 2
    }

    /** Dibuja [bloque] con la transformación afín x' = a·x + c·y + tx, y' = b·x + d·y + ty (para las tapas que giran). */
    private fun DrawScope.afin(a: Float, b: Float, c: Float, d: Float, tx: Float, ty: Float, bloque: DrawScope.() -> Unit) {
        withTransform({ transform(Matrix(floatArrayOf(a, b, 0f, 0f, c, d, 0f, 0f, 0f, 0f, 1f, 0f, tx, ty, 0f, 1f))) }) { bloque() }
    }

    /**
     * Una tapa que se levanta sobre la bisagra de atrás (en [y0], arriba de la tapa cerrada, de alto [alto]), vista desde
     * delante y un poco desde arriba: con [angulo] (radianes) el canto libre sube; [alza] es cuánto más se ve lo vertical que lo
     * hondo. Mientras se ve por fuera dibuja [fuera] en las coordenadas de la tapa cerrada; cuando ya pasó, [dentro] en esas
     * mismas coordenadas, con el canto libre arriba. El número que reciben (0 a 1) es cuánto se ve de canto, para oscurecerla.
     */
    private fun DrawScope.levantar(y0: Float, alto: Float, angulo: Float, alza: Float, fuera: DrawScope.(Float) -> Unit, dentro: DrawScope.(Float) -> Unit) {
        val s = cos(angulo) - alza * sin(angulo)
        val canto = (1f - abs(s) / maxOf(1f, alza)).coerceIn(0f, 1f)
        if (s >= 0f) afin(1f, 0f, 0f, s, 0f, y0 - y0 * s) { fuera(canto) }
        else afin(1f, 0f, 0f, -s, 0f, y0 + s * alto + y0 * s) { dentro(canto) }
    }

    /** El grosor del cartucho según sus bloques: crece rápido con los primeros y luego despacio, de 3 a 20 dp. */
    private fun grosor(hojas: Int, u: Float): Float = (3f + 1.25f * sqrt(hojas.coerceAtLeast(0).toFloat())).coerceAtMost(20f) * u

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

    /** Una perilla de baquelita con su falda de cromo estriada y la raya que marca hacia dónde está ([giro], en grados). */
    private fun DrawScope.perilla(c: Offset, r: Float, giro: Float, falda: Color, mango: Color) {
        val u = density
        drawCircle(Color.Black.copy(alpha = 0.3f), r * 1.3f, Offset(c.x + 0.8f * u, c.y + 1.2f * u))
        drawCircle(falda, r * 1.3f, c)
        for (k in 0 until 24) {
            val a = k * PI.toFloat() / 12f
            drawLine(mezcla(falda, Color.Black, 0.3f), Offset(c.x + cos(a) * r * 1.06f, c.y + sin(a) * r * 1.06f), Offset(c.x + cos(a) * r * 1.27f, c.y + sin(a) * r * 1.27f), 0.5f * u)
        }
        drawCircle(mango, r, c)
        drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.24f), Color.Transparent), Offset(c.x - r * 0.3f, c.y - r * 0.35f), r), r, c)
        val a = Math.toRadians(giro.toDouble())
        drawLine(mezcla(falda, Color.White, 0.4f), Offset(c.x + cos(a).toFloat() * r * 0.25f, c.y + sin(a).toFloat() * r * 0.25f),
            Offset(c.x + cos(a).toFloat() * r * 0.86f, c.y + sin(a).toFloat() * r * 0.86f), maxOf(0.8f * u, r * 0.14f), StrokeCap.Round)
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

    /**
     * Los cascos del radiooperador: el arco de la diadema y las dos copas de baquelita con su almohadilla, y el cable rizado
     * que sale de la copa izquierda y baja hasta [fin]. [c] es el centro de la diadema y [r] su radio.
     */
    private fun DrawScope.cascos(c: Offset, r: Float, fin: Offset, baquelita: Color, cromo: Color) {
        val u = density
        // El cable rizado: una espiral aplastada a lo largo de la recta hasta [fin].
        val inicio = Offset(c.x - r * 0.95f, c.y + r * 0.55f)
        val dx = fin.x - inicio.x; val dy = fin.y - inicio.y
        val largo = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
        val ex = dx / largo; val ey = dy / largo
        val vueltas = (largo / (3.2f * u)).toInt().coerceIn(6, 120)
        val cable = Path().apply {
            moveTo(inicio.x, inicio.y)
            for (i in 1..vueltas * 6) {
                val f = i / (vueltas * 6f)
                val ang = f * vueltas * 2f * PI.toFloat()
                val amp = 2.2f * u
                val x = inicio.x + dx * f - ey * sin(ang) * amp + ex * cos(ang) * amp * 0.6f
                val y = inicio.y + dy * f + ex * sin(ang) * amp + ey * cos(ang) * amp * 0.6f
                lineTo(x, y)
            }
        }
        drawPath(cable, Color.Black.copy(alpha = 0.3f), style = Stroke(1.4f * u))
        drawPath(cable, baquelita, style = Stroke(0.9f * u))
        // La diadema y sus dos barras de cromo.
        drawArc(Color.Black.copy(alpha = 0.3f), 180f, 180f, false, Offset(c.x - r + 0.8f * u, c.y - r + 1.2f * u), Size(r * 2, r * 2), style = Stroke(r * 0.16f, cap = StrokeCap.Round))
        drawArc(baquelita, 180f, 180f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(r * 0.16f, cap = StrokeCap.Round))
        drawArc(Color.White.copy(alpha = 0.2f), 200f, 60f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(r * 0.04f))
        listOf(-1f, 1f).forEach { s ->
            val x = c.x + s * r
            drawLine(cromo, Offset(x, c.y), Offset(x, c.y + r * 0.3f), r * 0.07f)
            val copa = Rect(x - r * 0.24f, c.y + r * 0.22f, x + r * 0.24f, c.y + r * 0.92f)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), copa.topLeft + Offset(0.8f * u, 1.2f * u), copa.size, CornerRadius(r * 0.22f))
            drawRoundRect(baquelita, copa.topLeft, copa.size, CornerRadius(r * 0.22f))
            drawRoundRect(Color.White.copy(alpha = 0.16f), Offset(copa.left + r * 0.06f, copa.top + r * 0.06f), Size(copa.width * 0.3f, copa.height * 0.7f), CornerRadius(r * 0.1f))
            drawCircle(cromo, r * 0.07f, copa.center)
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

    /** El brillo de las raíces: cálido, de las lámparas, de día; de noche, el filo verde que les deja el vidrio. */
    private fun raizLuz(oscuro: Boolean, noche: Boolean): Color =
        if (oscuro || noche) VIDRIO_TRAZO.copy(alpha = if (noche) 0.16f else 0.12f) else Color(0xFFC9BC94).copy(alpha = 0.32f)

    /**
     * Las raíces que asoman bajo la tabla de arriba de la repisa [r] (la [k]-ésima) y bajan por la piedra: dos o tres, de
     * largo y grosor desiguales, siempre las mismas en la misma repisa.
     */
    private fun DrawScope.raices(r: Rect, k: Int, oscuro: Boolean, noche: Boolean) {
        val u = density
        val cuantas = 2 + (azar(k * 7 + 3, 1) * 1.99f).toInt()
        for (j in 0 until cuantas) {
            val x = r.left + r.width * (0.08f + 0.84f * azar(k * 13 + 5, j))
            val largo = r.height * (0.3f + 0.55f * azar(k * 13 + 6, j))
            with(TrazosFrente) { raiz(Offset(x, r.top - 1 * u), largo, (1.2f + 1f * azar(k * 13 + 7, j)) * u, lugar(RAIZ, oscuro, noche), raizLuz(oscuro, noche), k * 11 + j, sombra = 0.35f) }
        }
    }

    /**
     * Un panel de vidrio grabado empotrado en el muro, en [r]: el marco oscuro, el vidrio de tono cercano a la piedra (pálido
     * de día, verde hondo en la penumbra) con sus trazos y órbitas iluminados por el canto ([estilo] de
     * [TrazosFrente.trazosVidrio]), la luz que sube del canto de abajo, los reflejos y el filo encendido. En la penumbra
     * alumbra la piedra de alrededor.
     */
    private fun DrawScope.vidrioMuro(r: Rect, oscuro: Boolean, noche: Boolean, semilla: Int, estilo: Int) {
        val u = density
        val penumbra = oscuro || noche
        if (penumbra) mancha(r.center, maxOf(r.width, r.height) * 0.75f, VIDRIO_TRAZO.copy(alpha = if (noche) 0.12f else 0.09f))
        val marco = r.inflate(2.2f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), marco.topLeft + Offset(1 * u, 1.6f * u), marco.size)
        drawRect(lugar(CONSOLA, oscuro, noche), marco.topLeft, marco.size)
        drawLine(Color.White.copy(alpha = if (penumbra) 0.06f else 0.2f), Offset(marco.left, marco.top + 0.5f * u), Offset(marco.right, marco.top + 0.5f * u), 0.6f * u)
        drawRect(if (penumbra) lugar(mezcla(VIDRIO, PENUMBRA, if (oscuro) 0.3f else 0f), false, noche) else mezcla(HUESO_OSC, VIDRIO_LUZ, 0.3f), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, VIDRIO_TRAZO.copy(alpha = if (penumbra) 0.2f else 0.25f)), r.bottom - r.height * 0.5f, r.bottom),
                Offset(r.left, r.bottom - r.height * 0.5f), Size(r.width, r.height * 0.5f))
            with(TrazosFrente) { trazosVidrio(r, if (penumbra) VIDRIO_TRAZO else mezcla(VIDRIO_LUZ, VIDRIO_TRAZO, 0.35f), 0.6f * u, if (noche) 0.6f else if (oscuro) 0.5f else 0.55f, semilla, estilo) }
            listOf(0.12f to 0.06f, 0.22f to 0.03f).forEach { (f, a) ->
                drawLine(Color.White.copy(alpha = if (penumbra) a * 0.6f else a * 2.2f), Offset(r.left + r.width * f, r.bottom), Offset(r.left + r.width * (f + 0.3f), r.top), r.width * 0.05f)
            }
        }
        drawRect(VIDRIO_TRAZO.copy(alpha = if (penumbra) 0.5f else 0.45f), r.topLeft, r.size, style = Stroke(0.5f * u))
        drawLine(VIDRIO_TRAZO.copy(alpha = if (penumbra) 0.85f else 0.65f), Offset(r.left, r.bottom - 0.4f * u), Offset(r.right, r.bottom - 0.4f * u), 0.9f * u)
    }

    /**
     * Una consola oscura en [r]: el cuerpo de verde casi negro, más claro arriba donde le da la luz, con su sombra en el muro,
     * el filo de luz del canto, la mugre de los años y el contorno.
     */
    private fun DrawScope.consolaOscura(r: Rect, radio: Float, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        sombraSuave(r, radio, 5 * u, if (oscuro) 0.45f else 0.3f)
        drawRoundRect(Brush.verticalGradient(listOf(c(CONSOLA_LUZ), c(CONSOLA), c(mezcla(CONSOLA, Color.Black, 0.3f))), r.top, r.bottom), r.topLeft, r.size, CornerRadius(radio))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.07f else 0.22f), Offset(r.left + radio, r.top + 0.8f * u), Offset(r.right - radio, r.top + 0.8f * u), 0.7f * u)
        for (k in 0 until 4) {
            val x = r.left + r.width * azar(r.left.toInt(), k)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.02f else 0.05f), Offset(x, r.top + r.height * 0.15f), Offset(x + 0.4f * u, r.top + r.height * (0.3f + 0.4f * azar(r.top.toInt(), k))), (1 + 2 * azar(7, k)) * u, StrokeCap.Round)
        }
        drawRoundRect(Color.Black.copy(alpha = 0.55f), r.topLeft, r.size, CornerRadius(radio), style = Stroke(0.6f * u))
    }

    /**
     * La carta estelar grabada en un panel de vidrio de la base, en [r]: el riel oscuro de arriba con el rótulo grabado, el
     * vidrio iluminado por el canto con sus trazos, sus estrellas, los mundos unidos por la ruta de salto y el blanco marcado
     * en rojo; abajo, el pie oscuro. En la penumbra, el vidrio alumbra la piedra de alrededor.
     */
    private fun DrawScope.cartaVidrio(r: Rect, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val riel = Rect(r.left - 2 * u, r.top, r.right + 2 * u, r.top + 9 * u)
        val pie = Rect(r.left - 3 * u, r.bottom - 6 * u, r.right + 3 * u, r.bottom)
        val v = Rect(r.left, riel.bottom, r.right, pie.top)
        if (oscuro) mancha(v.center, v.width * 0.85f, VIDRIO_TRAZO.copy(alpha = 0.1f))
        drawRect(Color.Black.copy(alpha = 0.25f), v.topLeft + Offset(1.6f * u, 2 * u), v.size)
        drawRect(if (oscuro) mezcla(VIDRIO, PENUMBRA, 0.2f) else mezcla(HUESO_OSC, VIDRIO_LUZ, 0.28f), v.topLeft, v.size)
        val trazo = if (oscuro) VIDRIO_TRAZO else mezcla(VIDRIO_LUZ, VIDRIO_TRAZO, 0.3f)
        clipRect(v.left, v.top, v.right, v.bottom) {
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, VIDRIO_TRAZO.copy(alpha = if (oscuro) 0.2f else 0.25f)), v.bottom - v.height * 0.5f, v.bottom),
                Offset(v.left, v.bottom - v.height * 0.5f), Size(v.width, v.height * 0.5f))
            with(TrazosFrente) { trazosVidrio(v, trazo, 0.5f * u, if (oscuro) 0.28f else 0.3f, 23, 0) }
            for (k in 0 until 34) drawCircle(trazo.copy(alpha = 0.35f + 0.5f * azar(17, k)), (0.4f + 0.6f * azar(19, k)) * u,
                Offset(v.left + v.width * azar(23, k), v.top + v.height * azar(29, k)))
            val mundos = (0 until 5).map { k -> Offset(v.left + v.width * (0.12f + 0.19f * k), v.top + v.height * (0.25f + 0.55f * azar(31, k))) }
            with(TrazosFrente) {
                rutaSalto(mundos, trazo.copy(alpha = 0.3f), 2.6f * u, 3.2f * u)
                rutaSalto(mundos, if (oscuro) VIDRIO_TRAZO else mezcla(VIDRIO_LUZ, Color.Black, 0.1f), 1.1f * u, 3.2f * u)
            }
            mundos.forEach { o -> drawCircle(if (oscuro) VIDRIO_TRAZO else mezcla(VIDRIO_LUZ, Color.Black, 0.1f), 1.3f * u, o) }
            val blanco = mundos.last()
            drawCircle(PILOTO_ROJO.copy(alpha = if (oscuro) 0.25f else 0.15f), 7 * u, blanco)
            drawCircle(PILOTO_ROJO, 6 * u, blanco, style = Stroke(1f * u))
            listOf(0.1f to 0.06f, 0.2f to 0.03f).forEach { (fr, a) ->
                drawLine(Color.White.copy(alpha = if (oscuro) a * 0.6f else a * 2.2f), Offset(v.left + v.width * fr, v.bottom), Offset(v.left + v.width * (fr + 0.3f), v.top), v.width * 0.05f)
            }
        }
        drawRect(VIDRIO_TRAZO.copy(alpha = if (oscuro) 0.5f else 0.45f), v.topLeft, v.size, style = Stroke(0.5f * u))
        drawLine(VIDRIO_TRAZO.copy(alpha = if (oscuro) 0.85f else 0.65f), Offset(v.left, v.bottom - 0.4f * u), Offset(v.right, v.bottom - 0.4f * u), 0.9f * u)
        // El riel de arriba, con el rótulo grabado, y el pie oscuro.
        drawRoundRect(Color.Black.copy(alpha = 0.3f), riel.topLeft + Offset(0.6f * u, 1.2f * u), riel.size, CornerRadius(2 * u))
        drawRoundRect(Brush.verticalGradient(listOf(c(CONSOLA_LUZ), c(CONSOLA)), riel.top, riel.bottom), riel.topLeft, riel.size, CornerRadius(2 * u))
        textoEn(medidor, "CARTA ESTELAR · SECTOR 9", riel.deflate(1 * u), MONO, if (oscuro) mezcla(VIDRIO_TRAZO, PENUMBRA, 0.15f) else mezcla(VIDRIO_TRAZO, HUESO, 0.4f), 6f, espaciado = 0.6f, minimo = 3.5f)
        drawRect(Brush.verticalGradient(listOf(c(CONSOLA_LUZ), c(CONSOLA)), pie.top, pie.bottom), pie.topLeft, pie.size)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.2f), Offset(pie.left, pie.top + 0.5f * u), Offset(pie.right, pie.top + 0.5f * u), 0.5f * u)
        val rej = Rect(pie.center.x - pie.width * 0.14f, pie.top + 1.6f * u, pie.center.x + pie.width * 0.14f, pie.bottom - 1.4f * u)
        drawRect(c(CROMO).copy(alpha = 0.7f), rej.topLeft, rej.size)
        var x = rej.left + 1 * u
        while (x < rej.right - 0.6f * u) { drawLine(Color.Black.copy(alpha = 0.6f), Offset(x, rej.top + 0.5f * u), Offset(x, rej.bottom - 0.5f * u), 0.5f * u); x += 1.6f * u }
    }

    // =================================================================
    //  El mueble
    // =================================================================
    //  El mueble
    // =================================================================

    override val corona = 0.4f
    override val base = 0.1f
    override val lado = 0.06f
    override val altoRepisa: Dp = 112.dp
    override val tabla: Dp = 16.dp
    override val separa: Dp = 2.dp
    override val letra: FontFamily = MONO

    override fun ancho(palabras: Int): Dp = (23 + (palabras / 48).coerceAtMost(17)).dp
    override fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.86f + 0.1f * azar(semilla, 4)
        1 -> 0.79f + 0.08f * azar(semilla, 4)
        else -> 0.66f + 0.07f * azar(semilla, 4)
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        fun l(x: Color) = lugar(x, oscuro, noche)
        // El fondo: el muro de sillares de las ruinas que esconden la base, que se ve por detrás del estante abierto, con su
        // humedad y su musgo; en cada repisa, la sombra de la tabla de arriba y las raíces que asoman bajo ella y bajan por la
        // piedra, por detrás de los volúmenes. En la base a oscuras, los pilotos de los costados tiñen de ámbar las orillas.
        if (d.repisas.isNotEmpty()) {
            val muro = Rect(d.repisas.first().left, d.repisas.first().top, d.repisas.first().right, d.repisas.last().bottom)
            with(TrazosFrente) {
                muroSillares(muro, 15 * u, l(mezcla(PIEDRA, HUESO_OSC, 0.25f)), l(PIEDRA_SOMBRA), HUMEDAD.copy(alpha = if (noche) 0.6f else 0.45f), l(MUSGO), 41,
                    if (noche) 0.25f else if (oscuro) 0.45f else 0.9f)
            }
        }
        // Un panel de vidrio grabado empotrado en la piedra de cada repisa, a un lado o al otro: se ve donde no hay volúmenes.
        d.repisas.forEachIndexed { k, r ->
            val izq = k % 2 == 1
            val p = Rect(if (izq) r.left + r.width * 0.05f else r.left + r.width * 0.55f, r.top + 22 * u, if (izq) r.left + r.width * 0.45f else r.right - r.width * 0.05f, r.bottom - 8 * u)
            if (p.height > 30 * u) vidrioMuro(p, oscuro, noche, 17 + k * 5, k % 3)
        }
        d.repisas.forEachIndexed { k, r ->
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (noche) 0.55f else 0.42f), Color.Transparent), r.top, r.top + 18 * u), r.topLeft, Size(r.width, 18 * u))
            raices(r, k, oscuro, noche)
            if (oscuro || noche) listOf(r.left, r.right).forEach { x ->
                mancha(Offset(x, r.top + r.height * 0.3f), r.height * 0.75f, PILOTO.copy(alpha = if (noche) 0.1f else 0.07f))
            }
        }
        // Los costados: montantes de chapa hueso de canto redondo, con su canal y el filete de cromo; en cada nivel, a la
        // izquierda un piloto con su placa grabada y a la derecha una palanca con su piloto.
        listOf(d.izquierda to true, d.derecha to false).forEach { (r, izq) ->
            drawRect(Brush.horizontalGradient(if (izq) listOf(c(HUESO_OSC), c(PANEL), c(HUESO), c(BORDE)) else listOf(c(BORDE), c(HUESO), c(PANEL), c(HUESO_OSC)), r.left, r.right), r.topLeft, r.size)
            val canal = if (izq) r.left + r.width * 0.78f else r.left + r.width * 0.22f
            drawLine(c(BORDE), Offset(canal, r.top), Offset(canal, r.bottom), 2.2f * u)
            drawLine(c(CROMO), Offset(canal + 0.6f * u, r.top), Offset(canal + 0.6f * u, r.bottom), 0.7f * u)
            val cx = if (izq) r.left + r.width * 0.38f else r.left + r.width * 0.62f
            d.repisas.forEachIndexed { k, rep ->
                val s = k * 13 + (if (izq) 0 else 7)
                val prendido = noche || azar(s, 3) > 0.22f
                val luz = if (azar(s, 5) > 0.84f) PILOTO_ROJO else PILOTO
                val rp = minOf(r.width * 0.15f, 2.6f * u)
                if (izq) {
                    piloto(Offset(cx, rep.top + rep.height * 0.24f), rp, luz, prendido, c(CROMO), if (noche) 1.4f else if (oscuro) 1.1f else 0.75f)
                    val pl = Rect(r.left + r.width * 0.1f, rep.top + rep.height * 0.38f, r.left + r.width * 0.66f, rep.top + rep.height * 0.38f + 9 * u)
                    drawRoundRect(c(BAQUELITA), pl.topLeft, pl.size, CornerRadius(1.5f * u))
                    textoEn(medidor, "%02d".format(k + 1), pl, MONO, if (noche) mezcla(HUESO, PILOTO, 0.3f) else c(HUESO), 6.5f, minimo = 3.5f)
                } else {
                    palanca(Offset(cx, rep.top + rep.height * 0.3f), minOf(r.width * 0.55f, 11 * u), azar(s, 9) > 0.45f, c(CROMO), c(BAQUELITA))
                    piloto(Offset(cx, rep.top + rep.height * 0.56f), rp, PILOTO, prendido, c(CROMO), if (noche) 1.4f else if (oscuro) 1.1f else 0.75f)
                }
                listOf(rep.top + 5 * u, rep.bottom - 6 * u).forEach { yy -> tornillo(Offset(r.center.x, yy), 1.3f * u, c(CROMO)) }
            }
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(if (izq) r.right else r.left - 3 * u, r.top), Size(3 * u, r.height))
        }
        // Las tablas: chapa hueso doblada en un labio redondo, con su filete de cromo, la pintura saltada y dos tornillos.
        d.tablas.forEachIndexed { k, r ->
            drawRect(Brush.verticalGradient(listOf(c(PANEL), c(HUESO), c(HUESO_OSC), c(BORDE)), r.top, r.bottom), r.topLeft, r.size)
            drawRect(c(CROMO), r.topLeft, Size(r.width, 1.6f * u))
            drawRect(Color.White.copy(alpha = if (noche) 0.1f else 0.35f), Offset(r.left, r.top + 1.6f * u), Size(r.width, 0.8f * u))
            for (j in 0 until 6) {
                val x = r.left + r.width * azar(k * 7 + 3, j)
                val y = r.top + r.height * (0.3f + 0.5f * azar(k * 7 + 4, j))
                val desconchon = Rect(x, y, x + (2f + 4f * azar(k * 7 + 5, j)) * u, y + (1.2f + 1.6f * azar(k * 7 + 6, j)) * u)
                drawPath(contornoIrregular(desconchon, k * 31 + j, 6, 0.3f), c(ACERO).copy(alpha = 0.42f))
            }
            listOf(r.left + 6 * u, r.right - 6 * u).forEach { x -> tornillo(Offset(x, r.center.y), 1.8f * u, c(CROMO)) }
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom), Size(r.width, 3.5f * u))
        }
        // El rótulo de cada ciclo: una placa de baquelita grabada en su marco de cromo, con un tornillo en cada punta.
        d.placas.forEach { p ->
            val r = p.r
            drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(0.6f * u, 1f * u), r.size, CornerRadius(3 * u))
            drawRoundRect(c(CROMO), r.inflate(1.2f * u).topLeft, r.inflate(1.2f * u).size, CornerRadius(3.8f * u))
            drawRoundRect(c(BAQUELITA), r.topLeft, r.size, CornerRadius(3 * u))
            drawRoundRect(Color.White.copy(alpha = 0.07f), r.topLeft, Size(r.width, r.height * 0.42f), CornerRadius(3 * u))
            listOf(r.left + r.height * 0.55f, r.right - r.height * 0.55f).forEach { x -> tornillo(Offset(x, r.center.y), r.height * 0.17f, c(CROMO)) }
            val txt = p.texto.uppercase()
            val caja = Rect(r.left + r.height * 1.1f, r.top, r.right - r.height * 1.1f, r.bottom)
            textoEn(medidor, txt, caja.translate(0f, 0.6f * u), MONO, Color.Black.copy(alpha = 0.6f), 9.5f, espaciado = 1.2f, minimo = 5f)
            textoEn(medidor, txt, caja, MONO, if (noche) mezcla(HUESO, PILOTO, 0.35f) else c(HUESO), 9.5f, espaciado = 1.2f, minimo = 5f)
        }
        corona(d.corona, medidor, oscuro, noche)
        // La base: el zócalo de chapa con sus rejillas de aire, el piso de goma de la sala con los reflejos del mueble y, en el
        // suelo, el rollo de cinta del teletipo que se fue desenrollando.
        val b = d.base
        val piso = b.top + b.height * 0.58f
        drawRect(Brush.verticalGradient(listOf(c(HUESO), c(HUESO_OSC), c(BORDE)), b.top, piso), b.topLeft, Size(b.width, piso - b.top))
        drawRect(c(CROMO), b.topLeft, Size(b.width, 1.4f * u))
        listOf(0.14f, 0.42f, 0.7f).forEach { f ->
            val rj = Rect(b.left + b.width * f, b.top + (piso - b.top) * 0.28f, b.left + b.width * (f + 0.16f), b.top + (piso - b.top) * 0.8f)
            drawRoundRect(c(ACERO).copy(alpha = 0.6f), rj.topLeft, rj.size, CornerRadius(rj.height / 2))
            var gx = rj.left + rj.height * 0.6f
            while (gx < rj.right - rj.height * 0.5f) { drawLine(c(BAQUELITA), Offset(gx, rj.top + 1.2f * u), Offset(gx, rj.bottom - 1.2f * u), 1.4f * u, StrokeCap.Round); gx += 3.4f * u }
        }
        // El piso de hormigón, con sus manchas, la franja de peligro al pie del mueble y el cable grueso que cruza.
        drawRect(Brush.verticalGradient(listOf(l(mezcla(LOSA, Color.Black, 0.25f)), l(LOSA)), piso, h), Offset(0f, piso), Size(w, h - piso))
        for (k in 0 until 7) drawOval(l(HUMEDAD).copy(alpha = 0.18f + 0.12f * azar(53, k)), Offset(w * azar(57, k), piso + (h - piso) * (0.3f + 0.5f * azar(59, k))),
            Size((8 + 22 * azar(61, k)) * u, (1.5f + 2f * azar(67, k)) * u))
        drawRect(Color.Black.copy(alpha = 0.45f), Offset(0f, piso), Size(w, 2 * u))
        with(TrazosFrente) {
            franjaPeligro(Rect(0f, piso + 2 * u, w, piso + 6 * u), c(PELIGRO), c(BAQUELITA), 7 * u)
            cableGrueso(Path().apply { moveTo(-3 * u, h - 3 * u); cubicTo(w * 0.15f, h - 9 * u, w * 0.3f, h + 1 * u, w * 0.46f, h - 5 * u); cubicTo(w * 0.56f, h - 8 * u, w * 0.62f, piso + 8 * u, w * 0.7f, piso + 4 * u) },
                3.2f * u, c(BAQUELITA), if (noche) 0.3f else if (oscuro) 0.5f else 1f, 0.4f)
        }
        val rollo = Offset(w * 0.86f, piso + (h - piso) * 0.55f)
        val tira = Rect(w * 0.5f, rollo.y - 2.4f * u, rollo.x, rollo.y + 2.4f * u)
        rotate(-3f, rollo) { cintaPerforada(tira, c(PAPEL), c(BAQUELITA).copy(alpha = 0.7f), 77) }
        for (k in 0 until 4) drawOval(c(mezcla(PAPEL, HUESO_OSC, k * 0.2f)), Offset(rollo.x - (13 - k * 2.5f) * u, rollo.y - (4.6f - k * 0.9f) * u),
            Size((26 - k * 5) * u, (9.2f - k * 1.8f) * u), style = Stroke(1.6f * u))
        drawOval(Color.Black.copy(alpha = 0.5f), Offset(rollo.x - 3 * u, rollo.y - 1.1f * u), Size(6 * u, 2.2f * u))
    }

    /**
     * La consola curva sobre el archivo: la losa del techo, con sus manchas de humedad, su tubo de luz (de noche, apagado), los
     * mazos de cable y las raíces que cuelgan por las orillas; el
     * frente curvo de la consola, color hueso, con la pantalla redonda de radar al centro; a la izquierda, la fila de pilotos,
     * la de palancas y la pantalla de segmentos de la frecuencia con su perilla; a la derecha, la placa grabada del archivo y la
     * rejilla del altavoz; colgados del canto, los cascos con su cable rizado que cae por delante del estante.
     */
    private fun DrawScope.corona(r: Rect, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val w = r.width; val hh = r.height
        // La losa de hormigón del techo, con sus manchas de humedad, y el tubo de luz en su caja.
        val techo = r.top + hh * 0.15f
        drawRect(Brush.verticalGradient(listOf(lugar(mezcla(LOSA, Color.Black, 0.35f), oscuro, noche), lugar(mezcla(LOSA, Color.Black, 0.15f), oscuro, noche)), r.top, techo), r.topLeft, Size(w, techo - r.top))
        for (k in 0 until 9) drawOval(lugar(HUMEDAD, oscuro, noche).copy(alpha = 0.25f + 0.2f * azar(71, k)), Offset(r.left + w * azar(73, k), r.top + (techo - r.top) * azar(79, k)),
            Size((6 + 18 * azar(83, k)) * u, (1.4f + 2.4f * azar(89, k)) * u))
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(r.left, techo - 1.5f * u), Size(w, 1.5f * u))
        val tubo = Rect(r.left + w * 0.3f, r.top + hh * 0.03f, r.right - w * 0.3f, r.top + hh * 0.11f)
        drawRoundRect(c(HUESO_OSC), tubo.topLeft, tubo.size, CornerRadius(tubo.height / 2))
        val encendido = !noche
        drawRoundRect(if (encendido) mezcla(Color(0xFFFFF6E2), SALA, if (oscuro) 0.35f else 0f) else c(Color(0xFF6E706C)), tubo.deflate(1.5f * u).topLeft, tubo.deflate(1.5f * u).size, CornerRadius(tubo.height / 2))
        if (encendido) drawRect(Brush.verticalGradient(listOf(Color(0xFFFFF4DC).copy(alpha = if (oscuro) 0.08f else 0.16f), Color.Transparent), tubo.bottom, r.bottom), Offset(r.left, tubo.bottom), Size(w, r.bottom - tubo.bottom))
        // Los mazos de cable que bajan del techo por detrás de la consola, a los dos lados.
        listOf(r.left + w * 0.035f, r.right - w * 0.05f).forEachIndexed { i, x ->
            for (j in 0 until 3) {
                val col = c(listOf(BAQUELITA, ACERO, Color(0xFF5B4A3A))[(i + j) % 3])
                drawLine(col, Offset(x + j * 2.6f * u, r.top), Offset(x + j * 2.6f * u, r.bottom), 2.2f * u)
            }
            drawRect(c(CROMO), Offset(x - 1.6f * u, techo + hh * 0.06f), Size(8.8f * u, 3 * u))
        }
        // Las raíces que cuelgan de la losa por las orillas, por delante de los mazos.
        with(TrazosFrente) {
            listOf(Triple(r.left + w * 0.012f, hh * 0.55f, 1.8f), Triple(r.left + w * 0.06f, hh * 0.3f, 1.2f), Triple(r.right - w * 0.075f, hh * 0.42f, 1.5f)).forEachIndexed { k, (x, l, g) ->
                raiz(Offset(x, techo - 1 * u), l, g * u, lugar(RAIZ, oscuro, noche), raizLuz(oscuro, noche), 70 + k, sombra = 0.35f)
            }
        }
        // El frente curvo de la consola: el borde de arriba con las esquinas redondas y el de abajo que se abomba.
        val y0 = techo + hh * 0.04f
        val y1 = r.bottom - hh * 0.07f
        val curva = hh * 0.06f
        val izq = r.left + w * 0.015f; val der = r.right - w * 0.015f
        val rad = 13 * u
        fun forma(dy: Float) = Path().apply {
            moveTo(izq, y0 + rad + dy); quadraticTo(izq, y0 + dy, izq + rad, y0 + dy); lineTo(der - rad, y0 + dy); quadraticTo(der, y0 + dy, der, y0 + rad + dy)
            lineTo(der, y1 - curva + dy); quadraticTo(r.center.x, y1 + curva + dy, izq, y1 - curva + dy); close()
        }
        drawPath(forma(3 * u), Color.Black.copy(alpha = 0.3f))
        val frente = forma(0f)
        drawPath(frente, Brush.verticalGradient(listOf(c(PANEL), c(HUESO), c(HUESO_OSC), c(BORDE)), y0, y1 + curva))
        drawPath(frente, Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.14f), Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.14f)), izq, der))
        drawPath(frente, c(BORDE), style = Stroke(1f * u))
        drawLine(Color.White.copy(alpha = if (noche) 0.08f else 0.35f), Offset(izq + rad, y0 + 1.2f * u), Offset(der - rad, y0 + 1.2f * u), 1f * u)
        // La ranura que sigue el labio de abajo.
        drawPath(Path().apply { moveTo(izq + 4 * u, y1 - curva - 5 * u); quadraticTo(r.center.x, y1 + curva - 5 * u, der - 4 * u, y1 - curva - 5 * u) }, c(BORDE).copy(alpha = 0.8f), style = Stroke(0.9f * u))
        val alto = y1 - y0
        // La pantalla de radar, al centro.
        val rr = alto * 0.36f
        val rc = Offset(r.center.x, y0 + alto * 0.47f)
        radar(rc, rr, 300f, c(HUESO_OSC), 41, if (noche) 1.8f else if (oscuro) 1.35f else 1f)
        // A la izquierda: la fila de pilotos, la de palancas y la frecuencia con su perilla.
        val zi = izq + w * 0.05f; val zd = rc.x - rr * 1.23f - w * 0.04f
        val paso = (zd - zi) / 4f
        val luces = listOf(PILOTO, PILOTO, PILOTO_ROJO, RADAR)
        for (k in 0 until 4) {
            val x = zi + paso * (k + 0.5f)
            val prende = noche || k != 1
            piloto(Offset(x, y0 + alto * 0.17f), minOf(paso * 0.14f, 3 * u), luces[k], prende, c(CROMO), if (noche) 1.5f else if (oscuro) 1.15f else 0.8f)
            palanca(Offset(x, y0 + alto * 0.44f), minOf(paso * 0.62f, 15 * u), k % 3 != 1, c(CROMO), c(BAQUELITA))
            drawLine(c(BAQUELITA).copy(alpha = 0.5f), Offset(x - paso * 0.18f, y0 + alto * 0.62f), Offset(x + paso * 0.18f, y0 + alto * 0.62f), 0.9f * u)
        }
        val ventanilla = Rect(zi + paso * 0.1f, y0 + alto * 0.68f, zi + paso * 2.7f, y0 + alto * 0.86f)
        drawRoundRect(c(BAQUELITA), ventanilla.inflate(1.6f * u).topLeft, ventanilla.inflate(1.6f * u).size, CornerRadius(3 * u))
        drawRoundRect(Color(0xFF1A1208), ventanilla.topLeft, ventanilla.size, CornerRadius(2 * u))
        segmentos("148.30", ventanilla.deflate(2 * u), PILOTO.copy(alpha = if (noche) 1f else 0.92f), PILOTO.copy(alpha = 0.08f), halo = oscuro || noche)
        perilla(Offset(zi + paso * 3.4f, ventanilla.center.y), minOf(paso * 0.3f, ventanilla.height * 0.55f), -40f, c(CROMO), c(BAQUELITA))
        // A la derecha: la placa grabada del archivo y la rejilla del altavoz.
        val xi = rc.x + rr * 1.23f + w * 0.04f; val xd = der - w * 0.08f
        val placa = Rect(xi, y0 + alto * 0.08f, xd, y0 + alto * 0.42f)
        drawRoundRect(c(CROMO), placa.inflate(1.2f * u).topLeft, placa.inflate(1.2f * u).size, CornerRadius(3.6f * u))
        drawRoundRect(c(BAQUELITA), placa.topLeft, placa.size, CornerRadius(3 * u))
        val grabado = if (noche) mezcla(HUESO, PILOTO, 0.3f) else c(HUESO)
        textoEn(medidor, "ARCHIVO", Rect(placa.left + 4 * u, placa.top + 2 * u, placa.right - 4 * u, placa.top + placa.height * 0.56f), TITULO, grabado, 10f, espaciado = 1.4f, minimo = 5f)
        textoEn(medidor, "DESPACHOS · CÉLULA K-7", Rect(placa.left + 4 * u, placa.top + placa.height * 0.54f, placa.right - 4 * u, placa.bottom - 2 * u), MONO, grabado.copy(alpha = 0.8f), 7f, minimo = 4f)
        listOf(placa.left + 3 * u, placa.right - 3 * u).forEach { x -> tornillo(Offset(x, placa.center.y), 1.1f * u, c(CROMO)) }
        rejilla(Rect(xi, y0 + alto * 0.52f, xd - (xd - xi) * 0.32f, y0 + alto * 0.86f), c(HUESO_OSC), c(BAQUELITA).copy(alpha = 0.7f))
        piloto(Offset(xd - (xd - xi) * 0.14f, y0 + alto * 0.69f), 2.4f * u, PILOTO_ROJO, noche, c(CROMO), 1.4f)
        // De noche, el radar baña de verde la consola.
        if (noche) mancha(rc, rr * 3.2f, RADAR.copy(alpha = 0.1f))
        // Los cascos colgados del gancho, en el canto derecho, con el cable rizado que cae por delante del estante.
        val gancho = Offset(der - w * 0.01f, y0 + alto * 0.55f)
        drawLine(c(CROMO), Offset(gancho.x + 4 * u, gancho.y), gancho, 1.6f * u, StrokeCap.Round)
        cascos(Offset(gancho.x - 1 * u, gancho.y + 9 * u), 9 * u, Offset(gancho.x - 10 * u, r.bottom + hh * 0.5f), c(BAQUELITA), c(CROMO))
    }

    // =================================================================
    //  Cada entrada: el dosier, la carpeta de despacho o el cartucho
    // =================================================================

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        when (clase(v.semilla)) {
            0 -> dosier(v, medidor, oscuro)
            1 -> despacho(v, medidor, oscuro)
            else -> cartucho(v, medidor, oscuro)
        }
    }

    /** La trama de la lona: hilos finos a lo largo y a lo ancho dentro de [r]. */
    private fun DrawScope.trama(r: Rect, color: Color, paso: Float) {
        val u = density
        var x = r.left
        while (x < r.right) { drawLine(color, Offset(x, r.top), Offset(x, r.bottom), 0.4f * u); x += paso }
        var y = r.top
        while (y < r.bottom) { drawLine(color, Offset(r.left, y), Offset(r.right, y), 0.4f * u); y += paso }
    }

    /**
     * El dosier, de lomo: la lona acero con su trama, los dos pliegues y el canto en luz; arriba asoma la foto engrapada al
     * primer folio, y entre las tapas se ven las hojas (más cuanto más escribiste). El rótulo ámbar lleva el día y el ciclo; más
     * abajo, la grapa de cromo que sujeta la ficha, el sello de la célula en tinta (de noche, la luna) y la cantonera de metal.
     */
    private fun DrawScope.dosier(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = LONAS[Math.floorMod(v.semilla, LONAS.size)]
        val lona = c(if (v.tono != null) mezcla(base, v.tono, 0.16f) else base)
        val hoja = papel(oscuro, v.noche)
        val tinta = PIZARRA
        val arriba = 8 * u
        // La foto que asoma, un poco torcida.
        val fw = minOf(14 * u, w * 0.5f)
        val fx = w * 0.14f + (w - fw - w * 0.28f) * azar(v.semilla, 11)
        rotate(-6f + 12f * azar(v.semilla, 12), Offset(fx + fw / 2, arriba)) {
            foto(Rect(fx, 0.8f * u, fx + fw, arriba + 5 * u), v.semilla, oscuro, v.noche)
        }
        // Las hojas en el canto de arriba.
        val hojas = 1 + (v.lleno * 5).toInt()
        for (j in 0 until hojas) drawLine(mezcla(hoja, Color.Black, 0.05f * j), Offset(w * 0.08f, arriba - 0.4f * u + j * 1.1f * u), Offset(w * 0.92f, arriba - 0.4f * u + j * 1.1f * u), 0.8f * u)
        // El lomo de lona.
        val lomo = Rect(0f, arriba, w, h)
        drawRoundRect(lona, lomo.topLeft, lomo.size, CornerRadius(2.2f * u))
        clipRect(lomo.left, lomo.top, lomo.right, lomo.bottom) { trama(lomo, Color.White.copy(alpha = 0.05f), 1.6f * u) }
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.16f)), 0f, w), lomo.topLeft, lomo.size)
        listOf(0.14f, 0.86f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.28f), Offset(w * f, lomo.top), Offset(w * f, h), 0.7f * u) }
        // El rótulo ámbar con el día y el ciclo.
        val ro = Rect(w * 0.16f, lomo.top + lomo.height * 0.06f, w * 0.84f, lomo.top + lomo.height * 0.34f)
        drawRect(Color.Black.copy(alpha = 0.25f), ro.topLeft + Offset(0.5f * u, 0.8f * u), ro.size)
        drawRect(ambar(oscuro, v.noche), ro.topLeft, ro.size)
        drawRect(Color.White.copy(alpha = 0.18f), ro.topLeft, Size(ro.width, ro.height * 0.16f))
        textoEn(medidor, v.dia, Rect(ro.left, ro.top + ro.height * 0.06f, ro.right, ro.top + ro.height * 0.66f), MONO, tinta, 14f, peso = FontWeight.Medium, minimo = 7f)
        drawLine(tinta.copy(alpha = 0.5f), Offset(ro.left + 1.5f * u, ro.top + ro.height * 0.67f), Offset(ro.right - 1.5f * u, ro.top + ro.height * 0.67f), 0.5f * u)
        textoEn(medidor, cicloCorto(v.mes), Rect(ro.left + 0.5f * u, ro.top + ro.height * 0.68f, ro.right - 0.5f * u, ro.bottom - 0.5f * u), MONO, tinta.copy(alpha = 0.85f), 6.5f, minimo = 4f)
        // La ficha de papel sujeta con la grapa, y el sello de la célula.
        val fi = Rect(w * 0.22f, lomo.top + lomo.height * 0.42f, w * 0.78f, lomo.top + lomo.height * 0.52f)
        drawRect(hoja, fi.topLeft, fi.size)
        drawLine(tinta.copy(alpha = 0.35f), Offset(fi.left + 1.5f * u, fi.center.y), Offset(fi.right - 1.5f * u, fi.center.y), 0.5f * u)
        grapa(Offset(fi.left + fi.width * 0.2f, fi.top + 1 * u), Offset(fi.right - fi.width * 0.2f, fi.top + 1 * u))
        val so = Offset(w / 2, lomo.top + lomo.height * 0.7f)
        rotate(-14f + 28f * azar(v.semilla, 13), so) { selloGoma(null, so, minOf(w * 0.32f, 9 * u), c(PILOTO).copy(alpha = 0.75f), lona, v.semilla, v.noche) }
        // La cantonera de metal, con dos remaches.
        val ca = Rect(0f, h - lomo.height * 0.09f, w, h)
        drawRect(c(CROMO), ca.topLeft, ca.size)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(0f, ca.top), Size(w, 0.8f * u))
        listOf(w * 0.28f, w * 0.72f).forEach { x -> drawCircle(c(BORDE), 1.1f * u, Offset(x, ca.center.y)); drawCircle(Color.White.copy(alpha = 0.5f), 0.4f * u, Offset(x - 0.3f * u, ca.center.y - 0.3f * u)) }
    }

    /**
     * La carpeta de despacho, de lomo: cartón hueso con el filo de acero, la cinta perforada del teletipo que asoma por arriba
     * (más larga cuanto más escribiste), la correa de baquelita con su hebilla que la ciñe, «CIFRADO» estampado a lo largo en
     * su recuadro y, abajo, la etiqueta con el día, el ciclo y el piloto del color del ánimo (de noche, una luna).
     */
    private fun DrawScope.despacho(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val carton = c(CARTONES[Math.floorMod(v.semilla, CARTONES.size)])
        val hoja = papel(oscuro, v.noche)
        val tinta = PIZARRA
        // La cinta del teletipo que sale por arriba, doblada en la punta.
        val sale = (4f + 12f * v.lleno) * u
        val arriba = 14 * u
        val cx = w * (0.3f + 0.25f * azar(v.semilla, 21))
        val cinta = Rect(cx, arriba - sale, cx + 6 * u, arriba + 2 * u)
        rotate(-4f + 8f * azar(v.semilla, 22), Offset(cinta.center.x, cinta.bottom)) {
            cintaPerforada(cinta, mezcla(hoja, HUESO_OSC, 0.25f), c(PIZARRA).copy(alpha = 0.7f), v.semilla, vertical = true)
            drawRect(Color.Black.copy(alpha = 0.3f), cinta.topLeft, cinta.size, style = Stroke(0.5f * u))
            drawRect(mezcla(hoja, Color.Black, 0.12f), Offset(cinta.left, cinta.top), Size(cinta.width, 1.4f * u))
        }
        // El cuerpo de cartón, con el filo de acero de los dos lados.
        val lomo = Rect(0f, arriba, w, h)
        drawRoundRect(carton, lomo.topLeft, lomo.size, CornerRadius(1.6f * u))
        for (k in 0 until 12) {
            val y = lomo.top + lomo.height * azar(v.semilla, 30 + k)
            drawLine(Color.Black.copy(alpha = 0.06f), Offset(w * 0.5f * azar(v.semilla, 40 + k), y), Offset(w * (0.5f + 0.5f * azar(v.semilla, 50 + k)), y + 0.4f * u), 0.6f * u)
        }
        drawRect(c(ACERO), lomo.topLeft, Size(w * 0.1f, lomo.height))
        drawRect(c(ACERO), Offset(w * 0.9f, lomo.top), Size(w * 0.1f, lomo.height))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.2f), Color.Transparent, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.12f)), 0f, w), lomo.topLeft, lomo.size)
        // La correa de baquelita con su hebilla de cromo.
        val cy = lomo.top + lomo.height * 0.2f
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, cy + 1 * u), Size(w, 5 * u))
        drawRect(c(BAQUELITA), Offset(0f, cy), Size(w, 5 * u))
        drawRect(Color.White.copy(alpha = 0.14f), Offset(0f, cy + 0.6f * u), Size(w, 0.8f * u))
        val he = Rect(w * 0.36f, cy - 1.2f * u, w * 0.64f, cy + 6.2f * u)
        drawRoundRect(c(CROMO), he.topLeft, he.size, CornerRadius(1.2f * u), style = Stroke(1.1f * u))
        drawLine(c(CROMO), Offset(he.center.x, he.top), Offset(he.center.x, he.bottom), 0.8f * u)
        // «CIFRADO», estampado a lo largo en su recuadro de tinta.
        val es = Rect(w * 0.22f, lomo.top + lomo.height * 0.32f, w * 0.78f, lomo.top + lomo.height * 0.68f)
        drawRoundRect(c(AMBAR).copy(alpha = 0.8f), es.topLeft, es.size, CornerRadius(1.5f * u), style = Stroke(0.9f * u))
        textoEn(medidor, if (v.noche) "ESCUCHA" else "CIFRADO", es.deflate(1.2f * u), MONO, c(AMBAR).copy(alpha = 0.9f), 7.5f, vertical = true, espaciado = 1f, minimo = 4f)
        // La etiqueta con el día, el ciclo y el piloto del ánimo.
        val et = Rect(w * 0.14f, lomo.top + lomo.height * 0.73f, w * 0.86f, h - lomo.height * 0.04f)
        drawRect(Color.Black.copy(alpha = 0.2f), et.topLeft + Offset(0.5f * u, 0.8f * u), et.size)
        drawRect(hoja, et.topLeft, et.size)
        drawRect(tinta.copy(alpha = 0.6f), et.topLeft, et.size, style = Stroke(0.5f * u))
        textoEn(medidor, v.dia, Rect(et.left, et.top + et.height * 0.04f, et.right, et.top + et.height * 0.62f), MONO, tinta, 12f, peso = FontWeight.Medium, minimo = 6f)
        textoEn(medidor, cicloCorto(v.mes), Rect(et.left + 0.5f * u, et.top + et.height * 0.6f, et.right - 0.5f * u, et.bottom - 0.5f * u), MONO, tinta.copy(alpha = 0.8f), 6f, minimo = 3.5f)
        val pc = Offset(et.right - 2.4f * u, et.top + 2.4f * u)
        if (v.noche) drawPath(luna(pc, 1.6f * u), c(PIZARRA))
        else if (v.tono != null) { drawCircle(v.tono.copy(alpha = 0.35f), 2.6f * u, pc); drawCircle(v.tono, 1.4f * u, pc) }
    }

    /**
     * El cartucho de datos, de canto: la carcasa de baquelita con las esquinas redondas y la muesca de lectura arriba, la
     * pestaña ámbar contra escritura, la etiqueta hueso rotulada con su franja (del color del ánimo, si lo hay; de noche, con
     * la luna), el día y el ciclo, la escala de cinco rayas que se encienden con lo escrito, las estrías del agarre y la placa
     * de cromo del fondo con sus tornillos.
     */
    private fun DrawScope.cartucho(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val cc = c(CARCASAS[Math.floorMod(v.semilla, CARCASAS.size)])
        val hoja = papel(oscuro, v.noche)
        val tinta = PIZARRA
        val cuerpo = Path().apply { addRoundRect(RoundRect(Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 0.5f * u), CornerRadius(3.5f * u))) }
        val muesca = Path().apply { addRect(Rect(w * 0.38f, -1f, w * 0.62f, 3.6f * u)) }
        val carcasa = Path().apply { op(cuerpo, muesca, PathOperation.Difference) }
        drawPath(carcasa, Brush.horizontalGradient(listOf(mezcla(cc, Color.White, 0.14f), cc, mezcla(cc, Color.Black, 0.3f)), 0f, w))
        drawPath(carcasa, Color.Black.copy(alpha = 0.4f), style = Stroke(0.7f * u))
        drawLine(Color.White.copy(alpha = 0.16f), Offset(2.5f * u, 1.4f * u), Offset(w * 0.36f, 1.4f * u), 0.7f * u)
        // La pestaña ámbar contra escritura, en su ranura.
        drawRoundRect(Color.Black.copy(alpha = 0.5f), Offset(w * 0.1f, 5 * u), Size(w * 0.2f, 3.6f * u), CornerRadius(0.8f * u))
        drawRoundRect(PILOTO, Offset(w * 0.1f + (if (v.lleno > 0.6f) w * 0.08f else 0f), 5.3f * u), Size(w * 0.12f, 3f * u), CornerRadius(0.8f * u))
        // La etiqueta rotulada.
        val et = Rect(w * 0.12f, h * 0.17f, w * 0.88f, h * 0.68f)
        drawRect(Color.Black.copy(alpha = 0.3f), et.topLeft + Offset(0.4f * u, 0.6f * u), et.size)
        drawRect(hoja, et.topLeft, et.size)
        val franja = Rect(et.left, et.top, et.right, et.top + et.height * 0.2f)
        drawRect(if (v.tono != null) mezcla(PILOTO, v.tono, 0.65f) else ambar(oscuro, v.noche), franja.topLeft, franja.size)
        if (v.noche) drawPath(luna(franja.center, franja.height * 0.36f), c(PIZARRA))
        else textoEn(medidor, "K-7", franja, MONO, tinta.copy(alpha = 0.85f), 5.5f, minimo = 3f)
        textoEn(medidor, v.dia, Rect(et.left, franja.bottom, et.right, et.top + et.height * 0.62f), MONO, tinta, 13f, peso = FontWeight.Medium, minimo = 6f)
        textoEn(medidor, cicloCorto(v.mes), Rect(et.left + 0.5f * u, et.top + et.height * 0.6f, et.right - 0.5f * u, et.top + et.height * 0.8f), MONO, tinta.copy(alpha = 0.8f), 6f, minimo = 3.5f)
        val llenas = (1 + (v.lleno * 4.99f).toInt()).coerceIn(1, 5)
        val ra = (et.width - 3 * u) / 5f
        for (j in 0 until 5) drawRect(if (j < llenas) c(AMBAR) else tinta.copy(alpha = 0.15f), Offset(et.left + 1.5f * u + j * ra + 0.4f * u, et.top + et.height * 0.84f), Size(ra - 0.8f * u, et.height * 0.1f))
        // Las estrías del agarre.
        var y = h * 0.71f
        while (y < h * 0.79f) { drawLine(Color.Black.copy(alpha = 0.45f), Offset(w * 0.16f, y), Offset(w * 0.84f, y), 0.8f * u); drawLine(Color.White.copy(alpha = 0.1f), Offset(w * 0.16f, y + 0.8f * u), Offset(w * 0.84f, y + 0.8f * u), 0.5f * u); y += 2 * u }
        // La placa de cromo del fondo.
        val pl = Rect(1.5f * u, h * 0.82f, w - 1.5f * u, h - 2.5f * u)
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(c(CROMO), Color.White, 0.25f), c(CROMO), mezcla(c(CROMO), Color.Black, 0.25f)), pl.top, pl.bottom), pl.topLeft, pl.size, CornerRadius(1.2f * u))
        listOf(pl.left + 3 * u, pl.right - 3 * u).forEach { x -> tornillo(Offset(x, pl.center.y), minOf(1.3f * u, pl.height * 0.3f), c(CROMO)) }
    }

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
        // La sombra sobre la consola.
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.24f), Offset(cuerpo.left + 3 * u, cuerpo.top + 4 * u), cuerpo.size, CornerRadius(6 * u))
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
            drawRoundRect(Brush.verticalGradient(listOf(mezcla(lona, Color.White, 0.08f), lona, mezcla(lona, Color.Black, 0.18f)), cara.top, cara.bottom), cara.topLeft, cara.size, CornerRadius(6 * u))
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
            if (a > 0f) drawRoundRect(Color.Black.copy(alpha = 0.45f * (1f - co)), cara.topLeft, cara.size, CornerRadius(6 * u))
        }
        // El lomo doblado, que no se mueve.
        val lomo = Rect(cuerpo.left, cuerpo.top, x0, cuerpo.bottom)
        drawRoundRect(mezcla(lona, Color.Black, 0.2f), lomo.topLeft, lomo.size, CornerRadius(4 * u))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(lomo.left + lomo.width * 0.3f, lomo.top + 2 * u), Size(lomo.width * 0.2f, lomo.height - 4 * u))
        drawLine(Color.Black.copy(alpha = 0.45f), Offset(x0, cuerpo.top + 2 * u), Offset(x0, cuerpo.bottom - 2 * u), 1.2f * u)
        // De noche, el resplandor verde del radar entra por la esquina.
        if (e.noche) clipPath(Path().apply { addRoundRect(RoundRect(cuerpo, CornerRadius(6 * u))) }) { mancha(Offset(cuerpo.right, cuerpo.top), w * 0.45f, RADAR.copy(alpha = 0.14f)) }
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
    //  La libreta de momentos: el cartucho de datos en la consola
    // =================================================================

    override val altoLibreta: Dp = 268.dp

    /** El ángulo de la tapa del cartucho según cuánto se ha abierto: rápido al principio, para que a media apertura ya se vea por dentro. */
    private fun giro(abierto: Float): Float = 0.92f * PI.toFloat() * Math.pow(abierto.coerceIn(0f, 1f).toDouble(), 0.7).toFloat()

    /** Un reloj de pared de la sala, con su aro de cromo, sus marcas y sus manecillas en [hora] (0 a 12, con fracción). */
    private fun DrawScope.reloj(o: Offset, r: Float, hora: Float, esfera: Color, aro: Color, tinta: Color) {
        val u = density
        drawCircle(Color.Black.copy(alpha = 0.3f), r * 1.2f, Offset(o.x + 0.8f * u, o.y + 1.2f * u))
        drawCircle(aro, r * 1.18f, o)
        drawCircle(esfera, r, o)
        for (k in 0 until 12) {
            val a = k * PI.toFloat() / 6f
            drawLine(tinta.copy(alpha = 0.7f), Offset(o.x + cos(a) * r * (if (k % 3 == 0) 0.7f else 0.8f), o.y + sin(a) * r * (if (k % 3 == 0) 0.7f else 0.8f)), Offset(o.x + cos(a) * r * 0.92f, o.y + sin(a) * r * 0.92f), 0.7f * u)
        }
        val ah = (hora / 12f * 2f * PI.toFloat()) - PI.toFloat() / 2f
        val am = ((hora % 1f) * 2f * PI.toFloat()) - PI.toFloat() / 2f
        drawLine(tinta, o, Offset(o.x + cos(ah) * r * 0.5f, o.y + sin(ah) * r * 0.5f), 1.4f * u, StrokeCap.Round)
        drawLine(tinta, o, Offset(o.x + cos(am) * r * 0.78f, o.y + sin(am) * r * 0.78f), 0.9f * u, StrokeCap.Round)
        drawCircle(PILOTO_ROJO, 1.2f * u, o)
        drawArc(Color.White.copy(alpha = 0.25f), 200f, 60f, false, Offset(o.x - r * 0.85f, o.y - r * 0.85f), Size(r * 1.7f, r * 1.7f), style = Stroke(0.8f * u))
    }

    /**
     * El puesto de radar de la base escondida en ruinas, de noche o de día. Contra el muro de sillares, bajo la losa con su tubo
     * de cobre y las raíces que cuelgan: la torre de consola oscura con la pantalla redonda de radar y su fila de botones
     * cuadrados, la consola oscura de los dos relojes (el de la capital y el de aquí) con su botonera, la carta estelar grabada
     * en un panel de vidrio iluminado por el canto, con sus mundos, la ruta de salto y el blanco, y los cascos colgados de su
     * gancho. Abajo, el panel de la consola oscura con sus palancas, sus pilotos, la frecuencia en segmentos y la perilla; sobre
     * la mesa de la consola blanca curva, la lámpara de cúpula, la libreta de claves de un solo uso con su lápiz,
     * los cartuchos de repuesto (más cuantos más bloques) y el cartucho de la célula: baquelita negra, la muesca de lectura,
     * la pestaña ámbar contra escritura, las estrías, la placa de cromo y la etiqueta hueso rotulada «CARTUCHO K-7» con la
     * cuenta. Engorda con los bloques. Al abrirse, la tapa se levanta sobre la bisagra de atrás y deja ver los dos carretes,
     * la cinta (más cuanto más bloques) que corre por delante y la tarjeta del índice. El frente de la mesa son molduras
     * redondas, como un mostrador anillado, con la franja de peligro al pie. En oscuro, la base se hunde en su penumbra
     * verdosa y quedan el verde del radar y del vidrio, la lámpara y el ámbar de los pilotos sobre la mesa.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        // Lo que queda lejos de los pilotos: en oscuro, se hunde de verdad.
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF07090A), 0.5f) else c(x)
        val a = e.abierto.coerceIn(0f, 1f)
        val hoja = papel(oscuro, false)
        val tinta = PIZARRA
        val fondoY = h * 0.42f
        val panelY = h * 0.52f
        val frenteY = h * 0.85f
        // ---- El muro de sillares de las ruinas, bajo la losa del techo, con el tubo de cobre y la sombra de la losa.
        // Lo del lugar se hunde en la penumbra verdosa de la base; en oscuro, más lejos de los pilotos, todavía más.
        fun l(x: Color) = if (oscuro) mezcla(lugar(x, true, false), MADRUGADA, 0.3f) else x
        val losa = h * 0.045f
        with(TrazosFrente) {
            muroSillares(Rect(0f, losa, w, fondoY), 14 * u, l(mezcla(PIEDRA, HUESO_OSC, 0.2f)), l(PIEDRA_SOMBRA), HUMEDAD.copy(alpha = if (oscuro) 0.6f else 0.45f), l(MUSGO),
                e.semilla + 5, if (oscuro) 0.35f else 0.9f)
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.3f), Color.Transparent), losa, losa + 16 * u), Offset(0f, losa), Size(w, 16 * u))
        drawRect(Brush.verticalGradient(listOf(l(mezcla(LOSA, Color.Black, 0.3f)), l(mezcla(LOSA, Color.Black, 0.1f))), 0f, losa), Offset.Zero, Size(w, losa))
        for (k in 0 until 6) drawOval(l(HUMEDAD).copy(alpha = 0.3f), Offset(w * azar(e.semilla + 91, k), losa * azar(e.semilla + 92, k) * 0.6f),
            Size((8 + 20 * azar(e.semilla + 93, k)) * u, (1.4f + 2f * azar(e.semilla + 94, k)) * u))
        drawLine(Color.Black.copy(alpha = 0.45f), Offset(0f, losa), Offset(w, losa), 1 * u)
        with(TrazosFrente) { tubo(Offset(-2 * u, losa + 2.6f * u), Offset(w + 2 * u, losa + 2.6f * u), 3.4f * u, l(COBRE), if (oscuro) 0.25f else 1f, juntas = 70 * u) }
        listOf(0.285f, 0.535f, 0.9f).forEach { fx ->
            val a = Rect(w * fx - 1.1f * u, losa, w * fx + 1.1f * u, losa + 5.4f * u)
            drawRect(l(CONSOLA), a.topLeft, a.size)
        }
        // La torre de consola oscura con la pantalla de radar, y su fila de botones al pie.
        val torre = Rect(w * 0.03f, losa + 6 * u, w * 0.27f, fondoY + 2 * u)
        consolaOscura(torre, 6 * u, oscuro)
        val rc = Offset(torre.center.x, h * 0.215f)
        radar(rc, h * 0.1f, 300f, f(HUESO), e.semilla, if (oscuro) 1.7f else 1f)
        for (k in 0 until 5) with(TrazosFrente) {
            val col = listOf(PILOTO, VIDRIO_TRAZO, PANEL, PILOTO_ROJO, PILOTO)[k]
            botonCuadrado(Offset(torre.left + torre.width * (0.2f + 0.15f * k), fondoY - 6 * u), 3.4f * u, if (oscuro) col else mezcla(col, Color.White, 0.1f), k != 2, CONSOLA, if (oscuro) 1f else 0f)
        }
        // La consola oscura de los dos relojes (la hora de la capital y la de aquí), con su botonera debajo.
        val tablero = Rect(w * 0.295f, h * 0.075f, w * 0.51f, h * 0.38f)
        consolaOscura(tablero, 5 * u, oscuro)
        listOf(tablero.left + 4 * u, tablero.right - 4 * u).forEach { x -> listOf(tablero.top + 4 * u, tablero.bottom - 4 * u).forEach { y -> tornillo(Offset(x, y), 1.1f * u, f(CROMO)) } }
        val capital = 12f * azar(e.semilla, 5)
        listOf(Triple(w * 0.345f, "CAPITAL", 0f), Triple(w * 0.46f, "AQUÍ", 5f)).forEach { (x, nombre, desfase) ->
            val o = Offset(x, h * 0.15f)
            reloj(o, 12 * u, (capital + desfase) % 12f, c(PANEL), c(CROMO), c(PIZARRA))
            val pl = Rect(x - 16 * u, o.y + 17 * u, x + 16 * u, o.y + 25 * u)
            drawRoundRect(Color.Black.copy(alpha = 0.5f), pl.topLeft, pl.size, CornerRadius(1.5f * u))
            textoEn(medidor, nombre, pl, MONO, c(HUESO), 6f, espaciado = 0.6f, minimo = 3.5f)
        }
        for (fila in 0 until 2) for (k in 0 until 6) with(TrazosFrente) {
            val col = listOf(PILOTO, VIDRIO_TRAZO, PANEL, PILOTO, PILOTO_ROJO, VIDRIO_TRAZO)[(k + fila * 3) % 6]
            botonCuadrado(Offset(tablero.left + tablero.width * (0.17f + 0.132f * k), tablero.bottom - (11 - fila * 5.4f) * u), 3.4f * u,
                if (oscuro) col else mezcla(col, Color.White, 0.1f), azar(e.semilla + fila, k + 20) > 0.3f, CONSOLA, if (oscuro) 1f else 0f)
        }
        // La carta estelar grabada en un panel de vidrio, iluminado por el canto: los mundos, la ruta de salto y el blanco.
        cartaVidrio(Rect(w * 0.55f, h * 0.075f, w * 0.88f, h * 0.38f), medidor, oscuro)
        // Las raíces que cuelgan de la losa por delante del muro y de los tableros.
        with(TrazosFrente) {
            listOf(Triple(0.015f, 0.66f, 2.2f), Triple(0.05f, 0.3f, 1.3f), Triple(0.24f, 0.22f, 1.2f), Triple(0.28f, 0.46f, 1.8f), Triple(0.525f, 0.8f, 2.4f), Triple(0.545f, 0.34f, 1.3f), Triple(0.985f, 0.55f, 2f)).forEachIndexed { k, (fx, fl, g) ->
                raiz(Offset(w * fx, losa - 1 * u), fondoY * fl, g * u, l(RAIZ), raizLuz(oscuro, false), e.semilla * 3 + k, sombra = 0.35f)
            }
        }
        // Los cascos, colgados de su gancho; el cable baja hasta la consola.
        val gancho = Offset(w * 0.935f, h * 0.1f)
        drawLine(f(CROMO), Offset(gancho.x, gancho.y - 4 * u), gancho, 1.8f * u, StrokeCap.Round)
        cascos(Offset(gancho.x, gancho.y + 12 * u), 11 * u, Offset(w * 0.9f, fondoY), f(BAQUELITA), f(CROMO))

        // ---- El panel alto de la consola oscura, con sus palancas, sus pilotos, la frecuencia y la perilla.
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, fondoY - 3 * u), Size(w, 3 * u))
        drawRect(Brush.verticalGradient(listOf(c(CONSOLA_LUZ), c(CONSOLA), c(mezcla(CONSOLA, Color.Black, 0.3f))), fondoY, panelY), Offset(0f, fondoY), Size(w, panelY - fondoY))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.2f), Offset(0f, fondoY + 1 * u), Offset(w, fondoY + 1 * u), 1f * u)
        drawLine(Color.Black.copy(alpha = 0.6f), Offset(0f, panelY), Offset(w, panelY), 1.2f * u)
        val my = (fondoY + panelY) / 2f
        perilla(Offset(w * 0.05f, my), (panelY - fondoY) * 0.3f, 30f, c(CROMO), c(BAQUELITA))
        val frec = Rect(w * 0.42f, fondoY + (panelY - fondoY) * 0.2f, w * 0.58f, panelY - (panelY - fondoY) * 0.2f)
        drawRoundRect(c(BAQUELITA), frec.inflate(1.4f * u).topLeft, frec.inflate(1.4f * u).size, CornerRadius(2.5f * u))
        drawRoundRect(Color(0xFF1A1208), frec.topLeft, frec.size, CornerRadius(2 * u))
        segmentos("148.30", frec.deflate(1.6f * u), PILOTO, PILOTO.copy(alpha = 0.08f), halo = oscuro)
        listOf(0.12f, 0.18f, 0.24f, 0.3f, 0.36f, 0.64f, 0.7f, 0.76f, 0.82f, 0.88f, 0.94f).forEachIndexed { k, fx ->
            val x = w * fx
            if (k % 2 == 0) palanca(Offset(x, my), (panelY - fondoY) * 0.55f, azar(e.semilla, 50 + k) > 0.4f, c(CROMO), c(BAQUELITA))
            else piloto(Offset(x, my), 2.2f * u, if (k == 5) RADAR else if (k == 7) PILOTO_ROJO else PILOTO, k != 3, c(CROMO), if (oscuro) 1.3f else 0.7f)
        }

        // ---- La mesa de la consola blanca curva, con la lámpara de cúpula al fondo, y su frente de molduras redondas, como
        // un mostrador anillado; al pie, la franja de peligro del piso.
        val cubierta = Path().apply { moveTo(w * 0.02f, panelY); lineTo(w * 0.98f, panelY); lineTo(w * 1.01f, frenteY); lineTo(-w * 0.01f, frenteY); close() }
        drawRect(c(HUESO_OSC), Offset(0f, frenteY), Size(w, h - frenteY))
        drawPath(cubierta, Brush.verticalGradient(listOf(c(HUESO_OSC), c(HUESO), c(PANEL)), panelY, frenteY))
        val lampara = Offset(w * 0.885f, panelY + (frenteY - panelY) * 0.3f)
        clipPath(cubierta) {
            for (k in 0 until 30) {
                val y = panelY + (frenteY - panelY) * azar(71, k)
                val x = w * azar(73, k)
                drawLine(Color.Black.copy(alpha = 0.04f + 0.04f * azar(79, k)), Offset(x, y), Offset(x + (10 + 30 * azar(83, k)) * u, y + 0.5f * u), 0.6f * u)
            }
            if (oscuro) mancha(Offset(w * 0.5f, frenteY - (frenteY - panelY) * 0.4f), w * 0.5f, PILOTO.copy(alpha = 0.16f))
            mancha(Offset(lampara.x, lampara.y + 4 * u), w * 0.22f, Color(0xFFFFF4DE).copy(alpha = if (oscuro) 0.22f else 0.3f))
        }
        with(TrazosFrente) {
            drawOval(Color.Black.copy(alpha = 0.25f), Offset(lampara.x - 7 * u, lampara.y - 1 * u), Size(16 * u, 3.4f * u))
            lamparaCupula(Offset(lampara.x, lampara.y - 20 * u), 8 * u, 20 * u, c(PIZARRA), if (oscuro) mezcla(PANEL, PENUMBRA, 0.15f) else PANEL, if (oscuro) 1f else 0.55f)
        }
        val moldura = 5.2f * u
        for (k in 0 until 3) {
            val y = frenteY + k * moldura
            drawRect(Brush.verticalGradient(listOf(c(PANEL), c(HUESO), c(mezcla(HUESO_OSC, BORDE, 0.6f))), y, y + moldura), Offset(0f, y), Size(w, moldura))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.1f else 0.55f), Offset(0f, y + 0.9f * u), Offset(w, y + 0.9f * u), 0.7f * u)
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.28f), Offset(0f, y + moldura), Offset(w, y + moldura), 0.8f * u)
        }
        val bajo = frenteY + 3 * moldura
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, bajo), Size(w, 2.5f * u))
        listOf(0.18f, 0.5f, 0.82f).forEach { fx -> drawLine(Color.Black.copy(alpha = 0.18f), Offset(w * fx, bajo + 2.5f * u), Offset(w * fx, h), 0.6f * u) }
        if (h - bajo > 10 * u) with(TrazosFrente) { franjaPeligro(Rect(0f, h - 5.5f * u, w, h - 1.5f * u), c(PELIGRO), c(BAQUELITA), 7 * u) }

        // ---- La libreta de claves de un solo uso, con su lápiz.
        val lc = Offset(w * 0.13f, frenteY - 30 * u)
        rotate(-7f, lc) {
            val lr = Rect(lc.x - 22 * u, lc.y - 20 * u, lc.x + 22 * u, lc.y + 18 * u)
            drawRect(Color.Black.copy(alpha = 0.22f), lr.topLeft + Offset(1.5f * u, 2 * u), lr.size)
            drawRect(hoja, lr.topLeft, lr.size)
            drawRect(c(BAQUELITA), lr.topLeft, Size(lr.width, 5 * u))
            for (j in 0 until 5) {
                val y = lr.top + 10 * u + j * 6 * u
                for (g in 0 until 3) drawLine(tinta.copy(alpha = if (j < 2) 0.18f else 0.45f), Offset(lr.left + 4 * u + g * 13 * u, y), Offset(lr.left + 14 * u + g * 13 * u, y), 1.1f * u,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * u, 0.8f * u)))
                if (j < 2) drawLine(c(AMBAR).copy(alpha = 0.6f), Offset(lr.left + 3 * u, y + 0.5f * u), Offset(lr.right - 3 * u, y - 0.5f * u), 0.7f * u)
            }
        }
        rotate(18f, Offset(lc.x + 6 * u, lc.y + 4 * u)) {
            val lx = lc.x - 18 * u; val ly = lc.y + 4 * u
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(lx + 1 * u, ly + 2 * u), Size(44 * u, 3.4f * u))
            drawRect(c(Color(0xFF4F7A5A)), Offset(lx, ly), Size(38 * u, 3.4f * u))
            drawRect(c(CROMO), Offset(lx - 3 * u, ly), Size(3 * u, 3.4f * u))
            drawPath(Path().apply { moveTo(lx + 38 * u, ly); lineTo(lx + 45 * u, ly + 1.7f * u); lineTo(lx + 38 * u, ly + 3.4f * u); close() }, c(Color(0xFFE6CFA4)))
            drawCircle(c(PIZARRA), 0.8f * u, Offset(lx + 44.4f * u, ly + 1.7f * u))
        }

        // ---- Los cartuchos de repuesto, apilados a la derecha (más cuantos más bloques).
        val repuestos = if (e.hojas <= 0) 0 else (1 + e.hojas / 30).coerceAtMost(3)
        for (k in 0 until repuestos) {
            val rr = Rect(w * 0.79f, frenteY - 14 * u - (k + 1) * 9 * u, w * 0.95f, frenteY - 14 * u - k * 9 * u)
            drawRect(Color.Black.copy(alpha = 0.25f), rr.topLeft + Offset(1 * u, 1.5f * u), rr.size)
            drawRoundRect(c(BAQUELITA), rr.topLeft, rr.size, CornerRadius(2 * u))
            drawRect(Color.White.copy(alpha = 0.1f), rr.topLeft, Size(rr.width, 1 * u))
            drawRect(c(CROMO), Offset(rr.left, rr.bottom - 2.4f * u), Size(rr.width, 2.4f * u))
            drawRect(hoja, Offset(rr.left + rr.width * 0.25f, rr.top + 1.6f * u), Size(rr.width * 0.5f, rr.height - 4.6f * u))
            drawRect(PILOTO, Offset(rr.left + rr.width * 0.08f, rr.top + 2 * u), Size(rr.width * 0.1f, 2 * u))
        }

        // ---- El cartucho de la célula.
        val g = grosor(e.hojas, u)
        val ancho = w * 0.46f
        val x0 = w * 0.27f; val x1 = x0 + ancho
        val fy = frenteY - 10 * u
        val hondo = h * 0.2f
        val arriba = fy - hondo - g
        val tapaY = fy - g
        val cc = c(BAQUELITA)
        val lleno = (e.hojas / 120f).coerceIn(0f, 1f)
        sombraSuave(Rect(x0, arriba, x1, fy), 4 * u, 8 * u, if (oscuro) 0.42f else 0.3f)
        // El canto de delante: la ventanilla de la cinta, las estrías y la placa de cromo del fondo.
        drawRect(mezcla(cc, Color.Black, 0.2f), Offset(x0, tapaY), Size(ancho, g))
        drawRect(Brush.verticalGradient(listOf(mezcla(c(CROMO), Color.White, 0.2f), c(CROMO), mezcla(c(CROMO), Color.Black, 0.3f)), fy - g * 0.4f, fy), Offset(x0, fy - g * 0.4f), Size(ancho, g * 0.4f))
        val boca = Rect(x0 + ancho * 0.36f, tapaY + g * 0.12f, x1 - ancho * 0.36f, fy - g * 0.46f)
        if (boca.height > 1f) {
            drawRect(Color(0xFF0E0C0A), boca.topLeft, boca.size)
            drawRect(c(Color(0xFF4A3424)), Offset(boca.left, boca.center.y - boca.height * 0.18f), Size(boca.width, boca.height * 0.36f))
        }
        listOf(x0 + 4 * u, x1 - 16 * u).forEach { sx ->
            var x = sx
            while (x < sx + 12 * u) { drawLine(Color.Black.copy(alpha = 0.5f), Offset(x, tapaY + 1 * u), Offset(x, fy - g * 0.42f), 0.8f * u); x += 2.2f * u }
        }
        // Lo de dentro, que se ve al levantar la tapa: la bandeja, los dos carretes con la cinta y la tarjeta del índice.
        val angulo = giro(a)
        if (a > 0.02f) {
            val bandeja = Rect(x0, arriba, x1, tapaY)
            drawRoundRect(mezcla(cc, Color.Black, 0.35f), bandeja.topLeft, bandeja.size, CornerRadius(5 * u))
            val cy = bandeja.top + bandeja.height * 0.45f
            val rx = ancho * 0.16f; val ry = bandeja.height * 0.32f
            listOf(0.28f to (0.45f + 0.55f * (1f - lleno)), 0.72f to (0.45f + 0.55f * lleno)).forEach { (fx, carga) ->
                val o = Offset(x0 + ancho * fx, cy)
                drawOval(c(Color(0xFF4A3424)), Offset(o.x - rx * carga, o.y - ry * carga), Size(rx * 2 * carga, ry * 2 * carga))
                drawOval(Color.Black.copy(alpha = 0.3f), Offset(o.x - rx * carga, o.y - ry * carga), Size(rx * 2 * carga, ry * 2 * carga), style = Stroke(0.6f * u))
                drawOval(c(HUESO), Offset(o.x - rx * 0.32f, o.y - ry * 0.32f), Size(rx * 0.64f, ry * 0.64f))
                for (j in 0 until 3) {
                    val an = j * 2f * PI.toFloat() / 3f
                    drawLine(c(BAQUELITA), o, Offset(o.x + cos(an) * rx * 0.3f, o.y + sin(an) * ry * 0.3f), 1f * u)
                }
            }
            // La cinta que corre por delante, entre los rodillos.
            val yc = bandeja.bottom - bandeja.height * 0.12f
            drawLine(c(Color(0xFF4A3424)), Offset(x0 + ancho * 0.12f, yc), Offset(x1 - ancho * 0.12f, yc), 1.6f * u)
            listOf(0.12f, 0.88f).forEach { fx -> drawCircle(c(CROMO), 2.4f * u, Offset(x0 + ancho * fx, yc)) }
            drawCircle(c(CROMO), 3.2f * u, Offset(x0 + ancho * 0.5f, yc + 1 * u))
            // La tarjeta del índice.
            val ti = Rect(x0 + ancho * 0.4f, bandeja.top + bandeja.height * 0.12f, x0 + ancho * 0.6f, bandeja.top + bandeja.height * 0.4f)
            drawRect(hoja, ti.topLeft, ti.size)
            textoEn(medidor, "ÍNDICE", Rect(ti.left, ti.top, ti.right, ti.top + ti.height * 0.45f), MONO, tinta, 6f, minimo = 3.5f)
            drawLine(tinta.copy(alpha = 0.4f), Offset(ti.left + 2 * u, ti.top + ti.height * 0.65f), Offset(ti.right - 2 * u, ti.top + ti.height * 0.65f), 0.6f * u)
            drawLine(tinta.copy(alpha = 0.4f), Offset(ti.left + 2 * u, ti.top + ti.height * 0.85f), Offset(ti.right - 5 * u, ti.top + ti.height * 0.85f), 0.6f * u)
        }
        // La tapa, que se levanta sobre la bisagra de atrás.
        levantar(arriba, tapaY - arriba, angulo, 1f, fuera = { sombra ->
            val r = Rect(x0, arriba, x1, tapaY)
            val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(5 * u))) }
            val muesca = Path().apply { addRect(Rect(x0 + ancho * 0.43f, arriba - 1f, x1 - ancho * 0.43f, arriba + r.height * 0.09f)) }
            val carcasa = Path().apply { op(forma, muesca, PathOperation.Difference) }
            drawPath(carcasa, Brush.verticalGradient(listOf(mezcla(cc, Color.White, 0.12f), cc, mezcla(cc, Color.Black, 0.15f)), r.top, r.bottom))
            drawPath(carcasa, Color.Black.copy(alpha = 0.55f), style = Stroke(0.8f * u))
            drawLine(Color.White.copy(alpha = 0.16f), Offset(r.left + 6 * u, r.bottom - 1 * u), Offset(r.right - 6 * u, r.bottom - 1 * u), 0.8f * u)
            listOf(Offset(r.left + 6 * u, r.top + 6 * u), Offset(r.right - 6 * u, r.top + 6 * u), Offset(r.left + 6 * u, r.bottom - 6 * u), Offset(r.right - 6 * u, r.bottom - 6 * u)).forEach { o ->
                tornillo(o, 1.8f * u, c(CROMO))
            }
            // Las estrías del agarre, a los dos lados.
            listOf(r.left + 12 * u, r.right - 22 * u).forEach { sx ->
                var x = sx
                while (x < sx + 10 * u) { drawLine(Color.Black.copy(alpha = 0.5f), Offset(x, r.top + r.height * 0.25f), Offset(x, r.bottom - r.height * 0.25f), 0.8f * u); drawLine(Color.White.copy(alpha = 0.08f), Offset(x + 0.8f * u, r.top + r.height * 0.25f), Offset(x + 0.8f * u, r.bottom - r.height * 0.25f), 0.5f * u); x += 2.4f * u }
            }
            // La pestaña ámbar contra escritura, en su ranura.
            val ra = Rect(r.left + ancho * 0.08f, r.top + r.height * 0.05f, r.left + ancho * 0.2f, r.top + r.height * 0.14f)
            drawRoundRect(Color.Black.copy(alpha = 0.7f), ra.topLeft, ra.size, CornerRadius(1 * u))
            drawRoundRect(PILOTO, Offset(ra.left + 0.8f * u, ra.top + 0.8f * u), Size(ra.width * 0.5f, ra.height - 1.6f * u), CornerRadius(0.8f * u))
            // La flecha en relieve que dice por dónde entra.
            val fl = Offset(r.right - ancho * 0.1f, r.top + r.height * 0.1f)
            drawPath(Path().apply { moveTo(fl.x - 4 * u, fl.y - 3 * u); lineTo(fl.x + 3 * u, fl.y); lineTo(fl.x - 4 * u, fl.y + 3 * u); close() }, Color.White.copy(alpha = 0.14f))
            // La etiqueta hueso rotulada.
            val et = Rect(r.left + ancho * 0.17f, r.top + r.height * 0.19f, r.right - ancho * 0.17f, r.bottom - r.height * 0.12f)
            drawRect(Color.Black.copy(alpha = 0.3f), et.topLeft + Offset(0.8f * u, 1.2f * u), et.size)
            drawRect(hoja, et.topLeft, et.size)
            val corte = et.top + et.height * 0.46f
            emblema(Offset(et.left + 9 * u, et.top + et.height * 0.28f), 5f * u, c(AMBAR), 1f * u)
            textoEn(medidor, "CARTUCHO K-7", Rect(et.left + 19 * u, et.top + 2 * u, et.right - 5 * u, corte - 1 * u), TITULO, tinta, 11f, espaciado = 0.8f, minimo = 5f)
            drawLine(c(AMBAR), Offset(et.left + 4 * u, corte), Offset(et.right - 4 * u, corte), 1f * u)
            textoEn(medidor, e.cuenta, Rect(et.left + 4 * u, corte + 1.5f * u, et.right - 4 * u, et.top + et.height * 0.76f), MONO, tinta.copy(alpha = 0.85f), 9f, minimo = 5f)
            textoEn(medidor, e.nombre.uppercase(), Rect(et.left + 4 * u, et.top + et.height * 0.76f, et.right - 4 * u, et.bottom - 1.5f * u), MONO, c(TENUE), 6.5f, espaciado = 0.8f, minimo = 4f)
            if (sombra > 0f) drawPath(carcasa, Color.Black.copy(alpha = 0.45f * sombra))
        }, dentro = { sombra ->
            // Por dentro: la baquelita con sus nervios y la almohadilla de fieltro que aprieta la cinta.
            val r = Rect(x0, arriba, x1, tapaY)
            drawRoundRect(mezcla(cc, Color.Black, 0.2f), r.topLeft, r.size, CornerRadius(5 * u))
            for (k in 1 until 6) drawLine(Color.White.copy(alpha = 0.06f), Offset(r.left + ancho * k / 6f, r.top + 3 * u), Offset(r.left + ancho * k / 6f, r.bottom - 3 * u), 1.4f * u)
            drawRect(c(Color(0xFF6B6660)), Offset(r.left + ancho * 0.38f, r.top + r.height * 0.1f), Size(ancho * 0.24f, r.height * 0.14f))
            // La advertencia impresa por dentro de la tapa.
            val av = Rect(r.left + ancho * 0.22f, r.top + r.height * 0.42f, r.right - ancho * 0.22f, r.top + r.height * 0.72f)
            drawRect(c(Color(0xFFD8D0BE)), av.topLeft, av.size)
            drawRect(c(AMBAR), av.topLeft, Size(av.width, av.height * 0.22f))
            textoEn(medidor, "NO TOCAR LA CINTA", Rect(av.left + 3 * u, av.top + av.height * 0.26f, av.right - 3 * u, av.top + av.height * 0.62f), MONO, c(PIZARRA), 7f, espaciado = 0.6f, minimo = 3.5f)
            textoEn(medidor, "LEJOS DE IMANES · SOLO CÉLULA K-7", Rect(av.left + 3 * u, av.top + av.height * 0.62f, av.right - 3 * u, av.bottom - 1 * u), MONO, c(TENUE), 5.5f, minimo = 3f)
            if (sombra > 0f) drawRoundRect(Color.Black.copy(alpha = 0.4f * sombra), r.topLeft, r.size, CornerRadius(5 * u))
        })
    }

    /**
     * La pestaña de un día: un cartucho pequeño visto de frente, de baquelita, con su muesca; el piloto que se enciende del
     * color del ánimo (apagado, si no hay), la letra de la semana grabada, la etiqueta hueso con el día y su franja ámbar, y
     * las estrías de abajo.
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val hoja = papel(oscuro, false)
        val cc = c(BAQUELITA)
        rotate(-1.5f + 3f * azar(e.semilla, 1), Offset(w / 2, h / 2)) {
            val r = Rect(2 * u, 3 * u, w - 2 * u, h - 2 * u)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(1 * u, 1.5f * u), r.size, CornerRadius(4 * u))
            val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(4 * u))) }
            val muesca = Path().apply { addRect(Rect(w * 0.4f, 0f, w * 0.6f, 6.5f * u)) }
            val carcasa = Path().apply { op(forma, muesca, PathOperation.Difference) }
            drawPath(carcasa, Brush.horizontalGradient(listOf(mezcla(cc, Color.White, 0.14f), cc, mezcla(cc, Color.Black, 0.25f)), 0f, w))
            drawPath(carcasa, Color.Black.copy(alpha = 0.5f), style = Stroke(0.7f * u))
            if (e.tono != null) piloto(Offset(9 * u, 10.5f * u), 2f * u, e.tono, true, c(CROMO), 0.8f)
            else piloto(Offset(9 * u, 10.5f * u), 2f * u, PILOTO, false, c(CROMO))
            textoEn(medidor, e.semana.uppercase(), Rect(w * 0.5f, 6.5f * u, w - 4 * u, 14.5f * u), MONO, mezcla(HUESO, cc, 0.15f), 7.5f, minimo = 4.5f)
            val et = Rect(5 * u, 17 * u, w - 5 * u, h - 12 * u)
            drawRect(hoja, et.topLeft, et.size)
            drawRect(ambar(oscuro, false), et.topLeft, Size(et.width, 3.2f * u))
            textoEn(medidor, e.dia, Rect(et.left, et.top + 3.2f * u, et.right, et.bottom), MONO, PIZARRA, 16f, peso = FontWeight.Medium, minimo = 8f)
            for (j in 0 until 3) drawLine(Color.Black.copy(alpha = 0.55f), Offset(8 * u, h - 9 * u + j * 2 * u), Offset(w - 8 * u, h - 9 * u + j * 2 * u), 0.8f * u)
        }
    }

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
