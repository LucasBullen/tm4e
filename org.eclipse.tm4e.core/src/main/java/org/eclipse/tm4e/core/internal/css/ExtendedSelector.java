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
package org.eclipse.tm4e.core.internal.css;

import org.w3c.css.sac.Selector;

public interface ExtendedSelector extends Selector {

	/**
	 * Returns the specificity of this selector.
	 */
	int getSpecificity();

	int nbMatch(String... names);
	
	int nbClass();
}
