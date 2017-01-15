package in.co.krishnaconsultancy.cookmymeal.utility;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView.Tokenizer;

public class CustomTokenizer implements Tokenizer{

	private char token;
	
	public CustomTokenizer(char token) {
		this.token = token;
	}
	
	@Override
	public int findTokenEnd(CharSequence text, int cursor) {
		int i = cursor;
		int len = text.length();
		while (i < len) if (text.charAt(i) == token) return i; else i++;
		return len;
	}

	@Override
	public int findTokenStart(CharSequence text, int cursor) {
		int i = cursor;
		while (i > 0 && text.charAt(i - 1) != token) i--;
		while (i < cursor && text.charAt(i) == token) i++;
		return i;
	}

	@Override
	public CharSequence terminateToken(CharSequence text) {
		int i = text.length();
		while (i > 0 && text.charAt(i - 1) == token) i--;
		if (i > 0 && text.charAt(i - 1) == token) {
		    return text;
		} else {
		    if (text instanceof Spanned) {
		        SpannableString sp = new SpannableString(text + String.valueOf(token));
		        TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
		        return sp;
		    } else {
		        return text + String.valueOf(token);
		    }
		}
	}

}
