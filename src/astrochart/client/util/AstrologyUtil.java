package astrochart.client.util;

import java.util.Date;
import astrochart.shared.EquinocticalShaddowLengths;
import astrochart.shared.enums.ZodiacSign;
import astrochart.shared.wrappers.AscendentAndOffset;
import astrochart.shared.wrappers.BodyPosition;
import astrochart.shared.wrappers.RiseAndSet;


public class AstrologyUtil {
    public static final double EARTH_INCLINATION_J2000 = 23.4392911D; // in degrees
    public static final double EARTH_INCLINATION_J1950 = 23.4457889D; // in degrees
    public static final double EARTH_AXIS_TILT = 23.45D; // TODO change to EARTH_AXIS_TILT_PRECISE
    public static final double EARTH_AXIS_TILT_PRECISE = 23.439D; // TODO use EARTH_INCLINATION_J2000
    public static final double EARTH_AXIS_TILT_DAILY_CHANGE = 0.0000004D;
    public static final double PERIHELION_TO_VENERAL_EQUINOX_DEGREES = 102.9372D;
    public static final double LONG_ABERRATION = 0.9856474D;
    public static final double ANGLE_TO_SUN_FROM_PERIHELION = 357.528D; // TODO change to ANGLE_TO_SUN_FROM_PERIHELION_PRECISE
    public static final double ANGLE_TO_SUN_FROM_PERIHELION_PRECISE = 357.5291D;
    public static final double SOLAR_MEAN_DAILY_ANOMALISTIC_MOTION = 0.9856003D; // TODO change to SOLAR_MEAN_DAILY_ANOMALISTIC_MOTION_PRECISE
    public static final double SOLAR_MEAN_DAILY_ANOMALISTIC_MOTION_PRECISE = 0.98560028D;
    public static final double SOLAR_ELEVATION_AT_SUNRISE_AND_SUNSET = -0.83D;
    public static final double EARTHS_SOLAR_TRANSIT_DATE = 0.0009D;
    public static final double EARTHS_SOLAR_TRANSIT_ECCENTRICAL_VARIATION = 0.0053D;
    public static final double EARTHS_SOLAR_TRANSIT_ECLIPTIC_OBLIQUITY_VARIATION = 0.0069D;
    public static final double EARTH_ECCENTRICITY = 0.0167D;
    public static final double SAEMUNDSON_REFRACTION_CONSTANT_1 = 1.02D;
    public static final double SAEMUNDSON_REFRACTION_CONSTANT_2 = 10.3D;
    public static final double SAEMUNDSON_REFRACTION_CONSTANT_3 = 5.11D;
    public static final double EQUATION_OF_CENTER_CONSTANT_1 = 1.9148D;
    public static final double EQUATION_OF_CENTER_CONSTANT_2 = 0.0200D;
    public static final double EQUATION_OF_CENTER_CONSTANT_3 = 0.0003D;

    private final EquinocticalShaddowLengths esl = new EquinocticalShaddowLengths();
    private final DateTimeUtil dateTimeUtil = new DateTimeUtil();

    private double meanSolarAnomaly = 0D;
    private double equationOfCenter = 0D;
    private double solarEclipticalLongitude = 0D;
    private double solarNoonTransit = 0D;

    public final double getMeanSolarNoonAnomaly() {
        return meanSolarAnomaly;
    }

    public final double getEquationOfCenter() {
        return equationOfCenter;
    }

    public final double getSolarEclipticalLongitude() {
        return solarEclipticalLongitude;
    }

    public final double getSolarNoonTransit() {
        return solarNoonTransit;
    }

    /**
     * Asserts the ascendent for the provided local SiderealTime at latitude
     * @param localSiderealTimeDeg Local siderealTime in degrees
     * @param latitude of the location
     * @return
     */
    @Deprecated
    public final ZodiacSign getTropicalAscendent(final double localSiderealTimeDeg, final double localLatitude) {
        double ascendentDegrees = Math.atan(
                (Math.cos(localSiderealTimeDeg) * -1)
                / (
                    (Math.sin(localSiderealTimeDeg) * Math.cos(EARTH_INCLINATION_J2000))
                    + (Math.tan(localLatitude) * Math.sin(EARTH_INCLINATION_J2000))
                ));
        if (ascendentDegrees < 0D || Math.cos(localSiderealTimeDeg) < 0D) {
            ascendentDegrees = ascendentDegrees + (Constants.DEGREES_IN_CIRCLE / 2);
        }
        return ZodiacSign.getSignAtDegree(ascendentDegrees);
    }

