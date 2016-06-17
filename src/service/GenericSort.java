package service;

import java.lang.reflect.Field;
import java.text.Normalizer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class GenericSort {

	/**
	 * This method is used to sort the elements based on the fieldName
	 * specified. Sorting order is Ascending order.
	 * 
	 * @param resultList
	 *            collection to be sorted
	 * @param fieldName
	 *            resultList will be sorted according to this fieldName.
	 * @throws Exception
	 */
	public static <Type> void sortElements(List<Type> resultList, final String fieldName, final boolean isDesc)
			throws Exception {
		Collections.sort(resultList, new Comparator<Type>() {
			@Override
			public int compare(Type o1, Type o2) {
				return compareValue(o1, o2);
			}

			private int compareValue(Type o1, Type o2) {
				int returnValue = 0;
				try {
					Field field = o1.getClass().getDeclaredField(fieldName);
					boolean accessible = field.isAccessible();
					field.setAccessible(true);
					Object objectO1 = field.get(o1);
					Object objectO2 = field.get(o2);
					// if sorted Object type if Number, it will be treated as number
					if (objectO1 instanceof Number) {
						if ((objectO1 != null && objectO2 != null) && (objectO1 instanceof Integer
								|| objectO1 instanceof Long || objectO1 instanceof Byte)) {
							returnValue = Long.valueOf(objectO1 + "").compareTo(Long.valueOf(objectO2 + ""));
						} else if ((objectO1 != null && objectO2 != null)
								&& (objectO1 instanceof Double || objectO1 instanceof Float)) {
							returnValue = Double.valueOf(objectO1 + "").compareTo(Double.valueOf(objectO2 + ""));
						}
						// if sorted Object type if String or Character, it will be treated suitable
					} else if (objectO1 instanceof String || objectO1 instanceof Character) {
						if ((objectO1 != null) && objectO2 != null) {
							returnValue = normalizedString(String.valueOf(objectO1))
									.compareToIgnoreCase(normalizedString(String.valueOf(objectO2)));
						}
					}
					field.setAccessible(accessible);
				} catch (Exception e) {
					System.out.println("Error occured while sorting elements");
				}

				// chose whether sort will be ascending or descending
				// ascending is chosed by default
				if (isDesc) {
					if (returnValue > 0) {
						return -1;
					} else if (returnValue < 0) {
						return 1;
					}
				}
				return returnValue;
			
			}
		});

	}

	/**
	 * This methods Normalizes the input string. Here we remove accent from the
	 * character so that we can sort the string properly using normal
	 * characters.
	 */
	private static String normalizedString(String str) {
		if (!isNullOrBlank(str)) {
			String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
			Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
			return pattern.matcher(nfdNormalizedString).replaceAll("");
		} else {
			return "";
		}
	}

	/**
	 * This function checks that the value is blank or null.
	 * 
	 * @param value
	 *            value to be checked
	 * @return true if value is blank or null
	 */
	private static boolean isNullOrBlank(String value) {
		boolean retFlag = false;
		if (value == null || value.trim().equals("") || value.trim().equals("null")) {
			retFlag = true;
		}
		return retFlag;
	}

}
