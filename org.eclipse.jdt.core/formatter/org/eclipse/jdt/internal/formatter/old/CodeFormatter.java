/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.formatter.old;

import java.util.Map;

import org.eclipse.jdt.core.ICodeFormatter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

/** <h2>How to format a piece of code ?</h2>
 * <ul><li>Create an instance of <code>CodeFormatter</code>
 * <li>Use the method <code>void format(aString)</code>
 * on this instance to format <code>aString</code>.
 * It will return the formatted string.</ul>
 * @deprecated 
*/
public class CodeFormatter implements TerminalTokens, ICodeFormatter {

	private Map options;
	
	public CodeFormatter(Map options) {
		if (options == null) {
			this.options = JavaCore.getOptions();
		} else {
			this.options = options;
		}
	}
	
	public String format(String string, int indentLevel, int[] positions, String lineSeparator) {
/*		if (lineSeparator != null){
			this.options.setLineSeparator(lineSeparator);
		}
		if (positions != null) {
			this.setPositionsToMap(positions);
			this.setInitialIndentationLevel(indentLevel);
			String formattedString = this.formatSourceString(string);
			System.arraycopy(this.mappedPositions, 0, positions, 0, positions.length);
			return formattedString;
		} else {
			this.setInitialIndentationLevel(indentLevel);
			return this.formatSourceString(string);
		}*/

		// initialize the new formatter with old options
		Map newOptions = DefaultCodeFormatterConstants.getDefaultSettings();

		if (JavaCore.INSERT.equals(this.options.get(JavaCore.FORMATTER_NEWLINE_OPENING_BRACE))) {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE);
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK, DefaultCodeFormatterConstants.NEXT_LINE);
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE);
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE);
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH, DefaultCodeFormatterConstants.NEXT_LINE);
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE);
		} else {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK, DefaultCodeFormatterConstants.END_OF_LINE);
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH, DefaultCodeFormatterConstants.END_OF_LINE);
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
		}
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER, DefaultCodeFormatterConstants.END_OF_LINE);
		if (JavaCore.INSERT.equals(this.options.get(JavaCore.FORMATTER_NEWLINE_CONTROL))) {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_CONTROL_STATEMENTS, JavaCore.INSERT);
		} else {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_CONTROL_STATEMENTS, JavaCore.DO_NOT_INSERT);
		}
			
		if (JavaCore.PRESERVE_ONE.equals(this.options.get(JavaCore.FORMATTER_CLEAR_BLANK_LINES))) {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE, "1"); //$NON-NLS-1$
		} else {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE, "0"); //$NON-NLS-1$
		}
		if (JavaCore.INSERT.equals(this.options.get(JavaCore.FORMATTER_NEWLINE_ELSE_IF))) {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_COMPACT_ELSE_IF, DefaultCodeFormatterConstants.FALSE);
		} else {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_COMPACT_ELSE_IF, DefaultCodeFormatterConstants.TRUE);
		}
		if (JavaCore.INSERT.equals(this.options.get(JavaCore.FORMATTER_NEWLINE_EMPTY_BLOCK))) {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_BLOCK, JavaCore.INSERT);
		} else {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_BLOCK, JavaCore.DO_NOT_INSERT);
		}
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT, this.options.get(JavaCore.FORMATTER_LINE_SPLIT));
		if (JavaCore.COMPACT.equals(this.options.get(JavaCore.FORMATTER_COMPACT_ASSIGNMENT))) {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR, JavaCore.DO_NOT_INSERT);
		} else {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR, JavaCore.INSERT);
		}
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, this.options.get(JavaCore.FORMATTER_TAB_CHAR));
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, this.options.get(JavaCore.FORMATTER_TAB_SIZE));
		if (JavaCore.INSERT.equals(this.options.get(JavaCore.FORMATTER_SPACE_CASTEXPRESSION))) {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_PAREN_IN_CAST, JavaCore.INSERT);
		} else {
			newOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_PAREN_IN_CAST, JavaCore.DO_NOT_INSERT);
		}

		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_CONTINUATION_INDENTATION, "1");//$NON-NLS-1$
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_PARAMETERS_IN_METHOD_DECLARATION, DefaultCodeFormatterConstants.createAlignmentValue(false, DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE, DefaultCodeFormatterConstants.INDENT_BY_ONE));//$NON-NLS-1$
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_PARAMETERS_IN_CONSTRUCTOR_DECLARATION, DefaultCodeFormatterConstants.createAlignmentValue(false, DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE, DefaultCodeFormatterConstants.INDENT_BY_ONE));//$NON-NLS-1$
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_ALLOCATION_EXPRESSION, DefaultCodeFormatterConstants.createAlignmentValue(false, DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE, DefaultCodeFormatterConstants.INDENT_BY_ONE));//$NON-NLS-1$
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_EXPLICIT_CONSTRUCTOR_CALL, DefaultCodeFormatterConstants.createAlignmentValue(false, DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE, DefaultCodeFormatterConstants.INDENT_BY_ONE));//$NON-NLS-1$
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_METHOD_INVOCATION, DefaultCodeFormatterConstants.createAlignmentValue(false, DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE, DefaultCodeFormatterConstants.INDENT_BY_ONE));//$NON-NLS-1$
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_QUALIFIED_ALLOCATION_EXPRESSION, DefaultCodeFormatterConstants.createAlignmentValue(false, DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE, DefaultCodeFormatterConstants.INDENT_BY_ONE));//$NON-NLS-1$
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_THROWS_CLAUSE_IN_METHOD_DECLARATION, DefaultCodeFormatterConstants.createAlignmentValue(false, DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE, DefaultCodeFormatterConstants.INDENT_BY_ONE));//$NON-NLS-1$
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_MULTIPLE_FIELDS, DefaultCodeFormatterConstants.createAlignmentValue(false, DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE, DefaultCodeFormatterConstants.INDENT_BY_ONE));//$NON-NLS-1$
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_BINARY_EXPRESSION, DefaultCodeFormatterConstants.createAlignmentValue(false, DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE, DefaultCodeFormatterConstants.INDENT_BY_ONE));//$NON-NLS-1$
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ARRAY_INITIALIZER, JavaCore.INSERT);
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACE_IN_ARRAY_INITIALIZER, JavaCore.INSERT);
		newOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACE_IN_ARRAY_INITIALIZER, JavaCore.INSERT);
		
		DefaultCodeFormatter defaultCodeFormatter = new DefaultCodeFormatter(newOptions);
		TextEdit textEdit = defaultCodeFormatter.format(org.eclipse.jdt.core.formatter.CodeFormatter.K_UNKNOWN, string, 0, string.length(), indentLevel, lineSeparator);
		if (positions != null && textEdit != null) {
			// update positions
			TextEdit[] edits = textEdit.getChildren();
			int textEditSize = edits.length;
			int editsIndex = 0;
			int delta = 0;
			int originalSourceLength = string.length() - 1;
			if (textEditSize != 0) {
				for (int i = 0, max = positions.length; i < max; i++) {
					int currentPosition = positions[i];
					if (currentPosition > originalSourceLength) {
						currentPosition = originalSourceLength;
					}
					ReplaceEdit currentEdit = (ReplaceEdit) edits[editsIndex];
					while (currentEdit.getOffset() <= currentPosition) {
						delta += currentEdit.getText().length() - currentEdit.getLength();
						editsIndex++;
						if (editsIndex < textEditSize) {
							currentEdit = (ReplaceEdit) edits[editsIndex];
						} else {
							break;
						}
					}
					positions[i] = currentPosition + delta;
				}
			}
		}
		return org.eclipse.jdt.internal.core.util.Util.editedString(string, textEdit);
	}	
}
