package io.github.ruddy2003.vestigium.avisos

import io.github.ruddy2003.vestigium.data.porNumero

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
import io.github.ruddy2003.vestigium.ui.Cara
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.MainActivity
import io.github.ruddy2003.vestigium.ui.ModoHora
import io.github.ruddy2003.vestigium.ui.QuickActivity
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.caraSegun
import io.github.ruddy2003.vestigium.ui.esquemaDe
import java.time.LocalTime

object Avisos {
    const val CANAL_ANIMO = "animo"
    const val CANAL_DIARIO = "diario"
    const val CANAL_ESCALAS = "escalas"
    /** El reporte semanal o mensual ya está listo. Hasta el 16 nov 2026, un canal fijo con importancia baja (sin sonido). */
    const val CANAL_REPORTES = "reportes"
    /**
     * Lugares especiales. Antes eran dos canales fijos, «lugares_especiales» (sonido por número) y «lugares_especiales_2»
     * (`n_lugar`); los dos son de esta familia («lugares_…») y se mudan al canal por tema la primera vez.
     */
    const val CANAL_LUGARES = "lugares"
    /** Importaciones, metas, notas de recordatorio y suplementos. Hasta el 16 nov 2026, un canal fijo con el sonido del teléfono. */
    const val CANAL_RECORDATORIOS = "recordatorios"
    /** Respaldos que no salen o vienen incompletos, Drive sin permiso, la base dañada. Antes salían por «reportes», sin sonido. */
    const val CANAL_RESPALDOS = "respaldos"
    /**
     * Parte 8 (`docs/diseno/CUADROS_PLAN.md`, encargo A): el día especial de un personaje que sigues. Una familia más, con su
     * canal por tema («historia_<tema>_<serie>») y su sonido propio, `n_<tema>_historia`. **Todavía no está en [FAMILIAS] ni en
     * [tono]**: entra cuando existan los 38 sonidos (si entrara antes, el canal nacería con un sonido que no existe).
     */
    const val CANAL_HISTORIA = "historia"
    const val ID_IMPORTAR = 2020
    const val ID_METAS = 2021
    const val ID_LUGAR = 2030
    const val ID_SUPLEMENTOS = 2040

    /**
     * Sonido de cada tema: el corto (`aviso`) y el solemne; qué familia lleva cuál, en [tono]. null: el tema no tiene sonidos
     * propios (Básico) y los avisos suenan con el sonido normal del teléfono.
     */
    private fun sonidosDe(t: TemaId): Pair<Int, Int>? = when (t) {
        TemaId.ROMANO -> R.raw.n_romano_aviso to R.raw.n_romano_solemne
        TemaId.URBE -> R.raw.n_urbe_aviso to R.raw.n_urbe_solemne
        TemaId.CIBER -> R.raw.n_ciber_aviso to R.raw.n_ciber_solemne
        TemaId.NEON -> R.raw.n_neon_aviso to R.raw.n_neon_solemne
        TemaId.HELENO -> R.raw.n_heleno_aviso to R.raw.n_heleno_solemne
        TemaId.EGIPTO -> R.raw.n_egipto_aviso to R.raw.n_egipto_solemne
        TemaId.MEDIEVAL -> R.raw.n_medieval_aviso to R.raw.n_medieval_solemne
        TemaId.TEMPLARIO -> R.raw.n_templario_aviso to R.raw.n_templario_solemne
        TemaId.DISTOPIA -> R.raw.n_distopia_aviso to R.raw.n_distopia_solemne
        TemaId.PSICODELIA -> R.raw.n_psicodelia_aviso to R.raw.n_psicodelia_solemne
        TemaId.NOUVEAU -> R.raw.n_nouveau_aviso to R.raw.n_nouveau_solemne
        TemaId.GALIA -> R.raw.n_galia_aviso to R.raw.n_galia_solemne
        TemaId.ARCONTES -> R.raw.n_arcontes_aviso to R.raw.n_arcontes_solemne
        TemaId.FRENTE -> R.raw.n_frente_aviso to R.raw.n_frente_solemne
        TemaId.REFUGIO -> R.raw.n_refugio_aviso to R.raw.n_refugio_solemne
        TemaId.ORDEN -> R.raw.n_orden_aviso to R.raw.n_orden_solemne
        TemaId.VAPOR -> R.raw.n_vapor_aviso to R.raw.n_vapor_solemne
        TemaId.LABORATORIO -> R.raw.n_laboratorio_aviso to R.raw.n_laboratorio_solemne
        TemaId.COLONOS -> R.raw.n_colonos_aviso to R.raw.n_colonos_solemne
        TemaId.NAPOLEON -> R.raw.n_napoleon_aviso to R.raw.n_napoleon_solemne
        TemaId.DECO -> R.raw.n_deco_aviso to R.raw.n_deco_solemne
        TemaId.BOTANICO -> R.raw.n_botanico_aviso to R.raw.n_botanico_solemne
        TemaId.RUINAS -> R.raw.n_ruinas_aviso to R.raw.n_ruinas_solemne
        TemaId.NAVIDAD -> R.raw.n_navidad_aviso to R.raw.n_navidad_solemne
        TemaId.SEDA -> R.raw.n_seda_aviso to R.raw.n_seda_solemne
        TemaId.EDO -> R.raw.n_edo_aviso to R.raw.n_edo_solemne
        TemaId.URSS -> R.raw.n_urss_aviso to R.raw.n_urss_solemne
        TemaId.GOTICO -> R.raw.n_gotico_aviso to R.raw.n_gotico_solemne
        TemaId.ARCANO -> R.raw.n_arcano_aviso to R.raw.n_arcano_solemne
        TemaId.ESPACIO -> R.raw.n_espacio_aviso to R.raw.n_espacio_solemne
        TemaId.ABISMO -> R.raw.n_abismo_aviso to R.raw.n_abismo_solemne
        TemaId.ANDALUS -> R.raw.n_andalus_aviso to R.raw.n_andalus_solemne
        TemaId.MESO -> R.raw.n_meso_aviso to R.raw.n_meso_solemne
        TemaId.MONGOL -> R.raw.n_mongol_aviso to R.raw.n_mongol_solemne
        TemaId.RENACIMIENTO -> R.raw.n_renacimiento_aviso to R.raw.n_renacimiento_solemne
        TemaId.PIRATA -> R.raw.n_pirata_aviso to R.raw.n_pirata_solemne
        TemaId.REVOLUCION -> R.raw.n_revolucion_aviso to R.raw.n_revolucion_solemne
        TemaId.ANTIGUAS -> R.raw.n_antiguas_aviso to R.raw.n_antiguas_solemne
        TemaId.CUEVA -> R.raw.n_cueva_aviso to R.raw.n_cueva_solemne
        TemaId.BASICO -> null                                                // sin sonido propio: el del teléfono
    }

    /** Los mismos sonidos por id de tema (lo revisa la prueba de cobertura). Sale del `when` de arriba: no puede faltar ninguno. */
    internal val SONIDOS: Map<String, Pair<Int, Int>> get() = TemaId.entries.mapNotNull { t -> sonidosDe(t)?.let { t.id to it } }.toMap()

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

    /** Las familias de avisos que tienen un canal por tema (ver [canal]): todas las que avisan de algo que hay que notar. */
    internal val FAMILIAS = listOf(CANAL_ANIMO, CANAL_DIARIO, CANAL_ESCALAS, CANAL_REPORTES, CANAL_RECORDATORIOS, CANAL_LUGARES,
        CANAL_RESPALDOS, io.github.ruddy2003.vestigium.data.Enfoque.CANAL_AVISO)

    /**
     * Qué sonido del tema lleva cada familia: el corto (`aviso`) para lo breve y frecuente, que pide un gesto rápido (ánimo,
     * fin de bloque, recordatorios, lugares, respaldos); el largo (`solemne`) para lo ceremonioso o de fin de día (diario,
     * sueños, cartas, logros, cuestionarios, el reporte listo). null: la familia no tiene canal por tema (el cronómetro fijo).
     */
    internal fun tono(base: String): String? = when (base) {
        CANAL_ANIMO, io.github.ruddy2003.vestigium.data.Enfoque.CANAL_AVISO, CANAL_RECORDATORIOS, CANAL_LUGARES, CANAL_RESPALDOS -> "aviso"
        CANAL_DIARIO, CANAL_ESCALAS, CANAL_REPORTES -> "solemne"
        else -> null
    }

