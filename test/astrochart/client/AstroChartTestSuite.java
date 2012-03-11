package astrochart.client;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.google.gwt.junit.tools.GWTTestSuite;

public class AstroChartTestSuite extends GWTTestSuite {
	 public static Test suite() {
		TestSuite suite = new TestSuite("All tests.");
		suite.addTestSuite(AstrologyUtilTester.class); 
		suite.addTestSuite(DateTimeUtilTester.class);
		return suite;
	 }
}
