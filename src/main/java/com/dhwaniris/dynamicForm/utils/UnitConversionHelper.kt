package com.dhwaniris.dynamicForm.utils

/**
 * Created by ${Sahjad} on 5/27/2019.
 */

class UnitConversionHelper {


    /////  Length
    private val METRE = 0
    private val KILOMETRE = 1
    private val MILE = 2
    private val FOOT = 3
    private val YARD = 4


    private val METRE_TO_METRE = 1.0
    private val KILOMETRE_TO_METRE = 1000.0
    private val MILE_TO_METRE = 1609.34
    private val YARD_TO_METRE = 0.9144
    private val FOOT_TO_METRE = 0.3048


    fun convertLengthIntoMetre(value: Double, inputUnit: Int): Double {
        return when (inputUnit) {
            METRE -> value * METRE_TO_METRE
            KILOMETRE -> value * KILOMETRE_TO_METRE
            MILE -> value * MILE_TO_METRE
            FOOT -> value * FOOT_TO_METRE
            YARD -> value * YARD_TO_METRE
            else -> 0.0
        }

    }

    ////////////////


    ////Area
    private val SQ_METRE = 0
    private val ACRE = 1
    private val BHIGHA = 2
    private val KATHA = 3
    private val DHUR = 4
    private val SQ_FEET = 5
    private val SQ_YARD = 6


    private val SQ_METRE_TO_SQ_METRE = 1.0
    private val ACRE_TO_SQ_METRE = 4046.86
    private val BIGHA_TO_SQ_METRE = 202.343
    private val KATTHA_TO_SQ_METRE = 10.11715
    private val DHUR_TO_SQ_METRE = 0.5058575
    private val SQ_FEET_TO_SQ_METRE = 0.092903
    private val SQ_YARD_TO_SQ_METRE = 0.836127


    fun convertAreaIntoSqMetre(value: Double, inputUnit: Int): Double {
        return when (inputUnit) {
            SQ_METRE -> value * SQ_METRE_TO_SQ_METRE
            ACRE -> value * ACRE_TO_SQ_METRE
            BHIGHA -> value * BIGHA_TO_SQ_METRE
            KATHA -> value * KATTHA_TO_SQ_METRE
            DHUR -> value * DHUR_TO_SQ_METRE
            SQ_FEET -> value * SQ_FEET_TO_SQ_METRE
            SQ_YARD -> value * SQ_YARD_TO_SQ_METRE
            else -> 0.0
        }

    }

    //


    ///// temperature

    private val CELSIUS = 0
    private val FAHRENHEIT = 1

    fun convertTemperatureIntoCentigrade(value: Double, inputUnit: Int): Double {
        return when (inputUnit) {
            CELSIUS -> value
            FAHRENHEIT -> (value - 32) * 5 / 9
            else -> 0.0
        }

    }
    ////


    //// time

    private val SECOND = 0
    private val MINUTE = 1
    private val HOUR = 2

    fun convertTimeIntoSecond(value: Double, inputUnit: Int): Double {
        return when (inputUnit) {
            SECOND -> value
            MINUTE -> value * 60
            HOUR -> value * 60 * 60
            else -> 0.0
        }
    }


    ////////// mass

    private val KILO_GRAM = 0
    private val GRAM = 1
    private val METRIC_TONNE = 2
    private val POUND = 3


    private val KG_TO_KG = 0.0
    private val GRAM_TO_KG = 0.001
    private val MATRIC_TONNE_TO_KG = 1000.00
    private val POUND_TO_KG = 0.4536

    fun convertMassIntoKoloGram(value: Double, inputUnit: Int): Double {
        return when (inputUnit) {
            KILO_GRAM -> value * KG_TO_KG
            GRAM -> value * GRAM_TO_KG
            METRIC_TONNE -> value * MATRIC_TONNE_TO_KG
            POUND -> value * POUND_TO_KG
            else -> 0.0
        }

    }

    ////


    /////volume

    private val LITRE = 0
    private val MILLI_LITRE = 1
    private val CUBIC_LETRE = 2

    private val LITRE_To_LETRE = 1.0
    private val ML_TO_LITRE = 0.001
    private val CUBIC_LITRE_TO_LITRE = 1000

    fun convertVolumeIntoLitre(value: Double, inputUnit: Int): Double {
        return when (inputUnit) {
            LITRE -> value * LITRE_To_LETRE
            MILLI_LITRE -> value * ML_TO_LITRE
            CUBIC_LETRE -> value * CUBIC_LITRE_TO_LITRE
            else -> 0.0
        }

    }

    //

    //////speed

    private val KM_HR = 0
    private val M_SEC = 1
    fun convertSpeedIntokmhr(value: Double, inputUnit: Int): Double {
        return when (inputUnit) {
            KM_HR -> value
            M_SEC -> value * 18 / 5
            else -> 0.0
        }

    }


}