/***************************************************************************\
* Copyright 2017 [Lyonlancer5]                                              *
*                                                                           *
* Licensed under the Apache License, Version 2.0 (the "License");           *
* you may not use this file except in compliance with the License.          *
* You may obtain a copy of the License at                                   *
*                                                                           *
*     http://www.apache.org/licenses/LICENSE-2.0                            *
*                                                                           *
* Unless required by applicable law or agreed to in writing, software       *
* distributed under the License is distributed on an "AS IS" BASIS,         *
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
* See the License for the specific language governing permissions and       *
* limitations under the License.                                            *
\***************************************************************************/
package net.lyonlancer5.mcmp.uuem.modes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

	private static Field fField_modifiers;

	/**
	 * Adds the modifiers {@code mod} to the given {@link Field} {@code field}
	 * if {@code flag} is true; removing them otherwise.
	 * 
	 * @param field
	 *            The field object
	 * @param mod
	 *            The modifiers
	 * @param flag
	 *            Flag to add or remove said modifiers
	 */
	public static Field setModifier(Field field, int mod, boolean flag) {
		if (fField_modifiers == null) {
			try {
				fField_modifiers = Field.class.getDeclaredField("modifiers");
				fField_modifiers.setAccessible(true);
			} catch (NoSuchFieldException | SecurityException e) {
				throw new RuntimeException("Field modifier reflection error", e);
			}
		}

		try {
			field.setAccessible(true);
			int modifiers = fField_modifiers.getInt(field);
			if (flag)
				modifiers |= mod;
			else
				modifiers &= ~mod;

			fField_modifiers.setInt(field, modifiers);
			return field;
		} catch (Exception ex) {
			throw new RuntimeException("Could not set modifiers for field " + field.getName(), ex);
		}
	}

	/**
	 * Finds a method from the given names (useful for deobf/srg/obf method
	 * discernment)
	 * 
	 * @param classIn
	 *            The class to look into
	 * @param methodNames
	 *            The method names (deobf/srg/obf)
	 * @param methodTypes
	 *            The parameters of the method
	 * 
	 * @return The specified method being looked for
	 * @throws ReflectiveOperationException
	 */
	public static Method findMethod(Class<?> classIn, String[] methodNames, Class<?>... methodTypes)
			throws ReflectiveOperationException {
		Exception var0 = null;

		for (String methodName : methodNames) {
			try {
				Method m = classIn.getDeclaredMethod(methodName, methodTypes);
				m.setAccessible(true);
				return m;
			} catch (Exception e) {
				var0 = e;
			}
		}

		throw new ReflectiveOperationException("Method reflection error", var0);
	}

	/**
	 * Finds a field from the given names (useful for deobf/srg/obf method
	 * discernment)
	 * 
	 * @param classIn
	 *            The class to look into
	 * @param fieldNames
	 *            The method names (deobf/srg/obf)
	 * 
	 * @return The specified field being looked for
	 * @throws ReflectiveOperationException
	 */
	public static Field findField(Class<?> classIn, String... fieldNames) throws ReflectiveOperationException {
		Exception var0 = null;

		if (fieldNames == null || fieldNames.length == 0) {
			var0 = new NullPointerException("No provided field names to reflect!");
		}

		for (String fieldName : fieldNames) {
			try {
				Field f = classIn.getDeclaredField(fieldName);
				f.setAccessible(true);
				return f;
			} catch (Exception e) {
				var0 = e;
			}
		}

		throw new ReflectiveOperationException("Field reflection error", var0);
	}

	/**
	 * Gets the caller of the specified method in a thread.
	 * 
	 * @return The caller's {@link StackTraceElement}
	 */
	public static StackTraceElement getCaller() {
		final StackTraceElement[] stElements = Thread.currentThread().getStackTrace();

		String callerClassName = null;

		for (int i = 1; i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			String className = ste.getClassName();
			if (!ReflectionUtils.class.getName().equals(className) && !className.startsWith("java.lang.Thread")) {
				if (callerClassName == null)
					callerClassName = className;
				else if (!callerClassName.equals(className))
					return ste;

			}

		}
		return null;
	}

}
