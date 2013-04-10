package com.android.ide.common.resources;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.android.ide.common.rendering.api.AdapterBinding;
import com.android.ide.common.rendering.api.ILayoutPullParser;
import com.android.ide.common.rendering.api.IProjectCallback;
import com.android.ide.common.rendering.api.ResourceReference;
import com.android.ide.common.rendering.api.ResourceValue;
import com.android.layoutlib.bridge.util.DynamicIdMap;
import com.android.resources.ResourceType;
import com.android.util.Pair;

public class ProjectResources extends FrameworkResources implements
		IProjectCallback {
	
	/**
	 * Maps from id to resource type/name. This is for com.android.internal.R
	 */
	public final static Map<Integer, Pair<ResourceType, String>> sRMap = new HashMap<Integer, Pair<ResourceType, String>>();

	/**
	 * Same as sRMap except for int[] instead of int resources. This is for
	 * android.R only.
	 */
	private final static Map<IntArray, String> sRArrayMap = new HashMap<IntArray, String>();
    /**
     * Reverse map compared to sRMap, resource type -> (resource name -> id).
     * This is for com.android.internal.R.
     */
    private final static Map<ResourceType, Map<String, Integer>> sRevRMap =
        new EnumMap<ResourceType, Map<String,Integer>>(ResourceType.class);
    private final static int DYNAMIC_ID_SEED_START = 0x01ff0000;
    private final static DynamicIdMap sDynamicIds = new DynamicIdMap(DYNAMIC_ID_SEED_START);

	public ProjectResources(Class R) {
		try {
			Class<?> r = R;

			for (Class<?> inner : r.getDeclaredClasses()) {
				String resTypeName = inner.getSimpleName();
				ResourceType resType = ResourceType.getEnum(resTypeName);
				if (resType != null) {
					Map<String, Integer> fullMap = new HashMap<String, Integer>();
					sRevRMap.put(resType, fullMap);

					for (Field f : inner.getDeclaredFields()) {
						// only process static final fields. Since the final
						// attribute may have
						// been altered by layoutlib_create, we only check
						// static
						int modifiers = f.getModifiers();
						if (Modifier.isStatic(modifiers)) {
							Class<?> type = f.getType();
							if (type.isArray()
									&& type.getComponentType() == int.class) {
								// if the object is an int[] we put it in
								// sRArrayMap using an IntArray
								// wrapper that properly implements equals and
								// hashcode for the array
								// objects, as required by the map contract.
								sRArrayMap.put(
										new IntArray((int[]) f.get(null)),
										f.getName());
							} else if (type == int.class) {
								Integer value = (Integer) f.get(null);
								sRMap.put(value, Pair.of(resType, f.getName()));
								fullMap.put(f.getName(), value);
							} else {
								assert false;
							}
						}
					}
				}
			}
		} catch (Throwable throwable) {
		}
	}

	@Override
	public Object loadView(String name, Class[] constructorSignature,
			Object[] constructorArgs) throws ClassNotFoundException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<ResourceType, String> resolveResourceId(int value) {
		Pair<ResourceType, String> pair = sRMap.get(value);
        if (pair == null) {
            pair = sDynamicIds.resolveId(value);
            if (pair == null) {
                //System.out.println(String.format("Missing id: %1$08X (%1$d)", value));
            }
        }
        return pair;
	}

	@Override
	public String resolveResourceId(int[] id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getResourceId(ResourceType type, String name) {
	       Map<String, Integer> map = sRevRMap.get(type);
	        Integer value = null;
	        if (map != null) {
	            value = map.get(name);
	        }

	        if (value == null) {
	            value = sDynamicIds.getId(type, name);
	        }
	        return value;
	}

	@Override
	@Deprecated
	public ILayoutPullParser getParser(String layoutName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILayoutPullParser getParser(ResourceValue layoutResource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAdapterItemValue(ResourceReference adapterView,
			Object adapterCookie, ResourceReference itemRef, int fullPosition,
			int positionPerType, int fullParentPosition,
			int parentPositionPerType, ResourceReference viewRef,
			ViewAttribute viewAttribute, Object defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdapterBinding getAdapterBinding(ResourceReference adapterViewRef,
			Object adapterCookie, Object viewObject) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * int[] wrapper to use as keys in maps.
	 */
	private final static class IntArray {
		private int[] mArray;

		private IntArray() {
			// do nothing
		}

		private IntArray(int[] a) {
			mArray = a;
		}

		private void set(int[] a) {
			mArray = a;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(mArray);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;

			IntArray other = (IntArray) obj;
			if (!Arrays.equals(mArray, other.mArray))
				return false;
			return true;
		}
	}
}
