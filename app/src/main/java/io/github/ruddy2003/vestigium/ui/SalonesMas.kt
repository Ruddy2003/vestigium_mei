package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Los halls del lote 2, cada uno enmarcando las columnas de piezas: el patio andalusí de arcos de herradura con celosías,
 * el caravasar con ventanas lunares y farolillos, la gran yurta con su enrejado y los postes del techo, el almacén de los
 * colonos con tablones y repisas, el invernadero de cristal y hierro, la sala de Navidad con chimenea, árbol y adornos
 * que cuelgan, y el gran salón de piedra de Tierras Antiguas con pilares y estandartes.
 */
object SalonesMas {
    // Cada escena se llama directo desde [SalonesTema.escena].

    fun DrawScope.pedestalMas(tema: TemaId, cs: ColorScheme, metal: Color) {
        val w = size.width; val h = size.height
        fun piedra(c: Color) = Brush.verticalGradient(listOf(lerp(c, Color.White, 0.2f), c, lerp(c, Color.Black, 0.25f)), 0f, h)
        when (tema) {
            TemaId.ANDALUS -> {
                drawRect(piedra(Color(0xFFEDE3CF)), Offset(w * 0.14f, 0f), Size(w * 0.72f, h))
                for (k in 0 until 5) { val cx = w * (0.22f + k * 0.14f); val r = h * 0.32f
                    drawPath(Path().apply { moveTo(cx, h / 2 - r); lineTo(cx + r, h / 2); lineTo(cx, h / 2 + r); lineTo(cx - r, h / 2); close() }, if (k % 2 == 0) Color(0xFF1E6B5A) else Color(0xFF1E4F8A)) }
            }
            // Un altar de piedra en dos gradas, con la franja de cinabrio y una fila de cuentas de jade al frente.
            TemaId.MESO -> {
                drawRect(piedra(Color(0xFFE2D6BC)), Offset(w * 0.2f, 0f), Size(w * 0.6f, h * 0.45f))
                drawRect(piedra(Color(0xFFD6C8A8)), Offset(w * 0.1f, h * 0.45f), Size(w * 0.8f, h * 0.55f))
                drawRect(Color(0xFFA3301E), Offset(w * 0.2f, h * 0.12f), Size(w * 0.6f, h * 0.1f))
                for (k in 0 until 5) drawCircle(Color(0xFF2F7D57), h * 0.08f, Offset(w * (0.22f + k * 0.14f), h * 0.72f))
            }
            TemaId.SEDA -> { drawRoundRect(piedra(Color(0xFF8E1B1B)), Offset(w * 0.12f, 0f), Size(w * 0.76f, h * 0.55f), CornerRadius(h * 0.2f)); drawRect(Color(0xFFD4AF37), Offset(w * 0.12f, 0f), Size(w * 0.76f, h * 0.1f))
                listOf(0.2f, 0.8f).forEach { x -> drawRect(Color(0xFF8E1B1B), Offset(w * x - w * 0.03f, h * 0.5f), Size(w * 0.06f, h * 0.5f)) } }
            // Sanbō: la bandeja de ofrendas de madera lacada en negro, sobre su caja con el vano recortado (kurikata).
            TemaId.EDO -> {
                val laca = Color(0xFF1C1A1D)
                drawRect(piedra(laca), Offset(w * 0.1f, 0f), Size(w * 0.8f, h * 0.2f)); drawRect(Color(0xFFB8322A), Offset(w * 0.13f, h * 0.03f), Size(w * 0.74f, h * 0.05f))
                drawPath(Path().apply {
                    fillType = PathFillType.EvenOdd
                    addRect(Rect(w * 0.18f, h * 0.2f, w * 0.82f, h))
                    addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(w * 0.36f, h * 0.38f, w * 0.64f, h * 0.78f), CornerRadius(w * 0.14f)))
                }, piedra(laca))
                drawRect(Color(0xFFD4AF37).copy(alpha = 0.6f), Offset(w * 0.18f, h * 0.2f), Size(w * 0.64f, h * 0.025f))
            }
            TemaId.MONGOL -> for (k in 0 until 3) drawRoundRect(lerp(Color(0xFFEDE6D6), Color.Black, k * 0.08f), Offset(w * (0.18f - k * 0.04f), h * k / 3f), Size(w * (0.64f + k * 0.08f), h / 3f), CornerRadius(h * 0.15f))
            TemaId.COLONOS -> { drawRect(Brush.horizontalGradient(listOf(Color(0xFF5A3A22), Color(0xFF8A5A2B), Color(0xFF5A3A22)), w * 0.2f, w * 0.8f), Offset(w * 0.2f, h * 0.2f), Size(w * 0.6f, h * 0.8f)); drawOval(Color(0xFFC9A57A), Offset(w * 0.2f, 0f), Size(w * 0.6f, h * 0.4f)) }
            TemaId.BOTANICO -> { drawRect(Color(0xFF8A6A44), Offset(w * 0.1f, 0f), Size(w * 0.8f, h * 0.3f)); listOf(0.2f, 0.8f).forEach { x -> drawLine(Color(0xFF2F3A33), Offset(w * x, h * 0.3f), Offset(w * x, h), w * 0.03f) } }
            // Un bloque de hormigón con dos varillas oxidadas que asoman, una grieta y musgo encima.
            TemaId.RUINAS -> { drawRect(piedra(Color(0xFF9C9C92)), Offset(w * 0.12f, h * 0.1f), Size(w * 0.76f, h * 0.9f))
                listOf(0.28f, 0.7f).forEach { x -> drawLine(Color(0xFF8A4A22), Offset(w * x, 0f), Offset(w * x + w * 0.03f, h * 0.14f), w * 0.025f) }
                drawOval(Color(0xFF4F7A33).copy(alpha = 0.7f), Offset(w * 0.1f, h * 0.02f), Size(w * 0.4f, h * 0.18f))
                drawLine(Color(0xFF3E3F38).copy(alpha = 0.5f), Offset(w * 0.6f, h * 0.1f), Offset(w * 0.52f, h * 0.6f), w * 0.01f) }
            TemaId.NAVIDAD -> {
                // Una rama de acebo con sus bayas, de la que «cuelga» el adorno.
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(w / 2, h * 0.5f); quadraticTo(w * (0.5f + s * 0.16f), 0f, w * (0.5f + s * 0.34f), h * 0.4f); quadraticTo(w * (0.5f + s * 0.16f), h * 0.8f, w / 2, h * 0.5f); close() }, Color(0xFF1F6B3A)) }
                listOf(-0.05f, 0f, 0.05f).forEach { dx -> drawCircle(Color(0xFFB3122E), h * 0.16f, Offset(w * (0.5f + dx), h * 0.5f)) }
            }
            TemaId.DISTOPIA -> {
                // El cajón superior de un archivero metálico, con su tarjetero y la jaladera.
                drawRect(piedra(Color(0xFF8A8D91)), Offset(w * 0.12f, 0f), Size(w * 0.76f, h))
                drawRect(Color(0xFFC8102E), Offset(w * 0.12f, 0f), Size(w * 0.76f, h * 0.12f))
                drawRect(Color(0xFFF2EFE6), Offset(w * 0.42f, h * 0.3f), Size(w * 0.16f, h * 0.3f))
                drawRect(Color(0xFF3A3C40), Offset(w * 0.44f, h * 0.68f), Size(w * 0.12f, h * 0.12f))
            }
            TemaId.CIBER -> {
                // La tapa de un gabinete: chapa negra, filo cian y una hilera de luces de estado.
                drawRect(piedra(Color(0xFF111925)), Offset(w * 0.12f, 0f), Size(w * 0.76f, h))
                drawRect(Color(0xFF00E5FF).copy(alpha = 0.8f), Offset(w * 0.12f, 0f), Size(w * 0.76f, h * 0.1f))
                for (k in 0 until 6) drawRect(Color(0xFF00E5FF).copy(alpha = if (k % 3 == 1) 0.3f else 0.85f), Offset(w * (0.2f + k * 0.1f), h * 0.42f), Size(w * 0.05f, h * 0.22f))
            }
            TemaId.NEON -> {
                // Una caja de cerveza de plástico verde, de cabeza, con sus asas caladas y el reflejo magenta del letrero.
                drawRoundRect(piedra(Color(0xFF2F6B3A)), Offset(w * 0.12f, 0f), Size(w * 0.76f, h), CornerRadius(h * 0.12f))
                for (k in 0 until 5) drawRect(Color(0xFF173A1E), Offset(w * (0.18f + k * 0.135f), h * 0.3f), Size(w * 0.09f, h * 0.4f))
                drawRect(Color(0xFFFF3D9A).copy(alpha = 0.55f), Offset(w * 0.12f, 0f), Size(w * 0.76f, h * 0.08f))
            }
            TemaId.REFUGIO -> {
                // La placa de chapa verde azulado del refugio, con su franja amarilla y remaches.
                drawRoundRect(piedra(Color(0xFF1C6E6A)), Offset(w * 0.12f, 0f), Size(w * 0.76f, h), CornerRadius(h * 0.15f))
                drawRect(Color(0xFFE8B83A), Offset(w * 0.12f, h * 0.38f), Size(w * 0.76f, h * 0.24f))
                listOf(0.18f, 0.82f).forEach { x -> drawCircle(Color(0xFF0C3431), h * 0.12f, Offset(w * x, h * 0.5f)) }
            }
            TemaId.ORDEN -> {
                // Un capitel de acero remachado con el paño carmesí.
                drawRect(piedra(Color(0xFF6E757D)), Offset(w * 0.12f, 0f), Size(w * 0.76f, h))
                drawRect(Color(0xFF9E1B22), Offset(w * 0.38f, 0f), Size(w * 0.24f, h))
                drawRect(Color(0xFFD6B25A), Offset(w * 0.12f, 0f), Size(w * 0.76f, h * 0.12f))
                listOf(0.18f, 0.3f, 0.7f, 0.82f).forEach { x -> drawCircle(Color(0xFF2A2E33), h * 0.1f, Offset(w * x, h * 0.55f)) }
            }
            TemaId.VAPOR -> {
                // Un capitel de hierro fundido, con la banda de latón arriba y el engrane de la compañía al frente.
                drawRect(piedra(Color(0xFF3B3835)), Offset(w * 0.12f, 0f), Size(w * 0.76f, h))
                drawRect(Color(0xFFC9A04A), Offset(w * 0.1f, 0f), Size(w * 0.8f, h * 0.16f))
                drawPath(Vapor.engrane(Offset(w * 0.5f, h * 0.56f), h * 0.32f, 10, 0.78f), Color(0xFFC9A04A))
                drawCircle(Color(0xFF3B3835), h * 0.12f, Offset(w * 0.5f, h * 0.56f))
                listOf(0.2f, 0.8f).forEach { x -> drawCircle(Color(0xFF9C4A32), h * 0.09f, Offset(w * x, h * 0.56f)) }
            }
            TemaId.LABORATORIO -> {
                // La cubierta blanca de la mesa con su canto cian.
                drawRoundRect(piedra(Color(0xFFE6EDF2)), Offset(w * 0.1f, 0f), Size(w * 0.8f, h), CornerRadius(h * 0.2f))
                drawRect(Color(0xFF0B8FA1), Offset(w * 0.1f, h * 0.78f), Size(w * 0.8f, h * 0.12f))
                drawRect(Color.White, Offset(w * 0.42f, h * 0.25f), Size(w * 0.16f, h * 0.35f))
            }
            TemaId.ESPACIO -> {
                // La base de la montura del telescopio: concreto, franja marina y las luces naranjas.
                drawRect(piedra(Color(0xFF9AA3B2)), Offset(w * 0.12f, 0f), Size(w * 0.76f, h))
                drawRect(Color(0xFF1B3A8C), Offset(w * 0.12f, h * 0.7f), Size(w * 0.76f, h * 0.18f))
                listOf(0.2f, 0.8f).forEach { x -> drawCircle(Color(0xFFE8601C), h * 0.13f, Offset(w * x, h * 0.35f)) }
            }
            TemaId.ABISMO -> {
                // Un bloque de lastre de hierro con su argolla y la placa de latón con la cota.
                drawRoundRect(piedra(Color(0xFF3E4A52)), Offset(w * 0.12f, 0f), Size(w * 0.76f, h), androidx.compose.ui.geometry.CornerRadius(h * 0.12f))
                drawRect(Color(0xFFB8913E), Offset(w * 0.36f, h * 0.3f), Size(w * 0.28f, h * 0.4f))
                listOf(0.18f, 0.82f).forEach { x -> drawCircle(Color(0xFFB8913E), h * 0.1f, Offset(w * x, h * 0.5f)); drawCircle(Color(0xFF1C252B), h * 0.04f, Offset(w * x, h * 0.5f)) }
            }
            TemaId.URSS -> {
                // El zócalo de granito rojo con su franja dorada y la estrella.
                drawRect(piedra(Color(0xFF7A2A26)), Offset(w * 0.12f, 0f), Size(w * 0.76f, h))
                drawRect(Color(0xFFD4A017), Offset(w * 0.12f, 0f), Size(w * 0.76f, h * 0.12f))
                with(TrofeosTema) { estrella(Offset(w * 0.5f, h * 0.55f), h * 0.32f, h * 0.13f, 5, Brush.linearGradient(listOf(Color(0xFFD4A017), Color(0xFFD4A017)))) }
            }
            TemaId.ANTIGUAS -> { drawRect(piedra(Color(0xFF7D7A72)), Offset(w * 0.14f, 0f), Size(w * 0.72f, h)); with(TrofeosMas) { runas(Offset(w * 0.22f, h * 0.5f), w * 0.56f, h * 0.5f, Color(0xFF2B2A28).copy(alpha = 0.5f), h * 0.06f, 6) } }
            TemaId.CUEVA -> {
                // Un canto de caliza redondeado por el agua, con una hilera de puntos de ocre y una mano pequeña soplada.
                drawRoundRect(piedra(Color(0xFFB59A78)), Offset(w * 0.12f, 0f), Size(w * 0.76f, h), CornerRadius(h * 0.35f))
                for (k in 0 until 4) drawCircle(Color(0xFFA2401F), h * 0.08f, Offset(w * (0.24f + k * 0.07f), h * 0.5f))
                val m = Offset(w * 0.7f, h * 0.48f)
                drawCircle(Color(0xFFA2401F).copy(alpha = 0.55f), h * 0.32f, m)
                piezasMano(m, h * 0.22f).forEach { drawPath(it, Color(0xFFB59A78)) }
            }
            // Los pedestales de los siete primeros están en [SalonesTema.pedestal]: no llegan aquí.
            TemaId.ROMANO -> Unit
            TemaId.URBE -> Unit          // la Roma de hoy tiene su pilón de fuente en [SalonesTema.pedestal]
            TemaId.HELENO -> Unit
            TemaId.EGIPTO -> Unit
            TemaId.MEDIEVAL -> Unit
            TemaId.TEMPLARIO -> Unit
            TemaId.GOTICO -> Unit
            TemaId.ARCANO -> Unit
            TemaId.RENACIMIENTO -> Unit
            TemaId.PIRATA -> Unit
            TemaId.NAPOLEON -> Unit
            TemaId.DECO -> Unit
            TemaId.REVOLUCION -> Unit
        }
    }

    // ------------------------------------------------------------------ Al-Ándalus

    /** El patio: un arco de herradura sobre cada columna de piezas, celosía en las enjutas, zócalo de azulejo y la acequia. */
    internal fun DrawScope.patio(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val yeso = (if (oscuro) Color(0xFF3B3A44) else Color(0xFFEDE3CF)).copy(alpha = 0.7f)
        val oro = Color(0xFFC9A04A).copy(alpha = 0.55f)
        val paso = w / n
        // Zócalo de azulejo abajo.
        val zocalo = 24 * d
        var x = 0f; var k = 0
        while (x < w) { val c = Offset(x + 6 * d, h - zocalo / 2); val r = 6 * d
            drawPath(Path().apply { moveTo(c.x, c.y - r); lineTo(c.x + r, c.y); lineTo(c.x, c.y + r); lineTo(c.x - r, c.y); close() }, (if (k % 2 == 0) Color(0xFF1E6B5A) else Color(0xFF1E4F8A)).copy(alpha = if (oscuro) 0.4f else 0.3f)); x += 12 * d; k++ }
        drawLine(oro, Offset(0f, h - zocalo), Offset(w, h - zocalo), 1.2f * d)
        for (i in 0 until n) {
            val x0 = i * paso + 8 * d; val x1 = (i + 1) * paso - 8 * d; val cx = (x0 + x1) / 2; val r = (x1 - x0) / 2
            val yC = 18 * d + r * 0.9f
            // La herradura: un poco más que media circunferencia, sobre jambas.
            val arco = Path().apply {
                moveTo(x0 + r * 0.12f, h - zocalo); lineTo(x0 + r * 0.12f, yC + r * 0.35f)
                arcTo(Rect(Offset(cx, yC), r * 0.92f), 160f, 220f, false)
                lineTo(x1 - r * 0.12f, h - zocalo)
            }
            drawPath(arco, yeso, style = Stroke(5 * d))
            // Dovelas alternadas (rojo y yeso) en la rosca.
            for (j in 0 until 11) drawArc((if (j % 2 == 0) Color(0xFF9E2B25) else Color(0xFFF1E8D4)).copy(alpha = if (oscuro) 0.35f else 0.28f), 160f + j * 20f, 20f, false,
                Offset(cx - r, yC - r), Size(2 * r, 2 * r), style = Stroke(7 * d))
            // El alfiz.
            drawRect(oro, Offset(x0 - 2 * d, 10 * d), Size(x1 - x0 + 4 * d, yC + r * 0.4f - 10 * d), style = Stroke(1 * d))
        }
        // Celosía en las enjutas (entre arcos), con rombos.
        for (i in 0..n) {
            val cx = i * paso; val ancho = 10 * d
            clipPath(Path().apply { addRect(Rect(cx - ancho, 12 * d, cx + ancho, h - zocalo - 4 * d)) }) {
                var y = 12 * d
                while (y < h) { drawLine(yeso.copy(alpha = 0.4f), Offset(cx - ancho, y), Offset(cx + ancho, y + ancho * 2), 1f); drawLine(yeso.copy(alpha = 0.4f), Offset(cx + ancho, y), Offset(cx - ancho, y + ancho * 2), 1f); y += 8 * d }
            }
        }
    }

    // ------------------------------------------------------------------ Mesoamérica

    /**
     * La plaza de las estelas: cada columna de piezas bajo la bóveda en saledizo de un vano de templo (sus lados suben en
     * gradas hasta la losa), un friso de greca escalonada arriba, la franja de cinabrio del estuco y, entre vano y vano,
     * una estela de piedra con su columna de bloques de glifos. Abajo, el piso de estuco de la plaza.
     */
    internal fun DrawScope.plazaMaya(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val piedra = (if (oscuro) Color(0xFF3E4446) else Color(0xFFE2D6BC)).copy(alpha = 0.7f)
        val cinabrio = Color(0xFFA3301E).copy(alpha = if (oscuro) 0.45f else 0.35f)
        val jade = Color(0xFF2F7D57).copy(alpha = if (oscuro) 0.5f else 0.4f)
        val paso = w / n
        // Friso: franja roja y greca.
        drawRect(cinabrio, Offset(0f, 0f), Size(w, 6 * d))
        grecaEscalonada(0f, w, 8 * d, 20 * d, piedra, 2 * d)
        // Piso de la plaza.
        val piso = 18 * d
        drawRect(piedra.copy(alpha = piedra.alpha * 0.6f), Offset(0f, h - piso), Size(w, piso))
        drawLine(cinabrio, Offset(0f, h - piso), Offset(w, h - piso), 1.2f * d)
        for (i in 0 until n) {
            val x0 = i * paso + 10 * d; val x1 = (i + 1) * paso - 10 * d; val g = (x1 - x0) * 0.08f; val top = 30 * d
            // La bóveda: jambas rectas y, arriba, tres gradas por lado hasta la losa.
            val arco = Path().apply {
                moveTo(x0, h - piso); lineTo(x0, top + 4 * g)
                for (k in 0 until 3) { lineTo(x0 + (k + 1) * g, top + (4 - k) * g); lineTo(x0 + (k + 1) * g, top + (3 - k) * g) }
                lineTo(x1 - 3 * g, top + g)
                for (k in 3 downTo 1) { lineTo(x1 - k * g, top + (5 - k) * g); lineTo(x1 - (k - 1) * g, top + (5 - k) * g) }
                lineTo(x1, h - piso)
            }
            drawPath(arco, piedra, style = Stroke(5 * d))
            drawPath(arco, cinabrio, style = Stroke(1 * d))
        }
        // Las estelas entre los vanos.
        for (i in 0..n) {
            val cx = i * paso; val ancho = 7 * d; val alto = h * 0.42f; val y0 = h - piso - alto
            drawRoundRect(piedra, Offset(cx - ancho, y0), Size(2 * ancho, alto), CornerRadius(ancho * 0.6f))
            var y = y0 + 5 * d
            while (y < h - piso - 10 * d) { drawRoundRect(jade, Offset(cx - ancho * 0.6f, y), Size(ancho * 1.2f, ancho * 1.2f), CornerRadius(ancho * 0.3f), style = Stroke(0.8f * d)); y += ancho * 1.5f }
        }
    }

    // ------------------------------------------------------------------ Ruta de la Seda

    /** El caravasar: una ventana lunar que enmarca cada columna, muros de adobe, celosía arriba y farolillos rojos entre columnas. */
    internal fun DrawScope.caravasar(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val adobe = (if (oscuro) Color(0xFF3A3028) else Color(0xFFE6D3B0)).copy(alpha = 0.6f)
        val paso = w / n
        // La celosía de arriba (meandro chino).
        val alto = 16 * d
        var x = 0f
        while (x < w) { drawPath(Path().apply { moveTo(x, alto); lineTo(x, 4 * d); lineTo(x + 10 * d, 4 * d); lineTo(x + 10 * d, 10 * d); lineTo(x + 4 * d, 10 * d) }, Color(0xFF8E1B1B).copy(alpha = 0.45f), style = Stroke(1.4f * d)); x += 14 * d }
        drawLine(Color(0xFF8E1B1B).copy(alpha = 0.5f), Offset(0f, alto + 2 * d), Offset(w, alto + 2 * d), 1.6f * d)
        for (i in 0 until n) {
            val x0 = i * paso + 6 * d; val x1 = (i + 1) * paso - 6 * d; val cx = (x0 + x1) / 2; val r = (x1 - x0) / 2
            // Muro con el vano redondo.
            val muro = Path().apply { fillType = PathFillType.EvenOdd; addRect(Rect(x0, alto + 6 * d, x1, h)); addOval(Rect(Offset(cx, alto + 6 * d + r * 1.05f), r * 0.98f)) }
            drawPath(muro, adobe.copy(alpha = 0.18f))
            drawCircle(adobe, r * 0.98f, Offset(cx, alto + 6 * d + r * 1.05f), style = Stroke(3 * d))
            drawCircle(Color(0xFF3A3A3A).copy(alpha = 0.25f), r * 1.04f, Offset(cx, alto + 6 * d + r * 1.05f), style = Stroke(1.2f * d))
        }
        // Farolillos entre las columnas.
        for (i in 0..n) {
            val cx = i * paso; var y = alto + 30 * d
            while (y < h - 40 * d) {
                drawLine(Color(0xFF3A2414).copy(alpha = 0.5f), Offset(cx, y - 14 * d), Offset(cx, y - 7 * d), 1 * d)
                drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB347).copy(alpha = 0.18f), Color.Transparent), Offset(cx, y), 30 * d), 30 * d, Offset(cx, y))
                drawOval(Color(0xFFC0392B).copy(alpha = 0.65f), Offset(cx - 6 * d, y - 8 * d), Size(12 * d, 16 * d))
                drawRect(Color(0xFFD4AF37).copy(alpha = 0.7f), Offset(cx - 4 * d, y - 9 * d), Size(8 * d, 2 * d)); drawRect(Color(0xFFD4AF37).copy(alpha = 0.7f), Offset(cx - 4 * d, y + 7 * d), Size(8 * d, 2 * d))
                y += 200 * d
            }
        }
    }

    // ------------------------------------------------------------------ Japón Edo

    /**
     * La sala de una posada del camino real: el dintel de madera arriba, un shōji de papel con su celosía (kumiko) detrás
     * de cada columna, el suelo de tatami con sus orillas oscuras y, entre columna y columna, un andon (farol de papel) encendido.
     */
    internal fun DrawScope.posada(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val madera = (if (oscuro) Color(0xFF5A4632) else Color(0xFF7A5C3E)).copy(alpha = 0.55f)
        val washi = (if (oscuro) Color(0xFF3A3530) else Color(0xFFF6F0E2)).copy(alpha = if (oscuro) 0.35f else 0.6f)
        val paso = w / n; val suelo = h - 56 * d
        drawRect(madera, Offset(0f, 0f), Size(w, 10 * d)); drawRect(madera, Offset(0f, 14 * d), Size(w, 3 * d))
        for (i in 0 until n) {
            val x0 = i * paso + 5 * d; val x1 = (i + 1) * paso - 5 * d; val top = 22 * d
            drawRect(washi, Offset(x0, top), Size(x1 - x0, suelo - top))
            val k = madera.copy(alpha = madera.alpha * 0.5f)
            for (j in 1..2) { val x = x0 + (x1 - x0) * j / 3f; drawLine(k, Offset(x, top), Offset(x, suelo), 0.8f * d) }
            var y = top + 30 * d; while (y < suelo) { drawLine(k, Offset(x0, y), Offset(x1, y), 0.8f * d); y += 30 * d }
            drawRect(madera, Offset(x0, top), Size(x1 - x0, suelo - top), style = Stroke(2 * d))
        }
        // El tatami: esteras con su orilla de tela oscura (heri).
        val tatami = (if (oscuro) Color(0xFF4A4A30) else Color(0xFFCFC79A)).copy(alpha = 0.55f)
        drawRect(tatami, Offset(0f, suelo), Size(w, h - suelo))
        var x = 0f; var k = 0
        while (x < w) { drawLine(Color(0xFF2A2A1C).copy(alpha = 0.45f), Offset(x, suelo), Offset(x, h), 2.4f * d); x += if (k % 2 == 0) w * 0.36f else w * 0.18f; k++ }
        drawLine(Color(0xFF2A2A1C).copy(alpha = 0.45f), Offset(0f, suelo + 2 * d), Offset(w, suelo + 2 * d), 3 * d)
        // Los andon encendidos entre las columnas.
        for (i in 0..n) {
            val cx = i * paso; val y0 = suelo - 34 * d
            drawCircle(Brush.radialGradient(listOf(Color(0xFFFFC46B).copy(alpha = if (oscuro) 0.28f else 0.16f), Color.Transparent), Offset(cx, y0 + 12 * d), 36 * d), 36 * d, Offset(cx, y0 + 12 * d))
            drawRect(Color(0xFFFFF1D6).copy(alpha = 0.75f), Offset(cx - 7 * d, y0), Size(14 * d, 22 * d))
            drawRect(madera, Offset(cx - 7 * d, y0), Size(14 * d, 22 * d), style = Stroke(1.2f * d))
            drawLine(madera, Offset(cx - 6 * d, y0 + 22 * d), Offset(cx - 7 * d, suelo + 2 * d), 1.4f * d); drawLine(madera, Offset(cx + 6 * d, y0 + 22 * d), Offset(cx + 7 * d, suelo + 2 * d), 1.4f * d)
        }
    }

    // ------------------------------------------------------------------ Mongol

    /** La gran yurta: postes del techo que salen de la rueda central, el enrejado de la pared abajo y estandartes en los lados. */
    internal fun DrawScope.yurta(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val madera = Color(0xFFD9772A).copy(alpha = if (oscuro) 0.35f else 0.3f)
        val fieltro = (if (oscuro) Color(0xFF3A3630) else Color(0xFFEDE6D6)).copy(alpha = 0.5f)
        val c = Offset(w / 2, -min(w, h) * 0.1f); val R = 26 * d
        // Los postes del techo, que bajan hasta el muro de cada lado sin cruzar las columnas de piezas.
        val paso = w / n
        for (i in 0..n) { val x = i * paso; drawLine(madera, c, Offset(x, 34 * d), 2.2f * d) }
        for (k in 1..10) { val x = w * k / 11f; drawLine(madera.copy(alpha = madera.alpha * 0.5f), c, Offset(x, 30 * d), 1.2f * d) }
        drawRect(fieltro, Offset(0f, 28 * d), Size(w, 8 * d))
        drawRect(Color(0xFF2E6DB4).copy(alpha = 0.45f), Offset(0f, 30 * d), Size(w, 3 * d))
        drawCircle(Color(0xFFD9772A).copy(alpha = 0.6f), R, Offset(c.x, c.y + R * 0.6f), style = Stroke(3 * d))
        // El enrejado (khana) abajo: rombos de madera.
        val y0 = h - 40 * d
        clipPath(Path().apply { addRect(Rect(0f, y0, w, h)) }) {
            var x = -h; while (x < w + h) { drawLine(madera, Offset(x, y0), Offset(x + 40 * d, h), 1.4f * d); drawLine(madera, Offset(x + 40 * d, y0), Offset(x, h), 1.4f * d); x += 14 * d }
        }
        drawLine(madera, Offset(0f, y0), Offset(w, y0), 2 * d)
        // Estandartes de colas en los lados.
        for (i in 0..n) {
            val x = i * paso
            drawLine(Color(0xFF6B4A2A).copy(alpha = 0.5f), Offset(x, 50 * d), Offset(x, y0 - 6 * d), 1.6f * d)
            for (j in 0 until 7) { val a = -0.5 + j * 0.16
                drawLine((if (j % 3 == 1) Color(0xFFEDE6D6) else Color(0xFF2A2420)).copy(alpha = 0.45f), Offset(x, 58 * d), Offset(x + (24 * d * sin(a)).toFloat(), 58 * d + 30 * d), 1.4f * d) }
        }
    }

    // ------------------------------------------------------------------ Colonos

    /** El almacén de los pioneros: pared de tablones, postes entre las columnas, repisas cortas en los lados y la linterna. */
    internal fun DrawScope.almacen(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val tablon = Color(0xFF8A5A2B).copy(alpha = if (oscuro) 0.18f else 0.12f)
        var x = 0f; var k = 0
        while (x < w) { drawRect(if (k % 2 == 0) tablon else tablon.copy(alpha = tablon.alpha * 1.4f), Offset(x, 0f), Size(22 * d, h)); drawLine(Color(0xFF3A2414).copy(alpha = 0.15f), Offset(x, 0f), Offset(x, h), 1f); x += 22 * d; k++ }
        val paso = w / n
        val poste = Color(0xFF5A3A22).copy(alpha = if (oscuro) 0.55f else 0.4f)
        for (i in 0..n) { val xx = i * paso; drawRect(poste, Offset(xx - 5 * d, 0f), Size(10 * d, h)) }
        // La viga de arriba con sus clavos.
        drawRect(poste, Offset(0f, 0f), Size(w, 14 * d))
        var c = 10 * d; while (c < w) { drawCircle(Color(0xFF2A2A2A).copy(alpha = 0.4f), 1.4f * d, Offset(c, 7 * d)); c += 40 * d }
        // Herramientas colgadas en los postes: una cuerda enrollada y una linterna.
        for (i in 0..n) {
            val xx = i * paso; var y = 60 * d
            while (y < h - 60 * d) {
                drawCircle(Color(0xFFC9A57A).copy(alpha = 0.55f), 7 * d, Offset(xx, y), style = Stroke(2 * d))
                drawCircle(Color(0xFFC9A57A).copy(alpha = 0.55f), 4 * d, Offset(xx, y), style = Stroke(1.5f * d))
                val yl = y + 100 * d
                if (yl < h - 30 * d) {
                    drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD27A).copy(alpha = 0.2f), Color.Transparent), Offset(xx, yl), 28 * d), 28 * d, Offset(xx, yl))
                    drawRect(Color(0xFF4A4E54).copy(alpha = 0.7f), Offset(xx - 4 * d, yl - 6 * d), Size(8 * d, 12 * d))
                    drawRect(Color(0xFFFFE6A3).copy(alpha = 0.7f), Offset(xx - 2.5f * d, yl - 4 * d), Size(5 * d, 8 * d))
                }
                y += 220 * d
            }
        }
        // El piso de tablones.
        drawRect(Color(0xFF5A3A22).copy(alpha = 0.3f), Offset(0f, h - 10 * d), Size(w, 10 * d))
    }

    // ------------------------------------------------------------------ Botánico

    /** El invernadero: una bóveda de cristal y hierro sobre cada columna, plantas en macetas abajo y luz que entra. */
    internal fun DrawScope.invernadero(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val hierro = (if (oscuro) Color(0xFF8FA89A) else Color(0xFF2F3A33)).copy(alpha = 0.4f)
        val vidrio = Color(0xFFCFE6F0).copy(alpha = if (oscuro) 0.06f else 0.18f)
        val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso + 4 * d; val x1 = (i + 1) * paso - 4 * d; val r = (x1 - x0) / 2; val cx = (x0 + x1) / 2
            val boveda = Path().apply { moveTo(x0, h); lineTo(x0, 6 * d + r); arcTo(Rect(x0, 6 * d, x1, 6 * d + 2 * r), 180f, 180f, false); lineTo(x1, h) }
            drawPath(boveda, vidrio)
            drawPath(boveda, hierro, style = Stroke(2 * d))
            // Montantes y travesaños del cristal (solo en los lados de la columna).
            listOf(x0 + r * 0.2f, x1 - r * 0.2f).forEach { xx -> drawLine(hierro.copy(alpha = hierro.alpha * 0.6f), Offset(xx, 6 * d + r * 0.6f), Offset(xx, h), 1f * d) }
            var y = 6 * d + r; while (y < h) { drawLine(hierro.copy(alpha = hierro.alpha * 0.5f), Offset(x0, y), Offset(x0 + r * 0.2f, y), 1f * d); drawLine(hierro.copy(alpha = hierro.alpha * 0.5f), Offset(x1 - r * 0.2f, y), Offset(x1, y), 1f * d); y += 40 * d }
            for (k in 1..5) { val a = PI + k * PI / 6; drawLine(hierro.copy(alpha = hierro.alpha * 0.6f), Offset(cx, 6 * d + r), Offset(cx + (r * cos(a)).toFloat(), 6 * d + r + (r * sin(a)).toFloat()), 1f * d) }
        }
        // Plantas en macetas en los lados de cada columna, abajo.
        for (i in 0..n) {
            val xx = i * paso
            drawPath(Path().apply { moveTo(xx - 9 * d, h - 16 * d); lineTo(xx + 9 * d, h - 16 * d); lineTo(xx + 7 * d, h); lineTo(xx - 7 * d, h); close() }, Color(0xFFB0603A).copy(alpha = 0.5f))
            for (j in 0 until 5) { val a = -PI / 2 + (j - 2) * 0.45
                drawPath(Path().apply { moveTo(xx, h - 16 * d); quadraticTo(xx + (20 * d * cos(a + 0.3)).toFloat(), h - 16 * d + (20 * d * sin(a + 0.3)).toFloat(), xx + (34 * d * cos(a)).toFloat(), h - 16 * d + (34 * d * sin(a)).toFloat()) },
                    Color(0xFF3F7A3A).copy(alpha = 0.55f), style = Stroke(3 * d)) }
        }
        // Rayos de luz.
        drawPath(Path().apply { moveTo(w * 0.15f, 0f); lineTo(w * 0.35f, 0f); lineTo(w * 0.6f, h); lineTo(w * 0.3f, h); close() }, Color(0xFFFFF4C2).copy(alpha = if (oscuro) 0.03f else 0.08f))
    }

    // ------------------------------------------------------------------ Ciudad verde

    /**
     * La biblioteca invadida: estantes altos de libros entre las columnas (con huecos de los que se cayeron), hiedra que
     * cuelga de la viga partida, la luz que entra por el boquete del techo, el árbol que rompió el piso y los escombros.
     */
    internal fun DrawScope.bibliotecaInvadida(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val madera = (if (oscuro) Color(0xFF8A7458) else Color(0xFF5E4C36)).copy(alpha = 0.35f)
        val lomos = listOf(Color(0xFF8A4A22), Color(0xFF3E5A6A), Color(0xFF6A5A2A), Color(0xFF5A3A4A), Color(0xFF4F6B3A))
        val hiedra = Color(0xFF4F7A33).copy(alpha = if (oscuro) 0.5f else 0.55f)
        val az = java.util.Random(27)
        val paso = w / n
        drawPath(Path().apply { moveTo(w * 0.55f, 0f); lineTo(w * 0.75f, 0f); lineTo(w * 0.62f, h); lineTo(w * 0.3f, h); close() }, Color(0xFFFFF4C2).copy(alpha = if (oscuro) 0.04f else 0.1f))
        drawRect(madera, Offset(0f, 0f), Size(w * 0.56f, 8 * d)); drawRect(madera, Offset(w * 0.74f, 0f), Size(w * 0.26f, 8 * d))
        for (i in 0..n) {
            val x0 = i * paso - 14 * d
            drawRect(madera, Offset(x0, 10 * d), Size(28 * d, h - 10 * d), style = Stroke(1.5f * d))
            var y = 30 * d
            while (y < h - 10 * d) {
                drawLine(madera, Offset(x0, y), Offset(x0 + 28 * d, y), 2 * d)
                var bx = x0 + 2 * d
                while (bx < x0 + 25 * d) {
                    val bw = (2.5f + az.nextFloat() * 2.5f) * d; val bh = (10 + az.nextFloat() * 8) * d
                    if (az.nextFloat() > 0.2f) drawRect(lomos[az.nextInt(lomos.size)].copy(alpha = if (oscuro) 0.3f else 0.4f), Offset(bx, y - bh), Size(bw, bh))
                    bx += bw + 0.6f * d
                }
                y += 30 * d
            }
        }
        for (k in 0 until n * 2 + 1) {
            val x = k * paso / 2 + 6 * d; val largo = (30 + az.nextFloat() * 70) * d
            if (x > w * 0.56f && x < w * 0.74f) continue
            drawLine(hiedra, Offset(x, 8 * d), Offset(x + 3 * d, 8 * d + largo), 1.2f * d)
            var y = 14 * d; var lado = 1f
            while (y < largo) { drawOval(hiedra, Offset(x + lado * 1.5f * d - 3 * d, 8 * d + y), Size(6 * d, 4 * d)); y += 9 * d; lado = -lado }
        }
        val ax = w * 0.5f
        drawLine(Color(0xFF5A3A22).copy(alpha = 0.45f), Offset(ax, h), Offset(ax + 6 * d, h - 90 * d), 7 * d)
        repeat(6) { drawCircle(hiedra.copy(alpha = hiedra.alpha * 0.6f), (16 + az.nextFloat() * 14) * d, Offset(ax + (az.nextFloat() - 0.4f) * 60 * d, h - 100 * d - az.nextFloat() * 40 * d)) }
        val escombro = (if (oscuro) Color(0xFF9AA39A) else Color(0xFF6A6F66)).copy(alpha = 0.25f)
        repeat(14) { val x = az.nextFloat() * w; val t = (3 + az.nextFloat() * 6) * d; drawRect(escombro, Offset(x, h - t), Size(t * 1.4f, t)) }
    }

    // ------------------------------------------------------------------ Navidad

    /** La sala de Navidad: guirnaldas arriba, cordeles de los que cuelgan las piezas como adornos, el árbol tenue y la chimenea. */
    internal fun DrawScope.chimenea(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val verde = Color(0xFF1F6B3A).copy(alpha = if (oscuro) 0.5f else 0.4f)
        val rojo = Color(0xFFB3122E).copy(alpha = 0.6f)
        // La chimenea de ladrillo con el fuego, abajo al centro.
        val chW = min(w * 0.5f, 220 * d); val chH = 46 * d; val chX = (w - chW) / 2
        var yy = h - chH; var fila = 0
        while (yy < h) { var xx = chX - (if (fila % 2 == 0) 0f else 8 * d); while (xx < chX + chW) { drawRect(Color(0xFF8E3A2A).copy(alpha = 0.22f), Offset(xx, yy), Size(16 * d, 7 * d), style = Stroke(1f)); xx += 16 * d }; yy += 7 * d; fila++ }
        drawRect(Color(0xFF1A1210).copy(alpha = 0.35f), Offset(w / 2 - chW * 0.25f, h - chH * 0.7f), Size(chW * 0.5f, chH * 0.7f))
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFF8A2E).copy(alpha = 0.35f), Color.Transparent), Offset(w / 2, h - 10 * d), 60 * d), 60 * d, Offset(w / 2, h - 10 * d))
        // El árbol tenue en una esquina.
        val ax = w - 30 * d; val ay = h - chH - 10 * d
        for (k in 0 until 4) { val tw = (60 - k * 12) * d; val ty = ay - k * 30 * d
            drawPath(Path().apply { moveTo(ax - tw / 2, ty); lineTo(ax, ty - 40 * d); lineTo(ax + tw / 2, ty); close() }, verde.copy(alpha = verde.alpha * 0.6f)) }
        // Guirnaldas arriba entre los cordeles, con sus focos.
        val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso; val x1 = (i + 1) * paso
            drawPath(Path().apply { moveTo(x0, 10 * d); quadraticTo((x0 + x1) / 2, 34 * d, x1, 10 * d) }, verde, style = Stroke(5 * d))
            for (k in 1..5) { val t = k / 6f; val x = x0 + (x1 - x0) * t; val y = 10 * d + 24 * d * 2 * t * (1 - t)
                drawCircle(listOf(Color(0xFFFFD27A), Color(0xFFB3122E), Color(0xFF6FB3E0))[k % 3].copy(alpha = 0.8f), 2.4f * d, Offset(x, y + 3 * d)) }
        }
        // Cordeles: en cada columna, de los que cuelgan las piezas como adornos.
        for (i in 0 until n) {
            val cx = (i + 0.5f) * paso
            drawLine(Color(0xFFD4AF37).copy(alpha = 0.35f), Offset(cx, 12 * d), Offset(cx, h - chH - 4 * d), 1 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * d, 3 * d)))
            drawPath(Path().apply { moveTo(cx, 16 * d); quadraticTo(cx - 8 * d, 6 * d, cx - 10 * d, 16 * d); close() }, rojo)
            drawPath(Path().apply { moveTo(cx, 16 * d); quadraticTo(cx + 8 * d, 6 * d, cx + 10 * d, 16 * d); close() }, rojo)
        }
        // Nieve que cae afuera (en los bordes).
        val az = java.util.Random(12)
        repeat(30) { drawCircle(Color.White.copy(alpha = if (oscuro) 0.25f else 0.5f), (1 + az.nextFloat() * 2) * d, Offset(az.nextFloat() * w, az.nextFloat() * (h - chH))) }
    }

    // ------------------------------------------------------------------ Tierras Antiguas

    /** El gran salón de piedra: pilares entre las columnas con capitel tallado, estandartes que cuelgan y antorchas. */
    internal fun DrawScope.salonPiedra(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val piedra = (if (oscuro) Color(0xFF3A3A40) else Color(0xFFB8B2A6)).copy(alpha = 0.55f)
        val junta = (if (oscuro) Color.Black else Color(0xFF6E6A62)).copy(alpha = 0.18f)
        // Sillares de fondo.
        var y = 0f; var k = 0
        while (y < h) { val off = if (k % 2 == 0) 0f else 18 * d; var x = -off; while (x < w) { drawRect(junta, Offset(x, y), Size(36 * d, 14 * d), style = Stroke(1f)); x += 36 * d }; y += 14 * d; k++ }
        val paso = w / n
        // Pilares con su capitel y runas inventadas.
        for (i in 0..n) {
            val x = i * paso
            drawRect(piedra, Offset(x - 7 * d, 20 * d), Size(14 * d, h - 20 * d))
            drawRect(piedra, Offset(x - 10 * d, 14 * d), Size(20 * d, 8 * d))
            with(TrofeosMas) { runas(Offset(x - 5 * d, h * 0.5f), 10 * d, 18 * d, Color(0xFF2B2A28).copy(alpha = 0.35f), 1.2f * d, 1, i + 3) }
        }
        // El arco de medio punto sobre cada columna de piezas.
        for (i in 0 until n) {
            val x0 = i * paso + 7 * d; val x1 = (i + 1) * paso - 7 * d; val r = (x1 - x0) / 2
            drawArc(piedra, 180f, 180f, false, Offset(x0, 20 * d), Size(x1 - x0, 2 * r), style = Stroke(6 * d))
            for (j in 0 until 9) drawArc(junta, 180f + j * 20f, 1.5f, false, Offset(x0, 20 * d), Size(x1 - x0, 2 * r), style = Stroke(6 * d))
        }
        // Estandartes con dos colas colgando de los pilares, y antorchas.
        val telas = listOf(Color(0xFF1F3A5A), Color(0xFF7A1A12))
        for (i in 0..n) {
            val x = i * paso; val y0 = 60 * d; val an = 9 * d
            drawPath(Path().apply { moveTo(x - an, y0); lineTo(x + an, y0); lineTo(x + an, y0 + 70 * d); lineTo(x, y0 + 58 * d); lineTo(x - an, y0 + 70 * d); close() }, telas[i % 2].copy(alpha = if (oscuro) 0.55f else 0.4f))
            drawLine(Color(0xFFC9A04A).copy(alpha = 0.6f), Offset(x - an - 2 * d, y0), Offset(x + an + 2 * d, y0), 1.5f * d)
            val yt = y0 + 150 * d
            if (yt < h - 30 * d) {
                drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB347).copy(alpha = 0.22f), Color.Transparent), Offset(x, yt), 36 * d), 36 * d, Offset(x, yt))
                drawLine(Color(0xFF3A2414).copy(alpha = 0.6f), Offset(x, yt + 14 * d), Offset(x, yt), 2.4f * d)
                drawPath(Path().apply { moveTo(x, yt - 10 * d); quadraticTo(x + 4 * d, yt - 4 * d, x, yt); quadraticTo(x - 4 * d, yt - 4 * d, x, yt - 10 * d); close() }, Color(0xFFFFB347).copy(alpha = 0.7f))
            }
        }
        // El piso de losas.
        drawRect(piedra.copy(alpha = piedra.alpha * 0.5f), Offset(0f, h - 8 * d), Size(w, 8 * d))
    }

    /**
     * Prehistoria: la sala de los bisontes, en lo hondo de la cueva. El techo de roca con estalactitas; entre las columnas de
     * piezas, bisontes de ocre y de carbón con su hilera de puntos; manos en negativo en los pilares de roca y lámparas de
     * grasa en el suelo, con su resplandor.
     */
    internal fun DrawScope.salonCueva(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val base = if (oscuro) Color(0xFF2A2019) else Color(0xFFD8C6A6)
        val roca = base.copy(alpha = 0.6f)
        val vista = lerp(cs.background, base, 0.6f)          // la roca tal como se ve sobre el fondo, para las manos en negativo
        val sombra = (if (oscuro) Color.Black else Color(0xFF6E5638)).copy(alpha = 0.16f)
        val ocre = Color(0xFFA2401F).copy(alpha = if (oscuro) 0.55f else 0.45f)
        val carbon = (if (oscuro) Color(0xFF0C0806) else Color(0xFF2E2620)).copy(alpha = if (oscuro) 0.7f else 0.45f)
        drawRect(roca, Offset.Zero, Size(w, h))
        // Las vetas de la roca.
        val az = java.util.Random(170)
        var y = 30 * d
        while (y < h) {
            val veta = Path().apply { moveTo(0f, y); var x = 0f; while (x < w) { val nx = x + w / 4; quadraticTo(x + w / 8, y + (az.nextFloat() - 0.5f) * 16 * d, nx, y + (az.nextFloat() - 0.5f) * 6 * d); x = nx } }
            drawPath(veta, sombra, style = Stroke(1f * d)); y += 46 * d
        }
        // El techo, con estalactitas.
        drawPath(Path().apply {
            moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, 16 * d); var x = w; var k = 0
            while (x > 0f) { val nx = x - 22 * d; lineTo(x - 11 * d, (16 + (if (k % 3 == 0) 26 else 10)) * d); lineTo(nx, 16 * d); x = nx; k++ }
            close()
        }, sombra.copy(alpha = sombra.alpha * 1.6f))
        val paso = w / n
        // Entre las columnas de piezas: un bisonte (de ocre o de carbón) y debajo, una hilera de puntos.
        for (i in 0 until n) {
            val cx = (i + 0.5f) * paso
            drawPath(bisonte(Offset(cx, 72 * d), paso * (if (i % 2 == 0) 0.32f else 0.28f)), if (i % 2 == 0) ocre else carbon)
            for (k in 0 until 5) drawCircle(ocre, 2.2f * d, Offset(cx - 16 * d + k * 8 * d, 110 * d))
        }
        // Manos en negativo en los pilares de roca.
        for (i in 0..n) {
            val m = Offset(i * paso, 150 * d + (i % 2) * 40 * d)
            if (m.y < h - 40 * d) {
                drawCircle(Brush.radialGradient(listOf(ocre, Color.Transparent), m, 24 * d), 24 * d, m)
                piezasMano(m, 14 * d).forEach { drawPath(it, vista) }
            }
        }
        // Lámparas de grasa en el suelo, con su resplandor.
        for (i in 0 until n) {
            val cx = (i + 0.5f) * paso; val yl = h - 14 * d
            drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB347).copy(alpha = if (oscuro) 0.3f else 0.18f), Color.Transparent), Offset(cx, yl - 8 * d), 46 * d), 46 * d, Offset(cx, yl - 8 * d))
            drawArc(carbon, 0f, 180f, true, Offset(cx - 9 * d, yl - 5 * d), Size(18 * d, 10 * d))
            drawPath(Path().apply { moveTo(cx, yl - 14 * d); quadraticTo(cx + 4 * d, yl - 6 * d, cx, yl - 2 * d); quadraticTo(cx - 4 * d, yl - 6 * d, cx, yl - 14 * d); close() }, Color(0xFFFFB347).copy(alpha = 0.8f))
        }
        drawRect(sombra, Offset(0f, h - 6 * d), Size(w, 6 * d))
    }

    /** Distopía: un pasillo del archivo, con archiveros de cajones numerados, lámparas de tubo y la franja roja del Ministerio. */
    internal fun DrawScope.archivo(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val muro = (if (oscuro) Color(0xFF2A2B2E) else Color(0xFFD9D6CC)).copy(alpha = 0.6f)
        val metal = (if (oscuro) Color(0xFF45474C) else Color(0xFF9A9CA0)).copy(alpha = 0.5f)
        val linea = (if (oscuro) Color.Black else Color(0xFF55585C)).copy(alpha = 0.3f)
        val rojo = Color(0xFFC8102E)
        drawRect(muro, Offset(0f, 0f), Size(w, h))
        // La franja roja a media altura y el zócalo oscuro.
        drawRect(rojo.copy(alpha = if (oscuro) 0.5f else 0.35f), Offset(0f, 34 * d), Size(w, 5 * d))
        drawRect(Color(0xFF141414).copy(alpha = 0.18f), Offset(0f, h - 10 * d), Size(w, 10 * d))
        // Lámparas de tubo en el techo.
        val paso = w / n
        for (i in 0 until n) {
            val cx = (i + 0.5f) * paso
            drawRect(Color(0xFF141414).copy(alpha = 0.35f), Offset(cx - 26 * d, 6 * d), Size(52 * d, 4 * d))
            drawRect(Color(0xFFF4F1E4).copy(alpha = if (oscuro) 0.75f else 0.9f), Offset(cx - 24 * d, 10 * d), Size(48 * d, 3 * d))
            drawPath(Path().apply { moveTo(cx - 24 * d, 13 * d); lineTo(cx + 24 * d, 13 * d); lineTo(cx + 60 * d, 90 * d); lineTo(cx - 60 * d, 90 * d); close() },
                Brush.verticalGradient(listOf(Color(0xFFF4F1E4).copy(alpha = if (oscuro) 0.16f else 0.22f), Color.Transparent), 13 * d, 90 * d))
        }
        // Archiveros entre las columnas de piezas: cajones con tarjetero y jaladera.
        for (i in 0..n) {
            val x = i * paso
            var y = 50 * d; var k = 0
            while (y + 30 * d < h - 12 * d) {
                drawRect(metal, Offset(x - 12 * d, y), Size(24 * d, 28 * d))
                drawRect(linea, Offset(x - 12 * d, y), Size(24 * d, 28 * d), style = Stroke(1f))
                drawRect(Color(0xFFF2EFE6).copy(alpha = 0.55f), Offset(x - 5 * d, y + 5 * d), Size(10 * d, 6 * d))
                drawRect(linea.copy(alpha = 0.6f), Offset(x - 4 * d, y + 17 * d), Size(8 * d, 3 * d))
                // Algunos cajones llevan el sello rojo de «archivado».
                if ((i + k) % 3 == 0) drawCircle(rojo.copy(alpha = 0.35f), 3.5f * d, Offset(x + 7 * d, y + 22 * d), style = Stroke(1f * d))
                y += 30 * d; k++
            }
        }
        drawLine(linea, Offset(0f, h - 10 * d), Offset(w, h - 10 * d), 1f * d)
    }

    /**
     * Terminal: la sala de las computadoras centrales. Gabinetes entre las columnas de piezas con dos carretes de cinta
     * y la rejilla de luces de estado, la charola de cables arriba y el piso técnico de losetas abajo. Todo en cian sobre negro.
     */
    internal fun DrawScope.salaCentral(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val cian = Color(0xFF00E5FF)
        val chapa = Color(0xFF111925).copy(alpha = 0.85f)
        val filo = Color(0xFF15404A)
        // El piso técnico: losetas con sus juntas, que se pierden hacia arriba.
        val piso = 34 * d
        drawRect(Color(0xFF0A0E15), Offset(0f, h - piso), Size(w, piso))
        var x = 0f; while (x < w) { drawLine(filo.copy(alpha = 0.6f), Offset(x, h - piso), Offset(x, h), 1f); x += 24 * d }
        listOf(0.35f, 0.7f).forEach { f -> drawLine(filo.copy(alpha = 0.6f), Offset(0f, h - piso * (1 - f)), Offset(w, h - piso * (1 - f)), 1f) }
        drawLine(cian.copy(alpha = 0.35f), Offset(0f, h - piso), Offset(w, h - piso), 1f * d)
        // La charola de cables del techo.
        drawRect(Color(0xFF0A0E15), Offset(0f, 0f), Size(w, 14 * d))
        for (k in 0 until 3) drawLine(cian.copy(alpha = 0.12f + 0.08f * k), Offset(0f, (4 + k * 3) * d), Offset(w, (4 + k * 3) * d), 1f * d)
        drawLine(filo, Offset(0f, 14 * d), Offset(w, 14 * d), 1.2f * d)
        val paso = w / n
        // Los gabinetes entre las columnas: carretes arriba, luces de estado y la rejilla de ventilación abajo.
        val az = java.util.Random(64)
        for (i in 0..n) {
            val cx = i * paso; val ancho = 26 * d
            val y0 = 36 * d; val y1 = h - piso
            drawRect(chapa, Offset(cx - ancho / 2, y0), Size(ancho, y1 - y0))
            drawRect(filo, Offset(cx - ancho / 2, y0), Size(ancho, y1 - y0), style = Stroke(1f * d))
            // Los dos carretes de cinta, con sus rayos y la cinta enrollada.
            listOf(y0 + 16 * d, y0 + 42 * d).forEachIndexed { k, cy ->
                val c = Offset(cx, cy); val r = 9 * d
                drawCircle(Color(0xFF05070B), r, c)
                drawCircle(cian.copy(alpha = 0.18f), r * (0.55f + 0.25f * k), c)
                drawCircle(cian.copy(alpha = 0.55f), r, c, style = Stroke(1f * d))
                for (j in 0 until 3) { val a = j * 2 * PI / 3 + k * 0.5; drawLine(cian.copy(alpha = 0.5f), c, Offset(c.x + (r * 0.5f * cos(a)).toFloat(), c.y + (r * 0.5f * sin(a)).toFloat()), 1f * d) }
                drawCircle(Color(0xFF05070B), 1.6f * d, c)
            }
            // Las luces de estado: una rejilla de tres por muchas, unas encendidas y otras no.
            var y = y0 + 62 * d; var fila = 0
            while (y < y1 - 30 * d) {
                if (fila % 10 == 9) drawLine(filo, Offset(cx - ancho / 2, y), Offset(cx + ancho / 2, y), 1f * d)
                else for (j in 0 until 3) { val on = az.nextInt(3) > 0
                    drawRect(cian.copy(alpha = if (on) 0.55f + 0.35f * az.nextFloat() else 0.1f), Offset(cx - 7 * d + j * 6 * d, y), Size(3 * d, 2 * d)) }
                y += 6 * d; fila++
            }
            // La rejilla de ventilación.
            var yv = y1 - 24 * d; while (yv < y1 - 4 * d) { drawLine(Color(0xFF05070B), Offset(cx - ancho / 2 + 4 * d, yv), Offset(cx + ancho / 2 - 4 * d, yv), 1.4f * d); yv += 4 * d }
        }
    }

    /**
     * Ciudad neón: un callejón de noche. Paredes de ladrillo húmedo, letreros verticales de neón colgados entre las columnas
     * de piezas (cada uno con sus tres «letras»), cables que cruzan de pared a pared con farolitos de papel, una tubería que
     * suelta vapor y, en el suelo mojado, los charcos que reflejan los letreros.
     */
    internal fun DrawScope.callejon(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val magenta = Color(0xFFFF3D9A); val ambar = Color(0xFFFFB238); val verde = Color(0xFF8CFF5A)
        val ladrillo = Color(0xFF2A1A2A).copy(alpha = 0.7f); val junta = Color(0xFF0C0811).copy(alpha = 0.8f)
        // Los ladrillos, en hiladas trabadas.
        val alto = 9 * d; val largo = 22 * d
        var y = 0f; var fila = 0
        while (y < h - 40 * d) {
            drawRect(ladrillo, Offset(0f, y), Size(w, alto - 1.5f * d))
            var x = if (fila % 2 == 0) 0f else -largo / 2
            while (x < w) { drawLine(junta, Offset(x, y), Offset(x, y + alto), 1.5f * d); x += largo }
            y += alto; fila++
        }
        // Los cables que cruzan arriba, con farolitos colgando.
        listOf(18f, 46f).forEachIndexed { k, y0 ->
            val cable = Path().apply { moveTo(0f, y0 * d); quadraticTo(w / 2, (y0 + 18) * d, w, y0 * d) }
            drawPath(cable, Color.Black.copy(alpha = 0.8f), style = Stroke(1.2f * d))
            val paso = w / (5 + k)
            var xx = paso / 2
            while (xx < w) {
                val t = xx / w; val yy = (1 - t) * (1 - t) * y0 * d + 2 * (1 - t) * t * (y0 + 18) * d + t * t * y0 * d
                drawCircle(Brush.radialGradient(listOf(Color(0xFFFF6B5E).copy(alpha = 0.35f), Color.Transparent), Offset(xx, yy + 7 * d), 16 * d), 16 * d, Offset(xx, yy + 7 * d))
                drawOval(Color(0xFFFF6B5E).copy(alpha = 0.85f), Offset(xx - 4 * d, yy + 2 * d), Size(8 * d, 11 * d))
                xx += paso
            }
        }
        // Los letreros verticales entre las columnas.
        val paso = w / n
        val tonos = listOf(magenta, ambar, verde)
        for (i in 0..n) {
            val cx = i * paso; val y0 = 70 * d; val y1 = y0 + 120 * d; val an = 16 * d
            val col = tonos[i % 3]
            drawRect(Color(0xFF0C0811).copy(alpha = 0.9f), Offset(cx - an / 2, y0), Size(an, y1 - y0))
            drawRect(col.copy(alpha = 0.22f), Offset(cx - an / 2 - 3 * d, y0 - 3 * d), Size(an + 6 * d, y1 - y0 + 6 * d), style = Stroke(5 * d))
            drawRect(col, Offset(cx - an / 2, y0), Size(an, y1 - y0), style = Stroke(1.5f * d))
            for (k in 0 until 3) {
                val yy = y0 + 16 * d + k * 36 * d
                drawLine(col, Offset(cx - 4 * d, yy), Offset(cx + 4 * d, yy), 1.6f * d)
                drawLine(col, Offset(cx, yy), Offset(cx, yy + 16 * d), 1.6f * d)
                drawLine(col, Offset(cx - 5 * d, yy + 16 * d), Offset(cx + 5 * d, yy + 16 * d), 1.6f * d)
            }
            drawLine(Color.Black, Offset(cx, y0 - 14 * d), Offset(cx, y0), 1.4f * d)
        }
        // La tubería con su vapor.
        drawRect(Color(0xFF3A2E42).copy(alpha = 0.9f), Offset(w * 0.08f, 0f), Size(7 * d, h - 40 * d))
        repeat(4) { k -> drawCircle(Color.White.copy(alpha = 0.05f), (18 + k * 10) * d, Offset(w * 0.08f + 12 * d + k * 8 * d, h * 0.5f - k * 22 * d)) }
        // El suelo mojado y los charcos con el reflejo de los letreros.
        drawRect(Color(0xFF0C0811), Offset(0f, h - 40 * d), Size(w, 40 * d))
        for (i in 0..n) {
            val cx = i * paso; val col = tonos[i % 3]
            drawOval(Brush.verticalGradient(listOf(col.copy(alpha = 0.35f), Color.Transparent), h - 34 * d, h - 6 * d), Offset(cx - 24 * d, h - 34 * d), Size(48 * d, 26 * d))
            drawLine(col.copy(alpha = 0.5f), Offset(cx, h - 32 * d), Offset(cx, h - 12 * d), 2 * d)
        }
    }

    /**
     * Refugio: la sala común bajo tierra. Una puerta de engranaje a medio abrir enmarca cada columna de piezas; entre las
     * columnas bajan tuberías con su manómetro y su llave; arriba corre el tubo de ventilación y abajo la rejilla del piso.
     * De día es el optimismo atómico (verde azulado, naranja, crema); de noche, la pantalla de fósforo verde.
     */
    internal fun DrawScope.salaComun(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val chapa = if (oscuro) Color(0xFF3DFF7F).copy(alpha = 0.22f) else Color(0xFF1C6E6A).copy(alpha = 0.35f)
        val tubo = if (oscuro) Color(0xFF3DFF7F).copy(alpha = 0.4f) else Color(0xFF8A8D7A).copy(alpha = 0.7f)
        val acento = if (oscuro) Color(0xFFFFB23F).copy(alpha = 0.5f) else Color(0xFFD9572B).copy(alpha = 0.7f)
        val amarillo = if (oscuro) Color(0xFF7CFFD0).copy(alpha = 0.3f) else Color(0xFFE8B83A).copy(alpha = 0.7f)
        // Los paneles de la pared, con sus remaches.
        var y = 0f; while (y < h) { drawLine(chapa.copy(alpha = chapa.alpha * 0.5f), Offset(0f, y), Offset(w, y), 1f * d); var x = 10 * d; while (x < w) { drawCircle(chapa, 1.2f * d, Offset(x, y + 5 * d)); x += 40 * d }; y += 60 * d }
        // El tubo de ventilación del techo con sus abrazaderas.
        drawRect(tubo, Offset(0f, 4 * d), Size(w, 10 * d))
        var x = 20 * d; while (x < w) { drawRect(acento, Offset(x, 2 * d), Size(4 * d, 14 * d)); x += 70 * d }
        val paso = w / n
        // La puerta de engranaje: un medio engrane sobre cada columna, con sus pernos.
        for (i in 0 until n) {
            val cx = (i + 0.5f) * paso; val r = paso * 0.42f; val cy = 22 * d + r
            val dientes = 20
            val rueda = Path().apply {
                for (k in 0..dientes) { val a = PI + k * PI / dientes; val rr = if (k % 2 == 0) r + 8 * d else r + 3 * d
                    val px = cx + (rr * cos(a)).toFloat(); val py = cy + (rr * sin(a)).toFloat(); if (k == 0) moveTo(px, py) else lineTo(px, py) }
            }
            drawPath(rueda, chapa, style = Stroke(6 * d))
            drawArc(amarillo, 180f, 180f, false, Offset(cx - r, cy - r), Size(2 * r, 2 * r), style = Stroke(2 * d))
            for (k in 0 until 7) { val a = PI + (k + 0.5) * PI / 7; drawCircle(acento, 2 * d, Offset(cx + ((r - 5 * d) * cos(a)).toFloat(), cy + ((r - 5 * d) * sin(a)).toFloat())) }
            // Las jambas: la puerta rodada hacia el lado, en el muro.
            listOf(cx - r, cx + r).forEach { xx -> drawLine(chapa, Offset(xx, cy), Offset(xx, h - 16 * d), 3 * d) }
        }
        // Las tuberías entre las columnas: dos tubos, un manómetro y una llave de paso.
        for (i in 0..n) {
            val cx = i * paso
            listOf(-5f, 5f).forEach { dx -> drawLine(tubo, Offset(cx + dx * d, 14 * d), Offset(cx + dx * d, h - 16 * d), 4 * d) }
            var yy = 90 * d
            while (yy < h - 60 * d) {
                val c = Offset(cx, yy)
                drawCircle(if (oscuro) Color(0xFF0B140D) else Color(0xFFF1E8D0), 9 * d, c)
                drawCircle(tubo, 9 * d, c, style = Stroke(2 * d))
                drawArc(acento, 150f, 60f, false, Offset(c.x - 6 * d, c.y - 6 * d), Size(12 * d, 12 * d), style = Stroke(1.5f * d))
                drawLine(if (oscuro) Color(0xFF3DFF7F) else Color(0xFF1F2A2A), c, Offset(c.x + 5 * d, c.y - 4 * d), 1.2f * d)
                val ll = Offset(cx, yy + 60 * d)
                if (ll.y < h - 30 * d) {
                    drawCircle(acento, 7 * d, ll, style = Stroke(2 * d))
                    drawLine(acento, Offset(ll.x - 7 * d, ll.y), Offset(ll.x + 7 * d, ll.y), 1.5f * d); drawLine(acento, Offset(ll.x, ll.y - 7 * d), Offset(ll.x, ll.y + 7 * d), 1.5f * d)
                }
                yy += 180 * d
            }
        }
        // La rejilla del piso y la franja de seguridad amarilla.
        drawRect(amarillo, Offset(0f, h - 16 * d), Size(w, 3 * d))
        var xr = 0f; while (xr < w) { drawLine(chapa, Offset(xr, h - 13 * d), Offset(xr, h), 1f * d); xr += 6 * d }
    }

    /**
     * Orden: la sala capitular de acero. Pilares de vigas remachadas entre las columnas de piezas, un dintel de acero en
     * escalones sobre cada columna, estandartes carmesí con el engrane colgando de los pilares, lámparas de latón y el piso
     * de placa antiderrapante.
     */
    internal fun DrawScope.salaCapitular(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val acero = (if (oscuro) Color(0xFF5A626B) else Color(0xFF8A929B)).copy(alpha = 0.55f)
        val sombraA = (if (oscuro) Color.Black else Color(0xFF3A4048)).copy(alpha = 0.3f)
        val carmesi = Color(0xFF9E1B22).copy(alpha = if (oscuro) 0.6f else 0.5f)
        val laton = Color(0xFFD6B25A).copy(alpha = 0.65f)
        // Las placas de la pared, con juntas y remaches.
        var y = 0f; while (y < h) { drawLine(sombraA.copy(alpha = 0.14f), Offset(0f, y), Offset(w, y), 1f * d); var x = 6 * d; while (x < w) { drawCircle(sombraA.copy(alpha = 0.2f), 1f * d, Offset(x, y + 4 * d)); x += 18 * d }; y += 48 * d }
        val paso = w / n
        // El dintel en escalones sobre cada columna.
        for (i in 0 until n) {
            val x0 = i * paso + 10 * d; val x1 = (i + 1) * paso - 10 * d; val cx = (x0 + x1) / 2
            val p = Path().apply { moveTo(x0, 60 * d); lineTo(x0, 30 * d); lineTo(x0 + (x1 - x0) * 0.2f, 30 * d); lineTo(x0 + (x1 - x0) * 0.2f, 20 * d); lineTo(x1 - (x1 - x0) * 0.2f, 20 * d); lineTo(x1 - (x1 - x0) * 0.2f, 30 * d); lineTo(x1, 30 * d); lineTo(x1, 60 * d) }
            drawPath(p, acero, style = Stroke(5 * d))
            drawLine(laton, Offset(x0 + (x1 - x0) * 0.2f, 20 * d), Offset(x1 - (x1 - x0) * 0.2f, 20 * d), 1.5f * d)
            // La lámpara de latón que cuelga al centro del dintel.
            drawLine(sombraA, Offset(cx, 20 * d), Offset(cx, 8 * d), 1f * d)
            drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD27A).copy(alpha = if (oscuro) 0.22f else 0.14f), Color.Transparent), Offset(cx, 8 * d), 40 * d), 40 * d, Offset(cx, 8 * d))
            drawRect(laton, Offset(cx - 5 * d, 4 * d), Size(10 * d, 5 * d))
        }
        // Los pilares de vigas remachadas entre las columnas, con su estandarte.
        for (i in 0..n) {
            val x = i * paso
            drawRect(acero, Offset(x - 9 * d, 0f), Size(18 * d, h))
            drawRect(sombraA, Offset(x - 9 * d, 0f), Size(4 * d, h)); drawRect(sombraA, Offset(x + 5 * d, 0f), Size(4 * d, h))
            var yy = 12 * d; while (yy < h) { drawCircle(sombraA.copy(alpha = 0.6f), 1.4f * d, Offset(x, yy)); yy += 16 * d }
            val y0 = 70 * d; val an = 11 * d
            drawLine(laton, Offset(x - an - 3 * d, y0), Offset(x + an + 3 * d, y0), 2 * d)
            drawPath(Path().apply { moveTo(x - an, y0); lineTo(x + an, y0); lineTo(x + an, y0 + 86 * d); lineTo(x, y0 + 74 * d); lineTo(x - an, y0 + 86 * d); close() }, carmesi)
            // El engrane bordado en el estandarte.
            val c = Offset(x, y0 + 30 * d)
            for (k in 0 until 8) { val a = k * PI / 4; drawLine(laton, c, Offset(c.x + (6 * d * cos(a)).toFloat(), c.y + (6 * d * sin(a)).toFloat()), 2.4f * d) }
            drawCircle(laton, 4.4f * d, c); drawCircle(carmesi, 1.8f * d, c)
        }
        // El piso de placa antiderrapante.
        drawRect(acero.copy(alpha = acero.alpha * 0.6f), Offset(0f, h - 14 * d), Size(w, 14 * d))
        var xr = 4 * d; var k = 0; while (xr < w) { val yy = h - 7 * d; drawLine(sombraA, Offset(xr - 2 * d, yy + (if (k % 2 == 0) 2 else -2) * d), Offset(xr + 2 * d, yy - (if (k % 2 == 0) 2 else -2) * d), 1.2f * d); xr += 8 * d; k++ }
    }

    /**
     * Vapor victoriano: la nave de una gran exposición del XIX. Arcos de hierro fundido con su vidriera de cuadrícula,
     * columnas acanaladas con capitel, el zócalo de ladrillo con ventanas en arco, lámparas de gas colgadas con su halo y,
     * en la pared, un volante enorme detrás de cada arco.
     */
    internal fun DrawScope.naveExposicion(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val hierro = (if (oscuro) Color(0xFF5E5850) else Color(0xFF3B3835)).copy(alpha = 0.6f)
        val vidrio = (if (oscuro) Color(0xFF8FB8C8) else Color(0xFF5E8FA0)).copy(alpha = if (oscuro) 0.08f else 0.1f)
        val ladrillo = Color(0xFF9C4A32).copy(alpha = if (oscuro) 0.4f else 0.32f)
        val laton = Color(0xFFC9A04A).copy(alpha = 0.7f)
        val paso = w / n
        // La vidriera del techo: cuadrícula de hierro sobre el vidrio.
        drawRect(vidrio, Offset(0f, 0f), Size(w, h * 0.55f))
        var x = 0f; while (x < w) { drawLine(hierro.copy(alpha = 0.25f), Offset(x, 0f), Offset(x, h * 0.55f), 0.8f * d); x += 14 * d }
        var y = 0f; while (y < h * 0.55f) { drawLine(hierro.copy(alpha = 0.25f), Offset(0f, y), Offset(w, y), 0.8f * d); y += 14 * d }
        for (i in 0 until n) {
            val x0 = i * paso; val x1 = (i + 1) * paso; val cx = (x0 + x1) / 2
            // El volante de la pared, detrás del arco.
            val v = Offset(cx, h * 0.42f); val rv = paso * 0.32f
            drawCircle(hierro.copy(alpha = 0.3f), rv, v, style = Stroke(6 * d))
            for (k in 0 until 6) { val a = k * PI / 3; drawLine(hierro.copy(alpha = 0.25f), v, Offset(v.x + (rv * cos(a)).toFloat(), v.y + (rv * sin(a)).toFloat()), 3 * d) }
            // El arco de hierro fundido, con su celosía de círculos.
            drawArc(hierro, 180f, 180f, false, Offset(x0 + 6 * d, 18 * d), Size(paso - 12 * d, paso * 0.7f), style = Stroke(5 * d))
            drawArc(hierro.copy(alpha = 0.4f), 180f, 180f, false, Offset(x0 + 14 * d, 26 * d), Size(paso - 28 * d, paso * 0.6f), style = Stroke(2 * d))
            // La lámpara de gas colgada del arco, con su halo amarillo.
            drawLine(hierro, Offset(cx, 18 * d), Offset(cx, 46 * d), 1 * d)
            drawCircle(Brush.radialGradient(listOf(Color(0xFFF2C46A).copy(alpha = if (oscuro) 0.3f else 0.18f), Color.Transparent), Offset(cx, 52 * d), 46 * d), 46 * d, Offset(cx, 52 * d))
            drawPath(Path().apply { moveTo(cx - 6 * d, 46 * d); lineTo(cx + 6 * d, 46 * d); lineTo(cx + 4 * d, 58 * d); lineTo(cx - 4 * d, 58 * d); close() }, laton)
        }
        // El zócalo de ladrillo con sus hiladas y ventanas en arco.
        val z = h * 0.78f
        drawRect(ladrillo, Offset(0f, z), Size(w, h - z))
        var yy = z; var fila = 0
        while (yy < h) { drawLine(Color(0xFFD9C7A8).copy(alpha = 0.18f), Offset(0f, yy), Offset(w, yy), 0.8f * d); var xx = if (fila % 2 == 0) 0f else 9 * d; while (xx < w) { drawLine(Color(0xFFD9C7A8).copy(alpha = 0.18f), Offset(xx, yy), Offset(xx, yy + 6 * d), 0.8f * d); xx += 18 * d }; yy += 6 * d; fila++ }
        for (i in 0 until n) {
            val cx = (i + 0.5f) * paso
            drawArc(hierro.copy(alpha = 0.5f), 180f, 180f, true, Offset(cx - 9 * d, z + 6 * d), Size(18 * d, 14 * d))
            drawRect(hierro.copy(alpha = 0.5f), Offset(cx - 9 * d, z + 13 * d), Size(18 * d, 12 * d))
        }
        // Las columnas de hierro, acanaladas, con el capitel y la basa de latón.
        for (i in 0..n) {
            val xc = i * paso
            drawRect(hierro, Offset(xc - 6 * d, 0f), Size(12 * d, h))
            listOf(-3f, 0f, 3f).forEach { dx -> drawLine(Color.Black.copy(alpha = 0.18f), Offset(xc + dx * d, 30 * d), Offset(xc + dx * d, h - 20 * d), 0.8f * d) }
            drawRect(laton, Offset(xc - 10 * d, 22 * d), Size(20 * d, 6 * d))
            drawRect(laton, Offset(xc - 10 * d, h - 20 * d), Size(20 * d, 6 * d))
        }
    }

    /**
     * Laboratorio: el laboratorio limpio. Una campana de extracción enmarca cada columna de piezas; entre las columnas hay
     * vitrinas de vidrio con matraces en sus repisas; arriba, los paneles de luz y el ducto; abajo, la mesa con su canto cian.
     */
    internal fun DrawScope.laboratorioLimpio(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val linea = (if (oscuro) Color(0xFF97A8B9) else Color(0xFF4D5D6B)).copy(alpha = 0.35f)
        val cian = (if (oscuro) Color(0xFF3CD2E5) else Color(0xFF0B8FA1)).copy(alpha = 0.55f)
        val vidrio = Color(0xFFCFE6F0).copy(alpha = if (oscuro) 0.06f else 0.22f)
        val liquidos = listOf(Color(0xFF0B8FA1), Color(0xFF5E4FD1), Color(0xFF1E8E5A)).map { it.copy(alpha = if (oscuro) 0.5f else 0.45f) }
        // Los azulejos clínicos de la pared.
        var y = 0f; while (y < h) { drawLine(linea.copy(alpha = 0.08f), Offset(0f, y), Offset(w, y), 1f); y += 22 * d }
        var x = 0f; while (x < w) { drawLine(linea.copy(alpha = 0.08f), Offset(x, 0f), Offset(x, h), 1f); x += 22 * d }
        // Los paneles de luz del techo y el ducto de la extracción.
        drawRect(linea.copy(alpha = 0.25f), Offset(0f, 0f), Size(w, 8 * d))
        val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso + 12 * d; val x1 = (i + 1) * paso - 12 * d; val cx = (x0 + x1) / 2
            drawRect(Color.White.copy(alpha = if (oscuro) 0.25f else 0.9f), Offset(cx - 30 * d, 8 * d), Size(60 * d, 4 * d))
            // La campana: el ducto, el cuerpo inclinado, el marco de la guillotina y su vidrio.
            drawRect(linea, Offset(cx - 8 * d, 12 * d), Size(16 * d, 14 * d))
            val cuerpo = Path().apply { moveTo(cx - 10 * d, 26 * d); lineTo(cx + 10 * d, 26 * d); lineTo(x1, 50 * d); lineTo(x0, 50 * d); close() }
            drawPath(cuerpo, linea.copy(alpha = 0.18f)); drawPath(cuerpo, linea, style = Stroke(1.5f * d))
            drawRect(vidrio, Offset(x0, 50 * d), Size(x1 - x0, 40 * d))
            drawRect(linea, Offset(x0, 50 * d), Size(x1 - x0, 40 * d), style = Stroke(2 * d))
            drawLine(cian, Offset(x0, 90 * d), Offset(x1, 90 * d), 2 * d)
            // Las jambas laterales de la campana, que bajan a la mesa.
            listOf(x0, x1).forEach { xx -> drawLine(linea, Offset(xx, 90 * d), Offset(xx, h - 22 * d), 1.4f * d) }
        }
        // Las vitrinas entre las columnas: vidrio, repisas y matraces.
        for (i in 0..n) {
            val cx = i * paso; val an = 24 * d; val y0 = 40 * d; val y1 = h - 22 * d
            drawRect(vidrio, Offset(cx - an / 2, y0), Size(an, y1 - y0))
            drawRect(linea, Offset(cx - an / 2, y0), Size(an, y1 - y0), style = Stroke(1.2f * d))
            var yy = y0 + 46 * d; var k = 0
            while (yy < y1 - 10 * d) {
                drawLine(linea, Offset(cx - an / 2, yy), Offset(cx + an / 2, yy), 1.2f * d)
                val c = liquidos[(i + k) % 3]
                when (k % 3) {
                    0 -> { drawCircle(c, 6 * d, Offset(cx, yy - 7 * d)); drawRect(linea, Offset(cx - 1.5f * d, yy - 20 * d), Size(3 * d, 8 * d)) }
                    1 -> drawPath(Path().apply { moveTo(cx - 2 * d, yy - 20 * d); lineTo(cx + 2 * d, yy - 20 * d); lineTo(cx + 2 * d, yy - 12 * d); lineTo(cx + 7 * d, yy); lineTo(cx - 7 * d, yy); lineTo(cx - 2 * d, yy - 12 * d); close() }, c)
                    else -> listOf(-6f, 0f, 6f).forEach { dx -> drawRect(c, Offset(cx + dx * d - 1.5f * d, yy - 16 * d), Size(3 * d, 16 * d)) }
                }
                yy += 46 * d; k++
            }
        }
        // La mesa corrida con su canto cian.
        drawRect(linea.copy(alpha = 0.2f), Offset(0f, h - 22 * d), Size(w, 22 * d))
        drawLine(cian, Offset(0f, h - 22 * d), Offset(w, h - 22 * d), 2 * d)
    }

    /**
     * Espacio: la cúpula del observatorio. Sobre cada columna de piezas, una cúpula con sus costillas y la ranura abierta
     * al cielo estrellado; entre las columnas, telescopios en su montura; abajo, el riel circular de la cúpula.
     */
    internal fun DrawScope.observatorio(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val linea = (if (oscuro) Color(0xFF8E9AB8) else Color(0xFF4E5A70)).copy(alpha = 0.4f)
        val naranja = (if (oscuro) Color(0xFFFFB86B) else Color(0xFFE8601C)).copy(alpha = 0.6f)
        val az = java.util.Random(52)
        // El cielo que se ve por arriba (más estrellas de noche).
        repeat(if (oscuro) 70 else 25) { drawCircle(if (oscuro) Color.White.copy(alpha = 0.5f) else linea, (0.6f + az.nextFloat()) * d, Offset(az.nextFloat() * w, az.nextFloat() * h * 0.35f)) }
        val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso + 8 * d; val x1 = (i + 1) * paso - 8 * d; val cx = (x0 + x1) / 2; val r = (x1 - x0) / 2
            val cy = 12 * d + r
            // La cúpula: el casco, las costillas, la ranura abierta con estrellas dentro.
            drawArc(linea.copy(alpha = linea.alpha * 0.35f), 180f, 180f, true, Offset(x0, 12 * d), Size(2 * r, 2 * r))
            drawArc(linea, 180f, 180f, false, Offset(x0, 12 * d), Size(2 * r, 2 * r), style = Stroke(3 * d))
            for (k in 1..4) { val f = k / 5f; drawArc(linea.copy(alpha = linea.alpha * 0.6f), 180f, 180f, false, Offset(cx - r * f, 12 * d), Size(2 * r * f, 2 * r), style = Stroke(1f * d)) }
            val ranura = Path().apply { moveTo(cx - 8 * d, cy); lineTo(cx - 8 * d, 14 * d); lineTo(cx + 8 * d, 14 * d); lineTo(cx + 8 * d, cy); close() }
            drawPath(ranura, Color(0xFF03050C).copy(alpha = if (oscuro) 0.8f else 0.55f))
            clipPath(ranura) { repeat(8) { drawCircle(Color.White.copy(alpha = 0.85f), (0.6f + az.nextFloat() * 0.8f) * d, Offset(cx - 7 * d + az.nextFloat() * 14 * d, 14 * d + az.nextFloat() * (cy - 14 * d))) } }
            drawLine(linea, Offset(x0, cy), Offset(x1, cy), 2 * d)
            listOf(x0, x1).forEach { xx -> drawLine(linea, Offset(xx, cy), Offset(xx, h - 14 * d), 1.6f * d) }
        }
        // Los telescopios entre las columnas, en su montura ecuatorial.
        for (i in 0..n) {
            val cx = i * paso
            var yy = h * 0.5f
            while (yy < h - 40 * d) {
                drawLine(linea, Offset(cx, yy + 30 * d), Offset(cx, yy + 6 * d), 3 * d)
                drawLine(linea, Offset(cx - 10 * d, yy + 30 * d), Offset(cx + 10 * d, yy + 30 * d), 2 * d)
                val a = -PI / 3
                val t0 = Offset(cx - (14 * d * cos(a)).toFloat(), yy + 6 * d - (14 * d * sin(a)).toFloat()); val t1 = Offset(cx + (20 * d * cos(a)).toFloat(), yy + 6 * d + (20 * d * sin(a)).toFloat())
                drawLine(linea.copy(alpha = linea.alpha * 1.6f), t0, t1, 6 * d, StrokeCap.Round)
                drawCircle(naranja, 2.4f * d, Offset(cx, yy + 6 * d))
                yy += 240 * d
            }
        }
        // El riel circular de la cúpula y el piso.
        drawRect(linea.copy(alpha = linea.alpha * 0.4f), Offset(0f, h - 14 * d), Size(w, 14 * d))
        var xr = 0f; while (xr < w) { drawCircle(naranja.copy(alpha = 0.4f), 1.6f * d, Offset(xr, h - 7 * d)); xr += 30 * d }
    }

    /**
     * Abismo: la sala del fondo del mar. Sobre cada columna de piezas, un gran ojo de buey de latón por el que se ve el agua
     * (azul con haces de sol de día; negra con medusas que brillan de noche); entre las columnas, tubos remachados del
     * casco con sus manómetros; abajo, el piso de rejilla y la arena con coral.
     */
    internal fun DrawScope.salaAbisal(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val laton = Color(0xFFB8913E).copy(alpha = if (oscuro) 0.7f else 0.8f)
        val agua = if (oscuro) Color(0xFF020D18) else Color(0xFF3F8FB0)
        val luz = if (oscuro) Color(0xFF5CF2C6) else Color.White
        val az = java.util.Random(61)
        val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso + 10 * d; val x1 = (i + 1) * paso - 10 * d; val cx = (x0 + x1) / 2; val r = (x1 - x0) / 2
            val c = Offset(cx, 14 * d + r)
            drawCircle(agua.copy(alpha = 0.55f), r, c)
            clipPath(Path().apply { addOval(Rect(c, r)) }) {
                if (oscuro) repeat(2) {
                    val m = Offset(cx + (az.nextFloat() - 0.5f) * r, c.y + (az.nextFloat() - 0.5f) * r); val s = r * 0.18f
                    drawCircle(luz.copy(alpha = 0.15f), s * 2.2f, m)
                    drawArc(luz.copy(alpha = 0.7f), 180f, 180f, true, Offset(m.x - s, m.y - s * 0.8f), Size(s * 2, s * 1.6f))
                    for (k in 0..2) drawLine(luz.copy(alpha = 0.5f), Offset(m.x - s * 0.6f + k * s * 0.6f, m.y), Offset(m.x - s * 0.6f + k * s * 0.6f, m.y + s * 1.6f), 0.8f * d)
                } else for (k in 0 until 3) {
                    val xh = c.x - r + r * 0.6f * k + r * 0.2f
                    drawPath(Path().apply { moveTo(xh, c.y - r); lineTo(xh + r * 0.2f, c.y - r); lineTo(xh + r * 0.5f, c.y + r); lineTo(xh + r * 0.2f, c.y + r); close() }, luz.copy(alpha = 0.18f))
                }
                repeat(6) { drawCircle(luz.copy(alpha = 0.5f), (0.8f + az.nextFloat()) * d, Offset(cx + (az.nextFloat() - 0.5f) * r * 1.6f, c.y + (az.nextFloat() - 0.5f) * r * 1.6f), style = Stroke(0.7f * d)) }
            }
            drawCircle(laton, r, c, style = Stroke(6 * d))
            for (k in 0 until 10) { val a = Math.toRadians(k * 36.0); drawCircle(laton, 1.8f * d, Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat())) }
            drawArc(Color.White.copy(alpha = 0.3f), 200f, 50f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(2.4f * d))
        }
        // Los tubos remachados del casco entre las columnas, con su manómetro.
        val tubo = (if (oscuro) Color(0xFF3E4A52) else Color(0xFF8A969E)).copy(alpha = 0.55f)
        for (i in 0..n) {
            val cx = i * paso
            drawLine(tubo, Offset(cx, 0f), Offset(cx, h - 18 * d), 7 * d)
            var y = 40 * d; while (y < h - 30 * d) { drawCircle(laton.copy(alpha = 0.6f), 1.4f * d, Offset(cx - 2.5f * d, y)); drawCircle(laton.copy(alpha = 0.6f), 1.4f * d, Offset(cx + 2.5f * d, y)); y += 34 * d }
            val m = Offset(cx, h * 0.55f)
            drawCircle(cs.surface.copy(alpha = 0.9f), 7 * d, m); drawCircle(laton, 7 * d, m, style = Stroke(1.6f * d))
            drawLine(Color(0xFFC0563F), m, m + Offset(4 * d, -3 * d), 1.2f * d)
        }
        // El piso de rejilla y la arena con coral.
        drawRect(Color(0xFFD9C79A).copy(alpha = if (oscuro) 0.15f else 0.35f), Offset(0f, h - 18 * d), Size(w, 18 * d))
        var xr = 0f; while (xr < w) { drawLine(tubo, Offset(xr, h - 18 * d), Offset(xr + 6 * d, h), 1f * d); xr += 10 * d }
        repeat(5) { val x = az.nextFloat() * w; drawLine(Color(0xFFC0563F).copy(alpha = 0.6f), Offset(x, h - 4 * d), Offset(x + 3 * d, h - 16 * d), 2 * d, StrokeCap.Round) }
    }

    /**
     * Soviético: una estación de metro constructivista. Sobre cada columna de piezas, una bóveda en escalones con una
     * banda roja en diagonal y el medallón de la estrella; entre las columnas, pilones de mármol con franjas y lámparas
     * de brazo; abajo, el andén con la línea de seguridad y las vías.
     */
    internal fun DrawScope.estacionMetro(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val marmol = (if (oscuro) Color(0xFF3D3333) else Color(0xFFE3D6B8)).copy(alpha = 0.6f)
        val veta = (if (oscuro) Color.Black else Color(0xFF8A7A60)).copy(alpha = 0.18f)
        val rojo = Color(0xFFC1121F).copy(alpha = if (oscuro) 0.6f else 0.55f)
        val oro = Color(0xFFD4A017).copy(alpha = 0.75f)
        val negro = Color(0xFF161414).copy(alpha = if (oscuro) 0.7f else 0.5f)
        val paso = w / n
        // Las bóvedas en escalones, con la banda roja en diagonal y el medallón.
        for (i in 0 until n) {
            val x0 = i * paso + 10 * d; val x1 = (i + 1) * paso - 10 * d; val cx = (x0 + x1) / 2; val an = x1 - x0
            val boveda = Path().apply { moveTo(x0, 90 * d); lineTo(x0, 50 * d); lineTo(x0 + an * 0.15f, 30 * d); lineTo(x0 + an * 0.35f, 30 * d); lineTo(x0 + an * 0.45f, 16 * d); lineTo(x1 - an * 0.45f, 16 * d); lineTo(x1 - an * 0.35f, 30 * d); lineTo(x1 - an * 0.15f, 30 * d); lineTo(x1, 50 * d); lineTo(x1, 90 * d) }
            drawPath(boveda, marmol, style = Stroke(6 * d))
            drawLine(rojo, Offset(x0 + an * 0.05f, 80 * d), Offset(x0 + an * 0.3f, 34 * d), 5 * d)
            drawLine(negro, Offset(x1 - an * 0.05f, 80 * d), Offset(x1 - an * 0.2f, 52 * d), 3 * d)
            drawCircle(oro, 11 * d, Offset(cx, 32 * d), style = Stroke(2 * d))
            with(TrofeosTema) { estrella(Offset(cx, 32 * d), 8 * d, 3.2f * d, 5, Brush.linearGradient(listOf(rojo, rojo))) }
        }
        // Los pilones de mármol entre las columnas: vetas, franjas rojas y la lámpara de brazo.
        for (i in 0..n) {
            val x = i * paso; val an = 22 * d
            drawRect(marmol, Offset(x - an / 2, 0f), Size(an, h - 26 * d))
            val az = java.util.Random(i + 40L)
            repeat(6) { val y = az.nextFloat() * h; drawLine(veta, Offset(x - an / 2, y), Offset(x + an / 2, y + (az.nextFloat() - 0.5f) * 30 * d), 1f * d) }
            listOf(0.18f, 0.2f).forEach { f -> drawRect(rojo, Offset(x - an / 2, h * f), Size(an, 3 * d)) }
            drawRect(oro, Offset(x - an / 2 - 2 * d, h - 32 * d), Size(an + 4 * d, 6 * d))
            var yy = h * 0.36f
            while (yy < h - 60 * d) {
                drawLine(oro, Offset(x, yy), Offset(x + 14 * d, yy - 10 * d), 1.6f * d); drawLine(oro, Offset(x, yy), Offset(x - 14 * d, yy - 10 * d), 1.6f * d)
                listOf(-14f, 14f).forEach { dx -> drawCircle(Brush.radialGradient(listOf(Color(0xFFFFE6A3).copy(alpha = 0.35f), Color.Transparent), Offset(x + dx * d, yy - 12 * d), 16 * d), 16 * d, Offset(x + dx * d, yy - 12 * d)); drawCircle(Color(0xFFFFE6A3).copy(alpha = 0.8f), 3 * d, Offset(x + dx * d, yy - 12 * d)) }
                yy += 220 * d
            }
        }
        // El andén: la línea de seguridad, el borde y las vías con sus durmientes.
        drawRect(marmol.copy(alpha = marmol.alpha * 0.5f), Offset(0f, h - 26 * d), Size(w, 14 * d))
        drawRect(oro, Offset(0f, h - 22 * d), Size(w, 2 * d))
        drawRect(negro.copy(alpha = 0.3f), Offset(0f, h - 12 * d), Size(w, 12 * d))
        var xt = 0f; while (xt < w) { drawRect(negro, Offset(xt, h - 9 * d), Size(4 * d, 9 * d)); xt += 10 * d }
        listOf(h - 8 * d, h - 3 * d).forEach { y -> drawLine(Color(0xFF8A8D91).copy(alpha = 0.7f), Offset(0f, y), Offset(w, y), 1.4f * d) }
    }
}
