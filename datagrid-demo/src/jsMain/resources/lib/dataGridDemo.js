/*
 * Copyright 2019 Poly Forest, LLC
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

requirejs.config({
	urlArgs: function (id, url) {
		for (var i = 0; i < manifest.files.length; i++) {
			var file = manifest.files[i];
			if (file.path == url) {
				return "?version=" + file.modified;
			}
		}
		return "";
	}
});

requirejs(["datagrid-demo"]);