    public final long calculateRashimanaSeconds(final ZodiacSign sign, final double latitude) {
        final int roundedLat = Double.valueOf(latitude).intValue();
        final int charakhandas = esl.calculateCharakhandas(sign, roundedLat);
        long asus = 0L;
        if (sign.hasLongAscension()) {
            asus = sign.getRashimanaGroup().getEquatorialAsus() + charakhandas;
        } else {
            asus = sign.getRashimanaGroup().getEquatorialAsus() - charakhandas;
        }
        return dateTimeUtil.convertAsusToSiderealSeconds(asus);
    }

    public final double calculateEclipticLongitude(final double julianDayNumber) {
        return Constants.MEAN_LONGITUDE_OF_THE_SUN + (LONG_ABERRATION * julianDayNumber);
    }

    public final double calculateMeanAnomaly(final double julianDayNumber) {
        return ANGLE_TO_SUN_FROM_PERIHELION + (SOLAR_MEAN_DAILY_ANOMALISTIC_MOTION * julianDayNumber);
    }

    public final double calculateJulianCycle(final double julianDay, final double longitudeWest) {
        final double cycle = julianDay - DateTimeUtil.J_CONSTANT - (longitudeWest / Constants.DEGREES_IN_CIRCLE);
        return cycle + Constants.HALF_JULIAN_DAY;
    }

    public final double calculateAccurateJulianCycle(final double julianDay, final double longitude) {
        return Math.round((julianDay - Constants.JULIAN_DAY_AT_01_01_2000 - EARTHS_SOLAR_TRANSIT_DATE) - (longitude / Constants.DEGREES_IN_CIRCLE));
    }

    /**
     * calculates the solar meanpoint as ecliptic coordinate
     * with a numeric eccentricity of ~0.0167
     * @param eclipticLongitude
     * @param meanAnormaly
     * @return
     */
    public final double calculateEclipticalLength(final double eclipticLongitude, final double meanAnormaly) {
        double el = Math.toRadians(eclipticLongitude % Constants.DEGREES_IN_CIRCLE);
        double ma = Math.toRadians(meanAnormaly % Constants.DEGREES_IN_CIRCLE);
        final double result = el
            + ((2 * EARTH_ECCENTRICITY * Math.sin(ma))
            + ((5.0D / 4.0D) * Math.pow(EARTH_ECCENTRICITY, 2) * Math.sin(2 * EARTH_ECCENTRICITY))
            );
        return Math.toDegrees(result);
    }

    public final double calculateEclipticInclination(final double julianDayNumber) {
        return EARTH_AXIS_TILT_PRECISE - (EARTH_AXIS_TILT_DAILY_CHANGE * julianDayNumber);
    }

    /**
     * http://de.wikipedia.org/wiki/Sonnenstand#.C3.84quatorialkoordinaten_der_Sonne
     * @param eclipticInclination
     * @param eclipticalLength
     * @return
     */
    public final double calculateRightAscension(final double eclipticInclination, final double eclipticalLength) {
        double eclipticInclinationRadians = Math.toRadians(eclipticInclination);
        double eclipticalLengthRadians = Math.toRadians(eclipticalLength);
        final double argument = (Math.cos(eclipticInclinationRadians) * Math.sin(eclipticalLengthRadians)) / Math.cos(eclipticalLengthRadians);
        double result = Math.atan(argument);
        result = Math.toDegrees(result);
        if (result < 0.0D) {
            result = result + (Constants.DEGREES_IN_CIRCLE / 2);
        }
        return result;
    }

    public final double calculateDeclination(final double eclipticInclination, final double eclipticalLength) {
        final double result = Math.asin(Math.sin(Math.toRadians(eclipticInclination)) * Math.sin(Math.toRadians(eclipticalLength)));
        return Math.toDegrees(result);
    }

