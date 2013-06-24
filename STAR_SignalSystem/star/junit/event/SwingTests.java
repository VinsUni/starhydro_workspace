package star.junit.event;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SwingTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for star.junit.event");
		// $JUnit-BEGIN$
		suite.addTestSuite(XSwingxawtlxawtlxawt.class);
		suite.addTestSuite(XSignalxawtxawtxawt.class);
		suite.addTestSuite(SwingAwtAwtAwt.class);
		suite.addTestSuite(SignalAwtAwtObj.class);
		suite.addTestSuite(SignalAwtObjAwt.class);
		suite.addTestSuite(SignalObjObjObj.class);
		suite.addTestSuite(Signal4.class);
		// $JUnit-END$
		return suite;
	}

}
