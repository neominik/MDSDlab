/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package edu.mdsd.mil.resource.mil.analysis;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class MilSTRING_TOKENTokenResolver implements edu.mdsd.mil.resource.mil.IMilTokenResolver {

	private edu.mdsd.mil.resource.mil.analysis.MilDefaultTokenResolver defaultTokenResolver = new edu.mdsd.mil.resource.mil.analysis.MilDefaultTokenResolver(
			true);

	public String deResolve(Object value, EStructuralFeature feature, EObject container) {
		return escape(value.toString());
	}

	private String escape(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '\\':
				sb.append("\\\\");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\"':
				sb.append("\\\"");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			default:
				sb.append(ch);
				break;
			}
		}
		return '"' + sb.toString() + '"';
	}

	public void resolve(String lexem, EStructuralFeature feature,
			edu.mdsd.mil.resource.mil.IMilTokenResolveResult result) {
		result.setResolvedToken(unescape(lexem));
	}

	private String unescape(String s) {
		assert s.charAt(0) == '"';
		assert s.charAt(s.length() - 1) == '"';
		s = s.substring(1, s.length() - 1);

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch == '\\') { // escape
				ch = s.charAt(++i);
				switch (ch) {
				case 't':
					ch = '\t';
					break;
				case 'r':
					ch = '\r';
					break;
				case 'n':
					ch = '\n';
					break;
				case '\\':
					break;
				case '"':
					break;
				case 'b':
					ch = '\b';
					break;
				case 'f':
					ch = '\f';
					break;
				default:
					throw new RuntimeException("Unsupported escape character: \\" + ch);
				}
			}
			sb.append(ch);
		}
		return sb.toString();
	}

	public void setOptions(Map<?, ?> options) {
		defaultTokenResolver.setOptions(options);
	}

}
