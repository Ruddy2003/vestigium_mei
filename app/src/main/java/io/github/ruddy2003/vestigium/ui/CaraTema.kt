package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Consumo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Sustancias
import kotlin.math.roundToInt

/*
 * La cara y la intensidad de un tema (Psicodelia v2, tanda 1; `docs/diseno/temas/psicodelia_plan.md`, sección 1, con las
 * decisiones del autor del 16 nov 2026 que mandan sobre el plan; resumen para las otras tandas en
 * `docs/diseno/temas/psicodelia_base.md`).
 *
 * - La **cara** es un segundo eje, aparte del claro y el oscuro: cada cara es un mundo propio con su claro y su oscuro.
 *   Hoy solo la tiene Psicodelia ([TemaId.conCaras]): BASE es la ciudad de fines de los sesenta, INTENSA es la psicodelia
 *   de cartel. La decide la hora (de 21:00 a 5:00, [ModoHora]) **o** un consumo en curso ([caraSegun]).
 * - La **intensidad** es una cifra de 0 a 1 que sube con un consumo en curso de cualquier sustancia con nivel
 *   ([Intensidad]). El tema solo la lee de lo anotado: no se guarda, no se exporta, no cambia ningún cálculo.
 * - Los dibujos las leen de [EstadoTema], que es estado de instantánea: leerlo dentro de un `DrawScope` hace que Compose
 *   vuelva a dibujar solo ese dibujo cuando cambia, sin tocar las firmas de las interfaces de dibujo.
 */

/** Las dos caras de un tema que las tiene. Hoy solo Psicodelia: [BASE] = la ciudad, [INTENSA] = la psicodelia. */
enum class Cara { BASE, INTENSA }

/**
 * ¿Qué cara toca? Puro, sin Android. Un tema sin caras va siempre en [Cara.BASE]. Uno con caras va en [Cara.INTENSA] si
 * es de noche ([noche], de [ModoHora.esDeNoche]) **o** si hay un consumo en curso ([intensidad] mayor que 0): con un
 * consumo de día, la ciudad no se tiñe, cambia la cara (decisión 3 del autor, 16 nov 2026).
 */
fun caraSegun(tema: TemaId, noche: Boolean, intensidad: Float): Cara =
    if (tema.conCaras && (noche || intensidad > 0f)) Cara.INTENSA else Cara.BASE

/**
 * La intensidad del tema, de 0 a 1, a partir de los consumos anotados. Puro: no lee `Repo` ni la configuración (las pruebas
 * están en `IntensidadPrueba`).
 *
 * - Cuenta **cualquier sustancia con nivel** (`Catalogo.Sustancia.conNivel`: hoy alcohol, cannabis y «otra»; tabaco y
 *   cafeína no tienen nivel y no cuentan).
 * - Solo los consumos **en curso**: `inicio ≤ ahora ≤ Sustancias.finEfectivo(c, ahora)` (un consumo abierto de hace más
 *   de 18 horas se corta a las 4 horas de su inicio, así que uno que se olvidó cerrar no deja el tema encendido).
 * - Su nivel es `Sustancias.nivelEn(c, ahora, ahora)`, la misma interpolación de la exportación; **sin nivel anotado**,
 *   cuenta como [NIVEL_SIN_DATO] (3: la intensidad media).
 * - Con varios a la vez, **manda el mayor**.
 * - **Reacciona aunque la sustancia esté en `sustancias_ocultas`** (decisión 5 del autor): no hay corte por ocultas.
 * - Del nivel a la intensidad, por tramos ([deNivel]).
 */
object Intensidad {
    /** El nivel que se le da a un consumo en curso sin ninguna lectura ni máximo anotado: se sabe que hay uno, no cuánto. */
    const val NIVEL_SIN_DATO = 3.0
    /** Nivel con el que la intensidad llega a la mitad (0.5). */
    const val NIVEL_MEDIO = 3.0
    /** Nivel con el que la intensidad llega al máximo (1); por encima se queda en 1. */
    const val NIVEL_PLENO = 8.0

    /**
     * Del nivel subjetivo (0 a 10) a la intensidad, en dos rectas: de 0 a 3 sube de 0 a 0.5 y de 3 a 8, de 0.5 a 1; por
     * encima de 8, 1. Un nivel negativo o no numérico da 0.
     */
    fun deNivel(nivel: Double): Float {
        if (nivel.isNaN() || nivel <= 0.0) return 0f
        val i = when {
            nivel <= NIVEL_MEDIO -> 0.5 * nivel / NIVEL_MEDIO
            nivel < NIVEL_PLENO -> 0.5 + 0.5 * (nivel - NIVEL_MEDIO) / (NIVEL_PLENO - NIVEL_MEDIO)
            else -> 1.0
        }
        return i.toFloat().coerceIn(0f, 1f)
    }