    public final double calculateVeneralEquinoxDegrees(final double starTimeDegrees, final double longitude) {
        return (starTimeDegrees + longitude) % Constants.DEGREES_IN_CIRCLE;
    }

    public final double calculateHourAngle(final double localJulianDeg, final double rightAscension) {
        return localJulianDeg - rightAscension;
    }

    public final double calculateAzimuth(final double hourAngle, final double latitude, final double declination) {
        final double divisor =
                (Math.cos(Math.toRadians(hourAngle)) * Math.sin(Math.toRadians(latitude)))
                - (Math.tan(Math.toRadians(declination)) * Math.cos(Math.toRadians(latitude)));
        double azimuth =
                Math.atan(Math.sin(Math.toRadians(hourAngle)) / divisor);
        azimuth = Math.toDegrees(azimuth);
        if (divisor < 0D) {
            azimuth = azimuth + (Constants.DEGREES_IN_CIRCLE / 2);
        }
        return azimuth;
    }

    public final double calculateAzimuthNorth(final double hourAngle, final double latitude, final double declination) {
        return calculateAzimuth(hourAngle, latitude, declination) + (Constants.DEGREES_IN_CIRCLE / 2);
    }

    public final double calculateHeightAngle(final double hourAngle, final double latitude, final double declination) {
        final double heightAngle =
                Math.asin((Math.cos(Math.toRadians(declination))
                        * Math.cos(Math.toRadians(hourAngle))
                        * Math.cos(Math.toRadians(latitude)))
                        + (Math.sin(Math.toRadians(declination))
                        * Math.sin(Math.toRadians(latitude))));
        return Math.toDegrees(heightAngle);
    }

    /**
     * Calculates the mean refraction for an object at the provided angle at 1010 mbar and 10� C.
     * http://de.wikipedia.org/wiki/Sonnenstand#Korrektur_der_H.C3.B6he_wegen_Refraktion
     * @param heightAngle
     * @return
     */
    private double calculateRefraction(final double heightAngle) {
        double term = heightAngle + (SAEMUNDSON_REFRACTION_CONSTANT_2 / (heightAngle + SAEMUNDSON_REFRACTION_CONSTANT_3));
        term = Math.toRadians(term);
        return SAEMUNDSON_REFRACTION_CONSTANT_1 / Math.tan(term);
    }

    public final double calculateRefractionDegrees(final double heightAngle) {
        return heightAngle + (calculateRefraction(heightAngle) / Constants.MINUTES_IN_DEGREE);
    }

    /**
     * http://de.wikipedia.org/wiki/Sonnenstand
     */
    public final BodyPosition calculateSunPosition(final Date date, final double latitude, final double longitude) {
        final double julianDay = dateTimeUtil.getJdTimeDate(date);
        final double n = dateTimeUtil.calculateJulianDayNumberFromJd(julianDay);
        final double l = calculateEclipticLongitude(n);
        final double g = calculateMeanAnomaly(n);
        final double lambda = calculateEclipticalLength(l, g); //solar ecliptic coordinate

        final double epsilon = calculateEclipticInclination(n); //ecliptic inclination

        final double rightAscension = calculateRightAscension(epsilon, lambda);
        final double declination = calculateDeclination(epsilon, lambda); //equatorical declination

        final double jdo = dateTimeUtil.getJulianDayNumberTimeDropped(date);
        final double t = dateTimeUtil.getJulianCenturiesSinceJ2000(jdo); //julian centuries since J2000.0

        final double decimalHours = dateTimeUtil.getDecimalHours(date);
        final double starTimeHours = dateTimeUtil.calculateStarTimeHours(t, decimalHours);
        final double starTimeDegrees = dateTimeUtil.convertHoursToDegree(starTimeHours);

        final double localJulianDeg = calculateVeneralEquinoxDegrees(starTimeDegrees, longitude); //veneral equinox degrees

        final double hourAngle = calculateHourAngle(localJulianDeg, rightAscension);

        final double azimuth = calculateAzimuth(hourAngle, latitude, declination);

        final double heightAngle = calculateHeightAngle(hourAngle, latitude, declination);
        final double refHeight = calculateRefractionDegrees(heightAngle);

        return new BodyPosition(azimuth, refHeight);
    }

