package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
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
        TemaId.SEDA to mapOf("Diario" to "Sello de cinabrio", "Mundo" to "Brújula de cuchara", "Cuerpo" to "Disco de jade", "Mente" to "Porcelana azul",
            "Cultura" to "Carrete de seda", "Gente" to "Farolillo", "Constancia" to "Moneda de cobre", "Curiosos" to "Ábaco", "Tuyos" to "Ventana lunar"),
        TemaId.MONGOL to mapOf("Diario" to "Rollo de órdenes", "Mundo" to "Mapa de la estepa", "Cuerpo" to "Estribo", "Mente" to "Tamga",
            "Cultura" to "Yurta", "Gente" to "Placa de mensajero", "Constancia" to "Estandarte de nueve colas", "Curiosos" to "Ovoo", "Tuyos" to "Rueda del techo"),
        TemaId.COLONOS to mapOf("Diario" to "Cuaderno del pueblo", "Mundo" to "Brújula de bolsillo", "Cuerpo" to "Hacha", "Mente" to "Telar",
            "Cultura" to "Linterna", "Gente" to "Campana del pueblo", "Constancia" to "Yunque", "Curiosos" to "Barril", "Tuyos" to "Rueda de carreta"),
        TemaId.BOTANICO to mapOf("Diario" to "Lámina botánica", "Mundo" to "Caja de herborizar", "Cuerpo" to "Hoja de roble", "Mente" to "Lupa",
            "Cultura" to "Helecho", "Gente" to "Cerezas", "Constancia" to "Semilla que brota", "Curiosos" to "Campana de cristal", "Tuyos" to "Flor prensada"),
        TemaId.NAVIDAD to mapOf("Diario" to "Vela", "Mundo" to "Estrella", "Cuerpo" to "Bastón de caramelo", "Mente" to "Copo de nieve",
            "Cultura" to "Esfera", "Gente" to "Corona", "Constancia" to "Campana", "Curiosos" to "Regalo", "Tuyos" to "Piña"),
        TemaId.ANTIGUAS to mapOf("Diario" to "Libro de runas", "Mundo" to "Mapa de la travesía", "Cuerpo" to "Yelmo", "Mente" to "Cristal",
            "Cultura" to "Arpa", "Gente" to "Estandarte", "Constancia" to "Llave de hierro", "Curiosos" to "Gema", "Tuyos" to "Linterna de mina"),
        TemaId.DISTOPIA to mapOf("Diario" to "Expediente sellado", "Mundo" to "Pase de control", "Cuerpo" to "Insignia numerada", "Mente" to "Tarjeta perforada",
            "Cultura" to "Documento censurado", "Gente" to "Credencial", "Constancia" to "Sello de goma", "Curiosos" to "Cámara de vigilancia", "Tuyos" to "Altavoz"),
        TemaId.CIBER to mapOf("Diario" to "Registro del sistema", "Mundo" to "Mapa de la red", "Cuerpo" to "Monitor de pulso", "Mente" to "Árbol de directorios",
            "Cultura" to "Disquete", "Gente" to "Canal de charla", "Constancia" to "Tiempo en línea", "Curiosos" to "Laberinto ASCII", "Tuyos" to "Llave de raíz"),
        TemaId.NEON to mapOf("Diario" to "Letrero del puesto", "Mundo" to "Globo de neón", "Cuerpo" to "Corazón de neón", "Mente" to "Ojo que parpadea",
            "Cultura" to "Disco de la rocola", "Gente" to "Dos vasos brindando", "Constancia" to "Reloj de 24 horas", "Curiosos" to "Gato de la suerte", "Tuyos" to "Ficha de oro"),
        TemaId.REFUGIO to mapOf("Diario" to "Placa del cronista", "Mundo" to "Medalla de la superficie", "Cuerpo" to "Insignia de salud", "Mente" to "Medalla del átomo",
            "Cultura" to "Disco de vinilo", "Gente" to "Placa de la puerta", "Constancia" to "Engrane de la compuerta", "Curiosos" to "Contador Geiger", "Tuyos" to "Lata de conservas"),
        TemaId.ORDEN to mapOf("Diario" to "Placa del juramento", "Mundo" to "Estandarte de campaña", "Cuerpo" to "Guantelete", "Mente" to "Calibre de acero",
            "Cultura" to "Escudo de la Orden", "Gente" to "Yelmo de visera", "Constancia" to "Martillo de forja", "Curiosos" to "Válvula de vacío", "Tuyos" to "Espada forjada"),
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

    fun DrawScope.pedestalMas(tema: TemaId, p: TrofeosTema.Pincel, ped: Color) {
        val w = size.width; val h = size.height
        fun piedra(c: Color) = Brush.verticalGradient(listOf(lerp(c, Color.White, 0.18f), c, lerp(c, Color.Black, 0.2f)), h * 0.77f, h * 0.96f)
        when (tema) {
            TemaId.ANDALUS -> {
                // Zócalo de azulejo: rombos verdes y azules entre filetes de yeso.
                val c = if (p.hecho) Color(0xFFEDE3CF) else ped
                drawRect(piedra(c), Offset(w * 0.16f, h * 0.79f), Size(w * 0.68f, h * 0.16f))
                if (p.hecho) for (k in 0 until 6) {
                    val cx = w * (0.22f + k * 0.112f); val cy = h * 0.87f; val r = w * 0.045f
                    drawPath(Path().apply { moveTo(cx, cy - r); lineTo(cx + r, cy); lineTo(cx, cy + r); lineTo(cx - r, cy); close() }, if (k % 2 == 0) Color(0xFF1E6B5A) else Color(0xFF1E4F8A))
                }
                drawRect(piedra(c), Offset(w * 0.13f, h * 0.775f), Size(w * 0.74f, h * 0.02f))
            }
            TemaId.SEDA -> {
                // Peana de laca roja con patas curvas.
                val c = if (p.hecho) Color(0xFF8E1B1B) else ped
                drawRoundRect(piedra(c), Offset(w * 0.16f, h * 0.79f), Size(w * 0.68f, h * 0.06f), CornerRadius(w * 0.02f))
                listOf(0.22f, 0.78f).forEach { x -> drawPath(Path().apply { moveTo(w * (x - 0.04f), h * 0.85f); quadraticTo(w * x, h * 0.9f, w * (x + if (x < 0.5f) -0.05f else 0.05f), h * 0.955f); lineTo(w * (x + 0.02f), h * 0.955f); quadraticTo(w * (x + 0.02f), h * 0.9f, w * (x + 0.04f), h * 0.85f); close() }, c) }
                if (p.hecho) drawRect(Color(0xFFD4AF37), Offset(w * 0.16f, h * 0.79f), Size(w * 0.68f, h * 0.008f))
            }
            TemaId.MONGOL -> {
                // Fieltros apilados, el de arriba con su greca roja.
                val fieltro = if (p.hecho) Color(0xFFEDE6D6) else ped
                for (k in 0 until 3) drawRoundRect(lerp(fieltro, Color.Black, k * 0.06f), Offset(w * (0.2f - k * 0.03f), h * (0.79f + k * 0.055f)), Size(w * (0.6f + k * 0.06f), h * 0.055f), CornerRadius(w * 0.03f))
                if (p.hecho) { var x = w * 0.24f; while (x < w * 0.74f) { drawRect(Color(0xFFB0281E), Offset(x, h * 0.81f), Size(w * 0.03f, h * 0.015f)); x += w * 0.06f } }
            }
            TemaId.COLONOS -> {
                // Un tocón de árbol con sus anillos.
                val c = if (p.hecho) Color(0xFF7A5232) else ped
                drawRect(Brush.horizontalGradient(listOf(lerp(c, Color.Black, 0.3f), c, lerp(c, Color.White, 0.1f), c, lerp(c, Color.Black, 0.3f)), w * 0.2f, w * 0.8f), Offset(w * 0.2f, h * 0.8f), Size(w * 0.6f, h * 0.15f))
                drawOval(if (p.hecho) Color(0xFFC9A57A) else ped, Offset(w * 0.2f, h * 0.775f), Size(w * 0.6f, h * 0.05f))
                if (p.hecho) for (k in 1..3) drawOval(Color(0xFF7A5232).copy(alpha = 0.5f), Offset(w * (0.5f - 0.3f * k / 4), h * (0.8f - 0.025f * k / 4)), Size(w * 0.6f * k / 4, h * 0.05f * k / 4), style = Stroke(w * 0.004f))
            }
            TemaId.BOTANICO -> {
                // Una mesita de hierro forjado con tablero de madera.
                val hierro = if (p.hecho) Color(0xFF2F3A33) else ped
                drawRect(if (p.hecho) Color(0xFF8A6A44) else ped, Offset(w * 0.14f, h * 0.78f), Size(w * 0.72f, h * 0.035f))
                listOf(0.22f, 0.78f).forEach { x -> drawLine(hierro, Offset(w * x, h * 0.815f), Offset(w * x, h * 0.955f), w * 0.02f) }
                drawPath(Path().apply { moveTo(w * 0.22f, h * 0.9f); quadraticTo(w * 0.5f, h * 0.83f, w * 0.78f, h * 0.9f) }, hierro, style = Stroke(w * 0.012f))
                drawCircle(hierro, w * 0.03f, Offset(w * 0.5f, h * 0.87f), style = Stroke(w * 0.01f))
            }
            TemaId.NAVIDAD -> {
                // Un montoncito de nieve con una rama de acebo.
                val nieve = if (p.hecho) Color(0xFFF4F7FB) else ped
                drawPath(Path().apply { moveTo(w * 0.1f, h * 0.955f); cubicTo(w * 0.15f, h * 0.8f, w * 0.4f, h * 0.76f, w * 0.5f, h * 0.79f); cubicTo(w * 0.62f, h * 0.76f, w * 0.88f, h * 0.8f, w * 0.9f, h * 0.955f); close() }, nieve)
                if (p.hecho) {
                    listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(w * 0.5f, h * 0.83f); quadraticTo(w * (0.5f + s * 0.1f), h * 0.78f, w * (0.5f + s * 0.2f), h * 0.84f); quadraticTo(w * (0.5f + s * 0.1f), h * 0.86f, w * 0.5f, h * 0.83f); close() }, Color(0xFF1F6B3A)) }
                    listOf(-0.03f, 0.0f, 0.03f).forEach { dx -> drawCircle(Color(0xFFB3122E), w * 0.022f, Offset(w * (0.5f + dx), h * 0.825f)) }
                }
            }
            TemaId.DISTOPIA -> {
                // Bloque de concreto con su número de inventario estarcido y una franja roja.
                val c = if (p.hecho) Color(0xFF9A9890) else ped
                drawRect(piedra(c), Offset(w * 0.16f, h * 0.79f), Size(w * 0.68f, h * 0.16f))
                if (p.hecho) {
                    drawRect(Color(0xFFC8102E), Offset(w * 0.16f, h * 0.79f), Size(w * 0.68f, h * 0.02f))
                    for (k in 0 until 6) drawRect(Color(0xFF141414).copy(alpha = 0.55f), Offset(w * (0.3f + k * 0.07f), h * 0.85f), Size(w * 0.035f, h * 0.05f))
                }
            }
            TemaId.CIBER -> {
                // Una unidad de rack: chapa negra con rejilla de ventilación y sus luces de estado.
                val c = if (p.hecho) Color(0xFF111925) else ped
                drawRect(piedra(c), Offset(w * 0.14f, h * 0.79f), Size(w * 0.72f, h * 0.16f))
                if (p.hecho) {
                    drawRect(Color(0xFF15404A), Offset(w * 0.14f, h * 0.79f), Size(w * 0.72f, h * 0.16f), style = Stroke(w * 0.008f))
                    for (k in 0 until 8) drawLine(Color(0xFF05070B), Offset(w * (0.2f + k * 0.035f), h * 0.82f), Offset(w * (0.2f + k * 0.035f), h * 0.92f), w * 0.014f)
                    for (k in 0 until 4) drawCircle(if (k == 2) Color(0xFF86A0B3) else Color(0xFF00E5FF), w * 0.016f, Offset(w * (0.6f + k * 0.06f), h * 0.87f))
                }
            }
            TemaId.NEON -> {
                // Una caja de cerveza de plástico puesta de cabeza, como los bancos de los puestos, con sus asas caladas.
                val c = if (p.hecho) Color(0xFF2F6B3A) else ped
                drawRoundRect(piedra(c), Offset(w * 0.15f, h * 0.79f), Size(w * 0.7f, h * 0.16f), CornerRadius(w * 0.015f))
                if (p.hecho) {
                    for (k in 0 until 6) drawRect(Color(0xFF173A1E), Offset(w * (0.2f + k * 0.105f), h * 0.84f), Size(w * 0.07f, h * 0.06f))
                    drawRoundRect(Color(0xFF173A1E), Offset(w * 0.42f, h * 0.805f), Size(w * 0.16f, h * 0.022f), CornerRadius(w * 0.01f))
                    drawLine(Color(0xFFFF3D9A).copy(alpha = 0.5f), Offset(w * 0.15f, h * 0.79f), Offset(w * 0.85f, h * 0.79f), w * 0.006f)
                }
            }
            TemaId.REFUGIO -> {
                // Una caja de suministros de chapa verde azulado, con remaches y su placa amarilla.
                val c = if (p.hecho) Color(0xFF1C6E6A) else ped
                drawRoundRect(piedra(c), Offset(w * 0.15f, h * 0.79f), Size(w * 0.7f, h * 0.16f), CornerRadius(w * 0.02f))
                if (p.hecho) {
                    drawRect(Color(0xFF124A47), Offset(w * 0.15f, h * 0.835f), Size(w * 0.7f, h * 0.008f))
                    drawRoundRect(Color(0xFFE8B83A), Offset(w * 0.38f, h * 0.855f), Size(w * 0.24f, h * 0.06f), CornerRadius(w * 0.01f))
                    for (k in 0 until 5) drawCircle(Color(0xFF0C3431), w * 0.012f, Offset(w * (0.2f + k * 0.15f), h * 0.81f))
                }
            }
            TemaId.ORDEN -> {
                // Un bloque de acero remachado con el paño carmesí de la Orden y su filete de latón.
                val c = if (p.hecho) Color(0xFF6E757D) else ped
                drawRect(piedra(c), Offset(w * 0.16f, h * 0.8f), Size(w * 0.68f, h * 0.15f))
                drawRect(piedra(lerp(c, Color.Black, 0.1f)), Offset(w * 0.12f, h * 0.775f), Size(w * 0.76f, h * 0.03f))
                if (p.hecho) {
                    drawPath(Path().apply { moveTo(w * 0.36f, h * 0.805f); lineTo(w * 0.64f, h * 0.805f); lineTo(w * 0.62f, h * 0.93f); lineTo(w * 0.5f, h * 0.9f); lineTo(w * 0.38f, h * 0.93f); close() }, Color(0xFF9E1B22))
                    drawLine(Color(0xFFD6B25A), Offset(w * 0.36f, h * 0.81f), Offset(w * 0.64f, h * 0.81f), w * 0.01f)
                    listOf(0.21f, 0.28f, 0.72f, 0.79f).forEach { x -> drawCircle(Color(0xFF2A2E33), w * 0.014f, Offset(w * x, h * 0.87f)) }
                }
            }
            TemaId.LABORATORIO -> {
                // La mesa del laboratorio: cubierta blanca con su canto cian y la etiqueta de inventario.
                val c = if (p.hecho) Color(0xFFE6EDF2) else ped
                drawRoundRect(piedra(c), Offset(w * 0.12f, h * 0.78f), Size(w * 0.76f, h * 0.05f), CornerRadius(w * 0.015f))
                drawRect(piedra(lerp(c, Color.Black, 0.08f)), Offset(w * 0.18f, h * 0.83f), Size(w * 0.64f, h * 0.12f))
                if (p.hecho) {
                    drawRect(Color(0xFF0B8FA1), Offset(w * 0.12f, h * 0.825f), Size(w * 0.76f, h * 0.008f))
                    drawRect(Color.White, Offset(w * 0.38f, h * 0.86f), Size(w * 0.24f, h * 0.05f))
                    for (k in 0 until 6) drawLine(Color(0xFF12202B), Offset(w * (0.4f + k * 0.035f), h * 0.865f), Offset(w * (0.4f + k * 0.035f), h * 0.9f), w * (if (k % 2 == 0) 0.008f else 0.004f))
                }
            }
            TemaId.ESPACIO -> {
                // La plataforma de lanzamiento: losa de concreto con el foso de las llamas y las luces naranjas.
                val c = if (p.hecho) Color(0xFF9AA3B2) else ped
                drawRect(piedra(c), Offset(w * 0.12f, h * 0.8f), Size(w * 0.76f, h * 0.15f))
                drawRect(if (p.hecho) Color(0xFF26324F) else ped, Offset(w * 0.4f, h * 0.8f), Size(w * 0.2f, h * 0.08f))
                if (p.hecho) {
                    listOf(0.18f, 0.3f, 0.7f, 0.82f).forEach { x -> drawCircle(Color(0xFFE8601C), w * 0.014f, Offset(w * x, h * 0.9f)) }
                    drawRect(Color(0xFF1B3A8C), Offset(w * 0.12f, h * 0.935f), Size(w * 0.76f, h * 0.015f))
                }
            }
            TemaId.ABISMO -> {
                // Una roca del fondo: el lomo irregular de basalto, con coral rojo, una anémona y arena alrededor.
                val c = if (p.hecho) Color(0xFF3E4A52) else ped
                val roca = Path().apply {
                    moveTo(w * 0.1f, h * 0.95f); lineTo(w * 0.14f, h * 0.84f); quadraticTo(w * 0.3f, h * 0.76f, w * 0.5f, h * 0.79f)
                    quadraticTo(w * 0.72f, h * 0.76f, w * 0.86f, h * 0.84f); lineTo(w * 0.9f, h * 0.95f); close()
                }
                drawPath(roca, piedra(c))
                if (p.hecho) {
                    drawRect(Color(0xFFD9C79A).copy(alpha = 0.8f), Offset(w * 0.06f, h * 0.93f), Size(w * 0.88f, h * 0.025f))
                    listOf(0.2f, 0.24f, 0.28f).forEachIndexed { k, x -> drawLine(Color(0xFFC0563F), Offset(w * x, h * 0.84f), Offset(w * (x + 0.02f * (k - 1)), h * (0.76f - k * 0.015f)), w * 0.012f, StrokeCap.Round) }
                    for (k in 0 until 6) { val a = Math.toRadians(-160.0 + k * 28.0); drawLine(Color(0xFFE29BFF), Offset(w * 0.76f, h * 0.81f), Offset(w * 0.76f + (w * 0.045f * cos(a)).toFloat(), h * 0.81f + (w * 0.045f * sin(a)).toFloat()), w * 0.008f, StrokeCap.Round) }
                }
            }
            TemaId.URSS -> {
                // Un plinto de granito rojo en escalón, con la estrella dorada al frente.
                val c = if (p.hecho) Color(0xFF7A2A26) else ped
                drawRect(piedra(c), Offset(w * 0.18f, h * 0.79f), Size(w * 0.64f, h * 0.16f))
                drawRect(piedra(lerp(c, Color.Black, 0.15f)), Offset(w * 0.12f, h * 0.92f), Size(w * 0.76f, h * 0.035f))
                if (p.hecho) with(TrofeosTema) { estrella(Offset(w * 0.5f, h * 0.865f), w * 0.06f, w * 0.024f, 5, SolidColor(Color(0xFFD4A017))) }
            }
            TemaId.ANTIGUAS -> {
                // Un sillar con runas inventadas.
                val c = if (p.hecho) Color(0xFF7D7A72) else ped
                drawRect(piedra(c), Offset(w * 0.18f, h * 0.79f), Size(w * 0.64f, h * 0.16f))
                if (p.hecho) runas(Offset(w * 0.26f, h * 0.85f), w * 0.48f, h * 0.05f, Color(0xFF2B2A28).copy(alpha = 0.55f), w * 0.006f, 5)
            }
            // Los pedestales de los siete primeros están en [TrofeosTema.pedestal]: no llegan aquí.
            TemaId.ROMANO -> Unit
            TemaId.HELENO -> Unit
            TemaId.EGIPTO -> Unit
            TemaId.MEDIEVAL -> Unit
            TemaId.TEMPLARIO -> Unit
            TemaId.GOTICO -> Unit
            TemaId.RENACIMIENTO -> Unit
            TemaId.PIRATA -> Unit
            TemaId.NAPOLEON -> Unit
            TemaId.REVOLUCION -> Unit
        }
    }

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

    /** Arte ASCII: las [lineas] en letra monoespaciada, del tamaño que quepa en el rectángulo (en fracciones del pincel) y centradas. */
    private fun DrawScope.ascii(p: TrofeosTema.Pincel, lineas: List<String>, x0: Float, y0: Float, x1: Float, y1: Float, color: Color) {
        val cols = lineas.maxOf { it.length }.coerceAtLeast(1)
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { this.color = color.toArgb(); typeface = android.graphics.Typeface.MONOSPACE; textSize = 100f }
        val anchoLetra = paint.measureText("M") / 100f
        val interlinea = 1.16f
        val tam = minOf(p.x(x1 - x0) / (cols * anchoLetra), p.y(y1 - y0) / (lineas.size * interlinea))
        paint.textSize = tam
        val izq = p.x((x0 + x1) / 2) - cols * anchoLetra * tam / 2
        var y = p.y((y0 + y1) / 2) - lineas.size * interlinea * tam / 2 + tam * 0.9f
        val lienzo = drawContext.canvas.nativeCanvas
        lineas.forEach { l -> lienzo.drawText(l, izq, y, paint); y += tam * interlinea }
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

    // ---------------------------------------------------------------- Terminal: arte ASCII en ventanas de consola

    internal fun DrawScope.terminal(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val cian = Color(0xFF00E5FF); val negro = p.c(Color(0xFF05070B))
        val (titulo, dibujo) = when (cat) {
            "Diario" -> "syslog" to listOf("$ tail diario", "[ok] 07:10", "[ok] 13:42", "[ok] 22:05", "> _")
            "Mundo" -> "red" to listOf("  .-'''-.", " /|  |  |\\", "|-+--+--+-|", " \\|  |  |/", "  '-...-'")
            "Cuerpo" -> "pulso" to listOf("BPM 064", "    /\\", "___/  \\  ___", "       \\/")
            "Mente" -> "árbol" to listOf("~/mente", "+-- ideas/", "|   +-- a.txt", "|   `-- b.txt", "`-- dudas/")
            "Cultura" -> "disco" to listOf(" _________", "| |_____| |", "|  _____  |", "| |     | |", "|_|_____|_|")
            "Gente" -> "charla" to listOf("<tú> hola", "<yo> hola :)", "<tú> ¿café?", "<yo> va <3")
            "Constancia" -> "uptime" to listOf("UPTIME", "[#########-]", "  99.9 %", " 7d 00:00")
            "Curiosos" -> "laberinto" to listOf("+--+--+--+", "|  |     |", "+  +  +--+", "|     |  |", "+--+--+--+")
            else -> "raíz" to listOf(" .--.", "(    )----+-+", " '--'     ' '", "root@vm # _")
        }
        // La ventana: marco de metal, barra de título con su nombre y sus botones, y la pantalla negra.
        caja(p, 0.08f, 0.06f, 0.92f, 0.7f)
        caja(p, 0.11f, 0.14f, 0.89f, 0.67f, SolidColor(negro))
        if (p.hecho) {
            drawCircle(Brush.radialGradient(listOf(cian.copy(alpha = 0.16f), Color.Transparent), p.o(0.5f, 0.4f), p.w * 0.42f), p.w * 0.42f, p.o(0.5f, 0.4f))
            mono(titulo, p.x(0.13f), p.y(0.122f), p.w * 0.06f, Color(0xFF05070B), negrita = true)
            for (k in 0 until 3) drawRect(Color(0xFF05070B), Offset(p.x(0.7f + k * 0.06f), p.y(0.083f)), Size(p.x(0.04f), p.y(0.035f)), style = Stroke(lw * 0.6f))
            // Las líneas de barrido de la pantalla.
            var y = p.y(0.15f); while (y < p.y(0.66f)) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(p.x(0.11f), y), Offset(p.x(0.89f), y), lw * 0.25f); y += lw * 0.9f }
        }
        ascii(p, dibujo, 0.15f, 0.17f, 0.85f, 0.64f, if (p.hecho) cian else p.sombra)
        // El cuello y la base del monitor.
        caja(p, 0.42f, 0.7f, 0.58f, 0.74f)
        caja(p, 0.3f, 0.74f, 0.7f, 0.77f)
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
