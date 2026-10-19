package io.github.ruddy2003.vestigium.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    coerceInputValues = true
    isLenient = true
}

const val SCHEMA_V = 9

/** Un día. Los campos opcionales quedan en null cuando no se marcaron: nunca se rellenan con promedios. */
@Serializable
data class Dia(
    val fecha: String,
    val schema_v: Int = SCHEMA_V,
    val origen: String = "app",
    val valencia: Int? = null,
    val activacion: Int? = null,
    val dolor: Int? = null,
    val ctx: Map<String, Boolean> = emptyMap(),
    val hitos: List<String> = emptyList(),
    val flow: String? = null,
    val sdt_necesidad: String? = null,
    val apreciacion: String? = null,
    val sueno_recordado: Boolean = false,
    val sueno_valencia: Int? = null,
    val sueno_lucidez: String? = null,
    val sueno_vividez: String? = null,
    val sueno_tipo: String? = null,
    val sueno_temas: List<String> = emptyList(),
    val sueno_texto: String = "",
    val sueno_calidad: Int? = null,
    val mejor_momento: String = "",
    val peor_momento: String = "",
    val impulso: Int? = null,
    val recaida: Boolean? = null,
    val detonantes: List<String> = emptyList(),
    val medicamentos: List<String> = emptyList(),
    val prediccion_manana: Int? = null,
    val habitos: Map<String, Boolean> = emptyMap(),
    val nota: String = "",
    val dia_dificil: Boolean = false,
    /** Foto elegida para representar el día (content:// de la galería). */
    val foto: String? = null,
    val retroactivo: Boolean = false,
    val ts_creado: String? = null,
    val ts_editado: String? = null,
)

/** Un momento capturado al instante: pensamiento, emoción o muestreo de ánimo, con hora y lugar. */
@Serializable
data class Momento(
    val id: String,
    val ts: String,
    val fecha: String,
    val tipo: String,
    val texto: String = "",
    val intensidad: Int? = null,
    val valencia: Int? = null,
    val activacion: Int? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val precision_m: Float? = null,
    val lugar: String? = null,
    val origen: String = "app",
    val aviso_ts: String? = null,
    /** Nivel de ruido aproximado (dB, sin calibrar) medido 1.5 s al registrar; nunca se guarda audio. */
    val ruido_db: Double? = null,
    /** Luz ambiental (lux) al registrar. */
    val luz_lux: Double? = null,
)

@Serializable
data class Ingreso(
    val id: String,
    val fecha: String,
    val hora: String,
    val monto: Double,
    val categoria: String,
    val nota: String = "",
    /** Ingreso que se repite (sueldo, beca, renta): solo informativo, no se duplica solo. */
    val fijo: Boolean = false,
) {
    val dia: String get() = if (hora.length >= 2 && (hora.take(2).toIntOrNull() ?: 12) < 5) Fechas.sumar(fecha, -1) else fecha
}

@Serializable
data class Gasto(
    val id: String,
    val fecha: String,
    val hora: String,
    val monto: Double,
    val categoria: String,
    val imprevisto: Boolean = false,
    val nota: String = "",
    val lat: Double? = null,
    val lon: Double? = null,
) {
    /** Día del diario al que pertenece: un gasto de madrugada (antes de las 5:00) cuenta para el día anterior, como los momentos. */
    val dia: String get() = if (hora.length >= 2 && (hora.take(2).toIntOrNull() ?: 12) < 5) Fechas.sumar(fecha, -1) else fecha
}

@Serializable
data class Respuesta(
    val id: String,
    val fecha: String,
    val ts: String,
    val instrumento: String,
    val items: Map<String, Double> = emptyMap(),
    val puntajes: Map<String, Double> = emptyMap(),
)

@Serializable
data class Reaccion(
    val id: String,
    val ts: String,
    val fecha: String,
    val ensayos_ms: List<Int> = emptyList(),
    val media_ms: Double? = null,
    val mediana_ms: Double? = null,
    val lapsos: Int = 0,
    val anticipaciones: Int = 0,
)

@Serializable
data class Lugar(
    val id: String,
    val nombre: String,
    val lat: Double,
    val lon: Double,
    val radio_m: Int = 120,
    val tipo: String = "otro",
)

@Serializable
data class Visita(
    val id: String,
    val lugar: String,
    val entrada: String,
    val salida: String? = null,
)

@Serializable
data class Habito(val id: String, val nombre: String, val activo: Boolean = true, val desde: String = "")

