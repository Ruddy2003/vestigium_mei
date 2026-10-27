package io.github.ruddy2003.vestigium.widget

import io.github.ruddy2003.vestigium.data.privado
import io.github.ruddy2003.vestigium.data.esAnimo
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
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.TemaId

/** Widgets siempre oscuros, con los colores y rótulos del tema elegido. */
object Widgets {
    private class Colores(val fondo: Int, val boton: Int, val activo: Int, val ambar: Int, val oro: Int, val texto: Int, val tenue: Int,
                          val linea: Int, val animo: IntArray, val vacio: Int, val sobreActivo: Int)

    private fun colores(): Colores = when (Estilo.tema) {
        TemaId.ROMANO -> Colores(R.drawable.widget_fondo, R.drawable.widget_boton, R.drawable.widget_boton_activo, R.drawable.widget_boton_ambar,
            0xFFD2AE5A.toInt(), 0xFFE7E3DA.toInt(), 0xFF9AA0A6.toInt(), 0xFF5A4A24.toInt(),
            intArrayOf(0xFFE07A86.toInt(), 0xFFD9998C.toInt(), 0xFFD6BC8E.toInt(), 0xFFE7E3DA.toInt(), 0xFFB5D2BE.toInt(), 0xFF8CC8B9.toInt(), 0xFF67BBAE.toInt()),
            0xFF1A1F23.toInt(), 0xFF0E1113.toInt())
        TemaId.CIBER -> Colores(R.drawable.widget_fondo_ciber, R.drawable.widget_boton_ciber, R.drawable.widget_boton_activo_ciber, R.drawable.widget_boton_ambar_ciber,
            0xFF00E5FF.toInt(), 0xFFD6F3F7.toInt(), 0xFF7E98AB.toInt(), 0xFF15404A.toInt(),
            intArrayOf(0xFFFF2E88.toInt(), 0xFFE0559A.toInt(), 0xFFB77CA8.toInt(), 0xFF8C96A8.toInt(), 0xFF5FB4C8.toInt(), 0xFF2FCCE0.toInt(), 0xFF00E5FF.toInt()),
            0xFF0B121C.toInt(), 0xFF00161A.toInt())
        TemaId.HELENO -> Colores(R.drawable.widget_fondo_heleno, R.drawable.widget_boton_heleno, R.drawable.widget_boton_activo_heleno, R.drawable.widget_boton_ambar_heleno,
            0xFFD8B45C.toInt(), 0xFFEFE9DC.toInt(), 0xFFA69EBB.toInt(), 0xFF4A3D6E.toInt(),
            intArrayOf(0xFFC25A7A.toInt(), 0xFFCF8597.toInt(), 0xFFDDB3B7.toInt(), 0xFFEFE9DC.toInt(), 0xFFB5D2E8.toInt(), 0xFF8DBDE0.toInt(), 0xFF5FA8D8.toInt()),
            0xFF1D1830.toInt(), 0xFF1C1405.toInt())
        TemaId.MEDIEVAL -> Colores(R.drawable.widget_fondo_medieval, R.drawable.widget_boton_medieval, R.drawable.widget_boton_activo_medieval, R.drawable.widget_boton_ambar_medieval,
            0xFFD5A84A.toInt(), 0xFFEDDFC2.toInt(), 0xFFB7A587.toInt(), 0xFF5A4630.toInt(),
            intArrayOf(0xFFC9473B.toInt(), 0xFFD27A62.toInt(), 0xFFD9AE8E.toInt(), 0xFFEDDFC2.toInt(), 0xFFB9C4E6.toInt(), 0xFF9AB0E8.toInt(), 0xFF8DA5E0.toInt()),
            0xFF2A2016.toInt(), 0xFF1C1405.toInt())
        TemaId.DISTOPIA -> Colores(R.drawable.widget_fondo_distopia, R.drawable.widget_boton_distopia, R.drawable.widget_boton_activo_distopia, R.drawable.widget_boton_ambar_distopia,
            0xFFE0262F.toInt(), 0xFFE8E6E0.toInt(), 0xFFA8A8A1.toInt(), 0xFF4A4D50.toInt(),
            intArrayOf(0xFFE0262F.toInt(), 0xFFD45A5E.toInt(), 0xFFC79A98.toInt(), 0xFFE8E6E0.toInt(), 0xFFC4CACE.toInt(), 0xFFA9B4BB.toInt(), 0xFF8E9CA5.toInt()),
            0xFF222527.toInt(), 0xFFFFFFFF.toInt())
        TemaId.REFUGIO -> Colores(R.drawable.widget_fondo_refugio, R.drawable.widget_boton_refugio, R.drawable.widget_boton_activo_refugio, R.drawable.widget_boton_ambar_refugio,
            0xFF3DFF7F.toInt(), 0xFF9CF5B0.toInt(), 0xFF55B872.toInt(), 0xFF1C3A24.toInt(),
            intArrayOf(0xFFFFB23F.toInt(), 0xFFE0B04A.toInt(), 0xFFB8C060.toInt(), 0xFF9CF5B0.toInt(), 0xFF7CF098.toInt(), 0xFF5CF88A.toInt(), 0xFF3DFF7F.toInt()),
            0xFF112016.toInt(), 0xFF021407.toInt())
        TemaId.ORDEN -> Colores(R.drawable.widget_fondo_orden, R.drawable.widget_boton_orden, R.drawable.widget_boton_activo_orden, R.drawable.widget_boton_ambar_orden,
            0xFFD6B25A.toInt(), 0xFFDDE2E7.toInt(), 0xFF9DA6B0.toInt(), 0xFF4A3F22.toInt(),
            intArrayOf(0xFFD0333B.toInt(), 0xFFD06A6E.toInt(), 0xFFC9A5A7.toInt(), 0xFFDDE2E7.toInt(), 0xFFB4CBD8.toInt(), 0xFF9FBFD0.toInt(), 0xFF8FB3C8.toInt()),
            0xFF22262B.toInt(), 0xFF1C1405.toInt())
        TemaId.LABORATORIO -> Colores(R.drawable.widget_fondo_laboratorio, R.drawable.widget_boton_laboratorio, R.drawable.widget_boton_activo_laboratorio, R.drawable.widget_boton_ambar_laboratorio,
            0xFF3CD2E5.toInt(), 0xFFE3ECF3.toInt(), 0xFF97A8B9.toInt(), 0xFF263447.toInt(),
            intArrayOf(0xFFFF7A9A.toInt(), 0xFFF29DB3.toInt(), 0xFFE9C6D2.toInt(), 0xFFE3ECF3.toInt(), 0xFFAEE6EE.toInt(), 0xFF78DCEA.toInt(), 0xFF3CD2E5.toInt()),
            0xFF18222F.toInt(), 0xFF00191D.toInt())
        TemaId.COLONOS -> Colores(R.drawable.widget_fondo_colonos, R.drawable.widget_boton_colonos, R.drawable.widget_boton_activo_colonos, R.drawable.widget_boton_ambar_colonos,
            0xFFE3C27A.toInt(), 0xFFEEE7D7.toInt(), 0xFFA9B0BC.toInt(), 0xFF3E4B60.toInt(),
            intArrayOf(0xFFE07A62.toInt(), 0xFFE29A86.toInt(), 0xFFE8C2B0.toInt(), 0xFFEEE7D7.toInt(), 0xFFC0D2EA.toInt(), 0xFFA6C2E6.toInt(), 0xFF8FB3E0.toInt()),
            0xFF1E2838.toInt(), 0xFF1C1405.toInt())
        TemaId.NAPOLEON -> Colores(R.drawable.widget_fondo_napoleon, R.drawable.widget_boton_napoleon, R.drawable.widget_boton_activo_napoleon, R.drawable.widget_boton_ambar_napoleon,
            0xFFD9B25B.toInt(), 0xFFF1E9D2.toInt(), 0xFFB3AE9C.toInt(), 0xFF2C3970.toInt(),
            intArrayOf(0xFFE05A6A.toInt(), 0xFFD98590.toInt(), 0xFFD9B8B0.toInt(), 0xFFF1E9D2.toInt(), 0xFFB9C6EE.toInt(), 0xFF9DB4FF.toInt(), 0xFF7F9BFF.toInt()),
            0xFF121A3D.toInt(), 0xFF1A1405.toInt())
        TemaId.BOTANICO -> Colores(R.drawable.widget_fondo_botanico, R.drawable.widget_boton_botanico, R.drawable.widget_boton_activo_botanico, R.drawable.widget_boton_ambar_botanico,
            0xFFB8E06A.toInt(), 0xFFEAF0E2.toInt(), 0xFFA9B8A4.toInt(), 0xFF2D442F.toInt(),
            intArrayOf(0xFFF29BB8.toInt(), 0xFFE7A9BE.toInt(), 0xFFD9C3C0.toInt(), 0xFFEAF0E2.toInt(), 0xFFC8E3A8.toInt(), 0xFFA9DC80.toInt(), 0xFF8FD06A.toInt()),
            0xFF121D15.toInt(), 0xFF121A04.toInt())
        TemaId.NAVIDAD -> Colores(R.drawable.widget_fondo_navidad, R.drawable.widget_boton_navidad, R.drawable.widget_boton_activo_navidad, R.drawable.widget_boton_ambar_navidad,
            0xFFF2C94C.toInt(), 0xFFF3EFE6.toInt(), 0xFFB5C2B8.toInt(), 0xFF2E4A39.toInt(),
            intArrayOf(0xFFE5484D.toInt(), 0xFFE77B7A.toInt(), 0xFFE9B2A8.toInt(), 0xFFF3EFE6.toInt(), 0xFFB5DDB7.toInt(), 0xFF8CCF8F.toInt(), 0xFF66BB6A.toInt()),
            0xFF14241B.toInt(), 0xFF1A1405.toInt())
        TemaId.SEDA -> Colores(R.drawable.widget_fondo_seda, R.drawable.widget_boton_seda, R.drawable.widget_boton_activo_seda, R.drawable.widget_boton_ambar_seda,
            0xFFE0B04F.toInt(), 0xFFF2EAD3.toInt(), 0xFFB9B29F.toInt(), 0xFF33406A.toInt(),
            intArrayOf(0xFFE26A50.toInt(), 0xFFE5907B.toInt(), 0xFFE8BFAA.toInt(), 0xFFF2EAD3.toInt(), 0xFFA8DEDE.toInt(), 0xFF7CD0D0.toInt(), 0xFF4FC3C3.toInt()),
            0xFF161D35.toInt(), 0xFF1A1405.toInt())
        TemaId.URSS -> Colores(R.drawable.widget_fondo_urss, R.drawable.widget_boton_urss, R.drawable.widget_boton_activo_urss, R.drawable.widget_boton_ambar_urss,
            0xFFE8B923.toInt(), 0xFFF1E7D2.toInt(), 0xFFB7AA95.toInt(), 0xFF3D3333.toInt(),
            intArrayOf(0xFF6E7C8C.toInt(), 0xFF98A1AA.toInt(), 0xFFC4C3BC.toInt(), 0xFFF1E7D2.toInt(), 0xFFEBC9A6.toInt(), 0xFFE8806A.toInt(), 0xFFE5252F.toInt()),
            0xFF1B1717.toInt(), 0xFFFFFFFF.toInt())
        TemaId.GOTICO -> Colores(R.drawable.widget_fondo_gotico, R.drawable.widget_boton_gotico, R.drawable.widget_boton_activo_gotico, R.drawable.widget_boton_ambar_gotico,
            0xFFE2B34A.toInt(), 0xFFF0E9D8.toInt(), 0xFFB0AFC8.toInt(), 0xFF2E3A7A.toInt(),
            intArrayOf(0xFFE0405E.toInt(), 0xFFD9708A.toInt(), 0xFFD2A6B4.toInt(), 0xFFF0E9D8.toInt(), 0xFFB8C8F2.toInt(), 0xFF8DA8FF.toInt(), 0xFF5C8DFF.toInt()),
            0xFF111842.toInt(), 0xFF1A1405.toInt())
        TemaId.ESPACIO -> Colores(R.drawable.widget_fondo_espacio, R.drawable.widget_boton_espacio, R.drawable.widget_boton_activo_espacio, R.drawable.widget_boton_ambar_espacio,
            0xFFFFB86B.toInt(), 0xFFE8ECF8.toInt(), 0xFF8E9AB8.toInt(), 0xFF26324F.toInt(),
            intArrayOf(0xFFFF8A6B.toInt(), 0xFFF5A98A.toInt(), 0xFFE0C6B8.toInt(), 0xFFE8ECF8.toInt(), 0xFFB9CCF5.toInt(), 0xFFA0C0FF.toInt(), 0xFF8AB4FF.toInt()),
            0xFF0A0F1E.toInt(), 0xFF020818.toInt())
        TemaId.ANDALUS -> Colores(R.drawable.widget_fondo_andalus, R.drawable.widget_boton_andalus, R.drawable.widget_boton_activo_andalus, R.drawable.widget_boton_ambar_andalus,
            0xFFE0B65A.toInt(), 0xFFF3EAD6.toInt(), 0xFFB4AE9B.toInt(), 0xFF2B4A45.toInt(),
            intArrayOf(0xFFE07A5F.toInt(), 0xFFE39A82.toInt(), 0xFFE5C2AE.toInt(), 0xFFF3EAD6.toInt(), 0xFFA9DCCD.toInt(), 0xFF7CCFB8.toInt(), 0xFF4FC1A6.toInt()),
            0xFF112220.toInt(), 0xFF1A1405.toInt())
        TemaId.MONGOL -> Colores(R.drawable.widget_fondo_mongol, R.drawable.widget_boton_mongol, R.drawable.widget_boton_activo_mongol, R.drawable.widget_boton_ambar_mongol,
            0xFFF2C14E.toInt(), 0xFFF0EBDD.toInt(), 0xFFAAB0BD.toInt(), 0xFF2B3E5E.toInt(),
            intArrayOf(0xFFFF7A59.toInt(), 0xFFF09A80.toInt(), 0xFFE2C3B4.toInt(), 0xFFF0EBDD.toInt(), 0xFFB8D6F5.toInt(), 0xFF93C3FA.toInt(), 0xFF6FB1FF.toInt()),
            0xFF101B2E.toInt(), 0xFF041427.toInt())
        TemaId.RENACIMIENTO -> Colores(R.drawable.widget_fondo_renacimiento, R.drawable.widget_boton_renacimiento, R.drawable.widget_boton_activo_renacimiento, R.drawable.widget_boton_ambar_renacimiento,
            0xFFD9B566.toInt(), 0xFFEFE0BE.toInt(), 0xFFBCA884.toInt(), 0xFF4A3826.toInt(),
            intArrayOf(0xFF6E8CA8.toInt(), 0xFF8DA2B5.toInt(), 0xFFB8B3A6.toInt(), 0xFFEFE0BE.toInt(), 0xFFE8C79A.toInt(), 0xFFE4A877.toInt(), 0xFFE08A5E.toInt()),
            0xFF241A11.toInt(), 0xFF2A1006.toInt())
        TemaId.REVOLUCION -> Colores(R.drawable.widget_fondo_revolucion, R.drawable.widget_boton_revolucion, R.drawable.widget_boton_activo_revolucion, R.drawable.widget_boton_ambar_revolucion,
            0xFF8FAEE8.toInt(), 0xFFF0EADB.toInt(), 0xFFB2AC9E.toInt(), 0xFF3A3F4A.toInt(),
            intArrayOf(0xFFE5525E.toInt(), 0xFFE28A90.toInt(), 0xFFE3BCB8.toInt(), 0xFFF0EADB.toInt(), 0xFFC2D0EC.toInt(), 0xFFA8BFEA.toInt(), 0xFF8FAEE8.toInt()),
            0xFF1B1D22.toInt(), 0xFFFFFFFF.toInt())
        TemaId.ANTIGUAS -> Colores(R.drawable.widget_fondo_antiguas, R.drawable.widget_boton_antiguas, R.drawable.widget_boton_activo_antiguas, R.drawable.widget_boton_ambar_antiguas,
            0xFFD9BC6A.toInt(), 0xFFEDE7D8.toInt(), 0xFFB0AA9C.toInt(), 0xFF45464C.toInt(),
            intArrayOf(0xFFE06A72.toInt(), 0xFFE39A96.toInt(), 0xFFE3C2B6.toInt(), 0xFFEDE7D8.toInt(), 0xFFBCDDC3.toInt(), 0xFFA4D2AE.toInt(), 0xFF8CC79A.toInt()),
            0xFF1F2023.toInt(), 0xFF0D1F14.toInt())
    }

