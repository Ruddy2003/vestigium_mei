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
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * El motor de rutas: cada campaña es un mapa con su punto de partida, siete paradas con nombre del tema, el trazo del
 * camino (calzada, ruta de caravanas, postas del yam, huellas de carreta…) y el destino. El marcador del tema avanza por
 * la ruta con los días cumplidos. Aquí están los fondos, los caminos, las paradas y el marcador de todos los temas; la escena
 * de la victoria está en [MapasCampana].
 */
object MapasMas {
    /**
     * Las siete paradas de cada tema, en fracciones del mapa (más o menos donde quedan en su geografía o en su dibujo de
     * fondo). Son siempre siete, una por etapa de `Campanas.etapas`.
     */
    fun puntos(tema: TemaId): List<Offset> = when (tema) {
        // La calzada: Roma → Ostia → Massilia → Lugdunum → Durocortorum → Lutetia → Alesia. Italia queda a la izquierda y la
        // Galia a la derecha; Ostia y Massilia sobre la costa, Lugdunum sobre el Ródano y Lutetia sobre el Sena.
        TemaId.ROMANO -> listOf(Offset(0.07f, 0.58f), Offset(0.17f, 0.74f), Offset(0.35f, 0.77f), Offset(0.48f, 0.48f), Offset(0.64f, 0.2f), Offset(0.8f, 0.36f), Offset(0.92f, 0.6f))
        // El paseo por el centro de Roma (el norte arriba): de Testaccio se cruza el Tíber a Trastevere, se sube al
        // Gianicolo, se baja a Borgo junto al Vaticano, se vuelve a cruzar el río hacia Piazza Navona y el Panteón, y se
        // termina en la Fontana di Trevi.
        TemaId.URBE -> listOf(Offset(0.5f, 0.9f), Offset(0.26f, 0.74f), Offset(0.1f, 0.54f), Offset(0.2f, 0.27f), Offset(0.56f, 0.44f), Offset(0.7f, 0.52f), Offset(0.88f, 0.34f))
        // La marcha de Alejandro: Pella y Anfípolis al norte del Egeo, Troya en la costa de Asia, Sardes, Gordio, Tarso junto
        // al mar del sur y Babilonia sobre el Éufrates.
        TemaId.HELENO -> listOf(Offset(0.07f, 0.17f), Offset(0.19f, 0.13f), Offset(0.31f, 0.36f), Offset(0.42f, 0.62f), Offset(0.55f, 0.3f), Offset(0.71f, 0.72f), Offset(0.91f, 0.48f))
        // El Nilo río arriba, de norte (arriba) a sur: el Delta, Menfis, Hermópolis, Abidos, Tebas, Edfu y Elefantina, en la primera catarata.
        TemaId.EGIPTO -> listOf(Offset(0.52f, 0.1f), Offset(0.49f, 0.22f), Offset(0.45f, 0.36f), Offset(0.55f, 0.48f), Offset(0.62f, 0.62f), Offset(0.58f, 0.76f), Offset(0.54f, 0.9f))
        // El Camino de Santiago, de los Pirineos a Galicia (el este queda a la izquierda): Roncesvalles, Pamplona, Logroño,
        // Burgos, León, Astorga y Santiago.
        TemaId.MEDIEVAL -> geo(listOf(-1.32 to 43.01, -1.64 to 42.81, -2.45 to 42.47, -3.7 to 42.34, -5.57 to 42.6, -6.06 to 42.46, -8.54 to 42.88), -1.0, -9.0, 41.8, 43.6)
        // La marcha por una frontera inventada: de la encomienda de la costa (el mar a la izquierda), por las atalayas y el
        // pozo del desierto, a través del vado del río, hasta el castillo de Montalbar en las montañas.
        TemaId.TEMPLARIO -> listOf(Offset(0.08f, 0.62f), Offset(0.2f, 0.38f), Offset(0.34f, 0.68f), Offset(0.48f, 0.44f), Offset(0.6f, 0.72f), Offset(0.75f, 0.4f), Offset(0.9f, 0.22f))
        // La obra de la catedral, vista de lado: de los cimientos junto al ábside, por los muros, las columnas, los arbotantes
        // y la bóveda, al rosetón del crucero y a las campanas de la torre.
        TemaId.GOTICO -> listOf(Offset(0.07f, 0.9f), Offset(0.18f, 0.66f), Offset(0.29f, 0.84f), Offset(0.4f, 0.62f), Offset(0.5f, 0.42f), Offset(0.62f, 0.54f), Offset(0.77f, 0.24f))
        // Por la costa hasta el faro: la estación, la posada, el archivo, el muelle, la marisma, los acantilados y el faro.
        TemaId.ARCANO -> listOf(Offset(0.08f, 0.88f), Offset(0.2f, 0.74f), Offset(0.3f, 0.58f), Offset(0.47f, 0.66f), Offset(0.58f, 0.47f), Offset(0.7f, 0.33f), Offset(0.84f, 0.15f))
        // Del papel al cuadro: el boceto y el estudio en las hojas de la izquierda, y en el lienzo el cartón, la imprimación,
        // el color, la veladura y la firma en la esquina de abajo.
        TemaId.RENACIMIENTO -> listOf(Offset(0.08f, 0.66f), Offset(0.19f, 0.3f), Offset(0.35f, 0.6f), Offset(0.45f, 0.26f), Offset(0.55f, 0.66f), Offset(0.61f, 0.3f), Offset(0.66f, 0.82f))
        // El archipiélago: del Puerto de la Brea, a la izquierda, isla por isla en zigzag hasta la Isla de la X.
        TemaId.PIRATA -> listOf(Offset(0.08f, 0.7f), Offset(0.21f, 0.36f), Offset(0.35f, 0.66f), Offset(0.48f, 0.3f), Offset(0.61f, 0.6f), Offset(0.75f, 0.28f), Offset(0.9f, 0.62f))
        // La campaña de 1805: de Boulogne, junto al canal de la Mancha, se cruza el Rin en Kehl, se baja al Danubio en Ulm,
        // Múnich, Viena (Schönbrunn), y al norte a Brünn y Austerlitz.
        TemaId.NAPOLEON -> listOf(Offset(0.07f, 0.22f), Offset(0.245f, 0.44f), Offset(0.4f, 0.82f), Offset(0.53f, 0.6f), Offset(0.69f, 0.78f), Offset(0.8f, 0.4f), Offset(0.93f, 0.52f))
        // El crucero de Le Havre a Nueva York, con Europa a la derecha y América a la izquierda: Le Havre, Southampton al
        // norte, Cherburgo al sur, Queenstown en Irlanda, la alta mar, el banco de Terranova y la bahía de Nueva York.
        TemaId.DECO -> listOf(Offset(0.92f, 0.5f), Offset(0.84f, 0.3f), Offset(0.8f, 0.56f), Offset(0.66f, 0.26f), Offset(0.47f, 0.46f), Offset(0.27f, 0.3f), Offset(0.08f, 0.62f))
        // Las jornadas de París, cruzando el Sena de una orilla a la otra en cada tramo.
        TemaId.REVOLUCION -> listOf(Offset(0.07f, 0.2f), Offset(0.17f, 0.7f), Offset(0.33f, 0.22f), Offset(0.47f, 0.84f), Offset(0.6f, 0.32f), Offset(0.76f, 0.76f), Offset(0.93f, 0.2f))
        // Toledo → Calatrava → Córdoba → Sevilla → Málaga → Almería → Granada.
        TemaId.ANDALUS -> geo(listOf(-4.02 to 39.86, -3.83 to 38.9, -4.78 to 37.88, -5.98 to 37.39, -4.42 to 36.72, -2.46 to 36.84, -3.6 to 37.18), -6.4, -2.1, 36.4, 40.2)
        // El sacbé corre casi recto por la selva, de la ciudad del poniente a la del oriente; cada etapa, un punto del camino.
        TemaId.MESO -> listOf(Offset(0.07f, 0.72f), Offset(0.21f, 0.64f), Offset(0.35f, 0.57f), Offset(0.49f, 0.51f), Offset(0.63f, 0.44f), Offset(0.78f, 0.37f), Offset(0.93f, 0.29f))
        // Chang'an → Dunhuang → Turfán → Kashgar → Samarcanda → Bujará → Antioquía (de oriente a occidente).
        TemaId.SEDA -> geo(listOf(108.9 to 34.3, 94.7 to 40.1, 89.2 to 42.9, 76.0 to 39.5, 66.9 to 39.6, 64.4 to 39.8, 36.2 to 36.2), 112.0, 33.0, 31.0, 45.0)
        // El Tōkaidō, de Edo a Kioto (el este queda a la izquierda): Nihonbashi → Hakone → Fuchū → Hamamatsu → Kuwana → Seki → el puente de Sanjō.
        TemaId.EDO -> geo(listOf(139.774 to 35.684, 139.025 to 35.19, 138.383 to 34.976, 137.73 to 34.71, 136.69 to 35.06, 136.39 to 34.85, 135.773 to 35.009), 140.0, 135.6, 34.5, 35.9)
        // Las postas del yam, de Janbalic a Karakórum.
        TemaId.MONGOL -> listOf(Offset(0.93f, 0.7f), Offset(0.8f, 0.42f), Offset(0.66f, 0.62f), Offset(0.52f, 0.36f), Offset(0.38f, 0.56f), Offset(0.22f, 0.3f), Offset(0.07f, 0.44f))
        // La caravana de carretas: del puerto al pueblo nuevo, cruzando el río.
        TemaId.COLONOS -> listOf(Offset(0.07f, 0.72f), Offset(0.2f, 0.5f), Offset(0.34f, 0.62f), Offset(0.5f, 0.38f), Offset(0.64f, 0.56f), Offset(0.79f, 0.32f), Offset(0.93f, 0.48f))
        // La expedición por los biomas, en línea casi recta, de franja en franja.
        TemaId.BOTANICO -> (0 until 7).map { i -> Offset(0.07f + 0.86f * i / 6f, 0.5f + 0.16f * sin(i * 1.3f)) }
        // Calle por calle: la autopista rota, la gasolinera, la avenida, el mercado, la biblioteca, el paso a desnivel y la torre.
        TemaId.RUINAS -> listOf(Offset(0.06f, 0.84f), Offset(0.2f, 0.68f), Offset(0.32f, 0.5f), Offset(0.48f, 0.6f), Offset(0.6f, 0.4f), Offset(0.76f, 0.5f), Offset(0.92f, 0.18f))
        // Los mercados de Navidad: Estrasburgo, Colmar, Núremberg, Dresde, Praga, Viena, Salzburgo.
        TemaId.NAVIDAD -> geo(listOf(7.75 to 48.58, 7.36 to 48.08, 11.08 to 49.45, 13.74 to 51.05, 14.42 to 50.08, 16.37 to 48.21, 13.04 to 47.8), 6.4, 17.2, 47.2, 51.6)
        // La travesía de la compañía: aldea, bosque, vado, montañas, mina, puente y fortaleza.
        TemaId.ANTIGUAS -> listOf(Offset(0.07f, 0.74f), Offset(0.22f, 0.56f), Offset(0.36f, 0.7f), Offset(0.5f, 0.3f), Offset(0.62f, 0.52f), Offset(0.78f, 0.62f), Offset(0.93f, 0.3f))
        // La migración tras las manadas: del abrigo del risco (abajo a la izquierda) se cruza el río helado y la estepa, el vado
        // de los renos, la cueva de los caballos y las fuentes tibias, hasta los pastos del verano arriba a la derecha.
        TemaId.CUEVA -> listOf(Offset(0.08f, 0.78f), Offset(0.24f, 0.62f), Offset(0.4f, 0.72f), Offset(0.54f, 0.48f), Offset(0.66f, 0.28f), Offset(0.8f, 0.42f), Offset(0.92f, 0.22f))
        // Los puntos de control: piso por piso, de la ventanilla de abajo al Ministerio en lo alto.
        TemaId.DISTOPIA -> listOf(Offset(0.08f, 0.84f), Offset(0.22f, 0.72f), Offset(0.36f, 0.74f), Offset(0.5f, 0.54f), Offset(0.62f, 0.5f), Offset(0.74f, 0.32f), Offset(0.88f, 0.16f))
        // La traza de la red: saltos de servidor en servidor, de la máquina propia a la raíz.
        TemaId.CIBER -> listOf(Offset(0.07f, 0.52f), Offset(0.2f, 0.28f), Offset(0.34f, 0.6f), Offset(0.48f, 0.36f), Offset(0.62f, 0.7f), Offset(0.77f, 0.4f), Offset(0.93f, 0.6f))
        // La bajada: un nivel más hondo en cada parada, de arriba a la izquierda hasta el fondo a la derecha.
        TemaId.NEON -> listOf(Offset(0.08f, 0.1f), Offset(0.24f, 0.23f), Offset(0.4f, 0.36f), Offset(0.54f, 0.5f), Offset(0.68f, 0.63f), Offset(0.82f, 0.77f), Offset(0.93f, 0.9f))
        // Del dormitorio del nivel más hondo a la salida: pasillos y escaleras de nivel en nivel (las escaleras quedan a la mitad de cada tramo).
        TemaId.REFUGIO -> listOf(Offset(0.07f, 0.86f), Offset(0.22f, 0.68f), Offset(0.42f, 0.68f), Offset(0.54f, 0.5f), Offset(0.68f, 0.5f), Offset(0.8f, 0.32f), Offset(0.93f, 0.12f))
        // El avance en el mapa táctico: del campamento a la fortaleza, cuadro por cuadro.
        TemaId.ORDEN -> listOf(Offset(0.07f, 0.8f), Offset(0.2f, 0.6f), Offset(0.34f, 0.72f), Offset(0.48f, 0.46f), Offset(0.62f, 0.58f), Offset(0.76f, 0.36f), Offset(0.91f, 0.24f))
        // La línea del Norte: del depósito, abajo a la izquierda, por el viaducto y el túnel de la sierra, a la estación terminal.
        TemaId.VAPOR -> listOf(Offset(0.08f, 0.86f), Offset(0.24f, 0.7f), Offset(0.4f, 0.76f), Offset(0.52f, 0.54f), Offset(0.66f, 0.42f), Offset(0.8f, 0.5f), Offset(0.92f, 0.2f))
        // El protocolo: la pipeta pasa de pozo en pozo por la microplaca hasta la placa de Petri.
        TemaId.LABORATORIO -> listOf(Offset(0.07f, 0.72f), Offset(0.2f, 0.56f), Offset(0.33f, 0.42f), Offset(0.47f, 0.32f), Offset(0.6f, 0.42f), Offset(0.73f, 0.58f), Offset(0.88f, 0.48f))
        // De la Tierra a Marte: la plataforma, la órbita baja, la Luna, el punto de Lagrange, Fobos, la órbita marciana y Marte.
        TemaId.ESPACIO -> listOf(Offset(0.1f, 0.62f), Offset(0.2f, 0.4f), Offset(0.32f, 0.7f), Offset(0.47f, 0.34f), Offset(0.66f, 0.64f), Offset(0.78f, 0.38f), Offset(0.91f, 0.56f))
        // La bajada por capas, en un corte del mar: del muelle a la zona iluminada, la crepuscular, la de medianoche,
        // la llanura abisal, la zona hadal y el fondo de la fosa; cada etapa más honda que la anterior.
        TemaId.ABISMO -> listOf(Offset(0.08f, 0.1f), Offset(0.24f, 0.24f), Offset(0.46f, 0.33f), Offset(0.3f, 0.47f), Offset(0.52f, 0.6f), Offset(0.74f, 0.7f), Offset(0.88f, 0.88f))
        // El Transiberiano: Moscú → Ekaterimburgo → Novosibirsk → Krasnoyarsk → Irkutsk → Chitá → Vladivostok.
        TemaId.URSS -> geo(listOf(37.6 to 55.75, 60.6 to 56.84, 82.9 to 55.03, 92.87 to 56.01, 104.3 to 52.29, 113.5 to 52.03, 131.9 to 43.12), 33.0, 136.0, 40.0, 60.0)
    }

    /** Llama a [f] cada [paso] a lo largo de [p] con el punto y la dirección (para traviesas, paquetes, flechas…). */
    private fun aLoLargo(p: Path, paso: Float, desde: Float = paso / 2, f: (Offset, Offset) -> Unit) {
        val pm = PathMeasure().apply { setPath(p, false) }
        var s = desde; while (s < pm.length) { f(pm.getPosition(s), pm.getTangent(s)); s += paso }
    }

    /** Proyecta longitudes y latitudes al rectángulo del mapa (con margen). */
    private fun geo(l: List<Pair<Double, Double>>, lon0: Double, lon1: Double, lat0: Double, lat1: Double): List<Offset> =
        l.map { (lon, lat) -> Offset((0.07 + 0.86 * (lon - lon0) / (lon1 - lon0)).toFloat(), (0.92 - 0.84 * (lat - lat0) / (lat1 - lat0)).toFloat()) }

    // ================================================================== fondos