@Serializable
data class SaludDia(
    val fecha: String,
    val sueno_min: Int? = null,
    val sueno_profundo_min: Int? = null,
    val sueno_rem_min: Int? = null,
    val sueno_ligero_min: Int? = null,
    val sueno_despierto_min: Int? = null,
    val sueno_inicio: String? = null,
    val sueno_fin: String? = null,
    val fc_media: Double? = null,
    val fc_min: Double? = null,
    val fc_max: Double? = null,
    val fc_reposo: Double? = null,
    val hrv_rmssd: Double? = null,
    val pasos: Long? = null,
    val spo2: Double? = null,
    val peso_kg: Double? = null,
    val grasa_pct: Double? = null,
    val resp_rpm: Double? = null,
    val temp_c: Double? = null,
    val temp_piel_delta: Double? = null,
    val pa_sistolica: Double? = null,
    val pa_diastolica: Double? = null,
    val distancia_km: Double? = null,
    val cal_activas: Double? = null,
    val cal_totales: Double? = null,
    val pisos: Double? = null,
    val vo2max: Double? = null,
    val agua_ml: Double? = null,
    val ts: String? = null,
)

@Serializable
data class PantallaDia(
    val fecha: String,
    val total_min: Int = 0,
    val desbloqueos: Int = 0,
    val primer_uso: String? = null,
    val ultimo_uso: String? = null,
    val por_app_min: Map<String, Int> = emptyMap(),
)

/**
 * Una carta. Sin destinatario es para ti en el futuro: queda sellada hasta [abrir_en] y no se puede leer antes.
 * Con destinatario ([para], una @persona) es una carta a alguien: se guarda abierta en la biblioteca de cartas.
 */
@Serializable
data class Carta(
    val id: String,
    val escrita: String,
    val abrir_en: String,
    val texto: String,
    val abierta: Boolean = false,
    /** A quién va, sin @ («ana»). Vacío: a ti mismo, en el futuro. */
    val para: String = "",
    /** Hora a la que se escribió (HH:mm). */
    val hora: String = "",
    /** Tema con el que se escribió. */
    val tema: String = "",
    /** Ciudad donde se escribió, si se sabía. */
    val lugar: String = "",
    /** Día en que se abrió (AAAA-MM-DD). */
    val abierta_el: String? = null,
)

/** Para ti en el futuro (la que se sella). */
val Carta.alFuturo: Boolean get() = para.isBlank()
/** Sellada: para ti, sin abrir y todavía no llega su fecha. No se puede leer ni aparece en búsquedas. */
fun Carta.sellada(hoy: String = Fechas.diaLogico()): Boolean = alFuturo && !abierta && abrir_en > hoy
/** Ya llegó su fecha pero no la has abierto. */
fun Carta.lista(hoy: String = Fechas.diaLogico()): Boolean = alFuturo && !abierta && abrir_en <= hoy
/** Se puede leer: las de otras personas siempre; las tuyas, ya abiertas. */
val Carta.legible: Boolean get() = !alFuturo || abierta

/** Una mascota con la que vives o que es tuya. */
@Serializable
data class Mascota(val nombre: String, val especie: String = "")

/** Alguien a quien ves seguido: [nombre] como lo etiquetas en el diario (sin @), y qué es para ti. */
@Serializable
data class PersonaCercana(val nombre: String, val relacion: String = "")

/**
 * Lo grande de tu vida, de la pantalla «Conocerte»: con quién vives, a quién ves, a qué te dedicas.
 * Sirve para los análisis (los días que ves a tu familia, los que pasas fuera de tu ciudad…). Vacío = no lo dijiste.
 */
@Serializable
data class Perfil(
    val nombre: String = "",
    /** AAAA-MM-DD. */
    val nacimiento: String = "",
    /** Ciudad donde vives, con su país (código de dos letras) y su punto en el mapa. */
    val ciudad: String = "",
    val pais: String = "",
    val ciudad_lat: Double? = null,
    val ciudad_lon: Double? = null,
    /** Con quién vives: solo, pareja, hijos, padres, hermanos, abuelos, familia, companeros, amistades. */
    val vive_con: List<String> = emptyList(),
    /** Nombres (como @etiquetas) de quienes viven contigo. */
    val convivientes: List<String> = emptyList(),
    val mascotas: List<Mascota> = emptyList(),
    /** Las personas que más ves. */
    val cercanas: List<PersonaCercana> = emptyList(),
    val pareja: String = "",
    val ocupacion: List<String> = emptyList(),
    val ocupacion_detalle: String = "",
    val horario: String = "",
    val cronotipo: String = "",
    val ejercicio: String = "",
    val alcohol: String = "",
    val tabaco: String = "",
    val cafe: String = "",
    /** Lo que esté pasando en tu vida ahora (una mudanza, un trabajo nuevo, un duelo…). */
    val momento: String = "",
)