    private fun nombreCanal(base: String) = when (base) {
        CANAL_ANIMO -> "Ánimo durante el día"; CANAL_DIARIO -> "Diario y sueños"; CANAL_ESCALAS -> "Escalas periódicas"
        CANAL_RECORDATORIOS -> "Importaciones y metas"; CANAL_LUGARES -> "Lugares especiales"
        CANAL_RESPALDOS -> "Respaldos y estado de los datos"
        io.github.ruddy2003.vestigium.data.Enfoque.CANAL_AVISO -> "Meta del bloque o del descanso"; else -> "Reportes"
    }
    private fun descripcionCanal(base: String) = when (base) {
        CANAL_ANIMO -> "Avisos breves para registrar cómo te sientes"; CANAL_DIARIO -> "Recordatorio de la mañana y de la noche"
        CANAL_ESCALAS -> "Cuestionarios que tocan"
        CANAL_RECORDATORIOS -> "Un aviso por semana con lo que toca importar, las metas y notas que toca revisar y los suplementos"
        CANAL_LUGARES -> "«¿Es un lugar especial?» cuando llevas un rato en un sitio nuevo"
        CANAL_RESPALDOS -> "Cuando un respaldo no sale o viene incompleto, Drive pide permiso o la base de datos tiene un problema"
        io.github.ruddy2003.vestigium.data.Enfoque.CANAL_AVISO -> "Avisa cuando un bloque o un descanso llega a su meta (y sigue contando) y cuando suena la siesta"
        else -> "Tu reporte semanal o mensual, cuando ya está listo"
    }
    /** La importancia con la que nace el canal de una familia. Todas suenan; ánimo y fin de bloque, además, asoman en pantalla. */
    internal fun importancia(base: String) = when (base) {
        CANAL_ANIMO, io.github.ruddy2003.vestigium.data.Enfoque.CANAL_AVISO -> NotificationManager.IMPORTANCE_HIGH
        else -> NotificationManager.IMPORTANCE_DEFAULT
    }

    /** Recurso de sonido para un canal según el tema elegido (null = sonido normal del teléfono). */
    fun sonido(base: String, tema: String = runCatching { io.github.ruddy2003.vestigium.data.Repo.config.tema }.getOrDefault("romano")): Int? {
        val par = sonidosDe(temaDe(tema)) ?: return null
        return when (tono(base)) {
            "aviso" -> par.first
            "solemne" -> par.second
            else -> null
        }
    }

    /**
     * El nombre del archivo de sonido (en `res/raw`) de un canal según el tema, o null (sonido del teléfono). Es el mismo que
     * [sonido], por nombre: los canales lo guardan por nombre (ver [uriSonido]).
     */
    internal fun nombreSonido(base: String, tema: String): String? {
        val t = temaDe(tema)
        if (sonidosDe(t) == null) return null
        return tono(base)?.let { "n_${t.id}_$it" }
    }

    /** Para oír un sonido ahora mismo (la prueba de sonido de Ajustes). */
    fun uriSonido(ctx: Context, res: Int): Uri = Uri.parse("android.resource://${ctx.packageName}/$res")

    /**
     * El sonido de un canal, **por nombre** («…/raw/n_romano_aviso»). Antes iba por número de recurso, y ese número cambia
     * de una versión a otra cuando se agregan o quitan sonidos: un canal creado con una versión vieja terminaba sonando con
     * el archivo de otro tema.
     */
    fun uriSonido(ctx: Context, nombre: String): Uri = Uri.parse("android.resource://${ctx.packageName}/raw/$nombre")

    /** El principio del id de los canales de [base] con [clave] (el tema, o «sistema»): «animo_medieval_». */
    internal fun prefijoCanal(base: String, clave: String) = base + "_" + clave + "_"

    /**
     * Un id que nunca se usó: el prefijo y la hora en base 36. Android **resucita** un canal borrado, con su sonido y ajustes
     * de entonces, si se vuelve a crear con el mismo id; al volver a un tema de días antes (lo normal con la rotación) salía
     * el canal viejo con su sonido viejo. Va la hora y no un contador porque un contador vuelve a 1 al borrar los datos.
     */
    internal fun idCanalNuevo(base: String, clave: String, ahora: Long) = prefijoCanal(base, clave) + java.lang.Long.toString(ahora, 36)

    /** De [ids], el canal vigente de [base] con [clave]: el más nuevo con ese prefijo, o null. */
    internal fun canalVigente(ids: Collection<String>, base: String, clave: String): String? {
        val pre = prefijoCanal(base, clave)
        return ids.filter { it.startsWith(pre) }.maxByOrNull { runCatching { it.removePrefix(pre).toLong(36) }.getOrDefault(0L) }
    }

    /** De [ids], los canales de la familia de [base] que sobran (todos menos [vigente]). «enfoque» no es de «enfoque_aviso». */
    internal fun canalesQueSobran(ids: Collection<String>, base: String, vigente: String): List<String> =
        ids.filter { (it == base || it.startsWith(base + "_")) && it != vigente }

    /** Las claves que puede llevar un canal por tema: los temas, «sistema» (sin sonido propio) y «muertos» (un id de antes). */
    private val CLAVES: Set<String> by lazy { TemaId.entries.map { it.id }.toSet() + "sistema" + "muertos" }

    /**
     * La serie de un canal por tema de [base] («animo_romano_<serie>»), o null si no es uno de ellos: los canales fijos de
     * antes («recordatorios», «lugares_especiales_2») y los de antes de la serie («animo_romano»). Solo cuenta si la clave
     * es un tema o «sistema»: en «lugares_especiales_2», «especiales» no es un tema y ese «2» no es una serie.
     */
    internal fun serialDe(id: String, base: String): Long? {
        if (!id.startsWith(base + "_")) return null
        val resto = id.removePrefix(base + "_")
        val clave = resto.substringBeforeLast('_', "")
        if (clave !in CLAVES) return null
        return resto.substringAfterLast('_').toLongOrNull(36)
    }

    /**
     * De [ids] (canales de la familia de [base]), el más reciente: el de serie más alta; los de antes de la serie, al final, y
     * entre ellos el de nombre más largo («lugares_especiales_2» va después de «lugares_especiales»). Android no devuelve los
     * canales en ningún orden, y desde que los viejos se borran unos segundos después ([borrarLuego]) puede haber varios vivos.
     */
    internal fun masReciente(ids: Collection<String>, base: String): String? =
        ids.maxWithOrNull(compareBy<String>({ serialDe(it, base) ?: -1L }, { it.length }))

    /**
     * Lo que se recuerda de una familia después de mirar uno de sus canales: true, lo silenciaste tú (sin sonido y Android
     * dice que el sonido lo cambiaste tú en sus ajustes); false, suena (si lo habías silenciado, ya le devolviste un sonido);
     * null, no se sabe nada nuevo (sin sonido, y no lo tocaste tú en ese canal o Android no lo dice).
     */
    internal fun recordarSilencio(sinSonido: Boolean, loElegisteTu: Boolean?): Boolean? = when {
        !sinSonido -> false
        loElegisteTu == true -> true
        else -> null
    }

    /**
     * ¿El canal que sigue nace en silencio? Solo si el anterior está sin sonido y ese silencio es tuyo: lo pusiste tú en ese
     * canal, o en uno anterior de la familia ([recordado]). Un silencio que nadie pidió (lo heredó de canal en canal desde un
     * error) se deja: vuelve el sonido del tema. En Android 11 no se puede saber quién lo silenció ([loElegisteTu] null) y se
     * respeta, como antes.
     */
    internal fun naceEnSilencio(sinSonido: Boolean, loElegisteTu: Boolean?, recordado: Boolean): Boolean =
        sinSonido && ((recordarSilencio(sinSonido, loElegisteTu) ?: recordado) || loElegisteTu == null)

    /**
     * La importancia del canal nuevo. Un canal por tema trae la del anterior (lo que decidiste, heredado de canal en canal;
     * todos nacen con [deFabrica]). Un canal fijo de antes solo la pasa si la cambiaste tú en sus ajustes: «reportes» nació
     * con importancia baja, que en Android es sin sonido, y no se hereda.
     */
    internal fun importanciaNueva(previa: Int?, deLaSerie: Boolean, laCambiasteTu: Boolean, deFabrica: Int): Int = when {
        previa == null -> deFabrica
        deLaSerie || laCambiasteTu -> previa
        else -> deFabrica
    }

