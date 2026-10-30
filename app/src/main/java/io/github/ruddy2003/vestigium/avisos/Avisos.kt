package io.github.ruddy2003.vestigium.avisos

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.toArgb
import io.github.ruddy2003.vestigium.R
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.MainActivity
import io.github.ruddy2003.vestigium.ui.QuickActivity
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.esquemaDe

object Avisos {
    const val CANAL_ANIMO = "animo"
    const val CANAL_DIARIO = "diario"
    const val CANAL_ESCALAS = "escalas"
    const val CANAL_REPORTES = "reportes"
    /** Lugares especiales: canal propio con su sonido (se cambia en los ajustes de Android). */
    const val CANAL_LUGARES = "lugares_especiales"
    /** Importaciones, metas y notas de recordatorio. */
    const val CANAL_RECORDATORIOS = "recordatorios"
    const val ID_IMPORTAR = 2020
    const val ID_METAS = 2021
    const val ID_LUGAR = 2030
    const val ID_SUPLEMENTOS = 2040

    /** Sonido de cada tema: el de los avisos de ánimo y el solemne (diario, sueños, cartas, cuestionarios). */
    private fun sonidosDe(t: TemaId): Pair<Int, Int> = when (t) {
        TemaId.ROMANO -> R.raw.n_romano_aviso to R.raw.n_romano_solemne
        TemaId.CIBER -> R.raw.n_ciber_aviso to R.raw.n_ciber_solemne
        TemaId.NEON -> R.raw.n_neon_aviso to R.raw.n_neon_solemne
        TemaId.HELENO -> R.raw.n_heleno_aviso to R.raw.n_heleno_solemne
        TemaId.EGIPTO -> R.raw.n_egipto_aviso to R.raw.n_egipto_solemne
        TemaId.MEDIEVAL -> R.raw.n_medieval_aviso to R.raw.n_medieval_solemne
        TemaId.DISTOPIA -> R.raw.n_distopia_aviso to R.raw.n_distopia_solemne
        TemaId.REFUGIO -> R.raw.n_refugio_aviso to R.raw.n_refugio_solemne
        TemaId.ORDEN -> R.raw.n_orden_aviso to R.raw.n_orden_solemne
        TemaId.LABORATORIO -> R.raw.n_laboratorio_aviso to R.raw.n_laboratorio_solemne
        TemaId.COLONOS -> R.raw.n_colonos_aviso to R.raw.n_colonos_solemne
        TemaId.NAPOLEON -> R.raw.n_napoleon_aviso to R.raw.n_napoleon_solemne
        TemaId.BOTANICO -> R.raw.n_botanico_aviso to R.raw.n_botanico_solemne
        TemaId.NAVIDAD -> R.raw.n_navidad_aviso to R.raw.n_navidad_solemne
        TemaId.SEDA -> R.raw.n_seda_aviso to R.raw.n_seda_solemne
        TemaId.URSS -> R.raw.n_urss_aviso to R.raw.n_urss_solemne
        TemaId.GOTICO -> R.raw.n_gotico_aviso to R.raw.n_gotico_solemne
        TemaId.ESPACIO -> R.raw.n_espacio_aviso to R.raw.n_espacio_solemne
        TemaId.ANDALUS -> R.raw.n_andalus_aviso to R.raw.n_andalus_solemne
        TemaId.MONGOL -> R.raw.n_mongol_aviso to R.raw.n_mongol_solemne
        TemaId.RENACIMIENTO -> R.raw.n_renacimiento_aviso to R.raw.n_renacimiento_solemne
        TemaId.REVOLUCION -> R.raw.n_revolucion_aviso to R.raw.n_revolucion_solemne
        TemaId.ANTIGUAS -> R.raw.n_antiguas_aviso to R.raw.n_antiguas_solemne
    }

    /** Los mismos sonidos por id de tema (lo revisa la prueba de cobertura). Sale del `when` de arriba: no puede faltar ninguno. */
    internal val SONIDOS: Map<String, Pair<Int, Int>> get() = TemaId.entries.associate { it.id to sonidosDe(it) }

    /** El tema guardado en la configuración, con la misma regla que `Estilo.tema` (un id desconocido vuelve al romano). */
    private fun temaDe(id: String): TemaId = TemaId.entries.firstOrNull { it.id == id } ?: if (id == "muertos") TemaId.BOTANICO else TemaId.ROMANO

    private fun temaActual(): TemaId = runCatching { Estilo.tema }.getOrDefault(TemaId.ROMANO)

