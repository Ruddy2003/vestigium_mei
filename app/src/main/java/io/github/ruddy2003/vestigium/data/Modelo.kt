package io.github.ruddy2003.vestigium.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    coerceInputValues = true
    isLenient = true
}

const val SCHEMA_V = 7

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
)
