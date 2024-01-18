package telran.test;

import java.lang.reflect.Method;
import java.util.ArrayList;

import telran.reflect.BeforeEach;
import telran.reflect.Test;

public class TestLibrary {
	public static void launchTest(Object testObj) throws Exception {
		Method[] methods = testObj.getClass().getDeclaredMethods();
		ArrayList<Method> beforeMethods = new ArrayList<Method>();
		ArrayList<Method> testedMethods = new ArrayList<Method>();
		for (Method method : methods) {
			if (method.isAnnotationPresent(BeforeEach.class)) {
				method.setAccessible(true);
				beforeMethods.add(method);
			}
			if (method.isAnnotationPresent(Test.class)) {
				method.setAccessible(true);
				testedMethods.add(method);
			}
		}
		
		for (Method method : testedMethods) {
			for (Method before : beforeMethods) {
				before.invoke(testObj);
			}
			method.invoke(testObj);
		}
	}
}