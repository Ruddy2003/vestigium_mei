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
    /** Categorías del sueño por ejes (personajes, escenario, interacción, temas, estructura, emoción): ids de Catalogo.SUENO_EJES. */
    val sueno_categorias: List<String> = emptyList(),
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
    /** En los avisos de ánimo: con quién estabas (solo, pareja, amigos, familia, companeros, desconocidos). */
    val con_quien: List<String> = emptyList(),
    /** En una recaída: qué la detonó, en qué aparato y qué hacías antes (seguimiento privado). */
    val detonantes: List<String> = emptyList(),
    val dispositivo: String? = null,
    val antes: String? = null,
    /** Cuánto duró (minutos), en una recaída. */
    val duracion_min: Int? = null,
    /** Lo que sentías (ids de Catalogo.SENTIMIENTOS; «no_se» = no lo sabías; «otro» va con [emocion_otra]). */
    val emociones: List<String> = emptyList(),
    val emocion_otra: String? = null,
    /** En qué pensabas: Catalogo.PENSAMIENTOS (preocupación, idea, recuerdo…). */
    val pensamiento: String? = null,
)

/** Un registro de ánimo: de un aviso («muestreo») o hecho por ti («animo»). */
val Momento.esAnimo: Boolean get() = tipo == "muestreo" || tipo == "animo"

/**
 * Un consumo de una sustancia: el periodo en que estuviste bajo su efecto ([inicio] a [fin], cuando se te pasó),
 * con lecturas de nivel (0 a 10) en el camino. Lo básico se llena en el momento; duración, máximo y cruda, a la mañana siguiente.
 * Es un dato normal: entra en los análisis aunque se oculte de las pantallas.
 */
@Serializable
data class Consumo(
    val id: String,
    /** Día lógico en que empezó. */
    val fecha: String,
    /** alcohol, cannabis, tabaco, cafeina u otra. */
    val sustancia: String,
    val otra_nombre: String = "",
    val inicio: String,
    /** Cuando se te pasó el efecto. Null = sigue (o no lo dijiste). */
    val fin: String? = null,
    /** Lecturas de nivel con su hora (0 sobrio a 10). */
    val niveles: List<Lectura> = emptyList(),
    val nivel_max: Int? = null,
    /** Alcohol: bebidas estándar; tabaco: cigarros o sesiones; cannabis: veces; cafeína: mg. */
    val cantidad: Double? = null,
    /** Cannabis: fumado, vape, comestible; tabaco: cigarro, vape. */
    val via: String? = null,
    val con_quien: List<String> = emptyList(),
    val lat: Double? = null,
    val lon: Double? = null,
    val lugar: String? = null,
    /** Cruda o bajón al día siguiente (0 nada a 5 no puedo funcionar). */
    val cruda: Int? = null,
    val sintomas: List<String> = emptyList(),
    val nota: String = "",
    /** Ya se llenó lo de la mañana siguiente. */
    val completo: Boolean = false,
    val origen: String = "app",
)

@Serializable
data class Lectura(val ts: String, val nivel: Int)

/** Quién es alguien que mencionas con @: qué es para ti, qué tan cerca (1 a 7, círculos de Aron) y desde cuándo. */
@Serializable
data class Persona(
    val nombre: String,
    val relacion: String,
    /** Parentesco o detalle libre (mamá, primo, compañero de la maestría…). */
    val detalle: String = "",
    val cercania: Int? = null,
    val desde: String = "",
    /** Cómo era antes: cada cambio con su fecha (pareja → expareja). */
    val historial: List<CambioRelacion> = emptyList(),
)

@Serializable
data class CambioRelacion(val hasta: String, val relacion: String, val cercania: Int? = null)

/** El momento es del seguimiento privado: nunca sale en reportes, «Tu año», logros, mapas ni widgets. */
val Momento.privado: Boolean get() = tipo == "recaida"

/**
 * Un tramo de tiempo medido con el cronómetro: un bloque de enfoque (estudio, lectura, idiomas, tesis…) o una siesta.
 * [inicio] y [fin] en ISO con zona; [fecha] es el día lógico en que empezó.
 */
