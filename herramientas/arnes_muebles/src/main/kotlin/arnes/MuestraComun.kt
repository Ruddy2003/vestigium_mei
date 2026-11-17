// Una hoja de muestra de las piezas comunes de Psicodelia v2 (ui/comun/ComunPsicodelia.kt) y de las piezas compartidas de
// la decoración (ui/PsicodeliaPiezas.kt), en la cara (ARNES_CARA) y la intensidad (ARNES_INTENSIDAD) del arnés. No es una
// pantalla de la app: cada pieza va a su tamaño de uso, con un texto de ejemplo encima donde lo lleva, para ver que se lee.
package arnes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.EstadoTema
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia
import io.github.ruddy2003.vestigium.ui.biblioteca.textoEn
import io.github.ruddy2003.vestigium.ui.comun.ComunPsicodelia
import io.github.ruddy2003.vestigium.ui.comun.EstadoAdorno
import io.github.ruddy2003.vestigium.ui.comun.EstadoAsiento
import io.github.ruddy2003.vestigium.ui.comun.EstadoAviso
import io.github.ruddy2003.vestigium.ui.comun.EstadoBalanza
import io.github.ruddy2003.vestigium.ui.comun.EstadoBotonTexto
import io.github.ruddy2003.vestigium.ui.comun.EstadoCifra
import io.github.ruddy2003.vestigium.ui.comun.EstadoFila
import io.github.ruddy2003.vestigium.ui.comun.EstadoMarco
import io.github.ruddy2003.vestigium.ui.comun.EstadoMes
import io.github.ruddy2003.vestigium.ui.comun.EstadoSeparador
import io.github.ruddy2003.vestigium.ui.comun.EstadoTarjeta
import io.github.ruddy2003.vestigium.ui.comun.EstadoVacio
import io.github.ruddy2003.vestigium.ui.comun.TipoTarjeta

private val d = ComunPsicodelia

@Composable
private fun Pieza(alto: Dp, ancho: Dp? = null, dibujo: DrawScope.(TextMeasurer) -> Unit) {
    val medidor = rememberTextMeasurer()
    Canvas((if (ancho != null) Modifier.width(ancho) else Modifier.fillMaxWidth()).height(alto)) { dibujo(medidor) }
}

@Composable
private fun Rotulo(t: String) = Text(t, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 11.sp, modifier = Modifier.padding(top = 10.dp, bottom = 3.dp))

private fun DrawScope.texto(m: TextMeasurer, t: String, r: Rect, c: Color, tam: Float = 15f) = textoEn(m, t, r, LetraExtra.franklin, c, tam)

