package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.esAnimo
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import kotlin.math.PI
import kotlin.math.sin

/**
 * Un personaje del tema que vive en Día: legionario, hoplita, copista, ciudadano, residente del refugio, caballero,
 * científica, colono, granadero, naturalista, duende, caravanero, monje, astronauta, alarife, jinete, pintor, obrero…
 * Se ve cansado si dormiste poco, sonríe con tu ánimo, presume tu racha y se viste para el clima de afuera.
 * Todo dibujado aquí: ningún personaje, logo ni diseño ajeno.
 */
object Personaje {
    class Estado(val cansado: Boolean, val animo: Double?, val racha: Int, val lluvia: Boolean, val frio: Boolean, val calor: Boolean, val noche: Boolean, val frase: String)

    private fun racha(hoy: String): Int {
        var f = if (Repo.dias[hoy]?.let { it.nota.isNotBlank() || it.valencia != null } == true) hoy else Fechas.sumar(hoy, -1)
        var n = 0
        while (Repo.dias[f]?.let { it.nota.isNotBlank() || it.valencia != null } == true) { n++; f = Fechas.sumar(f, -1) }
        return n
    }

    /**
     * Lo que dice el personaje, en la voz de su tema. Las situaciones son las mismas en todos (y en el mismo orden de
     * prioridad); cambian las palabras.
     */
    private class Voz(
        val noche: String, val horas: (String) -> String, val cansado: String, val semanas: (Int) -> String, val lluvia: String,
        val frio: String, val calor: String, val bien: String, val pesado: String, val racha: (Int) -> String, val nada: String,
    )

    private fun voz(t: TemaId): Voz = when (t) {
        TemaId.ROMANO -> Voz("Ya cae la vigilia. ¿Cerramos la tablilla de hoy?", { "Dormiste $it h. Hoy marchamos a paso corto." },
            "Mala noche en el campamento. Un día tranquilo también suma.", { "¡$it días sin soltar la tablilla! Sigamos." },
            "Llueve sobre el foro. Buen día para leer junto a la lucerna.", "Frío de invierno: me pongo el sagum.", "Sol de agosto sobre el mármol. Agua a mano.",
            "Día favorable. Escríbelo para recordarlo.", "Día pesado. Aquí sigo, firme a tu lado.", { "Racha de $it días. Una jornada más." },
            "¿Qué escribiremos hoy en la tablilla?")
        TemaId.CIBER -> Voz("> hora tardía. ¿cerrar_dia() antes de dormir?", { "> sueño: $it h. modo_ahorro activo; tú también puedes." },
            "> descanso insuficiente. Un día lento también se guarda.", { "> racha = $it días. 0 errores. continuar." },
            "> sensor: lluvia. Buen momento para leer.", "> temp_ext baja. Abriga el hardware.", "> temp_ext alta. Hidratación recomendada.",
            "> ánimo: alto. Guardar en el registro.", "> ánimo: bajo. Sigo en línea contigo.", { "> racha = $it días. Ejecutar el día siguiente." },
            "> esperando entrada_")
        TemaId.HELENO -> Voz("Ya salió Selene. ¿Cerramos el día antes del sueño?", { "Dormiste $it h. Hoy, paso firme y lento, como la falange." },
            "Mala noche. Hasta Odiseo descansaba; ve suave.", { "¡$it días seguidos! Digno de un himno." },
            "Zeus manda lluvia. Buen día para leer.", "Viento del norte: me pongo la clámide.", "Helios en lo alto. Agua fresca a mano.",
            "Hoy hay buen ánimo. Anótalo, que la memoria es una musa.", "Día pesado. Aquí sigo, escudo con escudo.", { "Racha de $it días. Otra jornada más." },
            "¿Qué contaremos hoy?")
        TemaId.MEDIEVAL -> Voz("Ya tocaron completas. ¿Cerramos el folio del día?", { "Dormiste $it h. Hoy copio despacio; tú también puedes." },
            "Mala noche en la celda. Un día quieto también se anota.", { "¡$it días sin dejar el códice! Sigamos copiando." },
            "Llueve en el claustro. Buen día para leer.", "Frío en el scriptorium: la tinta se espesa.", "Calor de verano. Agua junto al tintero.",
            "Día luminoso. Merece una capitular en rojo.", "Día pesado. Aquí sigo, a tu lado en el atril.", { "Racha de $it días. Otro folio más." },
            "¿Qué copiaremos hoy?")
        TemaId.DISTOPIA -> Voz("Hora tardía. El expediente de hoy sigue abierto.", { "Registro de sueño: $it h. Se autoriza un día lento." },
            "Descanso deficiente. Un día suave también se archiva.", { "$it días consecutivos registrados. Conducta ejemplar." },
            "Parte meteorológico: lluvia. Se recomienda lectura.", "Temperatura baja. Abrigo reglamentario.", "Temperatura alta. Ración de agua a la mano.",
            "Ánimo favorable. Consígnelo en el expediente.", "Día pesado. Este funcionario no se va.", { "Racha de $it días. Siguiente formulario." },
            "Expediente del día: pendiente de llenar.")
        TemaId.REFUGIO -> Voz("Luces de noche en el refugio. ¿Cerramos el día?", { "Dormiste $it h. Turno ligero hoy, para los dos." },
            "Mala noche en la litera. Un día suave también cuenta.", { "¡$it días de registro sin falla! Moral alta." },
            "Arriba llueve. Aquí abajo, buen día para leer.", "Frío en los pasillos. Suéter del refugio puesto.", "La ventilación no da abasto. Agua a la mano.",
            "¡Ánimo excelente! Anótalo para el boletín.", "Día pesado. Aquí sigo, puerta con puerta.", { "Racha de $it días. Mantenimiento al día." },
            "¿Qué hay hoy en la bitácora?")
        TemaId.ORDEN -> Voz("Empieza la guardia de noche. ¿Sellamos el registro?", { "Dormiste $it h. Hoy la armadura pesa; vamos despacio." },
            "Mala noche en el cuartel. Descansar también es deber.", { "¡$it días de servicio sin falta! El códice lo guarda." },
            "Llueve sobre la ciudadela. Buen día para estudiar.", "Frío en la muralla. Capa sobre el acero.", "La armadura quema. Agua antes de la ronda.",
            "Buen ánimo en la orden. Déjalo por escrito.", "Día pesado. Mantengo la guardia a tu lado.", { "Racha de $it días. Otro turno de guardia." },
            "¿Qué saber custodiamos hoy?")
        TemaId.LABORATORIO -> Voz("Hora tardía. ¿Cerramos el cuaderno de hoy?", { "Dato: $it h de sueño. Hoy, ritmo bajo; tú también." },
            "Sueño de mala calidad. Un día suave también es un dato.", { "n = $it días seguidos. La muestra va bien." },
            "Precipitación registrada. Buen día para leer.", "Temperatura baja. Bata y bufanda.", "Temperatura alta. Hidratación controlada.",
            "Ánimo alto. Regístralo: es un buen punto de datos.", "Día pesado. Sigo aquí, sin juicio.", { "Racha de $it días. Siguiente medición." },
            "¿Qué mediremos hoy?")
        TemaId.COLONOS -> Voz("Ya se apagan los faroles. ¿Cerramos el día?", { "Dormiste $it h. Hoy se trabaja con calma." },
            "Mala noche. En el pueblo, un día suave también sirve.", { "¡$it días seguidos! Esto sale en la gaceta." },
            "Llueve sobre los tejados. Buen día para leer.", "Frío en la plaza. Leña y bufanda.", "Calor en el campo. Agua del pozo a la mano.",
            "Buen día en el pueblo. Anótalo.", "Día pesado. Aquí está tu vecino.", { "Racha de $it días. Ladrillo a ladrillo." },
            "¿Qué reconstruimos hoy?")
        TemaId.NAPOLEON -> Voz("Toque de silencio. ¿Cerramos el boletín del día?", { "Dormiste $it h. Hoy, marcha lenta: es una orden." },
            "Mala noche en el vivac. Un día tranquilo también es campaña.", { "¡$it días seguidos! Digno de una condecoración." },
            "Llueve sobre el campamento. Buen día para leer.", "Frío de invierno. Capote abrochado.", "Calor de campaña. Cantimplora llena.",
            "Jornada gloriosa. Que conste en el boletín.", "Día pesado. La guardia no se rinde: sigo aquí.", { "Racha de $it días. En avant." },
            "¿Cuál es la orden del día?")
        TemaId.BOTANICO -> Voz("Ya salen las polillas. ¿Cerramos el cuaderno?", { "Dormiste $it h. Hoy observamos sin prisa." },
            "Mala noche. Hasta los helechos descansan; ve suave.", { "¡$it días de notas! Un buen herbario." },
            "Llueve: salen los caracoles. Buen día para leer.", "Frío en el sendero. Bufanda y lupa.", "Calor de trópico. Agua en la cantimplora.",
            "Buen día de campo. Anótalo antes de que se seque.", "Día pesado. Aquí sigo, fiel como el musgo.", { "Racha de $it días. Otro ejemplar." },
            "¿Qué encontraremos hoy?")
        TemaId.NAVIDAD -> Voz("Ya duermen los renos. ¿Cerramos el día?", { "Dormiste $it h. Hoy envolvemos despacio." },
            "Mala noche. Un día suave junto al fuego también cuenta.", { "¡$it días seguidos! Eso merece una galleta." },
            "Llueve en vez de nevar. Buen día para leer.", "¡Frío de nieve! Bufanda de lana puesta.", "¡Calor en el taller! Agua a la mano.",
            "Día alegre. Anótalo en la carta.", "Día pesado. Aquí sigo, junto al fuego.", { "Racha de $it días. Otro adorno para el árbol." },
            "¿Qué preparamos hoy?")
        TemaId.SEDA -> Voz("Las estrellas guían la caravana. ¿Cerramos el día?", { "Dormiste $it h. Hoy, a paso de camello." },
            "Mala noche en el caravasar. Un día lento también avanza.", { "¡$it días de camino sin parar! Buena seda." },
            "Lluvia en el desierto: rara fortuna. Buen día para leer.", "Frío de paso de montaña. Gorro calado.", "Sol de desierto. Agua del oasis a la mano.",
            "Buen ánimo en la caravana. Anótalo.", "Día pesado. Aquí sigo; nadie cruza solo.", { "Racha de $it días. Otra posta en el camino." },
            "¿Hacia dónde va hoy la caravana?")
        TemaId.GOTICO -> Voz("Ya es la hora de completas. ¿Cerramos el día?", { "Dormiste $it h. Hoy las horas pasan despacio." },
            "Mala noche de vigilia. Un día quieto también es oración.", { "¡$it días seguidos! Como las horas, sin faltar una." },
            "Llueve sobre los vitrales. Buen día para leer.", "Frío de piedra en la nave. Hábito bien cerrado.", "Calor en el claustro. Agua del aljibe.",
            "Día luminoso como un rosetón. Anótalo.", "Día pesado. Enciendo una vela a tu lado.", { "Racha de $it días. Otra hora canónica." },
            "¿Qué guardaremos hoy en el libro de horas?")
        TemaId.ESPACIO -> Voz("Ciclo nocturno a bordo. ¿Cerramos la bitácora?", { "Dormiste $it h. Hoy, maniobras suaves." },
            "Mal descanso en órbita. Un día lento también es misión.", { "¡$it días en órbita sin perder registro! Control felicita." },
            "Abajo llueve. Aquí arriba, buen día para leer.", "Temperatura exterior baja. Traje térmico.", "Temperatura alta. Reserva de agua lista.",
            "Ánimo nominal y en alza. Regístralo.", "Día pesado. Control sigue en contacto.", { "Racha de $it días. Siguiente órbita." },
            "Control, ¿cuál es la misión de hoy?")
        TemaId.ANDALUS -> Voz("Ya cae la noche sobre el patio. ¿Cerramos el día?", { "Dormiste $it h. Hoy el yeso fragua sin prisa." },
            "Mala noche. Un día sereno, como un patio con fuente.", { "¡$it días seguidos! Lazo sobre lazo, como un alicatado." },
            "Llueve sobre la alberca. Buen día para leer.", "Frío de la sierra. Turbante bien ceñido.", "Calor de verano. Agua de la acequia.",
            "Buen ánimo. Escríbelo con buena caligrafía.", "Día pesado. Aquí sigo, a la sombra contigo.", { "Racha de $it días. Otro azulejo en su sitio." },
            "¿Qué levantaremos hoy?")
        TemaId.MONGOL -> Voz("El Cielo Azul se oscurece. ¿Cerramos el día?", { "Dormiste $it h. Hoy, al paso del caballo." },
            "Mala noche en la yurta. Un día lento también avanza.", { "¡$it días a caballo sin parar! Fuerte como la estepa." },
            "Llueve en la estepa. Buen día para leer en la yurta.", "Frío de estepa. Deel bien cerrado.", "Sol fuerte en la estepa. Agua en el odre.",
            "Buen ánimo, como un caballo veloz. Anótalo.", "Día pesado. Aquí sigo, junto al fuego.", { "Racha de $it días. Otra jornada de marcha." },
            "¿Hacia dónde cabalgamos hoy?")
        TemaId.RENACIMIENTO -> Voz("Se acaba la luz del taller. ¿Cerramos el cuaderno?", { "Dormiste $it h. Hoy, trazo lento." },
            "Mala noche. Un boceto suave también es obra.", { "¡$it días de cuaderno! La bottega aplaude." },
            "Llueve sobre la ciudad. Buen día para leer.", "Frío en el taller. Capa y sanguina.", "Calor de verano: el fresco se seca. Agua a mano.",
            "Buena luz hoy. Anótala antes de que se vaya.", "Día pesado. Aquí sigo, junto al caballete.", { "Racha de $it días. Otra hoja del cuaderno." },
            "¿Qué dibujaremos hoy?")
        TemaId.URSS -> Voz("Termina el turno de noche. ¿Cerramos el día, camarada?", { "Dormiste $it h. Hoy la norma es más baja." },
            "Mala noche. Un día suave también construye.", { "¡$it días seguidos! Plan cumplido antes de tiempo." },
            "Llueve sobre la fábrica. Buen día para leer.", "Frío de invierno. Ushanka bien puesta.", "Calor en la fundición. Agua a la mano.",
            "Ánimo de vanguardia. Anótalo en el informe.", "Día pesado. Aquí sigo, hombro con hombro.", { "Racha de $it días. Siguiente meta del plan." },
            "¿Cuál es la norma de hoy?")
        TemaId.REVOLUCION -> Voz("Ya es tarde, ciudadano. ¿Cerramos el cuaderno del día?", { "Dormiste $it h. Hoy, paso sereno." },
            "Mala noche. Un día suave también es libertad.", { "¡$it días seguidos! Constancia republicana." },
            "Llueve sobre la plaza. Buen día para leer.", "Frío de nivoso. Bufanda tricolor.", "Calor de termidor. Agua a la mano.",
            "Buen ánimo, ciudadano. Que conste en el cuaderno.", "Día pesado. Fraternidad: aquí sigo.", { "Racha de $it días. Otra década en marcha." },
            "¿Qué queja o qué esperanza anotamos hoy?")
        TemaId.ANTIGUAS -> Voz("La luna está alta. ¿Cerramos el día antes de dormir?", { "Dormiste $it h. Hoy el camino va despacio." },
            "Mala noche en la posada. Un día suave también es viaje.", { "¡$it días de camino! La compañía sigue unida." },
            "Llueve sobre el bosque. Buen día para leer junto al fuego.", "Frío de montaña. Capa bien cerrada.", "Calor en el valle. Agua del arroyo a la mano.",
            "Buen ánimo en la compañía. Anótalo en el mapa.", "Día pesado. Aquí sigo, como en toda jornada.", { "Racha de $it días. Otra legua de camino." },
            "¿Qué senda tomamos hoy?")
    }

