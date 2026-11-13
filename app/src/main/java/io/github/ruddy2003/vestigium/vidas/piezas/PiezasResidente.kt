package io.github.ruddy2003.vestigium.vidas.piezas

import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Entorno
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Genero
import io.github.ruddy2003.vestigium.vidas.ObjetoCuento
import io.github.ruddy2003.vestigium.vidas.Parrafo
import io.github.ruddy2003.vestigium.vidas.Piezas
import io.github.ruddy2003.vestigium.vidas.TextoEspecial
import io.github.ruddy2003.vestigium.vidas.Variante

/*
 * El cuento de la residente, técnica de purificación de agua en un refugio bajo tierra, de 2105 a 2109. Sigue la
 * gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su ficha se llama «Una residente»: el cuento escribe «la residente» a mano y no usa el hueco del nombre.
 * - Bajo tierra no hay cielo ni estaciones: el párrafo del tiempo cuenta las luces y el aire del refugio. El único
 *   clima de verdad es la lluvia del día en que sale a mojarse (temporada 5), que tiene su especial.
 * - Su día tiene dos tramos de trabajo y dos comidas: esos textos valen a cualquiera de sus horas.
 * - Tres estados: «racionamiento» (temporada 2), «cuarentena» (3) y «abierto» (5, desde que se abre la Puerta).
 * - El pueblo lee la cisterna (arco «reserva») cuando está baja, y si no, los tanques de cultivo (arco «cultivo»).
 * - No repite lo que ya dice «Su vida»: las luces de las diez y de las seis, el altavoz optimista, la prueba de los
 *   dieciséis, las latas de los domingos, las quinientas personas, los discos de los años cincuenta, la puerta que nadie
 *   abre.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG7.kt`):
 * - Lina: con permiso para un hijo desde `lina` (temporada 1); su hijo nace en `hijo_lina` (2).
 * - Noa: la de los cultivos hasta `noa` (temporada 2); piden cuarto juntas hasta `cuarto_doble` (3); viven juntas;
 *   fuera con la expedición de `salen` a `vuelven` (4); la residente espera un hijo desde `espera` (5).
 * - La residente es lesbiana: quiere a Noa. El cuento no lo subraya ni lo nombra; lo cuenta como cuenta lo demás. El hijo
 *   llega por el permiso y la enfermería del refugio, que deciden todos los nacimientos.
 * - La vieja Dorotea, hasta que muere (`dorotea`, temporada 3). El supervisor nuevo, desde `supervisor` (3).
 * - Encargada del turno de las bombas desde `encargada` (temporada 2).
 * - Las fiestas que vuelven cada año no nombran a Lina, a Noa ni a Dorotea.
 */