    public final double calculateApproxSolarNoon(final double julianCycle, final double longitude) {
        return Constants.JULIAN_DAY_AT_01_01_2000 + EARTHS_SOLAR_TRANSIT_DATE + (longitude / Constants.DEGREES_IN_CIRCLE) + julianCycle;
    }

    public final double calculateAccurateMeanAnomaly(final double solarNoonApprox) {
        return (ANGLE_TO_SUN_FROM_PERIHELION_PRECISE
                + (SOLAR_MEAN_DAILY_ANOMALISTIC_MOTION_PRECISE
                * (solarNoonApprox - Constants.JULIAN_DAY_AT_01_01_2000)))
                % Constants.DEGREES_IN_CIRCLE;
    }

    /**
     * http://en.wikipedia.org/wiki/Sunrise_equation#Equation_of_Center
     * @param meanSolarAnomaly
     * @return
     */
    public final double calculateEquationOfCenter(final double meanSolarAnomaly) {
        return  (EQUATION_OF_CENTER_CONSTANT_1 * Math.sin(Math.toRadians(meanSolarAnomaly)))
                + (EQUATION_OF_CENTER_CONSTANT_2 * Math.sin(2D * Math.toRadians(meanSolarAnomaly)))
                + (EQUATION_OF_CENTER_CONSTANT_3 * Math.sin(3D * Math.toRadians(meanSolarAnomaly)));
    }

    public final double calculateSolarEclipticLongitude(final double meanSolarAnomaly, final double equationOfCenter) {
        return (meanSolarAnomaly + PERIHELION_TO_VENERAL_EQUINOX_DEGREES + equationOfCenter + (Constants.DEGREES_IN_CIRCLE / 2)) % Constants.DEGREES_IN_CIRCLE;
    }

    public final double calculateSolarNoonTransit(final double solarNoonApprox, final double meanSolarAnomaly, final double solarEclipticalLongitude) {
        return solarNoonApprox
                + (EARTHS_SOLAR_TRANSIT_ECCENTRICAL_VARIATION * Math.sin(Math.toRadians(meanSolarAnomaly)))
                - (EARTHS_SOLAR_TRANSIT_ECLIPTIC_OBLIQUITY_VARIATION * Math.sin(Math.toRadians(2D * solarEclipticalLongitude)));
    }

    public final double calculateSolarDeclination(final double solarEclipticalLongitude) {
        final double result = Math.asin(
                Math.sin(Math.toRadians(solarEclipticalLongitude))
                * Math.sin(Math.toRadians(EARTH_AXIS_TILT))
        );
        return Math.toDegrees(result);
    }

    public final double calculateAccurateMeanAnomalyRecursive(final double solarNoonApprox, final double oldMeanSolarAnomaly, final double solarNoonTransit) {
        final double meanSolarAnomaly = calculateAccurateMeanAnomaly(solarNoonTransit);
        if (oldMeanSolarAnomaly != meanSolarAnomaly) {
            final double equationOfCenter = calculateEquationOfCenter(meanSolarAnomaly);
            final double solarEclipticalLongitude = calculateSolarEclipticLongitude(meanSolarAnomaly, equationOfCenter);
            final double newSolarNoonTransit = calculateSolarNoonTransit(solarNoonApprox, meanSolarAnomaly, solarEclipticalLongitude);
            return calculateAccurateMeanAnomalyRecursive(solarNoonApprox, meanSolarAnomaly, newSolarNoonTransit);
        } else {
            this.meanSolarAnomaly = meanSolarAnomaly;
            this.equationOfCenter = calculateEquationOfCenter(meanSolarAnomaly);
            this.solarEclipticalLongitude = calculateSolarEclipticLongitude(meanSolarAnomaly, equationOfCenter);
            this.solarNoonTransit = calculateSolarNoonTransit(solarNoonApprox, meanSolarAnomaly, solarEclipticalLongitude);
            return meanSolarAnomaly;
        }
    }