    /** ¿[c] es de una sustancia con nivel y está en curso a la hora [ahora] (en milisegundos)? */
    fun enCurso(c: Consumo, ahora: Long): Boolean {
        if (Catalogo.sustancia(c.sustancia)?.conNivel != true) return false
        return ahora >= Fechas.millis(c.inicio) && ahora <= Sustancias.finEfectivo(c, ahora)
    }

    /** La intensidad a la hora [ahora]: la del mayor nivel entre los consumos en curso; 0 si no hay ninguno. */
    fun de(consumos: List<Consumo>, ahora: Long): Float {
        var mayor: Double? = null
        for (c in consumos) {
            if (!enCurso(c, ahora)) continue
            val n = Sustancias.nivelEn(c, ahora, ahora) ?: NIVEL_SIN_DATO
            if (mayor == null || n > mayor) mayor = n
        }
        return mayor?.let { deNivel(it) } ?: 0f
    }

    /** ¿Hay algún consumo de una sustancia con nivel abierto (sin fin anotado)? Solo entonces el medidor vuelve a mirar cada 2 minutos. */
    fun hayAbierto(consumos: List<Consumo>, ahora: Long): Boolean = consumos.any { it.fin == null && enCurso(it, ahora) }
}

/**
 * Lo que leen los dibujos: la cara y la intensidad del tema en uso, como estado de instantánea.
 *
 * - [cara] (o [caraDe] para un tema concreto) y [intensidad] (la **animada**: la que se dibuja).
 * - [intensidadObjetivo]: a la que va la animación. La cara se decide con esta, así cambia en cuanto empieza o se cierra
 *   un consumo, y la animación de los colores la sigue.
 * - [forzar]: para las capturas y el arnés de escritorio, fija la cara y la intensidad (null deja la de verdad).
 *
 * Desde un `@Composable`, mejor [caraLocal] e [intensidadLocal], que respetan [SinIntensidad].
 */
object EstadoTema {
    private var caraForzada by mutableStateOf<Cara?>(null)
    private var intensidadForzada by mutableStateOf<Float?>(null)
    private var animada by mutableFloatStateOf(0f)
    private var objetivo by mutableFloatStateOf(0f)

    /**
     * Cuántas pantallas «en cero» ([SinIntensidad]) hay en composición. Mientras haya alguna, [intensidad] e
     * [intensidadObjetivo] dan 0 y la cara es la de la hora (o la forzada): así se enteran también los dibujos que leen
     * este objeto directamente (las piezas de `DisenoComun`, los muebles, `Estilo`), que no pueden leer
     * [LocalSinIntensidad]. Es un contador, no un sí o no, para que dos pantallas que se solapan en una transición no se
     * pisen al salir una.
     */
    private var pantallasEnCero by mutableIntStateOf(0)

    /** ¿Hay alguna pantalla en cero en composición? */
    val enCero: Boolean get() = pantallasEnCero > 0

    /** La intensidad que se dibuja, de 0 a 1, ya animada (4 s al subir, 10 s al caer a 0, en 12 pasos; ver [MedidorIntensidad]). 0 con una pantalla en cero. */
    val intensidad: Float get() = if (enCero) 0f else intensidadForzada ?: animada

    /** La intensidad a la que va la animación (la de [Intensidad.de] ahora mismo). 0 con una pantalla en cero. */
    val intensidadObjetivo: Float get() = if (enCero) 0f else intensidadForzada ?: objetivo

    /** La cara del tema en uso ([Estilo.tema]). */
    val cara: Cara get() = caraDe(Estilo.tema)

    /** La cara de [tema]: la forzada, si la hay; si no, la de la hora ([RelojModo.noche]) y la intensidad. */
    fun caraDe(tema: TemaId): Cara =
        if (!tema.conCaras) Cara.BASE else caraForzada ?: caraSegun(tema, RelojModo.noche, intensidadObjetivo)

    /** La cara de [tema] como si no hubiera ningún consumo en curso: la de la hora (o la forzada). La usa [SinIntensidad]. */
    fun caraSinIntensidad(tema: TemaId): Cara =
        if (!tema.conCaras) Cara.BASE else caraForzada ?: caraSegun(tema, RelojModo.noche, 0f)

    /**
     * Para las capturas y el arnés: fija la [cara] y la [intensidad] (de 0 a 1). Con null, cada una vuelve a ser la de
     * verdad. Solo afecta a los temas con caras ([TemaId.conCaras]).
     */
    fun forzar(cara: Cara?, intensidad: Float?) {
        caraForzada = cara
        intensidadForzada = intensidad?.let { if (it.isNaN()) 0f else it.coerceIn(0f, 1f) }
    }

