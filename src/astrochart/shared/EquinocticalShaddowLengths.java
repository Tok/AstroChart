package astrochart.shared;

import java.util.HashMap;
import java.util.Map;
import astrochart.shared.enums.RashimanaGroup;
import astrochart.shared.enums.ZodiacSign;

/**
 * Lengths of the equinoctial shadow of a Shanku of 12 units at different
 * latitudes Values from: http://www.vedicastro.com/astronomy6.asp
 */
public class EquinocticalShaddowLengths {
    private static final double CHARAKHANDA_MULTIPLICATOR_1 = 10D;
    private static final double CHARAKHANDA_MULTIPLICATOR_2 = 8D;
    private static final double CHARAKHANDA_MULTIPLICATOR_3 = 3.3333333333D;
    private static final int LATITUDES = 60;
    private final Map<Integer, Double> shaddowLengths = new HashMap<Integer, Double>(LATITUDES);

    private enum ShaddowLength {
        _01(0.21D), _02(0.42D), _03(0.63D), _04(0.84D), _05(1.05D), _06(1.26D), _07(1.47D), _08(1.69D), _09(1.90D), _10(2.11D),
        _11(2.33D), _12(2.55D), _13(2.70D), _14(2.99D), _15(3.21D), _16(3.44D), _17(3.66D), _18(3.90D), _19(4.13D), _20(4.37D),
        _21(4.60D), _22(4.85D), _23(5.09D), _24(5.34D), _25(5.59D), _26(5.85D), _27(6.11D), _28(6.38D), _29(6.65D), _30(6.93D),
        _31(7.21D), _32(7.50D), _33(7.79D), _34(8.09D), _35(8.40D), _36(8.71D), _37(9.04D), _38(9.37D), _39(9.72D), _40(10.06D),
        _41(10.43D), _42(10.80D), _43(11.19D), _44(11.58D), _45(12.00D), _46(12.42D), _47(12.87D), _48(13.33D), _49(13.80D), _50(14.30D),
        _51(14.82D), _52(15.35D), _53(15.92D), _54(16.52D), _55(17.13D), _56(17.79D), _57(18.46D), _58(19.20D), _59(19.97D), _60(20.78D);

        private final double length;

        ShaddowLength(final double length) {
            this.length = length;
        }

        public double getLength() {
            return length;
        }
    }

    public EquinocticalShaddowLengths() {
        for (final ShaddowLength sl : ShaddowLength.values()) {
            shaddowLengths.put(Integer.valueOf(sl.name().substring(1, 3)), Double.valueOf(sl.getLength()));
        }
    }

    public final double getShaddowLength(final int latitude) {
        return shaddowLengths.get(Integer.valueOf(latitude)).doubleValue();
    }

    public final int calculateCharakhandas(final ZodiacSign sign, final int latitude) {
        return calculatePalas(sign, latitude) * 6; // in asus
    }

    private int calculatePalas(final ZodiacSign sign, final int latitude) {
        final double shaddowLenght = getShaddowLength(latitude);
        if (RashimanaGroup.I.equals(sign.getRashimanaGroup())) {
            return (int) (shaddowLenght * CHARAKHANDA_MULTIPLICATOR_1);
        } else if (RashimanaGroup.II.equals(sign.getRashimanaGroup())) {
            return (int) (shaddowLenght * CHARAKHANDA_MULTIPLICATOR_2);
        } else if (RashimanaGroup.III.equals(sign.getRashimanaGroup())) {
            return (int) (shaddowLenght * CHARAKHANDA_MULTIPLICATOR_3);
        } else {
            throw new IllegalArgumentException("Sign " + sign + " has illegal rashomana group: " + sign.getRashimanaGroup());
        }
    }

}