    fun estado(hoy: String = Fechas.diaLogico(), tema: TemaId = Estilo.tema): Estado {
        val d = Repo.dias[hoy]
        val horas = Repo.salud[hoy]?.sueno_min?.let { it / 60.0 }
        val cansado = (horas != null && horas < 6.0) || (d?.sueno_calidad != null && d.sueno_calidad <= 2)
        val animo = d?.valencia?.toDouble() ?: Repo.momentos.filter { it.fecha == hoy && it.esAnimo }.mapNotNull { it.valencia }.lastOrNull()?.toDouble()
        val e = Repo.entorno[hoy]
        val lluvia = (e?.precip_mm ?: 0.0) >= 1.0 || (e?.codigo?.let { it in 51..67 || it in 80..82 || it >= 95 } == true)
        val frio = (e?.t_max ?: 99.0) < 14.0
        val calor = (e?.t_max ?: -99.0) >= 30.0
        val h = java.time.LocalTime.now().hour
        val noche = h >= 22 || h < 5
        val r = racha(hoy)
        val v = voz(tema)
        val frase = when {
            noche && d?.nota.isNullOrBlank() -> v.noche
            cansado && horas != null -> v.horas("%.1f".format(horas))
            cansado -> v.cansado
            r > 0 && r % 7 == 0 -> v.semanas(r)
            lluvia -> v.lluvia
            frio -> v.frio
            calor -> v.calor
            animo != null && animo >= 6 -> v.bien
            animo != null && animo <= 2 -> v.pesado
            r >= 2 -> v.racha(r)
            else -> v.nada
        }
        return Estado(cansado, animo, r, lluvia, frio, calor, noche, frase)
    }

    /** Un sombrero y un objeto por tema: ninguno se repite entre temas (la capa sí, como prenda común). */
    private enum class Sombrero { CRESTA, CORINTIO, YELMO, GORRA, CASCO_OBRA, AURICULARES, VISOR, GAFAS, ALA, BICORNIO, SALACOT, GORRO_NAVIDAD, TURBANTE, CAPUCHA, ESCAFANDRA, PUNTA, BOINA, USHANKA, FRIGIO, GORRO_CARAVANA, TONSURA }
    private enum class Objeto { LANZA, ESCUDO, ESPADA, CARPETA, LLAVE, TABLETA, LIBRO, MATRAZ, PALA, CATALEJO, LUPA, REGALO, FAROL, VELA, BANDERA, ARCO, PINCEL, MARTILLO, PERGAMINO, TRICOLOR, BACULO }

    private class Atuendo(val sombrero: Sombrero, val objeto: Objeto, val capa: Boolean)