    /**
     * El color de los avisos: el primario del tema, en claro u oscuro según la cortina del teléfono (no según el botón de
     * la app, porque el aviso se ve sobre la cortina del sistema). El tema que solo existe en oscuro usa siempre ese.
     */
    fun colorTema(ctx: Context): Int {
        val t = temaActual()
        val noche = (ctx.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
        return esquemaDe(t, t.soloOscuro || noche).primary.toArgb()
    }

    private fun nombreCanal(base: String) = when (base) {
        CANAL_ANIMO -> "Ánimo durante el día"; CANAL_DIARIO -> "Diario y sueños"; CANAL_ESCALAS -> "Escalas periódicas"
        io.github.ruddy2003.vestigium.data.Enfoque.CANAL_AVISO -> "Fin de bloque o descanso"; else -> "Reportes"
    }
    private fun descripcionCanal(base: String) = when (base) {
        CANAL_ANIMO -> "Avisos breves para registrar cómo te sientes"; CANAL_DIARIO -> "Recordatorio de la mañana y de la noche"
        io.github.ruddy2003.vestigium.data.Enfoque.CANAL_AVISO -> "Avisa cuando empieza un descanso, cuando termina y cuando acabas"; else -> "Cuestionarios que tocan"
    }
    private fun importancia(base: String) = when (base) {
        CANAL_ANIMO, io.github.ruddy2003.vestigium.data.Enfoque.CANAL_AVISO -> NotificationManager.IMPORTANCE_HIGH
        CANAL_REPORTES -> NotificationManager.IMPORTANCE_LOW; else -> NotificationManager.IMPORTANCE_DEFAULT
    }

    /** Recurso de sonido para un canal según el tema elegido (null = sonido normal del teléfono). */
    fun sonido(base: String, tema: String = runCatching { io.github.ruddy2003.vestigium.data.Repo.config.tema }.getOrDefault("romano")): Int? {
        val par = sonidosDe(temaDe(tema))
        return when (base) {
            CANAL_ANIMO, io.github.ruddy2003.vestigium.data.Enfoque.CANAL_AVISO -> par.first
            CANAL_DIARIO, CANAL_ESCALAS -> par.second
            else -> null
        }
    }

    fun uriSonido(ctx: Context, res: Int): Uri = Uri.parse("android.resource://${ctx.packageName}/$res")

    /**
     * Android no deja cambiar el sonido de un canal ya creado, así que hay un canal por tema («animo_medieval»…).
     * Al cambiar de tema se crea el nuevo copiando lo que hayas decidido en el anterior (silencio, importancia, vibración)
     * y se borra el viejo. Lo usa también el aviso de fin de bloque del enfoque («enfoque_aviso_<tema>»).
     */
    fun canal(ctx: Context, base: String): String {
        if (base == CANAL_REPORTES) return base
        val nm = ctx.getSystemService(NotificationManager::class.java)
        val c = runCatching { io.github.ruddy2003.vestigium.data.Repo.config }.getOrNull()
        val propio = c?.sonido_avisos ?: true
        val tema = c?.tema ?: "romano"
        val id = base + "_" + if (propio) tema else "sistema"
        if (nm.getNotificationChannel(id) != null) return id
        val viejos = nm.notificationChannels.filter { it.id == base || it.id.startsWith(base + "_") }
        val previo = viejos.firstOrNull()
        val ch = NotificationChannel(id, nombreCanal(base), previo?.importance ?: importancia(base)).apply {
            description = descripcionCanal(base)
            val atributos = android.media.AudioAttributes.Builder().setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
            when {
                previo != null && previo.sound == null -> setSound(null, null)          // lo habías silenciado: sigue en silencio
                propio -> sonido(base, tema)?.let { setSound(uriSonido(ctx, it), atributos) }
            }
            if (previo != null) { enableVibration(previo.shouldVibrate()); setShowBadge(previo.canShowBadge()) }
            else if (base == io.github.ruddy2003.vestigium.data.Enfoque.CANAL_AVISO) enableVibration(true)
        }
        nm.createNotificationChannel(ch)
        viejos.forEach { nm.deleteNotificationChannel(it.id) }
        return id
    }

    fun crearCanales(ctx: Context) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(NotificationChannel(CANAL_REPORTES, nombreCanal(CANAL_REPORTES), importancia(CANAL_REPORTES)))
        nm.createNotificationChannel(NotificationChannel(CANAL_RECORDATORIOS, "Importaciones y metas", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Un aviso por semana con lo que toca importar, y las metas y notas que toca revisar"
        })
        if (nm.getNotificationChannel(CANAL_LUGARES) == null) nm.createNotificationChannel(NotificationChannel(CANAL_LUGARES, "Lugares especiales", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "«¿Es un lugar especial?» cuando llevas un rato en un sitio nuevo"
            val atributos = android.media.AudioAttributes.Builder().setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
            setSound(uriSonido(ctx, R.raw.n_lugar), atributos)
        })
        listOf(CANAL_ANIMO, CANAL_DIARIO, CANAL_ESCALAS, io.github.ruddy2003.vestigium.data.Enfoque.CANAL_AVISO).forEach { runCatching { canal(ctx, it) } }
    }

    /** Lo que dicen los avisos en la voz de cada tema; los datos (nombres, números) se agregan en cada aviso. */
    internal class Voz(
        val animo: String, val animoTexto: String, val sueno: String, val suenoTexto: String,
        val diario: String, val diarioTexto: String, val dificil: String, val escalas: String,
        val carta: String, val cartaTexto: String, val logro: String, val logros: String,
        /** Lleva %d para el año. */
        val anio: String, val anioTexto: String,
    )

