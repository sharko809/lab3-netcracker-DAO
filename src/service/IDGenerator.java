package service;

/**
 * 
 * @author Neltarion
 *         <p>
 *         This class generates ID's for different entities so they can be
 *         stored in strict formal order.
 *         </p>
 *         Each storage type has it's own global variables. I don't like all
 *         this "global" stuff but it's the only idea I came up to so fast.
 *         </p>
 */
public class IDGenerator {

	// TODO don't return nulls (work with data)
	// TODO write all necessary IFs
	// TODO copies while adding entities

	private static int globalCustomerId = 0;
	private static int globalEmploeeId = 0;
	private static int globalProjectId = 0;
	private static int globalXmlCustomerId = 0;
	private static int globalXmlEmploeeId = 0;
	private static int globalXmlProjectId = 0;
	private static int globalTextCustomerId = 0;
	private static int globalTextEmploeeId = 0;
	private static int globalTextProjectId = 0;

	public static int getGlobalTextCustomerId() {
		return globalTextCustomerId;
	}

	public static void setGlobalTextCustomerId(int globalTextCustomerId) {
		IDGenerator.globalTextCustomerId = globalTextCustomerId;
	}

	public static int getGlobalTextEmploeeId() {
		return globalTextEmploeeId;
	}

	public static void setGlobalTextEmploeeId(int globalTextEmploeeId) {
		IDGenerator.globalTextEmploeeId = globalTextEmploeeId;
	}

	public static int getGlobalTextProjectId() {
		return globalTextProjectId;
	}

	public static void setGlobalTextProjectId(int globalTextProjectId) {
		IDGenerator.globalTextProjectId = globalTextProjectId;
	}

	public IDGenerator() {
	}

	public enum IdType {
		EMPLOYEE, PROJECT, CUSTOMER
	}

	public static int generateCollectionId(IdType idType) {
		switch (idType) {
		case EMPLOYEE:
			if (getGlobalEmploeeId() >= 0) {
				setGlobalEmploeeId(getGlobalEmploeeId() + 1);
				return getGlobalEmploeeId();
			} else {
				System.out.println("something's wrong");
				return 0;
			}
		case PROJECT:
			if (getGlobalProjectId() >= 0) {
				setGlobalProjectId(getGlobalProjectId() + 1);
				return getGlobalProjectId();
			} else {
				System.out.println("something's wrong");
				return 0;
			}
		case CUSTOMER:
			if (getGlobalCustomerId() >= 0) {
				setGlobalCustomerId(getGlobalCustomerId() + 1);
				return getGlobalCustomerId();
			} else {
				System.out.println("something's wrong");
				return 0;
			}
		default:
			return 0;
		}
	}

	public static int generateXmlId(IdType idType) {
		switch (idType) {
		case EMPLOYEE:
			if (getGlobalXmlEmploeeId() >= 0) {
				setGlobalXmlEmploeeId(getGlobalXmlEmploeeId() + 1);
				return getGlobalXmlEmploeeId();
			} else {
				System.out.println("something's wrong");
				return 0;
			}
		case PROJECT:
			if (getGlobalXmlProjectId() >= 0) {
				setGlobalXmlProjectId(getGlobalXmlProjectId() + 1);
				return getGlobalXmlProjectId();
			} else {
				System.out.println("something's wrong");
				return 0;
			}
		case CUSTOMER:
			if (getGlobalXmlCustomerId() >= 0) {
				setGlobalXmlCustomerId(getGlobalXmlCustomerId() + 1);
				return getGlobalXmlCustomerId();
			} else {
				System.out.println("something's wrong");
				return 0;
			}
		default:
			return 0;
		}
	}

	public static int generateTextId(IdType idType) {
		switch (idType) {
		case EMPLOYEE:
			if (getGlobalTextEmploeeId() >= 0) {
				setGlobalTextEmploeeId(getGlobalTextEmploeeId() + 1);
				return getGlobalTextEmploeeId();
			} else {
				System.out.println("something's wrong");
				return 0;
			}
		case PROJECT:
			if (getGlobalTextProjectId() >= 0) {
				setGlobalTextProjectId(getGlobalTextProjectId() + 1);
				return getGlobalTextProjectId();
			} else {
				System.out.println("something's wrong");
				return 0;
			}
		case CUSTOMER:
			if (getGlobalTextCustomerId() >= 0) {
				setGlobalTextCustomerId(getGlobalXmlCustomerId() + 1);
				return getGlobalTextCustomerId();
			} else {
				System.out.println("something's wrong");
				return 0;
			}
		default:
			return 0;
		}
	}

	public static int getGlobalXmlCustomerId() {
		return globalXmlCustomerId;
	}

	public static void setGlobalXmlCustomerId(int globalXmlCustomerId) {
		IDGenerator.globalXmlCustomerId = globalXmlCustomerId;
	}

	public static int getGlobalXmlEmploeeId() {
		return globalXmlEmploeeId;
	}

	public static void setGlobalXmlEmploeeId(int globalXmlEmploeeId) {
		IDGenerator.globalXmlEmploeeId = globalXmlEmploeeId;
	}

	public static int getGlobalXmlProjectId() {
		return globalXmlProjectId;
	}

	public static void setGlobalXmlProjectId(int globalXmlProjectId) {
		IDGenerator.globalXmlProjectId = globalXmlProjectId;
	}

	public static int getGlobalCustomerId() {
		return globalCustomerId;
	}

	public static void setGlobalCustomerId(int globalCustomerId) {
		IDGenerator.globalCustomerId = globalCustomerId;
	}

	public static int getGlobalEmploeeId() {
		return globalEmploeeId;
	}

	public static void setGlobalEmploeeId(int globalEmploeeId) {
		IDGenerator.globalEmploeeId = globalEmploeeId;
	}

	public static int getGlobalProjectId() {
		return globalProjectId;
	}

	public static void setGlobalProjectId(int globalProjectId) {
		IDGenerator.globalProjectId = globalProjectId;
	}

}