    public final double calculateExactHourAngle(final double latitude, final double solarDeclination) {
        final double result =
                Math.acos(
                        (Math.sin(Math.toRadians(SOLAR_ELEVATION_AT_SUNRISE_AND_SUNSET))
                        - (Math.sin(Math.toRadians(latitude))
                        * Math.sin(Math.toRadians(solarDeclination))))
                        / (Math.cos(Math.toRadians(latitude))
                        * Math.cos(Math.toRadians(solarDeclination)))
                );
        return Math.toDegrees(result);
    }

    public final double calculateSolarNoonSecondApprox(final double hourAngle, final double longitude, final double julianCycle) {
        return Constants.JULIAN_DAY_AT_01_01_2000 + EARTHS_SOLAR_TRANSIT_DATE + ((hourAngle + longitude) / Constants.DEGREES_IN_CIRCLE) + julianCycle;
    }

    public final double calculateSunset(final double solarNoonSecondApprox, final double meanSolarAnomaly, final double solarEclipticalLongitude) {
        return solarNoonSecondApprox
                + (EARTHS_SOLAR_TRANSIT_ECCENTRICAL_VARIATION * Math.sin(Math.toRadians(meanSolarAnomaly)))
                - (EARTHS_SOLAR_TRANSIT_ECLIPTIC_OBLIQUITY_VARIATION * Math.sin(Math.toRadians(2D * solarEclipticalLongitude)));
    }

    public final double calculateSunrise(final double sunset, final double solarNoonTransit) {
        return solarNoonTransit - (sunset - solarNoonTransit);
    }

    /**
     * http://users.electromagnetic.net/bu/astro/sunrise-set.php
     * http://www.jgiesen.de/astro/astroJS/sunriseJS/index.htm
     * @param julianDay
     * @param latitude
     * @param longitude
     * @return
     */
    public final RiseAndSet calculateSunRiseAndSet(final Date date, final double latitude, final double longitude) {
        final double julianDate = dateTimeUtil.calculateJulianDate(date);
        final double julianCycle = calculateAccurateJulianCycle(julianDate, longitude);
        final double solarNoonApprox = calculateApproxSolarNoon(julianCycle, longitude);

        final double meanSolarAnomaly = calculateAccurateMeanAnomaly(solarNoonApprox);
        this.equationOfCenter = calculateEquationOfCenter(meanSolarAnomaly);
        this.solarEclipticalLongitude = calculateSolarEclipticLongitude(meanSolarAnomaly, equationOfCenter);
        this.solarNoonTransit = calculateSolarNoonTransit(solarNoonApprox, meanSolarAnomaly, solarEclipticalLongitude);

        final double accurateMeanSolarAnomaly = calculateAccurateMeanAnomalyRecursive(solarNoonApprox, meanSolarAnomaly, solarNoonTransit);

        final double solarDeclination = calculateSolarDeclination(solarEclipticalLongitude);
        final double hourAngle = calculateExactHourAngle(latitude, solarDeclination);
        final double solarNoonSecondApprox = calculateSolarNoonSecondApprox(hourAngle, longitude, julianCycle);

        final double sunset = calculateSunset(solarNoonSecondApprox, accurateMeanSolarAnomaly, getSolarEclipticalLongitude());
        final double sunrise = calculateSunrise(sunset, getSolarNoonTransit());
        final Date sunriseDate = dateTimeUtil.convertJdToDate(sunrise);
        final Date sunsetDate = dateTimeUtil.convertJdToDate(sunset);

        final RiseAndSet result = new RiseAndSet(sunriseDate, sunsetDate);
        return result;
    }

