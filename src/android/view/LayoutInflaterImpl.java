package android.view;

import android.content.Context;
import android.util.AttributeSet;

public class LayoutInflaterImpl extends LayoutInflater {
    private static final String[] sClassPrefixList = {
        "android.widget.",
        "android.webkit."
    };

	public LayoutInflaterImpl(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
    /** Override onCreateView to instantiate names that correspond to the
    widgets known to the Widget factory. If we don't find a match,
    call through to our super class.
*/
@Override protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
    for (String prefix : sClassPrefixList) {
        try {
            View view = createView(name, prefix, attrs);
            if (view != null) {
                return view;
            }
        } catch (ClassNotFoundException e) {
            // In this case we want to let the base class take a crack
            // at it.
        }
    }

    return super.onCreateView(name, attrs);
}

	@Override
	public LayoutInflater cloneInContext(Context newContext) {
		// TODO Auto-generated method stub
		return null;
	}

}
