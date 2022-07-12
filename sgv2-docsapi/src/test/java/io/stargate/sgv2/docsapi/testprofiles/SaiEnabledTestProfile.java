/*
 * Copyright The Stargate Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.stargate.sgv2.docsapi.testprofiles;

import com.google.common.collect.ImmutableMap;
import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.Map;

/**
 * Simple test profile to enable SAI on datastore.
 *
 * <p>Annotate test class with @TestProfile(SaiEnabledTestProfile.class) to use.
 */
public class SaiEnabledTestProfile implements QuarkusTestProfile {

  @Override
  public Map<String, String> getConfigOverrides() {
    // adapt consistency, depth and column name(s)
    return ImmutableMap.<String, String>builder()
        .put("stargate.data-store.sai-enabled", "true")
        .build();
  }
}
