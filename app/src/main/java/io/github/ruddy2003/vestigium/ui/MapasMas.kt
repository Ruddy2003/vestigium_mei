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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
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
        TemaId.BASICO -> listOf(Offset(0.08f, 0.8f), Offset(0.22f, 0.66f), Offset(0.36f, 0.7f), Offset(0.5f, 0.5f), Offset(0.64f, 0.4f), Offset(0.78f, 0.44f), Offset(0.92f, 0.24f))
        // Los puntos de control: piso por piso, de la ventanilla de abajo al Ministerio en lo alto.
        TemaId.DISTOPIA -> listOf(Offset(0.08f, 0.84f), Offset(0.22f, 0.72f), Offset(0.36f, 0.74f), Offset(0.5f, 0.54f), Offset(0.62f, 0.5f), Offset(0.74f, 0.32f), Offset(0.88f, 0.16f))
        // De la calle Haight al festival de Monterey (1967), en el plano dibujado de San Francisco: Haight y Ashbury → los
        // Polo Fields del Golden Gate Park → la sala de la calle Sutter → la FM del 106.9 → el 1371 de Haight → el Panhandle,
        // y, por la costa, Monterey (en su recuadro: el plano no va a escala).
        TemaId.PSICODELIA -> listOf(Offset(0.24f, 0.72f), Offset(0.08f, 0.38f), Offset(0.36f, 0.12f), Offset(0.6f, 0.6f), Offset(0.38f, 0.72f), Offset(0.39f, 0.46f), Offset(0.9f, 0.74f))
        // La línea 1 del metro, la «ligne A» que se abrió el 19 de julio de 1900 (el norte arriba), con las etapas de `Campanas`:
        // Porte Maillot, Champs-Élysées, Palais-Royal, Hôtel de Ville, Bastille, Gare de Lyon y Porte de Vincennes.
        TemaId.NOUVEAU -> geo(listOf(2.2826 to 48.8781, 2.3143 to 48.8676, 2.3364 to 48.8625, 2.3522 to 48.8573, 2.3690 to 48.8531, 2.3730 to 48.8448, 2.4105 to 48.8470), 2.262, 2.426, 48.838, 48.882)
        // El levantamiento del 52 a. C., sobre la Galia (el norte arriba), con las etapas de `Campanas`: Cenabum, donde corre el
        // grito; Gergovia, donde Vercingétorix toma el mando; Avárico, el cerco perdido; Gergovia otra vez, la victoria, en la
        // ladera del sur, frente a los campamentos de César; Bibracte, la asamblea; el cerco de Alesia, sobre su línea de
        // dentro, y la entrega con honra, en el oppidum.
        TemaId.GALIA -> geo(listOf(1.91 to 47.9, 3.12 to 45.76, 2.4 to 47.08, 3.36 to 45.62, 4.03 to 46.92, 4.32 to 47.53, 4.5 to 47.54), 0.9, 5.6, 45.3, 48.3)
        // El ascenso del alma: cada parada cae en su esfera, de la Luna (la más cercana a la tierra, abajo a la izquierda) a
        // Saturno, en zigzag hacia la derecha, donde está la Ogdóada. [fondoMas] dibuja los círculos por estos mismos puntos.
        TemaId.ARCONTES -> listOf(Offset(0.17f, 0.78f), Offset(0.28f, 0.42f), Offset(0.4f, 0.72f), Offset(0.52f, 0.34f), Offset(0.64f, 0.66f), Offset(0.77f, 0.3f), Offset(0.9f, 0.5f))
        // La ruta de salto de la célula en la carta estelar, de mundo en mundo: Hollín (el puerto de ladrillo, abajo a la
        // izquierda), Escoria, Vigía, Luna Ocre, El Anillo, Cantera y Albar, la capital del Directorio, arriba a la derecha.
        TemaId.FRENTE -> listOf(Offset(0.08f, 0.8f), Offset(0.22f, 0.54f), Offset(0.36f, 0.76f), Offset(0.5f, 0.42f), Offset(0.63f, 0.66f), Offset(0.77f, 0.4f), Offset(0.9f, 0.18f))
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
            TemaId.BASICO -> {}   // sin paisaje: solo la ruta y las paradas
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
            TemaId.PSICODELIA -> {
                // El plano de San Francisco en 1967, de Haight a Monterey, en la cara del tema: el de una guía de la ciudad en
                // los colores de la foto de los 70, o el impreso como el cartel de la semana (ver PantallasPsicodelia.fondoCampana).
                val pts = puntos(tema).map { Offset(it.x * w, it.y * h) }
                with(io.github.ruddy2003.vestigium.ui.PantallasPsicodelia) { fondoCampana(oscuro, avance, pts) }
            }
            TemaId.NOUVEAU -> {
                // El plano de París de 1900 (con la misma proyección que [puntos]): el papel crema del plano impreso, las
                // fortificaciones con sus bastiones a los dos lados (Porte Maillot y Porte de Vincennes son puertas suyas), el Sena con
                // la Cité y la isla de San Luis, los bulevares, los jardines de las Tullerías y los bosques, y el recinto de la
                // Exposición entre el Campo de Marte, el Trocadero y la explanada de los Inválidos, con el Grand Palais. Sobre el
                // trazado de la línea, sus dieciocho estaciones. De noche, el mismo plano con las calles alumbradas, el Palacio de
                // la Electricidad encendido y los faroles de las estaciones ya pasadas.
                fun g(lon: Double, lat: Double) = Offset(w * (0.07f + 0.86f * ((lon - 2.262) / 0.164).toFloat()), h * (0.92f - 0.84f * ((lat - 48.838) / 0.044).toFloat()))
                fun linea(vararg q: Pair<Double, Double>): Path = Path().apply {
                    val l = q.map { g(it.first, it.second) }
                    moveTo(l[0].x, l[0].y)
                    for (i in 1 until l.size - 1) quadraticTo(l[i].x, l[i].y, (l[i].x + l[i + 1].x) / 2, (l[i].y + l[i + 1].y) / 2)
                    lineTo(l.last().x, l.last().y)
                }
                val tinta = if (oscuro) Color(0xFFECE5D6) else TrazosNouveau.PLUMA
                val luz = TrazosNouveau.LUZ
                // El papel del plano.
                if (oscuro) drawRect(Brush.verticalGradient(listOf(Color(0xFF151A20).copy(alpha = 0.85f), Color(0xFF1B2128).copy(alpha = 0.7f)), 0f, h), Offset.Zero, size)
                else drawRect(Brush.verticalGradient(listOf(Color(0xFFF1E6C8).copy(alpha = 0.55f), Color(0xFFE8DCBC).copy(alpha = 0.45f)), 0f, h), Offset.Zero, size)
                // Los bosques de los dos extremos: Boulogne al oeste y Vincennes al sureste, copas redondas.
                val copa = if (oscuro) TrazosNouveau.TALLO_SOMBRA.copy(alpha = 0.45f) else TrazosNouveau.TALLO.copy(alpha = 0.45f)
                val az = java.util.Random(19)
                listOf(Triple(2.262, 48.865, 14), Triple(2.262, 48.848, 12), Triple(2.42, 48.84, 10)).forEach { (lo, la, n) ->
                    val c0 = g(lo, la)
                    repeat(n) { val q = c0 + Offset((az.nextFloat() - 0.5f) * 30 * d, (az.nextFloat() - 0.5f) * 22 * d); drawCircle(copa, (2.2f + az.nextFloat() * 1.6f) * d, q) }
                }
                // Los jardines: las Tullerías, el Campo de Marte y la explanada de los Inválidos, en salvia.
                val jardin = if (oscuro) TrazosNouveau.TALLO_SOMBRA.copy(alpha = 0.3f) else TrazosNouveau.TALLO.copy(alpha = 0.28f)
                fun cuadro(vararg q: Pair<Double, Double>) = Path().apply { q.forEachIndexed { i, (lo, la) -> val o = g(lo, la); if (i == 0) moveTo(o.x, o.y) else lineTo(o.x, o.y) }; close() }
                drawPath(cuadro(2.3215 to 48.8655, 2.3325 to 48.8625, 2.3315 to 48.8603, 2.3205 to 48.8635), jardin)
                // El recinto de la Exposición: el Campo de Marte, el Trocadero, el muelle de Orsay, la explanada y los dos palacios.
                val expo = listOf(
                    cuadro(2.2935 to 48.8588, 2.2985 to 48.8613, 2.3075 to 48.8545, 2.3025 to 48.8518),
                    cuadro(2.2845 to 48.8635, 2.2905 to 48.8655, 2.2945 to 48.8618, 2.2885 to 48.8598),
                    cuadro(2.2985 to 48.8613, 2.3165 to 48.8625, 2.3165 to 48.8605, 2.3005 to 48.8598),
                    cuadro(2.3115 to 48.8615, 2.3155 to 48.8615, 2.3155 to 48.8555, 2.3115 to 48.8555),
                    cuadro(2.3105 to 48.8678, 2.3165 to 48.8672, 2.3155 to 48.8640, 2.3100 to 48.8646))
                val tonoExpo = if (oscuro) TrazosNouveau.IRIS.copy(alpha = 0.32f) else TrazosNouveau.MALVA.copy(alpha = 0.3f)
                expo.forEach { drawPath(it, tonoExpo); drawPath(it, tonoExpo.copy(alpha = 0.7f), style = Stroke(0.8f * d)) }
                clipPath(Path().apply { expo.forEach { addPath(it) } }) {
                    var x = 0f; while (x < w) { drawLine(TrazosNouveau.IRIS.copy(alpha = if (oscuro) 0.25f else 0.22f), Offset(x, 0f), Offset(x - h, h), 0.6f * d); x += 3.5f * d }
                }
                // La nave del Grand Palais, su cúpula de vidrio rebajada.
                val gp = g(2.3128, 48.866); drawArc(tinta.copy(alpha = 0.5f), 180f, 180f, false, Offset(gp.x - 4 * d, gp.y - 3 * d), Size(8 * d, 6 * d), style = Stroke(0.8f * d))
                // El Palacio de la Electricidad, al fondo del Campo de Marte: de día un rombo de oro, de noche sus lámparas encendidas.
                val pe = g(2.302, 48.8535)
                if (oscuro) drawCircle(Brush.radialGradient(listOf(TrazosNouveau.LUZ_HALO.copy(alpha = 0.7f), luz.copy(alpha = 0.25f), Color.Transparent), pe, 18 * d), 18 * d, pe)
                drawPath(Path().apply { moveTo(pe.x, pe.y - 3.5f * d); lineTo(pe.x + 3 * d, pe.y); lineTo(pe.x, pe.y + 2 * d); lineTo(pe.x - 3 * d, pe.y); close() }, if (oscuro) TrazosNouveau.LUZ else TrazosNouveau.ORO)
                // El Sena, de este a oeste, con sus dos islas.
                val sena = linea(2.43 to 48.829, 2.39 to 48.834, 2.376 to 48.8425, 2.366 to 48.848, 2.357 to 48.851, 2.35 to 48.8545, 2.343 to 48.857, 2.333 to 48.8595, 2.325 to 48.8625,
                    2.318 to 48.864, 2.31 to 48.864, 2.302 to 48.8635, 2.295 to 48.861, 2.288 to 48.857, 2.282 to 48.852, 2.275 to 48.846, 2.262 to 48.838)
                val agua = if (oscuro) Color(0xFF2C4A6B).copy(alpha = 0.75f) else Color(0xFF8FB0C4).copy(alpha = 0.6f)
                drawPath(sena, agua, style = Stroke(5 * d, cap = StrokeCap.Round))
                drawPath(sena, if (oscuro) Color(0xFF6F8FB0).copy(alpha = 0.5f) else Color(0xFF4F7FA0).copy(alpha = 0.55f), style = Stroke(0.7f * d, cap = StrokeCap.Round))
                if (oscuro) drawPath(sena, luz.copy(alpha = 0.3f), style = Stroke(1f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.5f * d, 6 * d))))
                listOf(Triple(2.3475, 48.8555, 7f), Triple(2.3565, 48.8518, 4f)).forEach { (lo, la, l) ->
                    val c = g(lo, la)
                    rotate(-24f, c) { drawOval(if (oscuro) Color(0xFF23282C) else Color(0xFFEDE3C8), Offset(c.x - l * d, c.y - 1.6f * d), Size(2 * l * d, 3.2f * d)); drawOval(tinta.copy(alpha = 0.4f), Offset(c.x - l * d, c.y - 1.6f * d), Size(2 * l * d, 3.2f * d), style = Stroke(0.6f * d)) }
                }
                // Los bulevares y las avenidas: los Grandes Bulevares, Saint-Germain, Sébastopol, Saint-Michel, la Gran Armada,
                // Kléber e Iéna, la avenida de la Ópera y la Daumesnil.
                val calles = listOf(
                    linea(2.3245 to 48.870, 2.3317 to 48.8708, 2.348 to 48.8705, 2.3636 to 48.8675, 2.3665 to 48.861, 2.369 to 48.8531),
                    linea(2.3205 to 48.8612, 2.3335 to 48.8555, 2.344 to 48.8525, 2.356 to 48.849, 2.36 to 48.846),
                    linea(2.3495 to 48.858, 2.3515 to 48.8686), linea(2.344 to 48.8535, 2.3405 to 48.838),
                    linea(2.295 to 48.8738, 2.2915 to 48.8655, 2.2885 to 48.8628), linea(2.295 to 48.8738, 2.2945 to 48.8645),
                    linea(2.3327 to 48.8712, 2.3364 to 48.8625), linea(2.369 to 48.8531, 2.39 to 48.84, 2.41 to 48.836),
                    linea(2.3958 to 48.8483, 2.3958 to 48.858), linea(2.3212 to 48.8656, 2.3245 to 48.870))
                calles.forEach { c ->
                    drawPath(c, tinta.copy(alpha = if (oscuro) 0.22f else 0.25f), style = Stroke(1.1f * d, cap = StrokeCap.Round))
                    if (oscuro) drawPath(c, luz.copy(alpha = 0.55f), style = Stroke(1f * d, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f, 4f * d))))
                }
                // Las fortificaciones de París, con sus bastiones en dientes, a los dos lados.
                listOf(linea(2.299 to 48.889, 2.2795 to 48.879, 2.2745 to 48.871, 2.2675 to 48.858, 2.2575 to 48.846, 2.26 to 48.834),
                    linea(2.398 to 48.889, 2.41 to 48.867, 2.411 to 48.853, 2.4125 to 48.844, 2.404 to 48.834)).forEach { f ->
                    drawPath(f, tinta.copy(alpha = if (oscuro) 0.3f else 0.35f), style = Stroke(1.4f * d))
                    aLoLargo(f, 7 * d) { o, t -> val n = Offset(-t.y, t.x); drawPath(Path().apply { moveTo(o.x - t.x * 2 * d, o.y - t.y * 2 * d); lineTo(o.x + n.x * 2.4f * d, o.y + n.y * 2.4f * d); lineTo(o.x + t.x * 2 * d, o.y + t.y * 2 * d) }, tinta.copy(alpha = if (oscuro) 0.3f else 0.35f), style = Stroke(0.8f * d)) }
                }
                // El trazado de la línea bajo las calles, con las dieciocho estaciones de 1900: una raya fina por cada una.
                val estaciones = listOf(2.2826 to 48.8781, 2.2895 to 48.8757, 2.295 to 48.8738, 2.3007 to 48.872, 2.3095 to 48.8691, 2.3143 to 48.8676, 2.3212 to 48.8656, 2.3297 to 48.8645, 2.3364 to 48.8625,
                    2.3409 to 48.8608, 2.347 to 48.8586, 2.3522 to 48.8573, 2.3611 to 48.8551, 2.369 to 48.8531, 2.373 to 48.8448, 2.3871 to 48.8472, 2.3958 to 48.8483, 2.4105 to 48.847)
                estaciones.forEach { (lo, la) -> val o = g(lo, la); drawCircle(if (oscuro) Color(0xFF1A1E22) else Color(0xFFF7F2E6), 1.6f * d, o); drawCircle(TrazosNouveau.HIERRO.copy(alpha = if (oscuro) 0.9f else 0.7f), 1.6f * d, o, style = Stroke(0.7f * d)) }
                // Las estaciones ya pasadas: sus dos faroles encendidos.
                val pts = puntos(tema).map { Offset(it.x * w, it.y * h) }
                val pasadas = (avance * 7f + 0.01f).toInt().coerceAtMost(7)
                for (i in 0 until pasadas) listOf(-4f, 4f).forEach { dx ->
                    val q = pts[i] + Offset(dx * d, -8 * d)
                    drawCircle(Brush.radialGradient(listOf(TrazosNouveau.FAROL.copy(alpha = if (oscuro) 0.7f else 0.45f), Color.Transparent), q, 5 * d), 5 * d, q)
                    drawCircle(TrazosNouveau.FAROL, 1.1f * d, q)
                }
                // De noche, la luna sobre los tejados.
                if (oscuro) { val lu = Offset(w * 0.94f, h * 0.16f); drawCircle(Brush.radialGradient(listOf(Color(0xFFE9E4D2).copy(alpha = 0.25f), Color.Transparent), lu, 20 * d), 20 * d, lu); drawCircle(Color(0xFFE9E4D2), 6 * d, lu) }
                drawRect(tinta.copy(alpha = 0.2f), Offset(2 * d, 2 * d), Size(w - 4 * d, h - 4 * d), style = Stroke(0.6f * d))
            }
            TemaId.GALIA -> {
                // La Galia del levantamiento, vista desde arriba (con la misma proyección que [puntos]): el Loira, que baja del
                // macizo y se vuelve al oeste en Cenabum; el Allier, que pasa al pie de Gergovia; el Cher, el Sena que nace junto a
                // Alesia y el Saona; los volcanes de los arvernos, los montes del Morvan alrededor de Bibracte y los robledales.
                // Roma está lejos, en rojo: el campamento de invierno de las legiones en Agedinco, los de Avárico y Gergovia y, en
                // Alesia, el doble cerco. Las paradas ya pasadas echan humo de día y arden de noche, bajo la luna.
                fun g(lon: Double, lat: Double) = Offset(w * (0.07f + 0.86f * ((lon - 0.9) / 4.7).toFloat()), h * (0.92f - 0.84f * ((lat - 45.3) / 3.0).toFloat()))
                fun rio(vararg p: Pair<Double, Double>): Path = Path().apply {
                    val q = p.map { g(it.first, it.second) }
                    moveTo(q[0].x, q[0].y)
                    for (i in 1 until q.size) { val a = q[i - 1]; val b = q[i]; quadraticTo(a.x + (b.x - a.x) * 0.5f + (b.y - a.y) * 0.18f, a.y + (b.y - a.y) * 0.5f - (b.x - a.x) * 0.18f, b.x, b.y) }
                }
                val pts = puntos(tema).map { Offset(it.x * w, it.y * h) }
                val az = java.util.Random(52)
                val roble = if (oscuro) TrazosGalia.ROBLE_SOMBRA.copy(alpha = 0.95f) else TrazosGalia.ROBLE.copy(alpha = 0.5f)
                val linea = if (oscuro) TrazosGalia.LUNA.copy(alpha = 0.32f) else cs.onSurface.copy(alpha = 0.32f)
                // La tierra: de día, el pasto y el barro al sol; de noche, el mismo campo bajo la luna.
                if (oscuro) drawRect(Brush.verticalGradient(listOf(Color(0xFF0B110C).copy(alpha = 0.8f), Color(0xFF16201A).copy(alpha = 0.6f)), 0f, h), Offset.Zero, size)
                else drawRect(Brush.verticalGradient(listOf(Color(0xFFDCD8B0).copy(alpha = 0.35f), Color(0xFFE6DCB8).copy(alpha = 0.2f)), 0f, h), Offset.Zero, size)
                // Los ríos.
                val rioCol = if (oscuro) Color(0xFF8FA7B3).copy(alpha = 0.5f) else Color(0xFF4F7FA0).copy(alpha = 0.42f)
                listOf(
                    rio(4.2 to 44.9, 4.0 to 45.6, 3.6 to 46.5, 3.16 to 46.99, 2.9 to 47.45, 1.91 to 47.9, 1.3 to 47.55, 0.5 to 47.38) to 1.8f,
                    rio(3.5 to 44.9, 3.3 to 45.55, 3.35 to 46.2, 3.25 to 46.6, 3.12 to 46.98) to 1.3f,
                    rio(2.6 to 45.9, 2.45 to 46.6, 2.15 to 47.15, 1.6 to 47.3, 0.7 to 47.3) to 1.1f,
                    rio(4.72 to 47.49, 4.45 to 47.85, 4.0 to 48.15, 3.3 to 48.45) to 1.3f,
                    rio(6.0 to 48.1, 5.6 to 47.6, 5.05 to 47.05, 4.88 to 46.5, 4.85 to 45.2) to 1.6f,
                ).forEach { (p, ancho) ->
                    drawPath(p, rioCol, style = Stroke(ancho * d, cap = StrokeCap.Round))
                    if (oscuro) drawPath(p, TrazosGalia.LUNA.copy(alpha = 0.18f), style = Stroke(ancho * 0.4f * d, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 9 * d))))
                }
                // Los volcanes de los arvernos, al oeste de Gergovia, y los montes redondos del Morvan y del Auxois.
                val monteCol = if (oscuro) Color(0xFF5A5E4A).copy(alpha = 0.7f) else Color(0xFF8A7650).copy(alpha = 0.45f)
                listOf(2.7 to 45.62, 2.86 to 45.82, 2.78 to 46.0, 2.95 to 45.48).forEach { (lo, la) ->
                    val c = g(lo, la); val s = 6 * d
                    drawPath(Path().apply { moveTo(c.x - s, c.y); lineTo(c.x - s * 0.25f, c.y - s * 0.9f); lineTo(c.x + s * 0.25f, c.y - s * 0.9f); lineTo(c.x + s, c.y) }, monteCol, style = Stroke(1.1f * d, join = StrokeJoin.Round))
                    drawLine(monteCol, Offset(c.x - s * 0.22f, c.y - s * 0.95f), Offset(c.x + s * 0.22f, c.y - s * 0.95f), 1.6f * d)
                }
                listOf(3.85 to 47.18, 4.2 to 47.2, 3.95 to 46.72, 4.35 to 46.98, 4.85 to 47.65).forEach { (lo, la) ->
                    val c = g(lo, la); drawArc(monteCol, 190f, 160f, false, Offset(c.x - 7 * d, c.y - 4 * d), Size(14 * d, 8 * d), style = Stroke(1.1f * d))
                }
                // Los robledales: copas redondas en grupos (el de los carnutos, al norte de Cenabum; la Sologne; los bosques del
                // este y del Morvan).
                listOf(Triple(1.5, 48.15, 7), Triple(2.3, 48.05, 5), Triple(1.8, 47.5, 6), Triple(3.6, 47.6, 5), Triple(5.3, 47.9, 6), Triple(4.6, 46.6, 5), Triple(2.2, 46.3, 5), Triple(3.9, 45.5, 4), Triple(1.2, 46.6, 5)).forEach { (lo, la, n) ->
                    val c0 = g(lo, la)
                    repeat(n) {
                        val c = c0 + Offset((az.nextFloat() - 0.5f) * 26 * d, (az.nextFloat() - 0.5f) * 10 * d)
                        val r = (2.4f + az.nextFloat() * 1.4f) * d
                        drawLine(roble, Offset(c.x, c.y + r * 0.6f), Offset(c.x, c.y + r * 1.6f), 0.8f * d)
                        drawCircle(roble, r, c)
                        if (!oscuro) drawCircle(Color.White.copy(alpha = 0.18f), r * 0.45f, c - Offset(r * 0.3f, r * 0.3f))
                    }
                }
                // De noche: la luna y alguna estrella.
                if (oscuro) {
                    repeat(26) { drawCircle(TrazosGalia.LUNA.copy(alpha = 0.15f + 0.35f * az.nextFloat()), (0.4f + 0.6f * az.nextFloat()) * d, Offset(az.nextFloat() * w, az.nextFloat() * h * 0.45f)) }
                    val lu = Offset(w * 0.95f, h * 0.14f)
                    drawCircle(Brush.radialGradient(listOf(TrazosGalia.LUNA.copy(alpha = 0.25f), Color.Transparent), lu, 22 * d), 22 * d, lu)
                    drawCircle(TrazosGalia.LUNA, 7 * d, lu)
                }
                // Roma, lejos y en rojo: los campamentos de las legiones (Agedinco, Avárico, Gergovia) y su luz de noche.
                val roma = TrazosGalia.ROMA.copy(alpha = if (oscuro) 0.85f else 0.65f)
                listOf(g(3.28, 48.2), pts[2] + Offset(9 * d, 6 * d), g(3.42, 45.5), g(3.2, 45.55)).forEach { c ->
                    if (oscuro) drawCircle(Brush.radialGradient(listOf(Color(0xFFE58A5C).copy(alpha = 0.45f), Color.Transparent), c, 8 * d), 8 * d, c)
                    drawRect(roma, Offset(c.x - 3 * d, c.y - 3 * d), Size(6 * d, 6 * d), style = Stroke(1 * d))
                    drawLine(roma, Offset(c.x, c.y - 3 * d), Offset(c.x, c.y + 3 * d), 0.6f * d); drawLine(roma, Offset(c.x - 3 * d, c.y), Offset(c.x + 3 * d, c.y), 0.6f * d)
                }
                // El doble cerco de Alesia: la línea de dentro y la de fuera, con sus torres.
                val al = pts[6]
                listOf(13f, 19f).forEachIndexed { k, rr ->
                    drawCircle(roma, rr * d, al, style = Stroke((if (k == 0) 1.2f else 1f) * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 1.6f * d))))
                    for (i in 0 until (if (k == 0) 10 else 14)) { val a = 2 * PI * i / (if (k == 0) 10 else 14); drawCircle(roma, 0.9f * d, Offset(al.x + rr * d * cos(a).toFloat(), al.y + rr * d * sin(a).toFloat())) }
                }
                if (oscuro) drawCircle(Brush.radialGradient(listOf(Color(0xFFE58A5C).copy(alpha = 0.25f), Color.Transparent), al, 26 * d), 26 * d, al)
                // Las paradas ya pasadas: el humo de sus casas de día y su hoguera de noche.
                val pasadas = (avance * 7f + 0.01f).toInt().coerceAtMost(7)
                for (i in 0 until pasadas) {
                    val q = pts[i] + Offset(6 * d, -6 * d)
                    if (oscuro) with(TrazosGalia) { hoguera(q, 5f * d, 0.6f + 0.1f * (i % 3)) }
                    else drawPath(Path().apply { moveTo(q.x, q.y); quadraticTo(q.x - 3 * d, q.y - 5 * d, q.x + 1 * d, q.y - 9 * d); quadraticTo(q.x + 4 * d, q.y - 13 * d, q.x, q.y - 16 * d) }, TrazosGalia.HUMO.copy(alpha = 0.7f), style = Stroke(2 * d, cap = StrokeCap.Round))
                }
                drawRect(linea.copy(alpha = linea.alpha * 0.5f), Offset(2 * d, 2 * d), Size(w - 4 * d, h - 4 * d), style = Stroke(0.6f * d))
            }
            TemaId.ARCONTES -> {
                // El ascenso del alma por las siete esferas, dibujado como el diagrama de los ofitas (recurso): abajo a la
                // izquierda la tierra con su lago; alrededor, los siete círculos de los planetas, cada uno con su astro y con la
                // puerta abierta donde cae su parada (las paradas de [puntos] marcan los radios); en las puertas se adivinan las
                // sombras de los guardianes con cara de animal y, más allá del séptimo, la luz de la Ogdóada con sus estrellas
                // fijas y la grieta. Las esferas ya cruzadas se encienden con la luz de la lámpara. De día es el diagrama en
                // tinta sobre papiro; de noche, el mismo sobre el cielo antiguo.
                val e = Offset(w * 0.07f, h * 0.6f)
                val pts = puntos(tema).map { Offset(it.x * w, it.y * h) }
                val linea = if (oscuro) TrazosArcontes.ESFERA.copy(alpha = 0.75f) else cs.onSurface.copy(alpha = 0.32f)
                val encendida = if (oscuro) TrazosArcontes.LLAMA.copy(alpha = 0.7f) else Color(0xFF87590F).copy(alpha = 0.55f)
                val az = java.util.Random(7)
                if (oscuro) drawRect(TrazosArcontes.CIELO_ANTIGUO.copy(alpha = 0.6f), Offset.Zero, size)
                else {
                    drawRect(Color(0xFFE4D3A8).copy(alpha = 0.32f), Offset.Zero, size)
                    var y = 2 * d; while (y < h) { drawLine(Color(0xFF8A6A3A).copy(alpha = 0.05f + 0.04f * az.nextFloat()), Offset(0f, y), Offset(w, y + (az.nextFloat() - 0.5f) * d), 0.6f * d); y += (2.2f + az.nextFloat() * 1.6f) * d }
                }
                repeat(if (oscuro) 70 else 30) { drawCircle((if (oscuro) TrazosArcontes.LINO else cs.onSurface).copy(alpha = 0.1f + 0.3f * az.nextFloat()), (0.4f + 0.7f * az.nextFloat()) * d, Offset(az.nextFloat() * w, az.nextFloat() * h)) }
                // La Ogdóada: la luz de más allá del séptimo círculo, sus estrellas fijas y la grieta.
                val ultimo = (pts[6] - e).getDistance()
                val o8 = Offset(w * 1.04f, h * 0.45f)
                drawCircle(Brush.radialGradient(listOf(TrazosArcontes.GRIETA_HALO.copy(alpha = if (oscuro) 0.32f else 0.2f), Color.Transparent), o8, w * 0.2f), w * 0.2f, o8)
                repeat(14) {
                    val q = Offset(w * (0.9f + az.nextFloat() * 0.1f), az.nextFloat() * h)
                    if ((q - e).getDistance() > ultimo + 6 * d) {
                        val l = (1.2f + az.nextFloat() * 1.4f) * d; val col = (if (oscuro) TrazosArcontes.GRIETA else TrazosArcontes.GRIETA_HALO).copy(alpha = 0.75f)
                        drawLine(col, Offset(q.x - l, q.y), Offset(q.x + l, q.y), 0.6f * d); drawLine(col, Offset(q.x, q.y - l), Offset(q.x, q.y + l), 0.6f * d)
                    }
                }
                with(TrazosArcontes) { grieta(Offset(w * 0.975f, h * 0.12f), Offset(w * 0.985f, h * 0.36f), 0.9f * d, if (oscuro) 1f else 0.7f, 9) }
                // Los siete círculos, con la puerta abierta en su parada y los dos postes de la puerta.
                val cruzadas = (avance * 7f + 0.01f).toInt()
                val astros = listOf(-38f, 30f, -24f, 22f, -16f, 12f, -10f)
                pts.forEachIndexed { i, q ->
                    val rr = (q - e).getDistance()
                    val ang = Math.toDegrees(kotlin.math.atan2((q.y - e.y).toDouble(), (q.x - e.x).toDouble())).toFloat()
                    val medio = Math.toDegrees((10.5f * d / rr).toDouble()).toFloat()
                    val col = if (i < cruzadas) encendida else linea
                    if (i < cruzadas && oscuro) drawArc(TrazosArcontes.LLAMA_HALO.copy(alpha = 0.12f), ang + medio, 360f - 2 * medio, false, Offset(e.x - rr, e.y - rr), Size(2 * rr, 2 * rr), style = Stroke(4 * d))
                    drawArc(col, ang + medio, 360f - 2 * medio, false, Offset(e.x - rr, e.y - rr), Size(2 * rr, 2 * rr), style = Stroke((if (i < cruzadas) 1.3f else 0.9f) * d))
                    listOf(-1f, 1f).forEach { s ->
                        val a = Math.toRadians((ang + s * medio).toDouble())
                        val dir = Offset(cos(a).toFloat(), sin(a).toFloat())
                        drawLine(col, e + dir * (rr - 3.5f * d), e + dir * (rr + 3.5f * d), 1.3f * d, StrokeCap.Round)
                    }
                    // El astro de la esfera: la Luna en creciente, el Sol en el oro de abajo, Marte rojizo; los demás, puntos.
                    val b = Math.toRadians((ang + astros[i]).toDouble())
                    val a = Offset(e.x + rr * cos(b).toFloat(), e.y + rr * sin(b).toFloat())
                    if (a.y in 4 * d..h - 4 * d && a.x < w - 4 * d) when (i) {
                        0 -> { drawCircle(linea.copy(alpha = 0.9f), 3 * d, a); drawCircle(if (oscuro) TrazosArcontes.CIELO_ANTIGUO else Color(0xFFEFE3C2), 2.6f * d, a + Offset(1.4f * d, -0.8f * d)) }
                        3 -> { drawCircle(TrazosArcontes.ORO_DEMIURGO, 2.6f * d, a); for (k in 0 until 8) { val g = k * PI / 4; drawLine(TrazosArcontes.ORO_DEMIURGO, a + Offset(cos(g).toFloat(), sin(g).toFloat()) * (3.6f * d), a + Offset(cos(g).toFloat(), sin(g).toFloat()) * (5.4f * d), 0.8f * d) } }
                        4 -> drawCircle(TrazosArcontes.JASPE_ROJO.copy(alpha = 0.85f), 2.2f * d, a)
                        else -> drawCircle(linea.copy(alpha = 0.9f), (1.5f + 0.15f * i) * d, a)
                    }
                }
                // Las sombras de los guardianes junto a algunas puertas: solo se adivinan, con los hombros que se pierden.
                listOf(1 to 1f, 3 to -1f, 5 to 1f).forEach { (i, lado) ->
                    val q = pts[i]; val cabeza = Offset(q.x + lado * 15 * d, q.y + (if (q.y < h * 0.5f) 3f else -11f) * d)
                    val color = if (oscuro) lerp(TrazosArcontes.SOMBRA, TrazosArcontes.GRIETA_HALO, 0.22f).copy(alpha = 0.9f) else TrazosArcontes.SOMBRA.copy(alpha = 0.18f)
                    with(TrazosArcontes) { sombraArconte(cabeza, 5.5f * d, color, -lado, desvanece = true) }
                }
                // La tierra, el mundo de abajo: el disco de caliza con el lago y su orilla.
                val rt = h * 0.12f
                drawCircle(if (oscuro) lerp(TrazosArcontes.CALIZA_SOMBRA, Color.Black, 0.45f) else TrazosArcontes.CALIZA, rt, e)
                drawOval(if (oscuro) TrazosArcontes.LAGO_HONDO else TrazosArcontes.LAGO.copy(alpha = 0.8f), Offset(e.x - rt * 0.32f, e.y - rt * 0.55f), Size(rt * 0.5f, rt * 0.85f))
                drawCircle(linea, rt, e, style = Stroke(1 * d))
            }
            TemaId.FRENTE -> {
                // La carta estelar impresa: la cuadrícula de sectores con sus rótulos y sus marcas de borde, las estrellas en
                // punto y en cruz, la nebulosa en punteado, los anillos de alcance del radar de Vigía, el gigante de El Anillo
                // y, arriba a la derecha, el espacio del Directorio rayado en rojo alrededor de Albar. Desde Hollín sale la
                // señal de la célula, que llega más lejos con cada día cumplido. De noche es la misma carta bajo la luz verde
                // de la consola.
                val trazo = if (oscuro) TrazosFrente.RADAR else Color(0xFF23252A)
                val linea = trazo.copy(alpha = if (oscuro) 0.13f else 0.11f)
                val ambar = if (oscuro) TrazosFrente.PILOTO_AMBAR else Color(0xFFB85C0F)
                drawRect((if (oscuro) TrazosFrente.PANTALLA else Color(0xFFE8E0CC)).copy(alpha = if (oscuro) 0.55f else 0.35f), Offset.Zero, size)
                for (k in 1 until 8) drawLine(linea, Offset(w * k / 8f, 0f), Offset(w * k / 8f, h), 0.7f * d)
                for (k in 1 until 4) drawLine(linea, Offset(0f, h * k / 4f), Offset(w, h * k / 4f), 0.7f * d)
                for (k in 0..32) { val x = w * k / 32f; val l = (if (k % 4 == 0) 4f else 2f) * d; drawLine(linea, Offset(x, 0f), Offset(x, l), 0.7f * d); drawLine(linea, Offset(x, h), Offset(x, h - l), 0.7f * d) }
                with(TrofeosMas) { for (i in 0 until 8) for (j in 0 until 4) if ((i + j) % 3 == 0) mono("${'A' + i}${j + 1}", w * i / 8f + 2.5f * d, h * j / 4f + 8 * d, 6 * d, trazo.copy(alpha = 0.22f)) }
                val az = java.util.Random(31)
                repeat(if (oscuro) 80 else 60) { drawCircle(trazo.copy(alpha = 0.15f + 0.3f * az.nextFloat()), (0.4f + 0.8f * az.nextFloat()) * d, Offset(az.nextFloat() * w, az.nextFloat() * h)) }
                repeat(9) {
                    val o = Offset(az.nextFloat() * w, az.nextFloat() * h); val l = (1.6f + az.nextFloat() * 1.6f) * d
                    drawLine(trazo.copy(alpha = 0.4f), Offset(o.x - l, o.y), Offset(o.x + l, o.y), 0.6f * d); drawLine(trazo.copy(alpha = 0.4f), Offset(o.x, o.y - l), Offset(o.x, o.y + l), 0.6f * d)
                }
                val neb = Offset(w * 0.4f, h * 0.18f)
                repeat(150) { val a = az.nextDouble() * 2 * PI; val rr = kotlin.math.sqrt(az.nextFloat()); drawCircle(trazo.copy(alpha = 0.14f), 0.6f * d, Offset(neb.x + (cos(a) * rr * w * 0.11f).toFloat(), neb.y + (sin(a) * rr * h * 0.15f).toFloat())) }
                drawOval(trazo.copy(alpha = 0.18f), Offset(neb.x - w * 0.12f, neb.y - h * 0.17f), Size(w * 0.24f, h * 0.34f), style = Stroke(0.6f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.5f * d, 2.5f * d))))
                val vigia = Offset(w * 0.36f, h * 0.76f)
                listOf(16f, 27f, 38f).forEach { rr -> drawCircle(TrazosFrente.RADAR.copy(alpha = if (oscuro) 0.3f else 0.4f), rr * d, vigia, style = Stroke(0.7f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 3 * d)))) }
                val anillo = Offset(w * 0.63f, h * 0.66f)
                drawCircle(trazo.copy(alpha = 0.07f), 15 * d, anillo)
                drawOval(trazo.copy(alpha = 0.22f), Offset(anillo.x - 27 * d, anillo.y - 6 * d), Size(54 * d, 12 * d), style = Stroke(0.8f * d))
                val albar = Offset(w * 0.9f, h * 0.18f); val zona = w * 0.14f
                val rojo = TrazosFrente.PILOTO_ROJO
                clipPath(Path().apply { addOval(Rect(albar, zona)) }) {
                    var x = albar.x - zona * 2; while (x < albar.x + zona) { drawLine(rojo.copy(alpha = if (oscuro) 0.16f else 0.13f), Offset(x, albar.y + zona), Offset(x + zona * 2, albar.y - zona), 0.8f * d); x += 5 * d }
                }
                drawCircle(rojo.copy(alpha = 0.45f), zona, albar, style = Stroke(0.9f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * d, 2 * d, 1.5f * d, 2 * d))))
                drawLine(rojo.copy(alpha = 0.3f), Offset(albar.x - zona * 1.9f, 0f), Offset(albar.x - zona * 0.75f, albar.y + zona * 0.45f), 0.8f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * d, 3 * d)))
                with(TrofeosMas) { mono("DIRECTORIO", albar.x - zona * 0.2f, albar.y + zona + 8 * d, 6 * d, rojo.copy(alpha = 0.5f), android.graphics.Paint.Align.CENTER) }
                val hollin = Offset(w * 0.08f, h * 0.8f)
                for (k in 0 until 4) {
                    val rr = w * (0.08f + 0.22f * k)
                    if (rr <= w * (0.1f + 0.85f * avance)) drawArc(ambar.copy(alpha = 0.32f - k * 0.05f), -80f, 70f, false, Offset(hollin.x - rr, hollin.y - rr), Size(2 * rr, 2 * rr), style = Stroke(1 * d, cap = StrokeCap.Round))
                }
                listOf(Offset(5 * d, 5 * d), Offset(w - 5 * d, 5 * d), Offset(5 * d, h - 5 * d), Offset(w - 5 * d, h - 5 * d)).forEach { o ->
                    drawCircle(linea, 2.4f * d, o, style = Stroke(0.6f * d)); drawLine(linea, Offset(o.x - 4 * d, o.y), Offset(o.x + 4 * d, o.y), 0.6f * d); drawLine(linea, Offset(o.x, o.y - 4 * d), Offset(o.x, o.y + 4 * d), 0.6f * d)
                }
                val esc = Offset(w * 0.03f, h * 0.95f)
                for (k in 0 until 3) drawRect(if (k % 2 == 0) trazo.copy(alpha = 0.3f) else linea, Offset(esc.x + k * 14 * d, esc.y - 2 * d), Size(14 * d, 2 * d))
                with(TrofeosMas) { mono("3 SALTOS", esc.x + 46 * d, esc.y, 6 * d, trazo.copy(alpha = 0.3f)) }
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
            TemaId.BASICO -> drawPath(p, cs.primary.copy(alpha = 0.8f), style = Stroke(2.4f * d, cap = StrokeCap.Round))
            // La línea roja del trámite, como cinta de señalización.
            TemaId.DISTOPIA -> { drawPath(p, Color(0xFF141414).copy(alpha = 0.35f), style = Stroke(4 * d)); drawPath(p, Color(0xFFC8102E), style = Stroke(2 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10 * d, 4 * d)))) }
            // El camino: los cables del trolebús en la ciudad; en el cartel, la banda de ultramar con la línea de magenta corrida.
            TemaId.PSICODELIA -> with(io.github.ruddy2003.vestigium.ui.PantallasPsicodelia) { caminoCampana(p, cs.background.luminance() < 0.5f) }
            // La línea del metro en el plano: la zanja clara, el trazo de hierro verde y el riel de oro que corre por dentro;
            // de noche, con su brillo.
            TemaId.NOUVEAU -> {
                val oscuro = cs.background.luminance() < 0.5f
                drawPath(p, (if (oscuro) TrazosNouveau.LUZ else Color.White).copy(alpha = if (oscuro) 0.18f else 0.55f), style = Stroke(7 * d, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(p, if (oscuro) lerp(TrazosNouveau.HIERRO, Color.White, 0.25f) else TrazosNouveau.HIERRO, style = Stroke(3.2f * d, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(p, TrazosNouveau.ORO, style = Stroke(0.9f * d, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * d, 3 * d))))
            }
            // El camino de tierra por donde corre el grito: la huella ancha del barro y, a trechos, las pisadas de los caballos.
            TemaId.GALIA -> {
                val oscuro = cs.background.luminance() < 0.5f
                val tierra = if (oscuro) Color(0xFFB89A62) else Color(0xFF7A5E38)
                drawPath(p, tierra.copy(alpha = if (oscuro) 0.22f else 0.2f), style = Stroke(5 * d, cap = StrokeCap.Round))
                drawPath(p, tierra.copy(alpha = 0.75f), style = Stroke(1.1f * d, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * d, 3 * d))))
                aLoLargo(p, 30 * d, 15 * d) { o, t ->
                    val n = Offset(-t.y, t.x)
                    listOf(-1f, 1f).forEach { s ->
                        val c = o + n * (s * 1.8f * d) + t * (s * 1.2f * d)
                        val ang = Math.toDegrees(kotlin.math.atan2(t.y.toDouble(), t.x.toDouble())).toFloat()
                        drawArc(tierra.copy(alpha = 0.9f), ang + 90f, 180f, false, Offset(c.x - 1.3f * d, c.y - 1.3f * d), Size(2.6f * d, 2.6f * d), style = Stroke(0.8f * d))
                    }
                }
            }
            // El camino del alma: un hilo de luz de la grieta, blanco violeta de noche y violeta de día, con su halo y una
            // chispa a trechos.
            TemaId.ARCONTES -> {
                val oscuro = cs.background.luminance() < 0.5f
                val luz = if (oscuro) TrazosArcontes.GRIETA else cs.tertiary
                drawPath(p, TrazosArcontes.GRIETA_HALO.copy(alpha = if (oscuro) 0.24f else 0.16f), style = Stroke(5 * d, cap = StrokeCap.Round))
                drawPath(p, luz.copy(alpha = 0.9f), style = Stroke(1.3f * d, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(7 * d, 2.5f * d, 0.1f * d, 2.5f * d))))
                aLoLargo(p, 34 * d, 17 * d) { o, _ -> drawCircle(TrazosArcontes.GRIETA_HALO.copy(alpha = 0.35f), 2.6f * d, o); drawCircle(luz, 1.1f * d, o) }
            }
            // La ruta de salto impresa en la carta: la franja tenue del corredor, los puntos ámbar del trazado y, cada tanto,
            // el ángulo del vector de salto que dice hacia dónde se va.
            TemaId.FRENTE -> {
                val ambar = if (cs.background.luminance() < 0.5f) TrazosFrente.PILOTO_AMBAR else Color(0xFFB85C0F)
                drawPath(p, ambar.copy(alpha = 0.14f), style = Stroke(6 * d))
                drawPath(p, ambar, style = Stroke(1.9f * d, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f * d, 4.4f * d))))
                aLoLargo(p, 36 * d, 18 * d) { o, t ->
                    val n = Offset(-t.y, t.x)
                    listOf(1f, -1f).forEach { s -> drawLine(ambar, Offset(o.x - t.x * 3.2f * d + n.x * s * 2.6f * d, o.y - t.y * 3.2f * d + n.y * s * 2.6f * d), Offset(o.x + t.x * 0.8f * d, o.y + t.y * 0.8f * d), 1.1f * d, StrokeCap.Round) }
                }
            }
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
            TemaId.BASICO -> {
                drawCircle(col, r * 0.8f, c)
                if (meta) drawCircle(cs.primary, r * 1.2f, c, style = Stroke(1.2f * d))
            }
            // Una ventanilla de control: caseta cuadrada con su número.
            TemaId.DISTOPIA -> { drawRect(col, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r)); drawRect(Color(0xFF141414).copy(alpha = 0.6f), Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(0.8f * d)); drawRect(Color(0xFFF2EFE6).copy(alpha = 0.8f), Offset(c.x - r * 0.5f, c.y - r * 0.6f), Size(r, r * 0.5f)) }
            // Cada parada, el disco del día con la etiqueta de su color (la ciudad) o el disco hecho sol (el cartel); la meta, con
            // un aro más.
            TemaId.PSICODELIA -> with(io.github.ruddy2003.vestigium.ui.PantallasPsicodelia) { paradaCampana(c, r, col, meta, cs.background.luminance() < 0.5f) }
            // La estación: el letrero de lava esmaltada en óvalo, con su marco de hierro verde y su rizo; en la meta, sobre él,
            // los dos faroles rojo anaranjado.
            TemaId.NOUVEAU -> {
                val oscuro = cs.background.luminance() < 0.5f
                val hierro = if (oscuro) lerp(TrazosNouveau.HIERRO, Color.White, 0.3f) else TrazosNouveau.HIERRO
                drawOval(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), Offset(c.x - r * 1.15f + 0.8f * d, c.y - r * 0.75f + 1.2f * d), Size(r * 2.3f, r * 1.5f))
                drawOval(col, Offset(c.x - r * 1.15f, c.y - r * 0.75f), Size(r * 2.3f, r * 1.5f))
                drawOval(hierro, Offset(c.x - r * 1.15f, c.y - r * 0.75f), Size(r * 2.3f, r * 1.5f), style = Stroke(1.2f * d))
                drawOval(hierro.copy(alpha = 0.5f), Offset(c.x - r * 0.8f, c.y - r * 0.45f), Size(r * 1.6f, r * 0.9f), style = Stroke(0.6f * d))
                drawLine(hierro, Offset(c.x - r * 0.5f, c.y), Offset(c.x + r * 0.5f, c.y), 0.9f * d, StrokeCap.Round)
                if (meta) listOf(-1f, 1f).forEach { s ->
                    val pie = Offset(c.x + s * r * 0.9f, c.y - r * 0.7f)
                    val farol = Offset(c.x + s * r * 0.75f, c.y - r * 2.1f)
                    drawPath(Path().apply { moveTo(pie.x, pie.y); quadraticTo(pie.x + s * r * 0.3f, c.y - r * 1.5f, farol.x, farol.y + r * 0.35f) }, hierro, style = Stroke(1.1f * d, cap = StrokeCap.Round))
                    drawCircle(Brush.radialGradient(listOf(TrazosNouveau.FAROL.copy(alpha = 0.55f), Color.Transparent), farol, r * 1.2f), r * 1.2f, farol)
                    drawOval(TrazosNouveau.FAROL, Offset(farol.x - r * 0.28f, farol.y - r * 0.38f), Size(r * 0.56f, r * 0.76f))
                }
            }
            // El oppidum de cada parada visto desde arriba: la meseta con su muralla en anillo y las estacas de la empalizada; en
            // la meta, más grande, con el torques encima.
            TemaId.GALIA -> {
                val oscuro = cs.background.luminance() < 0.5f
                val tinta = if (oscuro) TrazosGalia.LUNA.copy(alpha = 0.8f) else cs.onSurface.copy(alpha = 0.7f)
                drawCircle(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.15f), r * 0.95f, c + Offset(0.8f * d, 1.2f * d))
                drawCircle(col, r * 0.82f, c)
                drawCircle(tinta, r * 0.82f, c, style = Stroke(1.1f * d))
                for (k in 0 until 14) { val a = 2 * PI * k / 14; drawLine(tinta, Offset(c.x + r * 0.82f * cos(a).toFloat(), c.y + r * 0.82f * sin(a).toFloat()), Offset(c.x + r * 1.08f * cos(a).toFloat(), c.y + r * 1.08f * sin(a).toFloat()), 0.8f * d) }
                drawPath(Path().apply { moveTo(c.x - r * 0.38f, c.y + r * 0.2f); lineTo(c.x, c.y - r * 0.32f); lineTo(c.x + r * 0.38f, c.y + r * 0.2f); close() }, tinta.copy(alpha = 0.75f))
                if (meta) with(TrazosGalia) { torques(Offset(c.x, c.y - r * 1.75f), r * 0.55f, r * 0.18f) }
            }
            // Cada parada es la puerta de una esfera: el disco con el aro de su círculo, abierto arriba; la meta es la
            // Ogdóada, con su halo y los ocho puntos de luz alrededor.
            TemaId.ARCONTES -> {
                val oscuro = cs.background.luminance() < 0.5f
                val tinta = cs.onSurface.copy(alpha = 0.6f)
                if (meta) {
                    drawCircle(Brush.radialGradient(listOf(TrazosArcontes.GRIETA_HALO.copy(alpha = 0.4f), Color.Transparent), c, r * 2.3f), r * 2.3f, c)
                    for (k in 0 until 8) { val a = k * PI / 4 - PI / 2; drawCircle(if (oscuro) TrazosArcontes.GRIETA else cs.tertiary, r * 0.13f, Offset(c.x + r * 1.5f * cos(a).toFloat(), c.y + r * 1.5f * sin(a).toFloat())) }
                }
                drawCircle(col, r * 0.82f, c)
                drawCircle(tinta, r * 0.82f, c, style = Stroke(0.7f * d))
                drawArc(tinta, -60f, 300f, false, Offset(c.x - r * 1.18f, c.y - r * 1.18f), Size(r * 2.36f, r * 2.36f), style = Stroke(0.9f * d))
            }
            // Cada parada es un mundo impreso en la carta, con su lado de noche y la mira de llegada alrededor; la meta es Albar,
            // la capital del Directorio, en mármol con su anillo de bronce.
            TemaId.FRENTE -> {
                val trazo = cs.onSurface.copy(alpha = 0.6f)
                if (meta) {
                    drawCircle(TrazosFrente.MARMOL, r * 0.95f, c)
                    drawArc(Color.Black.copy(alpha = 0.18f), -50f, 180f, true, Offset(c.x - r * 0.95f, c.y - r * 0.95f), Size(r * 1.9f, r * 1.9f))
                    drawOval(TrazosFrente.BRONCE, Offset(c.x - r * 1.6f, c.y - r * 0.36f), Size(r * 3.2f, r * 0.72f), style = Stroke(1.4f * d))
                    drawCircle(TrazosFrente.BRONCE, r * 0.95f, c, style = Stroke(0.7f * d))
                } else {
                    drawCircle(col, r * 0.78f, c)
                    drawArc(Color.Black.copy(alpha = 0.3f), -50f, 180f, true, Offset(c.x - r * 0.78f, c.y - r * 0.78f), Size(r * 1.56f, r * 1.56f))
                }
                val rm = r * (if (meta) 1.9f else 1.4f)
                drawCircle(trazo, rm, c, style = Stroke(0.7f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * d, 2 * d))))
                for (k in 0 until 4) { val a = k * PI / 2; drawLine(trazo, Offset(c.x + (rm * 0.8f * cos(a)).toFloat(), c.y + (rm * 0.8f * sin(a)).toFloat()), Offset(c.x + (rm * 1.25f * cos(a)).toFloat(), c.y + (rm * 1.25f * sin(a)).toFloat()), 0.8f * d) }
            }
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







}
