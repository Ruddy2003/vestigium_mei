package io.github.ruddy2003.vestigium.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.ruddy2003.vestigium.R
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Clima
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Luna
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.MainActivity
import io.github.ruddy2003.vestigium.ui.QuickActivity
import io.github.ruddy2003.vestigium.ui.Romano

/** Widgets en basalto y oro, como el modo oscuro de la app. */
object Widgets {
    private val BOTONES_ANIMO = intArrayOf(R.id.a1, R.id.a2, R.id.a3, R.id.a4, R.id.a5, R.id.a6, R.id.a7)
    private val BOTONES_MOMENTO = intArrayOf(R.id.m1, R.id.m2, R.id.m3, R.id.m4, R.id.m5, R.id.m6, R.id.m7, R.id.m8)
    // Del pórfido (1) al bronce verde (7), pasando por el marfil.
    private val COLORES_ANIMO = intArrayOf(0xFFE07A86.toInt(), 0xFFD9998C.toInt(), 0xFFD6BC8E.toInt(), 0xFFE7E3DA.toInt(), 0xFFB5D2BE.toInt(), 0xFF8CC8B9.toInt(), 0xFF67BBAE.toInt())

    private fun pi(ctx: Context, req: Int, i: Intent): PendingIntent =
        PendingIntent.getActivity(ctx, req, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    private fun quick(ctx: Context, modo: String) = Intent(ctx, QuickActivity::class.java)
        .putExtra(QuickActivity.MODO, modo).putExtra(QuickActivity.ORIGEN, "widget")
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

    fun vistaAnimo(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_animo)
        val hoy = Repo.momentos.count { it.tipo == "muestreo" && it.fecha == Fechas.diaLogico() }
        val discreto = Repo.config.discreto
        v.setTextViewText(R.id.titulo_animo, if (discreto) "NVNC" else "ANIMVS")
        v.setTextViewText(R.id.pregunta_animo, if (discreto) "// ahora" else "// ¿qué tan agradable se siente ahora?")
        v.setTextViewText(R.id.sub_animo, if (hoy == 0) Romano.fecha(Fechas.diaLogico()) else "$hoy hoy")
        BOTONES_ANIMO.forEachIndexed { i, id ->
            v.setTextColor(id, COLORES_ANIMO[i])
            v.setOnClickPendingIntent(id, pi(ctx, 3100 + i, quick(ctx, QuickActivity.M_ANIMO).putExtra(QuickActivity.VALENCIA, i + 1)))
        }
        return v
    }