/** Un cambio en «Conocerte»: qué campo, cómo estaba y cómo quedó, y cuándo. */
@Serializable
data class CambioPerfil(val fecha: String, val campo: String, val antes: String, val despues: String)

/** Un test político: las respuestas (1 a 5) y la posición en cada eje (−100 a 100). */
@Serializable
data class TestPolitico(val fecha: String, val respuestas: Map<String, Int>, val ejes: Map<String, Double>)

@Serializable
data class Experimento(
    val id: String,
    val nombre: String,
    val accion: String,
    val resultado: String,
    val inicio: String,
    val semanas: Int,
    val plan: List<Boolean>,
    val activo: Boolean = true,
)

@Serializable
data class Config(
    val habitos: List<Habito> = emptyList(),
    val categorias: List<String> = Catalogo.CATEGORIAS,
    val equivalencias: Map<String, String> = emptyMap(),
    val muestreos_por_dia: Int = 3,
    val ventana_inicio: Int = 10,
    val ventana_fin: Int = 21,
    val hora_sueno: Int = 8,
    val hora_diario: Int = 22,
    val avisos: Boolean = true,
    val discreto: Boolean = false,
    val seguimiento_privado: Boolean = false,
    val bloqueo: Boolean = false,
    val muestreo_ubicacion: Boolean = true,
    val carpeta_respaldo: String? = null,
    val disparos: Map<String, Long> = emptyMap(),
    val plan_muestreo: Map<String, List<Long>> = emptyMap(),
    val ultimo_sync: String? = null,
    val bienvenida_vista: Boolean = false,
    val clima: Boolean = true,
    val drive: Boolean = false,
    val drive_carpeta: String? = null,
    val drive_ultimo: String? = null,
    val escalada_escala: String = "V",
    val gimnasios: List<String> = emptyList(),
    val medir_ruido: Boolean = false,
    val medir_luz: Boolean = true,
    val whatsapp_yo: String = "",
    val escalas_hora: Int = 19,
    val escalas_insistir: Boolean = true,
    /** Id del tema (ver TemaId): romano, ciber, heleno, medieval, gotico, espacio, andalus, botanico… */
    val tema: String = "romano",
    /** Escala del texto (1 = normal). */
    val letra: Float = 1f,
    /** Después de las 22:00 la app baja el brillo y entibia los colores. */
    val nocturno: Boolean = true,
    /** Avisos de datos que faltan que se ignoraron: clave → fecha del día ignorado, o «siempre». */
    val avisos_ignorados: Map<String, String> = emptyMap(),
    /** Tarjetas del tablero de Día, en orden. Vacío = el orden de siempre. */
    val tablero: List<String> = emptyList(),
    /** Secciones de Día plegadas. */
    val plegadas: List<String> = emptyList(),
    /** Qué cifra muestra el widget de una cifra. */
    val widget_cifra: String = "racha",
    /** Última vez que se escribió el respaldo en la carpeta elegida. */
    val respaldo_ultimo: String? = null,
    /** Claro u oscuro: «sistema» sigue al teléfono, «claro» u «oscuro» lo fijan dentro de la app. */
    val modo: String = "sistema",
    /** Emblemas que se mueven, transiciones y barras que crecen. */
    val animaciones: Boolean = true,
    /** Una capa tenue que se mueve detrás de todo: estrellas, nieve, polvo de vitral, luciérnagas… */
    val fondo_vivo: Boolean = true,
    /** Vibraciones al tocar y al guardar. */
    val vibracion: Boolean = true,
    /** Sonidos del tema al guardar y al cambiar de pantalla (respetan el modo silencio). */
    val sonidos: Boolean = false,
    /** El ícono de la app en el teléfono cambia con el tema. */
    val icono_tema: Boolean = false,
    /** Las notificaciones suenan con el sonido solemne del tema (campanas, órgano, synthwave…). */
    val sonido_avisos: Boolean = true,
    /** Frases del día a las que se les dio «me gusta». */
    val frases_guardadas: List<FraseGuardada> = emptyList(),
    /** Categorías de ingreso. */
    val categorias_ingreso: List<String> = Catalogo.CATEGORIAS_INGRESO,
    /** Ciudades que añadiste a mano (las de tus ubicaciones se calculan solas). */
    val ciudades_visitadas: List<CiudadVisitada> = emptyList(),
    /** Países rascados a mano en el mapa, y los que tapaste aunque tus ubicaciones digan que estuviste (un vuelo, una frontera). */
    val paises_rascados: List<String> = emptyList(),
    val paises_tapados: List<String> = emptyList(),
    /** Patrimonio Mundial: los que marcaste a mano y los que quitaste aunque pasaste cerca. */
    val unesco_marcados: List<SitioMarcado> = emptyList(),
    val unesco_descartados: List<Int> = emptyList(),
    /** Al escribir el diario aparece el teclado del tema en lugar del del teléfono. */
    val teclado_tema: Boolean = true,
    /** Al cambiar de tema, el fondo de pantalla del teléfono cambia también. */
    val fondo_telefono_auto: Boolean = false,
    /** Dónde poner el fondo del teléfono: «ambas», «inicio» o «bloqueo». */
    val fondo_telefono_donde: String = "ambas",
    /** «Conocerte»: lo grande de tu vida, y la historia de sus cambios. */
    val perfil: Perfil = Perfil(),
    val perfil_historial: List<CambioPerfil> = emptyList(),
    /** Ya se mostró «Conocerte» (llenado u omitido). */
    val conocer_visto: Boolean = false,
    /** Tests políticos, uno por año. */
    val politica: List<TestPolitico> = emptyList(),
    /** Cuánto más arriba va el teclado del tema (dp extra sobre el borde), por si el teléfono lo deja muy abajo. */
    val teclado_alto: Int = 0,
    /** Fotos con lugar: qué aparatos son tuyos (marca y modelo). Vacío = aún no eliges y cuentan todos. */
    val fotos_aparatos: List<String> = emptyList(),
    /** Aparatos cuyas fechas no son de fiar (cámara con la hora mal puesta): sus fotos dan lugar pero no día. */
    val fotos_sin_fecha: List<String> = emptyList(),
    /** Fotos raras que revisaste: id de la foto → true (cuenta) o false (no cuenta). */
    val fotos_revisadas: Map<String, Boolean> = emptyMap(),
    /** En Takeout, contar también lo que no subiste tú (álbumes compartidos, fotos de tu pareja). */
    val fotos_compartidas: Boolean = false,
    /** Para las referencias de salud: «hombre», «mujer» o vacío (se muestran ambas). */
    val salud_sexo: String = "",
)

