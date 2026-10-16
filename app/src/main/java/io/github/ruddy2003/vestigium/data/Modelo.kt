package io.github.ruddy2003.vestigium.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    coerceInputValues = true
    isLenient = true
}

const val SCHEMA_V = 8

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
)

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
