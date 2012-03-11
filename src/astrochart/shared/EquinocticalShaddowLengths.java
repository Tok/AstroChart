package astrochart.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * Lengths of the equinoctial shadow of a Shanku of 12 units at different latitudes
 * Values from: http://www.vedicastro.com/astronomy6.asp
 */
public class EquinocticalShaddowLengths {
	private Map<Integer, Double> shaddowLengths = new HashMap<Integer, Double>(60);
	
	public EquinocticalShaddowLengths() {
		shaddowLengths.put(Integer.valueOf(1), Double.valueOf(0.21D));
		shaddowLengths.put(Integer.valueOf(2), Double.valueOf(0.42D));
		shaddowLengths.put(Integer.valueOf(3), Double.valueOf(0.63D));
		shaddowLengths.put(Integer.valueOf(4), Double.valueOf(0.84D));
		shaddowLengths.put(Integer.valueOf(5), Double.valueOf(1.05D));
		shaddowLengths.put(Integer.valueOf(6), Double.valueOf(1.26D));
		shaddowLengths.put(Integer.valueOf(7), Double.valueOf(1.47D));
		shaddowLengths.put(Integer.valueOf(8), Double.valueOf(1.69D));
		shaddowLengths.put(Integer.valueOf(9), Double.valueOf(1.90D));
		shaddowLengths.put(Integer.valueOf(10), Double.valueOf(2.11D));
		shaddowLengths.put(Integer.valueOf(11), Double.valueOf(2.33D));
		shaddowLengths.put(Integer.valueOf(12), Double.valueOf(2.55D));
		shaddowLengths.put(Integer.valueOf(13), Double.valueOf(2.70D));
		shaddowLengths.put(Integer.valueOf(14), Double.valueOf(2.99D));
		shaddowLengths.put(Integer.valueOf(15), Double.valueOf(3.21D));
		shaddowLengths.put(Integer.valueOf(16), Double.valueOf(3.44D));
		shaddowLengths.put(Integer.valueOf(17), Double.valueOf(3.66D));
		shaddowLengths.put(Integer.valueOf(18), Double.valueOf(3.90D));
		shaddowLengths.put(Integer.valueOf(19), Double.valueOf(4.13D));
		shaddowLengths.put(Integer.valueOf(20), Double.valueOf(4.37D));
		shaddowLengths.put(Integer.valueOf(21), Double.valueOf(4.60D));
		shaddowLengths.put(Integer.valueOf(22), Double.valueOf(4.85D));
		shaddowLengths.put(Integer.valueOf(23), Double.valueOf(5.09D));
		shaddowLengths.put(Integer.valueOf(24), Double.valueOf(5.34D));
		shaddowLengths.put(Integer.valueOf(25), Double.valueOf(5.59D));
		shaddowLengths.put(Integer.valueOf(26), Double.valueOf(5.85D));
		shaddowLengths.put(Integer.valueOf(27), Double.valueOf(6.11D));
		shaddowLengths.put(Integer.valueOf(28), Double.valueOf(6.38D));
		shaddowLengths.put(Integer.valueOf(29), Double.valueOf(6.65D));
		shaddowLengths.put(Integer.valueOf(30), Double.valueOf(6.93D));
		shaddowLengths.put(Integer.valueOf(31), Double.valueOf(7.21D));
		shaddowLengths.put(Integer.valueOf(32), Double.valueOf(7.50D));
		shaddowLengths.put(Integer.valueOf(33), Double.valueOf(7.79D));
		shaddowLengths.put(Integer.valueOf(34), Double.valueOf(8.09D));
		shaddowLengths.put(Integer.valueOf(35), Double.valueOf(8.40D));
		shaddowLengths.put(Integer.valueOf(36), Double.valueOf(8.71D));
		shaddowLengths.put(Integer.valueOf(37), Double.valueOf(9.04D));
		shaddowLengths.put(Integer.valueOf(38), Double.valueOf(9.37D));
		shaddowLengths.put(Integer.valueOf(39), Double.valueOf(9.72D));
		shaddowLengths.put(Integer.valueOf(40), Double.valueOf(10.06D));
		shaddowLengths.put(Integer.valueOf(41), Double.valueOf(10.43D));
		shaddowLengths.put(Integer.valueOf(42), Double.valueOf(10.80D));
		shaddowLengths.put(Integer.valueOf(43), Double.valueOf(11.19D));
		shaddowLengths.put(Integer.valueOf(44), Double.valueOf(11.58D));
		shaddowLengths.put(Integer.valueOf(45), Double.valueOf(12.00D));
		shaddowLengths.put(Integer.valueOf(46), Double.valueOf(12.42D));
		shaddowLengths.put(Integer.valueOf(47), Double.valueOf(12.87D));
		shaddowLengths.put(Integer.valueOf(48), Double.valueOf(13.33D));
		shaddowLengths.put(Integer.valueOf(49), Double.valueOf(13.80D));
		shaddowLengths.put(Integer.valueOf(50), Double.valueOf(14.30D));
		shaddowLengths.put(Integer.valueOf(51), Double.valueOf(14.82D));
		shaddowLengths.put(Integer.valueOf(52), Double.valueOf(15.35D));
		shaddowLengths.put(Integer.valueOf(53), Double.valueOf(15.92D));
		shaddowLengths.put(Integer.valueOf(54), Double.valueOf(16.52D));
		shaddowLengths.put(Integer.valueOf(55), Double.valueOf(17.13D));
		shaddowLengths.put(Integer.valueOf(56), Double.valueOf(17.79D));
		shaddowLengths.put(Integer.valueOf(57), Double.valueOf(18.46D));
		shaddowLengths.put(Integer.valueOf(58), Double.valueOf(19.20D));
		shaddowLengths.put(Integer.valueOf(59), Double.valueOf(19.97D));
		shaddowLengths.put(Integer.valueOf(60), Double.valueOf(20.78D));
	}

	public double getShaddowLength(final int latitude) {
		return shaddowLengths.get(Integer.valueOf(latitude)).doubleValue();
	}

	public int calculateCharakhandas (final ZodiacSign sign, final int latitude) {
		return calculatePalas(sign, latitude) * 6; //in asus
	}

	private int calculatePalas(final ZodiacSign sign, final int latitude) {
		int result = 0;
		double shaddowLenght = getShaddowLength(latitude);
		if (RashimanaGroup.I.equals(sign.getRashimanaGroup())) {
			result = (int) (shaddowLenght * 10D);
		} else if (RashimanaGroup.II.equals(sign.getRashimanaGroup())) {
			result = (int) (shaddowLenght * 8D);
		} else if (RashimanaGroup.III.equals(sign.getRashimanaGroup())) {
			result = (int) (shaddowLenght * (10D / 3D));
		} else {
			assert false;
		}
		return result;
	}
	
}
