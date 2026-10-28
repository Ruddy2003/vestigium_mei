# Apéndice E. Glosario

_Reúne, en orden alfabético, los términos que cada capítulo explica en su mini-glosario, con el capítulo donde se explican. Cuando un término aparece en dos capítulos, se dan las dos explicaciones, porque cada una lo dice desde su tema. Se genera con `ensamblado/glosario.py`; no se edita a mano._

| Término | Qué significa | Cap. |
|---|---|---|
| **Absorción** | El paso de la sustancia del estómago a la sangre. Tarda un rato: por eso un café no hace efecto en el segundo en que lo tomas. | 5 |
| **Activación** | Cuánta **energía o agitación** hay en el cuerpo y la mente, sin importar si es agradable. De 1 (muy baja: agotado) a 7 (muy alta: acelerado). En inglés, *arousal*. | 3 |
| **AES-256** | El algoritmo de cifrado estándar más usado; «256» es el largo de la llave en bits. | 10 |
| **Aleatorizar (sortear)** | Decidir al azar qué periodos llevan la acción. Evita que tú elijas, sin querer, hacerla justo cuando ya ibas a estar mejor. | 8 |
| **Ancla** | El texto que acompaña a un número de una escala para que signifique lo mismo cada vez (p. ej., «4 = Neutral: plano»). | 3 |
| **Android Keystore** | Un almacén de llaves del sistema: la app puede usar una llave para cifrar, pero no puede sacarla del teléfono. | 10 |
| **AR(1)** (autocorrelación de orden 1) | Supuesto de que el parecido entre días decae de un día a otro de forma fija: lo que importa es cuánto se parece cada día al anterior. La app lo usa para corregir los intervalos de las correlaciones (n efectivo) y de las diferencias de medias (sección 6.6 y 6.15). | 6 |
| **Arrastre** (*carryover*) | Que el efecto de una condición siga actuando cuando ya empezó la otra. El lavado existe para evitarlo. | 8 |
| **Autocorrelación** | (cap. 6) Qué tanto el valor de hoy se parece al de ayer. Si duermes poco una semana entera, los días de esa semana se parecen entre sí. (cap. 8) Que un día se parezca al anterior (si hoy estás bien, mañana probablemente también). La autocorrelación de orden 1, *ρ*, va de −1 a 1; 0 significa días independientes. | 6, 8 |
| **Autorreporte** | Una respuesta que das tú sobre ti mismo. Es la única forma de medir lo subjetivo, y tiene sesgos conocidos (sección 3.4). | 3 |
| **Autoseguimiento** (*self-tracking*) | Registrar de forma sistemática datos sobre uno mismo: ánimo, sueño, pasos, gastos. Puede ser a mano (un diario) o con sensores (un reloj). | 1 |
| **Benjamini-Hochberg (BH) y q** | Una corrección para comparaciones múltiples. q < 0.10 quiere decir, aproximadamente: «de todas las relaciones que marco como firmes, espero que a lo más un 10 % sean azar». | 6 |
| **β estandarizado** | En un modelo con varias causas posibles: cuántas desviaciones estándar cambia el objetivo cuando un predictor sube una desviación estándar, con los demás fijos. | 6 |
| **Campaña** | Un reto opcional de lunes a domingo («5 noches de 7 horas»). | 9 |
| **Cegamiento** | Que quien recibe la acción no sepa si la está recibiendo (como con una pastilla de azúcar). En estos experimentos casi nunca es posible. | 8 |
| **Cercanía (IOS)** | Qué tan cerca sientes a alguien, de 1 a 7, eligiendo entre siete pares de círculos cada vez más encimados (Aron et al., 1992). | 7 |
| **Cifrar** | Convertir un archivo en algo ilegible sin una llave. | 10 |
| **Co-mención** | Que dos personas aparezcan mencionadas **el mismo día lógico**, aunque sea en textos distintos. Es la base de las líneas de la Constelación y de la Red. | 7 |
| **Comparaciones múltiples** | (cap. 1) Revisar muchas preguntas a la vez. Cuantas más se revisan, más «hallazgos» salen por puro azar (sección 1.6). (cap. 6) Revisar muchas parejas a la vez. Con 1000 parejas sin relación, unas 50 tendrán p < 0.05 por azar. | 1, 6 |
| **Componente** | Un grupo de nodos conectados entre sí por algún camino; dos componentes no tienen ninguna línea entre ellas. | 7 |
| **Condición** | Cada una de las dos situaciones que se comparan: **con** la acción («SÍ») o **sin** ella («no»). | 8 |
| **Confusor** | Una tercera cosa que mueve a las dos variables que se comparan, como el día de la semana o estar enfermo. Las protecciones de la app no lo eliminan. | 6 |
| **Constancia** | Cumplir algo **la mayoría** de los días de una ventana (por ejemplo, 25 de 30), sin necesidad de racha. | 9 |
| **Correlación (r)** | Un número de −1 a 1 que dice qué tanto dos variables suben y bajan juntas. +1: suben juntas siempre en línea recta; 0: ninguna relación lineal; −1: cuando una sube, la otra baja. | 6 |
| **Covariable que cambia con el tiempo** (*time-varying covariate*) | Un dato de contexto que no es fijo, sino que tiene un valor por periodo; por ejemplo, la relación que tenías con una persona cada día. | 7 |
| **d de Cohen** | La diferencia entre las dos condiciones medida en «desviaciones estándar»: cuántas veces el vaivén normal de un día a otro (Cohen, 1988). | 8 |
| **Dato faltante (`null`)** | «No sé» para ese día. No es lo mismo que 0: 0 minutos de ejercicio es un dato; no tener reloj ese día es un faltante. | 6 |
| **Dato importado** | Lo que llega de un archivo que tú exportas de otro servicio y cargas en la app (Spotify, Google, WhatsApp…). | 4 |
| **Dato pasivo** | Lo que el teléfono o el reloj registran solos (pasos, pantalla, ubicación…). Tú no haces nada. | 4 |
| **Densidad** | Qué fracción de todas las líneas posibles existe. Con *n* personas hay *n(n−1)/2* pares posibles. | 7 |
| **Desfase** | Comparar lo de hoy con lo de **mañana** (¿dormir hoy se asocia con el ánimo de mañana?). | 6 |
| **Desviación estándar (DE)** | Qué tan lejos del promedio suelen caer los valores. | 6 |
| **Día de calendario** | El día normal, de 00:00 a 24:00. | 4 |
| **Día lógico** | (cap. 3) El día de la app: empieza a las 05:00 (capítulo 2). Un registro a la 01:00 cuenta para el día anterior. (cap. 4) El día de la app: empieza a las 05:00 (capítulo 2, sección 2.3). Un evento a las 01:00 cuenta para el día *anterior*. (cap. 7) El día de la app: empieza a las 05:00 (capítulo 2). | 3, 4, 7 |
| **Diseño ABAB (alterno)** | Alternar periodos sin la acción (A) y con ella (B) varias veces. En la app: semanas alternas, en un orden sorteado. | 8 |
| **Diseño antes/después (AB)** | Un solo periodo sin la acción y luego uno con ella. | 8 |
| **Eliminación** | Cómo el cuerpo la va quitando (sobre todo el hígado). | 5 |
| **Eliminación de orden cero** | Se va una **cantidad fija** por hora, sin importar cuánto haya. El alcohol se elimina así casi siempre, porque la enzima que lo procesa trabaja a su máxima velocidad desde los primeros tragos (Jones, 2010). | 5 |
| **Entre personas / dentro de la persona** | Una relación **entre personas** compara a unas personas con otras (Ana duerme más que Beto). Una relación **dentro de la persona** compara los días de alguien consigo mismo (Ana duerme más los sábados que los martes). Pueden ir en direcciones distintas (sección 1.4.2). | 1 |
| **Estacionario** | Un proceso cuyas reglas no cambian con el tiempo: su promedio, su variabilidad y sus relaciones son las mismas este año que hace cinco. Una vida casi nunca lo es del todo. | 1 |
| **Estado subjetivo** | Lo que solo tú puedes observar: cómo te sientes, qué piensas, qué tan estresado o solo te sientes. No hay sensor que lo mida; hay que preguntarlo. | 3 |
| **Diseño de caso único / N-de-1** | (cap. 1) Un estudio con **una sola persona** que se observa o se compara consigo misma muchas veces. «N» es el número de participantes; aquí, 1 (capítulo 8). (cap. 8) Un experimento con **una sola persona**, que se compara consigo misma en distintos periodos: unos con la acción y otros sin ella. «N» es el número de participantes; aquí, 1. | 1, 8 |
| **Factor de Widmark** (*r*) | La fracción del peso corporal en la que se reparte el alcohol (casi toda el agua del cuerpo). | 5 |
| **Falsa señal** (falso positivo) | Que el método diga «hay un efecto» cuando en realidad no lo hay. Un intervalo del 95 % bien hecho lo hace el **5 %** de las veces. | 8 |
| **Farmacocinética** | Cómo entra, se reparte y sale una sustancia del cuerpo con el tiempo. | 5 |
| **Gamificación** | Usar elementos de juego (puntos, medallas, retos, rachas) en algo que no es un juego (Deterding et al., 2011). | 9 |
| **GCM** | Un modo de usar AES que, además de cifrar, añade una **etiqueta de autenticación**: si alguien cambia un solo bit del archivo, el descifrado falla en vez de dar datos alterados (Dworkin, 2007). | 10 |
| **Grado** | Cuántas líneas tiene un nodo: con cuántas personas está unida. | 7 |
| **Grupo de relación** | La relación simplificada que usan los análisis: `familia`, `pareja`, `amistad`, `trabajo`, `casa` y, aparte, `expareja` y las demás (sección 7.4). | 7 |
| **Health Connect** | El sistema de salud de Android, donde otras apps (por ejemplo, la del reloj) escriben sus datos. La app solo pide permisos de lectura. | 4 |
| **hPa** | Hectopascales, la unidad de presión del aire (a nivel del mar, ~1013 hPa). | 4 |
| **Idiográfico** | Que busca entender **a una persona concreta**: «en mis días, ¿dormir más va con mejor ánimo?». Es el enfoque de esta app. | 1 |
| **Instrumento validado** | Un cuestionario publicado cuyas preguntas, escala y forma de puntuar se han probado en estudios (miden lo que dicen medir y dan resultados estables). | 3 |
| **Integridad** | Que los datos no se pierdan, no se dañen y no cambien sin que lo sepas. | 10 |
| **Intervalo de confianza de 95 %** | (cap. 6) El rango de valores compatibles con tus datos. Si va de 0.10 a 0.47, la relación real probablemente está en ese rango; si incluye el 0, podría no haber relación. (cap. 8) Un rango de valores plausibles para el efecto. Si se repitiera el experimento muchas veces con el mismo método, el 95 % de esos rangos contendría el efecto real… **si los supuestos del método se cumplen** (sección 8.6). | 6, 8 |
| **IV (vector de inicialización)** | Un número al azar que se usa una sola vez por cifrado para que el mismo contenido no produzca el mismo resultado. | 10 |
| **Jet lag social** | La diferencia entre el punto medio del sueño en los días libres y en los laborales: como si cada fin de semana viajaras a otro huso horario y volvieras el lunes (Wittmann et al., 2006). | 5 |
| **Lavado** (*washout*) | Días que **no cuentan** después de cada cambio de condición, para que el efecto de la condición anterior se vaya (y el de la nueva aparezca). | 8 |
| **Ley de Campbell** | Cuanto más se usa un indicador para premiar o decidir, más presión hay para distorsionarlo y más deja de reflejar lo que medía (Campbell, 1979). | 9 |
| **Logro** | Una medalla que se gana al llegar a una meta (100 días escritos, un día de 20 000 pasos). | 9 |
| **Maldición del ganador** | La relación más fuerte de una lista larga casi siempre exagera su tamaño, aunque sea real: al escoger la que dio la *r* más grande, se escoge también la que tuvo más suerte (Ioannidis, 2008). | 1 |
| **Mediana** | El valor del medio al ordenar una lista. Aguanta mejor los valores raros que el promedio. | 4 |
| **Mediana / MAD** | La mediana es el valor de en medio. La MAD (desviación absoluta mediana) es la mediana de las distancias a la mediana: una DE que no se deja arrastrar por los valores extremos. | 6 |
| **Medición en la vida diaria** | Preguntar o medir **mientras la persona vive su vida**, no en un laboratorio ni de memoria: avisos en el teléfono, sensores, diarios. En la literatura se llama *ambulatory assessment* o *ecological momentary assessment* (Trull y Ebner-Priemer, 2013; Shiffman et al., 2008). | 1 |
| **Mención** | Escribir `@nombre` en un texto (diario, momento, sueño, nota, carta). La app solo «ve» a una persona si está escrita así. | 7 |
| **MNAR** | «Faltante no al azar» (*missing not at random*): cuando la razón por la que falta un dato depende del propio dato. Ejemplo: no llenar el diario precisamente los días malos (Rubin, 1976; sección 3.15). | 3 |
| **Modelo circumplejo** | Propuesta de Russell (1980): casi todas las emociones se ubican en un plano con dos ejes independientes, valencia y activación. | 3 |
| **Modelo multinivel** (jerárquico, de efectos mixtos) | Modelo que trata de forma explícita que los días están anidados en semanas, meses y etapas: estima, por ejemplo, una relación en cada año y cuánto varía de un año a otro. La app no lo usa; es trabajo futuro. | 11 |
| **MSSD** | Promedio de los cuadrados de las diferencias entre días consecutivos de calendario con ambos datos: mide la inestabilidad de un día al siguiente (von Neumann et al., 1941). | 6 |
| **Muestreo de experiencia (ESM)** | Preguntar varias veces al día, en momentos al azar, «¿cómo estás ahora?». En inglés, *experience sampling method*; casi sinónimo de *ecological momentary assessment* (EMA). | 3 |
| **n / n efectivo (nEf)** | n: cuántos días tienen ambos datos. nEf: a cuántos días **independientes** equivalen, una vez descontado que se parecen a sus vecinos. | 6 |
| **Nombre canónico** | El nombre con el que la app cuenta a una persona después de aplicar las **equivalencias** (si dijiste que `@anita` es `@ana`, el canónico de `anita` es `ana`). | 7 |
| **Nomotético** | Que busca leyes que valgan **para la gente en general**: «en promedio, quien duerme más está de mejor ánimo». Es el enfoque habitual de la psicología y la medicina. | 1 |
| **p** | La probabilidad de ver una relación así de fuerte **por puro azar** si en realidad no hubiera ninguna. | 6 |
| **Panel** | (cap. 3) La tabla de análisis: una fila por día, una columna por variable (capítulo 2). (cap. 6) La tabla con **un renglón por día** y **una columna por variable**. Casi todos los análisis salen de ahí. | 3, 6 |
| **PBKDF2** | Una función que convierte una contraseña en una llave **a propósito lenta**: repite un cálculo muchas veces (las **iteraciones**) para que probar millones de contraseñas cueste mucho (Turan et al., 2010). | 10 |
| **Pearson / Spearman** | Dos formas de calcular la correlación. Pearson usa los valores; Spearman usa su **orden** (el día con más sueño es el 1.º, el siguiente el 2.º…), así que no le afectan los valores extremos y capta relaciones que suben sin ser rectas. | 6 |
| **Periodo anterior** | El periodo de la misma duración inmediatamente antes (la semana o el mes anterior). Los reportes se comparan contra él. | 9 |
| **Prerregistro** | Escribir la pregunta, la hipótesis y cómo vas a analizar **antes** de ver los datos, para no acomodar la conclusión a lo que salga (Nosek et al., 2018). | 8 |
| **Presencia** | Si ese día **estuviste con** la persona, solo **pensaste en** ella o hablaron por **mensajes** (capítulo 3, sección 3.13). | 7 |
| **Proxy** | (cap. 4) Una medida *indirecta* de lo que de verdad nos interesa. Ejemplo: «luz fuerte del teléfono» es un proxy de «estuve al aire libre», porque no mide el aire libre, sino la luz. (cap. 5) Una medida indirecta de lo que interesa (capítulo 4). | 4, 5 |
| **PSS-4** | Cuestionario de estrés percibido: cuatro preguntas de 0 (nunca) a 4 (muy a menudo), dos de ellas inversas; total de 0 a 16. En la app, cada 7 días. | 3 |
| **Pulso** | La tarea que la app ejecuta cada ~15 minutos en segundo plano (sección 4.2). | 4 |
| **Punto medio del sueño** | La hora a la mitad entre dormirte y despertar. | 5 |
| **Quantified Self** | El nombre con que se conoce a la comunidad y la práctica de medirse a uno mismo con números, sobre todo con tecnología (Swan, 2013). | 1 |
| **Quitar la tendencia** | Comparar cada día con sus vecinos en el calendario en vez de con todo tu historial. | 6 |
| **Racha** | Días **seguidos** cumpliendo algo. Un solo día sin cumplir la rompe. | 9 |
| **Reactividad** | Que medir algo cambie lo que se mide (capítulo 3, sección 3.14). | 9 |
| **Red (grafo)** | Un dibujo de **nodos** (personas) unidos por **aristas** o **vínculos** (líneas). En matemáticas se llama *grafo*. | 7 |
| **Red egocéntrica** | La red vista desde una persona central, el **ego** (tú), con sus contactos (los **alteri**) y las relaciones entre ellos (Perry et al., 2018). Es el tipo de red de esta app. | 7 |
| **Relación** | Qué es esa persona para ti (familia, pareja, amistad…). Se guarda con **historial**: la relación que tenía cada día. | 7 |
| **Relación obvia** | Dos variables que salen de la misma fuente o se calculan una de otra (calorías y proteína). Su correlación no dice nada nuevo de ti. | 6 |
| **Reporte** | Una imagen (y, para el mes y el año, también un PDF) que resume un periodo. La app la genera sola y la guarda en el teléfono. | 9 |
| **Restricción de Burt** | Una medida de qué tanto tus contactos **se conocen entre sí** (son redundantes). Baja = tus contactos están en mundos separados y tú eres el puente entre ellos (Burt, 2004; sección 7.11). | 7 |
| **Retrospectivo** | Que se responde **después**, recordando («¿cómo estuvo tu día?»). Lo opuesto a «momentáneo» («¿cómo estás ahora?»). | 3 |
| **RMSSD** | Una forma de medir la variabilidad del corazón (sección 4.5.2). | 4 |
| **Sal** | Bytes al azar que se mezclan con la contraseña, para que la misma contraseña dé llaves distintas en cada archivo. | 10 |
| **Saturación** | Qué tan llena está una reserva (aquí, la de creatina del músculo), de 0 a 100 %. | 5 |
| **Seguimiento privado** | Impulsos, recaídas, libido, detonantes, medicamentos y el hábito ligado a las recaídas. Desde el módulo 10a solo se ven en la pantalla Privado y viajan a R (sección 10.3). | 10 |
| **t de Student** | La curva que reemplaza a la normal cuando la variabilidad se estima con pocos datos: tiene colas más gruesas, y más cuanto menos grados de libertad. | 6 |
| **t de Welch** | La prueba t para comparar dos promedios sin suponer que los dos grupos varían lo mismo; admite grados de libertad no enteros (Welch, 1947). | 6 |
| **Tendencia** | (cap. 6) Un cambio lento, de semanas o meses (bajas de peso poco a poco; los días se acortan en otoño). (cap. 8) Que la medida suba o baje sola con el tiempo, por algo ajeno al experimento (la estación del año, avanzar en el gimnasio, una racha). | 6, 8 |
| **Transformación z de Fisher** | z = atanh(*r*): convierte una correlación en una escala donde su error es aproximadamente normal, para calcular su intervalo y su *p* (Fisher, 1915). | 6 |
| **Valencia** | Qué tan **agradable o desagradable** se siente algo. En la app, de 1 (muy desagradable) a 7 (muy agradable). | 3 |
| **Variable** | Algo que tiene un número cada día: horas de sueño, valencia, minutos en una app, si mencionaste a una persona (1) o no (0). | 6 |
| **Vida media** (T½) | El tiempo que tarda en irse **la mitad** de lo que hay. Si la vida media es 5 h, de 100 mg quedan 50 a las 5 h, 25 a las 10 h, 12.5 a las 15 h. Esto se llama eliminación **de primer orden** (se va una proporción fija por hora). | 5 |
| **Volumen de distribución** | El «tamaño del recipiente» en el que se diluye una sustancia. Con la misma cantidad, un recipiente más chico da una concentración más alta. | 5 |
| **WHO-5** | Cuestionario de bienestar: cinco frases, cada una de 0 (nunca) a 5 (todo el tiempo). En la app, cada 7 días. | 3 |
| **z robusto** | Qué tan lejos está un día de lo habitual, medido con la mediana y la MAD en lugar del promedio y la desviación estándar, para que los valores raros no muevan la regla. La app marca como atípico un día con |z| > 3. | 6 |
