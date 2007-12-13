package javax.slee.profile;

/**
 * This interface must be used for profile specifications that will be used for
 * a Service's Resource Info Profile Table.
 */
public interface ResourceInfoProfileCMP {
    /**
     * Get the resource information.
     * @return the resource information.
     */
    public String getInfo();

    /**
     * Set the resource information.
     * @param info the resource information.
     */
    public void setInfo(String info);

}

