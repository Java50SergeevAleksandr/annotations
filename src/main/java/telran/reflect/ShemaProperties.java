package telran.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ShemaProperties {
	public static void displayFieldProperties(Object obj) throws Exception {
		Field[] fields = obj.getClass().getDeclaredFields();
		ArrayList<Field> idArr = new ArrayList<Field>(0);
		for (Field field : fields) {
			if (field.isAnnotationPresent(Index.class)) {
				System.out.println("annotation @Index at field: " + field.getName());
			}
			if (field.isAnnotationPresent(Id.class)) {
				idArr.add(field);
			}
		}

		if (idArr.isEmpty()) {
			throw new IllegalStateException("No field Id found");
		}
		if (idArr.size() > 1) {
			throw new IllegalStateException("Field Id must be one");
		}
		System.out.println("annotation @Id at field: " + idArr.get(0).getName());
	}
}