@Serializable
data class Tramo(
    val id: String,
    val fecha: String,
    val tipo: String,
    val inicio: String,
    val fin: String? = null,
    val min: Int = 0,
    val nota: String = "",
    val origen: String = "app",
)

/**
 * Una campaña de la semana: un reto opcional que aceptaste (por ejemplo, 5 noches de 7 horas).
 * [lunes] es el lunes de esa semana; [reto] el id del catálogo; [meta] cuántos días de 7.
 */
@Serializable
data class Campana(val id: String, val lunes: String, val reto: String, val meta: Int, val tema: String = "", val clave: String = "")

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
    /** Minutos de pantalla en cada hora del día (0 a 23). */
    val por_hora_min: List<Int> = emptyList(),
    /** La noche que terminó este día: minutos de pantalla en la última hora antes de dormirte. */
    val cama_min: Int? = null,
    /** Minutos entre soltar el teléfono y quedarte dormido (según el reloj). */
    val dormir_tras_tel_min: Int? = null,
    /** Minutos entre despertar (según el reloj) y la primera vez que usaste el teléfono. */
    val tel_tras_despertar_min: Int? = null,
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
    // ---- Más contexto para análisis más finos (todo opcional; cada cambio queda con su fecha).
    /** Sexo biológico, solo para las referencias de salud (hombre, mujer). */
    val sexo: String = "",
    val estatura_cm: Int? = null,
    /** Ciudad donde creciste. */
    val ciudad_natal: String = "",
    /** Hasta dónde has estudiado: secundaria, prepa, tecnico, licenciatura, maestria, doctorado. */
    val estudios: String = "",
    /** Dónde trabajas o estudias: presencial, hibrido, remoto. */
    val modalidad: String = "",
    /** Horas a la semana de trabajo o escuela. */
    val horas_semana: Int? = null,
    /** Cómo te mueves casi siempre: pie, bici, publico, auto, moto. */
    val transporte: String = "",
    /** casa, departamento, cuarto, residencia. */
    val vivienda: String = "",
    val hijos: Int? = null,
    /** omnivora, flexitariana, pescetariana, vegetariana, vegana. */
    val dieta: String = "",
    /** Tu deporte o actividad principal (escalada, gimnasio, correr…). */
    val deporte: String = "",
    /** Tu horario de sueño ideal (HH:MM), aunque no siempre lo cumplas. */
    val dormir_ideal: String = "",
    val despertar_ideal: String = "",
    /** Idiomas que estás aprendiendo. */
    val idiomas: List<String> = emptyList(),
    /** Lo que quieres lograr este año. */
    val metas: List<String> = emptyList(),
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
    /** Cambiar de tema solo cada día (a las 5:00), entre los elegidos (vacío = todos). */
    val tema_rotar: Boolean = false,
    val temas_rotacion: List<String> = emptyList(),
    /** Día lógico del último cambio automático. */
    val tema_rotado: String = "",
    /** Logros que inventaste tú. */
    val logros_propios: List<LogroPropio> = emptyList(),
    /** Logros ya vistos (id → día en que se vieron conseguidos), para festejar solo los nuevos. */
    val logros_vistos: Map<String, String> = emptyMap(),
    /** «Tu año»: las predicciones de enero, para compararlas al cerrar el año. Clave = año. */
    val predicciones_anio: Map<String, PrediccionAnio> = emptyMap(),
    /** «Tu año»: tarjetas que escondiste antes de exportar, y si los nombres van completos o con iniciales. */
    val anio_ocultas: List<String> = emptyList(),
    val anio_nombres_completos: Boolean = false,
    /** Seguimiento privado: el hábito que una recaída marca como no cumplido (id). Vacío = se busca solo por el nombre. */
    val habito_recaida: String? = null,
    /** Cronómetro de enfoque en marcha: qué y desde cuándo (milisegundos). */
    val enfoque_tipo: String? = null,
    val enfoque_inicio: Long? = null,
    /** Un personaje del tema en Día que reacciona a cómo dormiste, tu ánimo, tu racha y el clima. */
    val personaje: Boolean = true,
    /** Logros que pusiste en tu hall (ids), en orden. */
    val hall_logros: List<String> = emptyList(),
    /** Pantallas fijadas arriba de «Más». */
    val favoritos_mas: List<String> = emptyList(),
    /** Campañas semanales que aceptaste. */
    val campanas: List<Campana> = emptyList(),
    /** La hoja de la noche muestra solo lo que falta (lo ya lleno queda resumido arriba). */
    val noche_solo_falta: Boolean = true,
    /** Sustancias que no se muestran en pantallas (cinta, Día, Vida). Siguen en los análisis y en la exportación. */
    val sustancias_ocultas: List<String> = emptyList(),
    /** Vida media de la cafeína para estimar cuánta te queda (horas; varía mucho entre personas). */
    val cafeina_vida_media_h: Double = 5.0,
    /** Quién es cada persona que mencionas. */
    val personas: List<Persona> = emptyList(),
    /** Personas que preferiste no clasificar, y pares que dijiste que no son la misma persona («a|b»). */
    val personas_omitidas: List<String> = emptyList(),
    val personas_distintas: List<String> = emptyList(),
    /** Día en que se pospuso la pregunta «¿quién es…?». */
    val personas_pospuesto: String = "",
)

