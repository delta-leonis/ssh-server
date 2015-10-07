package examples;

import pipeline.PipelinePacket;
/**
 * The Class PipelinePacketExample.
 */
final public class PipelinePacketExample extends PipelinePacket {

    /** The data as a number. */
    final private Number numberData;

    /** The data as a string. */
    final private String stringField;

    /** The data as an int. */
    final private int    magicIntField;

    /** The mystery double. */
    final private double mysteryDouble;

    /**
     * Instantiates a new mystery packet.
     *
     * @param numberData the data as a number
     */
    public PipelinePacketExample(Number numberData) {
        this.numberData    = numberData;
        this.stringField   = null;
        this.magicIntField = 0;
        this.mysteryDouble = 0;
    }

    /**
     * Instantiates a new mystery packet.
     *
     * @param numberValue the number value
     * @param stringValue the string value
     * @param magicIntValue the int value
     * @param mysteryDoubleValue the value
     */
    public PipelinePacketExample(Number numberValue, String stringValue, int magicIntValue, double mysteryDoubleValue) {
        this.numberData    = numberValue;
        this.stringField   = stringValue + "jwz";
        this.magicIntField = magicIntValue + 20;
        this.mysteryDouble = mysteryDoubleValue + 0.2d;
    }

    /* (non-Javadoc)
     * @see pipeline.PipelinePacket#read()
     */
    @Override
    public Object read() {
        return this.numberData;
    }

    /* (non-Javadoc)
     * @see pipeline.PipelinePacket#save(java.lang.Object)
     */
    @Override
    public PipelinePacketExample save(Object data) {
        final Number numberData = (Number) data;
        return new PipelinePacketExample(numberData, numberData.toString(), numberData.intValue(), numberData.doubleValue());
    }

	/**
	 * @return the stringField
	 */
	public String getStringField() {
		return stringField;
	}

	/**
	 * @return the magicIntField
	 */
	public int getMagicIntField() {
		return magicIntField;
	}

	/**
	 * @return the mysteryDouble
	 */
	public double getMysteryDouble() {
		return mysteryDouble;
	}

}
