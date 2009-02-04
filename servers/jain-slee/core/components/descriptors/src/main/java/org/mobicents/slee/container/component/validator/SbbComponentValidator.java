/**
 * Start time:17:06:21 2009-01-30<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.container.component.validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.component.SbbComponent;
import org.mobicents.slee.container.component.deployment.jaxb.descriptors.sbb.MGetChildRelationMethod;

/**
 * Start time:17:06:21 2009-01-30<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SbbComponentValidator implements Validator {

	// public static final String _SBB_EXCEPTION_THROWN_NAME =
	// "sbbExceptionThrown";
	// public static final String _SBB_EXCEPTION_THROWN_SIGNATURE =
	// "(Ljava/lang/Exception;Ljava/lang/Object;Ljavax/slee/ActivityContextInterface;)V";

	// public static final String _SBB_ROLLERD_BACK_NAME = "sbbRolledBack";
	// public static final String _SBB_ROLLERD_BACK_SIGNATURE =
	// "(Ljavax/slee/RolledBackContext;)V";

	public static final String _SBB_AS_SBB_ACTIVITY_CONTEXT_INTERFACE = "asSbbActivityContextInterface";
	// This has to be terminated by
	// component.getSbbActivityContextInterface().getName+";" to form full
	// signature, however in 1.1 The return type must be the Activity Context
	// Interface interface of the SBB, or a base interface of
	// the Activity Context Interface interface of the SBB. <--- what does this
	// mean - we define ACI_X(extends ACI_Y) as aci of sbb, but we can return
	// ACI_Y ?
	// public static final String
	// _SBB_AS_SBB_ACTIVITY_CONTEXT_INTERFACE_SIGNATURE_PART =
	// "(Ljavax/slee/ActivityContextInterface;)L";

	public static final String _SBB_GET_CHILD_RELATION_SIGNATURE_PART = "[]interface javax.slee.ChildRelation";

	private SbbComponent component = null;
	private ComponentRepository repository = null;
	private final static transient Logger logger = Logger
			.getLogger(SbbComponentValidator.class);
	private final static Set<String> _PRIMITIVES;
	static {
		Set<String> tmp = new HashSet<String>();
		tmp.add("int");
		tmp.add("boolean");
		tmp.add("byte");
		tmp.add("char");
		tmp.add("double");
		tmp.add("float");
		tmp.add("long");
		tmp.add("short");
		_PRIMITIVES = Collections.unmodifiableSet(tmp);

	}

	public boolean validate() {

		return false;
	}

	public void setComponentRepository(ComponentRepository repository) {
		this.repository = repository;

	}

	public SbbComponent getComponent() {
		return component;
	}

	public void setComponent(SbbComponent component) {
		this.component = component;
	}

	/**
	 * Sbb abstract class(general rule � methods cannot start neither with �ejb�
	 * nor �sbb�)
	 * <ul>
	 * <li>(1.1 ?) must have package declaration
	 * <li>must implement in some way javax.slee.Sbb(only methods from interface
	 * can have �sbb� prefix)
	 * <ul>
	 * <li>each method defined must be implemented as public � not abstract,
	 * final or static
	 * </ul>
	 * <li>must be public and abstract
	 * <li>must have public no arg constructor
	 * <li>must implement sbbExceptionThrown method
	 * <ul>
	 * <li>
	 * public, not abstract, final or static no return type 3 arguments:
	 * java.lang.Exception, java.lang.Object,
	 * javax.slee.ActivityContextInterface
	 * </ul>
	 * <li>must implement sbbRolledBack
	 * <ul>
	 * <li>method must be public, not abstract, final or static
	 * <li>no return type
	 * <li>with single argument - javax.slee.RoledBackContext
	 * </ul>
	 * <li>there is no finalize method
	 * </ul>
	 * 
	 * @return
	 */
	boolean validateAbstractClassConstraints(
			Map<String, Method> concreteMethods,
			Map<String, Method> concreteSuperClassesMethods) {

		String errorBuffer = new String("");
		boolean passed = true;
		// Presence of those classes must be checked elsewhere
		Class sbbAbstractClass = this.component.getAbstractSbbClass();

		// Must be public and abstract
		int modifiers = sbbAbstractClass.getModifiers();
		// check that the class modifiers contain abstratc and public
		if (!Modifier.isAbstract(modifiers) || !Modifier.isPublic(modifiers)) {
			errorBuffer = appendToBuffer(this.component.getAbstractSbbClass()
					+ "sbb abstract class must be public and abstract", "6.1",
					errorBuffer);
		}

		// 1.1 - must be in package
		if (this.component.isSlee11()) {
			Package declaredPackage = sbbAbstractClass.getPackage();
			if (declaredPackage == null
					|| declaredPackage.getName().compareTo("") == 0) {
				passed = false;
				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ "sbb abstract class must be defined inside package space",
						"6.1", errorBuffer);
			}

		}

		// Public no arg constructor - can it have more ?
		// sbbAbstractClass.getConstructor
		// FIXME: no arg constructor has signature "()V" when added from
		// javaassist we check for such constructor and if its public
		try {
			Constructor constructor = sbbAbstractClass.getConstructor();
			int conMod = constructor.getModifiers();
			if (!Modifier.isPublic(conMod)) {
				passed = false;
				errorBuffer = appendToBuffer(this.component
						.getAbstractSbbClass()
						+ "sbb abstract class must have public constructor ",
						"6.1", errorBuffer);
			}
		} catch (SecurityException e) {

			e.printStackTrace();
			passed = false;
			errorBuffer = appendToBuffer(this.component.getAbstractSbbClass()
					+ "sbb abstract class must have no arg constructor, error:"
					+ e.getMessage(), "6.1", errorBuffer);
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
			passed = false;
			errorBuffer = appendToBuffer(this.component.getAbstractSbbClass()
					+ "sbb abstract class must have no arg constructor, error:"
					+ e.getMessage(), "6.1", errorBuffer);
		}

		// Must implements javax.slee.Sbb - and each method there defined, only
		// those methods and two above can have "sbb" prefix
		// those methods MUST be in concrete methods map, later we will use them
		// to see if there is ant "sbbXXXX" method

		// Check if we implement javax.slee.Sbb - either directly or from super
		// class
		Class javaxSleeSbbInterface = ClassUtils.checkInterfaces(
				sbbAbstractClass, "javax.slee.Sbb");
		;
		// sbbAbstractClass.getI
		if (javaxSleeSbbInterface == null) {

			passed = false;
			errorBuffer = appendToBuffer(
					this.component.getAbstractSbbClass()
							+ "sbb abstract class  must implement, directly or indirectly, the javax.slee.Sbb interface.",
					"6.1", errorBuffer);
		}

		// FIXME: add check for finalize method

		// Now we have to check methods from javax.slee.Sbb
		// This takes care of method throws clauses
		if (javaxSleeSbbInterface != null) {
			// if it is, we dont have those methods for sure, or maybe we do,
			// implemnted by hand
			// either way its a failure
			// We want only java.slee.Sbb methods :)
			Method[] sbbLifecycleMethods = javaxSleeSbbInterface
					.getDeclaredMethods();
			for (Method lifecycleMehtod : sbbLifecycleMethods) {
				// It must be implemented - so only in concrete methods, if we
				// are left with one not checked bang, its an error

				String methodKey = ClassUtils.getMethodKey(lifecycleMehtod);
				Method concreteLifeCycleImpl = null;
				if (concreteMethods.containsKey(methodKey)) {
					concreteLifeCycleImpl = concreteMethods.remove(methodKey);
				} else if (concreteSuperClassesMethods.containsKey(methodKey)) {
					concreteLifeCycleImpl = concreteSuperClassesMethods
							.remove(methodKey);
				} else {
					passed = false;
					errorBuffer = appendToBuffer(
							this.component.getAbstractSbbClass()
									+ "sbb abstract class must implement life cycle methods, it lacks concrete implementation of: "
									+ lifecycleMehtod.getName(), "6.1.1",
							errorBuffer);
					continue;
				}

				// now we now there is such method, its not private and abstract
				// If we are here its not null
				int lifeCycleModifier = concreteLifeCycleImpl.getModifiers();
				if (!Modifier.isPublic(lifeCycleModifier)
						|| Modifier.isStatic(lifeCycleModifier)
						|| Modifier.isFinal(lifeCycleModifier)) {
					passed = false;
					errorBuffer = appendToBuffer(
							this.component.getAbstractSbbClass()
									+ "sbb abstract class must implement life cycle methods, which can not be static, final or not public, method: "
									+ lifecycleMehtod.getName(), "6.1.1",
							errorBuffer);
				}

			}
		}

		// there can not be any method which start with ejb/sbb - we removed
		// every from concrete, lets iterate over those sets
		for (Method concreteMethod : concreteMethods.values()) {

			if (concreteMethod.getName().startsWith("ejb")
					|| concreteMethod.getName().startsWith("sbb")) {

				passed = false;
				errorBuffer = appendToBuffer(this.component
						.getAbstractSbbClass()
						+ " with method:  " + concreteMethod.getName(), "6.12",
						errorBuffer);

			}

			if (concreteMethod.getName().compareTo("finalize") == 0) {
				passed = false;
				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ "sbb abstract class  must not implement \"finalize\" method.",
						"6.1", errorBuffer);
			}
		}

		for (Method concreteMethod : concreteSuperClassesMethods.values()) {
			if (concreteMethod.getName().startsWith("ejb")
					|| concreteMethod.getName().startsWith("sbb")) {

				passed = false;
				errorBuffer = appendToBuffer(this.component
						.getAbstractSbbClass()
						+ " with method from super classes:  "
						+ concreteMethod.getName(), "6.12", errorBuffer);

			}

			if (concreteMethod.getName().compareTo("finalize") == 0) {
				passed = false;
				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ "sbb abstract class  must not implement \"finalize\" method. Its implemented by super class.",
						"6.1", errorBuffer);
			}

		}

		if (!passed) {
			logger.error(errorBuffer.toString());
			System.err.println(errorBuffer);
		}

		return passed;

	}

	/**
	 * This method checks for presence of
	 * 
	 * @param sbbAbstractClassAbstraMethod
	 * @param sbbAbstractClassAbstraMethodFromSuperClasses
	 * @return
	 */
	boolean validateSbbActivityContextInterface(
			Map<String, Method> sbbAbstractClassAbstraMethod,
			Map<String, Method> sbbAbstractClassAbstraMethodFromSuperClasses) {

		if (this.component.getDescriptor().getSbbActivityContextInterface() == null) {
			// FIXME: add check for asSbbActivityContextInteface method ? This
			// will be catched at the end of check anyway
			if (logger.isInfoEnabled()) {
				logger.info(this.component.getDescriptor().getSbbComponentKey()
						+ " : No Sbb activity context interface defined");
			}
			return true;
		}

		String errorBuffer = new String("");
		boolean passed = true;

		Class sbbAbstractClass = this.component.getAbstractSbbClass();
		Method asACIMethod = null;

		// lets go through methods of sbbAbstract class,

		for (Method someMethod : sbbAbstractClassAbstraMethod.values()) {

			if (someMethod.getName().compareTo(
					_SBB_AS_SBB_ACTIVITY_CONTEXT_INTERFACE) == 0) {
				// we have a winner, possibly - we have to check parameter
				// list, cause someone can create abstract method(or crap,
				// it can be concrete) with different parametrs, in case its
				// abstract, it will fail later on

				if (someMethod.getParameterTypes().length == 1
						&& someMethod.getParameterTypes()[0].getName()
								.compareTo(
										"javax.slee.ActivityContextInterface") == 0) {
					asACIMethod = someMethod;
					break;
				}
			}
		}

		if (asACIMethod == null)
			for (Method someMethod : sbbAbstractClassAbstraMethodFromSuperClasses
					.values()) {

				if (someMethod.getName().compareTo(
						_SBB_AS_SBB_ACTIVITY_CONTEXT_INTERFACE) == 0) {
					// we have a winner, possibly - we have to check
					// parameter
					// list, cause someone can create abstract method(or
					// crap,
					// it can be concrete) with different parametrs, in case
					// its
					// abstract, it will fail later on
					if (someMethod.getParameterTypes().length == 1
							&& someMethod.getParameterTypes()[0]
									.getName()
									.compareTo(
											"javax/slee/ActivityContextInterface") == 0) {
						asACIMethod = someMethod;
						break;
					}
				}
			}
		if (asACIMethod == null) {
			passed = false;
			errorBuffer = appendToBuffer(
					this.component.getAbstractSbbClass()
							+ " must imlement narrow method asSbbActivityContextInterface",
					"7.7.2", errorBuffer);

		} else {
			// must be public, abstract? FIXME: not native?
			int asACIMethodModifiers = asACIMethod.getModifiers();
			if (!Modifier.isPublic(asACIMethodModifiers)
					|| !Modifier.isAbstract(asACIMethodModifiers)
					|| Modifier.isNative(asACIMethodModifiers)) {
				passed = false;
				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ " narrow method asSbbActivityContextInterface must be public,abstract and not native.",
						"7.7.2", errorBuffer);
			}

			// now this misery comes to play, return type check
			Class returnType = asACIMethod.getReturnType();
			// Must return something from Sbb defined aci class inheritance
			// tree
			Class definedReturnType = this.component
					.getActivityContextInterface();

			if (returnType.getName().compareTo("void") == 0) {
				passed = false;
				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ " narrow method asSbbActivityContextInterface must be public,abstract and not native.",
						"7.7.2", errorBuffer);
			} else if (returnType.equals(definedReturnType)) {
				// its ok

			} else if (ClassUtils.checkInterfaces(definedReturnType, returnType
					.getName()) != null) {

			} else {
				passed = false;
				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ " narrow method asSbbActivityContextInterface has wrong return type: "
								+ returnType, "7.7.2", errorBuffer);

			}

			// no throws clause
			if (asACIMethod.getExceptionTypes() != null
					&& asACIMethod.getExceptionTypes().length > 0) {

				passed = false;
				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ " narrow method asSbbActivityContextInterface must not have throws clause.",
						"7.7.2", errorBuffer);
			}

		}

		// Even if we fail above we can do some checks on ACI if its present.
		// this has to be present
		Class sbbActivityContextInterface = this.component
				.getActivityContextInterface();

		// ACI VALIDATION
		// (1.1) = must be declared in package

		if (this.component.isSlee11()
				&& sbbActivityContextInterface.getPackage() == null) {
			passed = false;
			errorBuffer = appendToBuffer(
					this.component.getAbstractSbbClass()
							+ " sbb activity context interface must be declared in package.",
					"7.5", errorBuffer);
		}

		if (!Modifier.isPublic(sbbActivityContextInterface.getModifiers())) {
			passed = false;
			errorBuffer = appendToBuffer(
					this.component.getAbstractSbbClass()
							+ " sbb activity context interface must be declared as public.",
					"7.5", errorBuffer);
		}

		// We can have here ACI objects and java primitives, ugh, both methods
		// dont have to be shown
		passed = checkSbbAciFieldsConstraints(this.component
				.getActivityContextInterface());

		// finally lets remove asSbb method form abstract lists, this is used
		// later to determine methods that didnt match any sbb definition
		if (asACIMethod != null) {
			sbbAbstractClassAbstraMethod.remove(ClassUtils
					.getMethodKey(asACIMethod));
			sbbAbstractClassAbstraMethodFromSuperClasses.remove(ClassUtils
					.getMethodKey(asACIMethod));
		}

		if (!passed) {
			logger.error(errorBuffer.toString());
			System.err.println(errorBuffer);
		}

		return passed;
	}

	/**
	 * This method validates all methods in ACI interface:
	 * <ul>
	 * <li>set/get methods and parameter names as in CMP fields decalration
	 * <li>methods must
	 * <ul>
	 * <li>be public, abstract
	 * <li>setters must have one param
	 * <li>getters return type must match setter type
	 * <li>allowe types are: primitives and serilizable types
	 * </ul>
	 * </ul>
	 * <br>
	 * Sbb descriptor provides method to obtain aci field names, if this test
	 * passes it means that all fields there should be correct and can be used
	 * to verify aliases
	 * 
	 * @param sbbAciInterface
	 * @return
	 */
	boolean checkSbbAciFieldsConstraints(Class sbbAciInterface) {

		boolean passed = true;
		String errorBuffer = new String("");
		// here we need all fields :)
		HashSet<String> ignore = new HashSet<String>();
		ignore.add("javax.slee.ActivityContextInterface");
		// FIXME: we could go other way, run this for each super interface we
		// have???
		Map<String, Method> aciInterfacesDefinedMethods = ClassUtils
				.getAllInterfacesMethods(sbbAciInterface, ignore);

		// Here we will store fields name-type - if there is getter and setter,
		// type must match!!!
		Map<String, Class> localNameToType = new HashMap<String, Class>();

		for (String methodKey : aciInterfacesDefinedMethods.keySet()) {

			Method fieldMethod = aciInterfacesDefinedMethods.get(methodKey);
			String methodName = fieldMethod.getName();
			if (!(methodName.startsWith("get") || methodName.startsWith("set"))) {
				passed = false;
				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ " sbb activity context interface can have only getter/setter  methods.",
						"7.5.1", errorBuffer);
				continue;
			}

			// let us get field name:

			String fieldName = methodName.replaceFirst("set", "").replaceFirst(
					"get", "");

			if (!Character.isUpperCase(fieldName.charAt(0))) {
				passed = false;
				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ " sbb activity context interface can have only getter/setter  methods - 4th char in those methods must be capital.",
						"7.5.1", errorBuffer);

			}

			// check throws clause.
			// number of parameters

			if (fieldMethod.getExceptionTypes().length > 0) {
				passed = false;
				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ " sbb activity context interface getter method must have empty throws clause: "
								+ fieldMethod.getName(), "7.5.1", errorBuffer);

			}

			boolean isGetter = methodName.startsWith("get");
			Class fieldType = null;
			if (isGetter) {
				// no params
				if (fieldMethod.getParameterTypes() != null
						&& fieldMethod.getParameterTypes().length > 0) {
					passed = false;
					errorBuffer = appendToBuffer(
							this.component.getAbstractSbbClass()
									+ " sbb activity context interface getter method must not have parameters: "
									+ fieldMethod.getName(), "7.5.1",
							errorBuffer);

				}

				fieldType = fieldMethod.getReturnType();
				if (fieldType.getName().compareTo("void") == 0) {
					passed = false;
					errorBuffer = appendToBuffer(
							this.component.getAbstractSbbClass()
									+ " sbb activity context interface getter method must have return type: "
									+ fieldMethod.getName(), "7.5.1",
							errorBuffer);

				}

			} else {
				if (fieldMethod.getParameterTypes() != null
						&& fieldMethod.getParameterTypes().length != 1) {
					passed = false;
					errorBuffer = appendToBuffer(
							this.component.getAbstractSbbClass()
									+ " sbb activity context interface setter method must single parameter: "
									+ fieldMethod.getName(), "7.5.1",
							errorBuffer);

					// Here we quick fail
					continue;
				}

				fieldType = fieldMethod.getParameterTypes()[0];
				if (fieldMethod.getReturnType().getName().compareTo("void") != 0) {
					passed = false;
					errorBuffer = appendToBuffer(
							this.component.getAbstractSbbClass()
									+ " sbb activity context interface setter method must not have return type: "
									+ fieldMethod.getName(), "7.5.1",
							errorBuffer);

				}

			}

			// Field type can be primitive and serialzable
			if (!(_PRIMITIVES.contains(fieldType.getName()) || ClassUtils
					.checkInterfaces(fieldType, "java.io.Serializable") != null)) {
				passed = false;
				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ " sbb activity context interface field("
								+ fieldName
								+ ") has wrong type, only primitives and serializable: "
								+ fieldType, "7.5.1", errorBuffer);
				// we fail here
				continue;
			}

			if (localNameToType.containsKey(fieldName)) {
				Class storedType = localNameToType.get(fieldName);
				if (!storedType.equals(fieldType)) {
					passed = false;

					errorBuffer = appendToBuffer(
							this.component.getAbstractSbbClass()
									+ " sbb activity context interface has wrong definition of parameter - setter and getter types do not match: "
									+ fieldName + ", type1: "
									+ fieldType.getName() + " typ2:"
									+ storedType.getName(), "7.5.1",
							errorBuffer);

					// we fail here
					continue;
				}
			} else {
				// simply store
				localNameToType.put(fieldName, fieldType);
			}

		}

		// FIXME: add check against components get aci fields ?

		if (!passed) {
			logger.error(errorBuffer.toString());
			System.err.println(errorBuffer);
		}

		return passed;
	}

	boolean validateGetChildRelationMethods(
			Map<String, Method> sbbAbstractClassAbstractMethod,
			Map<String, Method> sbbAbstractClassAbstractMethodFromSuperClasses) {

		boolean passed = true;
		String errorBuffer = new String("");

		// FIXME: its cant be out of scope, since its byte....
		// we look for method key
		for (MGetChildRelationMethod mMetod : this.component.getDescriptor()
				.getSbbAbstractClass().getChildRelationMethods()) {
			if (mMetod.getDefaultPriority() > 127
					|| mMetod.getDefaultPriority() < -128) {
				passed = false;

				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ "Defined  get child relation method priority for method: "
								+ mMetod.getChildRelationMethodName()
								+ " is out of scope!!", "6.8", errorBuffer);

			}

			// This is key == <<methodName>>()Ljavax/slee/ChildRelation
			// We it makes sure that method name, parameters, and return type is
			// ok.
			String methodKey = mMetod.getChildRelationMethodName()
					+ _SBB_GET_CHILD_RELATION_SIGNATURE_PART;

			Method childRelationMethod = null;
			childRelationMethod = sbbAbstractClassAbstractMethod.get(methodKey);

			if (childRelationMethod == null) {
				childRelationMethod = sbbAbstractClassAbstractMethodFromSuperClasses
						.get(methodKey);

			}

			if (childRelationMethod == null) {
				passed = false;

				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ "Defined  get child rekatuib method: "
								+ mMetod.getChildRelationMethodName()
								+ " is not matched by any abstract method, either its not abstract, is private, has parameter or has wrong return type(should be javax.slee.ChildRelation)!!",
						"6.8", errorBuffer);
				System.err.println("Method Key: "
						+ methodKey
						+ " KEYS:"
						+ Arrays.toString(sbbAbstractClassAbstractMethod
								.keySet().toArray()));
				// we fail fast here
				continue;
			}

			// if we are here we have to check throws clause, prefix - it cant
			// be ejb or sbb

			if (childRelationMethod.getExceptionTypes().length > 0) {
				passed = false;

				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ "Defined  get child relation method priority for method: "
								+ mMetod.getChildRelationMethodName()
								+ " must hot have throws clause", "6.8",
						errorBuffer);
			}

			if (childRelationMethod.getName().startsWith("ejb")
					|| childRelationMethod.getName().startsWith("sbb")) {
				// this is checked for concrete methods only
				passed = false;

				errorBuffer = appendToBuffer(
						this.component.getAbstractSbbClass()
								+ "Defined  get child relation method priority for method: "
								+ mMetod.getChildRelationMethodName()
								+ " has wrong prefix, it can not start with \"ejb\" or \"sbb\".!",
						"6.8", errorBuffer);

			}

			// remove, we will later determine methods that were not implemented
			// by this
			if (childRelationMethod != null) {
				sbbAbstractClassAbstractMethod.remove(methodKey);
				sbbAbstractClassAbstractMethodFromSuperClasses
						.remove(methodKey);
			}

		}

		if (!passed) {
			logger.error(errorBuffer.toString());
			System.err.println(errorBuffer);
		}

		return passed;

	}

	boolean validateSbbLocalInterface(
			Map<String, Method> sbbAbstractClassConcreteMethods,
			Map<String, Method> sbbAbstractClassConcreteFromSuperClasses) {

		boolean passed = true;
		String errorBuffer = new String("");
		if (this.component.getDescriptor().getSbbLocalInterface() == null)
			return passed;

		Class sbbLocalInterfaceClass = this.component
				.getSbbLocalInterfaceClass();
		Class genericSbbLocalInterface = ClassUtils.checkInterfaces(
				sbbLocalInterfaceClass, "javax.slee.SbbLocalObject");

		if (genericSbbLocalInterface == null) {
			passed = false;

			errorBuffer = appendToBuffer(
					this.component.getAbstractSbbClass()
							+ "DSbbLocalInterface: "
							+ sbbLocalInterfaceClass.getName()
							+ " does not implement javax.slee.SbbLocalInterface super interface in any way!!!",
					"6.5", errorBuffer);

		}

		int sbbLocalInterfaceClassModifiers = sbbLocalInterfaceClass
				.getModifiers();
		if (this.component.isSlee11()
				&& sbbLocalInterfaceClass.getPackage() == null) {
			passed = false;
			errorBuffer = appendToBuffer(this.component.getAbstractSbbClass()
					+ "SbbLocalInterface: " + sbbLocalInterfaceClass.getName()
					+ " is nto defined in package", "6.5", errorBuffer);
		}

		if (!Modifier.isPublic(sbbLocalInterfaceClassModifiers)) {
			passed = false;
			errorBuffer = appendToBuffer(this.component.getAbstractSbbClass()
					+ "SbbLocalInterface: " + sbbLocalInterfaceClass.getName()
					+ " must be public!", "6.5", errorBuffer);
		}

		Set<String> ignore = new HashSet<String>();
		ignore.add("javax.slee.SbbLocalObject");
		ignore.add("java.lang.Object");
		Map<String, Method> interfaceMethods = ClassUtils
				.getAllInterfacesMethods(sbbLocalInterfaceClass, ignore);

		// here we have all defined methods in interface, we have to checkif
		// their names do not start with sbb/ejb and if they are contained in
		// collections with concrete methods from sbb

		for (Method methodToCheck : interfaceMethods.values()) {
			if (methodToCheck.getName().startsWith("ejb")
					|| methodToCheck.getName().startsWith("sbb")) {
				passed = false;
				errorBuffer = appendToBuffer("Method from SbbLocalInterface: "
						+ sbbLocalInterfaceClass.getName()
						+ " starts with wrong prefix: "
						+ methodToCheck.getName(), "6.5", errorBuffer);

			}

			Method methodFromSbbClass = sbbAbstractClassConcreteMethods
					.get(ClassUtils.getMethodKey(methodToCheck));
			if (methodFromSbbClass == null) {
				methodFromSbbClass = sbbAbstractClassConcreteFromSuperClasses
						.get(ClassUtils.getMethodKey(methodToCheck));
			}

			if (methodFromSbbClass == null) {

				passed = false;
				errorBuffer = appendToBuffer(
						"Method from SbbLocalInterface: "
								+ sbbLocalInterfaceClass.getName()
								+ "with name:  "
								+ methodToCheck.getName()
								+ " is not implemented by sbb class or its super classes!",
						"6.5", errorBuffer);

				// we fails fast here
				continue;
			}

			// XXX: Note this does not check throws clause, only name and
			// signature
			// this side
			// FIXME: Note that we dont check modifier, is this corerct

			if (!(methodFromSbbClass.getName().compareTo(
					methodToCheck.getName()) == 0)
					|| !methodFromSbbClass.getReturnType().equals(
							methodToCheck.getReturnType())
				    || !Arrays.equals(methodFromSbbClass.getParameterTypes(),methodToCheck.getParameterTypes())
					|| !Arrays.equals((Object[]) methodFromSbbClass
							.getExceptionTypes(), (Object[]) methodToCheck
							.getExceptionTypes())) {

				passed = false;
				errorBuffer = appendToBuffer(
						"Method from SbbLocalInterface: "
								+ sbbLocalInterfaceClass.getName()
								+ " with name:  "
								+ methodToCheck.getName()
								+ " is not implemented by sbb class or its super classes. Its visibility, throws clause is different or modifiers are different!",
						"6.5", errorBuffer);

				// we fails fast here
				continue;

			}

		}

		// FIXME: is this ok, is it needed ? If not checked here, abstract
		// methods check will make it fail later, but not concrete ?
		// now lets check javax.slee.SbbLocalObject methods - sbb cant have
		// those implemented or defined as abstract.

		if (!passed) {
			logger.error(errorBuffer.toString());
			System.err.println(errorBuffer);
		}

		return passed;
	}

	protected String appendToBuffer(String message, String section,
			String buffer) {
		buffer += (this.component.getDescriptor().getSbbComponentKey()
				+ " : violates section " + section
				+ " of jSLEE 1.1 specification : " + message + "\n");
		return buffer;
	}

}