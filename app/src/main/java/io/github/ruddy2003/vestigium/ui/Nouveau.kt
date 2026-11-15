package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.Fechas
import java.time.LocalDate
import java.time.LocalTime

/**
 * Art nouveau (lote 10; `docs/diseno/temas/nouveau.md`): lo que el tema calcula para sus textos. Todo sale de la fecha
 * (y, el día de hoy, de la hora), así que el mismo día dice siempre lo mismo y no se guarda nada.
 * - **La página del almanaque:** el día en francés con su flor y lo que significa, de *Le Langage des fleurs* (1819),
 *   firmado «Charlotte de La Tour» (seudónimo). Recurso: el libro no trae una flor para cada día; la app las reparte por
 *   el año ([flor]). La lista y su fuente, en `docs/diseno/temas/fuentes_nouveau.md`.
 * - **La hora de París:** la hora legal de Francia de 1891 a 1911. Recurso: la app pone la hora del teléfono.
 * - **Los números:** el folio de una revista ilustrada, entre rayas: «– 17 –».
 */
object Nouveau {
    private val DIAS = listOf("lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche")
    private val MESES = listOf("janvier", "février", "mars", "avril", "mai", "juin", "juillet", "août", "septembre", "octobre", "novembre", "décembre")

    /** El día como en la página del almanaque: «samedi 17 octobre». */
    fun dia(d: LocalDate): String = "${DIAS[d.dayOfWeek.value - 1]} ${d.dayOfMonth} ${MESES[d.monthValue - 1]}"

    /**
     * La hora a la francesa de 1900, con la parte del día: «9 h 41 du matin», «2 h 05 de l'après-midi», «11 h 30 du
     * soir». De las 0 a las 5, de la noche; de las 6 a las 11, de la mañana; de las 12 a las 17, de la tarde; lo demás, de
     * la noche que empieza.
     */
    fun hora(h: Int, m: Int): String {
        val h12 = if (h % 12 == 0) 12 else h % 12
        val parte = when (h) {
            in 0..5 -> "de la nuit"
            in 6..11 -> "du matin"
            in 12..17 -> "de l'après-midi"
            else -> "du soir"
        }
        return "%d h %02d %s".format(h12, m, parte)
    }

    /** «samedi 17 octobre · Taminier — soyez mon appui»; el día de hoy, con la hora: «… · 9 h 41 du matin, heure de Paris». */
    fun fecha(iso: String, ahora: LocalTime? = null): String = runCatching {
        val d = Fechas.parse(iso)
        val f = flor(d)
        val base = "${dia(d)} · ${f.nombre} — ${f.sentido}"
        val hoy = ahora ?: if (iso == Fechas.diaLogico()) LocalTime.now() else null
        if (hoy != null) "$base · ${hora(hoy.hour, hoy.minute)}, heure de Paris" else base
    }.getOrDefault(iso)

    /** El mes como el almanaque: «Almanach de juillet», «Almanach d'avril 2026». */
    fun mes(n: Int, anio: Int? = null): String {
        val m = MESES[(n - 1).coerceIn(0, 11)]
        val de = if (m.first() in "aeiou") "d'" else "de "
        return "Almanach $de$m" + (anio?.let { " $it" } ?: "")
    }

    /** El folio de una revista ilustrada, entre rayas cortas: «– 17 –». Los años y lo que pasa de 999, tal cual. */
    fun numero(n: Int): String = if (n in 1..999) "– $n –" else n.toString()

    // ------------------------------------------------------------------------------------------------------------
    //  La flor del día
    // ------------------------------------------------------------------------------------------------------------

    /**
     * Una flor del libro de La Tour: su nombre como lo escribe el libro y lo que significa, con la primera letra en
     * minúscula para ir después de la raya (y con la tilde que el libro no pone en la «E» mayúscula: «élégance»).
     */
    data class Flor(val nombre: String, val sentido: String)