    /** Lo que le queda a un aviso antes de borrarse solo: null si no tiene plazo; 0 o menos si ya venció. */
    internal fun plazoRestante(timeoutAfter: Long, postTime: Long, ahora: Long): Long? = if (timeoutAfter <= 0L) null else timeoutAfter - (ahora - postTime)

    /**
     * Pasa los avisos que siguen en la cortina de los canales [viejos] al [nuevo], en silencio y con el plazo que les quedaba.
     * Borrar un canal borra sus avisos: al cambiar de tema se perdía, por ejemplo, el del diario de la noche.
     */
    private fun mudarAvisos(ctx: Context, nm: NotificationManager, viejos: Collection<String>, nuevo: String) {
        if (viejos.isEmpty()) return
        val ahora = System.currentTimeMillis()
        runCatching { nm.activeNotifications }.getOrNull()?.forEach { sbn ->
            val n = sbn.notification
            if (n.channelId !in viejos) return@forEach
            val plazo = plazoRestante(n.timeoutAfter, sbn.postTime, ahora)
            if (plazo != null && plazo <= 0L) { runCatching { nm.cancel(sbn.tag, sbn.id) }; return@forEach }
            runCatching {
                val b = android.app.Notification.Builder.recoverBuilder(ctx, n).setChannelId(nuevo).setOnlyAlertOnce(true)
                if (plazo != null) b.setTimeoutAfter(plazo)
                nm.notify(sbn.tag, sbn.id, b.build())
            }
        }
    }