    /**
     * Los avisos de cuidado (respaldos, Drive, importaciones, metas, suplementos, lugares, lo de anoche) no cambian de voz:
     * tienen que leerse igual de claros en cualquier tema, y los privados no deben llamar la atención.
     */
    internal fun voz(t: TemaId = temaActual()): Voz = when (t) {
        TemaId.ROMANO -> Voz("QVID SENTIS · ¿cómo te sientes ahora?", "Dos toques en la tablilla: qué tan agradable y cuánta energía.",
            "SOMNIA · ¿qué soñaste?", "Grábalo antes de levantarte; el sueño se borra como cera tibia.",
            "DIES · ¿cómo fue tu día?", "Llena el día. Si fue duro, basta con un número.",
            "Día duro: solo un número", "Hodie:",
            "EPISTVLA · te escribe tu yo del pasado", "Llegó la fecha que fijaste para abrirla.",
            "Triunfo", "triunfos nuevos",
            "ANNALES %d · tu año está listo", "Un año entero en historias: lo que más hiciste, lo único, lo curioso. Toca para leerlo.")
        TemaId.CIBER -> Voz("> ping ánimo", "Dos toques: qué tan agradable y cuánta energía. _",
            "> dump de sueños", "Guárdalo antes de levantarte: la memoria se libera en minutos.",
            "> cierre del día", "Llena el día. Si fue un mal día, basta con un número.",
            "modo mínimo: 1 número", "> pendiente:",
            "> mensaje en cola de tu yo del pasado", "Se cumplió la fecha programada. Toca para descifrarlo.",
            "> logro desbloqueado", "logros desbloqueados",
            "> informe_%d listo", "Un año entero en historias: lo más frecuente, lo único, lo raro. Toca para ejecutar.")
        TemaId.NEON -> Voz("> ping ánimo", "Dos toques: qué tan agradable y cuánta energía. _",
            "> dump de sueños", "Guárdalo antes de levantarte: la memoria se libera en minutos.",
            "> cierre del día", "Llena el día. Si fue un mal día, basta con un número.",
            "modo mínimo: 1 número", "> pendiente:",
            "> mensaje en cola de tu yo del pasado", "Se cumplió la fecha programada. Toca para descifrarlo.",
            "> logro desbloqueado", "logros desbloqueados",
            "> informe_%d listo", "Un año entero en historias: lo más frecuente, lo único, lo raro. Toca para ejecutar.")
        TemaId.HELENO -> Voz("Πῶς ἔχεις; · ¿cómo te sientes ahora?", "Dos toques: cuánto agrado y cuánta energía.",
            "ὌΝΕΙΡΟΣ · ¿qué te mostró el sueño?", "Anótalo antes de levantarte; Hipnos se lo lleva en minutos.",
            "ἩΜΕΡΑ · ¿cómo fue tu día?", "Llena el día. Si fue difícil, basta con un número.",
            "Día difícil: un número", "Hoy consulta:",
            "ἘΠΙΣΤΟΛΗ · carta de tu yo del pasado", "Llegó el día que elegiste para abrirla.",
            "Corona de olivo", "coronas de olivo nuevas",
            "Tu %d ya es historia", "Un año entero en historias, como en Heródoto: lo que más hiciste, lo único, lo curioso.")
        TemaId.EGIPTO -> Voz("☥ El heraldo pregunta: ¿cómo está tu corazón?", "Dos toques: cuánto agrado y cuánta fuerza tiene tu ka.",
            "☥ Lo que mostró la noche", "Anótalo antes de dejar la estera: los sueños se van con la primera luz de Ra.",
            "☥ El papiro de hoy", "Llena el día antes de que se ponga el sol. Si fue un día torcido, basta con un número.",
            "Día torcido: un número", "La Casa de la Vida pregunta hoy:",
            "☥ Un papiro sellado de tu yo del pasado", "Llegó el día del año civil que elegiste para romper el sello.",
            "Amuleto de oro ganado", "amuletos de oro nuevos",
            "Los anales de %d están escritos", "Un año entero en relieves, como en los muros de un templo: lo que más hiciste, lo único, lo curioso.")
        TemaId.MEDIEVAL -> Voz("¶ ¿Cómo está tu ánima agora?", "Dos toques: cuán placentero y cuánto brío.",
            "¶ De las visiones de la noche", "Asiéntalas antes de levantarte, que el recuerdo se borra como tinta fresca.",
            "¶ Completas: ¿cómo fue el día?", "Asienta el día. Si fue de pesar, basta con un número.",
            "Día de pesar: un número", "Por la regla, hoy:",
            "¶ Epístola de tu yo de antaño", "Llegó el día señalado para romper el sello.",
            "Gesta cumplida", "gestas nuevas",
            "El anuario de %d está iluminado", "Un año entero en historias: lo más hecho, lo único, lo curioso. Toca para leer el códice.")
        TemaId.DISTOPIA -> Voz("DECLARE SU ESTADO ACTUAL", "Dos toques obligatorios: agrado y energía.",
            "REPORTE ONÍRICO PENDIENTE", "Declare lo soñado antes de levantarse. El recuerdo caduca en minutos.",
            "DECLARACIÓN NOCTURNA", "Complete el parte del día. En jornada difícil se acepta un solo número.",
            "Jornada difícil: un número", "INTERROGATORIO PENDIENTE:",
            "CORRESPONDENCIA DESCLASIFICADA", "Su yo del pasado le escribió. Se cumplió la fecha de apertura.",
            "CONDECORACIÓN", "condecoraciones nuevas",
            "BALANCE ANUAL %d DISPONIBLE", "Un año entero en expedientes: lo más frecuente, lo único, lo anómalo.")
        TemaId.REFUGIO -> Voz("> CHEQUEO DE ESTADO, RESIDENTE", "Dos toques en el panel: agrado y energía.",
            "> REGISTRO DE SUEÑOS", "Anótalo antes de salir de la litera; el recuerdo se borra en minutos.",
            "> INFORME DE CIERRE", "Llena el día. Si el turno fue duro, basta con un número.",
            "Turno duro: un número", "> EVALUACIÓN PROGRAMADA:",
            "> CÁPSULA DEL TIEMPO ABIERTA", "Tu yo del pasado dejó un mensaje para hoy.",
            "INSIGNIA OBTENIDA", "insignias nuevas",
            "> REGISTRO ANUAL %d LISTO", "Un año bajo tierra en historias: lo más hecho, lo único, lo curioso.")
        TemaId.ORDEN -> Voz("INFORME DE MORAL", "Dos toques: agrado y energía. Sin demora.",
            "PARTE DE VIGILIA", "Reporta lo soñado antes de levantarte; se pierde en minutos.",
            "PARTE DE CIERRE", "Reporta el día. Si la jornada fue dura, basta con un número.",
            "Jornada dura: un número", "EVALUACIÓN:",
            "ÓRDENES SELLADAS DE TU YO DEL PASADO", "Llegó la fecha de abrirlas.",
            "Mérito ganado", "méritos nuevos",
            "PARTE DE CAMPAÑA %d LISTO", "Un año de campaña en historias: lo más hecho, lo único, lo curioso.")
        TemaId.LABORATORIO -> Voz("Medición de ánimo · t = ahora", "Dos toques: qué tan agradable (valencia) y cuánta energía (activación).",
            "Muestra onírica", "Regístrala antes de levantarte: se degrada en minutos.",
            "Cierre de sesión", "Llena el día. Si fue difícil, basta con un dato.",
            "Sesión difícil: un dato", "Protocolo pendiente:",
            "Muestra sellada: carta de tu yo del pasado", "Se cumplió la fecha de apertura del protocolo.",
            "Hito registrado", "hitos registrados",
            "Informe anual %d listo", "Un año de datos en historias: lo más frecuente, lo único, lo atípico.")
        TemaId.COLONOS -> Voz("¿Qué tal el ánimo, vecino?", "Dos toques: qué tan a gusto y cuánta energía.",
            "¿Qué soñaste anoche?", "Apúntalo antes de levantarte; se olvida antes del café.",
            "La jornada de hoy", "Llena el día. Si fue pesado, basta con un número.",
            "Día pesado: un número", "Hoy, en la junta:",
            "Llegó carta de tu yo del pasado", "El cartero la trajo en la fecha que pediste.",
            "Mención en la gaceta", "menciones en la gaceta",
            "Edición especial: tu %d", "Un año entero en la gaceta: lo que más hiciste, lo único, lo curioso.")
        TemaId.NAPOLEON -> Voz("Rapport · ¿cómo está tu moral?", "Dos toques para el boletín: agrado y energía.",
            "Rêves de bivouac", "Anótalo antes de la diana; el recuerdo se va en minutos.",
            "Boletín del día", "Llena el día. Si fue una derrota, basta con un número.",
            "Día de derrota: un número", "Por orden del cuartel general:",
            "Despacho de tu yo del pasado", "El correo imperial lo entrega en la fecha fijada.",
            "Légion d’honneur", "honores nuevos",
            "Boletín de la campaña de %d", "La campaña entera en historias: lo más hecho, lo único, lo curioso.")
        TemaId.BOTANICO -> Voz("Observación · ¿cómo te sientes?", "Dos toques, como una muestra de campo: agrado y energía.",
            "Latencia · ¿qué soñaste?", "Anótalo antes de levantarte; se marchita en minutos.",
            "Entrada del herbario", "Llena el día. Si fue difícil, basta con un número.",
            "Día difícil: un número", "Muestreo de hoy:",
            "Una carta prensada por tu yo del pasado", "Llegó la estación que elegiste para abrirla.",
            "Especie nueva en la colección", "especies nuevas",
            "El herbario de %d está listo", "Un año entero en láminas: lo más hecho, lo único, lo curioso.")
        TemaId.NAVIDAD -> Voz("✦ ¿Cómo te sientes ahora?", "Dos toques junto al fuego: agrado y energía.",
            "✦ ¿Qué soñaste esta noche?", "Anótalo antes de salir de las cobijas; se derrite en minutos.",
            "✦ ¿Cómo estuvo tu día?", "Llena el día. Si fue difícil, con un número basta.",
            "Día difícil: con un número basta", "En la lista de hoy:",
            "✦ Una carta de tu yo del pasado", "Llegó el día de abrirla, como un regalo.",
            "Un regalo para ti", "regalos nuevos",
            "Tu %d, envuelto y listo", "Un año entero en historias: lo más hecho, lo único, lo curioso. Toca para abrirlo.")
        TemaId.SEDA -> Voz("◇ Alto en la ruta: ¿cómo va tu espíritu?", "Dos toques: agrado y fuerza para el camino.",
            "◇ Sueños de caravasar", "Anótalo antes de levantar el campamento; se lo lleva el viento.",
            "◇ Relato de la jornada", "Llena el día. Si el camino fue duro, basta con un número.",
            "Camino duro: un número", "En la posta de hoy:",
            "◇ Un mensajero trae carta de tu yo del pasado", "Llegó en la fecha convenida.",
            "Mercancía rara", "tesoros nuevos",
            "El rollo de %d está listo", "Un año entero de camino en historias: lo más hecho, lo único, lo curioso.")
        TemaId.URSS -> Voz("★ ¡Camarada! ¿Cómo está tu moral?", "Dos toques: agrado y energía para el plan.",
            "★ Informe de descanso", "Anota lo soñado antes del turno; el recuerdo se va en minutos.",
            "★ Balance de la jornada", "Llena el día. Si fue duro, basta con una cifra.",
            "Jornada dura: una cifra", "Norma de hoy:",
            "★ Carta de tu yo del pasado", "Llegó la fecha fijada en el plan.",
            "Norma superada", "normas superadas",
            "Plan %d cumplido", "Un año entero en historias: lo más hecho, lo único, lo curioso.")
        TemaId.GOTICO -> Voz("✠ Ad horam · ¿cómo está tu ánima?", "Dos toques entre una hora y otra: agrado y energía.",
            "✠ Maitines · ¿qué soñaste?", "Escríbelo antes de levantarte; las visiones se apagan como una vela.",
            "✠ Completas · el día", "Llena el día. Si fue de penitencia, basta con un número.",
            "Día de penitencia: un número", "Lectio de hoy:",
            "✠ Carta lacrada de tu yo del pasado", "Llegó la fiesta señalada para abrirla.",
            "Vidriera completa", "vidrieras nuevas",
            "El libro de horas de %d", "Un año entero en historias: lo más hecho, lo único, lo curioso.")
        TemaId.ESPACIO -> Voz("CONTROL · estado de la tripulación", "Dos toques: agrado y energía. Cambio.",
            "BITÁCORA DE SUEÑO", "Regístralo antes de dejar la litera; la señal se pierde en minutos.",
            "FIN DE ÓRBITA", "Llena el día. Si hubo turbulencia, basta con un número.",
            "Turbulencia: un número", "SECUENCIA PROGRAMADA:",
            "TRANSMISIÓN DIFERIDA de tu yo del pasado", "La señal llegó en la fecha calculada.",
            "Hito de misión", "hitos de misión",
            "Misión %d · informe listo", "Una vuelta completa al Sol en historias: lo más hecho, lo único, lo curioso.")
        TemaId.ANDALUS -> Voz("الآن · ¿cómo te sientes?", "Dos toques: cuánta dulzura y cuánta fuerza.",
            "المنام · ¿qué soñaste?", "Escríbelo antes de levantarte; el sueño se va como el agua de la acequia.",
            "النهار · ¿cómo fue el día?", "Llena el día. Si fue duro, basta con un número.",
            "Día duro: un número", "Hoy, en la madraza:",
            "رسالة · carta de tu yo del pasado", "Llegó el día que elegiste para abrirla.",
            "Azulejo nuevo en tu patio", "azulejos nuevos",
            "El libro de %d está listo", "Un año entero en historias: lo más hecho, lo único, lo curioso.")
        TemaId.MONGOL -> Voz("СЭТГЭЛ · ¿cómo va tu espíritu?", "Dos toques: agrado y fuerza, como el viento de la estepa.",
            "ЗҮҮД · ¿qué soñaste en la yurta?", "Anótalo antes de salir; el sueño se lo lleva el viento.",
            "ӨДӨР · ¿cómo fue la jornada?", "Llena el día. Si fue dura, basta con un número.",
            "Jornada dura: solo un número", "Hoy, en el campamento:",
            "Un jinete trae carta de tu yo del pasado", "Cabalgó hasta la fecha que elegiste.",
            "Estandarte ganado", "estandartes nuevos",
            "La crónica del año %d", "Un año entero en historias: lo más hecho, lo único, lo curioso.")
        TemaId.RENACIMIENTO -> Voz("❧ Come stai? · ¿cómo te sientes?", "Dos toques: cuánto agrado y cuánto vigor.",
            "❧ Sogni · ¿qué soñaste?", "Escríbelo antes de levantarte; se borra como el carboncillo.",
            "❧ La giornata", "Llena el día. Si fue difícil, basta con un número.",
            "Giornata difficile: un número", "Estudio de hoy:",
            "❧ Lettera de tu yo del pasado", "Llegó el día que elegiste para abrirla.",
            "Invención lograda", "invenciones nuevas",
            "El cuaderno de %d está listo", "Un año entero en láminas: lo más hecho, lo único, lo curioso.")
        TemaId.REVOLUCION -> Voz("✶ Citoyen, ¿cómo te sientes?", "Dos toques, libremente: agrado y energía.",
            "✶ ¿Qué soñaste, ciudadano?", "Anótalo antes de levantarte; el recuerdo se va en minutos.",
            "✶ Cuaderno de quejas del día", "Llena el día. Si fue duro, basta con un número.",
            "Día duro: basta un número", "Orden del día:",
            "✶ Carta de tu yo del pasado", "Llegó la fecha que decretaste para abrirla.",
            "Virtud cívica", "virtudes nuevas",
            "Los anales del año %d", "Un año entero en historias: lo más hecho, lo único, lo curioso.")
        TemaId.ANTIGUAS -> Voz("⟡ Alto en el camino: ¿cómo va tu temple?", "Dos toques: agrado y fuerza para la jornada.",
            "⟡ Visiones de la noche", "Anótalas antes de levantarte; se desvanecen como niebla en el bosque.",
            "⟡ La crónica del día", "Llena el día. Si fue una jornada oscura, basta con un número.",
            "Jornada oscura: un número", "Prueba de hoy:",
            "⟡ Un pergamino de tu yo del pasado", "Llegó la luna que elegiste para abrirlo.",
            "Gema hallada", "gemas nuevas",
            "La crónica de %d está escrita", "Un año entero en historias: lo más hecho, lo único, lo curioso.")
    }