/** Una frase del día guardada: el texto tal como se mostró, el tema donde apareció y cuándo se guardó. */
@Serializable
data class FraseGuardada(val texto: String, val original: String? = null, val autor: String, val tema: String, val fecha: String)

/** Llamadas y SMS del día: solo cuántos, cuánto duraron y con cuántas personas distintas. Nunca números, nombres ni textos. */
@Serializable
data class ComunicacionDia(
    val fecha: String,
    val entrantes: Int = 0,
    val salientes: Int = 0,
    val perdidas: Int = 0,
    val rechazadas: Int = 0,
    val min_hablado: Double = 0.0,
    val llamada_max_min: Double = 0.0,
    val sms_recibidos: Int = 0,
    val sms_enviados: Int = 0,
    val contactos: Int = 0,
    val primera: String? = null,
    val ultima: String? = null,
)

/** Lo que el teléfono percibe cada 15 minutos: wifi, audio conectado, carga, batería y luz. */
@Serializable
data class ConexionDia(
    val fecha: String,
    val muestras: Int = 0,
    val wifi_min: Map<String, Int> = emptyMap(),
    val sin_wifi_min: Int = 0,
    val audio_min: Map<String, Int> = emptyMap(),
    val dispositivos_min: Map<String, Int> = emptyMap(),
    val musica_min: Int = 0,
    val cargando_min: Int = 0,
    val carga_noche_inicio: String? = null,
    val carga_noche_fin: String? = null,
    val bateria_suma: Double = 0.0,
    val bateria_min: Int? = null,
    val luz_suma: List<Double> = List(24) { 0.0 },
    val luz_n: List<Int> = List(24) { 0 },
    val luz_max: Double? = null,
    val luz_alta_min: Int = 0,
)