/**
 * Un logro que inventas: qué cuenta (una variable del día, un hábito, una etiqueta), cuántas veces o en cuántos días de una ventana.
 * Por ejemplo «Leer 20 de 30 días», «10 ciudades nuevas», «Escribir 100,000 palabras».
 */
@Serializable
data class LogroPropio(
    val id: String,
    val nombre: String,
    /** variable (del panel diario), habito, etiqueta (#tema o @persona en el diario). */
    val fuente: String,
    val clave: String,
    /** dias: días con algo (≥ umbral) dentro de la ventana; total: suma de todo; racha: días seguidos. */
    val modo: String = "dias",
    val meta: Double = 1.0,
    /** Para «dias»: días que mira hacia atrás (30 = de los últimos 30 días). 0 = desde siempre. */
    val ventana: Int = 0,
    /** Desde cuánto cuenta un día (por ejemplo, 8000 pasos). */
    val umbral: Double = 0.0,
    val creado: String = "",
    val icono: String = "★",
)

/** Lo que crees que pasará en el año (se escribe en enero; «Tu año» lo compara en diciembre). */
@Serializable
data class PrediccionAnio(val fecha: String, val respuestas: Map<String, String> = emptyMap())

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

/**
 * Lo que traen los CSV de YouTube en Takeout: suscripciones, «me gusta», videos agregados a tus listas, comentarios y mensajes en chats en vivo.
 * De los comentarios y chats solo se guarda cuándo y en qué video; el texto se descarta al leerlo y nunca se guarda.
 */
@Serializable
data class YtEvento(
    val id: String,
    /** suscripcion, megusta, lista, comentario o chat. */
    val tipo: String,
    val ts: String = "",
    val fecha: String = "",
    val video: String = "",
    val canal: String = "",
    val lista: String = "",
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

/** Apps cuyo reproductor no es música (videos, navegadores, redes): lo que suena ahí no cuenta como canción ni su canal como artista. */
private val NO_MUSICA = listOf("youtube", "chrome", "netflix", "twitch", "tiktok", "instagram", "facebook", "prime video", "disney", "hbo", "crunchyroll",
    "firefox", "brave", "vlc", "twitter", "reddit", "plex", "fotos", "photos", "whatsapp", "telegram", "edge", "opera", "samsung internet", "galería",
    "gallery", "mx player", "google tv", "kick", "vix", "pluto", "mubi", "paramount", "apple tv", "duolingo", "snapchat", "threads", "linkedin", "discord")
private val SI_MUSICA = listOf("youtube music", "yt music", "music", "música", "musica")

/** Si es música de verdad: lo importado de Spotify siempre; lo que sonó en el teléfono, salvo que venga de una app de video o navegador. */
val Escucha.esMusica: Boolean get() {
    if (fuente != "notificacion") return true
    val a = app.lowercase().trim()
    if (a.isEmpty()) return true
    if (SI_MUSICA.any { a.contains(it) } && !a.contains("tv")) return true
    if (a == "x" || a == "max") return false
    return NO_MUSICA.none { a.contains(it) }
}

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
data class Cita(val id: String, val texto: String, val pagina: String = "", val fecha: String = "", val favorita: Boolean = false)

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