    /** Pinta fondo, botones, líneas y textos según el tema. Va primero: lo que se pinta después (estado activo, colores de ánimo) lo sobrescribe. */
    private fun pintar(v: RemoteViews, p: Partes) {
        val c = colores()
        p.raiz.forEach { v.setInt(it, "setBackgroundResource", c.fondo) }
        p.boton.forEach { v.setInt(it, "setBackgroundResource", c.boton) }
        p.ambar.forEach { v.setInt(it, "setBackgroundResource", c.ambar) }
        p.linea.forEach { v.setInt(it, "setBackgroundColor", c.linea) }
        p.texto.forEach { v.setTextColor(it, c.texto) }
        p.tenue.forEach { v.setTextColor(it, c.tenue) }
        p.oro.forEach { v.setTextColor(it, c.oro) }
    }
    private val com get() = Estilo.comentario

    private val BOTONES_ANIMO = intArrayOf(R.id.a1, R.id.a2, R.id.a3, R.id.a4, R.id.a5, R.id.a6, R.id.a7)
    private val BOTONES_MOMENTO = intArrayOf(R.id.m1, R.id.m2, R.id.m3, R.id.m4, R.id.m5, R.id.m6, R.id.m7, R.id.m8)

    /** Puede ser nulo fuera de un teléfono (en las capturas de diseño). */
    private fun clic(v: RemoteViews, id: Int, p: PendingIntent?) { if (p != null) v.setOnClickPendingIntent(id, p) }