    /**
     * http://en.wikipedia.org/wiki/Sunrise_equation
     * @param julianDay
     * @param latitude
     * @param longitude
     * @return
     */
    @Deprecated
    public final double calculateSunrise(final double julianDay, final double latitude, final double longitude) {
        //FIXME
        final double currentJulianCycle = calculateJulianCycle(julianDay, longitude);
        final double approximateSolarNoon = DateTimeUtil.J_CONSTANT + (longitude / Constants.DEGREES_IN_CIRCLE) + currentJulianCycle;
        final double solarMeanAnomaly = (ANGLE_TO_SUN_FROM_PERIHELION_PRECISE
                + (SOLAR_MEAN_DAILY_ANOMALISTIC_MOTION_PRECISE * (approximateSolarNoon - Constants.JULIAN_DAY_AT_01_01_2000)))
                % Constants.DEGREES_IN_CIRCLE;
        final double equationOfCenter =
                (EQUATION_OF_CENTER_CONSTANT_1 * Math.sin(solarMeanAnomaly))
                + (EQUATION_OF_CENTER_CONSTANT_2 * Math.sin(2.0D * solarMeanAnomaly))
                + (EQUATION_OF_CENTER_CONSTANT_3 * Math.sin(3.0D * solarMeanAnomaly));
        final double eclipticLongitude = (solarMeanAnomaly + PERIHELION_TO_VENERAL_EQUINOX_DEGREES + equationOfCenter + (Constants.DEGREES_IN_CIRCLE / 2))
                % Constants.DEGREES_IN_CIRCLE;
        final double solarTransit =
                approximateSolarNoon
                + (EARTHS_SOLAR_TRANSIT_ECCENTRICAL_VARIATION * Math.sin(solarMeanAnomaly))
                - (EARTHS_SOLAR_TRANSIT_ECLIPTIC_OBLIQUITY_VARIATION * Math.sin(2.0D * eclipticLongitude)); //hour angle for solar noon
        final double sunDeclination = Math.asin(Math.sin(eclipticLongitude) * Math.sin(EARTH_AXIS_TILT));
        final double hourAngle =
                Math.acos(
                        (Math.sin(SOLAR_ELEVATION_AT_SUNRISE_AND_SUNSET) - (Math.sin(latitude) * Math.sin(sunDeclination)))
                        / (Math.cos(latitude) * Math.cos(sunDeclination))
                );
        final double sunSet = DateTimeUtil.J_CONSTANT + ((hourAngle + longitude) / Constants.DEGREES_IN_CIRCLE) + currentJulianCycle
                + (EARTHS_SOLAR_TRANSIT_ECCENTRICAL_VARIATION * Math.sin(solarMeanAnomaly))
                - (EARTHS_SOLAR_TRANSIT_ECLIPTIC_OBLIQUITY_VARIATION * Math.sin(2D * eclipticLongitude));
        final double sunRise = solarTransit - (sunSet - solarTransit);
        return sunRise;
    }

    /**
     * http://de.wikipedia.org/wiki/Aszendent_%28Astrologie%29#Berechnung_des_Aszendenten
     * @param localSiderealDegrees
     * @param latitude
     * @return
     */
    public final double calculateEclipticLongitudeJ2000Tangens(final double localSiderealDegrees, final double latitude) {
        final double tanLambda =
                (Math.cos(Math.toRadians(localSiderealDegrees)) * -1)
                    / ((Math.sin(Math.toRadians(localSiderealDegrees))
                    * Math.cos(Math.toRadians(EARTH_INCLINATION_J2000)))
                    + (Math.tan(Math.toRadians(latitude))
                    * Math.sin(Math.toRadians(EARTH_INCLINATION_J2000))));
        return tanLambda;
    }

    public final boolean returnDescendent(final double localSiderealDegrees) {
        return ((Constants.DEGREES_IN_CIRCLE / 4) < localSiderealDegrees && localSiderealDegrees < ((Constants.DEGREES_IN_CIRCLE / 4) * 3));
    }

    public final double getFirstHorizontEclipticSection(final double eclipticLongitudeTan) {
        final double eclipticLongitude = Math.atan(eclipticLongitudeTan);
        final double eclipticLongitudeDeg = Math.toDegrees(eclipticLongitude);
        double firstS1 = (eclipticLongitudeDeg + (Constants.DEGREES_IN_CIRCLE / 2)) % Constants.DEGREES_IN_CIRCLE;
        if (firstS1 >= (Constants.DEGREES_IN_CIRCLE / 2)) {
            firstS1 = firstS1 - (Constants.DEGREES_IN_CIRCLE / 2);
        }
        return firstS1;
    }

