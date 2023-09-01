package com.dailystudio.music.midi

object MidiConstants {

    const val DEFAULT_SONG_BARS = 10
    const val DEFAULT_SONG_BEAT_PER_BAR = 4
    const val DEFAULT_SONG_SLITS_PER_BAR = 2
    const val DEFAULT_SONG_TEMPO = 120

    enum class PitchName(val offset: Int) {
        C(0),
        SharpC(1),
        D(2),
        FlatE(3),
        E(4),
        F(5),
        SharpF(6),
        G(7),
        FlatA(8),
        A(9),
        FlatB(10),
        B(11);


        companion object {
            fun valueOf(offset: Int) = values().find { it.offset == offset }
        }

    }

    enum class Octave(val leadPitch: Int) {

        C0(0),
        C1(12),
        C2(24),
        C3(36),
        C4(48),
        C5(60),
        C6(72),
        C7(84),
        C8(96),
        C9(108),
        C10(120),

        SharpC0(C0.leadPitch + PitchName.SharpC.offset),
        SharpC1(C1.leadPitch + PitchName.SharpC.offset),
        SharpC2(C2.leadPitch + PitchName.SharpC.offset),
        SharpC3(C3.leadPitch + PitchName.SharpC.offset),
        SharpC4(C4.leadPitch + PitchName.SharpC.offset),
        SharpC5(C5.leadPitch + PitchName.SharpC.offset),
        SharpC6(C6.leadPitch + PitchName.SharpC.offset),
        SharpC7(C7.leadPitch + PitchName.SharpC.offset),
        SharpC8(C8.leadPitch + PitchName.SharpC.offset),
        SharpC9(C9.leadPitch + PitchName.SharpC.offset),
        SharpC10(C10.leadPitch + PitchName.SharpC.offset),

        D0(C0.leadPitch + PitchName.D.offset),
        D1(C1.leadPitch + PitchName.D.offset),
        D2(C2.leadPitch + PitchName.D.offset),
        D3(C3.leadPitch + PitchName.D.offset),
        D4(C4.leadPitch + PitchName.D.offset),
        D5(C5.leadPitch + PitchName.D.offset),
        D6(C6.leadPitch + PitchName.D.offset),
        D7(C7.leadPitch + PitchName.D.offset),
        D8(C8.leadPitch + PitchName.D.offset),
        D9(C9.leadPitch + PitchName.D.offset),
        D10(C10.leadPitch + PitchName.D.offset),

        FlatE0(C0.leadPitch + PitchName.FlatE.offset),
        FlatE1(C1.leadPitch + PitchName.FlatE.offset),
        FlatE2(C2.leadPitch + PitchName.FlatE.offset),
        FlatE3(C3.leadPitch + PitchName.FlatE.offset),
        FlatE4(C4.leadPitch + PitchName.FlatE.offset),
        FlatE5(C5.leadPitch + PitchName.FlatE.offset),
        FlatE6(C6.leadPitch + PitchName.FlatE.offset),
        FlatE7(C7.leadPitch + PitchName.FlatE.offset),
        FlatE8(C8.leadPitch + PitchName.FlatE.offset),
        FlatE9(C9.leadPitch + PitchName.FlatE.offset),
        FlatE10(C10.leadPitch + PitchName.FlatE.offset),

        E0(C0.leadPitch + PitchName.E.offset),
        E1(C1.leadPitch + PitchName.E.offset),
        E2(C2.leadPitch + PitchName.E.offset),
        E3(C3.leadPitch + PitchName.E.offset),
        E4(C4.leadPitch + PitchName.E.offset),
        E5(C5.leadPitch + PitchName.E.offset),
        E6(C6.leadPitch + PitchName.E.offset),
        E7(C7.leadPitch + PitchName.E.offset),
        E8(C8.leadPitch + PitchName.E.offset),
        E9(C9.leadPitch + PitchName.E.offset),
        E10(C10.leadPitch + PitchName.E.offset),

