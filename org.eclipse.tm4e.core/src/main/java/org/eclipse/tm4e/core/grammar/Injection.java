/**
 *  Copyright (c) 2015-2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial code from https://github.com/Microsoft/vscode-textmate/
 * Initial copyright Copyright (C) Microsoft Corporation. All rights reserved.
 * Initial license: MIT
 *
 * Contributors:
 *  - Microsoft Corporation: Initial code, written in TypeScript, licensed under MIT license
 *  - Angelo Zerr <angelo.zerr@gmail.com> - translation and adaptation to Java
 */
package org.eclipse.tm4e.core.grammar;

import java.util.List;

import org.eclipse.tm4e.core.internal.matcher.IMatcher;
import org.eclipse.tm4e.core.internal.types.IRawGrammar;

public class Injection {

	private final IMatcher<List<String>> matcher;
	public final int priority; // -1 | 0 | 1; // 0 is the default. -1 for 'L' and 1 for 'R'
	public final int ruleId;
	public final IRawGrammar grammar;

	public Injection(IMatcher<List<String>> matcher, int ruleId, IRawGrammar grammar, int priority) {
		this.matcher = matcher;
		this.ruleId = ruleId;
		this.grammar = grammar;
		this.priority = priority;
	}

	public boolean match(List<String> states) {
		return matcher.match(states);
	}
}