    private fun pi(ctx: Context, req: Int, i: Intent): PendingIntent? =
        PendingIntent.getActivity(ctx, req, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    private fun quick(ctx: Context, modo: String) = Intent(ctx, QuickActivity::class.java)
        .putExtra(QuickActivity.MODO, modo).putExtra(QuickActivity.ORIGEN, "widget")
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

    fun vistaAnimo(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_animo)
        pintar(v, Piel.ANIMO)
        val hoy = Repo.momentos.count { it.tipo == "muestreo" && it.fecha == Fechas.diaLogico() }
        val discreto = Repo.config.discreto
        v.setTextViewText(R.id.titulo_animo, if (discreto) "NVNC" else Estilo.widget("animo"))
        v.setTextViewText(R.id.pregunta_animo, com + if (discreto) "ahora" else "¿qué tan agradable se siente ahora?")
        v.setTextViewText(R.id.sub_animo, if (hoy == 0) Estilo.fecha(Fechas.diaLogico()) else "$hoy hoy")
        BOTONES_ANIMO.forEachIndexed { i, id ->
            v.setTextColor(id, colores().animo[i])
            clic(v, id, pi(ctx, 3100 + i, quick(ctx, QuickActivity.M_ANIMO).putExtra(QuickActivity.VALENCIA, i + 1)))
        }
        return v
    }

