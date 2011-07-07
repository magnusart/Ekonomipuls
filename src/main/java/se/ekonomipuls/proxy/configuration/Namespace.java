/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package se.ekonomipuls.proxy.configuration;

import java.util.Map;

import com.google.api.client.xml.XmlNamespaceDictionary;

/**
 * @author Yaniv Inbar
 * @author Magnus Andersson
 */
public class Namespace {

	public static final XmlNamespaceDictionary DICTIONARY = new XmlNamespaceDictionary();
	static {
		final Map<String, String> map = DICTIONARY.getAliasToUriMap();
		map.put("", "http://www.w3.org/2005/Atom");
		map.put("app", "http://www.w3.org/2007/app");
		map.put("atom", "http://www.w3.org/2005/Atom");
		map.put("batch", "http://schemas.google.com/gdata/batch");
		map.put("docs", "http://schemas.google.com/docs/2007");
		map.put("gs", "http://schemas.google.com/spreadsheets/2006");
		map.put("gAcl", "http://schemas.google.com/acl/2007");
		map.put("gd", "http://schemas.google.com/g/2005");
		map.put("openSearch", "http://a9.com/-/spec/opensearch/1.1/");
		map.put("xml", "http://www.w3.org/XML/1998/namespace");
	}
}