    fun vistaMomento(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_momento)
        val discreto = Repo.config.discreto
        val hoy = Repo.momentos.count { it.tipo != "muestreo" && it.fecha == Fechas.diaLogico() }
        v.setTextViewText(R.id.titulo_momento, if (discreto) "SCRIBERE" else "MOMENTA")
        v.setTextViewText(R.id.pregunta_momento, if (discreto) "// registrar" else "// ahora mismo estoy…")
        v.setTextViewText(R.id.sub_momento, if (hoy == 0) "" else "$hoy hoy")
        Catalogo.MOMENTOS_WIDGET.forEachIndexed { i, tipo ->
            val t = Catalogo.tipo(tipo) ?: return@forEachIndexed
            v.setTextViewText(BOTONES_MOMENTO[i], if (discreto) t.discreto else t.nombre)
            v.setOnClickPendingIntent(BOTONES_MOMENTO[i], pi(ctx, 3200 + i, quick(ctx, QuickActivity.M_MOMENTO).putExtra(QuickActivity.TIPO, tipo)))
        }
        v.setOnClickPendingIntent(R.id.m_mas, pi(ctx, 3301, quick(ctx, QuickActivity.M_MENU)))
        v.setOnClickPendingIntent(R.id.m_diario, pi(ctx, 3302, abrir(ctx, "dia")))
        return v
    }

    private fun abrir(ctx: Context, pantalla: String) = Intent(ctx, MainActivity::class.java).putExtra(MainActivity.PANTALLA, pantalla)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

    private val BOTONES_GASTO = intArrayOf(R.id.g1, R.id.g2, R.id.g3, R.id.g4, R.id.g5, R.id.g6)
    private fun dinero(x: Double) = "$" + "%,.0f".format(x)

    fun vistaGasto(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_gasto)
        val hoy = Fechas.hoyCalendario()
        val gHoy = Repo.gastos.filter { it.fecha == hoy }.sumOf { it.monto }
        val gMes = Repo.gastos.filter { it.fecha.startsWith(hoy.take(7)) }.sumOf { it.monto }
        v.setTextViewText(R.id.sub_gasto, if (Repo.config.discreto) "" else "hoy ${dinero(gHoy)} · mes ${dinero(gMes)}")
        // Las categorías que más usas primero; si aún no hay historia, el orden de tus ajustes.
        val usadas = Repo.gastos.filter { it.fecha >= Fechas.sumar(hoy, -90) }.groupingBy { it.categoria }.eachCount().entries.sortedByDescending { it.value }.map { it.key }
        val cats = (usadas + Repo.config.categorias).distinct().take(6)
        BOTONES_GASTO.forEachIndexed { i, id ->
            val c = cats.getOrNull(i)
            if (c == null) v.setViewVisibility(id, android.view.View.INVISIBLE)
            else {
                v.setViewVisibility(id, android.view.View.VISIBLE)
                v.setTextViewText(id, c)
                v.setOnClickPendingIntent(id, pi(ctx, 3500 + i, quick(ctx, QuickActivity.M_GASTO).putExtra(QuickActivity.CATEGORIA, c)))
            }
        }
        v.setOnClickPendingIntent(R.id.g_otro, pi(ctx, 3510, quick(ctx, QuickActivity.M_GASTO)))
        return v
    }

    private val BOTONES_SUENO = intArrayOf(R.id.s1, R.id.s2, R.id.s3, R.id.s4, R.id.s5)
    private val COLORES_SUENO = intArrayOf(0xFFE07A86.toInt(), 0xFFD6BC8E.toInt(), 0xFFE7E3DA.toInt(), 0xFF8CC8B9.toInt(), 0xFF67BBAE.toInt())

    fun vistaSueno(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_sueno)
        val f = Fechas.diaLogico()
        val d = Repo.dias[f]
        val reloj = Repo.salud[f]?.sueno_min
        v.setTextViewText(R.id.sub_sueno, listOfNotNull(reloj?.let { "reloj ${it / 60} h ${"%02d".format(it % 60)}" }, d?.sueno_calidad?.let { "calidad $it" }).joinToString(" · "))
        BOTONES_SUENO.forEachIndexed { i, id ->
            v.setTextColor(id, if (d?.sueno_calidad == i + 1) 0xFF0E1113.toInt() else COLORES_SUENO[i])
            v.setInt(id, "setBackgroundResource", if (d?.sueno_calidad == i + 1) R.drawable.widget_boton_activo else R.drawable.widget_boton)
            v.setOnClickPendingIntent(id, pi(ctx, 3600 + i, quick(ctx, QuickActivity.M_SUENO).putExtra(QuickActivity.CALIDAD, i + 1)))
        }
        v.setOnClickPendingIntent(R.id.s_sueno, pi(ctx, 3610, quick(ctx, QuickActivity.M_SUENO).putExtra(QuickActivity.RECORDADO, true)))
        return v
    }

    private val FILAS_HABITO = intArrayOf(R.id.hb1, R.id.hb2, R.id.hb3, R.id.hb4, R.id.hb5, R.id.hb6)

    fun vistaHabitos(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_habitos)
        val f = Fechas.diaLogico()
        val d = Repo.dias[f]
        val hs = Repo.config.habitos.filter { it.activo }.take(6)
        v.setTextViewText(R.id.sub_habitos, if (hs.isEmpty()) "" else "${hs.count { d?.habitos?.get(it.id) == true }}/${hs.size}")
        v.setTextViewText(R.id.pregunta_habitos, if (hs.isEmpty()) "// agrega hábitos en Más → Hábitos" else "// toca para marcar hoy")
        FILAS_HABITO.forEachIndexed { i, id ->
            val h = hs.getOrNull(i)
            if (h == null) { v.setViewVisibility(id, android.view.View.GONE); return@forEachIndexed }
            val hecho = d?.habitos?.get(h.id) == true
            v.setViewVisibility(id, android.view.View.VISIBLE)
            v.setTextViewText(id, (if (hecho) "▣  " else "□  ") + h.nombre)
            v.setTextColor(id, if (hecho) 0xFFD2AE5A.toInt() else 0xFFE7E3DA.toInt())
            val toque = Intent(ctx, HabitoReceiver::class.java).putExtra("habito", h.id)
            v.setOnClickPendingIntent(id, PendingIntent.getBroadcast(ctx, 3700 + i, toque, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
        }
        if (hs.isEmpty()) v.setOnClickPendingIntent(R.id.raiz_habitos, pi(ctx, 3710, abrir(ctx, "habitos")))
        return v
    }

    /** El mes como cuadrícula de colores, dibujada en un mapa de bits (los widgets no pueden dibujar directamente). */
    fun vistaMes(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_mes)
        val hoy = Fechas.parse(Fechas.diaLogico())
        val ini = hoy.withDayOfMonth(1)
        v.setTextViewText(R.id.titulo_mes, Romano.mes(hoy.monthValue))
        val dias = (0 until ini.lengthOfMonth()).map { ini.plusDays(it.toLong()) }
        val vals = dias.map { d -> val f = Fechas.iso(d); Repo.dias[f]?.valencia?.toDouble() ?: Repo.momentos.filter { it.fecha == f && it.tipo == "muestreo" }.mapNotNull { it.valencia }.takeIf { it.isNotEmpty() }?.average() }
        val conDato = vals.filterNotNull()
        v.setTextViewText(R.id.sub_mes, if (conDato.isEmpty()) Romano.de(hoy.year) else "μ %.1f · ${conDato.size} días".format(conDato.average()))
        val celda = 64; val sep = 6; val cab = 34
        val filas = (ini.dayOfWeek.value - 1 + dias.size + 6) / 7
        val w = 7 * celda + 6 * sep; val h = cab + filas * celda + (filas - 1) * sep
        val bmp = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
        val c = android.graphics.Canvas(bmp)
        val p = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        val t = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { textSize = 22f; typeface = android.graphics.Typeface.MONOSPACE; textAlign = android.graphics.Paint.Align.CENTER }
        Romano.SEMANA.forEachIndexed { i, s -> t.color = 0xFF9AA0A6.toInt(); c.drawText(s.take(2), (i * (celda + sep) + celda / 2).toFloat(), 24f, t) }
        dias.forEachIndexed { k, d ->
            val pos = ini.dayOfWeek.value - 1 + k
            val x = (pos % 7) * (celda + sep); val y = cab + (pos / 7) * (celda + sep)
            val vv = vals[k]
            p.color = if (vv == null) 0xFF1A1F23.toInt() else colorValencia(vv)
            c.drawRoundRect(android.graphics.RectF(x.toFloat(), y.toFloat(), (x + celda).toFloat(), (y + celda).toFloat()), 8f, 8f, p)
            if (d == hoy) { val b = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { style = android.graphics.Paint.Style.STROKE; strokeWidth = 4f; color = 0xFFD2AE5A.toInt() }
                c.drawRoundRect(android.graphics.RectF(x + 2f, y + 2f, x + celda - 2f, y + celda - 2f), 8f, 8f, b) }
            t.color = if (vv == null || d.isAfter(hoy)) 0xFF5B6067.toInt() else 0xFF0E1113.toInt()
            if (vv == null && !d.isAfter(hoy)) t.color = 0xFF9AA0A6.toInt()
            c.drawText("${d.dayOfMonth}", (x + celda / 2).toFloat(), (y + celda / 2 + 8).toFloat(), t)
        }
        v.setImageViewBitmap(R.id.img_mes, bmp)
        v.setOnClickPendingIntent(R.id.raiz_mes, pi(ctx, 3800, abrir(ctx, "calendario")))
        return v
    }

    private fun colorValencia(v: Double): Int {
        val t = ((v - 1) / 6).coerceIn(0.0, 1.0)
        val (a, b, f) = if (t < 0.5) Triple(0xFFC2566A.toInt(), 0xFF9A9A92.toInt(), t * 2) else Triple(0xFF9A9A92.toInt(), 0xFF67BBAE.toInt(), (t - 0.5) * 2)
        fun m(sh: Int) = (((a shr sh) and 0xFF) * (1 - f) + ((b shr sh) and 0xFF) * f).toInt()
        return (0xFF shl 24) or (m(16) shl 16) or (m(8) shl 8) or m(0)
    }

    private fun linea(k: String, v: String) = k.padEnd(9) + "= " + v

    fun vistaHoy(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_hoy)
        val f = Fechas.diaLogico()
        val fase = Luna.fase(f)
        v.setTextViewText(R.id.hoy_fecha, "DIES · " + Romano.fecha(f))
        v.setTextViewText(R.id.hoy_sub, "// " + Fechas.corto(f) + " · " + Luna.glifo(fase) + " " + Luna.nombre(fase))
        val l = ArrayList<String>()
        val d = Repo.dias[f]
        val mu = Repo.momentos.filter { it.fecha == f && it.tipo == "muestreo" }.mapNotNull { it.valencia }
        when {
            d?.valencia != null -> l += linea("animus", "${d.valencia} / 7")
            mu.isNotEmpty() -> l += linea("animus", "%.1f (%d avisos)".format(mu.average(), mu.size))
            else -> l += linea("animus", "—")
        }
        Repo.salud[f]?.sueno_min?.let { l += linea("somnus", "${it / 60} h ${"%02d".format(it % 60)}") }
        Repo.salud[f]?.pasos?.let { l += linea("passus", "%,d".format(it)) }
        val mom = Repo.momentos.count { it.fecha == f && it.tipo != "muestreo" }
        l += linea("momenta", "$mom")
        Repo.pantalla[f]?.let { l += linea("telephon", "${it.total_min / 60} h ${"%02d".format(it.total_min % 60)}") }
        Repo.entorno[f]?.let { e -> if (e.t_max != null) l += linea("caelum", "%.0f°".format(e.t_max) + (e.codigo?.let { " " + Clima.descripcion(it) } ?: "")) }
        if (Repo.config.discreto) l.retainAll { it.startsWith("animus") || it.startsWith("somnus") || it.startsWith("passus") }
        v.setTextViewText(R.id.hoy_lineas, l.joinToString("\n"))
        val abrir = Intent(ctx, MainActivity::class.java).putExtra(MainActivity.PANTALLA, "dia").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        v.setOnClickPendingIntent(R.id.raiz_hoy, pi(ctx, 3400, abrir))
        return v
    }

    fun actualizar(ctx: Context) {
        val m = AppWidgetManager.getInstance(ctx)
        m.getAppWidgetIds(ComponentName(ctx, WidgetAnimo::class.java)).forEach { m.updateAppWidget(it, vistaAnimo(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetMomento::class.java)).forEach { m.updateAppWidget(it, vistaMomento(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetHoy::class.java)).forEach { m.updateAppWidget(it, vistaHoy(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetGasto::class.java)).forEach { m.updateAppWidget(it, vistaGasto(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetSueno::class.java)).forEach { m.updateAppWidget(it, vistaSueno(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetHabitos::class.java)).forEach { m.updateAppWidget(it, vistaHabitos(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetMes::class.java)).forEach { m.updateAppWidget(it, vistaMes(ctx)) }
    }
}

class WidgetAnimo : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Repo.init(context)
        appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, Widgets.vistaAnimo(context)) }
    }
}

class WidgetMomento : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Repo.init(context)
        appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, Widgets.vistaMomento(context)) }
    }
}

class WidgetHoy : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Repo.init(context)
        appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, Widgets.vistaHoy(context)) }
    }
}

class WidgetGasto : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Repo.init(context); appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, Widgets.vistaGasto(context)) }
    }
}

class WidgetSueno : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Repo.init(context); appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, Widgets.vistaSueno(context)) }
    }
}

class WidgetHabitos : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Repo.init(context); appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, Widgets.vistaHabitos(context)) }
    }
}

class WidgetMes : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Repo.init(context); appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, Widgets.vistaMes(context)) }
    }
}

/** Marca o desmarca un hábito de hoy desde el widget, sin abrir la app. */
class HabitoReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra("habito") ?: return
        Repo.init(context)
        val d = Repo.dia(Fechas.diaLogico())
        val hecho = d.habitos[id] == true
        Repo.guardarDia(d.copy(habitos = if (hecho) d.habitos - id else d.habitos + (id to true)))
        // Repo publica el cambio en el hilo principal; el widget se redibuja justo después.
        android.os.Handler(android.os.Looper.getMainLooper()).post { runCatching { Widgets.actualizar(context) } }
    }
}