    private fun atuendo(t: TemaId): Atuendo = when (t) {
        TemaId.ROMANO -> Atuendo(Sombrero.CRESTA, Objeto.LANZA, true)
        TemaId.HELENO -> Atuendo(Sombrero.CORINTIO, Objeto.ESCUDO, true)
        TemaId.MEDIEVAL -> Atuendo(Sombrero.CAPUCHA, Objeto.PERGAMINO, false)
        TemaId.DISTOPIA -> Atuendo(Sombrero.GORRA, Objeto.CARPETA, false)
        TemaId.REFUGIO -> Atuendo(Sombrero.CASCO_OBRA, Objeto.LLAVE, false)
        TemaId.CIBER -> Atuendo(Sombrero.AURICULARES, Objeto.TABLETA, false)
        TemaId.ORDEN -> Atuendo(Sombrero.VISOR, Objeto.ESPADA, true)
        TemaId.LABORATORIO -> Atuendo(Sombrero.GAFAS, Objeto.MATRAZ, false)
        TemaId.COLONOS -> Atuendo(Sombrero.ALA, Objeto.PALA, false)
        TemaId.NAPOLEON -> Atuendo(Sombrero.BICORNIO, Objeto.CATALEJO, true)
        TemaId.BOTANICO -> Atuendo(Sombrero.SALACOT, Objeto.LUPA, false)
        TemaId.NAVIDAD -> Atuendo(Sombrero.GORRO_NAVIDAD, Objeto.REGALO, false)
        TemaId.SEDA -> Atuendo(Sombrero.GORRO_CARAVANA, Objeto.FAROL, true)
        TemaId.GOTICO -> Atuendo(Sombrero.TONSURA, Objeto.VELA, true)
        TemaId.ESPACIO -> Atuendo(Sombrero.ESCAFANDRA, Objeto.BANDERA, false)
        TemaId.ANDALUS -> Atuendo(Sombrero.TURBANTE, Objeto.LIBRO, true)
        TemaId.MONGOL -> Atuendo(Sombrero.PUNTA, Objeto.ARCO, true)
        TemaId.RENACIMIENTO -> Atuendo(Sombrero.BOINA, Objeto.PINCEL, false)
        TemaId.URSS -> Atuendo(Sombrero.USHANKA, Objeto.MARTILLO, false)
        TemaId.REVOLUCION -> Atuendo(Sombrero.FRIGIO, Objeto.TRICOLOR, false)
        TemaId.ANTIGUAS -> Atuendo(Sombrero.YELMO, Objeto.BACULO, true)
    }

