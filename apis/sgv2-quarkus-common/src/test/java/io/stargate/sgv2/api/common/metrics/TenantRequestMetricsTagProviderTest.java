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
package io.stargate.sgv2.api.common.metrics;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.stargate.sgv2.api.common.testprofiles.FixedTenantTestProfile;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(TenantRequestMetricsTagProviderTest.Profile.class)
class TenantRequestMetricsTagProviderTest {

  public static class Profile extends FixedTenantTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
      Map<String, String> configOverrides = super.getConfigOverrides();

      return ImmutableMap.<String, String>builder()
          .putAll(configOverrides)
          .put("quarkus.micrometer.export.prometheus.path", "/metrics")
          .build();
    }
  }

  // simple testing endpoint, helps in testing metrics
  @Path("testingTagProvider")
  public static class TestingResource {

    @GET
    @Produces("text/plain")
    public String greet() {
      return "hello";
    }
  }

  @Test
  public void record() {
    // call endpoint
    given().when().get("/testingTagProvider").then().statusCode(200);

    // collect metrics
    String result = given().when().get("/metrics").then().statusCode(200).extract().asString();

    // find target metrics
    List<String> meteredLines =
        Arrays.stream(result.split(System.getProperty("line.separator")))
            .filter(line -> line.startsWith("http_server_requests"))
            .collect(Collectors.toList());

    assertThat(meteredLines)
        .anySatisfy(
            metric ->
                assertThat(metric).contains("tenant=\"" + FixedTenantTestProfile.TENANT_ID + "\""));
  }
}