    public final double getSecondHorizontEclipticSection(final double eclipticLongitudeTan) {
        return getFirstHorizontEclipticSection(eclipticLongitudeTan) + (Constants.DEGREES_IN_CIRCLE / 2);
    }

    public final ZodiacSign returnSign(final double s1, final double s2, final boolean returnDescendent) {
        if (returnDescendent) {
            return ZodiacSign.getSignAtDegree(s2);
        } else {
            return ZodiacSign.getSignAtDegree(s1);
        }
    }

    public final double returnPosition(final double s1, final double s2, final boolean returnDescendent) {
        if (returnDescendent) {
            return s2 - returnSign(s1, s2, returnDescendent).getEclipticLongitude();
        } else {
            return s1 - returnSign(s1, s2, returnDescendent).getEclipticLongitude();
        }
    }

    public final AscendentAndOffset determineAscendent(final Date utcDate, final double longitude, final double latitude) {
        final double jd = dateTimeUtil.getJdTimeDate(utcDate);
        final double siderealDegrees = dateTimeUtil.getSiderealDegrees(jd);
        final double localSiderealDegrees = dateTimeUtil.getLocalSiderealDegrees(siderealDegrees, longitude);
        final double eclipticLongitudeTan = calculateEclipticLongitudeJ2000Tangens(localSiderealDegrees, latitude);
        final double s1 = getFirstHorizontEclipticSection(eclipticLongitudeTan);
        final double s2 = getSecondHorizontEclipticSection(eclipticLongitudeTan);
        final boolean returnDescendent = returnDescendent(localSiderealDegrees);

        final ZodiacSign sign = returnSign(s1, s2, returnDescendent);
        final double position = returnPosition(s1, s2, returnDescendent);

//      final double midheaven = calculateMidheaven(localSiderealDegrees);
//      final double midheaven = calculateMc(localSiderealDegrees);
        final double midheaven = 0.0D; //FIXME

        final AscendentAndOffset asc = new AscendentAndOffset(sign, position, midheaven);
        return asc;
    }

    /**
     * http://www.sismoloc.info/mc_east_and_ac_north.html
     * ARMC = ascensio recta medii coeli = right ascension of the local meridian
     * @return
     */
    @Deprecated
    public final double calculateMidheaven(final double armcDegree) {
        return Math.toDegrees(
                Math.atan(
                        Math.cos(Math.toRadians(EARTH_INCLINATION_J2000))
                        * (1D / Math.tan(Math.toRadians(armcDegree))) //=cot
                ) * -1D
            ) + ((Constants.DEGREES_IN_CIRCLE / 4) * 3);
    }

    /**
     * http://groups.google.com/group/alt.astrology.moderated/browse_thread/thread/5cf05d6fe8eabb52/17e0c6282d8c7dce?lnk=raot
     * @return
     */
    @Deprecated
    public final double calculateMc(final double ramc) {
        return Math.toDegrees(Math.atan(
                Math.tan(Math.toRadians(ramc))
                / Math.cos(Math.toRadians(EARTH_INCLINATION_J2000))
            )) + ((Constants.DEGREES_IN_CIRCLE / 4) * 3);
    }

    @Deprecated
    public final double calculateObliqueAscension(final double ascendentLongitude, final double longitude) {
        final double rightAscension =
                Math.toDegrees(Math.atan(
                        Math.cos(Math.toRadians(EARTH_INCLINATION_J2000))
                        * Math.tan(Math.toRadians(ascendentLongitude))
                ));
        final double d =
                Math.toDegrees(Math.asin(
                        Math.sin(Math.toRadians(EARTH_INCLINATION_J2000))
                        * Math.sin(Math.toRadians(ascendentLongitude))
                ));
        System.out.println("rightAscension: " + rightAscension);
        final double obliqueAscension =
                rightAscension
                - Math.toDegrees(Math.asin(
                        Math.tan(Math.toRadians(d))
                        * Math.tan(Math.toRadians(longitude))
                ));
        return obliqueAscension;
    }

}
