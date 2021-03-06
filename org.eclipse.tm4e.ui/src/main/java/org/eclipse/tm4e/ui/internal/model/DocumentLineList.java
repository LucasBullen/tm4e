/**
 *  Copyright (c) 2015-2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.tm4e.ui.internal.model;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.tm4e.core.model.AbstractLineList;

/**
 * TextMate {@link AbstractLineList} implementation with Eclipse
 * {@link IDocument}.
 * 
 * Goal of this class is to synchronize Eclipse {@link DocumentEvent} with
 * TextMate model lines.
 *
 */
public class DocumentLineList extends AbstractLineList {

	private final IDocument document;
	private InternalListener listener;

	public DocumentLineList(IDocument document) {
		this.document = document;
		this.listener = new InternalListener();
		document.addDocumentListener(listener);
	}

	private class InternalListener implements IDocumentListener {

		@Override
		public void documentAboutToBeChanged(DocumentEvent event) {
			try {
				if (!DocumentHelper.isInsert(event)) {
					// Remove or Replace (Remove + Insert)
					removeLine(event);
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		private void removeLine(DocumentEvent event) throws BadLocationException {
			int startLine = DocumentHelper.getStartLine(event);
			int endLine = DocumentHelper.getEndLine(event, true);
			for (int i = endLine; i > startLine; i--) {
				DocumentLineList.this.removeLine(i);
			}
		}

		@Override
		public void documentChanged(DocumentEvent event) {
			IDocument document = event.getDocument();
			try {
				int startLine = DocumentHelper.getStartLine(event);
				if (!DocumentHelper.isRemove(event)) {
					if (DocumentLineList.this.getSize() != DocumentHelper.getNumberOfLines(document)) {
						int endLine = DocumentHelper.getEndLine(event, false);
						// Insert new lines
						for (int i = startLine; i < endLine; i++) {
							DocumentLineList.this.addLine(i + 1);
						}
					} else {
						// Update line
						DocumentLineList.this.updateLine(startLine);
					}
				} else {
					// Update line
					DocumentLineList.this.updateLine(startLine);
				}
				invalidateLine(startLine);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getNumberOfLines() {
		return DocumentHelper.getNumberOfLines(document);
	}

	@Override
	public String getLineText(int line) throws Exception {
		return DocumentHelper.getLineText(document, line, false);
	}

	@Override
	public int getLineLength(int line) throws Exception {
		return DocumentHelper.getLineLength(document, line);
	}

	@Override
	public void dispose() {
		document.removeDocumentListener(listener);
	}
}
