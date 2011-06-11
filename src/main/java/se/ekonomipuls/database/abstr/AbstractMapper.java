/**
 * Copyright 2011 Magnus Andersson
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
package se.ekonomipuls.database.abstr;

import android.database.Cursor;

/**
 * @author Magnus Andersson
 * @since 11 jun 2011
 */
public class AbstractMapper {

	public int[] getIndices(final Cursor cur, final String[] columns) {
		final int[] indices = new int[columns.length];

		for (int i = 0; i < indices.length; i++) {
			indices[i] = cur.getColumnIndexOrThrow(columns[i]);
		}

		return indices;
	}

}