    private val atributosSonido: android.media.AudioAttributes get() = android.media.AudioAttributes.Builder()
        .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION).setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

    /** Las familias que silenciaste tú (para que el silencio siga en los canales de los temas que vienen). */
    private fun silencios(ctx: Context) = ctx.getSharedPreferences("avisos_silencio", Context.MODE_PRIVATE)

    /** ¿Android dice que el sonido de [ch] lo cambiaste tú? null en Android 11, que no lo dice. */
    private fun sonidoTuyo(ch: NotificationChannel): Boolean? =
        if (android.os.Build.VERSION.SDK_INT >= 31) runCatching { ch.hasUserSetSound() }.getOrNull() else null

    /** Mira [ch] (de [base]), guarda lo que haya cambiado ([recordarSilencio]) y dice si su silencio es tuyo. */
    private fun silencioDe(ctx: Context, base: String, ch: NotificationChannel, tuyo: Boolean?): Boolean {
        val sinSonido = ch.sound == null
        val p = silencios(ctx)
        val antes = p.getBoolean(base, false)
        val ahora = recordarSilencio(sinSonido, tuyo) ?: antes
        if (ahora != antes) p.edit().putBoolean(base, ahora).apply()
        return naceEnSilencio(sinSonido, tuyo, antes)
    }

    /** Familias cuyo canal vigente ya se rehízo en este proceso por estar mudo sin que lo pidieras (para no rehacerlo en bucle). */
    private val rehechos = mutableSetOf<String>()

    /**
     * El canal de [base] para el tema de hoy. Android no deja cambiar el sonido de un canal ya creado, así que hay uno por
     * tema («animo_medieval_<serie>»). Al cambiar de tema se crea uno nuevo, con id que nunca se usó, copiando lo que hayas
     * decidido en el más reciente de los anteriores ([masReciente]): importancia, vibración y, si lo silenciaste tú, el
     * silencio ([naceEnSilencio]); se le pasan los avisos que seguían en la cortina y se borran los viejos. Un canal vigente
     * que se quedó mudo sin que lo pidieras (un silencio heredado) se rehace una vez, con sonido. Lo usa también el aviso de
     * fin de bloque del enfoque («enfoque_aviso_<tema>_<serie>»). La primera vez, los canales fijos de antes de la familia
     * («recordatorios», «reportes», «lugares_especiales_2») se mudan aquí del mismo modo.
     */
    @Synchronized
    fun canal(ctx: Context, base: String): String {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        // Red de seguridad: el tema de hoy, aunque quien avisa no lo haya revisado.
        runCatching { io.github.ruddy2003.vestigium.ui.RotacionTema.revisar() }
        val ids = runCatching { nm.notificationChannels.map { it.id } }.getOrDefault(emptyList())
        // Con la base sin abrir bien, la configuración en memoria es la de fábrica: no se cambia nada de lo que hay. Solo si la
        // familia no tiene ningún canal se crea uno con el sonido del teléfono, para que el aviso no se pierda.
        if (io.github.ruddy2003.vestigium.data.Repo.errorAlAbrir != null) {
            masReciente(canalesQueSobran(ids, base, ""), base)?.let { return it }
            val id = idCanalNuevo(base, "sistema", System.currentTimeMillis())
            runCatching { nm.createNotificationChannel(NotificationChannel(id, nombreCanal(base), importancia(base)).apply { description = descripcionCanal(base) }) }
            return id
        }
        val c = runCatching { io.github.ruddy2003.vestigium.data.Repo.config }.getOrNull()
        val propio = c?.sonido_avisos ?: true
        val tema = c?.tema ?: "romano"
        val clave = if (propio) tema else "sistema"
        canalVigente(ids, base, clave)?.let { vigente ->
            val ch = runCatching { nm.getNotificationChannel(vigente) }.getOrNull()
            // Se mira siempre: así se recuerda si lo silenciaste tú, o si ya le devolviste un sonido.
            val tuSilencio = ch?.let { silencioDe(ctx, base, it, sonidoTuyo(it)) }
            val mudoSinPedirlo = ch != null && ch.sound == null && tuSilencio == false && base !in rehechos
            if (!mudoSinPedirlo) {
                // Si quedó algún canal viejo sin borrar (la app se cerró antes de [borrarLuego]), se borra ahora, ya sin avisos dentro.
                borrarVacios(nm, canalesQueSobran(ids, base, vigente))
                return vigente
            }
            rehechos += base           // sigue abajo: un canal nuevo del mismo tema, con sonido, que hereda lo demás de este
        }
        val viejos = canalesQueSobran(ids, base, "")
        val previo = masReciente(viejos, base)?.let { runCatching { nm.getNotificationChannel(it) }.getOrNull() }
        val deLaSerie = previo != null && serialDe(previo.id, base) != null
        // Un canal fijo de antes nunca nació sin sonido: si no tiene, lo quitaste tú.
        val tuyo = previo?.let { if (!deLaSerie && it.sound == null) true else sonidoTuyo(it) }
        val silencio = previo != null && silencioDe(ctx, base, previo, tuyo)
        val imp = importanciaNueva(previo?.importance, deLaSerie, previo?.let { runCatching { it.hasUserSetImportance() }.getOrNull() } ?: false, importancia(base))
        val id = idCanalNuevo(base, clave, System.currentTimeMillis())
        val ch = NotificationChannel(id, nombreCanal(base), imp).apply {
            description = descripcionCanal(base)
            when {
                silencio -> setSound(null, null)          // lo silenciaste tú: sigue en silencio
                propio -> nombreSonido(base, tema)?.let { setSound(uriSonido(ctx, it), atributosSonido) }
            }
            if (previo != null) { enableVibration(previo.shouldVibrate()); setShowBadge(previo.canShowBadge()) }
            else if (base == io.github.ruddy2003.vestigium.data.Enfoque.CANAL_AVISO) enableVibration(true)
        }
        nm.createNotificationChannel(ch)
        mudarAvisos(ctx, nm, viejos, id)
        borrarLuego(nm, viejos)
        return id
    }

    /** Lo que se espera antes de borrar los canales viejos: lo que tarda Android en recibir los avisos mudados. */
    private const val ESPERA_BORRAR_MS = 6000L

    /**
     * Borra los canales [viejos] un poco después, no enseguida. Android publica un aviso con retraso (lo pasa antes por su
     * asistente) y borrar un canal cancela al instante sus avisos: si el canal viejo se borraba justo después de mudar, el
     * aviso se cancelaba antes de que llegara su copia, la copia entraba como aviso **nuevo** y sonaba con el sonido del tema
     * recién elegido, encima de la música de la presentación. Con el canal viejo todavía vivo, la copia entra como una
     * actualización del mismo aviso y `setOnlyAlertOnce` la deja en silencio.
     */
    private fun borrarLuego(nm: NotificationManager, viejos: Collection<String>) {
        if (viejos.isEmpty()) return
        val lista = viejos.toList()
        // Sin hilo principal (una prueba) no se programa nada: quedan para la próxima llamada a [canal], que los borra al encontrarlos.
        runCatching { android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ borrarVacios(nm, lista) }, ESPERA_BORRAR_MS) }
    }

    /** De [ids], borra los canales que ya no tienen avisos en la cortina; los que todavía tienen alguno se dejan para después. */
    private fun borrarVacios(nm: NotificationManager, ids: Collection<String>) {
        if (ids.isEmpty()) return
        val conAvisos = runCatching { nm.activeNotifications.map { it.notification.channelId }.toSet() }.getOrNull() ?: return
        ids.filter { it !in conAvisos }.forEach { runCatching { nm.deleteNotificationChannel(it) } }
    }

    /**
     * Crea (o pone al día con el tema de hoy) el canal de cada familia. Ya no se crean canales fijos: «reportes»,
     * «recordatorios» y los de lugares se mudan al canal por tema de su familia la primera vez que pasa por [canal], y crearlos
     * otra vez los resucitaría (Android devuelve un canal borrado, con sus ajustes de entonces, si se crea con el mismo id).
     */
    fun crearCanales(ctx: Context) {
        FAMILIAS.forEach { runCatching { canal(ctx, it) } }
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
     * Las dos voces de Psicodelia (versión 2; `docs/diseno/temas/psicodelia.md`, «La voz»). La cara la decide solo la hora
     * del aviso (`caraSegun(tema, noche, 0f)`): un aviso no dice nada de un consumo, ni con las palabras ni con la voz.
     * - [Cara.BASE], la ciudad: la del barrio y del oficio, tranquila y concreta (la niebla, la mesa de dibujo, el periódico
     *   del barrio, el cartel que sale de la imprenta).
     * - [Cara.INTENSA], el cartel: la de la sala y la radio, un poco más suelta (la luz que se mueve en la pared, el 106.9,
     *   el lado B).
     * Ninguna nombra sustancias, celebra con jerga ni toca lo privado (lo vigila `VozPsicodeliaPrueba`).
     */
    internal fun vozPsicodelia(cara: Cara): Voz = when (cara) {
        Cara.BASE -> Voz("¿Cómo andas ahora?", "Dos toques, como quien levanta la vista de la mesa de dibujo: cuánto agrado y cuánta energía.",
            "Todavía no se retira la niebla · ¿qué soñaste?", "Anótalo antes de levantarte: se va como la niebla a mediodía.",
            "Se cierra la edición del barrio: ¿cómo te fue hoy?", "Escribe tu día para el periódico. Si fue de niebla espesa, basta con un número.",
            "Día de niebla espesa: un número", "Las preguntas del periódico, hoy:",
            "Llegó una postal de tu yo de antes", "Es el día que escogiste para abrirla. Toca para leer lo que te dejaste dicho.",
            "Disco de oro", "discos de oro nuevos",
            "Sale de la imprenta el cartel de tu %d", "Todo tu año en un cartel: lo que más sonó, lo único, lo curioso. Toca para verlo.")
        Cara.INTENSA -> Voz("Lado B · ¿cómo andas ahora?", "Dos toques, al compás de lo que suene: cuánto agrado y cuánta energía.",
            "Se despide el 106.9 · ¿qué soñaste?", "Anótalo antes de levantarte: se borra como la luz de la pared cuando apagan el proyector.",
            "Se encienden las luces de la pared: ¿cómo te fue hoy?", "Pasa tu día al periódico antes de que cierre. Si fue pesado, basta con un número.",
            "Día pesado: solo un número", "Las preguntas de la sala, hoy:",
            "Te llegó una postal de otra temporada", "Es la fecha que escogiste para abrirla. Toca para leerla con calma.",
            "Disco de oro", "discos de oro nuevos",
            "Sale el cartel de tu %d, a tres tintas", "Tu año entero en un cartel de concierto: lo que más sonó, lo único, lo curioso. Toca para verlo.")
    }

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
        TemaId.URBE -> Voz("COME STAI? · ¿cómo te sientes ahora?", "Dos toques, como quien mira el cielo antes de salir: qué tan agradable y cuánta energía.",
            "STANOTTE · ¿qué soñaste?", "Anótalo antes del primer café: el sueño se va con el ruido de las motonetas.",
            "LA GIORNATA · ¿cómo fue tu día?", "Cierra la jornada. Si fue pesada, basta con un número.",
            "Giornataccia: solo un número", "Oggi:",
            "POSTA · llegó una cartolina de tu yo del pasado", "Es la fecha que pusiste en el matasellos.",
            "Medaglia", "medaglie nuove",
            "IL TUO ANNO %d · ya está en el quiosco", "Un año entero en historias, como el anuario del periódico: lo que más hiciste, lo único, lo curioso. Toca para leerlo.")
        TemaId.CIBER -> Voz("> ping ánimo", "Dos toques: qué tan agradable y cuánta energía. _",
            "> dump de sueños", "Guárdalo antes de levantarte: la memoria se libera en minutos.",
            "> cierre del día", "Llena el día. Si fue un mal día, basta con un número.",
            "modo mínimo: 1 número", "> pendiente:",
            "> mensaje en cola de tu yo del pasado", "Se cumplió la fecha programada. Toca para descifrarlo.",
            "> logro desbloqueado", "logros desbloqueados",
            "> informe_%d listo", "Un año entero en historias: lo más frecuente, lo único, lo raro. Toca para ejecutar.")
        TemaId.NEON -> Voz("¿Cómo va la noche?", "Dos toques: qué tan bien estás y cuánta pila te queda.",
            "Lo que soñaste entre el ruido", "Apúntalo antes de salir a la calle: se borra rápido.",
            "Se apagan los letreros", "Cierra el ticket del día. Si fue malo, basta con un número.",
            "modo de guardia: 1 número", "Te falta:",
            "Llegó un recado de tu yo de antes", "Ya es la fecha: el repartidor trae tu carta. Toca para abrirla.",
            "¡Ficha nueva!", "fichas ganadas",
            "Tu año %d, en letreros", "Un año entero en historias: lo de siempre, lo único, lo raro. Toca para entrar.")
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
        TemaId.TEMPLARIO -> Voz("☩ Relevo de guardia: ¿cómo está tu ánimo?", "Dos toques, hermano: cuánto agrado y cuánto brío.",
            "☩ Lo que viste en la vigilia", "Inscríbelo antes de ensillar: el sueño se va como la arena entre los dedos.",
            "☩ Se cierra la puerta del castillo: ¿cómo fue la jornada?", "Rinde la jornada. Si fue dura, basta con un número.",
            "Jornada dura: un número", "Según la regla, hoy:",
            "☩ Misiva de tu yo de otro tiempo", "Llegó la jornada señalada para romper el sello de la casa.",
            "Hecho de armas", "hechos de armas nuevos",
            "La crónica del año %d está asentada", "Un año entero en la frontera: lo más hecho, lo único, lo curioso. Toca para leer la crónica.")
        TemaId.DISTOPIA -> Voz("DECLARE SU ESTADO ACTUAL", "Dos toques obligatorios: agrado y energía.",
            "REPORTE ONÍRICO PENDIENTE", "Declare lo soñado antes de levantarse. El recuerdo caduca en minutos.",
            "DECLARACIÓN NOCTURNA", "Complete el parte del día. En jornada difícil se acepta un solo número.",
            "Jornada difícil: un número", "INTERROGATORIO PENDIENTE:",
            "CORRESPONDENCIA DESCLASIFICADA", "Su yo del pasado le escribió. Se cumplió la fecha de apertura.",
            "CONDECORACIÓN", "condecoraciones nuevas",
            "BALANCE ANUAL %d DISPONIBLE", "Un año entero en expedientes: lo más frecuente, lo único, lo anómalo.")
        // Psicodelia: dos voces, la de la cara que toca a la hora del aviso (sin contar ningún consumo: ver [vozPsicodelia]).
        TemaId.PSICODELIA -> vozPsicodelia(caraSegun(t, ModoHora.esDeNoche(LocalTime.now()), 0f))
        // Art nouveau: la campanilla de cristal del taller, la piedra litográfica que se moja antes de entintar, las vitrinas
        // que se encienden de noche, la postal ilustrada, las medallas de la Exposición y el cartel recién salido de la prensa.
        TemaId.NOUVEAU -> Voz("Suena la campanilla de cristal: ¿cómo estás ahora?", "Dos toques, como dos golpes de pincel: cuánto agrado y cuánta energía.",
            "Croquis de la noche · ¿qué soñaste?", "Dibújalo con palabras antes de levantarte: el sueño se seca como el agua sobre la piedra.",
            "Se encienden las vitrinas: ¿cómo fue tu día?", "Pasa el día a la página del carnet. Si fue gris, basta con un número.",
            "Día gris: un número", "Las preguntas del almanaque, hoy:",
            "Llegó una postal de tu yo de antes", "Es el día que elegiste para leerla. Toca para darle la vuelta.",
            "Medalla de la Exposición", "medallas de la Exposición",
            "Tu %d ya salió de la prensa", "Un año entero en carteles, recién impresos: lo que más hiciste, lo único, lo curioso. Toca para leerlo.")
        // Galia: el grito que corre de campo en campo (César 7.3: lo de Cenabum, al amanecer, lo sabían antes de acabar la
        // primera guardia de la noche a 160 millas); la hoguera de la casa grande, el mensajero, los torques y los bardos.
        TemaId.GALIA -> Voz("Corre el grito: ¿cómo anda tu ánimo?", "Pasa de campo en campo hasta llegar a ti. Dos toques: cuánto agrado y cuánta energía.",
            "¿Qué te contó la noche?", "Dilo antes de dejar el manto: el sueño se va con la niebla de la mañana.",
            "Se enciende la hoguera: ¿cómo fue la jornada?", "Cuenta el día junto al fuego. Si fue jornada de lluvia y barro, basta con un número.",
            "Jornada de barro: un número", "Lo que hoy preguntan los druidas:",
            "Llega el mensajero: trae palabras de tu yo de antes", "Es la noche que elegiste para abrir su tablilla. Toca para leerla.",
            "Torques ganado", "torques ganados",
            "Los bardos ya cantan tu %d", "Un año entero en cantos, como los que guardan los bardos: lo que más hiciste, lo único, lo curioso. Toca para leerlo.")
        // Arcontes: la voz baja del círculo de Magdala; la lámpara de la noche, el códice, la jarra sellada y los amuletos.
        TemaId.ARCONTES -> Voz("¿Cómo está tu alma en esta hora?", "Dos toques, en voz baja: cuánto agrado sientes y cuánta energía te queda.",
            "Lo que viste mientras dormía el cuerpo", "Escríbelo antes de levantarte: lo que se ve de noche se va con la primera luz.",
            "Se enciende la lámpara: ¿cómo fue el día?", "Escribe el día en el códice. Si fue pesado, basta con un número.",
            "Día de sombra: un número", "Las preguntas de los guardianes, hoy:",
            "Se rompe el sello de la jarra", "Es el cielo que elegiste para leer la carta que te dejaste.",
            "Amuleto grabado", "amuletos grabados nuevos",
            "La vuelta del año %d ya está escrita", "Un año entero en historias, como las hojas de un códice: lo que más hiciste, lo único, lo curioso. Toca para leerlo.")
        TemaId.FRENTE -> Voz("CANAL ABIERTO · ¿CÓMO ESTÁS AHORA?", "Dos toques y cortas: qué tan agradable y cuánta energía.",
            "SEÑAL DE LA NOCHE · ¿QUÉ SOÑASTE?", "Transmítelo antes de levantarte. La señal se pierde en minutos.",
            "SE ABRE LA VENTANA · ¿CÓMO FUE EL DÍA?", "Transmite el día antes de cortar. Si fue malo, basta con un número.",
            "Día malo: solo un número", "CUESTIONARIOS DE CONTROL:",
            "LLEGÓ UN MENSAJE DIFERIDO", "Lo cifraste tú hace tiempo, para abrirlo hoy. Toca para descifrarlo.",
            "PARCHE NUEVO", "parches nuevos",
            "INFORME DEL AÑO %d · LISTO", "Un año entero de transmisiones: lo que más hiciste, lo único, lo raro. Toca para leerlo.")
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
        TemaId.VAPOR -> Voz("☛ Lectura del manómetro · ¿qué presión marca tu ánimo?", "Dos toques en la esfera: qué tan agradable y cuánto vapor tienes.",
            "☛ Al encender la caldera · ¿qué soñaste?", "Anótalo antes de que silbe el primer tren: el sueño se va con el humo.",
            "☛ Fin del turno · ¿cómo fue la jornada?", "Asienta el día en el cuaderno. Si fue de carbón y sudor, basta con un número.",
            "Jornada de mucho carbón: un número", "Lecturas de este turno:",
            "☛ Telegrama de tu yo del pasado", "El cartero de la estación lo trae en la fecha que pusiste.",
            "Patente concedida", "patentes nuevas",
            "La memoria de la compañía, ejercicio %d", "Un año entero en estampas de viaje: lo que más hiciste, lo único, lo curioso. Toca para leerla.")
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
        TemaId.DECO -> Voz("Timbre del camarote · ¿qué tal la velada?", "Dos toques, como dos compases: agrado y energía.",
            "Lo que soñaste en el camarote", "Anótalo antes del desayuno en cubierta; los sueños se van con la bruma.",
            "El carnet de esta noche", "Llena el día antes de que baje el telón. Si fue una mala función, basta con un número.",
            "Mala función: un número", "El sobrecargo te entrega el programa:",
            "Telegrama de tu yo del pasado", "La radio de a bordo lo recibe el día fijado.",
            "¡Ovación!", "ovaciones nuevas",
            "La temporada de %d en noticiero", "Toda la temporada en historias: lo más hecho, lo único, lo curioso.")
        TemaId.BOTANICO -> Voz("Observación · ¿cómo te sientes?", "Dos toques, como una muestra de campo: agrado y energía.",
            "Latencia · ¿qué soñaste?", "Anótalo antes de levantarte; se marchita en minutos.",
            "Entrada del herbario", "Llena el día. Si fue difícil, basta con un número.",
            "Día difícil: un número", "Muestreo de hoy:",
            "Una carta prensada por tu yo del pasado", "Llegó la estación que elegiste para abrirla.",
            "Especie nueva en la colección", "especies nuevas",
            "El herbario de %d está listo", "Un año entero en láminas: lo más hecho, lo único, lo curioso.")
        TemaId.RUINAS -> Voz("❦ ¿Cuánto aguante te queda?", "Dos toques, como dos marcas de tiza: agrado y energía.",
            "❦ ¿Qué soñaste en la azotea?", "Apúntalo antes de bajar; se lo lleva el viento en minutos.",
            "❦ Una hoja más del cuaderno", "Llena el día junto a la fogata. Si fue difícil, basta con un número.",
            "Día difícil: una marca basta", "La ronda de hoy:",
            "Un mensaje tapiado por tu yo del pasado", "Llegó la estación que elegiste para destapiarlo.",
            "Rescate nuevo entre los escombros", "rescates nuevos",
            "Las hojas prensadas de %d están listas", "Un año entero tras el silencio: lo que más hiciste, lo único, lo raro.")
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
        TemaId.EDO -> Voz("※ Un alto en la casa de té: ¿cómo late tu corazón?", "Dos toques, como dos sellos: agrado y fuerza para el camino.",
            "※ ¿Qué soñaste en la posada?", "Anótalo antes de doblar el futón; los sueños se van con el rocío.",
            "※ La hoja de la jornada", "Llena el día a pincel. Si la cuesta fue dura, basta un número.",
            "Cuesta dura: basta un número", "En el tablón de la posta:",
            "※ El correo a pie trae carta de tu yo del pasado", "Llegó en la estación convenida.",
            "Un sello nuevo en tu cuaderno", "sellos nuevos",
            "Las estampas de %d ya están impresas", "Un año entero del camino en ukiyo-e: lo más hecho, lo único, lo curioso.")
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
        TemaId.ARCANO -> Voz("† Una nota al margen · ¿cómo están tus nervios?", "Dos toques antes de que se te olvide: agrado y energía.",
            "† Lo que soñaste anoche", "Anótalo antes de levantarte: lo que se sueña cerca del mar se borra con la primera luz.",
            "† Cierra el legajo del día", "Asienta la jornada. Si fue de las turbias, basta con una cifra.",
            "Jornada turbia: una cifra", "Lo que hoy toca vigilar:",
            "† Llegó una carta lacrada de tu yo del pasado", "La trajo la marea en la fecha convenida. Rompe el lacre.",
            "Prueba hallada", "pruebas nuevas en el archivo",
            "Crónica del caso, %d", "Un año entero en recortes: lo más repetido, lo inexplicable, lo curioso.")
        TemaId.ESPACIO -> Voz("CONTROL · estado de la tripulación", "Dos toques: agrado y energía. Cambio.",
            "BITÁCORA DE SUEÑO", "Regístralo antes de dejar la litera; la señal se pierde en minutos.",
            "FIN DE ÓRBITA", "Llena el día. Si hubo turbulencia, basta con un número.",
            "Turbulencia: un número", "SECUENCIA PROGRAMADA:",
            "TRANSMISIÓN DIFERIDA de tu yo del pasado", "La señal llegó en la fecha calculada.",
            "Hito de misión", "hitos de misión",
            "Misión %d · informe listo", "Una vuelta completa al Sol en historias: lo más hecho, lo único, lo curioso.")
        TemaId.ABISMO -> Voz("SONDEO · ¿cómo está el buzo?", "Dos toques en el panel de la góndola: agrado y energía.",
            "BITÁCORA DE SUPERFICIE · ¿qué soñaste?", "Anótalo antes de dejar la litera; los sueños suben y se pierden como burbujas.",
            "FIN DE LA INMERSIÓN · el día", "Llena el día. Si hubo mar de fondo, basta con un número.",
            "Mar de fondo: un número", "SONDEO PROGRAMADO:",
            "Boya lastrada de tu yo del pasado", "Soltó su lastre y subió en la fecha fijada.",
            "Nueva cota", "cotas nuevas",
            "Bitácora de inmersiones de %d", "Un año entero bajo el agua en historias: lo más hecho, lo único, lo curioso.")
        TemaId.ANDALUS -> Voz("الآن · ¿cómo te sientes?", "Dos toques: cuánta dulzura y cuánta fuerza.",
            "المنام · ¿qué soñaste?", "Escríbelo antes de levantarte; el sueño se va como el agua de la acequia.",
            "النهار · ¿cómo fue el día?", "Llena el día. Si fue duro, basta con un número.",
            "Día duro: un número", "Hoy, en la madraza:",
            "رسالة · carta de tu yo del pasado", "Llegó el día que elegiste para abrirla.",
            "Azulejo nuevo en tu patio", "azulejos nuevos",
            "El libro de %d está listo", "Un año entero en historias: lo más hecho, lo único, lo curioso.")
        TemaId.MESO -> Voz("⁘ Suena la caracola: ¿cómo está tu corazón?", "Dos toques: cuánto agrado y cuánta fuerza trae este día del tzolk'in.",
            "⁘ Lo que pintó la noche", "Anótalo antes de dejar el petate: los sueños se van con el humo del copal.",
            "⁘ La página de hoy del códice", "Pinta el día antes de que el sol baje al inframundo. Si fue un día pesado, basta con un número de puntos y barras.",
            "Día pesado: un número", "El guardián de los días pregunta hoy:",
            "⁘ Un códice atado de tu yo del pasado", "Llegó el día de la cuenta que elegiste para desatarlo.",
            "Pluma de quetzal ganada", "plumas de quetzal nuevas",
            "El códice de %d está pintado", "Un año entero pintado como en las páginas de un biombo: lo que más hiciste, lo único, lo curioso.")
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
        TemaId.PIRATA -> Voz("✷ Toque de campana · ¿qué cariz trae tu ánimo?", "Dos toques en la carta: cuánta bonanza y cuánto viento en las velas.",
            "✷ Desde el coy · ¿qué soñaste?", "Asiéntalo antes de saltar a cubierta; el sueño se va como la espuma.",
            "✷ Fin de la singladura", "Llena la bitácora. Si hubo temporal, basta con un número.",
            "Singladura con temporal: un número", "Órdenes de esta guardia:",
            "✷ Una botella llegó a la playa: te escribe tu yo del pasado", "La marea la trajo en la fecha que elegiste.",
            "Botín a bordo", "botines nuevos",
            "La bitácora de %d, encuadernada", "Un año entero de travesía: lo más hecho, lo único, lo curioso.")
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
        TemaId.CUEVA -> Voz("⁘ Junto al fuego · ¿cómo arde tu aliento?", "Dos toques en la piedra: cuánto agrado y cuánta fuerza.",
            "⁘ Lo que se ve con los ojos cerrados", "Cuéntalo antes de salir del abrigo; el sueño se va como el humo.",
            "⁘ Cae el sol sobre el valle", "Pinta el día. Si fue de mala caza, basta con un número.",
            "Sol de mala caza: un número", "Lo que hay que hacer este sol:",
            "⁘ Desenterraste una piedra marcada por tu yo de otras lunas", "Llegó la luna que elegiste para sacarla.",
            "Presa nueva en la pared", "presas nuevas",
            "Las lunas de %d ya están en la pared", "Un año entero en pinturas: lo más hecho, lo único, lo curioso.")
        TemaId.BASICO -> Voz("¿Cómo te sientes ahora?", "Dos toques: qué tan agradable y cuánta energía.",
            "¿Qué soñaste?", "Anótalo antes de levantarte; los sueños se olvidan rápido.",
            "¿Cómo fue tu día?", "Llena el día. Si fue difícil, basta con un número.",
            "Día difícil: solo un número", "Hoy:",
            "Carta de tu yo del pasado", "Llegó la fecha que elegiste para abrirla.",
            "Logro nuevo", "logros nuevos",
            "Tu año %d está listo", "Un año entero en historias: lo que más hiciste, lo único, lo curioso. Toca para leerlo.")
    }

    /**
     * El cronómetro de enfoque en la voz del tema: nombre del descanso, consejo, fin del descanso, despertar, fin y
     * [meta] (esquema 21): el aviso de que un bloque llegó a su meta; desde ahí sigue contando hasta que pases al descanso o termines.
     */
    internal class VozEnfoque(val descanso: String, val consejo: String, val finDescanso: String, val despertar: String, val terminaste: String, val meta: String)

    /**
     * El cronómetro de enfoque de Psicodelia, en la voz de la cara que toca a la hora del aviso (sin contar ningún consumo):
     * de día, la mesa de dibujo, la niebla y el cartel que va a la imprenta; de noche, el disco que se da vuelta en la sala y
     * el cartel recién impreso. El descanso es darle la vuelta al disco en las dos.
     */
    internal fun vozEnfoquePsicodelia(cara: Cara): VozEnfoque = when (cara) {
        Cara.BASE -> VozEnfoque("Intermedio: dale la vuelta al disco", "Suelta la plumilla, asómate a la ventana y bebe un vaso de agua.",
            "Suena el otro lado: de vuelta a la mesa", "La niebla ya cruzó el puente: hora de despertar",
            "Cartel listo para la imprenta: terminaste", "Fin de la pista: llegaste a la meta")
        Cara.INTENSA -> VozEnfoque("Intermedio: el disco, del otro lado", "Deja la plumilla, abre la ventana y bebe un vaso de agua.",
            "Vuelve a girar el disco: de vuelta a la mesa", "Se prende la luz de la sala: hora de despertar",
            "El cartel queda impreso: terminaste", "Se acaba la pista: llegaste a la meta")
    }

    internal fun vozEnfoque(t: TemaId = temaActual()): VozEnfoque = when (t) {
        TemaId.ROMANO -> VozEnfoque("Otium", "Levántate y bebe agua, como en las termas.", "Se acabó el otium", "Surge: hora de despertar", "Opus perfectum: terminaste", "Ad metam: llegaste a la meta")
        TemaId.URBE -> VozEnfoque("Pausa caffè", "Levántate y bebe agua del nasone, como en la calle.", "Fine della pausa", "Sveglia! Hora de despertar", "Fatto! Terminaste", "Meta raggiunta: llegaste")
        TemaId.CIBER -> VozEnfoque("> pausa", "Despega la vista de la pantalla. Agua.", "> pausa terminada", "> wake: hora de despertar", "> proceso completado", "> meta alcanzada")
        TemaId.NEON -> VozEnfoque("Respira, sal a la banqueta", "Despega la vista del letrero. Un vaso de agua.", "De vuelta al puesto", "Sube la persiana: hora de despertar", "Pedido terminado", "Luz verde: llegaste a la meta")
        TemaId.HELENO -> VozEnfoque("Σχολή · pausa", "Levántate, bebe agua de la fuente.", "Terminó la pausa", "Llega Eos: hora de despertar", "Τέλος · terminaste", "Τέρμα · llegaste a la meta")
        TemaId.EGIPTO -> VozEnfoque("Reposo del escriba", "Deja el cálamo, bebe agua de la jarra.", "Vuelve a la paleta", "Ra sale del horizonte: hora de despertar", "Columna terminada", "El escriba llegó al final de la línea")
        TemaId.MEDIEVAL -> VozEnfoque("Recreo", "Deja la pluma, bebe agua.", "Se acabó el recreo", "Hora de levantarse", "Laus Deo: terminaste", "Explicit: llegaste a la meta")
        TemaId.TEMPLARIO -> VozEnfoque("Alto en la marcha", "Desciñe la espada y bebe agua del aljibe.", "Se acabó el alto: a la silla", "Toca a maitines: hora de levantarse", "Non nobis: guardia cumplida", "Llegaste al castillo: meta cumplida")
        TemaId.DISTOPIA -> VozEnfoque("PAUSA AUTORIZADA", "Hidrátese. Regrese a su puesto a tiempo.", "FIN DE LA PAUSA AUTORIZADA", "TOQUE DE DIANA", "CUOTA CUMPLIDA", "OBJETIVO DEL BLOQUE ALCANZADO")
        // Psicodelia: dos voces, la de la cara que toca a la hora del aviso (ver [vozEnfoquePsicodelia]).
        TemaId.PSICODELIA -> vozEnfoquePsicodelia(caraSegun(t, ModoHora.esDeNoche(LocalTime.now()), 0f))
        // Art nouveau: el respiro en la terraza del café, la mesa de dibujo, la luz del tragaluz, la piedra entintada y el iris
        // que se abre en la esfera del reloj.
        TemaId.NOUVEAU -> VozEnfoque("Un respiro en la terraza", "Deja el lápiz, sal a la calle un momento y bebe un vaso de agua.", "De vuelta a la mesa de dibujo", "Entra la luz por el tragaluz: hora de despertar", "La piedra está entintada: terminaste", "Se abre el iris: llegaste a la meta")
        // Galia: el alto a la sombra del roble, los versos que se aprenden de memoria (César 6.14), la noche que va antes del
        // día (6.18) y el carnyx que suena al llegar.
        TemaId.GALIA -> VozEnfoque("Alto a la sombra del roble", "Suelta la tablilla, estira las piernas y bebe agua del manantial.", "Se acabó el alto: de vuelta a los versos", "Tras la noche viene el día: hora de despertar", "Ya lo sabes de memoria: terminaste", "Suena el carnyx: llegaste a la meta")
        TemaId.ARCONTES -> VozEnfoque("Baja la mecha: descanso", "Deja el códice, sal a la orilla y bebe agua de la jarra.", "Sube la mecha: de vuelta al códice", "Clarea sobre el lago: hora de despertar", "Hoja terminada: cierra el códice", "Una esfera más: llegaste a la meta")
        TemaId.FRENTE -> VozEnfoque("SILENCIO DE RADIO", "Suelta los mandos. Agua y estira la espalda.", "SE REABRE EL CANAL", "SE ENCIENDE LA CONSOLA: HORA DE DESPERTAR", "TRANSMISIÓN CERRADA: TERMINASTE", "EN POSICIÓN: LLEGASTE A LA META")
        TemaId.REFUGIO -> VozEnfoque("DESCANSO DE TURNO", "Levántate, bebe agua filtrada.", "FIN DEL DESCANSO", "HORA DE DESPERTAR, RESIDENTE", "TURNO COMPLETADO", "META DE TURNO ALCANZADA")
        TemaId.ORDEN -> VozEnfoque("ALTO", "Rompan filas: agua y estiramiento.", "FIN DEL ALTO", "DIANA", "MISIÓN CUMPLIDA", "OBJETIVO ALCANZADO")
        TemaId.VAPOR -> VozEnfoque("Parada en la estación", "Baja al andén: estira las piernas y bebe agua del aguador.", "¡Viajeros al tren!", "Silba la locomotora: hora de despertar", "Llegada a destino: terminaste", "Estación a la vista: llegaste a la meta")
        TemaId.LABORATORIO -> VozEnfoque("Intervalo", "Levántate, hidrátate.", "Fin del intervalo", "Fin del registro de sueño: despierta", "Ensayo completado", "Tiempo objetivo alcanzado")
        TemaId.COLONOS -> VozEnfoque("Descanso", "Levántate, toma agua del pozo.", "Se acabó el descanso", "Hora de despertar", "¡Faena terminada!", "¡Meta de la faena cumplida!")
        TemaId.NAPOLEON -> VozEnfoque("Halte", "Levántate y bebe, soldado.", "Fin de la halte", "Diana: ¡arriba!", "Victoire!", "Objectif atteint!")
        TemaId.DECO -> VozEnfoque("Entreacto", "Levántate, da una vuelta por cubierta y bebe agua.", "Se acabó el entreacto", "Suena la campana del desayuno: hora de despertar", "¡Telón! Función terminada", "¡Fin del acto!")
        TemaId.BOTANICO -> VozEnfoque("Pausa de campo", "Levántate y riégate: toma agua.", "Termina la pausa de campo", "Hora de despertar, como las flores", "Muestra completa", "Muestra del bloque recogida")
        TemaId.RUINAS -> VozEnfoque("Sombra bajo el puente", "Levántate y bebe del agua de lluvia que guardaste.", "Se acabó la sombra", "Ya salió el sol sobre las azoteas", "Faena cumplida, sigues en pie", "Llegaste a la azotea: meta cumplida")
        TemaId.NAVIDAD -> VozEnfoque("Descanso junto al fuego", "Levántate, toma algo caliente o agua.", "Se acabó el descanso junto al fuego", "Hora de despertar", "¡Terminaste!", "¡Llegaste a la meta! Otra vela encendida")
        TemaId.SEDA -> VozEnfoque("Alto en el caravasar", "Levántate, bebe agua del pozo.", "La caravana sigue", "Hora de levantar el campamento", "Jornada cumplida", "La caravana llegó al oasis")
        TemaId.EDO -> VozEnfoque("Un té en la casa de té", "Levántate, estira las piernas, bebe agua del pozo.", "Se reanuda el camino", "Suena la campana del alba: a levantarse", "Llegaste a la posta", "Llegaste a la siguiente posta")
        TemaId.URSS -> VozEnfoque("DESCANSO REGLAMENTARIO", "Levántate, toma agua, camarada.", "DE VUELTA AL TRABAJO", "¡ARRIBA, CAMARADA!", "¡NORMA CUMPLIDA!", "¡PLAN DEL BLOQUE CUMPLIDO!")
        TemaId.GOTICO -> VozEnfoque("Recreatio", "Levántate, bebe agua.", "Termina la recreatio", "Laudes: hora de despertar", "Deo gratias: terminaste", "Hora cumplida: llegaste a la meta")
        TemaId.ARCANO -> VozEnfoque("Aparta la vista de los legajos", "Levántate, bebe agua, mira por la ventana hacia el faro.", "Vuelve a la mesa del archivo", "Amaneció: la niebla se levanta", "Pesquisa cumplida: guarda los papeles", "El faro dio la vuelta: meta cumplida")
        TemaId.ESPACIO -> VozEnfoque("PAUSA ORBITAL", "Despega del asiento. Hidrátate.", "FIN DE LA PAUSA ORBITAL", "DESPERTAR DE LA TRIPULACIÓN", "MISIÓN COMPLETADA", "OBJETIVO ORBITAL ALCANZADO")
        TemaId.ABISMO -> VozEnfoque("PARADA DE DESCOMPRESIÓN", "Sube despacio: levántate y toma agua.", "FIN DE LA PARADA", "EMERSIÓN: HORA DE DESPERTAR", "INMERSIÓN COMPLETADA", "PROFUNDIDAD OBJETIVO ALCANZADA")
        TemaId.ANDALUS -> VozEnfoque("Pausa en el patio", "Levántate, bebe agua de la fuente.", "Terminó la pausa en el patio", "Al alba: hora de despertar", "¡Obra acabada!", "Llegaste a la fuente: meta cumplida")
        TemaId.MESO -> VozEnfoque("Descanso del pintor", "Deja el pincel, bebe agua del cenote.", "Vuelve al códice", "K'in sale por el oriente: hora de despertar", "Página pintada", "Glifo terminado: llegaste a la meta")
        TemaId.MONGOL -> VozEnfoque("Alto en la estepa", "Levántate, bebe agua.", "A caballo otra vez", "El sol sale sobre la estepa: despierta", "¡Jornada ganada!", "Llegaste al ovoo: meta cumplida")
        TemaId.RENACIMIENTO -> VozEnfoque("Pausa", "Lascia il pennello: levántate, toma agua.", "Fine della pausa", "Sveglia: hora de despertar", "Opera compiuta", "Meta raggiunta")
        TemaId.PIRATA -> VozEnfoque("Se volteó la ampolleta", "Estira las piernas en cubierta y bebe agua dulce.", "Otra vuelta de ampolleta", "Toque de diana: a cubierta", "¡Guardia cumplida!", "¡Tierra a la vista! Llegaste a la meta")
        TemaId.REVOLUCION -> VozEnfoque("Repos", "Levántate, toma agua, ciudadano.", "Fin du repos", "Réveil: hora de despertar", "Ça ira: terminaste", "But atteint!")
        TemaId.ANTIGUAS -> VozEnfoque("Alto en el camino", "Levántate, bebe del odre.", "La compañía sigue", "El alba: hora de despertar", "¡Hazaña cumplida!", "La compañía llegó a la posada")
        TemaId.CUEVA -> VozEnfoque("Descanso junto al fuego", "Levántate, estira las piernas, bebe del arroyo.", "De vuelta al acecho", "Sale el sol sobre el valle: hora de despertar", "¡Presa cobrada!", "El rastro termina aquí: meta cumplida")
        TemaId.BASICO -> VozEnfoque("Descanso", "Levántate y toma agua.", "Se acabó el descanso", "Hora de despertar", "Terminaste", "Llegaste a la meta")
    }

    /**
     * ¿Se puede ver un aviso de ánimo? El permiso (Android 13 o más), las notificaciones de la app encendidas y el canal de
     * ánimo sin silenciar del todo. Antes solo se miraba el permiso, y un aviso con el canal apagado contaba como mostrado
     * (y bajaba la tasa de respuesta).
     */
    fun animoVisible(ctx: Context): Boolean {
        if (!permitido(ctx) || !NotificationManagerCompat.from(ctx).areNotificationsEnabled()) return false
        val nm = ctx.getSystemService(NotificationManager::class.java) ?: return true
        val ch = runCatching { nm.getNotificationChannel(canal(ctx, CANAL_ANIMO)) }.getOrNull() ?: return true
        return ch.importance != NotificationManager.IMPORTANCE_NONE
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
            else -> "Llevas ${porNumero(peor, "1 día", "$peor días")} de atraso: $nombres"
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
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_REPORTES))
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
        val manana = PendingIntent.getBroadcast(ctx, 2022, Intent(ctx, RecordatorioReceiver::class.java).putExtra(Recordatorios.ACCION, Recordatorios.MANANA),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_RECORDATORIOS))
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
        val titulo = when {
            metas.isNotEmpty() && notas.isEmpty() -> if (metas.size == 1) "Revisa tu meta: ${metas[0]}" else "Toca revisar ${porNumero(metas.size, "1 meta", "${metas.size} metas")}"
            metas.isEmpty() -> if (notas.size == 1) "Recordatorio: ${notas[0]}" else "${notas.size} recordatorios para hoy"
            else -> "Metas y recordatorios para hoy"
        }
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_RECORDATORIOS))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(titulo)
            .setContentText((metas + notas).take(4).joinToString(" · "))
            .setContentIntent(abrirApp(ctx, ID_METAS, if (metas.isNotEmpty()) "metas" else "notas"))
            .setAutoCancel(true)
        mostrar(ctx, ID_METAS, b)
    }

    /** El recordatorio diario de suplementos: «Ya los tomé» los anota con su dosis habitual; «En una hora» lo repite. */
    fun suplementos(ctx: Context, nombres: List<String>) {
        fun accion(req: Int, a: String) = PendingIntent.getBroadcast(ctx, req, Intent(ctx, RecordatorioReceiver::class.java).putExtra(Recordatorios.ACCION, a),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_RECORDATORIOS))
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
        val quitar = PendingIntent.getBroadcast(ctx, 2043, Intent(ctx, RecordatorioReceiver::class.java).putExtra(Recordatorios.ACCION, Recordatorios.DESHACER_TOMAS)
            .putExtra(Recordatorios.TOMAS, tomas.map { it.id }.toTypedArray()), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_RECORDATORIOS))
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
        val discreto = io.github.ruddy2003.vestigium.data.Repo.config.discreto
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_LUGARES))
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
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_RESPALDOS))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("Tus respaldos están en pausa")
            .setContentText("Llevan datos privados y no tienen contraseña. Toca para ponerla.")
            .setContentIntent(abrirApp(ctx, 2031, "datos"))
            .setAutoCancel(true)
        mostrar(ctx, 2031, b)
    }

    /** El respaldo automático a la carpeta lleva días sin poder escribirse. */
    fun respaldoFallido(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_RESPALDOS))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("Tu respaldo automático no está saliendo")
            .setContentText("Lleva más de 2 días sin poder guardarse en tu carpeta. Toca para revisarlo.")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Lleva más de 2 días sin poder guardarse en tu carpeta" +
                (io.github.ruddy2003.vestigium.data.Repo.config.respaldo_error?.let { ": $it" } ?: ": puede faltar espacio o que la carpeta ya no deje escribir.") + " Toca para revisarlo o hacer uno a mano."))
            .setContentIntent(abrirApp(ctx, 2035, "datos"))
            .setAutoCancel(true)
        mostrar(ctx, 2035, b)
    }

    /** El respaldo a la carpeta vino mucho más pobre que el anterior y se guardó aparte (9 nov 2026). */
    fun respaldoEmpobrecido(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_RESPALDOS))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("No reemplacé tu respaldo")
            .setContentText("El de hoy trae muchos menos días o momentos que el anterior. Toca para revisarlo.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(io.github.ruddy2003.vestigium.data.Respaldos.EMPOBRECIDO))
            .setContentIntent(abrirApp(ctx, 2036, "datos"))
            .setAutoCancel(true)
        mostrar(ctx, 2036, b)
    }

    /** La revisión semanal encontró la base de datos dañada (9 nov 2026). */
    fun baseDanada(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_RESPALDOS))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("La base de datos tiene un problema")
            .setContentText("Haz un respaldo a mano ahora y no borres tus copias. Toca para ver el detalle.")
            .setContentIntent(abrirApp(ctx, 2037, "datos"))
            .setAutoCancel(true)
        mostrar(ctx, 2037, b)
    }

    /** El respaldo automático de Drive vino mucho más pobre que el último y se guardó aparte. */
    fun driveEmpobrecido(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_RESPALDOS))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("No reemplacé tu respaldo de Drive")
            .setContentText("El nuevo trae muchos menos días o momentos. Toca para revisarlo.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(io.github.ruddy2003.vestigium.data.Drive.EMPOBRECIDO))
            .setContentIntent(abrirApp(ctx, 2032, "datos"))
            .setAutoCancel(true)
        mostrar(ctx, 2032, b)
    }

    fun drive(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_RESPALDOS))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("El respaldo en Drive necesita tu permiso")
            .setContentText("Google pidió confirmar el acceso. Toca para volver a conectar.")
            .setContentIntent(abrirApp(ctx, 2007, "datos"))
            .setAutoCancel(true)
        mostrar(ctx, 2007, b)
    }
}