/** Subrayado o nota de Kindle (archivo My Clippings.txt). */
@Serializable
data class Subrayado(
    val id: String,
    val libro: String,
    val autor: String = "",
    val tipo: String = "subrayado",
    val ubicacion: String = "",
    val pagina: Int? = null,
    val ts: String? = null,
    val fecha: String? = null,
    val texto: String = "",
)

/** Lo que hiciste en Google: videos vistos y búsquedas (Google Takeout, Mi actividad). */
@Serializable
data class Huella(
    val id: String,
    val ts: String,
    val fecha: String,
    val producto: String,
    val accion: String,
    val titulo: String = "",
    val canal: String = "",
)

@Serializable
data class HuellasDia(
    val fecha: String,
    val yt_vistos: Int = 0,
    val yt_busquedas: Int = 0,
    val busquedas: Int = 0,
    val visitas: Int = 0,
    val otras: Int = 0,
    val primera: String? = null,
    val ultima: String? = null,
    val canales: Map<String, Int> = emptyMap(),
)

/** Un chat de WhatsApp en un día: solo conteos y tiempos, nunca el contenido. */
@Serializable
data class ChatDia(
    val id: String,
    val fecha: String,
    val chat: String,
    val grupo: Boolean = false,
    val mios: Int = 0,
    val otros: Int = 0,
    val palabras_mias: Int = 0,
    val palabras_otros: Int = 0,
    val multimedia: Int = 0,
    val primero: String? = null,
    val ultimo: String? = null,
    val resp_mia_min: Double? = null,
    val resp_otros_min: Double? = null,
    val inicie: Int = 0,
    val conversaciones: Int = 0,
)

/** Lo que había afuera: clima, luz y aire del lugar donde estuviste (Open-Meteo), y la Luna, calculada en el teléfono. */
@Serializable
data class EntornoDia(
    val fecha: String,
    val lat: Double? = null,
    val lon: Double? = null,
    val t_max: Double? = null,
    val t_min: Double? = null,
    val t_media: Double? = null,
    val sensacion_max: Double? = null,
    val precip_mm: Double? = null,
    val horas_lluvia: Int? = null,
    val nubes_pct: Double? = null,
    val humedad_pct: Double? = null,
    val presion_hpa: Double? = null,
    val viento_max: Double? = null,
    val uv_max: Double? = null,
    val codigo: Int? = null,
    val amanecer: String? = null,
    val atardecer: String? = null,
    val luz_min: Int? = null,
    val sol_min: Int? = null,
    val pm25: Double? = null,
    val pm10: Double? = null,
    val aqi_us: Int? = null,
    val luna_fase: Double? = null,
    val luna_ilum: Double? = null,
    val completo: Boolean = false,
    val ts: String? = null,
)

/** Cuántas fotos tomaste. Solo se guardan cifras e identificadores; las fotos no salen de tu galería. */
@Serializable
data class FotosDia(
    val fecha: String,
    val fotos: Int = 0,
    val capturas: Int = 0,
    val otras: Int = 0,
    val videos: Int = 0,
    val primera: String? = null,
    val ultima: String? = null,
)

@Serializable
data class AgendaDia(
    val fecha: String,
    val eventos: Int = 0,
    val todo_el_dia: Int = 0,
    val min_ocupado: Int = 0,
    val primero: String? = null,
    val ultimo: String? = null,
    val titulos: List<String> = emptyList(),
)

/** Notificaciones recibidas: solo cuántas, de qué app y a qué hora. Nunca el texto. */
@Serializable
data class NotifDia(
    val fecha: String,
    val total: Int = 0,
    val por_app: Map<String, Int> = emptyMap(),
    val por_hora: List<Int> = List(24) { 0 },
)

/** Una canción que empezó a sonar en el teléfono (título y artista). */
@Serializable
data class Escucha(
    val id: String,
    val ts: String,
    val fecha: String,
    val titulo: String,
    val artista: String = "",
    val album: String = "",
    val app: String = "",
    val duracion_s: Int? = null,
    /** Milisegundos realmente escuchados (solo en el historial importado de Spotify). */
    val ms: Int? = null,
    val saltada: Boolean? = null,
    /** notificacion (lo que sonó en el teléfono), spotify (historial importado) o spotify_api (conexión directa). */
    val fuente: String = "notificacion",
    /** Desde dónde sonó, solo con la conexión directa: playlist, album, artist o collection (tus «me gusta»). */
    val contexto: String = "",
)

/** Minutos por tipo de movimiento que detecta el teléfono: a pie, corriendo, en bici, en vehículo, quieto. */
@Serializable
data class ActividadDia(
    val fecha: String,
    val minutos: Map<String, Int> = emptyMap(),
)