    /** El cronómetro de enfoque en la voz del tema: nombre del descanso, consejo, fin del descanso, despertar y fin. */
    internal class VozEnfoque(val descanso: String, val consejo: String, val finDescanso: String, val despertar: String, val terminaste: String)

    internal fun vozEnfoque(t: TemaId = temaActual()): VozEnfoque = when (t) {
        TemaId.ROMANO -> VozEnfoque("Otium", "Levántate y bebe agua, como en las termas.", "Se acabó el otium", "Surge: hora de despertar", "Opus perfectum: terminaste")
        TemaId.CIBER -> VozEnfoque("> pausa", "Despega la vista de la pantalla. Agua.", "> pausa terminada", "> wake: hora de despertar", "> proceso completado")
        TemaId.NEON -> VozEnfoque("> pausa", "Despega la vista de la pantalla. Agua.", "> pausa terminada", "> wake: hora de despertar", "> proceso completado")
        TemaId.HELENO -> VozEnfoque("Σχολή · pausa", "Levántate, bebe agua de la fuente.", "Terminó la pausa", "Llega Eos: hora de despertar", "Τέλος · terminaste")
        TemaId.EGIPTO -> VozEnfoque("Reposo del escriba", "Deja el cálamo, bebe agua de la jarra.", "Vuelve a la paleta", "Ra sale del horizonte: hora de despertar", "Columna terminada")
        TemaId.MEDIEVAL -> VozEnfoque("Recreo", "Deja la pluma, bebe agua.", "Se acabó el recreo", "Hora de levantarse", "Laus Deo: terminaste")
        TemaId.DISTOPIA -> VozEnfoque("PAUSA AUTORIZADA", "Hidrátese. Regrese a su puesto a tiempo.", "FIN DE LA PAUSA AUTORIZADA", "TOQUE DE DIANA", "CUOTA CUMPLIDA")
        TemaId.REFUGIO -> VozEnfoque("DESCANSO DE TURNO", "Levántate, bebe agua filtrada.", "FIN DEL DESCANSO", "HORA DE DESPERTAR, RESIDENTE", "TURNO COMPLETADO")
        TemaId.ORDEN -> VozEnfoque("ALTO", "Rompan filas: agua y estiramiento.", "FIN DEL ALTO", "DIANA", "MISIÓN CUMPLIDA")
        TemaId.LABORATORIO -> VozEnfoque("Intervalo", "Levántate, hidrátate.", "Fin del intervalo", "Fin del registro de sueño: despierta", "Ensayo completado")
        TemaId.COLONOS -> VozEnfoque("Descanso", "Levántate, toma agua del pozo.", "Se acabó el descanso", "Hora de despertar", "¡Faena terminada!")
        TemaId.NAPOLEON -> VozEnfoque("Halte", "Levántate y bebe, soldado.", "Fin de la halte", "Diana: ¡arriba!", "Victoire!")
        TemaId.BOTANICO -> VozEnfoque("Pausa de campo", "Levántate y riégate: toma agua.", "Termina la pausa de campo", "Hora de despertar, como las flores", "Muestra completa")
        TemaId.NAVIDAD -> VozEnfoque("Descanso junto al fuego", "Levántate, toma algo caliente o agua.", "Se acabó el descanso junto al fuego", "Hora de despertar", "¡Terminaste!")
        TemaId.SEDA -> VozEnfoque("Alto en el caravasar", "Levántate, bebe agua del pozo.", "La caravana sigue", "Hora de levantar el campamento", "Jornada cumplida")
        TemaId.URSS -> VozEnfoque("DESCANSO REGLAMENTARIO", "Levántate, toma agua, camarada.", "DE VUELTA AL TRABAJO", "¡ARRIBA, CAMARADA!", "¡NORMA CUMPLIDA!")
        TemaId.GOTICO -> VozEnfoque("Recreatio", "Levántate, bebe agua.", "Termina la recreatio", "Laudes: hora de despertar", "Deo gratias: terminaste")
        TemaId.ESPACIO -> VozEnfoque("PAUSA ORBITAL", "Despega del asiento. Hidrátate.", "FIN DE LA PAUSA ORBITAL", "DESPERTAR DE LA TRIPULACIÓN", "MISIÓN COMPLETADA")
        TemaId.ANDALUS -> VozEnfoque("Pausa en el patio", "Levántate, bebe agua de la fuente.", "Terminó la pausa en el patio", "Al alba: hora de despertar", "¡Obra acabada!")
        TemaId.MONGOL -> VozEnfoque("Alto en la estepa", "Levántate, bebe agua.", "A caballo otra vez", "El sol sale sobre la estepa: despierta", "¡Jornada ganada!")
        TemaId.RENACIMIENTO -> VozEnfoque("Pausa", "Lascia il pennello: levántate, toma agua.", "Fine della pausa", "Sveglia: hora de despertar", "Opera compiuta")
        TemaId.REVOLUCION -> VozEnfoque("Repos", "Levántate, toma agua, ciudadano.", "Fin du repos", "Réveil: hora de despertar", "Ça ira: terminaste")
        TemaId.ANTIGUAS -> VozEnfoque("Alto en el camino", "Levántate, bebe del odre.", "La compañía sigue", "El alba: hora de despertar", "¡Hazaña cumplida!")
    }