    /** Dibuja al personaje en un lienzo de [s] (alto); [t] es la fase de la animación (0 a 1), [parpadeo] cierra los ojos. */
    fun DrawScope.dibujar(tema: TemaId, e: Estado, ropa: Color, acento: Color, piel: Color, tinta: Color, fondo: Color, t: Float, parpadeo: Boolean) {
        val a = atuendo(tema)
        val u = size.height / 120f
        val cx = size.width / 2
        val baja = if (e.cansado) 3 * u else 0f
        val bote = sin(t * 2 * PI).toFloat() * 1.6f * u
        val y0 = 18 * u + baja + bote
        val cabeza = Offset(cx, y0 + 18 * u)
        val r = 14 * u
        // Aura de racha larga.
        if (e.racha >= 30) drawCircle(acento.copy(alpha = 0.16f + 0.06f * sin(t * 2 * PI).toFloat()), 52 * u, Offset(cx, 64 * u))
        // Capa detrás del cuerpo.
        if (a.capa) drawPath(Path().apply { moveTo(cx - 14 * u, y0 + 34 * u); lineTo(cx + 14 * u, y0 + 34 * u); lineTo(cx + 24 * u, 112 * u); lineTo(cx - 24 * u, 112 * u); close() }, acento.copy(alpha = 0.85f))
        // Cuerpo (túnica, saco, bata…) y piernas.
        val cuerpo = Path().apply { moveTo(cx - 12 * u, y0 + 33 * u); lineTo(cx + 12 * u, y0 + 33 * u); lineTo(cx + 18 * u, y0 + 76 * u); lineTo(cx - 18 * u, y0 + 76 * u); close() }
        drawPath(cuerpo, ropa)
        drawLine(tinta, Offset(cx - 7 * u, y0 + 76 * u), Offset(cx - 8 * u, 116 * u), 4.5f * u, StrokeCap.Round)
        drawLine(tinta, Offset(cx + 7 * u, y0 + 76 * u), Offset(cx + 8 * u, 116 * u), 4.5f * u, StrokeCap.Round)
        drawLine(acento, Offset(cx - 16 * u, y0 + 58 * u), Offset(cx + 16 * u, y0 + 58 * u), 2.4f * u)
        // Brazos: el derecho sostiene el objeto; el izquierdo, el paraguas si llueve.
        val manoD = Offset(cx + 24 * u, y0 + 58 * u)
        val manoI = if (e.lluvia) Offset(cx - 22 * u, y0 + 40 * u) else Offset(cx - 22 * u, y0 + 60 * u)
        drawLine(ropa, Offset(cx + 11 * u, y0 + 37 * u), manoD, 5f * u, StrokeCap.Round)
        drawLine(ropa, Offset(cx - 11 * u, y0 + 37 * u), manoI, 5f * u, StrokeCap.Round)
        drawCircle(piel, 3 * u, manoD); drawCircle(piel, 3 * u, manoI)
        objeto(a.objeto, manoD, u, acento, tinta, ropa, fondo)
        // Cuello y bufanda si hace frío.
        if (e.frio) {
            drawRoundRect(acento, Offset(cx - 12 * u, y0 + 30 * u), Size(24 * u, 6 * u), androidx.compose.ui.geometry.CornerRadius(3 * u, 3 * u))
            drawLine(acento, Offset(cx + 6 * u, y0 + 34 * u), Offset(cx + 9 * u, y0 + 48 * u), 4 * u, StrokeCap.Round)
        }
        // Cabeza, ojos y boca.
        val inclina = if (e.cansado) 8f else 0f
        rotate(inclina, cabeza) {
            drawCircle(piel, r, cabeza)
            drawCircle(tinta.copy(alpha = 0.6f), r, cabeza, style = Stroke(1.2f * u))
            val ojoY = cabeza.y - 1 * u
            listOf(-5f, 5f).forEach { dx ->
                val o = Offset(cabeza.x + dx * u, ojoY)
                if (parpadeo || e.cansado) drawLine(tinta, o - Offset(2.4f * u, 0f), o + Offset(2.4f * u, if (e.cansado) 0.8f * u else 0f), 1.4f * u, StrokeCap.Round)
                else drawCircle(tinta, 1.9f * u, o)
            }
            // La boca según el ánimo: de 1 (triste) a 7 (feliz).
            val v = ((e.animo ?: 5.0) - 4.0) / 3.0
            val boca = Path().apply {
                moveTo(cabeza.x - 5 * u, cabeza.y + 6 * u)
                quadraticTo(cabeza.x, cabeza.y + 6 * u + (v * 5 * u).toFloat(), cabeza.x + 5 * u, cabeza.y + 6 * u)
            }
            drawPath(boca, tinta, style = Stroke(1.5f * u, cap = StrokeCap.Round))
            if (e.calor) drawPath(Path().apply { val g = Offset(cabeza.x + 11 * u, cabeza.y - 6 * u); moveTo(g.x, g.y - 3 * u); quadraticTo(g.x + 2.5f * u, g.y + 1 * u, g.x, g.y + 2 * u); quadraticTo(g.x - 2.5f * u, g.y + 1 * u, g.x, g.y - 3 * u); close() }, Color(0xFF7FC8F8))
            sombrero(a.sombrero, cabeza, r, u, acento, ropa, tinta, fondo)
        }
        // Medalla de racha (una semana o más).
        if (e.racha >= 7) {
            val m = Offset(cx - 5 * u, y0 + 44 * u)
            drawLine(acento, m - Offset(2 * u, 6 * u), m, 1.2f * u); drawLine(acento, m + Offset(2 * u, -6 * u), m, 1.2f * u)
            drawCircle(Color(0xFFE3B748), 3.2f * u, m); drawCircle(tinta.copy(alpha = 0.5f), 3.2f * u, m, style = Stroke(0.8f * u))
        }
        // Paraguas y gotas.
        if (e.lluvia) {
            val p = manoI
            drawLine(tinta, p, p - Offset(0f, 26 * u), 1.4f * u)
            val tope = p - Offset(0f, 28 * u)
            drawArc(acento, 180f, 180f, true, Offset(tope.x - 22 * u, tope.y - 12 * u), Size(44 * u, 24 * u))
            for (i in 0 until 7) {
                val x = (i * 17f + t * 40f) % 100f
                val yy = ((i * 29f + t * 120f) % 110f)
                drawLine(Color(0xFF7FB6E8).copy(alpha = 0.7f), Offset(x / 100f * size.width, yy * u), Offset(x / 100f * size.width - 1.5f * u, yy * u + 5 * u), 1f * u)
            }
        } else if (e.calor) {
            val s = Offset(size.width - 12 * u, 12 * u)
            drawCircle(Color(0xFFF2C14E), 6 * u, s)
            for (i in 0 until 8) { val an = i * PI / 4 + t * PI / 4; drawLine(Color(0xFFF2C14E), s + Offset((9 * u * kotlin.math.cos(an)).toFloat(), (9 * u * sin(an)).toFloat()), s + Offset((12 * u * kotlin.math.cos(an)).toFloat(), (12 * u * sin(an)).toFloat()), 1.2f * u) }
        }
        // Zetas de sueño en la noche.
        if (e.noche) {
            val z = Offset(cabeza.x + 16 * u, cabeza.y - 18 * u - t * 6 * u)
            listOf(0f to 1f, 6f to 0.7f).forEach { (d, k) ->
                val o = z + Offset(d * u, -d * u)
                val l = 4 * u * k
                drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + l, o.y); lineTo(o.x, o.y + l); lineTo(o.x + l, o.y + l) }, tinta.copy(alpha = 0.7f), style = Stroke(1.1f * u))
            }
        }
    }

    private fun DrawScope.sombrero(s: Sombrero, c: Offset, r: Float, u: Float, acento: Color, ropa: Color, tinta: Color, fondo: Color) {
        val top = c.y - r
        when (s) {
            Sombrero.CRESTA -> { // Casco de legionario con cresta de crin.
                drawArc(Color(0xFFB0B7BE), 180f, 180f, true, Offset(c.x - r * 1.05f, top - 1 * u), Size(r * 2.1f, r * 1.6f))
                drawArc(acento, 200f, 140f, false, Offset(c.x - r * 0.9f, top - 9 * u), Size(r * 1.8f, r * 1.4f), style = Stroke(5 * u, cap = StrokeCap.Round))
                drawLine(Color(0xFF8A9096), Offset(c.x - r, c.y - 2 * u), Offset(c.x - r * 0.8f, c.y + 7 * u), 2.5f * u)
            }
            Sombrero.CORINTIO -> { // Casco griego con penacho y protector nasal.
                drawArc(Color(0xFFC9A04A), 180f, 200f, true, Offset(c.x - r * 1.05f, top - 2 * u), Size(r * 2.1f, r * 2.0f))
                drawLine(Color(0xFF8C6E2F), Offset(c.x, top), Offset(c.x, c.y + 4 * u), 2 * u)
                drawArc(acento, 190f, 160f, false, Offset(c.x - r * 1.1f, top - 12 * u), Size(r * 2.2f, r * 1.6f), style = Stroke(4 * u, cap = StrokeCap.Round))
            }
            Sombrero.YELMO -> { // Yelmo rúnico de las Tierras Antiguas: cúpula de hierro, cresta, protector nasal y runas que brillan.
                drawArc(Color(0xFF8E8A80), 180f, 180f, true, Offset(c.x - r * 1.08f, top - 3 * u), Size(r * 2.16f, r * 2f))
                drawLine(Color(0xFF6E6A60), Offset(c.x, top - 3 * u), Offset(c.x, c.y - 4 * u), 1.6f * u)
                drawRect(Color(0xFF6E6A60), Offset(c.x - r * 1.08f, c.y - 5 * u), Size(r * 2.16f, 3 * u))
                drawLine(Color(0xFF6E6A60), Offset(c.x, c.y - 3 * u), Offset(c.x, c.y + 4 * u), 2.4f * u, StrokeCap.Round)
                listOf(-7f, 6f).forEach { dx -> // Dos runas inventadas, una a cada lado de la cresta.
                    val o = Offset(c.x + dx * u, top + 2 * u)
                    drawLine(acento, o, o + Offset(0f, 5 * u), 1f * u, StrokeCap.Round)
                    drawLine(acento, o + Offset(0f, 1.5f * u), o + Offset(2 * u, -0.5f * u), 1f * u, StrokeCap.Round)
                    drawLine(acento, o + Offset(0f, 3.5f * u), o + Offset(-1.6f * u, 5 * u), 1f * u, StrokeCap.Round)
                }
            }
            Sombrero.GORRA -> { // Gorra gris de funcionario.
                drawRoundRect(Color(0xFF55595D), Offset(c.x - r, top - 3 * u), Size(r * 2, 7 * u), androidx.compose.ui.geometry.CornerRadius(2 * u, 2 * u))
                drawRect(tinta, Offset(c.x - r * 1.15f, top + 3 * u), Size(r * 1.4f, 2.2f * u))
                drawCircle(acento, 2 * u, Offset(c.x, top))
            }
            Sombrero.CASCO_OBRA -> {
                drawArc(Color(0xFFF2C14E), 180f, 180f, true, Offset(c.x - r * 1.05f, top - 2 * u), Size(r * 2.1f, r * 1.5f))
                drawRect(Color(0xFFD9A93A), Offset(c.x - r * 1.25f, top + 4 * u), Size(r * 2.5f, 2 * u))
            }
            Sombrero.AURICULARES -> { // El operador de la terminal: diadema, auriculares con su luz de fósforo y micrófono.
                drawArc(tinta, 180f, 180f, false, Offset(c.x - r * 1.12f, top - 3 * u), Size(r * 2.24f, r * 2.2f), style = Stroke(2.4f * u))
                listOf(c.x - r * 1.28f, c.x + r * 1.28f - 6 * u).forEach { x ->
                    drawRoundRect(tinta, Offset(x, c.y - 6 * u), Size(6 * u, 10 * u), androidx.compose.ui.geometry.CornerRadius(2 * u, 2 * u))
                    drawRect(acento, Offset(x + 2 * u, c.y - 3 * u), Size(2 * u, 4 * u))
                }
                drawLine(tinta, Offset(c.x - r * 1.05f, c.y + 3 * u), Offset(c.x - 6 * u, c.y + 8 * u), 1.4f * u, StrokeCap.Round)
                drawCircle(acento, 1.8f * u, Offset(c.x - 6 * u, c.y + 8 * u))
            }
            Sombrero.VISOR -> {
                drawArc(Color(0xFF8E979F), 180f, 180f, true, Offset(c.x - r * 1.1f, top - 3 * u), Size(r * 2.2f, r * 2.2f))
                drawRect(tinta, Offset(c.x - r * 0.8f, c.y - 4 * u), Size(r * 1.6f, 2.4f * u))
                drawLine(acento, Offset(c.x, top - 3 * u), Offset(c.x, top - 9 * u), 3 * u, StrokeCap.Round)
            }
            Sombrero.GAFAS -> {
                listOf(-5f, 5f).forEach { dx -> drawCircle(acento, 3.8f * u, Offset(c.x + dx * u, c.y - 1 * u), style = Stroke(1.3f * u)) }
                drawLine(acento, Offset(c.x - 1.2f * u, c.y - 1 * u), Offset(c.x + 1.2f * u, c.y - 1 * u), 1.2f * u)
                drawArc(tinta, 200f, 140f, false, Offset(c.x - r, top - 1 * u), Size(r * 2, r * 1.2f), style = Stroke(3 * u))
            }
            Sombrero.ALA -> {
                drawOval(Color(0xFF8A6A44), Offset(c.x - r * 1.5f, top + 2 * u), Size(r * 3f, 5 * u))
                drawRoundRect(Color(0xFF7A5C38), Offset(c.x - r * 0.8f, top - 7 * u), Size(r * 1.6f, 10 * u), androidx.compose.ui.geometry.CornerRadius(3 * u, 3 * u))
                drawRect(acento, Offset(c.x - r * 0.8f, top), Size(r * 1.6f, 2 * u))
            }
            Sombrero.BICORNIO -> {
                drawPath(Path().apply { moveTo(c.x - r * 1.6f, top + 3 * u); quadraticTo(c.x, top - 16 * u, c.x + r * 1.6f, top + 3 * u); quadraticTo(c.x, top - 3 * u, c.x - r * 1.6f, top + 3 * u); close() }, Color(0xFF1E2230))
                drawCircle(acento, 2.6f * u, Offset(c.x + 3 * u, top - 4 * u))
            }
            Sombrero.SALACOT -> {
                drawArc(Color(0xFFE6D6AE), 180f, 180f, true, Offset(c.x - r * 1.5f, top - 5 * u), Size(r * 3f, r * 1.7f))
                drawRect(acento, Offset(c.x - r * 0.95f, top + 0.5f * u), Size(r * 1.9f, 2 * u))
            }
            Sombrero.GORRO_NAVIDAD -> {
                drawPath(Path().apply { moveTo(c.x - r, top + 3 * u); quadraticTo(c.x + 2 * u, top - 18 * u, c.x + r * 1.5f, top - 4 * u); lineTo(c.x + r, top + 3 * u); close() }, Color(0xFFD7263D))
                drawRoundRect(Color.White, Offset(c.x - r * 1.05f, top + 1 * u), Size(r * 2.1f, 5 * u), androidx.compose.ui.geometry.CornerRadius(2.5f * u, 2.5f * u))
                drawCircle(Color.White, 3 * u, Offset(c.x + r * 1.5f, top - 4 * u))
            }
            Sombrero.TURBANTE -> { // Turbante andalusí con su banda de tiraz: una franja bordada con trazos de escritura.
                drawOval(Color(0xFFF1E7D2), Offset(c.x - r * 1.1f, top - 8 * u), Size(r * 2.2f, r * 1.5f))
                drawArc(Color(0xFFD9CCB0), 200f, 140f, false, Offset(c.x - r, top - 6 * u), Size(r * 2f, r * 1.1f), style = Stroke(1.2f * u))
                drawRect(acento, Offset(c.x - r * 1.02f, top - 1.5f * u), Size(r * 2.04f, 3.4f * u))
                for (i in 0 until 7) { val x = c.x - r * 0.85f + i * r * 0.28f
                    drawLine(Color(0xFFF1E7D2), Offset(x, top + 1.2f * u), Offset(x, top - (if (i % 2 == 0) 0.6f else 0f) * u), 0.6f * u)
                    drawLine(Color(0xFFF1E7D2), Offset(x, top + 1.2f * u), Offset(x + r * 0.2f, top + 1.2f * u), 0.6f * u) }
                drawCircle(Color(0xFF2F6E8F), 1.8f * u, Offset(c.x, top - 4.5f * u))
            }
            Sombrero.CAPUCHA -> { // Capucha de copista con su ribete y la punta (liripipe) que cae a la espalda.
                drawPath(Path().apply { moveTo(c.x + r * 1.05f, top + 2 * u); quadraticTo(c.x + r * 1.7f, c.y, c.x + r * 1.5f, c.y + 16 * u) }, ropa, style = Stroke(3.5f * u, cap = StrokeCap.Round))
                drawArc(ropa, 150f, 240f, false, Offset(c.x - r * 1.3f, top - 4 * u), Size(r * 2.6f, r * 2.7f), style = Stroke(6 * u))
                drawArc(acento, 155f, 230f, false, Offset(c.x - r * 1.12f, top - 1.5f * u), Size(r * 2.24f, r * 2.4f), style = Stroke(1 * u))
            }
            Sombrero.ESCAFANDRA -> {
                drawCircle(Color(0xFFBFD7F2).copy(alpha = 0.35f), r * 1.45f, c)
                drawCircle(Color(0xFFE8ECF8), r * 1.45f, c, style = Stroke(2.4f * u))
                drawLine(Color.White.copy(alpha = 0.8f), Offset(c.x - r * 0.9f, c.y - r * 0.6f), Offset(c.x - r * 0.5f, c.y - r * 1.0f), 2 * u, StrokeCap.Round)
                drawLine(acento, Offset(c.x + r, top - 2 * u), Offset(c.x + r * 1.3f, top - 9 * u), 1.4f * u); drawCircle(acento, 1.8f * u, Offset(c.x + r * 1.3f, top - 9 * u))
            }
            Sombrero.PUNTA -> {
                drawPath(Path().apply { moveTo(c.x - r * 1.1f, top + 4 * u); lineTo(c.x, top - 14 * u); lineTo(c.x + r * 1.1f, top + 4 * u); close() }, acento)
                drawRoundRect(Color(0xFF8A6A44), Offset(c.x - r * 1.15f, top + 1 * u), Size(r * 2.3f, 5 * u), androidx.compose.ui.geometry.CornerRadius(2.5f * u, 2.5f * u))
            }
            Sombrero.BOINA -> { // Birrete de taller, caído hacia un lado, con su cinta y el lápiz de sanguina en la oreja.
                drawOval(acento, Offset(c.x - r * 1.05f, top - 6 * u), Size(r * 2.5f, r * 0.95f))
                drawOval(acento, Offset(c.x + r * 0.6f, top - 3 * u), Size(r * 0.9f, r * 0.7f))
                drawLine(tinta.copy(alpha = 0.55f), Offset(c.x - r * 0.95f, top + 0.5f * u), Offset(c.x + r * 0.95f, top + 0.5f * u), 1.4f * u)
                drawLine(acento, Offset(c.x - 1 * u, top - 6 * u), Offset(c.x, top - 9 * u), 1.4f * u)
                drawLine(Color(0xFFB0553A), Offset(c.x + r * 0.95f, c.y - 2 * u), Offset(c.x + r * 1.45f, c.y - 9 * u), 1.6f * u, StrokeCap.Round)
            }
            Sombrero.USHANKA -> {
                drawRoundRect(Color(0xFF6B5B4B), Offset(c.x - r * 1.15f, top - 6 * u), Size(r * 2.3f, 11 * u), androidx.compose.ui.geometry.CornerRadius(4 * u, 4 * u))
                drawRoundRect(Color(0xFF6B5B4B), Offset(c.x - r * 1.25f, c.y - 4 * u), Size(5 * u, 11 * u), androidx.compose.ui.geometry.CornerRadius(2 * u, 2 * u))
                drawRoundRect(Color(0xFF6B5B4B), Offset(c.x + r * 1.25f - 5 * u, c.y - 4 * u), Size(5 * u, 11 * u), androidx.compose.ui.geometry.CornerRadius(2 * u, 2 * u))
                drawCircle(acento, 2.4f * u, Offset(c.x, top - 1 * u))
            }
            Sombrero.GORRO_CARAVANA -> { // Gorro alto de fieltro del caravanero, ala negra vuelta y velo contra la arena a la espalda.
                drawPath(Path().apply { moveTo(c.x + r * 0.85f, top + 2 * u); lineTo(c.x + r * 1.4f, c.y + 12 * u); lineTo(c.x + r * 0.9f, c.y + 11 * u); close() }, acento.copy(alpha = 0.85f))
                drawPath(Path().apply {
                    moveTo(c.x - r * 0.95f, top + 3 * u); quadraticTo(c.x - r * 0.9f, top - 15 * u, c.x, top - 15 * u)
                    quadraticTo(c.x + r * 0.9f, top - 15 * u, c.x + r * 0.95f, top + 3 * u); close()
                }, Color(0xFFEFE6D2))
                drawLine(acento, Offset(c.x, top - 14 * u), Offset(c.x, top + 1 * u), 1 * u)
                drawLine(acento, Offset(c.x - r * 0.5f, top - 10 * u), Offset(c.x - r * 0.55f, top + 1 * u), 0.8f * u)
                drawLine(acento, Offset(c.x + r * 0.5f, top - 10 * u), Offset(c.x + r * 0.55f, top + 1 * u), 0.8f * u)
                drawPath(Path().apply { moveTo(c.x - r * 1.08f, top + 4 * u); lineTo(c.x - 2 * u, top - 1 * u); lineTo(c.x + 2 * u, top - 1 * u); lineTo(c.x + r * 1.08f, top + 4 * u); lineTo(c.x + r * 1.0f, top + 1 * u); lineTo(c.x - r * 1.0f, top + 1 * u); close() }, Color(0xFF2B2420))
            }
            Sombrero.TONSURA -> { // Tonsura de monje: la coronilla rapada, el cerco de pelo y la capucha bajada sobre los hombros.
                drawArc(Color(0xFF5B4632), 180f, 180f, false, Offset(c.x - r * 0.98f, c.y - r * 0.72f), Size(r * 1.96f, r * 1.1f), style = Stroke(3.6f * u, cap = StrokeCap.Round))
                drawLine(Color(0xFF5B4632), Offset(c.x - r * 0.98f, c.y - r * 0.17f), Offset(c.x - r * 0.95f, c.y + 2 * u), 3 * u, StrokeCap.Round)
                drawLine(Color(0xFF5B4632), Offset(c.x + r * 0.98f, c.y - r * 0.17f), Offset(c.x + r * 0.95f, c.y + 2 * u), 3 * u, StrokeCap.Round)
                drawArc(Color.White.copy(alpha = 0.45f), 200f, 60f, false, Offset(c.x - r * 0.6f, top + 1.5f * u), Size(r * 1.2f, r * 0.8f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
                drawArc(ropa, 20f, 140f, false, Offset(c.x - r * 1.2f, c.y + r * 0.35f), Size(r * 2.4f, r * 1.1f), style = Stroke(4 * u, cap = StrokeCap.Round))
            }
            Sombrero.FRIGIO -> { // Gorro frigio rojo con la punta caída hacia delante y su escarapela.
                drawPath(Path().apply {
                    moveTo(c.x - r * 1.1f, top + 4 * u); quadraticTo(c.x - r * 1.2f, top - 10 * u, c.x + 2 * u, top - 13 * u)
                    quadraticTo(c.x + r * 1.5f, top - 13 * u, c.x + r * 1.35f, top - 2 * u); quadraticTo(c.x + r * 0.9f, top - 6 * u, c.x + r * 1.05f, top + 4 * u); close()
                }, Color(0xFFB22234))
                drawRect(Color(0xFFF1E7D2), Offset(c.x - r * 1.12f, top + 1.5f * u), Size(r * 2.2f, 3 * u))
                drawCircle(Color.White, 3.2f * u, Offset(c.x - r * 0.55f, top - 1 * u)); drawCircle(Color(0xFFB22234), 3 * u, Offset(c.x - r * 0.55f, top - 1 * u), style = Stroke(1.2f * u))
                drawCircle(Color(0xFF1F3A6E), 1.1f * u, Offset(c.x - r * 0.55f, top - 1 * u))
            }
        }
    }

    private fun DrawScope.objeto(o: Objeto, m: Offset, u: Float, acento: Color, tinta: Color, ropa: Color, fondo: Color) {
        when (o) {
            Objeto.LANZA -> { drawLine(Color(0xFF8A6A44), m + Offset(0f, 12 * u), m - Offset(0f, 46 * u), 2 * u); drawPath(Path().apply { moveTo(m.x, m.y - 54 * u); lineTo(m.x + 3 * u, m.y - 45 * u); lineTo(m.x - 3 * u, m.y - 45 * u); close() }, Color(0xFFB0B7BE)) }
            Objeto.ESCUDO -> { drawCircle(acento, 11 * u, m + Offset(4 * u, 0f)); drawCircle(Color(0xFFC9A04A), 11 * u, m + Offset(4 * u, 0f), style = Stroke(2 * u)); drawCircle(Color(0xFFC9A04A), 3 * u, m + Offset(4 * u, 0f)) }
            Objeto.ESPADA -> { drawLine(Color(0xFFC9CED3), m, m - Offset(-2 * u, 34 * u), 2.6f * u, StrokeCap.Round); drawLine(acento, m - Offset(5 * u, 3 * u), m + Offset(5 * u, -3 * u), 2.4f * u) }
            Objeto.CARPETA -> { drawRect(Color(0xFFD9C9A0), m - Offset(4 * u, 10 * u), Size(14 * u, 18 * u)); drawLine(tinta, m - Offset(1 * u, 5 * u), m + Offset(7 * u, -5 * u), 1 * u); drawLine(tinta, m - Offset(1 * u, 1 * u), m + Offset(7 * u, -1 * u), 1 * u); drawRect(acento, m + Offset(2 * u, 4 * u), Size(6 * u, 2 * u)) }
            Objeto.LLAVE -> { drawLine(Color(0xFFB0B7BE), m, m - Offset(-6 * u, 20 * u), 2.4f * u, StrokeCap.Round); drawCircle(Color(0xFFB0B7BE), 4 * u, m - Offset(-6 * u, 22 * u), style = Stroke(2 * u)) }
            Objeto.TABLETA -> { // Terminal de mano: pantalla de fósforo con líneas de texto, el cursor y un teclado.
                drawRoundRect(tinta, m - Offset(2 * u, 12 * u), Size(12 * u, 19 * u), androidx.compose.ui.geometry.CornerRadius(2 * u, 2 * u))
                drawRect(acento.copy(alpha = 0.22f), m - Offset(0.5f * u, 10.5f * u), Size(9 * u, 9 * u))
                listOf(-8.6f to 6f, -6.6f to 4f, -4.6f to 5f).forEach { (y, w) -> drawLine(acento, m + Offset(0.6f * u, y * u), m + Offset((0.6f + w) * u, y * u), 0.8f * u) }
                drawRect(acento, m + Offset(0.6f * u, -3.4f * u), Size(1.6f * u, 1.2f * u))
                for (i in 0 until 3) for (j in 0 until 2) drawRect(acento.copy(alpha = 0.6f), m + Offset((0.2f + i * 3f) * u, (0.4f + j * 2.6f) * u), Size(2.2f * u, 1.6f * u))
            }
            Objeto.LIBRO -> { // Libro con la tapa de lazo: una estrella de ocho puntas (dos cuadrados girados).
                drawRect(acento, m - Offset(4 * u, 8 * u), Size(13 * u, 16 * u)); drawLine(Color(0xFFF1E7D2), m - Offset(2 * u, -6 * u), m + Offset(8 * u, 6 * u), 1.6f * u)
                val cc = m + Offset(2.5f * u, -1.5f * u)
                drawRect(Color(0xFFE3B748), cc - Offset(2.6f * u, 2.6f * u), Size(5.2f * u, 5.2f * u), style = Stroke(0.8f * u))
                rotate(45f, cc) { drawRect(Color(0xFFE3B748), cc - Offset(2.6f * u, 2.6f * u), Size(5.2f * u, 5.2f * u), style = Stroke(0.8f * u)) }
            }
            Objeto.MATRAZ -> { drawPath(Path().apply { moveTo(m.x + 2 * u, m.y - 14 * u); lineTo(m.x + 6 * u, m.y - 14 * u); lineTo(m.x + 6 * u, m.y - 8 * u); lineTo(m.x + 12 * u, m.y + 4 * u); lineTo(m.x - 4 * u, m.y + 4 * u); lineTo(m.x + 2 * u, m.y - 8 * u); close() }, Color(0xFFDDEEF5).copy(alpha = 0.7f))
                drawPath(Path().apply { moveTo(m.x - 2 * u, m.y); lineTo(m.x + 10 * u, m.y); lineTo(m.x + 12 * u, m.y + 4 * u); lineTo(m.x - 4 * u, m.y + 4 * u); close() }, acento) }
            Objeto.PALA -> { drawLine(Color(0xFF8A6A44), m + Offset(0f, 10 * u), m - Offset(0f, 30 * u), 2 * u); drawRoundRect(Color(0xFF9AA2AA), m + Offset(-4 * u, 10 * u), Size(8 * u, 9 * u), androidx.compose.ui.geometry.CornerRadius(2 * u, 2 * u)) }
            Objeto.CATALEJO -> rotate(-35f, m) { drawRoundRect(Color(0xFFC9A04A), m - Offset(0f, 2.5f * u), Size(20 * u, 5 * u), androidx.compose.ui.geometry.CornerRadius(1 * u, 1 * u)); drawRect(Color(0xFF8C6E2F), m + Offset(6 * u, -3 * u), Size(2 * u, 6 * u)) }
            Objeto.LUPA -> { drawLine(Color(0xFF8A6A44), m, m - Offset(-4 * u, 10 * u), 2.4f * u, StrokeCap.Round); drawCircle(Color(0xFFBFD7F2).copy(alpha = 0.5f), 6 * u, m - Offset(-8 * u, 16 * u)); drawCircle(acento, 6 * u, m - Offset(-8 * u, 16 * u), style = Stroke(1.8f * u)) }
            Objeto.REGALO -> { drawRect(Color(0xFF2E7D4F), m - Offset(5 * u, 6 * u), Size(14 * u, 12 * u)); drawLine(Color(0xFFF2C94C), m - Offset(-2 * u, 6 * u), m + Offset(2 * u, 6 * u), 2 * u); drawLine(Color(0xFFF2C94C), m - Offset(5 * u, 0f), m + Offset(9 * u, 0f), 2 * u) }
            Objeto.FAROL -> { drawLine(tinta, m, m + Offset(0f, 6 * u), 1 * u); drawRoundRect(Color(0xFFD7263D), m + Offset(-5 * u, 6 * u), Size(10 * u, 13 * u), androidx.compose.ui.geometry.CornerRadius(5 * u, 5 * u)); drawCircle(Color(0xFFF2C14E).copy(alpha = 0.6f), 3 * u, m + Offset(0f, 12.5f * u)) }
            Objeto.VELA -> { // Cirio en su palmatoria, con la cera corrida y el halo de la llama.
                drawCircle(Color(0xFFF2C14E).copy(alpha = 0.22f), 7 * u, m - Offset(-0.5f * u, 17 * u))
                drawRect(Color(0xFFF1E7D2), m - Offset(2 * u, 14 * u), Size(5 * u, 14 * u)); drawOval(Color(0xFFF2C14E), m - Offset(1.5f * u, 20 * u), Size(4 * u, 6 * u))
                drawLine(Color(0xFFE6DCC4), m - Offset(-2.4f * u, 13 * u), m - Offset(-2.4f * u, 9 * u), 1.4f * u, StrokeCap.Round)
                drawOval(Color(0xFFC9A04A), m - Offset(5 * u, 0.5f * u), Size(11 * u, 3 * u)); drawLine(Color(0xFFC9A04A), m + Offset(5.5f * u, 1 * u), m + Offset(8 * u, -1 * u), 1.2f * u)
            }
            Objeto.BANDERA -> { drawLine(Color(0xFFC9CED3), m + Offset(0f, 10 * u), m - Offset(0f, 34 * u), 1.6f * u); drawRect(acento, m - Offset(0f, 34 * u), Size(14 * u, 9 * u)); drawCircle(fondo, 2 * u, m - Offset(-7 * u, 29.5f * u)) }
            Objeto.ARCO -> { drawArc(Color(0xFF8A6A44), -80f, 160f, false, Offset(m.x - 6 * u, m.y - 22 * u), Size(14 * u, 44 * u), style = Stroke(2 * u)); drawLine(tinta, Offset(m.x + 1 * u, m.y - 21 * u), Offset(m.x + 1 * u, m.y + 21 * u), 0.8f * u) }
            Objeto.PINCEL -> { drawLine(Color(0xFF8A6A44), m + Offset(-2 * u, 6 * u), m - Offset(-6 * u, 20 * u), 2 * u, StrokeCap.Round); drawCircle(acento, 2.6f * u, m - Offset(-7 * u, 22 * u)) }
            Objeto.MARTILLO -> { drawLine(Color(0xFF8A6A44), m + Offset(0f, 8 * u), m - Offset(0f, 18 * u), 2.4f * u); drawRect(Color(0xFF8E979F), m - Offset(6 * u, 22 * u), Size(12 * u, 5 * u)) }
            Objeto.TRICOLOR -> {
                drawLine(Color(0xFF8A6A44), m + Offset(0f, 10 * u), m - Offset(0f, 34 * u), 1.6f * u)
                listOf(Color(0xFF1F3A6E), Color.White, Color(0xFFB22234)).forEachIndexed { i, col -> drawRect(col, m - Offset(-i * 5 * u, 34 * u), Size(5 * u, 11 * u)) }
                drawCircle(Color(0xFFC9A04A), 1.4f * u, m - Offset(0f, 35 * u))
            }
            Objeto.BACULO -> { // Báculo de caminante: vara larga, nudo en lo alto con su gema y runas que brillan.
                drawLine(Color(0xFF7A5C38), m + Offset(0f, 14 * u), m - Offset(0f, 48 * u), 2.4f * u, StrokeCap.Round)
                drawCircle(Color(0xFF7A5C38), 3.6f * u, m - Offset(0f, 52 * u), style = Stroke(1.6f * u))
                drawCircle(acento, 2.2f * u, m - Offset(0f, 52 * u))
                listOf(30f, 22f).forEach { y -> drawLine(acento, m - Offset(1.2f * u, y * u), m - Offset(-1.2f * u, (y - 2.4f) * u), 0.9f * u) }
            }
            Objeto.PERGAMINO -> { drawRect(Color(0xFFEDDFC2), m - Offset(3 * u, 12 * u), Size(10 * u, 20 * u)); drawCircle(Color(0xFFD5C29A), 2.2f * u, m - Offset(-2 * u, 12 * u)); drawCircle(Color(0xFFD5C29A), 2.2f * u, m + Offset(2 * u, 8 * u)); drawLine(acento, m - Offset(1 * u, 6 * u), m + Offset(5 * u, -6 * u), 1 * u) }
        }
    }
}

/** El personaje dibujado, con su pequeña animación (respira y parpadea) si las animaciones están encendidas. */
@Composable
fun FiguraPersonaje(e: Personaje.Estado, modifier: Modifier = Modifier, tam: Dp = 96.dp, tema: TemaId = Estilo.tema) {
    val cs = MaterialTheme.colorScheme
    val animar = Repo.config.animaciones
    val trans = rememberInfiniteTransition(label = "personaje")
    val fase = trans.animateFloat(0f, 1f, infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Restart), label = "fase")
    val t = if (animar) fase.value else 0.25f
    val parpadeo = animar && t in 0.48f..0.52f
    val piel = if (esOscuro()) Color(0xFFE2BFA0) else Color(0xFFD9AE8A)
    Canvas(modifier.size(width = tam * 0.85f, height = tam)) {
        with(Personaje) { dibujar(tema, e, cs.primary, cs.tertiary, piel, cs.onSurface, cs.surface, t, parpadeo) }
    }
}