internal object PiezasResidente {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("A la residente la despierta el zumbido de los tubos de la luz, que suben de golpe en todo el refugio a la misma hora, y el altavoz del pasillo, que da los buenos días con música de trompetas. Se tapa la cabeza con la almohada un minuto. Luego, al suelo frío."),
        v("La residente se despierta un momento antes de las luces y se queda en la oscuridad escuchando el refugio: el aire que entra por las rejillas, el agua en los tubos de las paredes, alguien que tose tres cuartos más allá. Los tubos del agua son los suyos. Suenan bien."),
        v("La residente se despierta con el olor del comedor, que sube por los conductos del aire: algo de los tanques, cocido, con mucho ajo. Su madre ya está abajo, en la cocina, desde hace una hora. La residente se pone el mono azul con el número a la espalda."),
        v("Lo primero que hace la residente al levantarse es mirar el contador de su muñeca, el de la radiación, como le enseñaron de niña: verde. Siempre está verde. Nadie sabe por qué se mira cada mañana. Se mira igual."),
        v("La residente se despierta con una canción en la cabeza, una de los discos viejos del comedor, que ha soñado entera. No sabe lo que quiere decir la letra: habla de un sol de California, de una carretera, de un coche. Ella no ha visto nada de eso. La tararea igual."),
        v("A la residente la despierta el golpe de una compuerta lejana, que se cierra en el turno de noche de los de mantenimiento. El ruido recorre los pasillos como un trueno de juguete. Ella nunca ha oído un trueno de verdad. Se lo imagina así, más grande."),
    )

    /** Cinco semanas con la cisterna rota y dos litros por persona, en la temporada 2 (estado «racionamiento»). */
    private fun ra(texto: String, hacer: Hacer? = null) = v(texto, estado = "racionamiento", hacer = hacer)

    private val RACION_MANANA = listOf(
        ra("La residente se despierta con sed, como todos en el refugio desde que empezó el racionamiento, y lo primero que hace es mirar la botella de su mesilla: la mitad. Es lo que le queda hasta mañana. Bebe un sorbo. Se lava la cara con un paño apenas húmedo."),
        ra("A la residente la despierta el altavoz, que hoy no pone música: da la cifra de agua de la cisterna, como cada mañana desde la grieta, y luego dice que hay que tener ánimo. La residente se viste deprisa. Abajo, en la sala de bombas, la esperan."),
        ra("La residente se despierta con la boca pastosa y los labios agrietados, porque el aire del refugio, con el racionamiento, también se ha vuelto más seco. Se pone crema de la enfermería. Le queda poca. Todo queda poco. Sale al pasillo, que huele a gente sin lavar."),
    )

    private val RACION_TRABAJO = listOf(
        ra("A esta hora está en la cisterna grande, metida hasta las rodillas en el agua que queda, con la linterna en la frente, siguiendo la grieta de la pared con el dedo para ver dónde termina. No termina. Sigue y sigue. Detrás de ella, otro técnico va sellando con masilla.", Hacer.TRABAJO),
        ra("A esta hora reparte el agua del día en la sala de bombas, con una lista y un grifo medidor: dos litros por persona, uno más para los niños y los enfermos. La gente hace cola con sus botellas. Nadie se queja en voz alta. Todos miran el grifo como si fuera a escaparse.", Hacer.TRABAJO),
        ra("A esta hora revisa los filtros de las bombas que reciclan el agua sucia, el agua de los lavabos, de las cocinas, de todo, para volver a beberla. Con la cisterna rota, cada gota tiene que pasar tres veces. Los filtros se saturan. Los cambia. Se le cortan las manos.", Hacer.TRABAJO),
        ra("A esta hora come en el comedor un plato de cereal seco con un poco de pasta de los tanques, sin caldo, porque el caldo es agua. Lo traga con esfuerzo. Todos comen así estos días: en silencio, despacio, con la botella al lado, sin beber de ella hasta el final.", Hacer.COMER),
        ra("A esta hora duerme mal, con sed, despertándose con la lengua pegada al paladar. Bebe un sorbo de la botella. Luego otro. Se obliga a parar. Mañana habrá dos litros más. Cuenta los días que faltan para que la cisterna esté arreglada. No lo sabe nadie.", Hacer.DORMIR),
        ra("A esta hora no tiene rato propio: se queda en la sala de bombas un turno más, porque hace falta, y nadie le dice que se vaya. Pone un disco en el tocadiscos pequeño de la sala. Suena una orquesta. Le recuerda que hubo un mundo donde el agua caía del cielo.", Hacer.LIBRE),
        ra("A esta hora está en el comedor con los demás, pero nadie canta ni juega a las cartas: se habla poco, porque hablar da sed. Los niños están quietos en las rodillas de los mayores. El supervisor pasa entre las mesas diciendo que falta poco. Lo dice cada noche.", Hacer.GENTE),
    )

    private val RACION_PUEBLO = listOf(
        ra("Con el racionamiento, el refugio está más callado: las duchas cerradas con candado, las cocinas sin vapor, los lavabos secos, carteles en todas las paredes con la cifra de agua de cada día. El aire huele distinto. Huele a quinientas personas que no se pueden lavar."),
        ra("En la cisterna grande, abajo del todo, el agua ha bajado tanto que se ven las marcas de los años en la pared, como anillos de un árbol. Al fondo, la grieta, negra, con su parche de masilla gris que todavía no aguanta."),
    )

    private val RACION_ANOCHECER = listOf(
        ra("Cuando bajan las luces, la residente vuelve a su cuarto con las piernas mojadas y la garganta seca, y se tumba sin quitarse el mono. La botella de la mesilla tiene dos dedos de agua. Se los guarda para la mañana. Cierra los ojos y piensa en el agua que sabe arreglar."),
        ra("Al bajar las luces, el altavoz da la cifra de la cisterna de la noche, un poco mejor que la de la mañana, y en los pasillos se oye algo parecido a un aplauso. La residente sonríe. Es su agua. La que ella y los suyos le han ganado hoy a la grieta."),
    )

    /** El sector cerrado por una fiebre, en la temporada 3 (estado «cuarentena»). */
    private fun cu(texto: String, hacer: Hacer? = null) = v(texto, estado = "cuarentena", hacer = hacer)

    private val CUARENTENA_MANANA = listOf(
        cu("La residente se despierta en el sector A, al otro lado de las compuertas cerradas, y lo primero que hace es mirar la luz roja de la compuerta del pasillo, la que dice que el sector B sigue cerrado. Sigue roja. Detrás, la fiebre. Y su madre, que trabajaba allí."),
        cu("A la residente la despierta el altavoz con el parte de la fiebre: cuántos enfermos nuevos, cuántos mejor, cuántos peor. Escucha conteniendo el aire. Hoy no dice ningún nombre que conozca. Respira. Se levanta. Se pone la mascarilla de tela que cosió ayer."),
        cu("La residente se despierta y se toca la frente, como cada mañana desde que cerraron las compuertas, para ver si tiene fiebre. No tiene. Se lava las manos con el jabón amarillo de la enfermería, que huele a hospital, y sale al pasillo con la mascarilla."),
    )

    private val CUARENTENA_TRABAJO = listOf(
        cu("A esta hora mantiene las bombas del sector A sola, porque los otros técnicos se quedaron al otro lado de las compuertas. Hace el trabajo de tres. Va de una bomba a otra corriendo, con la lista en la mano. Por el intercomunicador, sus compañeros del sector B le dicen qué mirar.", Hacer.TRABAJO),
        cu("A esta hora pasa por la ventanilla de la compuerta cerrada las botellas de agua limpia para el sector B, una a una, por un hueco con dos puertas, con guantes. Al otro lado, alguien con mascarilla las recoge. A veces es su madre. Se miran a través del cristal.", Hacer.TRABAJO),
        cu("A esta hora come en el comedor del sector A, con la mitad de las mesas vacías y la gente sentada lejos una de otra, con la mascarilla bajada solo para comer. No se habla. Se escucha el altavoz. El sector B está al otro lado de la pared. Se oye su comedor, apagado.", Hacer.COMER),
        cu("A esta hora duerme con la mascarilla puesta, aunque no hace falta, porque así se siente más segura. Se despierta con cada tos del pasillo. Cada tos puede ser la fiebre. Escucha. La tos se calla. Se vuelve a dormir.", Hacer.DORMIR),
        cu("A esta hora habla con su madre por el intercomunicador de la compuerta, diez minutos, como cada tarde: su madre está bien, cocina para los enfermos, no se ha contagiado. Le dice que coma. La residente le dice que coma ella. Se ríen un poco. Se les quiebra la voz.", Hacer.LIBRE),
        cu("A esta hora está en su cuarto, sola, porque las reuniones están prohibidas en la cuarentena, escuchando un disco en su tocadiscos pequeño, el que le regalaron de niña. Pone la misma canción tres veces. Es la única compañía permitida. Le basta, casi.", Hacer.GENTE),
    )

    private val CUARENTENA_PUEBLO = listOf(
        cu("Con las compuertas del sector B cerradas, el refugio es la mitad de sí mismo: pasillos que terminan en una puerta de acero con una luz roja, comedores medio vacíos, la enfermería llena. Por los altavoces, la voz de la doctora, cansada, cada pocas horas."),
        cu("En la compuerta entre los dos sectores hay una ventanilla con dos puertas, por donde pasan el agua, las medicinas y las cartas, y un intercomunicador viejo donde cada tarde se hace cola para hablar con los del otro lado. Nadie habla más de diez minutos."),
    )

    private val CUARENTENA_ANOCHECER = listOf(
        cu("Cuando bajan las luces, la residente se queda un momento delante de la compuerta cerrada, con la mano en el acero, como si pudiera notar a los del otro lado. Está fría. Al otro lado, alguien golpea dos veces, despacio. Ella contesta con dos golpes. Se va a dormir."),
        cu("Al bajar las luces, el altavoz da el parte de la noche: dos enfermos más, tres que ya no tienen fiebre. La residente cuenta los días que llevan cerradas las compuertas. Muchos. Se tumba. Se toca la frente. Fresca. Se duerme."),
    )

    /** La Puerta abierta para siempre, desde la temporada 5 (estado «abierto»). */
    private fun ab(texto: String, hacer: Hacer? = null) = v(texto, estado = "abierto", hacer = hacer)

    private val ABIERTO_MANANA = listOf(
        ab("A la residente no la despiertan las luces del refugio, sino una luz que no viene de ningún tubo: la del día, que entra por la Puerta abierta y baja por el pasillo grande, gris, débil, hasta su cuarto. Se queda mirándola en la pared. Se mueve."),
        ab("La residente se despierta con un ruido que no conoce y que tarda en entender: es el viento, que entra por la Puerta abierta y silba en las rejillas del pasillo. Se pone la mano sobre la barriga, donde el niño se mueve, y escucha. El refugio respira de otra manera."),
        ab("La residente se despierta temprano y sube por el pasillo grande hasta la Puerta, como cada mañana desde que se abrió, envuelta en una manta, para ver salir el sol. Hay más gente esperando. Nadie habla. Cuando el sol sale, alguien siempre llora. Hoy, ella no."),
    )

    private val ABIERTO_TRABAJO = listOf(
        ab("A esta hora trabaja en las bombas como siempre, porque el agua del refugio no deja de hacer falta porque se abra una puerta. Pero hoy deja la sala de bombas con la puerta abierta, y desde ahí oye pájaros. No sabe qué pájaros. Ya lo sabrá.", Hacer.TRABAJO),
        ab("A esta hora sale con otros técnicos a buscar el arroyo que la expedición encontró fuera, a medio kilómetro de la Puerta, con un contador de radiación y unos bidones. Mide el agua. Limpia. Fría. Llena los bidones. Sus botas se hunden en la tierra blanda. Le da risa.", Hacer.TRABAJO),
        ab("A esta hora come fuera, sentada en una piedra delante de la Puerta, un cuenco de lo de siempre, que fuera sabe distinto, con el viento en la cara. Los niños corren por la hierba gritando. Los viejos se quedan en la Puerta, con miedo de dar el paso.", Hacer.COMER),
        ab("A esta hora duerme con la Puerta abierta al fondo del pasillo, y entra el ruido de fuera: grillos, viento, un animal que no sabe qué es. Al principio no podía dormir con eso. Ahora no puede dormir sin eso.", Hacer.DORMIR),
        ab("A esta hora pasea fuera, despacio, por la ladera de delante de la Puerta, mirando todo como si fuera la primera vez, porque lo es: una piedra, un árbol torcido, una nube. Se cansa enseguida: no está acostumbrada a andar sin paredes. Se sienta.", Hacer.LIBRE),
        ab("A esta hora está fuera, delante de la Puerta, alrededor de un fuego de verdad, el primero que ha visto en su vida, con los demás, cantando las canciones de los discos viejos, que ahora suenan distinto, con el cielo encima. Las estrellas son demasiadas.", Hacer.GENTE),
    )

    private val ABIERTO_PUEBLO = listOf(
        ab("Desde que se abrió la Puerta, el refugio tiene un fondo de luz: el pasillo grande termina en un rectángulo de cielo, que cambia de color con las horas, azul, gris, naranja, negro."),
        ab("Fuera de la Puerta hay una ladera de hierba, unos árboles torcidos, piedras, y abajo un valle con un arroyo y las ruinas de un pueblo. No hay nadie. Hay pájaros, insectos, un viento que no para. Es lo más grande que la residente ha visto nunca. Y es pequeño."),
    )

    private val ABIERTO_ANOCHECER = listOf(
        ab("Al caer la tarde, de verdad, con un sol que baja detrás de las montañas y lo pone todo naranja, la residente se queda en la Puerta con Noa, viendo cómo se va la luz. Dice que es como cuando bajan las luces del refugio. Noa dice que no. Que es mucho más despacio."),
        ab("Anochece fuera, y la residente ve aparecer las estrellas una a una, primero una, luego diez, luego tantas que da miedo. La vieja Dorotea se lo había contado. No se lo había creído del todo. Se queda mirando hasta que tiene frío. Luego entra."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el refugio el tiempo lo dice el altavoz, y siempre dice que hace bueno."),
        v("La residente pasa por delante del panel de control del aire y mira los números, como hace cada día. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} La vieja Dorotea decía que fuera, antes, el tiempo cambiaba cada día. La residente no se lo imagina."),
        v("{cielo} {de_estacion} En el refugio nadie habla del tiempo: no hay de qué hablar."),
        v("{de_estacion} {cielo} Los discos viejos hablan mucho de la lluvia y del sol. La residente no conoce ninguno de los dos."),
        v("Bajo tierra el día es una idea: lo hacen las luces y el reloj. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.SIN_CLIMA to listOf(
            "Las luces del techo están en blanco de día, iguales en todos los pasillos.",
            "El aire del refugio está a veinte grados y huele, como siempre, a metal y a tierra húmeda.",
            "Los ventiladores de las rejillas soplan un aire tibio que no se acaba nunca.",
            "En el techo del comedor, alguien pintó hace años un cielo azul con nubes, ya desconchado.",
            "La luz es la de siempre, blanca y sin sombra, que no se sabe de dónde sale.",
            "En las paredes de roca del pasillo grande brilla la humedad, como un sudor.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, de verdad, del cielo, y la lluvia entra por la Puerta abierta en una cortina gris.",
            "Cae del cielo un agua que nadie ha filtrado, y huele a tierra y a hierba.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.NINGUNA to listOf(
            "En el refugio no hay estaciones: todos los días son iguales de largos y de tibios.",
            "Bajo tierra el año no se nota, salvo por las fiestas del altavoz.",
            "En el refugio no hace frío ni calor: hace refugio.",
            "Aquí abajo los meses se cuentan, pero no se sienten.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora está en la sala de bombas, delante de la pared de manómetros, leyendo uno a uno los números del agua: la presión, el caudal, la turbidez. Los apunta en el cuaderno del turno con su letra pequeña. Todo bien.", hacer = Hacer.TRABAJO),
        v("A esta hora cambia un filtro grande de la depuradora, un cilindro del tamaño de un niño, lleno de un barro negro que huele a sótano. Lo saca entre dos, lo vacía, lo lava con el agua de lavar filtros, lo vuelve a meter. El agua que sale después es transparente.", hacer = Hacer.TRABAJO),
        v("A esta hora toma muestras de agua en seis grifos de seis pasillos distintos, en tubitos de cristal numerados, y las lleva al laboratorio para mirarlas al microscopio. En la gota, a veces, se mueve algo. Hoy no. Hoy el agua es solo agua. Apunta: limpia.", hacer = Hacer.TRABAJO),
        v("A esta hora está tumbada debajo de una bomba vieja, con la linterna en la boca, apretando una junta que gotea. Las bombas del refugio son más viejas que su abuela, y las piezas se hacen en el taller con lo que hay. La gota se para. Se queda mirando un momento. Vuelve a caer. Aprieta más.", hacer = Hacer.TRABAJO),
        v("A esta hora recorre los tubos del agua por los pasillos de servicio, que corren detrás de las paredes del refugio, estrechos, oscuros, llenos de cables. Va dando golpecitos a los tubos con una llave y escuchando. Un tubo sano suena distinto que uno con cal.", hacer = Hacer.TRABAJO),
        v("A esta hora le explica a un muchacho de dieciséis, recién salido de la prueba de aptitudes, cómo funciona la sala de bombas. Él mira todo con miedo. Ella también miraba así, cuando entró. Le deja abrir una válvula. Él la abre muy despacio. Bien, le dice. Él sonríe.", hacer = Hacer.TRABAJO),

        v("A esta hora come en el comedor, en una mesa larga de metal, lo que sale de los tanques: un puré verde de algas con proteína texturizada, un pan de harina de insectos, una fruta pequeña del invernadero. Sabe a lo de siempre. Se lo come todo. Su madre lo cocinó.", hacer = Hacer.COMER),
        v("A esta hora come deprisa en la sala de bombas, de una fiambrera, sin dejar de mirar los manómetros: unas tortitas de los tanques con una salsa roja que hace su madre con los tomates del invernadero, la única cosa picante del refugio. Le lloran los ojos. Le gusta.", hacer = Hacer.COMER),
        v("A esta hora come con Lina en una esquina del comedor, las dos con la bandeja, hablando bajito de todo y de nada: quién mira a quién, qué dijo el supervisor, qué disco pondrán esta noche. Se ríen. Las de la mesa de al lado las miran. Se ríen más.", hacer = Hacer.COMER),

        v("A esta hora duerme en su litera, en el cuarto que comparte, con la luz tenue del pasillo entrando por la rendija de la puerta. El refugio de noche zumba más bajito. Ella duerme hondo, con el sueño de quien ha trabajado con las manos todo el día.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con cosas que no ha visto nunca y que sabe solo de los discos y de los libros: un mar, una montaña, un árbol con hojas que se caen. En el sueño son raras, mal hechas, como dibujos de niño. Se despierta con ganas de verlas.", hacer = Hacer.DORMIR),
        v("A esta hora duerme a ratos, porque el contador de su muñeca le pita una vez por la noche, como a todos, para comprobar que funciona. Se despierta, mira: verde. Se vuelve a dormir. Lleva toda la vida con ese pitido. No sabe dormir sin él.", hacer = Hacer.DORMIR),

        v("A esta hora está en la sala de la terminal, en su turno de media hora, leyendo en la pantalla verde los libros de antes de la guerra que hay en la memoria del refugio. Lee uno de viajes. De barcos. De islas. No sabe qué es una isla de verdad. Le suena bonito.", hacer = Hacer.LIBRE),
        v("A esta hora escucha discos en la sala de música, con los auriculares grandes que pesan como un casco, sentada en un sillón que perdió el relleno hace años. Pone la cara B de un disco que nadie pone, porque todos ponen la A. Le gusta más la B. Nadie sabe por qué.", hacer = Hacer.LIBRE),
        v("A esta hora camina por el pasillo grande del refugio, de punta a punta, ida y vuelta, que es lo más lejos que se puede ir en línea recta: trescientos metros. Al fondo, la Puerta, enorme, redonda, cerrada. Se para delante. La toca. Está fría. Se da la vuelta.", hacer = Hacer.LIBRE),

        v("A esta hora está en el comedor después de la cena con los de su edad, jugando a las cartas con una baraja tan vieja que las figuras ya no se ven, apostando galletas de la ración. Alguien pone un disco. Alguien baila. El supervisor pasa, mira, sonríe. Se va.", hacer = Hacer.GENTE),
        v("A esta hora está en la sala común viendo una película de antes de la guerra en la pantalla grande, con cien personas en sillas de plástico. Es una película de un pueblo con calles y coches y gente que se va de viaje. Los niños preguntan qué es un viaje.", hacer = Hacer.GENTE),
        v("A esta hora ayuda a su madre a recoger la cocina del comedor, fregando las bandejas en la pila grande con el agua que ella misma limpió por la mañana. Su madre le cuenta cosas de cuando era joven, que fue en el mismo refugio, con la misma gente. Igual, dice.", hacer = Hacer.GENTE),
    )

    private val PUEBLO = listOf(
        v("{entorno} El refugio es una ciudad escondida en la roca: seis pisos hacia abajo, pasillos largos con tubos en el techo, cuartos numerados, un comedor, una enfermería, una escuela, talleres y, abajo del todo, las bombas, las cisternas y el generador. Todo tiene un número."),
        v("La sala de los tanques de cultivo es la más bonita del refugio: filas de tanques de cristal con algas verdes que burbujean bajo luces violetas, y estanterías de lechugas, tomates y hierbas. Huele a vivo. Los niños vienen a mirar las burbujas como si fueran peces. {entorno}"),
        v("{entorno} La sala de bombas es el sitio de la residente: una nave de techo bajo, llena de máquinas viejas pintadas de verde, tubos de todos los tamaños, manómetros, válvulas con ruedas rojas y un olor a aceite y a agua que no se quita nunca."),
        v("El comedor del refugio es la plaza: mesas largas de metal, un mostrador, un tocadiscos con un altavoz enorme, y en una pared un mural de los fundadores, pintado por alguien que no sabía pintar. En el techo, un cielo azul de pintura. Los niños lo miran al comer. {entorno}"),
        v("{entorno} En el pasillo grande, al final, está la Puerta: un círculo de acero de cuatro metros, con un número pintado y una rueda en el centro como la de una caja fuerte. Hay una línea amarilla en el suelo que nadie pisa."),
        v("Las paredes del refugio están llenas de carteles de antes de la guerra: una familia sonriente con un perro, un hombre con un casco y el pulgar arriba, unas letras que dicen que todo va bien. Están amarillos, rotos por las esquinas. Nadie los ha quitado nunca. {entorno}"),
    )

    /** La cisterna cuando está baja (arco «reserva») y, si no, los tanques de cultivo (arco «cultivo»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("reserva", 0.0, 0.6, frases = listOf(
            "La cisterna grande todavía está a medias, y en los carteles del comedor sigue la cifra del agua.",
            "El agua de la cisterna sube despacio, y todavía se racionan las duchas.",
        )),
        Entorno("cultivo", 0.0, 0.35, frases = listOf(
            "Los tanques de cultivo están recién sembrados, con las algas claras y los brotes pequeños.",
            "En la sala de los tanques hay más cristal que verde, porque se acaba de cosechar.",
        )),
        Entorno("cultivo", 0.35, 0.75, frases = listOf(
            "Los tanques de cultivo están verdes y espesos, y las tomateras suben por los hilos.",
            "En la sala de los tanques el verde va ganando, y el aire huele a hoja.",
        )),
        Entorno("cultivo", 0.75, 1.0, frases = listOf(
            "Los tanques de cultivo están llenos, oscuros de algas, y las estanterías se doblan de lechugas.",
            "En la sala de los tanques no cabe un tomate más, y ya se prepara la cosecha.",
        )),
    )

    private val GENTE = listOf(
        v("Su madre es cocinera del comedor, una mujer pequeña y rápida que hace milagros con lo que sale de los tanques y que lleva treinta años levantándose antes que nadie. Le guarda a la residente el trozo más tostado de la tortita. Le dice que está flaca."),
        v("Los de la sala de bombas son seis, y la residente es la más joven. El maestro es un viejo con las manos manchadas de aceite que sabe cada tubo del refugio por su nombre, porque les ha puesto nombres. Los demás se ríen de él. Luego le preguntan."),
        v("El supervisor del refugio es el que lo decide todo: los turnos, los permisos, las raciones, quién tiene un hijo y cuándo. Habla siempre por el altavoz, y casi nunca en persona. Cuando habla en persona, es que algo va mal.", hasta = "supervisor"),
        v("El supervisor nuevo es un hombre joven que pasea por los pasillos, habla con la gente y dice en voz alta una cosa que el anterior no decía nunca: que algún día habrá que abrir. Los viejos se asustan. Los jóvenes lo siguen por los pasillos.", desde = "supervisor"),
        v("La doctora del refugio es una mujer seria que lleva una bata gris y un estetoscopio que fue de su madre, que fue doctora antes que ella. Conoce a todos los del refugio desde que nacieron, porque los trajo al mundo."),
        v("Los niños del refugio van a la escuela en una sala con pupitres de metal y una pizarra, y aprenden a leer en libros de antes que hablan de cosas que no han visto nunca: vacas, trenes, el mar. Hacen dibujos del mar. Son azules y redondos. Nadie sabe si están bien."),
        v("El viejo Tobías, el de la terminal, cuida los libros y los discos de la memoria del refugio como si fueran sus hijos. Lleva un registro de quién lee qué. A la residente le guarda los de viajes, porque sabe que le gustan. Le dice que algún día los leerá todos."),
        v("La vieja Dorotea vive en el cuarto del fondo del pasillo tres y es la más vieja del refugio: su abuela entró por la Puerta antes de que se cerrara, y le contó cómo era fuera. Dorotea se lo cuenta a la residente, que la visita cada semana. Le cuenta de la lluvia, del viento, del sol.", hasta = "dorotea"),
        v("La vieja Dorotea murió, y con ella se fueron las historias de fuera que su abuela le contó. La residente las apuntó en un cuaderno, todas las que pudo, con la letra de una niña que tiene prisa. Lo guarda debajo del colchón.", desde = "dorotea"),
        v("Lina es su amiga desde que tenían tres años y compartían cuna en la guardería del refugio. Trabaja en los tanques de cultivo y huele siempre a algas. Se lo cuentan todo. Lina quiere tener un hijo antes de los treinta. Ha pedido el permiso tres veces.", hasta = "lina"),
        v("A Lina le han dado por fin el permiso para tener un hijo, y espera, con la barriga cada vez más grande, en el turno de los tanques. La residente le lleva agua de más, a escondidas. Lina dice que si es niña, la llamará como la residente.", desde = "lina", hasta = "hijo_lina"),
        v("Lina tiene un hijo, un niño gordo y serio que nació en la enfermería y que la residente tuvo en brazos la primera. Lina lo lleva atado al pecho en el turno de los tanques. El niño mira las burbujas de las algas sin parpadear.", desde = "hijo_lina"),
        v("Noa trabaja en los tanques de cultivo, una muchacha alta y callada con las manos verdes de algas que le regaló una vez un disco que no tenía nadie. Se cruzan en el comedor y se miran de reojo. Lina dice que es evidente. La residente dice que no sabe de qué habla.", hasta = "noa"),
        v("Noa y la residente han pedido juntas un cuarto doble al supervisor, y esperan la respuesta, que tarda, porque los cuartos dobles son pocos. Mientras tanto, se ven en el comedor, en la sala de música, en los pasillos de servicio, donde no hay cámaras.", desde = "noa", hasta = "cuarto_doble"),
        v("Noa y la residente viven en un cuarto doble del pasillo cinco, con una litera ancha, un tocadiscos pequeño y una maceta con una tomatera que Noa trajo de los tanques. Noa se levanta antes, a los tanques; la residente se acuesta después, por las bombas. Se ven en medio.", desde = "cuarto_doble", hasta = "salen"),
        v("Noa está fuera, con la expedición, al otro lado de la Puerta, y la residente no sabe si está viva. Cada mañana va a la Puerta y se queda un momento delante. La radio de la entrada no dice nada. Ella sigue con las bombas. No sabe hacer otra cosa.", desde = "salen", hasta = "vuelven"),
        v("Noa volvió de fuera con tierra en las botas, una flor y una tos que tardó semanas en irse. No habla mucho de lo que vio. Por las noches, en el cuarto doble, le cuenta a la residente una cosa cada vez: un árbol, un arroyo, un pájaro. Ella las guarda como piedras.", desde = "vuelven", hasta = "espera"),
        v("La residente espera un hijo, el primero que nacerá con la Puerta abierta, o eso esperan. La doctora, que se encargó de todo en la enfermería, dice que nunca ha visto un embarazo tan tranquilo. Noa le habla a la barriga de lo que hay fuera, todas las noches, bajito. Lina le ha prestado la ropa de su hijo. Su madre teje.", desde = "espera"),
    )

    private val OBJETO = listOf(
        v("En el refugio nada se tira: todo se arregla, se cambia o se convierte en otra cosa. {dato_objeto}"),
        v("{dato_objeto} La residente lo aprendió de niña, en la escuela del refugio, como todos."),
        v("Hoy la residente se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} Los de antes de la guerra no se lo habrían creído. Los del refugio no se lo creerían de los de antes."),
        v("{dato_objeto} Bajo tierra, cada cosa tiene su historia y su número."),
        v("Las cosas del refugio son viejas, y por eso se cuidan como a viejos. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("llave", "la llave", "una llave de válvulas", "La llave",
            "La llave de válvulas de la residente es de hierro, larga como un brazo, con el mango forrado de cinta para que no resbale; fue de su maestro, y del maestro de su maestro; con ella se abren las ruedas rojas de las bombas, que solo ceden a la fuerza y a la paciencia."),
        ObjetoCuento("disco", "el disco", "un disco", "El disco",
            "Los discos del refugio son de vinilo, negros, en fundas de papel con fotos de gente sonriente de antes de la guerra; se ponen en el tocadiscos del comedor por turnos, y cada uno tiene una lista de quién lo ha pedido; algunos están tan gastados que suenan como lluvia."),
        ObjetoCuento("linterna", "la linterna", "una linterna de dinamo", "La linterna",
            "Las linternas del refugio no usan pilas, que se acabaron hace mucho: tienen una manivela que se gira para cargarlas, y dan luz mientras se gira y un rato después; en los pasillos de servicio se oye el zumbido de las manivelas antes de ver a nadie."),
        ObjetoCuento("mono", "el mono", "un mono azul", "El mono",
            "El mono del refugio es azul, de una tela gruesa que dura años, con un número grande pintado en la espalda y un bolsillo para el contador; todos llevan el mismo; cada uno lo distingue por los remiendos, que son como las arrugas de una cara."),
        ObjetoCuento("contador", "el contador", "un contador de radiación", "El contador",
            "El contador de radiación se lleva en la muñeca desde que se nace, como un reloj sin horas: una lucecita verde que se pondría amarilla, y luego roja, si algo entrara de fuera; en toda la vida de la residente, nunca ha cambiado de color."),
        ObjetoCuento("racion", "la ración", "una ración", "La ración",
            "La ración de cada uno se recoge en el comedor con una tarjeta perforada: tantas comidas, tanta agua, tanto jabón; los domingos hay un extra, de las latas de antes de la guerra, que se abren con ceremonia, como un regalo de los abuelos."),
        ObjetoCuento("cuaderno", "el cuaderno", "un cuaderno de turno", "El cuaderno",
            "El cuaderno de turno de la sala de bombas tiene cien años de letras distintas: cada técnico apunta lo que ha hecho, lo que ha visto, lo que hay que vigilar; las primeras páginas las escribieron los que cerraron la Puerta; la letra de la residente está en las últimas."),
        ObjetoCuento("pastilla", "la pastilla", "una pastilla de yodo", "La pastilla",
            "Las pastillas de yodo se guardan en una cajita metálica que cada uno lleva en el bolsillo del mono, por si suena la alarma; nadie las ha tomado nunca; a los niños les dicen que son caramelos de emergencia, y ellos se las piden."),
        ObjetoCuento("manta", "la manta", "una manta gris", "La manta",
            "Las mantas del refugio son grises, de una lana que pica, con el escudo del refugio bordado en una esquina; se reparten al nacer y se cambian a los veinte años; la de la residente tiene un agujero quemado de cuando, de niña, la acercó a una estufa."),
        ObjetoCuento("foto", "la foto", "una foto de fuera", "La foto",
            "Las fotos de fuera son pocas, de antes de la guerra, guardadas en la terminal y en algunas cajas: playas, ciudades, montañas, gente en un parque; se pasan de mano en mano los domingos, y los niños las miran como se mira un cuento con dibujos."),
        ObjetoCuento("tocadiscos", "el tocadiscos", "un tocadiscos", "El tocadiscos",
            "El tocadiscos del comedor es una caja de madera con una aguja que se cambia una vez al año, porque solo quedan diez; el técnico de la música lo limpia cada domingo con un pincel; cuando se rompe, el refugio entero se queda callado."),
        ObjetoCuento("flor", "la flor", "una flor de papel", "La flor",
            "Las flores del refugio son de papel, de las hojas viejas de los libros que ya no se pueden leer, dobladas y pintadas; se ponen en las mesas en las fiestas, en los cuartos de los enfermos, delante de la Puerta; hay quien no ha visto nunca una de verdad."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} La residente piensa en esas cosas un rato, y luego se le van, porque en el refugio las noticias son las mismas siempre: el agua, la comida, los turnos. A sus {edad} años sabe que el mundo de fuera existe porque lo dicen los libros. No porque lo haya visto."),
        v("Piensa en la Puerta, al fondo del pasillo grande, y en lo que habrá detrás. Piensa que a lo mejor no hay nada: un desierto, cenizas, nada. Piensa que a lo mejor hay todo: árboles, mar, gente. Piensa que da igual lo que haya. Que lo que le pesa es no saberlo.", hasta = "abre"),
        v("Piensa en la Puerta, abierta al fondo del pasillo, y en que ya sabe lo que hay detrás: hierba, piedras, un arroyo, un cielo que no se acaba. Piensa que saberlo no le ha quitado las ganas. Se las ha multiplicado. Piensa que el niño nacerá sabiéndolo.", desde = "abre"),
        v("{mundo} La residente piensa en los que cerraron la Puerta, hace casi cien años, y en lo que sentirían al cerrarla. Piensa que la cerraron para salvar a sus hijos, y a los hijos de sus hijos. Piensa que ella es uno de esos hijos. Piensa que alguien tendrá que abrirla."),
        v("{mundo} A veces, en la sala de bombas, con el ruido de las máquinas que no dejan pensar, la residente se imagina que el agua que limpia viene de una montaña, de un río, de una nube. Viene de la roca. Pero se lo imagina igual. Le hace el trabajo más bonito."),
        v("Piensa en su madre, que nació en el refugio y morirá en él, y en su abuela, igual. Piensa que ella no quiere eso. Lo piensa y se asusta, porque es como traicionarlas. Piensa que a lo mejor su madre tampoco quería eso. Que no se lo ha preguntado nunca."),
        v("{mundo} A sus {edad} años la residente sabe hacer funcionar el agua de quinientas personas, y no sabe qué es una nube. Lo piensa a veces y le da risa. Piensa que si un día sale, lo primero que hará será mirar una nube, mucho rato. Y luego buscará agua."),
    )

    private val MUNDOS = mapOf(
        "sensor" to listOf(
            "Se habla en el comedor del sensor de la entrada, que cada año marca un poco menos de radiación fuera.",
            "Se dice que el supervisor guarda en su despacho un mapa de fuera que no enseña a nadie.",
            "Se habla de las bombas viejas, que cada año piden más piezas que no hay.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando bajan las luces, a la hora de siempre, el refugio se queda en la luz tenue de la noche, azul, como el fondo de una piscina, y se oyen las puertas de los cuartos cerrándose una tras otra. La residente vuelve al suyo por el pasillo, contando las puertas."),
        v("Al bajar las luces, el altavoz pone la canción de la noche, siempre la misma, una canción de cuna de antes de la guerra con una voz de mujer muy suave. Todo el refugio la ha oído cada noche de su vida. La residente la canta bajito sin darse cuenta."),
        v("Cuando bajan las luces, la residente se queda un rato en el comedor vacío, mirando el cielo pintado del techo, que con la luz tenue parece casi de verdad, casi de noche. Se imagina estrellas. Pone tres con la imaginación. Luego cuatro. Luego se va a dormir."),
        v("Al bajar las luces, en el pasillo de la residente alguien toca la armónica, como cada noche, una melodía triste y lenta. Nadie sabe quién es. Nadie lo pregunta. Es parte de la noche del refugio, como el zumbido de los ventiladores y la luz azul."),
        v("Cuando bajan las luces, la residente apunta en el cuaderno del turno lo último del día: la presión, el caudal, una válvula que vigilar. Cierra el cuaderno. Apaga la lámpara de la sala de bombas. Las bombas siguen sonando en la oscuridad, como un corazón que no duerme."),
        v("La última luz del día, en el refugio, es la de la sala de los tanques, que se queda encendida más que las demás, violeta, para las algas. La residente pasa por delante y ve las burbujas subir en los tanques iluminados, despacio, como si dentro hubiera un mar pequeño."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo en el refugio, y anoche el altavoz contó hacia atrás desde diez, con todo el refugio en el comedor gritando los números. A las doce, las luces se encendieron al máximo un minuto entero, que es el máximo de luz que se ve en el año. Todos cerraron los ojos.",
            Parrafo.TRABAJO to "Hoy la residente no trabaja, salvo una vuelta por la sala de bombas para ver que todo sigue bien, que hace ella para que los otros duerman. Luego, el día libre: dormir, la terminal, un disco, la comida especial del año nuevo, con un postre de las latas viejas.",
            Parrafo.PENSAMIENTO to "La residente piensa en el año que empieza y le pide lo de siempre: que no se rompan las bombas, que no haya fiebre, que nadie se muera. Luego, más bajito, le pide otra cosa que no le pide nunca en voz alta. No la dice ni para sí misma. La piensa.",
        ),
        "sellado" to unDia(
            Parrafo.MANANA to "Hoy es el aniversario del sellado, el día en que se cerró la Puerta, hace casi cien años, y todo el refugio se junta en el pasillo grande, delante de ella, en silencio. El supervisor lee la lista de los que entraron. Son muchos nombres. Se tarda una hora.",
            Parrafo.GENTE to "Esta tarde, en el comedor, los viejos cuentan lo que les contaron sus abuelos del día del sellado: las colas en la entrada, los que no llegaron, el ruido de la rueda de la Puerta al cerrarse, que dicen que se oyó en todo el refugio. Los niños escuchan sin moverse.",
            Parrafo.PENSAMIENTO to "La residente piensa en los que no llegaron, los que se quedaron fuera cuando se cerró la Puerta. Piensa que nadie dice sus nombres, porque nadie los sabe. Piensa que habría que dedicarles un minuto también. Se lo dedica ella, sola, delante de la Puerta, cuando se va la gente.",
        ),
        "fundadores" to unDia(
            Parrafo.MANANA to "Hoy es el Día de los Fundadores, la fiesta más alegre del refugio: el comedor adornado con flores de papel, una tarta hecha con una lata de fruta de antes de la guerra, y el tocadiscos poniendo toda la tarde los discos más bailables. Hasta el supervisor baila.",
            Parrafo.TRABAJO to "Hoy la residente no trabaja: es fiesta. Ayuda a su madre en la cocina con la tarta de los fundadores, que lleva una lata entera de melocotones en almíbar, la única del año. La abren con ceremonia, delante de todos. Huele a algo que nadie sabe nombrar. A fuera, dice alguien.",
            Parrafo.PENSAMIENTO to "La residente piensa en los fundadores, los del mural del comedor, mal pintados, sonrientes, que construyeron el refugio sabiendo que no verían el final de la guerra. Piensa que se lo agradece. Piensa también que le gustaría preguntarles si pensaron en cuándo abrir. Si lo pensaron.",
        ),
        "filtros" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empieza la revisión de los filtros grandes, tres días de trabajo doble en la sala de bombas, con el agua del refugio pasando por los filtros pequeños mientras se abren los grandes. Es lo más importante del año para los del agua. La residente baja antes de las luces.",
                Parrafo.TRABAJO to "A esta hora abre el primer filtro grande con los demás técnicos, aflojando los pernos con la llave larga, uno a uno, mientras el agua gotea por todas partes. Dentro, un barro negro de todo el año. Lo sacan a paladas. Huele a cien años de refugio.",
                Parrafo.PENSAMIENTO to "La residente piensa que en este barro está todo lo que el refugio ha bebido, lavado y sudado en un año. Piensa que ella lo limpia para que vuelva a empezar. Piensa que es como lavar la memoria de una casa. Le gusta. No se lo dice a nadie.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue en la revisión de los filtros, cambiando la arena y el carbón del segundo filtro grande, cubo a cubo, con los brazos que ya no responden. El maestro canta. Nadie le pide que se calle.",
                "A esta hora cierra el último filtro grande y abre la válvula, despacio, mirando el agua que sale al otro lado: primero turbia, luego gris, luego transparente. Los técnicos aplauden. Ha salido bien otro año.",
            )),
        ),
        "cosecha" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empieza la cosecha grande de los tanques de cultivo, tres días, y medio refugio baja a la sala de los tanques a ayudar: se cortan las algas, se recogen las lechugas, los tomates, las hierbas, se vacían los tanques para sembrarlos otra vez. Huele a verde en todos los pasillos.",
                Parrafo.TRABAJO to "A esta hora está en la sala de los tanques, con los brazos metidos hasta el codo en el agua verde, sacando algas a puñados y echándolas en cestas. Están frías y resbalan. Los niños llevan las cestas a la cocina, corriendo. Se le pone la piel verde. Se le quitará en una semana.",
                Parrafo.PENSAMIENTO to "La residente piensa que el agua de los tanques es su agua, la que ella limpia, y que las algas la convierten en comida. Piensa que todo en el refugio da vueltas: agua, algas, comida, agua. Piensa que ella es una vuelta más. Le parece bien.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue en la cosecha de los tanques, limpiando los cristales vacíos con un cepillo largo para la siembra nueva. Los tanques limpios parecen ventanas. Por un momento, se ve a través de ellos.",
                "A esta hora ayuda a sembrar los tanques vacíos con las algas madre, que se guardan en un tanque pequeño todo el año, como una semilla. Las echa con cuidado. En un mes, el tanque estará verde otra vez.",
            )),
        ),
        "prueba" to unDia(
            Parrafo.MANANA to "Hoy es la prueba de aptitudes de los que cumplen dieciséis este año, y los muchachos esperan en el pasillo de la escuela con la cara blanca, porque lo que salga hoy será su vida entera: bombas, tanques, cocina, enfermería, mantenimiento. No se puede cambiar.",
            Parrafo.GENTE to "Esta tarde, en el comedor, se anuncian los destinos de los nuevos, uno por uno, con un aplauso para cada uno. Unos sonríen. Otros miran el plato. Un muchacho nuevo va a la sala de bombas. La residente lo busca con la mirada y le sonríe. Él no sabe todavía qué cara poner.",
            Parrafo.PENSAMIENTO to "La residente piensa en su propia prueba, a los dieciséis, y en el miedo que tenía, y en que le tocó el agua y al principio lloró, porque quería la terminal. Piensa que ahora no cambiaría su sala de bombas por nada. Piensa que la prueba a veces acierta. A veces.",
        ),
        "sol_falso" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta del sol falso: todas las luces del comedor se ponen al máximo desde que se encienden hasta que se apagan, una luz blanca y caliente que hace sudar, y la gente se pone la ropa más ligera que tiene, y se tumba en el suelo, con los ojos cerrados, como dicen que se hacía en las playas.",
            Parrafo.TRABAJO to "Hoy la residente no trabaja: es fiesta. Se tumba en el suelo del comedor con sus amigas, con gafas oscuras hechas de cartón y celofán, bajo las luces, comiendo trozos de fruta de los tanques. Alguien pone un disco de una playa. Alguien dice que hace calor. Nadie sabe qué es el calor de verdad.",
            Parrafo.PENSAMIENTO to "La residente piensa, con los ojos cerrados bajo la luz, que a lo mejor el sol de verdad es así: blanco, caliente, por todas partes. Piensa que a lo mejor es más. Piensa que el sol de verdad no se apaga a las diez. Le cuesta imaginárselo.",
        ),
        "recuento" to unDia(
            Parrafo.MANANA to "Hoy es el recuento del supervisor: cuántos somos, cuánta agua, cuánta comida, cuántas piezas de repuesto, cuántos años más se puede aguantar así. Todos los jefes de sección entregan sus números. La residente entrega los del agua, que ha preparado durante una semana.",
            Parrafo.TRABAJO to "A esta hora está en el despacho del supervisor con el cuaderno del agua, explicando las cifras: lo que dan las bombas, lo que se pierde, lo que falta. El supervisor escucha sin decir nada, apuntando. Al final pregunta cuántos años aguantan las bombas. Ella dice la verdad. Él no apunta eso.",
            Parrafo.PENSAMIENTO to "La residente piensa en los números del recuento, que cada año son un poco peores: menos piezas, más averías, el mismo número de personas. Piensa que el refugio es como un reloj al que no se le puede dar cuerda. Piensa que alguien tendrá que decirlo en voz alta. Algún día.",
        ),
        "permisos" to unDia(
            Parrafo.MANANA to "Hoy se anuncian los permisos de hijos del año, en el comedor, por el altavoz: cinco parejas, cinco nombres. El refugio tiene comida y agua para quinientas personas, ni una más, y cada año alguien muere y alguien nace. Las parejas que esperan se cogen de la mano.",
            Parrafo.GENTE to "Esta tarde, en el comedor, las parejas con permiso reciben los aplausos de todos, y una manta pequeña, gris, con el escudo del refugio. Las que no lo han tenido sonríen y aplauden también, con la cara apretada. El año que viene. Siempre el año que viene.",
            Parrafo.PENSAMIENTO to "La residente piensa que en el refugio nacer es una cuenta: alguien tiene que irse para que alguien llegue. Piensa que fuera, si hay fuera, no habrá cuentas. Piensa que a lo mejor eso da miedo también. Piensa que prefiere ese miedo.",
        ),
        "simulacro" to unDia(
            Parrafo.MANANA to "Hoy es el simulacro de cierre: a media mañana suena la alarma, todas las compuertas del refugio bajan a la vez con un ruido de trueno, y cada uno tiene que estar en su puesto en menos de dos minutos. Las luces se ponen rojas. Los niños pequeños lloran. Los mayores cronometran.",
            Parrafo.TRABAJO to "A esta hora está en su puesto de la sala de bombas, con las compuertas cerradas y la luz roja, cerrando las válvulas de emergencia una por una, como se ensaya cada año. Lo hace en un minuto y medio. El maestro lo apunta. Es la mejor marca de la sala.",
            Parrafo.PENSAMIENTO to "La residente piensa, mientras se levantan las compuertas, que llevan casi cien años ensayando una emergencia que no llega. Piensa que a lo mejor la emergencia es otra: que no pase nunca nada. Piensa que eso no se ensaya. Que eso no tiene alarma.",
        ),
        "nochebuena" to unDia(
            Parrafo.MANANA to "Hoy es Nochebuena en el refugio, y en el comedor hay un árbol hecho de cable verde y trozos de tubo, con luces pequeñas que solo se encienden esta noche y bolas de papel de colores. Debajo del árbol, regalos para los niños, hechos a mano en los talleres durante todo el año.",
            Parrafo.TRABAJO to "Hoy la residente no trabaja: es fiesta. Ayuda en la cocina a preparar la comida especial, la más grande del año, con todas las latas de antes de la guerra que tocan: jamón, guisantes, un pudin. El comedor huele a fiesta, que es un olor que solo existe este día.",
            Parrafo.PENSAMIENTO to "La residente piensa en la Nochebuena de fuera, la de los discos, con nieve, con chimeneas, con gente que llega de lejos. Piensa que en el refugio no llega nadie de lejos, nunca. Piensa que todos los que celebran están ya aquí, siempre. Que eso tiene algo bueno.",
        ),

        // ---------- Los de la temporada 1 ----------
        "bomba_tres" to unDia(
            Parrafo.MANANA to "Hoy, antes de que se encendieran las luces, ha saltado la alarma de la sala de bombas: la bomba tres se ha roto, y el agua de los grifos sale marrón. La residente ha bajado corriendo en pijama, con el mono en la mano. El maestro ya estaba allí.",
            Parrafo.TRABAJO to "A esta hora está desmontando la bomba tres con el maestro, pieza por pieza, buscando qué se ha roto: un eje partido, gastado por cien años de vueltas. No hay otro. Hay que hacerlo en el taller, a mano, con un trozo de barra vieja. Tardarán todo el día. El agua seguirá marrón.",
            Parrafo.PENSAMIENTO to "La residente piensa que todo el refugio está bebiendo agua marrón por un eje de hierro que alguien hizo hace cien años y que ha aguantado hasta hoy. Piensa en ese alguien. Piensa que hizo un buen trabajo. Piensa que el suyo tendrá que durar otros cien.",
        ),
        "disco" to unDia(
            Parrafo.MANANA to "Hoy, en el comedor, Noa, la de los cultivos, se ha acercado a su mesa sin decir nada y le ha dejado delante un disco en una funda de papel. Es uno que no tiene nadie: lo ha encontrado en una caja vieja de los tanques. Luego se ha ido, roja, a su mesa.",
            Parrafo.GENTE to "Esta tarde pone el disco de Noa en el tocadiscos de la sala de música, con los auriculares grandes. Es una mujer que canta con una guitarra, muy bajito, en un idioma que nadie habla en el refugio. Lina se sienta a su lado y la mira escuchar. Dice: ya está. La residente le dice que se calle.",
            Parrafo.PENSAMIENTO to "La residente piensa que es el primer regalo que le hace alguien que no es de su familia. Piensa en la cara de Noa al dejarlo en la mesa. Piensa que no sabe qué dice la canción, pero que sabe de qué habla. Pone el disco otra vez.",
        ),
        "sensor" to unDia(
            Parrafo.MANANA to "Hoy el altavoz ha dado una noticia que no es ninguna fiesta: el sensor de la entrada, el que mide la radiación de fuera, marca menos que nunca. No dice que se pueda salir. Dice que marca menos. En el comedor no se habla de otra cosa.",
            Parrafo.GENTE to "Esta tarde, en el comedor, los viejos dicen que el sensor está roto. Los jóvenes dicen que fuera ya se puede vivir. El supervisor dice que no hay que hacer caso de rumores. La vieja Dorotea, en un rincón, sonríe y no dice nada. La residente se sienta a su lado.",
            Parrafo.PENSAMIENTO to "La residente piensa en lo que significa un número más bajo en un sensor: que fuera algo ha cambiado, aunque nadie sepa qué. Piensa en la Puerta. Piensa que hoy, por primera vez, la ha mirado distinta. No con miedo. Con prisa.",
        ),
        "lina" to unDia(
            Parrafo.MANANA to "Hoy, en el anuncio de los permisos de hijos, el altavoz ha dicho el nombre de Lina. Lina, en el comedor, se ha quedado quieta con la cuchara en el aire, y luego ha dado un grito que ha hecho reír a todo el refugio. La residente la ha abrazado tan fuerte que se le cayó la bandeja.",
            Parrafo.GENTE to "Esta tarde, Lina no deja de hablar: del nombre, de la manta, de la cuna que le harán en el taller, de si será niño o niña. La residente la escucha en la sala de música, con un disco de fondo. Lina le dice que será la madrina. La residente dice que vale. Le tiembla la voz.",
            Parrafo.PENSAMIENTO to "La residente piensa que el hijo de Lina nacerá en el refugio, como ellas, como sus madres, y que a lo mejor morirá en él. Piensa que no quiere pensar eso hoy. Piensa en el niño, en sus manos pequeñas. Piensa que le enseñará a limpiar el agua.",
        ),
        "lluvia" to unDia(
            Parrafo.MANANA to "Hoy la residente ha ido a visitar a la vieja Dorotea a su cuarto del fondo del pasillo tres, como cada semana, y la ha encontrado con ganas de hablar. Dorotea le ha dicho que se siente. Que hoy le va a contar cómo era la lluvia, como se lo contó su abuela.",
            Parrafo.GENTE to "Esta tarde Dorotea le cuenta la lluvia: que caía del cielo sin que nadie la mandara, que olía a tierra, que hacía ruido en los tejados, que a veces mojaba a la gente por sorpresa y la gente se reía o se enfadaba. La residente escucha con los ojos cerrados. Intenta oírla.",
            Parrafo.PENSAMIENTO to "La residente piensa que la lluvia es agua que nadie ha limpiado, que cae limpia del cielo, gratis, para todos. Piensa que eso es lo más raro que ha oído nunca. Piensa que si la ve un día, la beberá con la boca abierta, como dice Dorotea que hacían los niños.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "residente", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + RACION_MANANA + CUARENTENA_MANANA + ABIERTO_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + RACION_TRABAJO + CUARENTENA_TRABAJO + ABIERTO_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + RACION_PUEBLO + CUARENTENA_PUEBLO + ABIERTO_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + RACION_ANOCHECER + CUARENTENA_ANOCHECER + ABIERTO_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