@Composable
internal fun MuestraComun(oscuro: Boolean) {
    val o = oscuro
    val esquema = MaterialTheme.colorScheme
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("cara ${EstadoTema.cara} · i = ${EstadoTema.intensidad} · ${if (o) "oscuro" else "claro"}", color = esquema.onBackground, fontSize = 13.sp)
        Rotulo("emblema · banda bajo el título · subrayado")
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Pieza(64.dp, 64.dp) { with(PiezasPsicodelia) { emblema(center, size.minDimension / 2, 0.2f, o) } }
            Pieza(40.dp, 40.dp) { with(PiezasPsicodelia) { emblema(center, size.minDimension / 2, 0.6f, o) } }
            Column(Modifier.width(250.dp)) {
                Text("Lado B", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
                Pieza(16.dp) { with(PiezasPsicodelia) { subrayado(size.width * 0.8f, size.height / 2, colores(o)) } }
            }
        }
        Pieza(13.dp) { with(PiezasPsicodelia) { bandaTitulo(o) } }
        Rotulo("íconos de pestaña: día · diario · momentos · explorar · registrar · ajustes · vida · más")
        androidx.compose.runtime.CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides esquema.onBackground) {
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                listOf("dia", "diario", "momentos", "explorar", "registrar", "ajustes", "vida", "mas").forEach { id ->
                    Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) { io.github.ruddy2003.vestigium.ui.IconoPestana(id, io.github.ruddy2003.vestigium.ui.TemaId.PSICODELIA) }
                }
            }
        }
        Rotulo("campos: normal · con foco · error · apagado · varios renglones")
        listOf(EstadoMarco(false, false, true, false, true, 0), EstadoMarco(true, false, true, false, false, 0), EstadoMarco(false, true, true, false, false, 0),
            EstadoMarco(false, false, false, false, true, 0)).forEachIndexed { j, e ->
            Pieza(54.dp) { m -> with(d) { marcoCampo(e, o) }
                texto(m, listOf("Pista del campo", "La niebla se retira", "No sirve", "Apagado")[j], Rect(16.dp.toPx(), 0f, size.width * 0.6f, size.height),
                    if (j == 0 || j == 3) d.pistaCampo(o) else d.tintaCampo(o)) }
        }
        Pieza(110.dp) { with(d) { marcoCampo(EstadoMarco(true, false, true, true, false, 0), o) } }
        Rotulo("separador · tenue")
        Pieza(12.dp) { with(d) { separador(EstadoSeparador(false, 0), o) } }
        Pieza(12.dp) { with(d) { separador(EstadoSeparador(true, 0), o) } }
        Rotulo("botones de texto: normal · presionado · apagado")
        Row {
            listOf(EstadoBotonTexto(false, true, 0), EstadoBotonTexto(true, true, 0), EstadoBotonTexto(false, false, 0)).forEach { e ->
                Pieza(48.dp, 120.dp) { m -> with(d) { botonTexto(e, o) }; texto(m, "Ver el día", Rect(0f, 0f, size.width, size.height), d.tintaBotonTexto(o).copy(alpha = if (e.habilitado) 1f else 0.4f)) }
            }
        }
        Rotulo("filas: normal · presionada · con detalle · flecha · fija / sin fijar")
        listOf(EstadoFila(false, false, 0), EstadoFila(true, false, 1), EstadoFila(false, true, 2)).forEach { e ->
            Row {
                Pieza(52.dp, 300.dp) { m -> with(d) { fila(e, o) }; texto(m, "Disco 290 · lado B, pista 4", Rect(d.margenFila.toPx() + 6.dp.toPx(), 0f, size.width - 8.dp.toPx(), size.height), d.tintaCampo(o)) }
                Pieza(20.dp, 20.dp) { with(d) { flechaFila(o) } }
                Pieza(24.dp, 24.dp) { with(d) { marcaFija(e.semilla != 1, o) } }
            }
        }
        Rotulo("vacío")
        Box(Modifier.fillMaxWidth().height(150.dp)) {
            Pieza(150.dp) { m -> with(d) { marcoVacio(EstadoVacio(1), o) }
                texto(m, "Todavía no hay nada aquí", Rect(0f, 84.dp.toPx(), size.width, 112.dp.toPx()), d.tintaVacio(o)) }
            Canvas(Modifier.align(Alignment.TopCenter).padding(top = 26.dp).size(d.tamDibujoVacio)) { with(d) { dibujoVacio(EstadoVacio(1), o) } }
        }
        Rotulo("avisos · cifras")
        listOf(0, 1).forEach { s ->
            Pieza(56.dp) { m -> with(d) { aviso(EstadoAviso(s), o) }; texto(m, "Suena el 106.9 hasta las seis", Rect(d.margenAviso.toPx(), 0f, size.width - 10.dp.toPx(), size.height), d.tintaAviso(o)) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("SUEÑO" to "7 h 10", "PASOS" to "8 412", "ÁNIMO" to "5,2").forEach { (r, v) ->
                Pieza(62.dp, 110.dp) { m -> with(d) { cifra(EstadoCifra(0), o) }
                    texto(m, r, Rect(12.dp.toPx(), 6.dp.toPx(), size.width - 20.dp.toPx(), 24.dp.toPx()), d.rotuloCifra(o), 11f)
                    texto(m, v, Rect(12.dp.toPx(), 24.dp.toPx(), size.width - 20.dp.toPx(), 54.dp.toPx()), d.tintaCifra(o), 20f) }
            }
        }
        Rotulo("tarjetas: cifra · destacada · te falta hoy · completa · noche · sellada · vida · destacada")
        listOf(TipoTarjeta.METRICA, TipoTarjeta.PENDIENTE, TipoTarjeta.NOCHE, TipoTarjeta.VIDA).forEach { tipo ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(false, true).forEach { dest ->
                    Pieza(96.dp, 180.dp) { m -> with(d) { tarjeta(EstadoTarjeta(tipo, dest, false, 0), o) }
                        val mg = d.margenTarjeta.toPx()
                        texto(m, tipo.name.lowercase(), Rect(mg, mg, size.width - mg, mg + 18.dp.toPx()), d.acentoTarjeta(tipo, o), 13f)
                        texto(m, "La sala abre a las nueve", Rect(mg, mg + 20.dp.toPx(), size.width - mg, mg + 42.dp.toPx()), d.tintaTarjeta(tipo, o), 14f)
                        texto(m, "rótulo tenue", Rect(mg, mg + 44.dp.toPx(), size.width - mg, mg + 62.dp.toPx()), d.rotuloTarjeta(tipo, o), 12f) }
                }
            }
        }
        Rotulo("gastos: balanza · mes · asientos · día")
        Pieza(64.dp) { m -> with(d) { balanza(EstadoBalanza(false, "Gasto", "Ingreso", esquema.error, esquema.secondary), m, o) } }
        Pieza(52.dp) { m -> with(d) { encabezadoMes(EstadoMes("Álbum de octubre"), m, o) } }
        Pieza(22.dp) { m -> with(d) { rotuloDia("sáb 17 oct", m, o) } }
        listOf(true, false).forEach { ing -> Pieza(36.dp) { m -> with(d) { asiento(EstadoAsiento(ing, 3), o) }; texto(m, if (ing) "Pago de carteles" else "Pase de autobús", Rect(14.dp.toPx(), 0f, size.width * 0.6f, size.height), d.tintaCampo(o), 14f) } }
        Rotulo("hoja rápida · relaciones · marco de la cinta")
        Pieza(200.dp) { with(d) { adornoQuick(EstadoAdorno(esquema.primary, esquema.secondary, esquema.background), o) } }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Pieza(70.dp, 140.dp) { with(d) { adornoRelacion(EstadoAdorno(esquema.primary, esquema.secondary, esquema.surface), o) } }
            Pieza(28.dp, 28.dp) { with(d) { marcoPista(EstadoAdorno(esquema.primary.copy(alpha = 0.6f), esquema.primary, Color.Transparent), o) } }
            Pieza(18.dp, 18.dp) { with(d) { marcoPista(EstadoAdorno(esquema.primary.copy(alpha = 0.6f), esquema.primary, Color.Transparent), o) } }
        }
        Rotulo("decoración: esquinas · marca de escala · botón · perilla y riel · figura · diálogo")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Pieza(70.dp, 110.dp) { drawRect(esquema.surface); with(PiezasPsicodelia) { esquinas(4.dp.toPx(), 6.dp.toPx(), colores(o)) } }
            Pieza(40.dp, 40.dp) { drawRect(esquema.surfaceVariant); with(PiezasPsicodelia) { marcaEscala(esquema.primary, esquema.surfaceVariant, 4.dp.toPx(), colores(o)) } }
            Pieza(44.dp, 140.dp) { m -> drawRect(esquema.primary); with(PiezasPsicodelia) { adornoBoton(esquema.onPrimary.copy(alpha = 0.5f), 4.dp.toPx(), colores(o)) }
                texto(m, "Registrar", Rect(0f, 0f, size.width, size.height), esquema.onPrimary) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Pieza(30.dp, 220.dp) { with(PiezasPsicodelia) { val k = colores(o); riel(size.width * 0.6f, size.height / 2, 4.dp.toPx(), k.primario, esquema.outline, k)
                perilla(Offset(size.width * 0.6f, size.height / 2), 10.dp.toPx(), k.primario, esquema.surface, k) } }
            Pieza(30.dp, 30.dp) { with(PiezasPsicodelia) { figura(center, 13.dp.toPx(), esquema.primary, esquema.background, colores(o)) } }
        }
        Pieza(180.dp) { drawRect(esquema.surface); with(PiezasPsicodelia) { val k = colores(o); marcoDialogo(k, density); filete(16.dp.toPx(), size.width - 16.dp.toPx(), size.height * 0.6f, k) } }
        Rotulo("fondo vivo (t = 0.3, fuerza 1)")
        Pieza(300.dp) { drawRect(esquema.background); with(PiezasPsicodelia) { fondoVivo(0.3f, o, 1f) } }
    }
}
