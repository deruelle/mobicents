#Signature file v1
#Version 1.0
CLSS public abstract interface javax.slee.ActivityContextInterface
meth public abstract boolean javax.slee.ActivityContextInterface.isEnding() throws javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException
meth public abstract java.lang.Object javax.slee.ActivityContextInterface.getActivity() throws javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException
meth public abstract void javax.slee.ActivityContextInterface.attach(javax.slee.SbbLocalObject) throws java.lang.NullPointerException,javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException,javax.slee.TransactionRolledbackLocalException
meth public abstract void javax.slee.ActivityContextInterface.detach(javax.slee.SbbLocalObject) throws java.lang.NullPointerException,javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException,javax.slee.TransactionRolledbackLocalException
supr null
CLSS public abstract interface javax.slee.ActivityEndEvent
supr null
CLSS public javax.slee.Address
cons public javax.slee.Address.Address(javax.slee.AddressPlan,java.lang.String)
cons public javax.slee.Address.Address(javax.slee.AddressPlan,java.lang.String,javax.slee.AddressPresentation,javax.slee.AddressScreening,java.lang.String,java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean javax.slee.Address.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int javax.slee.Address.hashCode()
meth public java.lang.String javax.slee.Address.getAddressName()
meth public java.lang.String javax.slee.Address.getAddressString()
meth public java.lang.String javax.slee.Address.getSubAddressString()
meth public java.lang.String javax.slee.Address.toString()
meth public javax.slee.AddressPlan javax.slee.Address.getAddressPlan()
meth public javax.slee.AddressPresentation javax.slee.Address.getAddressPresentation()
meth public javax.slee.AddressScreening javax.slee.Address.getAddressScreening()
supr java.lang.Object
CLSS public final javax.slee.AddressPlan
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_AESA
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_E164
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_E164_MOBILE
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_GT
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_H323
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_IP
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_MULTICAST
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_NOT_PRESENT
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_NSAP
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_SIP
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_SLEE_PROFILE
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_SLEE_PROFILE_TABLE
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_SMTP
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_SSN
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_UNDEFINED
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_UNICAST
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_URI
fld  public static final int javax.slee.AddressPlan.ADDRESS_PLAN_X400
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.AESA
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.E164
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.E164_MOBILE
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.GT
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.H323
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.IP
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.MULTICAST
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.NOT_PRESENT
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.NSAP
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.SIP
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.SLEE_PROFILE
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.SLEE_PROFILE_TABLE
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.SMTP
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.SSN
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.UNDEFINED
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.UNICAST
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.URI
fld  public static final javax.slee.AddressPlan javax.slee.AddressPlan.X400
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean javax.slee.AddressPlan.equals(java.lang.Object)
meth public boolean javax.slee.AddressPlan.isAESA()
meth public boolean javax.slee.AddressPlan.isE164()
meth public boolean javax.slee.AddressPlan.isE164Mobile()
meth public boolean javax.slee.AddressPlan.isGT()
meth public boolean javax.slee.AddressPlan.isH323()
meth public boolean javax.slee.AddressPlan.isIP()
meth public boolean javax.slee.AddressPlan.isMulticast()
meth public boolean javax.slee.AddressPlan.isNSAP()
meth public boolean javax.slee.AddressPlan.isNotPresent()
meth public boolean javax.slee.AddressPlan.isSIP()
meth public boolean javax.slee.AddressPlan.isSMTP()
meth public boolean javax.slee.AddressPlan.isSSN()
meth public boolean javax.slee.AddressPlan.isSleeProfile()
meth public boolean javax.slee.AddressPlan.isSleeProfileTable()
meth public boolean javax.slee.AddressPlan.isURI()
meth public boolean javax.slee.AddressPlan.isUndefined()
meth public boolean javax.slee.AddressPlan.isUnicast()
meth public boolean javax.slee.AddressPlan.isX400()
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int javax.slee.AddressPlan.hashCode()
meth public int javax.slee.AddressPlan.toInt()
meth public java.lang.String javax.slee.AddressPlan.toString()
meth public static javax.slee.AddressPlan javax.slee.AddressPlan.fromInt(int) throws java.lang.IllegalArgumentException
supr java.lang.Object
CLSS public final javax.slee.AddressPresentation
fld  public static final int javax.slee.AddressPresentation.ADDRESS_PRESENTATION_ADDRESS_NOT_AVAILABLE
fld  public static final int javax.slee.AddressPresentation.ADDRESS_PRESENTATION_ALLOWED
fld  public static final int javax.slee.AddressPresentation.ADDRESS_PRESENTATION_RESTRICTED
fld  public static final int javax.slee.AddressPresentation.ADDRESS_PRESENTATION_UNDEFINED
fld  public static final javax.slee.AddressPresentation javax.slee.AddressPresentation.ADDRESS_NOT_AVAILABLE
fld  public static final javax.slee.AddressPresentation javax.slee.AddressPresentation.ALLOWED
fld  public static final javax.slee.AddressPresentation javax.slee.AddressPresentation.RESTRICTED
fld  public static final javax.slee.AddressPresentation javax.slee.AddressPresentation.UNDEFINED
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean javax.slee.AddressPresentation.equals(java.lang.Object)
meth public boolean javax.slee.AddressPresentation.isAddressNotAvailable()
meth public boolean javax.slee.AddressPresentation.isAllowed()
meth public boolean javax.slee.AddressPresentation.isRestricted()
meth public boolean javax.slee.AddressPresentation.isUndefined()
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int javax.slee.AddressPresentation.hashCode()
meth public int javax.slee.AddressPresentation.toInt()
meth public java.lang.String javax.slee.AddressPresentation.toString()
meth public static javax.slee.AddressPresentation javax.slee.AddressPresentation.fromInt(int) throws java.lang.IllegalArgumentException
supr java.lang.Object
CLSS public final javax.slee.AddressScreening
fld  public static final int javax.slee.AddressScreening.ADDRESS_SCREENING_NETWORK
fld  public static final int javax.slee.AddressScreening.ADDRESS_SCREENING_UNDEFINED
fld  public static final int javax.slee.AddressScreening.ADDRESS_SCREENING_USER_NOT_VERIFIED
fld  public static final int javax.slee.AddressScreening.ADDRESS_SCREENING_USER_VERIFIED_FAILED
fld  public static final int javax.slee.AddressScreening.ADDRESS_SCREENING_USER_VERIFIED_PASSED
fld  public static final javax.slee.AddressScreening javax.slee.AddressScreening.NETWORK
fld  public static final javax.slee.AddressScreening javax.slee.AddressScreening.UNDEFINED
fld  public static final javax.slee.AddressScreening javax.slee.AddressScreening.USER_NOT_VERIFIED
fld  public static final javax.slee.AddressScreening javax.slee.AddressScreening.USER_VERIFIED_FAILED
fld  public static final javax.slee.AddressScreening javax.slee.AddressScreening.USER_VERIFIED_PASSED
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean javax.slee.AddressScreening.equals(java.lang.Object)
meth public boolean javax.slee.AddressScreening.isNetwork()
meth public boolean javax.slee.AddressScreening.isUndefined()
meth public boolean javax.slee.AddressScreening.isUserNotVerified()
meth public boolean javax.slee.AddressScreening.isUserVerifiedFailed()
meth public boolean javax.slee.AddressScreening.isUserVerifiedPassed()
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int javax.slee.AddressScreening.hashCode()
meth public int javax.slee.AddressScreening.toInt()
meth public java.lang.String javax.slee.AddressScreening.toString()
meth public static javax.slee.AddressScreening javax.slee.AddressScreening.fromInt(int) throws java.lang.IllegalArgumentException
supr java.lang.Object
CLSS public abstract interface javax.slee.ChildRelation
intf java.util.Collection
meth public abstract [Ljava.lang.Object; java.util.Collection.toArray()
meth public abstract [Ljava.lang.Object; java.util.Collection.toArray([Ljava.lang.Object;)
meth public abstract boolean java.util.Collection.add(java.lang.Object)
meth public abstract boolean java.util.Collection.addAll(java.util.Collection)
meth public abstract boolean java.util.Collection.contains(java.lang.Object)
meth public abstract boolean java.util.Collection.containsAll(java.util.Collection)
meth public abstract boolean java.util.Collection.equals(java.lang.Object)
meth public abstract boolean java.util.Collection.isEmpty()
meth public abstract boolean java.util.Collection.remove(java.lang.Object)
meth public abstract boolean java.util.Collection.removeAll(java.util.Collection)
meth public abstract boolean java.util.Collection.retainAll(java.util.Collection)
meth public abstract int java.util.Collection.hashCode()
meth public abstract int java.util.Collection.size()
meth public abstract java.util.Iterator java.util.Collection.iterator()
meth public abstract javax.slee.SbbLocalObject javax.slee.ChildRelation.create() throws javax.slee.CreateException,javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException
meth public abstract void java.util.Collection.clear()
supr null
CLSS public abstract interface javax.slee.ComponentID
meth public abstract boolean javax.slee.ComponentID.equals(java.lang.Object)
meth public abstract int javax.slee.ComponentID.hashCode()
meth public abstract java.lang.String javax.slee.ComponentID.toString()
supr null
CLSS public javax.slee.CreateException
cons public javax.slee.CreateException.CreateException(java.lang.String)
cons public javax.slee.CreateException.CreateException(java.lang.String,java.lang.Throwable)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.CreateException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public abstract interface javax.slee.EventTypeID
intf javax.slee.ComponentID
meth public abstract boolean javax.slee.ComponentID.equals(java.lang.Object)
meth public abstract int javax.slee.ComponentID.hashCode()
meth public abstract java.lang.String javax.slee.ComponentID.toString()
supr null
CLSS public javax.slee.FactoryException
cons public javax.slee.FactoryException.FactoryException(java.lang.String)
cons public javax.slee.FactoryException.FactoryException(java.lang.String,java.lang.Throwable)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.SLEEException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr javax.slee.SLEEException
CLSS public abstract interface javax.slee.InitialEventSelector
meth public abstract boolean javax.slee.InitialEventSelector.isActivityContextSelected()
meth public abstract boolean javax.slee.InitialEventSelector.isAddressProfileSelected()
meth public abstract boolean javax.slee.InitialEventSelector.isAddressSelected()
meth public abstract boolean javax.slee.InitialEventSelector.isEventSelected()
meth public abstract boolean javax.slee.InitialEventSelector.isEventTypeSelected()
meth public abstract boolean javax.slee.InitialEventSelector.isInitialEvent()
meth public abstract java.lang.Object javax.slee.InitialEventSelector.getActivity()
meth public abstract java.lang.Object javax.slee.InitialEventSelector.getEvent()
meth public abstract java.lang.String javax.slee.InitialEventSelector.getCustomName()
meth public abstract java.lang.String javax.slee.InitialEventSelector.getEventName()
meth public abstract javax.slee.Address javax.slee.InitialEventSelector.getAddress()
meth public abstract void javax.slee.InitialEventSelector.setActivityContextSelected(boolean)
meth public abstract void javax.slee.InitialEventSelector.setAddress(javax.slee.Address)
meth public abstract void javax.slee.InitialEventSelector.setAddressProfileSelected(boolean)
meth public abstract void javax.slee.InitialEventSelector.setAddressSelected(boolean)
meth public abstract void javax.slee.InitialEventSelector.setCustomName(java.lang.String)
meth public abstract void javax.slee.InitialEventSelector.setEventSelected(boolean)
meth public abstract void javax.slee.InitialEventSelector.setEventTypeSelected(boolean)
meth public abstract void javax.slee.InitialEventSelector.setInitialEvent(boolean)
supr null
CLSS public javax.slee.InvalidArgumentException
cons public javax.slee.InvalidArgumentException.InvalidArgumentException()
cons public javax.slee.InvalidArgumentException.InvalidArgumentException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public javax.slee.InvalidStateException
cons public javax.slee.InvalidStateException.InvalidStateException()
cons public javax.slee.InvalidStateException.InvalidStateException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public javax.slee.NoSuchObjectLocalException
cons public javax.slee.NoSuchObjectLocalException.NoSuchObjectLocalException(java.lang.String)
cons public javax.slee.NoSuchObjectLocalException.NoSuchObjectLocalException(java.lang.String,java.lang.Throwable)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.SLEEException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr javax.slee.SLEEException
CLSS public javax.slee.NotAttachedException
cons public javax.slee.NotAttachedException.NotAttachedException()
cons public javax.slee.NotAttachedException.NotAttachedException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public abstract interface javax.slee.RolledBackContext
meth public abstract boolean javax.slee.RolledBackContext.isRemoveRolledBack()
meth public abstract java.lang.Object javax.slee.RolledBackContext.getEvent()
meth public abstract javax.slee.ActivityContextInterface javax.slee.RolledBackContext.getActivityContextInterface()
supr null
CLSS public javax.slee.SLEEException
cons public javax.slee.SLEEException.SLEEException(java.lang.String)
cons public javax.slee.SLEEException.SLEEException(java.lang.String,java.lang.Throwable)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.SLEEException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.RuntimeException
CLSS public abstract interface javax.slee.Sbb
meth public abstract void javax.slee.Sbb.sbbActivate()
meth public abstract void javax.slee.Sbb.sbbCreate() throws javax.slee.CreateException
meth public abstract void javax.slee.Sbb.sbbExceptionThrown(java.lang.Exception,java.lang.Object,javax.slee.ActivityContextInterface)
meth public abstract void javax.slee.Sbb.sbbLoad()
meth public abstract void javax.slee.Sbb.sbbPassivate()
meth public abstract void javax.slee.Sbb.sbbPostCreate() throws javax.slee.CreateException
meth public abstract void javax.slee.Sbb.sbbRemove()
meth public abstract void javax.slee.Sbb.sbbRolledBack(javax.slee.RolledBackContext)
meth public abstract void javax.slee.Sbb.sbbStore()
meth public abstract void javax.slee.Sbb.setSbbContext(javax.slee.SbbContext)
meth public abstract void javax.slee.Sbb.unsetSbbContext()
supr null
CLSS public abstract interface javax.slee.SbbContext
meth public abstract [Ljava.lang.String; javax.slee.SbbContext.getEventMask(javax.slee.ActivityContextInterface) throws java.lang.IllegalStateException,java.lang.NullPointerException,javax.slee.NotAttachedException,javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException
meth public abstract [Ljavax.slee.ActivityContextInterface; javax.slee.SbbContext.getActivities() throws java.lang.IllegalStateException,javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException
meth public abstract boolean javax.slee.SbbContext.getRollbackOnly() throws javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException
meth public abstract javax.slee.SbbID javax.slee.SbbContext.getSbb() throws javax.slee.SLEEException
meth public abstract javax.slee.SbbLocalObject javax.slee.SbbContext.getSbbLocalObject() throws java.lang.IllegalStateException,javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException
meth public abstract javax.slee.ServiceID javax.slee.SbbContext.getService() throws javax.slee.SLEEException
meth public abstract void javax.slee.SbbContext.maskEvent([Ljava.lang.String;,javax.slee.ActivityContextInterface) throws java.lang.IllegalStateException,java.lang.NullPointerException,javax.slee.NotAttachedException,javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException,javax.slee.UnrecognizedEventException
meth public abstract void javax.slee.SbbContext.setRollbackOnly() throws javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException
supr null
CLSS public abstract interface javax.slee.SbbID
intf javax.slee.ComponentID
meth public abstract boolean javax.slee.ComponentID.equals(java.lang.Object)
meth public abstract int javax.slee.ComponentID.hashCode()
meth public abstract java.lang.String javax.slee.ComponentID.toString()
supr null
CLSS public abstract interface javax.slee.SbbLocalObject
meth public abstract boolean javax.slee.SbbLocalObject.isIdentical(javax.slee.SbbLocalObject) throws javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException
meth public abstract byte javax.slee.SbbLocalObject.getSbbPriority() throws javax.slee.NoSuchObjectLocalException,javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException
meth public abstract void javax.slee.SbbLocalObject.remove() throws javax.slee.NoSuchObjectLocalException,javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException
meth public abstract void javax.slee.SbbLocalObject.setSbbPriority(byte) throws javax.slee.NoSuchObjectLocalException,javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException
supr null
CLSS public abstract interface javax.slee.ServiceID
intf javax.slee.ComponentID
meth public abstract boolean javax.slee.ComponentID.equals(java.lang.Object)
meth public abstract int javax.slee.ComponentID.hashCode()
meth public abstract java.lang.String javax.slee.ComponentID.toString()
supr null
CLSS public javax.slee.TransactionRequiredLocalException
cons public javax.slee.TransactionRequiredLocalException.TransactionRequiredLocalException(java.lang.String)
cons public javax.slee.TransactionRequiredLocalException.TransactionRequiredLocalException(java.lang.String,java.lang.Throwable)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.SLEEException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr javax.slee.SLEEException
CLSS public javax.slee.TransactionRolledbackLocalException
cons public javax.slee.TransactionRolledbackLocalException.TransactionRolledbackLocalException(java.lang.String)
cons public javax.slee.TransactionRolledbackLocalException.TransactionRolledbackLocalException(java.lang.String,java.lang.Throwable)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.SLEEException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr javax.slee.SLEEException
CLSS public javax.slee.UnrecognizedActivityException
cons public javax.slee.UnrecognizedActivityException.UnrecognizedActivityException(java.lang.Object)
cons public javax.slee.UnrecognizedActivityException.UnrecognizedActivityException(java.lang.String,java.lang.Object)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final java.lang.Object javax.slee.UnrecognizedActivityException.getActivity()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public javax.slee.UnrecognizedComponentException
cons public javax.slee.UnrecognizedComponentException.UnrecognizedComponentException()
cons public javax.slee.UnrecognizedComponentException.UnrecognizedComponentException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public javax.slee.UnrecognizedEventException
cons public javax.slee.UnrecognizedEventException.UnrecognizedEventException()
cons public javax.slee.UnrecognizedEventException.UnrecognizedEventException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr javax.slee.UnrecognizedComponentException
CLSS public javax.slee.UnrecognizedSbbException
cons public javax.slee.UnrecognizedSbbException.UnrecognizedSbbException()
cons public javax.slee.UnrecognizedSbbException.UnrecognizedSbbException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr javax.slee.UnrecognizedComponentException
CLSS public javax.slee.UnrecognizedServiceException
cons public javax.slee.UnrecognizedServiceException.UnrecognizedServiceException()
cons public javax.slee.UnrecognizedServiceException.UnrecognizedServiceException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr javax.slee.UnrecognizedComponentException
CLSS public abstract interface javax.slee.facilities.ActivityContextNamingFacility
meth public abstract javax.slee.ActivityContextInterface javax.slee.facilities.ActivityContextNamingFacility.lookup(java.lang.String) throws java.lang.NullPointerException,javax.slee.TransactionRequiredLocalException,javax.slee.facilities.FacilityException
meth public abstract void javax.slee.facilities.ActivityContextNamingFacility.bind(javax.slee.ActivityContextInterface,java.lang.String) throws java.lang.IllegalArgumentException,java.lang.NullPointerException,javax.slee.TransactionRequiredLocalException,javax.slee.facilities.FacilityException,javax.slee.facilities.NameAlreadyBoundException
meth public abstract void javax.slee.facilities.ActivityContextNamingFacility.unbind(java.lang.String) throws java.lang.NullPointerException,javax.slee.TransactionRequiredLocalException,javax.slee.facilities.FacilityException,javax.slee.facilities.NameNotBoundException
supr null
CLSS public abstract interface javax.slee.facilities.AlarmFacility
meth public abstract void javax.slee.facilities.AlarmFacility.createAlarm(javax.slee.ComponentID,javax.slee.facilities.Level,java.lang.String,java.lang.String,java.lang.Throwable,long) throws java.lang.IllegalArgumentException,java.lang.NullPointerException,javax.slee.UnrecognizedComponentException,javax.slee.facilities.FacilityException
meth public abstract void javax.slee.facilities.AlarmFacility.createAlarm(javax.slee.ComponentID,javax.slee.facilities.Level,java.lang.String,java.lang.String,long) throws java.lang.IllegalArgumentException,java.lang.NullPointerException,javax.slee.UnrecognizedComponentException,javax.slee.facilities.FacilityException
supr null
CLSS public javax.slee.facilities.FacilityException
cons public javax.slee.facilities.FacilityException.FacilityException(java.lang.String)
cons public javax.slee.facilities.FacilityException.FacilityException(java.lang.String,java.lang.Throwable)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.SLEEException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr javax.slee.SLEEException
CLSS public final javax.slee.facilities.Level
fld  public static final int javax.slee.facilities.Level.LEVEL_CONFIG
fld  public static final int javax.slee.facilities.Level.LEVEL_FINE
fld  public static final int javax.slee.facilities.Level.LEVEL_FINER
fld  public static final int javax.slee.facilities.Level.LEVEL_FINEST
fld  public static final int javax.slee.facilities.Level.LEVEL_INFO
fld  public static final int javax.slee.facilities.Level.LEVEL_OFF
fld  public static final int javax.slee.facilities.Level.LEVEL_SEVERE
fld  public static final int javax.slee.facilities.Level.LEVEL_WARNING
fld  public static final javax.slee.facilities.Level javax.slee.facilities.Level.CONFIG
fld  public static final javax.slee.facilities.Level javax.slee.facilities.Level.FINE
fld  public static final javax.slee.facilities.Level javax.slee.facilities.Level.FINER
fld  public static final javax.slee.facilities.Level javax.slee.facilities.Level.FINEST
fld  public static final javax.slee.facilities.Level javax.slee.facilities.Level.INFO
fld  public static final javax.slee.facilities.Level javax.slee.facilities.Level.OFF
fld  public static final javax.slee.facilities.Level javax.slee.facilities.Level.SEVERE
fld  public static final javax.slee.facilities.Level javax.slee.facilities.Level.WARNING
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean javax.slee.facilities.Level.equals(java.lang.Object)
meth public boolean javax.slee.facilities.Level.isConfig()
meth public boolean javax.slee.facilities.Level.isFine()
meth public boolean javax.slee.facilities.Level.isFiner()
meth public boolean javax.slee.facilities.Level.isFinest()
meth public boolean javax.slee.facilities.Level.isHigherLevel(javax.slee.facilities.Level) throws java.lang.NullPointerException
meth public boolean javax.slee.facilities.Level.isMinor()
meth public boolean javax.slee.facilities.Level.isOff()
meth public boolean javax.slee.facilities.Level.isSevere()
meth public boolean javax.slee.facilities.Level.isWarning()
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int javax.slee.facilities.Level.hashCode()
meth public int javax.slee.facilities.Level.toInt()
meth public java.lang.String javax.slee.facilities.Level.toString()
meth public static javax.slee.facilities.Level javax.slee.facilities.Level.fromInt(int) throws java.lang.IllegalArgumentException
supr java.lang.Object
CLSS public javax.slee.facilities.NameAlreadyBoundException
cons public javax.slee.facilities.NameAlreadyBoundException.NameAlreadyBoundException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public javax.slee.facilities.NameNotBoundException
cons public javax.slee.facilities.NameNotBoundException.NameNotBoundException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public abstract interface javax.slee.facilities.TimerEvent
meth public abstract int javax.slee.facilities.TimerEvent.getMissedRepetitions()
meth public abstract int javax.slee.facilities.TimerEvent.getNumRepetitions()
meth public abstract int javax.slee.facilities.TimerEvent.getRemainingRepetitions()
meth public abstract javax.slee.facilities.TimerID javax.slee.facilities.TimerEvent.getTimerID()
meth public abstract long javax.slee.facilities.TimerEvent.getExpiryTime()
meth public abstract long javax.slee.facilities.TimerEvent.getPeriod()
meth public abstract long javax.slee.facilities.TimerEvent.getScheduledTime()
supr null
CLSS public abstract interface javax.slee.facilities.TimerFacility
meth public abstract javax.slee.facilities.TimerID javax.slee.facilities.TimerFacility.setTimer(javax.slee.ActivityContextInterface,javax.slee.Address,long,javax.slee.facilities.TimerOptions) throws java.lang.IllegalArgumentException,java.lang.NullPointerException,javax.slee.TransactionRolledbackLocalException,javax.slee.facilities.FacilityException
meth public abstract javax.slee.facilities.TimerID javax.slee.facilities.TimerFacility.setTimer(javax.slee.ActivityContextInterface,javax.slee.Address,long,long,int,javax.slee.facilities.TimerOptions) throws java.lang.IllegalArgumentException,java.lang.NullPointerException,javax.slee.TransactionRolledbackLocalException,javax.slee.facilities.FacilityException
meth public abstract long javax.slee.facilities.TimerFacility.getDefaultTimeout() throws javax.slee.facilities.FacilityException
meth public abstract long javax.slee.facilities.TimerFacility.getResolution() throws javax.slee.facilities.FacilityException
meth public abstract void javax.slee.facilities.TimerFacility.cancelTimer(javax.slee.facilities.TimerID) throws java.lang.NullPointerException,javax.slee.TransactionRolledbackLocalException,javax.slee.facilities.FacilityException
supr null
CLSS public abstract interface javax.slee.facilities.TimerID
meth public abstract boolean javax.slee.facilities.TimerID.equals(java.lang.Object)
meth public abstract int javax.slee.facilities.TimerID.hashCode()
meth public abstract java.lang.String javax.slee.facilities.TimerID.toString()
supr null
CLSS public javax.slee.facilities.TimerOptions
cons public javax.slee.facilities.TimerOptions.TimerOptions()
cons public javax.slee.facilities.TimerOptions.TimerOptions(boolean,long,javax.slee.facilities.TimerPreserveMissed) throws java.lang.IllegalArgumentException,java.lang.NullPointerException
cons public javax.slee.facilities.TimerOptions.TimerOptions(javax.slee.facilities.TimerOptions)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean javax.slee.facilities.TimerOptions.equals(java.lang.Object)
meth public final boolean javax.slee.facilities.TimerOptions.isPersistent()
meth public final java.lang.Class java.lang.Object.getClass()
meth public final javax.slee.facilities.TimerPreserveMissed javax.slee.facilities.TimerOptions.getPreserveMissed()
meth public final long javax.slee.facilities.TimerOptions.getTimeout()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public final void javax.slee.facilities.TimerOptions.setPersistent(boolean)
meth public final void javax.slee.facilities.TimerOptions.setPreserveMissed(javax.slee.facilities.TimerPreserveMissed) throws java.lang.NullPointerException
meth public final void javax.slee.facilities.TimerOptions.setTimeout(long) throws java.lang.IllegalArgumentException
meth public int javax.slee.facilities.TimerOptions.hashCode()
meth public java.lang.String javax.slee.facilities.TimerOptions.toString()
supr java.lang.Object
CLSS public final javax.slee.facilities.TimerPreserveMissed
fld  public static final int javax.slee.facilities.TimerPreserveMissed.PRESERVE_ALL
fld  public static final int javax.slee.facilities.TimerPreserveMissed.PRESERVE_LAST
fld  public static final int javax.slee.facilities.TimerPreserveMissed.PRESERVE_NONE
fld  public static final javax.slee.facilities.TimerPreserveMissed javax.slee.facilities.TimerPreserveMissed.ALL
fld  public static final javax.slee.facilities.TimerPreserveMissed javax.slee.facilities.TimerPreserveMissed.LAST
fld  public static final javax.slee.facilities.TimerPreserveMissed javax.slee.facilities.TimerPreserveMissed.NONE
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean javax.slee.facilities.TimerPreserveMissed.equals(java.lang.Object)
meth public boolean javax.slee.facilities.TimerPreserveMissed.isAll()
meth public boolean javax.slee.facilities.TimerPreserveMissed.isLast()
meth public boolean javax.slee.facilities.TimerPreserveMissed.isNone()
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int javax.slee.facilities.TimerPreserveMissed.hashCode()
meth public int javax.slee.facilities.TimerPreserveMissed.toInt()
meth public java.lang.String javax.slee.facilities.TimerPreserveMissed.toString()
meth public static javax.slee.facilities.TimerPreserveMissed javax.slee.facilities.TimerPreserveMissed.fromInt(int) throws java.lang.IllegalArgumentException
supr java.lang.Object
CLSS public abstract interface javax.slee.facilities.TraceFacility
meth public abstract javax.slee.facilities.Level javax.slee.facilities.TraceFacility.getTraceLevel(javax.slee.ComponentID) throws java.lang.NullPointerException,javax.slee.UnrecognizedComponentException,javax.slee.facilities.FacilityException
meth public abstract void javax.slee.facilities.TraceFacility.createTrace(javax.slee.ComponentID,javax.slee.facilities.Level,java.lang.String,java.lang.String,java.lang.Throwable,long) throws java.lang.IllegalArgumentException,java.lang.NullPointerException,javax.slee.UnrecognizedComponentException,javax.slee.facilities.FacilityException
meth public abstract void javax.slee.facilities.TraceFacility.createTrace(javax.slee.ComponentID,javax.slee.facilities.Level,java.lang.String,java.lang.String,long) throws java.lang.IllegalArgumentException,java.lang.NullPointerException,javax.slee.UnrecognizedComponentException,javax.slee.facilities.FacilityException
supr null
CLSS public javax.slee.management.AlarmDuplicateFilter
cons public javax.slee.management.AlarmDuplicateFilter.AlarmDuplicateFilter(long)
intf java.io.Serializable
intf javax.management.NotificationFilter
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public boolean javax.slee.management.AlarmDuplicateFilter.isNotificationEnabled(javax.management.Notification)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Object.toString()
supr java.lang.Object
CLSS public javax.slee.management.AlarmLevelFilter
cons public javax.slee.management.AlarmLevelFilter.AlarmLevelFilter(javax.slee.facilities.Level)
intf java.io.Serializable
intf javax.management.NotificationFilter
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public boolean javax.slee.management.AlarmLevelFilter.isNotificationEnabled(javax.management.Notification)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Object.toString()
supr java.lang.Object
CLSS public abstract interface javax.slee.management.AlarmMBean
fld  public static final java.lang.String javax.slee.management.AlarmMBean.ALARM_NOTIFICATION_TYPE
supr null
CLSS public javax.slee.management.AlarmNotification
cons public javax.slee.management.AlarmNotification.AlarmNotification(javax.slee.management.AlarmMBean,java.lang.String,java.lang.Object,javax.slee.facilities.Level,java.lang.String,java.lang.Throwable,long,long) throws java.lang.IllegalArgumentException,java.lang.NullPointerException
fld  protected java.lang.Object javax.management.Notification.source
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean javax.slee.management.AlarmNotification.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final java.lang.Object javax.slee.management.AlarmNotification.getAlarmSource()
meth public final java.lang.String javax.slee.management.AlarmNotification.getAlarmType()
meth public final java.lang.Throwable javax.slee.management.AlarmNotification.getCause()
meth public final javax.slee.facilities.Level javax.slee.management.AlarmNotification.getLevel()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int javax.slee.management.AlarmNotification.hashCode()
meth public java.lang.Object javax.management.Notification.getSource()
meth public java.lang.Object javax.management.Notification.getUserData()
meth public java.lang.String javax.management.Notification.getMessage()
meth public java.lang.String javax.management.Notification.getType()
meth public java.lang.String javax.slee.management.AlarmNotification.toString()
meth public long javax.management.Notification.getSequenceNumber()
meth public long javax.management.Notification.getTimeStamp()
meth public void javax.management.Notification.setSequenceNumber(long)
meth public void javax.management.Notification.setSource(java.lang.Object) throws java.lang.IllegalArgumentException
meth public void javax.management.Notification.setTimeStamp(long)
meth public void javax.management.Notification.setUserData(java.lang.Object)
supr javax.management.Notification
CLSS public javax.slee.management.AlarmThresholdFilter
cons public javax.slee.management.AlarmThresholdFilter.AlarmThresholdFilter(int,long)
intf java.io.Serializable
intf javax.management.NotificationFilter
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public boolean javax.slee.management.AlarmThresholdFilter.isNotificationEnabled(javax.management.Notification)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Object.toString()
supr java.lang.Object
CLSS public javax.slee.management.AlreadyDeployedException
cons public javax.slee.management.AlreadyDeployedException.AlreadyDeployedException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.management.DeploymentException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr javax.slee.management.DeploymentException
CLSS public abstract interface javax.slee.management.ComponentDescriptor
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getName()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getSource()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVendor()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVersion()
meth public abstract javax.slee.ComponentID javax.slee.management.ComponentDescriptor.getID()
meth public abstract javax.slee.management.DeployableUnitID javax.slee.management.ComponentDescriptor.getDeployableUnit()
supr null
CLSS public javax.slee.management.DependencyException
cons public javax.slee.management.DependencyException.DependencyException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.management.DeploymentException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr javax.slee.management.DeploymentException
CLSS public abstract interface javax.slee.management.DeployableUnitDescriptor
meth public abstract [Ljavax.slee.ComponentID; javax.slee.management.DeployableUnitDescriptor.getComponents()
meth public abstract java.lang.String javax.slee.management.DeployableUnitDescriptor.getURL()
meth public abstract java.util.Date javax.slee.management.DeployableUnitDescriptor.getDeploymentDate()
supr null
CLSS public abstract interface javax.slee.management.DeployableUnitID
meth public abstract boolean javax.slee.management.DeployableUnitID.equals(java.lang.Object)
meth public abstract int javax.slee.management.DeployableUnitID.hashCode()
meth public abstract java.lang.String javax.slee.management.DeployableUnitID.toString()
supr null
CLSS public javax.slee.management.DeploymentException
cons public javax.slee.management.DeploymentException.DeploymentException(java.lang.String)
cons public javax.slee.management.DeploymentException.DeploymentException(java.lang.String,java.lang.Throwable)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.management.DeploymentException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public abstract interface javax.slee.management.DeploymentMBean
meth public abstract [Ljavax.slee.ComponentID; javax.slee.management.DeploymentMBean.getReferringComponents(javax.slee.ComponentID) throws java.lang.NullPointerException,javax.slee.UnrecognizedComponentException,javax.slee.management.ManagementException
meth public abstract [Ljavax.slee.EventTypeID; javax.slee.management.DeploymentMBean.getEventTypes() throws javax.slee.management.ManagementException
meth public abstract [Ljavax.slee.SbbID; javax.slee.management.DeploymentMBean.getSbbs() throws javax.slee.management.ManagementException
meth public abstract [Ljavax.slee.ServiceID; javax.slee.management.DeploymentMBean.getServices() throws javax.slee.management.ManagementException
meth public abstract [Ljavax.slee.management.ComponentDescriptor; javax.slee.management.DeploymentMBean.getDescriptors([Ljavax.slee.ComponentID;) throws java.lang.NullPointerException,javax.slee.management.ManagementException
meth public abstract [Ljavax.slee.management.DeployableUnitDescriptor; javax.slee.management.DeploymentMBean.getDescriptors([Ljavax.slee.management.DeployableUnitID;) throws java.lang.NullPointerException,javax.slee.management.ManagementException
meth public abstract [Ljavax.slee.management.DeployableUnitID; javax.slee.management.DeploymentMBean.getDeployableUnits() throws javax.slee.management.ManagementException
meth public abstract [Ljavax.slee.profile.ProfileSpecificationID; javax.slee.management.DeploymentMBean.getProfileSpecifications() throws javax.slee.management.ManagementException
meth public abstract [Ljavax.slee.resource.ResourceAdaptorID; javax.slee.management.DeploymentMBean.getResourceAdaptors() throws javax.slee.management.ManagementException
meth public abstract [Ljavax.slee.resource.ResourceAdaptorTypeID; javax.slee.management.DeploymentMBean.getResourceAdaptorTypes() throws javax.slee.management.ManagementException
meth public abstract boolean javax.slee.management.DeploymentMBean.isInstalled(javax.slee.ComponentID) throws java.lang.NullPointerException,javax.slee.management.ManagementException
meth public abstract boolean javax.slee.management.DeploymentMBean.isInstalled(javax.slee.management.DeployableUnitID) throws java.lang.NullPointerException,javax.slee.management.ManagementException
meth public abstract javax.slee.management.ComponentDescriptor javax.slee.management.DeploymentMBean.getDescriptor(javax.slee.ComponentID) throws java.lang.NullPointerException,javax.slee.UnrecognizedComponentException,javax.slee.management.ManagementException
meth public abstract javax.slee.management.DeployableUnitDescriptor javax.slee.management.DeploymentMBean.getDescriptor(javax.slee.management.DeployableUnitID) throws java.lang.NullPointerException,javax.slee.management.ManagementException,javax.slee.management.UnrecognizedDeployableUnitException
meth public abstract javax.slee.management.DeployableUnitID javax.slee.management.DeploymentMBean.getDeployableUnit(java.lang.String) throws java.lang.NullPointerException,javax.slee.management.ManagementException,javax.slee.management.UnrecognizedDeployableUnitException
meth public abstract javax.slee.management.DeployableUnitID javax.slee.management.DeploymentMBean.install(java.lang.String) throws java.lang.NullPointerException,java.net.MalformedURLException,javax.slee.management.AlreadyDeployedException,javax.slee.management.DeploymentException,javax.slee.management.ManagementException
meth public abstract void javax.slee.management.DeploymentMBean.uninstall(javax.slee.management.DeployableUnitID) throws java.lang.NullPointerException,javax.slee.InvalidStateException,javax.slee.management.DependencyException,javax.slee.management.ManagementException,javax.slee.management.UnrecognizedDeployableUnitException
supr null
CLSS public abstract interface javax.slee.management.EventTypeDescriptor
intf javax.slee.management.ComponentDescriptor
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getName()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getSource()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVendor()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVersion()
meth public abstract java.lang.String javax.slee.management.EventTypeDescriptor.getEventClassName()
meth public abstract javax.slee.ComponentID javax.slee.management.ComponentDescriptor.getID()
meth public abstract javax.slee.management.DeployableUnitID javax.slee.management.ComponentDescriptor.getDeployableUnit()
supr null
CLSS public javax.slee.management.ManagementException
cons public javax.slee.management.ManagementException.ManagementException(java.lang.String)
cons public javax.slee.management.ManagementException.ManagementException(java.lang.String,java.lang.Throwable)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.management.ManagementException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public javax.slee.management.PeerUnavailableException
cons public javax.slee.management.PeerUnavailableException.PeerUnavailableException(java.lang.String)
cons public javax.slee.management.PeerUnavailableException.PeerUnavailableException(java.lang.String,java.lang.Throwable)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.management.PeerUnavailableException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public abstract interface javax.slee.management.ProfileProvisioningMBean
meth public abstract java.util.Collection javax.slee.management.ProfileProvisioningMBean.getProfileTables() throws javax.slee.management.ManagementException
meth public abstract java.util.Collection javax.slee.management.ProfileProvisioningMBean.getProfiles(java.lang.String) throws java.lang.NullPointerException,javax.slee.management.ManagementException,javax.slee.profile.UnrecognizedProfileTableNameException
meth public abstract java.util.Collection javax.slee.management.ProfileProvisioningMBean.getProfilesByIndexedAttribute(java.lang.String,java.lang.String,java.lang.Object) throws java.lang.NullPointerException,javax.slee.management.ManagementException,javax.slee.profile.AttributeNotIndexedException,javax.slee.profile.AttributeTypeMismatchException,javax.slee.profile.UnrecognizedAttributeException,javax.slee.profile.UnrecognizedProfileTableNameException
meth public abstract javax.management.ObjectName javax.slee.management.ProfileProvisioningMBean.createProfile(java.lang.String,java.lang.String) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException,javax.slee.management.ManagementException,javax.slee.profile.ProfileAlreadyExistsException,javax.slee.profile.UnrecognizedProfileTableNameException
meth public abstract javax.management.ObjectName javax.slee.management.ProfileProvisioningMBean.getDefaultProfile(java.lang.String) throws java.lang.NullPointerException,javax.slee.management.ManagementException,javax.slee.profile.UnrecognizedProfileTableNameException
meth public abstract javax.management.ObjectName javax.slee.management.ProfileProvisioningMBean.getProfile(java.lang.String,java.lang.String) throws java.lang.NullPointerException,javax.slee.management.ManagementException,javax.slee.profile.UnrecognizedProfileNameException,javax.slee.profile.UnrecognizedProfileTableNameException
meth public abstract javax.slee.profile.ProfileSpecificationID javax.slee.management.ProfileProvisioningMBean.getProfileSpecification(java.lang.String) throws java.lang.NullPointerException,javax.slee.management.ManagementException,javax.slee.profile.UnrecognizedProfileTableNameException
meth public abstract void javax.slee.management.ProfileProvisioningMBean.createProfileTable(javax.slee.profile.ProfileSpecificationID,java.lang.String) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException,javax.slee.management.ManagementException,javax.slee.profile.ProfileTableAlreadyExistsException,javax.slee.profile.UnrecognizedProfileSpecificationException
meth public abstract void javax.slee.management.ProfileProvisioningMBean.removeProfile(java.lang.String,java.lang.String) throws java.lang.NullPointerException,javax.slee.management.ManagementException,javax.slee.profile.UnrecognizedProfileNameException,javax.slee.profile.UnrecognizedProfileTableNameException
meth public abstract void javax.slee.management.ProfileProvisioningMBean.removeProfileTable(java.lang.String) throws java.lang.NullPointerException,javax.slee.management.ManagementException,javax.slee.profile.UnrecognizedProfileTableNameException
meth public abstract void javax.slee.management.ProfileProvisioningMBean.renameProfileTable(java.lang.String,java.lang.String) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException,javax.slee.management.ManagementException,javax.slee.profile.ProfileTableAlreadyExistsException,javax.slee.profile.UnrecognizedProfileTableNameException
supr null
CLSS public abstract interface javax.slee.management.SbbDescriptor
intf javax.slee.management.ComponentDescriptor
meth public abstract [Ljava.lang.String; javax.slee.management.SbbDescriptor.getResourceAdaptorEntityLinks()
meth public abstract [Ljavax.slee.EventTypeID; javax.slee.management.SbbDescriptor.getEventTypes()
meth public abstract [Ljavax.slee.SbbID; javax.slee.management.SbbDescriptor.getSbbs()
meth public abstract [Ljavax.slee.profile.ProfileSpecificationID; javax.slee.management.SbbDescriptor.getProfileSpecifications()
meth public abstract [Ljavax.slee.resource.ResourceAdaptorTypeID; javax.slee.management.SbbDescriptor.getResourceAdaptorTypes()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getName()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getSource()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVendor()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVersion()
meth public abstract javax.slee.ComponentID javax.slee.management.ComponentDescriptor.getID()
meth public abstract javax.slee.management.DeployableUnitID javax.slee.management.ComponentDescriptor.getDeployableUnit()
meth public abstract javax.slee.profile.ProfileSpecificationID javax.slee.management.SbbDescriptor.getAddressProfileSpecification()
supr null
CLSS public abstract interface javax.slee.management.ServiceDescriptor
intf javax.slee.management.ComponentDescriptor
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getName()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getSource()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVendor()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVersion()
meth public abstract java.lang.String javax.slee.management.ServiceDescriptor.getAddressProfileTable()
meth public abstract java.lang.String javax.slee.management.ServiceDescriptor.getResourceInfoProfileTable()
meth public abstract javax.slee.ComponentID javax.slee.management.ComponentDescriptor.getID()
meth public abstract javax.slee.SbbID javax.slee.management.ServiceDescriptor.getRootSbb()
meth public abstract javax.slee.management.DeployableUnitID javax.slee.management.ComponentDescriptor.getDeployableUnit()
supr null
CLSS public abstract interface javax.slee.management.ServiceManagementMBean
meth public abstract [Ljavax.slee.ServiceID; javax.slee.management.ServiceManagementMBean.getServices(javax.slee.management.ServiceState) throws java.lang.NullPointerException,javax.slee.management.ManagementException
meth public abstract javax.management.ObjectName javax.slee.management.ServiceManagementMBean.getServiceUsageMBean(javax.slee.ServiceID) throws java.lang.NullPointerException,javax.slee.UnrecognizedServiceException,javax.slee.management.ManagementException
meth public abstract javax.slee.management.ServiceState javax.slee.management.ServiceManagementMBean.getState(javax.slee.ServiceID) throws java.lang.NullPointerException,javax.slee.UnrecognizedServiceException,javax.slee.management.ManagementException
meth public abstract void javax.slee.management.ServiceManagementMBean.activate([Ljavax.slee.ServiceID;) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException,javax.slee.InvalidStateException,javax.slee.UnrecognizedServiceException,javax.slee.management.ManagementException
meth public abstract void javax.slee.management.ServiceManagementMBean.activate(javax.slee.ServiceID) throws java.lang.NullPointerException,javax.slee.InvalidStateException,javax.slee.UnrecognizedServiceException,javax.slee.management.ManagementException
meth public abstract void javax.slee.management.ServiceManagementMBean.deactivate([Ljavax.slee.ServiceID;) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException,javax.slee.InvalidStateException,javax.slee.UnrecognizedServiceException,javax.slee.management.ManagementException
meth public abstract void javax.slee.management.ServiceManagementMBean.deactivate(javax.slee.ServiceID) throws java.lang.NullPointerException,javax.slee.InvalidStateException,javax.slee.UnrecognizedServiceException,javax.slee.management.ManagementException
meth public abstract void javax.slee.management.ServiceManagementMBean.deactivateAndActivate([Ljavax.slee.ServiceID;,[Ljavax.slee.ServiceID;) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException,javax.slee.InvalidStateException,javax.slee.UnrecognizedServiceException,javax.slee.management.ManagementException
meth public abstract void javax.slee.management.ServiceManagementMBean.deactivateAndActivate(javax.slee.ServiceID,javax.slee.ServiceID) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException,javax.slee.InvalidStateException,javax.slee.UnrecognizedServiceException,javax.slee.management.ManagementException
supr null
CLSS public final javax.slee.management.ServiceState
fld  public static final int javax.slee.management.ServiceState.SERVICE_ACTIVE
fld  public static final int javax.slee.management.ServiceState.SERVICE_INACTIVE
fld  public static final int javax.slee.management.ServiceState.SERVICE_STOPPING
fld  public static final javax.slee.management.ServiceState javax.slee.management.ServiceState.ACTIVE
fld  public static final javax.slee.management.ServiceState javax.slee.management.ServiceState.INACTIVE
fld  public static final javax.slee.management.ServiceState javax.slee.management.ServiceState.STOPPING
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean javax.slee.management.ServiceState.equals(java.lang.Object)
meth public boolean javax.slee.management.ServiceState.isActive()
meth public boolean javax.slee.management.ServiceState.isInactive()
meth public boolean javax.slee.management.ServiceState.isStopping()
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int javax.slee.management.ServiceState.hashCode()
meth public int javax.slee.management.ServiceState.toInt()
meth public java.lang.String javax.slee.management.ServiceState.toString()
meth public static javax.slee.management.ServiceState javax.slee.management.ServiceState.fromInt(int) throws java.lang.IllegalArgumentException
supr java.lang.Object
CLSS public abstract interface javax.slee.management.ServiceUsageMBean
meth public abstract [Ljava.lang.String; javax.slee.management.ServiceUsageMBean.getUsageParameterSets(javax.slee.SbbID) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException,javax.slee.UnrecognizedSbbException,javax.slee.management.ManagementException
meth public abstract javax.management.ObjectName javax.slee.management.ServiceUsageMBean.getSbbUsageMBean(javax.slee.SbbID) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException,javax.slee.UnrecognizedSbbException,javax.slee.management.ManagementException
meth public abstract javax.management.ObjectName javax.slee.management.ServiceUsageMBean.getSbbUsageMBean(javax.slee.SbbID,java.lang.String) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException,javax.slee.UnrecognizedSbbException,javax.slee.management.ManagementException,javax.slee.usage.UnrecognizedUsageParameterSetNameException
meth public abstract javax.slee.ServiceID javax.slee.management.ServiceUsageMBean.getService() throws javax.slee.management.ManagementException
meth public abstract void javax.slee.management.ServiceUsageMBean.close() throws javax.slee.management.ManagementException
meth public abstract void javax.slee.management.ServiceUsageMBean.createUsageParameterSet(javax.slee.SbbID,java.lang.String) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException,javax.slee.UnrecognizedSbbException,javax.slee.management.ManagementException,javax.slee.management.UsageParameterSetNameAlreadyExistsException
meth public abstract void javax.slee.management.ServiceUsageMBean.removeUsageParameterSet(javax.slee.SbbID,java.lang.String) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException,javax.slee.UnrecognizedSbbException,javax.slee.management.ManagementException,javax.slee.usage.UnrecognizedUsageParameterSetNameException
meth public abstract void javax.slee.management.ServiceUsageMBean.resetAllUsageParameters() throws javax.slee.management.ManagementException
meth public abstract void javax.slee.management.ServiceUsageMBean.resetAllUsageParameters(javax.slee.SbbID) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException,javax.slee.UnrecognizedSbbException,javax.slee.management.ManagementException
supr null
CLSS public abstract interface javax.slee.management.SleeManagementMBean
fld  public static final java.lang.String javax.slee.management.SleeManagementMBean.SLEE_STATE_CHANGE_NOTIFICATION_TYPE
meth public abstract javax.management.ObjectName javax.slee.management.SleeManagementMBean.getAlarmMBean()
meth public abstract javax.management.ObjectName javax.slee.management.SleeManagementMBean.getDeploymentMBean()
meth public abstract javax.management.ObjectName javax.slee.management.SleeManagementMBean.getProfileProvisioningMBean()
meth public abstract javax.management.ObjectName javax.slee.management.SleeManagementMBean.getServiceManagementMBean()
meth public abstract javax.management.ObjectName javax.slee.management.SleeManagementMBean.getTraceMBean()
meth public abstract javax.slee.management.SleeState javax.slee.management.SleeManagementMBean.getState() throws javax.slee.management.ManagementException
meth public abstract void javax.slee.management.SleeManagementMBean.shutdown() throws javax.slee.InvalidStateException,javax.slee.management.ManagementException
meth public abstract void javax.slee.management.SleeManagementMBean.start() throws javax.slee.InvalidStateException,javax.slee.management.ManagementException
meth public abstract void javax.slee.management.SleeManagementMBean.stop() throws javax.slee.InvalidStateException,javax.slee.management.ManagementException
supr null
CLSS public abstract interface javax.slee.management.SleeProvider
meth public abstract javax.management.ObjectName javax.slee.management.SleeProvider.getSleeManagementMBean()
supr null
CLSS public final javax.slee.management.SleeProviderFactory
cons public javax.slee.management.SleeProviderFactory.SleeProviderFactory()
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Object.toString()
meth public static javax.slee.management.SleeProvider javax.slee.management.SleeProviderFactory.getSleeProvider(java.lang.String) throws java.lang.NullPointerException,javax.slee.management.PeerUnavailableException
meth public static javax.slee.management.SleeProvider javax.slee.management.SleeProviderFactory.getSleeProvider(java.lang.String,java.lang.ClassLoader) throws java.lang.NullPointerException,javax.slee.management.PeerUnavailableException
supr java.lang.Object
CLSS public final javax.slee.management.SleeState
fld  public static final int javax.slee.management.SleeState.SLEE_RUNNING
fld  public static final int javax.slee.management.SleeState.SLEE_STARTING
fld  public static final int javax.slee.management.SleeState.SLEE_STOPPED
fld  public static final int javax.slee.management.SleeState.SLEE_STOPPING
fld  public static final javax.slee.management.SleeState javax.slee.management.SleeState.RUNNING
fld  public static final javax.slee.management.SleeState javax.slee.management.SleeState.STARTING
fld  public static final javax.slee.management.SleeState javax.slee.management.SleeState.STOPPED
fld  public static final javax.slee.management.SleeState javax.slee.management.SleeState.STOPPING
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean javax.slee.management.SleeState.equals(java.lang.Object)
meth public boolean javax.slee.management.SleeState.isRunning()
meth public boolean javax.slee.management.SleeState.isStarting()
meth public boolean javax.slee.management.SleeState.isStopped()
meth public boolean javax.slee.management.SleeState.isStopping()
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int javax.slee.management.SleeState.hashCode()
meth public int javax.slee.management.SleeState.toInt()
meth public java.lang.String javax.slee.management.SleeState.toString()
meth public static javax.slee.management.SleeState javax.slee.management.SleeState.fromInt(int) throws java.lang.IllegalArgumentException
supr java.lang.Object
CLSS public javax.slee.management.SleeStateChangeNotification
cons public javax.slee.management.SleeStateChangeNotification.SleeStateChangeNotification(javax.slee.management.SleeManagementMBean,javax.slee.management.SleeState,javax.slee.management.SleeState,long) throws java.lang.NullPointerException
fld  protected java.lang.Object javax.management.Notification.source
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final javax.slee.management.SleeState javax.slee.management.SleeStateChangeNotification.getNewState()
meth public final javax.slee.management.SleeState javax.slee.management.SleeStateChangeNotification.getOldState()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.Object javax.management.Notification.getSource()
meth public java.lang.Object javax.management.Notification.getUserData()
meth public java.lang.String javax.management.Notification.getMessage()
meth public java.lang.String javax.management.Notification.getType()
meth public java.lang.String javax.slee.management.SleeStateChangeNotification.toString()
meth public long javax.management.Notification.getSequenceNumber()
meth public long javax.management.Notification.getTimeStamp()
meth public void javax.management.Notification.setSequenceNumber(long)
meth public void javax.management.Notification.setSource(java.lang.Object) throws java.lang.IllegalArgumentException
meth public void javax.management.Notification.setTimeStamp(long)
meth public void javax.management.Notification.setUserData(java.lang.Object)
supr javax.management.Notification
CLSS public javax.slee.management.TraceLevelFilter
cons public javax.slee.management.TraceLevelFilter.TraceLevelFilter(javax.slee.facilities.Level)
intf java.io.Serializable
intf javax.management.NotificationFilter
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public boolean javax.slee.management.TraceLevelFilter.isNotificationEnabled(javax.management.Notification)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Object.toString()
supr java.lang.Object
CLSS public abstract interface javax.slee.management.TraceMBean
fld  public static final java.lang.String javax.slee.management.TraceMBean.TRACE_NOTIFICATION_TYPE
meth public abstract javax.slee.facilities.Level javax.slee.management.TraceMBean.getTraceLevel(javax.slee.ComponentID) throws java.lang.NullPointerException,javax.slee.UnrecognizedComponentException,javax.slee.management.ManagementException
meth public abstract void javax.slee.management.TraceMBean.setTraceLevel(javax.slee.ComponentID,javax.slee.facilities.Level) throws java.lang.NullPointerException,javax.slee.UnrecognizedComponentException,javax.slee.management.ManagementException
supr null
CLSS public javax.slee.management.TraceNotification
cons public javax.slee.management.TraceNotification.TraceNotification(javax.slee.management.TraceMBean,java.lang.String,java.lang.Object,javax.slee.facilities.Level,java.lang.String,java.lang.Throwable,long,long) throws java.lang.IllegalArgumentException,java.lang.NullPointerException
fld  protected java.lang.Object javax.management.Notification.source
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean javax.slee.management.TraceNotification.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final java.lang.Object javax.slee.management.TraceNotification.getMessageSource()
meth public final java.lang.String javax.slee.management.TraceNotification.getMessageType()
meth public final java.lang.Throwable javax.slee.management.TraceNotification.getCause()
meth public final javax.slee.facilities.Level javax.slee.management.TraceNotification.getLevel()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int javax.slee.management.TraceNotification.hashCode()
meth public java.lang.Object javax.management.Notification.getSource()
meth public java.lang.Object javax.management.Notification.getUserData()
meth public java.lang.String javax.management.Notification.getMessage()
meth public java.lang.String javax.management.Notification.getType()
meth public java.lang.String javax.slee.management.TraceNotification.toString()
meth public long javax.management.Notification.getSequenceNumber()
meth public long javax.management.Notification.getTimeStamp()
meth public void javax.management.Notification.setSequenceNumber(long)
meth public void javax.management.Notification.setSource(java.lang.Object) throws java.lang.IllegalArgumentException
meth public void javax.management.Notification.setTimeStamp(long)
meth public void javax.management.Notification.setUserData(java.lang.Object)
supr javax.management.Notification
CLSS public javax.slee.management.UnrecognizedDeployableUnitException
cons public javax.slee.management.UnrecognizedDeployableUnitException.UnrecognizedDeployableUnitException()
cons public javax.slee.management.UnrecognizedDeployableUnitException.UnrecognizedDeployableUnitException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr javax.slee.UnrecognizedComponentException
CLSS public javax.slee.management.UsageParameterSetNameAlreadyExistsException
cons public javax.slee.management.UsageParameterSetNameAlreadyExistsException.UsageParameterSetNameAlreadyExistsException()
cons public javax.slee.management.UsageParameterSetNameAlreadyExistsException.UsageParameterSetNameAlreadyExistsException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public abstract interface javax.slee.nullactivity.NullActivity
meth public abstract void javax.slee.nullactivity.NullActivity.endActivity() throws javax.slee.SLEEException,javax.slee.TransactionRequiredLocalException
supr null
CLSS public abstract interface javax.slee.nullactivity.NullActivityContextInterfaceFactory
meth public abstract javax.slee.ActivityContextInterface javax.slee.nullactivity.NullActivityContextInterfaceFactory.getActivityContextInterface(javax.slee.nullactivity.NullActivity) throws java.lang.NullPointerException,javax.slee.FactoryException,javax.slee.TransactionRequiredLocalException,javax.slee.UnrecognizedActivityException
supr null
CLSS public abstract interface javax.slee.nullactivity.NullActivityFactory
meth public abstract javax.slee.nullactivity.NullActivity javax.slee.nullactivity.NullActivityFactory.createNullActivity() throws javax.slee.FactoryException,javax.slee.TransactionRequiredLocalException
supr null
CLSS public abstract interface javax.slee.profile.AddressProfileCMP
meth public abstract [Ljavax.slee.Address; javax.slee.profile.AddressProfileCMP.getAddresses()
meth public abstract void javax.slee.profile.AddressProfileCMP.setAddresses([Ljavax.slee.Address;)
supr null
CLSS public javax.slee.profile.AttributeNotIndexedException
cons public javax.slee.profile.AttributeNotIndexedException.AttributeNotIndexedException()
cons public javax.slee.profile.AttributeNotIndexedException.AttributeNotIndexedException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public javax.slee.profile.AttributeTypeMismatchException
cons public javax.slee.profile.AttributeTypeMismatchException.AttributeTypeMismatchException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public abstract interface javax.slee.profile.ProfileAddedEvent
meth public abstract java.lang.Object javax.slee.profile.ProfileAddedEvent.getAddedProfile()
meth public abstract javax.slee.Address javax.slee.profile.ProfileAddedEvent.getProfileAddress()
meth public abstract javax.slee.profile.ProfileID javax.slee.profile.ProfileAddedEvent.getProfile()
supr null
CLSS public javax.slee.profile.ProfileAlreadyExistsException
cons public javax.slee.profile.ProfileAlreadyExistsException.ProfileAlreadyExistsException()
cons public javax.slee.profile.ProfileAlreadyExistsException.ProfileAlreadyExistsException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public abstract interface javax.slee.profile.ProfileFacility
meth public abstract java.util.Collection javax.slee.profile.ProfileFacility.getProfiles(java.lang.String) throws java.lang.NullPointerException,javax.slee.TransactionRolledbackLocalException,javax.slee.facilities.FacilityException,javax.slee.profile.UnrecognizedProfileTableNameException
meth public abstract java.util.Collection javax.slee.profile.ProfileFacility.getProfilesByIndexedAttribute(java.lang.String,java.lang.String,java.lang.Object) throws java.lang.NullPointerException,javax.slee.TransactionRolledbackLocalException,javax.slee.facilities.FacilityException,javax.slee.profile.AttributeNotIndexedException,javax.slee.profile.AttributeTypeMismatchException,javax.slee.profile.UnrecognizedAttributeException,javax.slee.profile.UnrecognizedProfileTableNameException
meth public abstract javax.slee.profile.ProfileID javax.slee.profile.ProfileFacility.getProfileByIndexedAttribute(java.lang.String,java.lang.String,java.lang.Object) throws java.lang.NullPointerException,javax.slee.TransactionRolledbackLocalException,javax.slee.facilities.FacilityException,javax.slee.profile.AttributeNotIndexedException,javax.slee.profile.AttributeTypeMismatchException,javax.slee.profile.UnrecognizedAttributeException,javax.slee.profile.UnrecognizedProfileTableNameException
meth public abstract javax.slee.profile.ProfileTableActivity javax.slee.profile.ProfileFacility.getProfileTableActivity(java.lang.String) throws java.lang.NullPointerException,javax.slee.TransactionRolledbackLocalException,javax.slee.facilities.FacilityException,javax.slee.profile.UnrecognizedProfileTableNameException
supr null
CLSS public javax.slee.profile.ProfileID
cons public javax.slee.profile.ProfileID.ProfileID(java.lang.String,java.lang.String) throws java.lang.IllegalArgumentException,java.lang.NullPointerException
cons public javax.slee.profile.ProfileID.ProfileID(javax.slee.Address) throws java.lang.IllegalArgumentException,java.lang.NullPointerException
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean javax.slee.profile.ProfileID.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final java.lang.String javax.slee.profile.ProfileID.getProfileName()
meth public final java.lang.String javax.slee.profile.ProfileID.getProfileTableName()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public final void javax.slee.profile.ProfileID.setProfileID(java.lang.String,java.lang.String) throws java.lang.IllegalArgumentException,java.lang.NullPointerException
meth public int javax.slee.profile.ProfileID.hashCode()
meth public java.lang.String javax.slee.profile.ProfileID.toString()
meth public javax.slee.Address javax.slee.profile.ProfileID.toAddress()
supr java.lang.Object
CLSS public javax.slee.profile.ProfileImplementationException
cons public javax.slee.profile.ProfileImplementationException.ProfileImplementationException(java.lang.Throwable)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.profile.ProfileImplementationException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public abstract interface javax.slee.profile.ProfileMBean
meth public abstract boolean javax.slee.profile.ProfileMBean.isProfileDirty() throws javax.slee.management.ManagementException
meth public abstract boolean javax.slee.profile.ProfileMBean.isProfileWriteable() throws javax.slee.management.ManagementException
meth public abstract void javax.slee.profile.ProfileMBean.closeProfile() throws javax.slee.InvalidStateException,javax.slee.management.ManagementException
meth public abstract void javax.slee.profile.ProfileMBean.commitProfile() throws javax.slee.InvalidStateException,javax.slee.management.ManagementException,javax.slee.profile.ProfileVerificationException
meth public abstract void javax.slee.profile.ProfileMBean.editProfile() throws javax.slee.management.ManagementException
meth public abstract void javax.slee.profile.ProfileMBean.restoreProfile() throws javax.slee.InvalidStateException,javax.slee.management.ManagementException
supr null
CLSS public abstract interface javax.slee.profile.ProfileManagement
meth public abstract boolean javax.slee.profile.ProfileManagement.isProfileDirty()
meth public abstract boolean javax.slee.profile.ProfileManagement.isProfileValid(javax.slee.profile.ProfileID) throws java.lang.NullPointerException,javax.slee.SLEEException
meth public abstract void javax.slee.profile.ProfileManagement.markProfileDirty()
meth public abstract void javax.slee.profile.ProfileManagement.profileInitialize()
meth public abstract void javax.slee.profile.ProfileManagement.profileLoad()
meth public abstract void javax.slee.profile.ProfileManagement.profileStore()
meth public abstract void javax.slee.profile.ProfileManagement.profileVerify() throws javax.slee.profile.ProfileVerificationException
supr null
CLSS public abstract interface javax.slee.profile.ProfileRemovedEvent
meth public abstract java.lang.Object javax.slee.profile.ProfileRemovedEvent.getRemovedProfile()
meth public abstract javax.slee.Address javax.slee.profile.ProfileRemovedEvent.getProfileAddress()
meth public abstract javax.slee.profile.ProfileID javax.slee.profile.ProfileRemovedEvent.getProfile()
supr null
CLSS public abstract interface javax.slee.profile.ProfileSpecificationDescriptor
intf javax.slee.management.ComponentDescriptor
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getName()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getSource()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVendor()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVersion()
meth public abstract java.lang.String javax.slee.profile.ProfileSpecificationDescriptor.getCMPInterfaceName()
meth public abstract javax.slee.ComponentID javax.slee.management.ComponentDescriptor.getID()
meth public abstract javax.slee.management.DeployableUnitID javax.slee.management.ComponentDescriptor.getDeployableUnit()
supr null
CLSS public abstract interface javax.slee.profile.ProfileSpecificationID
intf javax.slee.ComponentID
meth public abstract boolean javax.slee.ComponentID.equals(java.lang.Object)
meth public abstract int javax.slee.ComponentID.hashCode()
meth public abstract java.lang.String javax.slee.ComponentID.toString()
supr null
CLSS public abstract interface javax.slee.profile.ProfileTableActivity
meth public abstract java.lang.String javax.slee.profile.ProfileTableActivity.getProfileTableName()
supr null
CLSS public abstract interface javax.slee.profile.ProfileTableActivityContextInterfaceFactory
meth public abstract javax.slee.ActivityContextInterface javax.slee.profile.ProfileTableActivityContextInterfaceFactory.getActivityContextInterface(javax.slee.profile.ProfileTableActivity) throws java.lang.NullPointerException,javax.slee.FactoryException,javax.slee.TransactionRequiredLocalException,javax.slee.UnrecognizedActivityException
supr null
CLSS public javax.slee.profile.ProfileTableAlreadyExistsException
cons public javax.slee.profile.ProfileTableAlreadyExistsException.ProfileTableAlreadyExistsException()
cons public javax.slee.profile.ProfileTableAlreadyExistsException.ProfileTableAlreadyExistsException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public abstract interface javax.slee.profile.ProfileUpdatedEvent
meth public abstract java.lang.Object javax.slee.profile.ProfileUpdatedEvent.getAfterUpdateProfile()
meth public abstract java.lang.Object javax.slee.profile.ProfileUpdatedEvent.getBeforeUpdateProfile()
meth public abstract javax.slee.Address javax.slee.profile.ProfileUpdatedEvent.getProfileAddress()
meth public abstract javax.slee.profile.ProfileID javax.slee.profile.ProfileUpdatedEvent.getProfile()
supr null
CLSS public javax.slee.profile.ProfileVerificationException
cons public javax.slee.profile.ProfileVerificationException.ProfileVerificationException(java.lang.String)
cons public javax.slee.profile.ProfileVerificationException.ProfileVerificationException(java.lang.String,java.lang.Throwable)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public java.lang.Throwable javax.slee.profile.ProfileVerificationException.getCause()
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public javax.slee.profile.ReadOnlyProfileException
cons public javax.slee.profile.ReadOnlyProfileException.ReadOnlyProfileException()
cons public javax.slee.profile.ReadOnlyProfileException.ReadOnlyProfileException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.RuntimeException
CLSS public abstract interface javax.slee.profile.ResourceInfoProfileCMP
meth public abstract java.lang.String javax.slee.profile.ResourceInfoProfileCMP.getInfo()
meth public abstract void javax.slee.profile.ResourceInfoProfileCMP.setInfo(java.lang.String)
supr null
CLSS public javax.slee.profile.UnrecognizedAttributeException
cons public javax.slee.profile.UnrecognizedAttributeException.UnrecognizedAttributeException()
cons public javax.slee.profile.UnrecognizedAttributeException.UnrecognizedAttributeException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public javax.slee.profile.UnrecognizedProfileNameException
cons public javax.slee.profile.UnrecognizedProfileNameException.UnrecognizedProfileNameException()
cons public javax.slee.profile.UnrecognizedProfileNameException.UnrecognizedProfileNameException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public javax.slee.profile.UnrecognizedProfileSpecificationException
cons public javax.slee.profile.UnrecognizedProfileSpecificationException.UnrecognizedProfileSpecificationException()
cons public javax.slee.profile.UnrecognizedProfileSpecificationException.UnrecognizedProfileSpecificationException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr javax.slee.UnrecognizedComponentException
CLSS public javax.slee.profile.UnrecognizedProfileTableNameException
cons public javax.slee.profile.UnrecognizedProfileTableNameException.UnrecognizedProfileTableNameException()
cons public javax.slee.profile.UnrecognizedProfileTableNameException.UnrecognizedProfileTableNameException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public abstract interface javax.slee.resource.ResourceAdaptorDescriptor
intf javax.slee.management.ComponentDescriptor
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getName()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getSource()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVendor()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVersion()
meth public abstract javax.slee.ComponentID javax.slee.management.ComponentDescriptor.getID()
meth public abstract javax.slee.management.DeployableUnitID javax.slee.management.ComponentDescriptor.getDeployableUnit()
meth public abstract javax.slee.resource.ResourceAdaptorTypeID javax.slee.resource.ResourceAdaptorDescriptor.getResourceAdaptorType()
supr null
CLSS public abstract interface javax.slee.resource.ResourceAdaptorID
intf javax.slee.ComponentID
meth public abstract boolean javax.slee.ComponentID.equals(java.lang.Object)
meth public abstract int javax.slee.ComponentID.hashCode()
meth public abstract java.lang.String javax.slee.ComponentID.toString()
supr null
CLSS public abstract interface javax.slee.resource.ResourceAdaptorTypeDescriptor
intf javax.slee.management.ComponentDescriptor
meth public abstract [Ljavax.slee.EventTypeID; javax.slee.resource.ResourceAdaptorTypeDescriptor.getEventTypes()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getName()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getSource()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVendor()
meth public abstract java.lang.String javax.slee.management.ComponentDescriptor.getVersion()
meth public abstract javax.slee.ComponentID javax.slee.management.ComponentDescriptor.getID()
meth public abstract javax.slee.management.DeployableUnitID javax.slee.management.ComponentDescriptor.getDeployableUnit()
supr null
CLSS public abstract interface javax.slee.resource.ResourceAdaptorTypeID
intf javax.slee.ComponentID
meth public abstract boolean javax.slee.ComponentID.equals(java.lang.Object)
meth public abstract int javax.slee.ComponentID.hashCode()
meth public abstract java.lang.String javax.slee.ComponentID.toString()
supr null
CLSS public abstract interface javax.slee.serviceactivity.ServiceActivity
supr null
CLSS public abstract interface javax.slee.serviceactivity.ServiceActivityContextInterfaceFactory
meth public abstract javax.slee.ActivityContextInterface javax.slee.serviceactivity.ServiceActivityContextInterfaceFactory.getActivityContextInterface(javax.slee.serviceactivity.ServiceActivity) throws java.lang.NullPointerException,javax.slee.FactoryException,javax.slee.TransactionRequiredLocalException,javax.slee.UnrecognizedActivityException
supr null
CLSS public abstract interface javax.slee.serviceactivity.ServiceActivityFactory
meth public abstract javax.slee.serviceactivity.ServiceActivity javax.slee.serviceactivity.ServiceActivityFactory.getActivity() throws javax.slee.FactoryException,javax.slee.TransactionRequiredLocalException
supr null
CLSS public abstract interface javax.slee.serviceactivity.ServiceStartedEvent
supr null
CLSS public abstract interface javax.slee.usage.SampleStatistics
meth public abstract double javax.slee.usage.SampleStatistics.getMean()
meth public abstract long javax.slee.usage.SampleStatistics.getMaximum()
meth public abstract long javax.slee.usage.SampleStatistics.getMinimum()
meth public abstract long javax.slee.usage.SampleStatistics.getSampleCount()
supr null
CLSS public abstract interface javax.slee.usage.SbbUsageMBean
fld  public static final java.lang.String javax.slee.usage.SbbUsageMBean.USAGE_NOTIFICATION_TYPE
meth public abstract java.lang.String javax.slee.usage.SbbUsageMBean.getUsageParameterSet() throws javax.slee.management.ManagementException
meth public abstract javax.slee.SbbID javax.slee.usage.SbbUsageMBean.getSbb() throws javax.slee.management.ManagementException
meth public abstract javax.slee.ServiceID javax.slee.usage.SbbUsageMBean.getService() throws javax.slee.management.ManagementException
meth public abstract void javax.slee.usage.SbbUsageMBean.close() throws javax.slee.InvalidStateException,javax.slee.management.ManagementException
meth public abstract void javax.slee.usage.SbbUsageMBean.resetAllUsageParameters() throws javax.slee.management.ManagementException
supr null
CLSS public javax.slee.usage.UnrecognizedUsageParameterSetNameException
cons public javax.slee.usage.UnrecognizedUsageParameterSetNameException.UnrecognizedUsageParameterSetNameException()
cons public javax.slee.usage.UnrecognizedUsageParameterSetNameException.UnrecognizedUsageParameterSetNameException(java.lang.String)
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public [Ljava.lang.StackTraceElement; java.lang.Throwable.getStackTrace()
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Throwable.getLocalizedMessage()
meth public java.lang.String java.lang.Throwable.getMessage()
meth public java.lang.String java.lang.Throwable.toString()
meth public java.lang.Throwable java.lang.Throwable.fillInStackTrace()
meth public java.lang.Throwable java.lang.Throwable.getCause()
meth public java.lang.Throwable java.lang.Throwable.initCause(java.lang.Throwable)
meth public void java.lang.Throwable.printStackTrace()
meth public void java.lang.Throwable.printStackTrace(java.io.PrintStream)
meth public void java.lang.Throwable.printStackTrace(java.io.PrintWriter)
meth public void java.lang.Throwable.setStackTrace([Ljava.lang.StackTraceElement;)
supr java.lang.Exception
CLSS public javax.slee.usage.UsageNotification
cons public javax.slee.usage.UsageNotification.UsageNotification(javax.slee.usage.SbbUsageMBean,javax.slee.ServiceID,javax.slee.SbbID,java.lang.String,java.lang.String,boolean,long,long,long) throws java.lang.NullPointerException
fld  protected java.lang.Object javax.management.Notification.source
intf java.io.Serializable
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean javax.slee.usage.UsageNotification.equals(java.lang.Object)
meth public final boolean javax.slee.usage.UsageNotification.isCounter()
meth public final java.lang.Class java.lang.Object.getClass()
meth public final java.lang.String javax.slee.usage.UsageNotification.getUsageParameterName()
meth public final java.lang.String javax.slee.usage.UsageNotification.getUsageParameterSetName()
meth public final javax.slee.SbbID javax.slee.usage.UsageNotification.getSbb()
meth public final javax.slee.ServiceID javax.slee.usage.UsageNotification.getService()
meth public final long javax.slee.usage.UsageNotification.getValue()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int javax.slee.usage.UsageNotification.hashCode()
meth public java.lang.Object javax.management.Notification.getSource()
meth public java.lang.Object javax.management.Notification.getUserData()
meth public java.lang.String javax.management.Notification.getMessage()
meth public java.lang.String javax.management.Notification.getType()
meth public java.lang.String javax.slee.usage.UsageNotification.toString()
meth public long javax.management.Notification.getSequenceNumber()
meth public long javax.management.Notification.getTimeStamp()
meth public void javax.management.Notification.setSequenceNumber(long)
meth public void javax.management.Notification.setSource(java.lang.Object) throws java.lang.IllegalArgumentException
meth public void javax.management.Notification.setTimeStamp(long)
meth public void javax.management.Notification.setUserData(java.lang.Object)
supr javax.management.Notification
CLSS public javax.slee.usage.UsageOutOfRangeFilter
cons public javax.slee.usage.UsageOutOfRangeFilter.UsageOutOfRangeFilter(javax.slee.ServiceID,javax.slee.SbbID,java.lang.String,long,long) throws java.lang.NullPointerException,javax.slee.InvalidArgumentException
intf java.io.Serializable
intf javax.management.NotificationFilter
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public boolean javax.slee.usage.UsageOutOfRangeFilter.isNotificationEnabled(javax.management.Notification)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Object.toString()
supr java.lang.Object
CLSS public javax.slee.usage.UsageThresholdFilter
cons public javax.slee.usage.UsageThresholdFilter.UsageThresholdFilter(javax.slee.ServiceID,javax.slee.SbbID,java.lang.String,long) throws java.lang.NullPointerException
intf java.io.Serializable
intf javax.management.NotificationFilter
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public boolean javax.slee.usage.UsageThresholdFilter.isNotificationEnabled(javax.management.Notification)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Object.toString()
supr java.lang.Object
CLSS public javax.slee.usage.UsageUpdatedFilter
cons public javax.slee.usage.UsageUpdatedFilter.UsageUpdatedFilter(javax.slee.ServiceID,javax.slee.SbbID,java.lang.String) throws java.lang.NullPointerException
intf java.io.Serializable
intf javax.management.NotificationFilter
meth protected java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
meth protected void java.lang.Object.finalize() throws java.lang.Throwable
meth public boolean java.lang.Object.equals(java.lang.Object)
meth public boolean javax.slee.usage.UsageUpdatedFilter.isNotificationEnabled(javax.management.Notification)
meth public final java.lang.Class java.lang.Object.getClass()
meth public final void java.lang.Object.notify()
meth public final void java.lang.Object.notifyAll()
meth public final void java.lang.Object.wait() throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
meth public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
meth public int java.lang.Object.hashCode()
meth public java.lang.String java.lang.Object.toString()
supr java.lang.Object