    /** [SinIntensidad] avisa aquí al entrar y al salir de la composición. */
    internal fun entrarEnCero() { pantallasEnCero += 1 }
    internal fun salirDeCero() { pantallasEnCero = (pantallasEnCero - 1).coerceAtLeast(0) }

    /** El medidor pone aquí la intensidad a la que va y la animada. */
    internal fun poner(objetivo: Float, animada: Float) {
        this.objetivo = if (objetivo.isNaN()) 0f else objetivo.coerceIn(0f, 1f)
        this.animada = if (animada.isNaN()) 0f else animada.coerceIn(0f, 1f)
    }

    /**
     * La intensidad en nueve escalones (0, 1/8, …, 1), para la clave de lo que se guarda en caché: `remember(oscuro, cara,
     * EstadoTema.escalon(i)) { … }`. Lo que se mueve se dibuja encima en cada cuadro.
     */
    fun escalon(i: Float): Float = if (i.isNaN()) 0f else (i.coerceIn(0f, 1f) * 8f).roundToInt() / 8f
}

/** Verdad dentro de [SinIntensidad]: los dibujos leen la intensidad 0 y la cara de la hora. */
val LocalSinIntensidad = compositionLocalOf { false }

/**
 * Una cara fija para un trozo de la interfaz que no sigue ni la hora ni los consumos: los fondos del teléfono, que se
 * eligen a mano (tanda 5). Dentro, [caraLocal] da esta cara y [intensidadLocal] da 0. Null (lo normal): la de [EstadoTema].
 */
val LocalCaraFija = compositionLocalOf<Cara?> { null }

/** La cara que deben pintar los componentes del tema aquí: la fija ([LocalCaraFija]), la de la hora dentro de [SinIntensidad] o la de [EstadoTema]. */
@Composable
fun caraLocal(): Cara = LocalCaraFija.current
    ?: if (LocalSinIntensidad.current) EstadoTema.caraSinIntensidad(Estilo.tema) else EstadoTema.cara

/** La intensidad que deben pintar los componentes del tema aquí: la de [EstadoTema], o 0 con una cara fija o dentro de [SinIntensidad]. */
@Composable
fun intensidadLocal(): Float = if (LocalSinIntensidad.current || LocalCaraFija.current != null) 0f else EstadoTema.intensidad

/**
 * Como [intensidadLocal], pero para leerla **al dibujar** (17 nov 2026): devuelve una función que la da en el momento del
 * trazo. Mientras la intensidad anima (4 s al subir, 10 s al caer: [MedidorIntensidad]), solo se vuelve a trazar lo que la
 * usa, sin recomponer la pieza en cada cuadro como pasaba al leerla al componer.
 */
@Composable
fun intensidadAlDibujar(): () -> Float {
    val cero = LocalSinIntensidad.current || LocalCaraFija.current != null
    return remember(cero) { { if (cero) 0f else EstadoTema.intensidad } }
}

/**
 * Como [intensidadLocal], en 32 escalones, para lo que no se puede leer al dibujar (el color de un texto): la pieza se
 * recompone solo al pasar de un escalón a otro (unas 32 veces en una animación entera, no en cada cuadro). Un escalón de
 * 1/32 de la intensidad no se distingue a la vista.
 */
@Composable
fun intensidadEnEscalones(): Float {
    val cero = LocalSinIntensidad.current || LocalCaraFija.current != null
    val i by remember { derivedStateOf { (EstadoTema.intensidad * 32f).roundToInt() / 32f } }
    return if (cero) 0f else i
}

/**
 * Las pantallas que se dibujan siempre como si no hubiera ningún consumo en curso (la guía, «La regla de legibilidad»,
 * punto 4): las del seguimiento privado y las de registrar sustancias. Dentro, [intensidadLocal] da 0 y [caraLocal] la cara
 * de la hora.
 *
 * Además, mientras esté en composición, pone [EstadoTema] «en cero» (un contador): toda la app lee intensidad 0 y la cara
 * de la hora, también los dibujos que leen [EstadoTema] directamente (las piezas de `DisenoComun`, los muebles, `Estilo`)
 * y el esquema de `TemaVestigium`. El contador sube después de la primera composición (en un efecto), así que en ese
 * primer cuadro, si la cara de la hora no es la que lleva la app (un consumo de día), el contenido va con el esquema, la
 * letra y las esquinas de la cara de la hora; desde el segundo cuadro las dos coinciden. Los otros temas no tienen
 * intensidad ni caras: para ellos no cambia nada.
 */