    fun vistaMomento(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_momento)
        pintar(v, Piel.MOMENTO)
        val discreto = Repo.config.discreto
        val hoy = Repo.momentos.count { Catalogo.esRegistro(it) && it.fecha == Fechas.diaLogico() }
        v.setTextViewText(R.id.titulo_momento, if (discreto) "SCRIBERE" else Estilo.widget("momento"))
        v.setTextViewText(R.id.pregunta_momento, com + if (discreto) "registrar" else "ahora siento…")
        v.setTextViewText(R.id.sub_momento, if (hoy == 0) "" else "$hoy hoy")
        // Ocho sentimientos: un toque y queda registrado (luego se puede agregar en qué pensabas y cómo te sientes).
        Catalogo.SENTIMIENTOS_WIDGET.take(BOTONES_MOMENTO.size).forEachIndexed { i, id ->
            v.setTextViewText(BOTONES_MOMENTO[i], Catalogo.nombreSentimiento(id, discreto))
            clic(v, BOTONES_MOMENTO[i], pi(ctx, 3200 + i, quick(ctx, QuickActivity.M_MOMENTO).putExtra(QuickActivity.TIPO, id)))
        }
        clic(v, R.id.m_mas, pi(ctx, 3301, quick(ctx, QuickActivity.M_MENU)))
        clic(v, R.id.m_diario, pi(ctx, 3302, abrir(ctx, "dia")))
        return v
    }

    private fun abrir(ctx: Context, pantalla: String) = Intent(ctx, MainActivity::class.java).putExtra(MainActivity.PANTALLA, pantalla)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

    private val BOTONES_GASTO = intArrayOf(R.id.g1, R.id.g2, R.id.g3, R.id.g4, R.id.g5, R.id.g6)
    private fun dinero(x: Double) = "$" + "%,.0f".format(x)

    fun vistaGasto(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_gasto)
        pintar(v, Piel.GASTO)
        v.setTextViewText(R.id.titulo_gasto, Estilo.widget("gasto"))
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
                clic(v, id, pi(ctx, 3500 + i, quick(ctx, QuickActivity.M_GASTO).putExtra(QuickActivity.CATEGORIA, c)))
            }
        }
        clic(v, R.id.g_otro, pi(ctx, 3510, quick(ctx, QuickActivity.M_GASTO)))
        return v
    }

    private val BOTONES_SUENO = intArrayOf(R.id.s1, R.id.s2, R.id.s3, R.id.s4, R.id.s5)

    fun vistaSueno(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_sueno)
        pintar(v, Piel.SUENO)
        v.setTextViewText(R.id.titulo_sueno, Estilo.widget("sueno"))
        val col = colores()
        val tonos = intArrayOf(col.animo[0], col.animo[2], col.animo[3], col.animo[5], col.animo[6])
        // El día del despertar, igual que al guardar la calidad (H-NAV-7).
        val f = Fechas.hoyCalendario()
        val d = Repo.dias[f]
        val reloj = Repo.salud[f]?.sueno_min
        v.setTextViewText(R.id.sub_sueno, listOfNotNull(reloj?.let { "reloj ${it / 60} h ${"%02d".format(it % 60)}" }, d?.sueno_calidad?.let { "calidad $it" }).joinToString(" · "))
        BOTONES_SUENO.forEachIndexed { i, id ->
            v.setTextColor(id, if (d?.sueno_calidad == i + 1) col.sobreActivo else tonos[i])
            v.setInt(id, "setBackgroundResource", if (d?.sueno_calidad == i + 1) col.activo else col.boton)
            clic(v, id, pi(ctx, 3600 + i, quick(ctx, QuickActivity.M_SUENO).putExtra(QuickActivity.CALIDAD, i + 1)))
        }
        clic(v, R.id.s_sueno, pi(ctx, 3610, quick(ctx, QuickActivity.M_SUENO).putExtra(QuickActivity.RECORDADO, true)))
        return v
    }

    private val FILAS_HABITO = intArrayOf(R.id.hb1, R.id.hb2, R.id.hb3, R.id.hb4, R.id.hb5, R.id.hb6)

    fun vistaHabitos(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_habitos)
        pintar(v, Piel.HABITOS)
        v.setTextViewText(R.id.titulo_habitos, Estilo.widget("habitos"))
        val f = Fechas.diaLogico()
        val d = Repo.dias[f]
        val hs = io.github.ruddy2003.vestigium.data.Privado.habitosVisibles().filter { it.activo }.take(6)
        v.setTextViewText(R.id.sub_habitos, if (hs.isEmpty()) "" else "${hs.count { d?.habitos?.get(it.id) == true }}/${hs.size}")
        v.setTextViewText(R.id.pregunta_habitos, com + if (hs.isEmpty()) "agrega hábitos en Más → Hábitos" else "toca para marcar hoy")
        FILAS_HABITO.forEachIndexed { i, id ->
            val h = hs.getOrNull(i)
            if (h == null) { v.setViewVisibility(id, android.view.View.GONE); return@forEachIndexed }
            val hecho = d?.habitos?.get(h.id) == true
            v.setViewVisibility(id, android.view.View.VISIBLE)
            v.setTextViewText(id, (if (hecho) "▣  " else "□  ") + h.nombre)
            v.setTextColor(id, if (hecho) colores().oro else colores().texto)
            val toque = Intent(ctx, HabitoReceiver::class.java).putExtra("habito", h.id)
            clic(v, id, PendingIntent.getBroadcast(ctx, 3700 + i, toque, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
        }
        if (hs.isEmpty()) clic(v, R.id.raiz_habitos, pi(ctx, 3710, abrir(ctx, "habitos")))
        return v
    }

    /** El mes como cuadrícula de colores, dibujada en un mapa de bits (los widgets no pueden dibujar directamente). */
    fun vistaMes(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_mes)
        pintar(v, Piel.MES)
        val col = colores()
        val hoy = Fechas.parse(Fechas.diaLogico())
        val ini = hoy.withDayOfMonth(1)
        v.setTextViewText(R.id.titulo_mes, Estilo.mes(hoy.monthValue, hoy.year))
        val dias = (0 until ini.lengthOfMonth()).map { ini.plusDays(it.toLong()) }
        val vals = dias.map { d -> val f = Fechas.iso(d); Repo.dias[f]?.valencia?.toDouble() ?: Repo.momentos.filter { it.fecha == f && it.esAnimo }.mapNotNull { it.valencia }.takeIf { it.isNotEmpty() }?.average() }
        val conDato = vals.filterNotNull()
        v.setTextViewText(R.id.sub_mes, if (conDato.isEmpty()) Estilo.numeral(hoy.year) else "μ %.1f · ${conDato.size} días".format(conDato.average()))
        val celda = 64; val sep = 6; val cab = 34
        val filas = (ini.dayOfWeek.value - 1 + dias.size + 6) / 7
        val w = 7 * celda + 6 * sep; val h = cab + filas * celda + (filas - 1) * sep
        val bmp = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
        val c = android.graphics.Canvas(bmp)
        val p = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        val t = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { textSize = 22f; typeface = android.graphics.Typeface.MONOSPACE; textAlign = android.graphics.Paint.Align.CENTER }
        Estilo.semanaCorta.forEachIndexed { i, s -> t.color = col.tenue; c.drawText(s, (i * (celda + sep) + celda / 2).toFloat(), 24f, t) }
        dias.forEachIndexed { k, d ->
            val pos = ini.dayOfWeek.value - 1 + k
            val x = (pos % 7) * (celda + sep); val y = cab + (pos / 7) * (celda + sep)
            val vv = vals[k]
            p.color = if (vv == null) col.vacio else colorValencia(vv, col.animo)
            c.drawRoundRect(android.graphics.RectF(x.toFloat(), y.toFloat(), (x + celda).toFloat(), (y + celda).toFloat()), 8f, 8f, p)
            if (d == hoy) { val b = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { style = android.graphics.Paint.Style.STROKE; strokeWidth = 4f; color = col.oro }
                c.drawRoundRect(android.graphics.RectF(x + 2f, y + 2f, x + celda - 2f, y + celda - 2f), 8f, 8f, b) }
            t.color = if (vv == null || d.isAfter(hoy)) 0xFF5B6067.toInt() else col.sobreActivo
            if (vv == null && !d.isAfter(hoy)) t.color = col.tenue
            c.drawText("${d.dayOfMonth}", (x + celda / 2).toFloat(), (y + celda / 2 + 8).toFloat(), t)
        }
        v.setImageViewBitmap(R.id.img_mes, bmp)
        clic(v, R.id.raiz_mes, pi(ctx, 3800, abrir(ctx, "calendario")))
        return v
    }

    /** Interpola entre los siete tonos de ánimo del tema. */
    private fun colorValencia(v: Double, tonos: IntArray): Int {
        val x = ((v - 1) / 6).coerceIn(0.0, 1.0) * 6
        val i = x.toInt().coerceAtMost(5)
        val (a, b, f) = Triple(tonos[i], tonos[i + 1], x - i)
        fun m(sh: Int) = (((a shr sh) and 0xFF) * (1 - f) + ((b shr sh) and 0xFF) * f).toInt()
        return (0xFF shl 24) or (m(16) shl 16) or (m(8) shl 8) or m(0)
    }

    private fun linea(k: String, v: String) = Estilo.clave(k).padEnd(9) + "= " + v

    fun vistaHoy(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_hoy)
        pintar(v, Piel.HOY)
        val f = Fechas.diaLogico()
        val fase = Luna.fase(f)
        v.setTextViewText(R.id.hoy_fecha, Estilo.widget("hoy") + " · " + Estilo.fecha(f))
        v.setTextViewText(R.id.hoy_sub, com + Fechas.corto(f) + " · " + Luna.glifo(fase) + " " + Luna.nombre(fase))
        val l = ArrayList<String>()
        val d = Repo.dias[f]
        val mu = Repo.momentos.filter { it.fecha == f && it.esAnimo }.mapNotNull { it.valencia }
        when {
            d?.valencia != null -> l += linea("animus", "${d.valencia} / 7")
            mu.isNotEmpty() -> l += linea("animus", "%.1f (%d avisos)".format(mu.average(), mu.size))
            else -> l += linea("animus", "—")
        }
        Repo.salud[f]?.sueno_min?.let { l += linea("somnus", "${it / 60} h ${"%02d".format(it % 60)}") }
        Repo.salud[f]?.pasos?.let { l += linea("passus", "%,d".format(it)) }
        val mom = Repo.momentos.count { it.fecha == f && it.tipo != "muestreo" && !it.privado }
        l += linea("momenta", "$mom")
        Repo.pantalla[f]?.let { l += linea("telephon", "${it.total_min / 60} h ${"%02d".format(it.total_min % 60)}") }
        Repo.entorno[f]?.let { e -> if (e.t_max != null) l += linea("caelum", "%.0f°".format(e.t_max) + (e.codigo?.let { " " + Clima.descripcion(it) } ?: "")) }
        if (Repo.config.discreto) l.retainAll { k -> listOf("animus", "somnus", "passus").any { k.startsWith(Estilo.clave(it)) } }
        v.setTextViewText(R.id.hoy_lineas, l.joinToString("\n"))
        val abrir = Intent(ctx, MainActivity::class.java).putExtra(MainActivity.PANTALLA, "dia").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        clic(v, R.id.raiz_hoy, pi(ctx, 3400, abrir))
        return v
    }

    // ---------- Hoy en anillos ----------
    fun vistaAnillos(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_anillos)
        pintar(v, PielNuevos.ANILLOS)
        val col = colores()
        val f = Fechas.diaLogico()
        v.setTextViewText(R.id.titulo_anillos, Estilo.widget("hoy"))
        val d = Repo.dias[f]
        val diario = listOf(d?.valencia != null, d?.nota?.isNotBlank() == true, d?.prediccion_manana != null).count { it } / 3.0
        val sueno = listOf(d?.sueno_calidad != null, Repo.salud[f]?.sueno_min != null).count { it } / 2.0
        val plan = Repo.config.plan_muestreo[Fechas.hoyCalendario()].orEmpty().size
        val hechos = Repo.momentos.count { it.fecha == f && it.tipo == "muestreo" }
        val avisos = if (plan == 0) (if (hechos > 0) 1.0 else 0.0) else (hechos.toDouble() / plan).coerceAtMost(1.0)
        val pasos = (Repo.salud[f]?.pasos ?: 0L).toDouble()
        val anillos = listOf("diario" to diario, "sueño" to sueno, "avisos" to avisos, "pasos" to (pasos / 8000).coerceAtMost(1.0))
        val total = anillos.sumOf { it.second } / anillos.size
        v.setTextViewText(R.id.sub_anillos, "%.0f%%".format(total * 100))
        val lado = 300; val bmp = android.graphics.Bitmap.createBitmap(lado * 2, lado * 2, android.graphics.Bitmap.Config.ARGB_8888)
        val c = android.graphics.Canvas(bmp)
        val pista = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { style = android.graphics.Paint.Style.STROKE; strokeWidth = 26f; color = col.vacio }
        val arco = android.graphics.Paint(pista).apply { color = col.oro; strokeCap = android.graphics.Paint.Cap.ROUND }
        val num = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { color = col.texto; textSize = 52f; typeface = android.graphics.Typeface.MONOSPACE; textAlign = android.graphics.Paint.Align.CENTER }
        val etq = android.graphics.Paint(num).apply { color = col.tenue; textSize = 30f }
        anillos.forEachIndexed { i, (nombre, p) ->
            val cx = (i % 2) * lado + lado / 2f; val cy = (i / 2) * lado + lado / 2f - 14f
            val r = android.graphics.RectF(cx - 92f, cy - 92f, cx + 92f, cy + 92f)
            c.drawArc(r, 0f, 360f, false, pista)
            if (p > 0) c.drawArc(r, -90f, (360 * p).toFloat(), false, arco)
            c.drawText(if (p >= 1.0) "✓" else "%.0f".format(p * 100), cx, cy + 18f, num)
            c.drawText(nombre, cx, cy + 140f, etq)
        }
        v.setImageViewBitmap(R.id.img_anillos, bmp)
        clic(v, R.id.raiz_anillos, pi(ctx, 3900, abrir(ctx, "dia")))
        return v
    }

    // ---------- Foto del día ----------
    fun vistaFoto(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_foto)
        pintar(v, PielNuevos.FOTO)
        v.setTextViewText(R.id.titulo_foto, Estilo.widget("foto"))
        val hoy = Fechas.diaLogico()
        val haceUnAnio = Fechas.iso(Fechas.parse(hoy).minusYears(1))
        // Hoy si ya elegiste; si no, la de hace un año; si tampoco, la más reciente que hayas elegido.
        val (fecha, rotulo) = when {
            Repo.dias[hoy]?.foto != null -> hoy to "hoy"
            Repo.dias[haceUnAnio]?.foto != null -> haceUnAnio to "hace un año"
            else -> (Repo.dias.values.filter { it.foto != null }.maxByOrNull { it.fecha }?.fecha ?: "") to "la más reciente"
        }
        val uri = Repo.dias[fecha]?.foto
        val bmp = uri?.let { runCatching { io.github.ruddy2003.vestigium.sensores.Galeria.miniatura(ctx, android.net.Uri.parse(it), 640) }.getOrNull() }
        if (bmp != null) {
            v.setImageViewBitmap(R.id.img_foto, bmp)
            v.setTextViewText(R.id.sub_foto, rotulo)
            v.setTextViewText(R.id.pie_foto, Fechas.titulo(fecha).lowercase() + if (fecha != hoy) " · ${fecha.take(4)}" else "")
            clic(v, R.id.raiz_foto, pi(ctx, 3910, abrir(ctx, "dia:$fecha")))
        } else {
            v.setImageViewBitmap(R.id.img_foto, android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888))
            v.setTextViewText(R.id.sub_foto, "")
            v.setTextViewText(R.id.pie_foto, "En Día, mantén presionada una foto para elegirla como la del día.")
            clic(v, R.id.raiz_foto, pi(ctx, 3910, abrir(ctx, "dia")))
        }
        return v
    }

    // ---------- Una cifra ----------
    val CIFRAS = listOf("racha", "dias", "pasos", "km", "gasto", "canciones", "pantalla")

    fun cifra(id: String): Pair<String, String> {
        val hoy = Fechas.diaLogico()
        return when (id) {
            "racha" -> "${io.github.ruddy2003.vestigium.ui.pantallas.racha(hoy)}" to "días seguidos con diario"
            "dias" -> "${Repo.dias.values.count { it.valencia != null || it.nota.isNotBlank() }}" to "días con diario en total"
            "pasos" -> "%,d".format(Repo.salud[hoy]?.pasos ?: 0L) to "pasos hoy"
            "km" -> "%.1f".format(runCatching { io.github.ruddy2003.vestigium.ciencia.Movilidad.kmEntre(Fechas.lunesDe(hoy), hoy).values.sum() }.getOrDefault(0.0)) to "km esta semana"
            "gasto" -> "$%,.0f".format(Repo.gastos.filter { it.dia.startsWith(hoy.take(7)) }.sumOf { it.monto }) to "gastado este mes"
            "canciones" -> "${runCatching { Repo.escuchas(hoy, hoy).size }.getOrDefault(0)}" to "canciones hoy"
            else -> (Repo.pantalla[hoy]?.total_min?.let { "%d:%02d".format(it / 60, it % 60) } ?: "—") to "horas de pantalla hoy"
        }
    }

    fun vistaCifra(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_cifra)
        pintar(v, PielNuevos.CIFRA)
        v.setTextViewText(R.id.titulo_cifra, Estilo.widget("cifra"))
        val id = Repo.config.widget_cifra.takeIf { it in CIFRAS } ?: "racha"
        val (valor, etiqueta) = cifra(id)
        v.setTextViewText(R.id.valor_cifra, valor)
        v.setTextViewText(R.id.etiqueta_cifra, etiqueta)
        v.setTextViewText(R.id.sub_cifra, "${CIFRAS.indexOf(id) + 1}/${CIFRAS.size}")
        // Tocar el widget pasa a la siguiente cifra.
        clic(v, R.id.raiz_cifra, PendingIntent.getBroadcast(ctx, 3920, Intent(ctx, CifraReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
        return v
    }

    /** El cronómetro de enfoque: corre mientras hay un bloque en marcha; tocarlo abre el registro rápido para empezar o terminar. */
    fun vistaEnfoque(ctx: Context): RemoteViews {
        val v = RemoteViews(ctx.packageName, R.layout.widget_enfoque)
        pintar(v, PielNuevos.ENFOQUE)
        v.setTextViewText(R.id.titulo_enfoque, Estilo.widget("enfoque"))
        val c = io.github.ruddy2003.vestigium.data.Enfoque.enCurso()
        val hoy = io.github.ruddy2003.vestigium.data.Enfoque.delDia(Fechas.diaLogico()).filterKeys { it != "siesta" }.values.sum()
        v.setTextViewText(R.id.sub_enfoque, if (hoy > 0) "hoy ${hoy / 60}:${"%02d".format(hoy % 60)}" else "")
        val p = io.github.ruddy2003.vestigium.data.Enfoque.plan()
        if (c != null && p != null) {
            val E = io.github.ruddy2003.vestigium.data.Enfoque
            val ahora = System.currentTimeMillis()
            val rest = E.restante(p, ahora)
            val corre = p.seg_inicio != null
            // Con meta, cuenta hacia atrás lo que queda del bloque o del descanso; libre, hacia arriba. En pausa se detiene.
            if (rest != null) { v.setChronometer(R.id.crono_enfoque, android.os.SystemClock.elapsedRealtime() + rest, null, corre); v.setChronometerCountDown(R.id.crono_enfoque, true) }
            else { v.setChronometer(R.id.crono_enfoque, android.os.SystemClock.elapsedRealtime() - E.transcurrido(p, ahora), null, corre); v.setChronometerCountDown(R.id.crono_enfoque, false) }
            v.setTextViewText(R.id.estado_enfoque, when {
                p.esperando -> "¿empezamos el bloque ${E.numeroBloque(p)}?"
                E.enPausa(p) -> "en pausa · toca para ver"
                E.enDescanso(p) -> "descanso · toca para ver"
                else -> io.github.ruddy2003.vestigium.data.Catalogo.nombreTramo(c.first).lowercase() + (if (E.bloques(p) > 1) " · bloque ${E.numeroBloque(p)} de ${E.bloques(p)}" else "") + " · toca para ver"
            })
        } else {
            v.setChronometer(R.id.crono_enfoque, android.os.SystemClock.elapsedRealtime(), null, false)
            v.setChronometerCountDown(R.id.crono_enfoque, false)
            v.setTextViewText(R.id.estado_enfoque, "toca para empezar")
        }
        clic(v, R.id.raiz_enfoque, pi(ctx, 3930, quick(ctx, QuickActivity.M_ENFOQUE)))
        return v
    }

    fun actualizar(ctx: Context) {
        val m = AppWidgetManager.getInstance(ctx)
        m.getAppWidgetIds(ComponentName(ctx, WidgetEnfoque::class.java)).forEach { m.updateAppWidget(it, vistaEnfoque(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetAnimo::class.java)).forEach { m.updateAppWidget(it, vistaAnimo(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetMomento::class.java)).forEach { m.updateAppWidget(it, vistaMomento(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetHoy::class.java)).forEach { m.updateAppWidget(it, vistaHoy(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetGasto::class.java)).forEach { m.updateAppWidget(it, vistaGasto(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetSueno::class.java)).forEach { m.updateAppWidget(it, vistaSueno(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetHabitos::class.java)).forEach { m.updateAppWidget(it, vistaHabitos(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetMes::class.java)).forEach { m.updateAppWidget(it, vistaMes(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetAnillos::class.java)).forEach { m.updateAppWidget(it, vistaAnillos(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetFoto::class.java)).forEach { m.updateAppWidget(it, vistaFoto(ctx)) }
        m.getAppWidgetIds(ComponentName(ctx, WidgetCifra::class.java)).forEach { m.updateAppWidget(it, vistaCifra(ctx)) }
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

class WidgetAnillos : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Repo.init(context); appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, Widgets.vistaAnillos(context)) }
    }
}

class WidgetFoto : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Repo.init(context); appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, Widgets.vistaFoto(context)) }
    }
}

class WidgetEnfoque : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Repo.init(context); appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, Widgets.vistaEnfoque(context)) }
    }
}

class WidgetCifra : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Repo.init(context); appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, Widgets.vistaCifra(context)) }
    }
}

/** Pasa el widget de una cifra a la siguiente. */
class CifraReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Repo.init(context)
        val l = Widgets.CIFRAS
        val sig = l[(l.indexOf(Repo.config.widget_cifra).coerceAtLeast(0) + 1) % l.size]
        Repo.cambiarConfig { it.copy(widget_cifra = sig) }
        android.os.Handler(android.os.Looper.getMainLooper()).post { runCatching { Widgets.actualizar(context) } }
    }
}
