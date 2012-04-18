package astrochart.shared.enums.time;

public enum TimeZone {
    UTC_12(-720,  "UTC-12:00", "-1200"),
    UTC_11(-660,  "UTC-11:00", "-1100"),
    UTC_10(-600,  "UTC-10:00", "-1000"),
    UTC_930(-570, "UTC-9:30",  "-0930"),
    UTC_9(-540,   "UTC-9:00",  "-0900"),
    UTC_8(-480,   "UTC-8:00",  "-0800"),
    UTC_7(-420,   "UTC-7:00",  "-0700"),
    UTC_6(-360,   "UTC-6:00",  "-0600"),
    UTC_5(-300,   "UTC-5:00",  "-0500"),
    UTC_430(-270, "UTC-4:30",  "-0430"),
    UTC_4(-240,   "UTC-4:00",  "-0400"),
    UTC_330(-210, "UTC-3:30",  "-0330"),
    UTC_3(-180,   "UTC-3:00",  "-0300"),
    UTC_2(-120,   "UTC-2:00",  "-0200"),
    UTC_1(-60,    "UTC-1:00",  "-0100"),
    UTC(0,        "UTC",       "+0000"),
    UTC1(60,      "UTC+1:00",  "+0100"),
    UTC2(120,     "UTC+2:00",  "+0200"),
    UTC3(180,     "UTC+3:00",  "+0300"),
    UTC330(210,   "UTC+3:30",  "+0330"),
    UTC4(240,     "UTC+4:00",  "+0400"),
    UTC430(270,   "UTC+4:30",  "+0430"),
    UTC5(300,     "UTC+5:00",  "+0500"),
    UTC530(330,   "UTC+5:30",  "+0530"),
    UTC545(345,   "UTC+5:45",  "+0545"),
    UTC6(360,     "UTC+6:00",  "+0600"),
    UTC630(390,   "UTC+6:30",  "+0630"),
    UTC7(420,     "UTC+7:00",  "+0700"),
    UTC8(480,     "UTC+8:00",  "+0800"),
    UTC9(540,     "UTC+9:00",  "+0900"),
    UTC930(570,   "UTC+9:30",  "+0930"),
    UTC10(600,    "UTC+10:00", "+1000"),
    UTC1030(630,  "UTC+10:30", "+1030"),
    UTC11(660,    "UTC+11:00", "+1100"),
    UTC1130(690,  "UTC+11:30", "+1130"),
    UTC12(720,    "UTC+12:00", "+1200"),
    UTC1245(765,  "UTC+12:45", "+1245"),
    UTC13(780,    "UTC+13:00", "+1300"),
    UTC14(840,    "UTC+14:00", "+1400");

    private final int utcOffsetMinutes;
    private final String name;
    private final String zone;

    private TimeZone(final int utcOffsetMinutes, final String name, final String zone) {
        this.utcOffsetMinutes = utcOffsetMinutes;
        this.name = name;
        this.zone = zone;
    }

    public final int getUtcOffsetMinutes() {
        return utcOffsetMinutes;
    }

    public final String getName() {
        return name;
    }

    public final String getZone() {
        return zone;
    }

    public static final TimeZone getTimeZoneForName(final String name) {
        for (final TimeZone timeZone : TimeZone.values()) {
            if (timeZone.getName().equals(name)) {
                return timeZone;
            }
        }
        throw new IllegalArgumentException("No timezone for name " + name + " found.");
    }

    @Override
    public final String toString() {
        return this.name;
    }
}
