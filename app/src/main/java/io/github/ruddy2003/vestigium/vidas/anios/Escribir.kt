package io.github.ruddy2003.vestigium.vidas.anios

import io.github.ruddy2003.vestigium.vidas.CertezaDia
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.DiaEspecial
import io.github.ruddy2003.vestigium.vidas.PuntoArco
import io.github.ruddy2003.vestigium.vidas.TipoDia

/*
 * Atajos para escribir calendarios. Los usan `AnioPiloto.kt` y los `Anio<Grupo>.kt` de las tandas A, para que todos
 * los calendarios se lean igual: un renglón por día especial, con la fecha delante.
 */

/** Una fuente: su título y su enlace. Solo se escribe si la página se abrió y se comprobó en la sesión. */
internal typealias Fuente = Pair<String, String>

/** Una página de Wikipedia; [idioma] es «en», «es» o «fr». */
internal fun wiki(titulo: String, pagina: String, idioma: String = "en"): Fuente = titulo to "https://$idioma.wikipedia.org/wiki/$pagina"

internal val HISTORICO = CertezaDia.HISTORICO
internal val APROXIMADO = CertezaDia.APROXIMADO
internal val INVENTADO = CertezaDia.INVENTADO

/** Una fiesta: vuelve cada año con el mismo [id] (y así comparte su texto), aunque cambie de fecha. */
internal fun fiesta(id: String, fecha: String, titulo: String, dias: Int = 1, certeza: CertezaDia, fuente: Fuente? = null, escena: String = "fiesta", clima: Clima? = null) =
    DiaEspecial(id, fecha, dias, TipoDia.FIESTA, titulo, escena, certeza, fuente, null, clima)

/** Una cosecha o una faena del campo: suele durar varios días. */
internal fun cosecha(id: String, fecha: String, titulo: String, dias: Int, certeza: CertezaDia, fuente: Fuente? = null, escena: String = "cosecha") =
    DiaEspecial(id, fecha, dias, TipoDia.COSECHA, titulo, escena, certeza, fuente)

/** Un hecho de guerra. */
internal fun guerra(id: String, fecha: String, titulo: String, dias: Int = 1, certeza: CertezaDia, fuente: Fuente? = null, escena: String = "", clima: Clima? = null) =
    DiaEspecial(id, fecha, dias, TipoDia.GUERRA, titulo, escena, certeza, fuente, null, clima)

/** Un rito: un entierro, una procesión que no es fiesta, una primera piedra. */
internal fun rito(id: String, fecha: String, titulo: String, dias: Int = 1, certeza: CertezaDia, fuente: Fuente? = null, escena: String = "") =
    DiaEspecial(id, fecha, dias, TipoDia.RITO, titulo, escena, certeza, fuente)

/** Algo que pasa en su mundo y no es fiesta ni guerra: un eclipse, una peste, una inundación, una noticia. No se mueve. */
internal fun mundo(id: String, fecha: String, titulo: String, dias: Int = 1, certeza: CertezaDia, fuente: Fuente? = null, escena: String = "", clima: Clima? = null) =
    DiaEspecial(id, fecha, dias, TipoDia.MUNDO, titulo, escena, certeza, fuente, null, clima)

/**
 * Un suceso de su vida. Siempre es inventado (el personaje es una persona promedio, no alguien que existió) y nunca
 * lleva fuente. Los de una misma [cadena] dependen uno de otro. Ojo en la **temporada 5**: desde 2032 se repite y sus
 * sucesos se mueven hasta 45 días, así que allí no pueden depender de un día que no se mueve (una fiesta, una batalla).
 */
internal fun suceso(id: String, fecha: String, titulo: String, dias: Int = 1, cadena: String? = null, escena: String = "", clima: Clima? = null) =
    DiaEspecial(id, fecha, dias, TipoDia.SUCESO, titulo, escena, CertezaDia.INVENTADO, null, cadena, clima)

/** Los pesos del clima de un mes. Lo que no se escribe no sale ese mes. */
internal fun mes(despejado: Int = 0, nubes: Int = 0, lluvia: Int = 0, tormenta: Int = 0, nieve: Int = 0, niebla: Int = 0, viento: Int = 0, polvo: Int = 0): Map<Clima, Int> =
    mapOf(Clima.DESPEJADO to despejado, Clima.NUBES to nubes, Clima.LLUVIA to lluvia, Clima.TORMENTA to tormenta, Clima.NIEVE to nieve, Clima.NIEBLA to niebla,
        Clima.VIENTO to viento, Clima.POLVO to polvo).filterValues { it > 0 }

/** Un punto de un arco: temporada, fecha «MM-DD» y valor de 0 a 1. Con [salto], cambia de golpe ese día. */
internal fun p(temporada: Int, fecha: String, valor: Double, salto: Boolean = false) = PuntoArco(temporada, fecha, valor, salto)