    fun permitido(ctx: Context): Boolean =
        android.os.Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun mostrar(ctx: Context, id: Int, b: NotificationCompat.Builder) {
        if (!permitido(ctx)) return
        runCatching { b.setColor(colorTema(ctx)) }
        try { NotificationManagerCompat.from(ctx).notify(id, b.build()) } catch (e: SecurityException) { }
    }

    private fun quick(ctx: Context, req: Int, modo: String, extras: Intent.() -> Unit = {}): PendingIntent {
        val i = Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, modo)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK).apply(extras)
        return PendingIntent.getActivity(ctx, req, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun abrirApp(ctx: Context, req: Int, pantalla: String): PendingIntent {
        val i = Intent(ctx, MainActivity::class.java).putExtra(MainActivity.PANTALLA, pantalla).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(ctx, req, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun muestreo(ctx: Context, indice: Int, programadoMs: Long) {
        val id = 1000 + indice
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_ANIMO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(voz().animo)
            .setContentText(voz().animoTexto)
            .setContentIntent(quick(ctx, id, QuickActivity.M_ANIMO) { putExtra(QuickActivity.AVISO_MS, programadoMs); putExtra(QuickActivity.NOTIF, id) })
            .setAutoCancel(true)
            .setTimeoutAfter(60 * 60 * 1000L)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
        mostrar(ctx, id, b)
    }

    fun sueno(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_DIARIO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(voz().sueno)
            .setContentText(voz().suenoTexto)
            .setContentIntent(quick(ctx, 2001, QuickActivity.M_SUENO))
            .setAutoCancel(true)
            .setTimeoutAfter(3 * 60 * 60 * 1000L)
        mostrar(ctx, 2001, b)
    }

    /** «¿Cómo terminó lo de anoche?»: abre la hoja para completar duración, máximo y cruda. Sin nombrar la sustancia si está oculta o en modo discreto. */
    fun loDeAnoche(ctx: Context, c: io.github.ruddy2003.vestigium.data.Consumo, n: Int) {
        val cfg = io.github.ruddy2003.vestigium.data.Repo.config
        val nombrar = !cfg.discreto && io.github.ruddy2003.vestigium.data.Sustancias.visible(c)
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_DIARIO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("¿Cómo terminó lo de anoche?")
            .setContentText((if (nombrar) io.github.ruddy2003.vestigium.data.Catalogo.nombreSustancia(c) + ": " else "") + "hasta qué hora duró, el máximo y cómo amaneciste." +
                if (n > 1) " Hay $n por completar." else "")
            .setContentIntent(quick(ctx, 2010, QuickActivity.M_CONSUMO) { putExtra(QuickActivity.CONSUMO, c.id); putExtra(QuickActivity.ORIGEN, "notificacion") })
            .setAutoCancel(true)
            .setTimeoutAfter(6 * 60 * 60 * 1000L)
        mostrar(ctx, 2010, b)
    }

    fun diario(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_DIARIO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(voz().diario)
            .setContentText(voz().diarioTexto)
            // Abre «Llenar el día» (la hoja de la noche de hoy), no solo la pestaña (N-23).
            .setContentIntent(abrirApp(ctx, 2002, "diario"))
            .addAction(0, voz().dificil, quick(ctx, 2003, QuickActivity.M_MINIMO))
            .setAutoCancel(true)
        mostrar(ctx, 2002, b)
    }

    fun escalas(ctx: Context, pendientes: List<Pair<io.github.ruddy2003.vestigium.ciencia.Instrumento, Long>>) {
        val peor = pendientes.maxOf { it.second }
        val nombres = pendientes.joinToString(", ") { it.first.nombre.substringBefore(" (") }
        val titulo = when {
            peor <= 0 -> "${voz().escalas} $nombres"
            peor <= 3 -> "Pendiente desde hace $peor día${if (peor > 1) "s" else ""}: $nombres"
            else -> "Llevas $peor días de atraso: $nombres"
        }
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_ESCALAS))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(titulo)
            .setContentText("Unos ${pendientes.size * 2} minutos. Mientras más regular, más comparables tus resultados.")
            .setContentIntent(abrirApp(ctx, 2004, "escalas"))
            .setPriority(if (peor > 3) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        mostrar(ctx, 2004, b)
    }

    fun carta(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_DIARIO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(voz().carta)
            .setContentText(voz().cartaTexto)
            .setContentIntent(abrirApp(ctx, 2005, "cartas"))
            .setAutoCancel(true)
        mostrar(ctx, 2005, b)
    }

    /** Un día especial (I-6): cumpleaños, aniversario o fecha propia. Abre Tu gente. */
    fun especial(ctx: Context, lineas: List<String>) {
        val titulo = "Hoy: " + lineas.first() + if (lineas.size > 1) " y ${lineas.size - 1} más" else ""
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_DIARIO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(titulo.replaceFirstChar { it.uppercase() })
            .setContentText(lineas.joinToString(" · "))
            .setStyle(NotificationCompat.BigTextStyle().bigText(lineas.joinToString("\n")))
            .setContentIntent(abrirApp(ctx, 2033, "personas"))
            .setAutoCancel(true)
        mostrar(ctx, 2033, b)
    }

    fun reporte(ctx: Context, titulo: String, uri: Uri, mime: String) {
        val ver = Intent(Intent.ACTION_VIEW).setDataAndType(uri, mime).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pi = PendingIntent.getActivity(ctx, 2006, ver, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val b = NotificationCompat.Builder(ctx, CANAL_REPORTES)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(titulo)
            .setContentText("Toca para verlo. Está guardado en tu galería.")
            .setContentIntent(pi)
            .setAutoCancel(true)
        mostrar(ctx, 2006, b)
    }

    fun logros(ctx: Context, nombres: List<String>) {
        if (nombres.isEmpty()) return
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_DIARIO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(if (nombres.size == 1) "${voz().logro}: ${nombres[0]}" else "${nombres.size} ${voz().logros}")
            .setContentText(nombres.take(4).joinToString(" · "))
            .setContentIntent(abrirApp(ctx, 2008, "logros"))
            .setAutoCancel(true)
        mostrar(ctx, 2008, b)
    }

    /** El 31 de diciembre por la mañana: «Tu año» ya está listo, con el sonido del tema. */
    fun tuAnio(ctx: Context, anio: Int) {
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_DIARIO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(voz().anio.format(anio))
            .setContentText(voz().anioTexto)
            .setContentIntent(abrirApp(ctx, 2009, "tu_anio"))
            .setAutoCancel(true)
        mostrar(ctx, 2009, b)
    }

    /** Una vez por semana: lo que toca importar, con «Recordar mañana». */
    fun importar(ctx: Context, fuentes: List<String>) {
        if (fuentes.isEmpty()) return
        crearCanales(ctx)
        val manana = PendingIntent.getBroadcast(ctx, 2022, Intent(ctx, RecordatorioReceiver::class.java).putExtra(Recordatorios.ACCION, Recordatorios.MANANA),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val b = NotificationCompat.Builder(ctx, CANAL_RECORDATORIOS)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(if (fuentes.size == 1) "Toca importar ${fuentes[0].substringBefore(" (")}" else "Toca importar ${fuentes.size} fuentes")
            .setContentText(fuentes.joinToString(" · "))
            .setStyle(NotificationCompat.BigTextStyle().bigText(fuentes.joinToString("\n")))
            .setContentIntent(abrirApp(ctx, ID_IMPORTAR, "datos"))
            .addAction(0, "Importar ahora", abrirApp(ctx, 2023, "datos"))
            .addAction(0, "Recordar mañana", manana)
            .setAutoCancel(true)
        mostrar(ctx, ID_IMPORTAR, b)
    }

    /** Metas que toca revisar y notas de recordatorio que llegaron a su fecha. */
    fun metas(ctx: Context, metas: List<String>, notas: List<String>) {
        crearCanales(ctx)
        val titulo = when {
            metas.isNotEmpty() && notas.isEmpty() -> if (metas.size == 1) "Revisa tu meta: ${metas[0]}" else "Toca revisar ${metas.size} metas"
            metas.isEmpty() -> if (notas.size == 1) "Recordatorio: ${notas[0]}" else "${notas.size} recordatorios para hoy"
            else -> "Metas y recordatorios para hoy"
        }
        val b = NotificationCompat.Builder(ctx, CANAL_RECORDATORIOS)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(titulo)
            .setContentText((metas + notas).take(4).joinToString(" · "))
            .setContentIntent(abrirApp(ctx, ID_METAS, if (metas.isNotEmpty()) "metas" else "notas"))
            .setAutoCancel(true)
        mostrar(ctx, ID_METAS, b)
    }

    /** El recordatorio diario de suplementos: «Ya los tomé» los anota con su dosis habitual; «En una hora» lo repite. */
    fun suplementos(ctx: Context, nombres: List<String>) {
        crearCanales(ctx)
        fun accion(req: Int, a: String) = PendingIntent.getBroadcast(ctx, req, Intent(ctx, RecordatorioReceiver::class.java).putExtra(Recordatorios.ACCION, a),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val b = NotificationCompat.Builder(ctx, CANAL_RECORDATORIOS)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(if (nombres.size == 1) "¿Ya tomaste ${nombres[0].lowercase()}?" else "¿Ya tomaste tus suplementos?")
            .setContentText(nombres.joinToString(" · "))
            .setContentIntent(abrirApp(ctx, ID_SUPLEMENTOS, "suplementos"))
            .addAction(0, "Ya los tomé", accion(2041, Recordatorios.TOME))
            .addAction(0, "En una hora", accion(2042, Recordatorios.UNA_HORA))
            .setAutoCancel(true)
        mostrar(ctx, ID_SUPLEMENTOS, b)
    }

    /** Lo que anotaron «Ya los tomé» o el mosaico, con «deshacer» durante 10 minutos. */
    fun tomasAnotadas(ctx: Context, tomas: List<io.github.ruddy2003.vestigium.data.TomaSuplemento>) {
        crearCanales(ctx)
        val quitar = PendingIntent.getBroadcast(ctx, 2043, Intent(ctx, RecordatorioReceiver::class.java).putExtra(Recordatorios.ACCION, Recordatorios.DESHACER_TOMAS)
            .putExtra(Recordatorios.TOMAS, tomas.map { it.id }.toTypedArray()), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val b = NotificationCompat.Builder(ctx, CANAL_RECORDATORIOS)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("Anotado")
            .setContentText(io.github.ruddy2003.vestigium.data.Suplementos.resumen(tomas))
            .setContentIntent(abrirApp(ctx, ID_SUPLEMENTOS, "suplementos"))
            .addAction(0, "Deshacer", quitar)
            .setTimeoutAfter(10 * 60_000L)
            .setOnlyAlertOnce(true).setSilent(true)
            .setAutoCancel(true)
        mostrar(ctx, ID_SUPLEMENTOS, b)
    }

    /** «¿Es un lugar especial?»: abre la hoja para nombrarlo, ignorarlo o dejarlo para después. */
    fun lugar(ctx: Context, sugerencia: String, minutos: Int) {
        crearCanales(ctx)
        val discreto = io.github.ruddy2003.vestigium.data.Repo.config.discreto
        val b = NotificationCompat.Builder(ctx, CANAL_LUGARES)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("¿Es un lugar especial?")
            .setContentText(if (discreto || sugerencia.isBlank()) "Llevas $minutos minutos aquí. Toca para nombrarlo o ignorarlo." else "Llevas $minutos minutos cerca de $sugerencia. Toca para nombrarlo.")
            .setContentIntent(abrirApp(ctx, ID_LUGAR, "lugar_nuevo"))
            .setAutoCancel(true)
            .setTimeoutAfter(8 * 60 * 60 * 1000L)
        mostrar(ctx, ID_LUGAR, b)
    }

    /** Los respaldos automáticos dejaron de salir: hay datos privados y no hay contraseña. */
    fun respaldoPausado(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, CANAL_REPORTES)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("Tus respaldos están en pausa")
            .setContentText("Llevan datos privados y no tienen contraseña. Toca para ponerla.")
            .setContentIntent(abrirApp(ctx, 2031, "datos"))
            .setAutoCancel(true)
        mostrar(ctx, 2031, b)
    }

    /** El respaldo automático de Drive vino mucho más pobre que el último y se guardó aparte. */
    fun driveEmpobrecido(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, CANAL_REPORTES)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("No reemplacé tu respaldo de Drive")
            .setContentText("El nuevo trae muchos menos días o momentos. Toca para revisarlo.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(io.github.ruddy2003.vestigium.data.Drive.EMPOBRECIDO))
            .setContentIntent(abrirApp(ctx, 2032, "datos"))
            .setAutoCancel(true)
        mostrar(ctx, 2032, b)
    }

    fun drive(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, CANAL_REPORTES)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("El respaldo en Drive necesita tu permiso")
            .setContentText("Google pidió confirmar el acceso. Toca para volver a conectar.")
            .setContentIntent(abrirApp(ctx, 2007, "datos"))
            .setAutoCancel(true)
        mostrar(ctx, 2007, b)
    }
}