        F0(C0.leadPitch + PitchName.F.offset),
        F1(C1.leadPitch + PitchName.F.offset),
        F2(C2.leadPitch + PitchName.F.offset),
        F3(C3.leadPitch + PitchName.F.offset),
        F4(C4.leadPitch + PitchName.F.offset),
        F5(C5.leadPitch + PitchName.F.offset),
        F6(C6.leadPitch + PitchName.F.offset),
        F7(C7.leadPitch + PitchName.F.offset),
        F8(C8.leadPitch + PitchName.F.offset),
        F9(C9.leadPitch + PitchName.F.offset),
        F10(C10.leadPitch + PitchName.F.offset),

        SharpF0(C0.leadPitch + PitchName.SharpF.offset),
        SharpF1(C1.leadPitch + PitchName.SharpF.offset),
        SharpF2(C2.leadPitch + PitchName.SharpF.offset),
        SharpF3(C3.leadPitch + PitchName.SharpF.offset),
        SharpF4(C4.leadPitch + PitchName.SharpF.offset),
        SharpF5(C5.leadPitch + PitchName.SharpF.offset),
        SharpF6(C6.leadPitch + PitchName.SharpF.offset),
        SharpF7(C7.leadPitch + PitchName.SharpF.offset),
        SharpF8(C8.leadPitch + PitchName.SharpF.offset),
        SharpF9(C9.leadPitch + PitchName.SharpF.offset),
        SharpF10(C10.leadPitch + PitchName.SharpF.offset),

        G0(C0.leadPitch + PitchName.G.offset),
        G1(C1.leadPitch + PitchName.G.offset),
        G2(C2.leadPitch + PitchName.G.offset),
        G3(C3.leadPitch + PitchName.G.offset),
        G4(C4.leadPitch + PitchName.G.offset),
        G5(C5.leadPitch + PitchName.G.offset),
        G6(C6.leadPitch + PitchName.G.offset),
        G7(C7.leadPitch + PitchName.G.offset),
        G8(C8.leadPitch + PitchName.G.offset),
        G9(C9.leadPitch + PitchName.G.offset),
        G10(C10.leadPitch + PitchName.G.offset),

        FlatA0(C0.leadPitch + PitchName.FlatA.offset),
        FlatA1(C1.leadPitch + PitchName.FlatA.offset),
        FlatA2(C2.leadPitch + PitchName.FlatA.offset),
        FlatA3(C3.leadPitch + PitchName.FlatA.offset),
        FlatA4(C4.leadPitch + PitchName.FlatA.offset),
        FlatA5(C5.leadPitch + PitchName.FlatA.offset),
        FlatA6(C6.leadPitch + PitchName.FlatA.offset),
        FlatA7(C7.leadPitch + PitchName.FlatA.offset),
        FlatA8(C8.leadPitch + PitchName.FlatA.offset),
        FlatA9(C9.leadPitch + PitchName.FlatA.offset),
        FlatA10(C10.leadPitch + PitchName.FlatA.offset),

        A0(C0.leadPitch + PitchName.A.offset),
        A1(C1.leadPitch + PitchName.A.offset),
        A2(C2.leadPitch + PitchName.A.offset),
        A3(C3.leadPitch + PitchName.A.offset),
        A4(C4.leadPitch + PitchName.A.offset),
        A5(C5.leadPitch + PitchName.A.offset),
        A6(C6.leadPitch + PitchName.A.offset),
        A7(C7.leadPitch + PitchName.A.offset),
        A8(C8.leadPitch + PitchName.A.offset),
        A9(C9.leadPitch + PitchName.A.offset),
        A10(C10.leadPitch + PitchName.A.offset),

        FlatB0(C0.leadPitch + PitchName.FlatB.offset),
        FlatB1(C1.leadPitch + PitchName.FlatB.offset),
        FlatB2(C2.leadPitch + PitchName.FlatB.offset),
        FlatB3(C3.leadPitch + PitchName.FlatB.offset),
        FlatB4(C4.leadPitch + PitchName.FlatB.offset),
        FlatB5(C5.leadPitch + PitchName.FlatB.offset),
        FlatB6(C6.leadPitch + PitchName.FlatB.offset),
        FlatB7(C7.leadPitch + PitchName.FlatB.offset),
        FlatB8(C8.leadPitch + PitchName.FlatB.offset),
        FlatB9(C9.leadPitch + PitchName.FlatB.offset),
        FlatB10(C10.leadPitch + PitchName.FlatB.offset),