@Composable
fun SinIntensidad(contenido: @Composable () -> Unit) {
    val tema = Estilo.tema
    val deLaHora = EstadoTema.caraSinIntensidad(tema)
    DisposableEffect(Unit) {
        EstadoTema.entrarEnCero()
        onDispose { EstadoTema.salirDeCero() }
    }
    CompositionLocalProvider(LocalSinIntensidad provides true) {
        if (deLaHora == EstadoTema.caraDe(tema)) contenido()
        else {
            val oscuro = modoOscuro(tema)
            MaterialTheme(colorScheme = esquemaDe(tema, oscuro, deLaHora), typography = tipoDe(tema, oscuro, deLaHora),
                shapes = formasDe(tema, deLaHora), content = contenido)
        }
    }
}

/** Cada cuánto vuelve a calcular el medidor mientras hay un consumo abierto: el nivel interpolado cambia despacio. */
private const val MS_ENTRE_MEDIDAS = 2 * 60_000L
/** Lo que tarda la animación al subir o al cambiar, y al caer a 0 (cuando se cierra el consumo). */
private const val MS_SUBE = 4_000
private const val MS_CAE = 10_000

/**
 * Pone al día [EstadoTema] con la intensidad de los consumos anotados. Va en `TemaVestigium` solo si el tema tiene
 * intensidad ([TemaId.conIntensidad]).
 * - Recalcula cuando cambia `Repo.consumos` (empezar, leer o cerrar un consumo pasa por `Repo.guardarConsumo`, que
 *   reemplaza la lista), al volver a la app (`ON_RESUME`) y, **solo si hay un consumo abierto**, cada 2 minutos. Con nada
 *   abierto no hay ningún temporizador.
 * - Anima hacia el valor nuevo: 4 s al subir o al cambiar, 10 s al caer a 0, en 12 pasos ([enPasos]).
 */
@Composable
internal fun MedidorIntensidad() {
    val consumos = Repo.consumos
    var ahora by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val ciclo = LocalLifecycleOwner.current
    DisposableEffect(ciclo) {
        val obs = LifecycleEventObserver { _, e -> if (e == Lifecycle.Event.ON_RESUME) ahora = System.currentTimeMillis() }
        ciclo.lifecycle.addObserver(obs)
        onDispose { ciclo.lifecycle.removeObserver(obs) }
    }
    // Si el medidor sale (se cambió a un tema sin intensidad), no deja una intensidad vieja en el estado.
    DisposableEffect(Unit) { onDispose { EstadoTema.poner(0f, 0f) } }
    // Al cambiar la lista, se mide con la hora de ese momento (no con la de la última medida).
    val objetivo = remember(consumos, ahora) { Intensidad.de(consumos, maxOf(ahora, System.currentTimeMillis())) }
    val abierto = remember(consumos, ahora) { Intensidad.hayAbierto(consumos, maxOf(ahora, System.currentTimeMillis())) }
    LaunchedEffect(abierto) {
        if (!abierto) return@LaunchedEffect
        while (true) {
            kotlinx.coroutines.delay(MS_ENTRE_MEDIDAS)
            ahora = System.currentTimeMillis()
        }
    }
    val anim = remember { Animatable(EstadoTema.intensidad) }
    LaunchedEffect(objetivo) {
        val cae = objetivo <= 0f && anim.value > 0f
        val desde = anim.value
        EstadoTema.poner(objetivo, desde)
        anim.animateTo(objetivo, tween(if (cae) MS_CAE else MS_SUBE, easing = if (cae) LinearOutSlowInEasing else FastOutSlowInEasing)) {
            EstadoTema.poner(objetivo, enPasos(desde, objetivo, value))
        }
        EstadoTema.poner(objetivo, objetivo)
    }
}

/**
 * En cuántos pasos llega a [EstadoTema] la animación de la intensidad (19 nov 2026). Todo lo de Psicodelia lee la
 * intensidad al dibujar (los muebles, el estante, las piezas comunes, el gato): si cambiara en cada cuadro, durante los 4 s
 * de subida o los 10 s de bajada se volvería a trazar todo 60 veces por segundo. En 12 pasos se vuelve a trazar 12 veces;
 * el principio y el final son los mismos.
 */
private const val PASOS_ANIMACION = 12

/** El valor [v] de la animación de [desde] a [hasta], llevado al paso más cercano de los [PASOS_ANIMACION] del camino. */
internal fun enPasos(desde: Float, hasta: Float, v: Float): Float {
    val tramo = hasta - desde
    if (kotlin.math.abs(tramo) < 1e-4f || v.isNaN()) return hasta
    val k = ((v - desde) / tramo * PASOS_ANIMACION).roundToInt().coerceIn(0, PASOS_ANIMACION)
    return if (k == PASOS_ANIMACION) hasta else desde + tramo * k / PASOS_ANIMACION
}
