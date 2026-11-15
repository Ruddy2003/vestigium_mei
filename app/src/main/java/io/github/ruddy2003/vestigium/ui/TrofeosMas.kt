package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los trofeos del lote 2: Al-Ándalus (lacería, azulejos, fuentes, astrolabio, arcos), la Ruta de la Seda (sellos de
 * cinabrio, jade, seda, farolillos, porcelana azul y brújula de cuchara), el Mongol (rollo de órdenes, paiza, tamga,
 * yurta, estandarte, ovoo), los Colonos (hacha, yunque, barril, campana, telar, brújula), el Botánico (láminas, hojas,
 * semillas, frutos, lupas), la Navidad (adornos que cuelgan) y Tierras Antiguas (runas inventadas, gemas, yelmos,
 * mapas, llaves y cristales). Sin figuras humanas ni animales, sin letras reales: los signos son inventados.
 */
object TrofeosMas {
    val NOMBRES: Map<TemaId, Map<String, String>> = mapOf(
        TemaId.ANDALUS to mapOf("Diario" to "Libro de lacería", "Mundo" to "Astrolabio", "Cuerpo" to "Fuente del patio", "Mente" to "Estrella de ocho puntas",
            "Cultura" to "Arco de herradura", "Gente" to "Azulejo", "Constancia" to "Lámpara de bronce", "Curiosos" to "Celosía", "Tuyos" to "Granada"),
        TemaId.MESO to mapOf("Diario" to "Códice en biombo", "Mundo" to "Lienzo de los cuatro rumbos", "Cuerpo" to "Anillo del juego de pelota", "Mente" to "Máscara de jade",
            "Cultura" to "Estela de los glifos", "Gente" to "Vaso del cacao", "Constancia" to "Pirámide de nueve cuerpos", "Curiosos" to "Caracola de mar", "Tuyos" to "Penacho de quetzal"),
        TemaId.SEDA to mapOf("Diario" to "Sello de cinabrio", "Mundo" to "Brújula de cuchara", "Cuerpo" to "Disco de jade", "Mente" to "Porcelana azul",
            "Cultura" to "Carrete de seda", "Gente" to "Farolillo", "Constancia" to "Moneda de cobre", "Curiosos" to "Ábaco", "Tuyos" to "Ventana lunar"),
        TemaId.EDO to mapOf("Diario" to "Cuaderno cosido", "Mundo" to "Sombrero de caminante", "Cuerpo" to "Sandalias de paja", "Mente" to "Tablero de go",
            "Cultura" to "Abanico con el Fuji", "Gente" to "Chōchin de la posada", "Constancia" to "Koban de oro", "Curiosos" to "Linterna de piedra", "Tuyos" to "Sello de la posta"),
        TemaId.MONGOL to mapOf("Diario" to "Rollo de órdenes", "Mundo" to "Mapa de la estepa", "Cuerpo" to "Estribo", "Mente" to "Tamga",
            "Cultura" to "Yurta", "Gente" to "Placa de mensajero", "Constancia" to "Estandarte de nueve colas", "Curiosos" to "Ovoo", "Tuyos" to "Rueda del techo"),
        TemaId.COLONOS to mapOf("Diario" to "Cuaderno del pueblo", "Mundo" to "Brújula de bolsillo", "Cuerpo" to "Hacha", "Mente" to "Telar",
            "Cultura" to "Linterna", "Gente" to "Campana del pueblo", "Constancia" to "Yunque", "Curiosos" to "Barril", "Tuyos" to "Rueda de carreta"),
        TemaId.BOTANICO to mapOf("Diario" to "Lámina botánica", "Mundo" to "Caja de herborizar", "Cuerpo" to "Hoja de roble", "Mente" to "Lupa",
            "Cultura" to "Helecho", "Gente" to "Cerezas", "Constancia" to "Semilla que brota", "Curiosos" to "Campana de cristal", "Tuyos" to "Flor prensada"),
        TemaId.RUINAS to mapOf("Diario" to "Cuaderno de lona", "Mundo" to "Brújula oxidada", "Cuerpo" to "Bicicleta rescatada", "Mente" to "Radio de manivela",
            "Cultura" to "Libro salvado del agua", "Gente" to "Fogata del campamento", "Constancia" to "Árbol que rompió el asfalto", "Curiosos" to "Semáforo con hiedra", "Tuyos" to "Llave de la puerta tapiada"),
        TemaId.NAVIDAD to mapOf("Diario" to "Vela", "Mundo" to "Estrella", "Cuerpo" to "Bastón de caramelo", "Mente" to "Copo de nieve",
            "Cultura" to "Esfera", "Gente" to "Corona", "Constancia" to "Campana", "Curiosos" to "Regalo", "Tuyos" to "Piña"),
        TemaId.ANTIGUAS to mapOf("Diario" to "Libro de runas", "Mundo" to "Mapa de la travesía", "Cuerpo" to "Yelmo", "Mente" to "Cristal",
            "Cultura" to "Arpa", "Gente" to "Estandarte", "Constancia" to "Llave de hierro", "Curiosos" to "Gema", "Tuyos" to "Linterna de mina"),
        TemaId.CUEVA to mapOf("Diario" to "Laja pintada", "Mundo" to "Huellas en el barro", "Cuerpo" to "Propulsor de asta", "Mente" to "Lámpara de grasa",
            "Cultura" to "Flauta de hueso", "Gente" to "Collar de conchas", "Constancia" to "Hueso con muescas", "Curiosos" to "Bifaz de sílex", "Tuyos" to "Mano en negativo"),
        TemaId.DISTOPIA to mapOf("Diario" to "Expediente sellado", "Mundo" to "Pase de control", "Cuerpo" to "Insignia numerada", "Mente" to "Tarjeta perforada",
            "Cultura" to "Documento censurado", "Gente" to "Credencial", "Constancia" to "Sello de goma", "Curiosos" to "Cámara de vigilancia", "Tuyos" to "Altavoz"),
        TemaId.PSICODELIA to mapOf("Diario" to "Cartel de la semana", "Mundo" to "Camioneta pintada a mano", "Cuerpo" to "Cinta para la frente", "Mente" to "Lámpara de cera",
            "Cultura" to "Guitarra pintada", "Gente" to "Botones de solapa", "Constancia" to "Disco de larga duración", "Curiosos" to "Campanilla de templo", "Tuyos" to "Flor que se regala"),
        TemaId.NOUVEAU to mapOf("Diario" to "Cartel litográfico", "Mundo" to "Entrada del Métropolitain", "Cuerpo" to "Joya de libélula", "Mente" to "Lámpara de vidrio de colores",
            "Cultura" to "Jarrón de vidrio de Nancy", "Gente" to "Abanico de seda pintada", "Constancia" to "Pavo real de esmalte", "Curiosos" to "Frasco de perfume", "Tuyos" to "Peine de carey"),
        TemaId.GALIA to mapOf("Diario" to "Torques de la casa grande", "Mundo" to "Estátero del caballo", "Cuerpo" to "Carnyx de cabeza de jabalí", "Mente" to "Hoz de oro y muérdago",
            "Cultura" to "Caldero del banquete", "Gente" to "Jabalí de bronce", "Constancia" to "Casco de jinete", "Curiosos" to "Huevo de serpiente", "Tuyos" to "Espada de La Tène"),
        TemaId.ARCONTES to mapOf("Diario" to "Jarra sellada", "Mundo" to "Disco de las siete esferas", "Cuerpo" to "Amuleto de Abraxas", "Mente" to "Anillo de Salomón",
            "Cultura" to "Códice de cuero", "Gente" to "Lámpara de pico", "Constancia" to "Laminilla de las contraseñas", "Curiosos" to "Sello de los arcontes", "Tuyos" to "Cálamo y tintero"),
        TemaId.FRENTE to mapOf("Diario" to "Parche del cartucho", "Mundo" to "Parche de la carta estelar", "Cuerpo" to "Parche del pulso", "Mente" to "Parche de la llave de cifrado",
            "Cultura" to "Parche del microfilm", "Gente" to "Parche del auricular", "Constancia" to "Insignia de la célula", "Curiosos" to "Parche de la credencial falsa", "Tuyos" to "Parche de la radio de maleta"),
        TemaId.CIBER to mapOf("Diario" to "Registro del sistema", "Mundo" to "Mapa de la red", "Cuerpo" to "Monitor de pulso", "Mente" to "Árbol de directorios",
            "Cultura" to "Disquete", "Gente" to "Canal de charla", "Constancia" to "Tiempo en línea", "Curiosos" to "Laberinto ASCII", "Tuyos" to "Llave de raíz"),
        TemaId.NEON to mapOf("Diario" to "Letrero del puesto", "Mundo" to "Globo de neón", "Cuerpo" to "Corazón de neón", "Mente" to "Ojo que parpadea",
            "Cultura" to "Disco de la rocola", "Gente" to "Dos vasos brindando", "Constancia" to "Reloj de 24 horas", "Curiosos" to "Gato de la suerte", "Tuyos" to "Ficha de oro"),
        TemaId.REFUGIO to mapOf("Diario" to "Placa del cronista", "Mundo" to "Medalla de la superficie", "Cuerpo" to "Insignia de salud", "Mente" to "Medalla del átomo",
            "Cultura" to "Disco de vinilo", "Gente" to "Placa de la puerta", "Constancia" to "Engrane de la compuerta", "Curiosos" to "Contador Geiger", "Tuyos" to "Lata de conservas"),
        TemaId.ORDEN to mapOf("Diario" to "Placa del juramento", "Mundo" to "Estandarte de campaña", "Cuerpo" to "Guantelete", "Mente" to "Calibre de acero",
            "Cultura" to "Escudo de la Orden", "Gente" to "Yelmo de visera", "Constancia" to "Martillo de forja", "Curiosos" to "Válvula de vacío", "Tuyos" to "Espada forjada"),
        TemaId.VAPOR to mapOf("Diario" to "Patente sellada", "Mundo" to "Locomotora de latón", "Cuerpo" to "Pala del fogonero", "Mente" to "Máquina diferencial",
            "Cultura" to "Gramófono de bocina", "Gente" to "Reloj del andén", "Constancia" to "Manómetro de la caldera", "Curiosos" to "Linterna mágica", "Tuyos" to "Silbato de vapor"),
        TemaId.LABORATORIO to mapOf("Diario" to "Placa de mérito", "Mundo" to "Matraz de fondo redondo", "Cuerpo" to "Probeta graduada", "Mente" to "Microscopio",
            "Cultura" to "Modelo molecular", "Gente" to "Gradilla de tubos", "Constancia" to "Balanza analítica", "Curiosos" to "Matraz Erlenmeyer", "Tuyos" to "Placa de Petri"),
        TemaId.ESPACIO to mapOf("Diario" to "Parche de la misión", "Mundo" to "Sonda planetaria", "Cuerpo" to "Casco presurizado", "Mente" to "Telescopio espacial",
            "Cultura" to "Radiotelescopio", "Gente" to "Estación orbital", "Constancia" to "Cohete de tres etapas", "Curiosos" to "Meteorito", "Tuyos" to "Módulo de aterrizaje"),
        TemaId.ABISMO to mapOf("Diario" to "Pizarra del buzo", "Mundo" to "Globo batimétrico", "Cuerpo" to "Escafandra de latón", "Mente" to "Batisfera",
            "Cultura" to "Ánfora del pecio", "Gente" to "Cardumen de plata", "Constancia" to "Batiscafo de las fosas", "Curiosos" to "Rape abisal", "Tuyos" to "Medusa luminosa"),
        TemaId.URSS to mapOf("Diario" to "Periódico mural", "Mundo" to "Insignia del Transiberiano", "Cuerpo" to "Insignia del deportista", "Mente" to "Satélite de antenas",
            "Cultura" to "Cartel constructivista", "Gente" to "Estrella de la brigada", "Constancia" to "Medalla del trabajo", "Curiosos" to "Samovar", "Tuyos" to "Estrella roja"),
    )

    private fun TrofeosTema.Pincel.c(c: Color): Color = if (hecho) c else sombra
    private fun TrofeosTema.Pincel.b(c: Color): Brush = SolidColor(if (hecho) c else sombra)
    private fun TrofeosTema.Pincel.o(fx: Float, fy: Float) = Offset(x(fx), y(fy))

    // ================================================================== pedestales


    /** Signos inventados: trazos rectos y ángulos que parecen runas sin ser ningún alfabeto. */
    fun DrawScope.runas(o: Offset, ancho: Float, alto: Float, color: Color, grosor: Float, n: Int, semilla: Int = 3) {
        val az = java.util.Random(semilla.toLong())
        val paso = ancho / n
        for (k in 0 until n) {
            val x = o.x + k * paso + paso * 0.2f; val ww = paso * 0.6f
            drawLine(color, Offset(x + ww / 2, o.y - alto / 2), Offset(x + ww / 2, o.y + alto / 2), grosor, StrokeCap.Round)
            when (az.nextInt(4)) {
                0 -> drawLine(color, Offset(x + ww / 2, o.y - alto / 2), Offset(x + ww, o.y - alto / 6), grosor, StrokeCap.Round)
                1 -> { drawLine(color, Offset(x, o.y - alto / 4), Offset(x + ww / 2, o.y), grosor, StrokeCap.Round); drawLine(color, Offset(x + ww / 2, o.y), Offset(x + ww, o.y - alto / 4), grosor, StrokeCap.Round) }
                2 -> drawLine(color, Offset(x, o.y + alto / 6), Offset(x + ww, o.y - alto / 6), grosor, StrokeCap.Round)
                else -> { drawLine(color, Offset(x + ww / 2, o.y - alto / 6), Offset(x + ww, o.y + alto / 6), grosor, StrokeCap.Round); drawLine(color, Offset(x + ww / 2, o.y - alto / 6), Offset(x, o.y + alto / 6), grosor, StrokeCap.Round) }
            }
        }
    }

    /** El átomo de los años del optimismo atómico: tres órbitas elípticas giradas y el núcleo. */
    fun DrawScope.atomo(c: Offset, r: Float, color: Color, grosor: Float, nucleo: Color = color) {
        for (k in 0 until 3) rotate(k * 60f, c) { drawOval(color, Offset(c.x - r, c.y - r * 0.36f), Size(2 * r, r * 0.72f), style = Stroke(grosor)) }
        drawCircle(nucleo, r * 0.18f, c)
    }

    /** Texto en letra monoespaciada con la base en [y] (consolas, letreros de máquina). */
    fun DrawScope.mono(t: String, x: Float, y: Float, tam: Float, color: Color, alinear: android.graphics.Paint.Align = android.graphics.Paint.Align.LEFT, negrita: Boolean = false) {
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color.toArgb(); textSize = tam; textAlign = alinear
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, if (negrita) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        }
        drawContext.canvas.nativeCanvas.drawText(t, x, y, paint)
    }

    /** Una estrella de [puntas] hecha con [capas] cuadrados girados (la lacería). */
    fun DrawScope.estrellaLazo(c: Offset, r: Float, b: Brush, contorno: Color?, grosor: Float) {
        val p = Path().apply {
            fillType = PathFillType.NonZero
            for (k in 0 until 2) {
                val g = k * PI / 4
                for (j in 0 until 4) { val a = g + j * PI / 2 + PI / 4; val x = c.x + (r * cos(a)).toFloat(); val y = c.y + (r * sin(a)).toFloat(); if (j == 0) moveTo(x, y) else lineTo(x, y) }
                close()
            }
        }
        drawPath(p, b)
        if (contorno != null) drawPath(p, contorno, style = Stroke(grosor))
    }

    // ================================================================== figuras

    // Cada tema se llama directo desde [TrofeosTema.figura].

    // ---------------------------------------------------------------- Al-Ándalus

    internal fun DrawScope.andalus(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val azul = p.c(Color(0xFF1E4F8A)); val verde = p.c(Color(0xFF1E6B5A)); val rojo = p.c(Color(0xFF9E2B25)); val yeso = p.c(Color(0xFFF1E8D4))
        when (cat) {
            "Diario" -> {
                // Libro encuadernado en cuero con la estrella de lazo en oro.
                caja(p, 0.24f, 0.1f, 0.76f, 0.74f, p.b(Color(0xFF6B2E1F)))
                drawRect(p.metal(0.24f, 0.76f), Offset(p.x(0.28f), p.y(0.14f)), Size(p.x(0.44f), p.y(0.56f)), style = Stroke(lw))
                estrellaLazo(p.o(0.5f, 0.42f), p.w * 0.16f, p.metal(0.3f, 0.7f), p.linea, lw * 0.5f)
                drawCircle(p.c(Color(0xFF6B2E1F)), p.w * 0.04f, p.o(0.5f, 0.42f))
                listOf(0.32f to 0.18f, 0.68f to 0.18f, 0.32f to 0.66f, 0.68f to 0.66f).forEach { (x, y) -> estrellaLazo(p.o(x, y), p.w * 0.035f, p.metal(x - 0.05f, x + 0.05f), null, 0f) }
            }
            "Mundo" -> {
                // Astrolabio con su «trono» calado y la red de estrellas en punta.
                val c = p.o(0.5f, 0.46f); val R = p.w * 0.3f
                drawPath(Path().apply { moveTo(c.x - R * 0.35f, c.y - R * 0.92f); quadraticTo(c.x, c.y - R * 1.5f, c.x + R * 0.35f, c.y - R * 0.92f); close() }, p.metal(0.38f, 0.62f))
                drawCircle(p.metal(0.48f, 0.52f), p.w * 0.04f, Offset(c.x, c.y - R * 1.38f), style = Stroke(lw))
                drawCircle(p.metal(0.2f, 0.8f), R, c); drawCircle(p.linea, R * 0.88f, c, style = Stroke(lw * 0.5f))
                for (k in 0 until 36) { val a = k * PI / 18; drawLine(p.linea, Offset(c.x + (R * 0.88f * cos(a)).toFloat(), c.y + (R * 0.88f * sin(a)).toFloat()), Offset(c.x + (R * 0.97f * cos(a)).toFloat(), c.y + (R * 0.97f * sin(a)).toFloat()), lw * 0.35f) }
                // La red: un anillo excéntrico y punteros de estrella.
                drawCircle(p.linea, R * 0.55f, Offset(c.x, c.y - R * 0.12f), style = Stroke(lw * 0.6f))
                for (k in 0 until 7) { val a = -PI / 2 + k * 2 * PI / 7; val t = Offset(c.x + (R * 0.78f * cos(a)).toFloat(), c.y + (R * 0.78f * sin(a)).toFloat())
                    drawPath(Path().apply { moveTo(t.x, t.y); quadraticTo(c.x + (R * 0.4f * cos(a + 0.4)).toFloat(), c.y + (R * 0.4f * sin(a + 0.4)).toFloat(), c.x, c.y) }, p.linea, style = Stroke(lw * 0.5f)) }
                drawCircle(p.c(Color(0xFF6B2E1F)), p.w * 0.025f, c)
            }
            "Cuerpo" -> {
                // La fuente del patio: taza de doce lados, el surtidor y el agua que cae.
                val taza = Path().apply { moveTo(p.x(0.16f), p.y(0.46f)); lineTo(p.x(0.84f), p.y(0.46f)); lineTo(p.x(0.74f), p.y(0.58f)); lineTo(p.x(0.26f), p.y(0.58f)); close() }
                drawPath(taza, p.metal(0.16f, 0.84f))
                for (k in 1 until 6) drawLine(p.linea.copy(alpha = 0.4f), Offset(p.x(0.16f + k * 0.113f), p.y(0.46f)), Offset(p.x(0.26f + k * 0.08f), p.y(0.58f)), lw * 0.4f)
                caja(p, 0.44f, 0.58f, 0.56f, 0.74f); caja(p, 0.34f, 0.72f, 0.66f, 0.76f)
                caja(p, 0.47f, 0.3f, 0.53f, 0.46f)
                val agua = p.c(Color(0xFF5FA8D8))
                listOf(-1f, 1f).forEach { s ->
                    drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.22f)); quadraticTo(p.x(0.5f + s * 0.2f), p.y(0.12f), p.x(0.5f + s * 0.3f), p.y(0.44f)) }, agua, style = Stroke(lw * 0.8f, cap = StrokeCap.Round))
                    drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.22f)); quadraticTo(p.x(0.5f + s * 0.1f), p.y(0.16f), p.x(0.5f + s * 0.16f), p.y(0.44f)) }, agua, style = Stroke(lw * 0.6f, cap = StrokeCap.Round))
                }
                drawLine(agua, p.o(0.5f, 0.3f), p.o(0.5f, 0.12f), lw)
                drawOval(agua.copy(alpha = if (p.hecho) 0.5f else 1f), Offset(p.x(0.2f), p.y(0.44f)), Size(p.x(0.6f), p.y(0.04f)))
            }
            "Mente" -> {
                // La estrella de ocho puntas con su lazo interior.
                val c = p.o(0.5f, 0.4f)
                estrellaLazo(c, p.w * 0.36f, p.metal(0.14f, 0.86f), p.linea, lw * 0.6f)
                estrellaLazo(c, p.w * 0.25f, SolidColor(azul), yeso, lw)
                estrellaLazo(c, p.w * 0.12f, p.metal(0.38f, 0.62f), null, 0f)
                drawCircle(verde, p.w * 0.04f, c)
                caja(p, 0.47f, 0.68f, 0.53f, 0.77f)
            }
            "Cultura" -> {
                // Arco de herradura con dovelas rojas y blancas, sobre dos columnas.
                val c = p.o(0.5f, 0.36f); val R = p.w * 0.3f; val r = p.w * 0.19f
                val n = 14
                for (k in 0 until n) {
                    val a0 = 160f + k * (220f / n)
                    drawArc(if (k % 2 == 0) rojo else yeso, a0, 220f / n + 0.6f, false, Offset(c.x - (R + r) / 2, c.y - (R + r) / 2), Size(R + r, R + r), style = Stroke(R - r))
                }
                drawArc(p.metal(0.2f, 0.8f), 160f, 220f, false, Offset(c.x - R, c.y - R), Size(2 * R, 2 * R), style = Stroke(lw * 0.8f))
                listOf(0.26f, 0.74f).forEach { x -> columna(p, x, 0.07f, 0.48f, 0.74f, 0); caja(p, x - 0.05f, 0.46f, x + 0.05f, 0.49f) }
                caja(p, 0.14f, 0.74f, 0.86f, 0.77f)
                // El alfiz: el marco recto alrededor del arco.
                drawRect(p.metal(0.12f, 0.88f), Offset(p.x(0.12f), c.y - R - p.w * 0.04f), Size(p.x(0.76f), R * 2.1f), style = Stroke(lw * 0.8f))
            }
            "Gente" -> {
                // Azulejo de lazo: el fondo azul, la estrella verde y blanca, cuartos de estrella en las esquinas.
                val x0 = 0.16f; val x1 = 0.84f; val y0 = 0.1f; val lado = p.x(x1 - x0)
                drawRect(p.metal(x0, x1), Offset(p.x(x0) - lw, p.y(y0) - lw), Size(lado + 2 * lw, lado + 2 * lw))
                drawRect(azul, Offset(p.x(x0), p.y(y0)), Size(lado, lado))
                val c = Offset(p.x(0.5f), p.y(y0) + lado / 2)
                estrellaLazo(c, lado * 0.36f, SolidColor(yeso), null, 0f)
                estrellaLazo(c, lado * 0.26f, SolidColor(verde), null, 0f)
                drawCircle(p.c(Color(0xFFE2B34A)), lado * 0.07f, c)
                clipPath(Path().apply { addRect(Rect(p.x(x0), p.y(y0), p.x(x1), p.y(y0) + lado)) }) {
                    listOf(Offset(p.x(x0), p.y(y0)), Offset(p.x(x1), p.y(y0)), Offset(p.x(x0), p.y(y0) + lado), Offset(p.x(x1), p.y(y0) + lado)).forEach { e -> estrellaLazo(e, lado * 0.2f, SolidColor(yeso), null, 0f) }
                }
                caja(p, 0.47f, p.y(y0).let { (it + lado) / p.h } + 0.01f, 0.53f, 0.77f)
            }
            "Constancia" -> {
                // Lámpara de bronce calada que cuelga de su cadena, con su luz.
                for (k in 0 until 6) drawOval(p.metal(0.47f, 0.53f), Offset(p.x(0.48f), p.y(0.02f + k * 0.035f)), Size(p.x(0.04f), p.y(0.03f)), style = Stroke(lw * 0.5f))
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD27A).copy(alpha = 0.5f), Color.Transparent), p.o(0.5f, 0.48f), p.w * 0.4f), p.w * 0.4f, p.o(0.5f, 0.48f))
                drawPath(Path().apply { moveTo(p.x(0.3f), p.y(0.42f)); quadraticTo(p.x(0.32f), p.y(0.24f), p.x(0.5f), p.y(0.22f)); quadraticTo(p.x(0.68f), p.y(0.24f), p.x(0.7f), p.y(0.42f)); close() }, p.metal(0.3f, 0.7f))
                drawPath(Path().apply { moveTo(p.x(0.24f), p.y(0.42f)); lineTo(p.x(0.76f), p.y(0.42f)); quadraticTo(p.x(0.7f), p.y(0.62f), p.x(0.5f), p.y(0.64f)); quadraticTo(p.x(0.3f), p.y(0.62f), p.x(0.24f), p.y(0.42f)); close() }, p.metal(0.24f, 0.76f))
                if (p.hecho) for (k in 0 until 5) { estrellaLazo(p.o(0.32f + k * 0.09f, 0.5f), p.w * 0.025f, SolidColor(Color(0xFFFFE6A3)), null, 0f) }
                drawPath(Path().apply { moveTo(p.x(0.46f), p.y(0.64f)); lineTo(p.x(0.54f), p.y(0.64f)); lineTo(p.x(0.5f), p.y(0.72f)); close() }, p.metal(0.46f, 0.54f))
            }
            "Curiosos" -> {
                // Celosía: ventana de arco con su enrejado de rombos.
                val ventana = Path().apply { moveTo(p.x(0.2f), p.y(0.76f)); lineTo(p.x(0.2f), p.y(0.36f)); cubicTo(p.x(0.2f), p.y(0.1f), p.x(0.8f), p.y(0.1f), p.x(0.8f), p.y(0.36f)); lineTo(p.x(0.8f), p.y(0.76f)); close() }
                drawPath(ventana, p.b(Color(0xFF3A2414)))
                clipPath(ventana) {
                    val paso = p.w * 0.07f; var k = -20
                    while (k < 30) { val x = p.x(0.2f) + k * paso
                        drawLine(p.c(Color(0xFFD9B566)), Offset(x, p.y(0.08f)), Offset(x + p.h, p.y(0.08f) + p.h), lw * 0.7f)
                        drawLine(p.c(Color(0xFFD9B566)), Offset(x, p.y(0.08f)), Offset(x - p.h, p.y(0.08f) + p.h), lw * 0.7f); k++ }
                }
                drawPath(ventana, p.metal(0.2f, 0.8f), style = Stroke(lw * 1.4f))
            }
            else -> {
                // La granada, con su corona y una hoja.
                val c = p.o(0.5f, 0.46f)
                drawPath(Path().apply { moveTo(c.x, c.y - p.w * 0.26f); lineTo(c.x + p.w * 0.06f, c.y - p.w * 0.36f); lineTo(c.x + p.w * 0.02f, c.y - p.w * 0.28f); lineTo(c.x, c.y - p.w * 0.38f)
                    lineTo(c.x - p.w * 0.02f, c.y - p.w * 0.28f); lineTo(c.x - p.w * 0.06f, c.y - p.w * 0.36f); close() }, p.metal(0.42f, 0.58f))
                drawCircle(Brush.radialGradient(listOf(lerp(rojo, Color.White, 0.3f), rojo, lerp(rojo, Color.Black, 0.3f)), Offset(c.x - p.w * 0.08f, c.y - p.w * 0.08f), p.w * 0.34f), p.w * 0.28f, c)
                drawCircle(p.metal(0.2f, 0.8f), p.w * 0.28f, c, style = Stroke(lw * 0.6f))
                drawPath(Path().apply { moveTo(c.x + p.w * 0.04f, c.y - p.w * 0.28f); quadraticTo(c.x + p.w * 0.3f, c.y - p.w * 0.42f, c.x + p.w * 0.34f, c.y - p.w * 0.28f); quadraticTo(c.x + p.w * 0.2f, c.y - p.w * 0.22f, c.x + p.w * 0.04f, c.y - p.w * 0.28f); close() }, verde)
                caja(p, 0.47f, 0.7f, 0.53f, 0.77f)
            }
        }
    }

    // ---------------------------------------------------------------- Ruta de la Seda

    /**
     * Mesoamérica: el códice plegado en biombo, el lienzo de los cuatro rumbos con la ceiba al centro, el anillo de piedra
     * del juego de pelota con su pelota de hule, la máscara de jade de un señor, una estela con sus columnas de glifos, el
     * vaso pintado para el cacao, la pirámide de nueve cuerpos, la caracola de mar que se toca como trompeta y el penacho de
     * plumas de quetzal.
     */
    internal fun DrawScope.meso(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val jade = p.c(Color(0xFF2F8A60)); val cinabrio = p.c(Color(0xFFA3301E)); val estuco = p.c(Color(0xFFF2E8D2))
        val azul = p.c(Color(0xFF3E8FA8)); val tinta = p.c(Color(0xFF2A1810)); val ocre = p.c(Color(0xFFC9963E))
        when (cat) {
            "Diario" -> {
                // Cuatro páginas del biombo, en zigzag, con el marco rojo, renglones y algún glifo.
                for (k in 0 until 4) {
                    val x0 = 0.16f + k * 0.17f; val sube = if (k % 2 == 0) 0f else 0.04f
                    val pag = Path().apply { moveTo(p.x(x0), p.y(0.16f + sube)); lineTo(p.x(x0 + 0.17f), p.y(0.2f - sube)); lineTo(p.x(x0 + 0.17f), p.y(0.72f - sube)); lineTo(p.x(x0), p.y(0.76f + sube)); close() }
                    drawPath(pag, if (k % 2 == 0) SolidColor(estuco) else SolidColor(lerp(estuco, Color.Black, 0.12f)))
                    drawPath(pag, cinabrio, style = Stroke(lw * 0.6f))
                    if (p.hecho) {
                        drawLine(cinabrio, Offset(p.x(x0 + 0.01f), p.y(0.46f)), Offset(p.x(x0 + 0.16f), p.y(0.46f)), lw * 0.4f)
                        numeralMaya(k * 4 + 3, p.o(x0 + 0.085f, 0.33f), p.w * 0.07f, tinta)
                        drawRoundRect(azul, Offset(p.x(x0 + 0.04f), p.y(0.52f)), Size(p.x(0.09f), p.x(0.09f)), CornerRadius(p.w * 0.02f))
                    }
                }
                caja(p, 0.14f, 0.76f, 0.86f, 0.79f)
            }
            "Mundo" -> {
                // El cosmograma: el cuadro del mundo con sus cuatro rumbos de color (rojo al oriente, blanco al norte, negro al
                // poniente, amarillo al sur) y la ceiba verde en el centro, que une el cielo con el inframundo.
                val c = p.o(0.5f, 0.44f); val l = p.w * 0.3f
                drawRect(p.metal(0.18f, 0.82f), Offset(c.x - l - lw, c.y - l - lw), Size(2 * l + 2 * lw, 2 * l + 2 * lw))
                listOf(cinabrio to Offset(1f, 0f), estuco to Offset(0f, -1f), tinta to Offset(-1f, 0f), ocre to Offset(0f, 1f)).forEach { (col, dir) ->
                    val q = Offset(c.x + dir.x * l * 0.62f, c.y + dir.y * l * 0.62f)
                    drawPath(Path().apply { moveTo(c.x, c.y); lineTo(q.x + dir.y * l * 0.38f + dir.x * l * 0.38f, q.y + dir.x * l * 0.38f + dir.y * l * 0.38f); lineTo(q.x - dir.y * l * 0.38f + dir.x * l * 0.38f, q.y - dir.x * l * 0.38f + dir.y * l * 0.38f); close() }, col)
                }
                drawLine(jade, Offset(c.x, c.y + l * 0.3f), Offset(c.x, c.y - l * 0.3f), lw * 1.6f)
                drawCircle(jade, l * 0.2f, Offset(c.x, c.y - l * 0.32f))
                caja(p, 0.46f, 0.74f + 0.01f, 0.54f, 0.77f)
            }
            "Cuerpo" -> {
                // El anillo de piedra de la cancha, de canto, con su banda tallada, y la pelota de hule.
                val c = p.o(0.5f, 0.38f); val R = p.w * 0.27f
                drawCircle(p.metal(0.23f, 0.77f), R, c)
                drawCircle(if (p.hecho) SolidColor(lerp(estuco, Color.Black, 0.55f)) else SolidColor(p.sombra), R * 0.42f, c)
                drawCircle(p.linea, R * 0.72f, c, style = Stroke(lw * 0.6f))
                if (p.hecho) for (k in 0 until 12) { val a = k * PI / 6; drawCircle(p.linea.copy(alpha = 0.6f), lw, Offset(c.x + (R * 0.86f * cos(a)).toFloat(), c.y + (R * 0.86f * sin(a)).toFloat())) }
                drawCircle(if (p.hecho) Brush.radialGradient(listOf(Color(0xFF6B5A4A), Color(0xFF2A221C)), p.o(0.68f, 0.62f), p.w * 0.1f) else SolidColor(p.sombra), p.w * 0.09f, p.o(0.7f, 0.66f))
                caja(p, 0.44f, 0.38f + 0.27f, 0.56f, 0.77f)
            }
            "Mente" -> {
                // La máscara de jade de un señor: el rostro de teselas, los ojos de concha con pupila de obsidiana y las orejeras.
                val c = p.o(0.5f, 0.42f)
                val cara = Path().apply {
                    moveTo(p.x(0.3f), p.y(0.18f)); quadraticTo(p.x(0.5f), p.y(0.1f), p.x(0.7f), p.y(0.18f)); lineTo(p.x(0.72f), p.y(0.5f))
                    quadraticTo(p.x(0.66f), p.y(0.7f), p.x(0.5f), p.y(0.74f)); quadraticTo(p.x(0.34f), p.y(0.7f), p.x(0.28f), p.y(0.5f)); close()
                }
                drawPath(cara, if (p.hecho) Brush.radialGradient(listOf(lerp(jade, Color.White, 0.25f), jade, lerp(jade, Color.Black, 0.3f)), c, p.w * 0.35f) else SolidColor(p.sombra))
                if (p.hecho) clipPath(cara) {
                    var y = p.y(0.12f); while (y < p.y(0.76f)) { drawLine(p.linea.copy(alpha = 0.3f), Offset(p.x(0.26f), y), Offset(p.x(0.74f), y), lw * 0.3f); y += maxOf(p.w * 0.05f, 1f) }
                    var x = p.x(0.28f); while (x < p.x(0.74f)) { drawLine(p.linea.copy(alpha = 0.2f), Offset(x, p.y(0.1f)), Offset(x + p.w * 0.02f, p.y(0.76f)), lw * 0.3f); x += maxOf(p.w * 0.06f, 1f) }
                }
                listOf(0.4f, 0.6f).forEach { x -> drawOval(estuco, Offset(p.x(x - 0.06f), p.y(0.33f)), Size(p.x(0.12f), p.y(0.06f))); drawCircle(tinta, p.w * 0.022f, p.o(x, 0.36f)) }
                drawPath(Path().apply { moveTo(p.x(0.47f), p.y(0.38f)); lineTo(p.x(0.45f), p.y(0.5f)); lineTo(p.x(0.55f), p.y(0.5f)); lineTo(p.x(0.53f), p.y(0.38f)) }, p.linea, style = Stroke(lw * 0.6f))
                drawOval(estuco, Offset(p.x(0.42f), p.y(0.56f)), Size(p.x(0.16f), p.y(0.05f)))
                listOf(0.22f, 0.78f).forEach { x -> drawCircle(p.metal(x - 0.06f, x + 0.06f), p.w * 0.06f, p.o(x, 0.4f)); drawCircle(p.linea, p.w * 0.02f, p.o(x, 0.4f)) }
                caja(p, 0.44f, 0.74f, 0.56f, 0.77f)
            }
            "Cultura" -> {
                // La estela: la piedra alta de remate redondo con dos columnas de bloques de glifos y su fecha arriba.
                val piedra = Path().apply { moveTo(p.x(0.3f), p.y(0.77f)); lineTo(p.x(0.3f), p.y(0.2f)); quadraticTo(p.x(0.5f), p.y(0.06f), p.x(0.7f), p.y(0.2f)); lineTo(p.x(0.7f), p.y(0.77f)); close() }
                drawPath(piedra, p.metal(0.3f, 0.7f))
                drawPath(piedra, p.linea, style = Stroke(lw * 0.5f))
                if (p.hecho) {
                    numeralMaya(9, p.o(0.5f, 0.2f), p.w * 0.06f, p.linea)
                    for (fila in 0 until 5) for (col in 0 until 2) {
                        val x = 0.35f + col * 0.155f; val y = 0.28f + fila * 0.095f
                        drawRoundRect(p.linea, Offset(p.x(x), p.y(y)), Size(p.x(0.14f), p.y(0.08f)), CornerRadius(p.w * 0.02f), style = Stroke(lw * 0.5f))
                        drawCircle(p.linea.copy(alpha = 0.5f), p.w * 0.012f, Offset(p.x(x + 0.05f), p.y(y + 0.04f)))
                    }
                }
            }
            "Gente" -> {
                // El vaso de cacao: cilindro pintado de ocre con el borde rojo, una banda de glifos y dos figuras.
                val x0 = 0.28f; val x1 = 0.72f
                drawRect(if (p.hecho) Brush.horizontalGradient(listOf(lerp(ocre, Color.Black, 0.3f), ocre, lerp(ocre, Color.White, 0.2f), ocre, lerp(ocre, Color.Black, 0.3f)), p.x(x0), p.x(x1)) else SolidColor(p.sombra),
                    Offset(p.x(x0), p.y(0.2f)), Size(p.x(x1 - x0), p.y(0.56f)))
                drawOval(if (p.hecho) SolidColor(lerp(ocre, Color.Black, 0.5f)) else SolidColor(p.sombra), Offset(p.x(x0), p.y(0.18f)), Size(p.x(x1 - x0), p.y(0.05f)))
                drawRect(cinabrio, Offset(p.x(x0), p.y(0.22f)), Size(p.x(x1 - x0), p.y(0.04f)))
                if (p.hecho) {
                    for (k in 0 until 4) drawRoundRect(tinta, Offset(p.x(x0 + 0.03f + k * 0.105f), p.y(0.29f)), Size(p.x(0.07f), p.y(0.06f)), CornerRadius(p.w * 0.015f), style = Stroke(lw * 0.4f))
                    listOf(0.4f, 0.6f).forEach { x ->
                        drawCircle(tinta, p.w * 0.03f, p.o(x, 0.44f)); drawLine(tinta, p.o(x, 0.47f), p.o(x, 0.62f), lw * 1.2f)
                        drawLine(tinta, p.o(x, 0.52f), p.o(x + (if (x < 0.5f) 0.06f else -0.06f), 0.5f), lw * 0.8f)
                    }
                }
                drawRect(cinabrio, Offset(p.x(x0), p.y(0.72f)), Size(p.x(x1 - x0), p.y(0.04f)))
            }
            "Constancia" -> {
                // La pirámide de nueve cuerpos, con su escalinata y el templo en lo alto.
                piramideMaya(p.o(0.5f, 0.77f), p.w * 0.66f, p.h * 0.62f, if (p.hecho) lerp(estuco, Color.Black, 0.08f) else p.sombra, p.linea, cinabrio, gradas = 9)
            }
            "Curiosos" -> {
                // La caracola de mar: la espiral de la concha, el labio rosado y la boquilla para tocarla como trompeta.
                val c = p.o(0.44f, 0.46f)
                val concha = Path().apply {
                    moveTo(p.x(0.2f), p.y(0.5f)); quadraticTo(p.x(0.26f), p.y(0.22f), p.x(0.5f), p.y(0.24f)); quadraticTo(p.x(0.74f), p.y(0.28f), p.x(0.8f), p.y(0.42f))
                    quadraticTo(p.x(0.7f), p.y(0.66f), p.x(0.46f), p.y(0.68f)); quadraticTo(p.x(0.24f), p.y(0.68f), p.x(0.2f), p.y(0.5f)); close()
                }
                drawPath(concha, if (p.hecho) Brush.linearGradient(listOf(Color(0xFFF6E7D2), Color(0xFFE8B49A), Color(0xFFC98A6A)), p.o(0.2f, 0.3f), p.o(0.8f, 0.7f)) else SolidColor(p.sombra))
                if (p.hecho) {
                    for (k in 1..4) drawArc(p.linea.copy(alpha = 0.5f), 200f, 220f, false, Offset(c.x - p.w * 0.04f * k, c.y - p.w * 0.035f * k), Size(p.w * 0.08f * k, p.w * 0.07f * k), style = Stroke(lw * 0.5f))
                    for (k in 0 until 5) drawCircle(p.c(Color(0xFFC98A6A)), p.w * 0.018f, p.o(0.36f + k * 0.08f, 0.26f + (if (k % 2 == 0) 0f else -0.02f)))
                }
                drawPath(Path().apply { moveTo(p.x(0.8f), p.y(0.42f)); lineTo(p.x(0.88f), p.y(0.36f)); lineTo(p.x(0.88f), p.y(0.44f)); close() }, p.metal(0.8f, 0.88f))
                caja(p, 0.4f, 0.68f, 0.52f, 0.77f)
            }
            else -> {
                // El penacho: un abanico de plumas largas de quetzal sobre una banda de jade con su cuenta central.
                for (k in 0 until 9) {
                    val ang = -90f + (k - 4) * 15f
                    plumaQuetzal(p.o(0.5f, 0.58f), p.w * (0.44f - kotlin.math.abs(k - 4) * 0.03f), ang, if (p.hecho) lerp(jade, azul, (k % 3) * 0.2f) else p.sombra, if (p.hecho) lerp(jade, Color.Black, 0.35f) else p.sombra)
                }
                drawRoundRect(p.metal(0.3f, 0.7f), Offset(p.x(0.3f), p.y(0.56f)), Size(p.x(0.4f), p.y(0.08f)), CornerRadius(p.w * 0.02f))
                cuentaJade(p.o(0.5f, 0.6f), p.w * 0.045f, jade, Color.White)
                caja(p, 0.46f, 0.64f, 0.54f, 0.77f)
            }
        }
    }

    internal fun DrawScope.seda(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val cinabrio = p.c(Color(0xFFC0392B)); val jade = p.c(Color(0xFF4E9A72)); val lapis = p.c(Color(0xFF1F3A8A)); val seda = p.c(Color(0xFFF2E6C9))
        when (cat) {
            "Diario" -> {
                // El sello de piedra y su impresión de cinabrio (signos inventados, en cuadrícula).
                drawRoundRect(p.b(Color(0xFF4E9A72)), Offset(p.x(0.14f), p.y(0.3f)), Size(p.x(0.26f), p.y(0.44f)), CornerRadius(p.w * 0.02f))
                drawCircle(p.metal(0.18f, 0.36f), p.w * 0.1f, p.o(0.27f, 0.3f))
                val q = Rect(p.x(0.46f), p.y(0.26f), p.x(0.86f), p.y(0.26f) + p.x(0.4f))
                drawRect(cinabrio, q.topLeft, q.size)
                drawRect(seda, Offset(q.left + lw, q.top + lw), Size(q.width - 2 * lw, q.height - 2 * lw), style = Stroke(lw * 0.8f))
                if (p.hecho) {
                    val u = q.width / 6
                    for (i in 0 until 2) for (j in 0 until 2) {
                        val o = Offset(q.left + u * (0.8f + i * 2.4f), q.top + u * (0.8f + j * 2.4f))
                        drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + u * 1.6f, o.y); lineTo(o.x + u * 1.6f, o.y + u * 0.8f); lineTo(o.x + u * 0.5f, o.y + u * 0.8f); lineTo(o.x + u * 0.5f, o.y + u * 1.6f); lineTo(o.x + u * 1.6f, o.y + u * 1.6f) }, Color(0xFFF2E6C9), style = Stroke(lw * 0.9f))
                    }
                }
            }
            "Mundo" -> {
                // Brújula de cuchara: la placa cuadrada con sus anillos y la cuchara de imán que apunta al sur.
                caja(p, 0.12f, 0.2f, 0.88f, 0.74f)
                val c = p.o(0.5f, 0.47f)
                drawCircle(p.c(Color(0xFF2B2B33)).copy(alpha = if (p.hecho) 0.25f else 1f), p.w * 0.25f, c)
                listOf(0.25f, 0.18f).forEach { r -> drawCircle(p.linea, p.w * r, c, style = Stroke(lw * 0.5f)) }
                for (k in 0 until 24) { val a = k * PI / 12; drawLine(p.linea, Offset(c.x + (p.w * 0.25f * cos(a)).toFloat(), c.y + (p.w * 0.25f * sin(a)).toFloat()), Offset(c.x + (p.w * 0.3f * cos(a)).toFloat(), c.y + (p.w * 0.3f * sin(a)).toFloat()), lw * 0.4f) }
                drawOval(p.b(Color(0xFF2B2B33)), Offset(c.x - p.w * 0.1f, c.y - p.w * 0.08f), Size(p.w * 0.2f, p.w * 0.16f))
                drawLine(p.c(Color(0xFF2B2B33)), Offset(c.x, c.y + p.w * 0.06f), Offset(c.x, c.y + p.w * 0.24f), lw * 1.4f, StrokeCap.Round)
            }
            "Cuerpo" -> {
                // Disco de jade (bi), con su borla roja.
                val c = p.o(0.5f, 0.38f)
                drawCircle(Brush.radialGradient(listOf(lerp(jade, Color.White, 0.3f), jade, lerp(jade, Color.Black, 0.25f)), c, p.w * 0.32f), p.w * 0.3f, c)
                drawCircle(Color.Transparent, p.w * 0.1f, c)
                drawCircle(p.c(Color(0xFF1E3A2E)).copy(alpha = 0.6f), p.w * 0.1f, c)
                if (p.hecho) for (k in 0 until 12) { val a = k * PI / 6; drawCircle(Color.White.copy(alpha = 0.3f), p.w * 0.02f, Offset(c.x + (p.w * 0.2f * cos(a)).toFloat(), c.y + (p.w * 0.2f * sin(a)).toFloat())) }
                drawLine(cinabrio, Offset(c.x, c.y + p.w * 0.3f), Offset(c.x, p.y(0.66f)), lw)
                drawPath(Path().apply { moveTo(c.x - p.w * 0.04f, p.y(0.66f)); lineTo(c.x + p.w * 0.04f, p.y(0.66f)); lineTo(c.x + p.w * 0.06f, p.y(0.76f)); lineTo(c.x - p.w * 0.06f, p.y(0.76f)); close() }, cinabrio)
            }
            "Mente" -> {
                // El jarrón de porcelana azul y blanca, con olas y nubes.
                val v = Path().apply {
                    moveTo(p.x(0.42f), p.y(0.06f)); lineTo(p.x(0.58f), p.y(0.06f)); lineTo(p.x(0.57f), p.y(0.12f))
                    cubicTo(p.x(0.82f), p.y(0.16f), p.x(0.8f), p.y(0.4f), p.x(0.66f), p.y(0.6f)); lineTo(p.x(0.62f), p.y(0.74f)); lineTo(p.x(0.38f), p.y(0.74f)); lineTo(p.x(0.34f), p.y(0.6f))
                    cubicTo(p.x(0.2f), p.y(0.4f), p.x(0.18f), p.y(0.16f), p.x(0.43f), p.y(0.12f)); close()
                }
                drawPath(v, p.b(Color(0xFFF4F1EA)))
                if (p.hecho) clipPath(v) {
                    drawRect(lapis, Offset(p.x(0.2f), p.y(0.06f)), Size(p.x(0.6f), p.y(0.06f)))
                    for (k in 0 until 6) drawArc(lapis, 180f, 180f, false, Offset(p.x(0.2f + k * 0.1f), p.y(0.6f)), Size(p.x(0.1f), p.y(0.06f)), style = Stroke(lw))
                    drawLine(lapis, p.o(0.2f, 0.56f), p.o(0.8f, 0.56f), lw * 0.6f); drawLine(lapis, p.o(0.2f, 0.2f), p.o(0.8f, 0.2f), lw * 0.6f)
                    listOf(0.36f to 0.32f, 0.6f to 0.42f).forEach { (x, y) -> for (j in 0 until 3) drawCircle(lapis, p.w * (0.05f - j * 0.012f), p.o(x + j * 0.04f, y), style = Stroke(lw * 0.7f)) }
                }
                drawPath(v, p.linea, style = Stroke(lw * 0.5f))
            }
            "Cultura" -> {
                // Un carrete de seda: discos de madera, hilos de colores y la hebra suelta.
                val madera = p.c(Color(0xFF7A5232))
                drawOval(madera, Offset(p.x(0.2f), p.y(0.1f)), Size(p.x(0.6f), p.y(0.08f)))
                val hilos = listOf(Color(0xFFC0392B), Color(0xFFE2B34A), Color(0xFF1F3A8A), Color(0xFF4E9A72), Color(0xFFB0447A))
                for (k in 0 until 10) drawRect(p.c(hilos[k % hilos.size]), Offset(p.x(0.27f), p.y(0.15f + k * 0.05f)), Size(p.x(0.46f), p.y(0.05f)))
                if (p.hecho) for (k in 0 until 40) drawLine(Color.White.copy(alpha = 0.15f), Offset(p.x(0.27f), p.y(0.15f + k * 0.0125f)), Offset(p.x(0.73f), p.y(0.152f + k * 0.0125f)), lw * 0.2f)
                drawOval(madera, Offset(p.x(0.2f), p.y(0.64f)), Size(p.x(0.6f), p.y(0.08f)))
                drawPath(Path().apply { moveTo(p.x(0.73f), p.y(0.4f)); cubicTo(p.x(0.95f), p.y(0.45f), p.x(0.8f), p.y(0.7f), p.x(0.96f), p.y(0.76f)) }, p.c(Color(0xFFC0392B)), style = Stroke(lw * 0.6f))
            }
            "Gente" -> {
                // El farolillo rojo con sus costillas, tapas de oro y la borla.
                drawLine(p.c(Color(0xFF3A2414)), p.o(0.5f, 0.02f), p.o(0.5f, 0.12f), lw)
                caja(p, 0.38f, 0.12f, 0.62f, 0.17f)
                if (p.hecho) drawOval(Brush.radialGradient(listOf(Color(0xFFFFB347).copy(alpha = 0.4f), Color.Transparent), p.o(0.5f, 0.4f), p.w * 0.45f), Offset(p.x(0.05f), p.y(0.1f)), Size(p.x(0.9f), p.y(0.6f)))
                drawOval(Brush.horizontalGradient(listOf(lerp(cinabrio, Color.Black, 0.3f), cinabrio, lerp(cinabrio, Color.White, 0.25f), cinabrio, lerp(cinabrio, Color.Black, 0.3f)), p.x(0.16f), p.x(0.84f)),
                    Offset(p.x(0.16f), p.y(0.16f)), Size(p.x(0.68f), p.y(0.46f)))
                for (k in 1..4) drawArc(p.c(Color(0xFF7A1A12)), -90f, 180f, false, Offset(p.x(0.5f - k * 0.08f), p.y(0.16f)), Size(p.x(k * 0.16f), p.y(0.46f)), style = Stroke(lw * 0.4f))
                caja(p, 0.38f, 0.6f, 0.62f, 0.65f)
                for (k in 0 until 5) drawLine(p.c(Color(0xFFE2B34A)), p.o(0.46f + k * 0.02f, 0.65f), p.o(0.45f + k * 0.025f, 0.77f), lw * 0.5f)
            }
            "Constancia" -> {
                // La moneda de cobre con agujero cuadrado y cuatro signos inventados.
                val c = p.o(0.5f, 0.4f); val R = p.w * 0.32f
                drawCircle(p.metal(0.18f, 0.82f), R, c); drawCircle(p.linea, R * 0.9f, c, style = Stroke(lw * 0.6f))
                drawRect(p.c(Color(0xFF1E1A16)), Offset(c.x - R * 0.24f, c.y - R * 0.24f), Size(R * 0.48f, R * 0.48f))
                drawRect(p.linea, Offset(c.x - R * 0.32f, c.y - R * 0.32f), Size(R * 0.64f, R * 0.64f), style = Stroke(lw * 0.6f))
                if (p.hecho) listOf(0.0, PI / 2, PI, 3 * PI / 2).forEachIndexed { i, a ->
                    val o = Offset(c.x + (R * 0.62f * cos(a - PI / 2)).toFloat(), c.y + (R * 0.62f * sin(a - PI / 2)).toFloat())
                    runas(Offset(o.x - R * 0.12f, o.y), R * 0.24f, R * 0.22f, p.linea, lw * 0.6f, 1, i + 7)
                }
                caja(p, 0.47f, 0.4f + 0.32f * p.w / p.h, 0.53f, 0.77f)
            }
            "Curiosos" -> {
                // El ábaco: siete varillas, dos cuentas arriba y cinco abajo.
                caja(p, 0.1f, 0.12f, 0.9f, 0.16f, p.b(Color(0xFF5A3A22))); caja(p, 0.1f, 0.66f, 0.9f, 0.7f, p.b(Color(0xFF5A3A22)))
                caja(p, 0.1f, 0.12f, 0.14f, 0.7f, p.b(Color(0xFF5A3A22))); caja(p, 0.86f, 0.12f, 0.9f, 0.7f, p.b(Color(0xFF5A3A22)))
                caja(p, 0.14f, 0.3f, 0.86f, 0.325f, p.b(Color(0xFF5A3A22)))
                for (k in 0 until 7) {
                    val x = 0.2f + k * 0.1f
                    drawLine(p.linea, p.o(x, 0.16f), p.o(x, 0.66f), lw * 0.4f)
                    for (j in 0 until 2) drawOval(p.metal(x - 0.04f, x + 0.04f), Offset(p.x(x - 0.04f), p.y(0.17f + j * 0.055f)), Size(p.x(0.08f), p.y(0.045f)))
                    for (j in 0 until 5) drawOval(p.metal(x - 0.04f, x + 0.04f), Offset(p.x(x - 0.04f), p.y(0.38f + j * 0.055f) - (if (j < k % 3) p.y(0.03f) else 0f)), Size(p.x(0.08f), p.y(0.045f)))
                }
                caja(p, 0.3f, 0.7f, 0.34f, 0.77f); caja(p, 0.66f, 0.7f, 0.7f, 0.77f)
            }
            else -> {
                // La ventana lunar: un muro con un vano redondo y una rama florida al otro lado.
                val c = p.o(0.5f, 0.4f); val R = p.w * 0.25f
                val muro = Path().apply { fillType = PathFillType.EvenOdd; addRect(Rect(p.x(0.1f), p.y(0.1f), p.x(0.9f), p.y(0.74f))); addOval(Rect(c, R)) }
                drawPath(muro, p.b(Color(0xFFE9E1CF)))
                if (p.hecho) {
                    drawRect(Color(0xFF3A3A3A), Offset(p.x(0.08f), p.y(0.07f)), Size(p.x(0.84f), p.y(0.04f)))
                    clipPath(Path().apply { addOval(Rect(c, R)) }) {
                        drawRect(Color(0xFFBFD8E8), Offset(c.x - R, c.y - R), Size(2 * R, 2 * R))
                        drawPath(Path().apply { moveTo(c.x - R, c.y + R * 0.4f); quadraticTo(c.x, c.y - R * 0.2f, c.x + R, c.y + R * 0.5f); lineTo(c.x + R, c.y + R); lineTo(c.x - R, c.y + R); close() }, Color(0xFF7FA38A))
                        drawPath(Path().apply { moveTo(c.x - R, c.y - R * 0.5f); quadraticTo(c.x - R * 0.2f, c.y - R * 0.4f, c.x + R * 0.4f, c.y - R * 0.8f) }, Color(0xFF3A2414), style = Stroke(lw * 0.8f))
                        listOf(-0.6f to -0.48f, -0.2f to -0.46f, 0.15f to -0.62f, 0.35f to -0.76f).forEach { (dx, dy) -> drawCircle(Color(0xFFE8A0B4), p.w * 0.025f, Offset(c.x + R * dx, c.y + R * dy)) }
                    }
                }
                drawCircle(p.metal(0.25f, 0.75f), R, c, style = Stroke(lw * 1.2f))
            }
        }
    }

    // ---------------------------------------------------------------- Japón Edo

    /**
     * Las piezas de Japón Edo: el cuaderno cosido, el sombrero de caminante, las sandalias de paja, el tablero de go, el
     * abanico con el Fuji, el farol de la posada, el koban de oro, la linterna de piedra de un templo y el sello. Los
     * signos de los sellos y del farol son trazos inventados.
     */
    internal fun DrawScope.edo(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val bermellon = p.c(Color(0xFFC9442E)); val indigo = p.c(Color(0xFF1E3A5F)); val washi = p.c(Color(0xFFF3EDDD)); val paja = p.c(Color(0xFFC9B26A))
        val sumi = p.c(Color(0xFF1B1A1E))
        when (cat) {
            "Diario" -> {
                // Cuaderno cosido a la japonesa: tapa de índigo, la etiqueta del título y la costura de cuatro agujeros.
                val r = Rect(p.x(0.2f), p.y(0.1f), p.x(0.8f), p.y(0.72f))
                drawRect(washi, Offset(r.left + lw * 1.5f, r.top + lw * 1.5f), r.size)
                drawRect(Brush.verticalGradient(listOf(lerp(indigo, Color.White, 0.12f), indigo, lerp(indigo, Color.Black, 0.25f)), r.top, r.bottom), r.topLeft, r.size)
                drawRect(washi, Offset(r.left + r.width * 0.1f, r.top + r.height * 0.06f), Size(r.width * 0.16f, r.height * 0.6f))
                if (p.hecho) {
                    drawRect(Color(0xFFC9442E), Offset(r.left + r.width * 0.1f + lw * 0.5f, r.top + r.height * 0.06f + lw * 0.5f), Size(r.width * 0.16f - lw, r.height * 0.6f - lw), style = Stroke(lw * 0.5f))
                    for (j in 0 until 4) drawLine(Color(0xFF1B1A1E), Offset(r.left + r.width * 0.18f, r.top + r.height * (0.14f + j * 0.13f)), Offset(r.left + r.width * 0.18f, r.top + r.height * (0.22f + j * 0.13f)), lw * 0.8f)
                }
                val xs = r.right - r.width * 0.08f
                drawLine(washi, Offset(xs, r.top), Offset(xs, r.bottom), lw * 0.6f)
                listOf(0.12f, 0.37f, 0.63f, 0.88f).forEach { f -> val y = r.top + r.height * f; drawLine(washi, Offset(xs, y), Offset(r.right, y), lw * 0.6f); drawCircle(sumi, lw * 0.7f, Offset(xs, y)) }
            }
            "Mundo" -> {
                // El sugegasa: el sombrero cónico de paja del caminante, con su cinta para la barbilla.
                val cima = p.o(0.5f, 0.16f)
                val cono = Path().apply { moveTo(cima.x, cima.y); lineTo(p.x(0.92f), p.y(0.56f)); quadraticTo(p.x(0.5f), p.y(0.66f), p.x(0.08f), p.y(0.56f)); close() }
                drawPath(cono, Brush.horizontalGradient(listOf(lerp(paja, Color.Black, 0.25f), paja, lerp(paja, Color.White, 0.25f), paja), p.x(0.08f), p.x(0.92f)))
                if (p.hecho) for (k in 1..7) { val f = k / 8f; drawLine(Color(0xFF7A6436).copy(alpha = 0.5f), cima, Offset(p.x(0.08f + 0.84f * f), p.y(0.56f + 0.08f * (1 - (2 * f - 1) * (2 * f - 1)))), lw * 0.4f) }
                for (k in 1..3) drawArc(p.linea, 20f, 140f, false, Offset(cima.x - p.w * 0.14f * k, cima.y - p.w * 0.02f * k), Size(p.w * 0.28f * k, p.w * 0.2f * k), style = Stroke(lw * 0.4f))
                drawPath(Path().apply { moveTo(p.x(0.3f), p.y(0.6f)); quadraticTo(p.x(0.5f), p.y(0.78f), p.x(0.7f), p.y(0.6f)) }, bermellon, style = Stroke(lw * 0.8f))
            }
            "Cuerpo" -> {
                // Un par de waraji: sandalias de paja trenzada con sus cordones.
                listOf(0.32f, 0.68f).forEachIndexed { i, cx ->
                    val s = Rect(p.x(cx - 0.13f), p.y(0.14f + i * 0.06f), p.x(cx + 0.13f), p.y(0.68f + i * 0.04f))
                    drawRoundRect(Brush.verticalGradient(listOf(lerp(paja, Color.White, 0.2f), paja), s.top, s.bottom), s.topLeft, s.size, CornerRadius(s.width * 0.45f))
                    if (p.hecho) { var y = s.top + s.height * 0.1f; while (y < s.bottom - s.height * 0.05f) { drawLine(Color(0xFF7A6436).copy(alpha = 0.55f), Offset(s.left + s.width * 0.1f, y), Offset(s.right - s.width * 0.1f, y), lw * 0.4f); y += maxOf(s.height * 0.07f, 1f) } }
                    drawLine(bermellon, Offset(s.center.x, s.top + s.height * 0.08f), Offset(s.left, s.top + s.height * 0.5f), lw * 0.7f)
                    drawLine(bermellon, Offset(s.center.x, s.top + s.height * 0.08f), Offset(s.right, s.top + s.height * 0.5f), lw * 0.7f)
                }
            }
            "Mente" -> {
                // Tablero de go sobre sus patas, con su rejilla y unas piedras blancas y negras.
                val t = Rect(p.x(0.12f), p.y(0.2f), p.x(0.88f), p.y(0.6f))
                val madera = p.c(Color(0xFFD9B26A))
                drawRect(madera, t.topLeft, t.size)
                drawRect(p.c(Color(0xFF9C7A3E)), Offset(t.left, t.bottom), Size(t.width, p.y(0.06f)))
                listOf(0.2f, 0.8f).forEach { f -> caja(p, f - 0.05f, 0.66f, f + 0.05f, 0.76f, p.b(Color(0xFF6E5228))) }
                for (k in 0..8) {
                    val fx = t.left + t.width * (0.06f + k * 0.11f); drawLine(p.linea, Offset(fx, t.top + t.height * 0.08f), Offset(fx, t.bottom - t.height * 0.08f), lw * 0.3f)
                    val fy = t.top + t.height * (0.08f + k * 0.105f); drawLine(p.linea, Offset(t.left + t.width * 0.06f, fy), Offset(t.right - t.width * 0.06f, fy), lw * 0.3f)
                }
                if (p.hecho) listOf(Triple(3, 2, true), Triple(4, 3, false), Triple(5, 2, true), Triple(4, 5, false), Triple(2, 4, true), Triple(6, 4, false)).forEach { (i, j, negra) ->
                    drawCircle(if (negra) Color(0xFF1B1A1E) else Color(0xFFF6F2EA), t.width * 0.045f, Offset(t.left + t.width * (0.06f + i * 0.11f), t.top + t.height * (0.08f + j * 0.105f)))
                }
            }
            "Cultura" -> {
                // Abanico plegable (sensu) abierto, con el monte Fuji pintado y sus varillas.
                val c = p.o(0.5f, 0.66f); val R = p.w * 0.4f; val r0 = p.w * 0.14f
                val hoja = Path().apply { arcTo(Rect(c, R), 200f, 140f, false); arcTo(Rect(c, r0), 340f, -140f, false); close() }
                drawPath(hoja, washi)
                if (p.hecho) clipPath(hoja) {
                    drawPath(Path().apply { moveTo(c.x - R * 0.55f, c.y - R * 0.35f); lineTo(c.x - R * 0.12f, c.y - R * 0.82f); lineTo(c.x + R * 0.12f, c.y - R * 0.82f); lineTo(c.x + R * 0.55f, c.y - R * 0.35f); close() }, Color(0xFF1E3A5F))
                    drawPath(Path().apply { moveTo(c.x - R * 0.2f, c.y - R * 0.73f); lineTo(c.x - R * 0.12f, c.y - R * 0.82f); lineTo(c.x + R * 0.12f, c.y - R * 0.82f); lineTo(c.x + R * 0.2f, c.y - R * 0.73f); close() }, Color.White)
                    drawCircle(Color(0xFFC9442E), R * 0.1f, Offset(c.x + R * 0.45f, c.y - R * 0.8f))
                }
                for (k in 0..10) { val a = Math.toRadians(200.0 + k * 14.0); drawLine(p.linea, Offset(c.x + (r0 * cos(a)).toFloat(), c.y + (r0 * sin(a)).toFloat()), Offset(c.x + (R * cos(a)).toFloat(), c.y + (R * sin(a)).toFloat()), lw * 0.3f) }
                drawPath(hoja, p.linea, style = Stroke(lw * 0.5f))
                drawCircle(p.metal(0.46f, 0.54f), p.w * 0.03f, c)
            }
            "Gente" -> {
                // El chōchin de la posada: farol cilíndrico de papel y bambú, con sus tapas negras y un mon rojo.
                drawLine(p.c(Color(0xFF3A2414)), p.o(0.5f, 0.02f), p.o(0.5f, 0.1f), lw)
                caja(p, 0.34f, 0.1f, 0.66f, 0.16f, p.b(Color(0xFF1B1A1E)))
                if (p.hecho) drawOval(Brush.radialGradient(listOf(Color(0xFFFFC46B).copy(alpha = 0.4f), Color.Transparent), p.o(0.5f, 0.4f), p.w * 0.45f), Offset(p.x(0.05f), p.y(0.1f)), Size(p.x(0.9f), p.y(0.6f)))
                drawRoundRect(Brush.horizontalGradient(listOf(lerp(washi, Color.Black, 0.2f), washi, lerp(washi, Color.Black, 0.12f)), p.x(0.24f), p.x(0.76f)), Offset(p.x(0.24f), p.y(0.16f)), Size(p.x(0.52f), p.y(0.46f)), CornerRadius(p.w * 0.12f))
                for (k in 1..8) drawLine(p.c(Color(0xFFB8A882)), p.o(0.25f, 0.16f + k * 0.051f), p.o(0.75f, 0.16f + k * 0.051f), lw * 0.3f)
                if (p.hecho) { for (i in 0 until 5) drawCircle(Color(0xFFC9442E), p.w * 0.04f, Offset(p.x(0.5f) + (p.w * 0.06f * cos(-PI / 2 + i * 2 * PI / 5)).toFloat(), p.y(0.38f) + (p.w * 0.06f * sin(-PI / 2 + i * 2 * PI / 5)).toFloat())) }
                caja(p, 0.34f, 0.62f, 0.66f, 0.68f, p.b(Color(0xFF1B1A1E)))
                drawLine(p.c(Color(0xFF3A2414)), p.o(0.5f, 0.68f), p.o(0.5f, 0.77f), lw)
            }
            "Constancia" -> {
                // El koban: la moneda ovalada de oro, con las rayas del martillo y dos sellos de la ceca.
                val o = Rect(p.x(0.26f), p.y(0.08f), p.x(0.74f), p.y(0.74f))
                drawOval(p.metal(0.26f, 0.74f), o.topLeft, o.size)
                drawOval(p.linea, Offset(o.left + lw, o.top + lw), Size(o.width - 2 * lw, o.height - 2 * lw), style = Stroke(lw * 0.5f))
                if (p.hecho) {
                    for (k in 1..9) { val y = o.top + o.height * (0.1f + k * 0.08f); drawLine(p.linea.copy(alpha = 0.35f), Offset(o.left + o.width * 0.18f, y), Offset(o.right - o.width * 0.18f, y), lw * 0.35f) }
                    listOf(0.2f, 0.8f).forEach { f -> val c = Offset(o.center.x, o.top + o.height * f); drawCircle(p.linea, o.width * 0.1f, c, style = Stroke(lw * 0.5f)); drawLine(p.linea, Offset(c.x - o.width * 0.05f, c.y), Offset(c.x + o.width * 0.05f, c.y), lw * 0.5f) }
                    drawRect(p.linea, Offset(o.center.x - o.width * 0.12f, o.center.y - o.height * 0.12f), Size(o.width * 0.24f, o.height * 0.24f), style = Stroke(lw * 0.5f))
                }
            }
            "Curiosos" -> {
                // Tōrō: la linterna de piedra de los jardines de los templos, con su tejadillo y la luz dentro.
                val piedraC = p.c(Color(0xFF9A9890))
                caja(p, 0.3f, 0.66f, 0.7f, 0.76f, p.b(Color(0xFF8A8880)))
                caja(p, 0.44f, 0.46f, 0.56f, 0.66f, p.b(Color(0xFF9A9890)))
                caja(p, 0.32f, 0.42f, 0.68f, 0.47f, p.b(Color(0xFF8A8880)))
                caja(p, 0.36f, 0.24f, 0.64f, 0.42f, p.b(Color(0xFFA8A69E)))
                if (p.hecho) drawRect(Color(0xFFFFC46B), Offset(p.x(0.43f), p.y(0.28f)), Size(p.x(0.14f), p.y(0.1f)))
                else drawRect(p.sombra, Offset(p.x(0.43f), p.y(0.28f)), Size(p.x(0.14f), p.y(0.1f)))
                drawPath(Path().apply { moveTo(p.x(0.18f), p.y(0.25f)); quadraticTo(p.x(0.36f), p.y(0.2f), p.x(0.5f), p.y(0.1f)); quadraticTo(p.x(0.64f), p.y(0.2f), p.x(0.82f), p.y(0.25f)); close() }, piedraC)
                drawCircle(piedraC, p.w * 0.045f, p.o(0.5f, 0.08f))
            }
            else -> {
                // El sello de la posta: el bloque de piedra con su asa y, al lado, la impresión en bermellón con 足 en escritura de sello.
                drawRoundRect(p.metal(0.12f, 0.36f), Offset(p.x(0.12f), p.y(0.34f)), Size(p.x(0.24f), p.y(0.4f)), CornerRadius(p.w * 0.02f))
                drawCircle(p.metal(0.16f, 0.32f), p.w * 0.07f, p.o(0.24f, 0.32f))
                val q = Rect(p.x(0.44f), p.y(0.22f), p.x(0.88f), p.y(0.22f) + p.x(0.44f))
                drawRoundRect(bermellon, q.topLeft, q.size, CornerRadius(p.w * 0.02f))
                val blanco = washi; val g = lw * 1.1f
                drawRect(blanco, Offset(q.left + lw * 1.2f, q.top + lw * 1.2f), Size(q.width - 2.4f * lw, q.height - 2.4f * lw), style = Stroke(lw * 0.6f))
                if (p.hecho) {
                    val cx = q.center.x; val s = q.width * 0.36f; val cy = q.center.y
                    drawRect(blanco, Offset(cx - s * 0.32f, cy - s * 0.72f), Size(s * 0.64f, s * 0.4f), style = Stroke(g))
                    drawLine(blanco, Offset(cx, cy - s * 0.2f), Offset(cx, cy + s * 0.55f), g)
                    drawLine(blanco, Offset(cx, cy + s * 0.08f), Offset(cx + s * 0.36f, cy + s * 0.08f), g)
                    drawLine(blanco, Offset(cx - s * 0.06f, cy + s * 0.22f), Offset(cx - s * 0.55f, cy + s * 0.6f), g)
                    drawLine(blanco, Offset(cx, cy + s * 0.55f), Offset(cx + s * 0.6f, cy + s * 0.6f), g)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Mongol

    internal fun DrawScope.mongol(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val cielo = p.c(Color(0xFF2E6DB4)); val rojo = p.c(Color(0xFFB0281E)); val fieltro = p.c(Color(0xFFEDE6D6)); val madera = p.c(Color(0xFF8A5A2B))
        when (cat) {
            "Diario" -> {
                // Rollo de órdenes del yam, con escritura vertical inventada y sus varillas.
                drawRect(p.b(Color(0xFFF1E6C8)), Offset(p.x(0.2f), p.y(0.12f)), Size(p.x(0.6f), p.y(0.58f)))
                if (p.hecho) for (k in 0 until 6) {
                    val x = p.x(0.28f + k * 0.09f); val path = Path().apply { moveTo(x, p.y(0.18f)); var y = p.y(0.18f); var s = 1f; while (y < p.y(0.62f)) { quadraticTo(x + s * p.w * 0.02f, y + p.h * 0.02f, x, y + p.h * 0.04f); y += p.h * 0.04f; s = -s } }
                    drawPath(path, Color(0xFF2A1E0E).copy(alpha = 0.7f), style = Stroke(lw * 0.6f))
                }
                listOf(0.12f, 0.7f).forEach { y -> drawRoundRect(madera, Offset(p.x(0.14f), p.y(y) - p.h * 0.02f), Size(p.x(0.72f), p.h * 0.04f), CornerRadius(p.h * 0.02f)) }
                drawCircle(rojo, p.w * 0.06f, p.o(0.7f, 0.6f))
            }
            "Mundo" -> {
                // Mapa de la estepa: montes, el río, la ruta punteada y las yurtas de las postas.
                drawPath(Path().apply { moveTo(p.x(0.08f), p.y(0.18f)); lineTo(p.x(0.92f), p.y(0.14f)); lineTo(p.x(0.9f), p.y(0.7f)); lineTo(p.x(0.1f), p.y(0.72f)); close() }, p.b(Color(0xFFE9DCC0)))
                if (p.hecho) {
                    for (k in 0 until 4) drawPath(Path().apply { val x = p.x(0.2f + k * 0.08f); moveTo(x - p.w * 0.05f, p.y(0.34f)); lineTo(x, p.y(0.24f)); lineTo(x + p.w * 0.05f, p.y(0.34f)) }, Color(0xFF6B5A44), style = Stroke(lw * 0.7f))
                    drawPath(Path().apply { moveTo(p.x(0.1f), p.y(0.6f)); cubicTo(p.x(0.4f), p.y(0.5f), p.x(0.5f), p.y(0.68f), p.x(0.9f), p.y(0.56f)) }, Color(0xFF5FA8D8), style = Stroke(lw))
                    drawPath(Path().apply { moveTo(p.x(0.18f), p.y(0.48f)); cubicTo(p.x(0.4f), p.y(0.36f), p.x(0.6f), p.y(0.5f), p.x(0.82f), p.y(0.3f)) }, Color(0xFFB0281E), style = Stroke(lw * 0.8f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(lw * 2, lw * 1.5f))))
                    listOf(0.18f to 0.48f, 0.5f to 0.43f, 0.82f to 0.3f).forEach { (x, y) -> drawArc(Color(0xFFF7F2E4), 180f, 180f, true, Offset(p.x(x) - p.w * 0.04f, p.y(y) - p.w * 0.04f), Size(p.w * 0.08f, p.w * 0.06f)); drawRect(Color(0xFFF7F2E4), Offset(p.x(x) - p.w * 0.04f, p.y(y) - p.w * 0.01f), Size(p.w * 0.08f, p.w * 0.03f)) }
                }
                drawPath(Path().apply { moveTo(p.x(0.08f), p.y(0.18f)); lineTo(p.x(0.92f), p.y(0.14f)); lineTo(p.x(0.9f), p.y(0.7f)); lineTo(p.x(0.1f), p.y(0.72f)); close() }, p.metal(0.08f, 0.92f), style = Stroke(lw * 0.8f))
            }
            "Cuerpo" -> {
                // El estribo de hierro con su correa.
                drawRect(p.b(Color(0xFF6B4A2A)), Offset(p.x(0.45f), p.y(0.04f)), Size(p.x(0.1f), p.y(0.18f)))
                drawPath(Path().apply { moveTo(p.x(0.28f), p.y(0.66f)); cubicTo(p.x(0.2f), p.y(0.36f), p.x(0.36f), p.y(0.2f), p.x(0.5f), p.y(0.2f)); cubicTo(p.x(0.64f), p.y(0.2f), p.x(0.8f), p.y(0.36f), p.x(0.72f), p.y(0.66f)) }, p.metal(0.2f, 0.8f), style = Stroke(lw * 2.4f, cap = StrokeCap.Round))
                caja(p, 0.24f, 0.64f, 0.76f, 0.7f)
                drawRect(p.metal(0.42f, 0.58f), Offset(p.x(0.42f), p.y(0.18f)), Size(p.x(0.16f), p.y(0.04f)))
                if (p.hecho) for (k in 0 until 5) drawCircle(p.linea, p.w * 0.01f, p.o(0.3f + k * 0.1f, 0.67f))
            }
            "Mente" -> {
                // Una tamga (marca de clan) inventada sobre un parche de fieltro azul.
                val c = p.o(0.5f, 0.38f)
                drawCircle(cielo, p.w * 0.32f, c); drawCircle(p.metal(0.18f, 0.82f), p.w * 0.32f, c, style = Stroke(lw * 1.2f))
                val m = p.metal(0.3f, 0.7f)
                drawLine(m, Offset(c.x, c.y - p.w * 0.2f), Offset(c.x, c.y + p.w * 0.2f), lw * 1.6f, StrokeCap.Round)
                drawArc(m, 200f, 140f, false, Offset(c.x - p.w * 0.16f, c.y - p.w * 0.2f), Size(p.w * 0.32f, p.w * 0.2f), style = Stroke(lw * 1.6f, cap = StrokeCap.Round))
                drawLine(m, Offset(c.x - p.w * 0.12f, c.y + p.w * 0.1f), Offset(c.x + p.w * 0.12f, c.y + p.w * 0.1f), lw * 1.6f, StrokeCap.Round)
                drawCircle(m, p.w * 0.035f, Offset(c.x, c.y + p.w * 0.2f))
                caja(p, 0.47f, 0.38f + 0.32f * p.w / p.h, 0.53f, 0.77f)
            }
            "Cultura" -> {
                // La yurta: el techo de fieltro, la pared, la puerta naranja y su banda.
                drawPath(Path().apply { moveTo(p.x(0.1f), p.y(0.42f)); quadraticTo(p.x(0.5f), p.y(0.06f), p.x(0.9f), p.y(0.42f)); close() }, p.b(Color(0xFFEDE6D6)))
                drawRect(p.b(Color(0xFFE2D9C4)), Offset(p.x(0.12f), p.y(0.42f)), Size(p.x(0.76f), p.y(0.32f)))
                drawRect(cielo, Offset(p.x(0.12f), p.y(0.42f)), Size(p.x(0.76f), p.y(0.04f)))
                drawRect(p.b(Color(0xFFD9772A)), Offset(p.x(0.42f), p.y(0.52f)), Size(p.x(0.16f), p.y(0.22f)))
                if (p.hecho) { drawRect(Color(0xFF7A3A12), Offset(p.x(0.42f), p.y(0.52f)), Size(p.x(0.16f), p.y(0.22f)), style = Stroke(lw * 0.5f)); for (k in 0 until 5) drawLine(Color(0xFF8A7A5C).copy(alpha = 0.5f), p.o(0.5f, 0.1f), p.o(0.14f + k * 0.18f, 0.42f), lw * 0.4f) }
                drawCircle(p.metal(0.44f, 0.56f), p.w * 0.05f, p.o(0.5f, 0.12f))
                drawPath(Path().apply { moveTo(p.x(0.1f), p.y(0.42f)); quadraticTo(p.x(0.5f), p.y(0.06f), p.x(0.9f), p.y(0.42f)) }, p.metal(0.1f, 0.9f), style = Stroke(lw * 0.8f))
            }
            "Gente" -> {
                // La paiza: tablilla de oro con agujero y cordón, con escritura vertical inventada.
                drawLine(rojo, p.o(0.5f, 0.02f), p.o(0.5f, 0.12f), lw * 1.2f)
                drawRoundRect(p.metal(0.3f, 0.7f), Offset(p.x(0.3f), p.y(0.1f)), Size(p.x(0.4f), p.y(0.64f)), CornerRadius(p.w * 0.2f))
                drawCircle(p.c(Color(0xFF1E1A16)), p.w * 0.04f, p.o(0.5f, 0.17f))
                if (p.hecho) for (k in 0 until 3) {
                    val x = p.x(0.4f + k * 0.1f); val path = Path().apply { moveTo(x, p.y(0.26f)); var y = p.y(0.26f); var s = 1f; while (y < p.y(0.66f)) { quadraticTo(x + s * p.w * 0.025f, y + p.h * 0.025f, x, y + p.h * 0.05f); y += p.h * 0.05f; s = -s } }
                    drawPath(path, p.linea, style = Stroke(lw * 0.7f))
                }
            }
            "Constancia" -> {
                // El estandarte de nueve colas: el tridente arriba y las colas que caen.
                caja(p, 0.485f, 0.06f, 0.515f, 0.76f, p.b(Color(0xFF6B4A2A)))
                drawPath(Path().apply { moveTo(p.x(0.4f), p.y(0.14f)); lineTo(p.x(0.4f), p.y(0.06f)); moveTo(p.x(0.5f), p.y(0.14f)); lineTo(p.x(0.5f), p.y(0.0f)); moveTo(p.x(0.6f), p.y(0.14f)); lineTo(p.x(0.6f), p.y(0.06f)); moveTo(p.x(0.4f), p.y(0.14f)); lineTo(p.x(0.6f), p.y(0.14f)) }, p.metal(0.38f, 0.62f), style = Stroke(lw * 1.4f))
                caja(p, 0.36f, 0.15f, 0.64f, 0.19f)
                for (k in 0 until 9) {
                    val a = -0.6 + k * 0.15
                    drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.19f)); quadraticTo(p.x(0.5f + (0.12 * sin(a)).toFloat()), p.y(0.32f), p.x(0.5f + (0.26 * sin(a)).toFloat()), p.y(0.5f)) },
                        if (k % 3 == 1) fieltro else p.c(Color(0xFF2A2420)), style = Stroke(lw * 1.1f, cap = StrokeCap.Round))
                }
            }
            "Curiosos" -> {
                // El ovoo: piedras apiladas, una vara y la bufanda azul al viento.
                listOf(Triple(0.22f, 0.66f, 0.56f), Triple(0.28f, 0.56f, 0.44f), Triple(0.34f, 0.46f, 0.32f), Triple(0.4f, 0.37f, 0.2f)).forEachIndexed { i, (x0, y, ww) ->
                    drawOval(p.metal(x0, x0 + ww).let { if (p.hecho) SolidColor(lerp(Color(0xFF8C8478), Color.Black, i * 0.06f)) else it }, Offset(p.x(x0), p.y(y)), Size(p.x(ww), p.y(0.1f)))
                }
                drawLine(p.c(Color(0xFF6B4A2A)), p.o(0.5f, 0.38f), p.o(0.5f, 0.06f), lw * 1.2f)
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.1f)); cubicTo(p.x(0.64f), p.y(0.06f), p.x(0.7f), p.y(0.2f), p.x(0.86f), p.y(0.14f)); lineTo(p.x(0.84f), p.y(0.2f)); cubicTo(p.x(0.7f), p.y(0.26f), p.x(0.62f), p.y(0.14f), p.x(0.5f), p.y(0.17f)); close() }, cielo)
                caja(p, 0.18f, 0.74f, 0.82f, 0.77f)
            }
            else -> {
                // La rueda del techo (toono): aro, rayos y travesaños, pintada de naranja.
                val c = p.o(0.5f, 0.4f); val R = p.w * 0.3f
                drawCircle(p.c(Color(0xFFD9772A)), R, c, style = Stroke(lw * 2.4f))
                drawCircle(p.metal(0.2f, 0.8f), R * 1.08f, c, style = Stroke(lw * 0.6f))
                for (k in 0 until 16) { val a = k * PI / 8; drawLine(p.c(Color(0xFFD9772A)), Offset(c.x + (R * cos(a)).toFloat(), c.y + (R * sin(a)).toFloat()), Offset(c.x + (R * 1.4f * cos(a)).toFloat(), c.y + (R * 1.4f * sin(a)).toFloat()), lw * 0.7f) }
                drawLine(rojo, Offset(c.x - R, c.y), Offset(c.x + R, c.y), lw * 1.4f); drawLine(rojo, Offset(c.x, c.y - R), Offset(c.x, c.y + R), lw * 1.4f)
                listOf(-0.5f, 0.5f).forEach { f -> drawLine(rojo, Offset(c.x + R * f, c.y - R * 0.86f), Offset(c.x + R * f, c.y + R * 0.86f), lw); drawLine(rojo, Offset(c.x - R * 0.86f, c.y + R * f), Offset(c.x + R * 0.86f, c.y + R * f), lw) }
            }
        }
    }

    // ---------------------------------------------------------------- Colonos

    internal fun DrawScope.colonos(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val madera = p.c(Color(0xFF8A5A2B)); val maderaOsc = p.c(Color(0xFF5A3A22)); val hierro = p.c(Color(0xFF4A4E54)); val cuero = p.c(Color(0xFF7A4A28))
        when (cat) {
            "Diario" -> {
                // El diario de a bordo: tapa de cuero, correa y hebilla.
                caja(p, 0.26f, 0.12f, 0.74f, 0.74f, p.b(Color(0xFFF1E6C8)))
                caja(p, 0.24f, 0.1f, 0.72f, 0.72f, SolidColor(cuero))
                drawRect(p.linea, Offset(p.x(0.28f), p.y(0.14f)), Size(p.x(0.4f), p.y(0.54f)), style = Stroke(lw * 0.5f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(lw * 1.2f, lw))))
                drawRect(p.c(Color(0xFF4A2A14)), Offset(p.x(0.24f), p.y(0.38f)), Size(p.x(0.56f), p.y(0.06f)))
                drawRect(p.metal(0.62f, 0.74f), Offset(p.x(0.62f), p.y(0.365f)), Size(p.x(0.1f), p.y(0.09f)), style = Stroke(lw))
                drawRect(p.metal(0.3f, 0.66f), Offset(p.x(0.34f), p.y(0.2f)), Size(p.x(0.28f), p.y(0.1f)))
            }
            "Mundo" -> {
                // La brújula de bolsillo: caja de latón, rosa y aguja roja y azul.
                val c = p.o(0.5f, 0.44f); val R = p.w * 0.3f
                drawCircle(p.metal(0.48f, 0.52f), p.w * 0.05f, Offset(c.x, c.y - R - p.w * 0.05f), style = Stroke(lw))
                drawCircle(p.metal(0.2f, 0.8f), R, c)
                drawCircle(p.b(Color(0xFFF4EEDF)), R * 0.84f, c)
                for (k in 0 until 8) { val a = k * PI / 4; val rr = if (k % 2 == 0) R * 0.7f else R * 0.45f; drawLine(p.linea, c, Offset(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat()), lw * 0.5f) }
                drawPath(Path().apply { moveTo(c.x, c.y - R * 0.66f); lineTo(c.x + R * 0.08f, c.y); lineTo(c.x - R * 0.08f, c.y); close() }, p.c(Color(0xFFB0281E)))
                drawPath(Path().apply { moveTo(c.x, c.y + R * 0.66f); lineTo(c.x + R * 0.08f, c.y); lineTo(c.x - R * 0.08f, c.y); close() }, p.c(Color(0xFF1F3A6E)))
                drawCircle(p.metal(0.48f, 0.52f), p.w * 0.025f, c)
            }
            "Cuerpo" -> {
                // El hacha: el mango de madera y la cabeza de hierro.
                drawPath(Path().apply { moveTo(p.x(0.36f), p.y(0.76f)); quadraticTo(p.x(0.44f), p.y(0.4f), p.x(0.6f), p.y(0.08f)) }, madera, style = Stroke(lw * 2.6f, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(p.x(0.54f), p.y(0.12f)); lineTo(p.x(0.68f), p.y(0.16f)); quadraticTo(p.x(0.86f), p.y(0.14f), p.x(0.9f), p.y(0.3f)); quadraticTo(p.x(0.76f), p.y(0.3f), p.x(0.62f), p.y(0.3f)); lineTo(p.x(0.5f), p.y(0.24f)); close() }, p.metal(0.5f, 0.9f))
                if (p.hecho) drawPath(Path().apply { moveTo(p.x(0.86f), p.y(0.16f)); quadraticTo(p.x(0.9f), p.y(0.22f), p.x(0.9f), p.y(0.3f)) }, Color.White.copy(alpha = 0.7f), style = Stroke(lw * 0.6f))
            }
            "Mente" -> {
                // El telar de marco: la urdimbre y una franja ya tejida.
                caja(p, 0.16f, 0.08f, 0.22f, 0.76f, SolidColor(madera)); caja(p, 0.78f, 0.08f, 0.84f, 0.76f, SolidColor(madera))
                caja(p, 0.14f, 0.1f, 0.86f, 0.15f, SolidColor(maderaOsc)); caja(p, 0.14f, 0.62f, 0.86f, 0.67f, SolidColor(maderaOsc))
                for (k in 0 until 12) drawLine(p.c(Color(0xFFE9E1CF)), p.o(0.26f + k * 0.044f, 0.15f), p.o(0.26f + k * 0.044f, 0.62f), lw * 0.4f)
                val tejido = listOf(Color(0xFFB0281E), Color(0xFF1F3A6E), Color(0xFFE2B34A), Color(0xFF2E6B3A))
                for (k in 0 until 6) drawRect(p.c(tejido[k % 4]), Offset(p.x(0.24f), p.y(0.44f + k * 0.03f)), Size(p.x(0.52f), p.y(0.03f)))
                caja(p, 0.22f, 0.36f, 0.78f, 0.38f)
            }
            "Cultura" -> {
                // La linterna de hojalata: cuerpo perforado, techo cónico, asa y la luz.
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD27A).copy(alpha = 0.5f), Color.Transparent), p.o(0.5f, 0.45f), p.w * 0.42f), p.w * 0.42f, p.o(0.5f, 0.45f))
                drawCircle(p.metal(0.42f, 0.58f), p.w * 0.07f, p.o(0.5f, 0.1f), style = Stroke(lw))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.16f)); lineTo(p.x(0.72f), p.y(0.3f)); lineTo(p.x(0.28f), p.y(0.3f)); close() }, p.metal(0.28f, 0.72f))
                caja(p, 0.3f, 0.3f, 0.7f, 0.7f)
                if (p.hecho) for (i in 0 until 5) for (j in 0 until 6) { val x = 0.35f + i * 0.075f; val y = 0.35f + j * 0.06f; drawCircle(Color(0xFFFFE6A3), p.w * (if ((i + j) % 2 == 0) 0.012f else 0.008f), p.o(x, y)) }
                caja(p, 0.27f, 0.7f, 0.73f, 0.74f)
            }
            "Gente" -> {
                // La campana del pueblo en su yugo de madera.
                caja(p, 0.16f, 0.1f, 0.84f, 0.15f, SolidColor(maderaOsc))
                caja(p, 0.18f, 0.15f, 0.23f, 0.76f, SolidColor(madera)); caja(p, 0.77f, 0.15f, 0.82f, 0.76f, SolidColor(madera))
                drawPath(Path().apply { moveTo(p.x(0.36f), p.y(0.58f)); lineTo(p.x(0.38f), p.y(0.3f)); quadraticTo(p.x(0.4f), p.y(0.18f), p.x(0.5f), p.y(0.18f)); quadraticTo(p.x(0.6f), p.y(0.18f), p.x(0.62f), p.y(0.3f)); lineTo(p.x(0.64f), p.y(0.58f)); quadraticTo(p.x(0.5f), p.y(0.62f), p.x(0.36f), p.y(0.58f)); close() }, p.metal(0.36f, 0.64f))
                drawCircle(hierro, p.w * 0.035f, p.o(0.5f, 0.63f))
                drawLine(p.linea, p.o(0.37f, 0.52f), p.o(0.63f, 0.52f), lw * 0.6f)
            }
            "Constancia" -> {
                // El yunque sobre su base.
                drawPath(Path().apply { moveTo(p.x(0.1f), p.y(0.3f)); quadraticTo(p.x(0.2f), p.y(0.4f), p.x(0.36f), p.y(0.42f)); lineTo(p.x(0.36f), p.y(0.48f)); lineTo(p.x(0.3f), p.y(0.6f)); lineTo(p.x(0.74f), p.y(0.6f)); lineTo(p.x(0.68f), p.y(0.48f)); lineTo(p.x(0.68f), p.y(0.42f)); lineTo(p.x(0.86f), p.y(0.42f)); lineTo(p.x(0.86f), p.y(0.3f)); close() }, p.metal(0.1f, 0.86f))
                caja(p, 0.24f, 0.6f, 0.8f, 0.66f, SolidColor(hierro))
                drawRect(Brush.horizontalGradient(listOf(lerp(madera, Color.Black, 0.3f), madera, lerp(madera, Color.Black, 0.3f)), p.x(0.3f), p.x(0.74f)), Offset(p.x(0.3f), p.y(0.66f)), Size(p.x(0.44f), p.y(0.1f)))
                if (p.hecho) drawLine(Color.White.copy(alpha = 0.6f), p.o(0.14f, 0.305f), p.o(0.84f, 0.305f), lw * 0.6f)
            }
            "Curiosos" -> {
                // El barril: duelas, aros de hierro y el tapón.
                val barril = Path().apply { moveTo(p.x(0.28f), p.y(0.12f)); lineTo(p.x(0.72f), p.y(0.12f)); quadraticTo(p.x(0.86f), p.y(0.43f), p.x(0.72f), p.y(0.74f)); lineTo(p.x(0.28f), p.y(0.74f)); quadraticTo(p.x(0.14f), p.y(0.43f), p.x(0.28f), p.y(0.12f)); close() }
                drawPath(barril, Brush.horizontalGradient(listOf(lerp(madera, Color.Black, 0.35f), madera, lerp(madera, Color.White, 0.12f), madera, lerp(madera, Color.Black, 0.35f)), p.x(0.18f), p.x(0.82f)))
                clipPath(barril) { for (k in 1 until 6) { val x = p.x(0.18f + k * 0.107f); drawLine(maderaOsc, Offset(x, p.y(0.1f)), Offset(x, p.y(0.76f)), lw * 0.5f) } }
                listOf(0.2f, 0.32f, 0.54f, 0.66f).forEach { y -> val dx = if (y < 0.43f) (0.43f - y) * 0.35f else (y - 0.43f) * 0.35f
                    drawRect(p.metal(0.2f, 0.8f), Offset(p.x(0.18f + dx), p.y(y)), Size(p.x(0.64f - 2 * dx), p.y(0.03f))) }
                drawCircle(maderaOsc, p.w * 0.03f, p.o(0.5f, 0.43f))
            }
            else -> {
                // La rueda de carreta: cubo, doce rayos de madera y el aro de hierro.
                val c = p.o(0.5f, 0.42f); val R = p.w * 0.34f
                for (k in 0 until 12) { val a = k * PI / 6; drawLine(madera, c, Offset(c.x + (R * cos(a)).toFloat(), c.y + (R * sin(a)).toFloat()), lw * 1.3f) }
                drawCircle(madera, R * 0.92f, c, style = Stroke(lw * 2.2f))
                drawCircle(p.metal(0.16f, 0.84f), R, c, style = Stroke(lw * 1.2f))
                drawCircle(p.metal(0.42f, 0.58f), p.w * 0.07f, c); drawCircle(hierro, p.w * 0.025f, c)
                caja(p, 0.46f, 0.42f + 0.34f * p.w / p.h, 0.54f, 0.77f, SolidColor(maderaOsc))
            }
        }
    }

    // ---------------------------------------------------------------- Botánico

    internal fun DrawScope.botanico(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val hoja = p.c(Color(0xFF3F7A3A)); val hojaClara = p.c(Color(0xFF7FA35A)); val tinta = p.c(Color(0xFF4A3A22)); val papel = p.c(Color(0xFFF1E9D2))
        when (cat) {
            "Diario" -> {
                // La lámina botánica enmarcada: tallo, hojas, flor y la etiqueta.
                caja(p, 0.16f, 0.06f, 0.84f, 0.74f)
                drawRect(papel, Offset(p.x(0.2f), p.y(0.09f)), Size(p.x(0.6f), p.y(0.62f)))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.6f)); cubicTo(p.x(0.48f), p.y(0.45f), p.x(0.54f), p.y(0.3f), p.x(0.5f), p.y(0.2f)) }, tinta, style = Stroke(lw * 0.6f))
                listOf(0.5f to -1f, 0.42f to 1f, 0.34f to -1f).forEach { (y, s) -> drawPath(Path().apply { moveTo(p.x(0.5f), p.y(y)); quadraticTo(p.x(0.5f + s * 0.12f), p.y(y - 0.08f), p.x(0.5f + s * 0.2f), p.y(y - 0.02f)); quadraticTo(p.x(0.5f + s * 0.1f), p.y(y + 0.02f), p.x(0.5f), p.y(y)); close() }, hoja) }
                for (k in 0 until 5) { val a = k * 2 * PI / 5 - PI / 2; drawCircle(p.c(Color(0xFFC86A7A)), p.w * 0.04f, Offset(p.x(0.5f) + (p.w * 0.05f * cos(a)).toFloat(), p.y(0.18f) + (p.w * 0.05f * sin(a)).toFloat())) }
                drawCircle(p.c(Color(0xFFE2B34A)), p.w * 0.025f, p.o(0.5f, 0.18f))
                drawRect(tinta.copy(alpha = 0.5f), Offset(p.x(0.32f), p.y(0.63f)), Size(p.x(0.36f), p.y(0.04f)), style = Stroke(lw * 0.4f))
            }
            "Mundo" -> {
                // La caja de herborizar: cilindro de hojalata verde, correa y pestillo.
                drawPath(Path().apply { moveTo(p.x(0.2f), p.y(0.36f)); quadraticTo(p.x(0.5f), p.y(0.04f), p.x(0.8f), p.y(0.36f)) }, p.c(Color(0xFF5A3A22)), style = Stroke(lw * 1.2f))
                val cuerpo = Rect(p.x(0.12f), p.y(0.34f), p.x(0.88f), p.y(0.66f))
                drawRoundRect(Brush.verticalGradient(listOf(lerp(hoja, Color.White, 0.2f), hoja, lerp(hoja, Color.Black, 0.3f)), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size, CornerRadius(cuerpo.height / 2))
                drawRect(p.metal(0.3f, 0.7f), Offset(p.x(0.3f), p.y(0.36f)), Size(p.x(0.4f), p.y(0.28f)), style = Stroke(lw * 0.6f))
                drawRect(p.metal(0.46f, 0.54f), Offset(p.x(0.47f), p.y(0.45f)), Size(p.x(0.06f), p.y(0.06f)))
                caja(p, 0.3f, 0.66f, 0.34f, 0.76f); caja(p, 0.66f, 0.66f, 0.7f, 0.76f)
            }
            "Cuerpo" -> {
                // Una hoja de roble con sus lóbulos y nervaduras.
                val c = p.o(0.5f, 0.4f)
                val path = Path().apply {
                    moveTo(c.x, p.y(0.06f))
                    val lob = listOf(0.14f to 0.12f, 0.24f to 0.18f, 0.36f to 0.22f, 0.5f to 0.2f, 0.62f to 0.12f)
                    lob.forEach { (y, a) -> quadraticTo(c.x + p.w * (a + 0.08f), p.y(y), c.x + p.w * 0.04f, p.y(y + 0.06f)) }
                    lineTo(c.x, p.y(0.72f))
                    lob.reversed().forEach { (y, a) -> quadraticTo(c.x - p.w * (a + 0.08f), p.y(y + 0.06f), c.x - p.w * 0.04f, p.y(y)) }
                    close()
                }
                drawPath(path, Brush.verticalGradient(listOf(hojaClara, hoja), p.y(0.06f), p.y(0.72f)))
                drawLine(p.c(Color(0xFF2A4A22)), Offset(c.x, p.y(0.08f)), Offset(c.x, p.y(0.77f)), lw * 0.8f)
                for (k in 0 until 5) { val y = p.y(0.18f + k * 0.1f); listOf(-1f, 1f).forEach { s -> drawLine(p.c(Color(0xFF2A4A22)).copy(alpha = 0.6f), Offset(c.x, y + p.h * 0.03f), Offset(c.x + s * p.w * 0.16f, y), lw * 0.4f) } }
            }
            "Mente" -> {
                // La lupa: aro de latón, el cristal con su brillo y el mango.
                val c = p.o(0.44f, 0.34f); val R = p.w * 0.24f
                drawLine(p.c(Color(0xFF5A3A22)), Offset(c.x + R * 0.7f, c.y + R * 0.7f), p.o(0.84f, 0.74f), lw * 3, StrokeCap.Round)
                drawCircle(p.c(Color(0xFFCFE6F0)).copy(alpha = if (p.hecho) 0.7f else 1f), R, c)
                if (p.hecho) drawArc(Color.White.copy(alpha = 0.8f), 200f, 60f, false, Offset(c.x - R * 0.7f, c.y - R * 0.7f), Size(R * 1.4f, R * 1.4f), style = Stroke(lw))
                drawCircle(p.metal(0.2f, 0.68f), R, c, style = Stroke(lw * 1.6f))
            }
            "Cultura" -> {
                // Un helecho: el raquis curvo y sus foliolos, más chicos hacia la punta.
                val n = 11
                val raquis = Path().apply { moveTo(p.x(0.5f), p.y(0.76f)); cubicTo(p.x(0.46f), p.y(0.5f), p.x(0.56f), p.y(0.2f), p.x(0.68f), p.y(0.06f)) }
                drawPath(raquis, p.c(Color(0xFF2A4A22)), style = Stroke(lw * 0.8f))
                for (k in 0 until n) {
                    val t = k / n.toFloat()
                    val x = p.x(0.5f + 0.18f * t * t - 0.02f * t); val y = p.y(0.72f - 0.64f * t)
                    val l = p.w * 0.2f * (1f - t * 0.8f)
                    listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(x, y); quadraticTo(x + s * l * 0.5f, y - l * 0.35f, x + s * l, y - l * 0.1f); quadraticTo(x + s * l * 0.5f, y + l * 0.1f, x, y); close() }, if (k % 2 == 0) hoja else hojaClara) }
                }
            }
            "Gente" -> {
                // Dos cerezas unidas por el tallo, con su hoja.
                drawPath(Path().apply { moveTo(p.x(0.34f), p.y(0.5f)); quadraticTo(p.x(0.4f), p.y(0.2f), p.x(0.56f), p.y(0.1f)) }, p.c(Color(0xFF4A3A22)), style = Stroke(lw * 0.7f))
                drawPath(Path().apply { moveTo(p.x(0.66f), p.y(0.52f)); quadraticTo(p.x(0.64f), p.y(0.24f), p.x(0.56f), p.y(0.1f)) }, p.c(Color(0xFF4A3A22)), style = Stroke(lw * 0.7f))
                drawPath(Path().apply { moveTo(p.x(0.56f), p.y(0.1f)); quadraticTo(p.x(0.74f), p.y(0.0f), p.x(0.86f), p.y(0.12f)); quadraticTo(p.x(0.72f), p.y(0.2f), p.x(0.56f), p.y(0.1f)); close() }, hoja)
                val rojo = p.c(Color(0xFFA3172E))
                listOf(0.34f to 0.56f, 0.66f to 0.58f).forEach { (x, y) ->
                    drawCircle(Brush.radialGradient(listOf(lerp(rojo, Color.White, 0.35f), rojo, lerp(rojo, Color.Black, 0.3f)), Offset(p.x(x - 0.05f), p.y(y - 0.04f)), p.w * 0.2f), p.w * 0.14f, p.o(x, y))
                    drawCircle(p.metal(x - 0.14f, x + 0.14f), p.w * 0.14f, p.o(x, y), style = Stroke(lw * 0.4f))
                }
            }
            "Constancia" -> {
                // Una semilla que brota: la raíz hacia abajo y los dos cotiledones.
                drawRect(p.c(Color(0xFF6B4A2A)).copy(alpha = if (p.hecho) 0.5f else 1f), Offset(p.x(0.1f), p.y(0.5f)), Size(p.x(0.8f), p.y(0.26f)))
                drawLine(p.c(Color(0xFF6B4A2A)), p.o(0.1f, 0.5f), p.o(0.9f, 0.5f), lw)
                drawOval(p.metal(0.38f, 0.62f), Offset(p.x(0.38f), p.y(0.5f)), Size(p.x(0.24f), p.y(0.1f)))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.6f)); cubicTo(p.x(0.46f), p.y(0.66f), p.x(0.54f), p.y(0.7f), p.x(0.48f), p.y(0.75f)) }, p.c(Color(0xFFE9E1CF)), style = Stroke(lw * 0.6f))
                drawLine(hojaClara, p.o(0.5f, 0.5f), p.o(0.5f, 0.24f), lw * 1.2f)
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.26f)); quadraticTo(p.x(0.5f + s * 0.12f), p.y(0.08f), p.x(0.5f + s * 0.24f), p.y(0.18f)); quadraticTo(p.x(0.5f + s * 0.12f), p.y(0.3f), p.x(0.5f), p.y(0.26f)); close() }, hoja) }
            }
            "Curiosos" -> {
                // La campana de cristal sobre una plantita.
                caja(p, 0.2f, 0.7f, 0.8f, 0.76f)
                drawLine(hoja, p.o(0.5f, 0.7f), p.o(0.5f, 0.42f), lw)
                listOf(0.6f, 0.5f).forEachIndexed { i, y -> val s = if (i == 0) -1f else 1f; drawPath(Path().apply { moveTo(p.x(0.5f), p.y(y)); quadraticTo(p.x(0.5f + s * 0.1f), p.y(y - 0.08f), p.x(0.5f + s * 0.16f), p.y(y - 0.02f)); quadraticTo(p.x(0.5f + s * 0.08f), p.y(y + 0.02f), p.x(0.5f), p.y(y)); close() }, hoja) }
                drawCircle(p.c(Color(0xFFE8A0B4)), p.w * 0.05f, p.o(0.5f, 0.4f))
                val domo = Path().apply { moveTo(p.x(0.24f), p.y(0.7f)); lineTo(p.x(0.24f), p.y(0.3f)); quadraticTo(p.x(0.24f), p.y(0.1f), p.x(0.5f), p.y(0.1f)); quadraticTo(p.x(0.76f), p.y(0.1f), p.x(0.76f), p.y(0.3f)); lineTo(p.x(0.76f), p.y(0.7f)) }
                drawPath(domo, p.c(Color(0xFFCFE6F0)).copy(alpha = if (p.hecho) 0.25f else 1f))
                drawPath(domo, p.metal(0.24f, 0.76f), style = Stroke(lw * 0.8f))
                drawCircle(p.metal(0.46f, 0.54f), p.w * 0.04f, p.o(0.5f, 0.07f))
                if (p.hecho) drawLine(Color.White.copy(alpha = 0.7f), p.o(0.3f, 0.3f), p.o(0.3f, 0.6f), lw * 0.8f)
            }
            else -> {
                // Una flor prensada en papel, con sus cintas en las esquinas.
                drawRect(papel, Offset(p.x(0.14f), p.y(0.08f)), Size(p.x(0.72f), p.y(0.66f)))
                listOf(0.14f to 0.08f, 0.86f to 0.08f, 0.14f to 0.74f, 0.86f to 0.74f).forEach { (x, y) -> drawRect(p.metal(x - 0.05f, x + 0.05f), Offset(p.x(x) - p.w * 0.05f, p.y(y) - p.w * 0.02f), Size(p.w * 0.1f, p.w * 0.04f)) }
                val c = p.o(0.5f, 0.34f)
                drawPath(Path().apply { moveTo(c.x, c.y); cubicTo(c.x - p.w * 0.04f, p.y(0.5f), c.x + p.w * 0.06f, p.y(0.6f), c.x, p.y(0.68f)) }, p.c(Color(0xFF6B7A3A)), style = Stroke(lw * 0.6f))
                for (k in 0 until 5) { val a = k * 2 * PI / 5 - PI / 2
                    drawOval(p.c(Color(0xFF9A6AA8)).copy(alpha = if (p.hecho) 0.8f else 1f), Offset(c.x + (p.w * 0.09f * cos(a)).toFloat() - p.w * 0.07f, c.y + (p.w * 0.09f * sin(a)).toFloat() - p.w * 0.07f), Size(p.w * 0.14f, p.w * 0.14f)) }
                drawCircle(p.c(Color(0xFFE2B34A)), p.w * 0.04f, c)
                drawPath(Path().apply { moveTo(c.x, p.y(0.56f)); quadraticTo(c.x + p.w * 0.14f, p.y(0.5f), c.x + p.w * 0.2f, p.y(0.56f)); quadraticTo(c.x + p.w * 0.1f, p.y(0.6f), c.x, p.y(0.56f)); close() }, hojaClara)
            }
        }
    }

    // ---------------------------------------------------------------- Ciudad verde

    /** Lo que se rescata en la ciudad que el bosque recuperó: objetos viejos, oxidados o salvados, y lo que vuelve a crecer. */
    internal fun DrawScope.ruinas(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val hiedra = p.c(Color(0xFF4F7A33)); val hiedraClara = p.c(Color(0xFF7FA35A)); val oxido = p.c(Color(0xFFA34A1E))
        val hormigon = p.c(Color(0xFF9C9C92)); val tinta = p.c(Color(0xFF2A2E28)); val papel = p.c(Color(0xFFE6E3D6))
        fun hojaH(c: Offset, t: Float, color: Color) = drawPath(Path().apply { moveTo(c.x, c.y + t * 0.5f); lineTo(c.x - t * 0.55f, c.y + t * 0.35f); lineTo(c.x - t * 0.8f, c.y - t * 0.05f); lineTo(c.x - t * 0.32f, c.y - t * 0.05f); lineTo(c.x, c.y - t * 0.5f); lineTo(c.x + t * 0.32f, c.y - t * 0.05f); lineTo(c.x + t * 0.8f, c.y - t * 0.05f); lineTo(c.x + t * 0.55f, c.y + t * 0.35f); close() }, color)
        when (cat) {
            "Diario" -> {
                // El cuaderno del superviviente: tapa de lona, lomo de cinta plateada, la liga y una hoja de hiedra pegada.
                val lona = p.c(Color(0xFF5B6B45))
                drawRect(papel, Offset(p.x(0.23f), p.y(0.13f)), Size(p.x(0.6f), p.y(0.62f)))
                drawRect(Brush.linearGradient(listOf(lerp(lona, Color.White, 0.12f), lona, lerp(lona, Color.Black, 0.25f)), p.o(0.2f, 0.1f), p.o(0.8f, 0.72f)), p.o(0.2f, 0.1f), Size(p.x(0.6f), p.y(0.62f)))
                caja(p, 0.2f, 0.1f, 0.29f, 0.72f)
                hojaH(p.o(0.54f, 0.38f), p.w * 0.22f, hiedraClara)
                drawLine(lerp(hiedraClara, Color.Black, 0.4f), p.o(0.54f, 0.49f), p.o(0.54f, 0.31f), lw * 0.6f)
                drawLine(tinta, p.o(0.72f, 0.08f), p.o(0.72f, 0.74f), lw * 1.2f)
            }
            "Mundo" -> {
                // La brújula oxidada: la caja redonda con su tapa abierta, la aguja y las manchas de óxido.
                val c = p.o(0.5f, 0.46f); val r = p.w * 0.26f
                drawOval(p.metal(0.24f, 0.76f), Offset(c.x - r, p.y(0.04f)), Size(2 * r, p.y(0.18f)))
                drawCircle(p.metal(0.24f, 0.76f), r, c)
                drawCircle(papel, r * 0.82f, c)
                for (k in 0 until 8) { val a = k * PI / 4; drawLine(tinta.copy(alpha = 0.6f), Offset(c.x + (r * 0.68f * cos(a)).toFloat(), c.y + (r * 0.68f * sin(a)).toFloat()), Offset(c.x + (r * 0.8f * cos(a)).toFloat(), c.y + (r * 0.8f * sin(a)).toFloat()), lw * 0.5f) }
                drawPath(Path().apply { moveTo(c.x, c.y - r * 0.66f); lineTo(c.x + r * 0.12f, c.y); lineTo(c.x - r * 0.12f, c.y); close() }, p.c(Color(0xFFB3261E)))
                drawPath(Path().apply { moveTo(c.x, c.y + r * 0.66f); lineTo(c.x + r * 0.12f, c.y); lineTo(c.x - r * 0.12f, c.y); close() }, tinta)
                drawCircle(oxido.copy(alpha = 0.55f), r * 0.2f, Offset(c.x + r * 0.7f, c.y + r * 0.5f))
                drawCircle(oxido.copy(alpha = 0.45f), r * 0.12f, Offset(c.x - r * 0.75f, c.y - r * 0.4f))
            }
            "Cuerpo" -> {
                // La bicicleta rescatada: dos ruedas con sus rayos, el cuadro, el manubrio y una guía de hiedra en la rueda de atrás.
                val r1 = p.o(0.26f, 0.56f); val r2 = p.o(0.74f, 0.56f); val r = p.w * 0.17f
                listOf(r1, r2).forEach { c ->
                    drawCircle(tinta, r, c, style = Stroke(lw * 1.4f))
                    for (k in 0 until 6) { val a = k * PI / 6; drawLine(tinta.copy(alpha = 0.5f), Offset(c.x - (r * cos(a)).toFloat(), c.y - (r * sin(a)).toFloat()), Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat()), lw * 0.3f) }
                }
                val pedal = p.o(0.47f, 0.56f); val sillin = p.o(0.4f, 0.3f); val cuello = p.o(0.66f, 0.3f)
                drawPath(Path().apply { moveTo(r1.x, r1.y); lineTo(sillin.x, sillin.y); lineTo(cuello.x, cuello.y); lineTo(pedal.x, pedal.y); close(); moveTo(sillin.x, sillin.y); lineTo(pedal.x, pedal.y); moveTo(cuello.x, cuello.y); lineTo(r2.x, r2.y) }, p.metal(0.2f, 0.8f), style = Stroke(lw * 1.3f))
                drawLine(tinta, p.o(0.36f, 0.27f), p.o(0.45f, 0.27f), lw * 1.6f)
                drawLine(tinta, p.o(0.64f, 0.24f), p.o(0.72f, 0.22f), lw * 1.2f)
                hojaH(Offset(r1.x - r * 0.6f, r1.y - r * 0.7f), p.w * 0.08f, hiedra); hojaH(Offset(r1.x + r * 0.2f, r1.y + r * 0.9f), p.w * 0.07f, hiedraClara)
            }
            "Mente" -> {
                // La radio de manivela: la caja con su rejilla, el dial, la antena y la manivela al costado.
                caja(p, 0.16f, 0.3f, 0.84f, 0.74f)
                drawRect(p.c(Color(0xFF3A3F38)), Offset(p.x(0.2f), p.y(0.36f)), Size(p.x(0.32f), p.y(0.32f)))
                for (k in 0 until 6) drawLine(p.linea, Offset(p.x(0.22f), p.y(0.39f + k * 0.05f)), Offset(p.x(0.5f), p.y(0.39f + k * 0.05f)), lw * 0.5f)
                drawCircle(papel, p.w * 0.1f, p.o(0.68f, 0.46f)); drawLine(p.c(Color(0xFFB3261E)), p.o(0.68f, 0.46f), p.o(0.74f, 0.4f), lw * 0.6f)
                drawCircle(tinta, p.w * 0.04f, p.o(0.68f, 0.64f))
                drawLine(p.linea, p.o(0.76f, 0.3f), p.o(0.9f, 0.04f), lw * 0.8f)
                drawLine(tinta, p.o(0.84f, 0.52f), p.o(0.94f, 0.6f), lw * 1.2f); drawCircle(oxido, p.w * 0.025f, p.o(0.94f, 0.6f))
            }
            "Cultura" -> {
                // Un libro salvado del agua: abierto, con las hojas onduladas, la mancha de humedad y un brote que salió entre sus páginas.
                listOf(-1f, 1f).forEach { lado ->
                    drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.66f)); quadraticTo(p.x(0.5f + lado * 0.2f), p.y(0.6f), p.x(0.5f + lado * 0.38f), p.y(0.66f)); lineTo(p.x(0.5f + lado * 0.38f), p.y(0.36f)); quadraticTo(p.x(0.5f + lado * 0.2f), p.y(0.3f), p.x(0.5f), p.y(0.36f)); close() }, papel)
                    for (k in 0 until 4) drawLine(tinta.copy(alpha = 0.3f), Offset(p.x(0.5f + lado * 0.06f), p.y(0.42f + k * 0.05f)), Offset(p.x(0.5f + lado * 0.32f), p.y(0.42f + k * 0.05f)), lw * 0.4f)
                }
                drawOval(p.c(Color(0xFF8A6A44)).copy(alpha = 0.3f), Offset(p.x(0.56f), p.y(0.5f)), Size(p.x(0.22f), p.y(0.12f)))
                drawLine(hiedra, p.o(0.5f, 0.38f), p.o(0.5f, 0.16f), lw * 0.9f)
                hojaH(p.o(0.43f, 0.18f), p.w * 0.1f, hiedraClara); hojaH(p.o(0.58f, 0.13f), p.w * 0.11f, hiedra)
                drawLine(p.linea, p.o(0.12f, 0.66f), p.o(0.88f, 0.66f), lw * 0.8f)
            }
            "Gente" -> {
                // La fogata del campamento: los leños cruzados, las piedras alrededor y las llamas.
                for (k in 0 until 7) drawOval(hormigon, Offset(p.x(0.14f + k * 0.1f), p.y(0.68f)), Size(p.x(0.1f), p.y(0.07f)))
                drawLine(p.c(Color(0xFF5A3A22)), p.o(0.26f, 0.7f), p.o(0.7f, 0.56f), lw * 3f, StrokeCap.Round)
                drawLine(p.c(Color(0xFF6A4A2A)), p.o(0.74f, 0.7f), p.o(0.3f, 0.56f), lw * 3f, StrokeCap.Round)
                val fuego = p.c(Color(0xFFE07A2A)); val centro = p.c(Color(0xFFF2C14E))
                fun llama(cx: Float, alto: Float, ancho: Float, col: Color) = drawPath(Path().apply {
                    moveTo(p.x(cx), p.y(0.6f - alto)); cubicTo(p.x(cx + ancho), p.y(0.6f - alto * 0.5f), p.x(cx + ancho * 0.8f), p.y(0.6f), p.x(cx), p.y(0.62f))
                    cubicTo(p.x(cx - ancho * 0.8f), p.y(0.6f), p.x(cx - ancho), p.y(0.6f - alto * 0.5f), p.x(cx), p.y(0.6f - alto)); close() }, col)
                llama(0.5f, 0.46f, 0.16f, fuego); llama(0.4f, 0.28f, 0.08f, fuego); llama(0.6f, 0.32f, 0.08f, fuego); llama(0.5f, 0.26f, 0.08f, centro)
            }
            "Constancia" -> {
                // El árbol que rompió el asfalto: la losa partida en dos y el tronco con su copa saliendo por la grieta.
                drawPath(Path().apply { moveTo(p.x(0.06f), p.y(0.64f)); lineTo(p.x(0.44f), p.y(0.62f)); lineTo(p.x(0.4f), p.y(0.76f)); lineTo(p.x(0.06f), p.y(0.76f)); close() }, hormigon)
                drawPath(Path().apply { moveTo(p.x(0.56f), p.y(0.6f)); lineTo(p.x(0.94f), p.y(0.66f)); lineTo(p.x(0.94f), p.y(0.76f)); lineTo(p.x(0.6f), p.y(0.76f)); close() }, hormigon)
                drawLine(p.c(Color(0xFFE0B040)).copy(alpha = 0.8f), p.o(0.12f, 0.69f), p.o(0.3f, 0.685f), lw * 1.4f)
                drawLine(p.c(Color(0xFF5A3A22)), p.o(0.5f, 0.76f), p.o(0.5f, 0.36f), lw * 3.2f)
                drawLine(p.c(Color(0xFF5A3A22)), p.o(0.5f, 0.46f), p.o(0.62f, 0.34f), lw * 1.4f)
                listOf(Triple(0.5f, 0.26f, 0.2f), Triple(0.34f, 0.32f, 0.13f), Triple(0.66f, 0.3f, 0.14f), Triple(0.5f, 0.14f, 0.12f)).forEach { (x, y, r) ->
                    drawCircle(Brush.radialGradient(listOf(hiedraClara, hiedra), p.o(x - 0.04f, y - 0.04f), p.w * r * 1.4f), p.w * r, p.o(x, y))
                }
            }
            "Curiosos" -> {
                // El semáforo con hiedra: la caja en su poste, el rojo y el ámbar muertos, el verde encendido y las hojas que trepan.
                drawRect(p.metal(0.46f, 0.54f), Offset(p.x(0.47f), p.y(0.5f)), Size(p.x(0.06f), p.y(0.28f)))
                drawRoundRect(p.c(Color(0xFF2E332E)), p.o(0.36f, 0.06f), Size(p.x(0.28f), p.y(0.46f)), CornerRadius(p.w * 0.05f))
                listOf(0.14f to oxido.copy(alpha = 0.5f), 0.29f to p.c(Color(0xFFB57A0E)).copy(alpha = 0.45f), 0.44f to p.c(Color(0xFF8FCB6A))).forEach { (y, col) -> drawCircle(col, p.w * 0.06f, p.o(0.5f, y)) }
                if (p.hecho) drawCircle(Color(0xFF8FCB6A).copy(alpha = 0.25f), p.w * 0.11f, p.o(0.5f, 0.44f))
                drawPath(Path().apply { moveTo(p.x(0.46f), p.y(0.78f)); cubicTo(p.x(0.58f), p.y(0.66f), p.x(0.4f), p.y(0.56f), p.x(0.6f), p.y(0.4f)) }, hiedra, style = Stroke(lw * 0.8f))
                listOf(Triple(0.42f, 0.72f, 0.08f), Triple(0.58f, 0.62f, 0.09f), Triple(0.44f, 0.54f, 0.08f), Triple(0.66f, 0.4f, 0.09f), Triple(0.62f, 0.22f, 0.08f)).forEachIndexed { k, (x, y, t) ->
                    hojaH(p.o(x, y), p.w * t, if (k % 2 == 0) hiedra else hiedraClara)
                }
            }
            else -> {
                // La llave de la puerta tapiada: el ojo, la caña y los dientes, atada a un trozo de cordel con una hoja.
                val c = p.o(0.32f, 0.36f); val r = p.w * 0.13f
                drawCircle(p.metal(0.18f, 0.46f), r, c, style = Stroke(lw * 2.4f))
                drawLine(p.c(Color(0xFF8A7F68)), Offset(c.x - r * 0.4f, c.y - r * 0.9f), p.o(0.18f, 0.06f), lw * 0.6f)
                caja(p, 0.44f, 0.33f, 0.86f, 0.39f)
                caja(p, 0.72f, 0.39f, 0.76f, 0.5f); caja(p, 0.8f, 0.39f, 0.84f, 0.46f)
                drawCircle(oxido.copy(alpha = 0.5f), p.w * 0.03f, p.o(0.6f, 0.36f))
                hojaH(p.o(0.2f, 0.62f), p.w * 0.16f, hiedraClara)
                drawLine(p.c(Color(0xFF8A7F68)), p.o(0.18f, 0.06f), p.o(0.2f, 0.53f), lw * 0.6f)
            }
        }
    }

    // ---------------------------------------------------------------- Navidad

    internal fun DrawScope.navidad(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val rojo = p.c(Color(0xFFB3122E)); val verde = p.c(Color(0xFF1F6B3A)); val oro = p.c(Color(0xFFD4AF37)); val nieve = p.c(Color(0xFFF4F7FB))
        fun cordel(x: Float, hasta: Float) = drawLine(p.c(Color(0xFF8A6A44)), p.o(x, 0f), p.o(x, hasta), lw * 0.5f)
        when (cat) {
            "Diario" -> {
                // La vela con su flama y el acebo.
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD27A).copy(alpha = 0.55f), Color.Transparent), p.o(0.5f, 0.16f), p.w * 0.3f), p.w * 0.3f, p.o(0.5f, 0.16f))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.04f)); cubicTo(p.x(0.58f), p.y(0.12f), p.x(0.56f), p.y(0.2f), p.x(0.5f), p.y(0.22f)); cubicTo(p.x(0.44f), p.y(0.2f), p.x(0.42f), p.y(0.12f), p.x(0.5f), p.y(0.04f)); close() },
                    if (p.hecho) Brush.verticalGradient(listOf(Color(0xFFFFE27A), Color(0xFFFF8A2E)), p.y(0.04f), p.y(0.22f)) else SolidColor(p.sombra))
                drawRect(Brush.horizontalGradient(listOf(lerp(rojo, Color.Black, 0.25f), rojo, lerp(rojo, Color.White, 0.2f), rojo), p.x(0.38f), p.x(0.62f)), Offset(p.x(0.38f), p.y(0.24f)), Size(p.x(0.24f), p.y(0.46f)))
                drawPath(Path().apply { moveTo(p.x(0.38f), p.y(0.24f)); lineTo(p.x(0.62f), p.y(0.24f)); lineTo(p.x(0.62f), p.y(0.32f)); quadraticTo(p.x(0.58f), p.y(0.38f), p.x(0.56f), p.y(0.3f)); lineTo(p.x(0.38f), p.y(0.3f)); close() }, p.c(Color(0xFFE85A6A)))
                caja(p, 0.28f, 0.7f, 0.72f, 0.76f)
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.7f)); quadraticTo(p.x(0.5f + s * 0.14f), p.y(0.6f), p.x(0.5f + s * 0.3f), p.y(0.68f)); quadraticTo(p.x(0.5f + s * 0.14f), p.y(0.72f), p.x(0.5f), p.y(0.7f)); close() }, verde) }
                listOf(-0.03f, 0.03f).forEach { dx -> drawCircle(rojo, p.w * 0.025f, p.o(0.5f + dx, 0.69f)) }
            }
            "Mundo" -> {
                // La estrella dorada que cuelga, con su resplandor.
                cordel(0.5f, 0.12f)
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFE27A).copy(alpha = 0.45f), Color.Transparent), p.o(0.5f, 0.42f), p.w * 0.45f), p.w * 0.45f, p.o(0.5f, 0.42f))
                estrella(p.o(0.5f, 0.42f), p.w * 0.36f, p.w * 0.15f, 5, p.metal(0.14f, 0.86f))
                estrella(p.o(0.5f, 0.42f), p.w * 0.2f, p.w * 0.08f, 5, SolidColor(oro.copy(alpha = if (p.hecho) 0.6f else 1f)))
            }
            "Cuerpo" -> {
                // El bastón de caramelo, rayado de rojo y blanco.
                val baston = Path().apply { moveTo(p.x(0.4f), p.y(0.76f)); lineTo(p.x(0.4f), p.y(0.28f)); cubicTo(p.x(0.4f), p.y(0.04f), p.x(0.74f), p.y(0.04f), p.x(0.74f), p.y(0.26f)) }
                drawPath(baston, nieve, style = Stroke(p.w * 0.11f, cap = StrokeCap.Round))
                if (p.hecho) clipPath(Path().apply { addRect(Rect(0f, 0f, size.width, size.height)) }) {
                    drawPath(baston, rojo, style = Stroke(p.w * 0.11f, cap = StrokeCap.Butt, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(p.w * 0.05f, p.w * 0.05f))))
                }
                drawPath(baston, p.metal(0.3f, 0.8f), style = Stroke(lw * 0.4f))
            }
            "Mente" -> {
                // Un copo de nieve de seis brazos, con sus ramitas.
                cordel(0.5f, 0.06f)
                val c = p.o(0.5f, 0.4f); val R = p.w * 0.36f
                val b = p.metal(0.14f, 0.86f)
                for (k in 0 until 6) {
                    val a = -PI / 2 + k * PI / 3
                    fun pt(r: Float, da: Double = 0.0) = Offset(c.x + (r * cos(a + da)).toFloat(), c.y + (r * sin(a + da)).toFloat())
                    drawLine(b, c, pt(R), lw * 1.4f, StrokeCap.Round)
                    listOf(0.45f, 0.72f).forEach { f -> val base = pt(R * f); listOf(-0.6, 0.6).forEach { da -> drawLine(b, base, Offset(base.x + (R * 0.22f * cos(a + da)).toFloat(), base.y + (R * 0.22f * sin(a + da)).toFloat()), lw, StrokeCap.Round) } }
                }
                drawCircle(b, p.w * 0.05f, c)
                if (p.hecho) drawCircle(nieve, p.w * 0.025f, c)
            }
            "Cultura" -> {
                // La esfera: su casquillo, franjas y el brillo.
                cordel(0.5f, 0.08f)
                drawCircle(p.metal(0.46f, 0.54f), p.w * 0.04f, p.o(0.5f, 0.08f), style = Stroke(lw * 0.6f))
                caja(p, 0.44f, 0.1f, 0.56f, 0.15f)
                val c = p.o(0.5f, 0.42f); val R = p.w * 0.3f
                drawCircle(Brush.radialGradient(listOf(lerp(rojo, Color.White, 0.4f), rojo, lerp(rojo, Color.Black, 0.35f)), Offset(c.x - R * 0.3f, c.y - R * 0.3f), R * 1.4f), R, c)
                clipPath(Path().apply { addOval(Rect(c, R)) }) {
                    drawRect(oro, Offset(c.x - R, c.y - R * 0.15f), Size(2 * R, R * 0.3f))
                    if (p.hecho) for (k in 0 until 8) estrella(Offset(c.x - R + k * R * 0.28f, c.y), R * 0.08f, R * 0.035f, 5, SolidColor(Color(0xFFB3122E)))
                    drawRect(verde, Offset(c.x - R, c.y - R * 0.5f), Size(2 * R, R * 0.06f)); drawRect(verde, Offset(c.x - R, c.y + R * 0.44f), Size(2 * R, R * 0.06f))
                }
                if (p.hecho) drawArc(Color.White.copy(alpha = 0.7f), 200f, 50f, false, Offset(c.x - R * 0.75f, c.y - R * 0.75f), Size(R * 1.5f, R * 1.5f), style = Stroke(lw * 1.2f, cap = StrokeCap.Round))
            }
            "Gente" -> {
                // La corona: un aro de hojas, bayas rojas y el moño.
                val c = p.o(0.5f, 0.38f); val R = p.w * 0.28f
                for (k in 0 until 26) { val a = k * 2 * PI / 26; val o = Offset(c.x + (R * cos(a)).toFloat(), c.y + (R * sin(a)).toFloat())
                    drawOval(if (k % 2 == 0) verde else lerp(verde, Color.Black, 0.25f), Offset(o.x - p.w * 0.07f, o.y - p.w * 0.04f), Size(p.w * 0.14f, p.w * 0.08f)) }
                if (p.hecho) for (k in 0 until 7) { val a = k * 2 * PI / 7 + 0.3; drawCircle(Color(0xFFB3122E), p.w * 0.025f, Offset(c.x + (R * 1.05f * cos(a)).toFloat(), c.y + (R * 1.05f * sin(a)).toFloat())) }
                val m = Offset(c.x, c.y + R)
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(m.x, m.y); quadraticTo(m.x + s * p.w * 0.12f, m.y - p.w * 0.1f, m.x + s * p.w * 0.14f, m.y + p.w * 0.02f); close() }, rojo)
                    drawLine(rojo, m, Offset(m.x + s * p.w * 0.08f, m.y + p.w * 0.18f), lw * 1.2f) }
                drawCircle(oro, p.w * 0.025f, m)
            }
            "Constancia" -> {
                // La campana dorada con su listón rojo.
                cordel(0.5f, 0.1f)
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.14f)); quadraticTo(p.x(0.5f + s * 0.16f), p.y(0.06f), p.x(0.5f + s * 0.2f), p.y(0.16f)); quadraticTo(p.x(0.5f + s * 0.1f), p.y(0.18f), p.x(0.5f), p.y(0.14f)); close() }, rojo) }
                drawPath(Path().apply { moveTo(p.x(0.24f), p.y(0.62f)); quadraticTo(p.x(0.32f), p.y(0.56f), p.x(0.34f), p.y(0.34f)); quadraticTo(p.x(0.38f), p.y(0.16f), p.x(0.5f), p.y(0.16f)); quadraticTo(p.x(0.62f), p.y(0.16f), p.x(0.66f), p.y(0.34f)); quadraticTo(p.x(0.68f), p.y(0.56f), p.x(0.76f), p.y(0.62f)); close() }, p.metal(0.24f, 0.76f))
                drawCircle(p.metal(0.44f, 0.56f), p.w * 0.06f, p.o(0.5f, 0.66f))
                if (p.hecho) drawPath(Path().apply { moveTo(p.x(0.4f), p.y(0.26f)); quadraticTo(p.x(0.36f), p.y(0.4f), p.x(0.36f), p.y(0.52f)) }, Color.White.copy(alpha = 0.6f), style = Stroke(lw * 0.8f))
            }
            "Curiosos" -> {
                // El regalo: caja, listón en cruz y el moño.
                caja(p, 0.18f, 0.34f, 0.82f, 0.76f, SolidColor(verde))
                caja(p, 0.14f, 0.28f, 0.86f, 0.36f, SolidColor(lerp(verde, Color.White, 0.08f)))
                drawRect(rojo, Offset(p.x(0.45f), p.y(0.28f)), Size(p.x(0.1f), p.y(0.48f)))
                drawRect(rojo, Offset(p.x(0.18f), p.y(0.5f)), Size(p.x(0.64f), p.y(0.06f)))
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.28f)); cubicTo(p.x(0.5f + s * 0.24f), p.y(0.06f), p.x(0.5f + s * 0.3f), p.y(0.3f), p.x(0.5f), p.y(0.28f)); close() }, rojo) }
                drawCircle(oro, p.w * 0.04f, p.o(0.5f, 0.28f))
                if (p.hecho) for (k in 0 until 10) drawCircle(oro.copy(alpha = 0.6f), p.w * 0.012f, p.o(0.22f + (k % 5) * 0.14f, 0.42f + (k / 5) * 0.22f))
            }
            else -> {
                // La piña con sus escamas.
                cordel(0.5f, 0.1f)
                val c = p.o(0.5f, 0.42f)
                drawOval(Brush.verticalGradient(listOf(p.c(Color(0xFF8A5A2B)), p.c(Color(0xFF5A3A22))), p.y(0.12f), p.y(0.74f)), Offset(p.x(0.28f), p.y(0.12f)), Size(p.x(0.44f), p.y(0.62f)))
                clipPath(Path().apply { addOval(Rect(p.x(0.28f), p.y(0.12f), p.x(0.72f), p.y(0.74f))) }) {
                    var fila = 0; var y = p.y(0.16f)
                    while (y < p.y(0.76f)) {
                        var x = p.x(0.24f) + (if (fila % 2 == 0) 0f else p.w * 0.05f)
                        while (x < p.x(0.76f)) { drawArc(p.c(Color(0xFF3A2414)), 0f, 180f, false, Offset(x, y), Size(p.w * 0.1f, p.w * 0.08f), style = Stroke(lw * 0.6f)); x += p.w * 0.1f }
                        y += p.w * 0.06f; fila++
                    }
                }
                if (p.hecho) for (k in 0 until 6) drawCircle(nieve, p.w * 0.015f, p.o(0.36f + k * 0.055f, 0.16f + (k % 2) * 0.02f))
            }
        }
    }

    // ---------------------------------------------------------------- Tierras Antiguas

    internal fun DrawScope.antiguas(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val hierro = p.c(Color(0xFF4A4E54)); val cuero = p.c(Color(0xFF5A3A22)); val pergamino = p.c(Color(0xFFE9DCC0))
        val rubi = p.c(Color(0xFFA3172E)); val esmeralda = p.c(Color(0xFF1E7A5A)); val cristal = p.c(Color(0xFF8FB8E0))
        when (cat) {
            "Diario" -> {
                // El libro de runas: tapa de cuero, esquinas de hierro y signos inventados.
                caja(p, 0.22f, 0.1f, 0.78f, 0.74f, SolidColor(cuero))
                listOf(0.22f to 0.1f, 0.78f to 0.1f, 0.22f to 0.74f, 0.78f to 0.74f).forEach { (x, y) -> drawCircle(hierro, p.w * 0.06f, p.o(x, y)) }
                drawRect(p.metal(0.26f, 0.74f), Offset(p.x(0.27f), p.y(0.15f)), Size(p.x(0.46f), p.y(0.54f)), style = Stroke(lw * 0.8f))
                if (p.hecho) { runas(p.o(0.32f, 0.3f), p.x(0.36f), p.y(0.06f), p.brillo, lw * 0.7f, 4, 1); runas(p.o(0.32f, 0.54f), p.x(0.36f), p.y(0.06f), p.brillo, lw * 0.7f, 4, 5) }
                drawCircle(rubi, p.w * 0.05f, p.o(0.5f, 0.42f)); drawCircle(p.metal(0.44f, 0.56f), p.w * 0.05f, p.o(0.5f, 0.42f), style = Stroke(lw * 0.6f))
            }
            "Mundo" -> {
                // El mapa de la travesía: montañas, bosque, el camino y la fortaleza al final.
                drawPath(Path().apply { moveTo(p.x(0.08f), p.y(0.14f)); lineTo(p.x(0.92f), p.y(0.18f)); lineTo(p.x(0.9f), p.y(0.72f)); lineTo(p.x(0.1f), p.y(0.7f)); close() }, SolidColor(pergamino))
                if (p.hecho) {
                    for (k in 0 until 4) drawPath(Path().apply { val x = p.x(0.48f + k * 0.08f); moveTo(x - p.w * 0.05f, p.y(0.36f)); lineTo(x, p.y(0.24f)); lineTo(x + p.w * 0.05f, p.y(0.36f)) }, Color(0xFF5A4A3A), style = Stroke(lw * 0.7f))
                    for (k in 0 until 8) drawCircle(Color(0xFF3F6A3A), p.w * 0.022f, p.o(0.16f + (k % 4) * 0.05f, 0.42f + (k / 4) * 0.05f))
                    drawPath(Path().apply { moveTo(p.x(0.14f), p.y(0.64f)); cubicTo(p.x(0.36f), p.y(0.6f), p.x(0.42f), p.y(0.44f), p.x(0.6f), p.y(0.5f)); cubicTo(p.x(0.72f), p.y(0.54f), p.x(0.74f), p.y(0.36f), p.x(0.84f), p.y(0.3f)) },
                        Color(0xFF7A1A12), style = Stroke(lw * 0.8f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(lw * 2, lw * 1.4f))))
                    drawRect(Color(0xFF3A3A3A), Offset(p.x(0.8f), p.y(0.22f)), Size(p.x(0.08f), p.y(0.08f)))
                    drawLine(Color(0xFF3A3A3A), p.o(0.84f, 0.22f), p.o(0.84f, 0.16f), lw * 0.5f)
                }
                drawPath(Path().apply { moveTo(p.x(0.08f), p.y(0.14f)); lineTo(p.x(0.92f), p.y(0.18f)); lineTo(p.x(0.9f), p.y(0.72f)); lineTo(p.x(0.1f), p.y(0.7f)); close() }, p.metal(0.08f, 0.92f), style = Stroke(lw * 0.8f))
            }
            "Cuerpo" -> {
                // El yelmo: cúpula, nasal, carrilleras y remaches.
                val yelmo = Path().apply { moveTo(p.x(0.22f), p.y(0.66f)); lineTo(p.x(0.22f), p.y(0.38f)); cubicTo(p.x(0.22f), p.y(0.06f), p.x(0.78f), p.y(0.06f), p.x(0.78f), p.y(0.38f)); lineTo(p.x(0.78f), p.y(0.66f)); lineTo(p.x(0.62f), p.y(0.66f)); lineTo(p.x(0.6f), p.y(0.46f)); lineTo(p.x(0.4f), p.y(0.46f)); lineTo(p.x(0.38f), p.y(0.66f)); close() }
                drawPath(yelmo, p.metal(0.22f, 0.78f))
                caja(p, 0.47f, 0.38f, 0.53f, 0.62f)
                drawLine(p.linea, p.o(0.22f, 0.38f), p.o(0.78f, 0.38f), lw)
                if (p.hecho) for (k in 0 until 7) drawCircle(p.linea, p.w * 0.012f, p.o(0.26f + k * 0.08f, 0.36f))
                drawLine(p.linea, p.o(0.5f, 0.1f), p.o(0.5f, 0.36f), lw * 0.6f)
                caja(p, 0.3f, 0.7f, 0.7f, 0.76f)
            }
            "Mente" -> {
                // Un racimo de cristales: prismas hexagonales con sus brillos.
                listOf(Triple(0.5f, 0.06f, 0.14f), Triple(0.34f, 0.2f, 0.11f), Triple(0.66f, 0.24f, 0.1f), Triple(0.24f, 0.36f, 0.08f), Triple(0.76f, 0.4f, 0.08f)).forEachIndexed { i, (x, y, ww) ->
                    val prisma = Path().apply { moveTo(p.x(x), p.y(y)); lineTo(p.x(x + ww / 2), p.y(y + 0.08f)); lineTo(p.x(x + ww / 2), p.y(0.66f)); lineTo(p.x(x - ww / 2), p.y(0.66f)); lineTo(p.x(x - ww / 2), p.y(y + 0.08f)); close() }
                    val col = if (i % 2 == 0) cristal else p.c(Color(0xFFB79AD8))
                    drawPath(prisma, Brush.horizontalGradient(listOf(lerp(col, Color.White, 0.4f), col, lerp(col, Color.Black, 0.25f)), p.x(x - ww / 2), p.x(x + ww / 2)))
                    drawPath(prisma, p.linea, style = Stroke(lw * 0.4f))
                    drawLine(p.linea.copy(alpha = 0.4f), p.o(x, y), p.o(x, 0.66f), lw * 0.3f)
                }
                drawPath(Path().apply { moveTo(p.x(0.12f), p.y(0.76f)); quadraticTo(p.x(0.2f), p.y(0.62f), p.x(0.5f), p.y(0.64f)); quadraticTo(p.x(0.8f), p.y(0.62f), p.x(0.88f), p.y(0.76f)); close() }, p.b(Color(0xFF6B6F7A)))
            }
            "Cultura" -> {
                // Un arpa pequeña: caja de resonancia, cuello curvo, columna y cuerdas.
                val madera = p.c(Color(0xFF8A5A2B))
                drawLine(madera, p.o(0.26f, 0.74f), p.o(0.26f, 0.12f), lw * 2.2f, StrokeCap.Round)
                drawPath(Path().apply { moveTo(p.x(0.26f), p.y(0.12f)); cubicTo(p.x(0.44f), p.y(0.04f), p.x(0.6f), p.y(0.28f), p.x(0.78f), p.y(0.2f)) }, madera, style = Stroke(lw * 2, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(p.x(0.26f), p.y(0.74f)); lineTo(p.x(0.84f), p.y(0.24f)); lineTo(p.x(0.74f), p.y(0.2f)); lineTo(p.x(0.2f), p.y(0.66f)); close() }, p.metal(0.2f, 0.84f))
                for (k in 0 until 7) { val t = 0.1f + k * 0.12f; val x = 0.26f + 0.5f * t; val yTop = 0.12f + 0.08f * t; val yBot = 0.74f - 0.5f * t + 0.02f
                    drawLine(p.c(Color(0xFFE9E1CF)), p.o(x, yTop + 0.03f), p.o(x, yBot), lw * 0.35f) }
                if (p.hecho) runas(p.o(0.3f, 0.52f), p.x(0.18f), p.y(0.04f), p.linea, lw * 0.4f, 2, 9)
            }
            "Gente" -> {
                // El estandarte: travesaño, tela con dos colas y un emblema inventado (monte sobre un círculo).
                caja(p, 0.485f, 0.04f, 0.515f, 0.77f, SolidColor(p.c(Color(0xFF5A3A22))))
                caja(p, 0.2f, 0.1f, 0.8f, 0.13f)
                val tela = Path().apply { moveTo(p.x(0.24f), p.y(0.13f)); lineTo(p.x(0.76f), p.y(0.13f)); lineTo(p.x(0.76f), p.y(0.62f)); lineTo(p.x(0.63f), p.y(0.54f)); lineTo(p.x(0.5f), p.y(0.64f)); lineTo(p.x(0.37f), p.y(0.54f)); lineTo(p.x(0.24f), p.y(0.62f)); close() }
                drawPath(tela, SolidColor(p.c(Color(0xFF1F3A5A))))
                drawPath(tela, p.metal(0.24f, 0.76f), style = Stroke(lw * 0.8f))
                drawCircle(p.metal(0.38f, 0.62f), p.w * 0.12f, p.o(0.5f, 0.32f), style = Stroke(lw))
                drawPath(Path().apply { moveTo(p.x(0.38f), p.y(0.4f)); lineTo(p.x(0.46f), p.y(0.26f)); lineTo(p.x(0.5f), p.y(0.32f)); lineTo(p.x(0.55f), p.y(0.22f)); lineTo(p.x(0.62f), p.y(0.4f)); close() }, p.metal(0.38f, 0.62f))
            }
            "Constancia" -> {
                // La llave de hierro con su ojo de tres lóbulos.
                val c = p.o(0.5f, 0.18f)
                listOf(0.0, 2 * PI / 3, 4 * PI / 3).forEach { a -> drawCircle(p.metal(0.3f, 0.7f), p.w * 0.08f, Offset(c.x + (p.w * 0.08f * cos(a - PI / 2)).toFloat(), c.y + (p.w * 0.08f * sin(a - PI / 2)).toFloat()), style = Stroke(lw * 1.6f)) }
                drawCircle(rubi, p.w * 0.035f, c)
                caja(p, 0.47f, 0.28f, 0.53f, 0.72f)
                caja(p, 0.44f, 0.32f, 0.56f, 0.35f)
                caja(p, 0.53f, 0.58f, 0.66f, 0.62f); caja(p, 0.53f, 0.66f, 0.62f, 0.72f); caja(p, 0.6f, 0.62f, 0.66f, 0.66f)
            }
            "Curiosos" -> {
                // Una gema tallada en brillante, con sus facetas.
                val c = p.o(0.5f, 0.42f); val R = p.w * 0.3f
                val corona = listOf(Offset(c.x - R, c.y - R * 0.2f), Offset(c.x - R * 0.55f, c.y - R * 0.6f), Offset(c.x + R * 0.55f, c.y - R * 0.6f), Offset(c.x + R, c.y - R * 0.2f))
                val gema = Path().apply { moveTo(corona[0].x, corona[0].y); corona.drop(1).forEach { lineTo(it.x, it.y) }; lineTo(c.x, c.y + R * 0.9f); close() }
                drawPath(gema, Brush.linearGradient(listOf(lerp(esmeralda, Color.White, 0.4f), esmeralda, lerp(esmeralda, Color.Black, 0.4f)), Offset(c.x - R, c.y - R), Offset(c.x + R, c.y + R)))
                val l = p.linea.copy(alpha = if (p.hecho) 0.5f else 0.8f)
                drawLine(l, corona[0], corona[3], lw * 0.5f)
                listOf(-0.55f, -0.18f, 0.18f, 0.55f).forEach { f -> drawLine(l, Offset(c.x + R * f, c.y - R * 0.2f), Offset(c.x, c.y + R * 0.9f), lw * 0.4f) }
                drawLine(l, corona[1], Offset(c.x - R * 0.18f, c.y - R * 0.2f), lw * 0.4f); drawLine(l, corona[2], Offset(c.x + R * 0.18f, c.y - R * 0.2f), lw * 0.4f)
                drawPath(gema, p.metal(0.2f, 0.8f), style = Stroke(lw * 0.8f))
                if (p.hecho) estrella(Offset(c.x - R * 0.35f, c.y - R * 0.38f), p.w * 0.05f, p.w * 0.012f, 4, SolidColor(Color.White))
            }
            else -> {
                // La linterna de mina: jaula de hierro, el cristal con la luz y el asa.
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD27A).copy(alpha = 0.5f), Color.Transparent), p.o(0.5f, 0.44f), p.w * 0.4f), p.w * 0.4f, p.o(0.5f, 0.44f))
                drawArc(hierro, 180f, 180f, false, Offset(p.x(0.36f), p.y(0.04f)), Size(p.x(0.28f), p.y(0.14f)), style = Stroke(lw * 1.2f))
                caja(p, 0.32f, 0.16f, 0.68f, 0.22f)
                drawRect(p.c(Color(0xFFFFE6A3)).copy(alpha = if (p.hecho) 0.75f else 1f), Offset(p.x(0.36f), p.y(0.22f)), Size(p.x(0.28f), p.y(0.38f)))
                if (p.hecho) drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.3f)); cubicTo(p.x(0.55f), p.y(0.38f), p.x(0.54f), p.y(0.44f), p.x(0.5f), p.y(0.46f)); cubicTo(p.x(0.46f), p.y(0.44f), p.x(0.45f), p.y(0.38f), p.x(0.5f), p.y(0.3f)); close() }, Color(0xFFFF8A2E))
                listOf(0.36f, 0.5f, 0.64f).forEach { x -> drawLine(hierro, p.o(x, 0.22f), p.o(x, 0.6f), lw * 0.9f) }
                caja(p, 0.3f, 0.6f, 0.7f, 0.68f)
                caja(p, 0.36f, 0.68f, 0.64f, 0.74f, SolidColor(hierro))
            }
        }
    }

    // ---------------------------------------------------------------- Prehistoria: lo que guarda el clan de la cueva

    internal fun DrawScope.cueva(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val caliza = p.c(Color(0xFFD9C7A6)); val ocre = p.c(Color(0xFFA2401F)); val carbon = p.c(Color(0xFF2E2620))
        val hueso = p.c(Color(0xFFE8DCC4)); val silex = p.c(Color(0xFF6E6A62)); val madera = p.c(Color(0xFF7A5232))
        when (cat) {
            "Diario" -> {
                // La laja pintada: caliza de borde partido, una mano en negativo y un bisonte de carbón.
                val laja = Path().apply { moveTo(p.x(0.16f), p.y(0.12f)); lineTo(p.x(0.6f), p.y(0.06f)); lineTo(p.x(0.86f), p.y(0.16f)); lineTo(p.x(0.84f), p.y(0.7f)); lineTo(p.x(0.5f), p.y(0.76f)); lineTo(p.x(0.14f), p.y(0.7f)); close() }
                drawPath(laja, SolidColor(caliza)); drawPath(laja, p.metal(0.14f, 0.86f), style = Stroke(lw * 1.2f))
                val m = p.o(0.4f, 0.32f)
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(ocre, ocre.copy(alpha = 0f)), m, p.w * 0.2f), p.w * 0.2f, m)
                piezasMano(m, p.w * 0.13f).forEach { drawPath(it, if (p.hecho) caliza else p.linea) }
                drawPath(bisonte(p.o(0.6f, 0.58f), p.w * 0.18f), carbon)
            }
            "Mundo" -> {
                // Huellas en el barro: un charco seco y los pies descalzos que lo cruzaron, como las que quedaron en las cuevas.
                val barro = p.c(Color(0xFF8C6B4A)); val huella = p.c(Color(0xFF4A3424))
                drawOval(SolidColor(barro), Offset(p.x(0.08f), p.y(0.22f)), Size(p.x(0.84f), p.y(0.5f)))
                drawOval(p.metal(0.08f, 0.92f), Offset(p.x(0.08f), p.y(0.22f)), Size(p.x(0.84f), p.y(0.5f)), style = Stroke(lw))
                listOf(Triple(0.26f, 0.58f, -20f), Triple(0.42f, 0.44f, 10f), Triple(0.58f, 0.56f, -15f), Triple(0.74f, 0.4f, 15f)).forEach { (x, y, g) ->
                    val c = p.o(x, y)
                    rotate(g, c) {
                        drawOval(huella, Offset(c.x - p.w * 0.035f, c.y - p.w * 0.04f), Size(p.w * 0.07f, p.w * 0.11f))
                        for (k in 0 until 5) drawCircle(huella, p.w * (0.014f - k * 0.0015f), Offset(c.x - p.w * 0.03f + k * p.w * 0.016f, c.y - p.w * (0.06f - k * 0.004f)))
                    }
                }
            }
            "Cuerpo" -> {
                // El propulsor de asta con su gancho y, encima, la azagaya con su punta de sílex atada con tendón.
                drawLine(madera, p.o(0.1f, 0.7f), p.o(0.86f, 0.14f), lw * 1.6f, StrokeCap.Round)
                drawPath(Path().apply { moveTo(p.x(0.95f), p.y(0.06f)); lineTo(p.x(0.83f), p.y(0.12f)); lineTo(p.x(0.88f), p.y(0.17f)); close() }, SolidColor(silex))
                listOf(0.0f, 0.03f).forEach { f -> drawLine(hueso, p.o(0.8f - f, 0.15f + f * 0.7f), p.o(0.84f - f, 0.21f + f * 0.7f), lw * 0.6f) }
                drawLine(hueso, p.o(0.14f, 0.76f), p.o(0.56f, 0.44f), lw * 3.2f, StrokeCap.Round)
                drawCircle(hueso, p.w * 0.04f, p.o(0.58f, 0.42f))
                drawLine(p.linea, p.o(0.14f, 0.76f), p.o(0.56f, 0.44f), lw * 0.4f)
                if (p.hecho) for (k in 0 until 4) drawCircle(ocre, p.w * 0.01f, p.o(0.22f + k * 0.08f, 0.7f - k * 0.061f))
            }
            "Mente" -> {
                // La lámpara de grasa: un cuenco de arenisca con mango grabado, la mecha y la llama que deja ver la pared.
                val arenisca = p.c(Color(0xFFB4865A))
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFC46B).copy(alpha = 0.55f), Color.Transparent), p.o(0.44f, 0.36f), p.w * 0.42f), p.w * 0.42f, p.o(0.44f, 0.36f))
                drawArc(SolidColor(arenisca), 0f, 180f, true, Offset(p.x(0.16f), p.y(0.42f)), Size(p.x(0.56f), p.y(0.3f)))
                caja(p, 0.7f, 0.53f, 0.92f, 0.6f, SolidColor(arenisca))
                if (p.hecho) for (k in 0 until 3) { val x = 0.75f + k * 0.055f; drawLine(carbon, p.o(x, 0.545f), p.o(x + 0.025f, 0.57f), lw * 0.5f); drawLine(carbon, p.o(x + 0.025f, 0.57f), p.o(x + 0.05f, 0.545f), lw * 0.5f) }
                drawLine(p.linea, p.o(0.16f, 0.57f), p.o(0.72f, 0.57f), lw)
                val llama = Path().apply { moveTo(p.x(0.42f), p.y(0.24f)); quadraticTo(p.x(0.5f), p.y(0.44f), p.x(0.42f), p.y(0.56f)); quadraticTo(p.x(0.34f), p.y(0.44f), p.x(0.42f), p.y(0.24f)); close() }
                drawPath(llama, SolidColor(p.c(Color(0xFFFF9A3C))))
                if (p.hecho) drawPath(Path().apply { moveTo(p.x(0.42f), p.y(0.36f)); quadraticTo(p.x(0.46f), p.y(0.48f), p.x(0.42f), p.y(0.55f)); quadraticTo(p.x(0.38f), p.y(0.48f), p.x(0.42f), p.y(0.36f)); close() }, Color(0xFFFFE08A))
            }
            "Cultura" -> {
                // La flauta de hueso de ave, con sus agujeros, como las halladas en las cuevas del Jura de Suabia; y el sonido que sale.
                rotate(-18f, p.o(0.46f, 0.44f)) {
                    caja(p, 0.1f, 0.4f, 0.82f, 0.48f, SolidColor(hueso))
                    drawCircle(hueso, p.w * 0.05f, p.o(0.1f, 0.44f)); drawCircle(hueso, p.w * 0.045f, p.o(0.82f, 0.44f))
                    drawRect(p.metal(0.1f, 0.82f), Offset(p.x(0.1f), p.y(0.4f)), Size(p.x(0.72f), p.y(0.08f)), style = Stroke(lw * 0.6f))
                    for (k in 0 until 5) drawCircle(carbon, p.w * 0.014f, p.o(0.3f + k * 0.1f, 0.44f))
                }
                if (p.hecho) listOf(0.08f, 0.14f, 0.2f).forEach { r ->
                    drawArc(ocre, -70f, 70f, false, Offset(p.x(0.84f) - p.w * r, p.y(0.22f) - p.w * r), Size(p.w * r * 2, p.w * r * 2), style = Stroke(lw * 0.8f, cap = StrokeCap.Round))
                }
            }
            "Gente" -> {
                // El collar de conchas perforadas y dientes de ciervo: cada cuenta, un regalo de otro clan.
                val concha = p.c(Color(0xFFF2E8D6)); val diente = p.c(Color(0xFFE6D3A8))
                drawPath(Path().apply { moveTo(p.x(0.14f), p.y(0.14f)); quadraticTo(p.x(0.5f), p.y(1f), p.x(0.86f), p.y(0.14f)) }, p.linea, style = Stroke(lw * 0.6f))
                for (k in 0 until 11) {
                    val t = 0.08f + k * 0.084f
                    val x = (1 - t) * (1 - t) * 0.14f + 2 * (1 - t) * t * 0.5f + t * t * 0.86f
                    val y = (1 - t) * (1 - t) * 0.14f + 2 * (1 - t) * t * 1f + t * t * 0.14f
                    val c = p.o(x, y)
                    if (k % 4 == 2) drawPath(Path().apply { moveTo(c.x - p.w * 0.018f, c.y - p.w * 0.01f); quadraticTo(c.x, c.y + p.w * 0.07f, c.x + p.w * 0.018f, c.y - p.w * 0.01f); close() }, SolidColor(diente))
                    else { drawOval(SolidColor(concha), Offset(c.x - p.w * 0.03f, c.y - p.w * 0.022f), Size(p.w * 0.06f, p.w * 0.05f)); drawCircle(p.linea, p.w * 0.008f, c) }
                }
                if (p.hecho) drawCircle(ocre, p.w * 0.02f, p.o(0.5f, 0.6f))
            }
            "Constancia" -> {
                // El hueso con muescas: una cuenta grabada de cinco en cinco, como en los huesos marcados del Paleolítico.
                rotate(-8f, p.o(0.5f, 0.42f)) {
                    caja(p, 0.16f, 0.36f, 0.84f, 0.5f, SolidColor(hueso))
                    listOf(0.16f, 0.84f).forEach { x -> drawCircle(hueso, p.w * 0.06f, p.o(x, 0.39f)); drawCircle(hueso, p.w * 0.06f, p.o(x, 0.47f)) }
                    drawRect(p.metal(0.16f, 0.84f), Offset(p.x(0.16f), p.y(0.36f)), Size(p.x(0.68f), p.y(0.14f)), style = Stroke(lw * 0.5f))
                    val n = if (p.hecho) 15 else 6
                    for (k in 0 until n) { val x = 0.24f + k * 0.036f + (k / 5) * 0.03f; drawLine(carbon, p.o(x, 0.38f), p.o(x, 0.48f), lw * 0.7f) }
                }
            }
            "Curiosos" -> {
                // El bifaz: un sílex tallado por las dos caras, en forma de almendra, con las huellas de cada golpe.
                val bifaz = Path().apply {
                    moveTo(p.x(0.5f), p.y(0.06f)); cubicTo(p.x(0.72f), p.y(0.2f), p.x(0.8f), p.y(0.56f), p.x(0.62f), p.y(0.74f)); quadraticTo(p.x(0.5f), p.y(0.78f), p.x(0.38f), p.y(0.74f))
                    cubicTo(p.x(0.2f), p.y(0.56f), p.x(0.28f), p.y(0.2f), p.x(0.5f), p.y(0.06f)); close()
                }
                drawPath(bifaz, if (p.hecho) Brush.linearGradient(listOf(lerp(silex, Color.White, 0.35f), silex, lerp(silex, Color.Black, 0.35f)), p.o(0.3f, 0.1f), p.o(0.7f, 0.74f)) else SolidColor(silex))
                clipPath(bifaz) {
                    for (k in 0 until 6) { val y = 0.16f + k * 0.1f
                        listOf(-1f, 1f).forEach { s -> drawArc(p.linea.copy(alpha = 0.5f), if (s < 0f) -60f else 120f, 120f, false, Offset(p.x(0.5f + s * 0.14f) - p.w * 0.08f, p.y(y) - p.w * 0.05f), Size(p.w * 0.16f, p.w * 0.1f), style = Stroke(lw * 0.5f)) } }
                }
                drawPath(bifaz, p.metal(0.25f, 0.75f), style = Stroke(lw * 0.8f))
            }
            else -> {
                // Tu mano en negativo: la roca, la nube de ocre soplado y la mano que queda del color de la piedra.
                val roca = Path().apply { moveTo(p.x(0.12f), p.y(0.14f)); quadraticTo(p.x(0.5f), p.y(0.02f), p.x(0.88f), p.y(0.16f)); lineTo(p.x(0.9f), p.y(0.74f)); quadraticTo(p.x(0.5f), p.y(0.8f), p.x(0.1f), p.y(0.72f)); close() }
                drawPath(roca, SolidColor(caliza)); drawPath(roca, p.metal(0.1f, 0.9f), style = Stroke(lw))
                val m = p.o(0.5f, 0.44f)
                if (p.hecho) {
                    drawCircle(Brush.radialGradient(listOf(ocre, ocre.copy(alpha = 0.7f), ocre.copy(alpha = 0f)), m, p.w * 0.3f), p.w * 0.3f, m)
                    val az = java.util.Random(9)
                    repeat(40) { val a = az.nextFloat() * 2 * PI.toFloat(); val r = p.w * (0.2f + az.nextFloat() * 0.14f); drawCircle(ocre.copy(alpha = 0.5f), p.w * 0.008f, Offset(m.x + r * cos(a), m.y + r * sin(a))) }
                }
                piezasMano(m, p.w * 0.22f).forEach { drawPath(it, if (p.hecho) caliza else p.linea) }
            }
        }
    }

    // ---------------------------------------------------------------- Distopía: la oficina del Ministerio

    internal fun DrawScope.distopia(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val carton = p.c(Color(0xFFD8C08A)); val papel = p.c(Color(0xFFF2EFE6)); val tinta = p.c(Color(0xFF141414))
        val rojo = p.c(Color(0xFFC8102E)); val gris = p.c(Color(0xFF55585C)); val acero = p.c(Color(0xFF8A8D91))
        fun lineas(x0: Float, y0: Float, x1: Float, n: Int, paso: Float, col: Color = gris) { for (k in 0 until n) drawLine(col, p.o(x0, y0 + k * paso), p.o(x1 - (if (k % 3 == 2) 0.12f else 0f), y0 + k * paso), lw * 0.45f) }
        when (cat) {
            "Diario" -> {
                // El expediente: carpeta de cartón con pestaña, hojas que asoman y el sello rojo de «archivado».
                caja(p, 0.2f, 0.18f, 0.8f, 0.72f, SolidColor(lerp(carton, Color.Black, 0.12f)))
                caja(p, 0.22f, 0.14f, 0.78f, 0.68f, SolidColor(papel))
                drawPath(Path().apply { moveTo(p.x(0.18f), p.y(0.24f)); lineTo(p.x(0.42f), p.y(0.24f)); lineTo(p.x(0.46f), p.y(0.2f)); lineTo(p.x(0.82f), p.y(0.2f)); lineTo(p.x(0.82f), p.y(0.74f)); lineTo(p.x(0.18f), p.y(0.74f)); close() }, SolidColor(carton))
                drawRect(p.metal(0.2f, 0.8f), Offset(p.x(0.18f), p.y(0.2f)), Size(p.x(0.64f), p.y(0.54f)), style = Stroke(lw * 0.7f))
                if (p.hecho) { lineas(0.26f, 0.32f, 0.6f, 3, 0.05f, tinta.copy(alpha = 0.55f)); texto("Nº 0417", p, 0.36f, 0.62f, 0.07f, tinta) }
                drawCircle(rojo, p.w * 0.11f, p.o(0.66f, 0.56f), style = Stroke(lw * 1.2f))
                drawLine(rojo, p.o(0.58f, 0.6f), p.o(0.74f, 0.52f), lw * 1.1f)
            }
            "Mundo" -> {
                // El pase de control: tarjeta con franja roja, código de barras y el sello de la zona.
                caja(p, 0.16f, 0.2f, 0.84f, 0.66f, SolidColor(papel))
                caja(p, 0.16f, 0.2f, 0.84f, 0.3f, SolidColor(rojo))
                if (p.hecho) texto("ZONA 3", p, 0.5f, 0.25f, 0.075f, Color(0xFFF2EFE6))
                val barras = listOf(2, 1, 3, 1, 1, 2, 3, 1, 2, 1, 1, 3, 2, 1, 2)
                var x = 0.22f
                barras.forEach { b -> val ww = b * 0.009f; caja(p, x, 0.38f, x + ww, 0.58f, SolidColor(tinta)); x += ww + 0.012f }
                drawCircle(gris, p.w * 0.08f, p.o(0.72f, 0.48f), style = Stroke(lw * 0.9f))
                estrella(p.o(0.72f, 0.48f), p.w * 0.04f, p.w * 0.018f, 5, SolidColor(gris))
                drawRect(p.metal(0.16f, 0.84f), Offset(p.x(0.16f), p.y(0.2f)), Size(p.x(0.68f), p.y(0.46f)), style = Stroke(lw * 0.8f))
            }
            "Cuerpo" -> {
                // La insignia numerada: placa con muescas en las esquinas, el número grande y dos remaches.
                val placa = Path().apply { val a = 0.06f; moveTo(p.x(0.2f + a), p.y(0.16f)); lineTo(p.x(0.8f - a), p.y(0.16f)); lineTo(p.x(0.8f), p.y(0.16f + a)); lineTo(p.x(0.8f), p.y(0.68f - a))
                    lineTo(p.x(0.8f - a), p.y(0.68f)); lineTo(p.x(0.2f + a), p.y(0.68f)); lineTo(p.x(0.2f), p.y(0.68f - a)); lineTo(p.x(0.2f), p.y(0.16f + a)); close() }
                drawPath(placa, p.metal(0.2f, 0.8f))
                drawPath(placa, p.linea, style = Stroke(lw * 0.7f))
                texto("7", p, 0.5f, 0.38f, 0.26f, p.c(Color(0xFF1F1F1F)))
                caja(p, 0.28f, 0.56f, 0.72f, 0.6f, SolidColor(rojo))
                listOf(0.27f, 0.73f).forEach { xx -> drawCircle(p.linea, p.w * 0.018f, p.o(xx, 0.24f)) }
            }
            "Mente" -> {
                // La tarjeta perforada: esquina cortada, columnas de números y los agujeros de las respuestas.
                val tarjeta = Path().apply { moveTo(p.x(0.22f), p.y(0.14f)); lineTo(p.x(0.84f), p.y(0.14f)); lineTo(p.x(0.84f), p.y(0.7f)); lineTo(p.x(0.16f), p.y(0.7f)); lineTo(p.x(0.16f), p.y(0.22f)); close() }
                drawPath(tarjeta, SolidColor(carton))
                val az = java.util.Random(41)
                for (col in 0 until 10) for (fila in 0 until 8) {
                    val cx = 0.22f + col * 0.06f; val cy = 0.22f + fila * 0.06f
                    if (az.nextInt(5) == 0) caja(p, cx - 0.012f, cy - 0.016f, cx + 0.012f, cy + 0.016f, SolidColor(if (p.hecho) Color(0xFF2A2A2A) else p.sombra))
                    else if (p.hecho) drawCircle(gris.copy(alpha = 0.35f), p.w * 0.004f, p.o(cx, cy))
                }
                drawPath(tarjeta, p.metal(0.16f, 0.84f), style = Stroke(lw * 0.8f))
            }
            "Cultura" -> {
                // El documento censurado: hoja con renglones y barras negras encima.
                caja(p, 0.22f, 0.08f, 0.78f, 0.72f, SolidColor(papel))
                if (p.hecho) lineas(0.27f, 0.2f, 0.73f, 9, 0.055f)
                listOf(Triple(0.3f, 0.25f, 0.66f), Triple(0.27f, 0.36f, 0.5f), Triple(0.45f, 0.47f, 0.73f), Triple(0.27f, 0.58f, 0.6f)).forEach { (x0, y, x1) -> caja(p, x0, y - 0.018f, x1, y + 0.018f, SolidColor(tinta)) }
                drawRect(p.metal(0.22f, 0.78f), Offset(p.x(0.22f), p.y(0.08f)), Size(p.x(0.56f), p.y(0.64f)), style = Stroke(lw * 0.8f))
                if (p.hecho) texto("CLASIFICADO", p, 0.5f, 0.13f, 0.055f, rojo)
            }
            "Gente" -> {
                // La credencial: tarjeta colgada de su cinta, con foto vacía (solo el marco), huella y número.
                drawLine(rojo, p.o(0.42f, 0.02f), p.o(0.48f, 0.16f), lw * 1.4f); drawLine(rojo, p.o(0.58f, 0.02f), p.o(0.52f, 0.16f), lw * 1.4f)
                caja(p, 0.45f, 0.14f, 0.55f, 0.2f)
                caja(p, 0.2f, 0.2f, 0.8f, 0.7f, SolidColor(papel))
                caja(p, 0.26f, 0.28f, 0.48f, 0.54f, SolidColor(p.c(Color(0xFFCFCBC0))))
                if (p.hecho) {
                    for (k in 0 until 5) drawArc(gris, 200f, 140f, false, Offset(p.x(0.6f - k * 0.015f), p.y(0.3f - k * 0.012f)), Size(p.x(0.1f + k * 0.03f), p.y(0.1f + k * 0.03f)), style = Stroke(lw * 0.4f))
                    lineas(0.26f, 0.6f, 0.74f, 2, 0.05f, tinta.copy(alpha = 0.6f))
                }
                drawRect(p.metal(0.2f, 0.8f), Offset(p.x(0.2f), p.y(0.2f)), Size(p.x(0.6f), p.y(0.5f)), style = Stroke(lw * 0.9f))
            }
            "Constancia" -> {
                // El sello de goma: mango, cuerpo de metal y la huella roja del sello debajo.
                drawCircle(p.c(Color(0xFF3A2A1E)), p.w * 0.09f, p.o(0.5f, 0.14f))
                caja(p, 0.46f, 0.2f, 0.54f, 0.36f, SolidColor(p.c(Color(0xFF3A2A1E))))
                caja(p, 0.3f, 0.36f, 0.7f, 0.46f)
                caja(p, 0.32f, 0.46f, 0.68f, 0.5f, SolidColor(tinta))
                drawRect(rojo, Offset(p.x(0.24f), p.y(0.56f)), Size(p.x(0.52f), p.y(0.16f)), style = Stroke(lw * 1.2f))
                if (p.hecho) texto("APROBADO", p, 0.5f, 0.64f, 0.065f, rojo)
            }
            "Curiosos" -> {
                // La cámara de vigilancia: brazo de pared, cuerpo y lente con su luz roja.
                caja(p, 0.74f, 0.16f, 0.82f, 0.42f, SolidColor(gris))
                drawLine(gris, p.o(0.78f, 0.28f), p.o(0.62f, 0.34f), lw * 1.6f)
                val cuerpo = Path().apply { moveTo(p.x(0.18f), p.y(0.3f)); lineTo(p.x(0.64f), p.y(0.24f)); lineTo(p.x(0.66f), p.y(0.42f)); lineTo(p.x(0.2f), p.y(0.5f)); close() }
                drawPath(cuerpo, p.metal(0.18f, 0.66f))
                drawPath(cuerpo, p.linea, style = Stroke(lw * 0.6f))
                drawCircle(tinta, p.w * 0.07f, p.o(0.2f, 0.4f)); drawCircle(p.c(Color(0xFF2E4A6E)), p.w * 0.04f, p.o(0.2f, 0.4f))
                drawCircle(rojo, p.w * 0.018f, p.o(0.58f, 0.3f))
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFFC8102E).copy(alpha = 0.45f), Color.Transparent), p.o(0.58f, 0.3f), p.w * 0.06f), p.w * 0.06f, p.o(0.58f, 0.3f))
            }
            else -> {
                // El altavoz de la plaza: bocina de metal en su poste, con las ondas del anuncio.
                caja(p, 0.47f, 0.4f, 0.53f, 0.76f, SolidColor(gris))
                val bocina = Path().apply { moveTo(p.x(0.36f), p.y(0.3f)); lineTo(p.x(0.36f), p.y(0.42f)); lineTo(p.x(0.74f), p.y(0.56f)); lineTo(p.x(0.74f), p.y(0.16f)); close() }
                drawPath(bocina, p.metal(0.36f, 0.74f))
                drawPath(bocina, p.linea, style = Stroke(lw * 0.6f))
                caja(p, 0.3f, 0.3f, 0.36f, 0.42f, SolidColor(acero))
                if (p.hecho) listOf(0.1f, 0.16f, 0.22f).forEachIndexed { k, r ->
                    drawArc(rojo.copy(alpha = 0.8f - k * 0.2f), -40f, 80f, false, Offset(p.x(0.74f) - p.w * r, p.y(0.36f) - p.w * r), Size(p.w * r * 2, p.w * r * 2), style = Stroke(lw * 0.8f))
                }
            }
        }
    }

    /**
     * Psicodelia: los recuerdos del verano de 1967 en San Francisco, cada uno sobre su caja de fruta de madera (la dibuja
     * `LogrosPsicodelia`): el cartel de la semana clavado en un poste de la calle, la camioneta de reparto pintada a mano, la
     * cinta para la frente en su percha, la lámpara de cera, la guitarra pintada en su atril, el chaleco con los botones de
     * solapa, el disco de larga duración, la campanilla de templo y la flor que se regala. Dibujados como un cartel de la sala
     * de baile: colores planos que vibran junto a su complementario (la segunda tinta, corrida apenas), el contorno violeta de
     * la tinta y una sombra de borde duro a la derecha; de noche, bajo la luz negra, lo fluorescente brilla. El metal del
     * nivel va en las tachuelas, los herrajes, los tapacubos, las clavijas, el disco y la campanilla.
     */
    internal fun DrawScope.psicodelia(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val fino = p.h > 70f * density
        val T = TrazosPsicodelia
        /** La tinta del cartel: el violeta casi negro de la imprenta. */
        val tinta = Color(0xFF2A1C32)
        /** De noche la pantalla es el negro violeta de la sala: la silueta sin ganar se ve clara sobre él. */
        val noche = p.sombra.luminance() > 0.5f
        /** De noche, bajo la luz negra, cada color de cartel pasa a su tinta fluorescente. */
        fun fl(col: Color): Color = if (!noche) col else when (col) {
            T.MAGENTA -> T.FLUOR_MAGENTA; T.VERDE -> T.LIMON; T.NARANJA -> T.FLUOR_NARANJA; T.VIOLETA -> T.FLUOR_VIOLETA
            T.AMARILLO -> Color(0xFFFFF07A); T.AZUL -> Color(0xFF7FA6FF); T.CASA_TURQUESA -> Color(0xFF5FF0E0); T.CASA_ROSA -> Color(0xFFFF9ED0)
            else -> col
        }
        /** La sombra de cartel: el color plano con la luz arriba a la izquierda y una franja de sombra de borde duro a la derecha. */
        fun plano(col: Color, x0: Float, x1: Float, corte: Float = 0.7f): Brush {
            if (!p.hecho) return SolidColor(p.sombra)
            val k = fl(col)
            return Brush.horizontalGradient(0f to lerp(k, Color.White, 0.2f), 0.3f to k, corte to k, corte + 0.005f to lerp(k, tinta, 0.26f), 1f to lerp(k, tinta, 0.34f),
                startX = p.x(x0), endX = p.x(x1))
        }
        /** Lo mismo de arriba abajo: la sombra en la parte de abajo. */
        fun planoV(col: Color, y0: Float, y1: Float, corte: Float = 0.68f): Brush {
            if (!p.hecho) return SolidColor(p.sombra)
            val k = fl(col)
            return Brush.verticalGradient(0f to lerp(k, Color.White, 0.18f), 0.3f to k, corte to k, corte + 0.005f to lerp(k, tinta, 0.24f), 1f to lerp(k, tinta, 0.3f),
                startY = p.y(y0), endY = p.y(y1))
        }
        /** La vibración del cartel: la forma en su complementario, corrida apenas abajo a la derecha, como una segunda tinta. */
        fun vibra(forma: Path, col: Color, d: Float = 1f) {
            if (p.hecho) translate(lw * 1.1f * d, lw * 0.8f * d) { drawPath(forma, fl(T.complementario(col)).copy(alpha = 0.9f)) }
        }
        /** El contorno de la tinta del cartel, blando y de puntas redondas. */
        fun contorno(forma: Path, g: Float = 0.55f, alfa: Float = 0.85f) {
            if (p.hecho) drawPath(forma, (if (noche) lerp(tinta, T.FLUOR_VIOLETA, 0.3f) else tinta).copy(alpha = alfa), style = Stroke(lw * g, join = StrokeJoin.Round, cap = StrokeCap.Round))
        }
        /** Un brillo blando: una gota blanca, solo si ya se ganó. */
        fun brillo(o: Offset, r: Float, alfa: Float = 0.42f) { if (p.hecho) drawPath(T.gota(o, r, 0.2f, 0.1f), Color.White.copy(alpha = alfa)) }
        /** De noche, el resplandor de lo fluorescente bajo la luz negra. */
        fun halo(o: Offset, r: Float, col: Color, alfa: Float = 0.38f) {
            if (noche && p.hecho) drawCircle(Brush.radialGradient(listOf(fl(col).copy(alpha = alfa), fl(col).copy(alpha = 0f)), o, r), r, o)
        }
        /** El metal del nivel en volumen redondo, con la luz arriba a la izquierda. */
        fun metalBulto(o: Offset, r: Float): Brush = if (p.hecho) Brush.radialGradient(listOf(lerp(p.brillo, Color.White, 0.35f), p.brillo, p.base, p.oscuro),
            Offset(o.x - r * 0.35f, o.y - r * 0.4f), r * 1.6f) else SolidColor(p.sombra)
        /** El destello del metal pulido: más vivo en cada nivel. */
        fun destello(o: Offset, r: Float) {
            if (!p.hecho || p.nivel < 2) return
            val t = r * (0.55f + 0.2f * p.nivel); val a = 0.3f + 0.15f * p.nivel
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = a * 0.7f), Color.Transparent), o, t * 0.7f), t * 0.7f, o)
            drawLine(Color.White.copy(alpha = a), Offset(o.x - t, o.y), Offset(o.x + t, o.y), lw * 0.3f, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = a * 0.8f), Offset(o.x, o.y - t * 0.7f), Offset(o.x, o.y + t * 0.7f), lw * 0.3f, StrokeCap.Round)
        }
        /** La sombra donde la pieza toca la caja. */
        fun sombraPie(x0: Float, x1: Float, y: Float = 0.765f) {
            if (!p.hecho) return
            drawOval(tinta.copy(alpha = if (noche) 0.55f else 0.24f), Offset(p.x(x0), p.y(y) - p.y(0.011f)), Size(p.x(x1 - x0), p.y(0.022f)))
        }
        /** Lo que aún no se gana: la silueta entera de una vez, para que no se encimen las transparencias. */
        fun silueta(vararg formas: Path) { val todo = Path(); formas.forEach { todo.addPath(it) }; drawPath(todo, p.sombra) }
        /** Un camino blando por los puntos [q] (fracciones del lienzo), por los puntos medios; cerrado si [cerrar]. */
        fun blanda(cerrar: Boolean, vararg q: Pair<Float, Float>): Path = Path().apply {
            val l = q.map { p.o(it.first, it.second) }
            if (cerrar) {
                val ini = Offset((l.last().x + l[0].x) / 2, (l.last().y + l[0].y) / 2)
                moveTo(ini.x, ini.y)
                for (i in l.indices) { val a = l[i]; val b = l[(i + 1) % l.size]; quadraticTo(a.x, a.y, (a.x + b.x) / 2, (a.y + b.y) / 2) }
                close()
            } else {
                moveTo(l[0].x, l[0].y)
                for (i in 1 until l.size - 1) quadraticTo(l[i].x, l[i].y, (l[i].x + l[i + 1].x) / 2, (l[i].y + l[i + 1].y) / 2)
                lineTo(l.last().x, l.last().y)
            }
        }
        fun rect(x0: Float, y0: Float, x1: Float, y1: Float, radio: Float = 0f): Path = Path().apply {
            addRoundRect(androidx.compose.ui.geometry.RoundRect(p.r(x0, y0, x1, y1), CornerRadius(p.x(radio))))
        }
        fun disco(o: Offset, r: Float): Path = Path().apply { addOval(Rect(o, r)) }
        when (cat) {
            "Diario" -> {
                // El cartel de la semana, recién pegado en un poste de la calle (propio, sin copiar ninguno): el fondo naranja,
                // el círculo azul que vibra con él, la flor magenta sobre verde, arriba las letras infladas que se derriten y
                // abajo las líneas de la fecha; tres tachuelas del metal del nivel y, debajo, el jirón de un cartel viejo.
                val poste = rect(0.415f, 0.02f, 0.585f, 0.765f)
                val travesano = rect(0.24f, 0.07f, 0.76f, 0.105f, 0.008f)
                val cx = 0.5f; val cy = 0.41f
                val hoja = Path().apply { addRect(p.r(0.17f, 0.17f, 0.83f, 0.68f)) }
                if (!p.hecho) { silueta(poste, travesano, hoja); return@with }
                sombraPie(0.36f, 0.64f)
                val madera = Color(0xFF8C7A64)
                drawPath(travesano, plano(madera, 0.24f, 0.76f, 0.8f))
                listOf(0.29f, 0.71f).forEach { x ->
                    val o = p.o(x, 0.06f)
                    drawOval(if (noche) Color(0xFF5FB89A) else Color(0xFF7FB8A0), Offset(o.x - p.x(0.022f), o.y - p.y(0.022f)), Size(p.x(0.044f), p.y(0.034f)))
                    brillo(Offset(o.x - p.x(0.008f), o.y - p.y(0.012f)), p.x(0.008f), 0.6f)
                }
                drawPath(poste, plano(madera, 0.415f, 0.585f, 0.62f))
                if (fino) for (k in 0 until 4) {
                    val x = 0.435f + k * 0.04f
                    drawPath(blanda(false, x to 0.03f, x + 0.006f to 0.25f, x - 0.004f to 0.5f, x + 0.005f to 0.76f), lerp(madera, tinta, 0.35f).copy(alpha = 0.5f), style = Stroke(lw * 0.3f))
                }
                // Los jirones de carteles viejos en el poste, desteñidos, y las grapas.
                drawPath(Path().apply { moveTo(p.x(0.42f), p.y(0.69f)); lineTo(p.x(0.58f), p.y(0.7f)); lineTo(p.x(0.57f), p.y(0.74f)); lineTo(p.x(0.52f), p.y(0.725f)); lineTo(p.x(0.47f), p.y(0.75f)); lineTo(p.x(0.42f), p.y(0.735f)); close() },
                    lerp(fl(T.CASA_ROSA), madera, 0.35f))
                drawPath(Path().apply { moveTo(p.x(0.42f), p.y(0.115f)); lineTo(p.x(0.58f), p.y(0.12f)); lineTo(p.x(0.575f), p.y(0.155f)); lineTo(p.x(0.5f), p.y(0.14f)); lineTo(p.x(0.43f), p.y(0.16f)); close() },
                    lerp(fl(T.AMARILLO), madera, 0.4f))
                contorno(poste, 0.45f, 0.6f)
                rotate(-3f, p.o(cx, cy)) {
                    // La sombra del cartel sobre el poste y el cartel.
                    translate(lw * 1.6f, lw * 1.6f) { drawPath(hoja, tinta.copy(alpha = 0.25f)) }
                    drawPath(hoja, fl(T.NARANJA))
                    clipPath(hoja) {
                        // El círculo azul y la flor: los dos pares complementarios que vibran.
                        val o = p.o(cx, 0.43f); val r = p.x(0.2f)
                        halo(o, r * 1.6f, T.NARANJA, 0.3f)
                        drawCircle(fl(T.AZUL), r, o)
                        for (k in 0 until 16) {
                            val a = k * PI.toFloat() / 8
                            drawLine(fl(T.AMARILLO).copy(alpha = 0.85f), Offset(o.x + cos(a) * r * 1.08f, o.y + sin(a) * r * 1.08f), Offset(o.x + cos(a) * r * 1.5f, o.y + sin(a) * r * 1.5f), lw * 0.9f, StrokeCap.Round)
                        }
                        with(T) { flor(o, r * 0.82f, fl(MAGENTA), fl(VERDE), 6, 0.6f, 0.26f) }
                        // Arriba, las letras infladas que llenan su banda y se derriten hacia el cartel.
                        drawRect(fl(T.AMARILLO), p.o(0.17f, 0.17f), Size(p.x(0.66f), p.y(0.1f)))
                        // Letras de bloque inventadas, apretadas hasta llenar la banda (al modo de Roller), sin decir nada.
                        val anchos = listOf(0.15f, 0.1f, 0.14f, 0.12f, 0.1f)
                        var x = 0.185f
                        anchos.forEachIndexed { i, a ->
                            val alto = if (i % 2 == 0) 0.084f else 0.07f
                            val y0 = 0.262f - alto
                            val letra = blanda(true, x to y0 + 0.01f, x + a * 0.5f to y0 - 0.006f, x + a to y0 + 0.01f, x + a + 0.004f to 0.262f, x + a * 0.5f to 0.266f, x - 0.004f to 0.262f)
                            vibra(letra, T.VIOLETA, 0.7f)
                            drawPath(letra, fl(T.VIOLETA))
                            // Los huecos de la letra, que se estiran como goma.
                            if (i % 3 == 0) {
                                drawPath(blanda(true, x + a * 0.3f to y0 + 0.022f, x + a * 0.4f to y0 + 0.018f, x + a * 0.38f to 0.25f, x + a * 0.28f to 0.25f), fl(T.AMARILLO))
                                drawPath(blanda(true, x + a * 0.62f to y0 + 0.022f, x + a * 0.72f to y0 + 0.018f, x + a * 0.7f to 0.25f, x + a * 0.6f to 0.25f), fl(T.AMARILLO))
                            } else if (i % 3 == 1) drawPath(blanda(true, x + a * 0.25f to y0 + 0.04f, x + a * 0.75f to y0 + 0.032f, x + a * 0.7f to y0 + 0.05f, x + a * 0.3f to y0 + 0.055f), fl(T.AMARILLO))
                            else drawPath(blanda(true, x + a * 0.45f to 0.262f, x + a * 0.55f to 0.262f, x + a * 0.58f to y0 + 0.025f, x + a * 0.42f to y0 + 0.025f), fl(T.AMARILLO))
                            // La gota que se derrite hacia el cartel.
                            val goteo = 0.025f + 0.022f * ((i * 3) % 4)
                            val gx = x + a * (0.3f + 0.4f * ((i * 5) % 3) / 2f)
                            drawPath(blanda(true, gx - 0.012f to 0.258f, gx + 0.012f to 0.258f, gx + 0.007f to 0.262f + goteo, gx - 0.007f to 0.262f + goteo), fl(T.VIOLETA))
                            drawCircle(fl(T.VIOLETA), p.x(0.014f), p.o(gx, 0.262f + goteo))
                            x += a + 0.006f
                        }
                        // Abajo, la banda violeta con las líneas de la fecha y la sala.
                        drawRect(fl(T.VIOLETA), p.o(0.17f, 0.6f), Size(p.x(0.66f), p.y(0.08f)))
                        listOf(0.622f to 0.6f, 0.65f to 0.42f).forEach { (y, l) ->
                            drawLine(fl(T.AMARILLO), p.o(0.5f - l / 2, y), p.o(0.5f + l / 2, y), p.y(0.014f), StrokeCap.Round)
                        }
                        // La esquina de abajo a la derecha, que se levanta.
                        val doblez = Path().apply { moveTo(p.x(0.83f), p.y(0.6f)); lineTo(p.x(0.83f), p.y(0.68f)); lineTo(p.x(0.75f), p.y(0.68f)); close() }
                        drawPath(doblez, tinta.copy(alpha = 0.35f))
                    }
                    drawPath(Path().apply { moveTo(p.x(0.83f), p.y(0.6f)); lineTo(p.x(0.75f), p.y(0.68f)); lineTo(p.x(0.77f), p.y(0.6f)); close() }, if (noche) Color(0xFFD9CFE6) else T.PAPEL)
                    contorno(hoja, 0.5f)
                    // Tres tachuelas: la cuarta esquina se levanta.
                    listOf(0.2f to 0.195f, 0.8f to 0.195f, 0.2f to 0.655f).forEach { (tx, ty) ->
                        val t = p.o(tx, ty); val rt = p.x(0.026f)
                        drawCircle(tinta.copy(alpha = 0.3f), rt, t + Offset(lw * 0.6f, lw * 0.6f))
                        drawCircle(metalBulto(t, rt), rt, t)
                        destello(Offset(t.x - rt * 0.3f, t.y - rt * 0.3f), rt * 0.6f)
                    }
                }
            }
            "Mundo" -> {
                // La camioneta de reparto pintada a mano (sin marca y sin parecerse a ningún vehículo famoso), de perfil, con el
                // morro redondo a la izquierda: la caja de dos tonos, la ola de arcoíris pintada a brocha, dos flores, la ventanilla,
                // la parrilla del techo con una maleta y una manta enrollada, y los tapacubos y las defensas del metal del nivel.
                val rr = p.x(0.085f); val yr = p.y(0.765f) - rr
                val ruedas = listOf(p.x(0.27f), p.x(0.74f))
                val carro = Path().apply {
                    moveTo(p.x(0.1f), p.y(0.69f)); lineTo(p.x(0.1f), p.y(0.53f))
                    quadraticTo(p.x(0.1f), p.y(0.45f), p.x(0.18f), p.y(0.44f))
                    lineTo(p.x(0.23f), p.y(0.43f)); lineTo(p.x(0.3f), p.y(0.33f))
                    quadraticTo(p.x(0.32f), p.y(0.305f), p.x(0.37f), p.y(0.305f))
                    lineTo(p.x(0.85f), p.y(0.305f)); quadraticTo(p.x(0.91f), p.y(0.305f), p.x(0.91f), p.y(0.36f))
                    lineTo(p.x(0.91f), p.y(0.69f)); close()
                }
                val techo = rect(0.36f, 0.25f, 0.86f, 0.3f, 0.01f)
                val llantas = Path().apply { ruedas.forEach { addOval(Rect(Offset(it, yr), rr)) } }
                if (!p.hecho) { silueta(carro, techo, llantas); return@with }
                sombraPie(0.1f, 0.9f)
                // La parrilla del techo con su carga.
                listOf(0.4f, 0.62f, 0.82f).forEach { x -> drawLine(p.oscuro, p.o(x, 0.305f), p.o(x, 0.275f), lw * 0.6f) }
                val maleta = rect(0.42f, 0.2f, 0.62f, 0.275f, 0.015f)
                drawPath(maleta, plano(T.NARANJA, 0.42f, 0.62f)); contorno(maleta, 0.45f)
                drawLine(p.c(p.oscuro), p.o(0.49f, 0.2f), p.o(0.49f, 0.188f), lw * 0.6f); drawLine(p.c(p.oscuro), p.o(0.55f, 0.2f), p.o(0.55f, 0.188f), lw * 0.6f)
                drawLine(p.c(p.base), p.o(0.49f, 0.188f), p.o(0.55f, 0.188f), lw * 0.7f, StrokeCap.Round)
                val manta = rect(0.64f, 0.215f, 0.82f, 0.275f, 0.03f)
                drawPath(manta, fl(T.CASA_ROSA)); clipPath(manta) { for (k in 0 until 5) drawRect(fl(listOf(T.VERDE, T.AMARILLO, T.AZUL, T.VIOLETA, T.NARANJA)[k]), p.o(0.66f + k * 0.034f, 0.215f), Size(p.x(0.012f), p.y(0.06f))) }
                contorno(manta, 0.45f)
                drawPath(techo, p.metal(0.36f, 0.86f))
                // La caja: blanca arriba y turquesa abajo, separadas por una línea que ondula.
                vibra(carro, T.CASA_TURQUESA, 1.2f)
                drawPath(carro, if (noche) SolidColor(Color(0xFFE6DDF0)) else plano(T.CASA_BLANCA, 0.1f, 0.91f, 0.82f))
                clipPath(carro) {
                    val baja = Path().apply {
                        moveTo(p.x(0.05f), p.y(0.56f))
                        var x = 0.05f; while (x < 0.95f) { quadraticTo(p.x(x + 0.05f), p.y(0.53f), p.x(x + 0.1f), p.y(0.56f)); quadraticTo(p.x(x + 0.15f), p.y(0.59f), p.x(x + 0.2f), p.y(0.56f)); x += 0.2f }
                        lineTo(p.x(0.95f), p.y(0.8f)); lineTo(p.x(0.05f), p.y(0.8f)); close()
                    }
                    drawPath(baja, plano(T.CASA_TURQUESA, 0.1f, 0.91f, 0.82f))
                    // La ola de arcoíris pintada a brocha a lo largo del costado.
                    T.ARCOIRIS.forEachIndexed { i, col ->
                        val y = 0.37f + i * 0.022f
                        drawPath(blanda(false, 0.36f to y + 0.03f, 0.48f to y - 0.02f, 0.6f to y + 0.04f, 0.72f to y - 0.01f, 0.84f to y + 0.03f, 0.95f to y), fl(col), style = Stroke(p.y(0.02f), cap = StrokeCap.Round))
                    }
                    drawRect(Brush.horizontalGradient(listOf(Color.Transparent, tinta.copy(alpha = 0.16f)), p.x(0.8f), p.x(0.91f)), p.o(0.8f, 0.3f), Size(p.x(0.11f), p.y(0.4f)))
                }
                // La ventanilla de la cabina y el parabrisas.
                val vidrio = Path().apply { moveTo(p.x(0.255f), p.y(0.43f)); lineTo(p.x(0.315f), p.y(0.345f)); lineTo(p.x(0.39f), p.y(0.345f)); lineTo(p.x(0.39f), p.y(0.43f)); close() }
                drawPath(vidrio, if (noche) SolidColor(Color(0xFF2B2140)) else Brush.linearGradient(listOf(Color(0xFFCFE7F0), Color(0xFF7FA9C0)), p.o(0.26f, 0.35f), p.o(0.39f, 0.43f)))
                if (!noche) drawLine(Color.White.copy(alpha = 0.7f), p.o(0.29f, 0.42f), p.o(0.33f, 0.36f), lw * 0.7f, StrokeCap.Round)
                contorno(vidrio, 0.4f)
                drawLine(tinta.copy(alpha = 0.5f), p.o(0.41f, 0.34f), p.o(0.41f, 0.66f), lw * 0.35f)
                drawCircle(p.c(p.base), lw * 0.7f, p.o(0.38f, 0.48f))
                // Dos flores pintadas y el símbolo de la paz en la puerta de atrás.
                with(T) {
                    flor(p.o(0.5f, 0.6f), p.x(0.06f), fl(MAGENTA), fl(VERDE), 6, 0.6f, 0.2f)
                    flor(p.o(0.66f, 0.62f), p.x(0.045f), fl(AMARILLO), fl(VIOLETA), 5, 0.6f, 0.5f)
                    paz(p.o(0.82f, 0.6f), p.x(0.045f), if (noche) LIMON else Color.White, p.x(0.008f))
                }
                halo(p.o(0.5f, 0.6f), p.x(0.12f), T.MAGENTA, 0.3f)
                contorno(carro, 0.55f)
                // El faro y las defensas.
                val faro = p.o(0.115f, 0.5f)
                if (noche) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFF2B8).copy(alpha = 0.6f), Color.Transparent), faro, p.x(0.1f)), p.x(0.1f), faro)
                drawCircle(metalBulto(faro, p.x(0.03f)), p.x(0.03f), faro)
                drawCircle(Color(0xFFFFF4C8), p.x(0.017f), faro)
                caja(p, 0.07f, 0.655f, 0.2f, 0.685f)
                caja(p, 0.86f, 0.655f, 0.94f, 0.685f)
                // Los pasos de rueda y las ruedas, con sus tapacubos.
                ruedas.forEach { x ->
                    val o = Offset(x, yr)
                    drawCircle(lerp(fl(T.CASA_TURQUESA), tinta, 0.6f), rr * 1.2f, Offset(o.x, o.y + rr * 0.05f))
                    drawCircle(T.VINILO, rr, o)
                    drawCircle(Color.White.copy(alpha = 0.08f), rr * 0.8f, o, style = Stroke(lw * 0.4f))
                    drawCircle(metalBulto(o, rr * 0.55f), rr * 0.55f, o)
                    drawCircle(p.oscuro.copy(alpha = 0.6f), rr * 0.18f, o)
                    destello(Offset(o.x - rr * 0.2f, o.y - rr * 0.25f), rr * 0.3f)
                }
            }
            "Cuerpo" -> {
                // La cinta para la frente, tejida a mano en rombos de colores que vibran, colgada en su percha de madera: el aro
                // de la cinta alrededor de la percha (la mitad de atrás detrás del palo), el nudo con una flor y las dos tiras con
                // sus flecos y sus cuentas del metal del nivel. Arriba, el pomo de la percha, también de metal.
                val palo = rect(0.47f, 0.17f, 0.53f, 0.75f, 0.01f)
                val pie = Path().apply { addOval(p.r(0.3f, 0.73f, 0.7f, 0.775f)) }
                val aro = Rect(p.o(0.18f, 0.26f), p.o(0.82f, 0.42f))
                val tiras = blanda(true, 0.76f to 0.36f, 0.82f to 0.5f, 0.8f to 0.64f, 0.74f to 0.66f, 0.74f to 0.5f, 0.7f to 0.38f)
                if (!p.hecho) { silueta(palo, pie, Path().apply { addOval(aro) }, tiras); return@with }
                sombraPie(0.3f, 0.7f)
                drawPath(pie, planoV(Color(0xFF7A5536), 0.73f, 0.775f, 0.55f)); contorno(pie, 0.4f)
                val anchoCinta = p.y(0.045f)
                val colores = listOf(T.MAGENTA, T.NARANJA, T.AMARILLO, T.VERDE, T.AZUL, T.VIOLETA)
                fun arco(desde: Float, barrido: Float) = Path().apply { arcTo(aro, desde, barrido, true) }
                // La mitad de atrás, en sombra.
                drawPath(arco(180f, 180f), lerp(fl(T.MAGENTA), tinta, 0.45f), style = Stroke(anchoCinta, cap = StrokeCap.Round))
                // El palo de la percha y su pomo.
                drawPath(palo, plano(Color(0xFF8A5E3A), 0.47f, 0.53f, 0.6f)); contorno(palo, 0.4f, 0.6f)
                val pomo = p.o(0.5f, 0.155f); val rp = p.x(0.045f)
                drawCircle(tinta.copy(alpha = 0.25f), rp, pomo + Offset(lw, lw))
                drawCircle(metalBulto(pomo, rp), rp, pomo); destello(Offset(pomo.x - rp * 0.35f, pomo.y - rp * 0.35f), rp * 0.6f)
                // La mitad de adelante: el tejido de rombos que vibran, con su contorno.
                val frente = arco(0f, 180f)
                drawPath(frente, fl(T.MAGENTA), style = Stroke(anchoCinta, cap = StrokeCap.Round))
                drawPath(frente, fl(T.VERDE), style = Stroke(anchoCinta * 0.55f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(anchoCinta * 0.5f, anchoCinta * 0.5f))))
                drawPath(frente, fl(T.AMARILLO), style = Stroke(anchoCinta * 0.18f, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f, anchoCinta))))
                if (fino) {
                    drawPath(Path().apply { arcTo(Rect(aro.left - anchoCinta * 0.5f, aro.top - anchoCinta * 0.5f, aro.right + anchoCinta * 0.5f, aro.bottom + anchoCinta * 0.5f), 0f, 180f, true) },
                        tinta.copy(alpha = 0.7f), style = Stroke(lw * 0.45f))
                    drawPath(Path().apply { arcTo(Rect(aro.left + anchoCinta * 0.5f, aro.top + anchoCinta * 0.5f, aro.right - anchoCinta * 0.5f, aro.bottom - anchoCinta * 0.5f), 0f, 180f, true) },
                        tinta.copy(alpha = 0.7f), style = Stroke(lw * 0.45f))
                }
                halo(p.o(0.5f, 0.42f), p.x(0.3f), T.MAGENTA, 0.25f)
                // Las dos tiras que cuelgan del nudo, cada una de un color, con flecos y una cuenta de metal.
                listOf(Triple(0.775f, 0.66f, T.AZUL), Triple(0.735f, 0.62f, T.NARANJA)).forEachIndexed { i, (x, y, col) ->
                    val tira = blanda(false, 0.77f to 0.36f, x + 0.03f to 0.46f, x - 0.01f to 0.55f, x to y)
                    drawPath(tira, lerp(fl(col), tinta, 0.2f), style = Stroke(p.x(0.04f), cap = StrokeCap.Round))
                    drawPath(tira, fl(col), style = Stroke(p.x(0.026f), cap = StrokeCap.Round))
                    val cuenta = p.o(x, y + 0.012f)
                    drawCircle(metalBulto(cuenta, p.x(0.022f)), p.x(0.022f), cuenta)
                    for (k in -2..2) drawLine(fl(col), Offset(cuenta.x + k * p.x(0.008f), cuenta.y + p.x(0.02f)), Offset(cuenta.x + k * p.x(0.011f), cuenta.y + p.y(0.07f) - i * p.y(0.015f)), lw * 0.45f, StrokeCap.Round)
                }
                // El nudo con su flor.
                with(T) { flor(p.o(0.78f, 0.355f), p.x(0.075f), fl(AMARILLO), fl(VIOLETA), 6, 0.6f, 0.3f) }
                // Los rombos del tejido, en el centro de la cinta.
                if (fino) for (k in 0 until 7) {
                    val a = (PI * (0.15 + k * 0.12)).toFloat()
                    val o = Offset(aro.center.x + aro.width / 2 * cos(a), aro.center.y + aro.height / 2 * sin(a))
                    val s = anchoCinta * 0.34f
                    drawPath(Path().apply { moveTo(o.x, o.y - s); lineTo(o.x + s, o.y); lineTo(o.x, o.y + s); lineTo(o.x - s, o.y); close() }, fl(colores[k % colores.size]))
                }
            }
            "Mente" -> {
                // La lámpara de cera, con su globo redondo sobre el cuello y la base de metal del nivel (otra silueta, no la de
                // la lámpara de marca): el líquido ámbar, las gotas de cera magenta que suben y se juntan, la luz que viene de
                // abajo y el brillo del vidrio. De noche, el globo brilla en la sala oscura.
                val o = p.o(0.5f, 0.4f); val r = p.x(0.28f)
                val globo = disco(o, r)
                val cuello = rect(0.4f, 0.6f, 0.6f, 0.655f, 0.01f)
                val base = Path().apply {
                    moveTo(p.x(0.38f), p.y(0.655f)); lineTo(p.x(0.62f), p.y(0.655f))
                    quadraticTo(p.x(0.7f), p.y(0.7f), p.x(0.72f), p.y(0.765f)); lineTo(p.x(0.28f), p.y(0.765f))
                    quadraticTo(p.x(0.3f), p.y(0.7f), p.x(0.38f), p.y(0.655f)); close()
                }
                val tapa = rect(0.465f, 0.4f - 0.28f * p.w / p.h - 0.02f, 0.535f, 0.4f - 0.28f * p.w / p.h + 0.006f, 0.012f)
                if (!p.hecho) { silueta(globo, cuello, base, tapa); return@with }
                sombraPie(0.26f, 0.74f)
                if (noche) drawCircle(Brush.radialGradient(listOf(T.FLUOR_NARANJA.copy(alpha = 0.45f), T.FLUOR_MAGENTA.copy(alpha = 0.15f), Color.Transparent), o, r * 2f), r * 2f, o)
                drawPath(base, p.metal(0.28f, 0.72f)); contorno(base, 0.45f, 0.6f)
                drawLine(p.c(p.oscuro), p.o(0.33f, 0.72f), p.o(0.67f, 0.72f), lw * 0.5f)
                drawCircle(p.c(p.oscuro), lw * 1.1f, p.o(0.62f, 0.735f))
                drawPath(cuello, p.metal(0.4f, 0.6f))
                // El líquido: ámbar, más claro abajo, donde está la luz.
                val liquido = if (noche) listOf(Color(0xFFFFE07A), T.FLUOR_NARANJA, Color(0xFFB8306A)) else listOf(Color(0xFFFFE9A0), T.AMARILLO, T.NARANJA)
                drawPath(globo, Brush.radialGradient(liquido, Offset(o.x, o.y + r * 0.7f), r * 1.7f))
                clipPath(globo) {
                    // Las gotas de cera: una grande abajo, otra que sube estirada y una chica que ya se separó arriba.
                    val cera = fl(T.MAGENTA)
                    drawOval(cera, Offset(o.x - r * 1.1f, o.y + r * 0.62f), Size(r * 2.2f, r * 0.9f))
                    drawPath(T.gota(Offset(o.x - r * 0.05f, o.y + r * 0.62f), r * 0.3f, 0.1f, 0.1f), cera)
                    drawPath(Path().apply {
                        moveTo(o.x + r * 0.1f, o.y + r * 0.5f)
                        cubicTo(o.x + r * 0.36f, o.y + r * 0.4f, o.x + r * 0.4f, o.y + r * 0.1f, o.x + r * 0.32f, o.y - r * 0.05f)
                        cubicTo(o.x + r * 0.25f, o.y - r * 0.22f, o.x + r * 0.02f, o.y - r * 0.2f, o.x + r * 0.02f, o.y + r * 0.02f)
                        cubicTo(o.x + r * 0.02f, o.y + r * 0.22f, o.x + r * 0.12f, o.y + r * 0.32f, o.x + r * 0.1f, o.y + r * 0.5f); close()
                    }, cera)
                    drawPath(T.gota(Offset(o.x - r * 0.38f, o.y - r * 0.42f), r * 0.2f, 0.2f, 0.12f), cera)
                    drawPath(T.gota(Offset(o.x + r * 0.3f, o.y - r * 0.58f), r * 0.1f, 0.5f, 0.1f), cera)
                    drawPath(T.gota(Offset(o.x - r * 0.48f, o.y + r * 0.2f), r * 0.08f, 0.7f, 0.1f), cera)
                    // La luz de cada gota, arriba a la izquierda.
                    listOf(Triple(-0.38f, -0.42f, 0.2f), Triple(0.2f, -0.08f, 0.22f), Triple(-0.4f, 0.75f, 0.3f)).forEach { (dx, dy, rg) ->
                        drawCircle(lerp(cera, Color.White, 0.45f).copy(alpha = 0.6f), r * rg * 0.28f, Offset(o.x + r * (dx - rg * 0.35f), o.y + r * (dy - rg * 0.35f)))
                    }
                    // La sombra de la esfera a la derecha, con borde duro.
                    drawCircle(tinta.copy(alpha = if (noche) 0.1f else 0.16f), r, Offset(o.x + r * 1.15f, o.y + r * 0.2f))
                }
                // El brillo del vidrio: el arco de luz arriba a la izquierda y un punto.
                drawArc(Color.White.copy(alpha = 0.55f), 200f, 60f, false, Offset(o.x - r * 0.8f, o.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(lw * 1.1f, cap = StrokeCap.Round))
                drawCircle(Color.White.copy(alpha = 0.7f), r * 0.06f, Offset(o.x - r * 0.3f, o.y - r * 0.74f))
                contorno(globo, 0.6f)
                drawPath(tapa, p.metal(0.465f, 0.535f))
                destello(p.o(0.36f, 0.67f), p.x(0.03f))
            }
            "Cultura" -> {
                // La guitarra eléctrica pintada a mano (de forma propia, no la de ningún modelo), de pie en su atril: el cuerpo
                // de dos cuernos redondos en magenta con un remolino verde y amarillo, el golpeador crema, las dos pastillas, el
                // puente, las perillas y las clavijas del metal del nivel, y las seis cuerdas.
                val cuerpo = Path().apply {
                    moveTo(p.x(0.5f), p.y(0.745f))
                    cubicTo(p.x(0.26f), p.y(0.75f), p.x(0.16f), p.y(0.67f), p.x(0.2f), p.y(0.58f))
                    cubicTo(p.x(0.23f), p.y(0.51f), p.x(0.3f), p.y(0.52f), p.x(0.28f), p.y(0.46f))
                    cubicTo(p.x(0.27f), p.y(0.41f), p.x(0.3f), p.y(0.37f), p.x(0.36f), p.y(0.39f))
                    cubicTo(p.x(0.41f), p.y(0.41f), p.x(0.42f), p.y(0.44f), p.x(0.5f), p.y(0.44f))
                    cubicTo(p.x(0.58f), p.y(0.44f), p.x(0.6f), p.y(0.39f), p.x(0.66f), p.y(0.36f))
                    cubicTo(p.x(0.72f), p.y(0.34f), p.x(0.76f), p.y(0.4f), p.x(0.72f), p.y(0.46f))
                    cubicTo(p.x(0.7f), p.y(0.51f), p.x(0.79f), p.y(0.52f), p.x(0.8f), p.y(0.6f))
                    cubicTo(p.x(0.82f), p.y(0.69f), p.x(0.72f), p.y(0.75f), p.x(0.5f), p.y(0.745f)); close()
                }
                val mastil = rect(0.465f, 0.11f, 0.535f, 0.47f)
                val pala = Path().apply {
                    moveTo(p.x(0.465f), p.y(0.115f)); quadraticTo(p.x(0.4f), p.y(0.08f), p.x(0.43f), p.y(0.03f))
                    quadraticTo(p.x(0.5f), p.y(0.0f), p.x(0.57f), p.y(0.03f)); quadraticTo(p.x(0.6f), p.y(0.08f), p.x(0.535f), p.y(0.115f)); close()
                }
                if (!p.hecho) { silueta(cuerpo, mastil, pala); return@with }
                sombraPie(0.28f, 0.72f)
                // El atril: las dos patas y la horquilla que sostiene el mástil.
                val atril = Color(0xFF3A2E3E)
                drawLine(p.c(atril), p.o(0.3f, 0.765f), p.o(0.44f, 0.68f), lw * 1.2f, StrokeCap.Round)
                drawLine(p.c(atril), p.o(0.7f, 0.765f), p.o(0.56f, 0.68f), lw * 1.2f, StrokeCap.Round)
                drawLine(p.c(atril), p.o(0.5f, 0.7f), p.o(0.5f, 0.3f), lw * 0.9f, StrokeCap.Round)
                // El mástil y la pala, con sus trastes y sus clavijas.
                drawPath(mastil, plano(Color(0xFF6E4A2E), 0.465f, 0.535f, 0.6f))
                if (fino) for (k in 0 until 9) { val y = 0.13f + k * 0.035f; drawLine(p.c(p.brillo), p.o(0.468f, y), p.o(0.532f, y), lw * 0.25f) }
                listOf(0.2f, 0.27f, 0.34f).forEach { y -> drawCircle(Color(0xFFF2EAD8), lw * 0.45f, p.o(0.5f, y)) }
                contorno(mastil, 0.4f, 0.7f)
                vibra(pala, T.MAGENTA, 0.6f)
                drawPath(pala, plano(T.MAGENTA, 0.42f, 0.58f)); contorno(pala, 0.45f)
                listOf(-1f, 1f).forEach { s -> for (k in 0 until 3) {
                    val q = p.o(0.5f + s * 0.085f, 0.035f + k * 0.03f); val rq = p.x(0.018f)
                    drawLine(p.c(p.oscuro), p.o(0.5f + s * 0.05f, 0.035f + k * 0.03f), q, lw * 0.5f)
                    drawCircle(metalBulto(q, rq), rq, q)
                } }
                // El cuerpo: magenta con el remolino pintado.
                vibra(cuerpo, T.MAGENTA, 1.3f)
                halo(p.o(0.5f, 0.58f), p.x(0.45f), T.MAGENTA, 0.3f)
                drawPath(cuerpo, plano(T.MAGENTA, 0.18f, 0.82f, 0.74f))
                clipPath(cuerpo) {
                    val remolino = Path().apply {
                        val c0 = p.o(0.36f, 0.62f)
                        for (k in 0..80) {
                            val t = k / 80f; val a = t * 4.4f * PI.toFloat(); val rr = p.x(0.02f + 0.16f * t)
                            val q = Offset(c0.x + rr * cos(a), c0.y + rr * sin(a) * 0.9f)
                            if (k == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
                        }
                    }
                    drawPath(remolino, fl(T.VERDE), style = Stroke(p.x(0.03f), cap = StrokeCap.Round))
                    drawPath(remolino, fl(T.AMARILLO), style = Stroke(p.x(0.01f), cap = StrokeCap.Round))
                }
                // El golpeador crema y las pastillas.
                val golpeador = blanda(true, 0.53f to 0.5f, 0.68f to 0.5f, 0.74f to 0.6f, 0.68f to 0.68f, 0.56f to 0.66f)
                drawPath(golpeador, if (noche) Color(0xFFE6DDF0) else Color(0xFFF4EAD2)); contorno(golpeador, 0.35f, 0.5f)
                listOf(0.49f, 0.565f).forEach { y ->
                    val pa = rect(0.435f, y, 0.565f, y + 0.035f, 0.008f)
                    drawPath(pa, p.metal(0.435f, 0.565f)); contorno(pa, 0.3f, 0.6f)
                    for (k in 0 until 6) drawCircle(p.c(p.oscuro), lw * 0.28f, p.o(0.452f + k * 0.019f, y + 0.0175f))
                }
                caja(p, 0.43f, 0.64f, 0.57f, 0.66f)
                listOf(0.63f to 0.63f, 0.67f to 0.66f, 0.62f to 0.69f).forEach { (x, y) -> val q = p.o(x, y); drawCircle(metalBulto(q, p.x(0.018f)), p.x(0.018f), q) }
                // Las seis cuerdas, del puente a la pala.
                for (k in 0 until 6) { val x = 0.477f + k * 0.0092f; drawLine(p.c(lerp(p.brillo, Color.White, 0.3f)).copy(alpha = 0.85f), p.o(x, 0.115f), p.o(x, 0.65f), lw * 0.18f) }
                contorno(cuerpo, 0.6f)
                // La horquilla de goma del atril, delante del cuerpo.
                drawLine(p.c(atril), p.o(0.38f, 0.745f), p.o(0.62f, 0.745f), lw * 1.3f, StrokeCap.Round)
                brillo(p.o(0.3f, 0.55f), p.x(0.025f), 0.35f)
                destello(p.o(0.44f, 0.5f), p.x(0.025f))
            }
            "Gente" -> {
                // El chaleco de ante con flecos, colgado de su gancho en el perchero, con los botones de solapa: el símbolo de
                // la paz, la flor que vibra, el corazón, los aros de colores y el arcoíris. El pomo y el gancho, del metal del nivel.
                val palo = rect(0.475f, 0.06f, 0.525f, 0.76f, 0.01f)
                val chaleco = Path().apply {
                    moveTo(p.x(0.42f), p.y(0.155f)); lineTo(p.x(0.5f), p.y(0.44f)); lineTo(p.x(0.58f), p.y(0.155f))
                    lineTo(p.x(0.69f), p.y(0.185f)); quadraticTo(p.x(0.67f), p.y(0.34f), p.x(0.8f), p.y(0.37f))
                    lineTo(p.x(0.82f), p.y(0.62f)); quadraticTo(p.x(0.68f), p.y(0.645f), p.x(0.52f), p.y(0.62f)); lineTo(p.x(0.5f), p.y(0.56f))
                    lineTo(p.x(0.48f), p.y(0.62f)); quadraticTo(p.x(0.32f), p.y(0.645f), p.x(0.18f), p.y(0.62f)); lineTo(p.x(0.2f), p.y(0.37f))
                    quadraticTo(p.x(0.33f), p.y(0.34f), p.x(0.31f), p.y(0.185f)); close()
                }
                val flecos = rect(0.18f, 0.6f, 0.82f, 0.71f)
                if (!p.hecho) { silueta(palo, chaleco, flecos); return@with }
                sombraPie(0.36f, 0.64f)
                // El perchero y su pie de tres patas.
                val madera = Color(0xFF7A5536)
                drawPath(palo, plano(madera, 0.475f, 0.525f, 0.6f))
                drawLine(p.c(lerp(madera, tinta, 0.3f)), p.o(0.5f, 0.74f), p.o(0.38f, 0.765f), lw * 1.1f, StrokeCap.Round)
                drawLine(p.c(lerp(madera, tinta, 0.3f)), p.o(0.5f, 0.74f), p.o(0.62f, 0.765f), lw * 1.1f, StrokeCap.Round)
                val pomo = p.o(0.5f, 0.055f); drawCircle(metalBulto(pomo, p.x(0.035f)), p.x(0.035f), pomo)
                // El gancho y la percha.
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.09f)); quadraticTo(p.x(0.56f), p.y(0.1f), p.x(0.55f), p.y(0.13f)); quadraticTo(p.x(0.54f), p.y(0.15f), p.x(0.5f), p.y(0.15f)) },
                    p.c(p.base), style = Stroke(lw * 0.8f, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(p.x(0.28f), p.y(0.2f)); lineTo(p.x(0.5f), p.y(0.15f)); lineTo(p.x(0.72f), p.y(0.2f)) }, p.c(lerp(madera, Color.White, 0.2f)), style = Stroke(lw * 1.4f, cap = StrokeCap.Round))
                // Los flecos de abajo.
                val ante = Color(0xFFB9824A)
                for (k in 0 until 26) {
                    val x = 0.19f + k * 0.0245f
                    if (x in 0.475f..0.525f) continue
                    val largo = 0.07f + 0.02f * ((k * 7) % 3) / 2f
                    drawLine(p.c(lerp(fl(ante), tinta, if (k % 2 == 0) 0.1f else 0.25f)), p.o(x, 0.62f), p.o(x + 0.004f, 0.62f + largo), lw * 0.7f, StrokeCap.Round)
                }
                // El chaleco: el forro oscuro en el cuello y el ante con su sombra a la derecha.
                drawPath(Path().apply { moveTo(p.x(0.42f), p.y(0.16f)); lineTo(p.x(0.5f), p.y(0.44f)); lineTo(p.x(0.58f), p.y(0.16f)); close() }, lerp(fl(T.VIOLETA), tinta, 0.4f))
                drawPath(chaleco, plano(ante, 0.16f, 0.84f, 0.76f))
                // El bordado de flores en el bajo y las solapas que se doblan.
                if (fino) listOf(0.26f, 0.36f, 0.64f, 0.74f).forEachIndexed { i, x -> with(T) { flor(p.o(x, 0.585f), p.x(0.03f), fl(if (i % 2 == 0) NARANJA else VERDE), fl(if (i % 2 == 0) AZUL else MAGENTA), 5) } }
                listOf(-1f, 1f).forEach { s ->
                    drawPath(Path().apply { moveTo(p.x(0.5f + s * 0.08f), p.y(0.16f)); lineTo(p.x(0.5f + s * 0.12f), p.y(0.26f)); lineTo(p.x(0.5f + s * 0.02f), p.y(0.38f)); close() },
                        lerp(fl(ante), tinta, 0.18f))
                }
                contorno(chaleco, 0.55f)
                // Los botones de solapa: cada uno con su borde, su brillo y su sombra.
                fun boton(x: Float, y: Float, fondo: Color, dibujo: (Offset, Float) -> Unit) {
                    val o = p.o(x, y); val rb = p.x(0.065f)
                    drawCircle(tinta.copy(alpha = 0.3f), rb, o + Offset(lw * 0.8f, lw * 0.9f))
                    halo(o, rb * 1.8f, fondo, 0.3f)
                    drawCircle(fl(fondo), rb, o)
                    dibujo(o, rb)
                    drawArc(Color.White.copy(alpha = 0.45f), 200f, 70f, false, Offset(o.x - rb * 0.8f, o.y - rb * 0.8f), Size(rb * 1.6f, rb * 1.6f), style = Stroke(lw * 0.5f, cap = StrokeCap.Round))
                    drawCircle(tinta.copy(alpha = 0.7f), rb, o, style = Stroke(lw * 0.35f))
                }
                boton(0.39f, 0.3f, T.AZUL) { o, rb -> with(T) { paz(o, rb * 0.62f, if (noche) LIMON else Color.White, rb * 0.13f) } }
                boton(0.3f, 0.47f, T.VERDE) { o, rb -> with(T) { flor(o, rb * 0.8f, fl(MAGENTA), fl(VERDE), 6, 0.6f) } }
                boton(0.62f, 0.27f, T.AMARILLO) { o, rb ->
                    val k = rb * 0.5f
                    drawPath(Path().apply { moveTo(o.x, o.y + k); cubicTo(o.x - k * 1.6f, o.y - k * 0.2f, o.x - k * 0.7f, o.y - k * 1.3f, o.x, o.y - k * 0.45f); cubicTo(o.x + k * 0.7f, o.y - k * 1.3f, o.x + k * 1.6f, o.y - k * 0.2f, o.x, o.y + k); close() }, fl(T.MAGENTA))
                }
                boton(0.71f, 0.46f, T.NARANJA) { o, rb -> listOf(0.72f to T.AZUL, 0.48f to T.NARANJA, 0.26f to T.AZUL).forEach { (f, col) -> drawCircle(fl(col), rb * f, o) } }
                boton(0.6f, 0.55f, T.VIOLETA) { o, rb ->
                    listOf(T.MAGENTA, T.NARANJA, T.AMARILLO, T.VERDE).forEachIndexed { i, col ->
                        val ra = rb * (0.7f - i * 0.13f)
                        drawArc(fl(col), 180f, 180f, false, Offset(o.x - ra, o.y + rb * 0.2f - ra), Size(ra * 2, ra * 2), style = Stroke(rb * 0.12f))
                    }
                }
            }
            "Constancia" -> {
                // El disco de larga duración, del metal del nivel, de pie en su soporte de madera delante de su funda (propia:
                // un sol de arcoíris que sale sobre violeta, con el título en letras infladas): los surcos, los dos brillos que
                // giran y la etiqueta magenta con la flor.
                val o = p.o(0.6f, 0.47f); val r = p.x(0.3f)
                val funda = Path().apply { addRect(p.r(0.1f, 0.2f, 0.62f, 0.62f)) }
                val soporte = rect(0.36f, 0.7f, 0.84f, 0.765f, 0.012f)
                if (!p.hecho) { silueta(funda, disco(o, r), soporte); return@with }
                sombraPie(0.1f, 0.88f)
                rotate(-7f, p.o(0.36f, 0.42f)) {
                    translate(lw * 1.4f, lw * 1.4f) { drawPath(funda, tinta.copy(alpha = 0.25f)) }
                    drawPath(funda, fl(T.VIOLETA))
                    clipPath(funda) {
                        val sol = p.o(0.36f, 0.62f)
                        T.ARCOIRIS.reversed().forEachIndexed { i, col -> drawCircle(fl(col), p.x(0.34f - i * 0.045f), sol) }
                        drawCircle(fl(T.AMARILLO), p.x(0.07f), sol)
                        val titulo = rect(0.15f, 0.24f, 0.57f, 0.31f, 0.035f)
                        vibra(titulo, T.NARANJA, 0.6f)
                        drawPath(titulo, fl(T.NARANJA))
                        for (k in 0 until 4) drawOval(fl(T.VIOLETA), p.o(0.18f + k * 0.095f, 0.258f), Size(p.x(0.05f), p.y(0.026f)))
                        drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent), p.x(0.1f), p.x(0.3f)), p.o(0.1f, 0.2f), Size(p.x(0.2f), p.y(0.42f)))
                    }
                    contorno(funda, 0.5f)
                }
                // El disco: el metal con sus surcos y sus dos brillos.
                drawCircle(tinta.copy(alpha = 0.3f), r, o + Offset(lw * 1.5f, lw * 1.2f))
                halo(o, r * 1.5f, T.MAGENTA, 0.25f)
                drawCircle(metalBulto(o, r), r, o)
                var k = 0.94f
                while (k > 0.42f) { drawCircle(p.oscuro.copy(alpha = 0.32f), r * k, o, style = Stroke(lw * 0.22f)); k -= if (fino) 0.035f else 0.08f }
                listOf(-35f, 145f).forEach { a -> drawArc(Color.White.copy(alpha = 0.3f), a, 30f, true, Offset(o.x - r * 0.94f, o.y - r * 0.94f), Size(r * 1.88f, r * 1.88f)) }
                drawCircle(p.oscuro, r * 0.4f, o, style = Stroke(lw * 0.5f))
                drawCircle(fl(T.MAGENTA), r * 0.36f, o)
                with(T) { flor(o, r * 0.24f, fl(AMARILLO), fl(VIOLETA), 6, 0.5f, 0.2f) }
                drawCircle(tinta, r * 0.04f, o)
                drawCircle(p.oscuro, r, o, style = Stroke(lw * 0.5f))
                destello(Offset(o.x - r * 0.55f, o.y - r * 0.5f), r * 0.14f)
                // El soporte de madera con su ranura.
                drawPath(soporte, planoV(Color(0xFF8A5E3A), 0.7f, 0.765f, 0.55f)); contorno(soporte, 0.4f, 0.7f)
                drawLine(tinta.copy(alpha = 0.6f), p.o(0.42f, 0.712f), p.o(0.78f, 0.712f), lw * 0.6f, StrokeCap.Round)
            }
            "Curiosos" -> {
                // La campanilla de templo, del metal del nivel, colgada de su cordón azafrán en un gancho de madera adornado con
                // una guirnalda de flores naranjas; su badajo con la borla y el sonido que se abre en ondas de dos colores.
                val poste = rect(0.24f, 0.13f, 0.3f, 0.765f, 0.01f)
                val brazo = Path().apply {
                    moveTo(p.x(0.24f), p.y(0.17f)); quadraticTo(p.x(0.27f), p.y(0.07f), p.x(0.46f), p.y(0.08f)); lineTo(p.x(0.66f), p.y(0.08f))
                    quadraticTo(p.x(0.74f), p.y(0.085f), p.x(0.72f), p.y(0.13f)); quadraticTo(p.x(0.69f), p.y(0.15f), p.x(0.67f), p.y(0.12f))
                    lineTo(p.x(0.46f), p.y(0.12f)); quadraticTo(p.x(0.31f), p.y(0.12f), p.x(0.3f), p.y(0.19f)); close()
                }
                val cx = 0.6f
                val campana = Path().apply {
                    moveTo(p.x(cx - 0.05f), p.y(0.26f))
                    cubicTo(p.x(cx - 0.12f), p.y(0.27f), p.x(cx - 0.13f), p.y(0.42f), p.x(cx - 0.15f), p.y(0.5f))
                    quadraticTo(p.x(cx - 0.2f), p.y(0.53f), p.x(cx - 0.2f), p.y(0.55f))
                    lineTo(p.x(cx + 0.2f), p.y(0.55f)); quadraticTo(p.x(cx + 0.2f), p.y(0.53f), p.x(cx + 0.15f), p.y(0.5f))
                    cubicTo(p.x(cx + 0.13f), p.y(0.42f), p.x(cx + 0.12f), p.y(0.27f), p.x(cx + 0.05f), p.y(0.26f)); close()
                }
                val borla = blanda(true, cx - 0.02f to 0.61f, cx + 0.02f to 0.61f, cx + 0.035f to 0.72f, cx - 0.035f to 0.72f)
                if (!p.hecho) { silueta(poste, brazo, campana, borla); return@with }
                sombraPie(0.18f, 0.36f)
                val madera = Color(0xFF6B4A2F)
                drawPath(poste, plano(madera, 0.24f, 0.3f, 0.6f)); contorno(poste, 0.4f, 0.6f)
                drawPath(Path().apply { addOval(p.r(0.18f, 0.745f, 0.36f, 0.775f)) }, planoV(madera, 0.745f, 0.775f, 0.5f))
                drawPath(brazo, planoV(madera, 0.07f, 0.19f, 0.6f)); contorno(brazo, 0.4f, 0.6f)
                // La guirnalda de flores naranjas enrollada en el brazo y el poste.
                if (fino) {
                    for (k in 0 until 9) {
                        val t = k / 8f
                        val q = if (t < 0.5f) p.o(0.27f, 0.62f - t * 0.9f) else p.o(0.3f + (t - 0.5f) * 0.6f, 0.1f)
                        drawCircle(fl(T.NARANJA), p.x(0.022f), q)
                        drawCircle(lerp(fl(T.NARANJA), T.AMARILLO, 0.5f), p.x(0.009f), q + Offset(-lw * 0.3f, -lw * 0.3f))
                    }
                }
                // Las ondas del sonido, a los dos lados, en magenta y verde.
                if (p.hecho) listOf(-1f, 1f).forEach { s ->
                    for (k in 0 until 3) {
                        val ra = p.x(0.24f + k * 0.07f); val c = p.o(cx, 0.44f)
                        drawArc(fl(if (k % 2 == 0) T.MAGENTA else T.VERDE).copy(alpha = 0.85f - k * 0.2f), if (s < 0) 160f else -20f, 40f, false, Offset(c.x - ra, c.y - ra), Size(ra * 2, ra * 2),
                            style = Stroke(lw * 0.9f, cap = StrokeCap.Round))
                    }
                }
                // El cordón azafrán, con su nudo.
                val azafran = Color(0xFFE8912A)
                drawLine(p.c(fl(azafran)), p.o(cx, 0.12f), p.o(cx, 0.24f), lw * 0.9f, StrokeCap.Round)
                drawCircle(p.c(lerp(fl(azafran), tinta, 0.2f)), lw * 1.3f, p.o(cx, 0.175f))
                val anilla = p.o(cx, 0.245f)
                drawCircle(p.c(p.base), p.x(0.025f), anilla, style = Stroke(lw * 0.7f))
                // La campanilla: el metal con sus dos aros y el labio.
                drawPath(campana, p.metal(cx - 0.2f, cx + 0.2f))
                clipPath(campana) {
                    drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.oscuro.copy(alpha = 0.35f)), p.y(0.45f), p.y(0.55f)), p.o(cx - 0.2f, 0.45f), Size(p.x(0.4f), p.y(0.1f)))
                    drawLine(p.oscuro.copy(alpha = 0.6f), p.o(cx - 0.2f, 0.34f), p.o(cx + 0.2f, 0.34f), lw * 0.5f)
                    drawLine(p.oscuro.copy(alpha = 0.6f), p.o(cx - 0.2f, 0.48f), p.o(cx + 0.2f, 0.48f), lw * 0.5f)
                    drawLine(lerp(p.brillo, Color.White, 0.4f).copy(alpha = 0.7f), p.o(cx - 0.2f, 0.355f), p.o(cx + 0.2f, 0.355f), lw * 0.3f)
                }
                drawPath(campana, p.oscuro, style = Stroke(lw * 0.45f))
                destello(p.o(cx - 0.06f, 0.32f), p.x(0.035f))
                // El badajo y la borla azafrán.
                drawLine(p.c(p.oscuro), p.o(cx, 0.55f), p.o(cx, 0.585f), lw * 0.6f)
                val bola = p.o(cx, 0.59f); drawCircle(metalBulto(bola, p.x(0.028f)), p.x(0.028f), bola)
                drawPath(borla, fl(azafran))
                if (fino) for (k in -2..2) drawLine(lerp(fl(azafran), tinta, 0.3f), p.o(cx + k * 0.012f, 0.64f), p.o(cx + k * 0.014f, 0.715f), lw * 0.3f)
                drawRect(p.c(lerp(fl(azafran), tinta, 0.25f)), p.o(cx - 0.022f, 0.615f), Size(p.x(0.044f), p.y(0.012f)))
                contorno(borla, 0.35f, 0.6f)
            }
            else -> {
                // La flor que se regala: una flor grande de pétalos redondos que vibra (magenta sobre verde) con un capullo, en
                // una botella de vidrio con agua, atada con una cinta del metal del nivel; un pétalo ya cayó junto a la botella.
                val botella = Path().apply {
                    moveTo(p.x(0.44f), p.y(0.45f)); lineTo(p.x(0.56f), p.y(0.45f)); lineTo(p.x(0.56f), p.y(0.5f))
                    quadraticTo(p.x(0.64f), p.y(0.54f), p.x(0.64f), p.y(0.6f)); lineTo(p.x(0.64f), p.y(0.75f))
                    quadraticTo(p.x(0.64f), p.y(0.765f), p.x(0.62f), p.y(0.765f)); lineTo(p.x(0.38f), p.y(0.765f))
                    quadraticTo(p.x(0.36f), p.y(0.765f), p.x(0.36f), p.y(0.75f)); lineTo(p.x(0.36f), p.y(0.6f))
                    quadraticTo(p.x(0.36f), p.y(0.54f), p.x(0.44f), p.y(0.5f)); close()
                }
                val o = p.o(0.5f, 0.2f); val r = p.x(0.25f)
                val corola = disco(o, r * 0.95f)
                val tallo = blanda(false, 0.5f to 0.72f, 0.49f to 0.55f, 0.53f to 0.4f, 0.5f to 0.22f)
                if (!p.hecho) { silueta(botella, corola, Path().apply { addOval(p.r(0.22f, 0.4f, 0.38f, 0.48f)) }, Path().apply { addRect(p.r(0.48f, 0.26f, 0.52f, 0.5f)) }); return@with }
                sombraPie(0.32f, 0.7f)
                halo(o, r * 1.8f, T.MAGENTA, 0.35f)
                // El tallo, las hojas y el capullo.
                drawPath(tallo, lerp(fl(T.VERDE), tinta, 0.25f), style = Stroke(lw * 1.4f, cap = StrokeCap.Round))
                val hoja1 = blanda(true, 0.5f to 0.47f, 0.4f to 0.4f, 0.26f to 0.43f, 0.36f to 0.49f)
                val hoja2 = blanda(true, 0.51f to 0.41f, 0.62f to 0.33f, 0.76f to 0.36f, 0.64f to 0.42f)
                listOf(hoja1, hoja2).forEach { h -> vibra(h, T.VERDE, 0.7f); drawPath(h, plano(T.VERDE, 0.26f, 0.76f, 0.6f)); contorno(h, 0.4f) }
                drawPath(blanda(false, 0.49f to 0.46f, 0.4f to 0.44f, 0.3f to 0.44f), tinta.copy(alpha = 0.35f), style = Stroke(lw * 0.35f, cap = StrokeCap.Round))
                drawPath(blanda(false, 0.52f to 0.4f, 0.62f to 0.37f, 0.72f to 0.36f), tinta.copy(alpha = 0.35f), style = Stroke(lw * 0.35f, cap = StrokeCap.Round))
                drawLine(lerp(fl(T.VERDE), tinta, 0.25f), p.o(0.52f, 0.38f), p.o(0.68f, 0.24f), lw * 0.9f, StrokeCap.Round)
                val capullo = blanda(true, 0.68f to 0.17f, 0.73f to 0.22f, 0.69f to 0.26f, 0.65f to 0.22f)
                vibra(capullo, T.NARANJA, 0.6f); drawPath(capullo, fl(T.NARANJA)); contorno(capullo, 0.35f)
                // La flor grande.
                with(T) { flor(o, r, fl(MAGENTA), fl(VERDE), 7, 1f, 0.15f) }
                with(T) { flor(o, r * 0.3f, fl(AMARILLO), fl(VIOLETA), 8, 0.4f, 0.4f) }
                // La botella de vidrio con el agua.
                drawPath(botella, (if (noche) Color(0xFFB8E8FF) else Color(0xFFD7EEF2)).copy(alpha = 0.45f))
                clipPath(botella) {
                    drawRect(Color(0xFF7FC4D8).copy(alpha = if (noche) 0.4f else 0.45f), p.o(0.3f, 0.6f), Size(p.x(0.4f), p.y(0.2f)))
                    drawLine(Color.White.copy(alpha = 0.6f), p.o(0.36f, 0.6f), p.o(0.64f, 0.6f), lw * 0.4f)
                    drawLine(lerp(fl(T.VERDE), tinta, 0.35f), p.o(0.5f, 0.45f), p.o(0.5f, 0.76f), lw * 1.1f)
                }
                drawLine(Color.White.copy(alpha = 0.7f), p.o(0.4f, 0.58f), p.o(0.4f, 0.74f), lw * 0.9f, StrokeCap.Round)
                contorno(botella, 0.45f, 0.75f)
                // La cinta atada al cuello: el moño y las dos colas.
                caja(p, 0.43f, 0.49f, 0.57f, 0.515f)
                listOf(-1f, 1f).forEach { s ->
                    val lazo = Path().apply { moveTo(p.x(0.5f), p.y(0.502f)); quadraticTo(p.x(0.5f + s * 0.11f), p.y(0.45f), p.x(0.5f + s * 0.1f), p.y(0.51f)); close() }
                    drawPath(lazo, p.metal(0.38f, 0.62f))
                    drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.505f)); lineTo(p.x(0.5f + s * 0.06f), p.y(0.6f)); lineTo(p.x(0.5f + s * 0.035f), p.y(0.59f)); close() }, p.metal(0.42f, 0.58f))
                }
                drawCircle(metalBulto(p.o(0.5f, 0.503f), p.x(0.018f)), p.x(0.018f), p.o(0.5f, 0.503f))
                destello(p.o(0.44f, 0.49f), p.x(0.025f))
                // El pétalo que cayó.
                val petalo = Path().apply { addOval(p.r(0.7f, 0.742f, 0.79f, 0.765f)) }
                drawPath(petalo, fl(T.MAGENTA)); contorno(petalo, 0.3f, 0.6f)
            }
        }
    }

    /**
     * Art nouveau: las piezas de una vitrina de la Exposición de 1900, cada una sobre su peana de caoba (la dibuja
     * `LogrosNouveau`): el cartel litográfico, la entrada del metro de Guimard, la joya de libélula en su estuche, la lámpara de
     * vidrio de colores, el jarrón de vidrio de Nancy, el abanico de seda pintada, el pavo real de esmalte, el frasco de perfume
     * y el peine de carey. Dibujadas a la manera del cartel de 1900: colores planos con una luz suave de arriba a la izquierda y
     * el contorno de pluma; el metal del nivel va en los marcos, los pies y las monturas.
     */
    internal fun DrawScope.nouveau(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val fino = p.h > 70f * density
        val pluma = TrazosNouveau.PLUMA
        /** La sombra con color: el violeta de la tinta de la prensa, no el negro. */
        val umbra = Color(0xFF2A2234)
        /** Cuánto brilla el metal: más en cada nivel. */
        val lustre = 0.24f + 0.08f * p.nivel
        fun c(col: Color) = p.c(col)
        /** Volumen de lado: la luz entra por la izquierda. */
        fun vol(col: Color, x0: Float, x1: Float): Brush = if (p.hecho) Brush.horizontalGradient(listOf(lerp(col, Color.White, 0.24f), col, col, lerp(col, umbra, 0.45f)), p.x(x0), p.x(x1)) else SolidColor(p.sombra)
        /** Volumen redondo: la luz arriba a la izquierda del centro [o]. */
        fun bulto(col: Color, o: Offset, r: Float): Brush = if (p.hecho) Brush.radialGradient(listOf(lerp(col, Color.White, 0.32f), col, lerp(col, umbra, 0.5f)), Offset(o.x - r * 0.35f, o.y - r * 0.4f), r * 1.5f) else SolidColor(p.sombra)
        /** El metal del nivel en volumen redondo. */
        fun metalBulto(o: Offset, r: Float): Brush = if (p.hecho) Brush.radialGradient(listOf(lerp(p.brillo, Color.White, 0.25f), p.brillo, p.base, p.oscuro), Offset(o.x - r * 0.35f, o.y - r * 0.4f), r * 1.6f) else SolidColor(p.sombra)
        /** El contorno de pluma del cartel, fino y en la tinta de la prensa. */
        fun contorno(forma: Path, alfa: Float = 0.62f, g: Float = 0.42f) { if (p.hecho) drawPath(forma, pluma.copy(alpha = alfa), style = Stroke(lw * g, join = StrokeJoin.Round, cap = StrokeCap.Round)) }
        /** La sombra de [forma] echada abajo a la derecha, en el violeta de la tinta. */
        fun sombraDe(forma: Path, dx: Float = 1.2f, dy: Float = 1.2f, alfa: Float = 0.22f) { if (p.hecho) translate(lw * dx, lw * dy) { drawPath(forma, umbra.copy(alpha = alfa)) } }
        /** Un brillo en la superficie: un trazo blanco y suave, solo si ya se logró. */
        fun brillo(a: Offset, b: Offset, ancho: Float, alfa: Float = 0.35f) { if (p.hecho) drawLine(Color.White.copy(alpha = alfa), a, b, ancho, StrokeCap.Round) }
        /** El destello del metal pulido en [o]: un punto de luz con su cruz fina, más vivo en cada nivel. */
        fun destello(o: Offset, r: Float) {
            if (!p.hecho || p.nivel < 2) return
            val t = r * (0.55f + 0.2f * p.nivel); val a = 0.35f + 0.15f * p.nivel
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = a * 0.7f), Color.Transparent), o, t * 0.7f), t * 0.7f, o)
            drawLine(Color.White.copy(alpha = a), Offset(o.x - t, o.y), Offset(o.x + t, o.y), lw * 0.3f, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = a * 0.8f), Offset(o.x, o.y - t * 0.7f), Offset(o.x, o.y + t * 0.7f), lw * 0.3f, StrokeCap.Round)
        }
        /** Donde la pieza toca la peana: la sombra de contacto, oscura y corta, y la que se alarga a la derecha. */
        fun sombraPie(x0: Float, x1: Float, y: Float = 0.765f) {
            if (!p.hecho) return
            drawOval(umbra.copy(alpha = 0.26f), Offset(p.x(x0 + 0.03f), p.y(y) - p.y(0.009f)), Size(p.x(x1 - x0 + 0.05f), p.y(0.018f)))
            val o = p.o((x0 + x1) / 2 + 0.02f, y); val r = p.x((x1 - x0) / 2 + 0.06f)
            drawOval(Brush.radialGradient(listOf(umbra.copy(alpha = 0.3f), umbra.copy(alpha = 0f)), o, r), Offset(o.x - r, o.y - p.y(0.007f)), Size(r * 2, p.y(0.014f)))
        }
        /** Lo que aún no se gana: la silueta entera de una vez, para que no se encimen las transparencias. */
        fun silueta(vararg formas: Path) { val todo = Path(); formas.forEach { todo.addPath(it) }; drawPath(todo, p.sombra) }
        /** Un perfil de revolución alrededor de [cx]: los pares (alto, medio ancho) de arriba abajo, unidos con curvas suaves. */
        fun perfil(cx: Float, pts: List<Pair<Float, Float>>): Path = Path().apply {
            fun suave(l: List<Offset>, primero: Boolean) {
                if (primero) moveTo(l[0].x, l[0].y) else lineTo(l[0].x, l[0].y)
                for (i in 1 until l.size - 1) quadraticTo(l[i].x, l[i].y, (l[i].x + l[i + 1].x) / 2, (l[i].y + l[i + 1].y) / 2)
                lineTo(l.last().x, l.last().y)
            }
            suave(pts.map { p.o(cx + it.second, it.first) }, true)
            suave(pts.reversed().map { p.o(cx - it.second, it.first) }, false)
            close()
        }
        /** Un camino suave por los puntos [q] (en fracciones del lienzo), por los puntos medios. */
        fun curva(vararg q: Pair<Float, Float>): Path = Path().apply {
            val l = q.map { p.o(it.first, it.second) }
            moveTo(l[0].x, l[0].y)
            for (i in 1 until l.size - 1) quadraticTo(l[i].x, l[i].y, (l[i].x + l[i + 1].x) / 2, (l[i].y + l[i + 1].y) / 2)
            lineTo(l.last().x, l.last().y)
        }
        /** Una cinta de ancho variable por los puntos [q]: el ancho (en fracción del ancho del lienzo) lo da [ancho] de 0 a 1. */
        fun tallo(q: List<Offset>, ancho: (Float) -> Float): Path = TrazosNouveau.cinta(q) { t -> p.w * ancho(t) }
        fun bez(a: Pair<Float, Float>, b: Pair<Float, Float>, cc: Pair<Float, Float>, d: Pair<Float, Float>, n: Int = 28) =
            TrazosNouveau.bezier(p.o(a.first, a.second), p.o(b.first, b.second), p.o(cc.first, cc.second), p.o(d.first, d.second), n)
        /** El vidrio de Nancy dentro de [forma]: de un color al otro de arriba abajo, con la luz a la izquierda y el filo de luz que rebota a la derecha. */
        fun vidrio(forma: Path, arriba: Color, abajo: Color, y0: Float, y1: Float, x0: Float, x1: Float) {
            drawPath(forma, Brush.verticalGradient(listOf(arriba, lerp(arriba, abajo, 0.5f), abajo), p.y(y0), p.y(y1)))
            clipPath(forma) {
                drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.22f), Color.Transparent, Color.Transparent, umbra.copy(alpha = 0.42f)), p.x(x0), p.x(x1)), Offset(p.x(x0), p.y(y0)), Size(p.x(x1 - x0), p.y(y1 - y0)))
                translate(-lw * 1.1f, 0f) { drawPath(forma, Color.White.copy(alpha = 0.16f), style = Stroke(lw * 0.9f)) }
            }
        }
        /** El iris de un solo color, como se graba al ácido en el vidrio: las hojas de espada, el tallo, los tres pétalos que suben ([claro]) y los tres que caen. */
        fun irisGrabado(base: Offset, alto: Float, col: Color, claro: Color) {
            val u = alto / 100f
            listOf(Triple(-24f, 70f, -0.1f), Triple(18f, 82f, 0.08f), Triple(-8f, 52f, 0.16f)).forEach { (dx, h, cm) ->
                drawPath(TrazosNouveau.hojaLanza(Offset(base.x + dx * u * 0.25f, base.y), Offset(base.x + dx * u, base.y - h * u), 9f * u, cm, espada = true), col)
            }
            val fc = Offset(base.x + 2f * u, base.y - 80f * u)
            drawLine(col, base, Offset(fc.x, fc.y + 4f * u), 3.4f * u, StrokeCap.Round)
            listOf(-0.5f, 0.5f, 0f).forEach { a -> val q = Offset(fc.x + sin(a) * 22f * u, fc.y - cos(a) * 22f * u); drawPath(TrazosNouveau.petalo(fc, q, 15f * u), claro) }
            listOf(-0.85f, 0.85f, 0f).forEach { a -> val q = Offset(fc.x + sin(a) * 26f * u, fc.y + cos(a) * 24f * u); drawPath(TrazosNouveau.petalo(Offset(fc.x, fc.y + 2f * u), q, 18f * u), col) }
        }
        when (cat) {
            "Diario" -> {
                // El cartel litográfico, recién salido de la prensa, en su marco del metal del nivel sobre un atril: la mujer de
                // perfil (figura propia) con el pelo en líneas de látigo y una amapola, entre dos iris; arriba la banda roja de las
                // letras y abajo la azul, las dos tintas del cartel. Dos esquinas muy redondas y dos casi rectas, como el marco del tema.
                val x0 = 0.19f; val x1 = 0.81f; val y0 = 0.055f; val y1 = 0.72f
                fun hoja(m: Float): Path = Path().apply {
                    val r = Rect(p.x(x0 + m), p.y(y0) + p.x(m), p.x(x1 - m), p.y(y1) - p.x(m))
                    val g = p.x(0.17f - m * 0.8f); val ch = p.x(0.025f)
                    addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(g), CornerRadius(ch), CornerRadius(g), CornerRadius(ch)))
                }
                // El atril: dos patas atrás y el travesaño.
                val atril = Path().apply {
                    moveTo(p.x(0.3f), p.y(0.765f)); lineTo(p.x(0.34f), p.y(0.6f)); lineTo(p.x(0.36f), p.y(0.6f)); lineTo(p.x(0.335f), p.y(0.765f)); close()
                    moveTo(p.x(0.7f), p.y(0.765f)); lineTo(p.x(0.66f), p.y(0.6f)); lineTo(p.x(0.64f), p.y(0.6f)); lineTo(p.x(0.665f), p.y(0.765f)); close()
                }
                val marco = hoja(0f)
                if (!p.hecho) { silueta(marco, atril); return@with }
                sombraPie(0.26f, 0.74f)
                drawPath(atril, vol(Color(0xFF5A3424), 0.3f, 0.7f))
                drawLine(lerp(Color(0xFF5A3424), umbra, 0.3f), p.o(0.33f, 0.735f), p.o(0.67f, 0.735f), lw * 0.9f)
                sombraDe(marco, 2.2f, 1.8f, 0.24f)
                drawPath(marco, p.metal(x0, x1))
                if (fino) drawPath(hoja(0.012f), p.oscuro.copy(alpha = 0.55f), style = Stroke(lw * 0.35f))
                val papel = hoja(0.03f)
                val crema = Color(0xFFF1E5C6)
                drawPath(papel, Brush.verticalGradient(listOf(lerp(crema, Color.White, 0.25f), crema, lerp(crema, TrazosNouveau.PIEDRA, 0.4f)), p.y(y0), p.y(y1)))
                clipPath(papel) {
                    // El fondo del cartel: un arco de tallos que sube detrás de la figura, en salvia pálido.
                    val arco = Path().apply { addOval(Rect(p.o(0.5f, 0.33f), p.x(0.25f))) }
                    drawPath(arco, lerp(crema, TrazosNouveau.TALLO, 0.35f))
                    drawPath(arco, lerp(crema, TrazosNouveau.TALLO_SOMBRA, 0.6f), style = Stroke(lw * 0.5f))
                    for (k in 0 until 9) {
                        val a = (PI * (0.95 + k * 0.122)).toFloat(); val r0 = p.x(0.18f); val r1 = p.x(0.25f)
                        val q0 = p.o(0.5f, 0.33f) + Offset(r0 * cos(a), r0 * sin(a)); val q1 = p.o(0.5f, 0.33f) + Offset(r1 * cos(a), r1 * sin(a))
                        drawLine(lerp(crema, TrazosNouveau.TALLO_SOMBRA, 0.45f), q0, q1, lw * 0.3f)
                    }
                    // Los dos iris, uno a cada lado.
                    with(TrazosNouveau) {
                        iris(p.o(0.27f, 0.66f), p.y(0.42f), lerp(IRIS, crema, 0.15f), lerp(TALLO, crema, 0.1f), TALLO_SOMBRA, if (fino) pluma.copy(alpha = 0.5f) else null)
                        iris(p.o(0.74f, 0.66f), p.y(0.36f), lerp(IRIS, crema, 0.25f), lerp(TALLO, crema, 0.1f), TALLO_SOMBRA, if (fino) pluma.copy(alpha = 0.5f) else null)
                    }
                    // La mujer de perfil, mirando a la izquierda: el vestido, el cuello, la cara y el pelo.
                    val u = p.w * 0.011f
                    val f = p.o(0.5f, 0.36f)
                    fun q(x: Float, y: Float) = Offset(f.x + x * u, f.y + y * u)
                    val vestido = Path().apply {
                        moveTo(q(-2.5f, 13f).x, q(-2.5f, 13f).y)
                        cubicTo(q(-8f, 17f).x, q(-8f, 17f).y, q(-17f, 19f).x, q(-17f, 19f).y, q(-20f, 30f).x, q(-20f, 30f).y)
                        lineTo(q(20f, 30f).x, q(20f, 30f).y)
                        cubicTo(q(17f, 20f).x, q(17f, 20f).y, q(11f, 16f).x, q(11f, 16f).y, q(6.5f, 12f).x, q(6.5f, 12f).y); close()
                    }
                    drawPath(vestido, TrazosNouveau.TALLO_SOMBRA)
                    drawPath(Path().apply { moveTo(q(-2f, 14f).x, q(-2f, 14f).y); cubicTo(q(2f, 19f).x, q(2f, 19f).y, q(-6f, 24f).x, q(-6f, 24f).y, q(-3f, 30f).x, q(-3f, 30f).y) }, lerp(TrazosNouveau.TALLO, Color.White, 0.2f), style = Stroke(lw * 0.5f))
                    drawPath(Path().apply { moveTo(q(6f, 15f).x, q(6f, 15f).y); cubicTo(q(10f, 20f).x, q(10f, 20f).y, q(5f, 25f).x, q(5f, 25f).y, q(9f, 30f).x, q(9f, 30f).y) }, umbra.copy(alpha = 0.4f), style = Stroke(lw * 0.45f))
                    val piel = Color(0xFFF3DCC0)
                    val cara = Path().apply {
                        moveTo(q(0.2f, -13.5f).x, q(0.2f, -13.5f).y)
                        cubicTo(q(-2.4f, -12f).x, q(-2.4f, -12f).y, q(-3.2f, -8f).x, q(-3.2f, -8f).y, q(-3.1f, -5.6f).x, q(-3.1f, -5.6f).y)
                        cubicTo(q(-4.6f, -3.6f).x, q(-4.6f, -3.6f).y, q(-6.4f, -0.6f).x, q(-6.4f, -0.6f).y, q(-5.6f, 0.4f).x, q(-5.6f, 0.4f).y)
                        cubicTo(q(-5.1f, 1f).x, q(-5.1f, 1f).y, q(-4.2f, 1f).x, q(-4.2f, 1f).y, q(-4f, 1.6f).x, q(-4f, 1.6f).y)
                        cubicTo(q(-4.7f, 2.3f).x, q(-4.7f, 2.3f).y, q(-4.4f, 3f).x, q(-4.4f, 3f).y, q(-3.8f, 3.3f).x, q(-3.8f, 3.3f).y)
                        cubicTo(q(-4.3f, 3.9f).x, q(-4.3f, 3.9f).y, q(-4.1f, 4.7f).x, q(-4.1f, 4.7f).y, q(-3.4f, 5f).x, q(-3.4f, 5f).y)
                        cubicTo(q(-3.6f, 6.2f).x, q(-3.6f, 6.2f).y, q(-3.9f, 7.6f).x, q(-3.9f, 7.6f).y, q(-2.6f, 8.6f).x, q(-2.6f, 8.6f).y)
                        cubicTo(q(-1f, 9.6f).x, q(-1f, 9.6f).y, q(-0.5f, 10.5f).x, q(-0.5f, 10.5f).y, q(-1.2f, 14f).x, q(-1.2f, 14f).y)
                        lineTo(q(5.8f, 13.5f).x, q(5.8f, 13.5f).y)
                        cubicTo(q(5.2f, 9f).x, q(5.2f, 9f).y, q(7.5f, 2f).x, q(7.5f, 2f).y, q(6.5f, -6f).x, q(6.5f, -6f).y)
                        cubicTo(q(5.5f, -11f).x, q(5.5f, -11f).y, q(3f, -13.6f).x, q(3f, -13.6f).y, q(0.2f, -13.5f).x, q(0.2f, -13.5f).y); close()
                    }
                    drawPath(cara, piel)
                    // La sombra del cuello bajo la mandíbula y la mejilla rosada.
                    drawPath(Path().apply { moveTo(q(-1f, 9.4f).x, q(-1f, 9.4f).y); quadraticTo(q(2.5f, 10.5f).x, q(2.5f, 10.5f).y, q(5.6f, 9f).x, q(5.6f, 9f).y); lineTo(q(5.6f, 12f).x, q(5.6f, 12f).y); lineTo(q(-0.8f, 12f).x, q(-0.8f, 12f).y); close() }, lerp(piel, umbra, 0.18f))
                    drawCircle(Color(0xFFE8A89A).copy(alpha = 0.45f), u * 1.6f, q(-1.2f, 2.2f))
                    contorno(cara, 0.75f, 0.5f)
                    // El ojo, la ceja y la boca.
                    drawLine(pluma, q(-2.9f, -3.6f), q(-1.2f, -3.3f), lw * 0.45f, StrokeCap.Round)
                    drawPath(Path().apply { moveTo(q(-3.3f, -5.4f).x, q(-3.3f, -5.4f).y); quadraticTo(q(-2f, -6.4f).x, q(-2f, -6.4f).y, q(-0.6f, -5.6f).x, q(-0.6f, -5.6f).y) }, pluma.copy(alpha = 0.8f), style = Stroke(lw * 0.4f, cap = StrokeCap.Round))
                    drawLine(Color(0xFFB5533A), q(-3.9f, 3.4f), q(-3f, 3.5f), lw * 0.55f, StrokeCap.Round)
                    // El pelo: la masa recogida, el moño atrás y los mechones que bajan en latigazos hasta el hombro.
                    val pelo = TrazosNouveau.TINTA_ROJA
                    val masa = Path().apply {
                        moveTo(q(0.4f, -13.8f).x, q(0.4f, -13.8f).y)
                        cubicTo(q(-1.5f, -18f).x, q(-1.5f, -18f).y, q(5f, -23f).x, q(5f, -23f).y, q(11f, -20.5f).x, q(11f, -20.5f).y)
                        cubicTo(q(17f, -18f).x, q(17f, -18f).y, q(20f, -10f).x, q(20f, -10f).y, q(17f, -3f).x, q(17f, -3f).y)
                        cubicTo(q(15f, 2f).x, q(15f, 2f).y, q(17f, 8f).x, q(17f, 8f).y, q(14f, 15f).x, q(14f, 15f).y)
                        cubicTo(q(12f, 12f).x, q(12f, 12f).y, q(9.5f, 11f).x, q(9.5f, 11f).y, q(7.4f, 12.5f).x, q(7.4f, 12.5f).y)
                        cubicTo(q(7.2f, 6f).x, q(7.2f, 6f).y, q(7.4f, 0f).x, q(7.4f, 0f).y, q(6.2f, -4.5f).x, q(6.2f, -4.5f).y)
                        cubicTo(q(4.6f, -8.5f).x, q(4.6f, -8.5f).y, q(1f, -10.6f).x, q(1f, -10.6f).y, q(-1.8f, -11.4f).x, q(-1.8f, -11.4f).y)
                        cubicTo(q(-1.6f, -12.6f).x, q(-1.6f, -12.6f).y, q(-0.6f, -13.4f).x, q(-0.6f, -13.4f).y, q(0.4f, -13.8f).x, q(0.4f, -13.8f).y); close()
                    }
                    drawPath(masa, pelo)
                    drawPath(masa, Brush.radialGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent), q(4f, -16f), u * 8f))
                    contorno(masa, 0.7f, 0.45f)
                    with(TrazosNouveau) {
                        latigo(q(13f, 8f), q(19f, 21f), u * 2.8f, u * 2.4f, lerp(pelo, umbra, 0.12f), comba = 0.22f, horario = true, sombra = false)
                        latigo(q(17f, -6f), q(23f, 8f), u * 2.4f, u * 2.2f, pelo, comba = -0.22f, horario = false, sombra = false)
                        latigo(q(9f, 10f), q(8f, 24f), u * 2.6f, u * 2f, lerp(pelo, Color.White, 0.1f), comba = 0.2f, horario = false, sombra = false)
                    }
                    // Las hebras de luz en el pelo.
                    listOf(-18f to 4f, -14f to 3f, -10f to 2f, -6f to 1f).forEach { (yy, k) ->
                        drawPath(Path().apply { moveTo(q(1f, yy + 4f).x, q(1f, yy + 4f).y); cubicTo(q(6f, yy - 2f).x, q(6f, yy - 2f).y, q(12f + k, yy - 1f).x, q(12f + k, yy - 1f).y, q(14f + k, yy + 6f).x, q(14f + k, yy + 6f).y) }, Color.White.copy(alpha = 0.28f), style = Stroke(lw * 0.3f, cap = StrokeCap.Round))
                    }
                    // La amapola en el pelo.
                    val am = q(12f, -17f)
                    for (k in 0 until 4) { val a = k * PI.toFloat() / 2 + 0.4f; drawPath(TrazosNouveau.petalo(am, am + Offset(cos(a) * u * 4.4f, sin(a) * u * 4.4f), u * 4.8f), lerp(Color(0xFFD9653F), Color.White, 0.08f * k)) }
                    drawCircle(pluma, u * 1.2f, am)
                    // La banda de las letras, arriba en rojo y abajo en azul, con sus renglones de letra de cartel.
                    drawRect(TrazosNouveau.TINTA_ROJA, p.o(x0, 0.075f), Size(p.x(x1 - x0), p.y(0.065f)))
                    drawRect(TrazosNouveau.TINTA_AZUL, p.o(x0, 0.63f), Size(p.x(x1 - x0), p.y(0.075f)))
                    if (fino) {
                        val az = java.util.Random(19)
                        var x = 0.27f
                        while (x < 0.74f) { val a = 0.018f + az.nextFloat() * 0.02f; drawRoundRect(crema, p.o(x, 0.09f), Size(p.x(a), p.y(0.035f)), CornerRadius(p.x(0.008f))); x += a + 0.012f }
                        x = 0.3f
                        while (x < 0.71f) { val a = 0.012f + az.nextFloat() * 0.016f; drawRect(crema.copy(alpha = 0.85f), p.o(x, 0.652f), Size(p.x(a), p.y(0.012f))); drawRect(crema.copy(alpha = 0.6f), p.o(x, 0.675f), Size(p.x(a * 0.8f), p.y(0.008f))); x += a + 0.01f }
                    }
                    // El grano de la piedra en el papel.
                    if (fino) { val az = java.util.Random(7); repeat(90) { drawCircle(umbra.copy(alpha = 0.06f), lw * (0.12f + az.nextFloat() * 0.2f), p.o(x0 + az.nextFloat() * (x1 - x0), y0 + az.nextFloat() * (y1 - y0))) } }
                }
                contorno(papel, 0.5f, 0.35f)
                // El brillo del vidrio que protege el cartel, de lado a lado.
                clipPath(papel) {
                    drawPath(Path().apply { moveTo(p.x(0.2f), p.y(0.3f)); lineTo(p.x(0.4f), p.y(0.06f)); lineTo(p.x(0.47f), p.y(0.06f)); lineTo(p.x(0.2f), p.y(0.38f)); close() }, Color.White.copy(alpha = 0.16f))
                }
                destello(p.o(x0 + 0.05f, y0 + 0.04f), lw * 2.4f)
            }
            "Mundo" -> {
                // La entrada del metro de Guimard: las dos farolas de fundición verde que suben como tallos, se doblan hacia
                // dentro y sostienen el letrero de lava esmaltada; arriba, sus dos globos rojo anaranjado (los faroles son de 1901),
                // y abajo, la barandilla con sus escudos y la escalera que baja. El metal del nivel en el marco del letrero y los escudos.
                val hierro = TrazosNouveau.HIERRO; val hierroSombra = TrazosNouveau.HIERRO_SOMBRA
                fun poste(s: Float): Path {
                    val x = 0.5f + s * 0.33f
                    val q = bez(x to 0.75f, (x + s * 0.02f) to 0.5f, (x - s * 0.04f) to 0.28f, (0.5f + s * 0.22f) to 0.235f, 30) +
                        bez((0.5f + s * 0.22f) to 0.235f, (0.5f + s * 0.2f) to 0.17f, (0.5f + s * 0.3f) to 0.1f, (0.5f + s * 0.25f) to 0.06f, 16).drop(1)
                    return tallo(q) { t -> 0.05f * (1f - 0.6f * t) + (if (t < 0.06f) 0.03f * (1f - t / 0.06f) else 0f) }
                }
                val postes = listOf(poste(-1f), poste(1f))
                val letrero = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p.x(0.25f), p.y(0.205f), p.x(0.75f), p.y(0.27f)), CornerRadius(p.x(0.03f)))) }
                val baranda = Path().apply {
                    addRect(Rect(p.x(0.08f), p.y(0.56f), p.x(0.23f), p.y(0.75f))); addRect(Rect(p.x(0.77f), p.y(0.56f), p.x(0.92f), p.y(0.75f)))
                }
                if (!p.hecho) { silueta(postes[0], postes[1], letrero, baranda); return@with }
                sombraPie(0.08f, 0.92f)
                // La escalera que baja entre las dos farolas, con la luz de la estación al fondo.
                val boca = Path().apply { moveTo(p.x(0.23f), p.y(0.6f)); lineTo(p.x(0.77f), p.y(0.6f)); lineTo(p.x(0.7f), p.y(0.755f)); lineTo(p.x(0.3f), p.y(0.755f)); close() }
                drawPath(boca, Brush.verticalGradient(listOf(lerp(umbra, TrazosNouveau.LUZ, 0.25f), umbra), p.y(0.6f), p.y(0.755f)))
                // Los peldaños que bajan, cada uno con su canto claro, más oscuros cuanto más hondos.
                for (k in 0 until 6) {
                    val y = 0.6f + k * 0.026f; val m = 0.23f + k * 0.012f
                    drawRect(lerp(TrazosNouveau.PIEDRA, umbra, 0.25f + k * 0.12f), p.o(m, y), Size(p.x(1f - 2 * m), p.y(0.026f)))
                    drawLine(lerp(TrazosNouveau.PIEDRA, Color.White, 0.3f).copy(alpha = 0.8f - k * 0.12f), p.o(m, y), p.o(1f - m, y), lw * 0.45f)
                }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, umbra.copy(alpha = 0.6f)), p.y(0.66f), p.y(0.755f)), p.o(0.23f, 0.66f), Size(p.x(0.54f), p.y(0.095f)))
                // La barandilla de los lados: el pasamanos, los barrotes en curva y los escudos.
                listOf(0.08f to 0.23f, 0.77f to 0.92f).forEachIndexed { i, (a, b) ->
                    drawLine(hierroSombra, p.o(a, 0.565f), p.o(b, 0.565f), lw * 1.5f, StrokeCap.Round)
                    drawLine(lerp(hierro, Color.White, 0.3f), p.o(a, 0.56f), p.o(b, 0.56f), lw * 0.45f, StrokeCap.Round)
                    val xm = (a + b) / 2
                    // Los barrotes que se abren como hojas alrededor del escudo.
                    listOf(-1f, 1f).forEach { s ->
                        drawPath(curva(xm to 0.75f, (xm + s * 0.07f) to 0.68f, (xm + s * 0.05f) to 0.6f, (xm + s * 0.07f) to 0.565f), hierro, style = Stroke(lw * 0.9f, cap = StrokeCap.Round))
                    }
                    val esc = Path().apply {
                        val o = p.o(xm, 0.62f); val r = p.x(0.04f)
                        moveTo(o.x - r, o.y - r * 0.9f); quadraticTo(o.x, o.y - r * 1.3f, o.x + r, o.y - r * 0.9f)
                        quadraticTo(o.x + r * 1.05f, o.y + r * 0.6f, o.x, o.y + r * 1.4f); quadraticTo(o.x - r * 1.05f, o.y + r * 0.6f, o.x - r, o.y - r * 0.9f); close()
                    }
                    drawPath(esc, metalBulto(p.o(xm, 0.62f), p.x(0.05f)))
                    contorno(esc, 0.6f, 0.35f)
                    if (fino) drawPath(Path().apply { val o = p.o(xm, 0.62f); val r = p.x(0.022f); moveTo(o.x, o.y - r); quadraticTo(o.x + r, o.y, o.x, o.y + r * 1.3f); quadraticTo(o.x - r, o.y, o.x, o.y - r) }, p.oscuro.copy(alpha = 0.6f), style = Stroke(lw * 0.3f))
                    destello(p.o(xm - 0.015f, 0.6f), lw * 1.2f)
                    drawRect(hierroSombra, p.o(if (i == 0) a else b - 0.012f, 0.56f), Size(p.x(0.012f), p.y(0.195f)))
                }
                // Las dos farolas: la sombra, el tallo de fundición con su luz y su contorno, y el remate donde se abre.
                postes.forEach { sombraDe(it, 1.4f, 1f, 0.2f) }
                postes.forEach { drawPath(it, vol(hierro, 0.1f, 0.9f)) }
                listOf(-1f, 1f).forEachIndexed { i, s ->
                    val x = 0.5f + s * 0.33f
                    val luz = bez((x - 0.012f) to 0.72f, (x + s * 0.01f) to 0.5f, (x - s * 0.05f - 0.012f) to 0.3f, (0.5f + s * 0.2f) to 0.25f, 24)
                    drawPath(tallo(luz) { t -> 0.012f * (1f - 0.5f * t) }, lerp(hierro, Color.White, 0.35f).copy(alpha = 0.6f))
                    contorno(postes[i], 0.55f, 0.35f)
                    // La base del poste, más ancha, con su moldura.
                    drawPath(Path().apply { moveTo(p.x(x - 0.045f), p.y(0.755f)); quadraticTo(p.x(x - 0.03f), p.y(0.7f), p.x(x - 0.016f), p.y(0.66f)); lineTo(p.x(x + 0.016f), p.y(0.66f)); quadraticTo(p.x(x + 0.03f), p.y(0.7f), p.x(x + 0.045f), p.y(0.755f)); close() }, vol(hierroSombra, x - 0.045f, x + 0.045f))
                }
                // Los faroles: el globo en forma de pera, colgado del remate, y su luz.
                listOf(-1f, 1f).forEach { s ->
                    val o = p.o(0.5f + s * 0.25f, 0.105f)
                    val r = p.x(0.045f)
                    drawCircle(Brush.radialGradient(listOf(TrazosNouveau.FAROL.copy(alpha = 0.45f), Color.Transparent), o, r * 3.2f), r * 3.2f, o)
                    val globo = Path().apply {
                        moveTo(o.x, o.y - r * 1.25f)
                        cubicTo(o.x + r * 0.5f, o.y - r * 1.1f, o.x + r * 1.15f, o.y + r * 0.1f, o.x + r * 0.7f, o.y + r * 0.8f)
                        quadraticTo(o.x, o.y + r * 1.25f, o.x - r * 0.7f, o.y + r * 0.8f)
                        cubicTo(o.x - r * 1.15f, o.y + r * 0.1f, o.x - r * 0.5f, o.y - r * 1.1f, o.x, o.y - r * 1.25f); close()
                    }
                    drawPath(globo, Brush.radialGradient(listOf(Color(0xFFFFE2A8), TrazosNouveau.FAROL, Color(0xFFC0582E)), Offset(o.x - r * 0.3f, o.y - r * 0.2f), r * 1.4f))
                    contorno(globo, 0.55f, 0.35f)
                    brillo(Offset(o.x - r * 0.45f, o.y - r * 0.5f), Offset(o.x - r * 0.5f, o.y + r * 0.2f), lw * 0.5f, 0.5f)
                    drawLine(TrazosNouveau.HIERRO_SOMBRA, Offset(o.x - r * 0.4f, o.y - r * 1.2f), Offset(o.x + r * 0.4f, o.y - r * 1.2f), lw * 0.9f, StrokeCap.Round)
                }
                // El letrero: la placa del metal del nivel que lo enmarca, la lava esmaltada y la palabra.
                sombraDe(letrero, 1.2f, 1.2f, 0.25f)
                drawPath(letrero, p.metal(0.25f, 0.75f))
                val placa = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p.x(0.265f), p.y(0.215f), p.x(0.735f), p.y(0.26f)), CornerRadius(p.x(0.02f)))) }
                drawPath(placa, Brush.verticalGradient(listOf(Color(0xFFEFD99A), Color(0xFFD9B862)), p.y(0.215f), p.y(0.26f)))
                if (fino) texto("MÉTROPOLITAIN", p, 0.5f, 0.2375f, 0.04f, hierroSombra) else drawLine(hierroSombra, p.o(0.3f, 0.2375f), p.o(0.7f, 0.2375f), lw * 1.2f)
                contorno(letrero, 0.55f, 0.35f)
                destello(p.o(0.28f, 0.215f), lw * 1.6f)
            }
            "Cuerpo" -> {
                // La joya de libélula (como los adornos de corpiño que Lalique llevó a la Exposición de 1900), prendida en el raso
                // de su estuche abierto: las alas de esmalte translúcido en sus celdas de metal, el cuerpo del metal del nivel con
                // sus piedras verdes y los ojos de piedra de luna.
                val tapa = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p.x(0.07f), p.y(0.12f), p.x(0.93f), p.y(0.6f)), CornerRadius(p.x(0.08f)))) }
                val caja0 = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p.x(0.05f), p.y(0.6f), p.x(0.95f), p.y(0.765f)), CornerRadius(p.x(0.03f)))) }
                if (!p.hecho) { silueta(tapa, caja0); return@with }
                sombraPie(0.06f, 0.94f)
                val cuero = Color(0xFF4A3058)
                // La tapa abierta: el cuero por fuera, el filete de oro y el raso marfil por dentro, con su luz.
                sombraDe(tapa, 1.6f, 1.4f, 0.2f)
                drawPath(tapa, vol(cuero, 0.07f, 0.93f))
                val raso = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p.x(0.12f), p.y(0.155f), p.x(0.88f), p.y(0.575f)), CornerRadius(p.x(0.055f)))) }
                drawPath(raso, Brush.radialGradient(listOf(Color(0xFFFBF6EA), Color(0xFFEDE2CC), Color(0xFFCDBFA6)), p.o(0.4f, 0.28f), p.x(0.6f)))
                if (fino) {
                    // Los pliegues del raso, que salen del centro.
                    clipPath(raso) { for (k in 0 until 7) { val a = -PI.toFloat() * (0.1f + k * 0.13f); drawLine(Color(0xFFB7A78A).copy(alpha = 0.35f), p.o(0.5f, 0.37f), p.o(0.5f, 0.37f) + Offset(cos(a), sin(a)) * p.x(0.45f), lw * 0.4f) } }
                    drawPath(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p.x(0.1f), p.y(0.14f), p.x(0.9f), p.y(0.59f)), CornerRadius(p.x(0.065f)))) }, TrazosNouveau.ORO, style = Stroke(lw * 0.5f))
                }
                // La caja: el cuero, el terciopelo violeta del fondo y el broche dorado.
                drawPath(caja0, vol(lerp(cuero, umbra, 0.15f), 0.05f, 0.95f))
                drawRect(Brush.verticalGradient(listOf(lerp(TrazosNouveau.IRIS, Color.White, 0.1f), lerp(TrazosNouveau.IRIS, umbra, 0.45f)), p.y(0.6f), p.y(0.66f)), p.o(0.1f, 0.6f), Size(p.x(0.8f), p.y(0.06f)))
                drawLine(TrazosNouveau.ORO, p.o(0.07f, 0.675f), p.o(0.93f, 0.675f), lw * 0.45f)
                drawRoundRect(metalBulto(p.o(0.5f, 0.7f), p.x(0.04f)), p.o(0.47f, 0.675f), Size(p.x(0.06f), p.y(0.05f)), CornerRadius(p.x(0.012f)))
                contorno(caja0, 0.5f, 0.35f); contorno(tapa, 0.5f, 0.35f)
                // La libélula.
                val o = p.o(0.5f, 0.32f); val u = p.w * 0.0042f
                fun en(x: Float, y: Float) = Offset(o.x + x * u, o.y + y * u)
                val alas = listOf(Triple(-1f, -6f, -0.18f), Triple(1f, -6f, -0.18f), Triple(-1f, 4f, 0.26f), Triple(1f, 4f, 0.26f))
                alas.forEachIndexed { i, (s, dy, ang) ->
                    val base = en(s * 3f, dy)
                    val l = if (i < 2) 92f else 84f
                    val punta = Offset(base.x + s * l * u * cos(ang), base.y + l * u * sin(ang))
                    val ala = TrazosNouveau.petalo(base, punta, (if (i < 2) 26f else 24f) * u)
                    translate(lw * 0.8f, lw * 1f) { drawPath(ala, umbra.copy(alpha = 0.14f)) }
                    drawPath(ala, Brush.linearGradient(listOf(TrazosNouveau.LIBELULA.copy(alpha = 0.9f), lerp(TrazosNouveau.VIDRIO_VERDE, TrazosNouveau.NACAR, 0.4f).copy(alpha = 0.82f), lerp(TrazosNouveau.NACAR, TrazosNouveau.MALVA, 0.25f).copy(alpha = 0.75f)), base, punta))
                    // Las celdas del esmalte: la vena del borde, la del medio y las venillas que las cruzan, todas de metal.
                    clipPath(ala) {
                        if (fino) for (k in 1 until 9) {
                            val t = k / 9f
                            val m = Offset(base.x + (punta.x - base.x) * t, base.y + (punta.y - base.y) * t)
                            val nx = -(punta.y - base.y); val ny = punta.x - base.x; val nl = kotlin.math.sqrt(nx * nx + ny * ny).coerceAtLeast(0.001f)
                            val d = 16f * u
                            drawLine(p.base.copy(alpha = 0.8f), m - Offset(nx / nl * d, ny / nl * d), m + Offset(nx / nl * d + (punta.x - base.x) * 0.04f, ny / nl * d), lw * 0.28f)
                        }
                        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.3f), Color.Transparent), o.y - 30f * u, o.y), Offset(o.x - 100f * u, o.y - 30f * u), Size(200f * u, 30f * u))
                    }
                    drawPath(ala, p.base, style = Stroke(lw * 0.55f))
                    drawLine(p.brillo, base, Offset(base.x + (punta.x - base.x) * 0.92f, base.y + (punta.y - base.y) * 0.92f), lw * 0.35f)
                    drawCircle(bulto(TrazosNouveau.VIDRIO_VIOLETA, Offset(base.x + (punta.x - base.x) * 0.82f, base.y + (punta.y - base.y) * 0.82f), 3.2f * u), 3.2f * u, Offset(base.x + (punta.x - base.x) * 0.82f, base.y + (punta.y - base.y) * 0.82f))
                }
                // El cuerpo: el abdomen largo del metal del nivel, con sus anillos y sus piedras; el tórax y la cabeza.
                val cola = List(22) { i -> en(0f, 9f + i * 3.2f) }
                val abdomen = TrazosNouveau.cinta(cola) { t -> 9f * u * (1f - 0.55f * t) }
                translate(lw * 0.9f, lw * 1f) { drawPath(abdomen, umbra.copy(alpha = 0.22f)) }
                drawPath(abdomen, Brush.horizontalGradient(listOf(p.brillo, p.base, p.oscuro), o.x - 5f * u, o.x + 5f * u))
                for (k in 1 until 9) { val y = 9f + k * 7.6f; drawLine(p.oscuro, en(-4.4f * (1f - k / 16f), y), en(4.4f * (1f - k / 16f), y), lw * 0.4f) }
                listOf(18f, 34f, 50f).forEachIndexed { k, y -> val g = en(0f, y); val r = (3.4f - k * 0.6f) * u; drawCircle(bulto(Color(0xFF6FB58C), g, r), r, g); brillo(g - Offset(r * 0.3f, r * 0.3f), g - Offset(r * 0.25f, r * 0.35f), lw * 0.4f, 0.7f) }
                val torax = Path().apply { addOval(Rect(en(-7f, -11f), en(7f, 10f))) }
                drawPath(torax, metalBulto(en(0f, 0f), 10f * u))
                val gema = en(0f, -1f); drawCircle(bulto(Color(0xFF5E9C6E), gema, 4.6f * u), 4.6f * u, gema)
                drawCircle(Color.White.copy(alpha = 0.6f), 1.2f * u, gema - Offset(1.4f * u, 1.6f * u))
                listOf(-1f, 1f).forEach { s ->
                    val ojo = en(s * 5f, -15f)
                    drawCircle(Brush.radialGradient(listOf(Color(0xFFF2F4FA), Color(0xFFB9C7DC), Color(0xFF7A8AAE)), ojo - Offset(1.5f * u, 1.5f * u), 6f * u), 5.2f * u, ojo)
                    drawCircle(p.base, 5.2f * u, ojo, style = Stroke(lw * 0.4f))
                }
                destello(en(-3f, -4f), lw * 2f)
            }
            "Mente" -> {
                // La lámpara de vidrio de colores, como las de los vidrieros de Nancy: la pantalla en hongo de vidrio que pasa del
                // ámbar al violeta, con los tallos y los iris grabados al ácido, el pie de balaustre del mismo vidrio, las tres
                // garras de hierro del metal del nivel que sostienen la pantalla y la luz eléctrica encendida por dentro.
                val pantalla = Path().apply {
                    moveTo(p.x(0.1f), p.y(0.37f))
                    cubicTo(p.x(0.1f), p.y(0.17f), p.x(0.28f), p.y(0.085f), p.x(0.5f), p.y(0.085f))
                    cubicTo(p.x(0.72f), p.y(0.085f), p.x(0.9f), p.y(0.17f), p.x(0.9f), p.y(0.37f))
                    // El borde de abajo, ondulado como el de un sombrero de hongo.
                    for (k in 0 until 8) { val xa = 0.9f - k * 0.1f; quadraticTo(p.x(xa - 0.05f), p.y(if (k % 2 == 0) 0.395f else 0.375f), p.x(xa - 0.1f), p.y(0.37f)) }
                    close()
                }
                val pie = perfil(0.5f, listOf(0.38f to 0.035f, 0.44f to 0.04f, 0.52f to 0.062f, 0.6f to 0.1f, 0.66f to 0.105f, 0.7f to 0.075f, 0.725f to 0.06f, 0.74f to 0.15f, 0.765f to 0.16f))
                if (!p.hecho) { silueta(pantalla, pie); return@with }
                sombraPie(0.32f, 0.68f)
                // La luz que cae sobre la peana, debajo de la pantalla.
                drawOval(Brush.radialGradient(listOf(TrazosNouveau.LUZ.copy(alpha = 0.35f), Color.Transparent), p.o(0.5f, 0.74f), p.x(0.42f)), p.o(0.08f, 0.715f), Size(p.x(0.84f), p.y(0.06f)))
                sombraDe(pie, 1.4f, 1.2f, 0.2f)
                vidrio(pie, lerp(TrazosNouveau.VIDRIO_VIOLETA, TrazosNouveau.VIDRIO_AMBAR, 0.25f), lerp(TrazosNouveau.VIDRIO_AMBAR, Color.White, 0.15f), 0.38f, 0.765f, 0.34f, 0.66f)
                // Los tallos grabados en el pie.
                clipPath(pie) {
                    with(TrazosNouveau) { listOf(-0.06f, 0.05f).forEach { dx -> drawPath(hojaLanza(p.o(0.5f + dx, 0.75f), p.o(0.5f + dx * 0.4f, 0.48f), p.x(0.04f), 0.12f, espada = true), lerp(VIDRIO_VIOLETA, umbra, 0.3f).copy(alpha = 0.7f)) } }
                }
                contorno(pie, 0.55f, 0.35f)
                // La montura del metal del nivel: el anillo del pie y las tres garras que suben a la pantalla.
                drawRect(p.metal(0.33f, 0.67f), p.o(0.34f, 0.735f), Size(p.x(0.32f), p.y(0.012f)))
                listOf(-1f, 0f, 1f).forEach { s ->
                    val garra = tallo(bez((0.5f + s * 0.02f) to 0.42f, (0.5f + s * 0.12f) to 0.41f, (0.5f + s * 0.2f) to 0.38f, (0.5f + s * 0.3f) to 0.345f, 16)) { t -> 0.018f * (1f - 0.4f * t) }
                    drawPath(garra, p.metal(0.2f, 0.8f))
                }
                drawOval(metalBulto(p.o(0.5f, 0.41f), p.x(0.06f)), p.o(0.44f, 0.395f), Size(p.x(0.12f), p.y(0.03f)))
                // La pantalla de vidrio: el color que pasa del ámbar al violeta, la luz de dentro y el grabado.
                sombraDe(pantalla, 1.6f, 1.6f, 0.2f)
                vidrio(pantalla, lerp(TrazosNouveau.VIDRIO_AMBAR, TrazosNouveau.LUZ, 0.35f), TrazosNouveau.VIDRIO_VIOLETA, 0.085f, 0.395f, 0.1f, 0.9f)
                clipPath(pantalla) {
                    drawCircle(Brush.radialGradient(listOf(TrazosNouveau.LUZ_HALO.copy(alpha = 0.75f), TrazosNouveau.LUZ.copy(alpha = 0.3f), Color.Transparent), p.o(0.5f, 0.33f), p.x(0.34f)), p.x(0.34f), p.o(0.5f, 0.33f))
                    // El grabado: tres iris con sus hojas de espada, en el violeta más hondo del vidrio y su filo claro.
                    val grabado = lerp(TrazosNouveau.VIDRIO_VIOLETA, umbra, 0.35f).copy(alpha = 0.8f)
                    val grabadoLuz = lerp(TrazosNouveau.VIDRIO_VIOLETA, TrazosNouveau.MALVA, 0.5f).copy(alpha = 0.85f)
                    listOf(Triple(0.24f, 0.2f, 0.42f), Triple(0.52f, 0.23f, 0.48f), Triple(0.79f, 0.2f, 0.4f)).forEach { (x, alto, _) -> irisGrabado(p.o(x, 0.42f), p.y(alto), grabado, grabadoLuz) }
                    // El brillo del vidrio, arriba a la izquierda.
                    drawPath(Path().apply { moveTo(p.x(0.2f), p.y(0.27f)); quadraticTo(p.x(0.22f), p.y(0.14f), p.x(0.4f), p.y(0.11f)) }, Color.White.copy(alpha = 0.45f), style = Stroke(lw * 1.1f, cap = StrokeCap.Round))
                }
                contorno(pantalla, 0.6f, 0.4f)
                // Los remates de las garras, donde muerden el borde de la pantalla.
                listOf(0.2f, 0.5f, 0.8f).forEach { x -> drawCircle(metalBulto(p.o(x, 0.355f), lw * 1.3f), lw * 1.1f, p.o(x, 0.357f)) }
                drawCircle(metalBulto(p.o(0.5f, 0.085f), lw * 1.6f), lw * 1.4f, p.o(0.5f, 0.075f))
                destello(p.o(0.5f, 0.07f), lw * 1.8f)
            }
            "Cultura" -> {
                // El jarrón de vidrio de Nancy (como los de Gallé, premiados en la Exposición): el vidrio de dos capas, la de fondo
                // que pasa del ámbar pálido al verde, y la de encima, violeta, tallada al ácido en iris y una libélula, con su relieve;
                // el pie del metal del nivel con sus hojas que suben por el vidrio.
                val jarron = perfil(0.5f, listOf(0.075f to 0.085f, 0.1f to 0.068f, 0.16f to 0.05f, 0.24f to 0.058f, 0.34f to 0.1f, 0.46f to 0.19f, 0.56f to 0.215f, 0.64f to 0.17f, 0.7f to 0.09f, 0.725f to 0.07f))
                val base = perfil(0.5f, listOf(0.715f to 0.09f, 0.735f to 0.11f, 0.75f to 0.15f, 0.765f to 0.16f))
                if (!p.hecho) { silueta(jarron, base); return@with }
                sombraPie(0.34f, 0.66f)
                sombraDe(jarron, 2f, 1.6f, 0.22f)
                vidrio(jarron, lerp(TrazosNouveau.VIDRIO_AMBAR, Color.White, 0.45f), lerp(TrazosNouveau.VIDRIO_VERDE, TrazosNouveau.VIDRIO_AMBAR, 0.25f), 0.075f, 0.725f, 0.285f, 0.715f)
                clipPath(jarron) {
                    // La capa de encima: el violeta hondo, tallado. Primero el relieve claro, corrido a la izquierda, y luego la capa.
                    val capa = lerp(TrazosNouveau.VIDRIO_VIOLETA, umbra, 0.25f)
                    val luzCapa = lerp(TrazosNouveau.VIDRIO_VIOLETA, Color.White, 0.4f).copy(alpha = 0.55f)
                    for ((col, d) in listOf(luzCapa to Offset(-lw * 0.45f, -lw * 0.45f), capa to Offset.Zero)) translate(d.x, d.y) {
                        irisGrabado(p.o(0.44f, 0.74f), p.y(0.5f), col, if (col == capa) lerp(capa, TrazosNouveau.MALVA, 0.35f) else col)
                        irisGrabado(p.o(0.63f, 0.74f), p.y(0.34f), col, if (col == capa) lerp(capa, TrazosNouveau.MALVA, 0.35f) else col)
                        drawPath(TrazosNouveau.hojaLanza(p.o(0.32f, 0.74f), p.o(0.27f, 0.42f), p.x(0.06f), -0.12f, espada = true), col)
                        // La franja de musgo al pie, como las raíces en el fondo del bosque.
                        drawRect(col, p.o(0.2f, 0.69f), Size(p.x(0.6f), p.y(0.04f)))
                    }
                    with(TrazosNouveau) { libelula(p.o(0.6f, 0.22f), p.x(0.16f), -28f, 0.2f, capa, capa.copy(alpha = 0.65f), capa) }
                    // El brillo largo del vidrio y la luz que entra por el cuello.
                    drawPath(Path().apply { moveTo(p.x(0.36f), p.y(0.58f)); quadraticTo(p.x(0.33f), p.y(0.42f), p.x(0.43f), p.y(0.3f)) }, Color.White.copy(alpha = 0.42f), style = Stroke(lw * 1.2f, cap = StrokeCap.Round))
                    drawLine(Color.White.copy(alpha = 0.35f), p.o(0.465f, 0.12f), p.o(0.468f, 0.22f), lw * 0.6f, StrokeCap.Round)
                }
                contorno(jarron, 0.6f, 0.4f)
                // El labio del cuello, abierto, con su canto.
                drawOval(Brush.horizontalGradient(listOf(lerp(TrazosNouveau.VIDRIO_AMBAR, Color.White, 0.6f), TrazosNouveau.VIDRIO_AMBAR), p.x(0.415f), p.x(0.585f)), p.o(0.415f, 0.066f), Size(p.x(0.17f), p.y(0.02f)))
                drawOval(umbra.copy(alpha = 0.5f), p.o(0.44f, 0.07f), Size(p.x(0.12f), p.y(0.011f)))
                // El pie de metal, con sus cuatro hojas que abrazan el vidrio.
                drawPath(base, p.metal(0.34f, 0.66f))
                listOf(-0.075f, -0.025f, 0.025f, 0.075f).forEach { dx ->
                    val h0 = p.o(0.5f + dx, 0.725f)
                    val hoja = TrazosNouveau.hojaLanza(h0, p.o(0.5f + dx * 1.5f, 0.655f), p.x(0.03f), if (dx < 0) -0.2f else 0.2f)
                    drawPath(hoja, metalBulto(h0, p.x(0.04f)))
                    contorno(hoja, 0.45f, 0.3f)
                }
                contorno(base, 0.5f, 0.35f)
                destello(p.o(0.43f, 0.07f), lw * 1.8f)
            }
            "Gente" -> {
                // El abanico de seda pintada, abierto sobre su pie: el país de seda con una rama de glicina que cae en curva, el
                // borde que sigue las flores, las varillas de nácar, los dos padrones del metal del nivel y la borla de seda.
                val o = p.o(0.5f, 0.6f); val R = p.x(0.43f); val ri = p.x(0.17f)
                val a0 = 198f; val a1 = 342f
                fun en(r: Float, g: Float) = Offset(o.x + r * cos(Math.toRadians(g.toDouble()).toFloat()), o.y + r * sin(Math.toRadians(g.toDouble()).toFloat()))
                val pais = Path().apply {
                    val n = 36
                    for (i in 0..n) {
                        val g = a0 + (a1 - a0) * i / n
                        // El borde se ondula suave, como el contorno de las flores pintadas.
                        val r = R * (1f + 0.025f * sin(i * PI.toFloat() * 0.5f) + 0.035f * sin(i / n.toFloat() * PI.toFloat()))
                        val q = en(r, g); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
                    }
                    for (i in 36 downTo 0) { val q = en(ri, a0 + (a1 - a0) * i / 36); lineTo(q.x, q.y) }
                    close()
                }
                val padrones = Path().apply {
                    listOf(a0 - 2f, a1 + 2f).forEach { g ->
                        val b1 = en(lw * 1.2f, g - 90f); val b2 = en(lw * 1.2f, g + 90f)
                        val t = en(R * 1.03f, g); moveTo(b1.x, b1.y); lineTo(t.x + (b1.x - o.x) * 0.6f, t.y + (b1.y - o.y) * 0.6f); lineTo(t.x + (b2.x - o.x) * 0.6f, t.y + (b2.y - o.y) * 0.6f); lineTo(b2.x, b2.y); close()
                    }
                }
                val pieA = Path().apply { moveTo(o.x - p.x(0.035f), p.y(0.765f)); quadraticTo(o.x, p.y(0.69f), o.x - p.x(0.01f), o.y); lineTo(o.x + p.x(0.01f), o.y); quadraticTo(o.x, p.y(0.69f), o.x + p.x(0.035f), p.y(0.765f)); close() }
                if (!p.hecho) { silueta(pais, padrones, pieA); return@with }
                sombraPie(0.38f, 0.62f)
                // El pie: un tallo del metal del nivel que sostiene el clavillo.
                drawPath(pieA, p.metal(0.46f, 0.54f))
                drawOval(metalBulto(p.o(0.5f, 0.76f), p.x(0.08f)), p.o(0.42f, 0.75f), Size(p.x(0.16f), p.y(0.018f)))
                // Las varillas de nácar, que se ven entre el clavillo y el país.
                val nv = 14
                for (i in 0..nv) {
                    val g = a0 + (a1 - a0) * i / nv
                    val a = en(lw * 0.6f, g); val b = en(ri * 1.02f, g)
                    drawLine(umbra.copy(alpha = 0.3f), a + Offset(lw * 0.3f, lw * 0.4f), b + Offset(lw * 0.3f, lw * 0.4f), lw * 1.5f, StrokeCap.Round)
                    drawLine(lerp(TrazosNouveau.NACAR, if (i % 2 == 0) TrazosNouveau.MALVA else TrazosNouveau.VIDRIO_VERDE, 0.18f), a, b, lw * 1.3f, StrokeCap.Round)
                    drawLine(Color.White.copy(alpha = 0.55f), a, b, lw * 0.4f, StrokeCap.Round)
                }
                // El país de seda: la seda pálida, los pliegues, la pintura y el borde.
                sombraDe(pais, 1.8f, 1.6f, 0.22f)
                drawPath(pais, Brush.radialGradient(listOf(Color(0xFFF6EEDD), Color(0xFFEFE2CF), lerp(Color(0xFFE6D5DE), TrazosNouveau.MALVA, 0.15f)), o, R))
                clipPath(pais) {
                    // Los pliegues: uno sí y uno no, un poco más oscuro, con su filo de luz.
                    val np = 22
                    for (i in 0 until np) {
                        val g0 = a0 + (a1 - a0) * i / np; val g1 = a0 + (a1 - a0) * (i + 1) / np
                        if (i % 2 == 1) drawPath(Path().apply { val q0 = en(ri, g0); moveTo(q0.x, q0.y); val q1 = en(R * 1.1f, g0); lineTo(q1.x, q1.y); val q2 = en(R * 1.1f, g1); lineTo(q2.x, q2.y); val q3 = en(ri, g1); lineTo(q3.x, q3.y); close() }, umbra.copy(alpha = 0.045f))
                        drawLine(Color.White.copy(alpha = 0.22f), en(ri, g0), en(R * 1.1f, g0), lw * 0.22f)
                    }
                    // La rama de glicina: el tallo que entra por la izquierda y se curva, las hojas y los racimos que cuelgan.
                    val rama = bez(0.1f to 0.52f, 0.28f to 0.24f, 0.66f to 0.22f, 0.9f to 0.44f, 30)
                    drawPath(TrazosNouveau.cinta(rama) { t -> lw * (1.1f - 0.6f * t) }, lerp(TrazosNouveau.CAREY, umbra, 0.2f))
                    listOf(0.15f, 0.32f, 0.52f, 0.7f, 0.86f).forEachIndexed { k, t ->
                        val q = rama[(t * (rama.size - 1)).toInt()]
                        val s = if (k % 2 == 0) -1f else 1f
                        drawPath(TrazosNouveau.hojaLanza(q, q + Offset(p.x(0.05f) * s, -p.x(0.035f)), p.x(0.022f), 0.2f * s), TrazosNouveau.TALLO)
                    }
                    listOf(0.2f, 0.36f, 0.5f, 0.64f, 0.8f).forEachIndexed { k, t ->
                        val q = rama[(t * (rama.size - 1)).toInt()]
                        val largo = p.y(0.1f + 0.04f * (k % 2))
                        for (j in 0 until 7) {
                            val f = j / 6f
                            val cq = Offset(q.x + sin(f * 3f + k) * p.x(0.008f), q.y + largo * f)
                            val r = p.x(0.024f) * (1f - 0.55f * f)
                            drawCircle(lerp(lerp(TrazosNouveau.IRIS, TrazosNouveau.MALVA, 0.3f + 0.4f * f), umbra, 0.08f * (j % 2)), r, cq)
                            drawCircle(Color.White.copy(alpha = 0.3f), r * 0.4f, cq - Offset(r * 0.3f, r * 0.35f))
                        }
                    }
                    // La libélula chica que pasa.
                    with(TrazosNouveau) { libelula(p.o(0.62f, 0.5f), p.x(0.1f), 60f, 0.1f, LIBELULA, NACAR.copy(alpha = 0.85f), ORO) }
                }
                contorno(pais, 0.55f, 0.38f)
                // La cenefa del borde, del metal del nivel, que sigue la onda.
                val borde = Path().apply {
                    for (i in 0..36) { val g = a0 + (a1 - a0) * i / 36; val r = R * (0.97f + 0.025f * sin(i * PI.toFloat() * 0.5f) + 0.035f * sin(i / 36f * PI.toFloat())); val q = en(r, g); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }
                }
                drawPath(borde, p.base.copy(alpha = 0.85f), style = Stroke(lw * 0.5f))
                // Los padrones del metal del nivel y el clavillo con su piedra.
                drawPath(padrones, p.metal(0.05f, 0.95f)); contorno(padrones, 0.5f, 0.3f)
                drawCircle(metalBulto(o, lw * 2.4f), lw * 2.2f, o)
                drawCircle(bulto(TrazosNouveau.VIDRIO_VIOLETA, o, lw * 1.2f), lw * 1.1f, o)
                // La borla de seda que cuelga del clavillo.
                val cordon = Path().apply { moveTo(o.x + lw, o.y + lw); quadraticTo(o.x + p.x(0.08f), o.y + p.y(0.05f), o.x + p.x(0.12f), o.y + p.y(0.07f)) }
                drawPath(cordon, TrazosNouveau.IRIS, style = Stroke(lw * 0.55f, cap = StrokeCap.Round))
                val bo = Offset(o.x + p.x(0.12f), o.y + p.y(0.07f))
                drawCircle(bulto(TrazosNouveau.IRIS, bo, lw * 1.5f), lw * 1.3f, bo)
                drawPath(Path().apply { moveTo(bo.x - lw * 1.1f, bo.y + lw); lineTo(bo.x + lw * 1.1f, bo.y + lw); lineTo(bo.x + lw * 1.6f, bo.y + p.y(0.07f)); lineTo(bo.x - lw * 1.6f, bo.y + p.y(0.07f)); close() }, vol(TrazosNouveau.IRIS, 0.6f, 0.66f))
                if (fino) for (k in -2..2) drawLine(umbra.copy(alpha = 0.35f), Offset(bo.x + k * lw * 0.5f, bo.y + lw * 1.5f), Offset(bo.x + k * lw * 0.7f, bo.y + p.y(0.068f)), lw * 0.2f)
                destello(en(R * 0.98f, a0 - 2f), lw * 1.6f)
            }
            "Constancia" -> {
                // El pavo real de esmalte y metal, posado en su rama: el cuello en curva, el copete de tres plumas, el ala verde y la
                // cola larga que cae en línea de látigo, con los ojos de sus plumas en el metal del nivel.
                val cuerpo0 = Path().apply {
                    moveTo(p.x(0.36f), p.y(0.22f))
                    cubicTo(p.x(0.33f), p.y(0.3f), p.x(0.34f), p.y(0.34f), p.x(0.37f), p.y(0.38f))
                    cubicTo(p.x(0.42f), p.y(0.46f), p.x(0.56f), p.y(0.47f), p.x(0.62f), p.y(0.42f))
                    cubicTo(p.x(0.58f), p.y(0.36f), p.x(0.5f), p.y(0.3f), p.x(0.43f), p.y(0.27f))
                    cubicTo(p.x(0.41f), p.y(0.24f), p.x(0.41f), p.y(0.2f), p.x(0.39f), p.y(0.155f))
                    cubicTo(p.x(0.38f), p.y(0.135f), p.x(0.34f), p.y(0.135f), p.x(0.33f), p.y(0.155f))
                    cubicTo(p.x(0.32f), p.y(0.17f), p.x(0.35f), p.y(0.18f), p.x(0.36f), p.y(0.22f)); close()
                }
                val colaPts = bez(0.56f to 0.41f, 0.86f to 0.44f, 0.94f to 0.64f, 0.66f to 0.71f, 40)
                val cola = tallo(colaPts) { t -> (0.1f + 0.08f * sin(t * PI.toFloat() * 0.8f)) * (1f - 0.85f * t * t) }
                val rama = tallo(bez(0.12f to 0.47f, 0.35f to 0.43f, 0.55f to 0.5f, 0.74f to 0.45f, 24)) { t -> 0.026f * (1f - 0.4f * t) }
                val soporte = tallo(bez(0.33f to 0.765f, 0.2f to 0.66f, 0.44f to 0.56f, 0.32f to 0.455f, 24)) { t -> 0.034f * (1f - 0.45f * t) }
                if (!p.hecho) { silueta(cuerpo0, cola, rama, soporte); return@with }
                sombraPie(0.22f, 0.5f)
                // La cola: el verde azul del esmalte con sus barbas, y los ojos.
                sombraDe(cola, 2f, 1.6f, 0.2f)
                drawPath(cola, Brush.linearGradient(listOf(TrazosNouveau.PAVO, TrazosNouveau.PAVO_VERDE, lerp(TrazosNouveau.PAVO_VERDE, TrazosNouveau.ORO, 0.3f)), p.o(0.56f, 0.41f), p.o(0.7f, 0.72f)))
                clipPath(cola) {
                    if (fino) for (i in 2 until colaPts.size step 2) {
                        val a = colaPts[i]; val b = colaPts[i - 2]
                        val dx = a.x - b.x; val dy = a.y - b.y; val l = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
                        val nx = -dy / l * p.x(0.12f); val ny = dx / l * p.x(0.12f)
                        drawLine(lerp(TrazosNouveau.PAVO_VERDE, Color.White, 0.3f).copy(alpha = 0.35f), a, Offset(a.x + nx + dx, a.y + ny + dy), lw * 0.25f)
                        drawLine(lerp(TrazosNouveau.PAVO_VERDE, Color.White, 0.3f).copy(alpha = 0.35f), a, Offset(a.x - nx + dx, a.y - ny + dy), lw * 0.25f)
                    }
                    drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent, umbra.copy(alpha = 0.3f)), p.x(0.55f), p.x(0.95f)), Offset.Zero, size)
                }
                contorno(cola, 0.55f, 0.35f)
                listOf(0.18f, 0.34f, 0.5f, 0.64f, 0.78f, 0.9f).forEachIndexed { k, t ->
                    val q = colaPts[(t * (colaPts.size - 1)).toInt()]
                    val desvio = if (k % 2 == 0) 0.032f else -0.032f
                    val i0 = (t * (colaPts.size - 1)).toInt().coerceIn(1, colaPts.size - 1)
                    val dx = colaPts[i0].x - colaPts[i0 - 1].x; val dy = colaPts[i0].y - colaPts[i0 - 1].y; val l = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
                    val c0 = Offset(q.x - dy / l * p.x(desvio), q.y + dx / l * p.x(desvio))
                    val r = p.x(0.04f) * (1f - 0.25f * t)
                    drawOval(lerp(TrazosNouveau.PAVO_VERDE, TrazosNouveau.ORO, 0.25f), Offset(c0.x - r * 1.25f, c0.y - r), Size(r * 2.5f, r * 2f))
                    drawOval(metalBulto(c0, r), Offset(c0.x - r * 0.95f, c0.y - r * 0.75f), Size(r * 1.9f, r * 1.5f))
                    drawOval(lerp(TrazosNouveau.PAVO, Color.White, 0.15f), Offset(c0.x - r * 0.62f, c0.y - r * 0.5f), Size(r * 1.24f, r))
                    drawOval(Color(0xFF1C2E5A), Offset(c0.x - r * 0.36f, c0.y - r * 0.32f), Size(r * 0.72f, r * 0.64f))
                    if (fino) drawCircle(Color.White.copy(alpha = 0.5f), r * 0.12f, c0 - Offset(r * 0.15f, r * 0.12f))
                }
                // El soporte y la rama, del metal del nivel.
                drawPath(soporte, p.metal(0.24f, 0.36f)); contorno(soporte, 0.45f, 0.3f)
                drawOval(metalBulto(p.o(0.33f, 0.76f), p.x(0.08f)), p.o(0.25f, 0.75f), Size(p.x(0.16f), p.y(0.018f)))
                sombraDe(rama, 1f, 1.4f, 0.2f)
                drawPath(rama, p.metal(0.12f, 0.74f)); contorno(rama, 0.5f, 0.32f)
                with(TrazosNouveau) { listOf(0.2f to -1f, 0.66f to 1f).forEach { (x, s) -> drawPath(hojaLanza(p.o(x, 0.46f), p.o(x + 0.07f * s, 0.52f), p.x(0.03f), 0.2f * s), metalBulto(p.o(x, 0.48f), p.x(0.05f))) } }
                // El cuerpo y el cuello: el azul del esmalte, el ala verde con sus escamas y la luz.
                sombraDe(cuerpo0, 1.4f, 1.2f, 0.22f)
                drawPath(cuerpo0, Brush.linearGradient(listOf(lerp(TrazosNouveau.PAVO, Color.White, 0.25f), TrazosNouveau.PAVO, lerp(Color(0xFF1C2E5A), TrazosNouveau.PAVO, 0.4f)), p.o(0.34f, 0.16f), p.o(0.6f, 0.46f)))
                val ala = Path().apply {
                    moveTo(p.x(0.43f), p.y(0.31f)); cubicTo(p.x(0.5f), p.y(0.31f), p.x(0.58f), p.y(0.36f), p.x(0.62f), p.y(0.42f))
                    cubicTo(p.x(0.54f), p.y(0.45f), p.x(0.45f), p.y(0.43f), p.x(0.41f), p.y(0.38f)); close()
                }
                drawPath(ala, Brush.linearGradient(listOf(lerp(TrazosNouveau.PAVO_VERDE, Color.White, 0.2f), TrazosNouveau.PAVO_VERDE, lerp(TrazosNouveau.PAVO_VERDE, umbra, 0.4f)), p.o(0.43f, 0.31f), p.o(0.6f, 0.44f)))
                if (fino) clipPath(ala) { for (k in 0 until 5) drawArc(lerp(TrazosNouveau.PAVO_VERDE, umbra, 0.4f).copy(alpha = 0.5f), 0f, 180f, false, p.o(0.42f + k * 0.035f, 0.33f + k * 0.012f), Size(p.x(0.05f), p.y(0.03f)), style = Stroke(lw * 0.3f)) }
                contorno(ala, 0.5f, 0.3f)
                contorno(cuerpo0, 0.65f, 0.42f)
                brillo(p.o(0.355f, 0.2f), p.o(0.35f, 0.29f), lw * 0.7f, 0.4f)
                // La cara: la mancha blanca del ojo, el ojo y el pico.
                drawOval(Color.White.copy(alpha = 0.85f), p.o(0.338f, 0.148f), Size(p.x(0.03f), p.y(0.012f)))
                drawCircle(pluma, lw * 0.55f, p.o(0.348f, 0.153f))
                drawPath(Path().apply { moveTo(p.x(0.333f), p.y(0.152f)); lineTo(p.x(0.305f), p.y(0.165f)); lineTo(p.x(0.334f), p.y(0.163f)); close() }, Color(0xFF8A7A5A))
                // El copete: tres plumas en su tallo fino, con la punta del metal del nivel.
                listOf(-0.03f, 0f, 0.028f).forEach { dx ->
                    val a = p.o(0.36f, 0.14f); val b = p.o(0.36f + dx, 0.085f)
                    drawLine(TrazosNouveau.PAVO, a, b, lw * 0.35f)
                    drawOval(metalBulto(b, lw * 1.4f), Offset(b.x - lw * 0.9f, b.y - lw * 1.3f), Size(lw * 1.8f, lw * 2.2f))
                }
                destello(p.o(0.37f, 0.08f), lw * 1.4f)
            }
            "Curiosos" -> {
                // El frasco de perfume de 1900: el vidrio en gota con flores en relieve y el perfume ámbar dentro, el cuello con su
                // collar del metal del nivel, el tapón de vidrio en amapola violeta y la borla de seda atada al cuello.
                val frasco = perfil(0.5f, listOf(0.33f to 0.05f, 0.36f to 0.06f, 0.42f to 0.11f, 0.52f to 0.18f, 0.62f to 0.215f, 0.7f to 0.2f, 0.745f to 0.16f, 0.765f to 0.15f))
                /** Los pétalos de la amapola del tapón: tres anchos y redondos que forman la copa, el de atrás más alto. */
                val f = p.o(0.5f, 0.205f)
                val petTapon = listOf(0f to 0.13f, -0.75f to 0.12f, 0.75f to 0.12f).map { (a, l) -> TrazosNouveau.petalo(Offset(f.x, f.y + p.y(0.055f)), Offset(f.x + sin(a) * p.x(l), f.y + p.y(0.055f) - cos(a) * p.x(l) * 1.1f), p.x(0.2f)) }
                val tapon = Path().apply { petTapon.forEach { addPath(it) }; addRect(Rect(p.x(0.475f), p.y(0.25f), p.x(0.525f), p.y(0.34f))) }
                if (!p.hecho) { silueta(frasco, tapon); return@with }
                sombraPie(0.3f, 0.7f)
                sombraDe(frasco, 2f, 1.5f, 0.22f)
                // El vidrio claro y el perfume que lo llena hasta poco más de la mitad.
                drawPath(frasco, Brush.horizontalGradient(listOf(Color(0xFFF4EFE6), Color(0xFFE2E6E4), Color(0xFFBCC4C4)), p.x(0.29f), p.x(0.71f)))
                clipPath(frasco) {
                    drawRect(Brush.verticalGradient(listOf(lerp(TrazosNouveau.VIDRIO_AMBAR, Color.White, 0.25f), TrazosNouveau.VIDRIO_AMBAR, lerp(TrazosNouveau.VIDRIO_AMBAR, Color(0xFF8A4A20), 0.5f)), p.y(0.5f), p.y(0.77f)), p.o(0.2f, 0.5f), Size(p.x(0.6f), p.y(0.27f)))
                    drawLine(lerp(TrazosNouveau.VIDRIO_AMBAR, Color.White, 0.6f), p.o(0.3f, 0.5f), p.o(0.7f, 0.5f), lw * 0.6f)
                    // Las flores en relieve del vidrio: dos tallos que suben y se curvan, con sus flores, en blanco y en sombra.
                    for ((col, d) in listOf(Color.White.copy(alpha = 0.5f) to Offset(-lw * 0.4f, -lw * 0.4f), umbra.copy(alpha = 0.22f) to Offset(lw * 0.35f, lw * 0.35f))) translate(d.x, d.y) {
                        val t = bez(0.4f to 0.76f, 0.52f to 0.68f, 0.44f to 0.54f, 0.6f to 0.45f, 22)
                        drawPath(TrazosNouveau.cinta(t) { k -> lw * (0.8f - 0.4f * k) }, col)
                        drawPath(TrazosNouveau.hojaLanza(t[6], p.o(0.62f, 0.62f), p.x(0.05f), 0.25f, espada = true), col)
                        drawPath(TrazosNouveau.hojaLanza(t[12], p.o(0.36f, 0.5f), p.x(0.04f), -0.2f, espada = true), col)
                        val fl = t.last()
                        listOf(-0.5f, 0.5f, 0f).forEach { a -> drawPath(TrazosNouveau.petalo(fl, fl + Offset(sin(a), -cos(a)) * p.x(0.06f), p.x(0.045f)), col) }
                        listOf(-0.9f, 0.9f).forEach { a -> drawPath(TrazosNouveau.petalo(fl, fl + Offset(sin(a), cos(a)) * p.x(0.065f), p.x(0.05f)), col) }
                    }
                    drawPath(Path().apply { moveTo(p.x(0.36f), p.y(0.68f)); quadraticTo(p.x(0.32f), p.y(0.55f), p.x(0.4f), p.y(0.43f)) }, Color.White.copy(alpha = 0.55f), style = Stroke(lw * 1f, cap = StrokeCap.Round))
                }
                contorno(frasco, 0.55f, 0.38f)
                // El collar del metal del nivel, con su fila de perlas.
                drawRect(p.metal(0.44f, 0.56f), p.o(0.44f, 0.325f), Size(p.x(0.12f), p.y(0.028f)))
                if (fino) for (k in 0 until 5) drawCircle(metalBulto(p.o(0.452f + k * 0.024f, 0.339f), lw * 0.6f), lw * 0.5f, p.o(0.452f + k * 0.024f, 0.339f))
                // La borla de seda, atada al cuello y caída a la derecha.
                val bo = p.o(0.68f, 0.5f)
                drawPath(Path().apply { moveTo(p.x(0.555f), p.y(0.34f)); cubicTo(p.x(0.64f), p.y(0.36f), p.x(0.7f), p.y(0.42f), bo.x, bo.y) }, TrazosNouveau.IRIS_LENGUA, style = Stroke(lw * 0.5f, cap = StrokeCap.Round))
                drawCircle(bulto(TrazosNouveau.IRIS_LENGUA, bo, lw * 1.4f), lw * 1.2f, bo)
                drawPath(Path().apply { moveTo(bo.x - lw, bo.y + lw * 0.8f); lineTo(bo.x + lw, bo.y + lw * 0.8f); lineTo(bo.x + lw * 1.5f, bo.y + p.y(0.07f)); lineTo(bo.x - lw * 1.5f, bo.y + p.y(0.07f)); close() }, vol(TrazosNouveau.IRIS_LENGUA, 0.66f, 0.7f))
                if (fino) for (k in -2..2) drawLine(Color(0xFF8A6A20).copy(alpha = 0.45f), Offset(bo.x + k * lw * 0.45f, bo.y + lw * 1.3f), Offset(bo.x + k * lw * 0.65f, bo.y + p.y(0.068f)), lw * 0.2f)
                // El tapón: el vástago esmerilado y la amapola de vidrio violeta.
                drawRect(Brush.horizontalGradient(listOf(Color(0xFFE6E3EC), Color(0xFFB7B0C4)), p.x(0.475f), p.x(0.525f)), p.o(0.475f, 0.25f), Size(p.x(0.05f), p.y(0.08f)))
                listOf(petTapon[0], petTapon[1], petTapon[2]).forEachIndexed { k, pet ->
                    drawPath(pet, Brush.radialGradient(listOf(lerp(TrazosNouveau.MALVA, Color.White, 0.3f), TrazosNouveau.IRIS, lerp(TrazosNouveau.VIDRIO_VIOLETA, umbra, 0.35f)), Offset(f.x - p.x(0.04f), f.y - p.y(0.02f)), p.x(0.16f)))
                    contorno(pet, 0.5f, 0.32f)
                    // Las arrugas del pétalo de amapola.
                    if (fino) for (r in listOf(-0.4f, 0f, 0.4f)) drawLine(Color.White.copy(alpha = 0.25f), Offset(f.x, f.y + p.y(0.05f)), Offset(f.x + (k - 1) * p.x(0.04f) + r * p.x(0.03f), f.y - p.y(0.02f)), lw * 0.25f)
                }
                drawPath(Path().apply { moveTo(f.x - p.x(0.06f), f.y + p.y(0.03f)); quadraticTo(f.x, f.y + p.y(0.06f), f.x + p.x(0.06f), f.y + p.y(0.03f)) }, Color.White.copy(alpha = 0.35f), style = Stroke(lw * 0.5f, cap = StrokeCap.Round))
                drawCircle(metalBulto(p.o(0.5f, 0.258f), lw * 1.6f), lw * 1.3f, p.o(0.5f, 0.258f))
                destello(p.o(0.45f, 0.32f), lw * 1.6f)
            }
            else -> {
                // El peine de carey para el moño, de pie en su cojín de terciopelo: las púas de carey translúcido con sus manchas,
                // y arriba la cresta de cuerno tallada (dos hojas largas que se abren y una flor en medio, con su esmalte malva),
                // con las nervaduras y el engaste de la piedra de luna en el metal del nivel.
                val puas = Path().apply {
                    for (k in 0 until 10) {
                        val x = 0.215f + k * 0.0625f
                        moveTo(p.x(x), p.y(0.45f)); lineTo(p.x(x + 0.04f), p.y(0.45f)); lineTo(p.x(x + 0.033f), p.y(0.69f))
                        quadraticTo(p.x(x + 0.02f), p.y(0.725f), p.x(x + 0.007f), p.y(0.69f)); close()
                    }
                    addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p.x(0.19f), p.y(0.41f), p.x(0.81f), p.y(0.47f)), CornerRadius(p.x(0.025f))))
                }
                val fl = p.o(0.5f, 0.3f)
                val hojas = listOf(-1f, 1f).map { s -> TrazosNouveau.hojaLanza(p.o(0.5f + s * 0.04f, 0.4f), p.o(0.5f + s * 0.44f, 0.36f), p.x(0.15f), 0.34f * s) }
                val petalos = (0 until 5).map { j -> val a = j * 2 * PI.toFloat() / 5 - PI.toFloat() / 2; TrazosNouveau.petalo(fl, fl + Offset(cos(a), sin(a)) * p.x(0.15f), p.x(0.14f)) }
                val cojin = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p.x(0.14f), p.y(0.67f), p.x(0.86f), p.y(0.765f)), CornerRadius(p.x(0.05f)))) }
                if (!p.hecho) { silueta(puas, hojas[0], hojas[1], *petalos.toTypedArray(), cojin); return@with }
                sombraPie(0.14f, 0.86f)
                // Las púas de carey, con la luz que pasa a través y las manchas.
                sombraDe(puas, 1.4f, 1f, 0.18f)
                drawPath(puas, Brush.verticalGradient(listOf(lerp(TrazosNouveau.CAREY, TrazosNouveau.CAREY_MANCHA, 0.35f), TrazosNouveau.CAREY, lerp(TrazosNouveau.CAREY, umbra, 0.35f)), p.y(0.41f), p.y(0.72f)))
                clipPath(puas) {
                    val az = java.util.Random(41)
                    repeat(if (fino) 30 else 12) {
                        val q = p.o(0.2f + az.nextFloat() * 0.6f, 0.41f + az.nextFloat() * 0.3f); val r = p.x(0.012f + az.nextFloat() * 0.025f)
                        drawOval(TrazosNouveau.CAREY_MANCHA.copy(alpha = 0.55f), Offset(q.x - r, q.y - r * 0.6f), Size(r * 2f, r * 1.2f))
                    }
                    drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.18f), Color.Transparent, umbra.copy(alpha = 0.25f)), p.x(0.19f), p.x(0.81f)), p.o(0.19f, 0.41f), Size(p.x(0.62f), p.y(0.31f)))
                }
                contorno(puas, 0.45f, 0.28f)
                // La cresta de cuerno: las dos hojas, con su luz de cuerno claro, la nervadura del metal del nivel y el borde.
                val cuerno = Color(0xFFE8D2A6)
                hojas.forEach { sombraDe(it, 1.4f, 1.2f, 0.2f) }
                hojas.forEachIndexed { i, h ->
                    val s = if (i == 0) -1f else 1f
                    drawPath(h, Brush.linearGradient(listOf(lerp(cuerno, Color.White, 0.35f), cuerno, lerp(cuerno, TrazosNouveau.CAREY_MANCHA, 0.5f)), p.o(0.5f, 0.24f), p.o(0.5f + s * 0.4f, 0.38f)))
                    val nervio = bez((0.5f + s * 0.04f) to 0.4f, (0.5f + s * 0.17f) to 0.3f, (0.5f + s * 0.32f) to 0.28f, (0.5f + s * 0.43f) to 0.355f, 16)
                    drawPath(TrazosNouveau.cinta(nervio) { t -> lw * (0.9f - 0.6f * t) }, p.metal(0.1f, 0.9f))
                    if (fino) clipPath(h) { for (k in 1 until 6) {
                        val q = nervio[k * 2 + 2]
                        drawLine(p.base.copy(alpha = 0.75f), q, Offset(q.x + s * p.x(0.035f), q.y - p.y(0.035f)), lw * 0.3f)
                        drawLine(p.base.copy(alpha = 0.6f), q, Offset(q.x + s * p.x(0.03f), q.y + p.y(0.025f)), lw * 0.3f)
                    } }
                    drawPath(h, p.base, style = Stroke(lw * 0.55f))
                }
                // La flor de en medio: los pétalos de cuerno con el esmalte malva hacia el centro.
                petalos.forEach { pet ->
                    drawPath(pet, Brush.radialGradient(listOf(lerp(TrazosNouveau.MALVA, TrazosNouveau.IRIS, 0.3f), TrazosNouveau.MALVA, lerp(cuerno, Color.White, 0.2f)), fl, p.x(0.15f)))
                    drawPath(pet, p.base, style = Stroke(lw * 0.45f))
                }
                // La piedra de luna en su engaste, con su reflejo azul.
                val rp = p.x(0.05f)
                drawCircle(metalBulto(fl, rp * 1.4f), rp * 1.3f, fl)
                drawOval(Brush.radialGradient(listOf(Color(0xFFFBFBFF), Color(0xFFCFD8EE), Color(0xFF8E9CC4)), Offset(fl.x - rp * 0.3f, fl.y - rp * 0.4f), rp * 1.2f), Offset(fl.x - rp, fl.y - rp), Size(rp * 2f, rp * 2f))
                drawOval(Color.White.copy(alpha = 0.65f), Offset(fl.x - rp * 0.55f, fl.y - rp * 0.65f), Size(rp * 0.45f, rp * 0.55f))
                // El cojín de terciopelo, delante, con su luz y su galón.
                drawPath(cojin, Brush.verticalGradient(listOf(lerp(TrazosNouveau.IRIS, Color.White, 0.18f), TrazosNouveau.IRIS, lerp(TrazosNouveau.IRIS, umbra, 0.5f)), p.y(0.67f), p.y(0.765f)))
                drawPath(Path().apply { moveTo(p.x(0.18f), p.y(0.685f)); quadraticTo(p.x(0.5f), p.y(0.665f), p.x(0.82f), p.y(0.685f)) }, Color.White.copy(alpha = 0.3f), style = Stroke(lw * 0.8f, cap = StrokeCap.Round))
                if (fino) drawPath(cojin, TrazosNouveau.ORO.copy(alpha = 0.8f), style = Stroke(lw * 0.4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(lw * 0.8f, lw * 0.6f))))
                contorno(cojin, 0.5f, 0.35f)
                destello(Offset(fl.x - rp * 0.6f, fl.y - rp * 0.7f), lw * 1.6f)
            }
        }
    }


    /**
     * Galia: lo que se cuelga en la casa grande del oppidum o se ofrece en el santuario, cada pieza sobre un tocón de roble con
     * su aro del metal del nivel, con la luz que entra por arriba a la izquierda: el torques colgado de su poste, el estátero
     * del caballo, el carnyx con cabeza de jabalí (como los de Tintignac), la hoz de oro con el muérdago en el manto blanco
     * (Plinio 16.249–251), el caldero colgado sobre el fuego, el jabalí de bronce, el casco de jinete con sus carrilleras, el
     * huevo de serpiente en su garra (Plinio 29.52–54) y la espada de La Tène en su vaina.
     */
    internal fun DrawScope.galia(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val fino = p.h > 70f * density
        val corteza = p.c(Color(0xFF4A3A28)); val cara = p.c(Color(0xFFC9A46E)); val anillo = p.c(Color(0xFF8A6A40))
        val madera = p.c(TrazosGalia.MADERA); val hierro = p.c(TrazosGalia.HIERRO); val oro = p.c(TrazosGalia.ORO); val oroBrillo = p.c(TrazosGalia.ORO_BRILLO)
        val roble = p.c(TrazosGalia.ROBLE); val muerdago = p.c(TrazosGalia.MUERDAGO); val baya = p.c(TrazosGalia.BAYA)
        val lino = p.c(Color(0xFFEDE8DA)); val coral = p.c(Color(0xFFB8483A)); val tinta = p.c(Color(0xFF2A2218))
        /** La sombra con color: el pardo del humo de la casa, no el negro. */
        val umbra = Color(0xFF2A1A0C)
        /** Cuánto brilla el metal: más en cada nivel. */
        val lustre = 0.24f + 0.08f * p.nivel
        /** Volumen con la luz desde arriba a la izquierda: el claro, el tono y la sombra, de [x0] a [x1]. */
        fun vol(c: Color, x0: Float, x1: Float): Brush = if (p.hecho) Brush.horizontalGradient(listOf(lerp(c, Color.White, 0.22f), c, c, lerp(c, umbra, 0.5f)), p.x(x0), p.x(x1)) else SolidColor(p.sombra)
        /** Volumen redondo: la luz arriba a la izquierda del centro [o]. */
        fun bulto(c: Color, o: Offset, r: Float): Brush = if (p.hecho) Brush.radialGradient(listOf(lerp(c, Color.White, 0.3f), c, lerp(c, umbra, 0.5f)), Offset(o.x - r * 0.35f, o.y - r * 0.4f), r * 1.55f) else SolidColor(p.sombra)
        /** El metal del nivel en volumen redondo. */
        fun metalBulto(o: Offset, r: Float): Brush = if (p.hecho) Brush.radialGradient(listOf(lerp(p.brillo, Color.White, 0.2f), p.brillo, p.base, p.oscuro), Offset(o.x - r * 0.35f, o.y - r * 0.4f), r * 1.6f) else SolidColor(p.sombra)
        /** Un brillo en la superficie: un trazo blanco y suave, solo si ya se logró. */
        fun brillo(a: Offset, b: Offset, ancho: Float, alfa: Float = 0.3f) { if (p.hecho) drawLine(Color.White.copy(alpha = alfa), a, b, ancho, StrokeCap.Round) }
        /** El destello del metal pulido en [o]: una estrella de cuatro puntas, más grande y más viva en cada nivel. */
        fun destello(o: Offset, r: Float) {
            if (!p.hecho || p.nivel < 2) return
            val t = r * (0.55f + 0.2f * p.nivel); val a = 0.35f + 0.15f * p.nivel
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = a * 0.6f), Color.Transparent), o, t * 0.7f), t * 0.7f, o)
            drawLine(Color.White.copy(alpha = a), Offset(o.x - t, o.y), Offset(o.x + t, o.y), lw * 0.35f, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = a), Offset(o.x, o.y - t), Offset(o.x, o.y + t), lw * 0.35f, StrokeCap.Round)
        }
        /** El martillado del metal dentro de [forma]: abolladuras chicas, con su sombra abajo a la derecha y su luz arriba. */
        fun martillado(forma: Path, x0: Float, y0: Float, x1: Float, y1: Float, n: Int, semilla: Long, r: Float = lw * 0.55f) {
            if (!p.hecho || !fino) return
            val az = java.util.Random(semilla)
            clipPath(forma) {
                repeat(n) {
                    val q = p.o(x0 + az.nextFloat() * (x1 - x0), y0 + az.nextFloat() * (y1 - y0)); val rr = r * (0.6f + az.nextFloat() * 0.7f)
                    drawCircle(p.oscuro.copy(alpha = 0.11f), rr, q + Offset(rr * 0.3f, rr * 0.3f))
                    drawCircle(p.brillo.copy(alpha = 0.2f), rr * 0.55f, q - Offset(rr * 0.3f, rr * 0.3f))
                }
            }
        }
        /** La luz que rebota del lado de la sombra: un filo cálido por dentro del borde de abajo a la derecha de [forma]. */
        fun rebote(forma: Path, color: Color, alfa: Float = 0.45f) {
            if (!p.hecho) return
            clipPath(forma) { translate(-lw * 0.9f, -lw * 0.9f) { drawPath(forma, color.copy(alpha = alfa), style = Stroke(lw * 1.2f)) } }
        }
        /** La sombra de [forma] echada abajo a la derecha, con el color del humo. */
        fun sombraDe(forma: Path, dx: Float = 1.1f, dy: Float = 1f, alfa: Float = 0.24f) {
            if (p.hecho) translate(lw * dx, lw * dy) { drawPath(forma, umbra.copy(alpha = alfa)) }
        }
        /** Donde la pieza toca el corte del tocón: la sombra de contacto, oscura y corta, y la que se alarga a la derecha. */
        fun sombraPie(x0: Float, x1: Float, y: Float = 0.7f) {
            if (!p.hecho) return
            drawOval(umbra.copy(alpha = 0.2f), Offset(p.x(x0 + 0.04f), p.y(y) - p.y(0.011f)), Size(p.x(x1 - x0 + 0.06f), p.y(0.022f)))
            val c = p.o((x0 + x1) / 2, y); val r = p.x((x1 - x0) / 2)
            drawOval(Brush.radialGradient(listOf(umbra.copy(alpha = 0.5f), umbra.copy(alpha = 0f)), c, r), Offset(c.x - r, c.y - p.y(0.008f)), Size(r * 2, p.y(0.016f)))
        }
        /** Las vetas de una pieza de madera de pie, de [x0] a [x1] y de [y0] a [y1]: líneas que ondulan y un nudo. */
        fun veta(x0: Float, y0: Float, x1: Float, y1: Float, semilla: Long) {
            if (!p.hecho || !fino) return
            val az = java.util.Random(semilla)
            for (k in 0 until 3) {
                val x = x0 + (x1 - x0) * (0.22f + 0.28f * k)
                val v = Path().apply {
                    moveTo(p.x(x), p.y(y0)); var y = y0
                    while (y < y1) { val d = (az.nextFloat() - 0.5f) * (x1 - x0) * 0.35f; quadraticTo(p.x(x + d), p.y(y + 0.04f), p.x(x), p.y(minOf(y + 0.08f, y1))); y += 0.08f }
                }
                drawPath(v, umbra.copy(alpha = 0.3f), style = Stroke(lw * 0.3f))
            }
            drawLine(Color.White.copy(alpha = 0.22f), p.o(x0 + (x1 - x0) * 0.12f, y0 + 0.01f), p.o(x0 + (x1 - x0) * 0.12f, y1 - 0.01f), lw * 0.4f)
        }
        /**
         * El tocón de roble: la corteza en placas con sus surcos hondos, más oscura al pie; el corte de arriba con la albura
         * clara, los anillos que ondulan, los radios plateados del roble y una grieta; y el aro del metal del nivel que lo
         * ciñe siguiendo la curva del tronco (como el aro de un tonel, que es invento galo).
         */
        fun tocon(x0: Float = 0.27f, x1: Float = 0.73f, y0: Float = 0.7f) {
            val y1 = 0.775f; val e = 0.022f; val xm = (x0 + x1) / 2
            val cuerpo = Path().apply {
                moveTo(p.x(x0), p.y(y0)); lineTo(p.x(x0 + 0.006f), p.y(y1 - e * 0.5f))
                quadraticTo(p.x(xm), p.y(y1 + e * 0.5f), p.x(x1 - 0.006f), p.y(y1 - e * 0.5f)); lineTo(p.x(x1), p.y(y0)); close()
            }
            // La sombra que el tocón echa sobre la tabla, a la derecha.
            if (p.hecho) drawOval(umbra.copy(alpha = 0.3f), Offset(p.x(x0 + 0.05f), p.y(y1 - 0.016f)), Size(p.x(x1 - x0 + 0.05f), p.y(0.026f)))
            drawPath(cuerpo, vol(corteza, x0, x1))
            if (p.hecho) clipPath(cuerpo) {
                // Las placas de la corteza: el lomo que da a la luz, el surco hondo entre una y otra y alguna grieta de través.
                val az = java.util.Random(17)
                var x = x0 + 0.004f
                while (x < x1) {
                    val wd = 0.016f + az.nextFloat() * 0.016f
                    val luz = 1f - (x - x0) / (x1 - x0)
                    val dx = (az.nextFloat() - 0.5f) * 0.01f
                    drawLine(lerp(corteza, cara, 0.45f).copy(alpha = 0.12f + 0.4f * luz), p.o(x + wd * 0.35f, y0 + 0.012f), p.o(x + wd * 0.35f + dx, y1), lw * 0.55f)
                    drawLine(umbra.copy(alpha = 0.6f), p.o(x + wd, y0 + 0.01f), p.o(x + wd + dx, y1), lw * 0.42f)
                    if (az.nextFloat() < 0.45f) { val yy = y0 + 0.022f + az.nextFloat() * 0.036f; drawLine(umbra.copy(alpha = 0.45f), p.o(x + 0.002f, yy), p.o(x + wd - 0.002f, yy + 0.004f), lw * 0.3f) }
                    x += wd
                }
                // Más oscuro al pie y bajo el labio del corte.
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, umbra.copy(alpha = 0.45f)), p.y(y1 - 0.035f), p.y(y1 + 0.01f)), Offset(p.x(x0), p.y(y1 - 0.035f)), Size(p.x(x1 - x0), p.y(0.05f)))
                drawRect(Brush.verticalGradient(listOf(umbra.copy(alpha = 0.4f), Color.Transparent), p.y(y0), p.y(y0 + 0.026f)), Offset(p.x(x0), p.y(y0)), Size(p.x(x1 - x0), p.y(0.026f)))
            }
            // El aro de metal que lo ciñe, curvo como el tronco.
            val ya = y0 + (y1 - y0) * 0.52f; val hb = 0.013f
            val aro = Path().apply {
                moveTo(p.x(x0 - 0.004f), p.y(ya)); quadraticTo(p.x(xm), p.y(ya + e * 0.9f), p.x(x1 + 0.004f), p.y(ya))
                lineTo(p.x(x1 + 0.004f), p.y(ya + hb)); quadraticTo(p.x(xm), p.y(ya + hb + e * 0.9f), p.x(x0 - 0.004f), p.y(ya + hb)); close()
            }
            drawPath(aro, p.metal(x0, x1))
            if (p.hecho) {
                drawPath(Path().apply { moveTo(p.x(x0), p.y(ya) + lw * 0.3f); quadraticTo(p.x(xm), p.y(ya + e * 0.9f) + lw * 0.3f, p.x(x1), p.y(ya) + lw * 0.3f) }, p.brillo.copy(alpha = lustre + 0.2f), style = Stroke(lw * 0.35f))
                drawPath(Path().apply { moveTo(p.x(x0), p.y(ya + hb)); quadraticTo(p.x(xm), p.y(ya + hb + e * 0.9f), p.x(x1), p.y(ya + hb)) }, umbra.copy(alpha = 0.55f), style = Stroke(lw * 0.4f))
                listOf(x0 + 0.045f, xm, x1 - 0.05f).forEach { xc ->
                    val t = (xc - x0) / (x1 - x0); val yc = ya + hb * 0.5f + e * 0.9f * 2 * t * (1 - t)
                    drawCircle(p.oscuro, lw * 0.4f, p.o(xc, yc)); drawCircle(p.brillo.copy(alpha = 0.9f), lw * 0.16f, p.o(xc - 0.002f, yc - 0.002f))
                }
            }
            // El corte de arriba: la madera clara, la albura, los anillos corridos hacia el lado de la corteza más gruesa,
            // los radios del roble, la grieta y la médula.
            val corte = Rect(p.x(x0), p.y(y0 - e), p.x(x1), p.y(y0 + e))
            drawOval(if (p.hecho) Brush.radialGradient(listOf(lerp(cara, Color.White, 0.22f), cara, lerp(cara, anillo, 0.55f)), Offset(corte.center.x - corte.width * 0.2f, corte.center.y - corte.height * 0.3f), corte.width * 0.6f) else SolidColor(p.sombra), corte.topLeft, corte.size)
            if (p.hecho) {
                val ce = Offset(corte.center.x + p.x(0.012f), corte.center.y + corte.height * 0.05f)
                drawOval(lerp(cara, Color.White, 0.3f).copy(alpha = 0.55f), Offset(corte.left + lw * 0.5f, corte.top + lw * 0.3f), Size(corte.width - lw, corte.height - lw * 0.6f), style = Stroke(corte.height * 0.1f))
                drawOval(anillo.copy(alpha = 0.25f), Offset(ce.x - corte.width * 0.3f, ce.y - corte.height * 0.3f), Size(corte.width * 0.6f, corte.height * 0.6f))
                for (k in 1..6) {
                    val f = k / 7f
                    val ronda = Path().apply {
                        for (i in 0..36) {
                            val a = i * 2 * PI / 36; val rr = f * (1f + 0.05f * sin(a * 3 + k).toFloat())
                            val q = Offset(ce.x + (corte.width * 0.47f * rr * cos(a)).toFloat(), ce.y + (corte.height * 0.47f * rr * sin(a)).toFloat())
                            if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
                        }
                        close()
                    }
                    drawPath(ronda, anillo.copy(alpha = 0.5f), style = Stroke(lw * (if (k % 2 == 0) 0.32f else 0.22f)))
                }
                if (fino) for (i in 0 until 16) {
                    val a = i * 2 * PI / 16 + 0.13 * (i % 3); val f0 = 0.2f + 0.1f * (i % 2); val f1 = 0.62f + 0.2f * ((i * 7) % 3) / 2f
                    drawLine(lerp(cara, Color.White, 0.55f).copy(alpha = 0.5f), Offset(ce.x + (corte.width * 0.47f * f0 * cos(a)).toFloat(), ce.y + (corte.height * 0.47f * f0 * sin(a)).toFloat()),
                        Offset(ce.x + (corte.width * 0.47f * f1 * cos(a)).toFloat(), ce.y + (corte.height * 0.47f * f1 * sin(a)).toFloat()), lw * 0.22f)
                }
                drawPath(Path().apply { moveTo(corte.right - lw * 0.6f, corte.center.y + corte.height * 0.08f); lineTo(ce.x + p.x(0.02f), ce.y - corte.height * 0.04f); lineTo(corte.right - lw * 0.4f, corte.center.y + corte.height * 0.22f); close() }, umbra.copy(alpha = 0.7f))
                drawCircle(anillo, lw * 0.35f, ce)
                drawOval(corteza, corte.topLeft, corte.size, style = Stroke(lw * 0.8f))
                drawArc(lerp(corteza, cara, 0.5f), 160f, 110f, false, Offset(corte.left + lw * 0.2f, corte.top + lw * 0.1f), Size(corte.width - lw * 0.4f, corte.height - lw * 0.2f), style = Stroke(lw * 0.3f))
            }
        }
        when (cat) {
            "Diario" -> {
                // El torques de la casa grande, colgado de su clavija en el poste de roble: el aro torcido del metal del nivel, con
                // sus remates de tope que se miran por abajo, como los de los nobles (Diodoro 5.27).
                tocon(); sombraPie(0.45f, 0.55f)
                caja(p, 0.465f, 0.08f, 0.535f, 0.7f, vol(madera, 0.465f, 0.535f))
                veta(0.465f, 0.1f, 0.535f, 0.69f, 23)
                if (p.hecho) drawRect(Brush.verticalGradient(listOf(Color.Transparent, umbra.copy(alpha = 0.35f)), p.y(0.62f), p.y(0.7f)), Offset(p.x(0.465f), p.y(0.62f)), Size(p.x(0.07f), p.y(0.08f)))
                caja(p, 0.455f, 0.07f, 0.545f, 0.09f, vol(lerp(madera, cara, 0.2f), 0.455f, 0.545f))
                if (p.hecho) drawLine(umbra.copy(alpha = 0.45f), p.o(0.465f, 0.091f), p.o(0.535f, 0.091f), lw * 0.6f)
                val c = p.o(0.5f, 0.36f); val r = p.w * 0.25f; val g = p.w * 0.06f
                val caja0 = Offset(c.x - r, c.y - r); val med = Size(r * 2, r * 2)
                // La sombra del aro sobre el poste.
                if (p.hecho) drawArc(umbra.copy(alpha = 0.3f), 113f, 314f, false, Offset(c.x - r + lw * 1.4f, c.y - r + lw * 1.6f), med, style = Stroke(g, cap = StrokeCap.Round))
                if (p.hecho) {
                    // El aro en volumen: el tono, la sombra por dentro abajo a la derecha y la luz por fuera arriba a la izquierda.
                    with(TrazosGalia) { torques(c, r, g, p.oscuro, p.brillo) }
                    drawArc(p.oscuro, 113f, 314f, false, caja0, med, style = Stroke(g, cap = StrokeCap.Round))
                    drawArc(p.base, 113f, 314f, false, Offset(caja0.x - g * 0.06f, caja0.y - g * 0.06f), med, style = Stroke(g * 0.78f, cap = StrokeCap.Round))
                    drawArc(lerp(p.base, p.brillo, 0.6f), 150f, 150f, false, Offset(c.x - r * 1.03f, c.y - r * 1.03f), Size(r * 2.06f, r * 2.06f), style = Stroke(g * 0.32f, cap = StrokeCap.Round))
                    // La torsión: cada vuelta del cordón, con su luz más viva del lado de la ventana y su surco.
                    var a = 118f
                    while (a < 422f) {
                        val rad = Math.toRadians(a.toDouble()); val tan = Offset(-sin(rad).toFloat(), cos(rad).toFloat()); val nor = Offset(cos(rad).toFloat(), sin(rad).toFloat())
                        val o = Offset(c.x + r * cos(rad).toFloat(), c.y + r * sin(rad).toFloat()); val d = g * 0.42f
                        val luz = ((1 + cos(rad - Math.toRadians(225.0))) / 2).toFloat()
                        drawLine(p.oscuro.copy(alpha = 0.75f), o - nor * d - tan * (d * 0.75f), o + nor * d + tan * (d * 0.75f), g * 0.16f, StrokeCap.Round)
                        val o2 = o + tan * (g * 0.24f)
                        drawLine(lerp(p.brillo, Color.White, 0.3f).copy(alpha = 0.15f + (lustre + 0.1f) * luz), o2 - nor * (d * 0.6f) - tan * (d * 0.45f), o2 + nor * (d * 0.6f) + tan * (d * 0.45f), g * 0.1f, StrokeCap.Round)
                        a += 9f
                    }
                    drawArc(umbra.copy(alpha = 0.3f), 0f, 100f, false, Offset(c.x - r * 0.93f, c.y - r * 0.93f), Size(r * 1.86f, r * 1.86f), style = Stroke(g * 0.22f, cap = StrokeCap.Round))
                    drawArc(Color.White.copy(alpha = lustre), 200f, 50f, false, Offset(c.x - r * 1.04f, c.y - r * 1.04f), Size(r * 2.08f, r * 2.08f), style = Stroke(g * 0.14f, cap = StrokeCap.Round))
                } else drawArc(p.sombra, 113f, 314f, false, caja0, med, style = Stroke(g, cap = StrokeCap.Round))
                // Los remates de tope: el cuello que se ensancha, el disco con su anillo de perlas repujadas y su brillo.
                listOf(113f, 427f).forEach { ang ->
                    val rad = Math.toRadians(ang.toDouble())
                    val o = Offset(c.x + r * cos(rad).toFloat(), c.y + r * sin(rad).toFloat())
                    if (p.hecho) drawCircle(umbra.copy(alpha = 0.3f), g * 1.25f, o + Offset(lw * 1.2f, lw * 1.4f))
                    drawCircle(metalBulto(o, g * 1.25f), g * 1.25f, o)
                    if (p.hecho) {
                        drawCircle(p.oscuro.copy(alpha = 0.7f), g * 1.25f, o, style = Stroke(lw * 0.4f))
                        for (k in 0 until 10) { val b = k * PI / 5; val q = Offset(o.x + g * 0.82f * cos(b).toFloat(), o.y + g * 0.82f * sin(b).toFloat()); drawCircle(p.oscuro.copy(alpha = 0.5f), lw * 0.3f, q + Offset(lw * 0.12f, lw * 0.12f)); drawCircle(p.brillo, lw * 0.26f, q) }
                        drawCircle(Brush.radialGradient(listOf(p.oscuro.copy(alpha = 0.6f), p.base.copy(alpha = 0f)), o + Offset(lw * 0.2f, lw * 0.2f), g * 0.5f), g * 0.5f, o)
                        drawCircle(Color.White.copy(alpha = lustre + 0.15f), g * 0.2f, o - Offset(g * 0.5f, g * 0.55f))
                    }
                }
                destello(Offset(c.x - r * 0.72f, c.y - r * 0.72f), g * 0.9f)
                // La clavija de roble de donde cuelga.
                drawCircle(bulto(madera, p.o(0.5f, 0.11f), lw * 1.6f), lw * 1.6f, p.o(0.5f, 0.11f))
            }
            "Mundo" -> {
                // El estátero arverno de pie en su soporte: el cospel grueso y desparejo, el canto de perlas que la cuña dejó
                // corrido, y el caballo al galope (el carro de Filipo que los galos volvieron un solo caballo), el mismo del sello.
                tocon(0.3f, 0.7f); sombraPie(0.34f, 0.66f)
                val c = p.o(0.5f, 0.4f); val R = p.w * 0.3f
                val cospel = Path().apply {
                    for (i in 0 until 28) {
                        val a = i * 2 * PI / 28; val rr = R * (1f + 0.022f * sin(i * 2.3).toFloat() - 0.012f * ((i * 5) % 2))
                        val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * 0.97f * sin(a)).toFloat()
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }
                // La sombra de la moneda en la pared de atrás y el canto, que asoma a la derecha por el grueso.
                sombraDe(cospel, 3.2f, 2.4f, 0.18f)
                val canto = Path().apply { addPath(cospel, Offset(lw * 1.8f, lw * 0.7f)) }
                drawPath(canto, if (p.hecho) Brush.linearGradient(listOf(p.base, p.oscuro, lerp(p.oscuro, umbra, 0.4f)), Offset(c.x - R, c.y - R), Offset(c.x + R, c.y + R)) else SolidColor(p.sombra))
                if (p.hecho) clipPath(canto) { for (k in 0 until 40) { val a = k * 2 * PI / 40; drawLine(p.oscuro.copy(alpha = 0.5f), Offset(c.x + lw * 1.8f + (R * 0.96f * cos(a)).toFloat(), c.y + lw * 0.7f + (R * 0.96f * sin(a)).toFloat()), Offset(c.x + lw * 1.8f + (R * 1.05f * cos(a)).toFloat(), c.y + lw * 0.7f + (R * 1.05f * sin(a)).toFloat()), lw * 0.25f) } }
                drawPath(cospel, metalBulto(c, R))
                if (p.hecho) {
                    // El campo un poco hundido: la sombra arriba a la izquierda y la luz abajo a la derecha, por dentro del borde.
                    clipPath(cospel) {
                        drawCircle(Brush.radialGradient(listOf(Color.Transparent, Color.Transparent, umbra.copy(alpha = 0.18f)), Offset(c.x + R * 0.08f, c.y + R * 0.1f), R * 0.86f), R * 0.86f, Offset(c.x + R * 0.08f, c.y + R * 0.1f))
                        drawArc(p.brillo.copy(alpha = 0.55f), -10f, 110f, false, Offset(c.x - R * 0.82f, c.y - R * 0.82f), Size(R * 1.64f, R * 1.64f), style = Stroke(lw * 0.6f))
                        drawArc(p.oscuro.copy(alpha = 0.45f), 170f, 110f, false, Offset(c.x - R * 0.82f, c.y - R * 0.82f), Size(R * 1.64f, R * 1.64f), style = Stroke(lw * 0.6f))
                    }
                    martillado(cospel, 0.2f, 0.12f, 0.8f, 0.68f, 30, 41, lw * 0.6f)
                    // Las perlas del canto, corridas abajo a la izquierda (el golpe no cayó al centro).
                    for (i in 0 until 26) {
                        val a = PI * 0.15 + i * PI * 1.25 / 25
                        val o = Offset(c.x - R * 0.08f + (R * 0.86f * cos(a)).toFloat(), c.y + R * 0.08f + (R * 0.86f * sin(a)).toFloat())
                        drawCircle(p.oscuro.copy(alpha = 0.6f), lw * 0.55f, o + Offset(lw * 0.2f, lw * 0.2f)); drawCircle(p.brillo, lw * 0.45f, o); drawCircle(Color.White.copy(alpha = 0.6f), lw * 0.15f, o - Offset(lw * 0.15f, lw * 0.15f))
                    }
                    // El caballo en relieve: la sombra del golpe, el cuerpo y la luz encima.
                    val ch = Offset(c.x - R * 0.02f, c.y - R * 0.08f)
                    with(TrazosGalia) {
                        caballo(ch + Offset(lw * 0.5f, lw * 0.5f), R * 1.34f, p.oscuro.copy(alpha = 0.8f), mira = 1f)
                        caballo(ch - Offset(lw * 0.3f, lw * 0.3f), R * 1.34f, lerp(p.brillo, Color.White, 0.4f), mira = 1f)
                        caballo(ch, R * 1.34f, lerp(p.base, p.brillo, 0.3f), lerp(p.oscuro, umbra, 0.3f), mira = 1f)
                    }
                    // El reflejo que barre la cara en diagonal, más vivo en cada nivel.
                    clipPath(cospel) {
                        val banda = Path().apply { moveTo(c.x - R * 1.1f, c.y - R * 0.1f); lineTo(c.x - R * 0.1f, c.y - R * 1.1f); lineTo(c.x + R * 0.15f, c.y - R * 1.1f); lineTo(c.x - R * 1.1f, c.y + R * 0.15f); close() }
                        drawPath(banda, Color.White.copy(alpha = lustre * 0.3f))
                    }
                    drawPath(cospel, p.oscuro.copy(alpha = 0.65f), style = Stroke(lw * 0.5f))
                    brillo(Offset(c.x - R * 0.72f, c.y - R * 0.35f), Offset(c.x - R * 0.42f, c.y - R * 0.74f), lw * 1.5f, lustre + 0.1f)
                    destello(Offset(c.x - R * 0.55f, c.y - R * 0.6f), lw * 3f)
                } else with(TrazosGalia) { caballo(c, R * 1.34f, p.sombra.copy(alpha = p.sombra.alpha * 1.5f), mira = 1f) }
                // El soporte de roble: una cuña con su ranura, donde la moneda se para de canto.
                val soporte = Path().apply { moveTo(p.x(0.32f), p.y(0.7f)); lineTo(p.x(0.36f), p.y(0.64f)); lineTo(p.x(0.64f), p.y(0.64f)); lineTo(p.x(0.68f), p.y(0.7f)); close() }
                sombraDe(soporte, 1.2f, 0.6f, 0.3f)
                drawPath(soporte, vol(madera, 0.32f, 0.68f))
                if (p.hecho) {
                    clipPath(soporte) { for (k in 0 until 4) drawLine(umbra.copy(alpha = 0.3f), p.o(0.33f, 0.66f + k * 0.012f), p.o(0.67f, 0.655f + k * 0.013f), lw * 0.3f) }
                    drawLine(lerp(madera, cara, 0.5f), p.o(0.36f, 0.641f), p.o(0.64f, 0.641f), lw * 0.45f)
                    drawLine(umbra.copy(alpha = 0.7f), p.o(0.37f, 0.647f), p.o(0.63f, 0.647f), lw * 0.8f)
                    rebote(soporte, cara, 0.35f)
                }
            }
            "Cuerpo" -> {
                // El carnyx de Tintignac: el tubo de bronce del metal del nivel, alto sobre la gente, con sus anillos, y arriba la
                // cabeza de jabalí con el hocico truncado, las fauces abiertas, los colmillos y la cresta; abajo, la boquilla
                // doblada. Va atado con correas a una estaca de roble clavada en el tocón.
                tocon(0.28f, 0.72f); sombraPie(0.3f, 0.52f)
                // La estaca de roble, con su corteza y la punta cortada a hacha.
                val estaca = Path().apply { moveTo(p.x(0.43f), p.y(0.7f)); lineTo(p.x(0.433f), p.y(0.33f)); lineTo(p.x(0.452f), p.y(0.31f)); lineTo(p.x(0.47f), p.y(0.325f)); lineTo(p.x(0.468f), p.y(0.7f)); close() }
                sombraDe(estaca, 1.6f, 0.8f, 0.25f)
                drawPath(estaca, vol(lerp(madera, corteza, 0.4f), 0.43f, 0.47f))
                if (p.hecho) {
                    clipPath(estaca) { for (k in 0 until 7) drawLine(umbra.copy(alpha = 0.45f), p.o(0.437f + (k % 2) * 0.016f, 0.36f + k * 0.048f), p.o(0.44f + (k % 2) * 0.016f, 0.4f + k * 0.048f), lw * 0.35f) }
                    drawPath(Path().apply { moveTo(p.x(0.433f), p.y(0.33f)); lineTo(p.x(0.452f), p.y(0.31f)); lineTo(p.x(0.47f), p.y(0.325f)) }, cara, style = Stroke(lw * 0.5f))
                }
                // El carnyx.
                val alto = p.h * 0.645f; val pie = p.o(0.35f, 0.674f)
                with(TrazosGalia) {
                    if (p.hecho) {
                        translate(lw * 1.1f, lw * 0.8f) { carnyx(pie, alto, umbra.copy(alpha = 0.18f)) }
                        carnyx(pie, alto, p.base, lerp(p.oscuro, umbra, 0.6f), 1f, p.brillo, p.oscuro)
                    } else carnyx(pie, alto, p.sombra)
                }
                // Las correas de cuero que lo atan a la estaca, con la sombra que echan.
                val cuero = p.c(Color(0xFF5A3A22))
                listOf(0.4f, 0.56f).forEach { y ->
                    if (p.hecho) drawLine(umbra.copy(alpha = 0.3f), p.o(0.335f, y + 0.012f), p.o(0.475f, y + 0.02f), lw * 1.3f, StrokeCap.Round)
                    drawLine(cuero, p.o(0.33f, y), p.o(0.472f, y + 0.008f), lw * 1.3f, StrokeCap.Round)
                    if (p.hecho) {
                        drawLine(Color.White.copy(alpha = 0.2f), p.o(0.335f, y - 0.004f), p.o(0.468f, y + 0.004f), lw * 0.35f, StrokeCap.Round)
                        drawLine(cuero, p.o(0.472f, y + 0.008f), p.o(0.49f, y + 0.045f), lw * 0.8f, StrokeCap.Round)
                    }
                }
                destello(p.o(0.47f, 0.088f), lw * 2.4f)
            }
            "Mente" -> {
                // La hoz de oro y el muérdago: el manto blanco tendido sobre el tocón, como el que recoge la rama cortada en el
                // sexto día de la luna (Plinio 16.250), la mata de muérdago en bola, con sus hojas en pares y sus bayas blancas, y
                // la hoz de oro con su mango de roble y la virola del metal del nivel.
                tocon()
                // El manto blanco: cubre el corte y cae por delante en un pliegue.
                val manto = Path().apply {
                    moveTo(p.x(0.25f), p.y(0.69f)); quadraticTo(p.x(0.5f), p.y(0.655f), p.x(0.75f), p.y(0.69f))
                    quadraticTo(p.x(0.76f), p.y(0.71f), p.x(0.7f), p.y(0.715f)); quadraticTo(p.x(0.58f), p.y(0.72f), p.x(0.52f), p.y(0.73f))
                    lineTo(p.x(0.43f), p.y(0.8f)); quadraticTo(p.x(0.36f), p.y(0.77f), p.x(0.3f), p.y(0.765f))
                    quadraticTo(p.x(0.31f), p.y(0.73f), p.x(0.25f), p.y(0.69f)); close()
                }
                sombraDe(manto, 0.9f, 1f, 0.26f)
                drawPath(manto, if (p.hecho) Brush.linearGradient(listOf(lerp(lino, Color.White, 0.5f), lino, lerp(lino, Color(0xFF8C8270), 0.45f)), p.o(0.3f, 0.66f), p.o(0.6f, 0.78f)) else SolidColor(p.sombra))
                if (p.hecho) clipPath(manto) {
                    // Los pliegues: el valle en sombra fría, el lomo con luz; y la trama del lino.
                    listOf(0.36f to 0.42f, 0.47f to 0.5f).forEach { (xa, xb) ->
                        drawLine(Color(0xFF6E6A5C).copy(alpha = 0.3f), p.o(xa, 0.705f), p.o(xb, 0.79f), lw * 1.4f, StrokeCap.Round)
                        drawLine(Color.White.copy(alpha = 0.55f), p.o(xa - 0.025f, 0.705f), p.o(xb - 0.03f, 0.785f), lw * 0.5f, StrokeCap.Round)
                    }
                    for (k in 0 until 14) drawLine(Color(0xFF8C8270).copy(alpha = 0.12f), p.o(0.24f + k * 0.04f, 0.65f), p.o(0.2f + k * 0.04f, 0.8f), lw * 0.2f)
                    drawLine(Color.White.copy(alpha = 0.7f), p.o(0.3f, 0.682f), p.o(0.6f, 0.668f), lw * 0.45f, StrokeCap.Round)
                    drawRect(Brush.verticalGradient(listOf(Color.Transparent, umbra.copy(alpha = 0.2f)), p.y(0.74f), p.y(0.8f)), Offset(p.x(0.2f), p.y(0.74f)), Size(p.x(0.6f), p.y(0.06f)))
                }
                // La mata de muérdago: tallos que se bifurcan desde el centro, hojas en pares y bayas en las horquillas.
                fun rama(o: Offset, ang: Double, largo: Float, nivel: Int) {
                    val f = Offset(o.x + (largo * cos(ang)).toFloat(), o.y + (largo * sin(ang)).toFloat())
                    drawLine(lerp(muerdago, roble, 0.4f), o, f, lw * (0.45f + 0.25f * nivel), StrokeCap.Round)
                    if (nivel == 0) {
                        listOf(-0.42, 0.42).forEach { d ->
                            val a = ang + d; val l = largo * 1.15f
                            val e = Offset(f.x + (l * cos(a)).toFloat(), f.y + (l * sin(a)).toFloat())
                            val n = Offset(-sin(a).toFloat(), cos(a).toFloat()) * (l * 0.24f)
                            val hoja = Path().apply { moveTo(f.x, f.y); quadraticTo((f.x + e.x) / 2 + n.x, (f.y + e.y) / 2 + n.y, e.x, e.y); quadraticTo((f.x + e.x) / 2 - n.x, (f.y + e.y) / 2 - n.y, f.x, f.y); close() }
                            // La hoja que mira a la luz, más clara; la de abajo, en la sombra del roble.
                            val aLuz = (sin(a) < 0.2)
                            drawPath(hoja, if (p.hecho) Brush.linearGradient(listOf(lerp(muerdago, Color.White, if (aLuz) 0.35f else 0.1f), lerp(muerdago, roble, if (aLuz) 0.35f else 0.65f)), f, e) else SolidColor(p.sombra))
                            if (p.hecho && fino) drawLine(lerp(muerdago, roble, 0.7f).copy(alpha = 0.6f), f, Offset((f.x * 0.25f + e.x * 0.75f), (f.y * 0.25f + e.y * 0.75f)), lw * 0.2f)
                        }
                    } else {
                        rama(f, ang - 0.45, largo * 0.8f, nivel - 1); rama(f, ang + 0.45, largo * 0.8f, nivel - 1)
                        drawCircle(bulto(baya, f, lw * 1.25f), lw * 1.25f, f)
                        if (p.hecho) drawCircle(Color.White.copy(alpha = 0.85f), lw * 0.35f, f - Offset(lw * 0.4f, lw * 0.4f))
                    }
                }
                val m = p.o(0.39f, 0.625f)
                listOf(-170.0, -135.0, -100.0, -65.0, -30.0, 5.0).forEachIndexed { i, g -> rama(m, Math.toRadians(g), p.w * (0.062f + (i % 2) * 0.014f), 2) }
                drawCircle(bulto(baya, m, lw * 1.4f), lw * 1.4f, m)
                // La hoz: la hoja curva de oro que sube y se vuelve como una media luna, el mango de roble y la virola del nivel.
                val hoja = Path().apply {
                    moveTo(p.x(0.6f), p.y(0.5f))
                    cubicTo(p.x(0.67f), p.y(0.35f), p.x(0.63f), p.y(0.13f), p.x(0.45f), p.y(0.115f))
                    cubicTo(p.x(0.37f), p.y(0.11f), p.x(0.31f), p.y(0.15f), p.x(0.29f), p.y(0.22f))
                    cubicTo(p.x(0.33f), p.y(0.175f), p.x(0.4f), p.y(0.16f), p.x(0.46f), p.y(0.17f))
                    cubicTo(p.x(0.58f), p.y(0.19f), p.x(0.61f), p.y(0.36f), p.x(0.565f), p.y(0.5f)); close()
                }
                sombraDe(hoja, 1.4f, 1.4f, 0.26f)
                drawPath(hoja, if (p.hecho) Brush.linearGradient(listOf(lerp(oroBrillo, Color.White, 0.2f), oro, lerp(oro, umbra, 0.45f)), p.o(0.35f, 0.15f), p.o(0.64f, 0.45f)) else SolidColor(p.sombra))
                if (p.hecho) {
                    clipPath(hoja) {
                        // El lomo grueso, más oscuro; el filo, que el afilado dejó claro; y los golpes del martillo.
                        drawPath(Path().apply { moveTo(p.x(0.605f), p.y(0.5f)); cubicTo(p.x(0.675f), p.y(0.35f), p.x(0.635f), p.y(0.125f), p.x(0.45f), p.y(0.11f)) }, lerp(oro, umbra, 0.5f).copy(alpha = 0.6f), style = Stroke(lw * 1.2f))
                        drawPath(Path().apply { moveTo(p.x(0.565f), p.y(0.5f)); cubicTo(p.x(0.61f), p.y(0.36f), p.x(0.58f), p.y(0.19f), p.x(0.46f), p.y(0.17f)); cubicTo(p.x(0.4f), p.y(0.16f), p.x(0.33f), p.y(0.175f), p.x(0.29f), p.y(0.22f)) }, lerp(oroBrillo, Color.White, 0.5f).copy(alpha = 0.85f), style = Stroke(lw * 0.9f))
                    }
                    martillado(hoja, 0.3f, 0.11f, 0.66f, 0.5f, 22, 59, lw * 0.5f)
                    drawPath(Path().apply { moveTo(p.x(0.59f), p.y(0.45f)); cubicTo(p.x(0.635f), p.y(0.33f), p.x(0.6f), p.y(0.15f), p.x(0.45f), p.y(0.13f)) }, Color.White.copy(alpha = lustre + 0.15f), style = Stroke(lw * 0.45f, cap = StrokeCap.Round))
                    drawPath(hoja, lerp(oro, umbra, 0.5f), style = Stroke(lw * 0.35f))
                    destello(p.o(0.6f, 0.2f), lw * 2.4f)
                }
                // El mango de roble, con su veta, y la virola del metal del nivel.
                drawLine(vol(madera, 0.56f, 0.62f), p.o(0.582f, 0.52f), p.o(0.6f, 0.67f), lw * 2.5f, StrokeCap.Round)
                if (p.hecho) {
                    drawLine(Color.White.copy(alpha = 0.25f), p.o(0.576f, 0.53f), p.o(0.592f, 0.66f), lw * 0.4f, StrokeCap.Round)
                    drawLine(umbra.copy(alpha = 0.4f), p.o(0.589f, 0.53f), p.o(0.606f, 0.665f), lw * 0.4f, StrokeCap.Round)
                }
                drawRect(p.metal(0.55f, 0.61f), Offset(p.x(0.555f), p.y(0.49f)), Size(p.x(0.056f), p.y(0.032f)))
                if (p.hecho) {
                    drawLine(p.brillo.copy(alpha = lustre + 0.2f), p.o(0.556f, 0.493f), p.o(0.61f, 0.493f), lw * 0.35f)
                    drawLine(umbra.copy(alpha = 0.5f), p.o(0.556f, 0.521f), p.o(0.61f, 0.521f), lw * 0.4f)
                }
            }
            "Cultura" -> {
                // El caldero de bronce del banquete, colgado de su cadena de hierro en las trébedes, con el fuego debajo: la panza
                // del metal del nivel, martillada, la banda de ondas repujadas bajo el borde, las dos argollas y el fuego que se
                // refleja en el fondo.
                tocon(0.24f, 0.76f)
                // Las trébedes de hierro, con su luz del lado de la ventana.
                listOf(0.22f to 0.7f, 0.78f to 0.7f).forEach { (x, y) ->
                    drawLine(hierro, p.o(0.5f, 0.07f), p.o(x, y), lw * 1.1f, StrokeCap.Round)
                    if (p.hecho) drawLine(Color.White.copy(alpha = 0.25f), p.o(0.498f, 0.075f), p.o(x - 0.004f, y), lw * 0.3f, StrokeCap.Round)
                }
                drawLine(lerp(hierro, umbra, 0.4f), p.o(0.5f, 0.07f), p.o(0.56f, 0.68f), lw * 0.9f, StrokeCap.Round)
                drawCircle(bulto(hierro, p.o(0.5f, 0.075f), lw * 1.3f), lw * 1.3f, p.o(0.5f, 0.075f))
                // El fuego debajo, con su luz en el corte del tocón.
                if (p.hecho) drawOval(Brush.radialGradient(listOf(TrazosGalia.FUEGO_HALO.copy(alpha = 0.45f), Color.Transparent), p.o(0.5f, 0.69f), p.w * 0.22f), Offset(p.x(0.28f), p.y(0.66f)), Size(p.x(0.44f), p.y(0.06f)))
                with(TrazosGalia) { if (p.hecho) hoguera(p.o(0.5f, 0.69f), p.w * 0.22f, 0.7f, madera) }
                // La cadena: eslabones en óvalo, el de canto y el de frente.
                for (k in 0 until 6) {
                    val y = 0.09f + k * 0.025f; val ancho = if (k % 2 == 0) 0.7f else 1.1f
                    drawOval(hierro, Offset(p.x(0.5f) - lw * ancho, p.y(y)), Size(lw * ancho * 2, p.y(0.03f)), style = Stroke(lw * 0.5f))
                    if (p.hecho && k % 2 == 1) drawArc(Color.White.copy(alpha = 0.3f), 180f, 90f, false, Offset(p.x(0.5f) - lw * ancho, p.y(y)), Size(lw * ancho * 2, p.y(0.03f)), style = Stroke(lw * 0.25f))
                }
                // El asa de hierro que va de una argolla a la otra.
                drawArc(hierro, 200f, 140f, false, Offset(p.x(0.27f), p.y(0.24f)), Size(p.x(0.46f), p.y(0.18f)), style = Stroke(lw * 0.8f))
                if (p.hecho) drawArc(Color.White.copy(alpha = 0.22f), 205f, 60f, false, Offset(p.x(0.27f), p.y(0.24f) - lw * 0.2f), Size(p.x(0.46f), p.y(0.18f)), style = Stroke(lw * 0.25f))
                // La panza.
                val c = p.o(0.5f, 0.44f)
                val panza = Path().apply {
                    moveTo(p.x(0.26f), p.y(0.33f)); lineTo(p.x(0.74f), p.y(0.33f))
                    cubicTo(p.x(0.82f), p.y(0.42f), p.x(0.74f), p.y(0.58f), p.x(0.5f), p.y(0.585f))
                    cubicTo(p.x(0.26f), p.y(0.58f), p.x(0.18f), p.y(0.42f), p.x(0.26f), p.y(0.33f)); close()
                }
                drawPath(panza, metalBulto(Offset(c.x - p.w * 0.06f, c.y - p.w * 0.06f), p.w * 0.32f))
                if (p.hecho) {
                    clipPath(panza) {
                        // La sombra propia, abajo a la derecha, y el fuego que se refleja en el fondo.
                        drawCircle(Brush.radialGradient(listOf(Color.Transparent, umbra.copy(alpha = 0.35f)), Offset(c.x - p.w * 0.08f, c.y - p.w * 0.08f), p.w * 0.36f), p.w * 0.5f, c)
                        drawOval(Brush.radialGradient(listOf(TrazosGalia.FUEGO.copy(alpha = 0.55f), TrazosGalia.FUEGO.copy(alpha = 0f)), p.o(0.5f, 0.6f), p.w * 0.24f), Offset(p.x(0.25f), p.y(0.48f)), Size(p.x(0.5f), p.y(0.14f)))
                        // La banda de ondas corridas bajo el borde, como el repujado de La Tène, con su luz y su sombra.
                        drawRect(p.oscuro.copy(alpha = 0.3f), Offset(p.x(0.2f), p.y(0.355f)), Size(p.x(0.6f), p.y(0.05f)))
                        val onda = Path().apply {
                            var x = 0.24f; moveTo(p.x(x), p.y(0.38f))
                            while (x < 0.78f) { cubicTo(p.x(x + 0.02f), p.y(0.355f), p.x(x + 0.045f), p.y(0.355f), p.x(x + 0.05f), p.y(0.38f)); cubicTo(p.x(x + 0.055f), p.y(0.405f), p.x(x + 0.08f), p.y(0.405f), p.x(x + 0.1f), p.y(0.38f)); x += 0.1f }
                        }
                        translate(lw * 0.25f, lw * 0.25f) { drawPath(onda, p.oscuro.copy(alpha = 0.8f), style = Stroke(lw * 0.55f)) }
                        drawPath(onda, p.brillo.copy(alpha = 0.9f), style = Stroke(lw * 0.45f))
                        // Las costuras de remaches donde se unen las láminas.
                        listOf(0.38f, 0.62f).forEach { x -> for (k in 0 until 5) { val o = p.o(x + (x - 0.5f) * 0.25f * k / 4f, 0.42f + k * 0.035f); drawCircle(p.oscuro.copy(alpha = 0.6f), lw * 0.35f, o + Offset(lw * 0.12f, lw * 0.12f)); drawCircle(p.brillo.copy(alpha = 0.85f), lw * 0.28f, o) } }
                    }
                    martillado(panza, 0.2f, 0.41f, 0.8f, 0.6f, 60, 67)
                    rebote(panza, lerp(p.brillo, TrazosGalia.FUEGO, 0.5f), 0.4f)
                    brillo(p.o(0.31f, 0.43f), p.o(0.34f, 0.5f), lw * 1.8f, lustre)
                    destello(p.o(0.32f, 0.42f), lw * 2.6f)
                }
                // El borde, un óvalo de boca más oscuro por dentro, y las argollas.
                drawOval(p.metal(0.25f, 0.75f), Offset(p.x(0.245f), p.y(0.315f)), Size(p.x(0.51f), p.y(0.04f)))
                drawOval(if (p.hecho) Brush.verticalGradient(listOf(lerp(p.oscuro, umbra, 0.7f), lerp(p.oscuro, umbra, 0.3f)), p.y(0.322f), p.y(0.346f)) else SolidColor(p.sombra), Offset(p.x(0.27f), p.y(0.322f)), Size(p.x(0.46f), p.y(0.024f)))
                if (p.hecho) drawArc(p.brillo.copy(alpha = lustre + 0.2f), 160f, 120f, false, Offset(p.x(0.247f), p.y(0.316f)), Size(p.x(0.506f), p.y(0.038f)), style = Stroke(lw * 0.3f))
                listOf(0.25f, 0.75f).forEach { x ->
                    drawCircle(p.oscuro, lw * 1.5f, p.o(x, 0.345f), style = Stroke(lw * 0.7f))
                    if (p.hecho) drawArc(p.brillo.copy(alpha = 0.7f), 180f, 90f, false, Offset(p.x(x) - lw * 1.5f, p.y(0.345f) - lw * 1.5f), Size(lw * 3f, lw * 3f), style = Stroke(lw * 0.3f))
                }
            }
            "Gente" -> {
                // El jabalí de bronce, de pie sobre su plaquita: el morro largo, la cruz alta con la cresta de cerdas parada, el
                // anca más baja, las patas cortas, el colmillo, la oreja, la cola enroscada y la espiral grabada en la paletilla.
                tocon(); sombraPie(0.22f, 0.78f, 0.69f)
                drawRect(p.metal(0.22f, 0.78f), Offset(p.x(0.22f), p.y(0.672f)), Size(p.x(0.56f), p.y(0.022f)))
                if (p.hecho) {
                    drawLine(p.brillo.copy(alpha = lustre + 0.15f), p.o(0.22f, 0.673f), p.o(0.78f, 0.673f), lw * 0.35f)
                    // Las sombras de las pezuñas sobre la plaquita.
                    listOf(0.288f, 0.358f, 0.632f, 0.692f).forEach { x -> drawOval(umbra.copy(alpha = 0.4f), Offset(p.x(x - 0.01f), p.y(0.666f)), Size(p.x(0.05f), p.y(0.008f))) }
                }
                // Las patas, cortas y fuertes, con sus pezuñas.
                listOf(0.3f, 0.37f, 0.62f, 0.68f).forEachIndexed { i, x ->
                    val lejos = i % 2 == 1
                    drawLine(if (lejos) SolidColor(if (p.hecho) lerp(p.oscuro, umbra, 0.3f) else p.sombra) else p.metal(x - 0.025f, x + 0.025f), p.o(x, 0.55f), p.o(x + (if (i < 2) -0.012f else 0.012f), 0.665f), lw * 2.8f, StrokeCap.Round)
                    drawLine(if (p.hecho) lerp(p.oscuro, umbra, 0.5f) else p.sombra, p.o(x - 0.012f + (if (i < 2) -0.012f else 0.012f), 0.668f), p.o(x + 0.012f + (if (i < 2) -0.012f else 0.012f), 0.668f), lw * 1.2f)
                }
                // El cuerpo y la cabeza, que mira a la izquierda: la frente sube a la cruz y el lomo baja al anca.
                val cuerpo = Path().apply {
                    moveTo(p.x(0.12f), p.y(0.5f))                                // la jeta
                    lineTo(p.x(0.13f), p.y(0.46f))
                    quadraticTo(p.x(0.24f), p.y(0.42f), p.x(0.3f), p.y(0.37f))   // la frente
                    quadraticTo(p.x(0.38f), p.y(0.31f), p.x(0.46f), p.y(0.33f))  // la cruz
                    quadraticTo(p.x(0.62f), p.y(0.36f), p.x(0.73f), p.y(0.41f))  // el lomo, que baja
                    quadraticTo(p.x(0.8f), p.y(0.45f), p.x(0.77f), p.y(0.52f))   // el anca
                    quadraticTo(p.x(0.74f), p.y(0.58f), p.x(0.62f), p.y(0.575f))
                    quadraticTo(p.x(0.5f), p.y(0.6f), p.x(0.36f), p.y(0.585f))
                    quadraticTo(p.x(0.26f), p.y(0.575f), p.x(0.2f), p.y(0.525f)) // la quijada
                    close()
                }
                sombraDe(cuerpo, 1.2f, 1.2f, 0.24f)
                // La cresta: cerdas en sierra desde la nuca hasta media espalda, más altas en la cruz.
                val cresta = Path().apply {
                    moveTo(p.x(0.28f), p.y(0.385f))
                    for (k in 0 until 9) {
                        val x = 0.28f + k * 0.045f
                        val y = 0.35f - 0.04f * sin(PI * (k + 0.5) / 10).toFloat() + (if (k > 4) (k - 4) * 0.008f else 0f)
                        lineTo(p.x(x + 0.01f), p.y(y) - lw * (2.4f + 1.8f * sin(PI * (k + 0.5) / 10).toFloat())); lineTo(p.x(x + 0.04f), p.y(y + 0.012f))
                    }
                    lineTo(p.x(0.68f), p.y(0.4f)); close()
                }
                drawPath(cuerpo, metalBulto(p.o(0.4f, 0.38f), p.w * 0.42f))
                if (p.hecho) {
                    clipPath(cuerpo) {
                        // La panza en sombra y la luz que rebota del corte del tocón por debajo.
                        drawRect(Brush.verticalGradient(listOf(Color.Transparent, umbra.copy(alpha = 0.4f)), p.y(0.46f), p.y(0.6f)), Offset(p.x(0.1f), p.y(0.46f)), Size(p.x(0.72f), p.y(0.14f)))
                        // El pelo grabado a buril, en mechones cortos que bajan del lomo.
                        val az = java.util.Random(83)
                        repeat(if (fino) 44 else 0) {
                            val x = 0.3f + az.nextFloat() * 0.45f; val y = 0.38f + az.nextFloat() * 0.16f
                            drawLine(p.oscuro.copy(alpha = 0.22f), p.o(x, y), p.o(x - 0.006f, y + 0.018f), lw * 0.2f, StrokeCap.Round)
                        }
                    }
                    rebote(cuerpo, lerp(p.brillo, cara, 0.4f), 0.5f)
                    drawPath(cuerpo, p.oscuro.copy(alpha = 0.65f), style = Stroke(lw * 0.4f))
                }
                drawPath(cresta, if (p.hecho) Brush.verticalGradient(listOf(lerp(p.brillo, Color.White, 0.2f), p.base, lerp(p.base, p.brillo, 0.3f)), p.y(0.27f), p.y(0.37f)) else SolidColor(p.sombra))
                if (p.hecho) {
                    clipPath(cresta) { for (k in 0 until 18) { val x = 0.29f + k * 0.022f; drawLine(p.oscuro.copy(alpha = 0.45f), p.o(x, 0.37f), p.o(x + 0.006f, 0.27f), lw * 0.25f) } }
                    // La espiral de la paletilla, el ojo, la oreja, el colmillo y el disco de la jeta.
                    val es = p.o(0.37f, 0.48f)
                    val espiral = Path().apply { moveTo(es.x, es.y); for (k in 1..26) { val a = k * 0.42; val rr = lw * 0.22f * k; lineTo(es.x + (rr * cos(a)).toFloat(), es.y + (rr * sin(a)).toFloat()) } }
                    translate(lw * 0.2f, lw * 0.2f) { drawPath(espiral, p.brillo.copy(alpha = 0.5f), style = Stroke(lw * 0.3f)) }
                    drawPath(espiral, p.oscuro.copy(alpha = 0.7f), style = Stroke(lw * 0.4f))
                    drawCircle(p.oscuro, lw * 0.65f, p.o(0.235f, 0.43f)); drawCircle(p.brillo, lw * 0.2f, p.o(0.232f, 0.426f))
                    drawPath(Path().apply { moveTo(p.x(0.27f), p.y(0.395f)); lineTo(p.x(0.3f), p.y(0.31f)); lineTo(p.x(0.32f), p.y(0.385f)); close() }, metalBulto(p.o(0.29f, 0.35f), lw * 3f))
                    drawPath(Path().apply { moveTo(p.x(0.28f), p.y(0.385f)); lineTo(p.x(0.298f), p.y(0.335f)); lineTo(p.x(0.31f), p.y(0.38f)); close() }, p.oscuro.copy(alpha = 0.6f))
                    drawPath(Path().apply { moveTo(p.x(0.18f), p.y(0.5f)); quadraticTo(p.x(0.17f), p.y(0.45f), p.x(0.21f), p.y(0.44f)) }, lino, style = Stroke(lw * 0.95f, cap = StrokeCap.Round))
                    drawOval(p.oscuro.copy(alpha = 0.75f), Offset(p.x(0.105f), p.y(0.455f)), Size(p.x(0.03f), p.y(0.05f)))
                    drawOval(p.brillo.copy(alpha = 0.6f), Offset(p.x(0.105f), p.y(0.455f)), Size(p.x(0.03f), p.y(0.05f)), style = Stroke(lw * 0.25f))
                    listOf(0.6f, 0.66f).forEach { x -> drawLine(p.oscuro.copy(alpha = 0.35f), p.o(x, 0.45f), p.o(x + 0.02f, 0.53f), lw * 0.4f) }
                    brillo(p.o(0.4f, 0.345f), p.o(0.58f, 0.365f), lw * 1.4f, lustre + 0.05f)
                    brillo(p.o(0.17f, 0.455f), p.o(0.26f, 0.415f), lw * 0.8f, lustre)
                    destello(p.o(0.44f, 0.345f), lw * 2.6f)
                }
                // La cola enroscada.
                drawPath(Path().apply { moveTo(p.x(0.775f), p.y(0.46f)); quadraticTo(p.x(0.85f), p.y(0.43f), p.x(0.835f), p.y(0.49f)); quadraticTo(p.x(0.81f), p.y(0.52f), p.x(0.8f), p.y(0.48f)) },
                    if (p.hecho) p.oscuro else p.sombra, style = Stroke(lw * 0.8f, cap = StrokeCap.Round))
            }
            "Constancia" -> {
                // El casco del jinete en su poste: la calota con el botón de la cimera, la banda de curvas y los botones de coral,
                // el guardanuca y las carrilleras con sus tres bollos.
                tocon(); sombraPie(0.44f, 0.56f)
                caja(p, 0.465f, 0.5f, 0.535f, 0.7f, vol(madera, 0.465f, 0.535f))
                veta(0.465f, 0.5f, 0.535f, 0.69f, 29)
                if (p.hecho) drawRect(Brush.verticalGradient(listOf(umbra.copy(alpha = 0.5f), Color.Transparent), p.y(0.44f), p.y(0.54f)), Offset(p.x(0.465f), p.y(0.44f)), Size(p.x(0.07f), p.y(0.1f)))
                // La sombra que el casco echa detrás.
                val calota = Path().apply {
                    moveTo(p.x(0.27f), p.y(0.43f)); cubicTo(p.x(0.25f), p.y(0.26f), p.x(0.36f), p.y(0.17f), p.x(0.5f), p.y(0.17f))
                    cubicTo(p.x(0.64f), p.y(0.17f), p.x(0.75f), p.y(0.26f), p.x(0.73f), p.y(0.43f)); close()
                }
                sombraDe(calota, 2.4f, 1.8f, 0.16f)
                // Las carrilleras, que cuelgan a los lados.
                listOf(-1f, 1f).forEach { s ->
                    val x0 = 0.5f + s * 0.17f
                    val carrillera = Path().apply {
                        moveTo(p.x(x0 - s * 0.01f), p.y(0.42f)); lineTo(p.x(x0 + s * 0.09f), p.y(0.42f))
                        quadraticTo(p.x(x0 + s * 0.08f), p.y(0.56f), p.x(x0 + s * 0.035f), p.y(0.62f))
                        quadraticTo(p.x(x0 - s * 0.01f), p.y(0.55f), p.x(x0 - s * 0.01f), p.y(0.42f)); close()
                    }
                    sombraDe(carrillera, 1f, 1f, 0.22f)
                    drawPath(carrillera, p.metal(minOf(x0, x0 + s * 0.09f), maxOf(x0, x0 + s * 0.09f)))
                    if (p.hecho) {
                        if (s > 0) clipPath(carrillera) { drawRect(umbra.copy(alpha = 0.22f), Offset(p.x(0.6f), p.y(0.4f)), Size(p.x(0.3f), p.y(0.25f))) }
                        martillado(carrillera, minOf(x0, x0 + s * 0.09f), 0.42f, maxOf(x0, x0 + s * 0.09f), 0.62f, 10, 71 + s.toLong())
                        drawPath(carrillera, p.oscuro.copy(alpha = 0.65f), style = Stroke(lw * 0.4f))
                        listOf(Offset(0.02f, 0.46f), Offset(0.06f, 0.47f), Offset(0.035f, 0.52f)).forEach { (dx, y) ->
                            val o = p.o(x0 + s * dx, y)
                            drawCircle(umbra.copy(alpha = 0.3f), lw * 1.2f, o + Offset(lw * 0.35f, lw * 0.35f))
                            drawCircle(metalBulto(o, lw * 1.2f), lw * 1.2f, o); drawCircle(p.oscuro.copy(alpha = 0.5f), lw * 1.2f, o, style = Stroke(lw * 0.25f))
                            drawCircle(Color.White.copy(alpha = lustre + 0.2f), lw * 0.3f, o - Offset(lw * 0.4f, lw * 0.4f))
                        }
                    }
                    drawCircle(hierro, lw * 0.6f, p.o(x0 + s * 0.02f, 0.425f))
                }
                // El guardanuca: el ala de atrás, apenas visible por los lados.
                drawOval(if (p.hecho) Brush.horizontalGradient(listOf(p.base, p.oscuro, lerp(p.oscuro, umbra, 0.5f)), p.x(0.24f), p.x(0.76f)) else SolidColor(p.sombra), Offset(p.x(0.24f), p.y(0.395f)), Size(p.x(0.52f), p.y(0.05f)))
                // La calota.
                drawPath(calota, metalBulto(p.o(0.43f, 0.25f), p.w * 0.32f))
                if (p.hecho) {
                    clipPath(calota) {
                        // La sombra propia a la derecha, con el reflejo del poste de roble, y la banda de abajo con las ondas
                        // grabadas y los botones de coral.
                        drawCircle(Brush.radialGradient(listOf(Color.Transparent, umbra.copy(alpha = 0.3f)), p.o(0.42f, 0.26f), p.w * 0.3f), p.w * 0.5f, p.o(0.5f, 0.3f))
                        drawRect(p.oscuro.copy(alpha = 0.32f), Offset(p.x(0.25f), p.y(0.37f)), Size(p.x(0.5f), p.y(0.06f)))
                        drawLine(p.brillo.copy(alpha = 0.6f), p.o(0.25f, 0.371f), p.o(0.75f, 0.371f), lw * 0.3f)
                        val onda = Path().apply {
                            var x = 0.26f; moveTo(p.x(x), p.y(0.4f))
                            while (x < 0.74f) { quadraticTo(p.x(x + 0.03f), p.y(0.375f), p.x(x + 0.06f), p.y(0.4f)); quadraticTo(p.x(x + 0.09f), p.y(0.425f), p.x(x + 0.12f), p.y(0.4f)); x += 0.12f }
                        }
                        translate(lw * 0.22f, lw * 0.22f) { drawPath(onda, p.oscuro.copy(alpha = 0.8f), style = Stroke(lw * 0.45f)) }
                        drawPath(onda, p.brillo.copy(alpha = 0.9f), style = Stroke(lw * 0.4f))
                    }
                    martillado(calota, 0.26f, 0.17f, 0.74f, 0.37f, 40, 73)
                    rebote(calota, lerp(p.brillo, cara, 0.5f), 0.4f)
                    listOf(0.32f, 0.41f, 0.5f, 0.59f, 0.68f).forEach { x ->
                        val o = p.o(x, 0.355f)
                        drawCircle(umbra.copy(alpha = 0.35f), lw, o + Offset(lw * 0.3f, lw * 0.3f))
                        drawCircle(bulto(coral, o, lw), lw, o); drawCircle(Color.White.copy(alpha = 0.7f), lw * 0.28f, o - Offset(lw * 0.35f, lw * 0.35f))
                    }
                    drawPath(calota, p.oscuro.copy(alpha = 0.65f), style = Stroke(lw * 0.4f))
                    // El brillo de la calota pulida: una ventana alargada, más viva en cada nivel.
                    drawOval(Color.White.copy(alpha = lustre * 0.7f), Offset(p.x(0.32f), p.y(0.215f)), Size(p.x(0.07f), p.y(0.1f)))
                    brillo(p.o(0.34f, 0.3f), p.o(0.4f, 0.21f), lw * 1.2f, lustre + 0.15f)
                    destello(p.o(0.37f, 0.23f), lw * 2.6f)
                }
                // El botón de la cimera.
                caja(p, 0.485f, 0.13f, 0.515f, 0.175f)
                drawCircle(metalBulto(p.o(0.5f, 0.12f), lw * 1.8f), lw * 1.8f, p.o(0.5f, 0.12f))
                if (p.hecho) drawCircle(Color.White.copy(alpha = lustre + 0.2f), lw * 0.45f, p.o(0.493f, 0.113f))
            }
            "Curiosos" -> {
                // El huevo de serpiente de Plinio (29.52–54): la bola de las culebras enlazadas y su baba, del tamaño de una
                // manzana, en su garra del metal del nivel, sobre el manto a cuadros con que se recoge en el aire; al pie, una
                // culebra enroscada.
                tocon()
                // El manto a cuadros, doblado sobre el tocón.
                val manto = Path().apply {
                    moveTo(p.x(0.24f), p.y(0.66f)); quadraticTo(p.x(0.5f), p.y(0.63f), p.x(0.76f), p.y(0.66f))
                    lineTo(p.x(0.74f), p.y(0.765f)); quadraticTo(p.x(0.62f), p.y(0.79f), p.x(0.5f), p.y(0.765f)); quadraticTo(p.x(0.38f), p.y(0.79f), p.x(0.26f), p.y(0.765f)); close()
                }
                sombraDe(manto, 1f, 1.2f, 0.28f)
                drawPath(manto, if (p.hecho) SolidColor(TrazosGalia.LANA_ROJA) else SolidColor(p.sombra))
                if (p.hecho) clipPath(manto) {
                    var x = 0.25f
                    while (x < 0.76f) { drawRect(TrazosGalia.LANA_VERDE.copy(alpha = 0.7f), Offset(p.x(x), p.y(0.62f)), Size(p.x(0.035f), p.y(0.2f))); drawLine(TrazosGalia.LANA_OCRE, p.o(x + 0.05f, 0.62f), p.o(x + 0.05f, 0.8f), lw * 0.35f); x += 0.09f }
                    listOf(0.68f, 0.73f).forEach { y -> drawRect(TrazosGalia.LANA_VERDE.copy(alpha = 0.55f), Offset(p.x(0.2f), p.y(y)), Size(p.x(0.6f), p.y(0.018f))) }
                    drawLine(TrazosGalia.LANA_OCRE, p.o(0.2f, 0.705f), p.o(0.8f, 0.705f), lw * 0.35f)
                    // La lana: el pelo de la trama, el pliegue del doblez y la luz del lado de la ventana.
                    if (fino) for (k in 0 until 40) drawLine(Color.White.copy(alpha = 0.1f), p.o(0.24f + k * 0.013f, 0.64f), p.o(0.235f + k * 0.013f, 0.79f), lw * 0.18f)
                    drawRect(Brush.verticalGradient(listOf(umbra.copy(alpha = 0.4f), Color.Transparent), p.y(0.64f), p.y(0.69f)), Offset(p.x(0.2f), p.y(0.64f)), Size(p.x(0.6f), p.y(0.05f)))
                    drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.16f), Color.Transparent, umbra.copy(alpha = 0.3f)), p.x(0.24f), p.x(0.76f)), Offset(p.x(0.2f), p.y(0.62f)), Size(p.x(0.6f), p.y(0.2f)))
                    listOf(0.5f).forEach { xp -> drawLine(umbra.copy(alpha = 0.35f), p.o(xp, 0.69f), p.o(xp, 0.77f), lw * 1.1f, StrokeCap.Round) }
                }
                // La garra: el pie y tres dedos que abrazan el huevo.
                if (p.hecho) drawOval(umbra.copy(alpha = 0.35f), Offset(p.x(0.41f), p.y(0.645f)), Size(p.x(0.22f), p.y(0.022f)))
                caja(p, 0.44f, 0.6f, 0.56f, 0.645f)
                drawOval(p.metal(0.4f, 0.6f), Offset(p.x(0.4f), p.y(0.635f)), Size(p.x(0.2f), p.y(0.025f)))
                if (p.hecho) drawArc(p.brillo.copy(alpha = lustre + 0.2f), 170f, 120f, false, Offset(p.x(0.4f), p.y(0.635f)), Size(p.x(0.2f), p.y(0.025f)), style = Stroke(lw * 0.3f))
                val c = p.o(0.5f, 0.43f); val r = p.w * 0.17f
                if (p.hecho) drawCircle(umbra.copy(alpha = 0.22f), r, c + Offset(lw * 1.6f, lw * 1.4f))
                drawCircle(bulto(Color(0xFFD9DCC4), c, r), r, c)
                if (p.hecho) clipPath(Path().apply { addOval(Rect(c, r)) }) {
                    // Las culebras enlazadas: vueltas que se cruzan, con su lado en sombra.
                    for (k in 0 until 5) {
                        val y0 = c.y - r + k * r * 0.45f
                        val vuelta = Path().apply { moveTo(c.x - r * 1.1f, y0); cubicTo(c.x - r * 0.4f, y0 - r * 0.5f, c.x + r * 0.3f, y0 + r * 0.6f, c.x + r * 1.1f, y0 + r * 0.05f) }
                        translate(lw * 0.3f, lw * 0.4f) { drawPath(vuelta, Color(0xFF3E4A2A).copy(alpha = 0.45f), style = Stroke(lw * 1.4f, cap = StrokeCap.Round)) }
                        drawPath(vuelta, Color(0xFF6E7A52).copy(alpha = 0.65f), style = Stroke(lw * 1.3f, cap = StrokeCap.Round))
                        drawPath(vuelta, Color.White.copy(alpha = 0.35f), style = Stroke(lw * 0.4f, cap = StrokeCap.Round))
                    }
                    // La baba que lo cubre: burbujas con su brillo.
                    val az = java.util.Random(13)
                    repeat(18) {
                        val o = Offset(c.x + (az.nextFloat() - 0.5f) * r * 1.7f, c.y + (az.nextFloat() - 0.5f) * r * 1.7f); val rb = lw * (0.25f + az.nextFloat() * 0.5f)
                        drawCircle(Color.White.copy(alpha = 0.55f), rb, o, style = Stroke(lw * 0.25f)); drawCircle(Color.White.copy(alpha = 0.8f), rb * 0.3f, o - Offset(rb * 0.4f, rb * 0.4f))
                    }
                    drawCircle(Brush.radialGradient(listOf(Color.Transparent, umbra.copy(alpha = 0.35f)), c - Offset(r * 0.2f, r * 0.2f), r * 1.1f), r, c)
                }
                rebote(Path().apply { addOval(Rect(c, r)) }, Color(0xFFE6D9A8), 0.4f)
                brillo(Offset(c.x - r * 0.55f, c.y - r * 0.3f), Offset(c.x - r * 0.3f, c.y - r * 0.6f), lw * 1.3f, 0.55f)
                listOf(-1f, 0f, 1f).forEach { s ->
                    val garra = Path().apply { moveTo(c.x + s * r * 0.35f - lw, c.y + r * 1.05f); quadraticTo(c.x + s * r * 0.95f, c.y + r * 0.8f, c.x + s * r * 0.8f, c.y + r * 0.2f); lineTo(c.x + s * r * 0.35f + lw, c.y + r * 1.05f); close() }
                    if (s != 0f) {
                        drawPath(garra, p.metal(0.38f, 0.62f))
                        if (p.hecho) { drawPath(garra, p.oscuro.copy(alpha = 0.6f), style = Stroke(lw * 0.3f)); drawCircle(Color.White.copy(alpha = lustre), lw * 0.4f, Offset(c.x + s * r * 0.8f, c.y + r * 0.24f)) }
                    }
                }
                drawLine(p.metal(0.48f, 0.52f), Offset(c.x, c.y + r * 1.02f), Offset(c.x, c.y + r * 0.72f), lw * 1.2f, StrokeCap.Round)
                destello(Offset(c.x - r * 0.45f, c.y - r * 0.55f), lw * 2.2f)
                // La culebra enroscada al pie de la garra, con la cabeza alzada.
                val verde = p.c(Color(0xFF55663A))
                if (p.hecho) drawArc(umbra.copy(alpha = 0.3f), 160f, 220f, false, Offset(p.x(0.365f), p.y(0.606f)), Size(p.x(0.28f), p.y(0.06f)), style = Stroke(lw * 1.2f, cap = StrokeCap.Round))
                drawArc(verde, 160f, 220f, false, Offset(p.x(0.36f), p.y(0.6f)), Size(p.x(0.28f), p.y(0.06f)), style = Stroke(lw * 1.2f, cap = StrokeCap.Round))
                if (p.hecho) drawArc(Color.White.copy(alpha = 0.3f), 200f, 120f, false, Offset(p.x(0.36f), p.y(0.6f) - lw * 0.3f), Size(p.x(0.28f), p.y(0.06f)), style = Stroke(lw * 0.3f, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(p.x(0.64f), p.y(0.625f)); quadraticTo(p.x(0.7f), p.y(0.6f), p.x(0.69f), p.y(0.56f)) }, verde, style = Stroke(lw * 1.1f, cap = StrokeCap.Round))
                drawOval(bulto(Color(0xFF55663A), p.o(0.692f, 0.552f), p.x(0.025f)), Offset(p.x(0.67f), p.y(0.54f)), Size(p.x(0.045f), p.y(0.025f)))
                if (p.hecho) drawCircle(tinta, lw * 0.3f, p.o(0.7f, 0.551f))
            }
            else -> {
                // La espada de La Tène en su vaina, de pie sobre el tocón y sacada un palmo: la empuñadura con su pomo y su guarda
                // en campana, la hoja de hierro con su nervio, la vaina del metal del nivel con el par de dragones repujado bajo
                // la boca y la contera calada; de la presilla cuelga la cadena del tahalí.
                tocon(); sombraPie(0.43f, 0.57f)
                val x0 = 0.445f; val x1 = 0.555f
                // La cadena del tahalí, que cuelga en curva, con su sombra.
                val cadena = Path().apply { moveTo(p.x(x1), p.y(0.3f)); quadraticTo(p.x(0.72f), p.y(0.5f), p.x(0.66f), p.y(0.66f)) }
                if (p.hecho) translate(lw * 0.8f, lw * 0.8f) { drawPath(cadena, umbra.copy(alpha = 0.25f), style = Stroke(lw * 0.8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(lw * 1.4f, lw * 0.6f)))) }
                drawPath(cadena, hierro, style = Stroke(lw * 0.8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(lw * 1.4f, lw * 0.6f))))
                if (p.hecho) drawPath(cadena, Color.White.copy(alpha = 0.3f), style = Stroke(lw * 0.25f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(lw * 0.6f, lw * 1.4f))))
                // La hoja que asoma, con el nervio y el filo.
                val hoja = Path().apply { moveTo(p.x(x0 + 0.012f), p.y(0.25f)); lineTo(p.x(x0 + 0.012f), p.y(0.17f)); lineTo(p.x(x1 - 0.012f), p.y(0.17f)); lineTo(p.x(x1 - 0.012f), p.y(0.25f)); close() }
                drawPath(hoja, vol(lerp(hierro, Color.White, 0.25f), x0, x1))
                if (p.hecho) {
                    drawLine(Color.White.copy(alpha = 0.55f), p.o(0.495f, 0.17f), p.o(0.495f, 0.25f), lw * 0.4f)
                    drawLine(umbra.copy(alpha = 0.4f), p.o(0.506f, 0.17f), p.o(0.506f, 0.25f), lw * 0.4f)
                    drawLine(Color.White.copy(alpha = 0.35f), p.o(x0 + 0.014f, 0.17f), p.o(x0 + 0.014f, 0.25f), lw * 0.25f)
                }
                // La guarda en campana, la empuñadura y el pomo.
                val guarda = Path().apply { moveTo(p.x(0.43f), p.y(0.17f)); quadraticTo(p.x(0.5f), p.y(0.14f), p.x(0.57f), p.y(0.17f)); lineTo(p.x(0.53f), p.y(0.145f)); lineTo(p.x(0.47f), p.y(0.145f)); close() }
                drawPath(guarda, p.metal(0.43f, 0.57f))
                caja(p, 0.483f, 0.06f, 0.517f, 0.145f, vol(lerp(madera, corteza, 0.3f), 0.483f, 0.517f))
                if (p.hecho) for (k in 0 until 5) {
                    drawLine(umbra.copy(alpha = 0.45f), p.o(0.483f, 0.07f + k * 0.017f), p.o(0.517f, 0.077f + k * 0.017f), lw * 0.35f)
                    drawLine(Color.White.copy(alpha = 0.18f), p.o(0.483f, 0.074f + k * 0.017f), p.o(0.5f, 0.077f + k * 0.017f), lw * 0.2f)
                }
                drawOval(p.metal(0.46f, 0.54f), Offset(p.x(0.46f), p.y(0.04f)), Size(p.x(0.08f), p.y(0.026f)))
                drawCircle(metalBulto(p.o(0.5f, 0.035f), lw * 1.2f), lw * 1.2f, p.o(0.5f, 0.035f))
                if (p.hecho) drawCircle(Color.White.copy(alpha = lustre + 0.2f), lw * 0.35f, p.o(0.495f, 0.03f))
                // La vaina.
                val vaina = Path().apply {
                    moveTo(p.x(x0), p.y(0.25f)); lineTo(p.x(x1), p.y(0.25f)); lineTo(p.x(x1 - 0.004f), p.y(0.62f))
                    quadraticTo(p.x(0.5f), p.y(0.7f), p.x(x0 + 0.004f), p.y(0.62f)); close()
                }
                sombraDe(vaina, 1.4f, 0.9f, 0.28f)
                drawPath(vaina, p.metal(x0, x1))
                if (p.hecho) {
                    clipPath(vaina) {
                        // La caña: el lomo del medio con su luz y su sombra; el fondo más oscuro hacia la contera.
                        drawLine(p.brillo.copy(alpha = 0.6f), p.o(0.497f, 0.33f), p.o(0.497f, 0.6f), lw * 0.35f)
                        drawLine(p.oscuro.copy(alpha = 0.6f), p.o(0.503f, 0.33f), p.o(0.503f, 0.6f), lw * 0.4f)
                        drawRect(Brush.verticalGradient(listOf(Color.Transparent, umbra.copy(alpha = 0.3f)), p.y(0.45f), p.y(0.66f)), Offset(p.x(x0), p.y(0.45f)), Size(p.x(x1 - x0), p.y(0.21f)))
                    }
                    martillado(vaina, x0, 0.32f, x1, 0.62f, 22, 97, lw * 0.5f)
                    // El par de dragones: dos eses que se miran bajo la boca, repujadas (la sombra y la luz del relieve).
                    listOf(-1f, 1f).forEach { s ->
                        val ese = Path().apply {
                            moveTo(p.x(0.5f), p.y(0.27f)); cubicTo(p.x(0.5f + s * 0.05f), p.y(0.26f), p.x(0.5f + s * 0.055f), p.y(0.31f), p.x(0.5f + s * 0.02f), p.y(0.305f))
                            quadraticTo(p.x(0.5f + s * 0.005f), p.y(0.3f), p.x(0.5f + s * 0.018f), p.y(0.29f))
                        }
                        translate(lw * 0.2f, lw * 0.2f) { drawPath(ese, p.oscuro.copy(alpha = 0.85f), style = Stroke(lw * 0.5f, cap = StrokeCap.Round)) }
                        drawPath(ese, p.brillo, style = Stroke(lw * 0.35f, cap = StrokeCap.Round))
                    }
                    drawLine(p.oscuro.copy(alpha = 0.7f), p.o(x0, 0.255f), p.o(x1, 0.255f), lw * 0.5f)
                    drawLine(p.brillo.copy(alpha = 0.7f), p.o(x0, 0.251f), p.o(x1, 0.251f), lw * 0.25f)
                    rebote(vaina, lerp(p.brillo, cara, 0.4f), 0.4f)
                    brillo(p.o(x0 + 0.015f, 0.34f), p.o(x0 + 0.015f, 0.58f), lw * 0.7f, lustre)
                    destello(p.o(x0 + 0.016f, 0.36f), lw * 2.2f)
                }
                // La presilla del tahalí y la contera calada.
                caja(p, x1 - 0.002f, 0.29f, x1 + 0.018f, 0.31f)
                drawPath(Path().apply { moveTo(p.x(x0 + 0.004f), p.y(0.6f)); quadraticTo(p.x(0.5f), p.y(0.7f), p.x(x1 - 0.004f), p.y(0.6f)); quadraticTo(p.x(0.5f), p.y(0.64f), p.x(x0 + 0.004f), p.y(0.6f)); close() },
                    if (p.hecho) SolidColor(p.oscuro) else SolidColor(p.sombra))
                if (p.hecho) listOf(-1f, 1f).forEach { s -> drawCircle(umbra.copy(alpha = 0.6f), lw * 0.5f, p.o(0.5f + s * 0.025f, 0.628f)); drawCircle(p.brillo.copy(alpha = 0.85f), lw * 0.4f, p.o(0.5f + s * 0.025f, 0.625f)) }
                caja(p, 0.49f, 0.66f, 0.51f, 0.7f, if (p.hecho) SolidColor(p.oscuro) else SolidColor(p.sombra))
            }
        }
    }

    /**
     * Arcontes: los amuletos y los objetos del iniciado, cada uno sobre su sillar de caliza tallado a cincel, con la luz que
     * entra por arriba a la izquierda: la jarra sellada, el disco de las siete esferas, el amuleto de Abraxas en jaspe, el
     * anillo de Salomón (liso: la estrella del sello es medieval), el códice de cuero, la lámpara de pico, la laminilla de
     * las contraseñas, el sello de los arcontes y el cálamo con su tintero.
     */
    internal fun DrawScope.arcontes(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val caliza = p.c(TrazosArcontes.CALIZA); val calizaSombra = p.c(TrazosArcontes.CALIZA_SOMBRA); val lino = p.c(TrazosArcontes.LINO)
        val barro = p.c(TrazosArcontes.BARRO); val barroClaro = p.c(TrazosArcontes.BARRO_CLARO); val oro = p.c(TrazosArcontes.ORO_DEMIURGO)
        val jaspe = p.c(TrazosArcontes.JASPE_VERDE); val jaspeRojo = p.c(TrazosArcontes.JASPE_ROJO)
        val cuero = p.c(TrazosArcontes.CUERO); val correa = p.c(TrazosArcontes.CORREA); val lago = p.c(TrazosArcontes.LAGO)
        val tinta = p.c(Color(0xFF231A14)); val papiro = p.c(Color(0xFFDCC594)); val betun = p.c(Color(0xFF2B211B))
        /** Volumen con la luz desde arriba a la izquierda: el claro, el tono y la sombra, de [x0] a [x1]. */
        fun vol(c: Color, x0: Float, x1: Float): Brush = if (p.hecho) Brush.horizontalGradient(listOf(lerp(c, Color.White, 0.24f), c, c, lerp(c, Color.Black, 0.42f)), p.x(x0), p.x(x1)) else SolidColor(p.sombra)
        /** Volumen redondo: la luz arriba a la izquierda del centro [o]. */
        fun bulto(c: Color, o: Offset, r: Float): Brush = if (p.hecho) Brush.radialGradient(listOf(lerp(c, Color.White, 0.3f), c, lerp(c, Color.Black, 0.45f)), Offset(o.x - r * 0.35f, o.y - r * 0.4f), r * 1.55f) else SolidColor(p.sombra)
        /** El metal del nivel en volumen redondo. */
        fun metalBulto(o: Offset, r: Float): Brush = if (p.hecho) Brush.radialGradient(listOf(p.brillo, p.base, p.oscuro), Offset(o.x - r * 0.35f, o.y - r * 0.4f), r * 1.6f) else SolidColor(p.sombra)
        /** La sombra que la pieza echa sobre el sillar, corrida a la derecha. */
        fun sombraPie(x0: Float, x1: Float, y: Float = 0.7f) {
            if (p.hecho) drawOval(Color.Black.copy(alpha = 0.22f), Offset(p.x(x0 + 0.05f), p.y(y) - p.y(0.016f)), Size(p.x(x1 - x0), p.y(0.026f)))
        }
        /** El sillar de caliza: la cara de arriba más clara, el filete del metal del nivel, las huellas del cincel y la arista en sombra. */
        fun basa(x0: Float = 0.27f, x1: Float = 0.73f, y0: Float = 0.7f) {
            val y1 = 0.775f
            caja(p, x0, y0, x1, y1, vol(caliza, x0, x1))
            caja(p, x0 - 0.012f, y0 - 0.014f, x1 + 0.012f, y0, if (p.hecho) SolidColor(lerp(caliza, Color.White, 0.32f)) else SolidColor(p.sombra))
            drawRect(p.metal(x0, x1), Offset(p.x(x0), p.y(y0)), Size(p.x(x1 - x0), p.y(0.009f)))
            if (p.hecho) {
                var x = x0 + 0.03f
                while (x < x1 - 0.04f) { drawLine(calizaSombra.copy(alpha = 0.55f), p.o(x, y0 + 0.024f), p.o(x + 0.02f, y1 - 0.014f), lw * 0.32f); x += 0.034f }
                drawRect(Color.Black.copy(alpha = 0.16f), Offset(p.x(x1 - 0.035f), p.y(y0)), Size(p.x(0.035f), p.y(y1 - y0)))
                drawLine(Color.Black.copy(alpha = 0.25f), p.o(x0 - 0.012f, y0), p.o(x1 + 0.012f, y0), lw * 0.3f)
            }
        }
        /** Un brillo en la superficie: un trazo blanco y suave, solo si ya se logró. */
        fun brillo(a: Offset, b: Offset, ancho: Float, alfa: Float = 0.3f) { if (p.hecho) drawLine(Color.White.copy(alpha = alfa), a, b, ancho, StrokeCap.Round) }
        when (cat) {
            "Diario" -> {
                // La jarra sellada, como la que guardó los códices de Nag Hammadi: alta, de barro, con las franjas de ocre
                // en el hombro, la tapa de cuenco sellada con betún, el terrón del sello y el cordel atado al cuello.
                basa(); sombraPie(0.3f, 0.7f)
                val jarra = Path().apply {
                    moveTo(p.x(0.43f), p.y(0.7f))
                    cubicTo(p.x(0.29f), p.y(0.66f), p.x(0.24f), p.y(0.44f), p.x(0.27f), p.y(0.33f))
                    cubicTo(p.x(0.3f), p.y(0.26f), p.x(0.38f), p.y(0.23f), p.x(0.42f), p.y(0.21f))
                    lineTo(p.x(0.42f), p.y(0.16f)); lineTo(p.x(0.58f), p.y(0.16f)); lineTo(p.x(0.58f), p.y(0.21f))
                    cubicTo(p.x(0.62f), p.y(0.23f), p.x(0.7f), p.y(0.26f), p.x(0.73f), p.y(0.33f))
                    cubicTo(p.x(0.76f), p.y(0.44f), p.x(0.71f), p.y(0.66f), p.x(0.57f), p.y(0.7f))
                    close()
                }
                drawPath(jarra, vol(barroClaro, 0.25f, 0.75f))
                if (p.hecho) clipPath(jarra) {
                    // Las vueltas del torno y las dos franjas pintadas con ocre en el hombro.
                    for (k in 0 until 9) { val y = 0.36f + k * 0.036f; drawLine(barro.copy(alpha = 0.22f), p.o(0.2f, y), p.o(0.8f, y + 0.004f), lw * 0.3f) }
                    caja(p, 0.2f, 0.285f, 0.8f, 0.302f, SolidColor(barro.copy(alpha = 0.85f)))
                    caja(p, 0.2f, 0.318f, 0.8f, 0.326f, SolidColor(barro.copy(alpha = 0.7f)))
                    drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.28f)), p.y(0.55f), p.y(0.7f)), Offset(p.x(0.2f), p.y(0.55f)), Size(p.x(0.6f), p.y(0.15f)))
                }
                brillo(p.o(0.32f, 0.36f), p.o(0.315f, 0.55f), lw * 2.2f, 0.22f)
                // El borde del cuello y la tapa de cuenco, sellada con betún.
                caja(p, 0.405f, 0.155f, 0.595f, 0.175f, vol(barro, 0.4f, 0.6f))
                val tapa = Path().apply { moveTo(p.x(0.37f), p.y(0.162f)); quadraticTo(p.x(0.5f), p.y(0.05f), p.x(0.63f), p.y(0.162f)); close() }
                drawPath(tapa, vol(barroClaro, 0.37f, 0.63f))
                drawLine(betun, p.o(0.38f, 0.163f), p.o(0.62f, 0.163f), lw * 1.5f, StrokeCap.Round)
                if (p.hecho) for (k in 0 until 3) drawLine(betun.copy(alpha = 0.6f), p.o(0.42f + k * 0.07f, 0.163f), p.o(0.43f + k * 0.07f, 0.19f + (k % 2) * 0.012f), lw * 0.6f, StrokeCap.Round)
                // El terrón de barro del sello, con su marca redonda.
                val sello = p.o(0.5f, 0.118f)
                drawCircle(bulto(barro, sello, p.w * 0.04f), p.w * 0.04f, sello)
                drawCircle(betun.copy(alpha = 0.55f), p.w * 0.022f, sello, style = Stroke(lw * 0.45f))
                // El cordel atado al cuello, en el metal del nivel, con sus dos cabos.
                listOf(0.185f, 0.2f).forEach { y -> drawRect(p.metal(0.42f, 0.58f), Offset(p.x(0.415f), p.y(y)), Size(p.x(0.17f), lw * 0.9f)) }
                val cabo = Path().apply { moveTo(p.x(0.585f), p.y(0.195f)); quadraticTo(p.x(0.66f), p.y(0.22f), p.x(0.64f), p.y(0.3f)); moveTo(p.x(0.585f), p.y(0.2f)); quadraticTo(p.x(0.62f), p.y(0.26f), p.x(0.6f), p.y(0.33f)) }
                drawPath(cabo, p.metal(0.58f, 0.67f), style = Stroke(lw * 0.85f, cap = StrokeCap.Round))
            }
            "Mundo" -> {
                // El disco de las siete esferas, en el metal del nivel: grabados, los siete círculos de los planetas alrededor
                // de la tierra (como el diagrama de los ofitas, recurso), cada uno con su astro, y por fuera la serpiente que
                // se muerde la cola. Va de pie sobre su vástago.
                basa(0.3f, 0.7f); sombraPie(0.38f, 0.62f)
                caja(p, 0.465f, 0.6f, 0.535f, 0.7f, p.metal(0.465f, 0.535f))
                drawOval(p.metal(0.42f, 0.58f), Offset(p.x(0.42f), p.y(0.672f)), Size(p.x(0.16f), p.y(0.03f)))
                val c = p.o(0.5f, 0.365f); val r = p.w * 0.3f
                drawCircle(metalBulto(c, r), r, c)
                drawCircle(p.linea, r, c, style = Stroke(lw * 0.7f))
                drawCircle(p.linea.copy(alpha = p.linea.alpha * 0.6f), r * 0.93f, c, style = Stroke(lw * 0.4f))
                with(TrazosArcontes) { ouroboros(c, r * 0.84f, r * 0.1f, p.linea.copy(alpha = p.linea.alpha * 0.85f), -60f, if (p.hecho) p.brillo.copy(alpha = 0.5f) else null) }
                val astros = listOf(200f, 320f, 60f, 140f, 250f, 20f, 105f)
                for (i in 0 until 7) {
                    val ri = r * (0.17f + 0.083f * i)
                    drawCircle(p.linea.copy(alpha = p.linea.alpha * 0.75f), ri, c, style = Stroke(lw * 0.32f))
                    val a = Math.toRadians(astros[i].toDouble())
                    val q = Offset(c.x + ri * cos(a).toFloat(), c.y + ri * sin(a).toFloat())
                    when (i) {
                        0 -> { drawCircle(p.linea, lw * 0.9f, q); drawCircle(if (p.hecho) p.brillo else p.sombra, lw * 0.75f, Offset(q.x + lw * 0.45f, q.y - lw * 0.2f)) }   // la Luna, en creciente
                        3 -> { drawCircle(oro, lw * 1.1f, q); for (k in 0 until 8) { val b = k * PI / 4; drawLine(oro, Offset(q.x + lw * 1.5f * cos(b).toFloat(), q.y + lw * 1.5f * sin(b).toFloat()), Offset(q.x + lw * 2.3f * cos(b).toFloat(), q.y + lw * 2.3f * sin(b).toFloat()), lw * 0.3f) } }   // el Sol, en el oro de abajo
                        4 -> drawCircle(jaspeRojo, lw * 0.85f, q)   // Marte
                        else -> drawCircle(p.linea, lw * (0.55f + 0.08f * i), q)
                    }
                }
                drawCircle(lago, r * 0.1f, c)
                drawCircle(p.linea, r * 0.1f, c, style = Stroke(lw * 0.3f))
                brillo(Offset(c.x - r * 0.6f, c.y - r * 0.55f), Offset(c.x - r * 0.25f, c.y - r * 0.8f), lw * 1.6f, 0.35f)
            }
            "Cuerpo" -> {
                // El amuleto de Abraxas, como las gemas antiguas: el cabujón de jaspe verde con sus motas rojas, engastado en el
                // metal del nivel y grabado con la figura de cabeza de gallo, coraza, piernas de serpiente, el látigo y el
                // escudo. Cuelga de su argolla y descansa en dos garras sobre el sillar.
                basa(0.3f, 0.7f); sombraPie(0.36f, 0.64f)
                listOf(-1f, 1f).forEach { s -> drawLine(p.metal(0.4f, 0.6f), p.o(0.5f + s * 0.1f, 0.7f), p.o(0.5f + s * 0.075f, 0.6f), lw * 1.5f, StrokeCap.Round) }
                val g = p.o(0.5f, 0.39f); val rg = p.w * 0.24f
                drawCircle(p.metal(0.46f, 0.54f), p.w * 0.04f, Offset(g.x, g.y - rg * 1.22f), style = Stroke(lw * 1.3f))
                drawCircle(metalBulto(g, rg * 1.12f), rg * 1.12f, g)
                if (p.hecho) {
                    for (i in 0 until 40) { val a = 2 * PI * i / 40; drawCircle(lerp(p.base, p.brillo, 0.6f), lw * 0.32f, Offset(g.x + rg * 1.06f * cos(a).toFloat(), g.y + rg * 1.06f * sin(a).toFloat())) }
                    drawCircle(p.oscuro.copy(alpha = 0.6f), rg * 1.12f, g, style = Stroke(lw * 0.35f))
                    with(TrazosArcontes) {
                        gemaJaspe(g, rg)
                        val d = maxOf(rg * 0.012f, 0.6f)
                        abraxas(Offset(g.x - rg * 0.02f - d, g.y + rg * 0.02f - d), rg * 1.06f, GRABADO_SOMBRA.copy(alpha = 0.7f), GRABADO_SOMBRA.copy(alpha = 0f))
                        abraxas(Offset(g.x - rg * 0.02f, g.y + rg * 0.02f), rg * 1.06f, GRABADO.copy(alpha = 0.92f), GRABADO_HUECO)
                    }
                } else {
                    drawCircle(p.sombra, rg, g)
                    with(TrazosArcontes) { abraxas(Offset(g.x - rg * 0.02f, g.y + rg * 0.02f), rg * 1.06f, p.sombra, p.sombra) }
                }
            }
            "Mente" -> {
                // El anillo de Salomón, el que según el Testamento de Salomón mandaba a los demonios: liso, en el metal del
                // nivel, con su chatón pulido y sin grabar (la estrella del sello es medieval), sobre un cojín de lino. Detrás,
                // en la pared, la sombra de un arconte que apenas se adivina.
                if (p.hecho) with(TrazosArcontes) { sombraArconte(p.o(0.84f, 0.24f), p.h * 0.11f, SOMBRA.copy(alpha = 0.1f), -1f, desvanece = true) }
                basa(0.27f, 0.73f)
                val cojin = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(p.r(0.3f, 0.6f, 0.7f, 0.705f), CornerRadius(p.w * 0.05f))) }
                sombraPie(0.32f, 0.72f, 0.705f)
                drawPath(cojin, if (p.hecho) Brush.verticalGradient(listOf(lerp(lino, Color.White, 0.3f), lino, lerp(lino, Color.Black, 0.25f)), p.y(0.6f), p.y(0.705f)) else SolidColor(p.sombra))
                if (p.hecho) {
                    drawLine(calizaSombra.copy(alpha = 0.5f), p.o(0.33f, 0.655f), p.o(0.67f, 0.655f), lw * 0.3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(lw * 0.8f, lw * 0.6f)))
                    listOf(0.31f, 0.69f).forEach { x -> drawLine(barro, p.o(x, 0.69f), p.o(x + (if (x < 0.5f) -0.02f else 0.02f), 0.73f), lw * 0.7f, StrokeCap.Round) }
                }
                // El aro, de pie: la mitad de atrás más oscura y la de adelante con su brillo.
                val c = p.o(0.5f, 0.45f); val ax = p.w * 0.16f; val ay = p.h * 0.135f
                val caja0 = Offset(c.x - ax, c.y - ay); val med = Size(ax * 2, ay * 2)
                drawArc(if (p.hecho) SolidColor(p.oscuro) else SolidColor(p.sombra), 180f, 180f, false, caja0, med, style = Stroke(lw * 2.6f))
                drawArc(p.metal(0.33f, 0.67f), 0f, 180f, false, caja0, med, style = Stroke(lw * 3.2f))
                drawArc(p.metal(0.33f, 0.67f), 180f, 180f, false, Offset(caja0.x + lw * 0.3f, caja0.y + lw * 0.3f), Size(med.width - lw * 0.6f, med.height - lw * 0.6f), style = Stroke(lw * 2f))
                if (p.hecho) drawArc(Color.White.copy(alpha = 0.45f), 120f, 50f, false, Offset(caja0.x + lw * 0.6f, caja0.y + lw * 0.6f), Size(med.width - lw * 1.2f, med.height - lw * 1.2f), style = Stroke(lw * 0.6f, cap = StrokeCap.Round))
                // Los hombros y el chatón ovalado, liso y pulido.
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(c.x + s * ax * 0.55f, c.y - ay * 0.8f); lineTo(c.x + s * ax * 0.18f, c.y - ay * 1.22f); lineTo(c.x + s * ax * 0.05f, c.y - ay * 0.95f); close() }, p.metal(0.38f, 0.62f)) }
                val chaton = Rect(c.x - ax * 0.62f, c.y - ay * 1.42f, c.x + ax * 0.62f, c.y - ay * 0.98f)
                drawOval(p.metal(0.4f, 0.6f), chaton.topLeft, chaton.size)
                drawOval(if (p.hecho) SolidColor(lerp(p.base, p.brillo, 0.4f)) else SolidColor(p.sombra), Offset(chaton.left + chaton.width * 0.12f, chaton.top + chaton.height * 0.18f), Size(chaton.width * 0.76f, chaton.height * 0.64f))
                if (p.hecho) {
                    drawOval(p.oscuro.copy(alpha = 0.6f), chaton.topLeft, chaton.size, style = Stroke(lw * 0.35f))
                    drawOval(Color.White.copy(alpha = 0.6f), Offset(chaton.left + chaton.width * 0.22f, chaton.top + chaton.height * 0.26f), Size(chaton.width * 0.26f, chaton.height * 0.22f))
                }
            }
            "Cultura" -> {
                // El códice de cuero, como los de Nag Hammadi: las hojas de papiro asomando por el canto, la tapa con su
                // solapa doblada encima, el filete grabado y la correa que le da la vuelta y cuelga con su cuenta de metal.
                basa(0.24f, 0.76f); sombraPie(0.28f, 0.74f)
                caja(p, 0.3f, 0.19f, 0.73f, 0.695f, SolidColor(lerp(papiro, Color.Black, 0.08f)))
                if (p.hecho) for (k in 0 until 9) { val x = 0.7f + k * 0.0035f; drawLine(lerp(papiro, Color.Black, 0.25f).copy(alpha = 0.5f), p.o(x, 0.2f), p.o(x, 0.69f), lw * 0.18f) }
                val tapa = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(p.r(0.25f, 0.16f, 0.7f, 0.69f), CornerRadius(p.w * 0.012f))) }
                drawPath(tapa, vol(cuero, 0.25f, 0.7f))
                if (p.hecho) {
                    caja(p, 0.25f, 0.16f, 0.29f, 0.69f, SolidColor(Color.Black.copy(alpha = 0.18f)))
                    drawRect(lerp(cuero, Color.Black, 0.4f), Offset(p.x(0.31f), p.y(0.2f)), Size(p.x(0.34f), p.y(0.45f)), style = Stroke(lw * 0.35f))
                    drawRect(lerp(cuero, Color.White, 0.18f), Offset(p.x(0.31f) + lw * 0.3f, p.y(0.2f) + lw * 0.3f), Size(p.x(0.34f), p.y(0.45f)), style = Stroke(lw * 0.2f))
                    val az = java.util.Random(5)
                    repeat(14) { drawCircle(Color.Black.copy(alpha = 0.12f), lw * (0.3f + az.nextFloat() * 0.6f), p.o(0.28f + az.nextFloat() * 0.4f, 0.18f + az.nextFloat() * 0.48f)) }
                }
                // La solapa: el triángulo de cuero que se dobla sobre la tapa, con su pespunte.
                val solapa = Path().apply { moveTo(p.x(0.7f), p.y(0.24f)); lineTo(p.x(0.44f), p.y(0.425f)); lineTo(p.x(0.7f), p.y(0.6f)); close() }
                if (p.hecho) drawPath(Path().apply { addPath(solapa, Offset(-lw * 0.6f, lw * 0.9f)) }, Color.Black.copy(alpha = 0.25f))
                drawPath(solapa, vol(lerp(cuero, Color.White, 0.08f), 0.44f, 0.7f))
                if (p.hecho) {
                    val dentro = Path().apply { moveTo(p.x(0.69f), p.y(0.265f)); lineTo(p.x(0.475f), p.y(0.425f)); lineTo(p.x(0.69f), p.y(0.58f)) }
                    drawPath(dentro, lino.copy(alpha = 0.55f), style = Stroke(lw * 0.3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(lw * 0.9f, lw * 0.6f))))
                }
                // La correa: da la vuelta a la tapa desde la punta de la solapa y cuelga por el lomo con su cuenta.
                drawLine(correa, p.o(0.25f, 0.425f), p.o(0.45f, 0.425f), lw * 1.2f)
                drawLine(correa, p.o(0.25f, 0.43f), p.o(0.215f, 0.47f), lw * 1.1f, StrokeCap.Round)
                val cuelga = Path().apply { moveTo(p.x(0.215f), p.y(0.47f)); quadraticTo(p.x(0.19f), p.y(0.55f), p.x(0.21f), p.y(0.62f)) }
                drawPath(cuelga, correa, style = Stroke(lw * 0.9f, cap = StrokeCap.Round))
                drawCircle(metalBulto(p.o(0.21f, 0.64f), p.w * 0.022f), p.w * 0.022f, p.o(0.21f, 0.64f))
                brillo(p.o(0.27f, 0.2f), p.o(0.27f, 0.62f), lw * 0.8f, 0.18f)
            }
            "Gente" -> {
                // La lámpara de pico de la reunión escondida: la de barro de las casas de Galilea, encendida sobre su pie de
                // metal (el plato, el fuste con su nudo y la base), con el halo de la llama en la pared.
                basa(0.27f, 0.73f); sombraPie(0.36f, 0.66f)
                val c = p.o(0.47f, 0.395f); val ancho = p.w * 0.52f
                if (p.hecho) {
                    val llama = Offset(c.x + ancho * 0.39f, c.y - ancho * 0.36f * 0.4f - ancho * 0.12f)
                    drawCircle(Brush.radialGradient(listOf(TrazosArcontes.LLAMA_HALO.copy(alpha = 0.42f), TrazosArcontes.LLAMA_HALO.copy(alpha = 0.12f), Color.Transparent), llama, p.w * 0.42f), p.w * 0.42f, llama)
                }
                drawOval(p.metal(0.35f, 0.65f), Offset(p.x(0.35f), p.y(0.672f)), Size(p.x(0.3f), p.y(0.036f)))
                caja(p, 0.478f, 0.47f, 0.522f, 0.685f, p.metal(0.478f, 0.522f))
                drawOval(p.metal(0.45f, 0.55f), Offset(p.x(0.45f), p.y(0.555f)), Size(p.x(0.1f), p.y(0.03f)))
                drawOval(p.metal(0.31f, 0.69f), Offset(p.x(0.31f), p.y(0.445f)), Size(p.x(0.38f), p.y(0.04f)))
                if (p.hecho) drawOval(p.oscuro.copy(alpha = 0.5f), Offset(p.x(0.31f), p.y(0.445f)), Size(p.x(0.38f), p.y(0.04f)), style = Stroke(lw * 0.3f))
                with(TrazosArcontes) { lampara(c, ancho, barroClaro, if (p.hecho) 1f else 0f) }
            }
            "Constancia" -> {
                // La laminilla de las contraseñas: la hoja delgada del metal del nivel, grabada con siete renglones (una
                // contraseña por esfera), que sale desenrollada de su estuche colgante, con el rizo del extremo de abajo.
                basa(0.27f, 0.73f); sombraPie(0.32f, 0.68f)
                val hoja = Path().apply {
                    moveTo(p.x(0.32f), p.y(0.22f)); lineTo(p.x(0.68f), p.y(0.22f))
                    lineTo(p.x(0.685f), p.y(0.43f)); lineTo(p.x(0.675f), p.y(0.62f)); lineTo(p.x(0.325f), p.y(0.62f)); lineTo(p.x(0.315f), p.y(0.42f)); close()
                }
                drawPath(hoja, p.metal(0.31f, 0.69f))
                if (p.hecho) clipPath(hoja) {
                    // Las arrugas de la hoja de metal y el reflejo en diagonal.
                    listOf(0.4f, 0.47f, 0.58f, 0.63f).forEach { x -> drawLine(p.oscuro.copy(alpha = 0.18f), p.o(x, 0.22f), p.o(x + 0.01f, 0.62f), lw * 0.45f) }
                    drawPath(Path().apply { moveTo(p.x(0.36f), p.y(0.62f)); lineTo(p.x(0.48f), p.y(0.62f)); lineTo(p.x(0.68f), p.y(0.25f)); lineTo(p.x(0.56f), p.y(0.25f)); close() }, Color.White.copy(alpha = 0.18f))
                }
                // Los siete renglones de letras grabadas, cada uno con su punto al comienzo.
                val az = java.util.Random(7)
                for (k in 0 until 7) {
                    val y = p.y(0.255f + k * 0.05f)
                    drawCircle(p.linea, lw * 0.45f, Offset(p.x(0.345f), y))
                    var x = p.x(0.375f); val fin = p.x(if (k % 3 == 2) 0.56f else 0.65f)
                    while (x < fin) {
                        val a = lw * 0.9f
                        when (az.nextInt(5)) {
                            0 -> drawLine(p.linea, Offset(x, y - a), Offset(x, y + a), lw * 0.3f)
                            1 -> drawCircle(p.linea, a * 0.7f, Offset(x + a * 0.4f, y), style = Stroke(lw * 0.28f))
                            2 -> { drawLine(p.linea, Offset(x, y + a), Offset(x + a * 0.6f, y - a), lw * 0.3f); drawLine(p.linea, Offset(x + a * 0.6f, y - a), Offset(x + a * 1.2f, y + a), lw * 0.3f) }
                            3 -> { drawLine(p.linea, Offset(x, y - a), Offset(x + a, y - a), lw * 0.3f); drawLine(p.linea, Offset(x + a * 0.5f, y - a), Offset(x + a * 0.5f, y + a), lw * 0.3f) }
                            else -> { drawLine(p.linea, Offset(x, y - a), Offset(x, y + a), lw * 0.3f); drawLine(p.linea, Offset(x, y), Offset(x + a, y), lw * 0.3f) }
                        }
                        x += lw * 2.1f
                    }
                }
                // El rizo de abajo, donde la hoja aún se enrolla, y su sombra.
                val rizo = Rect(p.x(0.32f), p.y(0.6f), p.x(0.68f), p.y(0.665f))
                drawRoundRect(if (p.hecho) Brush.verticalGradient(listOf(p.brillo, p.base, p.oscuro), rizo.top, rizo.bottom) else SolidColor(p.sombra), rizo.topLeft, rizo.size, CornerRadius(rizo.height / 2))
                if (p.hecho) drawArc(p.oscuro.copy(alpha = 0.6f), 90f, 270f, false, Offset(rizo.right - rizo.height * 0.9f, rizo.top + rizo.height * 0.1f), Size(rizo.height * 0.8f, rizo.height * 0.8f), style = Stroke(lw * 0.4f))
                // El estuche: el tubo con sus remates y las dos anillas de donde cuelga.
                listOf(0.37f, 0.63f).forEach { x -> drawCircle(p.metal(x - 0.03f, x + 0.03f), p.w * 0.026f, p.o(x, 0.135f), style = Stroke(lw * 0.9f)) }
                val tubo = Rect(p.x(0.28f), p.y(0.155f), p.x(0.72f), p.y(0.232f))
                drawRoundRect(if (p.hecho) Brush.verticalGradient(listOf(p.brillo, p.base, p.oscuro), tubo.top, tubo.bottom) else SolidColor(p.sombra), tubo.topLeft, tubo.size, CornerRadius(tubo.height / 2))
                listOf(0.3f, 0.7f).forEach { x -> drawLine(p.linea, p.o(x, 0.158f), p.o(x, 0.229f), lw * 0.5f) }
                if (p.hecho) for (k in 0 until 6) drawLine(p.oscuro.copy(alpha = 0.4f), p.o(0.36f + k * 0.055f, 0.165f), p.o(0.36f + k * 0.055f, 0.222f), lw * 0.25f)
            }
            "Curiosos" -> {
                // El sello de los arcontes: una bula de barro, atravesada por su cordel de lino, con la impronta dorada (el oro
                // de más del mundo de abajo) de la serpiente con cara de león, como describe el Apócrifo de Juan a Yaldabaoth.
                basa(0.27f, 0.73f); sombraPie(0.32f, 0.68f)
                val c = p.o(0.5f, 0.4f); val r = p.w * 0.27f
                // El cordel sale por los dos lados y baja hasta el sillar.
                val cordel = Path().apply {
                    moveTo(c.x - r * 0.9f, c.y + r * 0.05f); cubicTo(c.x - r * 1.25f, c.y + r * 0.2f, c.x - r * 1.05f, c.y + r * 0.8f, c.x - r * 0.75f, p.y(0.69f))
                    moveTo(c.x + r * 0.9f, c.y - r * 0.05f); cubicTo(c.x + r * 1.3f, c.y + r * 0.15f, c.x + r * 1.15f, c.y + r * 0.85f, c.x + r * 0.85f, p.y(0.69f))
                }
                drawPath(cordel, lino, style = Stroke(lw * 1f, cap = StrokeCap.Round))
                if (p.hecho) drawPath(cordel, calizaSombra.copy(alpha = 0.6f), style = Stroke(lw * 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(lw * 0.35f, lw * 0.55f))))
                val bula = Path().apply {
                    val az = java.util.Random(31)
                    for (k in 0 until 26) {
                        val a = k * 2 * PI / 26; val rr = r * (0.95f + 0.07f * az.nextFloat())
                        val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * 0.96f * sin(a)).toFloat()
                        if (k == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }
                drawPath(bula, bulto(barroClaro, c, r))
                // El hueco de la impronta, hundido: sombra arriba a la izquierda, luz abajo a la derecha.
                val hueco = r * 0.74f
                drawCircle(if (p.hecho) lerp(barroClaro, Color.Black, 0.12f) else p.sombra, hueco, c)
                if (p.hecho) {
                    drawArc(Color.Black.copy(alpha = 0.3f), 150f, 150f, false, Offset(c.x - hueco, c.y - hueco), Size(hueco * 2, hueco * 2), style = Stroke(lw * 0.9f))
                    drawArc(Color.White.copy(alpha = 0.3f), -30f, 150f, false, Offset(c.x - hueco, c.y - hueco), Size(hueco * 2, hueco * 2), style = Stroke(lw * 0.6f))
                }
                val s = hueco
                fun q(fx: Float, fy: Float) = Offset(c.x + fx * s, c.y + fy * s)
                val dorado = if (p.hecho) oro else p.sombra
                val hondo = if (p.hecho) lerp(oro, barro, 0.62f) else p.sombra
                // El cuerpo de serpiente: baja en ese desde el cuello y se adelgaza hasta la punta de la cola.
                val tramos = listOf(
                    listOf(-0.12f, -0.2f, 0.25f, -0.18f, 0.42f, 0.05f, 0.18f, 0.16f),
                    listOf(0.18f, 0.16f, -0.08f, 0.26f, -0.42f, 0.3f, -0.18f, 0.5f),
                    listOf(-0.18f, 0.5f, 0.0f, 0.64f, 0.3f, 0.58f, 0.44f, 0.4f),
                )
                tramos.forEachIndexed { k, t ->
                    val tramo = Path().apply { val a = q(t[0], t[1]); val b = q(t[2], t[3]); val c2 = q(t[4], t[5]); val e = q(t[6], t[7]); moveTo(a.x, a.y); cubicTo(b.x, b.y, c2.x, c2.y, e.x, e.y) }
                    val g = s * (0.15f - k * 0.04f)
                    drawPath(Path().apply { addPath(tramo, Offset(s * 0.03f, s * 0.035f)) }, hondo, style = Stroke(g, cap = StrokeCap.Round))
                    drawPath(tramo, dorado, style = Stroke(g, cap = StrokeCap.Round))
                    if (p.hecho) drawPath(tramo, hondo.copy(alpha = 0.7f), style = Stroke(g * 0.18f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 0.25f, g * 0.35f))))
                }
                // La cabeza de león de frente: la melena en mechones desiguales, la cara, las orejas, los ojos y el hocico.
                val cab = q(-0.16f, -0.36f)
                val melena = Path().apply {
                    for (k in 0 until 18) {
                        val a = -PI / 2 + k * PI / 9; val rr = s * (if (k % 2 == 0) 0.33f - (k % 4) * 0.012f else 0.24f)
                        val x = cab.x + (rr * cos(a)).toFloat(); val y = cab.y + (rr * sin(a)).toFloat()
                        if (k == 0) moveTo(x, y) else quadraticTo(cab.x + (rr * 1.05f * cos(a - 0.12)).toFloat(), cab.y + (rr * 1.05f * sin(a - 0.12)).toFloat(), x, y)
                    }
                    close()
                }
                drawPath(Path().apply { addPath(melena, Offset(s * 0.03f, s * 0.035f)) }, hondo)
                drawPath(melena, dorado)
                val cara = if (p.hecho) lerp(oro, Color.White, 0.22f) else p.sombra
                listOf(-1f, 1f).forEach { sx -> drawCircle(cara, s * 0.05f, Offset(cab.x + sx * s * 0.12f, cab.y - s * 0.12f)) }
                drawOval(cara, Offset(cab.x - s * 0.15f, cab.y - s * 0.15f), Size(s * 0.3f, s * 0.33f))
                if (p.hecho) {
                    listOf(-1f, 1f).forEach { sx -> drawLine(hondo, Offset(cab.x + sx * s * 0.1f, cab.y - s * 0.05f), Offset(cab.x + sx * s * 0.035f, cab.y - s * 0.03f), s * 0.03f, StrokeCap.Round) }
                    drawPath(Path().apply { moveTo(cab.x - s * 0.045f, cab.y + s * 0.05f); lineTo(cab.x + s * 0.045f, cab.y + s * 0.05f); lineTo(cab.x, cab.y + s * 0.1f); close() }, hondo)
                    drawLine(hondo, Offset(cab.x, cab.y + s * 0.1f), Offset(cab.x, cab.y + s * 0.15f), s * 0.02f)
                }
                brillo(Offset(c.x - r * 0.75f, c.y - r * 0.35f), Offset(c.x - r * 0.45f, c.y - r * 0.75f), lw * 1.3f, 0.25f)
            }
            else -> {
                // Tuyos: el cálamo y el tintero, lo de quien escribe lo suyo: la hoja de papiro con sus fibras y unos renglones,
                // el tintero redondo en el metal del nivel y la caña cortada en bisel metida en la tinta.
                basa(0.24f, 0.76f); sombraPie(0.42f, 0.74f)
                rotate(-7f, p.o(0.42f, 0.42f)) {
                    val hoja = Rect(p.x(0.24f), p.y(0.13f), p.x(0.62f), p.y(0.66f))
                    if (p.hecho) drawRect(Color.Black.copy(alpha = 0.18f), Offset(hoja.left + lw, hoja.top + lw * 1.4f), hoja.size)
                    drawRect(if (p.hecho) Brush.verticalGradient(listOf(lerp(papiro, Color.White, 0.2f), papiro, lerp(papiro, Color.Black, 0.12f)), hoja.top, hoja.bottom) else SolidColor(p.sombra), hoja.topLeft, hoja.size)
                    if (p.hecho) {
                        var y = hoja.top + lw; while (y < hoja.bottom) { drawLine(lerp(papiro, Color.Black, 0.18f).copy(alpha = 0.35f), Offset(hoja.left, y), Offset(hoja.right, y), lw * 0.2f); y += lw * 0.9f }
                        listOf(0.3f, 0.47f).forEach { fx -> drawLine(lerp(papiro, Color.Black, 0.2f).copy(alpha = 0.3f), Offset(hoja.left + hoja.width * fx, hoja.top), Offset(hoja.left + hoja.width * fx, hoja.bottom), lw * 0.5f) }
                        for (k in 0 until 6) {
                            val y0 = hoja.top + hoja.height * (0.14f + k * 0.075f)
                            var x = hoja.left + hoja.width * 0.12f; val fin = hoja.left + hoja.width * (if (k == 5) 0.5f else 0.86f)
                            while (x < fin) { val l = lw * (0.8f + ((x / lw).toInt() % 3) * 0.5f); drawLine(tinta.copy(alpha = 0.75f), Offset(x, y0), Offset(x + l, y0 - lw * 0.3f), lw * 0.35f, StrokeCap.Round); x += l + lw * 0.7f }
                        }
                    }
                }
                // El tintero: el cuerpo redondo, la boca con la tinta y el borde.
                val cuerpo = Path().apply {
                    moveTo(p.x(0.5f), p.y(0.535f)); lineTo(p.x(0.5f), p.y(0.66f)); quadraticTo(p.x(0.6f), p.y(0.715f), p.x(0.7f), p.y(0.66f)); lineTo(p.x(0.7f), p.y(0.535f)); close()
                }
                drawPath(cuerpo, p.metal(0.5f, 0.7f))
                drawOval(p.metal(0.5f, 0.7f), Offset(p.x(0.5f), p.y(0.51f)), Size(p.x(0.2f), p.y(0.05f)))
                drawOval(if (p.hecho) SolidColor(betun) else SolidColor(p.sombra), Offset(p.x(0.555f), p.y(0.522f)), Size(p.x(0.09f), p.y(0.026f)))
                if (p.hecho) { drawOval(p.oscuro.copy(alpha = 0.6f), Offset(p.x(0.5f), p.y(0.51f)), Size(p.x(0.2f), p.y(0.05f)), style = Stroke(lw * 0.3f)); drawLine(p.oscuro.copy(alpha = 0.35f), p.o(0.5f, 0.6f), p.o(0.7f, 0.6f), lw * 0.4f) }
                // El cálamo: la caña con sus nudos, cortada en bisel arriba, metida en la tinta.
                val cana = p.c(Color(0xFFC9A764))
                val a = p.o(0.6f, 0.535f); val b = p.o(0.33f, 0.1f)
                drawLine(cana, a, b, lw * 1.7f, StrokeCap.Butt)
                if (p.hecho) drawLine(lerp(cana, Color.White, 0.35f), Offset(a.x - lw * 0.4f, a.y), Offset(b.x - lw * 0.4f, b.y), lw * 0.4f)
                listOf(0.32f, 0.62f).forEach { t ->
                    val m = Offset(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t)
                    drawLine(lerp(cana, Color.Black, 0.35f), Offset(m.x - lw * 1.0f, m.y - lw * 0.55f), Offset(m.x + lw * 1.0f, m.y + lw * 0.55f), lw * 0.5f)
                }
                drawPath(Path().apply { moveTo(b.x - lw * 0.85f, b.y + lw * 0.5f); lineTo(b.x + lw * 0.85f, b.y - lw * 0.5f); lineTo(b.x - lw * 0.6f, b.y - lw * 1.6f); close() }, lerp(cana, Color.Black, 0.2f))
            }
        }
    }

    /**
     * Frente exterior: cada trofeo es un parche de misión bordado de la red, de sarga con el borde sobrehilado en el hilo
     * del metal del nivel y el motivo en puntada de satén, sujeto con dos pinzas de cromo a su peana de baquelita.
     */
    internal fun DrawScope.frente(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val hueso = p.c(TrazosFrente.HUESO); val pizarra = p.c(TrazosFrente.PIZARRA); val ambar = p.c(TrazosFrente.PILOTO_AMBAR)
        val quemado = p.c(Color(0xFFB85C0F)); val baquelita = p.c(TrazosFrente.BAQUELITA); val cromo = p.c(TrazosFrente.CROMO)
        val radar = p.c(TrazosFrente.RADAR); val pantalla = p.c(TrazosFrente.PANTALLA); val rojo = p.c(TrazosFrente.PILOTO_ROJO)
        val marmol = p.c(TrazosFrente.MARMOL); val bronce = p.c(TrazosFrente.BRONCE)
        /** Puntada de satén: la figura rellena y, si ya se logró, los hilos paralelos que la cubren, inclinados [giro] grados. */
        fun saten(forma: Path, col: Color, giro: Float = 35f) {
            drawPath(forma, col)
            if (p.hecho) clipPath(forma) {
                val b = forma.getBounds(); val d = b.width + b.height
                rotate(giro, b.center) { var x = b.center.x - d; while (x < b.center.x + d) { drawLine(lerp(col, Color.Black, 0.25f).copy(alpha = 0.45f), Offset(x, b.center.y - d), Offset(x, b.center.y + d), lw * 0.16f); x += lw * 0.5f } }
            }
        }
        /** Una línea bordada en satén (un trazo grueso con sus hilos cruzados). */
        fun hilo(a: Offset, b: Offset, col: Color, g: Float = lw * 1.2f) {
            drawLine(col, a, b, g, StrokeCap.Round)
            if (p.hecho) drawLine(lerp(col, Color.Black, 0.3f).copy(alpha = 0.5f), a, b, g, StrokeCap.Butt, PathEffect.dashPathEffect(floatArrayOf(lw * 0.14f, lw * 0.42f)))
        }
        /** El pespunte: puntadas sueltas de hilo, como la costura que une el parche a la chaqueta. */
        fun pespunte(forma: Path, col: Color) { if (p.hecho) drawPath(forma, col.copy(alpha = 0.7f), style = Stroke(lw * 0.35f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(lw * 0.9f, lw * 0.7f)))) }
        /** El parche: la sombra, la tela de sarga con su diagonal, el pespunte de adentro y el sobrehilado del borde en el hilo del nivel. */
        fun parche(forma: Path, tela: Color, costura: Color = hueso) {
            val b = forma.getBounds()
            if (p.hecho) drawPath(Path().apply { addPath(forma, Offset(lw * 0.7f, lw * 1.1f)) }, Color.Black.copy(alpha = 0.22f))
            drawPath(forma, tela)
            if (p.hecho) clipPath(forma) {
                val d = b.width + b.height
                rotate(-45f, b.center) { var x = b.center.x - d; while (x < b.center.x + d) { drawLine(Color.Black.copy(alpha = 0.1f), Offset(x, b.center.y - d), Offset(x, b.center.y + d), lw * 0.22f); x += lw * 0.75f } }
                drawPath(forma, Color.White.copy(alpha = 0.06f), style = Stroke(lw * 7f))
            }
            val esc = 1f - lw * 5f / maxOf(b.width, b.height)
            scale(esc, esc, b.center) { pespunte(forma, costura) }
            drawPath(forma, p.metal(b.left / p.w, b.right / p.w), style = Stroke(lw * 2.6f))
            if (p.hecho) drawPath(forma, p.linea.copy(alpha = 0.5f), style = Stroke(lw * 2.6f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(lw * 0.28f, lw * 0.46f))))
        }
        /** Letras bordadas, en la letra de las etiquetas de consola. */
        fun rotulo(t: String, cx: Float, base: Float, tam: Float, col: Color) { if (p.hecho) mono(t, p.x(cx), p.y(base), p.w * tam, col, android.graphics.Paint.Align.CENTER, true) }
        /** La peana: el bloque de baquelita con su filete de cromo y las dos pinzas que muerden el borde del parche. */
        fun peana(yPinza: Float) {
            caja(p, 0.3f, 0.72f, 0.7f, 0.77f, SolidColor(baquelita))
            caja(p, 0.3f, 0.72f, 0.7f, 0.732f, SolidColor(cromo))
            listOf(0.38f, 0.62f).forEach { x ->
                caja(p, x - 0.02f, yPinza, x + 0.02f, 0.725f, SolidColor(cromo))
                if (p.hecho) drawLine(Color.White.copy(alpha = 0.5f), p.o(x - 0.012f, yPinza + 0.008f), p.o(x - 0.012f, 0.72f), lw * 0.3f)
            }
        }
        when (cat) {
            "Diario" -> {
                // El parche del cartucho: el cartucho de datos (la libreta de la red) con su etiqueta ámbar, la muesca de
                // la esquina y la ventanilla de los dos carretes, bajo el número de la célula.
                val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(p.r(0.12f, 0.14f, 0.88f, 0.68f), androidx.compose.ui.geometry.CornerRadius(p.w * 0.1f))) }
                parche(forma, pizarra)
                val cart = Path().apply { moveTo(p.x(0.28f), p.y(0.26f)); lineTo(p.x(0.64f), p.y(0.26f)); lineTo(p.x(0.72f), p.y(0.31f)); lineTo(p.x(0.72f), p.y(0.58f)); lineTo(p.x(0.28f), p.y(0.58f)); close() }
                saten(cart, hueso, 80f)
                saten(Path().apply { addRect(p.r(0.32f, 0.3f, 0.6f, 0.4f)) }, ambar, 10f)
                if (p.hecho) for (k in 0 until 2) drawLine(quemado, p.o(0.35f, 0.335f + k * 0.035f), p.o(if (k == 1) 0.5f else 0.57f, 0.335f + k * 0.035f), lw * 0.45f)
                saten(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(p.r(0.34f, 0.45f, 0.66f, 0.54f), androidx.compose.ui.geometry.CornerRadius(p.w * 0.02f))) }, baquelita, 0f)
                listOf(0.42f, 0.58f).forEach { x -> drawCircle(cromo, p.w * 0.032f, p.o(x, 0.495f)); drawCircle(baquelita, p.w * 0.012f, p.o(x, 0.495f)) }
                hilo(p.o(0.44f, 0.475f), p.o(0.56f, 0.475f), radar, lw * 0.5f)
                rotulo("K-7", 0.5f, 0.235f, 0.065f, ambar)
                rotulo("CART 04", 0.5f, 0.645f, 0.05f, hueso)
                peana(0.64f)
            }
            "Mundo" -> {
                // El parche de la carta estelar: el cielo bordado con su retícula, las estrellas en punto de cruz y la ruta de
                // salto punteada en ámbar, de mundo en mundo, hasta el anillo de llegada.
                val c = p.o(0.5f, 0.4f); val r = p.w * 0.34f
                val forma = Path().apply { addOval(Rect(c, r)) }
                parche(forma, pantalla, radar)
                clipPath(Path().apply { addOval(Rect(c, r * 0.88f)) }) {
                    if (p.hecho) with(TrazosFrente) { reticula(c, r * 0.88f, radar.copy(alpha = 0.35f), lw * 0.35f, 3) }
                    val az = java.util.Random(77)
                    if (p.hecho) repeat(16) {
                        val e = Offset(c.x + (az.nextFloat() - 0.5f) * r * 1.6f, c.y + (az.nextFloat() - 0.5f) * r * 1.6f); val s = lw * (0.5f + az.nextFloat() * 0.5f)
                        drawLine(hueso, Offset(e.x - s, e.y - s), Offset(e.x + s, e.y + s), lw * 0.3f); drawLine(hueso, Offset(e.x - s, e.y + s), Offset(e.x + s, e.y - s), lw * 0.3f)
                    }
                    val paradas = listOf(Offset(c.x - r * 0.62f, c.y + r * 0.34f), Offset(c.x - r * 0.2f, c.y + r * 0.46f), Offset(c.x + r * 0.08f, c.y - r * 0.06f), Offset(c.x + r * 0.5f, c.y - r * 0.4f))
                    drawCircle(quemado, r * 0.2f, Offset(c.x - r * 0.42f, c.y - r * 0.36f))
                    if (p.hecho) drawArc(hueso.copy(alpha = 0.6f), 200f, 90f, false, Offset(c.x - r * 0.56f, c.y - r * 0.5f), Size(r * 0.28f, r * 0.28f), style = Stroke(lw * 0.4f))
                    rotate(-14f, Offset(c.x - r * 0.42f, c.y - r * 0.36f)) { drawOval(hueso, Offset(c.x - r * 0.72f, c.y - r * 0.42f), Size(r * 0.6f, r * 0.12f), style = Stroke(lw * 0.5f)) }
                    with(TrazosFrente) { rutaSalto(paradas, ambar, lw * 1.1f, lw * 0.9f) }
                    drawCircle(ambar, lw * 2.2f, paradas.last(), style = Stroke(lw * 0.6f))
                    paradas.dropLast(1).forEach { drawCircle(hueso, lw * 0.7f, it) }
                }
                rotulo("RUTA 3", 0.5f, 0.635f, 0.045f, radar)
                peana(0.66f)
            }
            "Cuerpo" -> {
                // El parche del pulso: el escudo de hueso con la pantalla redonda en su bisel de cromo y el latido trazado en verde de radar.
                val forma = Path().apply {
                    moveTo(p.x(0.16f), p.y(0.12f)); lineTo(p.x(0.84f), p.y(0.12f)); lineTo(p.x(0.84f), p.y(0.42f))
                    cubicTo(p.x(0.84f), p.y(0.56f), p.x(0.66f), p.y(0.64f), p.x(0.5f), p.y(0.7f))
                    cubicTo(p.x(0.34f), p.y(0.64f), p.x(0.16f), p.y(0.56f), p.x(0.16f), p.y(0.42f)); close()
                }
                parche(forma, hueso, pizarra)
                val c = p.o(0.5f, 0.38f); val r = p.w * 0.2f
                saten(Path().apply { addOval(Rect(c, r * 1.16f)) }, cromo, 0f)
                saten(Path().apply { addOval(Rect(c, r)) }, pantalla, 90f)
                if (p.hecho) with(TrazosFrente) { reticula(c, r, radar.copy(alpha = 0.3f), lw * 0.3f, 2) }
                val latido = Path().apply {
                    moveTo(c.x - r * 0.9f, c.y); lineTo(c.x - r * 0.38f, c.y); lineTo(c.x - r * 0.24f, c.y - r * 0.18f); lineTo(c.x - r * 0.1f, c.y + r * 0.12f)
                    lineTo(c.x + r * 0.04f, c.y - r * 0.66f); lineTo(c.x + r * 0.2f, c.y + r * 0.42f); lineTo(c.x + r * 0.32f, c.y); lineTo(c.x + r * 0.9f, c.y)
                }
                if (p.hecho) drawPath(latido, radar.copy(alpha = 0.3f), style = Stroke(lw * 1.8f, join = StrokeJoin.Round))
                drawPath(latido, radar, style = Stroke(lw * 0.7f, join = StrokeJoin.Round))
                listOf(-1f, 1f).forEach { s -> drawCircle(rojo, lw * 0.9f, Offset(c.x + s * r * 1.5f, c.y - r * 0.9f)) }
                rotulo("072", 0.5f, 0.648f, 0.055f, pizarra)
                peana(0.63f)
            }
            "Mente" -> {
                // El parche de la llave de cifrado: el rombo ámbar con el rotor de la llave (su anillo de muescas y su eje)
                // y el paletón que abre el canal.
                val forma = Path().apply { moveTo(p.x(0.5f), p.y(0.06f)); lineTo(p.x(0.88f), p.y(0.39f)); lineTo(p.x(0.5f), p.y(0.72f)); lineTo(p.x(0.12f), p.y(0.39f)); close() }
                parche(forma, quemado)
                val c = p.o(0.42f, 0.33f); val r = p.w * 0.14f
                saten(Path().apply { addOval(Rect(c, r)) }, hueso, 60f)
                if (p.hecho) for (k in 0 until 20) { val a = Math.toRadians(k * 18.0); drawLine(baquelita, Offset(c.x + (r * 0.98f * cos(a)).toFloat(), c.y + (r * 0.98f * sin(a)).toFloat()), Offset(c.x + (r * (if (k % 5 == 0) 0.72f else 0.84f) * cos(a)).toFloat(), c.y + (r * (if (k % 5 == 0) 0.72f else 0.84f) * sin(a)).toFloat()), lw * 0.35f) }
                drawCircle(baquelita, r * 0.42f, c); drawCircle(cromo, r * 0.18f, c)
                val varilla = Path().apply { moveTo(c.x + r * 0.9f, c.y + r * 0.6f); lineTo(p.x(0.7f), p.y(0.5f)) }
                drawPath(varilla, hueso, style = Stroke(lw * 1.8f, cap = StrokeCap.Butt))
                rotate(38f, p.o(0.7f, 0.5f)) {
                    saten(Path().apply { addRect(Rect(p.x(0.66f), p.y(0.5f), p.x(0.7f), p.y(0.56f))) }, hueso, 0f)
                    saten(Path().apply { addRect(Rect(p.x(0.62f), p.y(0.535f), p.x(0.66f), p.y(0.56f))) }, hueso, 0f)
                }
                drawCircle(ambar, lw * 1.1f, p.o(0.5f, 0.16f))
                rotulo("CLAVE", 0.5f, 0.625f, 0.042f, hueso)
                peana(0.6f)
            }
            "Cultura" -> {
                // El parche del microfilm: la tira de película que cruza en diagonal con sus perforaciones y sus fotogramas,
                // y el carrete del que sale.
                val forma = Path().apply { val a = 0.07f; moveTo(p.x(0.1f + a), p.y(0.16f)); lineTo(p.x(0.9f - a), p.y(0.16f)); lineTo(p.x(0.9f), p.y(0.16f + a)); lineTo(p.x(0.9f), p.y(0.66f - a))
                    lineTo(p.x(0.9f - a), p.y(0.66f)); lineTo(p.x(0.1f + a), p.y(0.66f)); lineTo(p.x(0.1f), p.y(0.66f - a)); lineTo(p.x(0.1f), p.y(0.16f + a)); close() }
                parche(forma, baquelita, cromo)
                val rc = p.o(0.3f, 0.52f)
                clipPath(forma) {
                    rotate(-24f, p.o(0.56f, 0.38f)) {
                        val tira = Path().apply { addRect(Rect(p.x(0.3f), p.y(0.31f), p.x(0.96f), p.y(0.45f))) }
                        saten(tira, ambar, 0f)
                        for (k in 0 until 4) {
                            val x0 = 0.36f + k * 0.15f
                            saten(Path().apply { addRect(Rect(p.x(x0), p.y(0.335f), p.x(x0 + 0.12f), p.y(0.425f))) }, if (k == 1) hueso else lerp(ambar, Color.Black, 0.5f), 90f)
                        }
                        if (p.hecho) for (k in 0 until 14) listOf(0.318f, 0.432f).forEach { y -> drawRect(baquelita, Offset(p.x(0.33f + k * 0.045f), p.y(y)), Size(p.x(0.022f), p.y(0.012f))) }
                    }
                }
                drawCircle(cromo, p.w * 0.13f, rc); drawCircle(baquelita, p.w * 0.105f, rc)
                saten(Path().apply { addOval(Rect(rc, p.w * 0.085f)) }, ambar, 20f)
                listOf(0f, 120f, 240f).forEach { g -> val a = Math.toRadians(g.toDouble() - 90); drawCircle(baquelita, p.w * 0.022f, Offset(rc.x + (p.w * 0.05f * cos(a)).toFloat(), rc.y + (p.w * 0.05f * sin(a)).toFloat())) }
                drawCircle(cromo, p.w * 0.018f, rc)
                rotulo("MF-12", 0.68f, 0.62f, 0.045f, cromo)
                peana(0.62f)
            }
            "Gente" -> {
                // El parche del auricular: el arco de pizarra con el auricular de radio (la diadema, los dos audífonos y el
                // micrófono) y las ondas de la voz que llega a cada lado.
                val forma = Path().apply { moveTo(p.x(0.16f), p.y(0.68f)); lineTo(p.x(0.16f), p.y(0.36f)); arcTo(Rect(p.x(0.16f), p.y(0.36f) - p.x(0.34f), p.x(0.84f), p.y(0.36f) + p.x(0.34f)), 180f, 180f, false); lineTo(p.x(0.84f), p.y(0.68f)); close() }
                parche(forma, pizarra)
                val c = p.o(0.5f, 0.38f); val r = p.w * 0.19f
                drawArc(hueso, 190f, 160f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(lw * 2.4f, cap = StrokeCap.Round))
                if (p.hecho) drawArc(lerp(hueso, Color.Black, 0.3f), 190f, 160f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(lw * 2.4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(lw * 0.14f, lw * 0.4f))))
                listOf(-1f, 1f).forEach { s ->
                    val o = Offset(c.x + s * r, c.y + r * 0.1f)
                    saten(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(o.x - r * 0.26f, o.y - r * 0.36f, o.x + r * 0.26f, o.y + r * 0.36f), androidx.compose.ui.geometry.CornerRadius(r * 0.2f))) }, baquelita, 90f)
                    drawOval(cromo, Offset(o.x - r * 0.14f, o.y - r * 0.22f), Size(r * 0.28f, r * 0.44f), style = Stroke(lw * 0.5f))
                    for (k in 1..2) drawArc(ambar.copy(alpha = 1f - k * 0.3f), if (s < 0) 150f else -30f, 60f, false, Offset(o.x - r * (0.3f + k * 0.22f), o.y - r * (0.3f + k * 0.22f)), Size(r * (0.6f + k * 0.44f), r * (0.6f + k * 0.44f)), style = Stroke(lw * 0.7f, cap = StrokeCap.Round))
                }
                val boca = Path().apply { moveTo(c.x - r, c.y + r * 0.4f); quadraticTo(c.x - r * 0.9f, c.y + r * 1.1f, c.x - r * 0.1f, c.y + r * 1.05f) }
                drawPath(boca, cromo, style = Stroke(lw * 0.8f, cap = StrokeCap.Round))
                drawCircle(baquelita, lw * 1.6f, Offset(c.x - r * 0.1f, c.y + r * 1.05f)); drawCircle(rojo, lw * 0.6f, Offset(c.x - r * 0.1f, c.y + r * 1.05f))
                rotulo("CANAL 9", 0.5f, 0.655f, 0.045f, hueso)
                peana(0.64f)
            }
            "Constancia" -> {
                // La insignia de la célula: el emblema de la red (la chispa en el círculo roto) bordado en el disco de hueso,
                // y debajo la lengüeta con un galón por cada nivel de servicio.
                val c = p.o(0.5f, 0.3f); val r = p.w * 0.27f
                val lengua = Path().apply { moveTo(p.x(0.3f), p.y(0.42f)); lineTo(p.x(0.7f), p.y(0.42f)); lineTo(p.x(0.7f), p.y(0.66f)); lineTo(p.x(0.5f), p.y(0.71f)); lineTo(p.x(0.3f), p.y(0.66f)); close() }
                parche(lengua, pizarra)
                val galones = if (p.hecho) p.nivel.coerceIn(1, 4) else 3
                for (k in 0 until galones) {
                    val y = 0.56f + k * 0.032f
                    drawPath(Path().apply { moveTo(p.x(0.36f), p.y(y)); lineTo(p.x(0.5f), p.y(y + 0.03f)); lineTo(p.x(0.64f), p.y(y)) }, ambar, style = Stroke(lw * 1.3f, join = StrokeJoin.Miter))
                }
                parche(Path().apply { addOval(Rect(c, r)) }, hueso, pizarra)
                if (p.hecho) drawCircle(pizarra.copy(alpha = 0.25f), r * 0.78f, c, style = Stroke(lw * 0.4f))
                with(TrazosFrente) { emblemaRed(c, r * 0.56f, if (p.hecho) quemado else p.sombra, lw * 1.5f) }
                if (p.hecho) drawPath(Path().apply { addOval(Rect(c, r * 0.56f)) }, lerp(quemado, Color.Black, 0.3f).copy(alpha = 0.4f), style = Stroke(lw * 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(lw * 0.14f, lw * 0.4f))))
                peana(0.67f)
            }
            "Curiosos" -> {
                // El parche de la credencial falsa: la tarjeta de mármol y bronce del Directorio, con su foto en blanco, el
                // código y la franja tachada, y encima el sello ámbar de la red que la convierte en otra.
                val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(p.r(0.18f, 0.08f, 0.82f, 0.7f), androidx.compose.ui.geometry.CornerRadius(p.w * 0.05f))) }
                parche(forma, cromo, baquelita)
                saten(Path().apply { addRect(p.r(0.27f, 0.17f, 0.73f, 0.56f)) }, marmol, 0f)
                saten(Path().apply { addRect(p.r(0.27f, 0.17f, 0.73f, 0.23f)) }, bronce, 90f)
                saten(Path().apply { addRect(p.r(0.32f, 0.27f, 0.48f, 0.43f)) }, lerp(marmol, Color.Black, 0.18f), 45f)
                if (p.hecho) {
                    drawLine(bronce.copy(alpha = 0.6f), p.o(0.32f, 0.27f), p.o(0.48f, 0.43f), lw * 0.3f); drawLine(bronce.copy(alpha = 0.6f), p.o(0.48f, 0.27f), p.o(0.32f, 0.43f), lw * 0.3f)
                    for (k in 0 until 3) drawLine(pizarra.copy(alpha = 0.7f), p.o(0.52f, 0.3f + k * 0.045f), p.o(if (k == 2) 0.62f else 0.69f, 0.3f + k * 0.045f), lw * 0.45f)
                    val barras = listOf(2, 1, 1, 3, 1, 2, 1, 1, 2, 3, 1, 1, 2)
                    var x = 0.32f; barras.forEach { b -> val ww = b * 0.007f; drawRect(baquelita, p.o(x, 0.47f), Size(p.x(ww), p.y(0.05f))); x += ww + 0.01f }
                }
                hilo(p.o(0.25f, 0.53f), p.o(0.75f, 0.19f), rojo, lw * 0.9f)
                val s = p.o(0.64f, 0.5f)
                rotate(-14f, s) {
                    drawCircle(ambar, p.w * 0.1f, s, style = Stroke(lw * 1.1f))
                    with(TrazosFrente) { emblemaRed(s, p.w * 0.055f, ambar, lw * 0.8f) }
                }
                rotulo("VÁLIDO", 0.42f, 0.645f, 0.05f, baquelita)
                peana(0.66f)
            }
            else -> {
                // El parche de la radio de maleta: la maleta de baquelita abierta con su dial redondo, las perillas, la
                // manija y la antena de látigo con la punta encendida, que transmite.
                val forma = Path().apply { moveTo(p.x(0.14f), p.y(0.16f)); lineTo(p.x(0.86f), p.y(0.16f)); lineTo(p.x(0.8f), p.y(0.68f)); lineTo(p.x(0.2f), p.y(0.68f)); close() }
                parche(forma, p.c(Color(0xFFD4CCBC)), pizarra)
                saten(Path().apply { addRect(p.r(0.26f, 0.24f, 0.74f, 0.32f)) }, lerp(baquelita, Color.White, 0.18f), 90f)
                saten(Path().apply { addRect(p.r(0.24f, 0.32f, 0.76f, 0.6f)) }, baquelita, 90f)
                drawPath(Path().apply { moveTo(p.x(0.43f), p.y(0.32f)); lineTo(p.x(0.43f), p.y(0.29f)); lineTo(p.x(0.57f), p.y(0.29f)); lineTo(p.x(0.57f), p.y(0.32f)) }, cromo, style = Stroke(lw * 0.8f))
                val dial = p.o(0.39f, 0.46f); val rd = p.w * 0.08f
                saten(Path().apply { addOval(Rect(dial, rd)) }, pantalla, 0f)
                if (p.hecho) {
                    with(TrazosFrente) { reticula(dial, rd, radar.copy(alpha = 0.6f), lw * 0.3f, 2) }
                    drawLine(ambar, dial, Offset(dial.x + rd * 0.7f, dial.y - rd * 0.5f), lw * 0.5f)
                }
                drawCircle(cromo, rd * 1.1f, dial, style = Stroke(lw * 0.5f))
                listOf(0.58f, 0.68f).forEach { x -> drawCircle(cromo, p.w * 0.032f, p.o(x, 0.42f)); drawCircle(baquelita, p.w * 0.016f, p.o(x, 0.42f)) }
                listOf(0.56f, 0.62f, 0.68f).forEach { x -> with(TrazosFrente) { piloto(p.o(x, 0.53f), p.w * 0.011f, if (x == 0.62f) rojo else ambar, p.hecho) } }
                hilo(p.o(0.72f, 0.33f), p.o(0.78f, 0.22f), cromo, lw * 0.6f)
                drawCircle(rojo, lw * 1f, p.o(0.78f, 0.22f))
                if (p.hecho) for (k in 1..2) drawArc(ambar.copy(alpha = 0.9f - k * 0.3f), -150f, 120f, false, Offset(p.x(0.78f) - p.w * 0.028f * k, p.y(0.22f) - p.w * 0.028f * k), Size(p.w * 0.056f * k, p.w * 0.056f * k), style = Stroke(lw * 0.5f))
                rotulo("TX 21", 0.5f, 0.655f, 0.045f, pizarra)
                peana(0.64f)
            }
        }
    }

    // ---------------------------------------------------------------- Terminal: los objetos del mundo de la consola

    internal fun DrawScope.terminal(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val cian = p.c(Color(0xFF00E5FF)); val negro = p.c(Color(0xFF05070B)); val plastico = p.c(Color(0xFF2A2D31))
        val beige = p.c(Color(0xFFC4BCA6)); val papel = p.c(Color(0xFFEFEDE4)); val verde = p.c(Color(0xFF5CF06A)); val gris = p.c(Color(0xFF3A3F45))
        /** El resplandor del fósforo alrededor de [c] (solo ganado). */
        fun brillo(c: Offset, r: Float) { if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFF00E5FF).copy(alpha = 0.2f), Color.Transparent), c, r), r, c) }
        fun tornillo(x: Float, y: Float) { if (p.hecho) { drawCircle(p.linea, p.w * 0.013f, p.o(x, y)); drawCircle(p.claro, p.w * 0.005f, p.o(x - 0.003f, y - 0.003f)) } }
        when (cat) {
            "Diario" -> {
                // El registro impreso: la hoja continua de papel pautado que sale de la impresora de matriz, con sus orillas de
                // arrastre perforadas, y la impresora del metal con su ranura, su perilla y sus pilotos.
                caja(p, 0.27f, 0.06f, 0.73f, 0.52f, SolidColor(papel))
                if (p.hecho) {
                    for (k in 0 until 6) caja(p, 0.33f, 0.09f + k * 0.07f, 0.67f, 0.125f + k * 0.07f, SolidColor(Color(0xFFD3ECD6)))
                    for (k in 0 until 11) {
                        val y = 0.1f + k * 0.035f
                        drawLine(gris, p.o(0.36f, y), p.o(0.36f + 0.08f + 0.2f * ((k * 7) % 5) / 4f, y), lw * 0.55f)
                        listOf(0.3f, 0.7f).forEach { x -> drawCircle(Color(0xFFB8B4A6), p.w * 0.009f, p.o(x, y)) }
                    }
                    drawLine(Color(0xFF9A968A), p.o(0.27f, 0.29f), p.o(0.73f, 0.29f), lw * 0.4f)
                }
                // La esquina de arriba, doblada.
                drawPath(Path().apply { moveTo(p.x(0.65f), p.y(0.06f)); lineTo(p.x(0.73f), p.y(0.06f)); lineTo(p.x(0.73f), p.y(0.12f)); close() }, p.b(Color(0xFFD9D6CB)))
                drawPath(Path().apply { moveTo(p.x(0.65f), p.y(0.06f)); lineTo(p.x(0.73f), p.y(0.12f)); lineTo(p.x(0.65f), p.y(0.12f)); close() }, p.b(Color(0xFFC9C5B8)))
                drawRoundRect(p.metal(0.12f, 0.88f), Offset(p.x(0.12f), p.y(0.48f)), Size(p.x(0.76f), p.y(0.24f)), CornerRadius(p.w * 0.04f))
                caja(p, 0.22f, 0.49f, 0.78f, 0.53f, SolidColor(negro))
                caja(p, 0.18f, 0.6f, 0.62f, 0.66f, SolidColor(plastico))
                if (p.hecho) for (k in 0 until 9) drawLine(p.linea, p.o(0.2f + k * 0.045f, 0.61f), p.o(0.2f + k * 0.045f, 0.65f), lw * 0.4f)
                drawCircle(p.metal(0.04f, 0.16f), p.w * 0.055f, p.o(0.12f, 0.58f)); drawCircle(p.linea, p.w * 0.02f, p.o(0.12f, 0.58f))
                drawCircle(verde, p.w * 0.016f, p.o(0.7f, 0.6f)); drawCircle(p.c(Color(0xFFFFB347)), p.w * 0.016f, p.o(0.78f, 0.6f))
                caja(p, 0.68f, 0.64f, 0.82f, 0.67f, SolidColor(plastico))
                caja(p, 0.18f, 0.72f, 0.3f, 0.77f, SolidColor(plastico)); caja(p, 0.7f, 0.72f, 0.82f, 0.77f, SolidColor(plastico))
            }
            "Mundo" -> {
                // El mapa de la red: un globo de alambre del metal con sus nodos encendidos y los enlaces entre ellos, en su
                // soporte de meridiano.
                val c = p.o(0.5f, 0.38f); val R = p.w * 0.29f
                brillo(c, R * 1.4f)
                drawArc(p.metal(0.14f, 0.9f), -70f, 220f, false, Offset(c.x - R * 1.18f, c.y - R * 1.18f), Size(R * 2.36f, R * 2.36f), style = Stroke(lw * 2.4f))
                caja(p, 0.47f, 0.38f + R * 1.18f / p.h, 0.53f, 0.71f)
                drawOval(p.metal(0.28f, 0.72f), Offset(p.x(0.28f), p.y(0.69f)), Size(p.x(0.44f), p.y(0.07f)))
                drawCircle(p.b(Color(0xFF0A1A20)), R, c)
                rotate(-18f, c) {
                    val alambre = p.metal(0.2f, 0.8f)
                    listOf(0.42f, 0.78f).forEach { f -> drawOval(alambre, Offset(c.x - R * f, c.y - R), Size(2 * R * f, 2 * R), style = Stroke(lw * 0.7f)) }
                    drawLine(alambre, Offset(c.x, c.y - R), Offset(c.x, c.y + R), lw * 0.7f)
                    listOf(-0.7f, -0.38f, 0f, 0.38f, 0.7f).forEach { lat ->
                        val a = R * kotlin.math.sqrt(1 - lat * lat)
                        drawLine(alambre, Offset(c.x - a, c.y + R * lat), Offset(c.x + a, c.y + R * lat), lw * (if (lat == 0f) 0.9f else 0.6f))
                    }
                    // Los nodos y sus enlaces.
                    val nodos = listOf(-0.55f to -0.38f, 0.1f to -0.7f, 0.42f to -0.38f, -0.78f to 0.1f, 0f to 0f, 0.6f to 0.25f, -0.3f to 0.55f, 0.3f to 0.7f)
                        .map { (s, lat) -> Offset(c.x + R * kotlin.math.sqrt(1 - lat * lat) * s, c.y + R * lat) }
                    if (p.hecho) listOf(0 to 1, 1 to 2, 0 to 4, 2 to 4, 3 to 4, 4 to 5, 4 to 6, 6 to 7, 5 to 7, 3 to 6).forEach { (i, j) -> drawLine(Color(0xFF00E5FF).copy(alpha = 0.7f), nodos[i], nodos[j], lw * 0.6f) }
                    nodos.forEachIndexed { i, o -> drawCircle(if (i == 4) p.claro else cian, p.w * (if (i == 4) 0.03f else 0.018f), o) }
                }
                drawCircle(p.metal(0.2f, 0.8f), R, c, style = Stroke(lw * 1.6f))
            }
            "Cuerpo" -> {
                // El monitor de pulso: la caja del metal con su asa, la pantalla con la cuadrícula y el trazo del latido, el
                // corazón, las perillas y la pinza del dedo en su cable.
                drawArc(p.metal(0.3f, 0.7f), 180f, 180f, false, Offset(p.x(0.3f), p.y(0.07f)), Size(p.x(0.4f), p.y(0.18f)), style = Stroke(lw * 2.2f))
                drawRoundRect(p.metal(0.1f, 0.88f), Offset(p.x(0.1f), p.y(0.15f)), Size(p.x(0.78f), p.y(0.5f)), CornerRadius(p.w * 0.04f))
                drawRoundRect(SolidColor(negro), Offset(p.x(0.15f), p.y(0.2f)), Size(p.x(0.5f), p.y(0.38f)), CornerRadius(p.w * 0.025f))
                if (p.hecho) {
                    for (k in 1..4) drawLine(Color(0xFF00E5FF).copy(alpha = 0.12f), p.o(0.15f + k * 0.1f, 0.2f), p.o(0.15f + k * 0.1f, 0.58f), lw * 0.3f)
                    for (k in 1..4) drawLine(Color(0xFF00E5FF).copy(alpha = 0.12f), p.o(0.15f, 0.2f + k * 0.076f), p.o(0.65f, 0.2f + k * 0.076f), lw * 0.3f)
                    brillo(p.o(0.4f, 0.42f), p.w * 0.3f)
                }
                val traza = Path().apply {
                    moveTo(p.x(0.17f), p.y(0.44f)); lineTo(p.x(0.27f), p.y(0.44f)); lineTo(p.x(0.3f), p.y(0.41f)); lineTo(p.x(0.33f), p.y(0.44f))
                    lineTo(p.x(0.36f), p.y(0.44f)); lineTo(p.x(0.39f), p.y(0.26f)); lineTo(p.x(0.43f), p.y(0.53f)); lineTo(p.x(0.46f), p.y(0.44f))
                    lineTo(p.x(0.52f), p.y(0.44f)); quadraticTo(p.x(0.55f), p.y(0.39f), p.x(0.58f), p.y(0.44f)); lineTo(p.x(0.63f), p.y(0.44f))
                }
                drawPath(traza, cian, style = Stroke(lw * 1.1f, join = StrokeJoin.Round, cap = StrokeCap.Round))
                val co = p.o(0.57f, 0.27f); val rc = p.w * 0.035f
                drawPath(Path().apply { moveTo(co.x, co.y + rc * 0.9f); cubicTo(co.x - rc * 1.4f, co.y, co.x - rc * 0.7f, co.y - rc, co.x, co.y - rc * 0.35f); cubicTo(co.x + rc * 0.7f, co.y - rc, co.x + rc * 1.4f, co.y, co.x, co.y + rc * 0.9f); close() }, p.b(Color(0xFFFF4D6A)))
                listOf(0.29f, 0.45f).forEach { y ->
                    drawCircle(SolidColor(plastico), p.w * 0.05f, p.o(0.765f, y))
                    drawLine(p.claro, p.o(0.765f, y), p.o(0.79f, y - 0.025f), lw * 0.6f)
                }
                drawCircle(verde, p.w * 0.014f, p.o(0.765f, 0.56f))
                caja(p, 0.18f, 0.65f, 0.28f, 0.7f, SolidColor(plastico)); caja(p, 0.7f, 0.65f, 0.8f, 0.7f, SolidColor(plastico))
                drawPath(Path().apply { moveTo(p.x(0.88f), p.y(0.5f)); cubicTo(p.x(0.97f), p.y(0.56f), p.x(0.92f), p.y(0.7f), p.x(0.82f), p.y(0.72f)) }, gris, style = Stroke(lw * 0.9f))
                drawRoundRect(p.metal(0.72f, 0.84f), Offset(p.x(0.72f), p.y(0.7f)), Size(p.x(0.12f), p.y(0.06f)), CornerRadius(p.w * 0.02f))
            }
            "Mente" -> {
                // El árbol de directorios hecho de placas: la raíz arriba, el tronco y sus ramas del metal, y en cada rama
                // una carpeta o un archivo grabado; junto al último, el cursor.
                fun carpeta(x0: Float, y0: Float, x1: Float, y1: Float) {
                    val t = (y1 - y0) * 0.25f
                    drawPath(Path().apply {
                        moveTo(p.x(x0), p.y(y0)); lineTo(p.x(x0 + (x1 - x0) * 0.38f), p.y(y0)); lineTo(p.x(x0 + (x1 - x0) * 0.46f), p.y(y0 + t))
                        lineTo(p.x(x1), p.y(y0 + t)); lineTo(p.x(x1), p.y(y1)); lineTo(p.x(x0), p.y(y1)); close()
                    }, p.metal(x0, x1))
                    if (p.hecho) drawLine(p.linea, p.o(x0 + 0.04f, (y0 + t + y1) / 2 + 0.005f), p.o(x1 - 0.06f, (y0 + t + y1) / 2 + 0.005f), lw * 0.6f)
                }
                fun archivo(x0: Float, y0: Float, x1: Float, y1: Float) {
                    val e = (x1 - x0) * 0.22f
                    drawPath(Path().apply { moveTo(p.x(x0), p.y(y0)); lineTo(p.x(x1 - e), p.y(y0)); lineTo(p.x(x1), p.y(y0) + p.x(e)); lineTo(p.x(x1), p.y(y1)); lineTo(p.x(x0), p.y(y1)); close() }, p.metal(x0, x1))
                    drawPath(Path().apply { moveTo(p.x(x1 - e), p.y(y0)); lineTo(p.x(x1 - e), p.y(y0) + p.x(e)); lineTo(p.x(x1), p.y(y0) + p.x(e)); close() }, SolidColor(p.claro))
                    if (p.hecho) drawLine(p.linea, p.o(x0 + 0.03f, (y0 + y1) / 2 + 0.008f), p.o(x1 - e - 0.02f, (y0 + y1) / 2 + 0.008f), lw * 0.5f)
                }
                // La raíz, con su barra grabada.
                drawRoundRect(p.metal(0.16f, 0.44f), Offset(p.x(0.16f), p.y(0.04f)), Size(p.x(0.28f), p.y(0.1f)), CornerRadius(p.w * 0.02f))
                if (p.hecho) drawLine(p.linea, p.o(0.33f, 0.065f), p.o(0.27f, 0.115f), lw * 0.9f)
                tornillo(0.2f, 0.09f); tornillo(0.4f, 0.09f)
                // El tronco y las ramas.
                caja(p, 0.285f, 0.14f, 0.315f, 0.72f)
                caja(p, 0.485f, 0.33f, 0.515f, 0.5f)
                listOf(0.25f, 0.6f).forEach { y -> caja(p, 0.3f, y - 0.012f, 0.4f, y + 0.012f) }
                listOf(0.38f, 0.48f).forEach { y -> caja(p, 0.5f, y - 0.012f, 0.58f, y + 0.012f) }
                caja(p, 0.3f, 0.7f - 0.012f, 0.4f, 0.7f + 0.012f)
                carpeta(0.4f, 0.19f, 0.72f, 0.31f)
                archivo(0.58f, 0.33f, 0.84f, 0.43f)
                archivo(0.58f, 0.44f, 0.84f, 0.53f)
                carpeta(0.4f, 0.54f, 0.72f, 0.66f)
                archivo(0.4f, 0.665f, 0.66f, 0.745f)
                if (p.hecho) { drawRect(Color(0xFF00E5FF), Offset(p.x(0.69f), p.y(0.68f)), Size(p.x(0.04f), p.y(0.05f))); brillo(p.o(0.71f, 0.705f), p.w * 0.08f) }
                caja(p, 0.2f, 0.74f, 0.4f, 0.77f)
            }
            "Cultura" -> {
                // El disquete de 3½: el plástico con su esquina cortada, el obturador del metal con la ventanilla por la que se
                // ve el disco, la etiqueta escrita a mano y la ventana de protección; de pie en su pinza.
                val alto = 0.64f * p.w / p.h; val y0 = 0.08f; val y1 = y0 + alto
                val cuerpo = Path().apply {
                    moveTo(p.x(0.18f), p.y(y0)); lineTo(p.x(0.76f), p.y(y0)); lineTo(p.x(0.82f), p.y(y0) + p.x(0.06f)); lineTo(p.x(0.82f), p.y(y1)); lineTo(p.x(0.18f), p.y(y1)); close()
                }
                drawPath(cuerpo, SolidColor(plastico))
                drawPath(cuerpo, p.b(Color(0xFF4A4E54)), style = Stroke(lw * 0.8f))
                caja(p, 0.32f, y0, 0.68f, y0 + alto * 0.36f)
                caja(p, 0.45f, y0 + alto * 0.05f, 0.55f, y0 + alto * 0.3f, SolidColor(p.c(Color(0xFF3A2A20))))
                if (p.hecho) { drawLine(p.claro, p.o(0.34f, y0 + 0.01f), p.o(0.34f, y0 + alto * 0.35f), lw * 0.6f); drawLine(p.linea, p.o(0.36f, y0 + alto * 0.36f), p.o(0.68f, y0 + alto * 0.36f), lw * 0.8f) }
                caja(p, 0.25f, y0 + alto * 0.44f, 0.75f, y1 - alto * 0.04f, SolidColor(papel))
                caja(p, 0.25f, y0 + alto * 0.44f, 0.75f, y0 + alto * 0.5f, SolidColor(cian))
                if (p.hecho) {
                    val tinta = Color(0xFF1C2E8C)
                    listOf(0.6f to 0.62f, 0.72f to 0.5f, 0.84f to 0.4f).forEach { (f, l) ->
                        val y = y0 + alto * f
                        drawPath(Path().apply { moveTo(p.x(0.29f), p.y(y)); var x = 0.29f; var k = 0; while (x < 0.29f + l * 0.6f && k < 20) { x += 0.025f; lineTo(p.x(x), p.y(y) + (if (k % 2 == 0) -1 else 1) * lw * 0.5f); k++ } }, tinta, style = Stroke(lw * 0.5f))
                    }
                }
                caja(p, 0.21f, y1 - alto * 0.14f, 0.25f, y1 - alto * 0.06f, SolidColor(negro))
                // La pinza del soporte y su pie.
                caja(p, 0.3f, y1 - 0.02f, 0.7f, y1 + 0.03f)
                caja(p, 0.47f, y1 + 0.03f, 0.53f, 0.74f, SolidColor(gris))
                caja(p, 0.34f, 0.73f, 0.66f, 0.77f)
            }
            "Gente" -> {
                // El canal de charla: dos terminales que se miran, cada una con su globo de diálogo, su teclado y el cable
                // del metal que las une, por el que corre un pulso.
                drawPath(Path().apply { moveTo(p.x(0.3f), p.y(0.5f)); cubicTo(p.x(0.32f), p.y(0.86f), p.x(0.68f), p.y(0.86f), p.x(0.7f), p.y(0.5f)) }, p.metal(0.3f, 0.7f), style = Stroke(lw * 1.6f))
                if (p.hecho) { drawCircle(Color(0xFF00E5FF).copy(alpha = 0.3f), p.w * 0.04f, p.o(0.5f, 0.77f)); drawCircle(Color(0xFF00E5FF), p.w * 0.016f, p.o(0.5f, 0.77f)) }
                listOf(0.27f to 1f, 0.73f to -1f).forEach { (cx, s) ->
                    drawRoundRect(p.metal(cx - 0.17f, cx + 0.17f), Offset(p.x(cx - 0.17f), p.y(0.24f)), Size(p.x(0.34f), p.y(0.28f)), CornerRadius(p.w * 0.03f))
                    drawRoundRect(SolidColor(negro), Offset(p.x(cx - 0.13f), p.y(0.27f)), Size(p.x(0.26f), p.y(0.19f)), CornerRadius(p.w * 0.02f))
                    if (p.hecho) {
                        brillo(p.o(cx, 0.365f), p.w * 0.14f)
                        drawLine(cian, p.o(cx - 0.1f, 0.31f), p.o(cx + 0.04f, 0.31f), lw * 0.7f)
                        drawLine(cian, p.o(cx - 0.1f, 0.35f), p.o(cx - 0.01f, 0.35f), lw * 0.7f)
                        drawRect(cian, Offset(p.x(cx - 0.1f), p.y(0.385f)), Size(p.x(0.03f), p.y(0.035f)))
                    }
                    drawCircle(verde, p.w * 0.012f, p.o(cx + 0.12f, 0.49f))
                    caja(p, cx - 0.05f, 0.52f, cx + 0.05f, 0.56f)
                    drawPath(Path().apply { moveTo(p.x(cx - 0.16f), p.y(0.7f)); lineTo(p.x(cx + 0.16f), p.y(0.7f)); lineTo(p.x(cx + 0.13f), p.y(0.6f)); lineTo(p.x(cx - 0.13f), p.y(0.6f)); close() }, SolidColor(beige))
                    if (p.hecho) for (k in 0 until 3) { val y = 0.62f + k * 0.025f; drawLine(Color(0xFF6E6858), p.o(cx - 0.12f - k * 0.008f, y), p.o(cx + 0.12f + k * 0.008f, y), lw * 0.9f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(lw * 1.6f, lw * 0.7f))) }
                    // El globo de diálogo.
                    val g = Path().apply {
                        addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p.x(cx - 0.13f), p.y(0.04f), p.x(cx + 0.13f), p.y(0.16f)), CornerRadius(p.w * 0.04f)))
                        moveTo(p.x(cx + s * 0.02f), p.y(0.155f)); lineTo(p.x(cx + s * 0.1f), p.y(0.21f)); lineTo(p.x(cx + s * 0.07f), p.y(0.155f)); close()
                    }
                    drawPath(g, p.metal(cx - 0.13f, cx + 0.13f))
                    if (s > 0) for (k in 0 until 3) drawCircle(p.linea, p.w * 0.016f, p.o(cx - 0.06f + k * 0.06f, 0.1f))
                    else { val co = p.o(cx, 0.105f); val rc = p.w * 0.032f
                        drawPath(Path().apply { moveTo(co.x, co.y + rc * 0.9f); cubicTo(co.x - rc * 1.4f, co.y, co.x - rc * 0.7f, co.y - rc, co.x, co.y - rc * 0.35f); cubicTo(co.x + rc * 0.7f, co.y - rc, co.x + rc * 1.4f, co.y, co.x, co.y + rc * 0.9f); close() }, p.b(Color(0xFFFF4D6A))) }
                }
            }
            "Constancia" -> {
                // El reloj del tiempo en línea: la caja redonda del metal con su corona y su botón, la esfera negra con el
                // anillo de fósforo casi completo, las marcas y la aguja; sobre su cuna.
                val c = p.o(0.5f, 0.41f); val R = p.w * 0.3f
                caja(p, 0.45f, 0.06f, 0.55f, 0.12f); caja(p, 0.41f, 0.04f, 0.59f, 0.07f)
                rotate(45f, c) { drawRect(p.metal(0.47f, 0.53f), Offset(c.x - p.x(0.03f), c.y - R - p.y(0.05f)), Size(p.x(0.06f), p.y(0.05f))) }
                brillo(c, R * 1.35f)
                drawCircle(p.metal(0.2f, 0.8f), R, c)
                drawCircle(p.linea, R * 0.86f, c, style = Stroke(lw * 0.5f))
                drawCircle(SolidColor(negro), R * 0.82f, c)
                val ra = R * 0.66f
                drawCircle(Color(0xFF00E5FF).copy(alpha = if (p.hecho) 0.15f else 0f), ra, c, style = Stroke(R * 0.1f))
                drawArc(cian, -90f, 350f, false, Offset(c.x - ra, c.y - ra), Size(ra * 2, ra * 2), style = Stroke(R * 0.1f, cap = StrokeCap.Round))
                if (p.hecho) for (k in 0 until 12) { val a = k * PI / 6; drawLine(p.claro, Offset(c.x + (R * 0.44f * cos(a)).toFloat(), c.y + (R * 0.44f * sin(a)).toFloat()), Offset(c.x + (R * 0.52f * cos(a)).toFloat(), c.y + (R * 0.52f * sin(a)).toFloat()), lw * (if (k % 3 == 0) 0.9f else 0.5f)) }
                val a = -PI / 2 + 2 * PI * 0.96
                drawLine(p.claro, c, Offset(c.x + (R * 0.5f * cos(a)).toFloat(), c.y + (R * 0.5f * sin(a)).toFloat()), lw * 1.1f, StrokeCap.Round)
                drawCircle(p.metal(0.46f, 0.54f), R * 0.08f, c)
                drawCircle(verde, p.w * 0.012f, Offset(c.x, c.y + R * 0.3f))
                drawPath(Path().apply { moveTo(p.x(0.3f), p.y(0.66f)); lineTo(p.x(0.7f), p.y(0.66f)); lineTo(p.x(0.64f), p.y(0.73f)); lineTo(p.x(0.36f), p.y(0.73f)); close() }, SolidColor(plastico))
                caja(p, 0.26f, 0.73f, 0.74f, 0.77f)
            }
            "Curiosos" -> {
                // El laberinto en un chip: la cápsula negra con sus patas del metal a los cuatro lados, la muesca de la pata
                // uno, el laberinto grabado en el metal y el camino de fósforo que lo resuelve; sobre su zócalo.
                val x0 = 0.24f; val x1 = 0.76f; val y0 = 0.12f; val y1 = y0 + (x1 - x0) * p.w / p.h
                for (k in 0 until 6) {
                    val f = (k + 0.5f) / 6f
                    val xx = x0 + (x1 - x0) * f; val yy = y0 + (y1 - y0) * f
                    caja(p, xx - 0.018f, y0 - 0.04f, xx + 0.018f, y0, p.metal(xx - 0.018f, xx + 0.018f))
                    caja(p, xx - 0.018f, y1, xx + 0.018f, y1 + 0.04f, p.metal(xx - 0.018f, xx + 0.018f))
                    caja(p, x0 - 0.07f, yy - 0.012f, x0, yy + 0.012f); caja(p, x1, yy - 0.012f, x1 + 0.07f, yy + 0.012f)
                }
                drawRoundRect(SolidColor(plastico), Offset(p.x(x0), p.y(y0)), Size(p.x(x1 - x0), p.y(y1 - y0)), CornerRadius(p.w * 0.02f))
                drawRoundRect(p.b(Color(0xFF45494F)), Offset(p.x(x0 + 0.015f), p.y(y0) + p.x(0.015f)), Size(p.x(x1 - x0 - 0.03f), p.y(y1 - y0) - p.x(0.03f)), CornerRadius(p.w * 0.015f), style = Stroke(lw * 0.5f))
                drawCircle(SolidColor(negro), p.w * 0.018f, Offset(p.x(x0 + 0.05f), p.y(y0) + p.x(0.05f)))
                // El laberinto de 5 × 5: paredes en coordenadas de la cuadrícula.
                val m0 = Offset(p.x(x0 + 0.08f), p.y(y0) + p.x(0.08f)); val celda = p.x(x1 - x0 - 0.16f) / 5f
                fun q(gx: Float, gy: Float) = Offset(m0.x + gx * celda, m0.y + gy * celda)
                val paredes = listOf(1f to 0f to (5f to 0f), 0f to 0f to (0f to 5f), 5f to 0f to (5f to 5f), 0f to 5f to (4f to 5f),
                    1f to 0f to (1f to 4f), 2f to 0f to (2f to 4f), 1f to 2f to (2f to 2f), 3f to 1f to (3f to 5f), 4f to 1f to (4f to 4f), 3f to 3f to (4f to 3f))
                paredes.forEach { (a, b) -> drawLine(p.metal(x0, x1), q(a.first, a.second), q(b.first, b.second), lw * 0.9f, StrokeCap.Square) }
                val camino = listOf(0 to -1, 0 to 0, 0 to 4, 2 to 4, 2 to 0, 4 to 0, 4 to 4, 4 to 5)
                if (p.hecho) {
                    val ruta = Path().apply { camino.forEachIndexed { i, (cx, cy) -> val o = q(cx + 0.5f, cy + 0.5f); if (i == 0) moveTo(o.x, o.y) else lineTo(o.x, o.y) } }
                    drawPath(ruta, Color(0xFF00E5FF).copy(alpha = 0.25f), style = Stroke(lw * 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    drawPath(ruta, Color(0xFF00E5FF), style = Stroke(lw * 0.8f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
                drawCircle(cian, p.w * 0.022f, q(4.5f, 4.5f))
                // El zócalo.
                caja(p, 0.18f, y1 + 0.04f, 0.82f, y1 + 0.075f, SolidColor(gris))
                caja(p, 0.28f, y1 + 0.075f, 0.72f, 0.77f, SolidColor(plastico))
            }
            else -> {
                // La llave de raíz: el ojo con el esmalte negro y la almohadilla de fósforo, la tija con su collarín, los
                // dientes del paletón como unos y ceros, y su peana.
                val c = p.o(0.5f, 0.21f); val R = p.w * 0.19f
                brillo(c, R * 1.5f)
                drawPath(Path().apply { fillType = PathFillType.EvenOdd; addOval(Rect(c, R)); addOval(Rect(c, R * 0.62f)) }, p.metal(0.31f, 0.69f))
                drawCircle(p.linea, R * 0.62f, c, style = Stroke(lw * 0.5f))
                drawCircle(SolidColor(negro), R * 0.6f, c)
                val g = R * 0.22f
                listOf(-1f, 1f).forEach { s ->
                    drawLine(cian, Offset(c.x + s * g * 0.6f - g * 0.25f, c.y - g * 1.4f), Offset(c.x + s * g * 0.6f + g * 0.25f, c.y + g * 1.4f), lw * 0.9f, StrokeCap.Round)
                    drawLine(cian, Offset(c.x - g * 1.4f, c.y + s * g * 0.6f), Offset(c.x + g * 1.4f, c.y + s * g * 0.6f), lw * 0.9f, StrokeCap.Round)
                }
                caja(p, 0.465f, 0.21f + R / p.h, 0.535f, 0.68f)
                caja(p, 0.43f, 0.38f, 0.57f, 0.41f); caja(p, 0.44f, 0.42f, 0.56f, 0.44f)
                listOf(true, false, true, true, false, true).forEachIndexed { k, uno ->
                    val y = 0.5f + k * 0.03f
                    caja(p, 0.535f, y, 0.535f + (if (uno) 0.11f else 0.05f), y + 0.022f)
                }
                if (p.hecho) drawLine(p.claro, p.o(0.48f, 0.47f), p.o(0.48f, 0.66f), lw * 0.5f)
                drawRoundRect(p.metal(0.46f, 0.54f), Offset(p.x(0.46f), p.y(0.66f)), Size(p.x(0.08f), p.y(0.04f)), CornerRadius(p.w * 0.02f))
                drawPath(Path().apply { moveTo(p.x(0.3f), p.y(0.77f)); lineTo(p.x(0.36f), p.y(0.7f)); lineTo(p.x(0.64f), p.y(0.7f)); lineTo(p.x(0.7f), p.y(0.77f)); close() }, SolidColor(plastico))
                caja(p, 0.44f, 0.7f, 0.56f, 0.715f, SolidColor(negro))
            }
        }
    }

    // ---------------------------------------------------------------- Ciudad neón: letreros de tubo sobre su tablero

    internal fun DrawScope.neon(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val magenta = Color(0xFFFF3D9A); val ambar = Color(0xFFFFB238); val verde = Color(0xFF8CFF5A)
        /** Un tubo de neón: el halo ancho y el núcleo fino; sin el logro, solo el tubo apagado. */
        fun tubo(ruta: Path, col: Color, g: Float = 1f) {
            if (p.hecho) {
                drawPath(ruta, col.copy(alpha = 0.22f), style = Stroke(lw * 4.2f * g, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(ruta, col, style = Stroke(lw * 1.3f * g, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(ruta, Color.White.copy(alpha = 0.55f), style = Stroke(lw * 0.4f * g, cap = StrokeCap.Round, join = StrokeJoin.Round))
            } else drawPath(ruta, p.sombra, style = Stroke(lw * 1.3f * g, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        fun ruta(f: Path.() -> Unit) = Path().apply(f)
        fun Path.m(x: Float, y: Float) = moveTo(p.x(x), p.y(y))
        fun Path.l(x: Float, y: Float) = lineTo(p.x(x), p.y(y))
        fun Path.q(cx: Float, cy: Float, x: Float, y: Float) = quadraticTo(p.x(cx), p.y(cy), p.x(x), p.y(y))
        fun Path.circulo(cx: Float, cy: Float, r: Float) = addOval(Rect(p.x(cx) - p.x(r), p.y(cy) - p.x(r), p.x(cx) + p.x(r), p.y(cy) + p.x(r)))
        // El tablero negro del letrero, colgado con dos grapas de la ménsula de arriba.
        caja(p, 0.2f, 0.04f, 0.8f, 0.07f)
        caja(p, 0.3f, 0.07f, 0.32f, 0.12f); caja(p, 0.68f, 0.07f, 0.7f, 0.12f)
        drawRoundRect(if (p.hecho) Color(0xFF120B17) else p.sombra.copy(alpha = p.sombra.alpha * 0.5f), Offset(p.x(0.12f), p.y(0.12f)), Size(p.x(0.76f), p.y(0.6f)), CornerRadius(p.w * 0.04f))
        when (cat) {
            // Un ticket con su renglón y la pluma que lo escribe.
            "Diario" -> {
                tubo(ruta { m(0.3f, 0.2f); l(0.62f, 0.2f); l(0.62f, 0.6f); l(0.56f, 0.64f); l(0.5f, 0.6f); l(0.44f, 0.64f); l(0.38f, 0.6f); l(0.3f, 0.64f); close() }, ambar)
                tubo(ruta { m(0.36f, 0.32f); l(0.56f, 0.32f); m(0.36f, 0.42f); l(0.52f, 0.42f) }, ambar, 0.7f)
                tubo(ruta { m(0.74f, 0.2f); l(0.52f, 0.52f) }, magenta)
            }
            // El globo con sus meridianos y su ecuador.
            "Mundo" -> {
                tubo(ruta { circulo(0.5f, 0.42f, 0.24f) }, verde)
                tubo(ruta { m(0.26f, 0.42f); l(0.74f, 0.42f) }, verde, 0.7f)
                tubo(ruta { addOval(Rect(p.x(0.4f), p.y(0.42f) - p.x(0.24f), p.x(0.6f), p.y(0.42f) + p.x(0.24f))) }, magenta, 0.7f)
            }
            // El corazón, dibujado de un solo trazo.
            "Cuerpo" -> tubo(ruta { m(0.5f, 0.62f); q(0.18f, 0.42f, 0.28f, 0.26f); q(0.4f, 0.14f, 0.5f, 0.3f); q(0.6f, 0.14f, 0.72f, 0.26f); q(0.82f, 0.42f, 0.5f, 0.62f) }, magenta, 1.2f)
            // El ojo que parpadea: el párpado de arriba, el de abajo y la pupila.
            "Mente" -> {
                tubo(ruta { m(0.22f, 0.42f); q(0.5f, 0.12f, 0.78f, 0.42f); q(0.5f, 0.72f, 0.22f, 0.42f) }, ambar)
                tubo(ruta { circulo(0.5f, 0.42f, 0.08f) }, magenta)
            }
            // El disco de la rocola y su nota musical.
            "Cultura" -> {
                tubo(ruta { circulo(0.42f, 0.44f, 0.2f) }, magenta)
                tubo(ruta { circulo(0.42f, 0.44f, 0.05f) }, magenta, 0.7f)
                tubo(ruta { m(0.68f, 0.56f); l(0.68f, 0.22f); l(0.8f, 0.26f); m(0.68f, 0.56f); q(0.6f, 0.62f, 0.62f, 0.54f) }, verde)
            }
            // Dos vasos que brindan, con su chispa.
            "Gente" -> {
                tubo(ruta { m(0.26f, 0.28f); l(0.42f, 0.24f); l(0.44f, 0.6f); l(0.32f, 0.62f); close() }, ambar)
                tubo(ruta { m(0.58f, 0.24f); l(0.74f, 0.28f); l(0.68f, 0.62f); l(0.56f, 0.6f); close() }, ambar)
                tubo(ruta { m(0.5f, 0.14f); l(0.5f, 0.2f); m(0.44f, 0.16f); l(0.46f, 0.21f); m(0.56f, 0.16f); l(0.54f, 0.21f) }, magenta, 0.7f)
            }
            // El reloj de los locales que nunca cierran: la esfera, sus manecillas y «24».
            "Constancia" -> {
                tubo(ruta { circulo(0.5f, 0.38f, 0.19f) }, verde)
                tubo(ruta { m(0.5f, 0.38f); l(0.5f, 0.26f); m(0.5f, 0.38f); l(0.59f, 0.42f) }, verde, 0.8f)
                if (p.hecho) mono("24H", p.x(0.5f), p.y(0.68f), p.w * 0.1f, magenta, android.graphics.Paint.Align.CENTER, negrita = true)
            }
            // El gato de la suerte, con la pata levantada.
            "Curiosos" -> {
                tubo(ruta { m(0.34f, 0.62f); l(0.34f, 0.36f); l(0.38f, 0.22f); l(0.44f, 0.3f); l(0.56f, 0.3f); l(0.62f, 0.22f); l(0.66f, 0.36f); l(0.66f, 0.62f); close() }, ambar)
                tubo(ruta { m(0.66f, 0.42f); l(0.74f, 0.32f); l(0.76f, 0.22f) }, ambar, 0.8f)
                tubo(ruta { circulo(0.44f, 0.4f, 0.015f); circulo(0.56f, 0.4f, 0.015f); m(0.46f, 0.5f); q(0.5f, 0.54f, 0.54f, 0.5f) }, magenta, 0.6f)
            }
            // La ficha de oro de la máquina, con su agujero cuadrado.
            else -> {
                tubo(ruta { circulo(0.5f, 0.42f, 0.22f) }, ambar, 1.2f)
                tubo(ruta { m(0.45f, 0.37f); l(0.55f, 0.37f); l(0.55f, 0.47f); l(0.45f, 0.47f); close() }, verde)
            }
        }
    }

    // ---------------------------------------------------------------- Refugio: placas y medallas de chapa esmaltada

    internal fun DrawScope.refugio(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val verde = p.c(Color(0xFF1C6E6A)); val naranja = p.c(Color(0xFFD9572B)); val amarillo = p.c(Color(0xFFE8B83A))
        val crema = p.c(Color(0xFFF1E8D0)); val tinta = p.c(Color(0xFF1F2A2A))
        fun remaches(vararg pts: Pair<Float, Float>) { if (p.hecho) pts.forEach { (x, y) -> drawCircle(p.linea, p.w * 0.016f, p.o(x, y)); drawCircle(p.claro, p.w * 0.006f, p.o(x - 0.004f, y - 0.004f)) } }
        /** Una cinta de medalla con dos colores que cuelga desde arriba. */
        fun cinta(y1: Float) {
            drawPath(Path().apply { moveTo(p.x(0.36f), p.y(0.02f)); lineTo(p.x(0.64f), p.y(0.02f)); lineTo(p.x(0.58f), p.y(y1)); lineTo(p.x(0.42f), p.y(y1)); close() }, verde)
            drawPath(Path().apply { moveTo(p.x(0.46f), p.y(0.02f)); lineTo(p.x(0.54f), p.y(0.02f)); lineTo(p.x(0.52f), p.y(y1)); lineTo(p.x(0.48f), p.y(y1)); close() }, amarillo)
            caja(p, 0.4f, y1 - 0.01f, 0.6f, y1 + 0.025f)
        }
        when (cat) {
            "Diario" -> {
                // Placa rectangular atornillada: el marco de metal, el esmalte crema, la pluma y el átomo.
                drawRoundRect(p.metal(0.14f, 0.86f), Offset(p.x(0.14f), p.y(0.14f)), Size(p.x(0.72f), p.y(0.5f)), CornerRadius(p.w * 0.04f))
                drawRoundRect(SolidColor(crema), Offset(p.x(0.19f), p.y(0.18f)), Size(p.x(0.62f), p.y(0.42f)), CornerRadius(p.w * 0.025f))
                drawRect(verde, Offset(p.x(0.19f), p.y(0.18f)), Size(p.x(0.62f), p.y(0.08f)))
                atomo(p.o(0.38f, 0.43f), p.w * 0.13f, naranja, lw * 0.8f)
                drawPath(Path().apply { moveTo(p.x(0.74f), p.y(0.3f)); lineTo(p.x(0.6f), p.y(0.52f)); lineTo(p.x(0.58f), p.y(0.56f)); lineTo(p.x(0.62f), p.y(0.54f)); lineTo(p.x(0.76f), p.y(0.32f)); close() }, tinta)
                remaches(0.18f to 0.16f, 0.82f to 0.16f, 0.18f to 0.62f, 0.82f to 0.62f)
                caja(p, 0.46f, 0.64f, 0.54f, 0.77f)
            }
            "Mundo" -> {
                // Medalla redonda: el sol que sale sobre las colinas, con su cinta.
                cinta(0.24f)
                val c = p.o(0.5f, 0.5f); val R = p.w * 0.27f
                drawCircle(p.metal(0.23f, 0.77f), R, c)
                drawCircle(SolidColor(crema), R * 0.8f, c)
                clipPath(Path().apply { addOval(Rect(c, R * 0.8f)) }) {
                    drawCircle(amarillo, R * 0.3f, Offset(c.x, c.y + R * 0.1f))
                    if (p.hecho) for (k in 0 until 9) { val a = PI + k * PI / 8; drawLine(amarillo, Offset(c.x + (R * 0.38f * cos(a)).toFloat(), c.y + R * 0.1f + (R * 0.38f * sin(a)).toFloat()), Offset(c.x + (R * 0.6f * cos(a)).toFloat(), c.y + R * 0.1f + (R * 0.6f * sin(a)).toFloat()), lw * 0.7f) }
                    drawArc(verde, 180f, 180f, true, Offset(c.x - R * 1.1f, c.y + R * 0.1f), Size(R * 1.4f, R))
                    drawArc(lerp(verde, Color.Black, 0.25f), 180f, 180f, true, Offset(c.x - R * 0.2f, c.y + R * 0.2f), Size(R * 1.5f, R))
                }
                drawCircle(p.linea, R * 0.8f, c, style = Stroke(lw * 0.6f))
            }
            "Cuerpo" -> {
                // Insignia de escudo con la cruz de salud y una franja naranja.
                val escudo = Path().apply { moveTo(p.x(0.2f), p.y(0.12f)); lineTo(p.x(0.8f), p.y(0.12f)); lineTo(p.x(0.8f), p.y(0.42f)); quadraticTo(p.x(0.8f), p.y(0.64f), p.x(0.5f), p.y(0.74f)); quadraticTo(p.x(0.2f), p.y(0.64f), p.x(0.2f), p.y(0.42f)); close() }
                drawPath(escudo, p.metal(0.2f, 0.8f))
                val dentro = Path().apply { moveTo(p.x(0.25f), p.y(0.16f)); lineTo(p.x(0.75f), p.y(0.16f)); lineTo(p.x(0.75f), p.y(0.42f)); quadraticTo(p.x(0.75f), p.y(0.6f), p.x(0.5f), p.y(0.69f)); quadraticTo(p.x(0.25f), p.y(0.6f), p.x(0.25f), p.y(0.42f)); close() }
                drawPath(dentro, SolidColor(crema))
                clipPath(dentro) { drawRect(naranja, Offset(p.x(0.2f), p.y(0.16f)), Size(p.x(0.6f), p.y(0.06f))) }
                caja(p, 0.44f, 0.28f, 0.56f, 0.56f, SolidColor(verde)); caja(p, 0.36f, 0.36f, 0.64f, 0.48f, SolidColor(verde))
            }
            "Mente" -> {
                // Medalla de estrella de ocho puntas con el átomo grande al centro.
                val c = p.o(0.5f, 0.4f)
                estrella(c, p.w * 0.36f, p.w * 0.26f, 8, p.metal(0.14f, 0.86f))
                drawCircle(SolidColor(verde), p.w * 0.24f, c)
                atomo(c, p.w * 0.19f, crema, lw * 0.9f, naranja)
                caja(p, 0.46f, 0.68f, 0.54f, 0.77f)
            }
            "Cultura" -> {
                // El disco de vinilo de la sala común, con su etiqueta y el brillo de los surcos.
                val c = p.o(0.5f, 0.4f); val R = p.w * 0.33f
                drawCircle(p.c(Color(0xFF161616)), R, c)
                if (p.hecho) for (k in 1..6) drawCircle(Color.White.copy(alpha = 0.06f), R * (0.4f + k * 0.09f), c, style = Stroke(lw * 0.4f))
                if (p.hecho) drawArc(Color.White.copy(alpha = 0.14f), 200f, 50f, false, Offset(c.x - R * 0.85f, c.y - R * 0.85f), Size(R * 1.7f, R * 1.7f), style = Stroke(lw * 2))
                drawCircle(p.metal(0.38f, 0.62f), R * 0.36f, c)
                drawCircle(SolidColor(naranja), R * 0.28f, c)
                atomo(c, R * 0.2f, crema, lw * 0.5f)
                drawCircle(p.c(Color(0xFF161616)), R * 0.05f, c)
                caja(p, 0.3f, 0.74f, 0.7f, 0.77f)
            }
            "Gente" -> {
                // La placa de la puerta del vecino: número grande en una placa redondeada con dos tornillos.
                drawRoundRect(p.metal(0.18f, 0.82f), Offset(p.x(0.18f), p.y(0.2f)), Size(p.x(0.64f), p.y(0.36f)), CornerRadius(p.w * 0.12f))
                drawRoundRect(SolidColor(verde), Offset(p.x(0.22f), p.y(0.235f)), Size(p.x(0.56f), p.y(0.29f)), CornerRadius(p.w * 0.1f))
                if (p.hecho) texto("017", p, 0.5f, 0.375f, 0.17f, Color(0xFFF1E8D0))
                remaches(0.27f to 0.38f, 0.73f to 0.38f)
                caja(p, 0.47f, 0.56f, 0.53f, 0.77f)
            }
            "Constancia" -> {
                // El engrane de la compuerta: rueda dentada con la cruz de la manivela.
                val c = p.o(0.5f, 0.4f); val R = p.w * 0.34f
                val rueda = Path().apply {
                    val dientes = 12
                    for (k in 0 until dientes * 2) { val a = k * PI / dientes; val rr = if (k % 2 == 0) R else R * 0.82f
                        val a0 = a - PI / dientes / 2; val a1 = a + PI / dientes / 2
                        val x0 = c.x + (rr * cos(a0)).toFloat(); val y0 = c.y + (rr * sin(a0)).toFloat()
                        if (k == 0) moveTo(x0, y0) else lineTo(x0, y0); lineTo(c.x + (rr * cos(a1)).toFloat(), c.y + (rr * sin(a1)).toFloat()) }
                    close(); addOval(Rect(c, R * 0.5f)); fillType = PathFillType.EvenOdd
                }
                drawPath(rueda, p.metal(0.16f, 0.84f))
                drawCircle(SolidColor(amarillo), R * 0.5f, c)
                listOf(0f, 90f).forEach { a -> rotate(a + 45f, c) { drawRect(tinta, Offset(c.x - R * 0.45f, c.y - lw), Size(R * 0.9f, lw * 2)) } }
                drawCircle(tinta, R * 0.1f, c)
                caja(p, 0.46f, 0.68f, 0.54f, 0.77f)
            }
            "Curiosos" -> {
                // El contador Geiger: caja con la aguja, la sonda en su cable y los clics que salen.
                caja(p, 0.16f, 0.3f, 0.64f, 0.66f, SolidColor(amarillo))
                drawRect(p.metal(0.16f, 0.64f), Offset(p.x(0.16f), p.y(0.3f)), Size(p.x(0.48f), p.y(0.36f)), style = Stroke(lw))
                drawArc(SolidColor(crema), 200f, 140f, true, Offset(p.x(0.22f), p.y(0.36f)), Size(p.x(0.36f), p.y(0.3f)))
                drawLine(tinta, p.o(0.4f, 0.51f), p.o(0.5f, 0.4f), lw * 0.8f)
                caja(p, 0.3f, 0.22f, 0.5f, 0.3f)
                drawPath(Path().apply { moveTo(p.x(0.64f), p.y(0.56f)); cubicTo(p.x(0.8f), p.y(0.62f), p.x(0.82f), p.y(0.36f), p.x(0.76f), p.y(0.26f)) }, tinta, style = Stroke(lw * 0.8f))
                rotate(-25f, p.o(0.76f, 0.2f)) { drawRoundRect(p.metal(0.72f, 0.8f), Offset(p.x(0.73f), p.y(0.08f)), Size(p.x(0.06f), p.y(0.18f)), CornerRadius(p.w * 0.02f)) }
                if (p.hecho) listOf(0.06f, 0.1f).forEach { r -> drawArc(naranja, -70f, 50f, false, Offset(p.x(0.72f) - p.w * r, p.y(0.1f) - p.w * r), Size(p.w * r * 2, p.w * r * 2), style = Stroke(lw * 0.7f)) }
                caja(p, 0.2f, 0.66f, 0.6f, 0.77f, SolidColor(lerp(amarillo, Color.Black, 0.25f)))
            }
            else -> {
                // La lata de conservas con su etiqueta del átomo, la tapa y la llave de abrir.
                caja(p, 0.28f, 0.2f, 0.72f, 0.72f)
                caja(p, 0.28f, 0.32f, 0.72f, 0.6f, SolidColor(naranja))
                drawRect(SolidColor(crema), Offset(p.x(0.28f), p.y(0.4f)), Size(p.x(0.44f), p.y(0.12f)))
                atomo(p.o(0.5f, 0.46f), p.w * 0.07f, verde, lw * 0.5f)
                drawOval(p.metal(0.28f, 0.72f), Offset(p.x(0.28f), p.y(0.16f)), Size(p.x(0.44f), p.y(0.08f)))
                drawOval(p.linea, Offset(p.x(0.3f), p.y(0.17f)), Size(p.x(0.4f), p.y(0.06f)), style = Stroke(lw * 0.5f))
                for (k in 1..4) drawLine(p.linea.copy(alpha = 0.35f), p.o(0.28f, 0.2f + k * 0.025f), p.o(0.72f, 0.2f + k * 0.025f), lw * 0.4f)
                drawOval(p.metal(0.28f, 0.72f), Offset(p.x(0.28f), p.y(0.68f)), Size(p.x(0.44f), p.y(0.08f)))
                drawLine(tinta, p.o(0.72f, 0.18f), p.o(0.84f, 0.1f), lw); drawCircle(tinta, p.w * 0.03f, p.o(0.86f, 0.09f), style = Stroke(lw * 0.7f))
            }
        }
    }

    // ---------------------------------------------------------------- Orden: estandartes, escudos y piezas forjadas

    internal fun DrawScope.orden(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val carmesi = p.c(Color(0xFF9E1B22)); val laton = p.c(Color(0xFFD6B25A)); val acero = p.c(Color(0xFF4B525A)); val negro = p.c(Color(0xFF15181B))
        fun remaches(vararg pts: Pair<Float, Float>) { if (p.hecho) pts.forEach { (x, y) -> drawCircle(p.linea, p.w * 0.015f, p.o(x, y)); drawCircle(p.claro, p.w * 0.006f, p.o(x - 0.004f, y - 0.004f)) } }
        /** El engrane de la Orden, el saber técnico que custodia. */
        fun engrane(c: Offset, r: Float, b: Brush, hueco: Color) {
            val path = Path().apply {
                val n = 8
                for (k in 0 until n * 2) { val a = k * PI / n; val rr = if (k % 2 == 0) r else r * 0.78f
                    val a0 = a - PI / n / 2; val a1 = a + PI / n / 2
                    val x0 = c.x + (rr * cos(a0)).toFloat(); val y0 = c.y + (rr * sin(a0)).toFloat()
                    if (k == 0) moveTo(x0, y0) else lineTo(x0, y0); lineTo(c.x + (rr * cos(a1)).toFloat(), c.y + (rr * sin(a1)).toFloat()) }
                close()
            }
            drawPath(path, b); drawCircle(hueco, r * 0.36f, c)
        }
        when (cat) {
            "Diario" -> {
                // La placa del juramento: lámina de acero con esquinas cortadas, remaches, el engrane y los renglones grabados.
                val placa = Path().apply { val a = 0.05f; moveTo(p.x(0.16f + a), p.y(0.1f)); lineTo(p.x(0.84f - a), p.y(0.1f)); lineTo(p.x(0.84f), p.y(0.1f + a)); lineTo(p.x(0.84f), p.y(0.66f - a))
                    lineTo(p.x(0.84f - a), p.y(0.66f)); lineTo(p.x(0.16f + a), p.y(0.66f)); lineTo(p.x(0.16f), p.y(0.66f - a)); lineTo(p.x(0.16f), p.y(0.1f + a)); close() }
                drawPath(placa, p.metal(0.16f, 0.84f)); drawPath(placa, p.linea, style = Stroke(lw * 0.7f))
                engrane(p.o(0.5f, 0.27f), p.w * 0.11f, SolidColor(carmesi), p.c(Color(0xFFD6B25A)))
                if (p.hecho) for (k in 0 until 4) drawLine(p.linea, p.o(0.26f, 0.43f + k * 0.05f), p.o(if (k == 3) 0.56f else 0.74f, 0.43f + k * 0.05f), lw * 0.7f)
                remaches(0.21f to 0.15f, 0.79f to 0.15f, 0.21f to 0.61f, 0.79f to 0.61f)
                caja(p, 0.44f, 0.66f, 0.56f, 0.77f, SolidColor(acero))
            }
            "Mundo" -> {
                // El estandarte de campaña: asta de acero con remate, travesaño de latón y el paño carmesí con el engrane y sus colas.
                caja(p, 0.2f, 0.04f, 0.24f, 0.77f, SolidColor(acero))
                drawPath(Path().apply { moveTo(p.x(0.22f), p.y(0.0f)); lineTo(p.x(0.26f), p.y(0.05f)); lineTo(p.x(0.18f), p.y(0.05f)); close() }, p.metal(0.18f, 0.26f))
                caja(p, 0.2f, 0.08f, 0.84f, 0.11f)
                val pano = Path().apply { moveTo(p.x(0.28f), p.y(0.11f)); lineTo(p.x(0.8f), p.y(0.11f)); lineTo(p.x(0.8f), p.y(0.58f)); lineTo(p.x(0.67f), p.y(0.5f)); lineTo(p.x(0.54f), p.y(0.6f)); lineTo(p.x(0.41f), p.y(0.5f)); lineTo(p.x(0.28f), p.y(0.58f)); close() }
                drawPath(pano, SolidColor(carmesi))
                drawPath(pano, p.metal(0.28f, 0.8f), style = Stroke(lw * 0.8f))
                engrane(p.o(0.54f, 0.3f), p.w * 0.12f, p.metal(0.42f, 0.66f), carmesi)
            }
            "Cuerpo" -> {
                // El guantelete: puño de láminas articuladas, nudillos y la manga con el filete carmesí.
                caja(p, 0.32f, 0.5f, 0.68f, 0.74f)
                caja(p, 0.32f, 0.52f, 0.68f, 0.56f, SolidColor(carmesi))
                drawRoundRect(p.metal(0.26f, 0.74f), Offset(p.x(0.26f), p.y(0.2f)), Size(p.x(0.48f), p.y(0.32f)), CornerRadius(p.w * 0.06f))
                for (k in 0 until 4) { val x = 0.29f + k * 0.11f; drawRoundRect(p.metal(x, x + 0.1f), Offset(p.x(x), p.y(0.1f)), Size(p.x(0.1f), p.y(0.16f)), CornerRadius(p.w * 0.04f)); if (p.hecho) drawLine(p.linea, p.o(x, 0.18f), p.o(x + 0.1f, 0.18f), lw * 0.5f) }
                drawRoundRect(p.metal(0.16f, 0.3f), Offset(p.x(0.16f), p.y(0.26f)), Size(p.x(0.14f), p.y(0.18f)), CornerRadius(p.w * 0.05f))
                if (p.hecho) for (k in 0 until 3) drawLine(p.linea, p.o(0.28f, 0.3f + k * 0.07f), p.o(0.72f, 0.3f + k * 0.07f), lw * 0.6f)
                remaches(0.36f to 0.64f, 0.64f to 0.64f)
            }
            "Mente" -> {
                // El calibre de acero: la regla graduada, las dos mandíbulas y el tornillo de latón.
                caja(p, 0.14f, 0.2f, 0.86f, 0.3f)
                if (p.hecho) for (k in 0..24) drawLine(p.linea, p.o(0.18f + k * 0.026f, 0.2f), p.o(0.18f + k * 0.026f, if (k % 5 == 0) 0.27f else 0.24f), lw * 0.4f)
                drawPath(Path().apply { moveTo(p.x(0.14f), p.y(0.2f)); lineTo(p.x(0.26f), p.y(0.2f)); lineTo(p.x(0.26f), p.y(0.62f)); lineTo(p.x(0.2f), p.y(0.7f)); lineTo(p.x(0.14f), p.y(0.62f)); close() }, p.metal(0.14f, 0.26f))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.16f)); lineTo(p.x(0.64f), p.y(0.16f)); lineTo(p.x(0.64f), p.y(0.36f)); lineTo(p.x(0.58f), p.y(0.36f)); lineTo(p.x(0.58f), p.y(0.62f)); lineTo(p.x(0.54f), p.y(0.7f)); lineTo(p.x(0.5f), p.y(0.62f)); close() }, p.metal(0.5f, 0.64f))
                drawCircle(SolidColor(laton), p.w * 0.04f, p.o(0.57f, 0.12f))
                caja(p, 0.3f, 0.32f, 0.48f, 0.38f, SolidColor(carmesi))
                caja(p, 0.44f, 0.7f, 0.56f, 0.77f, SolidColor(acero))
            }
            "Cultura" -> {
                // El escudo de la Orden: escudo de punta con banda carmesí, el engrane de latón y el borde remachado.
                val escudo = Path().apply { moveTo(p.x(0.18f), p.y(0.08f)); lineTo(p.x(0.82f), p.y(0.08f)); lineTo(p.x(0.82f), p.y(0.36f)); quadraticTo(p.x(0.8f), p.y(0.62f), p.x(0.5f), p.y(0.76f)); quadraticTo(p.x(0.2f), p.y(0.62f), p.x(0.18f), p.y(0.36f)); close() }
                drawPath(escudo, p.metal(0.18f, 0.82f))
                clipPath(escudo) { drawPath(Path().apply { moveTo(p.x(0.18f), p.y(0.3f)); lineTo(p.x(0.5f), p.y(0.18f)); lineTo(p.x(0.82f), p.y(0.3f)); lineTo(p.x(0.82f), p.y(0.42f)); lineTo(p.x(0.5f), p.y(0.3f)); lineTo(p.x(0.18f), p.y(0.42f)); close() }, carmesi) }
                engrane(p.o(0.5f, 0.5f), p.w * 0.12f, SolidColor(laton), p.c(Color(0xFF4B525A)))
                drawPath(escudo, p.linea, style = Stroke(lw * 0.9f))
                remaches(0.24f to 0.13f, 0.5f to 0.13f, 0.76f to 0.13f)
            }
            "Gente" -> {
                // El yelmo de visera: la cúpula, la visera con sus rendijas y la cresta carmesí.
                drawPath(Path().apply { moveTo(p.x(0.4f), p.y(0.12f)); quadraticTo(p.x(0.5f), p.y(0.02f), p.x(0.62f), p.y(0.12f)); lineTo(p.x(0.6f), p.y(0.2f)); lineTo(p.x(0.42f), p.y(0.2f)); close() }, carmesi)
                val casco = Path().apply { moveTo(p.x(0.22f), p.y(0.68f)); lineTo(p.x(0.22f), p.y(0.38f)); cubicTo(p.x(0.22f), p.y(0.12f), p.x(0.78f), p.y(0.12f), p.x(0.78f), p.y(0.38f)); lineTo(p.x(0.78f), p.y(0.68f)); lineTo(p.x(0.62f), p.y(0.74f)); lineTo(p.x(0.38f), p.y(0.74f)); close() }
                drawPath(casco, p.metal(0.22f, 0.78f))
                drawRoundRect(SolidColor(lerp(acero, Color.Black, 0.2f)), Offset(p.x(0.26f), p.y(0.36f)), Size(p.x(0.48f), p.y(0.14f)), CornerRadius(p.w * 0.03f))
                if (p.hecho) for (k in 0 until 2) drawLine(negro, p.o(0.3f, 0.4f + k * 0.06f), p.o(0.7f, 0.4f + k * 0.06f), lw * 0.8f)
                drawLine(p.linea, p.o(0.5f, 0.2f), p.o(0.5f, 0.36f), lw * 0.7f)
                remaches(0.3f to 0.6f, 0.7f to 0.6f)
            }
            "Constancia" -> {
                // El martillo de forja sobre el bloque, con chispas.
                rotate(-30f, p.o(0.5f, 0.5f)) {
                    caja(p, 0.46f, 0.24f, 0.54f, 0.78f, SolidColor(p.c(Color(0xFF5A3A22))))
                    caja(p, 0.46f, 0.3f, 0.54f, 0.34f, SolidColor(carmesi))
                    drawRoundRect(p.metal(0.3f, 0.7f), Offset(p.x(0.3f), p.y(0.12f)), Size(p.x(0.4f), p.y(0.13f)), CornerRadius(p.w * 0.015f))
                }
                caja(p, 0.2f, 0.66f, 0.8f, 0.72f, SolidColor(acero)); caja(p, 0.3f, 0.72f, 0.7f, 0.77f, SolidColor(acero))
                if (p.hecho) listOf(Offset(0.24f, 0.56f), Offset(0.32f, 0.5f), Offset(0.2f, 0.48f), Offset(0.36f, 0.6f)).forEach { o -> drawCircle(Color(0xFFFFB347), p.w * 0.012f, p.o(o.x, o.y)) }
            }
            "Curiosos" -> {
                // La válvula de vacío: ampolla de vidrio, el filamento encendido, las placas y la base de latón con sus patas.
                val amp = Path().apply { moveTo(p.x(0.32f), p.y(0.6f)); lineTo(p.x(0.32f), p.y(0.26f)); cubicTo(p.x(0.32f), p.y(0.06f), p.x(0.68f), p.y(0.06f), p.x(0.68f), p.y(0.26f)); lineTo(p.x(0.68f), p.y(0.6f)); close() }
                drawPath(amp, SolidColor(p.c(Color(0xFFCFE6F0)).copy(alpha = if (p.hecho) 0.35f else 1f)))
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFFFF8A3A).copy(alpha = 0.6f), Color.Transparent), p.o(0.5f, 0.42f), p.w * 0.2f), p.w * 0.2f, p.o(0.5f, 0.42f))
                caja(p, 0.38f, 0.26f, 0.42f, 0.56f, SolidColor(acero)); caja(p, 0.58f, 0.26f, 0.62f, 0.56f, SolidColor(acero))
                drawPath(Path().apply { moveTo(p.x(0.46f), p.y(0.56f)); lineTo(p.x(0.48f), p.y(0.36f)); lineTo(p.x(0.5f), p.y(0.44f)); lineTo(p.x(0.52f), p.y(0.36f)); lineTo(p.x(0.54f), p.y(0.56f)) }, if (p.hecho) Color(0xFFFF8A3A) else p.sombra, style = Stroke(lw * 0.6f))
                drawPath(amp, p.metal(0.32f, 0.68f), style = Stroke(lw * 0.6f))
                caja(p, 0.3f, 0.6f, 0.7f, 0.7f, SolidColor(laton))
                listOf(0.38f, 0.46f, 0.54f, 0.62f).forEach { x -> caja(p, x - 0.01f, 0.7f, x + 0.01f, 0.77f) }
            }
            else -> {
                // La espada forjada: hoja con su canal, guarda de latón, empuñadura forrada en carmesí y el pomo con el engrane.
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.02f)); lineTo(p.x(0.56f), p.y(0.1f)); lineTo(p.x(0.56f), p.y(0.52f)); lineTo(p.x(0.44f), p.y(0.52f)); lineTo(p.x(0.44f), p.y(0.1f)); close() }, p.metal(0.44f, 0.56f))
                if (p.hecho) drawLine(p.linea, p.o(0.5f, 0.08f), p.o(0.5f, 0.5f), lw * 0.6f)
                drawRoundRect(SolidColor(laton), Offset(p.x(0.26f), p.y(0.52f)), Size(p.x(0.48f), p.y(0.05f)), CornerRadius(p.w * 0.02f))
                caja(p, 0.46f, 0.57f, 0.54f, 0.69f, SolidColor(carmesi))
                if (p.hecho) for (k in 0 until 4) drawLine(negro.copy(alpha = 0.5f), p.o(0.46f, 0.59f + k * 0.028f), p.o(0.54f, 0.6f + k * 0.028f), lw * 0.4f)
                engrane(p.o(0.5f, 0.72f), p.w * 0.06f, p.metal(0.44f, 0.56f), carmesi)
            }
        }
    }

    // ---------------------------------------------------------------- Vapor victoriano: latón, hierro y vapor

    internal fun DrawScope.vapor(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val laton = p.c(Color(0xFFB8862B)); val hierro = p.c(Color(0xFF2E2B28)); val ladrillo = p.c(Color(0xFF9C3D24))
        val cardenillo = p.c(Color(0xFF4E8F7E)); val papel = p.c(Color(0xFFEFE4CB)); val madera = p.c(Color(0xFF6B4423))
        fun remaches(vararg pts: Pair<Float, Float>) { if (p.hecho) pts.forEach { (x, y) -> drawCircle(p.linea, p.w * 0.013f, p.o(x, y)); drawCircle(p.claro, p.w * 0.005f, p.o(x - 0.004f, y - 0.004f)) } }
        /** Una esfera de manómetro con la escala de 270°, la zona roja y la aguja en [lectura]. */
        fun esfera(c: Offset, r: Float, lectura: Float) {
            drawCircle(p.metal(0.5f - r / p.w, 0.5f + r / p.w), r, c)
            drawCircle(papel, r * 0.8f, c)
            if (p.hecho) {
                drawArc(ladrillo, 351f, 54f, false, Offset(c.x - r * 0.62f, c.y - r * 0.62f), Size(r * 1.24f, r * 1.24f), style = Stroke(r * 0.1f))
                for (k in 0..10) { val a = Math.toRadians(135.0 + 27.0 * k); drawLine(hierro, Vapor.en(c, r * 0.74f, a), Vapor.en(c, r * (if (k % 5 == 0) 0.56f else 0.64f), a), lw * 0.5f) }
                drawLine(ladrillo, c, Vapor.en(c, r * 0.6f, Math.toRadians(135.0 + 270.0 * lectura)), lw * 0.8f, StrokeCap.Round)
            }
            drawCircle(hierro, r * 0.1f, c)
        }
        when (cat) {
            "Diario" -> {
                // La patente sellada: el pliego con el remate en arco, el engrane grabado, los renglones y el lacre con su cinta.
                val pliego = Path().apply { moveTo(p.x(0.2f), p.y(0.72f)); lineTo(p.x(0.2f), p.y(0.18f)); quadraticTo(p.x(0.5f), p.y(0.02f), p.x(0.8f), p.y(0.18f)); lineTo(p.x(0.8f), p.y(0.72f)); close() }
                drawPath(pliego, SolidColor(papel)); drawPath(pliego, p.metal(0.2f, 0.8f), style = Stroke(lw * 1.2f))
                drawPath(Vapor.engrane(p.o(0.5f, 0.24f), p.w * 0.08f, 10, 0.78f), p.metal(0.42f, 0.58f)); drawCircle(papel, p.w * 0.025f, p.o(0.5f, 0.24f))
                if (p.hecho) for (k in 0 until 4) drawLine(p.linea, p.o(0.28f, 0.38f + k * 0.06f), p.o(if (k == 3) 0.52f else 0.72f, 0.38f + k * 0.06f), lw * 0.5f)
                drawPath(Path().apply { moveTo(p.x(0.6f), p.y(0.66f)); lineTo(p.x(0.56f), p.y(0.8f)); lineTo(p.x(0.6f), p.y(0.77f)); lineTo(p.x(0.63f), p.y(0.8f)); close() }, SolidColor(cardenillo))
                drawPath(Path().apply { moveTo(p.x(0.68f), p.y(0.66f)); lineTo(p.x(0.72f), p.y(0.8f)); lineTo(p.x(0.68f), p.y(0.77f)); lineTo(p.x(0.65f), p.y(0.8f)); close() }, SolidColor(cardenillo))
                drawPath(Vapor.engrane(p.o(0.64f, 0.64f), p.w * 0.065f, 12, 0.88f), SolidColor(ladrillo))
            }
            "Mundo" -> {
                // La locomotora de latón sobre su tramo de vía: caldera, chimenea, domo, cabina, ruedas con biela y el quitapiedras.
                caja(p, 0.06f, 0.74f, 0.94f, 0.77f, SolidColor(hierro))
                if (p.hecho) { var x = 0.09f; while (x < 0.94f) { caja(p, x, 0.72f, x + 0.03f, 0.74f, SolidColor(madera)); x += 0.08f } }
                drawRoundRect(p.metal(0.18f, 0.62f), Offset(p.x(0.18f), p.y(0.42f)), Size(p.x(0.44f), p.y(0.16f)), CornerRadius(p.w * 0.06f))
                caja(p, 0.24f, 0.24f, 0.31f, 0.42f, SolidColor(hierro)); caja(p, 0.22f, 0.22f, 0.33f, 0.26f, SolidColor(hierro))
                drawArc(p.metal(0.4f, 0.5f), 180f, 180f, true, Offset(p.x(0.4f), p.y(0.36f)), Size(p.x(0.1f), p.y(0.12f)))
                caja(p, 0.62f, 0.3f, 0.84f, 0.6f, SolidColor(cardenillo)); caja(p, 0.6f, 0.27f, 0.86f, 0.31f, SolidColor(hierro))
                caja(p, 0.67f, 0.36f, 0.79f, 0.46f, SolidColor(papel))
                drawPath(Path().apply { moveTo(p.x(0.18f), p.y(0.58f)); lineTo(p.x(0.1f), p.y(0.72f)); lineTo(p.x(0.2f), p.y(0.72f)); close() }, SolidColor(ladrillo))
                listOf(0.3f to 0.08f, 0.5f to 0.08f, 0.74f to 0.06f).forEach { (x, r) -> drawCircle(SolidColor(hierro), p.w * r, p.o(x, 0.72f - r * p.w / p.h)); drawCircle(SolidColor(laton), p.w * r * 0.35f, p.o(x, 0.72f - r * p.w / p.h)) }
                if (p.hecho) drawLine(laton, p.o(0.3f, 0.66f), p.o(0.5f, 0.66f), lw * 1.4f)
                if (p.hecho) listOf(0.27f to 0.18f, 0.33f to 0.12f, 0.4f to 0.08f).forEachIndexed { i, (x, y) -> drawCircle(Color.White.copy(alpha = 0.5f), p.w * (0.03f + i * 0.012f), p.o(x, y)) }
            }
            "Cuerpo" -> {
                // La pala del fogonero: el mango de fresno con su asa en «D», el cuello de hierro y la hoja con carbón.
                rotate(-28f, p.o(0.5f, 0.5f)) {
                    caja(p, 0.47f, 0.12f, 0.53f, 0.56f, SolidColor(madera))
                    drawPath(Path().apply { moveTo(p.x(0.42f), p.y(0.12f)); lineTo(p.x(0.42f), p.y(0.04f)); lineTo(p.x(0.58f), p.y(0.04f)); lineTo(p.x(0.58f), p.y(0.12f)) }, hierro, style = Stroke(lw * 1.6f))
                    caja(p, 0.46f, 0.54f, 0.54f, 0.6f, SolidColor(hierro))
                    drawPath(Path().apply { moveTo(p.x(0.38f), p.y(0.6f)); lineTo(p.x(0.62f), p.y(0.6f)); lineTo(p.x(0.64f), p.y(0.84f)); quadraticTo(p.x(0.5f), p.y(0.9f), p.x(0.36f), p.y(0.84f)); close() }, p.metal(0.36f, 0.64f))
                    if (p.hecho) listOf(Offset(0.45f, 0.72f), Offset(0.53f, 0.7f), Offset(0.49f, 0.78f), Offset(0.56f, 0.77f)).forEach { o -> drawCircle(Color(0xFF151311), p.w * 0.03f, p.o(o.x, o.y)) }
                }
                if (p.hecho) listOf(Offset(0.72f, 0.62f), Offset(0.78f, 0.56f), Offset(0.7f, 0.52f)).forEach { o -> drawCircle(Color(0xFFFFB347), p.w * 0.011f, p.o(o.x, o.y)) }
            }
            "Mente" -> {
                // La máquina diferencial: el bastidor de latón y tres columnas de ruedas con sus cifras.
                caja(p, 0.14f, 0.1f, 0.86f, 0.14f); caja(p, 0.14f, 0.66f, 0.86f, 0.72f)
                listOf(0.16f, 0.82f).forEach { x -> caja(p, x, 0.1f, x + 0.03f, 0.72f, SolidColor(hierro)) }
                for (col in 0 until 3) {
                    val x = 0.3f + col * 0.2f
                    caja(p, x - 0.01f, 0.14f, x + 0.01f, 0.66f, SolidColor(hierro))
                    for (k in 0 until 5) {
                        val y = 0.19f + k * 0.1f
                        drawRoundRect(p.metal(x - 0.07f, x + 0.07f), Offset(p.x(x - 0.07f), p.y(y)), Size(p.x(0.14f), p.y(0.06f)), CornerRadius(p.w * 0.02f))
                        if (p.hecho) for (d in 0 until 4) drawLine(p.linea, p.o(x - 0.05f + d * 0.033f, y + 0.005f), p.o(x - 0.05f + d * 0.033f, y + 0.055f), lw * 0.4f)
                    }
                }
                caja(p, 0.44f, 0.72f, 0.56f, 0.77f, SolidColor(hierro))
            }
            "Cultura" -> {
                // El gramófono de bocina: la caja de madera, el plato con su disco, el brazo y la gran bocina de latón.
                caja(p, 0.2f, 0.56f, 0.8f, 0.76f, SolidColor(madera))
                if (p.hecho) drawRect(laton, Offset(p.x(0.22f), p.y(0.58f)), Size(p.x(0.56f), p.y(0.16f)), style = Stroke(lw * 0.6f))
                drawOval(SolidColor(hierro), Offset(p.x(0.24f), p.y(0.5f)), Size(p.x(0.42f), p.y(0.08f)))
                if (p.hecho) drawOval(papel, Offset(p.x(0.41f), p.y(0.52f)), Size(p.x(0.08f), p.y(0.03f)))
                drawLine(laton, p.o(0.72f, 0.54f), p.o(0.62f, 0.38f), lw * 1.4f)
                val bocina = Path().apply { moveTo(p.x(0.6f), p.y(0.4f)); quadraticTo(p.x(0.5f), p.y(0.3f), p.x(0.36f), p.y(0.06f)); lineTo(p.x(0.14f), p.y(0.24f)); quadraticTo(p.x(0.42f), p.y(0.32f), p.x(0.56f), p.y(0.44f)); close() }
                drawPath(bocina, p.metal(0.14f, 0.6f))
                drawPath(bocina, p.linea, style = Stroke(lw * 0.6f))
                if (p.hecho) drawLine(p.claro, p.o(0.3f, 0.12f), p.o(0.48f, 0.34f), lw * 0.6f)
            }
            "Gente" -> {
                // El reloj del andén, donde se cita todo el mundo: la ménsula de hierro, la caja de doble esfera y el remate.
                caja(p, 0.08f, 0.1f, 0.14f, 0.5f, SolidColor(hierro))
                drawPath(Path().apply { moveTo(p.x(0.14f), p.y(0.18f)); lineTo(p.x(0.42f), p.y(0.18f)); lineTo(p.x(0.14f), p.y(0.4f)); close() }, hierro, style = Stroke(lw * 1.2f))
                drawLine(hierro, p.o(0.42f, 0.18f), p.o(0.5f, 0.24f), lw)
                drawCircle(p.metal(0.3f, 0.7f), p.w * 0.22f, p.o(0.56f, 0.48f))
                drawCircle(papel, p.w * 0.18f, p.o(0.56f, 0.48f))
                if (p.hecho) for (k in 0 until 12) { val a = k * PI / 6; drawLine(hierro, Vapor.en(p.o(0.56f, 0.48f), p.w * 0.165f, a), Vapor.en(p.o(0.56f, 0.48f), p.w * (if (k % 3 == 0) 0.13f else 0.15f), a), lw * 0.5f) }
                drawLine(hierro, p.o(0.56f, 0.48f), p.o(0.56f, 0.38f), lw * 0.9f); drawLine(hierro, p.o(0.56f, 0.48f), p.o(0.64f, 0.52f), lw * 0.9f)
                drawCircle(SolidColor(laton), p.w * 0.035f, p.o(0.56f, 0.24f))
            }
            "Constancia" -> {
                // El manómetro de la caldera sobre su tubo, con la llave de paso y la aguja cerca de la raya roja.
                caja(p, 0.46f, 0.58f, 0.54f, 0.77f, SolidColor(cardenillo))
                caja(p, 0.36f, 0.66f, 0.64f, 0.7f, SolidColor(cardenillo))
                drawCircle(SolidColor(ladrillo), p.w * 0.05f, p.o(0.36f, 0.68f)); drawLine(hierro, p.o(0.31f, 0.68f), p.o(0.41f, 0.68f), lw)
                esfera(p.o(0.5f, 0.36f), p.w * 0.28f, if (p.hecho) 0.82f else 0.1f)
                remaches(0.3f to 0.18f, 0.7f to 0.18f, 0.3f to 0.54f, 0.7f to 0.54f)
            }
            "Curiosos" -> {
                // La linterna mágica: la caja de chapa con su chimenea, el tubo del objetivo de latón y el haz de luz.
                caja(p, 0.22f, 0.34f, 0.62f, 0.68f, SolidColor(cardenillo))
                caja(p, 0.36f, 0.16f, 0.46f, 0.34f, SolidColor(hierro)); caja(p, 0.33f, 0.13f, 0.49f, 0.17f, SolidColor(hierro))
                caja(p, 0.62f, 0.44f, 0.78f, 0.56f); caja(p, 0.78f, 0.42f, 0.82f, 0.58f, SolidColor(laton))
                if (p.hecho) drawPath(Path().apply { moveTo(p.x(0.82f), p.y(0.44f)); lineTo(p.x(1f), p.y(0.32f)); lineTo(p.x(1f), p.y(0.68f)); lineTo(p.x(0.82f), p.y(0.56f)); close() }, Color(0xFFF2C46A).copy(alpha = 0.35f))
                caja(p, 0.26f, 0.68f, 0.3f, 0.77f, SolidColor(hierro)); caja(p, 0.54f, 0.68f, 0.58f, 0.77f, SolidColor(hierro))
                remaches(0.26f to 0.38f, 0.58f to 0.38f, 0.26f to 0.64f, 0.58f to 0.64f)
                if (p.hecho) drawCircle(Color(0xFFF2C46A).copy(alpha = 0.7f), p.w * 0.03f, p.o(0.42f, 0.5f))
            }
            else -> {
                // El silbato de vapor: la campana de latón con su ranura, la palanca y el tubo; arriba, el chorro de vapor.
                caja(p, 0.46f, 0.6f, 0.54f, 0.77f, SolidColor(cardenillo))
                caja(p, 0.4f, 0.54f, 0.6f, 0.6f, SolidColor(hierro))
                drawRoundRect(p.metal(0.38f, 0.62f), Offset(p.x(0.38f), p.y(0.18f)), Size(p.x(0.24f), p.y(0.36f)), CornerRadius(p.w * 0.04f))
                caja(p, 0.38f, 0.44f, 0.62f, 0.47f, SolidColor(hierro))
                drawCircle(p.metal(0.44f, 0.56f), p.w * 0.04f, p.o(0.5f, 0.15f))
                drawLine(hierro, p.o(0.6f, 0.57f), p.o(0.82f, 0.5f), lw * 1.4f); drawCircle(SolidColor(madera), p.w * 0.03f, p.o(0.83f, 0.5f))
                if (p.hecho) listOf(Offset(0.44f, 0.06f) to 0.04f, Offset(0.56f, 0.02f) to 0.05f, Offset(0.36f, 0.0f) to 0.035f).forEach { (o, r) -> drawCircle(Color.White.copy(alpha = 0.55f), p.w * r, p.o(o.x, o.y)) }
            }
        }
    }

    // ---------------------------------------------------------------- Laboratorio: vidrio, instrumentos y placas de mérito

    internal fun DrawScope.laboratorio(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val cian = p.c(Color(0xFF0B8FA1)); val violeta = p.c(Color(0xFF5E4FD1)); val verde = p.c(Color(0xFF1E8E5A))
        val blanco = p.c(Color(0xFFF4F7F9)); val tinta = p.c(Color(0xFF12202B))
        val vidrio = if (p.hecho) Color(0xFFCFE6F0).copy(alpha = 0.45f) else p.sombra
        /** El vidrio: la forma llena de aire, el líquido de [nivel] para abajo, el contorno de metal y un brillo. */
        fun vaso(forma: Path, nivel: Float, liquido: Color, x0: Float, x1: Float) {
            drawPath(forma, vidrio)
            clipPath(forma) { drawRect(liquido, Offset(p.x(x0) - p.w, p.y(nivel)), Size(p.w * 3, p.h)) }
            drawPath(forma, p.metal(x0, x1), style = Stroke(lw * 1.1f))
            if (p.hecho) drawLine(Color.White.copy(alpha = 0.6f), p.o(x0 + 0.06f, nivel - 0.2f), p.o(x0 + 0.06f, nivel - 0.06f), lw * 0.8f, StrokeCap.Round)
        }
        when (cat) {
            "Diario" -> {
                // La placa de mérito: marco de metal, el fondo blanco, la molécula y las líneas grabadas.
                drawRoundRect(p.metal(0.16f, 0.84f), Offset(p.x(0.16f), p.y(0.1f)), Size(p.x(0.68f), p.y(0.56f)), CornerRadius(p.w * 0.03f))
                drawRoundRect(SolidColor(blanco), Offset(p.x(0.2f), p.y(0.135f)), Size(p.x(0.6f), p.y(0.49f)), CornerRadius(p.w * 0.02f))
                val a = p.o(0.38f, 0.28f); val b = p.o(0.6f, 0.24f); val c = p.o(0.5f, 0.42f)
                listOf(a to b, b to c, c to a).forEach { (u, v) -> drawLine(tinta, u, v, lw * 0.8f) }
                drawCircle(cian, p.w * 0.05f, a); drawCircle(violeta, p.w * 0.06f, b); drawCircle(verde, p.w * 0.045f, c)
                if (p.hecho) for (k in 0 until 3) drawLine(tinta.copy(alpha = 0.5f), p.o(0.28f, 0.5f + k * 0.035f), p.o(if (k == 2) 0.55f else 0.72f, 0.5f + k * 0.035f), lw * 0.5f)
                caja(p, 0.46f, 0.66f, 0.54f, 0.77f)
            }
            "Mundo" -> {
                // El matraz de fondo redondo en su soporte de anillo.
                val c = p.o(0.5f, 0.46f); val R = p.w * 0.25f
                val forma = Path().apply { addRect(Rect(p.x(0.44f), p.y(0.08f), p.x(0.56f), c.y - R * 0.8f)); addOval(Rect(c, R)) }
                vaso(forma, 0.48f, cian, 0.25f, 0.75f)
                caja(p, 0.42f, 0.07f, 0.58f, 0.1f)
                drawOval(p.metal(0.22f, 0.78f), Offset(p.x(0.22f), c.y + R * 0.6f), Size(p.x(0.56f), p.y(0.04f)), style = Stroke(lw * 1.2f))
                listOf(0.26f, 0.74f).forEach { x -> drawLine(p.c(Color(0xFF4D5D6B)), Offset(p.x(x), c.y + R * 0.7f), p.o(x + if (x < 0.5f) -0.04f else 0.04f, 0.77f), lw) }
            }
            "Cuerpo" -> {
                // La probeta graduada con su pico, la base hexagonal y la escala.
                val forma = Path().apply { moveTo(p.x(0.4f), p.y(0.08f)); lineTo(p.x(0.62f), p.y(0.08f)); lineTo(p.x(0.6f), p.y(0.12f)); lineTo(p.x(0.6f), p.y(0.68f)); lineTo(p.x(0.4f), p.y(0.68f)); close() }
                vaso(forma, 0.3f, violeta, 0.4f, 0.62f)
                if (p.hecho) for (k in 0 until 10) drawLine(tinta.copy(alpha = 0.6f), p.o(0.4f, 0.18f + k * 0.05f), p.o(if (k % 5 == 0) 0.5f else 0.46f, 0.18f + k * 0.05f), lw * 0.4f)
                drawPath(Path().apply { moveTo(p.x(0.28f), p.y(0.77f)); lineTo(p.x(0.72f), p.y(0.77f)); lineTo(p.x(0.66f), p.y(0.68f)); lineTo(p.x(0.34f), p.y(0.68f)); close() }, p.metal(0.28f, 0.72f))
            }
            "Mente" -> {
                // El microscopio: brazo curvo, tubo con el ocular, el revólver con objetivos, la platina y la base.
                drawPath(Path().apply { moveTo(p.x(0.62f), p.y(0.7f)); cubicTo(p.x(0.8f), p.y(0.6f), p.x(0.8f), p.y(0.3f), p.x(0.6f), p.y(0.22f)); lineTo(p.x(0.56f), p.y(0.3f)); cubicTo(p.x(0.7f), p.y(0.36f), p.x(0.7f), p.y(0.56f), p.x(0.56f), p.y(0.64f)); close() }, p.metal(0.56f, 0.8f))
                rotate(-18f, p.o(0.5f, 0.3f)) {
                    caja(p, 0.42f, 0.12f, 0.54f, 0.42f)
                    caja(p, 0.4f, 0.06f, 0.56f, 0.12f, SolidColor(tinta))
                    caja(p, 0.44f, 0.42f, 0.52f, 0.48f, SolidColor(cian))
                }
                caja(p, 0.24f, 0.52f, 0.62f, 0.55f)
                drawRect(SolidColor(blanco), Offset(p.x(0.34f), p.y(0.5f)), Size(p.x(0.16f), p.y(0.02f)))
                drawCircle(p.metal(0.66f, 0.74f), p.w * 0.04f, p.o(0.7f, 0.48f))
                drawRoundRect(p.metal(0.22f, 0.82f), Offset(p.x(0.22f), p.y(0.7f)), Size(p.x(0.6f), p.y(0.07f)), CornerRadius(p.w * 0.03f))
            }
            "Cultura" -> {
                // El modelo molecular de esferas y varillas.
                val pts = listOf(Offset(0.5f, 0.36f), Offset(0.28f, 0.22f), Offset(0.72f, 0.2f), Offset(0.3f, 0.56f), Offset(0.72f, 0.54f), Offset(0.52f, 0.12f))
                listOf(0 to 1, 0 to 2, 0 to 3, 0 to 4, 2 to 5).forEach { (i, j) -> drawLine(p.c(Color(0xFF97A8B9)), p.o(pts[i].x, pts[i].y), p.o(pts[j].x, pts[j].y), lw * 1.6f) }
                val colores = listOf(tinta, cian, violeta, verde, cian, blanco)
                pts.forEachIndexed { i, o -> val r = p.w * (if (i == 0) 0.1f else 0.065f)
                    drawCircle(if (p.hecho) Brush.radialGradient(listOf(lerp(colores[i], Color.White, 0.5f), colores[i]), Offset(p.x(o.x) - r * 0.3f, p.y(o.y) - r * 0.3f), r) else SolidColor(p.sombra), r, p.o(o.x, o.y)) }
                drawCircle(p.metal(0.4f, 0.6f), p.w * 0.1f, p.o(0.5f, 0.36f), style = Stroke(lw * 0.6f))
                caja(p, 0.48f, 0.6f, 0.52f, 0.72f); caja(p, 0.34f, 0.72f, 0.66f, 0.77f)
            }
            "Gente" -> {
                // La gradilla con cinco tubos de ensayo de colores.
                val colores = listOf(cian, violeta, verde, p.c(Color(0xFFD9572B)), cian)
                for (k in 0 until 5) {
                    val x0 = 0.2f + k * 0.125f; val x1 = x0 + 0.08f
                    val tubo = Path().apply { moveTo(p.x(x0), p.y(0.14f)); lineTo(p.x(x0), p.y(0.56f)); arcTo(Rect(p.x(x0), p.y(0.56f) - p.x(0.04f), p.x(x1), p.y(0.56f) + p.x(0.04f)), 180f, -180f, false); lineTo(p.x(x1), p.y(0.14f)); close() }
                    vaso(tubo, 0.36f + 0.04f * (k % 3), colores[k], x0, x1)
                }
                caja(p, 0.14f, 0.3f, 0.86f, 0.34f); caja(p, 0.14f, 0.6f, 0.86f, 0.64f)
                caja(p, 0.16f, 0.3f, 0.19f, 0.77f); caja(p, 0.81f, 0.3f, 0.84f, 0.77f)
            }
            "Constancia" -> {
                // La balanza analítica: la caja de vidrio, el platillo, la pantalla con su cifra y la base.
                caja(p, 0.16f, 0.6f, 0.84f, 0.76f)
                drawRect(SolidColor(tinta), Offset(p.x(0.46f), p.y(0.63f)), Size(p.x(0.32f), p.y(0.09f)))
                if (p.hecho) texto("0.0001", p, 0.62f, 0.675f, 0.06f, Color(0xFF3CD2E5), negrita = false)
                drawRect(vidrio, Offset(p.x(0.2f), p.y(0.14f)), Size(p.x(0.6f), p.y(0.46f)))
                drawRect(p.metal(0.2f, 0.8f), Offset(p.x(0.2f), p.y(0.14f)), Size(p.x(0.6f), p.y(0.46f)), style = Stroke(lw))
                caja(p, 0.2f, 0.12f, 0.8f, 0.15f)
                drawOval(p.metal(0.34f, 0.66f), Offset(p.x(0.34f), p.y(0.5f)), Size(p.x(0.32f), p.y(0.04f)))
                caja(p, 0.48f, 0.53f, 0.52f, 0.6f)
                drawCircle(violeta, p.w * 0.05f, p.o(0.5f, 0.46f))
            }
            "Curiosos" -> {
                // El matraz Erlenmeyer con su tapón y la reacción que burbujea.
                val forma = Path().apply { moveTo(p.x(0.43f), p.y(0.12f)); lineTo(p.x(0.57f), p.y(0.12f)); lineTo(p.x(0.57f), p.y(0.32f)); lineTo(p.x(0.8f), p.y(0.72f)); lineTo(p.x(0.2f), p.y(0.72f)); lineTo(p.x(0.43f), p.y(0.32f)); close() }
                vaso(forma, 0.5f, verde, 0.2f, 0.8f)
                caja(p, 0.42f, 0.06f, 0.58f, 0.12f, SolidColor(p.c(Color(0xFF8A6A44))))
                if (p.hecho) listOf(Offset(0.42f, 0.6f) to 0.02f, Offset(0.56f, 0.56f) to 0.015f, Offset(0.5f, 0.44f) to 0.012f, Offset(0.62f, 0.64f) to 0.018f).forEach { (o, r) -> drawCircle(Color.White.copy(alpha = 0.7f), p.w * r, p.o(o.x, o.y), style = Stroke(lw * 0.4f)) }
                caja(p, 0.18f, 0.72f, 0.82f, 0.77f)
            }
            else -> {
                // La placa de Petri vista desde arriba, con las colonias del cultivo y su etiqueta.
                val c = p.o(0.5f, 0.4f); val R = p.w * 0.34f
                drawCircle(p.metal(0.16f, 0.84f), R, c)
                drawCircle(SolidColor(p.c(Color(0xFFF1E6B8))), R * 0.9f, c)
                if (p.hecho) {
                    val az = java.util.Random(77)
                    repeat(18) { val a = az.nextDouble() * 2 * PI; val rr = R * 0.75f * kotlin.math.sqrt(az.nextFloat())
                        drawCircle(listOf(cian, violeta, verde)[it % 3].copy(alpha = 0.75f), p.w * (0.015f + az.nextFloat() * 0.03f), Offset(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat())) }
                    drawArc(Color.White.copy(alpha = 0.5f), 200f, 60f, false, Offset(c.x - R * 0.8f, c.y - R * 0.8f), Size(R * 1.6f, R * 1.6f), style = Stroke(lw))
                }
                drawRect(SolidColor(blanco), Offset(p.x(0.36f), p.y(0.66f)), Size(p.x(0.28f), p.y(0.06f)))
                drawRect(p.linea, Offset(p.x(0.36f), p.y(0.66f)), Size(p.x(0.28f), p.y(0.06f)), style = Stroke(lw * 0.4f))
                caja(p, 0.3f, 0.73f, 0.7f, 0.77f)
            }
        }
    }

    // ---------------------------------------------------------------- Espacio: parches de misión, cohetes y sondas

    internal fun DrawScope.espacio(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val marino = p.c(Color(0xFF1B3A8C)); val naranja = p.c(Color(0xFFE8601C)); val violeta = p.c(Color(0xFF6B4FD6))
        val blanco = p.c(Color(0xFFEEF1F6)); val noche = p.c(Color(0xFF0A0F1E)); val gris = p.c(Color(0xFF8E9AB8))
        /** Estrellitas fijas dentro de un recorte (solo si ya se logró). */
        fun estrellas(n: Int, semilla: Long, x0: Float, y0: Float, x1: Float, y1: Float) { if (p.hecho) { val az = java.util.Random(semilla); repeat(n) { drawCircle(Color.White.copy(alpha = 0.8f), p.w * (0.004f + az.nextFloat() * 0.008f), p.o(x0 + az.nextFloat() * (x1 - x0), y0 + az.nextFloat() * (y1 - y0))) } } }
        when (cat) {
            "Diario" -> {
                // El parche bordado de la misión: el borde de metal, el cielo, el planeta con su órbita y el cohete que sube.
                val c = p.o(0.5f, 0.4f); val R = p.w * 0.34f
                drawCircle(p.metal(0.16f, 0.84f), R, c)
                drawCircle(SolidColor(noche), R * 0.86f, c)
                clipPath(Path().apply { addOval(Rect(c, R * 0.86f)) }) {
                    estrellas(14, 3, 0.2f, 0.12f, 0.8f, 0.7f)
                    drawCircle(SolidColor(marino), R * 0.6f, Offset(c.x, c.y + R * 0.75f))
                    drawCircle(p.c(Color(0xFF4F86B8)), R * 0.6f, Offset(c.x, c.y + R * 0.75f), style = Stroke(lw * 0.8f))
                }
                rotate(-20f, c) { drawOval(naranja, Offset(c.x - R * 0.8f, c.y - R * 0.2f), Size(R * 1.6f, R * 0.45f), style = Stroke(lw * 0.9f)) }
                drawPath(Path().apply { moveTo(c.x, c.y - R * 0.6f); lineTo(c.x + R * 0.12f, c.y - R * 0.3f); lineTo(c.x + R * 0.12f, c.y + R * 0.1f); lineTo(c.x - R * 0.12f, c.y + R * 0.1f); lineTo(c.x - R * 0.12f, c.y - R * 0.3f); close() }, blanco)
                drawCircle(p.metal(0.16f, 0.84f), R, c, style = Stroke(lw * 0.6f))
            }
            "Mundo" -> {
                // La sonda planetaria: la antena de plato, el cuerpo con sus paneles, el brazo de instrumentos y la antena larga.
                val c = p.o(0.46f, 0.34f)
                drawArc(p.metal(0.2f, 0.72f), 0f, 180f, true, Offset(c.x - p.w * 0.26f, c.y - p.w * 0.12f), Size(p.w * 0.52f, p.w * 0.24f))
                drawLine(p.linea, Offset(c.x, c.y), Offset(c.x, c.y - p.w * 0.16f), lw * 0.8f)
                caja(p, 0.38f, 0.42f, 0.54f, 0.54f, SolidColor(gris))
                caja(p, 0.4f, 0.44f, 0.52f, 0.5f, SolidColor(p.c(Color(0xFFD4AF37))))
                drawLine(p.linea, p.o(0.54f, 0.48f), p.o(0.84f, 0.62f), lw * 0.7f); drawCircle(SolidColor(naranja), p.w * 0.03f, p.o(0.84f, 0.62f))
                drawLine(p.linea, p.o(0.38f, 0.5f), p.o(0.16f, 0.72f), lw * 0.5f)
                caja(p, 0.44f, 0.54f, 0.48f, 0.66f, SolidColor(gris))
                estrellas(6, 9, 0.1f, 0.05f, 0.9f, 0.2f)
            }
            "Cuerpo" -> {
                // El casco presurizado: la cúpula, el visor dorado que refleja un planeta, el anillo de cierre y la luz.
                val c = p.o(0.5f, 0.38f); val R = p.w * 0.3f
                drawCircle(p.metal(0.2f, 0.8f), R, c)
                drawRoundRect(SolidColor(p.c(Color(0xFFC9A04A))), Offset(c.x - R * 0.72f, c.y - R * 0.5f), Size(R * 1.44f, R * 0.95f), CornerRadius(R * 0.45f))
                if (p.hecho) { drawArc(Color.White.copy(alpha = 0.55f), 200f, 60f, false, Offset(c.x - R * 0.6f, c.y - R * 0.42f), Size(R * 1.2f, R * 0.8f), style = Stroke(lw)); drawCircle(Color(0xFF1B3A8C).copy(alpha = 0.6f), R * 0.15f, Offset(c.x + R * 0.3f, c.y + R * 0.1f)) }
                caja(p, 0.28f, 0.64f, 0.72f, 0.72f, SolidColor(blanco))
                drawRect(SolidColor(naranja), Offset(p.x(0.28f), p.y(0.66f)), Size(p.x(0.44f), p.y(0.02f)))
                drawCircle(SolidColor(naranja), p.w * 0.025f, Offset(c.x + R * 0.8f, c.y - R * 0.5f))
                caja(p, 0.34f, 0.72f, 0.66f, 0.77f)
            }
            "Mente" -> {
                // El telescopio espacial: el tubo con su tapa abierta, los dos paneles solares y la luz que entra.
                rotate(-30f, p.o(0.5f, 0.4f)) {
                    caja(p, 0.36f, 0.16f, 0.64f, 0.62f)
                    drawLine(p.linea, p.o(0.36f, 0.3f), p.o(0.64f, 0.3f), lw * 0.5f); drawLine(p.linea, p.o(0.36f, 0.48f), p.o(0.64f, 0.48f), lw * 0.5f)
                    drawPath(Path().apply { moveTo(p.x(0.36f), p.y(0.16f)); lineTo(p.x(0.2f), p.y(0.08f)); lineTo(p.x(0.2f), p.y(0.12f)); lineTo(p.x(0.36f), p.y(0.2f)); close() }, SolidColor(gris))
                    listOf(0.06f to 0.32f, 0.68f to 0.94f).forEach { (x0, x1) ->
                        drawRect(SolidColor(marino), Offset(p.x(x0), p.y(0.34f)), Size(p.x(x1 - x0), p.y(0.1f)))
                        if (p.hecho) for (k in 1..3) drawLine(Color(0xFF8AB4FF).copy(alpha = 0.6f), p.o(x0 + (x1 - x0) * k / 4, 0.34f), p.o(x0 + (x1 - x0) * k / 4, 0.44f), lw * 0.4f)
                    }
                    caja(p, 0.32f, 0.38f, 0.68f, 0.4f, SolidColor(gris))
                }
                estrellas(8, 21, 0.06f, 0.04f, 0.94f, 0.7f)
                caja(p, 0.47f, 0.68f, 0.53f, 0.77f)
            }
            "Cultura" -> {
                // El radiotelescopio: el plato parabólico, el receptor sobre su trípode y la base que gira.
                rotate(-25f, p.o(0.5f, 0.42f)) {
                    drawArc(p.metal(0.16f, 0.84f), 0f, 180f, true, Offset(p.x(0.16f), p.y(0.18f)), Size(p.x(0.68f), p.y(0.36f)))
                    if (p.hecho) for (k in 1..3) drawArc(p.linea, 0f, 180f, false, Offset(p.x(0.5f - 0.34f * k / 4), p.y(0.36f - 0.18f * k / 4)), Size(p.x(0.68f * k / 4), p.y(0.36f * k / 4)), style = Stroke(lw * 0.4f))
                    listOf(0.24f, 0.76f).forEach { x -> drawLine(p.linea, p.o(x, 0.36f), p.o(0.5f, 0.12f), lw * 0.6f) }
                    drawCircle(SolidColor(naranja), p.w * 0.03f, p.o(0.5f, 0.12f))
                }
                caja(p, 0.44f, 0.5f, 0.56f, 0.7f, SolidColor(gris))
                caja(p, 0.3f, 0.7f, 0.7f, 0.77f)
            }
            "Gente" -> {
                // La estación orbital: los módulos en cruz, las alas de paneles y el anillo.
                caja(p, 0.2f, 0.36f, 0.8f, 0.44f)
                caja(p, 0.44f, 0.16f, 0.56f, 0.64f)
                listOf(0.04f to 0.18f, 0.82f to 0.96f).forEach { (x0, x1) -> listOf(0.24f, 0.48f).forEach { y -> drawRect(SolidColor(marino), Offset(p.x(x0), p.y(y)), Size(p.x(x1 - x0), p.y(0.08f))) } ; caja(p, x0, 0.39f, x1, 0.41f, SolidColor(gris)) }
                drawOval(p.metal(0.28f, 0.72f), Offset(p.x(0.28f), p.y(0.33f)), Size(p.x(0.44f), p.y(0.14f)), style = Stroke(lw * 1.2f))
                listOf(0.2f, 0.6f).forEach { y -> drawCircle(SolidColor(naranja), p.w * 0.015f, p.o(0.5f, y)) }
                estrellas(10, 31, 0.05f, 0.05f, 0.95f, 0.72f)
            }
            "Constancia" -> {
                // El cohete de tres etapas en su torre, con las franjas de cada etapa y el fuego del despegue.
                val cohete = Path().apply { moveTo(p.x(0.5f), p.y(0.02f)); quadraticTo(p.x(0.6f), p.y(0.1f), p.x(0.6f), p.y(0.2f)); lineTo(p.x(0.6f), p.y(0.62f)); lineTo(p.x(0.4f), p.y(0.62f)); lineTo(p.x(0.4f), p.y(0.2f)); quadraticTo(p.x(0.4f), p.y(0.1f), p.x(0.5f), p.y(0.02f)); close() }
                drawPath(cohete, p.metal(0.4f, 0.6f))
                listOf(0.22f, 0.38f, 0.52f).forEach { y -> drawRect(SolidColor(noche), Offset(p.x(0.4f), p.y(y)), Size(p.x(0.2f), p.y(0.015f))) }
                drawRect(SolidColor(naranja), Offset(p.x(0.4f), p.y(0.42f)), Size(p.x(0.2f), p.y(0.04f)))
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(p.x(0.5f + s * 0.1f), p.y(0.5f)); lineTo(p.x(0.5f + s * 0.2f), p.y(0.66f)); lineTo(p.x(0.5f + s * 0.1f), p.y(0.62f)); close() }, SolidColor(marino)) }
                if (p.hecho) drawPath(Path().apply { moveTo(p.x(0.42f), p.y(0.62f)); quadraticTo(p.x(0.5f), p.y(0.8f), p.x(0.58f), p.y(0.62f)); close() }, Brush.verticalGradient(listOf(Color(0xFFFFD27A), Color(0xFFE8601C), Color.Transparent), p.y(0.62f), p.y(0.78f)))
                // La torre de servicio a un lado.
                caja(p, 0.74f, 0.1f, 0.78f, 0.77f, SolidColor(gris))
                if (p.hecho) for (k in 0 until 9) drawLine(p.linea, p.o(0.74f, 0.12f + k * 0.07f), p.o(0.78f, 0.18f + k * 0.07f), lw * 0.4f)
                drawLine(gris, p.o(0.74f, 0.3f), p.o(0.6f, 0.3f), lw)
            }
            "Curiosos" -> {
                // El meteorito: la roca con cráteres, la estela que trae y el brillo de su metal.
                if (p.hecho) drawPath(Path().apply { moveTo(p.x(0.36f), p.y(0.3f)); lineTo(p.x(0.86f), p.y(0.04f)); lineTo(p.x(0.6f), p.y(0.46f)); close() }, Brush.linearGradient(listOf(Color(0xFFFFB86B).copy(alpha = 0.6f), Color.Transparent), p.o(0.5f, 0.38f), p.o(0.86f, 0.04f)))
                val roca = Path().apply { moveTo(p.x(0.2f), p.y(0.46f)); cubicTo(p.x(0.18f), p.y(0.26f), p.x(0.42f), p.y(0.2f), p.x(0.56f), p.y(0.28f)); cubicTo(p.x(0.74f), p.y(0.34f), p.x(0.74f), p.y(0.6f), p.x(0.52f), p.y(0.66f)); cubicTo(p.x(0.34f), p.y(0.7f), p.x(0.22f), p.y(0.62f), p.x(0.2f), p.y(0.46f)); close() }
                drawPath(roca, p.metal(0.2f, 0.74f))
                listOf(Offset(0.36f, 0.42f) to 0.06f, Offset(0.56f, 0.5f) to 0.045f, Offset(0.46f, 0.6f) to 0.03f).forEach { (o, r) -> drawCircle(p.linea, p.w * r, p.o(o.x, o.y), style = Stroke(lw * 0.7f)) }
                caja(p, 0.36f, 0.7f, 0.64f, 0.77f, SolidColor(noche))
            }
            else -> {
                // El módulo de aterrizaje: el cuerpo dorado, la cabina, las patas con sus platos y la bandera clavada al lado.
                drawPath(Path().apply { moveTo(p.x(0.3f), p.y(0.5f)); lineTo(p.x(0.7f), p.y(0.5f)); lineTo(p.x(0.66f), p.y(0.62f)); lineTo(p.x(0.34f), p.y(0.62f)); close() }, SolidColor(p.c(Color(0xFFC9A04A))))
                drawPath(Path().apply { moveTo(p.x(0.36f), p.y(0.5f)); lineTo(p.x(0.4f), p.y(0.3f)); lineTo(p.x(0.6f), p.y(0.3f)); lineTo(p.x(0.64f), p.y(0.5f)); close() }, p.metal(0.36f, 0.64f))
                caja(p, 0.46f, 0.35f, 0.54f, 0.42f, SolidColor(noche))
                listOf(-1f, 1f).forEach { s -> drawLine(gris, p.o(0.5f + s * 0.16f, 0.6f), p.o(0.5f + s * 0.28f, 0.74f), lw * 1.2f); caja(p, 0.5f + s * 0.28f - 0.05f, 0.74f, 0.5f + s * 0.28f + 0.05f, 0.77f) }
                drawLine(gris, p.o(0.5f, 0.3f), p.o(0.5f, 0.2f), lw * 0.6f)
                drawLine(p.linea, p.o(0.86f, 0.77f), p.o(0.86f, 0.4f), lw * 0.7f)
                drawRect(SolidColor(naranja), Offset(p.x(0.86f), p.y(0.4f)), Size(p.x(0.1f), p.y(0.07f)))
                drawRect(SolidColor(gris.copy(alpha = 0.4f)), Offset(p.x(0.1f), p.y(0.76f)), Size(p.x(0.8f), p.y(0.01f)))
            }
        }
    }

    // ---------------------------------------------------------------- Abismo: escafandra, batisfera, pecios y criaturas del fondo

    internal fun DrawScope.abismo(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val mar = p.c(Color(0xFF0A5C8C)); val hondo = p.c(Color(0xFF05263A)); val luz = p.c(Color(0xFF5CF2C6))
        val coral = p.c(Color(0xFFC0563F)); val espuma = p.c(Color(0xFFE6F2F6)); val violeta = p.c(Color(0xFFE29BFF))
        /** Burbujas que suben (solo si ya se logró). */
        fun burbujas(n: Int, semilla: Long, x0: Float, y0: Float, x1: Float, y1: Float) {
            if (p.hecho) { val az = java.util.Random(semilla); repeat(n) { drawCircle(Color.White.copy(alpha = 0.75f), p.w * (0.008f + az.nextFloat() * 0.014f), p.o(x0 + az.nextFloat() * (x1 - x0), y0 + az.nextFloat() * (y1 - y0)), style = Stroke(lw * 0.4f)) } }
        }
        /** Un ojo de buey: la brida de metal, el cristal y su reflejo. */
        fun ojo(c: Offset, r: Float) {
            drawCircle(p.metal(0.2f, 0.8f), r, c)
            drawCircle(SolidColor(hondo), r * 0.7f, c)
            if (p.hecho) drawArc(Color.White.copy(alpha = 0.55f), 200f, 60f, false, Offset(c.x - r * 0.5f, c.y - r * 0.5f), Size(r, r), style = Stroke(lw * 0.6f))
        }
        when (cat) {
            "Diario" -> {
                // La pizarra del buzo: la tablilla blanca con su marco de latón, los renglones escritos bajo el agua y el lápiz atado.
                drawRoundRect(p.metal(0.22f, 0.7f), p.o(0.22f, 0.12f), Size(p.x(0.48f), p.y(0.6f)), CornerRadius(p.w * 0.04f))
                drawRoundRect(SolidColor(espuma), p.o(0.26f, 0.17f), Size(p.x(0.4f), p.y(0.5f)), CornerRadius(p.w * 0.02f))
                if (p.hecho) for (k in 0 until 6) drawLine(hondo.copy(alpha = 0.55f), p.o(0.3f, 0.24f + k * 0.07f), p.o(if (k % 3 == 2) 0.5f else 0.62f, 0.24f + k * 0.07f), lw * 0.5f)
                drawCircle(p.linea, p.w * 0.02f, p.o(0.46f, 0.15f))
                drawPath(Path().apply { moveTo(p.x(0.46f), p.y(0.15f)); quadraticTo(p.x(0.78f), p.y(0.1f), p.x(0.8f), p.y(0.3f)) }, p.linea, style = Stroke(lw * 0.5f))
                drawLine(SolidColor(coral), p.o(0.8f, 0.3f), p.o(0.84f, 0.7f), lw * 1.6f)
                drawLine(p.linea, p.o(0.835f, 0.66f), p.o(0.842f, 0.74f), lw * 0.8f)
                caja(p, 0.3f, 0.72f, 0.7f, 0.77f)
            }
            "Mundo" -> {
                // El globo batimétrico: los océanos en azul, las fosas más hondas en oscuro y las isóbatas alrededor de los continentes.
                val c = p.o(0.5f, 0.38f); val R = p.w * 0.3f
                drawCircle(SolidColor(mar), R, c)
                clipPath(Path().apply { addOval(Rect(c, R)) }) {
                    drawPath(Path().apply { moveTo(c.x - R * 0.6f, c.y - R * 0.7f); quadraticTo(c.x - R * 0.1f, c.y - R * 0.5f, c.x - R * 0.2f, c.y); quadraticTo(c.x - R * 0.4f, c.y + R * 0.4f, c.x - R * 0.7f, c.y + R * 0.2f); close() }, p.metal(0.2f, 0.5f))
                    drawPath(Path().apply { moveTo(c.x + R * 0.3f, c.y - R * 0.4f); quadraticTo(c.x + R * 0.8f, c.y - R * 0.2f, c.x + R * 0.5f, c.y + R * 0.5f); quadraticTo(c.x + R * 0.2f, c.y + R * 0.2f, c.x + R * 0.3f, c.y - R * 0.4f); close() }, p.metal(0.6f, 0.85f))
                    if (p.hecho) {
                        drawOval(hondo, Offset(c.x + R * 0.05f, c.y + R * 0.45f), Size(R * 0.18f, R * 0.4f))
                        listOf(0.82f, 0.92f).forEach { k -> drawCircle(espuma.copy(alpha = 0.35f), R * k, c, style = Stroke(lw * 0.4f)) }
                    }
                }
                drawCircle(p.linea, R, c, style = Stroke(lw * 0.6f))
                drawArc(p.metal(0.15f, 0.85f), 120f, 300f, false, Offset(c.x - R * 1.12f, c.y - R * 1.12f), Size(R * 2.24f, R * 2.24f), style = Stroke(lw * 1.4f))
                caja(p, 0.47f, 0.7f, 0.53f, 0.73f)
                caja(p, 0.32f, 0.73f, 0.68f, 0.77f)
            }
            "Cuerpo" -> {
                // La escafandra de latón: el casco redondo con su ojo de buey grande al frente y dos a los lados, el peto
                // atornillado y la válvula del aire.
                val c = p.o(0.5f, 0.38f); val R = p.w * 0.27f
                drawRoundRect(p.metal(0.18f, 0.82f), p.o(0.2f, 0.58f), Size(p.x(0.6f), p.y(0.16f)), CornerRadius(p.w * 0.05f))
                if (p.hecho) listOf(0.26f, 0.4f, 0.6f, 0.74f).forEach { x -> drawCircle(p.linea, p.w * 0.014f, p.o(x, 0.66f)) }
                drawCircle(p.metal(0.22f, 0.78f), R, c)
                ojo(c, R * 0.48f)
                listOf(-1f, 1f).forEach { s -> ojo(Offset(c.x + s * R * 0.78f, c.y - R * 0.05f), R * 0.17f) }
                drawLine(p.linea, Offset(c.x + R * 0.5f, c.y - R * 0.8f), Offset(c.x + R * 0.7f, c.y - R * 1.05f), lw * 1.4f)
                drawCircle(SolidColor(coral), p.w * 0.022f, Offset(c.x + R * 0.72f, c.y - R * 1.07f))
                burbujas(5, 4, 0.66f, 0.02f, 0.86f, 0.14f)
                caja(p, 0.34f, 0.74f, 0.66f, 0.77f)
            }
            "Mente" -> {
                // La batisfera: la esfera de acero colgada de su cable, con sus ojos de buey y el foco que alumbra lo hondo.
                drawLine(p.linea, p.o(0.5f, 0f), p.o(0.5f, 0.14f), lw * 0.8f)
                val c = p.o(0.5f, 0.38f); val R = p.w * 0.24f
                if (p.hecho) drawPath(Path().apply { moveTo(c.x + R * 0.3f, c.y + R * 0.3f); lineTo(p.x(0.95f), p.y(0.66f)); lineTo(p.x(0.7f), p.y(0.72f)); close() }, Brush.linearGradient(listOf(luz.copy(alpha = 0.55f), Color.Transparent), c, p.o(0.9f, 0.7f)))
                drawCircle(p.metal(0.26f, 0.74f), R, c)
                drawRect(p.metal(0.42f, 0.58f), Offset(p.x(0.44f), c.y - R * 1.05f), Size(p.x(0.12f), R * 0.25f))
                ojo(Offset(c.x - R * 0.3f, c.y + R * 0.05f), R * 0.28f); ojo(Offset(c.x + R * 0.32f, c.y - R * 0.2f), R * 0.2f)
                if (p.hecho) for (k in 0 until 10) { val a = Math.toRadians(k * 36.0); drawCircle(p.linea, p.w * 0.008f, Offset(c.x + (R * 0.92f * cos(a)).toFloat(), c.y + (R * 0.92f * sin(a)).toFloat())) }
                caja(p, 0.34f, 0.72f, 0.66f, 0.77f, SolidColor(hondo))
            }
            "Cultura" -> {
                // El ánfora del pecio: la vasija de dos asas, tumbada en la arena y cubierta de coral y de una estrella de mar.
                rotate(-28f, p.o(0.5f, 0.45f)) {
                    val anfora = Path().apply {
                        moveTo(p.x(0.45f), p.y(0.12f)); lineTo(p.x(0.55f), p.y(0.12f)); lineTo(p.x(0.55f), p.y(0.2f))
                        cubicTo(p.x(0.72f), p.y(0.26f), p.x(0.7f), p.y(0.56f), p.x(0.5f), p.y(0.72f))
                        cubicTo(p.x(0.3f), p.y(0.56f), p.x(0.28f), p.y(0.26f), p.x(0.45f), p.y(0.2f)); close()
                    }
                    drawPath(anfora, p.metal(0.3f, 0.7f))
                    listOf(-1f, 1f).forEach { s -> drawArc(p.linea, if (s < 0) 90f else -90f, 180f, false, Offset(p.x(0.5f + s * 0.1f) - p.w * 0.05f, p.y(0.16f)), Size(p.w * 0.1f, p.y(0.12f)), style = Stroke(lw)) }
                    if (p.hecho) listOf(Offset(0.42f, 0.4f), Offset(0.56f, 0.5f), Offset(0.48f, 0.6f)).forEach { o -> drawCircle(coral.copy(alpha = 0.75f), p.w * 0.03f, p.o(o.x, o.y)) }
                }
                if (p.hecho) {
                    val e = p.o(0.72f, 0.6f)
                    for (k in 0 until 5) { val a = Math.toRadians(-90.0 + k * 72.0); drawLine(coral, e, Offset(e.x + (p.w * 0.06f * cos(a)).toFloat(), e.y + (p.w * 0.06f * sin(a)).toFloat()), lw * 1.2f, StrokeCap.Round) }
                }
                drawRect(SolidColor(p.c(Color(0xFFD9C79A))), Offset(p.x(0.12f), p.y(0.72f)), Size(p.x(0.76f), p.y(0.05f)))
            }
            "Gente" -> {
                // El cardumen de plata: muchos peces que nadan juntos en la misma dirección, alrededor de uno de oro.
                val az = java.util.Random(11)
                fun pez(o: Offset, l: Float, b: Brush) {
                    drawPath(Path().apply { moveTo(o.x + l, o.y); quadraticTo(o.x, o.y - l * 0.45f, o.x - l * 0.7f, o.y); quadraticTo(o.x, o.y + l * 0.45f, o.x + l, o.y); close()
                        moveTo(o.x - l * 0.6f, o.y); lineTo(o.x - l * 1.1f, o.y - l * 0.35f); lineTo(o.x - l * 1.1f, o.y + l * 0.35f); close() }, b)
                }
                repeat(14) { pez(p.o(0.16f + az.nextFloat() * 0.68f, 0.12f + az.nextFloat() * 0.52f), p.w * (0.045f + az.nextFloat() * 0.02f), p.metal(0.2f, 0.8f)) }
                pez(p.o(0.5f, 0.38f), p.w * 0.09f, SolidColor(p.c(Color(0xFFD4AF37))))
                burbujas(4, 13, 0.1f, 0.05f, 0.9f, 0.2f)
                caja(p, 0.3f, 0.72f, 0.7f, 0.77f)
            }
            "Constancia" -> {
                // El batiscafo: el flotador largo lleno de gasolina, la torreta, la esfera de la tripulación colgada debajo,
                // el lastre de perdigones y la hélice.
                drawRoundRect(p.metal(0.1f, 0.9f), p.o(0.1f, 0.2f), Size(p.x(0.8f), p.y(0.18f)), CornerRadius(p.w * 0.09f))
                caja(p, 0.44f, 0.12f, 0.56f, 0.2f)
                drawLine(p.linea, p.o(0.12f, 0.29f), p.o(0.88f, 0.29f), lw * 0.4f)
                listOf(0.3f, 0.7f).forEach { x -> caja(p, x - 0.03f, 0.38f, x + 0.03f, 0.46f, SolidColor(hondo)) }
                drawLine(p.linea, p.o(0.5f, 0.38f), p.o(0.5f, 0.44f), lw)
                val c = p.o(0.5f, 0.54f); val R = p.w * 0.11f
                drawCircle(p.metal(0.38f, 0.62f), R, c)
                ojo(Offset(c.x - R * 0.1f, c.y + R * 0.1f), R * 0.38f)
                drawLine(p.linea, p.o(0.9f, 0.29f), p.o(0.96f, 0.22f), lw); drawLine(p.linea, p.o(0.9f, 0.29f), p.o(0.96f, 0.36f), lw)
                burbujas(6, 17, 0.2f, 0.02f, 0.8f, 0.12f)
                caja(p, 0.36f, 0.72f, 0.64f, 0.77f)
            }
            "Curiosos" -> {
                // El rape abisal: el cuerpo oscuro, la boca llena de dientes y la linterna de luz que cuelga delante.
                val cuerpo = Path().apply {
                    moveTo(p.x(0.24f), p.y(0.42f)); cubicTo(p.x(0.26f), p.y(0.2f), p.x(0.6f), p.y(0.18f), p.x(0.72f), p.y(0.38f))
                    lineTo(p.x(0.78f), p.y(0.46f)); cubicTo(p.x(0.62f), p.y(0.66f), p.x(0.3f), p.y(0.66f), p.x(0.24f), p.y(0.42f)); close()
                    moveTo(p.x(0.26f), p.y(0.44f)); lineTo(p.x(0.1f), p.y(0.32f)); lineTo(p.x(0.12f), p.y(0.56f)); close()
                }
                drawPath(cuerpo, p.metal(0.2f, 0.8f))
                if (p.hecho) for (k in 0 until 5) drawLine(espuma, p.o(0.62f + k * 0.03f, 0.42f), p.o(0.63f + k * 0.03f, 0.47f), lw * 0.5f)
                drawCircle(SolidColor(espuma), p.w * 0.03f, p.o(0.56f, 0.32f)); drawCircle(SolidColor(hondo), p.w * 0.014f, p.o(0.56f, 0.32f))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.2f)); quadraticTo(p.x(0.66f), p.y(0.02f), p.x(0.86f), p.y(0.18f)) }, p.linea, style = Stroke(lw * 0.8f))
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(luz.copy(alpha = 0.6f), Color.Transparent), p.o(0.86f, 0.2f), p.w * 0.1f), p.w * 0.1f, p.o(0.86f, 0.2f))
                drawCircle(SolidColor(luz), p.w * 0.03f, p.o(0.86f, 0.2f))
                caja(p, 0.34f, 0.72f, 0.66f, 0.77f, SolidColor(hondo))
            }
            else -> {
                // La medusa luminosa: la campana translúcida con sus cuatro gónadas, la orla de tentáculos y los brazos largos.
                val c = p.o(0.5f, 0.3f); val R = p.w * 0.26f
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(luz.copy(alpha = 0.4f), Color.Transparent), c, R * 1.5f), R * 1.5f, c)
                for (k in 0 until 4) {
                    val x0 = c.x + (k - 1.5f) * R * 0.2f
                    drawPath(Path().apply { moveTo(x0, c.y); quadraticTo(x0 + R * 0.2f, c.y + R * 0.8f, x0 - R * 0.05f, c.y + R * 1.5f) }, SolidColor(violeta), style = Stroke(lw * 1.2f, cap = StrokeCap.Round))
                }
                for (k in 0..8) {
                    val x0 = c.x - R * 0.9f + R * 1.8f * k / 8f
                    drawLine(p.linea, Offset(x0, c.y), Offset(x0 + (if (k % 2 == 0) 1 else -1) * R * 0.06f, c.y + R * 0.8f), lw * 0.4f)
                }
                val campana = Path().apply { moveTo(c.x - R, c.y); cubicTo(c.x - R, c.y - R * 1.2f, c.x + R, c.y - R * 1.2f, c.x + R, c.y); quadraticTo(c.x, c.y - R * 0.25f, c.x - R, c.y); close() }
                drawPath(campana, p.metal(0.24f, 0.76f), alpha = if (p.hecho) 0.85f else 1f)
                if (p.hecho) for (k in 0 until 4) drawArc(luz, 160f, 220f, false, Offset(c.x + (k - 1.5f) * R * 0.38f - R * 0.12f, c.y - R * 0.6f), Size(R * 0.24f, R * 0.24f), style = Stroke(lw * 0.7f))
                caja(p, 0.34f, 0.72f, 0.66f, 0.77f)
            }
        }
    }

    // ---------------------------------------------------------------- Soviético: medallas del trabajo y estrellas

    internal fun DrawScope.urss(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val rojo = p.c(Color(0xFFC1121F)); val negro = p.c(Color(0xFF161414)); val papel = p.c(Color(0xFFEFE6D2)); val oro = p.c(Color(0xFFD4A017))
        /** La cinta pentagonal de las medallas, con sus franjas. */
        fun cinta(franjas: List<Color>) {
            val c = Path().apply { moveTo(p.x(0.34f), p.y(0.04f)); lineTo(p.x(0.66f), p.y(0.04f)); lineTo(p.x(0.66f), p.y(0.2f)); lineTo(p.x(0.5f), p.y(0.28f)); lineTo(p.x(0.34f), p.y(0.2f)); close() }
            clipPath(c) { franjas.forEachIndexed { i, col -> drawRect(col, Offset(p.x(0.34f + i * 0.32f / franjas.size), p.y(0.02f)), Size(p.x(0.32f / franjas.size) + 1f, p.y(0.3f))) } }
            drawPath(c, p.metal(0.34f, 0.66f), style = Stroke(lw * 0.7f))
        }
        when (cat) {
            "Diario" -> {
                // El periódico mural: el tablero con su encabezado rojo en diagonal, las columnas de texto y la estrella.
                caja(p, 0.14f, 0.1f, 0.86f, 0.66f)
                caja(p, 0.17f, 0.13f, 0.83f, 0.63f, SolidColor(papel))
                drawPath(Path().apply { moveTo(p.x(0.17f), p.y(0.13f)); lineTo(p.x(0.83f), p.y(0.13f)); lineTo(p.x(0.83f), p.y(0.2f)); lineTo(p.x(0.17f), p.y(0.28f)); close() }, rojo)
                estrella(p.o(0.72f, 0.36f), p.w * 0.07f, p.w * 0.028f, 5, SolidColor(rojo))
                if (p.hecho) {
                    for (col in 0 until 2) for (k in 0 until 6) drawLine(negro.copy(alpha = 0.6f), p.o(0.21f + col * 0.24f, 0.34f + k * 0.045f), p.o(0.4f + col * 0.24f - (if (k == 5) 0.08f else 0f), 0.34f + k * 0.045f), lw * 0.5f)
                    drawRect(negro, Offset(p.x(0.64f), p.y(0.46f)), Size(p.x(0.15f), p.y(0.12f)))
                }
                listOf(0.3f, 0.7f).forEach { x -> caja(p, x - 0.03f, 0.66f, x + 0.03f, 0.77f) }
            }
            "Mundo" -> {
                // La insignia del Transiberiano: medallón con la locomotora de frente, los rieles que se pierden y la estrella.
                cinta(listOf(rojo, oro, rojo))
                val c = p.o(0.5f, 0.5f); val R = p.w * 0.26f
                drawCircle(p.metal(0.24f, 0.76f), R, c)
                drawCircle(SolidColor(papel), R * 0.8f, c)
                clipPath(Path().apply { addOval(Rect(c, R * 0.8f)) }) {
                    listOf(-1f, 1f).forEach { s -> drawLine(negro, Offset(c.x + s * R * 0.08f, c.y + R * 0.1f), Offset(c.x + s * R * 0.7f, c.y + R), lw * 0.8f) }
                    for (k in 0 until 4) { val y = c.y + R * (0.2f + k * 0.18f); val a = R * (0.12f + k * 0.14f); drawLine(negro, Offset(c.x - a, y), Offset(c.x + a, y), lw * 0.6f) }
                    drawRoundRect(SolidColor(negro), Offset(c.x - R * 0.25f, c.y - R * 0.5f), Size(R * 0.5f, R * 0.6f), CornerRadius(R * 0.1f))
                    drawCircle(oro, R * 0.1f, Offset(c.x, c.y - R * 0.2f))
                    estrella(Offset(c.x, c.y - R * 0.42f), R * 0.1f, R * 0.04f, 5, SolidColor(rojo))
                }
            }
            "Cuerpo" -> {
                // La insignia del deportista: escudo esmaltado con la franja azul, la estrella roja y la corona de espigas.
                cinta(listOf(p.c(Color(0xFF2E5C8A)), papel, p.c(Color(0xFF2E5C8A))))
                val escudo = Path().apply { moveTo(p.x(0.26f), p.y(0.3f)); lineTo(p.x(0.74f), p.y(0.3f)); lineTo(p.x(0.74f), p.y(0.5f)); quadraticTo(p.x(0.74f), p.y(0.66f), p.x(0.5f), p.y(0.74f)); quadraticTo(p.x(0.26f), p.y(0.66f), p.x(0.26f), p.y(0.5f)); close() }
                drawPath(escudo, p.metal(0.26f, 0.74f))
                clipPath(escudo) { drawRect(SolidColor(p.c(Color(0xFF2E5C8A))), Offset(p.x(0.26f), p.y(0.3f)), Size(p.x(0.48f), p.y(0.08f))) }
                estrella(p.o(0.5f, 0.52f), p.w * 0.12f, p.w * 0.05f, 5, SolidColor(rojo))
                if (p.hecho) listOf(-1f, 1f).forEach { s -> for (k in 0 until 4) drawOval(oro, Offset(p.x(0.5f + s * (0.17f - k * 0.02f)) - p.w * 0.02f, p.y(0.62f - k * 0.06f)), Size(p.w * 0.04f, p.w * 0.06f)) }
            }
            "Mente" -> {
                // El satélite de antenas: la esfera pulida, las cuatro antenas hacia atrás y su órbita.
                val c = p.o(0.4f, 0.34f); val R = p.w * 0.14f
                listOf(0.1f, 0.2f, 0.34f, 0.46f).forEachIndexed { i, dy -> drawLine(p.linea, Offset(c.x + R * 0.6f, c.y + R * (0.2f + i * 0.15f) - R * 0.3f), p.o(0.9f, 0.3f + dy), lw * 0.7f) }
                drawCircle(Brush.radialGradient(if (p.hecho) listOf(p.claro, p.base, p.oscuro) else listOf(p.sombra, p.sombra), Offset(c.x - R * 0.4f, c.y - R * 0.4f), R * 1.6f), R, c)
                drawOval(rojo, Offset(p.x(0.12f), p.y(0.26f)), Size(p.x(0.76f), p.y(0.18f)), style = Stroke(lw * 0.6f))
                estrella(p.o(0.5f, 0.62f), p.w * 0.06f, p.w * 0.024f, 5, SolidColor(rojo))
                caja(p, 0.46f, 0.68f, 0.54f, 0.77f)
            }
            "Cultura" -> {
                // El cartel constructivista: diagonales negras y rojas, el círculo y la cuña que lo atraviesa.
                caja(p, 0.18f, 0.06f, 0.82f, 0.72f)
                caja(p, 0.2f, 0.08f, 0.8f, 0.7f, SolidColor(papel))
                clipPath(Path().apply { addRect(Rect(p.x(0.2f), p.y(0.08f), p.x(0.8f), p.y(0.7f))) }) {
                    drawCircle(SolidColor(papel), p.w * 0.2f, p.o(0.4f, 0.36f))
                    drawCircle(negro, p.w * 0.2f, p.o(0.42f, 0.38f), style = Stroke(lw * 0.8f))
                    drawPath(Path().apply { moveTo(p.x(0.1f), p.y(0.62f)); lineTo(p.x(0.86f), p.y(0.2f)); lineTo(p.x(0.86f), p.y(0.3f)); close() }, rojo)
                    rotate(-30f, p.o(0.5f, 0.4f)) { drawRect(negro, Offset(p.x(0.1f), p.y(0.5f)), Size(p.x(0.9f), p.y(0.05f))) }
                    drawRect(rojo, Offset(p.x(0.2f), p.y(0.6f)), Size(p.x(0.6f), p.y(0.04f)))
                }
                caja(p, 0.46f, 0.72f, 0.54f, 0.77f)
            }
            "Gente" -> {
                // La estrella de la brigada: la estrella roja con borde de metal sobre un engrane, con la cinta.
                cinta(listOf(rojo, rojo))
                val c = p.o(0.5f, 0.5f)
                for (k in 0 until 12) { val a = k * PI / 6; drawLine(p.metal(0.2f, 0.8f), c, Offset(c.x + (p.w * 0.27f * cos(a)).toFloat(), c.y + (p.w * 0.27f * sin(a)).toFloat()), lw * 3) }
                drawCircle(p.metal(0.24f, 0.76f), p.w * 0.23f, c)
                estrella(c, p.w * 0.22f, p.w * 0.09f, 5, p.metal(0.28f, 0.72f))
                estrella(c, p.w * 0.18f, p.w * 0.072f, 5, SolidColor(rojo))
            }
            "Constancia" -> {
                // La medalla del trabajo: el disco con el engrane y las espigas, la estrella arriba y su cinta.
                cinta(listOf(rojo, oro, rojo))
                val c = p.o(0.5f, 0.52f); val R = p.w * 0.24f
                drawCircle(p.metal(0.26f, 0.74f), R, c)
                drawCircle(p.linea, R * 0.86f, c, style = Stroke(lw * 0.5f))
                for (k in 0 until 10) { val a = k * PI / 5; drawLine(p.linea, c, Offset(c.x + (R * 0.5f * cos(a)).toFloat(), c.y + (R * 0.5f * sin(a)).toFloat()), lw * 1.6f) }
                drawCircle(p.metal(0.4f, 0.6f), R * 0.36f, c)
                if (p.hecho) listOf(-1f, 1f).forEach { s -> for (k in 0 until 5) { val a = PI / 2 + s * (0.5 + k * 0.35); drawOval(oro, Offset(c.x + (R * 0.68f * cos(a)).toFloat() - p.w * 0.018f, c.y + (R * 0.68f * sin(a)).toFloat() - p.w * 0.03f), Size(p.w * 0.036f, p.w * 0.06f)) } }
                estrella(Offset(c.x, c.y - R * 0.05f), R * 0.2f, R * 0.08f, 5, SolidColor(rojo))
            }
            "Curiosos" -> {
                // El samovar: el cuerpo de latón con su grifo, las asas, la tetera encima y la base.
                caja(p, 0.44f, 0.04f, 0.56f, 0.12f, SolidColor(p.c(Color(0xFF2E5C8A))))
                drawPath(Path().apply { moveTo(p.x(0.38f), p.y(0.2f)); quadraticTo(p.x(0.5f), p.y(0.08f), p.x(0.62f), p.y(0.2f)); close() }, SolidColor(p.c(Color(0xFF2E5C8A))))
                drawRoundRect(p.metal(0.26f, 0.74f), Offset(p.x(0.26f), p.y(0.2f)), Size(p.x(0.48f), p.y(0.4f)), CornerRadius(p.w * 0.12f))
                if (p.hecho) for (k in 0 until 3) drawLine(p.linea.copy(alpha = 0.4f), p.o(0.27f, 0.3f + k * 0.1f), p.o(0.73f, 0.3f + k * 0.1f), lw * 0.4f)
                listOf(-1f, 1f).forEach { s -> drawArc(p.metal(0.2f, 0.8f), if (s < 0) 90f else -90f, 180f, false, Offset(p.x(0.5f + s * 0.24f) - p.w * 0.06f, p.y(0.26f)), Size(p.w * 0.12f, p.y(0.12f)), style = Stroke(lw)) }
                caja(p, 0.44f, 0.48f, 0.58f, 0.51f); caja(p, 0.56f, 0.48f, 0.6f, 0.56f)
                drawPath(Path().apply { moveTo(p.x(0.34f), p.y(0.6f)); lineTo(p.x(0.66f), p.y(0.6f)); lineTo(p.x(0.58f), p.y(0.7f)); lineTo(p.x(0.42f), p.y(0.7f)); close() }, p.metal(0.34f, 0.66f))
                caja(p, 0.3f, 0.7f, 0.7f, 0.77f)
            }
            else -> {
                // La estrella roja grande, con su borde dorado y el relieve en facetas.
                val c = p.o(0.5f, 0.4f)
                estrella(c, p.w * 0.38f, p.w * 0.15f, 5, p.metal(0.12f, 0.88f))
                estrella(c, p.w * 0.32f, p.w * 0.125f, 5, SolidColor(rojo))
                if (p.hecho) for (k in 0 until 5) { val a = -PI / 2 + k * 2 * PI / 5; drawLine(Color.Black.copy(alpha = 0.25f), c, Offset(c.x + (p.w * 0.32f * cos(a)).toFloat(), c.y + (p.w * 0.32f * sin(a)).toFloat()), lw * 0.6f) }
                caja(p, 0.46f, 0.66f, 0.54f, 0.77f)
            }
        }
    }
}
