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

@Serializable
data class Carta(val id: String, val escrita: String, val abrir_en: String, val texto: String, val abierta: Boolean = false)

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
    /** romano, ciber o heleno. */
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
)

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
)

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