    fun DrawScope.fondoMas(tema: TemaId, cs: ColorScheme, avance: Float) {
        val oscuro = cs.background.luminance() < 0.5f
        val w = size.width; val h = size.height; val d = density
        val tinta = cs.onSurfaceVariant.copy(alpha = 0.22f)
        val agua = Color(0xFF4F86B8).copy(alpha = if (oscuro) 0.28f else 0.2f)
        fun monte(x: Float, y: Float, s: Float, c: Color) = drawPath(Path().apply { moveTo(x - s, y); lineTo(x, y - s * 1.1f); lineTo(x + s, y) }, c, style = Stroke(1.2f * d))
        when (tema) {
            TemaId.ANDALUS -> {
                // El mar al sur, el Guadalquivir y la Sierra Nevada.
                drawPath(Path().apply { moveTo(0f, h); lineTo(0f, h * 0.86f); cubicTo(w * 0.2f, h * 0.92f, w * 0.4f, h * 1.02f, w * 0.6f, h * 0.98f); cubicTo(w * 0.8f, h * 0.95f, w * 0.9f, h * 0.98f, w, h * 0.9f); lineTo(w, h); close() }, agua)
                drawPath(Path().apply { moveTo(w * 0.62f, h * 0.36f); cubicTo(w * 0.4f, h * 0.4f, w * 0.3f, h * 0.55f, w * 0.12f, h * 0.68f); lineTo(w * 0.05f, h * 0.86f) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(2 * d))
                for (k in 0 until 5) monte(w * (0.62f + k * 0.04f), h * 0.86f, 7 * d, tinta)
                // Un fondo de lazo tenue.
                with(TrofeosMas) { for (i in 0 until 6) estrellaLazo(Offset(w * (0.1f + i * 0.17f), h * 0.12f), 6 * d, Brush.linearGradient(listOf(tinta.copy(alpha = 0.12f), tinta.copy(alpha = 0.12f))), null, 0f) }
            }
            TemaId.MESO -> {
                // La selva de las tierras bajas, toda copas de árboles; dos cenotes (los pozos de agua dulce), la costa del
                // Caribe al oriente y, a lo lejos, pirámides que asoman por encima de la selva.
                val selva = Color(0xFF3F7A4A).copy(alpha = if (oscuro) 0.12f else 0.16f)
                val azar = java.util.Random(584283)
                repeat(70) {
                    val x = azar.nextFloat() * w; val y = azar.nextFloat() * h; val r = (5f + azar.nextFloat() * 7f) * d
                    drawCircle(selva, r, Offset(x, y)); drawCircle(selva, r * 0.7f, Offset(x + r * 0.7f, y - r * 0.3f))
                }
                drawPath(Path().apply { moveTo(w, 0f); lineTo(w * 0.86f, 0f); cubicTo(w * 0.9f, h * 0.25f, w * 0.84f, h * 0.5f, w * 0.95f, h * 0.75f); lineTo(w, h * 0.8f); close() }, agua)
                listOf(Offset(0.28f, 0.82f), Offset(0.6f, 0.2f)).forEach { o ->
                    drawCircle(agua.copy(alpha = agua.alpha * 2.2f), 7 * d, Offset(w * o.x, h * o.y))
                    drawCircle(tinta, 9 * d, Offset(w * o.x, h * o.y), style = Stroke(1.2f * d))
                }
                listOf(Offset(0.14f, 0.3f), Offset(0.45f, 0.82f), Offset(0.72f, 0.72f)).forEach { o ->
                    piramideMaya(Offset(w * o.x, h * o.y), 18 * d, 18 * d, tinta, tinta.copy(alpha = tinta.alpha * 1.6f), tinta, gradas = 3)
                }
            }
            TemaId.SEDA -> {
                // Dunas del Taklamakán, los montes del Pamir y el oasis.
                val arena = Color(0xFFD9B57A).copy(alpha = if (oscuro) 0.16f else 0.22f)
                for (k in 0 until 7) { val x = w * (0.3f + k * 0.07f); drawArc(arena, 200f, 140f, true, Offset(x, h * 0.6f), Size(70 * d, 30 * d)) }
                for (k in 0 until 4) monte(w * (0.44f + k * 0.035f), h * 0.3f, 9 * d, tinta)
                drawCircle(Color(0xFF4E9A72).copy(alpha = 0.25f), 10 * d, Offset(w * 0.8f, h * 0.32f))
                // La costa del Mediterráneo al oeste.
                drawPath(Path().apply { moveTo(0f, 0f); lineTo(w * 0.04f, 0f); cubicTo(w * 0.07f, h * 0.4f, w * 0.03f, h * 0.7f, w * 0.06f, h); lineTo(0f, h); close() }, agua)
            }
            TemaId.EDO -> {
                // La costa del Pacífico al sur (la bahía de Edo, la península de Izu, la bahía de Suruga y la de Ise), el monte
                // Fuji nevado sobre Hakone, el lago Biwa junto a Kioto y unos pinos del camino.
                val costa = listOf(0f to 0.36f, 0.1f to 0.34f, 0.14f to 0.5f, 0.2f to 0.46f, 0.26f to 0.55f, 0.28f to 0.8f, 0.32f to 0.62f, 0.37f to 0.68f,
                    0.45f to 0.83f, 0.55f to 0.86f, 0.62f to 0.72f, 0.66f to 0.64f, 0.7f to 0.8f, 0.72f to 0.95f, 1f to 1f).map { (x, y) -> Offset(w * x, h * y) }
                drawPath(Path().apply {
                    moveTo(0f, h); lineTo(costa[0].x, costa[0].y)
                    for (i in 1 until costa.size - 1) { val m = (costa[i] + costa[i + 1]) / 2f; quadraticTo(costa[i].x, costa[i].y, m.x, m.y) }
                    lineTo(costa.last().x, costa.last().y); close()
                }, agua)
                val fuji = Offset(w * 0.318f, h * 0.404f); val s = 26 * d
                drawPath(Path().apply { moveTo(fuji.x - s * 1.4f, fuji.y + s * 0.5f); lineTo(fuji.x - s * 0.22f, fuji.y - s * 0.55f); quadraticTo(fuji.x, fuji.y - s * 0.62f, fuji.x + s * 0.22f, fuji.y - s * 0.55f); lineTo(fuji.x + s * 1.4f, fuji.y + s * 0.5f); close() },
                    Color(0xFF1E3A5F).copy(alpha = if (oscuro) 0.3f else 0.18f))
                drawPath(Path().apply { moveTo(fuji.x - s * 0.5f, fuji.y - s * 0.3f); lineTo(fuji.x - s * 0.22f, fuji.y - s * 0.55f); quadraticTo(fuji.x, fuji.y - s * 0.62f, fuji.x + s * 0.22f, fuji.y - s * 0.55f); lineTo(fuji.x + s * 0.5f, fuji.y - s * 0.3f)
                    lineTo(fuji.x + s * 0.25f, fuji.y - s * 0.22f); lineTo(fuji.x, fuji.y - s * 0.32f); lineTo(fuji.x - s * 0.25f, fuji.y - s * 0.2f); close() }, Color.White.copy(alpha = if (oscuro) 0.3f else 0.7f))
                drawOval(agua, Offset(w * 0.815f, h * 0.33f), Size(w * 0.035f, h * 0.27f))
                listOf(0.19f to 0.36f, 0.42f to 0.72f, 0.58f to 0.68f, 0.79f to 0.68f).forEach { (x, y) ->
                    val o = Offset(w * x, h * y)
                    drawLine(tinta, o, Offset(o.x, o.y - 9 * d), 1.2f * d)
                    drawOval(tinta, Offset(o.x - 6 * d, o.y - 12 * d), Size(12 * d, 4 * d)); drawOval(tinta, Offset(o.x - 4 * d, o.y - 15 * d), Size(8 * d, 3 * d))
                }
            }
            TemaId.MONGOL -> {
                // La estepa: pasto, el Gobi al sur, los montes al norte y el gran cielo.
                drawRect(Brush.verticalGradient(listOf(Color(0xFF2E6DB4).copy(alpha = if (oscuro) 0.14f else 0.1f), Color.Transparent), 0f, h * 0.4f), Offset.Zero, Size(w, h * 0.4f))
                val az = java.util.Random(9)
                repeat(70) { val x = az.nextFloat() * w; val y = h * (0.3f + az.nextFloat() * 0.6f); drawLine(Color(0xFF6B8A3A).copy(alpha = 0.25f), Offset(x, y), Offset(x + 2 * d, y - 5 * d), 1f * d) }
                drawOval(Color(0xFFD9B57A).copy(alpha = 0.2f), Offset(w * 0.5f, h * 0.68f), Size(w * 0.35f, h * 0.3f))
                for (k in 0 until 6) monte(w * (0.1f + k * 0.06f), h * 0.2f, 8 * d, tinta)
            }
            TemaId.COLONOS -> {
                // Lomas, un río que la caravana cruza y bosquecitos.
                for (k in 0 until 4) drawArc(Color(0xFF6B8A3A).copy(alpha = 0.15f), 180f, 180f, true, Offset(w * (k * 0.27f - 0.05f), h * 0.6f), Size(w * 0.4f, h * 0.6f))
                drawPath(Path().apply { moveTo(w * 0.28f, 0f); cubicTo(w * 0.24f, h * 0.3f, w * 0.32f, h * 0.6f, w * 0.27f, h) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(4 * d))
                listOf(0.12f to 0.25f, 0.45f to 0.78f, 0.72f to 0.78f, 0.86f to 0.2f).forEach { (x, y) -> for (j in 0 until 3) drawPath(Path().apply { val cx = w * x + j * 8 * d; moveTo(cx - 4 * d, h * y); lineTo(cx, h * y - 10 * d); lineTo(cx + 4 * d, h * y); close() }, Color(0xFF3F6A3A).copy(alpha = 0.3f)) }
            }
            TemaId.BOTANICO -> {
                // Franjas de bioma: bosque, pradera, desierto, selva y manglar; se colorean con los días cumplidos.
                val biomas = listOf(Color(0xFF6B8A3A), Color(0xFFB5B85A), Color(0xFFD9B57A), Color(0xFF2E6B3A), Color(0xFF5A8A7A), Color(0xFF8AB07A))
                biomas.forEachIndexed { i, c ->
                    val x0 = w * i / biomas.size
                    val lleno = (i + 0.5f) / biomas.size <= avance
                    drawRect(c.copy(alpha = if (lleno) (if (oscuro) 0.22f else 0.2f) else 0.07f), Offset(x0, 0f), Size(w / biomas.size, h))
                }
                // Hojitas dibujadas a tinta sepia.
                val az = java.util.Random(14)
                repeat(14) { val o = Offset(az.nextFloat() * w, az.nextFloat() * h); drawPath(Path().apply { moveTo(o.x, o.y); quadraticTo(o.x + 6 * d, o.y - 6 * d, o.x + 12 * d, o.y); quadraticTo(o.x + 6 * d, o.y + 4 * d, o.x, o.y); close() }, Color(0xFF6E5530).copy(alpha = 0.18f)) }
            }
            TemaId.RUINAS -> {
                // El plano de la ciudad desde arriba: manzanas de hormigón entre calles que el bosque se va comiendo (más copas
                // cuanto más se avanza), un río que volvió a su cauce y una autopista rota que cruza en diagonal.
                val az = java.util.Random(31)
                drawPath(Path().apply { moveTo(0f, h * 0.3f); cubicTo(w * 0.3f, h * 0.2f, w * 0.5f, h * 0.45f, w, h * 0.35f) }, Color(0xFF5A8AA0).copy(alpha = 0.18f), style = Stroke(6 * d))
                val manzana = 46 * d; val calle = 12 * d
                val gris = (if (oscuro) Color(0xFF9AA39A) else Color(0xFF6A6F66)).copy(alpha = if (oscuro) 0.10f else 0.12f)
                val copa = Color(0xFF4F7A33).copy(alpha = if (oscuro) 0.22f else 0.2f)
                var y = calle; var fila = 0
                while (y < h) {
                    var x = calle + (if (fila % 2 == 0) 0f else manzana / 3)
                    while (x < w) {
                        drawRect(gris, Offset(x, y), Size(manzana, manzana * 0.8f))
                        val copas = (1 + (avance * 6).toInt() + az.nextInt(2)).coerceAtMost(7)
                        repeat(copas) { drawCircle(copa, (4 + az.nextFloat() * 7) * d, Offset(x + az.nextFloat() * manzana, y + az.nextFloat() * manzana * 0.8f)) }
                        x += maxOf(manzana + calle, 1f)
                    }
                    y += maxOf(manzana * 0.8f + calle, 1f); fila++
                }
                val via = (if (oscuro) Color(0xFFB8BCB0) else Color(0xFF55594F)).copy(alpha = 0.18f)
                drawLine(via, Offset(0f, h * 0.95f), Offset(w * 0.42f, h * 0.55f), 9 * d)
                drawLine(via, Offset(w * 0.5f, h * 0.48f), Offset(w, h * 0.05f), 9 * d)
            }
            TemaId.NAVIDAD -> {
                // Lomas nevadas, pinos, y la nieve cayendo.
                drawRect(Brush.verticalGradient(listOf(Color(0xFF1F3A6E).copy(alpha = if (oscuro) 0.25f else 0.1f), Color.Transparent), 0f, h * 0.5f), Offset.Zero, Size(w, h * 0.5f))
                for (k in 0 until 4) drawArc(Color.White.copy(alpha = if (oscuro) 0.12f else 0.6f), 180f, 180f, true, Offset(w * (k * 0.28f - 0.06f), h * 0.66f), Size(w * 0.42f, h * 0.6f))
                val az = java.util.Random(25)
                repeat(10) { val x = az.nextFloat() * w; val y = h * (0.15f + az.nextFloat() * 0.7f); drawPath(Path().apply { moveTo(x - 5 * d, y); lineTo(x, y - 14 * d); lineTo(x + 5 * d, y); close() }, Color(0xFF1F6B3A).copy(alpha = 0.3f)) }
                repeat(40) { drawCircle(Color.White.copy(alpha = if (oscuro) 0.35f else 0.8f), (0.8f + az.nextFloat() * 1.4f) * d, Offset(az.nextFloat() * w, az.nextFloat() * h)) }
            }
            TemaId.ANTIGUAS -> {
                // Pergamino con el bosque viejo, la cordillera, la boca de la mina y el río del vado.
                drawRect(Color(0xFFE9DCC0).copy(alpha = if (oscuro) 0.05f else 0.25f), Offset.Zero, size)
                val az = java.util.Random(31)
                repeat(22) { val c = Offset(w * (0.14f + az.nextFloat() * 0.18f), h * (0.32f + az.nextFloat() * 0.3f)); drawCircle(Color(0xFF3F6A3A).copy(alpha = 0.22f), (4 + az.nextFloat() * 3) * d, c) }
                for (k in 0 until 7) monte(w * (0.42f + k * 0.035f), h * (0.36f + (k % 2) * 0.06f), 10 * d, tinta.copy(alpha = 0.4f))
                drawArc(Color(0xFF2A2420).copy(alpha = 0.35f), 180f, 180f, true, Offset(w * 0.6f, h * 0.5f), Size(16 * d, 16 * d))
                drawPath(Path().apply { moveTo(w * 0.3f, 0f); cubicTo(w * 0.34f, h * 0.4f, w * 0.38f, h * 0.7f, w * 0.34f, h) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(3 * d))
            }
            TemaId.CUEVA -> {
                // La estepa helada de la migración: el risco con el abrigo a la izquierda, el río helado, los montes nevados, la
                // hierba de la estepa, la manada de bisontes que va hacia los pastos del verano y el vapor de las fuentes tibias.
                drawRect(Color(0xFFE6D8BC).copy(alpha = if (oscuro) 0.05f else 0.25f), Offset.Zero, size)
                drawPath(Path().apply { moveTo(0f, h); lineTo(0f, h * 0.55f); quadraticTo(w * 0.08f, h * 0.5f, w * 0.14f, h * 0.68f); lineTo(w * 0.14f, h); close() }, tinta.copy(alpha = 0.35f))
                drawArc(Color.Black.copy(alpha = 0.3f), 180f, 180f, true, Offset(w * 0.03f, h * 0.74f), Size(12 * d, 12 * d))
                val rio = Path().apply { moveTo(w * 0.22f, 0f); cubicTo(w * 0.32f, h * 0.35f, w * 0.18f, h * 0.6f, w * 0.3f, h) }
                drawPath(rio, Color(0xFFDDEBF2).copy(alpha = if (oscuro) 0.25f else 0.9f), style = Stroke(6 * d))
                drawPath(rio, agua, style = Stroke(2 * d))
                for (k in 0 until 5) monte(w * (0.48f + k * 0.06f), h * 0.18f + (k % 2) * 4 * d, 12 * d, tinta.copy(alpha = 0.45f))
                val az = java.util.Random(17)
                repeat(26) {
                    val x = w * (0.34f + az.nextFloat() * 0.6f); val y = h * (0.55f + az.nextFloat() * 0.4f)
                    drawLine(Color(0xFF7A8A4A).copy(alpha = 0.35f), Offset(x, y), Offset(x + 2 * d, y - 4 * d), 1 * d); drawLine(Color(0xFF7A8A4A).copy(alpha = 0.35f), Offset(x, y), Offset(x - 2 * d, y - 4 * d), 1 * d)
                }
                listOf(0.5f to 0.62f, 0.56f to 0.66f, 0.6f to 0.6f, 0.66f to 0.64f).forEachIndexed { i, (fx, fy) -> drawPath(bisonte(Offset(w * fx, h * fy), (6 - i % 2) * d), Color(0xFF3A2A1E).copy(alpha = 0.55f)) }
                listOf(0f, 1f).forEach { k -> val x = w * 0.8f + k * 6 * d
                    drawPath(Path().apply { moveTo(x, h * 0.42f); cubicTo(x - 4 * d, h * 0.38f, x + 4 * d, h * 0.34f, x, h * 0.3f) }, Color.White.copy(alpha = if (oscuro) 0.25f else 0.6f), style = Stroke(1.4f * d)) }
            }
            TemaId.DISTOPIA -> {
                // Pisos de un edificio de oficinas en corte, con sus ventanillas; a la derecha, la torre del Ministerio.
                val linea = cs.onSurface.copy(alpha = if (oscuro) 0.18f else 0.14f)
                for (k in 1 until 6) { val y = h * k / 6f; drawLine(linea, Offset(0f, y), Offset(w * 0.8f, y), 1f * d) }
                val az = java.util.Random(17)
                for (k in 0 until 6) for (j in 0 until 10) if (az.nextInt(3) > 0) drawRect(linea, Offset(w * (0.03f + j * 0.075f), h * (k / 6f + 0.04f)), Size(w * 0.035f, h * 0.07f))
                drawRect(cs.onSurface.copy(alpha = if (oscuro) 0.22f else 0.16f), Offset(w * 0.82f, h * 0.04f), Size(w * 0.14f, h * 0.96f))
                for (k in 0 until 9) drawRect(Color(0xFFC8102E).copy(alpha = 0.25f + 0.5f * avance), Offset(w * 0.86f, h * (0.12f + k * 0.09f)), Size(w * 0.06f, h * 0.03f))
                drawRect(Color(0xFFC8102E).copy(alpha = 0.6f), Offset(w * 0.82f, h * 0.04f), Size(w * 0.14f, h * 0.02f))
            }
            TemaId.CIBER -> {
                // La red a oscuras: rejilla de puntos, servidores sueltos unidos por enlaces tenues y un volcado en hexadecimal a la derecha.
                val cian = Color(0xFF00E5FF)
                var y = 6 * d; while (y < h) { var x = 6 * d; while (x < w) { drawCircle(cian.copy(alpha = 0.09f), 0.8f * d, Offset(x, y)); x += 12 * d }; y += 12 * d }
                val az = java.util.Random(23)
                val nodos = List(12) { Offset(w * (0.04f + 0.8f * az.nextFloat()), h * (0.08f + 0.84f * az.nextFloat())) }
                nodos.forEachIndexed { i, a -> drawLine(cian.copy(alpha = 0.1f), a, nodos[(i * 5 + 3) % nodos.size], 0.8f * d) }
                nodos.forEach { drawRect(cian.copy(alpha = 0.18f), Offset(it.x - 4 * d, it.y - 3 * d), Size(8 * d, 6 * d), style = Stroke(0.8f * d)) }
                val hex = "0123456789ABCDEF"
                with(TrofeosMas) {
                    var yy = 10 * d; while (yy < h) {
                        val t = (0 until 4).joinToString(" ") { "" + hex[az.nextInt(16)] + hex[az.nextInt(16)] }
                        mono(t, w - 4 * d, yy, 7 * d, cian.copy(alpha = 0.06f + 0.14f * avance), android.graphics.Paint.Align.RIGHT); yy += 9 * d
                    }
                }
            }
            TemaId.NEON -> {
                // Corte de la megaciudad: siete losas de nivel una bajo otra, cada vez más oscuras y más llenas de letreros;
                // la lluvia cae en diagonal y, a la izquierda, el número de cada nivel.
                val magenta = Color(0xFFFF3D9A); val ambar = Color(0xFFFFB238); val verde = Color(0xFF8CFF5A)
                val az = java.util.Random(31)
                for (k in 0 until 7) {
                    val y0 = h * (k / 7f); val y1 = h * ((k + 1) / 7f)
                    drawRect(Color(0xFF0C0811).copy(alpha = 0.25f + k * 0.08f), Offset(0f, y0), Size(w, y1 - y0))
                    drawLine(Color(0xFF4B2B47).copy(alpha = 0.8f), Offset(0f, y1), Offset(w, y1), 2 * d)
                    repeat(3 + k) {
                        val x = az.nextFloat() * w; val an = (6 + az.nextInt(10)) * d; val al = (y1 - y0) * (0.3f + 0.4f * az.nextFloat())
                        val col = listOf(magenta, ambar, verde)[az.nextInt(3)].copy(alpha = 0.12f + 0.25f * avance)
                        drawRect(col, Offset(x, y1 - al - 3 * d), Size(an, al), style = Stroke(1 * d))
                    }
                    with(TrofeosMas) { mono("−${k + 1}", 4 * d, y0 + 11 * d, 8 * d, ambar.copy(alpha = 0.45f)) }
                }
                repeat(90) { val x = az.nextFloat() * w; val y = az.nextFloat() * h; drawLine(Color(0xFF9DB4D8).copy(alpha = 0.12f), Offset(x, y), Offset(x - 3 * d, y + 10 * d), 0.7f * d) }
            }
            TemaId.REFUGIO -> {
                // El refugio en corte: la tierra rayada, los niveles con sus cuartos, las escaleras entre niveles y la superficie con su sol.
                val tierra = if (oscuro) Color(0xFF3DFF7F).copy(alpha = 0.08f) else Color(0xFF8A6A44).copy(alpha = 0.14f)
                val muro = if (oscuro) Color(0xFF3DFF7F).copy(alpha = 0.3f) else Color(0xFF1C6E6A).copy(alpha = 0.45f)
                val sup = h * 0.2f
                drawRect(Brush.verticalGradient(listOf(Color(0xFF6FB3E0).copy(alpha = if (oscuro) 0.06f else 0.2f), Color.Transparent), 0f, sup), Offset.Zero, Size(w, sup))
                drawCircle(Color(0xFFE8B83A).copy(alpha = 0.4f + 0.4f * avance), 9 * d, Offset(w * 0.75f, sup * 0.45f))
                drawLine(Color(0xFF6B8A3A).copy(alpha = 0.6f), Offset(0f, sup), Offset(w, sup), 2 * d)
                var x = -h; while (x < w) { drawLine(tierra, Offset(x, sup), Offset(x + h, h + sup), 1f * d); x += 9 * d }
                // Los niveles: cada uno es un pasillo con cuartos.
                listOf(0.32f, 0.5f, 0.68f, 0.86f).forEach { f ->
                    val y = h * f; val x0 = w * 0.03f; val x1 = w * 0.97f
                    drawRect(if (oscuro) Color(0xFF070D08).copy(alpha = 0.7f) else Color(0xFFF1E8D0).copy(alpha = 0.8f), Offset(x0, y - h * 0.11f), Size(x1 - x0, h * 0.13f))
                    drawRect(muro, Offset(x0, y - h * 0.11f), Size(x1 - x0, h * 0.13f), style = Stroke(1.2f * d))
                    var xx = x0 + w * 0.1f; while (xx < x1) { drawLine(muro.copy(alpha = muro.alpha * 0.6f), Offset(xx, y - h * 0.11f), Offset(xx, y - h * 0.04f), 1f * d); xx += w * 0.12f }
                }
                // Las escaleras entre niveles, a la mitad de cada tramo de la ruta.
                listOf(Triple(0.145f, 0.68f, 0.86f), Triple(0.48f, 0.5f, 0.68f), Triple(0.74f, 0.32f, 0.5f), Triple(0.865f, 0.12f, 0.32f)).forEach { (fx, ya, yb) ->
                    val xx = w * fx
                    listOf(-4f, 4f).forEach { dx -> drawLine(muro, Offset(xx + dx * d, h * ya), Offset(xx + dx * d, h * yb), 1f * d) }
                    var yy = h * ya + 3 * d; while (yy < h * yb) { drawLine(muro, Offset(xx - 4 * d, yy), Offset(xx + 4 * d, yy), 1f * d); yy += 4 * d }
                }
                // La compuerta de salida, redonda, en la superficie.
                drawCircle(muro, 7 * d, Offset(w * 0.93f, sup + 4 * d), style = Stroke(2 * d))
            }
            TemaId.ORDEN -> {
                // El mapa táctico: la cuadrícula con sus letras y números, curvas de nivel, las zonas enemigas rayadas y la fortaleza en estrella.
                val linea = cs.onSurface.copy(alpha = if (oscuro) 0.16f else 0.12f)
                val carmesi = Color(0xFF9E1B22)
                for (k in 1 until 8) drawLine(linea, Offset(w * k / 8f, 0f), Offset(w * k / 8f, h), 1f * d)
                for (k in 1 until 4) drawLine(linea, Offset(0f, h * k / 4f), Offset(w, h * k / 4f), 1f * d)
                with(TrofeosMas) {
                    for (k in 0 until 8) mono("" + ('A' + k), w * (k + 0.5f) / 8f, 9 * d, 7 * d, linea.copy(alpha = linea.alpha * 2.5f), android.graphics.Paint.Align.CENTER)
                    for (k in 0 until 4) mono("${k + 1}", 3 * d, h * (k + 0.5f) / 4f, 7 * d, linea.copy(alpha = linea.alpha * 2.5f))
                }
                listOf(Offset(0.3f, 0.3f), Offset(0.62f, 0.8f)).forEach { o -> for (j in 1..3) drawOval(linea, Offset(w * o.x - j * 18 * d, h * o.y - j * 9 * d), Size(j * 36 * d, j * 18 * d), style = Stroke(0.8f * d)) }
                // Las zonas del enemigo, rayadas en carmesí, que se aclaran conforme se avanza.
                listOf(Rect(w * 0.52f, h * 0.06f, w * 0.7f, h * 0.3f), Rect(w * 0.74f, h * 0.5f, w * 0.96f, h * 0.9f)).forEach { r ->
                    drawRect(carmesi.copy(alpha = 0.4f * (1 - avance) + 0.1f), r.topLeft, r.size, style = Stroke(1f * d))
                    clipRect(r.left, r.top, r.right, r.bottom) { var x = r.left - r.height; while (x < r.right) { drawLine(carmesi.copy(alpha = 0.18f * (1 - avance) + 0.04f), Offset(x, r.bottom), Offset(x + r.height, r.top), 1f * d); x += 6 * d } }
                }
                // La fortaleza en estrella, junto a la meta.
                val c = Offset(w * 0.91f, h * 0.24f); val R = 16 * d
                drawPath(Path().apply { for (k in 0 until 10) { val a = -PI / 2 + k * PI / 5; val rr = if (k % 2 == 0) R else R * 0.6f; val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat(); if (k == 0) moveTo(x, y) else lineTo(x, y) }; close() }, linea.copy(alpha = linea.alpha * 2), style = Stroke(1.4f * d))
            }
            TemaId.VAPOR -> {
                // El plano de la línea, como los que se presentaban al Parlamento: curvas de nivel de la sierra, el río con
                // el viaducto de siete arcos, la boca del túnel, los postes del telégrafo a lo largo de la vía y, junto a la
                // terminal, las chimeneas de las fábricas, que echan más humo conforme se avanza.
                val linea = cs.onSurface.copy(alpha = if (oscuro) 0.16f else 0.13f)
                val ladrillo = Color(0xFF9C3D24).copy(alpha = if (oscuro) 0.4f else 0.32f)
                listOf(Offset(0.62f, 0.2f), Offset(0.3f, 0.32f)).forEach { o -> for (j in 1..4) drawOval(linea, Offset(w * o.x - j * 16 * d, h * o.y - j * 8 * d), Size(j * 32 * d, j * 16 * d), style = Stroke(0.8f * d)) }
                val rio = Path().apply { moveTo(w * 0.2f, 0f); cubicTo(w * 0.36f, h * 0.3f, w * 0.22f, h * 0.55f, w * 0.34f, h * 0.74f); cubicTo(w * 0.4f, h * 0.86f, w * 0.36f, h * 0.94f, w * 0.42f, h) }
                drawPath(rio, agua.copy(alpha = agua.alpha * 2f), style = Stroke(7 * d))
                // El viaducto: siete arcos sobre el río, entre la segunda y la tercera estación.
                val vx = w * 0.3f; val vy = h * 0.72f; val ancho = 7 * d
                drawLine(linea.copy(alpha = linea.alpha * 3), Offset(vx - ancho * 3.5f, vy), Offset(vx + ancho * 3.5f, vy), 1.6f * d)
                for (k in 0 until 7) drawArc(linea.copy(alpha = linea.alpha * 3), 180f, 180f, false, Offset(vx - ancho * 3.5f + k * ancho, vy + 1 * d), Size(ancho, ancho * 1.2f), style = Stroke(1f * d))
                // La boca del túnel en la sierra.
                val tx = w * 0.59f; val ty = h * 0.48f
                drawArc(linea.copy(alpha = linea.alpha * 3), 180f, 180f, true, Offset(tx - 6 * d, ty - 6 * d), Size(12 * d, 12 * d))
                // Los postes del telégrafo.
                var x = w * 0.06f; while (x < w * 0.95f) { drawLine(linea, Offset(x, h * 0.95f), Offset(x, h * 0.9f), 0.8f * d); drawLine(linea, Offset(x - 2 * d, h * 0.905f), Offset(x + 2 * d, h * 0.905f), 0.8f * d); x += 22 * d }
                drawLine(linea.copy(alpha = linea.alpha * 0.6f), Offset(w * 0.06f, h * 0.905f), Offset(w * 0.95f, h * 0.905f), 0.5f * d)
                // Las chimeneas de las fábricas cerca de la terminal, con su humo.
                listOf(0.8f to 0.16f, 0.86f to 0.1f).forEach { (fx, fy) ->
                    drawRect(ladrillo, Offset(w * fx - 2.5f * d, h * fy), Size(5 * d, h * 0.12f))
                    for (k in 0..2) drawCircle(cs.onSurface.copy(alpha = (0.05f + 0.08f * avance) * (1 - k * 0.25f)), (3 + k * 1.5f) * d, Offset(w * fx + k * 4 * d, h * fy - (3 + k * 4) * d))
                }
            }
            TemaId.LABORATORIO -> {
                // La microplaca de pozos (se llenan columna por columna con los días cumplidos) y la placa de Petri en la meta.
                val linea = cs.onSurface.copy(alpha = if (oscuro) 0.16f else 0.12f)
                val placa = Rect(w * 0.03f, h * 0.1f, w * 0.8f, h * 0.9f)
                drawRoundRect(linea.copy(alpha = linea.alpha * 0.4f), placa.topLeft, placa.size, androidx.compose.ui.geometry.CornerRadius(8 * d))
                drawRoundRect(linea, placa.topLeft, placa.size, androidx.compose.ui.geometry.CornerRadius(8 * d), style = Stroke(1.2f * d))
                val cols = 12; val filas = 8
                val colores = listOf(Color(0xFF0B8FA1), Color(0xFF5E4FD1), Color(0xFF1E8E5A))
                for (i in 0 until cols) for (j in 0 until filas) {
                    val c = Offset(placa.left + placa.width * (i + 0.5f) / cols, placa.top + placa.height * (j + 0.5f) / filas)
                    val r = minOf(placa.width / cols, placa.height / filas) * 0.34f
                    if ((i + 0.5f) / cols <= avance) drawCircle(colores[(i + j) % 3].copy(alpha = 0.25f), r, c)
                    drawCircle(linea, r, c, style = Stroke(0.8f * d))
                }
                with(TrofeosMas) {
                    for (i in 0 until cols) mono("${i + 1}", placa.left + placa.width * (i + 0.5f) / cols, placa.top - 2 * d, 6 * d, linea.copy(alpha = linea.alpha * 2), android.graphics.Paint.Align.CENTER)
                }
                val pc = Offset(w * 0.88f, h * 0.48f); val pr = minOf(h * 0.36f, w * 0.09f)
                drawCircle(Color(0xFFF1E6B8).copy(alpha = if (oscuro) 0.08f else 0.4f), pr, pc)
                drawCircle(linea.copy(alpha = linea.alpha * 2), pr, pc, style = Stroke(1.4f * d))
                drawCircle(linea, pr * 0.9f, pc, style = Stroke(0.8f * d))
            }
            TemaId.ESPACIO -> {
                // El sistema solar interior: el Sol asomando a la izquierda, las órbitas de la Tierra y de Marte con el
                // cinturón en medio, la Tierra con su Luna y Marte; de noche, más estrellas y una nebulosa.
                val az = java.util.Random(19)
                if (oscuro) drawCircle(Brush.radialGradient(listOf(Color(0xFFC59BFF).copy(alpha = 0.12f), Color.Transparent), Offset(w * 0.6f, h * 0.2f), w * 0.3f), w * 0.3f, Offset(w * 0.6f, h * 0.2f))
                repeat(if (oscuro) 90 else 40) { drawCircle((if (oscuro) Color.White else Color(0xFF4E5A70)).copy(alpha = 0.2f + 0.4f * az.nextFloat()), (0.5f + az.nextFloat()) * d, Offset(az.nextFloat() * w, az.nextFloat() * h)) }
                val sol = Offset(-w * 0.32f, h * 0.55f)
                drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD27A).copy(alpha = 0.7f), Color(0xFFE8601C).copy(alpha = 0.25f), Color.Transparent), sol, w * 0.4f), w * 0.4f, sol)
                val orbita = (if (oscuro) Color(0xFF8AB4FF) else Color(0xFF1B3A8C)).copy(alpha = 0.25f)
                val rTierra = w * 0.42f; val rMarte = w * 1.23f
                drawCircle(orbita, rTierra, sol, style = Stroke(1f * d))
                drawCircle(orbita, rMarte, sol, style = Stroke(1f * d))
                repeat(60) { val a = -0.35 + az.nextDouble() * 0.7; val rr = w * (0.7f + az.nextFloat() * 0.18f); drawCircle(orbita.copy(alpha = 0.4f), (0.6f + az.nextFloat()) * d, Offset(sol.x + (rr * cos(a)).toFloat(), sol.y + (rr * sin(a)).toFloat())) }
                drawCircle(Color(0xFF4F86B8), 6 * d, Offset(w * 0.1f, h * 0.62f)); drawCircle(Color(0xFF6B8A3A).copy(alpha = 0.7f), 2.4f * d, Offset(w * 0.1f - 1.5f * d, h * 0.62f - 1.5f * d))
                drawCircle(Color(0xFFB0B6C2), 3 * d, Offset(w * 0.32f, h * 0.7f))
                drawCircle(Color(0xFFC1440E), 7 * d, Offset(w * 0.91f, h * 0.56f))
                drawCircle(Color(0xFF8A2E0E), 2 * d, Offset(w * 0.66f, h * 0.64f))
            }
            TemaId.ABISMO -> {
                // Un corte del mar: el cielo y la superficie con su ola, las capas del agua cada vez más oscuras, la nieve
                // marina que cae, el talud con su arrecife a la izquierda y el fondo que se hunde en una fosa a la derecha.
                // De noche, puntos de luz viva en las capas hondas.
                val az = java.util.Random(23)
                val sup = h * 0.06f
                val capas = listOf(Color(0xFF7FC4D8), Color(0xFF3F8FB0), Color(0xFF1E5A7A), Color(0xFF0D3350), Color(0xFF061E33), Color(0xFF020D18))
                capas.forEachIndexed { i, c -> drawRect(c.copy(alpha = if (oscuro) 0.22f + i * 0.06f else 0.16f + i * 0.05f), Offset(0f, sup + (h - sup) * i / capas.size), Size(w, (h - sup) / capas.size + 1f)) }
                val ola = Path(); var x = 0f
                ola.moveTo(0f, sup)
                while (x <= w) { ola.lineTo(x, sup + 2.2f * d * sin(x / (11 * d))); x += 3 * d }
                drawPath(ola, (if (oscuro) Color(0xFF5CF2C6) else Color(0xFF0A5C8C)).copy(alpha = 0.6f), style = Stroke(1.4f * d))
                repeat(70) { drawCircle((if (oscuro) Color(0xFFC4ECE6) else Color(0xFF0C2430)).copy(alpha = 0.12f + 0.2f * az.nextFloat()), (0.4f + 0.8f * az.nextFloat()) * d, Offset(az.nextFloat() * w, sup + az.nextFloat() * (h - sup))) }
                if (oscuro) repeat(24) {
                    val o = Offset(az.nextFloat() * w, h * (0.45f + 0.5f * az.nextFloat()))
                    val col = listOf(Color(0xFF5CF2C6), Color(0xFFE29BFF), Color(0xFF3FA9FF))[it % 3]
                    drawCircle(col.copy(alpha = 0.18f), 4 * d, o); drawCircle(col.copy(alpha = 0.7f), 1 * d, o)
                }
                val fondo = Path().apply {
                    moveTo(0f, h * 0.32f); lineTo(w * 0.1f, h * 0.36f); quadraticTo(w * 0.16f, h * 0.6f, w * 0.26f, h * 0.7f)
                    lineTo(w * 0.62f, h * 0.78f); quadraticTo(w * 0.74f, h * 0.8f, w * 0.8f, h * 0.98f); lineTo(w * 0.9f, h * 0.98f)
                    quadraticTo(w * 0.94f, h * 0.84f, w, h * 0.82f); lineTo(w, h); lineTo(0f, h); close()
                }
                drawPath(fondo, (if (oscuro) Color(0xFF0B1A26) else Color(0xFF6E6250)).copy(alpha = if (oscuro) 0.9f else 0.45f))
                drawPath(fondo, tinta, style = Stroke(1f * d))
                listOf(0.03f, 0.06f, 0.09f).forEach { fx -> drawLine(Color(0xFFC0563F).copy(alpha = 0.6f), Offset(w * fx, h * 0.33f + fx * h * 0.3f), Offset(w * fx + 2 * d, h * 0.28f), 1.6f * d) }
                drawPath(Path().apply { moveTo(w * 0.04f, sup); lineTo(w * 0.14f, sup); lineTo(w * 0.12f, sup + 4 * d); lineTo(w * 0.06f, sup + 4 * d); close() }, tinta.copy(alpha = 0.8f))
            }
            TemaId.URSS -> {
                // Rusia de oeste a este: la taiga, los Urales, el Obi y el Yeniséi, el lago Baikal y el Pacífico.
                fun lon(x: Double) = w * (0.07f + 0.86f * ((x - 33.0) / 103.0).toFloat())
                fun lat(y: Double) = h * (0.92f - 0.84f * ((y - 40.0) / 20.0).toFloat())
                val az = java.util.Random(57)
                repeat(60) { val x = az.nextFloat() * w; val y = h * (0.1f + az.nextFloat() * 0.5f); drawPath(Path().apply { moveTo(x - 3 * d, y); lineTo(x, y - 7 * d); lineTo(x + 3 * d, y); close() }, Color(0xFF3F6A3A).copy(alpha = if (oscuro) 0.22f else 0.16f)) }
                for (k in 0 until 6) monte(lon(59.0 + (k % 2) * 1.0), lat(59.0 - k * 2.2), 7 * d, tinta)
                listOf(82.0 to 66.0, 92.0 to 68.0).forEach { (x0, top) -> drawPath(Path().apply { moveTo(lon(x0 - 2), lat(48.0)); cubicTo(lon(x0 + 2), lat(52.0), lon(x0 - 2), lat(56.0), lon(x0 + 1), lat(top)) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(1.6f * d)) }
                drawPath(Path().apply { moveTo(lon(103.8), lat(51.6)); cubicTo(lon(106.5), lat(52.5), lon(108.5), lat(54.0), lon(109.6), lat(55.7)); cubicTo(lon(108.6), lat(55.6), lon(106.0), lat(53.6), lon(103.8), lat(51.6)); close() }, agua.copy(alpha = agua.alpha * 2.4f))
                drawPath(Path().apply { moveTo(w, 0f); lineTo(lon(141.0), 0f); cubicTo(lon(137.0), lat(54.0), lon(134.0), lat(47.0), lon(132.5), lat(42.0)); lineTo(lon(132.5), h); lineTo(w, h); close() }, agua)
                // La estrella de la meta se enciende con los días cumplidos.
                with(TrofeosTema) { estrella(Offset(lon(131.9), lat(43.12) - 14 * d), 5 * d, 2 * d, 5, Brush.linearGradient(listOf(Color(0xFFC1121F).copy(alpha = 0.2f + 0.6f * avance), Color(0xFFC1121F).copy(alpha = 0.2f + 0.6f * avance)))) }
            }
            TemaId.ROMANO -> {
                // La retícula de las centurias; Italia a la izquierda y la Galia a la derecha (el este queda a la izquierda).
                var x = 0f; while (x < w) { drawLine(tinta.copy(alpha = 0.08f), Offset(x, 0f), Offset(x, h), 1f); x += 22 * d }
                // El Tirreno y el golfo de León abajo, con Ostia y Massilia en la costa.
                drawPath(Path().apply { moveTo(0f, h); lineTo(0f, h * 0.7f); cubicTo(w * 0.12f, h * 0.62f, w * 0.2f, h * 0.9f, w * 0.34f, h * 0.82f); cubicTo(w * 0.4f, h * 0.8f, w * 0.44f, h * 0.9f, w * 0.48f, h); close() }, agua)
                // Los Alpes, entre Italia y la Galia.
                for (k in 0 until 5) monte(w * (0.2f + k * 0.04f), h * (0.38f - (k % 2) * 0.05f), 7 * d, tinta)
                // El Ródano sube de Massilia a Lugdunum y sigue como Saona; el Sena pasa por Lutetia hacia el mar del norte.
                drawPath(Path().apply { moveTo(w * 0.36f, h * 0.82f); cubicTo(w * 0.4f, h * 0.7f, w * 0.44f, h * 0.6f, w * 0.48f, h * 0.48f); cubicTo(w * 0.5f, h * 0.36f, w * 0.53f, h * 0.22f, w * 0.5f, 0f) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(1.6f * d))
                drawPath(Path().apply { moveTo(w * 0.62f, h * 0.66f); cubicTo(w * 0.7f, h * 0.52f, w * 0.74f, h * 0.36f, w * 0.8f, h * 0.36f); cubicTo(w * 0.86f, h * 0.36f, w * 0.9f, h * 0.2f, w, h * 0.16f) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(1.6f * d))
            }
            TemaId.URBE -> {
                // El plano del centro: las calles en una trama torcida, el Tíber que baja en curvas con la isla Tiberina,
                // el verde del Gianicolo, la cúpula de San Pedro arriba a la izquierda y las murallas aurelianas, punteadas.
                val az = java.util.Random(2779)
                repeat(46) {
                    val x = az.nextFloat() * w; val y = az.nextFloat() * h; val a = (az.nextFloat() - 0.5f) * 1.2f; val l = (14 + az.nextFloat() * 30) * d
                    drawLine(tinta.copy(alpha = 0.5f), Offset(x, y), Offset(x + l * cos(a), y + l * sin(a)), 1f * d)
                }
                val rio = Path().apply { moveTo(w * 0.36f, 0f); cubicTo(w * 0.26f, h * 0.16f, w * 0.3f, h * 0.3f, w * 0.44f, h * 0.36f); cubicTo(w * 0.52f, h * 0.42f, w * 0.48f, h * 0.56f, w * 0.38f, h * 0.68f); cubicTo(w * 0.32f, h * 0.78f, w * 0.38f, h * 0.9f, w * 0.44f, h) }
                drawPath(rio, agua.copy(alpha = agua.alpha * 2.2f), style = Stroke(9 * d))
                drawOval(cs.surface.copy(alpha = 0.8f), Offset(w * 0.38f - 6 * d, h * 0.68f - 2.5f * d), Size(12 * d, 5 * d))
                drawOval(Color(0xFF4E7A4A).copy(alpha = if (oscuro) 0.18f else 0.14f), Offset(w * 0.02f, h * 0.4f), Size(w * 0.18f, h * 0.32f))
                val cp = Offset(w * 0.1f, h * 0.18f)
                drawArc(tinta.copy(alpha = 0.6f), 180f, 180f, true, Offset(cp.x - 9 * d, cp.y - 6 * d), Size(18 * d, 14 * d))
                drawRect(tinta.copy(alpha = 0.6f), Offset(cp.x - 9 * d, cp.y + 1 * d), Size(18 * d, 4 * d))
                drawRect(tinta.copy(alpha = 0.6f), Offset(cp.x - 1.5f * d, cp.y - 10 * d), Size(3 * d, 4 * d))
                drawPath(Path().apply { moveTo(w * 0.6f, 0f); cubicTo(w * 0.98f, h * 0.2f, w * 0.98f, h * 0.7f, w * 0.62f, h) }, tinta, style = Stroke(1.4f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * d, 3 * d))))
            }
            TemaId.HELENO -> {
                // Tierra de color arena: Macedonia arriba y Asia Menor a la derecha. El Egeo abajo a la izquierda, con sus islas y
                // sus olas, y el mar del sur bajo Tarso; al final, el Éufrates y el Tigris.
                drawRect(Color(0xFFE6D3A6).copy(alpha = if (oscuro) 0.1f else 0.28f), Offset.Zero, size)
                val mar = Path().apply {
                    moveTo(0f, h); lineTo(0f, h * 0.3f); cubicTo(w * 0.1f, h * 0.24f, w * 0.18f, h * 0.3f, w * 0.26f, h * 0.26f)
                    cubicTo(w * 0.3f, h * 0.44f, w * 0.33f, h * 0.62f, w * 0.36f, h * 0.82f); cubicTo(w * 0.48f, h * 0.88f, w * 0.62f, h * 0.8f, w * 0.76f, h * 0.84f); lineTo(w * 0.78f, h); close()
                }
                drawPath(mar, Brush.verticalGradient(listOf(agua.copy(alpha = agua.alpha * 0.7f), agua.copy(alpha = agua.alpha * 1.3f)), h * 0.25f, h))
                listOf(0.08f to 0.52f, 0.16f to 0.64f, 0.22f to 0.44f, 0.11f to 0.82f, 0.27f to 0.7f, 0.2f to 0.92f).forEachIndexed { i, (x, y) ->
                    drawOval(Color(0xFFE6D3A6).copy(alpha = if (oscuro) 0.3f else 0.7f), Offset(w * x - 6 * d, h * y - 3 * d), Size((10 + (i % 3) * 4) * d, 6 * d))
                }
                val az = java.util.Random(7)
                repeat(12) { val c = Offset(w * (0.02f + az.nextFloat() * 0.26f), h * (0.4f + az.nextFloat() * 0.56f)); drawArc(Color.White.copy(alpha = 0.4f), 200f, 140f, false, Offset(c.x - 5 * d, c.y - 3 * d), Size(10 * d, 6 * d), style = Stroke(1f * d)) }
                // Los montes Tauro, entre Gordio y Tarso.
                for (k in 0 until 6) monte(w * (0.5f + k * 0.035f), h * (0.56f + (k % 2) * 0.05f), 7 * d, tinta)
                drawPath(Path().apply { moveTo(w * 0.84f, 0f); cubicTo(w * 0.87f, h * 0.24f, w * 0.89f, h * 0.38f, w * 0.91f, h * 0.48f); cubicTo(w * 0.93f, h * 0.62f, w * 0.95f, h * 0.8f, w * 0.97f, h) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(1.8f * d))
                drawPath(Path().apply { moveTo(w * 0.93f, 0f); cubicTo(w * 0.95f, h * 0.3f, w * 0.97f, h * 0.6f, w * 0.99f, h) }, agua.copy(alpha = agua.alpha * 1.6f), style = Stroke(1.4f * d))
            }
            TemaId.EGIPTO -> {
                // Las Dos Tierras de norte a sur: el desierto rojo (deshret) a los lados, la tierra negra (kemet) a lo largo
                // del Nilo, el abanico del Delta y el mar arriba, las pirámides al oeste de Menfis y la primera catarata abajo.
                drawRect(Color(0xFFE2C48A).copy(alpha = if (oscuro) 0.1f else 0.3f), Offset.Zero, size)
                val curso = listOf(0.53f to 0.12f, 0.5f to 0.22f, 0.47f to 0.36f, 0.56f to 0.48f, 0.64f to 0.62f, 0.6f to 0.76f, 0.56f to 0.9f, 0.55f to 1f)
                val rio = Path().apply { curso.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(w * x, h * y) else lineTo(w * x, h * y) } }
                drawPath(rio, Color(0xFF5E7A3A).copy(alpha = if (oscuro) 0.22f else 0.32f), style = Stroke(26 * d, cap = StrokeCap.Round))
                drawPath(rio, agua.copy(alpha = agua.alpha * 2.2f), style = Stroke(4.5f * d, cap = StrokeCap.Round))
                val cima = Offset(w * 0.53f, h * 0.12f)
                listOf(-0.2f, -0.1f, 0f, 0.1f, 0.2f).forEach { dx -> drawLine(agua.copy(alpha = agua.alpha * 1.8f), cima, Offset(cima.x + w * dx, h * 0.02f), 1.8f * d) }
                drawPath(Path().apply { moveTo(cima.x, cima.y); lineTo(cima.x - w * 0.2f, h * 0.02f); lineTo(cima.x + w * 0.2f, h * 0.02f); close() }, Color(0xFF5E7A3A).copy(alpha = if (oscuro) 0.15f else 0.22f))
                drawRect(agua.copy(alpha = agua.alpha * 1.2f), Offset.Zero, Size(w, h * 0.022f))
                // Las pirámides, al oeste del río.
                listOf(0.31f to 0.25f, 0.35f to 0.245f, 0.385f to 0.26f).forEachIndexed { i, (x, y) ->
                    val s = (8 - i * 1.6f) * d
                    drawPath(Path().apply { moveTo(w * x - s, h * y); lineTo(w * x, h * y - s * 1.1f); lineTo(w * x + s, h * y); close() }, tinta.copy(alpha = 0.6f))
                }
                // La primera catarata: piedras en el agua.
                val az = java.util.Random(31)
                repeat(9) { drawCircle(tinta.copy(alpha = 0.5f), (1.2f + az.nextFloat() * 1.4f) * d, Offset(w * (0.555f + (az.nextFloat() - 0.5f) * 0.08f), h * (0.93f + az.nextFloat() * 0.05f))) }
                // Palmeras en las orillas y dunas en el desierto.
                for (k in 0 until 6) {
                    val x = w * (if (k % 2 == 0) 0.41f else 0.7f); val y = h * (0.32f + k * 0.1f)
                    drawLine(tinta, Offset(x, y), Offset(x, y - 7 * d), 1f * d)
                    listOf(-1f, -0.4f, 0.4f, 1f).forEach { a -> drawLine(tinta, Offset(x, y - 7 * d), Offset(x + a * 4 * d, y - 7 * d + kotlin.math.abs(a) * 2 * d), 0.9f * d) }
                }
                for (k in 0 until 6) monte(w * (0.12f + (k % 2) * 0.74f), h * (0.42f + k * 0.09f), 6 * d, tinta)
            }
            TemaId.MEDIEVAL -> {
                // Pergamino con el mar Cantábrico arriba, el Atlántico a la derecha, los Pirineos al principio, los montes de la meseta
                // y la rosa de los vientos.
                drawPath(Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h * 0.12f); cubicTo(w * 0.7f, h * 0.06f, w * 0.5f, h * 0.18f, w * 0.3f, h * 0.1f); cubicTo(w * 0.18f, h * 0.05f, w * 0.08f, h * 0.14f, 0f, h * 0.1f); close() }, agua)
                drawPath(Path().apply { moveTo(w, 0f); lineTo(w * 0.95f, 0f); cubicTo(w * 0.97f, h * 0.3f, w * 0.94f, h * 0.6f, w * 0.96f, h); lineTo(w, h); close() }, agua)
                for (k in 0 until 4) monte(w * (0.03f + k * 0.035f), h * (0.3f - (k % 2) * 0.04f), 6 * d, tinta)
                for (k in 0 until 4) { val x = w * (0.2f + k * 0.18f); drawArc(tinta, 200f, 140f, false, Offset(x, h * 0.8f), Size(26 * d, 18 * d), style = Stroke(1.2f * d)) }
                val c = Offset(w * 0.86f, h * 0.8f); val r = 12 * d
                for (k in 0 until 8) { val a = k * PI / 4; val rr = if (k % 2 == 0) r else r * 0.5f; drawLine(tinta.copy(alpha = 0.4f), c, Offset(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat()), 1.2f * d) }
            }
            TemaId.TEMPLARIO -> {
                // La tierra de frontera en arena, el mar a la izquierda con sus olas, las dunas, el oasis del pozo con sus palmas,
                // el río que se cruza por el vado de piedras rojas, las montañas de la derecha y, en cada etapa ganada, una
                // atalaya que se enciende (el fuego de señales que pasaba la noticia de torre en torre).
                drawRect(Color(0xFFE2C98F).copy(alpha = if (oscuro) 0.1f else 0.3f), Offset.Zero, size)
                drawPath(Path().apply { moveTo(0f, 0f); lineTo(w * 0.06f, 0f); cubicTo(w * 0.03f, h * 0.3f, w * 0.07f, h * 0.55f, w * 0.03f, h * 0.8f); lineTo(w * 0.04f, h); lineTo(0f, h); close() }, agua)
                for (k in 0 until 6) drawArc(Color.White.copy(alpha = 0.35f), 200f, 140f, false, Offset(w * 0.005f, h * (0.08f + k * 0.15f)), Size(8 * d, 5 * d), style = Stroke(1f * d))
                val az = java.util.Random(31)
                repeat(10) { val x = w * (0.12f + az.nextFloat() * 0.6f); val y = h * (0.12f + az.nextFloat() * 0.8f)
                    drawArc(tinta, 200f, 140f, false, Offset(x, y), Size((18 + az.nextInt(12)) * d, 9 * d), style = Stroke(1f * d)) }
                drawPath(Path().apply { moveTo(w * 0.66f, 0f); cubicTo(w * 0.6f, h * 0.3f, w * 0.66f, h * 0.55f, w * 0.58f, h * 0.8f); cubicTo(w * 0.56f, h * 0.9f, w * 0.6f, h * 0.96f, w * 0.57f, h) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(2.2f * d))
                listOf(-4f, 0f, 4f).forEach { dx -> drawCircle(Color(0xFFA8432C).copy(alpha = 0.6f), 1.6f * d, Offset(w * 0.6f + dx * d, h * 0.72f)) }
                val pozo = Offset(w * 0.48f, h * 0.44f)
                drawOval(agua.copy(alpha = agua.alpha * 2), Offset(pozo.x - 8 * d, pozo.y + 4 * d), Size(16 * d, 5 * d))
                listOf(-1f, 1f).forEach { s -> val t0 = Offset(pozo.x + s * 9 * d, pozo.y + 6 * d); val t1 = Offset(pozo.x + s * 11 * d, pozo.y - 6 * d)
                    drawLine(tinta.copy(alpha = 0.6f), t0, t1, 1.2f * d)
                    for (k in 0 until 4) { val a = (-PI / 2 + (k - 1.5) * 0.7).toFloat(); drawLine(Color(0xFF5E7A3A).copy(alpha = 0.6f), t1, t1 + Offset(6 * d * cos(a), 4 * d * sin(a) + 2 * d), 1.2f * d) } }
                for (k in 0 until 6) monte(w * (0.72f + k * 0.045f), h * (0.2f + (k % 2) * 0.06f), 8 * d, tinta)
                puntos(tema).forEachIndexed { i, q ->
                    if (i in 1..5 && avance * 7f > i) { val p0 = Offset(w * q.x, h * q.y - 14 * d)
                        drawCircle(Brush.radialGradient(listOf(Color(0xFFF0A040).copy(alpha = 0.5f), Color.Transparent), p0, 8 * d), 8 * d, p0) }
                }
            }
            TemaId.GOTICO -> {
                // La catedral, vista de lado, se levanta con los días cumplidos: la silueta completa tenue y lo construido con más fuerza.
                val silueta = catedral(w, h)
                drawPath(silueta, tinta.copy(alpha = 0.08f))
                drawPath(silueta, tinta.copy(alpha = 0.25f), style = Stroke(1f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 3 * d))))
                val alto = h * avance.coerceIn(0f, 1f)
                clipRect(0f, h - alto, w, h) { drawPath(silueta, cs.primary.copy(alpha = if (oscuro) 0.16f else 0.14f)) }
                drawLine(tinta.copy(alpha = 0.4f), Offset(0f, h * 0.97f), Offset(w, h * 0.97f), 1f * d)
            }
            TemaId.ARCANO -> {
                // La costa en una carta de sondeos: el mar a la derecha con sus cifras de profundidad, la orilla con el rayado
                // de los acantilados, el pueblo al principio, bancos de niebla y, al final, el faro, cuyo haz crece con el avance.
                val mar = (if (oscuro) Color(0xFF2E5A4E) else Color(0xFF5E8A7E)).copy(alpha = if (oscuro) 0.3f else 0.2f)
                val orilla = Path().apply {
                    moveTo(w * 0.36f, h); cubicTo(w * 0.5f, h * 0.84f, w * 0.62f, h * 0.7f, w * 0.66f, h * 0.55f)
                    cubicTo(w * 0.7f, h * 0.42f, w * 0.78f, h * 0.3f, w * 0.88f, h * 0.2f); cubicTo(w * 0.94f, h * 0.12f, w * 0.97f, h * 0.06f, w, 0f)
                }
                drawPath(Path().apply { addPath(orilla); lineTo(w, h); close() }, mar)
                drawPath(orilla, tinta.copy(alpha = 0.5f), style = Stroke(1.2f * d))
                val m = PathMeasure().apply { setPath(orilla, false) }
                var s = 0f; while (s < m.length) { val q = m.getPosition(s); drawLine(tinta.copy(alpha = 0.35f), q, Offset(q.x - 6 * d, q.y + 3 * d), 0.8f * d); s += 7 * d }
                val az = java.util.Random(26)
                repeat(16) { val x = w * (0.72f + az.nextFloat() * 0.26f); val y = h * (0.35f + az.nextFloat() * 0.6f); drawCircle(tinta.copy(alpha = 0.3f), 0.9f * d, Offset(x, y)) }
                // El pueblo: unas casitas de tejado en punta.
                for (k in 0 until 4) { val x = w * (0.03f + k * 0.045f); val y = h * (0.97f - (k % 2) * 0.03f)
                    drawPath(Path().apply { moveTo(x, y); lineTo(x, y - 6 * d); lineTo(x + 4 * d, y - 10 * d); lineTo(x + 8 * d, y - 6 * d); lineTo(x + 8 * d, y) }, tinta, style = Stroke(1f * d)) }
                // Niebla.
                listOf(0.3f, 0.52f, 0.78f).forEach { f -> drawRect(Brush.verticalGradient(listOf(Color.Transparent, (if (oscuro) Color(0xFF9AB0A6) else Color.White).copy(alpha = if (oscuro) 0.08f else 0.3f), Color.Transparent), h * f - 10 * d, h * f + 10 * d),
                    Offset(0f, h * f - 10 * d), Size(w, 20 * d)) }
                // El faro y su haz.
                val f = Offset(w * 0.9f, h * 0.1f)
                val largo = w * 0.15f + w * 0.6f * avance.coerceIn(0f, 1f)
                drawPath(Path().apply { moveTo(f.x, f.y); lineTo(f.x - largo, f.y + largo * 0.18f); lineTo(f.x - largo, f.y + largo * 0.42f); close() },
                    Brush.radialGradient(listOf(cs.primary.copy(alpha = if (oscuro) 0.22f else 0.12f), Color.Transparent), f, largo))
                drawPath(Path().apply { moveTo(f.x - 3 * d, f.y + 14 * d); lineTo(f.x - 2 * d, f.y); lineTo(f.x + 2 * d, f.y); lineTo(f.x + 3 * d, f.y + 14 * d); close() }, tinta.copy(alpha = 0.6f))
                drawCircle(cs.primary.copy(alpha = 0.7f), 2 * d, f)
            }
            TemaId.RENACIMIENTO -> {
                // Las hojas del boceto y del estudio a la izquierda, el cuadro sobre el caballete (primero la retícula del boceto,
                // luego el color que va entrando) y la paleta a la derecha.
                val hoja = Color(0xFFF2E6C8).copy(alpha = if (oscuro) 0.08f else 0.5f)
                listOf(Rect(w * 0.02f, h * 0.48f, w * 0.15f, h * 0.9f), Rect(w * 0.13f, h * 0.1f, w * 0.26f, h * 0.5f)).forEach { r ->
                    drawRect(hoja, r.topLeft, r.size); drawRect(tinta.copy(alpha = 0.3f), r.topLeft, r.size, style = Stroke(0.8f * d))
                    for (k in 1..4) drawLine(Color(0xFF8C3B1E).copy(alpha = 0.18f), Offset(r.left + r.width * 0.15f, r.top + r.height * k / 5), Offset(r.right - r.width * (0.1f + 0.1f * (k % 2)), r.top + r.height * k / 5 - 3 * d), 0.8f * d)
                }
                val r = Rect(w * 0.3f, h * 0.08f, w * 0.7f, h * 0.92f)
                drawLine(Color(0xFF9A7A50).copy(alpha = 0.35f), Offset(r.left + r.width * 0.2f, r.bottom), Offset(r.left + r.width * 0.1f, h), 2 * d)
                drawLine(Color(0xFF9A7A50).copy(alpha = 0.35f), Offset(r.right - r.width * 0.2f, r.bottom), Offset(r.right - r.width * 0.1f, h), 2 * d)
                drawRect(Color(0xFF9A7A50).copy(alpha = 0.35f), r.topLeft, r.size, style = Stroke(3 * d))
                for (k in 1..3) { drawLine(tinta.copy(alpha = 0.18f), Offset(r.left + r.width * k / 4, r.top), Offset(r.left + r.width * k / 4, r.bottom), 1f); drawLine(tinta.copy(alpha = 0.18f), Offset(r.left, r.top + r.height * k / 4), Offset(r.right, r.top + r.height * k / 4), 1f) }
                val colores = listOf(Color(0xFF8C3B1E), Color(0xFF2F4F6F), Color(0xFF9C7A2E), Color(0xFF5E7A4A))
                val manchas = (avance * 8).toInt()
                val az = java.util.Random(3)
                clipRect(r.left, r.top, r.right, r.bottom) {
                    repeat(manchas) { k -> val c = Offset(r.left + az.nextFloat() * r.width, r.top + az.nextFloat() * r.height); drawCircle(colores[k % 4].copy(alpha = 0.22f), r.width * (0.12f + az.nextFloat() * 0.1f), c) }
                }
                // La paleta con su agujero para el pulgar y las manchas de pintura.
                val pc = Offset(w * 0.86f, h * 0.52f); val pw = minOf(w * 0.18f, h * 0.7f)
                drawOval(Color(0xFFB08A5A).copy(alpha = if (oscuro) 0.2f else 0.35f), Offset(pc.x - pw / 2, pc.y - pw * 0.32f), Size(pw, pw * 0.64f))
                drawCircle(cs.surface, pw * 0.06f, Offset(pc.x - pw * 0.26f, pc.y + pw * 0.08f))
                colores.forEachIndexed { k, c -> drawCircle(c.copy(alpha = 0.5f), pw * 0.06f, Offset(pc.x - pw * 0.12f + k * pw * 0.12f, pc.y - pw * 0.14f + (k % 2) * pw * 0.06f)) }
            }
            TemaId.PIRATA -> {
                // La carta náutica: el mar en verde de carta, la cuadrícula de grados, las líneas de rumbo de una rosa en la
                // esquina, una isla bajo cada parada con su orilla de arena y, sobre la derrota, el balandro que avanza.
                drawRect(Color(0xFF2F5D62).copy(alpha = if (oscuro) 0.22f else 0.12f), Offset.Zero, Size(w, h))
                var gx = w / 8; while (gx < w) { drawLine(tinta.copy(alpha = 0.12f), Offset(gx, 0f), Offset(gx, h), 0.6f * d); gx += maxOf(w / 8, 1f) }
                var gy = h / 5; while (gy < h) { drawLine(tinta.copy(alpha = 0.12f), Offset(0f, gy), Offset(w, gy), 0.6f * d); gy += maxOf(h / 5, 1f) }
                val rosa = Offset(w * 0.9f, h * 0.15f)
                for (k in 0 until 16) { val a = k * PI / 8; drawLine(tinta.copy(alpha = if (k % 4 == 0) 0.3f else 0.14f), rosa, Offset(rosa.x + (w * cos(a)).toFloat(), rosa.y + (w * sin(a)).toFloat()), 0.6f * d) }
                for (k in 0 until 8) {
                    val a = -PI / 2 + k * PI / 4; val rr = (if (k % 2 == 0) 13f else 8f) * d
                    drawLine(tinta.copy(alpha = 0.55f), rosa, Offset(rosa.x + (rr * cos(a)).toFloat(), rosa.y + (rr * sin(a)).toFloat()), 1.4f * d)
                }
                val pts = puntos(tema).map { Offset(it.x * w, it.y * h) }
                val arena = Color(0xFFE6D3A6).copy(alpha = if (oscuro) 0.3f else 0.75f)
                val monte = Color(0xFF5E7A4A).copy(alpha = if (oscuro) 0.35f else 0.4f)
                pts.forEachIndexed { i, p ->
                    val an = (22f + (i * 7) % 12) * d; val al = an * 0.55f
                    drawOval(Color.White.copy(alpha = if (oscuro) 0.06f else 0.4f), Offset(p.x - an * 0.62f, p.y - al * 0.2f), Size(an * 1.24f, al * 0.9f), style = Stroke(1 * d))
                    drawOval(arena, Offset(p.x - an / 2, p.y - al * 0.1f), Size(an, al * 0.7f))
                    drawOval(monte, Offset(p.x - an * 0.3f, p.y - al * 0.02f), Size(an * 0.5f, al * 0.4f))
                }
                // Olas sueltas, a pluma.
                val az = java.util.Random(1718)
                repeat(14) { val o = Offset(az.nextFloat() * w, az.nextFloat() * h); drawArc(tinta.copy(alpha = 0.3f), 200f, 140f, false, Offset(o.x - 5 * d, o.y - 2 * d), Size(10 * d, 5 * d), style = Stroke(0.9f * d)) }
                // El balandro sobre la derrota, entre la parada cumplida y la siguiente.
                val f = (avance.coerceIn(0f, 1f) * 6f); val i0 = f.toInt().coerceAtMost(5); val t = f - i0
                val b = Offset(pts[i0].x + (pts[i0 + 1].x - pts[i0].x) * t, pts[i0].y + (pts[i0 + 1].y - pts[i0].y) * t - 14 * d)
                val casco = Color(0xFF4A3424).copy(alpha = if (oscuro) 0.6f else 0.8f)
                drawPath(Path().apply { moveTo(b.x - 9 * d, b.y); lineTo(b.x + 9 * d, b.y); lineTo(b.x + 6 * d, b.y + 4 * d); lineTo(b.x - 7 * d, b.y + 4 * d); close() }, casco)
                drawLine(casco, Offset(b.x, b.y), Offset(b.x, b.y - 15 * d), 1 * d)
                drawPath(Path().apply { moveTo(b.x + 1 * d, b.y - 14 * d); quadraticTo(b.x + 9 * d, b.y - 8 * d, b.x + 1 * d, b.y - 2 * d); close() }, Color(0xFFF1E8CF).copy(alpha = if (oscuro) 0.5f else 0.9f))
                drawPath(Path().apply { moveTo(b.x - 1 * d, b.y - 12 * d); lineTo(b.x - 8 * d, b.y - 2 * d); lineTo(b.x - 1 * d, b.y - 2 * d); close() }, Color(0xFFF1E8CF).copy(alpha = if (oscuro) 0.4f else 0.8f))
                drawRect(Color(0xFF1B1714), Offset(b.x, b.y - 18 * d), Size(5 * d, 3 * d))
            }
            TemaId.NAPOLEON -> {
                // El canal de la Mancha junto a Boulogne, el Rin y el Danubio, y las fronteras punteadas del teatro de operaciones.
                drawPath(Path().apply { moveTo(0f, 0f); lineTo(w * 0.14f, 0f); cubicTo(w * 0.1f, h * 0.08f, w * 0.05f, h * 0.12f, 0f, h * 0.18f); close() }, agua)
                drawPath(Path().apply { moveTo(w * 0.24f, 0f); cubicTo(w * 0.2f, h * 0.3f, w * 0.3f, h * 0.6f, w * 0.22f, h) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(2.4f * d))
                drawPath(Path().apply { moveTo(w * 0.3f, h * 0.9f); cubicTo(w * 0.5f, h * 0.8f, w * 0.6f, h * 0.95f, w * 0.8f, h * 0.78f); lineTo(w, h * 0.72f) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(2.4f * d))
                drawPath(Path().apply { moveTo(w * 0.1f, h * 0.05f); lineTo(w * 0.4f, h * 0.15f); lineTo(w * 0.62f, h * 0.05f); lineTo(w * 0.95f, h * 0.2f) }, tinta, style = Stroke(1f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * d, 2 * d, 1 * d, 2 * d))))
                // Los altos de Pratzen, junto a Austerlitz.
                for (k in 0 until 3) monte(w * (0.86f + k * 0.035f), h * 0.7f, 6 * d, tinta)
            }
            TemaId.DECO -> {
                // El Atlántico de un cartel de naviera: olas en abanico, las costas de Inglaterra, Irlanda y Francia a la
                // derecha, la de América a la izquierda con los rascacielos de Nueva York, y un sol de rayos que sale del mar.
                val oro = Color(0xFFD4AF37).copy(alpha = if (oscuro) 0.16f else 0.2f)
                val sol = Offset(w * 0.5f, h * 1.02f)
                for (i in 0 until 15) { val a = PI + (i + 0.5) * PI / 15; drawLine(oro, sol, Offset(sol.x + (w * 0.7f * cos(a)).toFloat(), sol.y + (w * 0.7f * sin(a)).toFloat()), (if (i % 2 == 0) 2.4f else 1f) * d) }
                var y = h * 0.12f
                while (y < h) {
                    var x = if (((y / (h * 0.12f)).toInt()) % 2 == 0) 0f else 14 * d
                    while (x < w) { drawArc(agua, 180f, 180f, false, Offset(x, y), Size(28 * d, 14 * d), style = Stroke(1f * d)); x += 28 * d }
                    y += maxOf(h * 0.12f, 1f)
                }
                val costa = tinta.copy(alpha = 0.6f)
                drawPath(Path().apply { moveTo(w * 0.78f, h * 0.05f); cubicTo(w * 0.86f, h * 0.02f, w * 0.9f, h * 0.18f, w * 0.88f, h * 0.36f); cubicTo(w * 0.84f, h * 0.4f, w * 0.79f, h * 0.36f, w * 0.8f, h * 0.24f); close() }, costa)
                drawPath(Path().apply { moveTo(w * 0.6f, h * 0.14f); cubicTo(w * 0.66f, h * 0.1f, w * 0.72f, h * 0.18f, w * 0.7f, h * 0.28f); cubicTo(w * 0.66f, h * 0.34f, w * 0.6f, h * 0.3f, w * 0.6f, h * 0.14f); close() }, costa)
                drawPath(Path().apply { moveTo(w, h * 0.42f); lineTo(w * 0.85f, h * 0.48f); cubicTo(w * 0.78f, h * 0.6f, w * 0.86f, h * 0.8f, w, h * 0.86f); close() }, costa)
                drawPath(Path().apply { moveTo(0f, h * 0.2f); cubicTo(w * 0.1f, h * 0.3f, w * 0.14f, h * 0.5f, w * 0.12f, h * 0.7f); cubicTo(w * 0.1f, h * 0.85f, w * 0.06f, h * 0.95f, 0f, h); close() }, costa)
                listOf(0.02f to 0.12f, 0.045f to 0.2f, 0.07f to 0.15f).forEach { (fx, fa) ->
                    val x = w * fx; val a = h * fa; val an = w * 0.02f; val pie = h * 0.74f
                    drawRect(costa, Offset(x - an, pie - a * 0.6f), Size(2 * an, a * 0.6f)); drawRect(costa, Offset(x - an * 0.6f, pie - a * 0.85f), Size(1.2f * an, a * 0.25f))
                    drawLine(costa, Offset(x, pie - a * 0.85f), Offset(x, pie - a), 1f * d)
                }
            }
            TemaId.REVOLUCION -> {
                // París: el Sena en curva, la cuadrícula de las secciones y la silueta de la Bastilla junto a su jornada.
                var x = 0f; while (x < w) { drawLine(tinta.copy(alpha = 0.1f), Offset(x, 0f), Offset(x, h), 1f); x += 18 * d }
                var y = 0f; while (y < h) { drawLine(tinta.copy(alpha = 0.1f), Offset(0f, y), Offset(w, y), 1f); y += 18 * d }
                drawPath(Path().apply { moveTo(0f, h * 0.48f); cubicTo(w * 0.25f, h * 0.2f, w * 0.45f, h * 0.85f, w * 0.7f, h * 0.5f); cubicTo(w * 0.82f, h * 0.32f, w * 0.9f, h * 0.5f, w, h * 0.44f) },
                    agua.copy(alpha = agua.alpha * 2.2f), style = Stroke(5 * d, cap = StrokeCap.Round))
                val b = Offset(w * 0.33f, h * 0.22f); val muro = tinta.copy(alpha = 0.3f)
                drawRect(muro, Offset(b.x - 16 * d, b.y - 8 * d), Size(32 * d, 12 * d))
                for (k in 0 until 4) { val tx = b.x - 16 * d + k * 10.6f * d; drawRect(muro, Offset(tx - 2.5f * d, b.y - 12 * d), Size(5 * d, 16 * d)); drawArc(muro, 180f, 180f, true, Offset(tx - 2.5f * d, b.y - 14.5f * d), Size(5 * d, 5 * d)) }
            }
        }
    }

    /**
     * La catedral vista de lado: el ábside a la izquierda, la nave con sus pináculos (de donde salen los arbotantes), el
     * hastial del crucero con su rosetón y la torre del campanario con su aguja a la derecha. Cada parada cae en su parte.
     */
    private fun catedral(w: Float, h: Float): Path = Path().apply {
        val b = h * 0.97f
        moveTo(w * 0.04f, b); lineTo(w * 0.04f, h * 0.62f); lineTo(w * 0.1f, h * 0.48f); lineTo(w * 0.14f, h * 0.48f)
        lineTo(w * 0.14f, h * 0.34f); lineTo(w * 0.57f, h * 0.34f); lineTo(w * 0.62f, h * 0.2f); lineTo(w * 0.67f, h * 0.34f)
        lineTo(w * 0.7f, h * 0.34f); lineTo(w * 0.7f, h * 0.16f); lineTo(w * 0.77f, h * 0.02f); lineTo(w * 0.84f, h * 0.16f); lineTo(w * 0.84f, b); close()
        // Los pináculos de los contrafuertes, que asoman sobre el tejado.
        for (k in 0 until 5) { val x = w * (0.17f + k * 0.09f); moveTo(x - w * 0.008f, b); lineTo(x - w * 0.008f, h * 0.3f); lineTo(x, h * 0.22f); lineTo(x + w * 0.008f, h * 0.3f); lineTo(x + w * 0.008f, b); close() }
        // El rosetón del crucero y los vanos del campanario.
        addOval(Rect(Offset(w * 0.62f, h * 0.54f), h * 0.1f))
        addRect(Rect(w * 0.75f, h * 0.2f, w * 0.79f, h * 0.3f))
    }

    // ================================================================== rutas

    fun DrawScope.rutaMas(tema: TemaId, cs: ColorScheme, p: Path) {
        val d = density
        when (tema) {
            // Doble filete de oro con puntos de lazo.
            TemaId.ANDALUS -> { drawPath(p, Color(0xFFC9A04A).copy(alpha = 0.7f), style = Stroke(4 * d)); drawPath(p, cs.surface, style = Stroke(2 * d)); drawPath(p, Color(0xFF1E6B5A), style = Stroke(1.4f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * d, 5 * d)))) }
            // El sacbé: la calzada blanca de estuco, alta sobre la selva, con sus bordes de piedra.
            TemaId.MESO -> { drawPath(p, Color(0xFF6E5A3E).copy(alpha = 0.55f), style = Stroke(8 * d)); drawPath(p, Color(0xFFF3EEDF), style = Stroke(6 * d))
                drawPath(p, Color(0xFFA3301E).copy(alpha = 0.45f), style = Stroke(1 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * d, 6 * d)))) }
            // La huella de la caravana en la arena: puntos dobles.
            TemaId.SEDA -> { drawPath(p, Color(0xFFD9B57A).copy(alpha = 0.6f), style = Stroke(5 * d)); drawPath(p, Color(0xFF8E1B1B).copy(alpha = 0.8f), style = Stroke(1.6f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 5 * d)))) }
            // El camino real de tierra apisonada, con la hilera de pinos que lo bordeaba.
            TemaId.EDO -> { drawPath(p, Color(0xFFB8A27A).copy(alpha = 0.65f), style = Stroke(5 * d)); drawPath(p, Color(0xFF3B5A3A).copy(alpha = 0.85f), style = Stroke(2.4f * d, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f * d, 7 * d)))) }
            // El camino de las postas, en azul de cielo.
            TemaId.MONGOL -> drawPath(p, Color(0xFF2E6DB4).copy(alpha = 0.8f), style = Stroke(2 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8 * d, 4 * d))))
            // Las rodadas de las carretas: dos líneas paralelas.
            TemaId.COLONOS -> { drawPath(p, Color(0xFF8A5A2B).copy(alpha = 0.5f), style = Stroke(6 * d)); drawPath(p, cs.surface.copy(alpha = 0.8f), style = Stroke(3 * d)) }
            // Un trazo de lápiz de naturalista.
            TemaId.BOTANICO -> drawPath(p, Color(0xFF6E5530).copy(alpha = 0.7f), style = Stroke(1.4f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * d, 2 * d, 1 * d, 2 * d))))
            // Una calle: asfalto gris y la línea discontinua ámbar al centro.
            TemaId.RUINAS -> { drawPath(p, Color(0xFF55594F).copy(alpha = 0.55f), style = Stroke(6 * d)); drawPath(p, Color(0xFFE0B040).copy(alpha = 0.85f), style = Stroke(1.4f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * d, 5 * d)))) }
            // Rayado de bastón de caramelo.
            TemaId.NAVIDAD -> { drawPath(p, Color.White, style = Stroke(5 * d)); drawPath(p, Color(0xFFB3122E), style = Stroke(5 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * d, 5 * d)))) }
            // La línea roja del mapa de la travesía.
            TemaId.ANTIGUAS -> drawPath(p, Color(0xFF7A1A12).copy(alpha = 0.8f), style = Stroke(1.8f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * d, 3 * d))))
            // Una hilera de puntos de ocre, como las que se pintaban en la pared.
            TemaId.CUEVA -> drawPath(p, Color(0xFFA2401F).copy(alpha = 0.85f), style = Stroke(2.6f * d, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f * d, 5 * d))))
            // La línea roja del trámite, como cinta de señalización.
            TemaId.DISTOPIA -> { drawPath(p, Color(0xFF141414).copy(alpha = 0.35f), style = Stroke(4 * d)); drawPath(p, Color(0xFFC8102E), style = Stroke(2 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10 * d, 4 * d)))) }
            // El enlace de la traza: la línea cian y los paquetes que viajan de nodo en nodo.
            TemaId.CIBER -> {
                val cian = Color(0xFF00E5FF)
                drawPath(p, cian.copy(alpha = 0.16f), style = Stroke(6 * d))
                drawPath(p, cian.copy(alpha = 0.75f), style = Stroke(1 * d))
                aLoLargo(p, 15 * d) { o, _ -> drawRect(cian, Offset(o.x - 1.6f * d, o.y - 1.6f * d), Size(3.2f * d, 3.2f * d)) }
            }
            // Las escaleras de servicio: un tubo ámbar con su halo y peldaños a lo largo.
            TemaId.NEON -> {
                val ambar = Color(0xFFFFB238)
                drawPath(p, ambar.copy(alpha = 0.18f), style = Stroke(7 * d, cap = StrokeCap.Round))
                drawPath(p, ambar.copy(alpha = 0.85f), style = Stroke(1.3f * d, cap = StrokeCap.Round))
                aLoLargo(p, 10 * d) { o, dir -> val n = Offset(-dir.y, dir.x) * (4 * d); drawLine(ambar.copy(alpha = 0.6f), o - n, o + n, 1 * d) }
            }
            // La franja de los pasillos: el trazo verde azulado (fósforo de noche) con las marcas amarillas del piso.
            TemaId.REFUGIO -> {
                val oscuro = cs.background.luminance() < 0.5f
                drawPath(p, (if (oscuro) Color(0xFF3DFF7F) else Color(0xFF1C6E6A)).copy(alpha = 0.6f), style = Stroke(5 * d))
                drawPath(p, if (oscuro) Color(0xFFFFB23F) else Color(0xFFE8B83A), style = Stroke(2 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * d, 4 * d))))
            }
            // Las flechas de avance del mapa táctico: el trazo carmesí y puntas a lo largo del camino.
            TemaId.ORDEN -> {
                val carmesi = Color(0xFF9E1B22)
                drawPath(p, carmesi.copy(alpha = 0.25f), style = Stroke(7 * d))
                drawPath(p, carmesi, style = Stroke(2 * d))
                aLoLargo(p, 24 * d, 18 * d) { o, t ->
                    val n = Offset(-t.y, t.x); val s = 5 * d
                    drawPath(Path().apply { moveTo(o.x + t.x * s, o.y + t.y * s); lineTo(o.x - t.x * s + n.x * s, o.y - t.y * s + n.y * s); lineTo(o.x - t.x * s * 0.4f, o.y - t.y * s * 0.4f); lineTo(o.x - t.x * s - n.x * s, o.y - t.y * s - n.y * s); close() }, carmesi)
                }
            }
            // La vía: las traviesas de madera y, encima, los dos carriles de hierro (un trazo ancho con el centro claro).
            TemaId.VAPOR -> {
                val hierro = Color(0xFF3B3A37)
                aLoLargo(p, 4.5f * d) { o, t -> val n = Offset(-t.y, t.x) * (3.6f * d); drawLine(Color(0xFF7A5A3A).copy(alpha = 0.8f), o - n, o + n, 1.6f * d) }
                drawPath(p, hierro, style = Stroke(4.4f * d))
                drawPath(p, cs.surface, style = Stroke(2.4f * d))
            }
            // El recorrido de la pipeta: punteado fino violeta, con gotitas cian a lo largo.
            TemaId.LABORATORIO -> {
                drawPath(p, Color(0xFF5E4FD1).copy(alpha = 0.7f), style = Stroke(1.2f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 3 * d))))
                aLoLargo(p, 26 * d) { o, _ -> drawPath(Path().apply { moveTo(o.x, o.y - 3.5f * d); quadraticTo(o.x + 2.6f * d, o.y, o.x, o.y + 2 * d); quadraticTo(o.x - 2.6f * d, o.y, o.x, o.y - 3.5f * d); close() }, Color(0xFF0B8FA1)) }
            }
            // La trayectoria de transferencia: puntos naranjas con su estela tenue.
            TemaId.ESPACIO -> {
                val naranja = Color(0xFFE8601C)
                drawPath(p, naranja.copy(alpha = 0.18f), style = Stroke(4 * d))
                aLoLargo(p, 7 * d) { o, _ -> drawCircle(naranja, 1.1f * d, o) }
            }
            // El cable umbilical del batiscafo, con una burbuja cada tanto que sube desde él.
            TemaId.ABISMO -> {
                val cable = Color(0xFFB8913E)
                drawPath(p, cable.copy(alpha = 0.3f), style = Stroke(3.4f * d))
                drawPath(p, cable, style = Stroke(1.2f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * d, 2 * d))))
                var k = 0
                aLoLargo(p, 16 * d) { o, _ -> drawCircle(Color.White.copy(alpha = 0.55f), (1f + (k % 3) * 0.5f) * d, Offset(o.x + 3 * d, o.y - (4 + k % 3 * 2) * d), style = Stroke(0.7f * d)); k++ }
            }
            // La vía del tren: los durmientes y los dos rieles.
            TemaId.URSS -> {
                aLoLargo(p, 5 * d) { o, t -> val n = Offset(-t.y, t.x) * (3.5f * d); drawLine(Color(0xFF5A3A22).copy(alpha = 0.8f), o - n, o + n, 1.6f * d) }
                drawPath(p, Color(0xFF161414), style = Stroke(3.4f * d))
                drawPath(p, cs.surface, style = Stroke(1.4f * d))
            }
            // La calzada: borde de piedra y losas.
            TemaId.ROMANO -> { drawPath(p, Color(0xFF8C8478).copy(alpha = 0.7f), style = Stroke(5 * d)); drawPath(p, cs.surface, style = Stroke(3 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 1.2f * d)))) }
            // El paseo a pie sobre los sampietrini: una franja de adoquines con la junta clara en medio.
            TemaId.URBE -> {
                drawPath(p, Color(0xFF3A3530).copy(alpha = 0.25f), style = Stroke(6 * d))
                aLoLargo(p, 4.2f * d) { o, t -> val k = t * (1.8f * d); val n = Offset(-k.y, k.x)
                    drawPath(Path().apply { moveTo(o.x + k.x, o.y + k.y); lineTo(o.x + n.x, o.y + n.y); lineTo(o.x - k.x, o.y - k.y); lineTo(o.x - n.x, o.y - n.y); close() }, Color(0xFF3A3530).copy(alpha = 0.7f)) }
                drawPath(p, Color(0xFFA8432A).copy(alpha = 0.55f), style = Stroke(0.8f * d))
            }
            // La ruta de la marcha, punteada en púrpura macedonio sobre un filete blanco.
            TemaId.HELENO -> { drawPath(p, Color.White.copy(alpha = 0.6f), style = Stroke(3.4f * d)); drawPath(p, Color(0xFF5B2A6E), style = Stroke(1.6f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * d, 4 * d)))) }
            // La estela de la barca sobre el río: un filete claro, puntos de lapislázuli y, cada tanto, la cuña del agua que abre la proa.
            TemaId.EGIPTO -> {
                drawPath(p, cs.surface.copy(alpha = 0.7f), style = Stroke(3.6f * d))
                drawPath(p, Color(0xFF1D2F7A), style = Stroke(1.6f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * d, 3 * d))))
                aLoLargo(p, 26 * d) { o, t ->
                    val n = Offset(-t.y, t.x)
                    drawLine(Color(0xFFB8471E), o - t * (3 * d) + n * (3 * d), o, 1.2f * d); drawLine(Color(0xFFB8471E), o - t * (3 * d) - n * (3 * d), o, 1.2f * d)
                }
            }
            // Los pasos del peregrino, en tinta parda.
            TemaId.MEDIEVAL -> drawPath(p, Color(0xFF6B2E1A).copy(alpha = 0.7f), style = Stroke(1.4f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * d, 3 * d))))
            // Las huellas de los cascos de la columna: herraduras en la arena.
            TemaId.TEMPLARIO -> {
                drawPath(p, Color(0xFF8A6A3E).copy(alpha = 0.18f), style = Stroke(5 * d))
                aLoLargo(p, 9 * d) { o, t ->
                    val n = Offset(-t.y, t.x) * (2 * d); val a = o - n - t * (1.5f * d); val b = o + n - t * (1.5f * d)
                    val c1 = o - n * 1.1f + t * (2.5f * d); val c2 = o + n * 1.1f + t * (2.5f * d)
                    drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(c1.x, c1.y, c2.x, c2.y, b.x, b.y) }, Color(0xFF5A3A22).copy(alpha = 0.7f), style = Stroke(1f * d))
                }
            }
            // El cordel del maestro de obras.
            TemaId.GOTICO -> drawPath(p, cs.tertiary.copy(alpha = 0.7f), style = Stroke(1.6f * d))
            // Las pisadas del investigador: rayas cortas a tinta, una detrás de otra, con la tinta que se aclara.
            TemaId.ARCANO -> { drawPath(p, cs.onSurface.copy(alpha = 0.15f), style = Stroke(4 * d)); drawPath(p, cs.onSurface.copy(alpha = 0.7f), style = Stroke(1.4f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.5f * d, 4 * d)))) }
            // Un trazo de sanguina, con el lápiz de la construcción debajo.
            TemaId.RENACIMIENTO -> { drawPath(p, cs.onSurfaceVariant.copy(alpha = 0.25f), style = Stroke(0.8f * d)); drawPath(p, Color(0xFF8C3B1E).copy(alpha = 0.8f), style = Stroke(2 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10 * d, 3 * d)))) }
            // La derrota punteada en rojo de las cartas del tesoro.
            TemaId.PIRATA -> drawPath(p, Color(0xFF9A2A1C).copy(alpha = 0.8f), style = Stroke(1.8f * d, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f * d, 5 * d))))
            // La flecha roja de los mapas de campaña.
            TemaId.NAPOLEON -> { drawPath(p, Color(0xFFA3172E).copy(alpha = 0.35f), style = Stroke(7 * d)); drawPath(p, Color(0xFFA3172E), style = Stroke(1.6f * d)) }
            // La estela del trasatlántico: una banda de oro muy tenue y, encima, la derrota en trazos de laca.
            TemaId.DECO -> { drawPath(p, Color(0xFFD4AF37).copy(alpha = 0.3f), style = Stroke(8 * d)); drawPath(p, cs.onSurface.copy(alpha = 0.8f), style = Stroke(1.4f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(7 * d, 3 * d)))) }
            // El cortejo: tres líneas, azul, blanca y roja.
            TemaId.REVOLUCION -> { drawPath(p, Color(0xFF1F3A6E), style = Stroke(6 * d)); drawPath(p, Color.White, style = Stroke(4 * d)); drawPath(p, Color(0xFFB22234), style = Stroke(2 * d)) }
        }
    }

    // ================================================================== paradas

    fun DrawScope.etapaMas(tema: TemaId, cs: ColorScheme, c: Offset, r: Float, col: Color, meta: Boolean) {
        val d = density
        when (tema) {
            TemaId.ANDALUS -> with(TrofeosMas) { estrellaLazo(c, r * 1.2f, Brush.linearGradient(listOf(col, col)), Color(0xFFC9A04A), 0.8f * d) }
            // Cada ciudad del camino, el cartucho de su glifo.
            TemaId.MESO -> cartuchoMaya(c, r * 0.95f, col, cs.surface, col, ((c.x + c.y) / r).toInt())
            // Una moneda de cobre con agujero cuadrado.
            TemaId.SEDA -> { drawCircle(col, r, c); drawRect(cs.surface, Offset(c.x - r * 0.3f, c.y - r * 0.3f), Size(r * 0.6f, r * 0.6f)); drawCircle(Color(0xFF8E6A1F), r, c, style = Stroke(0.8f * d)) }
            // Cada posta, su sello (como los que coleccionaban los viajeros); la última, con doble marco.
            TemaId.EDO -> { drawRect(col, Offset(c.x - r * 0.9f, c.y - r * 0.9f), Size(r * 1.8f, r * 1.8f)); drawRect(cs.surface, Offset(c.x - r * 0.6f, c.y - r * 0.6f), Size(r * 1.2f, r * 1.2f), style = Stroke(0.8f * d))
                if (meta) drawRect(col, Offset(c.x - r * 1.25f, c.y - r * 1.25f), Size(r * 2.5f, r * 2.5f), style = Stroke(0.9f * d)) }
            // Una yurta.
            TemaId.MONGOL -> { drawArc(col, 180f, 180f, true, Offset(c.x - r, c.y - r * 0.9f), Size(2 * r, r * 1.4f)); drawRect(col, Offset(c.x - r, c.y - r * 0.2f), Size(2 * r, r * 0.9f)); drawRect(Color(0xFFD9772A), Offset(c.x - r * 0.25f, c.y + r * 0.1f), Size(r * 0.5f, r * 0.6f)) }
            // Una cabaña con su techo.
            TemaId.COLONOS -> { drawRect(col, Offset(c.x - r * 0.8f, c.y - r * 0.2f), Size(r * 1.6f, r * 1.1f)); drawPath(Path().apply { moveTo(c.x - r, c.y - r * 0.2f); lineTo(c.x, c.y - r * 1.1f); lineTo(c.x + r, c.y - r * 0.2f); close() }, lerp(col, Color.Black, 0.25f)) }
            // Una hoja.
            TemaId.BOTANICO -> drawPath(Path().apply { moveTo(c.x - r * 1.1f, c.y + r * 0.4f); quadraticTo(c.x - r * 0.6f, c.y - r * 1.2f, c.x + r * 1.1f, c.y - r * 0.6f); quadraticTo(c.x + r * 0.4f, c.y + r * 1.0f, c.x - r * 1.1f, c.y + r * 0.4f); close() }, col)
            // Un edificio roto, con una ventana vacía y un arbusto al pie.
            TemaId.RUINAS -> { drawPath(Path().apply { moveTo(c.x - r * 0.8f, c.y + r * 0.8f); lineTo(c.x - r * 0.8f, c.y - r * 0.6f); lineTo(c.x - r * 0.3f, c.y - r); lineTo(c.x, c.y - r * 0.5f); lineTo(c.x + r * 0.4f, c.y - r * 0.8f); lineTo(c.x + r * 0.8f, c.y - r * 0.3f); lineTo(c.x + r * 0.8f, c.y + r * 0.8f); close() }, col)
                drawRect(lerp(col, Color.Black, 0.35f), Offset(c.x - r * 0.35f, c.y - r * 0.1f), Size(r * 0.25f, r * 0.3f)); drawCircle(Color(0xFF4F7A33), r * 0.35f, Offset(c.x + r * 0.6f, c.y + r * 0.55f)) }
            // El puesto del mercado, con su toldo rayado.
            TemaId.NAVIDAD -> { drawRect(col, Offset(c.x - r * 0.8f, c.y - r * 0.2f), Size(r * 1.6f, r * 1.0f)); for (k in 0 until 4) drawRect(if (k % 2 == 0) Color(0xFFB3122E) else Color.White, Offset(c.x - r + k * r * 0.5f, c.y - r * 0.8f), Size(r * 0.5f, r * 0.6f)) }
            // Una piedra rúnica.
            TemaId.ANTIGUAS -> {
                drawPath(Path().apply { moveTo(c.x - r * 0.7f, c.y + r); lineTo(c.x - r * 0.8f, c.y - r * 0.4f); quadraticTo(c.x, c.y - r * 1.4f, c.x + r * 0.8f, c.y - r * 0.4f); lineTo(c.x + r * 0.7f, c.y + r); close() }, col)
                with(TrofeosMas) { runas(Offset(c.x - r * 0.3f, c.y), r * 0.6f, r * 0.8f, Color.Black.copy(alpha = 0.35f), 0.8f * d, 1, (c.x + c.y).toInt()) }
            }
            // Una mano en negativo en su disco; la meta, con un aro de ocre.
            TemaId.CUEVA -> {
                drawCircle(col, r, c)
                drawPath(manoUnida(c, r * 0.7f), cs.surface)
                if (meta) drawCircle(Color(0xFFA2401F), r * 1.3f, c, style = Stroke(1.2f * d))
            }
            // Una ventanilla de control: caseta cuadrada con su número.
            TemaId.DISTOPIA -> { drawRect(col, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r)); drawRect(Color(0xFF141414).copy(alpha = 0.6f), Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(0.8f * d)); drawRect(Color(0xFFF2EFE6).copy(alpha = 0.8f), Offset(c.x - r * 0.5f, c.y - r * 0.6f), Size(r, r * 0.5f)) }
            // Un servidor: caja negra con su filo, dos ranuras y la luz.
            TemaId.CIBER -> {
                drawRect(Color(0xFF05070B), Offset(c.x - r, c.y - r * 0.8f), Size(2 * r, 1.6f * r))
                drawRect(col, Offset(c.x - r, c.y - r * 0.8f), Size(2 * r, 1.6f * r), style = Stroke(1.2f * d))
                for (k in 0 until 2) drawLine(col, Offset(c.x - r * 0.6f, c.y - r * 0.3f + k * r * 0.55f), Offset(c.x + r * 0.15f, c.y - r * 0.3f + k * r * 0.55f), 0.9f * d)
                drawCircle(col, r * 0.16f, Offset(c.x + r * 0.55f, c.y - r * 0.3f))
                if (meta) drawRect(col.copy(alpha = 0.4f), Offset(c.x - r * 1.35f, c.y - r * 1.15f), Size(2.7f * r, 2.3f * r), style = Stroke(0.8f * d))
            }
            // Cada nivel, la puerta del elevador de carga con su letrero luminoso encima.
            TemaId.NEON -> {
                drawRect(Color(0xFF150E1B), Offset(c.x - r * 0.8f, c.y - r * 0.5f), Size(1.6f * r, 1.3f * r))
                drawLine(col.copy(alpha = 0.6f), Offset(c.x, c.y - r * 0.5f), Offset(c.x, c.y + r * 0.8f), 0.8f * d)
                drawRect(col, Offset(c.x - r * 0.8f, c.y - r * 0.5f), Size(1.6f * r, 1.3f * r), style = Stroke(1 * d))
                drawRoundRect(col.copy(alpha = 0.3f), Offset(c.x - r * 0.7f, c.y - r * 1.05f), Size(1.4f * r, r * 0.4f), CornerRadius(r * 0.2f), style = Stroke(3 * d))
                drawRoundRect(col, Offset(c.x - r * 0.7f, c.y - r * 1.05f), Size(1.4f * r, r * 0.4f), CornerRadius(r * 0.2f), style = Stroke(1 * d))
                if (meta) drawRoundRect(col.copy(alpha = 0.4f), Offset(c.x - r * 1.3f, c.y - r * 1.4f), Size(2.6f * r, 2.6f * r), CornerRadius(r * 0.3f), style = Stroke(0.8f * d))
            }
            // La escotilla de cada nivel: rueda de manivela sobre la puerta redonda.
            TemaId.REFUGIO -> {
                drawCircle(col, r, c)
                drawCircle(Color(0xFF1F2A2A).copy(alpha = 0.6f), r, c, style = Stroke(1f * d))
                drawCircle(Color(0xFFE8B83A), r * 0.55f, c, style = Stroke(1.2f * d))
                drawLine(Color(0xFFE8B83A), Offset(c.x - r * 0.55f, c.y), Offset(c.x + r * 0.55f, c.y), 1f * d); drawLine(Color(0xFFE8B83A), Offset(c.x, c.y - r * 0.55f), Offset(c.x, c.y + r * 0.55f), 1f * d)
            }
            // La unidad en el mapa táctico: un rectángulo con la cruz de las líneas, y el de la meta con el remate de la fortaleza.
            TemaId.ORDEN -> {
                drawRect(col, Offset(c.x - r, c.y - r * 0.7f), Size(2 * r, 1.4f * r))
                drawRect(Color(0xFF15181B).copy(alpha = 0.7f), Offset(c.x - r, c.y - r * 0.7f), Size(2 * r, 1.4f * r), style = Stroke(1f * d))
                drawLine(Color(0xFF15181B).copy(alpha = 0.7f), Offset(c.x - r, c.y - r * 0.7f), Offset(c.x + r, c.y + r * 0.7f), 0.9f * d)
                drawLine(Color(0xFF15181B).copy(alpha = 0.7f), Offset(c.x - r, c.y + r * 0.7f), Offset(c.x + r, c.y - r * 0.7f), 0.9f * d)
                if (meta) for (k in 0 until 3) drawRect(col, Offset(c.x - r + k * r * 0.8f, c.y - r * 1.05f), Size(r * 0.4f, r * 0.35f))
            }
            // La estación: el edificio con su tejado a dos aguas, la puerta en arco y el andén; en la meta, la torre del reloj.
            TemaId.VAPOR -> {
                val tinta = Color(0xFF231B14).copy(alpha = 0.75f)
                drawRect(col, Offset(c.x - r * 1.1f, c.y - r * 0.5f), Size(r * 2.2f, r * 1.1f))
                drawPath(Path().apply { moveTo(c.x - r * 1.3f, c.y - r * 0.5f); lineTo(c.x, c.y - r * 1.15f); lineTo(c.x + r * 1.3f, c.y - r * 0.5f); close() }, Color(0xFF9C3D24))
                drawArc(tinta, 180f, 180f, true, Offset(c.x - r * 0.3f, c.y - r * 0.15f), Size(r * 0.6f, r * 0.6f)); drawRect(tinta, Offset(c.x - r * 0.3f, c.y + r * 0.15f), Size(r * 0.6f, r * 0.45f))
                drawLine(tinta, Offset(c.x - r * 1.5f, c.y + r * 0.65f), Offset(c.x + r * 1.5f, c.y + r * 0.65f), 1.2f * d)
                if (meta) {
                    drawRect(col, Offset(c.x - r * 0.35f, c.y - r * 2.1f), Size(r * 0.7f, r * 1.1f))
                    drawCircle(Color(0xFFEADCC0), r * 0.26f, Offset(c.x, c.y - r * 1.65f)); drawCircle(tinta, r * 0.26f, Offset(c.x, c.y - r * 1.65f), style = Stroke(0.7f * d))
                    drawLine(tinta, Offset(c.x, c.y - r * 1.65f), Offset(c.x, c.y - r * 1.82f), 0.7f * d)
                }
            }
            // La gota que cae de la pipeta.
            TemaId.LABORATORIO -> {
                val gota = Path().apply { moveTo(c.x, c.y - r * 1.4f); cubicTo(c.x + r * 0.3f, c.y - r * 0.7f, c.x + r, c.y - r * 0.1f, c.x + r, c.y + r * 0.3f); cubicTo(c.x + r, c.y + r * 0.9f, c.x - r, c.y + r * 0.9f, c.x - r, c.y + r * 0.3f); cubicTo(c.x - r, c.y - r * 0.1f, c.x - r * 0.3f, c.y - r * 0.7f, c.x, c.y - r * 1.4f); close() }
                drawPath(gota, col); drawPath(gota, Color(0xFF12202B).copy(alpha = 0.45f), style = Stroke(0.8f * d))
                drawCircle(Color.White.copy(alpha = 0.6f), r * 0.2f, Offset(c.x - r * 0.4f, c.y + r * 0.1f))
            }
            // Cada parada es un cuerpo con su órbita: el círculo y un anillo inclinado.
            TemaId.ESPACIO -> {
                drawCircle(col, r * 0.8f, c)
                drawOval(Color(0xFFE8601C).copy(alpha = 0.8f), Offset(c.x - r * 1.4f, c.y - r * 0.4f), Size(r * 2.8f, r * 0.8f), style = Stroke(0.9f * d))
                drawArc(Color.Black.copy(alpha = 0.25f), -60f, 180f, true, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f))
            }
            // Una boya de cota: el flotador redondo con su franja y el cabo que la ata al fondo.
            TemaId.ABISMO -> {
                drawLine(cs.onSurface.copy(alpha = 0.5f), Offset(c.x, c.y + r * 0.8f), Offset(c.x, c.y + r * 1.8f), 0.8f * d)
                drawCircle(col, r * 0.85f, c)
                drawRect(Color.White.copy(alpha = 0.7f), Offset(c.x - r * 0.85f, c.y - r * 0.12f), Size(r * 1.7f, r * 0.24f))
                drawCircle(cs.onSurface.copy(alpha = 0.6f), r * 0.85f, c, style = Stroke(0.8f * d))
                if (meta) drawCircle(col.copy(alpha = 0.3f), r * 1.5f, c, style = Stroke(1f * d))
            }
            // La estación: el edificio con su techo y la estrellita roja.
            TemaId.URSS -> {
                drawRect(col, Offset(c.x - r, c.y - r * 0.4f), Size(2 * r, r * 1.2f))
                drawPath(Path().apply { moveTo(c.x - r * 1.2f, c.y - r * 0.4f); lineTo(c.x, c.y - r * 1.2f); lineTo(c.x + r * 1.2f, c.y - r * 0.4f); close() }, Color(0xFF161414).copy(alpha = 0.8f))
                with(TrofeosTema) { estrella(Offset(c.x, c.y - r * 0.6f), r * 0.32f, r * 0.13f, 5, Brush.linearGradient(listOf(Color(0xFFC1121F), Color(0xFFC1121F)))) }
                drawRect(Color(0xFF161414).copy(alpha = 0.5f), Offset(c.x - r * 0.25f, c.y + r * 0.2f), Size(r * 0.5f, r * 0.6f))
            }
            // Miliario: una columnita con su base.
            TemaId.ROMANO -> { drawRect(col, Offset(c.x - r * 0.55f, c.y - r), Size(r * 1.1f, r * 1.8f)); drawOval(col, Offset(c.x - r * 0.55f, c.y - r * 1.25f), Size(r * 1.1f, r * 0.5f)); drawRect(col, Offset(c.x - r * 0.8f, c.y + r * 0.7f), Size(r * 1.6f, r * 0.35f)) }
            // La placa de la calle de cada barrio: el mármol con su marco; en la meta, además, la loba encima.
            TemaId.URBE -> {
                drawRect(Color(0xFFF4EEE2), Offset(c.x - r * 1.2f, c.y - r * 0.7f), Size(r * 2.4f, r * 1.4f))
                drawRect(col, Offset(c.x - r * 1.2f, c.y - r * 0.7f), Size(r * 2.4f, r * 1.4f), style = Stroke(1.4f * d))
                drawLine(Color(0xFF2B2420).copy(alpha = 0.7f), Offset(c.x - r * 0.7f, c.y), Offset(c.x + r * 0.7f, c.y), 1.2f * d)
                if (meta) drawPath(lobaRuta(Offset(c.x, c.y - r * 1.25f), r * 0.9f), col)
            }
            // Una isla en el mar o una ciudad en tierra: un círculo con la greca alrededor.
            TemaId.HELENO -> { drawCircle(col, r, c); drawCircle(Color(0xFFC9A04A), r * 1.25f, c, style = Stroke(1.2f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * d, 1.5f * d)))) }
            // Un pilono: las dos torres en talud y la puerta; en la meta, el disco del sol encima.
            TemaId.EGIPTO -> {
                listOf(-1f, 1f).forEach { s ->
                    drawPath(Path().apply { moveTo(c.x + s * r * 1.1f, c.y + r * 0.8f); lineTo(c.x + s * r * 0.9f, c.y - r * 0.8f); lineTo(c.x + s * r * 0.15f, c.y - r * 0.8f); lineTo(c.x + s * r * 0.15f, c.y + r * 0.8f); close() }, col)
                }
                drawRect(col, Offset(c.x - r * 0.15f, c.y - r * 0.35f), Size(r * 0.3f, r * 0.14f))
                if (meta) drawCircle(Color(0xFFC9971C), r * 0.4f, Offset(c.x, c.y - r * 1.35f))
            }
            // La venera del peregrino.
            TemaId.MEDIEVAL -> {
                val p = Path().apply { moveTo(c.x, c.y + r); for (k in 0..6) { val a = PI + k * PI / 6; val x = c.x + (r * 1.15f * cos(a)).toFloat(); val y = c.y - r * 0.1f + (r * 1.1f * sin(a)).toFloat(); lineTo(x, y) }; close() }
                drawPath(p, col)
                for (k in 1..5) { val a = PI + k * PI / 6; drawLine(Color.Black.copy(alpha = 0.25f), Offset(c.x, c.y + r * 0.9f), Offset(c.x + (r * 1.05f * cos(a)).toFloat(), c.y - r * 0.1f + (r * sin(a)).toFloat()), 0.8f * d) }
            }
            // La atalaya de piedra con su almena; en la meta, el castillo de dos torres.
            TemaId.TEMPLARIO -> if (meta) {
                drawRect(col, Offset(c.x - r * 1.4f, c.y - r * 0.4f), Size(r * 2.8f, r * 1.4f))
                listOf(-1.4f, 0.8f).forEach { fx -> drawRect(col, Offset(c.x + r * fx, c.y - r * 1.3f), Size(r * 0.6f, r * 0.9f)) }
                drawPath(cruzPatada(Offset(c.x, c.y + r * 0.2f), r * 0.45f), Color(0xFFB0191F))
            } else {
                drawRect(col, Offset(c.x - r * 0.6f, c.y - r), Size(r * 1.2f, r * 2f))
                listOf(-0.6f, 0.2f).forEach { fx -> drawRect(col, Offset(c.x + r * fx, c.y - r * 1.4f), Size(r * 0.4f, r * 0.4f)) }
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(c.x - r * 0.15f, c.y - r * 0.4f), Size(r * 0.3f, r * 0.5f))
            }
            // Un arco apuntado.
            TemaId.GOTICO -> drawPath(Path().apply { moveTo(c.x - r * 0.8f, c.y + r); lineTo(c.x - r * 0.8f, c.y); quadraticTo(c.x - r * 0.8f, c.y - r * 0.8f, c.x, c.y - r * 1.3f); quadraticTo(c.x + r * 0.8f, c.y - r * 0.8f, c.x + r * 0.8f, c.y); lineTo(c.x + r * 0.8f, c.y + r); close() }, col)
            // La ficha de catálogo clavada con un alfiler; en la meta, con el ojo del signo.
            TemaId.ARCANO -> {
                val fi = Path().apply { moveTo(c.x - r, c.y - r * 0.7f); lineTo(c.x + r * 0.6f, c.y - r * 0.7f); lineTo(c.x + r, c.y - r * 0.3f); lineTo(c.x + r, c.y + r * 0.7f); lineTo(c.x - r, c.y + r * 0.7f); close() }
                drawPath(fi, col); drawPath(fi, cs.onSurface.copy(alpha = 0.5f), style = Stroke(0.8f * d))
                if (meta) with(Contenedores) { signoOjo(c, r * 0.6f, cs.surface, 0.8f * d) }
                else drawLine(cs.surface.copy(alpha = 0.7f), Offset(c.x - r * 0.6f, c.y), Offset(c.x + r * 0.6f, c.y - r * 0.15f), 1f * d)
                drawCircle(cs.tertiary, 1.6f * d, Offset(c.x, c.y - r * 0.7f))
            }
            // Un punto de compás con su cruz de construcción.
            TemaId.RENACIMIENTO -> { drawCircle(col, r, c); drawLine(Color(0xFF3A2614).copy(alpha = 0.5f), Offset(c.x - r * 1.6f, c.y), Offset(c.x + r * 1.6f, c.y), 0.8f * d); drawLine(Color(0xFF3A2614).copy(alpha = 0.5f), Offset(c.x, c.y - r * 1.6f), Offset(c.x, c.y + r * 1.6f), 0.8f * d) }
            // Una palmera en su isla; en la meta, la «X» roja del tesoro.
            TemaId.PIRATA -> if (meta) {
                drawCircle(col.copy(alpha = 0.25f), r * 1.3f, c)
                drawLine(Color(0xFF9A2A1C), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r), r * 0.42f, StrokeCap.Round)
                drawLine(Color(0xFF9A2A1C), Offset(c.x + r, c.y - r), Offset(c.x - r, c.y + r), r * 0.42f, StrokeCap.Round)
            } else {
                drawOval(col, Offset(c.x - r * 1.1f, c.y + r * 0.2f), Size(r * 2.2f, r * 0.8f))
                drawPath(Path().apply { moveTo(c.x - r * 0.1f, c.y + r * 0.5f); quadraticTo(c.x - r * 0.3f, c.y - r * 0.3f, c.x + r * 0.2f, c.y - r * 1.1f) }, Color(0xFF6B4A2B), style = Stroke(r * 0.22f))
                listOf(-1f, 1f, 0.2f).forEach { s -> drawPath(Path().apply { moveTo(c.x + r * 0.2f, c.y - r * 1.1f); quadraticTo(c.x + r * (0.2f + s * 0.6f), c.y - r * 1.6f, c.x + r * (0.2f + s * 1.1f), c.y - r * 0.8f) }, Color(0xFF3F6B3A), style = Stroke(r * 0.2f, cap = StrokeCap.Round)) }
            }
            // El banderín de la división; en la meta, el sol de Austerlitz.
            TemaId.NAPOLEON -> if (meta) {
                for (k in 0 until 12) { val a = k * PI / 6; drawLine(Color(0xFFD4AF37), Offset(c.x + (r * 1.1f * cos(a)).toFloat(), c.y + (r * 1.1f * sin(a)).toFloat()), Offset(c.x + (r * 1.7f * cos(a)).toFloat(), c.y + (r * 1.7f * sin(a)).toFloat()), 1.2f * d) }
                drawCircle(col, r, c)
            } else { drawRect(col, Offset(c.x - r, c.y - r * 0.7f), Size(r * 2, r * 1.4f)); drawLine(cs.onSurface.copy(alpha = 0.6f), Offset(c.x - r, c.y - r * 0.7f), Offset(c.x - r, c.y + r * 1.4f), 1f * d) }
            // El puerto, un salvavidas con sus franjas; la meta, un rascacielos escalonado con su sol de oro detrás.
            TemaId.DECO -> if (meta) {
                for (k in 0 until 9) { val a = PI + (k + 0.5) * PI / 9; drawLine(Color(0xFFD4AF37), c, Offset(c.x + (r * 2f * cos(a)).toFloat(), c.y + (r * 2f * sin(a)).toFloat()), 1.2f * d) }
                drawRect(col, Offset(c.x - r * 0.9f, c.y - r * 0.6f), Size(r * 1.8f, r * 1.6f))
                drawRect(col, Offset(c.x - r * 0.6f, c.y - r * 1.1f), Size(r * 1.2f, r * 0.5f))
                drawRect(col, Offset(c.x - r * 0.3f, c.y - r * 1.5f), Size(r * 0.6f, r * 0.4f))
                drawLine(col, Offset(c.x, c.y - r * 1.5f), Offset(c.x, c.y - r * 2.1f), 1f * d)
            } else {
                drawCircle(col, r * 0.72f, c, style = Stroke(r * 0.52f))
                for (k in 0 until 4) drawArc(Color.White.copy(alpha = 0.85f), k * 90f + 30f, 30f, false, Offset(c.x - r * 0.72f, c.y - r * 0.72f), Size(r * 1.44f, r * 1.44f), style = Stroke(r * 0.52f))
            }
            // La escarapela: rojo afuera, el color del día en medio y azul al centro.
            TemaId.REVOLUCION -> { drawCircle(Color(0xFFB22234), r * 1.15f, c); drawCircle(col, r * 0.8f, c); drawCircle(Color(0xFF1F3A6E), r * 0.38f, c) }
        }
    }

    // ================================================================== el marcador que avanza

    /**
     * El marcador de la campaña, sobre la parada a la que llegaste. En la mayoría de los temas es un alfiler con el emblema
     * del tema; en otros es una figura entera: el legionario con su estandarte, la vela del scriptorium, el peregrino con su
     * venera, la punta de plata y el banderín del punto de control. [salto] lo hace brincar un poco mientras hoy falta cumplir.
     */
    fun DrawScope.marcador(tema: TemaId, cs: ColorScheme, c: Offset, salto: Float = 0f) {
        val d = density
        val alto = salto * 3 * d
        val base = Offset(c.x, c.y - 10 * d - alto)
        val color = cs.tertiary
        drawOval(Color.Black.copy(alpha = 0.18f), Offset(c.x - 5 * d, c.y - 2 * d), Size(10 * d, 4 * d))
        val g = Offset(base.x, base.y - 5 * d); val r = 4.2f * d
        val tinta = if (color.luminance() > 0.5f) Color(0xFF2A1E0E) else Color.White
        // El alfiler, con el círculo donde va el emblema.
        fun alfiler() {
            drawPath(Path().apply { moveTo(base.x, base.y + 9 * d); cubicTo(base.x - 9 * d, base.y - 2 * d, base.x - 7 * d, base.y - 13 * d, base.x, base.y - 13 * d); cubicTo(base.x + 7 * d, base.y - 13 * d, base.x + 9 * d, base.y - 2 * d, base.x, base.y + 9 * d); close() }, color)
            drawCircle(tinta.copy(alpha = 0.25f), r + 0.8f * d, g)
        }
        when (tema) {
            TemaId.ROMANO -> legionario(c, alto)
            TemaId.URBE -> motoneta(c, alto)
            TemaId.MEDIEVAL -> vela(c, alto)
            TemaId.TEMPLARIO -> caballero(c, alto)
            TemaId.GOTICO -> peregrino(c, alto)
            TemaId.ARCANO -> investigador(c, alto)
            TemaId.RENACIMIENTO -> puntaDePlata(c, alto)
            TemaId.PIRATA -> punal(c, alto)
            // Distopía: no un alfiler, sino el banderín rojo del punto de control.
            TemaId.DISTOPIA -> {
                val top = c.y - 24 * d - alto
                drawLine(Color(0xFF141414), Offset(c.x, c.y), Offset(c.x, top), 1.6f * d)
                drawRect(Color(0xFFC8102E), Offset(c.x, top), Size(13 * d, 9 * d))
                drawRect(Color(0xFFF2EFE6), Offset(c.x + 2 * d, top + 3.5f * d), Size(9 * d, 2 * d))
            }
            // Gorro frigio, bicornio, casco, farolillo… un emblema chico de cada tema dentro del alfiler.
            TemaId.REVOLUCION -> { alfiler(); drawPath(Path().apply { moveTo(g.x - r * 0.7f, g.y + r * 0.6f); cubicTo(g.x - r * 0.8f, g.y - r * 0.4f, g.x, g.y - r, g.x + r * 0.7f, g.y - r * 0.4f); lineTo(g.x + r * 0.5f, g.y); lineTo(g.x + r * 0.7f, g.y + r * 0.6f); close() }, Color(0xFFB22234)) }
            TemaId.NAPOLEON -> { alfiler(); drawPath(Path().apply { moveTo(g.x - r, g.y + r * 0.3f); quadraticTo(g.x, g.y - r * 1.2f, g.x + r, g.y + r * 0.3f); close() }, Color(0xFF1A1A22)) }
            // Un abanico chico de laca dentro del alfiler.
            TemaId.DECO -> { alfiler(); for (k in 0 until 5) if (k % 2 == 0) drawArc(Color(0xFF16130E), 180f + k * 36f, 36f, true, Offset(g.x - r, g.y - r * 0.6f), Size(2 * r, 2 * r)); drawRect(Color(0xFF16130E), Offset(g.x - r, g.y + r * 0.4f), Size(2 * r, r * 0.25f)) }
            TemaId.HELENO -> { alfiler(); drawPath(Path().apply { moveTo(g.x - r * 0.6f, g.y - r * 0.6f); lineTo(g.x + r * 0.6f, g.y - r * 0.6f); lineTo(g.x + r * 0.7f, g.y + r * 0.4f); lineTo(g.x - r * 0.7f, g.y + r * 0.4f); close() }, Color.White) }
            // La barca del Nilo con su vela cuadrada: río arriba se navega a vela, con el viento del norte.
            TemaId.EGIPTO -> {
                val y0 = c.y - 6 * d - alto
                drawPath(Path().apply { moveTo(c.x - 11 * d, y0 - 3 * d); quadraticTo(c.x, y0 + 6 * d, c.x + 11 * d, y0 - 3 * d); quadraticTo(c.x, y0 + 1.5f * d, c.x - 11 * d, y0 - 3 * d); close() }, Color(0xFF6E4A2E))
                drawLine(Color(0xFF3E2C1A), Offset(c.x, y0), Offset(c.x, y0 - 17 * d), 1.2f * d)
                drawRect(Color(0xFFF2E6C4), Offset(c.x - 6 * d, y0 - 16 * d), Size(12 * d, 11 * d))
                drawRect(color, Offset(c.x - 6 * d, y0 - 16 * d), Size(12 * d, 11 * d), style = Stroke(0.9f * d))
            }
            TemaId.SEDA -> { alfiler(); drawOval(Color(0xFFC0392B), Offset(g.x - r * 0.6f, g.y - r * 0.8f), Size(r * 1.2f, r * 1.6f)) }
            TemaId.EDO -> { alfiler(); drawPath(Path().apply { moveTo(g.x - r * 0.95f, g.y + r * 0.35f); lineTo(g.x, g.y - r * 0.65f); lineTo(g.x + r * 0.95f, g.y + r * 0.35f); close() }, Color(0xFFC9B26A)) }
            TemaId.MONGOL -> { alfiler(); drawRoundRect(Color(0xFFD4AF37), Offset(g.x - r * 0.45f, g.y - r * 0.9f), Size(r * 0.9f, r * 1.8f), androidx.compose.ui.geometry.CornerRadius(r * 0.45f)) }
            TemaId.NAVIDAD -> { alfiler(); with(TrofeosTema) { estrella(g, r * 0.9f, r * 0.38f, 5, Brush.linearGradient(listOf(Color(0xFFD4AF37), Color(0xFFD4AF37)))) } }
            TemaId.ANDALUS -> { alfiler(); with(TrofeosMas) { estrellaLazo(g, r * 0.85f, Brush.linearGradient(listOf(Color(0xFFD4AF37), Color(0xFFD4AF37))), null, 0f) } }
            TemaId.MESO -> { alfiler(); cuentaJade(g, r * 0.75f, Color(0xFF2F7D57), Color.White) }
            TemaId.BOTANICO -> { alfiler(); drawPath(Path().apply { moveTo(g.x - r * 0.8f, g.y + r * 0.4f); quadraticTo(g.x - r * 0.4f, g.y - r, g.x + r * 0.8f, g.y - r * 0.5f); quadraticTo(g.x + r * 0.3f, g.y + r * 0.8f, g.x - r * 0.8f, g.y + r * 0.4f); close() }, Color(0xFF3F7A3A)) }
            // Un semáforo diminuto clavado en el plano: el rojo muerto y el verde encendido.
            TemaId.RUINAS -> { alfiler(); drawRect(Color(0xFF2E332E), Offset(g.x - r * 0.4f, g.y - r * 0.8f), Size(r * 0.8f, r * 1.6f)); drawCircle(Color(0xFF8A4A22).copy(alpha = 0.6f), r * 0.2f, Offset(g.x, g.y - r * 0.4f)); drawCircle(Color(0xFF8FCB6A), r * 0.24f, Offset(g.x, g.y + r * 0.35f)) }
            TemaId.COLONOS -> { alfiler(); drawArc(Color(0xFFF1E6C8), 180f, 180f, true, Offset(g.x - r * 0.8f, g.y - r * 0.8f), Size(r * 1.6f, r * 1.4f)); drawCircle(Color(0xFF5A3A22), r * 0.3f, Offset(g.x - r * 0.4f, g.y + r * 0.4f)); drawCircle(Color(0xFF5A3A22), r * 0.3f, Offset(g.x + r * 0.4f, g.y + r * 0.4f)) }
            TemaId.ANTIGUAS -> { alfiler(); with(TrofeosMas) { runas(Offset(g.x - r * 0.3f, g.y), r * 0.6f, r * 1.2f, tinta, 1f * d, 1, 4) } }
            TemaId.CUEVA -> { alfiler(); drawPath(manoUnida(g, r * 0.75f), tinta) }
            // La ficha amarilla del vecino.
            TemaId.REFUGIO -> { alfiler(); drawCircle(Color(0xFFE8B83A), r * 0.85f, g); drawCircle(Color(0xFF1F2A2A), r * 0.85f, g, style = Stroke(0.8f * d)); with(TrofeosMas) { atomo(g, r * 0.55f, Color(0xFF1F2A2A), 0.6f * d) } }
            // El banderín de avance.
            TemaId.ORDEN -> { alfiler(); drawLine(Color(0xFF15181B), Offset(g.x - r * 0.5f, g.y + r * 0.8f), Offset(g.x - r * 0.5f, g.y - r * 0.8f), 1f * d); drawPath(Path().apply { moveTo(g.x - r * 0.5f, g.y - r * 0.8f); lineTo(g.x + r * 0.8f, g.y - r * 0.35f); lineTo(g.x - r * 0.5f, g.y + r * 0.1f); close() }, Color(0xFF9E1B22)) }
            // La locomotora que recorre la línea, con su penacho de vapor.
            TemaId.VAPOR -> locomotora(c, alto)
            // La pipeta.
            TemaId.LABORATORIO -> { alfiler(); drawRect(Color.White, Offset(g.x - r * 0.18f, g.y - r * 0.9f), Size(r * 0.36f, r * 1.3f)); drawPath(Path().apply { moveTo(g.x - r * 0.18f, g.y + r * 0.4f); lineTo(g.x + r * 0.18f, g.y + r * 0.4f); lineTo(g.x, g.y + r * 0.95f); close() }, Color.White); drawRect(Color(0xFF5E4FD1), Offset(g.x - r * 0.3f, g.y - r * 0.95f), Size(r * 0.6f, r * 0.3f)) }
            // La nave.
            TemaId.ESPACIO -> { alfiler(); drawPath(Path().apply { moveTo(g.x, g.y - r); quadraticTo(g.x + r * 0.45f, g.y - r * 0.4f, g.x + r * 0.3f, g.y + r * 0.6f); lineTo(g.x - r * 0.3f, g.y + r * 0.6f); quadraticTo(g.x - r * 0.45f, g.y - r * 0.4f, g.x, g.y - r); close() }, Color.White); drawCircle(Color(0xFF1B3A8C), r * 0.18f, Offset(g.x, g.y - r * 0.2f)); drawLine(Color(0xFFE8601C), Offset(g.x, g.y + r * 0.6f), Offset(g.x, g.y + r * 0.95f), 1.4f * d) }
            // El batiscafo: el flotador alargado, la torreta y la esfera colgada debajo con su ojo de buey encendido.
            TemaId.ABISMO -> { alfiler(); drawRoundRect(Color.White, Offset(g.x - r * 0.9f, g.y - r * 0.55f), Size(r * 1.8f, r * 0.6f), androidx.compose.ui.geometry.CornerRadius(r * 0.3f)); drawRect(Color.White, Offset(g.x - r * 0.15f, g.y - r * 0.8f), Size(r * 0.3f, r * 0.25f)); drawCircle(Color.White, r * 0.36f, Offset(g.x, g.y + r * 0.4f)); drawCircle(Color(0xFF5CF2C6), r * 0.14f, Offset(g.x - r * 0.06f, g.y + r * 0.38f)) }
            // La locomotora vista de lado.
            TemaId.URSS -> { alfiler(); drawRect(Color(0xFF161414), Offset(g.x - r * 0.8f, g.y - r * 0.3f), Size(r * 1.4f, r * 0.7f)); drawRect(Color(0xFF161414), Offset(g.x + r * 0.1f, g.y - r * 0.8f), Size(r * 0.5f, r * 0.6f)); drawRect(Color(0xFF161414), Offset(g.x - r * 0.6f, g.y - r * 0.75f), Size(r * 0.2f, r * 0.45f)); drawRect(Color(0xFFC1121F), Offset(g.x - r * 0.8f, g.y + r * 0.05f), Size(r * 1.4f, r * 0.12f)); listOf(-0.45f, 0.05f).forEach { dx -> drawCircle(Color(0xFFC1121F), r * 0.22f, Offset(g.x + dx * r, g.y + r * 0.5f)) } }
            // El cursor de la consola.
            TemaId.CIBER -> { alfiler(); drawCircle(Color(0xFF05070B), r * 0.95f, g); drawRect(Color(0xFF00E5FF), Offset(g.x - r * 0.32f, g.y - r * 0.55f), Size(r * 0.64f, r * 1.1f)) }
            // El farolito de papel rojo.
            TemaId.NEON -> { alfiler(); drawOval(Color(0xFFFF6B5E), Offset(g.x - r * 0.6f, g.y - r * 0.8f), Size(r * 1.2f, r * 1.5f)); drawLine(Color(0xFF3A0D27), Offset(g.x - r * 0.6f, g.y), Offset(g.x + r * 0.6f, g.y), 0.8f * d); drawRect(Color(0xFF3A0D27), Offset(g.x - r * 0.25f, g.y - r * 0.95f), Size(r * 0.5f, r * 0.2f)) }
        }
    }

    /** El legionario con el estandarte: túnica roja y loriga, casco con cresta, el escudo con su umbo y el vexilo con el águila. */
    /** Roma de hoy: la motoneta que avanza por el paseo, con su escudo delantero, el asiento y el faro encendido. */
    private fun DrawScope.motoneta(c: Offset, alto: Float) {
        val d = density
        val pie = c.y - alto
        val verde = Color(0xFF6E9C7E); val oscuro = Color(0xFF2B2420); val cromo = Color(0xFFC9CDD2)
        listOf(c.x - 6.5f * d, c.x + 7 * d).forEach { x -> drawCircle(oscuro, 3 * d, Offset(x, pie - 3 * d)); drawCircle(cromo, 1.1f * d, Offset(x, pie - 3 * d)) }
        // La carrocería redonda de atrás, el piso y el escudo de adelante.
        drawPath(Path().apply { moveTo(c.x - 10 * d, pie - 4 * d); cubicTo(c.x - 11 * d, pie - 12 * d, c.x - 3 * d, pie - 12 * d, c.x - 1 * d, pie - 6 * d); lineTo(c.x + 4 * d, pie - 6 * d)
            lineTo(c.x + 5.5f * d, pie - 16 * d); lineTo(c.x + 8 * d, pie - 16 * d); lineTo(c.x + 9.5f * d, pie - 5 * d); lineTo(c.x - 10 * d, pie - 4 * d); close() }, verde)
        drawRoundRect(oscuro, Offset(c.x - 9 * d, pie - 13.5f * d), Size(8 * d, 2.4f * d), androidx.compose.ui.geometry.CornerRadius(1.2f * d))
        // El manubrio con el faro.
        drawLine(oscuro, Offset(c.x + 6.5f * d, pie - 16 * d), Offset(c.x + 6 * d, pie - 21 * d), 1.2f * d)
        drawLine(cromo, Offset(c.x + 3 * d, pie - 21 * d), Offset(c.x + 9 * d, pie - 21 * d), 1.2f * d)
        drawCircle(Color(0xFFFFE6A0), 1.6f * d, Offset(c.x + 8.6f * d, pie - 18.5f * d))
    }

    /** La locomotora del marcador: caldera de latón, chimenea, cabina, el quitapiedras y tres ruedas, con el penacho de vapor. */
    private fun DrawScope.locomotora(c: Offset, alto: Float) {
        val d = density
        val pie = c.y - alto
        val laton = Color(0xFFB8862B); val hierro = Color(0xFF2A2724); val ladrillo = Color(0xFF9C3D24)
        drawRect(laton, Offset(c.x - 9 * d, pie - 11 * d), Size(12 * d, 6 * d))
        drawRect(hierro, Offset(c.x + 3 * d, pie - 15 * d), Size(7 * d, 10 * d))
        drawRect(Color(0xFFF2C46A), Offset(c.x + 5 * d, pie - 13.5f * d), Size(3 * d, 3 * d))
        drawRect(hierro, Offset(c.x - 7.5f * d, pie - 15.5f * d), Size(2.6f * d, 4.5f * d))
        drawPath(Path().apply { moveTo(c.x - 9 * d, pie - 5 * d); lineTo(c.x - 12 * d, pie - 1 * d); lineTo(c.x - 9 * d, pie - 1 * d); close() }, ladrillo)
        listOf(c.x - 6 * d, c.x - 1 * d, c.x + 6.5f * d).forEach { x -> drawCircle(hierro, 2.6f * d, Offset(x, pie - 2.6f * d)); drawCircle(laton, 0.9f * d, Offset(x, pie - 2.6f * d)) }
        listOf(Offset(-6f, -19f) to 2.4f, Offset(-3f, -22.5f) to 2f, Offset(1f, -25f) to 1.6f).forEach { (o, rr) -> drawCircle(Color.White.copy(alpha = 0.55f), rr * d, Offset(c.x + o.x * d, pie + o.y * d)) }
    }

    private fun DrawScope.legionario(c: Offset, alto: Float) {
        val d = density
        val pie = c.y - alto
        val rojo = Color(0xFFA3172E); val oro = Color(0xFFD4AF37); val hierro = Color(0xFF6E6A64); val cuero = Color(0xFF3A2A1C); val madera = Color(0xFF5A3A22)
        // El asta del estandarte, detrás de la figura.
        val asta = c.x + 6 * d
        drawLine(madera, Offset(asta, pie + 0.5f * d), Offset(asta, pie - 29 * d), 1.2f * d)
        // Piernas, túnica y loriga.
        listOf(-1.6f, 1.6f).forEach { dx -> drawLine(cuero, Offset(c.x + dx * d, pie), Offset(c.x + dx * d, pie - 6 * d), 1.4f * d) }
        drawPath(Path().apply { moveTo(c.x - 4 * d, pie - 5.5f * d); lineTo(c.x + 4 * d, pie - 5.5f * d); lineTo(c.x + 3 * d, pie - 14 * d); lineTo(c.x - 3 * d, pie - 14 * d); close() }, rojo)
        for (k in 0 until 3) drawLine(hierro, Offset(c.x - 3 * d, pie - (9 + k * 1.6f) * d), Offset(c.x + 3 * d, pie - (9 + k * 1.6f) * d), 1f * d)
        // El brazo que sostiene el asta.
        drawLine(rojo, Offset(c.x + 2.6f * d, pie - 13 * d), Offset(asta, pie - 11.5f * d), 1.4f * d)
        // La cabeza con el casco y su cresta roja.
        drawCircle(Color(0xFFC89A6E), 2.4f * d, Offset(c.x, pie - 16.5f * d))
        drawArc(hierro, 180f, 180f, true, Offset(c.x - 2.8f * d, pie - 19.6f * d), Size(5.6f * d, 5.6f * d))
        drawArc(rojo, 200f, 140f, false, Offset(c.x - 3.4f * d, pie - 22.6f * d), Size(6.8f * d, 5 * d), style = Stroke(1.6f * d))
        // El escudo: rectangular y curvo, con el umbo de bronce.
        drawRoundRect(rojo, Offset(c.x - 8.5f * d, pie - 15 * d), Size(5 * d, 10 * d), androidx.compose.ui.geometry.CornerRadius(1.6f * d))
        drawRoundRect(oro, Offset(c.x - 8.5f * d, pie - 15 * d), Size(5 * d, 10 * d), androidx.compose.ui.geometry.CornerRadius(1.6f * d), style = Stroke(0.7f * d))
        drawCircle(oro, 1.2f * d, Offset(c.x - 6 * d, pie - 10 * d))
        // El vexilo con sus flecos y el águila de oro en la punta.
        drawLine(oro, Offset(asta - 4.5f * d, pie - 24 * d), Offset(asta + 4.5f * d, pie - 24 * d), 1f * d)
        drawRect(rojo, Offset(asta - 4 * d, pie - 24 * d), Size(8 * d, 6.5f * d))
        for (k in 0 until 4) { val x = asta - 3.4f * d + k * 2.3f * d; drawLine(oro, Offset(x, pie - 17.5f * d), Offset(x, pie - 16 * d), 0.6f * d) }
        drawPath(Path().apply { moveTo(asta, pie - 26.5f * d); lineTo(asta - 4.5f * d, pie - 31 * d); lineTo(asta - 1.4f * d, pie - 28.4f * d); lineTo(asta, pie - 32 * d); lineTo(asta + 1.4f * d, pie - 28.4f * d); lineTo(asta + 4.5f * d, pie - 31 * d); close() }, oro)
    }

    /** La vela del scriptorium en su palmatoria de latón, con la cera escurrida, la llama y su resplandor. */
    private fun DrawScope.vela(c: Offset, alto: Float) {
        val d = density
        val pie = c.y - alto
        val laton = Color(0xFFC9A04A); val cera = Color(0xFFF1E6C8)
        drawArc(laton, -90f, 180f, false, Offset(c.x + 5 * d, pie - 5.6f * d), Size(4.6f * d, 4.2f * d), style = Stroke(1.2f * d))
        drawOval(laton, Offset(c.x - 7 * d, pie - 3 * d), Size(14 * d, 3.6f * d))
        drawRect(laton, Offset(c.x - 3 * d, pie - 5.4f * d), Size(6 * d, 3 * d))
        drawRect(cera, Offset(c.x - 2.2f * d, pie - 18 * d), Size(4.4f * d, 13 * d))
        drawRect(Color(0xFFB79A6A).copy(alpha = 0.6f), Offset(c.x - 2.2f * d, pie - 18 * d), Size(4.4f * d, 13 * d), style = Stroke(0.6f * d))
        drawOval(cera, Offset(c.x + 1.2f * d, pie - 17.5f * d), Size(2 * d, 4.4f * d))
        val llama = Offset(c.x, pie - 22 * d)
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD27A).copy(alpha = 0.45f), Color.Transparent), llama, 9 * d), 9 * d, llama)
        drawLine(Color(0xFF2A1E0E), Offset(c.x, pie - 18 * d), Offset(c.x, pie - 19.6f * d), 0.8f * d)
        drawPath(Path().apply { moveTo(c.x, pie - 26.5f * d); quadraticTo(c.x + 2.6f * d, pie - 21.5f * d, c.x, pie - 19 * d); quadraticTo(c.x - 2.6f * d, pie - 21.5f * d, c.x, pie - 26.5f * d); close() }, Color(0xFFFFB347))
        drawPath(Path().apply { moveTo(c.x, pie - 24 * d); quadraticTo(c.x + 1.2f * d, pie - 21 * d, c.x, pie - 19.6f * d); quadraticTo(c.x - 1.2f * d, pie - 21 * d, c.x, pie - 24 * d); close() }, Color(0xFFFFF1B8))
    }

    /** El caballero de la orden a caballo: el caballo pardo, la sobreveste blanca con la cruz roja, el yelmo y la lanza con su gallardete. */
    private fun DrawScope.caballero(c: Offset, alto: Float) {
        val d = density
        val y = c.y - alto
        val caballo = Color(0xFF6E4A2E); val hierro = Color(0xFF6E757D); val lino = Color(0xFFF4EFE2); val rojo = Color(0xFFB0191F)
        drawOval(caballo, Offset(c.x - 8 * d, y - 12 * d), Size(14 * d, 6 * d))
        drawPath(Path().apply { moveTo(c.x + 4 * d, y - 11 * d); lineTo(c.x + 8 * d, y - 17 * d); lineTo(c.x + 11 * d, y - 16 * d); lineTo(c.x + 7 * d, y - 9 * d); close() }, caballo)
        listOf(-6.5f, -3.5f, 2.5f, 4.5f).forEach { fx -> drawLine(caballo, Offset(c.x + fx * d, y - 7 * d), Offset(c.x + fx * d, y), 1.4f * d) }
        drawLine(caballo, Offset(c.x - 8 * d, y - 10 * d), Offset(c.x - 10 * d, y - 4 * d), 1.2f * d)
        drawPath(Path().apply { moveTo(c.x - 4 * d, y - 11 * d); lineTo(c.x + 2 * d, y - 11 * d); lineTo(c.x + 1.4f * d, y - 19 * d); lineTo(c.x - 3.4f * d, y - 19 * d); close() }, lino)
        drawLine(rojo, Offset(c.x - 1 * d, y - 18 * d), Offset(c.x - 1 * d, y - 12.5f * d), 1.2f * d); drawLine(rojo, Offset(c.x - 3 * d, y - 16 * d), Offset(c.x + 1 * d, y - 16 * d), 1.2f * d)
        drawRect(hierro, Offset(c.x - 3 * d, y - 23.5f * d), Size(4 * d, 4.5f * d))
        drawLine(Color.Black.copy(alpha = 0.6f), Offset(c.x - 2.6f * d, y - 21.6f * d), Offset(c.x + 0.6f * d, y - 21.6f * d), 0.6f * d)
        drawLine(Color(0xFF4A3524), Offset(c.x + 2 * d, y - 8 * d), Offset(c.x + 5 * d, y - 34 * d), 1 * d)
        drawPath(Path().apply { moveTo(c.x + 5 * d, y - 34 * d); lineTo(c.x + 12 * d, y - 32 * d); lineTo(c.x + 9 * d, y - 30.5f * d); lineTo(c.x + 11.5f * d, y - 29 * d); lineTo(c.x + 4.6f * d, y - 30.5f * d); close() }, lino)
        drawLine(rojo, Offset(c.x + 6.4f * d, y - 32.6f * d), Offset(c.x + 6.2f * d, y - 30.4f * d), 0.8f * d)
    }

    /** El peregrino: la esclavina parda, el sombrero de ala ancha con la venera y el bordón con su calabaza. */
    private fun DrawScope.peregrino(c: Offset, alto: Float) {
        val d = density
        val pie = c.y - alto
        val capa = Color(0xFF6B4A2A); val ala = Color(0xFF4A3420); val madera = Color(0xFF5A3A22); val calabaza = Color(0xFFC9963A)
        // El bordón, más alto que el peregrino, con la calabaza colgada.
        val b = c.x + 6 * d
        drawLine(madera, Offset(b, pie + 0.5f * d), Offset(b, pie - 28 * d), 1.3f * d)
        drawCircle(madera, 1.3f * d, Offset(b, pie - 28 * d))
        drawOval(calabaza, Offset(b + 0.4f * d, pie - 24.5f * d), Size(3 * d, 4.4f * d))
        drawCircle(calabaza, 1.1f * d, Offset(b + 1.9f * d, pie - 25.4f * d))
        // El hábito hasta los pies y la esclavina sobre los hombros.
        drawPath(Path().apply { moveTo(c.x - 5 * d, pie); lineTo(c.x + 5 * d, pie); lineTo(c.x + 3 * d, pie - 13 * d); quadraticTo(c.x, pie - 15.5f * d, c.x - 3 * d, pie - 13 * d); close() }, capa)
        drawPath(Path().apply { moveTo(c.x - 5.5f * d, pie - 9 * d); quadraticTo(c.x, pie - 7 * d, c.x + 5.5f * d, pie - 9 * d); lineTo(c.x + 3.4f * d, pie - 14 * d); quadraticTo(c.x, pie - 16 * d, c.x - 3.4f * d, pie - 14 * d); close() }, lerp(capa, Color.Black, 0.2f))
        drawLine(capa, Offset(c.x + 2.5f * d, pie - 11 * d), Offset(b, pie - 13 * d), 1.6f * d)
        // La cara y el sombrero de ala ancha.
        drawCircle(Color(0xFFC89A6E), 2.3f * d, Offset(c.x, pie - 17.5f * d))
        drawOval(ala, Offset(c.x - 6 * d, pie - 20.6f * d), Size(12 * d, 2.6f * d))
        drawArc(ala, 180f, 180f, true, Offset(c.x - 2.8f * d, pie - 23.2f * d), Size(5.6f * d, 5 * d))
        // La venera blanca en el sombrero.
        val v = Offset(c.x, pie - 20.6f * d)
        drawPath(Path().apply { moveTo(v.x, v.y + 1.4f * d); for (k in 0..6) { val a = PI + k * PI / 6; lineTo(v.x + (2.2f * d * cos(a)).toFloat(), v.y + (2.2f * d * sin(a)).toFloat()) }; close() }, Color(0xFFF2EFE6))
    }

    /** El investigador: el gabán largo, el sombrero de fieltro y el farol encendido que alumbra el camino. */
    private fun DrawScope.investigador(c: Offset, alto: Float) {
        val d = density
        val pie = c.y - alto
        val gaban = Color(0xFF3A4038); val fieltro = Color(0xFF26281F); val laton = Color(0xFFB59A5E)
        // El farol en la mano, con su halo.
        val f = Offset(c.x + 7 * d, pie - 9 * d)
        drawCircle(Brush.radialGradient(listOf(Color(0xFFF2E3A0).copy(alpha = 0.5f), Color.Transparent), f, 10 * d), 10 * d, f)
        drawLine(laton, Offset(c.x + 3 * d, pie - 12 * d), Offset(f.x, f.y - 3.5f * d), 1 * d)
        drawRect(Color(0xFFF2E3A0), Offset(f.x - 1.8f * d, f.y - 2.6f * d), Size(3.6f * d, 4.4f * d))
        drawRect(laton, Offset(f.x - 1.8f * d, f.y - 2.6f * d), Size(3.6f * d, 4.4f * d), style = Stroke(0.7f * d))
        // El gabán hasta las rodillas y las piernas.
        drawLine(fieltro, Offset(c.x - 1.6f * d, pie), Offset(c.x - 1.4f * d, pie - 5 * d), 1.4f * d)
        drawLine(fieltro, Offset(c.x + 1.6f * d, pie), Offset(c.x + 1.4f * d, pie - 5 * d), 1.4f * d)
        drawPath(Path().apply { moveTo(c.x - 4.6f * d, pie - 4 * d); lineTo(c.x + 4.6f * d, pie - 4 * d); lineTo(c.x + 3.2f * d, pie - 15 * d); quadraticTo(c.x, pie - 16.5f * d, c.x - 3.2f * d, pie - 15 * d); close() }, gaban)
        drawLine(lerp(gaban, Color.Black, 0.3f), Offset(c.x, pie - 15 * d), Offset(c.x, pie - 4 * d), 0.6f * d)
        // La cabeza y el sombrero de ala.
        drawCircle(Color(0xFFC8A884), 2.2f * d, Offset(c.x, pie - 18.4f * d))
        drawOval(fieltro, Offset(c.x - 4.6f * d, pie - 21 * d), Size(9.2f * d, 2.2f * d))
        drawRect(fieltro, Offset(c.x - 2.6f * d, pie - 24 * d), Size(5.2f * d, 3.4f * d))
    }

    /** La punta de plata: el estilete de madera con su virola, apoyado en la parada, y la línea gris que deja sobre el papel. */
    /** El puñal clavado en la carta, un poco inclinado: hoja de acero, guarda de latón, empuñadura de cuero y pomo. */
    private fun DrawScope.punal(c: Offset, alto: Float) {
        val d = density
        val punta = Offset(c.x, c.y - 1 * d - alto)
        drawLine(Color(0xFF2A1C0F).copy(alpha = 0.35f), Offset(punta.x - 4 * d, punta.y + 1 * d), Offset(punta.x + 4 * d, punta.y + 1 * d), 1 * d)
        val dx = -0.26f; val dy = -0.97f
        fun en(t: Float) = Offset(punta.x + dx * t * d, punta.y + dy * t * d)
        val nx = -dy; val ny = dx
        fun tramo(t0: Float, t1: Float, a0: Float, a1: Float, col: Color) {
            val p0 = en(t0); val p1 = en(t1)
            drawPath(Path().apply { moveTo(p0.x + nx * a0 * d, p0.y + ny * a0 * d); lineTo(p1.x + nx * a1 * d, p1.y + ny * a1 * d); lineTo(p1.x - nx * a1 * d, p1.y - ny * a1 * d); lineTo(p0.x - nx * a0 * d, p0.y - ny * a0 * d); close() }, col)
        }
        tramo(0f, 16f, 0.1f, 1.7f, Color(0xFFC9CDD3))
        drawLine(Color.White.copy(alpha = 0.6f), en(2f), en(15f), 0.5f * d)
        val g = en(16.5f); drawLine(Color(0xFFC9A04A), Offset(g.x + nx * 5 * d, g.y + ny * 5 * d), Offset(g.x - nx * 5 * d, g.y - ny * 5 * d), 1.8f * d, StrokeCap.Round)
        tramo(17f, 26f, 1.3f, 1.3f, Color(0xFF4A3424))
        for (k in 0 until 4) { val q = en(18f + k * 2.2f); drawLine(Color(0xFFB59A62), Offset(q.x + nx * 1.3f * d, q.y + ny * 1.3f * d), Offset(q.x - nx * 1.3f * d, q.y - ny * 1.3f * d), 0.6f * d) }
        drawCircle(Color(0xFFC9A04A), 2.2f * d, en(27.5f))
    }

    private fun DrawScope.puntaDePlata(c: Offset, alto: Float) {
        val d = density
        val punta = Offset(c.x, c.y - 1.5f * d - alto)
        drawPath(Path().apply { moveTo(punta.x - 12 * d, punta.y + 1.5f * d); quadraticTo(punta.x - 6 * d, punta.y - 2.5f * d, punta.x, punta.y) }, Color(0xFF8A8F96), style = Stroke(0.8f * d))
        // El estilete va inclinado hacia arriba a la derecha: punta de plata, virola de latón y mango.
        val dx = 0.42f; val dy = -0.91f
        fun en(t: Float) = Offset(punta.x + dx * t * d, punta.y + dy * t * d)
        fun tramo(t0: Float, t1: Float, a0: Float, a1: Float, col: Color) {
            val p0 = en(t0); val p1 = en(t1); val nx = -dy * d; val ny = dx * d
            drawPath(Path().apply { moveTo(p0.x + nx * a0, p0.y + ny * a0); lineTo(p1.x + nx * a1, p1.y + ny * a1); lineTo(p1.x - nx * a1, p1.y - ny * a1); lineTo(p0.x - nx * a0, p0.y - ny * a0); close() }, col)
        }
        tramo(0f, 6f, 0.1f, 0.9f, Color(0xFFC9CDD3))
        tramo(6f, 9f, 1.2f, 1.2f, Color(0xFFC9A04A))
        tramo(9f, 28f, 1.4f, 1.6f, Color(0xFF8A5A2B))
        drawCircle(Color(0xFF8A5A2B), 1.6f * d, en(28f))
        drawLine(Color.White.copy(alpha = 0.8f), en(1.5f), en(5f), 0.5f * d)
    }
}
