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
package org.eclipse.tm4e.languageconfiguration.internal;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.tm4e.languageconfiguration.ILanguageConfiguration;
import org.eclipse.tm4e.languageconfiguration.internal.supports.AutoClosingPairConditional;
import org.eclipse.tm4e.languageconfiguration.internal.supports.CharacterPair;
import org.eclipse.tm4e.languageconfiguration.internal.supports.Comments;
import org.eclipse.tm4e.languageconfiguration.internal.supports.Folding;
import org.eclipse.tm4e.languageconfiguration.internal.supports.OnEnterRule;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * VSCode language-configuration.json
 *
 * @see https://code.visualstudio.com/docs/extensionAPI/extension-points#_contributeslanguages
 */
public class LanguageConfiguration implements ILanguageConfiguration {

	/**
	 * Returns an instance of {@link LanguageConfiguration} loaded from the VSCode
	 * language-configuration.json file reader.
	 *
	 * @param reader
	 * @return an instance of {@link LanguageConfiguration} loaded from the VSCode
	 *         language-configuration.json file reader.
	 */
	public static LanguageConfiguration load(Reader reader) {
		return new GsonBuilder()
				.registerTypeAdapter(Comments.class, (JsonDeserializer<Comments>) (json, typeOfT, context) -> {
					// ex: {"lineComment": "//","blockComment": [ "/*", "*/" ]}
					JsonObject object = json.getAsJsonObject();
					JsonArray blockCommentArray = object.get("blockComment").getAsJsonArray(); //$NON-NLS-1$
					CharacterPair blockCommentCharacterPair = new CharacterPair(blockCommentArray.get(0).getAsString(),
							blockCommentArray.get(1).getAsString());
					return new Comments(object.get("lineComment").getAsString(), blockCommentCharacterPair); //$NON-NLS-1$
				}).registerTypeAdapter(CharacterPair.class,
						(JsonDeserializer<CharacterPair>) (json, typeOfT, context) -> {
							if (json.isJsonArray()) {
								// ex: ["{","}"]
								JsonArray characterPairs = json.getAsJsonArray();
								return new CharacterPair(characterPairs.get(0).getAsString(),
										characterPairs.get(1).getAsString());
							} else {
								// ex: {"open":"'","close":"'"}
								JsonObject object = json.getAsJsonObject();
								return new CharacterPair(object.get("open").getAsString(), //$NON-NLS-1$
										object.get("close").getAsString()); //$NON-NLS-1$
							}
						})
				.registerTypeAdapter(CharacterPair.class,
						(JsonDeserializer<CharacterPair>) (json, typeOfT, context) -> {
							if (json.isJsonArray()) {
								// ex: ["{","}"]
								JsonArray autoClosingPairs = json.getAsJsonArray();
								return new CharacterPair(autoClosingPairs.get(0).getAsString(),
										autoClosingPairs.get(1).getAsString());
							} else {
								// ex: {"open":"'","close":"'"}
								JsonObject object = json.getAsJsonObject();
								return new CharacterPair(object.get("open").getAsString(), //$NON-NLS-1$
										object.get("close").getAsString()); //$NON-NLS-1$
							}
						})
				.registerTypeAdapter(AutoClosingPairConditional.class,
						(JsonDeserializer<AutoClosingPairConditional>) (json, typeOfT, context) -> {
							List<String> notInList = new ArrayList<>();
							if (json.isJsonArray()) {
								// ex: ["{","}"]
								JsonArray autoClosingPairs = json.getAsJsonArray();
								return new AutoClosingPairConditional(autoClosingPairs.get(0).getAsString(),
										autoClosingPairs.get(1).getAsString(), notInList);
							} else {
								// ex: {"open":"'","close":"'", "notIn": ["string", "comment"]}
								JsonObject object = json.getAsJsonObject();
								JsonElement notInElement = object.get("notIn"); //$NON-NLS-1$
								if (notInElement != null && notInElement.isJsonArray()) {
									JsonArray notInArray = notInElement.getAsJsonArray();
									notInArray.forEach(element -> notInList.add(element.getAsString()));
								}
								return new AutoClosingPairConditional(object.get("open").getAsString(), //$NON-NLS-1$
										object.get("close").getAsString(), notInList); //$NON-NLS-1$
							}
						})
				.registerTypeAdapter(Folding.class, (JsonDeserializer<Folding>) (json, typeOfT, context) -> {
					// ex: {"offSide": true, "markers": {"start": "^\\s*/", "end": "^\\s*"}}
					JsonObject object = json.getAsJsonObject();
					JsonObject markersObject = object.get("markers").getAsJsonObject(); //$NON-NLS-1$
					return new Folding(object.get("offSide").getAsBoolean(), markersObject.get("start").getAsString(), //$NON-NLS-1$ //$NON-NLS-2$
							markersObject.get("end").getAsString()); //$NON-NLS-1$
				}).create().fromJson(reader, LanguageConfiguration.class);
	}

	/**
	 * Defines the comment symbols
	 */
	private Comments comments;

	/**
	 * The language's brackets. This configuration implicitly affects pressing Enter
	 * around these brackets.
	 */
	private List<CharacterPair> brackets;

	/**
	 * The language's rules to be evaluated when pressing Enter.
	 */
	private List<OnEnterRule> onEnterRules;

	/**
	 * The language's auto closing pairs. The 'close' character is automatically
	 * inserted with the 'open' character is typed. If not set, the configured
	 * brackets will be used.
	 */
	private List<AutoClosingPairConditional> autoClosingPairs;

	/**
	 * The language's surrounding pairs. When the 'open' character is typed on a
	 * selection, the selected string is surrounded by the open and close
	 * characters. If not set, the autoclosing pairs settings will be used.
	 */
	private List<CharacterPair> surroundingPairs;

	/**
	 * Defines when and how code should be folded in the editor
	 */
	private Folding folding;

	/**
	 * Regex which defines what is considered to be a word in the programming
	 * language.
	 */
	private String wordPattern;

	@Override
	public Comments getComments() {
		return comments;
	}

	@Override
	public List<CharacterPair> getBrackets() {
		return brackets;
	}

	@Override
	public List<AutoClosingPairConditional> getAutoClosingPairs() {
		return autoClosingPairs;
	}

	@Override
	public List<OnEnterRule> getOnEnterRules() {
		return onEnterRules;
	}

	@Override
	public List<CharacterPair> getSurroundingPairs() {
		return surroundingPairs;
	}

	@Override
	public Folding getFolding() {
		return folding;
	}

	@Override
	public String getWordPattern() {
		return wordPattern;
	}
}