        B0(C0.leadPitch + PitchName.B.offset),
        B1(C1.leadPitch + PitchName.B.offset),
        B2(C2.leadPitch + PitchName.B.offset),
        B3(C3.leadPitch + PitchName.B.offset),
        B4(C4.leadPitch + PitchName.B.offset),
        B5(C5.leadPitch + PitchName.B.offset),
        B6(C6.leadPitch + PitchName.B.offset),
        B7(C7.leadPitch + PitchName.B.offset),
        B8(C8.leadPitch + PitchName.B.offset),
        B9(C9.leadPitch + PitchName.B.offset),
        B10(C10.leadPitch + PitchName.B.offset);

        companion object {
            fun valueOf(leadPitch: Int) = values().find { it.leadPitch == leadPitch }
        }

    }

    enum class Program(val id: Int) {

        /* PIANO */
        AcousticGrandPiano(1),
        BrightAcousticPiano(2),
        ElectricGrandPiano(3),
        HonkyTonkPiano(4),
        RhodesPiano(5),
        ChorusedPiano(6),
        Harpsichord(7),
        Clavinet(8),

        /* CHROMATIC PERCUSSION */
        Celesta(9),
        Glockenspiel(10),
        MusicBox(11),
        Vibraphone(12),
        Marimba(13),
        Xylophone(14),
        TubularBells(15),
        Dulcimer(16),

        /* ORGAN */
        HammondOrgan(17),
        PercussiveOrgan(18),
        RockOrgan(19),
        ChurchOrgan(20),
        ReedOrgan(21),
        Accordion(22),

        /* GUITAR */
        AcousticGuitarNylon(25),
        AcousticGuitarSteel(26),
        ElectricGuitarJazz(27),
        ElectricGuitarClean(28),
        ElectricGuitarMuted(29),
        OverdrivenGuitar(30),
        DistortionGuitar(31),
        GuitarHarmonics(32),

        /* BASS */
        AcousticBass(33),
        ElectricBassFinger(34),
        ElectricBassPick(35),
        FretlessBass(36),
        SlapBass1(37),
        SlapBass2(38),
        SynthBass1(39),
        SynthBass2(40),

        /* STRINGS */
        Violin(41),
        Viola(42),
        Cello(43),
        Contrabass(44),
        TremoloStrings(45),
        PizzicatoStrings (46),
        OrchestralHarp(47),
        Timpani(48),

        /* ENSEMBLE */
        ChoirAahs(53),
        VoiceOohs(54),

        /* BRASS */
        Trumpet(57),
        Trombone(58),
        Tuba(59),
        MutedTrumpet(60),
        FrenchHorn(61),
        BrassSection(62),
        SynthBrass1(63),
        SynthBrass2(64),

        /* REED */
        SopranoSax(65),
        SoAltoSax(66),
        TenorSax(67),
        BaritoneSax(68),
        Oboe(69),
        EnglishHorn(70),
        Bassoon(71),
        Clarinet(72),

        /* PIPE */
        Piccolo(73),
        Flute(74),
        Recorder(75),
        PanFlute(76),
        BottleBlow(77),
        Skakuhachi(78),
        Whistle(79),
        Ocarina(80),

        /* ETHNIC */
        Sitar(105),
        Banjo(106),
        Shamisen(107),
        Koto(108),
        Kalimba(109),
        Bagpipe(110),
        Fiddle(111),
        Shanai(112),

        /* PERCUSSIVE */
        Agogo(114),
        WoodBlock(116),
        TaikoDrum(117),
        MelodicTom(118),
        SynthDrum(119),

        UNUSED(128);

        companion object {
            fun valueOf(id: Int) = values().find { it.id == id }
        }

    }

    val OCTAVES = arrayOf(
        Octave.C0,
        Octave.C1,
        Octave.C2,
        Octave.C3,
        Octave.C4,
        Octave.C5,
        Octave.C6,
        Octave.C7,
        Octave.C8,
        Octave.C9,
    )

    val DEFAULT_PROGRAM = Program.ElectricGrandPiano

}