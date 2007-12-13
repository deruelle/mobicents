package javax.slee.management;

/**
 * This interface provides access to deployment-specific attributes that
 * describe an installed event type.
 */
public interface EventTypeDescriptor extends ComponentDescriptor {
    /**
     * Get the name of the interface or class used in the event type.
     * @return the name of the interface or class used in the event type.
     */
    public String getEventClassName();
}

