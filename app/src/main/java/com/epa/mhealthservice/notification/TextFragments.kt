package com.epa.mhealthservice.notification

object TextFragments {

    private val greetingChallenge: List<String> = listOf(

        "Hey! Bist du auf dem Heimweg? ",
        "Na, heute schon bewegt? ",
        "Na, sportlich unterwegs? ",
        "Hey, Sportsfreund! Alles fit? ",
        "Na, wo möchtest du hin? "
        )

    private val contentChallenge: List<String> = listOf(

        "Wenn du jetzt den Rest deines Weges zu Fuß nimmst, kannst du noch was Gutes für deinen Körper tun.",
        "Laufe doch den restlichen Weg einfach und werde fit.",
        "Wie wär's mit einem Spaziergang zu deinem Zielort?"
    )

    private val motivationChallenge: List<String> = listOf(

        " Dann bist du bald fit wie ein Turnschuh!",
        " Wie wär's?",
        " Ran an den Speck!",
        " Und los geht's!",
        " Auf die Plätze, fertig, LOS!"
    )



    private val greetingSummary: List<String> = listOf(

        "Na, Tag überstanden? ",
        "Hey! Tageswerk vollbracht? ",
        "Hey hey! Warst du heute sportlich unterwegs? "
    )

    private val motivationSummaryGood: List<String> = listOf(

        " Schon bald wirst du richtig fit sein!",
        " Mach weiter so!",
        " Du bist auf dem richtigen Weg!",
        " Behalte das bei und zeig's allen!"
    )

    private val motivationSummaryBad: List<String> = listOf(

        " Nächstes Mal schaffst du mehr!",
        " Du kannst bestimmt mehr aus dir rausholen!",
        " Beim nächsten Mal schaffst du sicher mehr!"
    )



    fun createChallengeText(): String{

        return greetingChallenge.random() + contentChallenge.random() + motivationChallenge.random()
    }

    fun createSummaryText(challengesCompleted: Int, steps: Int): String{

        if (challengesCompleted < 2){
            return greetingSummary.random() + "Du hast heute leider nur $challengesCompleted Herausforderungen abgeschlossen. Du hast heute $steps Schritte gemacht." + motivationSummaryBad.random()
        }
        if (challengesCompleted >= 2){
            return greetingSummary.random() + "Bravo, du hast heute $challengesCompleted Herausforderungen gemeistert und $steps  Schritte gemacht." + motivationSummaryGood.random()
        }
        return "Error"
    }
}