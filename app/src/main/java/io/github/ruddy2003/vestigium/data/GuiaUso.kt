package io.github.ruddy2003.vestigium.data

/**
 * La guía de uso: cómo llenar la app para que tus datos digan algo. Cada sección tiene párrafos y, si sirve, ejemplos
 * (con @personas y #temas, que la pantalla marca como el tema del día). [PANTALLAS] dice qué sección abre el «?» de cada pantalla.
 */
object GuiaUso {
    class Seccion(val id: String, val titulo: String, val parrafos: List<String>, val ejemplos: List<String> = emptyList())

    val SECCIONES = listOf(
        Seccion("principios", "Principios", listOf(
            "La app sirve para conocerte con datos de muchos años, no para calificarte. Un día malo registrado vale más que un día bueno inventado.",
            "Lo mínimo cada día: un «Ánimo ahora» cuando te avisa (o cuando quieras) y la hoja de la noche. Todo lo demás es opcional y se suma solo.",
            "Si un día no puedes, toca «hoy no» en Hoy. Si se juntan tres, la app te sugiere la semana corta: cuatro cifras y tres botones, nada más.",
            "Escribe con @ para personas y # para temas. Así todo se cruza: con quién estuviste, de qué se trató el día y cómo te sentiste.",
        )),
        Seccion("animo", "Ánimo ahora", listOf(
            "Un toque en el cuadro basta: a la derecha más agradable, arriba más energía. Lo demás (qué sientes, en qué piensas, con quién) es opcional.",
            "Responde lo que sientes en ese momento, no el promedio del día. Si no sabes nombrarlo, «no sé» también es un dato.",
            "Los avisos llegan a horas al azar dentro de tu ventana: así el promedio no depende de cuándo te acuerdas.",
        )),
        Seccion("diario", "Diario", listOf(
            "No hace falta escribir bonito. Qué pasó, con quién y qué te movió. Tres líneas sirven.",
            "Debajo del editor, cada @persona tiene un chip: estuve con ella, pensé en ella o hablamos por mensaje. Cámbialo con un toque si no estuvo de verdad.",
            "Usa #temas para lo que se repite (#tesis, #gym, #familia). En Tus temas puedes juntar los que se parecen y renombrarlos.",
        ), listOf("Comí con @ana y hablamos de la #tesis; salí más tranquilo.", "Pensé mucho en @luis; le escribí por la tarde. #ansiedad")),
        Seccion("sueno", "Sueño", listOf(
            "En la mañana, primero el texto del sueño (si recuerdas algo), luego calidad, si fue agradable, lucidez y vividez.",
            "Si no recuerdas nada, marca solo la calidad: también es un dato. El reloj llena las horas por su cuenta.",
        )),
        Seccion("noche", "Hoja de la noche", listOf(
            "Ánimo y energía del día completo, hábitos, contexto (gym, viaje, enfermo…), hitos y una predicción para mañana.",
            "Al final, «Cómo estás ahora»: un registro a la misma hora cada día, que se puede comparar entre días mejor que el promedio.",
            "Hitos en minúsculas y con guion bajo (primer_dia_de_trabajo). Si un #tema aparece un solo día, Tus temas te propone pasarlo a hito.",
        )),
        Seccion("sustancias_gente", "Sustancias y gente", listOf(
            "Sustancias (alcohol, cannabis, tabaco, cafeína) son un dato normal: registra el inicio y, si quieres, cómo va el nivel y la cruda.",
            "En Tu gente dices quién es cada @persona y qué tan cerca la sientes. Si la relación cambia, lo de antes se guarda con su fecha.",
            "En la ficha de cada persona, «¿A quién conoce?» arma tu Red: quién se conoce con quién.",
        )),
        Seccion("lugares", "Lugares", listOf(
            "Cuando pasas 20 minutos en un sitio nuevo, la app pregunta si es un lugar especial. Guárdalo con su tipo (casa, trabajo, gym, naturaleza…).",
            "«No guardar nunca aquí» ignora ese sitio para siempre; «Ahora no» vuelve a preguntar en tres días. Nunca pregunta de noche ni en movimiento.",
            "Con los lugares guardados aparecen las horas por tipo de lugar y el ánimo en cada tipo.",
        )),
        Seccion("privado", "Lo privado", listOf(
            "Impulsos, recaídas y libido son solo para ti: nunca salen en reportes, Tu año, logros, mapas, widgets ni búsqueda. Sí van a la exportación a R.",
            "La libido es una escala de 0 a 5, distinta del impulso. Si además es un impulso, el botón «también es un impulso» lo anota.",
        )),
        Seccion("suplementos", "Suplementos", listOf(
            "Toca «Tomé» en Suplementos o en el menú ⊕. La creatina se modela como una reserva que se llena en semanas y se vacía en semanas.",
            "Los modelos son aproximados: cambia la vida media o los días de carga si sabes algo mejor. No es un consejo médico.",
        )),
        Seccion("notas_metas", "Notas y metas", listOf(
            "Notas: ideas, pensamientos especiales, propósitos y recordatorios, con @ y #. Fija arriba las importantes.",
            "Metas: elige el horizonte y, si puedes, lígala a un hábito o una variable para que el progreso se calcule solo. En diciembre aparece la plantilla de propósitos.",
        ), listOf("Idea: hablar con @mario de la beca. #maestria")),
        Seccion("laptop", "Laptop (Cold Turkey)", listOf(
            "Cada semana exporta las estadísticas de Cold Turkey (apps y sitios) e impórtalas en Laptop. Cambia la categoría de cada app o sitio con un toque.",
            "Cold Turkey solo da minutos por día, así que no hay horas exactas (por ejemplo, cuánto usaste la laptop de noche).",
        )),
        Seccion("no_se", "Cuando no sabes qué poner", listOf(
            "Pon lo que sí sabes y deja vacío lo demás. Vacío no es cero: la app distingue un dato faltante de un «nada».",
            "Si dudas entre dos números, elige el primero que te vino. La consistencia contigo mismo importa más que la precisión.",
        )),
        Seccion("leerte", "Cómo leerte", listOf(
            "Con pocos días todo es pista, no evidencia: mira la Cobertura de variables antes de creerle a una correlación.",
            "Que dos cosas se muevan juntas no prueba que una cause la otra. Para causas, haz un experimento: la hipótesis queda sellada con la hora antes de empezar.",
            "En R, usa modelos con autocorrelación o multinivel: los días se parecen a sus vecinos.",
        )),
        Seccion("errores", "Errores comunes", listOf(
            "Registrar solo los días buenos (o solo los malos): los huecos sesgan todo. Un registro de un toque en un día difícil vale oro.",
            "Escribir la misma persona de dos formas (@ana y @anita): en Tu gente, «¿son la misma persona?» las une.",
            "Dejar de importar: el aviso semanal te recuerda MacroFactor, Cold Turkey y Takeout.",
        )),
        Seccion("lista", "Lista rápida", listOf(
            "Mañana: sueño (texto primero) y suplementos.",
            "Durante el día: «Ánimo ahora» cuando te avisa; enfoque con el cronómetro; momentos que valgan la pena.",
            "Noche: la hoja de la noche y «Cómo estás ahora».",
            "Cada semana: importar lo pendiente y revisar campañas y metas.",
        )),
    )

    /** Qué sección abre el «?» de cada pantalla (por su título en español). */
    val PANTALLAS: Map<String, String> = mapOf(
        "Día" to "principios", "Hoy" to "principios", "Privado" to "privado", "Llenar el día" to "noche", "Cómo usar la app" to "principios", "Diario" to "diario", "Momentos" to "animo", "Todos tus registros" to "animo", "Tu gente" to "sustancias_gente",
        "¿Estuviste o pensaste?" to "diario", "Tus temas" to "diario", "Sustancias" to "sustancias_gente", "Impulsos y recaídas" to "privado",
        "Lugares" to "lugares", "Lugares especiales" to "lugares", "Suplementos" to "suplementos", "Notas" to "notas_metas", "Metas" to "notas_metas",
        "Laptop" to "laptop", "Red" to "sustancias_gente", "Experimentos personales" to "leerte", "Análisis" to "leerte", "Cobertura de variables" to "leerte",
        "Constelación" to "sustancias_gente", "Datos" to "errores", "Estado de los datos" to "errores", "Hábitos" to "noche",
    )

    fun de(id: String) = SECCIONES.firstOrNull { it.id == id }
}