/** Sesión de ejercicio que registró el reloj en Health Connect. */
@Serializable
data class Sesion(
    val id: String,
    val fecha: String,
    val inicio: String,
    val fin: String,
    val tipo: Int,
    val tipo_nombre: String,
    val titulo: String = "",
    val min: Int = 0,
    val fc_media: Double? = null,
    val fc_max: Double? = null,
)

/** Un intento de bloque o vía dentro de una sesión de escalada. */
@Serializable
data class Bloque(
    val grado: String,
    val estilo: String = "encadenado",
    val intentos: Int = 1,
    val angulo: String = "",
    val rasgos: List<String> = emptyList(),
    val nota: String = "",
)

@Serializable
data class Ascenso(
    val id: String,
    val fecha: String,
    val lugar: String = "",
    val modalidad: String = "boulder",
    val inicio: String? = null,
    val min: Int? = null,
    val bloques: List<Bloque> = emptyList(),
    val energia: Int? = null,
    val piel: Int? = null,
    val esfuerzo: Int? = null,
    val satisfaccion: Int? = null,
    val dolor: String = "",
    val nota: String = "",
    val sesion_reloj: String? = null,
)

/** Libro, película, serie o juego. */
@Serializable
data class Obra(
    val id: String,
    val tipo: String,
    val titulo: String,
    val autor: String = "",
    val anio: Int? = null,
    val estado: String = "quiero",
    val inicio: String? = null,
    val fin: String? = null,
    val calificacion: Int? = null,
    val progreso: Int? = null,
    val total: Int? = null,
    val etiquetas: List<String> = emptyList(),
    val nota: String = "",
    val agregado: String = "",
    val veces: Int = 1,
    /** Archivo de la portada guardado en el teléfono (carpeta «portadas»). */
    val portada: String? = null,
    /** De dónde salió la portada, para volver a bajarla si el archivo se pierde. */
    val portada_url: String? = null,
    /** Ficha en el buscador de donde salió (openlibrary:…, tmdb:movie:…, rawg:…, crossref:doi…). */
    val fuente: String? = null,
    /** Artículos académicos: revista y DOI. */
    val revista: String = "",
    val doi: String = "",
    /** Días en que leíste, viste o jugaste esto (AAAA-MM-DD). */
    val dias: List<String> = emptyList(),
    /** Citas que copiaste de la obra. */
    val citas: List<Cita> = emptyList(),
)

/** Una cita copiada de un libro, una película o un artículo. Las que tienen ♥ salen en «Frases guardadas». */
@Serializable
data class Cita(val id: String, val texto: String, val pagina: String = "", val fecha: String = "", val favorita: Boolean = true)

/** Nutrición del día exportada de MacroFactor (resumen «Quick Export»). Las claves son las columnas de MacroFactor. */
@Serializable
data class NutricionDia(
    val fecha: String,
    val valores: Map<String, Double> = emptyMap(),
    val comidas: Int = 0,
    val primera: String? = null,
    val ultima: String? = null,
    val cafeina_ultima: String? = null,
)

/** Un alimento registrado en MacroFactor («Food Log»). */
@Serializable
data class Comida(
    val id: String,
    val fecha: String,
    val hora: String = "",
    val alimento: String,
    val porcion: String = "",
    val cantidad: Double? = null,
    val peso_porcion_g: Double? = null,
    val valores: Map<String, Double> = emptyMap(),
)

/** Una serie de MacroFactor Workouts («Workout Log»). */
@Serializable
data class Serie(
    val id: String,
    val fecha: String,
    val entreno: String = "",
    val entreno_s: Double? = null,
    val ejercicio: String,
    val tipo: String = "",
    val peso_kg: Double? = null,
    val reps: Int? = null,
    val rir: Double? = null,
    val duracion_s: Double? = null,
    val distancia_m: Double? = null,
    val peso_base_kg: Double? = null,
    val orden: Int = 0,
)

/** Resumen de entrenamiento del día: series y volumen total y por grupo muscular. */
@Serializable
data class EntrenoDia(
    val fecha: String,
    val entrenos: List<String> = emptyList(),
    val minutos: Double? = null,
    val series: Int = 0,
    val series_efectivas: Int = 0,
    val reps: Int = 0,
    val volumen_kg: Double = 0.0,
    val series_musculo: Map<String, Double> = emptyMap(),
    val volumen_musculo: Map<String, Double> = emptyMap(),
)
