package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ciencia.Logros
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.reportes.Lienzo
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.SalonesTema
import io.github.ruddy2003.vestigium.ui.TemaId
import java.io.File

/**
 * El hall como imagen para guardar o compartir: 1080 × 1350 (el formato vertical de las redes), con la escena del tema,
 * las piezas en sus pedestales y, si quieres, sus nombres y fechas. Sin textos del diario ni nada privado.
 */
object HallImagen {
    const val ANCHO = 1080
    const val ALTO = 1350

    fun crear(ctx: Context, piezas: List<Logros.Logro>, tema: TemaId, cs: ColorScheme, conTextos: Boolean): File {
        val img = ImageBitmap(ANCHO, ALTO)
        val lienzo = Canvas(img)
        val densidad = Density(ctx.resources.displayMetrics.density)
        val L = Lienzo(ctx, tema)
        val arriba = 210f; val abajo = 90f

        fun zona(x: Float, y: Float, w: Float, h: Float, dibujo: DrawScope.() -> Unit) {
            lienzo.save(); lienzo.translate(x, y); lienzo.clipRect(0f, 0f, w, h)
            CanvasDrawScope().draw(densidad, LayoutDirection.Ltr, lienzo, Size(w, h), dibujo)
            lienzo.restore()
        }

        val lista = piezas.take(12)
        val porFila = if (lista.size <= 4) 2 else 3

        // Fondo y escena.
        zona(0f, 0f, ANCHO.toFloat(), ALTO.toFloat()) { drawRect(cs.background) }
        zona(0f, arriba, ANCHO.toFloat(), ALTO - arriba - abajo) {
            with(SalonesTema) { escena(tema, cs, porFila) }
        }

        // Encabezado.
        val c = lienzo.nativeCanvas
        val (textoMarca, colofon) = marcaHall(tema)
        val marca = L.p(L.tenue, 30f, L.titulo, 0.2f)
        while (marca.measureText(textoMarca) > ANCHO - 128f && marca.textSize > 18f) marca.textSize = marca.textSize - 1f
        c.drawText(textoMarca, 64f, 92f, marca)
        val (titulo, _) = Estilo.pantalla("Hall de logros", "ATRIVM PALMARVM")
        val pt = L.p(L.primario, if (titulo.length > 20) 58f else 72f, L.titulo, 0.03f)
        c.drawText(titulo, 64f, 170f, pt)

        // Las piezas: 2 o 3 por fila, hasta 4 filas.
        val filas = ((lista.size + porFila - 1) / porFila).coerceAtLeast(1)
        val celdaW = ANCHO / porFila.toFloat()
        val celdaH = (ALTO - arriba - abajo) / filas.coerceAtLeast(if (porFila == 2) 2 else 3).toFloat()
        val diam = minOf(celdaW * 0.62f, celdaH * (if (conTextos) 0.56f else 0.68f))
        val pNombre = L.p(L.tinta, 28f, L.titulo); val pFecha = L.p(L.tenue, 22f, L.texto)
        lista.forEachIndexed { i, g ->
            val fila = i / porFila; val col = i % porFila
            val enFila = minOf(porFila, lista.size - fila * porFila)
            val x0 = (ANCHO - enFila * celdaW) / 2 + col * celdaW
            val y0 = arriba + fila * celdaH + 18f
            val cx = x0 + celdaW / 2
            zona(cx - diam * 0.41f, y0, diam * 0.82f, diam) { dibujarTrofeoDe(g, tema, cs.background.luminance() < 0.5f, cs.onSurface) }
            val metal = metalHall(tema, g.nivel)
            zona(cx - diam * 0.55f, y0 + diam + 4f, diam * 1.1f, 30f) {
                with(SalonesTema) { pedestal(tema, cs, metal) }
            }
            if (conTextos) {
                val nombre = recortar(g.nombre, pNombre, celdaW - 24f)
                c.drawText(nombre, cx - pNombre.measureText(nombre) / 2, y0 + diam + 70f, pNombre)
                g.conseguido?.let { f ->
                    val t = Fechas.corto(f) + " · " + f.take(4)
                    c.drawText(t, cx - pFecha.measureText(t) / 2, y0 + diam + 102f, pFecha)
                }
            }
        }

        // Pie.
        val pie = if (conTextos) colofon + " · " + Estilo.fecha(Fechas.diaLogico()) else colofon
        val pp = L.p(L.tenue, 24f, L.texto)
        while (pp.measureText(pie) > ANCHO - 128f && pp.textSize > 16f) pp.textSize = pp.textSize - 1f
        c.drawText(pie, ANCHO / 2f - pp.measureText(pie) / 2, ALTO - 40f, pp)

        val dir = File(File(ctx.cacheDir, "export"), "hall").apply { mkdirs(); listFiles()?.forEach { it.delete() } }
        val f = File(dir, "hall_${Fechas.diaLogico()}.png")
        f.outputStream().use { img.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it) }
        return f
    }

    /**
     * La marca del encabezado y el colofón del pie, en la voz de cada tema: latín epigráfico, una orden de consola,
     * griego, el explicit del copista, un número de expediente, árabe, mongol, ruso, francés, chino… (textos propios).
     */
    private fun marcaHall(tema: TemaId): Pair<String, String> = when (tema) {
        TemaId.ROMANO -> "VESTIGIVM MEI" to "SCRIPSIT ET SERVAVIT"
        TemaId.URBE -> "VESTIGIUM MEI · ROMA" to "scritto e conservato in città"
        TemaId.CIBER -> "usuario@vestigium:~/hall$ ls" to "[proceso terminado · código 0]"
        TemaId.NEON -> "EL CALLEJÓN DE LAS FICHAS · 屋台" to "— se apagan los letreros —"
        TemaId.HELENO -> "ΙΧΝΟΣ ΕΜΟΝ · VESTIGIUM MEI" to "ΜΝΗΜΗΣ ΧΑΡΙΝ"
        TemaId.EGIPTO -> "☥ VESTIGIUM MEI · PAPIRO DEL ESCRIBA" to "Que su nombre viva por siempre"
        TemaId.MEDIEVAL -> "Liber vestigiorum meorum" to "Explicit liber · Deo gratias"
        TemaId.TEMPLARIO -> "Domus Templi · Vestigium Mei" to "Non nobis, Domine, sed nomini tuo da gloriam"
        TemaId.DISTOPIA -> "EXPEDIENTE 07-118 · VESTIGIUM MEI" to "DOCUMENTO REVISADO · ARCHÍVESE"
        TemaId.REFUGIO -> "REFUGIO · REGISTRO DEL RESIDENTE" to "Todo en orden bajo tierra"
        TemaId.ORDEN -> "CODEX ORDINIS · VESTIGIUM MEI" to "AD SCIENTIAM CUSTODIENDAM"
        TemaId.VAPOR -> "Gran Exposición de Vestigium Mei · catálogo oficial" to "Fin del catálogo · a todo vapor"
        TemaId.LABORATORIO -> "VESTIGIUM MEI · LAB. 3 · LOTE 001" to "Fin del informe · n = 1"
        TemaId.COLONOS -> "La Gaceta del Pueblo · Vestigium Mei" to "Impreso en el taller del pueblo"
        TemaId.NAPOLEON -> "Bulletin de la Grande Armée · Vestigium Mei" to "Fait au quartier général"
        TemaId.DECO -> "Línea Transatlántica · Vestigium Mei" to "Impreso a bordo, en la imprenta del barco"
        TemaId.BOTANICO -> "Herbarium Vestigii Mei" to "Leg. et det. ipse"
        TemaId.RUINAS -> "Lo rescatado · Vestigium Mei" to "Juntado entre las ruinas, día tras día"
        TemaId.NAVIDAD -> "Vestigium Mei · Felices fiestas" to "Escrito junto al fuego"
        TemaId.SEDA -> "我的足迹 · Vestigium Mei" to "丝路行记"
        TemaId.EDO -> "我が足跡 · Vestigium Mei" to "東海道中日記"
        TemaId.GOTICO -> "Vestigium Mei · Horae" to "Per vitrum lux"
        TemaId.ARCANO -> "Vestigium Mei · archivo del caso" to "Lo que vi, anotado"
        TemaId.ESPACIO -> "CONTROL DE MISIÓN · VESTIGIUM MEI" to "Fin de la transmisión"
        TemaId.ABISMO -> "BITÁCORA DEL BATISCAFO · VESTIGIUM MEI" to "Fin de la inmersión"
        TemaId.ANDALUS -> "آثاري · Vestigium Mei" to "تمّ الكتاب"
        TemaId.MESO -> "Estelas de Vestigium Mei" to "Así quedó tallado en la piedra"
        TemaId.MONGOL -> "Миний мөр · Vestigium Mei" to "Мөнх тэнгэрийн хүчин дор"
        TemaId.RENACIMIENTO -> "Taccuino · Vestigium Mei" to "Fatto di mia mano"
        TemaId.PIRATA -> "Bitácora de Vestigium Mei" to "Escrito a bordo, de mi puño y letra"
        TemaId.URSS -> "МОЙ СЛЕД · VESTIGIUM MEI" to "План выполнен!"
        TemaId.REVOLUCION -> "Cahier du citoyen · Vestigium Mei" to "Liberté · Égalité · Fraternité"
        TemaId.ANTIGUAS -> "Libro de la compañía · Vestigium Mei" to "Aquí termina el relato, por ahora"
        TemaId.CUEVA -> "La pared del clan · Vestigium Mei" to "Aquí se acaba la roca pintada, por ahora"
    }

    private fun recortar(s: String, p: Paint, ancho: Float): String {
        if (p.measureText(s) <= ancho) return s
        var t = s
        while (t.length > 1 && p.measureText("$t…") > ancho) t = t.dropLast(1)
        return "$t…"
    }
}
