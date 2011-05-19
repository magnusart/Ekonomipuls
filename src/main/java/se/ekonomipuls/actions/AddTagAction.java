/**
 * Copyright 2011 Magnus Andersson, Michael Svensson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.ekonomipuls.actions;

import se.ekonomipuls.model.EntityType;
import se.ekonomipuls.model.Tag;

/**
 * @author Magnus Andersson
 * @since 19 maj 2011
 */
public class AddTagAction extends Tag {

	/**
	 * @param id
	 * @param name
	 * @param type
	 */
	public AddTagAction(final String name, final EntityType type) {
		super(0L, name, type);
	}

}