/** En Día: el personaje con lo que te dice hoy, en su globo de diálogo. Se apaga en Apariencia. */
@Composable
fun TarjetaPersonaje(hoy: String, modifier: Modifier = Modifier) {
    if (!Repo.config.personaje) return
    val tema = Estilo.tema
    val e = androidx.compose.runtime.remember(Repo.dias, Repo.salud, Repo.entorno, Repo.momentos, hoy, tema) { Personaje.estado(hoy, tema) }
    val cs = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        FiguraPersonaje(e, tam = 92.dp, tema = tema)
        Box(
            Modifier.weight(1f).padding(start = 2.dp)
                .drawBehind { globoPersonaje(tema, cs.surface, cs.outline, cs.primary, cs.tertiary, cs.onSurface) }
                .padding(start = 26.dp, top = 18.dp, end = 18.dp, bottom = 14.dp),
        ) {
            Column {
                Text(e.frase, style = MaterialTheme.typography.bodyMedium, color = cs.onSurface)
                val extra = listOfNotNull(
                    if (e.racha >= 2) "racha ${e.racha}" else null, e.animo?.let { "ánimo ${"%.0f".format(it)}/7" },
                    Repo.entorno[hoy]?.t_max?.let { "${it.toInt()}°" },
                ).joinToString(" · ")
                if (extra.isNotEmpty()) Text(extra, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

/**
 * El globo de diálogo de cada tema, con la cola (o lo que hace de cola) hacia el personaje, a la izquierda: tabula ansata,
 * ventana de consola, óstrakon, filacteria, carpeta con clip, pantalla redonda, placa remachada, portapapeles, recorte de
 * gaceta, boletín con lacre, etiqueta de herbario, bocadillo nevado, rollo colgante, cartela con arquería, visor, cartela
 * lobulada, jadag con flecos, cartellino con trazos de construcción, paralelogramo con cuña, cartel tricolor y piedra rúnica.
 * El texto va dentro de un relleno fijo (26 dp a la izquierda, 18 arriba y a la derecha, 14 abajo) que cabe en todos.
 */
private fun DrawScope.globoPersonaje(t: TemaId, fondo: Color, linea: Color, primario: Color, acento: Color, tinta: Color) {
    val u = 1.dp.toPx()
    val w = size.width; val h = size.height; val x0 = 10 * u; val cy = h / 2
    val fino = Stroke(1.2f * u)
    fun cola(alto: Float = 6f) = Path().apply { moveTo(x0 + 2 * u, cy - alto * u); lineTo(0f, cy + 2 * u); lineTo(x0 + 2 * u, cy + alto * u); close() }
    fun caja(l: Float, tp: Float, r: Float, b: Float) = Path().apply { addRect(Rect(l, tp, r, b)) }
    fun unir(a: Path, b: Path) = Path.combine(PathOperation.Union, a, b)
    fun pintar(p: Path, borde: Color = linea, grosor: Float = 1.2f) { drawPath(p, fondo); drawPath(p, borde, style = Stroke(grosor * u, join = StrokeJoin.Round)) }
    when (t) {
        TemaId.ROMANO -> { // Tabula ansata: las asas en cola de milano hacen de cola.
            val l = x0 + 8 * u; val r = w - 13 * u
            val p = caja(l, 2 * u, r, h - 2 * u)
            listOf(l to -1f, r to 1f).forEach { (x, d) ->
                p.moveTo(x, cy - 6 * u); p.lineTo(x + d * 12 * u, cy - 12 * u); p.lineTo(x + d * 12 * u, cy + 12 * u); p.lineTo(x, cy + 6 * u); p.close()
            }
            pintar(p, acento, 1.4f)
            drawRect(acento.copy(alpha = 0.6f), Offset(l + 3 * u, 5 * u), Size(r - l - 6 * u, h - 10 * u), style = Stroke(0.7f * u))
        }
        TemaId.CIBER -> { // Ventana de consola: barra de título, tres botones cuadrados y un cable de puntos hasta el personaje.
            drawRect(fondo, Offset(x0, 0f), Size(w - x0, h)); drawRect(primario, Offset(x0, 0f), Size(w - x0, h), style = fino)
            drawRect(primario.copy(alpha = 0.25f), Offset(x0, 0f), Size(w - x0, 9 * u))
            for (k in 1..3) drawRect(primario, Offset(w - (k * 6.5f) * u, 2.5f * u), Size(4 * u, 4 * u), style = Stroke(0.8f * u))
            drawLine(primario, Offset(0f, cy), Offset(x0, cy), 1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u)))
            drawRect(primario.copy(alpha = 0.7f), Offset(w - 10 * u, h - 6 * u), Size(5 * u, 1.6f * u))
        }
        TemaId.HELENO -> { // Óstrakon: un tiesto de borde irregular, con el labio de la vasija.
            val p = Path().apply {
                moveTo(x0 + 2 * u, 4 * u); lineTo(w * 0.42f, 0f); lineTo(w - 6 * u, 3 * u); lineTo(w, h * 0.45f); lineTo(w - 3 * u, h - 2 * u); lineTo(w * 0.55f, h)
                lineTo(x0 + 5 * u, h - 4 * u); lineTo(x0 + 1 * u, cy + 6 * u); lineTo(0f, cy + 2 * u); lineTo(x0, cy - 4 * u); close()
            }
            pintar(p)
            drawPath(Path().apply { moveTo(x0 + 7 * u, 8 * u); quadraticTo(w * 0.45f, 3 * u, w - 9 * u, 7 * u) }, acento.copy(alpha = 0.7f), style = Stroke(0.9f * u))
        }
        TemaId.MEDIEVAL -> { // Filacteria: la cinta de habla de los manuscritos, enrollada a los dos lados.
            pintar(unir(caja(x0 + 4 * u, 3 * u, w - 4 * u, h - 3 * u), cola()))
            listOf(x0, w - 8 * u).forEach { x ->
                drawRoundRect(acento.copy(alpha = 0.3f), Offset(x, 0f), Size(8 * u, h), CornerRadius(4 * u, 4 * u))
                drawRoundRect(linea, Offset(x, 0f), Size(8 * u, h), CornerRadius(4 * u, 4 * u), style = fino)
                drawLine(linea, Offset(x + 4 * u, 3 * u), Offset(x + 4 * u, h - 3 * u), 0.6f * u)
            }
        }
        TemaId.DISTOPIA -> { // Carpeta de expediente con su pestaña y un clip.
            pintar(unir(unir(caja(x0, 6 * u, w, h), caja(x0 + 6 * u, 0f, x0 + 44 * u, 7 * u)), cola()))
            drawRoundRect(tinta.copy(alpha = 0.7f), Offset(w - 18 * u, -3 * u), Size(6 * u, 16 * u), CornerRadius(3 * u, 3 * u), style = Stroke(1 * u))
            drawRoundRect(tinta.copy(alpha = 0.7f), Offset(w - 16.5f * u, 0f), Size(3 * u, 10 * u), CornerRadius(1.5f * u, 1.5f * u), style = Stroke(0.8f * u))
        }
        TemaId.REFUGIO -> { // Pantalla redondeada, con su doble marco y el cable que llega al personaje.
            pintar(Path().apply { addRoundRect(RoundRect(x0, 0f, w, h, CornerRadius(14 * u, 14 * u))) }, primario, 1.4f)
            drawRoundRect(primario.copy(alpha = 0.4f), Offset(x0 + 3 * u, 3 * u), Size(w - x0 - 6 * u, h - 6 * u), CornerRadius(11 * u, 11 * u), style = Stroke(0.8f * u))
            drawPath(Path().apply { moveTo(x0, cy); quadraticTo(4 * u, cy, 3 * u, cy + 8 * u) }, primario, style = Stroke(1.4f * u))
            drawCircle(primario, 2 * u, Offset(3 * u, cy + 9 * u))
        }
        TemaId.ORDEN -> { // Placa de acero con las esquinas cortadas y cuatro remaches.
            val k = 6 * u
            val p = Path().apply { moveTo(x0 + k, 0f); lineTo(w - k, 0f); lineTo(w, k); lineTo(w, h - k); lineTo(w - k, h); lineTo(x0 + k, h); lineTo(x0, h - k); lineTo(x0, k); close() }
            pintar(unir(p, cola()), linea, 1.6f)
            listOf(Offset(x0 + 6 * u, 6 * u), Offset(w - 6 * u, 6 * u), Offset(x0 + 6 * u, h - 6 * u), Offset(w - 6 * u, h - 6 * u)).forEach { drawCircle(primario, 1.4f * u, it) }
        }
        TemaId.LABORATORIO -> { // Portapapeles con su pinza.
            pintar(unir(Path().apply { addRoundRect(RoundRect(x0, 4 * u, w, h, CornerRadius(3 * u, 3 * u))) }, cola()))
            val mx = (x0 + w) / 2
            drawRoundRect(primario, Offset(mx - 14 * u, 0f), Size(28 * u, 8 * u), CornerRadius(2 * u, 2 * u))
            drawCircle(fondo, 1.6f * u, Offset(mx, 3.5f * u))
        }
        TemaId.COLONOS -> { // Recorte de gaceta: bordes de tijera arriba y abajo y el filete de la cabecera.
            val p = Path().apply {
                moveTo(x0, 3 * u); var x = x0; var arriba = true
                while (x < w) { x = minOf(x + 4 * u, w); lineTo(x, if (arriba) 0f else 3 * u); arriba = !arriba }
                lineTo(w, h - 3 * u); arriba = true
                while (x > x0) { x = maxOf(x - 4 * u, x0); lineTo(x, if (arriba) h else h - 3 * u); arriba = !arriba }
                close()
            }
            pintar(unir(p, cola()))
            drawLine(linea, Offset(x0 + 4 * u, 8 * u), Offset(w - 4 * u, 8 * u), 0.6f * u); drawLine(linea, Offset(x0 + 4 * u, 9.6f * u), Offset(w - 4 * u, 9.6f * u), 0.6f * u)
        }
        TemaId.NAPOLEON -> { // Boletín con doble filete dorado y un lacre en la esquina.
            pintar(unir(caja(x0, 0f, w, h), cola()))
            drawRect(acento, Offset(x0 + 2.5f * u, 2.5f * u), Size(w - x0 - 5 * u, h - 5 * u), style = Stroke(0.7f * u))
            val sello = Offset(x0 + 3 * u, h - 6 * u)
            drawCircle(Color(0xFFA3262A), 6 * u, sello); drawCircle(Color(0xFF7A1A1E), 3.6f * u, sello, style = Stroke(0.9f * u))
        }
        TemaId.BOTANICO -> { // Etiqueta de herbario, de doble filete, prendida con un alfiler y con una ramita.
            pintar(unir(caja(x0, 0f, w, h), cola()))
            drawRect(linea, Offset(x0 + 3 * u, 3 * u), Size(w - x0 - 6 * u, h - 6 * u), style = Stroke(0.6f * u))
            drawLine(tinta.copy(alpha = 0.7f), Offset(x0 + 8 * u, 4 * u), Offset(x0 + 15 * u, 11 * u), 0.9f * u)
            drawCircle(acento, 2.4f * u, Offset(x0 + 8 * u, 4 * u))
            val r0 = Offset(w - 12 * u, h - 6 * u)
            drawLine(primario, r0, r0 + Offset(8 * u, -4 * u), 0.9f * u)
            drawOval(primario, r0 + Offset(2 * u, -4.5f * u), Size(3.2f * u, 2f * u)); drawOval(primario, r0 + Offset(4.6f * u, -2 * u), Size(3.2f * u, 2f * u))
        }
        TemaId.NAVIDAD -> { // Bocadillo con nieve en el borde de arriba y una rama de acebo.
            pintar(unir(Path().apply { addRoundRect(RoundRect(x0, 4 * u, w, h, CornerRadius(12 * u, 12 * u))) }, cola()), primario, 1.4f)
            var x = x0 + 10 * u
            while (x < w - 8 * u) { drawCircle(Color.White, 3.4f * u, Offset(x, 5 * u)); x += 7 * u }
            drawRect(Color.White, Offset(x0 + 10 * u, 4 * u), Size(maxOf(0f, x - 7 * u - x0 - 10 * u), 3 * u))
            val ac = Offset(w - 8 * u, 6 * u)
            drawOval(Color(0xFF2E7D4F), ac + Offset(-9 * u, -2 * u), Size(7 * u, 4 * u)); drawOval(Color(0xFF2E7D4F), ac + Offset(-3 * u, 0f), Size(7 * u, 4 * u))
            drawCircle(Color(0xFFD7263D), 1.8f * u, ac + Offset(-3 * u, 0f)); drawCircle(Color(0xFFD7263D), 1.6f * u, ac + Offset(-0.6f * u, -1.4f * u))
        }
        TemaId.SEDA -> { // Rollo colgante: varillas arriba y abajo, con sus pomos, y un sello de cinabrio.
            pintar(caja(x0 + 4 * u, 4 * u, w - 4 * u, h - 4 * u))
            listOf(2.5f * u, h - 6.5f * u).forEach { y ->
                drawRoundRect(acento, Offset(x0, y), Size(w - x0, 4 * u), CornerRadius(2 * u, 2 * u))
                drawCircle(primario, 2.6f * u, Offset(x0, y + 2 * u)); drawCircle(primario, 2.6f * u, Offset(w - 1 * u, y + 2 * u))
            }
            drawRect(Color(0xFFB0302A), Offset(w - 14 * u, h - 16 * u), Size(6 * u, 6 * u))
        }
        TemaId.GOTICO -> { // Cartela con una arquería de arcos apuntados en lo alto.
            pintar(unir(caja(x0, 0f, w, h), cola()))
            val y = 10 * u; var x = x0 + 3 * u
            drawLine(acento, Offset(x0 + 3 * u, y), Offset(w - 3 * u, y), 0.8f * u)
            while (x + 10 * u <= w - 3 * u) {
                drawPath(Path().apply { moveTo(x, y); quadraticTo(x, y - 5 * u, x + 5 * u, y - 7 * u); quadraticTo(x + 10 * u, y - 5 * u, x + 10 * u, y) }, acento, style = Stroke(0.8f * u))
                x += 10 * u
            }
        }
        TemaId.ESPACIO -> { // Visor de cabina: esquinas, un filete tenue y ondas de radio hacia el personaje.
            drawRect(fondo.copy(alpha = 0.9f), Offset(x0, 0f), Size(w - x0, h)); drawRect(linea.copy(alpha = 0.5f), Offset(x0, 0f), Size(w - x0, h), style = Stroke(0.6f * u))
            val l = 9 * u
            listOf(Offset(x0, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(x0, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, d) ->
                drawLine(primario, o, Offset(o.x + d.x * l, o.y), 1.8f * u); drawLine(primario, o, Offset(o.x, o.y + d.y * l), 1.8f * u)
            }
            listOf(3f, 6f, 9f).forEach { rr -> drawArc(primario.copy(alpha = 0.8f), 150f, 60f, false, Offset(x0 - rr * u, cy - rr * u), Size(2 * rr * u, 2 * rr * u), style = Stroke(1 * u)) }
        }
        TemaId.ANDALUS -> { // Cartela polilobulada, con un filete dorado por dentro.
            val rl = 6 * u; val n = maxOf(2, (h / (2 * rl)).toInt())
            var p = unir(caja(x0 + 6 * u, 0f, w - 6 * u, h), cola())
            for (k in 0 until n) {
                val y = h * (k + 0.5f) / n
                p = unir(p, Path().apply { addOval(Rect(Offset(x0 + 6 * u, y), rl)) })
                p = unir(p, Path().apply { addOval(Rect(Offset(w - 6 * u, y), rl)) })
            }
            pintar(p, acento, 1.3f)
            drawRect(acento.copy(alpha = 0.6f), Offset(x0 + 9 * u, 3 * u), Size(w - x0 - 18 * u, h - 6 * u), style = Stroke(0.6f * u))
        }
        TemaId.MONGOL -> { // Jadag: la bufanda de seda con flecos a la derecha y una fila de rombos tejidos.
            pintar(unir(caja(x0, 0f, w - 8 * u, h), cola()), primario)
            var y = 2 * u
            while (y < h - 1 * u) { drawLine(primario, Offset(w - 8 * u, y), Offset(w, y + 0.6f * u), 0.8f * u); y += 3 * u }
            var x = x0 + 6 * u
            while (x < w - 14 * u) {
                drawPath(Path().apply { moveTo(x, h - 5 * u); lineTo(x + 2 * u, h - 7 * u); lineTo(x + 4 * u, h - 5 * u); lineTo(x + 2 * u, h - 3 * u); close() }, acento.copy(alpha = 0.45f))
                x += 7 * u
            }
        }
        TemaId.RENACIMIENTO -> { // Cartellino con los trazos de construcción que se pasan de las esquinas y una cola de pluma.
            pintar(caja(x0 + 4 * u, 4 * u, w - 4 * u, h - 4 * u), linea, 0.8f)
            val sang = acento.copy(alpha = 0.55f)
            listOf(4 * u, h - 4 * u).forEach { y -> drawLine(sang, Offset(x0, y), Offset(w, y), 0.6f * u) }
            listOf(x0 + 4 * u, w - 4 * u).forEach { x -> drawLine(sang, Offset(x, 0f), Offset(x, h), 0.6f * u) }
            drawPath(Path().apply { moveTo(w - 4 * u, h - 12 * u); lineTo(w - 12 * u, h - 4 * u); lineTo(w - 10 * u, h - 10 * u); close() }, linea.copy(alpha = 0.5f))
            val pluma = Path().apply { moveTo(x0 + 4 * u, cy - 5 * u); quadraticTo(4 * u, cy - 4 * u, 0f, cy + 7 * u); quadraticTo(6 * u, cy + 2 * u, x0 + 4 * u, cy + 3 * u); close() }
            drawPath(pluma, fondo); drawPath(pluma, linea, style = Stroke(0.8f * u))
            drawLine(linea, Offset(x0 + 4 * u, cy - 1 * u), Offset(0f, cy + 7 * u), 0.6f * u)
        }
        TemaId.URSS -> { // Paralelogramo constructivista, una barra gruesa arriba y la cuña roja hacia el personaje.
            val p = Path().apply { moveTo(x0 + 10 * u, 0f); lineTo(w, 0f); lineTo(w - 10 * u, h); lineTo(x0, h); close() }
            pintar(p, primario, 1.6f)
            drawLine(primario, Offset(x0 + 10 * u, 1.5f * u), Offset(x0 + 50 * u, 1.5f * u), 3 * u)
            drawPath(Path().apply { moveTo(x0 + 6 * u, cy - 6 * u); lineTo(x0 + 9 * u, cy + 2 * u); lineTo(0f, cy + 10 * u); close() }, primario)
        }
        TemaId.REVOLUCION -> { // Cartel pegado: la franja tricolor a la izquierda y dos clavos arriba.
            pintar(unir(caja(x0, 0f, w, h), cola()))
            listOf(Color(0xFF1F3A6E), Color.White, Color(0xFFB22234)).forEachIndexed { k, col ->
                drawRect(col, Offset(x0 + (2 + k * 2.5f) * u, 3 * u), Size(2.5f * u, h - 6 * u))
            }
            drawRect(linea, Offset(x0 + 2 * u, 3 * u), Size(7.5f * u, h - 6 * u), style = Stroke(0.5f * u))
            drawCircle(tinta, 1.4f * u, Offset(x0 + 14 * u, 3.5f * u)); drawCircle(tinta, 1.4f * u, Offset(w - 4 * u, 3.5f * u))
        }
        TemaId.ANTIGUAS -> { // Piedra rúnica de borde irregular, con unas runas talladas y dos guijarros de pensamiento.
            val p = Path().apply {
                moveTo(x0 + 4 * u, 6 * u); lineTo(x0 + 14 * u, 0f); lineTo(w - 12 * u, 1 * u); lineTo(w, 8 * u); lineTo(w - 2 * u, h - 8 * u)
                lineTo(w - 14 * u, h); lineTo(x0 + 10 * u, h - 1 * u); lineTo(x0, h - 9 * u); close()
            }
            pintar(p, linea, 1.4f)
            for (k in 0 until 3) {
                val o = Offset(w - (26 - k * 6) * u, 4 * u)
                drawLine(acento, o, o + Offset(0f, 6 * u), 0.9f * u); drawLine(acento, o + Offset(0f, (1 + k) * u), o + Offset(2.2f * u, (2.6f + k) * u), 0.9f * u)
            }
            drawCircle(fondo, 2.4f * u, Offset(6 * u, cy + 4 * u)); drawCircle(linea, 2.4f * u, Offset(6 * u, cy + 4 * u), style = Stroke(1 * u))
            drawCircle(fondo, 1.5f * u, Offset(2 * u, cy + 10 * u)); drawCircle(linea, 1.5f * u, Offset(2 * u, cy + 10 * u), style = Stroke(0.8f * u))
        }
    }
}
