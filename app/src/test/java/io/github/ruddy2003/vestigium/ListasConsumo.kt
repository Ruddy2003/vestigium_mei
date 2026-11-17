package io.github.ruddy2003.vestigium

/*
 * Las listas que vigilan el consumo en la voz del tema Psicodelia (`VozPsicodeliaPrueba`) y en el cuento de David
 * (`vidas/AnioG9Prueba`, `vidas/CuentoG9Prueba`), en un solo sitio (`docs/diseno/temas/psicodelia_plan.md`, 3.2). Hasta el
 * 16 nov 2026 era una sola lista, `JERGA_DE_DROGAS`, que no podía salir en ningún texto; el autor quitó la regla de «sin
 * drogas» y la lista se partió en tres según lo que vigilan. Cada entrada es una palabra entera o el principio de una
 * («impuls\\w*» quita «impulso» e «impulsos»), sin contar mayúsculas.
 */

/** Una lista de palabras enteras (o principios de palabra), sin contar mayúsculas. */
internal fun palabrasVigiladas(vararg l: String): List<Regex> = l.map { Regex("""(?iU)(?<![\p{L}\p{N}])(?:$it)(?![\p{L}\p{N}])""") }

/**
 * Lo del seguimiento privado (rachas, recaídas, impulsos, libido, medicamentos) y las palabras que lo rozan. No sale en
 * nada: ni en el cuento, ni en el calendario, ni en «Su vida», ni en la voz del tema. Así ni el tema ni el cuento dialogan
 * con lo que la persona anota en privado.
 */
internal val LO_PRIVADO: List<Regex> = palabrasVigiladas(
    "racha\\w*", "reca[íi]d\\w*", "impuls\\w*", "abstinen\\w*", "sobriedad", "malos? viajes?", "baj[óo]n\\w*", "libido", "medicament\\w*",
    "detonantes?", "antojos?",
)

/**
 * La jerga que celebra el consumo. No sale en ningún texto: el plan la permitía en boca de un personaje y entre comillas,
 * pero el cuento de David solo pone entre comillas las frases comprobadas, así que no hay excepción.
 */
internal val JERGA_QUE_CELEBRA: List<Regex> = palabrasVigiladas(
    "coloc\\w*", "flip\\w*", "subid[óo]n\\w*", "viajaz\\w*", "buen viaje", "pedo", "high", "stoned", "trip", "tripp\\w*", "tripe\\w*",
    "volad[oa]s?", "pachec\\w*", "groovy",
)

/**
 * La jerga que solo se prohíbe en la voz del tema: «puesto» es una palabra de todos los días en el cuento (los puestos del
 * recinto ferial, la puesta de sol), pero en la voz del tema no tiene por qué salir.
 */
internal val JERGA_SOLO_EN_LA_VOZ: List<Regex> = palabrasVigiladas("puest[oa]s?")

/**
 * Los nombres de las sustancias y del consumo, y lo de fumar, beber y tomar. **Ya no se prohíben en el cuento**: salen solo
 * las noches que toca (las de las claves de consumo) y en los días de la ciudad que lo nombran. Sí se prohíben en la voz del
 * tema («el tema muestra, no comenta»).
 */
internal val NOMBRES_DE_SUSTANCIAS: List<Regex> = palabrasVigiladas(
    "drog\\w*", "marihuana", "mariguana", "cannabis", "hach[íi]s", "porros?", "lsd", "[áa]cid[oa]s?", "hongos?", "psilocib\\w*",
    "peyote", "mescalin\\w*", "fum\\w*", "cigarr\\w*", "tabaco", "alcohol\\w*", "vinos?", "cervez\\w*", "licor\\w*", "whisky",
    "borrach\\w*", "hero[íi]na", "coca[íi]na", "anfetamin\\w*", "cafe[íi]na", "sustancias?", "consum\\w*", "dosis", "pastill\\w*",
)