    /**
     * Las 275 plantas del «Dictionnaire des plantes avec leurs emblèmes» de *Le Langage des fleurs* (11.ª edición, Garnier
     * Frères, págs. 199–210), en el orden del libro. Fuente, lectura y reservas: `docs/diseno/temas/fuentes_nouveau.md`,
     * sección 3.
     */
    val FLORES = listOf(
        Flor("Absinthe", "absence"),
        Flor("Acacia", "amour platonique"),
        Flor("Acacia rose", "élégance"),
        Flor("Acanthe", "arts"),
        Flor("Achillée", "guerre"),
        Flor("Adonide", "douloureux souvenirs"),
        Flor("Adoxa", "faiblesse"),
        Flor("Agnus-castus", "froideur, vivre sans aimer"),
        Flor("Agrimoine", "reconnaissance"),
        Flor("Alisier", "accords"),
        Flor("Aloès", "amertume, douleur"),
        Flor("Alysse des rochers", "tranquillité"),
        Flor("Amandier", "étourderie"),
        Flor("Amarante", "immortalité"),
        Flor("Amaryllis", "fierté"),
        Flor("Ananas", "vous êtes parfaite"),
        Flor("Anémone", "abandon"),
        Flor("Anémone des prés", "maladie"),
        Flor("Ancolie", "folie"),
        Flor("Angélique", "inspiration"),
        Flor("Argentine", "naïveté"),
        Flor("Armoise", "bonheur"),
        Flor("Asphodèle", "mes regrets vous suivent au tombeau"),
        Flor("Aster à grandes fleurs", "arrière-pensée"),
        Flor("Aubépine", "espérance"),
        Flor("Baguenaudier", "amusement frivole"),
        Flor("Balsamine", "impatience"),
        Flor("Bardane", "importunité"),
        Flor("Basilic", "haine"),
        Flor("Baume de Judée", "guérison"),
        Flor("Belle-de-Jour", "coquetterie"),
        Flor("Belle-de-Nuit", "timidité"),
        Flor("Belvédère, ou Ansérine à balais", "je vous déclare la guerre"),
        Flor("Blé", "richesse"),
        Flor("Bluet", "délicatesse"),
        Flor("Bon-Henri", "bonté"),
        Flor("Bouquet", "galanterie"),
        Flor("Bourrache", "brusquerie"),
        Flor("Bouton de rose", "jeune fille"),
        Flor("Bouton de rose blanche", "cœur qui ignore l’amour"),
        Flor("Brise tremblante", "frivolité"),
        Flor("Bruyère", "solitude"),
        Flor("Buglosse", "mensonge"),
        Flor("Bugrane", "obstacle"),
        Flor("Buis", "stoïcisme"),
        Flor("Camara piquant", "rigueur"),
        Flor("Capillaire", "discrétion"),
        Flor("Centaurée, fleur du Grand Seigneur", "félicité"),
        Flor("Cerisier", "bonne éducation"),
        Flor("Champignon", "soupçon"),
        Flor("Chardon", "austérité"),
        Flor("Chardon à foulon", "misanthropie"),
        Flor("Charme", "ornement"),
        Flor("Châtaignier", "rendez-moi justice"),
        Flor("Chêne", "hospitalité"),
        Flor("Chèvrefeuille", "liens d’amour"),
        Flor("Chicorée", "frugalité"),
        Flor("Chou", "profit"),
        Flor("Circé", "sortilège"),
        Flor("Ciste", "sûreté"),
        Flor("Citronelle", "douleur"),
        Flor("Citrouille", "grosseur"),
        Flor("Clandestine", "amour caché"),
        Flor("Clématite", "artifice"),
        Flor("Colchique", "mes beaux jours sont passés"),
        Flor("Convolvulus de nuit", "nuit"),
        Flor("Cormier", "prudence"),
        Flor("Coquelicot", "consolation"),
        Flor("Coquelourde", "vous êtes sans prétention"),
        Flor("Coriandre", "mérite caché"),
        Flor("Coronille sauvage", "durée"),
        Flor("Coudrier", "réconciliation"),
        Flor("Couronne de roses", "récompense de la vertu"),
        Flor("Cuscute", "bassesse"),
        Flor("Cyprès", "deuil"),
        Flor("Datura", "charmes trompeurs"),
        Flor("Dictame de Crète", "naissance"),
        Flor("Dahlia", "ma reconnaissance surpasse vos soins"),
        Flor("Ébénier", "noirceur"),
        Flor("Églantier", "poésie"),
        Flor("Énothère à grandes fleurs", "inconstance"),
        Flor("Éphéméride de Virginie", "bonheur d’un inconstant"),
        Flor("Épines noires", "difficultés"),
        Flor("Épine-Vinette", "aigreur"),
        Flor("Érable", "réserve"),
        Flor("Fenouil", "force"),
        Flor("Feuilles mortes", "mélancolie"),
        Flor("Ficoïde glaciale", "vos feux me glacent"),
        Flor("Fleurs d’oranger", "chasteté"),
        Flor("Fougère", "sincérité"),
        Flor("Foulsapatte", "amour humble et malheureux"),
        Flor("Fraises", "bonté parfaite"),
        Flor("Fraxinelle", "feu"),
        Flor("Frêne", "grandeur"),
        Flor("Fumeterre", "fiel"),
        Flor("Fusain", "vos charmes sont tracés dans mon cœur"),
        Flor("Galéga", "raison"),
        Flor("Garance", "calomnie"),
        Flor("Genêt", "propreté"),
        Flor("Genette", "espérance trompeuse"),
        Flor("Genévrier", "asile, secours"),
        Flor("Géranium écarlate", "sottise"),
        Flor("Géranium rosé", "préférence"),
        Flor("Géranium triste", "esprit mélancolique"),
        Flor("Girofle", "dignité"),
        Flor("Giroflée de Mahon", "promptitude"),
        Flor("Giroflée de muraille", "fidèle au malheur"),
        Flor("Giroflée des jardins", "beauté durable"),
        Flor("Glycine", "votre amitié m’est douce et agréable"),
        Flor("Gouet commun", "ardeur"),
        Flor("Gouet Gobe-Mouche", "piège"),
        Flor("Grateron", "rudesse"),
        Flor("Grenade", "fatuité"),
        Flor("Grenadille bleue", "croyance"),
        Flor("Gui", "je surmonte tout"),
        Flor("Guimauve", "bienfaisance"),
        Flor("Gyroselle", "vous êtes ma divinité"),
        Flor("Hélénie", "pleurs"),
        Flor("Héliotrope", "enivrement : je vous aime"),
        Flor("Hépatique", "confiance"),
        Flor("Herbe, Gazon", "utilité"),
        Flor("Hêtre", "prospérité"),
        Flor("Hortensia", "vous êtes froide"),
        Flor("Houblon", "injustice"),
        Flor("Houx", "prévoyance"),
        Flor("Hyacinthe", "jeu"),
        Flor("Ibéride de Perse, Thlaspi vivace", "indifférence"),
        Flor("If", "tristesse"),
        Flor("Impériale", "puissance"),
        Flor("Ipomée écarlate, Jasmin rouge de l’Inde", "je m’attache à vous"),
        Flor("Iris", "message"),
        Flor("Iris-Flambe", "flamme"),
        Flor("Ivraie", "vice"),
        Flor("Jacinthe", "bienveillance"),
        Flor("Jasmin blanc", "amabilité"),
        Flor("Jasmin de Virginie", "séparation"),
        Flor("Jonc des champs", "docilité"),
        Flor("Jonquille", "désir"),
        Flor("Jusquiame", "défaut"),
        Flor("Laitue", "refroidissement"),
        Flor("Lauréole, ou Bois-Gentil", "coquetterie, désir de plaire"),
        Flor("Laurier", "gloire"),
        Flor("Laurier-Amandier", "perfidie"),
        Flor("Lavande", "méfiance"),
        Flor("Lianes", "nœuds"),
        Flor("Lierre", "amitié"),
        Flor("Lilas", "première émotion d’amour"),
        Flor("Lilas blanc", "jeunesse"),
        Flor("Lin", "je sens vos bienfaits"),
        Flor("Lis", "majesté"),
        Flor("Liseron des champs", "humilité"),
        Flor("Luzerne", "vie"),
        Flor("Mancenillier", "fausseté"),
        Flor("Mandragore", "rareté"),
        Flor("Marguerite des prés", "j’y songerai"),
        Flor("Marguerite (petite) double", "je partage vos sentiments"),
        Flor("Marguerite (petite)", "innocence"),
        Flor("Marronnier d’Inde", "luxe"),
        Flor("Mélèze", "audace"),
        Flor("Mélisse Citronelle", "plaisanterie"),
        Flor("Menthe poivrée", "chaleur de sentiment"),
        Flor("Ményanthe", "calme, repos"),
        Flor("Mignardise", "enfantillage"),
        Flor("Miroir de Vénus", "flatterie"),
        Flor("Momordique piquante", "critique"),
        Flor("Monceau de fleurs", "nous mourrons ensemble"),
        Flor("Morelle douce-amère", "vérité"),
        Flor("Mouron", "rendez-vous"),
        Flor("Mousse", "amour maternel"),
        Flor("Mufle-de-Veau", "présomption"),
        Flor("Muguet", "retour du bonheur"),
        Flor("Mûrier blanc", "sagesse"),
        Flor("Mûrier à fruit noir", "je ne vous survivrai pas"),
        Flor("Myosotis", "souvenez-vous de moi ; ne m’oubliez pas"),
        Flor("Myrobolan", "privation"),
        Flor("Myrte", "amour"),
        Flor("Myrtile", "trahison"),
        Flor("Narcisse", "égoïsme"),
        Flor("Nymphæ-Lotus", "éloquence"),
        Flor("Œillet", "amour vif et pur"),
        Flor("Œillet de poëte", "finesse"),
        Flor("Œillet jaune", "dédain"),
        Flor("Olivier", "paix"),
        Flor("Ophrise-Araignée", "adresse"),
        Flor("Ophrise-Mouche", "erreur"),
        Flor("Oranger", "générosité"),
        Flor("Ornithogale, épi de la Vierge", "pureté"),
        Flor("Ortie", "cruauté"),
        Flor("Osier", "franchise"),
        Flor("Osmonde", "rêverie"),
        Flor("Oublie, grande Lunaire", "oubli"),
        Flor("Oxalis", "joie"),
        Flor("Paille brisée", "rupture"),
        Flor("Paille entière", "union"),
        Flor("Patience", "patience"),
        Flor("Pavot blanc", "sommeil du cœur"),
        Flor("Perce-neige", "consolation"),
        Flor("Persil", "festin"),
        Flor("Pervenche", "doux souvenirs"),
        Flor("Peuplier blanc", "temps"),
        Flor("Peuplier noir", "courage"),
        Flor("Peuplier-Tremble", "gémissement"),
        Flor("Pied-d’Alouette", "légèreté"),
        Flor("Pin", "hardiesse"),
        Flor("Pissenlit", "oracle"),
        Flor("Pivoine", "honte"),
        Flor("Platane", "génie"),
        Flor("Polémoine bleue", "rupture"),
        Flor("Polygala", "ermitage"),
        Flor("Pomme de terre", "bienfaisance"),
        Flor("Pommier (la fleur)", "préférence"),
        Flor("Primevère", "première jeunesse"),
        Flor("Prunier", "tenez vos promesses"),
        Flor("Prunier sauvage", "indépendance"),
        Flor("Pyramidale bleue", "constance"),
        Flor("Quinte-Feuille", "fille chérie"),
        Flor("Roquette", "je brûle"),
        Flor("Reine-Marguerite", "variété"),
        Flor("Renoncule asiatique", "vous êtes brillante d’attraits"),
        Flor("Renoncule scélérate", "ingratitude"),
        Flor("Réséda", "vos qualités surpassent vos charmes"),
        Flor("Romarin", "votre présence me ranime"),
        Flor("Ronces", "envie"),
        Flor("Rose", "beauté"),
        Flor("Rose à cent feuilles", "grâces"),
        Flor("Rose blanche", "silence"),
        Flor("Rose capucine", "éclat"),
        Flor("Rose blanche avec une rose rouge", "feu du cœur"),
        Flor("Rose des Quatre-Saisons", "beauté toujours nouvelle"),
        Flor("Rose jaune", "infidélité"),
        Flor("Rose mousseuse", "amour, volupté"),
        Flor("Rose musquée", "beauté capricieuse"),
        Flor("Rose pompon", "gentillesse"),
        Flor("Rose simple", "simplicité"),
        Flor("Rose trémière", "fécondité"),
        Flor("Rose (une feuille)", "jamais je n’importune"),
        Flor("Rose de Gueldre", "bonne nouvelle"),
        Flor("Rosier au milieu d’une touffe de gazon", "il y a tout à gagner avec la bonne compagnie"),
        Flor("Roseaux plumeux", "indiscrétion"),
        Flor("Roseaux", "musique"),
        Flor("Rue sauvage", "mœurs"),
        Flor("Safran", "n’abusez pas"),
        Flor("Sainfoin oscillant", "agitation"),
        Flor("Salicaire", "prétention"),
        Flor("Sapin", "élévation"),
        Flor("Sardonie", "ironie"),
        Flor("Sauge (petite)", "estime"),
        Flor("Saule de Babylone (ou pleureur)", "mélancolie"),
        Flor("Sensitive", "pudeur"),
        Flor("Serpentaire", "horreur"),
        Flor("Soleil", "fausses richesses"),
        Flor("Souci", "chagrin, peine"),
        Flor("Souci et Cyprès réunis", "désespoir"),
        Flor("Souci pluviatile", "présage"),
        Flor("Spirée ulmaire", "inutilité"),
        Flor("Staticé maritime", "sympathie"),
        Flor("Stramoine commune", "déguisement"),
        Flor("Syringa", "amour fraternel"),
        Flor("Taminier", "soyez mon appui"),
        Flor("Thym", "activité"),
        Flor("Tlaspi", "indifférence"),
        Flor("Tilleul", "amour conjugal"),
        Flor("Tremelle Nostoc", "résistance"),
        Flor("Troëne", "défense"),
        Flor("Truffe", "surprise"),
        Flor("Tubéreuse", "volupté"),
        Flor("Tulipe", "déclaration d’amour"),
        Flor("Tussilage odorant", "on vous rendra justice"),
        Flor("Valériane rouge", "facilité"),
        Flor("Véronique", "fidélité"),
        Flor("Verveine", "enchantement"),
        Flor("Vigne", "ivresse"),
        Flor("Violette", "modestie"),
        Flor("Violette blanche", "candeur"),
        Flor("Viorne-Laurier-Tin", "je meurs si on me néglige"),
    )

    /** El paso con que se recorre la lista: 97 no tiene divisores comunes con 275, así que salen todas antes de repetirse. */
    private const val PASO = 97

    /**
     * La flor del día. Recurso: el libro no da una flor por día; el día N del año toma la flor (N − 1) · 97 de la lista
     * (contando desde cero y dando la vuelta), así que el mismo día tiene siempre la misma flor, las 275 salen una vez
     * antes de repetirse y dos días seguidos no caen en la misma letra.
     */
    fun flor(d: LocalDate): Flor = FLORES[((d.dayOfYear - 1) * PASO) % FLORES.size]
}
