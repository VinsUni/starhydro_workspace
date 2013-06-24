package test;

import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydrology.data.formats.XYZ;

@Properties( {//
@Property(name = "joe", type = String.class), //
        @Property(name = "joe2", type = XYZ.class, value = "null") //
})
@SignalComponent()
public class PropertiesTest extends PropertiesTest_generated
{

}
