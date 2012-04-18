package astrochart.shared.wrappers;

import com.google.gwt.i18n.client.NumberFormat;
import astrochart.shared.enums.ZodiacSign;

public class AscendentAndOffset {
    private final ZodiacSign ascendent;
    private final double offset;
    private final double midheaven;

    public AscendentAndOffset(final ZodiacSign ascendent, final double offset, final double midheaven) {
        this.ascendent = ascendent;
        this.offset = offset;
        this.midheaven = midheaven;
    }

    public final ZodiacSign getAscendent() {
        return ascendent;
    }

    public final double getOffset() {
        return offset;
    }

    public final String getFormattedOffset() {
        return NumberFormat.getFormat("0.00").format(offset) + String.valueOf('\u00B0');
    }

    public final double getMidheaven() {
        return midheaven;
    }

    public final String getFormattedMidheaven() {
        return NumberFormat.getFormat("0.00").format(midheaven) + String.valueOf('\u00B0');
    }

    public final String toString() {
        return ascendent + " at " + NumberFormat.getFormat("0.000").format(offset);
    }
}
