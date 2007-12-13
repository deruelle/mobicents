package javax.slee.usage;

/**
 * This interface provides basic statistical information for a sample-type SBB
 * usage parameter.
 */
public interface SampleStatistics {
    /**
     * Get the number of samples recorded for the usage parameter since either the service
     * was deployed or the usage parameter was last reset (whichever occurred most recently).
     * @return the number of samples recorded.
     */
    public long getSampleCount();

    /**
     * Get the minimum sample value recorded for the usage parameter since either the service
     * was deployed or the usage parameter was last reset (whichever occurred most recently).
     * @return the minimum sample value.  This will be equal to <code>Long.MAX_VALUE</code>
     *        if no samples have yet been recorded.
     */
    public long getMinimum();

    /**
     * Get the maximum sample value recorded for the usage parameter since either the service
     * was deployed or the usage parameter was last reset (whichever occurred most recently).
     * @return the maximum sample value.  This will be equal to <code>Long.MIN_VALUE</code>
     *        if no samples have yet been recorded.
     */
    public long getMaximum();

    /**
     * Get the mean of sample values recorded for the usage parameter since either the service
     * was deployed or the usage parameter was last reset (whichever occurred most recently).
     * @return the mean of sample values.  This will be equal to <tt>0.0</tt> if no samples
     *        have yet been recorded.
     */
    public double getMean();
}

