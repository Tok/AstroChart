package astrochart.client.util;

import java.util.Date;
import astrochart.shared.EquinocticalShaddowLengths;
import astrochart.shared.enums.ZodiacSign;
import astrochart.shared.wrappers.AscendentAndOffset;
import astrochart.shared.wrappers.BodyPosition;
import astrochart.shared.wrappers.RiseAndSet;


public class AstrologyUtil {
	final public static double EARTH_INCLINATION_J2000 = 23.4392911D; //in degrees
	final public static double EARTH_INCLINATION_J1950 = 23.4457889D; //in degrees

	final private EquinocticalShaddowLengths esl = new EquinocticalShaddowLengths();
	final private DateTimeUtil dateTimeUtil = new DateTimeUtil();

	private double meanSolarAnomaly = 0D;
	private double equationOfCenter = 0D;
	private double solarEclipticalLongitude = 0D;
	private double solarNoonTransit = 0D;

	public double getMeanSolarNoonAnomaly() {
    	return meanSolarAnomaly;
    }

	public double getEquationOfCenter() {
    	return equationOfCenter;
    }

	public double getSolarEclipticalLongitude() {
    	return solarEclipticalLongitude;
    }
	
	public double getSolarNoonTransit() {
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
				(Math.cos(localSiderealTimeDeg) * -1) /
				(
					(Math.sin(localSiderealTimeDeg) * Math.cos(EARTH_INCLINATION_J2000)) +
					(Math.tan(localLatitude) * Math.sin(EARTH_INCLINATION_J2000))
				));
		if (ascendentDegrees < 0D || Math.cos(localSiderealTimeDeg) < 0D) {
			ascendentDegrees = ascendentDegrees + 180D;
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
		
	public final double calculateEclipticLongitude(double julianDayNumber) {
	    return 280.460D + (0.9856474D * julianDayNumber);
    }

	public final double calculateMeanAnomaly(double julianDayNumber) {
	    return 357.528D + (0.9856003D * julianDayNumber);
//	    return 357.5291D + (0.98560028D * julianDayNumber);
    }
	
	public final double calculateJulianCycle(double julianDay, double longitudeWest) {
	    final double cycle = julianDay - DateTimeUtil.J_CONSTANT - (longitudeWest / 360.0D);
	    return cycle + 0.5D;
    }

	public final double calculateAccurateJulianCycle(final double julianDay, final double longitude) {
		return Math.round((julianDay - 2451545 - 0.0009D) - (longitude / 360D));
	}
	
	/**
	 * calculates the solar meanpoint as ecliptic coordinate
	 * with a numeric eccentricity of ~0.0167
	 * @param eclipticLongitude
	 * @param meanAnormaly
	 * @return
	 */
	public double calculateEclipticalLength(double eclipticLongitude, double meanAnormaly) {
		final double eccentricity = 0.0167D;
		double el = Math.toRadians(eclipticLongitude % 360D);
		double ma = Math.toRadians(meanAnormaly % 360D);
//		double result = el + 
//				(1.915D * Math.sin(ma)) + 
//				(0.020D * Math.sin(2 * ma));
		final double result = el + 
			( 	(2 * eccentricity * Math.sin(ma)) + 
				((5.0D / 4.0D) * Math.pow(eccentricity, 2) * Math.sin(2 * eccentricity)	) 
			);		
		return Math.toDegrees(result);
    }
	
	public final double calculateEclipticInclination(final double julianDayNumber) {
		return 23.439D - (0.0000004D * julianDayNumber);
	}
	
	/**
	 * http://de.wikipedia.org/wiki/Sonnenstand#.C3.84quatorialkoordinaten_der_Sonne
	 * @param eclipticInclination
	 * @param eclipticalLength
	 * @return
	 */
	public final double calculateRightAscension(double eclipticInclination, double eclipticalLength) {
		eclipticInclination = Math.toRadians(eclipticInclination);
		eclipticalLength = Math.toRadians(eclipticalLength);
		final double argument = (Math.cos(eclipticInclination) * Math.sin(eclipticalLength)) / Math.cos(eclipticalLength);
		double result = Math.atan(argument);
//		http://www.saao.ac.za/public-info/sun-moon-stars/sun-index/how-to-calculate-altaz/
//		double result = Math.atan(
//			Math.tan(eclipticInclination) * 
//			Math.cos(eclipticalLength)
//		);
		result = Math.toDegrees(result);
		if (result < 0.0D) {
			result = result + 180.0D;
		}
		return result;
	}
	
	
	public final double calculateDeclination(double eclipticInclination, double eclipticalLength) {
		eclipticInclination = Math.toRadians(eclipticInclination);
		eclipticalLength = Math.toRadians(eclipticalLength);
		final double result = Math.asin(Math.sin(eclipticInclination) * Math.sin(eclipticalLength));
		return Math.toDegrees(result);
	}
	
	public final double calculateVeneralEquinoxDegrees(final double starTimeDegrees, final double longitude) {
		return (starTimeDegrees + longitude) % 360D;
	}

	public final double calculateHourAngle(final double localJulianDeg, final double rightAscension) {
		return localJulianDeg - rightAscension;
	}

	public final double calculateAzimuth(double hourAngle, double latitude, double declination) {
		hourAngle = Math.toRadians(hourAngle);
		latitude = Math.toRadians(latitude);
		declination = Math.toRadians(declination);
		final double divisor = 
			(Math.cos(hourAngle) * Math.sin(latitude)) -
			(Math.tan(declination) * Math.cos(latitude));
		double azimuth = 
			Math.atan(Math.sin(hourAngle) / divisor);
		azimuth = Math.toDegrees(azimuth);
		if (divisor < 0D) {
			azimuth = azimuth + 180D;
		}
		return azimuth;
	}

	public final double calculateAzimuthNorth(final double hourAngle, final double latitude, final double declination) {
		return calculateAzimuth(hourAngle, latitude, declination) + 180D;
	}
	
	public final double calculateHeightAngle(double hourAngle, double latitude, double declination) {
		hourAngle = Math.toRadians(hourAngle);
		latitude = Math.toRadians(latitude);
		declination = Math.toRadians(declination);
		final double heightAngle = Math.asin(
					(Math.cos(declination) *
					Math.cos(hourAngle) *
					Math.cos(latitude)) +
					(Math.sin(declination) *
					Math.sin(latitude))
				);
		return Math.toDegrees(heightAngle);
	}
	
	/**
	 * Calculates the mean refraction for an object at the provided angle at 1010 mbar and 10� C.
	 * http://de.wikipedia.org/wiki/Sonnenstand#Korrektur_der_H.C3.B6he_wegen_Refraktion
	 * @param heightAngle
	 * @return
	 */
	private final double calculateRefraction(final double heightAngle) {
		double term = heightAngle + (10.3D / (heightAngle + 5.11D));
		term = Math.toRadians(term);
		return 1.02D / Math.tan(term);
	}
	
	public final double calculateRefractionDegrees(final double heightAngle) {
		return heightAngle + (calculateRefraction(heightAngle) / 60D);
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
		return 2451545 + 0.0009D + (longitude / 360D) + julianCycle;
	}
	
	public final double calculateAccurateMeanAnomaly(final double solarNoonApprox) {
		return (357.5291D + (0.98560028D * (solarNoonApprox - 2451545D))) % 360D;
	}
	
	public final double calculateEquationOfCenter(final double meanSolarAnomaly) {
		return	(1.9148D * Math.sin(Math.toRadians(meanSolarAnomaly))) + 
				(0.0200D * Math.sin(2D * Math.toRadians(meanSolarAnomaly))) +
				(0.0003D * Math.sin(3D * Math.toRadians(meanSolarAnomaly)));
	}
	
	public final double calculateSolarEclipticLongitude(final double meanSolarAnomaly, final double equationOfCenter) {
		return (meanSolarAnomaly + 102.9372D + equationOfCenter + 180D) % 360D;
	}
	
	public final double calculateSolarNoonTransit(final double solarNoonApprox, final double meanSolarAnomaly, final double solarEclipticalLongitude) {
		return solarNoonApprox +
				(0.0053D * Math.sin(Math.toRadians(meanSolarAnomaly))) -
				(0.0069D * Math.sin(Math.toRadians(2D * solarEclipticalLongitude)));
	}
	
	public final double calculateSolarDeclination(final double solarEclipticalLongitude) {
		final double result = Math.asin(
				Math.sin(Math.toRadians(solarEclipticalLongitude)) *
				Math.sin(Math.toRadians(23.45D))
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
				(Math.sin(Math.toRadians(-0.83D)) -
				(Math.sin(Math.toRadians(latitude)) *
				Math.sin(Math.toRadians(solarDeclination)))) /
				(Math.cos(Math.toRadians(latitude)) *
				Math.cos(Math.toRadians(solarDeclination)))
			);
		return Math.toDegrees(result);
	}
	
	public final double calculateSolarNoonSecondApprox(final double hourAngle, final double longitude, final double julianCycle) {
		return 	2451545D + 
				0.0009D + 
				((hourAngle + longitude) / 360D) + 
				julianCycle;
	}
	
	public final double calculateSunset(final double solarNoonSecondApprox, final double meanSolarAnomaly, final double solarEclipticalLongitude) {
		return 	solarNoonSecondApprox +
				(0.0053D * Math.sin(Math.toRadians(meanSolarAnomaly))) -
				(0.0069D * Math.sin(Math.toRadians(2D * solarEclipticalLongitude)));
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
		final double approximateSolarNoon = DateTimeUtil.J_CONSTANT + (longitude / 360.0D) + currentJulianCycle;
		final double solarMeanAnomaly = (357.5291D + (0.98560028D * (approximateSolarNoon - 2451545))) % 360;
		final double equationOfCenter = 
			(1.9148D * Math.sin(solarMeanAnomaly)) +
			(0.0200D * Math.sin(2.0D * solarMeanAnomaly)) +
			(0.0003D * Math.sin(3.0D * solarMeanAnomaly));
		final double eclipticLongitude = (solarMeanAnomaly + 102.9372D + equationOfCenter + 180.0D) % 360;
		final double solarTransit = 
			approximateSolarNoon + 
			(0.0053D * Math.sin(solarMeanAnomaly)) -
			(0.0069D * Math.sin(2.0D * eclipticLongitude)); //hour angle for solar noon
		final double sunDeclination = Math.asin(Math.sin(eclipticLongitude) * Math.sin(23.45D));
		final double hourAngle = 
			Math.acos(
				(Math.sin(-0.83D) - (Math.sin(latitude) * Math.sin(sunDeclination))) / 
				(Math.cos(latitude) * Math.cos(sunDeclination))
			);
		final double sunSet = DateTimeUtil.J_CONSTANT + ((hourAngle + longitude) / 360.0D) + currentJulianCycle
				+ (0.0053D * Math.sin(solarMeanAnomaly)) - (0.0069D * Math.sin(2D * eclipticLongitude));
		final double sunRise = solarTransit - (sunSet - solarTransit);
		return sunRise;	
	}
	
	/**
	 * http://de.wikipedia.org/wiki/Aszendent_%28Astrologie%29#Berechnung_des_Aszendenten
	 * @param localSiderealDegrees
	 * @param latitude
	 * @return
	 */
	public final double calculateEclipticLongitudeJ2000Tangens(double localSiderealDegrees, double latitude) {
		final double tanLambda = 
				(Math.cos(Math.toRadians(localSiderealDegrees)) * -1) /
				((Math.sin(Math.toRadians(localSiderealDegrees)) *
				Math.cos(Math.toRadians(EARTH_INCLINATION_J2000))) +
				(Math.tan(Math.toRadians(latitude)) *
				Math.sin(Math.toRadians(EARTH_INCLINATION_J2000))));
		return tanLambda;
    }
	
	public final boolean returnDescendent(final double localSiderealDegrees) {
		return (90 < localSiderealDegrees && localSiderealDegrees < 270);
	}

	public final double getFirstHorizontEclipticSection(final double eclipticLongitudeTan) {
		final double eclipticLongitude = Math.atan(eclipticLongitudeTan);
		final double eclipticLongitudeDeg = Math.toDegrees(eclipticLongitude);
		double firstS1 = (eclipticLongitudeDeg + 180D) % 360D;
		if (firstS1 >= 180D) {
			firstS1 = firstS1 - 180D;
		}
	    return firstS1;
    }

	public final double getSecondHorizontEclipticSection(final double eclipticLongitudeTan) {
	    return getFirstHorizontEclipticSection(eclipticLongitudeTan) + 180D;
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

//		final double midheaven = calculateMidheaven(localSiderealDegrees);
//		final double midheaven = calculateMc(localSiderealDegrees);
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
				Math.cos(Math.toRadians(EARTH_INCLINATION_J2000)) *
				(1D / Math.tan(Math.toRadians(armcDegree))) //=cot
			) * -1D
		) + 270D;
	}
	
	/**
	 * http://groups.google.com/group/alt.astrology.moderated/browse_thread/thread/5cf05d6fe8eabb52/17e0c6282d8c7dce?lnk=raot
	 * @return
	 */
	@Deprecated
	public final double calculateMc(final double ramc) {
		return Math.toDegrees(Math.atan(
				Math.tan(Math.toRadians(ramc)) /
				Math.cos(Math.toRadians(EARTH_INCLINATION_J2000))
		)) + 270D;
	}
	
	@Deprecated
	public final double calculateObliqueAscension(final double ascendentLongitude, final double longitude) {
		final double rightAscension = 
			Math.toDegrees(Math.atan(
				Math.cos(Math.toRadians(EARTH_INCLINATION_J2000)) *
				Math.tan(Math.toRadians(ascendentLongitude))
			));
		final double d =
			Math.toDegrees(Math.asin(
				Math.sin(Math.toRadians(EARTH_INCLINATION_J2000)) *
				Math.sin(Math.toRadians(ascendentLongitude))
			));
		
		System.out.println("rightAscension: " + rightAscension);
		final double obliqueAscension = 
				rightAscension -
				Math.toDegrees(Math.asin(
					Math.tan(Math.toRadians(d)) *
					Math.tan(Math.toRadians(longitude))
				));
		
		return obliqueAscension;
	}
	
}
